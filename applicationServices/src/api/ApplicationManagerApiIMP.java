package api;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Member;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.enums.ApplicationManagerTypes;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.manager.PartnerManager;
import com.manager.dao.MerchantDAO;
import com.enums.ApplicationManagerMode;
import com.enums.ApplicationStatus;
import com.enums.Module;
import com.manager.helper.TransactionHelper;
import com.manager.utils.TransactionUtil;
import com.manager.vo.DirectKitResponseVO;

import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PartnerDetailsVO;
import com.merchant.manager.RegistrationManager;


import com.payment.errors.ErrorMessages;
import com.payment.response.PZResponseStatus;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;
import com.utils.WriteMerchantServiceResponse;
import com.vo.applicationManagerVOs.*;
import com.vo.requestVOs.*;
import com.vo.responseVOs.ApplicationManagerResponse;
import com.vo.responseVOs.Result;
import com.vo.responseVOs.ValidationError;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.*;

/**
 * Created by NIKET on 2/12/2016.
 */

@Path("/ApplicationManager")
public class ApplicationManagerApiIMP implements ApplicationManagerApiService
{
    private static Functions functions = new Functions();
    private static Logger logger=new Logger(ApplicationManagerApiIMP.class.getName());
    private static AppRequestManager requestManager = new AppRequestManager();


    private WriteMerchantServiceResponse writeMerchantServiceResponse = new WriteMerchantServiceResponse();
    private RestCommonInputValidator restCommonInputValidator = new RestCommonInputValidator();
    private RegistrationManager registrationManager = new RegistrationManager();
    private AppRequestManager appRequestManager = new AppRequestManager();
    private ApplicationManager applicationManager = new ApplicationManager();
    private TransactionUtil transactionUtil = new TransactionUtil();

    private TransactionLogger transactionLogger = new TransactionLogger(ApplicationManagerApiIMP.class.getName());


    @Context
    HttpServletResponse response;

    @Context
    HttpServletRequest request1;

    public ApplicationManagerResponse submitMerchantApplicationForm(@InjectParam ApplicationManagerAuthenticationRequest authenticationRequest)
    {
        if(request1.getQueryString()!=null){
            ApplicationManagerResponse response1 = new ApplicationManagerResponse();
            Result result=new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }


        ApplicationManagerResponse response = new ApplicationManagerResponse();
        ApplicationManagerAuthentication authentication = convertRequestToVO(authenticationRequest);
    /*    if (authentication != null)
        {
            authentication.setApplicationManagerVO(getapplicationmanagerVo(authenticationRequest.getApplicationManagerRequestVO()));
        }*/
        response = submitMerchantApplicationFormJSON(authentication);
        return response;
    }




    public ApplicationManagerResponse submitMerchantApplicationFormJSON(ApplicationManagerAuthentication applicationManagerAuthentication)
    {

        //Manager instance
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        ApplicationManagerVO dataBaseApplicationManagerVO=new ApplicationManagerVO();
        ApplicationManagerVO requestApplicationManagerVO=null;
        NavigationVO navigationVO =null;
        AppValidationVO appValidationVO =null;

        //ValidationErrorList instance
        ValidationErrorList validationErrorList = null;
        List<ValidationError> validationErrors=new ArrayList<ValidationError>();
        Set<Integer> contextInvalid= new TreeSet<Integer>();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        //Response VO
        ApplicationManagerResponse applicationManagerResponse=new ApplicationManagerResponse();


        try
        {
            //DECRYPTION And Authentication For Merchant and Extract MemberDetails
            if(applicationManagerAuthentication!=null && (!functions.isValueNull(applicationManagerAuthentication.getMemberId()) || !ESAPI.validator().isValidInput("memberid", applicationManagerAuthentication.getMemberId(), "OnlyNumber", 10, false) || !functions.isValueNull(applicationManagerAuthentication.getChecksum()) || !ESAPI.validator().isValidInput("random", applicationManagerAuthentication.getRandom(), "onlyAlphanum", 10, false)))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MEMBERID_RANDOM_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","checksum,memberid,random","merchant","Please Provide Checksum,MemberId And Random", PZConstraintExceptionEnum.CTOKEN_MISMATCH, errorCodeListVO, "Please Provide Checksum,MemberId And Random",new Throwable("Please Provide Checksum,MemberId And Random"));
            }

            if(!merchantDAO.authenticateMemberViaChecksum(applicationManagerAuthentication.getMemberId(), applicationManagerAuthentication.getChecksum(),applicationManagerAuthentication.getRandom()))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","checksum,memberid,Random","merchant","Merchant Authentication Failure", PZConstraintExceptionEnum.CTOKEN_MISMATCH, errorCodeListVO, "Merchant Authentication Failure",new Throwable("Merchant Authentication Failure"));
            }

            if(applicationManagerAuthentication!=null && (!functions.isValueNull(applicationManagerAuthentication.getMode())) || !ApplicationManagerMode.isInEnum(applicationManagerAuthentication.getMode()))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MODE);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Please Provide Mode", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Please Provide Mode", new Throwable("Please Provide Mode"));
            }

            System.out.println("above request");

            requestApplicationManagerVO=applicationManagerAuthentication.getApplicationManagerVO();



            if(requestApplicationManagerVO!=null)
            {
                System.out.println("inside if");
                requestApplicationManagerVO=appRequestManager.getApplicationManagerVO(requestApplicationManagerVO);
                navigationVO = appRequestManager.getNavigationVO(null, Module.MERCHANT);

                applicationManagerVO.setMemberId(applicationManagerAuthentication.getMemberId());
                System.out.println("memberid inside applicationmanager"+applicationManagerAuthentication.getMemberId());
                applicationManager.populateAppllicationData(applicationManagerVO);
                applicationManagerVO.setUser(Module.MERCHANT.name());

                if(ApplicationManagerMode.CREATE.toString().equals(applicationManagerAuthentication.getMode()) && ((applicationManagerVO!=null && ((ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getMaf_Status())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.SAVED.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.MODIFIED.toString().equals(applicationManagerVO.getMaf_Status()))|| (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus()))))))
                {
                    System.out.println("mode---"+applicationManagerAuthentication.getMode());
                    errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_CREATE_MODE);
                    applicationManagerVO.setStatus(null);
                    PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Create Mode is not valid since the application is present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Create Mode is not valid since the application is present",new Throwable("Create Mode is not valid since the application is present"));
                }

                //Modify the required details and save it under same aplicationId
                else if(ApplicationManagerMode.MODIFY.toString().equals(applicationManagerAuthentication.getMode()) && ((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getMaf_Status()) || !functions.isValueNull(applicationManagerVO.getMaf_Status())))))
                {
                    if(applicationManagerVO==null || (applicationManagerVO!=null && !functions.isValueNull(applicationManagerVO.getMaf_Status())))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MODIFY_MODE_APP_NOT_PRESENT);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Modify Mode is not valid since the application is not Present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is not Present",new Throwable("Update Mode is not valid since the application is not Present"));
                    }
                    else
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_MODIFY_MODE_APP_SUBMITED);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Modify Mode is not valid since the application is Submitted/Verified", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is Submitted/Verified",new Throwable("Update Mode is not valid since the application is Submitted/Verified"));
                    }
                }

                //Update entire data under same applicationId
                else if (ApplicationManagerMode.UPDATE.toString().equals(applicationManagerAuthentication.getMode()) && ((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getMaf_Status()) || !functions.isValueNull(applicationManagerVO.getMaf_Status())))))
                {
                    if(applicationManagerVO==null || (applicationManagerVO!=null && !functions.isValueNull(applicationManagerVO.getMaf_Status())))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_UPDATE_MODE_APP_NOT_PRESENT);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Update Mode is not valid since the application is not Present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is not Present",new Throwable("Update Mode is not valid since the application is not Present"));
                    }
                    else
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_UPDATE_MODE_APP_SUBMITED);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "submitMerchantApplicationForm()", "mode", "merchant", "Update Mode is not valid since the application is Submitted/Verified", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is Submitted/Verified", new Throwable("Update Mode is not valid since the application is Submitted/Verified"));
                    }
                }

                if(ApplicationManagerMode.UPDATE.toString().equals(applicationManagerAuthentication.getMode()) && !((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getMaf_Status()) || !functions.isValueNull(applicationManagerVO.getMaf_Status())))))
                {
                    requestApplicationManagerVO = appRequestManager.getApplicationManagerVO(requestApplicationManagerVO);
                    appRequestManager.getApplicationSavedStatus(applicationManagerVO, requestApplicationManagerVO);
                }

                appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
                applicationManagerVO=appRequestManager.getApplicationManagerVO(applicationManagerVO);
                //getting applicationManagerVO & navigationVO
                dataBaseApplicationManagerVO = applicationManagerVO;

                validationErrorList = appRequestManager.validationForInterfaceOrApi(null, requestApplicationManagerVO, navigationVO, applicationManagerVO, true, false, appValidationVO);

                if (!validationErrorList.isEmpty())
                {
                    validationErrors=new ArrayList<ValidationError>();

                    appRequestManager.setApplicationDetailsAfterValidation(applicationManagerVO,dataBaseApplicationManagerVO,navigationVO,validationErrorList);

                    applicationManagerVO.setNotificationMessage(applicationManager.saveAllPageForApi(navigationVO, applicationManagerVO, ApplicationStatus.SAVED),null,null);

                    for(ValidationException validationException:validationErrorList.errors())
                    {
                        PZValidationException pzValidationException = (PZValidationException) validationException;

                        logger.debug("Context::: " + validationException.getContext() + "----" + validationException.getMessage());
                        contextInvalid.add(Integer.valueOf(validationException.getContext()));

                        if(Integer.valueOf(validationException.getContext())!=6)
                        {
                            ValidationError validationError = new ValidationError();
                            validationError.setFieldName(pzValidationException.getMessage());
                            validationError.setMessage(pzValidationException.getLogMessage());
                            System.out.println("apicode---"+pzValidationException.getErrorCodeVO().getApiCode());
                            if(pzValidationException!=null)
                            {
                                validationError.setResultCode(pzValidationException.getErrorCodeVO().getApiCode());
                                validationError.setResultDescription(pzValidationException.getErrorCodeVO().getApiDescription());
                            }

                            validationErrors.add(validationError);
                        }
                    }
                }
                else
                {
                    if(!functions.isValueNull(dataBaseApplicationManagerVO.getKyc_Status()) || ApplicationStatus.SAVED.toString().equals(dataBaseApplicationManagerVO.getKyc_Status()) || ApplicationStatus.MODIFIED.toString().equals(dataBaseApplicationManagerVO.getKyc_Status()))
                    {
                        System.out.println("inside if save");
                        applicationManagerVO.setNotificationMessage(applicationManager.saveAllPageForApi(navigationVO,applicationManagerVO,ApplicationStatus.SUBMIT),"All Profile"," Please Upload KYC");
                    }
                    else
                    {
                        applicationManagerVO.setNotificationMessage(applicationManager.submitAllProfile(applicationManagerVO, false, false), "All Profile", " Submitted Successfully");
                    }
                }
            }
            else
            {
                logger.debug("No Merchant Application Details Has been Submitted");
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                ValidationError validationError = new ValidationError();

                validationErrors=new ArrayList<ValidationError>();
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.MAF_VALIDATION_APPLICATION_NULL);

                validationError.setResultCode(errorCodeVO.getApiCode());
                validationError.setResultDescription(errorCodeVO.getApiDescription());
                validationErrors.add(validationError);
            }

        }
        catch (PZConstraintViolationException pce)
        {
            logger.error("Exception in Navigation Page",pce);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setResultCode(pce.getPzConstraint().getErrorCodeListVO().getApiCode());
            validationError.setResultDescription(pce.getPzConstraint().getErrorCodeListVO().getApiDescription());
            validationErrors.add(validationError);
        }
        catch(Exception e)
        {
            logger.error("Exception in Navigation Page",e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            /*validationError.setFieldName("");
            validationError.setMessage("Internal Issue Please Contact Support Team For More Details");*/
            validationError.setResultCode("22222");
            validationError.setResultDescription("Internal Issue Please Contact Support Team For More Details");
            validationErrors.add(validationError);
        }

        applicationManagerResponse.setValidationError(validationErrors);

        if(ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()))
        {
            validationErrors = new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();

            errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_SUBMITTED_SUCCESSFULLY);

            validationError.setResultCode(errorCodeListVO.getApiCode());
            validationError.setResultDescription(errorCodeListVO.getApiDescription());
            validationErrors.add(validationError);

            dataBaseApplicationManagerVO.setBusinessProfileVO(null);
            dataBaseApplicationManagerVO.setBankProfileVO(null);
            dataBaseApplicationManagerVO.setCardholderProfileVO(null);
            dataBaseApplicationManagerVO.setCompanyProfileVO(null);
            dataBaseApplicationManagerVO.setExtradetailsprofileVO(null);
            dataBaseApplicationManagerVO.setOwnershipProfileVO(null);
            dataBaseApplicationManagerVO.setUploadLabelVOs(null);
            dataBaseApplicationManagerVO.setFileDetailsVOs(null);
            dataBaseApplicationManagerVO.setMessageColorClass(null);
            dataBaseApplicationManagerVO.setStandby_user(null);



            applicationManagerResponse.setApplicationManagerVO(dataBaseApplicationManagerVO);
            applicationManagerResponse.setStatusDescription("SUBMITTED_SUCCESSFULLY(All Merchant Application Form Details As Well As Kyc Documents Has been submitted successfully)");
        }

        if(validationErrors.isEmpty())
        {
            validationErrors = new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();

            errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_PROFILE_SAVED);

            validationError.setResultCode(errorCodeListVO.getApiCode());
            validationError.setResultDescription(errorCodeListVO.getApiDescription());
            validationErrors.add(validationError);

            dataBaseApplicationManagerVO.setBusinessProfileVO(null);
            dataBaseApplicationManagerVO.setBankProfileVO(null);
            dataBaseApplicationManagerVO.setCardholderProfileVO(null);
            dataBaseApplicationManagerVO.setCompanyProfileVO(null);
            dataBaseApplicationManagerVO.setExtradetailsprofileVO(null);
            dataBaseApplicationManagerVO.setOwnershipProfileVO(null);
            dataBaseApplicationManagerVO.setUploadLabelVOs(null);
            dataBaseApplicationManagerVO.setFileDetailsVOs(null);
            dataBaseApplicationManagerVO.setMessageColorClass(null);
            dataBaseApplicationManagerVO.setStandby_user(null);

            applicationManagerResponse.setValidationError(validationErrors);
            applicationManagerResponse.setApplicationManagerVO(dataBaseApplicationManagerVO);
        }

        //applicationManagerResponse.setStatus(dataBaseApplicationManagerVO.getStatus());

        return applicationManagerResponse;
    }

    public ApplicationManagerResponse submitMerchantApplicationForSpeedProcess(@InjectParam ApplicationManagerAuthenticationRequest authenticationRequest)
    {
        if(request1.getQueryString()!=null){
            ApplicationManagerResponse response1 = new ApplicationManagerResponse();
            Result result=new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        System.out.println("inside mafinquiry url");

        ApplicationManagerResponse response = new ApplicationManagerResponse();
        ApplicationManagerAuthentication authentication = convertRequestToVO(authenticationRequest);
        if (authentication != null)
        {
            authentication.setApplicationManagerVO(getapplicationmanagerVo(authenticationRequest.getApplicationManagerRequestVO()));
        }
        response = submitMerchantApplicationForSpeedProcessJSON(authentication);
        return response;
    }
    public ApplicationManagerResponse submitMerchantApplicationForSpeedProcessJSON(ApplicationManagerAuthentication requestApplicationManagerAuthentication)
    {
        //Manager instance
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        ApplicationManagerVO dataBaseApplicationManagerVO=new ApplicationManagerVO();
        ApplicationManagerVO requestApplicationManagerVO=null;

        //ValidationErrorList instance
        ValidationErrorList validationErrorList = null;
        List<ValidationError> validationErrors=new ArrayList<ValidationError>();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        //Response VO
        ApplicationManagerResponse applicationManagerResponse=new ApplicationManagerResponse();

        try
        {
            if(requestApplicationManagerAuthentication!=null && (!functions.isValueNull(requestApplicationManagerAuthentication.getMemberId()) || !ESAPI.validator().isValidInput("memberid", requestApplicationManagerAuthentication.getMemberId(), "OnlyNumber", 10, false) || !functions.isValueNull(requestApplicationManagerAuthentication.getChecksum()) || !ESAPI.validator().isValidInput("random", requestApplicationManagerAuthentication.getRandom(), "onlyAlphanum", 10, false)))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MEMBERID_RANDOM_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","Checksum,memberid,Random","merchant","Please Provide Checksum,MemberId And Random", PZConstraintExceptionEnum.CTOKEN_MISMATCH, errorCodeListVO,"Please Provide Checksum,MemberId And Random",new Throwable("Please Provide Checksum,MemberId And Random"));
            }
            if(!merchantDAO.authenticateMemberViaChecksum(requestApplicationManagerAuthentication.getMemberId(), requestApplicationManagerAuthentication.getChecksum(), requestApplicationManagerAuthentication.getRandom()))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","Checksum,memberid,random","merchant","Merchant Authentication Failure", PZConstraintExceptionEnum.CTOKEN_MISMATCH,errorCodeListVO,"Merchant Authentication Failure",new Throwable("Merchant Authentication Failure"));
            }
            if(requestApplicationManagerAuthentication!=null && (!functions.isValueNull(requestApplicationManagerAuthentication.getMode())) || !ApplicationManagerMode.isInEnum(requestApplicationManagerAuthentication.getMode()))
            {
                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MODE);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Please Provide Mode", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, errorCodeListVO, "Please Provide Mode", new Throwable("Please Provide Mode"));
            }

            requestApplicationManagerVO = requestApplicationManagerAuthentication.getApplicationManagerVO();
            if(requestApplicationManagerVO!=null)
            {
                requestApplicationManagerVO=appRequestManager.getApplicationManagerVO(requestApplicationManagerVO);

                applicationManagerVO.setMemberId(requestApplicationManagerAuthentication.getMemberId());
                applicationManager.populateAppllicationData(applicationManagerVO);
                applicationManagerVO.setSpeed_user(Module.MERCHANT.name());

                if(ApplicationManagerMode.CREATE.toString().equals(requestApplicationManagerAuthentication.getMode()) && ((applicationManagerVO!=null && ((ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getSpeed_status())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getSpeed_status()) || ApplicationStatus.STEP1_SAVED.toString().equals(applicationManagerVO.getSpeed_status()) || ApplicationStatus.MODIFIED.toString().equals(applicationManagerVO.getSpeed_status()))|| (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus()))))))
                {
                    errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_CREATE_MODE);
                    applicationManagerVO.setStatus(null);
                    PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Create Mode is not valid since the application is present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Create Mode is not valid since the application is present",new Throwable("Create Mode is not valid since the application is present"));
                }

                //Modify the required details and save it under same aplicationId
                else if(ApplicationManagerMode.MODIFY.toString().equals(requestApplicationManagerAuthentication.getMode()) && ((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getSpeed_status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getSpeed_status())))))
                {
                    if(applicationManagerVO!=null && !functions.isValueNull(applicationManagerVO.getSpeed_status()))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MODIFY_MODE_APP_NOT_PRESENT);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Modify Mode is not valid since the application is not Present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is not Present",new Throwable("Update Mode is not valid since the application is not Present"));
                    }
                    else
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_MODIFY_MODE_APP_SUBMITED);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Modify Mode is not valid since the application is Submitted/Verified", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is Submitted/Verified",new Throwable("Update Mode is not valid since the application is Submitted/Verified"));
                    }
                }

                //Update entire data under same applicationId
                else if(ApplicationManagerMode.UPDATE.toString().equals(requestApplicationManagerAuthentication.getMode()) && ((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.STEP1_SUBMIT.toString().equals(applicationManagerVO.getSpeed_status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getSpeed_status())))))
                {
                    if(applicationManagerVO==null || (applicationManagerVO!=null && !functions.isValueNull(applicationManagerVO.getSpeed_status())))
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_UPDATE_MODE_APP_NOT_PRESENT);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Update Mode is not valid since the application is not Present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is not Present",new Throwable("Update Mode is not valid since the application is not Present"));
                    }
                    else
                    {
                        errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_INVALID_UPDATE_MODE_APP_SUBMITED);
                        applicationManagerVO.setStatus(null);
                        PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "submitMerchantApplicationForm()", "mode", "merchant", "Update Mode is not valid since the application is Submitted/Verified", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Update Mode is not valid since the application is Submitted/Verified", new Throwable("Update Mode is not valid since the application is Submitted/Verified"));
                    }
                }

                if(ApplicationManagerMode.UPDATE.toString().equals(requestApplicationManagerAuthentication.getMode()) && !((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getMaf_Status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getMaf_Status()) || !functions.isValueNull(applicationManagerVO.getMaf_Status())))))
                {
                    requestApplicationManagerVO = appRequestManager.getApplicationManagerVO(requestApplicationManagerVO);
                    appRequestManager.getApplicationSavedStatus(applicationManagerVO, requestApplicationManagerVO);
                }

                applicationManagerVO=appRequestManager.getApplicationManagerVO(applicationManagerVO);
                //getting applicationManagerVO & navigationVO
                dataBaseApplicationManagerVO=applicationManagerVO;

                validationErrorList=appRequestManager.validateSpeed1Request(null, requestApplicationManagerVO, applicationManagerVO, false, true);
                if (!validationErrorList.isEmpty())
                {
                    validationErrors=new ArrayList<ValidationError>();
                    //appRequestManager.setApplicationDetailsAfterValidation(applicationManagerVO,dataBaseApplicationManagerVO,navigationVO,validationErrorList);
                    applicationManagerVO.setNotificationMessage(applicationManager.saveStep1Page(applicationManagerVO),"Speed Process",null);

                    for(ValidationException validationException:validationErrorList.errors())
                    {
                        PZValidationException pzValidationException = (PZValidationException) validationException;
                        ValidationError validationError = new ValidationError();

                        validationError.setResultCode(pzValidationException.getErrorCodeVO().getApiCode());
                        System.out.println("api code----"+pzValidationException.getErrorCodeVO().getApiCode());
                        validationError.setResultDescription(pzValidationException.getErrorCodeVO().getApiDescription());

                        validationErrors.add(validationError);
                    }
                }
                else
                {
                    applicationManagerVO.setNotificationMessage(applicationManager.submitAllProfile(applicationManagerVO,true,false), null, "Speed Submitted Successfully");
                }
            }
            else
            {
                logger.debug("No Merchant Application Details Has been Submitted");
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                ValidationError validationError = new ValidationError();

                validationErrors=new ArrayList<ValidationError>();
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.MAF_VALIDATION_APPLICATION_NULL);

                validationError.setResultCode(errorCodeVO.getApiCode());
                validationError.setResultDescription(errorCodeVO.getApiDescription());
                validationErrors.add(validationError);
            }
        }
        catch (PZConstraintViolationException pce)
        {
            logger.error("Exception in Navigation Page",pce);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setResultCode(pce.getPzConstraint().getErrorCodeListVO().getApiCode());
            validationError.setResultDescription(pce.getPzConstraint().getErrorCodeListVO().getApiDescription());
            validationErrors.add(validationError);
        }
        catch(Exception e)
        {
            logger.error("Exception in Navigation Page",e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setResultCode("22222");
            validationError.setResultDescription("Internal Issue Please Contact Support Team For More Details");
            validationErrors.add(validationError);
        }

        applicationManagerResponse.setValidationError(validationErrors);
        if(validationErrors.isEmpty())
        {
            validationErrors = new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();

            errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_PROFILE_SAVED);
            validationError.setResultCode(errorCodeListVO.getApiCode());
            validationError.setResultDescription(errorCodeListVO.getApiDescription());
            validationErrors.add(validationError);

            dataBaseApplicationManagerVO.setBankProfileVO(null);
            dataBaseApplicationManagerVO.setFileDetailsVOs(null);
            dataBaseApplicationManagerVO.setCompanyProfileVO(null);
            dataBaseApplicationManagerVO.setBusinessProfileVO(null);
            dataBaseApplicationManagerVO.setCardholderProfileVO(null);
            dataBaseApplicationManagerVO.setOwnershipProfileVO(null);
            dataBaseApplicationManagerVO.setCardholderProfileVO(null);
            dataBaseApplicationManagerVO.setExtradetailsprofileVO(null);
            dataBaseApplicationManagerVO.setUploadLabelVOs(null);
            dataBaseApplicationManagerVO.setStandby_user(null);
            dataBaseApplicationManagerVO.setMessageColorClass(null);
            dataBaseApplicationManagerVO.setAppliedToModify(null);
            dataBaseApplicationManagerVO.setApplicationSaved(null);

            applicationManagerResponse.setApplicationManagerVO(dataBaseApplicationManagerVO);
            applicationManagerResponse.setValidationError(validationErrors);
        }
        return applicationManagerResponse;
    }

    public ApplicationManagerResponse uploadKycDocuments(FormDataMultiPart form) {

        //Manager instance
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO =null;
        AppValidationVO appValidationVO =null;

        //ValidationErrorList instance
        ValidationErrorList validationErrorList = null;
        List<ValidationError> validationErrors=new ArrayList<ValidationError>();
        Set<Integer> contextInvalid= new TreeSet<Integer>();

        //Response VO
        ApplicationManagerResponse applicationManagerResponse=new ApplicationManagerResponse();

        try
        {

            FormDataBodyPart bodyMember = form.getField("memberId");
            FormDataBodyPart bodyChecksum = form.getField("checksum");
            FormDataBodyPart bodyRandom = form.getField("random");
            FormDataBodyPart bodyMode = form.getField("mode");

            if(bodyMember==null || bodyChecksum==null || bodyRandom==null)
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "uploadKycDocuments()", "Checksum,memberid,Random", "merchant", "Please Provide Checksum,MemberId And Random", PZConstraintExceptionEnum.CTOKEN_MISMATCH, null, "Please Provide Checksum,MemberId And Random", new Throwable("Please Provide Checksum,MemberId And Random"));
            }

            if(bodyMode==null)
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "uploadKycDocuments()", "mode", "merchant", "Please Provide Mode", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING, null, "Please Provide Mode", new Throwable("Please Provide Mode"));
            }

            String memberId = bodyMember.getValue();
            String checksum = bodyChecksum.getValue();
            String random   = bodyRandom.getValue();
            String mode     = bodyMode.getValue();

            if (!functions.isValueNull(memberId) || !functions.isValueNull(checksum) || !ESAPI.validator().isValidInput("random", random, "onlyAlphanum", 10, false))
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "uploadKycDocuments()", "Checksum,memberid,Random", "merchant", "Please Provide Checksum,MemberId And Random", PZConstraintExceptionEnum.CTOKEN_MISMATCH, null, "Please Provide Checksum,MemberId And Random", new Throwable("Please Provide Checksum,MemberId And Random"));
            }

            if (!merchantDAO.authenticateMemberViaChecksum(memberId, checksum, random))
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "uploadKycDocuments()", "Checksum,memberid,Random", "merchant", "Merchant Authentication Failure", PZConstraintExceptionEnum.CTOKEN_MISMATCH, null, "Merchant Authentication Failure", new Throwable("Merchant Authentication Failure"));
            }

            if((!functions.isValueNull(mode)) || !ApplicationManagerMode.isInEnum(mode))
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","mode","merchant","Please Provide Mode", PZConstraintExceptionEnum.MANDATORY_PARAMETER_MISSING,null,"Please Provide Mode",new Throwable("Please Provide Mode"));
            }

            //Populate ApplicationManagerDetails
            applicationManagerVO=appRequestManager.getApplicationManagerVO(applicationManagerVO);
            navigationVO = appRequestManager.getNavigationVO(null, Module.MERCHANT);


            applicationManagerVO.setMemberId(memberId);
            applicationManager.populateAppllicationData(applicationManagerVO);
            applicationManagerVO.setUser(Module.MERCHANT.name());

            if(ApplicationManagerMode.CREATE.toString().equals(mode) && ((applicationManagerVO!=null && ((ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getKyc_Status())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getKyc_Status()) || ApplicationStatus.SAVED.toString().equals(applicationManagerVO.getKyc_Status())|| ApplicationStatus.MODIFIED.toString().equals(applicationManagerVO.getKyc_Status()))|| (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus())|| ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus()))))))
            {
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"uploadKycDocuments()","mode","merchant","Create Mode is not valid since the application is present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,"Create Mode is not valid since the application is present",new Throwable("Create Mode is not valid since the application is present"));
            }

            if(ApplicationManagerMode.UPDATE.toString().equals(mode) && ((applicationManagerVO==null) || (applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getStatus())))||(applicationManagerVO!=null && (ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getKyc_Status()) || ApplicationStatus.VERIFIED.toString().equals(applicationManagerVO.getKyc_Status()) || !functions.isValueNull(applicationManagerVO.getKyc_Status()) ))))
            {
                if(applicationManagerVO==null || (applicationManagerVO!=null && !functions.isValueNull(applicationManagerVO.getKyc_Status())))
                {
                    PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"uploadKycDocuments()","mode","merchant","Update Mode is not valid since the application is not Present", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,"Update Mode is not valid since the application is not Present",new Throwable("Update Mode is not valid since the application is not Present"));
                }
                else
                {
                    PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"uploadKycDocuments()","mode","merchant","Update Mode is not valid since the application is Submitted/Verified", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,"Update Mode is not valid since the application is Submitted/Verified",new Throwable("Update Mode is not valid since the application is Submitted/Verified"));
                }
            }

            appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
            applicationManagerVO.setSubmittedFileDetailsVO(applicationManager.uploadMultipleFileAppManager(null, applicationManagerVO, true, form));


            //This is for converting all the datepicker from the previous fields
            appRequestManager.convertAllTimeStampToDatepicker(applicationManagerVO);

            //This is for navigation to get the KYC API Validation
            navigationVO.setCurrentPageNO(6);

            validationErrorList=appRequestManager.validationForInterfaceOrApi(null,applicationManagerVO,navigationVO,applicationManagerVO,true,true,appValidationVO);

            if (!validationErrorList.isEmpty())
            {
                validationErrors=new ArrayList<ValidationError>();

                //applicationManagerVO.setNotificationMessage(applicationManager.saveAllPageForApi(navigationVO, applicationManagerVO));//THIS is not required

                applicationManagerVO.setNotificationMessage(applicationManager.onlyInsertAndUpdateApplicationManagerForKycAPI(applicationManagerVO,ApplicationStatus.SAVED,ApplicationStatus.SAVED),"Kyc"," Saved Successfully");

                for(ValidationException validationException:validationErrorList.errors())
                {
                    contextInvalid.add(Integer.valueOf(validationException.getContext()));

                    if(Integer.valueOf(validationException.getContext())==6)
                    {
                        ValidationError validationError = new ValidationError();

                        validationError.setFieldName(validationException.getMessage());
                        validationError.setMessage(validationException.getLogMessage());

                        validationErrors.add(validationError);
                    }
                }
            }
            else
            {
                if(!functions.isValueNull(applicationManagerVO.getMaf_Status()) || ApplicationStatus.SAVED.toString().equals(applicationManagerVO.getMaf_Status()))
                {
                    applicationManagerResponse.setApplicationManagerVO(applicationManagerVO);
                    applicationManagerVO.setNotificationMessage(applicationManager.onlyInsertAndUpdateApplicationManagerForKycAPI(applicationManagerVO,ApplicationStatus.SUBMIT,ApplicationStatus.SAVED),"Kyc"," Saved Successfully");
                }
                else
                    applicationManagerVO.setNotificationMessage(applicationManager.onlyInsertAndUpdateApplicationManagerForKycAPI(applicationManagerVO, ApplicationStatus.SUBMIT, ApplicationStatus.SUBMIT),"Kyc"," Submitted Successfully");

            }

        }
        catch (PZConstraintViolationException pce)
        {
            logger.error("Exception in Navigation Page",pce);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setFieldName(pce.getPzConstraint().getPropertyName());
            validationError.setMessage(pce.getPzConstraint().getMessage());
            validationErrors.add(validationError);
        }
        catch (PZDBViolationException e)
        {
            logger.error("Exception in Navigation Page",e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setFieldName("");
            validationError.setMessage("Internal Issue Please Contact Support Team For More Details");
            validationErrors.add(validationError);
        }
        catch (Exception e)
        {
            logger.error("Exception in Navigation Page",e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setFieldName("");
            validationError.setMessage("Internal Issue Please Contact Support Team For More Details");
            validationErrors.add(validationError);
        }

        applicationManagerResponse.setValidationError(validationErrors);
        if(ApplicationStatus.SUBMIT.toString().equals(applicationManagerVO.getStatus()))
        {
            applicationManagerResponse.setApplicationManagerVO(applicationManagerVO);
            applicationManagerResponse.setStatusDescription("SUBMITTED_SUCCESSFULLY(All Merchant Application Form Details As Well As Kyc Documents Has been submitted successfully)");
        }

        applicationManagerResponse.setStatus(applicationManagerVO.getStatus());
        return applicationManagerResponse;
    }

    public ApplicationManagerResponse getApplicationManagerDetails(@InjectParam ApplicationManagerAuthenticationRequest authenticationRequest)
    {
        System.out.println("inside mafinquiry url");
        if(request1.getQueryString()!=null){
            ApplicationManagerResponse response1 = new ApplicationManagerResponse();
            Result result=new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }

        ApplicationManagerResponse response = new ApplicationManagerResponse();
        ApplicationManagerAuthentication authentication = convertRequestToVO(authenticationRequest);
        if (authentication != null)
        {
            authentication.setApplicationManagerVO(getapplicationmanagerVo(authenticationRequest.getApplicationManagerRequestVO()));
        }
        response = getApplicationManagerDetailsJSON(authentication);
        return response;
    }

    public ApplicationManagerResponse getApplicationManagerDetailsJSON(ApplicationManagerAuthentication applicationManagerAuthentication)
    {
        System.out.println("inside maf");
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        MerchantDAO merchantDAO = new MerchantDAO();
        //FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        ApplicationManagerResponse applicationManagerResponse = new ApplicationManagerResponse();
        List<ValidationError> validationErrors=new ArrayList<ValidationError>();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        try
        {
            if(applicationManagerAuthentication!=null && (!functions.isValueNull(applicationManagerAuthentication.getMemberId()) || !functions.isValueNull(applicationManagerAuthentication.getChecksum()) || !ESAPI.validator().isValidInput("random", applicationManagerAuthentication.getRandom(), "onlyAlphanum", 10, false)))
            {
//                errorCodeListVO = getErrorVO(ErrorName.MAF_VALIDATION_MEMBERID_RANDOM_CHECKSUM);
                errorCodeVO = errorCodeUtils.getMafErrorCode(ErrorName.MAF_VALIDATION_MEMBERID_RANDOM_CHECKSUM);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "getApplicationManagerDetails()", "checksum,memberid,random", "merchant", "Please Provide Checksum,MemberId And Random", PZConstraintExceptionEnum.CTOKEN_MISMATCH, errorCodeListVO, "Please Provide Checksum,MemberId And Random", new Throwable("Please Provide Checksum,MemberId And Random"));
            }
            if(!merchantDAO.authenticateMemberViaChecksum(applicationManagerAuthentication.getMemberId(), applicationManagerAuthentication.getChecksum(),applicationManagerAuthentication.getRandom()))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECKSUM);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","checksum,memberid,Random","merchant","Merchant Authentication Failure", PZConstraintExceptionEnum.CTOKEN_MISMATCH,errorCodeListVO,"Merchant Authentication Failure",new Throwable("Merchant Authentication Failure"));
            }

            if(applicationManagerAuthentication != null && (!functions.isValueNull(applicationManagerAuthentication.getMode()) || !"INQUIRY".equals(applicationManagerAuthentication.getMode())))
            {
                errorCodeVO = errorCodeUtils.getMafErrorCode(ErrorName.MAF_VALIDATION_INQUIRY_MODE);
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(),"submitMerchantApplicationForm()","Mode","merchant","Inquiry mode is invalid", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,"Inquiry mode invalid",new Throwable("Inquiry mode invalid"));
            }

            applicationManagerVO = applicationManagerAuthentication.getApplicationManagerVO();
            applicationManagerVO.setMemberId(applicationManagerAuthentication.getMemberId());

            if (functions.isValueNull(applicationManagerAuthentication.getApplicationId()))
            {
                if ((functions.isValueNull(applicationManagerAuthentication.getApplicationId()) && ESAPI.validator().isValidInput("random", applicationManagerAuthentication.getApplicationId(), "onlyAlphanum", 10, false)))
                {
                    applicationManagerVO.setApplicationId(applicationManagerAuthentication.getApplicationId());
                    applicationManagerVO = applicationManager.getApplicationManagerDetailsFromApplicationId(applicationManagerVO);
                }
                else
                {
                    errorCodeVO = errorCodeUtils.getMafErrorCode(ErrorName.MAF_VALIDATION_APPLICATIONID);
                    errorCodeListVO.addListOfError(errorCodeVO);
                    PZExceptionHandler.raiseConstraintViolationException(ApplicationManagerApiIMP.class.getName(), "submitMerchantApplicationForm()", "Mode", "merchant", "Inquiry mode is invalid", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, "Inquiry mode invalid", new Throwable("Inquiry mode invalid"));
                }
            }
            else
            {
                applicationManagerVO = applicationManager.getApplicationManagerDetails(applicationManagerVO);
            }

            applicationManagerVO.setStandby_user(null);
            applicationManagerVO.setApplicationSaved(null);
            applicationManagerVO.setAppliedToModify(null);
            applicationManagerVO.getCompanyProfileVO().setApplicationId(null);
            applicationManagerVO.getBankProfileVO().setApplication_id(null);
            applicationManagerVO.getOwnershipProfileVO().setApplicationid(null);
            applicationManagerVO.getBusinessProfileVO().setApplication_id(null);
            applicationManagerVO.getExtradetailsprofileVO().setApplication_id(null);
            applicationManagerVO.getCardholderProfileVO().setApplication_id(null);
            System.out.println(""+applicationManagerVO.getUser());
            applicationManagerResponse.setApplicationManagerVO(applicationManagerVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("Exception in Navigation Page", e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setFieldName(e.getPzConstraint().getErrorCodeListVO().getListOfError().get(0).getApiCode());
            validationError.setMessage(e.getPzConstraint().getErrorCodeListVO().getListOfError().get(0).getApiDescription());
            validationErrors.add(validationError);
            applicationManagerResponse.setValidationError(validationErrors);
        }
        catch(Exception e)
        {
            logger.error("Exception in Navigation Page",e);
            validationErrors=new ArrayList<ValidationError>();
            ValidationError validationError = new ValidationError();
            validationError.setFieldName("");
            validationError.setMessage("Internal Issue Please Contact Support Team For More Details");
            validationErrors.add(validationError);
        }
        return applicationManagerResponse;
    }



    private ApplicationManagerAuthentication convertRequestToVO(ApplicationManagerAuthenticationRequest authenticationRequest)
    {

        //authentication

        ApplicationManagerAuthentication applicationManagerAuthentication = new ApplicationManagerAuthentication();

        applicationManagerAuthentication.setMemberId(authenticationRequest.getMemberId());
        applicationManagerAuthentication.setChecksum(authenticationRequest.getChecksum());
        applicationManagerAuthentication.setMode(authenticationRequest.getMode());
        applicationManagerAuthentication.setRandom(authenticationRequest.getRandom());
        applicationManagerAuthentication.setApplicationId(authenticationRequest.getApplicationId());


        //Setting MerchantVO
        Merchant merchantVO = new Merchant();
        merchantVO.setLoginName(authenticationRequest.getMerchant().getLoginName());
        merchantVO.setConPassword(authenticationRequest.getMerchant().getConPassword());
        merchantVO.setNewPassword(authenticationRequest.getMerchant().getNewPassword());
        merchantVO.setEmail(authenticationRequest.getMerchant().getEmail());
        merchantVO.setCompanyName(authenticationRequest.getMerchant().getCompanyName());
        merchantVO.setPhone(authenticationRequest.getMerchant().getPhone());
        merchantVO.setContactName(authenticationRequest.getMerchant().getContactName());
        applicationManagerAuthentication.setMerchantVO(merchantVO);

        //Setting AuthenticationVO
        Authentication authenticationVo = new Authentication();
        authenticationVo.setPartnerId(authenticationRequest.getAuthentication().getPartnerId());
        applicationManagerAuthentication.setAuthentication(authenticationVo);

        // setting ApplicationManagerVO
        applicationManagerAuthentication.setApplicationManagerVO(getapplicationmanagerVo(authenticationRequest.getApplicationManagerRequestVO()));


        return applicationManagerAuthentication;
    }

    private ApplicationManagerVO getapplicationmanagerVo(ApplicationManagerRequestVO applicationManagerRequest)
    {
        ApplicationManagerVO applicationManagerVO = null;

        if (applicationManagerRequest!=null)
        {
            applicationManagerVO=new ApplicationManagerVO();
            applicationManagerVO.setApplicationId(applicationManagerRequest.getApplicationId());
            applicationManagerVO.setMemberId(applicationManagerRequest.getMemberId());
            applicationManagerVO.setStatus(applicationManagerRequest.getStatus());
            applicationManagerVO.setMaf_Status(applicationManagerRequest.getMaf_Status());
            applicationManagerVO.setKyc_Status(applicationManagerRequest.getKyc_Status());
            applicationManagerVO.setUser(applicationManagerRequest.getUser());
            applicationManagerVO.setSpeed_user(applicationManagerRequest.getSpeed_user());
            applicationManagerVO.setStandby_user(applicationManagerRequest.getStandby_user());

            applicationManagerVO.setCompanyProfileVO(getCompanyProfileVO(applicationManagerRequest.getCompanyProfileRequestVO()));
            applicationManagerVO.setOwnershipProfileVO(getOwnershipprofile(applicationManagerRequest.getOwnershipProfileRequestVO()));
            applicationManagerVO.setBusinessProfileVO(getBusinessProfile(applicationManagerRequest.getBusinessProfileRequestVO()));
            applicationManagerVO.setCardholderProfileVO(getCardholderProfileVO(applicationManagerRequest.getCardholderProfileVO()));
            applicationManagerVO.setBankProfileVO(getbankProfileVO(applicationManagerRequest.getBankProfileVO()));
            applicationManagerVO.setExtradetailsprofileVO(getextradetails(applicationManagerRequest.getExtradetailsprofileVO()));
        }
        return applicationManagerVO;

    }


    //new signup api

    public ApplicationManagerResponse merchantsignup(@InjectParam ApplicationManagerAuthenticationRequest authentication)
    {
        if(request1.getQueryString()!=null){
            ApplicationManagerResponse response1 = new ApplicationManagerResponse();
            Result result=new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        ApplicationManagerAuthentication authentication2 = convertRequestToVO(authentication);
        ApplicationManagerResponse   applicationManagerResponse =merchantsignupJSON(authentication2);
        return applicationManagerResponse;
    }



    public ApplicationManagerResponse merchantsignupJSON(ApplicationManagerAuthentication applicationManagerAuthentication)
    {
        System.out.println("signup---");
        ApplicationManagerResponse signupResponse = new ApplicationManagerResponse();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        AppRequestManager appRequestManager = new AppRequestManager();
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(ClientConfig.FEATURE_DISABLE_XML_SECURITY, true);
        Random rand = new Random();
        try
        {
            System.out.println("login authentication----"+applicationManagerAuthentication.getMerchantVO().getLoginName());
            commonValidatorVO = readRequestForMerchantSignUpJSON(applicationManagerAuthentication);
            System.out.println("partnerid----"+commonValidatorVO.getPartnerDetailsVO().getPartnerId());


            if (commonValidatorVO == null)
            {
                System.out.println("inside 1st if");
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }


            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantSignUpValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            request1.setAttribute("role", "merchant");
            ESAPI.httpUtilities().setCurrentHTTP(request1, null);
            directKitResponseVO = registrationManager.processMerchantSignUp(commonValidatorVO,request1);

             int  n = rand.nextInt(500000000) + 1;
            applicationManagerAuthentication.setRandom(String.valueOf(n));

            String memberid=directKitResponseVO.getMemberId();
            String partnerkey =commonValidatorVO.getPartnerDetailsVO().getPartnerKey();
            System.out.println("partnerkey----"+partnerkey);


            String chkSum = functions.generateMD5ChecksumAPIBased(memberid ,  partnerkey  , String.valueOf(n));

            applicationManagerAuthentication.setMemberId(memberid);
            applicationManagerAuthentication.setRandom(String.valueOf(n));
            applicationManagerAuthentication.setChecksum(chkSum);
            applicationManagerAuthentication.setMode("CREATE");
            commonValidatorVO.setRes_checksum(chkSum);





            writeMerchantServiceResponse.setSuccessMerchantSignupResponse(signupResponse, directKitResponseVO, commonValidatorVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            //transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            //  transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    public ApplicationManagerResponse processMerchantLogin(@InjectParam ApplicationManagerAuthenticationRequest loginRequest)
    {
        if(request1.getQueryString()!=null){
            ApplicationManagerResponse response1 = new ApplicationManagerResponse();
            Result result=new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        ApplicationManagerAuthentication authentication2 = convertRequestToVO(loginRequest);
        ApplicationManagerResponse   applicationManagerResponse =processMerchantLoginJSON(authentication2);
        return applicationManagerResponse;
    }

    //new login api

    public ApplicationManagerResponse processMerchantLoginJSON(ApplicationManagerAuthentication loginRequest)
    {

        System.out.println("inside signup----"+loginRequest.getAuthentication().getPartnerId());
        ApplicationManagerAuthenticationRequest authenticationRequest = new ApplicationManagerAuthenticationRequest();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress=request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
        Functions functions = new Functions();
        AppRequestManager appRequestManager = new AppRequestManager();
        TransactionHelper transactionHelper = new TransactionHelper();
        ApplicationManagerResponse applicationManagerResponse = new ApplicationManagerResponse();
        try
        {

            commonValidatorVO = readRequestForMerchantSignUpJSON(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(applicationManagerResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return applicationManagerResponse;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantLoginValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeMerchantServiceResponse.setLoginResponseForError(applicationManagerResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return applicationManagerResponse;
            }

            commonValidatorVO = transactionHelper.merchantActivationChecks(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                logger.debug("inside if performMerchantCurrenciesValidation---");
                writeMerchantServiceResponse.setSignUpResponseForError(applicationManagerResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return applicationManagerResponse;
            }
            else
            {
                request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
                request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
                request1.setAttribute("role", "merchant");
                directKitResponseVO = registrationManager.processMerchantLogin(commonValidatorVO, request1, response);
                writeMerchantServiceResponse.setSuccessMerchantLoginResponse(applicationManagerResponse, directKitResponseVO, commonValidatorVO);
            }

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setLoginResponseForError(applicationManagerResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            assert commonValidatorVO != null;
            writeMerchantServiceResponse.setLoginResponseForError(applicationManagerResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }



        return applicationManagerResponse;
    }

    public ApplicationManagerResponse processVerification( @InjectParam ApplicationManagerAuthenticationRequest verifyRequest)
    {
        ApplicationManagerResponse applicationManagerResponse = new ApplicationManagerResponse();

        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setShareholderprofile1_lastname("damani");
        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setShareholderprofile1("pranav");
        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setShareholderprofile1_identificationtype("100%");
        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setShareholderprofile1_identificationtypeselect("PASSPORT");
        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setAuthorizedsignatoryprofile1_addressId("123456");
        verifyRequest.getApplicationManagerRequestVO().getOwnershipProfileRequestVO().setShareholderprofile1_identificationtypeselect("PASSPORT");

        if (verifyRequest!=null)

        {
            applicationManagerResponse.setStatus("SUCCESS");
        }
        else

        {
            applicationManagerResponse.setStatus("SUCCESS");
        }
        System.out.println("response----"+applicationManagerResponse.getStatus());
        return applicationManagerResponse;

    }




    private CommonValidatorVO readRequestForMerchantSignUpJSON(ApplicationManagerAuthentication applicationManagerAuthentication)
    {

          //  ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = new GenericAddressDetailsVO();


        partnerDetailsVO.setPartnerId(applicationManagerAuthentication.getAuthentication().getPartnerId());
        merchantDetailsVO.setLogin(applicationManagerAuthentication.getMerchantVO().getLoginName());
        merchantDetailsVO.setNewPassword(applicationManagerAuthentication.getMerchantVO().getNewPassword());
        merchantDetailsVO.setConPassword(applicationManagerAuthentication.getMerchantVO().getConPassword());
        merchantDetailsVO.setCompany_name(applicationManagerAuthentication.getMerchantVO().getCompanyName());
        addressDetailsVO.setPhone(applicationManagerAuthentication.getMerchantVO().getPhone());
        merchantDetailsVO.setTelNo(applicationManagerAuthentication.getMerchantVO().getPhone());
        merchantDetailsVO.setWebsite(applicationManagerAuthentication.getMerchantVO().getWebsite());
        merchantDetailsVO.setPartnerId(applicationManagerAuthentication.getAuthentication().getPartnerId());
        commonValidatorVO.setParetnerId(applicationManagerAuthentication.getAuthentication().getPartnerId());
        merchantDetailsVO.setContact_persons(applicationManagerAuthentication.getMerchantVO().getContactName());
        merchantDetailsVO.setNewPassword(applicationManagerAuthentication.getMerchantVO().getNewPassword());
        merchantDetailsVO.setPassword(applicationManagerAuthentication.getMerchantVO().getNewPassword());
        addressDetailsVO.setEmail(applicationManagerAuthentication.getMerchantVO().getEmail());
        addressDetailsVO.setCountry(applicationManagerAuthentication.getMerchantVO().getCountry());

        commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setAddressDetailsVO(addressDetailsVO);






        return commonValidatorVO;
    }



    private CompanyProfileVO getCompanyProfileVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        CompanyProfileVO companyProfileVO = null;

        if (companyProfileRequestVO != null)
        {
            companyProfileVO = new CompanyProfileVO();
            companyProfileVO.setApplicationId(companyProfileRequestVO.getApplicationId());
            companyProfileVO.setApplicationId(companyProfileRequestVO.getApplicationId());
            companyProfileVO.setCompanyBankruptcy(companyProfileRequestVO.getCompanyBankruptcy());
            companyProfileVO.setCompanyBankruptcydate(companyProfileRequestVO.getCompanyBankruptcydate());
            companyProfileVO.setCompanyTypeOfBusiness(companyProfileRequestVO.getCompanyTypeOfBusiness());
            companyProfileVO.setCountryOfRegistration(companyProfileRequestVO.getCountryOfRegistration());
            companyProfileVO.setCompanyLengthOfTimeInBusiness(companyProfileRequestVO.getCompanyLengthOfTimeInBusiness());
            companyProfileVO.setCompanyCapitalResources(companyProfileRequestVO.getCompanyCapitalResources());
            companyProfileVO.setInsured_companyname(companyProfileRequestVO.getInsured_companyname());
            companyProfileVO.setInsured_amount(companyProfileRequestVO.getInsured_amount());
            companyProfileVO.setIncome_sources_other(companyProfileRequestVO.getIncome_sources_other());
            companyProfileVO.setIncome_sources_other_yes(companyProfileRequestVO.getIncome_sources_other_yes());
            companyProfileVO.setCompany_currencylastyear(companyProfileRequestVO.getCompany_currencylastyear());
            companyProfileVO.setCompany_turnoverlastyear_unit(companyProfileRequestVO.getCompany_turnoverlastyear_unit());
            companyProfileVO.setCompanyCapitalResources(companyProfileRequestVO.getCompanyCapitalResources());
            companyProfileVO.setTime_business(companyProfileRequestVO.getTime_business());
            companyProfileVO.setStartup_business(companyProfileRequestVO.getStartup_business());
            companyProfileVO.setMain_business_partner(companyProfileRequestVO.getMain_business_partner());
            companyProfileVO.setLoans(companyProfileRequestVO.getLoans());
            companyProfileVO.setLicense_required(companyProfileRequestVO.getLicense_required());
            companyProfileVO.setLicense_Permission(companyProfileRequestVO.getLicense_Permission());
            companyProfileVO.setLegalProceeding(companyProfileRequestVO.getLegalProceeding());
            companyProfileVO.setIscompany_insured(companyProfileRequestVO.getIscompany_insured());
            companyProfileVO.setInvestments(companyProfileRequestVO.getInvestments());
            companyProfileVO.setInterest_income(companyProfileRequestVO.getInterest_income());
            companyProfileVO.setCompanyRegisteredEU(companyProfileRequestVO.getCompanyRegisteredEU());
            companyProfileVO.setCompanyProfileSaved(companyProfileRequestVO.getIsCompanyProfileSaved());
            companyProfileVO.setCompanyNumberOfEmployees(companyProfileRequestVO.getCompanyNumberOfEmployees());
            companyProfileVO.setInsured_currency(companyProfileRequestVO.getInsured_currency());
            companyProfileVO.setIncome_economic_activity(companyProfileRequestVO.getIncome_economic_activity());
            companyProfileVO.setInterest_income(companyProfileRequestVO.getInterest_income());
            companyProfileVO.setInsured_currency(companyProfileRequestVO.getInsured_currency());
            companyProfileVO.setCompanyTurnoverLastYear(companyProfileRequestVO.getCompanyTurnoverLastYear());
            //CHILD
            Map<String, AddressIdentificationVO> companyProfile_addressVOMap = new HashMap<String, AddressIdentificationVO>();
            companyProfile_addressVOMap.put(ApplicationManagerTypes.COMPANY,getcompany_AddressVo(companyProfileRequestVO));
            companyProfile_addressVOMap.put(ApplicationManagerTypes.BUSINESS,getbusiness_addressIdentificationVO(companyProfileRequestVO));
            companyProfile_addressVOMap.put(ApplicationManagerTypes.EU_COMPANY,getEU_addressIdentificationVO(companyProfileRequestVO));

            Map<String, ContactDetailsVO> companyProfile_contactVOMap = new HashMap<String, ContactDetailsVO>();

            companyProfile_contactVOMap.put(ApplicationManagerTypes.MAIN,getmain_contactdetailsVO(companyProfileRequestVO));
            companyProfile_contactVOMap.put(ApplicationManagerTypes.TECHNICAL,gettechnical_contactdetailsVO(companyProfileRequestVO));
            companyProfile_contactVOMap.put(ApplicationManagerTypes.BILLING, getbilling_contactdetailsVO(companyProfileRequestVO));
            companyProfile_contactVOMap.put(ApplicationManagerTypes.CBK, getcbk_contactdetailsVO(companyProfileRequestVO));
            companyProfile_contactVOMap.put(ApplicationManagerTypes.PCI, getpci_contactdetailsVO(companyProfileRequestVO));

            companyProfileVO.setCompanyProfile_contactInfoVOMap(companyProfile_contactVOMap);
            companyProfileVO.setCompanyProfile_addressVOMap(companyProfile_addressVOMap);

        }
        return companyProfileVO;
    }

    private OwnershipProfileVO getOwnershipprofile(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileVO ownershipProfileVO = null;

        if (ownershipProfileRequestVO != null)
        {

            ownershipProfileVO = new OwnershipProfileVO();
            ownershipProfileVO.setApplicationid(ownershipProfileRequestVO.getApplicationid());
            ownershipProfileVO.setNumOfAuthrisedSignatory(ownershipProfileRequestVO.getNumOfAuthrisedSignatory());
            ownershipProfileVO.setNumOfCorporateShareholders(ownershipProfileRequestVO.getNumOfCorporateShareholders());
            ownershipProfileVO.setNumOfDirectors(ownershipProfileRequestVO.getNumOfDirectors());
            ownershipProfileVO.setOwnerShipProfileSaved(ownershipProfileRequestVO.getOwnerShipProfileSaved());
            //child
            Map<String, OwnershipProfileDetailsVO> ownership_ProfileDetailsVOMap = new HashMap<String, OwnershipProfileDetailsVO>();

            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER1,getshareholder1(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER2,getshareholder2(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.SHAREHOLDER3,getshareholder3(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER1,getcorporate1(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER2,getcorporate2(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.CORPORATESHAREHOLDER3,getcorporate3(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR1,getdirector1(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR2,getdirector2(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.DIRECTOR3,getdirector3(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY1,getauthorize1(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY2,getauthorize2(ownershipProfileRequestVO));
            ownership_ProfileDetailsVOMap.put(ApplicationManagerTypes.AUTHORIZESIGNATORY3,getauthorize3(ownershipProfileRequestVO));

            ownershipProfileVO.setOwnershipProfileDetailsVOMap(ownership_ProfileDetailsVOMap);

        }
        return ownershipProfileVO;
    }

    private CardholderProfileVO getCardholderProfileVO(CardholderProfileRequestVO cardholderProfileRequestVO)
    {
        System.out.println("inside cardholderProfile");
        CardholderProfileVO cardholderProfileVO = null;

        if (cardholderProfileRequestVO != null)
        {

            cardholderProfileVO = new CardholderProfileVO();

            cardholderProfileVO.setApplication_id(cardholderProfileRequestVO.getApplication_id());
            cardholderProfileVO.setCompliance_swapp(cardholderProfileRequestVO.getCompliance_swapp());
            cardholderProfileVO.setCompliance_thirdpartyappform(cardholderProfileRequestVO.getCompliance_thirdpartyappform());
            cardholderProfileVO.setCompliance_thirdpartysoft(cardholderProfileRequestVO.getCompliance_thirdpartysoft());
            cardholderProfileVO.setCompliance_version(cardholderProfileRequestVO.getCompliance_version());
            cardholderProfileVO.setCompliance_companiesorgateways(cardholderProfileRequestVO.getCompliance_companiesorgateways());
            cardholderProfileVO.setCompliance_companiesorgateways_yes(cardholderProfileRequestVO.getCompliance_companiesorgateways_yes());
            cardholderProfileVO.setCompliance_electronically(cardholderProfileRequestVO.getCompliance_electronically());
            cardholderProfileVO.setCompliance_carddatastored(cardholderProfileRequestVO.getCompliance_carddatastored());
            cardholderProfileVO.setCompliance_pcidsscompliant(cardholderProfileRequestVO.getCompliance_pcidsscompliant());
            cardholderProfileVO.setCompliance_pcidsscompliant_yes(cardholderProfileRequestVO.getCompliance_pcidsscompliant_yes());
            cardholderProfileVO.setCompliance_qualifiedsecurityassessor(cardholderProfileRequestVO.getCompliance_qualifiedsecurityassessor());
            cardholderProfileVO.setCompliance_dateofcompliance(cardholderProfileRequestVO.getCompliance_dateofcompliance());
            cardholderProfileVO.setCompliance_dateoflastscan(cardholderProfileRequestVO.getCompliance_dateoflastscan());
            cardholderProfileVO.setCompliance_datacompromise(cardholderProfileRequestVO.getCompliance_datacompromise());
            cardholderProfileVO.setCompliance_datacompromise_yes(cardholderProfileRequestVO.getCompliance_datacompromise_yes());
            cardholderProfileVO.setSiteinspection_merchant(cardholderProfileRequestVO.getSiteinspection_merchant());
            cardholderProfileVO.setSiteinspection_landlord(cardholderProfileRequestVO.getSiteinspection_landlord());
            cardholderProfileVO.setSiteinspection_buildingtype(cardholderProfileRequestVO.getSiteinspection_buildingtype());
            cardholderProfileVO.setSiteinspection_areazoned(cardholderProfileRequestVO.getSiteinspection_areazoned());
            cardholderProfileVO.setSiteinspection_squarefootage(cardholderProfileRequestVO.getSiteinspection_squarefootage());
            cardholderProfileVO.setSiteinspection_operatebusiness(cardholderProfileRequestVO.getSiteinspection_operatebusiness());
            cardholderProfileVO.setSiteinspection_principal1(cardholderProfileRequestVO.getSiteinspection_principal1());
            cardholderProfileVO.setSiteinspection_principal1_date(cardholderProfileRequestVO.getSiteinspection_principal1_date());
            cardholderProfileVO.setSiteinspection_principal2(cardholderProfileRequestVO.getSiteinspection_principal2());
            cardholderProfileVO.setSiteinspection_principal2_date(cardholderProfileRequestVO.getSiteinspection_principal2_date());
            cardholderProfileVO.setCompliance_cispcompliant(cardholderProfileRequestVO.getCompliance_cispcompliant());
            cardholderProfileVO.setCompliance_cispcompliant_yes(cardholderProfileRequestVO.getCompliance_cispcompliant_yes());
            cardholderProfileVO.setCardHolderProfileSaved(cardholderProfileRequestVO.getCardHolderProfileSaved());
        }
        return cardholderProfileVO;
    }

    private BusinessProfileVO getBusinessProfile(BusinessProfileRequestVO businessProfileRequestVO)
    {
        BusinessProfileVO businessProfileVO = null;

        if (businessProfileRequestVO != null)
        {
            businessProfileVO = new BusinessProfileVO();

            businessProfileVO.setApplication_id(businessProfileRequestVO.getApplication_id());
            businessProfileVO.setForeigntransactions_Asia(businessProfileRequestVO.getForeigntransactions_Asia());
            businessProfileVO.setForeigntransactions_uk(businessProfileRequestVO.getForeigntransactions_uk());
            businessProfileVO.setForeigntransactions_canada(businessProfileRequestVO.getForeigntransactions_canada());
            businessProfileVO.setForeigntransactions_Europe(businessProfileRequestVO.getForeigntransactions_Europe());
            businessProfileVO.setForeigntransactions_RestoftheWorld(businessProfileRequestVO.getForeigntransactions_RestoftheWorld());
            businessProfileVO.setForeigntransactions_cis(businessProfileRequestVO.getForeigntransactions_cis());
            businessProfileVO.setForeigntransactions_us(businessProfileRequestVO.getForeigntransactions_us());
            businessProfileVO.setMethodofacceptance_moto(businessProfileRequestVO.getMethodofacceptance_moto());
            businessProfileVO.setMethodofacceptance_swipe(businessProfileRequestVO.getMethodofacceptance_swipe());
            businessProfileVO.setMethodofacceptance_internet(businessProfileRequestVO.getMethodofacceptance_internet());
            businessProfileVO.setAffiliate_programs(businessProfileRequestVO.getAffiliate_programs());
            businessProfileVO.setAverageticket(businessProfileRequestVO.getAverageticket());
            businessProfileVO.setHighestticket(businessProfileRequestVO.getHighestticket());
            businessProfileVO.setUrls(businessProfileRequestVO.getUrls());
            businessProfileVO.setDescriptor(businessProfileRequestVO.getDescriptor());
            businessProfileVO.setAgency_employed(businessProfileRequestVO.getAgency_employed());
            businessProfileVO.setAgency_employed_yes(businessProfileRequestVO.getAgency_employed_yes());
            businessProfileVO.setTest_link(businessProfileRequestVO.getTest_link());
            businessProfileVO.setIs_website_live(businessProfileRequestVO.getIs_website_live());
            businessProfileVO.setCardvolume_americanexpress(businessProfileRequestVO.getCardvolume_americanexpress());
            businessProfileVO.setCardvolume_discover(businessProfileRequestVO.getCardvolume_discover());
            businessProfileVO.setCardvolume_mastercard(businessProfileRequestVO.getCardvolume_mastercard());
            businessProfileVO.setCardvolume_dinner(businessProfileRequestVO.getCardvolume_dinner());
            businessProfileVO.setCardvolume_visa(businessProfileRequestVO.getCardvolume_visa());
            businessProfileVO.setCardvolume_rupay(businessProfileRequestVO.getCardvolume_rupay());
            businessProfileVO.setCardvolume_jcb(businessProfileRequestVO.getCardvolume_jcb());
            businessProfileVO.setCardvolume_other(businessProfileRequestVO.getCardvolume_other());
            businessProfileVO.setCardtypesaccepted_rupay(businessProfileRequestVO.getCardtypesaccepted_rupay());
            businessProfileVO.setCardtypesaccepted_americanexpress(businessProfileRequestVO.getCardtypesaccepted_americanexpress());
            businessProfileVO.setCardtypesaccepted_diners(businessProfileRequestVO.getCardtypesaccepted_diners());
            businessProfileVO.setCardtypesaccepted_discover(businessProfileRequestVO.getCardtypesaccepted_discover());
            businessProfileVO.setCardtypesaccepted_jcb(businessProfileRequestVO.getCardtypesaccepted_jcb());
            businessProfileVO.setTraffic_countries_Asia(businessProfileRequestVO.getTraffic_countries_Asia());
            businessProfileVO.setTraffic_countries_canada(businessProfileRequestVO.getTraffic_countries_canada());
            businessProfileVO.setTraffic_countries_CIS(businessProfileRequestVO.getTraffic_countries_CIS());
            businessProfileVO.setTraffic_countries_Europe(businessProfileRequestVO.getTraffic_countries_Europe());
            businessProfileVO.setTraffic_countries_restworld(businessProfileRequestVO.getTraffic_countries_restworld());
            businessProfileVO.setOrderconfirmation_post(businessProfileRequestVO.getOrderconfirmation_post());
            businessProfileVO.setOrderconfirmation_email(businessProfileRequestVO.getTraffic_countries_restworld());
            businessProfileVO.setTraffic_countries_Europe(businessProfileRequestVO.getTraffic_countries_Europe());
            businessProfileVO.setDescriptionofproducts(businessProfileRequestVO.getDescriptionofproducts());
            businessProfileVO.setProduct_sold_currencies(businessProfileRequestVO.getProduct_sold_currencies());
            businessProfileVO.setRecurringservices(businessProfileRequestVO.getRecurringservices());
            businessProfileVO.setRecurringservicesyes(businessProfileRequestVO.getRecurringservicesyes());
            businessProfileVO.setIsacallcenterused(businessProfileRequestVO.getIsacallcenterused());
            businessProfileVO.setIsacallcenterusedyes(businessProfileRequestVO.getIsacallcenterusedyes());
            businessProfileVO.setIsafulfillmenthouseused(businessProfileRequestVO.getIsafulfillmenthouseused());
            businessProfileVO.setIsafulfillmenthouseused_yes(businessProfileRequestVO.getIsafulfillmenthouseused_yes());
            businessProfileVO.setCardtypesaccepted_visa(businessProfileRequestVO.getCardtypesaccepted_visa());
            businessProfileVO.setCardtypesaccepted_mastercard(businessProfileRequestVO.getCardtypesaccepted_mastercard());
            businessProfileVO.setCardtypesaccepted_other(businessProfileRequestVO.getCardtypesaccepted_other());
            businessProfileVO.setCardtypesaccepted_other_yes(businessProfileRequestVO.getCardtypesaccepted_other_yes());
            businessProfileVO.setSizeofcustomer_Database(businessProfileRequestVO.getSizeofcustomer_Database());
            businessProfileVO.setTopfivecountries(businessProfileRequestVO.getTopfivecountries());
            businessProfileVO.setKyc_processes(businessProfileRequestVO.getKyc_processes());
            businessProfileVO.setCustomer_account(businessProfileRequestVO.getCustomer_account());
            businessProfileVO.setVisa_cardlogos(businessProfileRequestVO.getVisa_cardlogos());
            businessProfileVO.setMaster_cardlogos(businessProfileRequestVO.getMaster_cardlogos());
            businessProfileVO.setThreeD_secure_compulsory(businessProfileRequestVO.getThreeD_secure_compulsory());
            businessProfileVO.setPrice_displayed(businessProfileRequestVO.getPrice_displayed());
            businessProfileVO.setTransaction_currency(businessProfileRequestVO.getTransaction_currency());
            businessProfileVO.setCardholder_asked(businessProfileRequestVO.getCardholder_asked());
            businessProfileVO.setDynamic_descriptors(businessProfileRequestVO.getDynamic_descriptors());
            businessProfileVO.setShopping_cart(businessProfileRequestVO.getShopping_cart());
            businessProfileVO.setShopping_cart_details(businessProfileRequestVO.getShopping_cart_details());
            businessProfileVO.setPricing_policies_website_yes(businessProfileRequestVO.getPricing_policies_website_yes());
            businessProfileVO.setPricing_policies_website(businessProfileRequestVO.getPricing_policies_website());
            businessProfileVO.setFulfillment_timeframe(businessProfileRequestVO.getFulfillment_timeframe());
            businessProfileVO.setGoods_policy(businessProfileRequestVO.getGoods_policy());
            businessProfileVO.setMCC_Ctegory(businessProfileRequestVO.getMCC_Ctegory());
            businessProfileVO.setCountries_blocked(businessProfileRequestVO.getCountries_blocked());
            businessProfileVO.setCountries_blocked_details(businessProfileRequestVO.getCountries_blocked_details());
            businessProfileVO.setSecuritypolicy(businessProfileRequestVO.getSecuritypolicy());
            businessProfileVO.setConfidentialitypolicy(businessProfileRequestVO.getConfidentialitypolicy());
            businessProfileVO.setApplicablejurisdictions(businessProfileRequestVO.getApplicablejurisdictions());
            businessProfileVO.setPrivacy_anonymity_dataprotection(businessProfileRequestVO.getPrivacy_anonymity_dataprotection());
            businessProfileVO.setApp_Services(businessProfileRequestVO.getApp_Services());
            businessProfileVO.setProduct_requires(businessProfileRequestVO.getProduct_requires());
            businessProfileVO.setAgency_employed(businessProfileRequestVO.getAgency_employed());
            businessProfileVO.setAgency_employed_yes(businessProfileRequestVO.getAgency_employed_yes());
            businessProfileVO.setLoginId(businessProfileRequestVO.getLoginId());
            businessProfileVO.setPassWord(businessProfileRequestVO.getPassWord());
            businessProfileVO.setDomainsOwned(businessProfileRequestVO.getDomainsOwned());
            businessProfileVO.setDomainsOwned_no(businessProfileRequestVO.getDomainsOwned_no());
            businessProfileVO.setSslSecured(businessProfileRequestVO.getSslSecured());
            businessProfileVO.setCopyright(businessProfileRequestVO.getCopyright());
            businessProfileVO.setPassWord(businessProfileRequestVO.getPassWord());
            businessProfileVO.setCompanyIdentifiable(businessProfileRequestVO.getCompanyIdentifiable());
            businessProfileVO.setClearlyPresented(businessProfileRequestVO.getClearlyPresented());
            businessProfileVO.setTrackingNumber(businessProfileRequestVO.getTrackingNumber());
            businessProfileVO.setSourceContent(businessProfileRequestVO.getSourceContent());
            businessProfileVO.setDirectMail(businessProfileRequestVO.getDirectMail());
            businessProfileVO.setYellowPages(businessProfileRequestVO.getYellowPages());
            businessProfileVO.setRadioTv(businessProfileRequestVO.getRadioTv());
            businessProfileVO.setInternet(businessProfileRequestVO.getInternet());
            businessProfileVO.setNetworking(businessProfileRequestVO.getNetworking());
            businessProfileVO.setOutboundTelemarketing(businessProfileRequestVO.getOutboundTelemarketing());
            businessProfileVO.setInHouseLocation(businessProfileRequestVO.getInHouseLocation());
            businessProfileVO.setContactPerson(businessProfileRequestVO.getContactPerson());
            businessProfileVO.setShippingContactemail(businessProfileRequestVO.getShippingContactemail());
            businessProfileVO.setOtherLocation(businessProfileRequestVO.getOtherLocation());
            businessProfileVO.setWarehouseLocation(businessProfileRequestVO.getWarehouseLocation());
            businessProfileVO.setMainSuppliers(businessProfileRequestVO.getMainSuppliers());
            businessProfileVO.setShipmentAssured(businessProfileRequestVO.getShipmentAssured());
            businessProfileVO.setBillingModel(businessProfileRequestVO.getBillingModel());
            businessProfileVO.setBillingTimeFrame(businessProfileRequestVO.getBillingTimeFrame());
            businessProfileVO.setRecurringAmount(businessProfileRequestVO.getRecurringAmount());
            businessProfileVO.setMultipleMembership(businessProfileRequestVO.getMultipleMembership());
            businessProfileVO.setFreeMembership(businessProfileRequestVO.getFreeMembership());
            businessProfileVO.setCreditCardRequired(businessProfileRequestVO.getCreditCardRequired());
            businessProfileVO.setAutomaticallyBilled(businessProfileRequestVO.getAutomaticallyBilled());
            businessProfileVO.setPreAuthorization(businessProfileRequestVO.getPreAuthorization());
            businessProfileVO.setAutomaticRecurring(businessProfileRequestVO.getAutomaticRecurring());
            businessProfileVO.setMerchantCode(businessProfileRequestVO.getMerchantCode());
            businessProfileVO.setMCC_Ctegory(businessProfileRequestVO.getMCC_Ctegory());
            businessProfileVO.setLowestticket(businessProfileRequestVO.getLowestticket());
            businessProfileVO.setTimeframe(businessProfileRequestVO.getTimeframe());
            businessProfileVO.setLivechat(businessProfileRequestVO.getLivechat());
            businessProfileVO.setIpaddress(businessProfileRequestVO.getIpaddress());
            businessProfileVO.setShopsystem_plugin(businessProfileRequestVO.getShopsystem_plugin());
            businessProfileVO.setDirect_debit_sepa(businessProfileRequestVO.getDirect_debit_sepa());
            businessProfileVO.setAlternative_payments(businessProfileRequestVO.getAlternative_payments());
            businessProfileVO.setRisk_management(businessProfileRequestVO.getRisk_management());
            businessProfileVO.setPayment_engine(businessProfileRequestVO.getPayment_engine());
            businessProfileVO.setWebhost_company_name(businessProfileRequestVO.getWebhost_company_name());
            businessProfileVO.setWebhost_phone(businessProfileRequestVO.getWebhost_phone());
            businessProfileVO.setWebhost_email(businessProfileRequestVO.getWebhost_email());
            businessProfileVO.setWebhost_website(businessProfileRequestVO.getWebhost_website());
            businessProfileVO.setWebhost_address(businessProfileRequestVO.getWebhost_address());
            businessProfileVO.setPayment_company_name(businessProfileRequestVO.getPayment_company_name());
            businessProfileVO.setPayment_phone(businessProfileRequestVO.getPayment_phone());
            businessProfileVO.setPayment_email(businessProfileRequestVO.getPayment_email());
            businessProfileVO.setPayment_website(businessProfileRequestVO.getPayment_website());
            businessProfileVO.setPayment_address(businessProfileRequestVO.getPayment_address());
            businessProfileVO.setCallcenter_phone(businessProfileRequestVO.getCallcenter_phone());
            businessProfileVO.setCallcenter_email(businessProfileRequestVO.getCallcenter_email());
            businessProfileVO.setCallcenter_website(businessProfileRequestVO.getCallcenter_website());
            businessProfileVO.setCallcenter_address(businessProfileRequestVO.getCallcenter_address());
            businessProfileVO.setShoppingcart_company_name(businessProfileRequestVO.getShoppingcart_company_name());
            businessProfileVO.setShoppingcart_phone(businessProfileRequestVO.getShoppingcart_phone());
            businessProfileVO.setShoppingcart_email(businessProfileRequestVO.getShoppingcart_email());
            businessProfileVO.setShoppingcart_website(businessProfileRequestVO.getShoppingcart_website());
            businessProfileVO.setShoppingcart_address(businessProfileRequestVO.getShoppingcart_address());
            businessProfileVO.setSeasonal_fluctuating(businessProfileRequestVO.getSeasonal_fluctuating());
            businessProfileVO.setPaymenttype_credit(businessProfileRequestVO.getPaymenttype_credit());
            businessProfileVO.setPaymenttype_debit(businessProfileRequestVO.getPaymenttype_debit());
            businessProfileVO.setPaymenttype_netbanking(businessProfileRequestVO.getPaymenttype_netbanking());
            businessProfileVO.setPaymenttype_wallet(businessProfileRequestVO.getPaymenttype_wallet());
            businessProfileVO.setPaymenttype_alternate(businessProfileRequestVO.getPaymenttype_alternate());
            businessProfileVO.setCreditor_id(businessProfileRequestVO.getCreditor_id());
            businessProfileVO.setPayment_delivery(businessProfileRequestVO.getPayment_delivery());
            businessProfileVO.setPayment_delivery_otheryes(businessProfileRequestVO.getPayment_delivery_otheryes());
            businessProfileVO.setGoods_delivery(businessProfileRequestVO.getGoods_delivery());
            businessProfileVO.setTerminal_type(businessProfileRequestVO.getTerminal_type());
            businessProfileVO.setTerminal_type_other(businessProfileRequestVO.getTerminal_type_other());
            businessProfileVO.setTerminal_type_otheryes(businessProfileRequestVO.getTerminal_type_otheryes());
            businessProfileVO.setOne_time_percentage(businessProfileRequestVO.getOne_time_percentage());
            businessProfileVO.setMoto_percentage(businessProfileRequestVO.getMoto_percentage());
            businessProfileVO.setRecurring_percentage(businessProfileRequestVO.getRecurring_percentage());
            businessProfileVO.setThreedsecure_percentage(businessProfileRequestVO.getThreedsecure_percentage());
            businessProfileVO.setInternet_percentage(businessProfileRequestVO.getInternet_percentage());
            businessProfileVO.setSwipe_percentage(businessProfileRequestVO.getSwipe_percentage());
            businessProfileVO.setPayment_type_yes(businessProfileRequestVO.getPayment_type_yes());
            businessProfileVO.setBusinessProfileSaved(businessProfileRequestVO.getBusinessProfileSaved());
            businessProfileVO.setOrderconfirmation_post(businessProfileRequestVO.getOrderconfirmation_post());
            businessProfileVO.setOrderconfirmation_email(businessProfileRequestVO.getOrderconfirmation_email());
            businessProfileVO.setOrderconfirmation_sms(businessProfileRequestVO.getOrderconfirmation_sms());
            businessProfileVO.setOrderconfirmation_other(businessProfileRequestVO.getOrderconfirmation_other());
            businessProfileVO.setOrderconfirmation_other_yes(businessProfileRequestVO.getOrderconfirmation_other_yes());
            businessProfileVO.setPhysicalgoods_delivered(businessProfileRequestVO.getPhysicalgoods_delivered());
            businessProfileVO.setViainternetgoods_delivered(businessProfileRequestVO.getOrderconfirmation_post());
            businessProfileVO.setIs_website_live(businessProfileRequestVO.getIs_website_live());
            businessProfileVO.setSeasonal_fluctuating_yes(businessProfileRequestVO.getSeasonal_fluctuating_yes());
            businessProfileVO.setAffiliate_programs_details(businessProfileRequestVO.getAffiliate_programs_details());
            businessProfileVO.setCustomer_support(businessProfileRequestVO.getCustomer_support());
            businessProfileVO.setCustsupportwork_hours(businessProfileRequestVO.getCustsupportwork_hours());
            businessProfileVO.setCustomersupport_email(businessProfileRequestVO.getCustomersupport_email());
            businessProfileVO.setTechnical_contact(businessProfileRequestVO.getTechnical_contact());
            businessProfileVO.setCoolingoffperiod(businessProfileRequestVO.getCoolingoffperiod());
            businessProfileVO.setCoolingoffperiod(businessProfileRequestVO.getCoolingoffperiod());
            businessProfileVO.setListfraudtools(businessProfileRequestVO.getListfraudtools());
            businessProfileVO.setListfraudtools_yes(businessProfileRequestVO.getListfraudtools_yes());
            businessProfileVO.setCustomers_identification(businessProfileRequestVO.getCustomers_identification());
            businessProfileVO.setCustomers_identification_yes(businessProfileRequestVO.getCustomers_identification_yes());

        }
        return businessProfileVO;
    }



    private BankProfileVO getbankProfileVO(BankProfileRequestVO bankProfileRequestVO)
    {
        BankProfileVO bankProfileVO = null;

        if (bankProfileRequestVO != null)
        {
            bankProfileVO = new BankProfileVO();
            bankProfileVO.setApplication_id(bankProfileRequestVO.getApplication_id());
            bankProfileVO.setCurrencyrequested_bankaccount(bankProfileRequestVO.getCurrencyrequested_bankaccount());
            bankProfileVO.setCurrencyrequested_productssold(bankProfileRequestVO.getCurrencyrequested_productssold());
            bankProfileVO.setBankinfo_bic(bankProfileRequestVO.getBankinfo_bic());
            bankProfileVO.setBankinfo_bank_name(bankProfileRequestVO.getBankinfo_bank_name());
            bankProfileVO.setBankinfo_bankaddress(bankProfileRequestVO.getBankinfo_bankaddress());
            bankProfileVO.setBankinfo_bankTelCC(bankProfileRequestVO.getBankinfo_bankTelCC());
            bankProfileVO.setBankinfo_bankphonenumber(bankProfileRequestVO.getBankinfo_bankphonenumber());
            bankProfileVO.setBankinfo_aba_routingcode(bankProfileRequestVO.getBankinfo_aba_routingcode());
            bankProfileVO.setBankinfo_accountholder(bankProfileRequestVO.getBankinfo_accountholder());
            bankProfileVO.setBankinfo_IBAN(bankProfileRequestVO.getBankinfo_IBAN());
            bankProfileVO.setBankinfo_accountnumber(bankProfileRequestVO.getBankinfo_accountnumber());
            bankProfileVO.setBankinfo_currency(bankProfileRequestVO.getBankinfo_currency());
            bankProfileVO.setCurrency(bankProfileRequestVO.getCurrency());
           /* bankProfileVO.setCurrency_products_INR(bankProfileRequestVO.getCurrency_products_INR());
            bankProfileVO.setCurrency_products_USD(bankProfileRequestVO.getCurrency_products_USD());
            bankProfileVO.setCurrency_products_EUR(bankProfileRequestVO.getCurrency_products_EUR());
            bankProfileVO.setCurrency_products_GBP(bankProfileRequestVO.getCurrency_products_GBP());
            bankProfileVO.setCurrency_products_JPY(bankProfileRequestVO.getCurrency_products_JPY());
            bankProfileVO.setCurrency_products_PEN(bankProfileRequestVO.getCurrency_products_PEN());
            bankProfileVO.setCurrency_payments_INR(bankProfileRequestVO.getCurrency_payments_INR());
            bankProfileVO.setCurrency_payments_USD(bankProfileRequestVO.getCurrency_payments_USD());
            bankProfileVO.setCurrency_payments_EUR(bankProfileRequestVO.getCurrency_payments_EUR());
            bankProfileVO.setCurrency_payments_GBP(bankProfileRequestVO.getCurrency_payments_GBP());
            bankProfileVO.setCurrency_payments_JPY(bankProfileRequestVO.getCurrency_payments_JPY());
            bankProfileVO.setCurrency_payments_PEN(bankProfileRequestVO.getCurrency_payments_PEN());*/
            bankProfileVO.setBank_account_currencies(bankProfileRequestVO.getBank_account_currencies());
            bankProfileVO.setProduct_sold_currencies(bankProfileRequestVO.getProduct_sold_currencies());
            bankProfileVO.setAquirer(bankProfileRequestVO.getAquirer());
            bankProfileVO.setReasonaquirer(bankProfileRequestVO.getReasonaquirer());
            bankProfileVO.setCustomer_trans_data(bankProfileRequestVO.getCustomer_trans_data());
            bankProfileVO.setCurrency_products_HKD(bankProfileRequestVO.getCurrency_products_HKD());
            bankProfileVO.setCurrency_products_AUD(bankProfileRequestVO.getCurrency_products_AUD());
            bankProfileVO.setCurrency_products_CAD(bankProfileRequestVO.getCurrency_products_CAD());
            bankProfileVO.setCurrency_products_DKK(bankProfileRequestVO.getCurrency_products_DKK());
            bankProfileVO.setCurrency_products_SEK(bankProfileRequestVO.getCurrency_products_SEK());
            bankProfileVO.setCurrency_products_NOK(bankProfileRequestVO.getCurrency_products_NOK());
            bankProfileVO.setCurrency_payments_HKD(bankProfileRequestVO.getCurrency_payments_HKD());
            bankProfileVO.setCurrency_payments_AUD(bankProfileRequestVO.getCurrency_payments_AUD());
            bankProfileVO.setCurrency_payments_CAD(bankProfileRequestVO.getCurrency_payments_CAD());
            bankProfileVO.setCurrency_payments_DKK(bankProfileRequestVO.getCurrency_payments_DKK());
            bankProfileVO.setCurrency_payments_SEK(bankProfileRequestVO.getCurrency_payments_SEK());
            bankProfileVO.setCurrency_payments_NOK(bankProfileRequestVO.getCurrency_payments_NOK());
            bankProfileVO.setBankinfo_contactperson(bankProfileRequestVO.getBankinfo_contactperson());
            bankProfileVO.setSalesvolume_lastmonth(bankProfileRequestVO.getSalesvolume_lastmonth());
            bankProfileVO.setSalesvolume_2monthsago(bankProfileRequestVO.getSalesvolume_2monthsago());
            bankProfileVO.setSalesvolume_3monthsago(bankProfileRequestVO.getSalesvolume_3monthsago());
            bankProfileVO.setSalesvolume_4monthsago(bankProfileRequestVO.getSalesvolume_4monthsago());
            bankProfileVO.setSalesvolume_5monthsago(bankProfileRequestVO.getSalesvolume_5monthsago());
            bankProfileVO.setSalesvolume_6monthsago(bankProfileRequestVO.getSalesvolume_6monthsago());
            bankProfileVO.setSalesvolume_12monthsago(bankProfileRequestVO.getSalesvolume_12monthsago());
            bankProfileVO.setSalesvolume_year2(bankProfileRequestVO.getSalesvolume_year2());
            bankProfileVO.setSalesvolume_year3(bankProfileRequestVO.getSalesvolume_year3());
            bankProfileVO.setNumberoftransactions_lastmonth(bankProfileRequestVO.getNumberoftransactions_lastmonth());
            bankProfileVO.setNumberoftransactions_2monthsago(bankProfileRequestVO.getNumberoftransactions_2monthsago());
            bankProfileVO.setNumberoftransactions_3monthsago(bankProfileRequestVO.getNumberoftransactions_3monthsago());
            bankProfileVO.setNumberoftransactions_4monthsago(bankProfileRequestVO.getNumberoftransactions_4monthsago());
            bankProfileVO.setNumberoftransactions_5monthsago(bankProfileRequestVO.getNumberoftransactions_5monthsago());
            bankProfileVO.setNumberoftransactions_6monthsago(bankProfileRequestVO.getNumberoftransactions_6monthsago());
            bankProfileVO.setNumberoftransactions_12monthsago(bankProfileRequestVO.getNumberoftransactions_12monthsago());
            bankProfileVO.setNumberoftransactions_12monthsago(bankProfileRequestVO.getNumberoftransactions_12monthsago());
            bankProfileVO.setNumberoftransactions_year3(bankProfileRequestVO.getNumberoftransactions_year3());
            bankProfileVO.setChargebackvolume_lastmonth(bankProfileRequestVO.getChargebackvolume_lastmonth());
            bankProfileVO.setChargebackvolume_2monthsago(bankProfileRequestVO.getChargebackvolume_2monthsago());
            bankProfileVO.setChargebackvolume_3monthsago(bankProfileRequestVO.getChargebackvolume_3monthsago());
            bankProfileVO.setChargebackvolume_4monthsago(bankProfileRequestVO.getChargebackvolume_4monthsago());
            bankProfileVO.setChargebackvolume_5monthsago(bankProfileRequestVO.getChargebackvolume_5monthsago());
            bankProfileVO.setChargebackvolume_6monthsago(bankProfileRequestVO.getChargebackvolume_6monthsago());
            bankProfileVO.setChargebackvolume_12monthsago(bankProfileRequestVO.getChargebackvolume_12monthsago());
            bankProfileVO.setChargebackvolume_year2(bankProfileRequestVO.getChargebackvolume_year2());
            bankProfileVO.setChargebackvolume_year3(bankProfileRequestVO.getChargebackvolume_year3());
            bankProfileVO.setNumberofchargebacks_lastmonth(bankProfileRequestVO.getNumberofchargebacks_lastmonth());
            bankProfileVO.setNumberofchargebacks_2monthsago(bankProfileRequestVO.getNumberofchargebacks_2monthsago());
            bankProfileVO.setNumberofchargebacks_3monthsago(bankProfileRequestVO.getNumberofchargebacks_3monthsago());
            bankProfileVO.setNumberofchargebacks_4monthsago(bankProfileRequestVO.getNumberofchargebacks_4monthsago());
            bankProfileVO.setNumberofchargebacks_5monthsago(bankProfileRequestVO.getNumberofchargebacks_5monthsago());
            bankProfileVO.setNumberofchargebacks_6monthsago(bankProfileRequestVO.getNumberofchargebacks_6monthsago());
            bankProfileVO.setNumberofchargebacks_12monthsago(bankProfileRequestVO.getNumberofchargebacks_12monthsago());
            bankProfileVO.setNumberofchargebacks_year2(bankProfileRequestVO.getNumberofchargebacks_year2());
            bankProfileVO.setNumberofchargebacks_year3(bankProfileRequestVO.getNumberofchargebacks_year3());
            bankProfileVO.setRefundsvolume_lastmonth(bankProfileRequestVO.getRefundsvolume_lastmonth());
            bankProfileVO.setRefundsvolume_2monthsago(bankProfileRequestVO.getRefundsvolume_2monthsago());
            bankProfileVO.setRefundsvolume_3monthsago(bankProfileRequestVO.getRefundsvolume_3monthsago());
            bankProfileVO.setRefundsvolume_4monthsago(bankProfileRequestVO.getRefundsvolume_4monthsago());
            bankProfileVO.setRefundsvolume_5monthsago(bankProfileRequestVO.getRefundsvolume_5monthsago());
            bankProfileVO.setRefundsvolume_6monthsago(bankProfileRequestVO.getRefundsvolume_6monthsago());
            bankProfileVO.setRefundsvolume_12monthsago(bankProfileRequestVO.getRefundsvolume_12monthsago());
            bankProfileVO.setRefundsvolume_year2(bankProfileRequestVO.getRefundsvolume_year2());
            bankProfileVO.setRefundsvolume_year3(bankProfileRequestVO.getRefundsvolume_year3());
            bankProfileVO.setNumberofrefunds_lastmonth(bankProfileRequestVO.getNumberofrefunds_lastmonth());
            bankProfileVO.setNumberofrefunds_2monthsago(bankProfileRequestVO.getNumberofrefunds_2monthsago());
            bankProfileVO.setNumberofrefunds_3monthsago(bankProfileRequestVO.getNumberofrefunds_3monthsago());
            bankProfileVO.setNumberofrefunds_4monthsago(bankProfileRequestVO.getNumberofrefunds_4monthsago());
            bankProfileVO.setNumberofrefunds_5monthsago(bankProfileRequestVO.getNumberofrefunds_5monthsago());
            bankProfileVO.setNumberofrefunds_6monthsago(bankProfileRequestVO.getNumberofrefunds_6monthsago());
            bankProfileVO.setNumberofrefunds_12monthsago(bankProfileRequestVO.getNumberofrefunds_12monthsago());
            bankProfileVO.setNumberofrefunds_year2(bankProfileRequestVO.getNumberofrefunds_year2());
            bankProfileVO.setNumberofrefunds_year3(bankProfileRequestVO.getNumberofrefunds_year3());
            bankProfileVO.setChargebackratio_lastmonth(bankProfileRequestVO.getChargebackratio_lastmonth());
            bankProfileVO.setChargebackratio_2monthsago(bankProfileRequestVO.getChargebackratio_2monthsago());
            bankProfileVO.setChargebackratio_3monthsago(bankProfileRequestVO.getChargebackratio_3monthsago());
            bankProfileVO.setChargebackratio_4monthsago(bankProfileRequestVO.getChargebackratio_4monthsago());
            bankProfileVO.setChargebackratio_5monthsago(bankProfileRequestVO.getChargebackratio_5monthsago());
            bankProfileVO.setChargebackratio_6monthsago(bankProfileRequestVO.getChargebackratio_6monthsago());
            bankProfileVO.setChargebackratio_12monthsago(bankProfileRequestVO.getChargebackratio_12monthsago());
            bankProfileVO.setChargebackratio_year2(bankProfileRequestVO.getChargebackratio_year2());
            bankProfileVO.setChargebackratio_year3(bankProfileRequestVO.getChargebackratio_year3());
            bankProfileVO.setRefundratio_lastmonth(bankProfileRequestVO.getRefundratio_lastmonth());
            bankProfileVO.setRefundratio_2monthsago(bankProfileRequestVO.getRefundratio_2monthsago());
            bankProfileVO.setRefundratio_3monthsago(bankProfileRequestVO.getRefundratio_3monthsago());
            bankProfileVO.setRefundratio_4monthsago(bankProfileRequestVO.getRefundratio_4monthsago());
            bankProfileVO.setRefundratio_5monthsago(bankProfileRequestVO.getRefundratio_5monthsago());
            bankProfileVO.setRefundratio_6monthsago(bankProfileRequestVO.getRefundratio_6monthsago());
            bankProfileVO.setRefundratio_12monthsago(bankProfileRequestVO.getRefundratio_12monthsago());
            bankProfileVO.setRefundratio_year2(bankProfileRequestVO.getRefundratio_year2());
            bankProfileVO.setRefundratio_year3(bankProfileRequestVO.getRefundratio_year3());
            bankProfileVO.setIscurrencywisebankinfo(bankProfileRequestVO.getIscurrencywisebankinfo());
            bankProfileVO.setBankProfileSaved(bankProfileRequestVO.getBankProfileSaved());
            bankProfileVO.setIsProcessingHistory(bankProfileRequestVO.getIsProcessingHistory());
            bankProfileVO.setCurrency(bankProfileRequestVO.getCurrency());
            bankProfileVO.setCurrencywisebankinfo_id(bankProfileRequestVO.getCurrencywisebankinfo_id());
            bankProfileVO.setProcessinghistory_id(bankProfileRequestVO.getProcessinghistory_id());
            bankProfileVO.setProcessinghistory_creation_time(bankProfileRequestVO.getProcessinghistory_creation_time());
            bankProfileVO.setProcessinghistory_updation_time(bankProfileRequestVO.getProcessinghistory_updation_time());
        }
            return bankProfileVO;
        }

    private ExtraDetailsProfileVO getextradetails(ExtraDetailsProfileRequestVO extraDetailsProfileRequestVO)
    {
        ExtraDetailsProfileVO extraDetailsProfileVO = null;

        if (extraDetailsProfileRequestVO != null)

            extraDetailsProfileVO = new ExtraDetailsProfileVO();
        {
            extraDetailsProfileVO.setApplication_id(extraDetailsProfileRequestVO.getApplication_id());
            extraDetailsProfileVO.setCompany_financialReport(extraDetailsProfileRequestVO.getCompany_financialReport());
            extraDetailsProfileVO.setCompany_financialReportYes(extraDetailsProfileRequestVO.getCompany_financialReportYes());
            extraDetailsProfileVO.setFinancialReport_institution(extraDetailsProfileRequestVO.getFinancialReport_institution());
            extraDetailsProfileVO.setFinancialReport_available(extraDetailsProfileRequestVO.getFinancialReport_available());
            extraDetailsProfileVO.setFinancialReport_availableYes(extraDetailsProfileRequestVO.getFinancialReport_availableYes());
            extraDetailsProfileVO.setOwnerSince(extraDetailsProfileRequestVO.getOwnerSince());
            extraDetailsProfileVO.setSocialSecurity(extraDetailsProfileRequestVO.getSocialSecurity());
            extraDetailsProfileVO.setCompany_formParticipation(extraDetailsProfileRequestVO.getCompany_formParticipation());
            extraDetailsProfileVO.setFinancialObligation(extraDetailsProfileRequestVO.getFinancialObligation());
            extraDetailsProfileVO.setCompliance_punitiveSanction(extraDetailsProfileRequestVO.getCompliance_punitiveSanction());
            extraDetailsProfileVO.setCompliance_punitiveSanctionYes(extraDetailsProfileRequestVO.getCompliance_punitiveSanctionYes());
            extraDetailsProfileVO.setWorkingExperience(extraDetailsProfileRequestVO.getWorkingExperience());
            extraDetailsProfileVO.setGoodsInsuranceOffered(extraDetailsProfileRequestVO.getGoodsInsuranceOffered());
            extraDetailsProfileVO.setFulfillment_productEmail(extraDetailsProfileRequestVO.getFulfillment_productEmail());
            extraDetailsProfileVO.setFulfillment_productEmailYes(extraDetailsProfileRequestVO.getFulfillment_productEmailYes());
            extraDetailsProfileVO.setBlacklistedAccountClosed(extraDetailsProfileRequestVO.getBlacklistedAccountClosed());
            extraDetailsProfileVO.setBlacklistedAccountClosedYes(extraDetailsProfileRequestVO.getBlacklistedAccountClosedYes());
            extraDetailsProfileVO.setShiping_deliveryMethod(extraDetailsProfileRequestVO.getShiping_deliveryMethod());
            extraDetailsProfileVO.setTransactionMonitoringProcess(extraDetailsProfileRequestVO.getTransactionMonitoringProcess());
            extraDetailsProfileVO.setOperationalLicense(extraDetailsProfileRequestVO.getOperationalLicense());
            extraDetailsProfileVO.setSupervisorregularcontrole(extraDetailsProfileRequestVO.getSupervisorregularcontrole());
            extraDetailsProfileVO.setDeedOfAgreement(extraDetailsProfileRequestVO.getDeedOfAgreement());
            extraDetailsProfileVO.setDeedOfAgreementYes(extraDetailsProfileRequestVO.getDeedOfAgreementYes());
            extraDetailsProfileVO.setExtraDetailsProfileSaved(extraDetailsProfileRequestVO.getExtraDetailsProfileSaved());

        }
        return extraDetailsProfileVO;
    }

    private AddressIdentificationVO getcompany_AddressVo(CompanyProfileRequestVO companyProfileRequestVO)
    {
        AddressIdentificationVO company_addressIdentificationVO = null;

        if (companyProfileRequestVO != null)

        {
            company_addressIdentificationVO = new AddressIdentificationVO();
        //17 fields
            company_addressIdentificationVO.setCompany_name(companyProfileRequestVO.getMerchantName());
            company_addressIdentificationVO.setRegistration_number(companyProfileRequestVO.getCompanyRegistrationNumber());
            company_addressIdentificationVO.setDate_of_registration(companyProfileRequestVO.getCompany_Date_Registration());
            company_addressIdentificationVO.setPhone_cc(companyProfileRequestVO.getCompanyphonecc1());
            company_addressIdentificationVO.setPhone_number(companyProfileRequestVO.getCompanyTelephoneNO());
            company_addressIdentificationVO.setFax(companyProfileRequestVO.getCompanyFax());
            company_addressIdentificationVO.setEmail_id(companyProfileRequestVO.getCompanyEmailAddress());
            company_addressIdentificationVO.setAddress(companyProfileRequestVO.getLocationAddress());
            company_addressIdentificationVO.setCity(companyProfileRequestVO.getMerchantCity());
            company_addressIdentificationVO.setState(companyProfileRequestVO.getMerchantState());
            company_addressIdentificationVO.setStreet(companyProfileRequestVO.getMerchantStreet());
            company_addressIdentificationVO.setZipcode(companyProfileRequestVO.getMerchantZipCode());
            company_addressIdentificationVO.setCountry(companyProfileRequestVO.getMerchantCountry());
            company_addressIdentificationVO.setAddressId(companyProfileRequestVO.getMerchant_addressId());
            company_addressIdentificationVO.setAddressProof(companyProfileRequestVO.getMerchant_addressproof());
            company_addressIdentificationVO.setVatidentification(companyProfileRequestVO.getVatIdentification());
            company_addressIdentificationVO.setFederalTaxId(companyProfileRequestVO.getFedraltaxid());
        }
        return company_addressIdentificationVO;


        }
    private AddressIdentificationVO getbusiness_addressIdentificationVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        AddressIdentificationVO business_addressIdentificationVO = null;

        if (companyProfileRequestVO != null)

        {
            business_addressIdentificationVO = new AddressIdentificationVO();
            //9 fields
            business_addressIdentificationVO.setCompany_name(companyProfileRequestVO.getCorporateName());
            business_addressIdentificationVO.setAddressProof(companyProfileRequestVO.getCorporate_addressproof());
            business_addressIdentificationVO.setAddressId(companyProfileRequestVO.getCorporate_addressId());
            business_addressIdentificationVO.setAddress(companyProfileRequestVO.getCorporateAddress());
            business_addressIdentificationVO.setCity(companyProfileRequestVO.getCorporateCity());
            business_addressIdentificationVO.setState(companyProfileRequestVO.getCorporateState());
            business_addressIdentificationVO.setZipcode(companyProfileRequestVO.getCorporateZipCode());
            business_addressIdentificationVO.setCountry(companyProfileRequestVO.getCorporateCountry());
            business_addressIdentificationVO.setStreet(companyProfileRequestVO.getCorporateStreet());
        }
        return business_addressIdentificationVO;
    }

    private AddressIdentificationVO getEU_addressIdentificationVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        AddressIdentificationVO EU_addressIdentificationVO = null;

        if (companyProfileRequestVO != null)

        {
            EU_addressIdentificationVO = new AddressIdentificationVO();
//11 fields
            EU_addressIdentificationVO.setCompany_name(companyProfileRequestVO.getCorporateName());
            EU_addressIdentificationVO.setRegistred_directors(companyProfileRequestVO.getRegisteredDirectors());
            EU_addressIdentificationVO.setRegistration_number(companyProfileRequestVO.getEURegistrationNumber());
            EU_addressIdentificationVO.setAddressProof(companyProfileRequestVO.getRegistered_directors_addressproof());
            EU_addressIdentificationVO.setAddressId(companyProfileRequestVO.getRegistered_directors_addressId());
            EU_addressIdentificationVO.setAddress(companyProfileRequestVO.getRegisteredDirectorsAddress());
            EU_addressIdentificationVO.setCity(companyProfileRequestVO.getRegisteredDirectorsCity());
            EU_addressIdentificationVO.setState(companyProfileRequestVO.getRegisteredDirectorsState());
            EU_addressIdentificationVO.setZipcode(companyProfileRequestVO.getRegisteredDirectorsPostalcode());
            EU_addressIdentificationVO.setCountry(companyProfileRequestVO.getRegisteredDirectorsCountry());
            EU_addressIdentificationVO.setStreet(companyProfileRequestVO.getRegisteredDirectorsStreet());
        }
        return EU_addressIdentificationVO;

    }

    private ContactDetailsVO getmain_contactdetailsVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        ContactDetailsVO main_contactDetailsVO = null;

        if (companyProfileRequestVO != null)
        {
            main_contactDetailsVO = new ContactDetailsVO();

            main_contactDetailsVO.setName(companyProfileRequestVO.getContactName());
            main_contactDetailsVO.setEmailaddress(companyProfileRequestVO.getContactEmailAddress());
            main_contactDetailsVO.setPhonecc1(companyProfileRequestVO.getContactname_telnocc1());
            main_contactDetailsVO.setTelephonenumber(companyProfileRequestVO.getContactnamePhoneNumber());
            main_contactDetailsVO.setDesignation(companyProfileRequestVO.getContact_designation());
            main_contactDetailsVO.setSkypeIMaddress(companyProfileRequestVO.getSkypeIMaddress());
        }
        return main_contactDetailsVO;
    }

    private ContactDetailsVO gettechnical_contactdetailsVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        ContactDetailsVO tech_contactDetailsVO = null;

        if (companyProfileRequestVO != null)
        {

            tech_contactDetailsVO = new ContactDetailsVO();
            tech_contactDetailsVO.setName(companyProfileRequestVO.getTechnicalContactName());
            tech_contactDetailsVO.setEmailaddress(companyProfileRequestVO.getTechnicalEmailAddress());
            tech_contactDetailsVO.setPhonecc1(companyProfileRequestVO.getTechnicalphonecc1());
            tech_contactDetailsVO.setTelephonenumber(companyProfileRequestVO.getTechnical_telephonenumber());
            tech_contactDetailsVO.setDesignation(companyProfileRequestVO.getTechnical_designation());
        }
        return tech_contactDetailsVO;
    }

    private ContactDetailsVO getbilling_contactdetailsVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        ContactDetailsVO billing_contactDetailsVO = null;

        if (companyProfileRequestVO != null)
        {
            billing_contactDetailsVO = new ContactDetailsVO();
            billing_contactDetailsVO.setName(companyProfileRequestVO.getBillingContactName());
            billing_contactDetailsVO.setEmailaddress(companyProfileRequestVO.getBillingEmailAddress());
            billing_contactDetailsVO.setPhonecc1(companyProfileRequestVO.getFinancialphonecc1());
            billing_contactDetailsVO.setTelephonenumber(companyProfileRequestVO.getFinancial_telephonenumber());
            billing_contactDetailsVO.setDesignation(companyProfileRequestVO.getBilling_designation());
        }
        return billing_contactDetailsVO;
    }

    private ContactDetailsVO getcbk_contactdetailsVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        ContactDetailsVO cbk_contactDetailsVO = null;

        if (companyProfileRequestVO != null)
        {
            cbk_contactDetailsVO = new ContactDetailsVO();

            cbk_contactDetailsVO.setName(companyProfileRequestVO.getCbk_contactperson());
            cbk_contactDetailsVO.setEmailaddress(companyProfileRequestVO.getCbk_email());
            cbk_contactDetailsVO.setPhonecc1(companyProfileRequestVO.getCbk_phonecc());
            cbk_contactDetailsVO.setTelephonenumber(companyProfileRequestVO.getCbk_telephonenumber());
            cbk_contactDetailsVO.setDesignation(companyProfileRequestVO.getCbk_designation());
        }

        return cbk_contactDetailsVO;
    }

    private ContactDetailsVO getpci_contactdetailsVO(CompanyProfileRequestVO companyProfileRequestVO)
    {
        ContactDetailsVO pci_contactDetailsVO = null;

        if (companyProfileRequestVO != null)
        {
            pci_contactDetailsVO = new ContactDetailsVO();

            pci_contactDetailsVO.setName(companyProfileRequestVO.getPci_contactperson());
            pci_contactDetailsVO.setEmailaddress(companyProfileRequestVO.getPci_email());
            pci_contactDetailsVO.setPhonecc1(companyProfileRequestVO.getPci_phonecc());
            pci_contactDetailsVO.setTelephonenumber(companyProfileRequestVO.getPci_telephonenumber());
            pci_contactDetailsVO.setDesignation(companyProfileRequestVO.getPci_designation());
        }
        return pci_contactDetailsVO;
    }
    //ownership

    private OwnershipProfileDetailsVO getshareholder1(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO shareholder1 = null;

        if (ownershipProfileRequestVO != null)
        {
            shareholder1 = new OwnershipProfileDetailsVO();
            shareholder1.setFirstname(ownershipProfileRequestVO.getShareholderprofile1());
            shareholder1.setTitle(ownershipProfileRequestVO.getShareholderprofile1_title());
            shareholder1.setOwned(ownershipProfileRequestVO.getShareholderprofile1_owned());
            shareholder1.setTelnocc1(ownershipProfileRequestVO.getShareholderprofile1_telnocc1());
            shareholder1.setTelephonenumber(ownershipProfileRequestVO.getShareholderprofile1_telephonenumber());
            shareholder1.setEmailaddress(ownershipProfileRequestVO.getShareholderprofile1_emailaddress());
            shareholder1.setDateofbirth(ownershipProfileRequestVO.getShareholderprofile1_dateofbirth());
            shareholder1.setIdentificationtypeselect(ownershipProfileRequestVO.getShareholderprofile1_identificationtypeselect());
            shareholder1.setIdentificationtype(ownershipProfileRequestVO.getShareholderprofile1_identificationtype());
            shareholder1.setState(ownershipProfileRequestVO.getShareholderprofile1_State());
            shareholder1.setAddress(ownershipProfileRequestVO.getShareholderprofile1_address());
            shareholder1.setCity(ownershipProfileRequestVO.getShareholderprofile1_city());
            shareholder1.setZipcode(ownershipProfileRequestVO.getShareholderprofile1_zip());
            shareholder1.setCountry(ownershipProfileRequestVO.getShareholderprofile1_country());
            shareholder1.setStreet(ownershipProfileRequestVO.getShareholderprofile1_street());
            shareholder1.setNationality(ownershipProfileRequestVO.getShareholderprofile1_nationality());
            shareholder1.setPassportexpirydate(ownershipProfileRequestVO.getShareholderprofile1_Passportexpirydate());
            shareholder1.setPassportissuedate(ownershipProfileRequestVO.getShareholderprofile1PassportIssueDate());
            shareholder1.setPoliticallyexposed(ownershipProfileRequestVO.getShareholderprofile1_politicallyexposed());
            shareholder1.setCriminalrecord(ownershipProfileRequestVO.getShareholderprofile1_criminalrecord());
            shareholder1.setAddressProof(ownershipProfileRequestVO.getShareholderprofile1_addressproof());
            shareholder1.setAddressId(ownershipProfileRequestVO.getShareholderprofile1_addressId());
            shareholder1.setLastname(ownershipProfileRequestVO.getShareholderprofile1_lastname());
        }
        return shareholder1;
    }
    private OwnershipProfileDetailsVO getshareholder2(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO shareholder2= null;

        if (ownershipProfileRequestVO != null)
        {
            shareholder2 = new OwnershipProfileDetailsVO();
            shareholder2.setFirstname(ownershipProfileRequestVO.getShareholderprofile2());
            shareholder2.setTitle(ownershipProfileRequestVO.getShareholderprofile2_title());
            shareholder2.setOwned(ownershipProfileRequestVO.getShareholderprofile2_owned());
            shareholder2.setTelnocc1(ownershipProfileRequestVO.getShareholderprofile2_telnocc2());
            shareholder2.setTelephonenumber(ownershipProfileRequestVO.getShareholderprofile2_telephonenumber());
            shareholder2.setEmailaddress(ownershipProfileRequestVO.getShareholderprofile2_emailaddress());
            shareholder2.setDateofbirth(ownershipProfileRequestVO.getShareholderprofile2_dateofbirth());
            shareholder2.setIdentificationtypeselect(ownershipProfileRequestVO.getShareholderprofile2_identificationtypeselect());
            shareholder2.setIdentificationtype(ownershipProfileRequestVO.getShareholderprofile2_identificationtype());
            shareholder2.setState(ownershipProfileRequestVO.getShareholderprofile2_State());
            shareholder2.setAddress(ownershipProfileRequestVO.getShareholderprofile2_address());
            shareholder2.setCity(ownershipProfileRequestVO.getShareholderprofile2_city());
            shareholder2.setZipcode(ownershipProfileRequestVO.getShareholderprofile2_zip());
            shareholder2.setCountry(ownershipProfileRequestVO.getShareholderprofile2_country());
            shareholder2.setStreet(ownershipProfileRequestVO.getShareholderprofile2_street());
            shareholder2.setNationality(ownershipProfileRequestVO.getShareholderprofile2_nationality());
            shareholder2.setPassportexpirydate(ownershipProfileRequestVO.getShareholderprofile2_Passportexpirydate());
            shareholder2.setPassportissuedate(ownershipProfileRequestVO.getShareholderprofile2PassportIssueDate());
            shareholder2.setPoliticallyexposed(ownershipProfileRequestVO.getShareholderprofile2_politicallyexposed());
            shareholder2.setCriminalrecord(ownershipProfileRequestVO.getShareholderprofile2_criminalrecord());
            shareholder2.setAddressProof(ownershipProfileRequestVO.getShareholderprofile2_addressproof());
            shareholder2.setAddressId(ownershipProfileRequestVO.getShareholderprofile2_addressId());
            shareholder2.setLastname(ownershipProfileRequestVO.getShareholderprofile2_lastname());
        }
        return shareholder2;
    }
    private OwnershipProfileDetailsVO getshareholder3(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO shareholder3= null;

        if (ownershipProfileRequestVO != null)
        {
            shareholder3 = new OwnershipProfileDetailsVO();

            shareholder3.setFirstname(ownershipProfileRequestVO.getShareholderprofile3());
            shareholder3.setTitle(ownershipProfileRequestVO.getShareholderprofile3_title());
            shareholder3.setOwned(ownershipProfileRequestVO.getShareholderprofile3_owned());
            shareholder3.setTelnocc1(ownershipProfileRequestVO.getShareholderprofile3_telnocc2());
            shareholder3.setTelephonenumber(ownershipProfileRequestVO.getShareholderprofile3_telephonenumber());
            shareholder3.setEmailaddress(ownershipProfileRequestVO.getShareholderprofile3_emailaddress());
            shareholder3.setDateofbirth(ownershipProfileRequestVO.getShareholderprofile3_dateofbirth());
            shareholder3.setIdentificationtypeselect(ownershipProfileRequestVO.getShareholderprofile3_identificationtypeselect());
            shareholder3.setIdentificationtype(ownershipProfileRequestVO.getShareholderprofile3_identificationtype());
            shareholder3.setState(ownershipProfileRequestVO.getShareholderprofile3_State());
            shareholder3.setAddress(ownershipProfileRequestVO.getShareholderprofile3_address());
            shareholder3.setCity(ownershipProfileRequestVO.getShareholderprofile3_city());
            shareholder3.setZipcode(ownershipProfileRequestVO.getShareholderprofile3_zip());
            shareholder3.setCountry(ownershipProfileRequestVO.getShareholderprofile3_country());
            shareholder3.setStreet(ownershipProfileRequestVO.getShareholderprofile3_street());
            shareholder3.setNationality(ownershipProfileRequestVO.getShareholderprofile3_nationality());
            shareholder3.setPassportexpirydate(ownershipProfileRequestVO.getShareholderprofile3_Passportexpirydate());
            shareholder3.setPassportissuedate(ownershipProfileRequestVO.getShareholderprofile3PassportIssueDate());
            shareholder3.setPoliticallyexposed(ownershipProfileRequestVO.getShareholderprofile3_politicallyexposed());
            shareholder3.setPoliticallyexposed(ownershipProfileRequestVO.getShareholderprofile3_politicallyexposed());
            shareholder3.setCriminalrecord(ownershipProfileRequestVO.getShareholderprofile3_criminalrecord());
            shareholder3.setAddressProof(ownershipProfileRequestVO.getShareholderprofile3_addressproof());
            shareholder3.setAddressId(ownershipProfileRequestVO.getShareholderprofile3_addressId());
            shareholder3.setLastname(ownershipProfileRequestVO.getShareholderprofile3_lastname());
        }
        return shareholder3;
    }

    private OwnershipProfileDetailsVO getcorporate1(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO corporate1 = null;

        if (ownershipProfileRequestVO != null)
        {
            corporate1 = new OwnershipProfileDetailsVO();

            corporate1.setName(ownershipProfileRequestVO.getCorporateshareholder1_Name());
            corporate1.setRegistrationNumber(ownershipProfileRequestVO.getCorporateshareholder1_RegNumber());
            corporate1.setAddress(ownershipProfileRequestVO.getCorporateshareholder1_Address());
            corporate1.setCity(ownershipProfileRequestVO.getCorporateshareholder1_City());
            corporate1.setState(ownershipProfileRequestVO.getCorporateshareholder1_State());
            corporate1.setZipcode(ownershipProfileRequestVO.getCorporateshareholder1_ZipCode());
            corporate1.setCountry(ownershipProfileRequestVO.getCorporateshareholder1_Country());
            corporate1.setStreet(ownershipProfileRequestVO.getCorporateshareholder1_Street());
            corporate1.setOwned(ownershipProfileRequestVO.getCorporateshareholder1_owned());
            corporate1.setAddressProof(ownershipProfileRequestVO.getCorporateshareholder1_addressproof());
            corporate1.setAddressId(ownershipProfileRequestVO.getCorporateshareholder1_addressId());
            corporate1.setIdentificationtypeselect(ownershipProfileRequestVO.getCorporateshareholder1_identificationtypeselect());
            corporate1.setIdentificationtype(ownershipProfileRequestVO.getCorporateshareholder1_identificationtype());
        }
        return corporate1;
    }
    private OwnershipProfileDetailsVO getcorporate2(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO corporate2 = null;

        if (ownershipProfileRequestVO != null)
        {
            corporate2 = new OwnershipProfileDetailsVO();

            corporate2.setName(ownershipProfileRequestVO.getCorporateshareholder2_Name());
            corporate2.setRegistrationNumber(ownershipProfileRequestVO.getCorporateshareholder2_RegNumber());
            corporate2.setAddress(ownershipProfileRequestVO.getCorporateshareholder2_Address());
            corporate2.setCity(ownershipProfileRequestVO.getCorporateshareholder2_City());
            corporate2.setState(ownershipProfileRequestVO.getCorporateshareholder2_State());
            corporate2.setZipcode(ownershipProfileRequestVO.getCorporateshareholder2_ZipCode());
            corporate2.setCountry(ownershipProfileRequestVO.getCorporateshareholder2_Country());
            corporate2.setStreet(ownershipProfileRequestVO.getCorporateshareholder2_Street());
            corporate2.setOwned(ownershipProfileRequestVO.getCorporateshareholder2_owned());
            corporate2.setAddressProof(ownershipProfileRequestVO.getCorporateshareholder2_addressproof());
            corporate2.setAddressId(ownershipProfileRequestVO.getCorporateshareholder2_addressId());
            corporate2.setIdentificationtypeselect(ownershipProfileRequestVO.getCorporateshareholder2_identificationtypeselect());
            corporate2.setIdentificationtype(ownershipProfileRequestVO.getCorporateshareholder2_identificationtype());
        }
        return corporate2;
    }
    private OwnershipProfileDetailsVO getcorporate3(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO corporate3 = null;

        if (ownershipProfileRequestVO != null)
        {
            corporate3 = new OwnershipProfileDetailsVO();

            corporate3.setName(ownershipProfileRequestVO.getCorporateshareholder3_Name());
            corporate3.setRegistrationNumber(ownershipProfileRequestVO.getCorporateshareholder3_RegNumber());
            corporate3.setAddress(ownershipProfileRequestVO.getCorporateshareholder3_Address());
            corporate3.setCity(ownershipProfileRequestVO.getCorporateshareholder3_City());
            corporate3.setState(ownershipProfileRequestVO.getCorporateshareholder3_State());
            corporate3.setZipcode(ownershipProfileRequestVO.getCorporateshareholder3_ZipCode());
            corporate3.setCountry(ownershipProfileRequestVO.getCorporateshareholder3_Country());
            corporate3.setStreet(ownershipProfileRequestVO.getCorporateshareholder3_Street());
            corporate3.setOwned(ownershipProfileRequestVO.getCorporateshareholder3_owned());
            corporate3.setAddressProof(ownershipProfileRequestVO.getCorporateshareholder3_addressproof());
            corporate3.setAddressId(ownershipProfileRequestVO.getCorporateshareholder3_addressId());
            corporate3.setIdentificationtypeselect(ownershipProfileRequestVO.getCorporateshareholder3_identificationtypeselect());
            corporate3.setIdentificationtype(ownershipProfileRequestVO.getCorporateshareholder3_identificationtype());
        }
        return corporate3;
    }
    private OwnershipProfileDetailsVO getdirector1(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO director1 = null;

        if (ownershipProfileRequestVO != null)
        {
            director1 = new OwnershipProfileDetailsVO();

            director1.setFirstname(ownershipProfileRequestVO.getDirectorsprofile());
            director1.setTitle(ownershipProfileRequestVO.getDirectorsprofile_title());
            director1.setOwned(ownershipProfileRequestVO.getDirectorsprofile_owned());
            director1.setTelnocc1(ownershipProfileRequestVO.getDirectorsprofile_telnocc1());
            director1.setTelephonenumber(ownershipProfileRequestVO.getDirectorsprofile_telephonenumber());
            director1.setEmailaddress(ownershipProfileRequestVO.getDirectorsprofile_emailaddress());

            director1.setDateofbirth(ownershipProfileRequestVO.getDirectorsprofile_dateofbirth());

            director1.setIdentificationtypeselect(ownershipProfileRequestVO.getDirectorsprofile_identificationtypeselect());
            director1.setIdentificationtype(ownershipProfileRequestVO.getDirectorsprofile_identificationtype());
            director1.setState(ownershipProfileRequestVO.getDirectorsprofile_State());
            director1.setAddress(ownershipProfileRequestVO.getDirectorsprofile_address());
            director1.setCity(ownershipProfileRequestVO.getDirectorsprofile_city());
            director1.setZipcode(ownershipProfileRequestVO.getDirectorsprofile_zip());
            director1.setCountry(ownershipProfileRequestVO.getDirectorsprofile_country());
            director1.setStreet(ownershipProfileRequestVO.getDirectorsprofile_street());
            director1.setNationality(ownershipProfileRequestVO.getDirectorsprofile_nationality());
            director1.setPassportexpirydate(ownershipProfileRequestVO.getDirectorsprofile_Passportexpirydate());
            director1.setPassportissuedate(ownershipProfileRequestVO.getDirectorsprofilePassportissuedate());
            director1.setPoliticallyexposed(ownershipProfileRequestVO.getDirectorsprofile_politicallyexposed());
            director1.setCriminalrecord(ownershipProfileRequestVO.getDirectorsprofile_criminalrecord());
            director1.setAddressProof(ownershipProfileRequestVO.getDirectorsprofile_addressproof());
            director1.setAddressId(ownershipProfileRequestVO.getDirectorsprofile_addressId());
            director1.setLastname(ownershipProfileRequestVO.getDirectorsprofile_lastname());
        }
        return director1;
    }
    private OwnershipProfileDetailsVO getdirector2(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO director2 = null;

        if (ownershipProfileRequestVO != null)
        {
            director2 = new OwnershipProfileDetailsVO();

            director2.setFirstname(ownershipProfileRequestVO.getDirectorsprofile2());
            director2.setTitle(ownershipProfileRequestVO.getDirectorsprofile2_title());
            director2.setOwned(ownershipProfileRequestVO.getDirectorsprofile2_owned());
            director2.setTelnocc1(ownershipProfileRequestVO.getDirectorsprofile2_telnocc1());
            director2.setTelephonenumber(ownershipProfileRequestVO.getDirectorsprofile2_telephonenumber());
            director2.setEmailaddress(ownershipProfileRequestVO.getDirectorsprofile2_emailaddress());
            director2.setDateofbirth(ownershipProfileRequestVO.getDirectorsprofile2_dateofbirth());
            director2.setIdentificationtypeselect(ownershipProfileRequestVO.getDirectorsprofile2_identificationtypeselect());
            director2.setIdentificationtype(ownershipProfileRequestVO.getDirectorsprofile2_identificationtype());
            director2.setState(ownershipProfileRequestVO.getDirectorsprofile2_State());
            director2.setAddress(ownershipProfileRequestVO.getDirectorsprofile2_address());
            director2.setCity(ownershipProfileRequestVO.getDirectorsprofile2_city());
            director2.setZipcode(ownershipProfileRequestVO.getDirectorsprofile2_zip());
            director2.setCountry(ownershipProfileRequestVO.getDirectorsprofile2_country());
            director2.setStreet(ownershipProfileRequestVO.getDirectorsprofile2_street());
            director2.setNationality(ownershipProfileRequestVO.getDirectorsprofile2_nationality());
            director2.setPassportexpirydate(ownershipProfileRequestVO.getDirectorsprofile2_Passportexpirydate());
            director2.setPassportissuedate(ownershipProfileRequestVO.getDirectorsprofile2Passportissuedate());
            director2.setPoliticallyexposed(ownershipProfileRequestVO.getDirectorsprofile2_politicallyexposed());
            director2.setCriminalrecord(ownershipProfileRequestVO.getDirectorsprofile2_criminalrecord());
            director2.setAddressProof(ownershipProfileRequestVO.getDirectorsprofile2_addressproof());
            director2.setAddressId(ownershipProfileRequestVO.getDirectorsprofile2_addressId());
            director2.setLastname(ownershipProfileRequestVO.getDirectorsprofile2_lastname());
        }
        return director2;
    }
    private OwnershipProfileDetailsVO getdirector3(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO director3 = null;

        if (ownershipProfileRequestVO != null)

        {
            director3 = new OwnershipProfileDetailsVO();

            director3.setFirstname(ownershipProfileRequestVO.getDirectorsprofile3());
            director3.setTitle(ownershipProfileRequestVO.getDirectorsprofile3_title());
            director3.setOwned(ownershipProfileRequestVO.getDirectorsprofile3_owned());
            director3.setTelnocc1(ownershipProfileRequestVO.getDirectorsprofile3_telnocc1());
            director3.setTelephonenumber(ownershipProfileRequestVO.getDirectorsprofile3_telephonenumber());
            director3.setEmailaddress(ownershipProfileRequestVO.getDirectorsprofile3_emailaddress());
            director3.setDateofbirth(ownershipProfileRequestVO.getDirectorsprofile3_dateofbirth());
            director3.setIdentificationtypeselect(ownershipProfileRequestVO.getDirectorsprofile3_identificationtypeselect());
            director3.setIdentificationtype(ownershipProfileRequestVO.getDirectorsprofile3_identificationtype());
            director3.setState(ownershipProfileRequestVO.getDirectorsprofile3_State());
            director3.setAddress(ownershipProfileRequestVO.getDirectorsprofile3_address());
            director3.setCity(ownershipProfileRequestVO.getDirectorsprofile3_city());
            director3.setZipcode(ownershipProfileRequestVO.getDirectorsprofile3_zip());
            director3.setCountry(ownershipProfileRequestVO.getDirectorsprofile3_country());
            director3.setStreet(ownershipProfileRequestVO.getDirectorsprofile3_street());
            director3.setNationality(ownershipProfileRequestVO.getDirectorsprofile3_nationality());
            director3.setPassportexpirydate(ownershipProfileRequestVO.getDirectorsprofile3_Passportexpirydate());
            director3.setPassportissuedate(ownershipProfileRequestVO.getDirectorsprofile3Passportissuedate());
            director3.setPoliticallyexposed(ownershipProfileRequestVO.getDirectorsprofile3_politicallyexposed());
            director3.setCriminalrecord(ownershipProfileRequestVO.getDirectorsprofile3_criminalrecord());
            director3.setAddressProof(ownershipProfileRequestVO.getDirectorsprofile3_addressproof());
            director3.setAddressId(ownershipProfileRequestVO.getDirectorsprofile3_addressId());
            director3.setLastname(ownershipProfileRequestVO.getDirectorsprofile3_lastname());
        }
        return director3;
    }
    private OwnershipProfileDetailsVO getauthorize1(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO authorize1 = null;

        if (ownershipProfileRequestVO != null)
        {
            authorize1 = new OwnershipProfileDetailsVO();

            authorize1.setFirstname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile());
            authorize1.setTitle(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_title());
            authorize1.setOwned(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_owned());
            authorize1.setTelnocc1(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_telnocc1());
            authorize1.setTelephonenumber(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_telephonenumber());
            authorize1.setEmailaddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_emailaddress());
            authorize1.setDateofbirth(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_dateofbirth());
            authorize1.setIdentificationtypeselect(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_identificationtypeselect());
            authorize1.setIdentificationtype(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_identificationtype());
            authorize1.setState(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_State());
            authorize1.setAddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_address());
            authorize1.setCity(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_city());
            authorize1.setZipcode(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_zip());
            authorize1.setCountry(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_country());
            authorize1.setStreet(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_street());
            authorize1.setNationality(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_nationality());
            authorize1.setPassportexpirydate(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_Passportexpirydate());
            authorize1.setPassportissuedate(ownershipProfileRequestVO.getAuthorizedsignatoryprofilePassportissuedate());
            authorize1.setPoliticallyexposed(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_politicallyexposed());
            authorize1.setCriminalrecord(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_criminalrecord());
            authorize1.setAddressProof(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_addressproof());
            authorize1.setAddressId(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_addressId());
            authorize1.setLastname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile_lastname());
            authorize1.setDesignation(ownershipProfileRequestVO.getAuthorizedsignatoryprofile1_designation());
        }
        return authorize1;
    }
    private OwnershipProfileDetailsVO getauthorize2(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO authorize2 = null;

        if (ownershipProfileRequestVO != null)
        {
            authorize2 = new OwnershipProfileDetailsVO();

            authorize2.setFirstname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2());
            authorize2.setTitle(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_title());
            authorize2.setOwned(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_owned());
            authorize2.setTelnocc1(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_telnocc1());
            authorize2.setTelephonenumber(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_telephonenumber());
            authorize2.setEmailaddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_emailaddress());
            authorize2.setDateofbirth(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_dateofbirth());
            authorize2.setIdentificationtypeselect(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_identificationtypeselect());
            authorize2.setIdentificationtype(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_identificationtype());
            authorize2.setState(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_State());
            authorize2.setAddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_address());
            authorize2.setCity(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_city());
            authorize2.setZipcode(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_zip());
            authorize2.setCountry(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_country());
            authorize2.setStreet(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_street());
            authorize2.setNationality(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_nationality());
            authorize2.setPassportexpirydate(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_Passportexpirydate());
            authorize2.setPassportissuedate(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2Passportissuedate());
            authorize2.setPoliticallyexposed(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_politicallyexposed());
            authorize2.setCriminalrecord(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_criminalrecord());
            authorize2.setAddressProof(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_addressproof());
            authorize2.setAddressId(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_addressId());
            authorize2.setLastname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_lastname());
            authorize2.setDesignation(ownershipProfileRequestVO.getAuthorizedsignatoryprofile2_designation());
        }
        return authorize2;
    }
    private OwnershipProfileDetailsVO getauthorize3(OwnershipProfileRequestVO ownershipProfileRequestVO)
    {
        OwnershipProfileDetailsVO authorize3 = null;

        if (ownershipProfileRequestVO != null)
        {

            authorize3 = new OwnershipProfileDetailsVO();

            authorize3.setFirstname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3());
            authorize3.setTitle(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_title());
            authorize3.setOwned(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_owned());
            authorize3.setTelnocc1(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_telnocc1());
            authorize3.setTelephonenumber(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_telephonenumber());
            authorize3.setEmailaddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_emailaddress());
            authorize3.setDateofbirth(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_dateofbirth());
            authorize3.setIdentificationtype(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_identificationtype());
            authorize3.setIdentificationtypeselect(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_identificationtypeselect());
            authorize3.setState(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_State());
            authorize3.setAddress(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_address());
            authorize3.setCity(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_city());
            authorize3.setZipcode(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_zip());
            authorize3.setCountry(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_country());
            authorize3.setStreet(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_street());
            authorize3.setNationality(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_nationality());
            authorize3.setPassportexpirydate(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_Passportexpirydate());
            authorize3.setPassportissuedate(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3Passportissuedate());
            authorize3.setPoliticallyexposed(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_politicallyexposed());
            authorize3.setCriminalrecord(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_criminalrecord());
            authorize3.setAddressProof(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_addressproof());
            authorize3.setAddressId(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_addressId());
            authorize3.setLastname(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_lastname());
            authorize3.setDesignation(ownershipProfileRequestVO.getAuthorizedsignatoryprofile3_designation());

        }
        return authorize3;
    }

    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();

        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);

        errorCodeListVO.setApiCode(errorCodeVO.getApiCode());
        errorCodeListVO.setApiDescription(errorCodeVO.getApiDescription());
        return errorCodeListVO;
    }
}