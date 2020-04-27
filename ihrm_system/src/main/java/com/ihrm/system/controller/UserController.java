package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.PageResult;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.system.client.DeptFeign;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//1.解决跨域
@CrossOrigin
@RestController
@RequestMapping("/sys")
@Api(tags = "用户管理Api", description= "用户的 增删改查")
public class UserController extends BaseController {
  @Autowired
  private UserService userService;
  @Autowired
  private PermissionService permissionService;
  @Autowired
  private DeptFeign deptFeign;

  @ApiOperation("测试feign组件")
  @GetMapping("/test/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "部门id",required = true,dataType = "string",paramType = "path")
  })
  public Result add(@PathVariable String id){

    Result dept = deptFeign.findById(id);

    return new Result(ResultCode.SUCCESS,dept);
  }

  @ApiOperation("保存用户")
  @PostMapping("/user")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "user",value = "用户数据",required = true,dataType = "User",paramType = "body")
  })
  public Result add(@RequestBody User user) throws Exception {
    user.setCompanyId(companyId);
    user.setCompanyName(companyName);
    userService.save(user);
    return Result.SUCCESS();
  }

  @ApiOperation("更新用户")
  @PutMapping("/user/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String",paramType = "path"),
          @ApiImplicitParam(name = "user",value = "用户数据",required = true,dataType = "User",paramType = "body")
  })
  public Result update(@PathVariable(name = "id") String id, @RequestBody User user)
          throws Exception {
    userService.update(user);
    return Result.SUCCESS();
  }

  //Bearer eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1ODc1NzYyNzAsImlhdCI6MTU4NzU3NTkxMCwianRpIjoiMTI1MjYzNDc3NzE2MTQzNzE4NSIsInN1YiI6IuiAgeWImCIsImNvbXBhbnlJZCI6IjEiLCJhcGlzIjoiIiwiY29tcGFueU5hbWUiOiLliJjlp6Xlp6Xlhazlj7gifQ.T8ViIq23JMjjwVCmUvX8xKrRcXleovMj2HUGZG3PItw
  //1252634777161437186
  @ApiOperation("删除用户")
  @DeleteMapping(value = "/user/{id}",name = "API-USER-DELETE")
//  @RequestMapping(value = "/user/{id}",method = RequestMethod.DELETE,name = "API-USER-DELETE")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String",paramType = "path"),
          @ApiImplicitParam(name = "Authorization",value = "user token",required = true,dataType = "string",paramType = "header"),
  })
  public Result delete(@PathVariable(name = "id") String id) throws Exception {
    userService.delete(id);
    return Result.SUCCESS();
  }

  @ApiOperation("根据ID查询用户")
  @GetMapping("/user/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String",paramType = "path")
  })
  public Result findById(@PathVariable(name = "id") String id) throws Exception {
    User user = userService.findById(id);
    return new Result(ResultCode.SUCCESS, user);
  }

  @ApiOperation("分页查询用户")
  @GetMapping("/user")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "page",value = "第几页",required = true,dataType = "int",paramType = "path"),
          @ApiImplicitParam(name = "size",value = "每页条数",required = true,dataType = "int",paramType = "path"),
          @ApiImplicitParam(name = "map",value = "查询条件",dataType = "map",paramType = "path")
  })
  public Result findByPage(int page, int size, @RequestParam Map<String, Object> map) throws Exception {
    map.put("companyId", companyId);
    Page<User> searchPage = userService.findSearch(map, page, size);
    PageResult<User> pr = new PageResult(searchPage.getTotalElements(), searchPage.getContent());
    return new Result(ResultCode.SUCCESS, pr);
  }

  @ApiOperation("登陆")
  @PostMapping("/login")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "map",value = "{mobile:13800000001,password:123456}",required = true,dataType = "map",paramType = "body"),
  })
  public Result login(@RequestBody Map<String,Object> map) throws Exception {

    String mobile = (String)map.get("mobile");
    String password = (String)map.get("password");

    password = new Md5Hash(password, mobile, 3).toString();

    //更换成shiro方式验证登陆 并获取生成sessionId返回前端
    try {
      //构造登陆令牌
      UsernamePasswordToken token = new UsernamePasswordToken(mobile,password);
      //获取subject
      Subject subject = SecurityUtils.getSubject();
      //调用login 方法 进入realm完成认证
      subject.login(token);
      //获取sessionId
      String sessionId = subject.getSession().getId().toString();
      //构造返回结果
      return new Result(ResultCode.SUCCESS,sessionId);

    } catch (Exception e) {
      e.printStackTrace();
    }

    //构造返回结果
    return new Result(ResultCode.SERVER_ERROR.code(),"登陆失败",false);

    //根据电话号码和密码查询用户信息
//    User user = userService.findByMobileAndPassword(mobile,password);
//    if(user == null){
//      return new Result(ResultCode.SERVER_ERROR.code(),"密码或用户名失败",false);
//    }
//    //登陆成功后，获取所有可访问的api权限
//    //api 权限字符串 使用 , 分割
//    StringBuilder builder = new StringBuilder();
//    Set<Role> roles = user.getRoles();
//    for (Role role : roles) {
//      Set<Permission> permissions = role.getPermission();
//      for (Permission permission : permissions) {
//        if(permission.getType() == PermissionConstants.PY_API){
//          builder.append(permission.getCode()).append(",");
//        }
//      }
//    }
    //登陆成功则进行token申请
//    String token = userService.getToken(user,builder.toString());
//    return new Result(ResultCode.SUCCESS,token);
  }

  @ApiOperation("获取用户信息")
  @PostMapping("/profile")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "Authorization",value = "user token",required = true,dataType = "string",paramType = "header"),
  })
  public Result profile() throws Exception {

    //从请求头中获取token 并进行解析(已用拦截器进行过滤)
//    Claims claims = userService.getTokenForHeader(request);

    //普通token做法
//    //获取id
//    String userid = claims.getId();
//
//    User user = userService.findById(userid);
//
//    ProfileResult profileResult = null;
//
//    //根据身份不同 封装不一样的权限
//    if("user".equals(user.getLevel())){
//      //普通用户拥有当前的权限
//      profileResult = new ProfileResult(user);
//    }else{
//      Map<String,Object> map = new HashMap<>();
//
//      //企业管理员具有所有的企业权限
//      if ("coAdmin".equals(user.getLevel())){
//        map.put("enVisible","1");
//      }
//
//      //saas 具有所有权限
//      List<Permission> list = permissionService.findAll(map);
//
//      //封装参数
//      profileResult = new ProfileResult(user,list);
//    }

    //shiro做法
    Subject subject = SecurityUtils.getSubject();

    //获取安全数据
    PrincipalCollection previousPrincipals = subject.getPreviousPrincipals();
    if(previousPrincipals!= null && !previousPrincipals.isEmpty()){
      ProfileResult result = (ProfileResult) previousPrincipals.getPrimaryPrincipal();
      return new Result(ResultCode.SUCCESS,result);
    }

    //1.subject获取所有的安全数据集合
    PrincipalCollection principals = subject.getPrincipals();
    if(principals != null && !principals.isEmpty()){
      //2.获取安全数据
      ProfileResult result = (ProfileResult)principals.getPrimaryPrincipal();
      return new Result(ResultCode.SUCCESS,result);
    }

    return new Result(ResultCode.SERVER_ERROR.code(),"查询失败",false);
  }

  @ApiOperation("上传导入poi文件")
  @PostMapping("/user/import")
  public Result importExcel(@RequestParam(name = "file") MultipartFile file) throws IOException {

    //解析excel
    //获取 Workbook 对象
    Workbook workbook = new XSSFWorkbook(file.getInputStream());
    //获取sheet
    Sheet sheet = workbook.getSheetAt(0);

    //创建存放user对象的集合
    List<User> users = new ArrayList<>();
    //从第二行开始获取数据
    for(int i = 1; i < sheet.getLastRowNum() + 1; i ++ ){
      //获取row
      Row row = sheet.getRow(i);
      //创建封装每行数据的数组
      Object[] obejcts = new Object[row.getLastCellNum()];

      for (int j = 1; j < row.getLastCellNum(); j++) {
        Cell cell = row.getCell(j);
        Object value = this.getValue(cell);
        obejcts[j] = value;

      }

      //根据每一行数据，进行封装成为user对象
      User user = new User(obejcts,companyId,companyName);
      users.add(user);

    }

    //第一个参数用户列表  第二个 部门编码
    userService.saveMore(users);

    return Result.SUCCESS();
  }

  /**
   * 根据单元格元素类型的不同进行对应的数据接收
  **/
  private Object getValue(Cell cell){
    Object object = null;

    switch (cell.getCellType()){
      case STRING:
        object = cell.getStringCellValue();
        break;
      case BOOLEAN: //boolean类型
        object = cell.getBooleanCellValue();
        break;
      case NUMERIC: //数字类型（包含日期和普通数字）
        if(DateUtil.isCellDateFormatted(cell)) {
          object = cell.getDateCellValue();
        }else{
          object = cell.getNumericCellValue();
        }
        break;
      case FORMULA: //公式类型
        object = cell.getCellFormula();
        break;
      default:
        break;
    }

    return object;
  }

  @RequestMapping(value="/user/upload/{id}")
  public Result upload(@PathVariable String id,@RequestParam(name = "file") MultipartFile file) throws Exception {
    String image = userService.uploadImage(id, file);
    return new Result(ResultCode.SUCCESS,image);
  }

}