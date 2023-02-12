package com.payment.globalgate;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.apco.core.AsyncApcoPayQueryService;
import com.payment.apco.www.ServiceLocator;
import com.payment.apco.www.ServiceSoap12Stub;
import com.payment.apcoFastpay.ApcoFastpayUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.apache.axis.AxisFault;
import org.apache.axis.i18n.RB;


import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Jyoti on 8/16/2021.
 */
public class GlobalGatePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "globalgate";
    private static Logger log=new Logger(GlobalGatePaymentGateway.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(GlobalGatePaymentGateway.class.getName());
    ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.GlobalGateServlet");
    public GlobalGatePaymentGateway(String accountId){
        this.accountId=accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        log.error("Entering into processAuthentication of GlobalGatePaymentGateway :::::");
        transactionLogger.error("Entering into processAuthentication of  GlobalGatePaymentGateway :::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();

        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String Pass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String is3dSupported = GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String TrType="4";
        String CardNum = cardDetailsVO.getCardNum();
        String CVV2 = cardDetailsVO.getcVV();
        String ExpDay="";
        String ExpMonth = cardDetailsVO.getExpMonth();
        String ExpYear = cardDetailsVO.getExpYear();
        String CardHName = addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname();
        String Amount = transDetailsVO.getAmount();
        String Addr = addressDetailsVO.getStreet();
        String PostCode = addressDetailsVO.getZipCode();
        String TransID="";
        String UserIP="";
        String UDF1=transDetailsVO.getOrderId();
        if(UDF1.equals(trackingID)){
            UDF1=transDetailsVO.getMerchantOrderId();
        }
        String UDF2="";
        String UDF3="";
        String CALL_EXECUTE_AFTER=rb.getString("CALL_EXECUTE_AFTER");
        String CALL_EXECUTE_INTERVAL=rb.getString("CALL_EXECUTE_INTERVAL");
        String MAX_EXECUTE_SEC=rb.getString("MAX_EXECUTE_SEC");


        String redirectionUrl = "";
        String statusUrl = "";
        String attemptThreeD = "";
        String reject3DCard = commRequestVO.getReject3DCard();
        String tmpl_amount="";
        String tmpl_currency="";
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }
        transactionLogger.error("attemptThreeD----" + attemptThreeD);
        transactionLogger.error("reject3DCard----" + reject3DCard);
        transactionLogger.error("tmpl_amount----" + tmpl_amount);
        transactionLogger.error("tmpl_currency----" + tmpl_currency);


        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            redirectionUrl = "https://" + commMerchantVO.getHostUrl() + "/transaction/GlobalGateFrontEndServlet";
            System.out.println("redirectionUrl===>"+redirectionUrl);
            statusUrl = "https://" + commMerchantVO.getHostUrl() + "/transaction/GlobalGateBackEndServlet";
            System.out.println("statusUrl===>"+statusUrl);

        }
        else
        {
            redirectionUrl = rb.getString("GLOBALGATE_FRONTEND");
            statusUrl = rb.getString("GLOBAGATE_BACKEND");

        }
        try
        {
            if ("Y".equalsIgnoreCase(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD)))
            {
                String data = "<WS><ORef>" + trackingID + "</ORef><RedirectionURL>" + rb.getString("GLOBALGATE_3D_FRONTEND") + "</RedirectionURL>" +
                        "<status_url>" + statusUrl + "</status_url></WS>";
                String data1 = data.replaceAll("<", "&lt;");
                UDF3 = data1.replaceAll(">", "&gt;");

                String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <Do3DSTransaction xmlns=\"https://www.apsp.biz/\">\n" +
                        "      <MerchID>" + MerchID + "</MerchID>\n" +
                        "      <MerchPassword>" + Pass + "</MerchPassword>\n" +
                        "      <TrType>4</TrType>\n" +
                        "      <CardNum>" + CardNum + "</CardNum>\n" +
                        "      <CVV2>" + CVV2 + "</CVV2>\n" +
                        "      <ExpDay>" + ExpDay + "</ExpDay>\n" +
                        "      <ExpMonth>" + ExpMonth + "</ExpMonth>\n" +
                        "      <ExpYear>" + ExpYear + "</ExpYear>\n" +
                        "      <CardHName>" + CardHName + "</CardHName>\n" +
                        "      <Amount>" + Amount + "</Amount>\n" +
                        "      <CurrencyCode>" + String.valueOf(currencyCode) + "</CurrencyCode>\n" +
                        "      <Addr>" + Addr + "</Addr>\n" +
                        "      <PostCode>" + PostCode + "</PostCode>\n" +
                        "      <TransID></TransID>\n" +
                        "      <UserIP></UserIP>\n" +
                        "      <UDF1>"+UDF1+"</UDF1>\n" +
                        "      <UDF2></UDF2>\n" +
                        "      <UDF3>" + UDF3 + "</UDF3>\n" +
                        "      <OrderRef>" + trackingID + "</OrderRef>\n" +
                        "    </Do3DSTransaction>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";



                String requestlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <Do3DSTransaction xmlns=\"https://www.apsp.biz/\">\n" +
                        "      <MerchID>" + MerchID + "</MerchID>\n" +
                        "      <MerchPassword>" + Pass + "</MerchPassword>\n" +
                        "      <TrType>4</TrType>\n" +
                        "      <CardNum>" + functions.maskingPan(CardNum )+ "</CardNum>\n" +
                        "      <CVV2>" + functions.maskingNumber(CVV2) + "</CVV2>\n" +
                        "      <ExpDay>" + functions.maskingNumber(ExpDay) + "</ExpDay>\n" +
                        "      <ExpMonth>" + functions.maskingNumber(ExpMonth) + "</ExpMonth>\n" +
                        "      <ExpYear>" + functions.maskingNumber(ExpYear) + "</ExpYear>\n" +
                        "      <CardHName>" + functions.maskingFirstName(addressDetailsVO.getFirstname())+" "+functions.maskingLastName(addressDetailsVO.getLastname()) + "</CardHName>\n" +
                        "      <Amount>" + Amount + "</Amount>\n" +
                        "      <CurrencyCode>" + String.valueOf(currencyCode) + "</CurrencyCode>\n" +
                        "      <Addr>" + Addr + "</Addr>\n" +
                        "      <PostCode>" + PostCode + "</PostCode>\n" +
                        "      <TransID></TransID>\n" +
                        "      <UserIP></UserIP>\n" +
                        "      <UDF1>"+UDF1+"</UDF1>\n" +
                        "      <UDF2></UDF2>\n" +
                        "      <UDF3>" + UDF3 + "</UDF3>\n" +
                        "      <OrderRef>" + trackingID + "</OrderRef>\n" +
                        "    </Do3DSTransaction>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";

                transactionLogger.debug("Auth request--for--" + trackingID + "--" + requestlog);

                String response = GlobalGateUtils.doPostHTTPSURLConnection(rb.getString("service_url"), request);

                transactionLogger.debug("Auth response--for--" + trackingID + "--" + response);

                if (functions.isValueNull(response))
                {
                    String _3Ddata = "";
                    String result = "";
                    String _3DTicket = "";
                    String status = "";
                    String remark = "";
                    HashMap map = (HashMap) GlobalGateUtils.readGlobalgate3DRedirectionXMLResponse(response);
                    if (map != null || map.size() != 0)
                    {
                        _3Ddata = (String) map.get("Do3DSTransactionResult");
                        String[] split = _3Ddata.split(Pattern.quote("||"));
                        result = split[0];
                        _3DTicket = split[1];

                        transactionLogger.debug("result----" + result + "-----3dTicket---" + _3DTicket);

                        if ("ENROLLED".equalsIgnoreCase(result))
                        {

                            if ("Y".equals(reject3DCard))
                            {
                                transactionLogger.error("rejecting 3d card as per configuration ");
                                commResponseVO.setStatus("failed");
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                commResponseVO.setTransactionId(_3DTicket);
                                commResponseVO.setDescription("3D Enrolled Card");
                                commResponseVO.setRemark("3D Enrolled Card");
                            }
                            else
                            {
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(rb.getString("3D_RedirectUrl") + _3DTicket);
                                commResponseVO.setTransactionId(_3DTicket);
                                commResponseVO.setRemark(result);
                                String isThreadAllowed=rb.getString("THREAD_CALL");
                                if("Y".equalsIgnoreCase(isThreadAllowed))
                                    new AsyncGlobalGateQueryService(trackingID,accountId,_3DTicket,"N",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
                            }

                        }
                        else if ("Only3D".equalsIgnoreCase(attemptThreeD))
                        {
                            transactionLogger.error("Only 3D Card Required");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            commResponseVO.setDescription("Only 3D Card Required");
                            commResponseVO.setRemark("Only 3D Card Required");

                        }
                        else if ("CAPTURED".equalsIgnoreCase(result) || "APPROVED".equalsIgnoreCase(result))
                        {
                            remark = result;
                            status = "success";
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(remark);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }
                        else
                        {
                            remark = result;
                            status = "fail";
                            //commResponseVO.setDescription("Transaction Failed");
                            commResponseVO.setDescription(remark);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(remark);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }


                        if (functions.isValueNull(split[1]))
                        {
                            commResponseVO.setTransactionId(split[1]);
                        }
                        if (split.length > 2)
                        {
                            commResponseVO.setErrorCode(split[2]);
                        }

                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        String responseDate = dateFormat.format(date);
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));


                    }

                }
            }
            else
            {
                ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
                serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
                assertNotNull("binding is null", serviceSoap12Stub);
                serviceSoap12Stub.setTimeout(60000);
                log.error("------ Auth request------" + MerchID + "--" + Pass + "--" + TrType + "--" + functions.maskingPan(CardNum) + "--" + functions.maskingNumber(CVV2) + "--" + functions.maskingNumber(ExpDay) + "--" + functions.maskingNumber(ExpMonth) + "--" + functions.maskingNumber(ExpYear) + "--" + CardHName + "--" + Amount + "--" + currencyCode + "--" + Addr + "--" + PostCode + "--" + TransID + "--" + UserIP + "--" + UDF1 + "--" + UDF2 + "--" + UDF3);
                String responseDocument = serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);
                log.error("------ Auth response------" + responseDocument);
                transactionLogger.error("------ Auth Non-3D response--for--" + trackingID + "--" + responseDocument);

                String[] split = responseDocument.split(Pattern.quote("||"));
                log.debug("split0----" + split[0]);
                log.debug("split1----" + split[1]);

                String status = "";
                if (responseDocument != null)
                {
                    if (functions.isValueNull(split[0]))
                    {
                        if (split[0].equalsIgnoreCase("APPROVED"))
                        {
                            status = "success";
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(split[0]);

                        }
                        else
                        {
                            status = "fail";
                            //commResponseVO.setDescription("Transaction Failed");
                            commResponseVO.setDescription(split[0]);

                        }
                    }
                    else
                    {
                        status = "fail";
                        commResponseVO.setDescription("Transaction Failed");
                       // commResponseVO.setDescription(split[0]);

                    }
                }
                if (functions.isValueNull(split[1]))
                {
                    commResponseVO.setTransactionId(split[1]);
                }
                if (split.length > 2)
                {
                    commResponseVO.setErrorCode(split[2]);
                }

                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType("auth");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch (ServiceException e)
        {
            log.error("Auth ServiceException--for--" + trackingID + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Service Exception while refunding transaction ", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (AxisFault e)
        {
            log.error("Auth AxisFault--for--" + trackingID + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());

        }
        catch (RemoteException e)
        {
            log.error("Auth RemoteException--for--" + trackingID + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processAuthentication()", null, "common", "Remote Exception while refunding transaction via AlliedWallet", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (Exception e)
        {
            log.error("Auth Exception--for--" + trackingID + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processSale()", null, "common", "Remote Exception while refunding transaction via AlliedWallet", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        log.error("Entering into processSale of GlobalGatePaymentGateway:::::");
        transactionLogger.error("Entering into processSale of GlobalGatePaymentGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String Pass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String is3dSupported = GatewayAccountService.getGatewayAccount(accountId).get_3DSupportAccount();
        String TrType = "1";
        String CardNum = cardDetailsVO.getCardNum();
        String CVV2 = cardDetailsVO.getcVV();
        String ExpDay = "";
        String ExpMonth = cardDetailsVO.getExpMonth();
        String ExpYear = cardDetailsVO.getExpYear();
        String CardHName = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
        String Amount = transDetailsVO.getAmount();
        String Addr = addressDetailsVO.getStreet();
        String PostCode = addressDetailsVO.getZipCode();
        String TransID = "";
        String UserIP = "";
        String UDF1=transDetailsVO.getOrderId();
        if(UDF1.equals(trackingID)){
            UDF1=transDetailsVO.getMerchantOrderId();
        }
        transactionLogger.debug("UDF1------"+UDF1);
        String UDF2 = "";
        String UDF3 = "";
        String tmpl_amount="";
        String tmpl_currency="";
        String redirectionUrl="";
        String statusUrl="";
        String attemptThreeD = "";
        String reject3DCard=commRequestVO.getReject3DCard();
        String CALL_EXECUTE_AFTER=rb.getString("CALL_EXECUTE_AFTER");
        String CALL_EXECUTE_INTERVAL=rb.getString("CALL_EXECUTE_INTERVAL");
        String MAX_EXECUTE_SEC=rb.getString("MAX_EXECUTE_SEC");
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        if (functions.isValueNull(commRequestVO.getAttemptThreeD()))
        {
            attemptThreeD = commRequestVO.getAttemptThreeD();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }
        transactionLogger.error("attemptThreeD----"+attemptThreeD);
        transactionLogger.error("reject3DCard----"+reject3DCard);

        if(functions.isValueNull(commMerchantVO.getHostUrl())){
            redirectionUrl="https://"+commMerchantVO.getHostUrl()+"/transaction/GlobalGateFrontEndServlet";
            statusUrl="https://"+commMerchantVO.getHostUrl()+"/transaction/GlobalGateBackEndServlet";
        }else
        {
            redirectionUrl=rb.getString("GLOBALGATE_FRONTEND");
            statusUrl=rb.getString("GLOBALGATE_BACKEND");

        }
        try
        {
            if ("Y".equalsIgnoreCase(is3dSupported) && !("Direct".equalsIgnoreCase(attemptThreeD)))
            {
                String data = "<WS><ORef>" + trackingID + "</ORef><RedirectionURL>"+rb.getString("GLOBALGATE_3D_FRONTEND")+"</RedirectionURL>" +
                        "<status_url>"+statusUrl+"</status_url></WS>";
                String data1 = data.replaceAll("<", "&lt;");
                UDF3 = data1.replaceAll(">", "&gt;");

                String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <Do3DSTransaction xmlns=\"https://www.apsp.biz/\">\n" +
                        "      <MerchID>" + MerchID + "</MerchID>\n" +
                        "      <MerchPassword>" + Pass + "</MerchPassword>\n" +
                        "      <TrType>1</TrType>\n" +
                        "      <CardNum>" + CardNum + "</CardNum>\n" +
                        "      <CVV2>" + CVV2 + "</CVV2>\n" +
                        "      <ExpDay>" + ExpDay + "</ExpDay>\n" +
                        "      <ExpMonth>" + ExpMonth + "</ExpMonth>\n" +
                        "      <ExpYear>" + ExpYear + "</ExpYear>\n" +
                        "      <CardHName>" + CardHName + "</CardHName>\n" +
                        "      <Amount>" + Amount + "</Amount>\n" +
                        "      <CurrencyCode>" + String.valueOf(currencyCode) + "</CurrencyCode>\n" +
                        "      <Addr>" + Addr + "</Addr>\n" +
                        "      <PostCode>" + PostCode + "</PostCode>\n" +
                        "      <TransID></TransID>\n" +
                        "      <UserIP></UserIP>\n" +
                        "      <UDF1>"+UDF1+"</UDF1>\n" +
                        "      <UDF2></UDF2>\n" +
                        "      <UDF3>" + UDF3 + "</UDF3>\n" +
                        "      <OrderRef>" + trackingID + "</OrderRef>\n" +
                        "    </Do3DSTransaction>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";

                String requestlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "  <soap:Body>\n" +
                        "    <Do3DSTransaction xmlns=\"https://www.apsp.biz/\">\n" +
                        "      <MerchID>" + MerchID + "</MerchID>\n" +
                        "      <MerchPassword>" + Pass + "</MerchPassword>\n" +
                        "      <TrType>1</TrType>\n" +
                        "      <CardNum>" + functions.maskingPan(CardNum) + "</CardNum>\n" +
                        "      <CVV2>" + functions.maskingNumber(CVV2) + "</CVV2>\n" +
                        "      <ExpDay>" + functions.maskingNumber(ExpDay) + "</ExpDay>\n" +
                        "      <ExpMonth>" + functions.maskingNumber(ExpMonth) + "</ExpMonth>\n" +
                        "      <ExpYear>" + functions.maskingNumber(ExpYear) + "</ExpYear>\n" +
                        "      <CardHName>" + functions.maskingFirstName(addressDetailsVO.getFirstname())+" "+functions.maskingLastName(addressDetailsVO.getLastname()) + "</CardHName>\n" +
                        "      <Amount>" + Amount + "</Amount>\n" +
                        "      <CurrencyCode>" + String.valueOf(currencyCode) + "</CurrencyCode>\n" +
                        "      <Addr>" + Addr + "</Addr>\n" +
                        "      <PostCode>" + PostCode + "</PostCode>\n" +
                        "      <TransID></TransID>\n" +
                        "      <UserIP></UserIP>\n" +
                        "      <UDF1>"+UDF1+"</UDF1>\n" +
                        "      <UDF2></UDF2>\n" +
                        "      <UDF3>" + UDF3 + "</UDF3>\n" +
                        "      <OrderRef>" + trackingID + "</OrderRef>\n" +
                        "    </Do3DSTransaction>\n" +
                        "  </soap:Body>\n" +
                        "</soap:Envelope>";

                transactionLogger.error("Sale request--for--" + trackingID + "--" + requestlog);

                String response = GlobalGateUtils.doPostHTTPSURLConnection(rb.getString("service_url"), request);

                transactionLogger.error("Sale response--for--" + trackingID + "--" + response);

                if (functions.isValueNull(response))
                {
                    String _3Ddata = "";
                    String result="";
                    String _3DTicket="";
                    String status="";
                    String remark="";
                    HashMap map = (HashMap) GlobalGateUtils.readGlobalGate3DRedirectionXMLResponse(response);
                    if (map != null || map.size() != 0)
                    {
                        _3Ddata = (String) map.get("Do3DSTransactionResult");
                        transactionLogger.error("Do3DSTransactionResult --" + _3Ddata);
                        String[] split = _3Ddata.split(Pattern.quote("||"));
                        result= split[0];
                        _3DTicket= split[1];

                        transactionLogger.debug("result----"+result+"-----3dTicket---"+_3DTicket);

                        if("ENROLLED".equalsIgnoreCase(result)){

                            if("Y".equals(reject3DCard))
                            {
                                transactionLogger.error("rejecting 3d card as per configuration ");
                                commResponseVO.setStatus("failed");
                                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                                commResponseVO.setTransactionId(_3DTicket);

                                commResponseVO.setDescription("3D Enrolled Card");
                                commResponseVO.setRemark("3D Enrolled Card");
                            }else {
                                commResponseVO.setStatus("pending3DConfirmation");
                                commResponseVO.setUrlFor3DRedirect(rb.getString("3D_RedirectUrl") + _3DTicket);
                                commResponseVO.setTransactionId(_3DTicket);
                                //commResponseVO.setRemark("Pending 3D Confirmation");
                                commResponseVO.setRemark(result);
                                String isThreadAllowed=rb.getString("THREAD_CALL");
                                if("Y".equalsIgnoreCase(isThreadAllowed))
                                    new AsyncGlobalGateQueryService(trackingID,accountId,_3DTicket,"Y",CALL_EXECUTE_AFTER,CALL_EXECUTE_INTERVAL,MAX_EXECUTE_SEC);
                            }

                        }
                        else if ("Only3D".equalsIgnoreCase(attemptThreeD))
                        {

                            transactionLogger.error("Only 3D Card Required");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            commResponseVO.setDescription("Only 3D Card Required");
                            commResponseVO.setRemark("Only 3D Card Required");

                        }else if("CAPTURED".equalsIgnoreCase(result) || "APPROVED".equalsIgnoreCase(result))
                        {
                            remark=result;
                            status = "success";
                           // commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(remark);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(remark);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                        }else
                        {
                            remark=result;
                            status = "fail";
                            commResponseVO.setDescription(remark);
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(remark);
                            commResponseVO.setTmpl_Amount(tmpl_amount);
                            commResponseVO.setTmpl_Currency(tmpl_currency);
                        }

                        System.out.println("Globalgate transactionId 1---"+commResponseVO.getTransactionId());
                        if (functions.isValueNull(split[1]))
                        {
                            System.out.println("transactionId-----"+split[1]);
                            commResponseVO.setTransactionId(split[1]);
                        }
                        if (split.length > 2)
                        {
                            commResponseVO.setErrorCode(split[2]);
                        }

                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        java.util.Date date = new java.util.Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));

                        System.out.println("globalgate transactionId---"+commResponseVO.getTransactionId());
                    }

                }
            }
            else
            {
                ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
                serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
                assertNotNull("binding is null", serviceSoap12Stub);
                serviceSoap12Stub.setTimeout(60000);
                transactionLogger.error("------ sale request------" + MerchID + "--" + Pass + "--" + TrType + "--" + functions.maskingPan(CardNum) + "--" +functions.maskingNumber( CVV2) + "--" + functions.maskingNumber(ExpDay) + "--" +functions.maskingNumber( ExpMonth) + "--" + functions.maskingNumber(ExpYear) + "--" + CardHName + "--" + Amount + "--" + currencyCode + "--" + Addr + "--" + PostCode + "--" + TransID + "--" + UserIP + "--" + UDF1 + "--" + UDF2 + "--" + UDF3);
                String responseDocument = serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);
                log.error("------ sale response------" + responseDocument);
                transactionLogger.error("------ sale Non-3D response--for--" + trackingID + "--" + responseDocument);
                String[] split = responseDocument.split(Pattern.quote("||"));
                log.error("split0----" + split[0]);
                log.error("split1----" + split[1]);

                String status="";

                if(responseDocument!=null)
                {
                    if(functions.isValueNull(split[0]))
                    {
                        if(split[0].equalsIgnoreCase("CAPTURED"))
                        {
                            status="success";
                           // commResponseVO.setDescription("Transaction successful");
                            commResponseVO.setDescription(split[0]);

                        }
                        else
                        {
                            status="fail";
                           // commResponseVO.setDescription("Transaction Failed");
                            commResponseVO.setDescription(split[0]);

                        }
                    }
                    else
                    {
                        status="fail";
                        commResponseVO.setDescription("Transaction Failed");
                    }
                }
                if(functions.isValueNull(split[1]))
                {
                    commResponseVO.setTransactionId(split[1]);
                }
                if(split.length>2)
                {
                    commResponseVO.setErrorCode(split[2]);
                }
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                java.util.Date date = new java.util.Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch (ServiceException e)
        {
            log.error("sale service Exception---for--"+trackingID+"---",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processSale()",null,"common","Service Exception While Service Transaction",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (AxisFault e){
            log.error("Capture AxisFault--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processCapture()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e){
            log.error("Capture RemoteException--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processCapture()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (Exception e)
        {
            log.error("Sale Exception--for--" + trackingID + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processSale()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.error("Entering into processRefund of GlobalGatePaymentGateway::::");
        transactionLogger.error("Entering into processRefund of GlobalGatePaymentGateway::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String language = commRequestVO.getAddressDetailsVO().getLanguage();
        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String Pass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String forcePayment = GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();

        String TrType="12";
        String CardNum = "";
        String CVV2 = "";
        String ExpDay="";
        String ExpMonth = "";
        String ExpYear = "";
        String CardHName = "";
        String Amount = transDetailsVO.getAmount();
        String Addr = "";
        String PostCode = "";
        String TransID=commTransactionDetailsVO.getPreviousTransactionId();
        String UserIP="";
        String UDF1="";
        String UDF2="";
        String UDF3="";
        String tmpl_amount="";
        String tmpl_currency="";

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        if ("FASTPAY".equalsIgnoreCase(forcePayment))
        {

            CommTransactionDetailsVO transactionDetailsVO = commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            String profileId = gatewayAccount.getFRAUD_FTP_USERNAME();
            String secretWord = gatewayAccount.getFRAUD_FTP_PATH();
            String orderId = commTransactionDetailsVO.getOrderId();
            boolean isTest = gatewayAccount.isTest();
            String testString = "";
            String URL = "";
            if (isTest)
            {
                testString = "<TEST />";
                URL = rb.getString("URL");

            }
            else
            {
                URL = rb.getString("URL");
            }
            String amount = transactionDetailsVO.getAmount();
            String setPreviousTransactionId = transactionDetailsVO.getPreviousTransactionId();
            String status = "";
            String remark = "";

            String redirectURLSuccess = rb.getString("GLOBALGATE_FRONTEND");


            try
            {
                String refund1 = "<Transaction hash=\"" + secretWord + "\">" +
                        "<ProfileID>" + profileId + "</ProfileID>" +
                        "<Value>" + amount + "</Value>" +
                        "<Curr>" + currencyCode + "</Curr>" +
                        "<Lang>" + language + "</Lang>" +
                        "<ORef>" + orderId + "</ORef>" +
                        "<PspID>" + setPreviousTransactionId + "</PspID>" +
                        "<ActionType>" + TrType + "</ActionType>" +
                        "<UDF1 />" +
                        "<UDF2 />" +
                        "<UDF3 />" +
                        "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                        testString +
                        "</Transaction>";

                transactionLogger.error("-----refund1 request--for--" + trackingID + "--" + refund1);
                String hash = GlobalGateUtils.getMD5HashVal(refund1);
                transactionLogger.error("-----refund1 hash-----" + hash);

                String refund1HashedRequest = "params=<Transaction hash=\"" + hash + "\">" +
                        "<ProfileID>" + profileId + "</ProfileID>" +
                        "<Value>" + amount + "</Value>" +
                        "<Curr>" + currencyCode + "</Curr>" +
                        "<Lang>" + language + "</Lang>" +
                        "<ORef>" + orderId + "</ORef>" +
                        "<PspID>" + setPreviousTransactionId + "</PspID>" +
                        "<ActionType>" + TrType + "</ActionType>" +
                        "<UDF1 />" +
                        "<UDF2 />" +
                        "<UDF3 />" +
                        "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                        testString +
                        "</Transaction>";

                transactionLogger.error("----- hashed refund1 request-----" + refund1HashedRequest);
                String refund1Response = GlobalGateUtils.doPostHTTPSURLConnection(URL, refund1HashedRequest);
                transactionLogger.error("-----refund1 response--for--" + trackingID + "--" + refund1Response);
                Map<String, String> stringStringMap = GlobalGateUtils.readGlobalgatePayoutResponse(refund1Response);
                transactionLogger.error("stringStringMap:::::" + stringStringMap);
                if (stringStringMap != null)
                {
                    if ("OK".equals(stringStringMap.get("Status")))
                    {
                        status = "success";
                        remark = "Refund Successful";
                    }
                    else
                    {
                        status = "failed";
                        if (stringStringMap.get("ErrorMsg") != null)
                        {
                            remark = stringStringMap.get("ErrorMsg");
                        }
                        else
                        {
                            remark = "Refund Failed";
                        }
                    }
                }
                else
                {
                    status = "failed";
                    remark = "Refund Failed";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(remark);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);

            }
            catch (Exception e)
            {
                transactionLogger.error("FastPay Refund Exception--for--" + trackingID + "--",e);
            }
            return commResponseVO;
        }
        else
        {
            try
            {
                ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
                serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
                assertNotNull("binding is null", serviceSoap12Stub);
                serviceSoap12Stub.setTimeout(60000);
                log.error("------ refund request--for--" + trackingID + "-->" + MerchID + "--" + Pass + "--" + TrType + "--" + functions.maskingPan(CardNum) + "--" + functions.maskingNumber(CVV2) + "--" + functions.maskingNumber(ExpDay) + "--" + functions.maskingNumber(ExpMonth) + "--" + functions.maskingNumber(ExpYear) + "--" + CardHName + "--" + Amount + "--" + currencyCode + "--" + Addr + "--" + PostCode + "--" + TransID + "--" + UserIP + "--" + UDF1 + "--" + UDF2 + "--" + UDF3);
                String responseDocument = serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);
                log.error("------ refund response------" + responseDocument);
                transactionLogger.error("------ refund response--for--" + trackingID + "--" + responseDocument);
                String[] split = responseDocument.split(Pattern.quote("||"));
                log.error("split0----" + split[0]);
                log.error("split1----" + split[1]);

                String status = "";
                if (responseDocument != null)
                {
                    if (functions.isValueNull(split[0]))
                    {
                        if (split[0].equalsIgnoreCase("CAPTURED"))
                        {
                            status = "success";
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setRemark(split[0]);
                            commResponseVO.setDescription(split[0]);

                            transactionLogger.error("remarks===========>"+commResponseVO.getRemark());

                        }
                        else
                        {
                            status = "fail";
                            //commResponseVO.setDescription("Transaction Failed");
                            commResponseVO.setDescription(split[0]);

                        }
                    }
                    else{
                        status = "fail";
                        commResponseVO.setDescription("Transaction Failed");
                    }
                }
                if (functions.isValueNull(split[1]))
                {
                    commResponseVO.setTransactionId(split[1]);
                    commResponseVO.setResponseHashInfo(split[1]);
                }
                if (split.length > 2)
                {
                    commResponseVO.setErrorCode(split[2]);
                }
               // commResponseVO.setRemark(split[0]);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType("refund");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                java.util.Date date = new java.util.Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
            catch (ServiceException e)
            {
                log.error("Refund ServiceException--for--" + trackingID + "--", e);
                PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processRefund()", null, "common", "Service Exception while refunding transaction ", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());

            }
            catch (AxisFault e)
            {
                log.error("Refund AxisFault--for--" + trackingID + "--", e);
                PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());

            }
            catch (RemoteException e)
            {
                log.error("Refund RemoteException--for--" + trackingID + "--", e);
                PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processRefund()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());

            }
            return commResponseVO;
        }

    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering into processVoid of GlobalGatePaymentGateway:::::");
        transactionLogger.debug("Entering into processVoid of GlobalGatePaymentGateway:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String Pass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String TrType="9";
        String CardNum = "";
        String CVV2 = "";
        String ExpDay="";
        String ExpMonth = "";
        String ExpYear = "";
        String CardHName = "";
        String Amount = transDetailsVO.getAmount();
        String Addr = "";
        String PostCode = "";
        String TransID=commTransactionDetailsVO.getPreviousTransactionId();
        String UserIP="";
        String UDF1="";
        String UDF2="";
        String UDF3="";
        String tmpl_amount="";
        String tmpl_currency="";

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }

        try
        {
            ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
            serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
            assertNotNull("binding is null", serviceSoap12Stub);
            serviceSoap12Stub.setTimeout(60000);
            transactionLogger.error("------ cancel Request--for--" + trackingID + "--->"+MerchID+"--"+Pass+"--"+TrType+"--"+ functions.maskingPan(CardNum) + "--" + functions.maskingNumber(CVV2) + "--" + functions.maskingNumber(ExpDay) + "--" + functions.maskingNumber(ExpMonth) + "--" + functions.maskingNumber(ExpYear)+"--"+CardHName+"--"+Amount+"--"+currencyCode+"--"+Addr+"--"+PostCode+"--"+TransID+"--"+UserIP+"--"+UDF1+"--"+UDF2+"--"+UDF3);
            String responseDocument= serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);
            log.error("------ cancel response------"+responseDocument);
            transactionLogger.error("------ cancel response--for--" + trackingID + "--" + responseDocument);
            String[] split = responseDocument.split(Pattern.quote("||"));
            log.error("split0----"+split[0]);
            log.error("split1----"+split[1]);

            String status="";
            if(responseDocument != null){
                if (functions.isValueNull(split[0])){
                    if (split[0].equalsIgnoreCase("VOIDED")){
                        status = "success";
                       // commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setDescription(split[0]);

                    }
                    else{
                        status = "fail";
                        commResponseVO.setDescription(split[0]);
                        //commResponseVO.setDescription("Transaction Failed");
                    }
                }
                else{
                    status = "fail";
                    commResponseVO.setDescription("Transaction Failed");
                   // commResponseVO.setDescription(split[0]);

                }
            }
            if (functions.isValueNull(split[1])){
                commResponseVO.setTransactionId(split[1]);
                commResponseVO.setResponseHashInfo(split[1]);

            }
            if(split.length > 2)
            {
                commResponseVO.setErrorCode(split[2]);
            }
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setStatus(status);
            commResponseVO.setRemark(split[0]);

            commResponseVO.setTransactionType("void");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        catch (ServiceException e){
            log.error("Cancel ServiceException--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processVoid()",null,"common","Service Exception while voiding transaction ",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());

        }
        catch (AxisFault e){
            log.error("Cancel AxisFault--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processVoid()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT,null,e.getMessage(),e.getCause());

        }
        catch (RemoteException e){
            log.error("Cancel RemoteException--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processVoid()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering into processCapture of GlobalGatePaymentGateway:::::");
        transactionLogger.debug("Entering into processCapture of GlobalGatePaymentGateway:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        Functions functions = new Functions();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
        Integer currencyCode = Integer.parseInt(currency);

        String MerchID = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String Pass = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String TrType="5";
        String CardNum = "";
        String CVV2 = "";
        String ExpDay="";
        String ExpMonth = "";
        String ExpYear = "";
        String CardHName = addressDetailsVO.getFirstname()+" "+addressDetailsVO.getLastname();
        String Amount = transDetailsVO.getAmount();
        String Addr = "";
        String PostCode = "";
        String TransID = commTransactionDetailsVO.getPreviousTransactionId();
        String UserIP="";
        String UDF1="";
        String UDF2="";
        String UDF3="";
        String tmpl_amount="";
        String tmpl_currency="";

        if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount=commAddressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency=commAddressDetailsVO.getTmpl_currency();
        }
        try
        {
            ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
            serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
            assertNotNull("binding is null", serviceSoap12Stub);
            serviceSoap12Stub.setTimeout(60000);
            transactionLogger.error("------ capture request--for--" + trackingID + "-->"+MerchID+"--"+Pass+"--"+TrType+"--"+functions.maskingPan(CardNum)+"--"+functions.maskingNumber(CVV2)+"--"+functions.maskingNumber(ExpDay)+"--"+functions.maskingNumber(ExpMonth)+"--"+functions.maskingNumber(ExpYear)+"--"+CardHName+"--"+Amount+"--"+currencyCode+"--"+Addr+"--"+PostCode+"--"+TransID+"--"+UserIP+"--"+UDF1+"--"+UDF2+"--"+UDF3);
            String responseDocument= serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);
            log.error("------ capture response------"+responseDocument);
            transactionLogger.error("------ capture response--for--" + trackingID + "--" + responseDocument);
            String[] split = responseDocument.split(Pattern.quote("||"));
            log.error("split0----"+split[0]);
            log.error("split1----"+split[1]);

            String status="";
            if(responseDocument != null){
                if (functions.isValueNull(split[0])){
                    if (split[0].equalsIgnoreCase("CAPTURED")){
                        status = "success";
                        //commResponseVO.setDescription("Transaction Successful");
                        commResponseVO.setDescription(split[0]);
                        commResponseVO.setRemark(split[0]);
                        transactionLogger.error("remarks===========>"+commResponseVO.getRemark());


                    }
                    else{
                        status = "fail";
                       // commResponseVO.setDescription("Transaction Failed");
                        commResponseVO.setDescription(split[0]);

                    }
                }
                else{
                    status = "fail";
                    //commResponseVO.setDescription(split[0]);
                    commResponseVO.setDescription("Transaction Failed");
                }
            }
            if (functions.isValueNull(split[1])){
                commResponseVO.setTransactionId(split[1]);
                commResponseVO.setResponseHashInfo(split[1]);
            }
            if(split.length>2){
                commResponseVO.setErrorCode(split[2]);
            }
          //  commResponseVO.setRemark(split[0]);
            commResponseVO.setTmpl_Currency(tmpl_currency);
            commResponseVO.setTmpl_Amount(tmpl_amount);
            commResponseVO.setStatus(status);
            commResponseVO.setTransactionType("capture");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
        }
        catch (ServiceException e){
            log.error("Capture ServiceException--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processCapture()",null,"common","Service Exception while voiding transaction ",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (AxisFault e){
            log.error("Capture AxisFault--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(),"processCapture()",null,"common","Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT,null,e.getMessage(),e.getCause());
        }
        catch (RemoteException e){
            log.error("Capture RemoteException--for--" + trackingID + "--",e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processCapture()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }


    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        log.debug("Entering into processInquiry of GlobalGatePaymentGateway:::::");
        transactionLogger.debug("Entering into processInquiry of GlobalGatePaymentGateway:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String merchID = gatewayAccount.getMerchantId(); //GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchPass = gatewayAccount.getPassword(); //GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId = commTransactionDetailsVO.getOrderId();
        String _3DTicket="";

        try
        {
            String _3DInquiryRequest="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "  <soap12:Body>" +
                    "    <getTransactionStatus xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>"+merchID+"</MerchID>" +
                    "      <MerchPass>"+merchPass+"</MerchPass>" +
                    "      <ORef>"+orderId+"</ORef>" +
                    "    </getTransactionStatus>" +
                    "  </soap12:Body>" +
                    "</soap12:Envelope>";


            transactionLogger.error("Inquiry Request--for--" + commTransactionDetailsVO.getOrderId() + "--" + _3DInquiryRequest);

            String inquiryResponse = GlobalGateUtils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), _3DInquiryRequest);
            transactionLogger.error("Inquiry Response--for--" + commTransactionDetailsVO.getOrderId() + "--"+inquiryResponse);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(inquiryResponse))
            {


                HashMap<String,String> map = (HashMap) GlobalGateUtils.readGlobalgateInquiryXMLResponse(inquiryResponse);
                if (map != null || map.size() != 0)
                {
                    result=map.get("Result");
                    transactionLogger.error("result--->"+result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED"))
                        {
                            transactionLogger.error("Inside result--->"+result);
                            status = "success";
                            commResponseVO.setStatus(status);
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(result);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }else if(result.equals("ENROLLED"))
                        {
                            transactionLogger.error("Inside ENROLLED--->"+result);

                            status = "pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(result);
                        }else
                        {
                            status = "fail";
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescription(result);
                        }
                    }
                    else
                    {
                        status = "fail";
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark(result);
                        commResponseVO.setDescription("Transaction Failed");
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    java.util.Date date = new java.util.Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setMerchantId(merchID);
                    commResponseVO.setMerchantOrderId(map.get("ORef"));
                    commResponseVO.setTransactionId(map.get("pspid"));
                    commResponseVO.setAuthCode(map.get("AuthCode"));
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(map.get("Value"));
                    commResponseVO.setCurrency(map.get("Currency"));
                    commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(map.get("Result"));
                }else
                {
                    status = "pending";
                    commResponseVO.setStatus(status);
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setTransactionId(paymentid);
                }
            }else{
                status = "pending";
                commResponseVO.setStatus(status);
                commResponseVO.setRemark("Transaction Pending");
                commResponseVO.setDescription("Transaction Pending");
                commResponseVO.setTransactionId(paymentid);
            }
          //  commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }catch (Exception e)
        {
            log.error("Inquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processQuery()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("GlobalgatePaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    public GenericResponseVO processPayout(String trackingID, GenericRequestVO genericRequestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("-----Inside processPayout------");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)genericRequestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(commMerchantVO.getAccountId());
        String profileId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String MerchID = gatewayAccount.getMerchantId();
        String Pass = gatewayAccount.getFRAUD_FTP_PASSWORD();
        boolean isTest = gatewayAccount.isTest();
        String TrType="13";
        String CardNum = "";
        String CVV2 = "";
        String ExpDay="";
        String ExpMonth = "";
        String ExpYear = "";
        String CardHName = "";
        String Amount = transactionDetailsVO.getAmount();
        String Addr = "";
        String PostCode = "";
        String TransID=transactionDetailsVO.getPaymentId();
        String UserIP="";
        String UDF1="";
        String UDF2="";
        String UDF3="";

        String testString = "";
        String URL="";
        if (isTest){
            testString = "<TEST />";
            URL=rb.getString("URL");

        }else{
            URL=rb.getString("URL");
        }

        String  phoneNo=commAddressDetailsVO.getPhone();
        String  amount=transactionDetailsVO.getAmount();
        String name = commRequestVO.getAddressDetailsVO().getFirstname() + "" + commRequestVO.getAddressDetailsVO().getLastname();
        String setPreviousTransactionId=transactionDetailsVO.getPreviousTransactionId();
        String  currency = CurrencyCodeISO4217.getNumericCurrencyCode(transactionDetailsVO.getCurrency());
        String forcePayment = GatewayAccountService.getGatewayAccount(accountId).getCHARGEBACK_FTP_PATH();
        Integer currencyCode = Integer.parseInt(currency);

        String status="" ;
        String remark="";
        String redirectURLSuccess = rb.getString("GLOBALGATE_FRONTEND");
        String language = commRequestVO.getAddressDetailsVO().getLanguage();

        try
        {

            if ("FASTPAY".equalsIgnoreCase(forcePayment))
            {
                String payoutRequest = "<Transaction hash=\"" + secretWord + "\">" +
                        "<ProfileID>" + profileId + "</ProfileID>" +
                        "<Value>" + amount + "</Value>" +
                        "<Curr>" + currencyCode + "</Curr>" +
                        "<Lang>" + language + "</Lang>" +
                        "<ORef>" + trackingID + "</ORef>" +
                        "<PspID>" + setPreviousTransactionId + "</PspID>" +
                        "<RegName>" + name + "</RegName>" +
                        "<AccountID>" + phoneNo + "</AccountID>" +
                        "<UDF1 />" +
                        "<UDF2 />" +
                        "<UDF3 />" +
                        "<ActionType>" + TrType + "</ActionType>" +
                        "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                        testString +
                        "</Transaction>";

                transactionLogger.error("-----payout request-----" + payoutRequest);
                String hash = GlobalGateUtils.getMD5HashVal(payoutRequest);
                transactionLogger.error("-----hash-----" + hash);

                String payoutHashedRequest = "params=<Transaction hash=\"" + hash + "\">" +
                        "<ProfileID>" + profileId + "</ProfileID>" +
                        "<Value>" + amount + "</Value>" +
                        "<Curr>" + currencyCode + "</Curr>" +
                        "<Lang>" + language + "</Lang>" +
                        "<ORef>" + trackingID + "</ORef>" +
                        "<PspID>" + setPreviousTransactionId + "</PspID>" +
                        "<RegName>" + name + "</RegName>" +
                        "<AccountID>" + phoneNo + "</AccountID>" +
                        "<UDF1 />" +
                        "<UDF2 />" +
                        "<UDF3 />" +
                        "<ActionType>" + TrType + "</ActionType>" +
                        "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                        testString +
                        "</Transaction>";

                transactionLogger.error("----- hashed payout request-----" + payoutHashedRequest);
                String payoutResponse = GlobalGateUtils.doPostHTTPSURLConnection(URL, payoutHashedRequest);
                transactionLogger.error("-----payout response-----" + payoutResponse);
                Map<String, String> stringStringMap = GlobalGateUtils.readGlobalgatePayoutResponse(payoutResponse);
                transactionLogger.error("stringStringMap:::::" + stringStringMap);
                if (stringStringMap != null)
                {
                    if ("OK".equals(stringStringMap.get("Status")))
                    {
                        status = "success";
                        remark = "Payout Successful";
                    }
                    else
                    {
                        status = "failed";
                        if (stringStringMap.get("ErrorMsg") != null)
                        {
                            remark = stringStringMap.get("ErrorMsg");
                        }
                        else
                        {
                            remark = "Payout Failed";
                        }
                    }
                }
                else
                {
                    status = "failed";
                    remark = "Payout Failed";
                }
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(remark);
            }
            else
            {
                ServiceSoap12Stub serviceSoap12Stub = new ServiceSoap12Stub();
                serviceSoap12Stub = (ServiceSoap12Stub) new ServiceLocator().getServiceSoap12();
                assertNotNull("binding is null", serviceSoap12Stub);
                serviceSoap12Stub.setTimeout(60000);
                log.error("------ payout response------" + MerchID + "--" + Pass + "--" + TrType + "--" + functions.maskingPan(CardNum) + "--" + functions.maskingNumber(CVV2) + "--" + functions.maskingNumber(ExpDay) + "--" + functions.maskingNumber(ExpMonth) + "--" + functions.maskingNumber(ExpYear) + "--" + CardHName + "--" + Amount + "--" + currencyCode + "--" + Addr + "--" + PostCode + "--" + TransID + "--" + UserIP + "--" + UDF1 + "--" + UDF2 + "--" + UDF3);
                String responseDocument = serviceSoap12Stub.doTransaction(MerchID, Pass, TrType, CardNum, CVV2, ExpDay, ExpMonth, ExpYear, CardHName, Amount, String.valueOf(currencyCode), Addr, PostCode, TransID, UserIP, UDF1, UDF2, UDF3);

                //log.error("------ payout response------" + responseDocument);
                //transactionLogger.error("------ payout response------" + responseDocument);
                String[] split = responseDocument.split(Pattern.quote("||"));
                log.error("split0----" + split[0]);
                log.error("split1----" + split[1]);

                status = "";
                if (responseDocument != null)
                {
                    if (functions.isValueNull(split[0]))
                    {
                        if (split[0].equalsIgnoreCase("CAPTURED"))
                        {
                            status = "success";
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(split[0]);
                        }
                        else
                        {
                            status = "fail";
                            commResponseVO.setDescription(split[0]);
                            //commResponseVO.setDescription("Transaction Failed");
                        }
                    }
                    else{
                        status = "fail";
                        commResponseVO.setDescription("Transaction Failed");
                    }
                }
                if (functions.isValueNull(split[1]))
                {
                    commResponseVO.setTransactionId(split[1]);
                }
                if (split.length > 2)
                {
                    commResponseVO.setErrorCode(split[2]);
                }

                commResponseVO.setStatus(status);
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                java.util.Date date = new java.util.Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch(ServiceException e){
            log.error("ServiceException---", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processPayout()", null, "common", "Service Exception while payout transaction ", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch(AxisFault e){
            log.error("AxisFault---", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processPayout()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.AXISFAULT, null, e.getMessage(), e.getCause());

        }
        catch(RemoteException e){
            log.error("RemoteException---", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processPayout()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());

        }
        catch (Exception e)
        {
            log.error("Exception---", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processPayout()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        return commResponseVO;
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error(":::::Entered into processAutoRedirect for Globalgate:::::");
        GlobalGateUtils globalGateUtils=new GlobalGateUtils();
        String html = "";
        html = globalGateUtils.getGlobalGateRequest(commonValidatorVO);

        return html;
    }

    @Override
    public GenericResponseVO processPayoutInquiry(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZGenericConstraintViolationException
    {
        log.debug("Entering into processPayoutInquiry of GlobalGatePaymentGateway:::::");
        transactionLogger.debug("Entering into processPayoutInquiry of GlobalGatePaymentGateway:::::");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Functions functions = new Functions();
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO       = commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String merchID = gatewayAccount.getMerchantId(); //GatewayAccountService.getGatewayAccount(accountId).getMerchantId();
        String merchPass = gatewayAccount.getPassword(); //GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String orderId = commTransactionDetailsVO.getOrderId();
        String _3DTicket="";

        try
        {
            String _3DInquiryRequest="<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "  <soap12:Body>" +
                    "    <getTransactionStatus xmlns=\"https://www.apsp.biz/\">" +
                    "      <MerchID>"+merchID+"</MerchID>" +
                    "      <MerchPass>"+merchPass+"</MerchPass>" +
                    "      <ORef>"+orderId+"</ORef>" +
                    "    </getTransactionStatus>" +
                    "  </soap12:Body>" +
                    "</soap12:Envelope>";


            transactionLogger.error("PayoutInquiry Request--for--" + commTransactionDetailsVO.getOrderId() + "--" + _3DInquiryRequest);

            String inquiryResponse = GlobalGateUtils.doPostHTTPSURLConnection(rb.getString("inquiry_url"), _3DInquiryRequest);
            transactionLogger.error("PayoutInquiry Response--for--" + commTransactionDetailsVO.getOrderId() + "--"+inquiryResponse);

            String data = "";
            String result = "";
            String paymentid = "";
            String status = "";
            String remark = "";
            if (functions.isValueNull(inquiryResponse))
            {


                HashMap<String,String> map = (HashMap) GlobalGateUtils.readGlobalgateInquiryXMLResponse(inquiryResponse);
                if (map != null || map.size() != 0)
                {
                    result=map.get("Result");
                    transactionLogger.error("result--->"+result);
                    if (functions.isValueNull(result))
                    {
                        if (result.equals("OK") || result.equals("CAPTURED") || result.equals("APPROVED"))
                        {
                            status = "success";
                            commResponseVO.setStatus(status);
                            //commResponseVO.setDescription("Transaction Successful");
                            commResponseVO.setDescription(result);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }else if(result.equals("ENROLLED"))
                        {
                            transactionLogger.error("Inside ENROLLED--->"+result);

                            status = "pending";
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(result);
                        }else
                        {
                            status = "fail";
                            commResponseVO.setStatus(status);
                            commResponseVO.setRemark(result);
                            commResponseVO.setDescription(result);
                        }
                    }
                    else
                    {
                        status = "fail";
                        commResponseVO.setStatus(status);
                        commResponseVO.setRemark(result);
                        commResponseVO.setDescription("Transaction Failed");
                    }
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    java.util.Date date = new java.util.Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    commResponseVO.setMerchantId(merchID);
                    commResponseVO.setMerchantOrderId(map.get("ORef"));
                    commResponseVO.setTransactionId(map.get("pspid"));
                    commResponseVO.setAuthCode(map.get("AuthCode"));
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(map.get("Value"));
                    commResponseVO.setCurrency(map.get("Currency"));
                    commResponseVO.setBankTransactionDate(commResponseVO.getResponseTime());
                    commResponseVO.setDescription(commResponseVO.getDescription());
                    commResponseVO.setRemark(map.get("Result"));
                }else
                {
                    status = "pending";
                    commResponseVO.setStatus(status);
                    commResponseVO.setRemark("Transaction Pending");
                    commResponseVO.setDescription("Transaction Pending");
                    commResponseVO.setTransactionId(paymentid);
                }
            }else{
                status = "pending";
                commResponseVO.setStatus(status);
                commResponseVO.setRemark("Transaction Pending");
                commResponseVO.setDescription("Transaction Pending");
                commResponseVO.setTransactionId(paymentid);
            }
            //  commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            java.util.Date date = new java.util.Date();
            commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
        }catch (Exception e)
        {
            log.error("PayoutInquiry Exception--for--" + commTransactionDetailsVO.getOrderId() + "--", e);
            PZExceptionHandler.raiseTechnicalViolationException(GlobalGatePaymentGateway.class.getName(), "processQuery()", null, "common", "Remote Exception while refunding transaction via Globalgate", PZTechnicalExceptionEnum.PARSE_CONFIG_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }


}




