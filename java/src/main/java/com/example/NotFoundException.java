package com.example;

import org.openqa.selenium.NoSuchElementException;
import java.lang.Throwable;

public class NotFoundException extends NoSuchElementException{
    
	private static final long serialVersionUID = 1L; 

	private static final String msg = "探している作品が見つかりませんでした。";
	NotFoundException(Throwable cause){
		super(msg,cause);
	}

	NotFoundException(String message,Throwable cause){
		super(message,cause);
	}
}