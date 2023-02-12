package transaction;

import com.directi.pg.FailedTransactionLogEntry;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.DirectTransactionManager;
import com.manager.PartnerManager;
import com.manager.helper.TransactionHelper;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.response.PZResponseStatus;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.transaction.utils.ReadDirectTransactionRequest;
import com.transaction.utils.TransactionWSUtils;
import com.transaction.utils.WriteDirectTransactionResponse;
import com.transaction.vo.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 6/8/15
 * Time: 6:24 PM
 * To change this template use File | Settings | File Templates.
 */
@Path("/DirectTransaction")
public class DirectTransactionRESTImpl implements DirectTransactionRESTService
{
    private DirectTransactionManager directTransactionManager = new DirectTransactionManager();
    private ReadDirectTransactionRequest readDirectTransactionRequest = new ReadDirectTransactionRequest();
    private WriteDirectTransactionResponse writeDirectTransactionResponse = new WriteDirectTransactionResponse();
    private Logger logger = new Logger(DirectTransactionImpl.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(DirectTransactionImpl.class.getName());

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Context
    HttpServletRequest request1;

    public DirectTransactionResponse processTransaction(DirectTransactionRequest directTransactionRequest)
    {
        //ResponseVo of the system
        //List<DirectKitResponseVO> directKitResponseVOList = new ArrayList<DirectKitResponseVO>();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        //Response Vo of the client
        DirectTransactionResponse directTransactionResponse = new DirectTransactionResponse();
        List<DirectTransactionErrorCode> directTransactionErrorCodeVOs=null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        //errorCode VO  of system and client
        //List<ErrorCodeListVO> errorCodeListVOList = new ArrayList<ErrorCodeListVO>();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        CommonInputValidator commonInputValidator = new CommonInputValidator();
        TransactionWSUtils transactionWSUtils = new TransactionWSUtils();
        Functions functions = new Functions();

        //Read Request
        CommonValidatorVO directKitValidatorVO = readDirectTransactionRequest.readRequestParameterForWebServiceDirectKit(directTransactionRequest);

        String sOrderId = directKitValidatorVO.getTransDetailsVO().getOrderId();
        String sAmount = directKitValidatorVO.getTransDetailsVO().getAmount();
        if(directKitValidatorVO==null)
        {
            String error = "";
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Please provide Request Attribute while placing transaction"));
            try
            {
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--->",e);
            }
            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while placing transaction", null);
        }
        else
        {
            //this is additional mapping for commonValidator to be set from request
            String IpAddress=request1.getRemoteAddr();

            String reqIp = "";

            if(IpAddress.contains(","))
            {
                String sIp[] = IpAddress.split(",");
                reqIp = sIp[0].trim();
            }
            else
            {
                reqIp = IpAddress;
            }

            directKitValidatorVO.getAddressDetailsVO().setIp(reqIp);
            directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath()); //todo not trust worthy
            directKitValidatorVO.getAddressDetailsVO().setRequestedHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath()); //todo not trust worthy
            logger.debug("REQUEST DATA:::::"+directKitValidatorVO.getTransDetailsVO().getHeader());
            directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);

            /*User user = new DefaultUser(directKitValidatorVO.getParetnerId());
            ESAPI.authenticator().setCurrentUser(user);*/
            TransactionHelper transactionHelper=new TransactionHelper();

            String error=null;
            try
            {
                error = readDirectTransactionRequest.checkValueNumeric(directKitValidatorVO.getPaymentType(), directKitValidatorVO.getCardType(), directKitValidatorVO.getTerminalId(), errorCodeListVO,directKitValidatorVO);
                if (!errorCodeListVO.getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, error, null);
                }
                else
                {

                    if (!functions.isValueNull(directKitValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(directKitValidatorVO.getParetnerId()))
                    {
                        error = "Please provide Request Attribute while placing transaction";
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Please provide Request Attribute while placing transaction"));
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while placing transaction",null);
                    }

                    transactionLogger.debug("before start flow---");
                    transactionLogger.debug("member id before if-----"+directKitValidatorVO.getMerchantDetailsVO().getMemberId());
                    transactionLogger.debug("partner id id before if-----"+directKitValidatorVO.getParetnerId());
                    transactionLogger.debug("partner id id before if-----"+functions.isValueNull(directKitValidatorVO.getMerchantDetailsVO().getMemberId()));
                    transactionLogger.debug("partner id id before if-----"+functions.isValueNull(directKitValidatorVO.getParetnerId()));
                    if (functions.isValueNull(directKitValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(directKitValidatorVO.getParetnerId()))
                    {
                        transactionLogger.debug("Inside merchantid and blank partner---");

                        //Todo - call decrypt function

                        directKitValidatorVO = commonInputValidator.getMerchantDetailsForIFE(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                        }

                        directKitValidatorVO = commonInputValidator.decryptCardDetailsForIFEMerchant(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                        }

                        directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                        }

                        commonInputValidator.checksumverificationForMember(directKitValidatorVO);

                        if ("Y".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getSplitPaymentAllowed()))
                        {

                            String splitPaymentDetail = directKitValidatorVO.getSplitPaymentVO().getSplitPaymentDetail();

                            String splitValue[] = splitPaymentDetail.split(",");
                            HashMap amountMap = new HashMap();
                            HashMap terminalMap = new HashMap();

                            for (int i = 0; i < splitValue.length; i++)
                            {
                                String splitPayIdAndAmount[] = splitValue[i].split("=");
                                logger.debug("s----" + splitPayIdAndAmount[0] + "-" + splitPayIdAndAmount[1]);

                                terminalMap.put(i, splitPayIdAndAmount[0]);
                                amountMap.put(i, splitPayIdAndAmount[1]);
                                directKitValidatorVO.setTrackingid("");
                                transactionLogger.debug("terminal map----" + terminalMap.get(0));

                                directKitValidatorVO.setTerminalId(terminalMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());

                                try
                                {
                                    directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);
                                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                    {
                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), splitValue[i]);
                                    }
                                    else
                                    {

                                        directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);

                                        writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                                    }
                                }
                                catch (PZTechnicalViolationException tve)
                                {
                                    logger.error("PZ Technical exception while placing transaction via WebService", tve);
                                    transactionLogger.error("PZ Technical exception while placing transaction via WebService", tve);

                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", splitValue[i]);
                                    PZExceptionHandler.handleTechicalCVEException(tve, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                                }
                                catch (PZConstraintViolationException cve)
                                {
                                    logger.error("PZ Constraint exception while placing transaction via WebService", cve);
                                    transactionLogger.error("PZ Constraint exception while placing transaction via WebService", cve);

                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage(), splitValue[i]);
                                    PZExceptionHandler.handleCVEException(cve, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                                }
                                catch (PZDBViolationException dbe)
                                {
                                    logger.error("PZ DB exception while placing transaction via WebService", dbe);
                                    transactionLogger.error("PZ DB exception while placing transaction via WebService", dbe);

                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", splitValue[i]);
                                    PZExceptionHandler.handleDBCVEException(dbe, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                                }
                                catch (PZGenericConstraintViolationException gce)
                                {
                                    logger.error("PZ GenericConstraint exception while placing transaction via WebService", gce);
                                    transactionLogger.error("PZ GenericConstraint exception while placing transaction via WebService", gce);

                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, gce.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, gce.getPzGenericConstraint().getMessage(), splitValue[i]);
                                    PZExceptionHandler.handleGenericCVEException(gce, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                                }
                                catch (Exception e)
                                {
                                    logger.error("Exception while placing transaction via WebService", e);
                                    transactionLogger.error("GenericException while placing transaction via WebService", e);

                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal error occur:::", splitValue[i]);
                                }

                                logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                                logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                                logger.debug("Final Split Status---" + directTransactionResponse.getStatus());
                                logger.debug("Final Split Status---" + directTransactionResponse.getSplitTransactionId());

                                directTransactionResponse.setAmount(sAmount);
                                directTransactionResponse.setOrderId(sOrderId);
                                //Based on the
                                directTransactionResponse.setStatus(transactionWSUtils.getFinalStatus(directTransactionResponse.getStatus()));

                                logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                                logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                                logger.debug("Final Split Status---" + directTransactionResponse.getStatus());
                            }
                        }
                        else
                        {
                            //ToDO -Please check if code is required or not
                            directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                            if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                            }



                            directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);

                            if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                            }
                            else
                            {
                                directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                                writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                            }
                        }
                    }
                    else if (/*(!functions.isValueNull(directKitValidatorVO.getMerchantDetailsVO().getMemberId()) || directKitValidatorVO.getMerchantDetailsVO().getMemberId().equalsIgnoreCase("0")) && */functions.isValueNull(directKitValidatorVO.getParetnerId()))
                    {
                        transactionLogger.debug("Inside partner id and blank merchant---"+directKitValidatorVO.getParetnerId());
                        transactionLogger.debug("Inside partner id and blank merchant---"+!functions.isValueNull(directKitValidatorVO.getParetnerId()));
                        transactionLogger.debug("Inside partner id and blank merchant---"+!functions.isValueNull(directKitValidatorVO.getMerchantDetailsVO().getMemberId()));
                        PartnerDetailsVO partnerDetailsVO = null;
                        PartnerManager partnerManager = new PartnerManager();

                        partnerDetailsVO = partnerManager.getPartnerDetailsForIFE(directKitValidatorVO.getParetnerId());
                        transactionLogger.debug("key for partner----" + partnerDetailsVO.getPartnerKey());
                        transactionLogger.debug("split payment for partner----" + partnerDetailsVO.getSplitPaymentAllowed());
                        directKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

                        directKitValidatorVO.getPartnerDetailsVO().setPartnerKey(partnerDetailsVO.getPartnerKey());

                        //ToDo - Decrypt the card

                        directKitValidatorVO = commonInputValidator.decryptCardDetailsForIFEPartner(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                        }

                        commonInputValidator.checksumVerificationForPartner(directKitValidatorVO);

                        if ("Y".equalsIgnoreCase(partnerDetailsVO.getSplitPaymentAllowed()))
                        {
                            transactionLogger.debug("partner splitpayment allowed---"+partnerDetailsVO.getSplitPaymentAllowed());
                            String splitPaymentDetail = directKitValidatorVO.getSplitPaymentVO().getSplitPaymentDetail();

                            String splitValue[] = splitPaymentDetail.split(",");
                            //HashMap terminalMap = new HashMap();
                            HashMap amountMap = new HashMap();
                            HashMap memberMap = new HashMap();

                            //TODO - Amount check
                            for (int i = 0; i < splitValue.length; i++)
                            {
                                String splitPayIdAndAmount[] = splitValue[i].split("=");
                                transactionLogger.debug("s----" + splitPayIdAndAmount[0] + "-" + splitPayIdAndAmount[1]);

                                //terminalMap.put(i, splitPayIdAndAmount[0]);
                                memberMap.put(i, splitPayIdAndAmount[0]);
                                amountMap.put(i, splitPayIdAndAmount[1]);
                                directKitValidatorVO.setTrackingid("");
                                transactionLogger.debug("member map----" + memberMap.get(0));

                                //Split Payment Based on Terminal Only
                                //directKitValidatorVO.setTerminalId(terminalMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());

                            /*if ("terminal".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getSplitPaymentType()))
                            {
                                directKitValidatorVO.setTerminalId(memberMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());
                            }
                            else if ("merchant".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getSplitPaymentType()))
                            {*/
                                String cardType = directKitValidatorVO.getCardType();
                                String paymodeType = directKitValidatorVO.getPaymentType();
                                transactionLogger.debug("memberid---" + memberMap.get(i) + "--" + cardType + "--" + paymodeType);
                                directKitValidatorVO.setTerminalId("0");
                                directKitValidatorVO.setPaymentType(paymodeType);
                                directKitValidatorVO.setCardType(cardType);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getMerchantDetailsVO().setMemberId(memberMap.get(i).toString());
                                // }
                                directKitValidatorVO = commonInputValidator.getMerchantDetailsForIFE(directKitValidatorVO);
                                if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                {
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                                }

                                directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                                if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                {
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), null);
                                }
                                else
                                {
                                    try
                                    {
                                        directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);
                                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                        {
                                            directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), splitValue[i]);
                                        }
                                        else
                                        {

                                            transactionLogger.debug("before gateway call-----");
                                            directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                                            directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                            transactionLogger.debug("response for IFE----" + directKitResponseVO.getStatus());
                                            transactionLogger.debug("response for IFE----" + directKitResponseVO.getStatusMsg());
                                            transactionLogger.debug("response for IFE----" + directKitResponseVO.getTrackingId());

                                            writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                                        }
                                    }
                                    catch (PZTechnicalViolationException tve)
                                    {
                                        logger.error("PZ Technical exception while placing transaction via WebService", tve);
                                        transactionLogger.error("PZ Technical exception while placing transaction via WebService", tve);

                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", splitValue[i]);
                                        PZExceptionHandler.handleTechicalCVEException(tve, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                                    }
                                    catch (PZConstraintViolationException cve)
                                    {
                                        logger.error("PZ Constraint exception while placing transaction via WebService", cve);
                                        transactionLogger.error("PZ Constraint exception while placing transaction via WebService", cve);

                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        //System.out.println("remark---" + cve.getPzConstraint().getErrorCodeListVO().getListOfError().size());
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage(), splitValue[i]);
                                        PZExceptionHandler.handleCVEException(cve, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                                    }
                                    catch (PZDBViolationException dbe)
                                    {
                                        logger.error("PZ DB exception while placing transaction via WebService", dbe);
                                        transactionLogger.error("PZ DB exception while placing transaction via WebService", dbe);

                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", splitValue[i]);
                                        PZExceptionHandler.handleDBCVEException(dbe, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                                    }
                                    catch (PZGenericConstraintViolationException gce)
                                    {
                                        logger.error("PZ GenericConstraint exception while placing transaction via WebService", gce);
                                        transactionLogger.error("PZ GenericConstraint exception while placing transaction via WebService", gce);

                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, gce.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, gce.getPzGenericConstraint().getMessage(), splitValue[i]);
                                        PZExceptionHandler.handleGenericCVEException(gce, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                                    }
                                    catch (Exception e)
                                    {
                                        logger.error("Exception while placing transaction via WebService", e);
                                        transactionLogger.error("GenericException while placing transaction via WebService", e);

                                        directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Internal error occur:::", splitValue[i]);
                                    }
                                    logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                                    logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                                    logger.debug("Final Split Status---" + directTransactionResponse.getStatus());
                                    logger.debug("Final Split Status---" + directTransactionResponse.getSplitTransactionId());

                                    directTransactionResponse.setAmount(sAmount);
                                    directTransactionResponse.setOrderId(sOrderId);
                                    //Based on the
                                    directTransactionResponse.setStatus(transactionWSUtils.getFinalStatus(directTransactionResponse.getStatus()));

                                    logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                                    logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                                    logger.debug("Final Split Status---" + directTransactionResponse.getStatus());
                                }
                            }
                        }
                        else
                        {
                            directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                            if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                            }

                            directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);

                            if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                            }
                            else
                            {
                                directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                                writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                            }
                        }
                    }




                    /*PartnerDetailsVO partnerDetailsVO = null;
                    PartnerManager partnerManager = new PartnerManager();

                    partnerDetailsVO = partnerManager.getPartnerDetailsForIFE(directKitValidatorVO.getParetnerId());
                    transactionLogger.debug("key for partner----"+partnerDetailsVO.getPartnerKey());
                    transactionLogger.debug("split payment for partner----"+partnerDetailsVO.getSplitPaymentAllowed());

                    directKitValidatorVO.getPartnerDetailsVO().setPartnerKey(partnerDetailsVO.getPartnerKey());
                    commonInputValidator.checksumVerificationForPartner(directKitValidatorVO);



                    /*directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                    if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                    }*/

                    //this call is for the manager for processing the transaction
                    //logger.debug("split payment---" + directKitValidatorVO.getPartnerDetailsVO().getSplitPaymentAllowed());

                    //directKitValidatorVO.getMerchantDetailsVO().setSplitPaymentAllowed("Y");
                       /* if(directKitValidatorVO.getSplitPaymentVO() != null && "Y".equalsIgnoreCase(partnerDetailsVO.getSplitPaymentAllowed()))
                    {
                         splitPaymentDetail = directKitValidatorVO.getSplitPaymentVO().getSplitPaymentDetail();

                        String splitValue[] = splitPaymentDetail.split(",");
                        //HashMap terminalMap = new HashMap();
                        HashMap amountMap = new HashMap();
                        HashMap memberMap = new HashMap();

                        //TODO - Amount check
                        for(int i=0;i<splitValue.length;i++)
                        {
                            String splitPayIdAndAmount[] = splitValue[i].split("=");
                            logger.debug("s----" + splitPayIdAndAmount[0] + "-" + splitPayIdAndAmount[1]);

                            //terminalMap.put(i, splitPayIdAndAmount[0]);
                            memberMap.put(i, splitPayIdAndAmount[0]);
                            amountMap.put(i, splitPayIdAndAmount[1]);
                            directKitValidatorVO.setTrackingid("");
                            transactionLogger.debug("member map----"+memberMap.get(0));

                            //Split Payment Based on Terminal Only
                            //directKitValidatorVO.setTerminalId(terminalMap.get(i).toString());
                            directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                            directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());

                            *//*if ("terminal".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getSplitPaymentType()))
                            {
                                directKitValidatorVO.setTerminalId(memberMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());
                            }
                            else if ("merchant".equalsIgnoreCase(directKitValidatorVO.getMerchantDetailsVO().getSplitPaymentType()))
                            {*//*
                                String cardType = directKitValidatorVO.getCardType();
                                String paymodeType = directKitValidatorVO.getPaymentType();
                                logger.debug("memberid---" + memberMap.get(i) + "--" + cardType + "--" + paymodeType);
                                directKitValidatorVO.setTerminalId("");
                                directKitValidatorVO.setPaymentType(paymodeType);
                                directKitValidatorVO.setCardType(cardType);
                                directKitValidatorVO.getTransDetailsVO().setAmount(amountMap.get(i).toString());
                                directKitValidatorVO.getTransDetailsVO().setOrderId(sOrderId + "_" + i);
                                directKitValidatorVO.getMerchantDetailsVO().setMemberId(memberMap.get(i).toString());
                           // }

                            directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                            if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                            }

                            //System Check for each split transaction

                            try
                            {
                                directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);
                                if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                {
                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                    writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(), splitValue[i]);
                                }
                                else
                                {

                                    directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                                    directKitResponseVO.setSplitTransactionId(splitValue[i]);

                                    writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                                }
                            }
                            catch (PZTechnicalViolationException tve)
                            {
                                logger.error("PZ Technical exception while placing transaction via WebService",tve);
                                transactionLogger.error("PZ Technical exception while placing transaction via WebService",tve);

                                directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information",splitValue[i]);
                                PZExceptionHandler.handleTechicalCVEException(tve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                            }
                            catch (PZConstraintViolationException cve)
                            {
                                logger.error("PZ Constraint exception while placing transaction via WebService",cve);
                                transactionLogger.error("PZ Constraint exception while placing transaction via WebService",cve);

                                directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage(),splitValue[i]);
                                PZExceptionHandler.handleCVEException(cve, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                            }
                            catch (PZDBViolationException dbe)
                            {
                                logger.error("PZ DB exception while placing transaction via WebService",dbe);
                                transactionLogger.error("PZ DB exception while placing transaction via WebService",dbe);

                                directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information",splitValue[i]);
                                PZExceptionHandler.handleDBCVEException(dbe, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

                            }
                            catch (PZGenericConstraintViolationException gce)
                            {
                                logger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);
                                transactionLogger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);

                                directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, gce.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, gce.getPzGenericConstraint().getMessage(),splitValue[i]);
                                PZExceptionHandler.handleGenericCVEException(gce, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
                            }
                            catch (Exception e)
                            {
                                logger.error("Exception while placing transaction via WebService",e);
                                transactionLogger.error("GenericException while placing transaction via WebService",e);

                                directKitResponseVO.setSplitTransactionId(splitValue[i]);
                                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal error occur:::", splitValue[i]);
                            }



                        /*logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                        logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                        logger.debug("Final Split Status---" + directTransactionResponse.getStatus());
                        logger.debug("Final Split Status---" + directTransactionResponse.getSplitTransactionId());

                        directTransactionResponse.setAmount(sAmount);
                        directTransactionResponse.setOrderId(sOrderId);
                        //Based on the
                        directTransactionResponse.setStatus(transactionWSUtils.getFinalStatus(directTransactionResponse.getStatus()));

                        logger.debug("Final Split Amount---" + directTransactionResponse.getAmount());
                        logger.debug("Final Split Order---" + directTransactionResponse.getOrderId());
                        logger.debug("Final Split Status---"+directTransactionResponse.getStatus());*/



                    //else if(directKitValidatorVO.getSplitPaymentVO()==null)
                   /* else
                    {
                        directKitValidatorVO = commonInputValidator.performRestTransactionValidation(directKitValidatorVO);
                        if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                        }

                        directKitValidatorVO = transactionHelper.performRESTSystemCheck(directKitValidatorVO);

                        if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg(),null);
                        }
                        else
                        {
                            directKitResponseVO = directTransactionManager.processDirectTransaction(directKitValidatorVO);
                            writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse, directKitResponseVO);
                        }
                    }*/
                }
            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while placing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService",tve);

                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", null);
                PZExceptionHandler.handleTechicalCVEException(tve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while placing transaction via WebService",cve);
                transactionLogger.error("PZ Constraint exception while placing transaction via WebService",cve);

                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage(), null);
                PZExceptionHandler.handleCVEException(cve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while placing transaction via WebService",dbe);
                transactionLogger.error("PZ DB exception while placing transaction via WebService",dbe);

                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information", null);
                PZExceptionHandler.handleDBCVEException(dbe, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

            }
            catch (PZGenericConstraintViolationException gce)
            {
                logger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);
                transactionLogger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);

                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, gce.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, gce.getPzGenericConstraint().getMessage(), null);
                PZExceptionHandler.handleGenericCVEException(gce, directKitValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            /*catch (Exception e)
            {
                logger.error("Exception while placing transaction via WebService",e);
                transactionLogger.error("Exception while placing transaction via WebService",e);

                writeDirectTransactionResponse.setClientTransactionResponseforError(directTransactionResponse, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal error occur:::", null);
            }*/
        }
        return directTransactionResponse;
    }

    @Override
    public DirectRefundResponse processRefund(DirectRefundRequest directRefundRequest)
    {
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //ResponseVO
        DirectRefundResponse directRefundResponse = new DirectRefundResponse();
        //Validator Utils instance
        DirectRefundValidatorVO directRefundValidatorVO = null;
        //Response VO
        DirectKitResponseVO directKitResponseVO = null;
        Functions functions = new Functions();

        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();

        directRefundValidatorVO=readDirectTransactionRequest.readRequestParameterForRefundTransaction(directRefundRequest);

        if(directRefundValidatorVO==null || (!functions.isValueNull(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(directRefundValidatorVO.getParetnerId())))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while refunding transaction"));
            writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while refunding transaction");
        }
        else
        {
            try
            {
                String reqIp = "";
                String ipAddress = Functions.getIpAddress(request1);

                if(ipAddress.contains(","))
                {
                    String sIp[] = ipAddress.split(",");
                    reqIp = sIp[0].trim();
                }
                else
                {
                    reqIp = ipAddress;
                }
                directRefundValidatorVO.setMerchantIpAddress(reqIp);
                directRefundValidatorVO.setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                //Set errorCodeListVo within commonValidator VO
                directRefundValidatorVO.setErrorCodeListVO(errorCodeListVO);
                logger.debug("memberid---"+directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
                logger.debug("partnerid---"+directRefundValidatorVO.getParetnerId());
                //merchantid=not present in request
                //partnerid=present in request
                if (!functions.isValueNull(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()) && functions.isValueNull(directRefundValidatorVO.getParetnerId()))
                {

                    PartnerDetailsVO partnerDetailsVO = null;
                    PartnerManager partnerManager = new PartnerManager();

                    partnerDetailsVO = partnerManager.getPartnerDetailsForIFE(directRefundValidatorVO.getParetnerId());
                    directRefundValidatorVO.setFlightMode(partnerDetailsVO.getFlightMode());

                    commonInputValidator.performRefundStep1Validation(directRefundValidatorVO);
                    if (!directRefundValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directRefundValidatorVO.getErrorMessage());
                    }
                    else
                    {
                        commonInputValidator.checksumVerificationForRefundREST(directRefundValidatorVO,partnerDetailsVO.getPartnerKey());
                        directKitResponseVO = directTransactionManager.processRefund(directRefundValidatorVO);
                        writeDirectTransactionResponse.convertRefundSystemResponseToClientsResponse(directRefundResponse, directKitResponseVO);
                    }
                }
                else
                {
                    //Merchant related checks and checksum
                    commonInputValidator.performDirectKitRefundValidation(directRefundValidatorVO);
                    if (!directRefundValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directRefundValidatorVO.getErrorMessage());
                    }
                    else
                    {
                        directKitResponseVO = directTransactionManager.processRefund(directRefundValidatorVO);
                        writeDirectTransactionResponse.convertRefundSystemResponseToClientsResponse(directRefundResponse, directKitResponseVO);
                    }
                }
            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while placing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService",tve);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleTechicalCVEException(tve,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while refunding transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while refunding transaction via RESTful",cve);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while refunding transaction via RESTful",dbe);
                transactionLogger.error("PZ DB exception while refunding transaction via RESTful",dbe);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (Exception e)
            {
                logger.error("Exception while placing transaction via WebService",e);
                transactionLogger.error("Exception while placing transaction via WebService",e);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO, null, PZResponseStatus.ERROR, "Internal error occur:::");
            }
        }
        return directRefundResponse;
    }

    @Override
    public DirectCaptureResponse processCapture(DirectCaptureRequest directCaptureRequest)
    {
        //CommonValidator vo for validation purpose
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectCaptureResponse directCaptureResponse = new DirectCaptureResponse();

        DirectCaptureValidatorVO directCaptureValidatorVO = null;
        DirectKitResponseVO directKitResponseVO = null;
        Functions functions = new Functions();

        directCaptureValidatorVO=readDirectTransactionRequest.readRequestParameterForCaptureTransaction(directCaptureRequest);

        if(directCaptureValidatorVO==null || (!functions.isValueNull(directCaptureValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(directCaptureValidatorVO.getParetnerId())))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while capturing transaction"));
            writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while capturing transaction");
        }
        else
        {
            try
            {
                String reqIp = "";
                String ipAddress = Functions.getIpAddress(request1);

                if(ipAddress.contains(","))
                {
                    String sIp[] = ipAddress.split(",");
                    reqIp = sIp[0].trim();
                }
                else
                {
                    reqIp = ipAddress;
                }
                directCaptureValidatorVO.setMerchantIpAddress(reqIp);
                directCaptureValidatorVO.setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());

                directCaptureValidatorVO.setErrorCodeListVO(errorCodeListVO);
                //merchantid=not present in request
                //partnerid=present in request
                logger.debug("memberid---"+directCaptureValidatorVO.getMerchantDetailsVO().getMemberId());
                logger.debug("partnerid---"+directCaptureValidatorVO.getParetnerId());
                if (!functions.isValueNull(directCaptureValidatorVO.getMerchantDetailsVO().getMemberId()) && functions.isValueNull(directCaptureValidatorVO.getParetnerId()))
                {

                    PartnerDetailsVO partnerDetailsVO = null;
                    PartnerManager partnerManager = new PartnerManager();

                    partnerDetailsVO = partnerManager.getPartnerDetailsForIFE(directCaptureValidatorVO.getParetnerId());
                    directCaptureValidatorVO.setFlightMode(partnerDetailsVO.getFlightMode());

                    commonInputValidator.performCaptureStep1Validation(directCaptureValidatorVO);
                    if (!directCaptureValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directCaptureValidatorVO.getErrorMessage());
                    }
                    else
                    {
                        commonInputValidator.checksumVerificationForCaptureREST(directCaptureValidatorVO, partnerDetailsVO.getPartnerKey());
                        directKitResponseVO=directTransactionManager.processCapture(directCaptureValidatorVO);
                        writeDirectTransactionResponse.convertCaptureSystemResponseToClientsResponse(directCaptureResponse, directKitResponseVO);
                    }
                }
                else
                {
                    commonInputValidator.performDirectKitCaptureValidation(directCaptureValidatorVO);
                    if (!directCaptureValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directCaptureValidatorVO.getErrorMessage());
                    }
                    else
                    {
                        directKitResponseVO = directTransactionManager.processCapture(directCaptureValidatorVO);
                        writeDirectTransactionResponse.convertCaptureSystemResponseToClientsResponse(directCaptureResponse, directKitResponseVO);
                    }
                }
            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while capturing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while capturing transaction via WebService",tve);

                writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleTechicalCVEException(tve,directCaptureValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while capturing transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while capturing transaction via RESTful",cve);

                writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve, directCaptureValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while capturing transaction via RESTful",dbe);
                transactionLogger.error("PZ DB exception while capturing transaction via RESTful",dbe);

                writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe, directCaptureValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (Exception e)
            {
                logger.error("Exception while capturing transaction via RESTful",e);
                transactionLogger.error("Exception while capturing transaction via RESTful",e);

                writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, directCaptureValidatorVO, null, PZResponseStatus.ERROR, "Internal error occur:::");
            }
        }
        return directCaptureResponse;
    }

    @Override
    public DirectCancelResponse processCancel(DirectCancelRequest directCancelRequest)
    {
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectCancelResponse directCancelResponse = new DirectCancelResponse();
        Functions functions = new Functions();

        DirectKitResponseVO directKitResponseVO = null;
        CommonValidatorVO commonValidatorVO=readDirectTransactionRequest.readRequestParameterForCancelTransaction(directCancelRequest);

        if(commonValidatorVO==null || (!functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(commonValidatorVO.getParetnerId())))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide all Request Attribute while cancelling transaction"));
            writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide all Request Attribute while cancelling transaction");
        }
        else
        {
            try
            {
                //commonValidatorVO.getAddressDetailsVO().setIp(Functions.getIpAddress(request1));
                commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                //merchantid=not present in request
                //partnerid=present in request
                logger.debug("memberid---"+commonValidatorVO.getMerchantDetailsVO().getMemberId());
                logger.debug("partnerid---"+commonValidatorVO.getParetnerId());

                if (!functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()) && functions.isValueNull(commonValidatorVO.getParetnerId()))
                {

                    PartnerDetailsVO partnerDetailsVO = null;
                    PartnerManager partnerManager = new PartnerManager();

                    partnerDetailsVO = partnerManager.getPartnerDetailsForIFE(commonValidatorVO.getParetnerId());
                    commonValidatorVO.setFlightMode(partnerDetailsVO.getFlightMode());


                    commonInputValidator.performCancelStep1Validation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    }
                    else
                    {
                        commonInputValidator.checksumVerificationForCancelREST(commonValidatorVO, partnerDetailsVO.getPartnerKey());
                        directKitResponseVO = directTransactionManager.processCancel(commonValidatorVO);
                        writeDirectTransactionResponse.convertCancelSystemResponseToClientsResponse(directCancelResponse, directKitResponseVO);
                    }
                }
                else
                {
                    //if Member id is not null
                    commonInputValidator.performDirectKitCancelValidation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    }
                    else
                    {
                        directKitResponseVO = directTransactionManager.processCancel(commonValidatorVO);
                        writeDirectTransactionResponse.convertCancelSystemResponseToClientsResponse(directCancelResponse, directKitResponseVO);
                    }

                }

            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while capturing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while capturing transaction via WebService", tve);

                writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleTechicalCVEException(tve,commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while capturing transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while capturing transaction via RESTful", cve);

                writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while capturing transaction via RESTful",dbe);
                transactionLogger.error("PZ DB exception while capturing transaction via RESTful", dbe);

                writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe, commonValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (Exception e)
            {
                logger.error("Exception while capturing transaction via RESTful",e);
                transactionLogger.error("Exception while capturing transaction via RESTful", e);

                writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occur:::");
            }
        }
        return directCancelResponse;
    }

    @Override
    public DirectInquiryResponse processStatus(DirectInquiryRequest directStatusRequest)
    {
        //CommonValidator vo for validation purpose
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectInquiryResponse directInquiryResponse = new DirectInquiryResponse();
        DirectKitResponseVO directKitResponseVO=null;

        DirectInquiryValidatorVO directInquiryValidatorVO=readDirectTransactionRequest.readRequestParameterForInquiryTransaction(directStatusRequest);

        if(directInquiryValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while cancelling transaction"));
            writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
        }
        else
        {
            try
            {
                String reqIp = "";
                String ipAddress = Functions.getIpAddress(request1);

                if(ipAddress.contains(","))
                {
                    String sIp[] = ipAddress.split(",");
                    reqIp = sIp[0].trim();
                }
                else
                {
                    reqIp = ipAddress;
                }
                directInquiryValidatorVO.setMerchantIpAddress(reqIp);
                directInquiryValidatorVO.setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());

                directInquiryValidatorVO.setErrorCodeListVO(errorCodeListVO);

                commonInputValidator.performDirectKitInquiryValidation(directInquiryValidatorVO);
                if(!directInquiryValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directInquiryValidatorVO.getErrorMessage());
                }
                else
                {
                    directKitResponseVO=directTransactionManager.processInquiry(directInquiryValidatorVO);
                    writeDirectTransactionResponse.convertInquirySystemResponseToClientsResponse(directInquiryResponse, directKitResponseVO);
                }
            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while placing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService",tve);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleTechicalCVEException(tve,directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while inquiring transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while inquiring transaction via RESTful",cve);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve, directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while inquiring transaction via RESTful", dbe);
                transactionLogger.error("PZ DB exception while inquiring transaction via RESTful", dbe);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe, directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (Exception e)
            {
                logger.error("Exception while inquiring transaction via RESTful", e);
                transactionLogger.error("Exception while inquiring transaction via RESTful", e);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occur:::");
            }
        }

        return directInquiryResponse;
    }
}