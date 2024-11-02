package com.riverside.tamarind.image;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageUpdate {
	
	private String name;
	
	@Lob
	@JdbcTypeCode(Types.LONGVARBINARY)
	private byte[] imageData;
	
	public String[] ignoreProperties() {
	List<Object> list=new ArrayList<>();
	
	if(name == null) {
		list.add(name);
	}
	if(imageData == null) {
		list.add(imageData);

	}
	return list.toArray(new String[0]);
	}

}
