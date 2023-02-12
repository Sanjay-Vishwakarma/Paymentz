<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<% try
{

    Connection[] conn = new Connection[8];
    for (int i = 0, connLength = conn.length; i < connLength; i++)
    {

        conn[i] = Database.getPooledConnection();

        ResultSet rs = conn[i].createStatement().executeQuery("select * from members limit 5");
        while (rs.next())
        {
            out.print(rs.getInt(1) + "<br>");
        }


    }

    for (Connection aConn : conn)
    {
        out.println(aConn);
    }

    Thread.sleep(10000);
    for (Connection aConn : conn)
    {
        out.println(aConn);
        aConn.close();
    }


}
catch (Exception e)
{
    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
}

%>