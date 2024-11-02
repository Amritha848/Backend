package com.riverside.tamarind.exceptions;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.aop.AopInvocationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.yaml.snakeyaml.scanner.ScannerException;

import com.twilio.exception.ApiException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ProblemDetail handleSecurityException(Exception ex){
        ProblemDetail errMessage=null;

        //BadCredentialsException
       if(ex instanceof BadCredentialsException){
                 errMessage= ProblemDetail
                            .forStatusAndDetail(HttpStatusCode.valueOf(401),ex.getMessage());
                  errMessage.setProperty("Access-Denied-reason","Wrong username or password");
       }

//       AccessDeniedException
        if(ex instanceof AccessDeniedException){
                  errMessage=ProblemDetail
                          .forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
                  errMessage.setProperty("Access-Denied-reason","un_authorized_role");
        }

        //SignatureException
       if(ex instanceof SignatureException){
                   errMessage=ProblemDetail
                            .forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
                   errMessage.setProperty("Access_Denied_Reason","Invalid_JWT-Signature");
       }

       //ExpiredJwtException
       if(ex instanceof ExpiredJwtException){
                   errMessage=ProblemDetail
                           .forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
                   errMessage.setProperty("Access_Denied_Reason","JWT token is already expired");
       }
       return errMessage;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
//
//        List<String> errors = ex.getConstraintViolations()
//                .stream()
//                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
//                .collect(Collectors.toList());
        String error=ex.getLocalizedMessage();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.EXPECTATION_FAILED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", error);
        map.put("status", HttpStatus.EXPECTATION_FAILED.name());

        return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        System.out.println("SQLIntegrityConstraintViolationException");

        List<String> errors = new LinkedList<>();
        errors.add(ex.getLocalizedMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.EXPECTATION_FAILED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.EXPECTATION_FAILED.name());

        return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> illegalArgumentException(IllegalArgumentException ex) {
        System.out.println("illegalArgumentException");

        List<String> errors = new LinkedList<>();
        errors.add(ex.getLocalizedMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);

    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.out.println("dataIntegrityViolationException");

       List<String> errors = new LinkedList<>();
       String rootCause = ex.getRootCause() != null ? ex.getRootCause().getLocalizedMessage() : "Unknown error";
      
      
    
       if(rootCause.contains("UKhyb5inafpyn1hbqst0oijp8nb")) {
    	   
    	   errors.add("The user is already exist with the given email");
       }
       else if(rootCause.contains("UKa8ym09c2aobr0emj5jjbfnu9p")){
    	   
            errors.add("The user is already exist with the mobileNumber");
            
       }else if(rootCause.contains("UKalkaw2ganumddwjpeftf1c7yj")) {
    	   
    	   errors.add("The user is already exist with the userName");
       }
     
       
       

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.CONFLICT.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.CONFLICT.name());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);

    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        System.out.println("methodArgumentNotValidException");

        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        
//        String error=ex.getBindingResult().getFieldError().getDefaultMessage();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.NOT_ACCEPTABLE.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.NOT_ACCEPTABLE.name());

        return new ResponseEntity<>(map, HttpStatus.NOT_ACCEPTABLE);

    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> accessDeniedException(AccessDeniedException ex) {
        System.out.println("AccessDeniedException");

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException ex) {
        System.out.println("BadCredentialsException");

        String errors = ex.getLocalizedMessage();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> UserAlreadyExistsException(UserAlreadyExistsException ex) {
        System.out.println("UserAlreadyExistsException");

//        List<String> errors = Collections.singletonList(ex.getMessage());
        
        String error=ex.getLocalizedMessage();

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.EXPECTATION_FAILED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", error);
        map.put("status", HttpStatus.EXPECTATION_FAILED.name());

        return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);

    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> UserNameNotFoundException(UserNotFoundException ex) {
        System.out.println("UserNameNotFoundException");

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode:", HttpStatus.NOT_FOUND.value());
        map.put("timestamp:", LocalDateTime.now());
        map.put("errmesessage:", errors);
        map.put("status:", HttpStatus.NOT_FOUND.name());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

    }
    @ExceptionHandler(TaskNotFoundWithTheGivenUserException.class)
    public ResponseEntity<Object> TaskNotFoundWithTheGivenUserException(TaskNotFoundWithTheGivenUserException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.EXPECTATION_FAILED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.EXPECTATION_FAILED.name());

        return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);

    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> noResourceFoundException(NoResourceFoundException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_GATEWAY.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.BAD_GATEWAY.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_GATEWAY);

    }
    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<?> unsupportedOperationException(UnsupportedOperationException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<?> TransactionSystemException(TransactionSystemException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noSuchElementException(NoSuchElementException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.NOT_FOUND.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.NOT_FOUND.name());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

    }
    
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<?> nullPointerException(NullPointerException ex) {
//
//        List<String> errors = Collections.singletonList(ex.getMessage());
//
//        Map<String, Object> map = new LinkedHashMap<>();
//        map.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
//        map.put("timestamp", LocalDateTime.now());
//        map.put("errmesessage", errors);
//        map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.name());
//
//        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
//
//    }
   
    
    @ExceptionHandler( HttpMessageNotReadableException.class)
    public ResponseEntity<?>  httpMessageNotReadableException( HttpMessageNotReadableException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage().substring(0,32));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler( InvalidOtpException.class)
    public ResponseEntity<?>  invalidOtpException( InvalidOtpException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmesessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(ManagerNotPresentInDatabaseException.class)
    public ResponseEntity<?>  managerNotPresentInDatabaseException(ManagerNotPresentInDatabaseException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<?>  invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<?>  servletException(ServletException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<?>  JpaSystemException(JpaSystemException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(TokenNotFoundInDtabaseException.class)
    public ResponseEntity<?>  tokenNotFoundInDtabaseException(TokenNotFoundInDtabaseException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(WrongReEnterPasswordException.class)
    public ResponseEntity<?>  wrongReEnterPasswordException(WrongReEnterPasswordException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(AlreadyOtpSendException.class)
    public ResponseEntity<?>  alreadyOtpSendException(AlreadyOtpSendException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(ClickSendOtpBeforeVerifyOtp.class)
    public ResponseEntity<?>  clickSendOtpBeforeVerifyOtp(ClickSendOtpBeforeVerifyOtp ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<?>  classCastException(ClassCastException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<?>  httpMessageNotWritableException(HttpMessageNotWritableException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(StringIndexOutOfBoundsException.class)
    public ResponseEntity<?>  stringIndexOutOfBoundsException(StringIndexOutOfBoundsException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<?>  incorrectResultSizeDataAccessException(IncorrectResultSizeDataAccessException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(AopInvocationException.class)
    public ResponseEntity<?>  aopInvocationException(AopInvocationException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(ScannerException.class)
    public ResponseEntity<?>  ScannerException(ScannerException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    
    @ExceptionHandler(StatusIsAlreadyApprovedOrDeclined.class)
    public ResponseEntity<?>  StatusIsAlreadyApprovedOrDeclined(StatusIsAlreadyApprovedOrDeclined ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?>  MessagingException(MessagingException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.UNAUTHORIZED.name());

        return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

    }
    
    
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<?>  InvalidEmailException(InvalidEmailException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.NOT_FOUND.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.NOT_FOUND.name());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

    }
    
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?>  NullPointerException(NullPointerException ex) {

        return new ResponseEntity<>("NO DATA FOUND", HttpStatus.OK);

    }
    
//    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
//    public ResponseEntity<?>  jpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException ex) {
//
//        List<String> errors = Collections.singletonList(ex.getMessage());
//
//        Map<String, Object> map = new LinkedHashMap<>();
//        map.put("statusCode", HttpStatus.NOT_FOUND.value());
//        map.put("timestamp", LocalDateTime.now());
//        map.put("errmessage", errors);
//        map.put("status", HttpStatus.NOT_FOUND.name());
//
//        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
//
//    }
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?>  apiException(ApiException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.NOT_FOUND.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.NOT_FOUND.name());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

    }
    
    
    
    
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?>  apiException(HandlerMethodValidationException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    
    
    
    @ExceptionHandler(UnsupportedTemporalTypeException.class)
    public ResponseEntity<?>  apiException(UnsupportedTemporalTypeException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<?>  apiException(JpaObjectRetrievalFailureException ex) {


        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", "Please, Select Manager Id");
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?>  methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.BAD_REQUEST.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.BAD_REQUEST.name());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }
    
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<?>  invalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException ex) {


        Map<String, Object> map = new LinkedHashMap<>();
        
        map.put("message", "NO PENDING LEAVES");
       

        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    
    
    @ExceptionHandler(LeaveAlreadyApprovedOrDeclinedException.class)
    public ResponseEntity<?>  leaveAlreadyApprovedOrDeclinedException(LeaveAlreadyApprovedOrDeclinedException ex) {

        List<String> errors = Collections.singletonList(ex.getMessage());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("statusCode", HttpStatus.ALREADY_REPORTED.value());
        map.put("timestamp", LocalDateTime.now());
        map.put("errmessage", errors);
        map.put("status", HttpStatus.ALREADY_REPORTED);

        return new ResponseEntity<>(map, HttpStatus.ALREADY_REPORTED);

    }
    
    
    
    
    
   
    
    
    
    
    
    
    
    
    
    
}
