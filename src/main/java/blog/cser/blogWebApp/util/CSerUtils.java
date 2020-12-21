package blog.cser.blogWebApp.util;

import blog.cser.blogWebApp.conf.ConstantValue;
import blog.cser.blogWebApp.entity.Blog;
import blog.cser.blogWebApp.entity.Post;
import blog.cser.blogWebApp.entity.PostDto;
import blog.cser.blogWebApp.entity.PropertyForBlog;
import blog.cser.blogWebApp.mapper.PostMapper;
import blog.cser.thirdparty.Util.ListUtils;
import com.alibaba.fastjson.JSON;

import javafx.geometry.Pos;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PropertySource({"classpath:application.properties"})
@Component

public class CSerUtils {

    /**
     * 将md 的路径按照时间顺序排序
     * @param mdPath
     * @return
     */
    public  List<String> sortListByYYYYMMDD(List<String> mdPath){
        String pattern="/\\d{8}-";
        Pattern p1 = Pattern.compile(pattern); // 正则表达式

        List<Integer> integerList = new ArrayList<Integer>();
        mdPath.stream().forEach(md->{
            Matcher m = p1.matcher(md); // 操作的字符串
            boolean flag = m.find();
            String o = m.group(0);
            Integer temp = Integer.valueOf(m.group().substring(1,9));
            integerList.add(temp);
        });
        int t = 0;
        Collections.sort(integerList,Collections.reverseOrder());                 //降序排列

        List<String> mdNewPath  = new ArrayList<>();
        //双重for循环待优化
        for (Integer num : integerList) {
            Pattern p2 = Pattern.compile(num.toString());
            for (int i = 0; i < mdPath.size(); i++) {
                t = t + 1;
                Matcher m = p2.matcher(mdPath.get(i)); // 操作的字符串
                boolean flag = m.find();
                if (flag) {
                    mdNewPath.add(mdPath.get(i));
                    mdPath.remove(mdPath.get(i));
                    break;
                }
            }


        }
        System.out.println(t);
        return mdNewPath;
    }
    /**
     * list去重，待优化
     * @param list
     */
    public  void removeDuplicate(List<String> list) {
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
    }
    public  String getURLHead(String protocol){
        String urlHead = null;
        String port = null;
        String host = ConstantValue.domain;
        if (ConstantValue.webEnvironment.equals("production")){
            if (host.equals("https")) port = "443";
            if (host.equals("http"))  port = "80";
        }
        if (ConstantValue.webEnvironment.equals("development")){
            port = ConstantValue.tomcatPort;
        }
        urlHead = protocol +"://" + host +":" + port ;
        //System.out.println(urlHead);
        return urlHead;
    }

    public  Blog initMyBlog(String firstPath) {
        //整个网站所有的数据
        Blog initWebBlog = new Blog();
        List<String> allFilePathList = new ArrayList<>();
        //获取当前文件下所有文件的绝对路径
        getAllFilePath(firstPath,allFilePathList);
        //仅保留以md和markdown结尾的路径
        List<String> mdPathList = checkMdFile(allFilePathList);
        List<String>  mdNewPathList = sortListByYYYYMMDD(mdPathList);
        List<Post> postList = new ArrayList<>();
        mdNewPathList.stream().forEach(mdPath->{
            try {
                //读取单个md文件内容，string 类型返回
             //   System.out.println(mdPath.substring(0,7));
                String fileContent = readFileContent2String(mdPath);
                Post singlePost = getPostObjectFromFileContent(fileContent,mdPath,initWebBlog);
                //草稿状态的不要
                if (!singlePost.getStatus().equals("publish")) return; //retune 相当于continue，结束本单次循环
                postList.add(singlePost);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        });

        int i = 0;
        i++;

/*        String [] sortNameArr = {"top","writeDayDate","title"};
        boolean [] isAscArr = {false,false,true};*/
        //根据属性排序
        String [] sortNameArr = {"top","writeDayDate","title"};
        boolean [] isAscArr = {false,false,true};
        ListUtils.sort(postList,sortNameArr,isAscArr);

        initWebBlog.setPostList(postList);
        List<PostDto> postDtoList = PostMapper.INSTANCE.PostListToPostDtoList(postList);

        initWebBlog.setPostDtoList(postDtoList);
        initWebBlog.setPostDtoList(postDtoList);
        return initWebBlog;

    }



    /**
     *
     * @param date
     * @return  日期格式统一转化为yyyy—MM-dd, 月，天不足两位补0
     */
    public  String dateUtil(String date) {
        String[] myDate = date.split("-");
        String year  = String.format("%02d" ,Long.parseLong(myDate[0]));
        String month = String.format("%02d" ,Long.parseLong(myDate[1]));
        String day   = String.format("%02d" ,Long.parseLong(myDate[2]));
        return (year + "-" + month + "-" + day);
    }


    /**
     *
     * @param pathList
     * @return 仅仅读取所有md和markdown结尾的文件
     */
    public  List<String> checkMdFile(List<String> pathList){
        String pattern1="^(.*?)\\.md$";
        String pattern2="^(.*?)\\.markdown$";

        List<String> mdList = new ArrayList<>();


        pathList.stream().forEach(p->{
            boolean isMatch1= Pattern.matches(pattern1, p);
            boolean isMatch2= Pattern.matches(pattern2, p);
            if ( (isMatch1 || isMatch2) ){
                mdList.add(p);
            }
        });

        return mdList;
    }


    /**
     *
     * @param firstPath 需要遍历的文件夹路径
     * @param allFilepathList 返回该文件夹下所有文件的绝对路径
     * @return
     */
    public  List<String> getAllFilePath(String firstPath,List<String> allFilepathList) {
        // get file list where the path has
        File file = new File(firstPath);
        // get the folder list
        File[] array = file.listFiles();

        for(int i=0;i<array.length;i++){
            if(array[i].isFile()){
                // only take file name
                //  System.out.println("文件名=======" + array[i].getName());
                // take file path and name
                //  System.out.println("文件=========" + array[i]);
                // take file path and name
                //  System.out.println("文件绝对路径=====" + array[i].getPath());
                //  System.out.println("文件相对路径=====" + array[i].getCanonicalPath());
                firstPath = array[i].getAbsolutePath();
                String  p = file.separator;
                firstPath = firstPath.replaceAll("\\\\","/");
               // System.out.println(firstPath);
                allFilepathList.add(firstPath);
                //  System.out.println("=====================");
            }else if(array[i].isDirectory()){
                getAllFilePath(array[i].getPath(), allFilepathList);
            }
        }
        //System.out.println(pathList.toString());
        return allFilepathList;
    }


    public  List<String> getTagsList(String tags){
        String pattern = "(,|\\s|，|。|·|\\*|；|’|‘|、|;|'|\\.|\\/)";
        String[] myTags = tags.split(pattern);
        List<String> tagsList = new ArrayList<>();
        for (String singleTag:myTags) {
            tagsList.add(singleTag);
        }
        return  tagsList;
    }



    /**
     *
     * @param filePath  文件路径
     * @return 以为字符串的形式返回文件内容
     * @throws IOException
     */
    public  String  readFileContent2String(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        byte[] data = Files.readAllBytes(path);
        String result = new String(data, "utf-8");
        return result;

    }

    /**
     *
     * @param mdPath
     * @return 根据文件路径生成对应的url
     */
    public String setPostUrl(String mdPath){
        String patternPath = "\\/" + ConstantValue.PostPath +"\\/(.*)(\\.md|\\.markdown)";//头信息细节正则
        Pattern p = Pattern.compile(patternPath); // 正则表达式
        Matcher m = p.matcher(mdPath); // 操作的字符串

        Pattern p1 = Pattern.compile(patternPath); // 正则表达式
        Matcher m1 = p1.matcher(mdPath); // 操作的字符串
        boolean flag = m1.find();
        String iWantMdPath = null;
        String iWantUrl = null;
        if(flag){
            iWantMdPath = m1.group(0);
            iWantUrl = iWantMdPath.replaceAll("(\\.md|\\.markdown)",ConstantValue.webSuffix);
        }

        return iWantUrl; //        /blogtest/algorithm-of-rsa.html
    }

    /**
     * 将file文件的内容解析，封装成Post 对象
     * @param fileContent
     * @return
     */
    public  Post getPostObjectFromFileContent(String fileContent,String mdPath,Blog initWebBlog) throws ParseException {
        //准备工作
        Post singlePost = new Post();
        String patternHead1 = "<!--([\\s\\S]*)-->";//头信息正则
        String pattern2 = "\\s*(author|head|date|lock|title|top|summary|images|tags|category|status)\\s*:(.*?)";//头信息细节正则
        String patternPath = "\\/" + ConstantValue.PostPath +"\\/(.*)(\\.md|\\.markdown)";//头信息细节正则
        //设置博文内容
        singlePost.setContent(mdString2Html(fileContent));
        {//这一部分主要设置 post的系统信息
            String postUrl = setPostUrl(mdPath);
            String fileName = postUrl.substring(ConstantValue.PostPath.length()+2,postUrl.length() - ConstantValue.webSuffix.length());
            singlePost.setFileName(fileName);
            singlePost.setPostId(fileName.substring(0,ConstantValue.webSuffix.length()).hashCode());
            singlePost.setSiteHalfURL(postUrl);
            //System.out.println(url);
        }
        // 根据正则 获取md文件头信息
        Pattern p = Pattern.compile(patternHead1); // 正则表达式
        Matcher m = p.matcher(fileContent); // 操作的字符串
        boolean flag = m.find();
        String headMSG = null;//保存头信息
        //获取了头信息
        if (flag) {
            headMSG = m.group(0);
            //System.out.println(headMSG);
        }
        String[] postHeadInfo = null;
        if(StringUtils.isNotBlank(headMSG)){
            String headMSG1 =  headMSG.trim();
            postHeadInfo = headMSG.split("\n");//将头信息分组
            for (String temp :postHeadInfo) {
                String info = temp.trim();
                Pattern pattern = Pattern.compile(pattern2); // 正则表达式
                Matcher matcher = pattern.matcher(info); // 操作的字符串
                boolean flag1 = matcher.find();
                if (flag1) {
                    String infoHead = matcher.group();
                    String infoValue1 = info.replaceAll(pattern2, "");
                    String infoValue  = infoValue1.trim();
                    int i = 0;
                    switch (infoHead){
                        case "lock:":
                            if (StringUtils.isNotBlank(infoValue)){
                                singlePost.setLock(infoValue);
                                String postContent = singlePost.getContent();
                                singlePost.setContent(String.valueOf(postContent.hashCode()));
                            }

                            break;
                        case "top:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setTop(Integer.valueOf(infoValue));
                            else
                                singlePost.setTop(0);
                            break;
                        case "author:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setAuthor(infoValue);
                            break;
                        case "head:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setHead(infoValue);
                            break;
                        case "date:" :
                            if (StringUtils.isNotBlank(infoValue)){
                                String yyyyMMdd = dateUtil(infoValue);
                                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                                Date writeDay = formater.parse(yyyyMMdd);
                                singlePost.setWriteDayStr(yyyyMMdd);
                                singlePost.setWriteDayDate(writeDay);

                                String yyyyMM = yyyyMMdd.substring(0,7);
                                //获取时间分类map中键为 yyyyMM 对应的list
                                List<String> yearMonthUrlList = initWebBlog.getYearMonthURLMap().get(yyyyMM);
                                //如果list 为空，生成一个新的list 并且添加对用的url
                                if (yearMonthUrlList == null){
                                    yearMonthUrlList = new ArrayList<>();
                                    yearMonthUrlList.add(singlePost.getSiteHalfURL());
                                } // 不为空，直接添加
                                else if (yearMonthUrlList != null) {
                                    yearMonthUrlList.add(singlePost.getSiteHalfURL());
                                }
                                // 刷新该map下的 <k,v>
                                initWebBlog.getYearMonthURLMap().put(yyyyMM,yearMonthUrlList);
                            }
                            break;
                        case "title:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setTitle(infoValue);
                            break;
                        case "tags:" :
                            if (StringUtils.isNotBlank(infoValue)){
                                //对单个博客设置对应的标签
                                singlePost.setTagsStr(infoValue);
                                singlePost.setTagsList(getTagsList(infoValue));
                                //list 转化为 set
                                List<String> tagsList = singlePost.getTagsList();
                                //如果对应的tagMap 则初始化
                                if(initWebBlog.getTagsForURLMap() == null){
                                    HashMap<String, List<String>>  tempMap = new HashMap<>();
                                    tagsList.stream().forEach(tag->{
                                        List<String> tempList = new ArrayList<>();
                                        tempList.add(singlePost.getSiteHalfURL());
                                        tempMap.put(infoValue,tempList);
                                    });
                                    initWebBlog.setTagsForURLMap(tempMap);
                                }//如果对应tagMap不为空
                                else if (initWebBlog.getTagsForURLMap() != null) {
                                    //获取对应tagsMap
                                    HashMap<String, List<String>>  tempMap = initWebBlog.getTagsForURLMap();
                                    //对所有标签遍历
                                    tagsList.stream().forEach(tag->{
                                        //获取该标签的对应的url 的list
                                        List<String>  tempTagsListFromMap =  tempMap.get(tag);
                                        //没有说明这是一个新标签
                                        if (tempTagsListFromMap == null){
                                            //生成新的list
                                            List<String> tempTagsList = new ArrayList<>();
                                            tempTagsList.add(singlePost.getSiteHalfURL());
                                            //刷新map
                                            tempMap.put(tag,tempTagsList);
                                        } //如果
                                        else if (tempTagsListFromMap != null) {
                                            //对该list 新增url
                                            tempTagsListFromMap.add(singlePost.getSiteHalfURL());
                                            //刷新map
                                            tempMap.put(tag,tempTagsListFromMap);
                                        }
                                    });
                                    initWebBlog.setTagsForURLMap(tempMap);
                                }

                   /*             if(initWebBlog.getTagsList() != null){
                                    tempSet.addAll(initWebBlog.getTagsList());
                                    List<String> list = new ArrayList<>(tempSet);
                                    initWebBlog.setTagsList(list);
                                }
                                if(initWebBlog.getTagsList() == null){
                                    initWebBlog.setTagsList(singlePost.getTagsList());
                                }*/
                            }
                            break;
                        case "images:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setImagesURL(infoValue);
                            break;
                        case "category:" ://分类
                            //逻辑忘了哈哈哈哈
                            if (StringUtils.isNotBlank(infoValue)){
                                //获取分类map中键为 infovalue 对应的list
                                List<String> urlList = initWebBlog.getCategoryURLMap().get(infoValue);
                                //如果list 为空，生成一个新的list 并且添加对用的url
                                if (urlList == null){
                                    urlList = new ArrayList<>();
                                    urlList.add(singlePost.getSiteHalfURL());
                                } // 不为空，直接添加
                                else if (urlList != null) {
                                    urlList.add(singlePost.getSiteHalfURL());
                                }
                                // 刷新该map下的 <k,v>
                                initWebBlog.getCategoryURLMap().put(infoValue,urlList);
                                //对本单个博文分类赋值
                                singlePost.setCategory(infoValue);
/*                                //获取
                                if (initWebBlog.getCategoryList() == null){
                                    initWebBlog.getCategoryList().add(infoValue);
                                }else if(initWebBlog.getCategoryList() != null &&!initWebBlog.getCategoryList().contains(infoValue)){
                                    initWebBlog.getCategoryList().add(infoValue);
                                }*/
                            }
                            break;
                        case "status:" ://暂定0/1
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setStatus(infoValue);
                            break;
                        case "summary:" :
                            if (StringUtils.isNotBlank(infoValue))
                                singlePost.setSummary(mdString2Html(infoValue));
                            break;
                    }
                }
            }
        }
        {// 分类和标签整合到关键词
            Set<String> tempSet = new HashSet<>(singlePost.getTagsList());
            tempSet.add(singlePost.getCategory());
            List<String> postKeywordsList = new ArrayList<>(tempSet);
            singlePost.setPostKeywords(postKeywordsList);
        }
        String json= JSON.toJSONString(singlePost);//关键
        // System.out.println(json);
        autoCheckBlogProps(singlePost);
        //singlePost.setPostId("");
       // PostDto singlePostDto = PostMapper.INSTANCE.PostToPostDto(singlePost);
        return singlePost;
    }

    /**
     * 对没有填写的属性自动填写
     * @param singPost
     */
    void autoCheckBlogProps(Post singPost){
        if (singPost.getTop() == null) {
        singPost.setTop(0);
        }
    }
    /**
     *
     * @param sourceString 入参 String 类型的md文件内容
     * @return 解析成html格式的 string
     */
    public  String mdString2Html(String sourceString){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(sourceString);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        return html;
    }


    public  String Object2Json(Object T){

        return JSON.toJSONString(T);
    }

}
