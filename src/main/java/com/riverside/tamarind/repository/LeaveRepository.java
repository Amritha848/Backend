package com.riverside.tamarind.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riverside.tamarind.dto.LeaveManagementDTO;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.enums.LeaveStatus;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveManagement, String>{

	Optional<LeaveManagement> findByLeaveId(String leaveId);
	
//    @Query("select lm from LeaveManagement lm where lm.leaveId like %:leaveId%")
	@Query("select l.leaveId as leaveId,concat(date_format(l.fromDate,'%M %d,%Y')) as fromDate,concat(date_format(l.toDate,'%M %d,%Y')) as toDate,l.type as type,l.user.userId as userId,l.status as status,concat(date_format(l.appliedDate,'%M %d,%Y'),' at ', date_format(l.appliedDate,'%l:%i %p')) as appliedDate from LeaveManagement l join User u on l.user.userId=u.userId "
			+ "where l.leaveId like %:leaveId% or l.fromDate =:fromDate% or l.toDate =:toDate%")
	List<Map<String,Object>> searchByIdAndDateTypeUserIdName(@Param(value = "leaveId") String leaveId,@Param(value = "fromDate") LocalDate fromDate,@Param(value = "toDate") LocalDate toDate);

	
	@Query("select new com.riverside.tamarind.dto.LeaveManagementDTO(l.leaveId,concat(date_format(l.fromDate,'%b %d,%Y')),concat(date_format(l.toDate,'%b %d,%Y')),l.reason,l.type,"
			+ "l.status,l.sickLeavesCount,l.casualLeavesCount,"
			+ "concat(date_format(l.appliedDate,'%b %d,%Y'), ' ', TIME_FORMAT(l.appliedDate, '%H:%i:%s')),l.message,l.user.userId as employeeId,l.user.name as employeeName) from User u right join LeaveManagement l on u.userId=l.user.userId join User m on :userId=u.manager.userId where u.role='ROLE_EMPLOYEE' and u.departmentName=m.departmentName and l.status= :leaveStatus group by l.leaveId")
	List<LeaveManagementDTO> findAllItems(String userId,LeaveStatus leaveStatus);

	
	@Query("select new com.riverside.tamarind.dto.LeaveManagementDTO(l.leaveId,concat(date_format(l.fromDate,'%b %d,%Y')),concat(date_format(l.toDate,'%b %d,%Y')),l.reason,l.type,"
			+ "l.status,l.sickLeavesCount,l.casualLeavesCount,"
			+ "concat(date_format(l.appliedDate,'%b %d,%Y'), ' ', TIME_FORMAT(l.appliedDate, '%H:%i:%s')),l.message,l.user.userId as employeeId,l.user.name as employeeName) from User u right join LeaveManagement l on u.userId=l.user.userId join User m on :userId=u.manager.userId where u.role='ROLE_EMPLOYEE' and u.departmentName=m.departmentName and l.status= 'PENDING' group by l.leaveId")
	List<LeaveManagementDTO> getAllPendingLeaves(String userId);

	User findByUser(User user);
 

	
//	@Query("select sum(l.count) from LeaveManagement l join User u where l.user=u.leaves group by l.user = :userId")
	
//	@Query("SELECT SUM(l.count) FROM LeaveManagement l JOIN l.user u WHERE u.id = :userId")
//	long LeavesTaken(String userId);


}
