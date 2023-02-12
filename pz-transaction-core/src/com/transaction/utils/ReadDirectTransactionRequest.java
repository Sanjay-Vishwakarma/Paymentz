package com.transaction.utils;

import com.directi.pg.FailedTransactionLogEntry;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.*;
import com.google.gson.Gson;
import com.manager.vo.*;
import com.manager.vo.PaginationVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.*;
import com.payment.validators.vo.ReserveField2VO;
import com.transaction.vo.*;
import com.transaction.vo.restVO.RequestVO.*;
import com.transaction.vo.restVO.RequestVO.BankAccountVO;
import com.transaction.vo.restVO.RequestVO.CardVO;
import com.transaction.vo.restVO.RequestVO.CustomerVO;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 5/30/15
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReadDirectTransactionRequest
{
    private static Logger logger = new Logger(ReadDirectTransactionRequest.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReadDirectTransactionRequest.class.getName());
    private Functions functions = new Functions();

    /**
     * This is method is to read the request from the WebService VO
     * @param directTransactionRequest
     * @return
     */
    public DirectKitValidatorVO readRequestParameterForWebServiceDirectKit(DirectTransactionRequest directTransactionRequest)
    {
        //SimpleDate formatter
        DirectKitValidatorVO directKitValidatorVO=null;
        if(directTransactionRequest!=null)
        {
            directKitValidatorVO=new DirectKitValidatorVO();

            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            RecurringBillingVO recurringBillingVO = null;
            SplitPaymentVO splitPaymentVO = null;
            PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

            transactionLogger.debug("Partner ID from the web service::::" + directTransactionRequest.getPartnerId());
            //merchant details VO
            if (functions.isValueNull(String.valueOf(directTransactionRequest.getToId())))
            {
                merchantDetailsVO.setMemberId(String.valueOf(directTransactionRequest.getToId()));
            }

            if (functions.isValueNull(String.valueOf(directTransactionRequest.getPartnerId())))
            {
                directKitValidatorVO.setParetnerId(directTransactionRequest.getPartnerId());
            }

            merchantDetailsVO.setAccountId("");
            genericTransDetailsVO.setCurrency(directTransactionRequest.getCurrency());
            //Transaction details VO

            genericTransDetailsVO.setTotype(directTransactionRequest.getToType());
            genericTransDetailsVO.setRedirectUrl(directTransactionRequest.getRedirectURL());
            genericTransDetailsVO.setChecksum(directTransactionRequest.getChecksum());
            genericTransDetailsVO.setAmount(String.valueOf(directTransactionRequest.getAmount()));
            genericTransDetailsVO.setOrderDesc(directTransactionRequest.getOrderDescription());
            genericTransDetailsVO.setOrderId(directTransactionRequest.getDescription());
            //Address Details VO
            genericAddressDetailsVO.setCountry(directTransactionRequest.getCountryCode());
            genericAddressDetailsVO.setCity(directTransactionRequest.getCity());
            genericAddressDetailsVO.setState(directTransactionRequest.getState());
            genericAddressDetailsVO.setZipCode(directTransactionRequest.getZip());
            genericAddressDetailsVO.setStreet(directTransactionRequest.getStreet());
            genericAddressDetailsVO.setPhone(directTransactionRequest.getTelNo());
            genericAddressDetailsVO.setEmail(directTransactionRequest.getEmailAddress());
            genericAddressDetailsVO.setLanguage(directTransactionRequest.getLanguage());
            genericAddressDetailsVO.setFirstname(directTransactionRequest.getFirstName());
            genericAddressDetailsVO.setLastname(directTransactionRequest.getLastName());
            genericAddressDetailsVO.setBirthdate(directTransactionRequest.getBirthDate());
            genericAddressDetailsVO.setSsn(directTransactionRequest.getSsn());
            genericAddressDetailsVO.setTelnocc(directTransactionRequest.getTelNoCC());
            genericAddressDetailsVO.setCardHolderIpAddress(directTransactionRequest.getCardHolderIpAddress());
            //Card Details VO
            genericCardDetailsVO.setCardNum(directTransactionRequest.getCardnumber());
            //genericCardDetailsVO.setcVV(String.format("%03d",String.valueOf(directTransactionRequest.getCvv())));
            genericCardDetailsVO.setcVV(directTransactionRequest.getCvv());
            //String ExpiryMonth=String.format("%02d", directTransactionRequest.getExpiry_month());
            //genericCardDetailsVO.setExpMonth(ExpiryMonth);
            genericCardDetailsVO.setExpMonth(directTransactionRequest.getExpiry_month1());
            genericCardDetailsVO.setExpYear(directTransactionRequest.getExpiry_year1());
            genericCardDetailsVO.setCardHolderName(directTransactionRequest.getFirstName()+" "+directTransactionRequest.getLastName());
            //Payment details
            directKitValidatorVO.setPaymentType(directTransactionRequest.getPaymentType());
            directKitValidatorVO.setCardType(directTransactionRequest.getCardType());
            directKitValidatorVO.setTerminalId(directTransactionRequest.getTerminalId());
            directKitValidatorVO.setIsEncrypted(directTransactionRequest.getIsEncrypted());
            //RecurringDetails VO
            if(functions.isValueNull(directTransactionRequest.getRecurringDetail()))
            {
                recurringBillingVO = new RecurringBillingVO();
                recurringBillingVO.setReqField1(directTransactionRequest.getRecurringDetail());
                if(directTransactionRequest.getReservedField1().contains("|"))
                {
                    String values[] = directTransactionRequest.getRecurringDetail().split("\\|");
                    if(values.length==4)
                    {
                        recurringBillingVO.setRecurring(values[0]);
                        recurringBillingVO.setInterval(values[1]);
                        recurringBillingVO.setFrequency(values[2]);
                        recurringBillingVO.setRunDate(values[3]);
                    }
                }
            }

            if(functions.isValueNull(directTransactionRequest.getSplitPaymentDetail()))
            {
                splitPaymentVO = new SplitPaymentVO();
                splitPaymentVO.setSplitPaymentDetail(directTransactionRequest.getSplitPaymentDetail());
            }
            directKitValidatorVO.setCreateRegistration(directTransactionRequest.getCreateRegistration());

            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setSplitPaymentVO(splitPaymentVO);
            directKitValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        }

        transactionLogger.debug("card number-"+directTransactionRequest.getCardnumber());
        transactionLogger.debug("Exp-" + directTransactionRequest.getExpiry_month());
        transactionLogger.debug("exp-" + directTransactionRequest.getExpiry_year());
        transactionLogger.debug("cvv-" + directTransactionRequest.getCvv());
        transactionLogger.debug("last-" + directTransactionRequest.getLastName());
        transactionLogger.debug("first-" + directTransactionRequest.getFirstName());
        transactionLogger.debug("lang-" + directTransactionRequest.getLanguage());
        transactionLogger.debug("ip-" + directTransactionRequest.getCardHolderIpAddress());
        transactionLogger.debug("amt-" + directTransactionRequest.getAmount());
        transactionLogger.debug("checksum-" + directTransactionRequest.getChecksum());
        transactionLogger.debug("city-" + directTransactionRequest.getCity());
        transactionLogger.debug("coucode-" + directTransactionRequest.getCountryCode());
        transactionLogger.debug("desc-" + directTransactionRequest.getDescription());
        transactionLogger.debug("odesc-" + directTransactionRequest.getOrderDescription());
        transactionLogger.debug("zip-" + directTransactionRequest.getZip());
        transactionLogger.debug("toid-" + directTransactionRequest.getToId());
        transactionLogger.debug("totype-"+directTransactionRequest.getToType());
        transactionLogger.debug("bdate-" + directTransactionRequest.getBirthDate());
        transactionLogger.debug("terminal-" + directTransactionRequest.getTerminalId());
        transactionLogger.debug("tel-" + directTransactionRequest.getTelNoCC());
        transactionLogger.debug("street-" + directTransactionRequest.getStreet());
        transactionLogger.debug("email-" + directTransactionRequest.getEmailAddress());
        transactionLogger.debug("tel-" + directTransactionRequest.getTelNo());
        transactionLogger.debug("state-" + directTransactionRequest.getState());
        transactionLogger.debug("payment-" + directTransactionRequest.getPaymentType());
        transactionLogger.debug("card-" + directTransactionRequest.getCardType());
        transactionLogger.debug("split-" + directTransactionRequest.getSplitPaymentDetail());
        transactionLogger.debug("partnerid-" + directTransactionRequest.getPartnerId());


        return directKitValidatorVO;
    }

    /**
     * This is to convert client Refund request to System type request
     * @param directRefundRequest
     * @return
     */
    public DirectRefundValidatorVO readRequestParameterForRefundTransaction(DirectRefundRequest directRefundRequest)
    {
        DirectRefundValidatorVO directRefundValidatorVO =null;

        if(directRefundRequest!=null)
        {

            directRefundValidatorVO=new DirectRefundValidatorVO();
            //Internal Vo
            MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();

            merchantDetailsVO.setMemberId(directRefundRequest.getToId());
            directRefundValidatorVO.setParetnerId(directRefundRequest.getPartnerid());
            directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            directRefundValidatorVO.setTrackingid(directRefundRequest.getTrackingId());
            directRefundValidatorVO.setRefundAmount(String.valueOf(directRefundRequest.getRefundAmount()));
            directRefundValidatorVO.setRefundReason(directRefundRequest.getReason());
            directRefundValidatorVO.setCheckSum(directRefundRequest.getCheckSum());
        }
        return directRefundValidatorVO;
    }

    public DirectCaptureValidatorVO readRequestParameterForCaptureTransaction(DirectCaptureRequest directCaptureRequest)
    {
        DirectCaptureValidatorVO directKitValidatorVO= null;
        if(directCaptureRequest!=null)
        {
            directKitValidatorVO= new DirectCaptureValidatorVO();

            MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();

            directKitValidatorVO.setTrackingid(directCaptureRequest.getTrackingId());
            directKitValidatorVO.setParetnerId(directCaptureRequest.getPartnerid());
            merchantDetailsVO.setMemberId(directCaptureRequest.getToId());
            directKitValidatorVO.setCaptureAmount(String.valueOf(directCaptureRequest.getCaptureAmount()));
            directKitValidatorVO.setCheckSum(directCaptureRequest.getCheckSum());
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        }
        return directKitValidatorVO;
    }

    public DirectKitValidatorVO readRequestParameterForCancelTransaction(DirectCancelRequest directCancelRequest)
    {
        DirectKitValidatorVO directKitValidatorVO = null;
        if (directCancelRequest != null)
        {
            directKitValidatorVO = new DirectKitValidatorVO();

            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();

            merchantDetailsVO.setMemberId(directCancelRequest.getToId());

            genericTransDetailsVO.setChecksum(directCancelRequest.getCheckSum());
            genericTransDetailsVO.setOrderId(directCancelRequest.getDescription());

            directKitValidatorVO.setTrackingid(directCancelRequest.getTrackingId());
            directKitValidatorVO.setParetnerId(directCancelRequest.getPartnerid());

            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        }
        return directKitValidatorVO;
    }

    public DirectInquiryValidatorVO readRequestParameterForInquiryTransaction(DirectInquiryRequest directInquiryRequest)
    {
        DirectInquiryValidatorVO directInquiryValidatorVO = null;
        if (directInquiryRequest != null)
        {
            directInquiryValidatorVO = new DirectInquiryValidatorVO();

            MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

            merchantDetailsVO.setMemberId(directInquiryRequest.getToId());

            directInquiryValidatorVO.setCheckSum(directInquiryRequest.getCheckSum());
            directInquiryValidatorVO.setDescription(directInquiryRequest.getDescription());

            directInquiryValidatorVO.setTrackingId(directInquiryRequest.getTrackingId());

            directInquiryValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        }
        return directInquiryValidatorVO;
    }

    public String checkValueNumeric(String paymentType, String cardType, String TerminalId, ErrorCodeListVO errorCodeListVO, CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        Functions functions = new Functions();
        String error = "";
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        try
        {
            if (functions.isValueNull(paymentType))
            {
                if (functions.isNumericVal(paymentType))
                {
                    Integer.parseInt(paymentType);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TYPE);
                    error = /*error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+ */ErrorMessages.INVALID_PAYMENT_TYPE + "|";
                    errorCodeListVO.addListOfError(errorCodeVO);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                }
            }

            if (functions.isValueNull(cardType))
            {
                if (functions.isNumericVal(cardType))
                {
                    Integer.parseInt(cardType);
                }
                else
                {

                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CARD_TYPE);
                    error = /*error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+*/ ErrorMessages.INVALID_CARD_TYPE + "|";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_CARD_TYPE.toString(), ErrorType.VALIDATION.toString());
                    errorCodeListVO.addListOfError(errorCodeVO);
                }
            }

            if (functions.isValueNull(TerminalId))
            {
                if (functions.isNumericVal(TerminalId))
                {
                    Integer.parseInt(TerminalId);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TERMINALID);
                    error = /*error + errorCodeVO.getErrorCode()+" "+errorCodeVO.getErrorDescription()+","+*/ ErrorMessages.INVALID_TERMINALID + "|";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_TERMINALID.toString(), ErrorType.VALIDATION.toString());
                    errorCodeListVO.addListOfError(errorCodeVO);
                }
            }
        }
        catch (NumberFormatException e)
        {
            error = "Kindly Pass Numeric value in paymentType , Card Type and Terminal ID fields. ";
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java", "checkValueNumeric()", null, "transaction", "Technical Exception Thrown:::", PZTechnicalExceptionEnum.NUMBER_FORMAT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return error;
    }

    /**
     * This is to convert Rest request to System type request
     *
     * @param request
     * @return
     */

    public RestPaymentRequestVO readRequestForRestTransaction(RestPaymentRequest request)
    {

        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        if (request != null)
        {

            CardVO              cardVO              = new CardVO();
            BillingAddressVO    billingAddressVO    = new BillingAddressVO();
            AuthenticationVO    authentication      = new AuthenticationVO();

            //Athentication Details
            //authentication = setAuthentication(request);
            restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
            restPaymentRequestVO.setMerchantTransactionId(request.getMerchantTransactionId());
            restPaymentRequestVO.setOrderDescriptor(request.getOrderDescriptor());
            restPaymentRequestVO.setCurrency(request.getCurrency());
            restPaymentRequestVO.setAmount(request.getAmount());
            restPaymentRequestVO.setVoucherNumber(request.getVoucherNumber());
            restPaymentRequestVO.setNotificationUrl(request.getNotificationUrl());
            if (functions.isValueNull(request.getRecurringType()))
                restPaymentRequestVO.setRecurringType(request.getRecurringType());
            if (request.getCreateRegistration() != null)
                restPaymentRequestVO.setCreateRegistration(request.getCreateRegistration());

            if(functions.isValueNull(request.getRedirectMethod())){
                restPaymentRequestVO.setRedirectMethod(request.getRedirectMethod());
            }
            //Address details
            restPaymentRequestVO.setShippingAddressVO(getShippingAddressVO(request.getShippingAddress()));
            restPaymentRequestVO.setCardHolderAccountInfoVO(getCardholderAccountInfo(request.getCardHolderAccountInfo()));

            //card details fetching
            restPaymentRequestVO.setCardVO(getCardVO(request.getCard()));

            restPaymentRequestVO.setCustomerVO(getCustomerVO(request.getCustomer()));

            //3D Parameters
            restPaymentRequestVO.setThreeDSecureVO(getThreeDSecureVO(request.getThreeDSecure()));

            //account details fetching
            restPaymentRequestVO.setBankAccountVO(getBankAccountVO(request.getBankAccount()));
            //normal transaction details
            if (functions.isValueNull(request.getMerchantRedirectUrl()))
                restPaymentRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
            if (functions.isValueNull(request.getPaymentBrand()))
            {
                restPaymentRequestVO.setPaymentBrand(request.getPaymentBrand());
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                restPaymentRequestVO.setPaymentMode(request.getPaymentMode());
            }

            if (functions.isValueNull(request.getTmpl_amount()))
                restPaymentRequestVO.setTmpl_amount(request.getTmpl_amount());
            else
                restPaymentRequestVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                restPaymentRequestVO.setTmpl_currency(request.getTmpl_currency());
            else
                restPaymentRequestVO.setTmpl_currency(request.getCurrency());

            if (functions.isValueNull(request.getAttemptThreeD()))
                restPaymentRequestVO.setAttemptThreeD(request.getAttemptThreeD());

            restPaymentRequestVO.setPaymentType(request.getPaymentType());
            restPaymentRequestVO.setPaymentProvider(request.getPaymentProvider());
            restPaymentRequestVO.setVpa_address(request.getVpa_address());

            restPaymentRequestVO.setDeviceDetailsVO(getDeviceDetailsVO(request.getDeviceDetails()));
        }
        return restPaymentRequestVO;
    }

    public CommonValidatorVO readRequestForExchanger(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO directKitValidatorVO = null;

        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();

            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

            //Authentication Details
            directKitValidatorVO = populateAuthenticationDetailsJSON(directKitValidatorVO, request);

            GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();

            //Transaction Details
            genericAddressDetailsVO.setIp(Functions.getIpAddress(httpServletRequest));

            genericTransDetailsVO.setCurrency(request.getCurrency());
            genericTransDetailsVO.setAmount(request.getAmount());
            genericTransDetailsVO.setOrderId("");
            genericTransDetailsVO.setRedirectUrl("");

            if (functions.isValueNull(request.getTmpl_amount()))
                genericAddressDetailsVO.setTmpl_amount(request.getTmpl_amount());
            else
                genericAddressDetailsVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                genericAddressDetailsVO.setTmpl_currency(request.getTmpl_currency());
            else
                genericAddressDetailsVO.setTmpl_currency(request.getCurrency());

            //Address details
            if (request.getCustomerVO() != null)
            {
                if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
                    directKitValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
            }
            if (functions.isValueNull(request.getPaymentBrand()))
            {
                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }

            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        }
        return directKitValidatorVO;
    }

    public CommonValidatorVO readRequestForCustomerValidation(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO directKitValidatorVO = null;

        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();

            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

            //Authentication Details
            directKitValidatorVO = populateAuthenticationDetailsJSON(directKitValidatorVO, request);

            GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();

            //Transaction Details
            genericAddressDetailsVO.setIp(Functions.getIpAddress(httpServletRequest));

            genericTransDetailsVO.setCurrency(request.getCurrency());
            genericTransDetailsVO.setAmount(request.getAmount());
            genericTransDetailsVO.setOrderId("");
            genericTransDetailsVO.setRedirectUrl("");

            if (functions.isValueNull(request.getTmpl_amount()))
                genericAddressDetailsVO.setTmpl_amount(request.getTmpl_amount());
            else
                genericAddressDetailsVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                genericAddressDetailsVO.setTmpl_currency(request.getTmpl_currency());
            else
                genericAddressDetailsVO.setTmpl_currency(request.getCurrency());

            //Address details
            if (request.getCustomerVO() != null)
            {
                if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
                    directKitValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
            }
            if (functions.isValueNull(request.getPaymentBrand()))
            {

                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }

            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        }
        return directKitValidatorVO;
    }

    public CommonValidatorVO readRequestForRestTransaction(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO directKitValidatorVO  = null;
        List<MarketPlace> marketPlaceList       = null;
        List<MarketPlaceVO> marketPlaceVOList   = null;
        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();

            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO       = new GenericCardDetailsVO();
            RecurringBillingVO recurringBillingVO           = new RecurringBillingVO();
            ReserveField2VO reserveField2VO                 = new ReserveField2VO();
            AccountInfoVO accountInfoVO                     = new AccountInfoVO();
            GenericDeviceDetailsVO genericDeviceDetails     = new GenericDeviceDetailsVO();
            MarketPlaceVO marketPlaceVO                     = new MarketPlaceVO();

            //Authentication Details
            directKitValidatorVO = populateAuthenticationDetailsJSON(directKitValidatorVO, request);

            GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO  = directKitValidatorVO.getMerchantDetailsVO();

            //Transaction Details
            genericAddressDetailsVO.setIp(Functions.getIpAddress(httpServletRequest));

            if(functions.isValueNull(request.getRedirectMethod())){
                genericTransDetailsVO.setRedirectMethod(request.getRedirectMethod());
            }


            genericTransDetailsVO.setOrderId(request.getMerchantTransactionId());
            if (request.getOrderDescriptor() != null)
                genericTransDetailsVO.setOrderDesc(request.getOrderDescriptor());
            else
                genericTransDetailsVO.setOrderDesc(request.getMerchantTransactionId());

            if (functions.isValueNull(request.getNotificationUrl()))
                genericTransDetailsVO.setNotificationUrl(request.getNotificationUrl());

            genericTransDetailsVO.setCurrency(request.getCurrency());
            if(functions.isValueNull(genericTransDetailsVO.getCurrency())&&"JPY".equalsIgnoreCase(genericTransDetailsVO.getCurrency())){
                genericTransDetailsVO.setAmount(String.format("%.2f", Double.parseDouble(request.getAmount())));
                genericTransDetailsVO.setChecksumAmount(request.getAmount());
            }
            else {
                genericTransDetailsVO.setAmount(request.getAmount());
                genericTransDetailsVO.setChecksumAmount(request.getAmount());
            }

            if (functions.isValueNull(request.getCustomerVO().getWalletId()))
                genericTransDetailsVO.setWalletId(request.getCustomerVO().getWalletId());

            if (functions.isValueNull(request.getCustomerVO().getWalletAmount()))
                genericTransDetailsVO.setWalletAmount(request.getCustomerVO().getWalletAmount());

            if (functions.isValueNull(request.getCustomerVO().getWalletCurrency()))
                genericTransDetailsVO.setWalletCurrency(request.getCustomerVO().getWalletCurrency());

            if (functions.isValueNull(request.getPaymentProvider()))
                genericTransDetailsVO.setPaymentProvider(request.getPaymentProvider());


            if (functions.isValueNull(request.getTmpl_amount()))
                genericAddressDetailsVO.setTmpl_amount(request.getTmpl_amount());
            else
                genericAddressDetailsVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                genericAddressDetailsVO.setTmpl_currency(request.getTmpl_currency());
            else
                genericAddressDetailsVO.setTmpl_currency(request.getCurrency());

            //Recurring Type
            if (functions.isValueNull(request.getRecurringType()))
                recurringBillingVO.setRecurringType(request.getRecurringType());

            if (request.getCreateRegistration() != null)
                directKitValidatorVO.setCreateRegistration(request.getCreateRegistration());

            //Address details
            if (request.getCustomerVO() != null)
            {
                genericAddressDetailsVO.setFirstname(request.getCustomerVO().getGivenName());
                genericAddressDetailsVO.setLastname(request.getCustomerVO().getSurname());
                genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());
                if (request.getCustomerVO().getBirthDate() != null)
                    genericAddressDetailsVO.setBirthdate(request.getCustomerVO().getBirthDate());
                if (request.getCustomerVO().getBirthDate() != null)
                    genericAddressDetailsVO.setBirthdate(request.getCustomerVO().getBirthDate());
                if (request.getCustomerVO().getTelnocc() != null)
                    genericAddressDetailsVO.setTelnocc(request.getCustomerVO().getTelnocc());
                if (request.getCustomerVO().getPhone() != null)
                    genericAddressDetailsVO.setPhone(request.getCustomerVO().getPhone());
                if (request.getCustomerVO().getIp() != null)
                    genericAddressDetailsVO.setCardHolderIpAddress(request.getCustomerVO().getIp());
                if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
                    directKitValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
                if (functions.isValueNull(request.getCustomerVO().getLanguage()))
                    genericAddressDetailsVO.setLanguage(request.getCustomerVO().getLanguage());
            }
            if (request.getShippingAddressVO() != null)
            {
                if (!functions.isValueNull(genericAddressDetailsVO.getFirstname()))
                    genericAddressDetailsVO.setFirstname(request.getShippingAddressVO().getGivenName());
                if (!functions.isValueNull(genericAddressDetailsVO.getLastname()))
                    genericAddressDetailsVO.setLastname(request.getShippingAddressVO().getSurname());
                if(functions.isValueNull(request.getShippingAddressVO().getStreet1()))
                    genericAddressDetailsVO.setStreet(request.getShippingAddressVO().getStreet1().trim());
                if(functions.isValueNull(request.getShippingAddressVO().getCity()))
                    genericAddressDetailsVO.setCity(request.getShippingAddressVO().getCity().trim());
                if(functions.isValueNull(request.getShippingAddressVO().getState()))
                    genericAddressDetailsVO.setState(request.getShippingAddressVO().getState().trim());
                if(functions.isValueNull(request.getShippingAddressVO().getPostcode()))
                    genericAddressDetailsVO.setZipCode(request.getShippingAddressVO().getPostcode().trim());
                if(functions.isValueNull(request.getShippingAddressVO().getCountry()))
                    genericAddressDetailsVO.setCountry(request.getShippingAddressVO().getCountry().trim());
            }


            //card details fetching
            if(request.getCardVO()!=null)
            {
                if (functions.isValueNull(request.getCardVO().getNumber()))
                    genericCardDetailsVO.setCardNum(request.getCardVO().getNumber());
                if (functions.isValueNull(request.getCardVO().getCvv()))
                    genericCardDetailsVO.setcVV(request.getCardVO().getCvv());
                if (functions.isValueNull(request.getCardVO().getExpiryMonth()))
                    genericCardDetailsVO.setExpMonth(request.getCardVO().getExpiryMonth());
                if (functions.isValueNull(request.getCardVO().getExpiryYear()))
                    genericCardDetailsVO.setExpYear(request.getCardVO().getExpiryYear());
            }
            //account details fetching
            if (request.getBankAccountVO() != null)
            {
                if (functions.isValueNull(request.getBankAccountVO().getBic()))
                    genericCardDetailsVO.setBIC(request.getBankAccountVO().getBic());
                if (functions.isValueNull(request.getBankAccountVO().getIban()))
                    genericCardDetailsVO.setIBAN(request.getBankAccountVO().getIban());
                if (functions.isValueNull(request.getBankAccountVO().getMandateId()))
                    genericCardDetailsVO.setMandateId(request.getBankAccountVO().getMandateId());
                if (!functions.isValueNull(genericAddressDetailsVO.getCountry()))
                    genericAddressDetailsVO.setCountry(request.getBankAccountVO().getCountry());
                if (functions.isValueNull(request.getBankAccountVO().getBankName()))
                    genericCardDetailsVO.setBankName(request.getBankAccountVO().getBankName());
                if (functions.isValueNull(request.getBankAccountVO().getAccountType()))
                    reserveField2VO.setAccountType(request.getBankAccountVO().getAccountType());
                if (functions.isValueNull(request.getBankAccountVO().getRoutingNumber()))
                    reserveField2VO.setRoutingNumber(request.getBankAccountVO().getRoutingNumber());
                if (functions.isValueNull(request.getBankAccountVO().getAccountNumber()))
                    reserveField2VO.setAccountNumber(request.getBankAccountVO().getAccountNumber());
                if (functions.isValueNull(request.getBankAccountVO().getCheckNumber()))
                    reserveField2VO.setCheckNumber(request.getBankAccountVO().getCheckNumber());
            }

            //normal transaction details
            if (request.getVoucherNumber() != null)
                genericCardDetailsVO.setVoucherNumber(request.getVoucherNumber());
            if (functions.isValueNull(request.getMerchantRedirectUrl()))
                genericTransDetailsVO.setRedirectUrl(request.getMerchantRedirectUrl());
            if (functions.isValueNull(request.getPaymentBrand()))
            {
                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }

            if (functions.isValueNull(request.getAttemptThreeD()))
                directKitValidatorVO.setAttemptThreeD(request.getAttemptThreeD());
            if (request.getThreeDSecureVO() != null)
            {
                if (functions.isValueNull(request.getThreeDSecureVO().getEci()))
                    directKitValidatorVO.setEci(request.getThreeDSecureVO().getEci());
                if (functions.isValueNull(request.getThreeDSecureVO().getVerificationId()))
                    directKitValidatorVO.setVerificationId(request.getThreeDSecureVO().getVerificationId());
                if (functions.isValueNull(request.getThreeDSecureVO().getXid()))
                    directKitValidatorVO.setXid(request.getThreeDSecureVO().getXid());
            }

            if (request.getCardHolderAccountInfoVO() != null)
            {
                if (functions.isValueNull(request.getCardHolderAccountInfoVO().getAccChangeDate()))
                    accountInfoVO.setAccChangeDate(request.getCardHolderAccountInfoVO().getAccChangeDate());
                if (functions.isValueNull(request.getCardHolderAccountInfoVO().getAccActivationDate()))
                    accountInfoVO.setAccActivationDate(request.getCardHolderAccountInfoVO().getAccActivationDate());
                if (functions.isValueNull(request.getCardHolderAccountInfoVO().getAccPwChangeDate()))
                    accountInfoVO.setAccPwChangeDate(request.getCardHolderAccountInfoVO().getAccPwChangeDate());
                if (functions.isValueNull(request.getCardHolderAccountInfoVO().getAddressUseDate()))
                    accountInfoVO.setAddressUseDate(request.getCardHolderAccountInfoVO().getAddressUseDate());
                if (functions.isValueNull(request.getCardHolderAccountInfoVO().getPaymentAccActivationDate()))
                    accountInfoVO.setPaymentAccActivationDate(request.getCardHolderAccountInfoVO().getPaymentAccActivationDate());
            }
            if (request.getDeviceDetailsVO() != null)
            {
                genericDeviceDetails.setUser_Agent(request.getDeviceDetailsVO().getUser_Agent());
                genericDeviceDetails.setAcceptHeader(request.getDeviceDetailsVO().getBrowserAcceptHeader());
                genericDeviceDetails.setBrowserColorDepth(request.getDeviceDetailsVO().getBrowserColorDepth());
                genericDeviceDetails.setBrowserLanguage(request.getDeviceDetailsVO().getBrowserLanguage());
                genericDeviceDetails.setBrowserTimezoneOffset(request.getDeviceDetailsVO().getBrowserTimezoneOffset());
                genericDeviceDetails.setBrowserScreenHeight(request.getDeviceDetailsVO().getBrowserScreenHeight());
                genericDeviceDetails.setBrowserScreenWidth(request.getDeviceDetailsVO().getBrowserScreenWidth());
                genericDeviceDetails.setBrowserJavaEnabled(request.getDeviceDetailsVO().getBrowserJavaEnabled());
            }
            marketPlaceList = request.getMarketPlace();
            if (marketPlaceList != null)
            {
                marketPlaceVOList = new ArrayList<>();
                for (MarketPlace marketPlace : marketPlaceList)
                {
                    marketPlaceVO               = new MarketPlaceVO();
                    String memberId             = marketPlace.getMemberid();
                    String orderid              = marketPlace.getOrderid();
                    String order_description    = marketPlace.getOrder_Description();
                    String amount               = marketPlace.getAmount();
                    marketPlaceVO.setMemberid(memberId);
                    marketPlaceVO.setOrderid(orderid);
                    marketPlaceVO.setOrderDesc(order_description);
                    marketPlaceVO.setAmount(amount);
                    marketPlaceVOList.add(marketPlaceVO);
                }
            }
            directKitValidatorVO.setVpa_address(request.getVpa_address());
            directKitValidatorVO.setTransactionType(request.getPaymentType());
            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
            directKitValidatorVO.setReserveField2VO(reserveField2VO);
            directKitValidatorVO.setAccountInfoVO(accountInfoVO);
            directKitValidatorVO.setDeviceDetailsVO(genericDeviceDetails);
            directKitValidatorVO.setMarketPlaceVOList(marketPlaceVOList);
        }
        return directKitValidatorVO;
    }


    public CommonValidatorVO readRequestForRestTransactionJSON(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO directKitValidatorVO = null;

        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();

            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
            GenericCardDetailsVO genericCardDetailsVO       = new GenericCardDetailsVO();
            RecurringBillingVO recurringBillingVO           = new RecurringBillingVO();
            ReserveField2VO reserveField2VO                 = new ReserveField2VO();

            //Athentication Details
            directKitValidatorVO = populateAuthenticationDetailsJSON(directKitValidatorVO, request);

            GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();

            //Transaction Details
            genericAddressDetailsVO.setIp(Functions.getIpAddress(httpServletRequest));
            genericTransDetailsVO.setOrderId(request.getMerchantTransactionId());
            if (request.getOrderDescriptor() != null)
                genericTransDetailsVO.setOrderDesc(request.getOrderDescriptor());
            else
                genericTransDetailsVO.setOrderDesc(request.getMerchantTransactionId());

            genericTransDetailsVO.setCurrency(request.getCurrency());
            genericTransDetailsVO.setAmount(request.getAmount());

            //Recurring Type
            if (functions.isValueNull(request.getRecurringType()))
                recurringBillingVO.setRecurringType(request.getRecurringType());

            if (request.getCreateRegistration() != null)
                directKitValidatorVO.setCreateRegistration(request.getCreateRegistration());

            //Address details
            if (request.getShippingAddressVO() != null)
            {
                if (!functions.isValueNull(genericAddressDetailsVO.getFirstname()))
                    genericAddressDetailsVO.setFirstname(request.getShippingAddressVO().getGivenName());
                if (!functions.isValueNull(genericAddressDetailsVO.getLastname()))
                    genericAddressDetailsVO.setLastname(request.getShippingAddressVO().getSurname());
                genericAddressDetailsVO.setStreet(request.getShippingAddressVO().getStreet1());
                genericAddressDetailsVO.setCity(request.getShippingAddressVO().getCity());
                genericAddressDetailsVO.setState(request.getShippingAddressVO().getState());
                genericAddressDetailsVO.setZipCode(request.getShippingAddressVO().getPostcode());
                genericAddressDetailsVO.setCountry(request.getShippingAddressVO().getCountry());
            }

            if (request.getCustomerVO() != null)
            {
                genericAddressDetailsVO.setFirstname(request.getCustomerVO().getGivenName());
                genericAddressDetailsVO.setLastname(request.getCustomerVO().getSurname());
                genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());
                if(request.getCustomerVO().getGivenName()!=null)
                {

                }

                if (request.getCustomerVO().getBirthDate() != null)
                    genericAddressDetailsVO.setBirthdate(request.getCustomerVO().getBirthDate());
                if (request.getCustomerVO().getTelnocc() != null)
                    genericAddressDetailsVO.setTelnocc(request.getCustomerVO().getTelnocc());
                if (request.getCustomerVO().getPhone() != null)
                    genericAddressDetailsVO.setPhone(request.getCustomerVO().getPhone());
                if (request.getCustomerVO().getIp() != null)
                    genericAddressDetailsVO.setCardHolderIpAddress(request.getCustomerVO().getIp());
                if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
                    directKitValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
            }


            //card details fetching
            if (functions.isValueNull(request.getCardVO().getNumber()))
                genericCardDetailsVO.setCardNum(request.getCardVO().getNumber());
            if (functions.isValueNull(request.getCardVO().getCvv()))
                genericCardDetailsVO.setcVV(request.getCardVO().getCvv());
            if (functions.isValueNull(request.getCardVO().getExpiryMonth()))
                genericCardDetailsVO.setExpMonth(request.getCardVO().getExpiryMonth());
            if (functions.isValueNull(request.getCardVO().getExpiryYear()))
                genericCardDetailsVO.setExpYear(request.getCardVO().getExpiryYear());

            //account details fetching
            if (request.getBankAccountVO() != null)
            {
                if (functions.isValueNull(request.getBankAccountVO().getBic()))
                    genericCardDetailsVO.setBIC(request.getBankAccountVO().getBic());
                if (functions.isValueNull(request.getBankAccountVO().getIban()))
                    genericCardDetailsVO.setIBAN(request.getBankAccountVO().getIban());
                if (!functions.isValueNull(genericAddressDetailsVO.getCountry()))
                    genericAddressDetailsVO.setCountry(request.getBankAccountVO().getCountry());
                if (functions.isValueNull(request.getBankAccountVO().getBankName()))
                    genericCardDetailsVO.setBankName(request.getBankAccountVO().getBankName());
                if (functions.isValueNull(request.getBankAccountVO().getAccountType()))
                    reserveField2VO.setAccountType(request.getBankAccountVO().getAccountType());
                if (functions.isValueNull(request.getBankAccountVO().getRoutingNumber()))
                    reserveField2VO.setRoutingNumber(request.getBankAccountVO().getRoutingNumber());
                if (functions.isValueNull(request.getBankAccountVO().getAccountNumber()))
                    reserveField2VO.setAccountNumber(request.getBankAccountVO().getAccountNumber());
            }

            //normal transaction details
            if (functions.isValueNull(request.getMerchantRedirectUrl()))
                genericTransDetailsVO.setRedirectUrl(request.getMerchantRedirectUrl());
            if (functions.isValueNull(request.getPaymentBrand()))
            {
                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }
            directKitValidatorVO.setTransactionType(request.getPaymentType());
            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setCardDetailsVO(genericCardDetailsVO);
            directKitValidatorVO.setRecurringBillingVO(recurringBillingVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
            directKitValidatorVO.setReserveField2VO(reserveField2VO);
        }
        return directKitValidatorVO;
    }

    public RestPaymentRequestVO readRequestForRestCaptureCancelRefundRecurring(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        CardVO cardVO = new CardVO();

       /* if (restPaymentRequest != null)*/
        {
            restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
            restPaymentRequestVO.setPaymentType(restPaymentRequest.getPaymentType());
            restPaymentRequestVO.setAmount(restPaymentRequest.getAmount());
            restPaymentRequestVO.setCurrency(restPaymentRequest.getCurrency());
            restPaymentRequestVO.setRecurringType(restPaymentRequest.getRecurringType());
            if (restPaymentRequest.getIdType() != null)
                restPaymentRequestVO.setIdType(restPaymentRequest.getIdType());

            if (restPaymentRequest.getMerchantTransactionId() != null)
                restPaymentRequestVO.setMerchantTransactionId(restPaymentRequest.getMerchantTransactionId());

            if (restPaymentRequest.getMerchantRedirectUrl() != null)
            {
                restPaymentRequestVO.setMerchantRedirectUrl(restPaymentRequest.getMerchantRedirectUrl());
            }

            if (functions.isValueNull(String.valueOf(restPaymentRequest.getCard())) && functions.isValueNull(restPaymentRequest.getCard().getCvv()))
                cardVO.setCvv(restPaymentRequest.getCard().getCvv());

            restPaymentRequestVO.setCardVO(cardVO);
        }

        return restPaymentRequestVO;
    }

    public CommonValidatorVO readRequestForRestCaptureCancelRefundRecurringJSON(RestPaymentRequestVO paymentRequestVO, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO commonValidatorVO     = null;
        GenericCardDetailsVO cardDetailsVO      = new GenericCardDetailsVO();
        List<MarketPlace> marketPlaceList       = null;
        List<MarketPlaceVO> marketPlaceVOList   = null;
        if (paymentRequestVO != null)
        {
            commonValidatorVO                           = new CommonValidatorVO();
            MarketPlaceVO marketPlaceVO                 = new MarketPlaceVO();
            RecurringBillingVO recurringBillingVO       = new RecurringBillingVO();
            GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
            //Athentication Details
            commonValidatorVO = populateAuthenticationDetailsJSON(commonValidatorVO, paymentRequestVO);

            GenericTransDetailsVO transDetailsVO = commonValidatorVO.getTransDetailsVO();
            commonValidatorVO.setTransactionType(paymentRequestVO.getPaymentType());
            transDetailsVO.setAmount(paymentRequestVO.getAmount());
            addressDetailsVO.setIp(functions.getIpAddress(httpServletRequest));

            if (paymentRequestVO.getIdType() != null)
                commonValidatorVO.setIdType(paymentRequestVO.getIdType());

            if (paymentRequestVO.getCardVO() != null && paymentRequestVO.getCardVO().getCvv() != null)
            {
                transDetailsVO.setCvv(paymentRequestVO.getCardVO().getCvv());
                cardDetailsVO.setcVV(paymentRequestVO.getCardVO().getCvv());
            }

            if ("PA".equalsIgnoreCase(commonValidatorVO.getTransactionType()) || "DB".equalsIgnoreCase(commonValidatorVO.getTransactionType()))
            {
                transDetailsVO.setAmount(paymentRequestVO.getAmount());
                transDetailsVO.setCurrency(paymentRequestVO.getCurrency()); //Payment type is CP
                transDetailsVO.setRedirectUrl(paymentRequestVO.getMerchantRedirectUrl());
                recurringBillingVO.setRecurringType(paymentRequestVO.getRecurringType());
                commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
                addressDetailsVO.setIp(functions.getIpAddress(httpServletRequest));
            }
            transDetailsVO.setChecksumAmount(paymentRequestVO.getAmount());
            marketPlaceList = paymentRequestVO.getMarketPlace();
            if (marketPlaceList != null)
            {
                marketPlaceVOList = new ArrayList<>();
                for (MarketPlace marketPlace:marketPlaceList)
                {
                    marketPlaceVO = new MarketPlaceVO();
                    String trackingid = marketPlace.getTrackingid();
                    String amount = marketPlace.getAmount();
                    transactionLogger.error("trackingid-------------->" + trackingid);
                    transactionLogger.error("amount-------------->" + amount);
                    marketPlaceVO.setTrackingid(trackingid);
                    marketPlaceVO.setRefundAmount(amount);
                    marketPlaceVOList.add(marketPlaceVO);
                }
            }
            commonValidatorVO.setTransDetailsVO(transDetailsVO);
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setCardDetailsVO(cardDetailsVO);
            commonValidatorVO.setMarketPlaceVOList(marketPlaceVOList);
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForRestInquiry(RestPaymentRequest paymentRequest, HttpServletRequest servletRequest)
    {
        //CommonValidatorVO commonValidatorVO = null;
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();

        //Athentication Details
        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, paymentRequest);
        addressDetailsVO.setIp(Functions.getIpAddress(servletRequest));
        commonValidatorVO.setTransactionType(paymentRequest.getPaymentType());
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForRestRefund(RestPaymentRequest restPaymentRequest)
    {
        CommonValidatorVO commonValidatorVO = null;
        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();

        if (restPaymentRequest != null)
        {
            commonValidatorVO = new DirectRefundValidatorVO();

            //Athentication Details
            commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, restPaymentRequest);

            genericTransDetailsVO.setAmount(restPaymentRequest.getAmount());
            genericTransDetailsVO.setCurrency(restPaymentRequest.getCurrency());
            if (restPaymentRequest.getMerchantTransactionId() != null)
                genericTransDetailsVO.setOrderId(restPaymentRequest.getMerchantTransactionId());

            commonValidatorVO.setPaymentType(restPaymentRequest.getPaymentType());
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForRestDeleteToken(RestPaymentRequest paymentRequest, HttpServletRequest servletRequest)
    {
        CommonValidatorVO commonValidatorVO = null;

        if (paymentRequest != null)
        {
            commonValidatorVO = new CommonValidatorVO();
            GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();

            //Athentication Details
            commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, paymentRequest);

            GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
            if (paymentRequest.getMerchantTransactionId() != null)
                genericTransDetailsVO.setOrderId(paymentRequest.getMerchantTransactionId());
            addressDetailsVO.setIp(functions.getIpAddress(servletRequest));

            commonValidatorVO.setTransactionType(paymentRequest.getPaymentType());
            commonValidatorVO.setToken(paymentRequest.getId());
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        }
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForTokenTransactionRegistration(RestPaymentRequestVO requestVO, HttpServletRequest servletRequest)
    {
        logger.debug("Reading the request for recurring with token::");
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        ReserveField2VO reserveField2VO = new ReserveField2VO();
        AccountInfoVO accountInfoVO = new AccountInfoVO();

        //Athentication Details
        commonValidatorVO = populateAuthenticationDetailsJSON(commonValidatorVO, requestVO);

        //Transaction details
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        genericTransDetailsVO.setAmount(requestVO.getAmount());
        genericTransDetailsVO.setCurrency(requestVO.getCurrency());
        genericTransDetailsVO.setRedirectUrl(requestVO.getMerchantRedirectUrl());
        genericTransDetailsVO.setOrderDesc("Test");//Hardcode for p4
        addressDetailsVO.setIp(functions.getIpAddress(servletRequest));
        commonValidatorVO.setTerminalId(requestVO.getAuthenticationVO().getTerminalId());
        if (functions.isValueNull(requestVO.getNotificationUrl()))
        {
            genericTransDetailsVO.setNotificationUrl(requestVO.getNotificationUrl());
        }
        if (functions.isValueNull(requestVO.getRedirectMethod()))
        {
            genericTransDetailsVO.setRedirectMethod(requestVO.getRedirectMethod());
        }
        if (functions.isValueNull(requestVO.getInstallment()))
        {
            genericTransDetailsVO.setEmiCount(requestVO.getInstallment());
        }
        if (requestVO.getCardVO() != null)
        {
            if (functions.isValueNull(requestVO.getCardVO().getNumber()))
                genericCardDetailsVO.setCardNum(requestVO.getCardVO().getNumber());
            if (functions.isValueNull(requestVO.getCardVO().getExpiryMonth()))
                genericCardDetailsVO.setExpMonth(requestVO.getCardVO().getExpiryMonth());
            if (functions.isValueNull(requestVO.getCardVO().getExpiryYear()))
                genericCardDetailsVO.setExpYear(requestVO.getCardVO().getExpiryYear());
            if (functions.isValueNull(requestVO.getCardVO().getCvv()))
                genericCardDetailsVO.setcVV(requestVO.getCardVO().getCvv());
        }
        if (functions.isValueNull(requestVO.getPaymentBrand()))
        {
            commonValidatorVO.setPaymentBrand(requestVO.getPaymentBrand());
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(requestVO.getPaymentBrand()));
        }
        else if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
        {
            commonValidatorVO.setPaymentBrand(Functions.getCardType(genericCardDetailsVO.getCardNum()));
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(Functions.getCardType(genericCardDetailsVO.getCardNum())));
        }
        if (functions.isValueNull(requestVO.getPaymentMode()))
        {
            commonValidatorVO.setPaymentMode(requestVO.getPaymentMode());
            commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(requestVO.getPaymentMode()));
        }

        //CVV
        //genericCardDetailsVO.setcVV(requestVO.getCardVO().getCvv());

        //recurringType
        if (functions.isValueNull(requestVO.getRecurringType()))
            recurringBillingVO.setRecurringType(requestVO.getRecurringType());

        if (requestVO.getCustomerVO() != null)
        {
            if (functions.isValueNull(requestVO.getCustomerVO().getGivenName()))
                addressDetailsVO.setFirstname(requestVO.getCustomerVO().getGivenName());
            if (functions.isValueNull(requestVO.getCustomerVO().getSurname()))
                addressDetailsVO.setLastname(requestVO.getCustomerVO().getSurname());
            if (functions.isValueNull(requestVO.getCustomerVO().getTelnocc()))
                addressDetailsVO.setTelnocc(requestVO.getCustomerVO().getTelnocc());
            if (functions.isValueNull(requestVO.getCustomerVO().getPhone()))
                addressDetailsVO.setPhone(requestVO.getCustomerVO().getPhone());
            if (functions.isValueNull(requestVO.getCustomerVO().getEmail()))
                addressDetailsVO.setEmail(requestVO.getCustomerVO().getEmail());
            if (functions.isValueNull(requestVO.getCustomerVO().getIp()))
                addressDetailsVO.setCardHolderIpAddress(requestVO.getCustomerVO().getIp());
            if (functions.isValueNull(requestVO.getCustomerVO().getCustomerId()))
                commonValidatorVO.setCustomerId(requestVO.getCustomerVO().getCustomerId());
            if (functions.isValueNull(requestVO.getCustomerVO().getBirthDate()))
                addressDetailsVO.setBirthdate(requestVO.getCustomerVO().getBirthDate());
        }


        if (requestVO.getShippingAddressVO() != null)
        {
            if (functions.isValueNull(requestVO.getShippingAddressVO().getCountry()))
                addressDetailsVO.setCountry(requestVO.getShippingAddressVO().getCountry());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getState()))
                addressDetailsVO.setState(requestVO.getShippingAddressVO().getState());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getStreet1()))
                addressDetailsVO.setStreet(requestVO.getShippingAddressVO().getStreet1());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getPostcode()))
                addressDetailsVO.setZipCode(requestVO.getShippingAddressVO().getPostcode());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getCity()))
                addressDetailsVO.setCity(requestVO.getShippingAddressVO().getCity());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getGivenName()))
                addressDetailsVO.setFirstname(requestVO.getShippingAddressVO().getGivenName());
            if (functions.isValueNull(requestVO.getShippingAddressVO().getSurname()))
                addressDetailsVO.setLastname(requestVO.getShippingAddressVO().getSurname());

        }

        if (requestVO.getBankAccountVO() != null)
        {
            if (functions.isValueNull(requestVO.getBankAccountVO().getBic()))
                genericCardDetailsVO.setBIC(requestVO.getBankAccountVO().getBic());
            if (functions.isValueNull(requestVO.getBankAccountVO().getIban()))
                genericCardDetailsVO.setIBAN(requestVO.getBankAccountVO().getIban());
            if (functions.isValueNull(requestVO.getBankAccountVO().getCountry()))
                addressDetailsVO.setCountry(requestVO.getBankAccountVO().getCountry());
            if (functions.isValueNull(requestVO.getBankAccountVO().getBankName()))
                genericCardDetailsVO.setBankName(requestVO.getBankAccountVO().getBankName());
            if (functions.isValueNull(requestVO.getBankAccountVO().getAccountType()))
                reserveField2VO.setAccountType(requestVO.getBankAccountVO().getAccountType());
            if (functions.isValueNull(requestVO.getBankAccountVO().getAccountNumber()))
                reserveField2VO.setAccountNumber(requestVO.getBankAccountVO().getAccountNumber());
            if (functions.isValueNull(requestVO.getBankAccountVO().getRoutingNumber()))
                reserveField2VO.setRoutingNumber(requestVO.getBankAccountVO().getRoutingNumber());

        }

        if (requestVO.getCardHolderAccountInfoVO() != null)
        {
            if (functions.isValueNull(requestVO.getCardHolderAccountInfoVO().getAccActivationDate()))
                accountInfoVO.setAccActivationDate(requestVO.getCardHolderAccountInfoVO().getAccActivationDate());
            if (functions.isValueNull(requestVO.getCardHolderAccountInfoVO().getAccChangeDate()))
                accountInfoVO.setAccChangeDate(requestVO.getCardHolderAccountInfoVO().getAccChangeDate());
            if (functions.isValueNull(requestVO.getCardHolderAccountInfoVO().getAccPwChangeDate()))
                accountInfoVO.setAccPwChangeDate(requestVO.getCardHolderAccountInfoVO().getAccPwChangeDate());
            if (functions.isValueNull(requestVO.getCardHolderAccountInfoVO().getAddressUseDate()))
                accountInfoVO.setAddressUseDate(requestVO.getCardHolderAccountInfoVO().getAddressUseDate());
            if (functions.isValueNull(requestVO.getCardHolderAccountInfoVO().getPaymentAccActivationDate()))
                accountInfoVO.setPaymentAccActivationDate(requestVO.getCardHolderAccountInfoVO().getPaymentAccActivationDate());

        }

        commonValidatorVO.setTransactionType(requestVO.getPaymentType());
        commonValidatorVO.setAttemptThreeD(requestVO.getAttemptThreeD());

        commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
        commonValidatorVO.setTransactionType(requestVO.getPaymentType());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        commonValidatorVO.setReserveField2VO(reserveField2VO);
        commonValidatorVO.setAccountInfoVO(accountInfoVO);

        return commonValidatorVO;
    }

    public RestPaymentRequestVO readRequestForTokenRegistrationTransaction(RestPaymentRequest request)
    {
        logger.debug("Reading the request for recurring with token::");
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        //Athentication Details
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
        restPaymentRequestVO.setAmount(request.getAmount());
        restPaymentRequestVO.setCurrency(request.getCurrency());
        restPaymentRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());
        restPaymentRequestVO.setCustomerVO(getCustomerVO(request.getCustomer()));
        restPaymentRequestVO.setShippingAddressVO(getShippingAddressVO(request.getShippingAddress()));
        restPaymentRequestVO.setCardVO(getCardVO(request.getCard()));
        restPaymentRequestVO.setCardHolderAccountInfoVO(getCardholderAccountInfo(request.getCardHolderAccountInfo()));

        restPaymentRequestVO.setRecurringType(request.getRecurringType());
        restPaymentRequestVO.setPaymentType(request.getPaymentType());
        restPaymentRequestVO.setAttemptThreeD(request.getAttemptThreeD());

        restPaymentRequestVO.setPaymentBrand(request.getPaymentBrand());
        restPaymentRequestVO.setPaymentMode(request.getPaymentMode());
        restPaymentRequestVO.setInstallment(request.getInstallment());
        restPaymentRequestVO.setRedirectMethod(request.getRedirectMethod());
        restPaymentRequestVO.setNotificationUrl(request.getNotificationUrl());

        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO readRequestForTokenRegistration(RestPaymentRequest request)
    {
        logger.debug("Reading the request for recurring with token::");
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        //Athentication Details
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
        restPaymentRequestVO.setAmount(request.getAmount());
        restPaymentRequestVO.setCurrency(request.getCurrency());
        restPaymentRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());

        restPaymentRequestVO.setCardVO(getCardVO(request.getCard()));

        restPaymentRequestVO.setRecurringType(request.getRecurringType());
        restPaymentRequestVO.setPaymentType(request.getPaymentType());

        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO readStandAloneTokenRequest(RestPaymentRequest request)
    {
        logger.debug("Reading the request for recurring with token::");
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        //Athentication Details
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
        restPaymentRequestVO.setAmount(request.getAmount());
        restPaymentRequestVO.setCurrency(request.getCurrency());
        restPaymentRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());

        restPaymentRequestVO.setCardVO(getCardVO(request.getCard()));
        restPaymentRequestVO.setBankAccountVO(getBankAccountVO(request.getBankAccount()));

        restPaymentRequestVO.setCustomerVO(getCustomerVO(request.getCustomer()));
        restPaymentRequestVO.setShippingAddressVO(getShippingAddressVO(request.getShippingAddress()));

        restPaymentRequestVO.setRecurringType(request.getRecurringType());
        restPaymentRequestVO.setPaymentType(request.getPaymentType());
        restPaymentRequestVO.setPaymentBrand(request.getPaymentBrand());
        restPaymentRequestVO.setPaymentMode(request.getPaymentMode());
        restPaymentRequestVO.setNotificationUrl(request.getNotificationUrl());

        return restPaymentRequestVO;
    }


/*
    public RestPaymentRequestVO readRequestForTokenRegistration(RestPaymentRequest request)
    {
        logger.debug("Reading the request for recurring with token::");
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        //Athentication Details
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
        restPaymentRequestVO.setAmount(request.getAmount());
        restPaymentRequestVO.setCurrency(request.getCurrency());
        restPaymentRequestVO.setMerchantRedirectUrl(request.getMerchantRedirectUrl());

        restPaymentRequestVO.setCardVO(getCardVO(request.getCard()));

        restPaymentRequestVO.setRecurringType(request.getRecurringType());
        restPaymentRequestVO.setPaymentType(request.getPaymentType());

        return restPaymentRequestVO;
    }
*/



/*
    public CommonValidatorVO readRequestForTokenRegistration(RestPaymentRequest request, HttpServletRequest servletRequest)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        GenericCardDetailsVO cardDetailsVO = new GenericCardDetailsVO();
        ReserveField2VO reserveField2VO = new ReserveField2VO();

        //Athentication Details
        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO,request);
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

        //Fetching CardDetails
        if(functions.isValueNull(request.getCard().getNumber()))
            cardDetailsVO.setCardNum(request.getCard().getNumber());
        if(functions.isValueNull(request.getCard().getCvv()))
            cardDetailsVO.setcVV(request.getCard().getCvv());
        if(functions.isValueNull((request.getCard().getExpiryMonth())))
            cardDetailsVO.setExpMonth(request.getCard().getExpiryMonth());
        if(functions.isValueNull(request.getCard().getExpiryYear()))
            cardDetailsVO.setExpYear(request.getCard().getExpiryYear());

        //fetching account details
        if(functions.isValueNull(request.getBankAccount().getBic()))
            cardDetailsVO.setBIC(request.getBankAccount().getBic());
        if(functions.isValueNull(request.getBankAccount().getIban()))
            cardDetailsVO.setIBAN(request.getBankAccount().getIban());
        if (functions.isValueNull(request.getBankAccount().getAccountNumber()))
            reserveField2VO.setAccountNumber(request.getBankAccount().getAccountNumber());
        if (functions.isValueNull(request.getBankAccount().getRoutingNumber()))
            reserveField2VO.setRoutingNumber(request.getBankAccount().getRoutingNumber());
        if (functions.isValueNull(request.getBankAccount().getAccountType()))
            reserveField2VO.setAccountType(request.getBankAccount().getAccountType());

        genericAddressDetailsVO.setCountry(request.getBillingAddress().getCountry());
        genericAddressDetailsVO.setCity(request.getBillingAddress().getCity());
        genericAddressDetailsVO.setState(request.getBillingAddress().getState());
        genericAddressDetailsVO.setZipCode(request.getBillingAddress().getPostcode());
        genericAddressDetailsVO.setStreet(request.getBillingAddress().getStreet());
        genericAddressDetailsVO.setFirstname(request.getBillingAddress().getGivenName());
        genericAddressDetailsVO.setLastname(request.getBillingAddress().getSurname());
        cardDetailsVO.setCardHolderName(request.getBillingAddress().getGivenName() + " " + request.getBillingAddress().getSurname());
        genericTransDetailsVO.setCurrency(request.getCurrency());
        genericAddressDetailsVO.setLanguage(request.getBillingAddress().getLanguage());

        if(request.getCustomer() != null)
        {
            if(request.getCustomer().getBirthDate() != null)
                genericAddressDetailsVO.setBirthdate(request.getCustomer().getBirthDate());
            if(request.getCustomer().getTelnocc() != null)
                genericAddressDetailsVO.setTelnocc(request.getCustomer().getTelnocc());
            if(request.getCustomer().getEmail() != null)
                genericAddressDetailsVO.setEmail(request.getCustomer().getEmail());
            if(request.getCustomer().getGivenName() != null)
                genericAddressDetailsVO.setFirstname(request.getCustomer().getGivenName());
            if(request.getCustomer().getSurname() != null)
                genericAddressDetailsVO.setLastname(request.getCustomer().getSurname());
            if(request.getCustomer().getPhone() != null)
            {
                genericAddressDetailsVO.setPhone(request.getCustomer().getPhone());
            }
            if (request.getCustomer().getIp() != null)
            {
                genericAddressDetailsVO.setIp(functions.getIpAddress(servletRequest));
                genericAddressDetailsVO.setCardHolderIpAddress(request.getCustomer().getIp());
            }
        }

        if(functions.isValueNull(request.getPaymentBrand()))
        {
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand())); //PaymentBrand e.g VISA
            commonValidatorVO.setPaymentBrand(request.getPaymentBrand());
        }
        if(functions.isValueNull(request.getPaymentMode()))
        {
            commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode())); //PaymentBrand e.g CC
            commonValidatorVO.setPaymentMode(request.getPaymentMode());
        }
        if(functions.isValueNull(request.getCustomer().getCustomerId()))
            commonValidatorVO.setCustomerId(request.getCustomer().getCustomerId());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setCardDetailsVO(cardDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setReserveField2VO(reserveField2VO);

        return commonValidatorVO;
    }
*/

    public CommonValidatorVO readGetCardsAndAccountRequest(RestPaymentRequestVO request, HttpServletRequest servletRequest)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        if (request != null)
        {
            populateAuthenticationDetailsJSON(commonValidatorVO, request);
            addressDetailsVO.setIp(functions.getIpAddress(servletRequest));
            if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
            {
                commonValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
            }
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        }
        return commonValidatorVO;
    }

    public RestPaymentRequestVO readGetCardsAndAccountRequestForm(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        if (restPaymentRequest != null)
        {
            restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
            if (restPaymentRequest.getCustomer() != null)
            {
                restPaymentRequestVO.setCustomerVO(getCustomerVO(restPaymentRequest.getCustomer()));
            }
        }
        return restPaymentRequestVO;
    }

    public CommonValidatorVO readRequestForMerchantSignUpAndcardHolderRegistration(RestPaymentRequest request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

        commonValidatorVO = populateAuthenticationDetails(commonValidatorVO, request);
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        //Authentication Details
        if (functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
            merchantDetailsVO.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
        if (request.getMerchant().getLoginName() != null)
            merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        if (request.getMerchant().getNewPassword() != null)
            merchantDetailsVO.setNewPassword(request.getMerchant().getNewPassword());
        if (request.getMerchant().getConPassword() != null)
            merchantDetailsVO.setConPassword(request.getMerchant().getConPassword());
        if (request.getMerchant().getCompanyName() != null)
            merchantDetailsVO.setCompany_name(request.getMerchant().getCompanyName());
        if (request.getMerchant().getWebsite() != null)
            merchantDetailsVO.setWebsite(request.getMerchant().getWebsite());

        genericAddressDetailsVO.setFirstname(request.getCustomer().getGivenName());
        genericAddressDetailsVO.setLastname(request.getCustomer().getSurname());
        if (request.getCustomer().getSex() != null)
            genericAddressDetailsVO.setSex(request.getCustomer().getSex());

        genericAddressDetailsVO.setEmail(request.getCustomer().getEmail());
        if (request.getCustomer().getPhone() != null)
            genericAddressDetailsVO.setPhone(request.getCustomer().getPhone());
        if (request.getShippingAddress().getCountry() != null)
            genericAddressDetailsVO.setCountry(request.getShippingAddress().getCountry());
        if (request.getShippingAddress().getPostcode() != null)
            genericAddressDetailsVO.setZipCode(request.getShippingAddress().getPostcode());
        if (request.getCustomer().getBirthDate() != null)
            genericAddressDetailsVO.setBirthdate(request.getCustomer().getBirthDate());
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);


        return commonValidatorVO;
    }


    private CommonValidatorVO populateAuthenticationDetails(CommonValidatorVO commonValidatorVO, RestPaymentRequest request)
    {
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        if (functions.isValueNull(request.getAuthentication().getMemberId()))
            genericMerchantDetailsVO.setMemberId(request.getAuthentication().getMemberId());
        if (functions.isValueNull(request.getAuthentication().getPartnerId()))
            partnerDetailsVO.setPartnerId(request.getAuthentication().getPartnerId());
        if (functions.isValueNull(request.getAuthentication().getChecksum()))
            transDetailsVO.setChecksum(request.getAuthentication().getChecksum());
        if (functions.isValueNull(request.getAuthentication().getTerminalId()))
            commonValidatorVO.setTerminalId(request.getAuthentication().getTerminalId());

        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }

    private AuthenticationVO getAuthenticationVO(Authentication authentication)
    {
        AuthenticationVO authenticationVO = new AuthenticationVO();
        authenticationVO.setPartnerId(authentication.getPartnerId());
        authenticationVO.setChecksum(authentication.getChecksum());
        authenticationVO.setTerminalId(authentication.getTerminalId());
        authenticationVO.setMemberId(authentication.getMemberId());
        authenticationVO.setPassword(authentication.getPassword());
        authenticationVO.setsKey(authentication.getsKey());
        authenticationVO.setAccountId(authentication.getAccountId());

        return authenticationVO;
    }
    private DeviceDetailsVO getDeviceDetailsVO(DeviceDetails deviceDetails)
    {
        DeviceDetailsVO deviceDetailsVO = new DeviceDetailsVO();
        deviceDetailsVO.setUser_Agent(deviceDetails.getUser_Agent());
        deviceDetailsVO.setBrowserAcceptHeader(deviceDetails.getBrowserAcceptHeader());
        deviceDetailsVO.setBrowserColorDepth(deviceDetails.getBrowserColorDepth());
        deviceDetailsVO.setBrowserLanguage(deviceDetails.getBrowserLanguage());
        deviceDetailsVO.setBrowserTimezoneOffset(deviceDetails.getBrowserTimezoneOffset());
        deviceDetailsVO.setBrowserScreenHeight(deviceDetails.getBrowserScreenHeight());
        deviceDetailsVO.setBrowserScreenWidth(deviceDetails.getBrowserScreenWidth());
        deviceDetailsVO.setBrowserJavaEnabled(deviceDetails.getBrowserJavaEnabled());

        return deviceDetailsVO;
    }

    private MerchantVO getMerchantVO(Merchant merchant)
    {
        MerchantVO merchantVO = new MerchantVO();
        if (merchant.getLoginName() != null)
            merchantVO.setLoginName(merchant.getLoginName());
        if (merchant.getNewPassword() != null)
            merchantVO.setNewPassword(merchant.getNewPassword());
        if (merchant.getEmail() != null)
            merchantVO.setEmail(merchant.getEmail());

        return merchantVO;
    }


    private ShippingAddressVO getShippingAddressVO(ShippingAddress shippingAddress)
    {
        ShippingAddressVO shippingAddressVO = new ShippingAddressVO();

        if (shippingAddress != null)
        {
            if (functions.isValueNull(shippingAddress.getGivenName()))
                shippingAddressVO.setGivenName(shippingAddress.getGivenName());
            if (functions.isValueNull(shippingAddress.getSurname()))
                shippingAddressVO.setSurname(shippingAddress.getSurname());
            shippingAddressVO.setStreet1(shippingAddress.getStreet1());
            shippingAddressVO.setCity(shippingAddress.getCity());
            shippingAddressVO.setState(shippingAddress.getState());
            shippingAddressVO.setPostcode(shippingAddress.getPostcode());
            shippingAddressVO.setCountry(shippingAddress.getCountry());

        }

        return shippingAddressVO;
    }

    private CardHolderAccountInfoVO getCardholderAccountInfo(CardHolderAccountInfo cardHolderAccountInfo)
    {
        CardHolderAccountInfoVO cardHolderAccountInfoVO = new CardHolderAccountInfoVO();

        if (cardHolderAccountInfoVO != null)
        {
            if (functions.isValueNull(cardHolderAccountInfo.getAccChangeDate()))
                cardHolderAccountInfoVO.setAccChangeDate(cardHolderAccountInfo.getAccChangeDate());
            if (functions.isValueNull(cardHolderAccountInfo.getAccActivationDate()))
                cardHolderAccountInfoVO.setAccActivationDate(cardHolderAccountInfo.getAccActivationDate());
            if (functions.isValueNull(cardHolderAccountInfo.getAccPwChangeDate()))
                cardHolderAccountInfoVO.setAccPwChangeDate(cardHolderAccountInfo.getAccPwChangeDate());
            if (functions.isValueNull(cardHolderAccountInfo.getAddressUseDate()))
                cardHolderAccountInfoVO.setAddressUseDate(cardHolderAccountInfo.getAddressUseDate());
            if (functions.isValueNull(cardHolderAccountInfo.getPaymentAccActivationDate()))
                cardHolderAccountInfoVO.setPaymentAccActivationDate(cardHolderAccountInfo.getPaymentAccActivationDate());
        }
        return cardHolderAccountInfoVO;
    }

    private CardVO getCardVO(Card card)
    {
        CardVO cardVO = new CardVO();

        if(functions.isValueNull(card.getNumber()))
            cardVO.setNumber(card.getNumber());
        if (functions.isValueNull(card.getCvv()))
            cardVO.setCvv(card.getCvv());
        if (functions.isValueNull(card.getExpiryMonth()))
            cardVO.setExpiryMonth(card.getExpiryMonth());
        if (functions.isValueNull(card.getExpiryYear()))
            cardVO.setExpiryYear(card.getExpiryYear());
        if(functions.isValueNull(card.getFirstsix()))
            cardVO.setFirstsix(card.getFirstsix());
        if(functions.isValueNull(card.getLastfour()))
            cardVO.setLastfour(card.getLastfour());

        return cardVO;
    }

    private BankAccountVO getBankAccountVO(BankAccount bankAccount)
    {
        BankAccountVO bankAccountVO = new BankAccountVO();
        if (functions.isValueNull(bankAccount.getBic()))
            bankAccountVO.setBic(bankAccount.getBic());
        if (functions.isValueNull(bankAccount.getIban()))
            bankAccountVO.setIban(bankAccount.getIban());
        if (functions.isValueNull(bankAccount.getMandateId()))
            bankAccountVO.setMandateId(bankAccount.getMandateId());
        if (functions.isValueNull(bankAccount.getCountry()))
            bankAccountVO.setCountry(bankAccount.getCountry());
        if (functions.isValueNull(bankAccount.getBankName()))
            bankAccountVO.setBankName(bankAccount.getBankName());
        if (functions.isValueNull(bankAccount.getAccountType()))
            bankAccountVO.setAccountType(bankAccount.getAccountType());
        if (functions.isValueNull(bankAccount.getAccountNumber()))
            bankAccountVO.setAccountNumber(bankAccount.getAccountNumber());
        if (functions.isValueNull(bankAccount.getRoutingNumber()))
            bankAccountVO.setRoutingNumber(bankAccount.getRoutingNumber());
        if (functions.isValueNull(bankAccount.getCheckNumber()))
            bankAccountVO.setCheckNumber(bankAccount.getCheckNumber());
        if (functions.isValueNull(bankAccount.getBankAccountName()))
            bankAccountVO.setBankAccountName(bankAccount.getBankAccountName());
        if (functions.isValueNull(bankAccount.getBankIfsc()))
            bankAccountVO.setBankIfsc(bankAccount.getBankIfsc());
        if (functions.isValueNull(bankAccount.getTransferType()))
            bankAccountVO.setTransferType(bankAccount.getTransferType());
        if (functions.isValueNull(bankAccount.getBankAccountNumber()))
            bankAccountVO.setBankAccountNumber(bankAccount.getBankAccountNumber());
        if (functions.isValueNull(bankAccount.getBranchName()))
            bankAccountVO.setBranchName(bankAccount.getBranchName());
        if (functions.isValueNull(bankAccount.getBranchCode()))
            bankAccountVO.setBranchCode(bankAccount.getBranchCode());
        if (functions.isValueNull(bankAccount.getBankCode()))
            bankAccountVO.setBankCode(bankAccount.getBankCode());

        return bankAccountVO;
    }

    private ThreeDSecureVO getThreeDSecureVO(ThreeDSecure threeDSecure)
    {
        ThreeDSecureVO threeDSecureVO = new ThreeDSecureVO();

        if (threeDSecure != null)
        {
            if (functions.isValueNull(threeDSecure.getEci()))
                threeDSecureVO.setEci(threeDSecure.getEci());
            if (functions.isValueNull(threeDSecure.getXid()))
                threeDSecureVO.setXid(threeDSecure.getXid());
            if (functions.isValueNull(threeDSecure.getVerificationId()))
                threeDSecureVO.setVerificationId(threeDSecure.getVerificationId());

        }
        return threeDSecureVO;

    }

    private CustomerVO getCustomerVO(Customer customer)
    {
        CustomerVO customerVO = new CustomerVO();

        if (customer != null)
        {
            customerVO.setGivenName(customer.getGivenName());
            customerVO.setSurname(customer.getSurname());
            customerVO.setEmail(customer.getEmail());
            customerVO.setMobile(customer.getMobile());
            customerVO.setSmsOtp(customer.getSmsOtp());
            customerVO.setEmailOtp(customer.getEmailOTP());

            if (functions.isValueNull(customer.getBirthDate()))
                customerVO.setBirthDate(customer.getBirthDate());
            if (functions.isValueNull(customer.getTelnocc()))
                customerVO.setTelnocc(customer.getTelnocc());
            if (functions.isValueNull(customer.getPhone()))
                customerVO.setPhone(customer.getPhone());
            if (functions.isValueNull(customer.getIp()))
                customerVO.setIp(customer.getIp());
            if (functions.isValueNull(customer.getCustomerId()))
                customerVO.setCustomerId(customer.getCustomerId());
            if (functions.isValueNull(customer.getId()))
                customerVO.setId(customer.getId());
            if (functions.isValueNull(customer.getCustomerBankId()))
                customerVO.setCustomerBankId(customer.getCustomerBankId());
            if (functions.isValueNull(customer.getLanguage()))
                customerVO.setLanguage(customer.getLanguage());
            if (functions.isValueNull(customer.getWalletId()))
                customerVO.setWalletId(customer.getWalletId());
            if (functions.isValueNull(customer.getWalletAmount()))
                customerVO.setWalletAmount(customer.getWalletAmount());
            if (functions.isValueNull(customer.getWalletCurrency()))
                customerVO.setWalletCurrency(customer.getWalletCurrency());

            if (functions.isValueNull(customer.getCountry()))
                customerVO.setCountry(customer.getCountry());
        }
        return customerVO;

    }

    private CommonValidatorVO populateAuthenticationDetailsJSON(CommonValidatorVO commonValidatorVO, RestPaymentRequestVO request)
    {
        MerchantDetailsVO genericMerchantDetailsVO  = new MerchantDetailsVO();
        PartnerDetailsVO partnerDetailsVO           = new PartnerDetailsVO();
        GenericTransDetailsVO transDetailsVO        = new GenericTransDetailsVO();

        if (functions.isValueNull(request.getAuthenticationVO().getMemberId()))
            genericMerchantDetailsVO.setMemberId(request.getAuthenticationVO().getMemberId());
        if (functions.isValueNull(request.getAuthenticationVO().getPartnerId()))
            partnerDetailsVO.setPartnerId(request.getAuthenticationVO().getPartnerId());
        if (functions.isValueNull(request.getAuthenticationVO().getChecksum()))
            transDetailsVO.setChecksum(request.getAuthenticationVO().getChecksum());
        if (functions.isValueNull(request.getAuthenticationVO().getTerminalId()))
            commonValidatorVO.setTerminalId(request.getAuthenticationVO().getTerminalId());
        if (functions.isValueNull(request.getAuthenticationVO().getAccountId()))
            commonValidatorVO.setAccountId(request.getAuthenticationVO().getAccountId());

        if (request.getExpDateOffset() != null)
            genericMerchantDetailsVO.setExpDateOffset(request.getExpDateOffset());

        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO getTransactionRequest(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        //GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        populateAuthenticationDetailsJSON(commonValidatorVO, request);
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        PaginationVO paginationVO = new PaginationVO();
        if (request.getPaginationVO() != null)
        {
            paginationVO.setStartdate(request.getPaginationVO().getFromdate());
            paginationVO.setEnddate(request.getPaginationVO().getTodate());
        }
        if (request.getMerchant()!=null)
        {
            merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setInvoiceAction(request.getActionType());
        commonValidatorVO.getTransDetailsVO().setCurrency(request.getCurrency());
        commonValidatorVO.setPaginationVO(paginationVO);


        return commonValidatorVO;

    }

    public RestPaymentRequestVO readSMSCode(RestPaymentRequest request)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setPaymentId(request.getPaymentId());
        transactionLogger.debug("payment id from request ---" + request.getPaymentId());
        restPaymentRequestVO.setSmsCode(request.getSmsCode());
        transactionLogger.debug("sms code from request ---" + request.getSmsCode());

        return restPaymentRequestVO;
    }

    public CommonValidatorVO readSMSCodeJSON(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setTrackingid(request.getPaymentId());
        transactionLogger.debug("payment id from request ---" + request.getPaymentId());
        commonValidatorVO.setSmsCode(request.getSmsCode());
        transactionLogger.debug("sms code from request ---" + request.getSmsCode());
        return commonValidatorVO;
    }

    public RestPaymentRequestVO readProcessGetPaymentAndCardType(RestPaymentRequest request)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
        return restPaymentRequestVO;
    }
    public CommonValidatorVO readProcessGetPaymentAndCardTypeJSON(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        if (request.getAuthenticationVO() != null)
        {
            merchantDetailsVO.setMemberId(request.getAuthenticationVO().getMemberId());
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }

    public RestPaymentRequestVO readProcessSaveTransactionReceipt(RestPaymentRequest request)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setId(request.getPaymentId());
        restPaymentRequestVO.setTransactionReceipt(request.getTransactionReceipt());
        return restPaymentRequestVO;
    }

    public CommonValidatorVO readProcessSaveTransactionReceiptJSON(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        commonValidatorVO.setTrackingid(request.getId());
        if (!functions.isValueNull(commonValidatorVO.getTrackingid()))
            commonValidatorVO.setTrackingid(request.getPaymentId());
        transDetailsVO.setTransactionReceipt(request.getTransactionReceipt());
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO readPayoutRequestJSON(RestPaymentRequestVO requestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        GenericAddressDetailsVO addressDetailsVO    = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();
        populateAuthenticationDetailsJSON(commonValidatorVO, requestVO);
        GenericTransDetailsVO transDetailsVO        = commonValidatorVO.getTransDetailsVO();
        ReserveField2VO reserveField2VO             = new ReserveField2VO();
        transDetailsVO.setOrderId(requestVO.getMerchantTransactionId());
        transDetailsVO.setOrderDesc(requestVO.getOrderDescriptor());
        transDetailsVO.setAmount(requestVO.getAmount());
        if (requestVO.getPaymentId() != null)
            commonValidatorVO.setTrackingid(requestVO.getPaymentId());
        else if (requestVO.getId() != null)
            commonValidatorVO.setTrackingid(requestVO.getId());
        if (requestVO.getMerchant() != null)
        {
            if (functions.isValueNull(requestVO.getMerchant().getEmail()))
                addressDetailsVO.setEmail(requestVO.getMerchant().getEmail());

        }
        commonValidatorVO.setCustAccount(requestVO.getCustAccount());
        /*if (requestVO.getMerchant().getEmail() != null)
        addressDetailsVO.setEmail(requestVO.getMerchant().getEmail());*/

        if (requestVO.getCustomerVO() != null)
        {

            if(functions.isValueNull(requestVO.getCustomerVO().getEmail())){
                addressDetailsVO.setEmail(requestVO.getCustomerVO().getEmail());
                commonValidatorVO.setCustEmail(requestVO.getCustomerVO().getEmail());
            }
            //addressDetailsVO.setEmail(requestVO.getCustomerVO().getEmail());
            commonValidatorVO.setCustomerBankId(requestVO.getCustomerVO().getCustomerBankId());
            commonValidatorVO.setCustomerId(requestVO.getCustomerVO().getId());
            transDetailsVO.setWalletId(requestVO.getCustomerVO().getWalletId());
            transDetailsVO.setWalletAmount(requestVO.getCustomerVO().getWalletAmount());
            transDetailsVO.setWalletCurrency(requestVO.getCustomerVO().getWalletCurrency());

            if (functions.isValueNull(requestVO.getCustomerVO().getPhone())){
                addressDetailsVO.setPhone(requestVO.getCustomerVO().getPhone());
            }

            if (functions.isValueNull(requestVO.getCustomerVO().getTelnocc())){
                addressDetailsVO.setPhoneCountryCode(requestVO.getCustomerVO().getTelnocc());
            }

            if (functions.isValueNull(requestVO.getCustomerVO().getTelnocc())){
                addressDetailsVO.setTelnocc(requestVO.getCustomerVO().getTelnocc());
            }

            if (functions.isValueNull(requestVO.getCustomerVO().getCountry())){
                addressDetailsVO.setCountry(requestVO.getCustomerVO().getCountry());
            }
        }


        if (functions.isValueNull(requestVO.getPaymentBrand()))
        {
            commonValidatorVO.setPaymentBrand(requestVO.getPaymentBrand());
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(requestVO.getPaymentBrand()));
        }
        if (functions.isValueNull(requestVO.getPaymentMode()))
        {
            commonValidatorVO.setPaymentMode(requestVO.getPaymentMode());
            commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(requestVO.getPaymentMode()));
        }

        if (requestVO.getCurrency() != null)
            transDetailsVO.setCurrency(requestVO.getCurrency());

        if (requestVO.getNotificationUrl() != null)
            transDetailsVO.setNotificationUrl(requestVO.getNotificationUrl());

        if (requestVO.getTmpl_amount() != null)
            addressDetailsVO.setTmpl_amount(requestVO.getTmpl_amount());

        if (requestVO.getTmpl_currency() != null)
            addressDetailsVO.setTmpl_currency(requestVO.getTmpl_currency());
        if (functions.isValueNull(requestVO.getPayoutType()))
        {
            commonValidatorVO.setPayoutType(requestVO.getPayoutType());
        }

        if (requestVO.getBankAccountVO()!=null)
        {
            reserveField2VO.setBankIfsc(requestVO.getBankAccountVO().getBankIfsc());
            reserveField2VO.setBankAccountNumber(requestVO.getBankAccountVO().getBankAccountNumber());
            reserveField2VO.setBankAccountName(requestVO.getBankAccountVO().getBankAccountName());
            reserveField2VO.setTransferType(requestVO.getBankAccountVO().getTransferType());


            if(requestVO.getBankAccountVO().getAccountNumber() != null){
                reserveField2VO.setAccountNumber(requestVO.getBankAccountVO().getAccountNumber());
            }
            if(requestVO.getBankAccountVO().getBankName() != null){
                reserveField2VO.setBankName(requestVO.getBankAccountVO().getBankName());
            }
            if(requestVO.getBankAccountVO().getBranchName() != null){
                reserveField2VO.setBranchName(requestVO.getBankAccountVO().getBranchName());
            }
            if(requestVO.getBankAccountVO().getBankCode() != null){
                reserveField2VO.setBankCode(requestVO.getBankAccountVO().getBankCode());
            }
            if(requestVO.getBankAccountVO().getAccountType() != null){
                reserveField2VO.setAccountType(requestVO.getBankAccountVO().getAccountType());
            }
            if(requestVO.getBankAccountVO().getBranchCode() != null){
                reserveField2VO.setBranchCode(requestVO.getBankAccountVO().getBranchCode());
            }
        }

        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        commonValidatorVO.setReserveField2VO(reserveField2VO);

        return commonValidatorVO;
    }

    public RestPaymentRequestVO readPayoutRequest(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        /*if (functions.isValueNull(restPaymentRequest.getMerchant().getEmail()))
            restPaymentRequestVO.getMerchant().setEmail(restPaymentRequest.getMerchant().getEmail());*/
        restPaymentRequestVO.setMerchantTransactionId(restPaymentRequest.getMerchantTransactionId());
        restPaymentRequestVO.setOrderDescriptor(restPaymentRequest.getOrderDescriptor());
        restPaymentRequestVO.setAmount(restPaymentRequest.getAmount());
        restPaymentRequestVO.setId(restPaymentRequest.getId());
        restPaymentRequestVO.setCustAccount(restPaymentRequest.getCustAccount());
        restPaymentRequestVO.setMerchant(getMerchantVO(restPaymentRequest.getMerchant()));
        if (restPaymentRequest.getCurrency() != null)
            restPaymentRequestVO.setCurrency(restPaymentRequest.getCurrency());

        if (restPaymentRequest.getPaymentBrand() != null)
            restPaymentRequestVO.setPaymentBrand(restPaymentRequest.getPaymentBrand());

        if (restPaymentRequest.getPaymentMode() != null)
            restPaymentRequestVO.setPaymentMode(restPaymentRequest.getPaymentMode());

        if (restPaymentRequest.getPaymentId() != null)
            restPaymentRequestVO.setPaymentId(restPaymentRequest.getPaymentId());

        if (restPaymentRequest.getExpDateOffset() != null)
            restPaymentRequestVO.setExpDateOffset(restPaymentRequest.getExpDateOffset());

        if (restPaymentRequest.getTmpl_amount() != null)
            restPaymentRequestVO.setTmpl_amount(restPaymentRequest.getTmpl_amount());

        if (restPaymentRequest.getTmpl_currency() != null)
            restPaymentRequestVO.setTmpl_currency(restPaymentRequest.getTmpl_currency());

        if (restPaymentRequest.getNotificationUrl() != null)
            restPaymentRequestVO.setNotificationUrl(restPaymentRequest.getNotificationUrl());


        if (restPaymentRequest.getCustomer() != null)
        {
            restPaymentRequestVO.setCustomerVO(getCustomerVO(restPaymentRequest.getCustomer()));
        }
        if (restPaymentRequest.getPayoutType() != null)
        {
            restPaymentRequestVO.setPayoutType(restPaymentRequest.getPayoutType());
        }
        if (restPaymentRequest.getBankAccount() !=null)
        {
            restPaymentRequestVO.setBankAccountVO(getBankAccountVO(restPaymentRequest.getBankAccount()));
        }
        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO getTransactionRequestForm(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setMerchant(getMerchantVO(restPaymentRequest.getMerchant()));
        restPaymentRequestVO.setActionType(restPaymentRequest.getActionType());
        //restPaymentRequestVO.setPaginationVO(getpaginationVO(restPaymentRequest.getPagination()));

        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO getAuthTokenRequest(RestPaymentRequest authRequest)
    {
        RestPaymentRequestVO authRequestVO = new RestPaymentRequestVO();

        authRequestVO.setMerchant(getMerchantVO(authRequest.getMerchant()));
        authRequestVO.setAuthenticationVO(getAuthenticationVO(authRequest.getAuthentication()));

        return authRequestVO;
    }

    public RestPaymentRequestVO getPartnerAuthTokenRequest(RestPaymentRequest authRequest)
    {
        RestPaymentRequestVO authRequestVO = new RestPaymentRequestVO();

        authRequestVO.setPartnerUserName(authRequest.getPartnerUserName());
        authRequestVO.setAuthenticationVO(getAuthenticationVO(authRequest.getAuthentication()));

        return authRequestVO;
    }


    public RestPaymentRequestVO getNewAuthTokenRequest(RestPaymentRequest request)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        AuthenticationVO authenticationVO = new AuthenticationVO();
        restPaymentRequestVO.setAuthToken(request.getAuthToken());
        authenticationVO.setPartnerId(request.getAuthentication().getPartnerId());
        restPaymentRequestVO.setAuthenticationVO(authenticationVO);

        return restPaymentRequestVO;
    }

    private com.transaction.vo.restVO.RequestVO.PaginationVO getpaginationVO(Pagination pagination)

    {
        com.transaction.vo.restVO.RequestVO.PaginationVO paginationVo = new com.transaction.vo.restVO.RequestVO.PaginationVO();
        paginationVo.setStart(pagination.getStart());
        paginationVo.setEnd(pagination.getEnd());
        paginationVo.setFromdate(pagination.getFromdate());
        paginationVo.setTodate(pagination.getTodate());
        paginationVo.setPageno(pagination.getPageno());
        paginationVo.setRecords(pagination.getRecords());

        return paginationVo;
    }

    public CommonValidatorVO readRequestForMerchantLogin(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setLogin(request.getMerchant().getLoginName());
        merchantDetailsVO.setPassword(request.getMerchant().getNewPassword());
        merchantDetailsVO.setKey(request.getAuthenticationVO().getsKey());
        commonValidatorVO.setParetnerId(request.getAuthenticationVO().getPartnerId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForPartnerLogin(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        commonValidatorVO.setPartnerName(request.getPartnerUserName());
        partnerDetailsVO.setPartnerKey(request.getAuthenticationVO().getsKey());
        commonValidatorVO.setParetnerId(request.getAuthenticationVO().getPartnerId());

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForgetNewAuthToken(RestPaymentRequestVO requestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        commonValidatorVO.setAuthToken(requestVO.getAuthToken());
        commonValidatorVO.setParetnerId(requestVO.getAuthenticationVO().getPartnerId());

        return commonValidatorVO;
    }

    public CommonValidatorVO readRequestForWalletDetails(RestPaymentRequestVO paymentRequestVO)
    {
        transactionLogger.error("IN readRequestForWalletDetails ---");
        CommonValidatorVO commonValidatorVO = null;
        if (paymentRequestVO != null)
        {
            commonValidatorVO = new CommonValidatorVO();
            GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

            if (paymentRequestVO.getAmount() != null)
                transDetailsVO.setAmount(paymentRequestVO.getAmount());
            if (paymentRequestVO.getCurrency() != null)
                transDetailsVO.setCurrency(paymentRequestVO.getCurrency());
            if (paymentRequestVO.getId() != null)
                commonValidatorVO.setTrackingid(paymentRequestVO.getId());


            commonValidatorVO.setTransDetailsVO(transDetailsVO);
        }

        return commonValidatorVO;
    }

    // read QR Checkout params
    public RestPaymentRequestVO readRequestForQRCheckoutParams(RestPaymentRequest request)
    {
        Gson gson = new Gson();
        transactionLogger.error("in readRequestForQRCheckoutParams ------ " + gson.toJson(request));

        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        if (request != null)
        {
            restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));
            restPaymentRequestVO.setMerchantTransactionId(request.getMerchantTransactionId());
            restPaymentRequestVO.setOrderDescriptor(request.getOrderDescriptor());
            restPaymentRequestVO.setCurrency(request.getCurrency());
            restPaymentRequestVO.setAmount(request.getAmount());

            CustomerVO customerVO = new CustomerVO();

            if (functions.isValueNull(request.getNotificationUrl()))
                restPaymentRequestVO.setNotificationUrl(request.getNotificationUrl());

            if (request.getCustomer() != null)
            {
                transactionLogger.error("email ----- " + request.getCustomer().getEmail());
                if (functions.isValueNull(request.getCustomer().getEmail()))
                    customerVO.setEmail(request.getCustomer().getEmail());

                if (functions.isValueNull(request.getCustomer().getCustomerId()))
                    customerVO.setCustomerId(request.getCustomer().getCustomerId());

                restPaymentRequestVO.setCustomerVO(customerVO);
            }

            if (functions.isValueNull(request.getPaymentBrand()))
                restPaymentRequestVO.setPaymentBrand(request.getPaymentBrand());

            if (functions.isValueNull(request.getPaymentMode()))
                restPaymentRequestVO.setPaymentMode(request.getPaymentMode());

            if (functions.isValueNull(request.getTmpl_amount()))
                restPaymentRequestVO.setTmpl_amount(request.getTmpl_amount());
            else
                restPaymentRequestVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                restPaymentRequestVO.setTmpl_currency(request.getTmpl_currency());
            else
                restPaymentRequestVO.setTmpl_currency(request.getCurrency());
        }

        return restPaymentRequestVO;
    }

    public CommonValidatorVO readRequestForQRCheckoutTransaction(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        Gson gson = new Gson();
        transactionLogger.error("in readRequestForQRCheckoutTransaction ------ " + gson.toJson(request));

        CommonValidatorVO directKitValidatorVO = null;
        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();
            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();

            //Authentication Details
            directKitValidatorVO = populateAuthenticationDetailsJSON(directKitValidatorVO, request);
            GenericTransDetailsVO genericTransDetailsVO = directKitValidatorVO.getTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = directKitValidatorVO.getMerchantDetailsVO();


            genericTransDetailsVO.setOrderId(request.getMerchantTransactionId());
            if (request.getOrderDescriptor() != null)
                genericTransDetailsVO.setOrderDesc(request.getOrderDescriptor());
            else
                genericTransDetailsVO.setOrderDesc(request.getMerchantTransactionId());

            genericTransDetailsVO.setCurrency(request.getCurrency());
            genericTransDetailsVO.setAmount(request.getAmount());

            if (request.getCustomerVO() != null)
            {
                if (functions.isValueNull(request.getCustomerVO().getEmail()))
                    genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());

                if (functions.isValueNull(request.getCustomerVO().getCustomerId()))
                    directKitValidatorVO.setCustomerId(request.getCustomerVO().getCustomerId());
            }

            if (functions.isValueNull(request.getTmpl_amount()))
                genericAddressDetailsVO.setTmpl_amount(request.getTmpl_amount());
            else
                genericAddressDetailsVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                genericAddressDetailsVO.setTmpl_currency(request.getTmpl_currency());
            else
                genericAddressDetailsVO.setTmpl_currency(request.getCurrency());

            if (functions.isValueNull(request.getNotificationUrl()))
                genericTransDetailsVO.setNotificationUrl(request.getNotificationUrl());

            if (functions.isValueNull(request.getPaymentBrand()))
            {
                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }

            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        }
        return directKitValidatorVO;
    }


    // QR Code read params
    public RestPaymentRequestVO readRequestForQRParams(RestPaymentRequest request)
    {
        Gson gson = new Gson();
        transactionLogger.error("in readRequestForQRParams ------ " + gson.toJson(request));

        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        if (request != null)
        {
            if (functions.isValueNull(request.getPaymentId()))
                restPaymentRequestVO.setId(request.getPaymentId());

            if (request.getAuthentication() != null && functions.isValueNull(request.getAuthentication().getMemberId()))
                restPaymentRequestVO.getAuthenticationVO().setMemberId(request.getAuthentication().getMemberId());

            if (functions.isValueNull(request.getMerchantTransactionId()))
                restPaymentRequestVO.setMerchantTransactionId(request.getMerchantTransactionId());

            if (functions.isValueNull(request.getCurrency()))
                restPaymentRequestVO.setCurrency(request.getCurrency());

            if (functions.isValueNull(request.getAmount()))
                restPaymentRequestVO.setAmount(request.getAmount());

            if (request.getCustomer() != null)
            {
                restPaymentRequestVO.setCustomerVO(getCustomerVO(request.getCustomer()));

                if (functions.isValueNull(request.getCustomer().getGivenName()))
                    restPaymentRequestVO.getCustomerVO().setGivenName(request.getCustomer().getGivenName());

                if (functions.isValueNull(request.getCustomer().getSurname()))
                    restPaymentRequestVO.getCustomerVO().setSurname(request.getCustomer().getSurname());

                if (functions.isValueNull(request.getCustomer().getEmail()))
                    restPaymentRequestVO.getCustomerVO().setEmail(request.getCustomer().getEmail());

                if (functions.isValueNull(request.getCustomer().getWalletId()))
                    restPaymentRequestVO.getCustomerVO().setWalletId(request.getCustomer().getWalletId());

                if (functions.isValueNull(request.getCustomer().getWalletAmount()))
                    restPaymentRequestVO.getCustomerVO().setWalletAmount(request.getCustomer().getWalletAmount());

                if (functions.isValueNull(request.getCustomer().getWalletCurrency()))
                    restPaymentRequestVO.getCustomerVO().setWalletCurrency(request.getCustomer().getWalletCurrency());
            }

            if (functions.isValueNull(request.getPaymentBrand()))
                restPaymentRequestVO.setPaymentBrand(request.getPaymentBrand());

            if (functions.isValueNull(request.getPaymentMode()))
                restPaymentRequestVO.setPaymentMode(request.getPaymentMode());

            if (functions.isValueNull(request.getTmpl_amount()))
                restPaymentRequestVO.setTmpl_amount(request.getTmpl_amount());
            else
                restPaymentRequestVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                restPaymentRequestVO.setTmpl_currency(request.getTmpl_currency());
            else
                restPaymentRequestVO.setTmpl_currency(request.getCurrency());

            restPaymentRequestVO.setPaymentType(request.getPaymentType());
        }
        return restPaymentRequestVO;
    }


    public CommonValidatorVO readRequestForQRTransaction(RestPaymentRequestVO request, HttpServletRequest httpServletRequest)
    {
        Gson gson = new Gson();
        transactionLogger.error("in readRequestForQRTransaction ------ " + gson.toJson(request));

        CommonValidatorVO directKitValidatorVO = null;
        if (request != null)
        {
            directKitValidatorVO = new CommonValidatorVO();
            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

            //Transaction Details
            genericAddressDetailsVO.setIp(Functions.getIpAddress(httpServletRequest));

            transactionLogger.error("order id -----" + request.getMerchantTransactionId());

            if (functions.isValueNull(request.getCurrency()))
                genericTransDetailsVO.setCurrency(request.getCurrency());

            if (functions.isValueNull(request.getAmount()))
                genericTransDetailsVO.setAmount(request.getAmount());

            if (functions.isValueNull(request.getId()))
                directKitValidatorVO.setTrackingid(request.getId());

            if (request.getAuthenticationVO() != null && functions.isValueNull(request.getAuthenticationVO().getMemberId()))
                genericMerchantDetailsVO.setMemberId(request.getAuthenticationVO().getMemberId());

            if (functions.isValueNull(request.getCustomerVO().getWalletId()))
                genericTransDetailsVO.setWalletId(request.getCustomerVO().getWalletId());

            if (functions.isValueNull(request.getCustomerVO().getWalletAmount()))
                genericTransDetailsVO.setWalletAmount(request.getCustomerVO().getWalletAmount());

            if (functions.isValueNull(request.getCustomerVO().getWalletCurrency()))
                genericTransDetailsVO.setWalletCurrency(request.getCustomerVO().getWalletCurrency());

            if (functions.isValueNull(request.getTmpl_amount()))
                genericAddressDetailsVO.setTmpl_amount(request.getTmpl_amount());
            else
                genericAddressDetailsVO.setTmpl_amount(request.getAmount());

            if (functions.isValueNull(request.getTmpl_currency()))
                genericAddressDetailsVO.setTmpl_currency(request.getTmpl_currency());
            else
                genericAddressDetailsVO.setTmpl_currency(request.getCurrency());

            if (functions.isValueNull(request.getMerchantTransactionId()))
                genericTransDetailsVO.setOrderId(request.getMerchantTransactionId());

            if (functions.isValueNull(request.getOrderDescriptor()))
                genericTransDetailsVO.setOrderDesc(request.getOrderDescriptor());
            else
                genericTransDetailsVO.setOrderDesc(request.getMerchantTransactionId());


            //Address details
            if (request.getCustomerVO() != null)
            {
                if (functions.isValueNull(request.getCustomerVO().getGivenName()))
                    genericAddressDetailsVO.setFirstname(request.getCustomerVO().getGivenName());

                if (functions.isValueNull(request.getCustomerVO().getSurname()))
                    genericAddressDetailsVO.setLastname(request.getCustomerVO().getSurname());

                if (functions.isValueNull(request.getCustomerVO().getEmail()))
                    genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());
            }

            //normal transaction details
            if (functions.isValueNull(request.getPaymentBrand()))
            {
                directKitValidatorVO.setPaymentBrand(request.getPaymentBrand());
                directKitValidatorVO.setCardType(GatewayAccountService.getCardId(request.getPaymentBrand()));
            }
            if (functions.isValueNull(request.getPaymentMode()))
            {
                directKitValidatorVO.setPaymentMode(request.getPaymentMode());
                directKitValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(request.getPaymentMode()));
            }

            if (functions.isValueNull(request.getPaymentType()))
            {
                directKitValidatorVO.setTransactionType(request.getPaymentType());
            }
            transactionLogger.error("in readRequestForQRTransaction ---- tracking Id ----- " + request.getId());
            transactionLogger.error("in readRequestForQRTransaction ---- amount ----- " + request.getAmount());
            transactionLogger.error("in readRequestForQRTransaction ---- currency ----- " + request.getCurrency());
            transactionLogger.error("in readRequestForQRTransaction ---- paymode ----- " + request.getPaymentMode());
            transactionLogger.error("in readRequestForQRTransaction ---- cardtype ----- " + request.getPaymentBrand());
            transactionLogger.error("in readRequestForQRTransaction ---- firstname ----- " + request.getCustomerVO().getGivenName());
            transactionLogger.error("in readRequestForQRTransaction ---- lastname ----- " + request.getCustomerVO().getSurname());
            transactionLogger.error("in readRequestForQRTransaction ---- email ----- " + request.getCustomerVO().getEmail());
            transactionLogger.error("in readRequestForQRTransaction ---- walletid ----- " + request.getCustomerVO().getWalletId());
            transactionLogger.error("in readRequestForQRTransaction ---- walletamount ----- " + request.getCustomerVO().getWalletAmount());
            transactionLogger.error("in readRequestForQRTransaction ---- walletcurrency ----- " + request.getCustomerVO().getWalletCurrency());
            transactionLogger.error("in readRequestForQRTransaction ---- paymenttype ----- " + request.getPaymentType());

            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
        }
        return directKitValidatorVO;
    }

    public RestPaymentRequestVO readRequestForQRConfirm(RestPaymentRequest request)
    {
        Gson gson = new Gson();
        transactionLogger.error("in readRequestForQRConfirm" + gson.toJson(request));

        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        CustomerVO customerVO = new CustomerVO();
        if (request != null)
        {
            restPaymentRequestVO.setStatus(request.getStatus());

            if (request.getCustomer() != null)
            {
                if (functions.isValueNull(request.getCustomer().getWalletAmount()))
                {
                    customerVO.setWalletAmount(request.getCustomer().getWalletAmount());
                }
                if (functions.isValueNull(request.getCustomer().getWalletCurrency()))
                {
                    customerVO.setWalletCurrency(request.getCustomer().getWalletCurrency());
                }
            }
        }

        restPaymentRequestVO.setCustomerVO(customerVO);
        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO readRequestForTokenInstallmentCount(RestPaymentRequest request)
    {
        logger.debug("Reading the request for recurring with token::");
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        //Athentication Details
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(request.getAuthentication()));

        return restPaymentRequestVO;
    }

    public CommonValidatorVO readRequestForTokenInstallmentCount(RestPaymentRequestVO requestVO, HttpServletRequest servletRequest)
    {
        logger.debug("Reading the request for get Installment count with token::");
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        //Athentication Details
        commonValidatorVO = populateAuthenticationDetailsJSON(commonValidatorVO, requestVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO readInitiateAuthentication(RestPaymentRequestVO restPaymentRequestVO, HttpServletRequest httpServletRequest)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setMemberId(restPaymentRequestVO.getMemberId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
        genericTransDetailsVO.setCurrency(restPaymentRequestVO.getCurrency());
        genericTransDetailsVO.setCardType(restPaymentRequestVO.getPaymentMode());
        genericTransDetailsVO.setPaymentType(restPaymentRequestVO.getPaymentType());
        genericTransDetailsVO.setOrderId(restPaymentRequestVO.getMerchantTransactionId());
        genericTransDetailsVO.setAmount(restPaymentRequestVO.getAmount());
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        commonValidatorVO.setPaymentBrand(restPaymentRequestVO.getPaymentBrand());
        commonValidatorVO.setTransactionType(restPaymentRequestVO.getPaymentType());

        if (functions.isValueNull(restPaymentRequestVO.getPaymentBrand()))
        {
            commonValidatorVO.setPaymentBrand(restPaymentRequestVO.getPaymentBrand());
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(restPaymentRequestVO.getPaymentBrand()));
        }
        if (functions.isValueNull(restPaymentRequestVO.getPaymentMode()))
        {
            commonValidatorVO.setPaymentMode(restPaymentRequestVO.getPaymentMode());
            commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(restPaymentRequestVO.getPaymentMode()));
        }

        return commonValidatorVO;
    }

    public CommonValidatorVO readAuthenticate(RestPaymentRequestVO restPaymentRequestVO, HttpServletRequest request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        //card details
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
        genericCardDetailsVO.setCardNum(restPaymentRequestVO.getCardVO().getNumber());
        genericCardDetailsVO.setExpYear(restPaymentRequestVO.getCardVO().getExpiryYear());
        genericCardDetailsVO.setExpMonth(restPaymentRequestVO.getCardVO().getExpiryMonth());
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);

        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        merchantDetailsVO.setMemberId(restPaymentRequestVO.getMemberId());
        merchantDetailsVO.setMerchantAccountType(restPaymentRequestVO.getMerchantAccountType());
        merchantDetailsVO.setCurrency(restPaymentRequestVO.getCurrency());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();
        addressDetailsVO.setStreet(restPaymentRequestVO.getShippingAddressVO().getStreet1());
        addressDetailsVO.setCountry(restPaymentRequestVO.getShippingAddressVO().getCountry());
        addressDetailsVO.setCity(restPaymentRequestVO.getShippingAddressVO().getCity());
        addressDetailsVO.setState(restPaymentRequestVO.getShippingAddressVO().getState());
        addressDetailsVO.setZipCode(restPaymentRequestVO.getShippingAddressVO().getPostcode());
        addressDetailsVO.setCardHolderIpAddress(restPaymentRequestVO.getCustomerVO().getIp());
        addressDetailsVO.setFirstname(restPaymentRequestVO.getCustomerVO().getGivenName());
        addressDetailsVO.setLastname(restPaymentRequestVO.getCustomerVO().getSurname());
        addressDetailsVO.setEmail(restPaymentRequestVO.getCustomerVO().getEmail());
        addressDetailsVO.setTelnocc(restPaymentRequestVO.getCustomerVO().getTelnocc());
        addressDetailsVO.setPhone(restPaymentRequestVO.getCustomerVO().getPhone());
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);

        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();
        transDetailsVO.setCurrency(restPaymentRequestVO.getCurrency());
        transDetailsVO.setAmount(restPaymentRequestVO.getAmount());
        transDetailsVO.setOrderId(restPaymentRequestVO.getMerchantTransactionId());
        transDetailsVO.setPaymentType(restPaymentRequestVO.getPaymentType());
        transDetailsVO.setCardType(restPaymentRequestVO.getPaymentMode());
        transDetailsVO.setResponseOrderNumber(restPaymentRequestVO.getReferenceId());
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        commonValidatorVO.setReferenceid(restPaymentRequestVO.getReferenceId());
        commonValidatorVO.setDeviceType(restPaymentRequestVO.getDeviceType());
        commonValidatorVO.setPaymentBrand(restPaymentRequestVO.getPaymentBrand());
        commonValidatorVO.setTransactionType(restPaymentRequestVO.getPaymentType());
        commonValidatorVO.setTrackingid(restPaymentRequestVO.getOrderNumber());

        if (functions.isValueNull(restPaymentRequestVO.getPaymentBrand()))
        {
            commonValidatorVO.setPaymentBrand(restPaymentRequestVO.getPaymentBrand());
            commonValidatorVO.setCardType(GatewayAccountService.getCardId(restPaymentRequestVO.getPaymentBrand()));
        }
        if (functions.isValueNull(restPaymentRequestVO.getPaymentMode()))
        {
            commonValidatorVO.setPaymentMode(restPaymentRequestVO.getPaymentMode());
            commonValidatorVO.setPaymentType(GatewayAccountService.getPaymentId(restPaymentRequestVO.getPaymentMode()));
        }

        return commonValidatorVO;
    }

    public RestPaymentRequestVO readProcessGetTransactionList(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setCustomerVO(getCustomerVO(restPaymentRequest.getCustomer()));
        restPaymentRequestVO.setPaginationVO(getpaginationVO(restPaymentRequest.getPagination()));
        restPaymentRequestVO.setPaymentId(restPaymentRequest.getPaymentId());
        restPaymentRequestVO.setStatus(restPaymentRequest.getStatus());
        restPaymentRequestVO.setCardVO(getCardVO(restPaymentRequest.getCard()));
        if (functions.isValueNull(restPaymentRequest.getCurrency())){
        restPaymentRequestVO.setCurrency(restPaymentRequest.getCurrency());}
        if (functions.isValueNull(restPaymentRequest.getTimeZone())){
        restPaymentRequestVO.setTimeZone(restPaymentRequest.getTimeZone());}


        return restPaymentRequestVO;

    }

    public RestPaymentRequestVO readProcessCustomerCardWhitelisting(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setCustomerVO(getCustomerVO(restPaymentRequest.getCustomer()));
        //restPaymentRequestVO.setCard1(restPaymentRequest.getCard1());
        return restPaymentRequestVO;
    }

    public CommonValidatorVO readProcessGetTransactionListJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        PaginationVO paginationVO = new PaginationVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();

        if(restPaymentRequestVO.getAuthenticationVO()!=null)
        {
            merchantDetailsVO.setMemberId(restPaymentRequestVO.getAuthenticationVO().getMemberId());
            transDetailsVO.setChecksum(restPaymentRequestVO.getAuthenticationVO().getChecksum());
        }

         if(functions.isValueNull(restPaymentRequestVO.getPaymentId()))
           commonValidatorVO.setTrackingid(restPaymentRequestVO.getPaymentId());
       else
           commonValidatorVO.setTrackingid(restPaymentRequestVO.getId());

        //for status
        if(functions.isValueNull(restPaymentRequestVO.getStatus()))
            commonValidatorVO.setStatus(restPaymentRequestVO.getStatus());
        else
           commonValidatorVO.setStatus(restPaymentRequestVO.getStatus());
        //for currency
        if (functions.isValueNull(restPaymentRequestVO.getCurrency())){
            transDetailsVO.setCurrency(restPaymentRequestVO.getCurrency()); }

        if(restPaymentRequestVO.getCustomerVO()!=null)
        {
            genericAddressDetailsVO.setEmail(restPaymentRequestVO.getCustomerVO().getEmail());
            genericAddressDetailsVO.setFirstname(restPaymentRequestVO.getCustomerVO().getGivenName());
            genericAddressDetailsVO.setLastname(restPaymentRequestVO.getCustomerVO().getSurname());
        }

        if(restPaymentRequestVO.getCardVO()!=null)
        {
            genericCardDetailsVO.setFirstSix(restPaymentRequestVO.getCardVO().getFirstsix());
            genericCardDetailsVO.setLastFour(restPaymentRequestVO.getCardVO().getLastfour());
        }

        if (restPaymentRequestVO.getPaginationVO() != null)
        {
            paginationVO.setStartdate(restPaymentRequestVO.getPaginationVO().getFromdate());
            paginationVO.setEnddate(restPaymentRequestVO.getPaginationVO().getTodate());
            paginationVO.setStart(restPaymentRequestVO.getPaginationVO().getStart());
            paginationVO.setEnd(restPaymentRequestVO.getPaginationVO().getEnd());
            paginationVO.setPageNo(restPaymentRequestVO.getPaginationVO().getPageno());
            paginationVO.setRecordsPerPage(restPaymentRequestVO.getPaginationVO().getRecords());
        }
        if(restPaymentRequestVO.getTimeZone() != null){
            commonValidatorVO.setTimeZone(restPaymentRequestVO.getTimeZone());
        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setPaginationVO(paginationVO);
        commonValidatorVO.setCardDetailsVO(genericCardDetailsVO);//above all VO are set here one by one to return in next line
        return commonValidatorVO;
    }

    public RestPaymentRequestVO readQueryFraudefender(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        //restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setPurchase_identifier(restPaymentRequest.getPurchase_identifier());
        restPaymentRequestVO.setAmount(restPaymentRequest.getAmount());
        restPaymentRequestVO.setCurrency(restPaymentRequest.getCurrency());
        restPaymentRequestVO.setTransactionDate(restPaymentRequest.getTransactionDate());
        restPaymentRequestVO.setTransactionType(restPaymentRequest.getTransactionType());
        restPaymentRequestVO.setPersonalAccountNumber(restPaymentRequest.getPersonalAccountNumber());
        restPaymentRequestVO.setMerchant_id(restPaymentRequest.getMerchant_id());
        restPaymentRequestVO.setAuthorization_code(restPaymentRequest.getAuthorization_code());
        restPaymentRequestVO.setRrn(restPaymentRequest.getRrn());
        restPaymentRequestVO.setArn(restPaymentRequest.getArn());
        restPaymentRequestVO.setStan(restPaymentRequest.getStan());
        restPaymentRequestVO.setCall_type(restPaymentRequest.getCall_type());

        return restPaymentRequestVO;

    }


    public CommonValidatorVO readQueryFraudefenderJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        PaginationVO paginationVO = new PaginationVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO=new GenericCardDetailsVO();

        if(functions.isValueNull(restPaymentRequestVO.getPurchase_identifier()))
            transDetailsVO.setPurchase_identifier(restPaymentRequestVO.getPurchase_identifier());

        if(functions.isValueNull(restPaymentRequestVO.getAmount()))
            transDetailsVO.setAmount(String.format("%.2f",Double.parseDouble(restPaymentRequestVO.getAmount())));

        if(functions.isValueNull(restPaymentRequestVO.getCurrency()))
            transDetailsVO.setCurrency(restPaymentRequestVO.getCurrency());

        if(functions.isValueNull(restPaymentRequestVO.getTransactionDate()))
            transDetailsVO.setTransactionDate(restPaymentRequestVO.getTransactionDate());

        if(functions.isValueNull(restPaymentRequestVO.getTransactionType()))
            transDetailsVO.setTransactionType(restPaymentRequestVO.getTransactionType());

        if(functions.isValueNull(restPaymentRequestVO.getPersonalAccountNumber()))
        {
            transDetailsVO.setPersonalAccountNumber(restPaymentRequestVO.getPersonalAccountNumber());
            cardDetailsVO.setFirstSix(functions.getFirstSix(restPaymentRequestVO.getPersonalAccountNumber()));
            cardDetailsVO.setLastFour(functions.getLastFour(restPaymentRequestVO.getPersonalAccountNumber()));
        }

        if(functions.isValueNull(restPaymentRequestVO.getMerchant_id()))
            transDetailsVO.setMerchant_id(restPaymentRequestVO.getMerchant_id());

        if(functions.isValueNull(restPaymentRequestVO.getAuthorization_code()))
            transDetailsVO.setAuthorization_code(restPaymentRequestVO.getAuthorization_code());

        if(functions.isValueNull(restPaymentRequestVO.getRrn()))
            transDetailsVO.setRrn(restPaymentRequestVO.getRrn());

        if(functions.isValueNull(restPaymentRequestVO.getArn()))
            transDetailsVO.setArn(restPaymentRequestVO.getArn());

        if(functions.isValueNull(restPaymentRequestVO.getStan()))
            transDetailsVO.setStan(restPaymentRequestVO.getStan());

        if(functions.isValueNull(restPaymentRequestVO.getCall_type()))
            transDetailsVO.setCall_type(restPaymentRequestVO.getCall_type());
        //for status
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        commonValidatorVO.setCardDetailsVO(cardDetailsVO);

        return commonValidatorVO;
    }


    public CommonValidatorVO readProcessCardWhitelistingJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO=new GenericAddressDetailsVO();

        if(restPaymentRequestVO.getCustomerVO()!=null)
        {
            genericAddressDetailsVO.setCustomerid(restPaymentRequestVO.getCustomerVO().getCustomerId());
            genericAddressDetailsVO.setEmail(restPaymentRequestVO.getCustomerVO().getEmail());
        }
        if(restPaymentRequestVO.getAuthenticationVO()!=null)
        {
            merchantDetailsVO.setMemberId(restPaymentRequestVO.getAuthenticationVO().getMemberId());
            merchantDetailsVO.setAccountId(restPaymentRequestVO.getAuthenticationVO().getAccountId());
        }
        HashMap<String, CardVO> cardDetailsVOHashMap=null;
        if(restPaymentRequestVO.getCard1()!=null)
        {
            for (CardVO cardVO : restPaymentRequestVO.getCard1())
            {
                logger.error("firstSix::::"+cardVO.getFirstsix()+"-----"+cardVO.getLastfour());
                cardDetailsVOHashMap = new HashMap<>();
                cardDetailsVOHashMap.put(restPaymentRequestVO.getCustomerVO().getCustomerId(),cardVO);
            }
        }
        commonValidatorVO.setCardVOHashMap(cardDetailsVOHashMap);
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }
    public RestPaymentRequestVO readRefundFraudefender(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setClientTransactionId(restPaymentRequest.getClientTransactionId());
        restPaymentRequestVO.setRefundAmount(restPaymentRequest.getRefundAmount());
        restPaymentRequestVO.setRefundCurrency(restPaymentRequest.getRefundCurrency());
        restPaymentRequestVO.setCall_type(restPaymentRequest.getCall_type());

        return restPaymentRequestVO;

    }


    public CommonValidatorVO readRefundFraudefenderJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        PaginationVO paginationVO = new PaginationVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();

        if(restPaymentRequestVO.getAuthenticationVO()!=null)
            transDetailsVO.setChecksum(restPaymentRequestVO.getAuthenticationVO().getChecksum());

        if(functions.isValueNull(restPaymentRequestVO.getClientTransactionId()))
            transDetailsVO.setClientTransactionId(restPaymentRequestVO.getClientTransactionId());

        if(functions.isValueNull(restPaymentRequestVO.getRefundAmount()))
            transDetailsVO.setRefundAmount(String.format("%.2f", Double.parseDouble(restPaymentRequestVO.getRefundAmount())));

        if(functions.isValueNull(restPaymentRequestVO.getRefundCurrency()))
            transDetailsVO.setRefundCurrency(restPaymentRequestVO.getRefundCurrency());

        if(functions.isValueNull(restPaymentRequestVO.getCall_type()))
            transDetailsVO.setCall_type(restPaymentRequestVO.getCall_type());

        //for status
        commonValidatorVO.setTransDetailsVO(transDetailsVO);

        return commonValidatorVO;
    }
    public CommonValidatorVO readProcessGetDailySalesReportJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        PaginationVO paginationVO = new PaginationVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericTransDetailsVO transDetailsVO=new GenericTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        if(restPaymentRequestVO.getAuthenticationVO()!=null)
        {
            merchantDetailsVO.setMemberId(restPaymentRequestVO.getAuthenticationVO().getMemberId());
            transDetailsVO.setChecksum(restPaymentRequestVO.getAuthenticationVO().getChecksum());
        }
        if(functions.isValueNull(restPaymentRequestVO.getStatus()))
            commonValidatorVO.setStatus(restPaymentRequestVO.getStatus());
        else
            commonValidatorVO.setStatus(restPaymentRequestVO.getStatus());
        if (restPaymentRequestVO.getPaginationVO() != null)
        {
            paginationVO.setStartdate(restPaymentRequestVO.getPaginationVO().getFromdate());
            paginationVO.setEnddate(restPaymentRequestVO.getPaginationVO().getTodate());
        }

        if(restPaymentRequestVO.getTimeZone() != null){
            commonValidatorVO.setTimeZone(restPaymentRequestVO.getTimeZone());
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(transDetailsVO);
        commonValidatorVO.setPaginationVO(paginationVO);
        return commonValidatorVO;
    }
    public RestPaymentRequestVO readProcessGetDailySalesReport(RestPaymentRequest restPaymentRequest)
    {
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        restPaymentRequestVO.setAuthenticationVO(getAuthenticationVO(restPaymentRequest.getAuthentication()));
        restPaymentRequestVO.setPaginationVO(getpaginationVO(restPaymentRequest.getPagination()));
        if (functions.isValueNull(restPaymentRequest.getTimeZone())){
        restPaymentRequestVO.setTimeZone(restPaymentRequest.getTimeZone());}
        restPaymentRequestVO.setStatus(restPaymentRequest.getStatus());
        return restPaymentRequestVO;
    }

    public RestPaymentRequestVO getMerchantRequestVO(RestPaymentRequest merchantRequest)
    {
        RestPaymentRequestVO merchantRequestVO = new RestPaymentRequestVO();
        Customer customer = merchantRequest.getCustomer();

        merchantRequestVO.setMerchant_id(merchantRequest.getMerchant_id());
        merchantRequestVO.setMerchantTransactionId(merchantRequest.getMerchantTransactionId());
        merchantRequestVO.setAuthenticationVO(getAuthenticationVO(merchantRequest.getAuthentication()));
        merchantRequestVO.setCustomerVO(getCustomerVO(customer));

        return merchantRequestVO;
    }

    public CommonValidatorVO readRequestGenerateAppOTP(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

        genericAddressDetailsVO.setPhone(request.getCustomerVO().getMobile());
        genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());
        genericMerchantDetailsVO.setMemberId(request.getMerchant_id());
        genericMerchantDetailsVO.setTransactionID(request.getMerchantTransactionId());

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);

        return commonValidatorVO;
    }

    public CommonValidatorVO readVerifyOTP(RestPaymentRequestVO request)
    {

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();

        merchantDetailsVO.setSmsOtp(request.getCustomerVO().getSmsOtp());
        merchantDetailsVO.setEmailOtp(request.getCustomerVO().getEmailOtp());
        genericAddressDetailsVO.setPhone(request.getCustomerVO().getMobile());
        genericAddressDetailsVO.setEmail(request.getCustomerVO().getEmail());
        merchantDetailsVO.setTransactionID(request.getMerchantTransactionId());

        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }


    public CommonValidatorVO readRequestGetpayoutBalance(RestPaymentRequestVO request,String Merchant_id)
    {
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

        genericMerchantDetailsVO.setMemberId(Merchant_id);
        commonValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);

        return commonValidatorVO;
    }
    public CommonValidatorVO readRequestUpdateUpiTxnDetails(RestPaymentRequestVO request)
    {
        CommonValidatorVO commonValidatorVO         = new CommonValidatorVO();
        TransactionDetailsVO transactionDetailsVO   = new TransactionDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = new MerchantDetailsVO();

        transactionDetailsVO.setAmount(request.getAmount());
        transactionDetailsVO.setStatus(request.getStatus());
        transactionDetailsVO.setPaymentId(request.getId());// paymemtid
        transactionDetailsVO.setCustomerId(request.getVpa_address());// vpa address
        transactionDetailsVO.setTransactionTime(request.getTransactionDate());
        merchantDetailsVO.setMemberId(request.getMemberId());

        commonValidatorVO.setTransactionDetailsVO(transactionDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        return commonValidatorVO;
    }
}