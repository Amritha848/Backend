package com.riverside.tamarind.service;

import static java.util.Map.entry;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.riverside.tamarind.dto.LeaveManagementDTO;
import com.riverside.tamarind.dto.RegisterResponse;
import com.riverside.tamarind.dto.ResetPassword;
import com.riverside.tamarind.dto.UpdateProfileDTO;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.Token;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.entity.UserUpdate;
import com.riverside.tamarind.entityimplements.UserDetailsInfo;
import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.TokenType;
import com.riverside.tamarind.exceptions.InvalidEmailException;
import com.riverside.tamarind.exceptions.UserAlreadyExistsException;
import com.riverside.tamarind.exceptions.UserNotFoundException;
import com.riverside.tamarind.exceptions.WrongReEnterPasswordException;
import com.riverside.tamarind.jwtToken.JwtService1;
import com.riverside.tamarind.repository.LeaveRepository;
import com.riverside.tamarind.repository.TokenRepository;
import com.riverside.tamarind.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.ToString;

@Service
@ToString
public class UserDetailsServiceInfo implements UserDetailsService {

	@Autowired
	private UserRepository repo;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private LeaveRepository leaveRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	TwilioService twilioService;

	@Autowired
	private JwtService1 jwtService;

	@Autowired
	private TokenRepository tokenRepository;

	private static final String PREFIX = "2427RS";
	private static final int START_DIGIT = 1;
	private static final int END_DIGIT = 9;
	private static final char START_LETTER = 'A';
	private static final char END_LETTER = 'Z';
	private static final int START_SUFFIX = 0;
	private static final int END_SUFFIX = 99;

	public List<String> generateCodes() {

		List<String> list = new ArrayList<>();

		for (int digit = START_DIGIT; digit <= END_DIGIT; digit++) {

			char startLetter = (digit == START_DIGIT) ? START_LETTER : 'A';

			char endLetter = (digit == END_DIGIT) ? END_LETTER : 'Z';

			for (char letter = startLetter; letter <= endLetter; letter++) {

				int startSuffix = (digit == START_DIGIT && letter == START_LETTER) ? START_SUFFIX : 0;

				int endSuffix = (digit == END_DIGIT && letter == END_LETTER) ? END_SUFFIX : 99;

				for (int suffix = startSuffix; suffix <= endSuffix; suffix++) {

					String code = String.format("%s%d%c%02d", PREFIX, digit, letter, suffix);

					list.add(code);
				}
			}

		}
		return list;
	}

	public String getRandomCode(List<String> codes) {

		Random random = new Random();

		return codes.get(random.nextInt(codes.size()));

	}

	public RegisterResponse registerEmployeeTodataBase(User user) throws MessagingException {

		List<String> codes = generateCodes();

		String userId = getRandomCode(codes);

		Optional<User> userWithUserId = repo.findByUserId(userId);
		
		if (userWithUserId.isEmpty()) {

			user.setUserId(userId);

			user.setPassword(passwordEncoder.encode(user.getPassword()));

			user.setAttendance("PRESENT");

			user.setRegisteredDate(new Date());
			
                       System.out.println(user.getManager().getUserId().substring(0,10));
                       
			if (user.getRole().equals("ROLE_EMPLOYEE") && user.getManager().getUserId() == null
					&& !user.getDepartmentName()
							.equals(repo.findByUserId(user.getManager().getUserId().substring(0,10)).get().getDepartmentName())) {
				

				throw new UserNotFoundException("SELECT THE MANAGER_ID");

			}

			RegisterResponse registerResponse = new RegisterResponse();
			
			registerResponse.setRole(user.getRole());
			registerResponse.setStatusCode(HttpStatus.CREATED.value());
			registerResponse.setMessage(
					"Registered Successfully, Your userId has sent to your registered email. Kindly, check the email.");

			try {

			} catch (Exception ex) {

				throw new InvalidEmailException("Invalid Email address");
			}

			return registerResponse;

		} else {

			throw new UserAlreadyExistsException("EMPLOYEE IS ALREADY EXISTS");
		}

	}

	// -------------------------------------------------------------------------------------------------------------------------------

	public Token saveUserToken(String jwtToken, User user) {
		Token token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false)
				.revoked(false).build();

		tokenRepository.save(token);
		return token;
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		Optional<User> user = repo.findByUserId(userId);

		if (user.isPresent()) {

			return user.map(UserDetailsInfo::new).orElseThrow(() -> new BadCredentialsException("BAD CREDENTIALS"));

		}

		throw new BadCredentialsException("BAD CREDENTIALS");

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public List<Map<String, Object>> getUsersData() {

		var leavesData = leaveRepository.findAll();

		System.out.println(leavesData);

		LocalDate date = LocalDate.now();

		System.out.println(date);

		repo.setUpdateAttendanceForPresent();

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

//		leavesData.stream().filter(l->!date.isBefore(l.getFromDate()) && !date.isAfter(l.getToDate())).map(LeaveManagement::getUser).forEach(user->{
//			user.setAttendance("PRESENT");
//			repo.save(user);
//			
//			                           
//		});

//      repo.updateAttendance();

//		repo.setUpdateAttendanceForPresent(userIds, fromDates, toDates, LocalDate.now());

//		repo.findAll().stream().map(s->s.getUserId()).collect(Collectors.toList()).forEach(System.out::println);
//		
//		var data=repo.findAll().stream().map(user->new StatusCountsDto(
//				
//				user.getUserId(),
//				user.getName(),
//				user.getMobileNo(),
//				user.getEmail(),
//				user.getDepartmentName(),
//				user.getRegisteredDate(),
//				sickLeaveCounts(user.getUserId()),
//				casualLeavecounts(user.getUserId()),
//				privilegeLeavesCount(user.getUserId()),
//				user.getStatus())).collect(Collectors.toList());
//		
//		System.out.println(data);
//		repo.setUpdateAttendanceForPresent()

		return repo.findAllUsers(userId);

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public List<LeaveManagementDTO> getAllApprovedAndDeclinedLeaves(LeaveStatus leaveStatus) {

		String ManagerId = SecurityContextHolder.getContext().getAuthentication().getName();

		return leaveRepository.findAllItems(ManagerId,leaveStatus);
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public void deleteById(String id) {

		Optional<User> user = repo.findById(id);

		if (user.isPresent()) {

			repo.deleteById(id);

		} else {

			throw new UserNotFoundException("INVALID USER ID");

		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public Iterable<User> findAllByFilter(String status) {

		Session session = entityManager.unwrap(Session.class);

		Filter filter = session.enableFilter("statusFilter");

		filter.setParameter("isChanged", status);

		Iterable<User> user = repo.findAll();

		session.disableFilter("statusFilter");

		return user;
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public User getDetailsById(String id) {

		Optional<User> user = repo.findByUserId(id);

		System.out.println(user);

		if (user == null)

			throw new UserNotFoundException("USER IS NOT FOUND");

		else

			return user.get();
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public User userDetailsUpdate(String id, Map<String, Object> fields) {

		Optional<User> user = repo.findByUserId(id);

		if (user.isPresent()) {

			fields.forEach((key, value) -> {

				Field field = ReflectionUtils.findField(User.class, key);

				if (field != null)

					field.setAccessible(true);

				ReflectionUtils.setField(field, user.get(), value);
			});
			return repo.save(user.get());

		}
		throw new UserNotFoundException("USER IS NOT FOUND WITH THE ID: " + id);
	}

	// --------------------------------------------------------------------------------------------------------------------------------

	public Map<?, ?> updateUserDetails(UserUpdate user) {

		String twilioMobileNo = (String) twilioService.mobileNo.get("mobileNo");

		System.out.println(twilioMobileNo);

		User userWithId = repo.findByMobileNo(twilioMobileNo).orElseThrow();

		// retrieve the phoneNumber from twilioservice

		Map<String, Object> map = new ConcurrentHashMap<>();

		String[] ignoreProps = user.notNullColumns();

		user.setUserId(userWithId.getUserId());

		String message = "Hey " + userWithId.getName() + ", " + "Password has updated.";

		if (user.getConfirmPassword().equals(user.getPassword())) {

			return updatePassword(user, map, ignoreProps, userWithId, message);

		} else {
			throw new WrongReEnterPasswordException(
					" Hey, " + userWithId.getName() + " new password should match with re-enter password");
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------

	private Map<?, ?> updatePassword(UserUpdate user, Map<String, Object> map, String[] ignoreProps, User userWithId,
			String message) {

		repo.save(userWithId);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		BeanUtils.copyProperties(user, userWithId, ignoreProps);

		map.put("message", message);

		map.put("statusCode", HttpStatus.OK.value());

		repo.save(userWithId);

		return map;
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	public Set<String> getRoles() {

		return repo.findAll().stream().filter(s -> s.getRole().equals("ROLE_MANAGER"))
				.map(s -> s.getUserId().concat("("+s.getName()+")")).collect(Collectors.toSet());

	}

	// -------------------------------------------------------------------------------------------------------------------------------

	public long totalEmployeesCount() {

		return repo.findAll().stream()
				.filter(s -> s.getRole().equals("ROLE_EMPLOYEE") && s.getAttendance().equals("PRESENT")).count();
	}

	// ------------------------------------------------------------------------------------------------------------------------------

	public List<User> totalEmployeesData() {

		return repo.findAll();
	}

	

	public Long casualLeavecounts() {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		return leaveRepository.findAll().stream().collect(Collectors.groupingBy(leave -> leave.getUser().getUserId(),
				Collectors.summingLong(LeaveManagement::getCasualLeavesCount))).get(userId);

	}

//-----------------------------------------------------------------------------------------------------------------------------

	public Long sickLeaveCounts() {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		var data = leaveRepository.findAll().stream().collect(Collectors.groupingBy(
				leave -> leave.getUser().getUserId(), Collectors.summingLong(LeaveManagement::getSickLeavesCount)));

		return data.get(userId);

	}

//------------------------------------------------------------------------------------------------------------------------------------	   	    

	public Map<?, ?> updateUserDetailsForEmail(UserUpdate user) {

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		User optional = repo.findByUserId(userId).get();

		String[] ignoreProps = user.notNullColumns();

		Map<String, Object> map = new HashMap<>();

		user.setUserId(userId);

		String message = "Hey " + optional.getName() + ", " + "Password has updated.";

		if (user.getConfirmPassword().equals(user.getPassword())) {

			return updatePassword(user, map, ignoreProps, optional, message);

		} else {
			throw new WrongReEnterPasswordException(
					" Hey, " + optional.getName() + " new password should match with re-enter password");
		}

	}

//	public List<Map<String, Object>> searchByUserId(String userId, String name, String departmentName, String email) {
//
//		List<Map<String, Object>> strings = repo.searchByUserId(userId, name, departmentName, email);
//
//		return strings;
//
//	}

	public void updateResetPasswordToken(String token, String email) throws UserNotFoundException {
		Optional<User> customer = repo.findByEmail(email);
		if (customer != null) {
			customer.get().setPasswordResetToken(token);
			repo.save(customer.get());
		} else {
			throw new UserNotFoundException("Could not find any customer with the email " + email);
		}
	}

	public User getByResetPasswordToken(String token) {
		return repo.findByPasswordResetToken(token);
	}

	public void updatePassword(User customer, String newPassword) {
		
		System.out.println(newPassword);

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(newPassword);
		customer.setPassword(encodedPassword);

		customer.setPasswordResetToken(null);
		
		System.out.println(customer);
		repo.save(customer);

	}

	public List<Map<String, Object>> searchTheLeavesOnSelectedDate(LocalDate date) {

		List<Object[]> results = repo.searchTheLeavesOnSelectedDate(date);

		return results.stream()
				.map(row -> Map.ofEntries(entry("userId", row[0]), entry("employeeName", row[1]), entry("role", row[2]),
						entry("mobileNo", row[3]), entry("email", row[4]), entry("attendance", row[5]),
						entry("joiningDate", row[6]), entry("departmentName", row[7]), entry("sickLeavesCount", row[8]),
						entry("casualLeavesCount", row[9]), entry("privilegeLeaveCount", row[10])))
				.collect(Collectors.toList());

	}

	public Map<?, ?> ResetPasswordToken(ResetPassword passwords) {

		Map<String, Object> map = new HashMap<>();

		User userInDataBase = repo.findByUserId(SecurityContextHolder.getContext().getAuthentication().getName()).get();

		String[] ignoreProps = passwords.notNullColumns();

		userInDataBase.setPassword(passwordEncoder.encode(passwords.getNewPassword()));

		BeanUtils.copyProperties(passwords, userInDataBase, ignoreProps);

		map.put("statusCode", HttpStatus.OK.value());

		map.put("message", "Password has updated successfully");

		repo.save(userInDataBase);

		return map;

	}

	public Map<?,?> updateProfileDetails(UpdateProfileDTO user) {
		
		Map<String,Object> map = new HashMap<>();
		
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		
		Optional<User> userInRepo = repo.findByUserId(userId);
		
		if(userInRepo.isPresent()) {
			
			map.put("statusCode", HttpStatus.OK.value());
			
			var ignoreProps = user.notNullColumns();
			
			BeanUtils.copyProperties(user, userInRepo.get(), ignoreProps);
			
			System.out.println(user);
			
			System.out.println(userInRepo);
			
			System.out.println(Arrays.toString(ignoreProps));
			
			repo.save(userInRepo.get());
						
			map.put("message","Details Updated Successfully");
		}
		
		else {
			
			throw new UserNotFoundException("USER NOT FOUND with userId  "+userId);
		}
		
		return map;
		
		
	}

	public List<LeaveManagementDTO> getAllPendingLeaves() {

		String ManagerId = SecurityContextHolder.getContext().getAuthentication().getName();

		return leaveRepository.getAllPendingLeaves(ManagerId);
	}

}
