package com.riverside.tamarind.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDTO {
	
	private Integer sNo;
	
	private String date;
	
	private String day;
	
	private String festival;
	
	private Boolean optionalHoliday;


}
