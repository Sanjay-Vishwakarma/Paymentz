package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.google.gson.Gson;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.TransactionManager;
import com.manager.dao.PaymentDAO;
import com.manager.enums.ResponseLength;
import com.manager.enums.ResponseType;
import com.manager.vo.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.sbm.core.SBMPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;
import com.transaction.utils.WriteDirectTransactionResponse;
import com.transaction.vo.restVO.ResponseVO.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;
import payment.util.WriteJSONResponse;
import payment.util.WriteXMLResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 13/3/14
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionUtility
{
    //static Vector unboilchar = new Vector();
    private static Logger log                           = new Logger(TransactionUtility.class.getName());
    private static TransactionLogger transactionLogger  = new TransactionLogger(TransactionUtility.class.getName());
    Functions functions                                 = new Functions();
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");
   /* static
    {
        log.debug("enter inside TransactionUtility class static block");
        transactionLogger.debug("enter inside TransactionUtility class static block");
        unboilchar.add("a");
        unboilchar.add("b");
        unboilchar.add("c");
        unboilchar.add("d");
        unboilchar.add("e");
        unboilchar.add("f");
        unboilchar.add("g");
        unboilchar.add("h");
        unboilchar.add("i");
        unboilchar.add("j");
        unboilchar.add("k");
        unboilchar.add("l");
        unboilchar.add("m");
        unboilchar.add("n");
        unboilchar.add("o");
        unboilchar.add("p");
        unboilchar.add("q");
        unboilchar.add("r");
        unboilchar.add("s");
        unboilchar.add("t");
        unboilchar.add("u");
        unboilchar.add("v");
        unboilchar.add("w");
        unboilchar.add("x");
        unboilchar.add("y");
        unboilchar.add("x");
        unboilchar.add("0");
        unboilchar.add("1");
        unboilchar.add("2");
        unboilchar.add("3");
        unboilchar.add("4");
        unboilchar.add("5");
        unboilchar.add("6");
        unboilchar.add("7");
        unboilchar.add("8");
        unboilchar.add("9");
    }
*/

    //PayProcessController
    public String checkorderuniqueness(String toid, String fromtype, String description)
    {

        String str = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String query = "select * from transactions where toid = ? and description = ? order by dtstamp desc";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, toid);
            pstmt.setString(2, description);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                str = "Order is not Unique";
            }

            if (str.equals(""))
            {

                String transaction_table = "transaction_icicicredit";

                if (SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    transaction_table = "transaction_icicicredit";
                }
                else if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    transaction_table = "transaction_qwipi";

                }
                else if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    transaction_table = "transaction_ecore";

                }
                else
                {
                    transaction_table = "transaction_common";
                }


                String query2 = "select * from " + transaction_table + " where toid = ? and description = ? order by dtstamp desc";
                PreparedStatement pstmt1 = con.prepareStatement(query2);
                pstmt1.setString(1, toid);
                pstmt1.setString(2, description);
                ResultSet rs1 = pstmt1.executeQuery();
                if (rs1.next())
                {
                    String status = rs1.getString("status");

                    if (status.equals("begun"))
                    {
                        //str= " A transaction for this Order is already in progress. This can occur if you clicked on the back button and tried to submit this Transaction again. In order to prevent charging your card twice we have LOCKED this transaction. If the transaction was not completed then this transaction will be unlocked in the next 15 minutes. You will get a notification of the status of this transaction. Alternatively you can contact the Merchant and refer the below transaction details to verify the status of your transaction" ;

                    }
                    else if (!status.equals("authfailed") && !status.equals("failed") && !status.equals("authcancelled") && !status.equals("cancelled"))
                    {
                        str = "Your Transaction is already being processed. This can occur if you clicked on the back button and tried to submit this Transaction again. The transaction may succeed or fail, however the status of the Transaction will have to be set manually. Please contact the Merchant to verify the status of the transaction with the following reference numbers and inform him of this message. PLEASE DO NOT TRY to execute this transaction once more from the beginning, or you may end up charging your card twice";
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    /**
     * Used for redirecting to process servlets
     * @param str
     * @param ctoken
     * @param res
     * @param hiddenvariable
     * @param header
     * @throws SystemError
     */
    //PayProcessController
    public void doRedirect(String str, String ctoken ,HttpServletResponse res,Hashtable hiddenvariable,String header) throws SystemError
    {
        PrintWriter printWriter = null;
        try
        {
            log.debug(str+"?ctoken="+ctoken.trim());
            transactionLogger.debug(str+"?ctoken="+ctoken.trim());
            printWriter = res.getWriter();
            String url= str.trim()+"?ctoken="+ctoken.trim();
            StringBuffer stringBuffer = new StringBuffer();
            // str = stringBuffer.toString();
            stringBuffer.append("<HTML>");
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            stringBuffer.append("<form name=\"form\" method=\"post\" action=\""+url+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"hiddenvariables\" value=\""+hiddenvariable+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"HEADDER\" value=\""+header+"\">");

            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.debug(stringBuffer);
            printWriter.print(stringBuffer);

        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            throw new SystemError(e.getMessage());
        }
    }

    public void doAutoRedirect(String url, String ctoken ,HttpServletResponse res,String status,String billing,String trackingid, String desc, String amount, String checksum,String token) throws SystemError
    {
        PrintWriter printWriter = null;
        try
        {
            printWriter = res.getWriter();

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<HTML>");
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            stringBuffer.append("<form name=\"form\" method=\"post\" action=\""+url+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"status\" value=\""+status+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"descriptor\" value=\""+billing+"\">");

            stringBuffer.append("<input type=\"hidden\" name=\"trackingid\" value=\""+trackingid+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"desc\" value=\""+desc+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"amount\" value=\""+amount+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"checksum\" value=\""+checksum+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"token\" value=\""+token+"\">");
            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.debug(stringBuffer);
            printWriter.print(stringBuffer);
        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form", e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            throw new SystemError(e.getMessage());
        }
    }

    public void newDoAutoRedirect(String url, String ctoken ,HttpServletResponse res,String status,String billing) throws  PZTechnicalViolationException
    {
        PrintWriter printWriter = null;
        try
        {
            printWriter = res.getWriter();

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<HTML>");
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            stringBuffer.append("<form name=\"form\" method=\"post\" action=\""+url+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"status\" value=\""+status+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"discriptor\" value=\""+billing+"\">");
            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.debug(stringBuffer);
            printWriter.print(stringBuffer);
        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form", e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactionUtility.class.getName(),"newDoAutoRedirect()",null,"transaction","Technical Exception while redirecting to the page",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
    }

    public void doAutoRedirect(CommonValidatorVO commonValidatorVO,HttpServletResponse res,String status,String billing) throws SystemError
    {
        PrintWriter printWriter     = null;
        String checkSum             = null;
        Functions functions         = new Functions();
        String respStatus           = "N";
        DateFormat dateFormat       = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date                   = new Date();
        String timeStamp            = String.valueOf(dateFormat.format(date));
        ErrorCodeVO errorCodeVO     = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
        Transaction transaction         = new Transaction();
        String firstName    = "";
        String lastName     = "";
        String cardholderName   = "";
        String eci              = "";
        String token            = "";
        String redirectMethod   = "";
        if(!functions.isValueNull(billing))
            billing="";

        String paymentMode      = commonValidatorVO.getPaymentType();
        String paymentBrand     = commonValidatorVO.getCardType();
        String pType            = transaction.getPaymentModeForRest(paymentMode);
        String cType            = transaction.getPaymentBrandForRest(paymentBrand);

        if(functions.isValueNull(commonValidatorVO.getEci()))
            eci=commonValidatorVO.getEci();
        else
            eci="";

        if(functions.isValueNull(commonValidatorVO.getToken())){
            token   = commonValidatorVO.getToken();
        }else {
            token="";
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName   = commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName    = commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectMethod()))
            redirectMethod  = commonValidatorVO.getTransDetailsVO().getRedirectMethod();

        transactionLogger.error("redirectMethod >>>>>>>>>>>>>>>>>> "+redirectMethod);

        try
        {
            if (functions.isValueNull(commonValidatorVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                }
            }
            else if (functions.isValueNull(status) && (status.contains("Successful") || status.contains("successful") || status.contains("success")))
            {
                respStatus  = "Y";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
            }
            else if (functions.isValueNull(status) && (status.contains("Pending") || status.contains("pending")))
            {
                respStatus  = "P";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
            }
            else if (functions.isValueNull(status) && (status.contains("Cancel") || status.contains("cancel")))
            {
                respStatus  = "C";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CANCEL_SUCCESSFUL);
            }
            else
            {
                respStatus  = "N";
                if(functions.isValueNull(commonValidatorVO.getErrorMsg())){
                    errorCodeVO.setApiDescription(commonValidatorVO.getErrorMsg());
                }else{
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                }
            }
            String rstrackingid = commonValidatorVO.getTrackingid().split("\\/")[0];
            try
            {
                //checkSum = Checksum.generateChecksumForStandardKit(commonValidatorVO.getTrackingid(),commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getAmount(), respStatus, commonValidatorVO.getMerchantDetailsVO().getKey());
                checkSum = Checksum.generateChecksumForStandardKit(getValue(rstrackingid),getValue(commonValidatorVO.getTransDetailsVO().getOrderId()), getValue(commonValidatorVO.getTransDetailsVO().getAmount()), getValue(respStatus),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));

            }
            catch (NoSuchAlgorithmException e)
            {
                respStatus = "N";
            }
            String cardBin          ="";
            String cardLast4Digits  ="";
            if(null != commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin         = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

            }
            if(functions.isValueNull(commonValidatorVO.getDevice()) && "ios".equalsIgnoreCase(commonValidatorVO.getDevice())){
            String script="<script type=\"text/javascript\">\n" +
                    "        window.webkit.messageHandlers.callbackHandler.postMessage({\n" +
                    "            \"trackingId\" : \""+rstrackingid+"\",\n" +
                    "            \"status\" : \""+respStatus+"\",\n" +
                    "            \"splitTransaction\" : \""+getValue(commonValidatorVO.getFailedSplitTransactions())+"\",\n" +
                    "            \"firstName\" : \""+getValue(firstName)+"\",\n" +
                    "            \"lastName\" : \""+getValue(lastName)+"\",\n" +
                    "            \"checksum\" : \""+checkSum+"\",\n" +
                    "            \"desc\" : \""+getValue(commonValidatorVO.getTransDetailsVO().getOrderDesc())+"\",\n" +
                    "            \"currency\":\""+getValue(commonValidatorVO.getTransDetailsVO().getCurrency())+"\",\n" +
                    "            \"amount\":\""+getValue(commonValidatorVO.getTransDetailsVO().getAmount())+"\",\n" +
                    "            \"tmpl_currency\":\""+getValue(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())+"\",\n" +
                    "            \"tmpl_amount\":\""+getValue(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())+"\",\n" +
                    "            \"timestamp\":\""+getValue(timeStamp)+"\",\n" +
                    "            \"resultCode\":\""+getValue(errorCodeVO.getApiCode())+"\",\n" +
                    "            \"resultDescription\":\""+getValue(errorCodeVO.getApiDescription())+"\",\n" +
                    "            \"cardBin\":\""+getValue(cardBin)+"\",\n" +
                    "            \"cardLast4Digits\":\""+getValue(cardLast4Digits)+"\",\n" +
                    "            \"custEmail\":\""+getValue(commonValidatorVO.getAddressDetailsVO().getEmail())+"\",\n" +
                    "            \"paymentMode\":\""+getValue(pType)+"\",\n" +
                    "            \"paymentBrand\":\""+getValue(cType)+"\",\n" +
                    "            \"eci\":\""+getValue(eci)+"\"\n" +
                    "        });\n" +
                    "</script>";
                res.setContentType("text/html;charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                printWriter = res.getWriter();
                printWriter.print(script);
            }else if(functions.isValueNull(commonValidatorVO.getDevice()) && "android".equalsIgnoreCase(commonValidatorVO.getDevice())){
                String script="<script type=\"text/javascript\">\n" +
                        " var jsonObjectResponse = {};"+
                    "        jsonObjectResponse[\"trackingId\"] = \""+rstrackingid+"\";\n" +
                    "        jsonObjectResponse[\"status\"] = \""+status+"\";\n" +
                    "        jsonObjectResponse[\"splitTransaction\"] = \""+getValue(commonValidatorVO.getFailedSplitTransactions())+"\";\n" +
                    "        jsonObjectResponse[\"firstName\"] = \""+getValue(firstName)+"\";\n" +
                    "        jsonObjectResponse[\"lastName\"] = \""+getValue(lastName)+"\";\n" +
                    "        jsonObjectResponse[\"checksum\"] = \""+checkSum+"\";\n" +
                    "        jsonObjectResponse[\"desc\"] = \""+getValue(commonValidatorVO.getTransDetailsVO().getOrderDesc())+"\";\n" +
                    "        jsonObjectResponse[\"currency\"] = \""+commonValidatorVO.getTransDetailsVO().getCurrency()+"\";\n" +
                    "        jsonObjectResponse[\"amount\"] = \""+commonValidatorVO.getTransDetailsVO().getAmount()+"\";\n" +
                    "        jsonObjectResponse[\"tmpl_currency\"] = \""+commonValidatorVO.getAddressDetailsVO().getTmpl_currency()+"\";\n" +
                    "        jsonObjectResponse[\"tmpl_amount\"] = \""+commonValidatorVO.getAddressDetailsVO().getTmpl_amount()+"\";\n" +
                    "        jsonObjectResponse[\"timestamp\"] = \""+timeStamp+"\";\n" +
                    "        jsonObjectResponse[\"resultCode\"] = \""+errorCodeVO.getApiCode()+"\";\n" +
                    "        jsonObjectResponse[\"resultDescription\"] = \""+errorCodeVO.getApiDescription()+"\";\n" +
                    "        jsonObjectResponse[\"cardBin\"] = \""+cardBin+"\";\n" +
                    "        jsonObjectResponse[\"cardLast4Digits\"] = \""+cardLast4Digits+"\";\n" +
                    "        jsonObjectResponse[\"custEmail\"] = \""+getValue(commonValidatorVO.getAddressDetailsVO().getEmail())+"\";\n" +
                    "        jsonObjectResponse[\"paymentMode\"] = \""+getValue(pType)+"\";\n" +
                    "        jsonObjectResponse[\"paymentBrand\"] = \""+getValue(cType)+"\";\n" +
                    "        jsonObjectResponse[\"eci\"] = \""+eci+"\";\n" +
                    "        android.paymentResultListener(JSON.stringify(jsonObjectResponse));"+
                    "</script>";
                res.setContentType("text/html;charset=UTF-8");
                res.setCharacterEncoding("UTF-8");
                printWriter     = res.getWriter();
                printWriter.print(script);
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<HTML>");
            /*stringBuffer.append("<HEAD> " +
                    "<script type = \"text/javascript\" src=\"/merchant/transactionCSS/js/jquery-3.3.1.min.js\"></script>" +
                    "<script type = \"text/javascript\" src=\"/merchant/transactionCSS/js/content.js\"></script>" +
                    "<script language=\"javascript\">" +
                    "function Load(){" +
                    "var form=document.getElementsByName('form')[0];" +
                    "getResponseFromCheckout(\""+commonValidatorVO.getTransDetailsVO().getRedirectUrl()+"\",form);" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");*/
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            if("GET".equalsIgnoreCase(redirectMethod))
            {
                transactionLogger.error("inside "+redirectMethod+" method in if");
                stringBuffer.append("<form name=\"form\" method=\"get\" action=\"" + commonValidatorVO.getTransDetailsVO().getRedirectUrl() + "\">");
            }
            else
            {
                transactionLogger.error("inside post method in else");
                stringBuffer.append("<form name=\"form\" method=\"post\" action=\"" + commonValidatorVO.getTransDetailsVO().getRedirectUrl() + "\">");
            }

          //  String rstrackingid=commonValidatorVO.getTrackingid().split("\\/")[0];
            transactionLogger.error("autoredirect trackingid::"+rstrackingid);
            stringBuffer.append("<input type=\"hidden\" name=\"trackingid\" value=\""+getValue(rstrackingid)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"paymentId\" value=\""+getValue(rstrackingid)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"desc\" value=\""+getValue(commonValidatorVO.getTransDetailsVO().getOrderId())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"merchantTransactionId\" value=\""+getValue(commonValidatorVO.getTransDetailsVO().getOrderId())+"\">");
            if(functions.isValueNull(commonValidatorVO.getFailedSplitTransactions()))
                stringBuffer.append("<input type=\"hidden\" name=\"splitTransactions\" value=\""+getValue(commonValidatorVO.getFailedSplitTransactions())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"status\" value=\""+getValue(respStatus)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"checksum\" value=\""+getValue(checkSum)+"\">");
            if(functions.isValueNull(billing))
                stringBuffer.append("<input type=\"hidden\" name=\"descriptor\" value=\""+getValue(billing)+"\">");

            if(functions.isValueNull(token))
            {
                stringBuffer.append("<input type=\"hidden\" name=\"token\" value=\"" + token + "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"registrationId\" value=\"" + token + "\">");
            }

            if (null!=commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getMandateId()))
                stringBuffer.append("<input type=\"hidden\" name=\"mandateid\" value=\""+commonValidatorVO.getCardDetailsVO().getMandateId()+"\">");
            if (functions.isValueNull(firstName))
                stringBuffer.append("<input type=\"hidden\" name=\"firstname\" value=\""+firstName+"\">");

            if (functions.isValueNull(lastName))
                stringBuffer.append("<input type=\"hidden\" name=\"lastname\" value=\""+lastName+"\">");

            stringBuffer.append("<input type=\"hidden\" name=\"amount\" value=\""+getValue(commonValidatorVO.getTransDetailsVO().getAmount())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"currency\" value=\""+getValue(commonValidatorVO.getTransDetailsVO().getCurrency())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"timestamp\" value=\""+getValue(timeStamp)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"resultCode\" value=\""+getValue(errorCodeVO.getApiCode())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"resultDescription\" value=\""+getValue(errorCodeVO.getApiDescription())+"\">");
            if(functions.isValueNull(commonValidatorVO.getBankCode()) && functions.isValueNull(commonValidatorVO.getBankDescription()))
            {
                stringBuffer.append("<input type=\"hidden\" name=\"bankCode\" value=\"" + getValue(commonValidatorVO.getBankCode()) + "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"bankDescription\" value=\"" + getValue(commonValidatorVO.getBankDescription()) + "\">");
            }
            if (functions.isValueNull(commonValidatorVO.getReason()))
                stringBuffer.append("<input type=\"hidden\" name=\"remark\" value=\""+getValue(commonValidatorVO.getReason())+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"paymentMode\" value=\""+getValue(pType)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"paymentBrand\" value=\""+getValue(cType)+"\">");

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
                stringBuffer.append("<input type=\"hidden\" name=\"tmpl_currency\" value=\""+commonValidatorVO.getAddressDetailsVO().getTmpl_currency()+"\">");
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount()))
                stringBuffer.append("<input type=\"hidden\" name=\"tmpl_amount\" value=\""+commonValidatorVO.getAddressDetailsVO().getTmpl_amount() + "\">");
            if(null!=commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {

                stringBuffer.append("<input type=\"hidden\" name=\"cardBin\" value=\""+cardBin+ "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"cardLast4Digits\" value=\""+cardLast4Digits+ "\">");
            }
            if (functions.isValueNull(commonValidatorVO.getCustAccountId()))
                stringBuffer.append("<input type=\"hidden\" name=\"custAccountId\" value=\""+commonValidatorVO.getCustAccountId()+"\">");
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
                stringBuffer.append("<input type=\"hidden\" name=\"custEmail\" value=\""+commonValidatorVO.getAddressDetailsVO().getEmail()+ "\">");
            if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                stringBuffer.append("<input type=\"hidden\" name=\"custMerchantId\" value=\""+commonValidatorVO.getCustomerId()+"\">");
            if (functions.isValueNull(commonValidatorVO.getCustomerBankId()))
                stringBuffer.append("<input type=\"hidden\" name=\"custBankId\" value=\""+commonValidatorVO.getCustomerBankId()+"\">");
            /*if (functions.isValueNull(eci))*/
                stringBuffer.append("<input type=\"hidden\" name=\"eci\" value=\""+eci+"\">");
            if (functions.isValueNull(commonValidatorVO.getCommissionPaidToUser()))
                stringBuffer.append("<input type=\"hidden\" name=\"commissionPaidToUser\" value=\""+commonValidatorVO.getCommissionPaidToUser()+"\">");
            if(commonValidatorVO.getMarketPlaceVOList()!=null && commonValidatorVO.getMarketPlaceVOList().size()>0)
            {
                transactionLogger.error("commonValidatorVO.getMarketPlaceVOList()---->"+commonValidatorVO.getMarketPlaceVOList());
                for(MarketPlaceVO marketPlaceVO:commonValidatorVO.getMarketPlaceVOList())
                {
                    if (functions.isValueNull(marketPlaceVO.getTrackingid()))
                        stringBuffer.append("<input type=\"hidden\" name=\"MP_Trackingid[]\" value=\"" + marketPlaceVO.getTrackingid() + "\">");
                    if (functions.isValueNull(marketPlaceVO.getOrderid()))
                        stringBuffer.append("<input type=\"hidden\" name=\"MP_Orderid[]\" value=\"" + marketPlaceVO.getOrderid() + "\">");
                    if (functions.isValueNull(marketPlaceVO.getAmount()))
                        stringBuffer.append("<input type=\"hidden\" name=\"MP_Amount[]\" value=\"" + marketPlaceVO.getAmount() + "\">");
                }
            }

            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.error("automatic redirect---" + stringBuffer);
            res.setContentType("text/html;charset=UTF-8");
            res.setCharacterEncoding("UTF-8");
            printWriter = res.getWriter();
            printWriter.print(stringBuffer);

        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            throw new SystemError(e.getMessage());
        }
    }
    public void doCardRegistrationRedirectGet(CommonValidatorVO commonValidatorVO,HttpServletResponse res,String response,String responseToken) throws SystemError
    {
        PrintWriter printWriter = null;
        String checkSum=null;
        Functions functions=new Functions();
        String respStatus="N";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timeStamp = String.valueOf(dateFormat.format(date));
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String firstName="";
        String lastName="";
        String token="";
        String customerId="";
        String status="";
        if(functions.isValueNull(response)) {
            status=response;
        }

        if(functions.isValueNull(responseToken))
            token=responseToken;
        else
            token="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";
        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            customerId=commonValidatorVO.getCustomerId();
        else
            customerId="";

        try
        {
            if (functions.isValueNull(commonValidatorVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                }
            }
            else if (functions.isValueNull(status) && status.contains("success"))
            {
                respStatus="Y";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_TOKEN_GENERATION);
            }
            else
            {
                respStatus="N";
                if(functions.isValueNull(commonValidatorVO.getErrorMsg())){
                    errorCodeVO.setApiDescription(commonValidatorVO.getErrorMsg());
                }else{
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TOKEN_CREATION_FAILED);
                }
            }
            try
            {
                //checkSum = Checksum.generateChecksumForStandardKit(getValue(commonValidatorVO.getTrackingid()),getValue(commonValidatorVO.getTransDetailsVO().getOrderId()), getValue(commonValidatorVO.getTransDetailsVO().getAmount()), getValue(respStatus),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));
                checkSum = Checksum.generateChecksumForCardRegistration(getValue(commonValidatorVO.getMerchantDetailsVO().getMemberId()),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()), response);
            }
            catch (NoSuchAlgorithmException e)
            {
                respStatus = "N";
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<HTML>");
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            stringBuffer.append("<form name=\"form\" method=\"get\" action=\""+commonValidatorVO.getTransDetailsVO().getRedirectUrl()+"\">");
            if(functions.isValueNull(token))
            {
                //stringBuffer.append("<input type=\"hidden\" name=\"token\" value=\"" + token + "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"registrationId\" value=\"" + token + "\">");
            }
            stringBuffer.append("<input type=\"hidden\" name=\"status\" value=\""+getValue(respStatus)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"resultDescription\" value=\""+getValue(errorCodeVO.getApiDescription())+"\">");
            if(null!=commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                stringBuffer.append("<input type=\"hidden\" name=\"cardBin\" value=\""+cardBin+ "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"cardLast4Digits\" value=\""+cardLast4Digits+ "\">");
            }
            stringBuffer.append("<input type=\"hidden\" name=\"cardType\" value=\""+commonValidatorVO.getCardType()+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"timestamp\" value=\""+getValue(timeStamp)+"\">");
            if (functions.isValueNull(firstName))
                stringBuffer.append("<input type=\"hidden\" name=\"firstname\" value=\""+firstName+"\">");

            if (functions.isValueNull(lastName))
                stringBuffer.append("<input type=\"hidden\" name=\"lastname\" value=\""+lastName+"\">");
            if(functions.isValueNull(customerId))
                stringBuffer.append("<input type=\"hidden\" name=\"customerId\" value=\""+customerId+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"checksum\" value=\""+getValue(checkSum)+"\">");

            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.error("automatic redirect---" + stringBuffer);
            res.setContentType("text/html;charset=UTF-8");
            res.setCharacterEncoding("UTF-8");
            printWriter = res.getWriter();
            printWriter.print(stringBuffer);

        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            throw new SystemError(e.getMessage());
        }
    }
    public void doCardRegistrationRedirect(CommonValidatorVO commonValidatorVO,HttpServletResponse res,String response,String responseToken) throws SystemError
    {
        PrintWriter printWriter = null;
        String checkSum=null;
        Functions functions=new Functions();
        String respStatus="N";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String timeStamp = String.valueOf(dateFormat.format(date));
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String firstName="";
        String lastName="";
        String token="";
        String customerId="";
        String status="";
        if(functions.isValueNull(response)) {
            status=response;
        }

        if(functions.isValueNull(responseToken))
            token=responseToken;
        else
            token="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";
        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            customerId=commonValidatorVO.getCustomerId();
        else
            customerId="";


        try
        {
            if (functions.isValueNull(commonValidatorVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.valueOf(commonValidatorVO.getErrorName()));
                }
            }
            else if (functions.isValueNull(status) && status.contains("success"))
            {
                respStatus="Y";
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_TOKEN_GENERATION);
            }
            else
            {
                respStatus="N";
                if(functions.isValueNull(commonValidatorVO.getErrorMsg())){
                    errorCodeVO.setApiDescription(commonValidatorVO.getErrorMsg());
                }else{
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TOKEN_CREATION_FAILED);
                }
            }
            try
            {
                //checkSum = Checksum.generateChecksumForStandardKit(getValue(commonValidatorVO.getTrackingid()),getValue(commonValidatorVO.getTransDetailsVO().getOrderId()), getValue(commonValidatorVO.getTransDetailsVO().getAmount()), getValue(respStatus),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));
                checkSum = Checksum.generateChecksumForCardRegistration(getValue(commonValidatorVO.getMerchantDetailsVO().getMemberId()),getValue(commonValidatorVO.getMerchantDetailsVO().getKey()), response);
            }
            catch (NoSuchAlgorithmException e)
            {
                respStatus = "N";
            }
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("<HTML>");
            stringBuffer.append("<HEAD> <script language=\"javascript\">" +
                    "function Load(){" +
                    "document.form.submit();" +
                    "}" +
                    " </script>");
            stringBuffer.append("</HEAD>");
            stringBuffer.append("<BODY onload=Load()>");
            stringBuffer.append("<form name=\"form\" method=\"post\" action=\""+commonValidatorVO.getTransDetailsVO().getRedirectUrl()+"\">");
            if(functions.isValueNull(token))
            {
                //stringBuffer.append("<input type=\"hidden\" name=\"token\" value=\"" + token + "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"registrationId\" value=\"" + token + "\">");
            }
            stringBuffer.append("<input type=\"hidden\" name=\"status\" value=\""+getValue(respStatus)+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"resultDescription\" value=\""+getValue(errorCodeVO.getApiDescription())+"\">");
            if(null!=commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                stringBuffer.append("<input type=\"hidden\" name=\"cardBin\" value=\""+cardBin+ "\">");
                stringBuffer.append("<input type=\"hidden\" name=\"cardLast4Digits\" value=\""+cardLast4Digits+ "\">");
            }
            stringBuffer.append("<input type=\"hidden\" name=\"cardType\" value=\""+commonValidatorVO.getCardType()+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"timestamp\" value=\""+getValue(timeStamp)+"\">");
            if (functions.isValueNull(firstName))
                stringBuffer.append("<input type=\"hidden\" name=\"firstname\" value=\""+firstName+"\">");

            if (functions.isValueNull(lastName))
                stringBuffer.append("<input type=\"hidden\" name=\"lastname\" value=\""+lastName+"\">");
            if(functions.isValueNull(customerId))
                stringBuffer.append("<input type=\"hidden\" name=\"customerId\" value=\""+customerId+"\">");
            stringBuffer.append("<input type=\"hidden\" name=\"checksum\" value=\""+getValue(checkSum)+"\">");



            stringBuffer.append("</form>");
            stringBuffer.append("</BODY>");
            stringBuffer.append("</HTML>");
            log.debug(stringBuffer);
            transactionLogger.error("automatic redirect---" + stringBuffer);
            res.setContentType("text/html;charset=UTF-8");
            res.setCharacterEncoding("UTF-8");
            printWriter = res.getWriter();
            printWriter.print(stringBuffer);

        }
        catch (IOException e)
        {
            log.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            transactionLogger.error("IO EXCEPTION in PayProcessController while submitting the form",e);
            throw new SystemError(e.getMessage());
        }
    }

    public void redirectToServlet(PrintWriter pWriter, Hashtable value,String ctoken,String servletName)
    {

        String key = "";
        String val = "";

        //redirecting to EcoreServlet
        pWriter.println("<HTML>");
        pWriter.println("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        pWriter.println("</HEAD>");
        pWriter.println("<BODY onload=Load()>");
        pWriter.println("<form name=\"form\" action=\""+servletName+"?ctoken="+ctoken+"\" method=\"post\" >");
        Enumeration enu = value.keys();

        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            val = value.get(key).toString();
            pWriter.println("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\" >");
            log.debug("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\"");
            transactionLogger.debug("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\"");
        }

        pWriter.println("</form>");
        pWriter.println("</BODY>");
        pWriter.println("</HTML>");

        return;
    }


    /**
     * Generic Method for redirection to any servlet page
     * @param pWriter
     * @param req
     * @param ctoken
     * @param servletName
     */
    //XxxProcess
    public void redirectToServlet(PrintWriter pWriter, HttpServletRequest req,String ctoken,String servletName)
    {

        String key = "";
        String val = "";

        //redirecting to EcoreServlet
        pWriter.println("<HTML>");
        pWriter.println("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        pWriter.println("</HEAD>");
        pWriter.println("<BODY onload=Load()>");
        pWriter.println("<form name=\"form\" action=\""+servletName+"?ctoken="+ctoken+"\" method=\"post\" >");
        Enumeration enu = req.getParameterNames();

        while (enu.hasMoreElements())
        {

            key = (String) enu.nextElement();
            val = ESAPI.encoder().encodeForHTML(req.getParameter(key));
            if (val == null)
                val = "";
            pWriter.println("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\" >");
            log.debug("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\"");
            transactionLogger.debug("<input type=hidden name=\""+key.toString()+"\" value=\""+val.toString()+"\"");
        }
        pWriter.println("</form>");
        pWriter.println("</BODY>");
        pWriter.println("</HTML>");

        return;
    }

    public void getWaitPage(PrintWriter pWriter, HttpServletRequest request,String ctoken,String trackingId,int memberId,String version,Hashtable otherdetails,String PROXYSCHEME,String PROXYHOST,String PROXYPORT,String POSTURL)
    {
        StringBuffer queryString = new StringBuffer("");
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements())
        {
            String name = (String) enu.nextElement();
            String value = null;
            try{
                value= ESAPI.encoder().encodeForURL(request.getParameter(name));
            }
            catch(EncodingException e)
            {

            }
            if (name.startsWith("TMPL_"))
                queryString.append("&").append(name).append("=").append(value);
        }
        try
        {
            String newChecksum = Checksum.generateNewChecksum(trackingId, memberId + "");

            /*String detailsUrl = PROXYSCHEME+ "://" + PROXYHOST + ":" +PROXYPORT+ "?trackingid=" + ESAPI.encoder().encodeForURL(trackingId) + "&id=" + memberId + "&newchecksum=" + newChecksum + "&version=" + version + "&TEMPLATE=" + request.getParameter("TEMPLATE") + "&redirecturl=" + ESAPI.encoder().encodeForURL(request.getParameter("REDIRECTURL")) +"&ctoken="+ctoken+ "&POSTURL=" + POSTURL + queryString;
            otherdetails.put("CONTINUE", "<br><br>If this page takes longer than 60 seconds, please <a href=" + detailsUrl + ">click  here</a> to confirm your payment.");
            otherdetails.put("DETAILSURL", detailsUrl);*/

            pWriter.println(Template.getWaitPage(memberId + "", otherdetails));

        }
        catch (SystemError ex)
        {
            log.error("System Error ::",ex);
            transactionLogger.error("System Error ::",ex);
            pWriter.println("Error in Template " + ex.toString());
        }
        catch (Exception ex)
        {
            log.error("Internal System Error ::::::",ex);
            transactionLogger.error("Internal System Error ::::::",ex);
            pWriter.println("Error in Checksum Generation " + ex.toString());
        }
    }

    public void getNewWaitPage(PrintWriter pWriter)
    {
        try
        {
            pWriter.println(Template.getNewWaitPage());
        }
        catch (SystemError ex)
        {
            log.error("System Error ::",ex);
            transactionLogger.error("System Error ::",ex);
            pWriter.println("Error in Template " + ex.toString());
        }
        catch (Exception ex)
        {
            log.error("Internal System Error ::::::",ex);
            transactionLogger.error("Internal System Error ::::::",ex);
            pWriter.println("Error in Checksum Generation " + ex.toString());
        }
    }

    public void redirectToPost(PrintWriter pWriter, HttpServletRequest req,String ctoken,String POSTURL,String message,String checksum,String transactionStatus,String txnamount,String chargeamt)
    {

        pWriter.println("<form name=\"postpay\" action=\""+ POSTURL +"?ctoken="+ctoken+ "\" method=\"post\" >");
        pWriter.println("<input type=\"hidden\" name=\"status\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(transactionStatus) + "\">");
        pWriter.println("<input type=\"hidden\" name=\"message\" value=\"" + message + "\">");

        pWriter.println("<input type=\"hidden\" name=\"amount\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(txnamount) + "\">");
        pWriter.println("<input type=\"hidden\" name=\"chargeamt\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(chargeamt) + "\">");
        pWriter.println("<input type=\"hidden\" name=\"checksum\" value=\"" + checksum + "\">");
        pWriter.println("<input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\">");
        Enumeration enumParams = req.getParameterNames();
        while (enumParams.hasMoreElements())
        {
            String name = (String) enumParams.nextElement();
            pWriter.println("<input type=\"hidden\" name=\"" + name + "\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(req.getParameter(name)) + "\">");
        }
        pWriter.println("</form>");
        pWriter.println("<script language=\"javascript\">");
        pWriter.println("document.postpay.submit();");
        pWriter.println("</script>");
        pWriter.println("</body>");
        pWriter.println("</html>");

    }



    //XxxServlet
    public String getTCMessage(String memberId)
    {
        Merchants merchants = new Merchants();
        String msg = "";
        if (merchants.ifTCRequired(memberId))
        {
            String siteName = "";
            try
            {
                siteName = merchants.getColumn("sitename", memberId);
            }
            catch (SystemError ex)
            {
                log.error("exception",ex);
                transactionLogger.error("exception",ex);
            }
            msg = "<input type=\"checkbox\" value=\"tc\" name=\"TC\"> I have read the Terms and condition of " + siteName + " and I accept the same . Also I have necessary prescription of the product purchased.";
        }
        else
        {
            msg="<input type=\"checkbox\" value=\"tc\" name=\"TC\"> I have read the Terms and condition and I accept the same.";
        }
        return msg;
    }


    /**
     * Validating ctoken from session and request
     * @param req
     * @param pWriter
     * @return
     */
    //XxxServlet
    public String validateCtoken(HttpServletRequest req,PrintWriter pWriter)
    {
        String ctoken =null;
        HttpSession session = req.getSession();

        if(session== null)
        {
            pWriter.print(Functions.ShowMessage("Message", "Your session is Expire."));
            return ctoken;
        }
        if(session !=null)
        {
            ctoken =  (String)session.getAttribute("ctoken");
            log.debug("CSRF token from session"+ctoken);
            transactionLogger.debug("CSRF token from session"+ctoken);
        }

        if(ctoken!=null && !ctoken.equals("") && !req.getParameter("ctoken").equals(ctoken))
        {
            log.debug("CSRF token not match ");
            transactionLogger.debug("CSRF token not match ");
            pWriter.print(Functions.ShowMessage("Invalid Request","UnAuthorized member"));
        }
        return ctoken;
    }


    public String getCookie(HttpServletRequest request, HttpServletResponse response) throws SystemError
    {

        log.debug("Inside getCookie ");
        transactionLogger.debug("Inside getCookie ");

        Cookie cook[] = request.getCookies();
        String name = null;
        String value = null;
        if (cook != null)
        {
            for (int i = 0; i < cook.length; i++)
            {
                name = cook[i].getName();
                if (name.equals("mid"))
                {
                    value = cook[i].getValue();
                    break;
                }
            }
        }
        log.debug("leaving  getCookie " + value);
        transactionLogger.debug("leaving  getCookie " + value);
        return value;
    }


    public String getBoiledName(String name)
    {

        log.debug("Inside getBoiledName");
        transactionLogger.debug("Inside getBoiledName");
        /*if (Functions.parseData(name) == null)
            return name;

        int index = 0;
        String temp = "", boiledName = "";
        for (int i = 1; i <= name.length(); i++)
        {
            temp = name.substring(index, i).toLowerCase();
            if (unboilchar.contains(temp))
                boiledName = boiledName + temp;
            index = i;
        }

        log.debug("leaving getBoiledName");
        transactionLogger.debug("leaving getBoiledName");*/

        return name;
    }
    public String generateStatusChecksum(String toid,String description,String trackingid,String key) throws NoSuchAlgorithmException
    {
        String str=toid+"|"+description+"|"+trackingid+"|"+key;
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        return generatedCheckSum;
    }
    private String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    public StringBuffer getValueForPostTemplate(HttpServletRequest req)
    {
        StringBuffer templatevar = new StringBuffer();
        Enumeration enu = req.getParameterNames();

        while (enu.hasMoreElements())
        {
            String name = (String) enu.nextElement();

            if (name.equals("CARDHOLDER"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("TMPL_LOGOHEIGHT"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("TMPL_RESELLERID"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("TMPL_AMOUNT"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("TMPL_TABLEBG"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("TMPL_CURRENCY"))
                templatevar.append("&" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("emailaddr"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("street"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("city"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("state"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("zip"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("telnocc"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("telno"))
                templatevar.append("&" + "TMPL_" + name + "=" + URLEncoder.encode(req.getParameter(name)));

            else if (name.equals("country"))
                templatevar.append("&" + "TMPL_" + req.getParameter(name) + "=SELECTED");
        }
        return templatevar;
    }


    public boolean noOfTries(HttpServletRequest req,HttpSession session)
    {
        boolean  triesOver = false;
        String sessionID = req.getParameter("TOID") + "#~#" + req.getParameter("DESCRIPTION");
        if (session.getAttribute(sessionID) != null)
        {
            int onOfTries = Integer.parseInt((String) session.getAttribute(sessionID));
            onOfTries++;
            session.setAttribute(sessionID, "" + onOfTries);
            if (onOfTries >= 2)
            {
                triesOver = true;
            }
        }
        else
        {
            session.setAttribute(sessionID, "1");
        }

        return triesOver;
    }


    public void calCheckSumAndWriteStatusForSale(PrintWriter pWriter, String trackingId, String description, BigDecimal amount, String status, String statusMsg, String key, String checksumAlgo,String requestType,String billingDiscriptor,String token,String fraudScore,String responseType)
    {
        log.debug("ResponseType---"+responseType);
        String checkSum = null;
        String amountStr = "";
        if (amount != null)
        {
            amountStr = amount.toString();
        }
        if(billingDiscriptor==null)
        {
            billingDiscriptor=" ";
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amountStr, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForSale()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_SALE);
        }

        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderId",description);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("trackingId",trackingId);
            responseMap.put("amount",amountStr);
            responseMap.put("fraudscore",fraudScore);
            responseMap.put("billingdescriptor",billingDiscriptor);
            responseMap.put("token",token);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"sale"));
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderId",description);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("trackingId",trackingId);
            responseMap.put("amount",amountStr);
            responseMap.put("fraudscore",fraudScore);
            responseMap.put("billingdescriptor",billingDiscriptor);
            responseMap.put("token",token);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
        }
        else
        {
            pWriter.write(description + " : " + status + " : " + statusMsg + " : " + trackingId + " : " + amountStr + " : " + checkSum + " : " + billingDiscriptor + " : "+token+":"+fraudScore);
        }

    }
    public void calCheckSumAndWriteSaleFullResponse(PrintWriter pWriter,String orderId,String status,String statusMsg,String trackingId,String amount,
                                                    String authCode,String resultCode,String resultDescription,String cardSource,String cardIssuerName,
                                                    String eci,String eciDescription,String cvvResult,String txAcqId,String validationDescription,String token,
                                                    String cardCountryCode,String fraudScore,String billingDiscriptor,String bankTranDate,String key, String checksumAlgo,String responseType)
    {
        log.debug("ResponseType---"+responseType);
        String checkSum = null;
        if(billingDiscriptor==null)
        {
            billingDiscriptor=" ";
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(orderId, amount, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg= e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteSaleFullResponse()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_SALE);
        }
        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status",status);
            responseMap.put("statusdescription",statusMsg);
            responseMap.put("trackingid",trackingId);
            responseMap.put("amount",amount);
            responseMap.put("authcode",authCode);
            responseMap.put("resultcode",resultCode);
            responseMap.put("resultdescription",resultDescription);
            responseMap.put("cardsource",cardSource);
            responseMap.put("cardissuername",cardIssuerName);
            responseMap.put("eci",eci);
            responseMap.put("ecidescription",eciDescription);
            responseMap.put("cvvresult",cvvResult);
            responseMap.put("banktransid",txAcqId);
            responseMap.put("validationdescription",validationDescription);
            responseMap.put("cardcountrycode",cardCountryCode);
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate",bankTranDate);
            responseMap.put("token",token);
            responseMap.put("fraudscore",fraudScore);
            pWriter.write(WriteXMLResponse.writeFullSaleResponse(responseMap));
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status",status);
            responseMap.put("statusdescription",statusMsg);
            responseMap.put("trackingid",trackingId);
            responseMap.put("amount",amount);
            responseMap.put("authcode",authCode);
            responseMap.put("resultcode",resultCode);
            responseMap.put("resultdescription",resultDescription);
            responseMap.put("cardsource",cardSource);
            responseMap.put("cardissuername",cardIssuerName);
            responseMap.put("eci",eci);
            responseMap.put("ecidescription",eciDescription);
            responseMap.put("cvvresult",cvvResult);
            responseMap.put("banktransid",txAcqId);
            responseMap.put("validationdescription",validationDescription);
            responseMap.put("cardcountrycode",cardCountryCode);
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate",bankTranDate);
            responseMap.put("token",token);
            responseMap.put("fraudscore",fraudScore);
            pWriter.write(WriteJSONResponse.writeFullSaleResponse(responseMap));
        }
        else
        {
            pWriter.write(orderId+":" + status + ":" + statusMsg + ":" + trackingId + ":" + amount +
                    ":"+authCode+":"+resultCode+" : "+resultDescription+":"+cardSource+":"+cardIssuerName+":"+eci+
                    ":"+eciDescription+":"+cvvResult+":"+validationDescription+":"+cardCountryCode+":"+ checkSum +
                    ":" + billingDiscriptor + ":"+txAcqId+":"+bankTranDate+" : "+token+":"+fraudScore);
        }
    }

    public void calCheckSumAndWriteSaleFullResponseForSale(PrintWriter pWriter,DirectCommResponseVO directCommResponseVO,String orderId,String amount,String key, String checksumAlgo,String responseType)
    {
        String checkSum = null;
        String billingDiscriptor =directCommResponseVO.getDescriptor();
        String status = directCommResponseVO.getCommStatus();
        String statusMsg = directCommResponseVO.getCommStatusMessage();
        log.debug("status msg----"+statusMsg);
        if (billingDiscriptor == null || "N".equalsIgnoreCase(status))
        {
            billingDiscriptor=" ";
        }
        try
        {

            checkSum = Checksum.generateChecksumV2(orderId, amount, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg= e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteSaleFullResponseForSale()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_SALE);
        }
        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status", status);
            responseMap.put("statusdescription", statusMsg);
            responseMap.put("trackingid", directCommResponseVO.getTrackingid());
            responseMap.put("amount",amount);
            responseMap.put("authcode", directCommResponseVO.getAuthCode());
            responseMap.put("resultcode", directCommResponseVO.getResultCode());
            responseMap.put("resultdescription", directCommResponseVO.getResultDescription());
            responseMap.put("cardsource", directCommResponseVO.getCardSource());
            responseMap.put("cardissuername", directCommResponseVO.getCardIssuerName());
            responseMap.put("eci", directCommResponseVO.getEci());
            responseMap.put("ecidescription", directCommResponseVO.getEciDescription());
            responseMap.put("cvvresult", directCommResponseVO.getCvvResult());
            responseMap.put("banktransid", directCommResponseVO.getTxAcqId());
            responseMap.put("validationdescription", directCommResponseVO.getValidationDescription());
            responseMap.put("cardcountrycode", directCommResponseVO.getCardCountryCode());
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate", directCommResponseVO.getBankTransDate());
            responseMap.put("token", directCommResponseVO.getToken());
            responseMap.put("fraudscore", directCommResponseVO.getFraudScore());
            pWriter.write(WriteXMLResponse.writeFullSaleResponse(responseMap));
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status", status);
            responseMap.put("statusdescription", statusMsg);
            responseMap.put("trackingid", directCommResponseVO.getTrackingid());
            responseMap.put("amount",amount);
            responseMap.put("authcode", directCommResponseVO.getAuthCode());
            responseMap.put("resultcode", directCommResponseVO.getResultCode());
            responseMap.put("resultdescription", directCommResponseVO.getResultDescription());
            responseMap.put("cardsource", directCommResponseVO.getCardSource());
            responseMap.put("cardissuername", directCommResponseVO.getCardIssuerName());
            responseMap.put("eci", directCommResponseVO.getEci());
            responseMap.put("ecidescription", directCommResponseVO.getEciDescription());
            responseMap.put("cvvresult", directCommResponseVO.getCvvResult());
            responseMap.put("banktransid", directCommResponseVO.getTxAcqId());
            responseMap.put("validationdescription", directCommResponseVO.getValidationDescription());
            responseMap.put("cardcountrycode", directCommResponseVO.getCardCountryCode());
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate", directCommResponseVO.getBankTransDate());
            responseMap.put("token", directCommResponseVO.getToken());
            responseMap.put("fraudscore", directCommResponseVO.getFraudScore());
            responseMap.put("rulestriggered", directCommResponseVO.getRulesTriggered());
            pWriter.write(WriteJSONResponse.writeFullSaleResponse(responseMap));
        }
        else
        {
            String authcode = "";
            String resultcode = "";
            String resultDescription = "";
            String cardsource = "";
            String cardIssuerName = "";
            String eci = "";
            String eciDescription ="";
            String cvv = "";
            String validationDesc = "";
            String cardCountryCode = "";
            String txacid ="";
            String bankTransDate = "";
            String bankTransID = "";
            String token = "";
            String fraudScore = "";
            String refundAmt="";
            if(functions.isValueNull(directCommResponseVO.getAuthCode()))
                authcode = directCommResponseVO.getAuthCode();
            if(functions.isValueNull(directCommResponseVO.getResultCode()))
                resultcode = directCommResponseVO.getResultCode();
            if(functions.isValueNull(directCommResponseVO.getResultDescription()))
                resultDescription = directCommResponseVO.getResultDescription();
            if(functions.isValueNull(directCommResponseVO.getCardSource()))
                cardsource = directCommResponseVO.getCardSource();
            if(functions.isValueNull(directCommResponseVO.getCardIssuerName()))
                cardIssuerName = directCommResponseVO.getCardIssuerName();
            if(functions.isValueNull(directCommResponseVO.getEci()))
                eci = directCommResponseVO.getEci();
            if(functions.isValueNull(directCommResponseVO.getEciDescription()))
                eciDescription = directCommResponseVO.getEciDescription();
            if(functions.isValueNull(directCommResponseVO.getCvvResult()))
                cvv = directCommResponseVO.getCvvResult();
            if(functions.isValueNull(directCommResponseVO.getValidationDescription()))
                validationDesc = directCommResponseVO.getValidationDescription();
            if(functions.isValueNull(directCommResponseVO.getCardCountryCode()))
                cardCountryCode = directCommResponseVO.getCardCountryCode();
            if(functions.isValueNull(directCommResponseVO.getBankTransId()))
                bankTransID = directCommResponseVO.getBankTransId();
            if(functions.isValueNull(directCommResponseVO.getTxAcqId()))
                txacid = directCommResponseVO.getTxAcqId();
            if(functions.isValueNull(directCommResponseVO.getBankTransDate()))
                bankTransDate = directCommResponseVO.getBankTransDate();
            if(functions.isValueNull(directCommResponseVO.getToken()))
                token = directCommResponseVO.getToken();
            if(functions.isValueNull(directCommResponseVO.getFraudScore()))
                fraudScore = directCommResponseVO.getFraudScore();
            pWriter.write(orderId+":" + status + ":" + statusMsg + ":" + directCommResponseVO.getTrackingid() + ":" + amount +
                    ":"+authcode+":"+resultcode+" : "+resultDescription+":"+cardsource+":"+cardIssuerName+":"+eci+
                    ":"+eciDescription+":"+cvv+":"+validationDesc+":"+cardCountryCode+":"+ checkSum +
                    ":" + bankTransID + ":"+txacid+":"+bankTransDate+" : "+token+":"+fraudScore);
        }
    }


    public void calCheckSumAndWriteStatusForRebill(PrintWriter pWriter, String trackingId, String description, String amount, String status, String errorMsg, String key, String checksumAlgo,String billingDiscriptor)
    {
        String checkSum = null;
        String amountStr = "";
        if (amount != null)
        {
            amountStr = amount;
        }
        if(billingDiscriptor==null)
        {
            billingDiscriptor=" ";
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amountStr, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("No such algorithm error",e);
            status = "N";
            String statusMsg = e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRebill()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.MANUAL_REBILL);
        }
        pWriter.write(trackingId+ " : " + description  + " : " + amountStr + " : " +status + " : " + errorMsg + " : " + checkSum + " : " + billingDiscriptor);
    }

    public void calCheckSumAndWriteStatusForRebill(PrintWriter pWriter,ManualRebillResponseVO manualRebillResponseVO,CommonValidatorVO commonValidatorVO)
    {
        String responseType="";
        String responseLength="";
        String secretKey = "";
        if(commonValidatorVO.getMerchantDetailsVO() != null)
        {
            responseType = commonValidatorVO.getMerchantDetailsVO().getResponseType();
            responseLength = commonValidatorVO.getMerchantDetailsVO().getResponseLength();
            secretKey = commonValidatorVO.getMerchantDetailsVO().getKey();

            log.debug("ResponseType---"+responseType);
            log.debug("ResponseLength---"+responseLength);
            log.debug("secretKey---" + secretKey);
        }

        String trackingId = "";
        String description = "";
        String amount = "";
        String errorMsg = "";
        String checkSum = "";
        String status = "";
        String billingDiscriptor = "";

        if (functions.isValueNull(manualRebillResponseVO.getTrackingId()))
            trackingId = manualRebillResponseVO.getTrackingId();
        description = commonValidatorVO.getTransDetailsVO().getOrderId();
        amount = commonValidatorVO.getTransDetailsVO().getAmount();
        errorMsg = manualRebillResponseVO.getErrorMessage();
        checkSum = manualRebillResponseVO.getChecksum();
        if(functions.isValueNull(manualRebillResponseVO.getStatus()))
            status = manualRebillResponseVO.getStatus();
        if (functions.isValueNull(manualRebillResponseVO.getBillingDescriptor()))
            billingDiscriptor = manualRebillResponseVO.getBillingDescriptor();

        if(manualRebillResponseVO.getChecksum()==null || manualRebillResponseVO.getChecksum().equals(""))
        {
            try
            {
                checkSum = Checksum.generateChecksumV2(description, amount, status, secretKey, "");
            }
            catch (NoSuchAlgorithmException e)
            {
                log.error("No such algorithm error",e);
                //status = "N";
                String statusMsg = e.getMessage();
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRebill()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.MANUAL_REBILL);
            }
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingId",trackingId);
                responseMap.put("description",description);
                responseMap.put("amount",amount);
                responseMap.put("status",status);
                responseMap.put("errorMsg",errorMsg);
                responseMap.put("checkSum",checkSum);
                responseMap.put("billingDiscriptor",billingDiscriptor);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("trackingId",trackingId);
                responseMap.put("description",description);
                responseMap.put("amount",amount);
                responseMap.put("status",status);
                responseMap.put("errorMsg",errorMsg);
                responseMap.put("checkSum",checkSum);
                responseMap.put("billingDiscriptor",billingDiscriptor);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingId",trackingId);
                responseMap.put("description",description);
                responseMap.put("amount",amount);
                responseMap.put("status",status);
                responseMap.put("errorMsg",errorMsg);
                responseMap.put("checkSum",checkSum);
                responseMap.put("billingDiscriptor",billingDiscriptor);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "Rebill"));
            }
            else
            {
                responseMap.put("trackingId",trackingId);
                responseMap.put("description",description);
                responseMap.put("amount",amount);
                responseMap.put("status",status);
                responseMap.put("errorMsg",errorMsg);
                responseMap.put("checkSum",checkSum);
                responseMap.put("billingDiscriptor",billingDiscriptor);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "Rebill"));
            }
        }
        else
        {

            if(functions.isValueNull(manualRebillResponseVO.getTrackingId()))
                trackingId = manualRebillResponseVO.getTrackingId();
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                description = commonValidatorVO.getTransDetailsVO().getOrderId();
            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                amount = commonValidatorVO.getTransDetailsVO().getAmount();
            if(functions.isValueNull(manualRebillResponseVO.getStatus()))
                status = manualRebillResponseVO.getStatus();
            if(functions.isValueNull(manualRebillResponseVO.getErrorMessage()))
                errorMsg = manualRebillResponseVO.getErrorMessage();
            if(functions.isValueNull(manualRebillResponseVO.getBillingDescriptor()))
                billingDiscriptor = manualRebillResponseVO.getBillingDescriptor();

            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write(trackingId + " : " + description + " : " + amount + " : " + status + " : " + errorMsg + " : " + checkSum + " : " + billingDiscriptor);
            else
                pWriter.write(trackingId + " : " + description + " : " + amount + " : " + status + " : " + errorMsg + " : " + checkSum + " : " + billingDiscriptor);
        }
        transactionLogger.error("Manual Recurring Response---"+trackingId + " : " + description + " : " + amount + " : " + status + " : " + errorMsg + " : " + checkSum + " : " + billingDiscriptor);
    }

    public void calCheckSumAndWriteStatusForSale(PrintWriter pWriter, String trackingId, String description, String amount, String status, String statusMsg, String key, String checksumAlgo,String requestType,String billingDiscriptor,String token,String responseType)
    {
        log.debug("ResponseType---"+responseType);
        String checkSum = null;
        String amountStr = "";
        if (amount != null)
        {
            amountStr = amount;
        }
        if(billingDiscriptor==null)
        {
            billingDiscriptor=" ";
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amountStr, status, key, checksumAlgo);
            transactionLogger.debug("checksum---"+checkSum);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForSale()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_SALE);
        }

        if(ResponseType.XML.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("fraudscore","");
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("billingdescriptor",billingDiscriptor);
            responseMap.put("amount",amountStr);
            responseMap.put("orderId",description);
            responseMap.put("checksum",checkSum);
            responseMap.put("status",status);
            responseMap.put("trackingId",trackingId);
            responseMap.put("token",token);
            pWriter.write(WriteXMLResponse.writeSaleResponse(responseMap));
            log.debug("responseMap json---"+responseMap);
        }
        else if(ResponseType.JSON.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderId",description);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("trackingId",trackingId);
            responseMap.put("amount",amountStr);
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdescriptor",billingDiscriptor);
            responseMap.put("token",token);
            responseMap.put("fraudscore","");
            pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            log.debug("responseMap xml---"+responseMap);
        }
        else
        {
            pWriter.write(description + " : " + status + " : " + statusMsg + " : " + trackingId + " : " + amountStr + " : " + checkSum + " : " + billingDiscriptor + " : "+token);
            log.debug("text---"+description + " : " + status + " : " + statusMsg + " : " + trackingId + " : " + amountStr + " : " + checkSum + " : " + billingDiscriptor + " : "+token);
        }
    }

    public void calCheckSumAndWriteStatus(PrintWriter pWriter, String description, String trackingId, String amount, String captureamount, String status, String statusMsg, String key, String checksumAlgo, String requestType,String responseType)
    {
        log.error("ResponseType status---"+responseType);
        String checkSum = null;
        String amountStr = "";
        if (amount != null)
        {
            amountStr = amount;
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java","calCheckSumAndWriteStatusForSale()",null,"Transaction","No Algorithm Exception:::",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture from Direct Kit");
            status = "N";
            statusMsg = e.getMessage();
        }

        log.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamount + " : " + checkSum);
        transactionLogger.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamount + " : " + checkSum);

        if(ResponseType.XML.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("authamount",amountStr.toString());
            responseMap.put("captureamount",captureamount);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"status"));
            transactionLogger.debug("responseMap xml---"+WriteXMLResponse.writeXMLResponse(responseMap,"status"));
        }
        else if(ResponseType.JSON.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("authamount",amountStr.toString());
            responseMap.put("captureamount",captureamount);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            transactionLogger.debug("responseMap json---"+WriteJSONResponse.writeJSONResponse(responseMap));
        }
        else
        {
            String desc = "";
            String trackingid = "";
            String amount1 = "";
            String captureamt = "";
            if(functions.isValueNull(description))
                desc = description;
            if(functions.isValueNull(trackingId))
                trackingid = trackingId;
            if(functions.isValueNull(amountStr))
                amount1 = amountStr;
            if(functions.isValueNull(captureamount))
                captureamt = captureamount;

            pWriter.write(desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount1 + " : " + captureamt + " : " + checkSum);
            log.debug("text---"+desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount1 + " : " + captureamt + " : " + checkSum);

        }
    }

    public void calCheckSumAndWriteStatus(PrintWriter pWriter,TransactionDetailsVO transactionDetailsVO, String statusMsg, String key, String checksumAlgo,String responseType)
    {
        log.debug("ResponseType status---"+responseType);
        String checkSum = null;
        String amountStr = "";

        String amount = transactionDetailsVO.getAmount();
        String description = transactionDetailsVO.getDescription();
        String trackingId = transactionDetailsVO.getTrackingid();
        String status = transactionDetailsVO.getStatus();
        String captureamount = transactionDetailsVO.getCaptureAmount();
        String refundAmount = transactionDetailsVO.getRefundAmount();
        String payoutamount = transactionDetailsVO.getPayoutamount();
        if (amount != null)
        {
            amountStr = amount;
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description,amount, trackingId, key, checksumAlgo);
            transactionLogger.debug("checksum---"+checkSum);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java","calCheckSumAndWriteStatusForSale()",null,"Transaction","No Algorithm Exception:::",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture from Direct Kit");
            status = "N";
            statusMsg = e.getMessage();
        }

            log.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamount + " : " +refundAmount+ ":" +payoutamount+ ":" + checkSum);
            transactionLogger.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamount + " : "+refundAmount+ ":" +payoutamount+ ":"+ checkSum);

        if(ResponseType.XML.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("authamount",amountStr.toString());
            responseMap.put("captureamount",captureamount);
            if (status.equalsIgnoreCase("reversed"))
            {
                responseMap.put("refundAmount", refundAmount);
            }
            if (status.equalsIgnoreCase("payoutsuccessful"))
            {
                responseMap.put("payoutamount", payoutamount);
            }
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"status"));
            pWriter.write("\n");
            transactionLogger.debug("responseMap xml---"+WriteXMLResponse.writeXMLResponse(responseMap,"status"));
        }
        else if(ResponseType.JSON.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("authamount",amountStr.toString());
            responseMap.put("captureamount",captureamount);
            if (status.equalsIgnoreCase("reversed"))
            {
                responseMap.put("refundAmount", refundAmount);
            }
            if (status.equalsIgnoreCase("payoutsuccessful"))
            {
                responseMap.put("payoutamount",payoutamount);
            }
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            pWriter.write("\n");
            transactionLogger.debug("responseMap json---"+WriteJSONResponse.writeJSONResponse(responseMap));
        }
        else
        {
            String desc = "";
            String trackingid = "";
            String captureamt = "";
            String payoutamt="";
            String authAmount="";
            String refundAmt = "";

            if (functions.isValueNull(description))
                desc = description;
            if (functions.isValueNull(trackingId))
                trackingid = trackingId;
            if (functions.isValueNull(amountStr))
                authAmount=amountStr;
            if (status.equalsIgnoreCase("capturesuccess")&& functions.isValueNull(captureamount))
            {
                captureamt = captureamount;
                pWriter.write(desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamt);
            }
            if (status.equalsIgnoreCase("reversed")&& functions.isValueNull(refundAmount)&& functions.isValueNull(captureamount))
            {
                refundAmt = refundAmount;
                captureamt = captureamount;
                pWriter.write(desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount + " : " + captureamt +" : "+refundAmt);
            }
            if (status.equalsIgnoreCase("payoutsuccessful")&& functions.isValueNull(payoutamount))
             {
                 payoutamt=payoutamount;
                 pWriter.write(desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount + " : " + payoutamt);
             }

           /* if (status.equalsIgnoreCase("reversed"))
            { log.debug("inside ----1785 ------>");
                pWriter.write(" : " + refundAmt);
            }
            if (status.equalsIgnoreCase("payoutsuccessful"))
            { log.debug("inside ----1789----------------->");
                pWriter.write(" : " + payoutamt);
            }*/

            pWriter.write( " : " + checkSum);
            pWriter.write("\n");
            log.error("text---" + desc + " : " + trackingid + " : " + status + " : " + statusMsg + " : " + amount + " : " + authAmount + " :" + captureamt + " : " + refundAmt + ":" + payoutamt + ":" + checkSum);

        }
    }

    public void calCheckSumAndWriteFullStatusResponse(PrintWriter pWriter, CommInquiryResponseVO commInquiryResponseVO, String key, String checksumAlgo, String requestType,String responseType)
    {String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(commInquiryResponseVO.getOrderId(), commInquiryResponseVO.getAuthAmount(),commInquiryResponseVO.getStatus(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java","calCheckSumAndWriteStatusForSale()",null,"Transaction","No Algorithm Exception:::",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture from Direct Kit");
            commInquiryResponseVO.setStatus("N");
            commInquiryResponseVO.setStatusMessage(e.getMessage());
        }
        log.debug(commInquiryResponseVO.getOrderId() + " : " + commInquiryResponseVO.getTrackingId()+ " : " + commInquiryResponseVO.getStatus() + " : " + commInquiryResponseVO.getStatusMessage() + " : " + commInquiryResponseVO.getAuthAmount() +
                " : " + commInquiryResponseVO.getCaptureAmount() + " : "+commInquiryResponseVO.getAuthCode()+ " : " + commInquiryResponseVO.getResultCode()+":"+commInquiryResponseVO.getResultDescription() +
                ":"+commInquiryResponseVO.getCardSource()+":"+commInquiryResponseVO.getCardIssuerName()+":"+commInquiryResponseVO.getEci()+":"+commInquiryResponseVO.getEciDescription()+
                ":"+commInquiryResponseVO.getCvvResult()+":"+commInquiryResponseVO.getBankTransID()+":"+commInquiryResponseVO.getCardCountryCode()+":"+commInquiryResponseVO.getValidationMsg()+":"+commInquiryResponseVO.getBankTransDate()+
                " : " + checkSum);

        transactionLogger.debug(commInquiryResponseVO.getOrderId() + " : " + commInquiryResponseVO.getTrackingId()+ " : " + commInquiryResponseVO.getStatus() + " : " + commInquiryResponseVO.getStatusMessage() + " : " + commInquiryResponseVO.getAuthAmount() +
                " : " + commInquiryResponseVO.getCaptureAmount() + " : "+commInquiryResponseVO.getAuthCode()+ " : " + commInquiryResponseVO.getResultCode()+":"+commInquiryResponseVO.getResultDescription() +
                ":"+commInquiryResponseVO.getCardSource()+":"+commInquiryResponseVO.getCardIssuerName()+":"+commInquiryResponseVO.getEci()+":"+commInquiryResponseVO.getEciDescription()+
                ":"+commInquiryResponseVO.getCvvResult()+":"+commInquiryResponseVO.getBankTransID()+":"+commInquiryResponseVO.getCardCountryCode()+":"+commInquiryResponseVO.getValidationMsg()+":"+commInquiryResponseVO.getBankTransDate()+
                " : " + checkSum);

        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",commInquiryResponseVO.getOrderId());
            responseMap.put("status",commInquiryResponseVO.getStatus());
            responseMap.put("statusdescription",commInquiryResponseVO.getStatusMessage());
            responseMap.put("trackingid",commInquiryResponseVO.getTrackingId());
            responseMap.put("authamount",commInquiryResponseVO.getAuthAmount());
            responseMap.put("captureamount",commInquiryResponseVO.getCaptureAmount());
            responseMap.put("refundAmount",commInquiryResponseVO.getRefundAmount());
            responseMap.put("newchecksum",checkSum);
            responseMap.put("authcode",commInquiryResponseVO.getAuthCode());
            responseMap.put("resultcode",commInquiryResponseVO.getResultCode());
            responseMap.put("resultdescription",commInquiryResponseVO.getResultDescription());
            responseMap.put("cardsource",commInquiryResponseVO.getCardSource());
            responseMap.put("cardissuer",commInquiryResponseVO.getCardIssuerName());
            responseMap.put("eci",commInquiryResponseVO.getEci());
            responseMap.put("ecidescription",commInquiryResponseVO.getEciDescription());
            responseMap.put("cvvresult",commInquiryResponseVO.getCvvResult());
            responseMap.put("banktansid",commInquiryResponseVO.getBankTransID());
            responseMap.put("cardcountrycode",commInquiryResponseVO.getCardCountryCode());
            responseMap.put("validationdescription",commInquiryResponseVO.getValidationMsg());
            responseMap.put("banktransdate",commInquiryResponseVO.getBankTransDate());
            pWriter.write(WriteXMLResponse.writeFullStatusResponse(responseMap));
            log.debug("responseMap json---"+responseMap);
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",commInquiryResponseVO.getOrderId());
            responseMap.put("status",commInquiryResponseVO.getStatus());
            responseMap.put("statusdescription",commInquiryResponseVO.getStatusMessage());
            responseMap.put("trackingid",commInquiryResponseVO.getTrackingId());
            responseMap.put("authamount",commInquiryResponseVO.getAuthAmount());
            responseMap.put("captureamount",commInquiryResponseVO.getCaptureAmount());
            responseMap.put("refundAmount",commInquiryResponseVO.getRefundAmount());
            responseMap.put("newchecksum",checkSum);
            responseMap.put("authcode",commInquiryResponseVO.getAuthCode());
            responseMap.put("resultcode",commInquiryResponseVO.getResultCode());
            responseMap.put("resultdescription",commInquiryResponseVO.getResultDescription());
            responseMap.put("cardsource",commInquiryResponseVO.getCardSource());
            responseMap.put("cardissuer",commInquiryResponseVO.getCardIssuerName());
            responseMap.put("eci",commInquiryResponseVO.getEci());
            responseMap.put("ecidescription",commInquiryResponseVO.getEciDescription());
            responseMap.put("cvvresult",commInquiryResponseVO.getCvvResult());
            responseMap.put("banktransid",commInquiryResponseVO.getBankTransID());
            responseMap.put("cardcountrycode",commInquiryResponseVO.getCardCountryCode());
            responseMap.put("validationdescription",commInquiryResponseVO.getValidationMsg());
            responseMap.put("banktransdate",commInquiryResponseVO.getBankTransDate());
            pWriter.write(WriteJSONResponse.writeFullJSONStatusResponse(responseMap));
            log.debug("responseMap xml---"+responseMap);
        }
        else
        {
            String authcode = "";
            String resultcode = "";
            String resultDescription = "";
            String cardsource = "";
            String cardIssuerName = "";
            String eci = "";
            String eciDescription ="";
            String cvv = "";
            String validationDesc = "";
            String cardCountryCode = "";
            String bankTransDate = "";
            String bankTransID = "";
            String statusMessage = "";
            String refundAmount = "";
            if(functions.isValueNull(commInquiryResponseVO.getAuthCode()))
                authcode = commInquiryResponseVO.getAuthCode();
            if(functions.isValueNull(commInquiryResponseVO.getResultCode()))
                resultcode = commInquiryResponseVO.getResultCode();
            if(functions.isValueNull(commInquiryResponseVO.getResultDescription()))
                resultDescription = commInquiryResponseVO.getResultDescription();
            if(functions.isValueNull(commInquiryResponseVO.getCardSource()))
                cardsource = commInquiryResponseVO.getCardSource();
            if(functions.isValueNull(commInquiryResponseVO.getCardIssuerName()))
                cardIssuerName = commInquiryResponseVO.getCardIssuerName();
            if(functions.isValueNull(commInquiryResponseVO.getEci()))
                eci = commInquiryResponseVO.getEci();
            if(functions.isValueNull(commInquiryResponseVO.getEciDescription()))
                eciDescription = commInquiryResponseVO.getEciDescription();
            if(functions.isValueNull(commInquiryResponseVO.getCvvResult()))
                cvv = commInquiryResponseVO.getCvvResult();
            if(functions.isValueNull(commInquiryResponseVO.getValidationMsg()))
                validationDesc = commInquiryResponseVO.getValidationMsg();
            if(functions.isValueNull(commInquiryResponseVO.getCardCountryCode()))
                cardCountryCode = commInquiryResponseVO.getCardCountryCode();
            if(functions.isValueNull(commInquiryResponseVO.getBankTransID()))
                bankTransID = commInquiryResponseVO.getBankTransID();
            if(functions.isValueNull(commInquiryResponseVO.getBankTransDate()))
                bankTransDate = commInquiryResponseVO.getBankTransDate();
            if(functions.isValueNull(commInquiryResponseVO.getStatusMessage()))
            {
                statusMessage = commInquiryResponseVO.getStatusMessage();
            }
            else
            {
                statusMessage=key;
            }
            if (functions.isValueNull(commInquiryResponseVO.getRefundAmount()))
              refundAmount= commInquiryResponseVO.getRefundAmount();
            pWriter.write(commInquiryResponseVO.getOrderId() + " : " + commInquiryResponseVO.getTrackingId()+ " : " + commInquiryResponseVO.getStatus() + " : " + statusMessage + " : " + commInquiryResponseVO.getAuthAmount() + " : "+refundAmount+
                    " : " + commInquiryResponseVO.getCaptureAmount() +  " : " + checkSum+" : "+authcode+ " : " + resultcode+":"+resultDescription +
                    ":"+cardsource+":"+cardIssuerName+":"+eci+":"+eciDescription+
                    ":"+cvv+":"+bankTransID+":"+cardCountryCode+":"+validationDesc+":"+bankTransDate);
        }
    }
    public void calCheckSumAndWriteStatusForVoid(PrintWriter pWriter, String description, String trackingId, String status, String statusMsg, String key, String checksumAlgo, String requestType,String responseType)
    {
        log.error("ResponseType---"+responseType);
        String checkSum = null;

        try
        {
            checkSum = Checksum.generateChecksumForVOID(description, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java","calCheckSumAndWriteStatusForSale()",null,"Transaction","No Algorithm Exception:::",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture from Direct Kit");
            status = "N";
            statusMsg = e.getMessage();
        }

        log.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + checkSum);
        transactionLogger.debug(description + " : " + trackingId+ " : " + status + " : " + statusMsg + " : " + checkSum);

        if(ResponseType.XML.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"Cancel/Void"));
        }
        else if(ResponseType.JSON.toString().equalsIgnoreCase(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("description",description);
            responseMap.put("trackingid",trackingId);
            responseMap.put("status",status);
            responseMap.put("statusDescription",statusMsg);
            responseMap.put("checksum",checkSum);
            pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
        }
        else
        {
            pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + checkSum);
        }
    }

    public void calCheckSumAndWriteFullResponseForVoid(PrintWriter pWriter, String description, String trackingId, String status, String statusMsg,String bankStatus,String resultCode,String resultDescription,String key, String checksumAlgo, String requestType,String responseType)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumForVOID(description, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java","calCheckSumAndWriteStatusForSale()",null,"Transaction","No Algorithm Exception:::",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),null,"Capture from Direct Kit");
            status = "N";
            statusMsg = e.getMessage();
        }
        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",description);
            responseMap.put("status",status);
            responseMap.put("statusdescription",statusMsg);
            responseMap.put("trackingid",trackingId);
            responseMap.put("newchecksum",checkSum);
            responseMap.put("bankstatus",bankStatus);
            responseMap.put("resultcode",resultCode);
            responseMap.put("resultdescription",resultDescription);
            pWriter.write(WriteXMLResponse.writeFullResponseForVoid(responseMap));
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",description);
            responseMap.put("status",status);
            responseMap.put("statusdescription",statusMsg);
            responseMap.put("trackingid",trackingId);
            responseMap.put("newchecksum",checkSum);
            responseMap.put("bankstatus",bankStatus);
            responseMap.put("resultcode",resultCode);
            responseMap.put("resultdescription",resultDescription);
            pWriter.write(WriteJSONResponse.writeFullJSONResponseForVoid(responseMap));
        }
        else
        {
            pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + checkSum+" : "+bankStatus+" : "+ resultCode+" : "+resultDescription);
        }
    }
    public void calCheckSumAndWriteStatusForRefund(PrintWriter pWriter, String trackingid, String amount, StringBuffer status, StringBuffer statusMsg, String key, String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType= merchantDetailsVO.getResponseType();
            responseLength= merchantDetailsVO.getResponseLength();
        }
        String checkSum = null;

        try
        {
            checkSum = Checksum.generateChecksumV2(trackingid, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRefund()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("refundamount", amount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("refundamount", amount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("refundamount", amount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"refund"));
            }
            else
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("refundamount", amount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap,"refund"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + amount + ":" + checkSum);
            else
                pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + amount + ":" + checkSum);
        }
    }
    public void calCheckSumAndWriteStatus(PrintWriter pWriter, String trackingid, String amount, StringBuffer status, StringBuffer statusMsg, String key, String checksumAlgo)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(trackingid, amount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
        }
        pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + amount + ":" + checkSum);
    }

    public void calCheckSumAndWriteStatusForCapture(PrintWriter pWriter, String trackingid, String captureamount, StringBuffer status, StringBuffer statusMsg, String key, String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();
        }

        String checkSum = null;

        try
        {
            checkSum = Checksum.generateChecksumV2(trackingid, captureamount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForCapture()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, "Capture from Direct Kit");
        }

        HashMap responseMap = new HashMap();
        if (ResponseType.JSON.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("captureamount", captureamount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("captureamount", captureamount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("captureamount", captureamount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "Capture"));
            }
            else
            {
                responseMap.put("trackingid", trackingid);
                responseMap.put("status", status.toString());
                responseMap.put("statusDescription", statusMsg.toString());
                responseMap.put("captureamount", captureamount.toString());
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "Capture"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + captureamount  + ":" + checkSum);
            else
                pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + captureamount  + ":" + checkSum);
        }

    }
    public void calCheckSumAndWriteFullResponseForCapture(PrintWriter pWriter, String trackingid, String captureamount, StringBuffer status, StringBuffer statusMsg,String bankStatus,String bankResultCode,String bankResultDescription,String lote,String key, String checksumAlgo,String requestType,String responseType)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(trackingid, captureamount, status.toString(), key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status.append("N");
            statusMsg.append(e.getMessage());
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForCapture()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, "Capture from Direct Kit");
        }
        if(ResponseType.XML.toString().equals(responseType)){
            HashMap responseMap = new HashMap();
            responseMap.put("trackingid",trackingid);
            responseMap.put("status",status.toString());
            responseMap.put("statusdescription",statusMsg.toString());
            responseMap.put("captureamount",captureamount.toString());
            responseMap.put("checkSum",checkSum);
            responseMap.put("bankstatus",bankStatus);
            responseMap.put("resultcode",bankResultCode);
            responseMap.put("resultdescription",bankResultDescription);
            responseMap.put("lote",lote);
            pWriter.write(WriteXMLResponse.writeFullResponseForCapture(responseMap));

        }
        else if(ResponseType.JSON.toString().equals(responseType)){
            HashMap responseMap = new HashMap();
            responseMap.put("trackingid",trackingid);
            responseMap.put("status",status.toString());
            responseMap.put("statusdescription",statusMsg.toString());
            responseMap.put("captureamount",captureamount.toString());
            responseMap.put("checksum",checkSum);
            responseMap.put("bankstatus",bankStatus);
            responseMap.put("resultcode",bankResultCode);
            responseMap.put("resultdescription",bankResultDescription);
            responseMap.put("lote",lote);
            pWriter.write(WriteJSONResponse.writeFullJSONResponseForCapture(responseMap));
        }
        else{
            pWriter.write(trackingid + ":" + status + ":" + statusMsg + ":" + captureamount + ":" + checkSum+
                    ":" + bankStatus + ":" + bankResultCode + ":" + bankResultDescription + ":" + lote);
        }
    }
    public String generateMD5ChecksumForRefund(String memberid, String trackingid, String refundamount, String key) throws PZTechnicalViolationException
    {
        String generatedCheckSum = "";
        try
        {
            String str = memberid + "|" + trackingid + "|" + refundamount + "|" + key;
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException nsa)
        {
            PZExceptionHandler.raiseTechnicalViolationException("TransactionUtility.java", "generateMD5ChecksumForRefund()", null, "Transaction", "No such Algoritham Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, nsa.getMessage(), nsa.getCause());
        }
        return generatedCheckSum;
    }


    public void getPartnerDetails(String toid,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            log.debug("Entering getMember Details");
            transactionLogger.debug("Entering getMember Details");
            String query = "SELECT partnerId,logoName,partnerName FROM partners WHERE partnerid IN (SELECT partnerId FROM members WHERE memberid=?)";
            log.debug("Partner Details Query---"+query);
            transactionLogger.debug("Partner Details Query---"+query);

            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setString(1,toid);
            ResultSet res = pstmt.executeQuery();
            log.debug("Old merchant Details is loading ");
            transactionLogger.debug("Old merchant Details is loading ");
            if (res.next())
            {
                commonValidatorVO.setParetnerId(res.getString("partnerId"));
                commonValidatorVO.setPartnerName(res.getString("partnerName"));
                commonValidatorVO.setLogoName(res.getString("logoName"));
            }

        }
        catch (SystemError se)
        {
            log.error(":::::"+se.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.java", "getPartnerDetails()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null, se.getMessage(), se.getCause());
            log.error("sql Exception",se);
            transactionLogger.error("sql Exception",se);
        }
        catch (SQLException e)
        {
            log.error(":::::"+e.getMessage());
            PZExceptionHandler.raiseDBViolationException("TransactionUtility.class", "getPartnerDetails()", null, "Transaction", "SQL Exception Thrown:::::", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
            log.error("System Error while getting the Partner Details", e);
            transactionLogger.error("System Error while getting the Partner Details", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    public String validateCtokenPfs(HttpServletRequest req,PrintWriter pWriter)
    {
        String ctoken =null;
        HttpSession session = req.getSession();

        if(session== null)
        {
            pWriter.print(Functions.ShowMessage("Message", "Your session is Expire."));
            return ctoken;
        }
        if(session !=null)
        {
            ctoken =  (String)session.getAttribute("ctoken");
            log.debug("CSRF token from session"+ctoken);
            transactionLogger.debug("CSRF token frcalculateCheckSumAndWriteStatusTokenTransactionom session"+ctoken);
        }

        if(ctoken!=null && !ctoken.equals("") && !req.getParameter("c").equals(ctoken))
        {
            log.debug("CSRF token not match ");
            transactionLogger.debug("CSRF token not match ");
            pWriter.print(Functions.ShowMessage("Invalid Request","UnAuthorized member"));
        }
        return ctoken;
    }

    public void calculateCheckSumAndWriteStatusTokenTransaction(PrintWriter pWriter, String description, String trackingId, String status, String statusMsg, String amount,String key,String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String responseType="";
        String responseLength="";

        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();

            log.debug("ResponseType---"+merchantDetailsVO.getResponseType());
            log.debug("ResponseLength---"+merchantDetailsVO.getResponseLength());
        }
        String checkSum = null;

        try
        {
            checkSum = Checksum.generateChecksumV2(description, amount, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRefund()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "TokenTransaction"));
            }
            else
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "TokenTransaction"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + amount + ":" + checkSum);
            else
                pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + amount + ":" + checkSum);
        }
    }


    public void calculateCheckSumAndWriteStatusTokenTransaction(PrintWriter pWriter, String description, String trackingId, String status, String statusMsg, String amount,String key,String checksumAlgo,String responseType, String responseLength)
    {
        String checkSum = null;

        try
        {
            checkSum = Checksum.generateChecksumV2(description, amount, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = e.getMessage();
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRefund()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "TokenTransaction"));
            }
            else
            {
                responseMap.put("orderid",description);
                responseMap.put("trackingid",trackingId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("resamount",amount);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "TokenTransaction"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + amount + ":" + checkSum);
            else
                pWriter.write(description + " : " + trackingId + " : " + status + " : " + statusMsg + " : " + amount + ":" + checkSum);
        }
    }
    public void calculateCheckSumAndWriteTokenTransactionFullResponse(PrintWriter pWriter, String orderId, String status, String statusMsg, String amount, String key, String checksumAlgo, String responseType, VTResponseVO vtResponseVO)
    {
        String checkSum = null;
        String billingDiscriptor =vtResponseVO.getBillingDescriptor();
        if (billingDiscriptor == null || "N".equalsIgnoreCase(status))
        {
            billingDiscriptor=" ";
        }
        try
        {

            checkSum = Checksum.generateChecksumV2(orderId, amount, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg= e.getMessage();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteSaleFullResponseForSale()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_SALE);
        }
        if(ResponseType.XML.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status", status);
            responseMap.put("statusdescription", statusMsg);
            responseMap.put("trackingid", vtResponseVO.getTrackingId());
            responseMap.put("amount",amount);
            responseMap.put("authcode", vtResponseVO.getAuthCode());
            responseMap.put("resultcode", vtResponseVO.getResultCode());
            responseMap.put("resultdescription", vtResponseVO.getResultDescription());
            responseMap.put("cardsource", vtResponseVO.getCardSource());
            responseMap.put("cardissuername", vtResponseVO.getCardIssuerName());
            responseMap.put("eci", vtResponseVO.getEci());
            responseMap.put("ecidescription", vtResponseVO.getEciDescription());
            responseMap.put("cvvresult", vtResponseVO.getCvvResult());
            responseMap.put("banktransid", vtResponseVO.getTxAcqId());
            responseMap.put("validationdescription", vtResponseVO.getValidationDescription());
            responseMap.put("cardcountrycode", vtResponseVO.getCardCountryCode());
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate", vtResponseVO.getBankTransDate());
            responseMap.put("token", "");
            responseMap.put("fraudscore", vtResponseVO.getFraudScore());
            pWriter.write(WriteXMLResponse.writeFullSaleResponse(responseMap));
        }
        else if(ResponseType.JSON.toString().equals(responseType))
        {
            HashMap responseMap = new HashMap();
            responseMap.put("orderid",orderId);
            responseMap.put("status", status);
            responseMap.put("statusdescription", statusMsg);
            responseMap.put("trackingid", vtResponseVO.getTrackingId());
            responseMap.put("amount",amount);
            responseMap.put("authcode", vtResponseVO.getAuthCode());
            responseMap.put("resultcode", vtResponseVO.getResultCode());
            responseMap.put("resultdescription", vtResponseVO.getResultDescription());
            responseMap.put("cardsource", vtResponseVO.getCardSource());
            responseMap.put("cardissuername", vtResponseVO.getCardIssuerName());
            responseMap.put("eci", vtResponseVO.getEci());
            responseMap.put("ecidescription", vtResponseVO.getEciDescription());
            responseMap.put("cvvresult", vtResponseVO.getCvvResult());
            responseMap.put("banktransid", vtResponseVO.getTxAcqId());
            responseMap.put("validationdescription", vtResponseVO.getValidationDescription());
            responseMap.put("cardcountrycode", vtResponseVO.getCardCountryCode());
            responseMap.put("checksum",checkSum);
            responseMap.put("billingdiscriptor",billingDiscriptor);
            responseMap.put("banktransdate", vtResponseVO.getBankTransDate());
            //responseMap.put("token", vtResponseVO.getToken());
            responseMap.put("fraudscore", vtResponseVO.getFraudScore());
            responseMap.put("rulestriggered", vtResponseVO.getRulesTriggered());
            pWriter.write(WriteJSONResponse.writeFullSaleResponse(responseMap));
        }
        else
        {
            String authcode = "";
            String resultcode = "";
            String resultDescription = "";
            String cardsource = "";
            String cardIssuerName = "";
            String eci = "";
            String eciDescription ="";
            String cvv = "";
            String validationDesc = "";
            String cardCountryCode = "";
            String txacid ="";
            String bankTransDate = "";
            String bankTransID = "";
            String token = "";
            String fraudScore = "";
            if(functions.isValueNull(vtResponseVO.getAuthCode()))
                authcode = vtResponseVO.getAuthCode();
            if(functions.isValueNull(vtResponseVO.getResultCode()))
                resultcode = vtResponseVO.getResultCode();
            if(functions.isValueNull(vtResponseVO.getResultDescription()))
                resultDescription = vtResponseVO.getResultDescription();
            if(functions.isValueNull(vtResponseVO.getCardSource()))
                cardsource = vtResponseVO.getCardSource();
            if(functions.isValueNull(vtResponseVO.getCardIssuerName()))
                cardIssuerName = vtResponseVO.getCardIssuerName();
            if(functions.isValueNull(vtResponseVO.getEci()))
                eci = vtResponseVO.getEci();
            if(functions.isValueNull(vtResponseVO.getEciDescription()))
                eciDescription = vtResponseVO.getEciDescription();
            if(functions.isValueNull(vtResponseVO.getCvvResult()))
                cvv = vtResponseVO.getCvvResult();
            if(functions.isValueNull(vtResponseVO.getValidationDescription()))
                validationDesc = vtResponseVO.getValidationDescription();
            if(functions.isValueNull(vtResponseVO.getCardCountryCode()))
                cardCountryCode = vtResponseVO.getCardCountryCode();
            if(functions.isValueNull(vtResponseVO.getBankTransId()))
                bankTransID = vtResponseVO.getBankTransId();
            if(functions.isValueNull(vtResponseVO.getTxAcqId()))
                txacid = vtResponseVO.getTxAcqId();
            if(functions.isValueNull(vtResponseVO.getBankTransDate()))
                bankTransDate = vtResponseVO.getBankTransDate();
           /* if(functions.isValueNull(vtResponseVO.getToken()))
                token = vtResponseVO.getToken();*/
            if(functions.isValueNull(vtResponseVO.getFraudScore()))
                fraudScore = vtResponseVO.getFraudScore();
            pWriter.write(orderId+":" + status + ":" + statusMsg + ":" + vtResponseVO.getTrackingId() + ":" + amount +
                    ":"+authcode+":"+resultcode+" : "+resultDescription+":"+cardsource+":"+cardIssuerName+":"+eci+
                    ":"+eciDescription+":"+cvv+":"+validationDesc+":"+cardCountryCode+":"+ checkSum +
                    ":" + bankTransID + ":"+txacid+":"+bankTransDate+" : "+token+":"+fraudScore);
        }
    }


    public void calculateCheckSumAndWriteStatusCardRegistration(PrintWriter pWriter,String days,String token,String status,String statusMsg,String key,String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();

            log.debug("ResponseType---"+merchantDetailsVO.getResponseType());
            log.debug("ResponseLength---"+merchantDetailsVO.getResponseLength());
        }

        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumForCardRegistration(token, status, key);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg ="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusCardRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if (ResponseType.JSON.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardRegistration"));
            }
            else
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardRegistration"));
            }
        }
        else
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write( days+ " : " + status + " : " + statusMsg + " : " + token +":" + checkSum);
            else
                pWriter.write( days+ " : " + status + " : " + statusMsg + " : " + token +":" + checkSum);
        }
    }


    public void calculateCheckSumAndWriteStatusCardRegistration(PrintWriter pWriter,String days,String token,String status,String statusMsg,String key,String checksumAlgo,String responseType, String responseLength)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumForCardRegistration(token, status, key);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg ="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusCardRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if (ResponseType.JSON.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardRegistration"));
            }
            else
            {
                responseMap.put("days",days);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("token",token);
                responseMap.put("checksum",checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardRegistration"));
            }
        }
        else
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write( days+ " : " + status + " : " + statusMsg + " : " + token +":" + checkSum);
            else
                pWriter.write( days+ " : " + status + " : " + statusMsg + " : " + token +":" + checkSum);
        }
    }


    public void calculateCheckSumAndWriteStatusCardholderRegistration(PrintWriter pWriter,String cardholderId,String status,String statusMsg,String key,String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String checkSum = null;

        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();

            log.debug("ResponseType---"+merchantDetailsVO.getResponseType());
            log.debug("ResponseLength---"+merchantDetailsVO.getResponseLength());
        }

        try
        {
            checkSum = Checksum.generateChecksumForCardRegistration(cardholderId,status,key);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg ="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusCardholderRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardholderRegistration"));
            }
            else
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardholderRegistration"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write( cardholderId+ " : " + status + " : " + statusMsg +":" + checkSum);
            else
                pWriter.write( cardholderId+ " : " + status + " : " + statusMsg +":" + checkSum);
        }
    }
    public void calculateCheckSumAndWriteStatusCardholderRegistration(PrintWriter pWriter,String cardholderId,String status,String statusMsg,String key,String checksumAlgo,String responseType,String responseLength)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumForCardRegistration(cardholderId,status,key);
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg ="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusCardholderRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardholderRegistration"));
            }
            else
            {
                responseMap.put("cardholderid", cardholderId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusMsg);
                responseMap.put("checksum", checkSum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "CardholderRegistration"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
                pWriter.write( cardholderId+ " : " + status + " : " + statusMsg +":" + checkSum);
            else
                pWriter.write( cardholderId+ " : " + status + " : " + statusMsg +":" + checkSum);
        }
    }
    public void calculateCheckSumAndWriteStatusMerchantRegistration(PrintWriter pWriter, String toId, String status, String statusDescription, String partnerId, String clKey,Hashtable merchantHash)
    {
        String checksum="";
        String responseType="";
        String responseLength="";
        if(merchantHash != null)
        {
            responseType = (String) merchantHash.get("responseType");
            responseLength = (String) merchantHash.get("responseLength");
        }

        try
        {
            checksum = Checksum.getMerchantSignUpResponseChecksum(toId,status, partnerId, clKey);
        }
        catch (NoSuchAlgorithmException e)
        {
            statusDescription="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusMerchantRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();

        if (ResponseType.JSON.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if (ResponseType.XML.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "MerchantRegistration"));
            }
            else
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "MerchantRegistration"));
            }
        }
        else
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                pWriter.write(toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
            else
            {
                pWriter.write(toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
        }
    }
    public void calculateCheckSumAndWriteStatusMerchantRegistration(PrintWriter pWriter, String toId, String status, String statusDescription, String partnerId, String clKey,String responseType,String responseLength)
    {
        String checksum="";

        try
        {
            checksum = Checksum.getMerchantSignUpResponseChecksum(toId,status, partnerId, clKey);
        }
        catch (NoSuchAlgorithmException e)
        {
            statusDescription="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusMerchantRegistration()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();

        if (ResponseType.JSON.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if (ResponseType.XML.toString().equals(responseType))
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "MerchantRegistration"));
            }
            else
            {
                responseMap.put("memberid", toId);
                responseMap.put("status", status);
                responseMap.put("statusdescription", statusDescription);
                responseMap.put("checksum", checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "MerchantRegistration"));
            }
        }
        else
        {
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                pWriter.write(toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
            else
            {
                pWriter.write(toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
        }
    }

    public boolean isTrackingIdExistInDB(String fromtype, String trackingId,String accountid,String toid)
    {
        transactionLogger.debug("inside isTrackingIdExistInDB----");
        boolean id = false;
        Connection connection = null;
        try
        {
            connection = Database.getConnection();

            String transaction_table = "transaction_common";

            if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_qwipi";

            }
            else if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_ecore";

            }
            else
            {
                transaction_table = "transaction_common";
            }
            transactionLogger.debug("fromtype-----"+fromtype);

            String query = "select toid from " + transaction_table + " where trackingid = ? AND accountid=? AND toid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1,trackingId);
            preparedStatement.setString(2,accountid);
            preparedStatement.setString(3,toid);
            ResultSet resultSet = preparedStatement.executeQuery();


            if(resultSet.next())
            {
                id = true;
            }
            transactionLogger.debug("isTrackingIdExistInDB-----"+preparedStatement+id);
        }
        catch(SystemError systemError)
        {
            log.debug("System error"+systemError);
        }
        catch (SQLException e)
        {
            log.debug("SQL exception"+e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return id;
    }

    public void calculateCheckSumAndWriteStoredCards(PrintWriter pWriter,String partnerId,String toId,String cardholderId,String cardString,String status, String statusMsg,String key, String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {

        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();

            log.debug("ResponseType---"+merchantDetailsVO.getResponseType());
            log.debug("ResponseLength---"+merchantDetailsVO.getResponseLength());
        }

        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumV2(partnerId,cardholderId,status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status="N";
            statusMsg="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStoredCards()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, "FetchCard Direct Kit");
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "FetchCards"));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "FetchCards"));
            }
        }
    }

    public void calculateCheckSumAndWriteStoredCards(PrintWriter pWriter,String partnerId,String toId,String cardholderId,String cardString,String status, String statusMsg,String key, String checksumAlgo,String responseType,String responseLength)
    {
        String checkSum = null;
        try
        {
            checkSum = Checksum.generateChecksumForFetchCardResponse(partnerId, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status="N";
            statusMsg="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStoredCards()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, "FetchCard Direct Kit");
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "FetchCards"));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("cardholderid",cardholderId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusMsg);
                responseMap.put("checksum",checkSum);
                responseMap.put("customercards",cardString);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "FetchCards"));
            }
        }
    }

    public void calculateCheckSumAndWriteStatusInvalidateToken(PrintWriter pWriter,String partnerId,String toId,String status, String statusDescription, String token, String key, String checksumAlgo,MerchantDetailsVO merchantDetailsVO)
    {
        String responseType="";
        String responseLength="";
        if(merchantDetailsVO != null)
        {
            responseType = merchantDetailsVO.getResponseType();
            responseLength = merchantDetailsVO.getResponseLength();

            log.debug("ResponseType---"+merchantDetailsVO.getResponseType());
            log.debug("ResponseLength---"+merchantDetailsVO.getResponseLength());
        }

        String checksum="";

        try
        {
            checksum = Checksum.generateChecksumV2(partnerId, token, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status="N";
            statusDescription="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusInvalidateToken()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "InvalidateToken"));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "InvalidateToken"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                pWriter.write(partnerId + ":" + toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
            else
            {
                pWriter.write(partnerId + ":" + toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
        }
    }
    public void calculateCheckSumAndWriteStatusInvalidateToken(PrintWriter pWriter,String partnerId,String toId,String status, String statusDescription, String token, String key, String checksumAlgo,String responseType, String responseLength)
    {
        String checksum="";

        try
        {
            checksum = Checksum.generateChecksumV2(partnerId, token, status, key, checksumAlgo);
        }
        catch (NoSuchAlgorithmException e)
        {
            status="N";
            statusDescription="Internal error while processing your request";
            PZExceptionHandler.raiseAndHandleGenericViolationException("TransactionUtility.java", "calculateCheckSumAndWriteStatusInvalidateToken()", null, "Transaction", "No Algorithm Exception:::", null, e.getMessage(), e.getCause(), null, PZOperations.DIRECTKIT_REFUND);
        }

        HashMap responseMap = new HashMap();
        if(ResponseType.JSON.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteJSONResponse.writeJSONResponse(responseMap));
            }
        }
        else if(ResponseType.XML.toString().equals(responseType))
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "InvalidateToken"));
            }
            else
            {
                responseMap.put("partnerid",partnerId);
                responseMap.put("toid",toId);
                responseMap.put("status",status);
                responseMap.put("statusdescription",statusDescription);
                responseMap.put("checksum",checksum);
                pWriter.write(WriteXMLResponse.writeXMLResponse(responseMap, "InvalidateToken"));
            }
        }
        else
        {
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                pWriter.write(partnerId + ":" + toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
            else
            {
                pWriter.write(partnerId + ":" + toId + " : " + status + " : " + statusDescription + " : " + checksum);
            }
        }
    }


    public void setMerchantNotification(HashMap hashMap,TransactionDetailsVO transactionDetailsVO,String trackingid,String status,String resMessgae)
    {

        transactionLogger.error("inside sending TransactionUtility---"+transactionDetailsVO.getNotificationUrl());
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();

        GenericCardDetailsVO genericCardDetailsVO   = new GenericCardDetailsVO();
        AuditTrailVO auditTrailVO                   = new AuditTrailVO();
        CommResponseVO commResponseVO                   = new CommResponseVO();
        GenericTransDetailsVO genericTransDetailsVO     = new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO             = new MerchantDetailsVO();
        TransactionManager transactionManager           = new TransactionManager();

        DirectKitResponseVO directKitResponseVO         = new DirectKitResponseVO();
        Response responseVo         = new Response();
        Transaction transaction     = new Transaction();
        String checkSum         = "";
        String shortStatus      = "";
        String toType           = "";
        //HttpPost post           = new HttpPost(transactionDetailsVO.getNotificationUrl());
        HttpPost post                       = null;
        HttpPost postMerchantNotification   = null;
        int code = 0;
        try
        {
            transactionLogger.error("MerchantNotificationUrl >>>>>>>>>>> " +trackingid+" "+ transactionDetailsVO.getMerchantNotificationUrl());
            transactionLogger.error("TransactionNotificationUrl >>>>>>>>>>> "+trackingid+" " + transactionDetailsVO.getNotificationUrl());

            directKitResponseVO.setEmail((String)hashMap.get("customerEmail"));
            transactionLogger.error("customer id---"+transactionDetailsVO.getCustomerId());
            if(functions.isValueNull((String) hashMap.get("customerId")))
                directKitResponseVO.setCustId((String) hashMap.get("customerId"));
            else if(functions.isValueNull(transactionDetailsVO.getCustomerId())){
                directKitResponseVO.setCustId(transactionDetailsVO.getCustomerId());
            }
            else{
                directKitResponseVO.setCustId("");
            }

            if(functions.isValueNull(transactionDetailsVO.getTerminalId())){
                directKitResponseVO.setTerminalId(transactionDetailsVO.getTerminalId());
            }
            else{
                directKitResponseVO.setTerminalId("");
            }
            if(functions.isValueNull(transactionDetailsVO.getBankReferenceId())){
                directKitResponseVO.setBankReferenceId(transactionDetailsVO.getBankReferenceId());
                transactionLogger.error("transactionDetailsVO.getBankReferenceId()-->"+transactionDetailsVO.getBankReferenceId());
            }
            else{
                transactionLogger.error("inside else transactionDetailsVO.getBankReferenceId()-->"+transactionDetailsVO.getBankReferenceId());
                directKitResponseVO.setBankReferenceId("");
            }


            directKitResponseVO.setCustBankId((String) hashMap.get("customerBankId"));

            if(functions.isValueNull(transactionDetailsVO.getCommissionToPay()))
                directKitResponseVO.setCommissionToPay(transactionDetailsVO.getCommissionToPay());
            if(functions.isValueNull(transactionDetailsVO.getCommCurrency()))
                directKitResponseVO.setCommissionCurrency(transactionDetailsVO.getCommCurrency());

            transactionLogger.error("Hash table notification---"+hashMap);

            if (functions.isValueNull(status) && (status.contains("Successful") || status.contains("Success") || status.contains("success") || status.contains("authsuccessful") || status.contains("capturesuccess") || status.contains("payoutsuccessful")))
                shortStatus = "Y";
            else if (functions.isValueNull(status) && (status.contains("Pending") || status.contains("pending")) || status.contains("authstarted") || status.contains("authstarted_3D") || status.contains("begun") || status.contains("payoutstarted"))
                shortStatus = "P";
            else if (functions.isValueNull(status) && (status.contains("Cancel") || status.contains("cancel") || status.contains("authcancelled") || status.contains("cancelstarted")))
                shortStatus = "C";
            else
                shortStatus = "N";

            try
            {
                checkSum = Checksum.generateChecksumForStandardKit(getValue(trackingid),getValue(transactionDetailsVO.getDescription()), getValue(transactionDetailsVO.getAmount()), getValue(shortStatus),getValue(transactionDetailsVO.getSecretKey()));
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("NoSuchAlgorithmException while checksum in sending notification---",e);
            }

            genericTransDetailsVO.setChecksum(checkSum);
            genericTransDetailsVO.setAmount(transactionDetailsVO.getAmount());
            genericTransDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
            genericTransDetailsVO.setBillingDiscriptor(transactionDetailsVO.getBillingDesc());

            transactionLogger.debug("email-----"+transactionDetailsVO.getEmailaddr());
            transactionLogger.debug("email-----"+directKitResponseVO.getEmail());
            if(!functions.isValueNull(directKitResponseVO.getEmail())){
                directKitResponseVO.setEmail(transactionDetailsVO.getEmailaddr());
            }
            transactionLogger.error("email-----"+directKitResponseVO.getEmail());



            if(functions.isValueNull(transactionDetailsVO.getExpdate()))
            {

                String expDate = transactionDetailsVO.getExpdate();

                String expMonth = "";
                String expYear = "";

                if(expDate.contains("/"))
                {
                    String date[] = expDate.split("/");
                    if(date.length==2)
                    {
                        expMonth = date[0];
                        expYear = date[1];
                    }
                }
                genericCardDetailsVO.setExpMonth(expMonth);
                genericCardDetailsVO.setExpYear(expYear);
            }
            genericCardDetailsVO.setCardNum(transactionDetailsVO.getCcnum());
            genericAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
            genericAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
            genericAddressDetailsVO.setTmpl_currency(transactionDetailsVO.getTemplatecurrency());
            genericAddressDetailsVO.setTmpl_amount(transactionDetailsVO.getTemplateamount());

            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            commonValidatorVO.setEci(transactionDetailsVO.getEci());
            commonValidatorVO.setBankCode(transactionDetailsVO.getBankCode());
            commonValidatorVO.setBankDescription(transactionDetailsVO.getBankDescription());

            commonValidatorVO.setTrackingid(trackingid);
            directKitResponseVO.setDescription(transactionDetailsVO.getDescription());
            directKitResponseVO.setStatus(status);
            directKitResponseVO.setStatusMsg("Transaction Processed successful");
            commonValidatorVO.setPaymentBrand(transaction.getPaymentBrandForRest(transactionDetailsVO.getCardTypeId()));
            commonValidatorVO.setPaymentMode(transaction.getPaymentModeForRest(transactionDetailsVO.getPaymodeId()));
            directKitResponseVO.setRemark(resMessgae);
            commonValidatorVO.setMarketPlaceVOList(transactionDetailsVO.getMarketPlaceVOList());

            transactionLogger.error("Before writeDirectTransactionResponse---");
            WriteDirectTransactionResponse writeDirectTransactionResponse = new WriteDirectTransactionResponse();
            transactionLogger.error("before writeDirectTransactionResponse ---"+trackingid);
            writeDirectTransactionResponse.setSuccessRestInquiryResponse(responseVo, directKitResponseVO, commonValidatorVO);
            //transactionLogger.error(service.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class,responseVo));
            transactionLogger.error("after writeDirectTransactionResponse ---"+trackingid);
            Gson gson = new Gson();

            String notificationJson = gson.toJson(responseVo);
            transactionLogger.error("Sending Notification JSON---for---"+trackingid+"---"+notificationJson);
            StringEntity postingString  = new StringEntity(notificationJson);
            if(functions.isValueNull(transactionDetailsVO.getNotificationUrl())){
                post                        = new HttpPost(transactionDetailsVO.getNotificationUrl());
                HttpClient httpClient       = HttpClientBuilder.create().build();
                //StringEntity postingString  = new StringEntity(notificationJson);//gson.tojson() converts your pojo to json
                post.setHeader("Content-type", "application/json");
                post.setEntity(postingString);
                HttpResponse response   = httpClient.execute(post);
                code                    = response.getStatusLine().getStatusCode();
            }

            if(functions.isValueNull(transactionDetailsVO.getMerchantNotificationUrl())){
                HttpClient httpClient2              = HttpClientBuilder.create().build();
                postMerchantNotification            = new HttpPost(transactionDetailsVO.getMerchantNotificationUrl());
                postMerchantNotification.setHeader("Content-type", "application/json");
                postMerchantNotification.setEntity(postingString);
                HttpResponse responseMerchantNotification   = httpClient2.execute(postMerchantNotification);
                int codeMerchantNotification                = responseMerchantNotification.getStatusLine().getStatusCode();

                transactionLogger.error("Merchant Config URL Notification  JSON ----> "+trackingid+" ---> "+notificationJson+"  Response Staus code:: "+codeMerchantNotification);
            }

            if(functions.isValueNull(transactionDetailsVO.getTotype())){
                toType = transactionDetailsVO.getTotype();
            }
            transactionLogger.error("Response Staus code::"+code+" for trackingid "+trackingid);

            if("Facilero".equalsIgnoreCase(toType)){
                facileroLogger.error("Sending Notification JSON---for---> "+trackingid+" --- "+notificationJson+"  Response Staus code:: "+code);
            }else{
                transactionLogger.error("Sending Notification JSON---for---"+trackingid+"---"+notificationJson+"  Response Staus code:: "+code);
            }
            transactionManager.updateNotificationStatusCode(trackingid,String.valueOf(code),commResponseVO,auditTrailVO);

        }
        catch (UnsupportedEncodingException e)
        {
            transactionLogger.error("UnsupportedEncodingException while notification---",e);
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException while notification---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while notification---",e);
        }
        finally
        {

            if(post != null)
            {
                post.releaseConnection();
            }
            if(postMerchantNotification != null)
            {
                postMerchantNotification.releaseConnection();
            }
        }

    }

    public TransactionDetailsVO getTransactionDetails(CommonValidatorVO commonValidatorVO)
    {
        TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
        if(commonValidatorVO != null)
        {
            transactionDetailsVO.setTrackingid(commonValidatorVO.getTrackingid());
            transactionDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            transactionDetailsVO.setPaymodeId(commonValidatorVO.getPaymentType());
            transactionDetailsVO.setCardTypeId(commonValidatorVO.getCardType());
            transactionDetailsVO.setToid(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            transactionDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());//totype
            transactionDetailsVO.setSecretKey(commonValidatorVO.getMerchantDetailsVO().getKey());
            //transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
            //transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
            transactionDetailsVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
            transactionDetailsVO.setOrderDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            transactionDetailsVO.setTemplateamount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            transactionDetailsVO.setAmount(String.format("%.2f", Double.parseDouble(commonValidatorVO.getTransDetailsVO().getAmount())));
            transactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            transactionDetailsVO.setRedirectURL(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            transactionDetailsVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            transactionDetailsVO.setStatus(commonValidatorVO.getStatus());
            transactionDetailsVO.setFirstName(commonValidatorVO.getAddressDetailsVO().getFirstname());//fn
            transactionDetailsVO.setLastName(commonValidatorVO.getAddressDetailsVO().getLastname());//ln
            // transactionDetailsVO.setName(resultSet.getString("name"));//name
            transactionDetailsVO.setCcnum(commonValidatorVO.getCardDetailsVO().getCardNum());//ccnum
            transactionDetailsVO.setExpdate(commonValidatorVO.getCardDetailsVO().getExpMonth()+"/"+commonValidatorVO.getCardDetailsVO().getExpYear());
            //transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
            transactionDetailsVO.setCardtype(commonValidatorVO.getTransDetailsVO().getCardType());//cardtype
            transactionDetailsVO.setEmailaddr(commonValidatorVO.getAddressDetailsVO().getEmail());
            transactionDetailsVO.setIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            transactionDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());//country
            transactionDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());//state
            transactionDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());//city
            transactionDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());//street
            transactionDetailsVO.setZip(commonValidatorVO.getAddressDetailsVO().getZipCode());//zip
            transactionDetailsVO.setTelcc(commonValidatorVO.getAddressDetailsVO().getTelnocc());//telcc
            transactionDetailsVO.setTelno(commonValidatorVO.getAddressDetailsVO().getPhone());//telno
            //transactionDetailsVO.setHttpHeader();//httpheadet
            // transactionDetailsVO.setPaymentId(commonValidatorVO.gettransac);
            // transactionDetailsVO.setPaymentId(commonValidatorVO.);
            transactionDetailsVO.setTemplatecurrency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            transactionDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());
            transactionDetailsVO.setEci(commonValidatorVO.getEci());
            transactionDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
            transactionDetailsVO.setBillingDesc(commonValidatorVO.getTransDetailsVO().getBillingDiscriptor());
            transactionDetailsVO.setBankCode(commonValidatorVO.getBankCode());
            transactionDetailsVO.setBankDescription(commonValidatorVO.getBankDescription());
            transactionDetailsVO.setMarketPlaceVOList(commonValidatorVO.getMarketPlaceVOList());
            transactionDetailsVO.setTransactionNotification(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification());

            transactionLogger.error("getNotificationUrl >>>>>>> >>>>>> "+commonValidatorVO.getTrackingid()+" "+commonValidatorVO.getMerchantDetailsVO().getNotificationUrl());

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl())){
                transactionDetailsVO.setMerchantNotificationUrl(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl());
            }else{
                transactionDetailsVO.setMerchantNotificationUrl("");
            }

        }
        return transactionDetailsVO;
    }
    public void setToken(CommonValidatorVO commonValidatorVO,String responseStatus)
    {
        try
        {
            transactionLogger.error("commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()-->"+commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed());
            transactionLogger.error("responseStatus-->"+responseStatus);
            if (("Successful".equalsIgnoreCase(responseStatus) || "success".equalsIgnoreCase(responseStatus) || responseStatus.contains("Successful") || responseStatus.contains("capturesuccess") || responseStatus.contains("authsuccessful")) && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
            {
                TerminalManager terminalManager = new TerminalManager();
                PaymentDAO paymentDAO=new PaymentDAO();
                String token = "";
                if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                {
                    TokenManager tokenManager = new TokenManager();
                    TokenDetailsVO tokenDetailsVO=null;
                    tokenDetailsVO=tokenManager.getTokenDetailsByUsingTrackingId(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTrackingid());
                    if(tokenDetailsVO!=null)
                    {
                        if("N".equalsIgnoreCase(tokenDetailsVO.getIsActive()))
                        {
                            boolean isActivated = tokenManager.activate3DTransactionToken(tokenDetailsVO);
                            if (isActivated)
                            {
                                commonValidatorVO.setToken(tokenDetailsVO.getRegistrationToken());
                                commonValidatorVO.setStatus("success");

                                //registration transactions entry
                                TokenTransactionDetailsVO tokenTransactionDetailsVO = new TokenTransactionDetailsVO();
                                tokenTransactionDetailsVO.setToid(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                                tokenTransactionDetailsVO.setTrackingid(String.valueOf(commonValidatorVO.getTrackingid()));
                                tokenTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                                tokenTransactionDetailsVO.setRegistrationId(tokenDetailsVO.getRegistrationId());
                                transactionLogger.error("tokenDetailsVO.getRegistrationId()--->" + tokenDetailsVO.getRegistrationId());
                                tokenManager.manageRegistrationTransactionDetails(tokenTransactionDetailsVO);
                            }
                        }else
                        {
                            commonValidatorVO.setToken(tokenDetailsVO.getRegistrationToken());
                            commonValidatorVO.setStatus("success");
                        }
                    }
                }else {
                    transactionLogger.debug("---outside---");
                }

            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException-----", e);
        }
    }
    public void setTokenFrontEnd(CommonValidatorVO commonValidatorVO,String responseStatus)
    {
        try
        {
            transactionLogger.error("commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()-->"+commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed());
            transactionLogger.error("responseStatus-->"+responseStatus);
            if (("Successful".equalsIgnoreCase(responseStatus) || "success".equalsIgnoreCase(responseStatus) || responseStatus.contains("Successful") || responseStatus.contains("capturesuccess") || responseStatus.contains("authsuccess")) && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
            {
                TerminalManager terminalManager = new TerminalManager();
                PaymentDAO paymentDAO=new PaymentDAO();
                String token = "";
                if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                {
                    TokenManager tokenManager = new TokenManager();
                    MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
                    GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
                    GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
                    if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                    {
                        if (!tokenManager.isCardholderRegistered(merchantDetailsVO.getMemberId(), commonValidatorVO.getCustomerId()))
                        {
                            commonValidatorVO.setCustomerId(null);
                        }
                    }

                    String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();
                    TokenDetailsVO tokenDetailsVO = new TokenDetailsVO();
                    TokenRequestVO tokenRequestVO = new TokenRequestVO();
                    TokenResponseVO tokenResponseVO = new TokenResponseVO();
                    genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
                    genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

                    String existingTokenId = null;
                    String registrationStatus = null;

                    if(commonValidatorVO.getCardDetailsVO().getCardNum() != null)
                    {
                        String cvv="";
                        transactionLogger.error("commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()--->"+commonValidatorVO.getMerchantDetailsVO().getIsCvvStore());
//                        if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsCvvStore()))
//                         cvv=paymentDAO.getCvv(commonValidatorVO);
                        tokenRequestVO.setMemberId(merchantDetailsVO.getMemberId());
                        tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                        tokenRequestVO.setTrackingId(String.valueOf(commonValidatorVO.getTrackingid()));
                        tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                        tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                        tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                        tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                        tokenRequestVO.setGeneratedBy(generatedBy);
                        tokenRequestVO.setRegistrationGeneratedBy(generatedBy);
                        tokenRequestVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                        tokenRequestVO.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
                        tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                        tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                        genericTransDetailsVO.setCurrency(genericTransDetailsVO.getCurrency());
                        tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                        tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                        if(functions.isValueNull(cvv))
                            tokenRequestVO.getCardDetailsVO().setcVV(PzEncryptor.decryptCVV(cvv));
                        String expDate=functions.isValueNull(tokenRequestVO.getCardDetailsVO().getExpMonth())+"/"+functions.isValueNull(tokenRequestVO.getCardDetailsVO().getExpYear());

                        existingTokenId = tokenManager.isTokenAvailable(tokenRequestVO.getMemberId(), tokenRequestVO.getCardDetailsVO().getCardNum(),expDate);
                        if(functions.isValueNull(existingTokenId))
                        {
                            tokenRequestVO.setTokenId(existingTokenId);
                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                            registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                            tokenResponseVO.setStatus(registrationStatus);
                            tokenResponseVO.setTokenId(existingTokenId);
                            tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                        }
                        else
                        {
                            String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO);
                            tokenRequestVO.setTokenId(newTokenId);
                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                            registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                            tokenResponseVO.setStatus(registrationStatus);
                            tokenResponseVO.setTokenId(existingTokenId);
                            tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                        }
                    }

                   /* else if(commonValidatorVO.getCardDetailsVO().getBIC() != null || commRequestVO.getReserveField2VO().getAccountNumber() != null)
                    {
                        tokenRequestVO.setMemberId(toid);
                        tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                        tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                        tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                        tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                        tokenRequestVO.setAddressDetailsVO(genericAddressDetailsVO);
                        tokenRequestVO.setGeneratedBy(generatedBy);
                        tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                        tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                        tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                        tokenRequestVO.setReserveField2VO(commonValidatorVO.getReserveField2VO());
                        genericTransDetailsVO.setCurrency(transDetailsVO.getCurrency());
                        tokenRequestVO.setTransDetailsVO(genericTransDetailsVO);
                        tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());

                        existingTokenId = tokenManager.isNewAccount(tokenRequestVO.getMemberId(), tokenRequestVO);
                        if(functions.isValueNull(existingTokenId))
                        {
                            tokenRequestVO.setTokenId(existingTokenId);
                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO);
                            registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId());
                            tokenResponseVO.setStatus(registrationStatus);
                            tokenResponseVO.setTokenId(existingTokenId);
                            tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                        }
                        else
                        {
                            String bankAccountId = tokenManager.insertBankAccountDetails(tokenRequestVO); //inserting bank account details
                            tokenRequestVO.setBankAccountId(bankAccountId);
                            String newTokenId = tokenManager.createTokenForRegistrationByMember(tokenRequestVO); //new token creation in token_master
                            tokenRequestVO.setTokenId(newTokenId);
                            tokenDetailsVO = tokenManager.createNewTokenRegistrationByMember(tokenRequestVO); //new registration in registration_master
                            registrationStatus = tokenManager.newTokenRegistrationMemberMappingEntry(tokenDetailsVO.getRegistrationId(), tokenRequestVO.getMemberId(), tokenRequestVO.getTrackingId()); //inserting membersId and trackingId in mapping table
                            tokenResponseVO.setStatus(registrationStatus);
                            tokenResponseVO.setTokenId(existingTokenId);
                            tokenResponseVO.setRegistrationToken(tokenDetailsVO.getRegistrationToken());
                        }
                    }*/

                    if ("success".equals(tokenResponseVO.getStatus()))
                    {
                        commonValidatorVO.setToken(tokenResponseVO.getRegistrationToken());
                        commonValidatorVO.setStatus(tokenResponseVO.getStatus());

                        //registration transactions entry
                        TokenTransactionDetailsVO tokenTransactionDetailsVO = new TokenTransactionDetailsVO();
                        tokenTransactionDetailsVO.setToid(tokenRequestVO.getMemberId());
                        tokenTransactionDetailsVO.setTrackingid(String.valueOf(commonValidatorVO.getTrackingid()));
                        tokenTransactionDetailsVO.setAmount(genericTransDetailsVO.getAmount());
                        tokenTransactionDetailsVO.setRegistrationId(tokenDetailsVO.getRegistrationId());
                        transactionLogger.error("tokenDetailsVO.getRegistrationId()--->"+tokenDetailsVO.getRegistrationId());
                        tokenManager.manageRegistrationTransactionDetails(tokenTransactionDetailsVO);
                    }
                }else {
                    transactionLogger.debug("---outside---");
                }

            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException-----", e);
        }
    }
    /*public void setToken(CommonValidatorVO commonValidatorVO,String responseStatus)
    {
        try
        {
            if ("Successful".equalsIgnoreCase(responseStatus) || "sucess".equalsIgnoreCase(responseStatus) || responseStatus.contains("Successful") && "Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
            {
                TerminalManager terminalManager = new TerminalManager();
                String token = "";
                if (terminalManager.isTokenizationActiveOnTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
                {
                    TokenManager tokenManager = new TokenManager();
                    String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());

                    if (functions.isValueNull(strToken))
                    {

                        token = strToken;
                        commonValidatorVO.setToken(token);
                    }
                    else
                    {
                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                        TokenResponseVO tokenResponseVO = null;
                        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                        commCardDetailsVO.setCardType(commonValidatorVO.getCardType());
                        commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                        commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                        commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());

                        tokenRequestVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        tokenRequestVO.setTrackingId(String.valueOf(commonValidatorVO.getTrackingid()));
                        tokenRequestVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                        tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());

                        tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                        tokenRequestVO.setAddressDetailsVO(commonValidatorVO.getAddressDetailsVO());

                        tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                        if ("success".equals(tokenResponseVO.getStatus()))
                        {
                            token = tokenResponseVO.getToken();
                            transactionLogger.debug("Token-----" + token);
                            commonValidatorVO.setToken(token);
                        }
                    }
                }else {
                    transactionLogger.debug("---outside---");
                }

            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException-----", e);
        }
    }*/
    public String getValue(String value){
        if(functions.isValueNull(value))
            return value;
        else
            return "";
    }
}