package blog.cser.blogWebApp.controller;

import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.service.MainService;
import blog.cser.blogWebApp.util.CSerUtils;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class MainController extends BaseController  implements MainService {


    @GetMapping("/test")
    @ResponseBody
    public String hell0() {

        String i = null;

        //  String json= JSON.toJSONString(this.postList.get(0).getContent());//对象转化为json

        System.out.println(new Date());
        return this.myBlog.getPostList().get(0).getContent();
    }

    @Override
    public String index() {
        String i = null;
/*        Test t1 = new Test();
        List<Blog> p = t1.getAllBlog("/root/blog");
        String json= JSON.toJSONString(p);//对象转化为json
        String i = p.get(0).getContent();

        //JSONObject.toJSONString(p)//list 转json
        //System.out.println(json.toString());
        System.out.println(i);*/
        String json= JSON.toJSONString(this.myBlog.getPostList().get(0).getContent());//对象转化为json

        System.out.println(new Date()+"index()");

        return this.myBlog.getPostList().get(0).getContent();
    }

    @Override
    public String  getPostById(String urlPath) {
        System.out.println(new Date());
        String i = urlPath;
        for (Post myPost:this.myBlog.getPostList()){
            if (myPost.getFileName().equals(urlPath))
                return myPost.getContent();
        }
        return null;
    }

    @Override
    public String getAllTags(HttpServletRequest request) {
        return null;
    }

    @Override
    public String getAllCategory(HttpServletRequest request) {
        return null;
    }

    @Override
    public String getAllPostBycategory(HttpServletRequest request) {
        return CSerUtils.Object2Json(this.myBlog.getCategoryURLMap().toString());
    }

    @Override
    public String feel(HttpServletRequest request) {
        request.getScheme(); //得到协议名 例如：http
        request.getServerName(); //得到域名 localhost
        request.getServerPort();
        String xie_zhuji_duanou = CSerUtils.getURLHead(request.getScheme());

        List<String> urlList = new ArrayList<>();
        this.myBlog.getPostList().stream().forEach(singlePost ->{
            urlList.add(xie_zhuji_duanou + singlePost.getSiteHalfURL());
        });
        return fortest(this.myBlog.getPostList(),xie_zhuji_duanou);

        //return JSON.toJSONString(urlList);//对象转化为json
    }

    @Override
    public String getAllPostByTags(HttpServletRequest request) {
        //  List<String> blogTagsList =  this.myBlog.getTagsList();
        return "blogTagsList.toString()";
    }

    String fortest(List<Post> postList,String xie_zhuji_duanou){
        List<String> urlListmd = new ArrayList<>();
        StringBuffer sb = new StringBuffer("");
        postList.stream().forEach(post -> {
            sb.append("["+post.getTitle()+"]("+xie_zhuji_duanou+post.getSiteHalfURL()+")<br>");
            urlListmd.add("["+post.getTitle()+"]("+xie_zhuji_duanou+post.getSiteHalfURL()+")\\n");
        });

        return CSerUtils.mdString2Html(sb.toString());
    }
}
