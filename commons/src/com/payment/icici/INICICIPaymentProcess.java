package com.payment.icici;

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
import com.payment.procesosmc.ProcesosMCRequestVO;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;
import com.payment.visaNet.VisaNetResponseVO;

import java.awt.*;
import java.sql.*;

/**
 * Created by Kiran on 27/6/15.
 */
public class INICICIPaymentProcess extends CommonPaymentProcess
{

    private static Logger log = new Logger(INICICIPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(INICICIPaymentProcess.class.getName());


    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {

    log.debug("Entering ActionEntry for INICICIProcess Details");
    transactionLogger.debug("Entering ActionEntry for INICICIProcess Details");

    String authCode = "";
    String rrNumber="";
    String RTSRNumber="";

        INICICIResponseVO iniciciResponseVO=(INICICIResponseVO)responseVO;

     if(iniciciResponseVO!=null)
    {
        authCode=iniciciResponseVO.getAuthCode();
        rrNumber=iniciciResponseVO.getRRNumber();
        RTSRNumber=iniciciResponseVO.getRTSRNumber();
    }

    Connection conn = null;
    int k=0;
    try
    {
        conn = Database.getConnection();
        StringBuffer stringBuffer =new StringBuffer("insert into transaction_inicici_details(detailid,trackingid,status,rrnumber,authcode,RTSRNumber) values (?,?,?,?,?,?)");
        PreparedStatement pstmt = conn.prepareStatement(stringBuffer.toString());
        pstmt.setInt(1, newDetailId);
        pstmt.setString(2, trackingId);
        pstmt.setString(3, status);
        pstmt.setString(4,rrNumber);
        pstmt.setString(5, authCode);
        pstmt.setString(6,RTSRNumber);
        k=pstmt.executeUpdate();
//        transactionLogger.debug("IN  actionEntryExtension of INICICIPaymentProcess(query result) "+stringBuffer);
    }

    catch(SQLException se){
        transactionLogger.error(""+se);
        PZExceptionHandler.raiseDBViolationException(INICICIPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
    }

    catch(SystemError systemError){
        transactionLogger.error(""+systemError);
        PZExceptionHandler.raiseDBViolationException(INICICIPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
    }

    finally

    {
        Database.closeConnection(conn);
    }
        return k;
}

    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        setINICICIRequestParameters(requestVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        setINICICIRequestParameters(requestVO);
    }
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        setINICICIRequestParameters(requestVO);
    }
    private void setINICICIRequestParameters(CommRequestVO requestVO) throws PZDBViolationException
    {
        INICICIRequestVO iniciciRequestVO=(INICICIRequestVO)requestVO;
        String trackingId = iniciciRequestVO.getTransDetailsVO().getOrderId();

        String authCode = "";
        String rrNumber="";
        String RTSRNumber="";

        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            StringBuffer transaction_details =new StringBuffer("select authcode,rrnumber,RTSRNumber from transaction_inicici_details where trackingid=? and (status = 'authsuccessful' or status = 'capturesuccess') and authcode IS NOT NULL");
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details.toString());
            transDetailsprepstmnt.setInt(1, Integer.parseInt(trackingId));
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
//            transactionLogger.debug("IN  setINICICIRequestParameters of INICICIPaymentProcess(query result) "+transaction_details);
            if (rsTransDetails.next())
            {
                authCode = rsTransDetails.getString("authcode");
                rrNumber=rsTransDetails.getString("rrnumber");
                RTSRNumber=rsTransDetails.getString("RTSRNumber");
            }
        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(INICICIPaymentProcess.class.getName(),"setProcesosMCRequestParameters()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(INICICIPaymentProcess.class.getName(), "setProcesosMCRequestParameters()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        iniciciRequestVO.setAuthCode(authCode);
        iniciciRequestVO.setRRNumber(rrNumber);
        iniciciRequestVO.setRTSRNumber(RTSRNumber);
    }

}
