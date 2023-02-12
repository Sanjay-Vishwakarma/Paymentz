package com.payment;

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
import com.payment.ipaydna.core.message.IPayDNAResponseVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 1/10/14
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class IPAYDNAPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(IPAYDNAPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(IPAYDNAPaymentProcess.class.getName());


    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        log.debug("enter in ipaydna actionentry extension");
        transactionLogger.debug("enter in ipaydna actionentry extension");
        int i=0;
        Connection conn= null;
        IPayDNAResponseVO iPayDNAresVO= (IPayDNAResponseVO) responseVO;
        String INVOICENO="";
        String AUTHORIZATIONCODE="";
        String BATCHID="";
        String AVSRESPONSE="";
        String REFERRALORDERREFERENCE="";
        String SETTLEMENTDATE="";
        String SETTLEMENTSTATUSTEXT="";



        if(responseVO!=null)
        {
            INVOICENO=iPayDNAresVO.getINVOICENO();
            AUTHORIZATIONCODE=iPayDNAresVO.getAuthorizationCode();
            BATCHID=iPayDNAresVO.getBatchID();
            AVSRESPONSE=iPayDNAresVO.getAVSResponse();
            REFERRALORDERREFERENCE=iPayDNAresVO.getReferralOrderReference();
            SETTLEMENTDATE=iPayDNAresVO.getSettlementDate();
            SETTLEMENTSTATUSTEXT=iPayDNAresVO.getSettlementStatusText();
        }

        try
        {
            conn=Database.getConnection();
            String sql="insert into transaction_ipaydna_details(detailid,INVOICENO,AUTHORIZATIONCODE,BATCHID,AVSRESPONSE,REFERRALORDERREFERENCE,SETTLEMENTDATE,SETTLEMENTSTATUSTEXT) values (?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,newDetailId+"");
            pstmt.setString(2,INVOICENO+"");
            pstmt.setString(3,AUTHORIZATIONCODE+"");
            pstmt.setString(4,BATCHID+"");
            pstmt.setString(5,AVSRESPONSE+"");
            pstmt.setString(6,REFERRALORDERREFERENCE+"");
            pstmt.setString(7,SETTLEMENTDATE+"");
            pstmt.setString(8,SETTLEMENTSTATUSTEXT+"");



            i= pstmt.executeUpdate();

        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(IPAYDNAPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(IPAYDNAPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return i;
    }
}
