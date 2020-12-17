package blog.cser.blogWebApp.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Post implements java.io.Serializable {
    private static final long serialVersionUID = -3529051445403971732L;

    //以下属性来自md文件
    private String author;   //作者
    private String head;     //头图
    private Date   writeDayDate;      //日期
    private String writeDayStr;      //日期字符串
    private String title;         //标题
    private String summary;   //摘要
    private List<String> postKeywords;  //关键词
    private Integer top;      //是否置顶
    private String imagesURL;   //头图
    private String tagsStr;      //标签  将md 中的【a,b,c 】以字符串的形式存储
    private List<String> tagsList;// 标签  将上述的【a,b,c 】以list形式存储
    private String category; //分类
    private String status;   //是否发布
    private String content;  //文章内容
    private String lock;// 锁


    //以下属性来自 程序生成
    private Integer postId;
    private String fileName;
    private String serverPath;
    private String sitePath;
    private String mtime;
    private String ctime;
    private String siteHalfURL;



}
