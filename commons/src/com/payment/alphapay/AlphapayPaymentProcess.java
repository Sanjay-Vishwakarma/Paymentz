package com.payment.alphapay;

import com.directi.pg.Database;
import com.directi.pg.LoadProperties;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
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
import java.util.ResourceBundle;

/**
 * Created by Admin on 05-Dec-22.
 */
public class AlphapayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AlphapayPaymentGateway.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO CommonValidatorVO,Comm3DResponseVO response3d)
    {
        transactionLogger .error("inside AlphapayPaymentProcess From----");

        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3d.getUrlFor3DRedirect()+ "\">" ;
        form  += "</form><script language=\"javascript\">window.location=\"" +response3d.getUrlFor3DRedirect()+ "\";</script>";

        transactionLogger.error(CommonValidatorVO.getTrackingid() + " AlphapayPaymentProcess" + form.toString());
        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO,CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside AlphapayPayment getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        transactionLogger.debug("inside  AlphapayPayment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setMethod("POST");
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
        transactionLogger.error(directKitResponseVO.getTrackingId()+" >>>>> "+"Method:"+directKitResponseVO.getMethod());
    }

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("inside payout vo params etension --- >");
        CommAddressDetailsVO commAddressDetailsVO           = requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = requestVO.getCardDetailsVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setCustomerBankAccountNumber(payoutRequest.getCustomerBankAccountNumber());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());
        commTransactionDetailsVO.setCustomerBankCode(payoutRequest.getBankCode());
        commTransactionDetailsVO.setCardType(commTransactionDetailsVO.getCardType());
        commTransactionDetailsVO.setBankName(payoutRequest.getBankName());
        commTransactionDetailsVO.setBranchCode(payoutRequest.getBranchCode());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        //requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionLogger.error("Entering ActionEntry for AlphapayPaymentProcess");
        int results=0;
        Connection cn = null;
        try
        {
            String ifsc         = "";
            String bankaccount  = "";
            String fullname     = "";
            String bankRefNo    = "";
            String spkRefNo     = "";

            if(responseVO != null)
            {
                ifsc            = responseVO.getIfsc();
                bankaccount     = responseVO.getBankaccount();
                fullname        = responseVO.getFullname();
                bankRefNo       = responseVO.getBankRefNo();
                spkRefNo        = responseVO.getSpkRefNo();
            }
            if(commRequestVO != null)
            {
                ifsc        = commRequestVO.getTransDetailsVO().getBankIfsc();
                bankaccount = commRequestVO.getTransDetailsVO().getBankAccountNo();
                fullname    = commRequestVO.getTransDetailsVO().getCustomerBankAccountName();
            }
            cn          = Database.getConnection();
            String sql  = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,dtstamp) values (?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1,trackingId);
            pstmt.setString(2,fullname);
            pstmt.setString(3,bankaccount);
            pstmt.setString(4,ifsc);
            pstmt.setString(5,amount);
            pstmt.setString(6,status);

            results = pstmt.executeUpdate();
            transactionLogger.error("SqlQuery IMoney-----" + pstmt);

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(AlphapayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(AlphapayPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;



    }




}
