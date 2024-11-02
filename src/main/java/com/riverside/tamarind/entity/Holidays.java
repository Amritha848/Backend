package com.riverside.tamarind.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Holidays {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sNo;
	
	@NotNull(message = "Select Date")
	private LocalDate date;
	
	private String day;
	
	@NotNull(message = "Type Festival Name")
	private String festival;
	
	@NotNull(message = "select OptionalHolidAY WITH YES or NO")
	private Boolean optionalHoliday;
	
	

}
