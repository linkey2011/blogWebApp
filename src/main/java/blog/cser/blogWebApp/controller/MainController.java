package blog.cser.blogWebApp.controller;

import blog.cser.blogWebApp.annotation.CSerLog;
import blog.cser.blogWebApp.conf.ConstantValue;
import blog.cser.blogWebApp.entity.Blog;
import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.entity.PostDto;
import blog.cser.blogWebApp.entity.util.BaseResponse;
import blog.cser.blogWebApp.mapper.PostMapper;
import blog.cser.blogWebApp.util.CSerUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
//@RestController
@Controller
@RequestMapping("/")
//@CrossOrigin() // 跨域
public class MainController extends BaseController   {

    @Autowired
    CSerUtils cSerUtils;
    private static Blog MYWEBBLOG;
    @Value("${post.path.head}")
    String value1;

    int i = 1;
    //网站初始化
    public void initMyWeb(){
        if (this.MYWEBBLOG == null){
            System.out.println("执行blog初始化");
            String nowRunningPath  = "当前运行文件夹："+ System.getProperty("user.dir");
            System.out.println(nowRunningPath);
            String path = System.getProperty("user.dir") + File.separator + ConstantValue.PostPath;
            System.out.println("读取的文件所在文件夹："+path);
            //  List<Post> p = CSerUtils.initMyBlog(path);
            this.MYWEBBLOG = cSerUtils.initMyBlog(path);
            int o = 1;
            i += 1;
            //  this.postList = p;}
        }
    }

    //首页
    @CSerLog()
    @RequestMapping("")
    //@ResponseBody
    public ModelAndView index(HttpServletRequest request) {
        i += 2;
        System.out.println(i);
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("postList", this.MYWEBBLOG.getPostDtoList());

        return modelAndView;
    }
    @CSerLog
    //@RequestMapping("/blog/{halfURL:(.*?)\\.html}")
    @RequestMapping("/blog/**//{halfURL:.+}")
    public ModelAndView getPostDetail(@PathVariable("halfURL") String halfURL,HttpServletRequest request){
        String halfUrlFromRequest = request.getServletPath();
        Post iWantPost = null;
        for (Post post:this.MYWEBBLOG.getPostList()){
            if (post.getSiteHalfURL().equals(halfUrlFromRequest)){
                iWantPost = post;
                break;
            }
        }
        ModelAndView modelAndView = new ModelAndView("postDetail");
        modelAndView.addObject("iWantPost", iWantPost);
       // System.out.println(iWantPost.toString());
        return modelAndView;
    }


    @CSerLog()
    @RequestMapping("/getAllPosts")
    public BaseResponse getAllPosts(){
        this.initMyWeb();
        return setResultSucess(this.MYWEBBLOG.getPostDtoList());
    }

    @CSerLog
    @RequestMapping("/getBlogByName/{fileName}")
    @CrossOrigin()
    public BaseResponse getBlogByFileName(@PathVariable("fileName") String fileName){
        Post getPost = null;
        for (Post post : this.MYWEBBLOG.getPostList()){
            if (cSerUtils.sunday(post.getFileName(),fileName) != -1){
                getPost = post;
                break;
            }

        }
      return setResultSucess(getPost);
    }


    @CSerLog
    @RequestMapping("/feed.xml")
    public BaseResponse feedXml(){
        return setResultSucess();
    }


    @CSerLog()
    @RequestMapping("/getAllTags")
    public BaseResponse getAllTags(){
        return setResultSucess(this.MYWEBBLOG.getTagsForURLMap());
    }
    @CSerLog()
    @RequestMapping("/getAllCategory")
    public BaseResponse getAllCategory(){
        return setResultSucess(this.MYWEBBLOG.getCategoryURLMap());
    }
    @CSerLog()
    @RequestMapping("/getAllTimeClustering")
    public BaseResponse getAllTimeClustering(){
        return setResultSucess(this.MYWEBBLOG.getYearMonthURLMap());
    }

    @CSerLog()
    @RequestMapping("/search")
    public  ModelAndView search(@RequestParam(value = "keyword") String keyword){
        List<Post> postListForSearch = new ArrayList<>();

        this.MYWEBBLOG.getPostList().stream().forEach(post->{
            if (cSerUtils.sunday(post.getContent(),keyword) != -1){//不等于-1 则匹配成功
                postListForSearch.add(post);
            }
        });


        List<PostDto> postDtoListForSearch = PostMapper.INSTANCE.PostListToPostDtoList(postListForSearch);

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("postList", postDtoListForSearch);

        return modelAndView;
      //  return setResultSucess(postDtoListForSearch);



    }




    //首页
    @CSerLog()
    @RequestMapping("/123")
    //@ResponseBody
    public String index123(HttpServletRequest request) {
        this.initMyWeb();
        i += 2;
        System.out.println("i = "+i);

        request.getScheme(); //得到协议名 例如：http
        request.getServerName(); //得到域名 localhost
        request.getServerPort();
        String xie_zhuji_duanou = cSerUtils.getURLHead(request.getScheme());

        List<String> urlList = new ArrayList<>();
        this.MYWEBBLOG.getPostList().stream().forEach(singlePost ->{
            urlList.add(xie_zhuji_duanou + singlePost.getSiteHalfURL());
        });
        return fortest(this.MYWEBBLOG.getPostList(),xie_zhuji_duanou);
    }

    /**
     *
     * @param halfURL  博客文件夹到thml结尾L
     * @return
     */
    @CSerLog()
    @RequestMapping(value = "getSinglePostByHalfURL",method= RequestMethod.GET)
    public BaseResponse getSinglePostByHalfURL(@RequestParam("halfURl")String  halfURL) {
        Post iWantPost = null;
        int searchTime = 0;
        for (Post post :  this.MYWEBBLOG.getPostList()){
            searchTime +=1;
            if (post.getSiteHalfURL().equals(halfURL))
            iWantPost = post;
            break;
        }
        System.out.println("查找博客" + halfURL+ "次数"+searchTime);
        if (iWantPost != null){
            return setResultSucess(iWantPost);
        }else {
            return go404();
        }
    }

    /**
     * 404
     * @return
     */
    public BaseResponse go404(){
       return setResultSucess("404");
    }

    @RequestMapping(value = "${post.path.head}" + "/"+ "{postId:\\*}.html")
    @ResponseBody
    public String hell0() {

        String i = null;

        //  String json= JSON.toJSONString(this.postList.get(0).getContent());//对象转化为json

        System.out.println(new Date());
        return this.MYWEBBLOG.getPostList().get(0).getContent();
    }

    public String index1() {
        String i = null;
/*        Test t1 = new Test();
        List<Blog> p = t1.getAllBlog("/root/blog");
        String json= JSON.toJSONString(p);//对象转化为json
        String i = p.get(0).getContent();

        //JSONObject.toJSONString(p)//list 转json
        //System.out.println(json.toString());
        System.out.println(i);*/
        String json= JSON.toJSONString(this.MYWEBBLOG.getPostList().get(0).getContent());//对象转化为json

        System.out.println(new Date()+"index()");

        return this.MYWEBBLOG.getPostList().get(0).getContent();
    }

    public String  getPostById(HttpServletRequest request) {
        //System.out.println(new Date()+urlPath);
        System.out.println("1==="+request.getRequestURL());
        System.out.println("2=== "+request.getRequestURI());
        System.out.println("3==="+ request.getContextPath());
        System.out.println("4==="+request.getServletPath());//   有用
        System.out.println("5==="+request.getQueryString());

//        String i = urlPath;
//        for (Post myPost:this.myBlog.getPostList()){
//            if (myPost.getFileName().equals(urlPath))
//                return myPost.getContent();
//        }
        return null;
    }








    public String feel(HttpServletRequest request) {
        request.getScheme(); //得到协议名 例如：http
        request.getServerName(); //得到域名 localhost
        request.getServerPort();
        String xie_zhuji_duanou = cSerUtils.getURLHead(request.getScheme());

        List<String> urlList = new ArrayList<>();
        this.MYWEBBLOG.getPostList().stream().forEach(singlePost ->{
            urlList.add(xie_zhuji_duanou + singlePost.getSiteHalfURL());
        });
        return fortest(this.MYWEBBLOG.getPostList(),xie_zhuji_duanou);

        //return JSON.toJSONString(urlList);//对象转化为json
    }




    String fortest(List<Post> postList,String xie_zhuji_duanou){
        List<String> urlListmd = new ArrayList<>();
        StringBuffer sb = new StringBuffer("");
        postList.stream().forEach(post -> {
            sb.append("["+post.getTitle()+"]("+xie_zhuji_duanou+post.getSiteHalfURL()+")<br>");
            urlListmd.add("["+post.getTitle()+"]("+xie_zhuji_duanou+post.getSiteHalfURL()+")\\n");
        });

        return cSerUtils.mdString2Html(sb.toString());
    }
}
