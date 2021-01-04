package blog.cser.blogWebApp.entity.util;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class BaseResponse<T> {
    private Integer code;
    private String msg;
    private T csData;


    public BaseResponse(T data) {
        this.csData = data;
    }

    public  BaseResponse(Integer code, String msg, T data) {
        this.csData = data;
        this.msg = msg;
        this.code = code;
    }
}
