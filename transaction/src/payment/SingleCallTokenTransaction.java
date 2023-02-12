package payment;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.manager.*;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ResponseLength;
import com.manager.vo.*;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.transaction.utils.TransactionCoreUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import payment.util.ReadRequest;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 4/6/15
 * Time: 03:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallTokenTransaction extends HttpServlet
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

        PrintWriter printWriter=response.getWriter();

        CommonValidatorVO commonValidatorVO=ReadRequest.getRequestParametersForSale(request);

        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();

        //TransactionUtils transactionUtils=new TransactionUtils();
        TransactionCoreUtils transactionUtils = new TransactionCoreUtils();
        PaymentManager paymentManager=new PaymentManager();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        TransactionUtility transactionUtility=new TransactionUtility();
        TokenManager tokenManager=new TokenManager();
        Functions functions=new Functions();

        String remoteAddr = Functions.getIpAddress(request);
        int serverPort = request.getServerPort();
        String servletPath = request.getServletPath();
        String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
        ServletContext application=getServletContext();
        String token=request.getParameter("token");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
        Date date=new Date();
        MerchantDAO merchantDAO=new MerchantDAO();
        Transaction transaction=new Transaction();
        String key="";
        String checksumAlgorithm="";
        String responseType = "";
        String responseLength = "";

        commonValidatorVO.getTransDetailsVO().setHeader(header);

        if(!ESAPI.validator().isValidInput("toid", commonValidatorVO.getMerchantDetailsVO().getMemberId(), "Numbers", 10, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid ToId.", commonValidatorVO.getTransDetailsVO().getAmount(),"","",responseType,responseLength);
            return;
        }
        if(!ESAPI.validator().isValidInput("token", token, "SafeString", 50, false))
        {
            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid Token.", commonValidatorVO.getTransDetailsVO().getAmount(),"","",responseType,responseLength);
            return;
        }

        User user = new DefaultUser(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        ESAPI.authenticator().setCurrentUser(user);

        String error="";
        String status="";
        int paymentType=0;
        int cardType=0;
        int terminalId=0;
        String accountId="";
        String cardEncryptionEnable = "N";
        MerchantDetailsVO merchantConfDetails=null;
        try
        {
            Merchants merchants=new Merchants();
            PartnerManager partnerManager=new PartnerManager();
            TerminalManager terminalManager = new TerminalManager();
            PartnerDetailsVO partnerDetailsVO=null;

            if(merchants.isAuthenticMember(merchantDetailsVO.getMemberId(),commonValidatorVO.getTransDetailsVO().getTotype()))
            {
                merchantConfDetails= merchantDAO.getMemberDetails(merchantDetailsVO.getMemberId());
                if(functions.isValueNull(merchantConfDetails.getKey()))
                {
                    key=merchantConfDetails.getKey();
                    checksumAlgorithm=merchantConfDetails.getChecksumAlgo();
                    cardEncryptionEnable = merchantConfDetails.getIsCardEncryptionEnable();
                    if ("Y".equals(cardEncryptionEnable))
                    {
                        try
                        {
                            AESEncryptionManager encryptionManager=new AESEncryptionManager();
                            commonValidatorVO.getCardDetailsVO().setcVV(encryptionManager.decrypt(commonValidatorVO.getCardDetailsVO().getcVV(),key));
                        }
                        catch (Exception e)
                        {
                            transactionLogger.error("Invalid Data Encyption::::::"+e);
                            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid Data Encyption", commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm,responseType,responseLength);
                            return;
                        }
                    }
                }

                partnerDetailsVO=partnerManager.getPartnerDetails(merchantConfDetails.getPartnerId());
                if(partnerDetailsVO==null)
                {
                    transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid partner account", commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm,responseType,responseLength);
                    return;
                }

                responseType=partnerDetailsVO.getResponseType();
                responseLength=partnerDetailsVO.getResponseLength();

                if("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Tokenization feature has not been activated for your partner account", commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                    return;
                }

                if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                {
                    if("N".equals(merchantConfDetails.getIsTokenizationAllowed()))
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Tokenization feature not been activated to your merchant account", commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                        return;
                    }
                }

                if(functions.isValueNull(commonValidatorVO.getTerminalId()) && functions.isNumericVal(commonValidatorVO.getTerminalId()))
                {
                    terminalId = Integer.parseInt(commonValidatorVO.getTerminalId());
                }

                if(merchantConfDetails.getAutoSelectTerminal().equals("Y") && terminalId==0)
                {
                    if(!ESAPI.validator().isValidInput("paymenttype", commonValidatorVO.getPaymentType(), "Numbers", 5,false) || !functions.isNumericVal(commonValidatorVO.getPaymentType()))
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid paymodeid.", commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm,responseType,responseLength);
                        return;
                    }
                    if(!ESAPI.validator().isValidInput("cardtype", commonValidatorVO.getCardType(), "Numbers", 5, false) || !functions.isNumericVal(commonValidatorVO.getCardType()))
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid cardtypeid.", commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm,responseType,responseLength);
                        return;
                    }
                    accountId = transaction.getAccountId(merchantDetailsVO.getMemberId(), Integer.parseInt(commonValidatorVO.getPaymentType()),Integer.parseInt(commonValidatorVO.getCardType()));
                    commonValidatorVO.setTerminalId(String.valueOf(transaction.getTerminalId(accountId, merchantDetailsVO.getMemberId(),Integer.parseInt(commonValidatorVO.getPaymentType()),Integer.parseInt(commonValidatorVO.getCardType()))));
                }
                else
                {
                    if (!ESAPI.validator().isValidInput("terminalid", commonValidatorVO.getTerminalId(), "Numbers", 6, false) || !functions.isNumericVal(commonValidatorVO.getTerminalId()))
                    {
                        error= "Invalid terminal.";
                        transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", error, commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm,responseType,responseLength);
                        return;
                    }
                }

                if (!terminalManager.isTokenizationActiveOnTerminal(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId()))
                {
                    transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Tokenization feature not activated to your terminal", commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                    return;
                }

                TokenDetailsVO tokenDetailsVO=new TokenDetailsVO();
                if("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
                {
                    //tokenDetailsVO=tokenManager.getTokenDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId(),token);
                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByMerchant(commonValidatorVO.getMerchantDetailsVO().getMemberId(), token, commonValidatorVO, tokenDetailsVO);
                }
                else
                {
                    //tokenDetailsVO=tokenManager.getTokenDetailsByPartner(partnerDetailsVO.getPartnerId(),token);
                    tokenDetailsVO = tokenManager.getRegisteredTokenDetailsByPartner(merchantDetailsVO.getPartnerId(), token, commonValidatorVO); //token generated by PartnerId
                }

                if(tokenDetailsVO!=null && functions.isValueNull(tokenDetailsVO.getTokenId()))
                {
                    if("Y".equals(tokenDetailsVO.getIsActive()))
                    {
                        DateDetailsVO dateDetailsVO=tokenManager.getDateDifference(tokenDetailsVO.getCreationOn(), targetFormat.format(date));
                        if(tokenManager.isTokenExpired(dateDetailsVO,tokenDetailsVO.getTokenValidDays()))
                        {
                            //Do token As Inactive and return token expired message.
                            status=tokenManager.doTokenInactive(tokenDetailsVO.getTokenId());
                            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Token has been expired.", commonValidatorVO.getTransDetailsVO().getAmount(), key,checksumAlgorithm,responseType,responseLength);
                            return;
                        }

                        CommCardDetailsVO commCardDetailsVO=tokenDetailsVO.getCommCardDetailsVO();

                        GenericAddressDetailsVO addressDetailsVO=tokenDetailsVO.getAddressDetailsVO();
                        if("N".equals(tokenDetailsVO.getIsAddrDetailsRequired()))
                        {
                            //card holder ip is taken as input.
                            //rerive the request ip form request object
                            addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                            addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());

                            //Address detail VO =====cardholderIp+requestIp+address details during card registration
                            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
                        }
                        else
                        {
                            commonValidatorVO.getAddressDetailsVO().setFirstname(addressDetailsVO.getFirstname());
                            commonValidatorVO.getAddressDetailsVO().setLastname(addressDetailsVO.getLastname());
                            //commonValidatorVO.getAddressDetailsVO().setEmail(addressDetailsVO.getEmail());
                        }

                        //cvv is taken as input
                        commonValidatorVO.getCardDetailsVO().setCardNum(commCardDetailsVO.getCardNum());
                        commonValidatorVO.getCardDetailsVO().setExpMonth(commCardDetailsVO.getExpMonth());
                        commonValidatorVO.getCardDetailsVO().setExpYear(commCardDetailsVO.getExpYear());
                        commonValidatorVO.getCardDetailsVO().setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                        if(!functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                            commonValidatorVO.getTransDetailsVO().setCurrency(tokenDetailsVO.getCurrency());

                        CommonInputValidator commonInputValidator=new CommonInputValidator();
                        commonValidatorVO.setToken(token);

                        commonValidatorVO = commonInputValidator.performTokenTransactionValidation(commonValidatorVO);
                        if(commonValidatorVO.getErrorMsg()!=null && commonValidatorVO.getErrorMsg().trim().length()>0)
                        {
                            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", commonValidatorVO.getErrorMsg(), commonValidatorVO.getTransDetailsVO().getAmount(), key,checksumAlgorithm,responseType,responseLength);
                            return;
                        }

                        int trackingId= paymentManager.insertBegunTransactionEntry(commonValidatorVO, header);
                        commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                        commonValidatorVO.setResponseType(responseType);
                        commonValidatorVO.setResponseLength(responseLength);
                        VTResponseVO vtResponseVO = transactionUtils.singleCall(commonValidatorVO, application);


                        TokenTransactionDetailsVO tokenTransactionDetailsVO=new TokenTransactionDetailsVO();
                        tokenTransactionDetailsVO.setTokenId(tokenDetailsVO.getTokenId());
                        tokenTransactionDetailsVO.setToid(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        tokenTransactionDetailsVO.setTrackingid(vtResponseVO.getTrackingId());
                        tokenTransactionDetailsVO.setAmount(vtResponseVO.getResAmount());
                        tokenTransactionDetailsVO.setDescription(vtResponseVO.getOrderId());

                        tokenManager.manageTokenTransactionDetails(tokenTransactionDetailsVO);
                        /*if(!functions.isValueNull(tokenDetailsVO.getDescription()) && !functions.isValueNull(tokenDetailsVO.getTrackingId()))
                        {
                            //update the token master with trackingid and merchant orderid
                            status=tokenManager.updateTokenMaster(tokenTransactionDetailsVO);
                            if("success".equals(status))
                            {
                                logger.debug("Token Master Updated Successfully");
                            }
                        }*/

                        String resStatus="";
                        if("Y".equals(vtResponseVO.getIsSuccessful()))
                        {
                            resStatus="Y";
                        }
                        else
                        {
                            resStatus="N";
                        }

                        if(ResponseLength.FULL.toString().equals(responseLength))
                        {
                            transactionUtility.calculateCheckSumAndWriteTokenTransactionFullResponse(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), resStatus, vtResponseVO.getStatusDescription(), commonValidatorVO.getTransDetailsVO().getAmount(), key, checksumAlgorithm, responseType, vtResponseVO);
                            return;
                        }
                        else
                        {
                            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), vtResponseVO.getTrackingId(), resStatus, vtResponseVO.getStatusDescription(), commonValidatorVO.getTransDetailsVO().getAmount(), key,checksumAlgorithm,responseType,responseLength);
                            return;
                        }

                    }
                    else
                    {
                        transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Token has been expired.",commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                        return;
                    }
                }
                else
                {
                    transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Token not found.", commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                    return;
                }
            }
            else
            {
                transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", "Invalid Authentication.", commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            transactionLogger.error("----PZConstraintViolationException in SingleCallTokenTransaction------", cve);
            PZExceptionHandler.handleCVEException(cve, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.TOKEN_SALE);
            transactionLogger.error("----PZConstraintViolationException in SingleCallTokenTransaction------" + errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO()));
            error=errorCodeUtils.getSystemErrorCodeVO(cve.getPzConstraint().getErrorCodeListVO());
            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", error, commonValidatorVO.getTransDetailsVO().getAmount(), key,checksumAlgorithm,responseType,responseLength);
            return;
        }
        catch (PZGenericConstraintViolationException gve)
        {
            transactionLogger.error("---PZGenericConstraintViolationException in SingleCallTokenTransaction----", gve);
            PZExceptionHandler.handleGenericCVEException(gve, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.TOKEN_SALE);
            error = "Internal error while processing your request.";
            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", error, commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
            return;
        }
        catch (Exception gve)
        {
            transactionLogger.error("---PZGenericConstraintViolationException in SingleCallTokenTransaction----", gve);
            error = "Internal error while processing your request.";
            transactionUtility.calculateCheckSumAndWriteStatusTokenTransaction(printWriter, commonValidatorVO.getTransDetailsVO().getOrderId(), "", "N", error, commonValidatorVO.getTransDetailsVO().getAmount(),key,checksumAlgorithm,responseType,responseLength);
            return;
        }

    }
}
