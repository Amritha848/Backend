package com.riverside.tamarind.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.stereotype.Repository;

import com.riverside.tamarind.entity.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByEmail(String email);

	Optional<User> findByMobileNo(String mobileNo);

	Optional<User> findByName(String name);
	
	@Query("select u.userId as userId,u.name as employeeName,u.role as role,u.mobileNo as mobileNo,u.email as email,u.status as status,concat(date_format(u.registeredDate,'%M %d,%Y'),' at ', date_format(u.registeredDate,'%l:%i %p')) as joininigDate,u.departmentName as departmentName,u.attendance as attendance from User u left join LeaveManagement l on u.userId=l.user.userId join User m on :userId=u.manager.userId where u.role='ROLE_EMPLOYEE' and u.departmentName=m.departmentName group by u.userId")
	List<Map<String,Object>> findAllUsers(String userId);
	
    List<User> findAllByRole(String role);

	@Query("select u.userId,u.name,u.email,u.departmentName,u.mobileNo from User u")
	AuthorizationDecision findAllEmployees();

	Optional<User> findByRole(String role);
      
//	@Transactional
//	@Query("select u.userId as userId,u.name as employeeName,u.role as role,u.mobileNo as mobileNo,u.email as email,u.attendance as attendance,concat(date_format(registeredDate,'%M %d,%Y'),' at ', date_format(u.registeredDate,'%l:%i %p')) as joininigDate,u.departmentName as departmentName "
//			+ ",sum(l.sickLeavesCount) as sickLeavesCount,sum(l.casualLeavesCount) as casualLeavesCount,sum(privilegeLeavesCount) as privilegeLeaveCount  from User u left join LeaveManagement l on u.userId=l.user.userId where u.role='ROLE_EMPLOYEE' and u.userId like %:userId% or u.name like %:name% or u.departmentName like %:departmentName% or email like %:email% ")
//	List<Map<String,Object>> searchByUserId(@Param(value = "userId") String userId,@Param(value="name") String name,@Param(value="departmentName") String departmentName,@Param(value="email") String email);
	
//	@Transactional
//	@Query("select u.userId as userId, u.name as employeeName, u.role as role, u.mobileNo as mobileNo, u.email as email, u.attendance as attendance, " +
//	        "concat(date_format(u.registeredDate, '%M %d, %Y'), ' at ', date_format(u.registeredDate, '%l:%i %p')) as joiningDate, " +
//	        "u.departmentName as departmentName, " +
//	        "sum(l.sickLeavesCount) as sick, sum(l.casualLeavesCount) as casual" +
//	        "from User u left join LeaveManagement l on u.userId = l.user.userId " +
//	        "where u.role = 'ROLE_EMPLOYEE' " +
//	        "and (u.userId like %:userId% or :userId is null) " +
//	        "and (u.name like %:name% or :name is null) " +
//	        "and (u.departmentName like %:departmentName% or :departmentName is null) " +
//	        "and (u.email like %:email% or :email is null) " +
//	        "group by u.userId, u.name, u.role, u.mobileNo, u.email, u.attendance, u.registeredDate, u.departmentName")
//	List<Map<String, Object>> searchByUserId(
//	        @Param("userId") String userId,
//	        @Param("name") String name,
//	        @Param("departmentName") String departmentName,
//	        @Param("email") String email);

	
	@Transactional
	@Modifying
    @Query("update User u set u.attendance='ABSENT' where u.userId= :userId and :currentDate between :fromDate and :toDate")
	void setUpdateAttendanceForAbsent(String userId,LocalDate fromDate,LocalDate toDate,LocalDate currentDate);
	
	@Transactional
	@Modifying
    @Query("update User u join LeaveManagement l on u.userId=l.user.userId set u.attendance = 'PRESENT' where curdate() not between l.fromDate and l.toDate")
	void setUpdateAttendanceForPresent();
	
	@Query("select userId as userId,name as employeeName,role as role,mobileNo as mobileNo,email as email,status as status,concat(date_format(registeredDate,'%M %d,%Y'),' at ', date_format(registeredDate,'%l:%i %p')) as joininigDate,departmentName as departmentName from User u where u.attendance='ABSENT'")
	List<Map<String,Object>> findAbsentees();
	
	@Query("select userId as userId,name as employeeName,role as role,mobileNo as mobileNo,email as email,status as status,concat(date_format(registeredDate,'%M %d,%Y'),' at ', date_format(registeredDate,'%l:%i %p')) as joininigDate,departmentName as departmentName from User u where u.attendance='PRESENT'")
	List<Map<String,Object>> findPresentees();

	User findByPasswordResetToken(String token);
	
	@Query("select u.userId as userId, u.name as employeeName, u.role as role, u.mobileNo as mobileNo, u.email as email, u.attendance as attendance,concat(date_format(u.registeredDate, '%M %d, %Y'), ' at ', date_format(u.registeredDate, '%l:%i %p')) as joiningDate,u.departmentName as departmentName,sum(l.sickLeavesCount) as sickLeavesCount, sum(l.casualLeavesCount) as casualLeavesCount from User u left join LeaveManagement l on u.userId = l.user.userId where (u.role = 'ROLE_EMPLOYEE') and (:date between l.fromDate and l.toDate) and u.attendance = 'ABSENT'")
	List<Object[]> searchTheLeavesOnSelectedDate(@Param("date") LocalDate date);

//	@Modifying
//	@Transactional
//	@Query("update User u join LeaveManagement l on u.userId=l.user.userId set u.attendance='PRESENT' where current_date()  between l.fromDate and l.toDate")
//     @Query("update User u join LeaveManagement l set u.attendance='PRESENT' ")
//	void updateAttendance();
	
	

	
}
