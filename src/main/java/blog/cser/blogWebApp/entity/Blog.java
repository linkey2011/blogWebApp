package blog.cser.blogWebApp.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Blog {
     //  private List<String> blogKeywords;// 博客的总关键词,与文档无关，从配置文件中获取
       private List<Post> postList;//博客的所有文章
       private List<Post> summaryList;//博客的所有文章
       private HashMap<String,List<String>> tagsForURLMap;
       private HashMap<String,List<String>> categoryURLMap;
       private HashMap<String,List<String>> yearMonthURLMap;


       public Blog() {
             // this.blogKeywords = blogKeywords;
             // this.tagsList = tagsList;
             // this.postList = postList;

              this.categoryURLMap = new HashMap<>();
              this.yearMonthURLMap = new HashMap<>();
       }
}
