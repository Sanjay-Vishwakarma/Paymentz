package com.payment.LetzPay;

import com.directi.pg.Database;
import com.directi.pg.LetzPayGatewayLogger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Admin on 3/17/2021.
 */
public class LetzPayPaymentProcess extends CommonPaymentProcess
{
    private static LetzPayGatewayLogger transactionLogger=new LetzPayGatewayLogger(LetzPayPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  LetzPayPaymentProcess Form---");
        StringBuffer form=new StringBuffer("<form name=\"creditcard_checkout\" action=\""+response3D.getUrlFor3DRedirect()+"\" method=\"POST\">");
        HashMap<String,String> formMap= (HashMap<String, String>) response3D.getRequestMap();
        if(formMap!=null)
        {
            for (Map.Entry<String, String> entry : formMap.entrySet())
            {
                form.append("<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\">");
            }
        }
        form.append("</form><script>document.creditcard_checkout.submit();</script>");
        transactionLogger.error("form--->"+form);
        return form.toString();
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  LetzPayPaymentProcess Form VT---");
        String target="target=\"_Blank\"";
        StringBuffer form=new StringBuffer("<form name=\"creditcard_checkout\" action=\""+response3D.getUrlFor3DRedirect()+"\" method=\"POST\" "+target+">");
        HashMap<String,String> formMap= (HashMap<String, String>) response3D.getRequestMap();
        if(formMap!=null)
        {
            for (Map.Entry<String, String> entry : formMap.entrySet())
            {
                form.append("<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\">");
            }
        }
        form.append("</form><script>document.creditcard_checkout.submit();</script>");
        return form.toString();
    }
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside  LetzPayPaymentProcess Form REST NB---");
        transactionLogger.error("directKitResponseVO.getBankRedirectionUrl()---->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.error("inside  LetzPayPaymentProcess Form REST---");
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.error("response3D.getUrlFor3DRedirect()---->"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        HashMap<String,String> formMap= (HashMap<String, String>) response3D.getRequestMap();
        if(formMap!=null)
        {
            for (Map.Entry<String, String> entry : formMap.entrySet())
            {
                asyncParameterVO=new AsyncParameterVO();
                asyncParameterVO.setName(entry.getKey());
                asyncParameterVO.setValue(entry.getValue());
                directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
            }
        }
    }
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        transactionLogger.error("Entered into setRefundVOParamsextension");
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        Connection con=null;
        PreparedStatement ps=null;
        String responseHashInfo="";
        try{
            con= Database.getConnection();
            String query="select responseHashInfo from transaction_common_details where status='capturesuccess' and responseHashInfo is not null and responseHashInfo!='' and trackingId=?";
            ps=con.prepareStatement(query);
            ps.setString(1, String.valueOf(refundRequest.getTrackingId()));
            ResultSet rs=ps.executeQuery();
            while(rs.next())
            {
                responseHashInfo=rs.getString("responseHashInfo");
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
    commTransactionDetailsVO.setPaymentId(responseHashInfo);
    requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionLogger.debug("Entering ActionEntry for letzPayPaymentGateway");
        System.out.println("entering");

        int results=0;
        Connection cn = null;

        try
        {
            String ifsc="";
            String bankaccount="";
            String fullname="";
            String bankRefNo="";
            String spkRefNo="";
            if(responseVO!=null){
                ifsc=responseVO.getIfsc();
                bankaccount=responseVO.getBankaccount();
                fullname=responseVO.getFullname();
                bankRefNo=responseVO.getBankRefNo();
                spkRefNo=responseVO.getSpkRefNo();
            }
            if(commRequestVO!=null){
                ifsc=commRequestVO.getTransDetailsVO().getBankIfsc();
                bankaccount=commRequestVO.getTransDetailsVO().getBankAccountNo();
                fullname=commRequestVO.getTransDetailsVO().getCustomerBankAccountName();

            }

            cn = Database.getConnection();
            String sql = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,dtstamp) values (?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, fullname);
            pstmt.setString(3, bankaccount);
            pstmt.setString(4, ifsc);
            pstmt.setString(5, amount);
            pstmt.setString(6,status);
            transactionLogger.error("SqlQuery letzpay-----" + pstmt);
            results = pstmt.executeUpdate();


        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(SafexPayPaymentGateway.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }
}
