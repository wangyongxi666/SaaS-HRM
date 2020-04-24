package com.ihrm.company;

import com.ihrm.company.dao.CompanyResitory;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.system.response.ProfileResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @ClassName CompanyTest
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 15:23
 * @Version 1.0.0
*/
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CompanyTest {

  @Autowired
  private CompanyResitory companyResitory;


  @Test
  public void test01(){
    List<Company> all = companyResitory.findAll();
    System.out.println(all);
  }


  @Test
  public void test_log(){
    log.info("这个是info");
    //获取session中的安全数据
    Subject subject = SecurityUtils.getSubject();
    //1.subject获取所有的安全数据集合
    PrincipalCollection principals = subject.getPrincipals();
    if(principals != null && !principals.isEmpty()){
      //2.获取安全数据
      ProfileResult result = (ProfileResult)principals.getPrimaryPrincipal();
      System.out.println(result.getCompanyId());
      System.out.println(result.getCompany());
    }
    log.error("这个是error");
  }

}
