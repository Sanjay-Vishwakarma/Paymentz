package com.payment.billdesk;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Enum.PaymentModeEnum;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.payVaultPro.PayVaultProUtils;
import com.payment.romcard.RomCardUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;


import java.awt.geom.IllegalPathStateException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/26/2017.
 */
public class BillDeskPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionlogger = new TransactionLogger(BillDeskPaymentGateway.class.getName());
    private static Logger logger = new Logger(BillDeskPaymentGateway.class.getName());
    private final static String PAY_URL = "https://uat.billdesk.com/pgidsk/pgmerc/MercTestRedirect.jsp";
    public static final String GATEWAY_TYPE = "billdesk";
    private final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Billdesk");

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public BillDeskPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }

    @Override
    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionlogger.error(" ---- Autoredict in Billdesk ---- ");
        String html = "";
        PaymentManager paymentManager = new PaymentManager();
        Comm3DResponseVO transRespDetails = null;
        BillDeskUtils billDeskUtils = new BillDeskUtils();

        String paymentMode = commonValidatorVO.getPaymentMode();

        CommRequestVO commRequestVO = billDeskUtils.getBilldeskRequestVO(commonValidatorVO);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        boolean isTest = gatewayAccount.isTest();
        try
        {
            transactionlogger.error("isService Flag -----" + commonValidatorVO.getMerchantDetailsVO().getIsService());
            transactionlogger.error("autoredirect  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAutoRedirect());
            transactionlogger.error("addressdetail  flag-----" + commonValidatorVO.getMerchantDetailsVO().getAddressDeatails());
            transactionlogger.error("terminal id  is -----" + commonValidatorVO.getTerminalId());
            transactionlogger.error("tracking id  is -----" + commonValidatorVO.getTrackingid());

            transRespDetails = (Comm3DResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails.getStatus().equalsIgnoreCase("pending3DConfirmation"))
            {
                html = billDeskUtils.generateAutoSubmitForm(transRespDetails.getUrlFor3DRedirect(), transRespDetails.getPaReq(), paymentMode);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionlogger.error("PZGenericConstraintViolationException in BilldeskPaymentGateway---", e);
        }
        return html;
    }


    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionlogger.error("Entering processSale of BillDeskPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO generacAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        transactionlogger.error("genericTransDetailsVO.getPaymentType()-------->"+genericTransDetailsVO.getPaymentType());

        String payment_brand= genericTransDetailsVO.getCardType();
        String payment_Card= GatewayAccountService.getPaymentMode(genericTransDetailsVO.getPaymentType());
        transactionlogger.error("payment_Card is -----"+payment_Card);
       // MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        boolean isTest = gatewayAccount.isTest();

        String bankID = "IDB";
        String itemCode = "DIRECT";
        String paymentURL = "";

        if (genericTransDetailsVO.getPaymentType().equalsIgnoreCase("UPI"))
        {
            bankID = "NA";
            itemCode = "NA";
            if (isTest)
            {
                paymentURL = RB.getString("TEST_PAYMENT_URL");
            }
            else
            {
                paymentURL = RB.getString("UPI_LIVE_URL");
            }
        }
        else if(genericTransDetailsVO.getPaymentType().equalsIgnoreCase("NBI") || genericTransDetailsVO.getPaymentType().equalsIgnoreCase("EWI") ||
        genericTransDetailsVO.getPaymentType().equalsIgnoreCase("NBB") || genericTransDetailsVO.getPaymentType().equalsIgnoreCase("EWB"))
        {
            bankID = payment_brand;

            if (isTest)
            {
               /* if (functions.isValueNull(merchantDetailsVO.getHostUrl()))
                {
                    termUrl = "https://" + merchantDetailsVO.getHostUrl() + RB.getString("HOST_URL");
                    transactionLogger.error("From HOST_URL notificationUrl ----" + termUrl);
                }*/
                paymentURL = RB.getString("TEST_PAYMENT_URL");
            }
            else
            {
                paymentURL = RB.getString("NBW_LIVE_URL");
            }
        }

        else
        {
            if (payment_Card.equalsIgnoreCase("CC") || payment_Card.equalsIgnoreCase("DC"))
            {
                if (isTest)
                {
                    bankID = "IDB";
                    paymentURL = RB.getString("TEST_PAYMENT_URL");
                }
                else
                {
                    bankID = "CARD";
                    paymentURL = RB.getString("CARD_LIVE_URL");
                }

            }
        }

        transactionlogger.error(genericTransDetailsVO.getPaymentType()+"---"+paymentURL+"-IsTest---"+isTest);
        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String customerID = trackingID;
        String filler1 = "NA";
        String txnAmount = genericTransDetailsVO.getAmount();
        String filler2 = "NA";
        String filler3 = "NA";
        String currencyType = "INR";
        String typeField1 = "R";
        String securityID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String filler4 = "NA";
        String filler5 = "NA";
        String typeField2 = "F";
        String additionalInfo1 = "";
        if (functions.isValueNull(additionalInfo1))
        {
            additionalInfo1 = genericTransDetailsVO.getOrderId();
        }
        else
        {
            additionalInfo1 = trackingID;
        }

        String additionalInfo2 = "NA";
        String additionalInfo3 = "";

        additionalInfo3="9999999999";

        String additionalInfo4 = "NA";
        String additionalInfo5 = "NA";
        String additionalInfo6 = "NA";
        String additionalInfo7 = "NA";
        String ru = "";
        transactionlogger.error("commMerchantVO.getHostUrl----------------->" +commMerchantVO.getHostUrl());
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            ru = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionlogger.error("From HOST_URL notificationUrl ----" + ru);
        }
        else
        {
            ru =RB.getString("BILLDESK_RU");
            transactionlogger.error("From RB notificationUrl ----" + ru);
        }
        String checksumKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String checksumString = merchantID + "|" + customerID + "|" + filler1 + "|" + txnAmount + "|" + bankID + "|" + filler2 + "|" + filler3 + "|" + currencyType + "|" + itemCode + "|" + typeField1 + "|" + securityID +
                "|" + filler4 + "|" + filler5 + "|" + typeField2 + "|" + additionalInfo1 + "|" + additionalInfo2 + "|" + additionalInfo3 + "|" + additionalInfo4 + "|" + additionalInfo5 + "|" + additionalInfo6 + "|" + additionalInfo7 + "|" + ru;
        transactionlogger.error("checksumString---" + checksumString);
        String checksum = TestHMac.HmacSHA256(checksumString, checksumKey);
        transactionlogger.error("checksum---" + checksum);
        String requestMsg = checksumString + "|" + checksum;
        transactionlogger.error("requestMsg---" + requestMsg);

        commResponseVO.setStatus("pending3DConfirmation");
        commResponseVO.setUrlFor3DRedirect(paymentURL);
        commResponseVO.setPaReq(requestMsg);

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Enter in processRefund of BillDesk---");
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        CommResponseVO commResponseVO = new CommResponseVO();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        BillDeskUtils billDeskUtils = new BillDeskUtils();

        String requestType = "0400";
        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String billDeskTranId = commTransactionDetailsVO.getPreviousTransactionId();
        String tranDate = commTransactionDetailsVO.getResponsetime().replace("/", "");
        String customerId = trackingID;
        String tranAmt = commTransactionDetailsVO.getPreviousTransactionAmount();
        transactionlogger.error("tranAmt is ----------"+tranAmt);
        String refundAmt = commTransactionDetailsVO.getAmount();
        String refDate = df.format(new Date());
        String merRefNo = trackingID + refDate;
        String filler1 = "NA";
        String filler2 = "NA";
        String filler3 = "NA";
        String checksumKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String checksumString = requestType + "|" + merchantID + "|" + billDeskTranId + "|" + tranDate + "|" + customerId + "|" + tranAmt + "|" + refundAmt
                + "|" + refDate + "|" + merRefNo + "|" + filler1 + "|" + filler2 + "|" + filler3;

        logger.error("checksumString---" + checksumString);

        String checksum = TestHMac.HmacSHA256(checksumString, checksumKey);

        String requestMsg = checksumString + "|" + checksum;

        transactionlogger.error("requestMsg---" + requestMsg);
        Map<String, String> authMap = new TreeMap<String, String>();
        authMap.put("msg", requestMsg);

        String postParams = billDeskUtils.joinMapValue(authMap, '&');
        String response = billDeskUtils.doPostHTTPSURLConnection(RB.getString("REFUND_URL"), postParams);
        transactionlogger.error("response refund---" + response);

        if (response != null)
        {
            String sData[] = response.split("\\|");

            String refAmount = sData[6];
            String bdRefundID = sData[9];
            String errorCode = sData[10];
            String refundStatus = sData[12];
            String errorReason = sData[11];

            String status = "fail";
            if (refundStatus.equalsIgnoreCase("Y"))
            {
                status = "success";
            }
            commResponseVO.setMerchantOrderId(bdRefundID);
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            commResponseVO.setDescription("Transaction reverse " + status);
            commResponseVO.setTransactionType("refund");
            commResponseVO.setStatus(status);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setAmount(refAmount);
            if (!errorCode.equalsIgnoreCase("NA"))
            {
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setRemark(errorReason);
            }
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionlogger.error("Enter in processInquiry of BillDesk---");
        CommResponseVO commResponseVO = new CommResponseVO();
        CommRequestVO reqVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO commTransactionDetailsVO = reqVO.getTransDetailsVO();
        BillDeskUtils billDeskUtils = new BillDeskUtils();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();
        TransactionManager transactionManager =new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(commTransactionDetailsVO.getOrderId());
        transactionlogger.debug("status is -----"+transactionDetailsVO.getStatus());
        String transaction_status=transactionDetailsVO.getStatus();
        try
        {
            String requestType = "0122";
            String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
            String customerId = commTransactionDetailsVO.getOrderId();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String formattedDate = sdf.format(date);
            String checksumKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
            String checksumString = requestType + "|" + merchantID + "|" + customerId + "|" + formattedDate;

            logger.error("checksumString---" + checksumString);

            String checksum = TestHMac.HmacSHA256(checksumString, checksumKey);

            String requestMsg = checksumString + "|" + checksum;
            transactionlogger.error("requestMsg---" + requestMsg);
            Map<String, String> authMap = new TreeMap<String, String>();
            authMap.put("msg", requestMsg);
            String postParams = billDeskUtils.joinMapValue(authMap, '&');
            transactionlogger.error("postparams is ====== " + postParams);
            String inquiryResp = "";
            if (isTest)
            {
                inquiryResp = billDeskUtils.doPostHTTPSURLConnection(RB.getString("TEST_INQUIRY_URL"), postParams);
            }
            else
            {
                inquiryResp = billDeskUtils.doPostHTTPSURLConnection(RB.getString("LIVE_INQUIRY_URL"), postParams);
            }

            transactionlogger.error("Inquiry response---" + inquiryResp);
            if (inquiryResp != null)
            {
                String sData[] = inquiryResp.split("\\|");
                String merchanId = sData[1];
                String Transactionid = sData[2];
                String amount = sData[5];
                String bankId = sData[6];
                String currency = sData[9];
                String time = sData[14];
                String code = sData[15];
                String inquiryStatus = sData[31];
                String errorCode = sData[24];
                String errorDescription = sData[25];

                if (code.equals("0300"))
                {
                    commResponseVO.setStatus("Success");
                    commResponseVO.setDescription("Success");
                    commResponseVO.setTransactionStatus("Success");
                }
                else
                {
                    commResponseVO.setStatus("Failed");
                    commResponseVO.setDescription("Failed");
                    commResponseVO.setTransactionStatus("Failed");
                }

                if (!errorCode.equalsIgnoreCase("NA"))
                {
                    commResponseVO.setErrorCode(errorCode);
                    commResponseVO.setRemark(errorDescription);
                }

                commResponseVO.setAmount(amount);
                commResponseVO.setMerchantId(merchanId);
                commResponseVO.setTransactionId(Transactionid);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAuthCode(bankId);
                commResponseVO.setBankTransactionDate(time);
                if (transaction_status.equalsIgnoreCase("capturesuccess"))
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                else if (transaction_status.equalsIgnoreCase("markedforreversal")||transaction_status.equalsIgnoreCase("reversed"))
                    commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                else
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
            }
        }
        catch (IllegalFormatException e)
        {
            transactionlogger.error("IllegalFormatException processInquiry -----",e);
        }

        return commResponseVO;
    }


    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionlogger.error("inside process Rebilling ----------------------");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO generacAddressDetailsVO = commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        transactionlogger.error("Card Number------->" + functions.maskingPan(commRequestVO.getCardDetailsVO().getCardNum()));
        transactionlogger.error("Expiry Date------->" + functions.maskingNumber(commRequestVO.getCardDetailsVO().getExpMonth()) + "/" +functions.maskingNumber(commRequestVO.getCardDetailsVO().getExpYear()));
        transactionlogger.error("Cvv------->" + functions.maskingNumber(commRequestVO.getCardDetailsVO().getcVV()));

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String current_date = simpleDateFormat.format(date);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String billidesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        String merchantID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String customerID = trackingID;
        String filler1 = "NA";
        String txnAmount = genericTransDetailsVO.getAmount();
        String bankID = "CARD";
        String filler2 = "NA";
        String filler3 = "NA";
        String currencyType = "INR";
        String itemCode = "DIRECT";
        String typeField1 = "R";
        String securityID = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String filler4 = "NA";
        String filler5 = "NA";
        String typeField2 = "F";
        String additionalInfo1 = "";
        if (functions.isValueNull(additionalInfo1))
        {
            additionalInfo1 = genericTransDetailsVO.getOrderId();
        }
        else
        {
            additionalInfo1 = trackingID;
        }
        String additionalInfo2 = current_date;
        String additionalInfo3 = current_date;
        String additionalInfo4 = "10000.00";
        String additionalInfo5 = "NA";
        String additionalInfo6 = "NA";
        String additionalInfo7 = "NA";
        String ru = RB.getString("BILLDESK_RU");
        String checksumKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();

        String checksumString = merchantID + "|" + customerID + "|" + filler1 + "|" + txnAmount + "|" + bankID + "|" + filler2 + "|" + filler3 + "|" + currencyType + "|" + itemCode + "|" + typeField1 + "|" + securityID +
                "|" + filler4 + "|" + filler5 + "|" + typeField2 + "|" + additionalInfo1 + "|" + additionalInfo2 + "|" + additionalInfo3 + "|" + additionalInfo4 + "|" + additionalInfo5 + "|" + additionalInfo6 + "|" + additionalInfo7 + "|" + ru;
        transactionlogger.error("checksumString---" + checksumString);

        String checksum = TestHMac.HmacSHA256(checksumString, checksumKey);
        transactionlogger.error("checksum---" + checksum);
        String msg = checksumString + "|" + checksum;
        transactionlogger.error("requestMsg---" + msg);

        String ccno = commRequestVO.getCardDetailsVO().getCardNum();
        String expmon = commRequestVO.getCardDetailsVO().getExpMonth();
        String expyr = commRequestVO.getCardDetailsVO().getExpYear();
        String cvv2 = commRequestVO.getCardDetailsVO().getcVV();
        String cardtype = "NA";
        String authinput = "NA";

        String paydata = ccno + "|" + expmon + "|" + expyr + "|" + cvv2 + "|" + cardtype + "|" + authinput;

        String paydatalog = functions.maskingPan(ccno) + "|" + functions.maskingNumber(expmon) + "|" + functions.maskingNumber(expyr) + "|" + functions.maskingNumber(cvv2) + "|" + cardtype + "|" + authinput;

        String ipaddress = "1.1.1.1";

        String requestMsg = "msg=" + msg + "&paydata=" + paydata + "&ipaddress=" + ipaddress;
        String requestMsglog = "msg=" + msg + "&paydata=" + paydatalog + "&ipaddress=" + ipaddress;


        transactionlogger.error("rebilling request is ---------"+"trackingid"+trackingID + requestMsglog);

        String response = "";
        try
        {
            if (isTest)
            {
                response = BillDeskUtils.doPostHTTPSURLConnection(RB.getString("RECURRING_TEST_URL"), requestMsg);
            }
            else
            {
                response = BillDeskUtils.doPostHTTPSURLConnection(RB.getString("RECURRING_LIVE_URL"), requestMsg);

            }
        }
        catch (Exception e)
        {
            transactionlogger.error("Exception ----" ,e);
        }
        transactionlogger.error("response ----" + response);

        if (functions.isValueNull(response) && response.contains("{"))
        {
            String merchantId = "";
            String Transactionid = "";
            String amount = "";
            String BankID = "";
            String CurrencyType = "";
            String status_code = "";
            String key = "";
            String response_Time = "";
            try
            {
                JSONObject jsonobj1 = new JSONObject(response);

                if (jsonobj1 != null)
                {
                    String Msg = jsonobj1.getString("msg");

                    String value[] = Msg.split("\\|");
                    merchantId = value[0];
                    Transactionid = value[1];
                    String Filler1 = value[2];
                    String id = value[3];
                    amount = value[4];
                    BankID = value[5];
                    String Filler2 = value[6];
                    String Filler3 = value[7];
                    CurrencyType = value[8];
                    String ItemCode = value[9];
                    String TypeField1 = value[10];
                    String SecurityID = value[11];
                    String Filler4 = value[12];
                    response_Time = value[13];
                    status_code = value[14];
                    String AdditionalInfo1 = value[16];
                    String AdditionalInfo2 = value[17];
                    String AdditionalInfo3 = value[18];
                    String AdditionalInfo4 = value[19];
                    String success_value = value[24];
                    key = value[25];

                    if (status_code.equalsIgnoreCase("0300"))
                    {
                        commResponseVO.setStatus("Success");
                        commResponseVO.setDescription("Success");
                        commResponseVO.setTransactionStatus("Success");
                    }
                    else
                    {
                        commResponseVO.setStatus("Failed");
                        commResponseVO.setDescription("Failed");
                        commResponseVO.setTransactionStatus("Failed");
                    }
                }
                commResponseVO.setMerchantId(merchantId);//merchant id.
                commResponseVO.setTransactionId(Transactionid);//unique order id.
                commResponseVO.setAmount(amount);//txn amount.
                commResponseVO.setResponseTime(response_Time);//response time.
                commResponseVO.setDescriptor(billidesc);//billing descriptor

                }
            catch (JSONException e)
            {
                transactionlogger.error("JSONException processRebilling BillDeskPaymentGateway -----", e);
            }

        }
        return commResponseVO;
    }
}