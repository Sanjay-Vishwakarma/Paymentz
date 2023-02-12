import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * Created by nikita on 4/9/15.
 */
public  class AddBankAgentMapping extends HttpServlet
{
    private static Logger logger=new Logger(AddBankAgentMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request, response);

    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response)throws ServletException,IOException
    {

        HttpSession session=request.getSession();
        User user=(User)session.getAttribute("ESAPIUserSessionKey");
        if(!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/addBankAgentMapping.jsp?&ctoken=" + user.getCSRFToken());

        String pgtypeId=request.getParameter("pgtypeid");
        String agentId=request.getParameter("agentid");
        StringBuffer sb=new StringBuffer();

        if (!ESAPI.validator().isValidInput("pgtypeid",pgtypeId, "Numbers",15,false))
        {
            sb.append("<br>Invalid Gateway Type</br>");
        }
        if (!ESAPI.validator().isValidInput("agentid",agentId, "Numbers", 10, false))
        {
            sb.append("Invalid AgentId");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }
        try
        {
            if(isUniqueMapping(pgtypeId,agentId))
            {
                String  statusMsg=addBankAgentMapping(pgtypeId,agentId);
                request.setAttribute("statusMsg",statusMsg);

            }
            else
            {
                request.setAttribute("statusMsg","Mapping Already Available");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception  ::::" + e.getMessage());
            String result=e.toString();
            request.setAttribute("statusMsg",result);
        }
        rd.forward(request,response);
        return;
    }
    public boolean isUniqueMapping(String pgTypeId,String agentId)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer sb=new StringBuffer();
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            sb.append("select id from gatewaytype_agent_mapping where pgtypeId=? and agentId=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,pgTypeId);
            pstmt.setString(2,agentId);
            rs=pstmt.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return status;
    }

    public String addBankAgentMapping(String pgtypeId, String agentId)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        String statusMsg="";
        try
        {
            conn=Database.getConnection();
            String query="INSERT  INTO gatewaytype_agent_mapping(id,pgtypeId,agentId,creation_date) VALUES(null,?,?,unix_timestamp(now()))";
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,pgtypeId);
            pstmt.setString(2,agentId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                statusMsg="Mapping Successfully Added";
            }
            else
            {
                statusMsg="Mapping Adding Failed";
            }

        }
        catch (Exception e)
        {
            statusMsg=e.getMessage();
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return statusMsg;
    }
}