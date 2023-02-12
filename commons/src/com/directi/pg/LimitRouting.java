package com.directi.pg;

import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.*;

/**
 * Created by Admin on 2/13/2019.
 */
public class LimitRouting
{
    TransactionLogger transactionLogger = new TransactionLogger(LimitRouting.class.getName());
    LimitChecker limitChecker = new LimitChecker();

    public LinkedHashMap<String,TerminalVO> getTerminalVOBasedOnAmountLimitRouting(LinkedHashMap<String,TerminalVO> terminalMap, CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("List::::" + terminalMap.size());
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        LinkedHashMap<String,TerminalVO> terminalMap1 = new LinkedHashMap<String,TerminalVO>();
        List cardTypelist =null;
        HashMap<String,List<String>> paymentCardTypeMap =new HashMap<>();
        HashSet terminalSet = new HashSet();
        String error = "";
        Functions functions=new Functions();
        ErrorCodeListVO errorCodeListVO = null;
        boolean isLimitAvailableOnAccount = true;
        boolean isLimitAvailableOnTerminal = true;
        if(functions.isValueNull(commonValidatorVO.getTerminalId()))
        {
            TerminalVO terminalVO=terminalMap.get(commonValidatorVO.getTerminalId());
            isLimitAvailableOnAccount = true;
            isLimitAvailableOnTerminal = true;
            commonValidatorVO.setTerminalVO(terminalVO);
            if (!"N".equalsIgnoreCase(terminalVO.getAmountLimitCheckAccountLevel()))
            {
                isLimitAvailableOnAccount = limitChecker.checkAmountLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO.getAmountLimitCheckTerminalLevel()))
            {
                isLimitAvailableOnTerminal = limitChecker.checkAmountLimitTerminalLevel(commonValidatorVO);
            }
            transactionLogger.error("isLimitAvailableOnAccount:::::" + isLimitAvailableOnAccount+"---"+terminalVO.getTerminalId());
            transactionLogger.error("isLimitAvailableOnTerminal:::::" + isLimitAvailableOnTerminal+"---"+terminalVO.getTerminalId());
            if (isLimitAvailableOnAccount && isLimitAvailableOnTerminal)
                terminalMap1.put(terminalVO.getTerminalId(), terminalVO);
            }
        for (Map.Entry entry:terminalMap.entrySet())
        {
            String key= (String) entry.getKey();
        //    if(!commonValidatorVO.getPaymentMode().equalsIgnoreCase("19")){
            if(key.equalsIgnoreCase(commonValidatorVO.getTerminalId()))
                continue;
           // }
            TerminalVO terminalVO=terminalMap.get(key);
            isLimitAvailableOnAccount = true;
            isLimitAvailableOnTerminal = true;
            commonValidatorVO.setTerminalVO(terminalVO);
            if (!"N".equalsIgnoreCase(terminalVO.getAmountLimitCheckAccountLevel()))
            {
                isLimitAvailableOnAccount = limitChecker.checkAmountLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO.getAmountLimitCheckTerminalLevel()))
            {
                isLimitAvailableOnTerminal = limitChecker.checkAmountLimitTerminalLevel(commonValidatorVO);
            }
            transactionLogger.debug("isLimitAvailableOnAccount:::::" + isLimitAvailableOnAccount+"---"+terminalVO.getTerminalId());
            transactionLogger.debug("isLimitAvailableOnTerminal:::::" + isLimitAvailableOnTerminal+"---"+terminalVO.getTerminalId());

            if (isLimitAvailableOnAccount && isLimitAvailableOnTerminal){

                if("19".equalsIgnoreCase(terminalVO.getPaymodeId()))
                {    cardTypelist=paymentCardTypeMap.get("19");
                    if(cardTypelist==null)
                    cardTypelist=new ArrayList<>();
                    cardTypelist.add(terminalVO.getCardTypeId());
                    paymentCardTypeMap.put("19",cardTypelist);
                    transactionLogger.debug("inside for loop paymentCardTypeMap:::::" +paymentCardTypeMap);
                    commonValidatorVO.setMapOfPaymentCardType(paymentCardTypeMap);

                  /*  terminalSet.add(terminalVO);
                    commonValidatorVO.setSetOfPaymentCardType(terminalSet);
*/
                }

                else if("17".equalsIgnoreCase(terminalVO.getPaymodeId()))
                {
                    cardTypelist=paymentCardTypeMap.get("17");
                    if(cardTypelist==null)
                      cardTypelist=new ArrayList<>();
                    cardTypelist.add(terminalVO.getCardTypeId());
                    paymentCardTypeMap.put("17",cardTypelist);
                    commonValidatorVO.setMapOfPaymentCardType(paymentCardTypeMap);
                }
                terminalMap1.put(terminalVO.getTerminalId(), terminalVO);
                transactionLogger.error("limitrouting   terminalMap1:::::" +terminalMap1);

            }
        }
        transactionLogger.error("limitrouting   paymentCardTypeMap:::::" + paymentCardTypeMap);
        if(terminalMap1.size()==0)
        {
            error = "Transaction not permitted:::Please contact your Administrator.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, commonValidatorVO.getErrorName(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }
        for (Map.Entry entry : terminalMap1.entrySet())
        {
            String terminalId= (String) entry.getKey();
            TerminalVO terminalVO=terminalMap1.get(terminalId);
            transactionLogger.error("Accountid:::" + terminalVO.getAccountId() + ",TerminalId:::::" + terminalVO.getTerminalId());
        }
        return terminalMap1;
    }
    public TerminalVO getTerminalVOBasedOnCardAndCardAmountLimitRouting(LinkedHashMap<String,TerminalVO> terminalMap, CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException, PZDBViolationException
    {
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        Functions functions=new Functions();
        TerminalVO terminalVO=null;
        String error = "";
        ErrorCodeListVO errorCodeListVO = null;
        boolean isCardLimitAvailableOnAccount = true;
        boolean isCardLimitAvailableOnTerminal = true;
        boolean isCardAmountLimitAvailableOnAccount = true;
        boolean isCardAmountLimitAvailableOnTerminal = true;
        transactionLogger.error("commonValidatorVO.getTerminalId()---->"+commonValidatorVO.getTerminalId());
        if(functions.isValueNull(commonValidatorVO.getTerminalId()) && terminalMap.containsKey(commonValidatorVO.getTerminalId()))
        {
            TerminalVO terminalVO1=terminalMap.get(commonValidatorVO.getTerminalId());
            isCardLimitAvailableOnAccount = true;
            isCardLimitAvailableOnTerminal = true;
            isCardAmountLimitAvailableOnAccount = true;
            isCardAmountLimitAvailableOnTerminal = true;
            commonValidatorVO.setTerminalVO(terminalVO1);
            if (!"N".equalsIgnoreCase(terminalVO1.getCardLimitCheckAccountLevel()))
            {
                transactionLogger.error("--Check Card Limit Account level--");
                isCardLimitAvailableOnAccount = limitChecker.checkCardLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardAmountLimitCheckAccountLevel()))
            {
                transactionLogger.error("--Check Card Amount Limit Account level--");
                isCardAmountLimitAvailableOnAccount = limitChecker.checkCardAmountLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardLimitCheckTerminalLevel()))
            {
                transactionLogger.error("--Check Card Limit Terminal level--");
                isCardLimitAvailableOnTerminal = limitChecker.checkCardLimitTerminalLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardAmountLimitCheckTerminalLevel()))
            {
                transactionLogger.error("--Check Card Amount Limit Terminal level--");
                isCardAmountLimitAvailableOnTerminal = limitChecker.checkCardAmountLimitTerminalLevel(commonValidatorVO);
            }
            transactionLogger.error("isCardLimitAvailableOnAccount:::"+commonValidatorVO.getTerminalId()+"::" + isCardLimitAvailableOnAccount);
            transactionLogger.error("isCardAmountLimitAvailableOnAccount::"+commonValidatorVO.getTerminalId()+":::" + isCardAmountLimitAvailableOnAccount);
            transactionLogger.error("isCardLimitAvailableOnTerminal::"+commonValidatorVO.getTerminalId()+":::" + isCardLimitAvailableOnTerminal);
            transactionLogger.error("isCardAmountLimitAvailableOnTerminal::"+commonValidatorVO.getTerminalId()+":::" + isCardAmountLimitAvailableOnTerminal);
            if (isCardLimitAvailableOnAccount && isCardAmountLimitAvailableOnAccount && isCardLimitAvailableOnTerminal && isCardAmountLimitAvailableOnTerminal)
            {
                return terminalVO1;
            }else
            {
                terminalMap.remove(commonValidatorVO.getTerminalId());
            }
        }
        for (Map.Entry entry : terminalMap.entrySet())
        {
            String key= (String) entry.getKey();
            TerminalVO terminalVO1=terminalMap.get(key);
            isCardLimitAvailableOnAccount = true;
            isCardLimitAvailableOnTerminal = true;
            isCardAmountLimitAvailableOnAccount = true;
            isCardAmountLimitAvailableOnTerminal = true;
            commonValidatorVO.setTerminalVO(terminalVO1);
            if(functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType()) && (!commonValidatorVO.getPaymentType().equalsIgnoreCase(terminalVO1.getPaymodeId()) || !commonValidatorVO.getCardType().equalsIgnoreCase(terminalVO1.getCardTypeId())))
            {
                continue;
            }else if(functions.isValueNull(commonValidatorVO.getPaymentType()) && !commonValidatorVO.getPaymentType().equalsIgnoreCase(terminalVO1.getPaymodeId()))
            {
                continue;
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardLimitCheckAccountLevel()))
            {
                transactionLogger.error("--Check Card Limit Account level--");
                isCardLimitAvailableOnAccount = limitChecker.checkCardLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardAmountLimitCheckAccountLevel()))
            {
                transactionLogger.error("--Check Card Amount Limit Account level--");
                isCardAmountLimitAvailableOnAccount = limitChecker.checkCardAmountLimitAccountLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardLimitCheckTerminalLevel()))
            {
                transactionLogger.error("--Check Card Limit Terminal level--");
                isCardLimitAvailableOnTerminal = limitChecker.checkCardLimitTerminalLevel(commonValidatorVO);
            }
            if (!"N".equalsIgnoreCase(terminalVO1.getCardAmountLimitCheckTerminalLevel()))
            {
                transactionLogger.error("--Check Card Amount Limit Terminal level--");
                isCardAmountLimitAvailableOnTerminal = limitChecker.checkCardAmountLimitTerminalLevel(commonValidatorVO);
            }
            transactionLogger.error("isCardLimitAvailableOnAccount:::"+terminalVO1.getTerminalId()+"::" + isCardLimitAvailableOnAccount);
            transactionLogger.error("isCardAmountLimitAvailableOnAccount::"+terminalVO1.getTerminalId()+":::" + isCardAmountLimitAvailableOnAccount);
            transactionLogger.error("isCardLimitAvailableOnTerminal::"+terminalVO1.getTerminalId()+":::" + isCardLimitAvailableOnTerminal);
            transactionLogger.error("isCardAmountLimitAvailableOnTerminal::"+terminalVO1.getTerminalId()+":::" + isCardAmountLimitAvailableOnTerminal);
            if (isCardLimitAvailableOnAccount && isCardAmountLimitAvailableOnAccount && isCardLimitAvailableOnTerminal && isCardAmountLimitAvailableOnTerminal)
            {
                return terminalVO1;
            }
        }
        /*if(terminalVO==null)
        {
            error = "Transaction not permitted:::Please contact your Administrator.";
            errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_NOT_PERMITTED, error);
            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AMOUNT_LIMIT.toString(), ErrorType.SYSCHECK.toString());
            PZExceptionHandler.raiseConstraintViolationException("LimitChecker", "checkCardLimit()", null, "common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
        }*/

        return terminalVO;
    }
    private static ErrorCodeListVO getErrorVO(ErrorName errorName,String reason)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getSystemErrorCode(errorName);
        errorCodeVO.setErrorName(errorName);
        errorCodeVO.setErrorReason(reason);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }
}
