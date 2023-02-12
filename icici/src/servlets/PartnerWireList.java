import com.directi.pg.*;

import com.manager.PartnerManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerWireList extends HttpServlet
{
    private static Logger log = new Logger(PartnerWireList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in PartnerWireList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        String errormsg = "";
        String EOL = "<BR>";
        Hashtable hash = null;
        Functions functions = new Functions();
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
            RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String partnerId=req.getParameter("partnerid");
        String toid=req.getParameter("toid");
        String accountid=req.getParameter("accountid");
        String gateway = "";

        if(functions.isValueNull(req.getParameter("pgtypeid")) && !"0".equals(req.getParameter("pgtypeid")))
        {
            String gatewayArr[] = req.getParameter("pgtypeid").split("-");
            gateway = gatewayArr[0];

            Set<String> accountids = getAccountIds(gateway);
            accountid = String.valueOf(accountids).replace("[","").replace("]","");
            req.setAttribute("pgtypeid", gateway);
        }
        String terminalid=req.getParameter("terminalid");
        String is_paid=req.getParameter("isrr");
        //System.out.println("ispaid in jsp----"+is_paid);

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

        /*req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);*/

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        PartnerManager partnerManager = new PartnerManager();
        Hashtable partnerWireHash = partnerManager.getPartnerWireList(toid,accountid,terminalid,partnerId,gateway,is_paid,fdtstamp,tdtstamp,pageno,records);
        req.setAttribute("transdetails", partnerWireHash);
        req.setAttribute("toid", toid);
        req.setAttribute("accountid", accountid);
        req.setAttribute("terminalid", terminalid);
        req.setAttribute("isrr", is_paid);
        //System.out.println("casfafds----"+req.getParameter("isrr"));

        RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

        /*try
        {
            Functions functions = new Functions();
            conn= Database.getConnection();
            StringBuffer query = new StringBuffer("SELECT settledid,settlementstartdate,settlementenddate,partnerchargeamount,partnerunpaidamount,partnertotalfundedamount,currency,status,settlementreportfilename,markedfordeletion,partnerid,TIMESTAMP,toid,terminalid,accountid,paymodeid,cardtypeid,declinedcoverdateupto,reversedcoverdateupto,chargebackcoverdateupto,FROM_UNIXTIME(wirecreationtime) AS 'wirecreationtime'  FROM partner_wiremanager WHERE markedfordeletion='N'");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM partner_wiremanager WHERE markedfordeletion='N'");

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
            if (functions.isValueNull(partnerId))
            {
                query.append(" and partnerid='" + ESAPI.encoder().encodeForSQL(me,partnerId) + "'");
                countquery.append(" and partnerid='" + ESAPI.encoder().encodeForSQL(me,partnerId) + "'");
            }

            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                countquery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }

            if (functions.isValueNull(accountid))
            {
                query.append(" and accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
                countquery.append(" and accountid='" + ESAPI.encoder().encodeForSQL(me,accountid) + "'");
            }

            if (functions.isValueNull(terminalid))
            {
                query.append(" and terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
                countquery.append(" and terminalid='" + ESAPI.encoder().encodeForSQL(me,terminalid) + "'");
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
            log.debug("Query:-" + query);
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
        }
        catch (SystemError systemError)
        {
            log.error("error while fetching record from partner wiremanager",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL Error while fetching record from partner wiremanager",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/partnerWireList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);*/
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.PARTNERID);
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

    public Set<String> getAccountIds(String gateway)
    {
        Functions functions = new Functions();
        Set<String> accountIds = new HashSet<String>();
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        if(functions.isValueNull(gateway))
        {
            try
            {
                conn = Database.getRDBConnection();
                query = new StringBuffer("select A.accountid from gateway_accounts A, gateway_type T where A.pgtypeid = T.pgtypeid and T.gateway=?");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,gateway);
                //log.debug(query);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    accountIds.add((String)rs.getString("accountid"));
                }
            }
            catch (SQLException se)
            {
                log.error("SQLException in ListAccountID---",se);
            }
            catch (SystemError se)
            {
                log.error("SystemError in ListAccountID---",se);
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        return accountIds;
    }
}
