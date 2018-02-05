package com.practice.RedisCacheDemo.aspect;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.practice.RedisCacheDemo.annotation.RedisCacheable;
import com.practice.RedisCacheDemo.domain.Book;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author lchen283 Created by Liangyi 2/5/2018
 */
@Aspect
@Slf4j
@Component
public class CacheAspect implements InitializingBean {

	private final ReadWriteLock rwl = new ReentrantReadWriteLock();

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Book> hashOps;

	@Around(value = "@annotation(com.practice.RedisCacheDemo.annotation.RedisCacheable)")
	public Object cache(ProceedingJoinPoint point) {

		Object result = null;
		MethodSignature methodSignature = (MethodSignature) point.getSignature();
		RedisCacheable redisCacheable = methodSignature.getMethod().getAnnotation(RedisCacheable.class);
		
		
		if (!Objects.isNull(redisCacheable)) {
			String cacheName = (String) point.getArgs()[0];
			String cacheKey = redisCacheable.cacheKey();
			rwl.readLock().lock();
			Book book = hashOps.get(cacheKey, cacheName);

			if (Objects.isNull(book)) {
				try {

					rwl.readLock().unlock();

					rwl.writeLock().lock();
					book = hashOps.get(cacheKey, cacheName);
					try {
						if (Objects.isNull(book)) {
							result = point.proceed();
						}
					} catch (Throwable e) {
						log.info("Excute target method error", e);
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

		}

		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		hashOps = redisTemplate.opsForHash();
	}
}
