package com.ihrm.company;

import com.ihrm.company.dao.CompanyResitory;
import com.ihrm.domain.company.Company;
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
public class CompanyTest {

  @Autowired
  private CompanyResitory companyResitory;


  @Test
  public void test01(){
    List<Company> all = companyResitory.findAll();
    System.out.println(all);
  }

}
