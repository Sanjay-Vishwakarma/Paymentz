package payment;

import com.directi.pg.Checksum;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PartnerManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sneha on 12/24/2015.
 */
public class SingleCallInvalidateToken extends HttpServlet
{
    Logger logger=new Logger(SingleCallInvalidateToken.class.getName());
    TransactionLogger transactionLogger=new TransactionLogger(SingleCallInvalidateToken.class.getName());

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
        transactionLogger.debug("Entering into SingleCallInvalidateToken doProccess::");
        PrintWriter pWriter = response.getWriter();
        TransactionUtility transactionUtility = new TransactionUtility();

        String checksum="";
        String partnerId="";
        String toId="";
        String token="";
        String key = "";

        String checkAlgorithm = "";
        partnerId = request.getParameter("partnerid");
        toId = request.getParameter("toid");
        token = request.getParameter("token");
        checksum = request.getParameter("checksum");

        String responseType = "";
        String responseLength = "";

        if(!ESAPI.validator().isValidInput("partnerid",partnerId, "Numbers", 20, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Invalid partnerId", token, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Invalid checksum", token, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("token", token, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Invalid token", token, key, checkAlgorithm,responseType,responseLength);
            return;
        }

        Functions functions = new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();

        PartnerManager partnerManager=new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            partnerDetailsVO=partnerManager.getPartnerDetails(partnerId);
            if(partnerDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Unauthorised Partner", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            responseType=partnerDetailsVO.getResponseType();
            responseLength=partnerDetailsVO.getResponseLength();

        }
        catch (Exception gve)
        {
            transactionLogger.error("Exception:::::"+gve);
            logger.error("Exception:::::"+gve);
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Internal error while processing you request.", token, key, checkAlgorithm,responseType,responseLength);
            return;
        }

        if("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Tokenization feature has not been activated to your partner account", token, key, checkAlgorithm,responseType,responseLength);
            return;
        }

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
        {
            if(!ESAPI.validator().isValidInput("toid",request.getParameter("toid"), "Numbers", 20, false))
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Invalid ToId", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }
            else
            {
                toId = request.getParameter("toid");
            }
            MerchantDetailsVO merchantDetailsVO=null;
            try
            {
                merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                key=merchantDetailsVO.getKey();
                checkAlgorithm=merchantDetailsVO.getChecksumAlgo();
            }
            catch ( PZDBViolationException gve)
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Internal error while processing you request.", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            if(merchantDetailsVO!=null && !functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Unauthorized User.", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            if ("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter, partnerId, toId, "N", "Tokenization feature has not been activated to your merchant account", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }
        }
        else
        {
            key=partnerDetailsVO.getPartnerKey();
            toId="";
        }

        try
        {
            if(!Checksum.verifyInvalidateTokenChecksum(partnerId, token, key, checksum,checkAlgorithm))
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Checksum mismatch", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            TokenManager tokenManager = new TokenManager();
            TokenDetailsVO tokenDetailsVO=null;
            if("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
            {
                tokenDetailsVO=tokenManager.getTokenDetailsByPartner(partnerId,token);
            }
            else
            {
                tokenDetailsVO = tokenManager.getTokenDetails(toId,token);
            }

            if(tokenDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Token not found", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            if("N".equals(tokenDetailsVO.getIsActive()))
            {
                transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Token has already invalidated", token, key, checkAlgorithm,responseType,responseLength);
                return;
            }
            else
            {
                String tokenId = tokenDetailsVO.getTokenId();
                String isTokenInactive = tokenManager.doTokenInactive(tokenId);
                if("success".equals(isTokenInactive))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "Y", "Token invalidated successfully", token, key, checkAlgorithm,responseType,responseLength);
                    return;
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException::",e);
            logger.error("NoSuchAlgorithmException::",e);
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Internal error while processing your request", token, key, checkAlgorithm,responseType,responseLength);
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException::", e);
            logger.error("PZDBViolationException::", e);
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Internal error while processing your request", token, key, checkAlgorithm,responseType,responseLength);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception::", e);
            logger.error("Exception::", e);
            transactionUtility.calculateCheckSumAndWriteStatusInvalidateToken(pWriter,partnerId,toId, "N", "Internal error while processing your request", token, key, checkAlgorithm,responseType,responseLength);
        }
    }
}
