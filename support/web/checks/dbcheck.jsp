<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<HTML>
<HEAD>
    <TITLE>DB Check</TITLE>
</HEAD>
<BODY>
<%
    Connection conn = null;


    try
    {
        conn = Database.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement("select count(*) from members");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next())
        {
            out.println("SUCCESS");
        }
        else
        {
            out.println("FAILURE : No Result");
        }


    }
    catch (Exception e)
    {
        out.println("FAILURE : Exception "+e.getMessage());
    }
    finally
    {
        Database.closeConnection(conn);
    }

%>
</BODY>
</HTML>
