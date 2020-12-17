package blog.cser.blogWebApp.entity.util;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private Integer code;
    private String msg;
    private T data;


    public BaseResponse(T data) {
        this.data = data;
    }

    public  BaseResponse(Integer code, String msg, T data) {
        this.data = data;
        this.msg = msg;
        this.code = code;
    }
}
