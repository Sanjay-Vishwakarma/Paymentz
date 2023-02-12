import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PayoutManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 6/3/14
 * Time: 2:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireList extends HttpServlet
{
    private static Logger log = new Logger(WireList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in WireList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String reportid = req.getParameter("reportid");

        String errormsg = "";
        String EOL = "<BR>";

        try
        {
            validateOptionalParameter(req);

        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if (!(ESAPI.validator().isValidInput("reportid", reportid, "Number", 10, true)))
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b> Invalid Report Id" + EOL + "</b></font></center>";
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        Functions functions = new Functions();
        String accountid = req.getParameter("accountid");
        String toid = req.getParameter("toid");
        String terminalid=req.getParameter("terminalid");
        String is_paid=req.getParameter("isrr");
        String gateway= functions.splitGatewaySet(req.getParameter("pgtypeid"));
        String parentcycleid=req.getParameter("parentcycleid");
        String cycleid=req.getParameter("cycleid");

        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        int records=15;
        int pageno=1;
        int start = 0; // start index
        int end = 0; // end index

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        String fdtstamp=Functions.converttomillisec(fmonth,fdate,fyear,"0","0","0");
        String tdtstamp=Functions.converttomillisec(tmonth,tdate,tyear,"23","59","59");

        PayoutManager payoutManager = new PayoutManager();
        Hashtable wirelisthash = payoutManager.getMerchantWireList(toid,accountid,terminalid,cycleid,parentcycleid,gateway,is_paid,fdtstamp,tdtstamp,pageno,records,reportid);
        req.setAttribute("transdetails", wirelisthash);
        req.setAttribute("toid", toid);
        req.setAttribute("accountid", accountid);
        req.setAttribute("terminalid", terminalid);
        req.setAttribute("status", is_paid);

        RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

        /*try
        {
            conn= Database.getConnection();
            //StringBuffer query = new StringBuffer("SELECT settledid,settledate,firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,currency,status,settlementreportfilepath,settledtransactionfilepath,markedfordeletion,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate' FROM merchant_wiremanager WHERE markedfordeletion='N'");
            StringBuffer query = new StringBuffer("SELECT settledid,settledate,firstdate,lastdate,amount,balanceamount,netfinalamount,unpaidamount,mw.currency,mw.STATUS,settlementreportfilepath,settledtransactionfilepath,markedfordeletion,mw.TIMESTAMP,mw.toid,mw.terminalid,mw.accountid,mw.paymodeid,mw.cardtypeid,isrollingreserveincluded,rollingreservereleasedateupto,settlementcycle_no,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationdate' FROM merchant_wiremanager AS mw, gateway_accounts AS ga, gateway_type AS gt WHERE mw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND markedfordeletion='N'");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM merchant_wiremanager AS mw, gateway_accounts AS ga, gateway_type AS gt WHERE mw.accountid=ga.accountid AND ga.pgtypeid=gt.pgtypeid AND markedfordeletion='N'");

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and wirecreationtime >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and wirecreationtime <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and wirecreationtime <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and mw.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                countquery.append(" and mw.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }

            if (functions.isValueNull(accountid))
            {
                query.append(" and mw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
                countquery.append(" and mw.accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
            }
            if (functions.isValueNull(terminalid))
            {
                query.append(" and mw.terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
                countquery.append(" and mw.terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
            }
            if (functions.isValueNull(gateway) && !gateway.equals("0"))
            {
                query.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
                countquery.append(" and gt.pgtypeid='" + ESAPI.encoder().encodeForSQL(me,gateway) + "'");
            }

            if (functions.isValueNull(is_paid))
            {
                if(is_paid.equalsIgnoreCase("Y"))
                {
                    query.append(" and status='paid'");
                    countquery.append(" and status='paid'");
                }
                else
                {
                    query.append(" and status='unpaid'");
                    countquery.append(" and status='unpaid'");
                }
            }
            query.append(" ORDER BY toid DESC limit "+start + "," +end);
            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            int totalrecords=0;
            if(rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            req.setAttribute("transdetails", hash);
            req.setAttribute("toid", toid);
            req.setAttribute("accountid", accountid);
            req.setAttribute("terminalid", terminalid);
            req.setAttribute("status", is_paid);

            RequestDispatcher rd = req.getRequestDispatcher("/wirelist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from wire manager", systemError);
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from wire manager", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }*/
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.TERMINALID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}