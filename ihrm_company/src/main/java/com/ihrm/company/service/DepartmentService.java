package com.ihrm.company.service;

import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;

import java.util.List;

public interface DepartmentService {

  /**
   * 添加部门信息
  **/
  void add(Department department);

  /**
   * 修改部门信息
  **/
  void update(Department department);

  /**
   * 根据id查询部门信息
  **/
  Department findById(String id);

  /**
   * 查询部门信息列表
  **/
  DeptListResult findAll(String companyId);

  /**
   * 根据企业id和部门id删除
  **/
  void deleteByCompanyIdAndDeptId(String companyId, String id);
}
