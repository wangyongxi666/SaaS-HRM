package com.ihrm.common.controller;
import io.jsonwebtoken.Claims;
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

    @ModelAttribute
    public void setReqAndResp(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

      Object object = request.getAttribute("user_claims");

      if(object != null){
        claims = (Claims) object;
      }
   }
    //企业id，（暂时使用1,以后会动态获取）
    public String parseCompanyId() {
        return this.claims.get("companyId").toString();
   }
    public String parseCompanyName() {
        return this.claims.get("companyName").toString();
   }
}