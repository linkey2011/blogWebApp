package blog.cser.blogWebApp.controller;

import blog.cser.blogWebApp.conf.ConstantValue;
import blog.cser.blogWebApp.entity.Blog;
import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.entity.util.BaseResponse;
import blog.cser.blogWebApp.entity.util.ResultCode;
import blog.cser.blogWebApp.util.CSerUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class BaseController<T> {

    BaseResponse<T> setResultSucess(T data){
       return setResult(ResultCode.SUCCESS.code(),ResultCode.SUCCESS.message(),data);
    }
    BaseResponse<T> setResultSucess(){
        return setResult(ResultCode.SUCCESS.code(),ResultCode.SUCCESS.message(),null);
    }

    BaseResponse<T> setResultError(String msg){
        return setResult(ResultCode.SUCCESS.code(),msg,null);
    }
    BaseResponse<T> setResultError(){
        return setResult(ResultCode.SUCCESS.code(),ResultCode.ERROR.message(),null);
    }

    BaseResponse<T> setResult(Integer code,String msg,T data){
        return new BaseResponse(code,msg,data);
    }


}
