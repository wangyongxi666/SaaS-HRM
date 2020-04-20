package com.ihrm.company.service.impl;

import com.ihrm.common.util.IdWorker;
import com.ihrm.company.dao.CompanyResitory;
import com.ihrm.company.service.CompanyService;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName CompanyServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 15:43
 * @Version 1.0.0
*/
@Service
public class CompanyServiceImpl implements CompanyService{

  @Autowired
  private IdWorker idWorker;
  @Autowired
  private CompanyResitory companyResitory;

  /**
   * 添加企业信息
   *
   * @param company
   */
  @Override
  public void add(Company company) {

    //自己生成主键
    String id = idWorker.nextId() + "";
    company.setId(id);

    //设置默认状态
    company.setAuditState("0");
    company.setState(0);

    companyResitory.save(company);

  }

  /**
   * 添加企业信息
   *
   * @param company
   */
  @Override
  public void update(Company company) {

    Optional<Company> opt = companyResitory.findById(company.getId());

    if(opt.isPresent()){
      Company company1 = opt.get();
      company1.setName(company.getName());
      company1.setCompanyPhone(company.getCompanyPhone());

      companyResitory.save(company1);
    }
  }
  /**
   * 添加企业信息
   *
   * @param companyId
   */
  @Override
  public void delete(String companyId) {

    Optional<Company> opt = companyResitory.findById(companyId);

    if(opt.isPresent()){
      Company company1 = opt.get();

      companyResitory.delete(company1);
    }
  }

  /**
   * 根据id查询企业信息
   *
   * @param companyId
   */
  @Override
  public Company findById(String companyId) {
    Company company = companyResitory.findById(companyId).get();
    return company;
  }

  /**
   * 查询企业列表
   **/
  @Override
  public List<Company> findAll() {
    List<Company> all = companyResitory.findAll();
    return all;
  }

}
