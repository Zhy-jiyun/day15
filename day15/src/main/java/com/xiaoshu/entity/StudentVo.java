package com.xiaoshu.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class StudentVo extends Student{

	private String teachername;
	
	
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthday1;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date birthday2;
	
	private Integer num;

	public String getTeachername() {
		return teachername;
	}

	public void setTeachername(String teachername) {
		this.teachername = teachername;
	}

	public Date getBirthday1() {
		return birthday1;
	}
			
	public void setBirthday1(Date birthday1) {
		this.birthday1 = birthday1;
	}

	public Date getBirthday2() {
		return birthday2;
	}

	public void setBirthday2(Date birthday2) {
		this.birthday2 = birthday2;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	
}
