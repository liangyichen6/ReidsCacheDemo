package com.practice.RedisCacheDemo.service;

import com.practice.RedisCacheDemo.domain.Book;

public interface BookService {
	
	Book getBookByName(String name);
	
	Book getBookByNameSync(String name);
	
	Book getBookByNameReadWriteLock(String name);
	
	Book getBookByNameDoubleCheck(String name);
}
