package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.PageResult;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.domain.system.User;
import com.ihrm.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//1.解决跨域
@CrossOrigin
@RestController
@RequestMapping("/sys")
@Api(tags = "用户管理Api", description= "用户的 增删改查")
public class UserController extends BaseController {
  @Autowired
  private UserService userService;

  @ApiOperation("保存用户")
  @PostMapping("/user")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "user",value = "用户数据",required = true,dataType = "User",paramType = "body")
  })
  public Result add(@RequestBody User user) throws Exception {
    user.setCompanyId(parseCompanyId());
    user.setCompanyName(parseCompanyName());
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

  @ApiOperation("删除用户")
  @DeleteMapping("/user/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "用户id",required = true,dataType = "String",paramType = "path")
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
    map.put("companyId", parseCompanyId());
    Page<User> searchPage = userService.findSearch(map, page, size);
    PageResult<User> pr = new PageResult(searchPage.getTotalElements(), searchPage.getContent());
    return new Result(ResultCode.SUCCESS, pr);
  }
}