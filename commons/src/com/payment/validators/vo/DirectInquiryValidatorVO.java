package com.payment.validators.vo;

import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created by Admin on 23/6/15.
 */
public class DirectInquiryValidatorVO extends CommonValidatorVO
{
    MerchantDetailsVO merchantDetailsVO ;

    String checkSum;

    String trackingId;

    String description;

    String merchantIpAddress;

    String header;

    String authAmount;

    String captureAmount;

    String generatedCheckSum;

    ErrorCodeListVO errorCodeListVO;

    String errorMessage;

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public String getCheckSum()
    {
        return checkSum;
    }

    public void setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMerchantIpAddress()
    {
        return merchantIpAddress;
    }

    public void setMerchantIpAddress(String merchantIpAddress)
    {
        this.merchantIpAddress = merchantIpAddress;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }


    public String getAuthAmount()
    {
        return authAmount;
    }

    public void setAuthAmount(String authAmount)
    {
        this.authAmount = authAmount;
    }

    public String getCaptureAmount()
    {
        return captureAmount;
    }

    public void setCaptureAmount(String captureAmount)
    {
        this.captureAmount = captureAmount;
    }

    public String getGeneratedCheckSum()
    {
        return generatedCheckSum;
    }

    public void setGeneratedCheckSum(String generatedCheckSum)
    {
        this.generatedCheckSum = generatedCheckSum;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
