package com.payment.validators.vo;

import com.manager.vo.MerchantDetailsVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.List;

/**
 * Created by Admin on 17/6/15.
 */
public class DirectRefundValidatorVO extends CommonValidatorVO
{
    private MerchantDetailsVO merchantDetailsVO;

    private ErrorCodeListVO errorCodeListVO ;

    //String trackingId;

    String refundAmount;

    String refundReason;

    String checkSum;

    String generatedCheckSum;

    String merchantIpAddress;

    String header;

    String errorMessage;

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    /*public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }*/

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    public String getCheckSum()
    {
        return checkSum;
    }

    public void setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
    }

    public String getGeneratedCheckSum()
    {
        return generatedCheckSum;
    }

    public void setGeneratedCheckSum(String generatedCheckSum)
    {
        this.generatedCheckSum = generatedCheckSum;
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

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }
}
