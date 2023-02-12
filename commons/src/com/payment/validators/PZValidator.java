package com.payment.validators;


import org.owasp.esapi.errors.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 4/3/14
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public interface PZValidator
{
    //from HttpRequest
    public  void InputValidations(HttpServletRequest request,List<InputFields> inputList, org.owasp.esapi.ValidationErrorList validationErrorList,boolean isOptional);

    public  void InputValidations(HttpServletRequest request,List<InputFields> inputList,boolean isOptional)throws ValidationException;


    //from Hashtable
    public  void InputValidations(Hashtable<InputFields,String> hashable, org.owasp.esapi.ValidationErrorList validationErrorList,boolean isOptional);

    public  void InputValidations(Hashtable<InputFields,String> hashTable,boolean isOptional)throws ValidationException;


}


