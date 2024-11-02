package com.riverside.tamarind.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.riverside.tamarind.View;
import com.riverside.tamarind.dto.LoginResponseDto;
import com.riverside.tamarind.dto.RegisterResponse;
import com.riverside.tamarind.dto.ResetPassword;
import com.riverside.tamarind.dto.UpdateProfileDTO;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.RefreshToken;
import com.riverside.tamarind.entity.Token;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.entity.UserUpdate;
import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.TokenType;
import com.riverside.tamarind.jwtToken.JwtService1;
import com.riverside.tamarind.repository.LeaveRepository;
import com.riverside.tamarind.repository.RefreshTokenRepository;
import com.riverside.tamarind.repository.TokenRepository;
import com.riverside.tamarind.repository.UserRepository;
import com.riverside.tamarind.service.EmailService;
import com.riverside.tamarind.service.TwilioService;
import com.riverside.tamarind.service.UserDetailsServiceInfo;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.ToString;

@RestController
@RequestMapping("api/v1")
@ToString
@JsonView(View.Base.class)
public class UserController {

	Map<String, Object> map = new HashMap<>();

	@Autowired
	private UserDetailsServiceInfo service;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService1 jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TwilioService twilioService;

	@Autowired
	private UserRepository repo;

	@Autowired
	TwilioOtpController twilioOtpController;

	@Autowired
	TokenRepository tokenRepository;

	@Autowired
	LeaveRepository leaveRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	
//      SIGN-UP POST API
	
	@Transactional
	@PostMapping("/signup")

	public ResponseEntity<RegisterResponse> post(@Valid @RequestBody User user) throws MessagingException {

		var response = service.registerEmployeeTodataBase(user);

		String jwtToken = jwtService.generateAccessToken(user);

		String refreshToken = jwtService.generateRefreshToken(user);

		user.getManager().setUserId(user.getManager().getUserId().substring(0, 10));

		var savedUser = repo.save(user);

		saveUserToken(jwtToken, savedUser);

		saveOrUpdateRefreshToken(user.getUserId(), jwtToken, refreshToken);

		response.setToken(jwtToken);

		response.setRefreshToken(refreshToken);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getUserId())
				.toUri();

		emailService.sendEmailToTheEmployeeAfterSuccessfullRegistration(user);

		return ResponseEntity.created(location).body(response);
	}

	
    //   LOGIN POST API
	
	@PostMapping("/login")
	@Transactional
	public LoginResponseDto authenticate(@RequestBody User customer) {

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(customer.getUserId(), customer.getPassword()));
		} catch (Exception e) {
			throw new BadCredentialsException("INCORRECT ID OR PASSWORD");
		}
		service.loadUserByUsername(customer.getUserId());

		User optionalUser = repo.findByUserId(customer.getUserId())
				.orElseThrow(() -> new UsernameNotFoundException("EMPLOYEE NOT FOUND"));

		String jwtToken = jwtService.generateAccessToken(optionalUser);

		removeAllUserTokens(customer);

		saveUserToken(jwtToken, customer);

		String refreshToken = refreshTokenRepository.findByUserId(customer.getUserId())
				.map(RefreshToken::getRefreshToken).orElseGet(() -> {
					String newRefreshToken = jwtService.generateRefreshToken(optionalUser);
					saveOrUpdateRefreshToken(optionalUser.getUserId(), jwtToken, newRefreshToken);
					return newRefreshToken;
				});

		saveOrUpdateRefreshToken(optionalUser.getUserId(), jwtToken, refreshToken);

		return LoginResponseDto.builder().token(jwtToken).refreshToken(refreshToken).role(optionalUser.getRole())
				.statusCode(HttpStatus.OK.value()).userid(optionalUser.getUserId()).employeeName(optionalUser.getName())
				.message("Login successfull").build();

	}

	private void saveOrUpdateRefreshToken(String userId, String jwtToken, String refreshToken) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);
		if (existingToken.isPresent()) {
			RefreshToken token = existingToken.get();
			token.setAccessToken(jwtToken);
			token.setRefreshToken(refreshToken);
			refreshTokenRepository.save(token);
		} else {
			RefreshToken newToken = new RefreshToken();
			System.out.println(userId);
			newToken.setAccessToken(jwtToken);
			newToken.setRefreshToken(refreshToken);
			newToken.setUser(
					repo.findByUserId(userId).orElseThrow(() -> new UsernameNotFoundException("USER NOT FOUND")));
			refreshTokenRepository.save(newToken);
		}
	}

	public Token saveUserToken(String jwtToken, User user) {
		Token token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false)
				.revoked(false).build();

		System.out.println(token);

		tokenRepository.save(token);
		return token;
	}

	public void removeAllUserTokens(User user) {
		var validTokens = tokenRepository.findAllTokenByUser(user.getUserId());
		if (validTokens.isEmpty()) {
			return;
		}
		validTokens.forEach(t -> {
			t.setRevoked(true);
			t.setExpired(true);
		});
		tokenRepository.saveAll(validTokens);
	}
	
	//     Retrieve All the Employees under particular Manager GET API
	
	@GetMapping("/get/employees")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")

	public List<Map<String, Object>> manager() {

		return service.getUsersData();
	}
	
	
	
	// To filter ACTIVE and INACTIVE GET API

	@Transactional
	@GetMapping("/lists")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public Iterable<User> addAllFilter(
			@RequestParam(value = "isChanged", required = false, defaultValue = "ACTIVE") String status) {

		return service.findAllByFilter(status);

	}

	// To update some fields in User table PATCH API
	
	@PatchMapping("/update/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_MANAGER')")
	public ResponseEntity<?> userDetailsUpdate(@PathVariable String id, @RequestBody Map<String, Object> fields) {

		service.userDetailsUpdate(id, fields);

		return ResponseEntity.noContent().build();

	}
	
	// To Update some fields in User Table PUT API

	@PutMapping("/update")
	public ResponseEntity<Map<?, ?>> updateUserDetails(@RequestBody UserUpdate user) {

		Map<?, ?> map = service.updateUserDetails(user);

		return new ResponseEntity<>(map, HttpStatus.OK);

	}

//  To retrieve the Employees APPROVED and DECLINED leaves Details	

	@GetMapping("/get/employees/leaves/list")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")

	public ResponseEntity<?> getAllEmployees(@RequestParam LeaveStatus leaveStatus) {

		var employees = service.getAllApprovedAndDeclinedLeaves(leaveStatus);

		return new ResponseEntity<>(employees, HttpStatus.OK);
	}
	
// To Retrieve the Employees PENDING leave Details	
	
	@GetMapping("/get/employees/leaves/list/pending")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")

	public ResponseEntity<?> getAllEmployees() {

		var employees = service.getAllPendingLeaves();

		return new ResponseEntity<>(employees, HttpStatus.OK);
	}
	
//  To retrieve the All managers from user Table
	
	@GetMapping("/roles/manager")
	public ResponseEntity<?> getRoles() {

		var roles = service.getRoles();

		return new ResponseEntity<>(roles, HttpStatus.OK);
	}
	
	
	

//	@GetMapping("/sick/casual/privilege")
//	@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
//	public ResponseEntity<?> countOfAllAspects() {
//
//		var data = StatusCountsDto.builder().sickLeaveCounts(sickLeaveCounts()).casualLeaveCounts(casualLeavecounts())
//				.privilegeLeaveCounts(privilegeLeavesCount()).build();
//
//		return new ResponseEntity<>(data, HttpStatus.OK);
//	}

	

	public Long casualLeavecounts() {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		return leaveRepository.findAll().stream().collect(Collectors.groupingBy(leave -> leave.getUser().getUserId(),
				Collectors.summingLong(LeaveManagement::getCasualLeavesCount))).get(userId);

	}

	// -----------------------------------------------------------------------------------------------------------------------------

	public Long sickLeaveCounts() {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		return leaveRepository.findAll().stream().collect(Collectors.groupingBy(leave -> leave.getUser().getUserId(),
				Collectors.summingLong(LeaveManagement::getSickLeavesCount))).get(userId);

	}


//	@GetMapping("/search")
//	@PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_EMPLOYEE')")
//	public ResponseEntity<?> searchByUserId(@RequestParam(required = false) String userId,
//			@RequestParam(required = false) String name, @RequestParam(required = false) String departmentName,
//			@RequestParam(required = false) String email) {
//
//		var data = service.searchByUserId(userId, name, departmentName, email);
//
//		return new ResponseEntity<>(data, HttpStatus.OK);
//
//	}

	@GetMapping("/employees/absent")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> employeesAbsent() {

		var data = repo.findAbsentees();

		System.out.println(data);

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

//--------------------------------------------------------------------------------------------------------------------------------------------

	@GetMapping("/employees/present")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> employeesPresent() {

		var data = repo.findPresentees();

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@GetMapping("/searching")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> searchTheLeavesOnSelectedDate(@RequestParam LocalDate date) {

		List<Map<String, Object>> data = service.searchTheLeavesOnSelectedDate(date);

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

//---------------------------------------------------------------------------------------------------------------------------------------------------

	@PutMapping("/reset-password")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_EMPLOYEE')")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPassword passwords) {

		var data = service.ResetPasswordToken(passwords);

		return new ResponseEntity<>(data, HttpStatus.OK);

	}

//--------------------------------------------------------------------------------------------------------------------------------------------

	@PutMapping("/profile/update")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_EMPLOYEE')")
	public ResponseEntity<?> updateProfile(@RequestBody @Valid UpdateProfileDTO user) {

		var data = service.updateProfileDetails(user);

		return new ResponseEntity<>(data, HttpStatus.OK);

	}
	
//---------------------------------------------------------------------------------------------------------------------------------------------	

//	@GetMapping("/getAll")
//	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
//	public ResponseEntity<?> getLeaveAppliedEmployees(){
//		
//		var data = service.getAll();
//		
//		return new ResponseEntity<>(data,HttpStatus.OK);
//	}
	
//---------------------------------------------------------------------------------------------------------------------------------------------	
	
//	private SmsRequest smsRequest(User user) {
//
//		SmsRequest request = new SmsRequest();
//
//		request.setMobileNo("+91 " + user.getMobileNo());
//
//		request.setMessage("Hello " + user.getName()
//				+ ", You have successfully registered with Riverside's Leave Application. Please use this application to request other services, such as leaves.");
//
//		System.out.println(request);
//
//		return request;
//	}
	
//---------------------------------------------------------------------------------------------------------------------------------------------
	
//	@PutMapping("/update/password/email")
//	public ResponseEntity<Map<?, ?>> updateUserDetailsForEmail(@RequestBody UserUpdate user) {
//
//		Map<?, ?> map = service.updateUserDetailsForEmail(user);
//
//		return new ResponseEntity<>(map, HttpStatus.OK);
//
//	}
	
//-------------------------------------------------------------------------------------------------------------------------------------------
	
//	@PostMapping("/sendnotification")
//	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
//	public ResponseEntity<String> sendNotification(@RequestBody SmsRequest smsRequest) {
//
//		twilioService.sendNotification(smsRequest);
//
//		return new ResponseEntity<>("Notification sucessfully sent to " + smsRequest.getMobileNo(), HttpStatus.OK);
//	}

}
