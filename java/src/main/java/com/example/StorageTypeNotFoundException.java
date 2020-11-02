package com.example;

import java.lang.Throwable;
import java.lang.IllegalArgumentException;
public class StorageTypeNotFoundException extends IllegalArgumentException{
    
	private static final long serialVersionUID = 2L; 

    private static final String msg = "Not Found you selected Storage Type.";
    
    StorageTypeNotFoundException(){
		super(msg);
	}
	StorageTypeNotFoundException(Throwable cause){
		super(msg,cause);
	}

	StorageTypeNotFoundException(String message,Throwable cause){
		super(message,cause);
	}
}