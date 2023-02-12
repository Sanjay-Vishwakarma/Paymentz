package com.payment.credorax.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.CredoraxPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.payvision.core.PayVisionRequestVO;
import com.payment.payvision.core.PayVisionResponseVO;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZInquiryResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 5/17/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class CredoraxPaymentProcess extends CommonPaymentProcess
{

    private static Logger log = new Logger(CredoraxPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CredoraxPaymentProcess.class.getName());

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        PZInquiryResponse response = new PZInquiryResponse();
        try{
        CredoraxPaymentGateway gateway = new CredoraxPaymentGateway("306");
        gateway.processInquiry(pzInquiryRequest);
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while inquiring via Credorax",e);
            transactionLogger.error("PZTechnicalViolationException while inquiring via Credorax",e);
            PZExceptionHandler.handleTechicalCVEException(e,null,"Inquiry");
        }

        response.setTrackingId("tr1");
        response.setResponseTransactionId("tr2");
        response.setResponseTransactionStatus("st1");
        response.setResponseCode("co1");
        response.setResponseDescription("de1");
        response.setResponseDescriptor("de2");
        return response;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        setCredoraxRequestParameters(requestVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        setCredoraxRequestParameters(requestVO);
    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        String orderId = setCredoraxRequestParameters(requestVO);
        CredoraxRequestVO credoraxRequestVO = (CredoraxRequestVO) requestVO;
        credoraxRequestVO.getTransDetailsVO().setOrderId(orderId);
    }

    private String setCredoraxRequestParameters(CommRequestVO requestVO) throws PZDBViolationException
    {
        CredoraxRequestVO credoraxRequestVO = (CredoraxRequestVO) requestVO;
        String trackingId = credoraxRequestVO.getTransDetailsVO().getOrderId();

        String authCode = "";
        String status = "";
        String detailId = "";

        Connection conn = null;

        try
        {

            conn = Database.getConnection();
            String transaction_details = "select * from transaction_credorax_details where trackingid=? and (status = 'authsuccessful' or status = 'capturesuccess') and authcode not in ('null','')";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, Integer.parseInt(trackingId));
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                authCode = rsTransDetails.getString("authcode");
                status = rsTransDetails.getString("status");
                detailId = rsTransDetails.getString("detailid");
            }
        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(CredoraxPaymentProcess.class.getName(),"setCredoraxRequestParameters()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(CredoraxPaymentProcess.class.getName(), "setCredoraxRequestParameters()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        credoraxRequestVO.setResponseAuthCode(authCode);
        if (status.equals("authsuccessful"))
            return detailId;
        else
            return trackingId;
    }

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {

        log.debug("Entering ActionEntry for Credorax Details");
        transactionLogger.debug("Entering ActionEntry for Credorax Details");

        String authCode = "";
        int results=0;
        if (responseVO != null)
        {
            CredoraxResponseVO credoraxResponseVO = (CredoraxResponseVO) responseVO;
            authCode = credoraxResponseVO.getResponseAuthCode();

        }
        Connection cn = null;
        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_credorax_details(detailid,trackingid,amount,status,authcode) values (?,?,?,?,?)";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1, newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, status);
            pstmt.setString(5, authCode);

            results = pstmt.executeUpdate();


        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(CredoraxPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(CredoraxPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

}
