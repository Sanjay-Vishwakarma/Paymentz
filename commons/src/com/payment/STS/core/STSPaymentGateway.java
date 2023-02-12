package com.payment.STS.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.operations.PZOperations;


import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/6/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSPaymentGateway extends AbstractPaymentGateway
{
    private static Logger log = new Logger(STSPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(STSPaymentGateway.class.getName());
    private static TransactionLogger tlog = new TransactionLogger(STSPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE = "sts";
    private final static String TESTURL = "https://api.sts-pay.com/sale-rest";

    @Override
    public String getMaxWaitDays()
    {
        return "3.5"; //To change body of implemented methods use File | Settings | File Templates.
    }

    public STSPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("STSPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }
    public GenericResponseVO processSale(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering processSale of CashFlowPaymentGateway...");
        transactionLogger.debug("Entering processSale of CashFlowPaymentGateway...");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions=new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commRequestVO.getAddressDetailsVO();
            STSUtills stsUtills = new STSUtills();
            STSAccounts stsAccounts = new STSAccounts();
            Hashtable dataHash = stsAccounts.getDataHash(accountId);

            String mid = (String) dataHash.get("mid");
            String pwd = (String) dataHash.get("password");
            String oid = genericTransDetailsVO.getOrderId();
            String tran_type = "SALE";
            String amt = getAmount(genericTransDetailsVO.getAmount());
            String currncy = genericTransDetailsVO.getCurrency();
            String o_desc = genericTransDetailsVO.getOrderDesc();
            String fn = genericAddressDetailsVO.getFirstname();
            String ln = genericAddressDetailsVO.getLastname();
            String card = genericCardDetailsVO.getCardNum();
            String expMonth = genericCardDetailsVO.getExpMonth();
            String expYear = genericCardDetailsVO.getExpYear().substring(2);
            String cvv = genericCardDetailsVO.getcVV();
            String add1 = genericAddressDetailsVO.getStreet();
            String add2 = genericAddressDetailsVO.getStreet();
            String city = genericAddressDetailsVO.getCity();
            String pro = genericAddressDetailsVO.getState();
            String pcode = genericAddressDetailsVO.getTelnocc();
            String country = genericAddressDetailsVO.getCountry();
            String email = genericAddressDetailsVO.getEmail();
            String ph = genericAddressDetailsVO.getPhone();
            String ip = genericAddressDetailsVO.getIp();
            String custom = "transaction test";
            String cid = "10";

            String data = "website_id="+mid+"&password="+pwd+"&order_id="+oid+"&amount="+amt+"&currecy_code="+currncy+"&order_description="+o_desc+"&first_name="+fn+"&last_name="
                    +ln+"&card_number="+card+"&expiry_date_month="+expMonth+"&expiry_date_year="+expYear+"&cv2="+cvv+"&address1="+add1+"&address2="+add2+"&city="+city+"&province="
                    +pro+"&postal_code="+pcode+"&country_code="+country+"&customer_id="+cid+"&email_address="+email+"&phone_number="+ph+"&customer_ip_address="+ip;

            String dataLog = "website_id="+mid+"&password="+pwd+"&order_id="+oid+"&amount="+amt+"&currecy_code="+currncy+"&order_description="+o_desc+"&first_name="+fn+"&last_name="
                    +ln+"&card_number="+functions.maskingPan(card)+"&expiry_date_month="+functions.maskingNumber(expMonth)+"&expiry_date_year="+functions.maskingNumber(expYear)+"&cv2="+functions.maskingNumber(cvv)+"&address1="+add1+"&address2="+add2+"&city="+city+"&province="
                    +pro+"&postal_code="+pcode+"&country_code="+country+"&customer_id="+cid+"&email_address="+email+"&phone_number="+ph+"&customer_ip_address="+ip;

            transactionLogger.error("---request data--"+trackingID+"-"+dataLog);
            String saleResponse = STSUtills.doPostHTTPSURLConnection(TESTURL,data);
            transactionLogger.error("response--"+trackingID+"-"+saleResponse);

            //JSONObject json  = stsUtills.getStringValues(saleResponse);

            Hashtable responseHash = stsUtills.getStringValues(saleResponse);
            log.debug("----responseHash response----"+responseHash);
            transactionLogger.debug("----responseHash response----"+responseHash);

            if(!responseHash.equals("") && responseHash!=null)
            {
                String status = "fail";
                if(responseHash.get("status_code").equals("0"))
                {
                    status = "success";
                }

                commResponseVO.setStatus(status);
                commResponseVO.setDescription((String) responseHash.get("message"));
                commResponseVO.setTransactionId((String) responseHash.get("order_id"));
                commResponseVO.setErrorCode((String) responseHash.get("status_code"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                commResponseVO.setTransactionType("sale");
                commResponseVO.setResponseHashInfo((String) responseHash.get("cross_reference"));
                commResponseVO.setRemark((String) responseHash.get("auth_code"));

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AlliedWalletPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public String getAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }
}
