package com.ihrm.company.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName DepartmentController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 23:25
 * @Version 1.0.0
*/
@Api(value = "部门管理接口",description = "提供部门增、删、改、查服务")
@RestController
@RequestMapping("/department")
@CrossOrigin //解决跨域问题
public class DepartmentController extends BaseController{

  @Autowired
  private DepartmentService departmentService;

  @ApiOperation("添加部门信息")
  @PostMapping
  @ApiImplicitParams({
          @ApiImplicitParam(name = "department",value = "部门数据",dataType = "Department",required = true,paramType = "body")
  })
  public Result add(@RequestBody Department department){
    departmentService.add(department);

    return new Result(ResultCode.SUCCESS);
  }

  @ApiOperation("根据id修改部门信息")
  @PutMapping("/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "主键id",dataType = "String",required = true,paramType = "path"),
          @ApiImplicitParam(name = "department",value = "部门数据",dataType = "Department",required = true,paramType = "body")
  })
  public Result update(@PathVariable("id") String id,@RequestBody Department department){

    department.setId(id);

    departmentService.update(department);

    return new Result(ResultCode.SUCCESS);
  }

  @ApiOperation("根据id查询部门信息")
  @GetMapping("/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "id",value = "主键id",dataType = "String",required = true,paramType = "path")
  })
  public Result findById(@PathVariable("id") String id){

    Department department = departmentService.findById(id);

    return new Result(ResultCode.SUCCESS,department);
  }

  @ApiOperation("根据企业id查询部门信息")
  @GetMapping("/findAll/{companyId}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "companyId",value = "企业id",dataType = "String",required = true,paramType = "path")
  })
  public Result findAll(@PathVariable("companyId") String companyId){

    DeptListResult data = departmentService.findAll(companyId);

    return new Result(ResultCode.SUCCESS,data);
  }

  @ApiOperation("根据企业id和部门id删除")
  @DeleteMapping("/company/{companyId}/department/{id}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "companyId",value = "企业id",dataType = "String",required = true,paramType = "path"),
          @ApiImplicitParam(name = "id",value = "部门id",dataType = "String",required = true,paramType = "path")
  })
  public Result deleteByCompanyIdAndDeptId(@PathVariable("companyId") String companyId,@PathVariable("id") String id){

    departmentService.deleteByCompanyIdAndDeptId(companyId,id);

    return new Result(ResultCode.SUCCESS);
  }




}

