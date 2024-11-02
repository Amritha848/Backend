package com.riverside.tamarind.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;
import com.riverside.tamarind.View;
import com.riverside.tamarind.enums.Status;
import com.riverside.tamarind.image.Image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Entity
@Setter
@Getter
@JsonPropertyOrder({"id","name","password","role","email","mobileNo"})
@SQLDelete(sql = "UPDATE user SET Status='INACTIVE' WHERE user_ids = ?")
@FilterDef(name = "statusFilter",parameters = 
@ParamDef(name = "isChanged", type = String.class))
@Filter(name = "statusFilter" ,condition = "Status=:isChanged")
public class User{
	
    

	@Override
	public int hashCode() {
		return Objects.hash(departmentName, email, jwtToken, leaves, manager, mobileNo, name, password, registeredDate,
				role, status, token, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(departmentName, other.departmentName) && Objects.equals(email, other.email)
				&& Objects.equals(jwtToken, other.jwtToken) && Objects.equals(leaves, other.leaves)
				&& Objects.equals(manager, other.manager) && Objects.equals(mobileNo, other.mobileNo)
				&& Objects.equals(name, other.name) && Objects.equals(password, other.password)
				&& Objects.equals(registeredDate, other.registeredDate) && Objects.equals(role, other.role)
				&& status == other.status && Objects.equals(token, other.token) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", password=" + password + ", role=" + role + ", mobileNo="
				+ mobileNo + ", email=" + email + ", jwtToken=" + jwtToken + ", status=" + status + ", registeredDate="
				+ registeredDate + ", departmentName=" + departmentName + "]";
	}

	@Id
    @Column(length = 15, unique = true, updatable = false)
    @Pattern(regexp = "^\\d{4}[A-Z]{2}\\d{2}[A-Z]\\d$",message = "The userId should be in the format of ex: 2427RS01A0")
    @JsonView(View.Base.class)
    private String userId;

    @Column(name = "user_names", length = 17 , unique = true)
    @NotNull(message = "userName should contain first letter in uppercase")
    @Pattern(regexp = "^[A-Z][A-za-z]{6,16}$",message = "The userName should contain first character must be capital letter and character length should between 6 and 16")
    @JsonView(View.Base.class)
    private String name;

    @Column(name = "password",length = 200)
    @NotEmpty(message = " password requires at least one uppercase letter, one lowercase letter, one digit, and one special character, with a length between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",message = "The password should contain one uppercase, one lowercase"
    		+ ", one special character, one digit at least and more than 8 characters")
    private String password;

    @Column(name = "roles")
    @NotNull(message = "Enter your roles")
    @JsonView(View.Base.class)
    private String role;

    @Column(name = "mobilenos" , length = 15 ,unique = true)
    @Pattern(regexp = "[6789]\\d{9}",message = "WRONG MOBILE NUMBER")
    @NotEmpty(message = "Enter the mobile number")
    @JsonView(View.Base.class)
    private String mobileNo;

    @Column(name = "emails" , length = 50 , unique = true)
    @Email(message = "WRONG EMAIL ADDRESS")
    @NotEmpty(message = "Enter your email address")
    @JsonView(View.Base.class)
    private String email;
    
    @JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
    @Transient
    private String jwtToken;
    
    private String attendance;
    
    private String passwordResetToken;
    
    @Column(name = "status", insertable = false , updatable = true , columnDefinition = "varchar(10) default 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    @JsonView(View.Base.class)
    private Status status;
    
    @Column(name="registered_Date", nullable = false ,insertable = false,updatable = false, columnDefinition = "datetime(6) default current_timestamp(6)")
    @JsonView(View.Base.class)
    private Date registeredDate;
    
    @Column(name = "departmentName")
    @NotNull(message = "Enter the departmentName")
    private String departmentName;
    
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Token> token;
    
    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<LeaveManagement> leaves;

	@ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference
    private Image image;
    
    
    
    

    

     
    
    
    	
    	 
     
    
    
}

