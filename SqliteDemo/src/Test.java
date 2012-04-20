import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Test {    
    public static void main(String[] args) throws Exception {    
//      System.setProperty("java.library.path", ".");    
            
        Class.forName("org.sqlite.JDBC");    
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+System.getProperty("user.dir")+"/test3.db");    
        //建立事务机制,禁止自动提交，设置回滚点    
        conn.setAutoCommit(false);    
            
        Statement stat = conn.createStatement();    
        //stat.executeUpdate("create table people (name, occupation);");    
        stat.executeUpdate("insert into people values ('Gandhi', 'politics');");    
        stat.executeUpdate("insert into people values ('Turing', 'computers');");    
        stat.executeUpdate("insert into people values ('Wittgenstein', 'smartypants');");    
        conn.commit();    
            
        ResultSet rs = stat.executeQuery("select * from people;");    
        while (rs.next()) {    
            System.out.println("name = " + rs.getString("name"));    
            System.out.println("occupation = " + rs.getString("occupation"));    
        }    
           
        rs.close();    
        conn.close();    
    }    
}   

