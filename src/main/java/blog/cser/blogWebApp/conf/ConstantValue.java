package blog.cser.blogWebApp.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.sql.Connection;

@Component
public class ConstantValue {
    public static String PostPath;
    public static String env;
    public static String webSuffix;
    public static String domain;
    public static String webEnvironment;
    public static String tomcatPort;

    @Autowired
    private ConstantValue(@Value("${post.path.head}")  String value1,
                          @Value("${web.suffix}")  String value2,
                          @Value("${web.domain}")  String value3,
                          @Value("${web.environment}")  String value4,
                          @Value("${server.port}")  String value5
    ) {
        ConstantValue.PostPath = value1;
        ConstantValue.webSuffix = value2;
        ConstantValue.domain = value3;
        ConstantValue.webEnvironment = value4;
        ConstantValue.tomcatPort = value5;
        int p = 1;
    }

}
