package com.riverside.tamarind.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginDto{

    
    @NotNull(message = "Enter your username")
    private String userId;

   @NotNull(message="Enter the password")
   private String password;

}
