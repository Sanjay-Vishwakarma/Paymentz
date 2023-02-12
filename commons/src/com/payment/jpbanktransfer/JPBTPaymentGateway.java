package com.payment.jpbanktransfer;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PaymentManager;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.jpbanktransfer.JPBankTransferUtils;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.exceptionHandler.PZTechnicalViolationException;

import java.util.ResourceBundle;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.validators.vo.CommonValidatorVO;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sagar Sonar on 21-April-2020.
 */
public class JPBTPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "jpbt";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.jpbt");
    private static TransactionLogger transactionLogger = new TransactionLogger(JPBTPaymentGateway.class.getName());
    private static Logger log = new Logger(JPBTPaymentGateway.class.getName());

    public JPBTPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale---");

        Functions functions = new Functions();
        JPBankTransferUtils jpBankTransferUtils = new JPBankTransferUtils();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        JPBankTransferVO jpBankTransferVO = new JPBankTransferVO();

        String url = "";
        String amount = "";
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String userName = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getFRAUD_FTP_PASSWORD();
       // String id=gatewayAccount.getFRAUD_FILE_SHORT_NAME();
        String member_id = gatewayAccount.getMerchantId();
        String site =gatewayAccount.getFRAUD_FTP_SITE();
        String uid="";
        if(functions.isValueNull(addressDetailsVO.getCustomerid()))
        {
            uid=addressDetailsVO.getCustomerid();
        }
        else
        {
            uid=trackingID;
        }

        boolean isTest = gatewayAccount.isTest();

        if ("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount = JPBankTransferUtils.getJPYAmount(transDetailsVO.getAmount());
        else if ("KWD".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount = JPBankTransferUtils.getKWDSupportedAmount(transDetailsVO.getAmount());
        else if ("KRW".equalsIgnoreCase(transDetailsVO.getCurrency()))
            amount = JPBankTransferUtils.getKRWAmount(transDetailsVO.getAmount());
        else
            amount = JPBankTransferUtils.getCentAmount(transDetailsVO.getAmount());

        try
        {

            if (isTest)
            {
                if(!functions.isValueNull(site))
                {
                    site=RB.getString("TEST_URL");
                }
                    url = site +"?sid=" + member_id + "&uid=" + trackingID + "&am=" + amount + "&mode=json";

            }
            else
            {
                if(!functions.isValueNull(site))
                {
                    site=RB.getString("LIVE_URL");
                }
                url = site +"?sid=" + member_id + "&uid=" + trackingID + "&am=" + amount + "&mode=json";
            }
            transactionLogger.error("request-->" + url);
            String response = jpBankTransferUtils.doHttpPostConnection(url);
            transactionLogger.error("response-->" + response);
            jpBankTransferVO.setAmount(amount);
            JSONObject responseObject = null;

            if (response.startsWith("(") && response.endsWith(")"))
            {

                responseObject = new JSONObject(response.substring(1, response.length() - 1));
            }
            else
            {
                responseObject = new JSONObject(response);
            }

            if (responseObject != null && !responseObject.has("error"))
            {
                transactionLogger.error("responseObject-->" + responseObject);
                jpBankTransferVO.setStatus("pending");
                jpBankTransferVO.setRemark("Bank Transfer Initiated");
                jpBankTransferVO.setDescription("Bank Transfer Initiated");

                if (responseObject.has("shitenNm"))
                    jpBankTransferVO.setShitenNm(responseObject.getString("shitenNm"));

                if (responseObject.has("bankName"))
                    jpBankTransferVO.setBankName(responseObject.getString("bankName"));

                if (responseObject.has("shitenName"))
                    jpBankTransferVO.setShitenName(responseObject.getString("shitenName"));

                if (responseObject.has("kouzaType"))
                    jpBankTransferVO.setKouzaType(responseObject.getString("kouzaType"));

                if (responseObject.has("kouzaNm"))
                    jpBankTransferVO.setKouzaNm(responseObject.getString("kouzaNm"));

                if (responseObject.has("kouzaMeigi"))
                    jpBankTransferVO.setKouzaMeigi(responseObject.getString("kouzaMeigi"));

                if (responseObject.has("bid"))
                    jpBankTransferVO.setBid(responseObject.getString("bid"));

                if (responseObject.has("tel"))
                    jpBankTransferVO.setTel(responseObject.getString("tel"));

                if (responseObject.has("email"))
                    jpBankTransferVO.setEmail(responseObject.getString("email"));

                if (responseObject.has("company"))
                    jpBankTransferVO.setCompany(responseObject.getString("company"));

                if (responseObject.has("nameId")){
                    jpBankTransferVO.setNameId(responseObject.getString("nameId"));

                    jpBankTransferVO.setTransactionId(responseObject.getString("nameId"));
                }

                if (responseObject.has("result"))
                    jpBankTransferVO.setResult(responseObject.getString("result"));


            }
            else
            {
                jpBankTransferVO.setStatus("fail");
                if (responseObject.has("error"))
                {
                    jpBankTransferVO.setRemark(responseObject.getString("error"));
                    jpBankTransferVO.setDescription(responseObject.getString("error"));
                }
                else
                {

                    jpBankTransferVO.setRemark("NO Response Found");
                    jpBankTransferVO.setDescription("Transaction failed");

                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException-->", e);
        }


        return jpBankTransferVO;
    }
    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}
