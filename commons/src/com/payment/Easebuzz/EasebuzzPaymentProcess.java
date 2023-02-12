package com.payment.Easebuzz;

import com.directi.pg.Database;
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
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Admin on 23-Feb-22.
 */
public class EasebuzzPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(EasebuzzPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionlogger.error("inside  EasebuzzPaymentProcess Form---");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
        Iterator keys=response3D.getRequestMap().keySet().iterator();
        while (keys.hasNext())
        {
            String key= (String) keys.next();
            form+="<input type=\"hidden\" name=\""+key+"\"  value=\""+response3D.getRequestMap().get(key)+"\">";

        }
        form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionlogger.error("EasebuzzPaymentProcess Form---"+form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside EasebuzzPaymentProcess --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside EasebuzzPaymentProcess getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside  EasebuzzPaymentProcess---"+response3D.getUrlFor3DRedirect());

        Iterator keys = response3D.getRequestMap().keySet().iterator();
        String key ="";
        while (keys.hasNext())
        {
            key = (String)keys.next();
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName(key);
            asyncParameterVO.setValue(response3D.getRequestMap().get(key));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        }

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }


    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));


        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        //requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionlogger.debug("Entering ActionEntry for EasebuzzPaymentGateway");
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
            String sql = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,bankRefNo,spkRefNo,dtstamp) values (?,?,?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, fullname);
            pstmt.setString(3, bankaccount);
            pstmt.setString(4, ifsc);
            pstmt.setString(5,amount);
            pstmt.setString(6,status);
            pstmt.setString(7,bankRefNo);
            pstmt.setString(8,spkRefNo);
            results = pstmt.executeUpdate();
            transactionlogger.error("SqlQuery Easebuzz-----" + pstmt);

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

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside  ApexPay payment process---"+response3D.getUrlFor3DRedirect());

        Iterator keys   = response3D.getRequestMap().keySet().iterator();
        String key      ="";
        while (keys.hasNext())
        {
            key                 = (String)keys.next();
            asyncParameterVO    = new AsyncParameterVO();
            asyncParameterVO.setName(key);
            asyncParameterVO.setValue(response3D.getRequestMap().get(key));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        }

        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

}
