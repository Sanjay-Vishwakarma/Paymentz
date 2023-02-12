package com.payment.websecpay.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZInquiryRequest;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jignesh.r
 * Date: Aug 9, 2013
 * Time: 12:47:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class WSecPaymentProcess extends CommonPaymentProcess
{

    private static Logger log = new Logger(WSecPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WSecPaymentProcess.class.getName());

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for Payvision Details");
        transactionLogger.debug("Entering ActionEntry for Payvision Details");


        String ipCountry = "";
        String cardCountry = "";
        int results=0;
        if (requestVO != null)
        {

        }


        if (responseVO != null)
        {

            WSecResponseVO wSecResponseVO = (WSecResponseVO) responseVO;
            ipCountry = wSecResponseVO.getIpCountry();
            cardCountry = wSecResponseVO.getCardCountry();

        }
        Connection cn = null;
        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_wsec_details(detailid,trackingid,amount,status,ipcountry,cardcountry) values (?,?,?,?,?,?)";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1, newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, status);
            pstmt.setString(5, ipCountry);
            pstmt.setString(6, cardCountry);
            results = pstmt.executeUpdate();


        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(WSecPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(WSecPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }

    @Override
    public void setTransactionVOParamsExtension(CommRequestVO requestVO, Map transactionRequestPArams) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(String.valueOf(transactionRequestPArams.get("accountid")), wSecRequestVO);
    }

    public void setTransactionVOParamsExtension(CommRequestVO requestVO, CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(), wSecRequestVO);
    }

    @Override
    public void setTransactionVOExtension(CommRequestVO commRequestVO,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) commRequestVO;

            setDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(), wSecRequestVO);

    }

    private void setDetails(String accountid, WSecRequestVO wSecRequestVO) throws PZDBViolationException
    {
        Connection conn = null;

        String key = "";
        try
        {
            conn = Database.getConnection();
            String getKeyQuery = "select * from gateway_accounts_wsec where accountid = ?";
            PreparedStatement ps = conn.prepareStatement(getKeyQuery);

            ps.setString(1, accountid);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
            {
                wSecRequestVO.setKey(rs.getString("accountkey"));
                wSecRequestVO.setSiteUrl(rs.getString("siteurl"));

            }

        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(WSecPaymentProcess.class.getName(),"setDetails()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(WSecPaymentProcess.class.getName(), "setDetails()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }

    }

    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(String.valueOf(captureRequest.getAccountId()), wSecRequestVO);
    }

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(String.valueOf(refundRequest.getAccountId()), wSecRequestVO);
    }

    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(String.valueOf(pzInquiryRequest.getAccountId()), wSecRequestVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        WSecRequestVO wSecRequestVO = (WSecRequestVO) requestVO;
        setDetails(String.valueOf(cancelRequest.getAccountId()), wSecRequestVO);
    }
}
