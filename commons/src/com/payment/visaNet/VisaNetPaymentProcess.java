package com.payment.visaNet;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectCommResponseVO;
import com.manager.vo.VTResponseVO;
import com.payment.common.core.CommInquiryResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZCaptureResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 6/12/15
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class VisaNetPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(VisaNetPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(VisaNetPaymentProcess.class.getName());

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for VisaNetPaymentProcess Details");
        transactionLogger.debug("Entering ActionEntry for VisaNetPaymentProcess Details");
        VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)responseVO;
        String authCode="";
        String resultCode="";
        String resultDescription="";
        String cardSource="";
        String cardIssuerName="";
        int ECI=0;
        String ECIDescription="";
        String cvvResult="";
        String txAcqId="";
        String bankTransDate="";
        String validationDescription="";
        if(visaNetResponseVO!=null)
        {
            authCode=visaNetResponseVO.getAuthCode();
            resultCode=visaNetResponseVO.getResultCode();
            resultDescription=visaNetResponseVO.getResultDescription();
            cardSource=visaNetResponseVO.getCardSource();
            cardIssuerName=visaNetResponseVO.getCardIssuerName();
            if("auth".equals(visaNetResponseVO.getTransactionType()))
            {
                ECI=Integer.parseInt(visaNetResponseVO.getECI());
            }
            ECIDescription=visaNetResponseVO.getECIDescription();
            cvvResult=visaNetResponseVO.getCvvResult();
            txAcqId=visaNetResponseVO.getTxAcqId();
            bankTransDate=visaNetResponseVO.getBankTransDate();
            validationDescription=visaNetResponseVO.getValidationDescription();
        }
        Connection conn = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_visanet_details(detailid,trackingid,status,auth_code,result_code,result_description,card_source,cardissure_name,eci,eci_description,cvv_result,tx_acq_id,banktrans_date,validation_description) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,newDetailId);
            pstmt.setString(2,trackingId);
            pstmt.setString(3,status);
            pstmt.setString(4,authCode);
            pstmt.setString(5,resultCode);
            pstmt.setString(6,resultDescription);
            pstmt.setString(7,cardSource);
            pstmt.setString(8,cardIssuerName);
            pstmt.setInt(9,ECI);
            pstmt.setString(10,ECIDescription);
            pstmt.setString(11,cvvResult);
            pstmt.setString(12,txAcqId);
            pstmt.setString(13,bankTransDate);
            pstmt.setString(14,validationDescription);
            k = pstmt.executeUpdate();
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(VisaNetPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(VisaNetPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return k;
    }
    public void getIntegrationSpecificTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String toid)
    {
        Connection con = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        VisaNetTransactionDetailsVO visaNetTransactionDetailsVO=(VisaNetTransactionDetailsVO)transactionDetailsVO;
        try
        {
            con=Database.getConnection();
            StringBuffer stringBuffer =new StringBuffer("select auth_code,result_code,result_description,card_source,cardissure_name,eci,eci_description," +
                    "cvv_result,tx_acq_id,banktrans_date,validation_description from transaction_visanet_details where trackingid=? AND status IN('authsuccessful','authfailed')");
            preparedStatement=con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1,trackingId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                visaNetTransactionDetailsVO.setAuthCode(rs.getString("auth_code"));
                visaNetTransactionDetailsVO.setResultCode(rs.getString("result_code"));
                visaNetTransactionDetailsVO.setResultDescription(rs.getString("result_description"));
                visaNetTransactionDetailsVO.setCardSource(rs.getString("card_source"));
                visaNetTransactionDetailsVO.setCardIssuerName(rs.getString("cardissure_name"));
                visaNetTransactionDetailsVO.setECI(rs.getString("eci"));
                visaNetTransactionDetailsVO.setECIDescription(rs.getString("eci_description"));
                visaNetTransactionDetailsVO.setCvvResult(rs.getString("cvv_result"));
                visaNetTransactionDetailsVO.setTxAcqId(rs.getString("tx_acq_id"));
                visaNetTransactionDetailsVO.setBankTransDate(rs.getString("banktrans_date"));
                visaNetTransactionDetailsVO.setValidationDescription(rs.getString("validation_description"));
            }
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError:::::",systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException:::::::",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    public void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,Map transactionResponseParams) throws  PZDBViolationException
    {
        if(responseVO!=null)
        {
            VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)responseVO;
            transactionResponseParams.put("authcode",visaNetResponseVO.getAuthCode());
            transactionResponseParams.put("resultcode",visaNetResponseVO.getResultCode());
            transactionResponseParams.put("resultdescription",visaNetResponseVO.getResultDescription());
            transactionResponseParams.put("cardsource",visaNetResponseVO.getCardSource());
            transactionResponseParams.put("cardissuername",visaNetResponseVO.getCardIssuerName());
            transactionResponseParams.put("eci",visaNetResponseVO.getECI());
            transactionResponseParams.put("ecidescription",visaNetResponseVO.getECIDescription());
            transactionResponseParams.put("cvvresult",visaNetResponseVO.getCvvResult());
            transactionResponseParams.put("txacqid",visaNetResponseVO.getTxAcqId());
            transactionResponseParams.put("validationdescription",visaNetResponseVO.getValidationDescription());
            transactionResponseParams.put("banktransdate",visaNetResponseVO.getBankTransDate());
            transactionResponseParams.put("banktransid",visaNetResponseVO.getTransactionId());
        }
    }
    public void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,DirectCommResponseVO directCommResponseVO) throws  PZDBViolationException
    {
        if(responseVO!=null)
        {
            VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)responseVO;
            directCommResponseVO.setAuthCode(visaNetResponseVO.getAuthCode());
            directCommResponseVO.setResultCode(visaNetResponseVO.getResultCode());
            directCommResponseVO.setResultDescription(visaNetResponseVO.getResultDescription());
            directCommResponseVO.setCardSource(visaNetResponseVO.getCardSource());
            directCommResponseVO.setCardIssuerName(visaNetResponseVO.getCardIssuerName());
            directCommResponseVO.setEci(visaNetResponseVO.getECI());
            directCommResponseVO.setEciDescription(visaNetResponseVO.getECIDescription());
            directCommResponseVO.setCvvResult(visaNetResponseVO.getCvvResult());
            directCommResponseVO.setTxAcqId(visaNetResponseVO.getTxAcqId());
            directCommResponseVO.setValidationDescription(visaNetResponseVO.getValidationDescription());
            directCommResponseVO.setBankTransDate(visaNetResponseVO.getBankTransDate());
            directCommResponseVO.setBankTransId(visaNetResponseVO.getTransactionId());
        }
    }
    public void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,VTResponseVO vtResponseVO)
    {
        if(responseVO!=null)
        {
            VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)responseVO;
            vtResponseVO.setAuthCode(visaNetResponseVO.getAuthCode());
            vtResponseVO.setResultCode(visaNetResponseVO.getResultCode());
            vtResponseVO.setResultDescription(visaNetResponseVO.getResultDescription());
            vtResponseVO.setCardSource(visaNetResponseVO.getCardSource());
            vtResponseVO.setCardIssuerName(visaNetResponseVO.getCardIssuerName());
            vtResponseVO.setEci(visaNetResponseVO.getECI());
            vtResponseVO.setEciDescription(visaNetResponseVO.getECIDescription());
            vtResponseVO.setCvvResult(visaNetResponseVO.getCvvResult());
            vtResponseVO.setTxAcqId(visaNetResponseVO.getTxAcqId());
            vtResponseVO.setValidationDescription(visaNetResponseVO.getValidationDescription());
            vtResponseVO.setBankTransDate(visaNetResponseVO.getBankTransDate());
            vtResponseVO.setBankTransId(visaNetResponseVO.getTransactionId());
        }
    }
    public void setAddtionalResponseParameters(CommInquiryResponseVO commInquiryResponseVO,TransactionDetailsVO transactionDetailsVO)
    {
        VisaNetTransactionDetailsVO visaNetTransactionDetailsVO=(VisaNetTransactionDetailsVO)transactionDetailsVO;

        commInquiryResponseVO.setAuthCode(visaNetTransactionDetailsVO.getAuthCode());
        commInquiryResponseVO.setResultCode(visaNetTransactionDetailsVO.getResultCode());
        commInquiryResponseVO.setResultDescription(visaNetTransactionDetailsVO.getResultDescription());
        commInquiryResponseVO.setCardSource(visaNetTransactionDetailsVO.getCardSource());
        commInquiryResponseVO.setCardIssuerName(visaNetTransactionDetailsVO.getCardIssuerName());
        commInquiryResponseVO.setEci(visaNetTransactionDetailsVO.getECI());
        commInquiryResponseVO.setEciDescription(visaNetTransactionDetailsVO.getECIDescription());
        commInquiryResponseVO.setCvvResult(visaNetTransactionDetailsVO.getCvvResult());
        commInquiryResponseVO.setBankTransID(visaNetTransactionDetailsVO.getTxAcqId());
        commInquiryResponseVO.setValidationMsg(visaNetTransactionDetailsVO.getValidationDescription());
        commInquiryResponseVO.setBankTransDate(visaNetTransactionDetailsVO.getBankTransDate());
    }
    public void setCaptureResponseParams(PZCaptureResponse captureResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        if(commResponseVO!=null){
            VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)commResponseVO;
            captureResponseVO.setResultCode(visaNetResponseVO.getResultCode());
            captureResponseVO.setResultDescription(visaNetResponseVO.getResultDescription());
            captureResponseVO.setBankStatus(visaNetResponseVO.getBankTransStatus());
            captureResponseVO.setCaptureCode(visaNetResponseVO.getLote());
        }

    }
    public void setCancelResponseParams(PZCancelResponse cancelResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        if(commResponseVO!=null){
            VisaNetResponseVO visaNetResponseVO=(VisaNetResponseVO)commResponseVO;
            cancelResponseVO.setBankStatus(visaNetResponseVO.getBankTransStatus());
            cancelResponseVO.setResultCode(visaNetResponseVO.getResultCode());
            cancelResponseVO.setResultDescription(visaNetResponseVO.getResultDescription());
        }

    }

}
