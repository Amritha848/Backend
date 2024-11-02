package com.riverside.tamarind.corsconfig;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.ToString;



@Component
@ToString
public class CorsConfig implements WebMvcConfigurer {

    public void addCorsFilter(CorsRegistry registry){
    	
        registry.addMapping("**")
                .allowCredentials(false)
                .allowedOrigins("**")
                .allowedHeaders("**")
                .allowedMethods("**")
                .allowedOriginPatterns("**");

    }

}
