package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class LeaveAlreadyApprovedOrDeclinedException extends RuntimeException{
	
	public LeaveAlreadyApprovedOrDeclinedException(String message) {
		super(message);
	}

}
