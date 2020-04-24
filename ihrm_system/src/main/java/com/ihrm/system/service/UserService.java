package com.ihrm.system.service;

import com.ihrm.domain.system.User;
import io.jsonwebtoken.Claims;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserService {

  /**
   * 根据手机号和密码查询用户
  **/
  User findByMobileAndPassword(String mobile, String password);

  /**
   * 根据手机号查询用户
  **/
  User findByMobile(String mobile);
  /**
   * 添加用户
   */
  void save(User user);
  /**
   * 更新用户
   */
  void update(User user);
  /**
   * 根据ID查询用户
   */
  User findById(String id);
  /**
   * 删除部门
   *
   * @param id 部门ID
   */
  void delete(String id);

  /**
   * 条件查询
  **/
  Page<User> findSearch(Map<String,Object> map, int page, int size);

  /**
   * 分配角色
   */
  void assignRoles(String userId,List<String> roleIds);

  /**
   * 调整部门
   */
  public void changeDept(String deptId, String deptName, List<String> ids);

  /**
   * token申请
  **/
  String getToken(User user,String builder);

  /**
   * 从header中获取并解析token
  **/
  Claims getTokenForHeader(HttpServletRequest request);
}
