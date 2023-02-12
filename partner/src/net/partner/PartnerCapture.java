package net.partner;
import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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



public class PartnerCapture extends HttpServlet
{    private static Logger log = new Logger(PartnerCapture.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in POD");
        HttpSession session = req.getSession();
        PartnerFunctions partner=new PartnerFunctions();
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String PartnerCapture_MerchantId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerCapture_MerchantId_errormsg")) ? rb1.getString("PartnerCapture_MerchantId_errormsg") : "Invalid Merchant Id.";
        String PartnerCapture_partnerId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerCapture_partnerId_errormsg")) ? rb1.getString("PartnerCapture_partnerId_errormsg") : "Invalid Partner ID.";
        String PartnerCapture_trackingId_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerCapture_trackingId_errormsg")) ? rb1.getString("PartnerCapture_trackingId_errormsg") : "Invalid TrackingID.";
        String PartnerCapture_auth_errormsg = StringUtils.isNotEmpty(rb1.getString("PartnerCapture_auth_errormsg")) ? rb1.getString("PartnerCapture_auth_errormsg") : "Invalid Auth Code.";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");
        String merchantid = req.getParameter("memberid");
        req.setAttribute("memberid",merchantid);
        //System.out.println("memberid n java----"+merchantid);
       // String terminalid=null;

       //String sb = req.getParameter("terminalbuffer");

        int start = 0; // start index
        int end = 0; // end index
        int records=15;
        int pageno=1;
        String pid = "";
        res.setContentType("text/html");
        Functions functions = new Functions();

       // String errormsg = "";
        String error = "";
        //String EOL = "<BR>";

        try
        {
            //validateOptionalParameter(req);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
        }
        catch (ValidationException e)
        {
            error="<center><table align=\"center\" class=\"textb\" >" +error +e.getMessage() +"</table></center>";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {
            pageno =Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true),1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true),15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        // calculating start & end\\

        String trackingid = Functions.checkStringNull(req.getParameter("trackingid"));
        String paymentId=null;
        String accountid=null;
        //String merchantId=null;
        String isPartialCapture =null;
        String partnerId = (String) session.getAttribute("merchantid");
        //terminalid = req.getParameter("terminalid");


        if (!ESAPI.validator().isValidInput("pid",req.getParameter("pid"),"Numbers",10,true))
        {
                error=PartnerCapture_partnerId_errormsg;
                req.setAttribute("error",error);
                RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
        }

        if (!ESAPI.validator().isValidInput("trackingid",req.getParameter("trackingid"),"OnlyNumber",20,true))
        {

            error=PartnerCapture_trackingId_errormsg;
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            trackingid = req.getParameter("trackingid");
        }
        if (!ESAPI.validator().isValidInput("paymentid",req.getParameter("paymentid"),"SafeString",100,true))
        {

            error=PartnerCapture_auth_errormsg;
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            paymentId = req.getParameter("paymentid");
        }

        //String memberId = "";

        if (!ESAPI.validator().isValidInput("memberid",req.getParameter("memberid"),"OnlyNumber",20,false))
        {
            error=PartnerCapture_MerchantId_errormsg;
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;

        }
        /*else
            merchantId=req.getParameter("memberid");*/



        try
        {
            trackingid = ESAPI.validator().getValidInput("trackingid",req.getParameter("trackingid"),"Numbers",20,true);
            paymentId = ESAPI.validator().getValidInput("paymentid",req.getParameter("paymentid"),"SafeString",100,true);
            merchantid=ESAPI.validator().getValidInput("memberid",req.getParameter("memberid"),"Numbers",20,false);


            //accountid = ESAPI.validator().getValidInput("bank",req.getParameter("bank"),"Numbers",20,true);
            isPartialCapture = ESAPI.validator().getValidInput("partialCapture",req.getParameter("partialCapture"),"SafeString",20,true);
            log.debug("is partial capture---"+isPartialCapture);
        }
        catch(ValidationException e)
        {
            log.error("ValidationException",e);
            String error1="Invalid MerchantID";
            req.setAttribute("error", error1);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerSuperpartnerMapped(req.getParameter("pid"),(String) session.getAttribute("merchantid"))){
            req.setAttribute("error","Invalid Partner Mappig.");
            RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        try
        {
            if (functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerMemberMapped(req.getParameter("memberid"), req.getParameter("pid")))
            {
                req.setAttribute("error","Invalid partner member configuration.");
                RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            else if (!functions.isValueNull(req.getParameter("pid")) && !partner.isPartnerSuperpartnerMembersMapped(req.getParameter("memberid"), (String) session.getAttribute("merchantid")))
            {
                req.setAttribute("error","Invalid partner member configuration.");
                RequestDispatcher rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

        }catch(Exception e){
            log.error("Exception---" + e);
        }

        HashMap hash = null;
        RequestDispatcher rd;
        org.owasp.esapi.codecs.Codec MYSQL_CODEC = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        /*TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
        Set<String> gatewayTypeSet = new HashSet();
        gatewayTypeSet.addAll(transactionentry.getGatewayHash(merchantid,accountid).keySet());
        if(gatewayTypeSet.size()==0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }*/
        int totalRecords= getCaptureCountNew(merchantid,paymentId,trackingid);
        start = (pageno - 1) * records;
        end = records;

        if (totalRecords>records)
        {
            end= totalRecords-start;
            if (end>records)
            {
                end= records;
            }
        }
        else
        {
            end= totalRecords;
        }
        StringBuilder query = new StringBuilder();

        /*Iterator i = gatewayTypeSet.iterator();
        while(i.hasNext())
        {*/
           // tablename = Database.getTableName((String)i.next());
        String tablename="transaction_common";
        log.error("tablename++++ "+tablename);

        query.append("select T.trackingid as trackingid,T.description,T.amount,T.accountid,T.status,T.dtstamp,T.paymodeid,T.cardtype,T.terminalid,T.currency,T.fromtype from " +tablename+ " as T, members as M ");

        query.append(" where T.toid=M.memberid and T.toid="+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, merchantid)+" and (T.pod is null OR T.podbatch is null) and (T.status='authsuccessful' OR (T.status='capturesuccess' AND M.isPODRequired=TRUE)) ");
            if (functions.isValueNull(trackingid))
            {
                    query.append(" and trackingid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, trackingid)+"'");
            }
            if (functions.isValueNull(paymentId))
            {
                    query.append(" and paymentid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, paymentId)+"'");
            }

      //  }

        StringBuilder countquery = new StringBuilder("select count(*) from ( " + query + ") as temp ");
       // query.append(" order by trackingid  DESC");
        query.append("LIMIT " + start + "," + end);// Make Change Query

        log.debug("query 12::::"+query);
         Connection conn = null;
        ResultSet rs = null;
        Date date1 = new Date();
        log.debug("before try block::::::" + date1.getTime());
        try
        {
            log.debug(query.toString());
                //conn = Database.getConnection();
            conn = Database.getRDBConnection();
            Date date4= new Date();
            log.error("PartnerCapture query starts###########  "+date4.getTime());
                hash = Database.getHashMapFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            log.error("PartnerCapture query ends ###########  "+new Date().getTime());
            log.error("PartnerCapture query diff ###########  "+(new Date().getTime()-date4.getTime()));
            log.error("PartnerCapture query############ "+query.toString());
                //	out.println(hash.toString());

                hash.put("totalrecords", "" + totalRecords);
                hash.put("records", "0");

                if (totalRecords > 0)
                {
                    hash.put("records", "" + (hash.size() - 2));
                }

                //	out.println(hash.toString());
                req.setAttribute("poddetails", hash);
                session.setAttribute("bank",accountid);

                req.setAttribute("paymentid",paymentId);
        }
        catch (SystemError se)
        {
            log.error("System error in capture::::",se);
            //System.out.println(se.toString());
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");

        }
        catch (Exception e)
        {
            log.error("Exception occure",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        if (isPartialCapture!=null && trackingid!=null)
        {
            rd = req.getRequestDispatcher("/partialCapture.jsp?ctoken="+user.getCSRFToken());
        }
        else
        {
            rd = req.getRequestDispatcher("/partnerCapture.jsp?ctoken="+user.getCSRFToken());
        }
        log.debug("After try block:::" + new Date().getTime());
        log.debug("After try block difference time::::" + (new Date().getTime() - date1.getTime()));
        rd.forward(req, res);
    }

    public int getCaptureCountNew(String merchantid,String paymentId,String trackingid)
    {
        log.error("Entering into getCaptureCountNew method......");
        String tablename="transaction_common";
        StringBuilder query = new StringBuilder();
        Functions functions= new Functions();
        org.owasp.esapi.codecs.Codec MYSQL_CODEC = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        int totalRecords= 0;
        log.error("tablename++++ "+tablename);

        query.append("select T.trackingid as trackingid,T.description,T.amount,T.accountid,T.status,T.dtstamp,T.paymodeid,T.cardtype,T.terminalid,T.currency,T.fromtype from  " + tablename + " as T, members as M ");

        query.append(" where T.toid=M.memberid and T.toid="+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, merchantid)+" and (T.pod is null OR T.podbatch is null) and (T.status='authsuccessful' OR (T.status='capturesuccess' AND M.isPODRequired=TRUE)) ");
            if (functions.isValueNull(trackingid))
            {
                    query.append(" and trackingid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, trackingid)+"'");
            }
            if (functions.isValueNull(paymentId))
            {
                    query.append(" and paymentid='"+ESAPI.encoder().encodeForSQL(MYSQL_CODEC, paymentId)+"'");
            }

        StringBuilder countquery = new StringBuilder("select count(*) from ( " + query + ") as temp ");
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            Date date5= new Date();
            log.error("PartnerCapture countquery starts######## "+date5.getTime());
            rs = Database.executeQuery(countquery.toString(), conn);
            log.error("PartnerCapture countquery ends######### "+new Date().getTime());
            log.error("PartnerCapture countquery diff ######### "+(new Date().getTime()-date5.getTime()));
            log.error("PartnerCapture countquery ######### "+countquery.toString());
            rs.next();
            totalRecords = rs.getInt(1);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::: ",systemError);
        }
        catch (SQLException e)
        {
            log.error("SQLException::::: ",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        return totalRecords;
    }
    /*private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/
}