package net.partner;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by Mahima on 1/6/2020.
 */
public class MerchantMapping extends HttpServlet
{
    Logger logger=new Logger(MerchantMapping.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in MerchantMapping");
        HttpSession session = request.getSession();
        Functions functions=new Functions();
        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        StringBuffer sb=new StringBuffer();
        ServletContext application = getServletContext();
        PreparedStatement pstmt=null;
        PreparedStatement pstmt1=null;
        ResultSet rs=null;
        String agentId=request.getParameter("agentid");
        String memberId=request.getParameter("memberid");
        String action=request.getParameter("action");
        String partnerId=request.getParameter("pid");
        String partnerid1=request.getParameter("partnerid");
        String statusMsg="";
        int counter = 1;
        int records=15;
        int pageno=1;
        HashMap hash = null;
        RequestDispatcher rd = request.getRequestDispatcher("/merchantMapping.jsp?ctoken="+user.getCSRFToken());

        int start = 0; // start index
        int end = 0; // end index
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno", request.getParameter("SPageno"), "Numbers", 3, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", request.getParameter("SRecords"), "Numbers", 3, true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch (ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno =1;
            records = 10;
        }
        start = (pageno - 1) * records;
        end = records;

        if(functions.isValueNull(action) )
        {
            if(action.equalsIgnoreCase("Save"))
            {
                if(!functions.isValueNull(agentId)){
                    sb.append("Invalid Agent Id<BR>");
                }
                if(!functions.isValueNull(partnerId)){
                    sb.append("Invalid Partner Id<BR>");
                }
               /* if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 10, false))
                {
                    sb.append("Please provide Account Id<BR>");
                }*/
                if(!functions.isValueNull(memberId)){
                    sb.append("Invalid Member Id<BR>");
                }


                if(functions.isValueNull(partnerId) && !partner.isPartnerSuperpartnerMapped(partnerId, partnerid1)){

                    sb.append("Invalid PartnerID or Invalid Partner Mapping<BR>");
                }
                if(functions.isValueNull(memberId)&& !partner.isPartnerSuperpartnerMembersMapped(memberId,partnerId))
                {
                    sb.append("Invalid Member partner Mapping<BR>");
                }
                if(functions.isValueNull(agentId)&& !partner.isAgentpartnerMapped(agentId,partnerId))
                {
                    sb.append("Invalid AgentID or Invalid Agent Mapping<BR>");
                }
                if (sb.length() > 0)
                {
                    request.setAttribute("statusMsg", sb.toString());
                    rd.forward(request, response);
                    return;
                }
            }
        }
        Connection conn=null;
        try
        {
            if (functions.isValueNull(action))
            {
                if (action.equalsIgnoreCase("save"))
                {
                    if (isMappingUnique(partnerId, agentId,memberId))
                    {
                        statusMsg=addGatewayTypePartnerMapping(agentId, memberId,partnerId);
                        request.setAttribute("statusMsg", statusMsg);
                        rd.forward(request, response);
                        return;
                    }
                    else
                    {
                        request.setAttribute("statusMsg", "Mapping Already Available");
                        rd.forward(request, response);
                        return;
                    }
                }


                pstmt.setInt(counter, start);
                counter++;
                pstmt.setInt(counter, end);
                counter++;

                logger.debug("query:::::"+pstmt);
                logger.debug("countquery:::::"+pstmt1);


            }
        }
        catch (Exception e){
            logger.error("Exception---" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
    }
    public boolean isMappingUnique(String partnerId,String agentId,String memberId)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean statusMsg=true;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select mappingid from merchant_agent_mapping where agentid=? and memberid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,agentId);
            pstmt.setString(2,memberId);
            rs=pstmt.executeQuery();
            logger.error("Query::::"+pstmt);
            if(rs.next())
            {
                statusMsg=false;
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return statusMsg;
    }

    public String addGatewayTypePartnerMapping(String agentId,String memberId,String partnerId)
    {
        Connection conn=null;
        PreparedStatement pstmt=null;
        String statusMsg="";
        try
        {
            conn= Database.getConnection();
            String query="insert into merchant_agent_mapping(memberid,agentid,creationtime)values (?,?,unix_timestamp(now()))";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,memberId);
            pstmt.setString(2,agentId);
            int k=pstmt.executeUpdate();
            if(k>0)
            {
                statusMsg ="New Merchant Agent Mapping Added Successfully.";
            }
            else
            {
                statusMsg="New Merchant Agent Mapping Added Failed.";
            }
            logger.error("statusMsg:::"+statusMsg);
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