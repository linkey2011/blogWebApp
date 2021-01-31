package blog.cser.blogWebApp.util;


import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Kangbin on 2017/9/12.
 */
public class IPUtil {

    /**
     * 获取浏览器所在用户端的ip地址
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                System.out.println("ipAddress----------"  + ipAddress);
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }

//        if (ipAddress != null && (ipAddress.indexOf("172.") >= 0 || ipAddress.indexOf("192.") >= 0)) { //如果是内网ip， 则说明在同一局域网， 直接取系统外网ip
//            ipAddress = "183.131.11.57"; //如果是内网，写死为外网值
       // ipAddress = getPublicIP();
//        }
        return ipAddress;
    }


}