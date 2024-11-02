package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class TaskNotFoundWithTheGivenUserException extends RuntimeException{
public TaskNotFoundWithTheGivenUserException(String message) {
	super(message);
}
}
