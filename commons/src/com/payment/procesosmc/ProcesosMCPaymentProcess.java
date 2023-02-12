package com.payment.procesosmc;

import com.directi.pg.*;
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
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
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
 * Date: 5/19/15
 * Time: 05:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesosMCPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(ProcesosMCPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ProcesosMCPaymentProcess.class.getName());

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for ProcesosMCDetails");
        transactionLogger.debug("Entering ActionEntry for ProcesosMCDetails");
        ProcesosMCResponseVO  procesosMCResponseVO=(ProcesosMCResponseVO)responseVO;

        String authCode="";
        String refNumber="";
        int numberOfFees=0;
        String firstFeeDate="";
        String feeCurrency="";
        double feeAmount=0.00;
        String resultCode="";
        String resultDescription="";
        String txAcqId="";
        String cardCountryCode="";
        String bankTransDate="";
        String bankTransTime="";
        Functions  functions=new Functions();

        if(procesosMCResponseVO!=null)
        {
            authCode=procesosMCResponseVO.getAuthCode();
            refNumber=procesosMCResponseVO.getRefNumber();
            if(functions.isValueNull(procesosMCResponseVO.getNumberOfFees()))
            {
                numberOfFees=Integer.parseInt(procesosMCResponseVO.getNumberOfFees());
            }
            firstFeeDate=procesosMCResponseVO.getFirstFeeDate();
            feeCurrency=procesosMCResponseVO.getFeeCurrency();
            feeAmount=procesosMCResponseVO.getFeeAmount();
            resultCode=procesosMCResponseVO.getResultCode();
            resultDescription=procesosMCResponseVO.getResultDescription();
            txAcqId=procesosMCResponseVO.getTxAcqId();
            cardCountryCode=procesosMCResponseVO.getCardCountryCode();
            bankTransDate=procesosMCResponseVO.getBankTransDate();
            bankTransTime=procesosMCResponseVO.getBankTransTime();
        }
        int results=0;
        Connection cn = null;
        try
        {
            cn = Database.getConnection();
            String sql = "insert into transaction_procesosmc_details(detailid,trackingid,status,authcode,referencePMP,number_of_fees,first_fee_date,fee_currency,fee_amount,result_code,result_description,tx_acq_id,banktrandate,banktranstime,card_countrycode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setInt(1,newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3,status);
            pstmt.setString(4,authCode);
            pstmt.setString(5,refNumber);
            pstmt.setInt(6, numberOfFees);
            pstmt.setString(7, firstFeeDate);
            pstmt.setString(8,feeCurrency);
            pstmt.setDouble(9, feeAmount);
            pstmt.setString(10, resultCode);
            pstmt.setString(11,resultDescription);
            pstmt.setString(12,txAcqId);
            pstmt.setString(13,bankTransDate);
            pstmt.setString(14,bankTransTime);
            pstmt.setString(15,cardCountryCode);
            results = pstmt.executeUpdate();
        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(ProcesosMCPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ProcesosMCPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }
    @Override
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {
        setProcesosMCRequestParameters(requestVO);
    }

    @Override
    public void setCancelVOParamsExtension(CommRequestVO requestVO, PZCancelRequest cancelRequest) throws PZDBViolationException
    {
        setProcesosMCRequestParameters(requestVO);
    }
    private void setProcesosMCRequestParameters(CommRequestVO requestVO) throws PZDBViolationException
    {
        ProcesosMCRequestVO procesosMCRequestVO=(ProcesosMCRequestVO)requestVO;
        String trackingId = procesosMCRequestVO.getTransDetailsVO().getOrderId();

        String authCode = "";
        String refNumber="";
        String bankTransDate = "";
        String bankTransTime = "";
        String detailId = "";

        Connection conn = null;

        try
        {
            conn = Database.getConnection();
            String transaction_details = "select * from transaction_procesosmc_details where trackingid=? and (status = 'authsuccessful' or status = 'capturesuccess') and authcode IS NOT NULL";
            PreparedStatement transDetailsprepstmnt = conn.prepareStatement(transaction_details);
            transDetailsprepstmnt.setInt(1, Integer.parseInt(trackingId));
            ResultSet rsTransDetails = transDetailsprepstmnt.executeQuery();
            if (rsTransDetails.next())
            {
                authCode = rsTransDetails.getString("authcode");
                bankTransDate=rsTransDetails.getString("banktrandate");
                bankTransTime=rsTransDetails.getString("banktranstime");
                refNumber=rsTransDetails.getString("referencePMP");
                detailId = rsTransDetails.getString("detailid");
            }
        }

        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ProcesosMCPaymentProcess.class.getName(),"setProcesosMCRequestParameters()",null,"common","DB Exception",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(ProcesosMCPaymentProcess.class.getName(), "setProcesosMCRequestParameters()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        procesosMCRequestVO.setAuthCode(authCode);
        procesosMCRequestVO.setRefNumber(refNumber);
        procesosMCRequestVO.setBankTransDate(bankTransDate);
        procesosMCRequestVO.setBankTransTime(bankTransTime);

    }
    public void getIntegrationSpecificTransactionDetails(TransactionDetailsVO transactionDetailsVO,String trackingId,String toid)
    {
        Connection con = null;
        PreparedStatement preparedStatement=null;
        ResultSet rs=null;
        ProcesosMCTransactionDetailsVO procesosMCTransactionDetailsVO=(ProcesosMCTransactionDetailsVO)transactionDetailsVO;
        try
        {
            con=Database.getConnection();
            StringBuffer stringBuffer =new StringBuffer("select authcode,referencePMP,number_of_fees,first_fee_date," +
                    "fee_currency,fee_amount,result_code,result_description,tx_acq_id,card_countrycode,banktrandate,banktranstime from transaction_procesosmc_details where trackingid=? AND status IN('authsuccessful','authfailed')");
            preparedStatement=con.prepareStatement(stringBuffer.toString());
            preparedStatement.setString(1,trackingId);
            rs=preparedStatement.executeQuery();
            if(rs.next())
            {
                procesosMCTransactionDetailsVO.setAuthCode(rs.getString("authcode"));
                procesosMCTransactionDetailsVO.setReferencePMP(rs.getString("referencePMP"));
                procesosMCTransactionDetailsVO.setNumberOFFees(rs.getString("number_of_fees"));
                procesosMCTransactionDetailsVO.setFirstFeeDate(rs.getString("first_fee_date"));
                procesosMCTransactionDetailsVO.setFeeCurrency(rs.getString("fee_currency"));
                procesosMCTransactionDetailsVO.setFeeAmount(rs.getString("fee_amount"));
                procesosMCTransactionDetailsVO.setResultCode(rs.getString("result_code"));
                procesosMCTransactionDetailsVO.setResultDescription(rs.getString("result_description"));
                procesosMCTransactionDetailsVO.setTxAcqId(rs.getString("tx_acq_id"));
                procesosMCTransactionDetailsVO.setCardCountryCode(rs.getString("card_countrycode"));
                procesosMCTransactionDetailsVO.setBankTransDate(rs.getString("banktrandate"));
                procesosMCTransactionDetailsVO.setBankTransTime(rs.getString("banktranstime"));
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
            ProcesosMCResponseVO procesosMCResponseVO=(ProcesosMCResponseVO)responseVO;
            transactionResponseParams.put("authcode",procesosMCResponseVO.getAuthCode());
            transactionResponseParams.put("resultcode",procesosMCResponseVO.getResultCode());
            transactionResponseParams.put("resultdescription",procesosMCResponseVO.getResultDescription());
            transactionResponseParams.put("validationdescription","");
            transactionResponseParams.put("banktransdate",procesosMCResponseVO.getBankTransDate());
            transactionResponseParams.put("txacqid",procesosMCResponseVO.getTxAcqId());
            transactionResponseParams.put("cardcountrycode",procesosMCResponseVO.getCardCountryCode());
        }
    }
    public void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,DirectCommResponseVO directCommResponseVO) throws  PZDBViolationException
    {
        if(responseVO!=null)
        {
            ProcesosMCResponseVO procesosMCResponseVO=(ProcesosMCResponseVO)responseVO;
            directCommResponseVO.setAuthCode(procesosMCResponseVO.getAuthCode());
            directCommResponseVO.setResultCode(procesosMCResponseVO.getResultCode());
            directCommResponseVO.setResultDescription(procesosMCResponseVO.getResultDescription());
            directCommResponseVO.setValidationDescription("");
            directCommResponseVO.setBankTransDate(procesosMCResponseVO.getBankTransDate());
            directCommResponseVO.setTxAcqId(procesosMCResponseVO.getTxAcqId());
            directCommResponseVO.setCardCountryCode(procesosMCResponseVO.getCardCountryCode());
        }
    }
    public void setTransactionResponseVOParamsExtension(CommResponseVO responseVO,VTResponseVO vtResponseVO)
    {
        if(responseVO!=null)
        {
            ProcesosMCResponseVO procesosMCResponseVO=(ProcesosMCResponseVO)responseVO;
            vtResponseVO.setAuthCode(procesosMCResponseVO.getAuthCode());
            vtResponseVO.setResultCode(procesosMCResponseVO.getResultCode());
            vtResponseVO.setResultDescription(procesosMCResponseVO.getResultDescription());
            vtResponseVO.setValidationDescription("");
            vtResponseVO.setBankTransDate(procesosMCResponseVO.getBankTransDate());
            vtResponseVO.setTxAcqId(procesosMCResponseVO.getTxAcqId());
            vtResponseVO.setCardCountryCode(procesosMCResponseVO.getCardCountryCode());
        }
    }
    public void setAddtionalResponseParameters(CommInquiryResponseVO commInquiryResponseVO,TransactionDetailsVO transactionDetailsVO)
    {
        ProcesosMCTransactionDetailsVO procesosMCTransactionDetailsVO=(ProcesosMCTransactionDetailsVO) transactionDetailsVO;
        commInquiryResponseVO.setAuthCode(procesosMCTransactionDetailsVO.getAuthCode());
        commInquiryResponseVO.setNumberOfFees(procesosMCTransactionDetailsVO.getNumberOFFees());
        commInquiryResponseVO.setFirstFeeDate(procesosMCTransactionDetailsVO.getFirstFeeDate());
        commInquiryResponseVO.setFeeCurrency(procesosMCTransactionDetailsVO.getFeeCurrency());
        commInquiryResponseVO.setFeeAmount(procesosMCTransactionDetailsVO.getFeeAmount());
        commInquiryResponseVO.setResultCode(procesosMCTransactionDetailsVO.getResultCode());
        commInquiryResponseVO.setResultDescription(procesosMCTransactionDetailsVO.getResultDescription());
        commInquiryResponseVO.setBankTransID(procesosMCTransactionDetailsVO.getTxAcqId());
        commInquiryResponseVO.setCardCountryCode(procesosMCTransactionDetailsVO.getCardCountryCode());
        commInquiryResponseVO.setBankTransDate(procesosMCTransactionDetailsVO.getBankTransDate());
        commInquiryResponseVO.setValidationMsg(procesosMCTransactionDetailsVO.getResultDescription());
    }
    public void setCaptureResponseParams(PZCaptureResponse captureResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        if(commResponseVO!=null){
            ProcesosMCResponseVO mcResponseVO=(ProcesosMCResponseVO)commResponseVO;
            captureResponseVO.setResultCode(mcResponseVO.getResultCode());
            captureResponseVO.setResultDescription(mcResponseVO.getResultDescription());
            captureResponseVO.setBankStatus("");
            captureResponseVO.setCaptureCode(mcResponseVO.getLote());
        }
    }
    public void setCancelResponseParams(PZCancelResponse cancelResponseVO,CommResponseVO commResponseVO) throws PZDBViolationException
    {
        if(commResponseVO!=null){
            ProcesosMCResponseVO mcResponseVO=(ProcesosMCResponseVO)commResponseVO;
            cancelResponseVO.setBankStatus("");
            cancelResponseVO.setResultCode(mcResponseVO.getResultCode());
            cancelResponseVO.setResultDescription(mcResponseVO.getResultDescription());
        }

    }
}
