import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Hashtable;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;

public class TransactionDetails extends HttpServlet
{

    static Logger log = new Logger(TransactionDetails.class.getName());
    ServletContext ctx = null;

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   log.debug("Entering in TransactionDetails");
        ctx = getServletContext();
        String icicitransid = null;
        try
        {
            icicitransid=ESAPI.validator().getValidInput("STrackingid",(String) req.getParameter("STrackingid"),"Numbers",10,true);
        }
        catch(ValidationException e)
        {
            log.error("Invalid Input",e);
        }

        if (icicitransid == null || icicitransid.equals(""))
            doPost(req, res);
        boolean archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();
        PrintWriter out = res.getWriter();

        try
        {
            Hashtable hash = getTransactionDetails(icicitransid, archive);
            log.debug("fatch record through getTransactionDetails");
            req.setAttribute("transactionsdetails", hash);

            log.debug("forwarding to transactionDetails.jsp");
            hash=null;
            RequestDispatcher rd = req.getRequestDispatcher("/transactionDetails.jsp");
            rd.forward(req, res);

        }
        catch (SystemError se)
        {   log.error("SysyemError in doGet method",se);
            out.println(Functions.ShowMessage("Error", "In doGet"));
        }
        catch (Exception e)
        {   log.error("Exception in doGet method",e);
            out.println(Functions.ShowMessage("Error","Exception in doGet"));
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {  log.debug("listing Transaction in doPost method");
        if (ctx == null) ctx = getServletContext();
        int start = 0; // start index
        int end = 0; // end index
        String str = null;
        String name=null;
        String desc=null;
        String amount=null;
        String firstfourofccnum=null;
        String lastfourofccnum=null;
        String emailaddr=null;
        String orderdesc=null;
        String trackingid=null;
        String status=null;
        String perfectmatch=null;
        String toid=null;
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        perfectmatch = Functions.checkStringNull(req.getParameter("perfectmatch"));
        try
        {
        name = ESAPI.validator().getValidInput("name",req.getParameter("name"),"SafeString",50,true);
        desc = ESAPI.validator().getValidInput("desc",req.getParameter("desc"),"SafeString",50,true);
        amount = ESAPI.validator().getValidInput("amount",req.getParameter("amount"),"Numbers",10,true);
        firstfourofccnum = ESAPI.validator().getValidInput("firstfourofccnum",req.getParameter("firstfourofccnum"),"Numbers",6,true);
        lastfourofccnum = ESAPI.validator().getValidInput("lastfourofccnum",req.getParameter("lastfourofccnum"),"Numbers",4,true);
        emailaddr = ESAPI.validator().getValidInput("emailaddr",req.getParameter("emailaddr"),"Email",50,true);
        orderdesc = ESAPI.validator().getValidInput("orderdesc",req.getParameter("orderdesc"),"SafeString",50,true);
        trackingid = ESAPI.validator().getValidInput("STrackingid",req.getParameter("STrackingid"),"Numbers",10,true);
        status = ESAPI.validator().getValidInput("status",req.getParameter("status"),"Status",20,true);

        toid = ESAPI.validator().getValidInput("toid",req.getParameter("toid"),"Numbers",10,true);

        fdate = ESAPI.validator().getValidInput("fdate",req.getParameter("fdate"),"Days",2,true);
        tdate = ESAPI.validator().getValidInput("tdate",req.getParameter("tdate"),"Days",2,true);
        fmonth = ESAPI.validator().getValidInput("fmonth",req.getParameter("fmonth"),"Months",2,true);
        tmonth = ESAPI.validator().getValidInput("tmonth",req.getParameter("tmonth"),"Months",2,true);
        fyear = ESAPI.validator().getValidInput("fyear",req.getParameter("fyear"),"Years",4,true);
        tyear = ESAPI.validator().getValidInput("tyear",req.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {
          log.error("Invalid Input",e);
        }
        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        boolean archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();


        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        PrintWriter out = res.getWriter();

        //Functions fn=new Functions();
        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);


        try
        {   log.debug("All parameters are passed in listTransaction ");
            Hashtable hash = listTransactions(toid, trackingid, name, desc, orderdesc, amount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive);

            req.setAttribute("transactionsdetails", hash);
            //log.info(hash);
             firstfourofccnum=null;
             lastfourofccnum=null;
            hash=null;
            log.debug("Transaction is successfully load the data and forwaring to transection.jsp");
            hash=null;
            RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp");
            rd.forward(req, res);

        }
        catch (SystemError se)
        {   log.error("errror in doPost ",se);
            StringWriter sw = new StringWriter();
            se.printStackTrace(new PrintWriter(sw));
            out.println(Functions.ShowMessage("Error", sw.toString()));
        }
        catch (Exception e)
        {   log.error("Exception:::: errror in doPost",e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            out.println(Functions.ShowMessage("Error", sw.toString()));
        }
        firstfourofccnum=null;
        lastfourofccnum=null;
    }

    public Hashtable listTransactions(String toid, String trackingid, String name, String desc, String orderdesc, String amount, String firstfourofccnum, String lastfourofccnum, String emailaddr, String tdtstamp, String fdtstamp, String status, int records, int pageno, String perfectmatch, boolean archive) throws SystemError
    {

        ctx.log("Entering listTransactions");
        String tableName = "transaction_icicicredit";
        Hashtable hash = null;
        if (archive)
        {
            tableName += "_archive";
        }
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("select transaction_icicicredit.icicitransid,status,name,amount,description,orderdescription," + tableName + ".dtstamp,hrcode," + tableName + ".accountid from " + tableName + ",members,bin_details where " + tableName + ".toid=members.memberid and transaction_icicicredit.icicitransid = bin_details.icicitransid ");
            StringBuffer countquery = new StringBuffer("select count(*) from " + tableName + ",members where " + tableName + ".toid=members.memberid ");


            if (toid != null)
            {
                query.append(" and toid=" + toid);
                countquery.append(" and toid=" + toid);
            }


            if (status != null)
            {
                query.append(" and status='" + status + "'");
                countquery.append(" and status='" + status + "'");
            }

            if (amount != null)
            {
                query.append(" and amount=" + amount);
                countquery.append(" and amount=" + amount);
            }

            if (emailaddr != null)
            {
                query.append(" and emailaddr='" + emailaddr + "'");
                countquery.append(" and emailaddr='" + emailaddr + "'");
            }
            if (firstfourofccnum != null)
            {
                query.append(" and bin_details.first_six ='" + firstfourofccnum + "'");
                countquery.append(" and bin_details.last_four ='" + firstfourofccnum + "'");
            }
            if (lastfourofccnum != null)
            {
                query.append(" and bin_details.last_four ='" + lastfourofccnum + "'");
                countquery.append(" and bin_details.last_four ='" + lastfourofccnum + "'");
            }

            if (name != null)
            {
                if (perfectmatch == null)
                {
                    query.append(" and name like '%" + name + "%'");
                    countquery.append(" and name like '%" + name + "%'");

                }
                else
                {

                    query.append(" and name='" + name + "'");
                    countquery.append(" and name='" + name + "'");
                }
            }

            if (fdtstamp != null)
            {
                query.append(" and " + tableName + ".dtstamp >= " + fdtstamp);
                countquery.append(" and " + tableName + ".dtstamp >= " + fdtstamp);
            }

            if (tdtstamp != null)
            {
                query.append(" and " + tableName + ".dtstamp <= " + tdtstamp);
                countquery.append(" and " + tableName + ".dtstamp <= " + tdtstamp);
            }


            if (desc != null)
            {
                if (perfectmatch == null)
                {
                    query.append(" and description like '%" + desc + "%'");
                    countquery.append(" and description like '%" + desc + "%'");
                }
                else
                {
                    query.append(" and description='" + desc + "'");
                    countquery.append(" and description='" + desc + "'");
                }
            }

            if (orderdesc != null)
            {
                if (perfectmatch == null)
                {
                    query.append(" and orderdescription like '%" + orderdesc + "%'");
                    countquery.append(" and orderdescription like '%" + orderdesc + "%'");
                }
                else
                {
                    query.append(" and orderdescription='" + orderdesc + "'");
                    countquery.append(" and orderdescription='" + orderdesc + "'");
                }
            }


            if (trackingid != null)
            {
                query.append(" and icicitransid=" + trackingid);
                countquery.append(" and icicitransid=" + trackingid);
            }

            query.append(" order by " + tableName + ".dtstamp DESC,icicitransid ");

            query.append(" limit " + start + "," + end);


            

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        log.debug("Leaving listTransactions");
        ctx.log("Leaving listTransactions");
        return hash;
    }

    public Hashtable getTransactionDetails(String icicitransId, boolean archive) throws SystemError
    {   log.debug("fatch record 1");
        ctx.log("Entering getTransactionDetails");
        Hashtable hash = null;
        Connection conn = null;
        String tableName = "transaction_icicicredit";
        if (archive)
        {
            tableName += "_archive";
        }
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("select T.icicitransid as \"Tracking ID\",status as \"Status\",name as \"Cardholder's Name\",M.memberid as memberid, concat(B.first_six,'******',B.last_four) as \"ccnum\",expdate as \"Expiry date\",amount as \"Transaction Amount\",description as Description,orderdescription as \"Order Description\",date_format(from_unixtime(T.dtstamp),'%d-%m-%Y') as \"Date of transaction\",date_format(from_unixtime(unix_timestamp(T.timestamp)),'%d-%m-%Y') as \"Last update\",emailaddr as \"Customer's Emailaddress\",T.city as City,street as Street, T.state as State,T.country as Country,company_name as \"Name of Merchant\",contact_persons as \"Contact person\",contact_emails as \"Merchant's Emailaddress\",sitename as \"Site URL\",M.telno as \"Merchants telephone Number\",authqsiresponsedesc as \"Auth Response Description\",captureamount as \"Captured Amount\",refundamount as \"Refund Amount\",chargebackamount as \"Chargeback Amount\", T.hrcode as HRCode,T.accountid transaction_icicicredit T,members  M ,bin_details B where T.toid=M.memberid and T.icicitransid = B.icicitransid and T.icicitransid=?");
            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,icicitransId);

            hash = Database.getHashFromResultSetForTransactionEntry(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        log.debug("Leaving getTransactionDetails");
        ctx.log("Leaving getTransactionDetails");
        return hash;
    }

}
