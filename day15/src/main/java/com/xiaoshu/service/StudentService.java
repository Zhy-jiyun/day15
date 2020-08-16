package com.xiaoshu.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.StudentMapper;
import com.xiaoshu.dao.TeacherMapper;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.StudentVo;
import com.xiaoshu.entity.Teacher;

import redis.clients.jedis.Jedis;

@Service
public class StudentService {

	@Autowired
	private StudentMapper sm;
	
	@Autowired
	private TeacherMapper tm;
	
	public PageInfo<StudentVo> findList(StudentVo sv,Integer pageNum,Integer pageSize)
	{
		PageHelper.startPage(pageNum, pageSize);
		List<StudentVo> list=sm.findList(sv);
		return new PageInfo<>(list);
	}
	public Student findByName(String name)
	{
		Student s=new Student();
		s.setName(name);
		return sm.selectOne(s);
	}
	public void addStudent(Student s)
	{
		sm.insert(s);
		
		Jedis j = new Jedis("127.0.0.1", 6379);
		Student student=new Student();
		student.setName(s.getName());
		Student student2 = sm.selectOne(student);
		j.set(student2.getId() + "", student.getName());
	}
	public void updateStudent(Student s)
	{
		sm.updateByPrimaryKeySelective(s);
	}
	public void delStudent(Integer id)
	{
		sm.deleteByPrimaryKey(id);
	}
	public List<StudentVo> findList(StudentVo sv)
	{
		return sm.findList(sv);
	}
	public void importStu(MultipartFile studentFile) throws InvalidFormatException, IOException
	{
		Workbook workbook = WorkbookFactory.create(studentFile.getInputStream());
		Sheet at = workbook.getSheetAt(0);
		int lastRowNum = at.getLastRowNum();
		for (int i = 0; i <lastRowNum; i++) {
			Row row = at.getRow(i+1);
			String name = row.getCell(0).toString();
			String sex = row.getCell(1).toString();
			String status = row.getCell(2).toString();
			Long age = (long)row.getCell(3).getNumericCellValue();
			String hobby = row.getCell(4).toString();
			Date birthday = row.getCell(5).getDateCellValue();
			String teachername = row.getCell(6).toString();
			
			Student s=new Student();
			s.setName(name);
			s.setSex(sex);
			s.setHobby(hobby);
			s.setStatus(status);
			s.setBirthday(birthday);
			s.setAge(age);
			
			Teacher t=new Teacher();
			t.setTname(teachername);
			Teacher teacher = tm.selectOne(t);
			
			s.setTid(teacher.getTid());
			sm.insert(s);
	}
	
}
	public List<StudentVo> countc()
	{
		return sm.countc();
	}
}