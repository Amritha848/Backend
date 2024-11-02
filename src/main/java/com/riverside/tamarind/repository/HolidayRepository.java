package com.riverside.tamarind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riverside.tamarind.entity.Holidays;

public interface HolidayRepository extends JpaRepository<Holidays, Integer>{

}
 