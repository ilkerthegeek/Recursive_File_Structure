<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Search path</title>
</head>
<body>
<center>
    <table border="1">
        <tr>
            <th>name</th>

        </tr>
     <%
         try{
                //Database Connection
             String jdbcURL="jdbc:mysql://localhost:3386/recursivedb";
             String username="root";
             String password="password";
             Connection con= DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/recursivedb","root","Ilker.ozturk.1907");
             Connection con_child=DriverManager.getConnection(
                     "jdbc:mysql://localhost:3306/recursivedb","root","Ilker.ozturk.1907");
             Statement st=con.createStatement();
             String user = request.getParameter("user");
             String sqlStr="select url from trees WHERE url like'%"+user+"%'";
             ResultSet rs = st.executeQuery(sqlStr);
             while(rs.next()){
                 %>
                <tr>
                  <td> <%=rs.getString("url")%></td>
                </tr>
           <%  }

             // while loop completed here
         }
         catch (Exception e){
             System.out.println(e.getMessage());
         }
     %>

    </table>

</center>

<h1><%= "Hello World!" %>
</h1>
<br/>
<a href="hello-servlet">Hello Servlet</a>
</body>
</html>