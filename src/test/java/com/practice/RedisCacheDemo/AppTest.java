package com.practice.RedisCacheDemo;


import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.practice.RedisCacheDemo.service.BookService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AppTest {

	@Autowired
	private BookService bookService;

	/**
	 * 模拟多线程访问 getBookByName方法
	 * @throws InterruptedException 
	 */
	@Test
	public void testGetBookByName() throws InterruptedException {
		long times = System.currentTimeMillis();
		CountDownLatch countDownLatch = new CountDownLatch(10);
		
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					bookService.getBookByNameSync("Thinking in java");
					countDownLatch.countDown();
				}
			}).start();
		}
		
		countDownLatch.await();
		
		long spendTime = System.currentTimeMillis()-times;
		
		System.out.println(spendTime);
		
	}

}
