package payment;

import com.directi.pg.Checksum;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PartnerManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.CardHolderRequestVO;
import com.manager.vo.CardholderResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import org.owasp.esapi.ESAPI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 12/11/15
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallCustomerSignup extends HttpServlet
{
    Logger logger=new Logger(SingleCallTokenTransaction.class.getName());
    TransactionLogger transactionLogger=new TransactionLogger(SingleCallTokenTransaction.class.getName());
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
        //Prepare business logic
        logger.debug("Entering into doProcess");
        PrintWriter printWriter = response.getWriter();
        String checkAlgorithm=null;
        String key=null;

        String checksum="";
        String partnerId="";
        String toId="";
        String cardholderFirstName ="";
        String cardholderLastName ="";
        String cardholderEmail = "";
        String telNo = "";
        String birthDate = "";
        String gender = "";
        String zip = "";

        partnerId=request.getParameter("partnerid");
        checksum=request.getParameter("checksum");
        cardholderLastName =request.getParameter("lastname");
        cardholderFirstName =request.getParameter("firstname");
        cardholderEmail=request.getParameter("emailaddr");
        telNo=request.getParameter("telno");
        birthDate=request.getParameter("birthdate");
        gender=request.getParameter("gender");
        zip=request.getParameter("zip");

        String status = "N";
        String statusMsg = "";
        String cardholderId = "";

        Functions functions = new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;

        String responseType="";
        String responseLength="";

        TransactionUtility transactionUtility=new TransactionUtility();
        if(!ESAPI.validator().isValidInput("partnerid",partnerId, "Numbers", 10, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "", "N", "Invalid partnerId", key, checkAlgorithm,responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,"","N","Invalid Checksum.",key,checkAlgorithm,responseType,responseLength);
            return;
        }

        PartnerManager partnerManager=new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            partnerDetailsVO=partnerManager.getPartnerDetails(partnerId);
            if(partnerDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "", "N", "Unauthorised Partner", key, checkAlgorithm, responseType,responseLength);
                return;
            }
            responseType=partnerDetailsVO.getResponseType();
            responseLength=partnerDetailsVO.getResponseLength();
        }
        catch (Exception gve)
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "", "N", "Internal error while processing you request.",key,checkAlgorithm,responseType,responseLength);
            return;
        }

        if ("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "","N", "Tokenization feature has not been activated to partner account", key, checkAlgorithm,responseType,responseLength);
            return;
        }

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
        {
            if(!ESAPI.validator().isValidInput("toid",request.getParameter("toid"), "Numbers", 20, false))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "", "N", "Invalid ToId", key, checkAlgorithm, responseType,responseLength);
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
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "","N", "Internal error while processing you request.", key, checkAlgorithm, responseType,responseLength);
                return;
            }
            if(merchantDetailsVO!=null && !functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "","N", "Unauthorized User", key,checkAlgorithm,responseType,responseLength);
                return;
            }
            key=merchantDetailsVO.getKey();
            checkAlgorithm=merchantDetailsVO.getChecksumAlgo();
        }
        else
        {
            key=partnerDetailsVO.getPartnerKey();
        }

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
        {
            if ("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, "","N", "Tokenization feature has not been activated to merchant account", key, checkAlgorithm,responseType,responseLength);
                return;
            }
        }

        try
        {
            if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,"","N","Invalid Checksum.",key,checkAlgorithm,responseType,responseLength);
                return;
            }
            if (!Checksum.verifyChecksumV2(partnerId, cardholderFirstName, cardholderLastName, cardholderEmail, key, checksum, checkAlgorithm))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,"","N","Checksum mismatch.",key,checkAlgorithm,responseType,responseLength);
                return;
            }

            String errorMsg = "";
            CardholderResponseVO cardholderResponseVO=null;
            TokenManager tokenManager = new TokenManager();
            errorMsg = tokenManager.cardholderRegistrationDirectAPIValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,"","N",errorMsg,key,checkAlgorithm,responseType,responseLength);
                return;
            }

            CardHolderRequestVO cardHolderRequestVO=new CardHolderRequestVO();
            cardHolderRequestVO.setToId(toId);
            cardHolderRequestVO.setFirstName(cardholderFirstName);
            cardHolderRequestVO.setLastName(cardholderLastName);
            cardHolderRequestVO.setEmail(cardholderEmail);
            cardHolderRequestVO.setTelNo(telNo);
            cardHolderRequestVO.setBirthDate(birthDate);
            cardHolderRequestVO.setGender(gender);
            cardHolderRequestVO.setZip(zip);
            cardHolderRequestVO.setPartnerId(partnerId);
            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                cardholderResponseVO = tokenManager.registerCardholder(cardHolderRequestVO);
            }
            else
            {
                cardholderResponseVO= tokenManager.registerCardholderByPartner(cardHolderRequestVO);
            }

            if ("Y".equals(cardholderResponseVO.getStatus()))
            {
                cardholderId = cardholderResponseVO.getCardholderId();
            }
            status = cardholderResponseVO.getStatus();
            statusMsg =cardholderResponseVO.getStatusDescription();
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,cardholderId,status,statusMsg,key,checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (PZGenericConstraintViolationException gve)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("PZGenericConstraintViolationException::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, cardholderId, status, statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("NoSuchAlgorithmException::",e);
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter,cardholderId,status,statusMsg,key,checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (Exception gve)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("Exception::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStatusCardholderRegistration(printWriter, cardholderId, status, statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
    }
}
