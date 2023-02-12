import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
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
import java.sql.SQLException;


/**
 * Created by Swamy on 5/9/2015.
 */
public class AddPartnerAccountMapping extends HttpServlet
{
    static Logger logger=new Logger(AddPartnerAccountMapping.class.getName());
    String token="";
    String result="";

    public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
    {
        doPost(req,res);
    }

    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        //AsynchApplicationManager asynchApplicationManager = new AsynchApplicationManager();

        RequestDispatcher rd = req.getRequestDispatcher("/addPartnerAccountMapping.jsp?message=" + result.toString() + "&ctoken="+user.getCSRFToken());

        String accountid=req.getParameter("accountid");
        String partnerid=req.getParameter("partnerid");
        String isActive= req.getParameter("isactive");
        StringBuffer sb=new StringBuffer();
        token = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        try
        {
            if (!ESAPI.validator().isValidInput("pgtypeid",req.getParameter("pgtypeid"),"Description",100,true))
            {
                sb.append("Invalid Gateway,");
            }
            if (!ESAPI.validator().isValidInput("accountid",accountid,"Numbers",10,false))
            {
                sb.append("Invalid AccountID,");
            }
            if (!ESAPI.validator().isValidInput("partnerid",partnerid,"Numbers",10,false))
            {
                sb.append("Invalid PartnerID,");
            }
            if(sb.length()>0)
            {
                req.setAttribute("statusMsg", sb.toString());
                rd.forward(req,res);
                return;
            }

            if (isUniqueMapping(accountid,partnerid))
            {
                result=addPartnerAccountMapping(accountid,partnerid,isActive);
            }
            else
            {
                result="Mapping Already Available.";
            }
        }
        catch (Exception e)
        {
            logger.error("Exception :::::" + e.getStackTrace());
            result = e.toString();
        }
        req.setAttribute("statusMsg",result.toString());
        rd.forward(req,res);
    }
    public boolean isUniqueMapping(String accountId, String partnerId)throws Exception
    {
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=true;
        try
        {
            conn=Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select accountid,partnerid from gateway_account_partner_mapping where accountid=? and partnerid=?");
            pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,accountId);
            pstmt.setString(2,partnerId);
            rs = pstmt.executeQuery();
            if(rs.next())
            {
                status=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return status;
    }
    public String addPartnerAccountMapping(String accountid,String partnerid,String isActive) throws SystemError,SQLException
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        try
        {
            conn=Database.getConnection();
            String query="insert into gateway_account_partner_mapping(accountid,partnerid,isActive) values(?,?,?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,accountid);
            pstmt.setString(2,partnerid);
            pstmt.setString(3,isActive);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                result = "Success : Partner Account Mapping Successfully Added";
            }
            else
            {
                result="Partner Account Mapping Failed.!!!";
            }
        }
        catch(Exception e)
        {
            logger.debug(" Error " +e.getStackTrace ());
            result = "Error : Some error occurred : " + e.getMessage();
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return result;
    }
}
