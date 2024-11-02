package com.riverside.tamarind.service;


import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.riverside.tamarind.dto.LeaveFormSubmissionResponse;
import com.riverside.tamarind.dto.LeaveManagementLeaveDetailsUpdate;
import com.riverside.tamarind.dto.LeaveManagementResponseDto;
import com.riverside.tamarind.dto.LeaveStatusUpdate;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.LeaveType;
import com.riverside.tamarind.exceptions.LeaveAlreadyApprovedOrDeclinedException;
import com.riverside.tamarind.exceptions.StatusIsAlreadyApprovedOrDeclined;
import com.riverside.tamarind.exceptions.UserNotFoundException;
import com.riverside.tamarind.repository.LeaveRepository;
import com.riverside.tamarind.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class LeaveService {

	@Autowired
	private LeaveRepository leaveRepository;

	@Autowired
	private UserRepository userRepository;
	

	
	@Autowired
	private EmailService emailService;
	
	

	String format = null;

//----------------------------------------------------------------------------------------------------------------------------------

	public LeaveFormSubmissionResponse sendLeaveToManger(LeaveManagement leave) {
		
		var var=leave.getFromDate().compareTo(leave.getToDate());
				
		if(var <= 0) {
			

		User user = null; 

		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		leave.setLeaveId("L"+ userId.substring(4, 6) + new DecimalFormat("00000").format(new Random().nextInt(99999)));

		user = extracted(user);

		leave.setUser(user);
		
		leave.setAppliedDate(LocalDateTime.now());

		leave.setStatus(LeaveStatus.PENDING);

		leaveRepository.save(leave);

		return LeaveFormSubmissionResponse.builder()
				.message("Leave has sent successfully to Manager " + leave.getUser().getManager().getUserId())
				.statusCode(HttpStatus.CREATED.value()).build();

	}
		throw new UserNotFoundException("The from date "+leave.getFromDate()+" must be greater than toDate "+leave.getToDate());
	}

//--------------------------------------------------------------------------------------------------------------------------------------

	private User extracted(User user) {

		var authentication = SecurityContextHolder.getContext().getAuthentication();

		Object principal = authentication.getPrincipal();

		if (principal instanceof UserDetails) {

			UserDetails userDetails = (UserDetails) principal;

			user = userRepository.findByUserId(userDetails.getUsername()).get();

		} else if (principal instanceof String) {

			String userId = (String) principal;

			user = userRepository.findByUserId(userId).get();

		}
		return user;
	}

//--------------------------------------------------------------------------------------------------------------------------

	public List<LeaveManagement> findAllById(String userId) {

		var data = userRepository.findByUserId(userId);

		if (data.isPresent()) {

			return data.get().getLeaves().stream().sorted(Comparator.comparing(LeaveManagement::getAppliedDate).reversed()).collect(Collectors.toList()); 

		}
		return List.of();

//-------------------------------------------------------------------------------------------------------------------------

	}

	public Map<?, ?> updateStatusToApprovedOrDeclined(LeaveStatusUpdate leave) throws MessagingException {

		Map<String, Object> map = new HashMap<>();

		Optional<LeaveManagement> leaves = leaveRepository.findByLeaveId(leave.getLeaveId());
		
		System.out.println(leaves);

		if (leaves.isPresent()) {
			
			

			leave.setStatus(leave.getStatus());

			String[] ignoreProps = leave.ignoreProperties();

			BeanUtils.copyProperties(leave, leaves.get(), ignoreProps);
			
			long daysBetween = ChronoUnit.DAYS.between(leaves.get().getFromDate(),leaves.get().getToDate()) + 1;
						
			if(leaves.get().getStatus().equals(LeaveStatus.APPROVED)) {
				
		        LocalDate currentDate = LocalDate.now();
		        
	             userRepository.setUpdateAttendanceForAbsent(leaves.get().getUser().getUserId(),leaves.get().getFromDate(),leaves.get().getToDate(),currentDate);
	             
//	             userRepository.setUpdateAttendanceForPresent(leaves.get().getUser().getUserId(),leaves.get().getFromDate(),leaves.get().getToDate(),currentDate);
			}

			if (leaves.get().getStatus().equals(LeaveStatus.APPROVED) 
					&& leaves.get().getType().equals(LeaveType.CASUAL)) {
				
				

				leaves.get().setMessage(leaves.get().getMessage());

				leaves.get().setCasualLeavesCount(leaves.get().getCasualLeavesCount() + daysBetween);

			}
			if (leaves.get().getStatus().equals(LeaveStatus.APPROVED) 
					&& leaves.get().getType().equals(LeaveType.SICK)) {

				leaves.get().setMessage(leaves.get().getMessage());

				leaves.get().setSickLeavesCount(leaves.get().getSickLeavesCount() + daysBetween);

			}

//			if (leaves.get().getStatus().equals(LeaveStatus.APPROVED) && leaves.get().getEarnedLeavesCount() < threshold
//					&& leaves.get().getType().equals(LeaveType.EARNED)) {
//
//				leaves.get().setMessage(leaves.get().getMessage());
//
//				leaves.get().setEarnedLeavesCount(leaves.get().getEarnedLeavesCount() + daysBetween);
//
//			}

			if (leaves.get().getStatus().equals(LeaveStatus.DECLINED)) {

				leaves.get().setMessage(leaves.get().getMessage());
				
//				 LocalDate currentDate = LocalDate.now();
			        
//	             userRepository.setUpdateAttendanceForPresent(leaves.get().getUser().getUserId(),leaves.get().getFromDate(),leaves.get().getToDate(),currentDate);

				map.put("statusCode", HttpStatus.OK.value());

			}
			
			leaveRepository.save(leaves.get());

			map.put("message", "Leave has " + leave.getStatus() + " to leave id " + leave.getLeaveId());

			map.put("statusCode", HttpStatus.OK.value());
			
//			SmsRequest  smsRequest=new SmsRequest();
//						
//			smsRequest.setMobileNo("+91 "+leaves.get().getUser().getMobileNo());
//			
//			smsRequest.setMessage("Hi "+leaves.get().getUser().getName()+", Your leave has been "+leave.getStatus()+" with leaveId "+leaves.get().getLeaveId());
//						
//			twilioService.sendNotification(smsRequest);
			
			emailService.sendEmailToEmployeeAfterLeavesStatusUpdate(leaves.get());
			
			
		}

		return map;
	}

//-------------------------------------------------------------------------------------------------------------------------------

	public List<LeaveManagementResponseDto> getPendingList() {

		return leaveRepository.findAll().stream().map(leave -> new LeaveManagementResponseDto(

				leave.getLeaveId(), leave.getFromDate(), leave.getToDate(), leave.getReason(), leave.getType(),
				leave.getStatus(), s(leave), leave.getUser().getUserId(), leave.getUser().getName(),
				leave.getMessage())).filter(s -> s.getStatus().equals(LeaveStatus.PENDING))
				.collect(Collectors.toList());

	}

	public String s(LeaveManagement leave) {
		
		LocalDateTime now = LocalDateTime.now();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm a");

		var appliedDate = leave.getAppliedDate();
		
		String appliedStringDateTime = appliedDate.format(formatter);
			
//		long daysBetween = ChronoUnit.DAYS.between(LocalDateTime.parse(appliedDate,formatter), now);
//
//		long hoursBetween = ChronoUnit.HOURS.between(LocalDateTime.parse(appliedDate,formatter), now);
//
//		if (daysBetween == 0 && hoursBetween >= 0) {
//
//			return "today at " ;
////			appliedDate.format(timeFormatter);
//
//		} else if (daysBetween == 1) {
//
//			return "yesterday at ";
//			
////					+ appliedDate.format(timeFormatter);
//
//		} else if (daysBetween == 1) {
//
//			return daysBetween + " days ago";
//
//		} else if (daysBetween == 2) {
//
//			return daysBetween +" days ago";
//
//		} else if (daysBetween == 3) {
//
//			return daysBetween +" days ago";
//		} else if (daysBetween == 4) {
//
//			return daysBetween + " days ago";
//		} else if (daysBetween == 5) {
//
//			return daysBetween + " days ago";
//		} else if (daysBetween == 6) {
//
//			return "a week ago";
//		}

		return "a long days back";

	}

	public List<LeaveManagementResponseDto> getApprovedAndDeclinedList() {

		return leaveRepository.findAll().stream().map(leave -> new LeaveManagementResponseDto(

				leave.getLeaveId(), leave.getFromDate(), leave.getToDate(), leave.getReason(), leave.getType(),
				leave.getStatus(), s(leave), leave.getUser().getUserId(), leave.getUser().getName(),
				leave.getMessage()))
				.filter(s -> s.getStatus().equals(LeaveStatus.APPROVED) || s.getStatus().equals(LeaveStatus.DECLINED))
				.collect(Collectors.toList());
	}

	public List<Map<String,Object>> search(String leaveIds,LocalDate fromDate,LocalDate toDate) {
		
		
		
		
		return  leaveRepository.searchByIdAndDateTypeUserIdName(leaveIds,fromDate,toDate);
		
				
		
	}

	public Map<?,?> deleteByLeaveId(String leaveId) {
		
		Map<String,Object> map=new HashMap<>();
		
		var data=leaveRepository.findByLeaveId(leaveId);
		
		if(data.isPresent()) {
			
			if(data.get().getStatus().equals(LeaveStatus.PENDING)) {
			
			map.put("message", "The leave with leaveId "+leaveId+" has successfully deleted");
			
			map.put("statusCode", HttpStatus.OK);
			
			leaveRepository.deleteById(leaveId);
			
			return map;
		}if(data.get().getStatus().equals(LeaveStatus.APPROVED) || data.get().getStatus().equals(LeaveStatus.DECLINED)) {
			
			throw new StatusIsAlreadyApprovedOrDeclined("The leaveId "+leaveId+" has already "+data.get().getStatus()+". You cannot delete now!");
		}
			
		     }
		
		throw new UserNotFoundException("The "+leaveId+" has not found in the database");
	}

	public List<User> getAllPresenteesAndAbsenteesBasedOnTheGivenDate(LocalDate date) {
		
		
		return null;
	}

	public Map<?,?> updateServiceEmployeLeaveDeatails(LeaveManagementLeaveDetailsUpdate detailsUpdate,String leaveId) {
		
		Optional<LeaveManagement> leave=leaveRepository.findByLeaveId(leaveId);
		
		if(leave.get().getStatus() == LeaveStatus.PENDING) {
				
		Map<String,Object> map = new HashMap<>();
		
		String[] ignoreProps = detailsUpdate.notNullColumns();
		
		map.put("statusCode", HttpStatus.OK.value());
		
		BeanUtils.copyProperties(detailsUpdate,leave.get(),ignoreProps);
		
		leaveRepository.save(leave.get());
		
	    map.put("message", "Updated Successfully");
		
		return map;
		
		}else {
			
			throw new LeaveAlreadyApprovedOrDeclinedException("Leave is already "+leave.get().getStatus()+". You can't update ");
		}
	}



	

}
