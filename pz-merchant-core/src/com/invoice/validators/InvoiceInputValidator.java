package com.invoice.validators;

import com.directi.pg.*;
import com.invoice.helper.InvoiceHelper;
import com.invoice.vo.DefaultProductList;
import com.invoice.vo.InvoiceVO;
import com.invoice.vo.ProductList;
import com.invoice.vo.UnitList;
import com.manager.PartnerManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidatorForMerchantServices;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 6/7/2017.
 */
public class InvoiceInputValidator
{
    private static Logger log = new Logger(InvoiceInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InvoiceInputValidator.class.getName());
    private Functions functions = new Functions();
    public InvoiceVO performValidationGenerateInvoiceParams(InvoiceVO invoiceVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        log.debug("Entering performValidationInvoiceParams--");
        String error = "";
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        Merchants merchants=new Merchants();
        error = validateGenerateInvoiceParams(invoiceVO, "REST");
        if(functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDAO merchantDAO = new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO = null;
        merchantDetailsVO = merchantDAO.getMemberDetails(invoiceVO.getMemberid());

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }
        merchantDetailsVO.setRole("merchant");
        if(functions.isValueNull(invoiceVO.getUserName()) && !merchantDetailsVO.getLogin().equalsIgnoreCase(invoiceVO.getUserName())){


            String userid=merchants.isMemberUser(invoiceVO.getUserName(),invoiceVO.getMemberid());
            if(!functions.isValueNull(userid)){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER);
                if (invoiceVO.getErrorCodeListVO() != null)
                    invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return invoiceVO;
            }
            merchantDetailsVO.setLogin(invoiceVO.getUserName());
            merchantDetailsVO.setRole("submerchant");
            merchantDetailsVO.setUserid(userid);

        }

        if (functions.isValueNull(invoiceVO.getAmount()) && (invoiceVO.getAmount().equalsIgnoreCase("0") || invoiceVO.getAmount().equalsIgnoreCase("0.00")))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_AMOUNT);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }

        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        return invoiceVO;
    }

    private String validateGenerateInvoiceParams (InvoiceVO invoiceVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getGenerateInvoiceMandatoryParams());
        /*if(invoiceVO.getProductList() != null && invoiceVO.getProductList().size()>0)
        {
            inputInvoiceMandatoryList.add(InputFields.QUANTITY_TOTAL);
        }*/
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        //Validate optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getGenerateInvoiceOptionalParams());
        ValidationErrorList errorMandatoryList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, errorMandatoryList, true);
        error = error + inputValiDatorUtils.getError(errorMandatoryList,inputInvoiceOptionalList,actionName);

        if(invoiceVO.getProductList() != null && invoiceVO.getProductList().size()>0)
        {
            List<InputFields> inputInvoiceDefualtProductOptionalList = new ArrayList<InputFields>();
            inputInvoiceDefualtProductOptionalList.addAll(inputValiDatorUtils.getInvoiceConfigProductListOptionalParams());
            ValidationErrorList optionalDefaultProductErrorList = new ValidationErrorList();
            for (ProductList productList : invoiceVO.getProductList())
            {
                invoiceVO.setProduct(productList);
                optionalDefaultProductErrorList = new ValidationErrorList();
                RestInputValidations(invoiceVO, inputInvoiceDefualtProductOptionalList, optionalDefaultProductErrorList, true);
                error = error + inputValiDatorUtils.getError(optionalDefaultProductErrorList, inputInvoiceDefualtProductOptionalList, actionName);
            }

        }
        return error;
    }

    public InvoiceVO performRegenerateInvoiceValidation(InvoiceVO invoiceVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        error = validateRegenerateInvoiceParams(invoiceVO, "REST");
        if(functions.isEmptyOrNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }

        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=null;
        Merchants merchants=new Merchants();
        merchantDetailsVO=merchantDAO.getMemberDetails(invoiceVO.getMemberid());
        merchantDetailsVO.setRole("merchant");
        if(functions.isValueNull(invoiceVO.getUserName()) && !merchantDetailsVO.getLogin().equalsIgnoreCase(invoiceVO.getUserName()))
        {
            String userid=merchants.isMemberUser(invoiceVO.getMemberid(),invoiceVO.getUserName());
            if(!functions.isValueNull(userid)){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER);
                if (invoiceVO.getErrorCodeListVO() != null)
                    invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return invoiceVO;
            }

            merchantDetailsVO.setLogin(invoiceVO.getUserName());
            merchantDetailsVO.setRole("submerchant");
            merchantDetailsVO.setUserid(userid);

        }
        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        return invoiceVO;
    }

    public InvoiceVO performRemindInvoiceValidation (InvoiceVO invoiceVO, String actionName)
    {
        String error = null;
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getRemindInvoiceMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        if(functions.isEmptyOrNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        return invoiceVO;
    }

    public InvoiceVO performGetInvoiceConfiguration(InvoiceVO invoiceVO, String actionName) throws PZDBViolationException
    {
        //System.out.println("inside validation----");
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoiceConfigMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        String memberId = invoiceVO.getMemberid();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        //System.out.println("error----"+error);
        if(!functions.isEmptyOrNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        PartnerDAO partnerDAO = new PartnerDAO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
        //System.out.println("memberid===="+memberId);
        //System.out.println("merchant----"+merchantDetailsVO.getMemberId());
        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }
        //System.out.println("partner id----"+merchantDetailsVO.getMemberId());
        partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
        //System.out.println("partner id----"+merchantDetailsVO.getPartnerId());
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);


        return invoiceVO;
    }

    private String validateRegenerateInvoiceParams (InvoiceVO invoiceVO, String actionName)
    {
        String error = null;
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getRegenerateInvoiceMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getRegenerateInvoiceOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        return error;
    }

    public InvoiceVO performInquiryInvoiceValidation(InvoiceVO invoiceVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";
        error = validateInquiryInvoiceParams(invoiceVO, "REST");
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String memberId = invoiceVO.getMemberid();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        if(functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        PartnerDAO partnerDAO = new PartnerDAO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }
        if(!Checksum.verifyChecksumV3(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(),invoiceVO.getDescription(), invoiceVO.getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, invoiceVO.getErrorCodeListVO(), null, null);
        }
        partnerDetailsVO = partnerDAO.getPartnerDetails(merchantDetailsVO.getPartnerId());
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        return invoiceVO;
    }
    //new
    public  InvoiceVO performGetInvoiceValidation (InvoiceVO invoiceVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException

    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        Merchants merchants=new Merchants();
        String memberId =invoiceVO.getMemberid();
        // System.out.println("start date----"+invoiceVO.getPaginationVO().getStartdate());
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InvoiceHelper invoiceHelper = new InvoiceHelper();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoicelistMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);
        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInvoicelistOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        if (functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }

        merchantDetailsVO.setRole("merchant");
        if(functions.isValueNull(invoiceVO.getUserName()) && !merchantDetailsVO.getLogin().equalsIgnoreCase(invoiceVO.getUserName())){
            String userid=merchants.isMemberUser(invoiceVO.getUserName(),invoiceVO.getMemberid());
            if(!functions.isValueNull(userid)){
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_UNAUTHORIZE_MEMBER);
                if (invoiceVO.getErrorCodeListVO() != null)
                    invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return invoiceVO;
            }
            merchantDetailsVO.setLogin(invoiceVO.getUserName());
            merchantDetailsVO.setRole("submerchant");
            merchantDetailsVO.setUserid(userid);

        }

        if (invoiceVO.getPaginationVO().getPageNo() == 0 || invoiceVO.getPaginationVO().getRecordsPerPage() == 0)
        {
            //errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_RECORDS);
            error ="Invalid page number or Invalid records.";
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());

        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        return invoiceVO;

    }

    //new
    public  InvoiceVO performGetInvoiceDetailsValidation (InvoiceVO invoiceVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException

    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String memberId =invoiceVO.getMemberid();
        // System.out.println("start date----"+invoiceVO.getPaginationVO().getStartdate());
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoicelistMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);
        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInvoicelistOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        if (functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());


        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        return invoiceVO;

    }

    //new
    public  InvoiceVO performSetInvoiceConfigDetailsValidation (InvoiceVO invoiceVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException

    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String memberId =invoiceVO.getMemberid();
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();

        if (!functions.isValueNull(invoiceVO.getExpirationPeriod()))
        {
            invoiceVO.setExpirationPeriod("0");
        }

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoiceConfigListMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);
        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInvoiceConfigListOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        if(invoiceVO.getDefaultProductList()!=null)
        {
            List<InputFields> inputInvoiceDefualtProductOptionalList = new ArrayList<InputFields>();
            inputInvoiceDefualtProductOptionalList.addAll(inputValiDatorUtils.getInvoiceConfigDefaultProductOptionalParams());
            ValidationErrorList optionalDefaultProductErrorList = new ValidationErrorList();
            for (DefaultProductList defaultProductList : invoiceVO.getDefaultProductList())
            {
                invoiceVO.setDefaultProduct(defaultProductList);
                optionalDefaultProductErrorList = new ValidationErrorList();
                RestInputValidations(invoiceVO, inputInvoiceDefualtProductOptionalList, optionalDefaultProductErrorList, true);
                error = error + inputValiDatorUtils.getError(optionalDefaultProductErrorList, inputInvoiceDefualtProductOptionalList, actionName);
            }
        }
if(invoiceVO.getDefaultunitList()!=null)
{
    List<InputFields> inputInvoiceDefaultUnitOptionalList = new ArrayList<InputFields>();
    inputInvoiceDefaultUnitOptionalList.addAll(inputValiDatorUtils.getInvoiceConfigDefaultUnitOptionalParams());
    ValidationErrorList optionalDefaultUnitErrorList = new ValidationErrorList();
    for (UnitList defaultUnitList : invoiceVO.getDefaultunitList())
    {
        invoiceVO.setUnitlist(defaultUnitList);
        optionalDefaultUnitErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceDefaultUnitOptionalList, optionalDefaultUnitErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalDefaultUnitErrorList, inputInvoiceDefaultUnitOptionalList, actionName);
    }
}
        if (functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }

        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }
        /*if(!Checksum.verifyMD5ChecksumForMerchantCurrencies(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(), invoiceVO.getChecksum()))
        {*/
        if(!Checksum.verifyMD5ChecksumForMerchantCurrenciesInvoiceConfig(merchantDetailsVO.getMemberId(), merchantDetailsVO.getKey(),invoiceVO.getRedirecturl(), invoiceVO.getChecksum()))
        {
            error = "Checksum- Illegal Access. CheckSum mismatch";
            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CHECKSUM));
            PZExceptionHandler.raiseConstraintViolationException("RestCommonInputValidator.class", "performCommonSystemChecksStep1()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, invoiceVO.getErrorCodeListVO(), null, null);
        }
        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());


        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        return invoiceVO;

    }

    //new
    public  InvoiceVO performGetOrderIdValidation (InvoiceVO invoiceVO ,String actionName) throws  PZDBViolationException ,NoSuchAlgorithmException ,PZConstraintViolationException

    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        String memberId =invoiceVO.getMemberid();
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        PartnerManager partnerManager = new PartnerManager();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInvoiceOrderIdMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList, inputInvoiceMandatoryList, actionName);

        if (functions.isValueNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        MerchantDetailsVO merchantDetailsVO = invoiceVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        merchantDetailsVO = merchantDAO.getMemberDetails(memberId);

        if (!functions.isValueNull(merchantDetailsVO.getMemberId()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID);
            if (invoiceVO.getErrorCodeListVO() != null)
                invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return invoiceVO;
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(merchantDetailsVO.getPartnerId());

        invoiceVO.setMerchantDetailsVO(merchantDetailsVO);
        invoiceVO.setPartnerDetailsVO(partnerDetailsVO);
        return invoiceVO;

    }

    public String validateInquiryInvoiceParams (InvoiceVO invoiceVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getInquiryInvoiceMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getInquiryInvoiceOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        return error;
    }

    public InvoiceVO performValidationCancelInvoiceParams(InvoiceVO invoiceVO) throws PZDBViolationException, NoSuchAlgorithmException, PZConstraintViolationException
    {
        String error = "";

        error = validateCancelInvoiceParams(invoiceVO, "REST");
        if(!functions.isEmptyOrNull(error))
        {
            invoiceVO.setErrorMsg(error);
            return invoiceVO;
        }
        return invoiceVO;
    }

    public String validateRestFlagBasedAddressField(InvoiceVO invoiceVO, String actionName)
    {
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        List<InputFields> inputFlagBasedAddressValidation = new ArrayList<InputFields>();
        inputFlagBasedAddressValidation.addAll(inputValiDatorUtils.getAddressFieldValidation());
        ValidationErrorList addressValidationError = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputFlagBasedAddressValidation, addressValidationError, false);
        error = error + inputValiDatorUtils.getError(addressValidationError,inputFlagBasedAddressValidation,actionName);

        return error;
    }

    public CommonValidatorVO performRestMerchantLoginValidation(CommonValidatorVO commonValidatorVO)
    {
        String error = "";
        error = validateRestKitMerchantLoginParameters(commonValidatorVO, "REST");
        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        return commonValidatorVO;
    }

    public CommonValidatorVO performRestMerchantAuthTokenValidation(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        String error = "";
        error = validateRestKitMerchantAuthTokenParameters(commonValidatorVO, "REST");
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        MerchantDAO merchantDAO = new MerchantDAO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        PartnerManager partnerManager = new PartnerManager();
        PartnerDetailsVO partnerDetailsVO = null;
        String password = merchantDetailsVO.getPassword();
        String sKey = merchantDetailsVO.getKey();
        String loginName = merchantDetailsVO.getLogin();

        if (functions.isValueNull(error))
        {
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }

        if (functions.isValueNull(merchantDetailsVO.getLogin()))
        {
            merchantDetailsVO = merchantDAO.getMemberDetailsByLogin(merchantDetailsVO.getLogin());
            if (!loginName.equalsIgnoreCase(merchantDetailsVO.getLogin()))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_LOGIN);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }
        if (functions.isValueNull(sKey))
        {
            boolean isKey = merchantDAO.authenticateMemberViaKey(loginName, sKey, commonValidatorVO.getParetnerId());
            if (!isKey)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                return commonValidatorVO;
            }
        }

        partnerDetailsVO = partnerManager.getPartnerDetails(commonValidatorVO.getParetnerId());
        if (partnerDetailsVO == null)
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_PARTNERID);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        merchantDetailsVO.setPassword(password);

        merchantDetailsVO.setKey(sKey);
        merchantDetailsVO.setLogin(loginName);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);

        return commonValidatorVO;

    }

    private String validateRestKitMerchantAuthTokenParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestMerchantAuthTokenParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputMandatoryFieldsList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

        List<InputFields> inputOptionalFieldsList = new ArrayList<InputFields>();
        inputOptionalFieldsList.addAll(inputValiDatorUtils.getRestMerchantAuthTokenOptionalParameters());
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO, inputOptionalFieldsList, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1,inputOptionalFieldsList,actionName);
        return error;

    }



    private String validateRestKitMerchantLoginParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidatorForMerchantServices inputValidator = new InputValidatorForMerchantServices();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.addAll(inputValiDatorUtils.getRestMerchantLoginParameters());
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.restInputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);
        return error;

    }



    public String validateCancelInvoiceParams (InvoiceVO invoiceVO, String actionName)
    {
        String error = "";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();

        //Validate invoice mandatory parameters
        List<InputFields> inputInvoiceMandatoryList = new ArrayList<InputFields>();
        inputInvoiceMandatoryList.addAll(inputValiDatorUtils.getCancelInvoiceMandatoryParams());
        ValidationErrorList errorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceMandatoryList, errorList, false);
        error = error + inputValiDatorUtils.getError(errorList,inputInvoiceMandatoryList,actionName);

        //Validate invoice optional parameters
        List<InputFields> inputInvoiceOptionalList = new ArrayList<InputFields>();
        inputInvoiceOptionalList.addAll(inputValiDatorUtils.getCancelInvoiceOptionalParams());
        ValidationErrorList optionalErrorList = new ValidationErrorList();
        RestInputValidations(invoiceVO, inputInvoiceOptionalList, optionalErrorList, true);
        error = error + inputValiDatorUtils.getError(optionalErrorList,inputInvoiceOptionalList,actionName);

        return error;
    }

    public void RestInputValidations(InvoiceVO invoiceVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        String resField2 = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case TOID:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getMemberid(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TOID, ErrorMessages.INVALID_TOID + ":::" + invoiceVO.getMemberid(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case INVOICENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getInvoiceno(), "Numbers", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_INVOICE_NO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_INVOICE_NO, ErrorMessages.INVALID_INVOICE_NO + ":::" + invoiceVO.getInvoiceno(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getAmount(), "tenDigitAmount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_AMOUNT, ErrorMessages.INVALID_AMOUNT + ":::" + invoiceVO.getAmount(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(InputFields.DESCRIPTION.toString(), invoiceVO.getDescription(), "Description", 35, isOptional) || (functions.isValueNull(invoiceVO.getDescription()) && invoiceVO.getDescription().trim().equals("") ) )
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DESCRIPTION, ErrorMessages.INVALID_DESCRIPTION + ":::" + invoiceVO.getDescription(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(InputFields.ORDERDESCRIPTION.toString(), invoiceVO.getOrderDescription(), "Description", 100, isOptional) || functions.isValueNull(invoiceVO.getOrderDescription()) && invoiceVO.getOrderDescription().trim().equals(""))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ORDER_DESCRIPTION, ErrorMessages.INVALID_ORDER_DESCRIPTION + ":::" + invoiceVO.getOrderDescription(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CURRENCY:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getCurrency(), "currency_unit", 3, isOptional) /*|| genericTransDetailsVO.getCurrency().length() < 3 || genericTransDetailsVO.getCurrency().length() > 3*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + invoiceVO.getCurrency(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMAIL, ErrorMessages.INVALID_EMAIL + ":::" + invoiceVO.getEmail(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getRedirecturl(), "URL", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REDIRECT_URL, ErrorMessages.INVALID_REDIRECT_URL + ":::" + invoiceVO.getRedirecturl(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CHECKSUM:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getChecksum(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECKSUM);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CHECKSUM, ErrorMessages.INVALID_CHECKSUM + ":::" + invoiceVO.getChecksum(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(InputFields.COUNTRY.name(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + invoiceVO.getCountry(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getZip(), "Zip", 9, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(InputFields.ZIP.name(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + invoiceVO.getZip(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getCity(), "City", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + invoiceVO.getCity(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getStreet(), "alphanum", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + invoiceVO.getStreet(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getState(), "State", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + invoiceVO.getState(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getTelCc(), "Phone", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNOCC, ErrorMessages.INVALID_TELNOCC + ":::" + invoiceVO.getTelCc(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getTelno(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + invoiceVO.getTelCc(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CUSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getCustName(), "SafeString", 100, isOptional) || functions.hasHTMLTags(invoiceVO.getCustName()) || functions.isValueNull(invoiceVO.getCustName()) && invoiceVO.getCustName().trim().equals(""))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTMER_NAME, ErrorMessages.INVALID_CUSTMER_NAME + ":::" + invoiceVO.getCustName(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TERMINALID:
                    if (functions.isValueNull(invoiceVO.getTerminalid()) && invoiceVO.getTerminalid().trim().equals("") || !ESAPI.validator().isValidInput(input.toString(), invoiceVO.getTerminalid(), "Numbers", 6, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TERMINALID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TERMINALID, ErrorMessages.INVALID_TERMINALID + ":::" + invoiceVO.getTerminalid(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REASON:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getCancelReason(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CANCEL_INVOICE_REASON);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REASON, ErrorMessages.INVALID_REASON + ":::" + invoiceVO.getCancelReason(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case INVOICE_EXPIRATIONPERIOD:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getExpirationPeriod(), "Numbers", 3, isOptional) )
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_INVOICE_EXPIRATIONPERIOD);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_INVOICE_EXPIRATIONPERIOD, ErrorMessages.INVALID_INVOICE_EXPIRATIONPERIOD + ":::" + invoiceVO.getMemberid(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAGENO:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf(invoiceVO.getPaginationVO().getPageNo()), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAGE_NO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAGE_NO, ErrorMessages.INVALID_PAGE_NO + ":::" + invoiceVO.getPaginationVO().getPageNo(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case RECORDS:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf((invoiceVO.getPaginationVO().getRecordsPerPage())), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_RECORDS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_RECORDS, ErrorMessages.INVALID_RECORDS + ":::" + invoiceVO.getPaginationVO().getRecordsPerPage(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FROMDATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getPaginationVO().getStartdate(), "fromDate", 25, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FROM_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FROM_DATE, ErrorMessages.INVALID_FROM_DATE + ":::" + invoiceVO.getPaginationVO().getStartdate(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TODATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getPaginationVO().getEnddate(), "fromDate", 25, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TO_DATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TO_DATE, ErrorMessages.INVALID_TO_DATE + ":::" + invoiceVO.getPaginationVO().getEnddate(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case START:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf((invoiceVO.getPaginationVO().getStart())), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_START);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_START, ErrorMessages.INVALID_START + ":::" + invoiceVO.getPaginationVO().getStart(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case END:
                    if (!ESAPI.validator().isValidInput(input.toString(), String.valueOf((invoiceVO.getPaginationVO().getEnd())), "Numbers", 5, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_END);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_END, ErrorMessages.INVALID_END + ":::" + invoiceVO.getPaginationVO().getEnd(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACTION_TYPE:
                    if (!ESAPI.validator().isValidInput(input.toString(), ((invoiceVO.getInvoiceAction())), "actionType", 55, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_INVOICEACTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ACTION_TYPE, ErrorMessages.INVALID_ACTION_TYPE + ":::" + invoiceVO.getInvoiceAction(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case GST:
                    if (!ESAPI.validator().isValidInput(input.toString(), ((invoiceVO.getGST())), "Amount", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_GST);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_GST, ErrorMessages.INVALID_GST + ":::" + invoiceVO.getGST()  , errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case USERNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(),(invoiceVO.getUserName()),"UserName" , 255, isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_USERNAME);
                        validationErrorList.addError(input.toString(),new PZValidationException(ErrorMessages.INVALID_LOGIN,ErrorMessages.INVALID_LOGIN + ":::" + invoiceVO.getUserName(),errorCodeVO));
                        if(invoiceVO.getErrorCodeListVO()!=null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LATEFEE:
                    if ((!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getLatefee(), "tenDigitAmount", 13, isOptional))/* ||  "0.00".equalsIgnoreCase(invoiceVO.getLatefee()*/)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LATEFEE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LATEFEE, ErrorMessages.INVALID_LATEFEE+ ":::" + invoiceVO.getAmount(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case QUANTITY:
                    if ((!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getProduct().getQuantity(), "Amount", 5, isOptional)) ||  "0.00".equalsIgnoreCase(invoiceVO.getProduct().getQuantity()) ||  "0".equalsIgnoreCase(invoiceVO.getProduct().getQuantity()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_QUANTITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_QUANTITY, ErrorMessages.INVALID_QUANTITY+ ":::" + invoiceVO.getProduct().getQuantity(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PRODUCT_AMOUNT:
                    if ((!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getProduct().getProductAmount(), "tenDigitAmount", 13, isOptional)) ||  "0.00".equalsIgnoreCase(invoiceVO.getProduct().getProductAmount()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PRODUCT_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PRODUCT_AMOUNT, ErrorMessages.INVALID_PRODUCT_AMOUNT+ ":::" + invoiceVO.getProduct().getProductAmount(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PRODUCT_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getProduct().getProductDescription(), "Description", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PRODUCT_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PRODUCT_DESCRIPTION, ErrorMessages.INVALID_PRODUCT_DESCRIPTION + ":::" + invoiceVO.getProduct().getProductDescription(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case PRODUCT_TOTAL:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getProduct().getProductTotal(), "tenDigitAmount", 13, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PRODUCT_TOTAL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PRODUCT_TOTAL, ErrorMessages.INVALID_PRODUCT_TOTAL + ":::" + invoiceVO.getProduct().getProductTotal(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DEFAULT_QUANTITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getDefaultProduct().getQuantity(), "Amount", 5, isOptional) ||  "0.00".equalsIgnoreCase(invoiceVO.getDefaultProduct().getQuantity()) ||  "0".equalsIgnoreCase(invoiceVO.getDefaultProduct().getQuantity()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_QUANTITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_QUANTITY, ErrorMessages.INVALID_QUANTITY+ ":::" + invoiceVO.getDefaultProduct().getQuantity(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DEFAULT_PRODUCT_AMOUNT:
                    if ((!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getDefaultProduct().getProductAmount(), "Amount", 13, isOptional)) ||  "0.00".equalsIgnoreCase(invoiceVO.getDefaultProduct().getProductAmount()) ||  "0".equalsIgnoreCase(invoiceVO.getDefaultProduct().getProductAmount()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PRODUCT_AMOUNT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PRODUCT_AMOUNT, ErrorMessages.INVALID_PRODUCT_AMOUNT+ ":::" + invoiceVO.getDefaultProduct().getProductAmount(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case DEFAULT_PRODUCT_DESCRIPTION:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getDefaultProduct().getProductDescription(), "Description", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PRODUCT_DESCRIPTION);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DESCRIPTION, ErrorMessages.INVALID_DESCRIPTION + ":::" + invoiceVO.getDefaultProduct().getProductDescription(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case QUANTITY_TOTAL:
                    if ((!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getQuantityTotal(), "Amount", 5, isOptional))||  "0.00".equalsIgnoreCase(invoiceVO.getQuantityTotal()) ||  "0".equalsIgnoreCase(invoiceVO.getQuantityTotal()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_QUANTITY_TOTAL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_QUANTITY_TOTAL, ErrorMessages.INVALID_QUANTITY_TOTAL+ ":::" + invoiceVO.getQuantityTotal(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case PAYMENTTERMS:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getPaymentterms(), "SafeString", 50, isOptional) || functions.hasHTMLTags(invoiceVO.getPaymentterms()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_TERMS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_PAYMENT_TERMS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_PAYMENTTERMS, ErrorMessages.INVALID_PAYMENTTERMS + ":::" + invoiceVO.getPaymentterms(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case UNIT:
                    if (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getUnit(), "SafeString", 50, isOptional) || functions.hasHTMLTags(invoiceVO.getUnit()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_UNIT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_UNIT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_UNIT, ErrorMessages.INVALID_UNIT + ":::" + invoiceVO.getUnit(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case DEFAULTUNIT:
                    if (invoiceVO.getUnitlist()!=null && (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getUnitlist().getDefaultunit(), "SafeString", 50, isOptional) || functions.hasHTMLTags(invoiceVO.getUnitlist().getDefaultunit())))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_DEFAULTUNIT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_DEFAULTUNIT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_DEFAULTUNIT, ErrorMessages.INVALID_DEFAULTUNIT + ":::" + invoiceVO.getUnitlist().getDefaultunit(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                case DEFAULT_PRODUCT_UNIT:
                    if (invoiceVO.getDefaultProduct()!=null && (!ESAPI.validator().isValidInput(input.toString(), invoiceVO.getDefaultProduct().getUnit(), "SafeString", 50, isOptional) || functions.hasHTMLTags(invoiceVO.getDefaultProduct().getUnit())))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_UNIT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_UNIT);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_UNIT, ErrorMessages.INVALID_UNIT + ":::" + invoiceVO.getDefaultProduct().getUnit(), errorCodeVO));
                        if (invoiceVO.getErrorCodeListVO() != null)
                            invoiceVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }

}