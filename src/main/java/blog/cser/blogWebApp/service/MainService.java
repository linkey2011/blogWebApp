package blog.cser.blogWebApp.service;


import blog.cser.blogWebApp.conf.ConstantValue;
import blog.cser.blogWebApp.entity.Post;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/")
public interface MainService {

    @RequestMapping(value = "/test")
    String index();

//    @RequestMapping(value = "/blogtest/{postId:\\*}.html" )
//    String getPostById(@PathVariable("postId")String postId);
//

    @RequestMapping(value = "/blogtest/(\\**).html" )
    String getPostById(HttpServletRequest request);



    @RequestMapping(value ="")//这里本来应该填空"feed.xml"
    String feel(HttpServletRequest request);

    @RequestMapping(value ="/tags")//这里本来应该填空"feed.xml"
    String getAllTags(HttpServletRequest request);

    @RequestMapping(value ="/category")//这里本来应该填空"feed.xml"
    String getAllCategory(HttpServletRequest request);

    @RequestMapping(value ="/tags/1")//这里本来应该填空"feed.xml"
    String getAllPostByTags(HttpServletRequest request);

    @RequestMapping(value ="/category/1")//这里本来应该填空"feed.xml"
    String getAllPostBycategory(HttpServletRequest request);


}
