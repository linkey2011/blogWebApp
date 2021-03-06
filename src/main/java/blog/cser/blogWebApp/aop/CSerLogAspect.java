package blog.cser.blogWebApp.aop;

import blog.cser.blogWebApp.conf.BlogConf;
import blog.cser.blogWebApp.controller.MainController;
import blog.cser.blogWebApp.util.IPUtil;
import blog.cser.blogWebApp.util.IPUtil2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Aspect
@Component
@Order(1)
public class CSerLogAspect{
    @Autowired
    BlogConf    blogConf;

    @Pointcut("@annotation(blog.cser.blogWebApp.annotation.CSerLog)")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
    }

    @AfterReturning(value = "webLog()", returning = "ret")
    public void doAfterReturning(Object ret) throws Throwable {
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //第 0 步 获取方法名
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        //秒表开始
        StopWatch stopWatch = new StopWatch("统一一组任务耗时");
        stopWatch.start(method.getName());

        //第 1 步 准备工作 init网站
        MainController mainController = (MainController)joinPoint.getTarget();
        mainController.initMyWeb();

        //获取当前请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //记录请求信息
        //执行具体的业务代码
        Object result = joinPoint.proceed();


        //秒表停止
        stopWatch.stop();
        String methodName = request.getRequestURI(); // /api/category/query
        URLDecoder URIDecoder = null;
        methodName = URIDecoder.decode(methodName);
        //获取ip
        String ip1 = IPUtil.getClientIpAddr(request);
        String ip2 = IPUtil2.getIpAddress(request);
        System.out.println(LocalDateTime.now() +"   "+
                methodName +"        耗时---->" +stopWatch.getTotalTimeMillis()+ "ms"
                +"     ip--->"+ip1
                +"     ip2--->"+ip2
                +"参数"+getParameter(method, joinPoint.getArgs())
        );

        if (result instanceof ModelAndView){
            ModelAndView modelAndView = (ModelAndView)result;
            modelAndView.addObject("blogConf", blogConf);
            return modelAndView;
        }
        return result;
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Map<String, Object> map = new HashMap<>();
                String key = parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.size() == 0) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }


}
