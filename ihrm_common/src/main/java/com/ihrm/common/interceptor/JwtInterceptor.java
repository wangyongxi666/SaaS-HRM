package com.ihrm.common.interceptor;

import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.exception.IhrmException;
import com.ihrm.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName JwtInterceptor
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月22日 23:18
 * @Version 1.0.0
*/
@Component
public class JwtInterceptor extends HandlerInterceptorAdapter{

  @Autowired
  private JwtUtils jwtUtils;

  //进入控制器之前执行的内容
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    //获取token
    String tokenKey = "Authorization";
    String token = request.getHeader(tokenKey);

    //判断是否为空 且是否是指定开头
    //约定的值的格式 Bearer token值
    String toeknFormat = "Bearer ";
    if(!StringUtils.isEmpty(token) && token.startsWith(toeknFormat)){

      //替换前缀
      String replace = token.replace(toeknFormat, "");

      //解析token
      Claims claims = jwtUtils.parseJwt(replace);

      if(claims != null){

//        //获取当前用户可以访问的apis 权限
//        String apis = claims.get("apis").toString();
//        //获取当前接口的注解 的权限字段
//        HandlerMethod method = (HandlerMethod)handler;
//        String api = method.getMethodAnnotation(DeleteMapping.class).name();
//        //检查是否有权限可以访问
//        if(!apis.contains(api)){
//          throw new IhrmException(ResultCode.SERVER_ERROR.code(),"用户没有访问次api的权限");
//        }

        //存入request域中
        request.setAttribute("user_claims",claims);
        return true;
      }
    }

    throw new IhrmException(ResultCode.SERVER_ERROR.code(),"用户尚未登陆，请登陆之后再进行操作");
  }

  //控制器之后执行的方法
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    super.postHandle(request, response, handler, modelAndView);
  }

  //响应结束之前执行
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    super.afterCompletion(request, response, handler, ex);
  }
}
