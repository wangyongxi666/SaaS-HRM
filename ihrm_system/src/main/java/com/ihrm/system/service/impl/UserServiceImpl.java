package com.ihrm.system.service.impl;

import com.ihrm.common.util.IdWorker;
import com.ihrm.common.util.JwtUtils;
import com.ihrm.common.util.QiniuUtils;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.DeptFeign;
import com.ihrm.system.dao.RoleResitory;
import com.ihrm.system.dao.UserResitory;
import com.ihrm.system.service.UserService;
import com.ihrm.system.utils.BaiduAiUtil;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import io.jsonwebtoken.Claims;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 部门操作业务逻辑层
 */
@Service
public class UserServiceImpl implements UserService{
  @Autowired
  private IdWorker idWorker;
  @Autowired
  private UserResitory userResitory;
  @Autowired
  private RoleResitory roleResitory;
  @Autowired
  private DeptFeign deptFeign;
  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  private BaiduAiUtil baiduAiUtil;

  /**
   * 根据手机号和密码查询用户
   **/
  public User findByMobileAndPassword(String mobile, String password) {
    User user = userResitory.findUserByMobile(mobile);
    if (user != null && password.equals(user.getPassword())) {
      return user;
    } else {
      return null;
    }
  }

  /**
   * 根据手机号查询用户
   *
   * @param mobile
   */
  @Override
  public User findByMobile(String mobile) {
    User user = userResitory.findUserByMobile(mobile);
    if (user != null) {
      return user;
    } else {
      return null;
    }
  }

  /**
   * 添加用户
   */
  @Override
  public void save(User user) {
    //填充其他参数
    user.setId(idWorker.nextId() + "");
    user.setCreateTime(new Date()); //创建时间

    //加密
    String password = new Md5Hash("123456", user.getMobile(), 3).toString();
    user.setLevel("user");
    user.setPassword(password);//设置默认登录密码
    user.setEnableState(1);//状态
    userResitory.save(user);
  }

  /**
   * 导入execl批量添加用户
   *
   * @param users
   * @param
   */
  @Override
  public void saveMore(List<User> users) {
    for (User user : users) {
      //配置密码
      user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());
      //配置id
      user.setId(idWorker.nextId() + "");
      //其他基本属性
      user.setInServiceStatus(1);
      user.setEnableState(1);
      user.setLevel("user");
      //获取部门信息
      Department dept = deptFeign.findByCode(user.getDepartmentId(), user.getCompanyId());
      if (dept != null) {
        user.setDepartmentId(dept.getId());
        user.setDepartmentName(dept.getName());
      }
      userResitory.save(user);
    }
  }

  /**
   * 更新用户
   */
  @Override
  public void update(User user) {
    User targer = userResitory.getOne(user.getId());
    targer.setPassword(user.getPassword());
    targer.setUsername(user.getUsername());
    targer.setMobile(user.getMobile());
    targer.setDepartmentId(user.getDepartmentId());
    targer.setDepartmentName(user.getDepartmentName());
    userResitory.save(targer);
  }

  /**
   * 根据ID查询用户
   */
  @Override
  public User findById(String id) {
    return userResitory.findById(id).get();
  }

  /**
   * 删除部门
   *
   * @param id 部门ID
   */
  @Override
  public void delete(String id) {
    userResitory.deleteById(id);
  }

  @Override
  public Page<User> findSearch(Map<String, Object> map, int page, int size) {
    return userResitory.findAll(createSpecification(map), PageRequest.of(page - 1, size));
  }

  /**
   * 调整部门
   */
  @Override
  public void changeDept(String deptId, String deptName, List<String> ids) {
    for (String id : ids) {
      User user = userResitory.findById(id).get();
      user.setDepartmentName(deptName);
      user.setDepartmentId(deptId);
      userResitory.save(user);
    }
  }

  /**
   * 分配角色
   */
  @Override
  public void assignRoles(String userId, List<String> roleIds) {
    User user = userResitory.findById(userId).get();
    Set<Role> roles = new HashSet<>();
    for (String id : roleIds) {
      Role role = roleResitory.findById(id).get();
      roles.add(role);
    }
    //设置用户和角色之间的关系
    user.setRoles(roles);
    userResitory.save(user);
  }

  /**
   * token申请
   *
   * @param user
   */
  @Override
  public String getToken(User user,String builder) {

    Map<String,Object> map = new HashMap<>();

    map.put("companyId",user.getCompanyId());
    map.put("companyName",user.getCompanyName());
    map.put("apis",builder);

    String token = jwtUtils.createJwt(user.getUsername(), user.getId(), map);

    return token;
  }

  /**
   * (使用spring拦截器替代次方法的功能，此方法作废)
   * 从header中获取并解析token
   **/
  @Override
  public Claims getTokenForHeader(HttpServletRequest request) {
    //约定的key
    String tokenKey = "Authorization";
    //约定的值的格式 Bearer token值
    String toeknFormat = "Bearer ";

    //获取token
    String token = request.getHeader(tokenKey);
    //替换前缀
    String replace = token.replace(toeknFormat, "");
    //解析token

    Claims claims = jwtUtils.parseJwt(replace);

    return claims;
  }

  /**
   * @param id
   * @param file
   * @上传图片
   */
  @Override
  public String uploadImage(String id, MultipartFile file) throws Exception {
    //根据id查询用户
    User user = userResitory.findById(id).get();

//    //对上传文件进行Base64编码
//    String s = Base64.encode(file.getBytes());
//    //拼接DataURL数据头
//    String dataUrl = new String("data:image/jpg;base64,"+s);

    String base64 = Base64.encode(file.getBytes());

    //使用七牛云存储
    String url = QiniuUtils.upload2Qiniu(file.getBytes(), user.getId());

    //调用百度云api 不存在则调用上传，存在则调用更新
    Boolean flag = baiduAiUtil.faceExist(id);
    if(flag){
      baiduAiUtil.faceUpdate(id,base64);
    }else {
      baiduAiUtil.faceRegister(id,base64);
    }

    user.setStaffPhoto(url);
    //保存图片信息
    userResitory.save(user);
    return url;
  }

  /**
   * 动态条件构建
   *
   * @param searchMap
   * @return
   */
  private Specification<User> createSpecification(Map searchMap) {
    return new Specification<User>() {
      @Override
      public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicateList = new ArrayList<Predicate>();
        // ID
        if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
          predicateList.add(cb.equal(root.get("id").as(String.class),
                  (String) searchMap.get("id")));
        }
        // 手机号码
        if (searchMap.get("mobile") != null &&
                !"".equals(searchMap.get("mobile"))) {
          predicateList.add(cb.equal(root.get("mobile").as(String.class),
                  (String) searchMap.get("mobile")));
        }
        // 用户ID
        if (searchMap.get("departmentId") != null &&
                !"".equals(searchMap.get("departmentId"))) {

          predicateList.add(cb.like(root.get("departmentId").as(String.class),
                  (String) searchMap.get("departmentId")));
        }
        // 标题
        if (searchMap.get("formOfEmployment") != null &&
                !"".equals(searchMap.get("formOfEmployment"))) {

          predicateList.add(cb.like(root.get("formOfEmployment").as(String.class),
                  (String) searchMap.get("formOfEmployment")));
        }
        if (searchMap.get("companyId") != null &&
                !"".equals(searchMap.get("companyId"))) {
          predicateList.add(cb.like(root.get("companyId").as(String.class),
                  (String) searchMap.get("companyId")));
        }
        if (searchMap.get("hasDept") != null &&
                !"".equals(searchMap.get("hasDept"))) {
          if ("0".equals((String) searchMap.get("hasDept"))) {
            predicateList.add(cb.isNull(root.get("departmentId")));
          } else {
            predicateList.add(cb.isNotNull(root.get("departmentId")));
          }
        }
        return cb.and(predicateList.toArray(new
                Predicate[predicateList.size()]));
      }
    };
  }


}
