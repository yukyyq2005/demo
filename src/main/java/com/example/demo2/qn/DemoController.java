package com.example.demo2.qn;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @description:
 * @author: Youq
 * @create: 2019-09-20 15:21
 */
@CrossOrigin
@RestController
public class DemoController {
    @GetMapping("/test1")
    public Object test() {
        String string = PropertiesUtil.getValue("server.interface");
        System.out.println(string);
        try {
            String address = "jdbc:sqlserver://192.168.1.230:1433";//连接地址
//            String address = "jdbc:sqlserver://125.91.16.116:1433";//连接地址
            String user = "sa"; //sqlserver用户
            String passwd = "123456"; //密码
            String database = "GuardTour_DB"; //数据库名
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
            Connection con = DriverManager.getConnection(address, user, passwd);
            con.setCatalog(database);
            Statement smt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String selCode = "SELECT * FROM GT_DEPARTMENT";
            ResultSet rs = smt.executeQuery(selCode);
            while(rs.next()){
                String no = rs.getString("id");
                String name = rs.getString("DepartMent_Name");
                //String name1 = rs.getString("FKSJ");
                System.out.println(no+"\t"+name);
            }
            rs.close();
            smt.close();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return "hello world1 8802 youqiang war ddfaldjaldjalj youq hello";
    }
}
