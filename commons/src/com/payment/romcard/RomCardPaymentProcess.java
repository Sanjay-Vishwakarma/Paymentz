package com.payment.romcard;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.payment.PZTransactionStatus;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Rihen on 12/18/2018.
 */

public class RomCardPaymentProcess extends CommonPaymentProcess
{
    static private TransactionLogger transactionLogger= new TransactionLogger(RomCardPaymentProcess.class.getName());

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = response3D.getUrlFor3DRedirect();
        return form;
    }


    public static HashMap getDetails(String status,String trackingid){
        Connection conn=null;
        PreparedStatement ps=null;
        HashMap<String ,String>  hashMap=null;
        ResultSet rs=null;
        try
        {
            hashMap= new HashMap<String ,String>();
            conn = Database.getConnection();
            String sqlQuery = "Select responsetransactionid,responsehashinfo from transaction_common_details where status=? and trackingid=?";
            ps=conn.prepareStatement(sqlQuery);
            ps.setString(1,status);
            ps.setString(2,trackingid);
            rs= ps.executeQuery();
            if(rs.next())
            {
                hashMap.put("int_ref",rs.getString("responsetransactionid"));
                hashMap.put("rrn",rs.getString("responsehashinfo"));
            }
            transactionLogger.debug("SqlQuery-----"+ps);
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError-----",se);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException-----", e);
        }
        finally
        {
            Database.closeConnection(conn);
            Database.closePreparedStatement(ps);
            Database.closeResultSet(rs);
        }
        return hashMap;
    }


    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        transactionLogger.error("-----inside setCaptureVOParamsExtension-----");
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO= requestVO.getTransDetailsVO();
        HashMap<String,String> hashMap=getDetails(PZTransactionStatus.AUTH_SUCCESS.toString(),String.valueOf(captureRequest.getTrackingId()));
        if(hashMap!=null && !hashMap.isEmpty())
        {
            String int_ref="";
            String rrn="";

            if(functions.isValueNull(hashMap.get("int_ref")))
            {
                int_ref=hashMap.get("int_ref");
            }
            if(functions.isValueNull(hashMap.get("rrn")))
            {
                rrn=hashMap.get("rrn");
            }
            transactionLogger.debug("int_ref-----"+int_ref);
            transactionLogger.debug("rrn-----"+rrn);

            commTransactionDetailsVO.setResponseHashInfo(rrn);
            requestVO.setTransDetailsVO(commTransactionDetailsVO);
        }
    }


    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        transactionLogger.error("-----inside setCancelVOParamsExtension-----");
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO= requestVO.getTransDetailsVO();
        HashMap<String,String> hashMap=getDetails(PZTransactionStatus.AUTH_SUCCESS.toString(),String.valueOf(cancelRequest.getTrackingId()));
        if(hashMap!=null && !hashMap.isEmpty())
        {
            String int_ref="";
            String rrn="";

            if(functions.isValueNull(hashMap.get("int_ref")))
            {
                int_ref=hashMap.get("int_ref");
            }
            if(functions.isValueNull(hashMap.get("rrn")))
            {
                rrn=hashMap.get("rrn");
            }
            transactionLogger.debug("CANCEL int_ref-----"+int_ref);
            transactionLogger.debug("CANCEL rrn-----"+rrn);

            commTransactionDetailsVO.setResponseHashInfo(rrn);
            requestVO.setTransDetailsVO(commTransactionDetailsVO);
        }
    }


    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        transactionLogger.error("-----inside setRefundVOParamsextension-----");
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO= requestVO.getTransDetailsVO();
        HashMap<String,String> hashMap=getDetails(PZTransactionStatus.CAPTURE_SUCCESS.toString(),String.valueOf(refundRequest.getTrackingId()));
        if(hashMap!=null && !hashMap.isEmpty())
        {
            String int_ref="";
            String rrn="";

            if(functions.isValueNull(hashMap.get("int_ref")))
            {
                int_ref=hashMap.get("int_ref");
            }
            if(functions.isValueNull(hashMap.get("rrn")))
            {
                rrn=hashMap.get("rrn");
            }
            transactionLogger.debug("REFUND int_ref-----"+int_ref);
            transactionLogger.debug("REFUND rrn-----"+rrn);

            commTransactionDetailsVO.setResponseHashInfo(rrn);
            requestVO.setTransDetailsVO(commTransactionDetailsVO);
        }
    }

}