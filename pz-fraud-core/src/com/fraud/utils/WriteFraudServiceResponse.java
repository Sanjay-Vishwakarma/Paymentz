package com.fraud.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.fraud.vo.*;
import com.manager.vo.DirectKitResponseVO;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 11/8/14
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class WriteFraudServiceResponse
{
    private ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
    private Functions functions =new Functions();
    private Logger logger= new Logger(WriteFraudServiceResponse.class.getName());

    public void setFraudResponseParamenters(PZFraudCustRegResponseVO pzFraudCustRegResponseVO, RestFraudResponse restFraudResponse){

        restFraudResponse.setCustomerRegId(pzFraudCustRegResponseVO.getCustomerRegId());
        restFraudResponse.setDescription(pzFraudCustRegResponseVO.getDescription());
        restFraudResponse.setScore(pzFraudCustRegResponseVO.getScore());
        restFraudResponse.setRecommendation(pzFraudCustRegResponseVO.getRecommendation());
        restFraudResponse.setStatus(pzFraudCustRegResponseVO.getStatus());
        restFraudResponse.setErrorList(pzFraudCustRegResponseVO.getErrorList());
        restFraudResponse.setResponseCode(pzFraudCustRegResponseVO.getResponseCode());
        restFraudResponse.setCust_request_id(pzFraudCustRegResponseVO.getCust_request_id());
        restFraudResponse.setResult(pzFraudCustRegResponseVO.getResult());
        restFraudResponse.setFraud_result(pzFraudCustRegResponseVO.getFraud_result());
    }

    public void setFraudResponseNewTransaction(PZFraudResponseVO pzFraudResponseVO, RestFraudResponse restFraudResponse)
    {
        restFraudResponse.setDescription(pzFraudResponseVO.getDescription());
        restFraudResponse.setStatus(pzFraudResponseVO.getStatus());
        restFraudResponse.setResponseCode(pzFraudResponseVO.getResponseCode());
        restFraudResponse.setFraud_result(pzFraudResponseVO.getFraud_result());
        restFraudResponse.setResult(pzFraudResponseVO.getResult());
    }

    public void setFraudDocVerifyRespParameters(PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO, RestFraudResponse restFraudDocVerifyResponse){

        restFraudDocVerifyResponse.setStatus(pzFraudDocVerifyResponseVO.getStatus());
        //  restFraudDocVerifyResponse.setStatus1(pzFraudDocVerifyResponseVO.getStatus1());
        //  restFraudDocVerifyResponse.setStatus2(pzFraudDocVerifyResponseVO.getStatus2());
        restFraudDocVerifyResponse.setReference_id(pzFraudDocVerifyResponseVO.getReference_id());
        restFraudDocVerifyResponse.setReference_id2(pzFraudDocVerifyResponseVO.getReference_id2());
        restFraudDocVerifyResponse.setDescription(pzFraudDocVerifyResponseVO.getDescription());
        restFraudDocVerifyResponse.setDescription1(pzFraudDocVerifyResponseVO.getDescription1());
        restFraudDocVerifyResponse.setDescription2(pzFraudDocVerifyResponseVO.getDescription2());
        restFraudDocVerifyResponse.setKyc_source(pzFraudDocVerifyResponseVO.getKyc_source());
        restFraudDocVerifyResponse.setKyc_source2(pzFraudDocVerifyResponseVO.getKyc_source2());
        restFraudDocVerifyResponse.setResponse(pzFraudDocVerifyResponseVO.getResponse());
        restFraudDocVerifyResponse.setResponseCode(pzFraudDocVerifyResponseVO.getResponseCode());
        restFraudDocVerifyResponse.setErrorList(pzFraudDocVerifyResponseVO.getErrorList());
        restFraudDocVerifyResponse.setResult(pzFraudDocVerifyResponseVO.getResult());

    }

    public void setSuccessAuthTokenResponse(RestFraudResponse response, DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
        {
            result = new Result();
            result.setDescription(directKitResponseVO.getStatusMsg());
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();

            if(directKitResponseVO.getStatus().equalsIgnoreCase("success"))
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

            response.setResult(result);
            response.setAuthToken(directKitResponseVO.getAuthToken());
            response.setTimestamp(String.valueOf(dateFormat.format(date)));
        }
    }
    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
        if(errorCodeVO!=null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public void setLoginResponseForError(RestFraudResponse response, CommonValidatorVO commonValidatorVO, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark)
    {
        logger.debug("Inside Write error---");
        Result result = null;
        if(response != null)
        {

            if(errorCodeListVO != null && errorCodeListVO.getListOfError() != null)
            {
                StringBuffer errorCode = new StringBuffer();
                StringBuffer errorDesc = new StringBuffer();
                for(ErrorCodeVO errorCodeVO : errorCodeListVO.getListOfError())
                {
                    result = new Result();
                    if(errorCode.length() > 0)
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

    public void setFailAuthTokenResponse(RestFraudResponse response)
    {
        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        if(response != null)
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

    public void setRestFraudResponseForError(RestFraudResponse response,List errorList)
    {
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        Result result = new Result();
        {
            result.setResultCode("-1");
            //result.setStatus("failed");
            result.setDescription("Internal error occured. Please try again");
            result.setErrorList(errorList);
            response.setResult(result);
            response.setRecommendation("Failed");
        }

    }

    public void setRestFraudResponseForDocError(RestFraudResponse response,List errorList)
    {
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        Result result = new Result();
        {
            result.setResultCode("-1");
            //result.setStatus("failed");
            result.setDescription("Internal error occured. Please try again");
            result.setErrorList(errorList);
            response.setResult(result);

        }

    }

    public void setRestFraudCustRegResponseForError(RestFraudResponse response, ErrorCodeListVO errorCodeListVO,PZResponseStatus status,String remark){

    }



}
