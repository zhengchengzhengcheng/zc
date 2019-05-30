package com.cnzhcf.paas.zuul.config.dynamicrouter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZuulRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {

    public final static Logger logger = LoggerFactory.getLogger(ZuulRouteLocator.class);

    private ServerProperties server;

    private ZuulProperties properties;

    private String ip;

    public ZuulRouteLocator(String servletPath, ZuulProperties properties, String ip) {
        super(servletPath, properties);
        this.properties = properties;
        this.ip = ip;
        logger.info("servletPath:{}", servletPath);
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<String, ZuulRoute>();
        //从application.properties中加载路由信息
        routesMap.putAll(super.locateRoutes());
        for (Map.Entry<String, ZuulRoute> entry : super.locateRoutes().entrySet()) {
            System.out.println("#########   " + entry.getKey() + "##############     "  + entry.getValue());
        }
        //从db中加载路由信息
        List<ZuulRouteVO> dbRouter = locateRoutesFromDB();
        //优化一下配置
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            ZuulRoute route = entry.getValue();
            dbRouter.stream().forEach(temp ->{
                if(path.equals(temp.getPath())){
                    route.setServiceId(null);
                    route.setUrl(temp.getUrl());
                }
            });
            // Prepend with slash if not already present.
            values.put(path, route);
        }
        return values;
    }

    private  List<ZuulRouteVO> locateRoutesFromDB(){
        //声明Connection对象
        Connection con;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://192.168.50.250:3306/paas-user-dev?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "123456";
        //遍历查询结果集
        List<ZuulRouteVO> routers = new ArrayList<>();
        try {
            //加载驱动程序
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("Succeeded connecting to the Database!");
            }
            //2.创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句
            String sql = " select * from sys_zull_router_debug where ip = '" + ip + "'" ;
            //3.ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);
            logger.info("-----------------");
            logger.info("执行结果如下所示:");
            logger.info("-----------------");
            logger.info("path" + "\t" + "url"+ "\t" + "name") ;
            logger.info("-----------------");
            while (rs.next()) {
                ZuulRouteVO temp = new ZuulRouteVO();
                temp.setPath(rs.getString("path"));
                temp.setUrl(rs.getString("url"));
                routers.add(temp);
                //输出结果
                System.out.println(temp.getPath() + "\t" + temp.getUrl() + "\t" + rs.getString("name"));
            }
            rs.close();
            con.close();
        } catch (ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");
            e.printStackTrace();
        } catch (SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            return routers;
        }
    }

    public  class ZuulRouteVO {

        private String path;

        private String url;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
