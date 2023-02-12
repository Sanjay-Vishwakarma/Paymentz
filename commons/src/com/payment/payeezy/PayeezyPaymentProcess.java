package com.payment.payeezy;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * Created by Admin on 8/27/2019.
 */
public class PayeezyPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(PayeezyPaymentProcess.class.getName());

    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        Functions functions = new Functions();
        int trackingid = refundRequest.getTrackingId();
        transactionLogger.error("tracking id-----" + trackingid);

        HashMap hashMap = getPreviousTransactionDetails(String.valueOf(trackingid), "'authsuccessful','capturesuccess'");
        CommTransactionDetailsVO transactionDetailsVO = requestVO.getTransDetailsVO();
        if (functions.isValueNull(hashMap.get("responsehashinfo").toString()))
        {
            transactionDetailsVO.setResponseHashInfo(hashMap.get("responsehashinfo").toString());
            transactionLogger.error("responsehashinfo---" + transactionDetailsVO.getResponseHashInfo());
        }
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        Functions functions = new Functions();
        int trackingid=cancelRequest.getTrackingId();
        transactionLogger.error("trackingid-----"+ trackingid);

        HashMap hashMap=getPreviousTransactionDetails(String.valueOf(trackingid),"'authsuccessful','capturesuccess'");
        CommTransactionDetailsVO transactionDetailsVO=requestVO.getTransDetailsVO();
        if (functions.isValueNull(hashMap.get("responsehashinfo").toString()))
        {
            transactionDetailsVO.setResponseHashInfo(hashMap.get("responsehashinfo").toString());
            transactionLogger.error("responsehashinfo----"+transactionDetailsVO.getResponseHashInfo());
        }
    }

    public static HashMap getPreviousTransactionDetails(String trackingid,String status)
    {
        Connection connection=null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap hashMap=new HashMap();

        try
        {
            connection= Database.getConnection();
            String query="SELECT responsehashinfo FROM transaction_common_details WHERE trackingid='" + trackingid + "' AND status IN("+status+")  AND responsehashinfo IS NOT NULL AND responsehashinfo!='' LIMIT 1";
            ps=connection.prepareStatement(query);
            rs=ps.executeQuery();
            transactionLogger.error("query----"+ps);
            while(rs.next())
            {
                hashMap.put("responsehashinfo",rs.getString("responsehashinfo"));
                transactionLogger.error("responsehashinfo---"+hashMap.get("responsehashinfo"));
            }
        }catch (Exception e)
        {
            transactionLogger.error("Excepton----",e);
        }
        finally
        {
            Database.closeConnection(connection);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return hashMap;
    }
}
