package com.riverside.tamarind.image;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.exceptions.UserNotFoundException;
import com.riverside.tamarind.repository.UserRepository;

@Service
public class ImageService {
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Autowired
	private UserRepository userRepository;

	public Map<?,?> uploadImage(MultipartFile multipartFile) throws IOException{
		
		User user=null;
		
		var authentication=SecurityContextHolder.getContext().getAuthentication();
		
		var principal=authentication.getPrincipal();
		
           if(principal instanceof UserDetails) {
        	
        	System.out.println(principal instanceof UserDetails);
        	
        	UserDetails userDetails=(UserDetails) principal;
        	
        	 user=userRepository.findByUserId(userDetails.getUsername()).get();
        	 
        }else if(principal instanceof String){
        	
        	String userId=(String) principal;
        	
        	user=userRepository.findByUserId(userId).get();
        	
        }
		
		
		Map<String,Object> map=new HashMap<>();
		
		Image file=Image.builder()
				.name(multipartFile.getOriginalFilename())
				.type(multipartFile.getContentType())
				.imageData(ImageUtils.compressImage(multipartFile.getBytes()))
				.user(user)
				.build();
	
		imageRepository.save(file);
		
		map.put("message", " Hey "+ authentication.getName()+", your profile photo has updated succesfully with "+multipartFile.getOriginalFilename());
		
		map.put("statusCode", HttpStatus.CREATED.value());
		
		return map;
		
	}
	
	public byte[] downloadImage(String imageName){
		
		Optional<Image> dbImage=imageRepository.findByName(imageName);
		
		return dbImage.map(image->{
			try {
				return ImageUtils.decompressImage(image.getImageData());
			}catch(Exception ex) {
				throw new ContextedRuntimeException("Error while downloading an image "+ex)
				.addContextValue("Image ID",  image.getId())
                .addContextValue("Image name", imageName);
			}
		}).orElse(null);
		
	
	}

	public Image getAll(String employeeId) {
		
		var data=userRepository.findByUserId(employeeId);
		
		if(data.isPresent()) {
			
			return data.get().getImage();
		}
          throw new UserNotFoundException("Employee with "+employeeId+" has not found");
	}

}
