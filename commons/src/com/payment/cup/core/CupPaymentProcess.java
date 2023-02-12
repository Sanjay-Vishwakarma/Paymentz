package com.payment.cup.core;

import com.directi.pg.*;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.paymentgateway.CUPPaymentGateway;
import com.directi.pg.core.valueObjects.CupRequestVO;
import com.directi.pg.core.valueObjects.CupResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.request.*;
import com.payment.response.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/16/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class CupPaymentProcess extends CommonPaymentProcess
{
    private static Logger logger = new Logger(CupPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CupPaymentProcess.class.getName());

    Connection conn = null;
    BigDecimal bdConst = new BigDecimal("0.01");

    @Override
    public int insertTransactionDetails(Hashtable parameters) throws SystemError
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZCancelResponse cancel(PZCancelRequest cancelRequest) {
        PZCancelResponse response = new PZCancelResponse();
        CupRequestVO cupRequestVO = new CupRequestVO();
        CupResponseVO cupResponseVO = new CupResponseVO();
        String authAmount = null;
        String pgType = null;
        String time = CupUtils.getBeijingTime();
        PreparedStatement pstmt=null;
        ResultSet rs1=null;

        try{
            String accountId = cancelRequest.getAccountId().toString();
            String trackingId = cancelRequest.getTrackingId().toString();


            TransactionEntry transactionEntry = new TransactionEntry();

            conn = Database.getConnection();

            //Now Reverse transaction on the gateway

            logger.debug("callng processRefund");
            transactionLogger.debug("callng processRefund");

            String query = "select td.responsetransactionid, td.detailid, td.amount, ga.merchantid, ga.displayname, ga.pgtypeid, gac.merchantcategorycode, gac.istestaccount from transaction_common_details td, gateway_accounts ga, gateway_accounts_cup gac where td.trackingid= ? and td.status='authsuccessful' and ga.accountid=? and gac.accountid=ga.accountid";

            pstmt = conn.prepareStatement( query );
            pstmt.setString(1,trackingId);
            pstmt.setString(2,accountId);
            rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                pgType = rs1.getString("pgtypeid");
                cupRequestVO.setTransactionId(rs1.getString("responsetransactionid"));
                cupRequestVO.setDetailsId(rs1.getString("detailid"));
                authAmount = rs1.getString("amount");
                cupRequestVO.setMerchantId(rs1.getString("merchantid"));
                cupRequestVO.setMerchantName(rs1.getString("displayname"));
                cupRequestVO.setMcc(rs1.getString("merchantcategorycode"));
                cupRequestVO.setTransType("32");
                cupRequestVO.setTestAccount(rs1.getString("istestaccount"));
                //cupRequestVO.set
                //transactionEntry.newGenericRefundTransaction(icicitransid, amt, accountId, reason, cupResponseVO);
                transactionEntry.closeConnection();

            }
            query = "Select currencyCode from currency_code where currency = (select currency from gateway_type where pgtypeid= ?)";

            pstmt = conn.prepareStatement( query );
            pstmt.setString(1,pgType);
            rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                cupRequestVO.setCurrencyCode(rs1.getString("currencyCode"));

            }

            cupRequestVO.setIp(Inet4Address.getLocalHost().getHostAddress());
            cupRequestVO.setTransType("32");
            cupRequestVO.setOrderTime(time);
            int amountInCents = (int)(Double.parseDouble(authAmount)*100);
            cupRequestVO.setAmount(String.valueOf(amountInCents));
            CUPPaymentGateway paymentGateway = new CUPPaymentGateway();

            cupResponseVO = (CupResponseVO) paymentGateway.processPreAuthorizationCancellation(cupRequestVO);

            if (cupResponseVO != null && (cupResponseVO.getResponseCode()).equals("00"))
            {   Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set status='authcancelled'");
                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,trackingId)+ " and status  in ('proofrequired','authsuccessful','capturesuccess')");

                int rows = Database.executeUpdate(sb.toString(), conn);
                logger.debug("No of Rows updated : " + rows + "<br>");
                transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                if (rows == 1)
                {
                    response.setStatus(PZResponseStatus.SUCCESS);
                    response.setResponseDesceiption(cupResponseVO.getStatusDescription());
                }
            }
            else
            {
                logger.debug("Error while cancelling trackingid--" + trackingId);
                transactionLogger.debug("Error while cancelling trackingid--" + trackingId);
            }
        }

        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling the transaction",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling the transaction", e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"cancel");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (UnknownHostException e)
        {
            logger.error("UnknownHostException while cancelling the transaction",e);
            transactionLogger.error("UnknownHostException while cancelling the transaction",e);

            PZExceptionHandler.raiseAndHandleTechnicalViolationException(CupPaymentProcess.class.getName(),"cancel()",null,"common","Technical Exception", PZTechnicalExceptionEnum.UNKNOWN_HOST_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SystemError se){

            logger.debug(se);
            transactionLogger.debug(se);
            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"cancel",null,"common","Db Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,"cancel");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while cancelling the transaction via CupPayment", e);
            transactionLogger.error("Sql Exception while cancelling the transaction via CupPayment", e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"cancel",null,"common","Db Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");
            response.setStatus(PZResponseStatus.ERROR);


        }
        finally
        {
            Database.closeResultSet(rs1);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return response;
    }

    @Override
    public String getRedirectPage(Hashtable parameters) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZTransactionResponse transaction(PZTransactionRequest refundRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PZRefundResponse refund(PZRefundRequest refundRequest)
    {
        PZRefundResponse response = new PZRefundResponse();
        CupRequestVO cupRequestVO = new CupRequestVO();
        CupResponseVO cupResponseVO = new CupResponseVO();
        String pgType = null;
        try{
        String accountId = refundRequest.getAccountId().toString();
        String icicitransid = refundRequest.getTrackingId().toString();
        String refundamount = refundRequest.getRefundAmount().toString();
        String reason = refundRequest.getRefundReason();
        //String merchantid = String.valueOf(refundRequest.getMemberId());
        BigDecimal amt= new BigDecimal(refundamount);
        String time = CupUtils.getBeijingTime();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        auditTrailVO.setActionExecutorId(cupRequestVO.getMerchantId());
        auditTrailVO.setActionExecutorName("Customer");
        TransactionEntry transactionEntry = new TransactionEntry();

        conn = Database.getConnection();

            //Now Reverse transaction on the gateway

                logger.debug("calling processRefund");
                transactionLogger.debug("calling processRefund");

                String query = "select td.responsetransactionid, td.detailid, ga.merchantid, ga.displayname, ga.pgtypeid, gac.merchantcategorycode, gac.istestaccount from transaction_common_details td, gateway_accounts ga, gateway_accounts_cup gac where td.trackingid= ? and td.status='capturesuccess' and ga.accountid=? and gac.accountid=ga.accountid";

                PreparedStatement pstmt = conn.prepareStatement( query );
                pstmt.setString(1,icicitransid);
                pstmt.setString(2,accountId);
                ResultSet rs1 = pstmt.executeQuery();

                if (rs1.next())
                {
                    pgType = rs1.getString("pgtypeid");
                    cupRequestVO.setTransactionId(rs1.getString("responsetransactionid"));
                    cupRequestVO.setDetailsId(rs1.getString("detailid"));
                    cupRequestVO.setMerchantId(rs1.getString("merchantid"));
                    cupRequestVO.setMerchantName(rs1.getString("displayname"));
                    cupRequestVO.setMcc(rs1.getString("merchantcategorycode"));
                    cupRequestVO.setOrderTime(time);
                    cupRequestVO.setTestAccount(rs1.getString("istestaccount"));

                    cupResponseVO.setProcessingTime(time);
                    cupResponseVO.setTransType("04");
                    transactionEntry.newGenericRefundTransaction(icicitransid, amt, accountId, reason, cupResponseVO,auditTrailVO);
                    transactionEntry.closeConnection();

                }
                query = "Select currencyCode from currency_code where currency = (select currency from gateway_type where pgtypeid= ?)";

                pstmt = conn.prepareStatement( query );
                pstmt.setString(1,pgType);
                rs1 = pstmt.executeQuery();

                if (rs1.next())
                {
                    cupRequestVO.setCurrencyCode(rs1.getString("currencyCode"));

                }
                //double refAmountinCents =  Double.parseDouble(refundamount)*100;
                int refAmountinCents = (int)(Double.parseDouble(refundamount)*100);
                cupRequestVO.setAmount(String.valueOf(refAmountinCents));
                cupRequestVO.setIp(Inet4Address.getLocalHost().getHostAddress());
                cupRequestVO.setTransType("04");

                CUPPaymentGateway paymentGateway = new CUPPaymentGateway();

                cupResponseVO = (CupResponseVO) paymentGateway.processRefund(cupRequestVO);
                cupResponseVO.setTransType("04");

            if (cupResponseVO != null && (cupResponseVO.getResponseCode()).equals("00"))
            {   Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set status='reversed'");

                sb.append(",refundinfo='"+ESAPI.encoder().encodeForSQL(MY,reason)+"'");

                //sb.append(",refundamount='"+ESAPI.encoder().encodeForSQL(MY,ecoreTransRespDetails.getRefundAmount())+"'");
                //sb.append(",refundcode='"+ESAPI.encoder().encodeForSQL(MY,cupResponseVO.getResponseCode())+"'");

                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,icicitransid)+ " and status='markedforreversal'");

                int rows = Database.executeUpdate(sb.toString(), conn);
                logger.debug("No of Rows updated : " + rows + "<br>");
                transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                if (rows == 1)
                {
                    // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntryForCUP(icicitransid, refundamount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, cupResponseVO,null,auditTrailVO,null);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                    response.setStatus(PZResponseStatus.SUCCESS);
                    response.setResponseDesceiption(cupResponseVO.getStatusDescription());

                }
            }
    }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling the transaction",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling the transaction", e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling the transaction",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling the transaction", e);

            PZExceptionHandler.handleDBCVEException(e,null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (UnknownHostException e)
        {
            logger.error("UnknownHostException while cancelling the transaction",e);
            transactionLogger.error("UnknownHostException while cancelling the transaction",e);

            PZExceptionHandler.raiseAndHandleTechnicalViolationException(CupPaymentProcess.class.getName(),"refund()",null,"common","Technical Exception", PZTechnicalExceptionEnum.UNKNOWN_HOST_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SystemError se){

            logger.debug(se);
            transactionLogger.debug(se);
            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"refund",null,"common","Db Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while cancelling the transaction via CupPayment", e);
            transactionLogger.error("Sql Exception while cancelling the transaction via CupPayment", e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"refund",null,"common","Db Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");
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
        CupRequestVO cupRequestVO = new CupRequestVO();
        CupResponseVO cupResponseVO = new CupResponseVO();
        String pgType = null;
        try{
            String accountId = captureRequest.getAccountId().toString();
            String trackingId = captureRequest.getTrackingId().toString();
            String captureAmount = captureRequest.getAmount().toString();
            String pod = captureRequest.getPod();
            String memberId = String.valueOf(captureRequest.getMemberId());
            BigDecimal amt= new BigDecimal(captureAmount);
            String time = CupUtils.getBeijingTime();

            TransactionEntry transactionEntry = new TransactionEntry();

            conn = Database.getConnection();

            //Now Reverse transaction on the gateway

            logger.debug("calling processCapture");
            transactionLogger.debug("calling processCapture");

            String query = "select td.responsetransactionid, td.detailid, ga.merchantid, ga.displayname, ga.pgtypeid, gac.merchantcategorycode, gac.istestaccount from transaction_common_details td, gateway_accounts ga, gateway_accounts_cup gac where td.trackingid= ? and td.status='authsuccessful' and ga.accountid=? and gac.accountid=ga.accountid";

            PreparedStatement pstmt = conn.prepareStatement( query );
            pstmt.setString(1,trackingId);
            pstmt.setString(2,accountId);
            ResultSet rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                pgType = rs1.getString("pgtypeid");
                cupRequestVO.setTransactionId(rs1.getString("responsetransactionid"));
                cupRequestVO.setDetailsId(rs1.getString("detailid"));
                cupRequestVO.setMerchantId(rs1.getString("merchantid"));
                cupRequestVO.setMerchantName(rs1.getString("displayname"));
                cupRequestVO.setMcc(rs1.getString("merchantcategorycode"));
                cupRequestVO.setOrderTime(time);
                cupRequestVO.setTestAccount(rs1.getString("istestaccount"));

            }
            query = "Select currencyCode from currency_code where currency = (select currency from gateway_type where pgtypeid= ?)";

            AuditTrailVO auditTrailVO=new AuditTrailVO();
            auditTrailVO.setActionExecutorId(cupRequestVO.getMerchantId());
            auditTrailVO.setActionExecutorName("Customer");
            pstmt = conn.prepareStatement( query );
            pstmt.setString(1,pgType);
            rs1 = pstmt.executeQuery();

            if (rs1.next())
            {
                cupRequestVO.setCurrencyCode(rs1.getString("currencyCode"));

            }
            //double refAmountinCents =  Double.parseDouble(refundamount)*100;
            int capAmountinCents = (int)(Double.parseDouble(captureAmount)*100);
            cupRequestVO.setAmount(String.valueOf(capAmountinCents));
            cupRequestVO.setIp(Inet4Address.getLocalHost().getHostAddress());
            cupRequestVO.setTransType("03");

            query = "update transaction_common set pod= ?,captureamount= ?,status = 'capturestarted' where pod is null and status='authsuccessful' and trackingid=?";

            pstmt = conn.prepareStatement(query);
            pstmt.setString(1,pod);
            pstmt.setString(2,captureAmount);
            pstmt.setString(3,trackingId);
            int result = pstmt.executeUpdate();
            //Database.commit(conn);
            if (result == 1)
            {
                ActionEntry entry = new ActionEntry();
                int actionEntry = entry.actionEntryForCUP(trackingId,captureAmount,ActionEntry.ACTION_CAPTURE_STARTED,ActionEntry.STATUS_CAPTURE_STARTED,null,null,auditTrailVO,null);
                entry.closeConnection();
            }
            CUPPaymentGateway paymentGateway = new CUPPaymentGateway();
            cupResponseVO = (CupResponseVO) paymentGateway.processPreAuthorizationCompletion(cupRequestVO);
            cupResponseVO.setTransType("03");
            cupResponseVO.setProcessingTime(time);
            if (cupResponseVO != null && (cupResponseVO.getResponseCode()).equals("00"))
            {   Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                StringBuffer sb = new StringBuffer();
                sb.append("update transaction_common set status='capturesuccess'");

                sb.append(" where trackingid=" +ESAPI.encoder().encodeForSQL(MY,trackingId)+ " and status='capturestarted'");

                int rows = Database.executeUpdate(sb.toString(), conn);
                logger.debug("No of Rows updated : " + rows + "<br>");
                transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                if (rows == 1)
                {
                    // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntryForCUP(trackingId, captureAmount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, cupResponseVO,null,auditTrailVO,null);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table

                    response.setStatus(PZResponseStatus.SUCCESS);
                    response.setResponseDesceiption(cupResponseVO.getStatusDescription());

                }
            }
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling the transaction",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling the transaction", e);

            PZExceptionHandler.handleTechicalCVEException(e,null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZTechnicalViolationException while cancelling the transaction",e);
            transactionLogger.error("PZTechnicalViolationException while cancelling the transaction", e);

            PZExceptionHandler.handleDBCVEException(e,null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (UnknownHostException e)
        {
            logger.error("UnknownHostException while cancelling the transaction",e);
            transactionLogger.error("UnknownHostException while cancelling the transaction",e);

            PZExceptionHandler.raiseAndHandleTechnicalViolationException(CupPaymentProcess.class.getName(),"refund()",null,"common","Technical Exception", PZTechnicalExceptionEnum.UNKNOWN_HOST_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SystemError se){

            logger.debug(se);
            transactionLogger.debug(se);
            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"refund",null,"common","Db Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,se.getMessage(),se.getCause(),null,"refund");
            response.setStatus(PZResponseStatus.ERROR);
        }
        catch (SQLException e)
        {
            logger.error("Sql Exception while cancelling the transaction via CupPayment", e);
            transactionLogger.error("Sql Exception while cancelling the transaction via CupPayment", e);

            PZExceptionHandler.raiseAndHandleDBViolationException(CupPaymentProcess.class.getName(),"refund",null,"common","Db Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"refund");
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

    @Override
    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int actionEntry(String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO,AuditTrailVO auditTrailVO)
    {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
