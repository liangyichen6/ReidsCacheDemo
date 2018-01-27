package com.practice.RedisCacheDemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.practice.RedisCacheDemo.domain.Book;

@Mapper
public interface BookMapper {
	
	@Select("select * from book where name = #{name}")
	Book getBookByName(@Param("name") String name);
}
