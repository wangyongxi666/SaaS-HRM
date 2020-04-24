package com.ihrm.common.controller;
import com.ihrm.domain.system.response.ProfileResult;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.ModelAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
* 公共controller
*     获取request，response
*     获取企业id，获取企业名称
*/
public class BaseController {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected Claims claims;
    protected ProfileResult result;
    protected String companyId;
    protected String companyName;

    //使用jwt方式获取
//    @ModelAttribute
//    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response) {
//        this.request = request;
//        this.response = response;
//
//      Object object = request.getAttribute("user_claims");
//
//      if(object != null){
//        claims = (Claims) object;
//      }
//   }

  //使用shiro获取
  @ModelAttribute
  public void setResAnReq(HttpServletRequest request,HttpServletResponse response) {
    this.request = request;
    this.response = response;

    //获取session中的安全数据
    Subject subject = SecurityUtils.getSubject();
    //1.subject获取所有的安全数据集合
    PrincipalCollection principals = subject.getPrincipals();
    if(principals != null && !principals.isEmpty()){
      //2.获取安全数据
      ProfileResult result = (ProfileResult)principals.getPrimaryPrincipal();
      this.companyId = result.getCompanyId();
      this.companyName = result.getCompany();
    }
  }
}