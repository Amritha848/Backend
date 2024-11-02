package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class StatusIsAlreadyApprovedOrDeclined extends RuntimeException{
	
	public StatusIsAlreadyApprovedOrDeclined(String msg) {
		super(msg);

}
}
