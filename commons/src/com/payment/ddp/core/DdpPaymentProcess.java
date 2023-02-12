package com.payment.ddp.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.valueObjects.EcoreResponseVO;
import com.directi.pg.core.valueObjects.EcoreTransDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.ecore.core.request.EcoreCaptureRequest;
import com.payment.ecore.core.request.EcoreRefundRequest;
import com.payment.ecore.core.response.EcoreRefundResponse;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.*;
import com.payment.response.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class DdpPaymentProcess extends CommonPaymentProcess
{
    private static Logger logger = new Logger(DdpPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DdpPaymentProcess.class.getName());

    Connection conn = null;
    BigDecimal bdConst = new BigDecimal("0.01");
    @Override
    public PZCancelResponse cancel(PZCancelRequest cancelRequest)
    {
            PZCancelResponse pzResponse = new PZCancelResponse() ;
            logger.debug("Execute select query for CancelTransaction");
            transactionLogger.debug("Execute select query for CancelTransaction");
            try{
            conn = Database.getConnection();
            String query = "select mid, midkey from gateway_accounts_ecore where accountid=?";
            String accountId = cancelRequest.getAccountId().toString();
            String trackingId = cancelRequest.getTrackingId().toString();
            PreparedStatement pstmt = conn.prepareStatement( query );
            pstmt.setString(1,accountId);
            ResultSet rs = pstmt.executeQuery();
            String mid = null;
            String midKey = null;
            if (rs.next())
            {
                mid = rs.getString("mid");
                midKey = rs.getString("midkey");
            }
            query = "select ecorePaymentOrderNumber from transaction_ecore_details where parentid= ? and status='authsuccessful'";

            pstmt = conn.prepareStatement( query );
            pstmt.setString(1,trackingId);
            rs = pstmt.executeQuery();

            String transId = null;
            if (rs.next())
            {
                transId = rs.getString("ecorePaymentOrderNumber");
            }

            EcorePaymentGateway pg =(EcorePaymentGateway) AbstractPaymentGateway.getGateway(accountId);
            EcoreResponseVO response = (EcoreResponseVO)pg.processVoid(mid, midKey, transId) ;
            if (("100").equals(response.getResponseCode()))
            {
                logger.debug("Transaction cancelled successfully for trackingid--" + trackingId);
                transactionLogger.debug("Transaction cancelled successfully for trackingid--" + trackingId);
                String ecorePaymentOrderNumber = response.getTransactionID();
                query = "update transaction_ecore set status='authcancelled', ecorePaymentOrderNumber ="+ecorePaymentOrderNumber+" where trackingid=" + trackingId + " and status  in ('proofrequired','authsuccessful','capturesuccess')";
            }
            else
            {
                logger.debug("Error while cancelling trackingid--" + trackingId);
                transactionLogger.debug("Error while cancelling trackingid--" + trackingId);
                query = null;
            }
            if (query != null)
            {
                Database.executeUpdate(query, conn);
            }
            }
            catch (PZTechnicalViolationException e)
            {
                logger.error("PZTechnicalViolationException while cancelling transaction via Ddp",e);
                transactionLogger.error("PZTechnicalViolationException while cancelling transaction via Ddp",e);

                PZExceptionHandler.handleTechicalCVEException(e,null,"cancel");

                pzResponse.setStatus(PZResponseStatus.ERROR);
            }
            catch (SQLException e)
            {
                logger.error("SQLException while cancelling transaction via Ddp",e);
                transactionLogger.error("SQLException while cancelling transaction via Ddp",e);

                PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");

                pzResponse.setStatus(PZResponseStatus.ERROR);
            }
            catch (SystemError systemError)
            {
                logger.error("SQLException while cancelling transaction via Ddp",systemError);
                transactionLogger.error("SQLException while cancelling transaction via Ddp",systemError);

                PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"cancel");

                pzResponse.setStatus(PZResponseStatus.ERROR);
            }

            finally
            {
                Database.closeConnection(conn);
            }

            return pzResponse;
    }

    @Override

    public int insertTransactionDetails(Hashtable parameters) throws SystemError
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {

        StringBuffer responseBuffer = new StringBuffer();
        Hashtable error = new Hashtable();
        Hashtable otherdetails = new Hashtable();
        String template = null;
        String vbv = null;
        String autoredirect = null;
        String key = "";
        String val = "";
        String ctoken = String.valueOf(parameters.get("ctoken"));

        Connection connection = null;
        try
        {
            connection = Database.getConnection();
            String sql = "select autoredirect,template,vbv from members where memberid =?";
            PreparedStatement p = connection.prepareStatement(sql);
            p.setString(1, String.valueOf(parameters.get("TOID")));
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                autoredirect = rs.getString("autoredirect");
                template = rs.getString("template");
                vbv = rs.getString("vbv");

            }

        }
        catch (Exception e)
        {
            error.put("Error", "Error occured while inserting data");

        }
        finally
        {
            Database.closeConnection(connection);
        }

        StringBuffer sbuf = new StringBuffer();

        if (error.size() > 0)
        {
            sbuf.append("Following Parameters are Invalid");
            Enumeration enu = error.keys();

            sbuf.append("<center><table border=1>");
            sbuf.append("<tr bgcolor=\"blue\" >");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Field");
            sbuf.append("</font></td>");
            sbuf.append("<td><font color=\"#FFFFFF\" >");
            sbuf.append("Error");
            sbuf.append("</font></td>");
            sbuf.append("</tr>");
            while (enu.hasMoreElements())
            {
                String field = (String) enu.nextElement();
                sbuf.append("<tr>");
                sbuf.append("<td>");
                sbuf.append(field);
                sbuf.append("</td>");
                sbuf.append("<td>");
                sbuf.append((String) error.get(field));
                sbuf.append("</td>");
                sbuf.append("</tr>");
            }
            sbuf.append("</table>");

            otherdetails.put("MESSAGE", sbuf.toString());

            otherdetails.put("RETRYBUTTON", "<input type=\"button\" value=\"&nbsp;&nbsp;Retry&nbsp;&nbsp;\" onClick=\"javascript:history.go(-1)\">");


            try
            {
                String toid = String.valueOf(parameters.get("TOID"));
                responseBuffer.append(Template.getError(toid, otherdetails));


            }
            catch (SystemError se)
            {
                logger.error("Excpetion in WaitServlet", se);
                transactionLogger.error("Excpetion in WaitServlet", se);

            }
            return responseBuffer.toString();
        }

        parameters.put("autoredirect", autoredirect);
        parameters.put("TEMPLATE", template);
        parameters.put("VBV", vbv);


        responseBuffer.append("<HTML>");
        responseBuffer.append("<HEAD> <script language=\"javascript\">" +
                "function Load(){" +
                "document.form.submit();" +
                "}" +
                " </script>");
        responseBuffer.append("</HEAD>");
        responseBuffer.append("<BODY onload=Load()>");
        responseBuffer.append("<form name=\"form\" action=\"/icici/servlet/DdpServlet?ctoken=" + ctoken + "\" method=\"post\" >");
        Enumeration enu = parameters.keys();

        while (enu.hasMoreElements())
        {
            key = (String) enu.nextElement();
            val = (String) parameters.get(key).toString();
            responseBuffer.append("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\" >");
            //log.debug("<input type=hidden name=\"" + key.toString() + "\" value=\"" + val.toString() + "\"");
        }

        responseBuffer.append("</form>");
        responseBuffer.append("</BODY>");
        responseBuffer.append("</HTML>");

        return responseBuffer.toString();


    }

    @Override
    public PZTransactionResponse transaction(PZTransactionRequest refundRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        EcoreRefundResponse response = new EcoreRefundResponse();
        EcoreResponseVO ecoreResponseVO = new EcoreResponseVO();
        try{
        String transid =null;
        String accountId = refundRequest.getAccountId().toString();
        String icicitransid = refundRequest.getTrackingId().toString();
        String refundamount = refundRequest.getRefundAmount().toString();
        String reason = refundRequest.getRefundReason();
        String merchantid = String.valueOf(((EcoreRefundRequest)refundRequest).getMemberId());

        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId(merchantid);
        auditTrailVO.setActionExecutorName("Customer");
        EcoreResponseVO ecoreTransRespDetails=null;
        TransactionEntry transactionEntry = new TransactionEntry();

        conn = Database.getConnection();

        String query = "select T.*, TD.ecorePaymentOrderNumber, M.company_name,M.contact_emails,M.currency,M.taxper,M.reversalcharge,T.fixamount from transaction_ecore as T, transaction_ecore_details as TD, members as M where T.trackingid=? and T.trackingid=TD.parentid and T.toid=M.memberid and T.toid=? and TD.status='capturesuccess' and T.status IN('settled','capturesuccess')";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1,icicitransid);
        pstmt.setString(2,merchantid);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next())
        {

            String fixamt=rs.getString("fixamount");
            String ecore_paymentordernumber=rs.getString("ecorePaymentOrderNumber");
            String rsdescription=rs.getString("description");
            String captureamount=rs.getString("amount");
            String transactionDate=rs.getString("dtstamp");
            String icicimerchantid=rs.getString("fromid");
            String toid=rs.getString("toid");
            String cardholdername=rs.getString("name");

            int chargeper = rs.getInt("chargeper");
            int transactiontaxper = rs.getInt("T.taxper");
            int currtaxper = rs.getInt("M.taxper");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String reversalCharges = rs.getString("M.reversalcharge");
            String icicimerchanttype = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            String status = rs.getString("status");
            BigDecimal amt= new BigDecimal(captureamount);
            BigDecimal charge= new BigDecimal(chargeper);
            String transtexper= String.valueOf(transactiontaxper);
            String currenttex= String.valueOf(currtaxper);
            int year = 0;
            if (transactionDate != null)
            {
                long timeInSecs = Long.parseLong(transactionDate);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timeInSecs * 1000);
                year = cal.get(Calendar.YEAR);
            }
            EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();
            TransDetail.setOperation("02");
            TransDetail.setPaymentOrderNo(ecore_paymentordernumber);
            TransDetail.setBillNo(rsdescription);
            TransDetail.setAmount(captureamount);
            TransDetail.setRefundAmount(refundamount);
            int paymentTransId=0;
            if(status.equals("capturesuccess"))
            {
                transactionEntry.newGenericRefundTransaction(icicitransid, amt, accountId, reason, ecoreResponseVO,auditTrailVO);
                transactionEntry.closeConnection();
            }

            //Now Reverse transaction on the gateway
            try
            {
                logger.debug("callng processRefund");
                transactionLogger.debug("callng processRefund");

                query = "select mid, midkey from gateway_accounts_ecore where accountid=?";

                pstmt = conn.prepareStatement( query );
                pstmt.setString(1,accountId);
                ResultSet rs1 = pstmt.executeQuery();

                String mid = null;
                String midKey = null;
                if (rs1.next())
                {
                    mid = rs1.getString("mid");
                    midKey = rs1.getString("midkey");
                }
                query = "select ecorePaymentOrderNumber from transaction_ecore_details where parentid= ? and status='capturesuccess'";

                pstmt = conn.prepareStatement( query );
                pstmt.setString(1,icicitransid);
                rs1 = pstmt.executeQuery();

                String transId = null;
                if (rs1.next())
                {
                    transId = rs1.getString("ecorePaymentOrderNumber");
                }


                EcorePaymentGateway paymentGateway = (EcorePaymentGateway) AbstractPaymentGateway.getGateway(accountId);

                ecoreTransRespDetails = (EcoreResponseVO) paymentGateway.processRefund(mid, midKey, transId);

            }
            catch(SystemError e)
            {
                logger.error("system error",e);
                transactionLogger.error("system error",e);

            }

            if (ecoreTransRespDetails != null && (ecoreTransRespDetails.getResponseCode()).equals("100"))
            {   Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_ecore set status='reversed', ecorePaymentOrderNumber ='"+ecoreTransRespDetails.getTransactionID());

                sb.append("',refundinfo='"+ESAPI.encoder().encodeForSQL(MY,reason)+"'");

                //sb.append(",refundamount='"+ESAPI.encoder().encodeForSQL(MY,ecoreTransRespDetails.getRefundAmount())+"'");
                sb.append(",refundcode='"+ESAPI.encoder().encodeForSQL(MY,ecoreTransRespDetails.getResponseCode())+"'");

                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,icicitransid)+ " and status='markedforreversal'");

                int rows = Database.executeUpdate(sb.toString(), conn);
                logger.debug("No of Rows updated : " + rows + "<br>");
                transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                if (rows == 1)
                {
                    // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntryForEcore(icicitransid, captureamount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL,null, ecoreTransRespDetails,auditTrailVO);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                    response.setStatus(PZResponseStatus.SUCCESS);
                    response.setResponseDesceiption(rsdescription);
                    response.setCaptureAmount(captureamount);
                    response.setCardHolderName(cardholdername);

                }
            }
        }
    }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling transaction via Ddp", e);
            transactionLogger.error("PZTechnicalViolationException while cancelling transaction via Ddp", e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException while cancelling transaction via Ddp", e);
            transactionLogger.error("PZDBViolationException while cancelling transaction via Ddp", e);

            PZExceptionHandler.handleDBCVEException(e, null, "cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SQLException e)
        {
            logger.error("SQLException while cancelling transaction via Ddp",e);
            transactionLogger.error("SQLException while cancelling transaction via Ddp",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SystemError systemError)
        {
            logger.error("SQLException while cancelling transaction via Ddp", systemError);
            transactionLogger.error("SQLException while cancelling transaction via Ddp", systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(), "cancel()", null, "common", "Db violation exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }

        finally
    {
        Database.closeConnection(conn);
    }
         return response;
    }

    @Override
    public PZCaptureResponse capture(PZCaptureRequest captureRequest)
    {

        PZCaptureResponse response = new PZCaptureResponse();
        Transaction transaction = new Transaction();
        try{
        String accountId = captureRequest.getAccountId().toString();
        String trackingId = captureRequest.getTrackingId().toString();
        String mid = null;
        conn = Database.getConnection();
        String query = "select mid, midkey from gateway_accounts_ecore where accountid=?";

        PreparedStatement pstmt = conn.prepareStatement( query );
        pstmt.setString(1,accountId);
        ResultSet rs = pstmt.executeQuery();
        String captureamount = null;
        String midKey = null;
        if (rs.next())
        {
            mid =  rs.getString("mid");
            midKey = rs.getString("midkey");
        }
        query = "select ecorePaymentOrderNumber, amount from transaction_ecore_details where parentid= ? and status='authsuccessful'";

        pstmt = conn.prepareStatement( query );
        pstmt.setString(1,trackingId);
        rs = pstmt.executeQuery();

        String transId = null;
        if (rs.next())
        {
            transId = rs.getString("ecorePaymentOrderNumber");
            captureamount = rs.getString("amount");;
        }

        query = "update transaction_ecore set pod= ?,captureamount= ? where pod is null and toid= ? and status='authsuccessful' and trackingid=?";

        pstmt = conn.prepareStatement(query);
        pstmt.setString(1,((EcoreCaptureRequest)captureRequest).getPod());
        pstmt.setString(2,captureamount);
        pstmt.setString(3,String.valueOf(((EcoreCaptureRequest)captureRequest).getMemberId()));
        pstmt.setString(4,trackingId);
        pstmt.executeUpdate();

        String message = transaction.processCaptureForEcore(conn, trackingId, accountId, mid, midKey, transId, captureamount);
        response.setStatus(PZResponseStatus.SUCCESS);
        response.setResponseDesceiption(message);
        }
        catch (SQLException e)
        {
            logger.error("SQLException while cancelling transaction via Ddp",e);
            transactionLogger.error("SQLException while cancelling transaction via Ddp",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SystemError systemError)
        {
            logger.error("SQLException while cancelling transaction via Ddp", systemError);
            transactionLogger.error("SQLException while cancelling transaction via Ddp", systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(DdpPaymentProcess.class.getName(), "cancel()", null, "common", "Db violation exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "cancel");

            response.setStatus(PZResponseStatus.ERROR);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return response;
    }

    @Override
    public PZChargebackResponse chargeback(PZChargebackRequest pzChargebackRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZStatusResponse status(PZStatusRequest pzStatusRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO)
    {
        return 0;
    }
}
