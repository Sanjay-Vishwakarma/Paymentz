import com.directi.pg.*;
import com.logicboxes.util.Util;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 7/12/14
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiVerifyOrderRefundAlertList extends HttpServlet
{
    private static Logger logger = new Logger(QwipiVerifyOrderRefundAlertList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        String action=req.getParameter("action");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if(action==null)
        {
            logger.debug("No Action Specify check the action parameter of request...");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errormsg="";
        boolean flag=false;
        String EOL = "<BR>";
        Connection conn = null;
        if("searchList".equals(action))
        {
            int start = 0; // start index
            int end = 0; // end index
            PrintWriter out = res.getWriter();
            String data = req.getParameter("data");
            String mid=null;
            String toid=null;
            int pageno=1;
            int records=15;
            Hashtable hash = null;
            String trackingid=null;
            String description=null;
            String verifyorder=null;
            String refundalert=null;

            String fdate = req.getParameter("fdate");
            String tdate = req.getParameter("tdate");
            String fmonth = req.getParameter("fmonth");
            String tmonth = req.getParameter("tmonth");
            String fyear = req.getParameter("fyear");
            String tyear = req.getParameter("tyear");
            Calendar rightNow = Calendar.getInstance();

            if (fdate == null) fdate = "" + 1;
            if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

            if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
            if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

            if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
            if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid Input ::::::",e);
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
                flag = false;
                logger.debug("message..."+e.getMessage());
                req.setAttribute("errormessage",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            description=req.getParameter("description");
            mid = req.getParameter("mid");
            trackingid = req.getParameter("trackingid");
            toid = req.getParameter("toid");
            verifyorder = req.getParameter("verifyorder");
            refundalert = req.getParameter("refundalert");
            if(errormsg!="")
            {
                logger.debug("ENTER VALID DATA");
                req.setAttribute("error",errormsg);
            }
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 15;
            }
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            // calculating start & end
            start = (pageno - 1) * records;
            end = records;
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("select trackingid,toid,description,amount,status,timestamp,fromid,qwipiPaymentOrderNumber,bin_details.isVerifyOrder,isRefundAlert from transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid where status IN ('settled','capturesuccess','authfailed','reversed','chargeback') and trackingid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from transaction_qwipi AS T JOIN bin_details ON T.trackingid = bin_details.icicitransid where status IN ('settled','capturesuccess','authfailed','reversed','chargeback') and trackingid>0");
            Functions functions = new Functions();
            if (functions.isValueNull(toid))
            {
                query.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
                countquery.append(" and toid='" + ESAPI.encoder().encodeForSQL(me,toid) + "'");
            }

            if (functions.isValueNull(trackingid))
            {
                List<String> trackingidList = null;
                if(trackingid.contains(","))
                {
                    trackingidList = Arrays.asList(trackingid.split(","));
                }
                else
                {
                    trackingidList = Arrays.asList(trackingid.split(" "));
                }

                StringBuffer trackingIds=new StringBuffer();
                int i = 0;
                Iterator itr = trackingidList.iterator();
                while (itr.hasNext())
                {
                    if(i!=0)
                    {
                        trackingIds.append(",");
                    }
                    trackingIds.append(""+itr.next()+"");
                    i++;
                }
                logger.debug("Tracking Ids for verifyorder and refundalert====="+trackingIds.toString());
                query.append(" and trackingid IN ("+trackingIds.toString() + ") ");
                countquery.append(" and trackingid IN ("+trackingIds.toString() + ")  ");
            }
            if (functions.isValueNull(description))
            {
                query.append(" and description='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
                countquery.append(" and description='" + ESAPI.encoder().encodeForSQL(me, description) + "'");
            }
            if (functions.isValueNull(verifyorder))
            {
                query.append(" and bin_details.isVerifyOrder='" + ESAPI.encoder().encodeForSQL(me,verifyorder) + "'");
                countquery.append(" and bin_details.isVerifyOrder='" + ESAPI.encoder().encodeForSQL(me,verifyorder) + "'");
            }
            if (functions.isValueNull(refundalert))
            {
                query.append(" and bin_details.isRefundAlert='" + ESAPI.encoder().encodeForSQL(me,refundalert) + "'");
                countquery.append(" and bin_details.isRefundAlert='" + ESAPI.encoder().encodeForSQL(me,refundalert) + "'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                countquery.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " +ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            query.append("  order by trackingid desc LIMIT " + start + "," + end);
            //logger.debug("query==="+query.toString());

            try
            {
                conn = Database.getConnection();
                hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
                ResultSet rs = Database.executeQuery(countquery.toString(), conn);
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
                Database.closeConnection(conn);
            }
            RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp");
            rd.forward(req, res);
        }
        else if("updateOrderVerifyRefundAlert".equals(action))
        {
            String isVerifyOrder=null;
            String isRefundAlert=null;
            PreparedStatement pPStmt=null;
            String binUpdateQuery=null;
            int rowAffecdted=0;
            List successList=new LinkedList();
            List errorList=new LinkedList();
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid Input ::::::",e);
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
                flag = false;
                logger.debug("message..."+e.getMessage());
                req.setAttribute("errormessage",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            try
            {
                conn = Database.getConnection();
                String[] icicitransidStr = null;
                StringBuffer sErrorMessage=new StringBuffer();
                StringBuffer successMsg=new StringBuffer();
                StringBuffer errorMsg=new StringBuffer();

                logger.debug("Your Request to change status bin details table for isVerifyOrder and isRefundAlert flage change");
                if (req.getParameterValues("trackingid")!= null)
                {
                    icicitransidStr = req.getParameterValues("trackingid");
                }
                else
                {
                    sErrorMessage.append("Invalid TransactionID.");
                    req.setAttribute("cbmessage", sErrorMessage.toString());
                    logger.debug("forwarding to ");
                    RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                if (Functions.checkArrayNull(icicitransidStr) == null)
                {
                    sErrorMessage.append("Select at least one transaction.");
                    req.setAttribute("cbmessage", sErrorMessage.toString());
                    logger.debug("forwarding to qwipiVerifyOrderRefundAlertList.jsp");
                    RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
                //System.out.println("icicitransidStr====="+icicitransidStr.length);
                for (String icicitransid : icicitransidStr)
                {

                    isVerifyOrder=req.getParameter("isVerifyOrder_"+icicitransid);
                    isRefundAlert=req.getParameter("isRefundAlert_"+icicitransid);

                    binUpdateQuery ="update bin_details set isVerifyOrder=?, isRefundAlert=? where icicitransid=?";

                    pPStmt = conn.prepareStatement(binUpdateQuery);
                    pPStmt.setString(1,isVerifyOrder);
                    pPStmt.setString(2,isRefundAlert);
                    pPStmt.setString(3,icicitransid);
                    rowAffecdted=pPStmt.executeUpdate();
                    if(rowAffecdted==1)
                    {
                        //Add Transaction to SuccessList
                        successList.add(icicitransid);
                    }
                    else
                    {
                        //Add Transaction to ErrorList.
                        errorList.add(icicitransid);

                    }
                }
                successMsg.append("<BR>Successful  transaction list: <BR>  "+Functions.commaseperatedString(Util.getStringArrayFromVector(successList))+"<br> ");
                errorMsg.append("<BR>Fail  transaction list: <BR>  " + Functions.commaseperatedString(Util.getStringArrayFromVector(errorList))+"<BR>");

                StringBuilder veryfyOrderRefundAlert = new StringBuilder();
                veryfyOrderRefundAlert.append(successMsg.toString());
                req.setAttribute("sSuccessMessage",successMsg);
                req.setAttribute("sErrorMessage",errorMsg);
                veryfyOrderRefundAlert.append("<BR/>");
                veryfyOrderRefundAlert.append(errorMsg.toString());
                /*req.setAttribute("cbmessage", veryfyOrderRefundAlert.toString());*/
            }
            catch (Exception e)
            {
                logger.error(e);
                Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
            }
            finally
            {
                Database.closeConnection(conn);
            }
            RequestDispatcher rd = req.getRequestDispatcher("/qwipiVerifyOrderRefundAlertList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        else
        {
            logger.debug("Invalid Action Or Invalid Request");
            res.sendRedirect("/icici/logout.jsp");
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.COMMA_SEPRATED_NUM);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.MID);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
