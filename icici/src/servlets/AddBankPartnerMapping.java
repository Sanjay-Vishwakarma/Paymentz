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
 * Created by IntelliJ IDEA.
 * User: supriya
 * Date: 9/04/15
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class AddBankPartnerMapping extends HttpServlet
{
    private static Logger logger = new Logger(AddBankPartnerMapping.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }

    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = request.getRequestDispatcher("/addBankPartnerMapping.jsp?ctoken="+user.getCSRFToken());

        String pgtypeid =request.getParameter("pgtypeid");
        String partnerid =request.getParameter("partnerid");
        String daily_card_limit=request.getParameter("daily_card_limit");
        String weekly_card_limit=request.getParameter("weekly_card_limit");
        String monthly_card_limit=request.getParameter("monthly_card_limit");
        StringBuffer sb=new StringBuffer();
        if(daily_card_limit==null || daily_card_limit.equals(""))
        {
            daily_card_limit="1";
        }
        if(weekly_card_limit==null || weekly_card_limit.equals(""))
        {
            weekly_card_limit="3";
        }
        if(monthly_card_limit==null || monthly_card_limit.equals(""))
        {
            monthly_card_limit="5";
        }
        //AsynchApplicationManager asynchApplicationManager = new AsynchApplicationManager();

        if (!ESAPI.validator().isValidInput("pgtypeid", pgtypeid, "Numbers",10,false))
        {
            sb.append("Invalid GatewayType<BR>");
        }
        if (!ESAPI.validator().isValidInput("partnerid",partnerid, "Numbers", 10, false))
        {
            sb.append("Invalid PartnerID<BR>");
        }
        if (!ESAPI.validator().isValidInput("daily_card_limit",daily_card_limit, "Numbers", 5, true))
        {
            sb.append("Invalid Daily Card Limit<BR>");
        }
        if (!ESAPI.validator().isValidInput("weekly_card_limit",weekly_card_limit, "Numbers", 5, true))
        {
            sb.append("Invalid Weekly Card Limit<BR>");
        }
        if (!ESAPI.validator().isValidInput("monthly_card_limit",monthly_card_limit, "Numbers", 5, true))
        {
            sb.append("Invalid Monthly Card Limit<BR>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        try
        {
            if(isMappingUnique(pgtypeid,partnerid))
            {
                String statusMsg=addGatewayTypePartnerMapping(pgtypeid,partnerid,daily_card_limit,weekly_card_limit,monthly_card_limit);

                request.setAttribute("statusMsg",statusMsg);
            }
            else
            {
                request.setAttribute("statusMsg","Mapping Already Available");
            }
        }
        catch (Exception e)
        {
            logger.error("Exception :::::" + e.getStackTrace());
            String result = e.toString();
            request.setAttribute("statusMsg",result);
        }
        rd.forward(request,response);
        return;

    }

    public boolean isMappingUnique(String pgTypeId,String partnerId)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=true;
        try
        {
            con=Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select id from gatewaytype_partner_mapping where pgtypeid=? and partnerid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,pgTypeId);
            pstmt.setString(2,partnerId);
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

    public String addGatewayTypePartnerMapping(String pgTypeId,String partnerId,String daily_card_limit,String weekly_card_limit,String monthly_card_limit)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        String statusMsg="";
        try
        {
            conn= Database.getConnection();
            String query="insert into gatewaytype_partner_mapping(id,pgtypeid,partnerid,dailyCardLimit,weeklyCardLimit,monthlyCardLimit,creation_date) values(null,?,?,?,?,?,unix_timestamp(now()))";
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,pgTypeId);
            pstmt.setString(2,partnerId);
            pstmt.setString(3,daily_card_limit);
            pstmt.setString(4,weekly_card_limit);
            pstmt.setString(5,monthly_card_limit);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                statusMsg="Mapping Added Successfully";
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

