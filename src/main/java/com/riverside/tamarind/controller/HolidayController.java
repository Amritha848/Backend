package com.riverside.tamarind.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riverside.tamarind.entity.Holidays;
import com.riverside.tamarind.service.HolidayService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v9/leaves")
public class HolidayController {

	@Autowired
	private HolidayService holidayService;

	@PostMapping("/post/holidays")
	@PreAuthorize("hasAuthority('ROLE_MANAGER')")
	public ResponseEntity<Map<?, ?>> postHolidays(@RequestBody @Valid Holidays holidays) {

		var data = holidayService.postHolidaysIntodataBase(holidays);

		return new ResponseEntity<>(data, HttpStatus.CREATED);

	}
	
	@GetMapping("get/holidays/list")
	@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_MANAGER')")
	public ResponseEntity<?> getAllHolidaysList(@RequestParam(required = true) Integer year){
		
		var data = holidayService.getAllHolidaysList(year);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
		
	}

}
