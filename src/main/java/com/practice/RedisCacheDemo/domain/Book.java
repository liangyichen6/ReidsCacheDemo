package com.practice.RedisCacheDemo.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Book implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private float price;

	@Override
	public String toString() {
		return "Book [name=" + name + ", price=" + price + "]";
	}
	
}
