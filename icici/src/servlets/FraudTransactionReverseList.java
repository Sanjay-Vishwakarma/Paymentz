import com.directi.pg.Admin;
import com.directi.pg.*;
import com.fraud.FraudUtils;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 10/06/14
 * Time: 1:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class FraudTransactionReverseList extends HttpServlet
{
    static Logger logger = new Logger(FraudTransactionReverseList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        ServletContext application = getServletContext();
        int start = 0; // start index
        int end = 0; // end index

        String memberId=null;
        String accountId=null;
        String errormsg="";
        boolean flag=false;
        String EOL = "<BR>";
        int pageno=1;
        int records = 30;
        Hashtable hash = null;

        try
        {
            validateMandatoryParameter(req);
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input ::::::",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            logger.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        FraudUtils fraudUtils=new FraudUtils();
        memberId = req.getParameter("memberid");
        accountId = req.getParameter("accountid");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);


        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        if ("0".equals(accountId))
        {
            logger.debug("Please Provide AccountId");
            req.setAttribute("errormessage", "Please Provide AccountId");
            RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }

        String tableName = fraudUtils.getTableNameFromAccountId(accountId);
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", req.getParameter("SRecords"), "Numbers", 5, true), 30);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer();
        StringBuffer countquery = new StringBuffer();
        Functions functions=new Functions();
        if("transaction_common".equals(tableName))
        {
            query.append("select t.trackingid,t.accountid,t.toid,t.description,t.captureamount as amount,t.refundamount as refundamount,t.status,t.paymentid,t.fromid,t.timestamp,FROM_UNIXTIME(t.dtstamp) AS 'time',t.description as paymentOrderNumber,ft.isReversed,ft.fstransid,bd.isFraud,bd.isRefund,m.maxScoreAllowed,m.maxScoreAutoReversal,aftd.score from transaction_common AS t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+" ");
            countquery.append("select count(*) from transaction_common AS t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+"");
        }
        if("transaction_ecore".equals(tableName))
        {
            query.append("select t.trackingid,t.accountid,t.toid,t.description,t.amount,t.status,t.timestamp,FROM_UNIXTIME(t.dtstamp) AS 'time',t.fromid,t.ecorePaymentOrderNumber as paymentOrderNumber,ft.isReversed,ft.fstransid,bd.isFraud,bd.isRefund,m.maxScoreAllowed,m.maxScoreAutoReversal,aftd.score from transaction_ecore t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+" ");
            countquery.append("select count(*) from transaction_ecore AS t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+"");
        }
        if("transaction_qwipi".equals(tableName))
        {
            query.append("SELECT t.trackingid,t.toid,t.description,t.amount,t.status,t.timestamp,FROM_UNIXTIME(t.dtstamp) AS 'time',t.fromid,t.accountid,t.qwipiPaymentOrderNumber as paymentOrderNumber,ft.isReversed,ft.fstransid,bd.isFraud,bd.isRefund,m.maxScoreAllowed,m.maxScoreAutoReversal,aftd.score FROM transaction_qwipi AS t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+" ");
            countquery.append("SELECT count(*) FROM transaction_qwipi AS t JOIN fraud_transaction AS ft  ON t.trackingid=ft.trackingid JOIN bin_details AS bd ON t.trackingid=bd.icicitransid join members m on t.toid=m.memberid JOIN at_fraud_trans_details AS aftd ON ft.fraud_transaction_id=aftd.fraud_trans_id WHERE t.trackingid>0 AND t.toid="+ESAPI.encoder().encodeForSQL(me,memberId)+" AND t.accountid="+ESAPI.encoder().encodeForSQL(me,accountId)+"");
        }
        if (functions.isValueNull(fdtstamp))
        {
            query.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            countquery.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
        }
        if (functions.isValueNull(tdtstamp))
        {
            query.append(" and t.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
            countquery.append(" and t.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
        }
        countquery.append(" and t.status in('capturesuccess','settled') ");
        query.append(" and t.status in('capturesuccess','settled')  order by trackingid desc LIMIT " + start + "," + end);
        logger.debug("Data query===" + query.toString());
        logger.debug("Count query===" + countquery.toString());
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);

        }
        catch (SystemError se)
        {
            logger.error("System Error::::::",se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/fraudTransactionReverseList.jsp");
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
