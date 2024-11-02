package com.riverside.tamarind.image;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v5")
public class ImageController {
	
	@Autowired
	private ImageService imageService;
	
	@PostMapping("/upload/dp")
	@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_MANAGER')")
	public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile multipartFile) throws IOException{
		
		var data=imageService.uploadImage(multipartFile);
		
		return new ResponseEntity<>(data,HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{fileName}")
	@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_MANAGER')")
	public ResponseEntity<?> downloadImage(@PathVariable String fileName){
		
		var data=imageService.downloadImage(fileName);
		
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
				.body(data);
		
	}
	
	@GetMapping("/image")
	@PreAuthorize("hasAnyAuthority('ROLE_MANAGER','ROLE_EMPLOYEE')")
	public ResponseEntity<?> getImagetData(@RequestParam String employeeId){
		
		var data=imageService.getAll(employeeId);
		
		return new ResponseEntity<>(data,HttpStatus.OK);
	}

	
	

}
