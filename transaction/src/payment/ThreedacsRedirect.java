package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.ReserveField2VO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGateway;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentGatewayUtils;
import com.payment.CashflowsCaibo.CashFlowsCaiboPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.*;
import com.payment.endeavourmpi.EndeavourMPIV2Gateway;
import com.payment.endeavourmpi.EnrollmentRequestVO;
import com.payment.endeavourmpi.EnrollmentResponseVO;
import com.payment.endeavourmpi.ParesDecodeResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.icard.ICardPaymentGateway;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.paynetics.core.PayneticsPaymentProcess;
import com.payment.paynetics.core.PayneticsRequestVO;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Admin on 10/10/2020.
 */
public class ThreedacsRedirect extends HttpServlet
{
    TransactionLogger transactionLogger=new TransactionLogger(ThreedacsRedirect.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        HttpSession session=request.getSession();
        PrintWriter writer=response.getWriter();
        Functions functions=new Functions();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();
        GenericDeviceDetailsVO deviceDetailsVO=new GenericDeviceDetailsVO();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        TransactionManager transactionManager=new TransactionManager();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        Comm3DRequestVO commRequestVO=new Comm3DRequestVO();
        TransactionUtility transactionUtility=new TransactionUtility();
        Comm3DResponseVO transRespDetails=null;
        PaymentManager paymentManager=new PaymentManager();
        ParesDecodeResponseVO paresDecodeResponseVO=new ParesDecodeResponseVO();
        String trackingId=request.getParameter("trackingId");
        String MD=request.getParameter("MD");
        String threeDSServerTransID=request.getParameter("threeDSServerTransID");
        String browserTimezoneOffset=request.getParameter("browserTimezoneOffset");
        String browserScreenHeight=request.getParameter("browserScreenHeight");
        String browserScreenWidth=request.getParameter("browserScreenWidth");
        String browserLanguage=request.getParameter("browserLanguage");
        String browserColorDepth=request.getParameter("browserColorDepth");
        String browserJavaEnabled=request.getParameter("browserJavaEnabled");
        String userAgent=request.getHeader("User-Agent");
        String acceptHeader=request.getHeader("Accept");
        String ipAddress=Functions.getIpAddress(request);
        String payModeId = "";
        String cardTypeId = "";
        String currency="";
        String amount="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";
        String firstname="";
        String lastname="";
        String accountId="";
        String fromType="";
        String toid="";
        String description="";
        String mpiMid="";
        String cvv="";
        String updatestatus="";
        String transType="";
        String billingDesc="";
        String transactionId="";
        String eci="";
        String message="";
        String autoRedirect="";
        String logoName="";
        String partnerName="";
        String isService="";
        String tmpl_Amount="";
        String tmpl_Currency="";
        String orderDescription = "";
        String redirectUrl = "";
        String customerId="";
        String notificationUrl="";
        String terminalId="";
        String email="";
        String country="";
        String city="";
        String state="";
        String street="";
        String zip="";
        String paymentId="";
        String errorName="";
        String threeDStatus="";
        String status="";
        String avr="";
        String result="";
        String dbStatus="";
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection con=null;
        StringBuffer dbBuffer=new StringBuffer();
        ArrayList<String> colorDepth= new ArrayList<>();
        colorDepth.add("1");
        colorDepth.add("4");
        colorDepth.add("8");
        colorDepth.add("15");
        colorDepth.add("16");
        colorDepth.add("24");
        colorDepth.add("32");
        colorDepth.add("48");
        if(!colorDepth.contains(browserColorDepth)){
            browserColorDepth="32";
        }

        Enumeration enumeration  = request.getParameterNames();
        BufferedReader br        = request.getReader();
        StringBuffer responseMsg = new StringBuffer();

        while (enumeration.hasMoreElements())
        {
            String key      = (String) enumeration.nextElement();
            String value    = request.getParameter(key);
            transactionLogger.error("---Key---" + key + "---Value---" + value);
        }

        String str1;
        while ((str1=br.readLine())!=null)
        {
            responseMsg.append(str1);
        }
        transactionLogger.error("-----ThreedacsRedirect  JSON-----" + responseMsg);

        try
        {
            if (functions.isValueNull(trackingId))
            {
                TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
                {
                    if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                        ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    currency    = transactionDetailsVO.getCurrency();
                    amount      = transactionDetailsVO.getAmount();
                    firstname   = transactionDetailsVO.getFirstName();
                    lastname    = transactionDetailsVO.getLastName();
                    accountId   = transactionDetailsVO.getAccountId();
                    fromType    = transactionDetailsVO.getFromtype();
                    toid        = transactionDetailsVO.getToid();
                    description = transactionDetailsVO.getDescription();
                    if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                    {
                        expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                        String temp[] = expDate.split("/");

                        if (functions.isValueNull(temp[0]))
                        {
                            expMonth = temp[0];
                        }
                        if (functions.isValueNull(temp[1]))
                        {
                            expYear = temp[1];
                        }
                    }
                    payModeId       = transactionDetailsVO.getPaymodeId();
                    cardTypeId      = transactionDetailsVO.getCardTypeId();
                    redirectUrl     = transactionDetailsVO.getRedirectURL();
                    customerId      = transactionDetailsVO.getCustomerId();
                    notificationUrl = transactionDetailsVO.getNotificationUrl();
                    terminalId      = transactionDetailsVO.getTerminalId();
                    email           = transactionDetailsVO.getEmailaddr();
                    tmpl_Amount     = transactionDetailsVO.getTemplateamount();
                    tmpl_Currency   = transactionDetailsVO.getTemplatecurrency();
                    country         = transactionDetailsVO.getCountry();
                    city            = transactionDetailsVO.getCity();
                    state           = transactionDetailsVO.getState();
                    street          = transactionDetailsVO.getStreet();
                    zip             = transactionDetailsVO.getZip();
                    paymentId       = transactionDetailsVO.getPaymentId();
                    dbStatus        = transactionDetailsVO.getStatus();
                    eci             = transactionDetailsVO.getEci();
                    merchantDetailsVO   = merchantDAO.getMemberDetails(toid);
                    if (merchantDetailsVO != null)
                    {
                        autoRedirect    = merchantDetailsVO.getAutoRedirect();
                        logoName        = merchantDetailsVO.getLogoName();
                        partnerName     = merchantDetailsVO.getPartnerName();
                        isService       = merchantDetailsVO.getService();
                    }
                    if ("Y".equalsIgnoreCase(isService) && (!functions.isValueNull(transactionDetailsVO.getTransactionType()) || "DB".equalsIgnoreCase(transactionDetailsVO.getTransactionType())))
                        transType = "sale";
                    else
                        transType = "auth";

                    if (functions.isValueNull(MD))
                    {
                        cvv = PzEncryptor.decryptCVV(MD);
                    }

                    auditTrailVO.setActionExecutorId(toid);
                    auditTrailVO.setActionExecutorName("ThreeDs 2 ACS page");
                    commonValidatorVO.setTrackingid(trackingId);
                    commonValidatorVO.setAccountId(accountId);
                    genericTransDetailsVO.setOrderId(description);
                    genericTransDetailsVO.setOrderDesc(orderDescription);
                    genericTransDetailsVO.setAmount(amount);
                    genericTransDetailsVO.setCurrency(currency);
                    genericTransDetailsVO.setRedirectUrl(redirectUrl);
                    genericTransDetailsVO.setNotificationUrl(notificationUrl);
                    genericTransDetailsVO.setPaymentid(paymentId);

                    commonValidatorVO.setLogoName(logoName);
                    commonValidatorVO.setPartnerName(partnerName);
                    commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                    commonValidatorVO.setPaymentType(payModeId);
                    commonValidatorVO.setCardType(cardTypeId);
                    commonValidatorVO.setCustomerId(customerId);
                    commonValidatorVO.setErrorName(errorName);
                    commonValidatorVO.setEci(eci);
                    addressDetailsVO.setTmpl_amount(tmpl_Amount);
                    addressDetailsVO.setTmpl_currency(tmpl_Currency);
                    cardDetailsVO.setCardNum(ccnum);
                    cardDetailsVO.setExpMonth(expMonth);
                    cardDetailsVO.setExpYear(expYear);
                    cardDetailsVO.setcVV(cvv);

                    if (functions.isValueNull(email))
                        addressDetailsVO.setEmail(email);
                    if (functions.isValueNull(firstname))
                        addressDetailsVO.setFirstname(firstname);

                    if (functions.isValueNull(lastname))
                        addressDetailsVO.setLastname(lastname);
                    addressDetailsVO.setCountry(country);
                    addressDetailsVO.setCity(city);
                    addressDetailsVO.setState(state);
                    addressDetailsVO.setStreet(street);
                    addressDetailsVO.setZipCode(zip);
                    addressDetailsVO.setCardHolderIpAddress(ipAddress);

                    deviceDetailsVO.setBrowserLanguage(browserLanguage);
                    deviceDetailsVO.setBrowserTimezoneOffset(browserTimezoneOffset);
                    deviceDetailsVO.setBrowserColorDepth(browserColorDepth);
                    deviceDetailsVO.setBrowserScreenHeight(browserScreenHeight);
                    deviceDetailsVO.setBrowserScreenWidth(browserScreenWidth);
                    deviceDetailsVO.setBrowserJavaEnabled(browserJavaEnabled);
                    deviceDetailsVO.setUser_Agent(userAgent);
                    deviceDetailsVO.setAcceptHeader(acceptHeader);


                    commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                    commonValidatorVO.setCardDetailsVO(cardDetailsVO);
                    commonValidatorVO.setTerminalId(terminalId);
                    commonValidatorVO.setDeviceDetailsVO(deviceDetailsVO);
                    if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(dbStatus))
                    {
                        int k = paymentManager.mark3DTransaction(trackingId);
                        transactionLogger.error("paymentManager.mark3DTransaction().k=" + k);
                        if (k == 1)
                        {
                            commRequestVO = (Comm3DRequestVO) getCommonRequestVO(commonValidatorVO);

                            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                            AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accountId);
                            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));
                            if (PayneticsGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) || ICardPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                            {
                                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();

                                enrollmentRequestVO.setMid(mpiMid);
                                enrollmentRequestVO.setName(firstname + " " + lastname);
                                enrollmentRequestVO.setPan(ccnum);
                                enrollmentRequestVO.setExpiry(getCardExpiry(expMonth, expYear));
                                enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(currency));
                                if (currency.equalsIgnoreCase("JPY"))
                                    enrollmentRequestVO.setAmount(getJPYAmount(amount));
                                else
                                    enrollmentRequestVO.setAmount(getCentAmount(amount));

                                enrollmentRequestVO.setDesc(description);
                                if (functions.isValueNull(userAgent))
                                    enrollmentRequestVO.setUseragent(userAgent);
                                else
                                    enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                                enrollmentRequestVO.setAccept("en-us");
                                enrollmentRequestVO.setTrackid(trackingId);
                                enrollmentRequestVO.setAcceptHeader(acceptHeader);
                                enrollmentRequestVO.setHostUrl(merchantDetailsVO.getHostUrl());
                                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                                if (functions.isValueNull(merchantDetailsVO.getCountry()))
                                    enrollmentRequestVO.setMerchantCountry(CountryCodeISO3166.getNumericCountryCode(merchantDetailsVO.getCountry()));
                                enrollmentRequestVO.setBrowserLanguage(browserLanguage);
                                enrollmentRequestVO.setBrowserTimezoneOffset(browserTimezoneOffset);
                                enrollmentRequestVO.setBrowserColorDepth(browserColorDepth);
                                enrollmentRequestVO.setBrowserScreenHeight(browserScreenHeight);
                                enrollmentRequestVO.setBrowserScreenWidth(browserScreenWidth);
                                enrollmentRequestVO.setBrowserJavaEnabled(browserJavaEnabled);
                                enrollmentRequestVO.setBrowserIp(ipAddress);
                                enrollmentRequestVO.setThreeDSServerTransID(threeDSServerTransID);
                                paymentProcess.setEnrollmentRequestVOExtention(enrollmentRequestVO, transactionDetailsVO);

                                EndeavourMPIV2Gateway endeavourMPIV2Gateway = new EndeavourMPIV2Gateway();
                                EnrollmentResponseVO enrollmentResponseVO = endeavourMPIV2Gateway.processAuthentication(enrollmentRequestVO);
                                if ("Enrolled".equalsIgnoreCase(enrollmentResponseVO.getResult()) && ("C".equalsIgnoreCase(enrollmentResponseVO.getAvr())))
                                {
                                    Comm3DResponseVO comm3DResponseVO = new Comm3DResponseVO();
                                    comm3DResponseVO.setUrlFor3DRedirect(enrollmentResponseVO.getAcsUrl());
                                    comm3DResponseVO.setCreq(enrollmentResponseVO.getCreq());
                                    comm3DResponseVO.setThreeDSSessionData(enrollmentResponseVO.getThreeDSSessionData());
                                    comm3DResponseVO.setPaReq(enrollmentResponseVO.getPAReq());
                                    comm3DResponseVO.setMd(enrollmentResponseVO.getMD());
                                    comm3DResponseVO.setTerURL(enrollmentRequestVO.getTermUrl() + trackingId);
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D2_CONFIRMATION_STARTED, ActionEntry.STATUS_3D2_CONFIRMATION, null, null, auditTrailVO, null);
                                    String form = paymentProcess.get3DConfirmationForm(trackingId, "", comm3DResponseVO);
                                    writer.println(form);
                                    return;
                                }
                                else
                                {
                                    avr = enrollmentResponseVO.getAvr();
                                    result = enrollmentResponseVO.getResult();
                                    paresDecodeResponseVO.set_20BytesBinaryXIDBytes(enrollmentResponseVO.get_20BytesBinaryXIDBytes());
                                    paresDecodeResponseVO.set_20BytesBinaryXID(enrollmentResponseVO.get_20BytesBinaryXID());
                                    paresDecodeResponseVO.setXid(enrollmentResponseVO.getXID());
                                    paresDecodeResponseVO.setCavv(enrollmentResponseVO.getCAVV());
                                    paresDecodeResponseVO.set_20BytesBinaryCAVV(enrollmentResponseVO.get_20BytesBinaryCAVV());
                                    paresDecodeResponseVO.set_20BytesBinaryCAVVBytes(enrollmentResponseVO.get_20BytesBinaryCAVVBytes());
                                    paresDecodeResponseVO.setDsTransId(enrollmentResponseVO.getDsTransId());
                                    paresDecodeResponseVO.setEci(enrollmentResponseVO.getEci());
                                    paresDecodeResponseVO.setStatus(enrollmentResponseVO.getAvr());
                                    eci = enrollmentResponseVO.getEci();
                                    threeDStatus = "Frictionless";
                                }
                            }
                            else  if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                            {
                                String Md                 = request.getParameter("MD");
                                String PaRes              = request.getParameter("PaRes");

//                                threeDSServerTransID    = request.getParameter("threeDSServerTransID");
                                browserTimezoneOffset   = request.getParameter("browserTimezoneOffset");
                                browserScreenHeight     = request.getParameter("browserScreenHeight");
                                browserScreenWidth      = request.getParameter("browserScreenWidth");
                                browserLanguage         = request.getParameter("browserLanguage");
                                browserColorDepth       = request.getParameter("browserColorDepth");
                                browserJavaEnabled      = request.getParameter("browserJavaEnabled");

                                EnrollmentRequestVO enrollmentRequestVO = new EnrollmentRequestVO();
                                enrollmentRequestVO.setMessageId(Md);
                                enrollmentRequestVO.setExpiryMonth(expMonth);
                                enrollmentRequestVO.setExpiryYear(expYear);
                                enrollmentRequestVO.setPaRes(PaRes);
                                enrollmentRequestVO.setName(firstname + " " + lastname);
                                enrollmentRequestVO.setPan(ccnum);
                                enrollmentRequestVO.setExpiry(expMonth + CashFlowsCaiboPaymentGatewayUtils.getLast2DigitOfExpiryYear(expYear));
                                enrollmentRequestVO.setCurrency(currency);
//                                enrollmentRequestVO.setMid(mpiMid);
//                                enrollmentRequestVO.setCurrency(CurrencyCodeISO4217.getNumericCurrencyCode(currency));
                                if (currency.equalsIgnoreCase("JPY"))
                                    enrollmentRequestVO.setAmount(getJPYAmount(amount));
                                else
                                    enrollmentRequestVO.setAmount(amount);

                                enrollmentRequestVO.setDesc(description);
                                if (functions.isValueNull(userAgent))
                                    enrollmentRequestVO.setUseragent(userAgent);
                                else
                                    enrollmentRequestVO.setUseragent("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US)");
                                enrollmentRequestVO.setAccept("en-us");
                                enrollmentRequestVO.setTrackid(trackingId);
                                enrollmentRequestVO.setAcceptHeader(acceptHeader);
                                enrollmentRequestVO.setHostUrl(merchantDetailsVO.getHostUrl());
                                enrollmentRequestVO.setAcquirerMerchantID(gatewayAccount.getMerchantId());
                                if (functions.isValueNull(merchantDetailsVO.getCountry()))
                                {
                                    enrollmentRequestVO.setMerchantCountry(merchantDetailsVO.getCountry());
                                }
                                enrollmentRequestVO.setBrowserLanguage(browserLanguage);
                                enrollmentRequestVO.setBrowserTimezoneOffset(browserTimezoneOffset);
                                enrollmentRequestVO.setBrowserColorDepth(browserColorDepth);
                                enrollmentRequestVO.setBrowserScreenHeight(browserScreenHeight);
                                enrollmentRequestVO.setBrowserScreenWidth(browserScreenWidth);
                                enrollmentRequestVO.setBrowserJavaEnabled(browserJavaEnabled);
                                enrollmentRequestVO.setBrowserIp(ipAddress);
//                                enrollmentRequestVO.setThreeDSServerTransID(threeDSServerTransID);


                                CashFlowsCaiboPaymentGateway cashFlowsCaiboPaymentGateway = new CashFlowsCaiboPaymentGateway(accountId);
                                EnrollmentResponseVO enrollmentResponseVO                 = cashFlowsCaiboPaymentGateway.processAuthentication(enrollmentRequestVO);

                                result = enrollmentResponseVO.getStatus();
                                eci = enrollmentResponseVO.getEci();
                                if ("Y".equalsIgnoreCase(result) || "A".equalsIgnoreCase(result))
                                {
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, null, null, auditTrailVO, null);
                                    paresDecodeResponseVO.setXid(enrollmentResponseVO.getXID());
                                    paresDecodeResponseVO.setCavv(enrollmentResponseVO.getCAVV());
                                    paresDecodeResponseVO.setEci(enrollmentResponseVO.getEci());
                                    commRequestVO.setParesDecodeResponseVO(paresDecodeResponseVO);
                                }
                            }
                    /*if("Frictionless".equalsIgnoreCase(threeDStatus))
                    {*/
                            con = Database.getConnection();
                            transactionLogger.error("avr--->" + avr);
                            if (PayneticsGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                            {
                                if ("Y".equalsIgnoreCase(avr) || "A".equalsIgnoreCase(avr))
                                {
                                    PayneticsPaymentProcess payneticsPaymentProcess = new PayneticsPaymentProcess();
                                    PayneticsRequestVO payneticsRequestVO = new PayneticsRequestVO();
                                    payneticsPaymentProcess.setPayneticsRequestVO(payneticsRequestVO, trackingId, cvv);
                                    payneticsRequestVO.setAttemptThreeD("Frictionless");
                                    payneticsRequestVO.setParesDecodeResponseVO(paresDecodeResponseVO);
                                    PayneticsGateway payneticsGateway = new PayneticsGateway(accountId);
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D2_CONFIRMATION_STARTED, ActionEntry.STATUS_3D2_CONFIRMATION, null, null, auditTrailVO, null);
                                    transRespDetails = (Comm3DResponseVO) payneticsGateway.process3DSaleConfirmation(trackingId, payneticsRequestVO, transType);
                                }
                                else
                                {
                                    status = "failed";
                                }
                            }
                            else if (ICardPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                            {
                                ICardPaymentGateway iCardPaymentGateway = new ICardPaymentGateway(accountId);
                                commRequestVO.setAttemptThreeD("Frictionless");
                                commRequestVO.setParesDecodeResponseVO(paresDecodeResponseVO);
                                if ("Y".equalsIgnoreCase(avr) || "A".equalsIgnoreCase(avr))
                                {
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D2_CONFIRMATION_STARTED, ActionEntry.STATUS_3D2_CONFIRMATION, null, null, auditTrailVO, null);
                                    if ("sale".equalsIgnoreCase(transType))
                                        transRespDetails = (Comm3DResponseVO) iCardPaymentGateway.processCommon3DSaleConfirmation(trackingId, commRequestVO, "");
                                    else
                                        transRespDetails = (Comm3DResponseVO) iCardPaymentGateway.processCommon3DAuthConfirmation(trackingId, commRequestVO, "");
                                }
                                else
                                {
                                    status = "failed";
                                }
                            }
                            else if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                            {
                                if ("Y".equalsIgnoreCase(result) || "A".equalsIgnoreCase(result))
                                {   // Y = authentiacation sucessful //A = authentication attempted
                                    CashFlowsCaiboPaymentGateway cashFlowsCaiboPaymentGateway = new CashFlowsCaiboPaymentGateway(accountId);
                                    CashFlowsCaiboPaymentProcess cashFlowsCaiboPaymentProcess = new CashFlowsCaiboPaymentProcess();

                                    String DM = "";
                                    if (functions.isValueNull(request.getParameter("DM")))
                                    {
                                        DM = request.getParameter("DM");
                                        cvv = PzEncryptor.decryptCVV(DM);
                                    }

                                    String DB = "";
                                    if (functions.isValueNull(request.getParameter("DB"))){
                                        DB = PzEncryptor.decryptName(request.getParameter("DB"));
                                    }

                                    cashFlowsCaiboPaymentProcess.setCashflowsCaiboRequestVO(commRequestVO, trackingId, cvv, DB);
                                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D2_CONFIRMATION_STARTED, ActionEntry.STATUS_3D2_CONFIRMATION, null, null, auditTrailVO, null);
                                    transactionLogger.error("TransactionType " + transType);
                                    if ("auth".equalsIgnoreCase(transType))
                                    {
                                        transRespDetails = (Comm3DResponseVO) cashFlowsCaiboPaymentGateway.processCommon3DAuthConfirmation(trackingId, commRequestVO);
                                    }
                                    else if ("sale".equalsIgnoreCase(transType))
                                    {
                                        transRespDetails = (Comm3DResponseVO) cashFlowsCaiboPaymentGateway.processSale(trackingId, commRequestVO);
                                    }
                                }
                                else if ("N".equalsIgnoreCase(result) || "U".equalsIgnoreCase(result) || "Failed".equalsIgnoreCase(result))
                                {   // N  = authentication failed  //U = authentication un-available
                                    status = "failed";
                                }
                                else
                                {
                                    status = "pending";
                                }
                            }
                            else
                            {
                                transactionLogger.error("transType---->" + transType);
                                entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D2_CONFIRMATION_STARTED, ActionEntry.STATUS_3D2_CONFIRMATION, null, null, auditTrailVO, null);
                                if ("sale".equalsIgnoreCase(transType))
                                    transRespDetails = (Comm3DResponseVO) pg.processCommon3DSaleConfirmation(trackingId, commRequestVO);
                                else
                                    transRespDetails = (Comm3DResponseVO) pg.processCommon3DAuthConfirmation(trackingId, commRequestVO);
                            }

                            if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType) && transRespDetails != null)
                            {
                                transactionLogger.error("inside for Cashflows-Caibo integration");
                                if (functions.isValueNull(transRespDetails.getErrorName()))
                                    errorName = transRespDetails.getErrorName();
                                transactionId = transRespDetails.getTransactionId();
                                message = transRespDetails.getRemark();
                                transactionLogger.error("status-----> "+ transRespDetails.getStatus());
                                if ("success".equalsIgnoreCase(transRespDetails.getStatus()))
                                {
                                    billingDesc = gatewayAccount.getDisplayName();
                                    transRespDetails.setDescriptor(billingDesc);
                                    if (!functions.isValueNull(message))
                                        message = "Transaction Successful";
                                    status = "success";
                                    if ("sale".equalsIgnoreCase(transType))
                                    {
                                        updatestatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                        dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                    }
                                    else
                                    {
                                        updatestatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                        dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                        transactionLogger.error("DBQuery--->" + dbBuffer);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                    }
                                }
                                else if ("failed".equalsIgnoreCase(transRespDetails.getStatus()))
                                {
                                    message = transRespDetails.getRemark();
                                    if (!functions.isValueNull(message))
                                        message = "Transaction failed";
                                    status = "failed";
                                    updatestatus = PZTransactionStatus.AUTH_FAILED.toString();
                                    dbBuffer.append("update transaction_common set status='authfailed',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                    transactionLogger.error("DBQuery--->" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                }
                                else
                                {
                                    message = transRespDetails.getRemark();
                                    if (!functions.isValueNull(message))
                                        message = "Transaction pending";
                                    updatestatus = "Pending";
                                    status   = "Pending";
                                }
                            }
                            else
                            {
                                if (transRespDetails != null)
                                {
                                    if (functions.isValueNull(transRespDetails.getErrorName()))
                                        errorName = transRespDetails.getErrorName();
                                    transactionId = transRespDetails.getTransactionId();
                                    message = transRespDetails.getRemark();
                                    if ("success".equalsIgnoreCase(transRespDetails.getStatus()))
                                    {
                                        billingDesc = gatewayAccount.getDisplayName();
                                        transRespDetails.setDescriptor(billingDesc);
                                        if (!functions.isValueNull(message))
                                            message = "Transaction Successful";
                                        if ("sale".equalsIgnoreCase(transType))
                                        {
                                            updatestatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                        }
                                        else
                                        {
                                            updatestatus = PZTransactionStatus.AUTH_SUCCESS.toString();
                                            dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                            transactionLogger.error("DBQuery--->" + dbBuffer);
                                            Database.executeUpdate(dbBuffer.toString(), con);
                                            entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, auditTrailVO, null);
                                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                        }
                                    }
                                    else if ("pending3DConfirmation".equalsIgnoreCase(transRespDetails.getStatus()))
                                    {
                                        Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;
                                        transRespDetails.setThreeDVersion("3Dv2");
                                        paymentManager.updatePaymentIdForCommon(transRespDetails, trackingId);
                                        paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                                        String form = paymentProcess.get3DConfirmationForm(trackingId, ctoken, response3D);
                                        writer.println(form);
                                        return;

                                    }
                                    else
                                    {
                                        if (!functions.isValueNull(message))
                                            message = "Transaction failed";
                                        updatestatus = PZTransactionStatus.AUTH_FAILED.toString();
                                        dbBuffer.append("update transaction_common set status='authfailed',");
                                        if (functions.isValueNull(transactionId))
                                            dbBuffer.append("paymentid='" + transactionId + "',");
                                        dbBuffer.append("eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                        Database.executeUpdate(dbBuffer.toString(), con);
                                        transactionLogger.error("DBQuery--->" + dbBuffer);
                                        entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                        statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                    }
                                }
                                if ("failed".equalsIgnoreCase(status))
                                {
                                    transRespDetails = new Comm3DResponseVO();
                                    if ("N".equalsIgnoreCase(avr))
                                    {
                                        transRespDetails.setDescription("Authenticated Transaction Denied(Frictionless)");
                                        transRespDetails.setRemark("Authenticated Transaction Denied(Frictionless)");
                                    }
                                    else if ("R".equalsIgnoreCase(avr))
                                    {
                                        transRespDetails.setDescription("Authentication Rejected(Frictionless)");
                                        transRespDetails.setRemark("Authentication Rejected(Frictionless)");
                                    }
                                    else if ("failed".equalsIgnoreCase(avr))
                                    {
                                        transRespDetails.setDescription(result);
                                        transRespDetails.setRemark(result);
                                    }
                                    message = transRespDetails.getRemark();
                                    if (!functions.isValueNull(message))
                                        message = "Transaction failed";
                                    updatestatus = PZTransactionStatus.AUTH_FAILED.toString();
                                    dbBuffer.append("update transaction_common set status='authfailed',eci='" + eci + "',customerIp='" + ipAddress + "',customerIpCountry='" + functions.getIPCountryShort(ipAddress) + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingId);
                                    transactionLogger.error("DBQuery--->" + dbBuffer);
                                    Database.executeUpdate(dbBuffer.toString(), con);
                                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                                    statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                                }
                            }
                            AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();

                            if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType)){
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);
                            }else{
                                asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), updatestatus, message, billingDesc);
                            }

                            AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), updatestatus, message, billingDesc);

                            genericTransDetailsVO.setBillingDiscriptor(billingDesc);
                            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
                            commonValidatorVO.setEci(eci);

                            transactionUtility.setToken(commonValidatorVO, updatestatus);
                            if (functions.isValueNull(notificationUrl) && ("3D".equals(merchantDetailsVO.getTransactionNotification()) || "Both".equals(merchantDetailsVO.getTransactionNotification())))
                            {
                                transactionLogger.error("inside sending notification---" + notificationUrl);
                                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                                if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                                {
                                    transactionDetailsVO1.setBankReferenceId(transactionId);
                                    if (functions.isValueNull(transactionDetailsVO1.getExpdate())) {
                                        transactionDetailsVO1.setExpdate(functions.maskingExpiry(transactionDetailsVO1.getExpdate()));
                                    }
                                }
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatestatus, message, "");
                            }
                        }else
                        {
                            status = "pending";
                            message = "Transaction is in progress";
                            updatestatus=PZTransactionStatus.AUTH_STARTED.toString();
                        }
                    }else
                    {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatestatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                        } else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            message = "Transaction Successful";
                            updatestatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                        }
                        else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                        {
                            status = "fail";
                            //confirmStatus = "N";
                            message = "Failed";
                            updatestatus=PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        else if (PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                        {
                            status = "pending";
                            //confirmStatus = "P";
                            message = "Transaction is in progress";
                            updatestatus=PZTransactionStatus.AUTHSTARTED_3D.toString();
                        }
                        else
                        {
                            status = "fail";
                            //confirmStatus = "N";
                            message = "Failed(Transaction not found in correct status)";
                            updatestatus=PZTransactionStatus.FAILED.toString();
                        }
                    }

                if (CashFlowsCaiboPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromType))
                {
                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, status, billingDesc);
                    }
                    else
                    {
                        request.setAttribute("responceStatus", status);
                        request.setAttribute("displayName", billingDesc);
                        request.setAttribute("remark", message);
                        request.setAttribute("errorName", errorName);
                        request.setAttribute("transDetail", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);

                        String confirmationPage = "";
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
                else
                {
                    if ("Y".equalsIgnoreCase(autoRedirect))
                    {
                        transactionUtility.doAutoRedirect(commonValidatorVO, response, updatestatus, billingDesc);
                    }
                    else
                    {
                        request.setAttribute("responceStatus", updatestatus);
                        request.setAttribute("displayName", billingDesc);
                        request.setAttribute("remark", message);
                        request.setAttribute("errorName", errorName);
                        request.setAttribute("transDetail", commonValidatorVO);
                        session.setAttribute("ctoken", ctoken);

                        String confirmationPage = "";
                        confirmationPage = "/confirmationCheckout.jsp?ctoken=";

                        RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                        rd.forward(request, response);
                    }
                }
            }
        }
            //}
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException---->",e);
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException---->", e);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException---->", e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError---->", systemError);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException---->", e);
        }catch (Exception e)
        {
            transactionLogger.error("Exception---->", e);
        }
        finally
        {
            if(con!=null)
            Database.closeConnection(con);
        }
    }
    public String getCardExpiry(String cardExpiryMonth,String cardExpiryYear){
        return cardExpiryYear.substring(2,4)+cardExpiryMonth;
    }
    public String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));
        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }

    public String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    private Comm3DRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Functions functions=new Functions();
        Comm3DRequestVO commRequestVO = new Comm3DRequestVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommTransactionDetailsVO transDetailsVO=new CommTransactionDetailsVO();
        CommDeviceDetailsVO commDeviceDetailsVO=new CommDeviceDetailsVO();
        RecurringBillingVO recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getAccountId());
        String merctId = account.getMerchantId();
        String alias = account.getAliasName();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        String displayname = account.getDisplayName();
        ReserveField2VO reserveField2VO = new ReserveField2VO();
        CommAccountInfoVO commAccountInfoVO = new CommAccountInfoVO();

        if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
        {
            cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
            cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
            cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
            cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
            cardDetailsVO.setCardType(Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));
            transactionLogger.error("A2 country---"+commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
            cardDetailsVO.setCountry_code_A2(commonValidatorVO.getCardDetailsVO().getCountry_code_A2());

        }
        else if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
        {
            cardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
            cardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());
            addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
            cardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());
            cardDetailsVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname()+ " " +commonValidatorVO.getAddressDetailsVO().getLastname());
        }
        else if(functions.isValueNull(commonValidatorVO.getCardDetailsVO().getAccountNumber()))
        {
            reserveField2VO.setAccountType(commonValidatorVO.getCardDetailsVO().getAccountType());
            reserveField2VO.setAccountNumber(commonValidatorVO.getCardDetailsVO().getAccountNumber());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getCardDetailsVO().getRoutingNumber());
        }
        else if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getRoutingNumber()))
        {
            reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
            reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
            reserveField2VO.setCheckNumber(commonValidatorVO.getReserveField2VO().getCheckNumber());
        }

        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setCustomerid(commonValidatorVO.getCustomerId());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setToId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setPreviousTransactionId(commonValidatorVO.getTransDetailsVO().getPaymentid());

        if(commonValidatorVO.getDeviceDetailsVO()!=null)
        {
            commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
            commDeviceDetailsVO.setAcceptHeader(commonValidatorVO.getDeviceDetailsVO().getAcceptHeader());
            commDeviceDetailsVO.setBrowserColorDepth(commonValidatorVO.getDeviceDetailsVO().getBrowserColorDepth());
            commDeviceDetailsVO.setBrowserLanguage(commonValidatorVO.getDeviceDetailsVO().getBrowserLanguage());
            commDeviceDetailsVO.setBrowserTimezoneOffset(commonValidatorVO.getDeviceDetailsVO().getBrowserTimezoneOffset());
            commDeviceDetailsVO.setBrowserScreenHeight(commonValidatorVO.getDeviceDetailsVO().getBrowserScreenHeight());
            commDeviceDetailsVO.setBrowserScreenWidth(commonValidatorVO.getDeviceDetailsVO().getBrowserScreenWidth());
            commDeviceDetailsVO.setBrowserJavaEnabled(commonValidatorVO.getDeviceDetailsVO().getBrowserJavaEnabled());
        }

        transactionLogger.debug("paymentType------"+transDetailsVO.getPaymentType());
        transactionLogger.debug("cardType------"+transDetailsVO.getCardType());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
        merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        merchantAccountVO.setCountry(commonValidatorVO.getMerchantDetailsVO().getCountry());

        merchantAccountVO.setZipCode(commonValidatorVO.getMerchantDetailsVO().getZip());
        //  merchantAccountVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantTelNo = commonValidatorVO.getMerchantDetailsVO().getTelNo();
        String mPhone = "";

        if (functions.isValueNull(merchantTelNo) && merchantTelNo.contains("-"))
        {
            String[] phone = merchantTelNo.split("-");
            mPhone = phone[1];
        }
        else
        {
            mPhone = merchantTelNo;
        }
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if (functions.isValueNull(mPhone))
            merchantAccountVO.setPartnerSupportContactNumber(mPhone);

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);
        commRequestVO.setReserveField2VO(reserveField2VO);
        commRequestVO.setCommAccountInfoVO(commAccountInfoVO);
        commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);
        commRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
        commRequestVO.setAttemptThreeD("3Dv2");

        if (functions.isValueNull(commonValidatorVO.getAttemptThreeD()))
            commRequestVO.setAttemptThreeD(commonValidatorVO.getAttemptThreeD());

        return commRequestVO;
    }
}
