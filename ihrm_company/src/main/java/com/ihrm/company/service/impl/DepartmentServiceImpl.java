package com.ihrm.company.service.impl;

import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.exception.IhrmException;
import com.ihrm.common.util.IdWorker;
import com.ihrm.company.dao.CompanyResitory;
import com.ihrm.company.dao.DepartmentResitory;
import com.ihrm.company.service.DepartmentService;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName DepartmentServiceImpl
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 23:24
 * @Version 1.0.0
*/
@Service
public class DepartmentServiceImpl implements DepartmentService {

  @Autowired
  private DepartmentResitory departmentResitory;
  @Autowired
  private CompanyResitory companyResitory;
  @Autowired
  private IdWorker idWorker;

  /**
   * 添加部门信息
   *
   * @param department
   */
  @Override
  public void add(Department department) {

    //设置企业id
    String companyId = "1";

    String id = idWorker.nextId() + "";

    department.setId(id);
    department.setCompanyId(companyId);

    departmentResitory.save(department);
  }

  /**
   * 修改部门信息
   *
   * @param department
   */
  @Override
  public void update(Department department) {

    Optional<Department> opt = departmentResitory.findById(department.getId());

    if(opt.isPresent()){
      Department department1 = opt.get();

      //设置修改属性
      department1.setName(department.getName());
      department1.setParent_id(department.getParent_id());
      department1.setManagerId(department.getManagerId());
      department1.setIntroduce(department.getIntroduce());

      departmentResitory.save(department1);
    }

  }

  /**
   * 根据id查询部门信息
   *
   * @param id
   */
  @Override
  public Department findById(String id) {

    Optional<Department> opt = departmentResitory.findById(id);

    Department department = opt.get();

    return department;
  }

  /**
   * 查询部门信息列表
   **/
  @Override
  public DeptListResult findAll(String companyId) {

//    Specification<Department> spec = new Specification<Department>() {
//      @Override
//      public Predicate toPredicate(Root<Department> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
//
//        //根据企业id查询部门信息
//        return cb.equal(root.get("companyId").as(String.class),companyId);
//      }
//    };

    Department department = new Department();
    department.setCompanyId(companyId);

    Example<Department> example = Example.of(department);

    List<Department> list = departmentResitory.findAll(example);

    Optional<Company> opt = companyResitory.findById(companyId);
    if(!opt.isPresent()){
      throw new IhrmException(ResultCode.SERVER_ERROR.code(),"查询不到企业信息");
    }

    Company company = opt.get();

    //封装返回对象
    DeptListResult deptListResult = new DeptListResult(company,list);

    return deptListResult;
  }

  /**
   * 根据企业id和部门id删除
   *
   * @param companyId
   * @param id
   */
  @Override
  public void deleteByCompanyIdAndDeptId(String companyId, String id) {

    //根据企业id和部门id查询部门信息 （防止横向越权）
    Department department = new Department();
    department.setCompanyId(companyId);
    department.setId(id);

    Example<Department> ex = Example.of(department);

    Optional<Department> opt = departmentResitory.findOne(ex);

    if(!opt.isPresent() || opt.get() == null){
      throw new IhrmException(ResultCode.SERVER_ERROR.code(),"查询不到该企业下的部门信息");
    }

    departmentResitory.deleteById(id);

  }

  /**
   * 根据企业id和部门id查询
   *
   * @param code
   * @param companyId
   */
  @Override
  public Department findByCode(String code, String companyId) {
    return null;
  }
}
