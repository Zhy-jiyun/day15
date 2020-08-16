package com.xiaoshu.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.config.util.ConfigUtil;
import com.xiaoshu.entity.Log;
import com.xiaoshu.entity.Operation;
import com.xiaoshu.entity.Role;
import com.xiaoshu.entity.Student;
import com.xiaoshu.entity.StudentVo;
import com.xiaoshu.entity.Teacher;
import com.xiaoshu.entity.User;
import com.xiaoshu.service.OperationService;
import com.xiaoshu.service.RoleService;
import com.xiaoshu.service.StudentService;
import com.xiaoshu.service.TeacherService;
import com.xiaoshu.service.UserService;
import com.xiaoshu.util.StringUtil;
import com.xiaoshu.util.TimeUtil;
import com.xiaoshu.util.WriterUtil;

@Controller
@RequestMapping("student")
public class StudentController extends LogController{
	static Logger logger = Logger.getLogger(StudentController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService ;
	
	@Autowired
	private OperationService operationService;
	
	@Autowired
	private StudentService ss;
	
	@Autowired
	private TeacherService ts;
	
	@RequestMapping("studentIndex")
	public String index(Teacher t,HttpServletRequest request,Integer menuid) throws Exception{
		List<Role> roleList = roleService.findRole(new Role());
		List<Operation> operationList = operationService.findOperationIdsByMenuid(menuid);
		List<Teacher> tlist = ts.findT(t);
		request.setAttribute("tlist",tlist);
		request.setAttribute("operationList", operationList);
		request.setAttribute("roleList", roleList);
		return "student";
	}
	
	
	@RequestMapping(value="studentList",method=RequestMethod.POST)
	public void userList(StudentVo sv,HttpServletRequest request,HttpServletResponse response,String offset,String limit) throws Exception{
		try {
			Integer pageSize = StringUtil.isEmpty(limit)?ConfigUtil.getPageSize():Integer.parseInt(limit);
			Integer pageNum =  (Integer.parseInt(offset)/pageSize)+1;

			PageInfo<StudentVo> page = ss.findList(sv, pageNum, pageSize);	
			
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total",page.getTotal() );
			jsonObj.put("rows", page.getList());
	        WriterUtil.write(response,jsonObj.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("学生信息展示错误",e);
			throw e;
		}
	}
	
	
	// 新增或修改
	@RequestMapping("reserveUser")
	public void reserveUser(Student s,HttpServletRequest request,User user,HttpServletResponse response){
		Integer id = s.getId();
		JSONObject result=new JSONObject();
		try {
			Student s2 = ss.findByName(s.getName());
			if (id != null) {   // userId不为空 说明是修改
				if(s2 == null || (s2!=null && s2.getId().equals(id))){
					ss.updateStudent(s);
					result.put("success", true);
				}else{
					result.put("success", true);
					result.put("errorMsg", "该学生名称被使用");
				}
				
			}else {   // 添加
				if(s2==null){  // 没有重复可以添加
					ss.addStudent(s);
					result.put("success", true);
				} else {
					result.put("success", true);
					result.put("errorMsg", "该学生名称被使用");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存学生信息错误",e);
			result.put("success", true);
			result.put("errorMsg", "对不起，操作失败");
		}
		WriterUtil.write(response, result.toString());
	}
	
	@RequestMapping("reservet")
	public void addt(Teacher t,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			Teacher teacher2 = ts.findName(t.getTname());
			if(teacher2==null)
			{
				result.put("success", true);
				ts.addTeacher(t);
			}else{
				result.put("success", true);
				result.put("errorMsg", "该老师名称被使用");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保持老师信息错误",e);
			result.put("errorMsg", "对不起，保存失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("deleteUser")
	public void delUser(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String[] ids=request.getParameter("ids").split(",");
			for (String id : ids) {
				ss.delStudent(Integer.parseInt(id));
			}
			result.put("success", true);
			result.put("delNums", ids.length);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除学生信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("countc")
	public void countc(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			List<StudentVo> list = ss.countc();
			result.put("success", true);
			result.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除学生信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("import")
	public void importStu(MultipartFile studentFile,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			ss.importStu(studentFile);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("导入学生信息错误",e);
			result.put("errorMsg", "对不起，导入失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@RequestMapping("export")
	public void exportStu(StudentVo sv,HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		try {
			String time = TimeUtil.formatTime(new Date(), "yyyyMMddHHmmss");
		    String excelName = "手动备份"+time;
			Log log = new Log();
			List<StudentVo> list = ss.findList(sv);
			String[] handers = {"学生编号","学生姓名","性别","学习状态","年龄","爱好","生日","老师姓名"};
			// 1导入硬盘
			ExportExcelToDisk(request,handers,list, excelName);
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("删除学生信息错误",e);
			result.put("errorMsg", "对不起，删除失败");
		}
		WriterUtil.write(response, result.toString());
	}
	@SuppressWarnings("resource")
	private void ExportExcelToDisk(HttpServletRequest request,
			String[] handers, List<StudentVo> list, String excleName) throws Exception {
		
		try {
			HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
			HSSFSheet sheet = wb.createSheet("操作记录备份");//第一个sheet
			HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
			rowFirst.setHeight((short) 500);
			for (int i = 0; i < handers.length; i++) {
				sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
			}
			//写标题了
			for (int i = 0; i < handers.length; i++) {
			    //获取第一行的每一个单元格
			    HSSFCell cell = rowFirst.createCell(i);
			    //往单元格里面写入值
			    cell.setCellValue(handers[i]);
			}
			for (int i = 0;i < list.size(); i++) {
			    //获取list里面存在是数据集对象
			    StudentVo log = list.get(i);
			    //创建数据行
			    HSSFRow row = sheet.createRow(i+1);
			    //设置对应单元格的值
			    row.setHeight((short)400);   // 设置每行的高度
			    //"学生编号","学生姓名","性别","学习状态","年龄","爱好","生日","老师姓名"
			    row.createCell(0).setCellValue(log.getId());
			    row.createCell(1).setCellValue(log.getName());
			    row.createCell(2).setCellValue(log.getSex());
			    row.createCell(3).setCellValue(log.getStatus());
			    row.createCell(4).setCellValue(log.getAge());
			    row.createCell(5).setCellValue(log.getHobby());
			    row.createCell(6).setCellValue(TimeUtil.formatTime(log.getBirthday(),"yyyy-MM-dd"));
			    row.createCell(7).setCellValue(log.getTeachername());
			}
			//写出文件（path为文件路径含文件名）
				OutputStream os;
				File file = new File("E:\\student.xls");
				
				if (!file.exists()){//若此目录不存在，则创建之  
					file.createNewFile();  
					logger.debug("创建文件夹路径为："+ file.getPath());  
	            } 
				os = new FileOutputStream(file);
				wb.write(os);
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
	}

	@RequestMapping("editPassword")
	public void editPassword(HttpServletRequest request,HttpServletResponse response){
		JSONObject result=new JSONObject();
		String oldpassword = request.getParameter("oldpassword");
		String newpassword = request.getParameter("newpassword");
		HttpSession session = request.getSession();
		User currentUser = (User) session.getAttribute("currentUser");
		if(currentUser.getPassword().equals(oldpassword)){
			User user = new User();
			user.setUserid(currentUser.getUserid());
			user.setPassword(newpassword);
			try {
				userService.updateUser(user);
				currentUser.setPassword(newpassword);
				session.removeAttribute("currentUser"); 
				session.setAttribute("currentUser", currentUser);
				result.put("success", true);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("修改密码错误",e);
				result.put("errorMsg", "对不起，修改密码失败");
			}
		}else{
			logger.error(currentUser.getUsername()+"修改密码时原密码输入错误！");
			result.put("errorMsg", "对不起，原密码输入错误！");
		}
		WriterUtil.write(response, result.toString());
	}
}
