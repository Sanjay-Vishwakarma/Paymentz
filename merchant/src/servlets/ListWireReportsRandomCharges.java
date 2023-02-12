import com.directi.pg.*;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/3/1015
 * Time: 4:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListWireReportsRandomCharges extends HttpServlet
{
    Logger logger=new Logger(ListWireReportsRandomCharges.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        logger.debug("Entering into ListWireReportsRandomCharges");
        Merchants merchants = new Merchants();
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        Connection conn = null;
        int records=15;
        int pageno=1;

        String memberId=request.getParameter("memberid");
        String terminalId=request.getParameter("terminalid");
        String bankwireId=request.getParameter("bankwireid");
        String copyiframe=(String)session.getAttribute("fileName");
        Hashtable hash = null;
        Functions functions=new Functions();
        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd=request.getRequestDispatcher("/listWireReportsRandomCharges.jsp?ctoken="+user.getCSRFToken()+"&copyiframe="+copyiframe);

        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 20,true))
        {
            sb.append("Invalid Member Id,");
        }
        if(!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id,");
        }
        if(!ESAPI.validator().isValidInput("bankwireid", bankwireId, "Numbers", 5,true))
        {
            sb.append("Invalid Bankwire Id,");
        }
        if(sb.length()>0)
        {
            logger.error("Validation Failed===="+sb.toString());
            request.setAttribute("statusMsg",sb.toString());
            rd.forward(request,response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        ResultSet rs = null;
        try
        {
            //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select merchantrdmchargeid,memberid,terminalid,chargename,chargerate,valuetype,chargecounter,chargeamount,chargevalue,chargeremark from merchant_random_charges where merchantrdmchargeid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_random_charges where merchantrdmchargeid>0 ");


            if(functions.isValueNull(memberId))
            {
                query.append(" and memberid='"+memberId+"'");
                countquery.append(" and memberid='"+memberId+"'");
            }
            if(functions.isValueNull(terminalId))
            {
                query.append(" and terminalid='"+terminalId+"'");
                countquery.append(" and terminalid='"+terminalId+"'");
            }
            if(functions.isValueNull(bankwireId))
            {
                query.append(" and bankwireid='"+bankwireId+"'");
                countquery.append(" and bankwireid='"+bankwireId+"'");
            }
            query.append(" order by merchantrdmchargeid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);

        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            sb.append("System error while perform select query"+s);

        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            sb.append("SQL Exception while perform select query"+ e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("statusMsg",sb.toString());
        rd.forward(request,response);
        return;
    }
}
