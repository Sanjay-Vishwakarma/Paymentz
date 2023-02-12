package com.payment.Ecospend;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 1/14/2022.
 */
public class EcospendPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(EcospendPaymentProcess.class.getName());


    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        EcospendRequestVo ecospendRequestVo = (EcospendRequestVo) requestVO;
        String  trackingId = payoutRequest.getTrackingId()+"";

        EcospendRequestVo ecospendRequestVo2 = getTransactionDetails(trackingId);

        if(ecospendRequestVo2 != null){
            ecospendRequestVo.setBankid(ecospendRequestVo2.getBankid());
            ecospendRequestVo.setReference(ecospendRequestVo2.getReference());
            ecospendRequestVo.setCreditortype(ecospendRequestVo2.getCreditortype());
            ecospendRequestVo.setCreditorID(ecospendRequestVo2.getCreditorID());
            ecospendRequestVo.setCreditorName(ecospendRequestVo2.getCreditorName());
            ecospendRequestVo.setCreditorCurrency(ecospendRequestVo2.getCreditorCurrency());
            ecospendRequestVo.setCreditorBic(ecospendRequestVo2.getCreditorBic());
        }

    }

    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        EcospendRequestVo ecospendRequestVo = (EcospendRequestVo) requestVO;
        String  trackingId                  = refundRequest.getTrackingId()+"";

        CommTransactionDetailsVO commTransactionDetailsVO = requestVO.getTransDetailsVO();

        EcospendRequestVo ecospendRequestVo2 = getTransactionDetails(trackingId);
        String paymentId                     = getPaymentId(trackingId);

        if(ecospendRequestVo2 != null){
            ecospendRequestVo.setBankid(refundRequest.getBankId());
            ecospendRequestVo.setReference(ecospendRequestVo2.getReference());
            ecospendRequestVo.setCreditortype(ecospendRequestVo2.getCreditortype());
            ecospendRequestVo.setCreditorID(ecospendRequestVo2.getCreditorID());
            ecospendRequestVo.setCreditorName(ecospendRequestVo2.getCreditorName());
            ecospendRequestVo.setCreditorCurrency(ecospendRequestVo2.getCreditorCurrency());
            ecospendRequestVo.setCreditorBic(ecospendRequestVo2.getCreditorBic());
        }
        commTransactionDetailsVO.setPreviousTransactionId(paymentId);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }


    public EcospendRequestVo  getTransactionDetails(String trackingid)
    {
        Connection conn         = null;
        PreparedStatement p1    = null;
        ResultSet rs            = null;
        EcospendRequestVo ecospendRequestVo =  null;
        try
        {
            conn            = Database.getConnection();
            String query    = "SELECT bank_id,reference,dbtype,dbidentification,dbowner_name,crcurrency,crbic from transaction_ecospend_detaills WHERE trackingid=?";
            p1              = conn.prepareStatement(query);
            p1.setString(1, trackingid);
            transactionLogger.error("getTransactionDetails ----> "+" "+trackingid +" "+p1);
            rs = p1.executeQuery();

            if (rs.next())
            {
                ecospendRequestVo = new EcospendRequestVo();

                ecospendRequestVo.setBankid(rs.getString("bank_id"));
                ecospendRequestVo.setReference(rs.getString("reference"));
                ecospendRequestVo.setCreditortype(rs.getString("dbtype"));
                ecospendRequestVo.setCreditorID(rs.getString("dbidentification"));
                ecospendRequestVo.setCreditorName(rs.getString("dbowner_name"));
                ecospendRequestVo.setCreditorCurrency(rs.getString("crcurrency"));
                ecospendRequestVo.setCreditorBic(rs.getString("crbic"));
            }

        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError::::::", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException::::::", e);
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closeConnection(conn);
        }
        return ecospendRequestVo;
    }


    public String getPaymentId(String trackingId) throws PZDBViolationException
    {
        transactionLogger.error("Entered into setRefundVOParamsextension");
        Connection con          = null;
        PreparedStatement ps    = null;
        String paymentid        = "";
        try{
            con             = Database.getConnection();
            String query    = "select paymentid from transaction_common where trackingId=?";
            ps              = con.prepareStatement(query);
            ps.setString(1, trackingId);
            ResultSet rs    = ps.executeQuery();
            while(rs.next())
            {
                paymentid   = rs.getString("paymentid");
            }


        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError---->",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError---->",e);
        }finally{
            Database.closeConnection(con);
        }
        return paymentid;
    }
}
