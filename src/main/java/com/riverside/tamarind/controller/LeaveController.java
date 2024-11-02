package com.riverside.tamarind.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.riverside.tamarind.dto.LeaveManagementLeaveDetailsUpdate;
import com.riverside.tamarind.dto.LeaveStatusUpdate;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.service.LeaveService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;





@RestController
@RequestMapping("api/v4")
public class LeaveController {
	
	@Autowired
	private LeaveService service;
	
	
	@PostMapping("/apply")
	@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<?> sendLeaveToManger(@RequestBody LeaveManagement leave){
		
		URI location=ServletUriComponentsBuilder.fromCurrentRequest().path("/LeaveId").buildAndExpand(leave.getLeaveId()).toUri();
		
		var map=service.sendLeaveToManger(leave);
		
		return ResponseEntity.created(location).body(map);
		
		
	}

	//Get employee leave data based on userId
	@GetMapping("/get")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_EMPLOYEE')")
	public ResponseEntity<?> getLeaveDataById(@RequestParam String userId){
		
		var data=service.findAllById(userId);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}
	
	@PutMapping("/update/employee/leave/details/{leaveId}")
	@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<?> updateEmployeesLeaveDetails(@RequestBody LeaveManagementLeaveDetailsUpdate detailsUpdate,@PathVariable String leaveId){
		
		var data = service.updateServiceEmployeLeaveDeatails(detailsUpdate,leaveId);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}
	
	@Transactional
	@PutMapping("/leave/status")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> updateStatusToApproved(@RequestBody LeaveStatusUpdate leave) throws MessagingException{
		
		HttpStatus status = null;
		
		Map<?,?> data=service.updateStatusToApprovedOrDeclined(leave);
		
       Object value=data.get("statusCode");
		
		if(value.equals(406)) 
			
			 status=HttpStatus.NOT_ACCEPTABLE;
		else 
			
			status=HttpStatus.OK;
		
		return new ResponseEntity<>(data,status);
		
	}

	@GetMapping("/leave/status/pending")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> getPendlingList(){
		
		var data=service.getPendingList();
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}
	
	@GetMapping("/leave/status/approved/declined")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> getApprovedAndDeclinedList(){
		
		var data=service.getApprovedAndDeclinedList();
		
		return new ResponseEntity<>(data,HttpStatus.OK);
	}
	
	@GetMapping("/search")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> search(@RequestParam(required = false) String leaveId,
			@RequestParam(required = false) LocalDate fromDate,
			@RequestParam(required = false) LocalDate toDate){
		
		var data=service.search(leaveId,fromDate,toDate);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}
	
	@DeleteMapping("/delete/{leaveId}")
	@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<?> deleteByLeaveId(@PathVariable String leaveId){
		
		var data=service.deleteByLeaveId(leaveId);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
				
	}
	
	@GetMapping("/absentees/presentees/{date}")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<?> getAllPresenteesAndAbsenteesBasedOnTheGivenDate(@RequestParam LocalDate date){
		
		var data=service.getAllPresenteesAndAbsenteesBasedOnTheGivenDate(date);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}
	
	
	
	
	
	
	
}
