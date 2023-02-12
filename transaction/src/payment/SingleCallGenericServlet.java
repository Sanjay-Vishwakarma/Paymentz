package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.enums.ResponseLength;
import com.manager.helper.TransactionHelper;
import com.manager.vo.DirectCommResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.checkers.PaymentChecker;
import com.payment.checkers.RiskCheckers;
import com.payment.epay.EpayPaymentGateway;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.inpay.InPayPaymentGateway;
import com.payment.jeton.JetonPaymentGateway;
import com.payment.jeton.JetonVoucherPaymentGateway;
import com.payment.neteller.NetellerPaymentGateway;
import com.payment.perfectmoney.PerfectMoneyPaymentGateway;
import com.payment.skrill.SkrillPaymentGateway;
import com.payment.statussync.StatusSyncDAO;
import com.payment.trustly.TrustlyPaymentGateway;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.transaction.utils.TransactionCoreUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import payment.util.ReadRequest;
import payment.util.ReadXMLRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;

/*
 * Created by IntelliJ IDEA.
 * User: Jimmy Mehta
 * Date: Dec 31, 2012
 * Time: 4:36:45 PM
 * To change this template use File | Settings | File Templates. */


public class SingleCallGenericServlet extends PzServlet
{
    static String defaultchargepercent = "500";
    static String INR_defaulttaxpercent = "1224";
    static String USD_defaulttaxpercent = "1224";
    private static Logger log = new Logger(SingleCallGenericServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallGenericServlet.class.getName());
    private static Functions functions = new Functions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Date date1 = new Date();
        transactionLogger.debug("SingleCallGenericServlet doPost Start time 1#######"+date1.getTime());
        TransactionUtility transactionError = new TransactionUtility();
        PrintWriter pWriter = res.getWriter();
        CommonValidatorVO directKitValidatorVO=new CommonValidatorVO();
        TransactionHelper transactionHelper=new TransactionHelper();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        String requesttype = req.getParameter("requesttype");
        String error="";
        String toid=null;
        String key="";
        String algoName="";
        String orderId="";
        String amount1="0.00";
        CommonInputValidator commonInputValidator=new CommonInputValidator();
        String remoteAddr = Functions.getIpAddress(req);
        String httpProtocol=req.getScheme();
        int serverPort = req.getServerPort();
        String servletPath = req.getServletPath();

        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        String hostName=httpProtocol+"://"+remoteAddr;

        if("XML".equalsIgnoreCase(requesttype))
        {
            String XMLData = req.getParameter("data");
            if(XMLData==null || XMLData.equals(""))
            {
                transactionError.calCheckSumAndWriteStatusForSale(pWriter,null, null, "", "N", "Invalid Data", null, null,requesttype,null,"",directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                return;
            }
            directKitValidatorVO = ReadXMLRequest.readXmlRequestForSale(XMLData);
        }
        else
        {

            directKitValidatorVO = ReadRequest.getRequestParametersForSale(req);

        }

        if(directKitValidatorVO!=null)
        {
            try
            {
                User user = new DefaultUser(directKitValidatorVO.getMerchantDetailsVO().getMemberId());
                ESAPI.authenticator().setCurrentUser(user);
                directKitValidatorVO.getAddressDetailsVO().setRequestedHeader(header);
                directKitValidatorVO.getAddressDetailsVO().setRequestedHost(hostName);

                directKitValidatorVO = commonInputValidator.performDirectKitValidation(directKitValidatorVO);

                //log.error("validation Check --"+directKitValidatorVO.getErrorMsg());
                if (directKitValidatorVO.getErrorMsg() != null && directKitValidatorVO.getErrorMsg().trim().length() > 0)
                {

                    key = directKitValidatorVO.getMerchantDetailsVO().getKey();
                    algoName = directKitValidatorVO.getMerchantDetailsVO().getChecksumAlgo();
                    orderId = directKitValidatorVO.getTransDetailsVO().getOrderId();
                    amount1 = directKitValidatorVO.getTransDetailsVO().getAmount();
                    if(ResponseLength.FULL.toString().equals(directKitValidatorVO.getMerchantDetailsVO().getResponseLength()))
                    {

                        transactionError.calCheckSumAndWriteSaleFullResponse(pWriter,orderId, "N",directKitValidatorVO.getErrorMsg(),"", String.valueOf(amount1), "", "", "", "", "", "", "", "", "","","", "", "", "", "", key, algoName, directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                        return;
                    }
                    else
                    {

                        transactionError.calCheckSumAndWriteStatusForSale(pWriter, "", orderId, amount1, "N", directKitValidatorVO.getErrorMsg(), key, algoName, requesttype, null, "",directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                        return;
                    }

                }
                else
                {

                    singleCall(directKitValidatorVO, req, res, pWriter);

                }
            }
            /*catch (PZTechnicalViolationException tve)
            {
                log.error("PZTechnicalViolationException in SingleCallGenericServlet---",tve);
                transactionLogger.error("PZTechnicalViolationException in SingleCallGenericServlet---",tve);
                PZExceptionHandler.handleTechicalCVEException(tve,toid, PZOperations.DIRECTKIT_SALE);

                key = directKitValidatorVO.getMerchantDetailsVO().getKey();
                algoName = directKitValidatorVO.getMerchantDetailsVO().getChecksumAlgo();
                orderId = directKitValidatorVO.getTransDetailsVO().getOrderId();
                amount1 = directKitValidatorVO.getTransDetailsVO().getAmount();

                transactionError.calCheckSumAndWriteStatusForSale(pWriter,null, orderId, new BigDecimal(amount1), "N", "Technical Exception Thrown:::", key, algoName,requesttype,null,"");
                return;
            }*/
            catch (PZConstraintViolationException e)
            {
                log.error("PZConstraintViolationException in SingleCallGenericServlet---", e);
                transactionLogger.error("PZConstraintViolationException in SingleCallGenericServlet---",e);
                PZExceptionHandler.handleCVEException(e,directKitValidatorVO.getMerchantDetailsVO().getMemberId()+"IpAddress:"+remoteAddr, PZOperations.DIRECTKIT_SALE);
                error = errorCodeUtils.getSystemErrorCodeVOForDKIT(e.getPzConstraint().getErrorCodeListVO());

                key = directKitValidatorVO.getMerchantDetailsVO().getKey();
                algoName = directKitValidatorVO.getMerchantDetailsVO().getChecksumAlgo();
                orderId = directKitValidatorVO.getTransDetailsVO().getOrderId();
                amount1 = directKitValidatorVO.getTransDetailsVO().getAmount();

                if(ResponseLength.FULL.toString().equals(directKitValidatorVO.getMerchantDetailsVO().getResponseLength()))
                {
                    transactionError.calCheckSumAndWriteSaleFullResponse(pWriter,orderId, "N",error,"", String.valueOf(amount1), "", "", "", "", "", "", "", "", "","","", "", "", "", "", key, algoName, directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                    return;
                }
                else
                {
                    transactionError.calCheckSumAndWriteStatusForSale(pWriter,"",orderId,new BigDecimal(amount1), "N", error, key, algoName,requesttype,"","","",directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                    return;
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException in SingleCallGenericServlet---", e);
                transactionLogger.error("PZConstraintViolationException in SingleCallGenericServlet---",e);
                PZExceptionHandler.handleDBCVEException(e, directKitValidatorVO.getMerchantDetailsVO().getMemberId()+"IpAddress:"+remoteAddr, PZOperations.DIRECTKIT_SALE);

                key = directKitValidatorVO.getMerchantDetailsVO().getKey();
                algoName = directKitValidatorVO.getMerchantDetailsVO().getChecksumAlgo();
                orderId = directKitValidatorVO.getTransDetailsVO().getOrderId();
                amount1 = directKitValidatorVO.getTransDetailsVO().getAmount();
                if(ResponseLength.FULL.toString().equals(directKitValidatorVO.getMerchantDetailsVO().getResponseLength()))
                {
                    transactionError.calCheckSumAndWriteSaleFullResponse(pWriter,orderId, "N",error,"", String.valueOf(amount1), "", "", "", "", "", "", "", "", "","","", "", "", "", "", key, algoName, directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                    return;
                }
                else
                {
                    transactionError.calCheckSumAndWriteStatusForSale(pWriter,"",orderId,new BigDecimal(amount1), "N", error, key, algoName,requesttype,"","","",directKitValidatorVO.getMerchantDetailsVO().getResponseType());
                    return;
                }
                //transactionError.calCheckSumAndWriteStatusForSale(pWriter,null,orderId,new BigDecimal(amount1), "N", e.getMessage(), key, algoName,requesttype,null,"","");
                //return;
            }
        }
        else
        {
            transactionError.calCheckSumAndWriteStatusForSale(pWriter,"", "", "", "N","Transaction Data should not be empty", "", "",requesttype,"","",directKitValidatorVO.getMerchantDetailsVO().getResponseType());
            return;
        }
        transactionLogger.debug("SingleCallGenericServlet doPost end time 1 ######"+new Date().getTime());
        transactionLogger.debug("SingleCallGenericServlet doPost diff time 1 ######"+(new Date().getTime()-date1.getTime()));
    }



    private void singleCall(CommonValidatorVO directKitValidatorVO, HttpServletRequest request, HttpServletResponse response, PrintWriter pWriter ) throws ServletException, IOException
    {
        //variable decleration start
        log.debug("Enterred into Singlecall");
        transactionLogger.debug("Enterred into Singlecall");
        TransactionUtility transactionError = new TransactionUtility();
        SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
        RiskCheckers riskCheckers = new RiskCheckers();
        ActionEntry entry = new ActionEntry();
        PaymentChecker paymentChecker = new PaymentChecker();
        Transaction transaction = new Transaction();
        LimitChecker limitChecker = new LimitChecker();
        MerchantDetailsVO merchantDetailsVO= directKitValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= directKitValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO= directKitValidatorVO.getCardDetailsVO() ;
        GenericTransDetailsVO genericTransDetailsVO= directKitValidatorVO.getTransDetailsVO();
        //RecurringBillingVO recurringBillingVO = null;

        String key = null;
        String currency = null;
        String checksumAlgorithm = null;
        String accountId = null;
        String toid = null;
        String description = null;
        String fromid = null;
        String birthdate = null;
        String ssn = null;
        String status = "";
        String statusMsg = "";
        String HRCode = "";
        String reason = "";
        String language = null;
        BigDecimal amount = null;
        long ipcode = -99999;
        Hashtable hiddenvariables = new Hashtable();
        Hashtable error = new Hashtable();
        String machineid = null;

        String notifyEmails = null;
        String company_name = null;
        String brandname = null;
        String sitename = null;
        String chargepercent = null;
        String dbTaxPercent = null;
        String fixamount = null;
        String custremindermail = null;
        String billingDescriptor = null;

        String card_transaction_limit = null;
        String card_check_limit = null;
        BigDecimal charge = null;
        BigDecimal taxPercentage = null;

        int trackingId = 0;
        String isService = "";
        StringBuffer sb = new StringBuffer();
        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;

        int paymenttype = 0;
        int cardtype = 0;
        String requesttype = request.getParameter("requesttype");
        //Connection connection = null;
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        //String createRegistration=directKitValidatorVO.getCreateRegistration();
        String token="";
        String fraudScore="";
        String responseType=merchantDetailsVO.getResponseType();
        String responseLength=merchantDetailsVO.getResponseLength();

        try
        {
            toid = merchantDetailsVO.getMemberId();
            key = merchantDetailsVO.getKey();
            checksumAlgorithm = merchantDetailsVO.getChecksumAlgo();
            StatusSyncDAO statusSyncDAO=new StatusSyncDAO();

            paymenttype = Integer.parseInt(String.valueOf(directKitValidatorVO.getPaymentType()));
            cardtype = Integer.parseInt(String.valueOf(directKitValidatorVO.getCardType()));
            amount = new BigDecimal(genericTransDetailsVO.getAmount());
            amount = amount.setScale(2, BigDecimal.ROUND_DOWN);
            String totype = genericTransDetailsVO.getTotype();
            description = genericTransDetailsVO.getOrderId();
            String orderdescription = genericTransDetailsVO.getOrderDesc();
            String email = genericAddressDetailsVO.getEmail();
            String city = genericAddressDetailsVO.getCity();
            String street = genericAddressDetailsVO.getStreet();
            String zip = genericAddressDetailsVO.getZipCode();
            String state = genericAddressDetailsVO.getState();
            String telno = genericAddressDetailsVO.getPhone();
            String telnocc = genericAddressDetailsVO.getTelnocc();
            String TMPL_COUNTRY = genericAddressDetailsVO.getCountry();
            String firstname = genericAddressDetailsVO.getFirstname();
            String lastname = genericAddressDetailsVO.getLastname();
            String cardholder = genericCardDetailsVO.getCardHolderName();
            String ccnum = genericCardDetailsVO.getCardNum();
            String cvv = genericCardDetailsVO.getcVV();
            String expmonth = genericCardDetailsVO.getExpMonth();
            String expyear = genericCardDetailsVO.getExpYear();
            String expdate = expmonth + "/" + expyear;
            String redirecturl = genericTransDetailsVO.getRedirectUrl();
            birthdate = genericAddressDetailsVO.getBirthdate();
            String cardHolderIpAddress = genericAddressDetailsVO.getCardHolderIpAddress();
            accountId = merchantDetailsVO.getAccountId();
            String terminalid = directKitValidatorVO.getTerminalId();
            log.debug("terminalid in single call---"+terminalid);

            String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
            fromid = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();

            if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                currency = genericTransDetailsVO.getCurrency();
            else
                currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

            language = genericAddressDetailsVO.getLanguage();
            //get ipaddress from httpheadder
            String merchantIpaddress = genericAddressDetailsVO.getIp();
            log.debug("header    -------" + merchantIpaddress);
            transactionLogger.debug("header    -------" + merchantIpaddress);

            //is mastercard supported
            if (cardtype == 2 && !limitChecker.isMasterCardSupported(toid))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericServlet.java","singleCall()",null,"Transaction", ErrorMessages.UNSUPPORTED_MASTERCARD, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            //risk check
            ipcode = riskCheckers.getIPCode(merchantIpaddress);
            //block IP

            riskCheckers.checkBlockedIP(false,merchantIpaddress,ipcode,HRCode);

            //check whether this is blocked email

            riskCheckers.checkBlockedEmail(false,email,HRCode);

            //check whether this is blocked Domain

            riskCheckers.checkBlockedDomain(false,email,HRCode);

            //validate Country Code

            riskCheckers.checkDifferentCountry(false,TMPL_COUNTRY,HRCode,ipcode,false);

            machineid = transactionError.getCookie(request, response);
            log.debug("HRCode==="+HRCode);
            transactionLogger.debug("HRCode==="+HRCode);

            //memberDetails = merchants.getMemberDetailsForTransaction(toid);
            if(merchantDetailsVO!=null)
            {
                notifyEmails = merchantDetailsVO.getNotifyEmail();
                company_name = merchantDetailsVO.getCompany_name();
                isService = merchantDetailsVO.getService();
                brandname = merchantDetailsVO.getBrandName();
                sitename = merchantDetailsVO.getSiteName();
                chargepercent = merchantDetailsVO.getChargePer();
                dbTaxPercent = merchantDetailsVO.getTaxPer();
                fixamount = merchantDetailsVO.getFixamount();
                custremindermail = merchantDetailsVO.getCustReminderMail();
                card_transaction_limit = merchantDetailsVO.getCardTransLimit();
                card_check_limit = merchantDetailsVO.getCardCheckLimit();

                if (brandname.trim().equals(""))
                {
                    brandname = company_name;
                }
                if (sitename.trim().equals(""))
                {
                    sitename = company_name;
                }

                if (chargepercent == null)
                {
                    charge = new BigDecimal(defaultchargepercent);
                }
                else
                {
                    charge = new BigDecimal(chargepercent);
                }

                if (dbTaxPercent == null)
                {
                    if ("USD".equals(currency))
                    {
                        taxPercentage = new BigDecimal(USD_defaulttaxpercent);
                    }
                    else
                    {
                        taxPercentage = new BigDecimal(INR_defaulttaxpercent);
                    }
                }
                else
                {
                    taxPercentage = new BigDecimal(dbTaxPercent);
                }
            }
            else
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericServlet.java","singleCall()",null,"Transaction", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            //select payment gateway for payment start
            if (SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                //for SBM flow
            }
            else if(CUPPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,null,"","",responseType);
                return;
            }
            else if (PayLineVoucherGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,null,"","",responseType);
                return;
            }
            else if (InPayPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,null,"","",responseType);
                return;
            }
            else if (SkrillPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }
            else if (TrustlyPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }
            else if (PerfectMoneyPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }
            else if (JetonVoucherPaymentGateway.GATEWAY_TYPE.equals(fromtype) || JetonPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }
            else if (NetellerPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }
            else if (EpayPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                status = "N";
                statusMsg = "This transaction can not be made using DirectKit";
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,"","","",responseType);
                return;
            }

            else if (PayDollarPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                //for paydollar flow
            }
            else if (Functions.checkAPIGateways(fromtype))
            {
                ServletContext application = getServletContext();

                directKitValidatorVO.getMerchantDetailsVO().setCurrency(currency);
                directKitValidatorVO.getTransDetailsVO().setHeader(header);
                directKitValidatorVO.setResponseLength(responseLength);

                //AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.valueOf(accountId));
                TransactionCoreUtils transactionCoreUtils = new TransactionCoreUtils();

                //Hashtable<String, String> responseHash = paymentProcess.transactionAPI(transactionParameters);
                DirectCommResponseVO directCommResponseVO = transactionCoreUtils.transactionAPI(directKitValidatorVO);

                AbstractPaymentGateway pg = null;

                pg = AbstractPaymentGateway.getGateway(accountId);



                trackingId = Integer.parseInt(directCommResponseVO.getTrackingid());
                status = directCommResponseVO.getCommStatus();
                statusMsg = directCommResponseVO.getCommStatusMessage();

                //System.out.println("trackingid---"+trackingId+"--status--"+status+"--smmmm---"+statusMsg);

                if(directCommResponseVO.getToken()!=null)
                {
                    token=directCommResponseVO.getToken();
                }

                /*transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,pg.getDisplayName(),token,fraudScore);
                return;*/

                //todo - set farud score
                if(directCommResponseVO.getFraudScore()!=null)
                {
                    fraudScore=directCommResponseVO.getFraudScore();
                }
                if("Y".equals(status))
                {
                    billingDescriptor=directCommResponseVO.getDescriptor();
                }

                transactionLogger.debug("fraud score received========"+fraudScore);
                if(ResponseLength.FULL.toString().equals(responseLength))
                {
                    //transactionError.calCheckSumAndWriteSaleFullResponse(pWriter,responseHash,key,checksumAlgorithm,responseType);
                    transactionError.calCheckSumAndWriteSaleFullResponseForSale(pWriter, directCommResponseVO, description, String.valueOf(amount), key, checksumAlgorithm, responseType);
                    return;
                }
                else
                {
                    transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,billingDescriptor,token,fraudScore,responseType);
                    return;
                }
            }
            else
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericServlet.java","singleCall()",null,"Transaction",ErrorMessages.INVALID_GATEWYAY,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException in SingleCallGenericServlet---",cve);
            transactionLogger.error("PZConstraintViolationException in SingleCallGenericServlet---",cve);
            log.debug("member iddd----------"+directKitValidatorVO.getMerchantDetailsVO().getMemberId()+"-----toid-----"+toid);
            PZExceptionHandler.handleCVEException(cve,directKitValidatorVO.getMerchantDetailsVO().getMemberId()+"IpAddress:"+remoteAddr,PZOperations.DIRECTKIT_SALE);
            status = "N";
            statusMsg = cve.getPzConstraint().getMessage();
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                transactionError.calCheckSumAndWriteSaleFullResponse(pWriter, description, status, statusMsg, String.valueOf(trackingId),
                        String.valueOf(amount), "", "", "", "", "", "", "", "", "", statusMsg, "", "", "", "", "", key, checksumAlgorithm, responseType);
                return;
            }
            else
            {
                transactionError.calCheckSumAndWriteStatusForSale(pWriter, "", description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,null,token,fraudScore,responseType);
                return;
            }
        }
        catch (SystemError e) //for getCookie
        {
            log.error("systemerror", e);
            transactionLogger.error("systemerror", e);
            status = "N";
            statusMsg = "Your transaction is fail -(" + reason + ")";
            transactionError.calCheckSumAndWriteStatusForSale(pWriter, String.valueOf(trackingId), description, amount, status, statusMsg, key, checksumAlgorithm,requesttype,null,"","",responseType);
            return;
        }
    }

}
