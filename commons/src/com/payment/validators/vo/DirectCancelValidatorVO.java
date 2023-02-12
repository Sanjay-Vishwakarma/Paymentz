package com.payment.validators.vo;

import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

/**
 * Created by ThinkPadT410 on 2/18/2016.
 */
public class DirectCancelValidatorVO extends CommonValidatorVO
{
    @Override
    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    @Override
    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader(String header)
    {
        this.header = header;
    }

    @Override
    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    @Override
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

    public String getMerchantIpAddress()
    {
        return merchantIpAddress;
    }

    public void setMerchantIpAddress(String merchantIpAddress)
    {
        this.merchantIpAddress = merchantIpAddress;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    MerchantDetailsVO merchantDetailsVO;

    String header;

    ErrorCodeListVO errorCodeListVO;

    String errorMessage;

    String merchantIpAddress;

    String trackingId;



}
