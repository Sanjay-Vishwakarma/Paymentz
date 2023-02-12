package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 9:35:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiResponseVO  extends GenericResponseVO
{

       private String operation;
       private String resultCode;

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    private String errorCode;
    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getResultCode()
    {
        return resultCode;
    }

    public String getResultCode(String arsResultCode)
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getMerNo()
    {
        return merNo;
    }

    public void setMerNo(String merNo)
    {
        this.merNo = merNo;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getDateTime()
    {
        return dateTime;
    }

    public void setDateTime(String dateTime)
    {
        this.dateTime = dateTime;
    }

    public String getPaymentOrderNo()
    {
        return paymentOrderNo;
    }

    public void setPaymentOrderNo(String paymentOrderNo)
    {
        this.paymentOrderNo = paymentOrderNo;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getMd5Info()
    {
        return md5Info;
    }

    public void setMd5Info(String md5Info)
    {
        this.md5Info = md5Info;
    }

    public String getBillingDescriptor()
    {
        return billingDescriptor;
    }

    public void setBillingDescriptor(String billingDescriptor)
    {
        this.billingDescriptor = billingDescriptor;
    }

    private String merNo;
    private String billNo;
    private String amount;
    private String currency;
    private String dateTime;
    private String paymentOrderNo;
    private String remark;
    private String md5Info;
    private String billingDescriptor;

    public QwipiResponseVO(String operation, String resultCode, String merNo, String billNo,String amount, String currency, String dateTime, String paymentOrderNo,String remark, String md5Info, String billingDescriptor,String result,String code,String text,String Order,String id,String status,String refundCode,String refundText,String refundAmount,String refundDate,String refundRemark,String refundMessage,String cbCode,String cbText,String stCode,String stText)
    {
        this.operation = operation;
        this.resultCode = resultCode;
        this.merNo = merNo;
        this.billNo = billNo;
        this.amount = amount;
        this.currency = currency;
        this.dateTime = dateTime;
        this.paymentOrderNo = paymentOrderNo;
        this.remark = remark;
        this.md5Info = md5Info;
        this.billingDescriptor = billingDescriptor;
        this.result = result;
        this.code = code;
        this.text = text;
        this.Order = Order;
        this.id = id;
        this.status = status;

        this.refundCode = refundCode;
        this.refundText = refundText;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
        this.refundRemark = refundRemark;
        this.refundMessage = refundMessage;
        this.cbCode = cbCode;
        this.cbText = cbText;
        this.stCode = stCode;
        this.stText = stText;

    }
    public QwipiResponseVO()
    {
        /*this.operation = "";
        this.resultCode = "";
        this.merNo = "";
        this.billNo = "";
        this.amount = "";
        this.currency = "";
        this.dateTime = "";
        this.paymentOrderNo = "";
        this.remark = "";
        this.md5Info = "";
        this.billingDescriptor = "";
        this.result = "";
        this.code = "";
        this.text = "";
        this.Order = "";
        this.id = "";
        this.status = "";

        this.refundCode = "";
        this.refundText = "";
        this.refundAmount = "";
        this.refundDate = "";
        this.refundRemark = "";
        this.refundMessage = "";
        this.cbCode = "";
        this.cbText = "";
        this.stCode = "";
        this.stText = "";*/

    }

    private String result;
    private String code;
    private String text;
    private String Order;
    private String id;
    private String status;

    public String getRefundCode()
    {
        return refundCode;
    }

    public void setRefundCode(String refundCode)
    {
        this.refundCode = refundCode;
    }

    public String getRefundText()
    {
        return refundText;
    }

    public void setRefundText(String refundText)
    {
        this.refundText = refundText;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getRefundDate()
    {
        return refundDate;
    }

    public void setRefundDate(String refundDate)
    {
        this.refundDate = refundDate;
    }

    public String getRefundRemark()
    {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark)
    {
        this.refundRemark = refundRemark;
    }

    public String getRefundMessage()
    {
        return refundMessage;
    }

    public void setRefundMessage(String refundMessage)
    {
        this.refundMessage = refundMessage;
    }

    public String getCbCode()
    {
        return cbCode;
    }

    public void setCbCode(String cbCode)
    {
        this.cbCode = cbCode;
    }

    public String getCbText()
    {
        return cbText;
    }

    public void setCbText(String cbText)
    {
        this.cbText = cbText;
    }

    public String getStCode()
    {
        return stCode;
    }

    public void setStCode(String stCode)
    {
        this.stCode = stCode;
    }

    public String getStText()
    {
        return stText;
    }

    public void setStText(String stText)
    {
        this.stText = stText;
    }

    private String refundCode;
    private String refundText;
    private String refundAmount;
    private String refundDate;
    private String refundRemark;
    private String refundMessage;
    private String cbCode;
    private String cbText;
    private String stCode;
    private String stText;

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getOrder()
    {
        return Order;
    }

    public void setOrder(String order)
    {
        Order = order;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }







}
