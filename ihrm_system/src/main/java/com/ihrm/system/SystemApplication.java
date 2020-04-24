package com.ihrm.system;

import com.ihrm.common.util.IdWorker;
import com.ihrm.common.util.JwtUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

import javax.persistence.DiscriminatorColumn;

/**
 * @ClassName SystemApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月21日 11:49
 * @Version 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.ihrm")
@EntityScan("com.ihrm.domain.system")
@EnableEurekaClient
@DiscriminatorColumn
public class SystemApplication {
  public static void main(String[] args) {
    SpringApplication.run(SystemApplication.class, args);
  }

  @Bean
  public IdWorker idWorkker() {
    return new IdWorker(1, 1);
  }

  @Bean
  public JwtUtils jwtUtils(){
    JwtUtils jwtUtils = new JwtUtils();
    return jwtUtils;
  }

  //解决no session
  @Bean
  public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
    return new OpenEntityManagerInViewFilter();
  }
}
