package com.transaction.vo.restVO.RequestVO;

import com.sun.jersey.api.core.InjectParam;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Sneha on 6/20/2016.
 */
public class RestPaymentRequest
{
    @FormParam("id")
    String id;

    @FormParam("amount")
    String amount;

    @FormParam("currency")
    String currency;

    @FormParam("actionType")
    String actionType;

    @FormParam("paymentBrand")
    String paymentBrand;

    @FormParam("paymentMode")
    String paymentMode;

    @FormParam("paymentType")
    String paymentType;

    @FormParam("orderDescriptor")
    String orderDescriptor;

    @FormParam("threeDSecure")
    ThreeDSecureVO threeDSecureVO;

    @FormParam("merchantTransactionId")
    String merchantTransactionId;

    @FormParam("createRegistration")
    String createRegistration;

    @FormParam("recurringType")
    String recurringType;

    @FormParam("merchantRedirectUrl")
    String merchantRedirectUrl;

    @FormParam("notificationUrl")
    String notificationUrl;

    @FormParam("responseType")
    String responseType;

    @FormParam("cancelReason")
    String cancelReason;

    @FormParam("idType")
    String idType;

    @FormParam("customParameters")
    String[] customParameters;

    @InjectParam("authentication")
    Authentication authentication;

    @InjectParam("card")
    Card card;

    @InjectParam("virtualAccount")
    VirtualAccount virtualAccount;

    @InjectParam("bankAccount")
    BankAccount bankAccount;

    @InjectParam ("customer")
    Customer customer;

    @InjectParam ("shipping")
    ShippingAddress shippingAddress;

    @InjectParam ("billingAddress")
    BillingAddress billingAddress;

    @InjectParam("threeDSecure")
    ThreeDSecure threeDSecure;

    @InjectParam("date")
    Date date;

    @InjectParam("merchant")
    Merchant merchant;

    @InjectParam("merchant")
    Pagination pagination;


    @FormParam("custAccount")
    String custAccount;

    @FormParam("paymentId")
    String paymentId;

    @FormParam("authToken")
    String authToken;

    @FormParam("expDateOffset")
    String expDateOffset;

    @FormParam("tmpl_amount")
    String tmpl_amount;

    @FormParam("tmpl_currency")
    String tmpl_currency;

    @FormParam("attemptThreeD")
    String attemptThreeD;

    @FormParam("payoutType")
    String payoutType;

    @FormParam("status")
    String status;

    @FormParam("partner.userName")
    String partnerUserName;

    @FormParam("installment")
    String installment;

    @FormParam("redirectMethod")
    String redirectMethod;

    @FormParam("smsCode")
    String smsCode;

    @FormParam("transactionReceipt")
    String transactionReceipt;

    @FormParam("paymentProvider")
    String paymentProvider;

    @FormParam("vpa_address")
    String vpa_address;

    @InjectParam("cardHolderAccountInfo")
    CardHolderAccountInfo cardHolderAccountInfo;

    @InjectParam("deviceDetails")
    DeviceDetails deviceDetails;

    @FormParam("transactionDate")
    String transactionDate;

    @FormParam("transactionType")
    String transactionType;

    @FormParam("personalAccountNumber")
    String personalAccountNumber;

    @FormParam("client_transaction_id")
    String clientTransactionId;

    @FormParam("refund_amount")
    String refundAmount;

    @FormParam("refund_currency")
    String refundCurrency;

    @FormParam("purchase_identifier")
    String purchase_identifier;

    @FormParam("merchant_id")
    String merchant_id ;

    @FormParam("authorization_code")
    String authorization_code ;

    @FormParam("rrn")
    String rrn ;

    @FormParam("arn")
    String arn ;

    @FormParam("stan")
    String stan ;

    @FormParam("call_type")
    String call_type ;

    @FormParam("timeZone")
    String timeZone;

    @FormParam("voucherNumber")
    String voucherNumber;

    public String getVoucherNumber()
    {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber)
    {
        this.voucherNumber = voucherNumber;
    }

    public Merchant getMerchant()
    {
        return merchant;
    }

    public void setMerchant(Merchant merchant)
    {
        this.merchant = merchant;
    }

    public String getResponseType()
    {
        return responseType;
    }

    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }

    public String getPaymentMode()
    {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode)
    {
        this.paymentMode = paymentMode;
    }
    public BankAccount getBankAccount()
    {
        return bankAccount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public void setBankAccount(BankAccount bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String[] getCustomParameters()
    {
        return customParameters;
    }

    public void setCustomParameters(String[] customParameters)
    {
        this.customParameters = customParameters;
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

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getPaymentBrand()
    {
        return paymentBrand;
    }

    public void setPaymentBrand(String paymentBrand)
    {
        this.paymentBrand = paymentBrand;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getOrderDescriptor()
    {
        return orderDescriptor;
    }

    public void setOrderDescriptor(String orderDescriptor)
    {
        this.orderDescriptor = orderDescriptor;
    }
/*    public String getDescriptor()
    {
        return descriptor;
    }

    public void setDescriptor(String descriptor)
    {
        this.descriptor = descriptor;
    }*/

    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public Card getCard()
    {
        return card;
    }

    public void setCard(Card card)
    {
        this.card = card;
    }

    public VirtualAccount getVirtualAccount()
    {
        return virtualAccount;
    }

    public void setVirtualAccount(VirtualAccount virtualAccount)
    {
        this.virtualAccount = virtualAccount;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public ShippingAddress getShippingAddress() {return shippingAddress;}

    public void setShippingAddress(ShippingAddress shippingAddress)
    {
        this.shippingAddress = shippingAddress;
    }

    public BillingAddress getBillingAddress() {return billingAddress;}

    public void setBillingAddress(BillingAddress billingAddress)
    {
        this.billingAddress = billingAddress;
    }

    public String getCreateRegistration()
    {
        return createRegistration;
    }

    public void setCreateRegistration(String createRegistration)
    {
        this.createRegistration = createRegistration;
    }

    public String getRecurringType()
    {
        return recurringType;
    }

    public void setRecurringType(String recurringType)
    {
        this.recurringType = recurringType;
    }

    public ThreeDSecure getThreeDSecure()
    {
        return threeDSecure;
    }

    public void setThreeDSecure(ThreeDSecure threeDSecure)
    {
        this.threeDSecure = threeDSecure;
    }

    public String getMerchantRedirectUrl()
    {
        return merchantRedirectUrl;
    }

    public void setMerchantRedirectUrl(String merchantRedirectUrl)
    {
        this.merchantRedirectUrl = merchantRedirectUrl;
    }

    public String getNotificationUrl()
    {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl)
    {
        this.notificationUrl = notificationUrl;
    }

    public String getCancelReason()
    {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason)
    {
        this.cancelReason = cancelReason;
    }

    public Pagination getPagination()
    {
        return pagination;
    }

    public void setPagination(Pagination pagination)
    {
        this.pagination = pagination;
    }

    public String getCustAccount()
    {
        return custAccount;
    }

    public void setCustAccount(String custAccount)
    {
        this.custAccount = custAccount;
    }

    public String getIdType()
    {
        return idType;
    }

    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getExpDateOffset()
    {
        return expDateOffset;
    }

    public void setExpDateOffset(String expDateOffset)
    {
        this.expDateOffset = expDateOffset;
    }

    public String getTmpl_currency()
    {
        return tmpl_currency;
    }

    public void setTmpl_currency(String tmpl_currency)
    {
        this.tmpl_currency = tmpl_currency;
    }

    public String getTmpl_amount()
    {
        return tmpl_amount;
    }

    public void setTmpl_amount(String tmpl_amount)
    {
        this.tmpl_amount = tmpl_amount;
    }

    public String getAttemptThreeD()
    {
        return attemptThreeD;
    }

    public void setAttemptThreeD(String attemptThreeD)
    {
        this.attemptThreeD = attemptThreeD;
    }

    public String getPayoutType()
    {
        return payoutType;
    }

    public void setPayoutType(String payoutType)
    {
        this.payoutType = payoutType;
    }

    public ThreeDSecureVO getThreeDSecureVO()
    {
        return threeDSecureVO;
    }

    public void setThreeDSecureVO(ThreeDSecureVO threeDSecureVO)
    {
        this.threeDSecureVO = threeDSecureVO;
    }

    public String getPartnerUserName()
    {
        return partnerUserName;
    }

    public void setPartnerUserName(String partnerUserName)
    {
        this.partnerUserName = partnerUserName;
    }

    public String getInstallment() {return installment;}

    public void setInstallment(String installment) {this.installment = installment;}

    public String getRedirectMethod() {return redirectMethod;}

    public void setRedirectMethod(String redirectMethod) {this.redirectMethod = redirectMethod;}

    public String getSmsCode() {return smsCode;}

    public void setSmsCode(String smsCode) {this.smsCode = smsCode;}

    public CardHolderAccountInfo getCardHolderAccountInfo()
    {
        return cardHolderAccountInfo;
    }

    public void setCardHolderAccountInfo(CardHolderAccountInfo cardHolderAccountInfo)
    {
        this.cardHolderAccountInfo = cardHolderAccountInfo;
    }

    public DeviceDetails getDeviceDetails()
    {
        return deviceDetails;
    }

    public void setDeviceDetails(DeviceDetails deviceDetails)
    {
        this.deviceDetails = deviceDetails;
    }

    public String getTransactionReceipt()
    {
        return transactionReceipt;
    }

    public void setTransactionReceipt(String transactionReceipt)
    {
        this.transactionReceipt = transactionReceipt;
    }

    public String getPaymentProvider()
    {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider)
    {
        this.paymentProvider = paymentProvider;
    }

    public String getVpa_address()
    {
        return vpa_address;
    }

    public void setVpa_address(String vpa_address)
    {
        this.vpa_address = vpa_address;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getPersonalAccountNumber()
    {
        return personalAccountNumber;
    }

    public void setPersonalAccountNumber(String personalAccountNumber)
    {
        this.personalAccountNumber = personalAccountNumber;
    }

    public String getClientTransactionId()
    {
        return clientTransactionId;
    }

    public void setClientTransactionId(String clientTransactionId)
    {
        this.clientTransactionId = clientTransactionId;
    }

    public String getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public String getRefundCurrency()
    {
        return refundCurrency;
    }

    public void setRefundCurrency(String refundCurrency)
    {
        this.refundCurrency = refundCurrency;
    }

    public String getPurchase_identifier()
    {
        return purchase_identifier;
    }

    public void setPurchase_identifier(String purchase_identifier)
    {
        this.purchase_identifier = purchase_identifier;
    }

    public String getMerchant_id()
    {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id)
    {
        this.merchant_id = merchant_id;
    }

    public String getAuthorization_code()
    {
        return authorization_code;
    }

    public void setAuthorization_code(String authorization_code)
    {
        this.authorization_code = authorization_code;
    }

    public String getRrn()
    {
        return rrn;
    }

    public void setRrn(String rrn)
    {
        this.rrn = rrn;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getStan()
    {
        return stan;
    }

    public void setStan(String stan)
    {
        this.stan = stan;
    }

    public String getCall_type()
    {
        return call_type;
    }

    public void setCall_type(String call_type)
    {
        this.call_type = call_type;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }
}
