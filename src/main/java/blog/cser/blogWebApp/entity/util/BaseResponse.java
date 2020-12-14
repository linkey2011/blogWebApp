package blog.cser.blogWebApp.entity.util;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private String msg;
    private T data;
    private Integer code;
}
