package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.romcard.RomCardPaymentGateway;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

/**
 * Created by Rihen on 12/21/2018.
 */
public class RomCardFrontEndServlet extends HttpServlet
{

    private static TransactionLogger transactionLogger = new TransactionLogger(RomCardFrontEndServlet.class.getName());
    String ctoken = ESAPI.randomizer().getRandomString(32, DefaultEncoder.CHAR_ALPHANUMERICS);


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        transactionLogger.debug("-----Inside RomCardFrontEndServlet-----");
        TransactionManager transactionManager = new TransactionManager();
        ActionEntry entry = new ActionEntry();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO= new GenericCardDetailsVO();
        TransactionUtility transactionUtility = new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO = null;
        MerchantDAO merchantDAO = new MerchantDAO();
        CommonPaymentProcess paymentProcess = new CommonPaymentProcess();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        ActionEntry actionEntry = new ActionEntry();
        Functions functions = new Functions();
        HttpSession session = request.getSession(true);
        Connection con = null;

        Enumeration enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements())
        {
            String keyName = (String) enumeration.nextElement();
            transactionLogger.error(keyName + ":" + request.getParameter(keyName));
        }

        String trackingId = "";
        String resp_amount = "";
        String resp_currency = "";
        String resp_desc = "";
        String resp_action = "";
        String resp_rc = "";
        String resp_message = "";
        String resp_rrn = "";
        String resp_int_ref = "";
        String resp_approval = "";
        if(functions.isValueNull(request.getParameter("ORDER")))
        {
            trackingId = request.getParameter("ORDER");
        }
        if(functions.isValueNull(request.getParameter("AMOUNT")))
        {
            resp_amount = request.getParameter("AMOUNT");
        }
        if(functions.isValueNull(request.getParameter("CURRENCY")))
        {
            resp_currency = request.getParameter("CURRENCY");
        }
        if(functions.isValueNull(request.getParameter("DESC")))
        {
            resp_desc = request.getParameter("DESC");
        }
        if(functions.isValueNull(request.getParameter("ACTION")))
        {
            resp_action = request.getParameter("ACTION");
        }
        if(functions.isValueNull(request.getParameter("RC")))
        {
            resp_rc = request.getParameter("RC");
        }
        if(functions.isValueNull(request.getParameter("MESSAGE")))
        {
            resp_message = request.getParameter("MESSAGE");
        }
        if(functions.isValueNull(request.getParameter("RRN")))
        {
            resp_rrn = request.getParameter("RRN");
        }
        if(functions.isValueNull(request.getParameter("INT_REF")))
        {
            resp_int_ref = request.getParameter("INT_REF");
        }
        if(functions.isValueNull(request.getParameter("APPROVAL")))
        {
            resp_approval = request.getParameter("APPROVAL");
        }

        String toId = "";
        String accountId = "";
        String status = "";
        String amount = "";
        String description = "";
        String redirectUrl = "";
        String autoRedirect = "";
        String logoName = "";
        String partnerName = "";
        String confirmStatus = "";
        String orderDesc = "";
        String currency = "";
        String message = "";
        String billingDesc = "";
        String dbStatus = "";
        String tmpl_amt = "";
        String tmpl_currency = "";
        String paymodeid = "";
        String cardtypeid= "";
        String email = "";
        String customerId="";
        String transactionStatus="";
        String version="";
        String notificationUrl="";
        String terminalid="";
        String updatedStatus="";
        String isService="";
        String transactionId="";
        String transType="Sale";
        String responseStatus="";
        String firstName="";
        String lastName="";
        String paymentid="";
        String ccnum="";
        String expDate="";
        String expMonth="";
        String expYear="";

        String remoteAddr = Functions.getIpAddress(request);
        String reqIp = "";
        if(remoteAddr.contains(","))
        {
            String sIp[] = remoteAddr.split(",");
            reqIp = sIp[0].trim();
        }
        else
        {
            reqIp = remoteAddr;
        }

        try
        {
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
            if (transactionDetailsVO != null)
            {
                toId = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                tmpl_amt = transactionDetailsVO.getTemplateamount();
                tmpl_currency = transactionDetailsVO.getTemplatecurrency();
                description = transactionDetailsVO.getDescription();
                redirectUrl = transactionDetailsVO.getRedirectURL();
                orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                paymodeid = transactionDetailsVO.getPaymodeId();
                cardtypeid = transactionDetailsVO.getCardTypeId();
                email = transactionDetailsVO.getEmailaddr();
                dbStatus = transactionDetailsVO.getStatus();
                notificationUrl = transactionDetailsVO.getNotificationUrl();
                firstName = transactionDetailsVO.getFirstName();
                lastName = transactionDetailsVO.getLastName();
                version = transactionDetailsVO.getVersion();
                terminalid = transactionDetailsVO.getTerminalId();
                customerId=transactionDetailsVO.getCustomerId();
                paymentid=transactionDetailsVO.getPaymentId();
                if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                {
                    ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                }
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

                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (merchantDetailsVO != null)
                {
                    autoRedirect = merchantDetailsVO.getAutoRedirect();
                    logoName = merchantDetailsVO.getLogoName();
                    partnerName = merchantDetailsVO.getPartnerName();
                    isService=merchantDetailsVO.getIsService();
                }
                commMerchantVO.setIsService(isService);
                auditTrailVO.setActionExecutorName("AcquirerFrontEnd");
                auditTrailVO.setActionExecutorId(toId);

                if (functions.isValueNull(transactionDetailsVO.getOrderDescription()))
                    orderDesc = transactionDetailsVO.getOrderDescription();
                currency = transactionDetailsVO.getCurrency();
                transactionLogger.error("dbStatus-----" + dbStatus);

                if (PZTransactionStatus.AUTH_STARTED.toString().equals(dbStatus))
                {
                    Comm3DRequestVO comm3DRequestVO = new Comm3DRequestVO();
                    CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                    CommAddressDetailsVO commAddressDetailsVO= new CommAddressDetailsVO();
                    CommCardDetailsVO commCardDetailsVO= new CommCardDetailsVO();
                    RomCardPaymentGateway romCardPaymentGateway= new RomCardPaymentGateway(accountId);
                    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
                    commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                    commTransactionDetailsVO.setOrderId(trackingId);
                    commTransactionDetailsVO.setPreviousTransactionId(resp_int_ref);
                    commTransactionDetailsVO.setResponseHashInfo(resp_int_ref);
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commAddressDetailsVO.setFirstname(firstName);
                    commAddressDetailsVO.setLastname(lastName);
                    commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
                    commAddressDetailsVO.setState(transactionDetailsVO.getState());
                    commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
                    commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
                    commAddressDetailsVO.setTmpl_amount(transactionDetailsVO.getTemplateamount());
                    commAddressDetailsVO.setTmpl_currency(transactionDetailsVO.getTemplatecurrency());
                    commAddressDetailsVO.setCardHolderIpAddress(reqIp);
                    commAddressDetailsVO.setIp(transactionDetailsVO.getIpAddress());
                    commAddressDetailsVO.setEmail(email);
                    commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
                    commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());

                    commCardDetailsVO.setCardNum(ccnum);
                    commCardDetailsVO.setExpMonth(expMonth);
                    commCardDetailsVO.setExpYear(expYear);
                    commCardDetailsVO.setCardType(transactionDetailsVO.getCardtype());
                    comm3DRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    comm3DRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    comm3DRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                    comm3DRequestVO.setCommMerchantVO(commMerchantVO);
                    comm3DRequestVO.setCardDetailsVO(commCardDetailsVO);

                    CommResponseVO transRespDetails = null;
                    if ("N".equals(isService))
                    {
                        transactionLogger.debug("in AUTH");
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                        transType = "Auth";
                        transRespDetails = new CommResponseVO();
                        if("Approved".equalsIgnoreCase(resp_message) && "0".equals(resp_action))
                        {
                            transactionLogger.debug("in AUTH status APPROVED "+resp_message);
                            transRespDetails.setStatus("success");
                        }
                        else
                        {
                            transactionLogger.debug("in AUTH status failes "+resp_message);
                            transRespDetails.setStatus("fail");
                        }
                        transactionLogger.debug("resp_int_ref ---------"+resp_int_ref);
                        transactionLogger.debug("resp_message ---------"+resp_message);
                        transactionLogger.debug("resp_amount ---------"+resp_amount);
                        transactionLogger.debug("resp_currency ---------"+resp_currency);
                        transactionLogger.debug("resp_rrn ---------"+resp_rrn);
                        transRespDetails.setTransactionId(resp_int_ref);
                        transRespDetails.setDescription(resp_message);
                        transRespDetails.setAmount(resp_amount);
                        transRespDetails.setCurrency(resp_currency);
                        transRespDetails.setResponseHashInfo(resp_rrn);
                    }
                    else
                    {
                        entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, transRespDetails, comm3DRequestVO, auditTrailVO, null);
                        if("Approved".equalsIgnoreCase(resp_message) && "0".equals(resp_action))
                        {
                            commTransactionDetailsVO.setResponseHashInfo(resp_rrn);
                            comm3DRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            transRespDetails = (CommResponseVO) romCardPaymentGateway.processCapture(trackingId,comm3DRequestVO);
                        }
                        else
                        {
                            transRespDetails = new CommResponseVO();
                            transRespDetails.setStatus("fail");
                            transRespDetails.setTransactionId(resp_int_ref);
                            transRespDetails.setDescription(resp_message);
                            transRespDetails.setAmount(resp_amount);
                            transRespDetails.setCurrency(resp_currency);
                            transRespDetails.setResponseHashInfo(resp_rrn);
                        }
                    }
                    if (transRespDetails != null)
                    {
                        transactionLogger.debug("---inside transRespDetails---"+transRespDetails);
                        transactionStatus = transRespDetails.getStatus();
                        transactionId = transRespDetails.getTransactionId();
                        message = transRespDetails.getDescription();
                        billingDesc=transRespDetails.getDescriptor();

                        if(!functions.isValueNull(transRespDetails.getCurrency()))
                            transRespDetails.setCurrency(currency);
                        if(!functions.isValueNull(transRespDetails.getTmpl_Amount()))
                            transRespDetails.setTmpl_Amount(tmpl_amt);
                        if(!functions.isValueNull(transRespDetails.getTmpl_Currency()))
                            transRespDetails.setTmpl_Currency(tmpl_currency);
                        if(functions.isValueNull(transRespDetails.getAmount()))
                            amount = transRespDetails.getAmount();

                        transactionLogger.debug("transactionStatus-----"+transactionStatus);
                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            status = "success";
                            confirmStatus = "Y";
                            responseStatus = "Successful";
                            if(!functions.isValueNull(billingDesc))
                            {
                                billingDesc = gatewayAccount.getDisplayName();
                            }
                            if ("Sale".equalsIgnoreCase(transType))
                            {
                                transRespDetails.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess'");
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "capturesuccess");
                                updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();
                            }
                            else
                            {
                                transRespDetails.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                                dbBuffer.append("update transaction_common set paymentid='" + transactionId + "' ,status='authsuccessful'");
                                paymentProcess.actionEntry(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, null, auditTrailVO);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authsuccessful");
                                updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                            }
                        }
                        else
                        {
                            confirmStatus = "N";
                            status = "fail";
                            if(!functions.isValueNull(message))
                            {
                                message="fail";
                            }
                            responseStatus = "Failed(" + message + ")";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "'");
                            actionEntry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, auditTrailVO, null);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingId, "authfailed");
                            updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                        }
                        dbBuffer.append(" ,remark='" + message + "' where trackingid = " + trackingId);
                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);
                        transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                        AsynchronousMailService asynchronousMailService = AsynchronousMailService.getInstance();
                        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                        AsynchronousSmsService smsService = AsynchronousSmsService.getInstance();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), status, message, billingDesc);

                    }
                    else
                    {
                        transactionLogger.debug("-----inside pending-----");
                        status = "pending";
                        responseStatus = "Pending";
                        message = "FE:Transaction is pending";
                        updatedStatus=PZTransactionStatus.AUTH_STARTED.toString();
                    }
                }
                else
                {
                    status = "pending";
                    responseStatus = "Pending";
                    message = "FE:Transaction is in progress";
                    updatedStatus=PZTransactionStatus.AUTH_STARTED.toString();
                }
            }
            else
            {
                if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status = "success";
                    message = "Transaction Successful";
                    responseStatus = "Successful";
                    updatedStatus=PZTransactionStatus.CAPTURE_SUCCESS.toString();

                }
                else if (PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                {
                    billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    status = "success";
                    message = "Transaction Successful";
                    responseStatus = "Successful";
                    updatedStatus=PZTransactionStatus.AUTH_SUCCESS.toString();
                }
                else if (PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus))
                {
                    status = "fail";
                    responseStatus = "Failed";
                    message = "Failed";
                    updatedStatus=PZTransactionStatus.AUTH_FAILED.toString();
                }
                else if (PZTransactionStatus.AUTHSTARTED_3D.toString().equals(dbStatus))
                {
                    status = "pending";
                    responseStatus = "Pending";
                    message = "Transaction is in progress";
                    updatedStatus=PZTransactionStatus.AUTHSTARTED_3D.toString();
                }
                else
                {
                    status = "fail";
                    responseStatus = "Failed(Transaction not found in correct status)";
                    message = "Failed(Transaction not found in correct status)";
                    updatedStatus=PZTransactionStatus.FAILED.toString();
                }
            }

            commonValidatorVO.setTrackingid(trackingId);
            transDetailsVO.setOrderId(description);
            transDetailsVO.setOrderDesc(orderDesc);
            transDetailsVO.setAmount(amount);
            transDetailsVO.setCurrency(currency);
            transDetailsVO.setRedirectUrl(redirectUrl);
            transDetailsVO.setBillingDiscriptor(billingDesc);
            transDetailsVO.setNotificationUrl(notificationUrl);

            addressDetailsVO.setEmail(email);
            addressDetailsVO.setFirstname(firstName);
            addressDetailsVO.setLastname(lastName);
            addressDetailsVO.setTmpl_amount(tmpl_amt);
            addressDetailsVO.setTmpl_currency(tmpl_currency);

            cardDetailsVO.setCardNum(ccnum);
            cardDetailsVO.setExpMonth(expMonth);
            cardDetailsVO.setExpYear(expYear);

            commonValidatorVO.setLogoName(logoName);
            commonValidatorVO.setPartnerName(partnerName);
            commonValidatorVO.setPaymentType(paymodeid);
            commonValidatorVO.setCardType(cardtypeid);
            commonValidatorVO.setCustomerId(customerId);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
            commonValidatorVO.setTransDetailsVO(transDetailsVO);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setTerminalId(terminalid);

            transactionUtility.setToken(commonValidatorVO, responseStatus);

            if (functions.isValueNull(notificationUrl))
            {
                transactionLogger.error("inside sending notification---" + notificationUrl);
                TransactionDetailsVO transactionDetailsVO1 = transactionUtility.getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingId, updatedStatus, message,"");
            }

            if ("Y".equalsIgnoreCase(autoRedirect))
            {
                transactionUtility.doAutoRedirect(commonValidatorVO, response, responseStatus, billingDesc);
            }
            else
            {
                request.setAttribute("responceStatus", responseStatus);
                request.setAttribute("displayName", billingDesc);
                request.setAttribute("remark", message);
                request.setAttribute("transDetail", commonValidatorVO);
                session.setAttribute("ctoken", ctoken);

                String confirmationPage = "";

                if (functions.isValueNull(version) && version.equalsIgnoreCase("2"))
                    confirmationPage = "/confirmationCheckout.jsp?ctoken=";
                else
                    confirmationPage = "/confirmationpage.jsp?ctoken=";

                RequestDispatcher rd = request.getRequestDispatcher(confirmationPage + ctoken);
                rd.forward(request, response);
                session.invalidate();
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (SystemError e){
            transactionLogger.error("SystemError::::::",e);
            PZExceptionHandler.raiseAndHandleDBViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause(), toId, null);

        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("error::::", e);
            PZExceptionHandler.raiseAndHandleConstraintViolationException("Common3DFrontEndServlet.java", "doService()", null, "Transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, e.getMessage(), e.getCause(), toId, null);
        }
        catch (Exception e)
        {
            transactionLogger.error(" Exception ------ ",e);
        }
        finally
        {
            Database.closeConnection(con);
        }

    }

}