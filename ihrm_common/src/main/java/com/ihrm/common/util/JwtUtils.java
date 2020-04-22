package com.ihrm.common.util;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName JwtUtils
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月22日 11:54
 * @Version 1.0.0
*/
@Data
@ConfigurationProperties("jwt.config")
public class JwtUtils {

  //签名私钥
  private String key;

  //签名失效时间
  private Long ttl;

  /**
   * 生成token
  **/
  public String createJwt(String username, String id, Map<String,Object> map){

    //计算时间
    long now = System.currentTimeMillis();
    long exp = now + ttl;

    //创建JwtBuilder
    JwtBuilder builder = Jwts.builder()
            //设置失效时间
            .setExpiration(new Date(exp))
            .setIssuedAt(new Date())
            .signWith(SignatureAlgorithm.HS256,key)
            .setId(id)
            .setSubject(username);

    //根据map设置claims
//    Set<String> set = map.keySet();
//    for (String key : set) {
//      builder.claim(key, map.get(key));
//    }

    Set<Map.Entry<String, Object>> entries = map.entrySet();
    for (Map.Entry<String, Object> entry : entries) {
      String key = entry.getKey();
      Object value = entry.getValue();

      builder.claim(key, value);
    }


    String token = builder.compact();

    return token;
  }

  /**
   * 解析token
  **/
  public Claims parseJwt(String token){

    Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
    System.out.println(claimsJws);

    Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

    return claims;
  }

}
