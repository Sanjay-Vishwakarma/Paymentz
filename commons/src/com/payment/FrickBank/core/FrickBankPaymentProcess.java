package com.payment.FrickBank.core;

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
import com.payment.request.PZInquiryRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/28/13
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrickBankPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(FrickBankPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FrickBankPaymentProcess.class.getName());

    private Hashtable getTransactionDetails(String trackingid, String status)
    {
        Hashtable transDetail=new Hashtable();
        return transDetail;
    }

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        int i=0;
        Connection conn= null;
        FrickBankResponseVO frickResponseVO= (FrickBankResponseVO) responseVO;
        String referanceid="";
        String shortid="";
        String fxdate="";
        if(responseVO!=null)
        {
            referanceid=frickResponseVO.getReferanceid();
            shortid=frickResponseVO.getShortID();
            fxdate=frickResponseVO.getFxdate();
        }
        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_bankfrick_details(detailid,referenceid,shortid,fxdate) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,referanceid+"");
            pstmt.setString(3,shortid+"");
            pstmt.setString(4,fxdate+"");
            i= pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(FrickBankPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(FrickBankPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }


    @Override
    public void setInquiryVOParamsExtension(CommRequestVO requestVO, PZInquiryRequest pzInquiryRequest) throws PZDBViolationException
    {
        super.setInquiryVOParamsExtension(requestVO, pzInquiryRequest);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        super.setCancelVOParamsExtension(requestVO, cancelRequest);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
