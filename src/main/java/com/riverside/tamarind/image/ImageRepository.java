package com.riverside.tamarind.image;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer>{
	
	

	Optional<Image> findByName(String imageName);

}
