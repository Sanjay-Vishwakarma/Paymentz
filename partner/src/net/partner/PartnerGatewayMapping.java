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
public class PartnerGatewayMapping extends HttpServlet
{
    Logger logger=new Logger(PartnerGatewayMapping.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in PartnerGatewayMapping");
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
        String partnerid="";
        partnerid=partnerFunctions.getSubpartner((String) session.getAttribute("merchantid"));
        ServletContext application = getServletContext();
        String pgTypeId=request.getParameter("pgtypeid");
        String partnerId=request.getParameter("partnerId");
        String action=request.getParameter("action");
        String dailyCardLimit=request.getParameter("daily_card_limit");
        String weeklyCardLimit=request.getParameter("weekly_card_limit");
        String monthlyCardLimit=request.getParameter("monthly_card_limit");
        String statusMsg="";
        int counter = 1;
        int records=15;
        int pageno=1;
        HashMap hash = null;
        RequestDispatcher rd = request.getRequestDispatcher("/partnerGatewayMapping.jsp?ctoken="+user.getCSRFToken());
        logger.error("Action:::::" + action);

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

        if(dailyCardLimit==null || dailyCardLimit.equals(""))
        {
            dailyCardLimit="1";
        }
        if(weeklyCardLimit==null || weeklyCardLimit.equals(""))
        {
            weeklyCardLimit="3";
        }
        if(monthlyCardLimit==null || monthlyCardLimit.equals(""))
        {
            monthlyCardLimit="5";
        }

        if(functions.isValueNull(action) )
        {
            if(action.equalsIgnoreCase("Save"))
            {
                if (!ESAPI.validator().isValidInput("pgtypeid", pgTypeId, "Numbers", 10, false))
                {
                    sb.append("Invalid GatewayType<BR>");
                }
                if (!ESAPI.validator().isValidInput("partnerid", partnerId, "Numbers", 10, false))
                {
                    sb.append("Invalid PartnerID<BR>");
                }
                if (!ESAPI.validator().isValidInput("daily_card_limit",dailyCardLimit, "Numbers", 5, true))
                {
                    sb.append("Invalid Daily Card Limit<BR>");
                }
                if (!ESAPI.validator().isValidInput("weekly_card_limit",weeklyCardLimit, "Numbers", 5, true))
                {
                    sb.append("Invalid Weekly Card Limit<BR>");
                }
                if (!ESAPI.validator().isValidInput("monthly_card_limit",monthlyCardLimit, "Numbers", 5, true))
                {
                    sb.append("Invalid Monthly Card Limit<BR>");
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
                    if (isMappingUnique(pgTypeId, partnerId))
                    {
                        statusMsg=addGatewayTypePartnerMapping(pgTypeId, partnerId, dailyCardLimit, weeklyCardLimit, monthlyCardLimit);
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
                else if (action.equalsIgnoreCase("Delete"))
                {
                    boolean status = isMappingDelete(pgTypeId, partnerId);
                    if (status)
                    {
                        request.setAttribute("statusMsg", "Mapping Deleted Successfully");
                        rd.forward(request, response);
                        return;
                    }
                    else
                    {
                        request.setAttribute("statusMsg", "Mapping Deleted Failed");
                        rd.forward(request, response);
                        return;
                    }
                }
            }
            else {
                PreparedStatement pstmt=null;
                PreparedStatement pstmt1=null;
                ResultSet rs=null;
                conn = Database.getRDBConnection();
                StringBuilder query = new StringBuilder("SELECT gtype.pgtypeid,gtype.gateway,gtype.name,gtype.currency,gtpm.partnerid FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid IN("+partnerid+")");
                StringBuilder countquery = new StringBuilder("SELECT count(*) FROM gateway_type AS gtype JOIN gatewaytype_partner_mapping AS gtpm ON gtype.pgtypeid=gtpm.pgtypeid AND gtpm.partnerid IN("+partnerid+")");
                if (functions.isValueNull(partnerId))
                {
                    query.append(" AND gtpm.partnerid= ?");
                    countquery.append(" AND gtpm.partnerid= ?");
                }
                if (functions.isValueNull(pgTypeId))
                {
                    query.append(" and gtype.pgtypeid = ?");
                    countquery.append(" and gtype.pgtypeid = ? ");
                }
                query.append(" order by gtype.pgtypeid desc");
                query.append(" LIMIT ? , ? ");

                pstmt = conn.prepareStatement(query.toString());
                pstmt1 = conn.prepareStatement(countquery.toString());

                if (functions.isValueNull(partnerId))
                {
                    pstmt.setString(counter, partnerId);
                    pstmt1.setString(counter, partnerId);
                    counter++;
                }
                if (functions.isValueNull(pgTypeId))
                {
                    pstmt.setString(counter, pgTypeId);
                    pstmt1.setString(counter, pgTypeId);
                    counter++;
                }
                pstmt.setInt(counter, start);
                counter++;
                pstmt.setInt(counter, end);
                counter++;

                logger.debug("query:::::"+pstmt);
                logger.debug("countquery:::::"+pstmt1);

                //hash = Database.getHashFromResultSetForTransactionEntry(rs1);
                hash = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());

                rs = pstmt1.executeQuery();
                rs.next();
                int totalrecords = rs.getInt(1);

                hash.put("totalrecords", "" + totalrecords);
                hash.put("records", "0");

                if (totalrecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }
                request.setAttribute("transdetails", hash);
                rd.forward(request,response);
                logger.debug("forward to jsp"+hash);
                return;
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
    public boolean isMappingUnique(String pgTypeId,String partnerId)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=true;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("select id from gatewaytype_partner_mapping where pgtypeid=? and partnerid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,pgTypeId);
            pstmt.setString(2,partnerId);
            rs=pstmt.executeQuery();
            logger.error("Query::::"+pstmt);
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
            logger.error("Insert Query::::"+pstmt);
            if(k>0)
            {
                statusMsg="Mapping Added Successfully";
            }
            else
            {
                statusMsg="Mapping Adding Failed";
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

    public boolean isMappingDelete(String pgTypeId,String partnerId)throws Exception
    {
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        boolean status=false;
        try
        {
            con= Database.getRDBConnection();
            StringBuffer sb=new StringBuffer("Delete from gatewaytype_partner_mapping where pgtypeid=? and partnerid=?");
            pstmt=con.prepareStatement(sb.toString());
            pstmt.setString(1,pgTypeId);
            pstmt.setString(2,partnerId);
            int k=pstmt.executeUpdate();
            logger.error("Query::::"+pstmt);
            logger.error("K::::"+k);
            if(k>0)
            {
                status=true;
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
}
