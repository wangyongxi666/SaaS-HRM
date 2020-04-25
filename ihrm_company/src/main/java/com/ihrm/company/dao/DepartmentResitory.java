package com.ihrm.company.dao;

import com.ihrm.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DepartmentResitory extends JpaRepository<Department,String> , JpaSpecificationExecutor<Department>{

  Department findDepartmentByCodeAndCompanyId(String code,String companyId);
}
