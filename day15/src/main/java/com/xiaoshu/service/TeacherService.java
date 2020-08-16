package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xiaoshu.dao.TeacherMapper;
import com.xiaoshu.entity.Teacher;

@Service
public class TeacherService {

	@Autowired
	private TeacherMapper tm;
	
	public List<Teacher> findT(Teacher teacher)
	{
		return tm.findT(teacher);
	}
	public Teacher findName(String name)
	{
		Teacher t=new Teacher();
		t.setTname(name);
		return tm.selectOne(t);
	}
	public void addTeacher(Teacher teacher)
	{
		tm.addTeacher(teacher);
	}
}
