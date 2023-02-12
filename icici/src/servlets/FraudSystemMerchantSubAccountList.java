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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Sneha on 17/7/15.
 */
public class FraudSystemMerchantSubAccountList extends HttpServlet
{
    private static Logger logger = new Logger(FraudSystemMerchantSubAccountList.class.getName());
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
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String mid = null;
        String subaccount = null;

        Functions functions = new Functions();
        Hashtable hash = null;

        RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemMerchantSubAccountList.jsp?ctoken="+user.getCSRFToken());

        mid = request.getParameter("mid");
        subaccount = request.getParameter("subaccount");

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {

            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT map.fssubaccountid,merchantfraudserviceid,map.memberid,map.isactive,map.isvisible,map.isonlinefraudcheck,map.isapiuser,sub.subaccountname,sub.subusername,sub.submerchantUsername,fm.fsname,fam.accountname FROM merchant_fssubaccount_mappping AS map JOIN fsaccount_subaccount_mapping AS sub ON map.fssubaccountid=sub.fssubaccountid JOIN fraudsystem_account_mapping AS fam ON sub.fsaccountid=fam.fsaccountid JOIN fraudsystem_master AS fm ON fam.fsid=fm.fsid WHERE map.fssubaccountid >0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from merchant_fssubaccount_mappping AS map JOIN fsaccount_subaccount_mapping AS sub ON map.fssubaccountid=sub.fssubaccountid JOIN fraudsystem_account_mapping AS fam ON sub.fsaccountid=fam.fsaccountid JOIN fraudsystem_master AS fm ON fam.fsid=fm.fsid WHERE map.fssubaccountid >0");

            if(functions.isValueNull(mid))
            {
                query.append(" and map.memberid=" + ESAPI.encoder().encodeForSQL(me,mid));
                countquery.append(" and map.memberid=" + ESAPI.encoder().encodeForSQL(me,mid));
            }
            if(functions.isValueNull(subaccount))
            {
                query.append(" and map.fssubaccountid=" + ESAPI.encoder().encodeForSQL(me,subaccount));
                countquery.append(" and map.fssubaccountid=" + ESAPI.encoder().encodeForSQL(me,subaccount));
            }
            query.append(" order by map.merchantfraudserviceid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            request.setAttribute("transdetails", hash);
            logger.debug("forward to jsp" + hash);
        }
        catch (SystemError systemError)
        {
            logger.debug(systemError);
            //request.setAttribute("message", "Error While Reading The Data From Database");
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.debug(e);
            //request.setAttribute("message","Error While Reading The Data From Database");
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        rd.forward(request, response);
    }
}
