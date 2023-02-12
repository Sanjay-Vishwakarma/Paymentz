package payment;

import com.directi.pg.Checksum;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.AESEncryptionManager;
import com.manager.PartnerManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TokenRequestVO;
import com.manager.vo.TokenResponseVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import org.owasp.esapi.ESAPI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 11/28/15
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallTokenServlet extends HttpServlet
{
    Logger logger=new Logger(SingleCallTokenServlet.class.getName());
    TransactionLogger transactionLogger=new TransactionLogger(SingleCallTokenServlet.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.debug("GET request is received:::::");
        doProcess(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        transactionLogger.debug("POST request is received:::::");
        doProcess(request,response);
    }
    public void doProcess(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        PrintWriter printWriter = response.getWriter();
        String checkAlgorithm=null;
        String key=null;
        String toId="";
        String isAddressDetailsRequired="";
        String isCardEncryptionEnable = "N";
        String checksum=request.getParameter("checksum");
        String partnerId=request.getParameter("partnerid");
        String generatedBy="";
        String tokenValidDays="";

        TransactionUtility transactionUtility=new TransactionUtility();
        MerchantDetailsVO merchantDetailsVO= new MerchantDetailsVO();

        String responseType = "";
        String responseLength = "";

        if(!ESAPI.validator().isValidInput("partnerid",partnerId, "Numbers", 20, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid PartnerId","","",responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid Checksum", key, checkAlgorithm,responseType,responseLength);
            return;
        }

        PartnerManager partnerManager=new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            partnerDetailsVO=partnerManager.getPartnerDetails(partnerId);
            if(partnerDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Unauthorised Partner", key, checkAlgorithm,responseType,responseLength);
                return;
            }
            responseType=partnerDetailsVO.getResponseType();
            responseLength=partnerDetailsVO.getResponseLength();
        }
        catch (Exception gve)
        {
            //gve.printStackTrace();
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Internal error while processing you request.","","",responseType,responseLength);
            return;
        }

        if ("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Tokenization feature has not been activated to your partner account", key, checkAlgorithm,responseType,responseLength);
            return;
        }

        Functions functions = new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
        {
            if(!ESAPI.validator().isValidInput("toid",request.getParameter("toid"), "Numbers", 20, false))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid ToId","","",responseType,responseLength);
                return;
            }
            else
            {
                toId = request.getParameter("toid");
            }

            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
            }
            catch (Exception gve)
            {
                //gve.printStackTrace();
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Internal error while processing you request.","","",responseType,responseLength);
                return;
            }
            if(merchantDetailsVO!=null && !functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Unauthorized User","","",responseType,responseLength);
                return;
            }

            if ("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Tokenization feature has not been activated to your merchant account", key, checkAlgorithm,responseType,responseLength);
                return;
            }

            generatedBy=merchantDetailsVO.getLogin();
            key=merchantDetailsVO.getKey();
            checkAlgorithm=merchantDetailsVO.getChecksumAlgo();
            isAddressDetailsRequired=merchantDetailsVO.getIsAddressDetailsRequired();
            isCardEncryptionEnable = merchantDetailsVO.getIsCardEncryptionEnable();
        }
        else
        {
            if(!ESAPI.validator().isValidInput("toid",request.getParameter("toid"), "Numbers", 20, true))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid ToId","","",responseType,responseLength);
                return;
            }
            else
            {
                toId = request.getParameter("toid");
            }
            key=partnerDetailsVO.getPartnerKey();
            isAddressDetailsRequired=partnerDetailsVO.getIsAddressRequiredForTokenTransaction();
            isCardEncryptionEnable = partnerDetailsVO.getIsCardEncryptionEnable();
        }
        try
        {
            String error = "";
            String cardNumber = request.getParameter("cardnumber");
            String cvv = request.getParameter("cvv");
            String expiryMonth = request.getParameter("expirymonth");
            String expiryYear = request.getParameter("expiryyear");

            if ("Y".equals(isCardEncryptionEnable))
            {
                try
                {
                    AESEncryptionManager encryptionManager=new AESEncryptionManager();
                    cardNumber = encryptionManager.decrypt(cardNumber, key);
                    cvv = encryptionManager.decrypt(cvv, key);
                    expiryMonth = encryptionManager.decrypt(expiryMonth, key);
                    expiryYear = encryptionManager.decrypt(expiryYear, key);

                }
                catch (Exception se)
                {
                    transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid Data Encryption", key, checkAlgorithm,responseType,responseLength);
                    return;
                }
            }

            if (!ESAPI.validator().isValidInput("cardnumber", cardNumber, "CC", 20, false))
            {
                error = error + "Invalid CardNumber|";
            }
            if (!ESAPI.validator().isValidInput("cvv", cvv, "Numbers", 4, false))
            {
                error = error + "Invalid CVV|";
            }
            if (!ESAPI.validator().isValidInput("expirymonth",expiryMonth,"Months", 2, false))
            {
                error = error + "Invalid Expiry Month |";
            }
            if (!ESAPI.validator().isValidInput("expiryyear", expiryYear, "Years", 4, false))
            {
                error = error + "Invalid Expiry Year |";
            }
            if (functions.isValueNull(error))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", error, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            if (!Checksum.verifyChecksumV2(partnerId, cardNumber, cvv, key, checksum, checkAlgorithm))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Checksum mismatch", key, checkAlgorithm,responseType,responseLength);
                return;
            }

            String cardholderFirstName = "";
            String cardholderLastName = "";
            String country = "";
            String city = "";
            String state = "";
            String street = "";
            String zip = "";
            String telNoCc = "";
            String telNo = "";
            String cardholderEmail = "";
            String birthDate = "";
            String language = "";
            String cardholderId =null;


            String errorMsg = "";
            TokenResponseVO tokenResponseVO = null;
            TokenManager tokenManager = new TokenManager();

            if ("Y".equals(isCardEncryptionEnable))
            {
                errorMsg = tokenManager.cardRegistrationDirectAPIMandatoryValidationForAESEncryption(request, key);
            }
            else
            {
                errorMsg = tokenManager.cardRegistrationDirectAPIMandatoryValidation(request);
            }

            if (functions.isValueNull(errorMsg))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", errorMsg, key, checkAlgorithm,responseType,responseLength);
                return;
            }
            else
            {
                partnerId = request.getParameter("partnerid");
                cardholderFirstName = request.getParameter("firstname");
                cardholderLastName = request.getParameter("lastname");
                cardNumber = request.getParameter("cardnumber");
                expiryMonth = request.getParameter("expirymonth");
                expiryYear = request.getParameter("expiryyear");
                cardholderEmail= request.getParameter("emailaddr");
                cvv = request.getParameter("cvv");

                if ("Y".equals(isCardEncryptionEnable))
                {
                    try
                    {
                        AESEncryptionManager encryptionManager=new AESEncryptionManager();
                        cardNumber = encryptionManager.decrypt(cardNumber, key);
                        expiryMonth = encryptionManager.decrypt(expiryMonth, key);
                        expiryYear = encryptionManager.decrypt(expiryYear, key);
                        cvv = encryptionManager.decrypt(cvv, key);
                    }
                    catch (Exception e)
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Invalid Card Encryption", key, checkAlgorithm,responseType,responseLength);
                        return;
                    }
                }
            }

            if ("N".equals(isAddressDetailsRequired))
            {
                errorMsg = tokenManager.cardRegistrationDirectAPIConditionalValidation(request);
                if (functions.isValueNull(errorMsg))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", errorMsg, key, checkAlgorithm,responseType,responseLength);
                    return;
                }
                else
                {
                    country = request.getParameter("countrycode");
                    city = request.getParameter("city");
                    state = request.getParameter("state");
                    street = request.getParameter("street");
                    zip = request.getParameter("zip");
                    telNoCc = request.getParameter("telnocc");
                    telNo = request.getParameter("telno");
                    //cardholderEmail= request.getParameter("emailaddr");
                }
            }

            errorMsg = tokenManager.cardRegistrationDirectAPIOptionalValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", errorMsg, key, checkAlgorithm,responseType,responseLength);
                return;
            }
            else
            {
                birthDate = request.getParameter("birthdate");
                language = request.getParameter("language");
                cardholderId = request.getParameter("cardholderid");
            }

            if(functions.isValueNull(cardholderId))
            {
                if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                {
                    if(!tokenManager.isCardholderRegistered(toId,cardholderId))
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N","Requested cardholder not found",key,checkAlgorithm,responseType,responseLength);
                        return;
                    }
                }
                else
                {
                    if(!tokenManager.isCardholderRegisteredWithPartner(partnerId,cardholderId))
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N","Requested cardholder not found",key,checkAlgorithm,responseType,responseLength);
                        return;
                    }
                }
            }

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                if (!tokenManager.isNewCard(toId, cardNumber))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Card already tokenized for same merchant", key, checkAlgorithm,responseType,responseLength);
                    return;
                }

            }
            else
            {
                if (!tokenManager.isNewCardForPartner(partnerId, cardNumber))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Card already tokenized for same partner", key, checkAlgorithm,responseType,responseLength);
                    return;
                }
            }

            TokenRequestVO tokenRequestVO = new TokenRequestVO();
            CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
            CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

            commCardDetailsVO.setCardHolderFirstName(cardholderFirstName);
            commCardDetailsVO.setCardHolderSurname(cardholderLastName);
            commCardDetailsVO.setCardType("1");
            commCardDetailsVO.setCardHolderName(cardholderFirstName + " " + cardholderLastName);
            commCardDetailsVO.setCardNum(cardNumber);
            commCardDetailsVO.setExpMonth(expiryMonth);
            commCardDetailsVO.setExpYear(expiryYear);

            commAddressDetailsVO.setEmail(cardholderEmail);
            commAddressDetailsVO.setFirstname(cardholderFirstName);
            commAddressDetailsVO.setLastname(cardholderLastName);
            commAddressDetailsVO.setCountry(country);
            commAddressDetailsVO.setCity(city);
            commAddressDetailsVO.setState(state);
            commAddressDetailsVO.setStreet(street);
            commAddressDetailsVO.setZipCode(zip);
            commAddressDetailsVO.setTelnocc(telNoCc);
            commAddressDetailsVO.setPhone(telNo);
            commAddressDetailsVO.setBirthdate(birthDate);
            commAddressDetailsVO.setLanguage(language);

            tokenRequestVO.setAddressDetailsVO(commAddressDetailsVO);
            tokenRequestVO.setPartnerId(partnerId);
            tokenRequestVO.setMemberId(toId);
            tokenRequestVO.setCardholderId(cardholderId);
            tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
            tokenRequestVO.setGeneratedBy(generatedBy);

            String status = "N";
            String statusMsg = "";
            String tokenString = "";
            String cardRegistrationLevel="";

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                cardRegistrationLevel="merchants";
                tokenResponseVO = tokenManager.createTokenNew(tokenRequestVO);
            }
            else
            {
                tokenResponseVO = tokenManager.createTokenByPartner(tokenRequestVO);
                cardRegistrationLevel="partners";
            }

            if ("success".equals(tokenResponseVO.getStatus()))
            {
                tokenString = tokenResponseVO.getToken();
                tokenValidDays = String.valueOf(tokenResponseVO.getValidDays());
                status = "Y";
                statusMsg = "Card tokenized successfully";
            }
            else
            {
                statusMsg = "Card tokenizing failed";
            }

            MailService mailService=new MailService();
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            HashMap hashMap=new HashMap();

            hashMap.put(MailPlaceHolder.NAME, tokenRequestVO.getCommCardDetailsVO().getCardHolderFirstName());
            hashMap.put(MailPlaceHolder.CARDHOLDERNAME,cardholderFirstName+" "+cardholderLastName);
            hashMap.put(MailPlaceHolder.CustomerEmail, cardholderEmail);
            logger.debug(mailService.getDetailTableForCardDetails(tokenResponseVO.getTokenDetailsVO()));
            hashMap.put(MailPlaceHolder.MULTIPALTRANSACTION,mailService.getDetailTableForCardDetails(tokenResponseVO.getTokenDetailsVO()));
            logger.debug(hashMap+"");

            if("merchants".equalsIgnoreCase(cardRegistrationLevel))
            {
                hashMap.put(MailPlaceHolder.TOID, merchantDetailsVO.getMemberId());
               // mailService.sendMail(MailEventEnum.MERCHANTS_LEVEL_CARD_REGISTRATION, hashMap);
               asynchronousMailService.sendMerchantSignup(MailEventEnum.MERCHANTS_LEVEL_CARD_REGISTRATION, hashMap);
            }
            else if("partners".equalsIgnoreCase(cardRegistrationLevel))
            {
                hashMap.put(MailPlaceHolder.TOID, partnerId);
                //mailService.sendMail(MailEventEnum.PARTNERS_LEVEL_CARD_REGISTRATION, hashMap);
                asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_LEVEL_CARD_REGISTRATION, hashMap);
            }
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, tokenValidDays, tokenString, status, statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (PZGenericConstraintViolationException gve)
        {
            //gve.printStackTrace();
            transactionLogger.error("PZGenericConstraintViolationException::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Internal error while processing your request", key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (Exception gve)
        {
            //gve.printStackTrace();
            transactionLogger.error("Exception::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStatusCardRegistration(printWriter, "", "", "N", "Internal error while processing your request", key, checkAlgorithm,responseType,responseLength);
            return;
        }
    }
}
