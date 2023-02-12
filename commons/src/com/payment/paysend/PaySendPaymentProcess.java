package com.payment.paysend;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by Admin on 5/13/2019.
 */
public class PaySendPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger= new TransactionLogger(PaySendPaymentProcess.class.getName());
    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.paysend");


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
        return form;
    }

    @Override
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }


    public void setCPRequestVO(CommRequestVO requestVO,String trackingId) throws PZDBViolationException
    {
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO) requestVO;
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommCardDetailsVO commCardDetailsVO=new CommCardDetailsVO();
        CommMerchantVO commMerchantVO=new CommMerchantVO();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(trackingId);
        commTransactionDetailsVO.setPrevTransactionStatus(transactionVO.getStatus());

        transactionLogger.debug("dbStatus----"+commTransactionDetailsVO.getPrevTransactionStatus());
        transactionLogger.debug("paymentid----"+commTransactionDetailsVO.getPaymentId());
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
        transactionLogger.debug("inside CardPay Rest 3d payment process---"+response3D.getUrlFor3DRedirect());
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


    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.error("inside paysend payment process setNBResponseVO===");
        AsyncParameterVO asyncParameterVO = null;

        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        String project = gatewayAccount.getMerchantId();

        try
        {
            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("project");
            asyncParameterVO.setValue(project);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("amount");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getAmount());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("currency");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getCurrency());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("success_url");
            asyncParameterVO.setValue(RB.getString("SUCCESS_URL"));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("fail_url");
            asyncParameterVO.setValue(RB.getString("FAIL_URL"));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("first_name");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getFirstname());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("last_name");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getLastname());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("birth_date_full");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getBirthdate());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("address");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getStreet());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("email");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getEmail());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("description");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("identifier");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderId());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("order_id");
            asyncParameterVO.setValue(commonValidatorVO.getTrackingid());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(RB.getString("TOP_UP"));
        }
        catch (Exception e)
        {
            transactionLogger.error("error in PaySend Payment process ---",e);
        }
        return directKitResponseVO;
    }
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        PaymentManager paymentManager=new PaymentManager();
        Functions functions=new Functions();
        CommonValidatorVO commonValidatorVO=new CommonValidatorVO();
        commonValidatorVO.setTrackingid(String.valueOf(payoutRequest.getTrackingId()));
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        //String cvv=paymentManager.getCvv(commonValidatorVO);
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        /*if(functions.isValueNull(cvv))
            commCardDetailsVO.setcVV(PzEncryptor.decryptCVV(cvv));*/
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
    }



}