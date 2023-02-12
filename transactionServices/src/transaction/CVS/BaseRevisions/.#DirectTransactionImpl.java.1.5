package transaction;

import com.directi.pg.DefaultUser;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.DirectTransactionManager;
import com.manager.vo.DirectKitResponseVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.transaction.vo.*;
import com.transaction.utils.ReadDirectTransactionRequest;
import com.transaction.utils.WriteDirectTransactionResponse;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;



import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/28/15
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
/*@WebService(endpointInterface = "transaction.DirectTransactionService")*/
public class DirectTransactionImpl implements DirectTransactionService
{
    private DirectTransactionManager directTransactionManager = new DirectTransactionManager();
    private ReadDirectTransactionRequest readDirectTransactionRequest = new ReadDirectTransactionRequest();
    private WriteDirectTransactionResponse writeDirectTransactionResponse = new WriteDirectTransactionResponse();
    private Logger logger = new Logger(DirectTransactionImpl.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(DirectTransactionImpl.class.getName());
    /*@Resource*/
    WebServiceContext wsContext;

    public DirectTransactionResponse processTransaction(/*@WebParam(name = "request")*/ DirectTransactionRequest directTransactionRequest)
    {
        Date date = new Date();
        transactionLogger.debug("processTransaction start ############" + date.getTime());
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);

        //ResponseVo of the system
        DirectKitResponseVO directKitResponseVO =null;
        //Response Vo of the client
        DirectTransactionResponse directTransactionResponse = new DirectTransactionResponse();
        List<DirectTransactionErrorCode> directTransactionErrorCodeVOs=null;
        //errorCode VO  of system and client
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectTransactionErrorCode directTransactionErrorCodeVO=null;

        CommonInputValidator commonInputValidator=new CommonInputValidator();
        Date date0 = new Date();
        transactionLogger.debug("processTransaction.readRequestParameterForWebServiceDirectKit start ############"+date0.getTime());
        CommonValidatorVO directKitValidatorVO=readDirectTransactionRequest.readRequestParameterForWebServiceDirectKit(directTransactionRequest);
        transactionLogger.debug("processTransaction.readRequestParameterForWebServiceDirectKit end ############" + (new Date().getTime()));
        transactionLogger.debug("processTransaction.readRequestParameterForWebServiceDirectKit diff ############" + (new Date().getTime()-date0.getTime()));
        if(directKitValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while placing transaction"));
            writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,null,errorCodeListVO,PZResponseStatus.ERROR,"Please provide Request Attribute while placing transaction");
        }
        else
        {
            //this is additional mapping for commonValidator to be set from request
            String IpAddress=request.getRemoteAddr();
            directKitValidatorVO.getAddressDetailsVO().setIp(IpAddress);
            directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request) + ":" + request.getServerPort() + ",X-Forwarded=" + request.getServletPath()); //todo not trust worthy
            directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);

            User user = new DefaultUser(directKitValidatorVO.getMerchantDetailsVO().getMemberId());
            ESAPI.authenticator().setCurrentUser(user);

            String error=null;
            try
            {
                /*error = readDirectTransactionRequest.checkValueNumeric(directKitValidatorVO.getPaymentType(),directKitValidatorVO.getCardType(),directKitValidatorVO.getTerminalId(),errorCodeListVO);
                if(!errorCodeListVO.getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,directKitValidatorVO,errorCodeListVO,PZResponseStatus.ERROR,error);
                }
                else
                {*/
                Date date1 = new Date();
                transactionLogger.debug("processTransaction.performDirectKitValidation start ############" + date1.getTime());
                directKitValidatorVO = commonInputValidator.performDirectKitValidation(directKitValidatorVO);
                transactionLogger.debug("processTransaction.performDirectKitValidation end ############" + new Date().getTime());
                transactionLogger.debug("processTransaction.performDirectKitValidation diff ############" + (new Date().getTime()-date1.getTime()));
                if(!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,directKitValidatorVO,errorCodeListVO,PZResponseStatus.ERROR,directKitValidatorVO.getErrorMsg());
                }
                else
                {
                    //this call is for the manager for processing the transaction
                    Date date2 = new Date();
                    transactionLogger.debug("DirectTransactionImpl.processDirectTransaction starts##########"+date2.getTime());
                    directKitResponseVO=directTransactionManager.processDirectTransaction(directKitValidatorVO);
                    transactionLogger.debug("DirectTransactionImpl.processDirectTransaction end##########"+new Date().getTime());
                    transactionLogger.debug("DirectTransactionImpl.processDirectTransaction diff##########"+(new Date().getTime()-date2.getTime()));

                    Date date3 = new Date();
                    transactionLogger.debug("DirectTransactionImpl.convertSystemResponseToClientsResponse starts##########"+date3.getTime());
                    writeDirectTransactionResponse.convertSystemResponseToClientsResponse(directTransactionResponse,directKitResponseVO);
                    transactionLogger.debug("DirectTransactionImpl.convertSystemResponseToClientsResponse end##########"+new Date().getTime());
                    transactionLogger.debug("DirectTransactionImpl.convertSystemResponseToClientsResponse diff##########"+(new Date().getTime()-date3.getTime()));
                }
               // }

            }

            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while placing transaction via WebService",cve);
                transactionLogger.error("PZ Constraint exception while placing transaction via WebService",cve);

                writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,directKitValidatorVO,cve.getPzConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ DB exception while placing transaction via WebService",dbe);
                transactionLogger.error("PZ DB exception while placing transaction via WebService",dbe);

                writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,directKitValidatorVO,dbe.getPzdbConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,"Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleDBCVEException(dbe,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);

            }
            catch (PZGenericConstraintViolationException gce)
            {
                logger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);
                transactionLogger.error("PZ GenericConstraint exception while placing transaction via WebService",gce);

                writeDirectTransactionResponse.setClientTransactionResponse(directTransactionResponse,directKitValidatorVO,gce.getPzGenericConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,gce.getPzGenericConstraint().getMessage());
                PZExceptionHandler.handleGenericCVEException(gce,directKitValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }

        }
        transactionLogger.debug("DirectTransactionImpl.processTransaction end ###########"+new Date().getTime());
        transactionLogger.debug("DirectTransactionImpl.processTransaction diff ###########"+(new Date().getTime()-date.getTime()));
        return directTransactionResponse;
    }

    public DirectRefundResponse processRefund(/*@WebParam(name = "refundRequest")*/ DirectRefundRequest directRefundRequest)
    {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);

        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectRefundResponse directRefundResponse = new DirectRefundResponse();
        //Validator Utils instance
        DirectRefundValidatorVO directRefundValidatorVO = null;
        //Response VO
        DirectKitResponseVO directKitResponseVO = null;

        directRefundValidatorVO=readDirectTransactionRequest.readRequestParameterForRefundTransaction(directRefundRequest);

        if(directRefundValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while refunding transaction"));
            writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse,directRefundValidatorVO,errorCodeListVO,PZResponseStatus.ERROR,"Please provide Request Attribute while refunding transaction");
        }
        else
        {
            directRefundValidatorVO.setMerchantIpAddress(Functions.getIpAddress(request));
            directRefundValidatorVO.setHeader("Client =" + Functions.getIpAddress(request) + ":" + request.getServerPort() + ",X-Forwarded=" + request.getServletPath());
            //Set errorCodeListVo within commonValidator VO
            directRefundValidatorVO.setErrorCodeListVO(errorCodeListVO);
            try
            {
                //commonInputValidator.performDirectKitRefundValidation(directRefundValidatorVO);
                if (!directRefundValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse, directRefundValidatorVO,errorCodeListVO,PZResponseStatus.ERROR,directRefundValidatorVO.getErrorMessage());
                }
                else
                {
                    directKitResponseVO=directTransactionManager.processRefund(directRefundValidatorVO);
                    writeDirectTransactionResponse.convertRefundSystemResponseToClientsResponse(directRefundResponse,directKitResponseVO);
                }
            }
            catch (PZTechnicalViolationException tve)
            {
                logger.error("PZ Technical exception while placing transaction via WebService",tve);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService",tve);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse,directRefundValidatorVO,tve.getPzTechnicalConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,"Internal Error occurred,Please contact support for more information");
                PZExceptionHandler.handleTechicalCVEException(tve,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);

            }
            catch (PZConstraintViolationException cve)
            {
                logger.error("PZ Constraint exception while refunding transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while refunding transaction via RESTful",cve);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse,directRefundValidatorVO,cve.getPzConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ Constraint exception while refunding transaction via RESTful",dbe);
                transactionLogger.error("PZ Constraint exception while refunding transaction via RESTful",dbe);

                writeDirectTransactionResponse.setClientRefundResponse(directRefundResponse,directRefundValidatorVO,dbe.getPzdbConstraint().getErrorCodeListVO(),PZResponseStatus.ERROR,dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe,directRefundValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }

        }
        return directRefundResponse;

    }

    public DirectCaptureResponse processCapture(/*@WebParam(name = "captureRequest")*/ DirectCaptureRequest directCaptureRequest)
    {
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectCaptureResponse directCaptureResponse = new DirectCaptureResponse();

        CommonValidatorVO commonValidatorVO=readDirectTransactionRequest.readRequestParameterForCaptureTransaction(directCaptureRequest);

        if(commonValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while capturing transaction"));
            writeDirectTransactionResponse.setClientCaptureResponse(directCaptureResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while capturing transaction");
        }
        else
        {
            //directTransactionManager.processCapture(commonValidatorVO);
        }

        return directCaptureResponse;
    }

    public DirectCancelResponse processCancel(/*@WebParam(name = "cancelRequest")*/ DirectCancelRequest directCancelRequest)
    {
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectCancelResponse directCancelResponse = new DirectCancelResponse();

        CommonValidatorVO commonValidatorVO=readDirectTransactionRequest.readRequestParameterForCancelTransaction(directCancelRequest);

        if(commonValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while cancelling transaction"));
            writeDirectTransactionResponse.setClientCancelResponse(directCancelResponse,null,errorCodeListVO,PZResponseStatus.ERROR,"Please provide Request Attribute while cancelling transaction");
        }
        else
        {
            //directTransactionManager.processCancel(commonValidatorVO);
        }
        return directCancelResponse;
    }

    public DirectInquiryResponse processInquiry(DirectInquiryRequest directInquiryRequest)
    {
        MessageContext mc = wsContext.getMessageContext();
        HttpServletRequest request = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);

        //CommonValidator vo for validation purpose
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        //Error related VO
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //ResponseVO
        DirectInquiryResponse directInquiryResponse = new DirectInquiryResponse();
        DirectKitResponseVO directKitResponseVO=null;
        //List<DirectKitResponseVO> listDirectKitResponseVO = null;

        DirectInquiryValidatorVO directInquiryValidatorVO=readDirectTransactionRequest.readRequestParameterForInquiryTransaction(directInquiryRequest);

        if(directInquiryValidatorVO==null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL,"Please provide Request Attribute while cancelling transaction"));
            writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
        }
        else
        {
            try
            {
                directInquiryValidatorVO.setMerchantIpAddress(Functions.getIpAddress(request));
                directInquiryValidatorVO.setHeader("Client =" + Functions.getIpAddress(request) + ":" + request.getServerPort() + ",X-Forwarded=" + request.getServletPath());

                directInquiryValidatorVO.setErrorCodeListVO(errorCodeListVO);

                commonInputValidator.performDirectKitInquiryValidation(directInquiryValidatorVO);
                if(!directInquiryValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directInquiryValidatorVO.getErrorMessage());
                }
                else
                {
                    directKitResponseVO=directTransactionManager.processInquiry(directInquiryValidatorVO);
                    //listDirectKitResponseVO=directTransactionManager.processInquiryWS(directInquiryValidatorVO);
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
                logger.error("PZ Constraint exception while refunding transaction via RESTful",cve);
                transactionLogger.error("PZ Constraint exception while refunding transaction via RESTful",cve);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, cve.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, cve.getPzConstraint().getMessage());
                PZExceptionHandler.handleCVEException(cve,directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(),PZOperations.DIRECT_KIT_WEBSERVICE);
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZ Constraint exception while refunding transaction via RESTful",dbe);
                transactionLogger.error("PZ Constraint exception while refunding transaction via RESTful",dbe);

                writeDirectTransactionResponse.setClientInquiryResponse(directInquiryResponse, directInquiryValidatorVO, dbe.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, dbe.getPzdbConstraint().getMessage());
                PZExceptionHandler.handleDBCVEException(dbe, directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), PZOperations.DIRECT_KIT_WEBSERVICE);
            }

        }

        return directInquiryResponse;
    }

}
