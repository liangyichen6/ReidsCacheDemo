package com.practice.RedisCacheDemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.practice.RedisCacheDemo.domain.Book;
import com.practice.RedisCacheDemo.service.BookService;

@RestController
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@GetMapping("/books/{name}")
	public ResponseEntity<Book> getBookByName(@PathVariable("name") String name){
		
		return ResponseEntity.ok(this.bookService.getBookByName(name));
	}
}
