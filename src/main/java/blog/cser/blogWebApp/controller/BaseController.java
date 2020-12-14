package blog.cser.blogWebApp.controller;

import blog.cser.blogWebApp.conf.ConstantValue;
import blog.cser.blogWebApp.entity.Blog;
import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.entity.util.BaseResponse;
import blog.cser.blogWebApp.util.CSerUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class BaseController {
    Blog myBlog;

    BaseController() {
        System.out.println("当前运行文件夹："+ System.getProperty("user.dir"));
        //String firstPath = "C:\\Users\\i\\Desktop\\blogtest\\";
        String path = System.getProperty("user.dir") + File.separator + ConstantValue.PostPath;
        System.out.println("读取的文件所在文件夹："+path);
      //  List<Post> p = CSerUtils.initMyBlog(path);
        this.myBlog = CSerUtils.initMyBlog(path);
        int o = 1;
      //  this.postList = p;
    }

/*   <T> BaseResponse<T> setResultSucess(T data){
       return
    }*/


}
