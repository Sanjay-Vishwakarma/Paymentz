package com.payment.jeton;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uday on 8/28/17.
 */
public class JetonInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger = new TransactionLogger(JetonInputValidator.class.getName());
    private Functions functions = new Functions();

    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName) throws PZDBViolationException
    {
        transactionLogger.error("Inside JetonVoucher Input Validator");
        String error = "";

        List<InputFields> parameters= new ArrayList();
        parameters.add(InputFields.VOUCHER_NUMBER);
        parameters.add(InputFields.SECURITY_CODE);
        parameters.add(InputFields.EXP_MONTH);
        parameters.add(InputFields.EXP_YEAR);
       // parameters.add(InputFields.AMOUNT);
      //  parameters.add(InputFields.DESCRIPTION);
      //  parameters.add(InputFields.ORDERDESCRIPTION);

        ValidationErrorList errorList= new ValidationErrorList();
        inputValidationForJetonVoucher(commonValidatorVO,parameters,errorList,false);

        String EOL = "<BR>";
        if(!errorList.isEmpty()){
            for(InputFields inputFields:parameters){
                if(errorList.getError(inputFields.toString())!=null){
                    error=error + errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
            return error;
        }
        MerchantDetailsVO merchantDetailsVO =commonValidatorVO.getMerchantDetailsVO();
        String accountid=merchantDetailsVO.getAccountId();
        String toid=merchantDetailsVO.getMemberId();

        if(!functions.isValueNull(toid)|| !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",20,false)) {
            error = ErrorMessages.INVALID_TOID;
        }else {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
            merchantDetailsVO.setAccountId(accountid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        return error;
    }

    public void inputValidationForJetonVoucher(CommonValidatorVO commonValidatorVO,List<InputFields> inputFields,ValidationErrorList validationErrorList,boolean isOptional)
    {
        GenericCardDetailsVO genericCardDetailsVO=commonValidatorVO.getCardDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();

        for(InputFields inputFields1:inputFields){
            switch (inputFields1)
            {
                case VOUCHER_NUMBER:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getVoucherNumber(), "OnlyNumber",25, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Voucher Number", "Invalid Voucher Number:::::" + genericCardDetailsVO.getVoucherNumber()));
                    break;
                case SECURITY_CODE:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericCardDetailsVO.getSecurity_Code(),"OnlyNumber",4,isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Security Code","Invalid Security Code:::::"+genericCardDetailsVO.getSecurity_Code()));
                    break;
                case EXP_MONTH:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericCardDetailsVO.getExpMonth(),"Months",10,isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Month","Invalid Month:::::"+genericCardDetailsVO.getExpMonth()));
                    break;
                case EXP_YEAR:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericCardDetailsVO.getExpYear(),"Years",10,isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Year","Invalid Year:::::"+genericCardDetailsVO.getExpYear()));
                    break;
               /* case AMOUNT:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getAmount(), "AmountStr", 12, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Amount", "Invalid Amount ::::" + genericTransDetailsVO.getAmount()));
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderId(), "Description", 100, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid OrderId", "Invalid  OrderId::::" + genericTransDetailsVO.getOrderId()));
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderDesc(), "Description", 255, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Order Description", "Invalid Order Description ::::" + genericTransDetailsVO.getOrderDesc()));
                    break;
*/
            }
        }
    }
}
