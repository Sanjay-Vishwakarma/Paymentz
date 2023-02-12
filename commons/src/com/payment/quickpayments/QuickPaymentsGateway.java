package com.payment.quickpayments;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Admin on 4/27/2021.
 */
public class QuickPaymentsGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="quickpayments";
    private TransactionLogger transactionLogger = new TransactionLogger(QuickPaymentsGateway.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.quickpayments");

    public QuickPaymentsGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside QuickPaymentsGateway processSale");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String mid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String clientId = trackingID;
        String foundingSourceName = "";
        String notes ="";
        String firstName    = "customer";
        String lastName     = "customer";
        String emailAddress = "customer@gmail.com";
        String amount       = transDetailsVO.getAmount();
        String currency     = transDetailsVO.getCurrency();
        String termUrl      = "";
        String saleURL      = "";
        String notify_url   = RB.getString("NOTIFY_URL");
        String paymentMode=GatewayAccountService.getPaymentMode(transDetailsVO.getPaymentType());


        boolean isTest = gatewayAccount.isTest();
        try
        {
            if (functions.isValueNull(addressDetailsVO.getFirstname()))
            {
                firstName = addressDetailsVO.getFirstname();
            }
            if (functions.isValueNull(addressDetailsVO.getLastname()))
            {
                lastName = addressDetailsVO.getLastname();
            }
            if (functions.isValueNull(addressDetailsVO.getEmail()))
            {
                emailAddress = addressDetailsVO.getEmail();
            }


        /*    if (functions.isValueNull(commMerchantVO.getHostUrl()))
            {
                termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL") + trackingID;
                transactionLogger.error("From HOST_URL----" + termUrl);
            }
            else
            {
                termUrl = RB.getString("TERM_URL") + trackingID;
                transactionLogger.error("From RB TERM_URL ----" + termUrl);
            }*/


            if (isTest)
            {
                saleURL = RB.getString("TEST_URL");
            }
            else
            {
                saleURL = RB.getString("LIVE_URL");
            }

            StringBuffer request = new StringBuffer();
            String BTC_url="";

            if(paymentMode.equalsIgnoreCase("CreditCard"))
            {
                foundingSourceName="BTC3";//card
                BTC_url=RB.getString("BTC3_url");
            }
            else if(paymentMode.equalsIgnoreCase("BC"))
            {
                foundingSourceName="BTC1";//btc
                BTC_url=RB.getString("BTC1_url");
            }
            else if(paymentMode.equalsIgnoreCase("BT"))
            {
                foundingSourceName="BTC4";//wire
                BTC_url=RB.getString("BTC4_url");
            }
            else
            {
                foundingSourceName="BTC3";
                BTC_url=RB.getString("BTC3_url");
            }
            request.append("clientId=" + clientId + "&currency=" + currency + "&amount=" + amount + "&foundingSourceName=" + foundingSourceName + "&notes=" + notes+"&returnUrl="+notify_url + "&firstName=" + firstName + "&lastName=" + lastName +
                    "&emailAddress=" + emailAddress);


            transactionLogger.error("QuickPayments Sale request---->" + trackingID + "---" + request);
            String response = QuickPaymentsUtils.doPostHTTPSURLConnectionClient(saleURL, request.toString(), mid);
            transactionLogger.error("QuickPayments Sale response---->" + trackingID + "---" + response);


            if(functions.isValueNull(response))
            {
                JSONObject responseJSON=new JSONObject(response);
                String secretId = "";
                String mcTxId = "";
                if(responseJSON!=null)
                {
                    if(responseJSON.has("secretId"))
                    {
                        secretId=responseJSON.getString("secretId");
                    }
                    if(responseJSON.has("mcTxId"))
                    {
                        mcTxId=responseJSON.getString("mcTxId");
                    }

                    if(responseJSON.has("mcTxId"))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setTransactionId(secretId);
                        commResponseVO.setUrlFor3DRedirect(BTC_url + mcTxId);//card
                        commResponseVO.setResponseHashInfo(mcTxId);
                        QuickPaymentsUtils.updateTransactionPaymentId(trackingID,secretId);
                    }
                    else
                    {
                        commResponseVO.setStatus("failed");
                        commResponseVO.setTransactionId(mcTxId);
                    }
                }

            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception---->" + trackingID + "---", e);
        }

        return commResponseVO;

    }
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for QuickPayments:::::");

        CommRequestVO commRequestVO =null;
        QuickPaymentsPaymentProcess quickPaymentsPaymentProcess = new QuickPaymentsPaymentProcess();
        String form="";
        try
        {
            commRequestVO = QuickPaymentsUtils.getQuickPaymentsRequestVO(commonValidatorVO);
            Comm3DResponseVO comm3DResponseVO = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if("pending3DConfirmation".equalsIgnoreCase(comm3DResponseVO.getStatus()))
            {
                form =quickPaymentsPaymentProcess.get3DConfirmationForm(commonValidatorVO.getTrackingid(), "", comm3DResponseVO);
            }
            transactionLogger.error("form---->" + form);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException--",e);
        }
        return form;
    }
    public String getMaxWaitDays()
    {
        return null;
    }
}
