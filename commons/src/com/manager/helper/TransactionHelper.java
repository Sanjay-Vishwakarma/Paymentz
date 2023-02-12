package com.manager.helper;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.*;
import com.manager.dao.MerchantDAO;
import com.manager.enums.MerchantModuleEnum;
import com.manager.enums.TransReqRejectCheck;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.payment.Enum.CallTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/10/15
 * Time: 9:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionHelper
{
    private static Logger log = new Logger(TransactionHelper.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionHelper.class.getName());

    public CommonValidatorVO performCommonSystemChecksStep1(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {

        performCommonSystemChecksForSKStep0(commonValidatorVO);

        commonValidatorVO = performCommonSystemChecksForSKStep1(commonValidatorVO);


        return commonValidatorVO;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void checksumValidationforSK(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        //check already added
        ErrorCodeListVO errorCodeListVO             = null;
        String calchecksum                          = "";
        GenericCardDetailsVO genericCardDetailsVO   = commonValidatorVO.getCardDetailsVO();
        Functions functions                         = new Functions();
        String error                                = "";
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        try
        { log.error("transactionhelper genericTransDetailsVO.getOrderId()-->"+genericTransDetailsVO.getOrderId()+"  amount-->"+genericTransDetailsVO.getAmount());
          log.error("transactionhelper genericTransDetailsVO.getchecksumAmount()-->"+genericTransDetailsVO.getChecksumAmount());
          /*  if(functions.isValueNull(genericTransDetailsVO.getCurrency())&&"JPY".equalsIgnoreCase(genericTransDetailsVO.getCurrency()))
            {
                genericTransDetailsVO.setAmount(TransactionHelper.getAmount(genericTransDetailsVO.getAmount()));
            }*/
            if (functions.isValueNull(genericTransDetailsVO.getTotype()))
            {
                log.error("inside if totype-----");
                if (functions.isValueNull(commonValidatorVO.getToken()))
                {
                    log.error("inside if totype if - token-----");
                    calchecksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), commonValidatorVO.getToken(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
                /*else if (functions.isValueNull(genericCardDetailsVO.getCardNum()))
                {
                    log.debug("inside if totype else if - card num-----");
                    calchecksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), genericCardDetailsVO.getCardNum(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }*/
                else
                {
                    log.error("inside if totype else-----");
                    if (!functions.isValueNull(genericTransDetailsVO.getRedirectUrl()))
                    {
                        genericTransDetailsVO.setRedirectUrl("");
                    }
                    calchecksum = Functions.generateChecksumV4(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
            }
            else
            {
                log.error("inside else totype-----");
                if (commonValidatorVO.getToken() != null)
                {
                    log.error("inside else totype if-----");
                    calchecksum = Functions.generateChecksumSTDFlow(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), commonValidatorVO.getToken(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());

                }
                else if (genericCardDetailsVO.getCardNum() != null)
                {
                    log.error("inside else totype else if-----");
                    calchecksum = Functions.generateChecksumSTDFlow(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), genericCardDetailsVO.getCardNum(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());

                }
                else
                {
                    log.error("inside else totype else-----");
                    calchecksum = Functions.generateChecksumV5(merchantDetailsVO.getMemberId(),genericTransDetailsVO.getChecksumAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
            error = "Error while generating checksum. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_CHECKSUM_ERROR);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, e.getMessage(), e.getCause());
        }
        transactionLogger.error("received checksum::::" + genericTransDetailsVO.getChecksum()+" genericTransDetailsVO.getOrderId()-->"+genericTransDetailsVO.getOrderId());
      //  transactionLogger.error("generated checksum::::" + calchecksum+" genericTransDetailsVO.getOrderId()-->"+genericTransDetailsVO.getOrderId());
        if (!genericTransDetailsVO.getChecksum().equals(calchecksum))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            /*Transaction request rejected log entry with reason:Partner-MERCHANT_CHECKSUM_MISSMATCH*/
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MERCHANT_CHECKSUM_MISSMATCH.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CHECKSUM_ERROR.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
    }

    public CommonValidatorVO performCommonSystemChecksForSKStep0(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;

        //getting value from VO
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        GenericTransDetailsVO genericTransDetailsVO         = commonValidatorVO.getTransDetailsVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        Functions functions = new Functions();
        String error        = "";
        String accountId    = "";

        checksumValidationforSK(commonValidatorVO);

        log.debug("order id in sys check----" + genericTransDetailsVO.getOrderId());
        String uniqueorder = null;

        //String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
       /* if(!functions.isValueNull(commonValidatorVO.getPaymentType()) && !functions.isValueNull(commonValidatorVO.getCardType()) || functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType()))*/
        //  {
        uniqueorder = checkOrderUniquenessStatus(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getOrderId(),merchantDetailsVO.getIsUniqueOrderIdRequired());
        if (!uniqueorder.equals(""))
        {
            error           = commonValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id OR " + uniqueorder;
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            //Transaction request rejected log entry with reason:Partner-DUPLICATE_ORDERID
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.DUPLICATE_ORDERID.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if(!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getMarketPlace()))
        {
            List<MarketPlaceVO> mpDetailsList   = commonValidatorVO.getMarketPlaceVOList();
            MarketPlaceVO marketPlaceVO         = new MarketPlaceVO();
            if(mpDetailsList != null)
            {
                for (int i = 0; i < mpDetailsList.size(); i++)
                {
                    marketPlaceVO   = mpDetailsList.get(i);
                    uniqueorder     = checkOrderUniquenessStatusForMarketPlace(marketPlaceVO.getMemberid(), marketPlaceVO.getOrderid());
                    if (!uniqueorder.equals(""))
                    {
                        error           = marketPlaceVO.getOrderid() + "-Duplicate Order Id OR " + uniqueorder;
                        errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
                        //Transaction request rejected log entry with reason:Partner-DUPLICATE_ORDERID
                        //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.DUPLICATE_ORDERID.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_UNIQUEORDER_MP.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }
        }
        // }


        //Added Restricted Amount Check in CommonSystemCheckStep1
        if ("Y".equals(commonValidatorVO.getMerchantDetailsVO().getIsRestrictedTicket()))
        {
            performCommonSystemRestrictedTicketCheck(commonValidatorVO);
        }
        //Block country check by IP
        if (merchantDetailsVO.getBlacklistCountryIp().equalsIgnoreCase("Y") && functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
        {
            String customerIP       = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
            String ipCountryCode    = functions.getIPCountryShort(customerIP);
            transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP step 0---" + customerIP + "Country---" + ipCountryCode);
            //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
            if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
            {
                if (!isCountryBlocked(ipCountryCode, "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                {
                    error           = "Your Country is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep0()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        //Blacklist Transaction Flag check
        if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            //Moved Blacklist checks from system check step1 to system check step2
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error           = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_EMAIL
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDEMAIL.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(),error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            //checking cardholder IP in blacklisted IP's entry
            if (!isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
            {
                error           = "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_IP
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            {

                error           = "Your IpAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_IP
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_IP.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            //checking country in blacklisted entry
            /*if(!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId()))
            {
                error = "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_COUNTRY
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(),ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),commonValidatorVO.getAddressDetailsVO().getFirstname(),commonValidatorVO.getAddressDetailsVO().getLastname(),commonValidatorVO.getAddressDetailsVO().getEmail(),commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getCardDetailsVO().getExpMonth(),commonValidatorVO.getCardDetailsVO().getExpYear(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(),error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_COUNTRY.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }*/

            //Added new system check for cardholder name in blacklist entry
            if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                error           = "Customer Name Has Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_NAME
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDNAME.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_NAME.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getVpa_address())&&!isVPAaddressBlocked(commonValidatorVO.getVpa_address()))
            {     transactionLogger.error("BlacklistTransaction vpa address-----" + commonValidatorVO.getVpa_address());
                error           = "Your VPA Address is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDVPA);
                //Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_EMAIL
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDVPA.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(),error, TransReqRejectCheck.CUSTOMER_BLACKLISTED_EMAIL.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()) && !isPhoneBlocked(commonValidatorVO.getAddressDetailsVO().getPhone()))
            {
                error = "Your Phone No. is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDPHONE);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDPHONE.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }


        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        return commonValidatorVO;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CommonValidatorVO performCommonSystemChecksForSKStep1(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;

        Transaction transaction         = new Transaction();
        LimitChecker limitChecker       = new LimitChecker();
        PaymentChecker paymentChecker   = new PaymentChecker();

        //getting value from VO
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        TerminalVO terminalVO                           = null;
        TerminalManager terminalManager                 = new TerminalManager();
        RecurringBillingVO recurringBillingVO           = commonValidatorVO.getRecurringBillingVO();
        LimitRouting limitRouting                       = new LimitRouting();
        LinkedHashMap<String,TerminalVO> terminalMap    = null;

        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        Functions functions = new Functions();
        String error        = "";
        int paymentType     = 0;
        int cardType        = 0;
        int terminalId      = 0;
        log.debug("------limit-----" + merchantDetailsVO.getCheckLimit());
        transactionLogger.debug("------limit-----" + merchantDetailsVO.getCheckLimit());

        try
        {
            if (functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isNumericVal(commonValidatorVO.getPaymentType()))
            {
                paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
            }
        }
        catch (NumberFormatException e)
        {
            error               = "Kindly Pass Numeric value in paymentType field.";
            errorCodeListVO     = getErrorVO(ErrorName.VALIDATION_PAYMENT_TYPE);//SYS_NUMFORMAT_PAYMODE
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, e.getMessage(), e.getCause());
        }
        try
        {
            if (functions.isValueNull(commonValidatorVO.getCardType()) && functions.isNumericVal(commonValidatorVO.getCardType()))
            {
                cardType = Integer.parseInt(commonValidatorVO.getCardType());
            }
        }
        catch (NumberFormatException e)
        {
            error           = "Kindly Pass Numeric value in Card Type field.";
            errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CARD_TYPE);//SYS_NUMFORMAT_CARDTYPE
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, e.getMessage(), e.getCause());
        }
        try
        {
            if (functions.isValueNull(commonValidatorVO.getTerminalId()) && functions.isNumericVal(commonValidatorVO.getTerminalId()))
            {
                terminalId = Integer.parseInt(commonValidatorVO.getTerminalId());
            }
        }
        catch (NumberFormatException e)
        {
            error           = "Kindly Pass Numeric value in Terminal ID field.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_NUMFORMAT_TERMINALID);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, e.getMessage(), e.getCause());
        }

        String accountId    = "";
        String currency     = "";

        if (merchantDetailsVO.getCheckLimit().equals("1"))
        {
            //TODO : Merchant amount limit check
            Date date3          = new Date();
            transactionLogger.debug("TransactionHelper checkAmountLimit start #########" + date3.getTime());
            limitChecker.checkAmountLimit(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkAmountLimit end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkAmountLimit diff #########" + (new Date().getTime() - date3.getTime()));
                /*if(functions.isValueNull(genericTransDetailsVO.getCurrency()))
                {
                    log.debug("AutoSelectTerminal is Y and CheckLimit = 1 and Filling terminalVO basis on currency");
                    terminalVO = limitChecker.getTerminalBasedOnCurrency(commonValidatorVO,true);
                }
                else
                {
                    terminalVO = limitChecker.getTerminalBasedOnAccountID(commonValidatorVO,true);
                }*/
        }
        boolean isCardNumberAvailable       = false;
        transactionLogger.error("TerminalId->----" + terminalId);
        if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && merchantDetailsVO.getCard_velocity_check().equals("Y") && terminalId != 0)
        {
            terminalId = 0;
        }
        transactionLogger.error("TerminalId->>----" + terminalId);
        if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && terminalId == 0)
        {
            log.error("if inside autoterminal---" + merchantDetailsVO.getAutoSelectTerminal());
            log.error("merchantDetailsVO.getCard_velocity_check()------" + merchantDetailsVO.getCard_velocity_check());
            log.error("merchantDetailsVO.getCheckLimit()------" + merchantDetailsVO.getCheckLimit());
            log.error("merchantDetailsVO.getCardTransLimit()------" + merchantDetailsVO.getCardTransLimit());
            log.error("merchantDetailsVO.getCardCheckLimit()------" + merchantDetailsVO.getCardCheckLimit());

            if (merchantDetailsVO.getCard_velocity_check().equals("Y") && merchantDetailsVO.getCardTransLimit().equals("0") && merchantDetailsVO.getCardCheckLimit().equals("0"))
            {
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    isCardNumberAvailable=true;
                    transactionLogger.debug("inside CardDetailsVO().getCardNum()------------" + commonValidatorVO.getCardDetailsVO().getCardNum());
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                    {
                        transactionLogger.debug("AutoSelectTerminal is Y and CheckLimit = 1 and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                        terminalVO = limitChecker.getTerminalBasedOnCurrencyCardVelocity(commonValidatorVO, true);
                    }
                    else
                    {
                        terminalVO = limitChecker.getTerminalBasedOnAccountIDCardVelocity(commonValidatorVO, true);
                    }
                }
            }
            transactionLogger.debug("Limit Routing ------>"+commonValidatorVO.getMerchantDetailsVO().getLimitRouting());
            if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()) && merchantDetailsVO.getBinRouting().equalsIgnoreCase("N"))
            {
                terminalMap = terminalManager.getTerminalFromCurrency(commonValidatorVO);
                Date date3  = new Date();
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                terminalMap = limitRouting.getTerminalVOBasedOnAmountLimitRouting(terminalMap, commonValidatorVO);
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                for(Map.Entry entry:terminalMap.entrySet()){
                    String key= (String) entry.getKey();
                    terminalVO = terminalMap.get(key);
                    break;
                }
                commonValidatorVO.setTerminalMapLimitRouting(terminalMap);
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    date3 = new Date();
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting start #########" + date3.getTime());
                    terminalVO=limitRouting.getTerminalVOBasedOnCardAndCardAmountLimitRouting(terminalMap,commonValidatorVO);
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting end #########" + new Date().getTime());
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));

                    if(terminalVO==null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error = "Terminal ID provided by you is not valid.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, commonValidatorVO.getErrorName(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }

            }
            else
            {
                if (!merchantDetailsVO.getCard_velocity_check().equals("Y") || !isCardNumberAvailable)
                {
                    if (functions.isValueNull(genericTransDetailsVO.getCurrency())) // added by Sneha for REST API feature
                    {
                        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                        {
                            if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                            {
                                //If currency specific terminal found, transaction will gone through it and if not transaction will process through ALL support terminal
                                terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency(),commonValidatorVO.getAccountId());

                                if (terminalVO == null)
                                {
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL",commonValidatorVO.getAccountId());
                                    if (terminalVO != null)
                                        terminalVO.setCurrency(genericTransDetailsVO.getCurrency());
                                }
                            }
                            else
                            {
                                terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency(),commonValidatorVO.getAccountId());
                            }
                        }
                        else
                            terminalVO=terminalManager.getTerminalCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), genericTransDetailsVO.getCurrency());
                        if(terminalVO == null)
                            terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getAccountId());

                    }
                    else
                        terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType,commonValidatorVO.getAccountId());
                }
            }

            if (terminalVO == null)
            {
                // terminal not valid
                error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_CONFIGURATION*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),"0", error, TransReqRejectCheck.MERCHANT_TERMINAL_CONFIGURATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()) && !functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            {
                genericAddressDetailsVO.setTmpl_currency(terminalVO.getCurrency());
            }

            if (terminalVO.getIsActive().equalsIgnoreCase("N"))
            {
                error = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_CONFIGURATION*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),"0", error, TransReqRejectCheck.MERCHANT_TERMINAL_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            accountId       = terminalVO.getAccountId();
            commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
            commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
            commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
            commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            commonValidatorVO.setPaymentMode(GatewayAccountService.getPaymentMode(String.valueOf(terminalVO.getPaymodeId())));
            commonValidatorVO.setPaymentBrand(GatewayAccountService.getPaymentBrand(String.valueOf(terminalVO.getCardTypeId())));
            commonValidatorVO.setTerminalVO(terminalVO);
                limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

        }
        else
        {
            //Hashtable terminalDetails = transaction.getTerminalDetails(terminalId);
            //todo check if terminal flag need to check or not
            if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()) && !functions.isValueNull(commonValidatorVO.getAccountId()))
            {
                Date date4      = new Date();
                transactionLogger.error("TransactionHelper LimitRouting start #########" + date4.getTime());
                terminalMap     = new LinkedHashMap<>();
                terminalVO      = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                if (terminalVO == null)
                {
                    // terminal not valid
                    error           = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
                commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));

                transactionLogger.error("--Get All terminalDetails by currency--");
                terminalMap = terminalManager.getTerminalFromCurrency(commonValidatorVO);
                Date date3  = new Date();
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                terminalMap = limitRouting.getTerminalVOBasedOnAmountLimitRouting(terminalMap, commonValidatorVO);
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                commonValidatorVO.setTerminalMapLimitRouting(terminalMap);
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    date3       = new Date();
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting start #########" + date3.getTime());
                    terminalVO = limitRouting.getTerminalVOBasedOnCardAndCardAmountLimitRouting(terminalMap, commonValidatorVO);
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting end #########" + new Date().getTime());
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));

                    if (terminalVO == null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error                   = "Terminal ID provided by you is not valid.";
                        errorCodeListVO         = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, commonValidatorVO.getErrorName(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }else
                        terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                }else
                {
                    if(terminalMap!=null && terminalMap.size()>0)
                    {
                        for(Map.Entry entry:terminalMap.entrySet()){
                            String key  = (String) entry.getKey();
                            terminalVO  = terminalMap.get(key);
                            break;
                        }
                    }
                }
                transactionLogger.error("TransactionHelper LimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper LimitRouting diff #########" + (new Date().getTime() - date4.getTime()));
            }else
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                {
                    //terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency());
                    if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), "ALL", commonValidatorVO.getAccountId());
                            if (terminalVO != null)
                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                        }
                    }
                    else
                    {
                        terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                    }
                }
                else
                {
                    terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminal(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getAccountId());
                }

                if (terminalVO == null)
                {
                    // terminal not valid
                    error           = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_CONFIGURATION*/
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                    //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),"0", error, TransReqRejectCheck.MERCHANT_TERMINAL_CONFIGURATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
            if (!functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()) && !functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
            {
                genericAddressDetailsVO.setTmpl_currency(terminalVO.getCurrency());
            }
            if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
            {
                // terminal not not active
                error           = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_ACTIVATION*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MERCHANT_TERMINAL_ACTIVATION.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            commonValidatorVO.setPaymentMode(GatewayAccountService.getPaymentMode(String.valueOf(terminalVO.getPaymodeId())));
            commonValidatorVO.setPaymentBrand(GatewayAccountService.getPaymentBrand(String.valueOf(terminalVO.getCardTypeId())));
            commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
            commonValidatorVO.setReject3DCard(terminalVO.getReject3DCard());
            commonValidatorVO.setCurrencyConversion(terminalVO.getCurrencyConversion());
            commonValidatorVO.setConversionCurrency(terminalVO.getConversionCurrency());
            merchantDetailsVO.setAccountId(String.valueOf(terminalVO.getAccountId()));
            commonValidatorVO.setTerminalVO(terminalVO);

                limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

            //
            /*if (merchantDetailsVO.getCheckLimit().equals("1"))
            {

                limitChecker.checkAmountLimitForTerminalNew(genericTransDetailsVO.getAmount(), commonValidatorVO.getTerminalId() + "", merchantDetailsVO.getMemberId(), commonValidatorVO);

            }*/
            accountId = terminalVO.getAccountId();
        }
        String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
        String fromid   = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getMerchantId();

        if (commonValidatorVO.getRecurringBillingVO() != null && !commonValidatorVO.getRecurringBillingVO().equals("")
                && (functions.isValueNull(recurringBillingVO.getReqField1()) && ("N".equalsIgnoreCase(isRecurringAllowed(accountId))
                || ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()) && commonValidatorVO.getRecurringBillingVO().getReqField1() != null))))
        {
            error = "The functionality is not allowed for account. Please contact support for help:::";
            errorCodeListVO = getErrorVO(ErrorName.SYS_RECURRINGALLOW);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_RECURRINGALLOW.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
        {
            currency = genericTransDetailsVO.getCurrency();
        }
        else
        {
            currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();
        }

        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);// genericTransDetailsVO.setFromtype(fromtype);
        commonValidatorVO.getTransDetailsVO().setFromid(fromid);
        commonValidatorVO.getTransDetailsVO().setCurrency(currency);

        if (currency.equals("JPY") && !paymentChecker.isAmountValidForJPY(currency, genericTransDetailsVO.getAmount()))
        {
            error = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
            errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_JPY_CURRENCY_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if(functions.isValueNull(genericAddressDetailsVO.getTmpl_currency()) && functions.isValueNull(genericAddressDetailsVO.getTmpl_amount()))
        {
            if ("JPY".equals(genericAddressDetailsVO.getTmpl_currency()) && !paymentChecker.isAmountValidForJPY(currency, genericAddressDetailsVO.getTmpl_amount()))
            {
                error = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
                errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_JPY_CURRENCY_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        if ("Y".equals(terminalVO.getIsPSTTerminal()))
        {
            commonValidatorVO.setIsPSTProcessingRequest("Y");
        }
        else
        {
            commonValidatorVO.setIsPSTProcessingRequest("N");
        }
        merchantDetailsVO.setAccountId(accountId);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        commonValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);
        return commonValidatorVO;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public CommonValidatorVO performCommonSystemChecksStep2(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO   = commonValidatorVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO    = commonValidatorVO.getAddressDetailsVO();
        LimitChecker limitChecker                   = new LimitChecker();
        ErrorCodeListVO errorCodeListVO             = null;
        PaymentChecker paymentChecker               = new PaymentChecker();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        SplitPaymentVO splitPaymentVO                       = commonValidatorVO.getSplitPaymentVO();
        String error        = "";
        String cardNo       = commonValidatorVO.getCardDetailsVO().getCardNum();
        Functions functions = new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TerminalVO terminalVO                       = commonValidatorVO.getTerminalVO();

        //Card Limit Check per partner and Bank
        transactionLogger.debug("Card Limit Flag from Partner---" + merchantDetailsVO.getBankCardLimit());
        transactionLogger.debug("Partner ID---" + merchantDetailsVO.getPartnerId());
        transactionLogger.debug("Gateway Fromtype---" + commonValidatorVO.getTransDetailsVO().getFromtype());
        transactionLogger.debug("PGTYPE ID---" + commonValidatorVO.getTerminalVO().getGateway_id());
        transactionLogger.debug("Currency---" + commonValidatorVO.getTransDetailsVO().getCurrency());
        /*limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);*/
        if ("Y".equalsIgnoreCase(merchantDetailsVO.getBankCardLimit()))
        {
            limitChecker.checkCardLimitPerBank(cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4), commonValidatorVO);
        }

        //Card Velocity-Number of transactions per card.
        /*if("1".equals(merchantDetailsVO.getCardCheckLimit()))
        {
            limitChecker.checkCardLimitNew(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4), commonValidatorVO.getTerminalId(), commonValidatorVO);
        }*/
        //Card Velocity-Number of transactions per card on member
        if ("1".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCardCheckLimit()))
        {
            Date date3 = new Date();
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 start #########" + date3.getTime());
            limitChecker.checkCardLimitNew2(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 diff #########" + (new Date().getTime() - date3.getTime()));
        }

        //Card amount check on member
        if ("1".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCardTransLimit()))
        {
            Date date3 = new Date();
            transactionLogger.debug("TransactionHelper checkCardTransLimit start #########" + date3.getTime());
            limitChecker.checkCardTransLimit(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkCardTransLimit end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkCardTransLimit diff #########" + (new Date().getTime() - date3.getTime()));
        }

        if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
        {
            //Checking Gateway level Amount Limit
            transactionLogger.debug("commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel()----->" + commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel());
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                limitChecker.checkAmountLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkAmountLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkAmountLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }

            //Checking Terminal level Amount Limit
            transactionLogger.debug("commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel()---->" + commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel());
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                limitChecker.checkAmountLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkAmountLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkAmountLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
            //Card Velocity-Number of transactions per card on gateway.
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel start #########" + date3.getTime());
                limitChecker.checkCardLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }

            //Card Velocity-Number of transactions per card on terminal.
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel start #########" + date3.getTime());
                limitChecker.checkCardLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
        }

        //Todo : Use Switch for card type
        error = Functions.isValidCard(commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getCardType());
        if(error != null && !error.equals(""))
        {
            String errorName = "";
            if(error.contains("Visa"))
            {
                errorName       = ErrorName.SYS_INVALID_VISA.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_VISA);
            }
            else if(error.contains("Master"))
            {
                errorName       = ErrorName.SYS_INVALID_MASTER.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MASTER);
            }
            else if(error.contains("AMEX"))
            {
                errorName       = ErrorName.SYS_INVALID_AMEX.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_AMEX);
            }
            else if(error.contains("DINERS"))
            {
                errorName       = ErrorName.SYS_INVALID_DINERS.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DINERS);
            }
            else if(error.contains("JCB"))
            {
                errorName       = ErrorName.SYS_INVALID_JCB.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_JCB);
            }
            else if (error.contains("DISC"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DISC);
                errorName       = ErrorName.SYS_INVALID_DISC.toString();
            }
            else if (error.contains("MAESTRO"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MAESTRO);
                errorName       = ErrorName.SYS_INVALID_MAESTRO.toString();
            }
            else if (error.contains("RUPAY"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_RUPAY);
                errorName       = ErrorName.SYS_INVALID_RUPAY.toString();
            }
            else if (error.contains("INSTAPAYMENT"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_INSTAPAYMENT);
                errorName       = ErrorName.SYS_INVALID_INSTAPAYMENT.toString();
            }
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if((genericCardDetailsVO.getCardNum()!="" || !genericCardDetailsVO.getCardNum().equals("")) && !Functions.isValid(genericCardDetailsVO.getCardNum()))
        {
            error           = "Invalid Credit card number";
            errorCodeListVO = getErrorVO(ErrorName.SYS_LUHN_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LUHN_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", error, "Common", "Invalid Credit card number.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCardExpiryDateCheck()) && !functions.isFutureExpMonthYear(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()))
        {
            error           = "Kindly Pass Valid Card Expiry Month & Year.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_EXP_MONTH_YEAR);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_INVALID_EXP_MONTH_YEAR.toString(),ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", error, "Common", "Invalid Credit card Exp Month & Year.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }


        if("Y".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
        {
            boolean isCardWhitelisted = paymentChecker.isWhitelistedCardnumberforTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            if (!isCardWhitelisted)
            {
                if ("N".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
                {
                    error           = "ERROR!!! Your card number is not whitelisted.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CCWHITELIST);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCWHITELIST.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        if ("Y".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
        {

            String transactionEmail = commonValidatorVO.getAddressDetailsVO().getEmail();

            if (functions.isEmptyOrNull(transactionEmail) )
            {
                error           = "ERROR!!! Email id is required";
                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_EMAIL);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.VALIDATION_EMAIL.toString(), ErrorType.VALIDATION.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }


            List<CardDetailsVO> listOfCards = new ArrayList<CardDetailsVO>();
            boolean isEmailWhitelisted = paymentChecker.isWhitelistedEmailforTransaction(merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId(), commonValidatorVO.getAddressDetailsVO().getEmail(), listOfCards,commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            if (!isEmailWhitelisted)
            {
                error = "ERROR!!! Your card email id is not whitelisted.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_CC_EMAIL_WHITELIST);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CC_EMAIL_WHITELIST.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            String cardNumber   = genericCardDetailsVO.getCardNum();

            String first_six    = cardNumber.substring(0,6);
            String last_four    = cardNumber.substring((cardNumber.length()-4),cardNumber.length());


            if("Y".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
            {
                boolean isCardAvailable = false;
                String isTemp           = "";
                for (CardDetailsVO cardDetailsVO : listOfCards)
                {
                    if (first_six.equalsIgnoreCase(cardDetailsVO.getFirstsix()) && last_four.equalsIgnoreCase(cardDetailsVO.getLastfour()))
                    {
                        isCardAvailable = true;
                        isTemp          = cardDetailsVO.getIsTemp();
                        break;
                    }
                }
                if(isCardAvailable)
                {
                    if(isTemp.equals("Y"))
                    {
                        //Error
                        error           = "ERROR!!! Your card email id is not whitelisted.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_CCWHITELIST);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCWHITELIST.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }

                }
                else
                {
                    paymentChecker.insertWhitelistedCardDetails(merchantDetailsVO.getMemberId(),merchantDetailsVO.getAccountId(),commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getAddressDetailsVO().getEmail());
                }
            }
        }

        //Whitelisting check
        String whitelisting         = terminalVO.getWhitelisting();
        String whitelistdetails[]   = null;
        String card_Holder_Name     = "";
        String expiryDate           = "";
        String ipAddress            = "";
        String nameStr          = "";
        String ipAddressStr     = "";
        String expiryDateStr    = "";
        transactionLogger.debug("getWhitelisting Flag::::"+whitelisting);

        if(functions.isValueNull(whitelisting))
        {
            if (whitelisting.contains(","))
            {
                whitelistdetails = whitelisting.split(",");
            }
            else
            {
                whitelistdetails    = new String[1];
                whitelistdetails[0] = whitelisting;
            }
        }
        if(whitelistdetails!=null)
        {
            for (int i = 0; i < whitelistdetails.length; i++)
            {
                if ("name".equals(whitelistdetails[i]))
                    nameStr     = "name";
                if ("ipAddress".equals(whitelistdetails[i]))
                    ipAddressStr = "ipAddress";
                if ("expiryDate".equals(whitelistdetails[i]))
                    expiryDateStr = "expiryDate";
            }
            if ("name".equals(nameStr) || "ipAddress".equals(ipAddressStr) || "expiryDate".equals(expiryDateStr))
            {

                if ("name".equals(nameStr) && functions.isValueNull(addressDetailsVO.getFirstname()) && functions.isValueNull(addressDetailsVO.getLastname()))
                {
                    if(addressDetailsVO.getFirstname().equals(addressDetailsVO.getLastname()))
                        card_Holder_Name = addressDetailsVO.getFirstname();
                    else
                        card_Holder_Name = addressDetailsVO.getFirstname() + " " + addressDetailsVO.getLastname();
                }
                if ("ipAddress".equals(ipAddressStr))
                {
                    ipAddress = addressDetailsVO.getCardHolderIpAddress();
                    if (!functions.isValueNull(ipAddress))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.VALIDATION_CUSTOMER_IP);
                        error           = ErrorMessages.INVALID_CUSTOMER_IP;
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CC_CARD_DETAILS_WHITELIST.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
                if ("expiryDate".equals(expiryDateStr) && functions.isValueNull(genericCardDetailsVO.getExpMonth()) && functions.isValueNull(genericCardDetailsVO.getExpYear()))
                {
                    int expiryYear = Integer.parseInt(genericCardDetailsVO.getExpYear());
                    expiryYear = expiryYear - 2000;
                    expiryDate = genericCardDetailsVO.getExpMonth() + "/" + expiryYear;
                }

                boolean isCardHolderNameWhitelisted = paymentChecker.isWhitelistedCardDetailsforTransaction(merchantDetailsVO.getMemberId(),merchantDetailsVO.getAccountId(), card_Holder_Name, ipAddress, expiryDate);
                if (!isCardHolderNameWhitelisted)
                {
                    error           = "Your Card Details are not whitelisted.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CC_CARD_DETAILS_WHITELIST);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CC_CARD_DETAILS_WHITELIST.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        // VIP Card Whitelisted
        boolean isVIPCard   = false;
        transactionLogger.debug("getIsCardWhitelisted Flag::::"+terminalVO.getIsCardWhitelisted());
        if("V".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
        {
            boolean isCardWhitelisted = paymentChecker.isWhitelistedCardnumberforTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            if (isCardWhitelisted)
                isVIPCard = true;

            transactionLogger.error("isCardWhitelisted::::::::::::::" + isCardWhitelisted);
        }
        // VIP Email Whitelisted
        boolean isVIPEmail=false;
        if("V".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
        {
            List<CardDetailsVO> listOfCards = new ArrayList<CardDetailsVO>();
            boolean isEmailWhitelisted      = paymentChecker.isWhitelistedEmailforTransaction(merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId(), commonValidatorVO.getAddressDetailsVO().getEmail(), listOfCards,commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            transactionLogger.error("isEmailWhitelisted::::"+isEmailWhitelisted);
            if(isEmailWhitelisted)
                isVIPEmail  = true;
        }

        commonValidatorVO.setVIPCard(isVIPCard);
        commonValidatorVO.setVIPEmail(isVIPEmail);

        //ToDO - Check with Jimmy to remove ths check
        if (commonValidatorVO.getCardType().equals("2") && !limitChecker.isMasterCardSupported(merchantDetailsVO.getMemberId()))
        {
            error           =  "Requested card is not supported:::Please contact support desk.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MC_SUPPORT_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MC_SUPPORT_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
        }

        log.debug("merchantDetailsVO.getIsBlacklistTransaction()--->"+merchantDetailsVO.getIsBlacklistTransaction());
        if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
        {
            String firstSix = "";
            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
            {
                firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
            }
            GatewayAccountService gatewayAccountService = new GatewayAccountService();
            String gatewayCountry                       = gatewayAccountService.getGatewayCountry(commonValidatorVO.getTerminalVO().getAccountId());
            BinResponseVO binResponseVO                 = null;
            binResponseVO                               = functions.getBinDetails(firstSix, gatewayCountry);
            if(binResponseVO != null)
            {
                commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
                commonValidatorVO.getCardDetailsVO().setCountryName(binResponseVO.getCountryname());
                    commonValidatorVO.getCardDetailsVO().setIssuingBank(binResponseVO.getBank());
                //Check Add for Debit and Credit Card terminals
                /*if(commonValidatorVO.getPaymentType().equals("1") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBin_card_type()) && !(commonValidatorVO.getCardDetailsVO().getBin_card_type().equalsIgnoreCase("CREDIT") || commonValidatorVO.getCardDetailsVO().getBin_card_type().equalsIgnoreCase("DEBIT OR CREDIT")))
                {
                    error = "Invalid Credit Card";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CREDIT_CARD);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_CREDIT_CARD.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
                if (commonValidatorVO.getPaymentType().equals("4") && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getBin_card_type()) && !(commonValidatorVO.getCardDetailsVO().getBin_card_type().equalsIgnoreCase("DEBIT") || commonValidatorVO.getCardDetailsVO().getBin_card_type().equalsIgnoreCase("DEBIT OR CREDIT")))
                {
                    error = "Invalid Debit Card";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DEBIT_CARD);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_DEBIT_CARD.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        if(merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            //Moved Blacklist checks from system check step1 to system check step2
            if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error           = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDEMAIL.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            //Added new system check for cardholder name in blacklist entry
            if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                error           = "Customer Name Has Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDNAME.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!isBinBlocked(cardNo.substring(0, 6), merchantDetailsVO.getAccountId(),merchantDetailsVO.getMemberId()))
            {
                error = "Your credit Card is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_CCBLOCK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!isCardBlocked(cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4),commonValidatorVO))
            {
                if(functions.isValueNull(commonValidatorVO.getReason()))
                    error = "Your credit Card is Blocked ("+commonValidatorVO.getReason()+") :::Please contact support for further assistance";
                else
                    error = "Your credit Card is Blocked :::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_CCBLOCK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(functions.isValueNull(commonValidatorVO.getVpa_address())&&!isVPAaddressBlocked(commonValidatorVO.getVpa_address()))
            {
                transactionLogger.error("BlacklistTransaction vpa address-----" + commonValidatorVO.getVpa_address());
                error = "Your vpaAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDVPA);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDVPA.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()) && !isPhoneBlocked(commonValidatorVO.getAddressDetailsVO().getPhone()))
            {
                error = "Your Phone No. is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDPHONE);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDPHONE.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        /*if("1".equals(merchantDetailsVO.getCardTransLimit()))
        {
            limitChecker.checkCardAmountLimitNew(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getTransDetailsVO().getAmount(), cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4), commonValidatorVO, commonValidatorVO.getTerminalId());
        }*/
        if ("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()))
        {
            //Card amount check on Gateway
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardAmountLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel start #########" + date3.getTime());
                limitChecker.checkCardAmountLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
            //Card amount check on terminal
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardAmountLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel start #########" + date3.getTime());
                limitChecker.checkCardAmountLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
        }
        if ("Y".equals(merchantDetailsVO.getIsEmailLimitEnabled()))
        {
            limitChecker.checkDailyTrxnLimitPerEmail(merchantDetailsVO.getAccountId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getAddressDetailsVO().getEmail(),commonValidatorVO);
        }
        //Limit check by Customer Email
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailAmountLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyAmountLimit(commonValidatorVO);
            }
        }
        //Limit check by Customer Phone
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneLimitCheck()))
            {
                limitChecker.checkPhoneDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneAmountLimitCheck()))
            {
                limitChecker.checkPhoneDailyAmountLimit(commonValidatorVO);
            }
        }

        // limit check for Customer Ip
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress())){

            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpAmountLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customre Name
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()) || functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameAmountLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyAmountLimit(commonValidatorVO);
            }
        }



        return commonValidatorVO;
    }

    public CommonValidatorVO performCardRegistrationChecks(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        LimitChecker limitChecker = new LimitChecker();
        ErrorCodeListVO errorCodeListVO = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";
        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();
        Functions functions = new Functions();
        error = Functions.isValidCard(commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardType());
        if (error != null && !error.equals(""))
        {
            String errorName = "";
            if (error.contains("Visa"))
            {
                errorName = ErrorName.SYS_INVALID_VISA.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_VISA);
            }
            else if (error.contains("Master"))
            {
                errorName = ErrorName.SYS_INVALID_MASTER.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MASTER);
            }
            else if (error.contains("AMEX"))
            {
                errorName = ErrorName.SYS_INVALID_AMEX.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_AMEX);
            }
            else if (error.contains("DINERS"))
            {
                errorName = ErrorName.SYS_INVALID_DINERS.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DINERS);
            }
            else if (error.contains("JCB"))
            {
                errorName = ErrorName.SYS_INVALID_JCB.toString();
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_JCB);
            }
            else if (error.contains("DISC"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DISC);
                errorName = ErrorName.SYS_INVALID_DISC.toString();
            }
            else if (error.contains("MAESTRO"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MAESTRO);
                errorName = ErrorName.SYS_INVALID_MAESTRO.toString();
            }
            else if (error.contains("RUPAY"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_RUPAY);
                errorName = ErrorName.SYS_INVALID_RUPAY.toString();
            }
            else if (error.contains("INSTAPAYMENT"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_INSTAPAYMENT);
                errorName = ErrorName.SYS_INVALID_INSTAPAYMENT.toString();
            }
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorName, ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if ((genericCardDetailsVO.getCardNum() != "" || !genericCardDetailsVO.getCardNum().equals("")) && !Functions.isValid(genericCardDetailsVO.getCardNum()))
        {
            error = "Invalid Credit card number";
            errorCodeListVO = getErrorVO(ErrorName.SYS_LUHN_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LUHN_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", error, "Common", "Invalid Credit card number.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if (!functions.isFutureExpMonthYear(genericCardDetailsVO.getExpMonth(), genericCardDetailsVO.getExpYear()))
        {
            error = "Kindly Pass Valid Card Expiry Month & Year.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_EXP_MONTH_YEAR);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_INVALID_EXP_MONTH_YEAR.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", error, "Common", "Invalid Credit card Exp Month & Year.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }


        //ToDO - Check with Jimmy to remove ths check
        if (commonValidatorVO.getCardType().equals("2") && !limitChecker.isMasterCardSupported(merchantDetailsVO.getMemberId()))
        {
            error = "Requested card is not supported:::Please contact support desk.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MC_SUPPORT_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_MC_SUPPORT_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            //Moved Blacklist checks from system check step1 to system check step2
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDEMAIL.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            //Added new system check for cardholder name in blacklist entry
            if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                    error = "Customer Name Has Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDNAME.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            if (!isBinBlocked(cardNo.substring(0, 6), merchantDetailsVO.getAccountId(), merchantDetailsVO.getMemberId()))
            {
                error = "Your credit Card is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_CCBLOCK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!isCardBlocked(cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4),commonValidatorVO))
            {
                if(functions.isValueNull(commonValidatorVO.getReason()))
                    error = "Your credit Card is Blocked ("+commonValidatorVO.getReason()+") :::Please contact support for further assistance";
                else
                    error = "Your credit Card is Blocked :::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_CCBLOCK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (functions.isValueNull(commonValidatorVO.getVpa_address()) && !isVPAaddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error = "Your vpaAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDVPA);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDVPA.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCardRegistrationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        return commonValidatorVO;
    }

    public void checksumVerificationForSTDKit(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        String calchecksum = "";
        Functions functions = new Functions();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        try
        {
            if (functions.isValueNull(genericTransDetailsVO.getTotype()))
            {
                log.debug("inside if totype-----");
                if (functions.isValueNull(commonValidatorVO.getToken()))
                {
                    calchecksum = Functions.generateChecksumDirectKit(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), commonValidatorVO.getToken(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
                else
                {
                    log.debug("inside else totype if----");
                    calchecksum = Functions.generateChecksumV4(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getTotype(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
            }
            else
            {
                log.debug("inside else totype-----");
                if (functions.isValueNull(commonValidatorVO.getToken()))
                {
                    calchecksum = Functions.generateChecksumSTDFlow(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), commonValidatorVO.getToken(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());

                }
                else
                {
                    log.debug("inside else totype else----");
                    calchecksum = Functions.generateChecksumV5(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getAmount(), genericTransDetailsVO.getOrderId(), genericTransDetailsVO.getRedirectUrl(), merchantDetailsVO.getKey(), merchantDetailsVO.getChecksumAlgo());
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
            error = "Error while generating checksum. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_CHECKSUM_ERROR);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, e.getMessage(), e.getCause());
        }

        if (!genericTransDetailsVO.getChecksum().equals(calchecksum))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            /*Transaction request rejected log entry with reason:Partner-MERCHANT_CHECKSUM_MISSMATCH*/
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.MERCHANT_CHECKSUM_MISSMATCH.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
    }

    public CommonValidatorVO performSystemCheckForNetBanking(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        MerchantDetailsVO merchantDetailsVO                 = commonValidatorVO.getMerchantDetailsVO();
        String error                                        = "";
        ErrorCodeListVO errorCodeListVO                     = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        Functions functions                                 = new Functions();
        LimitChecker limitChecker                           = new LimitChecker();
        String paymentMode                                  = commonValidatorVO.getPaymentMode();

        //Block country check by IP
        if (merchantDetailsVO.getBlacklistCountryIp().equalsIgnoreCase("Y"))
        {
            String customerIP       = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
            String ipCountryCode    = functions.getIPCountryShort(customerIP);
            transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP---" + customerIP + "Country---" + ipCountryCode);
            //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
            if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
            {
                if (!isCountryBlocked(ipCountryCode, "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                {
                    error = "Your Country is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            //Moved Blacklist checks from system check step1 to system check step2
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                //error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                error = errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + errorCodeListVO.getListOfError().get(0).getErrorDescription();
                /*Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_EMAIL*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.SYS_BLOCKEDEMAIL.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (functions.isValueNull(commonValidatorVO.getVpa_address()) && !isVPAaddressBlocked(commonValidatorVO.getVpa_address()))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDVPA);
                error = errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + errorCodeListVO.getListOfError().get(0).getErrorDescription();
                /*Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_EMAIL*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.SYS_BLOCKEDVPA.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if(!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(),commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getMerchantDetailsVO().getAccountId()))
            {
                //error = "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                error = errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + errorCodeListVO.getListOfError().get(0).getErrorDescription();
                //*Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_COUNTRY*//
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(),commonValidatorVO.getAddressDetailsVO().getFirstname(),commonValidatorVO.getAddressDetailsVO().getLastname(),commonValidatorVO.getAddressDetailsVO().getEmail(),commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getCardDetailsVO().getExpMonth(),commonValidatorVO.getCardDetailsVO().getExpYear(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getTransDetailsVO().getOrderId(),commonValidatorVO.getTransDetailsVO().getOrderDesc(),commonValidatorVO.getTerminalId(),error, TransReqRejectCheck.SYS_BLOCKEDCOUNTRY.toString(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            //Added new system check for cardholder name in blacklist entry
            if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
            {
                    //error = "Customer Name Has Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                    error = errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + errorCodeListVO.getListOfError().get(0).getErrorDescription();
                /*Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_NAME*/
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.SYS_BLOCKEDNAME.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()) && !isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
            {
                error = "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()) && !isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            {

                error = "Your IpAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()) && !isPhoneBlocked(commonValidatorVO.getAddressDetailsVO().getPhone()))
            {
                error = "Your Phone No. is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDPHONE);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDPHONE.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }


        //Limit check by Customer Email
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailAmountLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customer Phone
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneLimitCheck()))
            {
                limitChecker.checkPhoneDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneAmountLimitCheck()))
            {
                limitChecker.checkPhoneDailyAmountLimit(commonValidatorVO);
            }
        }
        //Limit check by Customre UPI
        if(String.valueOf(PaymentModeEnum.UPI).equals(paymentMode))
        {
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getVpaAddressLimitCheck()))
            {
                limitChecker.checkVPAAddressDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getVpaAddressAmountLimitCheck()))
            {
                limitChecker.checkVPAAddressDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customer IP
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpAmountLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customer Name

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()) || functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameAmountLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyAmountLimit(commonValidatorVO);
            }
        }



        return commonValidatorVO;

    }

    //for REST API
    public CommonValidatorVO performRESTAPISystemCheck(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Functions functions                                         = new Functions();
        CommonInputValidator commonInputValidator                   = new CommonInputValidator();
        RestCommonInputValidator restCommonInputValidator           = new RestCommonInputValidator();
        FailedTransactionLogEntry failedTransactionLogEntry         = new FailedTransactionLogEntry();
        ErrorCodeUtils errorCodeUtils                               = new ErrorCodeUtils();
        String error                                                = "";

        if (commonValidatorVO.getTransactionType() != null)
        {
            commonValidatorVO = performRestTransactionChecksStep1(commonValidatorVO);
            // validation for standalone registration
            if(functions.isValueNull(commonValidatorVO.getTerminalVO().getPaymodeId()))
            {
                error   = error + restCommonInputValidator.validateMandatoryDetailsBasedOnPaymentMode(commonValidatorVO,"REST");
                if(functions.isValueNull(error))
                {
                    commonValidatorVO.setErrorMsg(error);
                    String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
                    return commonValidatorVO;
                }
            }
        }
        else
            commonValidatorVO = performRestStandAlonetokenSystemChecksStep1(commonValidatorVO);

        log.debug("validateRestFlagBasedAddressField---");
        error = error + commonInputValidator.validateRestFlagBasedAddressField(commonValidatorVO, "REST");
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.SYSCHECK.toString());
            return commonValidatorVO;
        }

        /*//cardDetails checking based on workflow "sync" & "async"
        if (functions.isValueNull(commonValidatorVO.getPaymentMode()) && !commonValidatorVO.getPaymentMode().equals("NB"))
        {
            error = error + (commonInputValidator.validateFlagBasedRestCardDetails(commonValidatorVO, "REST"));
        }
        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }*/
        if(commonValidatorVO.getPartnerDetailsVO() != null && commonValidatorVO.getTransactionType() != null)
        {
            String fromtype                         = commonValidatorVO.getTransDetailsVO().getFromtype();
            String accountAddressValidation         = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId()).getAddressValidation();
            AbstractInputValidator paymentProcess   = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
            log.debug("validateIntegrationSpecificParameters---");
            error = error + paymentProcess.validateIntegrationSpecificParameters(commonValidatorVO, "REST", accountAddressValidation);
            if (!functions.isEmptyOrNull(error))
            {
                commonValidatorVO.setErrorMsg(error);
                String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
                return commonValidatorVO;
            }
        }

        if (commonValidatorVO.getCardDetailsVO().getCardNum() != null)
        {
            log.debug("commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration() 2--->" + commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration());
            if (commonValidatorVO.getPartnerDetailsVO() != null  && commonValidatorVO.getTransactionType() == null)
            {
                log.debug("performCommonRegistartionByPartnerChecksStep2----");
                commonValidatorVO = performCommonRegistartionByPartnerChecksStep2(commonValidatorVO);
            }
            else
            {
                log.debug("performCommonSystemChecksStep2---");
                commonValidatorVO   = performCommonSystemChecksStep2(commonValidatorVO);
                error               = commonInputValidator.validateFlagBasedCardDetails(commonValidatorVO, "REST");
                if (functions.isValueNull(error))
                {
                    commonValidatorVO.setErrorMsg(error);
                    String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
                    return commonValidatorVO;
                }
            }

            }

        return commonValidatorVO;
    }

    public CommonValidatorVO performCommonRegistartionByPartnerChecksStep2(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        GenericCardDetailsVO genericCardDetailsVO           = commonValidatorVO.getCardDetailsVO();
        ErrorCodeListVO errorCodeListVO                     = null;
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error                                        = "";
        String errorname                                    = "";

        //Todo : Use Switch for card type
        error = Functions.isValidCard(commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardType());
        if (error != null && !error.equals(""))
        {
            if (error.contains("Visa"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_VISA);
                errorname = ErrorName.SYS_INVALID_VISA.toString();
            }
            else if (error.contains("Master"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MASTER);
                errorname = ErrorName.SYS_INVALID_MASTER.toString();
            }
            else if (error.contains("AMEX"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_AMEX);
                errorname = ErrorName.SYS_INVALID_AMEX.toString();
            }
            else if (error.contains("DINERS"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DINERS);
                errorname = ErrorName.SYS_INVALID_DINERS.toString();
            }
            else if (error.contains("DISC"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_DISC);
                errorname = ErrorName.SYS_INVALID_DISC.toString();
            }
            else if (error.contains("MAESTRO"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_MAESTRO);
                errorname = ErrorName.SYS_INVALID_MAESTRO.toString();
            }
            else if (error.contains("RUPAY"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_RUPAY);
                errorname = ErrorName.SYS_INVALID_RUPAY.toString();
            }
            else if (error.contains("INSTAPAYMENT"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_INSTAPAYMENT);
                errorname = ErrorName.SYS_INVALID_INSTAPAYMENT.toString();
            }
        }
        if (errorCodeListVO != null)
        {
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if ((genericCardDetailsVO.getCardNum() != "" || !genericCardDetailsVO.getCardNum().equals("")) && !Functions.isValid(genericCardDetailsVO.getCardNum()))
        {
            error = "Invalid Credit card number";
            errorCodeListVO = getErrorVO(ErrorName.SYS_LUHN_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_LUHN_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", error, "Common", "Invalid Credit card number.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        return commonValidatorVO;
    }

    //for REST API
    public CommonValidatorVO performRESTAPISystemCheckForMerchantSignup(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        String partnerId = commonValidatorVO.getMerchantDetailsVO().getPartnerId();
        partnerDetailsVO = partnerManager.getPartnerDetails(partnerId);

        if (!Checksum.verifyMD5ChecksumForMerchantSignup(commonValidatorVO.getMerchantDetailsVO().getLogin(), partnerDetailsVO.getPartnerKey(), commonValidatorVO.getMerchantDetailsVO().getWebsite(), addressDetailsVO.getFirstname(), addressDetailsVO.getLastname(), partnerId, commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if (!commonValidatorVO.getMerchantDetailsVO().getNewPassword().equals(commonValidatorVO.getMerchantDetailsVO().getConPassword()))
        {
            error = "Password and confirm password are not matching.";
            errorCodeListVO = getErrorVO(ErrorName.VALIDATION_PASSWORD_MATCH);
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        return commonValidatorVO;
    }

    //for REST API
    public CommonValidatorVO performRESTAPISystemCheckForCardholderRegistration(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

        if (functions.isValueNull(partnerDetailsVO.getPartnerId()))
        {
            String partnerId = commonValidatorVO.getPartnerDetailsVO().getPartnerId();
            if (!ESAPI.validator().isValidInput("partnerId", partnerId, "Numbers", 10, false))
            {
                error = "Invalid request";
                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_PARTNERID);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getPartnerDetailsVO().getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Invalid request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                error = "Invalid request";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MERCHANT_REQUIRED_FOR_REGISTRATION);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            //Checksum calculation with partner details
            if (!Checksum.verifyMD5ChecksumForCardHolderRegistration(partnerId, partnerDetailsVO.getPartnerKey(), addressDetailsVO.getFirstname(), addressDetailsVO.getLastname(), addressDetailsVO.getEmail(), commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            merchantDetailsVO.setPartnerId(partnerDetailsVO.getPartnerId());
        }

        else if (functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            String toid = merchantDetailsVO.getMemberId();
            if (!functions.isValueNull(toid) || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 10, false))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_TOID);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            merchantDetailsVO = commonInputValidator.getMerchantConfigDetailsByLogin(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_TOID_INVALID);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId()); //fetch the partner details with partnerId
            if (partnerDetailsVO == null)
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_UNAUTHORIZE_PARTNER);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if ("N".equals(partnerDetailsVO.getIsMerchantRequiredForCardholderRegistration()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_MERCHANT_CAN_NOT_PROCEED_REGISTRATION);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!Checksum.verifyMD5ChecksumForCardHolderRegistration(toid, merchantDetailsVO.getKey(), addressDetailsVO.getFirstname(), addressDetailsVO.getLastname(), addressDetailsVO.getEmail(), commonValidatorVO.getTransDetailsVO().getChecksum()))
            {
                error = "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
            {
                //Moved Blacklist checks from system check step1 to system check step2
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                if (functions.isValueNull(commonValidatorVO.getVpa_address()) && !isVPAaddressBlocked(commonValidatorVO.getVpa_address()))
                {
                    error = "Your vpaAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDVPA);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(), merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                {
                    error = "Your Country is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                //Added new system check for cardholder name in blacklist entry
                if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
                {
                        error = "Customer Name Has Blocked:::Please contact support for further assistance";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }
        else
        {
            error = "Customer Name Has Blocked:::Please contact support for further assistance";
            errorCodeListVO = getErrorVO(ErrorName.VALIDATION_REQUEST_NULL);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if ("N".equalsIgnoreCase(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            error = "You cannot register token";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TOKEN_ALLOWED);
            PZExceptionHandler.raiseConstraintViolationException("RegistrationHelper.class", "performVerificationWithStandAloneToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
        return commonValidatorVO;
    }

    public CommonValidatorVO performRESTSystemCheck(CommonValidatorVO directKitValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Functions functions = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        CommonInputValidator commonInputValidator = new CommonInputValidator();
        directKitValidatorVO = performRestSystemChecksStep1(directKitValidatorVO);
        performCommonSystemChecksStep2(directKitValidatorVO);
        String error = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String addressValidation = directKitValidatorVO.getTerminalVO().getAddressValidation();
        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(directKitValidatorVO.getMerchantDetailsVO().getAccountId()));
        error = paymentProcess.validateIntegrationSpecificParameters(directKitValidatorVO, "DKIT", addressValidation);

        if (!functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(directKitValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
            return directKitValidatorVO;
        }

        error = commonInputValidator.validateFlagBasedAddressField(directKitValidatorVO, "DKIT");

        if (!functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(directKitValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
            return directKitValidatorVO;
        }

        error = commonInputValidator.validateFlagBasedCardDetails(directKitValidatorVO, "DKIT");

        if (!functions.isEmptyOrNull(error))
        {
            directKitValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(directKitValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
            return directKitValidatorVO;
        }

        return directKitValidatorVO;

    }


    public String isRecurringAllowed(String accountid) throws PZDBViolationException
    {
        Connection con = null;
        String recurringFlag = "";
        try
        {
            con = Database.getConnection();
            String sql = "select is_recurring from gateway_accounts where accountid=?";
            PreparedStatement p = con.prepareStatement(sql);
            p.setString(1, accountid);
            ResultSet rs = p.executeQuery();
            if (rs.next())
            {
                recurringFlag = rs.getString("is_recurring");
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isRecurringAllowed()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isRecurringAllowed()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return recurringFlag;
    }

    public void performCommonSystemRestrictedTicketCheck(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        log.debug("Entering into performCommonSystemRestrictedTicketCheck");
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        SplitPaymentVO splitPaymentVO = new SplitPaymentVO();
        if (getIsRestrictedTicketActive(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId()))
        {

            if (isValidTicket(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getAmount()))
            {
                log.debug("Valid Restricted Ticket Amount.");
            }
            else
            {
                error = "Invalid Restricted Ticket Amount.";
                log.debug(error);
                errorCodeListVO = getErrorVO(ErrorName.SYS_RESTRICTED_TICKET_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_RESTRICTED_TICKET_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemRestrictedTicketCheck", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        else
        {
            log.debug("No Active Terminal To Restricted Ticket.");
        }

    }

    public boolean isValidTicket(String memberId, String terminalId, String amount) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValidTicket = false;
        try
        {
            con = Database.getConnection();
            String sql = "select id from member_amount_mapping where memberid=? and terminalid=? and amount=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, terminalId);
            pstmt.setString(3, amount);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                isValidTicket = true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isValidTicket()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isValidTicket()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isValidTicket;
    }

    public boolean getIsRestrictedTicketActive(String memberId, String terminalId) throws PZDBViolationException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isRestrictedTicketActive = false;
        try
        {
            con = Database.getConnection();
            String sql = "select terminalid from member_account_mapping where memberid=? and terminalid=? and isRestrictedTicketActive=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.setString(2, terminalId);
            pstmt.setString(3, "Y");
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                isRestrictedTicketActive = true;
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "getIsRestrictedTicketActive()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "getIsRestrictedTicketActive()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(con);
        }
        return isRestrictedTicketActive;
    }

    public boolean isCardBlocked(String firstSix, String lastFour,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        boolean isCardBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id,remark FROM blacklist_cards WHERE first_six=? AND last_four=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, firstSix);
            p.setString(2, lastFour);
            log.debug("isCardBlocked qry--->" + p);
            rs = p.executeQuery();
            if (rs.next())
            {
                isCardBlocked = false;
                if(commonValidatorVO != null)
                    commonValidatorVO.setReason(rs.getString("remark"));
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isCardBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCardBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in isCardBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCardBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isCardBlocked;
    }

    public boolean isIpAddressBlocked(String ipAddress, String memberId) throws PZDBViolationException
    {
        boolean isIpBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_ip WHERE ipaddress=? AND memberId=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, ipAddress);
            p.setString(2, memberId);
            transactionLogger.debug("isIpAddressBlocked----" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                isIpBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isIpAddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isIpAddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isIpBlocked;
    }

    public boolean isIpAddressBlockedGlobal(String ipAddress) throws PZDBViolationException
    {
        boolean isIpBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_ip WHERE ipaddress=? AND memberId IS NULL ";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, ipAddress);
            transactionLogger.debug("isIpAddressBlocked----" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                isIpBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isIpAddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isIpAddressBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isIpAddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isIpBlocked;
    }

    public boolean isEmailAddressBlocked(String emailAddress) throws PZDBViolationException
    {
        boolean isEmailBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_email WHERE emailAddress=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, emailAddress);

            rs = p.executeQuery();
            if (rs.next())
            {
                isEmailBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isEmailAddressBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isEmailAddressBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isEmailAddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isEmailBlocked;
    }

    public boolean isVPAaddressBlocked(String vpaAddress) throws PZDBViolationException
    {transactionLogger.error(" in side sql query isVPAaddressBlocked vpa address-----" + vpaAddress);
        boolean isVPABlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        Functions functions = new Functions();
        try
        {
            String vpaPrefix = functions.getVpaAddressPrefix(vpaAddress);
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_vpaAddress WHERE vpaAddress=? or vpaprefix = ?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, vpaAddress);
            p.setString(2, vpaPrefix);

            rs = p.executeQuery();
            if (rs.next())
            {
                isVPABlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isVPAaddressBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isVPAaddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isVPAaddressBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isVPAaddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isVPABlocked;
    }
    public boolean isPhoneBlocked(String phone) throws PZDBViolationException
    {
        boolean isPhoneBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_phone WHERE phone=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, phone);

            rs = p.executeQuery();
            if (rs.next())
            {
                isPhoneBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isPhoneBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isVPAaddressBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isPhoneBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isVPAaddressBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isPhoneBlocked;
    }

    public boolean isCountryBlocked(String countryCode, String telnocc, String memberid, String accountid) throws PZDBViolationException
    {
        boolean isCountryBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_country WHERE accountid=? and memberid=? and (country_code=? OR telnocc=?)";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, accountid);
            p.setString(2, memberid);
            p.setString(3, countryCode);
            p.setString(4, telnocc);

            transactionLogger.debug("isCountryBlocked---" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                isCountryBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isCountryBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isCountryBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isCountryBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isCountryBlocked;
    }

    public boolean isNameBlocked(String name) throws PZDBViolationException
    {
        boolean isNameBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        Functions functions=new Functions();
        try
        {
            if(functions.isValueNull(name.trim()))
            {
                conn = Database.getConnection();
                String query = "SELECT id FROM blacklist_name WHERE name=?";
                PreparedStatement p = conn.prepareStatement(query);
                p.setString(1, name);
                rs = p.executeQuery();
                if (rs.next())
                {
                    isNameBlocked = false;
                }
            }
        }
        catch (SystemError se)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isNameBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isNameBlocked;
    }

    public String allowedEmailCountfromCard(String firstSix, String lastFour) throws PZDBViolationException
    {
        String emailCount = "";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement p = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT COUNT(DISTINCT emailaddr) FROM bin_details WHERE first_six=? AND last_four=?";

            p = conn.prepareStatement(query);
            p.setString(1, firstSix);
            p.setString(2, lastFour);

            transactionLogger.debug("allowedEmailCountfromCard---" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                emailCount = rs.getString(1);
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in allowedEmailCountfromCard---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "allowedEmailCountfromCard()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in allowedEmailCountfromCard---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "allowedEmailCountfromCard()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
        }
        return emailCount;
    }

    public String allowedCardCountfromEmail(String email) throws PZDBViolationException
    {
        String cardCount = "";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement p = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT COUNT(DISTINCT first_six, last_four) FROM bin_details WHERE emailaddr=?";

            p = conn.prepareStatement(query);
            p.setString(1, email);

            transactionLogger.debug("allowedCardCountfromEmail---" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                cardCount = rs.getString(1);
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in allowedCardCountfromEmail---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "allowedCardCountfromEmail()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in allowedCardCountfromEmail---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "allowedCardCountfromEmail()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
            Database.closePreparedStatement(p);
            Database.closeResultSet(rs);
        }
        return cardCount;
    }

    public boolean isBinBlocked(String firstSix, String accountid, String memberid) throws PZDBViolationException
    {
        boolean isBinBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement p = null;

        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_bin WHERE ? BETWEEN startBin AND endBin AND accountid=? AND memberid=?";
            p = conn.prepareStatement(query);
            p.setString(1, firstSix);
            p.setString(2, accountid);
            p.setString(3, memberid);
            transactionLogger.debug("isBinBlocked---" + p);

            rs = p.executeQuery();
            if (rs.next())
            {
                isBinBlocked = false;
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isBinBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isBinBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException in isBinBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isBinBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch (SQLException e)
                {
                    transactionLogger.error("SQLException--->",e);
                }
            }
            if (p != null)
            {
                try
                {
                    p.close();
                    p = null;
                }
                catch (SQLException e)
                {
                    transactionLogger.error("SQLException--->", e);
                }
            }
            try
            {
                if (conn != null && !conn.isClosed())
                {
                    conn.close();
                    conn = null;
                }
            }
            catch (SQLException e)
            {
                transactionLogger.error("SQLException--->",e);
            }
        }
        return isBinBlocked;
    }

    public boolean isBankAccountnoBlocked(String bankAccountno) throws PZDBViolationException
    {transactionLogger.error(" in side sql query isbankAccountnoBlocked -----" + bankAccountno);
        boolean isbankAccountnoBlocked = true;
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT id FROM blacklist_bankAccountno WHERE bankAccountno=?";
            PreparedStatement p = conn.prepareStatement(query);
            p.setString(1, bankAccountno);

            rs = p.executeQuery();
            if (rs.next())
            {
                isbankAccountnoBlocked = false;
                transactionLogger.error("isbankAccountnoBlocked isbankAccountnoBlocked---"+isbankAccountnoBlocked);
            }
        }
        catch (SystemError se)
        {
            transactionLogger.error("SystemError in isbankAccountnoBlocked---", se);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isBankAccountnoBlocked()", null, "Common", "DB Connection Error:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, se.getMessage(), se.getCause());
        }
        catch (SQLException e)
        {
            transactionLogger.error("SystemError in isBankAccountnoBlocked---", e);
            PZExceptionHandler.raiseDBViolationException("TransactionHelper.java", "isBankAccountnoBlocked()", null, "Common", "SQL Exception thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return isbankAccountnoBlocked;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        log.debug("error in helper---------->" + errorName.toString());
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeVO.setErrorName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    public String checkorderuniqueness(String toid, String fromtype, String description)
    {
        transactionLogger.debug("checkorderuniqueness---");
        String str = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            /*String query = "select * from transactions where toid = ? and description = ? order by dtstamp desc";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, toid);
            pstmt.setString(2, description);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
            {
                str = "Order is not Unique";
            }*/

            /*if (str.equals(""))
            {*/

            String transaction_table = "transaction_common";
            transactionLogger.debug("InputValidatorUtils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select trackingid from " + transaction_table + " where toid = ? and description = ? order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            pstmt1.setString(2, description);
            log.debug("order unique query---" + pstmt1);
            transactionLogger.debug("order unique query---" + pstmt1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Your Transaction is already being processed. Kindly try to place transaction with unique orderId.";
            }
        }
        catch (Exception e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    public String checkOrderUniquenessStatus(String toid, String description,String isUniqueOrderIdRequired)
    {
        transactionLogger.debug("checkorderuniqueness---");
        String str = "";
        Connection con = null;
        try
        {
            String statusNotIn="";
            if("Y".equalsIgnoreCase(isUniqueOrderIdRequired))
                statusNotIn="\'failed\'";
            else
                statusNotIn="'begun','failed','authfailed'";

            con = Database.getConnection();
            String transaction_table = "transaction_common";
            transactionLogger.debug("InputValidatorUtils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select trackingid from " + transaction_table + " where toid = ? and description = ? and status not in ("+statusNotIn+")order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            pstmt1.setString(2, description);
            log.debug("order unique query---" + pstmt1);
            transactionLogger.debug("order unique query---" + pstmt1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Your Transaction is already being processed. Kindly try to place transaction with unique orderId.";
            }
        }
        catch (Exception e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    //for Paxlife & Rest API
    public CommonValidatorVO performRestSystemChecksStep1(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;

        Transaction transaction = new Transaction();
        LimitChecker limitChecker = new LimitChecker();
        PaymentChecker paymentChecker = new PaymentChecker();

        //getting value from VO
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        RecurringBillingVO recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        TerminalVO terminalVO = null;
        TerminalManager terminalManager = new TerminalManager();
        String cardNo = commonValidatorVO.getCardDetailsVO().getCardNum();

        Functions functions = new Functions();
        String error = "";
        int paymentType = 0;
        int cardType = 0;
        int terminalId = 0;
        log.debug("------limit-----" + merchantDetailsVO.getCheckLimit());
        transactionLogger.debug("------limit-----" + merchantDetailsVO.getCheckLimit());

        if (functions.isValueNull(commonValidatorVO.getPaymentType()))
        {
            paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
        }
        if (functions.isValueNull(commonValidatorVO.getCardType()))
        {
            cardType = Integer.parseInt(commonValidatorVO.getCardType());
        }
        if (functions.isValueNull(commonValidatorVO.getTerminalId()) && functions.isNumericVal(commonValidatorVO.getTerminalId()))
        {
            terminalId = Integer.parseInt(commonValidatorVO.getTerminalId());
        }
        //Card Limit Check per partner and Bank
        transactionLogger.debug("Card Limit Flag from Partner---" + merchantDetailsVO.getBankCardLimit());
        transactionLogger.debug("Partner ID---" + merchantDetailsVO.getPartnerId());
        transactionLogger.debug("Gateway Fromtype---" + commonValidatorVO.getTransDetailsVO().getFromtype());
        transactionLogger.debug("Currency---" + commonValidatorVO.getTransDetailsVO().getCurrency());

        log.debug("currency in helper---" + commonValidatorVO.getTransDetailsVO().getCurrency());
        String accountId = "";
        if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && terminalId == 0)
        {
            log.debug("if inside autoterminal---" + merchantDetailsVO.getAutoSelectTerminal());
            if (merchantDetailsVO.getCheckLimit().equals("1"))
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    log.debug("AutoSelectTerminal is Y and CheckLimit = 1 and Filling terminalVO basis on currency");
                    terminalVO = limitChecker.getTerminalBasedOnCurrency(commonValidatorVO, true);
                }
                else
                    terminalVO = limitChecker.getTerminalBasedOnAccountID(commonValidatorVO,/*inputValidatorRequestVO.getCheck_limit()*/true);
            }
            else
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                }
                else
                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType);
            }
            if (terminalVO == null) // if all terminal is inActive for the account
            {
                //error= "Terminal Id is not active for your account. Please check your Technical specification.";
                /*errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
*/
                error = "Payment type or card type or currency is not valid.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_PAYMENT_CARD_CURRENCY.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);

            }
            else if ("N".equals(terminalVO.getIsActive()))
            {
                error = "Terminal Id is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, TransReqRejectCheck.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            accountId = terminalVO.getAccountId();

            if (accountId == null || "0".equalsIgnoreCase(accountId) || accountId.equals(""))
            {
                error = "Account ID-Payment Mode or Card Type requested by you is not valid for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_ACCOUNTID);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_ACCOUNTID.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if ("-1".equalsIgnoreCase(accountId))
            {
                error = "Limits-Member's account Amount Limit exceeded. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_ACCOUNTID_LIMIT);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNTID_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setTerminalVO(terminalVO);
            limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

        }
        else
        {
            log.debug("else inside autoterminal---" + merchantDetailsVO.getAutoSelectTerminal());

            log.debug("else currency---" + commonValidatorVO.getTransDetailsVO().getCurrency());

            /*if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
            {
                log.debug("AutoSelectTerminal is N and Filling terminalVO basis on currency");
                terminalVO = terminalManager.getCardIdAndPaymodeIdFromTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType());
            }
            else*/
            terminalVO = terminalManager.getMemberTerminalfromTerminal(commonValidatorVO.getTerminalId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(),commonValidatorVO.getAccountId());

            if (terminalVO == null)
            {
                // terminal not valid
                error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
            {
                // terminal not not active
                error = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            merchantDetailsVO.setAccountId(String.valueOf(terminalVO.getAccountId()));

            limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

            if (merchantDetailsVO.getCheckLimit().equals("1"))
            {
                limitChecker.checkAmountLimitForTerminalNew(genericTransDetailsVO.getAmount(), commonValidatorVO.getTerminalId() + "", merchantDetailsVO.getMemberId(), commonValidatorVO);

            }
            commonValidatorVO.setTerminalVO(terminalVO);
        }
        if ("Y".equalsIgnoreCase(merchantDetailsVO.getBankCardLimit()))
        {
            limitChecker.checkCardLimitPerBank(cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4), commonValidatorVO);
        }

        String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
        String fromid = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getMerchantId();
        String currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();

        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);// genericTransDetailsVO.setFromtype(fromtype);
        commonValidatorVO.getTransDetailsVO().setFromid(fromid);
        commonValidatorVO.getTransDetailsVO().setCurrency(currency);

        if (currency.equals("JPY") && !paymentChecker.isAmountValidForJPY(currency, genericTransDetailsVO.getAmount()))
        {
            error = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
            errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_JPY_CURRENCY_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        String uniqueorder = null;

        uniqueorder = checkorderuniqueness(merchantDetailsVO.getMemberId(), fromtype, genericTransDetailsVO.getOrderId());

        Double amt = new Double(genericTransDetailsVO.getAmount());
        if (!uniqueorder.equals(""))
        {
            error = commonValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id " + uniqueorder;
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if (commonValidatorVO.getRecurringBillingVO() != null && !commonValidatorVO.getRecurringBillingVO().equals("") && recurringBillingVO.getReqField1() != null && !recurringBillingVO.getReqField1().equals("") && "N".equalsIgnoreCase(isRecurringAllowed(accountId)))
        {
            error = "The functionality is not allowed for account. Please contact support for help:::";
            errorCodeListVO = getErrorVO(ErrorName.SYS_RECURRINGALLOW);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_RECURRINGALLOW.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        //Added Restricted Amount Check in CommonSystemCheckStep1
        if ("Y".equals(commonValidatorVO.getMerchantDetailsVO().getIsRestrictedTicket()))
        {
            performCommonSystemRestrictedTicketCheck(commonValidatorVO);
        }
        // VIP Card Whitelisted
        boolean isVIPCard = true;
        transactionLogger.debug("getIsCardWhitelisted Flag::::" + terminalVO.getIsCardWhitelisted());
        if ("V".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
        {
            boolean isCardWhitelisted = paymentChecker.isWhitelistedCardnumberforTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            if (isCardWhitelisted)
                isVIPCard = false;

            transactionLogger.debug("isCardWhitelisted::::::::::::::" + isCardWhitelisted);
        }
        // VIP Email Whitelisted
        boolean isVIPEmail = true;
        if ("V".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
        {
            List<CardDetailsVO> listOfCards = new ArrayList<CardDetailsVO>();
            boolean isEmailWhitelisted = paymentChecker.isWhitelistedEmailforTransaction(merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId(), commonValidatorVO.getAddressDetailsVO().getEmail(), listOfCards,commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            transactionLogger.debug("isEmailWhitelisted::::" + isEmailWhitelisted);
            if (isEmailWhitelisted)
                isVIPEmail = false;
        }
        if (isVIPCard && isVIPEmail)
        {
            //Block country check by IP
            if (merchantDetailsVO.getBlacklistCountryIp().equalsIgnoreCase("Y") && functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
            {
                String customerIP = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                String ipCountryCode = functions.getIPCountryShort(customerIP);
                transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP PAXLife---" + customerIP + "Country---" + ipCountryCode);
                //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
                {
                    if (!isCountryBlocked(ipCountryCode, "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                    {
                        error = "Your Country is Blocked:::Please contact support for further assistance";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }

            //Block country check by BIN
            if (merchantDetailsVO.getBlacklistCountryBin().equalsIgnoreCase("Y"))
            {
                transactionLogger.error("Inside getBlacklistCountryBin Check BIN Country PAXLife---" + commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                {
                    if (!isCountryBlocked(commonValidatorVO.getCardDetailsVO().getCountry_code_A2(), "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                    {
                        error = "Your Country is Blocked:::Please contact support for further assistance";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }

            // country check by BIN and IP Mismatch
            if (merchantDetailsVO.getBlacklistCountryBinIp().equalsIgnoreCase("Y"))
            {
                String customerIP = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                String ipCountryCode = functions.getIPCountryShort(customerIP);
                String binCountryCode = commonValidatorVO.getCardDetailsVO().getCountry_code_A2();

                transactionLogger.error("Inside getBlacklistCountryBinIp Check Customer IP PAXLife---" + customerIP + "---IPCountry---" + ipCountryCode + "---BIN CountryCode---" + binCountryCode);
                //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode) && functions.isValueNull(binCountryCode))
                {
                    if (!ipCountryCode.equals(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                    {
                        error = "Bin IP Country Mismatch";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BINIPCOUNTRYMISMATCH);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }
        }
        if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            if (!isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getIp(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
            {
                error = "Your IpAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDEMAIL.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            /*if(!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(),merchantDetailsVO.getMemberId(),merchantDetailsVO.getAccountId()))
            {
                error = "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }*/
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        return commonValidatorVO;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //REST Transactions API (seperate method added because of IP blocked checking)
    public CommonValidatorVO performRestTransactionChecksStep1(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        ErrorCodeListVO errorCodeListVO = null;

        Transaction transaction         = new Transaction();
        LimitChecker limitChecker       = new LimitChecker();
        PaymentChecker paymentChecker   = new PaymentChecker();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        //getting value from VO
        MerchantDetailsVO merchantDetailsVO         = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        RecurringBillingVO recurringBillingVO       = commonValidatorVO.getRecurringBillingVO();
        GenericAddressDetailsVO addressDetailsVO    = commonValidatorVO.getAddressDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO   = commonValidatorVO.getCardDetailsVO();
        TerminalVO terminalVO           = null;
        TerminalManager terminalManager = new TerminalManager();
        LimitRouting limitRouting       = new LimitRouting();
        LinkedHashMap<String,TerminalVO> terminalMap = null;

        Functions functions = new Functions();
        String cardNo       = "";
        if (commonValidatorVO.getCardDetailsVO() != null)
            cardNo  = commonValidatorVO.getCardDetailsVO().getCardNum();
        String error    = "";
        int paymentType = 0;
        int cardType    = 0;
        int terminalId  = 0;
        log.debug("------limit-----" + merchantDetailsVO.getCheckLimit());
        transactionLogger.debug("------limit-----" + merchantDetailsVO.getCheckLimit());

        if (functions.isValueNull(commonValidatorVO.getPaymentType()))
        {
            paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
        }
        if (functions.isValueNull(commonValidatorVO.getCardType()))
        {
            cardType = Integer.parseInt(commonValidatorVO.getCardType());
        }
        if (functions.isValueNull(commonValidatorVO.getTerminalId()) && functions.isNumericVal(commonValidatorVO.getTerminalId()))
        {
            terminalId = Integer.parseInt(commonValidatorVO.getTerminalId());
        }

        log.debug("currency in helper---" + commonValidatorVO.getTransDetailsVO().getCurrency());
        String accountId = "";

        if (merchantDetailsVO.getCheckLimit().equals("1"))
        {
            Date date3 = new Date();
            transactionLogger.debug("TransactionHelper checkAmountLimit start #########" + date3.getTime());
            limitChecker.checkAmountLimit(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkAmountLimit end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkAmountLimit diff #########" + (new Date().getTime() - date3.getTime()));
                /*if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    log.debug("AutoSelectTerminal is Y and CheckLimit = 1 and Filling terminalVO basis on currency");
                    terminalVO = limitChecker.getTerminalBasedOnCurrency(commonValidatorVO, true);
                }
                else
                    terminalVO = limitChecker.getTerminalBasedOnAccountID(commonValidatorVO,inputValidatorRequestVO.getCheck_limit()true);*/
        }

        transactionLogger.error("TerminalId->----" + terminalId);
        if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && merchantDetailsVO.getCard_velocity_check().equals("Y") && terminalId != 0)
        {
            terminalId = 0;
        }
        else if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && !merchantDetailsVO.getBinRouting().equals("N") && terminalId != 0)
        {
            terminalId = 0;
        }
        transactionLogger.error("TerminalId->>----" + terminalId);

        if (merchantDetailsVO.getAutoSelectTerminal().equals("Y") && terminalId == 0)
        {
            log.error("if inside autoterminal---" + merchantDetailsVO.getAutoSelectTerminal());
            log.error("merchantDetailsVO.getCard_velocity_check()------" + merchantDetailsVO.getCard_velocity_check());
            log.error("commonValidatorVO.getMerchantDetailsVO().getLimitRouting()------"+commonValidatorVO.getMerchantDetailsVO().getLimitRouting());
            log.error("merchantDetailsVO.getCheckLimit()------" + merchantDetailsVO.getCheckLimit());
            log.error("merchantDetailsVO.getCardTransLimit()------" + merchantDetailsVO.getCardTransLimit());
            log.error("merchantDetailsVO.getCardCheckLimit()------" + merchantDetailsVO.getCardCheckLimit());

            if (merchantDetailsVO.getCard_velocity_check().equals("Y") && merchantDetailsVO.getCardTransLimit().equals("0")  && merchantDetailsVO.getCardCheckLimit().equals("0"))
            {
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    transactionLogger.debug("inside CardDetailsVO().getCardNum()------------" + commonValidatorVO.getCardDetailsVO().getCardNum());
                    if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()))
                    {
                        transactionLogger.debug("AutoSelectTerminal is Y and CheckLimit = 1 and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                        terminalVO = limitChecker.getTerminalBasedOnCurrencyCardVelocity(commonValidatorVO, true);
                    }
                    else
                    {
                        terminalVO = limitChecker.getTerminalBasedOnAccountIDCardVelocity(commonValidatorVO, true);
                    }
                }
            }
            if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()) && merchantDetailsVO.getBinRouting().equalsIgnoreCase("N") && !functions.isValueNull(commonValidatorVO.getAccountId()))
            {
                Date date4 = new Date();
                transactionLogger.error("TransactionHelper LimitRouting start #########" + date4.getTime());
                terminalMap = terminalManager.getTerminalFromCurrency(commonValidatorVO);
                Date date3  = new Date();
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                terminalMap = limitRouting.getTerminalVOBasedOnAmountLimitRouting(terminalMap, commonValidatorVO);
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                commonValidatorVO.setTerminalMapLimitRouting(terminalMap);
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    date3 = new Date();
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting start #########" + date3.getTime());
                    terminalVO  = limitRouting.getTerminalVOBasedOnCardAndCardAmountLimitRouting(terminalMap,commonValidatorVO);
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting end #########" + new Date().getTime());
                    transactionLogger.error("TransactionHelper getTerminalVOBasedOnCardAndCardAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));

                    if(terminalVO == null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error                   = "Terminal ID provided by you is not valid.";
                        errorCodeListVO         = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, commonValidatorVO.getErrorName().toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }

                else if(terminalMap.size()>0){
                    for (Map.Entry entry : terminalMap.entrySet())
                    {
                        String key= (String) entry.getKey();
                        terminalVO=terminalMap.get(key);
                        break;
                    }


                }
                transactionLogger.error("TransactionHelper LimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper LimitRouting diff #########" + (new Date().getTime() - date4.getTime()));
            }
            else
            {
                if (!merchantDetailsVO.getCard_velocity_check().equals("Y"))
                {

                    if (!"N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()) && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()) && !functions.isValueNull(commonValidatorVO.getAccountId()))
                    {
                        String firstSix = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                        String cNum     = commonValidatorVO.getCardDetailsVO().getCardNum();
                        if("Card".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
                        {
                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                            {
                                if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                                {
                                    //Giving first priority to specific currency based terminal
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), cNum);
                                    //If specific currency based terminal not found
                                    if (terminalVO == null)
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL", cNum);
                                        if (terminalVO != null)
                                            terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                    }
                                    //TODO set if terminal vo null
                                    //terminal picked up with non whitelisted bin based on specific currency
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                                    if (terminalVO == null)
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                                        if (terminalVO != null)
                                            terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                    }
                                }
                                else
                                {
                                    log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), cNum);
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                                }
                            }
                            else
                            {
                                terminalVO = terminalManager.getAccountIdTerminalVOforBinRoutingByCardNumber(merchantDetailsVO.getMemberId(), paymentType, cardType, cNum);
                                if (terminalVO == null)
                                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType);
                            }
                        }
                        else if("Bin_Country".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getBinRouting()))
                        {
                            BinVerificationManager binVerificationManager   = new BinVerificationManager();
                            BinResponseVO binResponseVO                     = binVerificationManager.getBinDetailsFromFirstSix(functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum()));
                            String country                                  = binResponseVO.getCountrycodeA3();
                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                            {
                                if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                                {
                                    //Giving first priority to specific currency based terminal
                                    if(functions.isValueNull(country))
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), country);
                                        //If specific currency based terminal not found
                                        if (terminalVO == null)
                                        {
                                            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL", country);
                                            if (terminalVO != null)
                                                terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                        }
                                    }
                                    //TODO set if terminal vo null
                                    //terminal picked up with non whitelisted bin based on specific currency
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                                    if (terminalVO == null)
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                                        if (terminalVO != null)
                                            terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                    }
                                }
                                else
                                {
                                    log.debug("Bin Country AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                                    if(functions.isValueNull(country))
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), country);
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                                }
                            }
                            else
                            {
                                transactionLogger.error("---inside else----");
                                if(functions.isValueNull(country))
                                    terminalVO = terminalManager.getAccountIdTerminalVOforBinCountryRouting(merchantDetailsVO.getMemberId(), paymentType, cardType, country);
                                if (terminalVO == null)
                                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType);
                            }
                        }
                        else
                        {
                            if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                            {
                                if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                                {
                                    //Giving first priority to specific currency based terminal
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), firstSix);

                                    //If specific currency based terminal not found
                                    if (terminalVO == null)
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL", firstSix);
                                        if (terminalVO != null)
                                            terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                    }
                                    //TODO set if terminal vo null
                                    //terminal picked up with non whitelisted bin based on specific currency
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());

                                    if (terminalVO == null)
                                    {
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                                        if (terminalVO != null)
                                            terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                                    }
                                }
                                else
                                {
                                    log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(), firstSix);
                                    if (terminalVO == null)
                                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                                }
                            }
                            else
                            {
                                terminalVO = terminalManager.getAccountIdTerminalVOforBinRouting(merchantDetailsVO.getMemberId(), paymentType, cardType, firstSix);
                                if (terminalVO == null)
                                    terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType);
                            }
                        }
                    }
                    else
                    {
                        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                        {
                            if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                            {
                                terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getAccountId());
                                if (terminalVO == null)
                                    terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL",commonValidatorVO.getAccountId());
                                if (terminalVO != null)
                                    terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                            }
                            else
                            {
                                log.debug("AutoSelectTerminal is Y and Filling terminalVO basis on currency" + commonValidatorVO.getTransDetailsVO().getCurrency());
                                terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getAccountId());
                            }
                        }

                        else
                            terminalVO = transaction.getAccountIdTerminalVO(merchantDetailsVO.getMemberId(), paymentType, cardType,commonValidatorVO.getAccountId());
                    }
                }
            }
            if (terminalVO == null) // if all terminal is inActive for the account
            {
                error = "Terminal Id is not active for your account. Please check your Technical specification.";
                if(functions.isValueNull(commonValidatorVO.getAccountId()))
                {
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_ACCOUNT_MODE_BRAND);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_ACCOUNT.toString(), ErrorType.SYSCHECK.toString());
                }
                else
                {
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND.toString(), ErrorType.SYSCHECK.toString());
                }
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            else if ("N".equals(terminalVO.getIsActive()))
            {
                error = "Terminal Id is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            accountId = terminalVO.getAccountId();
            transactionLogger.error("accountid-----"+accountId);

            if (accountId == null || "0".equalsIgnoreCase(accountId) || accountId.equals(""))
            {
                error = "Account ID-Payment Mode or Card Type requested by you is not valid for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_ACCOUNTID);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_ACCOUNTID.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if ("-1".equalsIgnoreCase(accountId))
            {
                error = "Limits-Member's account Amount Limit exceeded. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_ACCOUNTID_LIMIT);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_ACCOUNTID_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            commonValidatorVO.setPaymentMode(GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId()));
            commonValidatorVO.setPaymentBrand(GatewayAccountService.getPaymentBrand(terminalVO.getCardTypeId()));
            commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
            merchantDetailsVO.setAccountId(accountId);
            commonValidatorVO.setTerminalVO(terminalVO);
            limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);
        }
        else
        {
            log.error("else inside autoterminal---" + merchantDetailsVO.getAutoSelectTerminal());

            log.error("else currency---" + commonValidatorVO.getTransDetailsVO().getCurrency());
            if ("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()) && !functions.isValueNull(commonValidatorVO.getAccountId()))
            {
                Date date4 = new Date();
                transactionLogger.error("TransactionHelper LimitRouting start #########" + date4.getTime());
                terminalMap = new LinkedHashMap<>();
                terminalVO  = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                if (terminalVO == null)
                {
                    // terminal not valid
                    error           = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
                commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));

                transactionLogger.error("--Get All terminalDetails by currency--");
                terminalMap = terminalManager.getTerminalFromCurrency(commonValidatorVO);
                Date date3 = new Date();
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting start #########" + date3.getTime());
                terminalMap = limitRouting.getTerminalVOBasedOnAmountLimitRouting(terminalMap, commonValidatorVO);
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper getTerminalVOBasedOnAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));
                commonValidatorVO.setTerminalMapLimitRouting(terminalMap);
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
                {
                    date3 = new Date();
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting start #########" + date3.getTime());
                    terminalVO = limitRouting.getTerminalVOBasedOnCardAndCardAmountLimitRouting(terminalMap, commonValidatorVO);
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting end #########" + new Date().getTime());
                    transactionLogger.error("SingCallPayment getTerminalVOBasedOnCardAndCardAmountLimitRouting diff #########" + (new Date().getTime() - date3.getTime()));

                    if (terminalVO == null)
                    {
                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                        error = "Terminal ID provided by you is not valid.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, commonValidatorVO.getErrorName(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("SingleCallCheckout.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }else
                        terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                }

                else if(terminalMap.size()>0)
                {
                    for (Map.Entry entry : terminalMap.entrySet())
                    {
                        String key = (String) entry.getKey();
                        terminalVO = terminalMap.get(key);
                        break;
                    }
                }

                transactionLogger.error("TransactionHelper LimitRouting end #########" + new Date().getTime());
                transactionLogger.error("TransactionHelper LimitRouting diff #########" + (new Date().getTime() - date4.getTime()));
            }else
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency())) // added by Sneha for REST API feature
                {
                    log.debug("AutoSelectTerminal is N and Filling terminalVO basis on currency");
                    if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        //Giving first priority to specific currency based terminal
                        terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                        if (terminalVO != null)
                            terminalVO.setCurrency(genericTransDetailsVO.getCurrency());

                        //If specific currency based terminal not found
                        if (terminalVO == null)
                        {
                            terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), "ALL", commonValidatorVO.getAccountId());
                            if (terminalVO != null)
                                terminalVO.setCurrency(genericTransDetailsVO.getCurrency());
                        }
                    }
                    else
                    {
                        terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminalAndCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), commonValidatorVO.getAccountId());
                    }
                }
                else
                {
                    terminalVO = terminalManager.getMemberTerminalfromTerminal(commonValidatorVO.getTerminalId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getAccountId());
                }
                if (terminalVO == null)
                {
                    // terminal not valid
                    error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                    if(functions.isValueNull(commonValidatorVO.getAccountId()))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL_ACCOUNT);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL_ACCOUNT.toString(), ErrorType.SYSCHECK.toString());
                    }
                    else
                    {
                        errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALID_TERMINAL.toString(), ErrorType.SYSCHECK.toString());
                    }
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                else
                {
                    terminalVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                }
            }
            if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
            {
                // terminal not not active
                error           = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_TERMINAL_ACTIVE_CHECK.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            commonValidatorVO.setTerminalId(String.valueOf(terminalVO.getTerminalId()));
            merchantDetailsVO.setAccountId(String.valueOf(terminalVO.getAccountId()));

            limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);

            /*if (merchantDetailsVO.getCheckLimit().equals("1"))
            {
                limitChecker.checkAmountLimitForTerminalNew(genericTransDetailsVO.getAmount(), commonValidatorVO.getTerminalId() + "", merchantDetailsVO.getMemberId(), commonValidatorVO);
            }*/
            commonValidatorVO.setTerminalVO(terminalVO);
            commonValidatorVO.setPaymentMode(GatewayAccountService.getPaymentMode(terminalVO.getPaymodeId()));
            commonValidatorVO.setPaymentBrand(GatewayAccountService.getPaymentBrand(terminalVO.getCardTypeId()));
        }
        String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
        String fromid   = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getMerchantId();
        String currency = "";
        if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
        {
            currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        }
        else
        {
            currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();
        }

        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);// genericTransDetailsVO.setFromtype(fromtype);
        commonValidatorVO.getTransDetailsVO().setFromid(fromid);
        commonValidatorVO.getTransDetailsVO().setCurrency(currency);
        //Card Limit Check per partner and Bank
        transactionLogger.debug("Card Limit Flag from Partner---" + merchantDetailsVO.getBankCardLimit());
        transactionLogger.debug("Partner ID---" + merchantDetailsVO.getPartnerId());
        transactionLogger.debug("Gateway Fromtype---" + commonValidatorVO.getTransDetailsVO().getFromtype());
        transactionLogger.debug("PGTYPE ID---" + commonValidatorVO.getTerminalVO().getGateway_id());
        transactionLogger.debug("Currency---" + commonValidatorVO.getTransDetailsVO().getCurrency());
        if ("Y".equalsIgnoreCase(merchantDetailsVO.getBankCardLimit()))
        {
            limitChecker.checkCardLimitPerBank(cardNo.substring(0, 6), cardNo.substring(cardNo.length() - 4), commonValidatorVO);
        }
        //Card Velocity-Number of transactions per card on member
        if ("1".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCardCheckLimit()))
        {
            Date date3 = new Date();
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 start #########" + date3.getTime());
            limitChecker.checkCardLimitNew2(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkCardLimitNew2 diff #########" + (new Date().getTime() - date3.getTime()));
        }

        //Card amount check on member
        if ("1".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCardTransLimit()))
        {
            Date date3 = new Date();
            transactionLogger.debug("TransactionHelper checkCardTransLimit start #########" + date3.getTime());
            limitChecker.checkCardTransLimit(commonValidatorVO);
            transactionLogger.debug("TransactionHelper checkCardTransLimit end #########" + new Date().getTime());
            transactionLogger.debug("TransactionHelper checkCardTransLimit diff #########" + (new Date().getTime() - date3.getTime()));
        }

        if ("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getLimitRouting()) && "N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getCard_velocity_check()))
        {
            //Checking Gateway level Amount Limit
            transactionLogger.debug("commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel()------>" + commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel());
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAmountLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkAmountLimitAccountLevel start #########" + date3.getTime());
                limitChecker.checkAmountLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkAmountLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkAmountLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }

            //Checking Terminal level Amount Limit
            transactionLogger.debug("commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel()------>" + commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel());
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getAmountLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkAmountLimitTerminalLevel start #########" + date3.getTime());
                limitChecker.checkAmountLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkAmountLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkAmountLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
            //Card Velocity-Number of transactions per card on gateway.
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel start #########" + date3.getTime());
                limitChecker.checkCardLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }

            //Card Velocity-Number of transactions per card on terminal.
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel start #########" + date3.getTime());
                limitChecker.checkCardLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }

            //Card amount check on Gateway
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardAmountLimitCheckAccountLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel start #########" + date3.getTime());
                limitChecker.checkCardAmountLimitAccountLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardAmountLimitAccountLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
            //Card amount check on terminal
            if (!"N".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getCardAmountLimitCheckTerminalLevel()))
            {
                Date date3 = new Date();
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel start #########" + date3.getTime());
                limitChecker.checkCardAmountLimitTerminalLevel(commonValidatorVO);
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel end #########" + new Date().getTime());
                transactionLogger.debug("TransactionHelper checkCardAmountLimitTerminalLevel diff #########" + (new Date().getTime() - date3.getTime()));
            }
        }

        //Limit check by Customer Email
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerEmailAmountLimitCheck()))
            {
                limitChecker.checkCustomerEmailDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customer Phone
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone())&& !"UPI".equalsIgnoreCase(commonValidatorVO.getPaymentMode())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneLimitCheck()))
            {
                limitChecker.checkPhoneDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerPhoneAmountLimitCheck()))
            {
                limitChecker.checkPhoneDailyAmountLimit(commonValidatorVO);
            }
        }
        //Limit check by Customer UPI
        if("UPI".equalsIgnoreCase(commonValidatorVO.getPaymentMode()))
        {
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getVpaAddressLimitCheck()))
            {
                limitChecker.checkVPAAddressDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getVpaAddressAmountLimitCheck()))
            {
                limitChecker.checkVPAAddressDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customer IP
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerIpAmountLimitCheck()))
            {
                limitChecker.checkCustomerIpDailyAmountLimit(commonValidatorVO);
            }
        }

        //Limit check by Customre Name
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()) || functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname())){
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyCount(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getCustomerNameAmountLimitCheck()))
            {
                limitChecker.checkCustomerNameDailyAmountLimit(commonValidatorVO);
            }
        }



        if (currency.equals("JPY") && !paymentChecker.isAmountValidForJPY(currency, genericTransDetailsVO.getAmount()))
        {
            error               = "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
            errorCodeListVO     = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_JPY_CURRENCY_CHECK.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        String uniqueorder = null;

        uniqueorder = checkOrderUniquenessStatus(merchantDetailsVO.getMemberId(), genericTransDetailsVO.getOrderId(),merchantDetailsVO.getIsUniqueOrderIdRequired());

        Double amt = new Double(genericTransDetailsVO.getAmount());
        if (!uniqueorder.equals(""))
        {
            error           = commonValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id " + uniqueorder;
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if (commonValidatorVO.getRecurringBillingVO() != null && !commonValidatorVO.getRecurringBillingVO().equals("") && recurringBillingVO.getReqField1() != null && !recurringBillingVO.getReqField1().equals("") && "N".equalsIgnoreCase(isRecurringAllowed(accountId)))
        {
            error           = "The functionality is not allowed for account. Please contact support for help:::";
            errorCodeListVO = getErrorVO(ErrorName.SYS_RECURRINGALLOW);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_RECURRINGALLOW.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        //Added Restricted Amount Check in CommonSystemCheckStep1
        if ("Y".equals(commonValidatorVO.getMerchantDetailsVO().getIsRestrictedTicket()))
        {
            performCommonSystemRestrictedTicketCheck(commonValidatorVO);
        }
        // VIP Card Whitelisted
        boolean isVIPCard = true;
        transactionLogger.debug("getIsCardWhitelisted Flag::::" + terminalVO.getIsCardWhitelisted());
        if ("V".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
        {
            boolean isCardWhitelisted = paymentChecker.isWhitelistedCardnumberforTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            if (isCardWhitelisted)
                isVIPCard = false;

            transactionLogger.debug("isCardWhitelisted::::::::::::::" + isCardWhitelisted);
        }
        // VIP Email Whitelisted
        boolean isVIPEmail = true;
        if ("V".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
        {
            List<CardDetailsVO> listOfCards = new ArrayList<CardDetailsVO>();
            boolean isEmailWhitelisted = paymentChecker.isWhitelistedEmailforTransaction(merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId(), commonValidatorVO.getAddressDetailsVO().getEmail(), listOfCards,commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
            transactionLogger.debug("isEmailWhitelisted::::" + isEmailWhitelisted);
            if (isEmailWhitelisted)
                isVIPEmail = false;
        }
        if (isVIPCard && isVIPEmail)
        {
            //Block country check by IP
            if (merchantDetailsVO.getBlacklistCountryIp().equalsIgnoreCase("Y") && functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
            {
                String customerIP       = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                String ipCountryCode    = functions.getIPCountryShort(customerIP);
                transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP PAXLife---" + customerIP + "Country---" + ipCountryCode);
                //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
                {
                    if (!isCountryBlocked(ipCountryCode, "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                    {
                        error           = "Your Country is Blocked:::Please contact support for further assistance";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }

            //Block country check by BIN
            if (merchantDetailsVO.getBlacklistCountryBin().equalsIgnoreCase("Y"))
            {
                transactionLogger.error("Inside getBlacklistCountryBin Check BIN Country PAXLife---" + commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
                if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                {
                    if (!isCountryBlocked(commonValidatorVO.getCardDetailsVO().getCountry_code_A2(), "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                    {
                        error           = "Your Country is Blocked:::Please contact support for further assistance";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }

            // country check by BIN and IP Mismatch
            if (merchantDetailsVO.getBlacklistCountryBinIp().equalsIgnoreCase("Y"))
            {
                String customerIP       = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                String ipCountryCode    = functions.getIPCountryShort(customerIP);
                String binCountryCode   = commonValidatorVO.getCardDetailsVO().getCountry_code_A2();

                transactionLogger.error("Inside getBlacklistCountryBinIp Check Customer IP PAXLife---" + customerIP + "---IPCountry---" + ipCountryCode + "---BIN CountryCode---" + binCountryCode);
                //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode) && functions.isValueNull(binCountryCode))
                {
                    if (!ipCountryCode.equals(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                    {
                        error = "Bin IP Country Mismatch";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_BINIPCOUNTRYMISMATCH);
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                }
            }
        }
        if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            if (commonValidatorVO.getAddressDetailsVO() != null)
            {
                if (!isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                {
                    error = "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (!isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                {
                    error = "Your IpAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDIP.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDEMAIL.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getPhone()) && !isPhoneBlocked(commonValidatorVO.getAddressDetailsVO().getPhone()))
                {
                    error = "Your Phone No. is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDPHONE);
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDPHONE.toString(), ErrorType.SYSCHECK.toString());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (!isNameBlocked(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname()))
                {
                    //error = "Customer Name Has Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDNAME);
                    error = errorCodeListVO.getListOfError().get(0).getErrorCode() + "_" + errorCodeListVO.getListOfError().get(0).getErrorDescription();
                    /*Transaction request rejected log entry with reason:Partner-CUSTOMER_BLACKLISTED_NAME*/
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), commonValidatorVO.getAddressDetailsVO().getFirstname(), commonValidatorVO.getAddressDetailsVO().getLastname(), commonValidatorVO.getAddressDetailsVO().getEmail(), commonValidatorVO.getCardDetailsVO().getCardNum(), commonValidatorVO.getCardDetailsVO().getExpMonth(), commonValidatorVO.getCardDetailsVO().getExpYear(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getOrderDesc(), commonValidatorVO.getTerminalId(), error, TransReqRejectCheck.SYS_BLOCKEDNAME.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performSystemCheckForNetBanking()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }

            /*if(!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(),merchantDetailsVO.getMemberId(),merchantDetailsVO.getAccountId()))
            {
                error = "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }*/
        }
        if ("Y".equals(merchantDetailsVO.getIsEmailLimitEnabled()))
        {
            limitChecker.checkDailyTrxnLimitPerEmail(merchantDetailsVO.getAccountId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getAddressDetailsVO().getEmail(),commonValidatorVO);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        return commonValidatorVO;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public CommonValidatorVO checksumValidationExchanger(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        TerminalVO terminalVO = new TerminalVO();
        TerminalManager terminalManager = new TerminalManager();
        Transaction transaction = new Transaction();

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        if (!Checksum.verifyExchanger(commonValidatorVO.getCustomerId(), commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), "", "", "", "", "", "", "", commonValidatorVO.getAddressDetailsVO().getEmail(), "", "", "", commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), "", "", error, TransReqRejectCheck.SYS_TERMINAL_ACTIVE_CHECK.toString(), "");
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        else
        {
            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");

            commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
            commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
            merchantDetailsVO.setAccountId(String.valueOf(terminalVO.getAccountId()));
            commonValidatorVO.setTerminalVO(terminalVO);
            commonValidatorVO.setPaymentBrand(transaction.getPaymentBrandForRest(terminalVO.getCardTypeId()));
            commonValidatorVO.setPaymentMode(transaction.getPaymentModeForRest(terminalVO.getPaymodeId()));

            String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
            String fromid = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getMerchantId();
            String currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();

            commonValidatorVO.getTransDetailsVO().setTotype(commonValidatorVO.getPartnerName());
            commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);// genericTransDetailsVO.setFromtype(fromtype);
            commonValidatorVO.getTransDetailsVO().setFromid(fromid);
            commonValidatorVO.getTransDetailsVO().setCurrency(currency);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performSystemCheckForPayout(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException, NoSuchAlgorithmException
    {
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        ErrorCodeUtils errorCodeUtils =new ErrorCodeUtils();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String uniqueorder = null;
        TerminalVO terminalVO = new TerminalVO();
        Functions functions = new Functions();
        TerminalManager terminalManager = new TerminalManager();
        Transaction transaction = new Transaction();
        LimitChecker limitChecker = new LimitChecker();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        PaymentManager paymentManager = new PaymentManager();
        if (!Checksum.verifyMD5ChecksumForPayout(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getTransDetailsVO().getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), "", "", "", "", "", "", "", commonValidatorVO.getAddressDetailsVO().getEmail(), "", "", "", commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), "", "", error, TransReqRejectCheck.SYS_TERMINAL_ACTIVE_CHECK.toString(), "");
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if (functions.isValueNull(commonValidatorVO.getTrackingid()))
        {
            commonValidatorVO.setTerminalId(paymentManager.getTerminalFromTrackingid(commonValidatorVO.getTrackingid()));
        }


        String accountId = "";

        if (functions.isValueNull(commonValidatorVO.getTerminalId()))
        {
            //Hashtable terminalDetails = transaction.getTerminalDetails(terminalId);
            terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminal(merchantDetailsVO.getMemberId(), commonValidatorVO.getTerminalId(),"");
        }
        else
        {
            terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(merchantDetailsVO.getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
        }


        if (terminalVO == null)
        {
            // terminal not valid
            error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_CONFIGURATION*/
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), "", "", "", "", "", "", "", commonValidatorVO.getAddressDetailsVO().getEmail(), "", "", "", commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), "", "", error, TransReqRejectCheck.SYS_INVALID_TERMINAL.toString(), "");
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if(merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            transactionLogger.error("BlacklistTransaction BankAccountNo-----" + commonValidatorVO.getReserveField2VO().getBankAccountNumber());

            if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getBankAccountNumber())&&!isBankAccountnoBlocked(commonValidatorVO.getReserveField2VO().getBankAccountNumber()))
            {
            transactionLogger.error("BlacklistTransaction BankAccountNo-----" + commonValidatorVO.getReserveField2VO().getBankAccountNumber());
            error = "Your BankAccountNo is Blocked:::Please contact support for further assistance";
            errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDBANKACCOUNTNO);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDBANKACCOUNTNO.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);

            }
        }

        if ("N".equalsIgnoreCase(terminalVO.getPayoutActivation())&&"N".equalsIgnoreCase(merchantDetailsVO.getPayoutRouting()))
        {  
            // terminal not not active
            error = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_ACTIVATION*/
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), "", "", "", "", "", "", "", commonValidatorVO.getAddressDetailsVO().getEmail(), "", "", "", commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), "", "", error, TransReqRejectCheck.SYS_TERMINAL_ACTIVE_CHECK.toString(), "");
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if("Y".equalsIgnoreCase(merchantDetailsVO.getPayoutRouting()))
        {
            Date date=new Date();
           // transactionLogger.error("before payoutAmountLimitRouting execution time---->"+date);
            LinkedList<TerminalVO> terminalLinkedlist = terminalManager.getPayoutActiveTerminal(merchantDetailsVO.getMemberId());
            terminalVO= limitChecker.payoutAmountLimitRouting(commonValidatorVO, terminalLinkedlist);
            transactionLogger.error("after payoutAmountLimitRouting execution time---->"+(new Date().getTime()-date.getTime()));
            if (terminalVO==null)
            {
                // terminal not not active
                error = "Insufficent Payout Balance in Account.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED);
                /*Transaction request rejected log entry with reason:MERCHANT_TERMINAL_ACTIVATION*/
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_PAYOUT_AMOUNT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }


        limitChecker.checkTransactionAmountNew(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount(), commonValidatorVO);
        transactionLogger.error("commonValidatorVO.getReserveField2VO().getBankAccountNumber()--->"+commonValidatorVO.getReserveField2VO().getBankAccountNumber());
        if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getBankAccountNumber()))
        {
transactionLogger.error("inside if commonValidatorVO.getReserveField2VO().getBankAccountNumber()--->"+commonValidatorVO.getReserveField2VO().getBankAccountNumber());
transactionLogger.error("merchantDetailsVO.getBankAccountLimitCheck()--->"+merchantDetailsVO.getBankAccountLimitCheck());
transactionLogger.error("merchantDetailsVO.getBankAccountAmountLimitCheck()--->"+merchantDetailsVO.getBankAccountAmountLimitCheck());
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getBankAccountLimitCheck()))
            {
                limitChecker.checkPayoutBankAccountNoDailyCountLimit(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(merchantDetailsVO.getBankAccountAmountLimitCheck()))
            {
                limitChecker.checkPayoutBankAccountNoDailyAmountLimit(commonValidatorVO);
            }
        }

        commonValidatorVO.setCardType(String.valueOf(terminalVO.getCardTypeId()));
        commonValidatorVO.setPaymentType(String.valueOf(terminalVO.getPaymodeId()));
        commonValidatorVO.setTerminalId(terminalVO.getTerminalId());
        merchantDetailsVO.setAccountId(String.valueOf(terminalVO.getAccountId()));
        commonValidatorVO.setTerminalVO(terminalVO);
        commonValidatorVO.setPaymentBrand(transaction.getPaymentBrandForRest(terminalVO.getCardTypeId()));
        commonValidatorVO.setPaymentMode(transaction.getPaymentModeForRest(terminalVO.getPaymodeId()));


        String fromtype = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getGateway();
        String fromid = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getMerchantId();
        String currency = GatewayAccountService.getGatewayAccount(merchantDetailsVO.getAccountId()).getCurrency();

        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);// genericTransDetailsVO.setFromtype(fromtype);
        commonValidatorVO.getTransDetailsVO().setFromid(fromid);
        commonValidatorVO.getTransDetailsVO().setCurrency(currency);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);

        uniqueorder = checkorderuniqueness(merchantDetailsVO.getMemberId(), fromtype, genericTransDetailsVO.getOrderId());

        if (!uniqueorder.equals(""))
        {
            error = commonValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id " + uniqueorder;
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), "", "", "", "", "", "", "", commonValidatorVO.getAddressDetailsVO().getEmail(), "", "", "", commonValidatorVO.getTransDetailsVO().getAmount(), commonValidatorVO.getTransDetailsVO().getOrderId(), "", "", error, TransReqRejectCheck.SYS_TERMINAL_ACTIVE_CHECK.toString(), "");
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        transactionLogger.error("payout limit check flag---->"+commonValidatorVO.getMerchantDetailsVO().getPayout_amount_limit_check());
        AbstractInputValidator paymentProcess = InputValidatorFactory.getInputValidatorInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));


        if (!functions.isEmptyOrNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            String errorname = errorCodeUtils.getErrorNames(commonValidatorVO.getErrorCodeListVO().getListOfError());
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, errorname, ErrorType.VALIDATION.toString());
            return commonValidatorVO ;
        }
        if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getPayout_amount_limit_check())){
            limitChecker.checkPayoutAmountLimitMemberLevel(commonValidatorVO);

        }

        return commonValidatorVO;
    }

    //for Rest Transactions API
    public CommonValidatorVO performRestStandAlonetokenSystemChecksStep1(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        //getting value from VO
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        PartnerDetailsVO partnerDetailsVO = commonValidatorVO.getPartnerDetailsVO();
        PaymentChecker paymentChecker = new PaymentChecker();
        TerminalVO terminalVO = null;
        TerminalManager terminalManager = new TerminalManager();
        ErrorCodeListVO errorCodeListVO = null;
        String error = "";
        Functions functions = new Functions();

        if ("N".equals(partnerDetailsVO.getIsTokenizationAllowed()))
        {
            error = "Tokenization is not allowed for Partner.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TOKEN_ALLOWED);
            PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }

        if ("Y".equals(partnerDetailsVO.getIsMerchantRequiredForCardRegistration()))
        {
            if ("N".equalsIgnoreCase(merchantDetailsVO.getIsTokenizationAllowed()))
            {
                error = "Tokenization is not allowed for Merchant.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TOKEN_ALLOWED);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            log.debug("auto select terminal----" + merchantDetailsVO.getAutoSelectTerminal());
            if(functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                if ("Y".equals(merchantDetailsVO.getAutoSelectTerminal()))
                {
                    if (merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                    {
                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), "ALL");
                        if (terminalVO != null)
                            terminalVO.setCurrency(genericTransDetailsVO.getCurrency());
                    }
                    else
                    {
                        terminalVO = terminalManager.getCardIdAndPaymodeIdFromPaymentBrand(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
                    }
                    if (terminalVO == null)
                    {
                        // terminal not valid
                        error = "Account ID-payment brand or payment mode or currency requested by you is not valid for your account. Please check your technical specification.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CURRENCY_BRAND_MODE);
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }

                    if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
                    {
                        error = "Tokenization functionality is not allowed for your terminal. please contact support.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }

                    log.debug("isTokenizationActive-----" + terminalVO.getIsTokenizationActive());
                /*if ("N".equalsIgnoreCase(terminalVO.getIsTokenizationActive()))
                {
                    error = "Tokenization functionality is not allowed for your terminal. please contact support.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TOKEN_ALLOWED);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
                }
                else
                {
                    terminalVO = terminalManager.getMemberTerminalfromMemberAndTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(),"");
                    if (terminalVO == null)
                    {
                        // terminal not valid
                        error = "Terminal Id provided by you is not valid for your account.Please check your Technical specification.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL);
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }
                    if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
                    {
                        // terminal not not active
                        error = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                        errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                        PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                    }

                    log.debug("isTokenizationActive-----" + terminalVO.getIsTokenizationActive());
                /*if ("N".equalsIgnoreCase(terminalVO.getIsTokenizationActive()))
                {
                    error = "Tokenization functionality is not allowed for your terminal. please contact support.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_TOKEN_ALLOWED);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
                }
                // VIP Card Whitelisted
                boolean isVIPCard = true;
                transactionLogger.debug("getIsCardWhitelisted Flag::::" + terminalVO.getIsCardWhitelisted());
                if ("V".equalsIgnoreCase(terminalVO.getIsCardWhitelisted()))
                {
                    boolean isCardWhitelisted = paymentChecker.isWhitelistedCardnumberforTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getMerchantDetailsVO().getAccountId(), commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
                    if (isCardWhitelisted)
                        isVIPCard = false;

                    transactionLogger.debug("isCardWhitelisted::::::::::::::" + isCardWhitelisted);
                }
                // VIP Email Whitelisted
                boolean isVIPEmail = true;
                if ("V".equalsIgnoreCase(terminalVO.getIsEmailWhitelisted()))
                {
                    List<CardDetailsVO> listOfCards = new ArrayList<CardDetailsVO>();
                    boolean isEmailWhitelisted = paymentChecker.isWhitelistedEmailforTransaction(merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId(), commonValidatorVO.getAddressDetailsVO().getEmail(), listOfCards,commonValidatorVO.getMerchantDetailsVO().getCardWhitelistLevel());
                    transactionLogger.debug("isEmailWhitelisted::::" + isEmailWhitelisted);
                    if (isEmailWhitelisted)
                        isVIPEmail = false;
                }
                if (isVIPCard && isVIPEmail)
                {
                    //Block country check by IP
                    if (merchantDetailsVO.getBlacklistCountryIp().equalsIgnoreCase("Y") && functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                    {
                        String customerIP = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                        String ipCountryCode = functions.getIPCountryShort(customerIP);
                        transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP REST---" + customerIP + "Country---" + ipCountryCode);
                        //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                        if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
                        {
                            if (!isCountryBlocked(ipCountryCode, "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                            {
                                error = "Your Country is Blocked:::Please contact support for further assistance";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }
                    }

                    //Block country check by BIN
                    if (merchantDetailsVO.getBlacklistCountryBin().equalsIgnoreCase("Y"))
                    {
                        transactionLogger.error("Inside getBlacklistCountryBin Check BIN Country REST---" + commonValidatorVO.getCardDetailsVO().getCountry_code_A2());
                        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                        {
                            if (!isCountryBlocked(commonValidatorVO.getCardDetailsVO().getCountry_code_A2(), "", merchantDetailsVO.getMemberId(), merchantDetailsVO.getAccountId()))
                            {
                                error = "Your Country is Blocked:::Please contact support for further assistance";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }
                    }

                    //country check by BIN and IP Mismatch
                    if (merchantDetailsVO.getBlacklistCountryBinIp().equalsIgnoreCase("Y"))
                    {
                        String customerIP = commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
                        String ipCountryCode = functions.getIPCountryShort(customerIP);
                        String binCountryCode = commonValidatorVO.getCardDetailsVO().getCountry_code_A2();

                        transactionLogger.error("Inside getBlacklistCountryBinIp Check Customer IP-REST---" + customerIP + "---IPCountry---" + ipCountryCode + "---BIN CountryCode---" + binCountryCode);
                        //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                        if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode) && functions.isValueNull(binCountryCode))
                        {
                            if (!ipCountryCode.equals(commonValidatorVO.getCardDetailsVO().getCountry_code_A2()))
                            {
                                error = "Bin IP Country Mismatch";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BINIPCOUNTRYMISMATCH);
                                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }
                    }
                }
            }
            else
            {
                terminalVO=new TerminalVO();
            }

            if (merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
            {
                if (!isIpAddressBlockedGlobal(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()))
                {
                    error = "Your IpAddress is Blocked Globally:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (!isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), commonValidatorVO.getMerchantDetailsVO().getMemberId()))
                {
                    error = "Your IpAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getEmail()) && !isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
                {
                    error = "Your EmailAddress is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                /*if (!isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc(),merchantDetailsVO.getMemberId(),merchantDetailsVO.getAccountId()))
                {
                    error = "Your Country is Blocked:::Please contact support for further assistance";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }*/
            }
        }
        else //Partner level token creation
        {
            if (functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                terminalVO = terminalManager.getPartnerTerminalfromPartnerAndTerminal(partnerDetailsVO.getPartnerId(),commonValidatorVO.getTerminalId());
            }
            else
            {
                terminalVO = terminalManager.getPartnersTerminalDetails(partnerDetailsVO.getPartnerId(), commonValidatorVO.getPaymentType(), commonValidatorVO.getCardType(), commonValidatorVO.getTransDetailsVO().getCurrency());
            }

            if (terminalVO == null)
            {
                error = "Selected terminal is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_TERMINAL_MODE_BRAND);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if ("N".equalsIgnoreCase(terminalVO.getIsActive()))
            {
                error = "Terminal Id provided by you is not active for your account. Please check your Technical specification.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TERMINAL_ACTIVE_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }

        commonValidatorVO.setTerminalVO(terminalVO);
        merchantDetailsVO.setAccountId(commonValidatorVO.getTerminalVO().getAccountId());
        if(functions.isValueNull(commonValidatorVO.getTerminalVO().getCardTypeId()))
             commonValidatorVO.setCardType(commonValidatorVO.getTerminalVO().getCardTypeId());
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
        return commonValidatorVO;
    }

    public ErrorCodeListVO getReferencedCaptureCancelRefundTransDetails(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        Hashtable transHash             = null;
        TransactionUtil transactionUtil = new TransactionUtil();
        ErrorCodeListVO errorCodeListVO = null;
        PreparedStatement ps            = null;
        ResultSet rs                    = null;
        Connection con                  = null;
        Functions functions             = new Functions();

        try
        {
            con                 = Database.getConnection();
            StringBuffer qry    = new StringBuffer("SELECT status,amount,captureamount,refundamount,currency,trackingid,FROM_UNIXTIME(dtstamp) as transactiondate FROM transaction_common WHERE trackingid=? AND toid=?");
            ps                  = con.prepareStatement(qry.toString());
            ps.setString(1, commonValidatorVO.getTrackingid());
            ps.setString(2, commonValidatorVO.getMerchantDetailsVO().getMemberId());

            rs = ps.executeQuery();
            if (rs.next())
            {
                transHash = new Hashtable();
                transHash.put("status", rs.getString("status"));
                transHash.put("amount", rs.getString("amount"));
                transHash.put("captureamount", rs.getString("captureamount"));
                transHash.put("refundamount", rs.getString("refundamount"));
                transHash.put("currency", rs.getString("currency"));
                transHash.put("trackingid", rs.getString("trackingid"));
                transHash.put("transactiondate", rs.getString("transactiondate"));
            }

            if (transHash != null)
            {
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCurrency()) && !commonValidatorVO.getTransDetailsVO().getCurrency().equals(transHash.get("currency")))
                {
                    errorCodeListVO = new ErrorCodeListVO();
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_CURRENCY, ErrorMessages.INVALID_REFERENCE_CURRENCY));
                }
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getAmount()))
                {
                    float requestAmt    = Float.parseFloat(commonValidatorVO.getTransDetailsVO().getAmount());
                    float dbAmt         = Float.parseFloat(String.valueOf(transHash.get("amount")));
                    float dbRefundAmt   = Float.parseFloat(String.valueOf(transHash.get("refundamount")));

                    if (!(requestAmt <= dbAmt-dbRefundAmt))
                    {
                        errorCodeListVO = new ErrorCodeListVO();
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_AMOUNT, ErrorMessages.INVALID_REFERENCE_AMOUNT));
                    }
                }
                if (("RF".equals(commonValidatorVO.getTransactionType())) && (!transHash.get("status").equals("capturesuccess")) && (!transHash.get("status").equals("settled")) && (!transHash.get("status").equals("reversed") || ((Float.parseFloat(transHash.get("captureamount").toString())<= Float.parseFloat(transHash.get("refundamount").toString())))))
                {
                    errorCodeListVO = new ErrorCodeListVO();
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_TRANSACTION_NOT_FOUND, ErrorMessages.TRANSACTION_NOT_FOUND));
                }
                else if (("CP".equals(commonValidatorVO.getTransactionType()) || "RV".equals(commonValidatorVO.getTransactionType())) && !transHash.get("status").equals("authsuccessful"))
                {
                    errorCodeListVO = new ErrorCodeListVO();
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_TRANSACTION_NOT_FOUND, ErrorMessages.TRANSACTION_NOT_FOUND));
                }
            }
            else
            {
                errorCodeListVO = new ErrorCodeListVO();
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, ErrorMessages.INVALID_REFERENCE_TRACKINGID));
            }
        }
        catch (SQLException e)
        {
            log.error("SQLException---",e);
        }
        catch (SystemError e)
        {
            log.error("Systemerror---",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return errorCodeListVO;
    }

    public CommonValidatorVO merchantActivationChecks(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        Functions functions = new Functions();
        PaymentChecker paymentChecker = new PaymentChecker();
        Merchants merchants=new Merchants();
        String error = "";
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        MerchantModuleManager merchantModuleManager=new MerchantModuleManager();
        String role=commonValidatorVO.getMerchantDetailsVO().getRole();
        //IP Whitelist check
        if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIpWhitelistInvoice()))
        {
            if ("Y".equals((commonValidatorVO.getMerchantDetailsVO().getIpWhitelistInvoice())))
            {
                transactionLogger.debug("MerchantId and ip address--------" + commonValidatorVO.getMerchantDetailsVO().getMemberId() + "---" + commonValidatorVO.getRequestedIP());
                if (!paymentChecker.isIpWhitelistedForTransaction(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getRequestedIP()))
                {
                    error = "Merchant's IP is not white listed with us. Kindly Contact the pz Support Desk.";
                    errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "merchantActivationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
            }
        }

        transactionLogger.error("Role---"+role);
        if ("submerchant".equalsIgnoreCase(role)){
            String userId=merchants.getUserid(commonValidatorVO.getMerchantDetailsVO().getLogin());
            transactionLogger.error("getUserId---" +userId);
            if (!merchantModuleManager.isMappingAvailable(userId, MerchantModuleEnum.INVOICE.name()))
            {
                error="The functionality is not supported for submerchant";
                errorCodeListVO=getErrorVO(ErrorName.SYS_FUNCTIONALITY_NOT_SUPPORTED);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "merchantActivationChecks()", null, "Common", error,PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        //Activation check
        if (!"Y".equals(commonValidatorVO.getMerchantDetailsVO().getActivation()))
        {
            error = "Error- The Merchant Account is not set to LIVE mode.<BR><BR> This could happen if there is any pending formality from the Merchant Side. Please contact support so that they can activate your account.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
            PZExceptionHandler.raiseConstraintViolationException("InvoiceHelper.class", "merchantActivationChecks()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (errorCodeListVO != null)
        {
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            return commonValidatorVO;
        }

        return commonValidatorVO;
    }
    public String checkOrderUniquenessStatusForMarketPlace(String toid, String description)
    {
        transactionLogger.debug("checkorderuniqueness---");
        String str = "";
        Connection con = null;
        try
        {
            con = Database.getConnection();
            String transaction_table = "transaction_common";
            transactionLogger.debug("InputValidatorUtils.OrderUniqueness ::: DB Call" + transaction_table);
            String query2 = "select trackingid from " + transaction_table + " where toid = ? and description = ? and status not in ('begun','failed','authfailed')order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            pstmt1.setString(2, description);
            log.debug("order unique query---" + pstmt1);
            transactionLogger.debug("order unique query---" + pstmt1);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Your Transaction is already being processed. Kindly try to place transaction with unique orderId.";
            }
        }
        catch (Exception e)
        {
            log.error("Exception occur", e);
            transactionLogger.error("Exception occur", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return str;
    }

    public CommonValidatorVO performRESTAPISystemCheckForInitiateAuthentication(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        if (commonValidatorVO.getTransactionType() != null)
        {
            commonValidatorVO = performRestTransactionChecksStep1(commonValidatorVO);

        }
        return commonValidatorVO;
    }
    public static String getAmount(String amount)
    {
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        String amt = d.format(dObj2);
        return amt;
    }
}