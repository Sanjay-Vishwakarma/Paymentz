package com.payment.brd;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.codehaus.jettison.json.JSONObject;

import java.util.ResourceBundle;

/**
 * Created by Admin on 10/30/2018.
 */
public class BRDPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(BRDPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.brd");

    public static final String GATEWAY_TYPE = "brd";

    public BRDPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }
    
    public static void main(String[] args)
    {
        try
        {
            String formulaString = "{0:10,3:20,4:30,5:40,6:50,9:60,10:70,12:80}";
            String[] emiFormula = formulaString.split(",");
            String emi = "";
            String formula = "";

            for(String emiStr : emiFormula)
            {
                System.out.println("EMI---"+emiStr);
                String[] sEmiString = emiStr.split(":");
                System.out.println(sEmiString[0]+"::::"+sEmiString[1]);
            }

            JSONObject jsonObject = new JSONObject(formulaString);
            System.out.println("JSON ---- "+jsonObject);
            String emiCount = "3";

            if(jsonObject.has(emiCount)){
                System.out.println("EMI COUNT ------"+emiCount);
                System.out.println("JSON VALUE -----"+jsonObject.getString(emiCount));
            }

            /*String url="https://172.27.4.34/Transaction?";
            String request = "" +
                    "tid=RZ000001" +
                    "&seqnum=5000000014" +
                    "&pan=5126960900110274" +
                    "&expdate=2005" +
                    "&amount=100" +
                    "&cvc=939" +
                    "&formula=20";

            System.out.println("Request---" + request);

            String response = BRDUtils.doPostHTTPSURLConnectionClient(url, request);

            System.out.println("Response---" + response);*/

        }
        catch (Exception e){
            System.out.println("error----------------" + e);
        }
    }

    @Override
    public String getMaxWaitDays() {   return null; }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("---------- inside processSale BRD----------" + trackingID);
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        BRDUtils brdUtils = new BRDUtils();
        BRDPaymentGateway brdPaymentGateway = new BRDPaymentGateway(accountId);
        CommCardDetailsVO commCardDetailsVO = commRequestVO.getCardDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Functions functions = new Functions();

        if(trackingID.length() == 5)
        {
            trackingID = "000"+trackingID;
        }
        else if(trackingID.length() == 6)
        {
            trackingID = "00"+trackingID;
        }
        else if(trackingID.length() == 7)
        {
            trackingID = "0"+trackingID;
        }

        transactionLogger.error("---------- final tracking ID ----------"+trackingID);

        try{

            String formulaList = gatewayAccount.getFRAUD_FTP_USERNAME();
            JSONObject jsonObject = new JSONObject(formulaList);
            transactionLogger.error("JSON ---- " + jsonObject);
            String formula = "10";
            String emiCount = "";

            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                transactionLogger.error("Installment value not null= " + commTransactionDetailsVO.getEmiCount());
                emiCount = commTransactionDetailsVO.getEmiCount();
                if(jsonObject.has(emiCount)){
                    System.out.println("JSON VALUE -----"+jsonObject.getString(emiCount));
                    formula = jsonObject.getString(emiCount);
                }
            }


            String amount = (int)(Double.parseDouble(commTransactionDetailsVO.getAmount())*100)+"";

            String request = "" +
                    "?tid="+gatewayAccount.getMerchantId() +
                    "&seqnum=5"+trackingID+"4" +
                    "&pan="+ commCardDetailsVO.getCardNum() +
                    "&expdate="+ commCardDetailsVO.getExpYear().substring(2) + commCardDetailsVO.getExpMonth()  +
                    "&amount="+ amount +
                    "&cvc="+ commCardDetailsVO.getcVV() +
                    "&formula=" + formula;

            transactionLogger.error("sale Request ---- " + request);

            //String response = BRDUtils.doPostHTTPSURLConnectionClient(RB.getString("TRANSACTION"), request);
            String response = BRDUtils.doGetHTTPSURLConnectionClient(RB.getString("TRANSACTION") + request);

            transactionLogger.error("sale Response ---- " + response);

            commResponseVO = brdUtils.getSaleResponse(response);

            //Inquiry Called
            brdPaymentGateway.processInquiry(requestVO,trackingID);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }


    public GenericResponseVO processInquiry(GenericRequestVO requestVO, String trackingID) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("---------- inside processInquiry ----------");
        Functions functions = new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);

        //String trackingID=commRequestVO.getTransDetailsVO().getOrderId();

        if(trackingID.length() == 5)
        {
            trackingID = "000"+trackingID;
        }
        else if(trackingID.length() == 6)
        {
            trackingID = "00"+trackingID;
        }
        else if(trackingID.length() == 7)
        {
            trackingID = "0"+trackingID;
        }

        try{

            String orderRequest ="" +
                    "?tid="+gatewayAccount.getMerchantId() +
                    "&seqnum=5"+trackingID+"4";

            transactionLogger.error("BRD Inquiry Request ---- " + orderRequest);

            //String orderResponse = BRDUtils.doPostHTTPSURLConnectionClient(RB.getString("INQUIRY"),orderRequest);
            String orderResponse = BRDUtils.doGetHTTPSURLConnectionClient(RB.getString("INQUIRY")+orderRequest);

            transactionLogger.error("BRD Inquiry Response ---- " + orderResponse);

        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }
}