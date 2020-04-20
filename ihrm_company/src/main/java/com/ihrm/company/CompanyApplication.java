package com.ihrm.company;

import com.ihrm.common.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName SpringBootApplication
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 13:28
 * @Version 1.0.0
*/
@SpringBootApplication(scanBasePackages = "com.ihrm")
@EntityScan("com.ihrm.domain.company")
public class CompanyApplication {

  public static void main(String[] args) {
    SpringApplication.run(CompanyApplication.class, args);
  }

  @Bean
  public IdWorker idWorkker() {
    return new IdWorker(1, 1);
  }

}