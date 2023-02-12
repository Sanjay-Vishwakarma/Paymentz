package com.directi.pg.core.paymentgateway;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.manager.RecurringManager;
import com.manager.vo.RecurringBillingVO;
import com.payment.common.core.*;
import com.payment.common.core.ComConfirmRequestVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Dec 7, 2012
 * Time: 9:30:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class PfsPaymentGateway extends AbstractPaymentGateway
{
    //private static Logger log = new Logger(PfsPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PfsPaymentGateway.class.getName());
    //Configuration
    public static final String GATEWAY_TYPE = "PFS";

    final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.PfsServlet");
    private final static String GATEWAYURL = rb.getString("GATEWAYURL");
    private static String NOTIFICATION_URL = rb.getString("NOTIFICATION_URL");
    private final static String FIELD_RECURRING_BILLING_ID = "rbId";
   private final static String FIELD_AMOUNT = "amount";
    private final static String FIELD_resType= "resType";

    public PfsPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException
    {
        transactionLogger.info("Inside PFSPaymentGateway  ::::::::");
        transactionLogger.info("Inside processAuthentication  ::::::::");

        validateForSale(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(),"processAuthentication()",null,"common","Merchant Id not configured while placing transaction,AccountId:::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while placing transaction,AccountId:::"+accountId,new Throwable("Merchant Id not configured while placing transaction,AccountId:::"+accountId));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        RecurringManager recurringManager = new RecurringManager();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "Register";
        String messageId = commRequestVO.getTransDetailsVO().getResponseHashInfo();

        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";
        String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());
        String ipAddress = genericAddressDetailsVO.getIp();
        String name = genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();
        /*String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
        String isManualRecurring = recurringBillingVO.getIsManualRecurring();*/
        String error = "";

        if(ipAddress==null || ipAddress.equals(""))
        {
            ipAddress = genericAddressDetailsVO.getCardHolderIpAddress();
        }

        String recurringXML = "";
        transactionLogger.debug("is recurring in account===="+account.getIsRecurring());
        if ("Y".equalsIgnoreCase(account.getIsRecurring()))
        {
            String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
            String isManualRecurring = recurringBillingVO.getIsManualRecurring();
            if ("Y".equalsIgnoreCase(isManualRecurring) && "N".equalsIgnoreCase(isAutomaticRecurring))
            {
                transactionLogger.debug("isRecurring====" + isAutomaticRecurring);
                transactionLogger.debug("isManualRecurring====" + isManualRecurring);
                recurringXML = "<RB>\n" +
                        "<T>M</T>\n" +
                        "<A>S</A>\n" +
                        "</RB>\n";


            }
            else if ("N".equalsIgnoreCase(isManualRecurring) && "Y".equalsIgnoreCase(isAutomaticRecurring))
            {
                transactionLogger.debug("isRecurring====" + isAutomaticRecurring);
                transactionLogger.debug("isManualRecurring====" + isManualRecurring);
                transactionLogger.debug("from gateway---" + recurringBillingVO.getInterval() + recurringBillingVO.getFrequency() + recurringBillingVO.getRunDate());
                recurringXML = "<RB>\n" +
                        "<I>" + recurringBillingVO.getInterval().toLowerCase() + "</I>\n" +
                        "<F>" + recurringBillingVO.getFrequency() + "</F>\n" +
                        "<D>" + recurringBillingVO.getRunDate() + "</D>\n" +
                        "<T>A</T>\n" +
                        "<A>S</A>\n" +
                        "</RB>\n";
            }
        }
        String data="<R>\n" +
                "<R1>CA</R1>\n" +
                "<R2>"+trackingID+"</R2>\n" +
                "<R3>"+getAmount(genericTransDetailsVO.getAmount())+"</R3>\n" +
                "<R4>0</R4>\n" +
                "<R5>"+genericAddressDetailsVO.getFirstname()+"</R5>\n" +
                "<R6></R6>\n" +
                "<R7>"+genericAddressDetailsVO.getLastname()+"</R7>\n" +
                "<R8>"+genericAddressDetailsVO.getStreet()+"</R8>\n" +
                "<R9>"+","+"</R9>\n" +
                "<R10>"+genericAddressDetailsVO.getCity()+"</R10>\n" +
                "<R11>"+genericAddressDetailsVO.getState()+"</R11>\n" +
                "<R12>"+genericAddressDetailsVO.getZipCode()+"</R12>\n" +
                "<R13>"+genericAddressDetailsVO.getCountry()+"</R13>\n" +
                "<R14>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R14>\n" +
                "<R15>"+getPfsCardType(genericCardDetailsVO.getCardType())+"</R15>\n" +
                "<R16>"+genericCardDetailsVO.getCardNum()+"</R16>\n" +
                "<R17>"+genericCardDetailsVO.getExpMonth()+"</R17>\n" +
                "<R18>"+genericCardDetailsVO.getExpYear()+"</R18>\n" +
                "<R19>"+genericCardDetailsVO.getcVV()+"</R19>\n" +
                "<R20>"+currencyCode+"</R20>\n" +
                "<R21>0</R21>\n" +
                "<R22>"+genericAddressDetailsVO.getFirstname()+"</R22>\n" +
                "<R23></R23>\n" +
                "<R24>"+genericAddressDetailsVO.getLastname()+"</R24>\n" +
                "<R25>"+genericAddressDetailsVO.getStreet()+"</R25>\n" +
                "<R26>"+","+"</R26>\n" +
                "<R27>"+genericAddressDetailsVO.getCity()+"</R27>\n" +
                "<R28>"+genericAddressDetailsVO.getState()+"</R28>\n" +
                "<R29>"+genericAddressDetailsVO.getZipCode()+"</R29>\n" +
                "<R30>"+genericAddressDetailsVO.getCountry()+"</R30>\n" +
                "<R31>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R31>\n" +
                "<R32>"+genericTransDetailsVO.getOrderId()+"</R32>\n" +
                "<R33>MARK</R33>\n" +
                "<R34></R34>\n" +
                "<R35>S</R35>\n" +
                "<R36>"+genericAddressDetailsVO.getEmail()+"</R36>\n" +
                "<R37>"+ipAddress+"</R37>\n" +
                "<R38>*/*</R38>\n" +
                "<R39>Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)</R39>\n" +
                "<R40>"+trackingID+"</R40>\n" +
                "<R41>"+trackingID+"</R41>\n" +
                "<R42>"+NOTIFICATION_URL+"</R42>\n" +
                "<R43>2</R43>\n" +
                "<R44>2</R44>\n" +
                "<R45>"+commMerchantVO.getMerchantId()+"</R45>\n"+
                ""+recurringXML+""+
                "</R>";

        String logData="<R>\n" +
                "<R1>CA</R1>\n" +
                "<R2>"+trackingID+"</R2>\n" +
                "<R3>"+getAmount(genericTransDetailsVO.getAmount())+"</R3>\n" +
                "<R4>0</R4>\n" +
                "<R5>"+genericAddressDetailsVO.getFirstname()+"</R5>\n" +
                "<R6></R6>\n" +
                "<R7>"+genericAddressDetailsVO.getLastname()+"</R7>\n" +
                "<R8>"+genericAddressDetailsVO.getStreet()+"</R8>\n" +
                "<R9>"+","+"</R9>\n" +
                "<R10>"+genericAddressDetailsVO.getCity()+"</R10>\n" +
                "<R11>"+genericAddressDetailsVO.getState()+"</R11>\n" +
                "<R12>"+genericAddressDetailsVO.getZipCode()+"</R12>\n" +
                "<R13>"+genericAddressDetailsVO.getCountry()+"</R13>\n" +
                "<R14>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R14>\n" +
                "<R15>"+getPfsCardType(genericCardDetailsVO.getCardType())+"</R15>\n" +
                "<R20>"+currencyCode+"</R20>\n" +
                "<R21>0</R21>\n" +
                "<R22>"+genericAddressDetailsVO.getFirstname()+"</R22>\n" +
                "<R23></R23>\n" +
                "<R24>"+genericAddressDetailsVO.getLastname()+"</R24>\n" +
                "<R25>"+genericAddressDetailsVO.getStreet()+"</R25>\n" +
                "<R26>"+","+"</R26>\n" +
                "<R27>"+genericAddressDetailsVO.getCity()+"</R27>\n" +
                "<R28>"+genericAddressDetailsVO.getState()+"</R28>\n" +
                "<R29>"+genericAddressDetailsVO.getZipCode()+"</R29>\n" +
                "<R30>"+genericAddressDetailsVO.getCountry()+"</R30>\n" +
                "<R31>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R31>\n" +
                "<R32>"+genericTransDetailsVO.getOrderId()+"</R32>\n" +
                "<R33>MARK</R33>\n" +
                "<R34></R34>\n" +
                "<R35>S</R35>\n" +
                "<R36>"+genericAddressDetailsVO.getEmail()+"</R36>\n" +
                "<R37>"+ipAddress+"</R37>\n" +
                "<R38>*/*</R38>\n" +
                "<R39>Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)</R39>\n" +
                "<R40>"+trackingID+"</R40>\n" +
                "<R41>"+trackingID+"</R41>\n" +
                "<R42>"+NOTIFICATION_URL+"</R42>\n" +
                "<R43>2</R43>\n" +
                "<R44>2</R44>\n" +
                "<R45>"+commMerchantVO.getMerchantId()+"</R45>\n"+
                ""+recurringXML+""+
                "</R>";

        String result;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        transactionLogger.error("pfs notification url===" + NOTIFICATION_URL);
        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

        transactionLogger.error("-----request data===" + logData);
        transactionLogger.error("-----request===" + request);
        result = doPostHTTPSURLConnection(GATEWAYURL, request);
        transactionLogger.error("-----response---" + result);


        Map<String, String> responseMap = null;
        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }

        transactionLogger.error("pfs response---"+responseMap);

        String cardNumber=genericCardDetailsVO.getCardNum();

        String first_six=cardNumber.substring(0,6);
        String last_four=cardNumber.substring((cardNumber.length()-4),cardNumber.length());
        //transactionLogger.debug("first_six----"+first_six);
        //transactionLogger.debug("last_four----"+last_four);
        String rbid=responseMap.get("RBID");
        /*String rbId = recurringBillingVO.setRbid(rbid);*/
        transactionLogger.debug("rbid----"+rbid);
        if(responseMap.get("R1")!=null && (responseMap.get("R1").equals("0044")))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setDescription("Authorization Successful");

            if(responseMap.get("RBID")!=null && !responseMap.get("RBID").equals(""))
            {
                try
                {
                    String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
                    String isManualRecurring = recurringBillingVO.getIsManualRecurring();
                    if("Y".equalsIgnoreCase(isManualRecurring) && "N".equalsIgnoreCase(isAutomaticRecurring))
                    {
                        String amt=genericTransDetailsVO.getAmount();
                        //TODO - insert qyery for manual
                        recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                    }
                    else if("N".equalsIgnoreCase(isManualRecurring) && "Y".equalsIgnoreCase(isAutomaticRecurring))
                    {
                        recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                    }
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZDBViolationException in SingleCallManualRebill---",e);
/*PZExceptionHandler.handleDBCVEException(e, , PZOperations.MANUAL_REBILL);*/


                }
            }
            else
            {
                try
                {
                    transactionLogger.debug("inside try block of delete method----");
                    recurringManager.deleteEntryForPFSRebill(trackingID);
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZDBViolationException in SingleCallManualRebill---",e);
                }
            }


        }

        else if (responseMap.get("R3")!= null && !"".equals(responseMap.get("R3")) && responseMap.get("R1").equals("0000"))
        {
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(responseMap.get("R3"));
            commResponseVO.setPaReq(responseMap.get("R4"));
            commResponseVO.setMd(responseMap.get("R5"));
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Authorization failed");

            if(responseMap.get("RBID")==null || responseMap.get("RBID").equals(""))
            {
                try
                {
                    recurringManager.deleteEntryForPFSRebill(trackingID);
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZDBViolationException in SingleCallManualRebill---",e);
                }
            }
        }

        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        commResponseVO.setTransactionId(messageId);
        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setTransactionType("Authorisation");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processCapture of PfsPaymentGateway...");

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "clear";
        String messageId = UUID.randomUUID().toString();

        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";
            String data = "<R>\n" +
                    "<R1>"+trackingID+"</R1>\n" +
                    "<R2>"+amount+"</R2>\n" +
                    "<R3>"+commMerchantVO.getMerchantId()+"</R3>\n" +
                    "</R>";

            transactionLogger.error("pfs post capture===" + data);

            String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

            transactionLogger.error("pfs request capture===" + request);
            String result = doPostHTTPSURLConnection(GATEWAYURL, request);
            transactionLogger.error("pfs capture response---" + result);

            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = getResponseMap(result);
                String status = "fail";
                if(responseMap!=null && responseMap.get("R1").equals("0044"))
                {
                    status = "success";
                    commResponseVO.setDescription(responseMap.get("R2"));
                }
                commResponseVO.setStatus(status);
                //commResponseVO.setDescription(responseMap.get("R2"));
                commResponseVO.setTransactionType("Capture");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
       transactionLogger.debug("Entering processCapture of PfsPaymentGateway...");

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "reversalauth";
        String messageId = UUID.randomUUID().toString();

        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";

            String data = "<R>\n" +
                    "<R1>"+trackingID+"</R1>\n" +
                    "<R2>"+commMerchantVO.getMerchantId()+"</R2>\n" +
                    "<R3>1</R3>\n" +
                    "</R>";

            transactionLogger.error("pfs post cancel===" + data);

            String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

            transactionLogger.error("pfs request cancel===" + request);
            String result = doPostHTTPSURLConnection(GATEWAYURL, request);

            transactionLogger.error("pfs cancel response---" + result);
            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = getResponseMap(result);
                String status = "fail";
                if(responseMap!=null && responseMap.get("R1").equals("0050"))
                {
                    status = "success";
                    commResponseVO.setDescription(responseMap.get("R2"));
                }
                commResponseVO.setStatus(status);
                commResponseVO.setErrorCode(responseMap.get("R1"));
                //commResponseVO.setDescription(responseMap.get("R2"));
                commResponseVO.setTransactionType("Cancel");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {

        transactionLogger.info("Inside   PFSPaymentGateway  ::::::::");
        transactionLogger.info("Inside   processSale  ::::::::");

        //validateForSale(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            transactionLogger.info("MerchantId not configured");
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(),"processSale()",null,"common","Merchant Id not configured while placing transaction,accountId::"+accountId,PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null,"Merchant Id not configured while placing transaction,accountId::"+accountId,new Throwable("Merchant Id not configured while placing transaction,accountId::"+accountId));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commRequestVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();
        RecurringManager recurringManager = new RecurringManager();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "Register";
        String messageId = commRequestVO.getTransDetailsVO().getResponseHashInfo();
        /*String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
        String isManualRecurring = recurringBillingVO.getIsManualRecurring();*/

        //String recurringType = account.getRecurringType();
        String isRecurringFromAccounID = account.getIsRecurring();

        String ipAddress = genericAddressDetailsVO.getIp();
        if(ipAddress==null || ipAddress.equals(""))
        {
            ipAddress = genericAddressDetailsVO.getCardHolderIpAddress();
        }
        String recurringXML = "";
        if ("Y".equalsIgnoreCase(account.getIsRecurring()))
        {
            String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
            String isManualRecurring = recurringBillingVO.getIsManualRecurring();
        if("Y".equalsIgnoreCase(isManualRecurring) && "N".equalsIgnoreCase(isAutomaticRecurring))
        {
            transactionLogger.debug("isRecurring===="+isAutomaticRecurring);
            transactionLogger.debug("isManualRecurring====" + isManualRecurring);
            recurringXML = "<RB>\n" +
                    "<T>M</T>\n" +
                    "<A>S</A>\n" +
                    "</RB>\n";


        }
        else if ("N".equalsIgnoreCase(isManualRecurring) && "Y".equalsIgnoreCase(isAutomaticRecurring))
        {
            transactionLogger.debug("isRecurring====" + isAutomaticRecurring);
            transactionLogger.debug("isManualRecurring===="+isManualRecurring);
            transactionLogger.debug("from gateway---"+recurringBillingVO.getInterval()+recurringBillingVO.getFrequency()+recurringBillingVO.getRunDate());
            recurringXML = "<RB>\n" +
                    "<I>"+recurringBillingVO.getInterval().toLowerCase()+"</I>\n" +
                    "<F>"+recurringBillingVO.getFrequency()+"</F>\n" +
                    "<D>"+recurringBillingVO.getRunDate()+"</D>\n" +
                    "<T>A</T>\n" +
                    "<A>S</A>\n" +
                    "</RB>\n";
        }
        }
        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";
        String currencyCode = CurrencyCodeISO4217.getNumericCurrencyCode(genericTransDetailsVO.getCurrency());

        String data="<R>\n" +
                "<R1>C</R1>\n" +
                "<R2>"+trackingID+"</R2>\n" +
                "<R3>"+getAmount(genericTransDetailsVO.getAmount())+"</R3>\n" +
                "<R4>0</R4>\n" +
                "<R5>"+genericAddressDetailsVO.getFirstname()+"</R5>\n" +
                "<R6></R6>\n" +
                "<R7>"+genericAddressDetailsVO.getLastname()+"</R7>\n" +
                "<R8>"+genericAddressDetailsVO.getStreet()+"</R8>\n" +
                "<R9>"+","+"</R9>\n" +
                "<R10>"+genericAddressDetailsVO.getCity()+"</R10>\n" +
                "<R11>"+genericAddressDetailsVO.getState()+"</R11>\n" +
                "<R12>"+genericAddressDetailsVO.getZipCode()+"</R12>\n" +
                "<R13>"+genericAddressDetailsVO.getCountry()+"</R13>\n" +
                "<R14>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R14>\n" +
                "<R15>"+getPfsCardType(genericCardDetailsVO.getCardType())+"</R15>\n" +
                "<R16>"+genericCardDetailsVO.getCardNum()+"</R16>\n" +
                "<R17>"+genericCardDetailsVO.getExpMonth()+"</R17>\n" +
                "<R18>"+genericCardDetailsVO.getExpYear()+"</R18>\n" +
                "<R19>"+genericCardDetailsVO.getcVV()+"</R19>\n" +
                "<R20>"+currencyCode+"</R20>\n" +
                "<R21>0</R21>\n" +
                "<R22>"+genericAddressDetailsVO.getFirstname()+"</R22>\n" +
                "<R23></R23>\n" +
                "<R24>"+genericAddressDetailsVO.getLastname()+"</R24>\n" +
                "<R25>"+genericAddressDetailsVO.getStreet()+"</R25>\n" +
                "<R26>"+","+"</R26>\n" +
                "<R27>"+genericAddressDetailsVO.getCity()+"</R27>\n" +
                "<R28>"+genericAddressDetailsVO.getState()+"</R28>\n" +
                "<R29>"+genericAddressDetailsVO.getZipCode()+"</R29>\n" +
                "<R30>"+genericAddressDetailsVO.getCountry()+"</R30>\n" +
                "<R31>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R31>\n" +
                "<R32>"+genericTransDetailsVO.getOrderId()+"</R32>\n" +
                "<R33>MARK</R33>\n" +
                "<R34></R34>\n" +
                "<R35>S</R35>\n" +
                "<R36>"+genericAddressDetailsVO.getEmail()+"</R36>\n" +
                "<R37>"+ipAddress+"</R37>\n" +
                "<R38>*/*</R38>\n" +
                "<R39>Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)</R39>\n" +
                "<R40>"+trackingID+"</R40>\n" +
                "<R41>"+trackingID+"</R41>\n" +
                "<R42>"+NOTIFICATION_URL+"</R42>\n" +
                "<R43>2</R43>\n" +
                "<R44>2</R44>\n" +
                "<R45>"+commMerchantVO.getMerchantId()+"</R45>\n" +
                ""+recurringXML+""+
                "</R>";

        String requestData="<R>\n" +
                "<R1>C</R1>\n" +
                "<R2>"+trackingID+"</R2>\n" +
                "<R3>"+getAmount(genericTransDetailsVO.getAmount())+"</R3>\n" +
                "<R4>0</R4>\n" +
                "<R5>"+genericAddressDetailsVO.getFirstname()+"</R5>\n" +
                "<R6></R6>\n" +
                "<R7>"+genericAddressDetailsVO.getLastname()+"</R7>\n" +
                "<R8>"+genericAddressDetailsVO.getStreet()+"</R8>\n" +
                "<R9>"+","+"</R9>\n" +
                "<R10>"+genericAddressDetailsVO.getCity()+"</R10>\n" +
                "<R11>"+genericAddressDetailsVO.getState()+"</R11>\n" +
                "<R12>"+genericAddressDetailsVO.getZipCode()+"</R12>\n" +
                "<R13>"+genericAddressDetailsVO.getCountry()+"</R13>\n" +
                "<R14>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R14>\n" +
                "<R15>"+getPfsCardType(genericCardDetailsVO.getCardType())+"</R15>\n" +
                "<R20>"+currencyCode+"</R20>\n" +
                "<R21>0</R21>\n" +
                "<R22>"+genericAddressDetailsVO.getFirstname()+"</R22>\n" +
                "<R23></R23>\n" +
                "<R24>"+genericAddressDetailsVO.getLastname()+"</R24>\n" +
                "<R25>"+genericAddressDetailsVO.getStreet()+"</R25>\n" +
                "<R26>"+","+"</R26>\n" +
                "<R27>"+genericAddressDetailsVO.getCity()+"</R27>\n" +
                "<R28>"+genericAddressDetailsVO.getState()+"</R28>\n" +
                "<R29>"+genericAddressDetailsVO.getZipCode()+"</R29>\n" +
                "<R30>"+genericAddressDetailsVO.getCountry()+"</R30>\n" +
                "<R31>"+genericAddressDetailsVO.getPhone().replace("-","")+"</R31>\n" +
                "<R32>"+genericTransDetailsVO.getOrderId()+"</R32>\n" +
                "<R33>MARK</R33>\n" +
                "<R34></R34>\n" +
                "<R35>S</R35>\n" +
                "<R36>"+genericAddressDetailsVO.getEmail()+"</R36>\n" +
                "<R37>"+ipAddress+"</R37>\n" +
                "<R38>*/*</R38>\n" +
                "<R39>Mozilla/4.0 (compatible; Win32; WinHttp.WinHttpRequest.5)</R39>\n" +
                "<R40>"+trackingID+"</R40>\n" +
                "<R41>"+trackingID+"</R41>\n" +
                "<R42>"+NOTIFICATION_URL+"</R42>\n" +
                "<R43>2</R43>\n" +
                "<R44>2</R44>\n" +
                "<R45>"+commMerchantVO.getMerchantId()+"</R45>\n" +
                ""+recurringXML+""+
                "</R>";

        String result = "";
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();


        transactionLogger.error("------sale request-----"+requestData);
        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);
        transactionLogger.error("------sale request-----"+request);
        result = doPostHTTPSURLConnection(GATEWAYURL, request);
        transactionLogger.error("-----sale response-----"+result);

        Map<String, String> responseMap = null;
        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }

        String cardNumber = genericCardDetailsVO.getCardNum();
        String first_six=cardNumber.substring(0,6);
        String last_four=cardNumber.substring((cardNumber.length()-4),cardNumber.length());
        //log.debug("first_six----"+first_six);
        //log.debug("last_four----"+last_four);
        String rbid=responseMap.get("RBID");
        String name = genericAddressDetailsVO.getFirstname()+" "+genericAddressDetailsVO.getLastname();

        if(responseMap.get("R1")!=null && (responseMap.get("R1").equals("0044")))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setDescription("Transaction is Successful");

            if(responseMap.get("RBID")!=null && !responseMap.get("RBID").equals(""))
            {
                try
                {
                    String isAutomaticRecurring = recurringBillingVO.getIsAutomaticRecurring();
                    String isManualRecurring = recurringBillingVO.getIsManualRecurring();
                    if("Y".equalsIgnoreCase(isManualRecurring) && "N".equalsIgnoreCase(isAutomaticRecurring))
                    {
                        String amt=genericTransDetailsVO.getAmount();
                        //TODO - insert query for manual
                       // recurringManager.insertEntryForPFSManualRebill(trackingID,amt,name,first_six,last_four,rbid);
                        recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                    }
                    else if("N".equalsIgnoreCase(isManualRecurring) && "Y".equalsIgnoreCase(isAutomaticRecurring))
                    {
                        recurringManager.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
                    }
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZConstraintViolationException in SingleCallManualRebill---",e);
/*PZExceptionHandler.handleDBCVEException(e, , PZOperations.MANUAL_REBILL);*/


                }
            }
            else
            {
                try
                {
                    recurringManager.deleteEntryForPFSRebill(trackingID);
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZDBViolationException in SingleCallManualRebill---",e);
                }
            }
        }
        else if (responseMap.get("R3")!= null && !"".equals(responseMap.get("R3")) && responseMap.get("R1").equals("0000"))
        {
            commResponseVO.setStatus("pending3DConfirmation");
            commResponseVO.setUrlFor3DRedirect(responseMap.get("R3"));
            commResponseVO.setPaReq(responseMap.get("R4"));
            commResponseVO.setMd(responseMap.get("R5"));
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Transaction is failed");
            if(responseMap.get("RBID")==null || responseMap.get("RBID").equals(""))
            {
                try
                {
                    recurringManager.deleteEntryForPFSRebill(trackingID);
                }
                catch (PZDBViolationException e)
                {
                    transactionLogger.error("db violation exception",e);
                    transactionLogger.error("PZDBViolationException in SingleCallManualRebill---",e);
                }
            }
        }

        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        commResponseVO.setTransactionId(messageId);
        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setTransactionType("Sale");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {


        transactionLogger.info("  Inside   PfsPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   processRefund  ::::::::");
        validateForRefund(trackingID, requestVO);

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);

        if(account.getMerchantId()==null || account.getMerchantId().equals(""))
        {
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(), "processRefund()", null, "common", "Merchant Id not configured while refunding transaction,AccountId::" + accountId, PZTechnicalExceptionEnum.MAPPING_CONFIGURATION_NOT_PROVIDED,null, "Merchant Id not configured while refunding transaction,AccountId::" + accountId, new Throwable("Merchant Id not configured while refunding transaction,AccountId::"+accountId));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "Refund";
        String messageId = UUID.randomUUID().toString();


        String data="<R><R1>"+trackingID+"</R1>" +
                "<R2>"+amount+"</R2>" +
                "<R3>"+genericTransDetailsVO.getOrderDesc()+"</R3>" +
                "<R4>"+commMerchantVO.getMerchantId()+"</R4>" +
                "<R5>"+NOTIFICATION_URL+"</R5>" +
                "</R>";

        String result;
        CommResponseVO commResponseVO = new CommResponseVO();

        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);
        transactionLogger.error("pfs refund data---" + data);
        transactionLogger.error("pfs refund request---" + request);

        result = doPostHTTPSURLConnection(GATEWAYURL, request);

        transactionLogger.error("pfs refund response---" + result);
        Map<String, String> responseMap = null;

        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }



        /*if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0023"))
        {
            TransactionDAO tDAO = new TransactionDAO();

            try
            {
                log.debug("inside try sleep start -- confirmation received 0023");
                transactionLogger.debug("inside try sleep start -- confirmation received 0023");
                Thread.sleep(10000l);
                log.debug("inside try sleep end -- confirmation received 0023");
                transactionLogger.debug("inside try sleep end -- confirmation received 0023");
                vCurrentTransaction = tDAO.getDetailFromCommon(trackingID);
                if(vCurrentTransaction.getStatus().equals(ActionEntry.STATUS_REVERSAL_SUCCESSFUL) )
                {
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescription("Transaction reversed successfully");
                }
                else
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setDescription("Your transaction is submitted to bank. Please check the status after 5 minutes.");
                }
                log.debug("inside Refund of 0023");
                transactionLogger.debug("inside Refund of 0023");
            }
            catch (InterruptedException ie)
            {
                commResponseVO.setStatus("pending");
                commResponseVO.setDescription("Transaction submitted to bank. Please check the status after 5 minutes.");
            }
        }*/
        if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0054"))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setDescription("Refund Successful");
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Refund Failed");
        }

        commResponseVO.setTransactionId(messageId);
        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        commResponseVO.setTransactionType("Refund");
        commResponseVO.setRemark(responseMap.get("R2"));
        commResponseVO.setTransactionStatus(commResponseVO.getStatus());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        return commResponseVO;

    }

    public GenericResponseVO processRebilling(String trackingid, GenericRequestVO requestVO)  throws PZTechnicalViolationException,PZConstraintViolationException
    {

        transactionLogger.info("  Inside   PfsPaymentGateway  ::::::::");
        transactionLogger.info("  Inside   processRebill  ::::::::");

        validateForRebill(trackingid, requestVO);
        Functions functions = new Functions();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();

        if (!functions.isValueNull(recurringBillingVO.getRbid()))
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "processRebilling()", null, "common", "RBID not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "RBID not provided while rebilling transaction", new Throwable("RBID not provided while rebilling transaction"));
        }
        if (commRequestVO.getTransDetailsVO() != null)
        {

            if (!functions.isValueNull(commRequestVO.getTransDetailsVO().getAmount()))
            {
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "processRebilling()", null, "common", "Amount not provided while rebilling transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Amount not provided while rebilling transaction", new Throwable("Amount not provided while rebilling transaction"));
            }
        }
        else
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"processRebilling()",null,"common","TransactionDetails not provided while rebilling transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails not provided while rebilling transaction",new Throwable("TransactionDetails not provided while rebilling transaction"));
        }

        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();

        String amount = (int)(Double.parseDouble(genericTransDetailsVO.getAmount())*100)+"";
        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "processrecurringbilling";
        String messageId = UUID.randomUUID().toString();
        String rbid = recurringBillingVO.getRbid();

        String data = "<R><R1>"+rbid+"</R1>" +
                "<R2>"+amount+"</R2>" +
                "</R>";

        String result;

        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

        transactionLogger.error("pfs rebill data---"+data);
        transactionLogger.error("pfs rebill request---"+request);
        result = doPostHTTPSURLConnection(GATEWAYURL, request);
        transactionLogger.error("pfs rebill response---"+result);
        Map<String, String> responseMap = null;

        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }

       if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0000"))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setDescription("Process Recurring Billing Receive");
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Rebill Failed");
        }

        commResponseVO.setTransactionId(responseMap.get("R6"));
        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        commResponseVO.setTransactionType("Rebill");
        commResponseVO.setRemark(responseMap.get("R2"));
        commResponseVO.setTransactionStatus(commResponseVO.getStatus());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        return commResponseVO;
    }



    private static String getPfsCardType(String cardType)
    {
        String pfsCardType = null;
        if(cardType.equals("VISA"))
        {
            pfsCardType = "VSA";
        }
        else if(cardType.equals("MC"))
        {
            pfsCardType = "MSC";
        }
        return pfsCardType;  //To change body of created methods use File | Settings | File Templates.
    }



    public static Map<String,String> getResponseMap(String xmlResponseString) throws PZTechnicalViolationException
    {
        Map<String, String> responseMap = new HashMap<String, String>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        String strValue = null;
        try
        {
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(),"getResponseMap()",null,"common","Parse Configuration Exception while parsing Xml Response",PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        Document document = null;
        try
        {
            document = builder.parse(new InputSource(new StringReader(xmlResponseString.replaceAll("[^\\x20-\\x7e]", ""))));
        }
        catch (SAXException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(), "getResponseMap()", null, "common", "SAX Exception while parsing Xml Response", PZTechnicalExceptionEnum.SAXEXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PfsPaymentGateway.class.getName(), "getResponseMap()", null, "common", "IO Exception while parsing Xml Response", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        Element rootElement = document.getDocumentElement();
        strValue = getTagValue("R1", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R1",strValue);
        }
        strValue = getTagValue("R2", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R2",strValue);
        }
        strValue = getTagValue("R3", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R3",strValue);
        }
        strValue = getTagValue("R4", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R4",strValue);
        }
        strValue = getTagValue("R5", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R5",strValue);
        }
        strValue = getTagValue("R6", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R6",strValue);
        }
        strValue = getTagValue("R7", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R7",strValue);
        }
        strValue = getTagValue("R8", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R8",strValue);
        }
        strValue = getTagValue("R9", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R9",strValue);
        }
        strValue = getTagValue("R10", rootElement);
        if(strValue !=null)
        {
            responseMap.put("R10",strValue);
        }
        strValue = getTagValue("RBID", rootElement);
        if(strValue !=null)
        {
            responseMap.put("RBID",strValue);
        }

        return responseMap;
    }
    private static String getTagValue(String sTag, Element eElement) {

        NodeList nlList = null;
        String value  ="";
        if(eElement!=null && eElement.getElementsByTagName(sTag)!=null && eElement.getElementsByTagName(sTag).item(0)!=null)
        {
            nlList =  eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        }
        if(nlList!=null && nlList.item(0)!=null)
        {
            Node nValue = (Node) nlList.item(0);
            value =	nValue.getNodeValue();

        }

        return value;

    }
    public GenericResponseVO processConfirm(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.info("Inside   PFSPaymentGateway  ::::::::");
        transactionLogger.info("Inside   processConfirm ::::::::");

        ComConfirmRequestVO confirmRequestVO = (ComConfirmRequestVO)requestVO;

        String username = confirmRequestVO.getUserName();
        String password = confirmRequestVO.getPassword();
        String apiSignature = "Confirm";
        String messageId = UUID.randomUUID().toString();


        String data="<R><R1>"+confirmRequestVO.getPaRes()+"</R1>\n" +
                "<R2>"+confirmRequestVO.getTrackingId()+"</R2>\n" +
                "<R3>"+confirmRequestVO.getMerchantId()+"</R3></R>";

        String result;
        CommResponseVO commResponseVO = new CommResponseVO();

        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

        transactionLogger.error("data proessConfirm data---" + data);
        transactionLogger.error("request proessConfirm request---" + request);

        result = doPostHTTPSURLConnection(GATEWAYURL, request);
        transactionLogger.error("XML in proessConfirm---" + result);

        Map<String, String> responseMap = null;
        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }

        /*if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0008"))
        {
            commResponseVO.setStatus("pending");
            commResponseVO.setDescription("Your transaction is submitted to bank successfully. Please check the status after 2 minutes.");
        }*/
        if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0044"))
        {
            commResponseVO.setStatus("success");
            commResponseVO.setDescription("Transaction Successful");
        }
        else
        {
            commResponseVO.setStatus("fail");
            commResponseVO.setDescription("Your transaction is failed---"+responseMap.get("R2"));
        }

        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        commResponseVO.setTransactionId(messageId);
        commResponseVO.setErrorCode(responseMap.get("R1"));
        commResponseVO.setTransactionType("Sale");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

        return commResponseVO;
    }

    public GenericResponseVO processUpdateRecurring(GenericRequestVO requestVO,String rbid)
    {
        transactionLogger.info("Inside UpdateRecurring of PFS::::::::");

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "updaterecurringbilling";
        String messageId = UUID.randomUUID().toString();

        String amount = (int)(Double.parseDouble(recurringBillingVO.getAmount())*100)+"";
        String data="<R><R1>"+rbid+"</R1>\n" +
                    "<R2>"+recurringBillingVO.getInterval()+"</R2>\n" +
                    "<R3>"+recurringBillingVO.getFrequency()+"</R3>\n" +
                    "<R4>"+recurringBillingVO.getRunDate()+"</R4>\n"+
                    "<R5>"+amount+"</R5>\n"+
                    "</R>";

        try
        {
            transactionLogger.error("----processUpdateRecurring data---"+data);
            String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

            transactionLogger.error("----processUpdateRecurring request---"+request);
            String result = doPostHTTPSURLConnection(GATEWAYURL, request);
            transactionLogger.error("XML in processUpdateRecurring---"+result);
            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = getResponseMap(result);
            }
            //System.out.println("responsemap for update------"+responseMap.get("R1"));
            if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0027"))
            {
                commResponseVO.setStatus("Update Successful for Recurring Billing ID-"+rbid);
                commResponseVO.setDescription(responseMap.get("R2"));

            }
            else
            {
                commResponseVO.setStatus("Update Fail for Recurring Billing ID-"+rbid);
                commResponseVO.setDescription(responseMap.get("R2"));
            }
            commResponseVO.setErrorCode(responseMap.get("R1"));
        }
        catch (PZTechnicalViolationException tve)
        {
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("PfsPaymentgateway.java","processUpdateRecurring()",null,"Common","Technical Exception occurred",null,null,tve.getMessage(),tve.getCause(),null,"Update Recurring Billing");
        }
        return commResponseVO;
    }

    public GenericResponseVO processDeleteRecurring(GenericRequestVO requestVO,String rbid) throws PZTechnicalViolationException
    {
        transactionLogger.info("Inside DeleteRecurring of PFS::::::::");

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        //RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "deleterecurringbilling";
        String messageId = UUID.randomUUID().toString();

        String data="<R><R1>"+rbid+"</R1></R>";

            String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

            transactionLogger.error("-----processDeleteRecurring data-----" + data);
            transactionLogger.error("-----processDeleteRecurring request-----"+request);

            String result = doPostHTTPSURLConnection(GATEWAYURL, request);
            transactionLogger.error("---processDeleteRecurring response ---"+result);

            Map<String, String> responseMap = null;
            if(result !=null)
            {
                responseMap = getResponseMap(result);
            }
        //System.out.println("responsemap for delete----"+responseMap.get("R1"));
            if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0027"))
            {
                commResponseVO.setStatus("Delete Successful for Recurring Billing-"+rbid);
                commResponseVO.setDescription(responseMap.get("R2"));
            }
            else
            {
                commResponseVO.setStatus("Delete Fail for Recurirng Billing-"+rbid);
                commResponseVO.setDescription(responseMap.get("R2"));
            }
        commResponseVO.setErrorCode(responseMap.get("R1"));

        return commResponseVO;
    }

    public GenericResponseVO processActivateDeactivateRecurring(GenericRequestVO requestVO,String rbid) throws PZTechnicalViolationException
    {
        transactionLogger.info("Inside ActivateDeactivateRecurring of PFS::::::::");

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        RecurringBillingVO recurringBillingVO = commRequestVO.getRecurringBillingVO();

        String username = commMerchantVO.getMerchantUsername();
        String password = commMerchantVO.getPassword();
        String apiSignature = "deactivaterecurringbilling";
        String messageId = UUID.randomUUID().toString();


        String data="<R><R1>"+rbid+"</R1>\n" +
                "<R2>"+recurringBillingVO.getActiveDeactive()+"</R2>\n"+ //todo : change 0/1
                "</R>";
        String request = "Username="+username+"&Password="+password+"&APISignature="+apiSignature+"&MessageID="+messageId+"&Data="+URLEncoder.encode(data);

        transactionLogger.error("-----processActivateDeactivateRecurring data-----" + data);
        transactionLogger.error("-----processActivateDeactivateRecurring request-----"+request);
        String result = doPostHTTPSURLConnection(GATEWAYURL, request);
        transactionLogger.error("-----processActivateDeactivateRecurring---"+result);
        Map<String, String> responseMap = null;
        if(result !=null)
        {
            responseMap = getResponseMap(result);
        }

        if(responseMap.get("R1")!=null && responseMap.get("R1").equals("0043"))
        {
            commResponseVO.setStatus("Successful for Recurring Billing ID-"+rbid);
            commResponseVO.setDescription(responseMap.get("R2"));
        }
        else
        {
            commResponseVO.setStatus("Fail for Recurring Billing ID-"+rbid);
            commResponseVO.setDescription(responseMap.get("R2"));
        }

        return commResponseVO;
    }

    private void validateForSale(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(trackingID ==null || trackingID.equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Tracking Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Tracking Id not provided while placing the transaction",new Throwable("Tracking Id not provided while placing the transaction"));
        }

        if(requestVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Request  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Request  not provided while placing the transaction",new Throwable("Request  not provided while placing the transaction"));
        }

        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;


        GenericTransDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        if(genericTransDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","TransactionDetails  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"TransactionDetails  not provided while placing the transaction",new Throwable("TransactionDetails  not provided while placing the transaction"));
        }
        if(genericTransDetailsVO.getAmount() == null || genericTransDetailsVO.getAmount().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Amount not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Amount not provided while placing the transaction",new Throwable("Amount not provided while placing the transaction"));
        }

        GenericAddressDetailsVO genericAddressDetailsVO= commRequestVO.getAddressDetailsVO();

        if(genericAddressDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Addressdetails  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"Addressdetails  not provided while placing the transaction",new Throwable("Addressdetails  not provided while placing the transaction"));
        }
        //User Details
        if(genericAddressDetailsVO.getFirstname()==null|| genericAddressDetailsVO.getFirstname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","First Name not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"First Name not provided while placing the transaction",new Throwable("First Name not provided while placing the transaction"));
        }
/*
        if(genericAddressDetailsVO.getLastname()==null|| genericAddressDetailsVO.getLastname().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Last Name not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Last Name not provided while placing the transaction",new Throwable("Last Name not provided while placing the transaction"));
        }
*/

        if(genericAddressDetailsVO.getEmail()==null || genericAddressDetailsVO.getEmail().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Email Id not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Email Id not provided while placing the transaction",new Throwable("Email Id not provided while placing the transaction"));
        }


        /*if(genericAddressDetailsVO.getIp()==null || genericAddressDetailsVO.getIp().equals(""))
        {
            log.info("Customer IP not provided");
            throw new SystemError("Customer IP not provided");
        }*/


        //Address Details
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String addressValidation = account.getAddressValidation();
        if (addressValidation.equalsIgnoreCase("Y"))
        {

            if (genericAddressDetailsVO.getStreet() == null || genericAddressDetailsVO.getStreet().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "validateForSale()", null, "common", "Street not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Street not provided while placing the transaction", new Throwable("Street not provided while placing the transaction"));
            }

            if (genericAddressDetailsVO.getCity() == null || genericAddressDetailsVO.getCity().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "validateForSale()", null, "common", "City not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "City not provided while placing the transaction", new Throwable("City not provided while placing the transaction"));
            }


            if (genericAddressDetailsVO.getCountry() == null || genericAddressDetailsVO.getCountry().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "validateForSale()", null, "common", "Country not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Country not provided while placing the transaction", new Throwable("Country not provided while placing the transaction"));
            }

            if (genericAddressDetailsVO.getState() == null || genericAddressDetailsVO.getState().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "validateForSale()", null, "common", "State not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "State not provided while placing the transaction", new Throwable("State not provided while placing the transaction"));
            }

            if (genericAddressDetailsVO.getZipCode() == null || genericAddressDetailsVO.getZipCode().equals(""))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                if (errorCodeListVO.getListOfError().isEmpty())
                    errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(), "validateForSale()", null, "common", "Zip Code not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Zip Code not provided while placing the transaction", new Throwable("Zip Code not provided while placing the transaction"));
            }
        }


        GenericCardDetailsVO genericCardDetailsVO= commRequestVO.getCardDetailsVO();
        if(genericCardDetailsVO ==null)
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","CardDetails  not provided while placing the transaction", PZConstraintExceptionEnum.VO_MISSING,errorCodeListVO,"CardDetails  not provided while placing the transaction",new Throwable("CardDetails  not provided while placing the transaction"));
        }
        //Card Details

        if(genericCardDetailsVO.getCardNum()==null || genericCardDetailsVO.getCardNum().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Card NO not provided while placing the transaction",new Throwable("Card NO not provided while placing the transaction"));
        }

        String ccnum = genericCardDetailsVO.getCardNum();

        if(ccnum!=null && !Functions.isValid(ccnum))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REQUEST_NULL);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Card NO provided is Invalid while placing the transaction", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,"Card NO provided is Invalid while placing the transaction",new Throwable("Card NO provided is Invlaid while placing the transaction"));
        }



        if(genericCardDetailsVO.getcVV()==null || genericCardDetailsVO.getcVV().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","CVV not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"CVV not provided while placing the transaction",new Throwable("CVV not provided while placing the transaction"));
        }


        if(genericCardDetailsVO.getExpMonth()==null || genericCardDetailsVO.getExpMonth().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Month not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Month not provided while placing the transaction",new Throwable("Expiry Month not provided while placing the transaction"));
        }


        if(genericCardDetailsVO.getExpYear()==null || genericCardDetailsVO.getExpYear().equals(""))
        {
            errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
            if (errorCodeListVO.getListOfError().isEmpty())
                errorCodeListVO.addListOfError(errorCodeVO);
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForSale()",null,"common","Expiry Year not provided while placing the transaction", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,"Expiry Year not provided while placing the transaction",new Throwable("Expiry Year not provided while placing the transaction"));
        }

    }

    public static String doPostHTTPSURLConnection(String strURL, String req) throws PZTechnicalViolationException
    {
        OutputStreamWriter outSW = null;
        BufferedReader in = null;
        String strResponse="";
        URLConnection connection = null;
        try
        {
            URL url = new URL(strURL);
            try
            {
                connection = url.openConnection();
                connection.setConnectTimeout(120000);
                connection.setReadTimeout(120000);
            }
            catch (IOException io)
            {
                PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java", "doPostHTTPSURLConnection()", null, "common", "SSL Handshake Exception:::", PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE,null, io.getMessage(), io.getCause());
            }
            if(connection instanceof HttpURLConnection)
            {
                ((HttpURLConnection)connection).setRequestMethod("POST");
            }

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set request headers for content type and length
            connection.setRequestProperty("Content-type","application/x-www-form-urlencoded");

            outSW = new OutputStreamWriter(
                    connection.getOutputStream());
            outSW.write(req);
            outSW.close();

            in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null)
            {
                strResponse = strResponse + decodedString;
            }
            in.close();
        }
        catch (MalformedURLException me)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java", "doPostHTTPSURLConnection()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON,null, me.getMessage(), me.getCause());
        }
        catch (ProtocolException pe)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION,null,pe.getMessage(),pe.getCause());
        }
        catch (IOException ex)
        {
            PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java","doPostHTTPSURLConnection()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,ex.getMessage(),ex.getCause());
        }
        finally
        {
            if (outSW != null) {
                try {
                    outSW.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e)
                {
                    PZExceptionHandler.raiseTechnicalViolationException("PfsUtills.java","doPostHTTPSURLConnection()",null,"common","IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
                }
            }
        }
        if (strResponse == null)
            return "";
        else
            return strResponse;
    }

    private void validateForRefund(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRefund()",null,"common","Tracking Id not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }

        if(requestVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRefund()",null,"common","Request  not provided while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"Request  not provided while placing transaction",new Throwable("Request  not provided while placing transaction"));
        }

        CommRequestVO credoraxRequestVO  =    (CommRequestVO)requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = credoraxRequestVO.getTransDetailsVO();
        if(commTransactionDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRefund()",null,"common","TransactionDetails  not provided while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRefund()",null,"common","Amount not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }

        if(commTransactionDetailsVO.getPreviousTransactionId() == null || commTransactionDetailsVO.getPreviousTransactionId().equals(""))
        {
           PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRefund()",null,"common","Previous Transaction Id not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Previous Transaction Id not provided while placing transaction",new Throwable("Previous Transaction Id not provided while placing transaction"));
        }
    }

    private void validateForRebill(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        if(trackingID ==null || trackingID.equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRebill()",null,"common","Tracking Id not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Tracking Id not provided while placing transaction",new Throwable("Tracking Id not provided while placing transaction"));
        }


        CommRequestVO commRequestVO  =    (CommRequestVO)requestVO;

        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        if(commTransactionDetailsVO ==null)
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRebill()",null,"common","TransactionDetails  not provided while placing transaction",PZConstraintExceptionEnum.VO_MISSING,null,"TransactionDetails  not provided while placing transaction",new Throwable("TransactionDetails  not provided while placing transaction"));
        }
        if(commTransactionDetailsVO.getAmount() == null || commTransactionDetailsVO.getAmount().equals(""))
        {
            PZExceptionHandler.raiseConstraintViolationException(PfsPaymentGateway.class.getName(),"validateForRebill()",null,"common","Amount not provided while placing transaction",PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Amount not provided while placing transaction",new Throwable("Amount not provided while placing transaction"));
        }
    }

    public String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        String amt = d.format(dObj2);
        return amt;
    }

}