package com.ihrm.system.controller;

import com.baidu.aip.util.Base64Util;
import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import com.ihrm.domain.system.response.FaceLoginResult;
import com.ihrm.domain.system.response.QRCode;
import com.ihrm.system.service.FaceLoginService;
import com.ihrm.system.utils.BaiduAiUtil;
import com.ihrm.system.utils.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/sys/faceLogin")
public class FaceLoginController {

    @Autowired
    private FaceLoginService faceLoginService;
    @Autowired
    private BaiduAiUtil baiduAiUtil;

    /**
     * 获取刷脸登录二维码
     */
    @RequestMapping(value = "/qrcode", method = RequestMethod.GET)
    public Result qrcode() throws Exception {
        QRCode qrCode = faceLoginService.getQRCode();
        return new Result(ResultCode.SUCCESS,qrCode);
    }

    /**
     * 检查二维码：登录页面轮询调用此方法，根据唯一标识code判断用户登录情况
     */
    @RequestMapping(value = "/qrcode/{code}", method = RequestMethod.GET)
    public Result qrcodeCeck(@PathVariable(name = "code") String code) throws Exception {
        FaceLoginResult loginResult = faceLoginService.checkQRCode(code);
        return new Result(ResultCode.SUCCESS,loginResult);
    }

    /**
     * 人脸登录：根据落地页随机拍摄的面部头像进行登录
     *          根据拍摄的图片调用百度云AI进行检索查找
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.POST)
    public Result loginByFace(@PathVariable(name = "code") String code, @RequestParam(name = "file") MultipartFile attachment) throws Exception {
        String userId = faceLoginService.loginByFace(code, attachment);

        if(!StringUtils.isEmpty(userId)){
            return new Result(ResultCode.SUCCESS,userId);
        }else {
            return Result.ERROR();
        }
    }


    /**
     * 图像检测，判断图片中是否存在面部头像
     */
    @RequestMapping(value = "/checkFace", method = RequestMethod.POST)
    public Result checkFace(@RequestParam(name = "file") MultipartFile attachment) throws Exception {

        String images = Base64Util.encode(attachment.getBytes());

        Boolean check = baiduAiUtil.faceCheck(images);

        if(check){
            return new Result(ResultCode.SUCCESS.code(),"是人脸",true);
        }else {
            return new Result(ResultCode.SERVER_ERROR.code(),"不是人脸",false);
        }

    }

}
