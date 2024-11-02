package com.riverside.tamarind.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.riverside.tamarind.dto.HolidayDTO;
import com.riverside.tamarind.entity.Holidays;
import com.riverside.tamarind.repository.HolidayRepository;

@Service
public class HolidayService {

	@Autowired
	private HolidayRepository holidayRepository;

	public Map<?, ?> postHolidaysIntodataBase(Holidays holidays) {

		Map<String, Object> map = new HashMap<>();

		holidays.setDay(holidays.getDate().getDayOfWeek().toString());

		holidayRepository.save(holidays);

		map.put("statusCode", HttpStatus.CREATED.value());

		map.put("message", "Holiday added to database");

		return map;

	}

	public List<HolidayDTO> getAllHolidaysList(Integer year) {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

		return holidayRepository.findAll().stream().filter(holiday -> holiday.getDate().getYear() == year)

				.map(holiday -> {

					var formattedDate = holiday.getDate().format(dateTimeFormatter);

					return new HolidayDTO(holiday.getSNo(), formattedDate, holiday.getDay(), holiday.getFestival(),
							holiday.getOptionalHoliday());

				}).collect(Collectors.toList());

	}

}
