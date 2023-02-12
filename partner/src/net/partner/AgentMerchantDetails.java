package net.partner;

import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
import java.util.Hashtable;


/**
 * Created with IntelliJ IDEA.
 * Created by Kanchan on 03-02-2021.
 * Time: 12.50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentMerchantDetails extends HttpServlet
{
    private static Logger log = new Logger(AgentMerchantDetails.class.getName());


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("entering in AgentMerchantDetails....");
        HttpSession session= request.getSession();
        User user= (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner= new PartnerFunctions();

        log.debug("ctoken====="+request.getParameter("ctoken"));
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("partner is logout");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }

        Connection conn= null;
        ResultSet rs= null;
        PreparedStatement ps= null;

        int records=15;
        int pageno=1;
        Functions functions = new Functions();
        Hashtable hash = null;

        String agentId= request.getParameter("agentid");
        log.debug("agentID======="+agentId);
        String agentName= request.getParameter("agentName");

        int start=0; //start index
        int end=0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        log.debug("Page No ======================="+pageno);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {
            conn= Database.getRDBConnection();
            StringBuffer query= new StringBuffer("Select mam.memberid,login, company_name,contact_emails from members AS m INNER JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE");

            StringBuffer countquery = new StringBuffer("SELECT COUNT(*) FROM members AS m INNER JOIN merchant_agent_mapping AS mam ON m.memberid=mam.memberid WHERE");

            if (functions.isValueNull(agentId))
            {
                query.append(" mam.agentId=" + ESAPI.encoder().encodeForSQL(me,agentId));
                countquery.append(" mam.agentId=" + ESAPI.encoder().encodeForSQL(me,agentId));
            }
            query.append(" order by memberid LIMIT " + start + "," + end);

            ps= conn.prepareStatement(query.toString());
            log.debug("Query:: "+ps);
            rs= ps.executeQuery();
            hash= Database.getHashFromResultSetForTransactionEntry(rs);

            ps=conn.prepareStatement(countquery.toString());
            rs=ps.executeQuery();
            rs.next();

            int totalrecords= rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
            {
                hash.put("records", "" + (hash.size() - 2));
            }
            request.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);
            request.setAttribute("agentid",agentId);
            RequestDispatcher rd = request.getRequestDispatcher("/agentMerchantInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (SystemError systemError)
        {
            log.error("system error while performing select query",systemError);
        }
        catch (SQLException e)
        {
           log.error("sql exception",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
}
