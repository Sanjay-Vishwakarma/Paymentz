import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;

public class OrderDetails extends HttpServlet {

    static Logger log = new Logger(OrderDetails.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
          doPost(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {


        HttpSession session = req.getSession();
        log.debug("Entering in OrderDetails");
        String ccnum = (String) req.getAttribute("ccnum");
        User user =  (User)session.getAttribute("Anonymous");
        String icicitransid ="";
        String accountid="";
        String fromtype=(String)session.getAttribute("company");
        try
        {
             icicitransid = ESAPI.validator().getValidInput("STrackingid",(String) req.getParameter("STrackingid"),"Numbers",10,true);
             accountid = ESAPI.validator().getValidInput("AccountId",(String) req.getParameter("AccountId"),"Numbers",10,true);

        }
        catch(ValidationException e)
        {
            log.error("Invalid TrackingID",e);
        }


        PrintWriter out = res.getWriter();

        try {

            Hashtable hash = getTransactionDetails(icicitransid, ccnum,accountid,fromtype);
            if(hash!=null)
            {
            req.setAttribute("transactionsdetails", hash);
            }
            else
            {

                out.println( Functions.NewShowConfirmation("Sorry", "No records found for Tracking Id :"+icicitransid));
            }
            ccnum=null;
            hash=null;
            RequestDispatcher rd = req.getRequestDispatcher("/orderDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError se) {
            log.error("System Error::::::",se);
            out.println(Functions.NewShowConfirmation("ERROR", "No records found "));
        }


    }

    public Hashtable getTransactionDetails(String icicitransId, String ccnum, String accountid,String fromtype) throws SystemError {
        log.debug("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        String tablename="";
        String gateway = GatewayAccountService.getGatewayAccount(accountid).getGateway();
        try {
            conn = Database.getConnection();
            tablename = Database.getTableName(gateway);
            StringBuffer query =null;
            if(tablename.equals("transaction_icicicredit"))
            {
            query = new StringBuffer("select transaction_icicicredit.icicitransid as \"Tracking ID\",status as \"Status\",name as \"Cardholder's Name\",concat(bin_details.first_six,'******',bin_details.last_four) as \"card\",expdate as \"Expiry date\",amount as \"Transaction Amount\",description as Description,orderdescription as \"Order Description\",date_format(from_unixtime(transaction_icicicredit.dtstamp),'%d-%m-%Y') as \"Date of transaction\",date_format(from_unixtime(unix_timestamp(transaction_icicicredit.timestamp)),'%d-%m-%Y') as \"Last update\",t.emailaddr as \"Customer's Emailaddress\",company_name as \"Name of Merchant\",sitename as \"Site URL\",members.telno as \"Merchant's telephone Number\",captureamount as \"Captured Amount\",refundamount as \"Refund Chargeback Amount\",members.city as City,members.country as Country from transaction_icicicredit left join bin_details on transaction_icicicredit.icicitransid = bin_details.icicitransid,members where transaction_icicicredit.toid=members.memberid and t.totype=? and transaction_icicicredit.icicitransid=? ");
            }
            else
            {
            query = new StringBuffer("select t.trackingid as \"Tracking ID\",status as \"Status\",name as \"Cardholder's Name\",concat(bin_details.first_six,'******',bin_details.last_four) as \"card\",expdate as \"Expiry date\",amount as \"Transaction Amount\",description as Description,orderdescription as \"Order Description\",date_format(from_unixtime(t.dtstamp),'%d-%m-%Y') as \"Date of transaction\",date_format(from_unixtime(unix_timestamp(t.timestamp)),'%d-%m-%Y') as \"Last update\",t.emailaddr as \"Customer's Emailaddress\",company_name as \"Name of Merchant\",sitename as \"Site URL\",members.telno as \"Merchant's telephone Number\",captureamount as \"Captured Amount\",refundamount as \"Refund Chargeback Amount\",members.city as City,members.country as Country from "+tablename+" as t left join bin_details on t.trackingid = bin_details.icicitransid ,members where t.toid=members.memberid and t.totype=? and t.trackingid=? ");
            }


            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,fromtype);
            pstmt.setString(2,icicitransId);

            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());


        }
        catch (SQLException se) {
            log.error("SQL EXCEPTION in OrderDetails:::::",se);
            throw new SystemError();
        }
        finally {
            Database.closeConnection(conn);
        }

        log.debug("Leaving getTransactionDetails");
        return hash;
    }

}