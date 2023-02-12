package com.payment.validators;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 8/1/14
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class InputValiDatorUtils
{
    private static Logger log = new Logger(InputValiDatorUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InputValiDatorUtils.class.getName());

    public String getError(ValidationErrorList errorList,List<InputFields> inputFieldsList,String actionName)
    {
        String error = "";
        String EOL = "<BR>";



        log.debug("action in Utils---"+actionName);
        if(!errorList.isEmpty() && actionName!=null)
        {
            if(actionName.equals("STDKIT"))
            {
                for (InputFields inputFields : inputFieldsList)
                {

                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                        ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + " " + errorCodeVO.getErrorDescription()+ EOL;

                    }
                }
            }
            else if(actionName.equals("DKIT"))
            {
                for (InputFields inputFields : inputFieldsList)
                {

                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.debug("line 63------------------------");
                        PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                        ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + " "+errorCodeVO.getErrorCode()+"_"+ errorCodeVO.getErrorDescription()+"|";

                    }
                }
            }
            else if(actionName.equals("REST"))
            {
                for (InputFields inputFields : inputFieldsList)
                {

                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                        ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + " "+errorCodeVO.getApiCode()+"_"+ errorCodeVO.getApiDescription()+"|";

                    }
                }
            }
            else if(actionName.equals("VT"))
            {
                for (InputFields inputFields : inputFieldsList)
                {

                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                        ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error +  errorCodeVO.getErrorDescription()+ EOL;
                        //error = error + " " + errorCodeVO.getErrorDescription() + ", " + errorCodeVO.getErrorReason() + EOL;

                    }
                }
            }
        }
        return error;
    }

    public List<InputFields> getDirectKitOptionalInputField()
    {
        List<InputFields> DirectKitOptionalFieldsList = new ArrayList<InputFields>();
        DirectKitOptionalFieldsList.add(InputFields.BIRTHDATE);
        DirectKitOptionalFieldsList.add(InputFields.SSN);
        DirectKitOptionalFieldsList.add(InputFields.REDIRECT_URL);
        DirectKitOptionalFieldsList.add(InputFields.ORDERDESCRIPTION);
        DirectKitOptionalFieldsList.add(InputFields.CARDHOLDERIP);
        return null;
    }

    public List<InputFields> getDirectKitMandatoryField()
    {
        List<InputFields> DirectKitMandatoryFieldsList = new ArrayList<InputFields>();

        DirectKitMandatoryFieldsList.add(InputFields.STREET);
        DirectKitMandatoryFieldsList.add(InputFields.CITY);
        DirectKitMandatoryFieldsList.add(InputFields.ZIP);
        DirectKitMandatoryFieldsList.add(InputFields.STATE);
        DirectKitMandatoryFieldsList.add(InputFields.COUNTRYCODE);
        DirectKitMandatoryFieldsList.add(InputFields.TELNO);
        DirectKitMandatoryFieldsList.add(InputFields.TELCC);
        DirectKitMandatoryFieldsList.add(InputFields.EMAILADDR);
        DirectKitMandatoryFieldsList.add(InputFields.CARDNUMBER);
        DirectKitMandatoryFieldsList.add(InputFields.EXPIRY_MONTH);
        DirectKitMandatoryFieldsList.add(InputFields.EXPIRY_YEAR);
        DirectKitMandatoryFieldsList.add(InputFields.CVV_SMALL);
        DirectKitMandatoryFieldsList.add(InputFields.FIRSTNAME);
        DirectKitMandatoryFieldsList.add(InputFields.LASTNAME);
        DirectKitMandatoryFieldsList.add(InputFields.LANGUAGE);
        DirectKitMandatoryFieldsList.add(InputFields.TOID);
        DirectKitMandatoryFieldsList.add(InputFields.TOTYPE);
        DirectKitMandatoryFieldsList.add(InputFields.AMOUNT);
        DirectKitMandatoryFieldsList.add(InputFields.DESCRIPTION);
        //DirectKitMandatoryFieldsList.add(InputFields.FIRSTNAME_WC);
       // DirectKitMandatoryFieldsList.add(InputFields.LASTNAME_WC);



        return DirectKitMandatoryFieldsList;
    }

    public List<InputFields> getSTDKitOptionalFieldStep1()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.ORDERDESCRIPTION);
        inputFieldsListOptional.add(InputFields.IPADDR);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.CITY);
        inputFieldsListOptional.add(InputFields.STREET);
        inputFieldsListOptional.add(InputFields.ZIP);
        inputFieldsListOptional.add(InputFields.STATE);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.TELCC);
        inputFieldsListOptional.add(InputFields.COUNTRYCODE);
        inputFieldsListOptional.add(InputFields.INVOICE_NO);
        inputFieldsListOptional.add(InputFields.NOTIFICATIONURL);
        inputFieldsListOptional.add(InputFields.CUSTOMERID);
        inputFieldsListOptional.add(InputFields.TMPL_CURRENCY);
        inputFieldsListOptional.add(InputFields.TMPL_AMOUNT);
        inputFieldsListOptional.add(InputFields.FIRSTNAME);
        inputFieldsListOptional.add(InputFields.LASTNAME);
        inputFieldsListOptional.add(InputFields.BIRTHDATE);
        inputFieldsListOptional.add(InputFields.DEVICE);

        //inputFieldsListOptional.add(InputFields.LANGUAGE);


        //inputFieldsListOptional.add(InputFields.CURRENCY);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestKitMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        //inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TOTYPE);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);

        //inputFieldsListMandatory.add(InputFields.CVV);
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);

        //inputFieldsListOptional.add(InputFields.CURRENCY);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestKitOptionalParameters()
    {


        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ORDERDESCRIPTION);
        inputFieldsListMandatory.add(InputFields.INVOICE_NO);
        inputFieldsListMandatory.add(InputFields.CVV);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getSTDKitMandatoryFieldStep1()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TOTYPE);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
       // inputFieldsListMandatory.add(InputFields.CARDHOLDERIP);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getCardRegistrationMandatoryFieldStep1()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TOTYPE);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getSTDFlowMandatoryFieldStep1()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        return inputFieldsListMandatory;
    }


    public List<InputFields> getSTDKitMandatoryFieldStep2()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.COUNTRYCODE);
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.CVV);
        inputFieldsListMandatory.add(InputFields.STREET);
        inputFieldsListMandatory.add(InputFields.CITY);
        inputFieldsListMandatory.add(InputFields.STATE);
        inputFieldsListMandatory.add(InputFields.ZIP);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getMandatoryFieldStep2()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        //inputFieldsListMandatory.add(InputFields.CVV);
        //inputFieldsListMandatory.add(InputFields.EMAILADDR);Move to flag based personal info validation
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getPersonalDetailValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.EMAILADDR);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getDKMandatoryFieldStep2()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        //inputFieldsListMandatory.add(InputFields.CVV);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        //inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        //inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        //inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getCardDetailValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.CVV);
        //inputFieldsListMandatory.add(InputFields.PAYMENTBRAND);
        //inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.PAN);
        //inputFieldsListMandatory.add(InputFields.CARDHOLDER);
        //inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        //inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);
        //inputFieldsListMandatory.add(InputFields.CARDHOLDER);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getCardDetailValidationForDK()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        //inputFieldsListMandatory.add(InputFields.CVV);
        //inputFieldsListMandatory.add(InputFields.PAYMENTBRAND);
        //inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.PAN);
        //inputFieldsListMandatory.add(InputFields.CARDHOLDER);
        //inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        //inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);
        //inputFieldsListMandatory.add(InputFields.CARDHOLDER);

        return inputFieldsListMandatory;
    }


    public List<InputFields> getCardDetailsExcludeCvvValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList();
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getCardFieldsForTokenTransaction(boolean isCVVRequired)
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);
        if (isCVVRequired)
        {
            inputFieldsListMandatory.add(InputFields.CVV);
        }
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestCardDetailValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.CVV);
        inputFieldsListMandatory.add(InputFields.PAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EXPIRE_MONTH);
        inputFieldsListMandatory.add(InputFields.EXPIRE_YEAR);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getAddressFieldValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        //inputFieldsListMandatory.add(InputFields.COUNTRY);
        inputFieldsListMandatory.add(InputFields.STREET);
        inputFieldsListMandatory.add(InputFields.CITY);
        //inputFieldsListMandatory.add(InputFields.STATE);
        inputFieldsListMandatory.add(InputFields.ZIP);
        //inputFieldsListMandatory.add(InputFields.TELNO);
        //inputFieldsListMandatory.add(InputFields.TELCC);
        inputFieldsListMandatory.add(InputFields.COUNTRYCODE);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getStateValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.STATE);
        inputFieldsListMandatory.add(InputFields.TELCC);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getAddressFieldValidationForCardholderRegistration()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ZIP);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.GENDER);
        inputFieldsListMandatory.add(InputFields.BIRTHDATE);
        inputFieldsListMandatory.add(InputFields.COUNTRY_OTP);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getOptionalValidationForMerchantSignup()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ZIP);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.TELCC);
        inputFieldsListMandatory.add(InputFields.GENDER);
        inputFieldsListMandatory.add(InputFields.BIRTHDATE);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.COMPANY_NAME);
        inputFieldsListMandatory.add(InputFields.WEBSITE);
     //   inputFieldsListMandatory.add(InputFields.CONTACT_PERSON);
        //inputFieldsListMandatory.add(InputFields.COUNTRY);


        return inputFieldsListMandatory;
    }
    /*public List<InputFields> getOptionalprocessGenerateOTPTwoStepParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ZIP);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.GENDER);
        inputFieldsListMandatory.add(InputFields.BIRTHDATE);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.COMPANY_NAME);
        inputFieldsListMandatory.add(InputFields.WEBSITE);
      //  inputFieldsListMandatory.add(InputFields.COUNTRY);


        return inputFieldsListMandatory;
    }*/



    public List<InputFields> getTokenTransactionMandatoryField()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TOTYPE);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.COUNTRYCODE);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.CVV);
        inputFieldsListMandatory.add(InputFields.STREET);
        inputFieldsListMandatory.add(InputFields.CITY);
        inputFieldsListMandatory.add(InputFields.STATE);
        inputFieldsListMandatory.add(InputFields.ZIP);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.TELCC);
        inputFieldsListMandatory.add(InputFields.BIRTHDATE);
        return inputFieldsListMandatory;

    }
    public List<InputFields> getSTDKitOptionalFieldStep2()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TELCC);
        inputFieldsListMandatory.add(InputFields.STATE);
        inputFieldsListMandatory.add(InputFields.CVV);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getDKConditionalParam()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getConditionalParameterList(String autoSelectTerminal, String terminalId)
    {
        Functions functions=new Functions();

        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();
        if(autoSelectTerminal!=null)
        {
            if(autoSelectTerminal.equals("Y") && functions.isEmptyOrNull(terminalId))
            {
                //Please dont remove comment. already handle in validateStandardProcessStep0
                /*inputConditionalFieldsList.add(InputFields.PAYMENTYPE);
                inputConditionalFieldsList.add(InputFields.CARDTYPE);*/
            }
            else
            {
                inputConditionalFieldsList.add(InputFields.TERMINALID);
            }
        }
        inputConditionalFieldsList.add(InputFields.PAYMENTBRAND);
        return inputConditionalFieldsList;
    }

    public List<InputFields> getConditionalParameterListDK(String autoSelectTerminal, String terminalId)
    {
        transactionLogger.debug("inside InputValidotorListDk line 517--------------------------");
        transactionLogger.debug("autoSelectTerminal line 517--------------------------"+autoSelectTerminal);
        transactionLogger.debug("terminalId-------------------------"+terminalId);
        Functions functions=new Functions();

        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();
        if(autoSelectTerminal!=null)
        {
            if(autoSelectTerminal.equals("Y") && functions.isEmptyOrNull(terminalId))
            {
                transactionLogger.debug("inside    autoSelectTerminal.equals(\"Y\")----------------------------");
                //Please dont remove comment. already handle in validateStandardProcessStep0
                inputConditionalFieldsList.add(InputFields.PAYMENTYPE);
                inputConditionalFieldsList.add(InputFields.CARDTYPE);
            }
            else
            {
                transactionLogger.debug("line 534----------------------");
                inputConditionalFieldsList.add(InputFields.TERMINALID);
            }
        }
        inputConditionalFieldsList.add(InputFields.PAYMENTBRAND);
        return inputConditionalFieldsList;
    }

    public List<InputFields> getCardandPaymentValidation()
    {

        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();
        inputConditionalFieldsList.add(InputFields.PAYMENTYPE);
        inputConditionalFieldsList.add(InputFields.CARDTYPE);
        //inputConditionalFieldsList.add(InputFields.TERMINALID);
        inputConditionalFieldsList.add(InputFields.PAYMENTBRAND);
        return inputConditionalFieldsList;
    }

    public List<InputFields> getIPandTerminslValidation(String autoSelectTerminal,String ipWhitelisted)
    {

        List<InputFields> inputIPWhitelistedFieldsList = new ArrayList<InputFields>();
        if(ipWhitelisted!=null)
        {
            if(ipWhitelisted.equals("Y"))
            {
                inputIPWhitelistedFieldsList.add(InputFields.IPADDR);
            }
            if(autoSelectTerminal.equals("N"))
            {
                inputIPWhitelistedFieldsList.add(InputFields.TERMINALID);
            }
        }
        return inputIPWhitelistedFieldsList;
    }

    public List<InputFields> getSKitConditionalFieldStep1(CommonValidatorVO commonValidatorVO)
    {
        log.debug("Inside getSKitConditionalFieldStep1---");
        Functions functions = new Functions();
        List<InputFields> inputFieldsListConditional = new ArrayList<InputFields>();

        if(commonValidatorVO.getMerchantDetailsVO().getAutoSelectTerminal()!=null)
        {
            // new flow :if((!functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType()) && !commonValidatorVO.getCardType().equals("123")))

            if(commonValidatorVO.getMerchantDetailsVO().getAutoSelectTerminal().equals("Y") && !functions.isValueNull(commonValidatorVO.getTerminalId()))
            {
                if((!functions.isValueNull(commonValidatorVO.getPaymentType()) && functions.isValueNull(commonValidatorVO.getCardType())))
                {
                    inputFieldsListConditional.add(InputFields.PAYMENTYPE);
                    //inputFieldsListConditional.add(InputFields.CARDTYPE);
                }
            }
            else
            {
                inputFieldsListConditional.add(InputFields.TERMINALID);
            }
        }
        return inputFieldsListConditional;
    }

    public List<InputFields> getSKitOptionalForPTandCTFieldStep1()
    {
        log.debug("Inside getSKitOptionalForPTandCTFieldStep1------");
        List<InputFields> inputFieldsListConditional = new ArrayList<InputFields>();

        inputFieldsListConditional.add(InputFields.PAYMENTYPE);
        inputFieldsListConditional.add(InputFields.CARDTYPE);
        inputFieldsListConditional.add(InputFields.ACCOUNTID);

        return inputFieldsListConditional;
    }

    public List<InputFields> getConditionalParameterListForRest(String autoSelectTerminal, String ipWhitelisted, String terminalId)
    {
        Functions functions=new Functions();

        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();
        if(autoSelectTerminal!=null)
        {
            if(autoSelectTerminal.equals("Y") && functions.isEmptyOrNull(terminalId))
            {
                inputConditionalFieldsList.add(InputFields.PAYMENTYPE);
                inputConditionalFieldsList.add(InputFields.CARDTYPE);
                inputConditionalFieldsList.add(InputFields.CURRENCY);
            }
            else
            {
                inputConditionalFieldsList.add(InputFields.TERMINALID);
            }
        }

        if(ipWhitelisted!=null && ipWhitelisted.equals("Y"))
        {
            inputConditionalFieldsList.add(InputFields.IPADDR);
        }
        inputConditionalFieldsList.add(InputFields.DESCRIPTION);
        return inputConditionalFieldsList;
    }

    public List<InputFields> getConditionalParameterListForRest(String terminalId)
    {
        Functions functions=new Functions();

        List<InputFields> inputConditionalFieldsList = new ArrayList<InputFields>();

        if(!functions.isEmptyOrNull(terminalId))
        {
            inputConditionalFieldsList.add(InputFields.TERMINALID);
        }
        else
        {
            inputConditionalFieldsList.add(InputFields.PAYMENTYPE);
            inputConditionalFieldsList.add(InputFields.CARDTYPE);
            //inputConditionalFieldsList.add(InputFields.CURRENCY);
        }
        inputConditionalFieldsList.add(InputFields.EXPIRE_MONTH);
        inputConditionalFieldsList.add(InputFields.EXPIRE_YEAR);
        inputConditionalFieldsList.add(InputFields.CVV);
        return inputConditionalFieldsList;
    }

    public List<InputFields> getRestTransactionMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAYMENTBRAND);
        inputFieldsListMandatory.add(InputFields.PAYMENTMODE);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestAccountDetailsValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.CARDHOLDER);
        inputFieldsListMandatory.add(InputFields.BIC);
        inputFieldsListMandatory.add(InputFields.IBAN);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestBitcoinValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestPaymitcoDetailsValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ACCOUNTNUMBER);
        //inputFieldsListMandatory.add(InputFields.ACCOUNTTYPE);// comment for echeck.
        inputFieldsListMandatory.add(InputFields.ROUTINGNUMBER);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestCellulantDetailsValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ACCOUNTNUMBER);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestOptionalPaymitcoAccountDetailsValidation(){
        List<InputFields> inputFieldsListOptional=new ArrayList<>();
        inputFieldsListOptional.add(InputFields.CHECKNUMBER);
        inputFieldsListOptional.add(InputFields.ACCOUNTTYPE);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestRefundCaptureMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        inputFieldsListMandatory.add(InputFields.TOID);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestRefundCaptureOptionalParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TERMINALID);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestDynamicQRParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        inputFieldsListMandatory.add(InputFields.AMOUNT);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestStaticQRParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);

        return inputFieldsListMandatory;
    }


    public List<InputFields> getRestMandatoryQRParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ORDERID);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.PAYMENTMODE);
        inputFieldsListOptional.add(InputFields.PAYMENTBRAND);
        inputFieldsListOptional.add(InputFields.LASTNAME);
        inputFieldsListOptional.add(InputFields.FIRSTNAME);
        inputFieldsListOptional.add(InputFields.WALLET_ID);
        inputFieldsListOptional.add(InputFields.WALLET_AMOUNT);
        inputFieldsListOptional.add(InputFields.WALLET_CURRENCY);
        return inputFieldsListOptional;
    }

    public List<InputFields> getRestMandatoryQRCheckoutParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.ORDERID);
        inputFieldsListOptional.add(InputFields.AMOUNT);
        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.PAYMENTMODE);
        inputFieldsListOptional.add(InputFields.PAYMENTBRAND);
        inputFieldsListOptional.add(InputFields.NOTIFICATIONURL);
        return inputFieldsListOptional;
    }



    public List<InputFields> getRestRecurringAndTokenMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestCancelMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        inputFieldsListMandatory.add(InputFields.TOID);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestInquiryMandatoryParameters(CommonValidatorVO commonValidatorVO)
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();
        //inputFieldsList.add(InputFields.TRACKINGID);
        inputFieldsList.add(InputFields.TOID);
        if (commonValidatorVO.getIdType().equalsIgnoreCase("MID")){
            inputFieldsList.add(InputFields.DESCRIPTION);
        }else
        {
            inputFieldsList.add(InputFields.TRACKINGID);
        }


        return inputFieldsList;
    }

    public List<InputFields> getRestTransactionOptionalParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ORDERDESCRIPTION);
        inputFieldsListOptional.add(InputFields.BIRTHDATE);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.IPADDR);
        inputFieldsListOptional.add(InputFields.CREATEREGISTRATION);
        inputFieldsListOptional.add(InputFields.RECURRINGTYPE);
        inputFieldsListOptional.add(InputFields.LASTNAME);
        inputFieldsListOptional.add(InputFields.FIRSTNAME);
        inputFieldsListOptional.add(InputFields.CUSTOMERID);
        inputFieldsListOptional.add(InputFields.CVV);
        inputFieldsListOptional.add(InputFields.WALLET_ID);
        inputFieldsListOptional.add(InputFields.WALLET_AMOUNT);
        inputFieldsListOptional.add(InputFields.WALLET_CURRENCY);
        inputFieldsListOptional.add(InputFields.ACCACTIVATIONDATE);
        inputFieldsListOptional.add(InputFields.ACCCHANGEDATE);
        inputFieldsListOptional.add(InputFields.ACCPWCHANGEDATE);
        inputFieldsListOptional.add(InputFields.ADDRESSUSEDATE);
        inputFieldsListOptional.add(InputFields.PAYMENTACCACTIVATIONDATE);
        inputFieldsListOptional.add(InputFields.TMPL_AMOUNT);
        inputFieldsListOptional.add(InputFields.TMPL_CURRENCY);
        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.REDIRECT_URL);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.TELCC);
        inputFieldsListOptional.add(InputFields.ACCOUNTID);
        /*inputFieldsListOptional.add(InputFields.PAYMENTBRAND);
        inputFieldsListOptional.add(InputFields.PAYMENTMODE);*/
        inputFieldsListOptional.add(InputFields.LANGUAGE);
        return inputFieldsListOptional;
    }

    public List<InputFields> getRestTokenOptionalParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.BIRTHDATE);
        inputFieldsListOptional.add(InputFields.IPADDR);
        inputFieldsListOptional.add(InputFields.CUSTOMERID);
        inputFieldsListOptional.add(InputFields.LANGUAGE);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestTokenWithCardMandatoryParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAN);
        inputFieldsListOptional.add(InputFields.EXPIRE_YEAR);
        inputFieldsListOptional.add(InputFields.EXPIRE_MONTH);
        inputFieldsListOptional.add(InputFields.FIRSTNAME);
        inputFieldsListOptional.add(InputFields.LASTNAME);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestCustomerRegistrationMandatoryParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.FIRSTNAME_REST);
        inputFieldsListOptional.add(InputFields.LASTNAME_REST);

        return inputFieldsListOptional;
    }
    public List<InputFields>getUpdateAddressMandatoryParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.STREET);
        inputFieldsListOptional.add(InputFields.CITY);
        inputFieldsListOptional.add(InputFields.STATE);
        inputFieldsListOptional.add(InputFields.ZIP);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestMerchantSignupParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.LOGIN);
        inputFieldsListOptional.add(InputFields.NEW_PASSWORD);
        inputFieldsListOptional.add(InputFields.CONFIRM_PASSWORD);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
       // inputFieldsListOptional.add(InputFields.PHONENO);
        inputFieldsListOptional.add(InputFields.PARTNER_ID);
        inputFieldsListOptional.add(InputFields.COUNTRY_OTP);
        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);


       // inputFieldsListOptional.add(InputFields.TELCC);

        //inputFieldsListOptional.add(InputFields.FIRSTNAME);
        //inputFieldsListOptional.add(InputFields.LASTNAME);

        //inputFieldsListOptional.add(InputFields.COMPANY_NAME);
        //inputFieldsListOptional.add(InputFields.WEBSITE);
        //inputFieldsListOptional.add(InputFields.COUNTRY);


        return inputFieldsListOptional;
    }
    public List<InputFields> getGenerateAppOTP()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

       // inputFieldsListOptional.add(InputFields.TELCC);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.COUNTRY_OTP);
        inputFieldsListOptional.add(InputFields.EMAILADDR);

        return inputFieldsListOptional;
    }

    public List<InputFields> getVerifyAppOTP()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

       // inputFieldsListOptional.add(InputFields.TELCC);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.COUNTRY_OTP);
      //  inputFieldsListOptional.add(InputFields.OTP);


        return inputFieldsListOptional;
    }

    public List<InputFields> getRestMerchantLoginParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.LOGIN);
        inputFieldsList.add(InputFields.PASSWORD);
        inputFieldsList.add(InputFields.PARTNERID);

        return inputFieldsList;
    }

    public List<InputFields> getRestMerchantAuthTokenParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.LOGIN);
        //inputFieldsList.add(InputFields.PASSWORD);
        inputFieldsList.add(InputFields.PARTNERID);

        return inputFieldsList;
    }

    public List<InputFields> getRestPartnerAuthTokenParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.PARTNER_NAME);
        //inputFieldsList.add(InputFields.PASSWORD);
        inputFieldsList.add(InputFields.PARTNERID);

        return inputFieldsList;
    }

    public List<InputFields> getRestMerchantAuthTokenOptionalParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.PASSWORD);
        inputFieldsList.add(InputFields.AUTHTOKEN);

        return inputFieldsList;
    }


    public List<InputFields> getRestVerifyMailParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.LOGIN);


        return inputFieldsList;
    }

    public List<InputFields> getNewAuthTokenParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.AUTHTOKEN);
        inputFieldsList.add(InputFields.PARTNERID);

        return inputFieldsList;
    }

    public List<InputFields> getRestMerchantCurrencyParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.TOID);
        return inputFieldsList;
    }
    public List<InputFields> getRestMerchantCurrencyOptionalParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.LOGIN);
        return inputFieldsList;
    }

    public List<InputFields> getRestMerchantChangePasswordParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.TOID);
        inputFieldsList.add(InputFields.PASSWORD);
        inputFieldsList.add(InputFields.NEW_PASSWORD);
        inputFieldsList.add(InputFields.LOGIN);

        return inputFieldsList;
    }


    public List<InputFields> getRestTokenConditionalParametersForBrands()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAYMENTBRAND);
        inputFieldsListOptional.add(InputFields.PAYMENTMODE);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestAPIOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TERMINALID);
        return inputFieldsListOptional;
    }

    public List<InputFields> getRestTokenOptionalParametersForBrands()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.CVV);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestTransactionRecurringOptionalParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.RECURRINGTYPE);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.BIRTHDATE);
        inputFieldsListOptional.add(InputFields.FIRSTNAME);
        inputFieldsListOptional.add(InputFields.LASTNAME);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.EMICOUNT);
        inputFieldsListOptional.add(InputFields.ACCACTIVATIONDATE);
        inputFieldsListOptional.add(InputFields.ACCCHANGEDATE);
        inputFieldsListOptional.add(InputFields.ACCPWCHANGEDATE);
        inputFieldsListOptional.add(InputFields.ADDRESSUSEDATE);
        inputFieldsListOptional.add(InputFields.PAYMENTACCACTIVATIONDATE);
        inputFieldsListOptional.add(InputFields.IPADDR);

        return inputFieldsListOptional;
    }

    public List<InputFields> getStandardKitInputFieldStep1()
    {
        List<InputFields> inputTransactionFieldsList = new ArrayList<InputFields>();
        inputTransactionFieldsList.add(InputFields.TOID);
        inputTransactionFieldsList.add(InputFields.TOTYPE);
        inputTransactionFieldsList.add(InputFields.AMOUNT);
        inputTransactionFieldsList.add(InputFields.TMPL_AMOUNT);
        inputTransactionFieldsList.add(InputFields.DESCRIPTION);
        inputTransactionFieldsList.add(InputFields.TMPL_CURRENCY);
        return inputTransactionFieldsList;
    }
    public List<InputFields> getStandardKitOptionalInputFieldStep1()
    {
        List<InputFields> inputTransactionFieldsList = new ArrayList<InputFields>();
        inputTransactionFieldsList.add(InputFields.ORDERDESCRIPTION);
        inputTransactionFieldsList.add(InputFields.IPADDR);
        inputTransactionFieldsList.add(InputFields.TMPL_EMAILADDR);
        inputTransactionFieldsList.add(InputFields.TMPL_CITY);
        inputTransactionFieldsList.add(InputFields.TMPL_STREET);
        inputTransactionFieldsList.add(InputFields.TMPL_ZIP);
        inputTransactionFieldsList.add(InputFields.TMPL_STATE);
        inputTransactionFieldsList.add(InputFields.TMPL_TELNO);
        inputTransactionFieldsList.add(InputFields.TMPL_TELNOCC);
        inputTransactionFieldsList.add(InputFields.TMPL_COUNTRY);
        inputTransactionFieldsList.add(InputFields.INVOICE_NO);
       // inputTransactionFieldsList.add(InputFields.REDIRECT_URL);

        return inputTransactionFieldsList;
    }

    public List<InputFields> getManualRebillMandatoryFields()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TOTYPE);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        // inputFieldsListMandatory.add(InputFields.IPADDR);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getCardAndAccountOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.PARTNERID);
        inputFieldsListOptional.add(InputFields.CUSTOMERID);
        return inputFieldsListOptional;
    }

    public List<InputFields>getInvoiceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ORDERDESC);

        return inputFieldsListOptional;
    }

    public List<InputFields> getCancelInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.INVOICENO);
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.REDIRECT_URL);
        inputFieldsListOptional.add(InputFields.REASON);
        //inputFieldsListOptional.add(InputFields.CHECKSUM);

        return inputFieldsListOptional;
    }

    public List<InputFields> getInquiryInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TOID);
        //inputFieldsListOptional.add(InputFields.CHECKSUM);
        inputFieldsListOptional.add(InputFields.DESCRIPTION);

        return inputFieldsListOptional;
    }

    public List<InputFields> getInquiryInvoiceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.INVOICENO);

        return inputFieldsListOptional;
    }

    //new

    public List<InputFields> getInvoicelistMandatoryParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TOID);
        //inputFieldsListOptional.add(InputFields.CHECKSUM);
        //inputFieldsListOptional.add(InputFields.ACTION_TYPE);


        return inputFieldsListOptional;
    }

    public List<InputFields> getPayoutTransactionMandatoryParams(String tranType,String trackingId,String personalInfoValidation)
    {
        Functions functions =  new Functions();

        List<InputFields> inputFields = new ArrayList<InputFields>();
        inputFields.add(InputFields.DESCRIPTION);
        inputFields.add(InputFields.AMOUNT);
        inputFields.add(InputFields.TOID);
        if(!functions.isValueNull(trackingId)){
            inputFields.add(InputFields.TRANSFER_TYPE);
        }

        if("Y".equalsIgnoreCase(personalInfoValidation)){
            inputFields.add(InputFields.EMAILADDR);
            inputFields.add(InputFields.TELNO);
            inputFields.add(InputFields.TELCC);
        }

        //inputFields.add(InputFields.TERMINALID);
        if("IMPS".equalsIgnoreCase(tranType)){
            inputFields.add(InputFields.BANK_ACCOUNT_NAME);
            inputFields.add(InputFields.BANK_ACCOUNT_NUMBER);
            inputFields.add(InputFields.BANK_IFSC);
        }
        else if("TigerPayWallet".equalsIgnoreCase(tranType)){
            inputFields.add(InputFields.BANK_ACCOUNT_NAME);
            inputFields.add(InputFields.BANK_ACCOUNT_NUMBER);
        }
        else if("JPBank".equalsIgnoreCase(tranType)){
            inputFields.add(InputFields.BANK_NAME);
            inputFields.add(InputFields.BRANCH_NAME);
            inputFields.add(InputFields.BANK_CODE);
            inputFields.add(InputFields.ACCOUNT_TYPE);
            inputFields.add(InputFields.BRANCH_CODE);
            inputFields.add(InputFields.BANK_ACCOUNT_NAME);
            inputFields.add(InputFields.BANK_ACCOUNT_NUMBER);
        }
        /*else if("Bank_Transfer".equalsIgnoreCase(tranType)){
            inputFields.add(InputFields.ACCOUNTNUMBER);
            inputFields.add(InputFields.BANK_NAME);
            inputFields.add(InputFields.BANK_CODE);
            inputFields.add(InputFields.ACCOUNT_TYPE);
            inputFields.add(InputFields.BRANCH_CODE);
            inputFields.add(InputFields.BANK_ACCOUNT_NUMBER);
        }else if("PIX".equalsIgnoreCase(tranType) || "PICPAY".equalsIgnoreCase(tranType) ){
            inputFields.add(InputFields.ACCOUNTNUMBER);
            inputFields.add(InputFields.BANK_NAME);
            inputFields.add(InputFields.BANK_CODE);
        }*/
        return inputFields;
    }

    public List<InputFields> getPayoutOptionalParams()
    {
        List<InputFields> inputFields = new ArrayList<InputFields>();
        inputFields.add(InputFields.ORDERDESC);
        inputFields.add(InputFields.TRACKINGID);
        //inputFields.add(InputFields.EMAILADDR);
        inputFields.add(InputFields.TERMINALID);
        inputFields.add(InputFields.PAYMENTBRAND);
        inputFields.add(InputFields.PAYMENTMODE);
        inputFields.add(InputFields.CURRENCY);
        inputFields.add(InputFields.EXP_DATE_OFFSET);
        inputFields.add(InputFields.PAYOUT_TYPE);
        inputFields.add(InputFields.NOTIFICATIONURL);
        inputFields.add(InputFields.WALLET_ID);
        inputFields.add(InputFields.WALLET_AMOUNT);
        inputFields.add(InputFields.WALLET_CURRENCY);

       /*  inputFields.add(InputFields.BANK_ACCOUNT_NAME);
        inputFields.add(InputFields.BANK_ACCOUNT_NUMBER);
        inputFields.add(InputFields.BANK_IFSC);*/


        return inputFields;
    }

    public List<InputFields> getInvoicelistOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.LOGIN);
         inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.TODATE);
        return inputFieldsListOptional;
    }

    public List<InputFields> getInvoiceConfigListOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.INITIAL);
        inputFieldsListOptional.add(InputFields.REDIRECT_URL);
        inputFieldsListOptional.add(InputFields.INVOICE_EXPIRATIONPERIOD);
        inputFieldsListOptional.add(InputFields.GST);
        inputFieldsListOptional.add(InputFields.LATEFEE);
        inputFieldsListOptional.add(InputFields.PAYMENTTERMS);
        inputFieldsListOptional.add(InputFields.ACTION_TYPE);
        inputFieldsListOptional.add(InputFields.UNIT);
        inputFieldsListOptional.add(InputFields.DEFAULTUNIT);

        return inputFieldsListOptional;
    }

    public List<InputFields> getInvoiceConfigListMandatoryParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TOID);
        //inputFieldsListOptional.add(InputFields.ACTION_TYPE);

        return inputFieldsListOptional;
    }
    public List<InputFields> getInvoiceConfigDefaultProductOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.DEFAULT_PRODUCT_DESCRIPTION);
        inputFieldsListOptional.add(InputFields.DEFAULT_QUANTITY);
        inputFieldsListOptional.add(InputFields.DEFAULT_PRODUCT_AMOUNT);
        inputFieldsListOptional.add(InputFields.DEFAULT_PRODUCT_UNIT);
        return inputFieldsListOptional;
    }

    public List<InputFields> getInvoiceConfigProductListOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.PRODUCT_DESCRIPTION);
        inputFieldsListOptional.add(InputFields.QUANTITY);
        inputFieldsListOptional.add(InputFields.PRODUCT_AMOUNT);
        inputFieldsListOptional.add(InputFields.PRODUCT_TOTAL);
        return inputFieldsListOptional;
    }

    public List<InputFields> getInvoiceOrderIdMandatoryParams()
    {
        List<InputFields> inputFieldsMandatory = new ArrayList<InputFields>();

        inputFieldsMandatory.add(InputFields.TOID);
        return inputFieldsMandatory;
    }

    public List<InputFields> getCancelInvoiceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ORDERDESC);
        inputFieldsListOptional.add(InputFields.USERNAME);

        return inputFieldsListOptional;
    }


    public List<InputFields> getCardAndAccountMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        //inputFieldsListMandatory.add(InputFields.CHECKSUM);
        return inputFieldsListMandatory;
    }

    public  List<InputFields> getInvoiceRegenerateMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        return  inputFieldsListMandatory;
    }

    public  List<InputFields> getInvoiceRemindMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        return  inputFieldsListMandatory;
    }

    public  List<InputFields> getGenerateInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
       // inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        //inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        //inputFieldsListMandatory.add(InputFields.CUSTNAME);
        inputFieldsListMandatory.add(InputFields.INVOICE_EXPIRATIONPERIOD);
        return  inputFieldsListMandatory;
    }

    public  List<InputFields> getInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        return  inputFieldsListMandatory;
    }

    public  List<InputFields> getRegenerateInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        inputFieldsListMandatory.add(InputFields.INVOICE_EXPIRATIONPERIOD);
        inputFieldsListMandatory.add(InputFields.CHECKSUM);
        inputFieldsListMandatory.add(InputFields.DESCRIPTION);
        return  inputFieldsListMandatory;
    }

    public List<InputFields> getRegenerateInvoiceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional=new ArrayList<>();
        inputFieldsListOptional.add(InputFields.USERNAME);
        return inputFieldsListOptional;
    }

    public  List<InputFields> getRemindInvoiceMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.INVOICENO);
        inputFieldsListMandatory.add(InputFields.REDIRECT_URL);
        //inputFieldsListMandatory.add(InputFields.CHECKSUM);
        return  inputFieldsListMandatory;
    }
    public  List<InputFields> getInvoiceConfigMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        //inputFieldsListMandatory.add(InputFields.CHECKSUM);
        return  inputFieldsListMandatory;
    }

    public List<InputFields>getGenerateInvoiceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.ORDERDESCRIPTION);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.TERMINALID);
        inputFieldsListOptional.add(InputFields.PAYMODE);
        inputFieldsListOptional.add(InputFields.CARDTYPE);
        inputFieldsListOptional.add(InputFields.COUNTRY);
        inputFieldsListOptional.add(InputFields.CITY);
        inputFieldsListOptional.add(InputFields.ZIP);
        inputFieldsListOptional.add(InputFields.TELCC);
        inputFieldsListOptional.add(InputFields.TELNO);
        inputFieldsListOptional.add(InputFields.STREET);
        inputFieldsListOptional.add(InputFields.CUSTNAME);
        inputFieldsListOptional.add(InputFields.QUANTITY_TOTAL);
        return inputFieldsListOptional;
    }

    public List<InputFields>getMarketPlaceMandatoryParams()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.ORDERID);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getMarketPlaceOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.ORDERDESCRIPTION);
        return inputFieldsListOptional;
    }
    public List<InputFields>getMarketPlaceMandatoryParamsRest()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.ORDERID);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getMarketPlaceOptionalParamsRest()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.ORDERDESCRIPTION);
        return inputFieldsListOptional;
    }

    public String checkorderuniqueness(String toid, String fromtype, String description)
    {

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

            if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_qwipi";

            }
            else if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                transaction_table = "transaction_ecore";

            }
            else
            {
                transaction_table = "transaction_common";
            }

            transactionLogger.debug("InputValidatorUtils.OrderUniqueness ::: DB Call");
            String query2 = "select trackingid from " + transaction_table + " where toid = ? and description = ? order by dtstamp desc";
            PreparedStatement pstmt1 = con.prepareStatement(query2);
            pstmt1.setString(1, toid);
            pstmt1.setString(2, description);
            ResultSet rs1 = pstmt1.executeQuery();
            if (rs1.next())
            {
                str = "Your Transaction is already being processed. Kindly try to place transaction with unique orderId.";
            }
            //}
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

    public void getPartnerDetails(String toid,CommonValidatorVO commonValidatorVO)
    {

        Connection conn = null;
        try
        {
            conn = Database.getConnection();
            log.debug("Entering getMember Details");
            transactionLogger.debug("Entering getMember Details");
            String query = "SELECT partnerId,logoName,partnerName FROM partners WHERE partnerid = (SELECT partnerId FROM members WHERE memberid=?)";
            PreparedStatement pstmt= conn.prepareStatement(query);
            pstmt.setString(1,toid);
            ResultSet res = pstmt.executeQuery();

            if (res.next())
            {
                commonValidatorVO.setParetnerId(res.getString("partnerId"));
                commonValidatorVO.setPartnerName(res.getString("partnerName"));
                commonValidatorVO.setLogoName(res.getString("logoName"));
            }

        }
        catch (SQLException e)
        {
            log.error("Exception in getPartnerDetails",e);
            transactionLogger.error("Exception in getPartnerDetails",e);
        }
        catch (SystemError systemError)

        {
            log.error("Exception in getPartnerDetails",systemError);
            transactionLogger.error("Exception in getPartnerDetails",systemError);
        }
        finally {
            Database.closeConnection(conn);
        }
    }
    public List<InputFields> getRestSmsCodeMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        inputFieldsListMandatory.add(InputFields.SMS_CODE);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestPaymentAndCardTypeMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestSaveTransactionReceiptMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TRACKINGID);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestOthersBankMandatoryParametersValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAYMENT_PROVIDER);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestUPIMandatoryParametersValidation()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.VPA_ADDRESS);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestGetTransactionListMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestGetCardWhitelistMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID);
       // inputFieldsListMandatory.add(InputFields.CUSTOMERID);
        inputFieldsListMandatory.add(InputFields.EMAIL);
        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestGetTransactionListOptionalParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.STATUS);
        inputFieldsListMandatory.add(InputFields.FIRST_SIX);
        inputFieldsListMandatory.add(InputFields.LAST_FOUR);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        return inputFieldsListMandatory;
    }
    public List<InputFields> getRestMerchantLogoutParameters()
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();

        inputFieldsList.add(InputFields.AUTHTOKEN);
        inputFieldsList.add(InputFields.PARTNERID);


        return inputFieldsList;
    }

    public List<InputFields> getRestGetQueryFraudefenderMandatoryParameters()
    {
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.CURRENCY);
        inputFieldsListMandatory.add(InputFields.MERCHANT_ID);
        inputFieldsListMandatory.add(InputFields.TRANSACTION_DATE);
        inputFieldsListMandatory.add(InputFields.CALL_TYPE);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getRestGetQueryFraudefenderOptionalParameters()
    {

        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PURCHASE_IDENTIFIER);
        inputFieldsListOptional.add(InputFields.AUTHORIZATION_CODE);
        inputFieldsListOptional.add(InputFields.RRN);
        inputFieldsListOptional.add(InputFields.ARN);
        inputFieldsListOptional.add(InputFields.TRANS_TYPE);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestGetRefundFraudefenderMandatoryParameters()
    {

        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.CLIENT_TRANSACTION_ID);
        inputFieldsListMandatory.add(InputFields.REFUNDAMOUNT);
        inputFieldsListMandatory.add(InputFields.REFUND_CURRENCY);
        inputFieldsListMandatory.add(InputFields.CALL_TYPE);

        return inputFieldsListMandatory;
    }

    public List<InputFields> getInvoiceConfigDefaultUnitOptionalParams()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.DEFAULTUNIT);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestMerchantSendReceiptEmailParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.CHECKSUM);
        inputFieldsListOptional.add(InputFields.TRACKINGID);

        return inputFieldsListOptional;
    }

    public List<InputFields> getRestMerchantSendReceiptSmsParameters()
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.PHONENO);
        inputFieldsListOptional.add(InputFields.CHECKSUM);
        inputFieldsListOptional.add(InputFields.TRACKINGID);
        inputFieldsListOptional.add(InputFields.TELCC);

        return inputFieldsListOptional;
    }

    public List<InputFields> getGenerateOTP(CommonValidatorVO commonValidatorVO)
    {

        Functions functions = new Functions();
        String email = commonValidatorVO.getAddressDetailsVO().getEmail();
        String mobileNo = commonValidatorVO.getAddressDetailsVO().getPhone();

        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        if(!functions.isValueNull(mobileNo) && !functions.isValueNull(email))
        {
            inputFieldsListOptional.add(InputFields.TELNO);
            inputFieldsListOptional.add(InputFields.EMAILADDR);
        }

        if(functions.isValueNull(mobileNo))
            inputFieldsListOptional.add(InputFields.TELNO);

        if(functions.isValueNull(email))
            inputFieldsListOptional.add(InputFields.EMAILADDR);

        return inputFieldsListOptional;
    }


    public List<InputFields> validateParameter(CommonValidatorVO commonValidatorVO)
    {
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TOID);

        return inputFieldsListOptional;
    }
}