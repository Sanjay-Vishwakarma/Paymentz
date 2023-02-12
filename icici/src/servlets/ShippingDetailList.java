import com.directi.pg.*;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 3/4/14
 * Time: 12:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShippingDetailList  extends HttpServlet
{
    private static Logger log = new Logger(ShippingDetailList.class.getName());
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
        Functions functions = new Functions();
        int records=15;
        int pageno=1;

        String gateway = "";
        String trackingid = "";
        String memberid = "";
        String fromid = "";
        String desc = "";
        String fdate = "";
        String tdate = "";
        String fmonth = "";
        String tmonth = "";
        String fyear = "";
        String tyear = "";

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

        int start = 0; // start index
        int end = 0; // end index
        Hashtable hash = null;
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

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        gateway = req.getParameter("gateway");
        trackingid = req.getParameter("trackingid");
        memberid = req.getParameter("toid");
        fromid = req.getParameter("fromid");
        desc = req.getParameter("description");

        Set<String> gatewayTypeSet = getGatewayHash(gateway);
        try
        {
            hash = listTransactions(memberid,trackingid,desc,fromid, records, pageno,gatewayTypeSet,fdtstamp,tdtstamp);
        }
        catch (SystemError systemError)
        {
            log.error("System error",systemError);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        req.setAttribute("transdetails",hash);
        req.setAttribute("gateway",gateway);
        req.setAttribute("trackingid", trackingid);
        req.setAttribute("fromid", fromid);
        req.setAttribute("description", desc);
        req.setAttribute("toid",memberid);
        RequestDispatcher rd = req.getRequestDispatcher("/shippingdetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public Hashtable listTransactions(String toid, String trackingid, String description, String fromid, int records, int pageno,Set<String> gatewayTypeSet,String fdtstamp,String tdtstamp) throws SystemError
    {
        Hashtable hash = null;
        String tablename = "";
        String fields = "";
        Functions functions = new Functions();
        StringBuffer query = new StringBuffer();
        StringBuffer count =new StringBuffer();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            //tablename = Database.getTableName(gateway);
            Set<String>  accountids = null;
            if(gatewayTypeSet.size()==1)
            {
                accountids = getAccountIds(gatewayTypeSet.iterator().next());
            }
            Set<String> tables = Database.getTableSet(gatewayTypeSet);
            Iterator i = tables.iterator();
            while(i.hasNext())
            {
                tablename = (String) i.next();

                fields = "t.trackingid AS trackingid,t.description,t.amount,t.accountid,t.status,(FROM_UNIXTIME(t.dtstamp)) AS dt,t.paymodeid,t.cardtype,t.pod,t.podbatch,t.fromid,t.toid,t.name";
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


                query.append("select "+fields+ "," +sPaymentIdField+ " from " +tablename+ " AS t,members AS m where t.toid=m.memberid AND (t.pod IS NOT NULL AND t.podbatch IS NOT NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y')) ");       //AND m.isPODRequired=TRUE
                //count.append("select count(*) from " +tablename+ " AS t,members AS m where t.toid=m.memberid AND (t.pod IS NOT NULL AND t.podbatch IS NOT NULL) AND (t.status='authsuccessful' OR (t.status='capturesuccess' AND m.isPODRequired='Y')) ");

                if (functions.isValueNull(fdtstamp))
                {
                    query.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                   //count.append(" and t.dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                }

                if (functions.isValueNull(tdtstamp))
                {
                    query.append(" and t.dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                    //count.append(" and t.dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                }

                if (functions.isValueNull(toid))
                {
                    query.append(" AND t.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                    //count.append(" AND t.toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                }
                if (functions.isValueNull(trackingid))
                {
                    query.append(" AND t.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                    //count.append(" AND t.trackingid='" + ESAPI.encoder().encodeForSQL(me,trackingid) + "'");
                }

                if (functions.isValueNull(description))
                {
                    query.append(" AND t.description ='" + ESAPI.encoder().encodeForSQL(me,description) + "'");
                    //count.append(" AND t.description ='" + ESAPI.encoder().encodeForSQL(me,description) + "'");
                }

                if (functions.isValueNull(fromid))
                {
                    query.append(" AND t.fromid='" + fromid + "'");
                    //count.append(" AND t.fromid='" + fromid + "'");
                }
                if(accountids !=null && !accountids.equals("null") && !accountids.equals(""))
                {

                    query.append(" and t.accountid IN ( ");
                    //count.append(" and t.accountid IN ( ");
                    Iterator<String> accounts =  accountids.iterator();
                    while(accounts.hasNext())
                    {
                        query.append(accounts.next());
                        //count.append(accounts.next());
                        if(accounts.hasNext())
                        {
                            query.append(" , ");
                            //count.append(" , ");
                        }
                        else
                        {
                            query.append(" )");
                            //count.append(" )");
                        }
                    }
                }

               /* if(i.hasNext())
                    query.append(" UNION ");*/
                    //count.append(" UNION ");
            }

            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            query.append("  limit " + start + "," + end);


            log.debug("view query---"+query);
            log.error("ShippingDetailList query+++ "+query);
            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            log.error("ShippingDetailList countquery+++ "+countquery);

            int totalrecords = 0;

            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            log.error(query.toString());
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }

        catch (SQLException se)
        {   log.error("SQL Exception:::::",se);

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
    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }
    public Set<String> getAccountIds(String gateway) throws SystemError
    {
        Functions functions = new Functions();
        Set<String> accountIds = new HashSet<String>();
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        if(functions.isValueNull(gateway))
        {
            try
            {
                conn = Database.getConnection();
                query = new StringBuffer("select A.accountid from gateway_accounts A, gateway_type T where A.pgtypeid = T.pgtypeid and T.gateway=?");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,gateway);
                //log.debug(query);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    accountIds.add(rs.getString("accountid"));
                }
            }
            catch (SQLException se)
            {
                throw new SystemError(se.toString());
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
