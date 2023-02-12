import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.cup.CupUtils;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fraud.FraudChecker;
import com.manager.PaymentManager;
import com.manager.TokenManager;
import com.manager.dao.FraudServiceDAO;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.SplitPaymentVO;
import com.manager.vo.VTResponseVO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PayMitco.core.PayMitcoUtility;
import com.payment.apco.core.ApcoPayUtills;
import com.payment.b4payment.B4Utils;
import com.payment.b4payment.vos.TransactionResponse;
import com.payment.common.core.*;
import com.payment.duspaydirectdebit.DusPayDDUtils;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.inpay.InPayAccount;
import com.payment.jeton.JetonResponseVO;
import com.payment.jeton.JetonUtils;
import com.payment.neteller.NetellerUtils;
import com.payment.neteller.response.Links;
import com.payment.neteller.response.NetellerResponse;
import com.payment.p4.gateway.P4ResponseVO;
import com.payment.p4.gateway.P4Utils;
import com.payment.paysec.PaySecUtils;
import com.payment.sms.AsynchronousSmsService;
import com.payment.utils.TransactionUtilsDAO;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectKitValidatorVO;
import com.payment.validators.vo.ReserveField2VO;
import com.payment.voguePay.VoguePayUtils;
import com.transaction.utils.TransactionCoreUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//import com.directi.pg.core.GatewayType;
//import com.directi.pg.core.GatewayTypeService;

/**
 * Created with IntelliJ IDEA.
 * User: sagar
 * Date: 6/18/14
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualSingleCall extends HttpServlet
{
    final static ResourceBundle RB          = LoadProperties.getProperty("com.directi.pg.CupServlet");
    final static ResourceBundle RB1         = LoadProperties.getProperty("com.directi.pg.InPayServlet");
    final static ResourceBundle paysafecard = LoadProperties.getProperty("com.directi.pg.paysafecard");
    private static Logger log               = new Logger(VirtualSingleCall.class.getName());
    private static Logger transactionLogger = new Logger(VirtualSingleCall.class.getName());

    public static DirectKitValidatorVO getRequestParametersForSale(HttpServletRequest req)
    {
        DirectKitValidatorVO    directKitValidatorVO        = new DirectKitValidatorVO();
        MerchantDetailsVO       merchantDetailsVO           = new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO     = new GenericAddressDetailsVO();
        GenericCardDetailsVO    genericCardDetailsVO        = new GenericCardDetailsVO();
        GenericTransDetailsVO   genericTransDetailsVO       = new GenericTransDetailsVO();
        SplitPaymentVO          splitPaymentVO              = new SplitPaymentVO();
        RecurringBillingVO      recurringBillingVO          = null;
        ReserveField2VO         reserveField2VO             = null;

        String remoteAddr = Functions.getIpAddress(req);

        String reqIp = "";

        if(remoteAddr.contains(","))
        {
            String sIp[]    = remoteAddr.split(",");
            reqIp           = sIp[0].trim();
        }
        else
        {
            reqIp = remoteAddr;
        }

        int serverPort      = req.getServerPort();
        String servletPath  = req.getServletPath();
        String header       = "Client =" + reqIp + ":" + serverPort + ",X-Forwarded=" + servletPath;

        if(req.getParameter("version") != null && !req.getParameter("version").equals(""))
        {
            directKitValidatorVO.setVersion(req.getParameter("version"));
        }
        else
        {
            directKitValidatorVO.setVersion(" ");
        }

        directKitValidatorVO.setAction("Sale");

        //memberdetails
        merchantDetailsVO.setMemberId(getValue("toid", req));
        merchantDetailsVO.setAccountId(getValue("accountid", req));
        genericTransDetailsVO.setTotype(getValue("totype", req));
        genericTransDetailsVO.setRedirectUrl(getValue("redirecturl", req));
        genericTransDetailsVO.setChecksum(getValue("checksum", req));

        //order
        genericTransDetailsVO.setAmount(getValue("amount", req));
        genericTransDetailsVO.setChecksumAmount(getValue("amount", req));
        genericTransDetailsVO.setOrderDesc(getValue("orderdescription", req));
        genericTransDetailsVO.setOrderId(getValue("description", req));
        genericTransDetailsVO.setHeader(header);
        genericTransDetailsVO.setCurrency(getValue("currency",req));
        genericTransDetailsVO.setResponseHashInfo(getValue("csid",req));//setting csid for GCP/Payforasia


        //customerdetails
        genericAddressDetailsVO.setCountry(getValue("countrycode", req));
        genericAddressDetailsVO.setCity(getValue("city", req));
        genericAddressDetailsVO.setState(getValue("state", req));
        genericAddressDetailsVO.setZipCode(getValue("zip", req));
        genericAddressDetailsVO.setStreet(getValue("street", req));
        genericAddressDetailsVO.setPhone(getValue("telno", req));
        genericAddressDetailsVO.setEmail(getValue("emailaddr", req));
        genericAddressDetailsVO.setLanguage(getValue("language", req));
        genericAddressDetailsVO.setFirstname(getValue("firstname", req));
        genericAddressDetailsVO.setLastname(getValue("lastname", req));
        genericAddressDetailsVO.setBirthdate(getValue("byear", req) + getValue("bmonth", req) + getValue("bday", req));
        genericAddressDetailsVO.setSsn(getValue("ssn", req));
        genericAddressDetailsVO.setTelnocc(getValue("telnocc", req));
        genericAddressDetailsVO.setCardHolderIpAddress(getValue("cardholderipaddress", req));
        genericAddressDetailsVO.setIp(reqIp);
        //genericAddressDetailsVO.setIp("45.64.195.218");

        //carddetails
        genericCardDetailsVO.setCardNum(getValue("cardnumber", req));
        genericCardDetailsVO.setcVV(getValue("cvv", req));
        genericCardDetailsVO.setExpMonth(getValue("expiry_month", req));
        genericCardDetailsVO.setExpYear(getValue("expiry_year", req));
        genericCardDetailsVO.setCardHolderName(getValue("firstname", req) + " " + getValue("lastname", req));
        genericCardDetailsVO.setIBAN(getValue("iban", req));
        genericCardDetailsVO.setBIC(getValue("bic", req));
        genericCardDetailsVO.setMandateId(getValue("mandateId", req));
        genericCardDetailsVO.setVoucherNumber(getValue("vouchernumber",req));
        genericCardDetailsVO.setSecurity_Code(getValue("securityCode",req));
        //RecurringBilling
        if(!getValue("interval",req).equals("") && !getValue("frequency",req).equals(""))
        {
            recurringBillingVO = new RecurringBillingVO();
            recurringBillingVO.setInterval(getValue("interval", req));
            recurringBillingVO.setFrequency(getValue("frequency", req));
            recurringBillingVO.setRunDate(getValue("runDate", req));
            recurringBillingVO.setRecurring("Y");
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);

        }

        if(!getValue("accountnumber",req).equals("") && !getValue("routingnumber",req).equals("") && !getValue("accountType",req).equals(""))
        {
            reserveField2VO = new ReserveField2VO();
            reserveField2VO.setAccountNumber(getValue("accountnumber", req));
            reserveField2VO.setRoutingNumber(getValue("routingnumber", req));
            reserveField2VO.setAccountType(getValue("accountType", req));
        }
        //paymentdetails
        directKitValidatorVO.setPaymentType(getValue("paymenttype", req));
        directKitValidatorVO.setCardType(getValue("cardtype", req));
        directKitValidatorVO.setTerminalId(getValue("terminalid", req));
        directKitValidatorVO.setRequestedIP(Functions.getIpAddress(req));

        directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        directKitValidatorVO.setReserveField2VO(reserveField2VO);
        directKitValidatorVO.setSplitPaymentVO(splitPaymentVO);

        return directKitValidatorVO;
    }

    private static String getValue(String sTag, HttpServletRequest req)
    {
        String value  ="";
        if(req.getParameter(sTag)!=null && !req.getParameter(sTag).equals(""))
        {
            value= req.getParameter(sTag);
        }
        return value;
    }

    private static String generateAutoSubmitForm(String actionUrl, Map<String, String> paramMap)
    {
        StringBuilder html = new StringBuilder();
        html.append("<form target=\"_blank\" id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\" >\n");
        //html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\" onsubmit=\"window.open('\"").append(actionUrl).append("\"', 'popup', 'width=200,height=200,scrollbars=yes,menubar=no,status=no');\">\n");
        if(paramMap!=null)
        {
            for (String key : paramMap.keySet())
            {
                html.append("<input type=\"hidden\" name=\"" + key + "\" id=\"" + key + "\" value=\"" + paramMap.get(key) + "\">\n");
            }
        }
        html.append("</form>\n");
        return html.toString();
    }

    private static String getMD5HashVal(String str)
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest) {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();

        } catch (UnsupportedEncodingException e) {
           // e.printStackTrace();
            log.debug("UnsupportedEncodingExceptionUnsupportedEncodingException ::" + e);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
            log.debug("NoSuchAlgorithmException ::" + e);
        }
        return encryptedString;
    }

    public static String generateAutoSubmitFormForPaySafeCard(String actionUrl)
    {
        StringBuilder html = new StringBuilder();
        html.append("<script language=\"javascript\">window.onload=function(){window.open('"+actionUrl+"','mywindow','width=600,height=750')}</script>\n");
        html.append("<form id=\"pay_form\" name=\"pay_form\" action=\"").append(actionUrl).append("\" method=\"post\">\n");
        html.append("</form>\n");
        return html.toString();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        Merchants merchants = new Merchants();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        String error        = "";
        CommonValidatorVO directKitValidatorVO = new CommonValidatorVO();
        Functions functions = new Functions();

        //Read VT Request
        directKitValidatorVO                        = getRequestParametersForSale(request);
        GenericCardDetailsVO genericCardDetailsVO   = directKitValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = directKitValidatorVO.getMerchantDetailsVO();
        String accountId                            = merchantDetailsVO.getAccountId();
        /*if(functions.isValueNull(user.getCSRFToken()))
            directKitValidatorVO.setCtoken(user.getCSRFToken());*/
        //TransactionUtils transactionUtils=new TransactionUtils();
        TransactionCoreUtils transactionUtils   = new TransactionCoreUtils();
        ServletContext application              =getServletContext();
        TransactionUtilsDAO transactionUtilsDAO =new TransactionUtilsDAO();
        ActionEntry entry                       = new ActionEntry();
        VTResponseVO vtResponseVO           = new VTResponseVO();
        AuditTrailVO auditTrailVO           = new AuditTrailVO();
        PaymentManager paymentManager       = new PaymentManager();
        TransactionHelper transactionHelper = new TransactionHelper();
        AbstractPaymentGateway pg           = null;
        ErrorCodeUtils errorCodeUtils       = new ErrorCodeUtils();
        auditTrailVO.setActionExecutorId(directKitValidatorVO.getMerchantDetailsVO().getMemberId());
        auditTrailVO.setActionExecutorName("MerchantVT");
        request.setAttribute("terminalid",request.getParameter("terminalid"));
        request.setAttribute("accountid",request.getParameter("accountid"));
        request.setAttribute("cardtype",request.getParameter("cardtype"));
        request.setAttribute("paymodeid",request.getParameter("paymodeid"));
        request.setAttribute("cardtypename",request.getParameter("cardtypename"));
        request.setAttribute("paymenttype",request.getParameter("paymenttype"));
        request.setAttribute("filename",request.getParameter("filename"));
        request.setAttribute("currency",request.getParameter("currency"));

        String remoteAddr       = Functions.getIpAddress(request);
        String httpProtocol     =request.getScheme();
        int serverPort          = request.getServerPort();
        String servletPath      = request.getServletPath();
        String header           = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String hostName         = httpProtocol+"://"+remoteAddr;
        int trackingId          = 0;
        String toid             = "";
        String checksum         = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
        try
        {
            if (!merchants.isLoggedIn(session))
            {
                transactionLogger.debug("member is logout ");
                response.sendRedirect("/merchant/Logout.jsp");
                return;
            }
            toid = directKitValidatorVO.getMerchantDetailsVO().getMemberId();
            if(!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
            {
                errorCodeVO.setErrorType(ErrorType.VALIDATION);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("VirtualSingleCall.java","doPost()",null,"Merchant", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,errorCodeListVO,null,null);
            }
            else
            {
                merchantDetailsVO = getMerchantConfigDetails(toid);
                //merchantDetailsVO.setMultiCurrencySupport((String)session.getAttribute("multiCurrencySupport"));
                directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                directKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
                directKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);
            }

            directKitValidatorVO.getMerchantDetailsVO().setAccountId(accountId);
            String key          = merchantDetailsVO.getKey();
            String checksumAlgo = merchantDetailsVO.getChecksumAlgo();
            String totype       = genericTransDetailsVO.getTotype();
            String amount       = genericTransDetailsVO.getAmount();
            String description  = genericTransDetailsVO.getOrderId();
            String redirectUrl  = genericTransDetailsVO.getRedirectUrl();
            if(!functions.isValueNull(redirectUrl))
            {
                if(functions.isValueNull(merchantDetailsVO.getHostUrl()))
                {
                    redirectUrl = "https://" + merchantDetailsVO.getHostUrl() + "/transaction/virtualTerminalRedirect.jsp";
                }else
                {
                    redirectUrl = "http://localhost:8081/transaction/virtualTerminalRedirect.jsp";
                }
                genericTransDetailsVO.setRedirectUrl(redirectUrl);
            }
            if (functions.isValueNull(directKitValidatorVO.getToken()))
            {
                checksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), directKitValidatorVO.getToken(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
            }
            else if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
            {
               // checksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), genericCardDetailsVO.getCardNum(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                checksum = Functions.generateChecksumV4(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(),  merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
            }
            else
            {
                checksum = Functions.generateChecksumV4(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
            }

            genericTransDetailsVO.setChecksum(checksum);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);

            int paymenttype = 0;
            paymenttype     = Integer.parseInt(directKitValidatorVO.getPaymentType()+"");
            int cardtype    = 0;
            cardtype        = Integer.parseInt(directKitValidatorVO.getCardType()+"");

            genericTransDetailsVO.setFromid(GatewayAccountService.getGatewayAccount(accountId).getMerchantId());
            genericTransDetailsVO.setFromtype(GatewayAccountService.getGatewayAccount(accountId).getGateway());

            if(merchantDetailsVO.getMultiCurrencySupport().equals("Y"))
            {
                genericTransDetailsVO.setCurrency(genericTransDetailsVO.getCurrency());
            }
            else
            {
                genericTransDetailsVO.setCurrency(GatewayAccountService.getGatewayAccount(accountId).getCurrency());
            }

            System.out.println(PaymentModeEnum.PAYG.getValue()+"--"+paymenttype+"--"+CardTypeEnum.PAYG.getValue()+"--"+cardtype);

            if((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymenttype || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymenttype || PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymenttype) && CardTypeEnum.CUP_CARDTYPE.ordinal() == cardtype)
            {
                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error                                   = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error != null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                directKitValidatorVO    = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);
                trackingId              = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                transactionUtilsDAO.updateAuthstartedTransactionforCup(directKitValidatorVO, String.valueOf(trackingId));
                int detailId = entry.actionEntryForCUP(directKitValidatorVO.getTrackingid(),directKitValidatorVO.getTransDetailsVO().getAmount(),ActionEntry.ACTION_AUTHORISTION_STARTED,ActionEntry.STATUS_AUTHORISTION_STARTED,null,directKitValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),auditTrailVO,directKitValidatorVO);

                /*  Make Online Fraud Checking Using  Fraud Processor
                 *  Online Fraud checking is done only if merchant is active for online fraud check
                 */
                transactionLogger.debug("online fraud check flag----" + merchantDetailsVO.getOnlineFraudCheck());
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                {
                    FraudServiceDAO fraudServiceDAO                 = new FraudServiceDAO();
                    FraudAccountDetailsVO merchantFraudAccountVO    = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        directKitValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        //directKitValidatorVO.getAddressDetailsVO().setCardHolderIpAddress(directKitValidatorVO.getAddressDetailsVO().getIp());
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(directKitValidatorVO, merchantFraudAccountVO);
                        /*if (directKitValidatorVO.isFraud())
                        {
                            //action to be taken
                        }*/
                    }
                }

                String html = getCupRequest(directKitValidatorVO, detailId);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymenttype && CardTypeEnum.INPAY_CARDTYPE.ordinal()==cardtype)
            {
                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error                                   = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId          = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTerminalId(), auditTrailVO);

                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                transactionLogger.debug("online fraud check flag----" + merchantDetailsVO.getOnlineFraudCheck());
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                {
                    FraudServiceDAO fraudServiceDAO                 = new FraudServiceDAO();
                    FraudAccountDetailsVO merchantFraudAccountVO    = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        directKitValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        //directKitValidatorVO.getAddressDetailsVO().setCardHolderIpAddress(directKitValidatorVO.getAddressDetailsVO().getIp());
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(directKitValidatorVO, merchantFraudAccountVO);
                       /* if (directKitValidatorVO.isFraud())
                        {
                            //action to be taken
                        }*/
                    }
                }

                String html = getInPayRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            else if(String.valueOf(PaymentModeEnum.PAYG.getValue()).equals(String.valueOf(paymenttype)) && (String.valueOf(CardTypeEnum.PAYG.getValue()).equals(String.valueOf(cardtype))))
            {
                transactionLogger.error("inside voguepay VT-->");
                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error                                   = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId           = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTrackingid(), auditTrailVO);


                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                transactionLogger.debug("online fraud check flag----" + merchantDetailsVO.getOnlineFraudCheck());
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                {
                    FraudServiceDAO fraudServiceDAO                 = new FraudServiceDAO();
                    FraudAccountDetailsVO merchantFraudAccountVO    = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        directKitValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        //directKitValidatorVO.getAddressDetailsVO().setCardHolderIpAddress(directKitValidatorVO.getAddressDetailsVO().getIp());
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(directKitValidatorVO, merchantFraudAccountVO);
                       /* if (directKitValidatorVO.isFraud())
                        {
                            //action to be taken
                        }*/
                    }
                }
                VoguePayUtils voguePayUtils         = new VoguePayUtils();
                CommRequestVO commRequestVO         = null;
                CommResponseVO transRespDetails     = null;
                commRequestVO                       = voguePayUtils.getVoguePayUtils(directKitValidatorVO);

                pg                  =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails    = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);

                String html         = VoguePayUtils.getRedirectForm(String.valueOf(trackingId), transRespDetails);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if(String.valueOf(PaymentModeEnum.PAYG.getValue()).equals(String.valueOf(paymenttype)) && (String.valueOf(CardTypeEnum.DUSPAYDD.getValue()).equals(String.valueOf(cardtype))))
            {
                transactionLogger.error("inside DUSPAYDD VT-->");
                AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error                                   = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId              = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTrackingid(), auditTrailVO);
                if (directKitValidatorVO.getMerchantDetailsVO().getIsService().equalsIgnoreCase("N"))
                {
                    if (genericTransDetailsVO.getFromtype().equalsIgnoreCase("duspaydd"))
                    {
                        transactionLogger.error("inside isService N ---");
                        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                        PZExceptionHandler.raiseConstraintViolationException("VirtualSingleCall.class", "doPost()", null, "Merchant", directKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }

                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */
                transactionLogger.debug("online fraud check flag----" + merchantDetailsVO.getOnlineFraudCheck());
                if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                {
                    FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                    FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                    if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                    {
                        directKitValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                        //directKitValidatorVO.getAddressDetailsVO().setCardHolderIpAddress(directKitValidatorVO.getAddressDetailsVO().getIp());
                        FraudChecker fraudChecker = new FraudChecker();
                        fraudChecker.checkFraudBasedOnMerchantFlagNew(directKitValidatorVO, merchantFraudAccountVO);
                       /* if (directKitValidatorVO.isFraud())
                        {
                            //action to be taken
                        }*/
                    }
                }
               // VoguePayUtils voguePayUtils = new VoguePayUtils();
                DusPayDDUtils dusPayDDUtils=new DusPayDDUtils();
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = dusPayDDUtils.getCommRequestFromUtils(directKitValidatorVO);

                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);

                String html = dusPayDDUtils.getRedirectForm(String.valueOf(trackingId), transRespDetails);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }


            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymenttype && (CardTypeEnum.NEOSURF.ordinal() == cardtype))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","apcopayCreditpage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                checksum = Functions.generateMD5ChecksumVV(toid, totype, amount, description, redirectUrl, key, checksumAlgo);
                genericTransDetailsVO.setChecksum(checksum);
                transactionLogger.debug("checksum in Apco-----" + genericTransDetailsVO.getChecksum());
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTerminalId(), auditTrailVO);

                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html=apcoPayUtills.getApcoPayRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if (PaymentModeEnum.NETBANKING_PAYMODE.ordinal() == paymenttype && (CardTypeEnum.GIROPAY.ordinal() == cardtype))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","apcopayCreditpage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                checksum = Functions.generateMD5ChecksumVV(toid, totype, amount, description, redirectUrl, key, checksumAlgo);
                genericTransDetailsVO.setChecksum(checksum);
                transactionLogger.debug("checksum in Apco-----" + genericTransDetailsVO.getChecksum());
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTerminalId(), auditTrailVO);

                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html=apcoPayUtills.getApcoPayRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if (PaymentModeEnum.POSTPAID_CARD_PAYMODE.ordinal() == paymenttype && (CardTypeEnum.MULTIBANCO.ordinal() == cardtype))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","apcopayCreditpage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                checksum = Functions.generateMD5ChecksumVV(toid, totype, amount, description, redirectUrl, key, checksumAlgo);
                genericTransDetailsVO.setChecksum(checksum);
                transactionLogger.debug("checksum in Apco-----" + genericTransDetailsVO.getChecksum());
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTerminalId(), auditTrailVO);

                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html=apcoPayUtills.getApcoPayRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if (PaymentModeEnum.PREPAID_CARD_PAYMODE.ordinal() == paymenttype && (CardTypeEnum.ASTROPAY.ordinal() == cardtype))
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","apcopayCreditpage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                checksum = Functions.generateMD5ChecksumVV(toid, totype, amount, description, redirectUrl, key, checksumAlgo);
                genericTransDetailsVO.setChecksum(checksum);
                transactionLogger.debug("checksum in Apco-----" + genericTransDetailsVO.getChecksum());
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, directKitValidatorVO.getTerminalId(), auditTrailVO);

                ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
                String html=apcoPayUtills.getApcoPayRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            else if (PaymentModeEnum.WALLET_PAYMODE.ordinal() == paymenttype && CardTypeEnum.NETELLER.ordinal() == cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","netellerCreditPage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                checksum = Functions.generateMD5ChecksumVV(toid, totype, amount, description, redirectUrl, key, checksumAlgo);
                genericTransDetailsVO.setChecksum(checksum);
                transactionLogger.debug("checksum in Neteller-----" + genericTransDetailsVO.getChecksum());
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                NetellerResponse transRespDetails = null;
                NetellerUtils netellerUtils = new NetellerUtils();
                commRequestVO = netellerUtils.getNetellerRequestVO(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails =(NetellerResponse)pg.processSale(String.valueOf(trackingId), commRequestVO);


                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingId));
                        for(Links links : transRespDetails.getLinks())
                        {
                            if(links.getRel().equals("hosted_payment"))
                            {
                                response.sendRedirect(links.getUrl());
                            }
                        }
                        return;
                    }
                    else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails,"authfailed",String.valueOf(trackingId),auditTrailVO,"transaction_common","",transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), "failed", null, "");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), "failed", null, "");
                    }
                }

                Hashtable hiddenVariables = new Hashtable();
                hiddenVariables.put("orderid",directKitValidatorVO.getTransDetailsVO().getOrderId());
                hiddenVariables.put("status","success".equalsIgnoreCase(transRespDetails.getStatus())?"Y":"F");
                hiddenVariables.put("statusDesc",transRespDetails.getDescription());
                hiddenVariables.put("trackingid", String.valueOf(trackingId));
                hiddenVariables.put("resAmount", directKitValidatorVO.getTransDetailsVO().getAmount());
                hiddenVariables.put("currency", directKitValidatorVO.getTransDetailsVO().getCurrency());
                request.setAttribute("hiddenResponse",hiddenVariables);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            else if (PaymentModeEnum.VOUCHERS_PAYMODE.ordinal() == paymenttype && CardTypeEnum.JETON_VOUCHER.ordinal() == cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","jetonVoucher.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }
                //System Check
                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);
                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                JetonResponseVO transRespDetails = null;
                JetonUtils jetonUtils = new JetonUtils();
                String billingDescriptor="";

                commRequestVO = jetonUtils.getJetonRequestVO(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails =(JetonResponseVO)pg.processSale(String.valueOf(trackingId), commRequestVO);

                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingId));
                        paymentManager.updateTransactionForCommon(transRespDetails,"capturesuccess",String.valueOf(trackingId),auditTrailVO,"transaction_common","",transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());
                        billingDescriptor=transRespDetails.getDescriptor();
                    }
                    else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails,"authfailed",String.valueOf(trackingId),auditTrailVO,"transaction_common","",transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());
                        billingDescriptor=transRespDetails.getDescriptor();
                    }
                }

                Hashtable hiddenVariables = new Hashtable();
                hiddenVariables.put("orderid",directKitValidatorVO.getTransDetailsVO().getOrderId());
                hiddenVariables.put("status","success".equalsIgnoreCase(transRespDetails.getStatus())?"Y":"F");
                hiddenVariables.put("statusDesc",transRespDetails.getDescription());
                hiddenVariables.put("trackingid", String.valueOf(trackingId));
                hiddenVariables.put("resAmount", directKitValidatorVO.getTransDetailsVO().getAmount());
                hiddenVariables.put("currency", directKitValidatorVO.getTransDetailsVO().getCurrency());
                hiddenVariables.put("billingDescriptor",billingDescriptor);
                request.setAttribute("hiddenResponse",hiddenVariables);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            else if(PaymentModeEnum.ACH.ordinal()==paymenttype && CardTypeEnum.ACH.ordinal()==cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","ccPayMitcoPage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);
                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForPayMitco(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                directKitValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.ACH.ordinal()));
                commRequestVO = payMitcoUtility.getPayMitcoRequestVO(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                String mailtransactionStatus="";
                String billingDiscriptor = "";
                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", String.valueOf(trackingId), auditTrailVO, "transaction_common", "");
                        directKitValidatorVO.setErrorMsg(transRespDetails.getDescription());
                        if(functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDiscriptor=transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDiscriptor = pg.getDisplayName();
                        }
                        directKitValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);

                        transactionUtils.calCheckSumAndStatusForSale(directKitValidatorVO, vtResponseVO, "Y");

                    }
                    else
                    {
                        paymentManager.updateTransactionForPayMitco(transRespDetails,"authfailed",String.valueOf(trackingId),auditTrailVO,"transaction_common","");
                        directKitValidatorVO.setErrorMsg(transRespDetails.getDescription());
                        transactionUtils.calCheckSumAndStatusForSale(directKitValidatorVO,vtResponseVO,"N");
                    }
                    /*if("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getEmailSent()))
                    {
                     */   Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null, billingDiscriptor);
                    /*}*/
                    Hashtable hiddenVariables = new Hashtable();
                    hiddenVariables.put("orderid",vtResponseVO.getOrderId());
                    hiddenVariables.put("status",vtResponseVO.getStatus());
                    hiddenVariables.put("statusDesc",vtResponseVO.getStatusDescription());
                    hiddenVariables.put("trackingid",vtResponseVO.getTrackingId());
                    hiddenVariables.put("resAmount",vtResponseVO.getResAmount());
                    hiddenVariables.put("checksum",vtResponseVO.getCheckSum());
                    hiddenVariables.put("currency",vtResponseVO.getCurrency());
                    hiddenVariables.put("billingDescriptor",vtResponseVO.getBillingDescriptor());
                    request.setAttribute("hiddenResponse",hiddenVariables);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                }

            }

            else if(PaymentModeEnum.CHK.ordinal()==paymenttype && CardTypeEnum.CHK.ordinal()==cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","ccPayMitcoPage.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);
                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForPayMitco(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                PayMitcoUtility payMitcoUtility = new PayMitcoUtility();
                directKitValidatorVO.setPaymentType(String.valueOf(PaymentModeEnum.CHK.ordinal()));
                commRequestVO = payMitcoUtility.getPayMitcoRequestVO(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                String mailtransactionStatus="";
                String billingDiscriptor = "";
                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForPayMitco(transRespDetails, "capturesuccess", String.valueOf(trackingId), auditTrailVO, "transaction_common", "");
                        directKitValidatorVO.setErrorMsg(transRespDetails.getDescription());
                        if(functions.isValueNull(transRespDetails.getDescriptor()))
                        {
                            billingDiscriptor=transRespDetails.getDescriptor();
                        }
                        else
                        {
                            billingDiscriptor = pg.getDisplayName();
                        }
                        directKitValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);

                        transactionUtils.calCheckSumAndStatusForSale(directKitValidatorVO, vtResponseVO, "Y");
                    }
                    else
                    {
                        paymentManager.updateTransactionForPayMitco(transRespDetails,"authfailed",String.valueOf(trackingId),auditTrailVO,"transaction_common","");
                        directKitValidatorVO.setErrorMsg(transRespDetails.getDescription());
                        transactionUtils.calCheckSumAndStatusForSale(directKitValidatorVO,vtResponseVO,"N");
                    }
                    /*if("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getEmailSent()))
                    {*/
                    Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    //sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null);
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null, billingDiscriptor);
                    /*}*/
                    Hashtable hiddenVariables = new Hashtable();
                    hiddenVariables.put("orderid",vtResponseVO.getOrderId());
                    hiddenVariables.put("status",vtResponseVO.getStatus());
                    hiddenVariables.put("statusDesc",vtResponseVO.getStatusDescription());
                    hiddenVariables.put("trackingid",vtResponseVO.getTrackingId());
                    hiddenVariables.put("resAmount",vtResponseVO.getResAmount());
                    hiddenVariables.put("checksum",vtResponseVO.getCheckSum());
                    hiddenVariables.put("currency",vtResponseVO.getCurrency());
                    hiddenVariables.put("billingDescriptor",vtResponseVO.getBillingDescriptor());
                    request.setAttribute("hiddenResponse",hiddenVariables);
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                }

            }

            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymenttype && CardTypeEnum.PAYSEC_CARDTYPE.ordinal()==cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(accountId));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO,"VT","");
                if(functions.isValueNull(error))
                {
                    request.setAttribute("error",error);
                    request.setAttribute("ccpage","inpaySpecificfields.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
               // int detailId = paymentManager.insertAuthStartedTransactionEntryForCommon(directKitValidatorVO,String.valueOf(trackingId),auditTrailVO);
                /*  Make Online Fraud Checking Using Fraud Processor
                *  Online Fraud checking is done only if merchant is active for online fraud check
                */

                FraudServiceDAO fraudServiceDAO=new FraudServiceDAO();
                PaySecUtils paySecUtils = new PaySecUtils();
                FraudAccountDetailsVO merchantFraudAccountVO=fraudServiceDAO.getMerchantFraudConfigurationDetails(directKitValidatorVO.getMerchantDetailsVO().getMemberId());
                if("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                {
                    directKitValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                    FraudChecker fraudChecker=new FraudChecker();
                    fraudChecker.checkFraudBasedOnMerchantFlagNew(directKitValidatorVO,merchantFraudAccountVO);
                   /* if(directKitValidatorVO.isFraud())
                    {
                        //Action To Be Taken
                    }*/
                }
                String html =paySecUtils.getPaySecRequest(directKitValidatorVO);
                request.setAttribute("html",html);
                RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            else if(PaymentModeEnum.NETBANKING_PAYMODE.ordinal()==paymenttype && CardTypeEnum.DIRECT_DEBIT.ordinal()==cardtype)
            {
                //System.out.println("Inside Online P4 VirtualSingleCall-----");
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT", "");
                if(error!=null && !error.equals(""))
                {
                    errorCodeVO=new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("VirtualSingleCall.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","p4Payment.jsp");
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken=" +user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                    return;
                }

                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                paymentManager.updateAuthStartedTransactionEntryForP4(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO, false);
                CommRequestVO commRequestVO = null;
                P4ResponseVO transRespDetails = null;
                P4Utils p4Utils= new P4Utils();
                commRequestVO = p4Utils.getRequestForOnlineBankTransfer(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails =(P4ResponseVO)pg.processAuthentication(String.valueOf(trackingId), commRequestVO);

                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updatePaymentIdForCommon(transRespDetails, String.valueOf(trackingId));
                        response.sendRedirect(transRespDetails.getFormularURL());
                        String html=generateAutoSubmitForm(transRespDetails.getFormularURL(),null);
                        request.setAttribute("html",html);
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/redirecting.jsp?ctoken=" +user.getCSRFToken());
                        requestDispatcher.forward(request, response);
                        return;
                    }
                    else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {

                        paymentManager.updateTransactionForCommon(transRespDetails,"authfailed",String.valueOf(trackingId),auditTrailVO,"transaction_common","",transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());//TODO Update query for the P4 as of sofort

                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), "failed", null, "");
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), "failed", null, "");

                        //PZExceptionHandler.raiseConstraintViolationException("SingleCallPayment.java","doPost()",null,"Transaction",transRespDetails.getRemark(),PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,null,null);
                    }

                    /*Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));*/
                }

                Hashtable hiddenVariables = new Hashtable();
                hiddenVariables.put("orderid", directKitValidatorVO.getTransDetailsVO().getOrderId());
                hiddenVariables.put("status", "success".equalsIgnoreCase(transRespDetails.getStatus()) ? "Y" : "F");
                hiddenVariables.put("statusDesc", transRespDetails.getDescription());
                hiddenVariables.put("trackingid", String.valueOf(trackingId));
                hiddenVariables.put("resAmount", directKitValidatorVO.getTransDetailsVO().getAmount());
                hiddenVariables.put("currency", directKitValidatorVO.getTransDetailsVO().getCurrency());
                //hiddenVariables.put("currency",vtResponseVO.getCurrency());
                //hiddenVariables.put("billingDescriptor",vtResponseVO.getBillingDescriptor());
                request.setAttribute("hiddenResponse",hiddenVariables);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            else if(PaymentModeEnum.SEPA.ordinal()==paymenttype && CardTypeEnum.DIRECT_DEBIT.ordinal()==cardtype)
            {
                //System.out.println("Inside Sepa P4 VirtualSingleCall-----");
                TokenManager tokenManager = new TokenManager();

                String mailtransactionStatus="";

                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT", "");
                if(functions.isValueNull(error))
                {
                    errorCodeVO=new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("VirtualSingleCall.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","p4SepaPayment.jsp");
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                    return;
                }

                directKitValidatorVO = transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);

                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.updateAuthStartedTransactionEntryForP4(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO,true);
                CommRequestVO commRequestVO = null;
                P4ResponseVO transRespDetails = null;

                String mandateId=directKitValidatorVO.getCardDetailsVO().getMandateId();

                P4Utils p4Utils= new P4Utils();
                commRequestVO = p4Utils.getRequestForSEPATransfer(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails =(P4ResponseVO)pg.processSale(String.valueOf(trackingId), commRequestVO);



                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL,String.valueOf(trackingId),auditTrailVO,"transaction_common","",transRespDetails.getTransactionId(),transRespDetails.getResponseTime(),transRespDetails.getRemark());

                        String mandatePk="";

                        if(!functions.isValueNull(mandateId))
                        {
                            request.setAttribute("mandateId",commRequestVO.getCardDetailsVO().getMandateId());
                            mandatePk=tokenManager.insertMandateForSEPA(commRequestVO.getCardDetailsVO().getMandateId(),toid,String.valueOf(trackingId),transRespDetails.getMandateURL(),transRespDetails.getRevokeMandateURL(),transRespDetails.isRecurring()) ;
                        }

                        tokenManager.insertSEPATransactionHistory(mandatePk,String.valueOf(trackingId));

                        mailtransactionStatus="Pending";
                        directKitValidatorVO.getCardDetailsVO().setMandateId(commRequestVO.getCardDetailsVO().getMandateId());

                    }
                    else if((transRespDetails.getStatus().trim()).equalsIgnoreCase("failed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, PZTransactionStatus.AUTH_FAILED.toString(), String.valueOf(trackingId), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                        mailtransactionStatus="Pending";

                    }


                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, transRespDetails.getRemark(), transRespDetails.getDescriptor());
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, transRespDetails.getRemark(), transRespDetails.getDescriptor());



                    /*Date date72 = new Date();
                    transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDiscriptor);
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
                    transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));*/
                }

                Hashtable hiddenVariables = new Hashtable();
                hiddenVariables.put("orderid", directKitValidatorVO.getTransDetailsVO().getOrderId());
                hiddenVariables.put("status", "Pending".equalsIgnoreCase(mailtransactionStatus)?"P":"F");
                hiddenVariables.put("statusDesc", transRespDetails.getDescription());
                hiddenVariables.put("trackingid", String.valueOf(trackingId));
                hiddenVariables.put("resAmount", directKitValidatorVO.getTransDetailsVO().getAmount());
                hiddenVariables.put("currency",vtResponseVO.getCurrency());
                hiddenVariables.put("billingDescriptor",vtResponseVO.getBillingDescriptor());
                request.setAttribute("hiddenResponse",hiddenVariables);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            else if(PaymentModeEnum.VOUCHERS_PAYMODE.ordinal()==paymenttype && CardTypeEnum.PAYSAFECARD_CARDTYPE.ordinal()==cardtype)
            {
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT","");
                if(error!=null && !error.equals(""))
                {
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","paysafespecificfields.jsp");
                    RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    rd.forward(request, response);
                    return;
                }

                transactionHelper.performCommonSystemChecksStep1(directKitValidatorVO);
                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                paymentManager.insertAuthStartedTransactionEntryForPaySafeCard(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = getPaySafeRequestVO(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingId), commRequestVO);

                if(transRespDetails!=null)
                {   log.error("paysafecard res-----> "+transRespDetails.getStatus()+"---"+transRespDetails.getDescription()+"---"+transRespDetails.getErrorCode());
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        String redirectUrl1=paysafecard.getString("CUSTOMER_CPANEL")+"mid="+commRequestVO.getCommMerchantVO().getMerchantId()+"&mtid="+trackingId+"&amount="+directKitValidatorVO.getTransDetailsVO().getAmount()+"&currency="+directKitValidatorVO.getTransDetailsVO().getCurrency();
                        //String html = generateAutoSubmitForm(redirectUrl1,null);
                        String html = generateAutoSubmitFormForPaySafeCard(redirectUrl1);
                        request.setAttribute("html",html);
                        RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request, response);
                    }
                    else
                    {   directKitValidatorVO.setErrorMsg(transRespDetails.getDescription());
                        transactionUtils.calCheckSumAndStatusForSale(directKitValidatorVO,vtResponseVO,"Fail");
                        Hashtable hiddenVariables = new Hashtable();
                        hiddenVariables.put("orderid",vtResponseVO.getOrderId());
                        hiddenVariables.put("status",vtResponseVO.getStatus());
                        hiddenVariables.put("statusDesc",vtResponseVO.getStatusDescription());
                        hiddenVariables.put("trackingid",vtResponseVO.getTrackingId());
                        hiddenVariables.put("resAmount",vtResponseVO.getResAmount());
                        hiddenVariables.put("checksum",vtResponseVO.getCheckSum());
                        hiddenVariables.put("currency",vtResponseVO.getCurrency());
                        hiddenVariables.put("billingDescriptor",vtResponseVO.getBillingDescriptor());
                        request.setAttribute("hiddenResponse",hiddenVariables);
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                        requestDispatcher.forward(request, response);
                    }
                }

            }
            else if(PaymentModeEnum.SEPA.ordinal()==paymenttype && CardTypeEnum.SEPA_EXPRESS.ordinal()==cardtype)
            {
                log.debug("inside B4 virtualsinglecall");
                transactionLogger.debug("Inside B4 virtual single call");

                String mailtransactionStatus="";
                AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "VT");
                if(functions.isValueNull(error))
                {
                    errorCodeVO=new ErrorCodeVO();
                    errorCodeVO.setErrorReason(error);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseAndHandleConstraintViolationException("VirtualSingleCall.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, toid, PZOperations.STANDARDKIT_SALE);
                    request.setAttribute("error", error);
                    request.setAttribute("ccpage","b4SepaExpress.jsp");
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                    return;
                }
                trackingId= paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                //For SepaExpress transactions don't have card details.
                /*String firstSix = "";
                if(!directKitValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                {
                    firstSix = functions.getFirstSix(directKitValidatorVO.getCardDetailsVO().getCardNum());
                }

                BinResponseVO binResponseVO = new BinResponseVO();
                binResponseVO = functions.getBinDetails(firstSix,directKitValidatorVO.getMerchantDetailsVO().getCountry());
                directKitValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                directKitValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                directKitValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                directKitValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                directKitValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                directKitValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                directKitValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                directKitValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());*/

                paymentManager.updateAuthStartedTransactionEntryForB4(directKitValidatorVO, String.valueOf(trackingId), auditTrailVO);
                CommRequestVO commRequestVO = null;
                TransactionResponse transRespDetails = null;

                B4Utils b4Utils = new B4Utils();
                commRequestVO = b4Utils.getRequestForB4SEPATransfer(directKitValidatorVO);
                pg =  AbstractPaymentGateway.getGateway(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                transRespDetails =(TransactionResponse)pg.processSale(String.valueOf(trackingId), commRequestVO);

                if(transRespDetails!=null)
                {
                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, String.valueOf(trackingId), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

                        mailtransactionStatus = "Success";
                        request.setAttribute("mandateId", commRequestVO.getCardDetailsVO().getMandateId());

                        Hashtable hiddenVariables = new Hashtable();
                        hiddenVariables.put("orderid",directKitValidatorVO.getTransDetailsVO().getOrderId());
                        hiddenVariables.put("status", "success".equalsIgnoreCase(transRespDetails.getStatus())?"Y":"F");
                        hiddenVariables.put("statusDesc", transRespDetails.getDescription());
                        hiddenVariables.put("trackingid", String.valueOf(trackingId));
                        hiddenVariables.put("resAmount", transRespDetails.getResult().get(0).getAmount());
                        hiddenVariables.put("currency", transRespDetails.getResult().get(0).getCurrencyCode());
                        hiddenVariables.put("billingDescriptor",transRespDetails.getDescriptor());
                        request.setAttribute("hiddenResponse",hiddenVariables);
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("authfailed"))
                    {
                        paymentManager.updateTransactionForCommon(transRespDetails, PZTransactionStatus.AUTH_FAILED.toString(), String.valueOf(trackingId), auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());
                        mailtransactionStatus = "Failed";
                        Hashtable hiddenVariables = new Hashtable();
                        hiddenVariables.put("orderid",directKitValidatorVO.getTransDetailsVO().getOrderId());
                        hiddenVariables.put("status", "success".equalsIgnoreCase(transRespDetails.getStatus())?"Y":"F");
                        hiddenVariables.put("statusDesc", transRespDetails.getDescription());
                        hiddenVariables.put("trackingid", String.valueOf(trackingId));
                        hiddenVariables.put("resAmount", directKitValidatorVO.getTransDetailsVO().getAmount());
                        hiddenVariables.put("currency", directKitValidatorVO.getTransDetailsVO().getCurrency());
                        hiddenVariables.put("billingDescriptor", "");
                        request.setAttribute("hiddenResponse",hiddenVariables);
                    }
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, transRespDetails.getRemark(), transRespDetails.getDescriptor());
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, transRespDetails.getRemark(), transRespDetails.getDescriptor());
                }
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            else if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymenttype || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymenttype) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardtype || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardtype) || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardtype || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardtype || CardTypeEnum.JCB.ordinal() == cardtype || CardTypeEnum.RUPAY.ordinal() == cardtype || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardtype || CardTypeEnum.MAESTRO.ordinal() == cardtype || CardTypeEnum.INSTAPAYMENT.ordinal() == cardtype)
            {
                request.setAttribute("ccpage", "ccpaymentPage.jsp");

                if (functions.isValueNull(request.getParameter("ccpage")))
                {
                    request.setAttribute("ccpage", request.getParameter("ccpage"));
                }

                GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(directKitValidatorVO.getMerchantDetailsVO().getAccountId());
                String gatewayName = gatewayAccount.getGateway();
                transactionLogger.error("Gateway-----" + gatewayName);
                if (!gatewayName.equals("decta"))
                {

                    CommonInputValidator commonInputValidator = new CommonInputValidator();
                    directKitValidatorVO = commonInputValidator.performVirtualTerminalValidation(directKitValidatorVO);
                    if (!functions.isEmptyOrNull(directKitValidatorVO.getErrorMsg()))
                    {
                        request.setAttribute("error", directKitValidatorVO.getErrorMsg());
                        RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(request, response);
                        return;
                    }

                    //System Check
                    trackingId = paymentManager.insertBegunTransactionEntry(directKitValidatorVO, header);
                    directKitValidatorVO.setTrackingid(String.valueOf(trackingId));

                    vtResponseVO = transactionUtils.singleCallVT(directKitValidatorVO, application);

                    if (vtResponseVO != null)
                    {
                        if (vtResponseVO.getStatus().equalsIgnoreCase("pending3DConfirmation"))
                        {

                            Hashtable hiddenVariables = new Hashtable();
                            hiddenVariables.put("orderid", vtResponseVO.getOrderId());
                            hiddenVariables.put("status", "P");
                            hiddenVariables.put("statusDesc", "Transaction is Pending");
                            hiddenVariables.put("trackingid", vtResponseVO.getTrackingId());
                            hiddenVariables.put("resAmount", vtResponseVO.getResAmount());
                            hiddenVariables.put("checksum", vtResponseVO.getCheckSum());
                            hiddenVariables.put("billingDescriptor", vtResponseVO.getBillingDescriptor());
                            hiddenVariables.put("currency", vtResponseVO.getCurrency());
                            request.setAttribute("html", vtResponseVO.getHtmlFormValue());
                            session.setAttribute("hiddenResponse", hiddenVariables);
                            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken=" + user.getCSRFToken());
                            requestDispatcher.forward(request, response);
                            return;
                        /*RequestDispatcher rd = request.getRequestDispatcher("/redirecting.jsp?ctoken="+user.getCSRFToken());
                        rd.forward(request, response);
                        return;*/
                        }
                        else
                        {
                            Hashtable hiddenVariables = new Hashtable();
                            hiddenVariables.put("orderid", vtResponseVO.getOrderId());
                            hiddenVariables.put("status", vtResponseVO.getStatus());
                            hiddenVariables.put("statusDesc", vtResponseVO.getStatusDescription());
                            hiddenVariables.put("trackingid", vtResponseVO.getTrackingId());
                            hiddenVariables.put("resAmount", vtResponseVO.getResAmount());
                            hiddenVariables.put("checksum", vtResponseVO.getCheckSum());
                            hiddenVariables.put("billingDescriptor", vtResponseVO.getBillingDescriptor());
                            hiddenVariables.put("currency", vtResponseVO.getCurrency());
                            request.setAttribute("hiddenResponse", hiddenVariables);
                            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/transactionSinglecall.jsp?ctoken=" + user.getCSRFToken());
                            requestDispatcher.forward(request, response);
                        }
                    }
                }
                else
                {
                    request.setAttribute("error", "This transaction can not be made using Virtual Terminal");
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken=" + user.getCSRFToken());
                    requestDispatcher.forward(request, response);
                    return;
                }
            }
            else{
                request.setAttribute("error","This transaction can not be made using Virtual Terminal");
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
                requestDispatcher.forward(request, response);
                return;
            }
            transactionLogger.debug("redirecting to transaction Single call...");
        }
        catch (PZConstraintViolationException cve)
        {
            transactionLogger.error("----PZConstraintViolationException in VirtualSingleCall------",cve);
            PZExceptionHandler.handleCVEException(cve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.VT_SALE);
            /*if(!functions.isEmptyOrNull(directKitValidatorVO.getErrorMsg()))
            {
                request.setAttribute("error", directKitValidatorVO.getErrorMsg());
            }
            else
            {
                request.setAttribute("error", cve.getPzConstraint().getMessage());
            }*/
            transactionLogger.error("----PZConstraintViolationException in VirtualSingleCall------"+errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO()));
            request.setAttribute("error", errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO()));
            RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (PZTechnicalViolationException tve)
        {
            transactionLogger.error("----PZConstraintViolationException in VirtualSingleCall------",tve);
            PZExceptionHandler.handleTechicalCVEException(tve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.VT_SALE);
            error = "Technical Error occured.Please contact your Admin";
            request.setAttribute("error", error);
            RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (PZGenericConstraintViolationException gve)
        {
            transactionLogger.error("---PZGenericConstraintViolationException in VirtualSingleCall----",gve);
            PZExceptionHandler.handleGenericCVEException(gve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.VT_SALE);
            error = "Error in communicating to Database.";
            request.setAttribute("error", error);
            RequestDispatcher rd = request.getRequestDispatcher("/virtualSingleCall.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }
        catch (NoSuchAlgorithmException nse)
        {
            transactionLogger.error("---NoSuchAlgorithmException in VirtualSingleCall---",nse);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("VirtualSingleCall.java","doPost()",null,"Merchant",directKitValidatorVO.getErrorMsg(), PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,null,null,toid,PZOperations.VT_SALE);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("---SystemError in VirtualSingleCall---", systemError);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private MerchantDetailsVO getMerchantConfigDetails(String toid) throws PZGenericConstraintViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();
        try
        {
            merchantDetailsVO = merchantDAO.getMemberDetails(toid);
        }
        catch (Exception e)
        {
            transactionLogger.error("Error while collecting merchant details from database",e);
            PZExceptionHandler.raiseGenericViolationException("VirtualSingleCall.java","getMerchantConfigDetails()",null,"Merchant","Error while collecting merchant details from database:::",null,e.getMessage(),e.getCause());

        }
        return merchantDetailsVO;
    }

    /*private int insertFirstTransactionEntry(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        int trackingId=0;
        Transaction transaction=new Transaction();
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        String tablename = Database.getTableName(gatewayType.getGateway());
        if(accountId!=null && tablename!=null)
        {
            if(tablename.equals("transaction_qwipi"))
            {
                trackingId = transaction.insertTranseQwipi(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getTransDetailsVO().getTotype(),commonValidatorVO.getTransDetailsVO().getFromid(),commonValidatorVO.getTransDetailsVO().getFromtype(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getRedirectUrl(),"begun",commonValidatorVO.getMerchantDetailsVO().getAccountId(), Integer.parseInt(commonValidatorVO.getPaymentType()), Integer.parseInt(commonValidatorVO.getCardType()),commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getTransDetailsVO().getHeader(),commonValidatorVO.getAddressDetailsVO().getIp(),commonValidatorVO.getTerminalId());
            }
            else if(tablename.equals("transaction_ecore"))
            {
                trackingId = transaction.insertTransEcore(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getTransDetailsVO().getFromid(),commonValidatorVO.getTransDetailsVO().getFromtype(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getRedirectUrl(),commonValidatorVO.getMerchantDetailsVO().getAccountId(),Integer.parseInt(commonValidatorVO.getPaymentType()), Integer.parseInt(commonValidatorVO.getCardType()),commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getTransDetailsVO().getHeader(),commonValidatorVO.getAddressDetailsVO().getIp(),commonValidatorVO.getTerminalId());
            }
            else if(tablename.equals("transaction_common"))
            {
                trackingId = transaction.insertTransCommon(commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getTransDetailsVO().getTotype(),commonValidatorVO.getTransDetailsVO().getFromid(),commonValidatorVO.getTransDetailsVO().getFromtype(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getRedirectUrl(),commonValidatorVO.getMerchantDetailsVO().getAccountId(),Integer.parseInt(commonValidatorVO.getPaymentType()), Integer.parseInt(commonValidatorVO.getCardType()),commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getTransDetailsVO().getHeader(),commonValidatorVO.getAddressDetailsVO().getIp());
            }
        }
        return trackingId;
    }*/

    private String getCupRequest(CommonValidatorVO commonValidatorVO,int detailId) throws PZDBViolationException
    {
        String gatewayPayUrl;
        String securityKey;
        String transType="";
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        if ("Y".equals(commonValidatorVO.getMerchantDetailsVO().getService()))  {
            transType = "01";
        }
        else{
            transType = "02";
        }
        CupUtils cupUtils=new CupUtils();
        HashMap accountDetail= cupUtils.getAccountDetails(commonValidatorVO.getMerchantDetailsVO().getAccountId(),account.getCurrency());

        if(accountDetail.get("istestaccount").equals("N")){
            /* Production environment */
            gatewayPayUrl = CupUtils.livePayUrl;
            securityKey = CupUtils.liveSecurityKey;
        }
        else{
            /* Test environment */
            gatewayPayUrl = CupUtils.testPayUrl;
            securityKey = CupUtils.testSecurityKey;
        }

        String[] valueVo = new String[]{
                CupUtils.version,//Protocol version
                CupUtils.charset,//Character Encoding
                transType,//Transaction type
                "",//The original transaction serial number
                (String)accountDetail.get("merchantid"),//Merchant code
                (String)accountDetail.get("displayname"),//Merchant short name
                CupUtils.acqCode,//Acquirer code (Only need fill when acquirer access in)
                (String)accountDetail.get("merchantcategorycode"),//Merchant category (Acquirer need fill when access in)
                "",//Product URL
                "",//Product name
                "",//Product unit price, unit: Fen
                "",//Product quantity
                "",//Discount, unit: Fen
                "",//Shipping fee, unit: Fen
                String.valueOf(detailId),//Order Number (Requires merchants to generate)
                CupUtils.convertDollarToCents(commonValidatorVO.getTransDetailsVO().getAmount()),//Amount of the transaction, unit: Fen
                (String)accountDetail.get("currencycode"),//Transaction currency
                CupUtils.getBeijingTime(),//Transaction time
                commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(),//User IP
                "",//User real name
                "",//Default payment
                "",//Default bank code
                CupUtils.transTimeout,//Transaction timeout
                RB.getString("merFrontEndUrl"),//Frontend callback merchant URL
                CupUtils.merBackEndUrl,//Backend callback URL
                ""//Merchant reserved fields{orderTimeoutDate=20140912172500}
        };

        String signType =null;
        if (!CupUtils.signType_SHA1withRSA.equalsIgnoreCase(RB.getString("signMethod"))) {
            signType = CupUtils.signType_MD5;
        }
        else{
            signType =CupUtils.signType_SHA1withRSA;
        }

        String html = CupUtils.createPayHtmlinNewTab(valueVo, signType, gatewayPayUrl, securityKey);//redirect to UnionPay website for payment
        transactionLogger.error(html);
        return html;
    }

    private String getInPayRequest(CommonValidatorVO commonValidatorVO)
    {
        final  String MERCHANTID = "merchant_id";
        final  String ORDERID = "order_id";
        final  String AMOUNT = "amount";
        final  String CURRENCY = "currency";
        final  String ORDERTEXT = "order_text";
        final  String FLOWLAYOUT = "flow_layout";
        final  String RETURNURL = "return_url";
        final  String EMAILID = "buyer_email";
        final  String CHECKSUM = "checksum";
        final  String COUNTRY = "country";
        final  String NAME = "buyer_name";
        final  String ADDRESS = "buyer_address";
       // final  String PENDINGURL = "pending_url";
       // final  String CANCELURL = "cancel_url";
       // final  String NOTIFYURL = "notify_url";
        final  String testURL = "https://test-secure.inpay.com";
        final  String livetURL = "https://secure.inpay.com";
        String URL = "";

        boolean isTest = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).isTest();

        if(isTest)
        {
            URL = testURL;
        }
        else
        {
            URL = livetURL;
        }

        String flowLayout = "multi_page";
        InPayAccount inPayAccount = new InPayAccount();
        Hashtable dataHash = null;

        dataHash = inPayAccount.getMidAndSecretKey(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merchantid = (String) dataHash.get("mid");
        String secratKey = (String) dataHash.get("secretkey");
        String cMerchantid = URLEncoder.encode(merchantid);
        String cOrderid = URLEncoder.encode(commonValidatorVO.getTrackingid());
        String cAmount = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getAmount());
        String cCurrency = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getCurrency());
        String cOrderText = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId());
        String cFlowLayout = URLEncoder.encode(flowLayout);
        String cSecretKey = URLEncoder.encode(secratKey);
        String address=commonValidatorVO.getAddressDetailsVO().getStreet()+","+commonValidatorVO.getAddressDetailsVO().getCity()+","+commonValidatorVO.getAddressDetailsVO().getState()+","+commonValidatorVO.getAddressDetailsVO().getCountry();
        String checksumString = "merchant_id="+cMerchantid+"&order_id="+cOrderid+"&amount="+cAmount+"&currency="+cCurrency+"&order_text="+cOrderText+
                "&flow_layout="+cFlowLayout+"&secret_key="+cSecretKey;
        //log.debug("chksum string---"+checksumString);

        Map saleMap = new TreeMap();

        saleMap.put(MERCHANTID,merchantid);
        saleMap.put(ORDERID,commonValidatorVO.getTrackingid());
        saleMap.put(AMOUNT,commonValidatorVO.getTransDetailsVO().getAmount());
        saleMap.put(CURRENCY,commonValidatorVO.getTransDetailsVO().getCurrency());
        saleMap.put(COUNTRY,commonValidatorVO.getAddressDetailsVO().getCountry());
        saleMap.put(ORDERTEXT,commonValidatorVO.getTransDetailsVO().getOrderId());
        saleMap.put(FLOWLAYOUT,flowLayout);
        saleMap.put(RETURNURL,RB1.getString("FRONTEND"));
        saleMap.put(EMAILID,commonValidatorVO.getAddressDetailsVO().getEmail());
        saleMap.put(ADDRESS,address);
        saleMap.put(NAME,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
        saleMap.put(CHECKSUM,getMD5HashVal(checksumString));

        String redirectHTML = generateAutoSubmitForm(URL,saleMap);
        //log.debug("chksum string---"+singleCallPaymentDAO.getMD5HashVal(checksumString));
        return redirectHTML;
    }

    private CommRequestVO getPaySafeRequestVO(CommonValidatorVO commonValidatorVO)
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        merchantAccountVO.setAliasName(commonValidatorVO.getMerchantDetailsVO().getSiteName());

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);

        return commRequestVO;
    }

}