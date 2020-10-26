package com.example;

import org.openqa.selenium.NoSuchElementException;

public class NotFoundException extends NoSuchElementException{
    
	private static final long serialVersionUID = 1L; 

	private static final String msg = "探している作品が見つかりませんでした。";
	NotFoundException(){
		super(msg);
	}
}