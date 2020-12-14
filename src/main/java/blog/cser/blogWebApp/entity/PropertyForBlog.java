package blog.cser.blogWebApp.entity;

import java.util.List;
import java.util.Properties;

public class PropertyForBlog {
    private Integer total;
    private List<String> urlList;
    private List<String> propertyNameList;

    public PropertyForBlog(Integer total, List<String> urlList, List<String> propertyNameList){
        this.total = total;
        this.urlList = urlList;
        this.propertyNameList = propertyNameList;
    }
}
