package com.fraud.manager;

import com.directi.pg.FailedTransactionLogEntry;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.fraud.FraudTransaction;
import com.fraud.dao.FraudDAO;
import com.fraud.vo.PZFraudRequestVO;
import com.fraud.vo.PZFraudResponseVO;
import com.manager.helper.TransactionHelper;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import java.util.List;

/**
 * Created by Admin on 12/5/2018.
 */
public class InternalFraudProcessor
{
    private static Logger log = new Logger(InternalFraudProcessor.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InternalFraudProcessor.class.getName());

    Functions functions = new Functions();
    FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
    TransactionHelper transactionHelper = new TransactionHelper();

    public PZFraudResponseVO internalFraudCheck(PZFraudRequestVO commonValidatorVO,List<RuleMasterVO> fraudVOList) throws PZDBViolationException
    {
        transactionLogger.error("Inside InternalFraudProcessor internalFraudCheck---");
        ErrorCodeListVO errorCodeListVO = null;
        PZFraudResponseVO pzFraudResponseVO = new PZFraudResponseVO();
        FraudTransaction fsTransaction=new FraudTransaction();
        FraudDAO fraudDAO = new FraudDAO();
        transactionLogger.error("Internal Fraud rule list---"+fraudVOList.size());
        boolean isFraud = false;
        int fraud_trans_id=0;

        String memberId = commonValidatorVO.getMemberid();
        String accountId = commonValidatorVO.getAccountid();
        String trackingId = commonValidatorVO.getTrackingid();
        String memberTransId = commonValidatorVO.getDescription();

        fraud_trans_id = fsTransaction.fraudTransactionEntry(commonValidatorVO, "0");

        transactionLogger.error("Pending entry done for fraudTransactionEntry fraud_trans_id---"+fraud_trans_id);
        for(RuleMasterVO internalFraudVO : fraudVOList)
        {
            transactionLogger.error(internalFraudVO.getRuleName()+"---"+internalFraudVO.getDefaultStatus());
            if(internalFraudVO.getDefaultStatus().equalsIgnoreCase("enable"))
            {
                switch (internalFraudVO.getRuleName())
                {
                    case "Block_Country_By_IPaddress":
                        transactionLogger.error("Internal Fraud Rule BLOCK_COUNTRY_BY_IP");
                        String customerIP = commonValidatorVO.getIpaddrs();
                        String ipCountryCode = functions.getIPCountryShort(customerIP);
                        //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                        //String ipCountryCode="IN";
                        transactionLogger.error("Inside getBlacklistCountryIp Check Customer IP---" + customerIP + "Country---" + ipCountryCode);
                        if (functions.isValueNull(customerIP) && functions.isValueNull(ipCountryCode))
                        {
                            if (!transactionHelper.isCountryBlocked(ipCountryCode, "", memberId, accountId))
                            {
                                isFraud = true;
                                String error = "Your Country is Blocked:::Please contact support for further assistance";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                                //PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Block_Country_By_BIN":
                        transactionLogger.error("Internal Fraud Rule BLOCK_COUNTRY_BY_BIN");
                        transactionLogger.error("Inside getBlacklistCountryBin Check BIN Country---" + commonValidatorVO.getBinCountry());
                        if (functions.isValueNull(commonValidatorVO.getBinCountry()))
                        {
                            if (!transactionHelper.isCountryBlocked(commonValidatorVO.getBinCountry(), "", memberId, accountId))
                            {
                                String error = "Your Country is Blocked:::Please contact support for further assistance";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                                //PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                                isFraud = true;
                            }
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "BIN_IP_Country_Mismatch":
                        transactionLogger.error("Internal Fraud Rule BIN_IP_COUNTRY_MISMATCH");
                        String customerIP1 = commonValidatorVO.getIpaddrs();
                        String ipCountryCode1 = functions.getIPCountryShort(customerIP1);
                        String binCountryCode = commonValidatorVO.getBinCountry();

                        transactionLogger.error("Inside getBlacklistCountryBinIp Check Customer IP---" + customerIP1 + "---IPCountry---" + ipCountryCode1 + "---BIN CountryCode---" + binCountryCode);
                        //String ipCountryCode=functions.getIPCountryShort("15.219.207.0");
                        if (functions.isValueNull(customerIP1) && functions.isValueNull(ipCountryCode1) && functions.isValueNull(binCountryCode))
                        {
                            if (!ipCountryCode1.equals(binCountryCode))
                            {
                                String error = "Bin IP Country Mismatch";
                                errorCodeListVO = getErrorVO(ErrorName.SYS_BINIPCOUNTRYMISMATCH);
                                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_BLOCKEDCOUNTRY.toString(), ErrorType.SYSCHECK.toString());
                                //PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                                isFraud = true;
                            }
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Block_IPaddress":
                        transactionLogger.error("Internal Fraud Rule BLOCK_IPADDRESS"+commonValidatorVO.getIpaddrs());
                        if (!transactionHelper.isIpAddressBlocked(commonValidatorVO.getIpaddrs(),memberId))
                        {
                            String error = "Your credit Card is Blocked:::Please contact support for further assistance";
                            errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                            //PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            isFraud = true;
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Block_BIN":
                        String cardNo = commonValidatorVO.getFirstsix();
                        transactionLogger.error("Internal Fraud Rule BLOCK_BIN"+cardNo.substring(0, 6));
                        if (!transactionHelper.isBinBlocked(cardNo.substring(0, 6), accountId, memberId))
                        {
                            String error = "Your credit Card is Blocked:::Please contact support for further assistance";
                            errorCodeListVO = getErrorVO(ErrorName.SYS_CCBLOCK);
                            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_CCBLOCK.toString(), ErrorType.SYSCHECK.toString());
                            //PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep2()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            isFraud = true;
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Allowed_Email_Per_Card":
                        transactionLogger.error("Internal Fraud Rule EMAIL_PER_CARD");
                        //String cardNumber = commonValidatorVO.getCardDetailsVO().getCardNum();
                        String firstSix = commonValidatorVO.getFirstsix();
                        String lastFour = commonValidatorVO.getLastfour();

                        String emailCount = transactionHelper.allowedEmailCountfromCard(firstSix, lastFour);
                        transactionLogger.error("Email count from DB---" + emailCount + "---Allowed Limit---" + internalFraudVO.getDefaultValue());
                        if (functions.isValueNull(internalFraudVO.getDefaultValue()) && (Integer.parseInt(emailCount) > Integer.parseInt(internalFraudVO.getDefaultValue())))
                        {
                            transactionLogger.error("Not Allowed EMAIL_PER_CARD");
                            isFraud = true;
                            errorCodeListVO = getErrorVO(ErrorName.SYS_EXCEED_EMAIL_PER_CARD);
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Allowed_Card_Per_Email":
                        transactionLogger.error("Internal Fraud Rule CARD_PER_EMAIL");
                        String cardCount = transactionHelper.allowedCardCountfromEmail(commonValidatorVO.getEmailaddr());
                        transactionLogger.error("Email count from DB---" + cardCount + "---Allowed Limit---" + internalFraudVO.getDefaultValue());
                        if (functions.isValueNull(internalFraudVO.getDefaultValue()) && (Integer.parseInt(cardCount) > Integer.parseInt(internalFraudVO.getDefaultValue())))
                        {
                            transactionLogger.error("Not Allowed CARD_PER_EMAIL");
                            isFraud = true;
                            errorCodeListVO = getErrorVO(ErrorName.SYS_EXCEED_CARD_PER_EMAIL);
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO, String.valueOf(fraud_trans_id), isFraud);
                        break;

                    case "Block_Card_By_Type":
                        transactionLogger.error("Internal Fraud Rule Card Type from Card---"+commonValidatorVO.getBinCardType());
                        transactionLogger.error("Internal Fraud Rule Card Type from Rule---"+internalFraudVO.getDefaultValue());
                        if(functions.isValueNull(commonValidatorVO.getBinCardType()) && functions.isValueNull(internalFraudVO.getDefaultValue()) && commonValidatorVO.getBinCardType().equalsIgnoreCase(internalFraudVO.getDefaultValue()))
                        {
                            transactionLogger.error("Invalid Card Type/Usage is not supported by the System");
                            isFraud = true;
                            errorCodeListVO = getErrorVO(ErrorName.SYS_CARD_NOT_SUPPORTED);
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO,String.valueOf(fraud_trans_id),isFraud);
                        break;

                    case "Block_Card_By_Usage":
                        transactionLogger.error("Internal Fraud Rule Usage Type From Card---"+commonValidatorVO.getBinUsageType());
                        transactionLogger.error("Internal Fraud Rule Usage Type From Rule---"+internalFraudVO.getDefaultValue());
                        if(functions.isValueNull(commonValidatorVO.getBinUsageType()) && functions.isValueNull(internalFraudVO.getDefaultValue()) && commonValidatorVO.getBinUsageType().equalsIgnoreCase(internalFraudVO.getDefaultValue()))
                        {
                            transactionLogger.error("Invalid Card Type/Usage is not supported by the System");
                            isFraud = true;
                            errorCodeListVO = getErrorVO(ErrorName.SYS_CARD_NOT_SUPPORTED);
                        }
                        fraudDAO.insertTriggeredRuleEntry(internalFraudVO,String.valueOf(fraud_trans_id),isFraud);
                        break;

                    default:
                }
            }
            if(isFraud)
                break;
        }
        pzFraudResponseVO.setFraud(isFraud);
        pzFraudResponseVO.setErrorCodeListVO(errorCodeListVO);
        fsTransaction.fraudActionEntryforInternalFraud(String.valueOf(fraud_trans_id));
        transactionLogger.error("Success entry for fraudTransactionEntry fraud_trans_id---"+fraud_trans_id);
        return pzFraudResponseVO;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        log.debug("error in helper---------->"+errorName.toString());
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeVO.setErrorName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }
}
