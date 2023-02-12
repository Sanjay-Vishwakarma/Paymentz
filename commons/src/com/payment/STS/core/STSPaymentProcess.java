package com.payment.STS.core;

import com.directi.pg.Logger;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 12/6/14
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class STSPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(STSPaymentProcess.class.getName());

    public Hashtable specificValidationAPI(Hashtable<String, String> parameter)
    {
        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    @Override
    public String specificValidationAPI(HttpServletRequest request)
    {
        //System.out.println("inside specific validation");
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, request.getParameter("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    public Hashtable specificValidation(Map<String, String> parameter)
    {

        InputValidator inputValidator = new InputValidator();
        Hashtable error = new Hashtable();
        Hashtable<InputFields, String> hashTable =new Hashtable<InputFields, String>();

        hashTable.put(InputFields.CARDHOLDERIP, parameter.get("cardholderipaddress"));

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(hashTable,errorList,false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :hashTable.keySet())
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error.put(inputFields.toString(),errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "sts_specificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}
