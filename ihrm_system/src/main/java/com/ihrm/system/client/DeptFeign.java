package com.ihrm.system.client;

import com.ihrm.common.entiy.Result;
import com.ihrm.domain.company.Department;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName DeptFeign
 * @Description 通过feign 访问企业服务的 部门接口
 * @Author YongXi.Wang
 * @Date  2020年04月24日 21:24
 * @Version 1.0.0
*/
@FeignClient(value = "ihrm-company")
@RequestMapping("/company/department")
public interface DeptFeign {

  /**
   * 调用
  **/
  @GetMapping("/{id}")
  public Result findById(@PathVariable("id") String id);

  @PostMapping("/department/search")
  public Department findByCode(@RequestParam("code") String code,@RequestParam("companyId") String companyId);
}
