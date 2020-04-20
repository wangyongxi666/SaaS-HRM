package com.ihrm.common.exception;

import com.ihrm.common.entiy.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName IhrmException
 * @Description (这里用一句话描述这个类的作用)
 * @Author YongXi.Wang
 * @Date  2020年04月20日 19:46
 * @Version 1.0.0
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IhrmException extends RuntimeException{

  private ResultCode resultCode;
  private String message;
  private Integer code;

  public IhrmException(Integer code,String message){
    this.code = code;
    this.message = message;
  }

  public IhrmException(ResultCode resultCode){
    this.resultCode = resultCode;
  }



}
