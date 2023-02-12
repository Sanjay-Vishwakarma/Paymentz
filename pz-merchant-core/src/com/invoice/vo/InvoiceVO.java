package com.invoice.vo;

import com.directi.pg.MerchantTerminalVo;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.List;

/**
 * Created by Jinesh on 5/18/2015.
 */
public class InvoiceVO
{
    private String invoiceno;
    private String invoiceAction;
    private String checksum;
    private String memberid;
    private String timestamp;
    private String amount;
    private String refundAmount;
    private String description;
    private String orderDescription;
    private String email;
    private String redirecturl;
    private String currency;
    private String country;
    private String city;
    private String state;
    private String zip;
    private String telCc;
    private String telno;
    private String street;
    private String status;
    private String ctoken;
    private String accountid;
    private String reminderCounter;
    private String cancelReason;
    private String custName;
    private String paymodeid;
    private String cardTypeId;
    private String isCredential;
    private String userName;
    private String pwd;
    private String question;
    private String answer;
    private String merchantIpAddress;
    private String terminalid;
    private String remark;
    private String expirationPeriod;
    private String date;
    private String time;
    private String errorMsg;
    private PartnerDetailsVO partnerDetailsVO;
    private MerchantDetailsVO merchantDetailsVO;
    private MerchantTerminalVo merchantTerminalVo;
    private ErrorCodeListVO errorCodeListVO ;
    private GenericTransDetailsVO transDetailsVO;
    private PaginationVO paginationVO;
    private String raisedby;
    private String paymentBrand;
    private String paymentMode;
    private String transactionUrl;
    private String transactionStatus;
    private String trackingId;
    private String dtstamp;
    private String initial;
    private String orderOffset;
    private String Issms;
    private String Isemail;
    private String Isapp;
    private String paymentterms;
    private String smsactivation;
    private String isduedate;
    private String duedate;
    private String islatefee;
    private String latefee;
    private String unit;
    private  String invoiceduedate;
    private String loadDefaultProductList;
    private String isSplitInvoice;
    private String transactionUrlQRCode;
    private String defaultLanguage;





    private List<ProductList> productList;
    private ProductList product;

    private List<DefaultProductList> defaultProductList;
    private DefaultProductList defaultProduct;


    private List<UnitList> defaultunitList;

    private List<CustomerDetailList> customerDetailList;


    private UnitList unitlist;

    private String GST;
    private String quantityTotal;

    public String getCount()
    {
        return count;
    }

    public void setCount(String count)
    {
        this.count = count;
    }

    private String count;

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public String getTrackingId()
    {
        return trackingId;
    }

    public void setTrackingId(String trackingId)
    {
        this.trackingId = trackingId;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionUrl()
    {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl)
    {
        this.transactionUrl = transactionUrl;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId)
    {
        this.cardTypeId = cardTypeId;
    }

    public String getRaisedby()
    {
        return raisedby;
    }

    public void setRaisedby(String raisedby)
    {
        this.raisedby = raisedby;
    }

    public String getInvoiceno()
    {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno)
    {
        this.invoiceno = invoiceno;
    }

    public String getInvoiceAction()
    {
        return invoiceAction;
    }

    public void setInvoiceAction(String invoiceAction)
    {
        this.invoiceAction = invoiceAction;
    }

    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getOrderDescription()
    {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription)
    {
        this.orderDescription = orderDescription;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getRedirecturl()
    {
        return redirecturl;
    }

    public void setRedirecturl(String redirecturl)
    {
        this.redirecturl = redirecturl;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
    }

    public String getTelCc()
    {
        return telCc;
    }

    public void setTelCc(String telCc)
    {
        this.telCc = telCc;
    }

    public String getTelno()
    {
        return telno;
    }

    public void setTelno(String telno)
    {
        this.telno = telno;
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getCtoken()
    {
        return ctoken;
    }

    public void setCtoken(String ctoken)
    {
        this.ctoken = ctoken;
    }

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    public String getReminderCounter()
    {
        return reminderCounter;
    }

    public void setReminderCounter(String reminderCounter)
    {
        this.reminderCounter = reminderCounter;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public String getCustName()
    {
        return custName;
    }

    public void setCustName(String custName)
    {
        this.custName = custName;
    }

    public String getPaymodeid()
    {
        return paymodeid;
    }

    public void setPaymodeid(String paymodeid)
    {
        this.paymodeid = paymodeid;
    }

    public String getIsCredential()
    {
        return isCredential;
    }

    public void setIsCredential(String isCredential)
    {
        this.isCredential = isCredential;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPwd()
    {
        return pwd;
    }

    public void setPwd(String pwd)
    {
        this.pwd = pwd;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    public String getMerchantIpAddress()
    {
        return merchantIpAddress;
    }

    public void setMerchantIpAddress(String merchantIpAddress)
    {
        this.merchantIpAddress = merchantIpAddress;
    }

    public String getTerminalid()
    {
        return terminalid;
    }

    public void setTerminalid(String terminalid)
    {
        this.terminalid = terminalid;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getExpirationPeriod()
    {
        return expirationPeriod;
    }

    public void setExpirationPeriod(String expirationPeriod)
    {
        this.expirationPeriod = expirationPeriod;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public PaginationVO getPaginationVO()
    {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO)
    {
        this.paginationVO = paginationVO;
    }

    public MerchantTerminalVo getMerchantTerminalVo()
    {
        return merchantTerminalVo;
    }

    public void setMerchantTerminalVo(MerchantTerminalVo merchantTerminalVo)
    {
        this.merchantTerminalVo = merchantTerminalVo;
    }

    public GenericTransDetailsVO getTransDetailsVO()
    {
        return transDetailsVO;
    }

    public void setTransDetailsVO(GenericTransDetailsVO transDetailsVO)
    {
        this.transDetailsVO = transDetailsVO;
    }

    public String getInitial()
    {
        return initial;
    }

    public void setInitial(String initial)
    {
        this.initial = initial;
    }

    public String getOrderOffset()
    {
        return orderOffset;
    }

    public void setOrderOffset(String orderOffset)
    {
        this.orderOffset = orderOffset;
    }

    public List<ProductList> getProductList()
    {
        return productList;
    }

    public void setProductList(List<ProductList> productList)
    {
        this.productList = productList;
    }



    public String getGST()
    {
        return GST;
    }

    public void setGST(String GST)
    {
        this.GST = GST;
    }

    public String getIssms()
    {
        return Issms;
    }

    public void setIssms(String issms)
    {
        Issms = issms;
    }

    public String getIsemail()
    {
        return Isemail;
    }

    public void setIsemail(String isemail)
    {
        Isemail = isemail;
    }

    public String getIsapp()
    {
        return Isapp;
    }

    public void setIsapp(String isapp)
    {
        Isapp = isapp;
    }

    public String getPaymentterms()
    {
        return paymentterms;
    }

    public void setPaymentterms(String paymentterms)
    {
        this.paymentterms = paymentterms;
    }


    public String getSmsactivation()
    {
        return smsactivation;
    }

    public void setSmsactivation(String smsactivation)
    {
        this.smsactivation = smsactivation;
    }

    public String getIsduedate()
    {
        return isduedate;
    }

    public void setIsduedate(String isduedate)
    {
        this.isduedate = isduedate;
    }

    public String getDuedate()
    {
        return duedate;
    }

    public void setDuedate(String duedate)
    {
        this.duedate = duedate;
    }

    public String getIslatefee()
    {
        return islatefee;
    }

    public void setIslatefee(String islatefee)
    {
        this.islatefee = islatefee;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getLatefee()
    {
        return latefee;
    }

    public void setLatefee(String latefee)
    {
        this.latefee = latefee;
    }

    public String getInvoiceduedate()
    {
        return invoiceduedate;
    }

    public void setInvoiceduedate(String invoiceduedate)
    {
        this.invoiceduedate = invoiceduedate;
    }


    public List<DefaultProductList> getDefaultProductList()
    {
        return defaultProductList;
    }

    public void setDefaultProductList(List<DefaultProductList> defaultProductList)
    {
        this.defaultProductList = defaultProductList;
    }


    public List<UnitList> getDefaultunitList()
    {
        return defaultunitList;
    }

    public void setDefaultunitList(List<UnitList> defaultunitList)
    {
        this.defaultunitList = defaultunitList;
    }

    public String getLoadDefaultProductList()
    {
        return loadDefaultProductList;
    }

    public void setLoadDefaultProductList(String loadDefaultProductList)
    {
        this.loadDefaultProductList = loadDefaultProductList;
    }

    public List<CustomerDetailList> getCustomerDetailList()
    {
        return customerDetailList;
    }

    public void setCustomerDetailList(List<CustomerDetailList> customerDetailList)
    {
        this.customerDetailList = customerDetailList;
    }

    public String getIsSplitInvoice()
    {
        return isSplitInvoice;
    }

    public void setIsSplitInvoice(String isSplitInvoice)
    {
        this.isSplitInvoice = isSplitInvoice;
    }

    public DefaultProductList getDefaultProduct()
    {
        return defaultProduct;
    }

    public void setDefaultProduct(DefaultProductList defaultProduct)
    {
        this.defaultProduct = defaultProduct;
    }

    public ProductList getProduct()
    {
        return product;
    }

    public void setProduct(ProductList product)
    {
        this.product = product;
    }

    public String getQuantityTotal()
    {
        return quantityTotal;
    }

    public void setQuantityTotal(String quantityTotal)
    {
        this.quantityTotal = quantityTotal;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getTransactionUrlQRCode()
    {
        return transactionUrlQRCode;
    }

    public void setTransactionUrlQRCode(String transactionUrlQRCode)
    {
        this.transactionUrlQRCode = transactionUrlQRCode;
    }

    public UnitList getUnitlist()
    {
        return unitlist;
    }

    public void setUnitlist(UnitList unitlist)
    {
        this.unitlist = unitlist;
    }

    public String getDefaultLanguage() { return defaultLanguage; }

    public void setDefaultLanguage(String defaultLanguage) { this.defaultLanguage = defaultLanguage; }
}