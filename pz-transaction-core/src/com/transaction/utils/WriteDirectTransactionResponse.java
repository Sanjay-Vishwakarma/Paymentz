package com.transaction.utils;

import com.directi.pg.AsyncNotificationService;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.core.GatewayAccountService;
import com.google.gson.Gson;
import com.manager.TransactionManager;
import com.manager.vo.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.response.PZShortResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.transaction.vo.*;
import com.transaction.vo.restVO.RequestVO.MarketPlace;
import com.transaction.vo.restVO.RequestVO.Parameters;
import com.transaction.vo.restVO.ResponseVO.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/30/15
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteDirectTransactionResponse
{
    private ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
    private Functions functions             = new Functions();
    private Logger logger                   = new Logger(WriteDirectTransactionResponse.class.getName());

    public List<DirectTransactionErrorCode> convertSystemErrorListVoToClientsListOfErrorCodeVO(ErrorCodeListVO errorCodeListVO, String flightMode)
    {
        List<DirectTransactionErrorCode> directTransactionErrorCodeVOs = new ArrayList<DirectTransactionErrorCode>();
        for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
        {
            DirectTransactionErrorCode directTransactionErrorCode = new DirectTransactionErrorCode();
            directTransactionErrorCode.setErrorCode(errorCodeVO.getErrorCode());
            if ("N".equals(flightMode))
            {
                directTransactionErrorCode.setErrorDescription(errorCodeVO.getErrorDescription());
                directTransactionErrorCode.setErrorReason(errorCodeVO.getErrorReason());
            }
            directTransactionErrorCodeVOs.add(directTransactionErrorCode);
        }
        return directTransactionErrorCodeVOs;
    }

    public DirectTransactionErrorCode formClientsErrorCodeVO(ErrorName errorName, String reason)
    {
        DirectTransactionErrorCode directTransactionErrorCodeVO = new DirectTransactionErrorCode();
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCode(errorName);

        directTransactionErrorCodeVO.setErrorCode(errorCodeVO.getErrorCode());
        directTransactionErrorCodeVO.setErrorDescription(errorCodeVO.getErrorDescription());
        directTransactionErrorCodeVO.setErrorReason(reason);

        return directTransactionErrorCodeVO;
    }

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName, String reason)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        if (errorCodeVO != null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        return errorCodeVO;
    }

    public void setSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO, ErrorCodeListVO errorCodeListVO, ErrorName errorName, CommonValidatorVO commonValidatorVO, PZResponseStatus status, String remark, String billingDescriptor)
    {
        if (errorCodeListVO != null)
        {
            if (errorName != null)
            {
                ErrorCodeVO errorCodeVO = null;
                if (errorCodeUtils.getSystemErrorCode(errorName) != null)
                {
                    errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
                }
                else if (errorCodeUtils.getErrorCode(errorName) != null)
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(errorName);
                }
                errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if (commonValidatorVO != null)
        {
            if (directKitResponseVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                if (functions.isValueNull(commonValidatorVO.getToken()))
                    directKitResponseVO.setToken(commonValidatorVO.getToken());

                if (commonValidatorVO.getTransDetailsVO() != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                        directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                        directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                }
                if (errorCodeListVO != null)
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if (status != null)
            directKitResponseVO.setStatus(status.name());
        if (functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);
        if (functions.isValueNull(billingDescriptor))
            directKitResponseVO.setBillingDescriptor(billingDescriptor);
    }

    public void setClientTransactionResponseforError(DirectTransactionResponse directTransactionResponse, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark, String splitValue)
    {
        try
        {
            String flightMode = "N";
            if (directTransactionResponse != null)
            {
                if (commonValidatorVO != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                    {
                        String trackingIds = directTransactionResponse.getTrackingId();
                        if (trackingIds == null || trackingIds.equals(""))
                        {
                            directTransactionResponse.setTrackingId(commonValidatorVO.getTrackingid());
                        }
                        else
                        {
                            directTransactionResponse.setTrackingId(trackingIds + "," + commonValidatorVO.getTrackingid());
                        }
                    }
                    if (commonValidatorVO.getTransDetailsVO() != null)
                    {
                        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                            directTransactionResponse.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
                        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                            directTransactionResponse.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                    }
                    if (commonValidatorVO.getMerchantDetailsVO() != null)
                    {
                        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getFlightMode()))
                            flightMode = commonValidatorVO.getMerchantDetailsVO().getFlightMode();
                    }
                }

                //logger.debug("FlightMode::::::" + commonValidatorVO.getMerchantDetailsVO().getFlightMode());

                if (status != null)
                {
                    String tempStatus = directTransactionResponse.getStatus();
                    if (tempStatus == null || tempStatus.equals(""))
                    {
                        tempStatus = PZResponseStatus.SUCCESS.name().equals(status.name()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(status.name()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString());
                        directTransactionResponse.setStatus(tempStatus);
                    }
                    else
                    {
                        tempStatus = tempStatus + "," + (PZResponseStatus.SUCCESS.name().equals(status.name()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(status.name()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString()));
                        directTransactionResponse.setStatus(tempStatus);
                    }
                }

                if (PZResponseStatus.ERROR.name().equals(status.name()) && "Y".equals(flightMode))
                {
                    StringBuffer pipedErrorCode = new StringBuffer();
                    if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
                    {
                        for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                        {
                            if (pipedErrorCode.length() > 0)
                            {
                                pipedErrorCode.append("|");
                            }
                            pipedErrorCode.append(errorCodeVO.getErrorCode());
                        }
                        String statusCodes = directTransactionResponse.getStatusDescription();
                        if (statusCodes == null || statusCodes.equals(""))
                        {
                            directTransactionResponse.setStatusDescription(pipedErrorCode.toString());
                        }
                        else
                        {
                            directTransactionResponse.setStatusDescription(statusCodes + "," + pipedErrorCode.toString());
                        }
                    }

                }
                else
                {
                    if (functions.isValueNull(remark))
                    {
                        String statusMsg = directTransactionResponse.getStatusDescription();
                        //System.out.println("error code list----"+errorCodeListVO.getListOfError().size());
                        //System.out.println("error code list111----"+errorCodeListVO.getListOfError());
                        if (!functions.isValueNull(statusMsg))
                        {
                            directTransactionResponse.setStatusDescription(errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + remark);
                        }
                        else
                        {
                            directTransactionResponse.setStatusDescription(statusMsg + "," + errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + remark);
                        }
                    }
                }
                if (!"Y".equals(flightMode) && errorCodeListVO != null)
                {
                    List<DirectTransactionErrorCode> directTransactionErrorCodeVOs = directTransactionResponse.getDirectTransactionErrorCodeVOs();
                    if (directTransactionErrorCodeVOs == null || directTransactionErrorCodeVOs.isEmpty())
                    {
                        directTransactionResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
                    }
                    else
                    {
                        directTransactionErrorCodeVOs.addAll(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
                    }
                }

                if (functions.isValueNull(splitValue) && (status != null && !status.equals("")))
                {
                    String splitTransactionIds = directTransactionResponse.getSplitTransactionId();

                    String tempStatus = PZResponseStatus.SUCCESS.name().equals(status.name()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(status.name()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString());

                    if (!tempStatus.equals(PZShortResponseStatus.SUCCESS.toString()))
                    {
                        if (splitTransactionIds == null || splitTransactionIds.equals(""))
                            directTransactionResponse.setSplitTransactionId(splitValue);
                        else
                            directTransactionResponse.setSplitTransactionId(splitTransactionIds + "," + splitValue);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("NullPointerException while converting the response code", e);
        }
    }

    public void convertSystemResponseToClientsResponse(DirectTransactionResponse directTransactionResponse, DirectKitResponseVO directKitResponseVO)
    {
        if (directKitResponseVO != null)
        {
            if (functions.isValueNull(directKitResponseVO.getTrackingId()))
            {
                //String trackingIds = null;
                String trackingIds = directTransactionResponse.getTrackingId();
                if (trackingIds == null || trackingIds.equals(""))
                {
                    directTransactionResponse.setTrackingId(directKitResponseVO.getTrackingId());
                }
                else
                {
                    //trackingIds = directKitResponseVO.getTrackingId();
                    directTransactionResponse.setTrackingId(trackingIds + "," + directKitResponseVO.getTrackingId());
                }

            }
            if (functions.isValueNull(directKitResponseVO.getDescription()))
            {
                String orderIds = directTransactionResponse.getOrderId();
                if (orderIds == null || orderIds.equals(""))
                {
                    directTransactionResponse.setOrderId(directKitResponseVO.getDescription());
                }
                else
                {
                    directTransactionResponse.setOrderId(orderIds + "," + directKitResponseVO.getDescription());
                }
            }

            if (functions.isValueNull(directKitResponseVO.getAmount()))
            {
                String amounts = directTransactionResponse.getAmount();
                if (amounts == null || amounts.equals(""))
                {
                    directTransactionResponse.setAmount(directKitResponseVO.getAmount());
                }
                else
                {
                    directTransactionResponse.setAmount(amounts + "," + directKitResponseVO.getAmount());
                }
            }
            if (functions.isValueNull(directKitResponseVO.getStatus()))
            {
                String status = directTransactionResponse.getStatus();

                if (status == null || status.equals(""))
                {
                    status = PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString());
                    directTransactionResponse.setStatus(status);
                }
                else
                {
                    status = status + "," + (PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString()));
                    directTransactionResponse.setStatus(status);
                }

            }
            if (functions.isValueNull(directKitResponseVO.getStatusMsg()))
            {
                String statusMsg = directTransactionResponse.getStatusDescription();
                if (statusMsg == null || statusMsg.equals(""))
                {
                    directTransactionResponse.setStatusDescription(directKitResponseVO.getStatusMsg());
                }
                else
                {
                    directTransactionResponse.setStatusDescription(statusMsg + "," + directKitResponseVO.getStatusMsg());
                }
            }
            if (functions.isValueNull(directKitResponseVO.getBillingDescriptor()))
            {
                String billingDescriptor = directTransactionResponse.getBillingDescriptor();

                if (billingDescriptor == null || billingDescriptor.equals(""))
                {
                    directTransactionResponse.setBillingDescriptor(directKitResponseVO.getBillingDescriptor());
                }
                else
                {
                    directTransactionResponse.setBillingDescriptor(billingDescriptor + "," + directKitResponseVO.getBillingDescriptor());
                }
            }


            if (directKitResponseVO.getErrorCodeListVO() != null && !directKitResponseVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                String status = PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString());

                if ((PZShortResponseStatus.ERROR.toString()).equals(status) && "Y".equals(directKitResponseVO.getFlightMode()))
                {
                    StringBuffer pipedErrorCode = new StringBuffer();

                    for (ErrorCodeVO errorCodeVO : directKitResponseVO.getErrorCodeListVO().getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    String statusCodes = directTransactionResponse.getStatusDescription();
                    if (statusCodes == null || statusCodes.equals(""))
                    {
                        directTransactionResponse.setStatusDescription(pipedErrorCode.toString());
                    }
                    else
                    {
                        directTransactionResponse.setStatusDescription(statusCodes + "," + pipedErrorCode.toString());
                    }
                }
                else
                {
                    List<DirectTransactionErrorCode> directTransactionErrorCodeVOs = directTransactionResponse.getDirectTransactionErrorCodeVOs();
                    if (directTransactionErrorCodeVOs == null || directTransactionErrorCodeVOs.isEmpty())
                    {
                        directTransactionResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));
                    }
                    else
                    {
                        directTransactionErrorCodeVOs.addAll(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));
                    }

                }
            }

            if (functions.isValueNull(directKitResponseVO.getSplitTransactionId()) && (directKitResponseVO.getStatus() != null && !directKitResponseVO.getStatus().equals("")))
            {
                String splitTransactionIds = directTransactionResponse.getSplitTransactionId();

                String status = PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString());

                if (!status.equals(PZShortResponseStatus.SUCCESS.toString()))
                {
                    if (splitTransactionIds == null || splitTransactionIds.equals(""))
                        directTransactionResponse.setSplitTransactionId(directKitResponseVO.getSplitTransactionId());
                    else
                        directTransactionResponse.setSplitTransactionId(splitTransactionIds + "," + directKitResponseVO.getSplitTransactionId());
                }
            }
        }
    }

    public void convertRefundSystemResponseToClientsResponse(DirectRefundResponse directRefundResponse, DirectKitResponseVO directKitResponseVO)
    {
        if (directKitResponseVO != null)
        {
            if (functions.isValueNull(directKitResponseVO.getTrackingId()))
                directRefundResponse.setTrackingId(directKitResponseVO.getTrackingId());
            if (functions.isValueNull(directKitResponseVO.getAmount()))
                directRefundResponse.setRefundAmount(directKitResponseVO.getAmount());
            if (functions.isValueNull(directKitResponseVO.getStatus()))
                directRefundResponse.setStatus(directKitResponseVO.getStatus());
            if (functions.isValueNull(directKitResponseVO.getStatusMsg()))
                directRefundResponse.setStatusDescription(directKitResponseVO.getStatusMsg());
            if (directKitResponseVO.getErrorCodeListVO() != null && !directKitResponseVO.getErrorCodeListVO().getListOfError().isEmpty())
                directRefundResponse.setDirectTransactionErrorCodes(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));

        }
    }

    public void convertInquirySystemResponseToClientsResponse(DirectInquiryResponse directInquiryResponse, DirectKitResponseVO directKitResponseVO)
    {
        if (directKitResponseVO != null)
        {
            if (functions.isValueNull(directKitResponseVO.getTrackingId()))
                directInquiryResponse.setTrackingId(directKitResponseVO.getTrackingId());
            if (functions.isValueNull(directKitResponseVO.getDescription()))
                directInquiryResponse.setDescription(directKitResponseVO.getDescription());
            if (functions.isValueNull(directKitResponseVO.getAmount()))
                directInquiryResponse.setAuthAmount(directKitResponseVO.getAmount());
            if (functions.isValueNull(directKitResponseVO.getCaptureAmount()))
                directInquiryResponse.setCapturedAmount(directKitResponseVO.getCaptureAmount());
            if (functions.isValueNull(directKitResponseVO.getStatus()))
                directInquiryResponse.setStatus(directKitResponseVO.getStatus());
            //directInquiryResponse.setStatus((PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() :PZShortResponseStatus.ERROR.toString())));

            if (functions.isValueNull(directKitResponseVO.getStatusMsg()))
                directInquiryResponse.setStatusDescription(directKitResponseVO.getStatusMsg());
            if (functions.isValueNull(directKitResponseVO.getRemark()))
                directInquiryResponse.setRemark(directKitResponseVO.getRemark());

            if (directKitResponseVO.getErrorCodeListVO() != null && !directKitResponseVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                if ("Y".equals(directKitResponseVO.getFlightMode()))
                {
                    StringBuffer pipedErrorCode = new StringBuffer();

                    for (ErrorCodeVO errorCodeVO : directKitResponseVO.getErrorCodeListVO().getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    directInquiryResponse.setStatusDescription(pipedErrorCode.toString());
                }
                else
                {
                    directInquiryResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));
                }
            }
        }
    }

    public void convertCaptureSystemResponseToClientsResponse(DirectCaptureResponse directCaptureResponse, DirectKitResponseVO directKitResponseVO)
    {
        if (directKitResponseVO != null)
        {
            if (functions.isValueNull(directKitResponseVO.getTrackingId()))
                directCaptureResponse.setTrackingId(directKitResponseVO.getTrackingId());
            if (functions.isValueNull(directKitResponseVO.getDescription()))
                directCaptureResponse.setStatusDescription(directKitResponseVO.getDescription());
            if (functions.isValueNull(directKitResponseVO.getAmount()))
                directCaptureResponse.setAuthAmount(directKitResponseVO.getAmount());
            if (functions.isValueNull(directKitResponseVO.getCaptureAmount()))
                directCaptureResponse.setCaptureAmount(directKitResponseVO.getCaptureAmount());
            if (functions.isValueNull(directKitResponseVO.getStatus()))
                directCaptureResponse.setStatus(directKitResponseVO.getStatus());
            //directInquiryResponse.setStatus((PZResponseStatus.SUCCESS.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()) ? PZShortResponseStatus.FAILED.toString() :PZShortResponseStatus.ERROR.toString())));

            if (functions.isValueNull(directKitResponseVO.getStatusMsg()))
                directCaptureResponse.setStatusDescription(directKitResponseVO.getStatusMsg());

            if (directKitResponseVO.getErrorCodeListVO() != null && !directKitResponseVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                if ("Y".equals(directKitResponseVO.getFlightMode()))
                {
                    StringBuffer pipedErrorCode = new StringBuffer();

                    for (ErrorCodeVO errorCodeVO : directKitResponseVO.getErrorCodeListVO().getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    directCaptureResponse.setStatusDescription(pipedErrorCode.toString());
                }
                else
                {
                    directCaptureResponse.setDirectTransactionErrorCodes(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));
                }
            }
        }
    }

    public void convertCancelSystemResponseToClientsResponse(DirectCancelResponse directCancelResponse, DirectKitResponseVO directKitResponseVO)
    {
        if (directKitResponseVO != null)
        {
            if (functions.isValueNull(directKitResponseVO.getTrackingId()))
                directCancelResponse.setTrackingId(directKitResponseVO.getTrackingId());
            if (functions.isValueNull(directKitResponseVO.getDescription()))
                directCancelResponse.setDescription(directKitResponseVO.getDescription());
            if (functions.isValueNull(directKitResponseVO.getStatus()))
                directCancelResponse.setStatus(directKitResponseVO.getStatus());
            if (functions.isValueNull(directKitResponseVO.getStatusMsg()))
                directCancelResponse.setStatusDescription(directKitResponseVO.getStatusMsg());
            if (functions.isValueNull(directKitResponseVO.getGeneratedCheckSum()))
                directCancelResponse.setNewCheckSum(directKitResponseVO.getGeneratedCheckSum());

            if (directKitResponseVO.getErrorCodeListVO() != null && !directKitResponseVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                if ("Y".equals(directKitResponseVO.getFlightMode()))
                {
                    StringBuffer pipedErrorCode = new StringBuffer();

                    for (ErrorCodeVO errorCodeVO : directKitResponseVO.getErrorCodeListVO().getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    directCancelResponse.setStatusDescription(pipedErrorCode.toString());
                }
                else
                {
                    directCancelResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(directKitResponseVO.getErrorCodeListVO(), directKitResponseVO.getFlightMode()));
                }
            }
        }
    }


    public void setClientTransactionResponse(DirectTransactionResponse directTransactionResponse, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        String flightMode = "N";
        if (directTransactionResponse != null)
        {
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directTransactionResponse.setTrackingId(commonValidatorVO.getTrackingid());

                if (commonValidatorVO.getTransDetailsVO() != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                        directTransactionResponse.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                        directTransactionResponse.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());

                }
                if (commonValidatorVO.getMerchantDetailsVO() != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        flightMode = commonValidatorVO.getMerchantDetailsVO().getFlightMode();
                }
            }

            //logger.debug("FlightMode::::::"+commonValidatorVO.getMerchantDetailsVO().getFlightMode());

            if (status != null)
                directTransactionResponse.setStatus((PZResponseStatus.SUCCESS.name().equals(status.name()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(status.name()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString())));

            if ((PZShortResponseStatus.ERROR.toString()).equals(directTransactionResponse.getStatus()) && "Y".equals(flightMode))
            {
                StringBuffer pipedErrorCode = new StringBuffer();
                if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
                {
                    for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    directTransactionResponse.setStatusDescription(pipedErrorCode.toString());
                }


            }
            else
            {
                if (functions.isValueNull(remark))
                    directTransactionResponse.setStatusDescription(remark);


            }
            if (!"Y".equals(flightMode) && errorCodeListVO != null)
            {
                directTransactionResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
            }
        }
    }

    //This is for refund transaction
    public void setClientRefundResponse(DirectRefundResponse directRefundResponse, DirectRefundValidatorVO directRefundValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        String flightMode = null;
        if (directRefundResponse != null)
        {
            if (directRefundValidatorVO != null)
            {
                if (functions.isValueNull(directRefundValidatorVO.getTrackingid()))
                    directRefundResponse.setTrackingId(directRefundValidatorVO.getTrackingid());
                if (functions.isValueNull(directRefundValidatorVO.getRefundAmount()))
                    directRefundResponse.setRefundAmount(directRefundValidatorVO.getRefundAmount());
                if (functions.isValueNull(directRefundValidatorVO.getGeneratedCheckSum()))
                    directRefundResponse.setNewCheckSum(directRefundValidatorVO.getGeneratedCheckSum());

                if (directRefundValidatorVO.getMerchantDetailsVO() != null)
                {
                    if (functions.isValueNull(directRefundValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        flightMode = directRefundValidatorVO.getMerchantDetailsVO().getFlightMode();
                }
            }
            if (status != null)
                directRefundResponse.setStatus(status.name());
            if (functions.isValueNull(remark))
                directRefundResponse.setStatusDescription(remark);
            if (errorCodeListVO != null)
                directRefundResponse.setDirectTransactionErrorCodes(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
        }
    }

    //This is for capture transaction
    public void setClientCaptureResponse(DirectCaptureResponse directCaptureResponse, DirectCaptureValidatorVO directCaptureValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        String flightMode = null;
        if (directCaptureResponse != null)
        {
            if (directCaptureValidatorVO != null)
            {
                if (functions.isValueNull(directCaptureValidatorVO.getTrackingid()))
                    directCaptureResponse.setTrackingId(directCaptureValidatorVO.getTrackingid());
                if (directCaptureValidatorVO.getTransDetailsVO() != null)
                {
                    if (functions.isValueNull(directCaptureValidatorVO.getCaptureAmount()) && (status != null || PZResponseStatus.SUCCESS.equals(status)))
                        directCaptureResponse.setCaptureAmount(directCaptureValidatorVO.getCaptureAmount());
                }

                if (directCaptureValidatorVO.getMerchantDetailsVO() != null)
                {
                    if (functions.isValueNull(directCaptureValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        flightMode = directCaptureValidatorVO.getMerchantDetailsVO().getFlightMode();
                }
            }
            if (status != null)
                directCaptureResponse.setStatus(status.name());
            if (functions.isValueNull(remark))
                directCaptureResponse.setStatusDescription(remark);
            if (errorCodeListVO != null)
                directCaptureResponse.setDirectTransactionErrorCodes(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
        }

    }

    //This is for refund transaction
    public void setClientCancelResponse(DirectCancelResponse directCancelResponse, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        String flightMode = null;
        if (directCancelResponse != null)
        {
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directCancelResponse.setTrackingId(commonValidatorVO.getTrackingid());
                if (commonValidatorVO.getTransDetailsVO() != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                        directCancelResponse.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                }

                if (commonValidatorVO.getMerchantDetailsVO() != null)
                {
                    if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        flightMode = commonValidatorVO.getMerchantDetailsVO().getFlightMode();
                }
            }
            if (status != null)
                directCancelResponse.setStatus(status.name());
            if (functions.isValueNull(remark))
                directCancelResponse.setStatusDescription(remark);
            if (errorCodeListVO != null)
                directCancelResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
        }

    }

    public void setClientInquiryResponse(DirectInquiryResponse directInquiryResponse, DirectInquiryValidatorVO directInquiryValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        String flightMode = null;
        if (directInquiryResponse != null)
        {
            if (directInquiryValidatorVO != null)
            {
                if (functions.isValueNull(directInquiryValidatorVO.getTrackingId()))
                    directInquiryResponse.setTrackingId(directInquiryValidatorVO.getTrackingId());
                if (functions.isValueNull(directInquiryValidatorVO.getDescription()))
                    directInquiryResponse.setDescription(directInquiryValidatorVO.getDescription());
                if (functions.isValueNull(directInquiryValidatorVO.getAuthAmount()))
                    directInquiryResponse.setAuthAmount(directInquiryValidatorVO.getAuthAmount());
                if (functions.isValueNull(directInquiryValidatorVO.getCaptureAmount()))
                    directInquiryResponse.setCapturedAmount(directInquiryValidatorVO.getCaptureAmount());

                if (directInquiryValidatorVO.getMerchantDetailsVO() != null)
                {
                    if (functions.isValueNull(directInquiryValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        flightMode = directInquiryValidatorVO.getMerchantDetailsVO().getFlightMode();
                }

            }
            if (status != null)
                directInquiryResponse.setStatus((PZResponseStatus.SUCCESS.name().equals(status.name()) ? PZShortResponseStatus.SUCCESS.toString() : (PZResponseStatus.FAILED.name().equals(status.name()) ? PZShortResponseStatus.FAILED.toString() : PZShortResponseStatus.ERROR.toString())));

            if ((PZShortResponseStatus.ERROR.toString()).equals(directInquiryResponse.getStatus()) && "Y".equals(flightMode))
            {
                StringBuffer pipedErrorCode = new StringBuffer();
                if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
                {
                    for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                    {
                        if (pipedErrorCode.length() > 0)
                        {
                            pipedErrorCode.append("|");
                        }
                        pipedErrorCode.append(errorCodeVO.getErrorCode());
                    }
                    directInquiryResponse.setStatusDescription(pipedErrorCode.toString());
                }


            }
            else
            {
                if (functions.isValueNull(remark))
                    directInquiryResponse.setStatusDescription(remark);
            }


            if (!"Y".equals(flightMode) && errorCodeListVO != null)
                directInquiryResponse.setDirectTransactionErrorCodeVOs(convertSystemErrorListVoToClientsListOfErrorCodeVO(errorCodeListVO, flightMode));
        }

    }

    //Start Rest Error Response for Transaction
    public void setRestTransactionResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {

        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessRestTransactionResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        logger.error("inside setSuccessRestTransactionResponse---");
        Result result = null;
        Card card = null;
        //Risk risk = null;
        BankAccount bankAccount = null;
        Parameters parameters = null;
        Redirect redirect = null;

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        if (directKitResponseVO != null)
        {
            card = new Card();
            result = new Result();
            //risk = new Risk();
            bankAccount = new BankAccount();
            redirect = new Redirect();
            if (functions.isValueNull(commonValidatorVO.getBankCode()) && functions.isValueNull(commonValidatorVO.getBankDescription()))
            {
                result.setBankCode(commonValidatorVO.getBankCode());
                result.setBankDescription(commonValidatorVO.getBankDescription());
            }
            if ("success".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                }
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("Y");
            }
            else if ("pending".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
                }

                logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());

                redirect.setUrl(directKitResponseVO.getBankRedirectionUrl());
                if (directKitResponseVO.getListOfAsyncParameterVo().size() > 0)
                {
                    for (AsyncParameterVO asyncParameterVO : directKitResponseVO.getListOfAsyncParameterVo())
                    {
                        parameters = new Parameters();

                        parameters.setName(asyncParameterVO.getName());
                        parameters.setValue(asyncParameterVO.getValue());
                        redirect.addListOfParameters(parameters);
                    }
                    logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
                    response.setRedirect(redirect);
                }

                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("P");
            }
            else
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                }

                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("N");
            }
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                if(commonValidatorVO.getMerchantDetailsVO().getIsCardStorageRequired().equalsIgnoreCase("N")){

                    card.setBin("");
                    card.setLastFourDigits("");
                    card.setLast4Digits("");
                    card.setHolder("");
                    card.setExpiryMonth("");
                    card.setExpiryYear("");
                    response.setCard(card);
                }else{
                    String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                    String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                    card.setBin(firstSix);
                    card.setLastFourDigits(lastFour);
                    card.setLast4Digits(lastFour);
                    card.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                    card.setExpiryMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                    card.setExpiryYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                    response.setCard(card);
                }

            }
            else if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
            {
                bankAccount.setBic(commonValidatorVO.getCardDetailsVO().getBIC());
                bankAccount.setIban(commonValidatorVO.getCardDetailsVO().getIBAN());
                bankAccount.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                bankAccount.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname());
                response.setBankAccount(bankAccount);
            }
            //risk.setScore("0");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            response.setPaymentId(directKitResponseVO.getTrackingId());
            if (directKitResponseVO.getToken() != null)
                response.setRegistrationId(directKitResponseVO.getToken());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            response.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            response.setMerchantTransactionId(commonValidatorVO.getTransDetailsVO().getOrderId());
            if(functions.isValueNull(directKitResponseVO.getStatusMsg()))
                response.setRemark(directKitResponseVO.getStatusMsg());
            else
                response.setRemark("");
            response.setCustomerId(commonValidatorVO.getCustomerId());
            response.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

            if (commonValidatorVO.getTransDetailsVO() != null)
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(directKitResponseVO.getBillingDescriptor());
            logger.error("commonValidatorVO.getMerchantDetailsVO().getTransactionNotification()---->"+commonValidatorVO.getMerchantDetailsVO().getTransactionNotification());

            String notificationUrl = "";

            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())){
                notificationUrl = commonValidatorVO.getTransDetailsVO().getNotificationUrl();
                logger.error("notificationUrl Transaction ---->"+commonValidatorVO.getTrackingid()+" "+notificationUrl);
            }else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl())){
                notificationUrl = commonValidatorVO.getMerchantDetailsVO().getNotificationUrl();
                logger.error("notificationUrl Merchant Configuration  ----> "+commonValidatorVO.getTrackingid()+" "+notificationUrl);
            }

            logger.error("Notification Url  ----> "+commonValidatorVO.getTrackingid()+" "+notificationUrl);

            if (functions.isValueNull(notificationUrl) && !"pending".equalsIgnoreCase(directKitResponseVO.getStatus()) && ("Non-3D".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification()) || "Both".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification())))
            {
                TransactionDetailsVO transactionDetailsVO           = getTransactionDetails(commonValidatorVO);
                if(commonValidatorVO.getMerchantDetailsVO().getIsCardStorageRequired().equalsIgnoreCase("N")){
                    transactionDetailsVO.setCcnum("");
                }
                AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                if(functions.isValueNull(directKitResponseVO.getBankReferenceId())){
                    transactionDetailsVO.setBankReferenceId(directKitResponseVO.getBankReferenceId());
                }

                asyncNotificationService.sendNotification(transactionDetailsVO, directKitResponseVO.getTrackingId(), directKitResponseVO.getStatus(), directKitResponseVO.getStatusMsg());
            }
        }
    }

    public void setRestTransactionResponseForInternalFraudCheck(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, DirectKitResponseVO directKitResponseVO)
    {

        Result result = null;
        if (response != null)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            if (commonValidatorVO != null)
            {
                response.setPaymentId(directKitResponseVO.getTrackingId());
                response.setPaymentType(commonValidatorVO.getTransactionType());
                response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
                response.setPaymentMode(commonValidatorVO.getPaymentMode());
                response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                response.setDescriptor(directKitResponseVO.getBillingDescriptor());
                response.setResult(result);
                response.setTimestamp(String.valueOf(dateFormat.format(date)));
                response.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
                response.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
                response.setMerchantTransactionId(commonValidatorVO.getTransDetailsVO().getOrderId());
                response.setRemark(directKitResponseVO.getStatusMsg());
            }
            if (errorCodeListVO != null && commonValidatorVO.getErrorCodeListVO().getListOfError().size() > 0)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : commonValidatorVO.getErrorCodeListVO().getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessRestPayoutResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        Customer customer = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        if (directKitResponseVO != null)
        {
            result = new Result();

            if ("success".equalsIgnoreCase(directKitResponseVO.getStatus()) || "PAYOUTSUCCESSFUL".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("Y");
                response.setStatus("Y");
            }
            else if ("pending".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("P");
                response.setStatus("P");
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("N");
                response.setStatus("N");
            }

            customer = new Customer();
            if (functions.isValueNull(directKitResponseVO.getEmail()) || functions.isValueNull(directKitResponseVO.getCustBankId()) || functions.isValueNull(directKitResponseVO.getCustId()))
            {
                customer.setEmail(directKitResponseVO.getEmail());
                customer.setId(directKitResponseVO.getCustId());
                customer.setCustomerBankId(directKitResponseVO.getCustBankId());

                response.setCustomer(customer);
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            response.setPaymentId(directKitResponseVO.getTrackingId());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            /*response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());*/
            if (directKitResponseVO.getAmount() != null)
            {
                response.setAmount(directKitResponseVO.getAmount());
            }
            else
            {
                response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            }

            /*response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());*/

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setMerchantTransactionId(commonValidatorVO.getTransDetailsVO().getOrderId());
            logger.debug("remark in payout response---" + directKitResponseVO.getRemark());
            response.setRemark(directKitResponseVO.getRemark());
            if (directKitResponseVO.getVoucherNumber() != null)
                response.setVoucherNumber(directKitResponseVO.getVoucherNumber());
            if (functions.isValueNull(directKitResponseVO.getTmpl_amount()))
                response.setTmpl_amount(directKitResponseVO.getTmpl_amount());

            if (functions.isValueNull(directKitResponseVO.getTmpl_currency()))
                response.setTmpl_currency(directKitResponseVO.getTmpl_currency());

            if (directKitResponseVO.getCustAccount() != null)
                response.setCustomerAccount(directKitResponseVO.getCustAccount());

            if (functions.isValueNull(directKitResponseVO.getCustId()))
                response.setCustomerId(directKitResponseVO.getCustId());

            if (functions.isValueNull(directKitResponseVO.getMerchantUsersCommission()))
                response.setPayoutCommissionAmount(directKitResponseVO.getMerchantUsersCommission());

            if (functions.isValueNull(directKitResponseVO.getMerchantUsersCommission()))
                response.setCommissionCurrency(directKitResponseVO.getCommissionCurrency());
        }
    }

    public void setSuccessRestExchangerResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        //Customer customer = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        if (directKitResponseVO != null)
        {
            result = new Result();

            errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());

            response.setPaymentId(directKitResponseVO.getTrackingId());
            response.setResult(result);
        }
    }


    public void setSuccessRestAsyncTransactionResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {

        logger.debug("inside async write response---");
        Result result = null;
        Card card = null;
        Risk risk = null;
        BankAccount bankAccount = null;
        Redirect redirect = null;
        Parameters parameters = null;

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;

        if (directKitResponseVO != null)
        {
            card = new Card();
            result = new Result();
            //risk = new Risk();
            bankAccount = new BankAccount();
            redirect = new Redirect();
            //directKitResponseVO.setStatus("");

            logger.error("commonValidatorVO.getPaymentBrand(): "+commonValidatorVO.getPaymentBrand());
            if ("FLEXEPIN VOUCHER".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()) && "success".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else if ("success".equals(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("P");
            }
            else
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }


            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                card.setBin(firstSix);
                card.setLastFourDigits(lastFour);
                card.setLast4Digits(lastFour);
                card.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                card.setExpiryMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                card.setExpiryYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                response.setCard(card);
            }
            else if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
            {
                bankAccount.setBic(commonValidatorVO.getCardDetailsVO().getBIC());
                bankAccount.setIban(commonValidatorVO.getCardDetailsVO().getIBAN());
                bankAccount.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                bankAccount.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname());
                response.setBankAccount(bankAccount);
            }

            //risk.setScore("0");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            logger.debug("Submit URL---" + directKitResponseVO.getBankRedirectionUrl());
            redirect.setUrl(directKitResponseVO.getBankRedirectionUrl());
            if (directKitResponseVO.getMethod() != null)
            {
                redirect.setMethod(directKitResponseVO.getMethod());
            }
            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            for (AsyncParameterVO asyncParameterVO : directKitResponseVO.getListOfAsyncParameterVo())
            {
                parameters = new Parameters();
                parameters.setName(asyncParameterVO.getName());
                parameters.setValue(asyncParameterVO.getValue());
                redirect.addListOfParameters(parameters);
            }
            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            response.setRedirect(redirect);

            response.setPaymentId(directKitResponseVO.getTrackingId());
            if (directKitResponseVO.getToken() != null)
                response.setRegistrationId(directKitResponseVO.getToken());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setPaymentId(directKitResponseVO.getTrackingId());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessRest3DTransactionResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {

        logger.error("inside setSuccessRest3DTransactionResponse---");
        Result result = null;
        Card card = null;
        Risk risk = null;
        BankAccount bankAccount = null;
        Redirect redirect = null;
        Parameters parameters = null;

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;

        if (directKitResponseVO != null)
        {
            redirect = new Redirect();

            card = new Card();
            result = new Result();
            //risk = new Risk();
            bankAccount = new BankAccount();
            redirect = new Redirect();
            directKitResponseVO.setStatus("");
            if (functions.isValueNull(commonValidatorVO.getBankCode()) && functions.isValueNull(commonValidatorVO.getBankDescription()))
            {
                result.setBankCode(commonValidatorVO.getBankCode());
                result.setBankDescription(commonValidatorVO.getBankDescription());
            }
            if (functions.isValueNull(directKitResponseVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFULL_3D_AUTHENTICATION_PENDING_TRANSACTION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("3D");

            }


            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                if(commonValidatorVO.getMerchantDetailsVO().getIsCardStorageRequired().equalsIgnoreCase("N")){
                    card.setBin("");
                    card.setLastFourDigits("");
                    card.setLast4Digits("");
                    card.setHolder("");
                    card.setExpiryMonth("");
                    card.setExpiryYear("");
                    response.setCard(card);
                }else{
                    String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                    String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                    card.setBin(firstSix);
                    card.setLastFourDigits(lastFour);
                    card.setLast4Digits(lastFour);
                    card.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                    card.setExpiryMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                    card.setExpiryYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                    response.setCard(card);
                }

            }
            else if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBIC()))
            {
                bankAccount.setBic(commonValidatorVO.getCardDetailsVO().getBIC());
                bankAccount.setIban(commonValidatorVO.getCardDetailsVO().getIBAN());
                bankAccount.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                bankAccount.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname());
                response.setBankAccount(bankAccount);
            }

            //risk.setScore("0");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            logger.debug("Submit URL---" + directKitResponseVO.getBankRedirectionUrl());
            redirect.setUrl(directKitResponseVO.getBankRedirectionUrl());
            if (directKitResponseVO.getMethod() != null)
            {
                redirect.setMethod(directKitResponseVO.getMethod());
            }
            if (directKitResponseVO.getTarget() != null)
            {
                redirect.setTarget(directKitResponseVO.getTarget());
            }

            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            for (AsyncParameterVO asyncParameterVO : directKitResponseVO.getListOfAsyncParameterVo())
            {
                parameters = new Parameters();

                parameters.setName(asyncParameterVO.getName());
                parameters.setValue(asyncParameterVO.getValue());
                redirect.addListOfParameters(parameters);
            }
            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            response.setRedirect(redirect);

            response.setPaymentId(directKitResponseVO.getTrackingId());
            if (directKitResponseVO.getToken() != null)
                response.setRegistrationId(directKitResponseVO.getToken());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setPaymentId(directKitResponseVO.getTrackingId());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

            /*if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())){
                TransactionDetailsVO transactionDetailsVO=getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, directKitResponseVO.getTrackingId(), directKitResponseVO.getStatus(), directKitResponseVO.getStatusMsg());
            }*/
        }
    }


    public void setRestCaptureResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        Result result = null;
        if (response != null)
        {
            result = new Result();
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    response.setPaymentId("-");
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentType()))
                    response.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());

            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    if (errorCode.length() > 0)
                    {
                        errorCode.append("|");
                    }
                    errorCode.append(errorCode.toString());
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());

                }
            }
            response.setResult(result);
        }
    }

    public void setSuccessRestCaptureResonse(Response response, DirectKitResponseVO directKitResponseVO, DirectCaptureValidatorVO directCaptureValidatorVO,CommonValidatorVO commonValidatorVO)
    {
        Result result               = null;
        ErrorCodeVO errorCodeVO     = null;
        String transactioStatus     = "";
        if (response != null)
        {
            result          = new Result();
            errorCodeVO     = new ErrorCodeVO();
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date               = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("Y"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CAPTURE_SUCCESSFUL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.CAPTURE_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in ResponseVO
            response.setPaymentId(directCaptureValidatorVO.getTrackingid());
            response.setPaymentType(directCaptureValidatorVO.getPaymentType());
            response.setDescriptor(directKitResponseVO.getDescription());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

            /*String notificationUrl = "";

            if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl())){
                notificationUrl = commonValidatorVO.getTransDetailsVO().getNotificationUrl();
            }else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl())){
                notificationUrl = commonValidatorVO.getMerchantDetailsVO().getNotificationUrl();
            }

            if(directKitResponseVO.getStatus().equalsIgnoreCase("Y")){
                transactioStatus ="capturesuccess";
            }else if(directKitResponseVO.getStatus().equalsIgnoreCase("N")) {
                transactioStatus ="capturestarted";
            }else{
                transactioStatus ="pending";
            }

            logger.error("Notification Url----------> "+commonValidatorVO.getTrackingid()+" "+notificationUrl);
            logger.error("directKitResponseVO.getStatus() Url  ----> "+ commonValidatorVO.getTrackingid()+" "+directKitResponseVO.getStatus());

            //if (functions.isValueNull(notificationUrl) && !"pending".equalsIgnoreCase(directKitResponseVO.getStatus()) && ("Non-3D".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification()) || "Both".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification())))
            if (functions.isValueNull(notificationUrl) && !"pending".equalsIgnoreCase(transactioStatus) && ("Non-3D".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification()) || "Both".equals(commonValidatorVO.getMerchantDetailsVO().getTransactionNotification())))
            {
                TransactionManager transactionManager               = new  TransactionManager();
                AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(commonValidatorVO.getTrackingid());

                if(commonValidatorVO.getMerchantDetailsVO().getIsCardStorageRequired().equalsIgnoreCase("N")){
                    transactionDetailsVO.setCcnum("");
                }

                if(functions.isValueNull(directKitResponseVO.getBankReferenceId())){
                    transactionDetailsVO.setBankReferenceId(directKitResponseVO.getBankReferenceId());
                }
                if(functions.isValueNull(transactionDetailsVO.getCcnum()))
                {

                   String cardStr = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                   String Expdate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                    transactionDetailsVO.setCcnum(cardStr);
                    transactionDetailsVO.setExpdate(Expdate);

                }else{
                    transactionDetailsVO.setCcnum("");
                    transactionDetailsVO.setExpdate("");
                }
                transactionDetailsVO.setSecretKey(commonValidatorVO.getMerchantDetailsVO().getKey());

                if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl())){
                    transactionDetailsVO.setMerchantNotificationUrl(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl());
                }else{
                    transactionDetailsVO.setMerchantNotificationUrl("");
                }
                asyncNotificationService.sendNotification(transactionDetailsVO, directKitResponseVO.getTrackingId(), transactioStatus, transactionDetailsVO.getRemark());
            }*/
        }
    }

    public void setRestCancelResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        Result result = new Result();
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    response.setPaymentId("-");
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentType()))
                    response.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());
                if (functions.isValueNull(commonValidatorVO.getErrorMsg()))
                    result.setDescription(commonValidatorVO.getErrorMsg());
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }
            response.setResult(result);
        }
    }

    public void setSuccessRestCancelResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result               = null;
        ErrorCodeVO errorCodeVO     = null;
        String notificationUrl      = "";
        String transactioStatus     = "";

        if (response != null)
        {
            errorCodeVO = new ErrorCodeVO();
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            logger.error("==========STATUS OF APCOFATSPAYV6PAYMENT GATEWAY============="+directKitResponseVO.getStatus());

            if (directKitResponseVO.getStatus().equalsIgnoreCase("Y"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CANCEL_SUCCESSFUL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.CANCEL_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            //set success element in responseVO
            response.setPaymentId(commonValidatorVO.getTrackingid());
            response.setPaymentType(commonValidatorVO.getPaymentType());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
    }

    public void setRestRefundResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        Result result = null;
        if (response != null)
        {
            result = new Result();
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    response.setPaymentId("-");
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentType()))
                    response.setPaymentType(commonValidatorVO.getTransDetailsVO().getPaymentType());

            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    if (errorCode.length() > 0)
                    {
                        errorCode.append("|");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }
            response.setResult(result);
        }
    }

    public void setSuccessRestRefundResonse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        logger.debug("Inside refund response----------");
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        if (response != null)
        {
            errorCodeVO = new ErrorCodeVO();
            result      = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date               = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.REFUND_SUCCESSFUL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REFUND_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in ResponseVO
            response.setPaymentId(commonValidatorVO.getTrackingid());
            response.setPaymentType(commonValidatorVO.getPaymentType());
            response.setDescriptor(directKitResponseVO.getDescription());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
    }

    public void setRestDeleteTokenResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        Result result = new Result();
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    response.setPaymentId("-");
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }
            response.setPaymentId("-");
            response.setResult(result);
        }
    }

    public void setSuccessRestInquiryResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        Card card = null;
        Customer customer = null;
        ErrorCodeVO errorCodeVO = null;
        if (response != null)
        {
            result = new Result();
            card = new Card();
            errorCodeVO = new ErrorCodeVO();
            customer = new Customer();
            //For Phoneix Gateway
            if (functions.isValueNull(commonValidatorVO.getBankCode()) && functions.isValueNull(commonValidatorVO.getBankDescription()))
            {
                result.setBankCode(commonValidatorVO.getBankCode());
                result.setBankDescription(commonValidatorVO.getBankDescription());
            }
            if (functions.isValueNull(directKitResponseVO.getErrorName()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.begun.toString());
            }
            //else if(!PZResponseStatus.FAILED.name().equals(directKitResponseVO.getStatus()))
            //Success Transactions
            else if (directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.SUCCESS.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authsuccessful.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturesuccess.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authcancelled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.setteled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.settled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.markedforreversal.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.reversed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.chargeback.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutsuccessful.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.success.name()))
            {
                //errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.SUCCESS.toString());
            }
            //Failed Transactions
            else if (directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.FAILED.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authfailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturefailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutfailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.failed.name()))
            {
                //errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.FAILED.toString());
            }
            //Pending Transaction
            else if (directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.begun.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.PARTIAL_SUCCESS.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authstarted.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authstarted_3D.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturestarted.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutstarted.name()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.begun.toString());
            }
            //Cancel Transaction
            else if (directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.cancelled.name()))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CANCEL_SUCCESSFUL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.cancelled.toString());
            }
            //Error Transaction
            /*else if(directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.ERROR.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.error.name()))
            {
                //errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_DECLINED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }*/
            //Record Not Found
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_RECORD_NOT_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus(PZShortResponseStatus.FAILED.toString());
            }
            response.setResult(result);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();


            if (null != commonValidatorVO.getCardDetailsVO() && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                card.setBin(firstSix);
                card.setLastFourDigits(lastFour);
                card.setLast4Digits(lastFour);
                card.setHolder(commonValidatorVO.getCardDetailsVO().getCardHolderName());
                card.setExpiryMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                card.setExpiryYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                response.setCard(card);
            }else{
                card.setBin("");
                card.setLastFourDigits("");
                card.setLast4Digits("");
                card.setHolder("");
                card.setExpiryMonth("");
                card.setExpiryYear("");
                response.setCard(card);
            }

            if (functions.isValueNull(directKitResponseVO.getEmail()) || functions.isValueNull(directKitResponseVO.getCustId()))
            {
                customer.setEmail(directKitResponseVO.getEmail());
                customer.setId(directKitResponseVO.getCustId());
                customer.setCustomerBankId(directKitResponseVO.getCustBankId());
                response.setCustomer(customer);
            }

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
                response.setFirstName(commonValidatorVO.getAddressDetailsVO().getFirstname());
            else
                response.setFirstName("");
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
                response.setLastName(commonValidatorVO.getAddressDetailsVO().getLastname());
            else
                response.setLastName("");
            if (functions.isValueNull(commonValidatorVO.getEci()))
                response.setEci(commonValidatorVO.getEci());
            else
                response.setEci("");
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getBillingDiscriptor()))
                response.setDescriptor(commonValidatorVO.getTransDetailsVO().getBillingDiscriptor());
            else
                response.setDescriptor("");

            if (functions.isValueNull(directKitResponseVO.getTerminalId()))
                response.setTerminalId(directKitResponseVO.getTerminalId());
            else
                response.setTerminalId("");

            if (functions.isValueNull(directKitResponseVO.getBankReferenceId()))
                response.setBankReferenceId(directKitResponseVO.getBankReferenceId());
            else
                response.setBankReferenceId("");
            //set success element in responseVO
            response.setPaymentId(commonValidatorVO.getTrackingid());
            response.setMerchantTransactionId(directKitResponseVO.getDescription());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            response.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            //response.setMerchantTransactionId(commonValidatorVO.getTransDetailsVO().getOrderId());

            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc()))
                response.setOrderDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());

            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
                response.setCompanyName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());

            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getContact_persons()))
                response.setMerchantContact(commonValidatorVO.getMerchantDetailsVO().getContact_persons());

            response.setStatus(directKitResponseVO.getStatus());
            if (response.getStatus().equalsIgnoreCase("reversed"))
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRefundAmount()))
                    response.setRefundAmount(commonValidatorVO.getTransDetailsVO().getRefundAmount());
            }
            if (response.getStatus().equalsIgnoreCase("payoutsuccessful"))
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPayoutAmount()))
                    response.setPayoutAmount(commonValidatorVO.getTransDetailsVO().getPayoutAmount());
            }

            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setRemark(directKitResponseVO.getRemark());

            if (functions.isValueNull(directKitResponseVO.getCommissionToPay()))
                response.setDepositCommissionAmount(directKitResponseVO.getCommissionToPay());
            if (functions.isValueNull(directKitResponseVO.getCommissionCurrency()))
                response.setCommissionPaidToUserCurrency(directKitResponseVO.getCommissionCurrency());

            //checksum set for notification sent not for REST Inquiry API response
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getChecksum()))
                response.setChecksum(commonValidatorVO.getTransDetailsVO().getChecksum());
            logger.error("commonValidatorVO.getMarketPlaceVOList()--->"+commonValidatorVO.getMarketPlaceVOList());
            if(commonValidatorVO.getMarketPlaceVOList()!=null && commonValidatorVO.getMarketPlaceVOList().size()>0)
            {
                MarketPlace marketPlace=null;
                List<MarketPlace> marketPlaceList=new ArrayList<>();
                for (MarketPlaceVO marketPlaceVO:commonValidatorVO.getMarketPlaceVOList())
                {
                    marketPlace=new MarketPlace();
                    marketPlace.setTrackingid(marketPlaceVO.getTrackingid());
                    marketPlace.setOrderid(marketPlaceVO.getOrderid());
                    marketPlace.setAmount(marketPlaceVO.getAmount());
                    marketPlaceList.add(marketPlace);
                }
                if(marketPlaceList.size()>0)
                    response.setMarketPlaceDetails(marketPlaceList);
            }


            /*if(directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.begun.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.PARTIAL_SUCCESS.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authstarted.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authcancelled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturestarted.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutstarted.name()))
                response.setTransactionStatus(PZShortResponseStatus.begun.toString());

            if(directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.SUCCESS.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authsuccessful.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturesuccess.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.setteled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.settled.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.markedforreversal.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.reversed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.chargeback.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutsuccessful.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.success.name()))
                response.setTransactionStatus(PZShortResponseStatus.SUCCESS.toString());

            if(directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.FAILED.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.authfailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.capturefailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.payoutfailed.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.failed.name()))
                response.setTransactionStatus(PZShortResponseStatus.FAILED.toString());

            if(directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.ERROR.name()) || directKitResponseVO.getStatus().equalsIgnoreCase(PZShortResponseStatus.error.name()))
                response.setTransactionStatus(PZShortResponseStatus.ERROR.toString());*/

            logger.error("end of setSuccessRestInquiryResponse ---");
        }
    }

    public void setSuccessRestDeleteTokenResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            errorCodeVO = new ErrorCodeVO();
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TOKEN_INACTIVT_SUCCESSFUL);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TOKEN_CREATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in responseVO
            response.setRegistrationId(commonValidatorVO.getToken());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
    }

    public void setSuccessRestMerchantSignupResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            errorCodeVO = new ErrorCodeVO();
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MERCHANT_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFUL_MERCHANT_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in responseVO
            //response.setRegistrationId(commonValidatorVO.getToken());
            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setMemberId(directKitResponseVO.getTrackingId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
    }


    public void setSuccessRestCardholderRegistrationResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            response.setCustomerId(directKitResponseVO.getHolder());
            result.setDescription(directKitResponseVO.getStatusMsg());

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("Y"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_MEMBER_SIGNUP);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.MEMBER_SIGNUP_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            //set success element in responseVO
            if (functions.isValueNull(directKitResponseVO.getPartnerId()))
                response.setPartnerId(directKitResponseVO.getPartnerId());
            if (functions.isValueNull(directKitResponseVO.getMemberId()))
                response.setMemberId(directKitResponseVO.getMemberId());

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

        }
    }


    public void setSuccessRestRecurringResponse(Response response, ManualRebillResponseVO manualRebillResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (manualRebillResponseVO != null)
        {
            result = new Result();

            if (manualRebillResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            response.setPaymentId(manualRebillResponseVO.getTrackingId());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
            {
                TransactionDetailsVO transactionDetailsVO = getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, manualRebillResponseVO.getTrackingId(), manualRebillResponseVO.getStatus(), "");
            }
        }
    }

    public void setSuccessRestTokenGenerationResponse(Response response, DirectKitResponseVO directKitResponseVO, TokenRequestVO tokenRequestVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;

        if (directKitResponseVO != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg()); //todo: want to set proper msg from PayOn's Result Code

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setRegistrationId(directKitResponseVO.getToken());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessRestTokenWithAccountResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        Risk risk = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        BankAccount bankAccount = null;
        TokenResponseVO tokenResponseVO = null;
        if (directKitResponseVO != null)
        {
            result = new Result();
            risk = new Risk();
            bankAccount = new BankAccount();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            bankAccount.setHolder(commonValidatorVO.getBankAccountVO().getBIC());
            bankAccount.setIban(commonValidatorVO.getBankAccountVO().getIBAN());
            bankAccount.setBic(commonValidatorVO.getBankAccountVO().getBIC());
            bankAccount.setCountry(commonValidatorVO.getBankAccountVO().getCountry());

            risk.setScore("0");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            response.setPaymentId(directKitResponseVO.getTrackingId());
            if (directKitResponseVO.getToken() != null)
                response.setRegistrationId(directKitResponseVO.getToken());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setBankAccount(bankAccount);
            response.setRisk(risk);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setRestTokneGenerationResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    response.setPaymentId("-");
            }

            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());
                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setSuccessRestTokenGenerationResponse(Response response, TokenResponseVO tokenResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        if (tokenResponseVO != null)
        {
            result = new Result();
            errorCodeVO = new ErrorCodeVO();
            result.setDescription(tokenResponseVO.getStatusMsg());

            if (functions.isValueNull(tokenResponseVO.getStatus()) && tokenResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFUL_TOKEN_GENERATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TOKEN_CREATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            if (functions.isValueNull(tokenResponseVO.getMemberId()))
                response.setMemberId(tokenResponseVO.getMemberId());
            if (functions.isValueNull(tokenResponseVO.getPartnerId()))
                response.setPartnerId(tokenResponseVO.getPartnerId());
            if (functions.isValueNull(tokenResponseVO.getCustomerId()))
                response.setCustomerId(tokenResponseVO.getCustomerId());
            response.setRegistrationId(tokenResponseVO.getRegistrationToken());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setErrorForGetCardAccountsList(Response response, CommonValidatorVO commonValidatorVO, List<RegistrationDetailVO> registrationDetailVOs, ErrorCodeListVO errorCodeListVO)
    {
        if (response != null)
        {
            Result result = new Result();

            if (commonValidatorVO != null && commonValidatorVO.getPartnerDetailsVO() != null && functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
                response.setPartnerId("-");
            if (commonValidatorVO != null && commonValidatorVO.getMerchantDetailsVO() != null && functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                response.setMemberId("-");
            if (commonValidatorVO != null && commonValidatorVO.getMerchantDetailsVO() != null && functions.isValueNull(commonValidatorVO.getCustomerId()))
                response.setHolderId("-");

            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    if (errorCode.length() > 0)
                    {
                        errorCode.append("|");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }
            response.setResult(result);
        }
    }

    public void setCardAccountsListSuccessResponse(Response response, CommonValidatorVO commonValidatorVO, List<RegistrationDetailVO> registrationDetailVOList, ErrorCodeListVO errorCodeListVO)
    {
        if (response != null)
        {
            Result result = new Result();

            if (commonValidatorVO != null)
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                ListOfAccounts listOfAccounts = new ListOfAccounts();
                ListOfCards listOfCards = new ListOfCards();
                Card card = null;
                BankAccount bankAccount = null;

                if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                    response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                else if ("N".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
                    response.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
                response.setCustomerId(commonValidatorVO.getCustomerId());

                for (RegistrationDetailVO registrationDetailVO : registrationDetailVOList)
                {
                    logger.debug("regId::" + registrationDetailVO.getRegistration_token());
                    if (registrationDetailVO.getTokenDetailsVO() != null && !functions.isValueNull(registrationDetailVO.getTokenDetailsVO().getBankAccountId()))
                    {
                        card = new Card();
                        card.setRegistrationId(registrationDetailVO.getRegistration_token());
                        card.setCardType(Functions.getCardType(registrationDetailVO.getCommCardDetailsVO().getCardNum()));
                        card.setBin(registrationDetailVO.getCommCardDetailsVO().getCardNum().substring(0, 6));
                        //card.setLastFourDigits(registrationDetailVO.getCommCardDetailsVO().getCardNum().substring(registrationDetailVO.getCommCardDetailsVO().getCardNum().length() - 4));
                        card.setLast4Digits(registrationDetailVO.getCommCardDetailsVO().getCardNum().substring(registrationDetailVO.getCommCardDetailsVO().getCardNum().length() - 4));
                        card.setExpiryMonth(registrationDetailVO.getCommCardDetailsVO().getExpMonth());
                        card.setExpiryYear(registrationDetailVO.getCommCardDetailsVO().getExpYear());
                        card.setRegistrationStatus(registrationDetailVO.getIsActive());
                        if (!functions.isValueNull(commonValidatorVO.getCustomerId()))
                            card.setCustomerId(registrationDetailVO.getCustomerId());

                        listOfCards.addListOfCards(card);
                    }
                    else if (registrationDetailVO.getReserveField2VO() != null)
                    {
                        bankAccount = new BankAccount();
                        bankAccount.setRegistrationId(registrationDetailVO.getRegistration_token());

                        if (functions.isValueNull(registrationDetailVO.getReserveField2VO().getCardDetailsVO().getIBAN()))
                        {
                            String iban = registrationDetailVO.getReserveField2VO().getCardDetailsVO().getIBAN();
                            iban = iban.substring(iban.length() - 4, iban.length());
                            bankAccount.setIban("******************" + iban);
                        }
                        if (functions.isValueNull(registrationDetailVO.getReserveField2VO().getCardDetailsVO().getBIC()))
                            bankAccount.setBic(registrationDetailVO.getReserveField2VO().getCardDetailsVO().getBIC());
                        if (functions.isValueNull(registrationDetailVO.getReserveField2VO().getAccountNumber()))
                        {
                            String accNum = registrationDetailVO.getReserveField2VO().getAccountNumber();
                            accNum = accNum.substring(accNum.length() - 4, accNum.length());
                            bankAccount.setAccountNumber("********************" + accNum);
                        }
                        if (functions.isValueNull(registrationDetailVO.getReserveField2VO().getAccountType()))
                            bankAccount.setAccountType(registrationDetailVO.getReserveField2VO().getAccountType());
                        if (functions.isValueNull(registrationDetailVO.getReserveField2VO().getRoutingNumber()))
                            bankAccount.setRoutingNumber(registrationDetailVO.getReserveField2VO().getRoutingNumber());
                        bankAccount.setRegistrationStatus(registrationDetailVO.getIsActive());
                        if (!functions.isValueNull(commonValidatorVO.getCustomerId()))
                            bankAccount.setCustomerId(registrationDetailVO.getCustomerId());

                        listOfAccounts.addListOfAccounts(bankAccount);
                    }
                }

                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFUL_REQUEST_PROCESSED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

                if (card != null)
                    response.setListOfCards(listOfCards);
                if (bankAccount != null)
                    response.setListOfAccounts(listOfAccounts);
                response.setResult(result);
            }
        }
    }

    public void setTransactionDetailsresponse(Response response, CommonValidatorVO commonValidatorVO, List<TransactionVO> list) throws PZConstraintViolationException
    {
        if (list != null)
        {


            Transaction transaction = null;
            List<Transaction> transactionList = new ArrayList<Transaction>();
            for (TransactionVO transactionVO : list)
            {

                transaction = new Transaction();

                if (functions.isValueNull(transactionVO.getTransactionStatus()))
                {
                    transaction.setTransactionStatus(transactionVO.getTransactionStatus());
                }
                else
                {
                    transaction.setTransactionStatus("-");
                }

                // invoiceDetails.setStatus(invoiceVO1.getTransactionStatus());
                transaction.setAmount(transactionVO.getAmount());
                transaction.setCount(transactionVO.getCounts());
                transaction.setCurrency(transactionVO.getCurrency());
                transaction.setCaptureamount(transactionVO.getCapAmount());
                transaction.setRefundamount(transactionVO.getRefundAmount());
                transaction.setChargebackamount(transactionVO.getChargebackAmount());
                transaction.setDate(transactionVO.getTimestamp());

                transactionList.add(transaction);

            }
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            response.setTransactionList(transactionList);
        }
    }


    public void setSuccessAuthTokenResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_AUTH_TOKEN_REGENERATED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setAuthToken(directKitResponseVO.getAuthToken());
        }
    }
    public void setSuccessAuthTokenResponse1(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_AUTH_TOKEN_GENERATED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getMerchantDetailsVO().getLogin());
            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                response.setMemberId(directKitResponseVO.getMemberId());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setAuthToken(directKitResponseVO.getAuthToken());
        }
    }


    // Partner Auth Token Response
    public void setSuccessPartnerAuthTokenResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_AUTH_TOKEN_GENERATED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SYS_TOKEN_GENERATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
            response.setLoginName(commonValidatorVO.getPartnerName());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setAuthToken(directKitResponseVO.getAuthToken());
        }
    }


    // QR Checkout success response
    public void setSuccessQRCheckoutResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        logger.error("IN setSuccessQRCheckoutResponse -------");
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        if (response != null)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_REQUEST_PROCESSED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setStatus("Y");
            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setStatus("N");
            }
            logger.error("IN setSuccessWalletAddressResponse ----- tracking id commonValidatorVO----- " + commonValidatorVO.getTrackingid());
            logger.error("IN setSuccessWalletAddressResponse ----- amount commonValidatorVO----- " + commonValidatorVO.getTransDetailsVO().getAmount());
            logger.error("IN setSuccessWalletAddressResponse ----- currency commonValidatorVO----- " + commonValidatorVO.getTransDetailsVO().getCurrency());
            logger.error("IN setSuccessWalletAddressResponse ----- orderid  commonValidatorVO----- " + commonValidatorVO.getTransDetailsVO().getOrderId());
            response.setPaymentId(commonValidatorVO.getTrackingid());
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setMerchantTransactionId(commonValidatorVO.getTransDetailsVO().getOrderId());
            response.setOrderDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            response.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            response.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            response.setPaymentMode(commonValidatorVO.getPaymentMode());
            response.setPaymentBrand(commonValidatorVO.getPaymentBrand());
            response.setEmailaddr(commonValidatorVO.getCustEmail());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));

            response.setResult(result);
        }
    }

    public void setSuccessWalletAddressResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        logger.error("IN setSuccessWalletAddressResponse -------");
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        if (response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if (directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_REQUEST_PROCESSED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                if (functions.isValueNull(directKitResponseVO.getWalletAddress()))
                {
                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_WALLET_CURRENCY);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
            }

            logger.error("IN setSuccessWalletAddressResponse ----- wallet Address ----- " + directKitResponseVO.getWalletAddress());
            logger.error("IN setSuccessWalletAddressResponse ----- tracking id commonValidatorVO----- " + commonValidatorVO.getTrackingid());

            if (functions.isValueNull(directKitResponseVO.getWalletAddress()))
            {
                response.setWalletAddress(directKitResponseVO.getWalletAddress());
            }

            response.setPaymentId(commonValidatorVO.getTrackingid());
            // response.setTimestamp(String.valueOf(dateFormat.format(date)));
            //response.setAuthToken(directKitResponseVO.getAuthToken());
            response.setResult(result);
        }
    }

    //Start Rest Error Response for Transaction
    public void setLoginResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
            StringBuffer errorCode = new StringBuffer();
            StringBuffer errorDesc = new StringBuffer();
            for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
            {
                result = new Result();
                if (errorCode.length() > 0)
                {
                    errorCode.append(" | ");
                    errorDesc.append(" | ");
                }
                errorCode.append(errorCodeVO.getApiCode());
                errorDesc.append(errorCodeVO.getApiDescription());

                result.setResultCode(errorCode.toString());
                result.setDescription(errorDesc.toString());
                logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                response.setResult(result);

            }
        }
        }
    }

    //Start Rest Error Response for Transaction
    public void setPayoutResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }
        }
    }

    public void setExchangerResponseForError(Response response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO, PZResponseStatus status, String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if (response != null)
        {
            if (commonValidatorVO != null)
            {
                response.setPaymentId("-");
            }
            if (errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for (ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if (errorCode.length() > 0)
                    {
                        errorCode.append(" | ");
                        errorDesc.append(" | ");
                    }
                    errorCode.append(errorCodeVO.getApiCode());
                    errorDesc.append(errorCodeVO.getApiDescription());

                    result.setResultCode(errorCode.toString());
                    result.setDescription(errorDesc.toString());
                    logger.debug("error code---" + errorCodeVO.getApiCode() + "-" + errorCodeVO.getApiDescription());
                    response.setResult(result);

                }
            }

        }
    }

    public void setHostedPaymentTransactionResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {

        logger.debug("inside hosted payment write response---");
        Redirect redirect = null;
        Parameters parameters = null;


        if (directKitResponseVO != null)
        {
            redirect = new Redirect();
            logger.debug("Submit URL---" + directKitResponseVO.getBankRedirectionUrl());
            redirect.setUrl(directKitResponseVO.getBankRedirectionUrl());

            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            for (AsyncParameterVO asyncParameterVO : directKitResponseVO.getListOfAsyncParameterVo())
            {
                parameters = new Parameters();
                parameters.setName(asyncParameterVO.getName());
                parameters.setValue(asyncParameterVO.getValue());
                redirect.addListOfParameters(parameters);
            }
            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            response.setRedirect(redirect);

        }
    }

    public void setWhiteLabelPaymentTransactionResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {

        logger.debug("inside white label payment write response---");
        Redirect redirect = null;
        Parameters parameters = null;


        if (directKitResponseVO != null)
        {
            redirect = new Redirect();
            logger.debug("Submit URL---" + directKitResponseVO.getBankRedirectionUrl());
            redirect.setUrl(directKitResponseVO.getBankRedirectionUrl());

            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            for (AsyncParameterVO asyncParameterVO : directKitResponseVO.getListOfAsyncParameterVo())
            {
                parameters = new Parameters();
                parameters.setName(asyncParameterVO.getName());
                parameters.setValue(asyncParameterVO.getValue());
                redirect.addListOfParameters(parameters);
            }
            logger.debug("size of parameters---" + directKitResponseVO.getListOfAsyncParameterVo().size());
            response.setRedirect(redirect);
        }
    }


    public void setFailAuthTokenResponse(Response response)
    {
        Result result           = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if (response != null)
        {
            result = new Result();

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            errorCodeVO = errorCodeUtils.getSystemErrorCode(ErrorName.SYS_AUTHTOKEN_FAILED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public TransactionDetailsVO getTransactionDetails(CommonValidatorVO commonValidatorVO)
    {
        TransactionDetailsVO transactionDetailsVO = new TransactionDetailsVO();
        if (commonValidatorVO != null)
        {
            transactionDetailsVO.setTrackingid(commonValidatorVO.getTrackingid());
            transactionDetailsVO.setAccountId(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            transactionDetailsVO.setPaymodeId(commonValidatorVO.getPaymentType());
            transactionDetailsVO.setCardTypeId(commonValidatorVO.getCardType());
            transactionDetailsVO.setToid(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            transactionDetailsVO.setTotype(commonValidatorVO.getMerchantDetailsVO().getPartnerName());//totype
            transactionDetailsVO.setSecretKey(commonValidatorVO.getMerchantDetailsVO().getKey());
            //transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
            //transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
            transactionDetailsVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
            transactionDetailsVO.setOrderDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
            transactionDetailsVO.setTemplateamount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            transactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            transactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            transactionDetailsVO.setRedirectURL(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
            transactionDetailsVO.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            transactionDetailsVO.setStatus(commonValidatorVO.getStatus());
            transactionDetailsVO.setFirstName(commonValidatorVO.getAddressDetailsVO().getFirstname());//fn
            transactionDetailsVO.setLastName(commonValidatorVO.getAddressDetailsVO().getLastname());//ln
            // transactionDetailsVO.setName(resultSet.getString("name"));//name
            transactionDetailsVO.setCcnum(commonValidatorVO.getCardDetailsVO().getCardNum());//ccnum
            transactionDetailsVO.setExpdate(commonValidatorVO.getCardDetailsVO().getExpMonth() + "/" + commonValidatorVO.getCardDetailsVO().getExpYear());
            //transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
            transactionDetailsVO.setCardtype(commonValidatorVO.getTransDetailsVO().getCardType());//cardtype
            transactionDetailsVO.setEmailaddr(commonValidatorVO.getAddressDetailsVO().getEmail());
            transactionDetailsVO.setIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            transactionDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());//country
            transactionDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());//state
            transactionDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());//city
            transactionDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());//street
            transactionDetailsVO.setZip(commonValidatorVO.getAddressDetailsVO().getZipCode());//zip
            transactionDetailsVO.setTelcc(commonValidatorVO.getAddressDetailsVO().getTelnocc());//telcc
            transactionDetailsVO.setTelno(commonValidatorVO.getAddressDetailsVO().getPhone());//telno
            //transactionDetailsVO.setHttpHeader();//httpheadet
            // transactionDetailsVO.setPaymentId(commonValidatorVO.);
            transactionDetailsVO.setTemplatecurrency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            transactionDetailsVO.setCustomerId(commonValidatorVO.getCustomerId());
            transactionDetailsVO.setEci(commonValidatorVO.getEci());
            transactionDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
            transactionDetailsVO.setBillingDesc(commonValidatorVO.getTransDetailsVO().getBillingDiscriptor());
            transactionDetailsVO.setBankCode(commonValidatorVO.getBankCode());
            transactionDetailsVO.setBankDescription(commonValidatorVO.getBankDescription());

            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl())){
                transactionDetailsVO.setMerchantNotificationUrl(commonValidatorVO.getMerchantDetailsVO().getNotificationUrl());
            }else{
                transactionDetailsVO.setMerchantNotificationUrl("");
            }


        }
        return transactionDetailsVO;
    }

    public void setSuccessInstallmentCountResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = new Result();
        ErrorCodeVO errorCodeVO = null;
        Card card = null;

        if (commonValidatorVO != null && directKitResponseVO != null)
        {
            result = new Result();
            card = new Card();
            response.setRegistrationId(commonValidatorVO.getToken());
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                card.setBin(firstSix);
                card.setLastFourDigits(lastFour);
                card.setLast4Digits(lastFour);
                response.setCard(card);
            }
            response.setInstallment(directKitResponseVO.getEmiPeriod());
        }
        else
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_EMI_SUPPORT);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            response.setResult(result);
        }
    }

    public void setInitiateAuthenticationResponse(Response response, DirectKitResponseVO responseVO, CommonValidatorVO validatorVO)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        response.setInitToken(responseVO.getInitToken());
        response.setPaymentId(responseVO.getTrackingId());
        response.setPaymentType(validatorVO.getTransactionType());
        response.setPaymentMode(validatorVO.getPaymentMode());
        response.setPaymentBrand(validatorVO.getPaymentBrand());
        response.setAmount(validatorVO.getTransDetailsVO().getAmount());
        response.setCurrency(validatorVO.getTransDetailsVO().getCurrency());
        response.setTimestamp(String.valueOf(dateFormat.format(date)));
        response.setMerchantTransactionId(validatorVO.getTransDetailsVO().getOrderId());
        response.setReferenceId(responseVO.getReferenceId());
        response.setRemark(responseVO.getRemark());
    }

    public void setAuthenticateResponse(Response response, DirectKitResponseVO responseVO, CommonValidatorVO validatorVO)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        response.setPaymentType(validatorVO.getTransactionType());
        response.setPaymentMode(validatorVO.getPaymentMode());
        response.setPaymentBrand(validatorVO.getPaymentBrand());
        response.setAmount(validatorVO.getTransDetailsVO().getAmount());
        response.setCurrency(validatorVO.getTransDetailsVO().getCurrency());
        response.setTimestamp(String.valueOf(dateFormat.format(date)));
        response.setMerchantTransactionId(validatorVO.getTransDetailsVO().getOrderId());
        response.setPaymentId(validatorVO.getTrackingid());
        response.setRemark(responseVO.getRemark());
    }


    public void setSuccessRestSendSmsResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        Card card = null;
        //Risk risk = null;
        BankAccount bankAccount = null;
        Parameters parameters = null;
        Redirect redirect = null;

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        if (directKitResponseVO != null)
        {
            card = new Card();
            result = new Result();
            //risk = new Risk();
            bankAccount = new BankAccount();
            redirect = new Redirect();

            logger.error("setSuccessRestSendSmsResponse for response---" + directKitResponseVO.getStatus());
            if ("success".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                }
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("Y");
            }
            else
            {
                if (functions.isValueNull(directKitResponseVO.getErrorName()))
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(directKitResponseVO.getErrorName()));
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
                }

                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setTransactionStatus("N");
            }

            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                logger.debug("Inside WriteDirectTransaction --------------card Details --------------");
                String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                String lastFour = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());

                card.setBin(firstSix);
                card.setLastFourDigits(lastFour);
                // card.setLast4Digits(lastFour);
                card.setHolder(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
                card.setExpiryMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                card.setExpiryYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                response.setCard(card);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            response.setPaymentId(directKitResponseVO.getTrackingId());
            if (directKitResponseVO.getToken() != null)
                response.setRegistrationId(directKitResponseVO.getToken());
            response.setPaymentType(commonValidatorVO.getTransactionType());
            response.setPaymentBrand(GatewayAccountService.getCardType(commonValidatorVO.getTransactionDetailsVO().getCardTypeId()));
            response.setPaymentMode(GatewayAccountService.getPaymentMode(commonValidatorVO.getTransactionDetailsVO().getPaymodeId()));
            response.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
            response.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
            response.setDescriptor(directKitResponseVO.getBillingDescriptor());
            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
            response.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            response.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
            response.setMerchantTransactionId(commonValidatorVO.getTransactionDetailsVO().getDescription());
            response.setRemark(directKitResponseVO.getStatusMsg());
            response.setCustomerId(commonValidatorVO.getCustomerId());
            response.setNotificationUrl(commonValidatorVO.getTransDetailsVO().getNotificationUrl());

            if (commonValidatorVO.getTransDetailsVO() != null)
                commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(directKitResponseVO.getBillingDescriptor());

            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
            {
                TransactionDetailsVO transactionDetailsVO = getTransactionDetails(commonValidatorVO);
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, directKitResponseVO.getTrackingId(), directKitResponseVO.getStatus(), directKitResponseVO.getStatusMsg());
            }
        }
    }

    public void setSuccessRestGetPaymentAndCardTypeResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        Gson gson = new Gson();
        if (directKitResponseVO != null)
        {
            result = new Result();
            if (directKitResponseVO.getPaymentCardTypeMap() != null && !directKitResponseVO.getPaymentCardTypeMap().isEmpty())
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_PAYMENT_CARD_TYPE_REQUEST_PROCESSED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

                String mapJson = gson.toJson(directKitResponseVO.getPaymentCardTypeMap());
                response.setPaymentCardTypeDetails(mapJson);

            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.REJECTED_PAYMENT_CARD_TYPE_REQUEST);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

            response.setResult(result);
        }
    }

    public void setSuccessRestSaveTransactionReceiptResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        if (directKitResponseVO != null)
        {
            result = new Result();
            if ("success".equalsIgnoreCase(directKitResponseVO.getStatus()))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_TRANSACTION_RECEIPT_REQUEST_PROCESSED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.REJECTED_TRANSACTION_RECEIPT_REQUEST);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setResult(result);
        }
    }

    public void setSuccessRestGetTransactionListResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO, List<TransactionVO> transactionList)
    {
        Result result               = null;
        ErrorCodeVO errorCodeVO     = null;
        if (transactionList != null && !transactionList.isEmpty())
        {
            result      = new Result();
            errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_GET_TRANSACTION_REQUEST_PROCESSED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            List<Transaction> list  = new ArrayList<>();
            Transaction transaction = null;
            Customer customer       = null;
            Card card               = null;
            String paymentMode      = "";
            for(TransactionVO transactionVO :transactionList )
            {
                transaction     = new Transaction();
                customer        = new Customer();
                card            = new Card();

                paymentMode = GatewayAccountService.getPaymentMode(transactionVO.getPaymodeid());

                transaction.setPaymentBrand(transactionVO.getPaymentBrand());
                transaction.setPaymentMode(paymentMode);

                transaction.setSystemPaymentId(transactionVO.getPaymentId());
                transaction.setAmount(transactionVO.getAmount());
                transaction.setTransactionStatus(transactionVO.getTransactionStatus());
                transaction.setCaptureamount(transactionVO.getCapAmount());
                transaction.setRefundamount(transactionVO.getRefundAmount());
                transaction.setChargebackamount(transactionVO.getChargebackAmount());
                transaction.setDate(transactionVO.getTimestamp());
                transaction.setTransactionDate(transactionVO.getDtStamp());
                transaction.setMerchantTransactionId(transactionVO.getOrderDesc());
                transaction.setRemark(transactionVO.getRemark());
                transaction.setCurrency(transactionVO.getCurrency());
                transaction.setPayoutamount(transactionVO.getPayoutAmount());
                transaction.setBankReferenceId(transactionVO.getBankReferenceId());
                transaction.setTerminalid(transactionVO.getTerminalId());
                transaction.setTransactionReceiptImg(transactionVO.getTransactionReceiptImg());

                if(functions.isValueNull(transactionVO.getFirstSix()))
                    card.setBin(transactionVO.getFirstSix());
                else
                    card.setBin("");
                if(functions.isValueNull(transactionVO.getLastFour()))
                    card.setLast4Digits(transactionVO.getLastFour());
                else
                    card.setLast4Digits("");

                if(functions.isValueNull(transactionVO.getFirstName()))
                    customer.setGivenName(transactionVO.getFirstName());
                else
                    customer.setGivenName("");

                if(functions.isValueNull((transactionVO.getLastName())))
                    customer.setSurname(transactionVO.getLastName());
                else
                customer.setSurname("");

                if(functions.isValueNull(transactionVO.getTelnocc()))
                    customer.setTelnocc(transactionVO.getTelnocc());
                else
                    customer.setTelnocc("");

                if(functions.isValueNull(transactionVO.getCountry()))
                    customer.setCountry(transactionVO.getCountry());
                else
                    customer.setCountry("");

                if(functions.isValueNull(transactionVO.getCity()))
                    customer.setCity(transactionVO.getCity());
                else
                    customer.setCity("");


                if(functions.isValueNull(transactionVO.getStreet()))
                    customer.setStreet(transactionVO.getStreet());
                else
                    customer.setStreet("");

                if(functions.isValueNull(transactionVO.getTelno()))
                    customer.setPhone(transactionVO.getTelno());
                else
                    customer.setPhone("");

                if(functions.isValueNull(transactionVO.getEmailAddr()))
                    customer.setEmail(transactionVO.getEmailAddr());
                else
                    customer.setEmail("");

                if(functions.isValueNull(transactionVO.getCustomerId())){
                    if(transactionVO.getPaymentBrand().equalsIgnoreCase("UPI"))
                    {
                        customer.setId(functions.maskVpaAddress(transactionVO.getCustomerId()));
                    }
                    else{
                    customer.setId(transactionVO.getCustomerId());
                    }
                }else{
                    customer.setId("");
                }
                transaction.setPaymentBrand(transactionVO.getPaymentBrand());
                transaction.setPaymentMode(paymentMode);

                transaction.setCustomer(customer);
                transaction.setCard(card);
                list.add(transaction);
            }
            response.setResult(result);
            response.setTransactionList(list);
        }
    }



    public void setSuccessResponseQueryDefender(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        TransactionDetailsVO transactionDetailsVO=commonValidatorVO.getTransactionDetailsVO();



        result = new Result();
        if("success".equalsIgnoreCase(directKitResponseVO.getStatus())){
            errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_GET_TRANSACTION_REQUEST_PROCESSED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());

        }

            List<FraudDefender> list = new ArrayList<>();
            Transaction transaction = null;
            Customer customer=null;
            Card card=null;
            ChargebackInfo chargebackInfo=null;
            MerchantInfo merchantInfo=null;
            ProductInfo productInfo=null;
            PurchaseInfo purchaseInfo=null;
            BillingInfo billingInfo=null;
            FraudDefender fraudDefender=null;
            QueryCustomer queryCustomer=null;
            ShippingInfo  shippingInfo=null;
                transaction = new Transaction();
                fraudDefender=new  FraudDefender();
                customer=new Customer();
                card=new Card();
                chargebackInfo=new ChargebackInfo();
                merchantInfo=new MerchantInfo();
                productInfo=new ProductInfo();
                purchaseInfo=new PurchaseInfo();
                billingInfo=new BillingInfo();
                queryCustomer=new QueryCustomer();
                shippingInfo=new ShippingInfo();
                chargebackInfo.setCb_amount(transactionDetailsVO.getAmount());
                chargebackInfo.setCb_arn(transactionDetailsVO.getAmount());
                chargebackInfo.setCb_amount(transactionDetailsVO.getArn());
                chargebackInfo.setCb_case_number(transactionDetailsVO.getCcnum());
                chargebackInfo.setCb_currency(transactionDetailsVO.getCurrency());
                chargebackInfo.setCb_datetime(transactionDetailsVO.getTransactionTime());
                chargebackInfo.setCb_exists(transactionDetailsVO.getEci());
                chargebackInfo.setCb_purchase_identifier(transactionDetailsVO.getName());

                merchantInfo.setAcquirer_bin(transactionDetailsVO.getAction());
                merchantInfo.setAddress("");
                merchantInfo.setBank_mid("");
                merchantInfo.setBank_tid("");
                merchantInfo.setCard_acceptor_id(transactionDetailsVO.getAction());
                merchantInfo.setCity(transactionDetailsVO.getCity());
                merchantInfo.setCountry(transactionDetailsVO.getCountry());
                merchantInfo.setDescriptor(transactionDetailsVO.getBillingDesc());
                merchantInfo.setEmail(transactionDetailsVO.getEmailaddr());
                merchantInfo.setStatus(transactionDetailsVO.getStatus());
                merchantInfo.setIs_test(transactionDetailsVO.getRemark());

                productInfo.setProduct_contact_phone("");
                productInfo.setProduct_cost(transactionDetailsVO.getAmount());
                productInfo.setProduct_name(transactionDetailsVO.getOrderDescription());
                productInfo.setProduct_quantity("");

                purchaseInfo.setBank_order_number(transactionDetailsVO.getPaymentId());
                purchaseInfo.setClient_transaction_id(transactionDetailsVO.getTrackingid());

                billingInfo.setBill_address(transactionDetailsVO.getStreet());
                billingInfo.setBill_city(transactionDetailsVO.getCity());
                billingInfo.setBill_country(transactionDetailsVO.getCountry());
                billingInfo.setBill_first_name(transactionDetailsVO.getFirstName());
                billingInfo.setBill_last_name(transactionDetailsVO.getLastName());
                billingInfo.setBill_zip(transactionDetailsVO.getZip());

                queryCustomer.setEmail(transactionDetailsVO.getEmailaddr());
                queryCustomer.setIp(transactionDetailsVO.getIpAddress());
                queryCustomer.setPhone(transactionDetailsVO.getTelcc());

                shippingInfo.setDelivery_date("");
                shippingInfo.setShip_address("");
                shippingInfo.setShip_city("");
                shippingInfo.setShip_country("");
                shippingInfo.setShip_first_name("");
                shippingInfo.setShip_last_name("");
                shippingInfo.setShip_region("");
                shippingInfo.setShip_zip("");
                shippingInfo.setShipped_date("");
                shippingInfo.setShipping_amount("");
                shippingInfo.setShipping_type("");
                shippingInfo.setTracking_number("");





                if(functions.isValueNull(transactionDetailsVO.getFirstSix()))
                    card.setBin(transactionDetailsVO.getFirstSix());
                else
                    card.setBin("");
                if(functions.isValueNull(transactionDetailsVO.getLastFour()))
                    card.setLast4Digits(transactionDetailsVO.getLastFour());
                else
                    card.setLast4Digits("");


        fraudDefender.setChargebackInfo(chargebackInfo);
        fraudDefender.setMerchantInfo(merchantInfo);
        fraudDefender.setProductInfo(productInfo);
        fraudDefender.setPurchaseInfo(purchaseInfo);
        fraudDefender.setBillingInfo(billingInfo);

        fraudDefender.setShippingInfo(shippingInfo);
                list.add(fraudDefender);

            response.setResult(result);
            response.setFrauddefenderlist(list);

    }



    public void setCardWhitelistingResponse(Response response,CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        List cardList=new ArrayList<>();
        if (commonValidatorVO != null)
        {
            result = new Result();
            errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_REQUEST_PROCESSED);
            result.setResultCode(errorCodeVO.getApiCode());
            result.setDescription(errorCodeVO.getApiDescription());
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            response.setCustomerAccount(commonValidatorVO.getAccountId());
            response.setCustomerId(commonValidatorVO.getCustomerId());
            response.setEmailaddr(commonValidatorVO.getAddressDetailsVO().getEmail());
            /*HashMap<String,com.transaction.vo.restVO.RequestVO.CardVO> cardVOHashMap=commonValidatorVO.getCardVOHashMap();
            Iterator<Map.Entry<String, com.transaction.vo.restVO.RequestVO.CardVO>> map = cardVOHashMap.entrySet().iterator();
            while (map.hasNext()) {
                Map.Entry<String,com.transaction.vo.restVO.RequestVO.CardVO> entry=map.next();
                com.transaction.vo.restVO.RequestVO.CardVO cardVO=cardVOHashMap.get(entry.getKey());
                cardVO.getFirstsix();
                cardVO.getLastfour();
                cardList.add(cardVO);
            }*/
            response.setResult(result);
            response.setTransactionList(cardList);
        }
    }
    public void setSuccessRestGetDailySalesReportResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = null;
        Gson gson = new Gson();
        if (directKitResponseVO != null)
        {
            result = new Result();
            if (directKitResponseVO.getDailySalesReport() != null && !directKitResponseVO.getDailySalesReport().isEmpty())
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_RECORD_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());

                String mapJson = gson.toJson(directKitResponseVO.getDailySalesReport());
                response.setDailySalesReport(mapJson);
            }else
            {
                errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.REJECTED_RECORD_NOT_FOUND);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            response.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

            response.setResult(result);
        }
    }

    public void setSuccessOTPGeneratedResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Customer customer = new Customer();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.setCustomer(customer);
                response.getCustomer().setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                response.getCustomer().setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
                response.getCustomer().setIsEmailVerified(commonValidatorVO.getIsEmailVerified());
                response.getCustomer().setIsMobileNoVerified(commonValidatorVO.getIsMobileNoVerified());
            }
            else if(directKitResponseVO.getStatus().equalsIgnoreCase("limitexceed")){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_MAX_OTP_SEND_LIMIT);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.REJECTED_OTP_CREATION);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccessOTVerificationResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Customer customer = new Customer();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
                response.setCustomer(customer);
                //System.out.println("API code--"+errorCodeVO.getApiCode());
                errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_VERIFICATION);
                //System.out.println("Api code---->"+errorCodeVO.getApiCode() );
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
                response.getCustomer().setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
                response.getCustomer().setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());

            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.OTP_VERIFICATION_FAILED);
                result.setResultCode(errorCodeVO.getApiCode());
                result.setDescription(errorCodeVO.getApiDescription());
            }



            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }

    public void setSuccesspayoutBalanceResponse(Response response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Customer customer = new Customer();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
            {
              //  errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.SUCCESSFUL_OTP_CREATION);
                result.setResultCode("200");
                result.setTotalBalance(directKitResponseVO.getCaptureAmount());
                result.setCurrentPayoutBalance(directKitResponseVO.getAmount());

            }

            else
            {

                result.setResultCode("400");
                result.setDescription("Not Found!");
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }


    public void setSuccessUpiTxnResponse(Response response,boolean res, String error)
    {
        Result result = new Result();
        if(response != null)
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date             = new Date();

            if(res)
            {
                result.setResultCode("200");
                result.setDescription("Record added Successfully");
            }
            else if (functions.isValueNull(error))
            {
                result.setDescription(error);
            }
            else
            {
                result.setResultCode("400");
                result.setDescription("Failed to add Record!");
            }

            response.setResult(result);
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }
}