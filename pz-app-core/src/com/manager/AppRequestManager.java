package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.enums.AppFileActionType;
import com.enums.ApplicationManagerTypes;
import com.enums.Module;
import com.manager.vo.PartnerDetailsVO;
import com.utils.AppFunctionUtil;
import com.validators.AppManagerInputValidator;
import com.validators.BankInputName;
import com.vo.applicationManagerVOs.*;
import com.vo.requestVOs.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

//import com.manager.utils.CommonFunctionUtil;


/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 1/21/15
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppRequestManager
{
    private static Logger logger = new Logger(AppRequestManager.class.getName());
    private static Functions functions = new Functions();
    private static AppFunctionUtil commonFunctionUtil= new AppFunctionUtil();
    //manager instance
    private static ApplicationManager applicationManager = new ApplicationManager();
    private AppManagerInputValidator inputValidator= new AppManagerInputValidator();

    //setting applicationManagerVO  from session
    public ApplicationManagerVO getApplicationManagerVO(HttpSession session)
    {
        ApplicationManagerVO applicationManagerVO = null;
        if(session!=null && session.getAttribute("applicationManagerVO")!=null)
        {
            //getting applicationManagerVO from session
            applicationManagerVO= (ApplicationManagerVO) session.getAttribute("applicationManagerVO");
            //null check for each VO inside applicationManagerVO
            if(applicationManagerVO.getCompanyProfileVO()==null)
            {
                applicationManagerVO.setCompanyProfileVO(new CompanyProfileVO());
            }
            if(applicationManagerVO.getOwnershipProfileVO()==null)
            {
                applicationManagerVO.setOwnershipProfileVO(new OwnershipProfileVO());
            }
            if(applicationManagerVO.getBusinessProfileVO()==null)
            {
                applicationManagerVO.setBusinessProfileVO(new BusinessProfileVO());
            }
            if(applicationManagerVO.getBankProfileVO()==null)
            {
                applicationManagerVO.setBankProfileVO(new BankProfileVO());
            }
            if(applicationManagerVO.getCardholderProfileVO()==null)
            {
                applicationManagerVO.setCardholderProfileVO(new CardholderProfileVO());
            }
            if(applicationManagerVO.getExtradetailsprofileVO()==null)
            {
                applicationManagerVO.setExtradetailsprofileVO(new ExtraDetailsProfileVO());
            }
            if(applicationManagerVO.getUploadLabelVOs()==null)
            {
                applicationManagerVO.setUploadLabelVOs(new HashMap<String, AppUploadLabelVO>());
            }
            if(applicationManagerVO.getFileDetailsVOs()==null)
            {
                applicationManagerVO.setFileDetailsVOs(new HashMap<String, FileDetailsListVO>());         //Changes for Multiple KYC
            }
        }
        else
        {
            applicationManagerVO= new ApplicationManagerVO();
            applicationManagerVO.setCompanyProfileVO(new CompanyProfileVO());
            applicationManagerVO.setOwnershipProfileVO(new OwnershipProfileVO());
            applicationManagerVO.setBusinessProfileVO(new BusinessProfileVO());
            applicationManagerVO.setBankProfileVO(new BankProfileVO());
            applicationManagerVO.setCardholderProfileVO(new CardholderProfileVO());
            applicationManagerVO.setExtradetailsprofileVO(new ExtraDetailsProfileVO());
            applicationManagerVO.setUploadLabelVOs(new HashMap<String, AppUploadLabelVO>() );
            applicationManagerVO.setFileDetailsVOs(new HashMap<String, FileDetailsListVO>());         //Changes for Multiple KYC
        }
        //remove from the session
        applicationManagerVO.setNotificationMessage(null);
        applicationManagerVO.setMessageColorClass(null);
        applicationManagerVO.setSubmittedFileDetailsVO(null);

        return applicationManagerVO;
    }

    public ApplicationManagerVO readRequestForMerchantLoginAPPJSON(ApplicationManagerAuthentication request)
    {


        Authentication authentication = new Authentication();
        ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
        applicationManagerVO = populateAuthenticationDetails(applicationManagerVO, request);

        Merchant merchantVO = new Merchant();

        authentication.setPartnerId(request.getAuthentication().getPartnerId());
        merchantVO.setLoginName(request.getMerchantVO().getLoginName());
        merchantVO.setNewPassword(request.getMerchantVO().getNewPassword());



        applicationManagerVO.setMerchant(merchantVO);
        applicationManagerVO.setAuthentication(authentication);

        return applicationManagerVO;
    }

    //readrequest
    public ApplicationManagerVO readRequestForMerchantSignUp(ApplicationManagerAuthenticationRequest request)
    {
        //System.out.println("partnerid----"+request.getAuthentication().getPartnerId());


        MerchantVO merchantVO = new MerchantVO();
        AuthenticationVO authentication = new AuthenticationVO();
        ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
        applicationManagerVO = populateAuthenticationDetailsURL(applicationManagerVO, request);




        //Authentication Details

        //  merchantDetailsVO.setPartnerId(request.getAuthentication().getPartnerId());

        authentication.setPartnerId(request.getAuthentication().getPartnerId());
        //System.out.println("partnerid----"+request.getAuthentication().getPartnerId());

        merchantVO.setLoginName(request.getMerchant().getLoginName());
        //System.out.println("login name----"+request.getMerchant().getLoginName());


        merchantVO.setNewPassword(request.getMerchant().getNewPassword());

        merchantVO.setConPassword(request.getMerchant().getConPassword());
        merchantVO.setCompanyName(request.getMerchant().getCompanyName());
        merchantVO.setWebsite(request.getMerchant().getWebsite());
        merchantVO.setGivenName(request.getMerchant().getGivenName());
        merchantVO.setEmail(request.getMerchant().getEmail());
        merchantVO.setPhone(request.getMerchant().getPhone());
        merchantVO.setTelcc(request.getMerchant().getTelcc());
        merchantVO.setCountry(request.getMerchant().getCountry());

        applicationManagerVO.setMerchantVO(merchantVO);
        applicationManagerVO.setAuthenticationVO(authentication);


        return applicationManagerVO;
    }
    public ApplicationManagerVO readRequestForMerchantSignUpJSON(ApplicationManagerAuthentication request)
    {
        //System.out.println("partnerid----"+request.getAuthentication().getPartnerId());

        Authentication authentication = new Authentication();
        ApplicationManagerVO applicationManagerVO = new ApplicationManagerVO();
        applicationManagerVO = populateAuthenticationDetails(applicationManagerVO, request);

        Merchant merchantVO = new Merchant();

        authentication.setPartnerId(request.getAuthentication().getPartnerId());
        //System.out.println("partnerid----"+request.getAuthentication().getPartnerId());

        //System.out.println("login name----"+request.getMerchantVO().getLoginName());
        merchantVO.setLoginName(request.getMerchantVO().getLoginName());


        merchantVO.setNewPassword(request.getMerchantVO().getNewPassword());

        merchantVO.setConPassword(request.getMerchantVO().getConPassword());
        merchantVO.setCompanyName(request.getMerchantVO().getCompanyName());
        merchantVO.setWebsite(request.getMerchantVO().getWebsite());
        merchantVO.setGivenName(request.getMerchantVO().getGivenName());
        merchantVO.setEmail(request.getMerchantVO().getEmail());
        merchantVO.setPhone(request.getMerchantVO().getPhone());
        merchantVO.setTelcc(request.getMerchantVO().getTelcc());
        merchantVO.setCountry(request.getMerchantVO().getCountry());
        merchantVO.setContactName(request.getMerchantVO().getContactName());
        merchantVO.setPostcode(request.getMerchantVO().getPostcode());

        applicationManagerVO.setMerchant(merchantVO);
        applicationManagerVO.setAuthentication(authentication);


        return applicationManagerVO;
    }
    private ApplicationManagerVO populateAuthenticationDetails(ApplicationManagerVO applicationManagerVO,ApplicationManagerAuthentication merchantServiceRequestVO)
    {

        Authentication authenticationVO = new Authentication();

        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getMemberId()))
            authenticationVO.setMemberId(merchantServiceRequestVO.getAuthentication().getMemberId());

        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getPartnerId()))
            authenticationVO.setPartnerId(merchantServiceRequestVO.getAuthentication().getPartnerId());
        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getChecksum()))
            authenticationVO.setChecksum(merchantServiceRequestVO.getAuthentication().getChecksum());


        applicationManagerVO.setAuthentication(authenticationVO);


        return applicationManagerVO;
    }


    //for URL
    private ApplicationManagerVO populateAuthenticationDetailsURL(ApplicationManagerVO applicationManagerVO,ApplicationManagerAuthenticationRequest merchantServiceRequestVO)
    {

        AuthenticationVO authenticationVO = new AuthenticationVO();

        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        GenericTransDetailsVO transDetailsVO = new GenericTransDetailsVO();

        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getMemberId()))
            authenticationVO.setMemberId(merchantServiceRequestVO.getAuthentication().getMemberId());

        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getPartnerId()))
            authenticationVO.setPartnerId(merchantServiceRequestVO.getAuthentication().getPartnerId());
        if(functions.isValueNull(merchantServiceRequestVO.getAuthentication().getChecksum()))
            authenticationVO.setChecksum(merchantServiceRequestVO.getAuthentication().getChecksum());


        applicationManagerVO.setAuthenticationVO(authenticationVO);


        return applicationManagerVO;
    }

    public void getApplicationSavedStatus(ApplicationManagerVO applicationManagerVO, ApplicationManagerVO requestApplicationManagerVO)
    {
        requestApplicationManagerVO.setApplicationSaved(applicationManagerVO.getApplicationSaved());
        requestApplicationManagerVO.getBankProfileVO().setBankProfileSaved(applicationManagerVO.getBankProfileVO().getBankProfileSaved());
        requestApplicationManagerVO.getBusinessProfileVO().setBusinessProfileSaved(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved());
        requestApplicationManagerVO.getCardholderProfileVO().setCardHolderProfileSaved(applicationManagerVO.getCardholderProfileVO().getCardHolderProfileSaved());
        requestApplicationManagerVO.getCompanyProfileVO().setCompanyProfileSaved(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved());
        requestApplicationManagerVO.getExtradetailsprofileVO().setExtraDetailsProfileSaved(applicationManagerVO.getExtradetailsprofileVO().getExtraDetailsProfileSaved());
        requestApplicationManagerVO.getOwnershipProfileVO().setOwnerShipProfileSaved(applicationManagerVO.getOwnershipProfileVO().getOwnerShipProfileSaved());

        //removing all the database values from applicationManagerVO
        applicationManagerVO.setBankProfileVO(null);
        applicationManagerVO.setBusinessProfileVO(null);
        applicationManagerVO.setCardholderProfileVO(null);
        applicationManagerVO.setCompanyProfileVO(null);
        applicationManagerVO.setExtradetailsprofileVO(null);
        applicationManagerVO.setOwnershipProfileVO(null);

        //setting all the profile status in applicationManagerVO again
        applicationManagerVO.setApplicationSaved(requestApplicationManagerVO.getApplicationSaved());
        applicationManagerVO.setBankProfileVO(requestApplicationManagerVO.getBankProfileVO());
        applicationManagerVO.setBusinessProfileVO(requestApplicationManagerVO.getBusinessProfileVO());
        applicationManagerVO.setCardholderProfileVO(requestApplicationManagerVO.getCardholderProfileVO());
        applicationManagerVO.setCompanyProfileVO( requestApplicationManagerVO.getCompanyProfileVO());
        applicationManagerVO.setExtradetailsprofileVO(requestApplicationManagerVO.getExtradetailsprofileVO());
        applicationManagerVO.setOwnershipProfileVO(requestApplicationManagerVO.getOwnershipProfileVO());
    }
    //
    public ApplicationManagerVO getApplicationManagerVO(ApplicationManagerVO applicationManagerVO)
    {
        if(applicationManagerVO!=null)
        {
            //null check for each VO inside applicationManagerVO
            if(applicationManagerVO.getCompanyProfileVO()==null)
            {
                applicationManagerVO.setCompanyProfileVO(new CompanyProfileVO());
            }
            if(applicationManagerVO.getOwnershipProfileVO()==null)
            {
                applicationManagerVO.setOwnershipProfileVO(new OwnershipProfileVO());
            }
            if(applicationManagerVO.getBusinessProfileVO()==null)
            {
                applicationManagerVO.setBusinessProfileVO(new BusinessProfileVO());
            }
            if(applicationManagerVO.getBankProfileVO()==null)
            {
                applicationManagerVO.setBankProfileVO(new BankProfileVO());
            }
            if(applicationManagerVO.getCardholderProfileVO()==null)
            {
                applicationManagerVO.setCardholderProfileVO(new CardholderProfileVO());
            }
            if(applicationManagerVO.getExtradetailsprofileVO()==null)
            {
                applicationManagerVO.setExtradetailsprofileVO(new ExtraDetailsProfileVO());
            }
            if(applicationManagerVO.getUploadLabelVOs()==null)
            {
                applicationManagerVO.setUploadLabelVOs(new HashMap<String, AppUploadLabelVO>());
            }
            if(applicationManagerVO.getFileDetailsVOs()==null)
            {
                applicationManagerVO.setFileDetailsVOs(new HashMap<String, FileDetailsListVO>());         //Changes for Multiple KYC
            }
        }
        else
        {
            applicationManagerVO= new ApplicationManagerVO();
            applicationManagerVO.setCompanyProfileVO(new CompanyProfileVO());
            applicationManagerVO.setOwnershipProfileVO(new OwnershipProfileVO());
            applicationManagerVO.setBusinessProfileVO(new BusinessProfileVO());
            applicationManagerVO.setBankProfileVO(new BankProfileVO());
            applicationManagerVO.setCardholderProfileVO(new CardholderProfileVO());
            applicationManagerVO.setExtradetailsprofileVO(new ExtraDetailsProfileVO());
            applicationManagerVO.setUploadLabelVOs(new HashMap<String, AppUploadLabelVO>() );
            applicationManagerVO.setFileDetailsVOs(new HashMap<String, FileDetailsListVO>());         //Changes for Multiple KYC
        }
        //remove from the session
        applicationManagerVO.setNotificationMessage(null);
        applicationManagerVO.setMessageColorClass(null);
        applicationManagerVO.setSubmittedFileDetailsVO(null);

        return applicationManagerVO;
    }

    //getting NavigationVo
    public NavigationVO getNavigationVO(HttpSession session,Module module)
    {
        NavigationVO navigationVO = null;
        if(session!=null && session.getAttribute("navigationVO")!=null)
        {
            navigationVO= (NavigationVO) session.getAttribute("navigationVO");
            //this is put since session is also call by reference
            navigationVO.setNextPageNO(0);
            navigationVO.setPreviousPageNO(0);
            navigationVO.setCurrentPageNO(0);

        }
        else
        {
            navigationVO=new NavigationVO();
            navigationVO.addStepAndPageName("companyprofile.jsp");
            navigationVO.addStepAndPageName("ownershipprofile.jsp");
            navigationVO.addStepAndPageName("businessprofile.jsp");
            navigationVO.addStepAndPageName("bankapplication.jsp");
            navigationVO.addStepAndPageName("cardholderprofile.jsp");
            if(module!=null && (module.name().equals(Module.ADMIN.name()) || module.name().equals(Module.PARTNER.name())))
                navigationVO.addStepAndPageName("extradetailsprofile.jsp");
            navigationVO.addStepAndPageName("upload.jsp");
        }
        return navigationVO;
    }

    //getting AppValidationVO
    public AppValidationVO getAppValidationVO(HttpSession session)
    {
        AppValidationVO appValidationVO = null;
        if(session!=null && session.getAttribute("appValidationVO")!=null)
        {
            appValidationVO= (AppValidationVO) session.getAttribute("appValidationVO");
        }

        return appValidationVO;
    }


    //validation for interface as well as API
    public ValidationErrorList validationForInterfaceOrApi(HttpServletRequest request,ApplicationManagerVO requestApplicationManagerVO,NavigationVO navigationVO,ApplicationManagerVO applicationManagerVO, boolean isAPI,boolean isKYCAPI,AppValidationVO appValidationVO)
    {

        ValidationErrorList validationErrorList = new ValidationErrorList();

        try
        {
            if (isAPI)
            {

                if (isKYCAPI)
                {
                    //This is for KYC validation only
                    navigationVO.setUploadHit(true);
                    validateCurrentPage(null, requestApplicationManagerVO, navigationVO, applicationManagerVO, validationErrorList, isAPI, isKYCAPI, appValidationVO);
                    navigationVO.setUploadHit(false);
                }
                else
                {
                    //This is for maf validation only
                    navigationVO.getStepAndPageName().remove(6);
                    for (Map.Entry<Integer, String> stepNoAndNamePair : navigationVO.getStepAndPageName().entrySet())
                    {
                        navigationVO.setCurrentPageNO(stepNoAndNamePair.getKey());
                        navigationVO.setConditionalValidation(false);//mandatory check

                        validateCurrentPage(null, requestApplicationManagerVO, navigationVO, applicationManagerVO, validationErrorList, isAPI, isKYCAPI, appValidationVO);
                        navigationVO.setUploadHit(false);
                    }
                }

            }
            else
            {
                logger.debug("Inside AppRequestManager.validationForInterfaceOrApi");
                validateCurrentPage(request, null, navigationVO, applicationManagerVO, validationErrorList, isAPI, isKYCAPI, appValidationVO);
            }


        }
        catch (Exception e)
        {
            logger.error("Exception---->", e);
        }
        return validationErrorList;
    }


        /**
         * retaining the value of each profile from request and database;
         * @param requestApplicationManagerVOAfterValidation
         * @param applicationManagerVO
         * @param navigationVO
         * @param validationErrorList
         */

    public void setApplicationDetailsAfterValidation(ApplicationManagerVO requestApplicationManagerVOAfterValidation,ApplicationManagerVO applicationManagerVO,NavigationVO navigationVO,ValidationErrorList validationErrorList)
    {
        if(navigationVO.getStepAndPageName().containsKey(6))
            navigationVO.getStepAndPageName().remove(6);
        for(Map.Entry<Integer,String> stepNoAndNamePair:navigationVO.getStepAndPageName().entrySet())
        {
            navigationVO.setCurrentPageNO(stepNoAndNamePair.getKey());
            retainCurrentPage(requestApplicationManagerVOAfterValidation,applicationManagerVO,navigationVO,validationErrorList);
        }
    }
// changes from private to public

    public void retainCurrentPage(ApplicationManagerVO requestApplicationManagerVOAfterValidation,ApplicationManagerVO applicationManagerVO,NavigationVO navigationVO,ValidationErrorList validationErrorList)
    {
        if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "companyprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside company profile Retain");

            setCompanyProfileVO(requestApplicationManagerVOAfterValidation.getCompanyProfileVO(), applicationManagerVO.getCompanyProfileVO(),validationErrorList);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "ownershipprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside  ownership profile Retain");

            setOwnershipProfileVO(requestApplicationManagerVOAfterValidation.getOwnershipProfileVO(), applicationManagerVO.getOwnershipProfileVO(),validationErrorList);
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "businessprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside business profile Retain");

            setBusinessProfileVO(requestApplicationManagerVOAfterValidation.getBusinessProfileVO(), applicationManagerVO.getBusinessProfileVO(),validationErrorList);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "bankapplication.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside bankapplication Retain");

            setBankProfileVO(requestApplicationManagerVOAfterValidation.getBankProfileVO(), applicationManagerVO.getBankProfileVO(),validationErrorList);
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "cardholderprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside cardholderprofile Retain");

            setCardHolderProfileVO(requestApplicationManagerVOAfterValidation.getCardholderProfileVO(),applicationManagerVO.getCardholderProfileVO(),validationErrorList);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "extradetailsprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())) && (Module.ADMIN.name().equals(applicationManagerVO.getUser()) || Module.PARTNER.name().equals(applicationManagerVO.getUser())))
        {
            logger.debug("inside extradetailsprofile Retain");

            setExtraDetailsProfileVO(requestApplicationManagerVOAfterValidation.getExtradetailsprofileVO(), applicationManagerVO.getExtradetailsprofileVO(),validationErrorList);
        }
    }


    //doing validation and session inserting the text field from particular page on click of next button
    private void validateCurrentPage(HttpServletRequest request,ApplicationManagerVO requestApplicationManagerVO, NavigationVO navigationVO,ApplicationManagerVO applicationManagerVO,ValidationErrorList validationErrorList,boolean isAPI,boolean isKycAPI,AppValidationVO appValidationVO)
    {

        Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
        Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();


        applicationManager.setValidationForMember(applicationManagerVO.getMemberId(),navigationVO,fullValidationForStep,dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

        Map<Boolean,Set<BankInputName>> bankInputBooleanSetMap=null;

        if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "companyprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside company profile");
            if(isAPI)
            {
                setCompanyProfileVO(requestApplicationManagerVO.getCompanyProfileVO(),applicationManagerVO.getCompanyProfileVO());
            }
            else
            {
                setCompanyProfileVO(request, applicationManagerVO.getCompanyProfileVO());
            }

            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());

            //logger.debug("COMPANY DIRECT CASES CHECK::::"+bankInputBooleanSetMap);
            //logger.debug("COMPANY DEPENDENCY CASES::::"+dependencyPageViseValidation);
            //logger.debug("OPTIONAL COMPANY CASES::::"+otherValidationPageVise);
            //logger.debug("OPTIONAL COMPANY DEPENDENCY CASES::::"+dependencyOtherPageViseValidation);

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getCompanyProfileVO(), bankInputBooleanSetMapBooleanSetEntry.getValue(), validationErrorList, navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(),navigationVO.isConditionalValidation(), dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getCompanyProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "ownershipprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside  ownershipprofile");
            if(isAPI)
            {
                setOwnershipProfileVO(requestApplicationManagerVO.getOwnershipProfileVO(), applicationManagerVO.getOwnershipProfileVO());
            }
            else
            {
                setOwnershipProfileVO(request, applicationManagerVO.getOwnershipProfileVO());
            }

            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());

            //logger.debug("OWNER DIRECT CASES CHECK::::"+bankInputBooleanSetMap);
            //logger.debug("OWNER DEPENDENCY CASES::::"+dependencyPageViseValidation);
            //logger.debug("OWNER COMPANY CASES::::"+otherValidationPageVise);
            //logger.debug("OWNER COMPANY DEPENDENCY CASES::::"+dependencyOtherPageViseValidation);

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getOwnershipProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue() ,validationErrorList,navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(),navigationVO.isConditionalValidation(),dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getOwnershipProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "businessprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside businessprofile");

            if(isAPI)
            {
                setBusinessProfileVO(requestApplicationManagerVO.getBusinessProfileVO(), applicationManagerVO.getBusinessProfileVO());
            }
            else
            {
                setBusinessProfileVO(request, applicationManagerVO.getBusinessProfileVO());
            }

            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());

            //logger.debug("BUSINESS DIRECT CASES CHECK::::"+bankInputBooleanSetMap);
            //logger.debug("BUSINESS DEPENDENCY CASES::::"+dependencyPageViseValidation);
            //logger.debug("BUSINESS COMPANY CASES::::"+otherValidationPageVise);
            //logger.debug("BUSINESS COMPANY DEPENDENCY CASES::::"+dependencyOtherPageViseValidation);

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getBusinessProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue() ,validationErrorList,navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(),navigationVO.isConditionalValidation(),dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getBusinessProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "bankapplication.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside bankapplication");
            if(isAPI)
            {
                setBankProfileVO(requestApplicationManagerVO.getBankProfileVO(),applicationManagerVO.getBankProfileVO());
            }
            else
            {
                setBankProfileVO(request, applicationManagerVO.getBankProfileVO());
            }

            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());
            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getBankProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(), validationErrorList,navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(),navigationVO.isConditionalValidation(),dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getBankProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "cardholderprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            logger.debug("inside cardholderprofile");
            if(isAPI)
            {
                setCardHolderProfileVO(requestApplicationManagerVO.getCardholderProfileVO(),applicationManagerVO.getCardholderProfileVO());
            }
            else
            {
                setCardHolderProfileVO(request,applicationManagerVO.getCardholderProfileVO());
            }
            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());
            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getCardholderProfileVO(), bankInputBooleanSetMapBooleanSetEntry.getValue(), validationErrorList, navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(), navigationVO.isConditionalValidation(), dependencyPageViseValidation);
            }
            inputValidator.InputValidations(applicationManagerVO.getCardholderProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "extradetailsprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())) && (Module.ADMIN.name().equals(applicationManagerVO.getUser()) || Module.PARTNER.name().equals(applicationManagerVO.getUser())))
        {
            logger.debug("inside extradetailsprofile");
            if(isAPI)
            {
                setExtraDetailsProfileVO(requestApplicationManagerVO.getExtradetailsprofileVO(), applicationManagerVO.getExtradetailsprofileVO());
            }
            else
            {
                setExtraDetailsProfileVO(request, applicationManagerVO.getExtradetailsprofileVO());
            }

            bankInputBooleanSetMap=fullValidationForStep.get(navigationVO.getCurrentPageNO());

            //logger.debug("EXTRADETAILS DIRECT CASES CHECK::::"+bankInputBooleanSetMap);
            //logger.debug("EXTRADETAILS DEPENDENCY CASES::::"+dependencyPageViseValidation);
            //logger.debug("EXTRADETAILS COMPANY CASES::::"+otherValidationPageVise);
            //logger.debug("EXTRADETAILS COMPANY DEPENDENCY CASES::::"+dependencyOtherPageViseValidation);

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getExtradetailsprofileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(), validationErrorList,navigationVO.isConditionalValidation() || bankInputBooleanSetMapBooleanSetEntry.getKey(),navigationVO.isConditionalValidation(),dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getExtradetailsprofileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

        }
        else if((navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "upload.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))|| (isAPI && isKycAPI))
        {
            if(navigationVO.isUploadHit())
            {
                logger.debug("Inside isUploadHit....");
                validateAppFilesUploaded(applicationManagerVO, validationErrorList, (isAPI ? false : true), String.valueOf(navigationVO.getCurrentPageNO()),isAPI);
            }
        }
        else
        {
            //this is for submit
            logger.debug("inside else/all condition submit:::");

            convertAllTimeStampToDatepicker(applicationManagerVO);

            bankInputBooleanSetMap=fullValidationForStep.get(1);
            if(dependencyFullValidationForStep.containsKey(1))
            {
                dependencyPageViseValidation=dependencyFullValidationForStep.get(1);
            }

            if(dependencyOtherFullValidationForStep.containsKey(1))
            {
                dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(1);
            }

            if(otherValidation.containsKey(1))
            {
                otherValidationPageVise=otherValidation.get(1);
            }

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getCompanyProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getCompanyProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);


            bankInputBooleanSetMap=fullValidationForStep.get(2);

            if(dependencyFullValidationForStep.containsKey(2))
            {
                dependencyPageViseValidation=dependencyFullValidationForStep.get(2);
            }

            if(dependencyOtherFullValidationForStep.containsKey(2))
            {
                dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(2);
            }

            if(otherValidation.containsKey(2))
            {
                otherValidationPageVise=otherValidation.get(2);
            }

            logger.debug("bankInputBooleanSetMap.entrySet()----->"+bankInputBooleanSetMap.entrySet());
            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getOwnershipProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
            }
            inputValidator.InputValidations(applicationManagerVO.getOwnershipProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

            bankInputBooleanSetMap=fullValidationForStep.get(3);

            if(dependencyFullValidationForStep.containsKey(3))
            {
                dependencyPageViseValidation=dependencyFullValidationForStep.get(3);
            }

            if(dependencyOtherFullValidationForStep.containsKey(3))
            {
                dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(3);
            }

            if(otherValidation.containsKey(3))
            {
                otherValidationPageVise=otherValidation.get(3);
            }

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getBusinessProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getBusinessProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

            bankInputBooleanSetMap=fullValidationForStep.get(4);

            if(dependencyFullValidationForStep.containsKey(4))
            {
                dependencyPageViseValidation=dependencyFullValidationForStep.get(4);
            }

            if(dependencyOtherFullValidationForStep.containsKey(4))
            {
                dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(4);
            }

            if(otherValidation.containsKey(4))
            {
                otherValidationPageVise=otherValidation.get(4);
            }

            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getBankProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getBankProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

            bankInputBooleanSetMap=fullValidationForStep.get(5);

            if(dependencyFullValidationForStep.containsKey(5))
            {
                dependencyPageViseValidation=dependencyFullValidationForStep.get(5);
            }

            if(dependencyOtherFullValidationForStep.containsKey(5))
            {
                dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(5);
            }

            if(otherValidation.containsKey(5))
            {
                otherValidationPageVise=otherValidation.get(5);
            }

            //logger.debug("bankInputBooleanSetMap:::"+bankInputBooleanSetMap);
            //logger.debug("dependencyPageViseValidation:::"+dependencyPageViseValidation);
            //logger.debug("otherValidationPageVise:::"+otherValidationPageVise);
            //logger.debug("dependencyOtherPageViseValidation:::"+dependencyOtherPageViseValidation);
            for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
            {
                inputValidator.InputValidations(applicationManagerVO.getCardholderProfileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
            }

            inputValidator.InputValidations(applicationManagerVO.getCardholderProfileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);

            String pageNumber="6";

            if(Module.ADMIN.name().equals(applicationManagerVO.getUser()) || Module.PARTNER.name().equals(applicationManagerVO.getUser()))
            {
                pageNumber="7";

                bankInputBooleanSetMap=fullValidationForStep.get(6);

                if(dependencyFullValidationForStep.containsKey(6))
                {
                    dependencyPageViseValidation=dependencyFullValidationForStep.get(6);
                }

                if(dependencyOtherFullValidationForStep.containsKey(6))
                {
                    dependencyOtherPageViseValidation=dependencyOtherFullValidationForStep.get(6);
                }

                if(otherValidation.containsKey(6))
                {
                    otherValidationPageVise=otherValidation.get(6);
                }

                for(Map.Entry<Boolean,Set<BankInputName>> bankInputBooleanSetMapBooleanSetEntry:bankInputBooleanSetMap.entrySet())
                {
                    inputValidator.InputValidations(applicationManagerVO.getExtradetailsprofileVO(),bankInputBooleanSetMapBooleanSetEntry.getValue(),validationErrorList,false || bankInputBooleanSetMapBooleanSetEntry.getKey(),false,dependencyPageViseValidation);
                }

                inputValidator.InputValidations(applicationManagerVO.getExtradetailsprofileVO(),otherValidationPageVise,validationErrorList,true,true,dependencyOtherPageViseValidation);


            }
            logger.debug("validateAppFilesUploaded before setConditionalValidation false ....");
            validateAppFilesUploaded(applicationManagerVO, validationErrorList,false,pageNumber,isAPI);   // KYC optional for agnipay sagar chanages
            navigationVO.setConditionalValidation(false);
        }
    }

    public ValidationErrorList validateSpeed1Request(HttpServletRequest request,ApplicationManagerVO requestApplicationManagerVO,ApplicationManagerVO applicationManagerVO,boolean isOptional,boolean isApi)
    {
        ValidationErrorList validationErrorList = new ValidationErrorList();
        logger.debug("inside SpeedOption");
        if(isApi)
        {
            setSpeedProcessRequest(requestApplicationManagerVO,applicationManagerVO);
        }
        else
            setSpeedProcessRequest(request, applicationManagerVO);
        inputValidator.InputValidations(applicationManagerVO,validationErrorList,isOptional);
        return  validationErrorList;
    }

    //getting validationErrorList
    public ValidationErrorList compareValidationErrorList(HttpServletRequest request,ValidationErrorList sessionValidationErrorList,ValidationErrorList requestValidationErrorList)
    {
        ValidationErrorList validationErrorList = null;
        List<String> solvedContextName = new ArrayList<String>();
        if (!sessionValidationErrorList.isEmpty())
        {
            validationErrorList = new ValidationErrorList();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements())
            {
                String parameter = parameterNames.nextElement();
                if (functions.isValueNull(request.getParameter(parameter)))
                {
                    if (requestValidationErrorList.getError(parameter) == null && sessionValidationErrorList.getError(parameter) != null)
                    {
                        solvedContextName.add(parameter);
                    }
                }
                if (parameter.equals("is_website_live") && "N".equals(request.getParameter("is_website_live")))
                {
                    if (requestValidationErrorList.getError("test_link") == null && sessionValidationErrorList.getError("test_link") != null)
                    {
                        solvedContextName.add("test_link");
                    }
                }
                //company profile special condition
                if (parameter.equals("company_registered_eu") && "N".equals(request.getParameter("company_registered_eu")))
                {

                    if (requestValidationErrorList.getError("registered_corporatename") == null && sessionValidationErrorList.getError("registered_corporatename") != null)
                    {
                        solvedContextName.add("registered_corporatename");
                    }
                    if (requestValidationErrorList.getError("registered_directors") == null && sessionValidationErrorList.getError("registered_directors") != null)
                    {
                        solvedContextName.add("registered_directors");
                    }
                    if (requestValidationErrorList.getError("registered_directors_addressproof") == null && sessionValidationErrorList.getError("registered_directors_addressproof") != null)
                    {
                        solvedContextName.add("registered_directors_addressproof");
                    }
                    if (requestValidationErrorList.getError("registered_directors_addressId") == null && sessionValidationErrorList.getError("registered_directors_addressId") != null)
                    {
                        solvedContextName.add("registered_directors_addressId");
                    }
                    if (requestValidationErrorList.getError("registered_directors_country") == null && sessionValidationErrorList.getError("registered_directors_country") != null)
                    {
                        solvedContextName.add("registered_directors_country");
                    }
                    if (requestValidationErrorList.getError("registered_directors_address") == null && sessionValidationErrorList.getError("registered_directors_address") != null)
                    {
                        solvedContextName.add("registered_directors_address");
                    }
                    if (requestValidationErrorList.getError("registered_directors_city") == null && sessionValidationErrorList.getError("registered_directors_city") != null)
                    {
                        solvedContextName.add("registered_directors_city");
                    }
                    if (requestValidationErrorList.getError("registered_directors_State") == null && sessionValidationErrorList.getError("registered_directors_State") != null)
                    {
                        solvedContextName.add("registered_directors_State");
                    }
                    if (requestValidationErrorList.getError("registered_directors_postalcode") == null && sessionValidationErrorList.getError("registered_directors_postalcode") != null)
                    {
                        solvedContextName.add("registered_directors_postalcode");
                    }
                }
                if (parameter.equals("company_bankruptcy") && "N".equals(request.getParameter("company_bankruptcy")))
                {

                    if (requestValidationErrorList.getError("company_bankruptcydate") == null && sessionValidationErrorList.getError("company_bankruptcydate") != null)
                        if (requestValidationErrorList.getError("company_bankruptcydate") == null && sessionValidationErrorList.getError("company_bankruptcydate") != null)
                        {
                            solvedContextName.add("company_bankruptcydate");
                        }

                }
                //ownership profile special condition
                if (parameter.equals("nameprincipal1_owned"))
                {
                    if (requestValidationErrorList.getError("nameprincipal2") == null && sessionValidationErrorList.getError("nameprincipal2") != null)
                    {
                        solvedContextName.add("nameprincipal2");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_lastname") == null && sessionValidationErrorList.getError("nameprincipal2_lastname") != null)
                    {
                        solvedContextName.add("nameprincipal2_lastname");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_title") == null && sessionValidationErrorList.getError("nameprincipal2_title") != null)
                    {
                        solvedContextName.add("nameprincipal2_title");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_owned") == null && sessionValidationErrorList.getError("nameprincipal2_owned") != null)
                    {
                        solvedContextName.add("nameprincipal2_owned");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_identificationtypeselect") == null && sessionValidationErrorList.getError("nameprincipal2_identificationtypeselect") != null)
                    {
                        solvedContextName.add("nameprincipal2_identificationtypeselect");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_identificationtype") == null && sessionValidationErrorList.getError("nameprincipal2_identificationtype") != null)
                    {
                        solvedContextName.add("nameprincipal2_identificationtype");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_dateofbirth") == null && sessionValidationErrorList.getError("nameprincipal2_dateofbirth") != null)
                    {
                        solvedContextName.add("nameprincipal2_dateofbirth");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_address") == null && sessionValidationErrorList.getError("nameprincipal2_address") != null)
                    {
                        solvedContextName.add("nameprincipal2_address");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_city") == null && sessionValidationErrorList.getError("nameprincipal2_city") != null)
                    {
                        solvedContextName.add("nameprincipal2_city");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_State") == null && sessionValidationErrorList.getError("nameprincipal2_State") != null)
                    {
                        solvedContextName.add("nameprincipal2_State");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_zip") == null && sessionValidationErrorList.getError("nameprincipal2_zip") != null)
                    {
                        solvedContextName.add("nameprincipal2_zip");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_country") == null && sessionValidationErrorList.getError("nameprincipal2_country") != null)
                    {
                        solvedContextName.add("nameprincipal2_country");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_street") == null && sessionValidationErrorList.getError("nameprincipal2_street") != null)
                    {
                        solvedContextName.add("nameprincipal2_street");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_telnocc2") == null && sessionValidationErrorList.getError("nameprincipal2_telnocc2") != null)
                    {
                        solvedContextName.add("nameprincipal2_telnocc2");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_telephonenumber") == null && sessionValidationErrorList.getError("nameprincipal2_telephonenumber") != null)
                    {
                        solvedContextName.add("nameprincipal2_telephonenumber");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_emailaddress") == null && sessionValidationErrorList.getError("nameprincipal2_emailaddress") != null)
                    {
                        solvedContextName.add("nameprincipal2_emailaddress");
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_nationality") == null && sessionValidationErrorList.getError("nameprincipal2_nationality") != null)
                    {
                        solvedContextName.add("nameprincipal2_nationality");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_Passportexpirydate") == null && sessionValidationErrorList.getError("nameprincipal2_Passportexpirydate") != null)
                    {
                        solvedContextName.add("nameprincipal2_Passportexpirydate");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("nameprincipal2_passportissuedate") == null && sessionValidationErrorList.getError("nameprincipal2_passportissuedate") != null)
                    {
                        solvedContextName.add("nameprincipal2_passportissuedate");        //Add Specific
                    }

                    if (requestValidationErrorList.getError("nameprincipal3") == null && sessionValidationErrorList.getError("nameprincipal3") != null)
                    {
                        solvedContextName.add("nameprincipal3");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_lastname") == null && sessionValidationErrorList.getError("nameprincipal3_lastname") != null)
                    {
                        solvedContextName.add("nameprincipal3_lastname");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_title") == null && sessionValidationErrorList.getError("nameprincipal3_title") != null)
                    {
                        solvedContextName.add("nameprincipal3_title");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_owned") == null && sessionValidationErrorList.getError("nameprincipal3_owned") != null)
                    {
                        solvedContextName.add("nameprincipal3_owned");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_identificationtypeselect") == null && sessionValidationErrorList.getError("nameprincipal3_identificationtypeselect") != null)
                    {
                        solvedContextName.add("nameprincipal3_identificationtypeselect");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_identificationtype") == null && sessionValidationErrorList.getError("nameprincipal3_identificationtype") != null)
                    {
                        solvedContextName.add("nameprincipal3_identificationtype");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_dateofbirth") == null && sessionValidationErrorList.getError("nameprincipal3_dateofbirth") != null)
                    {
                        solvedContextName.add("nameprincipal3_dateofbirth");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_address") == null && sessionValidationErrorList.getError("nameprincipal3_address") != null)
                    {
                        solvedContextName.add("nameprincipal3_address");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_city") == null && sessionValidationErrorList.getError("nameprincipal3_city") != null)
                    {
                        solvedContextName.add("nameprincipal3_city");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_State") == null && sessionValidationErrorList.getError("nameprincipal3_State") != null)
                    {
                        solvedContextName.add("nameprincipal3_State");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_zip") == null && sessionValidationErrorList.getError("nameprincipal3_zip") != null)
                    {
                        solvedContextName.add("nameprincipal3_zip");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_country") == null && sessionValidationErrorList.getError("nameprincipal3_country") != null)
                    {
                        solvedContextName.add("nameprincipal3_country");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_street") == null && sessionValidationErrorList.getError("nameprincipal3_street") != null)
                    {
                        solvedContextName.add("nameprincipal3_street");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_telnocc1") == null && sessionValidationErrorList.getError("nameprincipal3_telnocc1") != null)
                    {
                        solvedContextName.add("nameprincipal3_telnocc1");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_telephonenumber") == null && sessionValidationErrorList.getError("nameprincipal3_telephonenumber") != null)
                    {
                        solvedContextName.add("nameprincipal3_telephonenumber");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_emailaddress") == null && sessionValidationErrorList.getError("nameprincipal3_emailaddress") != null)
                    {
                        solvedContextName.add("nameprincipal3_emailaddress");
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_nationality") == null && sessionValidationErrorList.getError("nameprincipal3_nationality") != null)
                    {
                        solvedContextName.add("nameprincipal3_nationality");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_Passportexpirydate") == null && sessionValidationErrorList.getError("nameprincipal3_Passportexpirydate") != null)
                    {
                        solvedContextName.add("nameprincipal3_Passportexpirydate");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("nameprincipal3_passportissuedate") == null && sessionValidationErrorList.getError("nameprincipal3_passportissuedate") != null)
                    {
                        solvedContextName.add("nameprincipal3_passportissuedate");        //Add Specific
                    }
                }
                if (requestValidationErrorList.getError("nameprincipalplus_owned") == null && sessionValidationErrorList.getError("nameprincipalplus_owned") != null)
                {
                    solvedContextName.add("nameprincipalplus_owned");
                }
                //shareholder profile special condition
                if (parameter.equals("shareholderprofile1_owned"))
                {
                    if (requestValidationErrorList.getError("shareholderprofile2") == null && sessionValidationErrorList.getError("shareholderprofile2") != null)
                    {
                        solvedContextName.add("shareholderprofile2");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_lastname") == null && sessionValidationErrorList.getError("shareholderprofile2_lastname") != null)
                    {
                        solvedContextName.add("shareholderprofile2_lastname");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_title") == null && sessionValidationErrorList.getError("shareholderprofile2_title") != null)
                    {
                        solvedContextName.add("shareholderprofile2_title");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_owned") == null && sessionValidationErrorList.getError("shareholderprofile2_owned") != null)
                    {
                        solvedContextName.add("shareholderprofile2_owned");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_identificationtypeselect") == null && sessionValidationErrorList.getError("shareholderprofile2_identificationtypeselect") != null)
                    {
                        solvedContextName.add("shareholderprofile2_identificationtypeselect");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_identificationtype") == null && sessionValidationErrorList.getError("shareholderprofile2_identificationtype") != null)
                    {
                        solvedContextName.add("shareholderprofile2_identificationtype");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_dateofbirth") == null && sessionValidationErrorList.getError("shareholderprofile2_dateofbirth") != null)
                    {
                        solvedContextName.add("shareholderprofile2_dateofbirth");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_address") == null && sessionValidationErrorList.getError("shareholderprofile2_address") != null)
                    {
                        solvedContextName.add("shareholderprofile2_address");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_city") == null && sessionValidationErrorList.getError("shareholderprofile2_city") != null)
                    {
                        solvedContextName.add("shareholderprofile2_city");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_State") == null && sessionValidationErrorList.getError("shareholderprofile2_State") != null)
                    {
                        solvedContextName.add("shareholderprofile2_State");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_zip") == null && sessionValidationErrorList.getError("shareholderprofile2_zip") != null)
                    {
                        solvedContextName.add("shareholderprofile2_zip");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_country") == null && sessionValidationErrorList.getError("shareholderprofile2_country") != null)
                    {
                        solvedContextName.add("shareholderprofile2_country");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_street") == null && sessionValidationErrorList.getError("shareholderprofile2_street") != null)
                    {
                        solvedContextName.add("shareholderprofile2_street");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_telnocc2") == null && sessionValidationErrorList.getError("shareholderprofile2_telnocc2") != null)
                    {
                        solvedContextName.add("shareholderprofile2_telnocc2");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_telephonenumber") == null && sessionValidationErrorList.getError("shareholderprofile2_telephonenumber") != null)
                    {
                        solvedContextName.add("shareholderprofile2_telephonenumber");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_emailaddress") == null && sessionValidationErrorList.getError("shareholderprofile2_emailaddress") != null)
                    {
                        solvedContextName.add("shareholderprofile2_emailaddress");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_nationality") == null && sessionValidationErrorList.getError("shareholderprofile2_nationality") != null)
                    {
                        solvedContextName.add("shareholderprofile2_nationality");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_Passportexpirydate") == null && sessionValidationErrorList.getError("shareholderprofile2_Passportexpirydate") != null)
                    {
                        solvedContextName.add("shareholderprofile2_Passportexpirydate");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile2_passportissuedate") == null && sessionValidationErrorList.getError("shareholderprofile2_passportissuedate") != null)
                    {
                        solvedContextName.add("shareholderprofile2_passportissuedate");        //Add Specific
                    }
                    //Shareholder profile 3
                    if (requestValidationErrorList.getError("shareholderprofile3") == null && sessionValidationErrorList.getError("shareholderprofile3") != null)
                    {
                        solvedContextName.add("shareholderprofile3");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_lastname") == null && sessionValidationErrorList.getError("shareholderprofile3_lastname") != null)
                    {
                        solvedContextName.add("shareholderprofile3_lastname");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_title") == null && sessionValidationErrorList.getError("shareholderprofile3_title") != null)
                    {
                        solvedContextName.add("shareholderprofile3_title");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_owned") == null && sessionValidationErrorList.getError("shareholderprofile3_owned") != null)
                    {
                        solvedContextName.add("shareholderprofile3_owned");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_identificationtypeselect") == null && sessionValidationErrorList.getError("shareholderprofile3_identificationtypeselect") != null)
                    {
                        solvedContextName.add("shareholderprofile3_identificationtypeselect");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_identificationtype") == null && sessionValidationErrorList.getError("shareholderprofile3_identificationtype") != null)
                    {
                        solvedContextName.add("shareholderprofile3_identificationtype");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_dateofbirth") == null && sessionValidationErrorList.getError("shareholderprofile3_dateofbirth") != null)
                    {
                        solvedContextName.add("shareholderprofile3_dateofbirth");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_address") == null && sessionValidationErrorList.getError("shareholderprofile3_address") != null)
                    {
                        solvedContextName.add("shareholderprofile3_address");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_city") == null && sessionValidationErrorList.getError("shareholderprofile3_city") != null)
                    {
                        solvedContextName.add("shareholderprofile3_city");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_State") == null && sessionValidationErrorList.getError("shareholderprofile3_State") != null)
                    {
                        solvedContextName.add("shareholderprofile3_State");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_zip") == null && sessionValidationErrorList.getError("shareholderprofile3_zip") != null)
                    {
                        solvedContextName.add("shareholderprofile3_zip");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_country") == null && sessionValidationErrorList.getError("shareholderprofile3_country") != null)
                    {
                        solvedContextName.add("shareholderprofile3_country");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_street") == null && sessionValidationErrorList.getError("shareholderprofile3_street") != null)
                    {
                        solvedContextName.add("shareholderprofile3_street");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_telnocc2") == null && sessionValidationErrorList.getError("shareholderprofile3_telnocc2") != null)
                    {
                        solvedContextName.add("shareholderprofile3_telnocc2");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_telephonenumber") == null && sessionValidationErrorList.getError("shareholderprofile3_telephonenumber") != null)
                    {
                        solvedContextName.add("shareholderprofile3_telephonenumber");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_emailaddress") == null && sessionValidationErrorList.getError("shareholderprofile3_emailaddress") != null)
                    {
                        solvedContextName.add("shareholderprofile3_emailaddress");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_nationality") == null && sessionValidationErrorList.getError("shareholderprofile3_nationality") != null)
                    {
                        solvedContextName.add("shareholderprofile3_nationality");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_Passportexpirydate") == null && sessionValidationErrorList.getError("shareholderprofile3_Passportexpirydate") != null)
                    {
                        solvedContextName.add("shareholderprofile3_Passportexpirydate");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile3_passportissuedate") == null && sessionValidationErrorList.getError("shareholderprofile3_passportissuedate") != null)
                    {
                        solvedContextName.add("shareholderprofile3_passportissuedate");        //Add Specific
                    }
                    /////
                    //Shareholder profile 4
                    if (requestValidationErrorList.getError("shareholderprofile4") == null && sessionValidationErrorList.getError("shareholderprofile4") != null)
                    {
                        solvedContextName.add("shareholderprofile4");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_lastname") == null && sessionValidationErrorList.getError("shareholderprofile4_lastname") != null)
                    {
                        solvedContextName.add("shareholderprofile4_lastname");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_title") == null && sessionValidationErrorList.getError("shareholderprofile4_title") != null)
                    {
                        solvedContextName.add("shareholderprofile4_title");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_owned") == null && sessionValidationErrorList.getError("shareholderprofile4_owned") != null)
                    {
                        solvedContextName.add("shareholderprofile4_owned");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_identificationtypeselect") == null && sessionValidationErrorList.getError("shareholderprofile4_identificationtypeselect") != null)
                    {
                        solvedContextName.add("shareholderprofile4_identificationtypeselect");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_identificationtype") == null && sessionValidationErrorList.getError("shareholderprofile4_identificationtype") != null)
                    {
                        solvedContextName.add("shareholderprofile4_identificationtype");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_dateofbirth") == null && sessionValidationErrorList.getError("shareholderprofile4_dateofbirth") != null)
                    {
                        solvedContextName.add("shareholderprofile4_dateofbirth");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_address") == null && sessionValidationErrorList.getError("shareholderprofile4_address") != null)
                    {
                        solvedContextName.add("shareholderprofile4_address");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_city") == null && sessionValidationErrorList.getError("shareholderprofile4_city") != null)
                    {
                        solvedContextName.add("shareholderprofile4_city");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_State") == null && sessionValidationErrorList.getError("shareholderprofile4_State") != null)
                    {
                        solvedContextName.add("shareholderprofile4_State");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_zip") == null && sessionValidationErrorList.getError("shareholderprofile4_zip") != null)
                    {
                        solvedContextName.add("shareholderprofile4_zip");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_country") == null && sessionValidationErrorList.getError("shareholderprofile4_country") != null)
                    {
                        solvedContextName.add("shareholderprofile4_country");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_street") == null && sessionValidationErrorList.getError("shareholderprofile4_street") != null)
                    {
                        solvedContextName.add("shareholderprofile4_street");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_telnocc2") == null && sessionValidationErrorList.getError("shareholderprofile4_telnocc2") != null)
                    {
                        solvedContextName.add("shareholderprofile4_telnocc2");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_telephonenumber") == null && sessionValidationErrorList.getError("shareholderprofile4_telephonenumber") != null)
                    {
                        solvedContextName.add("shareholderprofile4_telephonenumber");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_emailaddress") == null && sessionValidationErrorList.getError("shareholderprofile4_emailaddress") != null)
                    {
                        solvedContextName.add("shareholderprofile4_emailaddress");
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_nationality") == null && sessionValidationErrorList.getError("shareholderprofile4_nationality") != null)
                    {
                        solvedContextName.add("shareholderprofile4_nationality");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_Passportexpirydate") == null && sessionValidationErrorList.getError("shareholderprofile4_Passportexpirydate") != null)
                    {
                        solvedContextName.add("shareholderprofile4_Passportexpirydate");        //Add Specific
                    }
                    if (requestValidationErrorList.getError("shareholderprofile4_passportissuedate") == null && sessionValidationErrorList.getError("shareholderprofile4_passportissuedate") != null)
                    {
                        solvedContextName.add("shareholderprofile4_passportissuedate");        //Add Specific
                    }
                }
                if (requestValidationErrorList.getError("shareholderplus_owned") == null && sessionValidationErrorList.getError("shareholderplus_owned") != null)
                {
                    solvedContextName.add("shareholderplus_owned");
                }

                //business profile special condition

                if ("lowestticket".equals(parameter) || "averageticket".equals(parameter) || "highestticket".equals(parameter))
                {
                    if (requestValidationErrorList.getError("lowestticket") == null && sessionValidationErrorList.getError("lowestticket") != null)
                    {
                        solvedContextName.add("lowestticket");
                    }
                    if (requestValidationErrorList.getError("averageticket") == null && sessionValidationErrorList.getError("averageticket") != null)
                    {
                        solvedContextName.add("averageticket");
                    }
                    if (requestValidationErrorList.getError("highestticket") == null && sessionValidationErrorList.getError("highestticket") != null)
                    {
                        solvedContextName.add("highestticket");
                    }

                    if (requestValidationErrorList.getError("ltlessthan_AT") == null && sessionValidationErrorList.getError("ltlessthan_AT") != null)
                    {
                        solvedContextName.add("ltlessthan_AT");
                    }

                    if (requestValidationErrorList.getError("atlessthan_HT") == null && sessionValidationErrorList.getError("atlessthan_HT") != null)
                    {
                        solvedContextName.add("atlessthan_HT");
                    }
                }

                if ("methodofacceptance_moto".equals(parameter) || "methodofacceptance_internet".equals(parameter) || "methodofacceptance_swipe".equals(parameter))
                {
                    if (requestValidationErrorList.getError("methodofacceptance_moto") == null && sessionValidationErrorList.getError("methodofacceptance_moto") != null)
                    {
                        solvedContextName.add("methodofacceptance_moto");
                    }
                    if (requestValidationErrorList.getError("methodofacceptance_internet") == null && sessionValidationErrorList.getError("methodofacceptance_internet") != null)
                    {
                        solvedContextName.add("methodofacceptance_internet");
                    }
                    if (requestValidationErrorList.getError("methodofacceptance_swipe") == null && sessionValidationErrorList.getError("methodofacceptance_swipe") != null)
                    {
                        solvedContextName.add("methodofacceptance_swipe");
                    }

                    if (requestValidationErrorList.getError("methodofacceptance_All") == null && sessionValidationErrorList.getError("methodofacceptance_All") != null)
                    {
                        solvedContextName.add("methodofacceptance_All");
                    }
                }
                //Payment type Accepted and Volume
                if ("paymenttype_credit".equals(parameter) || "paymenttype_debit".equals(parameter) || "paymenttype_netbanking".equals(parameter)|| "paymenttype_wallet".equals(parameter)|| "paymenttype_alternate".equals(parameter))
                {
                    if (requestValidationErrorList.getError("paymenttype_credit") == null && sessionValidationErrorList.getError("paymenttype_credit") != null)
                    {
                        solvedContextName.add("paymenttype_credit");
                    }
                    if (requestValidationErrorList.getError("paymenttype_debit") == null && sessionValidationErrorList.getError("paymenttype_debit") != null)
                    {
                        solvedContextName.add("paymenttype_debit");
                    }
                    if (requestValidationErrorList.getError("paymenttype_netbanking") == null && sessionValidationErrorList.getError("paymenttype_netbanking") != null)
                    {
                        solvedContextName.add("paymenttype_netbanking");
                    }
                    if (requestValidationErrorList.getError("paymenttype_wallet") == null && sessionValidationErrorList.getError("paymenttype_wallet") != null)
                    {
                        solvedContextName.add("paymenttype_wallet");
                    }
                    if (requestValidationErrorList.getError("paymenttype_alternate") == null && sessionValidationErrorList.getError("paymenttype_alternate") != null)
                    {
                        solvedContextName.add("paymenttype_alternate");
                    }
                    if (requestValidationErrorList.getError("paymenttype_All") == null && sessionValidationErrorList.getError("paymenttype_All") != null)
                    {
                        solvedContextName.add("paymenttype_All");
                    }
                }

                //Percentage of foreign Transactions
                if ("foreigntransactions_us".equals(parameter) || "foreigntransactions_Europe".equals(parameter) || "foreigntransactions_Asia".equals(parameter)|| "foreigntransactions_cis".equals(parameter)|| "foreigntransactions_canada".equals(parameter)|| "foreigntransactions_uk".equals(parameter)|| "foreigntransactions_RestoftheWorld".equals(parameter))
                {
                    if (requestValidationErrorList.getError("foreigntransactions_us") == null && sessionValidationErrorList.getError("foreigntransactions_us") != null)
                    {
                        solvedContextName.add("foreigntransactions_us");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_Europe") == null && sessionValidationErrorList.getError("foreigntransactions_Europe") != null)
                    {
                        solvedContextName.add("foreigntransactions_Europe");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_Asia") == null && sessionValidationErrorList.getError("foreigntransactions_Asia") != null)
                    {
                        solvedContextName.add("foreigntransactions_Asia");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_cis") == null && sessionValidationErrorList.getError("foreigntransactions_cis") != null)
                    {
                        solvedContextName.add("foreigntransactions_cis");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_canada") == null && sessionValidationErrorList.getError("foreigntransactions_canada") != null)
                    {
                        solvedContextName.add("foreigntransactions_canada");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_uk") == null && sessionValidationErrorList.getError("foreigntransactions_uk") != null)
                    {
                        solvedContextName.add("foreigntransactions_uk");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_RestoftheWorld") == null && sessionValidationErrorList.getError("foreigntransactions_RestoftheWorld") != null)
                    {
                        solvedContextName.add("foreigntransactions_RestoftheWorld");
                    }
                    if (requestValidationErrorList.getError("foreigntransactions_All") == null && sessionValidationErrorList.getError("foreigntransactions_All") != null)
                    {
                        solvedContextName.add("foreigntransactions_All");
                    }
                }

                //Channel Type Accepted and Volume

                if ("one_time_percentage".equals(parameter) || "moto_percentage".equals(parameter) || "recurring_percentage".equals(parameter)|| "threedsecure_percentage".equals(parameter)|| "internet_percentage".equals(parameter)|| "swipe_percentage".equals(parameter) || "terminal_type_otheryes".equals(parameter) || "terminal_type_other".equals(parameter))
                {
                    if (requestValidationErrorList.getError("one_time_percentage") == null && sessionValidationErrorList.getError("one_time_percentage") != null)
                    {
                        solvedContextName.add("one_time_percentage");
                    }
                    if (requestValidationErrorList.getError("moto_percentage") == null && sessionValidationErrorList.getError("moto_percentage") != null)
                    {
                        solvedContextName.add("moto_percentage");
                    }
                    if (requestValidationErrorList.getError("recurring_percentage") == null && sessionValidationErrorList.getError("recurring_percentage") != null)
                    {
                        solvedContextName.add("recurring_percentage");
                    }
                    if (requestValidationErrorList.getError("threedsecure_percentage") == null && sessionValidationErrorList.getError("threedsecure_percentage") != null)
                    {
                        solvedContextName.add("threedsecure_percentage");
                    }
                    if (requestValidationErrorList.getError("internet_percentage") == null && sessionValidationErrorList.getError("internet_percentage") != null)
                    {
                        solvedContextName.add("internet_percentage");
                    }
                    if (requestValidationErrorList.getError("swipe_percentage") == null && sessionValidationErrorList.getError("swipe_percentage") != null)
                    {
                        solvedContextName.add("swipe_percentage");
                    }
                    if (requestValidationErrorList.getError("terminal_type_otheryes") == null && sessionValidationErrorList.getError("terminal_type_otheryes") != null)
                    {
                        solvedContextName.add("terminal_type_otheryes");
                    }
                    if (requestValidationErrorList.getError("terminal_type_other") == null && sessionValidationErrorList.getError("terminal_type_other") != null)
                    {
                        solvedContextName.add("terminal_type_other");
                    }
                    if (requestValidationErrorList.getError("terminaltypeAll") == null && sessionValidationErrorList.getError("terminaltypeAll") != null)
                    {
                        solvedContextName.add("terminaltypeAll");
                    }
                }

                if (parameter.equals("isacallcenterused") && "N".equals(request.getParameter("isacallcenterused")))
                {

                    if (requestValidationErrorList.getError("isacallcenterusedyes") == null && sessionValidationErrorList.getError("isacallcenterusedyes") != null)
                    {
                        solvedContextName.add("isacallcenterusedyes");
                    }

                }
                if (parameter.equals("isafulfillmenthouseused") && "N".equals(request.getParameter("isafulfillmenthouseused")))
                {

                    if (requestValidationErrorList.getError("isafulfillmenthouseused_yes") == null && sessionValidationErrorList.getError("isafulfillmenthouseused_yes") != null)
                    {
                        solvedContextName.add("isafulfillmenthouseused_yes");
                    }

                }
                if (parameter.equals("recurringservices") && "N".equals(request.getParameter("recurringservices")))
                {

                    if (requestValidationErrorList.getError("recurringservicesyes") == null && sessionValidationErrorList.getError("recurringservicesyes") != null)
                    {
                        solvedContextName.add("recurringservicesyes");
                    }

                }
                if (parameter.equals("shopping_cart") && "N".equals(request.getParameter("shopping_cart")))
                {

                    if (requestValidationErrorList.getError("shopping_cart_details") == null && sessionValidationErrorList.getError("shopping_cart_details") != null)
                    {
                        solvedContextName.add("shopping_cart_details");
                    }

                }


                if (requestValidationErrorList.getError("cardtypesaccepted_other_yes") == null && sessionValidationErrorList.getError("cardtypesaccepted_other_yes") != null)
                {
                    solvedContextName.add("cardtypesaccepted_other_yes");
                }
                if (requestValidationErrorList.getError("cardtypesaccepted_other") == null && sessionValidationErrorList.getError("cardtypesaccepted_other") != null)
                {
                    solvedContextName.add("cardtypesaccepted_other");
                }


                //Add Specific

                if (parameter.equals("countries_blocked") && "N".equals(request.getParameter("countries_blocked")))
                {

                    if (requestValidationErrorList.getError("countries_blocked_details") == null && sessionValidationErrorList.getError("countries_blocked_details") != null)
                    {
                        solvedContextName.add("countries_blocked_details");
                    }

                }

                if (parameter.equals("customer_support") && "N".equals(request.getParameter("customer_support")))
                {
                    if (requestValidationErrorList.getError("customersupport_email") == null && sessionValidationErrorList.getError("customersupport_email") != null)
                    {
                        solvedContextName.add("customersupport_email");
                    }
                    if (requestValidationErrorList.getError("custsupportwork_hours") == null && sessionValidationErrorList.getError("custsupportwork_hours") != null)
                    {
                        solvedContextName.add("custsupportwork_hours");
                    }
                    if (requestValidationErrorList.getError("technical_contact") == null && sessionValidationErrorList.getError("technical_contact") != null)
                    {
                        solvedContextName.add("technical_contact");
                    }
                    if (requestValidationErrorList.getError("timeframe") == null && sessionValidationErrorList.getError("timeframe") != null)
                    {
                        solvedContextName.add("timeframe");
                    }
                }
                if (parameter.equals("affiliate_programs") && "N".equals(request.getParameter("affiliate_programs")))
                {
                    if (requestValidationErrorList.getError("affiliate_programs_details") == null && sessionValidationErrorList.getError("affiliate_programs_details") != null)
                    {
                        solvedContextName.add("affiliate_programs_details");
                    }
                }
                if (parameter.equals("listfraudtools") && "N".equals(request.getParameter("listfraudtools")))
                {
                    if (requestValidationErrorList.getError("listfraudtools_yes") == null && sessionValidationErrorList.getError("listfraudtools_yes") != null)
                    {
                        solvedContextName.add("listfraudtools_yes");
                    }
                }
                if (parameter.equals("agency_employed") && "N".equals(request.getParameter("agency_employed")))
                {
                    if (requestValidationErrorList.getError("agency_employed_yes") == null && sessionValidationErrorList.getError("agency_employed_yes") != null)
                    {
                        solvedContextName.add("agency_employed_yes");
                    }
                }
                if (parameter.equals("MCC_Ctegory") && "N".equals(request.getParameter("MCC_Ctegory")))
                {
                    if (requestValidationErrorList.getError("merchantcode") == null && sessionValidationErrorList.getError("merchantcode") != null)
                    {
                        solvedContextName.add("merchantcode");
                    }
                }
                /*if (requestValidationErrorList.getError("terminal_type_otheryes") == null && sessionValidationErrorList.getError("terminal_type_otheryes") != null)
                {
                    solvedContextName.add("terminal_type_otheryes");
                }*/

                if (requestValidationErrorList.getError("cardtypesaccepted_other_yes") == null && sessionValidationErrorList.getError("cardtypesaccepted_other_yes") != null)
                {
                    solvedContextName.add("cardtypesaccepted_other_yes");
                }

                if (requestValidationErrorList.getError("cardtypesaccepted_other") == null && sessionValidationErrorList.getError("cardtypesaccepted_other") != null)
                {
                    solvedContextName.add("cardtypesaccepted_other");
                }

                if ("cardvolume_visa".equals(parameter) || "cardvolume_mastercard".equals(parameter) || "cardvolume_americanexpress".equals(parameter)|| "cardvolume_dinner".equals(parameter)|| "cardvolume_discover".equals(parameter)|| "cardvolume_other".equals(parameter))
                {
                    if (requestValidationErrorList.getError("cardvolume_visa") == null && sessionValidationErrorList.getError("cardvolume_visa") != null)
                    {
                        solvedContextName.add("cardvolume_visa");
                    }
                    if (requestValidationErrorList.getError("cardvolume_mastercard") == null && sessionValidationErrorList.getError("cardvolume_mastercard") != null)
                    {
                        solvedContextName.add("cardvolume_mastercard");
                    }
                    if (requestValidationErrorList.getError("cardvolume_americanexpress") == null && sessionValidationErrorList.getError("cardvolume_americanexpress") != null)
                    {
                        solvedContextName.add("cardvolume_americanexpress");
                    }
                    if (requestValidationErrorList.getError("cardvolume_dinner") == null && sessionValidationErrorList.getError("cardvolume_dinner") != null)
                    {
                        solvedContextName.add("cardvolume_dinner");
                    }
                    if (requestValidationErrorList.getError("cardvolume_discover") == null && sessionValidationErrorList.getError("cardvolume_discover") != null)
                    {
                        solvedContextName.add("cardvolume_discover");
                    }
                    if (requestValidationErrorList.getError("cardvolume_rupay") == null && sessionValidationErrorList.getError("cardvolume_rupay") != null)
                    {
                        solvedContextName.add("cardvolume_rupay");
                    }
                    if (requestValidationErrorList.getError("cardvolume_jcb") == null && sessionValidationErrorList.getError("cardvolume_jcb") != null)
                    {
                        solvedContextName.add("cardvolume_jcb");
                    }
                    if (requestValidationErrorList.getError("cardvolume_other") == null && sessionValidationErrorList.getError("cardvolume_other") != null)
                    {
                        solvedContextName.add("cardvolume_other");
                    }

                    if (requestValidationErrorList.getError("cardvolumeall") == null && sessionValidationErrorList.getError("cardvolumeall") != null)
                    {
                        solvedContextName.add("cardvolumeall");
                    }
                }

                //Card holder profile special condition
                if (parameter.equals("compliance_swapp") && "N".equals(request.getParameter("compliance_swapp")))
                {
                    if (requestValidationErrorList.getError("compliance_thirdpartyappform") == null && sessionValidationErrorList.getError("compliance_thirdpartyappform") != null)
                    {
                        solvedContextName.add("compliance_thirdpartyappform");
                    }
                    if (requestValidationErrorList.getError("compliance_thirdpartysoft") == null && sessionValidationErrorList.getError("compliance_thirdpartysoft") != null)
                    {
                        solvedContextName.add("compliance_thirdpartysoft");
                    }
                    if (requestValidationErrorList.getError("compliance_version") == null && sessionValidationErrorList.getError("compliance_version") != null)
                    {
                        solvedContextName.add("compliance_version");
                    }
                    if (requestValidationErrorList.getError("compliance_companiesorgateways") == null && sessionValidationErrorList.getError("compliance_companiesorgateways") != null)
                    {
                        solvedContextName.add("compliance_companiesorgateways");
                    }
                    if (requestValidationErrorList.getError("compliance_companiesorgateways_yes") == null && sessionValidationErrorList.getError("compliance_companiesorgateways_yes") != null)
                    {
                        solvedContextName.add("compliance_companiesorgateways_yes");
                    }
                }
                if (parameter.equals("compliance_companiesorgateways") && "N".equals(request.getParameter("compliance_companiesorgateways")))
                {
                    if (requestValidationErrorList.getError("compliance_companiesorgateways_yes") == null && sessionValidationErrorList.getError("compliance_companiesorgateways_yes") != null)
                    {
                        solvedContextName.add("compliance_companiesorgateways_yes");
                    }
                }
                if (parameter.equals("compliance_datacompromise") && "N".equals(request.getParameter("compliance_datacompromise")))
                {
                    if (requestValidationErrorList.getError("compliance_datacompromise_yes") == null && sessionValidationErrorList.getError("compliance_datacompromise_yes") != null)
                    {
                        solvedContextName.add("compliance_datacompromise_yes");
                    }
                }
                if (parameter.equals("siteinspection_merchant") && "N".equals(request.getParameter("siteinspection_merchant")))
                {
                    if (requestValidationErrorList.getError("siteinspection_landlord") == null && sessionValidationErrorList.getError("siteinspection_landlord") != null)
                    {
                        solvedContextName.add("siteinspection_landlord");
                    }
                }

                //extra details profile special condition
                if (parameter.equals("company_financialreport") && ("Y".equals(request.getParameter("company_financialreport")) || "N".equals(request.getParameter("company_financialreport"))))
                {

                    if(requestValidationErrorList.getError("company_financialreportyes")==null && sessionValidationErrorList.getError("company_financialreportyes")!=null)
                    {
                        solvedContextName.add("company_financialreportyes");
                    }

                }
                if (parameter.equals("financialreport_available") && ("Y".equals(request.getParameter("financialreport_available")) || "N".equals(request.getParameter("financialreport_available"))))
                {

                    if(requestValidationErrorList.getError("financialreport_availableyes")==null && sessionValidationErrorList.getError("financialreport_availableyes")!=null)
                    {
                        solvedContextName.add("financialreport_availableyes");
                    }

                }
                if (parameter.equals("compliance_punitivesanction") && ("Y".equals(request.getParameter("compliance_punitivesanction")) || "N".equals(request.getParameter("compliance_punitivesanction"))))
                {

                    if(requestValidationErrorList.getError("compliance_punitivesanctionyes")==null && sessionValidationErrorList.getError("compliance_punitivesanctionyes")!=null)
                    {
                        solvedContextName.add("compliance_punitivesanctionyes");
                    }

                }
                if (parameter.equals("fulfillment_productemail") && ("Y".equals(request.getParameter("fulfillment_productemail")) || "N".equals(request.getParameter("fulfillment_productemail"))))
                {

                    if(requestValidationErrorList.getError("fulfillment_productemailyes")==null && sessionValidationErrorList.getError("fulfillment_productemailyes")!=null)
                    {
                        solvedContextName.add("fulfillment_productemailyes");
                    }

                }
                if (parameter.equals("blacklistedaccountclosed") && ("Y".equals(request.getParameter("blacklistedaccountclosed")) || "N".equals(request.getParameter("blacklistedaccountclosed"))))
                {
                    if(requestValidationErrorList.getError("blacklistedaccountclosedyes")==null && sessionValidationErrorList.getError("blacklistedaccountclosedyes")!=null)
                    {
                        solvedContextName.add("blacklistedaccountclosedyes");
                    }
                }
                if (parameter.equals("deedofagreement") && ("Y".equals(request.getParameter("deedofagreement")) || "N".equals(request.getParameter("deedofagreement"))))
                {
                    if(requestValidationErrorList.getError("deedofagreementyes")==null && sessionValidationErrorList.getError("deedofagreementyes")!=null)
                    {
                        solvedContextName.add("deedofagreementyes");
                    }
                }
            }
            for (ValidationException validationException : sessionValidationErrorList.errors())
            {
                if (solvedContextName == null || !solvedContextName.contains(validationException.getMessage()))
                {
                    validationErrorList.addError(validationException.getMessage(), validationException);
                }
            }
            for (ValidationException validationException : requestValidationErrorList.errors())
            {
                if (validationErrorList.getError(validationException.getMessage()) == null)
                {
                    validationErrorList.addError(validationException.getMessage(), validationException);
                }
            }
        }
        return validationErrorList;
    }
    //comparing fileUpload validation with the session
    public ValidationErrorList compareFileUploadValidationErrorList(ApplicationManagerVO applicationManagerVO,ValidationErrorList sessionValidationErrorList,ValidationErrorList requestValidationErrorList)
    {
        ValidationErrorList validationErrorList =null;

        if(!sessionValidationErrorList.isEmpty())
        {
            validationErrorList = new ValidationErrorList();

            ValidationErrorList conversionValidationErr= new ValidationErrorList();

            String pageNumber="6";

            if(Module.ADMIN.name().equals(applicationManagerVO.getUser()) || Module.PARTNER.name().equals(applicationManagerVO.getUser()))
            {
                pageNumber="7";
            }

            logger.debug("validateAppFilesUploaded before requestValidationErrorList....");
            validateAppFilesUploaded(applicationManagerVO, conversionValidationErr,false,pageNumber,false);
            for(ValidationException validationException:requestValidationErrorList.errors())
            {
                validationErrorList.addError(validationException.getMessage(),validationException);
                logger.debug("validationException........"+validationException.getMessage());
            }
            for(ValidationException validationException:sessionValidationErrorList.errors())
            {
                if(requestValidationErrorList.getError(validationException.getMessage())==null && conversionValidationErr.getError(validationException.getMessage())!=null)
                {
                    validationErrorList.addError(conversionValidationErr.getError(validationException.getMessage()).getMessage(),conversionValidationErr.getError(validationException.getMessage()));
                }


            }
        }
        return validationErrorList;
    }
    //conversion timestamp to datepicker for submit button
    public void  convertAllTimeStampToDatepicker(ApplicationManagerVO applicationManagerVO)
    {
        if(functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyBankruptcydate()))        //CompanyBankruptcydate
        {
            applicationManagerVO.getCompanyProfileVO().setCompanyBankruptcydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCompanyProfileVO().getCompanyBankruptcydate()));
        }
        /*if(functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyLengthOfTimeInBusiness()))  //company_lengthoftime_business
        {
            applicationManagerVO.getCompanyProfileVO().setCompanyLengthOfTimeInBusiness(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCompanyProfileVO().getCompanyLengthOfTimeInBusiness()));
        }*/
        if(functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration()))  //CompanyDate_Registration
        {
            applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setDate_of_registration(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration()));
        }

        //--------
        if (applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap()!=null)
        {
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth()))  //shareholderprofile1_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth()))  //shareholderprofile2_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth()))  //shareholderprofile3_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth()))  //shareholderprofile4_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate()))  //shareholderprofile1_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate()))  //shareholderprofile1_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate()))  //shareholderprofile2_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate()))  //shareholderprofile1_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth()))  //authorizedsignatoryprofile_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate()))  //authorizedsignatoryprofile_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate()))  //authorizedsignatoryprofile_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth()))  //directorsprofile_dateofbirth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate()))  //directorsprofile_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate()))  //directorsprofile_Passportexpirydate
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate()));
            }
            //Add specific for detepicker
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth()))  //director 2_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate()))  //director 2 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate()))  //director 2 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth()))  //director3_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate()))  //director 3 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate()))  //director 3 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate()));
            }
            //director 4 dob
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth()))  //director4_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate()))  //director 4 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate()))  //director 4 passport
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate()));
            }
            ////
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth()))  //Authorize2_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate()))  //authorize2  passport expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate()))  //authorize2  passport expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth()))  //authorize3_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate()))  //Authorize3_passportexpiry
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate()))  //Authorize3_passportexpiry
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate()));
            }
            //authorize 4 dob

            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDateofbirth()))  //authorize4_dateof birth
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDateofbirth(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDateofbirth()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportexpirydate()))  //Authorize3_passportexpiry
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportissuedate()))  //Authorize3_passportexpiry
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportissuedate()));
            }

            ////
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate()))  //shareholder profile3 expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate()))  //shareholder profile3 expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate()));
            }
            ////shareholder 4 passport
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate()))  //shareholder profile3 expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportexpirydate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate()));
            }
            if (functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate()))  //shareholder profile3 expiry date
            {
                applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportissuedate(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getOwnershipProfileVO().getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate()));
            }
            ////
        }

        //---------
        if(functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getCompliance_dateofcompliance()))
        {
            applicationManagerVO.getCardholderProfileVO().setCompliance_dateofcompliance(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCardholderProfileVO().getCompliance_dateofcompliance()));
        }
        if(functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getCompliance_dateoflastscan()))
        {
            applicationManagerVO.getCardholderProfileVO().setCompliance_dateoflastscan(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCardholderProfileVO().getCompliance_dateoflastscan()));
        }
        if(functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getCompliance_datacompromise_yes()))
        {
            applicationManagerVO.getCardholderProfileVO().setCompliance_datacompromise_yes(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCardholderProfileVO().getCompliance_datacompromise_yes()));
        }
        if(functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getSiteinspection_principal1_date()))
        {
            applicationManagerVO.getCardholderProfileVO().setSiteinspection_principal1_date(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCardholderProfileVO().getSiteinspection_principal1_date()));
        }
        if(functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getSiteinspection_principal2_date()))
        {
            applicationManagerVO.getCardholderProfileVO().setSiteinspection_principal2_date(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getCardholderProfileVO().getSiteinspection_principal2_date()));
        }

        if(functions.isValueNull(applicationManagerVO.getExtradetailsprofileVO().getOwnerSince()))
        {
            applicationManagerVO.getExtradetailsprofileVO().setOwnerSince(commonFunctionUtil.convertTimestampToDatepicker(applicationManagerVO.getExtradetailsprofileVO().getOwnerSince()));    //extradetails profile ownersince
        }
    }
    private void setCompanyProfileVO(HttpServletRequest request,CompanyProfileVO companyProfileVO)
    {
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCompany_name(request.getParameter("merchantname"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setRegistration_number(request.getParameter("companyregistrationnumber"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setDate_of_registration(request.getParameter("Company_Date_Registration"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(request.getParameter("Companyphonecc1"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_number(request.getParameter("CompanyTelephoneNO"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFax(request.getParameter("CompanyFax"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setEmail_id(request.getParameter("CompanyEmailAddress")); //
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressProof(request.getParameter("merchant_addressproof"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressId(request.getParameter("merchant_addressId"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddress(StringEscapeUtils.unescapeHtml(request.getParameter("locationaddress")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setStreet(request.getParameter("merchantstreet"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCity(StringEscapeUtils.unescapeHtml(request.getParameter("merchantcity")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setState(StringEscapeUtils.unescapeHtml(request.getParameter("merchantstate")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(request.getParameter("merchantcountry"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setZipcode(request.getParameter("merchantzipcode"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setVatidentification(request.getParameter("vatidentification"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFederalTaxId(request.getParameter("FederalTaxID"));

        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCompany_name(request.getParameter("corporatename"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressProof(request.getParameter("corporate_addressproof"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressId(request.getParameter("corporate_addressId"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddress(StringEscapeUtils.unescapeHtml(request.getParameter("corporateaddress")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCity(StringEscapeUtils.unescapeHtml(request.getParameter("corporatecity")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setState(StringEscapeUtils.unescapeHtml(request.getParameter("corporatestate")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setZipcode(request.getParameter("corporatezipcode"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCountry(request.getParameter("corporatecountry"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setStreet(request.getParameter("corporatestreet"));

        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCompany_name(request.getParameter("registered_corporatename"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistred_directors(request.getParameter("registered_directors"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistration_number(request.getParameter("EURegistrationNumber"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddressProof(StringEscapeUtils.unescapeHtml(request.getParameter("registered_directors_addressproof")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddressId(StringEscapeUtils.unescapeHtml(request.getParameter("registered_directors_addressId")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddress(StringEscapeUtils.unescapeHtml(request.getParameter("registered_directors_address")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCity(StringEscapeUtils.unescapeHtml(request.getParameter("registered_directors_city")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setState(StringEscapeUtils.unescapeHtml(request.getParameter("registered_directors_State")));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setZipcode(request.getParameter("registered_directors_postalcode"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCountry(request.getParameter("registered_directors_country"));
        companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setStreet(request.getParameter("registered_directors_street"));

        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setName(request.getParameter("contactname"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setEmailaddress(request.getParameter("contactemailaddress"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setPhonecc1(request.getParameter("contactname_telnocc1"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setTelephonenumber(request.getParameter("contactname_telephonenumber"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setDesignation(request.getParameter("contact_designation"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setSkypeIMaddress(request.getParameter("SkypeIMaddress"));

        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setName(request.getParameter("technicalcontactname"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setEmailaddress(request.getParameter("technicalemailaddress"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setPhonecc1(request.getParameter("technicalphonecc1"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setTelephonenumber(request.getParameter("Technical_telephonenumber"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setDesignation(request.getParameter("technical_designation"));

        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setName(request.getParameter("billingcontactname"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setEmailaddress(request.getParameter("billingemailaddress"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setPhonecc1(request.getParameter("financialphonecc1"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setTelephonenumber(request.getParameter("Financial_telephonenumber"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setDesignation(request.getParameter("billing_designation"));

        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setName(request.getParameter("cbk_contactperson"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setEmailaddress(request.getParameter("cbk_email"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setPhonecc1(request.getParameter("cbk_phonecc"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setTelephonenumber(request.getParameter("cbk_telephonenumber"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setDesignation(request.getParameter("cbk_designation"));

        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setName(request.getParameter("pci_contactperson"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setEmailaddress(request.getParameter("pci_email"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setPhonecc1(request.getParameter("pci_phonecc"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setTelephonenumber(request.getParameter("pci_telephonenumber"));
        companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setDesignation(request.getParameter("pci_designation"));

        companyProfileVO.setCountryOfRegistration(request.getParameter("countryofregistration"));
        companyProfileVO.setCompanyBankruptcy(request.getParameter("company_bankruptcy"));
        companyProfileVO.setCompanyBankruptcydate(request.getParameter("company_bankruptcydate"));
        companyProfileVO.setCompanyTypeOfBusiness(request.getParameter("company_typeofbusiness"));

        companyProfileVO.setCompanyCapitalResources(request.getParameter("company_capitalresources"));
        companyProfileVO.setCompanyTurnoverLastYear(request.getParameter("company_turnoverlastyear"));
        companyProfileVO.setCompanyNumberOfEmployees(request.getParameter("company_numberofemployees"));
        companyProfileVO.setCompany_currencylastyear(request.getParameter("company_currencylastyear"));
        companyProfileVO.setCompany_turnoverlastyear_unit(request.getParameter("company_turnoverlastyear_unit"));
//        companyProfileVO.setCompany_Date_Registration(request.getParameter("Company_Date_Registration"));
        companyProfileVO.setIscompany_insured(request.getParameter("iscompany_insured"));
        companyProfileVO.setInsured_companyname(request.getParameter("insured_companyname"));
        companyProfileVO.setInsured_currency(request.getParameter("insured_currency"));
        companyProfileVO.setInsured_amount(request.getParameter("insured_amount"));
        companyProfileVO.setLicense_required(request.getParameter("License_required"));
        companyProfileVO.setLicense_Permission(request.getParameter("License_Permission"));
        /*companyProfileVO.setCompanyphonecc1(request.getParameter("Companyphonecc1"));
        companyProfileVO.setCompanyTelephoneNO(request.getParameter("CompanyTelephoneNO"));
        companyProfileVO.setCompanyFax(request.getParameter("CompanyFax"));
        companyProfileVO.setCompanyEmailAddress(request.getParameter("CompanyEmailAddress"));
        companyProfileVO.setEURegistrationNumber(request.getParameter("EURegistrationNumber"));*/

        //ADD new
        companyProfileVO.setLegalProceeding(request.getParameter("legal_proceeding"));
        companyProfileVO.setStartup_business(request.getParameter("startup_business"));
        companyProfileVO.setCompanyLengthOfTimeInBusiness(request.getParameter("company_lengthoftime_business"));
        companyProfileVO.setMain_business_partner(request.getParameter("main_business_partner"));
        companyProfileVO.setLoans(request.getParameter("loans"));
        companyProfileVO.setIncome_economic_activity(request.getParameter("income_economic_activity"));
        companyProfileVO.setInterest_income(request.getParameter("interest_income"));
        companyProfileVO.setInvestments(request.getParameter("investments"));
        companyProfileVO.setIncome_sources_other(request.getParameter("income_sources_other"));
        companyProfileVO.setIncome_sources_other_yes(request.getParameter("income_sources_other_yes"));
    }

    private void setCompanyProfileVO(CompanyProfileVO requestCompanyProfileVO,CompanyProfileVO companyProfileVO)
    {
    //Company Address Details
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCompany_name(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setRegistration_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setVatidentification(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFax(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressProof()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressProof(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressProof()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressId()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressId(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressId()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddress(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCity(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getState()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setState(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getState()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setZipcode(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry()))
        {
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry());
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(commonFunctionUtil.getCountryDetailsForAPI(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry()));
            if(functions.isValueNull(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry())&& !functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc()))
                companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry().split("\\|")[1]);
        }
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getStreet()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setStreet(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getStreet());

        //Corporate address details
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCompany_name(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressProof()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressProof(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressProof()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressId()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressId(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressId()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddress()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddress(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddress()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCity()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCity(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCity()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getState()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setState(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getState()));
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getZipcode()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setZipcode(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getZipcode());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCountry()))
        {
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCountry(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCountry());
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCountry(commonFunctionUtil.getCountryDetailsForAPI(companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCountry()));
        }
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getStreet()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setStreet(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getStreet());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getEmailaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getName()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getEmailaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getPhonecc1()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getTelephonenumber()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getTelephonenumber());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getDesignation()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getName()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getEmailaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCountryOfRegistration()))
        {
            companyProfileVO.setCountryOfRegistration(requestCompanyProfileVO.getCountryOfRegistration());
            companyProfileVO.setCountryOfRegistration(commonFunctionUtil.getCountryDetailsForAPI(companyProfileVO.getCountryOfRegistration()));
        }

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyBankruptcy()))
            companyProfileVO.setCompanyBankruptcy(requestCompanyProfileVO.getCompanyBankruptcy());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyBankruptcydate()))
            companyProfileVO.setCompanyBankruptcydate(requestCompanyProfileVO.getCompanyBankruptcydate());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyTypeOfBusiness()))
            companyProfileVO.setCompanyTypeOfBusiness(requestCompanyProfileVO.getCompanyTypeOfBusiness());
        if(functions.isValueNull(requestCompanyProfileVO.getStartup_business()))
            companyProfileVO.setStartup_business(requestCompanyProfileVO.getStartup_business());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyLengthOfTimeInBusiness()))
            companyProfileVO.setCompanyLengthOfTimeInBusiness(requestCompanyProfileVO.getCompanyLengthOfTimeInBusiness());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyCapitalResources()))
            companyProfileVO.setCompanyCapitalResources(requestCompanyProfileVO.getCompanyCapitalResources());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyTurnoverLastYear()))
            companyProfileVO.setCompanyTurnoverLastYear(requestCompanyProfileVO.getCompanyTurnoverLastYear());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyNumberOfEmployees()))
            companyProfileVO.setCompanyNumberOfEmployees(requestCompanyProfileVO.getCompanyNumberOfEmployees());
        if(functions.isValueNull(requestCompanyProfileVO.getCompany_currencylastyear()))
            companyProfileVO.setCompany_currencylastyear(requestCompanyProfileVO.getCompany_currencylastyear());
        if(functions.isValueNull(requestCompanyProfileVO.getCompany_turnoverlastyear_unit()))
            companyProfileVO.setCompany_turnoverlastyear_unit(requestCompanyProfileVO.getCompany_turnoverlastyear_unit());
        //add Specific
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getTelephonenumber()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getDesignation()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getTelephonenumber()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getDesignation()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getLicense_required()))
            companyProfileVO.setLicense_required(requestCompanyProfileVO.getLicense_required());
        if(functions.isValueNull(requestCompanyProfileVO.getLicense_Permission()))
            companyProfileVO.setLicense_Permission(requestCompanyProfileVO.getLicense_Permission());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getPhonecc1()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getPhonecc1()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFax(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setEmail_id(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id());

        //Add Specific
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFederalTaxId(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number()))
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistration_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number());

        //ADD new
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getSkypeIMaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setSkypeIMaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getSkypeIMaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getLegalProceeding()))
            companyProfileVO.setLegalProceeding(requestCompanyProfileVO.getLegalProceeding());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getName()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getEmailaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getPhonecc1()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getDesignation()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getTelephonenumber()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getName()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getEmailaddress()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getPhonecc1()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getDesignation()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getDesignation());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getTelephonenumber()))
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getIscompany_insured()))
            companyProfileVO.setIscompany_insured(requestCompanyProfileVO.getIscompany_insured());
        if(functions.isValueNull(requestCompanyProfileVO.getInsured_companyname()))
            companyProfileVO.setInsured_companyname(requestCompanyProfileVO.getInsured_companyname());
        if(functions.isValueNull(requestCompanyProfileVO.getInsured_currency()))
            companyProfileVO.setInsured_currency(requestCompanyProfileVO.getInsured_currency());
        if(functions.isValueNull(requestCompanyProfileVO.getInsured_amount()))
            companyProfileVO.setInsured_amount(requestCompanyProfileVO.getInsured_amount());
        if(functions.isValueNull(requestCompanyProfileVO.getMain_business_partner()))
            companyProfileVO.setMain_business_partner(requestCompanyProfileVO.getMain_business_partner());
        if(functions.isValueNull(requestCompanyProfileVO.getLoans()))
            companyProfileVO.setLoans(requestCompanyProfileVO.getLoans());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_economic_activity()))
            companyProfileVO.setIncome_economic_activity(requestCompanyProfileVO.getIncome_economic_activity());
        if(functions.isValueNull(requestCompanyProfileVO.getInterest_income()))
            companyProfileVO.setInterest_income(requestCompanyProfileVO.getInterest_income());
        if(functions.isValueNull(requestCompanyProfileVO.getInvestments()))
            companyProfileVO.setInvestments(requestCompanyProfileVO.getInvestments());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_sources_other()))
            companyProfileVO.setIncome_sources_other(requestCompanyProfileVO.getIncome_sources_other());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_sources_other_yes()))
            companyProfileVO.setIncome_sources_other_yes(requestCompanyProfileVO.getIncome_sources_other_yes());
    }


    private void setCompanyProfileVO(CompanyProfileVO requestCompanyProfileVO,CompanyProfileVO companyProfileVO,ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name()) && validationErrorList.getError("merchantname")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCompany_name(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name()) && validationErrorList.getError("corporatename")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCompany_name(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressProof())&& validationErrorList.getError("merchant_addressproof")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressProof(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressProof()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressId())&& validationErrorList.getError("merchant_addressId")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressId(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressId()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressProof())&& validationErrorList.getError("corporate_addressproof")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressProof(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressProof()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressId())&& validationErrorList.getError("corporate_addressId")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddressId(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddressId()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress())&& validationErrorList.getError("locationaddress")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddress(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddress())&& validationErrorList.getError("corporateaddress")== null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setAddress(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getAddress()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity())&& validationErrorList.getError("merchantcity")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCity(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getState())&& validationErrorList.getError("merchantstate")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setState((StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getState())));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode())&& validationErrorList.getError("merchantzipcode")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setZipcode(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry())&& validationErrorList.getError("merchantcountry")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getStreet())&& validationErrorList.getError("merchantstreet")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setStreet(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getStreet());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity())&& validationErrorList.getError("corporatecity")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCity(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCity()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getState())&& validationErrorList.getError("corporatestate")== null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setState(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getState()));

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getZipcode())&& validationErrorList.getError("corporatezipcode")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setZipcode(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getZipcode());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCountry())&& validationErrorList.getError("corporatecountry")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCountry(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCountry());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getStreet())&& validationErrorList.getError("corporatestreet")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setStreet(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getStreet());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName())&& validationErrorList.getError("contactname")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getEmailaddress())&& validationErrorList.getError("contactemailaddress")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getName())&& validationErrorList.getError("technicalcontactname")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getEmailaddress())&& validationErrorList.getError("technicalemailaddress")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getPhonecc1())&& validationErrorList.getError("contactname_telnocc1")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getTelephonenumber())&& validationErrorList.getError("contactname_telephonenumber")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getDesignation())&& validationErrorList.getError("contact_designation")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax())&& validationErrorList.getError("CompanyFax")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFax(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getName())&& validationErrorList.getError("billingcontactname")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getEmailaddress())&& validationErrorList.getError("billingemailaddress")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getEmailaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getCountryOfRegistration())&& validationErrorList.getError("countryofregistration")==null)
            companyProfileVO.setCountryOfRegistration(requestCompanyProfileVO.getCountryOfRegistration());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number())&& validationErrorList.getError("companyregistrationnumber")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setRegistration_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification())&& validationErrorList.getError("vatidentification")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setVatidentification(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number())&& validationErrorList.getError("company_registered_eu")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistration_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyBankruptcy())&& validationErrorList.getError("company_bankruptcy")==null)
            companyProfileVO.setCompanyBankruptcy(requestCompanyProfileVO.getCompanyBankruptcy());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyBankruptcydate())&& validationErrorList.getError("company_bankruptcydate")==null)
            companyProfileVO.setCompanyBankruptcydate(requestCompanyProfileVO.getCompanyBankruptcydate());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyTypeOfBusiness())&& validationErrorList.getError("company_typeofbusiness")==null)
            companyProfileVO.setCompanyTypeOfBusiness(requestCompanyProfileVO.getCompanyTypeOfBusiness());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCompany_name())&& validationErrorList.getError("registered_corporatename")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCompany_name(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCompany_name());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistred_directors())&& validationErrorList.getError("registered_directors")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistred_directors(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistred_directors());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddress())&& validationErrorList.getError("registered_directors_address")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddress(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddress()));

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCity())&& validationErrorList.getError("registered_directors_city")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCity(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCity()));

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getState())&& validationErrorList.getError("registered_directors_State")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setState(StringEscapeUtils.unescapeHtml(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getState()));

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getZipcode())&& validationErrorList.getError("registered_directors_postalcode")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setZipcode(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getZipcode());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddressProof())&& validationErrorList.getError("registered_directors_addressproof")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddressProof(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddressProof());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddressId())&& validationErrorList.getError("registered_directors_addressId")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setAddressId(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getAddressId());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCountry())&& validationErrorList.getError("registered_directors_country")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setCountry(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getCountry());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getStreet())&& validationErrorList.getError("registered_directors_street")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setStreet(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getStreet());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyCapitalResources())&& validationErrorList.getError("company_capitalresources")==null)
            companyProfileVO.setCompanyCapitalResources(requestCompanyProfileVO.getCompanyCapitalResources());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyTurnoverLastYear())&& validationErrorList.getError("company_turnoverlastyear")==null)
            companyProfileVO.setCompanyTurnoverLastYear(requestCompanyProfileVO.getCompanyTurnoverLastYear());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyNumberOfEmployees())&& validationErrorList.getError("company_numberofemployees")==null)
            companyProfileVO.setCompanyNumberOfEmployees(requestCompanyProfileVO.getCompanyNumberOfEmployees());

        if(functions.isValueNull(requestCompanyProfileVO.getCompany_currencylastyear())&& validationErrorList.getError("company_currencylastyear")==null)
            companyProfileVO.setCompany_currencylastyear(requestCompanyProfileVO.getCompany_currencylastyear());

        if(functions.isValueNull(requestCompanyProfileVO.getCompany_turnoverlastyear_unit())&& validationErrorList.getError("company_turnoverlastyear_unit")==null)
            companyProfileVO.setCompany_turnoverlastyear_unit(requestCompanyProfileVO.getCompany_turnoverlastyear_unit());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getName())&& validationErrorList.getError("cbk_contactperson")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getEmailaddress())&& validationErrorList.getError("cbk_email")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getEmailaddress());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getTelephonenumber())&& validationErrorList.getError("cbk_telephonenumber")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getPhonecc1())&& validationErrorList.getError("cbk_phonecc")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getDesignation())&& validationErrorList.getError("cbk_designation")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.CBK).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getName())&& validationErrorList.getError("pci_contactperson")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setName(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getName());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getEmailaddress())&& validationErrorList.getError("pci_email")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setEmailaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getEmailaddress());

        if (functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getTelephonenumber())&& validationErrorList.getError("pci_telephonenumber")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getPhonecc1())&& validationErrorList.getError("pci_phonecc")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getDesignation())&& validationErrorList.getError("pci_designation")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.PCI).getDesignation());

        //add Specific
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getTelephonenumber())&& validationErrorList.getError("Technical_telephonenumber")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getDesignation())&& validationErrorList.getError("technical_designation")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getTelephonenumber())&& validationErrorList.getError("Financial_telephonenumber")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setTelephonenumber(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getTelephonenumber());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getDesignation())&& validationErrorList.getError("billing_designation")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setDesignation(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getDesignation());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration())&& validationErrorList.getError("Company_Date_Registration")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setDate_of_registration(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration());

        if(functions.isValueNull(requestCompanyProfileVO.getLicense_required())&& validationErrorList.getError("License_required")==null)
            companyProfileVO.setLicense_required(requestCompanyProfileVO.getLicense_required());
        if(functions.isValueNull(requestCompanyProfileVO.getLicense_Permission())&& validationErrorList.getError("License_Permission")==null)
            companyProfileVO.setLicense_Permission(requestCompanyProfileVO.getLicense_Permission());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getPhonecc1())&& validationErrorList.getError("technicalphonecc1")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.TECHNICAL).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getPhonecc1())&& validationErrorList.getError("financialphonecc1")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).setPhonecc1(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.BILLING).getPhonecc1());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc())&& validationErrorList.getError("Companyphonecc1")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc());

        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number())&& validationErrorList.getError("CompanyTelephoneNO")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax())&& validationErrorList.getError("CompanyFax")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFax(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFax());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id())&& validationErrorList.getError("CompanyEmailAddress")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setEmail_id(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id());

        //Add Specific
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId())&& validationErrorList.getError("FederalTaxID")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFederalTaxId(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number())&& validationErrorList.getError("EURegistrationNumber")==null)
            companyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).setRegistration_number(requestCompanyProfileVO.getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.EU_COMPANY).getRegistration_number());

        //ADD new
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getSkypeIMaddress())&& validationErrorList.getError("SkypeIMaddress")==null)
            companyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setSkypeIMaddress(requestCompanyProfileVO.getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getSkypeIMaddress());

        if(functions.isValueNull(requestCompanyProfileVO.getLegalProceeding())&& validationErrorList.getError("legal_proceeding")==null)
            companyProfileVO.setLegalProceeding(requestCompanyProfileVO.getLegalProceeding());

        if(functions.isValueNull(requestCompanyProfileVO.getStartup_business())&& validationErrorList.getError("startup_business")==null)
            companyProfileVO.setStartup_business(requestCompanyProfileVO.getStartup_business());
        if(functions.isValueNull(requestCompanyProfileVO.getCompanyLengthOfTimeInBusiness())&& validationErrorList.getError("company_lengthoftime_business")==null)
            companyProfileVO.setCompanyLengthOfTimeInBusiness(requestCompanyProfileVO.getCompanyLengthOfTimeInBusiness());
        if(functions.isValueNull(requestCompanyProfileVO.getMain_business_partner())&& validationErrorList.getError("main_business_partner") == null)
            companyProfileVO.setMain_business_partner(requestCompanyProfileVO.getMain_business_partner());
        if(functions.isValueNull(requestCompanyProfileVO.getLoans())&& validationErrorList.getError("loans") == null)
            companyProfileVO.setLoans(requestCompanyProfileVO.getLoans());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_economic_activity())&& validationErrorList.getError("income_economic_activity") == null)
            companyProfileVO.setIncome_economic_activity(requestCompanyProfileVO.getIncome_economic_activity());
        if(functions.isValueNull(requestCompanyProfileVO.getInterest_income())&& validationErrorList.getError("interest_income") == null)
            companyProfileVO.setInterest_income(requestCompanyProfileVO.getInterest_income());
        if(functions.isValueNull(requestCompanyProfileVO.getInvestments())&& validationErrorList.getError("investments") == null)
            companyProfileVO.setInvestments(requestCompanyProfileVO.getInvestments());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_sources_other())&& validationErrorList.getError("income_sources_other") == null)
            companyProfileVO.setIncome_sources_other(requestCompanyProfileVO.getIncome_sources_other());
        if(functions.isValueNull(requestCompanyProfileVO.getIncome_sources_other_yes())&& validationErrorList.getError("income_sources_other_yes") == null)
            companyProfileVO.setIncome_sources_other_yes(requestCompanyProfileVO.getIncome_sources_other_yes());

    }

    private void setOwnershipProfileVO(HttpServletRequest request,OwnershipProfileVO ownershipProfileVO)
    {
        ownershipProfileVO.setNumOfShareholders(request.getParameter("numOfShareholders"));
        ownershipProfileVO.setNumOfCorporateShareholders(request.getParameter("numOfCorporateShareholders"));
        ownershipProfileVO.setNumOfDirectors(request.getParameter("numOfDirectors"));
        ownershipProfileVO.setNumOfAuthrisedSignatory(request.getParameter("numOfAuthrisedSignatory"));

        //Add Shareholder
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setFirstname(request.getParameter("shareholderprofile1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTitle(request.getParameter("shareholderprofile1_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setOwned(request.getParameter("shareholderprofile1_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelnocc1(request.getParameter("shareholderprofile1_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelephonenumber(request.getParameter("shareholderprofile1_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setEmailaddress(request.getParameter("shareholderprofile1_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setDateofbirth(request.getParameter("shareholderprofile1_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtypeselect(request.getParameter("shareholderprofile1_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtype(request.getParameter("shareholderprofile1_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setState(request.getParameter("shareholderprofile1_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddress(request.getParameter("shareholderprofile1_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCity(request.getParameter("shareholderprofile1_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setZipcode(request.getParameter("shareholderprofile1_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(request.getParameter("shareholderprofile1_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setStreet(request.getParameter("shareholderprofile1_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setNationality(request.getParameter("shareholderprofile1_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportexpirydate(request.getParameter("shareholderprofile1_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportissuedate(request.getParameter("shareholderprofile1_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPoliticallyexposed(request.getParameter("shareholderprofile1_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCriminalrecord(request.getParameter("shareholderprofile1_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressProof(request.getParameter("shareholderprofile1_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressId(request.getParameter("shareholderprofile1_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setLastname(request.getParameter("shareholderprofile1_lastname"));

        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setFirstname(request.getParameter("shareholderprofile2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTitle(request.getParameter("shareholderprofile2_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setOwned(request.getParameter("shareholderprofile2_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelnocc1(request.getParameter("shareholderprofile2_telnocc2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelephonenumber(request.getParameter("shareholderprofile2_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setEmailaddress(request.getParameter("shareholderprofile2_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setDateofbirth(request.getParameter("shareholderprofile2_dateofbirth"));
        //ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).set(request.getParameter("shareholderprofile2_socialsecurity"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtypeselect(request.getParameter("shareholderprofile2_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtype(request.getParameter("shareholderprofile2_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setState(request.getParameter("shareholderprofile2_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddress(request.getParameter("shareholderprofile2_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCity(request.getParameter("shareholderprofile2_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setZipcode(request.getParameter("shareholderprofile2_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(request.getParameter("shareholderprofile2_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setStreet(request.getParameter("shareholderprofile2_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setNationality(request.getParameter("shareholderprofile2_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportexpirydate(request.getParameter("shareholderprofile2_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportissuedate(request.getParameter("shareholderprofile2_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPoliticallyexposed(request.getParameter("shareholderprofile2_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCriminalrecord(request.getParameter("shareholderprofile2_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressProof(request.getParameter("shareholderprofile2_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressId(request.getParameter("shareholderprofile2_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setLastname(request.getParameter("shareholderprofile2_lastname"));

        //Add specific shareholder profile 3
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setFirstname(request.getParameter("shareholderprofile3"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTitle(request.getParameter("shareholderprofile3_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setOwned(request.getParameter("shareholderprofile3_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelnocc1(request.getParameter("shareholderprofile3_telnocc2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelephonenumber(request.getParameter("shareholderprofile3_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setEmailaddress(request.getParameter("shareholderprofile3_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setDateofbirth(request.getParameter("shareholderprofile3_dateofbirth"));
        //ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).se(request.getParameter("shareholderprofile3_socialsecurity"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtypeselect(request.getParameter("shareholderprofile3_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtype(request.getParameter("shareholderprofile3_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setState(request.getParameter("shareholderprofile3_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddress(request.getParameter("shareholderprofile3_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCity(request.getParameter("shareholderprofile3_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setZipcode(request.getParameter("shareholderprofile3_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCountry(request.getParameter("shareholderprofile3_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setStreet(request.getParameter("shareholderprofile3_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setNationality(request.getParameter("shareholderprofile3_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportexpirydate(request.getParameter("shareholderprofile3_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportissuedate(request.getParameter("shareholderprofile3_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPoliticallyexposed(request.getParameter("shareholderprofile3_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCriminalrecord(request.getParameter("shareholderprofile3_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressProof(request.getParameter("shareholderprofile3_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressId(request.getParameter("shareholderprofile3_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setLastname(request.getParameter("shareholderprofile3_lastname"));

        //Add specific shareholder profile 4
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setFirstname(request.getParameter("shareholderprofile4"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTitle(request.getParameter("shareholderprofile4_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setOwned(request.getParameter("shareholderprofile4_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelnocc1(request.getParameter("shareholderprofile4_telnocc2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelephonenumber(request.getParameter("shareholderprofile4_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setEmailaddress(request.getParameter("shareholderprofile4_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setDateofbirth(request.getParameter("shareholderprofile4_dateofbirth"));
        //System.out.println(" dob in appmanager 4: "+request.getParameter("shareholderprofile4_dateofbirth"));
        //System.out.println(" dob in appmanager 1: "+request.getParameter("shareholderprofile1_dateofbirth"));
        //System.out.println(" dob in appmanager 2: "+request.getParameter("shareholderprofile2_dateofbirth"));
        //System.out.println(" dob in appmanager 3: "+request.getParameter("shareholderprofile3_dateofbirth"));
        //ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).se(request.getParameter("shareholderprofile3_socialsecurity"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtypeselect(request.getParameter("shareholderprofile4_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtype(request.getParameter("shareholderprofile4_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setState(request.getParameter("shareholderprofile4_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddress(request.getParameter("shareholderprofile4_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCity(request.getParameter("shareholderprofile4_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setZipcode(request.getParameter("shareholderprofile4_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCountry(request.getParameter("shareholderprofile4_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setStreet(request.getParameter("shareholderprofile4_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setNationality(request.getParameter("shareholderprofile4_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportexpirydate(request.getParameter("shareholderprofile4_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportissuedate(request.getParameter("shareholderprofile4_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPoliticallyexposed(request.getParameter("shareholderprofile4_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCriminalrecord(request.getParameter("shareholderprofile4_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressProof(request.getParameter("shareholderprofile4_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressId(request.getParameter("shareholderprofile4_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setLastname(request.getParameter("shareholderprofile4_lastname"));

        //Add specific corporate shareholderprofile1
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setName(request.getParameter("corporateshareholder1_Name"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setRegistrationNumber(request.getParameter("corporateshareholder1_RegNumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddress(request.getParameter("corporateshareholder1_Address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCity(request.getParameter("corporateshareholder1_City"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setState(request.getParameter("corporateshareholder1_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setZipcode(request.getParameter("corporateshareholder1_ZipCode"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(request.getParameter("corporateshareholder1_Country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setStreet(request.getParameter("corporateshareholder1_Street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setOwned(request.getParameter("corporateshareholder1_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressProof(request.getParameter("corporateshareholder1_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressId(request.getParameter("corporateshareholder1_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtypeselect(request.getParameter("corporateshareholder1_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtype(request.getParameter("corporateshareholder1_identificationtype"));

        //Add specific corporate shareholderprofile2
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setName(request.getParameter("corporateshareholder2_Name"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setRegistrationNumber(request.getParameter("corporateshareholder2_RegNumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddress(request.getParameter("corporateshareholder2_Address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCity(request.getParameter("corporateshareholder2_City"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setState(request.getParameter("corporateshareholder2_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setZipcode(request.getParameter("corporateshareholder2_ZipCode"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(request.getParameter("corporateshareholder2_Country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setStreet(request.getParameter("corporateshareholder2_Street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setOwned(request.getParameter("corporateshareholder2_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressProof(request.getParameter("corporateshareholder2_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressId(request.getParameter("corporateshareholder2_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtypeselect(request.getParameter("corporateshareholder2_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtype(request.getParameter("corporateshareholder2_identificationtype"));

        //Add specific corporate shareholderprofile3
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setName(request.getParameter("corporateshareholder3_Name"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setRegistrationNumber(request.getParameter("corporateshareholder3_RegNumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddress(request.getParameter("corporateshareholder3_Address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCity(request.getParameter("corporateshareholder3_City"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setState(request.getParameter("corporateshareholder3_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setZipcode(request.getParameter("corporateshareholder3_ZipCode"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(request.getParameter("corporateshareholder3_Country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setStreet(request.getParameter("corporateshareholder3_Street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setOwned(request.getParameter("corporateshareholder3_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressProof(request.getParameter("corporateshareholder3_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressId(request.getParameter("corporateshareholder3_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtypeselect(request.getParameter("corporateshareholder3_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtype(request.getParameter("corporateshareholder3_identificationtype"));

        //Add specific corporate shareholderprofile4
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setName(request.getParameter("corporateshareholder4_Name"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setRegistrationNumber(request.getParameter("corporateshareholder4_RegNumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddress(request.getParameter("corporateshareholder4_Address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCity(request.getParameter("corporateshareholder4_City"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setState(request.getParameter("corporateshareholder4_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setZipcode(request.getParameter("corporateshareholder4_ZipCode"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(request.getParameter("corporateshareholder4_Country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setStreet(request.getParameter("corporateshareholder4_Street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setOwned(request.getParameter("corporateshareholder4_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressProof(request.getParameter("corporateshareholder4_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressId(request.getParameter("corporateshareholder4_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtypeselect(request.getParameter("corporateshareholder4_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtype(request.getParameter("corporateshareholder4_identificationtype"));


        //directors profile
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setFirstname(request.getParameter("directorsprofile"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTitle(request.getParameter("directorsprofile_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelnocc1(request.getParameter("directorsprofile_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelephonenumber(request.getParameter("directorsprofile_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setEmailaddress(request.getParameter("directorsprofile_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setDateofbirth(request.getParameter("directorsprofile_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtypeselect(request.getParameter("directorsprofile_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtype(request.getParameter("directorsprofile_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setState(request.getParameter("directorsprofile_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddress(request.getParameter("directorsprofile_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCity(request.getParameter("directorsprofile_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setZipcode(request.getParameter("directorsprofile_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(request.getParameter("directorsprofile_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setStreet(request.getParameter("directorsprofile_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setNationality(request.getParameter("directorsprofile_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportexpirydate(request.getParameter("directorsprofile_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportissuedate(request.getParameter("directorsprofile_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPoliticallyexposed(request.getParameter("directorsprofile_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCriminalrecord(request.getParameter("directorsprofile_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressProof(request.getParameter("directorsprofile_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressId(request.getParameter("directorsprofile_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setLastname(request.getParameter("directorsprofile_lastname"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setOwned(request.getParameter("directorsprofile_owned"));

        //Add specific Directors profile 2
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setFirstname(request.getParameter("directorsprofile2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTitle(request.getParameter("directorsprofile2_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelnocc1(request.getParameter("directorsprofile2_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelephonenumber(request.getParameter("directorsprofile2_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setEmailaddress(request.getParameter("directorsprofile2_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setDateofbirth(request.getParameter("directorsprofile2_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtypeselect(request.getParameter("directorsprofile2_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtype(request.getParameter("directorsprofile2_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setState(request.getParameter("directorsprofile2_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddress(request.getParameter("directorsprofile2_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCity(request.getParameter("directorsprofile2_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setZipcode(request.getParameter("directorsprofile2_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(request.getParameter("directorsprofile2_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setStreet(request.getParameter("directorsprofile2_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setNationality(request.getParameter("directorsprofile2_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportexpirydate(request.getParameter("directorsprofile2_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportissuedate(request.getParameter("directorsprofile2_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPoliticallyexposed(request.getParameter("directorsprofile2_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCriminalrecord(request.getParameter("directorsprofile2_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressProof(request.getParameter("directorsprofile2_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressId(request.getParameter("directorsprofile2_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setLastname(request.getParameter("directorsprofile2_lastname"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setOwned(request.getParameter("directorsprofile2_owned"));

        //Add specific Director Profile 3
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setFirstname(request.getParameter("directorsprofile3"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTitle(request.getParameter("directorsprofile3_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelnocc1(request.getParameter("directorsprofile3_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelephonenumber(request.getParameter("directorsprofile3_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setEmailaddress(request.getParameter("directorsprofile3_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setDateofbirth(request.getParameter("directorsprofile3_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtypeselect(request.getParameter("directorsprofile3_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtype(request.getParameter("directorsprofile3_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setState(request.getParameter("directorsprofile3_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddress(request.getParameter("directorsprofile3_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCity(request.getParameter("directorsprofile3_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setZipcode(request.getParameter("directorsprofile3_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCountry(request.getParameter("directorsprofile3_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setStreet(request.getParameter("directorsprofile3_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setNationality(request.getParameter("directorsprofile3_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportexpirydate(request.getParameter("directorsprofile3_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportissuedate(request.getParameter("directorsprofile3_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPoliticallyexposed(request.getParameter("directorsprofile3_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCriminalrecord(request.getParameter("directorsprofile3_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressProof(request.getParameter("directorsprofile3_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressId(request.getParameter("directorsprofile3_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setLastname(request.getParameter("directorsprofile3_lastname"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setOwned(request.getParameter("directorsprofile3_owned"));

        //Add specific Director Profile 4
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setFirstname(request.getParameter("directorsprofile4"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTitle(request.getParameter("directorsprofile4_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelnocc1(request.getParameter("directorsprofile4_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelephonenumber(request.getParameter("directorsprofile4_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setEmailaddress(request.getParameter("directorsprofile4_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setDateofbirth(request.getParameter("directorsprofile4_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtypeselect(request.getParameter("directorsprofile4_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtype(request.getParameter("directorsprofile4_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setState(request.getParameter("directorsprofile4_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddress(request.getParameter("directorsprofile4_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCity(request.getParameter("directorsprofile4_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setZipcode(request.getParameter("directorsprofile4_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCountry(request.getParameter("directorsprofile4_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setStreet(request.getParameter("directorsprofile4_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setNationality(request.getParameter("directorsprofile4_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportexpirydate(request.getParameter("directorsprofile4_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportissuedate(request.getParameter("directorsprofile4_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPoliticallyexposed(request.getParameter("directorsprofile4_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCriminalrecord(request.getParameter("directorsprofile4_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressProof(request.getParameter("directorsprofile4_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressId(request.getParameter("directorsprofile4_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setLastname(request.getParameter("directorsprofile4_lastname"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setOwned(request.getParameter("directorsprofile4_owned"));

        //authorizedsignatoryprofile
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setFirstname(request.getParameter("authorizedsignatoryprofile"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTitle(request.getParameter("authorizedsignatoryprofile_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelnocc1(request.getParameter("authorizedsignatoryprofile_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelephonenumber(request.getParameter("authorizedsignatoryprofile_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setEmailaddress(request.getParameter("authorizedsignatoryprofile_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDateofbirth(request.getParameter("authorizedsignatoryprofile_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtypeselect(request.getParameter("authorizedsignatoryprofile_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtype(request.getParameter("authorizedsignatoryprofile_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setState(request.getParameter("authorizedsignatoryprofile_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddress(request.getParameter("authorizedsignatoryprofile_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCity(request.getParameter("authorizedsignatoryprofile_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setZipcode(request.getParameter("authorizedsignatoryprofile_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(request.getParameter("authorizedsignatoryprofile_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setStreet(request.getParameter("authorizedsignatoryprofile_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setNationality(request.getParameter("authorizedsignatoryprofile_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportexpirydate(request.getParameter("authorizedsignatoryprofile_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportissuedate(request.getParameter("authorizedsignatoryprofile_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPoliticallyexposed(request.getParameter("authorizedsignatoryprofile1_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCriminalrecord(request.getParameter("authorizedsignatoryprofile1_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDesignation(request.getParameter("authorizedsignatoryprofile1_designation"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressProof(request.getParameter("authorizedsignatoryprofile1_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressId(request.getParameter("authorizedsignatoryprofile1_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setOwned(request.getParameter("authorizedsignatoryprofile1_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setLastname(request.getParameter("authorizedsignatoryprofile_lastname"));

        //authorize signatory profile2
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setFirstname(request.getParameter("authorizedsignatoryprofile2"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTitle(request.getParameter("authorizedsignatoryprofile2_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelnocc1(request.getParameter("authorizedsignatoryprofile2_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelephonenumber(request.getParameter("authorizedsignatoryprofile2_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setEmailaddress(request.getParameter("authorizedsignatoryprofile2_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDateofbirth(request.getParameter("authorizedsignatoryprofile2_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtypeselect(request.getParameter("authorizedsignatoryprofile2_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtype(request.getParameter("authorizedsignatoryprofile2_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setState(request.getParameter("authorizedsignatoryprofile2_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddress(request.getParameter("authorizedsignatoryprofile2_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCity(request.getParameter("authorizedsignatoryprofile2_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setZipcode(request.getParameter("authorizedsignatoryprofile2_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(request.getParameter("authorizedsignatoryprofile2_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setStreet(request.getParameter("authorizedsignatoryprofile2_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setNationality(request.getParameter("authorizedsignatoryprofile2_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportexpirydate(request.getParameter("authorizedsignatoryprofile2_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportissuedate(request.getParameter("authorizedsignatoryprofile2_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPoliticallyexposed(request.getParameter("authorizedsignatoryprofile2_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCriminalrecord(request.getParameter("authorizedsignatoryprofile2_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDesignation(request.getParameter("authorizedsignatoryprofile2_designation"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressProof(request.getParameter("authorizedsignatoryprofile2_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressId(request.getParameter("authorizedsignatoryprofile2_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setOwned(request.getParameter("authorizedsignatoryprofile2_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setLastname(request.getParameter("authorizedsignatoryprofile2_lastname"));

        //authorize signatory profile3
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setFirstname(request.getParameter("authorizedsignatoryprofile3"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTitle(request.getParameter("authorizedsignatoryprofile3_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelnocc1(request.getParameter("authorizedsignatoryprofile3_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelephonenumber(request.getParameter("authorizedsignatoryprofile3_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setEmailaddress(request.getParameter("authorizedsignatoryprofile3_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDateofbirth(request.getParameter("authorizedsignatoryprofile3_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtypeselect(request.getParameter("authorizedsignatoryprofile3_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtype(request.getParameter("authorizedsignatoryprofile3_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setState(request.getParameter("authorizedsignatoryprofile3_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddress(request.getParameter("authorizedsignatoryprofile3_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCity(request.getParameter("authorizedsignatoryprofile3_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setZipcode(request.getParameter("authorizedsignatoryprofile3_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(request.getParameter("authorizedsignatoryprofile3_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setStreet(request.getParameter("authorizedsignatoryprofile3_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setNationality(request.getParameter("authorizedsignatoryprofile3_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportexpirydate(request.getParameter("authorizedsignatoryprofile3_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportissuedate(request.getParameter("authorizedsignatoryprofile3_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPoliticallyexposed(request.getParameter("authorizedsignatoryprofile3_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCriminalrecord(request.getParameter("authorizedsignatoryprofile3_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDesignation(request.getParameter("authorizedsignatoryprofile3_designation"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressProof(request.getParameter("authorizedsignatoryprofile3_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressId(request.getParameter("authorizedsignatoryprofile3_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setOwned(request.getParameter("authorizedsignatoryprofile3_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setLastname(request.getParameter("authorizedsignatoryprofile3_lastname"));

        //authorize signatory profile4
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setFirstname(request.getParameter("authorizedsignatoryprofile4"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTitle(request.getParameter("authorizedsignatoryprofile4_title"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelnocc1(request.getParameter("authorizedsignatoryprofile4_telnocc1"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelephonenumber(request.getParameter("authorizedsignatoryprofile4_telephonenumber"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setEmailaddress(request.getParameter("authorizedsignatoryprofile4_emailaddress"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDateofbirth(request.getParameter("authorizedsignatoryprofile4_dateofbirth"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtypeselect(request.getParameter("authorizedsignatoryprofile4_identificationtypeselect"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtype(request.getParameter("authorizedsignatoryprofile4_identificationtype"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setState(request.getParameter("authorizedsignatoryprofile4_State"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddress(request.getParameter("authorizedsignatoryprofile4_address"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCity(request.getParameter("authorizedsignatoryprofile4_city"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setZipcode(request.getParameter("authorizedsignatoryprofile4_zip"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(request.getParameter("authorizedsignatoryprofile4_country"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setStreet(request.getParameter("authorizedsignatoryprofile4_street"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setNationality(request.getParameter("authorizedsignatoryprofile4_nationality"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportexpirydate(request.getParameter("authorizedsignatoryprofile4_Passportexpirydate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportissuedate(request.getParameter("authorizedsignatoryprofile4_passportissuedate"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPoliticallyexposed(request.getParameter("authorizedsignatoryprofile4_politicallyexposed"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCriminalrecord(request.getParameter("authorizedsignatoryprofile4_criminalrecord"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDesignation(request.getParameter("authorizedsignatoryprofile4_designation"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressProof(request.getParameter("authorizedsignatoryprofile4_addressproof"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressId(request.getParameter("authorizedsignatoryprofile4_addressId"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setOwned(request.getParameter("authorizedsignatoryprofile4_owned"));
        ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setLastname(request.getParameter("authorizedsignatoryprofile4_lastname"));

    }

    //set Ownership Profile From API
    private void setOwnershipProfileVO(OwnershipProfileVO requestOwnershipProfileVO,OwnershipProfileVO ownershipProfileVO)
    {
        if(functions.isValueNull(requestOwnershipProfileVO.getNumOfShareholders()))
            ownershipProfileVO.setNumOfShareholders(requestOwnershipProfileVO.getNumOfShareholders());
        if(functions.isValueNull(requestOwnershipProfileVO.getNumOfCorporateShareholders()))
            ownershipProfileVO.setNumOfShareholders(requestOwnershipProfileVO.getNumOfCorporateShareholders());
        if(functions.isValueNull(requestOwnershipProfileVO.getNumOfDirectors()))
            ownershipProfileVO.setNumOfShareholders(requestOwnershipProfileVO.getNumOfDirectors());
        if(functions.isValueNull(requestOwnershipProfileVO.getNumOfAuthrisedSignatory()))
            ownershipProfileVO.setNumOfShareholders(requestOwnershipProfileVO.getNumOfAuthrisedSignatory());

        //Add Shareholder
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(commonFunctionUtil.getCountryDetailsForAPI(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry()));
            if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressId());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setNationality(commonFunctionUtil.getCountryDetailsForAPI(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth());
        /*if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getS()))
            ownershipProfileVO.setShareholderprofile2_socialsecurity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate());*/
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressId());

        //Add specific shareholder profile 3
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth());
        /*if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId()))
            ownershipProfileVO.setShareholderprofile3_socialsecurity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId());*/
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getLastname());

        //Add specific shareholder profile 4
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth());
        /*if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId()))
            ownershipProfileVO.setShareholderprofile3_socialsecurity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId());*/
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getLastname());

        //Add specific corporate shareholderprofile1
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getRegistrationNumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getRegistrationNumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry());
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtype());


        //Add specific corporate shareholderprofile2
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getRegistrationNumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getRegistrationNumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry());
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtype());

        //Add specific corporate corporateShareholderProfile3
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getRegistrationNumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getRegistrationNumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry());
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtype());

        //Add specific corporate corporateShareholderProfile4
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getRegistrationNumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getRegistrationNumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getOwned());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry());
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressId());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtype());

        //directors profile
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelephonenumber());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressId());

        //Add specific Directors profile 2
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelephonenumber());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getEmailaddress());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtypeselect());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelnocc1()) )
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressId());

        //Add specific Director Profile 3
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelnocc1()) )
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry().split("\\|")[1]);
            else
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCountry(country);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressId());

        //Add specific Director Profile 4
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTitle());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelnocc1());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelephonenumber());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtype());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getState());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCity());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelnocc1()) )
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry().split("\\|")[1]);
            else
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCountry(country);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPoliticallyexposed());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCriminalrecord());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressId());




//authorizedsignatoryprofile Start
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getFirstname());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTitle());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelnocc1());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelephonenumber());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getEmailaddress());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtypeselect());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtype());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getState());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddress());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCity());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getZipcode());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPoliticallyexposed());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCriminalrecord());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDesignation()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDesignation());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressProof());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressId());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getOwned());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getLastname());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned());

        //authorize signatory profile2
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getFirstname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTitle());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelnocc1());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelephonenumber());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getEmailaddress());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtypeselect());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtype());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getState());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddress());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCity());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getZipcode());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry()));
            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelnocc1()))
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelnocc1(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getStreet());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPoliticallyexposed());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCriminalrecord());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDesignation()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDesignation());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressProof());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressId());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname());


        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getOwned());

        //authorize signatory profile3

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getZipcode());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry()));

            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1()) )
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getStreet());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDesignation()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDesignation());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressId());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getLastname());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getOwned());

        //authorize signatory profile4

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getFirstname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTitle()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelnocc1()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelephonenumber()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getEmailaddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDateofbirth()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtypeselect()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtype()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getState()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddress()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCity()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getZipcode()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getZipcode());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry()))
        {
            String country = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry()));

            if(functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry()) && !functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelnocc1()) )
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry());
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry().split("\\|")[1]);
            }
            else
            {
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(country);
            }
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getStreet()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getStreet());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality()))
        {
            String nationality = requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality();
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality());
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setNationality(commonFunctionUtil.getCountryDetailsForAPI(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality()));
            if(!functions.isValueNull(ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality()))
                ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setNationality(nationality);
        }
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportexpirydate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportissuedate()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPoliticallyexposed()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCriminalrecord()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDesignation()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDesignation());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressProof()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressId()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressId());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getLastname());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned());

        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getOwned()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getOwned());

        //ADD new
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getLastname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getLastname());

        //ADD new
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getLastname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getLastname());
        if(functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getLastname()))
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getLastname());


    }

    private void setOwnershipProfileVO(OwnershipProfileVO requestOwnershipProfileVO,OwnershipProfileVO ownershipProfileVO,ValidationErrorList validationErrorList)
    {
        //Add Shareholder
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getFirstname()) && validationErrorList.getError("shareholderprofile1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTitle()) && validationErrorList.getError("shareholderprofile1_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned()) && validationErrorList.getError("shareholderprofile1_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelnocc1()) && validationErrorList.getError("shareholderprofile1_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelephonenumber()) && validationErrorList.getError("shareholderprofile1_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getEmailaddress()) && validationErrorList.getError("shareholderprofile1_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth()) && validationErrorList.getError("shareholderprofile1_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtypeselect()) && validationErrorList.getError("shareholderprofile1_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtype()) && validationErrorList.getError("shareholderprofile1_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getState()) && validationErrorList.getError("shareholderprofile1_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddress()) && validationErrorList.getError("shareholderprofile1_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCity()) && validationErrorList.getError("shareholderprofile1_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getZipcode()) && validationErrorList.getError("shareholderprofile1_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry()) && validationErrorList.getError("shareholderprofile1_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getStreet()) && validationErrorList.getError("shareholderprofile1_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality()) && validationErrorList.getError("shareholderprofile1_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getNationality());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate()) && validationErrorList.getError("shareholderprofile1_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate()) && validationErrorList.getError("shareholderprofile1_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPoliticallyexposed()) && validationErrorList.getError("shareholderprofile1_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCriminalrecord()) && validationErrorList.getError("shareholderprofile1_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressProof()) && validationErrorList.getError("shareholderprofile1_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressId()) && validationErrorList.getError("shareholderprofile1_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getAddressId());

        //shareholder profile 2

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getFirstname()) && validationErrorList.getError("shareholderprofile2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTitle()) && validationErrorList.getError("shareholderprofile2_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getOwned()) && validationErrorList.getError("shareholderprofile2_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelnocc1()) && validationErrorList.getError("shareholderprofile2_telnocc2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelephonenumber()) && validationErrorList.getError("shareholderprofile2_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getEmailaddress()) && validationErrorList.getError("shareholderprofile2_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth()) && validationErrorList.getError("shareholderprofile2_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtypeselect()) && validationErrorList.getError("shareholderprofile2_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtype()) && validationErrorList.getError("shareholderprofile2_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getState()) && validationErrorList.getError("shareholderprofile2_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddress()) && validationErrorList.getError("getShareholderprofile2_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCity()) && validationErrorList.getError("shareholderprofile2_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getZipcode()) && validationErrorList.getError("shareholderprofile2_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry()) && validationErrorList.getError("shareholderprofile2_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getStreet()) && validationErrorList.getError("shareholderprofile2_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality()) && validationErrorList.getError("shareholderprofile2_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getNationality());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate()) && validationErrorList.getError("shareholderprofile2_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate()) && validationErrorList.getError("shareholderprofile2_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPoliticallyexposed()) && validationErrorList.getError("shareholderprofile2_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCriminalrecord()) && validationErrorList.getError("shareholderprofile2_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressProof()) && validationErrorList.getError("shareholderprofile2_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressId()) && validationErrorList.getError("shareholderprofile2_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getAddressId());


        //Add specific shareholder profile 3
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getFirstname()) && validationErrorList.getError("shareholderprofile3") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTitle()) && validationErrorList.getError("shareholderprofile3_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getOwned()) && validationErrorList.getError("shareholderprofile3_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelnocc1()) && validationErrorList.getError("shareholderprofile3_telnocc2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelephonenumber()) && validationErrorList.getError("shareholderprofile3_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getEmailaddress()) && validationErrorList.getError("shareholderprofile3_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth()) && validationErrorList.getError("shareholderprofile3_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtypeselect()) && validationErrorList.getError("shareholderprofile3_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtype()) && validationErrorList.getError("shareholderprofile3_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getState()) && validationErrorList.getError("shareholderprofile3_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddress()) && validationErrorList.getError("shareholderprofile3_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCity()) && validationErrorList.getError("shareholderprofile3_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getZipcode()) && validationErrorList.getError("shareholderprofile3_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry()) && validationErrorList.getError("shareholderprofile3_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getStreet()) && validationErrorList.getError("shareholderprofile3_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality()) && validationErrorList.getError("shareholderprofile3_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getNationality());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate()) && validationErrorList.getError("shareholderprofile3_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate()) && validationErrorList.getError("shareholderprofile3_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPoliticallyexposed()) && validationErrorList.getError("shareholderprofile3_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCriminalrecord()) && validationErrorList.getError("shareholderprofile3_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressProof()) && validationErrorList.getError("shareholderprofile3_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId()) && validationErrorList.getError("shareholderprofile3_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getAddressId());

        //Add specific shareholder profile 4
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getFirstname()) && validationErrorList.getError("shareholderprofile4") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTitle()) && validationErrorList.getError("shareholderprofile4_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getOwned()) && validationErrorList.getError("shareholderprofile4_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelnocc1()) && validationErrorList.getError("shareholderprofile4_telnocc2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelnocc1());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelephonenumber()) && validationErrorList.getError("shareholderprofile4_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getTelephonenumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getEmailaddress()) && validationErrorList.getError("shareholderprofile4_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getEmailaddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth()) && validationErrorList.getError("shareholderprofile4_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getDateofbirth());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtypeselect()) && validationErrorList.getError("shareholderprofile4_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtypeselect());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtype()) && validationErrorList.getError("shareholderprofile4_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getState()) && validationErrorList.getError("shareholderprofile4_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddress()) && validationErrorList.getError("shareholderprofile4_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCity()) && validationErrorList.getError("shareholderprofile4_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getZipcode()) && validationErrorList.getError("shareholderprofile4_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry()) && validationErrorList.getError("shareholderprofile4_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getStreet()) && validationErrorList.getError("shareholderprofile4_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality()) && validationErrorList.getError("shareholderprofile4_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getNationality());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate()) && validationErrorList.getError("shareholderprofile4_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportexpirydate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate()) && validationErrorList.getError("shareholderprofile4_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPoliticallyexposed()) && validationErrorList.getError("shareholderprofile4_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCriminalrecord()) && validationErrorList.getError("shareholderprofile4_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressProof()) && validationErrorList.getError("shareholderprofile4_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId()) && validationErrorList.getError("shareholderprofile4_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getAddressId());

        //Add specific corporate shareholderprofile1
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getName()) && validationErrorList.getError("corporateshareholder1_Name") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setName(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getName());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getRegistrationNumber()) && validationErrorList.getError("corporateshareholder1_RegNumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getRegistrationNumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddress()) && validationErrorList.getError("corporateshareholder1_Address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCity()) && validationErrorList.getError("corporateshareholder1_City") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getState()) && validationErrorList.getError("corporateshareholder1_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getZipcode()) && validationErrorList.getError("corporateshareholder1_ZipCode") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry()) && validationErrorList.getError("corporateshareholder1_Country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getStreet()) && validationErrorList.getError("corporateshareholder1_Street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getOwned()) && validationErrorList.getError("corporateshareholder1_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressProof()) && validationErrorList.getError("corporateshareholder1_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressId()) && validationErrorList.getError("corporateshareholder1_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getAddressId());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtypeselect()) && validationErrorList.getError("corporateshareholder1_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtype()) && validationErrorList.getError("corporateshareholder1_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER1).getIdentificationtype());

        //Add specific corporate shareholderprofile2
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getName()) && validationErrorList.getError("corporateshareholder2_Name") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setName(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getName());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getRegistrationNumber()) && validationErrorList.getError("corporateshareholder2_RegNumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getRegistrationNumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddress()) && validationErrorList.getError("corporateshareholder2_Address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCity()) && validationErrorList.getError("corporateshareholder2_City") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCity());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getState()) && validationErrorList.getError("corporateshareholder2_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getState());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getZipcode()) && validationErrorList.getError("corporateshareholder2_ZipCode") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getZipcode());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry()) && validationErrorList.getError("corporateshareholder2_Country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getStreet()) && validationErrorList.getError("corporateshareholder2_Street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getOwned()) && validationErrorList.getError("corporateshareholder2_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getOwned());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressProof()) && validationErrorList.getError("corporateshareholder2_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressId()) && validationErrorList.getError("corporateshareholder2_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getAddressId());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtypeselect()) && validationErrorList.getError("corporateshareholder2_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtype()) && validationErrorList.getError("corporateshareholder2_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER2).getIdentificationtype());

        //Add specific corporate shareholderprofile3
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getName()) && validationErrorList.getError("corporateshareholder3_Name") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setName(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getName());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getRegistrationNumber()) && validationErrorList.getError("corporateshareholder3_RegNumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getRegistrationNumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddress()) && validationErrorList.getError("corporateshareholder3_Address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCity()) && validationErrorList.getError("corporateshareholder3_City") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getState()) && validationErrorList.getError("corporateshareholder3_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getZipcode()) && validationErrorList.getError("corporateshareholder3_ZipCode") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry()) && validationErrorList.getError("corporateshareholder3_Country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getStreet()) && validationErrorList.getError("corporateshareholder3_Street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getOwned()) && validationErrorList.getError("corporateshareholder3_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getOwned());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressProof()) && validationErrorList.getError("corporateshareholder3_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressId()) && validationErrorList.getError("corporateshareholder3_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtypeselect()) && validationErrorList.getError("corporateshareholder3_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtype()) && validationErrorList.getError("corporateshareholder3_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER3).getIdentificationtype());

        //Add specific corporate shareholderprofile4
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getName()) && validationErrorList.getError("corporateshareholder4_Name") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setName(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getName());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getRegistrationNumber()) && validationErrorList.getError("corporateshareholder4_RegNumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setRegistrationNumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getRegistrationNumber());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddress()) && validationErrorList.getError("corporateshareholder4_Address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddress());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCity()) && validationErrorList.getError("corporateshareholder4_City") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getState()) && validationErrorList.getError("corporateshareholder4_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getZipcode()) && validationErrorList.getError("corporateshareholder4_ZipCode") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry()) && validationErrorList.getError("corporateshareholder4_Country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getStreet()) && validationErrorList.getError("corporateshareholder4_Street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getStreet());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getOwned()) && validationErrorList.getError("corporateshareholder4_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getOwned());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressProof()) && validationErrorList.getError("corporateshareholder4_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressId()) && validationErrorList.getError("corporateshareholder4_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtypeselect()) && validationErrorList.getError("corporateshareholder4_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtype()) && validationErrorList.getError("corporateshareholder4_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.CORPORATESHAREHOLDER4).getIdentificationtype());

        //directors profile
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getFirstname()) && validationErrorList.getError("directorsprofile") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getFirstname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTitle()) && validationErrorList.getError("directorsprofile_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelnocc1()) && validationErrorList.getError("directorsprofile_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelephonenumber()) && validationErrorList.getError("directorsprofile_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getEmailaddress()) && validationErrorList.getError("directorsprofile_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth()) && validationErrorList.getError("directorsprofile_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtypeselect()) && validationErrorList.getError("directorsprofile_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtype()) && validationErrorList.getError("directorsprofile_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getState()) && validationErrorList.getError("directorsprofile_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddress()) && validationErrorList.getError("directorsprofile_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCity()) && validationErrorList.getError("directorsprofile_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getZipcode()) && validationErrorList.getError("directorsprofile_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry()) && validationErrorList.getError("directorsprofile_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getStreet()) && validationErrorList.getError("directorsprofile_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality()) && validationErrorList.getError("directorsprofile_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate()) && validationErrorList.getError("directorsprofile_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate()) && validationErrorList.getError("directorsprofile_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPoliticallyexposed()) && validationErrorList.getError("directorsprofile_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCriminalrecord()) && validationErrorList.getError("directorsprofile_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressProof()) && validationErrorList.getError("directorsprofile_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressId()) && validationErrorList.getError("directorsprofile_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getLastname()) && validationErrorList.getError("directorsprofile_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned()) && validationErrorList.getError("directorsprofile_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR1).getOwned());

        //Add specific Directors profile 2
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getFirstname()) && validationErrorList.getError("directorsprofile2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTitle()) && validationErrorList.getError("directorsprofile2_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelnocc1()) && validationErrorList.getError("directorsprofile2_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelephonenumber()) && validationErrorList.getError("directorsprofile2_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getEmailaddress()) && validationErrorList.getError("directorsprofile2_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth()) && validationErrorList.getError("directorsprofile2_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtypeselect()) && validationErrorList.getError("directorsprofile2_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtype()) && validationErrorList.getError("directorsprofile2_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getState()) && validationErrorList.getError("directorsprofile2_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddress()) && validationErrorList.getError("directorsprofile2_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCity()) && validationErrorList.getError("directorsprofile2_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getZipcode()) && validationErrorList.getError("directorsprofile2_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry()) && validationErrorList.getError("directorsprofile2_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getStreet()) && validationErrorList.getError("directorsprofile2_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality()) && validationErrorList.getError("directorsprofile2_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setNationality((requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getNationality()));
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate()) && validationErrorList.getError("directorsprofile2_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate()) && validationErrorList.getError("directorsprofile2_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPoliticallyexposed()) && validationErrorList.getError("directorsprofile2_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCriminalrecord()) && validationErrorList.getError("directorsprofile2_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressProof()) && validationErrorList.getError("directorsprofile2_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressId()) && validationErrorList.getError("directorsprofile2_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getLastname()) && validationErrorList.getError("directorsprofile2_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getOwned()) && validationErrorList.getError("directorsprofile2_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR2).getOwned());

        //Add specific Director Profile 3
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getFirstname()) && validationErrorList.getError("directorsprofile3") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTitle()) && validationErrorList.getError("directorsprofile3_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelnocc1()) && validationErrorList.getError("directorsprofile3_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelephonenumber()) && validationErrorList.getError("directorsprofile3_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getEmailaddress()) && validationErrorList.getError("directorsprofile3_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth()) && validationErrorList.getError("directorsprofile3_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtypeselect()) && validationErrorList.getError("directorsprofile3_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtype()) && validationErrorList.getError("directorsprofile3_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getState()) && validationErrorList.getError("directorsprofile3_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddress()) && validationErrorList.getError("directorsprofile3_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCity()) && validationErrorList.getError("directorsprofile3_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getZipcode()) && validationErrorList.getError("directorsprofile3_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry()) && validationErrorList.getError("directorsprofile3_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getStreet()) && validationErrorList.getError("directorsprofile3_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality()) && validationErrorList.getError("directorsprofile3_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate()) && validationErrorList.getError("directorsprofile3_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate()) && validationErrorList.getError("directorsprofile3_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPoliticallyexposed()) && validationErrorList.getError("directorsprofile3_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCriminalrecord()) && validationErrorList.getError("directorsprofile3_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressProof()) && validationErrorList.getError("directorsprofile3_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressId()) && validationErrorList.getError("directorsprofile3_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getAddressId());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getLastname()) && validationErrorList.getError("directorsprofile3_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getLastname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned()) && validationErrorList.getError("directorsprofile3_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR3).getOwned());

        //Add specific Director Profile 4
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getFirstname()) && validationErrorList.getError("directorsprofile4") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTitle()) && validationErrorList.getError("directorsprofile4_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelnocc1()) && validationErrorList.getError("directorsprofile4_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelephonenumber()) && validationErrorList.getError("directorsprofile4_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getEmailaddress()) && validationErrorList.getError("directorsprofile4_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth()) && validationErrorList.getError("directorsprofile4_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtypeselect()) && validationErrorList.getError("directorsprofile4_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtype()) && validationErrorList.getError("directorsprofile4_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getState()) && validationErrorList.getError("directorsprofile4_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddress()) && validationErrorList.getError("directorsprofile4_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCity()) && validationErrorList.getError("directorsprofile4_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getZipcode()) && validationErrorList.getError("directorsprofile4_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry()) && validationErrorList.getError("directorsprofile4_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getStreet()) && validationErrorList.getError("directorsprofile4_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality()) && validationErrorList.getError("directorsprofile4_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate()) && validationErrorList.getError("directorsprofile4_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate()) && validationErrorList.getError("directorsprofile4_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPoliticallyexposed()) && validationErrorList.getError("directorsprofile4_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCriminalrecord()) && validationErrorList.getError("directorsprofile4_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getCriminalrecord());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressProof()) && validationErrorList.getError("directorsprofile4_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressProof());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressId()) && validationErrorList.getError("directorsprofile4_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getAddressId());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getLastname()) && validationErrorList.getError("directorsprofile4_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getLastname());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned()) && validationErrorList.getError("directorsprofile4_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.DIRECTOR4).getOwned());

        //authorizedsignatoryprofile
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getFirstname()) && validationErrorList.getError("authorizedsignatoryprofile") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTitle()) && validationErrorList.getError("authorizedsignatoryprofile_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTitle());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelnocc1()) && validationErrorList.getError("authorizedsignatoryprofile_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelephonenumber()) && validationErrorList.getError("authorizedsignatoryprofile_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getEmailaddress()) && validationErrorList.getError("authorizedsignatoryprofile_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth()) && validationErrorList.getError("authorizedsignatoryprofile_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtypeselect()) && validationErrorList.getError("authorizedsignatoryprofile_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtype()) && validationErrorList.getError("authorizedsignatoryprofile_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getIdentificationtype());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getState()) && validationErrorList.getError("authorizedsignatoryprofile_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddress()) && validationErrorList.getError("authorizedsignatoryprofile_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCity()) && validationErrorList.getError("authorizedsignatoryprofile_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getZipcode()) && validationErrorList.getError("authorizedsignatoryprofile_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry()) && validationErrorList.getError("authorizedsignatoryprofile_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCountry());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getStreet()) && validationErrorList.getError("authorizedsignatoryprofile_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality()) && validationErrorList.getError("authorizedsignatoryprofile_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate()) && validationErrorList.getError("authorizedsignatoryprofile_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate()) && validationErrorList.getError("authorizedsignatoryprofile_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPassportissuedate());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPoliticallyexposed()) && validationErrorList.getError("authorizedsignatoryprofile1_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getPoliticallyexposed());

        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCriminalrecord()) && validationErrorList.getError("authorizedsignatoryprofile1_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDesignation()) && validationErrorList.getError("authorizedsignatoryprofile1_designation") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getDesignation());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressProof()) && validationErrorList.getError("authorizedsignatoryprofile1_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressId()) && validationErrorList.getError("authorizedsignatoryprofile1_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getOwned()) && validationErrorList.getError("authorizedsignatoryprofile1_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getOwned());

        //authorize signatory profile2
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getFirstname()) && validationErrorList.getError("authorizedsignatoryprofile2") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTitle()) && validationErrorList.getError("authorizedsignatoryprofile2_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelnocc1()) && validationErrorList.getError("authorizedsignatoryprofile2_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelephonenumber()) && validationErrorList.getError("authorizedsignatoryprofile2_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getEmailaddress()) && validationErrorList.getError("authorizedsignatoryprofile2_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth()) && validationErrorList.getError("authorizedsignatoryprofile2_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtypeselect()) && validationErrorList.getError("authorizedsignatoryprofile2_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtype()) && validationErrorList.getError("directorsprofile3_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getState()) && validationErrorList.getError("authorizedsignatoryprofile2_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddress()) && validationErrorList.getError("authorizedsignatoryprofile2_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCity()) && validationErrorList.getError("authorizedsignatoryprofile2_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getZipcode()) && validationErrorList.getError("authorizedsignatoryprofile2_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry()) && validationErrorList.getError("authorizedsignatoryprofile2_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getStreet()) && validationErrorList.getError("authorizedsignatoryprofile2_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality()) && validationErrorList.getError("authorizedsignatoryprofile2_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate()) && validationErrorList.getError("authorizedsignatoryprofile2_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate()) && validationErrorList.getError("authorizedsignatoryprofile2_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPoliticallyexposed()) && validationErrorList.getError("authorizedsignatoryprofile2_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCriminalrecord()) && validationErrorList.getError("authorizedsignatoryprofile2_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDesignation()) && validationErrorList.getError("authorizedsignatoryprofile2_designation") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getDesignation());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressProof()) && validationErrorList.getError("authorizedsignatoryprofile2_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressId()) && validationErrorList.getError("authorizedsignatoryprofile2_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getOwned()) && validationErrorList.getError("authorizedsignatoryprofile2_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getOwned());

        //authorize signatory profile3
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getFirstname()) && validationErrorList.getError("authorizedsignatoryprofile3") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTitle()) && validationErrorList.getError("authorizedsignatoryprofile3_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1()) && validationErrorList.getError("authorizedsignatoryprofile3_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelephonenumber()) && validationErrorList.getError("authorizedsignatoryprofile3_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getEmailaddress()) && validationErrorList.getError("authorizedsignatoryprofile3_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth()) && validationErrorList.getError("authorizedsignatoryprofile3_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtypeselect()) && validationErrorList.getError("authorizedsignatoryprofile3_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtype()) && validationErrorList.getError("authorizedsignatoryprofile3_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getState()) && validationErrorList.getError("authorizedsignatoryprofile3_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddress()) && validationErrorList.getError("authorizedsignatoryprofile3_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCity()) && validationErrorList.getError("authorizedsignatoryprofile3_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getZipcode()) && validationErrorList.getError("authorizedsignatoryprofile3_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry()) && validationErrorList.getError("authorizedsignatoryprofile3_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getStreet()) && validationErrorList.getError("authorizedsignatoryprofile3_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality()) && validationErrorList.getError("authorizedsignatoryprofile3_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate()) && validationErrorList.getError("authorizedsignatoryprofile3_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate()) && validationErrorList.getError("authorizedsignatoryprofile3_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPoliticallyexposed()) && validationErrorList.getError("authorizedsignatoryprofile3_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCriminalrecord()) && validationErrorList.getError("authorizedsignatoryprofile3_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDesignation()) && validationErrorList.getError("authorizedsignatoryprofile3_designation") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDesignation());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressProof()) && validationErrorList.getError("authorizedsignatoryprofile3_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressId()) && validationErrorList.getError("authorizedsignatoryprofile3_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getOwned()) && validationErrorList.getError("authorizedsignatoryprofile3_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getOwned());

        //authorize signatory profile4
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getFirstname()) && validationErrorList.getError("authorizedsignatoryprofile3") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setFirstname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getFirstname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTitle()) && validationErrorList.getError("authorizedsignatoryprofile3_title") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTitle(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTitle());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelnocc1()) && validationErrorList.getError("authorizedsignatoryprofile3_telnocc1") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelnocc1(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelnocc1());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getTelephonenumber()) && validationErrorList.getError("authorizedsignatoryprofile3_telephonenumber") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setTelephonenumber(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getTelephonenumber());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getEmailaddress()) && validationErrorList.getError("authorizedsignatoryprofile3_emailaddress") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setEmailaddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getEmailaddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDateofbirth()) && validationErrorList.getError("authorizedsignatoryprofile3_dateofbirth") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDateofbirth(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDateofbirth());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtypeselect()) && validationErrorList.getError("authorizedsignatoryprofile3_identificationtypeselect") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtypeselect(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtypeselect());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getIdentificationtype()) && validationErrorList.getError("authorizedsignatoryprofile3_identificationtype") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setIdentificationtype(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getIdentificationtype());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getState()) && validationErrorList.getError("authorizedsignatoryprofile3_State") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setState(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getState());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddress()) && validationErrorList.getError("authorizedsignatoryprofile3_address") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddress(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddress());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCity()) && validationErrorList.getError("authorizedsignatoryprofile3_city") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCity(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCity());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getZipcode()) && validationErrorList.getError("authorizedsignatoryprofile3_zip") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setZipcode(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getZipcode());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCountry()) && validationErrorList.getError("authorizedsignatoryprofile3_country") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCountry(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCountry());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getStreet()) && validationErrorList.getError("authorizedsignatoryprofile3_street") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setStreet(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getStreet());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getNationality()) && validationErrorList.getError("authorizedsignatoryprofile3_nationality") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setNationality(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getNationality());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportexpirydate()) && validationErrorList.getError("authorizedsignatoryprofile3_Passportexpirydate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportexpirydate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportexpirydate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPassportissuedate()) && validationErrorList.getError("authorizedsignatoryprofile3_passportissuedate") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPassportissuedate(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPassportissuedate());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getPoliticallyexposed()) && validationErrorList.getError("authorizedsignatoryprofile3_politicallyexposed") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setPoliticallyexposed(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getPoliticallyexposed());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getCriminalrecord()) && validationErrorList.getError("authorizedsignatoryprofile3_criminalrecord") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setCriminalrecord(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getCriminalrecord());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getDesignation()) && validationErrorList.getError("authorizedsignatoryprofile3_designation") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setDesignation(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getDesignation());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressProof()) && validationErrorList.getError("authorizedsignatoryprofile3_addressproof") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressProof(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressProof());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getAddressId()) && validationErrorList.getError("authorizedsignatoryprofile3_addressId") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setAddressId(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getAddressId());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getOwned()) && validationErrorList.getError("authorizedsignatoryprofile3_owned") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setOwned(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getOwned());

        //ADD new
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getLastname()) && validationErrorList.getError("shareholderprofile1_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER1).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getLastname()) && validationErrorList.getError("shareholderprofile2_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER2).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getLastname()) && validationErrorList.getError("shareholderprofile3_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER3).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getLastname()) && validationErrorList.getError("shareholderprofile4_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.SHAREHOLDER4).getLastname());

        //ADD new
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getLastname()) && validationErrorList.getError("authorizedsignatoryprofile_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY1).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname()) && validationErrorList.getError("authorizedsignatoryprofile2_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY2).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getLastname()) && validationErrorList.getError("authorizedsignatoryprofile3_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY3).getLastname());
        if (functions.isValueNull(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getLastname()) && validationErrorList.getError("authorizedsignatoryprofile4_lastname") == null)
            ownershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).setLastname(requestOwnershipProfileVO.getOwnershipProfileDetailsVOMap().get(ApplicationManagerTypes.AUTHORIZESIGNATORY4).getLastname());
    }

    private void setBusinessProfileVO(HttpServletRequest request,BusinessProfileVO businessProfileVO)
    {

        businessProfileVO.setForeigntransactions_us(request.getParameter("foreigntransactions_us"));
        businessProfileVO.setForeigntransactions_Europe(request.getParameter("foreigntransactions_Europe"));
        businessProfileVO.setForeigntransactions_Asia(request.getParameter("foreigntransactions_Asia"));
        businessProfileVO.setForeigntransactions_cis(request.getParameter("foreigntransactions_cis"));
        businessProfileVO.setForeigntransactions_canada(request.getParameter("foreigntransactions_canada"));
        businessProfileVO.setForeigntransactions_uk(request.getParameter("foreigntransactions_uk"));
        businessProfileVO.setForeigntransactions_RestoftheWorld(request.getParameter("foreigntransactions_RestoftheWorld"));
        businessProfileVO.setMethodofacceptance_moto(request.getParameter("methodofacceptance_moto"));
        businessProfileVO.setMethodofacceptance_internet(request.getParameter("methodofacceptance_internet"));
        businessProfileVO.setMethodofacceptance_swipe(request.getParameter("methodofacceptance_swipe"));
        businessProfileVO.setAverageticket(request.getParameter("averageticket"));
        businessProfileVO.setHighestticket(request.getParameter("highestticket"));
        businessProfileVO.setUrls(request.getParameter("urls"));
        businessProfileVO.setDescriptor(request.getParameter("descriptor"));
        businessProfileVO.setDescriptionofproducts(request.getParameter("descriptionofproducts"));
        businessProfileVO.setProduct_sold_currencies(request.getParameter("product_sold_currencies"));
        businessProfileVO.setRecurringservices(request.getParameter("recurringservices"));
        businessProfileVO.setRecurringservicesyes(request.getParameter("recurringservicesyes"));
        businessProfileVO.setIsacallcenterusedyes(request.getParameter("isacallcenterusedyes"));
        businessProfileVO.setIsafulfillmenthouseused(request.getParameter("isafulfillmenthouseused"));
        businessProfileVO.setIsafulfillmenthouseused_yes(request.getParameter("isafulfillmenthouseused_yes"));
        businessProfileVO.setCardtypesaccepted_visa(request.getParameter("cardtypesaccepted_visa"));
        businessProfileVO.setCardtypesaccepted_mastercard(request.getParameter("cardtypesaccepted_mastercard"));
        businessProfileVO.setCardtypesaccepted_americanexpress(request.getParameter("cardtypesaccepted_americanexpress"));
        businessProfileVO.setCardtypesaccepted_discover(request.getParameter("cardtypesaccepted_discover"));
        businessProfileVO.setCardtypesaccepted_diners(request.getParameter("cardtypesaccepted_diners"));
        businessProfileVO.setCardtypesaccepted_jcb(request.getParameter("cardtypesaccepted_jcb"));
        businessProfileVO.setCardtypesaccepted_rupay(request.getParameter("cardtypesaccepted_rupay"));
        businessProfileVO.setCardtypesaccepted_other(request.getParameter("cardtypesaccepted_other"));
        businessProfileVO.setCardtypesaccepted_other_yes(request.getParameter("cardtypesaccepted_other_yes"));
        businessProfileVO.setOrderconfirmation_post(request.getParameter("orderconfirmation_post"));
        businessProfileVO.setOrderconfirmation_email(request.getParameter("orderconfirmation_email"));
        businessProfileVO.setOrderconfirmation_sms(request.getParameter("orderconfirmation_sms"));
        businessProfileVO.setOrderconfirmation_other(request.getParameter("orderconfirmation_other"));
        businessProfileVO.setOrderconfirmation_other_yes(request.getParameter("orderconfirmation_other_yes"));
        businessProfileVO.setPhysicalgoods_delivered(request.getParameter("physicalgoods_delivered"));
        businessProfileVO.setViainternetgoods_delivered(request.getParameter("viainternetgoods_delivered"));

        // businessProfileVO.setSizeofcustomer_Database(request.getParameter("sizeofcustomer_Database"));
        // businessProfileVO.setTopfivecountries(request.getParameter("topfivecountries"));
        businessProfileVO.setKyc_processes(request.getParameter("kyc_processes"));
        //businessProfileVO.setCustomer_account(request.getParameter("customer_account"));
        businessProfileVO.setVisa_cardlogos(request.getParameter("visa_cardlogos"));
        businessProfileVO.setMaster_cardlogos(request.getParameter("master_cardlogos"));
        businessProfileVO.setThreeD_secure_compulsory(request.getParameter("threeD_secure_compulsory"));
        businessProfileVO.setPrice_displayed(request.getParameter("price_displayed"));
        businessProfileVO.setTransaction_currency(request.getParameter("transaction_currency"));
        businessProfileVO.setCardholder_asked(request.getParameter("cardholder_asked"));
        businessProfileVO.setDynamic_descriptors(request.getParameter("dynamic_descriptors"));
        businessProfileVO.setShopping_cart(request.getParameter("shopping_cart"));
        businessProfileVO.setShopping_cart_details(request.getParameter("shopping_cart_details"));
        businessProfileVO.setPricing_policies_website(request.getParameter("pricing_policies_website"));
        businessProfileVO.setPricing_policies_website_yes(request.getParameter("pricing_policies_website_yes"));
        businessProfileVO.setFulfillment_timeframe(request.getParameter("fulfillment_timeframe"));
        businessProfileVO.setGoods_policy(request.getParameter("goods_policy"));
        businessProfileVO.setMCC_Ctegory(request.getParameter("MCC_Ctegory"));
        // businessProfileVO.setTraffic_countries_us(request.getParameter("traffic_countries_us"));
        //businessProfileVO.setTraffic_countries_Europe(request.getParameter("traffic_countries_Europe"));
        // businessProfileVO.setTraffic_countries_Asia(request.getParameter("traffic_countries_Asia"));
        // businessProfileVO.setTraffic_countries_CIS(request.getParameter("traffic_countries_CIS"));
        // businessProfileVO.setTraffic_countries_canada(request.getParameter("traffic_countries_canada"));
        //businessProfileVO.setTraffic_countries_restworld(request.getParameter("traffic_countries_restworld"));
        businessProfileVO.setCountries_blocked(request.getParameter("countries_blocked"));
        businessProfileVO.setCountries_blocked_details(request.getParameter("countries_blocked_details"));
        businessProfileVO.setCustomer_support(request.getParameter("customer_support"));
        // businessProfileVO.setCustomer_support_details(request.getParameter("customer_support_details"));

        businessProfileVO.setAffiliate_programs(request.getParameter("affiliate_programs"));
        businessProfileVO.setAffiliate_programs_details(request.getParameter("affiliate_programs_details"));
        businessProfileVO.setListfraudtools(request.getParameter("listfraudtools"));
        businessProfileVO.setListfraudtools_yes(request.getParameter("listfraudtools_yes"));
        businessProfileVO.setCustomers_identification(request.getParameter("customers_identification"));
        businessProfileVO.setCustomers_identification_yes(request.getParameter("customers_identification_yes"));
        businessProfileVO.setCoolingoffperiod(request.getParameter("coolingoffperiod"));
        businessProfileVO.setCustomersupport_email(request.getParameter("customersupport_email"));
        businessProfileVO.setCustsupportwork_hours(request.getParameter("custsupportwork_hours"));
        businessProfileVO.setTechnical_contact(request.getParameter("technical_contact"));

        businessProfileVO.setSecuritypolicy(request.getParameter("securitypolicy"));
        businessProfileVO.setConfidentialitypolicy(request.getParameter("confidentialitypolicy"));
        businessProfileVO.setApplicablejurisdictions(request.getParameter("applicablejurisdictions"));
        businessProfileVO.setPrivacy_anonymity_dataprotection(request.getParameter("privacy_anonymity_dataprotection"));
        businessProfileVO.setApp_Services(request.getParameter("App_Services"));
        businessProfileVO.setProduct_requires(request.getParameter("product_requires"));
        businessProfileVO.setAgency_employed(request.getParameter("agency_employed"));
        businessProfileVO.setAgency_employed_yes(request.getParameter("agency_employed_yes"));

        //add new
        businessProfileVO.setLowestticket(request.getParameter("lowestticket"));
        businessProfileVO.setTimeframe(request.getParameter("timeframe"));
        businessProfileVO.setLivechat(request.getParameter("livechat"));


        //Add new
        businessProfileVO.setLoginId(request.getParameter("login_id"));
        businessProfileVO.setPassWord(request.getParameter("password"));
        businessProfileVO.setIs_website_live(request.getParameter("is_website_live"));
        businessProfileVO.setTest_link(request.getParameter("test_link"));
        businessProfileVO.setCompanyIdentifiable(request.getParameter("companyidentifiable"));
        businessProfileVO.setClearlyPresented(request.getParameter("clearlypresented"));
        businessProfileVO.setTrackingNumber(request.getParameter("trackingnumber"));
        businessProfileVO.setDomainsOwned(request.getParameter("domainsowned"));
        businessProfileVO.setDomainsOwned_no(request.getParameter("domainsowned_no"));
        businessProfileVO.setSslSecured(request.getParameter("sslsecured"));
        businessProfileVO.setCopyright(request.getParameter("copyright"));
        businessProfileVO.setSourceContent(request.getParameter("sourcecontent"));
        businessProfileVO.setDirectMail(request.getParameter("directmail"));
        businessProfileVO.setYellowPages(request.getParameter("Yellowpages"));
        businessProfileVO.setRadioTv(request.getParameter("radiotv"));
        businessProfileVO.setInternet(request.getParameter("internet"));
        businessProfileVO.setNetworking(request.getParameter("networking"));
        businessProfileVO.setOutboundTelemarketing(request.getParameter("outboundtelemarketing"));
        businessProfileVO.setInHouseLocation(request.getParameter("inhouselocation"));
        businessProfileVO.setContactPerson(request.getParameter("contactperson"));
        businessProfileVO.setOtherLocation(request.getParameter("otherlocation"));
        businessProfileVO.setMainSuppliers(request.getParameter("mainsuppliers"));
        businessProfileVO.setShipmentAssured(request.getParameter("shipmentassured"));
        businessProfileVO.setBillingModel(request.getParameter("billing_model"));
        businessProfileVO.setBillingTimeFrame(request.getParameter("billing_timeframe"));
        // businessProfileVO.setRecurring(request.getParameter("recurring"));
        businessProfileVO.setRecurringAmount(request.getParameter("recurring_amount"));
        businessProfileVO.setAutomaticRecurring(request.getParameter("automatic_recurring"));
        businessProfileVO.setMultipleMembership(request.getParameter("multiple_membership"));
        businessProfileVO.setFreeMembership(request.getParameter("free_membership"));
        businessProfileVO.setCreditCardRequired(request.getParameter("creditcard_Required"));
        businessProfileVO.setAutomaticallyBilled(request.getParameter("automatically_billed"));
        businessProfileVO.setPreAuthorization(request.getParameter("pre_authorization"));
        businessProfileVO.setMerchantCode(request.getParameter("merchantcode"));
        businessProfileVO.setIpaddress(request.getParameter("ipaddress"));
        businessProfileVO.setShippingContactemail(request.getParameter("shipping_contactemail"));

        // Wirecard requirement added in Business Profile
        businessProfileVO.setShopsystem_plugin(request.getParameter("shopsystem_plugin"));
        businessProfileVO.setDirect_debit_sepa(request.getParameter("direct_debit_sepa"));
        businessProfileVO.setAlternative_payments(request.getParameter("alternative_payments"));
        businessProfileVO.setRisk_management(request.getParameter("risk_management"));
        businessProfileVO.setPayment_engine(request.getParameter("payment_engine"));
        businessProfileVO.setWebhost_company_name(request.getParameter("webhost_company_name"));
        businessProfileVO.setWebhost_phone(request.getParameter("webhost_phone"));
        businessProfileVO.setWebhost_email(request.getParameter("webhost_email"));
        businessProfileVO.setWebhost_website(request.getParameter("webhost_website"));
        businessProfileVO.setWebhost_address(request.getParameter("webhost_address"));
        businessProfileVO.setPayment_company_name(request.getParameter("payment_company_name"));
        businessProfileVO.setPayment_phone(request.getParameter("payment_phone"));
        businessProfileVO.setPayment_email(request.getParameter("payment_email"));
        businessProfileVO.setPayment_website(request.getParameter("payment_website"));
        businessProfileVO.setPayment_address(request.getParameter("payment_address"));
        businessProfileVO.setCallcenter_phone(request.getParameter("callcenter_phone"));
        businessProfileVO.setCallcenter_email(request.getParameter("callcenter_email"));
        businessProfileVO.setCallcenter_website(request.getParameter("callcenter_website"));
        businessProfileVO.setCallcenter_address(request.getParameter("callcenter_address"));
        businessProfileVO.setShoppingcart_company_name(request.getParameter("shoppingcart_company_name"));
        businessProfileVO.setShoppingcart_phone(request.getParameter("shoppingcart_phone"));
        businessProfileVO.setShoppingcart_email(request.getParameter("shoppingcart_email"));
        businessProfileVO.setShoppingcart_website(request.getParameter("shoppingcart_website"));
        businessProfileVO.setShoppingcart_address(request.getParameter("shoppingcart_address"));
        businessProfileVO.setSeasonal_fluctuating(request.getParameter("seasonal_fluctuating"));
        businessProfileVO.setSeasonal_fluctuating_yes(request.getParameter("seasonal_fluctuating_yes"));
        businessProfileVO.setCreditor_id(request.getParameter("creditor_id"));
        businessProfileVO.setPayment_delivery(request.getParameter("payment_delivery"));
        businessProfileVO.setPayment_delivery_otheryes(request.getParameter("payment_delivery_otheryes"));
        businessProfileVO.setGoods_delivery(request.getParameter("goods_delivery"));
        businessProfileVO.setTerminal_type(request.getParameter("terminal_type"));
        businessProfileVO.setTerminal_type_otheryes(request.getParameter("terminal_type_otheryes"));
        businessProfileVO.setTerminal_type_other(request.getParameter("terminal_type_other"));
        businessProfileVO.setOne_time_percentage(request.getParameter("one_time_percentage"));
        businessProfileVO.setMoto_percentage(request.getParameter("moto_percentage"));
        businessProfileVO.setInternet_percentage(request.getParameter("internet_percentage"));
        businessProfileVO.setSwipe_percentage(request.getParameter("swipe_percentage"));
        businessProfileVO.setRecurring_percentage(request.getParameter("recurring_percentage"));
        businessProfileVO.setThreedsecure_percentage(request.getParameter("threedsecure_percentage"));
        businessProfileVO.setCardvolume_visa(request.getParameter("cardvolume_visa"));
        businessProfileVO.setCardvolume_mastercard(request.getParameter("cardvolume_mastercard"));
        businessProfileVO.setCardvolume_americanexpress(request.getParameter("cardvolume_americanexpress"));
        businessProfileVO.setCardvolume_dinner(request.getParameter("cardvolume_dinner"));
        businessProfileVO.setCardvolume_other(request.getParameter("cardvolume_other"));
        businessProfileVO.setCardvolume_discover(request.getParameter("cardvolume_discover"));
        businessProfileVO.setCardvolume_rupay(request.getParameter("cardvolume_rupay"));
        businessProfileVO.setCardvolume_jcb(request.getParameter("cardvolume_jcb"));
        businessProfileVO.setPayment_type_yes(request.getParameter("payment_type_yes"));
        businessProfileVO.setOrderconfirmation_post(request.getParameter("orderconfirmation_post"));
        businessProfileVO.setOrderconfirmation_email(request.getParameter("orderconfirmation_email"));
        businessProfileVO.setOrderconfirmation_sms(request.getParameter("orderconfirmation_sms"));
        businessProfileVO.setOrderconfirmation_other(request.getParameter("orderconfirmation_other"));
        businessProfileVO.setOrderconfirmation_other_yes(request.getParameter("orderconfirmation_other_yes"));
        businessProfileVO.setPhysicalgoods_delivered(request.getParameter("physicalgoods_delivered"));
        businessProfileVO.setViainternetgoods_delivered(request.getParameter("viainternetgoods_delivered"));

        businessProfileVO.setPaymenttype_credit(request.getParameter("paymenttype_credit"));
        businessProfileVO.setPaymenttype_debit(request.getParameter("paymenttype_debit"));
        businessProfileVO.setPaymenttype_netbanking(request.getParameter("paymenttype_netbanking"));
        businessProfileVO.setPaymenttype_wallet(request.getParameter("paymenttype_wallet"));
        businessProfileVO.setPaymenttype_alternate(request.getParameter("paymenttype_alternate"));
    }

    //Business Profile from API
    private void setBusinessProfileVO(BusinessProfileVO requestBusinessProfileVO,BusinessProfileVO businessProfileVO)
    {
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_us()))
            businessProfileVO.setForeigntransactions_us(requestBusinessProfileVO.getForeigntransactions_us());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_Europe()))
            businessProfileVO.setForeigntransactions_Europe(requestBusinessProfileVO.getForeigntransactions_Europe());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_Asia()))
            businessProfileVO.setForeigntransactions_Asia(requestBusinessProfileVO.getForeigntransactions_Asia());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_cis()))
            businessProfileVO.setForeigntransactions_cis(requestBusinessProfileVO.getForeigntransactions_cis());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_canada()))
            businessProfileVO.setForeigntransactions_canada(requestBusinessProfileVO.getForeigntransactions_canada());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_uk()))
            businessProfileVO.setForeigntransactions_uk(requestBusinessProfileVO.getForeigntransactions_uk());
        if (functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_RestoftheWorld()))
            businessProfileVO.setForeigntransactions_RestoftheWorld(requestBusinessProfileVO.getForeigntransactions_RestoftheWorld());
        if (functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_moto()))
            businessProfileVO.setMethodofacceptance_moto(requestBusinessProfileVO.getMethodofacceptance_moto());
        if (functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_internet()))
            businessProfileVO.setMethodofacceptance_internet(requestBusinessProfileVO.getMethodofacceptance_internet());
        if (functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_swipe()))
            businessProfileVO.setMethodofacceptance_swipe(requestBusinessProfileVO.getMethodofacceptance_swipe());
        if (functions.isValueNull(requestBusinessProfileVO.getAverageticket()))
            businessProfileVO.setAverageticket(requestBusinessProfileVO.getAverageticket());
        if (functions.isValueNull(requestBusinessProfileVO.getHighestticket()))
            businessProfileVO.setHighestticket(requestBusinessProfileVO.getHighestticket());
        if (functions.isValueNull(requestBusinessProfileVO.getUrls()))
            businessProfileVO.setUrls(requestBusinessProfileVO.getUrls());
        if (functions.isValueNull(requestBusinessProfileVO.getDescriptor()))
            businessProfileVO.setDescriptor(requestBusinessProfileVO.getDescriptor());
        if (functions.isValueNull(requestBusinessProfileVO.getDescriptionofproducts()))
            businessProfileVO.setDescriptionofproducts(requestBusinessProfileVO.getDescriptionofproducts());
        if (functions.isValueNull(requestBusinessProfileVO.getProduct_sold_currencies()))
            businessProfileVO.setProduct_sold_currencies(requestBusinessProfileVO.getProduct_sold_currencies());
        if (functions.isValueNull(requestBusinessProfileVO.getRecurringservices()))
            businessProfileVO.setRecurringservices(requestBusinessProfileVO.getRecurringservices());
        if (functions.isValueNull(requestBusinessProfileVO.getRecurringservicesyes()))
            businessProfileVO.setRecurringservicesyes(requestBusinessProfileVO.getRecurringservicesyes());
        if (functions.isValueNull(requestBusinessProfileVO.getIsacallcenterusedyes()))
            businessProfileVO.setIsacallcenterusedyes(requestBusinessProfileVO.getIsacallcenterusedyes());
        if (functions.isValueNull(requestBusinessProfileVO.getIsafulfillmenthouseused()))
            businessProfileVO.setIsafulfillmenthouseused(requestBusinessProfileVO.getIsafulfillmenthouseused());
        if (functions.isValueNull(requestBusinessProfileVO.getIsafulfillmenthouseused_yes()))
            businessProfileVO.setIsafulfillmenthouseused_yes(requestBusinessProfileVO.getIsafulfillmenthouseused_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_visa()))
            businessProfileVO.setCardtypesaccepted_visa(requestBusinessProfileVO.getCardtypesaccepted_visa());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_mastercard()))
            businessProfileVO.setCardtypesaccepted_mastercard(requestBusinessProfileVO.getCardtypesaccepted_mastercard());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_americanexpress()))
            businessProfileVO.setCardtypesaccepted_americanexpress(requestBusinessProfileVO.getCardtypesaccepted_americanexpress());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_discover()))
            businessProfileVO.setCardtypesaccepted_discover(requestBusinessProfileVO.getCardtypesaccepted_discover());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_diners()))
            businessProfileVO.setCardtypesaccepted_diners(requestBusinessProfileVO.getCardtypesaccepted_diners());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_jcb()))
            businessProfileVO.setCardtypesaccepted_jcb(requestBusinessProfileVO.getCardtypesaccepted_jcb());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_rupay()))
            businessProfileVO.setCardtypesaccepted_rupay(requestBusinessProfileVO.getCardtypesaccepted_rupay());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_other()))
            businessProfileVO.setCardtypesaccepted_other(requestBusinessProfileVO.getCardtypesaccepted_other());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_other_yes()))
            businessProfileVO.setCardtypesaccepted_other_yes(requestBusinessProfileVO.getCardtypesaccepted_other_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getKyc_processes()))
            businessProfileVO.setKyc_processes(requestBusinessProfileVO.getKyc_processes());
        if (functions.isValueNull(requestBusinessProfileVO.getVisa_cardlogos()))
            businessProfileVO.setVisa_cardlogos(requestBusinessProfileVO.getVisa_cardlogos());
        if (functions.isValueNull(requestBusinessProfileVO.getMaster_cardlogos()))
            businessProfileVO.setMaster_cardlogos(requestBusinessProfileVO.getMaster_cardlogos());
        if (functions.isValueNull(requestBusinessProfileVO.getThreeD_secure_compulsory()))
            businessProfileVO.setThreeD_secure_compulsory(requestBusinessProfileVO.getThreeD_secure_compulsory());
        if (functions.isValueNull(requestBusinessProfileVO.getPrice_displayed()))
            businessProfileVO.setPrice_displayed(requestBusinessProfileVO.getPrice_displayed());
        if (functions.isValueNull(requestBusinessProfileVO.getTransaction_currency()))
            businessProfileVO.setTransaction_currency(requestBusinessProfileVO.getTransaction_currency());
        if (functions.isValueNull(requestBusinessProfileVO.getCardholder_asked()))
            businessProfileVO.setCardholder_asked(requestBusinessProfileVO.getCardholder_asked());
        if (functions.isValueNull(requestBusinessProfileVO.getDynamic_descriptors()))
            businessProfileVO.setDynamic_descriptors(requestBusinessProfileVO.getDynamic_descriptors());
        if (functions.isValueNull(requestBusinessProfileVO.getShopping_cart()))
            businessProfileVO.setShopping_cart(requestBusinessProfileVO.getShopping_cart());
        if (functions.isValueNull(requestBusinessProfileVO.getShopping_cart_details()))
            businessProfileVO.setShopping_cart_details(requestBusinessProfileVO.getShopping_cart_details());
        if (functions.isValueNull(requestBusinessProfileVO.getPricing_policies_website()))
            businessProfileVO.setPricing_policies_website(requestBusinessProfileVO.getPricing_policies_website());
        if (functions.isValueNull(requestBusinessProfileVO.getPricing_policies_website_yes()))
            businessProfileVO.setPricing_policies_website_yes(requestBusinessProfileVO.getPricing_policies_website_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getFulfillment_timeframe()))
            businessProfileVO.setFulfillment_timeframe(requestBusinessProfileVO.getFulfillment_timeframe());
        if (functions.isValueNull(requestBusinessProfileVO.getGoods_policy()))
            businessProfileVO.setGoods_policy(requestBusinessProfileVO.getGoods_policy());
        if (functions.isValueNull(requestBusinessProfileVO.getMCC_Ctegory()))
            businessProfileVO.setMCC_Ctegory(requestBusinessProfileVO.getMCC_Ctegory());
        if (functions.isValueNull(requestBusinessProfileVO.getCountries_blocked()))
            businessProfileVO.setCountries_blocked(requestBusinessProfileVO.getCountries_blocked());
        if (functions.isValueNull(requestBusinessProfileVO.getCountries_blocked_details()))
            businessProfileVO.setCountries_blocked_details(requestBusinessProfileVO.getCountries_blocked_details());
        if (functions.isValueNull(requestBusinessProfileVO.getCustomer_support()))
            businessProfileVO.setCustomer_support(requestBusinessProfileVO.getCustomer_support());
        if (functions.isValueNull(requestBusinessProfileVO.getAffiliate_programs()))
            businessProfileVO.setAffiliate_programs(requestBusinessProfileVO.getAffiliate_programs());
        if (functions.isValueNull(requestBusinessProfileVO.getAffiliate_programs_details()))
            businessProfileVO.setAffiliate_programs_details(requestBusinessProfileVO.getAffiliate_programs_details());
        if (functions.isValueNull(requestBusinessProfileVO.getListfraudtools()))
            businessProfileVO.setListfraudtools(requestBusinessProfileVO.getListfraudtools());
        if (functions.isValueNull(requestBusinessProfileVO.getListfraudtools_yes()))
            businessProfileVO.setListfraudtools_yes(requestBusinessProfileVO.getListfraudtools_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getCustomers_identification()))
            businessProfileVO.setCustomers_identification(requestBusinessProfileVO.getCustomers_identification());
        if (functions.isValueNull(requestBusinessProfileVO.getCustomers_identification_yes()))
            businessProfileVO.setCustomers_identification_yes(requestBusinessProfileVO.getCustomers_identification_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getCoolingoffperiod()))
            businessProfileVO.setCoolingoffperiod(requestBusinessProfileVO.getCoolingoffperiod());
        if (functions.isValueNull(requestBusinessProfileVO.getCustomersupport_email()))
            businessProfileVO.setCustomersupport_email(requestBusinessProfileVO.getCustomersupport_email());
        if (functions.isValueNull(requestBusinessProfileVO.getCustsupportwork_hours()))
            businessProfileVO.setCustsupportwork_hours(requestBusinessProfileVO.getCustsupportwork_hours());
        if (functions.isValueNull(requestBusinessProfileVO.getTechnical_contact()))
            businessProfileVO.setTechnical_contact(requestBusinessProfileVO.getTechnical_contact());
        if (functions.isValueNull(requestBusinessProfileVO.getSecuritypolicy()))
            businessProfileVO.setSecuritypolicy(requestBusinessProfileVO.getSecuritypolicy());
        if (functions.isValueNull(requestBusinessProfileVO.getConfidentialitypolicy()))
            businessProfileVO.setConfidentialitypolicy(requestBusinessProfileVO.getConfidentialitypolicy());
        if (functions.isValueNull(requestBusinessProfileVO.getApplicablejurisdictions()))
            businessProfileVO.setApplicablejurisdictions(requestBusinessProfileVO.getApplicablejurisdictions());
        if (functions.isValueNull(requestBusinessProfileVO.getPrivacy_anonymity_dataprotection()))
            businessProfileVO.setPrivacy_anonymity_dataprotection(requestBusinessProfileVO.getPrivacy_anonymity_dataprotection());
        if (functions.isValueNull(requestBusinessProfileVO.getApp_Services()))
            businessProfileVO.setApp_Services(requestBusinessProfileVO.getApp_Services());
        if (functions.isValueNull(requestBusinessProfileVO.getAgency_employed()))
            businessProfileVO.setAgency_employed(requestBusinessProfileVO.getAgency_employed());
        if (functions.isValueNull(requestBusinessProfileVO.getAgency_employed_yes()))
            businessProfileVO.setAgency_employed_yes(requestBusinessProfileVO.getAgency_employed_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getProduct_requires()))
            businessProfileVO.setProduct_requires(requestBusinessProfileVO.getProduct_requires());
        if (functions.isValueNull(requestBusinessProfileVO.getLowestticket()))
            businessProfileVO.setLowestticket(requestBusinessProfileVO.getLowestticket());
        if (functions.isValueNull(requestBusinessProfileVO.getTimeframe()))
            businessProfileVO.setTimeframe(requestBusinessProfileVO.getTimeframe());
        if (functions.isValueNull(requestBusinessProfileVO.getLivechat()))
            businessProfileVO.setLivechat(requestBusinessProfileVO.getLivechat());
        if (functions.isValueNull(requestBusinessProfileVO.getLoginId()))
            businessProfileVO.setLoginId(requestBusinessProfileVO.getLoginId());
        if (functions.isValueNull(requestBusinessProfileVO.getPassWord()))
            businessProfileVO.setPassWord(requestBusinessProfileVO.getPassWord());
        if (functions.isValueNull(requestBusinessProfileVO.getIs_website_live()))
            businessProfileVO.setIs_website_live(requestBusinessProfileVO.getIs_website_live());
        if (functions.isValueNull(requestBusinessProfileVO.getTest_link()))
            businessProfileVO.setTest_link(requestBusinessProfileVO.getTest_link());
        if (functions.isValueNull(requestBusinessProfileVO.getCompanyIdentifiable()))
            businessProfileVO.setCompanyIdentifiable(requestBusinessProfileVO.getCompanyIdentifiable());
        if (functions.isValueNull(requestBusinessProfileVO.getClearlyPresented()))
            businessProfileVO.setClearlyPresented(requestBusinessProfileVO.getClearlyPresented());
        if (functions.isValueNull(requestBusinessProfileVO.getTrackingNumber()))
            businessProfileVO.setTrackingNumber(requestBusinessProfileVO.getTrackingNumber());
        if (functions.isValueNull(requestBusinessProfileVO.getDomainsOwned()))
            businessProfileVO.setDomainsOwned(requestBusinessProfileVO.getDomainsOwned());
        if (functions.isValueNull(requestBusinessProfileVO.getDomainsOwned_no()))
            businessProfileVO.setDomainsOwned_no(requestBusinessProfileVO.getDomainsOwned_no());
        if (functions.isValueNull(requestBusinessProfileVO.getSslSecured()))
            businessProfileVO.setSslSecured(requestBusinessProfileVO.getSslSecured());
        if (functions.isValueNull(requestBusinessProfileVO.getCopyright()))
            businessProfileVO.setCopyright(requestBusinessProfileVO.getCopyright());
        if (functions.isValueNull(requestBusinessProfileVO.getSourceContent()))
            businessProfileVO.setSourceContent(requestBusinessProfileVO.getSourceContent());
        if (functions.isValueNull(requestBusinessProfileVO.getDirectMail()))
            businessProfileVO.setDirectMail(requestBusinessProfileVO.getDirectMail());
        if (functions.isValueNull(requestBusinessProfileVO.getYellowPages()))
            businessProfileVO.setYellowPages(requestBusinessProfileVO.getYellowPages());
        if (functions.isValueNull(requestBusinessProfileVO.getRadioTv()))
            businessProfileVO.setRadioTv(requestBusinessProfileVO.getRadioTv());
        if (functions.isValueNull(requestBusinessProfileVO.getInternet()))
            businessProfileVO.setInternet(requestBusinessProfileVO.getInternet());
        if (functions.isValueNull(requestBusinessProfileVO.getNetworking()))
            businessProfileVO.setNetworking(requestBusinessProfileVO.getNetworking());
        if (functions.isValueNull(requestBusinessProfileVO.getOutboundTelemarketing()))
            businessProfileVO.setOutboundTelemarketing(requestBusinessProfileVO.getOutboundTelemarketing());
        if (functions.isValueNull(requestBusinessProfileVO.getInHouseLocation()))
            businessProfileVO.setInHouseLocation(requestBusinessProfileVO.getInHouseLocation());
        if (functions.isValueNull(requestBusinessProfileVO.getContactPerson()))
            businessProfileVO.setContactPerson(requestBusinessProfileVO.getContactPerson());
        if (functions.isValueNull(requestBusinessProfileVO.getOtherLocation()))
            businessProfileVO.setOtherLocation(requestBusinessProfileVO.getOtherLocation());
        if (functions.isValueNull(requestBusinessProfileVO.getMainSuppliers()))
            businessProfileVO.setMainSuppliers(requestBusinessProfileVO.getMainSuppliers());
        if (functions.isValueNull(requestBusinessProfileVO.getShipmentAssured()))
            businessProfileVO.setShipmentAssured(requestBusinessProfileVO.getShipmentAssured());
        if (functions.isValueNull(requestBusinessProfileVO.getBillingModel()))
            businessProfileVO.setBillingModel(requestBusinessProfileVO.getBillingModel());
        if (functions.isValueNull(requestBusinessProfileVO.getBillingTimeFrame()))
            businessProfileVO.setBillingTimeFrame(requestBusinessProfileVO.getBillingTimeFrame());
        if (functions.isValueNull(requestBusinessProfileVO.getRecurringAmount()))
            businessProfileVO.setRecurringAmount(requestBusinessProfileVO.getRecurringAmount());
        if (functions.isValueNull(requestBusinessProfileVO.getAutomaticRecurring()))
            businessProfileVO.setAutomaticRecurring(requestBusinessProfileVO.getAutomaticRecurring());
        if (functions.isValueNull(requestBusinessProfileVO.getMultipleMembership()))
            businessProfileVO.setMultipleMembership(requestBusinessProfileVO.getMultipleMembership());
        if (functions.isValueNull(requestBusinessProfileVO.getFreeMembership()))
            businessProfileVO.setFreeMembership(requestBusinessProfileVO.getFreeMembership());
        if (functions.isValueNull(requestBusinessProfileVO.getCreditCardRequired()))
            businessProfileVO.setCreditCardRequired(requestBusinessProfileVO.getCreditCardRequired());
        if (functions.isValueNull(requestBusinessProfileVO.getAutomaticallyBilled()))
            businessProfileVO.setAutomaticallyBilled(requestBusinessProfileVO.getAutomaticallyBilled());
        if (functions.isValueNull(requestBusinessProfileVO.getPreAuthorization()))
            businessProfileVO.setPreAuthorization(requestBusinessProfileVO.getPreAuthorization());
        if (functions.isValueNull(requestBusinessProfileVO.getMerchantCode()))
        {
            businessProfileVO.setMerchantCode(requestBusinessProfileVO.getMerchantCode());
            businessProfileVO.setMerchantCode(commonFunctionUtil.getMerchantCodeForAPI(businessProfileVO.getMerchantCode()));
        }
        if(functions.isValueNull(requestBusinessProfileVO.getIpaddress()))
            businessProfileVO.setIpaddress(requestBusinessProfileVO.getIpaddress());

        // Wirecard requirement added in Business Profile
        if(functions.isValueNull(requestBusinessProfileVO.getShopsystem_plugin()))
            businessProfileVO.setShopsystem_plugin(requestBusinessProfileVO.getShopsystem_plugin());
        if(functions.isValueNull(requestBusinessProfileVO.getDirect_debit_sepa()))
            businessProfileVO.setDirect_debit_sepa(requestBusinessProfileVO.getDirect_debit_sepa());
        if(functions.isValueNull(requestBusinessProfileVO.getAlternative_payments()))
            businessProfileVO.setAlternative_payments(requestBusinessProfileVO.getAlternative_payments());
        if(functions.isValueNull(requestBusinessProfileVO.getRisk_management()))
            businessProfileVO.setRisk_management(requestBusinessProfileVO.getRisk_management());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_engine()))
            businessProfileVO.setPayment_engine(requestBusinessProfileVO.getPayment_engine());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_company_name()))
            businessProfileVO.setWebhost_company_name(requestBusinessProfileVO.getWebhost_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_phone()))
            businessProfileVO.setWebhost_phone(requestBusinessProfileVO.getWebhost_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_email()))
            businessProfileVO.setWebhost_email(requestBusinessProfileVO.getWebhost_email());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_address()))
            businessProfileVO.setWebhost_address(requestBusinessProfileVO.getWebhost_address());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_company_name()))
            businessProfileVO.setPayment_company_name(requestBusinessProfileVO.getPayment_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_phone()))
            businessProfileVO.setPayment_phone(requestBusinessProfileVO.getPayment_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_email()))
            businessProfileVO.setPayment_email(requestBusinessProfileVO.getPayment_email());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_address()))
            businessProfileVO.setPayment_address(requestBusinessProfileVO.getPayment_address());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_phone()))
            businessProfileVO.setCallcenter_phone(requestBusinessProfileVO.getCallcenter_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_email()))
            businessProfileVO.setCallcenter_email(requestBusinessProfileVO.getCallcenter_email());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_address()))
            businessProfileVO.setCallcenter_address(requestBusinessProfileVO.getCallcenter_address());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_company_name()))
            businessProfileVO.setShoppingcart_company_name(requestBusinessProfileVO.getShoppingcart_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_phone()))
            businessProfileVO.setShoppingcart_phone(requestBusinessProfileVO.getShoppingcart_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_email()))
            businessProfileVO.setShoppingcart_email(requestBusinessProfileVO.getShoppingcart_email());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_address()))
            businessProfileVO.setShoppingcart_address(requestBusinessProfileVO.getShoppingcart_address());
        if(functions.isValueNull(requestBusinessProfileVO.getSeasonal_fluctuating()))
            businessProfileVO.setSeasonal_fluctuating_yes(requestBusinessProfileVO.getSeasonal_fluctuating_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getSeasonal_fluctuating_yes()))
            businessProfileVO.setSeasonal_fluctuating_yes(requestBusinessProfileVO.getSeasonal_fluctuating_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getCreditor_id()))
            businessProfileVO.setCreditor_id(requestBusinessProfileVO.getCreditor_id());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_delivery()))
            businessProfileVO.setPayment_delivery(requestBusinessProfileVO.getPayment_delivery());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_delivery_otheryes()))
            businessProfileVO.setPayment_delivery_otheryes(requestBusinessProfileVO.getPayment_delivery_otheryes());
        if(functions.isValueNull(requestBusinessProfileVO.getGoods_delivery()))
            businessProfileVO.setGoods_delivery(requestBusinessProfileVO.getGoods_delivery());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type()))
            businessProfileVO.setTerminal_type(requestBusinessProfileVO.getTerminal_type());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type_otheryes()))
            businessProfileVO.setTerminal_type_otheryes(requestBusinessProfileVO.getTerminal_type_otheryes());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type_other()))
            businessProfileVO.setTerminal_type_other(requestBusinessProfileVO.getTerminal_type_other());
        if(functions.isValueNull(requestBusinessProfileVO.getOne_time_percentage()))
            businessProfileVO.setOne_time_percentage(requestBusinessProfileVO.getOne_time_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getMoto_percentage()))
            businessProfileVO.setMoto_percentage(requestBusinessProfileVO.getMoto_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getInternet_percentage()))
            businessProfileVO.setInternet_percentage(requestBusinessProfileVO.getInternet_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getSwipe_percentage()))
            businessProfileVO.setSwipe_percentage(requestBusinessProfileVO.getSwipe_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getRecurring_percentage()))
            businessProfileVO.setRecurring_percentage(requestBusinessProfileVO.getRecurring_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getThreedsecure_percentage()))
            businessProfileVO.setThreedsecure_percentage(requestBusinessProfileVO.getThreedsecure_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_visa()))
            businessProfileVO.setCardvolume_visa(requestBusinessProfileVO.getCardvolume_visa());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_mastercard()))
            businessProfileVO.setCardvolume_mastercard(requestBusinessProfileVO.getCardvolume_mastercard());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_americanexpress()))
            businessProfileVO.setCardvolume_americanexpress(requestBusinessProfileVO.getCardvolume_americanexpress());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_dinner()))
            businessProfileVO.setCardvolume_dinner(requestBusinessProfileVO.getCardvolume_dinner());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_other()))
            businessProfileVO.setCardvolume_other(requestBusinessProfileVO.getCardvolume_other());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_discover()))
            businessProfileVO.setCardvolume_discover(requestBusinessProfileVO.getCardvolume_discover());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_rupay()))
            businessProfileVO.setCardvolume_rupay(requestBusinessProfileVO.getCardvolume_rupay());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_jcb()))
            businessProfileVO.setCardvolume_jcb(requestBusinessProfileVO.getCardvolume_jcb());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_type_yes()))
            businessProfileVO.setPayment_type_yes(requestBusinessProfileVO.getPayment_type_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_post()))
            businessProfileVO.setOrderconfirmation_post(requestBusinessProfileVO.getOrderconfirmation_post());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_email()))
            businessProfileVO.setOrderconfirmation_email(requestBusinessProfileVO.getOrderconfirmation_email());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_sms()))
            businessProfileVO.setOrderconfirmation_sms(requestBusinessProfileVO.getOrderconfirmation_sms());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_other()))
            businessProfileVO.setOrderconfirmation_other(requestBusinessProfileVO.getOrderconfirmation_other());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_other_yes()))
            businessProfileVO.setOrderconfirmation_other_yes(requestBusinessProfileVO.getOrderconfirmation_other_yes());

        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_credit()))
            businessProfileVO.setPaymenttype_credit(requestBusinessProfileVO.getPaymenttype_credit());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_debit()))
            businessProfileVO.setPaymenttype_debit(requestBusinessProfileVO.getPaymenttype_debit());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_netbanking()))
            businessProfileVO.setPaymenttype_netbanking(requestBusinessProfileVO.getPaymenttype_netbanking());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_wallet()))
            businessProfileVO.setPaymenttype_wallet(requestBusinessProfileVO.getPaymenttype_wallet());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_alternate()))
            businessProfileVO.setPaymenttype_alternate(requestBusinessProfileVO.getPaymenttype_alternate());
    }

    //Business Profile from API
    private void setBusinessProfileVO(BusinessProfileVO requestBusinessProfileVO,BusinessProfileVO businessProfileVO,ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_us()) && validationErrorList.getError("foreigntransactions_us")==null )
            businessProfileVO.setForeigntransactions_us(requestBusinessProfileVO.getForeigntransactions_us());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_Europe()) && validationErrorList.getError("foreigntransactions_Europe")==null )
            businessProfileVO.setForeigntransactions_Europe(requestBusinessProfileVO.getForeigntransactions_Europe());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_Asia()) && validationErrorList.getError("foreigntransactions_Asia")==null )
            businessProfileVO.setForeigntransactions_Asia(requestBusinessProfileVO.getForeigntransactions_Asia());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_cis()) && validationErrorList.getError("foreigntransactions_cis")==null )
            businessProfileVO.setForeigntransactions_cis(requestBusinessProfileVO.getForeigntransactions_cis());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_canada()) && validationErrorList.getError("foreigntransactions_canada")==null )
            businessProfileVO.setForeigntransactions_canada(requestBusinessProfileVO.getForeigntransactions_canada());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_uk()) && validationErrorList.getError("foreigntransactions_uk")==null )
            businessProfileVO.setForeigntransactions_uk(requestBusinessProfileVO.getForeigntransactions_uk());
        if(functions.isValueNull(requestBusinessProfileVO.getForeigntransactions_RestoftheWorld()) && validationErrorList.getError("foreigntransactions_RestoftheWorld")==null )
            businessProfileVO.setForeigntransactions_RestoftheWorld(requestBusinessProfileVO.getForeigntransactions_RestoftheWorld());
        if(functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_moto()) && validationErrorList.getError("methodofacceptance_moto")==null )
            businessProfileVO.setMethodofacceptance_moto(requestBusinessProfileVO.getMethodofacceptance_moto());
        if(functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_internet()) && validationErrorList.getError("methodofacceptance_internet")==null )
            businessProfileVO.setMethodofacceptance_internet(requestBusinessProfileVO.getMethodofacceptance_internet());
        if(functions.isValueNull(requestBusinessProfileVO.getMethodofacceptance_swipe()) && validationErrorList.getError("methodofacceptance_swipe")==null )
            businessProfileVO.setMethodofacceptance_swipe(requestBusinessProfileVO.getMethodofacceptance_swipe());
        if(functions.isValueNull(requestBusinessProfileVO.getAverageticket()) && validationErrorList.getError("averageticket")==null )
            businessProfileVO.setAverageticket(requestBusinessProfileVO.getAverageticket());
        if(functions.isValueNull(requestBusinessProfileVO.getHighestticket()) && validationErrorList.getError("highestticket")==null )
            businessProfileVO.setHighestticket(requestBusinessProfileVO.getHighestticket());
        if(functions.isValueNull(requestBusinessProfileVO.getUrls()) && validationErrorList.getError("urls")==null )
            businessProfileVO.setUrls(requestBusinessProfileVO.getUrls());
        if(functions.isValueNull(requestBusinessProfileVO.getDescriptor()) && validationErrorList.getError("descriptor")==null )
            businessProfileVO.setDescriptor(requestBusinessProfileVO.getDescriptor());
        if(functions.isValueNull(requestBusinessProfileVO.getDescriptionofproducts()) && validationErrorList.getError("descriptionofproducts")==null )
            businessProfileVO.setDescriptionofproducts(requestBusinessProfileVO.getDescriptionofproducts());
        if(functions.isValueNull(requestBusinessProfileVO.getProduct_sold_currencies()) && validationErrorList.getError("product_sold_currencies")==null )
            businessProfileVO.setProduct_sold_currencies(requestBusinessProfileVO.getProduct_sold_currencies());
        if(functions.isValueNull(requestBusinessProfileVO.getRecurringservices()) && validationErrorList.getError("recurringservices")==null )
            businessProfileVO.setRecurringservices(requestBusinessProfileVO.getRecurringservices());
        if(functions.isValueNull(requestBusinessProfileVO.getRecurringservicesyes()) && validationErrorList.getError("recurringservicesyes")==null )
            businessProfileVO.setRecurringservicesyes(requestBusinessProfileVO.getRecurringservicesyes());
        if(functions.isValueNull(requestBusinessProfileVO.getIsacallcenterusedyes()) && validationErrorList.getError("isacallcenterusedyes")==null )
            businessProfileVO.setIsacallcenterusedyes(requestBusinessProfileVO.getIsacallcenterusedyes());
        if(functions.isValueNull(requestBusinessProfileVO.getIsafulfillmenthouseused()) && validationErrorList.getError("isafulfillmenthouseused")==null )
            businessProfileVO.setIsafulfillmenthouseused(requestBusinessProfileVO.getIsafulfillmenthouseused());
        if(functions.isValueNull(requestBusinessProfileVO.getIsafulfillmenthouseused_yes()) && validationErrorList.getError("isafulfillmenthouseused_yes")==null )
            businessProfileVO.setIsafulfillmenthouseused_yes(requestBusinessProfileVO.getIsafulfillmenthouseused_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_visa()) && validationErrorList.getError("cardtypesaccepted_visa")==null )
            businessProfileVO.setCardtypesaccepted_visa(requestBusinessProfileVO.getCardtypesaccepted_visa());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_mastercard()) && validationErrorList.getError("cardtypesaccepted_mastercard")==null )
            businessProfileVO.setCardtypesaccepted_mastercard(requestBusinessProfileVO.getCardtypesaccepted_mastercard());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_americanexpress()) && validationErrorList.getError("cardtypesaccepted_americanexpress")==null )
            businessProfileVO.setCardtypesaccepted_americanexpress(requestBusinessProfileVO.getCardtypesaccepted_americanexpress());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_discover()) && validationErrorList.getError("cardtypesaccepted_discover")==null )
            businessProfileVO.setCardtypesaccepted_discover(requestBusinessProfileVO.getCardtypesaccepted_discover());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_diners()) && validationErrorList.getError("cardtypesaccepted_diners")==null )
            businessProfileVO.setCardtypesaccepted_diners(requestBusinessProfileVO.getCardtypesaccepted_diners());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_jcb()) && validationErrorList.getError("cardtypesaccepted_jcb")==null )
            businessProfileVO.setCardtypesaccepted_jcb(requestBusinessProfileVO.getCardtypesaccepted_jcb());
        if (functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_rupay()) && validationErrorList.getError("cardtypesaccepted_rupay")==null )
            businessProfileVO.setCardtypesaccepted_rupay(requestBusinessProfileVO.getCardtypesaccepted_rupay());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_other()) && validationErrorList.getError("cardtypesaccepted_other")==null )
            businessProfileVO.setCardtypesaccepted_other(requestBusinessProfileVO.getCardtypesaccepted_other());
        if(functions.isValueNull(requestBusinessProfileVO.getCardtypesaccepted_other_yes()) && validationErrorList.getError("cardtypesaccepted_other_yes")==null )
            businessProfileVO.setCardtypesaccepted_other_yes(requestBusinessProfileVO.getCardtypesaccepted_other_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getKyc_processes()) && validationErrorList.getError("kyc_processes")==null )
            businessProfileVO.setKyc_processes(requestBusinessProfileVO.getKyc_processes());
        if(functions.isValueNull(requestBusinessProfileVO.getVisa_cardlogos()) && validationErrorList.getError("visa_cardlogos")==null )
            businessProfileVO.setVisa_cardlogos(requestBusinessProfileVO.getVisa_cardlogos());
        if(functions.isValueNull(requestBusinessProfileVO.getMaster_cardlogos()) && validationErrorList.getError("master_cardlogos")==null )
            businessProfileVO.setMaster_cardlogos(requestBusinessProfileVO.getMaster_cardlogos());
        if(functions.isValueNull(requestBusinessProfileVO.getThreeD_secure_compulsory()) && validationErrorList.getError("threeD_secure_compulsory")==null )
            businessProfileVO.setThreeD_secure_compulsory(requestBusinessProfileVO.getThreeD_secure_compulsory());
        if(functions.isValueNull(requestBusinessProfileVO.getPrice_displayed()) && validationErrorList.getError("price_displayed")==null )
            businessProfileVO.setPrice_displayed(requestBusinessProfileVO.getPrice_displayed());
        if(functions.isValueNull(requestBusinessProfileVO.getTransaction_currency()) && validationErrorList.getError("transaction_currency")==null )
            businessProfileVO.setTransaction_currency(requestBusinessProfileVO.getTransaction_currency());
        if(functions.isValueNull(requestBusinessProfileVO.getCardholder_asked()) && validationErrorList.getError("cardholder_asked")==null )
            businessProfileVO.setCardholder_asked(requestBusinessProfileVO.getCardholder_asked());
        if(functions.isValueNull(requestBusinessProfileVO.getDynamic_descriptors()) && validationErrorList.getError("dynamic_descriptors")==null )
            businessProfileVO.setDynamic_descriptors(requestBusinessProfileVO.getDynamic_descriptors());
        if(functions.isValueNull(requestBusinessProfileVO.getShopping_cart()) && validationErrorList.getError("shopping_cart")==null )
            businessProfileVO.setShopping_cart(requestBusinessProfileVO.getShopping_cart());
        if(functions.isValueNull(requestBusinessProfileVO.getShopping_cart_details()) && validationErrorList.getError("shopping_cart_details")==null )
            businessProfileVO.setShopping_cart_details(requestBusinessProfileVO.getShopping_cart_details());
        if(functions.isValueNull(requestBusinessProfileVO.getPricing_policies_website()) && validationErrorList.getError("pricing_policies_website")==null )
            businessProfileVO.setPricing_policies_website(requestBusinessProfileVO.getPricing_policies_website());
        if(functions.isValueNull(requestBusinessProfileVO.getPricing_policies_website_yes()) && validationErrorList.getError("pricing_policies_website_yes")==null )
            businessProfileVO.setPricing_policies_website_yes(requestBusinessProfileVO.getPricing_policies_website_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getFulfillment_timeframe()) && validationErrorList.getError("fulfillment_timeframe")==null )
            businessProfileVO.setFulfillment_timeframe(requestBusinessProfileVO.getFulfillment_timeframe());
        if(functions.isValueNull(requestBusinessProfileVO.getGoods_policy()) && validationErrorList.getError("goods_policy")==null )
            businessProfileVO.setGoods_policy(requestBusinessProfileVO.getGoods_policy());
        if(functions.isValueNull(requestBusinessProfileVO.getMCC_Ctegory()) && validationErrorList.getError("MCC_Ctegory")==null )
            businessProfileVO.setMCC_Ctegory(requestBusinessProfileVO.getMCC_Ctegory());
        if(functions.isValueNull(requestBusinessProfileVO.getCountries_blocked()) && validationErrorList.getError("countries_blocked")==null )
            businessProfileVO.setCountries_blocked(requestBusinessProfileVO.getCountries_blocked());
        if(functions.isValueNull(requestBusinessProfileVO.getCountries_blocked_details()) && validationErrorList.getError("countries_blocked_details")==null )
            businessProfileVO.setCountries_blocked_details(requestBusinessProfileVO.getCountries_blocked_details());
        if(functions.isValueNull(requestBusinessProfileVO.getCustomer_support()) && validationErrorList.getError("customer_support")==null )
            businessProfileVO.setCustomer_support(requestBusinessProfileVO.getCustomer_support());
        if(functions.isValueNull(requestBusinessProfileVO.getAffiliate_programs()) && validationErrorList.getError("affiliate_programs")==null )
            businessProfileVO.setAffiliate_programs(requestBusinessProfileVO.getAffiliate_programs());
        if(functions.isValueNull(requestBusinessProfileVO.getAffiliate_programs_details()) && validationErrorList.getError("affiliate_programs_details")==null )
            businessProfileVO.setAffiliate_programs_details(requestBusinessProfileVO.getAffiliate_programs_details());
        if(functions.isValueNull(requestBusinessProfileVO.getListfraudtools()) && validationErrorList.getError("listfraudtools")==null )
            businessProfileVO.setListfraudtools(requestBusinessProfileVO.getListfraudtools());
        if(functions.isValueNull(requestBusinessProfileVO.getListfraudtools_yes()) && validationErrorList.getError("listfraudtools_yes")==null )
            businessProfileVO.setListfraudtools_yes(requestBusinessProfileVO.getListfraudtools_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getCustomers_identification()) && validationErrorList.getError("customers_identification")==null )
            businessProfileVO.setCustomers_identification(requestBusinessProfileVO.getCustomers_identification());
        if(functions.isValueNull(requestBusinessProfileVO.getCustomers_identification_yes()) && validationErrorList.getError("customers_identification_yes")==null )
            businessProfileVO.setCustomers_identification_yes(requestBusinessProfileVO.getCustomers_identification_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getCoolingoffperiod()) && validationErrorList.getError("coolingoffperiod")==null )
            businessProfileVO.setCoolingoffperiod(requestBusinessProfileVO.getCoolingoffperiod());
        if(functions.isValueNull(requestBusinessProfileVO.getCustomersupport_email()) && validationErrorList.getError("customersupport_email")==null )
            businessProfileVO.setCustomersupport_email(requestBusinessProfileVO.getCustomersupport_email());
        if(functions.isValueNull(requestBusinessProfileVO.getCustsupportwork_hours()) && validationErrorList.getError("custsupportwork_hours")==null )
            businessProfileVO.setCustsupportwork_hours(requestBusinessProfileVO.getCustsupportwork_hours());
        if(functions.isValueNull(requestBusinessProfileVO.getTechnical_contact()) && validationErrorList.getError("technical_contact")==null)
            businessProfileVO.setTechnical_contact(requestBusinessProfileVO.getTechnical_contact());
        if(functions.isValueNull(requestBusinessProfileVO.getSecuritypolicy()) && validationErrorList.getError("securitypolicy")==null )
            businessProfileVO.setSecuritypolicy(requestBusinessProfileVO.getSecuritypolicy());
        if(functions.isValueNull(requestBusinessProfileVO.getConfidentialitypolicy()) && validationErrorList.getError("confidentialitypolicy")==null )
            businessProfileVO.setConfidentialitypolicy(requestBusinessProfileVO.getConfidentialitypolicy());
        if(functions.isValueNull(requestBusinessProfileVO.getApplicablejurisdictions()) && validationErrorList.getError("applicablejurisdictions")==null )
            businessProfileVO.setApplicablejurisdictions(requestBusinessProfileVO.getApplicablejurisdictions());
        if (functions.isValueNull(requestBusinessProfileVO.getPrivacy_anonymity_dataprotection()) && validationErrorList.getError("privacy_anonymity_dataprotection")==null )
            businessProfileVO.setPrivacy_anonymity_dataprotection(requestBusinessProfileVO.getPrivacy_anonymity_dataprotection());
        if(functions.isValueNull(requestBusinessProfileVO.getApp_Services()) && validationErrorList.getError("App_Services") == null)
            businessProfileVO.setApp_Services(requestBusinessProfileVO.getApp_Services());
        if(functions.isValueNull(requestBusinessProfileVO.getProduct_requires()) && validationErrorList.getError("product_requires")==null)
            businessProfileVO.setProduct_requires(requestBusinessProfileVO.getProduct_requires());
        if (functions.isValueNull(requestBusinessProfileVO.getAgency_employed()) && validationErrorList.getError("agency_employed")==null )
            businessProfileVO.setAgency_employed(requestBusinessProfileVO.getAgency_employed());
        if (functions.isValueNull(requestBusinessProfileVO.getAgency_employed_yes()) && validationErrorList.getError("agency_employed_yes")==null )
            businessProfileVO.setAgency_employed_yes(requestBusinessProfileVO.getAgency_employed_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getLowestticket()) && validationErrorList.getError("lowestticket")==null )
            businessProfileVO.setLowestticket(requestBusinessProfileVO.getLowestticket());
        if(functions.isValueNull(requestBusinessProfileVO.getTimeframe()) && validationErrorList.getError("timeframe")==null )
            businessProfileVO.setTimeframe(requestBusinessProfileVO.getTimeframe());
        if(functions.isValueNull(requestBusinessProfileVO.getLivechat()) && validationErrorList.getError("livechat")==null )
            businessProfileVO.setLivechat(requestBusinessProfileVO.getLivechat());
        if(functions.isValueNull(requestBusinessProfileVO.getLoginId()) && validationErrorList.getError("login_id")==null )
            businessProfileVO.setLoginId(requestBusinessProfileVO.getLoginId());
        if(functions.isValueNull(requestBusinessProfileVO.getPassWord()) && validationErrorList.getError("password")==null )
            businessProfileVO.setPassWord(requestBusinessProfileVO.getPassWord());
        if (functions.isValueNull(requestBusinessProfileVO.getIs_website_live()) && validationErrorList.getError("is_website_live")==null )
            businessProfileVO.setIs_website_live(requestBusinessProfileVO.getIs_website_live());
        if(functions.isValueNull(requestBusinessProfileVO.getTest_link()) && validationErrorList.getError("test_link")==null )
            businessProfileVO.setTest_link(requestBusinessProfileVO.getTest_link());
        if(functions.isValueNull(requestBusinessProfileVO.getCompanyIdentifiable()) && validationErrorList.getError("companyidentifiable")==null )
            businessProfileVO.setCompanyIdentifiable(requestBusinessProfileVO.getCompanyIdentifiable());
        if(functions.isValueNull(requestBusinessProfileVO.getClearlyPresented()) && validationErrorList.getError("clearlypresented")==null )
            businessProfileVO.setClearlyPresented(requestBusinessProfileVO.getClearlyPresented());
        if(functions.isValueNull(requestBusinessProfileVO.getTrackingNumber()) && validationErrorList.getError("trackingnumber")==null )
            businessProfileVO.setTrackingNumber(requestBusinessProfileVO.getTrackingNumber());
        if(functions.isValueNull(requestBusinessProfileVO.getDomainsOwned()) && validationErrorList.getError("domainsowned")==null )
            businessProfileVO.setDomainsOwned(requestBusinessProfileVO.getDomainsOwned());
        if(functions.isValueNull(requestBusinessProfileVO.getDomainsOwned_no()) && validationErrorList.getError("domainsowned_no")==null )
            businessProfileVO.setDomainsOwned_no(requestBusinessProfileVO.getDomainsOwned_no());
        if(functions.isValueNull(requestBusinessProfileVO.getSslSecured()) && validationErrorList.getError("sslsecured")==null )
            businessProfileVO.setSslSecured(requestBusinessProfileVO.getSslSecured());
        if(functions.isValueNull(requestBusinessProfileVO.getCopyright()) && validationErrorList.getError("copyright")==null )
            businessProfileVO.setCopyright(requestBusinessProfileVO.getCopyright());
        if(functions.isValueNull(requestBusinessProfileVO.getSourceContent()) && validationErrorList.getError("sourcecontent")==null )
            businessProfileVO.setSourceContent(requestBusinessProfileVO.getSourceContent());
        if(functions.isValueNull(requestBusinessProfileVO.getDirectMail()) && validationErrorList.getError("directmail")==null )
            businessProfileVO.setDirectMail(requestBusinessProfileVO.getDirectMail());
        if(functions.isValueNull(requestBusinessProfileVO.getYellowPages()) && validationErrorList.getError("Yellowpages")==null )
            businessProfileVO.setYellowPages(requestBusinessProfileVO.getYellowPages());
        if(functions.isValueNull(requestBusinessProfileVO.getRadioTv()) && validationErrorList.getError("radiotv")==null )
            businessProfileVO.setRadioTv(requestBusinessProfileVO.getRadioTv());
        if(functions.isValueNull(requestBusinessProfileVO.getInternet()) && validationErrorList.getError("internet")==null )
            businessProfileVO.setInternet(requestBusinessProfileVO.getInternet());
        if(functions.isValueNull(requestBusinessProfileVO.getNetworking()) && validationErrorList.getError("networking")==null )
            businessProfileVO.setNetworking(requestBusinessProfileVO.getNetworking());
        if(functions.isValueNull(requestBusinessProfileVO.getOutboundTelemarketing()) && validationErrorList.getError("outboundtelemarketing")==null )
            businessProfileVO.setOutboundTelemarketing(requestBusinessProfileVO.getOutboundTelemarketing());
        if(functions.isValueNull(requestBusinessProfileVO.getInHouseLocation()) && validationErrorList.getError("inhouselocation")==null )
            businessProfileVO.setInHouseLocation(requestBusinessProfileVO.getInHouseLocation());
        if(functions.isValueNull(requestBusinessProfileVO.getContactPerson()) && validationErrorList.getError("contactperson")==null )
            businessProfileVO.setContactPerson(requestBusinessProfileVO.getContactPerson());
        if(functions.isValueNull(requestBusinessProfileVO.getShippingContactemail()) && validationErrorList.getError("shipping_contactemail")==null )
            businessProfileVO.setShippingContactemail(requestBusinessProfileVO.getShippingContactemail());
        if(functions.isValueNull(requestBusinessProfileVO.getOtherLocation()) && validationErrorList.getError("otherlocation")==null )
            businessProfileVO.setOtherLocation(requestBusinessProfileVO.getOtherLocation());
        if(functions.isValueNull(requestBusinessProfileVO.getMainSuppliers()) && validationErrorList.getError("mainsuppliers")==null )
            businessProfileVO.setMainSuppliers(requestBusinessProfileVO.getMainSuppliers());
        if(functions.isValueNull(requestBusinessProfileVO.getShipmentAssured()) && validationErrorList.getError("shipmentassured")==null )
            businessProfileVO.setShipmentAssured(requestBusinessProfileVO.getShipmentAssured());
        if(functions.isValueNull(requestBusinessProfileVO.getBillingModel()) && validationErrorList.getError("billing_model")==null )
            businessProfileVO.setBillingModel(requestBusinessProfileVO.getBillingModel());
        if(functions.isValueNull(requestBusinessProfileVO.getBillingTimeFrame()) && validationErrorList.getError("billing_timeframe")==null )
            businessProfileVO.setBillingTimeFrame(requestBusinessProfileVO.getBillingTimeFrame());
        if(functions.isValueNull(requestBusinessProfileVO.getRecurringAmount()) && validationErrorList.getError("recurring_amount")==null )
            businessProfileVO.setRecurringAmount(requestBusinessProfileVO.getRecurringAmount());
        if(functions.isValueNull(requestBusinessProfileVO.getAutomaticRecurring()) && validationErrorList.getError("automatic_recurring")==null )
            businessProfileVO.setAutomaticRecurring(requestBusinessProfileVO.getAutomaticRecurring());
        if(functions.isValueNull(requestBusinessProfileVO.getMultipleMembership()) && validationErrorList.getError("multiple_membership")==null )
            businessProfileVO.setMultipleMembership(requestBusinessProfileVO.getMultipleMembership());
        if(functions.isValueNull(requestBusinessProfileVO.getFreeMembership()) && validationErrorList.getError("free_membership")==null )
            businessProfileVO.setFreeMembership(requestBusinessProfileVO.getFreeMembership());
        if(functions.isValueNull(requestBusinessProfileVO.getCreditCardRequired()) && validationErrorList.getError("creditcard_Required")==null )
            businessProfileVO.setCreditCardRequired(requestBusinessProfileVO.getCreditCardRequired());
        if(functions.isValueNull(requestBusinessProfileVO.getAutomaticallyBilled()) && validationErrorList.getError("automatically_billed")==null )
            businessProfileVO.setAutomaticallyBilled(requestBusinessProfileVO.getAutomaticallyBilled());
        if(functions.isValueNull(requestBusinessProfileVO.getPreAuthorization()) && validationErrorList.getError("pre_authorization")==null )
            businessProfileVO.setPreAuthorization(requestBusinessProfileVO.getPreAuthorization());
        if(functions.isValueNull(requestBusinessProfileVO.getMerchantCode()) && validationErrorList.getError("merchantcode")==null )
            businessProfileVO.setMerchantCode(requestBusinessProfileVO.getMerchantCode());
        if(functions.isValueNull(requestBusinessProfileVO.getIpaddress()) && validationErrorList.getError("ipaddress")==null )
            businessProfileVO.setIpaddress(requestBusinessProfileVO.getIpaddress());

        // Wirecard requirement added in Business Profile
        if(functions.isValueNull(requestBusinessProfileVO.getShopsystem_plugin()) && validationErrorList.getError("shopsystem_plugin")==null )
            businessProfileVO.setShopsystem_plugin(requestBusinessProfileVO.getShopsystem_plugin());
        if(functions.isValueNull(requestBusinessProfileVO.getDirect_debit_sepa()) && validationErrorList.getError("direct_debit_sepa")==null )
            businessProfileVO.setDirect_debit_sepa(requestBusinessProfileVO.getDirect_debit_sepa());
        if(functions.isValueNull(requestBusinessProfileVO.getAlternative_payments()) && validationErrorList.getError("alternative_payments")==null )
            businessProfileVO.setAlternative_payments(requestBusinessProfileVO.getAlternative_payments());
        if(functions.isValueNull(requestBusinessProfileVO.getRisk_management()) && validationErrorList.getError("risk_management")==null )
            businessProfileVO.setRisk_management(requestBusinessProfileVO.getRisk_management());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_engine()) && validationErrorList.getError("payment_engine")==null )
            businessProfileVO.setPayment_engine(requestBusinessProfileVO.getPayment_engine());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_company_name()) && validationErrorList.getError("webhost_company_name")==null )
            businessProfileVO.setWebhost_company_name(requestBusinessProfileVO.getWebhost_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_phone()) && validationErrorList.getError("webhost_phone")==null )
            businessProfileVO.setWebhost_phone(requestBusinessProfileVO.getWebhost_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_email()) && validationErrorList.getError("webhost_email")==null )
            businessProfileVO.setWebhost_email(requestBusinessProfileVO.getWebhost_email());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_website()) && validationErrorList.getError("webhost_website")==null )
            businessProfileVO.setWebhost_website(requestBusinessProfileVO.getWebhost_website());
        if(functions.isValueNull(requestBusinessProfileVO.getWebhost_address()) && validationErrorList.getError("webhost_address")==null )
            businessProfileVO.setWebhost_address(requestBusinessProfileVO.getWebhost_address());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_company_name()) && validationErrorList.getError("payment_company_name")==null )
            businessProfileVO.setPayment_company_name(requestBusinessProfileVO.getPayment_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_phone()) && validationErrorList.getError("payment_phone")==null )
            businessProfileVO.setPayment_phone(requestBusinessProfileVO.getPayment_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_email()) && validationErrorList.getError("payment_email")==null )
            businessProfileVO.setPayment_email(requestBusinessProfileVO.getPayment_email());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_website()) && validationErrorList.getError("payment_website")==null )
            businessProfileVO.setPayment_website(requestBusinessProfileVO.getPayment_website());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_address()) && validationErrorList.getError("payment_address")==null )
            businessProfileVO.setPayment_address(requestBusinessProfileVO.getPayment_address());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_phone()) && validationErrorList.getError("callcenter_phone")==null )
            businessProfileVO.setCallcenter_phone(requestBusinessProfileVO.getCallcenter_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_email()) && validationErrorList.getError("callcenter_email")==null )
            businessProfileVO.setCallcenter_email(requestBusinessProfileVO.getCallcenter_email());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_website()) && validationErrorList.getError("callcenter_website")==null )
            businessProfileVO.setCallcenter_website(requestBusinessProfileVO.getCallcenter_website());
        if(functions.isValueNull(requestBusinessProfileVO.getCallcenter_address()) && validationErrorList.getError("callcenter_address")==null )
            businessProfileVO.setCallcenter_address(requestBusinessProfileVO.getCallcenter_address());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_company_name()) && validationErrorList.getError("shoppingcart_company_name")==null )
            businessProfileVO.setShoppingcart_company_name(requestBusinessProfileVO.getShoppingcart_company_name());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_phone()) && validationErrorList.getError("shoppingcart_phone")==null )
            businessProfileVO.setShoppingcart_phone(requestBusinessProfileVO.getShoppingcart_phone());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_email()) && validationErrorList.getError("shoppingcart_email")==null )
            businessProfileVO.setShoppingcart_email(requestBusinessProfileVO.getShoppingcart_email());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_website()) && validationErrorList.getError("shoppingcart_website")==null )
            businessProfileVO.setShoppingcart_website(requestBusinessProfileVO.getShoppingcart_website());
        if(functions.isValueNull(requestBusinessProfileVO.getShoppingcart_address()) && validationErrorList.getError("shoppingcart_address")==null )
            businessProfileVO.setShoppingcart_address(requestBusinessProfileVO.getShoppingcart_address());
        if(functions.isValueNull(requestBusinessProfileVO.getSeasonal_fluctuating()) && validationErrorList.getError("seasonal_fluctuating")==null )
            businessProfileVO.setSeasonal_fluctuating(requestBusinessProfileVO.getSeasonal_fluctuating());
        if(functions.isValueNull(requestBusinessProfileVO.getSeasonal_fluctuating_yes()) && validationErrorList.getError("seasonal_fluctuating_yes")==null )
            businessProfileVO.setSeasonal_fluctuating_yes(requestBusinessProfileVO.getSeasonal_fluctuating_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getCreditor_id()) && validationErrorList.getError("creditor_id")==null )
            businessProfileVO.setCreditor_id(requestBusinessProfileVO.getCreditor_id());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_delivery()) && validationErrorList.getError("payment_delivery")==null )
            businessProfileVO.setPayment_delivery(requestBusinessProfileVO.getPayment_delivery());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_delivery_otheryes()) && validationErrorList.getError("payment_delivery_otheryes")==null )
            businessProfileVO.setPayment_delivery_otheryes(requestBusinessProfileVO.getPayment_delivery_otheryes());
        if(functions.isValueNull(requestBusinessProfileVO.getGoods_delivery()) && validationErrorList.getError("goods_delivery")==null )
            businessProfileVO.setGoods_delivery(requestBusinessProfileVO.getGoods_delivery());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type()) && validationErrorList.getError("terminal_type")==null )
            businessProfileVO.setTerminal_type(requestBusinessProfileVO.getTerminal_type());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type_otheryes()) && validationErrorList.getError("terminal_type_otheryes")==null )
            businessProfileVO.setTerminal_type_otheryes(requestBusinessProfileVO.getTerminal_type_otheryes());
        if(functions.isValueNull(requestBusinessProfileVO.getTerminal_type_other()) && validationErrorList.getError("terminal_type_other")==null )
            businessProfileVO.setTerminal_type_other(requestBusinessProfileVO.getTerminal_type_other());
        if(functions.isValueNull(requestBusinessProfileVO.getOne_time_percentage()) && validationErrorList.getError("one_time_percentage")==null )
            businessProfileVO.setOne_time_percentage(requestBusinessProfileVO.getOne_time_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getMoto_percentage()) && validationErrorList.getError("moto_percentage")==null )
            businessProfileVO.setMoto_percentage(requestBusinessProfileVO.getMoto_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getInternet_percentage()) && validationErrorList.getError("internet_percentage")==null )
            businessProfileVO.setInternet_percentage(requestBusinessProfileVO.getInternet_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getSwipe_percentage()) && validationErrorList.getError("swipe_percentage")==null )
            businessProfileVO.setSwipe_percentage(requestBusinessProfileVO.getSwipe_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getRecurring_percentage()) && validationErrorList.getError("recurring_percentage")==null )
            businessProfileVO.setRecurring_percentage(requestBusinessProfileVO.getRecurring_percentage());
        if(functions.isValueNull(requestBusinessProfileVO.getThreedsecure_percentage()) && validationErrorList.getError("threedsecure_percentage")==null )
            businessProfileVO.setThreedsecure_percentage(requestBusinessProfileVO.getThreedsecure_percentage());

        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_visa()) && validationErrorList.getError("cardvolume_visa")==null )
            businessProfileVO.setCardvolume_visa(requestBusinessProfileVO.getCardvolume_visa());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_mastercard()) && validationErrorList.getError("cardvolume_mastercard")==null )
            businessProfileVO.setCardvolume_mastercard(requestBusinessProfileVO.getCardvolume_mastercard());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_americanexpress()) && validationErrorList.getError("cardvolume_americanexpress")==null )
            businessProfileVO.setCardvolume_americanexpress(requestBusinessProfileVO.getCardvolume_americanexpress());
        if (functions.isValueNull(requestBusinessProfileVO.getCardvolume_dinner()) && validationErrorList.getError("cardvolume_dinner")==null )
            businessProfileVO.setCardvolume_dinner(requestBusinessProfileVO.getCardvolume_dinner());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_other()) && validationErrorList.getError("cardvolume_other")==null )
            businessProfileVO.setCardvolume_other(requestBusinessProfileVO.getCardvolume_other());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_discover()) && validationErrorList.getError("cardvolume_discover")==null )
            businessProfileVO.setCardvolume_discover(requestBusinessProfileVO.getCardvolume_discover());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_rupay()) && validationErrorList.getError("cardvolume_rupay")==null )
            businessProfileVO.setCardvolume_rupay(requestBusinessProfileVO.getCardvolume_rupay());
        if(functions.isValueNull(requestBusinessProfileVO.getCardvolume_jcb()) && validationErrorList.getError("cardvolume_jcb")==null )
            businessProfileVO.setCardvolume_jcb(requestBusinessProfileVO.getCardvolume_jcb());
        if(functions.isValueNull(requestBusinessProfileVO.getPayment_type_yes()) && validationErrorList.getError("payment_type_yes")==null )
            businessProfileVO.setPayment_type_yes(requestBusinessProfileVO.getPayment_type_yes());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_post()) && validationErrorList.getError("orderconfirmation_post")==null )
            businessProfileVO.setOrderconfirmation_post(requestBusinessProfileVO.getOrderconfirmation_post());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_email()) && validationErrorList.getError("orderconfirmation_email")==null )
            businessProfileVO.setOrderconfirmation_email(requestBusinessProfileVO.getOrderconfirmation_email());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_sms()) && validationErrorList.getError("orderconfirmation_sms")==null )
            businessProfileVO.setOrderconfirmation_sms(requestBusinessProfileVO.getOrderconfirmation_sms());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_other()) && validationErrorList.getError("orderconfirmation_other")==null )
            businessProfileVO.setOrderconfirmation_other(requestBusinessProfileVO.getOrderconfirmation_other());
        if(functions.isValueNull(requestBusinessProfileVO.getOrderconfirmation_other_yes()) && validationErrorList.getError("orderconfirmation_other_yes")==null )
            businessProfileVO.setOrderconfirmation_other_yes(requestBusinessProfileVO.getOrderconfirmation_other_yes());
        if (functions.isValueNull(requestBusinessProfileVO.getPhysicalgoods_delivered()) && validationErrorList.getError("physicalgoods_delivered")==null )
            businessProfileVO.setPhysicalgoods_delivered(requestBusinessProfileVO.getPhysicalgoods_delivered());
        if (functions.isValueNull(requestBusinessProfileVO.getViainternetgoods_delivered()) && validationErrorList.getError("viainternetgoods_delivered")==null )
            businessProfileVO.setViainternetgoods_delivered(requestBusinessProfileVO.getViainternetgoods_delivered());

        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_credit()) && validationErrorList.getError("paymenttype_credit")==null )
            businessProfileVO.setPaymenttype_credit(requestBusinessProfileVO.getPaymenttype_credit());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_debit()) && validationErrorList.getError("paymenttype_debit")==null )
            businessProfileVO.setPaymenttype_debit(requestBusinessProfileVO.getPaymenttype_debit());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_netbanking()) && validationErrorList.getError("paymenttype_netbanking")==null )
            businessProfileVO.setPaymenttype_netbanking(requestBusinessProfileVO.getPaymenttype_netbanking());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_wallet()) && validationErrorList.getError("paymenttype_wallet")==null )
            businessProfileVO.setPaymenttype_wallet(requestBusinessProfileVO.getPaymenttype_wallet());
        if(functions.isValueNull(requestBusinessProfileVO.getPaymenttype_alternate()) && validationErrorList.getError("paymenttype_alternate")==null )
            businessProfileVO.setPaymenttype_alternate(requestBusinessProfileVO.getPaymenttype_alternate());
    }

/*    private void setBankProfileVO(HttpServletRequest request,BankProfileVO bankProfileVO)
    {
        bankProfileVO.setCurrencyrequested_productssold(request.getParameter("currencyrequested_productssold"));
        bankProfileVO.setCurrencyrequested_bankaccount(request.getParameter("currencyrequested_bankaccount"));
        bankProfileVO.setBankinfo_bic(request.getParameter("bankinfo_bic"));
        bankProfileVO.setBankinfo_bank_name(request.getParameter("bankinfo_bank_name"));
        bankProfileVO.setBankinfo_bankaddress(request.getParameter("bankinfo_bankaddress"));
        bankProfileVO.setBankinfo_bankphonenumber(request.getParameter("bankinfo_bankphonenumber"));
        bankProfileVO.setBankinfo_aba_routingcode(request.getParameter("bankinfo_aba_routingcode"));
        bankProfileVO.setBankinfo_accountholder(request.getParameter("bankinfo_accountholder"));
        bankProfileVO.setBank_accountnumber_IBAN(request.getParameter("bank_accountnumber_IBAN"));
        bankProfileVO.setSalesvolume_lastmonth(request.getParameter("salesvolume_lastmonth"));
        bankProfileVO.setSalesvolume_2monthsago(request.getParameter("salesvolume_2monthsago"));
        bankProfileVO.setSalesvolume_3monthsago(request.getParameter("salesvolume_3monthsago"));
        bankProfileVO.setSalesvolume_4monthsago(request.getParameter("salesvolume_4monthsago"));
        bankProfileVO.setSalesvolume_5monthsago(request.getParameter("salesvolume_5monthsago"));
        bankProfileVO.setSalesvolume_6monthsago(request.getParameter("salesvolume_6monthsago"));
        bankProfileVO.setNumberoftransactions_lastmonth(request.getParameter("numberoftransactions_lastmonth"));
        bankProfileVO.setNumberoftransactions_2monthsago(request.getParameter("numberoftransactions_2monthsago"));
        bankProfileVO.setNumberoftransactions_3monthsago(request.getParameter("numberoftransactions_3monthsago"));
        bankProfileVO.setNumberoftransactions_4monthsago(request.getParameter("numberoftransactions_4monthsago"));
        bankProfileVO.setNumberoftransactions_5monthsago(request.getParameter("numberoftransactions_5monthsago"));
        bankProfileVO.setNumberoftransactions_6monthsago(request.getParameter("numberoftransactions_6monthsago"));
        bankProfileVO.setChargebackvolume_lastmonth(request.getParameter("chargebackvolume_lastmonth"));
        bankProfileVO.setChargebackvolume_2monthsago(request.getParameter("chargebackvolume_2monthsago"));
        bankProfileVO.setChargebackvolume_3monthsago(request.getParameter("chargebackvolume_3monthsago"));
        bankProfileVO.setChargebackvolume_4monthsago(request.getParameter("chargebackvolume_4monthsago"));
        bankProfileVO.setChargebackvolume_5monthsago(request.getParameter("chargebackvolume_5monthsago"));
        bankProfileVO.setChargebackvolume_6monthsago(request.getParameter("chargebackvolume_6monthsago"));
        bankProfileVO.setNumberofchargebacks_lastmonth(request.getParameter("numberofchargebacks_lastmonth"));
        bankProfileVO.setNumberofchargebacks_2monthsago(request.getParameter("numberofchargebacks_2monthsago"));
        bankProfileVO.setNumberofchargebacks_3monthsago(request.getParameter("numberofchargebacks_3monthsago"));
        bankProfileVO.setNumberofchargebacks_4monthsago(request.getParameter("numberofchargebacks_4monthsago"));
        bankProfileVO.setNumberofchargebacks_5monthsago(request.getParameter("numberofchargebacks_5monthsago"));
        bankProfileVO.setNumberofchargebacks_6monthsago(request.getParameter("numberofchargebacks_6monthsago"));
        bankProfileVO.setRefundsvolume_lastmonth(request.getParameter("refundsvolume_lastmonth"));
        bankProfileVO.setRefundsvolume_2monthsago(request.getParameter("refundsvolume_2monthsago"));
        bankProfileVO.setRefundsvolume_3monthsago(request.getParameter("refundsvolume_3monthsago"));
        bankProfileVO.setRefundsvolume_4monthsago(request.getParameter("refundsvolume_4monthsago"));
        bankProfileVO.setRefundsvolume_5monthsago(request.getParameter("refundsvolume_5monthsago"));
        bankProfileVO.setRefundsvolume_6monthsago(request.getParameter("refundsvolume_6monthsago"));
        bankProfileVO.setNumberofrefunds_lastmonth(request.getParameter("numberofrefunds_lastmonth"));
        bankProfileVO.setNumberofrefunds_2monthsago(request.getParameter("numberofrefunds_2monthsago"));
        bankProfileVO.setNumberofrefunds_3monthsago(request.getParameter("numberofrefunds_3monthsago"));
        bankProfileVO.setNumberofrefunds_4monthsago(request.getParameter("numberofrefunds_4monthsago"));
        bankProfileVO.setNumberofrefunds_5monthsago(request.getParameter("numberofrefunds_5monthsago"));
        bankProfileVO.setNumberofrefunds_6monthsago(request.getParameter("numberofrefunds_6monthsago"));
        bankProfileVO.setChargebackratio_lastmonth(request.getParameter("chargebackratio_lastmonth"));
        bankProfileVO.setChargebackratio_2monthsago(request.getParameter("chargebackratio_2monthsago"));
        bankProfileVO.setChargebackratio_3monthsago(request.getParameter("chargebackratio_3monthsago"));
        bankProfileVO.setChargebackratio_4monthsago(request.getParameter("chargebackratio_4monthsago"));
        bankProfileVO.setChargebackratio_5monthsago(request.getParameter("chargebackratio_5monthsago"));
        bankProfileVO.setChargebackratio_6monthsago(request.getParameter("chargebackratio_6monthsago"));
        bankProfileVO.setRefundratio_lastmonth(request.getParameter("refundratio_lastmonth"));
        bankProfileVO.setRefundratio_2monthsago(request.getParameter("refundratio_2monthsago"));
        bankProfileVO.setRefundratio_3monthsago(request.getParameter("refundratio_3monthsago"));
        bankProfileVO.setRefundratio_4monthsago(request.getParameter("refundratio_4monthsago"));
        bankProfileVO.setRefundratio_5monthsago(request.getParameter("refundratio_5monthsago"));
        bankProfileVO.setRefundratio_6monthsago(request.getParameter("refundratio_6monthsago"));

        bankProfileVO.setCurrency_products_INR(request.getParameter("currency_products_INR"));
        bankProfileVO.setCurrency_products_USD(request.getParameter("currency_products_USD"));
        bankProfileVO.setCurrency_products_EUR(request.getParameter("currency_products_EUR"));
        bankProfileVO.setCurrency_products_GBP(request.getParameter("currency_products_GBP"));
        bankProfileVO.setCurrency_products_JPY(request.getParameter("currency_products_JPY"));
        bankProfileVO.setCurrency_products_PEN(request.getParameter("currency_products_PEN"));

        bankProfileVO.setCurrency_payments_INR(request.getParameter("currency_payments_INR"));
        bankProfileVO.setCurrency_payments_USD(request.getParameter("currency_payments_USD"));
        bankProfileVO.setCurrency_payments_EUR(request.getParameter("currency_payments_EUR"));
        bankProfileVO.setCurrency_payments_GBP(request.getParameter("currency_payments_GBP"));
        bankProfileVO.setCurrency_payments_JPY(request.getParameter("currency_payments_JPY"));
        bankProfileVO.setCurrency_payments_PEN(request.getParameter("currency_payments_PEN"));

        //Add specific new currency
        bankProfileVO.setCurrency_products_HKD(request.getParameter("currency_products_HKD"));
        bankProfileVO.setCurrency_products_AUD(request.getParameter("currency_products_AUD"));
        bankProfileVO.setCurrency_products_CAD(request.getParameter("currency_products_CAD"));
        bankProfileVO.setCurrency_products_DKK(request.getParameter("currency_products_DKK"));
        bankProfileVO.setCurrency_products_SEK(request.getParameter("currency_products_SEK"));
        bankProfileVO.setCurrency_products_NOK(request.getParameter("currency_products_NOK"));

        //ADD specific new currency
        bankProfileVO.setCurrency_payments_HKD(request.getParameter("currency_payments_HKD"));
        bankProfileVO.setCurrency_payments_AUD(request.getParameter("currency_payments_AUD"));
        bankProfileVO.setCurrency_payments_CAD(request.getParameter("currency_payments_CAD"));
        bankProfileVO.setCurrency_payments_DKK(request.getParameter("currency_payments_DKK"));
        bankProfileVO.setCurrency_payments_SEK(request.getParameter("currency_payments_SEK"));
        bankProfileVO.setCurrency_payments_NOK(request.getParameter("currency_payments_NOK"));

        //add NEW
        bankProfileVO.setAquirer(request.getParameter("aquirer"));
        bankProfileVO.setReasonaquirer(request.getParameter("reason_aquirer"));
        bankProfileVO.setBankcontactperson(request.getParameter("bankcontactperson"));

    }

    //set bank profile for API
    private void setBankProfileVO(BankProfileVO requestBankProfileVO,BankProfileVO bankProfileVO)
    {
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_productssold()))
            bankProfileVO.setCurrencyrequested_productssold(requestBankProfileVO.getCurrencyrequested_productssold());
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_bankaccount()))
            bankProfileVO.setCurrencyrequested_bankaccount(requestBankProfileVO.getCurrencyrequested_bankaccount());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bic()))
            bankProfileVO.setBankinfo_bic(requestBankProfileVO.getBankinfo_bic());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bank_name()))
            bankProfileVO.setBankinfo_bank_name(requestBankProfileVO.getBankinfo_bank_name());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankaddress()))
            bankProfileVO.setBankinfo_bankaddress(requestBankProfileVO.getBankinfo_bankaddress());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankphonenumber()))
            bankProfileVO.setBankinfo_bankphonenumber(requestBankProfileVO.getBankinfo_bankphonenumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_aba_routingcode()))
            bankProfileVO.setBankinfo_aba_routingcode(requestBankProfileVO.getBankinfo_aba_routingcode());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountholder()))
            bankProfileVO.setBankinfo_accountholder(requestBankProfileVO.getBankinfo_accountholder());
        if(functions.isValueNull(requestBankProfileVO.getBank_accountnumber_IBAN()))
            bankProfileVO.setBank_accountnumber_IBAN(requestBankProfileVO.getBank_accountnumber_IBAN());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()))
            bankProfileVO.setSalesvolume_lastmonth(requestBankProfileVO.getSalesvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago()))
            bankProfileVO.setSalesvolume_2monthsago(requestBankProfileVO.getSalesvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()))
            bankProfileVO.setSalesvolume_3monthsago(requestBankProfileVO.getSalesvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()))
            bankProfileVO.setSalesvolume_4monthsago(requestBankProfileVO.getSalesvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()))
            bankProfileVO.setSalesvolume_5monthsago(requestBankProfileVO.getSalesvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()))
            bankProfileVO.setSalesvolume_6monthsago(requestBankProfileVO.getSalesvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_lastmonth()))
            bankProfileVO.setNumberoftransactions_lastmonth(requestBankProfileVO.getNumberoftransactions_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_2monthsago()))
            bankProfileVO.setNumberoftransactions_2monthsago(requestBankProfileVO.getNumberoftransactions_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_3monthsago()))
            bankProfileVO.setNumberoftransactions_3monthsago(requestBankProfileVO.getNumberoftransactions_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_4monthsago()))
            bankProfileVO.setNumberoftransactions_4monthsago(requestBankProfileVO.getNumberoftransactions_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_5monthsago()))
            bankProfileVO.setNumberoftransactions_5monthsago(requestBankProfileVO.getNumberoftransactions_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_6monthsago()))
            bankProfileVO.setNumberoftransactions_6monthsago(requestBankProfileVO.getNumberoftransactions_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth()))
            bankProfileVO.setChargebackvolume_lastmonth(requestBankProfileVO.getChargebackvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago()))
            bankProfileVO.setChargebackvolume_2monthsago(requestBankProfileVO.getChargebackvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago()))
            bankProfileVO.setChargebackvolume_3monthsago(requestBankProfileVO.getChargebackvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago()))
            bankProfileVO.setChargebackvolume_4monthsago(requestBankProfileVO.getChargebackvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago()))
            bankProfileVO.setChargebackvolume_5monthsago(requestBankProfileVO.getChargebackvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago()))
            bankProfileVO.setChargebackvolume_6monthsago(requestBankProfileVO.getChargebackvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_lastmonth()))
            bankProfileVO.setNumberofchargebacks_lastmonth(requestBankProfileVO.getNumberofchargebacks_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_2monthsago()))
            bankProfileVO.setNumberofchargebacks_2monthsago(requestBankProfileVO.getNumberofchargebacks_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_3monthsago()))
            bankProfileVO.setNumberofchargebacks_3monthsago(requestBankProfileVO.getNumberofchargebacks_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_4monthsago()))
            bankProfileVO.setNumberofchargebacks_4monthsago(requestBankProfileVO.getNumberofchargebacks_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_5monthsago()))
            bankProfileVO.setNumberofchargebacks_5monthsago(requestBankProfileVO.getNumberofchargebacks_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_6monthsago()))
            bankProfileVO.setNumberofchargebacks_6monthsago(requestBankProfileVO.getNumberofchargebacks_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth()))
            bankProfileVO.setRefundsvolume_lastmonth(requestBankProfileVO.getRefundsvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago()))
            bankProfileVO.setRefundsvolume_2monthsago(requestBankProfileVO.getRefundsvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago()))
            bankProfileVO.setRefundsvolume_3monthsago(requestBankProfileVO.getRefundsvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago()))
            bankProfileVO.setRefundsvolume_4monthsago(requestBankProfileVO.getRefundsvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago()))
            bankProfileVO.setRefundsvolume_5monthsago(requestBankProfileVO.getRefundsvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago()))
            bankProfileVO.setRefundsvolume_6monthsago(requestBankProfileVO.getRefundsvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_lastmonth()))
            bankProfileVO.setNumberofrefunds_lastmonth(requestBankProfileVO.getNumberofrefunds_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_2monthsago()))
            bankProfileVO.setNumberofrefunds_2monthsago(requestBankProfileVO.getNumberofrefunds_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_3monthsago()))
            bankProfileVO.setNumberofrefunds_3monthsago(requestBankProfileVO.getNumberofrefunds_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_4monthsago()))
            bankProfileVO.setNumberofrefunds_4monthsago(requestBankProfileVO.getNumberofrefunds_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_5monthsago()))
            bankProfileVO.setNumberofrefunds_5monthsago(requestBankProfileVO.getNumberofrefunds_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_6monthsago()))
            bankProfileVO.setNumberofrefunds_6monthsago(requestBankProfileVO.getNumberofrefunds_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()) && ESAPI.validator().isValidInput("salesvolume_lastmonth", bankProfileVO.getSalesvolume_lastmonth(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_lastmonth", bankProfileVO.getChargebackvolume_lastmonth(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_lastmonth(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_lastmonth()) *100)/Integer.valueOf(bankProfileVO.getSalesvolume_lastmonth())));
        }
        bankProfileVO.setChargebackratio_lastmonth(requestBankProfileVO.getChargebackratio_lastmonth());

        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago()) && functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()) && ESAPI.validator().isValidInput("salesvolume_2monthsago", bankProfileVO.getSalesvolume_2monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_2monthsago", bankProfileVO.getChargebackvolume_2monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_2monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_2monthsago())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_2monthsago())));
        }
        bankProfileVO.setChargebackratio_2monthsago(requestBankProfileVO.getChargebackratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()) && ESAPI.validator().isValidInput("salesvolume_3monthsago", bankProfileVO.getSalesvolume_3monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_3monthsago", bankProfileVO.getChargebackvolume_3monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_3monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_3monthsago())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_3monthsago())));
        }
        bankProfileVO.setChargebackratio_3monthsago(requestBankProfileVO.getChargebackratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()) && ESAPI.validator().isValidInput("salesvolume_4monthsago", bankProfileVO.getSalesvolume_4monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_4monthsago", bankProfileVO.getChargebackvolume_4monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_4monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_4monthsago())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_4monthsago()) ));
        }
        bankProfileVO.setChargebackratio_4monthsago(requestBankProfileVO.getChargebackratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()) && ESAPI.validator().isValidInput("salesvolume_5monthsago", bankProfileVO.getSalesvolume_5monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_5monthsago", bankProfileVO.getChargebackvolume_5monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_5monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_5monthsago()) *100)/ Integer.valueOf(bankProfileVO.getSalesvolume_5monthsago()) ));
        }
        bankProfileVO.setChargebackratio_5monthsago(requestBankProfileVO.getChargebackratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()) && ESAPI.validator().isValidInput("salesvolume_6monthsago", bankProfileVO.getSalesvolume_6monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_6monthsago", bankProfileVO.getChargebackvolume_6monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_6monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_6monthsago())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_6monthsago()) ));
        }
        bankProfileVO.setChargebackratio_6monthsago(requestBankProfileVO.getChargebackratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()) && ESAPI.validator().isValidInput("salesvolume_lastmonth", bankProfileVO.getSalesvolume_lastmonth(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_lastmonth", bankProfileVO.getRefundsvolume_lastmonth(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_lastmonth(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_lastmonth())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_lastmonth()) ));
        }
        bankProfileVO.setRefundratio_lastmonth(requestBankProfileVO.getRefundratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago()) && ESAPI.validator().isValidInput("salesvolume_2monthsago", bankProfileVO.getSalesvolume_2monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_2monthsago", bankProfileVO.getRefundsvolume_2monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_2monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_2monthsago()) *100)/ Integer.valueOf(bankProfileVO.getSalesvolume_2monthsago()) ));
        }
        bankProfileVO.setRefundratio_2monthsago(requestBankProfileVO.getRefundratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()) && ESAPI.validator().isValidInput("salesvolume_3monthsago", bankProfileVO.getSalesvolume_3monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_3monthsago", bankProfileVO.getRefundsvolume_3monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_3monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_3monthsago())*100) / Integer.valueOf(bankProfileVO.getSalesvolume_3monthsago()) ));
        }
        bankProfileVO.setRefundratio_3monthsago(requestBankProfileVO.getRefundratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()) && ESAPI.validator().isValidInput("salesvolume_4monthsago", bankProfileVO.getSalesvolume_4monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_4monthsago", bankProfileVO.getRefundsvolume_4monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_4monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_4monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_4monthsago())));
        }
        bankProfileVO.setRefundratio_4monthsago(requestBankProfileVO.getRefundratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()) && ESAPI.validator().isValidInput("salesvolume_5monthsago", bankProfileVO.getSalesvolume_5monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_5monthsago", bankProfileVO.getRefundsvolume_5monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_5monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_5monthsago()) *100)/ Integer.valueOf(bankProfileVO.getSalesvolume_5monthsago()) ));
        }
        bankProfileVO.setRefundratio_5monthsago(requestBankProfileVO.getRefundratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()) && ESAPI.validator().isValidInput("salesvolume_6monthsago", bankProfileVO.getSalesvolume_6monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_6monthsago", bankProfileVO.getRefundsvolume_6monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_6monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_6monthsago()) *100)/ Integer.valueOf(bankProfileVO.getSalesvolume_6monthsago()) ));
        }
        bankProfileVO.setRefundratio_6monthsago(requestBankProfileVO.getRefundratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_INR()))
            bankProfileVO.setCurrency_products_INR(requestBankProfileVO.getCurrency_products_INR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_USD()))
            bankProfileVO.setCurrency_products_USD(requestBankProfileVO.getCurrency_products_USD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_EUR()))
            bankProfileVO.setCurrency_products_EUR(requestBankProfileVO.getCurrency_products_EUR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_GBP()))
            bankProfileVO.setCurrency_products_GBP(requestBankProfileVO.getCurrency_products_GBP());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_JPY()))
            bankProfileVO.setCurrency_products_JPY(requestBankProfileVO.getCurrency_products_JPY());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_PEN()))
            bankProfileVO.setCurrency_products_PEN(requestBankProfileVO.getCurrency_products_PEN());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_INR()))
            bankProfileVO.setCurrency_payments_INR(requestBankProfileVO.getCurrency_payments_INR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_USD()))
            bankProfileVO.setCurrency_payments_USD(requestBankProfileVO.getCurrency_payments_USD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_EUR()))
            bankProfileVO.setCurrency_payments_EUR(requestBankProfileVO.getCurrency_payments_EUR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_GBP()))
            bankProfileVO.setCurrency_payments_GBP(requestBankProfileVO.getCurrency_payments_GBP());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_JPY()))
            bankProfileVO.setCurrency_payments_JPY(requestBankProfileVO.getCurrency_payments_JPY());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_PEN()))
            bankProfileVO.setCurrency_payments_PEN(requestBankProfileVO.getCurrency_payments_PEN());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_HKD()))
            bankProfileVO.setCurrency_products_HKD(requestBankProfileVO.getCurrency_products_HKD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_AUD()))
            bankProfileVO.setCurrency_products_AUD(requestBankProfileVO.getCurrency_products_AUD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_CAD()))
            bankProfileVO.setCurrency_products_CAD(requestBankProfileVO.getCurrency_products_CAD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_DKK()))
            bankProfileVO.setCurrency_products_DKK(requestBankProfileVO.getCurrency_products_DKK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_SEK()))
            bankProfileVO.setCurrency_products_SEK(requestBankProfileVO.getCurrency_products_SEK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_NOK()))
            bankProfileVO.setCurrency_products_NOK(requestBankProfileVO.getCurrency_products_NOK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_HKD()))
            bankProfileVO.setCurrency_payments_HKD(requestBankProfileVO.getCurrency_payments_HKD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_AUD()))
            bankProfileVO.setCurrency_payments_AUD(requestBankProfileVO.getCurrency_payments_AUD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_CAD()))
            bankProfileVO.setCurrency_payments_CAD(requestBankProfileVO.getCurrency_payments_CAD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_DKK()))
            bankProfileVO.setCurrency_payments_DKK(requestBankProfileVO.getCurrency_payments_DKK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_SEK()))
            bankProfileVO.setCurrency_payments_SEK(requestBankProfileVO.getCurrency_payments_SEK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_NOK()))
            bankProfileVO.setCurrency_payments_NOK(requestBankProfileVO.getCurrency_payments_NOK());
        if(functions.isValueNull(requestBankProfileVO.getAquirer()))
            bankProfileVO.setAquirer(requestBankProfileVO.getAquirer());
        if(functions.isValueNull(requestBankProfileVO.getReasonaquirer()))
            bankProfileVO.setReasonaquirer(requestBankProfileVO.getReasonaquirer());
        if(functions.isValueNull(requestBankProfileVO.getBankcontactperson()))
            bankProfileVO.setBankcontactperson(requestBankProfileVO.getBankcontactperson());
    }
    private void setBankProfileVO(BankProfileVO requestBankProfileVO,BankProfileVO bankProfileVO,ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_productssold())&& validationErrorList.getError("currencyrequested_productssold")==null)
            bankProfileVO.setCurrencyrequested_productssold(requestBankProfileVO.getCurrencyrequested_productssold());
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_bankaccount())&& validationErrorList.getError("currencyrequested_bankaccount")==null)
            bankProfileVO.setCurrencyrequested_bankaccount(requestBankProfileVO.getCurrencyrequested_bankaccount());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bic())&& validationErrorList.getError("bankinfo_bic")==null)
            bankProfileVO.setBankinfo_bic(requestBankProfileVO.getBankinfo_bic());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bank_name())&& validationErrorList.getError("bankinfo_bank_name")==null)
            bankProfileVO.setBankinfo_bank_name(requestBankProfileVO.getBankinfo_bank_name());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankaddress())&& validationErrorList.getError("bankinfo_bankaddress")==null)
            bankProfileVO.setBankinfo_bankaddress(requestBankProfileVO.getBankinfo_bankaddress());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankphonenumber())&& validationErrorList.getError("bankinfo_bankphonenumber")==null)
            bankProfileVO.setBankinfo_bankphonenumber(requestBankProfileVO.getBankinfo_bankphonenumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_aba_routingcode())&& validationErrorList.getError("bankinfo_aba_routingcode")==null)
            bankProfileVO.setBankinfo_aba_routingcode(requestBankProfileVO.getBankinfo_aba_routingcode());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountholder())&& validationErrorList.getError("bankinfo_accountholder")==null)
            bankProfileVO.setBankinfo_accountholder(requestBankProfileVO.getBankinfo_accountholder());
        if(functions.isValueNull(requestBankProfileVO.getBank_accountnumber_IBAN())&& validationErrorList.getError("bank_accountnumber_IBAN")==null)
            bankProfileVO.setBank_accountnumber_IBAN(requestBankProfileVO.getBank_accountnumber_IBAN());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth())&& validationErrorList.getError("salesvolume_lastmonth")==null)
            bankProfileVO.setSalesvolume_lastmonth(requestBankProfileVO.getSalesvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago())&& validationErrorList.getError("salesvolume_2monthsago")==null)
            bankProfileVO.setSalesvolume_2monthsago(requestBankProfileVO.getSalesvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago())&& validationErrorList.getError("salesvolume_3monthsago")==null)
            bankProfileVO.setSalesvolume_3monthsago(requestBankProfileVO.getSalesvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago())&& validationErrorList.getError("salesvolume_4monthsago")==null)
            bankProfileVO.setSalesvolume_4monthsago(requestBankProfileVO.getSalesvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago())&& validationErrorList.getError("salesvolume_5monthsago")==null)
            bankProfileVO.setSalesvolume_5monthsago(requestBankProfileVO.getSalesvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago())&& validationErrorList.getError("salesvolume_6monthsago")==null)
            bankProfileVO.setSalesvolume_6monthsago(requestBankProfileVO.getSalesvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_lastmonth())&& validationErrorList.getError("numberoftransactions_lastmonth")==null)
            bankProfileVO.setNumberoftransactions_lastmonth(requestBankProfileVO.getNumberoftransactions_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_2monthsago())&& validationErrorList.getError("numberoftransactions_2monthsago")==null)
            bankProfileVO.setNumberoftransactions_2monthsago(requestBankProfileVO.getNumberoftransactions_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_3monthsago())&& validationErrorList.getError("numberoftransactions_3monthsago")==null)
            bankProfileVO.setNumberoftransactions_3monthsago(requestBankProfileVO.getNumberoftransactions_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_4monthsago())&& validationErrorList.getError("numberoftransactions_4monthsago")==null)
            bankProfileVO.setNumberoftransactions_4monthsago(requestBankProfileVO.getNumberoftransactions_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_5monthsago())&& validationErrorList.getError("numberoftransactions_5monthsago")==null)
            bankProfileVO.setNumberoftransactions_5monthsago(requestBankProfileVO.getNumberoftransactions_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_6monthsago())&& validationErrorList.getError("numberoftransactions_6monthsago")==null)
            bankProfileVO.setNumberoftransactions_6monthsago(requestBankProfileVO.getNumberoftransactions_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth())&& validationErrorList.getError("chargebackvolume_lastmonth")==null)
            bankProfileVO.setChargebackvolume_lastmonth(requestBankProfileVO.getChargebackvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago())&& validationErrorList.getError("chargebackvolume_2monthsago")==null)
            bankProfileVO.setChargebackvolume_2monthsago(requestBankProfileVO.getChargebackvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago())&& validationErrorList.getError("chargebackvolume_3monthsago")==null)
            bankProfileVO.setChargebackvolume_3monthsago(requestBankProfileVO.getChargebackvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago())&& validationErrorList.getError("chargebackvolume_4monthsago")==null)
            bankProfileVO.setChargebackvolume_4monthsago(requestBankProfileVO.getChargebackvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago())&& validationErrorList.getError("chargebackvolume_5monthsago")==null)
            bankProfileVO.setChargebackvolume_5monthsago(requestBankProfileVO.getChargebackvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago())&& validationErrorList.getError("chargebackvolume_6monthsago")==null)
            bankProfileVO.setChargebackvolume_6monthsago(requestBankProfileVO.getChargebackvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_lastmonth())&& validationErrorList.getError("numberofchargebacks_lastmonth")==null)
            bankProfileVO.setNumberofchargebacks_lastmonth(requestBankProfileVO.getNumberofchargebacks_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_2monthsago())&& validationErrorList.getError("numberofchargebacks_2monthsago")==null)
            bankProfileVO.setNumberofchargebacks_2monthsago(requestBankProfileVO.getNumberofchargebacks_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_3monthsago())&& validationErrorList.getError("numberofchargebacks_3monthsago")==null)
            bankProfileVO.setNumberofchargebacks_3monthsago(requestBankProfileVO.getNumberofchargebacks_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_4monthsago())&& validationErrorList.getError("numberofchargebacks_4monthsago")==null)
            bankProfileVO.setNumberofchargebacks_4monthsago(requestBankProfileVO.getNumberofchargebacks_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_5monthsago())&& validationErrorList.getError("numberofchargebacks_5monthsago")==null)
            bankProfileVO.setNumberofchargebacks_5monthsago(requestBankProfileVO.getNumberofchargebacks_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_6monthsago())&& validationErrorList.getError("numberofchargebacks_6monthsago")==null)
            bankProfileVO.setNumberofchargebacks_6monthsago(requestBankProfileVO.getNumberofchargebacks_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth())&& validationErrorList.getError("refundsvolume_lastmonth")==null)
            bankProfileVO.setRefundsvolume_lastmonth(requestBankProfileVO.getRefundsvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago())&& validationErrorList.getError("refundsvolume_2monthsago")==null)
            bankProfileVO.setRefundsvolume_2monthsago(requestBankProfileVO.getRefundsvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago())&& validationErrorList.getError("refundsvolume_3monthsago")==null)
            bankProfileVO.setRefundsvolume_3monthsago(requestBankProfileVO.getRefundsvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago())&& validationErrorList.getError("refundsvolume_4monthsago")==null)
            bankProfileVO.setRefundsvolume_4monthsago(requestBankProfileVO.getRefundsvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago())&& validationErrorList.getError("refundsvolume_5monthsago")==null)
            bankProfileVO.setRefundsvolume_5monthsago(requestBankProfileVO.getRefundsvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago())&& validationErrorList.getError("refundsvolume_6monthsago")==null)
            bankProfileVO.setRefundsvolume_6monthsago(requestBankProfileVO.getRefundsvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_lastmonth())&& validationErrorList.getError("numberofrefunds_lastmonth")==null)
            bankProfileVO.setNumberofrefunds_lastmonth(requestBankProfileVO.getNumberofrefunds_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_2monthsago())&& validationErrorList.getError("numberofrefunds_2monthsago")==null)
            bankProfileVO.setNumberofrefunds_2monthsago(requestBankProfileVO.getNumberofrefunds_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_3monthsago())&& validationErrorList.getError("numberofrefunds_3monthsago")==null)
            bankProfileVO.setNumberofrefunds_3monthsago(requestBankProfileVO.getNumberofrefunds_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_4monthsago())&& validationErrorList.getError("numberofrefunds_4monthsago")==null)
            bankProfileVO.setNumberofrefunds_4monthsago(requestBankProfileVO.getNumberofrefunds_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_5monthsago())&& validationErrorList.getError("numberofrefunds_5monthsago")==null)
            bankProfileVO.setNumberofrefunds_5monthsago(requestBankProfileVO.getNumberofrefunds_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_6monthsago())&& validationErrorList.getError("numberofrefunds_6monthsago")==null)
            bankProfileVO.setNumberofrefunds_6monthsago(requestBankProfileVO.getNumberofrefunds_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_lastmonth())&& validationErrorList.getError("chargebackratio_lastmonth")==null)
            bankProfileVO.setChargebackratio_lastmonth(requestBankProfileVO.getChargebackratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_2monthsago())&& validationErrorList.getError("chargebackratio_2monthsago")==null)
            bankProfileVO.setChargebackratio_2monthsago(requestBankProfileVO.getChargebackratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_3monthsago())&& validationErrorList.getError("chargebackratio_3monthsago")==null)
            bankProfileVO.setChargebackratio_3monthsago(requestBankProfileVO.getChargebackratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_4monthsago())&& validationErrorList.getError("chargebackratio_4monthsago")==null)
            bankProfileVO.setChargebackratio_4monthsago(requestBankProfileVO.getChargebackratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_5monthsago())&& validationErrorList.getError("chargebackratio_5monthsago")==null)
            bankProfileVO.setChargebackratio_5monthsago(requestBankProfileVO.getChargebackratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_6monthsago())&& validationErrorList.getError("chargebackratio_6monthsago")==null)
            bankProfileVO.setChargebackratio_6monthsago(requestBankProfileVO.getChargebackratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_lastmonth())&& validationErrorList.getError("refundratio_lastmonth")==null)
            bankProfileVO.setRefundratio_lastmonth(requestBankProfileVO.getRefundratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_2monthsago())&& validationErrorList.getError("refundratio_2monthsago")==null)
            bankProfileVO.setRefundratio_2monthsago(requestBankProfileVO.getRefundratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_3monthsago())&& validationErrorList.getError("refundratio_3monthsago")==null)
            bankProfileVO.setRefundratio_3monthsago(requestBankProfileVO.getRefundratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_4monthsago())&& validationErrorList.getError("refundratio_4monthsago")==null)
            bankProfileVO.setRefundratio_4monthsago(requestBankProfileVO.getRefundratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_5monthsago())&& validationErrorList.getError("refundratio_5monthsago")==null)
            bankProfileVO.setRefundratio_5monthsago(requestBankProfileVO.getRefundratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_6monthsago())&& validationErrorList.getError("refundratio_6monthsago")==null)
            bankProfileVO.setRefundratio_6monthsago(requestBankProfileVO.getRefundratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_INR())&& validationErrorList.getError("currency_products_INR")==null)
            bankProfileVO.setCurrency_products_INR(requestBankProfileVO.getCurrency_products_INR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_USD())&& validationErrorList.getError("currency_products_USD")==null)
            bankProfileVO.setCurrency_products_USD(requestBankProfileVO.getCurrency_products_USD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_EUR())&& validationErrorList.getError("currency_products_EUR")==null)
            bankProfileVO.setCurrency_products_EUR(requestBankProfileVO.getCurrency_products_EUR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_GBP())&& validationErrorList.getError("currency_products_GBP")==null)
            bankProfileVO.setCurrency_products_GBP(requestBankProfileVO.getCurrency_products_GBP());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_JPY())&& validationErrorList.getError("currency_products_JPY")==null)
            bankProfileVO.setCurrency_products_JPY(requestBankProfileVO.getCurrency_products_JPY());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_PEN())&& validationErrorList.getError("currency_products_PEN")==null)
            bankProfileVO.setCurrency_products_PEN(requestBankProfileVO.getCurrency_products_PEN());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_INR())&& validationErrorList.getError("currency_payments_INR")==null)
            bankProfileVO.setCurrency_payments_INR(requestBankProfileVO.getCurrency_payments_INR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_USD())&& validationErrorList.getError("currency_payments_USD")==null)
            bankProfileVO.setCurrency_payments_USD(requestBankProfileVO.getCurrency_payments_USD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_EUR())&& validationErrorList.getError("currency_payments_EUR")==null)
            bankProfileVO.setCurrency_payments_EUR(requestBankProfileVO.getCurrency_payments_EUR());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_GBP())&& validationErrorList.getError("currency_payments_GBP")==null)
            bankProfileVO.setCurrency_payments_GBP(requestBankProfileVO.getCurrency_payments_GBP());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_JPY())&& validationErrorList.getError("currency_payments_JPY")==null)
            bankProfileVO.setCurrency_payments_JPY(requestBankProfileVO.getCurrency_payments_JPY());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_PEN())&& validationErrorList.getError("currency_payments_PEN")==null)
            bankProfileVO.setCurrency_payments_PEN(requestBankProfileVO.getCurrency_payments_PEN());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_HKD())&& validationErrorList.getError("currency_products_HKD")==null)
            bankProfileVO.setCurrency_products_HKD(requestBankProfileVO.getCurrency_products_HKD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_AUD())&& validationErrorList.getError("currency_products_AUD")==null)
            bankProfileVO.setCurrency_products_AUD(requestBankProfileVO.getCurrency_products_AUD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_CAD())&& validationErrorList.getError("currency_products_CAD")==null)
            bankProfileVO.setCurrency_products_CAD(requestBankProfileVO.getCurrency_products_CAD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_DKK())&& validationErrorList.getError("currency_products_DKK")==null)
            bankProfileVO.setCurrency_products_DKK(requestBankProfileVO.getCurrency_products_DKK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_SEK())&& validationErrorList.getError("currency_products_SEK")==null)
            bankProfileVO.setCurrency_products_SEK(requestBankProfileVO.getCurrency_products_SEK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_NOK())&& validationErrorList.getError("currency_products_NOK")==null)
            bankProfileVO.setCurrency_products_NOK(requestBankProfileVO.getCurrency_products_NOK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_HKD())&& validationErrorList.getError("currency_payments_HKD")==null)
            bankProfileVO.setCurrency_payments_HKD(requestBankProfileVO.getCurrency_payments_HKD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_AUD())&& validationErrorList.getError("currency_payments_AUD")==null)
            bankProfileVO.setCurrency_payments_AUD(requestBankProfileVO.getCurrency_payments_AUD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_CAD())&& validationErrorList.getError("currency_payments_CAD")==null)
            bankProfileVO.setCurrency_payments_CAD(requestBankProfileVO.getCurrency_payments_CAD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_DKK())&& validationErrorList.getError("currency_payments_DKK")==null)
            bankProfileVO.setCurrency_payments_DKK(requestBankProfileVO.getCurrency_payments_DKK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_SEK())&& validationErrorList.getError("currency_payments_SEK")==null)
            bankProfileVO.setCurrency_payments_SEK(requestBankProfileVO.getCurrency_payments_SEK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_payments_NOK())&& validationErrorList.getError("currency_payments_NOK")==null)
            bankProfileVO.setCurrency_payments_NOK(requestBankProfileVO.getCurrency_payments_NOK());
        if(functions.isValueNull(requestBankProfileVO.getAquirer())&& validationErrorList.getError("getAquirer")==null)
            bankProfileVO.setAquirer(requestBankProfileVO.getAquirer());
        if(functions.isValueNull(requestBankProfileVO.getReasonaquirer())&& validationErrorList.getError("reason_aquirer")==null)
            bankProfileVO.setReasonaquirer(requestBankProfileVO.getReasonaquirer());
        if(functions.isValueNull(requestBankProfileVO.getBankcontactperson())&& validationErrorList.getError("bankcontactperson")==null)
            bankProfileVO.setBankcontactperson(requestBankProfileVO.getBankcontactperson());
    }*/

    private void setBankProfileVO(HttpServletRequest request,BankProfileVO bankProfileVO)
    {
        bankProfileVO.setCurrencyrequested_productssold(request.getParameter("currencyrequested_productssold"));
        bankProfileVO.setCurrencyrequested_bankaccount(request.getParameter("currencyrequested_bankaccount"));
        bankProfileVO.setBankinfo_bic(request.getParameter("bankinfo_bic"));
        bankProfileVO.setBankinfo_bank_name(request.getParameter("bankinfo_bank_name"));
        bankProfileVO.setBankinfo_bankaddress(request.getParameter("bankinfo_bankaddress"));
        bankProfileVO.setBankinfo_bankphonenumber(request.getParameter("bankinfo_bankphonenumber"));
        bankProfileVO.setBankinfo_aba_routingcode(request.getParameter("bankinfo_aba_routingcode"));
        bankProfileVO.setBankinfo_accountholder(request.getParameter("bankinfo_accountholder"));
        bankProfileVO.setBankinfo_accountnumber(request.getParameter("bankinfo_accountnumber"));
        bankProfileVO.setBankinfo_IBAN(request.getParameter("bankinfo_IBAN"));
        bankProfileVO.setBankinfo_currency(request.getParameter("bankinfo_currency"));

        bankProfileVO.setSalesvolume_lastmonth(request.getParameter("salesvolume_lastmonth"));
        bankProfileVO.setSalesvolume_2monthsago(request.getParameter("salesvolume_2monthsago"));
        bankProfileVO.setSalesvolume_3monthsago(request.getParameter("salesvolume_3monthsago"));
        bankProfileVO.setSalesvolume_4monthsago(request.getParameter("salesvolume_4monthsago"));
        bankProfileVO.setSalesvolume_5monthsago(request.getParameter("salesvolume_5monthsago"));
        bankProfileVO.setSalesvolume_6monthsago(request.getParameter("salesvolume_6monthsago"));
        bankProfileVO.setSalesvolume_12monthsago(request.getParameter("salesvolume_12monthsago"));
        bankProfileVO.setSalesvolume_year2(request.getParameter("salesvolume_year2"));
        bankProfileVO.setSalesvolume_year3(request.getParameter("salesvolume_year3"));
        bankProfileVO.setNumberoftransactions_lastmonth(request.getParameter("numberoftransactions_lastmonth"));
        bankProfileVO.setNumberoftransactions_2monthsago(request.getParameter("numberoftransactions_2monthsago"));
        bankProfileVO.setNumberoftransactions_3monthsago(request.getParameter("numberoftransactions_3monthsago"));
        bankProfileVO.setNumberoftransactions_4monthsago(request.getParameter("numberoftransactions_4monthsago"));
        bankProfileVO.setNumberoftransactions_5monthsago(request.getParameter("numberoftransactions_5monthsago"));
        bankProfileVO.setNumberoftransactions_6monthsago(request.getParameter("numberoftransactions_6monthsago"));
        bankProfileVO.setNumberoftransactions_12monthsago(request.getParameter("numberoftransactions_12monthsago"));
        bankProfileVO.setNumberoftransactions_year2(request.getParameter("numberoftransactions_year2"));
        bankProfileVO.setNumberoftransactions_year3(request.getParameter("numberoftransactions_year3"));
        bankProfileVO.setChargebackvolume_lastmonth(request.getParameter("chargebackvolume_lastmonth"));
        bankProfileVO.setChargebackvolume_2monthsago(request.getParameter("chargebackvolume_2monthsago"));
        bankProfileVO.setChargebackvolume_3monthsago(request.getParameter("chargebackvolume_3monthsago"));
        bankProfileVO.setChargebackvolume_4monthsago(request.getParameter("chargebackvolume_4monthsago"));
        bankProfileVO.setChargebackvolume_5monthsago(request.getParameter("chargebackvolume_5monthsago"));
        bankProfileVO.setChargebackvolume_6monthsago(request.getParameter("chargebackvolume_6monthsago"));
        bankProfileVO.setChargebackvolume_12monthsago(request.getParameter("chargebackvolume_12monthsago"));
        bankProfileVO.setChargebackvolume_year2(request.getParameter("chargebackvolume_year2"));
        bankProfileVO.setChargebackvolume_year3(request.getParameter("chargebackvolume_year3"));
        bankProfileVO.setNumberofchargebacks_lastmonth(request.getParameter("numberofchargebacks_lastmonth"));
        bankProfileVO.setNumberofchargebacks_2monthsago(request.getParameter("numberofchargebacks_2monthsago"));
        bankProfileVO.setNumberofchargebacks_3monthsago(request.getParameter("numberofchargebacks_3monthsago"));
        bankProfileVO.setNumberofchargebacks_4monthsago(request.getParameter("numberofchargebacks_4monthsago"));
        bankProfileVO.setNumberofchargebacks_5monthsago(request.getParameter("numberofchargebacks_5monthsago"));
        bankProfileVO.setNumberofchargebacks_6monthsago(request.getParameter("numberofchargebacks_6monthsago"));
        bankProfileVO.setNumberofchargebacks_12monthsago(request.getParameter("numberofchargebacks_12monthsago"));
        bankProfileVO.setNumberofchargebacks_year2(request.getParameter("numberofchargebacks_year2"));
        bankProfileVO.setNumberofchargebacks_year3(request.getParameter("numberofchargebacks_year3"));
        bankProfileVO.setRefundsvolume_lastmonth(request.getParameter("refundsvolume_lastmonth"));
        bankProfileVO.setRefundsvolume_2monthsago(request.getParameter("refundsvolume_2monthsago"));
        bankProfileVO.setRefundsvolume_3monthsago(request.getParameter("refundsvolume_3monthsago"));
        bankProfileVO.setRefundsvolume_4monthsago(request.getParameter("refundsvolume_4monthsago"));
        bankProfileVO.setRefundsvolume_5monthsago(request.getParameter("refundsvolume_5monthsago"));
        bankProfileVO.setRefundsvolume_6monthsago(request.getParameter("refundsvolume_6monthsago"));
        bankProfileVO.setRefundsvolume_12monthsago(request.getParameter("refundsvolume_12monthsago"));
        bankProfileVO.setRefundsvolume_year2(request.getParameter("refundsvolume_year2"));
        bankProfileVO.setRefundsvolume_year3(request.getParameter("refundsvolume_year3"));
        bankProfileVO.setNumberofrefunds_lastmonth(request.getParameter("numberofrefunds_lastmonth"));
        bankProfileVO.setNumberofrefunds_2monthsago(request.getParameter("numberofrefunds_2monthsago"));
        bankProfileVO.setNumberofrefunds_3monthsago(request.getParameter("numberofrefunds_3monthsago"));
        bankProfileVO.setNumberofrefunds_4monthsago(request.getParameter("numberofrefunds_4monthsago"));
        bankProfileVO.setNumberofrefunds_5monthsago(request.getParameter("numberofrefunds_5monthsago"));
        bankProfileVO.setNumberofrefunds_6monthsago(request.getParameter("numberofrefunds_6monthsago"));
        bankProfileVO.setNumberofrefunds_12monthsago(request.getParameter("numberofrefunds_12monthsago"));
        bankProfileVO.setNumberofrefunds_year2(request.getParameter("numberofrefunds_year2"));
        bankProfileVO.setNumberofrefunds_year3(request.getParameter("numberofrefunds_year3"));
        bankProfileVO.setChargebackratio_lastmonth(request.getParameter("chargebackratio_lastmonth"));
        bankProfileVO.setChargebackratio_2monthsago(request.getParameter("chargebackratio_2monthsago"));
        bankProfileVO.setChargebackratio_3monthsago(request.getParameter("chargebackratio_3monthsago"));
        bankProfileVO.setChargebackratio_4monthsago(request.getParameter("chargebackratio_4monthsago"));
        bankProfileVO.setChargebackratio_5monthsago(request.getParameter("chargebackratio_5monthsago"));
        bankProfileVO.setChargebackratio_6monthsago(request.getParameter("chargebackratio_6monthsago"));
        bankProfileVO.setChargebackratio_12monthsago(request.getParameter("chargebackratio_12monthsago"));
        bankProfileVO.setChargebackratio_year2(request.getParameter("chargebackratio_year2"));
        bankProfileVO.setChargebackratio_year3(request.getParameter("chargebackratio_year3"));
        bankProfileVO.setRefundratio_lastmonth(request.getParameter("refundratio_lastmonth"));
        bankProfileVO.setRefundratio_2monthsago(request.getParameter("refundratio_2monthsago"));
        bankProfileVO.setRefundratio_3monthsago(request.getParameter("refundratio_3monthsago"));
        bankProfileVO.setRefundratio_4monthsago(request.getParameter("refundratio_4monthsago"));
        bankProfileVO.setRefundratio_5monthsago(request.getParameter("refundratio_5monthsago"));
        bankProfileVO.setRefundratio_6monthsago(request.getParameter("refundratio_6monthsago"));
        bankProfileVO.setRefundratio_12monthsago(request.getParameter("refundratio_12monthsago"));
        bankProfileVO.setRefundratio_year2(request.getParameter("refundratio_year2"));
        bankProfileVO.setRefundratio_year3(request.getParameter("refundratio_year3"));

        bankProfileVO.setCurrency(request.getParameter("currency"));
        bankProfileVO.setBank_account_currencies(request.getParameter("bank_account_currencies"));
        //bankProfileVO.setProduct_sold_currencies(request.getParameter("product_sold_currencies"));

        //add NEW
        bankProfileVO.setAquirer(request.getParameter("aquirer"));
        bankProfileVO.setReasonaquirer(request.getParameter("reason_aquirer"));
        bankProfileVO.setBankinfo_contactperson(request.getParameter("bankinfo_contactperson"));
        bankProfileVO.setCustomer_trans_data(request.getParameter("customer_trans_data"));
    }

    //set bank profile for API
    private void setBankProfileVO(BankProfileVO requestBankProfileVO,BankProfileVO bankProfileVO)
    {
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_productssold()))
            bankProfileVO.setCurrencyrequested_productssold(requestBankProfileVO.getCurrencyrequested_productssold());
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_bankaccount()))
            bankProfileVO.setCurrencyrequested_bankaccount(requestBankProfileVO.getCurrencyrequested_bankaccount());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bic()))
            bankProfileVO.setBankinfo_bic(requestBankProfileVO.getBankinfo_bic());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bank_name()))
            bankProfileVO.setBankinfo_bank_name(requestBankProfileVO.getBankinfo_bank_name());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankaddress()))
            bankProfileVO.setBankinfo_bankaddress(requestBankProfileVO.getBankinfo_bankaddress());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankphonenumber()))
            bankProfileVO.setBankinfo_bankphonenumber(requestBankProfileVO.getBankinfo_bankphonenumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_aba_routingcode()))
            bankProfileVO.setBankinfo_aba_routingcode(requestBankProfileVO.getBankinfo_aba_routingcode());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountholder()))
            bankProfileVO.setBankinfo_accountholder(requestBankProfileVO.getBankinfo_accountholder());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountnumber()))
            bankProfileVO.setBankinfo_accountnumber(requestBankProfileVO.getBankinfo_accountnumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_IBAN()))
            bankProfileVO.setBankinfo_IBAN(requestBankProfileVO.getBankinfo_IBAN());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_currency()))
            bankProfileVO.setBankinfo_currency(requestBankProfileVO.getBankinfo_currency());

        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()))
            bankProfileVO.setSalesvolume_lastmonth(requestBankProfileVO.getSalesvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago()))
            bankProfileVO.setSalesvolume_2monthsago(requestBankProfileVO.getSalesvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()))
            bankProfileVO.setSalesvolume_3monthsago(requestBankProfileVO.getSalesvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()))
            bankProfileVO.setSalesvolume_4monthsago(requestBankProfileVO.getSalesvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()))
            bankProfileVO.setSalesvolume_5monthsago(requestBankProfileVO.getSalesvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()))
            bankProfileVO.setSalesvolume_6monthsago(requestBankProfileVO.getSalesvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_12monthsago()))
            bankProfileVO.setSalesvolume_12monthsago(requestBankProfileVO.getSalesvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_year2()))
            bankProfileVO.setSalesvolume_year2(requestBankProfileVO.getSalesvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_year3()))
            bankProfileVO.setSalesvolume_year3(requestBankProfileVO.getSalesvolume_year3());

        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_lastmonth()))
            bankProfileVO.setNumberoftransactions_lastmonth(requestBankProfileVO.getNumberoftransactions_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_2monthsago()))
            bankProfileVO.setNumberoftransactions_2monthsago(requestBankProfileVO.getNumberoftransactions_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_3monthsago()))
            bankProfileVO.setNumberoftransactions_3monthsago(requestBankProfileVO.getNumberoftransactions_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_4monthsago()))
            bankProfileVO.setNumberoftransactions_4monthsago(requestBankProfileVO.getNumberoftransactions_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_5monthsago()))
            bankProfileVO.setNumberoftransactions_5monthsago(requestBankProfileVO.getNumberoftransactions_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_6monthsago()))
            bankProfileVO.setNumberoftransactions_6monthsago(requestBankProfileVO.getNumberoftransactions_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_12monthsago()))
            bankProfileVO.setNumberoftransactions_12monthsago(requestBankProfileVO.getNumberoftransactions_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_year2()))
            bankProfileVO.setNumberoftransactions_year2(requestBankProfileVO.getNumberoftransactions_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_year3()))
            bankProfileVO.setNumberoftransactions_year3(requestBankProfileVO.getNumberoftransactions_year3());

        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth()))
            bankProfileVO.setChargebackvolume_lastmonth(requestBankProfileVO.getChargebackvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago()))
            bankProfileVO.setChargebackvolume_2monthsago(requestBankProfileVO.getChargebackvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago()))
            bankProfileVO.setChargebackvolume_3monthsago(requestBankProfileVO.getChargebackvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago()))
            bankProfileVO.setChargebackvolume_4monthsago(requestBankProfileVO.getChargebackvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago()))
            bankProfileVO.setChargebackvolume_5monthsago(requestBankProfileVO.getChargebackvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago()))
            bankProfileVO.setChargebackvolume_6monthsago(requestBankProfileVO.getChargebackvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_12monthsago()))
            bankProfileVO.setChargebackvolume_12monthsago(requestBankProfileVO.getChargebackvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year2()))
            bankProfileVO.setChargebackvolume_year2(requestBankProfileVO.getChargebackvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year3()))
            bankProfileVO.setChargebackvolume_year3(requestBankProfileVO.getChargebackvolume_year3());

        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_lastmonth()))
            bankProfileVO.setNumberofchargebacks_lastmonth(requestBankProfileVO.getNumberofchargebacks_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_2monthsago()))
            bankProfileVO.setNumberofchargebacks_2monthsago(requestBankProfileVO.getNumberofchargebacks_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_3monthsago()))
            bankProfileVO.setNumberofchargebacks_3monthsago(requestBankProfileVO.getNumberofchargebacks_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_4monthsago()))
            bankProfileVO.setNumberofchargebacks_4monthsago(requestBankProfileVO.getNumberofchargebacks_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_5monthsago()))
            bankProfileVO.setNumberofchargebacks_5monthsago(requestBankProfileVO.getNumberofchargebacks_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_6monthsago()))
            bankProfileVO.setNumberofchargebacks_6monthsago(requestBankProfileVO.getNumberofchargebacks_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_12monthsago()))
            bankProfileVO.setNumberofchargebacks_12monthsago(requestBankProfileVO.getNumberofchargebacks_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_year2()))
            bankProfileVO.setNumberofchargebacks_year2(requestBankProfileVO.getNumberofchargebacks_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_year3()))
            bankProfileVO.setNumberofchargebacks_year3(requestBankProfileVO.getNumberofchargebacks_year3());

        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth()))
            bankProfileVO.setRefundsvolume_lastmonth(requestBankProfileVO.getRefundsvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago()))
            bankProfileVO.setRefundsvolume_2monthsago(requestBankProfileVO.getRefundsvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago()))
            bankProfileVO.setRefundsvolume_3monthsago(requestBankProfileVO.getRefundsvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago()))
            bankProfileVO.setRefundsvolume_4monthsago(requestBankProfileVO.getRefundsvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago()))
            bankProfileVO.setRefundsvolume_5monthsago(requestBankProfileVO.getRefundsvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago()))
            bankProfileVO.setRefundsvolume_6monthsago(requestBankProfileVO.getRefundsvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago()))
            bankProfileVO.setRefundsvolume_12monthsago(requestBankProfileVO.getRefundsvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_12monthsago()))
            bankProfileVO.setRefundsvolume_year2(requestBankProfileVO.getRefundsvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_year3()))
            bankProfileVO.setRefundsvolume_year3(requestBankProfileVO.getRefundsvolume_year3());

        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_lastmonth()))
            bankProfileVO.setNumberofrefunds_lastmonth(requestBankProfileVO.getNumberofrefunds_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_2monthsago()))
            bankProfileVO.setNumberofrefunds_2monthsago(requestBankProfileVO.getNumberofrefunds_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_3monthsago()))
            bankProfileVO.setNumberofrefunds_3monthsago(requestBankProfileVO.getNumberofrefunds_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_4monthsago()))
            bankProfileVO.setNumberofrefunds_4monthsago(requestBankProfileVO.getNumberofrefunds_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_5monthsago()))
            bankProfileVO.setNumberofrefunds_5monthsago(requestBankProfileVO.getNumberofrefunds_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_6monthsago()))
            bankProfileVO.setNumberofrefunds_6monthsago(requestBankProfileVO.getNumberofrefunds_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_12monthsago()))
            bankProfileVO.setNumberofrefunds_12monthsago(requestBankProfileVO.getNumberofrefunds_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_year2()))
            bankProfileVO.setNumberofrefunds_year2(requestBankProfileVO.getNumberofrefunds_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_year3()))
            bankProfileVO.setNumberofrefunds_year3(requestBankProfileVO.getNumberofrefunds_year3());

        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()) && ESAPI.validator().isValidInput("salesvolume_lastmonth", bankProfileVO.getSalesvolume_lastmonth(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_lastmonth", bankProfileVO.getChargebackvolume_lastmonth(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_lastmonth(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_lastmonth()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_lastmonth())));
        }
        bankProfileVO.setChargebackratio_lastmonth(requestBankProfileVO.getChargebackratio_lastmonth());

        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago()) && functions.isValueNull(bankProfileVO.getSalesvolume_2monthsago()) && ESAPI.validator().isValidInput("salesvolume_2monthsago", bankProfileVO.getSalesvolume_2monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_2monthsago", bankProfileVO.getChargebackvolume_2monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_2monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_2monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_2monthsago())));
        }
        bankProfileVO.setChargebackratio_2monthsago(requestBankProfileVO.getChargebackratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()) && ESAPI.validator().isValidInput("salesvolume_3monthsago", bankProfileVO.getSalesvolume_3monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_3monthsago", bankProfileVO.getChargebackvolume_3monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_3monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_3monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_3monthsago())));
        }
        bankProfileVO.setChargebackratio_3monthsago(requestBankProfileVO.getChargebackratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()) && ESAPI.validator().isValidInput("salesvolume_4monthsago", bankProfileVO.getSalesvolume_4monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_4monthsago", bankProfileVO.getChargebackvolume_4monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_4monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_4monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_4monthsago()) ));
        }
        bankProfileVO.setChargebackratio_4monthsago(requestBankProfileVO.getChargebackratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()) && ESAPI.validator().isValidInput("salesvolume_5monthsago", bankProfileVO.getSalesvolume_5monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_5monthsago", bankProfileVO.getChargebackvolume_5monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_5monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_5monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_5monthsago())));
        }
        bankProfileVO.setChargebackratio_5monthsago(requestBankProfileVO.getChargebackratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()) && ESAPI.validator().isValidInput("salesvolume_6monthsago", bankProfileVO.getSalesvolume_6monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_6monthsago", bankProfileVO.getChargebackvolume_6monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_6monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_6monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_6monthsago())));
        }
        bankProfileVO.setChargebackratio_6monthsago(requestBankProfileVO.getChargebackratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_12monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_12monthsago()) && ESAPI.validator().isValidInput("salesvolume_12monthsago", bankProfileVO.getSalesvolume_12monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_12monthsago", bankProfileVO.getChargebackvolume_12monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_12monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_12monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_12monthsago())));
        }
        bankProfileVO.setChargebackratio_12monthsago(requestBankProfileVO.getChargebackratio_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year2()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_year2()) && ESAPI.validator().isValidInput("salesvolume_year2", bankProfileVO.getSalesvolume_year2(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_year2", bankProfileVO.getChargebackvolume_year2(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_year2(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_year2()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_year2())));
        }
        bankProfileVO.setChargebackratio_year2(requestBankProfileVO.getChargebackratio_year2());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year3()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_year3()) && ESAPI.validator().isValidInput("salesvolume_year3", bankProfileVO.getSalesvolume_year3(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("chargebackvolume_year3", bankProfileVO.getChargebackvolume_year3(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setChargebackratio_year3(String.valueOf((Integer.valueOf(bankProfileVO.getChargebackvolume_year3()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_year3())));
        }
        bankProfileVO.setChargebackratio_year3(requestBankProfileVO.getChargebackratio_year3());

        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth()) && ESAPI.validator().isValidInput("salesvolume_lastmonth", bankProfileVO.getSalesvolume_lastmonth(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_lastmonth", bankProfileVO.getRefundsvolume_lastmonth(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_lastmonth(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_lastmonth()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_lastmonth())));
        }
        bankProfileVO.setRefundratio_lastmonth(requestBankProfileVO.getRefundratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago()) && ESAPI.validator().isValidInput("salesvolume_2monthsago", bankProfileVO.getSalesvolume_2monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_2monthsago", bankProfileVO.getRefundsvolume_2monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_2monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_2monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_2monthsago()) ));
        }
        bankProfileVO.setRefundratio_2monthsago(requestBankProfileVO.getRefundratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago()) && ESAPI.validator().isValidInput("salesvolume_3monthsago", bankProfileVO.getSalesvolume_3monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_3monthsago", bankProfileVO.getRefundsvolume_3monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_3monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_3monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_3monthsago())));
        }
        bankProfileVO.setRefundratio_3monthsago(requestBankProfileVO.getRefundratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago()) && ESAPI.validator().isValidInput("salesvolume_4monthsago", bankProfileVO.getSalesvolume_4monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_4monthsago", bankProfileVO.getRefundsvolume_4monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_4monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_4monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_4monthsago())));
        }
        bankProfileVO.setRefundratio_4monthsago(requestBankProfileVO.getRefundratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago()) && ESAPI.validator().isValidInput("salesvolume_5monthsago", bankProfileVO.getSalesvolume_5monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_5monthsago", bankProfileVO.getRefundsvolume_5monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_5monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_5monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_5monthsago()) ));
        }
        bankProfileVO.setRefundratio_5monthsago(requestBankProfileVO.getRefundratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago()) && ESAPI.validator().isValidInput("salesvolume_6monthsago", bankProfileVO.getSalesvolume_6monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_6monthsago", bankProfileVO.getRefundsvolume_6monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_6monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_6monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_6monthsago()) ));
        }
        bankProfileVO.setRefundratio_6monthsago(requestBankProfileVO.getRefundratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_12monthsago()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_12monthsago()) && ESAPI.validator().isValidInput("salesvolume_12monthsago", bankProfileVO.getSalesvolume_12monthsago(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_12monthsago", bankProfileVO.getRefundsvolume_12monthsago(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_12monthsago(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_12monthsago()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_12monthsago())));
        }
        bankProfileVO.setRefundratio_12monthsago(requestBankProfileVO.getRefundratio_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_year2()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_year2()) && ESAPI.validator().isValidInput("salesvolume_year2", bankProfileVO.getSalesvolume_year2(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_year2", bankProfileVO.getRefundsvolume_year2(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_year2(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_year2()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_year2())));
        }
        bankProfileVO.setRefundratio_year2(requestBankProfileVO.getRefundratio_year2());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_year3()) && functions.isValueNull(requestBankProfileVO.getSalesvolume_year3()) && ESAPI.validator().isValidInput("salesvolume_year3", bankProfileVO.getSalesvolume_year3(), "OnlyNumber", 20, false) && ESAPI.validator().isValidInput("refundsvolume_year3", bankProfileVO.getRefundsvolume_year3(), "OnlyNumber", 20, false))
        {
            requestBankProfileVO.setRefundratio_year3(String.valueOf((Integer.valueOf(bankProfileVO.getRefundsvolume_year3()) * 100) / Integer.valueOf(bankProfileVO.getSalesvolume_year3())));
        }
        bankProfileVO.setRefundratio_year3(requestBankProfileVO.getRefundratio_year3());

        if(functions.isValueNull(requestBankProfileVO.getCurrency()))
            bankProfileVO.setCurrency(requestBankProfileVO.getCurrency());

        if(functions.isValueNull(requestBankProfileVO.getBank_account_currencies()))
            bankProfileVO.setBank_account_currencies(requestBankProfileVO.getBank_account_currencies());

       /* if (functions.isValueNull(requestBankProfileVO.getProduct_sold_currencies()))
            bankProfileVO.setProduct_sold_currencies(requestBankProfileVO.getProduct_sold_currencies());
       */
        /*if(functions.isValueNull(requestBankProfileVO.getBank_account_currencies()))
            bankProfileVO.setBank_account_currencies(requestBankProfileVO.getBank_account_currencies());*/

        /*if (functions.isValueNull(requestBankProfileVO.getProduct_sold_currencies()))
            bankProfileVO.setProduct_sold_currencies(requestBankProfileVO.getProduct_sold_currencies());*/
        /*if(functions.isValueNull(requestBankProfileVO.getCurrency_products_HKD()))
            bankProfileVO.setCurrency_products_HKD(requestBankProfileVO.getCurrency_products_HKD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_AUD()))
            bankProfileVO.setCurrency_products_AUD(requestBankProfileVO.getCurrency_products_AUD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_CAD()))
            bankProfileVO.setCurrency_products_CAD(requestBankProfileVO.getCurrency_products_CAD());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_DKK()))
            bankProfileVO.setCurrency_products_DKK(requestBankProfileVO.getCurrency_products_DKK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_SEK()))
            bankProfileVO.setCurrency_products_SEK(requestBankProfileVO.getCurrency_products_SEK());
        if(functions.isValueNull(requestBankProfileVO.getCurrency_products_NOK()))
            bankProfileVO.setCurrency_products_NOK(requestBankProfileVO.getCurrency_products_NOK());*/
        if(functions.isValueNull(requestBankProfileVO.getAquirer()))
            bankProfileVO.setAquirer(requestBankProfileVO.getAquirer());
        if(functions.isValueNull(requestBankProfileVO.getReasonaquirer()))
            bankProfileVO.setReasonaquirer(requestBankProfileVO.getReasonaquirer());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_contactperson()))
            bankProfileVO.setBankinfo_contactperson(requestBankProfileVO.getBankinfo_contactperson());
    }
    private void setBankProfileVO(BankProfileVO requestBankProfileVO,BankProfileVO bankProfileVO,ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_productssold())&& validationErrorList.getError("currencyrequested_productssold")==null)
            bankProfileVO.setCurrencyrequested_productssold(requestBankProfileVO.getCurrencyrequested_productssold());
        if(functions.isValueNull(requestBankProfileVO.getCurrencyrequested_bankaccount())&& validationErrorList.getError("currencyrequested_bankaccount")==null)
            bankProfileVO.setCurrencyrequested_bankaccount(requestBankProfileVO.getCurrencyrequested_bankaccount());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bic())&& validationErrorList.getError("bankinfo_bic")==null)
            bankProfileVO.setBankinfo_bic(requestBankProfileVO.getBankinfo_bic());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bank_name())&& validationErrorList.getError("bankinfo_bank_name")==null)
            bankProfileVO.setBankinfo_bank_name(requestBankProfileVO.getBankinfo_bank_name());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankaddress())&& validationErrorList.getError("bankinfo_bankaddress")==null)
            bankProfileVO.setBankinfo_bankaddress(requestBankProfileVO.getBankinfo_bankaddress());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_bankphonenumber())&& validationErrorList.getError("bankinfo_bankphonenumber")==null)
            bankProfileVO.setBankinfo_bankphonenumber(requestBankProfileVO.getBankinfo_bankphonenumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_aba_routingcode())&& validationErrorList.getError("bankinfo_aba_routingcode")==null)
            bankProfileVO.setBankinfo_aba_routingcode(requestBankProfileVO.getBankinfo_aba_routingcode());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountholder())&& validationErrorList.getError("bankinfo_accountholder")==null)
            bankProfileVO.setBankinfo_accountholder(requestBankProfileVO.getBankinfo_accountholder());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_IBAN())&& validationErrorList.getError("bankinfo_IBAN")==null)
            bankProfileVO.setBankinfo_IBAN(requestBankProfileVO.getBankinfo_IBAN());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_accountnumber())&& validationErrorList.getError("bankinfo_accountnumber")==null)
            bankProfileVO.setBankinfo_accountnumber(requestBankProfileVO.getBankinfo_accountnumber());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_contactperson())&& validationErrorList.getError("bankinfo_contactperson")==null)
            bankProfileVO.setBankinfo_contactperson(requestBankProfileVO.getBankinfo_contactperson());
        if(functions.isValueNull(requestBankProfileVO.getBankinfo_currency())&& validationErrorList.getError("bankinfo_currency")==null)
            bankProfileVO.setBankinfo_currency(requestBankProfileVO.getBankinfo_currency());

        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_lastmonth())&& validationErrorList.getError("salesvolume_lastmonth")==null)
            bankProfileVO.setSalesvolume_lastmonth(requestBankProfileVO.getSalesvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_2monthsago())&& validationErrorList.getError("salesvolume_2monthsago")==null)
            bankProfileVO.setSalesvolume_2monthsago(requestBankProfileVO.getSalesvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_3monthsago())&& validationErrorList.getError("salesvolume_3monthsago")==null)
            bankProfileVO.setSalesvolume_3monthsago(requestBankProfileVO.getSalesvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_4monthsago())&& validationErrorList.getError("salesvolume_4monthsago")==null)
            bankProfileVO.setSalesvolume_4monthsago(requestBankProfileVO.getSalesvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_5monthsago())&& validationErrorList.getError("salesvolume_5monthsago")==null)
            bankProfileVO.setSalesvolume_5monthsago(requestBankProfileVO.getSalesvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_6monthsago())&& validationErrorList.getError("salesvolume_6monthsago")==null)
            bankProfileVO.setSalesvolume_6monthsago(requestBankProfileVO.getSalesvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_12monthsago())&& validationErrorList.getError("salesvolume_12monthsago")==null)
            bankProfileVO.setSalesvolume_12monthsago(requestBankProfileVO.getSalesvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_year2())&& validationErrorList.getError("salesvolume_year2")==null)
            bankProfileVO.setSalesvolume_year2(requestBankProfileVO.getSalesvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getSalesvolume_year3())&& validationErrorList.getError("salesvolume_year3")==null)
            bankProfileVO.setSalesvolume_year3(requestBankProfileVO.getSalesvolume_year3());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_lastmonth())&& validationErrorList.getError("numberoftransactions_lastmonth")==null)
            bankProfileVO.setNumberoftransactions_lastmonth(requestBankProfileVO.getNumberoftransactions_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_2monthsago())&& validationErrorList.getError("numberoftransactions_2monthsago")==null)
            bankProfileVO.setNumberoftransactions_2monthsago(requestBankProfileVO.getNumberoftransactions_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_3monthsago())&& validationErrorList.getError("numberoftransactions_3monthsago")==null)
            bankProfileVO.setNumberoftransactions_3monthsago(requestBankProfileVO.getNumberoftransactions_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_4monthsago())&& validationErrorList.getError("numberoftransactions_4monthsago")==null)
            bankProfileVO.setNumberoftransactions_4monthsago(requestBankProfileVO.getNumberoftransactions_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_5monthsago())&& validationErrorList.getError("numberoftransactions_5monthsago")==null)
            bankProfileVO.setNumberoftransactions_5monthsago(requestBankProfileVO.getNumberoftransactions_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_6monthsago())&& validationErrorList.getError("numberoftransactions_6monthsago")==null)
            bankProfileVO.setNumberoftransactions_6monthsago(requestBankProfileVO.getNumberoftransactions_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_12monthsago())&& validationErrorList.getError("numberoftransactions_12monthsago")==null)
            bankProfileVO.setNumberoftransactions_12monthsago(requestBankProfileVO.getNumberoftransactions_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_year2())&& validationErrorList.getError("numberoftransactions_year2")==null)
            bankProfileVO.setNumberoftransactions_year2(requestBankProfileVO.getNumberoftransactions_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberoftransactions_year3())&& validationErrorList.getError("numberoftransactions_year3")==null)
            bankProfileVO.setNumberoftransactions_year3(requestBankProfileVO.getNumberoftransactions_year3());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_lastmonth())&& validationErrorList.getError("chargebackvolume_lastmonth")==null)
            bankProfileVO.setChargebackvolume_lastmonth(requestBankProfileVO.getChargebackvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_2monthsago())&& validationErrorList.getError("chargebackvolume_2monthsago")==null)
            bankProfileVO.setChargebackvolume_2monthsago(requestBankProfileVO.getChargebackvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_3monthsago())&& validationErrorList.getError("chargebackvolume_3monthsago")==null)
            bankProfileVO.setChargebackvolume_3monthsago(requestBankProfileVO.getChargebackvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_4monthsago())&& validationErrorList.getError("chargebackvolume_4monthsago")==null)
            bankProfileVO.setChargebackvolume_4monthsago(requestBankProfileVO.getChargebackvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_5monthsago())&& validationErrorList.getError("chargebackvolume_5monthsago")==null)
            bankProfileVO.setChargebackvolume_5monthsago(requestBankProfileVO.getChargebackvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_6monthsago())&& validationErrorList.getError("chargebackvolume_6monthsago")==null)
            bankProfileVO.setChargebackvolume_6monthsago(requestBankProfileVO.getChargebackvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_12monthsago())&& validationErrorList.getError("chargebackvolume_12monthsago")==null)
            bankProfileVO.setChargebackvolume_12monthsago(requestBankProfileVO.getChargebackvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year2())&& validationErrorList.getError("chargebackvolume_year2")==null)
            bankProfileVO.setChargebackvolume_year2(requestBankProfileVO.getChargebackvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getChargebackvolume_year3())&& validationErrorList.getError("chargebackvolume_year3")==null)
            bankProfileVO.setChargebackvolume_year3(requestBankProfileVO.getChargebackvolume_year3());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_lastmonth())&& validationErrorList.getError("numberofchargebacks_lastmonth")==null)
            bankProfileVO.setNumberofchargebacks_lastmonth(requestBankProfileVO.getNumberofchargebacks_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_2monthsago())&& validationErrorList.getError("numberofchargebacks_2monthsago")==null)
            bankProfileVO.setNumberofchargebacks_2monthsago(requestBankProfileVO.getNumberofchargebacks_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_3monthsago())&& validationErrorList.getError("numberofchargebacks_3monthsago")==null)
            bankProfileVO.setNumberofchargebacks_3monthsago(requestBankProfileVO.getNumberofchargebacks_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_4monthsago())&& validationErrorList.getError("numberofchargebacks_4monthsago")==null)
            bankProfileVO.setNumberofchargebacks_4monthsago(requestBankProfileVO.getNumberofchargebacks_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_5monthsago())&& validationErrorList.getError("numberofchargebacks_5monthsago")==null)
            bankProfileVO.setNumberofchargebacks_5monthsago(requestBankProfileVO.getNumberofchargebacks_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_6monthsago())&& validationErrorList.getError("numberofchargebacks_6monthsago")==null)
            bankProfileVO.setNumberofchargebacks_6monthsago(requestBankProfileVO.getNumberofchargebacks_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_12monthsago())&& validationErrorList.getError("numberofchargebacks_12monthsago")==null)
            bankProfileVO.setNumberofchargebacks_12monthsago(requestBankProfileVO.getNumberofchargebacks_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_year2())&& validationErrorList.getError("numberofchargebacks_year2")==null)
            bankProfileVO.setNumberofchargebacks_year2(requestBankProfileVO.getNumberofchargebacks_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberofchargebacks_year3())&& validationErrorList.getError("numberofchargebacks_year3")==null)
            bankProfileVO.setNumberofchargebacks_year3(requestBankProfileVO.getNumberofchargebacks_year3());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_lastmonth())&& validationErrorList.getError("refundsvolume_lastmonth")==null)
            bankProfileVO.setRefundsvolume_lastmonth(requestBankProfileVO.getRefundsvolume_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_2monthsago())&& validationErrorList.getError("refundsvolume_2monthsago")==null)
            bankProfileVO.setRefundsvolume_2monthsago(requestBankProfileVO.getRefundsvolume_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_3monthsago())&& validationErrorList.getError("refundsvolume_3monthsago")==null)
            bankProfileVO.setRefundsvolume_3monthsago(requestBankProfileVO.getRefundsvolume_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_4monthsago())&& validationErrorList.getError("refundsvolume_4monthsago")==null)
            bankProfileVO.setRefundsvolume_4monthsago(requestBankProfileVO.getRefundsvolume_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_5monthsago())&& validationErrorList.getError("refundsvolume_5monthsago")==null)
            bankProfileVO.setRefundsvolume_5monthsago(requestBankProfileVO.getRefundsvolume_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_6monthsago())&& validationErrorList.getError("refundsvolume_6monthsago")==null)
            bankProfileVO.setRefundsvolume_6monthsago(requestBankProfileVO.getRefundsvolume_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_12monthsago())&& validationErrorList.getError("refundsvolume_12monthsago")==null)
            bankProfileVO.setRefundsvolume_12monthsago(requestBankProfileVO.getRefundsvolume_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_year2())&& validationErrorList.getError("refundsvolume_year2")==null)
            bankProfileVO.setRefundsvolume_year2(requestBankProfileVO.getRefundsvolume_year2());
        if(functions.isValueNull(requestBankProfileVO.getRefundsvolume_year3())&& validationErrorList.getError("refundsvolume_year3")==null)
            bankProfileVO.setRefundsvolume_year3(requestBankProfileVO.getRefundsvolume_year3());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_lastmonth())&& validationErrorList.getError("numberofrefunds_lastmonth")==null)
            bankProfileVO.setNumberofrefunds_lastmonth(requestBankProfileVO.getNumberofrefunds_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_2monthsago())&& validationErrorList.getError("numberofrefunds_2monthsago")==null)
            bankProfileVO.setNumberofrefunds_2monthsago(requestBankProfileVO.getNumberofrefunds_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_3monthsago())&& validationErrorList.getError("numberofrefunds_3monthsago")==null)
            bankProfileVO.setNumberofrefunds_3monthsago(requestBankProfileVO.getNumberofrefunds_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_4monthsago())&& validationErrorList.getError("numberofrefunds_4monthsago")==null)
            bankProfileVO.setNumberofrefunds_4monthsago(requestBankProfileVO.getNumberofrefunds_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_5monthsago())&& validationErrorList.getError("numberofrefunds_5monthsago")==null)
            bankProfileVO.setNumberofrefunds_5monthsago(requestBankProfileVO.getNumberofrefunds_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_6monthsago())&& validationErrorList.getError("numberofrefunds_6monthsago")==null)
            bankProfileVO.setNumberofrefunds_6monthsago(requestBankProfileVO.getNumberofrefunds_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_12monthsago())&& validationErrorList.getError("numberofrefunds_12monthsago")==null)
            bankProfileVO.setNumberofrefunds_12monthsago(requestBankProfileVO.getNumberofrefunds_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_year2())&& validationErrorList.getError("numberofrefunds_year2")==null)
            bankProfileVO.setNumberofrefunds_year2(requestBankProfileVO.getNumberofrefunds_year2());
        if(functions.isValueNull(requestBankProfileVO.getNumberofrefunds_year3())&& validationErrorList.getError("numberofrefunds_year3")==null)
            bankProfileVO.setNumberofrefunds_year3(requestBankProfileVO.getNumberofrefunds_year3());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_lastmonth())&& validationErrorList.getError("chargebackratio_lastmonth")==null)
            bankProfileVO.setChargebackratio_lastmonth(requestBankProfileVO.getChargebackratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_2monthsago())&& validationErrorList.getError("chargebackratio_2monthsago")==null)
            bankProfileVO.setChargebackratio_2monthsago(requestBankProfileVO.getChargebackratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_3monthsago())&& validationErrorList.getError("chargebackratio_3monthsago")==null)
            bankProfileVO.setChargebackratio_3monthsago(requestBankProfileVO.getChargebackratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_4monthsago())&& validationErrorList.getError("chargebackratio_4monthsago")==null)
            bankProfileVO.setChargebackratio_4monthsago(requestBankProfileVO.getChargebackratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_5monthsago())&& validationErrorList.getError("chargebackratio_5monthsago")==null)
            bankProfileVO.setChargebackratio_5monthsago(requestBankProfileVO.getChargebackratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_6monthsago())&& validationErrorList.getError("chargebackratio_6monthsago")==null)
            bankProfileVO.setChargebackratio_6monthsago(requestBankProfileVO.getChargebackratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_12monthsago())&& validationErrorList.getError("chargebackratio_12monthsago")==null)
            bankProfileVO.setChargebackratio_12monthsago(requestBankProfileVO.getChargebackratio_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_year2())&& validationErrorList.getError("chargebackratio_year2")==null)
            bankProfileVO.setChargebackratio_year2(requestBankProfileVO.getChargebackratio_year2());
        if(functions.isValueNull(requestBankProfileVO.getChargebackratio_year3())&& validationErrorList.getError("chargebackratio_year3")==null)
            bankProfileVO.setChargebackratio_year3(requestBankProfileVO.getChargebackratio_year3());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_lastmonth())&& validationErrorList.getError("refundratio_lastmonth")==null)
            bankProfileVO.setRefundratio_lastmonth(requestBankProfileVO.getRefundratio_lastmonth());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_2monthsago())&& validationErrorList.getError("refundratio_2monthsago")==null)
            bankProfileVO.setRefundratio_2monthsago(requestBankProfileVO.getRefundratio_2monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_3monthsago())&& validationErrorList.getError("refundratio_3monthsago")==null)
            bankProfileVO.setRefundratio_3monthsago(requestBankProfileVO.getRefundratio_3monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_4monthsago())&& validationErrorList.getError("refundratio_4monthsago")==null)
            bankProfileVO.setRefundratio_4monthsago(requestBankProfileVO.getRefundratio_4monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_5monthsago())&& validationErrorList.getError("refundratio_5monthsago")==null)
            bankProfileVO.setRefundratio_5monthsago(requestBankProfileVO.getRefundratio_5monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_6monthsago())&& validationErrorList.getError("refundratio_6monthsago")==null)
            bankProfileVO.setRefundratio_6monthsago(requestBankProfileVO.getRefundratio_6monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_12monthsago())&& validationErrorList.getError("refundratio_12monthsago")==null)
            bankProfileVO.setRefundratio_12monthsago(requestBankProfileVO.getRefundratio_12monthsago());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_year2())&& validationErrorList.getError("refundratio_year2")==null)
            bankProfileVO.setRefundratio_year2(requestBankProfileVO.getRefundratio_year2());
        if(functions.isValueNull(requestBankProfileVO.getRefundratio_year3())&& validationErrorList.getError("refundratio_year3")==null)
            bankProfileVO.setRefundratio_year3(requestBankProfileVO.getRefundratio_year3());

        if(functions.isValueNull(requestBankProfileVO.getCurrency())&& validationErrorList.getError("currency")==null)
            bankProfileVO.setCurrency(requestBankProfileVO.getCurrency());
        if(functions.isValueNull(requestBankProfileVO.getBank_account_currencies())&& validationErrorList.getError("bank_account_currencies")==null)
            bankProfileVO.setBank_account_currencies(requestBankProfileVO.getBank_account_currencies());
        /*if(functions.isValueNull(requestBankProfileVO.getProduct_sold_currencies())&& validationErrorList.getError("product_sold_currencies")==null)
            bankProfileVO.setProduct_sold_currencies(requestBankProfileVO.getProduct_sold_currencies());*/
        if(functions.isValueNull(requestBankProfileVO.getAquirer())&& validationErrorList.getError("getAquirer")==null)
            bankProfileVO.setAquirer(requestBankProfileVO.getAquirer());
        if(functions.isValueNull(requestBankProfileVO.getReasonaquirer())&& validationErrorList.getError("reason_aquirer")==null)
            bankProfileVO.setReasonaquirer(requestBankProfileVO.getReasonaquirer());
        if(functions.isValueNull(requestBankProfileVO.getCustomer_trans_data())&& validationErrorList.getError("customer_trans_data")==null)
            bankProfileVO.setCustomer_trans_data(requestBankProfileVO.getCustomer_trans_data());
        /*if(functions.isValueNull(requestBankProfileVO.getBankcontactperson())&& validationErrorList.getError("bankcontactperson")==null)
            bankProfileVO.setBankcontactperson(requestBankProfileVO.getBankcontactperson());*/
    }

    private void setCardHolderProfileVO(HttpServletRequest request,CardholderProfileVO cardholderProfileVO)
    {
        //System.out.println("inside cardholder profile--- apprequest");
        cardholderProfileVO.setCompliance_swapp(request.getParameter("compliance_swapp"));
        cardholderProfileVO.setCompliance_thirdpartyappform(request.getParameter("compliance_thirdpartyappform"));
        cardholderProfileVO.setCompliance_thirdpartysoft(request.getParameter("compliance_thirdpartysoft"));
        cardholderProfileVO.setCompliance_version(request.getParameter("compliance_version"));
        cardholderProfileVO.setCompliance_companiesorgateways(request.getParameter("compliance_companiesorgateways"));
        cardholderProfileVO.setCompliance_companiesorgateways_yes(request.getParameter("compliance_companiesorgateways_yes"));
        cardholderProfileVO.setCompliance_electronically(request.getParameter("compliance_electronically"));
        cardholderProfileVO.setCompliance_carddatastored(request.getParameter("compliance_carddatastored"));
        cardholderProfileVO.setCompliance_cispcompliant(request.getParameter("compliance_cispcompliant"));
        cardholderProfileVO.setCompliance_cispcompliant_yes(request.getParameter("compliance_cispcompliant_yes"));
        cardholderProfileVO.setCompliance_pcidsscompliant(request.getParameter("compliance_pcidsscompliant"));
        cardholderProfileVO.setCompliance_pcidsscompliant_yes(request.getParameter("compliance_pcidsscompliant_yes"));
        cardholderProfileVO.setCompliance_qualifiedsecurityassessor(request.getParameter("compliance_qualifiedsecurityassessor"));
        cardholderProfileVO.setCompliance_dateofcompliance(request.getParameter("compliance_dateofcompliance"));
        cardholderProfileVO.setCompliance_dateoflastscan(request.getParameter("compliance_dateoflastscan"));
        cardholderProfileVO.setCompliance_datacompromise(request.getParameter("compliance_datacompromise"));
        cardholderProfileVO.setCompliance_datacompromise_yes(request.getParameter("compliance_datacompromise_yes"));
        cardholderProfileVO.setSiteinspection_merchant(request.getParameter("siteinspection_merchant"));
        cardholderProfileVO.setSiteinspection_landlord(request.getParameter("siteinspection_landlord"));
        cardholderProfileVO.setSiteinspection_buildingtype(request.getParameter("siteinspection_buildingtype"));
        cardholderProfileVO.setSiteinspection_areazoned(request.getParameter("siteinspection_areazoned"));
        cardholderProfileVO.setSiteinspection_squarefootage(request.getParameter("siteinspection_squarefootage"));
        cardholderProfileVO.setSiteinspection_operatebusiness(request.getParameter("siteinspection_operatebusiness"));
        cardholderProfileVO.setSiteinspection_principal1(request.getParameter("siteinspection_principal1"));
        cardholderProfileVO.setSiteinspection_principal1_date(request.getParameter("siteinspection_principal1_date"));
        cardholderProfileVO.setSiteinspection_principal2(request.getParameter("siteinspection_principal2"));
        cardholderProfileVO.setSiteinspection_principal2_date(request.getParameter("siteinspection_principal2_date"));
    }

    //set cardHolderProfile for API
    private void setCardHolderProfileVO(CardholderProfileVO requestCardholderProfileVO,CardholderProfileVO cardholderProfileVO)
    {
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_swapp()))
            cardholderProfileVO.setCompliance_swapp(requestCardholderProfileVO.getCompliance_swapp());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_thirdpartyappform()))
            cardholderProfileVO.setCompliance_thirdpartyappform(requestCardholderProfileVO.getCompliance_thirdpartyappform());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_thirdpartysoft()))
            cardholderProfileVO.setCompliance_thirdpartysoft(requestCardholderProfileVO.getCompliance_thirdpartysoft());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_version()))
            cardholderProfileVO.setCompliance_version(requestCardholderProfileVO.getCompliance_version());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_companiesorgateways()))
            cardholderProfileVO.setCompliance_companiesorgateways(requestCardholderProfileVO.getCompliance_companiesorgateways());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_companiesorgateways_yes()))
            cardholderProfileVO.setCompliance_companiesorgateways_yes(requestCardholderProfileVO.getCompliance_companiesorgateways_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_electronically()))
            cardholderProfileVO.setCompliance_electronically(requestCardholderProfileVO.getCompliance_electronically());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_carddatastored()))
            cardholderProfileVO.setCompliance_carddatastored(requestCardholderProfileVO.getCompliance_carddatastored());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_cispcompliant()))
            cardholderProfileVO.setCompliance_cispcompliant(requestCardholderProfileVO.getCompliance_cispcompliant());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_cispcompliant_yes()))
            cardholderProfileVO.setCompliance_cispcompliant_yes(requestCardholderProfileVO.getCompliance_cispcompliant_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_pcidsscompliant()))
            cardholderProfileVO.setCompliance_pcidsscompliant(requestCardholderProfileVO.getCompliance_pcidsscompliant());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_pcidsscompliant_yes()))
            cardholderProfileVO.setCompliance_pcidsscompliant_yes(requestCardholderProfileVO.getCompliance_pcidsscompliant_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_qualifiedsecurityassessor()))
            cardholderProfileVO.setCompliance_qualifiedsecurityassessor(requestCardholderProfileVO.getCompliance_qualifiedsecurityassessor());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_dateofcompliance()))
            cardholderProfileVO.setCompliance_dateofcompliance(requestCardholderProfileVO.getCompliance_dateofcompliance());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_dateoflastscan()))
            cardholderProfileVO.setCompliance_dateoflastscan(requestCardholderProfileVO.getCompliance_dateoflastscan());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_datacompromise()))
            cardholderProfileVO.setCompliance_datacompromise(requestCardholderProfileVO.getCompliance_datacompromise());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_datacompromise_yes()))
            cardholderProfileVO.setCompliance_datacompromise_yes(requestCardholderProfileVO.getCompliance_datacompromise_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_merchant()))
            cardholderProfileVO.setSiteinspection_merchant(requestCardholderProfileVO.getSiteinspection_merchant());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_landlord()))
            cardholderProfileVO.setSiteinspection_landlord(requestCardholderProfileVO.getSiteinspection_landlord());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_buildingtype()))
            cardholderProfileVO.setSiteinspection_buildingtype(requestCardholderProfileVO.getSiteinspection_buildingtype());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_areazoned()))
            cardholderProfileVO.setSiteinspection_areazoned(requestCardholderProfileVO.getSiteinspection_areazoned());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_squarefootage()))
            cardholderProfileVO.setSiteinspection_squarefootage(requestCardholderProfileVO.getSiteinspection_squarefootage());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_operatebusiness()))
            cardholderProfileVO.setSiteinspection_operatebusiness(requestCardholderProfileVO.getSiteinspection_operatebusiness());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal1()))
            cardholderProfileVO.setSiteinspection_principal1(requestCardholderProfileVO.getSiteinspection_principal1());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal1_date()))
            cardholderProfileVO.setSiteinspection_principal1_date(requestCardholderProfileVO.getSiteinspection_principal1_date());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal2()))
            cardholderProfileVO.setSiteinspection_principal2(requestCardholderProfileVO.getSiteinspection_principal2());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal2_date()))
            cardholderProfileVO.setSiteinspection_principal2_date(requestCardholderProfileVO.getSiteinspection_principal2_date());
    }


    private void setCardHolderProfileVO(CardholderProfileVO requestCardholderProfileVO,CardholderProfileVO cardholderProfileVO, ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_swapp())&& validationErrorList.getError("compliance_swapp")==null)
            cardholderProfileVO.setCompliance_swapp(requestCardholderProfileVO.getCompliance_swapp());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_thirdpartyappform())&& validationErrorList.getError("compliance_thirdpartyappform")==null)
            cardholderProfileVO.setCompliance_thirdpartyappform(requestCardholderProfileVO.getCompliance_thirdpartyappform());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_thirdpartysoft())&& validationErrorList.getError("compliance_thirdpartysoft")==null)
            cardholderProfileVO.setCompliance_thirdpartysoft(requestCardholderProfileVO.getCompliance_thirdpartysoft());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_version())&& validationErrorList.getError("compliance_version")==null)
            cardholderProfileVO.setCompliance_version(requestCardholderProfileVO.getCompliance_version());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_companiesorgateways())&& validationErrorList.getError("compliance_companiesorgateways")==null)
            cardholderProfileVO.setCompliance_companiesorgateways(requestCardholderProfileVO.getCompliance_companiesorgateways());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_companiesorgateways_yes())&& validationErrorList.getError("compliance_companiesorgateways_yes")==null)
            cardholderProfileVO.setCompliance_companiesorgateways_yes(requestCardholderProfileVO.getCompliance_companiesorgateways_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_electronically())&& validationErrorList.getError("compliance_electronically")==null)
            cardholderProfileVO.setCompliance_electronically(requestCardholderProfileVO.getCompliance_electronically());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_carddatastored())&& validationErrorList.getError("compliance_carddatastored")==null)
            cardholderProfileVO.setCompliance_carddatastored(requestCardholderProfileVO.getCompliance_carddatastored());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_cispcompliant())&& validationErrorList.getError("compliance_cispcompliant")==null)
            cardholderProfileVO.setCompliance_cispcompliant(requestCardholderProfileVO.getCompliance_cispcompliant());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_cispcompliant_yes()) && validationErrorList.getError("compliance_cispcompliant_yes")==null)
            cardholderProfileVO.setCompliance_cispcompliant_yes(requestCardholderProfileVO.getCompliance_cispcompliant_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_pcidsscompliant())&& validationErrorList.getError("compliance_pcidsscompliant")==null)
            cardholderProfileVO.setCompliance_pcidsscompliant(requestCardholderProfileVO.getCompliance_pcidsscompliant());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_pcidsscompliant_yes()) && validationErrorList.getError("compliance_pcidsscompliant_yes")==null)
            cardholderProfileVO.setCompliance_pcidsscompliant_yes(requestCardholderProfileVO.getCompliance_pcidsscompliant_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_qualifiedsecurityassessor())&& validationErrorList.getError("compliance_qualifiedsecurityassessor")==null)
            cardholderProfileVO.setCompliance_qualifiedsecurityassessor(requestCardholderProfileVO.getCompliance_qualifiedsecurityassessor());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_dateofcompliance())&& validationErrorList.getError("compliance_dateofcompliance")==null)
            cardholderProfileVO.setCompliance_dateofcompliance(requestCardholderProfileVO.getCompliance_dateofcompliance());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_dateoflastscan())&& validationErrorList.getError("compliance_dateoflastscan")==null)
            cardholderProfileVO.setCompliance_dateoflastscan(requestCardholderProfileVO.getCompliance_dateoflastscan());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_datacompromise())&& validationErrorList.getError("compliance_datacompromise")==null)
            cardholderProfileVO.setCompliance_datacompromise(requestCardholderProfileVO.getCompliance_datacompromise());
        if(functions.isValueNull(requestCardholderProfileVO.getCompliance_datacompromise_yes())&& validationErrorList.getError("compliance_datacompromise_yes")==null)
            cardholderProfileVO.setCompliance_datacompromise_yes(requestCardholderProfileVO.getCompliance_datacompromise_yes());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_merchant())&& validationErrorList.getError("siteinspection_merchant")==null)
            cardholderProfileVO.setSiteinspection_merchant(requestCardholderProfileVO.getSiteinspection_merchant());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_landlord())&& validationErrorList.getError("siteinspection_landlord")==null)
            cardholderProfileVO.setSiteinspection_landlord(requestCardholderProfileVO.getSiteinspection_landlord());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_buildingtype())&& validationErrorList.getError("siteinspection_buildingtype")==null)
            cardholderProfileVO.setSiteinspection_buildingtype(requestCardholderProfileVO.getSiteinspection_buildingtype());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_areazoned())&& validationErrorList.getError("siteinspection_areazoned")==null)
            cardholderProfileVO.setSiteinspection_areazoned(requestCardholderProfileVO.getSiteinspection_areazoned());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_squarefootage())&& validationErrorList.getError("siteinspection_squarefootage")==null)
            cardholderProfileVO.setSiteinspection_squarefootage(requestCardholderProfileVO.getSiteinspection_squarefootage());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_operatebusiness())&& validationErrorList.getError("siteinspection_operatebusiness")==null)
            cardholderProfileVO.setSiteinspection_operatebusiness(requestCardholderProfileVO.getSiteinspection_operatebusiness());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal1())&& validationErrorList.getError("siteinspection_principal1")==null)
            cardholderProfileVO.setSiteinspection_principal1(requestCardholderProfileVO.getSiteinspection_principal1());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal1_date())&& validationErrorList.getError("siteinspection_principal1_date")==null)
            cardholderProfileVO.setSiteinspection_principal1_date(requestCardholderProfileVO.getSiteinspection_principal1_date());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal2())&& validationErrorList.getError("siteinspection_principal2")==null)
            cardholderProfileVO.setSiteinspection_principal2(requestCardholderProfileVO.getSiteinspection_principal2());
        if(functions.isValueNull(requestCardholderProfileVO.getSiteinspection_principal2_date())&& validationErrorList.getError("siteinspection_principal2_date")==null)
            cardholderProfileVO.setSiteinspection_principal2_date(requestCardholderProfileVO.getSiteinspection_principal2_date());
    }


    //step 6 only Admin extra details profile
    private void setExtraDetailsProfileVO(HttpServletRequest request,ExtraDetailsProfileVO extraDetailsProfileVO)
    {
        extraDetailsProfileVO.setCompany_financialReport(request.getParameter("company_financialreport"));
        //System.out.println("extra detail value----->"+request.getParameter("company_financialreport"));
        extraDetailsProfileVO.setCompany_financialReportYes(request.getParameter("company_financialreportyes"));
        extraDetailsProfileVO.setFinancialReport_institution(request.getParameter("financialreport_institution"));
        extraDetailsProfileVO.setFinancialReport_available(request.getParameter("financialreport_available"));
        extraDetailsProfileVO.setFinancialReport_availableYes(request.getParameter("financialreport_availableyes"));
        extraDetailsProfileVO.setOwnerSince(request.getParameter("ownersince"));
        extraDetailsProfileVO.setSocialSecurity(request.getParameter("socialsecurity"));
        extraDetailsProfileVO.setCompany_formParticipation(request.getParameter("company_formparticipation"));
        extraDetailsProfileVO.setFinancialObligation(request.getParameter("financialobligation"));
        extraDetailsProfileVO.setCompliance_punitiveSanction(request.getParameter("compliance_punitivesanction"));
        extraDetailsProfileVO.setCompliance_punitiveSanctionYes(request.getParameter("compliance_punitivesanctionyes"));
        extraDetailsProfileVO.setWorkingExperience(request.getParameter("workingexperience"));
        extraDetailsProfileVO.setGoodsInsuranceOffered(request.getParameter("goodsinsuranceoffered"));
        extraDetailsProfileVO.setFulfillment_productEmail(request.getParameter("fulfillment_productemail"));
        extraDetailsProfileVO.setFulfillment_productEmailYes(request.getParameter("fulfillment_productemailyes"));
        extraDetailsProfileVO.setBlacklistedAccountClosed(request.getParameter("blacklistedaccountclosed"));
        extraDetailsProfileVO.setBlacklistedAccountClosedYes(request.getParameter("blacklistedaccountclosedyes"));
        extraDetailsProfileVO.setShiping_deliveryMethod(request.getParameter("shiping_deliverymethod"));
        extraDetailsProfileVO.setTransactionMonitoringProcess(request.getParameter("transactionmonitoringprocess"));
        extraDetailsProfileVO.setOperationalLicense(request.getParameter("operationallicense"));
        extraDetailsProfileVO.setSupervisorregularcontrole(request.getParameter("supervisorregularcontrole"));
        extraDetailsProfileVO.setDeedOfAgreement(request.getParameter("deedofagreement"));
        extraDetailsProfileVO.setDeedOfAgreementYes(request.getParameter("deedofagreementyes"));
    }
    //convert request to VOs object value for Speed option(Step 1)
    private void setSpeedProcessRequest(HttpServletRequest request,ApplicationManagerVO applicationManagerVO)
    {
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCompany_name(request.getParameter("merchantname"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCompany_name(request.getParameter("corporatename"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setName(request.getParameter("contactname"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setRegistration_number(request.getParameter("companyregistrationnumber"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setDate_of_registration(request.getParameter("Company_Date_Registration"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddress(request.getParameter("locationaddress"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setVatidentification(request.getParameter("vatidentification"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setZipcode(request.getParameter("merchantzipcode"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(request.getParameter("merchantcountry"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setStreet(request.getParameter("merchantstreet"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressProof(request.getParameter("merchant_addressproof"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressId(request.getParameter("merchant_addressId"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(request.getParameter("Companyphonecc1"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_number(request.getParameter("CompanyTelephoneNO"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setEmail_id(request.getParameter("CompanyEmailAddress"));
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFederalTaxId(request.getParameter("FederalTaxID"));
        applicationManagerVO.getBusinessProfileVO().setUrls(request.getParameter("urls"));
        applicationManagerVO.getCompanyProfileVO().setCompanyTypeOfBusiness(request.getParameter("company_typeofbusiness"));

        applicationManagerVO.getBankProfileVO().setBankinfo_bank_name(request.getParameter("bankinfo_bank_name"));
        //applicationManagerVO.getBankProfileVO().setBankinfo_IBAN_number(request.getParameter("bankinfo_IBAN_number"));
        applicationManagerVO.getBankProfileVO().setBankinfo_IBAN(request.getParameter("bankinfo_IBAN"));
        applicationManagerVO.getBankProfileVO().setBankinfo_accountnumber(request.getParameter("bankinfo_accountnumber"));
        applicationManagerVO.getBankProfileVO().setBankinfo_accountholder(request.getParameter("bankinfo_accountholder"));
        applicationManagerVO.getBankProfileVO().setBankinfo_bic(request.getParameter("bankinfo_bic"));

        applicationManagerVO.getBusinessProfileVO().setProduct_sold_currencies(request.getParameter("product_sold_currencies"));
        //applicationManagerVO.getBankProfileVO().setProduct_sold_currencies(request.getParameter("product_sold_currencies"));

        /*applicationManagerVO.getBankProfileVO().setCurrency_products_INR(request.getParameter("currency_products_INR"));

        applicationManagerVO.getBankProfileVO().setCurrency_products_USD(request.getParameter("currency_products_USD"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_EUR(request.getParameter("currency_products_EUR"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_GBP(request.getParameter("currency_products_GBP"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_JPY(request.getParameter("currency_products_JPY"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_PEN(request.getParameter("currency_products_PEN"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_HKD(request.getParameter("currency_products_HKD"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_AUD(request.getParameter("currency_products_AUD"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_CAD(request.getParameter("currency_products_CAD"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_DKK(request.getParameter("currency_products_DKK"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_SEK(request.getParameter("currency_products_SEK"));
        applicationManagerVO.getBankProfileVO().setCurrency_products_NOK(request.getParameter("currency_products_NOK"));*/
    }

    //sett speed Process Request For API
    private void setSpeedProcessRequest(ApplicationManagerVO requestApplicationManagerVO,ApplicationManagerVO applicationManagerVO)
    {
        //logger.debug("CompanyProfile MuerchantName:::"+requestApplicationManagerVO.getCompanyProfileVO().getMerchantName());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCompany_name(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).setCompany_name(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.BUSINESS).getCompany_name());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).setName(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_contactInfoVOMap().get(ApplicationManagerTypes.MAIN).getName());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setRegistration_number(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getRegistration_number());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setDate_of_registration(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getDate_of_registration());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddress(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddress());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressProof(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressProof());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setAddressId(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getAddressId());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setVatidentification(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getVatidentification());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setZipcode(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getZipcode());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry());
        //System.out.println("country----" + requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry());

        if(functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry()))
        {
            applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry());
            applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setCountry(commonFunctionUtil.getCountryDetailsForAPI(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry()));
            if(functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry()) && !functions.isValueNull(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc()))
                applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCountry().split("\\|")[1]);
        }
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setStreet(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getStreet());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_cc(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_cc());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setPhone_number(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getPhone_number());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setEmail_id(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getEmail_id());

        applicationManagerVO.getBusinessProfileVO().setUrls(requestApplicationManagerVO.getBusinessProfileVO().getUrls());
        applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).setFederalTaxId(requestApplicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getFederalTaxId());
        applicationManagerVO.getCompanyProfileVO().setCompanyTypeOfBusiness(requestApplicationManagerVO.getCompanyProfileVO().getCompanyTypeOfBusiness());

        applicationManagerVO.getBankProfileVO().setBankinfo_bank_name(requestApplicationManagerVO.getBankProfileVO().getBankinfo_bank_name());
        //applicationManagerVO.getBankProfileVO().setBankinfo_IBAN_number(requestApplicationManagerVO.getBankProfileVO().getBankinfo_IBAN_number());
        applicationManagerVO.getBankProfileVO().setBankinfo_IBAN(requestApplicationManagerVO.getBankProfileVO().getBankinfo_IBAN());
        applicationManagerVO.getBankProfileVO().setBankinfo_accountnumber(requestApplicationManagerVO.getBankProfileVO().getBankinfo_accountnumber());
        applicationManagerVO.getBankProfileVO().setBankinfo_accountholder(requestApplicationManagerVO.getBankProfileVO().getBankinfo_accountholder());
        applicationManagerVO.getBankProfileVO().setBankinfo_currency(requestApplicationManagerVO.getBankProfileVO().getBankinfo_currency());
        applicationManagerVO.getBankProfileVO().setBankinfo_bic(requestApplicationManagerVO.getBankProfileVO().getBankinfo_bic());


        applicationManagerVO.getBankProfileVO().setCurrency_products_AUD(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_AUD());
        applicationManagerVO.getBankProfileVO().setCurrency_products_USD(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_USD());
        applicationManagerVO.getBankProfileVO().setCurrency_products_GBP(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_GBP());
        applicationManagerVO.getBankProfileVO().setCurrency_products_EUR(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_EUR());
        applicationManagerVO.getBankProfileVO().setCurrency_products_JPY(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_JPY());
        applicationManagerVO.getBankProfileVO().setCurrency_products_PEN(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_PEN());
        applicationManagerVO.getBankProfileVO().setCurrency_products_HKD(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_HKD());
        applicationManagerVO.getBankProfileVO().setCurrency_products_CAD(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_CAD());
        applicationManagerVO.getBankProfileVO().setCurrency_products_DKK(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_DKK());
        applicationManagerVO.getBankProfileVO().setCurrency_products_SEK(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_SEK());
        applicationManagerVO.getBankProfileVO().setCurrency_products_NOK(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_NOK());
        applicationManagerVO.getBankProfileVO().setCurrency_products_INR(requestApplicationManagerVO.getBankProfileVO().getCurrency_products_INR());
       // System.out.println("prouct sold---"+requestApplicationManagerVO.getBankProfileVO().getProduct_sold_currencies());

       // applicationManagerVO.getBusinessProfileVO().set(requestApplicationManagerVO.getBusinessProfileVO().getProduct_sold_currencies());
        //System.out.println("prouct sold---"+requestApplicationManagerVO.getBusinessProfileVO().getProduct_sold_currencies());
    }

    //step 6 only Admin extra details profile
    private void setExtraDetailsProfileVO(ExtraDetailsProfileVO requestExtraDetailsProfileVO,ExtraDetailsProfileVO extraDetailsProfileVO)
    {
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_financialReport()))
            extraDetailsProfileVO.setCompany_financialReport(requestExtraDetailsProfileVO.getCompany_financialReport());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_financialReportYes()))
            extraDetailsProfileVO.setCompany_financialReportYes(requestExtraDetailsProfileVO.getCompany_financialReportYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_institution()))
            extraDetailsProfileVO.setFinancialReport_institution(requestExtraDetailsProfileVO.getFinancialReport_institution());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_available()))
            extraDetailsProfileVO.setFinancialReport_available(requestExtraDetailsProfileVO.getFinancialReport_available());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_availableYes()))
            extraDetailsProfileVO.setFinancialReport_availableYes(requestExtraDetailsProfileVO.getFinancialReport_availableYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getOwnerSince()))
            extraDetailsProfileVO.setOwnerSince(requestExtraDetailsProfileVO.getOwnerSince());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getSocialSecurity()))
            extraDetailsProfileVO.setSocialSecurity(requestExtraDetailsProfileVO.getSocialSecurity());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_formParticipation()))
            extraDetailsProfileVO.setCompany_formParticipation(requestExtraDetailsProfileVO.getCompany_formParticipation());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialObligation()))
            extraDetailsProfileVO.setFinancialObligation(requestExtraDetailsProfileVO.getFinancialObligation());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompliance_punitiveSanction()))
            extraDetailsProfileVO.setCompliance_punitiveSanction(requestExtraDetailsProfileVO.getCompliance_punitiveSanction());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompliance_punitiveSanctionYes()))
            extraDetailsProfileVO.setCompliance_punitiveSanctionYes(requestExtraDetailsProfileVO.getCompliance_punitiveSanctionYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getWorkingExperience()))
            extraDetailsProfileVO.setWorkingExperience(requestExtraDetailsProfileVO.getWorkingExperience());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getGoodsInsuranceOffered()))
            extraDetailsProfileVO.setGoodsInsuranceOffered(requestExtraDetailsProfileVO.getGoodsInsuranceOffered());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFulfillment_productEmail()))
            extraDetailsProfileVO.setFulfillment_productEmail(requestExtraDetailsProfileVO.getFulfillment_productEmail());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFulfillment_productEmailYes()))
            extraDetailsProfileVO.setFulfillment_productEmailYes(requestExtraDetailsProfileVO.getFulfillment_productEmailYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getBlacklistedAccountClosed()))
            extraDetailsProfileVO.setBlacklistedAccountClosed(requestExtraDetailsProfileVO.getBlacklistedAccountClosed());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getBlacklistedAccountClosedYes()))
            extraDetailsProfileVO.setBlacklistedAccountClosedYes(requestExtraDetailsProfileVO.getBlacklistedAccountClosedYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getShiping_deliveryMethod()))
            extraDetailsProfileVO.setShiping_deliveryMethod(requestExtraDetailsProfileVO.getShiping_deliveryMethod());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getTransactionMonitoringProcess()))
            extraDetailsProfileVO.setTransactionMonitoringProcess(requestExtraDetailsProfileVO.getTransactionMonitoringProcess());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getOperationalLicense()))
            extraDetailsProfileVO.setOperationalLicense(requestExtraDetailsProfileVO.getOperationalLicense());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getSupervisorregularcontrole()))
            extraDetailsProfileVO.setSupervisorregularcontrole(requestExtraDetailsProfileVO.getSupervisorregularcontrole());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getDeedOfAgreement()))
            extraDetailsProfileVO.setDeedOfAgreement(requestExtraDetailsProfileVO.getDeedOfAgreement());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getDeedOfAgreementYes()))
            extraDetailsProfileVO.setDeedOfAgreementYes(requestExtraDetailsProfileVO.getDeedOfAgreementYes());
    }




    private void setExtraDetailsProfileVO(ExtraDetailsProfileVO requestExtraDetailsProfileVO,ExtraDetailsProfileVO extraDetailsProfileVO,ValidationErrorList validationErrorList)
    {
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_financialReport())&& validationErrorList.getError("company_financialreport")==null)
            extraDetailsProfileVO.setCompany_financialReport(requestExtraDetailsProfileVO.getCompany_financialReport());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_financialReportYes())&& validationErrorList.getError("company_financialreportyes")==null)
            extraDetailsProfileVO.setCompany_financialReportYes(requestExtraDetailsProfileVO.getCompany_financialReportYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_institution())&& validationErrorList.getError("financialreport_institution")==null)
            extraDetailsProfileVO.setFinancialReport_institution(requestExtraDetailsProfileVO.getFinancialReport_institution());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_available())&& validationErrorList.getError("financialreport_available")==null)
            extraDetailsProfileVO.setFinancialReport_available(requestExtraDetailsProfileVO.getFinancialReport_available());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialReport_availableYes())&& validationErrorList.getError("financialreport_availableyes")==null)
            extraDetailsProfileVO.setFinancialReport_availableYes(requestExtraDetailsProfileVO.getFinancialReport_availableYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getOwnerSince())&& validationErrorList.getError("ownersince")==null)
            extraDetailsProfileVO.setOwnerSince(requestExtraDetailsProfileVO.getOwnerSince());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getSocialSecurity())&& validationErrorList.getError("socialsecurity")==null)
            extraDetailsProfileVO.setSocialSecurity(requestExtraDetailsProfileVO.getSocialSecurity());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompany_formParticipation())&& validationErrorList.getError("company_formparticipation")==null)
            extraDetailsProfileVO.setCompany_formParticipation(requestExtraDetailsProfileVO.getCompany_formParticipation());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFinancialObligation())&& validationErrorList.getError("financialobligation")==null)
            extraDetailsProfileVO.setFinancialObligation(requestExtraDetailsProfileVO.getFinancialObligation());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompliance_punitiveSanction())&& validationErrorList.getError("compliance_punitivesanction")==null)
            extraDetailsProfileVO.setCompliance_punitiveSanction(requestExtraDetailsProfileVO.getCompliance_punitiveSanction());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getCompliance_punitiveSanctionYes())&& validationErrorList.getError("compliance_punitivesanctionyes")==null)
            extraDetailsProfileVO.setCompliance_punitiveSanctionYes(requestExtraDetailsProfileVO.getCompliance_punitiveSanctionYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getWorkingExperience())&& validationErrorList.getError("workingexperience")==null)
            extraDetailsProfileVO.setWorkingExperience(requestExtraDetailsProfileVO.getWorkingExperience());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getGoodsInsuranceOffered())&& validationErrorList.getError("goodsinsuranceoffered")==null)
            extraDetailsProfileVO.setGoodsInsuranceOffered(requestExtraDetailsProfileVO.getGoodsInsuranceOffered());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFulfillment_productEmail())&& validationErrorList.getError("fulfillment_productemail")==null)
            extraDetailsProfileVO.setFulfillment_productEmail(requestExtraDetailsProfileVO.getFulfillment_productEmail());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getFulfillment_productEmailYes())&& validationErrorList.getError("fulfillment_productemailyes")==null)
            extraDetailsProfileVO.setFulfillment_productEmailYes(requestExtraDetailsProfileVO.getFulfillment_productEmailYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getBlacklistedAccountClosed())&& validationErrorList.getError("blacklistedaccountclosed")==null)
            extraDetailsProfileVO.setBlacklistedAccountClosed(requestExtraDetailsProfileVO.getBlacklistedAccountClosed());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getBlacklistedAccountClosedYes())&& validationErrorList.getError("blacklistedaccountclosedyes")==null)
            extraDetailsProfileVO.setBlacklistedAccountClosedYes(requestExtraDetailsProfileVO.getBlacklistedAccountClosedYes());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getShiping_deliveryMethod())&& validationErrorList.getError("shiping_deliverymethod")==null)
            extraDetailsProfileVO.setShiping_deliveryMethod(requestExtraDetailsProfileVO.getShiping_deliveryMethod());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getTransactionMonitoringProcess())&& validationErrorList.getError("transactionmonitoringprocess")==null)
            extraDetailsProfileVO.setTransactionMonitoringProcess(requestExtraDetailsProfileVO.getTransactionMonitoringProcess());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getOperationalLicense())&& validationErrorList.getError("operationallicense")==null)
            extraDetailsProfileVO.setOperationalLicense(requestExtraDetailsProfileVO.getOperationalLicense());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getSupervisorregularcontrole())&& validationErrorList.getError("supervisorregularcontrole")==null)
            extraDetailsProfileVO.setSupervisorregularcontrole(requestExtraDetailsProfileVO.getSupervisorregularcontrole());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getDeedOfAgreement())&& validationErrorList.getError("deedofagreement")==null)
            extraDetailsProfileVO.setDeedOfAgreement(requestExtraDetailsProfileVO.getDeedOfAgreement());
        if(functions.isValueNull(requestExtraDetailsProfileVO.getDeedOfAgreementYes())&& validationErrorList.getError("deedofagreementyes")==null)
            extraDetailsProfileVO.setDeedOfAgreementYes(requestExtraDetailsProfileVO.getDeedOfAgreementYes());
    }
////

    //changes for multiple KYC
    private void validateAppFilesUploaded(ApplicationManagerVO applicationManagerVO,ValidationErrorList validationErrorList,boolean isOptional,String pageNumber,boolean isAPI)
    {

        //ToOD Put logger error to priint isoptional and is API flag
        if(isOptional)
        {

            for(FileDetailsListVO fileDetailsVO1:applicationManagerVO.getSubmittedFileDetailsVO().values())
            {
                for (int i = 0; i < fileDetailsVO1.getFiledetailsvo().size(); i++)
                {
                    AppFileDetailsVO fileDetailsVO = fileDetailsVO1.getFiledetailsvo().get(i);

                    logger.debug("getSubmittedFileDetailsVO values......." + fileDetailsVO.getFieldName());
                    if (!fileDetailsVO.isSuccess())
                    {
                        logger.debug("inside if Field Name !fileDetailsVO.isSuccess....." + fileDetailsVO.getFieldName());
                        validationErrorList.addError(fileDetailsVO.getFieldName(), new ValidationException(fileDetailsVO.getFieldName(), fileDetailsVO.getReasonOfFailure(), pageNumber));
                    }
                    else
                    {
                        FileDetailsListVO existingFileDetailsListVO = applicationManagerVO.getFileDetailsVOs().get(fileDetailsVO.getFieldName());

                        if(existingFileDetailsListVO ==null)
                        {
                            existingFileDetailsListVO = new FileDetailsListVO();
                        }

                        List<AppFileDetailsVO> existingFilesVO = existingFileDetailsListVO.getFiledetailsvo();

                        if (AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                        {                        //alternateName  applicationVO
                            //Added for Multiple KYC
                            logger.debug("Action Type uploAD...."+fileDetailsVO.getFileActionType());
                            if(existingFilesVO!=null)
                            {
                                existingFilesVO.add(fileDetailsVO);
                            }
                            else
                            {
                                existingFilesVO = new ArrayList<AppFileDetailsVO>();
                                existingFilesVO.add(fileDetailsVO);
                            }
                            existingFileDetailsListVO.setFiledetailsvo(existingFilesVO);
                            applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(),existingFileDetailsListVO);
                        }
                        else if (AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                        {
                            logger.debug("Action Type rEPLACE...."+fileDetailsVO.getFileActionType());
                            AppFileDetailsVO removalFilrDetailVO = null;
                            for (AppFileDetailsVO existingFileDetailsVO : existingFilesVO)
                            {
                                if(existingFileDetailsVO.getMappingId().equals(fileDetailsVO.getMappingId()))
                                {
                                    removalFilrDetailVO = existingFileDetailsVO;
                                }
                            }
                            existingFilesVO.remove(removalFilrDetailVO);
                            existingFilesVO.add(fileDetailsVO);
                            existingFileDetailsListVO.setFiledetailsvo(existingFilesVO);
                            applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(),existingFileDetailsListVO);

                        }

                    }
                }
                //applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }

        }
        else{
            logger.debug(""+isAPI);
            if(isAPI)
            {
                if(applicationManagerVO.getSubmittedFileDetailsVO()!=null)
                {
                    logger.debug("inside if isAPI....."+applicationManagerVO.getSubmittedFileDetailsVO().values());
                    for (FileDetailsListVO  FileDetailsVOList: applicationManagerVO.getSubmittedFileDetailsVO().values())
                    {
                        for(int i=0;i<FileDetailsVOList.getFiledetailsvo().size();i++)
                        {
                            AppFileDetailsVO fileDetailsVO = FileDetailsVOList.getFiledetailsvo().get(i);
                            if (!fileDetailsVO.isSuccess())
                            {
                                logger.debug("inside If Field Name....." + fileDetailsVO.getFieldName());
                                validationErrorList.addError(fileDetailsVO.getFieldName(), new ValidationException(fileDetailsVO.getFieldName(), fileDetailsVO.getReasonOfFailure(), pageNumber));
                            }
                            else
                            {
                                logger.debug("inside else Field Name....." + fileDetailsVO.getFieldName());
                                //applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(), fileDetailsVO);
                                FileDetailsListVO existingFileDetailsListVO = applicationManagerVO.getFileDetailsVOs().get(fileDetailsVO.getFieldName());

                                if(existingFileDetailsListVO ==null)
                                {
                                    existingFileDetailsListVO = new FileDetailsListVO();
                                }

                                List<AppFileDetailsVO> existingFilesVO = existingFileDetailsListVO.getFiledetailsvo();


                                if (AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                                {
                                    //Added for Multiple KYC

                                    logger.debug("Action Type uploAD...."+fileDetailsVO.getFileActionType());
                                    if(existingFilesVO!=null)
                                    {
                                        existingFilesVO.add(fileDetailsVO);
                                    }
                                    else
                                    {
                                        existingFilesVO = new ArrayList<AppFileDetailsVO>();
                                        existingFilesVO.add(fileDetailsVO);
                                    }
                                    existingFileDetailsListVO.setFiledetailsvo(existingFilesVO);
                                    applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(),existingFileDetailsListVO);
                                }
                                else if (AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                                {
                                    for (AppFileDetailsVO existingFileDetailsVO : existingFilesVO)
                                    {
                                        if(existingFileDetailsVO.getMappingId().equals(fileDetailsVO.getMappingId()))
                                        {
                                            existingFilesVO.remove(existingFileDetailsVO);
                                        }
                                    }
                                    existingFilesVO.add(fileDetailsVO);
                                    existingFileDetailsListVO.setFiledetailsvo(existingFilesVO);
                                    applicationManagerVO.getFileDetailsVOs().put(fileDetailsVO.getFieldName(),existingFileDetailsListVO);

                                }
                            }
                        }
                    }
                }
            }

            for(Map.Entry<String,AppUploadLabelVO> uploadLabelVO:applicationManagerVO.getUploadLabelVOs().entrySet())              //changes Alternate name to label name......sagar
            {
                //Todo Put logger error to priint uploadLabelVO.getValue().getAlternateName()
                // Todo applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getValue().getAlternateName())
                logger.debug("Alternate Name in for loop...."+uploadLabelVO.getValue().getAlternateName());
                logger.debug("ismandatory Name in for loop...."+uploadLabelVO.getValue().getIsManadatory());
                logger.debug("containsKey value in for loop....."+applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getValue().getAlternateName()));
                if ("Y".equals(uploadLabelVO.getValue().getIsManadatory())){
                    if(!applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getValue().getAlternateName()) && validationErrorList.getError(uploadLabelVO.getValue().getAlternateName())==null)
                    {
                        // Todo applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getValue().getAlternateName())
                        logger.debug("inside if validation....");
                        logger.debug("inside if...."+applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getValue().getAlternateName()));
                        validationErrorList.addError(uploadLabelVO.getValue().getAlternateName(),new ValidationException(uploadLabelVO.getValue().getAlternateName(),"Invalid "+uploadLabelVO.getValue().getAlternateName()+" or document has not been uploaded",pageNumber));
                    }
                }
            }
        }
    }

}