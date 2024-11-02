package com.riverside.tamarind.jwtauthenticationfilter;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.riverside.tamarind.jwtToken.JwtService1;
import com.riverside.tamarind.repository.TokenRepository;
import com.riverside.tamarind.service.UserDetailsServiceInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.ToString;
@ToString
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private JwtService1 jwtService;

    @Autowired
    private UserDetailsServiceInfo userDetailsServiceInfo;
    
    @Autowired
    TokenRepository tokenRepository;

  
    public JwtAuthenticationFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    private HandlerExceptionResolver exceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
    	


        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                userId = jwtService.extractUsername(token);
            }




            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsServiceInfo.loadUserByUsername(userId);
                var isTokenValid=tokenRepository.findByToken(token)
                		.map(t->!t.isExpired() && !t.isRevoked())
                		.orElse(false);
                
                System.out.println(isTokenValid);
                
                System.out.println(token);
                
                System.out.println(tokenRepository.findByToken(token));
                
                System.out.println(userDetails);

                if (jwtService.isValid(token, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }

}



