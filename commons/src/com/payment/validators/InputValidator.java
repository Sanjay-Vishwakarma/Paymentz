package com.payment.validators;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.AccountInfoVO;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.*;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 4/3/14
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class InputValidator implements PZValidator
{
    private static Logger log = new Logger(InputValidator.class.getName());

    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    private Functions functions = new Functions();

    /**
     * InputList will contain the list of Enums for  input fields
     * This method will return list of ValidationError if any
     *
     * @param req
     * @param inputList
     * @param validationErrorList
     */
    public void InputValidations(HttpServletRequest req, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {

        for (InputFields input : inputList)
        {
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TOID/Merchant Id", "Invalid TOID/Merchant Id :::" + req.getParameter(input.toString())));
                    break;
                case TOTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TOTYPE", "Invalid TOTYPE:::" + req.getParameter(input.toString())));
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Amount", 12, isOptional) || req.getParameter(input.toString()).length() > 12)
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount. It accepts Only Numeric value", "Invalid Amount :::" + req.getParameter(input.toString())));
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Id/Description", "Invalid Order Id/Description :::" + req.getParameter(input.toString())));
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 255, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderDescription", "Invalid OrderDescription :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Street", "Invalid Street :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_TELNOCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telno", "Invalid Telno :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Amount", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount", "Invalid Amount :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Currency", "Invalid Currency :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Country", "Invalid Country :::" + req.getParameter(input.toString())));
                    break;
                case INVOICE_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Invoice No", "Invalid Invoice No :::" + req.getParameter(input.toString())));
                    break;
                case STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid  start time", "Invalid start time :::" + req.getParameter(input.toString())));
                    break;
                case ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid end time", "Invalid end time :::" + req.getParameter(input.toString())));
                    break;

                //Add here
                case MID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid MID", "Invalid MID :::" + req.getParameter(input.toString())));
                    break;
                case OID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderId", "Invalid OrderId:::" + req.getParameter(input.toString())));
                    break;
                case TOTAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount Or Enter Amount with 2 Digit Decimal", "Invalid Amount :::" + req.getParameter(input.toString())));
                    break;
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CardType", "Invalid CardType :::" + req.getParameter(input.toString())));
                    break;
                case BITKEY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid BITKey", "Invalid BITKey :::" + req.getParameter(input.toString())));
                    break;
                case CHECKSUMALGORITHM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChecksumAlgoritham", "Invalid ChecksumAlgoritham :::" + req.getParameter(input.toString())));
                    break;
                case B_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case B_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + req.getParameter(input.toString())));
                    break;
                case B_ZIPCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + req.getParameter(input.toString())));
                    break;
                case B_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + req.getParameter(input.toString())));
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CountryCode", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CountryCode", "Invalid CountryCode :::" + req.getParameter(input.toString())));
                    break;
                case PHONE1:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone", "Invalid Phone :::" + req.getParameter(input.toString())));
                    break;
                case PHONE2:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone", "Invalid Phone :::" + req.getParameter(input.toString())));
                    break;
                case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentType", "Invalid PaymentType :::" + req.getParameter(input.toString())));
                    break;
                case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Terminal ID", "Invalid Terminal ID :::" + req.getParameter(input.toString())));
                    break;
                //Add here
                case TOID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TOID", "Invalid TOID :::" + req.getParameter(input.toString())));
                    break;
                case DESCRIPTION_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + req.getParameter(input.toString())));
                    break;
                case TRACKINGID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + req.getParameter(input.toString())));
                    break;
                case ACCOUNTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid AccountId", "Invalid AccountId :::" + req.getParameter(input.toString())));
                    break;
                //Add here
                case LANGUAGE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Language", "Invalid Language :::" + req.getParameter(input.toString())));
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Country", "Invalid Country :::" + req.getParameter(input.toString())));
                    break;
                case DAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Day", "Invalid Day :::" + req.getParameter(input.toString())));
                    break;
                case MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Month", "Invalid Month :::" + req.getParameter(input.toString())));
                    break;
                case YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Year", "Invalid Year :::" + req.getParameter(input.toString())));
                    break;
                case SSN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SSN", "Invalid SSN :::" + req.getParameter(input.toString())));
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid FirstName", "Invalid FirstName :::" + req.getParameter(input.toString())));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid LastName", "Invalid LastName :::" + req.getParameter(input.toString())));
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + req.getParameter(input.toString())));
                    break;
                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CC", 19, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PAN", "Invalid PAN :::" + req.getParameter(input.toString())));
                    break;
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CVV", "Invalid CVV :::" + req.getParameter(input.toString())));
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address/Street", "Invalid Address/Street :::" + req.getParameter(input.toString())));
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "City", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + req.getParameter(input.toString())));
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + req.getParameter(input.toString())));
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + req.getParameter(input.toString())));
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + req.getParameter(input.toString())));
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TelCC", "Invalid TelCC :::" + req.getParameter(input.toString())));
                    break;
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expire Month", "Invalid Expiry Month :::" + req.getParameter(input.toString())));
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expire Year", "Invalid Expire Year :::" + req.getParameter(input.toString())));
                    break;
                case NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + req.getParameter(input.toString())));
                    break;
                case CCCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CCP", "Invalid CCP :::" + req.getParameter(input.toString())));
                    break;
                case ADDRCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case CARDTYPE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cardtype", "Invalid Cardtype :::" + req.getParameter(input.toString())));
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + req.getParameter(input.toString())));
                    break;
                case TRACKING_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + req.getParameter(input.toString())));
                    break;
                case TRANS_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Transaction Status", "Invalid Transaction Status :::" + req.getParameter(input.toString())));
                    break;
                case CARDHOLDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cardholder", "Invalid Cardholder :::" + req.getParameter(input.toString())));
                    break;
                case NAME_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + req.getParameter(input.toString())));
                    break;
                case CARDNAME_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Holder Name", "Invalid Card Holder Name :::" + req.getParameter(input.toString())));
                    break;
                case STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Status", "Invalid Status :::" + req.getParameter(input.toString())));
                    break;
                case MESSAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Message", "Invalid Message :::" + req.getParameter(input.toString())));
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Checksum", "Invalid Checksum :::" + req.getParameter(input.toString())));
                    break;
                case TRACKING_ID_REF:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reference", "Invalid Reference :::" + req.getParameter(input.toString())));
                    break;
                case CHECKSUM_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Checksum", "Invalid Checksum :::" + req.getParameter(input.toString())));
                    break;
                case TMPL_COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CountryCode", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Country code", "Invalid Country code:::" + req.getParameter(input.toString())));
                    break;
                case ORDER_DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 255, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Description", "Invalid Order Description :::" + req.getParameter(input.toString())));
                    break;
                //Add here
                case USERNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "username", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid User Name", "Invalid User Name :::" + req.getParameter(input.toString())));
                    break;
                case PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + req.getParameter(input.toString())));
                    break;
                case COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "companyName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company Name", "Invalid Company Name :::" + req.getParameter(input.toString())));
                    break;
                case WL_INVOICE_COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "companyName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Organisation Name  For WL Invoice", "Invalid Organisation Name  For WL Invoice :::" + req.getParameter(input.toString())));
                    break;
                case CONTACT_PERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Contact_Person", "Invalid Contact_Person :::" + req.getParameter(input.toString())));
                    break;
                case CONTACT_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Email address or Email address field cannot be left blank", "Invalid Email address or Email address field cannot be left blank :::" + req.getParameter(input.toString())));
                    break;
                case CONTACT_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Contact_CC Email", "Invalid Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case SALES_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact_CC Email", "Invalid Sales Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case BILLING_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing Contact_CC Email", "Invalid Billing Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case NOTIFY_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notify Contact_CC Email", "Invalid Notify Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case FRAUD_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud Contact_CC Email", "Invalid Fraud Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case CB_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Contact_CC Email", "Invalid Chargeback Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case REFUND_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Contact_CC Email", "Invalid Refund Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case TECH_CCEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Technical Contact_CC Email", "Invalid Technical Contact_CC Email :::" + req.getParameter(input.toString())));
                    break;
                case PRIVACY_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Privacyurl", "Invalid Privacyurl :::" + req.getParameter(input.toString())));
                    break;
                case SITENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sitename", "Invalid Sitename :::" + req.getParameter(input.toString())));
                    break;
                case SUPPORT_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_Mail_Id", "Invalid Support_Mail_Id :::" + req.getParameter(input.toString())));
                    break;
                case ADMIN_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Admin_Mail_Id", "Invalid Admin_Mail_Id :::" + req.getParameter(input.toString())));
                    break;
                case SUPPORT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_URL", "Invalid Support_URL :::" + req.getParameter(input.toString())));
                    break;
                case DOCUMENTATION_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Documentation URL", "Invalid Documentation_URL :::" + req.getParameter(input.toString())));
                    break;
                case HOST_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "HostURL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Host URL", "Invalid Host URL :::" + req.getParameter(input.toString())));
                    break;
                case SALES_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales_Email", "Invalid Sales_Email :::" + req.getParameter(input.toString())));
                    break;
                case BILLING_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing_Email", "Invalid Billing_Email :::" + req.getParameter(input.toString())));
                    break;
                case NOTIFY_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notify_Email", "Invalid Notify_Email :::" + req.getParameter(input.toString())));
                    break;
                case COMPANY_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company_From_Address", "Invalid Company_From_Address :::" + req.getParameter(input.toString())));
                    break;
                case SUPPORT_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_From_Address", "Invalid Support_From_Address :::" + req.getParameter(input.toString())));
                    break;
                case SMTP_HOST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "smtphost", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_HOST", "Invalid SMTP_HOST :::" + req.getParameter(input.toString())));
                    break;
                case SMTP_PORT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_PORT", "Invalid SMTP_PORT :::" + req.getParameter(input.toString())));
                    break;
                case SMTP_USER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_USER", "Invalid SMTP_USER :::" + req.getParameter(input.toString())));
                    break;
                case SMTP_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_Password", "Invalid SMTP_Password :::" + req.getParameter(input.toString())));
                    break;
                case PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + req.getParameter(input.toString())));
                    break;
                case PID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Id", "Invalid Partner Id :::" + req.getParameter(input.toString())));
                    break;
                case RBIDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Recurring Billing Id", "Invalid Recurring Billing Id :::" + req.getParameter(input.toString())));
                    break;
                case TRACKINGS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + req.getParameter(input.toString())));
                    break;
                case FIRST_SIXS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six digit of Card", "Invalid First Six digit of Card :::" + req.getParameter(input.toString())));
                    break;
                case LAST_FOURS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four digit of Card", "Invalid Last Four digit of Card :::" + req.getParameter(input.toString())));
                    break;

                case PARTNERLIST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Id", "Invalid Partner Id :::" + req.getParameter(input.toString())));
                    break;
                case CARDNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CC", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Number", "Invalid Card Number :::" + req.getParameter(input.toString())));
                    break;
                case CVV_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CVV", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CVV", "Invalid CVV :::" + req.getParameter(input.toString())));
                    break;
                case EXPIRY_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expiry Month", "Invalid Expiry Month :::" + req.getParameter(input.toString())));
                    break;
                case EXPIRY_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expiry Year", "Invalid Expiry Year :::" + req.getParameter(input.toString())));
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 8, isOptional) || !Functions.isValidDate(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Birth Date", "Invalid Birth Date :::" + req.getParameter(input.toString())));
                    break;
                case LANGUAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Language", "Invalid Language :::" + req.getParameter(input.toString())));
                    break;
                case TRACKINGID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 140, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + req.getParameter(input.toString())));
                    break;
                case CUSTOMER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Id ", "Invalid Customer Id :::" + req.getParameter(input.toString())));
                    break;
                case CAPTUREAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + req.getParameter(input.toString())));
                    break;
                case REFUNDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Amount", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Amount", "Invalid Refund Amount :::" + req.getParameter(input.toString())));
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reason", "Invalid Reason :::" + req.getParameter(input.toString())));
                    break;

                case MIDDLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Middle Name", "Invalid Middle Name :::" + req.getParameter(input.toString())));
                    break;
                case IPADDRESS:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)|| ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid IP Address", "Invalid IP Address :::" + req.getParameter(input.toString())));
                    break;
                case MERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + req.getParameter(input.toString())));
                    break;
                case AGENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent ID", "Invalid Agent ID :::" + req.getParameter(input.toString())));
                    break;
                //validation starts for ICICI Module
                case MEMBERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Id", "Invalid Merchant Id :::" + req.getParameter(input.toString())));
                    break;
                case ACTIVATION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Statues", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Activation", "Invalid Activation :::" + req.getParameter(input.toString())));
                    break;
                case ICICI:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Statues", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ICICI", "Invalid ICICI :::" + req.getParameter(input.toString())));
                    break;
                case RESERVES:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reserves", "Invalid Reserves :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeper", "Invalid Chargeper :::" + req.getParameter(input.toString())));
                    break;
                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Days", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Date", "Invalid From Date :::" + req.getParameter(input.toString())));
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Days", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Date", "Invalid To Date :::" + req.getParameter(input.toString())));
                    break;
                case FROMMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Month", "Invalid From Month :::" + req.getParameter(input.toString())));
                    break;
                case TOMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Month", "Invalid To Month :::" + req.getParameter(input.toString())));
                    break;
                case TOYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Year", "Invalid To Year :::" + req.getParameter(input.toString())));
                    break;
                case FROMYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Year", "Invalid From Year :::" + req.getParameter(input.toString())));
                    break;
                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Page No", "Invalid Page No :::" + req.getParameter(input.toString())));
                    break;
                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Records", "Invalid Records :::" + req.getParameter(input.toString())));
                    break;
                case DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Domain", "Invalid Domain :::" + req.getParameter(input.toString())));
                    break;
                case NAME_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + req.getParameter(input.toString())));
                    break;
                case DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + req.getParameter(input.toString())));
                    break;
                case ORDERDESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 40, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Desc", "Invalid Order Desc :::" + req.getParameter(input.toString())));
                    break;
                case TRACKINGID_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + req.getParameter(input.toString())));
                    break;
                case GATEWAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway", "Invalid Gateway :::" + req.getParameter(input.toString())));
                    break;
                case GATEWAY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway", "Invalid Gateway Name:::" + req.getParameter(input.toString())));
                    break;
                case YEAR_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Year", "Invalid Year :::" + req.getParameter(input.toString())));
                    break;
                case MONTH_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Month", "Invalid Month :::" + req.getParameter(input.toString())));
                    break;
                case COMPANY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company", "Invalid Company :::" + req.getParameter(input.toString())));
                    break;
                case ACCOUNTID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid AccountID", "Invalid AccountID :::" + req.getParameter(input.toString())));
                    break;
                case FIRSTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six digit of Card Number", "Invalid First Six digit of Card Number :::" + req.getParameter(input.toString())));
                    break;
                case LASTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four digit of Card Number", "Invalid Last Four digit of Card Number :::" + req.getParameter(input.toString())));
                    break;
                case OLD_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Password", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Old Password", "Invalid Old Password :::" + req.getParameter(input.toString())));
                    break;
                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid New Password", "Invalid New Password :::" + req.getParameter(input.toString())));
                    break;
                case CONFIRM_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Confirm Password", "Invalid Confirm Password :::" + req.getParameter(input.toString())));
                    break;
                case MAILTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Mailtype", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Mail Type", "Invalid Mail Type :::" + req.getParameter(input.toString())));
                    break;
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Login", "Invalid Login :::" + req.getParameter(input.toString())));
                    break;
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderID", "Invalid OrderID :::" + req.getParameter(input.toString())));
                    break;
                case INVOICENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + req.getParameter(input.toString())));
                    break;
                case INV:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + req.getParameter(input.toString())));
                    break;
                case FROMID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fromid", "Invalid Fromid :::" + req.getParameter(input.toString())));
                    break;
                case PAYMENTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentNumber", "Invalid PaymentNumber :::" + req.getParameter(input.toString())));
                    break;
                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentId", "Invalid PaymentId :::" + req.getParameter(input.toString())));
                    break;
                case CCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Number", "Invalid Card Number :::" + req.getParameter(input.toString())));
                    break;
                case FIRST_SIX:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six digit of Card", "Invalid First Six digit of Card :::" + req.getParameter(input.toString())));
                    break;
                case LAST_FOUR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four digit of Card", "Invalid Last Four digit of Card :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargrId", "Invalid ChargeId :::" + req.getParameter(input.toString())));
                    break;
                case CHARGENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Name", "Invalid Charge Name :::" + req.getParameter(input.toString())));
                    break;
                case ISINPUTREQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Input Required", "Invalid Is Input Required :::" + req.getParameter(input.toString())));
                    break;
                case MAPPINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid MappingId", "Invalid MappingId :::" + req.getParameter(input.toString())));
                    break;
                case PAYMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Paymode", "Invalid Paymode :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargr Value", "Invalid Chargr Value :::" + req.getParameter(input.toString())));
                    break;
                case CHARGETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargr Type", "Invalid Charge Type :::" + req.getParameter(input.toString())));
                    break;
                case MIN_PAYOUT_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Payout Amount", "Invalid Min Payout Amount:::" + req.getParameter(input.toString())));
                    break;
                case MCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Charge Value", "Invalid Merchant Charge Value:::" + req.getParameter(input.toString())));
                    break;
                case ACHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Charge Value", "Invalid Agent Charge Value :::" + req.getParameter(input.toString())));
                    break;
                case PCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Charge Value", "Invalid Partner Charge Value :::" + req.getParameter(input.toString())));
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Currency", "Invalid Currency :::" + req.getParameter(input.toString())));
                    break;
                case PGTYPEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PayGatewayId", "Invalid PayGatewayId :::" + req.getParameter(input.toString())));
                    break;
                case ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Action", "Invalid Action :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEPERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargePercentage", "Invalid ChargePercentage :::" + req.getParameter(input.toString())));
                    break;
                case TAXPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TaxPercentage", "Invalid TaxPercentage :::" + req.getParameter(input.toString())));
                    break;
                case WITHDRAWCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid WITHDRAWCHARGE", "Invalid WITHDRAWCHARGE :::" + req.getParameter(input.toString())));
                    break;
                case REVERSECHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reversal Charge", "Invalid Reversal Charge :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEBACKCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CHARGEBACKCHARGE", "Invalid CHARGEBACKCHARGE :::" + req.getParameter(input.toString())));
                    break;
                case CHARGESACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CHARGESACCOUNT", "Invalid CHARGESACCOUNT :::" + req.getParameter(input.toString())));
                    break;
                case TAXACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TAXACCOUNT", "Invalid TAXACCOUNT :::" + req.getParameter(input.toString())));
                    break;
                case HIGHRISKAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid HIGHRISKAMOUNT", "Invalid HIGHRISKAMOUNT :::" + req.getParameter(input.toString())));
                    break;
                case ADDRESS_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case GATEWAY_TABLE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + req.getParameter(input.toString())));
                    break;
                case LOGONAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Logo Name", "Invalid Logo Name :::" + req.getParameter(input.toString())));
                    break;

                case ALIASNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Alias Name", "Invalid Alias Name :::" + req.getParameter(input.toString())));
                    break;
                case DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Display Name", "Invalid Display Name :::" + req.getParameter(input.toString())));
                    break;
                case ISMASTERCARDSUPPORTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Mastercard Supported", "Invalid Is Mastercard Supported :::" + req.getParameter(input.toString())));
                    break;
                case SHORTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Short Name", "Invalid Short Name :::" + req.getParameter(input.toString())));
                    break;
                case SITE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Site", "Invalid Site :::" + req.getParameter(input.toString())));
                    break;
                case PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Path", "Invalid Path :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEBACKPATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + req.getParameter(input.toString())));
                    break;
                case ISCVVREQUIRED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + req.getParameter(input.toString())));
                    break;
                case DAILYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case DAILYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + req.getParameter(input.toString())));
                    break;

                case DAILY_APPROVAL_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Approval Ratio", "Invalid Daily Approval Ratio :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_APPROVAL_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Approval Ratio", "Invalid Weekly Approval Ratio :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_APPROVAL_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Approval Ratio", "Invalid Monthly Approval Ratio:::" + req.getParameter(input.toString())));
                    break;

                case DAILY_CHARGEBACK_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Chargeback Ratio", "Invalid Daily Chargeback Ratio :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_CHARGEBACK_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Chargeback Ratio", "Invalid Weekly Chargeback Ratio :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_CHARGEBACK_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Chargeback Ratio", "Invalid Monthly Chargeback Ratio:::" + req.getParameter(input.toString())));
                    break;

                case DAILY_REFUND_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Refund Ratio", "Invalid Daily Refund Ratio :::" + req.getParameter(input.toString())));
                    break;

                case WEEKLY_REFUND_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Refund Ratio", "Invalid Weekly Refund Ratio :::" + req.getParameter(input.toString())));
                    break;

                case MONTHLY_REFUND_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Refund Ratio", "Invalid Monthly Refund Ratio:::" + req.getParameter(input.toString())));
                    break;

                case DAILY_REFUND_RATIO_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Refund Ratio Amount", "Invalid Daily Refund Ratio Amount :::" + req.getParameter(input.toString())));
                    break;

                case WEEKLY_REFUND_RATIO_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Refund Ratio Amount", "Invalid Weekly Refund Ratio Amount :::" + req.getParameter(input.toString())));
                    break;

                case MONTHLY_REFUND_RATIO_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Refund Ratio Amount", "Invalid Monthly Refund RatioAmount:::" + req.getParameter(input.toString())));
                    break;

                case DAILY_AVGTICKET_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Avergae Ticket Amount", "Invalid Daily Avergae Ticket Amount :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_AVGTICKET_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Avergae Ticket Amount", "Invalid Weekly Avergae Ticket Amount :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_AVGTICKET_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Avergae Ticket Amount", "Invalid Monthly Avergae Ticket Amount :::" + req.getParameter(input.toString())));
                    break;

                case MANUAL_CAPTURE_THRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Manual Capture Threshold", "Invalid Manual Capture Threshold :::" + req.getParameter(input.toString())));
                    break;

                case SAME_CARDAMOUNT_THRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Same Card Amount Threshold", "Invalid Same Caed Amount Threshold :::" + req.getParameter(input.toString())));
                    break;

                case DAILY_QUARTERLY_AVGTICKET_THRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Quarterly Avgticket Threshold", "Invalid Daily Quarterly Avgticket Threshold :::" + req.getParameter(input.toString())));
                    break;

                case CHARGEBACK_INCOUNT_FORALERT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Incount For Alert", "Invalid Chargeback Incount For Alert :::" + req.getParameter(input.toString())));
                    break;

                case AMOUNT_MONTHLY_CHARGEBACK_RATIO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid monthly cb ratio(amount)", "Invalid monthly cb ratio(amount)::::::::" + req.getParameter(input.toString())));
                    break;

                case WEEKLY_CHARGEBACK_RATIO_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid weekly cb ratio(amount)", "Invalid weekly cb ratio(amount)::::::::" + req.getParameter(input.toString())));
                    break;

                case DAILY_CHARGEBACK_RATIO_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid daily cb ratio(amount)", "Invalid daily cb ratio(amount)::::::::" + req.getParameter(input.toString())));
                    break;

                case MONTHLY_CREDIT_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid monthly credit amount", "Invalid monthly credit amount::::::::" + req.getParameter(input.toString())));
                    break;

                case INACTIVE_PERIOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 11, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Inactive Period", "Invalid  Inactive Period :::" + req.getParameter(input.toString())));
                    break;
                case FIRST_SUBMISSION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 11, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Submission", "Invalid First Submission :::" + req.getParameter(input.toString())));
                    break;

                case CHARGEBACK_INCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 11, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid chargeback allowed count", "Invalid chargeback allowed count:::" + req.getParameter(input.toString())));
                    break;

                case RESUME_PROCESSING_ALERT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 11, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Resume Processing Alert", "Invalid Resume Processing Alert:::" + req.getParameter(input.toString())));
                    break;

                case DAILY_AVGTICKET_PERCENTAGE_THRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 11, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Avgticket Percentage Threshold", "Invalid Daily Avgticket Percentage Threshold:::" + req.getParameter(input.toString())));
                    break;

                case MIN_TRANSACTION_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + req.getParameter(input.toString())));
                    break;
                case MAX_TRANSACTION_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + req.getParameter(input.toString())));
                    break;

                case MINTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + req.getParameter(input.toString())));
                    break;
                case MAXTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + req.getParameter(input.toString())));
                    break;
                case DAILYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;
                case COLUMN_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 250, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Column Name", "Invalid Column Name :::" + req.getParameter(input.toString())));
                    break;
                case GATEWAY_TABLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway Table Name", "Invalid Gateway Table Name :::" + req.getParameter(input.toString())));
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1055, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Redirect URL", "Invalid Redirect URL :::" + req.getParameter(input.toString())));
                    break;
                case NOTIFICATIONURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notification URL", "Invalid Notification URL :::" + req.getParameter(input.toString())));
                    break;
                case SEARCH_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Type", "Invalid Search Type :::" + req.getParameter(input.toString())));
                    break;
                case SEARCH_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Id", "Invalid Search Id :::" + req.getParameter(input.toString())));
                    break;
                case GATEWAY_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway Type", "Invalid Gateway Type :::" + req.getParameter(input.toString())));
                    break;
                case SDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + req.getParameter(input.toString())));
                    break;
                case STRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + req.getParameter(input.toString())));
                    break;
                case SCAPTUREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Capture Id", "Invalid Capture Id :::" + req.getParameter(input.toString())));
                    break;
                case ICICITRANSEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ICICI Transaction Id", "Invalid ICICI Transaction Id :::" + req.getParameter(input.toString())));
                    break;
                case REFUNDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Id", "Invalid Refund Id :::" + req.getParameter(input.toString())));
                    break;
                case REFUND_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Code", "Invalid Refund Code :::" + req.getParameter(input.toString())));
                    break;
                case REFUND_RECEIPT_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Receipt No", "Invalid Refund Receipt No :::" + req.getParameter(input.toString())));
                    break;
                case EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid Address :::" + req.getParameter(input.toString())));
                    break;
                case SEARCHTYPE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Type", "Invalid Search Type :::" + req.getParameter(input.toString())));
                    break;
                case DATE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "date", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Date", "Invalid Date:::" + req.getParameter(input.toString())));
                    break;
                case CB_REF_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Ref No", "Invalid CB Ref No :::" + req.getParameter(input.toString())));
                    break;
                case CB_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Amount", "Invalid CB Amount :::" + req.getParameter(input.toString())));
                    break;
                case CB_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Reason", "Invalid CB Reason :::" + req.getParameter(input.toString())));
                    break;
                case SEARCH_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Status", "Invalid Search Status :::" + req.getParameter(input.toString())));
                    break;
                case AGENT_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Id", "Invalid Agent Id :::" + req.getParameter(input.toString())));
                    break;
                case AGENT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Name", "Invalid Agent Name :::" + req.getParameter(input.toString())));
                    break;
                case PARTNER_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Name", "Invalid Partner Name :::" + req.getParameter(input.toString())));
                    break;
                case PASSWORD_FULL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Password", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + req.getParameter(input.toString())));
                    break;
                case MERCHANTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + req.getParameter(input.toString())));
                    break;
                case DAILY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Amount limit", "Invalid Weekly Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case DAILY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + req.getParameter(input.toString())));
                    break;
                case DAILY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + req.getParameter(input.toString())));
                    break;

                case DAILY_AVG_TICKET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Avg Ticket", "Invalid Daily Avg Ticket :::" + req.getParameter(input.toString())));
                    break;
                case WEEKLY_AVG_TICKET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Avg Ticket", "Invalid Weekly Avg Ticket :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_AVG_TICKET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Avg Ticket", "Invalid Monthly Avg Ticket :::" + req.getParameter(input.toString())));
                    break;
                case MIN_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + req.getParameter(input.toString())));
                    break;
                case MAX_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + req.getParameter(input.toString())));
                    break;
                case IS_ACTIVE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Active", "Invalid Is Active :::" + req.getParameter(input.toString())));
                    break;
                case PRIORITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Priority", "Invalid Priority :::" + req.getParameter(input.toString())));
                    break;
                case IS_TEST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Test", "Invalid Is Test :::" + req.getParameter(input.toString())));
                    break;
                case CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 7, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Percentage", "Invalid Charge Percentage :::" + req.getParameter(input.toString())));
                    break;
                case FIX_APPROVE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fix Approve Charge", "Invalid Fix Approve Charge :::" + req.getParameter(input.toString())));
                    break;
                case FIX_DECLINE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fix Decline Charge", "Invalid Fix Decline Charge :::" + req.getParameter(input.toString())));
                    break;
                case TAX_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tax Percentage", "Invalid Tax Percentage :::" + req.getParameter(input.toString())));
                    break;
                case REVERSE_PERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reverse Percentage", "Invalid Reverse Percentage :::" + req.getParameter(input.toString())));
                    break;
                case FRAUDE_VARIFICATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraude Varification Charge", "Invalid Fraude Varification Charge :::" + req.getParameter(input.toString())));
                    break;
                case ANNUAL_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Annual Charge", "Invalid Annual Charge :::" + req.getParameter(input.toString())));
                    break;
                case SETUP_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Setup Charge", "Invalid Setup Charge :::" + req.getParameter(input.toString())));
                    break;
                case FX_CLERANCE_CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Forex Clearance Charge Percentage", "Invalid Forex Clearance Charge Percentage :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_GATEWAY_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Gateway Charge", "Invalid Monthly Gateway Charge :::" + req.getParameter(input.toString())));
                    break;
                case MONTHLY_ACC_MN_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Account Mnt Charge", "Invalid Monthly Account Mnt Charge :::" + req.getParameter(input.toString())));
                    break;
                case REPORT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Report Charge", "Invalid Report Charge :::" + req.getParameter(input.toString())));
                    break;
                case FRAUDULENT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraudulent Charge", "Invalid Fraudulent Charge :::" + req.getParameter(input.toString())));
                    break;
                case AUTO_REPRESENTATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Auto Representation Charge", "Invalid Auto Representation Charge :::" + req.getParameter(input.toString())));
                    break;
                case INTERCHANGE_PLUS_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Interchange Plus Charge", "Invalid Interchange Plus Charge :::" + req.getParameter(input.toString())));
                    break;
                case OLD_DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Old Display Name", "Invalid Display Name :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEBACK_PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + req.getParameter(input.toString())));
                    break;
                case ISCVV_REQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + req.getParameter(input.toString())));
                    break;
                case SEARCH_DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Domain", "Invalid Domain :::" + req.getParameter(input.toString())));
                    break;
                case SEARCH_WORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Word", "Invalid Search Word :::" + req.getParameter(input.toString())));
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid IP Address", "Invalid IP Address :::" + req.getParameter(input.toString())));
                    break;

                case CUSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid customer Name", "Invalid customer Name :::" + req.getParameter(input.toString())));
                    break;
                case INVOICE_CANCEL_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cancel Reason", "Invalid Cancel Reason :::" + req.getParameter(input.toString())));
                    break;
                case MINUS_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount", "Invalid Amount :::" + req.getParameter(input.toString())));
                    break;
                case PASSWORD2:
                    if (!ESAPI.validator().isValidInput(input.toString(), (String) req.getAttribute(input.toString()), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + req.getAttribute(input.toString())));
                    break;
                case DATASOURCE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Datasource", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Datasource", "Invalid Datasource :::" + req.getParameter(input.toString())));
                    break;

                case SHIPPINGPODBATCH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CSEURL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Shipping Batch", "INVALID Shipping Batch :::" + req.getParameter(input.toString())));
                    break;

                case SHIPPINGPOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Shipping Id", "INVALID Shiping Id :::" + req.getParameter(input.toString())));
                    break;

                case PHONENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone Number", "Invalid Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case CSEEid:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CSEid", "Invalid CSEid:::" + req.getParameter(input.toString())));
                    break;

                case CSEname:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CSEname", "Invalid CSEname:::" + req.getParameter(input.toString())));
                    break;

                case LASTFOURCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "LastFourcc", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four CC No", "Invalid Last Four CC No:::" + req.getParameter(input.toString())));
                    break;
                case FIRSTSIXCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "FirstSixcc", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six CC No", "Invalid First Six CC No:::" + req.getParameter(input.toString())));
                    break;
                case LASTFOUR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "LastFourcc", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four CC No", "Invalid Last Four CC No:::" + req.getParameter(input.toString())));
                    break;
                case FIRSTSIX:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "FirstSixcc", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six CC No", "Invalid First Six CC No:::" + req.getParameter(input.toString())));
                    break;
                case BIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "FirstSixcc", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bin No", "Invalid Bin No:::" + req.getParameter(input.toString())));
                    break;

                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Holder IpAddress", "Invalid Card Holder IpAddress:::" + req.getParameter(input.toString())));
                    break;
                case BRANDNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Brandname", "Invalid Brandname:::" + req.getParameter(input.toString())));
                    break;

                case PAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isPaid", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isPaid", "Invalid isPaid:::" + req.getParameter(input.toString())));
                    break;

                case FDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid from date", "Invalid from date:::" + req.getParameter(input.toString())));
                    break;

                case TDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "toDate", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid to date", "Invalid to date:::" + req.getParameter(input.toString())));
                    break;

                case COMMA_SEPRATED_NUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CommaSeprateNum", 500, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking ID", "Invalid Tracking ID:::" + req.getParameter(input.toString())));
                    break;

                case FILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fileName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid File Name", "Invalid File Name:::" + req.getParameter(input.toString())));
                    break;

                case SMALL_ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Action", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Action", "Invalid Action:::" + req.getParameter(input.toString())));
                    break;

                case BANKRECEIVEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank SettlementCycle Id ", "Invalid Bank SettlementCycle Id:::" + req.getParameter(input.toString())));
                    break;

                case SETTLEMENTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Date", "Invalid Settlement Date:::" + req.getParameter(input.toString())));
                    break;

                case EXPECTED_STARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected Start Date", "Invalid Expected Start Date:::" + req.getParameter(input.toString())));
                    break;

                case EXPECTED_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected End Date", "Invalid Expected End Date:::" + req.getParameter(input.toString())));
                    break;

                case ACTUAL_SARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual Start Date", "Invalid Actual Start Date:::" + req.getParameter(input.toString())));
                    break;

                case ACTUAL_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual End Date", "Invalid Actual End Date:::" + req.getParameter(input.toString())));
                    break;

                case SETTLEMENTCYCLEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement cycle id", "Invalid Settlement cycle id:::" + req.getParameter(input.toString())));
                    break;

                case ROLLINGRESERVEDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rolling Release Date", "Invalid Rolling Release Date:::" + req.getParameter(input.toString())));
                    break;

                case ISSETTLEMENTCRONEXECCUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isSettlement cron executed ", "Invalid isSettlement cron executed:::" + req.getParameter(input.toString())));
                    break;

                case ISPAYOUCRONEXECUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isPayout cron executed ", "Invalid isPayout cron executed:::" + req.getParameter(input.toString())));
                    break;

                case ISDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Day Light ", "Invalid Day Light:::" + req.getParameter(input.toString())));
                    break;

                case ISPAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Paid ", "Invalid Is Paid:::" + req.getParameter(input.toString())));
                    break;

                case BANKMERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank merchant settlement id ", "Invalid Bank merchant settlement id:::" + req.getParameter(input.toString())));
                    break;
                case FSID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid FraudSystem Id", "FraudSystem Id :::" + req.getParameter(input.toString())));
                    break;
                case FSNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud System Name", "Invalid Fraud System Name :::" + req.getParameter(input.toString())));
                    break;

                case BANKWIREMANGERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Wire Manager Id", "Invalid Bank Wire Manager Id :::" + req.getParameter(input.toString())));
                    break;
                case PARENT_BANKWIREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Parent Bank Wire Id", "Invalid Parent Wire Manager Id :::" + req.getParameter(input.toString())));
                    break;
                case PROCESSINGAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Processing Amount", "Invalid Processing Amount :::" + req.getParameter(input.toString())));
                    break;
                case GROSSAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gross Amount", "Invalid Gross Amount :::" + req.getParameter(input.toString())));
                    break;
                case NETFINALAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Net Final Amount", "Invalid Net Final Amount :::" + req.getParameter(input.toString())));
                    break;
                case UNPAIDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Unpaid Amount", "Invalid Unpaid Amount :::" + req.getParameter(input.toString())));
                    break;
                case ISROLLINGRESERVERELAEASEWIRE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isRollingReserveReleaseWire", "Invalid isRollingReserveReleaseWire :::" + req.getParameter(input.toString())));
                    break;
                case DECLINEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid DeclinedCoveredDateUpto", "Invalid DeclinedCoveredDateUpto :::" + req.getParameter(input.toString())));
                    break;
                case CHARGEBACKCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargebackCoveredDateUpto", "Invalid ChargebackCoveredDateUpto :::" + req.getParameter(input.toString())));
                    break;
                case REVERSEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ReversedCoveredDateUpto", "Invalid ReversedCoveredDateUpto :::" + req.getParameter(input.toString())));
                    break;
                case SETTLEMENTREPORTFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "reportFile", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Report File", "Invalid Settlement Report File :::" + req.getParameter(input.toString())));
                    break;
                case SETTLEMENTTRANSACTIONFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "transactionFile", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Transaction File", "Invalid Settlement Transaction File :::" + req.getParameter(input.toString())));
                    break;
                case EXPECTED_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected start time", "Invalid Expected start time :::" + req.getParameter(input.toString())));
                    break;
                case EXPECTED_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected end time", "Invalid Expected end time :::" + req.getParameter(input.toString())));
                    break;
                case ACTUAL_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual start time", "Invalid Actual start time :::" + req.getParameter(input.toString())));
                    break;
                case ACTUAL_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual end time", "Invalid Actual end time :::" + req.getParameter(input.toString())));
                    break;
                case ROLLINGRELEASETIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rolling Release Time", "Invalid Rolling Release Time :::" + req.getParameter(input.toString())));
                    break;
                case DECLINECOVERTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Decline Covered Time", "Invalid Decline Coversed Time :::" + req.getParameter(input.toString())));
                    break;

                case CHARGEBACKCOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Covered Time", "Invalid Chargeback Coversed Time :::" + req.getParameter(input.toString())));
                    break;

                case REVERSECOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reverse Covered Time", "Invalid Reverse Coversed Time :::" + req.getParameter(input.toString())));
                    break;

                case TIMEDIFFNORMAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 16, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Time difference Normal", "Invalid Time difference Normal:::" + req.getParameter(input.toString())));
                    break;

                case TIMEDIFFDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 16, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Time diffence DayLight", "Invalid Time diffence DayLight:::" + req.getParameter(input.toString())));
                    break;

                case SETTLEMENTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Time", "Invalid Settlement Time:::" + req.getParameter(input.toString())));
                    break;
                case HIGH_RISK_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid High Risk Amount", "Invalid High Risk Amount:::" + req.getParameter(input.toString())));
                    break;
                case MAX_SCORE_ALLOWED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max score Allowed", "Invalid Max score Allowed:::" + req.getParameter(input.toString())));
                    break;
                case MAX_SCORE_REVERSEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max score Reversal", "Invalid Max score Reversal:::" + req.getParameter(input.toString())));
                    break;
                case REFUND_DAILY_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund daily Limit", "Invalid Refund daily Limit:::" + req.getParameter(input.toString())));
                    break;
                case CHARGETECHNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Tech Name", "Invalid Charge Tech Name:::" + req.getParameter(input.toString())));
                    break;

                case FRAUDEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid fraud email", "Invalid fraud email:::" + req.getParameter(input.toString())));
                    break;
                case IDEAL_BANK_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank code", "Invalid Bank code:::" + req.getParameter(input.toString())));
                    break;
                case BANKIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank IP Address", "Invalid Bank IP Address:::" + req.getParameter(input.toString())));
                    break;
                case APPTOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Application", "Invalid Application:::" + req.getParameter(input.toString())));
                    break;

                case BANK_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank", "Invalid Bank:::" + req.getParameter(input.toString())));
                    break;

                case ARCHIVE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Archive", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Archive", "Invalid Archive:::" + req.getParameter(input.toString())));
                    break;
                case RBID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Recurring Billing Id", "Invalid Recurring Billing Id:::" + req.getParameter(input.toString())));
                    break;
                case RESERVEFIELD1:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "recurring", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28)", "Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28):::" + req.getParameter(input.toString())));
                    break;

                case TOKENVALIDDAYS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Token Valid Days", "Invalid Token Valid Days:::" + req.getParameter(input.toString())));
                    break;

                case RISK_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Name", "Invalid Risk Rule Name:::" + req.getParameter(input.toString())));
                    break;

                case RISK_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Label", "Invalid Risk Rule Label:::" + req.getParameter(input.toString())));
                    break;

                case RISK_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Description", "Invalid Risk Rule Description:::" + req.getParameter(input.toString())));
                    break;

                case BUSINESS_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Name", "Invalid Business Rule Name:::" + req.getParameter(input.toString())));
                    break;

                case BUSINESS_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Label", "Invalid Business Rule Label:::" + req.getParameter(input.toString())));
                    break;

                case BUSINESS_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Description", "Invalid Business Rule Description:::" + req.getParameter(input.toString())));
                    break;

                case BUSINESS_RULE_OPERATOR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Operator", "Invalid Business Rule Operator:::" + req.getParameter(input.toString())));
                    break;

                case Bank_EmailID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Email ID", "Invalid Bank Email ID:::" + req.getParameter(input.toString())));
                    break;

                case RISKPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile", "Invalid Risk Profile:::" + req.getParameter(input.toString())));

                    break;

                case RISKPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile Name", "Invalid Risk Profile Name:::" + req.getParameter(input.toString())));

                    break;

                case RISKRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Id", "Invalid Risk Rule Id:::" + req.getParameter(input.toString())));

                    break;

                case BUSINESSRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Id", "Invalid Business Rule Id:::" + req.getParameter(input.toString())));

                    break;

                case BUSINESSPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Profile Id", "Invalid Business Profile Id:::" + req.getParameter(input.toString())));

                    break;

                case BUSINESSPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Profile Name", "Invalid Business Profile Name:::" + req.getParameter(input.toString())));

                    break;

                case COUNTOFROW:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile Row ", "Invalid Risk Profile Row:::" + req.getParameter(input.toString())));

                    break;

                case USERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid User Profile Id", "Invalid User Profile Id:::" + req.getParameter(input.toString())));

                    break;

                case ONLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Online Processing URL", "Invalid Online Processing URL::::" + req.getParameter(input.toString())));

                    break;
                case OFFLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Offline Processing URL", "Invalid Offline Processing URL:::" + req.getParameter(input.toString())));

                    break;
                case ONLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Online Risk Threshold", "Invalid Online Risk Threshold:::" + req.getParameter(input.toString())));

                    break;
                case OFFLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Offline Risk Threshold", "Invalid Offline Risk Threshold:::" + req.getParameter(input.toString())));

                    break;
                case DEFAULTMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Default Mode", "Invalid Default Mode:::" + req.getParameter(input.toString())));

                    break;
                case BUSINESSPROFILE_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Profile", "Invalid Business Profile:::" + req.getParameter(input.toString())));

                    break;
                case BACKGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Background color", "Invalid Background color:::" + req.getParameter(input.toString())));

                    break;
                case FOREGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Foreground color", "Invalid Foreground color:::" + req.getParameter(input.toString())));

                    break;
                case FONT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Font color", "Invalid Font color:::" + req.getParameter(input.toString())));

                    break;
                case MERCHANTLOGO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Logo", "Invalid Merchant Logo:::" + req.getParameter(input.toString())));

                    break;

                case CONSOLIDATEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CONSOLIDATED ID", "Invalid CONSOLIDATED ID:::" + req.getParameter(input.toString())));

                    break;

                case BUSINESS_RULE_OPERATOR_QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid Business Rule Operator","Invalid Business Rule Operator:::"+req.getParameter(input.toString())));
                    break;

                case RULETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rule Type","Invalid Rule Type:::"+req.getParameter(input.toString())));

                    break;

                case QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 1000, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Query","Invalid Query:::"+req.getParameter(input.toString())));

                    break;

                case REGEX:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Regular Expression","Invalid Regular Expression:::"+req.getParameter(input.toString())));

                    break;

                case SALES_CONTACTPERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact Name", "Invalid Sales Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case BILLING_CONTACTPERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid  Billing Contact Name", "Invalid Billing Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case FRAUD_CONTACTPERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid  Fraud Contact Name ", "Invalid Fraud Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case NOTIFY_CONTACTPERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notify Contact Name", "Invalid Notify Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case MAINCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Main Contact Cc Email ID ", "Invalid Main Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case MAINCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Main Contact Phone Number", "Invalid Main Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case CBCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Contact Name ", "Invalid Chargeback Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case CBCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Contact Email ID ", "Invalid Chargeback Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case CBCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Contact Cc Email ID ", "Invalid Chargeback Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case CBCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Contact Phone Number", "Invalid Chargeback Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case REFUNDCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Contact Name ", "Invalid Refund Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case REFUNDCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Contact Email ID ", "Invalid Refund Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case REFUNDCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Contact Cc Email ID ", "Invalid Refund Contct Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case REFUNDCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Contact Phone Number", "Invalid Refund Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case SALESCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact Name ", "Invalid Sales Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case SALESCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact Email ID ", "Invalid Sales Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case SALESCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact Cc Email ID ", "Invalid Sales Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case SALESCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales Contact Phone Number", "Invalid Sales Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case BILLINGCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing Contact Name ", "Invalid Billing Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case BILLINGCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing Contact Email ID ", "Invalid Billing Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case BILLINGCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing Contact Cc Email ID ", "Invalid Billing Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case BILLINGCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing Contact Phone Number", "Invalid Billing Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case TECHCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Technical Contact Name ", "Invalid Technical Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case TECHCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Technical Contact Email ID ", "Invalid Technical Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case TECHCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Technical Contact Cc Email ID ", "Invalid Technical Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case TECHCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Technical Contact Phone Number", "Invalid Technical Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case FRAUDCONTACT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud Contact Name ", "Invalid Fraud Contact Name :::" + req.getParameter(input.toString())));
                    break;

                case FRAUDCONTACT_MAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud Contact Email ID ", "Invalid Fraud Contact Email ID  :::" + req.getParameter(input.toString())));
                    break;

                case FRAUDCONTACT_CC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud Contact Cc Email ID ", "Invalid Fraud Contact Cc Email ID :::" + req.getParameter(input.toString())));
                    break;

                case FRAUDCONTACT_PHONE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud Contact Phone Number", "Invalid Fraud Contact Phone Number:::" + req.getParameter(input.toString())));
                    break;

                case PROCESSOR_PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Number", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Processor Partner Id", "Invalid Processor Partner Id:::" + req.getParameter(input.toString())));
                    break;
                case VPA_ADDRESS_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 9, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid VPA Address Daily Count", "Invalid VPA Address Daily Count:::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_IP_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 9, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Ip Daily Count", "Invalid Customer Ip Daily Count:::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_NAME_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 9, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Name Daily Count", "Invalid Customer Name Daily Count:::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_EMAIL_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 9, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Email Daily Count", "Invalid Customer Email Daily Count:::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_PHONE_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 9, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Phone Daily Count", "Invalid Customer Phone Daily Count:::" + req.getParameter(input.toString())));
                    break;

                case VPA_ADDRESS_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid VPA Address Daily Amount limit", "Invalid VPA Address Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_IP_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Ip Daily Amount limit", "Invalid Customer Ip Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_NAME_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Name Daily Amount limit", "Invalid Customer Name Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_EMAIL_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Email Daily Amount limit", "Invalid Customer Email Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_PHONE_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Phone Daily Amount limit", "Invalid Customer Phone Daily Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case VPA_ADDRESS_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid VPA Address Monthly Count", "Invalid VPA Address Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case VPA_ADDRESS_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid VPA Address Monthly Amount limit", "Invalid VPA Address Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_EMAIL_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Email Monthly Count", "Invalid Customer Email Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_EMAIL_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Email Monthly Amount limit", "Invalid Customer Email Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_PHONE_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Phone Monthly Count", "Invalid Customer Phone Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_PHONE_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Phone Monthly Amount limit", "Invalid Customer Phone Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case BANK_ACCOUNT_NO_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank AccountNo Monthly Count", "Invalid Bank AccountNo Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case BANK_ACCOUNT_NO_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank AccountNo Monthly Amount limit", "Invalid Bank AccountNo Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;

                case BANK_ACCOUNT_NO_DAILY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank AccountNo Daily Amount Limit", "Invalid Bank AccountNo Daily Amount Limit :::" +req.getParameter(input.toString())));
                    break;

                case BANK_ACCOUNT_NO_DAILY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank AccountNo Daily Count", "Invalid Bank AccountNo Daily Count :::" +req.getParameter(input.toString())));
                    break;
                case CUSTOMER_IP_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Ip Monthly Count", "Invalid Customer Ip Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_IP_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Ip Monthly Amount limit", "Invalid Customer Ip Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;


                case CUSTOMER_NAME_MONTHLY_COUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Name Monthly Count", "Invalid Customer Name Monthly Count :::" + req.getParameter(input.toString())));
                    break;

                case CUSTOMER_NAME_MONTHLY_AMOUNT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Name Monthly Amount limit", "Invalid Customer Name Monthly Amount limit :::" + req.getParameter(input.toString())));
                    break;
                case BANK_ACCOUNT_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Account Number", "Invalid Bank Account Number :::" + req.getParameter(input.toString())));
                    break;


                default:
                    break;
            }
        }
    }

    /**
     * InputList will contain the list of Enums for  input fields
     * This method will throw ValidationException in case of validation failure
     *
     * @param req
     * @param inputList
     * @throws ValidationException
     */
    public void InputValidations(HttpServletRequest req, List<InputFields> inputList, boolean isOptional) throws ValidationException
    {
        for (InputFields input : inputList)
        {

            switch (input)
            {

                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid TOID/Merchant Id", "Invalid TOID/Merchant Id:::" + req.getParameter(input.toString()));
                    break;
                case TOTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid TOTYPE", "Invalid TOTYPE:::" + req.getParameter(input.toString()));
                    break;
                case CARD_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 30, isOptional))
                        throw new ValidationException("Invalid CARD TYPE", "Invalid CARD TYPE:::" + req.getParameter(input.toString()));
                    break;
                case CARD_ISSUING_BANK:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid ISSUING BANK NAME", "Invalid ISSUING BANK NAME:::" + req.getParameter(input.toString()));
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Amount", 12, isOptional))
                        throw new ValidationException("Invalid Amount. It accepts Only Numeric value", "Invalid Amount:::" + req.getParameter(input.toString()));
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional))
                        throw new ValidationException("Invalid Order Id/Description", "Invalid Order Id/Description:::" + req.getParameter(input.toString()));
                    break;
                case STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid  start time", "Invalid start time :::" + req.getParameter(input.toString()));
                    break;
                case ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid end time", "Invalid end time :::" + req.getParameter(input.toString()));
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 255, isOptional) || functions.hasHTMLTags(req.getParameter(req.getParameter(input.toString()))))
                        throw new ValidationException("Invalid OrderDescription", "Invalid OrderDescription:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Street", "Invalid Street:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Invalid Zip:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 15, isOptional))
                        throw new ValidationException("Invalid Telephone Number", "Invalid Telephone Number:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_TELNOCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 4, isOptional))
                        throw new ValidationException("Invalid TelCC", "Invalid TelCC:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Amount", 12, isOptional))
                        throw new ValidationException("Invalid Amount", "Invalid Amount:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 3, isOptional))
                        throw new ValidationException("Invalid Currency", "Invalid Currency:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Country", "Invalid Country:::" + req.getParameter(input.toString()));
                    break;
                case INVOICE_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Invoice No", "Invalid Invoice No:::" + req.getParameter(input.toString()));
                    break;
                //Add here
                case MID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 30, isOptional))
                        throw new ValidationException("Invalid MID", "Invalid MID:::" + req.getParameter(input.toString()));
                    break;
                case OID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 30, isOptional))
                        throw new ValidationException("Invalid OrderId", "Invalid OrderId :::" + req.getParameter(input.toString()));
                    break;
                case TOTAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 10, isOptional))
                        throw new ValidationException("Invalid Amount Or Enter Amount with 2 Digit Decimal", "Invalid Amount :::" + req.getParameter(input.toString()));
                    break;
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 12, isOptional))
                        throw new ValidationException("Invalid Cardtype", "Invalid Cardtype:::" + req.getParameter(input.toString()));
                    break;
                case BITKEY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid BITKey", "Invalid BITKey:::" + req.getParameter(input.toString()));
                    break;
                case CHECKSUMALGORITHM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Checksum Algorithm", "Invalid Checksum Algoritham:::" + req.getParameter(input.toString()));
                    break;
                case B_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address:::" + req.getParameter(input.toString()));
                    break;
                case B_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City:::" + req.getParameter(input.toString()));
                    break;
                case B_ZIPCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Zip:::" + req.getParameter(input.toString()));
                    break;
                case B_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State:::" + req.getParameter(input.toString()));
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CountryCode", 3, isOptional))
                        throw new ValidationException("Invalid Country code", "Invalid Country code:::" + req.getParameter(input.toString()));
                    break;
                case PHONE1:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 30, isOptional))
                        throw new ValidationException("Invalid Phone", "Invalid Phone:::" + req.getParameter(input.toString()));
                    break;
                case PHONE2:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 30, isOptional))
                        throw new ValidationException("Invalid Phone", "Invalid Phone:::" + req.getParameter(input.toString()));
                    break;
                case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 2, isOptional))
                        throw new ValidationException("Invalid Paymenttype", "Invalid Paymenttype:::" + req.getParameter(input.toString()));
                    break;
                case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 6, isOptional))
                        throw new ValidationException("Invalid Terminal ID", "Invalid Terminal ID:::" + req.getParameter(input.toString()));
                    break;

                //Add here
                case TOID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid TOID", "Invalid TOID:::" + req.getParameter(input.toString()));
                    break;
                case DESCRIPTION_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description:::" + req.getParameter(input.toString()));
                    break;
                case TRACKINGID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId:::" + req.getParameter(input.toString()));
                    break;
                case ACCOUNTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid AccountId", "Invalid AccountId:::" + req.getParameter(input.toString()));
                    break;
                //Add here
                case LANGUAGE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Language", "Invalid Language:::" + req.getParameter(input.toString()));
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 30, isOptional))
                        throw new ValidationException("Invalid Country", "Invalid Country:::" + req.getParameter(input.toString()));
                    break;
                case DAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 2, isOptional))
                        throw new ValidationException("Invalid Day", "Invalid Day:::" + req.getParameter(input.toString()));
                    break;
                case MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 2, isOptional))
                        throw new ValidationException("Invalid Month", "Invalid Month:::" + req.getParameter(input.toString()));
                    break;
                case YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid Year", "Invalid Year:::" + req.getParameter(input.toString()));
                    break;
                case SSN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid SSN", "Invalid SSN:::" + req.getParameter(input.toString()));
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid FirstName", "Invalid FirstName:::" + req.getParameter(input.toString()));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid LastName", "Invalid LastName:::" + req.getParameter(input.toString()));
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress:::" + req.getParameter(input.toString()));
                    break;
                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CC", 19, isOptional))
                        throw new ValidationException("Invalid PAN", "Invalid PAN:::" + req.getParameter(input.toString()));
                    break;
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid CVV", "Invalid CVV:::" + req.getParameter(input.toString()));
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Address/Street", "Invalid Address/Street:::" + req.getParameter(input.toString()));
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "City", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City:::" + req.getParameter(input.toString()));
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State:::" + req.getParameter(input.toString()));
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Invalid Zip:::" + req.getParameter(input.toString()));
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 15, isOptional))
                        throw new ValidationException("Invalid Phone Number", "Invalid Phone Number:::" + req.getParameter(input.toString()));
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 4, isOptional))
                        throw new ValidationException("Invalid Phone CC", "Invalid Phone CC:::" + req.getParameter(input.toString()));
                    break;
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Expiry Month", "Invalid Expiry Month:::" + req.getParameter(input.toString()));
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Expiry Year", "Invalid Expiry Year:::" + req.getParameter(input.toString()));
                    break;
                case NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name:::" + req.getParameter(input.toString()));
                    break;
                case CCCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid CCCP", "Invalid CCCP:::" + req.getParameter(input.toString()));
                    break;
                case ADDRCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address:::" + req.getParameter(input.toString()));
                    break;
                case CARDTYPE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 12, isOptional))
                        throw new ValidationException("Invalid Cardtype", "Invalid Cardtype:::" + req.getParameter(input.toString()));
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId:::" + req.getParameter(input.toString()));
                    break;
                case TRACKING_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Tracking_Id", "Invalid Tracking_Id:::" + req.getParameter(input.toString()));
                    break;
                case TRANS_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Status", "Invalid Status:::" + req.getParameter(input.toString()));
                    break;
                case CARDHOLDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Cardholder", "Invalid Cardholder:::" + req.getParameter(input.toString()));
                    break;
                case NAME_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name:::" + req.getParameter(input.toString()));
                    break;
                case STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 100, isOptional))
                        throw new ValidationException("Invalid Stutas", "Invalid Status:::" + req.getParameter(input.toString()));
                    break;

                case STATUS_LIST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Status", "Invalid Status:::" + req.getParameter(input.toString()));
                    break;

                case MESSAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Message", "Invalid Message:::" + req.getParameter(input.toString()));
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Checksum", "Invalid Checksum:::" + req.getParameter(input.toString()));
                    break;
                case TRACKING_ID_REF:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid TrackingIdRef", "Invalid TrackingIdRef:::" + req.getParameter(input.toString()));
                    break;
                case CHECKSUM_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Checksum", "Invalid Checksum:::" + req.getParameter(input.toString()));
                    break;
                case TMPL_COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CountryCode", 3, isOptional))
                        throw new ValidationException("Invalid CountryCode", "Invalid Countrycode:::" + req.getParameter(input.toString()));
                    break;
                case ORDER_DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 255, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid OrderDescription", "Invalid OrderDescription:::" + req.getParameter(input.toString()));
                    break;
                //Add here
                case USERNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "UserName", 100, isOptional))
                        throw new ValidationException("Invalid UserName", "Invalid UserName:::" + req.getParameter(input.toString()));
                    break;
                case PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password:::" + req.getParameter(input.toString()));
                    break;
                case COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "companyName", 100, isOptional))
                        throw new ValidationException("Invalid Company Name", "Invalid Company Name:::" + req.getParameter(input.toString()));
                    break;
                case CONTACT_PERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "contactName", 100, isOptional))
                        throw new ValidationException("Invalid Company Contact Person", "Invalid Contact Person:::" + req.getParameter(input.toString()));
                    break;
                case CONTACT_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Email address or Email address field cannot be left blank", "Invalid Email address or Email address field cannot be left blank:::" + req.getParameter(input.toString()));
                    break;
                case SITENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Site Name", "Invalid Site Name:::" + req.getParameter(input.toString()));
                    break;
                case SUPPORT_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Support Mail Id", "Invalid Support Mail Id:::" + req.getParameter(input.toString()));
                    break;
                case ADMIN_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Admin Mail Id", "Invalid Admin Mail Id:::" + req.getParameter(input.toString()));
                    break;
                case SUPPORT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Support URL", "Invalid Support URL:::" + req.getParameter(input.toString()));
                    break;
                case HOST_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "HostURL", 255, isOptional))
                        throw new ValidationException("Invalid Host URL", "Invalid Host URL:::" + req.getParameter(input.toString()));
                    break;
                case SALES_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Sales Email Address", "Invalid Sales Email Address:::" + req.getParameter(input.toString()));
                    break;
                case BILLING_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Billing Email Address", "Invalid Billing Email Address:::" + req.getParameter(input.toString()));
                    break;
                case NOTIFY_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Notify Email Address", "Invalid Notify Email Address:::" + req.getParameter(input.toString()));
                    break;
                case COMPANY_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Company from Address", "Invalid Company from Address:::" + req.getParameter(input.toString()));
                    break;
                case SUPPORT_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Support from Address", "Invalid Support from Address:::" + req.getParameter(input.toString()));
                    break;
                case SMTP_HOST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_Host", "Invalid SMTP_Host:::" + req.getParameter(input.toString()));
                    break;
                case SMTP_PORT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_Port", "Invalid SMTP_Port:::" + req.getParameter(input.toString()));
                    break;
                case SMTP_USER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_User", "Invalid SMTP_User:::" + req.getParameter(input.toString()));
                    break;
                case SMTP_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_Password", "Invalid SMTP_Password:::" + req.getParameter(input.toString()));
                    break;
                case SMS_USER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMS_User", "Invalid SMS_User:::" + req.getParameter(input.toString()));
                    break;
                case SMS_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMS_PASSWORD", "Invalid SMS_PASSWORD:::" + req.getParameter(input.toString()));
                    break;
                case FROM_SMS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid FROM_SMS", "Invalid FROM_SMS:::" + req.getParameter(input.toString()));
                    break;
                case PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PartnerId", "Invalid PartnerId:::" + req.getParameter(input.toString()));
                    break;
                case CARDNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CC", 20, isOptional))
                        throw new ValidationException("Invalid Card Number", "Invalid Card Number:::" + req.getParameter(input.toString()));
                    break;
                case CVV_SMALL:
                    if (req.getParameter(input.toString()).length() < 3 || !ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CVV", 4, isOptional))
                        throw new ValidationException("Invalid CVV", "Invalid CVV:::" + req.getParameter(input.toString()));
                    break;
                case EXPIRY_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Expiry Month", "Invalid Expiry Month:::" + req.getParameter(input.toString()));
                    break;
                case EXPIRY_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Expiry Year", "Invalid Expiry Year:::" + req.getParameter(input.toString()));
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 8, isOptional) || !Functions.isValidDate(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Birth Date", "Invalid Birth Date:::" + req.getParameter(input.toString()));
                    break;
                case LANGUAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 3, isOptional))
                        throw new ValidationException("Invalid Language", "Invalid Language:::" + req.getParameter(input.toString()));
                    break;
                case TRACKINGID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 500, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId:::" + req.getParameter(input.toString()));
                    break;
                case CAPTUREAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + req.getParameter(input.toString()));
                    break;
                case REFUNDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + req.getParameter(input.toString()));
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 50, isOptional))
                        throw new ValidationException("Invalid Reason", "Invalid Reason :::" + req.getParameter(input.toString()));
                    break;
                case MIDDLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Middle Name", "Invalid Middle Name :::" + req.getParameter(input.toString()));
                    break;
                case IPADDRESS:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid IP Address", "Invalid IP Address :::" + req.getParameter(input.toString()));
                    break;
                case MERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + req.getParameter(input.toString()));
                    break;
                case AGENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Agent ID", "Invalid Agent ID :::" + req.getParameter(input.toString()));
                    break;
                //validation starts for ICICI Module
                case MEMBERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid Merchant Id", "Invalid Merchant Id :::" + req.getParameter(input.toString()));
                    break;
                case ACTIVATION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Statues", 10, isOptional))
                        throw new ValidationException("Invalid Activation", "Invalid Activation :::" + req.getParameter(input.toString()));
                    break;
                case ICICI:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Statues", 10, isOptional))
                        throw new ValidationException("Invalid ICICI", "Invalid ICICI :::" + req.getParameter(input.toString()));
                    break;
                case RESERVES:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Reserves", "Invalid Reserves :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Chargeper", "Invalid Chargeper :::" + req.getParameter(input.toString()));
                    break;
                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Days", 2, isOptional))
                        throw new ValidationException("Invalid From Date", "Invalid From Date :::" + req.getParameter(input.toString()));
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Days", 2, isOptional))
                        throw new ValidationException("Invalid To Date", "Invalid To Date :::" + req.getParameter(input.toString()));
                    break;
                case FROMMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        throw new ValidationException("Invalid From Month", "Invalid From Month :::" + req.getParameter(input.toString()));
                    break;
                case TOMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        throw new ValidationException("Invalid To Month", "Invalid To Month :::" + req.getParameter(input.toString()));
                    break;
                case TOYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        throw new ValidationException("Invalid To Year", "Invalid To Year :::" + req.getParameter(input.toString()));
                    break;
                case FROMYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        throw new ValidationException("Invalid From Year", "Invalid From Year :::" + req.getParameter(input.toString()));
                    break;
                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Page No", "Invalid Page No :::" + req.getParameter(input.toString()));
                    break;
                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Records", "Invalid Records :::" + req.getParameter(input.toString()));
                    break;
                case ARN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid ARN", "Invalid ARN :::" + req.getParameter(input.toString()));
                    break;
                case RRN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 30, isOptional))
                        throw new ValidationException("Invalid RRN", "Invalid RRN :::" + req.getParameter(input.toString()));
                    break;
                case AUTHORIZATION_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 50, isOptional))
                        throw new ValidationException("Invalid Authoriation code", "Invalid Authoriation code :::" + req.getParameter(input.toString()));
                    break;
                case DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Domain", "Invalid Domain :::" + req.getParameter(input.toString()));
                    break;
                case NAME_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name :::" + req.getParameter(input.toString()));
                    break;
                case DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description :::" + req.getParameter(input.toString()));
                    break;
                case ORDERDESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 40, isOptional))
                        throw new ValidationException("Invalid Order Desc", "Invalid Order Desc :::" + req.getParameter(input.toString()));
                    break;
                case TRACKINGID_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + req.getParameter(input.toString()));
                    break;
                case GATEWAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "alphanum", 20, isOptional))
                        throw new ValidationException("Invalid Gateway Name", "Invalid Gateway Name :::" + req.getParameter(input.toString()));
                    break;
                case YEAR_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Year", "Invalid Year :::" + req.getParameter(input.toString()));
                    break;
                case MONTH_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Month", "Invalid Month :::" + req.getParameter(input.toString()));
                    break;
                case COMPANY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Company", "Invalid Company :::" + req.getParameter(input.toString()));
                    break;
                case ACCOUNTID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid AccountID", "Invalid AccountID :::" + req.getParameter(input.toString()));
                    break;
                case FIRSTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 6, isOptional))
                        throw new ValidationException("Invalid First Six digit of Card Number", "Invalid First Six digit of Card Number :::" + req.getParameter(input.toString()));
                    break;
                case LASTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid Last Four digit of Card Number", "Invalid Last Four digit of Card Number :::" + req.getParameter(input.toString()));
                    break;
                case OLD_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Password", 20, isOptional))
                        throw new ValidationException("Invalid Old Password", "Invalid Old Password :::" + req.getParameter(input.toString()));
                    break;
                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid New Password", "Invalid New Password :::" + req.getParameter(input.toString()));
                    break;
                case CONFIRM_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Confirm Password", "Invalid Confirm Password :::" + req.getParameter(input.toString()));
                    break;
                case MAILTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Mailtype", 10, isOptional))
                        throw new ValidationException("Invalid Mail Type", "Invalid Mail Type :::" + req.getParameter(input.toString()));
                    break;
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Login", "Invalid Login :::" + req.getParameter(input.toString()));
                    break;
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid OrderID", "Invalid OrderID :::" + req.getParameter(input.toString()));
                    break;
                case INVOICENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + req.getParameter(input.toString()));
                    break;
                case INV:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + req.getParameter(input.toString()));
                    break;
                case FROMID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid Fromid", "Invalid Fromid :::" + req.getParameter(input.toString()));
                    break;
                case PAYMENTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid PaymentNumber", "Invalid PaymentNumber :::" + req.getParameter(input.toString()));
                    break;
                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid PaymentId", "Invalid PaymentId :::" + req.getParameter(input.toString()));
                    break;
                case CCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Card Number", "Invalid Card Number :::" + req.getParameter(input.toString()));
                    break;
                case FIRST_SIX:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid First Six digit of Card", "Invalid First Six digit of Card :::" + req.getParameter(input.toString()));
                    break;
                case LAST_FOUR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Last Four digit of Card", "Invalid Last Four digit of Card :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid ChargrId", "Invalid ChargeId :::" + req.getParameter(input.toString()));
                    break;
                case CHARGENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Charge Name", "Invalid Charge Name :::" + req.getParameter(input.toString()));
                    break;
                case ISINPUTREQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 5, isOptional))
                        throw new ValidationException("Invalid Is Input Required", "Invalid Is Input Required :::" + req.getParameter(input.toString()));
                    break;
                case MAPPINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid MappingId", "Invalid MappingId :::" + req.getParameter(input.toString()));
                    break;
                case PAYMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Paymode", "Invalid Paymode :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Chargr Value", "Invalid Chargr Value :::" + req.getParameter(input.toString()));
                    break;
                case CHARGETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Chargr Type", "Invalid Charge Type :::" + req.getParameter(input.toString()));
                    break;
                case MIN_PAYOUT_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Min Payout Amount", "Invalid Min Payout Amount:::" + req.getParameter(input.toString()));
                    break;
                case PCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Partner Charge Value", "Invalid Partner Charge Value :::" + req.getParameter(input.toString()));
                    break;
                case ACHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Agent Charge Value", "Invalid Agent Charge Value :::" + req.getParameter(input.toString()));
                    break;
                case MCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Merchant Charge Value", "Invalid Merchant Charge Value :::" + req.getParameter(input.toString()));
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 3, isOptional))
                        throw new ValidationException("Invalid Currency", "Invalid Currency :::" + req.getParameter(input.toString()));
                    break;
                case PGTYPEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PgType Id", "Invalid PgType Id :::" + req.getParameter(input.toString()));
                    break;
                case ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Action", "Invalid Action :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEPERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid ChargePercentage", "Invalid ChargePercentage :::" + req.getParameter(input.toString()));
                    break;
                case TAXPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid TaxPercentage", "Invalid TaxPercentage :::" + req.getParameter(input.toString()));
                    break;
                case WITHDRAWCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid WITHDRAWCHARGE", "Invalid WITHDRAWCHARGE :::" + req.getParameter(input.toString()));
                    break;
                case REVERSECHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid Reversal Charge", "Invalid Reversal Charge :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEBACKCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid CHARGEBACKCHARGE", "Invalid CHARGEBACKCHARGE :::" + req.getParameter(input.toString()));
                    break;
                case CHARGESACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid CHARGESACCOUNT", "Invalid CHARGESACCOUNT :::" + req.getParameter(input.toString()));
                    break;
                case TAXACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid TAXACCOUNT", "Invalid TAXACCOUNT :::" + req.getParameter(input.toString()));
                    break;
                case HIGHRISKAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid HIGHRISKAMOUNT", "Invalid HIGHRISKAMOUNT :::" + req.getParameter(input.toString()));
                    break;
                case ADDRESS_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString()));
                    break;
                case GATEWAY_TABLE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + req.getParameter(input.toString()));
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PartnerId", "Invalid PartnerId:::" + req.getParameter(input.toString()));
                    break;
                case SUPERPARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid SuperPartnerId", "Invalid SuperPartnerId:::" + req.getParameter(input.toString()));
                    break;
                case LOGONAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Logo Name", "Invalid Logo Name :::" + req.getParameter(input.toString()));
                    break;

                case ALIASNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Alias Name", "Invalid Alias Name :::" + req.getParameter(input.toString()));
                    break;
                case DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Display Name", "Invalid Display Name :::" + req.getParameter(input.toString()));
                    break;
                case ISMASTERCARDSUPPORTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Mastercard Supported", "Invalid Is Mastercard Supported :::" + req.getParameter(input.toString()));
                    break;
                case SHORTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Short Name", "Invalid Short Name :::" + req.getParameter(input.toString()));
                    break;
                case SITE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Site", "Invalid Site :::" + req.getParameter(input.toString()));
                    break;
                case PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Path", "Invalid Path :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEBACKPATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + req.getParameter(input.toString()));
                    break;
                case ISCVVREQUIRED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 5, isOptional))
                        throw new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + req.getParameter(input.toString()));
                    break;
                case DAILYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + req.getParameter(input.toString()));
                    break;
                case DAILYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + req.getParameter(input.toString()));
                    break;
                case WEEKLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + req.getParameter(input.toString()));
                    break;
                case MINTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + req.getParameter(input.toString()));
                    break;
                case MAXTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + req.getParameter(input.toString()));
                    break;
                case DAILYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case WEEKLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case COLUMN_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 250, isOptional))
                        throw new ValidationException("Invalid Column Name", "Invalid Column Name :::" + req.getParameter(input.toString()));
                    break;
                case GATEWAY_TABLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Gateway Table Name", "Invalid Gateway Table Name :::" + req.getParameter(input.toString()));
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1055, isOptional))
                        throw new ValidationException("Invalid Redirect URL", "Invalid Redirect URL :::" + req.getParameter(input.toString()));
                    break;
                case NOTIFICATIONURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Notification URL", "Invalid Notification URL :::" + req.getParameter(input.toString()));
                    break;
                case SEARCH_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Search Type", "Invalid Search Type :::" + req.getParameter(input.toString()));
                    break;
                case SEARCH_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 15, isOptional))
                        throw new ValidationException("Invalid Search Id", "Invalid Search Id :::" + req.getParameter(input.toString()));
                    break;
                case GATEWAY_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Gateway Type", "Invalid Gateway Type :::" + req.getParameter(input.toString()));
                    break;
                case SDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description :::" + req.getParameter(input.toString()));
                    break;
                case STRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + req.getParameter(input.toString()));
                    break;
                case SCAPTUREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Id", "Invalid Capture Id :::" + req.getParameter(input.toString()));
                    break;
                case ICICITRANSEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid ICICI Transe Id", "Invalid ICICI Transe Id :::" + req.getParameter(input.toString()));
                    break;
                case REFUNDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Id", "Invalid Refund Id :::" + req.getParameter(input.toString()));
                    break;
                case REFUND_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Code", "Invalid Refund Code :::" + req.getParameter(input.toString()));
                    break;
                case REFUND_RECEIPT_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Receipt No", "Invalid Refund Receipt No :::" + req.getParameter(input.toString()));
                    break;
                case EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress:::" + req.getParameter(input.toString()));
                    break;
                case SEARCHTYPE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Search Type", "Invalid Search Type :::" + req.getParameter(input.toString()));
                    break;
                case DATE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "date", 30, isOptional))
                        throw new ValidationException("Invalid Date", "Invalid Date:::" + req.getParameter(input.toString()));
                    break;
                case CB_REF_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid CB Ref No", "Invalid CB Ref No :::" + req.getParameter(input.toString()));
                    break;
                case CB_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid CB Amount", "Invalid CB Amount :::" + req.getParameter(input.toString()));
                    break;
                case CB_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 100, isOptional))
                        throw new ValidationException("Invalid CB Reason", "Invalid CB Reason :::" + req.getParameter(input.toString()));
                    break;
                case SEARCH_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Search Status", "Invalid Search Status :::" + req.getParameter(input.toString()));
                    break;
                case AGENT_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Agent Id", "Invalid Agent Id :::" + req.getParameter(input.toString()));
                    break;
                case AGENT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Agent Name", "Invalid Agent Name :::" + req.getParameter(input.toString()));
                    break;
                case PARTNER_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Partner Name", "Invalid Partner Name :::" + req.getParameter(input.toString()));
                    break;
                case PASSWORD_FULL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Password", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password :::" + req.getParameter(input.toString()));
                    break;
                case MERCHANTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + req.getParameter(input.toString()));
                    break;
                case DAILY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + req.getParameter(input.toString()));
                    break;
                case WEEKLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Weekly Amount limit", "Invalid Weekly Amount limit :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + req.getParameter(input.toString()));
                    break;
                case DAILY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + req.getParameter(input.toString()));
                    break;
                case WEEKLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + req.getParameter(input.toString()));
                    break;
                case DAILY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case WEEKLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + req.getParameter(input.toString()));
                    break;
                case MIN_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + req.getParameter(input.toString()));
                    break;
                case MAX_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + req.getParameter(input.toString()));
                    break;
                case IS_ACTIVE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Active", "Invalid Is Active :::" + req.getParameter(input.toString()));
                    break;
                case PRIORITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 1, isOptional))
                        throw new ValidationException("Invalid Priority", "Invalid Priority :::" + req.getParameter(input.toString()));
                    break;
                case IS_TEST:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Test", "Invalid Is Test :::" + req.getParameter(input.toString()));
                    break;

                case CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 7, isOptional))
                        throw new ValidationException("Invalid Charge Percentage", "Invalid Charge Percentage :::" + req.getParameter(input.toString()));
                    break;
                case FIX_APPROVE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fix Approve Charge", "Invalid Fix Approve Charge :::" + req.getParameter(input.toString()));
                    break;
                case FIX_DECLINE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fix Decline Charge", "Invalid Fix Decline Charge :::" + req.getParameter(input.toString()));
                    break;
                case TAX_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Tax Percentage", "Invalid Tax Percentage :::" + req.getParameter(input.toString()));
                    break;
                case REVERSE_PERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Reverse Percentage", "Invalid Reverse Percentage :::" + req.getParameter(input.toString()));
                    break;
                case FRAUDE_VARIFICATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fraude Varification Charge", "Invalid Fraude Varification Charge :::" + req.getParameter(input.toString()));
                    break;
                case ANNUAL_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Annual Charge", "Invalid Annual Charge :::" + req.getParameter(input.toString()));
                    break;
                case SETUP_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Setup Charge", "Invalid Setup Charge :::" + req.getParameter(input.toString()));
                    break;
                case FX_CLERANCE_CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Forex Clearance Charge Percentage", "Invalid Forex Clearance Charge Percentage :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLY_GATEWAY_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Monthly Gateway Charge", "Invalid Monthly Gateway Charge :::" + req.getParameter(input.toString()));
                    break;
                case MONTHLY_ACC_MN_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Monthly Account Mnt Charge", "Invalid Monthly Account Mnt Charge :::" + req.getParameter(input.toString()));
                    break;
                case REPORT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Report Charge", "Invalid Report Charge :::" + req.getParameter(input.toString()));
                    break;
                case FRAUDULENT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fraudulent Charge", "Invalid Fraudulent Charge :::" + req.getParameter(input.toString()));
                    break;
                case AUTO_REPRESENTATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Auto Representation Charge", "Invalid Auto Representation Charge :::" + req.getParameter(input.toString()));
                    break;
                case INTERCHANGE_PLUS_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Interchange Plus Charge", "Invalid Interchange Plus Charge :::" + req.getParameter(input.toString()));
                    break;
                case OLD_DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Old Display Name", "Invalid Old Display Name :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEBACK_PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + req.getParameter(input.toString()));
                    break;
                case ISCVV_REQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + req.getParameter(input.toString()));
                    break;
                case SEARCH_DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "URL", 100, isOptional))
                        throw new ValidationException("Invalid Domain", "Invalid Domain :::" + req.getParameter(input.toString()));
                    break;
                case SEARCH_WORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Search Word", "Invalid Search Word :::" + req.getParameter(input.toString()));
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid IP Address", "Invalid IP Address :::" + req.getParameter(input.toString()));
                    break;

                case CUSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Customer Name", "Invalid Customer Name :::" + req.getParameter(input.toString()));
                    break;
                case INVOICE_CANCEL_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Description", 50, isOptional))
                        throw new ValidationException("Invalid Cancel Reason", "Invalid Cancel Reason :::" + req.getParameter(input.toString()));
                    break;

                case MINUS_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 50, isOptional))
                        throw new ValidationException("Invalid Amount", "Invalid Amount :::" + req.getParameter(input.toString()));
                    break;

                case PASSWORD2:
                    if (!ESAPI.validator().isValidInput(input.toString(), (String) req.getAttribute(input.toString()), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password :::" + req.getAttribute(input.toString()));
                    break;
                case DATASOURCE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Datasource", 20, isOptional))
                        throw new ValidationException("Invalid Datasource", "Invalid Datasource :::" + req.getParameter(input.toString()));
                    break;

                case SHIPPINGPODBATCH:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CSEURL", 100, isOptional))
                        throw new ValidationException("Invalid Shipping Batch", "INVALID Shipping Batch :::" + req.getParameter(input.toString()));
                    break;

                case SHIPPINGPOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Shipping Id", "INVALID Shiping Id :::" + req.getParameter(input.toString()));
                    break;

                case PHONENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Phone", 20, isOptional))
                        throw new ValidationException("Invalid Phone Number", "Invalid Phone Number:::" + req.getParameter(input.toString()));
                    break;

                case CSEEid:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid CSEid", "Invalid CSEid:::" + req.getParameter(input.toString()));
                    break;

                case CSEname:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid CSEname", "Invalid CSEname:::" + req.getParameter(input.toString()));
                    break;

                case LASTFOURCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "LastFourcc", 4, isOptional))
                        throw new ValidationException("Invalid Last Four CC No", "Invalid Last Four CC No:::" + req.getParameter(input.toString()));
                    break;
                case FIRSTSIXCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "FirstSixcc", 6, isOptional))
                        throw new ValidationException("Invalid First Six CC No", "Invalid First Six CC No:::" + req.getParameter(input.toString()));
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Cardholder IpAddress", "Invalid Cardholder IpAddress:::" + req.getParameter(input.toString()));
                    break;
                case BRANDNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Brandname", "Invalid Brandname:::" + req.getParameter(input.toString()));
                    break;
                case PAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isPaid", 30, isOptional))
                        throw new ValidationException("Invalid isPaid", "Invalid isPaid:::" + req.getParameter(input.toString()));
                    break;

                case FDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 10, isOptional))
                        throw new ValidationException("Invalid from date", "Invalid from date:::" + req.getParameter(input.toString()));
                    break;

                case TDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 10, isOptional))
                        throw new ValidationException("Invalid to date", "Invalid to date:::" + req.getParameter(input.toString()));
                    break;
                case COMMA_SEPRATED_NUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CommaSeprateNum", 500, isOptional))
                        throw new ValidationException("Invalid Tracking ID", "Invalid Tracking ID:::" + req.getParameter(input.toString()));
                    break;

                case COMMASEPRATED_TRACKINGID_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "CommaSeprateNum", 500, isOptional))
                        throw new ValidationException("Invalid Tracking ID", "Invalid Tracking ID:::" + req.getParameter(input.toString()));
                    break;

                case FILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fileName", 255, isOptional))
                        throw new ValidationException("Invalid File Name", "Invalid File Name:::" + req.getParameter(input.toString()));
                    break;

                case SMALL_ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Action", 100, isOptional))
                        throw new ValidationException("Invalid Action", "Invalid Action:::" + req.getParameter(input.toString()));
                    break;
                case BANKRECEIVEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 25, isOptional))
                        throw new ValidationException("Invalid Bank SettlementCycle Id ", "Invalid Bank SettlementCycle Id:::" + req.getParameter(input.toString()));
                    break;

                case SETTLEMENTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Settlement Date", "Invalid Settlement Date:::" + req.getParameter(input.toString()));
                    break;

                case EXPECTED_STARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Expected Start Date", "Invalid Expected Start Date:::" + req.getParameter(input.toString()));
                    break;

                case EXPECTED_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Expected End Date", "Invalid Expected End Date:::" + req.getParameter(input.toString()));
                    break;

                case ACTUAL_SARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Actual Start Date", "Invalid Actual Start Date:::" + req.getParameter(input.toString()));
                    break;

                case ACTUAL_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Actual End Date", "Invalid Actual End Date:::" + req.getParameter(input.toString()));
                    break;

                case SETTLEMENTCYCLEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 25, isOptional))
                        throw new ValidationException("Invalid Settlement cycle id", "Invalid Settlement cycle id:::" + req.getParameter(input.toString()));
                    break;

                case ROLLINGRESERVEDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Rolling Release Date", "Invalid Rolling Release Date:::" + req.getParameter(input.toString()));
                    break;

                case ISSETTLEMENTCRONEXECCUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid isSettlement cron executed ", "Invalid isSettlement cron executed:::" + req.getParameter(input.toString()));
                    break;

                case ISPAYOUCRONEXECUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid isPayout cron executed ", "Invalid isPayout cron executed:::" + req.getParameter(input.toString()));
                    break;

                case ISDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid Day Light ", "Invalid Day Light:::" + req.getParameter(input.toString()));
                    break;

                case ISPAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid Is Paid ", "Invalid Is Paid:::" + req.getParameter(input.toString()));
                    break;

                case BANKMERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Bank merchant settlement id ", "Invalid Bank merchant settlement id:::" + req.getParameter(input.toString()));
                    break;
                case FSID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid FraudSystem ID", "Invalid FraudSystem ID :::" + req.getParameter(input.toString()));
                    break;
                case FSNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Fraud System Name", "Invalid Fraud System Name :::" + req.getParameter(input.toString()));
                    break;

                case BANKWIREMANGERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid Bank Wire Manager Id", "Invalid Bank Wire Manager Id :::" + req.getParameter(input.toString()));
                    break;
                case PARENT_BANKWIREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Parent Wire Id", "Invalid BanParent Wire Id :::" + req.getParameter(input.toString()));
                    break;
                case PROCESSINGAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Processing Amount", "Invalid Processing Amount :::" + req.getParameter(input.toString()));
                    break;
                case GROSSAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Gross Amount", "Invalid Gross Amount :::" + req.getParameter(input.toString()));
                    break;
                case NETFINALAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Net Final Amount", "Invalid Net Final Amount :::" + req.getParameter(input.toString()));
                    break;
                case UNPAIDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Unpaid Amount", "Invalid Unpaid Amount :::" + req.getParameter(input.toString()));
                    break;
                case ISROLLINGRESERVERELAEASEWIRE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "isYN", 50, isOptional))
                        throw new ValidationException("Invalid isRollingReserveReleaseWire", "Invalid isRollingReserveReleaseWire :::" + req.getParameter(input.toString()));
                    break;
                case DECLINEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid DeclinedCoveredDateUpto", "Invalid DeclinedCoveredDateUpto :::" + req.getParameter(input.toString()));
                    break;
                case CHARGEBACKCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid ChargebackCoveredDateUpto", "Invalid ChargebackCoveredDateUpto :::" + req.getParameter(input.toString()));
                    break;
                case REVERSEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid ReversedCoveredDateUpto", "Invalid ReversedCoveredDateUpto :::" + req.getParameter(input.toString()));
                    break;
                case SETTLEMENTREPORTFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "reportFile", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Report File", "Invalid Settlement Report File :::" + req.getParameter(input.toString()));
                    break;
                case SETTLEMENTTRANSACTIONFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "transactionFile", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Transaction File", "Invalid Settlement Transaction File :::" + req.getParameter(input.toString()));
                    break;

                case EXPECTED_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Expected start time", "Invalid Expected start time :::" + req.getParameter(input.toString()));
                    break;
                case EXPECTED_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Expected end time", "Invalid Expected end time :::" + req.getParameter(input.toString()));
                    break;
                case ACTUAL_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Actual start time", "Invalid Actual start time :::" + req.getParameter(input.toString()));
                    break;
                case ACTUAL_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Actual end time", "Invalid Actual end time :::" + req.getParameter(input.toString()));
                    break;

                case ROLLINGRELEASETIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Rolling Release Time", "Invalid Rolling Release Time :::" + req.getParameter(input.toString()));
                    break;

                case DECLINECOVERTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Decline Covered Time", "Invalid Decline Covered Time :::" + req.getParameter(input.toString()));
                    break;

                case CHARGEBACKCOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Chargeback Covered Time", "Invalid Chargeback Covered Time :::" + req.getParameter(input.toString()));
                    break;

                case REVERSECOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Reverse Covered Time", "Invalid Reverse Covered Time :::" + req.getParameter(input.toString()));
                    break;

                case TIMEDIFFNORMAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 16, isOptional))
                        throw new ValidationException("Invalid Time difference Normal", "Invalid Time difference Normal:::" + req.getParameter(input.toString()));
                    break;

                case TIMEDIFFDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 16, isOptional))
                        throw new ValidationException("Invalid Time diffence DayLight", "Invalid Time diffence DayLight:::" + req.getParameter(input.toString()));
                    break;

                case SETTLEMENTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "time", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Time", "Invalid Settlement Time :::" + req.getParameter(input.toString()));
                    break;

                case HIGH_RISK_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid High Risk Amount", "Invalid High Risk Amount:::" + req.getParameter(input.toString()));
                    break;
                case MAX_SCORE_ALLOWED:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max score Allowed", "Invalid Max score Allowed:::" + req.getParameter(input.toString()));
                    break;
                case MAX_SCORE_REVERSEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max score Reversal", "Invalid Max score Reversal:::" + req.getParameter(input.toString()));
                    break;
                case REFUND_DAILY_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund daily Limit", "Invalid Refund daily Limit:::" + req.getParameter(input.toString()));
                    break;
                case CHARGETECHNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "StrictString", 25, isOptional))
                        throw new ValidationException("Invalid Charge Tech Name", "Invalid Charge Tech Name:::" + req.getParameter(input.toString()));
                    break;
                case FRAUDEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        throw new ValidationException("Invalid fraud email", "Invalid fraud email:::" + req.getParameter(input.toString()));
                    break;
                case BANKIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid Bank IP Address", "Invalid Bank IP Address:::" + req.getParameter(input.toString()));
                    break;
                case APPTOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Application", "Invalid Application:::" + req.getParameter(input.toString()));
                    break;

                case BANK_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Bank", "Invalid Bank:::" + req.getParameter(input.toString()));
                    break;

                case ARCHIVE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Archive", 50, isOptional))
                        throw new ValidationException("Invalid Archive", "Invalid Archive:::" + req.getParameter(input.toString()));
                    break;
                case RBID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Recurring Billing Id", "Invalid Recurring Billing Id:::" + req.getParameter(input.toString()));
                    break;
                case RESERVEFIELD1:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "recurring", 20, isOptional))
                        throw new ValidationException("Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28)", "Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28):::" + req.getParameter(input.toString()));
                    break;
                case Bank_EmailID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "Email", 50, isOptional))
                        throw new ValidationException("Invalid Bank Email ID", "Invalid Bank Email ID:::" + req.getParameter(input.toString()));

                    break;
                case RISKPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Profile Id", "Invalid RiskProfile Id:::" + req.getParameter(input.toString()));

                    break;

                case RISKRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Rule Id", "Invalid Risk Rule Id:::" + req.getParameter(input.toString()));

                    break;

                case RISKPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Risk Profile Name", "Invalid Risk Profile Name:::" + req.getParameter(input.toString()));

                    break;

                case BUSINESSRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Business Rule Id", "Invalid Business Rule Id:::" + req.getParameter(input.toString()));

                    break;

                case BUSINESSPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid BusinessProfile Id", "Invalid BusinessProfile Id:::" + req.getParameter(input.toString()));

                    break;

                case BUSINESSPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Business Profile Name", "Invalid Business Profile Name:::" + req.getParameter(input.toString()));

                    break;

                case COUNTOFROW:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Profile Row", "Invalid Risk Profile Row:::" + req.getParameter(input.toString()));

                    break;


                case RISK_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Name", "Invalid Risk Rule Name:::" + req.getParameter(input.toString()));
                    break;

                case RISK_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Label", "Invalid Risk Rule Label:::" + req.getParameter(input.toString()));
                    break;

                case RISK_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Description", "Invalid Risk Rule Description:::" + req.getParameter(input.toString()));
                    break;

                case BUSINESS_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Name", "Invalid Business Rule Name:::" + req.getParameter(input.toString()));
                    break;

                case BUSINESS_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Label", "Invalid Business Rule Label:::" + req.getParameter(input.toString()));
                    break;

                case BUSINESS_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Description", "Invalid Business Rule Description:::" + req.getParameter(input.toString()));
                    break;

                case BUSINESS_RULE_OPERATOR:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Operator", "Invalid Business Rule Operator:::" + req.getParameter(input.toString()));
                    break;

                case USERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid User Profile Id", "Invalid User Profile Id:::" + req.getParameter(input.toString()));

                    break;

                case ONLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Online Processing URL", "Invalid Online Processing URL::::" + req.getParameter(input.toString()));

                    break;
                case OFFLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Offline Processing URL", "Invalid Offline Processing URL:::" + req.getParameter(input.toString()));

                    break;
                case ONLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Online Threshold", "Invalid Online Threshold:::" + req.getParameter(input.toString()));

                    break;
                case OFFLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Offline Threshold", "Invalid Offline Threshold:::" + req.getParameter(input.toString()));

                    break;
                case DEFAULTMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Default Mode", "Invalid Default Mode:::" + req.getParameter(input.toString()));

                    break;
                case BUSINESSPROFILE_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Business Profile", "Invalid Business Profile:::" + req.getParameter(input.toString()));

                    break;
                case BACKGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Background color", "Invalid Background color:::" + req.getParameter(input.toString()));

                    break;
                case FOREGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Foreground color", "Invalid Foreground color:::" + req.getParameter(input.toString()));

                    break;
                case FONT:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional) || functions.hasHTMLTags(req.getParameter(input.toString())))
                        throw new ValidationException("Invalid Font color", "Invalid Font color:::" + req.getParameter(input.toString()));

                    break;
                case MERCHANTLOGO:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Merchant Logo", "Invalid Merchant Logo:::" + req.getParameter(input.toString()));

                    break;

                case CONSOLIDATEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid CONSOLIDATED ID", "Invalid CONSOLIDATED ID:::" + req.getParameter(input.toString()));

                    break;

                case BUSINESS_RULE_OPERATOR_QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Operator","Invalid Business Rule Operator:::"+req.getParameter(input.toString()));
                    break;

                case RULETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Rule Type","Invalid Rule Type:::"+req.getParameter(input.toString()));

                    break;

                case QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 1000, isOptional))
                        throw new ValidationException("Invalid Query","Invalid Query:::"+req.getParameter(input.toString()));

                    break;

                case REGEX:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()),"SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Regular Expression","Invalid Regular Expression:::"+req.getParameter(input.toString()));

                    break;
                case PAYMODEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber",10, isOptional ))
                        throw new ValidationException("Invalid paymodeid","Invalid paymodeid:::"+req.getParameter(input.toString()));
                    break;
                case CARDTYPEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), req.getParameter(input.toString()), "OnlyNumber",10, isOptional ))
                        throw new ValidationException("Invalid Card Type","Invalid Card Type:::"+req.getParameter(input.toString()));
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * hashTable will contain the  Enums as key for  input fields and field value
     * This method will return list of ValidationError if any
     *
     * @param hashTable
     * @param validationErrorList
     * @param isOptional
     */
    public void InputValidations(Hashtable<InputFields, String> hashTable, org.owasp.esapi.ValidationErrorList validationErrorList, boolean isOptional)
    {
        for (InputFields input : hashTable.keySet())
        {

            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Id", "Invalid Merchant Id :::" + hashTable.get(input)));
                    break;
                case TOTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TOTYPE", "Invalid TOTYPE:::" + hashTable.get(input)));
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Amount", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount. It accepts Only Numeric value", "Invalid Amount :::" + hashTable.get(input)));
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Id/Description", "Invalid Order Id/Description :::" + hashTable.get(input)));
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 255, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderDescription", "Invalid OrderDescription :::" + hashTable.get(input)));
                    break;
                case TMPL_EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input)));
                    break;
                case TMPL_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input)));
                    break;
                case TMPL_STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Street", "Invalid Street :::" + hashTable.get(input)));
                    break;
                case TMPL_ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input)));
                    break;
                case TMPL_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input)));
                    break;
                case TMPL_TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + hashTable.get(input)));
                    break;
                case TMPL_TELNOCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telno", "Invalid Telno :::" + hashTable.get(input)));
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Amount", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount", "Invalid Amount :::" + hashTable.get(input)));
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Currency", "Invalid Currency :::" + hashTable.get(input)));
                    break;
                case TMPL_COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Country", "Invalid Country :::" + hashTable.get(input)));
                    break;
                case INVOICE_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Invoice No", "Invalid Invoice No :::" + hashTable.get(input)));
                    break;

                //Add here
                case MID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid MID", "Invalid MID :::" + hashTable.get(input)));
                    break;
                case OID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderId", "Invalid OrderId:::" + hashTable.get(input)));
                    break;
                case TOTAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Amount Or Enter Amount with 2 Digit Decimal", "Invalid Amount :::" + hashTable.get(input)));
                    break;
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CardType", "Invalid CardType :::" + hashTable.get(input)));
                    break;
                case BITKEY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid BITKey", "Invalid BITKey :::" + hashTable.get(input)));
                    break;
                case CHECKSUMALGORITHM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChecksumAlgoritham", "Invalid ChecksumAlgoritham :::" + hashTable.get(input)));
                    break;
                case B_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input)));
                    break;
                case B_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input)));
                    break;
                case B_ZIPCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input)));
                    break;
                case B_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input)));
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CountryCode", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CountryCode", "Invalid CountryCode :::" + hashTable.get(input)));
                    break;
                case PHONE1:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone", "Invalid Phone :::" + hashTable.get(input)));
                    break;
                case PHONE2:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone", "Invalid Phone :::" + hashTable.get(input)));
                    break;
                case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentType", "Invalid PaymentType :::" + hashTable.get(input)));
                    break;
                case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Terminal ID", "Invalid Terminal ID :::" + hashTable.get(input)));
                    break;
                //Add here
                case TOID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TOID", "Invalid TOID :::" + hashTable.get(input)));
                    break;
                case DESCRIPTION_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input)));
                    break;
                case TRACKINGID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input)));
                    break;
                case ACCOUNTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid AccountId", "Invalid AccountId :::" + hashTable.get(input)));
                    break;
                //Add here
                case LANGUAGE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Language", "Invalid Language :::" + hashTable.get(input)));
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Country", "Invalid Country :::" + hashTable.get(input)));
                    break;
                case DAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Day", "Invalid Day :::" + hashTable.get(input)));
                    break;
                case MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Month", "Invalid Month :::" + hashTable.get(input)));
                    break;
                case YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Year", "Invalid Year :::" + hashTable.get(input)));
                    break;
                case SSN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SSN", "Invalid SSN :::" + hashTable.get(input)));
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid FirstName", "Invalid FirstName :::" + hashTable.get(input)));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid LastName", "Invalid LastName :::" + hashTable.get(input)));
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input)));
                    break;
                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CC", 19, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PAN", "Invalid PAN :::" + hashTable.get(input)));
                    break;
                case CVV:
                    if (hashTable.get(input).length() < 3 || !ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CVV", "Invalid CVV :::" + hashTable.get(input)));
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address/Street", "Invalid Address/Street :::" + hashTable.get(input)));
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "City", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input)));
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input)));
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input)));
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + hashTable.get(input)));
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TelCC", "Invalid TelCC :::" + hashTable.get(input)));
                    break;
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expire Month", "Invalid Expiry Month :::" + hashTable.get(input)));
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expire Year", "Invalid Expire Year :::" + hashTable.get(input)));
                    break;
                case NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input)));
                    break;
                case CCCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CCP", "Invalid CCP :::" + hashTable.get(input)));
                    break;
                case ADDRCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input)));
                    break;
                case CARDTYPE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 12, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cardtype", "Invalid Cardtype :::" + hashTable.get(input)));
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input)));
                    break;
                case TRACKING_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input)));
                    break;
                case TRANS_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Transaction Status", "Invalid Transaction Status :::" + hashTable.get(input)));
                    break;
                case CARDHOLDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cardholder", "Invalid Cardholder :::" + hashTable.get(input)));
                    break;
                case NAME_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input)));
                    break;
                case STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Status", "Invalid Status :::" + hashTable.get(input)));
                    break;
                case MESSAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Message", "Invalid Message :::" + hashTable.get(input)));
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Checksum", "Invalid Checksum :::" + hashTable.get(input)));
                    break;
                case TRACKING_ID_REF:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reference", "Invalid Reference :::" + hashTable.get(input)));
                    break;
                case CHECKSUM_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Checksum", "Invalid Checksum :::" + hashTable.get(input)));
                    break;
                case TMPL_COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CountryCode", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Currency code", "Invalid Currency code:::" + hashTable.get(input)));
                    break;
                case ORDER_DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 255, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Description", "Invalid Order Description :::" + hashTable.get(input)));
                    break;
                //Add here
                case USERNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "alphanum", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid User Name", "Invalid User Name :::" + hashTable.get(input)));
                    break;
                case PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + hashTable.get(input)));
                    break;
                case COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company Name", "Invalid Company Name :::" + hashTable.get(input)));
                    break;
                case CONTACT_PERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "contactName", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Contact_Person", "Invalid Contact_Person :::" + hashTable.get(input)));
                    break;
                case CONTACT_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Contact_Email", "Invalid Contact_Email :::" + hashTable.get(input)));
                    break;
                case SITENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sitename", "Invalid Sitename :::" + hashTable.get(input)));
                    break;
                case SUPPORT_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_Mail_Id", "Invalid Support_Mail_Id :::" + hashTable.get(input)));
                    break;
                case ADMIN_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Admin_Mail_Id", "Invalid Admin_Mail_Id :::" + hashTable.get(input)));
                    break;
                case SUPPORT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_URL", "Invalid Support_URL :::" + hashTable.get(input)));
                    break;
                case HOST_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "HostURL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Host URL", "Invalid Host URL :::" + hashTable.get(input)));
                    break;
                case SALES_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Sales_Email", "Invalid Sales_Email :::" + hashTable.get(input)));
                    break;
                case BILLING_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Billing_Email", "Invalid Billing_Email :::" + hashTable.get(input)));
                    break;
                case NOTIFY_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notify_Email", "Invalid Notify_Email :::" + hashTable.get(input)));
                    break;
                case COMPANY_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company_From_Address", "Invalid Company_From_Address :::" + hashTable.get(input)));
                    break;
                case SUPPORT_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support_From_Address", "Invalid Support_From_Address :::" + hashTable.get(input)));
                    break;
                case SMTP_HOST:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_HOST", "Invalid SMTP_HOST :::" + hashTable.get(input)));
                    break;
                case SMTP_PORT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_PORT", "Invalid SMTP_PORT :::" + hashTable.get(input)));
                    break;
                case SMTP_USER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_USER", "Invalid SMTP_USER :::" + hashTable.get(input)));
                    break;
                case SMTP_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid SMTP_Password", "Invalid SMTP_Password :::" + hashTable.get(input)));
                    break;
                case PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + hashTable.get(input)));
                    break;
                case CARDNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CC", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Number", "Invalid Card Number :::" + hashTable.get(input)));
                    break;
                case CVV_SMALL:
                    if (hashTable.get(input).length() < 3 || !ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CVV", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CVV", "Invalid CVV :::" + hashTable.get(input)));
                    break;
                case EXPIRY_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expiry Month", "Invalid Expiry Month :::" + hashTable.get(input)));
                    break;
                case EXPIRY_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expiry Year", "Invalid Expiry Year :::" + hashTable.get(input)));
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 8, isOptional) || !Functions.isValidDate(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Birth Date", "Invalid Birth Date :::" + hashTable.get(input)));
                    break;
                case LANGUAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Language", "Invalid Language :::" + hashTable.get(input)));
                    break;
                case TRACKINGID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 140, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input)));
                    break;
                case CAPTUREAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input.toString()), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + hashTable.get(input.toString())));
                    break;
                case REFUNDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Amount", "Invalid Refund Amount :::" + hashTable.get(input.toString())));
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reason", "Invalid Reason :::" + hashTable.get(input.toString())));
                    break;

                case MIDDLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Middle Name", "Invalid Middle Name :::" + hashTable.get(input)));
                    break;
                case IPADDRESS:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid IP Address", "Invalid IP Address :::" + hashTable.get(input)));
                    break;
                case MERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + hashTable.get(input)));
                    break;
                case AGENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent ID", "Invalid Agent ID :::" + hashTable.get(input)));
                    break;
                //validation starts for ICICI Module
                case MEMBERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Id", "Invalid Merchant Id :::" + hashTable.get(input)));
                    break;
                case ACTIVATION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Statues", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Activation", "Invalid Activation :::" + hashTable.get(input)));
                    break;
                case ICICI:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Statues", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ICICI", "Invalid ICICI :::" + hashTable.get(input)));
                    break;
                case RESERVES:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reserves", "Invalid Reserves :::" + hashTable.get(input)));
                    break;
                case CHARGEPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeper", "Invalid Chargeper :::" + hashTable.get(input)));
                    break;
                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Days", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Date", "Invalid From Date :::" + hashTable.get(input)));
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Days", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Date", "Invalid To Date :::" + hashTable.get(input)));
                    break;
                case FROMMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Month", "Invalid From Month :::" + hashTable.get(input)));
                    break;
                case TOMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Month", "Invalid To Month :::" + hashTable.get(input)));
                    break;
                case TOYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid To Year", "Invalid To Year :::" + hashTable.get(input)));
                    break;
                case FROMYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid From Year", "Invalid From Year :::" + hashTable.get(input)));
                    break;
                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Page no", "Invalid Page no :::" + hashTable.get(input)));
                    break;
                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Records", "Invalid Records :::" + hashTable.get(input)));
                    break;
                case DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Domain", "Invalid Domain :::" + hashTable.get(input)));
                    break;
                case NAME_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input)));
                    break;
                case DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input)));
                    break;
                case ORDERDESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 40, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Order Desc", "Invalid Order Desc :::" + hashTable.get(input)));
                    break;
                case TRACKINGID_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + hashTable.get(input)));
                    break;
                case GATEWAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway", "Invalid Gateway :::" + hashTable.get(input)));
                    break;
                case YEAR_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Year", "Invalid Year :::" + hashTable.get(input)));
                    break;
                case MONTH_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Month", "Invalid Month :::" + hashTable.get(input)));
                    break;
                case COMPANY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Company", "Invalid Company :::" + hashTable.get(input)));
                    break;
                case ACCOUNTID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid AccountID", "Invalid AccountID :::" + hashTable.get(input)));
                    break;
                case FIRSTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six digit of Card Number", "Invalid First Six digit of Card Number :::" + hashTable.get(input)));
                    break;
                case LASTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four digit of Card Number", "Invalid Last Four digit of Card Number :::" + hashTable.get(input)));
                    break;
                case OLD_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Password", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Old Password", "Invalid Old Password :::" + hashTable.get(input)));
                    break;
                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid New Password", "Invalid New Password :::" + hashTable.get(input)));
                    break;
                case CONFIRM_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Confirm Password", "Invalid Confirm Password :::" + hashTable.get(input)));
                    break;
                case MAILTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Mailtype", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Mail Typa", "Invalid Mail Typa :::" + hashTable.get(input)));
                    break;
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Login", "Invalid Login :::" + hashTable.get(input)));
                    break;
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid OrderID", "Invalid OrderID :::" + hashTable.get(input)));
                    break;
                case INVOICENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + hashTable.get(input)));
                    break;
                case INV:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + hashTable.get(input)));
                    break;
                case FROMID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fromid", "Invalid Fromid :::" + hashTable.get(input)));
                    break;
                case PAYMENTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentNumber", "Invalid PaymentNumber :::" + hashTable.get(input)));
                    break;
                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PaymentId", "Invalid PaymentId :::" + hashTable.get(input)));
                    break;
                case CCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Number", "Invalid Card Number :::" + hashTable.get(input)));
                    break;
                case FIRST_SIX:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six digit of Card", "Invalid First Six digit of Card :::" + hashTable.get(input)));
                    break;
                case LAST_FOUR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four digit of Card", "Invalid Last Four digit of Card :::" + hashTable.get(input)));
                    break;
                case CHARGEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargrId", "Invalid ChargeId :::" + hashTable.get(input)));
                    break;
                case CHARGENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Name", "Invalid Charge Name :::" + hashTable.get(input)));
                    break;
                case ISINPUTREQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Input Required", "Invalid Is Input Required :::" + hashTable.get(input)));
                    break;
                case MAPPINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid MappingId", "Invalid MappingId :::" + hashTable.get(input)));
                    break;
                case PAYMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Paymode", "Invalid Paymode :::" + hashTable.get(input)));
                    break;
                case CHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargr Value", "Invalid Chargr Value :::" + hashTable.get(input)));
                    break;
                case CHARGETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargr Type", "Invalid Charge Type :::" + hashTable.get(input)));
                    break;
                case MIN_PAYOUT_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Payout Amount", "Invalid Min Payout Amount:::" + hashTable.get(input)));
                    break;
                case MCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Charge Value", "Invalid Merchant Charge Value :::" + hashTable.get(input)));
                    break;
                case ACHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Charge Value", "Invalid Agent Charge Value :::" + hashTable.get(input)));
                    break;
                case PCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Charge Value", "Invalid Partner Charge Value :::" + hashTable.get(input)));
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 3, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Currency", "Invalid Currency :::" + hashTable.get(input)));
                    break;
                case PGTYPEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PayGatewayId", "Invalid PayGatewayId :::" + hashTable.get(input)));
                    break;
                case ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Action", "Invalid Action :::" + hashTable.get(input)));
                    break;
                case CHARGEPERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargePercentage", "Invalid ChargePercentage :::" + hashTable.get(input)));
                    break;
                case TAXPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TaxPercentage", "Invalid TaxPercentage :::" + hashTable.get(input)));
                    break;
                case WITHDRAWCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid WITHDRAWCHARGE", "Invalid WITHDRAWCHARGE :::" + hashTable.get(input)));
                    break;
                case REVERSECHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reversal Charge", "Invalid Reversal Charge :::" + hashTable.get(input)));
                    break;
                case CHARGEBACKCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CHARGEBACKCHARGE", "Invalid CHARGEBACKCHARGE :::" + hashTable.get(input)));
                    break;
                case CHARGESACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CHARGESACCOUNT", "Invalid CHARGESACCOUNT :::" + hashTable.get(input)));
                    break;
                case TAXACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid TAXACCOUNT", "Invalid TAXACCOUNT :::" + hashTable.get(input)));
                    break;
                case HIGHRISKAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid HIGHRISKAMOUNT", "Invalid HIGHRISKAMOUNT :::" + hashTable.get(input)));
                    break;
                case ADDRESS_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input)));
                    break;
                case GATEWAY_TABLE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input)));
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + hashTable.get(input)));
                    break;
                case LOGONAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Logo Name", "Invalid Logo Name :::" + hashTable.get(input)));
                    break;

                case ALIASNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Alias Name", "Invalid Alias Name :::" + hashTable.get(input)));
                    break;
                case DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Display Name", "Invalid Display Name :::" + hashTable.get(input)));
                    break;
                case ISMASTERCARDSUPPORTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Mastercard Supported", "Invalid Is Mastercard Supported :::" + hashTable.get(input)));
                    break;
                case SHORTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Short Name", "Invalid Short Name :::" + hashTable.get(input)));
                    break;
                case SITE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Site", "Invalid Site :::" + hashTable.get(input)));
                    break;
                case PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Path", "Invalid Path :::" + hashTable.get(input)));
                    break;
                case CHARGEBACKPATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + hashTable.get(input)));
                    break;
                case ISCVVREQUIRED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + hashTable.get(input)));
                    break;
                case MONTHLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + hashTable.get(input)));
                    break;
                case DAILYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + hashTable.get(input)));
                    break;
                case MONTHLYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + hashTable.get(input)));
                    break;
                case DAILYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + hashTable.get(input)));
                    break;
                case WEEKLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + hashTable.get(input)));
                    break;
                case MINTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + hashTable.get(input)));
                    break;
                case MAXTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + hashTable.get(input)));
                    break;
                case DAILYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case WEEKLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case MONTHLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case COLUMN_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 250, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Column Name", "Invalid Column Name :::" + hashTable.get(input)));
                    break;
                case GATEWAY_TABLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway Table Name", "Invalid Gateway Table Name :::" + hashTable.get(input)));
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1055, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Redirect URL", "Invalid Redirect URL :::" + hashTable.get(input)));
                    break;
                case NOTIFICATIONURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Notification URL", "Invalid Notification URL :::" + hashTable.get(input)));
                    break;
                case SEARCH_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Type", "Invalid Search Type :::" + hashTable.get(input)));
                    break;
                case SEARCH_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 15, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Id", "Invalid Search Id :::" + hashTable.get(input)));
                    break;
                case GATEWAY_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gateway Type", "Invalid Gateway Type :::" + hashTable.get(input)));
                    break;
                case SDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input)));
                    break;
                case STRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + hashTable.get(input)));
                    break;
                case SCAPTUREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Capture Id", "Invalid Capture Id :::" + hashTable.get(input)));
                    break;
                case ICICITRANSEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ICICI Transe Id", "Invalid ICICI Transe Id :::" + hashTable.get(input)));
                    break;
                case REFUNDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Id", "Invalid Refund Id :::" + hashTable.get(input)));
                    break;
                case REFUND_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Code", "Invalid Refund Code :::" + hashTable.get(input)));
                    break;
                case REFUND_RECEIPT_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund Receipt No", "Invalid Refund Receipt No :::" + hashTable.get(input)));
                    break;
                case EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input)));
                    break;
                case SEARCHTYPE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Type", "Invalid Search Type :::" + hashTable.get(input)));
                    break;
                case DATE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "date", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Date", "Invalid Date:::" + hashTable.get(input)));
                    break;
                case CB_REF_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Ref No", "Invalid CB Ref No :::" + hashTable.get(input)));
                    break;
                case CB_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Amount", "Invalid CB Amount :::" + hashTable.get(input)));
                    break;
                case CB_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CB Reason", "Invalid CB Reason :::" + hashTable.get(input)));
                    break;
                case SEARCH_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Status", "Invalid Search Status :::" + hashTable.get(input)));
                    break;
                case AGENT_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Id", "Invalid Agent Id :::" + hashTable.get(input)));
                    break;
                case AGENT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Agent Name", "Invalid Agent Name :::" + hashTable.get(input)));
                    break;
                case PARTNER_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Partner Name", "Invalid Partner Name :::" + hashTable.get(input)));
                    break;
                case PASSWORD_FULL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Password", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + hashTable.get(input)));
                    break;
                case MERCHANTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + hashTable.get(input)));
                    break;
                case DAILY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + hashTable.get(input)));
                    break;
                case WEEKLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Amount limit", "Invalid Weekly Amount limit :::" + hashTable.get(input)));
                    break;
                case MONTHLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + hashTable.get(input)));
                    break;
                case DAILY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + hashTable.get(input)));
                    break;
                case WEEKLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + hashTable.get(input)));
                    break;
                case MONTHLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + hashTable.get(input)));
                    break;
                case DAILY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case WEEKLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case MONTHLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + hashTable.get(input)));
                    break;
                case MIN_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + hashTable.get(input)));
                    break;
                case MAX_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + hashTable.get(input)));
                    break;
                case IS_ACTIVE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Active", "Invalid Is Active :::" + hashTable.get(input)));
                    break;
                case PRIORITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Priority", "Invalid Priority :::" + hashTable.get(input)));
                    break;
                case IS_TEST:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Test", "Invalid Is Test :::" + hashTable.get(input)));
                    break;

                case CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 7, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Percentage", "Invalid Charge Percentage :::" + hashTable.get(input)));
                    break;
                case FIX_APPROVE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fix Approve Charge", "Invalid Fix Approve Charge :::" + hashTable.get(input)));
                    break;
                case FIX_DECLINE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fix Decline Charge", "Invalid Fix Decline Charge :::" + hashTable.get(input)));
                    break;
                case TAX_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tax Percentage", "Invalid Tax Percentage :::" + hashTable.get(input)));
                    break;
                case REVERSE_PERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reverse Percentage", "Invalid Reverse Percentage :::" + hashTable.get(input)));
                    break;
                case FRAUDE_VARIFICATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraude Varification Charge", "Invalid Fraude Varification Charge :::" + hashTable.get(input)));
                    break;
                case ANNUAL_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Annual Charge", "Invalid Annual Charge :::" + hashTable.get(input)));
                    break;
                case SETUP_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Setup Charge", "Invalid Setup Charge :::" + hashTable.get(input)));
                    break;
                case FX_CLERANCE_CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Forex Clearance Charge Percentage", "Invalid Forex Clearance Charge Percentage :::" + hashTable.get(input)));
                    break;
                case MONTHLY_GATEWAY_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Gateway Charge", "Invalid Monthly Gateway Charge :::" + hashTable.get(input)));
                    break;
                case MONTHLY_ACC_MN_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Monthly Account Mnt Charge", "Invalid Monthly Account Mnt Charge :::" + hashTable.get(input)));
                    break;
                case REPORT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Report Charge", "Invalid Report Charge :::" + hashTable.get(input)));
                    break;
                case FRAUDULENT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraudulent Charge", "Invalid Fraudulent Charge :::" + hashTable.get(input)));
                    break;
                case AUTO_REPRESENTATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Auto Representation Charge", "Invalid Auto Representation Charge :::" + hashTable.get(input)));
                    break;
                case INTERCHANGE_PLUS_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Interchange Plus Charge", "Invalid Interchange Plus Charge :::" + hashTable.get(input)));
                    break;
                case OLD_DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Old Display Name", "Invalid Old Display Name :::" + hashTable.get(input)));
                    break;
                case CHARGEBACK_PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + hashTable.get(input)));
                    break;
                case ISCVV_REQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + hashTable.get(input)));
                    break;
                case SEARCH_DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Domain", "Invalid Domain :::" + hashTable.get(input)));
                    break;
                case SEARCH_WORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Search Word", "Invalid Search Word :::" + hashTable.get(input)));
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid IP Address", "Invalid IP Address :::" + hashTable.get(input)));
                    break;

                case CUSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Customer Name", "Invalid Customer Name :::" + hashTable.get(input)));
                    break;
                case INVOICE_CANCEL_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Cancel Reason", "Invalid Cancel Reason :::" + hashTable.get(input)));
                    break;

                case PASSWORD2:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Password", "Invalid Password :::" + hashTable.get(input)));
                    break;
                case DATASOURCE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Datasource", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Datasource", "Invalid Datasource :::" + hashTable.get(input)));
                    break;

                case SHIPPINGPODBATCH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CSEURL", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Shipping Site", "Invalid Shipping Site :::" + hashTable.get(input)));
                    break;

                case SHIPPINGPOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                            validationErrorList.addError(input.toString(), new ValidationException("Invalid Shipping Id", "Invalid Shipping Id :::" + hashTable.get(input)));
                    break;

                case PHONENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Phone Number", "Invalid Phone No :::" + hashTable.get(input)));
                    break;

                case CSEEid:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support Exec Id", "Invalid Support Exec Id :::" + hashTable.get(input)));
                    break;

                case CSEname:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Support Exec Name", "Invalid Support Exec Name :::" + hashTable.get(input)));
                    break;

                case LASTFOURCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "LastFourcc", 4, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Last Four CC No", "Invalid Last Four CC No:::" + hashTable.get(input)));
                    break;
                case FIRSTSIXCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "FirstSixcc", 6, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid First Six CC No", "Invalid First Six CC No:::" + hashTable.get(input)));
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Card Holder Ip Address", "Invalid Card Holder Ip Address:::" + hashTable.get(input)));
                    break;
                case BRANDNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Brandname", "Invalid Brandname:::" + hashTable.get(input)));
                    break;

                case PAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isPaid", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isPaid", "Invalid isPaid:::" + hashTable.get(input)));
                    break;

                case FDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid from date", "Invalid from date:::" + hashTable.get(input)));
                    break;

                case TDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid to date", "Invalid to date:::" + hashTable.get(input)));
                    break;
                case COMMA_SEPRATED_NUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CommaSeprateNum", 500, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Tracking Id", "Invalid Tracking Id:::" + hashTable.get(input)));
                    break;

                case FILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fileName", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid File Name", "Invalid File Name:::" + hashTable.get(input)));
                    break;

                case SMALL_ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Action", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Action", "Invalid Action:::" + hashTable.get(input)));
                    break;

                case BANKRECEIVEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank SettlementCycle Id ", "Invalid Bank SettlementCycle Id:::" + hashTable.get(input)));
                    break;

                case SETTLEMENTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Date", "Invalid Settlement Date:::" + hashTable.get(input)));
                    break;

                case EXPECTED_STARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected Start Date", "Invalid Expected Start Date:::" + hashTable.get(input)));
                    break;

                case EXPECTED_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected End Date", "Invalid Expected End Date:::" + hashTable.get(input)));
                    break;

                case ACTUAL_SARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual Start Date", "Invalid Actual Start Date:::" + hashTable.get(input)));
                    break;

                case ACTUAL_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual End Date", "Invalid Actual End Date:::" + hashTable.get(input)));
                    break;

                case SETTLEMENTCYCLEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement cycle id", "Invalid Settlement cycle id:::" + hashTable.get(input)));
                    break;

                case ROLLINGRESERVEDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rolling Release Date", "Invalid Rolling Release Date:::" + hashTable.get(input)));
                    break;

                case ISSETTLEMENTCRONEXECCUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isSettlement cron executed ", "Invalid isSettlement cron executed:::" + hashTable.get(input)));
                    break;

                case ISPAYOUCRONEXECUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isPayout cron executed ", "Invalid isPayout cron executed:::" + hashTable.get(input)));
                    break;

                case ISDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Day Light ", "Invalid Day Light:::" + hashTable.get(input)));
                    break;

                case ISPAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Is Paid ", "Invalid Is Paid:::" + hashTable.get(input)));
                    break;

                case BANKMERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank merchant settlement id ", "Invalid Bank merchant settlement id:::" + hashTable.get(input)));
                    break;
                case FSID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid FraudSystem ID", "Invalid FraudSystem ID :::" + hashTable.get(input)));
                    break;
                case FSNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Fraud System Name", "Invalid Fraud System Name :::" + hashTable.get(input)));
                    break;

                case BANKWIREMANGERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Wire Manager Id", "Invalid Bank Wire Manager Id :::" + hashTable.get(input)));
                    break;
                case PARENT_BANKWIREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Parent Bank Wire Id", "Invalid Parent Bank Wire Id :::" + hashTable.get(input)));
                    break;
                case PROCESSINGAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Processing Amount", "Invalid Processing Amount :::" + hashTable.get(input)));
                    break;
                case GROSSAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Gross Amount", "Invalid Gross Amount :::" + hashTable.get(input)));
                    break;
                case NETFINALAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Net Final Amount", "Invalid Net Final Amount :::" + hashTable.get(input)));
                    break;
                case UNPAIDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Unpaid Amount", "Invalid Unpaid Amount :::" + hashTable.get(input)));
                    break;
                case ISROLLINGRESERVERELAEASEWIRE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid isRollingReserveReleaseWire", "Invalid isRollingReserveReleaseWire :::" + hashTable.get(input)));
                    break;
                case DECLINEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid DeclinedCoveredDateUpto", "Invalid DeclinedCoveredDateUpto :::" + hashTable.get(input)));
                    break;
                case CHARGEBACKCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ChargebackCoveredDateUpto", "Invalid ChargebackCoveredDateUpto :::" + hashTable.get(input)));
                    break;
                case REVERSEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ReversedCoveredDateUpto", "Invalid ReversedCoveredDateUpto :::" + hashTable.get(input)));
                    break;
                case SETTLEMENTREPORTFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "reportFile", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Report File", "Invalid Settlement Report File :::" + hashTable.get(input)));
                    break;
                case SETTLEMENTTRANSACTIONFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "transactionFile", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Transaction File", "Invalid Settlement Transaction File :::" + hashTable.get(input)));
                    break;

                case EXPECTED_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected start time", "Invalid Expected start time :::" + hashTable.get(input)));
                    break;
                case EXPECTED_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Expected end time", "Invalid Expected end time :::" + hashTable.get(input)));
                    break;
                case ACTUAL_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual start time", "Invalid Actual start time :::" + hashTable.get(input)));
                    break;
                case ACTUAL_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Actual end time", "Invalid Actual end time :::" + hashTable.get(input)));
                    break;

                case ROLLINGRELEASETIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rolling Release Time", "Invalid Rolling Release Time :::" + hashTable.get(input)));
                    break;
                case DECLINECOVERTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Decline Covered Time", "Invalid Decline Covered Time :::" + hashTable.get(input)));
                    break;

                case CHARGEBACKCOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Chargeback Covered Time", "Invalid Chargeback Covered Time :::" + hashTable.get(input)));
                    break;

                case REVERSECOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Reverse Covered Time", "Invalid Reverse Covered Time :::" + hashTable.get(input)));
                    break;

                case TIMEDIFFNORMAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 16, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Time difference Normal", "Invalid Time difference Normal:::" + hashTable.get(input)));
                    break;

                case TIMEDIFFDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 16, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Time diffence DayLight", "Invalid Time diffence DayLight:::" + hashTable.get(input)));
                    break;

                case SETTLEMENTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Settlement Time", "Invalid Settlement Time :::" + hashTable.get(input)));
                    break;
                case HIGH_RISK_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid High Risk Amount", "Invalid High Risk Amount:::" + hashTable.get(input)));
                    break;
                case MAX_SCORE_ALLOWED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max score Allowed", "Invalid Max score Allowed:::" + hashTable.get(input)));
                    break;
                case MAX_SCORE_REVERSEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Max score Reversal", "Invalid Max score Reversal:::" + hashTable.get(input)));
                    break;
                case REFUND_DAILY_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Refund daily Limit", "Invalid Refund daily Limit:::" + hashTable.get(input)));
                    break;

                case CHARGETECHNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 25, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Charge Tech Name", "Invalid Charge Tech Name:::" + hashTable.get(input)));
                    break;
                case FRAUDEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid fraud email", "Invalid fraud email:::" + hashTable.get(input)));
                    break;
                case IDEAL_BANK_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Code", "Invalid Bank Code:::" + hashTable.get(input)));
                    break;
                case BANKIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank IP Address", "Invalid Bank IP Address:::" + hashTable.get(input)));
                    break;
                case APPTOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Application", "Invalid Application:::" + hashTable.get(input)));
                    break;

                case BANK_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank", "Invalid Bank:::" + hashTable.get(input)));
                    break;

                case ARCHIVE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Archive", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Archive", "Invalid Archive:::" + hashTable.get(input)));
                    break;
                case RESERVEFIELD1:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "recurring", 20, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28)", "Invalid ReserveField1-(eg.(Y/y)|(day/month/year)|(1-31)|(1-28):::" + hashTable.get(input)));
                    break;
                case Bank_EmailID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Bank Email ID", "Invalid Bank Email ID:::" + hashTable.get(input)));

                    break;

                case RISKPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile", "Invalid Risk Profile:::" + hashTable.get(input)));

                    break;

                case RISKRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Id", "Invalid Risk Rule Id:::" + hashTable.get(input)));

                    break;

                case RISKPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile Name", "Invalid Risk Profile Name:::" + hashTable.get(input)));

                    break;

                case BUSINESSRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Id", "Invalid Business Rule Id:::" + hashTable.get(input)));

                    break;

                case BUSINESSPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid BusinessProfile Id", "Invalid BusinessProfile Id:::" + hashTable.get(input)));

                    break;

                case BUSINESSPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Profile Name", "Invalid Business Profile Name:::" + hashTable.get(input)));

                    break;

                case COUNTOFROW:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Profile Row", "Invalid Risk Profile Row:::" + hashTable.get(input)));

                    break;

                //TODO for Risk related validation 3 for all other method also.

                case RISK_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Name", "Invalid Risk Rule Name:::" + hashTable.get(input)));
                    break;

                case RISK_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Label", "Invalid Risk Rule Label:::" + hashTable.get(input)));
                    break;

                case RISK_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Risk Rule Description", "Invalid Risk Rule Description:::" + hashTable.get(input)));
                    break;

                case BUSINESS_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Name", "Invalid Business Rule Name:::" + hashTable.get(input)));
                    break;

                case BUSINESS_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Label", "Invalid Business Rule Label:::" + hashTable.get(input)));
                    break;

                case BUSINESS_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Description", "Invalid Business Rule Description:::" + hashTable.get(input)));
                    break;

                case BUSINESS_RULE_OPERATOR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Rule Operator", "Invalid Business Rule Operator:::" + hashTable.get(input)));
                    break;

                case USERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid User Profile Id", "Invalid User Profile Id:::" + hashTable.get(input)));

                    break;

                case ONLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Online Processing URL", "Invalid Online Processing URL::::" + hashTable.get(input)));

                    break;
                case OFFLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Offline Processing URL", "Invalid Offline Processing URL:::" + hashTable.get(input)));

                    break;
                case ONLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Online Threshold", "Invalid Online Threshold:::" + hashTable.get(input)));

                    break;
                case OFFLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Offline Threshold", "Invalid Offline Threshold:::" + hashTable.get(input)));

                    break;
                case DEFAULTMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Default Mode", "Invalid Default Mode:::" + hashTable.get(input)));

                    break;
                case BUSINESSPROFILE_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Business Profile", "Invalid Business Profile:::" + hashTable.get(input)));

                    break;
                case BACKGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Background color", "Invalid Background color:::" + hashTable.get(input)));

                    break;
                case FOREGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Foreground color", "Invalid Foreground color:::" + hashTable.get(input)));

                    break;
                case FONT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Font color", "Invalid Font color:::" + hashTable.get(input)));

                    break;
                case MERCHANTLOGO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Merchant Logo", "Invalid Merchant Logo:::" + hashTable.get(input)));

                    break;

                case CONSOLIDATEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid CONSOLIDATED ID", "Invalid CONSOLIDATED ID:::" + hashTable.get(input)));

                    break;

                case BUSINESS_RULE_OPERATOR_QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid Business Rule Operator","Invalid Business Rule Operator:::"+hashTable.get(input)));
                    break;

                case RULETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 50, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Rule Type","Invalid Rule Type:::"+hashTable.get(input)));

                    break;

                case QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 1000, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Query","Invalid Query:::"+hashTable.get(input)));

                    break;

                case REGEX:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 255, isOptional))
                        validationErrorList.addError(input.toString(), new ValidationException("Invalid Regular Expression","Invalid Regular Expression:::"+hashTable.get(input)));

                    break;

                default:
                    break;

            }
        }
    }

    /**
     * hashTable will contain the  Enums as key for  input fields and field value
     * This method will throw validationException
     *
     * @param hashTable
     * @param isOptional
     * @throws ValidationException
     */
    public void InputValidations(Hashtable<InputFields, String> hashTable, boolean isOptional) throws ValidationException
    {
        for (InputFields input : hashTable.keySet())
        {
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid TOID/Merchant Id", "Invalid TOID/Merchant Id :::" + hashTable.get(input));
                    break;
                case TOTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid TOTYPE", "Invalid TOTYPE:::" + hashTable.get(input));
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Amount", 12, isOptional))
                        throw new ValidationException("Invalid Amount. It accepts Only Numeric value", "Invalid Amount :::" + hashTable.get(input));
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 100, isOptional))
                        throw new ValidationException("Invalid Order Id/Description", "Invalid Order Id/Description :::" + hashTable.get(input));
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 255, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid OrderDescription", "Invalid OrderDescription :::" + hashTable.get(input));
                    break;
                case TMPL_EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input));
                    break;
                case TMPL_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input));
                    break;
                case TMPL_STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Street", "Invalid Street :::" + hashTable.get(input));
                    break;
                case TMPL_ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input));
                    break;
                case TMPL_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input));
                    break;
                case TMPL_TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 15, isOptional))
                        throw new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + hashTable.get(input));
                    break;
                case TMPL_TELNOCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 4, isOptional))
                        throw new ValidationException("Invalid Telno", "Invalid Telno :::" + hashTable.get(input));
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Amount", 12, isOptional))
                        throw new ValidationException("Invalid Amount", "Invalid Amount :::" + hashTable.get(input));
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 3, isOptional))
                        throw new ValidationException("Invalid Currency", "Invalid Currency :::" + hashTable.get(input));
                    break;
                case TMPL_COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Country", "Invalid Country :::" + hashTable.get(input));
                    break;
                case INVOICE_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Invoice No", "Invalid Invoice No :::" + hashTable.get(input));
                    break;

                //Add here
                case MID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid MID", "Invalid MID :::" + hashTable.get(input));
                    break;
                case OID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 30, isOptional))
                        throw new ValidationException("Invalid OrderId", "Invalid OrderId:::" + hashTable.get(input));
                    break;
                case TOTAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 10, isOptional))
                        throw new ValidationException("Invalid Amount Or Enter Amount with 2 Digit Decimal", "Invalid Amount :::" + hashTable.get(input));
                    break;
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 12, isOptional))
                        throw new ValidationException("Invalid CardType", "Invalid CardType :::" + hashTable.get(input));
                    break;
                case BITKEY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid BITKey", "Invalid BITKey :::" + hashTable.get(input));
                    break;
                case CHECKSUMALGORITHM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid ChecksumAlgoritham", "Invalid ChecksumAlgoritham :::" + hashTable.get(input));
                    break;
                case B_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input));
                    break;
                case B_CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input));
                    break;
                case B_ZIPCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input));
                    break;
                case B_STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input));
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CountryCode", 3, isOptional))
                        throw new ValidationException("Invalid CountryCode", "Invalid CountryCode :::" + hashTable.get(input));
                    break;
                case PHONE1:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 30, isOptional))
                        throw new ValidationException("Invalid Phone", "Invalid Phone :::" + hashTable.get(input));
                    break;
                case PHONE2:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 30, isOptional))
                        throw new ValidationException("Invalid Phone", "Invalid Phone :::" + hashTable.get(input));
                    break;
                case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 2, isOptional))
                        throw new ValidationException("Invalid PaymentType", "Invalid PaymentType :::" + hashTable.get(input));
                    break;
                case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 6, isOptional))
                        throw new ValidationException("Invalid Terminal ID", "Invalid Terminal ID :::" + hashTable.get(input));
                    break;
                //Add here
                case TOID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid TOID", "Invalid TOID :::" + hashTable.get(input));
                    break;
                case DESCRIPTION_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input));
                    break;
                case TRACKINGID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input));
                    break;
                case ACCOUNTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid AccountId", "Invalid AccountId :::" + hashTable.get(input));
                    break;
                //Add here
                case LANGUAGE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Language", "Invalid Language :::" + hashTable.get(input));
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Country", "Invalid Country :::" + hashTable.get(input));
                    break;
                case DAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 2, isOptional))
                        throw new ValidationException("Invalid Day", "Invalid Day :::" + hashTable.get(input));
                    break;
                case MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 2, isOptional))
                        throw new ValidationException("Invalid Month", "Invalid Month :::" + hashTable.get(input));
                    break;
                case YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid Year", "Invalid Year :::" + hashTable.get(input));
                    break;
                case SSN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid SSN", "Invalid SSN :::" + hashTable.get(input));
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid FirstName", "Invalid FirstName :::" + hashTable.get(input));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid LastName", "Invalid LastName :::" + hashTable.get(input));
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input));
                    break;
                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CC", 19, isOptional))
                        throw new ValidationException("Invalid PAN", "Invalid PAN :::" + hashTable.get(input));
                    break;
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid CVV", "Invalid CVV :::" + hashTable.get(input));
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 100, isOptional))
                        throw new ValidationException("Invalid Address/Street", "Invalid Address/Street :::" + hashTable.get(input));
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "City", 30, isOptional))
                        throw new ValidationException("Invalid City", "Invalid City :::" + hashTable.get(input));
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 30, isOptional))
                        throw new ValidationException("Invalid State", "Invalid State :::" + hashTable.get(input));
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Zip", 10, isOptional))
                        throw new ValidationException("Invalid Zip", "Invalid Zip :::" + hashTable.get(input));
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 15, isOptional))
                        throw new ValidationException("Invalid Telephone Number", "Invalid Telephone Number :::" + hashTable.get(input));
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 5, isOptional))
                        throw new ValidationException("Invalid TelCC", "Invalid TelCC :::" + hashTable.get(input));
                    break;
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Expire Month", "Invalid Expiry Month :::" + hashTable.get(input));
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Expire Year", "Invalid Expire Year :::" + hashTable.get(input));
                    break;
                case NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input));
                    break;
                case CCCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid CCP", "Invalid CCP :::" + hashTable.get(input));
                    break;
                case ADDRCP:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input));
                    break;
                case CARDTYPE_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 12, isOptional))
                        throw new ValidationException("Invalid Cardtype", "Invalid Cardtype :::" + hashTable.get(input));
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input));
                    break;
                case TRACKING_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input));
                    break;
                case TRANS_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Transaction Status", "Invalid Transaction Status :::" + hashTable.get(input));
                    break;
                case CARDHOLDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Cardholder", "Invalid Cardholder :::" + hashTable.get(input));
                    break;
                case NAME_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input));
                    break;
                case STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Status", "Invalid Status :::" + hashTable.get(input));
                    break;
                case MESSAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Message", "Invalid Message :::" + hashTable.get(input));
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Checksum", "Invalid Checksum :::" + hashTable.get(input));
                    break;
                case TRACKING_ID_REF:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid Reference", "Invalid Reference :::" + hashTable.get(input));
                    break;
                case CHECKSUM_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Checksum", "Invalid Checksum :::" + hashTable.get(input));
                    break;
                case TMPL_COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CountryCode", 3, isOptional))
                        throw new ValidationException("Invalid Currency code", "Invalid Currency code:::" + hashTable.get(input));
                    break;
                case ORDER_DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Address", 255, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Order Description", "Invalid Order Description :::" + hashTable.get(input));
                    break;
                //Add here
                case USERNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "alphanum", 50, isOptional))
                        throw new ValidationException("Invalid User Name", "Invalid User Name :::" + hashTable.get(input));
                    break;
                case PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password :::" + hashTable.get(input));
                    break;
                case COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Company Name", "Invalid Company Name :::" + hashTable.get(input));
                    break;
                case CONTACT_PERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Contact_Person", "Invalid Contact_Person :::" + hashTable.get(input));
                    break;
                case CONTACT_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Contact_Email", "Invalid Contact_Email :::" + hashTable.get(input));
                    break;
                case SITENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 100, isOptional))
                        throw new ValidationException("Invalid Sitename", "Invalid Sitename :::" + hashTable.get(input));
                    break;
                case SUPPORT_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Support_Mail_Id", "Invalid Support_Mail_Id :::" + hashTable.get(input));
                    break;
                case ADMIN_MAIL_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Admin_Mail_Id", "Invalid Admin_Mail_Id :::" + hashTable.get(input));
                    break;
                case SUPPORT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Support_URL", "Invalid Support_URL :::" + hashTable.get(input));
                    break;
                case HOST_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "HostURL", 255, isOptional))
                        throw new ValidationException("Invalid Host URL", "Invalid Host URL :::" + hashTable.get(input));
                    break;
                case SALES_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Sales_Email", "Invalid Sales_Email :::" + hashTable.get(input));
                    break;
                case BILLING_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Billing_Email", "Invalid Billing_Email :::" + hashTable.get(input));
                    break;
                case NOTIFY_EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Notify_Email", "Invalid Notify_Email :::" + hashTable.get(input));
                    break;
                case COMPANY_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Company_From_Address", "Invalid Company_From_Address :::" + hashTable.get(input));
                    break;
                case SUPPORT_FROM_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 100, isOptional))
                        throw new ValidationException("Invalid Support_From_Address", "Invalid Support_From_Address :::" + hashTable.get(input));
                    break;
                case SMTP_HOST:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_HOST", "Invalid SMTP_HOST :::" + hashTable.get(input));
                    break;
                case SMTP_PORT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_PORT", "Invalid SMTP_PORT :::" + hashTable.get(input));
                    break;
                case SMTP_USER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_USER", "Invalid SMTP_USER :::" + hashTable.get(input));
                    break;
                case SMTP_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid SMTP_Password", "Invalid SMTP_Password :::" + hashTable.get(input));
                    break;
                case PARTNERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + hashTable.get(input));
                    break;
                case CARDNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CC", 20, isOptional))
                        throw new ValidationException("Invalid Card Number", "Invalid Card Number :::" + hashTable.get(input));
                    break;
                case CVV_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CVV", 4, isOptional))
                        throw new ValidationException("Invalid CVV", "Invalid CVV :::" + hashTable.get(input));
                    break;
                case EXPIRY_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Expiry Month", "Invalid Expiry Month :::" + hashTable.get(input));
                    break;
                case EXPIRY_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Expiry Year", "Invalid Expiry Year :::" + hashTable.get(input));
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 8, isOptional) || !Functions.isValidDate(hashTable.get(input)))
                        throw new ValidationException("Invalid Birth Date", "Invalid Birth Date :::" + hashTable.get(input));
                    break;
                case LANGUAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 3, isOptional))
                        throw new ValidationException("Invalid Language", "Invalid Language :::" + hashTable.get(input));
                    break;
                case TRACKINGID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 140, isOptional))
                        throw new ValidationException("Invalid TrackingId", "Invalid TrackingId :::" + hashTable.get(input));
                    break;
                case CAPTUREAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + hashTable.get(input.toString()));
                    break;
                case REFUNDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input.toString()), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Amount", "Invalid Capture Amount :::" + hashTable.get(input.toString()));
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input.toString()), "Description", 50, isOptional))
                        throw new ValidationException("Invalid Reason", "Invalid Reason :::" + hashTable.get(input.toString()));
                    break;

                case MIDDLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Middle Name", "Invalid Middle Name :::" + hashTable.get(input));
                    break;
                case IPADDRESS:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid IP Address", "Invalid IP Address :::" + hashTable.get(input));
                    break;
                case MERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + hashTable.get(input));
                    break;
                case AGENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Agent ID", "Invalid Agent ID :::" + hashTable.get(input));
                    break;
                //validation starts for ICICI Module
                case MEMBERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid Merchant Id", "Invalid Merchant Id :::" + hashTable.get(input));
                    break;
                case ACTIVATION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Statues", 10, isOptional))
                        throw new ValidationException("Invalid Activation", "Invalid Activation :::" + hashTable.get(input));
                    break;
                case ICICI:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Statues", 10, isOptional))
                        throw new ValidationException("Invalid ICICI", "Invalid ICICI :::" + hashTable.get(input));
                    break;
                case RESERVES:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Reserves", "Invalid Reserves :::" + hashTable.get(input));
                    break;
                case CHARGEPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Chargeper", "Invalid Chargeper :::" + hashTable.get(input));
                    break;
                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Days", 2, isOptional))
                        throw new ValidationException("Invalid From Date", "Invalid From Date :::" + hashTable.get(input));
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Days", 2, isOptional))
                        throw new ValidationException("Invalid To Date", "Invalid To Date :::" + hashTable.get(input));
                    break;
                case FROMMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        throw new ValidationException("Invalid From Month", "Invalid From Month :::" + hashTable.get(input));
                    break;
                case TOMONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        throw new ValidationException("Invalid To Month", "Invalid To Month :::" + hashTable.get(input));
                    break;
                case TOYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        throw new ValidationException("Invalid To Year", "Invalid To Year :::" + hashTable.get(input));
                    break;
                case FROMYEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        throw new ValidationException("Invalid From Year", "Invalid From Year :::" + hashTable.get(input));
                    break;
                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Page No", "Invalid Page No :::" + hashTable.get(input));
                    break;
                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Records", "Invalid Records :::" + hashTable.get(input));
                    break;
                case DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 50, isOptional))
                        throw new ValidationException("Invalid Domain", "Invalid Domain :::" + hashTable.get(input));
                    break;
                case NAME_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Name", "Invalid Name :::" + hashTable.get(input));
                    break;
                case DESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input));
                    break;
                case ORDERDESC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 40, isOptional))
                        throw new ValidationException("Invalid Order Desc", "Invalid Order Desc :::" + hashTable.get(input));
                    break;
                case TRACKINGID_TRA:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + hashTable.get(input));
                    break;
                case GATEWAY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Gateway", "Invalid Gateway :::" + hashTable.get(input));
                    break;
                case YEAR_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Years", 4, isOptional))
                        throw new ValidationException("Invalid Year", "Invalid Year :::" + hashTable.get(input));
                    break;
                case MONTH_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Months", 2, isOptional))
                        throw new ValidationException("Invalid Month", "Invalid Month :::" + hashTable.get(input));
                    break;
                case COMPANY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Company", "Invalid Company :::" + hashTable.get(input));
                    break;
                case ACCOUNTID_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid AccountID", "Invalid AccountID :::" + hashTable.get(input));
                    break;
                case FIRSTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 6, isOptional))
                        throw new ValidationException("Invalid First Six digit of Card Number", "Invalid First Six digit of Card Number :::" + hashTable.get(input));
                    break;
                case LASTFOURCCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 4, isOptional))
                        throw new ValidationException("Invalid Last Four digit of Card Number", "Invalid Last Four digit of Card Number :::" + hashTable.get(input));
                    break;
                case OLD_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Password", 20, isOptional))
                        throw new ValidationException("Invalid Old Password", "Invalid Old Password :::" + hashTable.get(input));
                    break;
                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid New Password", "Invalid New Password :::" + hashTable.get(input));
                    break;
                case CONFIRM_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Confirm Password", "Invalid Confirm Password :::" + hashTable.get(input));
                    break;
                case MAILTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Mailtype", 10, isOptional))
                        throw new ValidationException("Invalid Mail Type", "Invalid Mail Type :::" + hashTable.get(input));
                    break;
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Login", "Invalid Login :::" + hashTable.get(input));
                    break;
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid OrderID", "Invalid OrderID :::" + hashTable.get(input));
                    break;
                case INVOICENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + hashTable.get(input));
                    break;
                case INV:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid InvoiceNO", "Invalid InvoiceNO :::" + hashTable.get(input));
                    break;
                case FROMID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid Fromid", "Invalid Fromid :::" + hashTable.get(input));
                    break;
                case PAYMENTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid PaymentNumber", "Invalid PaymentNumber :::" + hashTable.get(input));
                    break;
                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid PaymentId", "Invalid PaymentId :::" + hashTable.get(input));
                    break;
                case CCNUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Card Number", "Invalid Card Number :::" + hashTable.get(input));
                    break;
                case FIRST_SIX:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid First Six digit of Card", "Invalid First Six digit of Card :::" + hashTable.get(input));
                    break;
                case LAST_FOUR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Last Four digit of Card", "Invalid Last Four digit of Card :::" + hashTable.get(input));
                    break;
                case CHARGEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid ChargrId", "Invalid ChargeId :::" + hashTable.get(input));
                    break;
                case CHARGENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Charge Name", "Invalid Charge Name :::" + hashTable.get(input));
                    break;
                case ISINPUTREQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 5, isOptional))
                        throw new ValidationException("Invalid Is Input Required", "Invalid Is Input Required :::" + hashTable.get(input));
                    break;
                case MAPPINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid MappingId", "Invalid MappingId :::" + hashTable.get(input));
                    break;
                case PAYMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Paymode", "Invalid Paymode :::" + hashTable.get(input));
                    break;
                case CHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Chargr Value", "Invalid Chargr Value :::" + hashTable.get(input));
                    break;
                case CHARGETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Chargr Type", "Invalid Charge Type :::" + hashTable.get(input));
                    break;

                case MIN_PAYOUT_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Min Payout Amount", "Invalid Min Payout Amount:::" + hashTable.get(input));
                    break;
                case MCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Merchant Charge Value", "Invalid Merchant Charge Value :::" + hashTable.get(input));
                    break;
                case ACHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Agent Charge Value", "Invalid Agent Charge Value :::" + hashTable.get(input));
                    break;
                case PCHARGEVALUE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountStr", 20, isOptional))
                        throw new ValidationException("Invalid Partner Charge Value", "Invalid Partner Charge Value :::" + hashTable.get(input));
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 3, isOptional))
                        throw new ValidationException("Invalid Currency", "Invalid Currency :::" + hashTable.get(input));
                    break;
                case PGTYPEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PayGatewayId", "Invalid PayGatewayId :::" + hashTable.get(input));
                    break;
                case ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Action", "Invalid Action :::" + hashTable.get(input));
                    break;
                case CHARGEPERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid ChargePercentage", "Invalid ChargePercentage :::" + hashTable.get(input));
                    break;
                case TAXPER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid TaxPercentage", "Invalid TaxPercentage :::" + hashTable.get(input));
                    break;
                case WITHDRAWCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid WITHDRAWCHARGE", "Invalid WITHDRAWCHARGE :::" + hashTable.get(input));
                    break;
                case REVERSECHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid Reversal Charge", "Invalid Reversal Charge :::" + hashTable.get(input));
                    break;
                case CHARGEBACKCHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid CHARGEBACKCHARGE", "Invalid CHARGEBACKCHARGE :::" + hashTable.get(input));
                    break;
                case CHARGESACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid CHARGESACCOUNT", "Invalid CHARGESACCOUNT :::" + hashTable.get(input));
                    break;
                case TAXACCOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid TAXACCOUNT", "Invalid TAXACCOUNT :::" + hashTable.get(input));
                    break;
                case HIGHRISKAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 10, isOptional))
                        throw new ValidationException("Invalid HIGHRISKAMOUNT", "Invalid HIGHRISKAMOUNT :::" + hashTable.get(input));
                    break;
                case ADDRESS_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input));
                    break;
                case GATEWAY_TABLE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Address", "Invalid Address :::" + hashTable.get(input));
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid PartnerId", "Invalid PartnerId :::" + hashTable.get(input));
                    break;
                case LOGONAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Logo Name", "Invalid Logo Name :::" + hashTable.get(input));
                    break;

                case ALIASNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Alias Name", "Invalid Alias Name :::" + hashTable.get(input));
                    break;
                case DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Display Name", "Invalid Display Name :::" + hashTable.get(input));
                    break;
                case ISMASTERCARDSUPPORTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Mastercard Supported", "Invalid Is Mastercard Supported :::" + hashTable.get(input));
                    break;
                case SHORTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Short Name", "Invalid Short Name :::" + hashTable.get(input));
                    break;
                case SITE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 255, isOptional))
                        throw new ValidationException("Invalid Site", "Invalid Site :::" + hashTable.get(input));
                    break;
                case PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Path", "Invalid Path :::" + hashTable.get(input));
                    break;
                case CHARGEBACKPATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + hashTable.get(input));
                    break;
                case ISCVVREQUIRED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + hashTable.get(input));
                    break;
                case MONTHLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 5, isOptional))
                        throw new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + hashTable.get(input));
                    break;
                case DAILYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + hashTable.get(input));
                    break;
                case MONTHLYAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + hashTable.get(input));
                    break;
                case DAILYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + hashTable.get(input));
                    break;
                case WEEKLYCARDLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + hashTable.get(input));
                    break;
                case MINTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + hashTable.get(input));
                    break;
                case MAXTRANSACTIONLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + hashTable.get(input));
                    break;
                case DAILYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + hashTable.get(input));
                    break;
                case WEEKLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + hashTable.get(input));
                    break;
                case MONTHLYCARDAMOUNTLIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 10, isOptional))
                        throw new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + hashTable.get(input));
                    break;
                case COLUMN_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 250, isOptional))
                        throw new ValidationException("Invalid Column Name", "Invalid Column Name :::" + hashTable.get(input));
                    break;
                case GATEWAY_TABLENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Gateway Table Name", "Invalid Gateway Table Name :::" + hashTable.get(input));
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1055, isOptional))
                        throw new ValidationException("Invalid Redirect URL", "Invalid Redirect URL :::" + hashTable.get(input));
                    break;
                case NOTIFICATIONURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Notification URL", "Invalid Notification URL :::" + hashTable.get(input));
                    break;
                case SEARCH_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Search Type", "Invalid Search Type :::" + hashTable.get(input));
                    break;
                case SEARCH_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 15, isOptional))
                        throw new ValidationException("Invalid Search Id", "Invalid Search Id :::" + hashTable.get(input));
                    break;
                case GATEWAY_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Gateway Type", "Invalid Gateway Type :::" + hashTable.get(input));
                    break;
                case SDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Description", "Invalid Description :::" + hashTable.get(input));
                    break;
                case STRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Tracking Id", "Invalid Tracking Id :::" + hashTable.get(input));
                    break;
                case SCAPTUREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Capture Id", "Invalid Capture Id :::" + hashTable.get(input));
                    break;
                case ICICITRANSEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid ICICI Transaction Id", "Invalid ICICI Transaction Id :::" + hashTable.get(input));
                    break;
                case REFUNDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Id", "Invalid Refund Id :::" + hashTable.get(input));
                    break;
                case REFUND_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Code", "Invalid Refund Code :::" + hashTable.get(input));
                    break;
                case REFUND_RECEIPT_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund Receipt No", "Invalid Refund Receipt No :::" + hashTable.get(input));
                    break;
                case EMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        throw new ValidationException("Invalid EmailAddress", "Invalid EmailAddress :::" + hashTable.get(input));
                    break;
                case SEARCHTYPE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Search Type", "Invalid Search Type :::" + hashTable.get(input));
                    break;
                case DATE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "date", 30, isOptional))
                        throw new ValidationException("Invalid Date", "Invalid Date:::" + hashTable.get(input));
                    break;
                case CB_REF_NO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid CB Ref No", "Invalid CB Ref No :::" + hashTable.get(input));
                    break;
                case CB_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid CB Amount", "Invalid CB Amount :::" + hashTable.get(input));
                    break;
                case CB_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 100, isOptional))
                        throw new ValidationException("Invalid CB Reason", "Invalid CB Reason :::" + hashTable.get(input));
                    break;
                case SEARCH_STATUS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 35, isOptional))
                        throw new ValidationException("Invalid Search Status", "Invalid Search Status :::" + hashTable.get(input));
                    break;
                case AGENT_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Agent Id", "Invalid Agent Id :::" + hashTable.get(input));
                    break;
                case AGENT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Agent Name", "Invalid Agent Name :::" + hashTable.get(input));
                    break;
                case PARTNER_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Partner Name", "Invalid Partner Name :::" + hashTable.get(input));
                    break;
                case PASSWORD_FULL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Password", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password :::" + hashTable.get(input));
                    break;
                case MERCHANTID_CAPS:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Merchant ID", "Invalid Merchant ID :::" + hashTable.get(input));
                    break;
                case DAILY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Daily Amount limit", "Invalid Daily Amount limit :::" + hashTable.get(input));
                    break;
                case WEEKLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Weekly Amount limit", "Invalid Weekly Amount limit :::" + hashTable.get(input));
                    break;
                case MONTHLY_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 100, isOptional))
                        throw new ValidationException("Invalid Monthly Amount limit", "Invalid Monthly Amount limit :::" + hashTable.get(input));
                    break;
                case DAILY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Daily Card limit", "Invalid Daily Card limit :::" + hashTable.get(input));
                    break;
                case WEEKLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Weekly Card limit", "Invalid Weekly Card limit :::" + hashTable.get(input));
                    break;
                case MONTHLY_CARD_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Monthly card limit", "Invalid Monthly card limit :::" + hashTable.get(input));
                    break;
                case DAILY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Daily Card Amount Limit", "Invalid Daily Card Amount Limit :::" + hashTable.get(input));
                    break;
                case WEEKLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Weekly Card Amount Limit", "Invalid Weekly Card Amount Limit :::" + hashTable.get(input));
                    break;
                case MONTHLY_CARD_AMT_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Monthly Card Amount Limit", "Invalid Monthly Card Amount Limit :::" + hashTable.get(input));
                    break;
                case MIN_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Min Transaction Amount", "Invalid Min Transaction Amount :::" + hashTable.get(input));
                    break;
                case MAX_TRANSE_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max Transaction Amount", "Invalid Max Transaction Amount :::" + hashTable.get(input));
                    break;
                case IS_ACTIVE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Active", "Invalid Is Active :::" + hashTable.get(input));
                    break;
                case PRIORITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 1, isOptional))
                        throw new ValidationException("Invalid Priority", "Invalid Priority :::" + hashTable.get(input));
                    break;
                case IS_TEST:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is Test", "Invalid Is Test :::" + hashTable.get(input));
                    break;

                case CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 7, isOptional))
                        throw new ValidationException("Invalid Charge Percentage", "Invalid Charge Percentage :::" + hashTable.get(input));
                    break;
                case FIX_APPROVE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fix Approve Charge", "Invalid Fix Approve Charge :::" + hashTable.get(input));
                    break;
                case FIX_DECLINE_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fix Decline Charge", "Invalid Fix Decline Charge :::" + hashTable.get(input));
                    break;
                case TAX_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Tax Percentage", "Invalid Tax Percentage :::" + hashTable.get(input));
                    break;
                case REVERSE_PERCENTAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Reverse Percentage", "Invalid Reverse Percentage :::" + hashTable.get(input));
                    break;
                case FRAUDE_VARIFICATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fraude Varification Charge", "Invalid Fraude Varification Charge :::" + hashTable.get(input));
                    break;
                case ANNUAL_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Annual Charge", "Invalid Annual Charge :::" + hashTable.get(input));
                    break;
                case SETUP_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Setup Charge", "Invalid Setup Charge :::" + hashTable.get(input));
                    break;
                case FX_CLERANCE_CHARGE_PER:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Forex Clearance Charge Percentage", "Invalid Forex Clearance Charge Percentage :::" + hashTable.get(input));
                    break;
                case MONTHLY_GATEWAY_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Monthly Gateway Charge", "Invalid Monthly Gateway Charge :::" + hashTable.get(input));
                    break;
                case MONTHLY_ACC_MN_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Monthly Account Mnt Charge", "Invalid Monthly Account Mnt Charge :::" + hashTable.get(input));
                    break;
                case REPORT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Report Charge", "Invalid Report Charge :::" + hashTable.get(input));
                    break;
                case FRAUDULENT_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Fraudulent Charge", "Invalid Fraudulent Charge :::" + hashTable.get(input));
                    break;
                case AUTO_REPRESENTATION_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Auto Representation Charge", "Invalid Auto Representation Charge :::" + hashTable.get(input));
                    break;
                case INTERCHANGE_PLUS_CHARGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 5, isOptional))
                        throw new ValidationException("Invalid Interchange Plus Charge", "Invalid Interchange Plus Charge :::" + hashTable.get(input));
                    break;
                case OLD_DISPLAYNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 25, isOptional))
                        throw new ValidationException("Invalid Old Display Name", "Invalid Old Display Name :::" + hashTable.get(input));
                    break;
                case CHARGEBACK_PATH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Cargeback Path", "Invalid Cargeback Path :::" + hashTable.get(input));
                    break;
                case ISCVV_REQ:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 1, isOptional))
                        throw new ValidationException("Invalid Is CVV Required", "Invalid Is CVV Required :::" + hashTable.get(input));
                    break;
                case SEARCH_DOMAIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "URL", 100, isOptional))
                        throw new ValidationException("Invalid Domain", "Invalid Domain :::" + hashTable.get(input));
                    break;
                case SEARCH_WORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Search Word", "Invalid Search Word :::" + hashTable.get(input));
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid IP Address", "Invalid IP Address :::" + hashTable.get(input));
                    break;
                case CUSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Customer Name", "Invalid Customer Name :::" + hashTable.get(input));
                    break;
                case INVOICE_CANCEL_REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Description", 50, isOptional))
                        throw new ValidationException("Invalid Cancel Reason", "Invalid Cancel Reason :::" + hashTable.get(input));
                    break;
                case PASSWORD2:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "NewPassword", 20, isOptional))
                        throw new ValidationException("Invalid Password", "Invalid Password:::" + hashTable.get(input));
                    break;
                case DATASOURCE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Datasource", 20, isOptional))
                        throw new ValidationException("Invalid Datasource", "Invalid Datasource :::" + hashTable.get(input));
                    break;
                case SHIPPINGPODBATCH:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CSEURL", 100, isOptional))
                        throw new ValidationException("Invalid Shipping Site", "Invalid Shipping Site :::" + hashTable.get(input));
                    break;
                case SHIPPINGPOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Shipping Id", "Invalid Shipping Id :::" + hashTable.get(input));
                    break;
                case PHONENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Phone", 20, isOptional))
                        throw new ValidationException("Invalid Phone Number", "Invalid Phone Number :::" + hashTable.get(input));
                    break;
                case CSEEid:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Support Exec Id", "Invalid Support Exec Id :::" + hashTable.get(input));
                    break;
                case CSEname:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 20, isOptional))
                        throw new ValidationException("Invalid Support Exec Name", "Invalid Support Exec Name :::" + hashTable.get(input));
                    break;
                case LASTFOURCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "LastFourcc", 4, isOptional))
                        throw new ValidationException("Invalid Last Four CC No", "Invalid Last Four CC No:::" + hashTable.get(input));
                    break;
                case FIRSTSIXCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "FirstSixcc", 6, isOptional))
                        throw new ValidationException(" invalid First Six CC No", "Invalid First Six CC No:::" + hashTable.get(input));
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Card Holder IpAddress", "Invalid Card Holder IpAddress:::" + hashTable.get(input));
                    break;
                case BRANDNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 30, isOptional))
                        throw new ValidationException("Invalid Brandname", "Invalid Brandname:::" + hashTable.get(input));
                    break;

                case PAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isPaid", 30, isOptional))
                        throw new ValidationException("Invalid isPaid", "Invalid isPaid:::" + hashTable.get(input));
                    break;

                case FDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 10, isOptional))
                        throw new ValidationException("Invalid from date", "Invalid from date:::" + hashTable.get(input));
                    break;

                case TDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 10, isOptional))
                        throw new ValidationException("Invalid to date", "Invalid to date:::" + hashTable.get(input));
                    break;
                case COMMA_SEPRATED_NUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "CommaSeprateNum", 500, isOptional))
                        throw new ValidationException("Invalid Tracking Id", "Invalid Tracking Id:::" + hashTable.get(input));
                    break;

                case FILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fileName", 255, isOptional))
                        throw new ValidationException("Invalid File Name", "Invalid File Name:::" + hashTable.get(input));
                    break;

                case SMALL_ACTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Action", 100, isOptional))
                        throw new ValidationException("Invalid Action", "Invalid Action:::" + hashTable.get(input));
                    break;

                case BANKRECEIVEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 25, isOptional))
                        throw new ValidationException("Invalid Bank SettlementCycle Id ", "Invalid Bank SettlementCycle Id:::" + hashTable.get(input));
                    break;

                case SETTLEMENTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Settlement Date", "Invalid Settlement Date:::" + hashTable.get(input));
                    break;

                case EXPECTED_STARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Expected Start Date", "Invalid Expected Start Date:::" + hashTable.get(input));
                    break;

                case EXPECTED_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Expected End Date", "Invalid Expected End Date:::" + hashTable.get(input));
                    break;

                case ACTUAL_SARTDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Actual Start Date", "Invalid Actual Start Date:::" + hashTable.get(input));
                    break;

                case ACTUAL_ENDDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Actual End Date", "Invalid Actual End Date:::" + hashTable.get(input));
                    break;

                case SETTLEMENTCYCLEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 25, isOptional))
                        throw new ValidationException("Invalid Settlement cycle id", "Invalid Settlement cycle id:::" + hashTable.get(input));
                    break;

                case ROLLINGRESERVEDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 25, isOptional))
                        throw new ValidationException("Invalid Rolling Release Date", "Invalid Rolling Release Date:::" + hashTable.get(input));
                    break;

                case ISSETTLEMENTCRONEXECCUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid isSettlement cron executed ", "Invalid isSettlement cron executed:::" + hashTable.get(input));
                    break;

                case ISPAYOUCRONEXECUTED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid isPayout cron executed ", "Invalid isPayout cron executed:::" + hashTable.get(input));
                    break;

                case ISDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid Day Light ", "Invalid Day Light:::" + hashTable.get(input));
                    break;
                case ISPAID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 1, isOptional))
                        throw new ValidationException("Invalid Is Paid ", "Invalid Is Paid:::" + hashTable.get(input));
                    break;

                case BANKMERCHANTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Bank merchant settlement id ", "Invalid Bank merchant settlement id:::" + hashTable.get(input));
                    break;
                case FSID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid FraudSystem ID", "Invalid FraudSystem ID :::" + hashTable.get(input));
                    break;
                case FSNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Fraud System Name", "Invalid Fraud System Name :::" + hashTable.get(input));
                    break;

                case BANKWIREMANGERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 50, isOptional))
                        throw new ValidationException("Invalid Bank Wire Manager Id", "Invalid Bank Wire Manager Id :::" + hashTable.get(input));
                    break;
                case PARENT_BANKWIREID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 20, isOptional))
                        throw new ValidationException("Invalid Parent Bank Wire Id", "Invalid Parent Bank Wire Id :::" + hashTable.get(input));
                    break;
                case PROCESSINGAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Processing Amount", "Invalid Processing Amount :::" + hashTable.get(input));
                    break;
                case GROSSAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Gross Amount", "Invalid Gross Amount :::" + hashTable.get(input));
                    break;
                case NETFINALAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Net Final Amount", "Invalid Net Final Amount :::" + hashTable.get(input));
                    break;
                case UNPAIDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "AmountMinus", 20, isOptional))
                        throw new ValidationException("Invalid Unpaid Amount", "Invalid Unpaid Amount :::" + hashTable.get(input));
                    break;
                case ISROLLINGRESERVERELAEASEWIRE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "isYN", 50, isOptional))
                        throw new ValidationException("Invalid isRollingReserveReleaseWire", "Invalid isRollingReserveReleaseWire :::" + hashTable.get(input));
                    break;
                case DECLINEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid DeclinedCoveredDateUpto", "Invalid DeclinedCoveredDateUpto :::" + hashTable.get(input));
                    break;
                case CHARGEBACKCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid ChargebackCoveredDateUpto", "Invalid ChargebackCoveredDateUpto :::" + hashTable.get(input));
                    break;
                case REVERSEDCOVEREDDATEUPTO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "fromDate", 50, isOptional))
                        throw new ValidationException("Invalid ReversedCoveredDateUpto", "Invalid ReversedCoveredDateUpto :::" + hashTable.get(input));
                    break;
                case SETTLEMENTREPORTFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "reportFile", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Report File", "Invalid Settlement Report File :::" + hashTable.get(input));
                    break;
                case SETTLEMENTTRANSACTIONFILE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "transactionFile", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Transaction File", "Invalid Settlement Transaction File :::" + hashTable.get(input));
                    break;

                case EXPECTED_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Expected start time", "Invalid Expected start time :::" + hashTable.get(input));
                    break;
                case EXPECTED_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Expected end time", "Invalid Expected end time :::" + hashTable.get(input));
                    break;
                case ACTUAL_STARTTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Actual start time", "Invalid Actual start time :::" + hashTable.get(input));
                    break;
                case ACTUAL_ENDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Actual end time", "Invalid Actual end time :::" + hashTable.get(input));
                    break;

                case ROLLINGRELEASETIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Rolling Release Time", "Invalid Rolling Release Time :::" + hashTable.get(input));
                    break;

                case DECLINECOVERTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Decline Covered Time", "Invalid Decline Covered Time :::" + hashTable.get(input));
                    break;

                case CHARGEBACKCOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Chargeback Covered Time", "Invalid Chargeback Covered Time :::" + hashTable.get(input));
                    break;

                case REVERSECOVEREDTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Reverse Covered Time", "Invalid Reverse Covered Time :::" + hashTable.get(input));
                    break;

                case TIMEDIFFNORMAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 16, isOptional))
                        throw new ValidationException("Invalid Time difference Normal", "Invalid Time difference Normal:::" + hashTable.get(input));
                    break;

                case TIMEDIFFDAYLIGHT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 16, isOptional))
                        throw new ValidationException("Invalid Time diffence DayLight", "Invalid Time diffence DayLight:::" + hashTable.get(input));
                    break;

                case SETTLEMENTIME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "time", 255, isOptional))
                        throw new ValidationException("Invalid Settlement Time", "Invalid Settlement Time :::" + hashTable.get(input));
                    break;
                case HIGH_RISK_AMT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid High Risk Amount", "Invalid High Risk Amount:::" + hashTable.get(input));
                    break;
                case MAX_SCORE_ALLOWED:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max score Allowed", "Invalid Max score Allowed:::" + hashTable.get(input));
                    break;
                case MAX_SCORE_REVERSEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Max score Reversal", "Invalid Max score Reversal:::" + hashTable.get(input));
                    break;
                case REFUND_DAILY_LIMIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Refund daily Limit", "Invalid Refund daily Limit:::" + hashTable.get(input));
                    break;

                case CHARGETECHNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "StrictString", 25, isOptional))
                        throw new ValidationException("Invalid Charge Tech Name", "Invalid Charge Tech Name:::" + hashTable.get(input));
                    break;
                case FRAUDEMAIL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        throw new ValidationException("Invalid fraud email", "Invalid fraud email:::" + hashTable.get(input));
                    break;
                case BANKIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "IPAddressNew", 80, isOptional)))
                        throw new ValidationException("Invalid Bank IP Address", "Invalid Bank IP Address:::" + hashTable.get(input));
                    break;
                case APPTOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Application", "Invalid Application:::" + hashTable.get(input));
                    break;

                case BANK_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Bank", "Invalid Bank:::" + hashTable.get(input));
                    break;

                case ARCHIVE_SMALL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Archive", 50, isOptional))
                        throw new ValidationException("Invalid Archive", "Invalid Archive:::" + hashTable.get(input));
                    break;
                case RBID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Numbers", 10, isOptional))
                        throw new ValidationException("Invalid Recurring Billing Id", "Invalid Recurring Billing Id:::" + hashTable.get(input));
                    break;
                case RESERVEFIELD1:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "recurring", 20, isOptional))
                        throw new ValidationException("Invalid Recurring Billing Id", "Invalid Recurring Billing Id:::" + hashTable.get(input));
                    break;
                case Bank_EmailID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "Email", 50, isOptional))
                        throw new ValidationException("Invalid Bank Email ID", "Invalid Invalid Bank Email ID:::" + hashTable.get(input));
                    break;

                case RISKPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Profile Id", "Invalid RiskProfile Id:::" + hashTable.get(input));

                    break;

                case RISKPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Risk Profile Name", "Invalid Risk Profile Name:::" + hashTable.get(input));

                    break;


                case RISKRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Rule Id", "Invalid Risk Rule Id:::" + hashTable.get(input));

                    break;

                case BUSINESSRULEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Business Rule Id", "Invalid Business Rule Id:::" + hashTable.get(input));

                    break;

                case BUSINESSPROFILEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid BusinessProfile Id", "Invalid BusinessProfile Id:::" + hashTable.get(input));

                    break;

                case BUSINESSPROFILENAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Business Profile Name", "Invalid Business Profile Name:::" + hashTable.get(input));

                    break;

                case COUNTOFROW:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Risk Profile Row", "Invalid Risk Profile Row:::" + hashTable.get(input));

                    break;

                case RISK_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Name", "Invalid Risk Rule Name:::" + hashTable.get(input));
                    break;

                case RISK_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Label", "Invalid Risk Rule Label:::" + hashTable.get(input));
                    break;

                case RISK_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Risk Rule Description", "Invalid Risk Rule Description:::" + hashTable.get(input));
                    break;

                case BUSINESS_RULE_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Name", "Invalid Business Rule Name:::" + hashTable.get(input));
                    break;

                case BUSINESS_RULE_LABEL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Label", "Invalid Business Rule Label:::" + hashTable.get(input));
                    break;

                case BUSINESS_RULE_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Description", "Invalid Business Rule Description:::" + hashTable.get(input));
                    break;

                case BUSINESS_RULE_OPERATOR:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Operator", "Invalid Business Rule Operator:::" + hashTable.get(input));
                    break;

                case USERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid User Profile Id", "Invalid User Profile Id:::" + hashTable.get(input));

                    break;

                case ONLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Online Processing URL", "Invalid Online Processing URL::::" + hashTable.get(input));

                    break;
                case OFFLINEPROCESSINGURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Offline Processing URL", "Invalid Offline Processing URL:::" + hashTable.get(input));

                    break;
                case ONLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Online Threshold", "Invalid Online Threshold:::" + hashTable.get(input));

                    break;
                case OFFLINETHRESHOLD:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Offline Threshold", "Invalid Offline Threshold:::" + hashTable.get(input));

                    break;
                case DEFAULTMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Default Mode", "Invalid Default Mode:::" + hashTable.get(input));

                    break;
                case BUSINESSPROFILE_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid Business Profile", "Invalid Business Profile:::" + hashTable.get(input));

                    break;
                case BACKGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Background color", "Invalid Background color:::" + hashTable.get(input));

                    break;
                case FOREGROUND:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Foreground color", "Invalid Foreground color:::" + hashTable.get(input));

                    break;
                case FONT:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional) || functions.hasHTMLTags(hashTable.get(input)))
                        throw new ValidationException("Invalid Font color", "Invalid Font color:::" + hashTable.get(input));

                    break;
                case MERCHANTLOGO:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Merchant Logo", "Invalid Merchant Logo:::" + hashTable.get(input));

                    break;
                case CONSOLIDATEDID:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "OnlyNumber", 50, isOptional))
                        throw new ValidationException("Invalid CONSOLIDATED ID", "Invalid CONSOLIDATED ID:::" + hashTable.get(input));

                    break;

                case BUSINESS_RULE_OPERATOR_QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input), "SafeString", 100, isOptional))
                        throw new ValidationException("Invalid Business Rule Operator","Invalid Business Rule Operator:::"+hashTable.get(input));
                    break;

                case RULETYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 50, isOptional))
                        throw new ValidationException("Invalid Rule Type","Invalid Rule Type:::"+hashTable.get(input));

                    break;

                case QUERY:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 1000, isOptional))
                        throw new ValidationException("Invalid Query","Invalid Query:::"+hashTable.get(input));

                    break;

                case REGEX:
                    if (!ESAPI.validator().isValidInput(input.toString(), hashTable.get(input),"SafeString", 255, isOptional))
                        throw new ValidationException("Invalid Regular Expression","Invalid Regular Expression:::"+hashTable.get(input));

                    break;


                default:
                    break;
            }
        }
    }

    //validation for SK,DK & VT return errorlist.
    public void InputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
        RecurringBillingVO recurringBillingVO           = new RecurringBillingVO();

        if(commonValidatorVO.getRecurringBillingVO()!=null)
        {
            recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        }
        ReserveField2VO reserveField2VO = new ReserveField2VO();

        Functions functions = new Functions();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        String resField2 = "";

        if(commonValidatorVO.getReserveField2VO()!=null)
        {
            resField2 = commonValidatorVO.getReserveField2VO().getAccountType()+"|"+commonValidatorVO.getReserveField2VO().getAccountNumber()+"|"+commonValidatorVO.getReserveField2VO().getRoutingNumber();

        }

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getMemberId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TOTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getTotype(), "SafeString", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOTYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOTYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOTYPE, ErrorMessages.INVALID_TOTYPE + ":::" + genericTransDetailsVO.getTotype(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAmount(), "Amount", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DESCRIPTION:
                    if ((functions.isValueNull(genericTransDetailsVO.getOrderId()) && genericTransDetailsVO.getOrderId().trim().equals("") ) || !ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getOrderId(), "Description", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DESCRIPTION, ErrorMessages.INVALID_DESCRIPTION + ":::" + genericTransDetailsVO.getOrderId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERDESCRIPTION:
                    if ((functions.isValueNull(genericTransDetailsVO.getOrderDesc()) && genericTransDetailsVO.getOrderDesc().trim().equals("") ) || !ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getOrderDesc(), "Address", 255, isOptional) || functions.hasHTMLTags(genericTransDetailsVO.getOrderDesc()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ORDER_DESCRIPTION, ErrorMessages.INVALID_ORDER_DESCRIPTION + ":::" + genericTransDetailsVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getRedirectUrl(), "SafeString", 1055, isOptional) /*|| genericTransDetailsVO.getRedirectUrl().length() > 255*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REDIRECT_URL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REDIRECT_URL, ErrorMessages.INVALID_REDIRECT_URL + ":::" + genericTransDetailsVO.getRedirectUrl(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case NOTIFICATIONURL:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getNotificationUrl(), "SafeString", 1055, isOptional) /*|| genericTransDetailsVO.getRedirectUrl().length() > 255*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_NOTIFICATION_URL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_NOTIFICATION_URL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_NOTIFICATION_URL, ErrorMessages.INVALID_NOTIFICATION_URL + ":::" + genericTransDetailsVO.getNotificationUrl(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getChecksum(), "alphanum", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECKSUM);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CHECKSUM);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + genericTransDetailsVO.getChecksum(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPaymentType(), "Numbers", 5, isOptional) /*|| !functions.isNumericVal(commonValidatorVO.getPaymentType())*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENT_TYPE, ErrorMessages.INVALID_PAYMENT_TYPE + ":::" + commonValidatorVO.getPaymentType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCardType(), "Numbers", 5, isOptional) /*|| !functions.isNumericVal(commonValidatorVO.getCardType())*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CARD_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CARD_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CARD_TYPE, ErrorMessages.INVALID_CARD_TYPE + ":::" + commonValidatorVO.getCardType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TERMINALID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTerminalId(), "Numbers", 6, isOptional) || !functions.isNumericVal(commonValidatorVO.getTerminalId()) || (functions.isValueNull(commonValidatorVO.getTerminalId()) && (commonValidatorVO.getTerminalId().trim()).equals("")) )
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TERMINALID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TERMINALID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TERMINALID, ErrorMessages.INVALID_TERMINALID + ":::" + commonValidatorVO.getTerminalId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAccountId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNTID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNTID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACCOUNTID, ErrorMessages.INVALID_ACCOUNTID + ":::" + commonValidatorVO.getAccountId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if ( (functions.isValueNull(genericAddressDetailsVO.getCity()) && (genericAddressDetailsVO.getCity().trim()).equals("")) || !ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "City", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (/*!genericAddressDetailsVO.getState().matches(state) || */!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "State", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 24, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTelnocc(), "Phone", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNOCC, ErrorMessages.INVALID_TELNOCC + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMAIL, ErrorMessages.INVALID_EMAIL + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getCardNum(), "CC", 19, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAN, ErrorMessages.INVALID_PAN + ":::" + genericCardDetailsVO.getCardNum(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                /*case PAYMENTBRAND:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPaymentBrand(), "paymentBrand", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENTBRAND);
                        //errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENTBRAND, ErrorMessages.INVALID_PAYMENTBRAND + ":::" + commonValidatorVO.getPaymentBrand(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getcVV(), "OnlyNumber", 4, isOptional) || (!isOptional && genericCardDetailsVO.getcVV().length() < 3))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CVV);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CVV, ErrorMessages.INVALID_CVV + ":::" + genericCardDetailsVO.getcVV(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getExpMonth(), "Months", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EXP_MONTH);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EXP_MONTH, ErrorMessages.INVALID_EXP_MONTH + ":::" + genericCardDetailsVO.getExpMonth(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getExpYear(), "Years", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EXP_YEAR);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EXP_YEAR, ErrorMessages.INVALID_EXP_YEAR + ":::" + genericCardDetailsVO.getExpYear(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case SSN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getSsn(), "Numbers", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_SSN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_SSN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_SSN, ErrorMessages.INVALID_SSN + ":::" + genericAddressDetailsVO.getSsn(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getFirstname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getLastname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getBirthdate(), "Numbers", 8, isOptional) || (commonValidatorVO.getAddressDetailsVO().getBirthdate()!=null && !commonValidatorVO.getAddressDetailsVO().getBirthdate().equalsIgnoreCase("") && (functions.isFutureDate(commonValidatorVO.getAddressDetailsVO().getBirthdate(), "yyyyMMdd") || functions.isRandomDate(commonValidatorVO.getAddressDetailsVO().getBirthdate(), "yyyyMMdd"))))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BIRTHDATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BIRTHDATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_BIRTHDATE, ErrorMessages.INVALID_BIRTHDATE + ":::" + genericAddressDetailsVO.getBirthdate(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LANGUAGE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLanguage(), "StrictString", 3, isOptional) || genericAddressDetailsVO.getLanguage().length() > 3)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LANGUAGE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LANGUAGE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LANGUAGE, ErrorMessages.INVALID_LANGUAGE + ":::" + genericAddressDetailsVO.getLanguage(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getIp(), "IPAddress", 80, isOptional) || ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getIp(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IPADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_IPADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException("Invalid IP Address", "Invalid IP Address :::" + genericAddressDetailsVO.getIp(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(genericAddressDetailsVO.getCardHolderIpAddress()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(input.toString(), new PZValidationException("Invalid IP Address", "Invalid IP Address :::" + genericAddressDetailsVO.getIp(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTmpl_amount(), "Amount", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TMPL_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TMPL_AMOUNT);
                        //validationErrorList.addError(input.toString(), new ValidationException("Invalid Template Amount", "Invalid Template Amount :::" + genericAddressDetailsVO.getTmpl_amount()));
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TMPL_AMOUNT, ErrorMessages.INVALID_TMPL_AMOUNT + ":::" + genericAddressDetailsVO.getTmpl_amount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTmpl_currency(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TMPL_CURRENCY);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TMPL_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TMPL_CURRENCY);
                        //validationErrorList.addError(input.toString(), new ValidationException("Invalid Template Currency", "Invalid Template Currency :::" + genericAddressDetailsVO.getTmpl_currency(),errorCodeVO));
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TMPL_CURRENCY, ErrorMessages.INVALID_TMPL_CURRENCY + ":::" + genericAddressDetailsVO.getTmpl_currency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getCurrency(), "StrictString", 3, isOptional) /*|| genericTransDetailsVO.getCurrency().length() < 3 || genericTransDetailsVO.getCurrency().length() > 3*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case RESERVEFIELD1:
                    if (!ESAPI.validator().isValidInput(input.toString(), recurringBillingVO.getReqField1(), "recurring", 20, isOptional))
                    {

                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_RESERVEFIELD1);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_RESERVEFIELD1);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RESERVEFIELD1, ErrorMessages.INVALID_RESERVEFIELD1 + ":::" + recurringBillingVO.getReqField1(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case RESERVEFIELD2:
                    if (!ESAPI.validator().isValidInput(input.toString(),resField2, "payMitco", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_RESERVEFIELD2);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_RESERVEFIELD2);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RESERVEFIELD2, ErrorMessages.INVALID_RESERVEFIELD2 + ":::" + reserveField2VO.getAccountType()+"|"+reserveField2VO.getAccountNumber()+"|"+reserveField2VO.getRoutingNumber(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTrackingid(), "Numbers", 20, isOptional) || (functions.isValueNull(commonValidatorVO.getTrackingid()) && (commonValidatorVO.getTrackingid().trim()).equals("")) )
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRACKINGID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + commonValidatorVO.getTrackingid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReason(), "Description", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REASON);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REASON);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_Refund_REASON, ErrorMessages.INVALID_Refund_REASON + ":::" + commonValidatorVO.getReason(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case MANDATEID:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getMandateId(), "alphanum", 54, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MANDATEID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_MANDATEID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.MANDATEID_NOTVALID, ErrorMessages.MANDATEID_NOTVALID + ":::" + genericCardDetailsVO.getMandateId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case IBAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getIBAN(), "alphanum", 25, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_IBAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_IBAN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.IBAN_NOTVALID, ErrorMessages.IBAN_NOTVALID + ":::" + genericCardDetailsVO.getIBAN(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BIC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getBIC(), "alphanum", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BIC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BIC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.BIC_NOTVALID, ErrorMessages.BIC_NOTVALID + ":::" + genericCardDetailsVO.getBIC(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CUSTOMERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCustomerId(), "customerId", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID + ":::" + commonValidatorVO.getCustomerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case VPA_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getVpa_address(), "VPAAddress", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_VPA_ADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_VPA_ADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_VPA_ADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_VPA_ADDRESS, ErrorMessages.INVALID_VPA_ADDRESS + ":::" + commonValidatorVO.getVpa_address(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DEVICE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getDevice(), "SafeString", 10, isOptional) || functions.hasHTMLTags(commonValidatorVO.getDevice()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DEVICE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_DEVICE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_DEVICE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DEVICE, ErrorMessages.INVALID_DEVICE + ":::" + commonValidatorVO.getVpa_address(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }

        }
    }

    public void InputValidations(DirectRefundValidatorVO directRefundValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        MerchantDetailsVO merchantDetailsVO = directRefundValidatorVO.getMerchantDetailsVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        for (InputFields input : inputList)
        {
            errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(InputFields.TOID.name(), merchantDetailsVO.getMemberId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                        validationErrorList.addError(InputFields.TOID.name(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(InputFields.CHECKSUM.name(), directRefundValidatorVO.getCheckSum(), "alphanum", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CHECKSUM);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_CHECKSUM);
                        validationErrorList.addError(InputFields.CHECKSUM.name(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + directRefundValidatorVO.getCheckSum(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(InputFields.IPADDR.name(), directRefundValidatorVO.getMerchantIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(InputFields.IPADDR.name(), directRefundValidatorVO.getMerchantIpAddress(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_IPADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_IPADDRESS);
                        validationErrorList.addError(InputFields.IPADDR.name(), new PZValidationException("Invalid IP Address", "Invalid IP Address :::" + directRefundValidatorVO.getMerchantIpAddress(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(InputFields.TRACKINGID.name(), directRefundValidatorVO.getTrackingid(), "Numbers", 20, isOptional) || !Functions.isValidSQL(directRefundValidatorVO.getTrackingid()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TRACKINGID);
                        validationErrorList.addError(InputFields.TRACKINGID.name(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + directRefundValidatorVO.getTrackingid(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(InputFields.AMOUNT.name(), directRefundValidatorVO.getRefundAmount(), "Amount", 12, isOptional) || !Functions.isValidSQL(directRefundValidatorVO.getRefundAmount()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_AMOUNT);
                        validationErrorList.addError(ErrorName.VALIDATION_AMOUNT.name(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + directRefundValidatorVO.getRefundAmount() + " for Tracking Id" + directRefundValidatorVO.getTrackingid(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(InputFields.REASON.name(), directRefundValidatorVO.getRefundReason(), "SafeString", 255, isOptional) || !Functions.isValidSQL(directRefundValidatorVO.getRefundReason()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REASON);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_Refund_REASON);
                        validationErrorList.addError(InputFields.REASON.name(), new PZValidationException(ErrorMessages.INVALID_Refund_REASON, ErrorMessages.INVALID_Refund_REASON + ":::" + directRefundValidatorVO.getRefundReason() + " for Tracking Id" + directRefundValidatorVO.getTrackingid(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(InputFields.PARTNER_ID.name(), directRefundValidatorVO.getParetnerId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_PARTNERID);
                        validationErrorList.addError(InputFields.PARTNER_ID.name(), new PZValidationException(ErrorMessages.INVALID_PARTNERID, ErrorMessages.INVALID_PARTNERID + ":::" + directRefundValidatorVO.getParetnerId(), errorCodeVO));
                        if (directRefundValidatorVO.getErrorCodeListVO() != null)
                            directRefundValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;

            }
        }

    }


    public void InputValidations(DirectInquiryValidatorVO directInquiryValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        MerchantDetailsVO merchantDetailsVO = directInquiryValidatorVO.getMerchantDetailsVO();
        //ErrorCodeUtils instance
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        //To Validate ToId
        for (InputFields input : inputList)
        {
            errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(InputFields.TOID.name(), merchantDetailsVO.getMemberId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                        validationErrorList.addError(InputFields.TOID.name(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (directInquiryValidatorVO.getErrorCodeListVO() != null)
                            directInquiryValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(InputFields.CHECKSUM.name(), directInquiryValidatorVO.getCheckSum(), "alphanum", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CHECKSUM);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_CHECKSUM);
                        validationErrorList.addError(InputFields.CHECKSUM.name(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + directInquiryValidatorVO.getCheckSum(), errorCodeVO));
                        if (directInquiryValidatorVO.getErrorCodeListVO() != null)
                            directInquiryValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(InputFields.TRACKINGID.name(), directInquiryValidatorVO.getTrackingId(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TRACKINGID);
                        validationErrorList.addError(InputFields.TRACKINGID.name(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + directInquiryValidatorVO.getTrackingId(), errorCodeVO));
                        if (directInquiryValidatorVO.getErrorCodeListVO() != null)
                            directInquiryValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), directInquiryValidatorVO.getDescription(), "Description", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DESCRIPTION);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DESCRIPTION, ErrorMessages.INVALID_DESCRIPTION + ":::" + directInquiryValidatorVO.getDescription(), errorCodeVO));
                        if (directInquiryValidatorVO.getErrorCodeListVO() != null)
                            directInquiryValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;


            }
        }

    }
    public void InputValidations(DirectCaptureValidatorVO directCaptureValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        MerchantDetailsVO merchantDetailsVO = directCaptureValidatorVO.getMerchantDetailsVO();
        //ErrorCodeUtils instance
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = null;
        //To Validate ToId
        for (InputFields input : inputList)
        {
            errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(InputFields.TOID.name(), merchantDetailsVO.getMemberId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TOID);
                        validationErrorList.addError(InputFields.TOID.name(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (directCaptureValidatorVO.getErrorCodeListVO() != null)
                            directCaptureValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(InputFields.CHECKSUM.name(), directCaptureValidatorVO.getCheckSum(), "alphanum", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CHECKSUM);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_CHECKSUM);
                        validationErrorList.addError(InputFields.CHECKSUM.name(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + directCaptureValidatorVO.getCheckSum(), errorCodeVO));
                        if (directCaptureValidatorVO.getErrorCodeListVO() != null)
                            directCaptureValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(InputFields.TRACKINGID.name(), directCaptureValidatorVO.getTrackingid(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TRACKINGID);
                        validationErrorList.addError(InputFields.TRACKINGID.name(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + directCaptureValidatorVO.getTrackingid(), errorCodeVO));
                        if (directCaptureValidatorVO.getErrorCodeListVO() != null)
                            directCaptureValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
                case CAPTUREAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), directCaptureValidatorVO.getCaptureAmount(), "Amount", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + directCaptureValidatorVO.getCaptureAmount(), errorCodeVO));
                        if (directCaptureValidatorVO.getErrorCodeListVO() != null)
                            directCaptureValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(InputFields.PARTNER_ID.name(), directCaptureValidatorVO.getParetnerId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_PARTNERID);
                        validationErrorList.addError(InputFields.PARTNER_ID.name(), new PZValidationException(ErrorMessages.INVALID_PARTNERID, ErrorMessages.INVALID_PARTNERID + ":::" + directCaptureValidatorVO.getParetnerId(), errorCodeVO));
                        if (directCaptureValidatorVO.getErrorCodeListVO() != null)
                            directCaptureValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;

            }
        }

    }


    public void RestInputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO       = commonValidatorVO.getCardDetailsVO();
       RecurringBillingVO recurringBillingVO            = new RecurringBillingVO();
        AccountInfoVO accountInfoVO                     = new AccountInfoVO();
        String resField2                                = "";
        if(commonValidatorVO.getRecurringBillingVO()!= null)
        {
            recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        }
        if(commonValidatorVO.getAccountInfoVO() != null)
        {
            accountInfoVO = commonValidatorVO.getAccountInfoVO();
        }
        Functions functions             = new Functions();
        ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();

        if(commonValidatorVO.getReserveField2VO()!=null)
        {
            resField2 = commonValidatorVO.getReserveField2VO().getAccountType()+"|"+commonValidatorVO.getReserveField2VO().getAccountNumber()+"|"+commonValidatorVO.getReserveField2VO().getRoutingNumber();
        }

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (functions.isValueNull(merchantDetailsVO.getMemberId()) && merchantDetailsVO.getMemberId().trim().equals("") || !ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getMemberId(), "OnlyNumber", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PARTNERID: //partnerId validation from partnerDetails VO
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPartnerDetailsVO().getPartnerId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PARTNERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PARTNERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PARTNERID, ErrorMessages.INVALID_PARTNERID + ":::" + commonValidatorVO.getPartnerDetailsVO().getPartnerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getChecksum(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECKSUM);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CHECKSUM);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + genericTransDetailsVO.getChecksum(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CUSTOMERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCustomerId(), "customerId", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID + ":::" + commonValidatorVO.getCustomerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CUSTOMER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCustomerId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID + ":::" + commonValidatorVO.getCustomerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYMENTBRAND:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPaymentBrand(), "StrictString", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_BRAND);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_BRAND);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENT_BRAND, ErrorMessages.INVALID_PAYMENT_BRAND + ":::" + commonValidatorVO.getPaymentBrand(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYMENTMODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPaymentMode(), "StrictString", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_MODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_MODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENT_MODE, ErrorMessages.INVALID_PAYMENT_BRAND_MODE + ":::" + commonValidatorVO.getPaymentMode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PHONENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "alphaNumPhone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case AMOUNT:
                   // if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAmount(), "tenDigitAmount", 13, isOptional))
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAmount(), "Amount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(InputFields.DESCRIPTION.toString(), genericTransDetailsVO.getOrderId(), "Description", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DESCRIPTION, ErrorMessages.INVALID_DESCRIPTION + ":::" + genericTransDetailsVO.getOrderId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(InputFields.ORDERDESCRIPTION.toString(), genericTransDetailsVO.getOrderDesc(), "Address", 255, isOptional) || functions.hasHTMLTags(genericTransDetailsVO.getOrderDesc()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ORDER_DESCRIPTION, ErrorMessages.INVALID_ORDER_DESCRIPTION + ":::" + genericTransDetailsVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getCurrency(), "StrictString", 3, isOptional) /*|| genericTransDetailsVO.getCurrency().length() < 3 || genericTransDetailsVO.getCurrency().length() > 3*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTrackingid(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRACKINGID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + commonValidatorVO.getTrackingid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getCardNum(), "CC", 19, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAN, ErrorMessages.INVALID_PAN + ":::" + genericCardDetailsVO.getCardNum(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getcVV(), "CVV", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CVV);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CVV, ErrorMessages.INVALID_CVV + ":::" + genericCardDetailsVO.getcVV(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getExpMonth(), "Months", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EXP_MONTH);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EXP_MONTH, ErrorMessages.INVALID_EXP_MONTH + ":::" + genericCardDetailsVO.getExpMonth(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getExpYear(), "Years", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EXP_YEAR);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EXP_YEAR, ErrorMessages.INVALID_EXP_YEAR + ":::" + genericCardDetailsVO.getExpYear(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(InputFields.IPADDR.name(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(InputFields.IPADDR.name(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(genericAddressDetailsVO.getCardHolderIpAddress()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_IPADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_IPADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_IPADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_IPADDRESS, ErrorMessages.INVALID_IPADDRESS + ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getFirstname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getLastname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                /*case PAYMENTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPaymentType(), "Numbers", 5, isOptional) || !functions.isNumericVal(commonValidatorVO.getPaymentType()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENT_TYPE, ErrorMessages.INVALID_PAYMENT_TYPE + ":::" + commonValidatorVO.getPaymentType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
                case CARDTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCardType(), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CARD_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CARD_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CARD_TYPE, ErrorMessages.INVALID_CARD_TYPE + ":::" + commonValidatorVO.getCardType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        //System.out.println("inside country----------");
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRY_OTP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_COUNTRY);
                        validationErrorList.addError(InputFields.COUNTRY_OTP.name(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_OTP, ErrorMessages.INVALID_COUNTRY_OTP + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRST_SIX:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericCardDetailsVO.getFirstSix(), "Number",6,isOptional))
                    {
                        errorCodeVO=errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_FIRST_SIX);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRST_SIX);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_FIRST_SIX, ErrorMessages.INVALID_FIRST_SIX + genericCardDetailsVO.getFirstSix(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!= null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;

                case LAST_FOUR:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericCardDetailsVO.getLastFour(), "Number", 4 ,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LAST_FOUR);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LAST_FOUR);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_FOUR, ErrorMessages.INVALID_LAST_FOUR + genericCardDetailsVO.getLastFour(), errorCodeVO ));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }

                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getCity()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getStreet()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "SafeString", 40, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getState()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case REFERENCE_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getCurrency(), "StrictString", 3, isOptional) || genericTransDetailsVO.getCurrency().length() < 3 || genericTransDetailsVO.getCurrency().length() > 3)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REFERENCE_CURRENCY, ErrorMessages.INVALID_REFERENCE_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case REFERENCE_TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTrackingid(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REFERENCE_TRACKINGID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REFERENCE_TRACKINGID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REFERENCE_TRACKINGID, ErrorMessages.INVALID_REFERENCE_TRACKINGID + ":::" + commonValidatorVO.getTrackingid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case REFERENCE_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAmount(), "tenDigitAmount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REFERENCE_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REFERENCE_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REFERENCE_AMOUNT, ErrorMessages.INVALID_REFERENCE_AMOUNT + ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMAIL, ErrorMessages.INVALID_EMAIL + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CONTACT_PERSON:
                    if (!ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getContact_persons(), "SafeString", 50, isOptional) || functions.hasHTMLTags(merchantDetailsVO.getContact_persons()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CONTACT_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CONTACT_NAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CONTACTPERSON, ErrorMessages.INVALID_CONTACTPERSON + ":::" + merchantDetailsVO.getContact_persons(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TRANSACTION_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTransactionType(), "transactionType", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSACTION_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSACTION_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRANSACTION_TYPE, ErrorMessages.INVALID_TRANSACTION_TYPE + ":::" + commonValidatorVO.getTransactionType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getPhone(), "Phone", 24, isOptional) || functions.hasHTMLTags(commonValidatorVO.getAddressDetailsVO().getPhone()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + commonValidatorVO.getAddressDetailsVO().getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getTelnocc(), "Phone", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNOCC, ErrorMessages.INVALID_TELNOCC + ":::" + commonValidatorVO.getAddressDetailsVO().getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ACCOUNT_HOLDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCardDetailsVO().getCardHolderName(), "onlyAlphanum", 128, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REJECTED_ACCOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_ACCOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REJECTED_ACCOUNT, ErrorMessages.INVALID_REJECTED_ACCOUNT + ":::" + commonValidatorVO.getCardDetailsVO().getCardHolderName(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case BIC:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCardDetailsVO().getBIC(), "bic", 11, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REJECTED_BIC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_BIC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REJECTED_BIC, ErrorMessages.INVALID_REJECTED_BIC + ":::" + commonValidatorVO.getCardDetailsVO().getBIC(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case IBAN:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCardDetailsVO().getIBAN(), "iban", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_REJECTED_IBAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_IBAN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REJECTED_IBAN, ErrorMessages.INVALID_REJECTED_IBAN + ":::" + commonValidatorVO.getCardDetailsVO().getIBAN(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case RECURRINGTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getRecurringBillingVO().getRecurringType(), "recurringType", 8, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_RECURRING_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_RECURRING_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RECURRING_TYPE, ErrorMessages.INVALID_RECURRING_TYPE + ":::" + commonValidatorVO.getRecurringBillingVO().getRecurringType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CREATEREGISTRATION:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCreateRegistration(), "createRegistration", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_CREATE_REGISTRATION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CREATE_REGISTRATION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CREATE_REGISTRATION, ErrorMessages.INVALID_CREATE_REGISTRATION + ":::" + commonValidatorVO.getCreateRegistration(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BIRTHDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getBirthdate(), "Numbers", 8, isOptional) || (commonValidatorVO.getAddressDetailsVO().getBirthdate()!=null && (functions.isFutureDate(commonValidatorVO.getAddressDetailsVO().getBirthdate(), "yyyyMMdd") || functions.isRandomDate(commonValidatorVO.getAddressDetailsVO().getBirthdate(), "yyyyMMdd"))))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_BIRTHDATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BIRTHDATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_BIRTHDATE, ErrorMessages.INVALID_BIRTHDATE + ":::" + commonValidatorVO.getAddressDetailsVO().getBirthdate(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TERMINALID:
                    if (functions.isValueNull(commonValidatorVO.getTerminalId()) && commonValidatorVO.getTerminalId().trim().equals("") || !ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTerminalId(), "OnlyNumber", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TERMINALID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TERMINALID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TERMINALID, ErrorMessages.INVALID_TERMINALID + ":::" + commonValidatorVO.getTerminalId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getAccountNumber(), "OnlyNumber", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNT_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNT_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACCOUNT_NUMBER, ErrorMessages.INVALID_ACCOUNT_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getAccountNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ROUTINGNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getRoutingNumber(), "OnlyNumber", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ROUTING_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ROUTING_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ROUTING_NUMBER, ErrorMessages.INVALID_ROUTING_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getRoutingNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CHECKNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getCheckNumber(), "OnlyNumber", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECK_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CHECK_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CHECK_NUMBER, ErrorMessages.INVALID_CHECK_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getCheckNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNTTYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getAccountType(), "accounttype", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNT_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNT_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACCOUNT_TYPE, ErrorMessages.INVALID_ACCOUNT_TYPE + ":::" + commonValidatorVO.getReserveField2VO().getAccountType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PARTNER_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getPartnerId(), "OnlyNumber", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PARTNERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PARTNERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PARTNERID, ErrorMessages.INVALID_PARTNERID + ":::" + commonValidatorVO.getMerchantDetailsVO().getPartnerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getTransDetailsVO().getRedirectUrl(), "SafeString", 1055, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REDIRECT_URL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REDIRECT_URL, ErrorMessages.INVALID_REDIRECT_URL + ":::" + commonValidatorVO.getTransDetailsVO().getRedirectUrl(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LOGIN:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getLogin(), "username", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LOGIN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LOGIN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LOGIN, ErrorMessages.INVALID_LOGIN + ":::" + commonValidatorVO.getMerchantDetailsVO().getLogin(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COMPANY_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getCompany_name(), "alphanum", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COMPANY_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COMPANY_NAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COMPANY_NAME, ErrorMessages.INVALID_COMPANY_NAME + ":::" + commonValidatorVO.getMerchantDetailsVO().getCompany_name(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case WEBSITE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getMerchantDetailsVO().getWebsite(), "URL", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_WEBSITE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_WEBSITE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_WEBSITE, ErrorMessages.INVALID_WEBSITE + ":::" + commonValidatorVO.getMerchantDetailsVO().getWebsite(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case GENDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getSex(), "gender", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_GENDER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_GENDER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_GENDER, ErrorMessages.INVALID_GENDER + ":::" + commonValidatorVO.getAddressDetailsVO().getSex(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case FIRSTNAME_REST:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getFirstname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(commonValidatorVO.getAddressDetailsVO().getFirstname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + commonValidatorVO.getAddressDetailsVO().getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME_REST:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAddressDetailsVO().getLastname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(commonValidatorVO.getAddressDetailsVO().getLastname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + commonValidatorVO.getAddressDetailsVO().getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case NEW_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getNewPassword(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PASSWORD);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PASSWORD, ErrorMessages.INVALID_PASSWORD + ":::" +merchantDetailsVO.getNewPassword(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CONFIRM_PASSWORD:
                    if (!ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getConPassword(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PASSWORD);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PASSWORD);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PASSWORD, ErrorMessages.INVALID_PASSWORD + ":::" +merchantDetailsVO.getNewPassword(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EXP_DATE_OFFSET:
                    if (!ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getExpDateOffset(), "OnlyNumber", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXPDATE_OFFSET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EXPDATE_OFFSET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EXPDATE_OFFSET, ErrorMessages.INVALID_EXPDATE_OFFSET + ":::" +merchantDetailsVO.getExpDateOffset(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYOUT_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getPayoutType(), "StrictString", 10, isOptional))
                    {
                        //System.out.println("inside validation----"+commonValidatorVO.getPayoutType());
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYOUT_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYOUT_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYOUT_TYPE, ErrorMessages.INVALID_PAYOUT_TYPE + ":::" +commonValidatorVO.getPayoutType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case WALLET_ID:
                    if(!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getTransDetailsVO().getWalletId(),"alphanum",100,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_WALLET_ID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_WALLET_ID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_WALLET_ID, ErrorMessages.INVALID_WALLET_ID + ":::" +commonValidatorVO.getTransDetailsVO().getWalletId(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case WALLET_AMOUNT:
                    if(!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getTransDetailsVO().getWalletAmount(),"Amount",100,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_WALLET_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_WALLET_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_WALLET_AMOUNT, ErrorMessages.INVALID_WALLET_AMOUNT + ":::" +commonValidatorVO.getTransDetailsVO().getWalletAmount(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case WALLET_CURRENCY:
                    if(!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getTransDetailsVO().getWalletCurrency(),"StrictString",3,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_WALLET_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_WALLET_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_WALLET_CURRENCY, ErrorMessages.INVALID_WALLET_CURRENCY + ":::" +commonValidatorVO.getTransDetailsVO().getWalletCurrency(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMICOUNT:
                    if(!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getTransDetailsVO().getEmiCount(),"OnlyNumber",2,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMI_COUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMI_COUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMI_COUNT, ErrorMessages.INVALID_EMI_COUNT + ":::" +commonValidatorVO.getTransDetailsVO().getEmiCount(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case SMS_CODE:
                    if(!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getSmsCode(),"OnlyNumber",6,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMI_COUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMI_COUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMI_COUNT, ErrorMessages.INVALID_EMI_COUNT + ":::" +commonValidatorVO.getSmsCode(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCACTIVATIONDATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),accountInfoVO.getAccActivationDate(),"FormatDate",20,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getAccActivationDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ACCCHANGEDATE:
                    if(!ESAPI.validator().isValidInput(input.toString(), accountInfoVO.getAccChangeDate(), "FormatDate", 20, isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getAccChangeDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ACCPWCHANGEDATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),accountInfoVO.getAccPwChangeDate(),"FormatDate",20,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getAccPwChangeDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ADDRESSUSEDATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),accountInfoVO.getAddressUseDate(),"FormatDate",20,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getAddressUseDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYMENTACCACTIVATIONDATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),accountInfoVO.getPaymentAccActivationDate(),"FormatDate",20,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACC_ACTIVATION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getPaymentAccActivationDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TMPL_AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTmpl_amount(), "Amount", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TMPL_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TMPL_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TMPL_AMOUNT, ErrorMessages.INVALID_TMPL_AMOUNT + ":::" + genericAddressDetailsVO.getTmpl_amount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTmpl_currency(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TMPL_CURRENCY);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TMPL_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TMPL_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TMPL_CURRENCY, ErrorMessages.INVALID_TMPL_CURRENCY + ":::" + genericAddressDetailsVO.getTmpl_currency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PAYMENT_PROVIDER:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getPaymentProvider(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_PROVIDER);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAYMENT_PROVIDER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_PROVIDER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENT_PROVIDER, ErrorMessages.INVALID_PAYMENT_PROVIDER + ":::" + genericTransDetailsVO.getPaymentProvider(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case VPA_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getVpa_address(), "VPAAddress", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_VPA_ADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_VPA_ADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_VPA_ADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_VPA_ADDRESS, ErrorMessages.INVALID_VPA_ADDRESS + ":::" + commonValidatorVO.getVpa_address(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LANGUAGE:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getLanguage(),"StrictString",3,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LANGUAGE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_LANGUAGE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LANGUAGE);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_LANGUAGE,ErrorMessages.INVALID_LANGUAGE+ ":::" + genericAddressDetailsVO.getLanguage(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BANK_ACCOUNT_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getBankAccountName(), "StrictString",35,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BANK_ACCOUNT_NAME);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_BANK_ACCOUNT_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BANK_ACCOUNT_NAME);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BANK_ACCOUNT_NAME,ErrorMessages.INVALID_BANK_ACCOUNT_NAME+ ":::" + commonValidatorVO.getReserveField2VO().getBankAccountName(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                  case BANK_ACCOUNT_NUMBER:
                      if (!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getReserveField2VO().getBankAccountNumber(),"OnlyNumber",35,isOptional))
                      {
                          errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BANK_ACCOUNT_NUMBER);
                          errorCodeVO.setErrorReason(ErrorMessages.INVALID_BANK_ACCOUNT_NUMBER);
                          errorCodeVO.setErrorName(ErrorName.VALIDATION_BANK_ACCOUNT_NUMBER);
                          validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BANK_ACCOUNT_NUMBER,ErrorMessages.INVALID_BANK_ACCOUNT_NUMBER+ ":::" + commonValidatorVO.getReserveField2VO().getBankAccountNumber(),errorCodeVO));
                          if (commonValidatorVO.getErrorCodeListVO() != null)
                              commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                      }
                      break;
                  case TRANSFER_TYPE:
                      if (!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getReserveField2VO().getTransferType(),"StrictString",20,isOptional))
                      {
                          errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSFER_TYPE);
                          errorCodeVO.setErrorReason(ErrorMessages.INVALID_TRANSFER_TYPE);
                          errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSFER_TYPE);
                          validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_TRANSFER_TYPE,ErrorMessages.INVALID_TRANSFER_TYPE+ ":::" + commonValidatorVO.getReserveField2VO().getTransferType(),errorCodeVO));
                          if (commonValidatorVO.getErrorCodeListVO() != null)
                              commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                      }
                      break;
                  case BANK_IFSC:
                      if (!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getReserveField2VO().getBankIfsc(),"IfscCode",11,isOptional))
                      {
                          errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BANK_IFSC);
                          errorCodeVO.setErrorReason(ErrorMessages.INVALID_BANK_IFSC);
                          errorCodeVO.setErrorName(ErrorName.VALIDATION_BANK_IFSC);
                          validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BANK_IFSC,ErrorMessages.INVALID_BANK_IFSC+ ":::" + commonValidatorVO.getReserveField2VO().getBankIfsc(),errorCodeVO));
                          if (commonValidatorVO.getErrorCodeListVO() != null)
                              commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                      }
                      break;
                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getPaginationVO().getStartdate(),"fromDate",25,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FROM_DATE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_FROM_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FROM_DATE);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_FROM_DATE,ErrorMessages.INVALID_FROM_DATE+ ":::" + commonValidatorVO.getPaginationVO().getStartdate(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(),commonValidatorVO.getPaginationVO().getEnddate(),"toDate",25,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TO_DATE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TO_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TO_DATE);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_TO_DATE,ErrorMessages.INVALID_TO_DATE+ ":::" + commonValidatorVO.getPaginationVO().getEnddate(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf((commonValidatorVO.getPaginationVO().getPageNo())), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAGE_NO);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_PAGE_NO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAGE_NO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAGE_NO, ErrorMessages.INVALID_PAGE_NO + ":::" + commonValidatorVO.getPaginationVO().getPageNo(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf((commonValidatorVO.getPaginationVO().getRecordsPerPage())), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_RECORDS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_RECORDS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_RECORDS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RECORDS, ErrorMessages.INVALID_RECORDS + ":::" + commonValidatorVO.getPaginationVO().getRecordsPerPage(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getAccountId(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNTID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNTID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACCOUNTID, ErrorMessages.INVALID_ACCOUNTID + ":::" + commonValidatorVO.getAccountId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;


                case REFUND_CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getRefundCurrency(), "StrictString", 3, isOptional) || functions.hasHTMLTags(genericTransDetailsVO.getRefundCurrency()))
                    {
                        log.error("REFUND_CURRENCY------------------------>"+ genericTransDetailsVO.getRefundCurrency());
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getRefundCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CLIENT_TRANSACTION_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getClientTransactionId(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CLIENT_TRANSACTION_ID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CLIENT_TRANSACTION_ID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CLIENT_TRANSACTION_ID, ErrorMessages.INVALID_CLIENT_TRANSACTION_ID + ":::" + genericTransDetailsVO.getClientTransactionId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case REFUNDAMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getRefundAmount(), "tenDigitAmount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + genericTransDetailsVO.getRefundAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getPaymentid(), "SafeString", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENTID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENTID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENTID, ErrorMessages.INVALID_PAYMENTID + ":::" + genericTransDetailsVO.getPaymentid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PURCHASE_IDENTIFIER:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getPurchase_identifier(), "SafeString", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PURCHASE_IDENTIFIER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PURCHASE_IDENTIFIER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PURCHASE_IDENTIFIER, ErrorMessages.INVALID_PURCHASE_IDENTIFIER + ":::" + genericTransDetailsVO.getPurchase_identifier(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case MERCHANT_ID:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getMerchant_id(), "merchantid", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MERCHANT_ID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_MERCHANT_ID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_MERCHANT_ID, ErrorMessages.INVALID_MERCHANT_ID + ":::" + genericTransDetailsVO.getMerchant_id(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AUTHORIZATION_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getAuthorization_code(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AUTHORIZATION_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AUTHORIZATION_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AUTHORIZATION_CODE, ErrorMessages.INVALID_AUTHORIZATION_CODE + ":::" + genericTransDetailsVO.getAuthorization_code(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TRANSACTION_DATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericTransDetailsVO.getTransactionDate(),"Timestamp",20,isOptional)){
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSACTION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSACTION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" +accountInfoVO.getPaymentAccActivationDate(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TRANS_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getTransactionType(), "transType", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSACTION_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSACTION_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRANSACTION_TYPE, ErrorMessages.INVALID_TRANSACTION_TYPE + ":::" + genericTransDetailsVO.getTransactionType(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case RRN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getRrn(), "Numbers", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_RRN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_RRN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RRN, ErrorMessages.INVALID_RRN + ":::" + genericTransDetailsVO.getRrn(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ARN:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getArn(), "Numbers", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ARN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ARN);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ARN, ErrorMessages.INVALID_ARN + ":::" + genericTransDetailsVO.getRrn(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CALL_TYPE:
                    if (!functions.isValueNull(genericTransDetailsVO.getCall_type()) && !isOptional)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CALL_TYPE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CALL_TYPE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CALL_TYPE, ErrorMessages.INVALID_CALL_TYPE + ":::" + genericTransDetailsVO.getCall_type(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                //JPBANK
                case BANK_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getBankName(), "StrictString",35,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BANK_NAME);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_BANK_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BANK_NAME);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BANK_NAME,ErrorMessages.INVALID_BANK_NAME+ ":::" + commonValidatorVO.getReserveField2VO().getBankName(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BRANCH_NAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getBranchName(), "StrictString",35,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BRANCH_NAME);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_BRANCH_NAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BRANCH_NAME);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BRANCH_NAME,ErrorMessages.INVALID_BRANCH_NAME+ ":::" + commonValidatorVO.getReserveField2VO().getBranchName(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BANK_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getBankCode(), "Number",20,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BANK_CODE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_BANK_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BANK_CODE);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BANK_CODE,ErrorMessages.INVALID_BANK_CODE+ ":::" + commonValidatorVO.getReserveField2VO().getBankCode(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNT_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getAccountType(), "StrictString",20,isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNT_TYPE_JPBANK);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_ACCOUNT_TYPE_JPBANK);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNT_TYPE_JPBANK);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_ACCOUNT_TYPE_JPBANK,ErrorMessages.INVALID_ACCOUNT_TYPE_JPBANK+ ":::" + commonValidatorVO.getReserveField2VO().getAccountType(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BRANCH_CODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getBranchCode(), "Number",20,isOptional))
                    {
                        errorCodeVO =    errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BRANCH_CODE);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_BRANCH_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_BRANCH_CODE);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_BRANCH_CODE,ErrorMessages.INVALID_BRANCH_CODE+ ":::" + commonValidatorVO.getReserveField2VO().getBranchCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }
    public void InputValidations(CommonValidatorVO commonValidatorVO,MarketPlaceVO marketPlaceVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {

        Functions functions = new Functions();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getMemberid(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID_MP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID_MP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID_MP, ErrorMessages.INVALID_TOID_MP + ":::" + marketPlaceVO.getMemberid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getAmount(), "Amount", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT_MP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT_MP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT_MP, ErrorMessages.INVALID_AMOUNT_MP + ":::" + marketPlaceVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERID:
                    if ((functions.isValueNull(marketPlaceVO.getOrderid()) && marketPlaceVO.getOrderid().trim().equals("")) || !ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getOrderid(), "SafeString", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_ID_MP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_ID_MP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ORDER_ID_MP, ErrorMessages.INVALID_ORDER_ID_MP + ":::" + marketPlaceVO.getOrderid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERDESCRIPTION:
                    if ((functions.isValueNull(marketPlaceVO.getOrderDesc()) && marketPlaceVO.getOrderDesc().trim().equals("")) || !ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getOrderDesc(), "Address", 255, isOptional)
                            || functions.hasHTMLTags(marketPlaceVO.getOrderDesc()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION_MP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION_MP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ORDER_DESCRIPTION_MP, ErrorMessages.INVALID_ORDER_DESCRIPTION_MP + ":::" + marketPlaceVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }
    public void RestInputValidations(CommonValidatorVO commonValidatorVO,MarketPlaceVO marketPlaceVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
            switch (input)
            {
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getRefundAmount(), "tenDigitAmount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT_MP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT_MP, ErrorMessages.INVALID_AMOUNT_MP + ":::" + marketPlaceVO.getRefundAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), marketPlaceVO.getTrackingid(), "Numbers", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRACKINGID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + marketPlaceVO.getTrackingid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }

    public String validateUpiTxnDetails(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional, String error)
    {
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        TransactionDetailsVO transactionDetailsVO       = commonValidatorVO.getTransactionDetailsVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();

        for (InputFields input : inputList)
        {
            switch (input)
            {
                case TRACKINGID:
                    if (!ESAPI.validator().isValidInput(input.toString(), transactionDetailsVO.getTrackingid(), "Numbers", 20, isOptional))
                    {
                        error += "INVALID_TRACKINGID, TrackingID should not be empty should be numeric with Max Length 20 | ";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRACKINGID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRACKINGID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TRACKINGID, ErrorMessages.INVALID_TRACKINGID + ":::" + transactionDetailsVO.getTrackingid(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case MEMBERID:
                    if (functions.isValueNull(merchantDetailsVO.getMemberId()) && merchantDetailsVO.getMemberId().trim().equals("") || !ESAPI.validator().isValidInput(input.toString(), merchantDetailsVO.getMemberId(), "OnlyNumber", 10, isOptional))
                    {
                        error += "Invalid MemberId, MemberId should not be empty and should be numeric with Max Length 10 | ";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TOID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + merchantDetailsVO.getMemberId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYMENTID:
                    if (!ESAPI.validator().isValidInput(input.toString(), transactionDetailsVO.getPaymentId(), "SafeString", 255, isOptional))
                    {
                        error += "Invalid payment id, payment id should not be empty with Max Length 255 | ";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENTID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENTID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENTID, ErrorMessages.INVALID_PAYMENTID + ":::" + transactionDetailsVO.getPaymentId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), transactionDetailsVO.getAmount(), "tenDigitAmount", 13, isOptional))
                    {
                        error += " Amount should not be empty and Accept only [0-9] and . (Single point) with Max length 12 | ";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + transactionDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case VPA_ADDRESS:
                    if (!ESAPI.validator().isValidInput(input.toString(), transactionDetailsVO.getCustomerId(), "VPAAddress", 50, isOptional))
                    {
                        error += "Enter valid VPA address | ";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_VPA_ADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_VPA_ADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_VPA_ADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_VPA_ADDRESS, ErrorMessages.INVALID_VPA_ADDRESS + ":::" + transactionDetailsVO.getCustomerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TRANSACTION_DATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),transactionDetailsVO.getTransactionTime(),"Timestamp",20,isOptional))
                    {
                        error += "Invalid Transaction Date Format, Date Format Should be YYYY-mm-dd HH:MM:SS";
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSACTION_DATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSACTION_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" + transactionDetailsVO.getTransactionTime(), errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

//                case STATUS:
////                    if(!ESAPI.validator().isValidInput(input.toString(),transactionDetailsVO.getStatus(),"SafeString",15,isOptional))
////                    {
//                        if ((!functions.isValueNull(commonValidatorVO.getTransactionDetailsVO().getStatus()))  || (functions.isValueNull(commonValidatorVO.getTransactionDetailsVO().getStatus()) &&
//                                ((!"capturesuccess".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) || !"payoutfailed".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) ||
//                                        !"payoutstarted".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) || !"payoutsuccessful".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) ||
//                                        !"authsuccessful".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) || !"authfailed".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) ||
//                                        !"authstarted".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus()) || !"begun".equalsIgnoreCase(commonValidatorVO.getTransactionDetailsVO().getStatus())))))
//                        {
//                            error += " Invalid Status, Status can be begun or authstarted or authfailed or authsuccessful or payoutsuccessful or payoutstarted or payoutfailed or capturesuccess ";
//                        }
////
//
////                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TRANSACTION_DATE);
////                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TRANSACTION_DATE);
////                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACC_ACTIVATION_DATE, ErrorMessages.INVALID_ACC_ACTIVATION_DATE + ":::" + transactionDetailsVO.getStatus(), errorCodeVO));
////                        if(commonValidatorVO.getErrorCodeListVO()!=null)
////                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
////                    }
//                    break;
            }
        }
        return  error;
    }

}