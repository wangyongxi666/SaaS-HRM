package com.ihrm.system.service;


import com.baidu.aip.util.Base64Util;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.common.util.IdWorker;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.FaceLoginResult;
import com.ihrm.domain.system.response.QRCode;
import com.ihrm.system.dao.UserResitory;
import com.ihrm.system.utils.BaiduAiUtil;
import com.ihrm.system.utils.QRCodeUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Service
public class FaceLoginService {

    @Value("${qr.url}")
    private String url;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private QRCodeUtil qrCodeUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BaiduAiUtil baiduAiUtil;
    @Autowired
    private UserResitory userResitory;

	//创建二维码
    public QRCode getQRCode() throws Exception {

      //创建唯一标识
      String code = idWorker.nextId() + "";
      //创建二维码
      String content = url + "?code=" + code;
      System.out.println(content);
      String qrCode = qrCodeUtil.crateQRCode(content);
      FaceLoginResult loginResult = new FaceLoginResult("-1");

      //存入当前二维码状态(存入redis)
      redisTemplate.boundValueOps(getCacheKey(code)).set(loginResult,1, TimeUnit.MINUTES);

      System.out.println(qrCode);

	  return new QRCode(code,qrCode);
    }

	//根据唯一标识，查询用户是否登录成功
    public FaceLoginResult checkQRCode(String code) {

      FaceLoginResult loginResult = (FaceLoginResult) redisTemplate.opsForValue().get(getCacheKey(code));

      return loginResult;
    }

	//扫描二维码之后，使用拍摄照片进行登录
    public String loginByFace(String code, MultipartFile attachment) throws Exception {
      //调用百度云 查询当前用户
      String userId = baiduAiUtil.faceSearch(Base64Util.encode(attachment.getBytes()));
      FaceLoginResult loginResult = new FaceLoginResult("0");
      //自动登陆
      if(!StringUtils.isEmpty(userId)){
        //模拟登陆逻辑
        //查询用户对象
        User user = userResitory.findById(userId).get();

        //获取subject
        if(user != null){
          //调用login方法登陆
          Subject subject = SecurityUtils.getSubject();

          //获取token（session）
          subject.login(
//                  new UsernamePasswordToken(user.getMobile(),new Md5Hash(user.getPassword(), user.getMobile(), 3).toString())
                  new UsernamePasswordToken(user.getMobile(),user.getPassword())
          );
          String token = subject.getSession().getId().toString();

          loginResult.setState("1");
          loginResult.setUserId(userId);
          loginResult.setToken(token);
          redisTemplate.boundValueOps(getCacheKey(code)).set(loginResult);
          return userId;
        }
      }
      //修改二维码状态
      redisTemplate.boundValueOps(getCacheKey(code)).set(loginResult,1,TimeUnit.MINUTES);

	  return null;
    }

	//构造缓存key
    private String getCacheKey(String code) {
        return "qrcode_" + code;
    }
}
