package com.payment.processing.core;

import com.directi.pg.Database;
import com.directi.pg.PzEncryptor;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZCaptureRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2/20/15
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessingPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ProcessingPaymentProcess.class.getName());
    public void setCaptureVOParamsExtension(CommRequestVO requestVO, PZCaptureRequest captureRequest) throws PZDBViolationException
    {

        transactionLogger.debug(" inside setCaptureVOParamsExtensions for Processing Gateway ");
        CommTransactionDetailsVO commTransactionDetailsVO =requestVO.getTransDetailsVO();

        transactionLogger.debug(" inside setCaptureVOParamsExtensions for Processing Gateway");
        Connection con=null;
        PreparedStatement psCaptureInfo=null;
        ResultSet rsCaptureInfo=null;
        try
        {
            con= Database.getConnection();

            String query="select responsehashinfo from transaction_common_details where detailid =?";
            psCaptureInfo=con.prepareStatement(query);
            psCaptureInfo.setString(1,commTransactionDetailsVO.getDetailId());
            rsCaptureInfo=psCaptureInfo.executeQuery();
            if(rsCaptureInfo.next())
            {
                commTransactionDetailsVO.setResponseHashInfo(rsCaptureInfo.getString(1));
            }

        }
        catch(SystemError e)
        {
            PZExceptionHandler.raiseDBViolationException(ProcessingPaymentProcess.class.getName(),"setCaptureVOParamsExtension",null,"common","DB Exeption", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(ProcessingPaymentProcess.class.getName(), "setCaptureVOParamsExtension", null, "common", "DB Exeption", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }

        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }
    public void setProcessingRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
    {
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

        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commTransactionDetailsVO.setCustomerId(transactionVO.getCustomerId());

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
}
