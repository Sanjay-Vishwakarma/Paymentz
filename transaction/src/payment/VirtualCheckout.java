package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.PartnerManager;
import com.manager.PaymentManager;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.MarketPlaceVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Rihen on 9/16/2019.
 */
public class VirtualCheckout extends HttpServlet
{

    private static Logger log = new Logger(VirtualCheckout.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(VirtualCheckout.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);

    SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("entering in VirtualCheckout----");
        Enumeration enumeration=req.getParameterNames();
        while(enumeration.hasMoreElements())
        {
            String key=(String)enumeration.nextElement();
            String value=(String)req.getParameter(key);
            transactionLogger.error(key +"---" + value);
        }

        String error = "";
        String errorName = "";
        CommonValidatorVO standardKitValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();

        Functions functions = new Functions();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        TransactionHelper transactionHelper = new TransactionHelper();
        PartnerManager partnerManager = new PartnerManager();
        PaymentManager paymentManager = new PaymentManager();
        TerminalManager terminalManager = new TerminalManager();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        String remoteAddr = Functions.getIpAddress(req);
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();
        String httpProtocol = req.getScheme();
        String userAgent = req.getHeader("User-Agent");
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath + ",User-Agent="+userAgent;
        String hostName = httpProtocol + "://" + remoteAddr;
        int paymentType = 0;
        int cardType = 0;
        int trackingId = 0;
        int child_trackingid=0;

        String billingDiscriptor = "";

        PrintWriter pWriter = res.getWriter();
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");


        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        try
        {
            String memberId = "";
            String totype = "";
            String currency = "";
            String amount = "0.00";
            String redirectUrl = "https://ife.pz.com/TestApp/redirecturl.jsp";
            if(functions.isValueNull(req.getParameter("memberId"))) {
                String URLdecoded=ESAPI.encoder().decodeFromURL(req.getParameter("memberId"));
                String decoded = URLdecoded.replaceAll(" ","+");
                memberId = PzEncryptor.decryptName(decoded);
//                memberId = req.getParameter("memberId");
            }
            if(functions.isValueNull(req.getParameter("totype"))) {
                totype = req.getParameter("totype");
            }
            if(functions.isValueNull(req.getParameter("currency"))) {
                currency = req.getParameter("currency");
            }
            
            if(functions.isValueNull(memberId))
            {
                merchantDetailsVO.setMemberId(memberId);
                merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            }
            String orderId = memberId+"_"+functions.getAutoGeneratedOrderId(memberId);
            genericTransDetailsVO.setTotype(totype);
            genericTransDetailsVO.setOrderId(orderId);
            genericTransDetailsVO.setAmount(amount);
            genericTransDetailsVO.setRedirectUrl(redirectUrl);
            if(functions.isValueNull(currency)) {
                genericTransDetailsVO.setCurrency(currency);
                genericAddressDetailsVO.setTmpl_currency(currency);
            }

            genericAddressDetailsVO.setRequestedHeader(header);
            genericAddressDetailsVO.setRequestedHost(hostName);
            genericAddressDetailsVO.setIp(remoteAddr);

            standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            standardKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            //standardKitValidatorVO = ReadRequest.getSTDProcessControllerRequestParametersForSale(req);
            standardKitValidatorVO.setVersion("2");
            standardKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

            if (standardKitValidatorVO == null)
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REQUEST_NULL);
                errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_DETAILS);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_PAYMENT_DETAILS, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            //Member id validation for partner
            if (!functions.isValueNull(memberId) || !functions.isNumericVal(memberId) || memberId.length() > 10 || !ESAPI.validator().isValidInput("memberId", memberId, "Numbers", 10, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                error = errorCodeVO.getErrorDescription();

                transactionLogger.debug("error----" + error+"---"+errorCodeVO.getErrorCode());
                errorCodeListVO.addListOfError(errorCodeVO);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }
            if (!functions.isValueNull(totype) || totype.length() > 30 || !ESAPI.validator().isValidInput("totype", totype, "organizationName", 30, false))
            {
                ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOTYPE);
                errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                error = errorCodeVO.getErrorDescription();
                errorCodeListVO.addListOfError(errorCodeVO);
                log.debug("totype error----" + errorCodeListVO.getListOfError().get(0).getErrorReason());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(standardKitValidatorVO, error, ErrorName.VALIDATION_TOTYPE.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_TOTYPE, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
            }

            User user = new DefaultUser(memberId);
            ESAPI.authenticator().setCurrentUser(user);
            HttpSession session = req.getSession();
            session.setAttribute("ctoken", ctoken);
            session.setAttribute("version","2");
             req.setAttribute("ctoken", ctoken);

            //validation
            standardKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
            standardKitValidatorVO = commonInputValidator.performVTCheckoutValidations(standardKitValidatorVO, session);
            transactionLogger.debug("merchant logo from VirtualCheckout-----" + standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());

            if (functions.isValueNull(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()))
            {
                session.setAttribute("merchantLogoName", standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName());
            }
            String headerVal = "";
            String merchantSiteName = standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain();

            transactionLogger.error("merchantSiteName PayProcessControl---" + merchantSiteName);
            transactionLogger.error("X-Frame-Options from session---" + session.getAttribute("X-Frame-Options"));
            transactionLogger.error("REFERER from header---" + req.getHeader("referer"));

            if (functions.isValueNull((String) session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String) session.getAttribute("X-Frame-Options")))
            {
                transactionLogger.error("X-Frame-Options from IF---" + session.getAttribute("X-Frame-Options"));
                res.setHeader("X-Frame-Options", (String) session.getAttribute("X-Frame-Options"));
            }
            else
            {
                String responseOrigin = req.getHeader("referer");
                if (functions.isValueNull(responseOrigin) && functions.isValueNull(merchantSiteName) /*&& responseOrigin.contains(merchantSiteName)*/)
                {
                    transactionLogger.error("from merchantSiteName---" + merchantSiteName);
                    headerVal = functions.getIFrameHeaderValue(responseOrigin, merchantSiteName);
                    if ("SAMEORIGIN".equals(headerVal))
                    {
                        res.setHeader("X-Frame-Options", headerVal);
                        session.setAttribute("X-Frame-Options", headerVal);
                    }
                    transactionLogger.error("X-Frame-Options merchantSiteName---" + headerVal);
                }
                else
                {
                    headerVal = "SAMEORIGIN";
                    res.setHeader("X-Frame-Options", headerVal);
                    session.setAttribute("X-Frame-Options", headerVal);
                    transactionLogger.error("X-Frame-Options from else---" + headerVal);
                }
                transactionLogger.error("origin PayProcessControl---" + responseOrigin);
            }

            res.setHeader("Cache-control", "no-store"); //HTTP 1.1
            res.setHeader("Pragma","no-cache"); //HTTP1.0
            res.setDateHeader("Expire", 0); //prevents caching at the proxy server
            res.setCharacterEncoding("UTF-8");
            res.setContentType("text/html");
            transactionLogger.error("final header value from session---" + session.getAttribute("X-Frame-Options"));

            if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
            {
                PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, standardKitValidatorVO.getErrorCodeListVO(), null, null);
            }

            /*if (standardKitValidatorVO != null)
            {
                transactionLogger.debug("Amount-----" + standardKitValidatorVO.getTransDetailsVO().getAmount());
                transactionLogger.debug("Tmpl_Amount-----" + standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount());
                if ((functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getAmount())))
                {
                    standardKitValidatorVO.getTransDetailsVO().setAmount(Functions.roundOff(standardKitValidatorVO.getTransDetailsVO().getAmount())); //Amount * 100 according to the docs
                }
                if ((functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount())))
                {
                    standardKitValidatorVO.getAddressDetailsVO().setTmpl_amount(Functions.roundOff(standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount()));
                }
            }*/

            //transactionHelper.checksumValidationforSK(standardKitValidatorVO);

            //setting white lable template page
            //getPaymentPageTemplateDetails(standardKitValidatorVO, session);

            transactionLogger.debug("is processed from pay process---" + req.getParameter("isProcessed"));
            if ((!functions.isValueNull(req.getParameter("isProcessed")) || req.getParameter("isProcessed").equalsIgnoreCase("f")))
            {
                transactionLogger.debug("inside is processed if trackingid got---");
//                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep0(standardKitValidatorVO);
//                if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
//                {
//                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
//                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
//                    errorCodeListVO.addListOfError(errorCodeVO);
//                    PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
//                }

                partnerManager.getPartnerDetailFromMemberId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), standardKitValidatorVO);
                standardKitValidatorVO.getTransDetailsVO().setTotype(standardKitValidatorVO.getPartnerName());

                //Begun Transaction Entry
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId(memberId);
                auditTrailVO.setActionExecutorName("VirtualCheckout");
                //if request comes from invoice, remark add to DB
                if (functions.isValueNull(standardKitValidatorVO.getInvoiceId()))
                {
                    standardKitValidatorVO.setReason("Payment is initiated with InvoiceNO:" + standardKitValidatorVO.getInvoiceId());
                }
                if(!"split".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                {
                    trackingId = paymentManager.insertBegunTransactionEntry(standardKitValidatorVO, auditTrailVO, header);

                    if (trackingId != 0)
                    {
                        standardKitValidatorVO.setTrackingid(String.valueOf(trackingId));
                    }
                    else
                    {
                        PZExceptionHandler.raiseGenericViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", ErrorMessages.DB_COMMUNICATION, null, null, null);
                    }
                }
                if(!"N".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()))
                {
                    String totalamount=standardKitValidatorVO.getTransDetailsVO().getAmount();
                    if(standardKitValidatorVO.getMarketPlaceVOList()!=null)
                    {
                        List<MarketPlaceVO> mpDetailsList1=standardKitValidatorVO.getMarketPlaceVOList();
                        List<MarketPlaceVO> mpDetailsList=new ArrayList<>();
                        for (int i=0;i<mpDetailsList1.size();i++)
                        {
                            marketPlaceVO=mpDetailsList1.get(i);
                            standardKitValidatorVO.getMerchantDetailsVO().setMemberId(marketPlaceVO.getMemberid());
                            standardKitValidatorVO.getTransDetailsVO().setAmount(marketPlaceVO.getAmount());
                            standardKitValidatorVO.getTransDetailsVO().setOrderId(marketPlaceVO.getOrderid());
                            standardKitValidatorVO.getTransDetailsVO().setOrderDesc(marketPlaceVO.getOrderDesc());
                            child_trackingid=paymentManager.insertBegunTransactionEntryForMarketPlace(standardKitValidatorVO, auditTrailVO, header,marketPlaceVO.getMerchantDetailsVO().getPartnerName());
                            marketPlaceVO.setTrackingid(String.valueOf(child_trackingid));
                            mpDetailsList.add(i,marketPlaceVO);
                        }
                        standardKitValidatorVO.getMerchantDetailsVO().setMemberId(memberId);
                        standardKitValidatorVO.getTransDetailsVO().setAmount(totalamount);
                        standardKitValidatorVO.getTransDetailsVO().setOrderId(orderId);
                        standardKitValidatorVO.setMarketPlaceVOList(mpDetailsList);
                    }
                }
            }

            if ((functions.isValueNull(standardKitValidatorVO.getPaymentType()) && functions.isValueNull(standardKitValidatorVO.getCardType())) || functions.isValueNull(standardKitValidatorVO.getTerminalId()))
            {
                standardKitValidatorVO = transactionHelper.performCommonSystemChecksForSKStep1(standardKitValidatorVO);

                standardKitValidatorVO = commonInputValidator.validateResfield1(standardKitValidatorVO, "STDKIT");

                if (!functions.isEmptyOrNull(standardKitValidatorVO.getErrorMsg()))
                {
                    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                    errorCodeVO.setErrorReason(standardKitValidatorVO.getErrorMsg());
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", standardKitValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null);
                }

                String fromtype = standardKitValidatorVO.getTransDetailsVO().getFromtype();

                if (fromtype == null)
                {
                    PZExceptionHandler.raiseGenericViolationException("VirtualCheckout.class", "doPost()", null, "Transaction", ErrorMessages.INVALID_FROMTYPE, null, "", null);
                }

                String autoRedirectRequest = standardKitValidatorVO.getTerminalVO().getAutoRedirectRequest();
                if (autoRedirectRequest.equals("Y") && functions.isValueNull(standardKitValidatorVO.getTerminalId()) && standardKitValidatorVO.getTerminalVO().getAddressValidation().equals("N"))
                {
                    AuditTrailVO auditTrailVO = new AuditTrailVO();
                    auditTrailVO.setActionExecutorId(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    auditTrailVO.setActionExecutorName("VirtualCheckout");
                    //MerchantDetailsVO merchantDetailsVO = standardKitValidatorVO.getMerchantDetailsVO();
//                    MerchantDAO merchantDAO = new MerchantDAO();
//                    merchantDetailsVO = merchantDAO.getMemberDetails(standardKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    merchantDetailsVO.setAccountId(standardKitValidatorVO.getMerchantDetailsVO().getAccountId());
                    standardKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

                    AbstractPaymentGateway pg = null;
                    pg = AbstractPaymentGateway.getGateway(merchantDetailsVO.getAccountId());

                    //Gateway Specific Validation
                    AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()));
                    String addressValidation = GatewayAccountService.getGatewayAccount(standardKitValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
                    error = paymentProcess.validateIntegrationSpecificParameters(standardKitValidatorVO, "STDKIT", addressValidation);
                    if (error != null && !error.equals(""))
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        errorCodeVO.setErrorReason(error);
                        errorCodeListVO.addListOfError(errorCodeVO);
                        PZExceptionHandler.raiseAndHandleConstraintViolationException("SingleCallPayment.java", "doPost()", null, "Transaction", error, PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, null, null, memberId, PZOperations.STANDARDKIT_SALE);
                        req.setAttribute("error", error);
                        req.setAttribute("transDetails", standardKitValidatorVO);
                        session.setAttribute("ctoken", ctoken);
                        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
                        requestDispatcher.forward(req, res);
                        return;
                    }

                    PaymentManager.insertAuthStartedTransactionEntryForCommon(standardKitValidatorVO, standardKitValidatorVO.getTrackingid(), auditTrailVO);

                    String html = pg.processAutoRedirect(standardKitValidatorVO);

                    if(functions.isValueNull(html) && (html.equalsIgnoreCase("success") || html.equalsIgnoreCase("failed")))
                    {
                        TransactionUtility transactionUtility = new TransactionUtility();
                        if (functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getNotificationUrl()))
                        {
                            transactionLogger.error("inside sending notification---" + standardKitValidatorVO.getTransDetailsVO().getNotificationUrl());
                            standardKitValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDiscriptor);
                            TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(standardKitValidatorVO);
                            AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                            asyncNotificationService.sendNotification(transactionDetailsVO1, standardKitValidatorVO.getTrackingid(), html, html,"");
                        }
                        if (standardKitValidatorVO.getMerchantDetailsVO().getAutoRedirect().equals("N"))
                        {
                            req.setAttribute("transDetail", standardKitValidatorVO);
                            req.setAttribute("errorName", errorName);
                            req.setAttribute("responceStatus", html);
                            req.setAttribute("displayName", billingDiscriptor);
                            req.setAttribute("ctoken", ctoken);
                            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/confirmationCheckout.jsp?ctoken=" + ctoken);
                            requestDispatcher.forward(req, res);
                            return;
                        }
                        else
                        {
                            try
                            {
                                transactionUtility.doAutoRedirect(standardKitValidatorVO, res, html, billingDiscriptor);
                            }
                            catch (SystemError systemError)
                            {
                                log.error("System Error while redirecting to redirect url", systemError);
                            }
                        }
                    }
                    else
                    {
                        pWriter.println(html);
                        return;
                    }
                }
                else
                {
                    req.setAttribute("transDetails", standardKitValidatorVO);
                    req.setAttribute("ctoken", session.getAttribute("ctoken"));
                    req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                    req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                    RequestDispatcher rd = req.getRequestDispatcher("/virtualCheckoutPayment.jsp?ctoken=" + ctoken);
                    rd.forward(req, res);
                    return;
                }
            }
            else
            {
                LimitChecker limitChecker=new LimitChecker();
                //merchantDetailsVO=standardKitValidatorVO.getMerchantDetailsVO();
                System.out.println("merch limit ---------"+merchantDetailsVO.getCheckLimit());
                if (merchantDetailsVO.getCheckLimit().equals("1"))
                {
                    //TODO : Merchant amount limit check
                    Date date3 = new Date();
                    transactionLogger.debug("TransactionHelper checkAmountLimit start #########" + date3.getTime());
                    limitChecker.checkAmountLimit(standardKitValidatorVO);
                    transactionLogger.debug("TransactionHelper checkAmountLimit end #########" + new Date().getTime());
                    transactionLogger.debug("TransactionHelper checkAmountLimit diff #########" + (new Date().getTime() - date3.getTime()));
                }
                req.setAttribute("transDetails", standardKitValidatorVO);
                req.setAttribute("ctoken", session.getAttribute("ctoken"));
                req.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
                req.setAttribute("cardtype", standardKitValidatorVO.getCardType());
                RequestDispatcher rd = req.getRequestDispatcher("/virtualCheckoutPayment.jsp?ctoken=" + ctoken);
                rd.forward(req, res);
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("-------PZConstraintViolationException in VirtualCheckout------", cve);
            transactionLogger.error("PZConstraintViolationException occured ", cve);
            error = errorCodeUtils.getSystemErrorCodesVO(cve.getPzConstraint().getErrorCodeListVO());
            if (standardKitValidatorVO != null && functions.isValueNull(standardKitValidatorVO.getTrackingid()))
            {
                errorName = errorCodeUtils.getErrorName(cve.getPzConstraint().getErrorCodeListVO().getListOfError().get(0));
                try
                {
                    paymentManager.updateDetailsTablewithErrorName(errorName, standardKitValidatorVO.getTrackingid());
                }
                catch (PZDBViolationException d)
                {
                    log.error("----PZDBViolationException in update with error name-----", d);
                }
            }
            PZExceptionHandler.handleCVEException(cve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error);
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dve)
        {
            log.error("----PZDBViolationException in VirtualCheckout-----", dve);
            error = "Internal Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleDBCVEException(dve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + dve.getPzdbConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + dve.getPzdbConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZTechnicalViolationException tve)
        {
            log.error("----PZTechnicalViolationException in VirtualCheckout-----", tve);
            error = "Technical Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleTechicalCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + tve.getPzGenericConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (PZGenericConstraintViolationException tve)
        {
            log.error("----PZGenericConstraintViolationException in VirtualCheckout-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            PZExceptionHandler.handleGenericCVEException(tve, standardKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.STANDARDKIT_SALE);
            req.setAttribute("error", error + " : " + tve.getPzGenericConstraint().getMessage());
            standardKitValidatorVO.setErrorMsg(error + " : " + tve.getPzGenericConstraint().getMessage());
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (SystemError tve)
        {
            log.error("----PZGenericConstraintViolationException in VirtualCheckout-----", tve);
            error = "Generic Errror Occured : Please contact Customer support for further help<BR>";
            req.setAttribute("transDetail", standardKitValidatorVO);
            RequestDispatcher rd = req.getRequestDispatcher("/checkoutError.jsp?ctoken=" + ctoken);
            rd.forward(req, res);
            return;
        }
        catch (Exception e)
        {
            log.error("----PZGenericConstraintViolationException in VirtualCheckout-----", e);
        }
    }
}