package com.ihrm.domain.company.response;

import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DeptListResult
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月21日 0:33
 * @Version 1.0.0
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptListResult {

  private String companyId;
  private String companyName;
  private String companyManage;

  private List<Department> depts;

  public DeptListResult(Company company,List<Department> depts){
    this.companyId = company.getId();
    this.companyName = company.getName();
    this.companyManage = company.getManagerName();

    this.depts = depts;
  }

}
