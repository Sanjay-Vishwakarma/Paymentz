package com.payment.ems.core;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Admin on 1/26/2018.
 */
public class EMSPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(EMSPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(EMSPaymentProcess.class.getName());
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("Entering ActionEntry for EMSPaymentProcess");
        transactionLogger.debug("Entering ActionEntry for EMSPaymentProcess");

        EMSResponseVO emsResponseVO = null;
        int results=0;
        Connection cn = null;
        try
        {
            if(responseVO != null  && !responseVO.equals(""))
            {
                emsResponseVO = (EMSResponseVO) responseVO;
                //System.out.println("in payprocess---" + emsResponseVO.getServiceProvider());

                cn = Database.getConnection();
                String sql = "insert into transaction_ems_details(trackingId,serviceProvider,paymentType,procApprovalCode,procCCVResponse,procReferenceNumber,procResponseCode,procResponseMessage,tDate,tTime,tFromatedDate) values (?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement pstmt = cn.prepareStatement(sql);
                pstmt.setString(1, trackingId);
                pstmt.setString(2, emsResponseVO.getServiceProvider());
                pstmt.setString(3, emsResponseVO.getPaymentType());
                pstmt.setString(4, emsResponseVO.getpApproveCode());
                pstmt.setString(5, emsResponseVO.getpCCVResponse());
                pstmt.setString(6, emsResponseVO.getpRefNumber());
                pstmt.setString(7, emsResponseVO.getpRespCode());
                pstmt.setString(8, emsResponseVO.getRemark());
                pstmt.setString(9, emsResponseVO.gettTime());
                pstmt.setString(10, emsResponseVO.gettDate());
                pstmt.setString(11, emsResponseVO.gettDateFormat());
                results = pstmt.executeUpdate();

                transactionLogger.error("SqlQuery EMS-----" + pstmt);
            }

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(EMSPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(EMSPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\""+target+">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String form = "<form name=\"launch3D\" method=\"POST\" action=\"" + response3D.getUrlFor3DRedirect() + "\">" +
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>" +
                "<script language=\"javascript\">document.launch3D.submit();</script>";
        return form;
    }


    public void setEMSRequestVO(CommRequestVO requestVO,String trackingId,String cvv) throws PZDBViolationException{

        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(trackingId);

        commCardDetailsVO.setCardHolderName(transactionVO.getFirstName()+" "+transactionVO.getLastName());
        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        commCardDetailsVO.setCardType(transactionVO.getCardtype());
        String expdate = transactionVO.getExpdate();
        if (expdate != null)
        {
            String eDate = PzEncryptor.decryptExpiryDate(expdate);
            String expMonth = eDate.split("\\/")[0];
            String expYear = eDate.split("\\/")[1];
            commCardDetailsVO.setExpMonth(expMonth);
            commCardDetailsVO.setExpYear(expYear);
        }
        commCardDetailsVO.setcVV(cvv);
        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());

        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());
        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setCity(transactionVO.getCity());
        commAddressDetailsVO.setEmail(transactionVO.getEmailaddr());
        commAddressDetailsVO.setIp(transactionVO.getIpAddress());
        commAddressDetailsVO.setPhone(transactionVO.getTelno());
        commAddressDetailsVO.setZipCode(transactionVO.getZip());
        commAddressDetailsVO.setState(transactionVO.getState());
        commAddressDetailsVO.setStreet(transactionVO.getStreet());
        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);

        transactionLogger.error("dbStatus:::::::"+commTransactionDetailsVO.getPrevTransactionStatus());



    }

}
