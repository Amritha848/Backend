package com.riverside.tamarind.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.Token;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.enums.Status;
import com.riverside.tamarind.image.Image;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPassword {

	@NotNull(message = "Enter the New Password")
	private String newPassword;

	@NotNull(message = "Enter the New Password Again to Confirm")
	private String reEnterPassword;

	private String userId;

	private String name;

	private String password;

	private String role;

	private String mobileNo;

	private String email;

	private String jwtToken;

	private String attendance;

	private String passwordResetToken;

	private Status status;

	private Date registeredDate;

	private String departmentName;

	private List<Token> token;

	private List<LeaveManagement> leaves;

	private User manager;

	private Image image;

	public String[] notNullColumns() {

		List<Object> list = new LinkedList<>();

		if (userId == null) {

			list.add("userId");
		}

		if (name == null) {

			list.add("name");
		}

		if (role == null) {

			list.add("role");
		}

		if (mobileNo == null) {

			list.add("mobileNo");
		}

		if (email == null) {

			list.add("email");
		}

		if (jwtToken == null) {

			list.add("jwtToken");
		}

		if (attendance == null) {

			list.add("attendance");
		}

		if (passwordResetToken == null) {

			list.add("passwordResetToken");
		}

		if (status == null) {

			list.add("status");
		}

		if (registeredDate == null) {

			list.add("registeredDate");
		}

		if (departmentName == null) {

			list.add("departmentName");
		}

		if (manager == null) {

			list.add("manager");
		}

		if (password == null) {

			list.add("password");
		}

		return list.toArray(new String[0]);

	}

}
