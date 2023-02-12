import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/5/14
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class EditShippingDetailList extends HttpServlet
{
    static Logger log = new Logger(EditShippingDetailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in SheepingDetailList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        String trackingid=null;
        String memberid=null;
        String fromid=null;
        String desc=null;
        String gateway=null;

        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        int records=15;
        int pageno=1;
        Calendar rightNow = Calendar.getInstance();

        /*if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

        fdate = req.getParameter("fdate");
        tdate = req.getParameter("tdate");
        fmonth = req.getParameter("fmonth");
        tmonth = req.getParameter("tmonth");
        fyear = req.getParameter("fyear");
        tyear = req.getParameter("tyear");

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        //System.out.println("fdtstamp:::::"+fdtstamp);
        //System.out.println("tdtstamp::::"+tdtstamp);

        int start = 0; // start index
        int end = 0; // end index
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
            //Incase of ValidationException, show which are the fields are having invalid values on the screen as error
            //Program execution must be stopped.
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        gateway = req.getParameter("gateway");

        trackingid = req.getParameter("trackingid");
        memberid = req.getParameter("toid");
        fromid = req.getParameter("fromid");
        desc = req.getParameter("description");

        try
        {
            hash = listTransactions(memberid,trackingid,desc,fromid, records, pageno,fdtstamp,tdtstamp,gateway);

        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
        }
        req.setAttribute("transdetails",hash);
        req.setAttribute("gateway",gateway);
        req.setAttribute("trackingid", trackingid);
        req.setAttribute("fromid", fromid);
        req.setAttribute("description", desc);
        req.setAttribute("toid",memberid);
        RequestDispatcher rd = req.getRequestDispatcher("/editshippingdetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public Hashtable listTransactions(String toid, String trackingid, String description, String fromid, int records, int pageno,String fdtstamp,String tdtstamp, String gatewayName) throws SystemError
    {
        Hashtable hash = null;
        Functions functions = new Functions();

        String tablename = "";
        String fields = "";
        StringBuffer count=new StringBuffer();
        StringBuffer query = new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            tablename = Database.getTableName(gatewayName);
            fields = "t.trackingid AS trackingid,t.description,t.amount,t.accountid,t.status,FROM_UNIXTIME(t.dtstamp) AS dt,t.paymodeid,t.cardtype,t.pod,t.podbatch,t.fromid,t.toid,t.name";
            String sPaymentIdField = null;

            ///////////
            if(tablename.equals("transaction_qwipi"))
            {
                sPaymentIdField="t.qwipiPaymentOrderNumber AS paymentid";
            }
            else if(tablename.equals("transaction_ecore"))
            {
                sPaymentIdField="t.ecorePaymentOrderNumber AS paymentid";
            }
            else
            {
                sPaymentIdField="t.paymentid AS paymentid";
            }

            /*query.append("select " + fields + ","+sPaymentIdField+" from "+tablename+" AS t,members AS m "+" where t.toid=m.memberid AND (t.pod IS NULL OR t.podbatch IS NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y')) AND t.fromtype = '"+gatewayName+"'");
            count.append("select count(*) from "+tablename+" AS t,members AS m "+" where t.toid=m.memberid AND (t.pod IS NULL OR t.podbatch IS NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y')) AND t.fromtype = '"+gatewayName+"'");*/

            query.append("select " + fields + ","+sPaymentIdField+" from "+tablename+" AS t,members AS m "+" where t.toid=m.memberid AND (t.pod IS NOT NULL AND t.podbatch IS NOT NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y'))");
            count.append("select count(*) from "+tablename+" AS t,members AS m "+" where t.toid=m.memberid AND (t.pod IS NOT NULL AND t.podbatch IS NOT NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y'))");

            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                count.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }

            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and t.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                count.append(" and t.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(toid))
            {
                query.append(" AND t.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                count.append(" AND t.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query.append(" AND t.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                count.append(" AND t.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
            }

            if (functions.isValueNull(description))
            {
                query.append(" AND t.description ='" + ESAPI.encoder().encodeForSQL(me,description) + "'");
                count.append(" AND t.description ='" + ESAPI.encoder().encodeForSQL(me,description) + "'");
            }

            if (functions.isValueNull(fromid))
            {
                query.append(" AND t.fromid='" + fromid + "'");
                count.append(" AND t.fromid='" + fromid + "'");
            }
            query.append("  limit " + start + "," + end);
            log.error(query.toString());
            log.debug("query:::::"+query.toString());
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(count.toString(), conn);

            int totalrecords = 0;

            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }

        catch (SQLException se)
        {
            log.error("SQL Exception:::::",se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return hash;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListMandatory.add(InputFields.TOID);
        //inputFieldsListMandatory.add(InputFields.FROMID);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
