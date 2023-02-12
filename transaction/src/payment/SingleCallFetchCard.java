package payment;

import com.directi.pg.Checksum;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.PartnerManager;
import com.manager.TokenManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ResponseType;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import org.owasp.esapi.ESAPI;
import payment.util.WriteJSONResponse;
import payment.util.WriteXMLResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 12/16/15
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallFetchCard extends HttpServlet
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
        PrintWriter printWriter = response.getWriter();
        String checkAlgorithm=null;
        String key=null;

        String checksum="";
        String toId="";
        String partnerId="";
        String toType="";
        String cardholderId = "";

        partnerId=request.getParameter("partnerid");
        checksum=request.getParameter("checksum");
        cardholderId=request.getParameter("cardholderid");

        String status = "N";
        String statusMsg = "";
        String errorMsg = "";

        Functions functions = new Functions();
        MerchantDAO merchantDAO=new MerchantDAO();

        String responseType="";
        String responseLength="";

        TransactionUtility transactionUtility=new TransactionUtility();

        if(!ESAPI.validator().isValidInput("partnerid",partnerId, "Numbers", 20, false))
        {
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter, partnerId,toId, cardholderId, "", "N", "Invalid PartnerId", "", "",responseType,responseLength);
            return;
        }
        if (!ESAPI.validator().isValidInput("checksum", checksum, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"","N", "Invalid Checksum", "", "",responseType,responseLength);
            return;
        }


        PartnerManager partnerManager=new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=null;
        try
        {
            partnerDetailsVO=partnerManager.getPartnerDetails(partnerId);
            if(partnerDetailsVO==null)
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"","N", "Unauthorised Partner", "", "",responseType,responseLength);
                return;
            }
            responseType = partnerDetailsVO.getResponseType();
            responseLength = partnerDetailsVO.getResponseLength();

        }
        catch (Exception gve)
        {
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,"","","","","N", "Internal error while processing you request.", "", "",responseType,responseLength);
            return;

        }

        if("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", "N", "Tokenization feature has not been activated to your partner account", key, checkAlgorithm,responseType,responseLength);
            return;
        }

        if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
        {
            if(!ESAPI.validator().isValidInput("toid",request.getParameter("toid"), "Numbers", 20, false))
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", "N", "Invalid ToId", "", "",responseType,responseLength);
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
            }
            catch ( PZDBViolationException gve)
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", "N", "Internal error while processing you request.", "", "",responseType,responseLength);
                return;
            }
            if(merchantDetailsVO!=null && !functions.isValueNull(merchantDetailsVO.getMemberId()))
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", "N", "Unauthorized User.", "", "",responseType,responseLength);
                return;
            }

            key=merchantDetailsVO.getKey();
            checkAlgorithm=merchantDetailsVO.getChecksumAlgo();
            toType=merchantDetailsVO.getPartnerName();

            if("N".equals(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", "N", "Tokenization feature has not been activated to your merchant account", key, checkAlgorithm,responseType,responseLength);
                return;
            }
        }
        else
        {
            key=partnerDetailsVO.getPartnerKey();
            toType=partnerDetailsVO.getCompanyName();
        }

        try
        {
            if (!Checksum.verifyChecksumWithV3(partnerId,toType,key,checksum,checkAlgorithm))
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"","N", "Checksum mismatch", key,checkAlgorithm,responseType,responseLength);
                return;
            }

            TokenManager tokenManager = new TokenManager();
            errorMsg = tokenManager.listOFCardsDirectAPIValidation(request);
            if (functions.isValueNull(errorMsg))
            {
                transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"","N", errorMsg, key, checkAlgorithm,responseType,responseLength);
                return;
            }

            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                if(functions.isValueNull(cardholderId))
                {
                    if(!tokenManager.isCardholderRegistered(toId,cardholderId))
                    {
                        status="N";
                        statusMsg="Requested cardholder not found";
                        transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId, cardholderId, "", status, statusMsg, key, checkAlgorithm,responseType,responseLength);
                        return;
                    }
                }
            }
            else
            {
                if(functions.isValueNull(cardholderId))
                {
                    if(!tokenManager.isCardholderRegisteredWithPartner(partnerId,cardholderId))
                    {
                        status="N";
                        statusMsg="Requested cardholder not found";
                        transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"",status,statusMsg, key, checkAlgorithm,responseType,responseLength);
                        return;
                    }
                }

            }
            List<TokenDetailsVO> tokenDetailsVOs=null;
            if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                if(functions.isValueNull(cardholderId))
                {
                    tokenDetailsVOs=tokenManager.getCustomerStoredCards(toId,cardholderId);
                }
                else
                {
                    tokenDetailsVOs=tokenManager.getCustomerStoredCardsWithoutCardholderId(toId);
                }
            }
            else
            {
                if(functions.isValueNull(cardholderId))
                {
                    tokenDetailsVOs=tokenManager.getCustomerStoredCardsByPartner(partnerId, cardholderId);
                }
                else
                {
                    tokenDetailsVOs=tokenManager.getCustomerStoredCardsByPartnerWithoutCardholderId(partnerId);
                }
            }
            StringBuffer innerData = new StringBuffer();
            HashMap responseMap = new HashMap();
            if(tokenDetailsVOs.size()>0)
            {
                if(ResponseType.XML.toString().equals(responseType) || ResponseType.STRING.toString().equals(responseType))
                {
                    for(TokenDetailsVO tokenDetailsVO:tokenDetailsVOs)
                    {
                        responseMap.put("cardtoken", tokenDetailsVO.getToken());
                        responseMap.put("cardtype", Functions.getCardType(tokenDetailsVO.getCommCardDetailsVO().getCardNum()));
                        responseMap.put("cardnumber", tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(0, 6) + "******" + tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(tokenDetailsVO.getCommCardDetailsVO().getCardNum().length() - 4));
                        responseMap.put("expirymonth", tokenDetailsVO.getCommCardDetailsVO().getExpMonth());
                        responseMap.put("expiryyear", tokenDetailsVO.getCommCardDetailsVO().getExpYear());
                        responseMap.put("tokenstatus", tokenDetailsVO.getIsActive());

                        innerData.append(WriteXMLResponse.writeInnerXMLResponse(responseMap, "card"));
                    }
                }
                else if(ResponseType.JSON.toString().equals(responseType))
                {
                    StringBuffer initialJSON = new StringBuffer();
                    String str = "";
                    for(TokenDetailsVO tokenDetailsVO : tokenDetailsVOs)
                    {
                        responseMap.put("cardtoken", tokenDetailsVO.getToken());
                        responseMap.put("cardtype", Functions.getCardType(tokenDetailsVO.getCommCardDetailsVO().getCardNum()));
                        responseMap.put("cardnumber", tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(0, 6) + "******" + tokenDetailsVO.getCommCardDetailsVO().getCardNum().substring(tokenDetailsVO.getCommCardDetailsVO().getCardNum().length() - 4));
                        responseMap.put("expirymonth", tokenDetailsVO.getCommCardDetailsVO().getExpMonth());
                        responseMap.put("expiryyear", tokenDetailsVO.getCommCardDetailsVO().getExpYear());
                        responseMap.put("tokenstatus", tokenDetailsVO.getIsActive());

                        initialJSON.append(WriteJSONResponse.writeJSONResponse(responseMap)+ ",");
                    }
                    str = (initialJSON.toString().substring(0,initialJSON.toString().length()-1));
                    logger.debug("initialJSON---->"+str);
                    innerData.append("card:["+str+"]");
                }
            }
            else
            {
                if(ResponseType.XML.toString().equals(responseType) || ResponseType.STRING.toString().equals(responseType))
                {
                    innerData.append("<card>No cards found</card>");
                }
                else if(ResponseType.JSON.toString().equals(responseType))
                {
                    innerData.append("No cards found");
                }
            }

            logger.debug("DATA--->"+innerData);
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,innerData.toString(),"Y", "Your request processed successfully", key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (PZGenericConstraintViolationException gve)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("PZGenericConstraintViolationException::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"",status,statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("NoSuchAlgorithmException::",e);
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"",status,statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
        catch (Exception gve)
        {
            status = "N";
            statusMsg = "Internal error while processing your request.";
            transactionLogger.error("Exception::::" + gve);
            transactionUtility.calculateCheckSumAndWriteStoredCards(printWriter,partnerId,toId,cardholderId,"",status,statusMsg, key, checkAlgorithm,responseType,responseLength);
            return;
        }
    }
}
