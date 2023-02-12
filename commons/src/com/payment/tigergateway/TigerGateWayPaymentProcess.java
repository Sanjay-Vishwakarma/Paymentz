package com.payment.tigergateway;

import com.directi.pg.Database;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.TigerGatePayRequestVO;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by Admin on 3/8/2021.
 */
public class TigerGateWayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(TigerGateWayPaymentProcess.class.getName());


    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside TigerPay Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside TigerPay getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionlogger.debug("inside  TigerPay payment process---"+response3D.getUrlFor3DRedirect());

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

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("inside payout vo params etension -- >");
        TigerGatePayRequestVO tigerGatePayRequestVO =(TigerGatePayRequestVO) requestVO;

        CommAddressDetailsVO commAddressDetailsVO           = tigerGatePayRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = tigerGatePayRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = tigerGatePayRequestVO.getCardDetailsVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());


        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());

        tigerGatePayRequestVO.setBank_Name(payoutRequest.getBankName());
        tigerGatePayRequestVO.setBranch_Name(payoutRequest.getBranchName());
        tigerGatePayRequestVO.setBranch_Code(payoutRequest.getBranchCode());
        tigerGatePayRequestVO.setAccount_Type(payoutRequest.getAccountType());
        tigerGatePayRequestVO.setBank_Code(payoutRequest.getBankCode());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionlogger.debug("Entering ActionEntry for TigerPayPaymentGateway");
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
            pstmt.setString(5,amount);
            pstmt.setString(6,status);

            results = pstmt.executeUpdate();
            transactionlogger.error("SqlQuery TigerPay-----" + pstmt);

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(TigerGateWayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(TigerGateWayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }


}
