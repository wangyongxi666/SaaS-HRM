package com.ihrm.company.controller;

import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.exception.IhrmException;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CompanyController
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 15:29
 * @Version 1.0.0
*/
@Api(tags = "企业管理接口",description = "提供企业增、删、改、查服务")
@RestController
@RequestMapping("/company")
@CrossOrigin //解决跨域问题
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @ApiOperation("添加企业信息")
  @PostMapping
  @ApiImplicitParams({
          @ApiImplicitParam(name = "company",value = "添加数据",required = true,dataType = "Company",paramType = "body"),
  })
  public Result add(@RequestBody Company company){

    companyService.add(company);

    return new Result( ResultCode.SUCCESS);
  }

  @ApiOperation("更新企业信息")
  @PutMapping("/{companyId}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "companyId",value = "数据主键",required = true,dataType = "String",paramType = "path"),
          @ApiImplicitParam(name = "company",value = "修改数据",required = true,dataType = "Company",paramType = "body")
  })
  public Result update(@PathVariable("companyId") String companyId,@RequestBody Company company){

    company.setId(companyId);
    companyService.update(company);

    return new Result(ResultCode.SUCCESS);
  }

  @ApiOperation("删除企业信息")
  @DeleteMapping("/{companyId}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "companyId",value = "数据主键",required = true,dataType = "String",paramType = "path")
  })
  public Result delete(@PathVariable("companyId") String companyId){

    companyService.delete(companyId);

    return new Result(ResultCode.SUCCESS);
  }

  @ApiOperation("根据id查询企业信息")
  @GetMapping("/{companyId}")
  @ApiImplicitParams({
          @ApiImplicitParam(name = "companyId",value = "数据主键",required = true,dataType = "String",paramType = "path")
  })
  public Result findById(@PathVariable("companyId") String companyId){

    Company company = companyService.findById(companyId);

    return new Result(ResultCode.SUCCESS,company);
  }

  @ApiOperation("查询全部企业信息")
  @GetMapping()
  public Result findAll(){

    List<Company> list = companyService.findAll();

    return new Result(ResultCode.SUCCESS,list);
  }

  @ApiOperation("测试自定义异常抛出")
  @GetMapping("/error")
  public void errorTest() throws IhrmException{
    throw new IhrmException();
  }

  @ApiOperation("定位subject问题")
  @GetMapping("/subject/check")
  public String subjectDemo(){

    Subject subject = SecurityUtils.getSubject();

    if(subject != null){
      return "查到subject" + subject;
    }else{
      return "查不到subject" + subject;
    }
  }

}
