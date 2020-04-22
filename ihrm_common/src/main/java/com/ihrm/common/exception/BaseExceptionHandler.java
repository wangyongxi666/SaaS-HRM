package com.ihrm.common.exception;

import com.ihrm.common.entiy.Result;
import com.ihrm.common.entiy.ResultCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName BaseExceptionHandler
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 18:31
 * @Version 1.0.0
*/
@ControllerAdvice
public class BaseExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  @ResponseBody
  public Result error(HttpServletRequest request, HttpServletResponse response,Exception e){

    e.printStackTrace();

    return new Result(ResultCode.SERVER_ERROR);
  }

  @ExceptionHandler(value = IhrmException.class)
  @ResponseBody
  public Result myError(IhrmException e){

    e.printStackTrace();

    if(e.getMessage() != null){
      return new Result(ResultCode.SERVER_ERROR.code(),e.getMessage(),false);
    }

    if(e.getResultCode() == null){
      return new Result(ResultCode.SERVER_ERROR);
    }

    return new Result(e.getResultCode());
  }

}
