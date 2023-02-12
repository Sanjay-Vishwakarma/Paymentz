package com.manager.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TokenResponseVO;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 6/3/15
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionUtil
{
    private static Logger logger =new Logger(TransactionUtil.class.getName());
    private static TransactionLogger transactionLogger =new TransactionLogger(TransactionUtil.class.getName());

    private ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
    private Functions functions = new Functions();

    /**
     * This is to get SystemError Code from error name and specified Reason
     * @param errorName
     * @param reason
     * @return
     */
    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
        if(errorCodeVO!=null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    /**
     * This is to make System Response according to the following input(USED now for WebService)
     * @param directKitResponseVO
     * @param errorCodeListVO
     * @param errorName
     * @param commonValidatorVO
     * @param status
     * @param remark
     * @param billingDescriptor
     */
    public void setSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,CommonValidatorVO commonValidatorVO,PZResponseStatus status,String remark,String billingDescriptor)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(commonValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                if(functions.isValueNull(commonValidatorVO.getToken()))
                    directKitResponseVO.setToken(commonValidatorVO.getToken());
                if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                    directKitResponseVO.setSplitTransactionId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                else
                    directKitResponseVO.setSplitTransactionId(commonValidatorVO.getTerminalId());

                if(commonValidatorVO.getTransDetailsVO()!=null)
                {
                    if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                        directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                    if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                        directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                }
                if(commonValidatorVO.getMerchantDetailsVO()!=null)
                {
                    if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getFlightMode()))
                        directKitResponseVO.setFlightMode(commonValidatorVO.getMerchantDetailsVO().getFlightMode());
                }
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(status!=null)
            directKitResponseVO.setStatus(status.name());
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);
        if(functions.isValueNull(billingDescriptor))
            directKitResponseVO.setBillingDescriptor(billingDescriptor);

    }

    public void setRefundSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,DirectRefundValidatorVO directRefundValidatorVO,PZResponseStatus status,String remark)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(directRefundValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(directRefundValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(directRefundValidatorVO.getTrackingid());
                if(functions.isValueNull(directRefundValidatorVO.getRefundAmount()))
                    directKitResponseVO.setAmount(directRefundValidatorVO.getRefundAmount());
                if(functions.isValueNull(directRefundValidatorVO.getGeneratedCheckSum()))
                    directKitResponseVO.setGeneratedCheckSum(directRefundValidatorVO.getGeneratedCheckSum());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(status!=null)
            directKitResponseVO.setStatus(status.name());
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);


    }

    public void setInquirySystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,DirectInquiryValidatorVO directInquiryValidatorVO,String status,String remark, String statusDescription)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(directInquiryValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(directInquiryValidatorVO.getTrackingId()))
                    directKitResponseVO.setTrackingId(directInquiryValidatorVO.getTrackingId());
                if(functions.isValueNull(directInquiryValidatorVO.getDescription()))
                    directKitResponseVO.setDescription(directInquiryValidatorVO.getDescription());
                if(functions.isValueNull(directInquiryValidatorVO.getAuthAmount()))
                    directKitResponseVO.setAmount(directInquiryValidatorVO.getAuthAmount());
                if(functions.isValueNull(directInquiryValidatorVO.getCaptureAmount()))
                    directKitResponseVO.setCaptureAmount(directInquiryValidatorVO.getCaptureAmount());
                if(functions.isValueNull(directInquiryValidatorVO.getGeneratedCheckSum()))
                    directKitResponseVO.setGeneratedCheckSum(directInquiryValidatorVO.getGeneratedCheckSum());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
                if(functions.isValueNull(directInquiryValidatorVO.getGeneratedCheckSum()))
                    directKitResponseVO.setGeneratedCheckSum(directInquiryValidatorVO.getGeneratedCheckSum());
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);

        if (functions.isValueNull(statusDescription))
            directKitResponseVO.setRemark(statusDescription);
    }

    public DirectKitResponseVO setInquirySystemResponseAndErrorCodeListVO(ErrorCodeListVO errorCodeListVO,ErrorName errorName,CommonValidatorVO commonValidatorVO,String status,String remark)
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(commonValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc()))
                    directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                    directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                /*if(functions.isValueNull(commonValidatorVO.getCaptureAmount()))
                    directKitResponseVO.setCaptureAmount(commonValidatorVO.getCaptureAmount());*/
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getChecksum()))
                    directKitResponseVO.setGeneratedCheckSum(commonValidatorVO.getTransDetailsVO().getChecksum());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);

        return directKitResponseVO;

    }

    public DirectKitResponseVO setInquirySystemResponseAndErrorCodeListVOForRest(ErrorCodeListVO errorCodeListVO,ErrorName errorName,CommonValidatorVO commonValidatorVO,String status,String remark)
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(commonValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc()))
                    directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                    directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                /*if(functions.isValueNull(commonValidatorVO.getCaptureAmount()))
                    directKitResponseVO.setCaptureAmount(commonValidatorVO.getCaptureAmount());*/
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getChecksum()))
                    directKitResponseVO.setGeneratedCheckSum(commonValidatorVO.getTransDetailsVO().getChecksum());
                if(functions.isValueNull(commonValidatorVO.getPaymentBrand()))
                    directKitResponseVO.setPaymentBrand(commonValidatorVO.getPaymentBrand());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);

        return directKitResponseVO;

    }

    public DirectKitResponseVO setInquirySystemResponseAndErrorCodeListVOForRest(ErrorCodeListVO errorCodeListVO,ErrorName errorName,CommonValidatorVO commonValidatorVO,String status,String statusMsg,String remark)
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(commonValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderDesc()))
                    directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderDesc());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                    directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRefundAmount()))
                    directKitResponseVO.setRefundAmount(commonValidatorVO.getTransDetailsVO().getRefundAmount());
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPayoutAmount()))
                    directKitResponseVO.setPayoutAmount(commonValidatorVO.getTransDetailsVO().getPayoutAmount());
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    directKitResponseVO.setDescription(commonValidatorVO.getTransDetailsVO().getOrderId());
                /*if(functions.isValueNull(commonValidatorVO.getCaptureAmount()))
                    directKitResponseVO.setCaptureAmount(commonValidatorVO.getCaptureAmount());*/
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getChecksum()))
                    directKitResponseVO.setGeneratedCheckSum(commonValidatorVO.getTransDetailsVO().getChecksum());
                if(functions.isValueNull(commonValidatorVO.getPaymentBrand()))
                    directKitResponseVO.setPaymentBrand(commonValidatorVO.getPaymentBrand());
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                    directKitResponseVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                    directKitResponseVO.setCustId(commonValidatorVO.getCustomerId());
                if (functions.isValueNull(commonValidatorVO.getCustomerBankId()))
                    directKitResponseVO.setCustBankId(commonValidatorVO.getCustomerBankId());
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()))
                    directKitResponseVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                if (functions.isValueNull(commonValidatorVO.getErrorName()))
                    directKitResponseVO.setErrorName(commonValidatorVO.getErrorName());
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);
        if (functions.isValueNull(statusMsg))
            directKitResponseVO.setRemark(statusMsg);

            return directKitResponseVO;

    }


    public void setInquirySystemResponseAndErrorCodeListVOforSplit(List<DirectKitResponseVO> listofDirectKitResponseVO,DirectInquiryValidatorVO directInquiryValidatorVO,List<TransactionVO> listOfTransactionVO,String remark,String status)
    {


        if(directInquiryValidatorVO!=null)
        {
            if(listOfTransactionVO.size()>0)
            {
            for(TransactionVO transactionVO : listOfTransactionVO)
            {
                DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
                directKitResponseVO.setTrackingId(transactionVO.getTrackingId());
                directKitResponseVO.setAmount(transactionVO.getAmount());
                directKitResponseVO.setDescription(transactionVO.getOrderId());
                directKitResponseVO.setStatus(transactionVO.getStatus());
                if(functions.isValueNull(status))
                    directKitResponseVO.setStatus(status);
                if(functions.isValueNull(remark))
                    directKitResponseVO.setStatusMsg(remark);
                listofDirectKitResponseVO.add(directKitResponseVO);
            }
            }
            else
            {
                DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
                directKitResponseVO.setTrackingId(directInquiryValidatorVO.getTrackingId());
                directKitResponseVO.setAmount(directInquiryValidatorVO.getTransDetailsVO().getAmount());
                directKitResponseVO.setDescription(directInquiryValidatorVO.getTransDetailsVO().getOrderId());
                if(functions.isValueNull(status))
                    directKitResponseVO.setStatus(status);
                if(functions.isValueNull(remark))
                    directKitResponseVO.setStatusMsg(remark);
                listofDirectKitResponseVO.add(directKitResponseVO);
            }


        }


    }

    public void setCaptureSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,DirectCaptureValidatorVO directCaptureValidatorVO,String status,String remark)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(directCaptureValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                if(functions.isValueNull(directCaptureValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(directCaptureValidatorVO.getTrackingid());
                if(functions.isValueNull(directCaptureValidatorVO.getCaptureAmount()))
                    directKitResponseVO.setCaptureAmount(directCaptureValidatorVO.getCaptureAmount());
                if(functions.isValueNull(directCaptureValidatorVO.getGeneratedCheckSum()))
                    directKitResponseVO.setGeneratedCheckSum(directCaptureValidatorVO.getGeneratedCheckSum());
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);

    }

    public void setCancelSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,CommonValidatorVO commonValidatorVO,String status,String remark)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(commonValidatorVO!=null)
        {
            if(directKitResponseVO!=null)
            {
                /*if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getOrderId()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTransDetailsVO().getOrderId());*/
                if(functions.isValueNull(commonValidatorVO.getTrackingid()))
                    directKitResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                /*if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getChecksum()))
                    directKitResponseVO.setGeneratedCheckSum(commonValidatorVO.getTransDetailsVO().getChecksum());*/
                if(errorCodeListVO!=null && !errorCodeListVO.getListOfError().isEmpty())
                    directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);

    }

    public void setTokenSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,TokenResponseVO tokenResponseVO,String status,String remark)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(tokenResponseVO != null)
        {
            if(directKitResponseVO != null)
            {
                if(functions.isValueNull(tokenResponseVO.getToken()))
                    directKitResponseVO.setToken(tokenResponseVO.getToken());
                if(functions.isValueNull(String.valueOf(tokenResponseVO.getValidDays())))
                    directKitResponseVO.setTokenValidDays(tokenResponseVO.getValidDays());
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);
    }

    /**
     * this is to generate checksum
     * @param memberid
     * @param trackingid
     * @param refundamount
     * @param key
     * @return
     * @throws PZTechnicalViolationException
     */
    public String generateMD5ChecksumForRefund(String memberid, String trackingid, String refundamount, String key) throws PZTechnicalViolationException
    {
        String generatedCheckSum = "";
        try
        {
            String str = memberid + "|" + trackingid + "|" + refundamount + "|" + key;
            logger.debug("str refund gen---" + str);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException nsa)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TransactionUtil.class.getName(), "generateMD5ChecksumForRefund()", null, "common", "No such Algoritham Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, nsa.getMessage(), nsa.getCause());
        }
        return generatedCheckSum;
    }

    public String generateStatusChecksum(String toid,String description,String trackingid,String key,String flightMode) throws PZTechnicalViolationException
    {
        String str = "";
        if("N".equalsIgnoreCase(flightMode))
        {
            str = toid + "|" + description + "|" + trackingid + "|" + key;
        }
        else
        {
            str = toid + "|" + description + "|" + trackingid;
        }
        String generatedCheckSum=null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TransactionUtil.class.getName(), "generateStatusChecksum()", null, "common", "No such Algoritham Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return generatedCheckSum;
    }

    public String generateCaptureChecksum(String toid,String trackingid,String captureAmount,String key) throws PZTechnicalViolationException
    {
        String str=toid+"|"+trackingid+"|"+captureAmount+"|"+key;
        String generatedCheckSum=null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TransactionUtil.class.getName(), "generateStatusChecksum()", null, "common", "No such Algoritham Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return generatedCheckSum;
    }

    public String generateCancelChecksum(String toid,String trackingid,String description,String key) throws PZTechnicalViolationException
    {

        String str = toid + "|" + trackingid+ "|" + description  + "|" + key;

        String generatedCheckSum=null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            generatedCheckSum = getString(messageDigest.digest(str.getBytes()));
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(TransactionUtil.class.getName(), "generateStatusChecksum()", null, "common", "No such Algoritham Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return generatedCheckSum;
    }

    private String getString(byte buf[])
    {
        StringBuffer sb = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++)
        {
            int h = (buf[i] & 0xf0) >> 4;
            int l = (buf[i] & 0x0f);
            sb.append(new Character((char) ((h > 9) ? 'a' + h - 10 : '0' + h)));
            sb.append(new Character((char) ((l > 9) ? 'a' + l - 10 : '0' + l)));
        }
        return sb.toString();
    }

    /*public void setTokenSystemResponseAndErrorCodeListVO(DirectKitResponseVO directKitResponseVO,ErrorCodeListVO errorCodeListVO,ErrorName errorName,TokenResponseVO tokenResponseVO,String status,String remark)
    {
        if(errorCodeListVO!=null)
        {
            if(errorName!=null)
            {
                ErrorCodeVO errorCodeVO=null;
                errorCodeVO=errorCodeUtils.getErrorCodeFromName(errorName);
                if(errorCodeVO!=null)
                    errorCodeVO.setErrorReason(remark);
                errorCodeListVO.addListOfError(errorCodeVO);
            }
        }
        if(tokenResponseVO != null)
        {
            if(directKitResponseVO != null)
            {
                if(functions.isValueNull(tokenResponseVO.getToken()))
                    directKitResponseVO.setToken(tokenResponseVO.getToken());
                if(functions.isValueNull(String.valueOf(tokenResponseVO.getValidDays())))
                    directKitResponseVO.setTokenValidDays(tokenResponseVO.getValidDays());
            }
        }
        if(functions.isValueNull(status))
            directKitResponseVO.setStatus(status);
        if(functions.isValueNull(remark))
            directKitResponseVO.setStatusMsg(remark);
    }*/
}