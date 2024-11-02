package com.riverside.tamarind.image;




import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.riverside.tamarind.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Image {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private String type;
	
	@Lob
	@JdbcTypeCode(Types.LONGVARBINARY)
	private byte[] imageData;
	
	@OneToOne
	@JoinColumn(name = "employee_id")
	@JsonBackReference
	private User user;

}
