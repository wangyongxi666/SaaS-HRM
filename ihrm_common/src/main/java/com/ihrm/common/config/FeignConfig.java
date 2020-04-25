package com.ihrm.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName FeignConfig
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月24日 23:34
 * @Version 1.0.0
*/
@Configuration
public class FeignConfig {

  @Bean
  public RequestInterceptor requestInterceptor(){
    return new RequestInterceptor() {
      @Override
      public void apply(RequestTemplate requestTemplate) {

        //获取request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
          HttpServletRequest request = requestAttributes.getRequest();

          String authorization = request.getHeader("Authorization");

          if(!StringUtils.isEmpty(authorization)){
            requestTemplate.header("Authorization",authorization);
          }
        }

      }
    };
  }

}
