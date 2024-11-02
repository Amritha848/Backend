package com.riverside.tamarind.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.riverside.tamarind.dto.OtpGenerator;
import com.riverside.tamarind.entity.Email;
import com.riverside.tamarind.entity.LeaveManagement;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.exceptions.InvalidOtpException;
import com.riverside.tamarind.exceptions.UserNotFoundException;
import com.riverside.tamarind.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private OtpGenerator generator;

	Map<String, Object> map1 = new HashMap<>();

	@Autowired
	UserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;

	public Map<?, ?> sendOtp(Email email) {

		Map<String, Object> map = new HashMap<>();

		SimpleMailMessage message = new SimpleMailMessage();

		var user = userRepository.findByEmail(email.getToEmail());

		if (userRepository.findByEmail(email.getToEmail()) != null) {

			message.setFrom("venkatasaikumar803@gmail.com");

			message.setTo(email.getToEmail());

			message.setReplyTo("venkatasaikumar803@gmail.com");

			message.setSubject(generator.subject());

			message.setText(generator.body());

			mailSender.send(message);

			map.put("message", "Otp Send successfully to user " + user.get().getEmail());

			map.put("statusCode", HttpStatus.OK.value());

			return map;

		} else {
			throw new UserNotFoundException("USER WITH MAIL " + email.getToEmail() + " IS NOT EXIST");
		}

	}

	public Map<?, ?> verifyOtp(Email email) {

		Map<String, Object> map = new HashMap<>();

		if (generator.maps.get("otp").equals(email.getEnterOtp())) {

			map.put("message", "Otp Verified successfully. You can proceed to change your password");

			map.put("statusCode", HttpStatus.OK.value());

			return map;
		} else {
			throw new InvalidOtpException("YOU ENTERED WRONG OTP OR EXPIRED OTP");
		}

	}

	public void sendEmailToTheEmployeeAfterSuccessfullRegistration(User user) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(user.getEmail());

		helper.setSubject("Congratulations!  Successfully Registered ");

		String htmlContent = "Hi " + user.getName() + ", <br> <br>"
				+ "We are delighted to confirm that your registration has been successfully completed and you userId is "
				+ "<mark> " + user.getUserId() + "</mark>  for further usage."
				+ "<br> <br> As a new member of our team, we are excited to welcome you and look forward to the contributions you'll bring to Riverside."
				+ "<br> <br>"
				+ "If you have any questions or need further assistance, please feel free to reach out to Riverside at Department cell."
				+ "<br> <br> "
				+ "Once again, welcome to the team! We are thrilled to have you with us and look forward to your journey at Riverside."
				+ "<br> <br>" + "Best regards,<br>  Riverside,<br>" + user.getDepartmentName().toString().toLowerCase()
				+ " Team," + "<br>" + "admin@riverside.com.";

		helper.setText(htmlContent, true);

		mailSender.send(message);

	}

	public void sendEmailToEmployeeAfterLeavesStatusUpdate(LeaveManagement leave) throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(leave.getUser().getEmail());

		helper.setSubject("Updated Leave Status");

		String htmlContent = "Hi " + leave.getUser().getName() + ",  <br> <br>"

				+ " You have recently applied for the leave. Click here to view your details of the particular leave."
				+ "<br>  <br> Your leave has been " + "<mark>" + leave.getStatus() + "</mark>"
				+ ".please send an email to " + leave.getUser().getManager().getEmail() + " If you have any queries."
				+ "<br> <br> Please mention your userId and leaveId in every email" + "<br> <br> Thanks and regards,"
				+ " <br> " + leave.getUser().getManager().getName() + ". ";
		
		helper.setText(htmlContent, true);
		
		mailSender.send(message);

	}

	public String otpGenerator() {

		return new DecimalFormat("000000").format(new Random().nextInt(999999));
	}

	public String body() {

		Map<String, Object> maps = new HashMap<>();

		maps.put("otp", otpGenerator());

		return "Dear " + "To reset your riverside account password. Enter the Otp and change your password. your OTP= "
				+ maps.get("otp");
	}

}
