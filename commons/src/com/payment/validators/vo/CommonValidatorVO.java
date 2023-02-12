package com.payment.validators.vo;

import com.directi.pg.core.valueObjects.*;
import com.manager.vo.*;
import com.payment.beekash.vos.CardVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/2/14
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonValidatorVO extends GenericRequestVO
{
    private GenericTransDetailsVO transDetailsVO;
    private GenericCardDetailsVO cardDetailsVO;
    private GenericAddressDetailsVO addressDetailsVO;
    private MerchantDetailsVO merchantDetailsVO;
    private RecurringBillingVO recurringBillingVO;
    private ErrorCodeListVO errorCodeListVO ;
    private SplitPaymentVO splitPaymentVO;
    private AccountInfoVO accountInfoVO;
    private TerminalVO terminalVO;
    private TerminalVO requestedTerminalVO;
    private ReserveField2VO reserveField2VO;
    private BankAccountVO bankAccountVO;
    private PartnerDetailsVO partnerDetailsVO;
    private PaginationVO paginationVO;
    private MarketPlaceVO marketPlaceVO;
    private TransactionDetailsVO transactionDetailsVO;
    private GenericDeviceDetailsVO deviceDetailsVO;
    private ProductDetailsVO productDetailsVO;
    private String paymentType;
    private String cardType;
    private String transactionType;
    private String terminalId;
    private String trackingid;
    private  String resp_flag;
    private String res_checksum;
    //Added for Ideal
    private String senderBankCode;
    private String reason;
    private String ruleTriggered;
    private String responseLength;
    private String responseType;
    private String ctoken;
    //Added For Online Fraud Checking
    private String  time;
    private boolean isFraud;
    private String fraudScore;
    private String htmlFormValue;
    private String logoName;
    private String partnerName;
    private String paretnerId;
    private String createRegistration;
    private String token;
    private String errorMsg;
    private String isPSTProcessingRequest;
    private String actionType;
    private String paymentBrand;
    private String paymentMode;
    private String requestedIP;
    private String customerId;
    private String customerBankId;
    private HashMap mapOfPaymentCardType;
    private HashMap terminalMap;
    private String isProcessed;
    private String version;
    private String invoiceAction;
    private String invoiceid;
    private String flightMode;
    private String invoiceId;
    private String custAccount;
    private String idType;
    private String custAccountId;
    private String custEmail;
    private String authToken;
    private String displayCurrency; //used only for Voucher Money - Not To Use
    private String displayAmount; //used only for Voucher Money - Not To Use
    private String reject3DCard;
    private String currencyConversion;
    private String conversionCurrency;
    private String status;
    private String eci;
    private String verificationId;
    private String xid;
    private String attemptThreeD;
    private String errorName;
    private String payoutType;
    private String consentStmnt;
    private String commissionPaidToUser;//for vm deposit response
    private String commPaidToUserCurrency;//for vm deposit response
    private String device; // for mobile devices
    private boolean isVIPCard;
    private boolean isVIPEmail;
    private LinkedHashMap<String,TerminalVO> terminalMapLimitRouting;
    private List<MarketPlaceVO> marketPlaceVOList;
    private String processorName;
    private String processorBankName;
    private String failedSplitTransactions;
    private String merchantTransactionId;
    private String referenceid;
    private String deviceType;
    private String smsCode;
    private String bankCode;
    private String bankDescription;
    private String processingbank;

    //Device info for wland
    private String deviceId;
    private String uniqueId;
    private String vpa_address;
    private String accountId;
    private String fraudId;

    private HashMap currencyListMap;
    private HashMap cardVOHashMap;
    private HashMap existCardVOHash;
    private Hashtable transHash;
    private String timeZone;
    private String isMobileNoVerified;
    private String isEmailVerified;



    public String getMerchantTransactionId()
    {
        return merchantTransactionId;
    }

    public void setMerchantTransactionId(String merchantTransactionId)
    {
        this.merchantTransactionId = merchantTransactionId;
    }

    public String getInvoiceId()
    {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId)
    {
        this.invoiceId = invoiceId;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
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

    public BankAccountVO getBankAccountVO()
    {
        return bankAccountVO;
    }

    public void setBankAccountVO(BankAccountVO bankAccountVO)
    {
        this.bankAccountVO = bankAccountVO;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getResp_flag()
    {
        return resp_flag;
    }

    public void setResp_flag(String resp_flag)
    {
        this.resp_flag = resp_flag;
    }

    public String getRes_checksum()
    {
        return res_checksum;
    }

    public void setRes_checksum(String res_checksum)
    {
        this.res_checksum = res_checksum;
    }

    public String getSenderBankCode()
    {
        return senderBankCode;
    }

    public void setSenderBankCode(String senderBankCode)
    {
        this.senderBankCode = senderBankCode;
    }

    public String getHtmlFormValue()
    {
        return htmlFormValue;
    }

    public void setHtmlFormValue(String htmlFormValue)
    {
        this.htmlFormValue = htmlFormValue;
    }

    public String getCtoken()
    {
        return ctoken;
    }

    public void setCtoken(String ctoken)
    {
        this.ctoken = ctoken;
    }

    public String getLogoName()
    {
        return logoName;
    }

    public void setLogoName(String logoName)
    {
        this.logoName = logoName;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getParetnerId()
    {
        return paretnerId;
    }

    public void setParetnerId(String paretnerId)
    {
        this.paretnerId = paretnerId;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getTrackingid()
    {
        return trackingid;
    }

    public void setTrackingid(String trackingid)
    {
        this.trackingid = trackingid;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId = terminalId;
    }

    public GenericTransDetailsVO getTransDetailsVO()
    {
        return transDetailsVO;
    }

    public void setTransDetailsVO(GenericTransDetailsVO transDetailsVO)
    {
        this.transDetailsVO = transDetailsVO;
    }

    public GenericCardDetailsVO getCardDetailsVO()
    {
        return cardDetailsVO;
    }

    public void setCardDetailsVO(GenericCardDetailsVO cardDetailsVO)
    {
        this.cardDetailsVO = cardDetailsVO;
    }

    public GenericAddressDetailsVO getAddressDetailsVO()
    {
        return addressDetailsVO;
    }

    public void setAddressDetailsVO(GenericAddressDetailsVO addressDetailsVO)
    {
        this.addressDetailsVO = addressDetailsVO;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public boolean isFraud()
    {
        return isFraud;
    }

    public void setFraud(boolean fraud)
    {
        isFraud = fraud;
    }

    public RecurringBillingVO getRecurringBillingVO()
    {
        return recurringBillingVO;
    }

    public void setRecurringBillingVO(RecurringBillingVO recurringBillingVO)
    {
        this.recurringBillingVO = recurringBillingVO;
    }

    public String getCreateRegistration()
    {
        return createRegistration;
    }

    public void setCreateRegistration(String createRegistration)
    {
        this.createRegistration = createRegistration;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public ErrorCodeListVO getErrorCodeListVO()
    {
        return errorCodeListVO;
    }

    public void setErrorCodeListVO(ErrorCodeListVO errorCodeListVO)
    {
        this.errorCodeListVO = errorCodeListVO;
    }

    public String getFraudScore()
    {
        return fraudScore;
    }

    public void setFraudScore(String fraudScore)
    {
        this.fraudScore = fraudScore;
    }

    public SplitPaymentVO getSplitPaymentVO()
    {
        return splitPaymentVO;
    }

    public void setSplitPaymentVO(SplitPaymentVO splitPaymentVO)
    {
        this.splitPaymentVO = splitPaymentVO;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public ReserveField2VO getReserveField2VO()
    {
        return reserveField2VO;
    }

    public void setReserveField2VO(ReserveField2VO reserveField2VO)
    {
        this.reserveField2VO = reserveField2VO;
    }

    public String getRuleTriggered()
    {
        return ruleTriggered;
    }

    public void setRuleTriggered(String ruleTriggered)
    {
        this.ruleTriggered = ruleTriggered;
    }

    public String getResponseLength()
    {
        return responseLength;
    }

    public void setResponseLength(String responseLength)
    {
        this.responseLength = responseLength;
    }

    public String getResponseType()
    {
        return responseType;
    }

    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getIsPSTProcessingRequest()
    {
        return isPSTProcessingRequest;
    }

    public void setIsPSTProcessingRequest(String isPSTProcessingRequest)
    {
        this.isPSTProcessingRequest = isPSTProcessingRequest;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getRequestedIP()
    {
        return requestedIP;
    }

    public void setRequestedIP(String requestedIP)
    {
        this.requestedIP = requestedIP;
    }

    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
    }

    public HashMap getMapOfPaymentCardType()
    {
        return mapOfPaymentCardType;
    }

    public void setMapOfPaymentCardType(HashMap mapOfPaymentCardType)
    {
        this.mapOfPaymentCardType = mapOfPaymentCardType;
    }

    public HashMap getTerminalMap()
    {
        return terminalMap;
    }

    public void setTerminalMap(HashMap terminalMap)
    {
        this.terminalMap = terminalMap;
    }

    public void setIsFraud(boolean isFraud)
    {
        this.isFraud = isFraud;
    }

    public String getIsProcessed()
    {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed)
    {
        this.isProcessed = isProcessed;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getInvoiceAction()
    {
        return invoiceAction;
    }

    public void setInvoiceAction(String invoiceAction)
    {
        this.invoiceAction = invoiceAction;
    }

    public String getInvoiceid()
    {
        return invoiceid;
    }

    public void setInvoiceid(String invoiceid)
    {
        this.invoiceid = invoiceid;
    }

    public String getFlightMode()
    {
        return flightMode;
    }

    public void setFlightMode(String flightMode)
    {
        this.flightMode = flightMode;
    }

    public PaginationVO getPaginationVO()
    {
        return paginationVO;
    }

    public void setPaginationVO(PaginationVO paginationVO)
    {
        this.paginationVO = paginationVO;
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

    public String getCustAccountId()
    {
        return custAccountId;
    }

    public void setCustAccountId(String custAccountId)
    {
        this.custAccountId = custAccountId;
    }

    public String getCustEmail()
    {
        return custEmail;
    }

    public void setCustEmail(String custEmail)
    {
        this.custEmail = custEmail;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public String getDisplayCurrency()
    {
        return displayCurrency;
    }

    public void setDisplayCurrency(String displayCurrency)
    {
        this.displayCurrency = displayCurrency;
    }

    public String getDisplayAmount()
    {
        return displayAmount;
    }

    public void setDisplayAmount(String displayAmount)
    {
        this.displayAmount = displayAmount;
    }

    public String getCustomerBankId()
    {
        return customerBankId;
    }

    public void setCustomerBankId(String customerBankId)
    {
        this.customerBankId = customerBankId;
    }

    public String getReject3DCard()
    {
        return reject3DCard;
    }

    public void setReject3DCard(String reject3DCard)
    {
        this.reject3DCard = reject3DCard;
    }

    public String getCurrencyConversion()
    {
        return currencyConversion;
    }

    public void setCurrencyConversion(String currencyConversion)
    {
        this.currencyConversion = currencyConversion;
    }

    public String getConversionCurrency()
    {
        return conversionCurrency;
    }

    public void setConversionCurrency(String conversionCurrency)
    {
        this.conversionCurrency = conversionCurrency;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getEci()
    {
        return eci;
    }

    public void setEci(String eci)
    {
        this.eci = eci;
    }

    public String getPayoutType()
    {
        return payoutType;
    }

    public void setPayoutType(String payoutType)
    {
        this.payoutType = payoutType;
    }

    public String getAttemptThreeD()
    {
        return attemptThreeD;
    }

    public void setAttemptThreeD(String attemptThreeD)
    {
        this.attemptThreeD = attemptThreeD;
    }

    public String getErrorName()
    {
        return errorName;
    }

    public void setErrorName(String errorName)
    {
        this.errorName = errorName;
    }

    public String getConsentStmnt()
    {
        return consentStmnt;
    }

    public void setConsentStmnt(String consentStmnt)
    {
        this.consentStmnt = consentStmnt;
    }

    public String getVerificationId()
    {
        return verificationId;
    }

    public void setVerificationId(String verificationId)
    {
        this.verificationId = verificationId;
    }

    public String getXid()
    {
        return xid;
    }

    public void setXid(String xid)
    {
        this.xid = xid;
    }

    public String getCommissionPaidToUser()
    {
        return commissionPaidToUser;
    }

    public void setCommissionPaidToUser(String commissionPaidToUser)
    {
        this.commissionPaidToUser = commissionPaidToUser;
    }

    public String getCommPaidToUserCurrency()
    {
        return commPaidToUserCurrency;
    }

    public void setCommPaidToUserCurrency(String commPaidToUserCurrency)
    {
        this.commPaidToUserCurrency = commPaidToUserCurrency;
    }

    public String getDevice()
    {
        return device;
    }

    public void setDevice(String device)
    {
        this.device = device;
    }

    public boolean isVIPCard()
    {
        return isVIPCard;
    }

    public void setVIPCard(boolean isVIPCard)
    {
        this.isVIPCard = isVIPCard;
    }

    public boolean isVIPEmail()
    {
        return isVIPEmail;
    }

    public void setVIPEmail(boolean isVIPEmail)
    {
        this.isVIPEmail = isVIPEmail;
    }

    public LinkedHashMap<String,TerminalVO> getTerminalMapLimitRouting()
    {
        return terminalMapLimitRouting;
    }

    public void setTerminalMapLimitRouting(LinkedHashMap<String,TerminalVO> terminalMapLimitRouting)
    {
        this.terminalMapLimitRouting = terminalMapLimitRouting;
    }

    public List<MarketPlaceVO> getMarketPlaceVOList()
    {
        return marketPlaceVOList;
    }

    public void setMarketPlaceVOList(List<MarketPlaceVO> marketPlaceVOList)
    {
        this.marketPlaceVOList = marketPlaceVOList;
    }

    public MarketPlaceVO getMarketPlaceVO()
    {
        return marketPlaceVO;
    }

    public void setMarketPlaceVO(MarketPlaceVO marketPlaceVO)
    {
        this.marketPlaceVO = marketPlaceVO;
    }

    public String getProcessorName()
    {
        return processorName;
    }

    public void setProcessorName(String processorName)
    {
        this.processorName = processorName;
    }

    public String getFailedSplitTransactions()
    {
        return failedSplitTransactions;
    }

    public void setFailedSplitTransactions(String failedSplitTransactions) {this.failedSplitTransactions = failedSplitTransactions;}

    public TerminalVO getRequestedTerminalVO() {return requestedTerminalVO;}

    public void setRequestedTerminalVO(TerminalVO requestedTerminalVO) {this.requestedTerminalVO = requestedTerminalVO;}

    public String getReferenceid()
    {
        return referenceid;
    }

    public void setReferenceid(String referenceid)
    {
        this.referenceid = referenceid;
    }

    public String getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public HashMap getCurrencyListMap()
    {
        return currencyListMap;
    }

    public void setCurrencyListMap(HashMap currencyListMap)
    {
        this.currencyListMap = currencyListMap;
    }

    public String getSmsCode() {return smsCode;}

    public void setSmsCode(String smsCode) {this.smsCode = smsCode;}

    public TransactionDetailsVO getTransactionDetailsVO() {return transactionDetailsVO;}

    public void setTransactionDetailsVO(TransactionDetailsVO transactionDetailsVO) {this.transactionDetailsVO = transactionDetailsVO;}

    public AccountInfoVO getAccountInfoVO()
    {
        return accountInfoVO;
    }

    public void setAccountInfoVO(AccountInfoVO accountInfoVO)
    {
        this.accountInfoVO = accountInfoVO;
    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(String deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    public String getBankCode() {return bankCode;}

    public void setBankCode(String bankCode) {this.bankCode = bankCode;}

    public String getBankDescription() {return bankDescription;}

    public void setBankDescription(String bankDescription) {this.bankDescription = bankDescription;}

    public GenericDeviceDetailsVO getDeviceDetailsVO()
    {
        return deviceDetailsVO;
    }

    public void setDeviceDetailsVO(GenericDeviceDetailsVO deviceDetailsVO)
    {
        this.deviceDetailsVO = deviceDetailsVO;
    }

    public String getVpa_address()
    {
        return vpa_address;
    }

    public void setVpa_address(String vpa_address)
    {
        this.vpa_address = vpa_address;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public HashMap getCardVOHashMap()
    {
        return cardVOHashMap;
    }

    public void setCardVOHashMap(HashMap cardVOHashMap)
    {
        this.cardVOHashMap = cardVOHashMap;
    }

    public HashMap getExistCardVOHash()
    {
        return existCardVOHash;
    }

    public void setExistCardVOHash(HashMap existCardVOHash)
    {
        this.existCardVOHash = existCardVOHash;
    }

    public Hashtable getTransHash()
    {
        return transHash;
    }

    public void setTransHash(Hashtable transHash)
    {
        this.transHash = transHash;
    }

    public String getFraudId()
    {
        return fraudId;
    }

    public void setFraudId(String fraudId)
    {
        this.fraudId = fraudId;
    }

    public String getProcessorBankName()
    {
        return processorBankName;
    }

    public void setProcessorBankName(String processorBankName)
    {
        this.processorBankName = processorBankName;
    }

    public String getProcessingbank()
    {
        return processingbank;
    }

    public void setProcessingbank(String processingbank)
    {
        this.processingbank = processingbank;
    }

    public String getTimeZone()
    {
        return timeZone;
    }

    public void setTimeZone(String timeZone)
    {
        this.timeZone = timeZone;
    }

    public String getIsMobileNoVerified()
    {
        return isMobileNoVerified;
    }

    public void setIsMobileNoVerified(String isMobileNoVerified)
    {
        this.isMobileNoVerified = isMobileNoVerified;
    }

    public String getIsEmailVerified()
    {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified)
    {
        this.isEmailVerified = isEmailVerified;
    }
}