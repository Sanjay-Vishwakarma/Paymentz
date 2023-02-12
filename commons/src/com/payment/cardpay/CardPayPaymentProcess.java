package com.payment.cardpay;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Admin on 7/26/2018.
 */
public class CardPayPaymentProcess extends CommonPaymentProcess
{
    private static CardPayLogger transactionLogger= new CardPayLogger(CardPayPaymentProcess.class.getName());
    @Override
    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        transactionLogger.error("get3DConfirmationFormVT " +trackingId+" "+ form);
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        transactionLogger.error("3d page displayed....." + response3D.getThreeDSMethodData());
        String  ThreeDSMethod ="";
        String form="";

        if(response3D.getThreeDSMethodData() != null){
            ThreeDSMethod =response3D.getThreeDSMethodData();
        }


        if("3Dsv2".equalsIgnoreCase(ThreeDSMethod)){
            /*form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                    "</form>"+
                    "<script language=\"javascript\"> document.launch3D.submit(); </script>";*/

            form += "<form name=\"launch3D\" method=\"GET\" action=\"" + response3D.getUrlFor3DRedirect() + "\">";
            form += "</form><script language=\"javascript\">window.location=\"" + response3D.getUrlFor3DRedirect() + "\";</script>";
        }else{
             form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                    "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                    "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                    "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                    "</form>"+
                    "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        }

        /*String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";*/
        transactionLogger.error("get3DConfirmationForm " +trackingId+" "+ form);
        return form;
    }

    public void setCPRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
    {
        Functions functions= new Functions();
        CommRequestVO commRequestVO                         = (CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO            = new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO             = new CommCardDetailsVO();
        CommMerchantVO commMerchantVO                   = new CommMerchantVO();
        TransactionManager transactionManager           = new TransactionManager();
        TransactionDetailsVO transactionVO              = transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        transactionLogger.debug("dbStatus----"+commTransactionDetailsVO.getPrevTransactionStatus());
        transactionLogger.debug("paymentid----"+commTransactionDetailsVO.getPaymentId());
        transactionLogger.debug("getPaymentId----"+transactionVO.getPaymentId());
        transactionLogger.debug("accountid----"+transactionVO.getAccountId());

        commTransactionDetailsVO.setCurrency(transactionVO.getCurrency());
        commTransactionDetailsVO.setAmount(transactionVO.getAmount());
        commTransactionDetailsVO.setOrderId(transactionVO.getDescription());
        commTransactionDetailsVO.setOrderDesc(transactionVO.getOrderDescription());
        commTransactionDetailsVO.setPaymentId(transactionVO.getPaymentId());
        commTransactionDetailsVO.setNotificationUrl(transactionVO.getNotificationUrl());
        commTransactionDetailsVO.setVersion(transactionVO.getVersion());
        commTransactionDetailsVO.setTerminalId(transactionVO.getTerminalId());

        commTransactionDetailsVO.setToId(transactionVO.getToid());
        commTransactionDetailsVO.setPaymentType(transactionVO.getPaymodeId());
        commTransactionDetailsVO.setCardType(transactionVO.getCardTypeId());
        commTransactionDetailsVO.setRedirectUrl(transactionVO.getRedirectURL());
        commTransactionDetailsVO.setRedirectMethod(transactionVO.getRedirectMethod());
        commTransactionDetailsVO.setTransactionmode(transactionVO.getTransactionMode());

        commAddressDetailsVO.setCountry(transactionVO.getCountry());
        commAddressDetailsVO.setCustomerid(transactionVO.getCustomerId());
        commAddressDetailsVO.setEmail(transactionVO.getEmailaddr());

        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transactionVO.getCcnum()));
        String expDate= PzEncryptor.decryptExpiryDate(transactionVO.getExpdate());
        String expMonth="";
        String expYear="";
        String temp[]=expDate.split("/");

        if(functions.isValueNull(temp[0]))
        {
            expMonth=temp[0];
        }
        if(functions.isValueNull(temp[1]))
        {
            expYear=temp[1];
        }
        commCardDetailsVO.setExpMonth(expMonth);
        commCardDetailsVO.setExpYear(expYear);

        commAddressDetailsVO.setTmpl_amount(transactionVO.getTemplateamount());
        commAddressDetailsVO.setTmpl_currency(transactionVO.getTemplatecurrency());
        commAddressDetailsVO.setFirstname(transactionVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionVO.getLastName());

        commMerchantVO.setAccountId(transactionVO.getAccountId());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        requestVO.setCommMerchantVO(commMerchantVO);
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;


        String ThreeDSMethod = "";

        if(response3D.getThreeDSMethodData() != null){
            ThreeDSMethod =response3D.getThreeDSMethodData();
        }

        transactionLogger.debug("inside CardPay Rest 3d payment process ---> "+response3D.getUrlFor3DRedirect());
        transactionLogger.debug("inside CardPay Rest ThreeDSMethod ---> "+ThreeDSMethod);

        if("3Dsv2".equalsIgnoreCase(ThreeDSMethod)){
            directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
            directKitResponseVO.setMethod("GET");
        }else{
            directKitResponseVO.setMethod("POST");
            directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("launch3D");
            asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("PaReq");
            asyncParameterVO.setValue(response3D.getPaReq());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("MD");
            asyncParameterVO.setValue(response3D.getMd());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("TermUrl");
            asyncParameterVO.setValue(response3D.getTerURL());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
        }


    }


    public static String getPreviousStatus(String trackingid)
    {
        String status = "";
        Connection conn = null;
        PreparedStatement ps = null;
        try
        {
            conn = Database.getConnection();
            String query = "select status from transaction_common_details where trackingid='"+trackingid+"' ORDER BY detailid DESC LIMIT 1";
            ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                status = rs.getString("status");
                transactionLogger.error("status------" + status);
            }
            transactionLogger.error("Sql Query-----" + ps);
        }
        catch (SystemError e)
        {
            transactionLogger.error("SystemError-----" + e);
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException-----" + se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return status;
    }

    public String getSpecificVirtualTerminalJSP()
    {
        return "payforasiaspecificfields.jsp";
    }


    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("inside payout vo params etension -- >");
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
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }



}
