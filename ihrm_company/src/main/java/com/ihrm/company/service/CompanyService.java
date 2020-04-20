package com.ihrm.company.service;

import com.ihrm.domain.company.Company;

import java.util.List;

public interface CompanyService {

  /**
   * 添加企业信息
  **/
  void add(Company company);

  /**
   * 更新企业信息
  **/
  void update(Company company);

  /**
   * 删除企业信息
  **/
  void delete(String companyId);

  /**
   * 根据id查询企业信息
  **/
  Company findById(String companyId);

  /**
   * 查询企业列表
  **/
  List<Company> findAll();

}
