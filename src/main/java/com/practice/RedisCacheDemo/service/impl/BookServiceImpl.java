package com.practice.RedisCacheDemo.service.impl;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.practice.RedisCacheDemo.domain.Book;
import com.practice.RedisCacheDemo.mapper.BookMapper;
import com.practice.RedisCacheDemo.service.BookService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookServiceImpl implements BookService, InitializingBean {

	@Autowired
	private BookMapper bookMapper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Book> hashOps;

	private final static String KEY = "BOOK";

	private final ReadWriteLock rwl = new ReentrantReadWriteLock();

	@Override
	public Book getBookByName(String name) {

		Book book = (Book) hashOps.get(KEY, name);

		if (Objects.isNull(book)) {

			log.info("Get book from database");

			book = this.bookMapper.getBookByName(name);
			hashOps.put(KEY, name, book);
			return book;
		}

		log.info("Get book from redis cache");
		return book;
	}

	@Override
	public synchronized Book getBookByNameSync(String name) {

		Book book = (Book) hashOps.get(KEY, name);

		if (Objects.isNull(book)) {

			log.info("Get book from database");

			book = this.bookMapper.getBookByName(name);
			hashOps.put(KEY, name, book);
			return book;
		}

		log.info("Get book from redis cache");
		return book;

	}

	@Override
	public Book getBookByNameDoubleCheck(String name) {

		Book book = (Book) hashOps.get(KEY, name);
		if (Objects.isNull(book)) {

			synchronized (this) {
				book = (Book) hashOps.get(KEY, name);
				if (Objects.isNull(book)) {
					log.info("Get book from database");
					book = this.bookMapper.getBookByName(name);
					hashOps.put(KEY, name, book);
					return book;
				}

				log.info("Get book from redis cache");
				return book;
			}
		}

		log.info("Get book from redis cache");
		return book;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		hashOps = redisTemplate.opsForHash();
	}

	
	@Override
	public Book getBookByNameReadWriteLock(String name) {
		rwl.readLock().lock();
		Book book = hashOps.get(KEY, name);
		
		if (Objects.isNull(book)) {
			try {
				
				rwl.readLock().unlock();

				rwl.writeLock().lock();
				book = hashOps.get(KEY, name);
				try {
					if (Objects.isNull(book)) {
						log.info("Get book from database");
						book = this.bookMapper.getBookByName(name);
						hashOps.put(KEY, name, book);
					} 
				} finally {
					rwl.writeLock().unlock();
				}
				
				rwl.readLock().lock();
				log.info("Get book from redis cache 1");
				return book;
			} finally {
					rwl.readLock().unlock();
			}
		}

		log.info("Get book from redis cache 2");
		return book;
	}

}
