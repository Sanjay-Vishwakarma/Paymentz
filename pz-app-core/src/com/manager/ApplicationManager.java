package com.manager;

import com.dao.AppFileExtractionDao;
import com.dao.ApplicationManagerDAO;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.enums.*;
import com.lowagie.text.pdf.PdfReader;
import com.manager.enums.FunctionalUsage;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.utils.AppFileHandlingUtil;
import com.utils.FtpFileHandlingUtil;
import com.validators.BankInputName;
import com.vo.applicationManagerVOs.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.ZipOutputStream;

//import com.manager.dao.FileExtractionDao;

//import com.utils.FileHandlingUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 1/16/15
 * Time: 6:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationManager //step wise declaration of the method
{
    //common instances
    private static Logger logger = new Logger(ApplicationManager.class.getName());
    private static Functions functions = new Functions();
    private static Map<Integer,Map<DefinedAcroFields,Set<BankInputName>>> mandatoryValidationMapOfList = new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();
    private static Map<Integer,Map<DefinedAcroFields,Set<BankInputName>>> optionalValidationMapOfList = new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();

    //private static Map<String,List<GatewayTypeVO>> memberGatewayListMap  = new HashMap<String, List<GatewayTypeVO>>();
    //private static Map<String,Map<Boolean,Set<DefinedAcroFields>>> memberAcroFieldsValidation  = new HashMap<String, Map<Boolean, Set<DefinedAcroFields>>>();
    private static Map<Integer,Map<DefinedAcroFields,Set<BankInputName>>> dependencyMandatoryValidationMapOfList = new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();
    private static Map<Integer,Map<DefinedAcroFields,Set<BankInputName>>> dependencyOptionalValidationMapOfList = new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();
    private static Map<Integer,Map<Boolean,Set<BankInputName>>> propertyValidationMapOfSet = new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    private static Map<Integer,Map<Boolean,Set<BankInputName>>> propertyDependencyValidationMapOfSet = new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
    //DAO instance
    private ApplicationManagerDAO applicationManagerDAO = new ApplicationManagerDAO();
    private AppFileExtractionDao fileExtractionDao = new AppFileExtractionDao();
    //private static Map<String,Map<Integer,Map<Boolean,Set<BankInputName>>>> fullValidationMapOfSet = new HashMap<String, Map<Integer, Map<Boolean, Set<BankInputName>>>>();
    //private static Map<String,Map<Integer,Map<Boolean,Set<BankInputName>>>> dependencyFullValidationMapOfSet = new HashMap<String, Map<Integer, Map<Boolean, Set<BankInputName>>>>();
    //private static Map<String,Map<Integer,Set<BankInputName>>> otherFullValidationMapOfSet = new HashMap<String, Map<Integer,Set<BankInputName>>>();
    static
    {
        loadPropertiesFileForMandatoryAndOptionalValidation();
    }

    /**
     * Application status dropdown elements without the list of Application status that is not needed.
     * @param selectedItem
     * @param applicationStatusListNotNeeded
     * @return
     */

    public static StringBuffer getApplicationStatus(String selectedItem,List<ApplicationStatus> applicationStatusListNotNeeded)
    {
        Functions functions= new Functions();

        StringBuffer optionList= new StringBuffer();

        for (ApplicationStatus applicationStatus: ApplicationStatus.values())
        {
            if(applicationStatusListNotNeeded!=null && applicationStatusListNotNeeded.contains(applicationStatus))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && applicationStatus.name().contains(selectedItem))
                {
                    optionList.append("<option value=\"" + applicationStatus.name() + "\" selected>" + applicationStatus.name() + "</option>");
                }
                else
                {
                    optionList.append("<option value=\"" + applicationStatus.name() + "\" >" + applicationStatus.name() + "</option>");
                }
            }
        }

        return optionList;
    }

    public static StringBuffer getBankApplicationStatus(String selectedItem,List<BankApplicationStatus> statusListNotNeeded)
    {
        Functions functions= new Functions();

        StringBuffer optionList= new StringBuffer();

        for (BankApplicationStatus bankApplicationStatus: BankApplicationStatus.values())
        {
            if(statusListNotNeeded!=null && !statusListNotNeeded.contains(bankApplicationStatus))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && bankApplicationStatus.name().contains(selectedItem))
                {
                    optionList.append("<option value=\"" + bankApplicationStatus.name() + "\" selected>" + bankApplicationStatus.name() + "</option>");
                }
                else
                {
                    optionList.append("<option value=\"" + bankApplicationStatus.name() + "\" >" + bankApplicationStatus.name() + "</option>");
                }
            }
        }

        return optionList;
    }

    public static StringBuffer getConsolidatedAppStatusOptionTag(String selectedItem,List<ConsolidatedAppStatus> notIncludeConsolidatedAppStatusList)
    {
        Functions functions= new Functions();

        StringBuffer optionList= new StringBuffer();

        for (ConsolidatedAppStatus consolidatedAppStatus: ConsolidatedAppStatus.values())
        {
            if(notIncludeConsolidatedAppStatusList!=null && notIncludeConsolidatedAppStatusList.contains(consolidatedAppStatus))
            {

            }
            else
            {
                if (functions.isValueNull(selectedItem) && consolidatedAppStatus.name().contains(selectedItem))
                {
                    optionList.append("<option value=\"" + consolidatedAppStatus.name() + "\" selected>" + consolidatedAppStatus.name() + "</option>");
                }
                else
                {
                    optionList.append("<option value=\"" + consolidatedAppStatus.name() + "\" >" + consolidatedAppStatus.name() + "</option>");
                }
            }
        }

        return optionList;
    }

    //This is to load all the property from the properties Related to ApplicationManager
    public static void loadPropertiesFileForMandatoryAndOptionalValidation()
    {
        AppRequestManager appRequestManager  =new AppRequestManager();

        mandatoryValidationMapOfList=new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();//Initialize after each refresh
        optionalValidationMapOfList=new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();//Initialize after each refresh
        dependencyMandatoryValidationMapOfList=new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();//Initialize after each refresh
        dependencyOptionalValidationMapOfList=new HashMap<Integer, Map<DefinedAcroFields, Set<BankInputName>>>();//Initialize after each refresh
        propertyValidationMapOfSet= new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();//Initialize after each refresh
        propertyDependencyValidationMapOfSet= new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();//Initialize after each refresh

        try
        {
            NavigationVO navigationVO = appRequestManager.getNavigationVO(null, Module.ADMIN);//THIS is JUST To extract ALL the Property File
            for (Map.Entry<Integer, String> stepAndPageNamePair : navigationVO.getStepAndPageName().entrySet())
            {
                String propertiesFile = stepAndPageNamePair.getValue();
                ResourceBundle resourceBundle = LoadProperties.getProperty("com.directi.pg.bankApplication." + propertiesFile.replaceFirst("\\.jsp", ""));
                ////logger.debug("Property CHECK:::"+propertiesFile.replaceFirst("\\.jsp", ""));
                setValueFromThePropertyFile(resourceBundle, stepAndPageNamePair.getKey());
            }
        }
        catch (Exception e)
        {
            //logger.error("Exception while fetching the Property from the file",e);
        }

    }

    //Set property from the properties Mandatory and Optional Field
    private static void setValueFromThePropertyFile(ResourceBundle resourceBundle, int pageNo)
    {

        if(resourceBundle!=null)
        {
            Enumeration<String> keys = resourceBundle.getKeys();
            Map<DefinedAcroFields,Set<BankInputName>> definedMandatoryAcroFieldsSetMap= new HashMap<DefinedAcroFields, Set<BankInputName>>();
            Map<DefinedAcroFields,Set<BankInputName>> definedOptionalAcroFieldsSetMap= new HashMap<DefinedAcroFields, Set<BankInputName>>();
            Map<DefinedAcroFields,Set<BankInputName>> definedDependencyManadatoryAcroFieldsSetMap= new HashMap<DefinedAcroFields, Set<BankInputName>>();
            Map<DefinedAcroFields,Set<BankInputName>> definedDependencyOptionalAcroFieldsSetMap= new HashMap<DefinedAcroFields, Set<BankInputName>>();
            Map<Boolean,Set<BankInputName>> fullValidation=new HashMap<Boolean, Set<BankInputName>>();
            Map<Boolean,Set<BankInputName>> dependencyFullValidation=new HashMap<Boolean, Set<BankInputName>>();


            while (keys.hasMoreElements())
            {
                String name = keys.nextElement();
                String value = resourceBundle.getString(name);

                Set<BankInputName> mandatoryBankInputNames = null;
                Set<BankInputName> optionalBankInputNames = null;
                Set<BankInputName> dependencyMandatoryBankInputNames = null;
                Set<BankInputName> dependencyOptionalBankInputNames = null;

                Set<BankInputName> mandatoryBankInputNameSet = new HashSet<BankInputName>();
                Set<BankInputName> optionalBankInputNameSet = new HashSet<BankInputName>();

                String[] bankInputs = value.split(",");
                for (int i = 0; i < bankInputs.length; i++)
                {
                    if (bankInputs[i].startsWith("M|"))
                    {
                        String bankInputName = bankInputs[i].replaceFirst("M\\|", "");
                        if (functions.isValueNull(name) && DefinedAcroFields.getEnum(name) != null && definedMandatoryAcroFieldsSetMap.containsKey(DefinedAcroFields.getEnum(name)) && definedMandatoryAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name)) != null)
                        {
                            mandatoryBankInputNames = definedMandatoryAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name));
                            if (BankInputName.getEnum(bankInputName) != null)
                                mandatoryBankInputNames.add(BankInputName.getEnum(bankInputName));
                        }
                        else
                        {
                            mandatoryBankInputNames = new HashSet<BankInputName>();
                            if (BankInputName.getEnum(bankInputName) != null)
                                mandatoryBankInputNames.add(BankInputName.getEnum(bankInputName));
                        }

                        if (DefinedAcroFields.getEnum(name) != null)
                        {
                            definedMandatoryAcroFieldsSetMap.put(DefinedAcroFields.getEnum(name), mandatoryBankInputNames);
                        }

                    }
                    else if (bankInputs[i].startsWith("O|"))
                    {
                        String bankInputName = bankInputs[i].replaceFirst("O\\|", "");
                        if (functions.isValueNull(name) && DefinedAcroFields.getEnum(name) != null && definedOptionalAcroFieldsSetMap.containsKey(DefinedAcroFields.getEnum(name)) && definedOptionalAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name)) != null)
                        {
                            optionalBankInputNames = definedOptionalAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name));
                            if (BankInputName.getEnum(bankInputName) != null)
                                optionalBankInputNames.add(BankInputName.getEnum(bankInputName));
                        }
                        else
                        {
                            optionalBankInputNames = new HashSet<BankInputName>();
                            if (BankInputName.getEnum(bankInputName) != null)
                                optionalBankInputNames.add(BankInputName.getEnum(bankInputName));
                        }


                        if (DefinedAcroFields.getEnum(name) != null)
                        {
                            definedOptionalAcroFieldsSetMap.put(DefinedAcroFields.getEnum(name), optionalBankInputNames);
                        }


                    }
                    else if (bankInputs[i].startsWith("D|"))
                    {
                        String dependencyValue = bankInputs[i].replaceAll("D\\|", "");
                        if (dependencyValue.startsWith("M|"))
                        {
                            String bankInputName = dependencyValue.replaceFirst("M\\|", "");
                            if (functions.isValueNull(name) && DefinedAcroFields.getEnum(name) != null && definedDependencyManadatoryAcroFieldsSetMap.containsKey(DefinedAcroFields.getEnum(name)) && definedDependencyManadatoryAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name)) != null)
                            {
                                dependencyMandatoryBankInputNames = definedDependencyManadatoryAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name));
                                if (BankInputName.getEnum(bankInputName) != null)
                                    dependencyMandatoryBankInputNames.add(BankInputName.getEnum(bankInputName));
                            }
                            else
                            {
                                dependencyMandatoryBankInputNames = new HashSet<BankInputName>();
                                if (BankInputName.getEnum(bankInputName) != null)
                                    dependencyMandatoryBankInputNames.add(BankInputName.getEnum(bankInputName));
                            }

                            if (DefinedAcroFields.getEnum(name) != null)
                            {
                                definedDependencyManadatoryAcroFieldsSetMap.put(DefinedAcroFields.getEnum(name), dependencyMandatoryBankInputNames);
                            }
                        }
                        else if (dependencyValue.startsWith("O|"))
                        {
                            String bankInputName = dependencyValue.replaceFirst("O\\|", "");
                            if (functions.isValueNull(name) && DefinedAcroFields.getEnum(name) != null && definedDependencyOptionalAcroFieldsSetMap.containsKey(DefinedAcroFields.getEnum(name)) && definedDependencyOptionalAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name)) != null)
                            {
                                dependencyOptionalBankInputNames = definedDependencyOptionalAcroFieldsSetMap.get(DefinedAcroFields.getEnum(name));
                                if (BankInputName.getEnum(bankInputName) != null)
                                    dependencyOptionalBankInputNames.add(BankInputName.getEnum(bankInputName));
                            }
                            else
                            {
                                dependencyOptionalBankInputNames = new HashSet<BankInputName>();
                                if (BankInputName.getEnum(bankInputName) != null)
                                    dependencyOptionalBankInputNames.add(BankInputName.getEnum(bankInputName));
                            }


                            if (DefinedAcroFields.getEnum(name) != null)
                            {
                                definedDependencyOptionalAcroFieldsSetMap.put(DefinedAcroFields.getEnum(name), dependencyOptionalBankInputNames);
                            }

                        }
                    }

                    //logger.debug("D| present"+bankInputs[i].startsWith("D|"));

                    if (!bankInputs[i].startsWith("D|"))
                    {
                        if (fullValidation.containsKey(false))
                        {
                            mandatoryBankInputNameSet = fullValidation.get(false);
                            //logger.debug("Mandatory mandatoryBankInputNameSet:::" + mandatoryBankInputNameSet);
                        }

                        if (fullValidation.containsKey(true))
                        {
                            optionalBankInputNameSet = fullValidation.get(true);
                            //logger.debug("Optional optionalBankInputNameSet:::" + optionalBankInputNameSet);
                        }

                        if (mandatoryBankInputNames != null && !mandatoryBankInputNames.isEmpty())
                        {
                            //logger.debug("Mandatory Bank InputName:::" + mandatoryBankInputNames);
                            mandatoryBankInputNameSet.addAll(mandatoryBankInputNames);
                            fullValidation.put(false, mandatoryBankInputNameSet);
                        }

                        if (optionalBankInputNames != null && !optionalBankInputNames.isEmpty())
                        {
                            //logger.debug("Optional Bank InputName:::" + optionalBankInputNames);
                            optionalBankInputNameSet.addAll(optionalBankInputNames);
                            fullValidation.put(true, optionalBankInputNameSet);
                        }
                    }
                    else
                    {
                        if (dependencyFullValidation.containsKey(false))
                        {
                            mandatoryBankInputNameSet = dependencyFullValidation.get(false);
                            //logger.debug("DEPENDENCY Mandatory mandatoryBankInputNameSet:::" + mandatoryBankInputNameSet);
                        }

                        if (dependencyFullValidation.containsKey(true))
                        {
                            optionalBankInputNameSet = dependencyFullValidation.get(true);
                            //logger.debug("DEPENDENCY Optional optionalBankInputNameSet:::" + optionalBankInputNameSet);
                        }

                        if (dependencyMandatoryBankInputNames != null && !dependencyMandatoryBankInputNames.isEmpty())
                        {
                            //logger.debug("DEPENDENCY Mandatory Bank InputName:::" + dependencyMandatoryBankInputNames);
                            mandatoryBankInputNameSet.addAll(dependencyMandatoryBankInputNames);
                            dependencyFullValidation.put(false, mandatoryBankInputNameSet);
                        }

                        if (dependencyOptionalBankInputNames != null && !dependencyOptionalBankInputNames.isEmpty())
                        {
                            //logger.debug("DEPENDENCY Optional Bank InputName:::" + dependencyOptionalBankInputNames);
                            optionalBankInputNameSet.addAll(dependencyOptionalBankInputNames);
                            dependencyFullValidation.put(true, optionalBankInputNameSet);
                        }
                    }
                }
            }

            Set<BankInputName> mandateInputName=new HashSet<BankInputName>();
            Set<BankInputName> optionalInputName=new HashSet<BankInputName>();
            Set<BankInputName> dependencyMandatoryInputName=new HashSet<BankInputName>();
            Set<BankInputName> dependencyOptionalInputName=new HashSet<BankInputName>();

            if(fullValidation.containsKey(false))
            {
               mandateInputName=fullValidation.get(false);
            }

            if(fullValidation.containsKey(true))
            {
                optionalInputName=fullValidation.get(true);
            }

            optionalInputName.removeAll(mandateInputName);

            if(dependencyFullValidation.containsKey(false))
            {
                dependencyMandatoryInputName=dependencyFullValidation.get(false);
            }

            if(dependencyFullValidation.containsKey(true))
            {
                dependencyOptionalInputName=dependencyFullValidation.get(true);
            }

            dependencyMandatoryInputName.removeAll(mandateInputName);
            dependencyMandatoryInputName.removeAll(optionalInputName);

            dependencyOptionalInputName.removeAll(mandateInputName);
            dependencyOptionalInputName.removeAll(optionalInputName);
            dependencyOptionalInputName.removeAll(dependencyMandatoryInputName);

            fullValidation.put(true,optionalInputName);
            fullValidation.put(false,mandateInputName);
            dependencyFullValidation.put(true,dependencyOptionalInputName);
            dependencyFullValidation.put(false,dependencyMandatoryInputName);

            propertyValidationMapOfSet.put(pageNo,fullValidation);
            propertyDependencyValidationMapOfSet.put(pageNo,dependencyFullValidation);
            mandatoryValidationMapOfList.put(pageNo,definedMandatoryAcroFieldsSetMap);
            optionalValidationMapOfList.put(pageNo,definedOptionalAcroFieldsSetMap);
            dependencyMandatoryValidationMapOfList.put(pageNo,definedDependencyManadatoryAcroFieldsSetMap);
            dependencyOptionalValidationMapOfList.put(pageNo,definedDependencyOptionalAcroFieldsSetMap);

            //logger.debug("Final Mandatory List:::"+mandatoryValidationMapOfList);
            //logger.debug("Final Optional List:::"+optionalValidationMapOfList);
            //logger.debug("Dependency Final Mandatory List:::"+dependencyMandatoryValidationMapOfList);
            //logger.debug("Dependency Final Optional List:::"+dependencyOptionalValidationMapOfList);
            //logger.debug("Final PropertyValidation"+propertyValidationMapOfSet);
            //logger.debug("DEPENDENCY Final PropertyValidation"+propertyDependencyValidationMapOfSet);
        }
    }

    //populate from when not in session
    public void populateAppllicationData(ApplicationManagerVO applicationManagerVO)
    {
         applicationManagerDAO.populateApplicationVO(applicationManagerVO);
        logger.error("partner id : "+applicationManagerVO.getPartnerid());
        //upload two queries 1st for list of document and 2nd for files uploaded by the merchant
        applicationManagerVO.setFileDetailsVOs(applicationManagerDAO.getApplicationUploadedFileDetail(applicationManagerVO.getMemberId()));
        applicationManagerVO.setUploadLabelVOs(fileExtractionDao.getListOfUploadLabel(FunctionalUsage.APPLICATIONMANAGER.toString(),applicationManagerVO.getPartnerid()));
    }

    //populate for applicationID and memberID for admin report
      public List<ApplicationManagerVO> getApplicationManagerVO(ApplicationManagerVO applicationManagerVO)
      {
          return applicationManagerDAO.getApplicationManagerVO(applicationManagerVO);
      }

    public List<ApplicationManagerVO> getSuperPartnerApplicationManagerVO(ApplicationManagerVO applicationManagerVO,String partnerId)
    {
        return applicationManagerDAO.getSuperPartnerApplicationManagerVO(applicationManagerVO,partnerId);
    }

    public List<ApplicationManagerVO> getPartnerApplicationManagerVO(ApplicationManagerVO applicationManagerVO,String partnerId)
    {
        return applicationManagerDAO.getPartnerApplicationManagerVO(applicationManagerVO,partnerId);
    }

    public Map<String, List<BankApplicationMasterVO>> getPartnerBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO, String orderBy,String groupBy,String partnerID )
    {
        return applicationManagerDAO.getPartnerBankApplicationMasterVOForMemberId(bankApplicationMasterVO, orderBy, groupBy,partnerID);
    }

    public Map<String, List<BankApplicationMasterVO>> getSuperPartnerBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO, String orderBy,String groupBy,String partnerID )
    {
        return applicationManagerDAO.getSuperPartnerBankApplicationMasterVOForMemberId(bankApplicationMasterVO, orderBy, groupBy, partnerID);
    }

    //populate for applicationID and memberID for admin report
    public List<ApplicationManagerVO> getPartnersMerchantApplicationManagerVO(String partnerId)
    {
        return applicationManagerDAO.getPartnersMerchantApplicationManagerVO(partnerId);
    }

    public List<ApplicationManagerVO> getSuperPartnersMerchantApplicationManagerVO(String partnerId)
    {
        return applicationManagerDAO.getSuperPartnersMerchantApplicationManagerVO(partnerId);
    }

    //populate for applicationID and memberID for admin report
    public List<ApplicationManagerVO> getSuperPartnersMerchantApplicationManagerVO1(String partnerId,PaginationVO paginationVO)
    {
        return applicationManagerDAO.getSuperPartnersMerchantApplicationManagerVO1(partnerId, paginationVO);
    }

    //populate for applicationID and memberID for admin report
    public List<ApplicationManagerVO> getPartnersMerchantApplicationManagerVO1(String partnerId,PaginationVO paginationVO)
    {
        return applicationManagerDAO.getPartnersMerchantApplicationManagerVO1(partnerId, paginationVO);
    }

    //populate for applicationID and memberID for admin report
    public List<ApplicationManagerVO> getPartnersNewMerchantApplicationManagerVO(String partnerId)
    {
        return applicationManagerDAO.getPartnersNewMerchantApplicationManagerVO(partnerId);
    }

    public List<ApplicationManagerVO> getSuperPartnersNewMerchantApplicationManagerVO(String partnerId)
    {
        return applicationManagerDAO.getSuperPartnersNewMerchantApplicationManagerVO(partnerId);
    }

    //populate for applicationID and memberID for admin report
    public List<ApplicationManagerVO> getPartnersNewMerchantApplicationManagerVO1(String partnerId, PaginationVO paginationVO)
    {
        return applicationManagerDAO.getPartnersNewMerchantApplicationManagerVO(partnerId, paginationVO);
    }

    public List<ApplicationManagerVO> getSuperPartnersNewMerchantApplicationManagerVO1(String partnerId, PaginationVO paginationVO)
    {
        return applicationManagerDAO.getSuperPartnersNewMerchantApplicationManagerVO(partnerId, paginationVO);
    }

    //update applicationManager Status
    public boolean updateAppManagerStatus(ApplicationManagerVO applicationManagerVO) throws Exception
    {
        return applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);

    }

    public ApplicationManagerVO getApplicationManagerDetails(ApplicationManagerVO applicationManagerVO)
    {
        ApplicationManagerDAO applicationManagerDAO = new ApplicationManagerDAO();

        applicationManagerVO = applicationManagerDAO.getAppManagerDetails(applicationManagerVO);
        return applicationManagerVO;
    }

    public ApplicationManagerVO getApplicationManagerDetailsFromApplicationId(ApplicationManagerVO applicationManagerVO)
    {
        ApplicationManagerDAO applicationManagerDAO = new ApplicationManagerDAO();

        applicationManagerVO = applicationManagerDAO.getApplicationManagerDetailsFromApplicationId(applicationManagerVO);
        return applicationManagerVO;
    }

    /**
     * This is only to update the status of the application for Kyc API
     * @param applicationManagerVO
     * @param kyc_status
     * @return
     * @throws Exception
     */
    public boolean onlyInsertAndUpdateApplicationManagerForKycAPI(ApplicationManagerVO applicationManagerVO,ApplicationStatus kyc_status,ApplicationStatus status) throws Exception
    {
        if (!functions.isValueNull(applicationManagerVO.getApplicationSaved()) && !"Y".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(status!=null?status.toString():ApplicationStatus.SAVED.toString());
            applicationManagerVO.setKyc_Status(kyc_status!=null ? kyc_status.toString() : ApplicationStatus.SAVED.toString());
            return applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else if ("Y".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(status!=null?status.toString():ApplicationStatus.SAVED.toString());
            applicationManagerVO.setKyc_Status(kyc_status!=null ? kyc_status.toString() : ApplicationStatus.SAVED.toString());
            return applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }
        return false;
    }

    /**
     * Save all profile for API
     * @param navigationVO
     * @param applicationManagerVO
     * @return
     */
    public boolean saveAllPageForApi(NavigationVO navigationVO,ApplicationManagerVO applicationManagerVO,ApplicationStatus maf_status) throws Exception
    {
        boolean isSaved=true;

        if(navigationVO!=null && navigationVO.getStepAndPageName()!=null && navigationVO.getStepAndPageName().containsKey(6))
            navigationVO.getStepAndPageName().remove(6);
        for(Map.Entry<Integer,String> stepNoAndNamePair:navigationVO.getStepAndPageName().entrySet())
        {
            navigationVO.setCurrentPageNO(stepNoAndNamePair.getKey());
            navigationVO.setConditionalValidation(false);//mandatory check
            isSaved=isSaved && saveCurrentPage(navigationVO, applicationManagerVO, stepNoAndNamePair.getKey() == 1,maf_status);
        }

        return isSaved;
    }

    //converting list to hashMap with k=alternateValue & value=UploadFileVo according to the functional usage
    /*public Map<String,AppFileDetailsVO> uploadMultipleFileAppManager(HttpServletRequest request,ApplicationManagerVO applicationManagerVO,boolean isAPI,FormDataMultiPart formDataMultiPart) throws Exception
    {
        FileManager  fileManager = new FileManager();
        if(!functions.isValueNull(applicationManagerVO.getApplicationSaved()) || "N".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        Map<String,AppFileDetailsVO> fileDetailsVOHashMap=fileManager.uploadMultipleFileForAppManager(request,applicationManagerVO,isAPI,formDataMultiPart);



        return fileDetailsVOHashMap;
    }*/

    //inserting the current Page info to the table
    public boolean saveCurrentPage(NavigationVO navigationVO,ApplicationManagerVO applicationManagerVO,boolean isFirstStepOrNormal,ApplicationStatus maf_status) throws Exception
    {
        logger.debug("currentPageNo::"+navigationVO.getCurrentPageNO());
        //this is while the creation of the application

        if(isFirstStepOrNormal)
        {
            if (!functions.isValueNull(applicationManagerVO.getApplicationSaved()) && !"Y".equals(applicationManagerVO.getApplicationSaved()) && !applicationManagerDAO.isApplicationExistForMember(applicationManagerVO.getMemberId()))
            {
                applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
                applicationManagerVO.setMaf_Status(maf_status!=null?maf_status.toString():ApplicationStatus.SAVED.toString());
                applicationManagerDAO.insertApplicationManager(applicationManagerVO);
            }
            else if ("Y".equals(applicationManagerVO.getApplicationSaved()))
            {
                applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
                applicationManagerVO.setMaf_Status(maf_status!=null?maf_status.toString():ApplicationStatus.SAVED.toString());
                applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
            }
        }

        //to keep all FK application_id to sync with PK application_id
        applicationManagerVO.getCompanyProfileVO().setApplicationId(applicationManagerVO.getApplicationId());
        applicationManagerVO.getOwnershipProfileVO().setApplicationid(applicationManagerVO.getApplicationId());
        applicationManagerVO.getBusinessProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getBankProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getCardholderProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getExtradetailsprofileVO().setApplication_id(applicationManagerVO.getApplicationId());
        //this is for insert Or update of particular Profile
        if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "companyprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Company Profile");
            if(!functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()) || "N".equals(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()))
            {
                return applicationManagerDAO.insertCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }
            else
            {
                return applicationManagerDAO.updateCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "ownershipprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Ownership Profile");
            if(!functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnerShipProfileSaved()) || "N".equals(applicationManagerVO.getOwnershipProfileVO().getOwnerShipProfileSaved()))
            {
                return applicationManagerDAO.insertOwnershipProfile(applicationManagerVO.getOwnershipProfileVO());
            }
            else
            {
                return applicationManagerDAO.updateOwnershipProfile(applicationManagerVO.getOwnershipProfileVO());
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "businessprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Business Profile");

            if(!functions.isValueNull(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()) || "N".equals(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()))
            {
                return applicationManagerDAO.insertBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }
            else
            {
                return applicationManagerDAO.updateBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "bankapplication.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Bank Profile");
            if(!functions.isValueNull(applicationManagerVO.getBankProfileVO().getBankProfileSaved()) || "N".equals(applicationManagerVO.getBankProfileVO().getBankProfileSaved()))
            {
                applicationManagerDAO.insertBankProfile(applicationManagerVO.getBankProfileVO());
                if(!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
                {
                    applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
                }
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
                {
                    applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
                }
                return false;
            }
            else
            {
                applicationManagerDAO.updateBankProfile(applicationManagerVO.getBankProfileVO());
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
                {
                    applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
                }
                else
                {
                    applicationManagerDAO.updateProcessingHistoryByID(applicationManagerVO.getBankProfileVO());
                }
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
                {
                    applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
                }
                else
                {
                    applicationManagerDAO.updateCurrencyWiseBankInfoByID(applicationManagerVO.getBankProfileVO());
                }
                return false;
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "cardholderprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Cardholder Profile");
            if(!functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getCardHolderProfileSaved()) || "N".equals(applicationManagerVO.getCardholderProfileVO().getCardHolderProfileSaved()))
            {
                return applicationManagerDAO.insertCardholderProfile(applicationManagerVO.getCardholderProfileVO());
            }
            else
            {
                return applicationManagerDAO.updateCardholderProfile(applicationManagerVO.getCardholderProfileVO());
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "extradetailsprofile.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
            applicationManagerVO.setNotificationMessage("Extradetails Profile");
            if(!functions.isValueNull(applicationManagerVO.getExtradetailsprofileVO().getExtraDetailsProfileSaved()) || "N".equals(applicationManagerVO.getExtradetailsprofileVO().getExtraDetailsProfileSaved()))
            {
                return applicationManagerDAO.insertExtraDetailsProfile(applicationManagerVO.getExtradetailsprofileVO());
            }
            else
            {
                return applicationManagerDAO.updateExtraDetailsProfile(applicationManagerVO.getExtradetailsprofileVO());
            }
        }
        else if(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())!=null && "upload.jsp".equals(navigationVO.getShowPageName(navigationVO.getCurrentPageNO())))
        {
          //no operation
        }

       return  false;
    }

    //inserting the Step1 Speed Process
    public boolean saveStep1Page(ApplicationManagerVO applicationManagerVO) throws SystemError
    {

        boolean isSaved=true;

        String speed_status=applicationManagerVO.getSpeed_status();
        logger.debug("SAVED ::: SPEED STATUS:::"+speed_status);

        if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
        {
            applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
        }

        if(functions.isValueNull(applicationManagerVO.getUser()))
        {
            applicationManagerVO.setUser(applicationManagerVO.getUser());
        }


        //this is while the creation of the application
        if(!functions.isValueNull(applicationManagerVO.getApplicationSaved()) && !"Y".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(ApplicationStatus.STEP1_SAVED.name());
            applicationManagerVO.setSpeed_status(ApplicationStatus.STEP1_SAVED.name());
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else if("Y".equals(applicationManagerVO.getApplicationSaved()))
        {
            if(ApplicationStatus.STEP1_SAVED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.STEP1_SUBMIT.name().equals(applicationManagerVO.getStatus()))
            {
                applicationManagerVO.setStatus(ApplicationStatus.STEP1_SAVED.name());

            }

            /*if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
            {
                applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
            }*/

            applicationManagerVO.setSpeed_status(ApplicationStatus.STEP1_SAVED.name());
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        if( !applicationManagerDAO.isApplicationExistForMember(applicationManagerVO.getMemberId()) && !functions.isValueNull(applicationManagerVO.getApplicationId()) && !functions.isValueNull(speed_status) )
        {
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else
        {
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        //to keep all FK application_id to sync with PK application_id
        applicationManagerVO.getCompanyProfileVO().setApplicationId(applicationManagerVO.getApplicationId());
        applicationManagerVO.getOwnershipProfileVO().setApplicationid(applicationManagerVO.getApplicationId());
        applicationManagerVO.getBusinessProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getBankProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getCardholderProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
        applicationManagerVO.getExtradetailsprofileVO().setApplication_id(applicationManagerVO.getApplicationId());
        //this is for insert Or update of particular Profile

            if(!functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()) || "N".equals(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()))
            {
                isSaved= isSaved && applicationManagerDAO.insertCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }
            else
            {
                isSaved= isSaved && applicationManagerDAO.updateCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }

            if(!functions.isValueNull(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()) || "N".equals(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()))
            {
                isSaved= isSaved && applicationManagerDAO.insertBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }
            else
            {
                isSaved= isSaved && applicationManagerDAO.updateBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }

        logger.debug("applicationManagerVO.getBankProfileVO().getBankProfileSaved()---->"+applicationManagerVO.getBankProfileVO().getBankProfileSaved());
        if(!functions.isValueNull(applicationManagerVO.getBankProfileVO().getBankProfileSaved()) || "N".equals(applicationManagerVO.getBankProfileVO().getBankProfileSaved()))
        {
            logger.debug("Inside IF for inserting the information--->");
            isSaved= isSaved && applicationManagerDAO.insertBankProfile(applicationManagerVO.getBankProfileVO());
            if(!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
            {
                isSaved = isSaved && applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
            }

            if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
            {
                applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
            }
        }
        else
        {
            logger.debug("Inside ELSE for updating the information---");
            isSaved= isSaved && applicationManagerDAO.updateBankProfile(applicationManagerVO.getBankProfileVO());
//            isSaved = isSaved && applicationManagerDAO.updateProcessingHistory(applicationManagerVO.getBankProfileVO());
            if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
            {
                applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
            }
            else
            {
                applicationManagerDAO.updateProcessingHistoryByID(applicationManagerVO.getBankProfileVO());
            }
            if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
            {
                applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
            }
            else
            {
                applicationManagerDAO.updateCurrencyWiseBankInfoByID(applicationManagerVO.getBankProfileVO());
            }
        }
       return  isSaved;
    }

    public  boolean submitAllProfile(ApplicationManagerVO applicationManagerVO,boolean isStep1,boolean onlyChangeApplicationStatus)throws SystemError
    {
        String speed_status=applicationManagerVO.getSpeed_status();
        logger.debug("SUBMIT ::: SPEED STATUS:::"+speed_status);

        if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
        {
            applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
        }

        if(functions.isValueNull(applicationManagerVO.getUser()))
        {
            applicationManagerVO.setUser(applicationManagerVO.getUser());
        }

        if(isStep1)
        {
            if(ApplicationStatus.STEP1_SAVED.name().equals(applicationManagerVO.getStatus()) || ApplicationStatus.STEP1_SUBMIT.name().equals(applicationManagerVO.getStatus()))
            {
                applicationManagerVO.setStatus(ApplicationStatus.STEP1_SUBMIT.name());

            }

            if(functions.isValueNull(applicationManagerVO.getSpeed_user()))
            {
                applicationManagerVO.setSpeed_user(applicationManagerVO.getSpeed_user());
            }

            applicationManagerVO.setSpeed_status(ApplicationStatus.STEP1_SUBMIT.name());
        }
        else
        {
            applicationManagerVO.setMaf_Status(ApplicationStatus.SUBMIT.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SUBMIT.name());
            applicationManagerVO.setStatus(ApplicationStatus.SUBMIT.name());
        }

        if(!functions.isValueNull(applicationManagerVO.getApplicationSaved()) || !"Y".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else
        {
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        //to keep all FK application_id to sync with PK application_id
        if(!onlyChangeApplicationStatus)
        {
            applicationManagerVO.getCompanyProfileVO().setApplicationId(applicationManagerVO.getApplicationId());
            applicationManagerVO.getOwnershipProfileVO().setApplicationid(applicationManagerVO.getApplicationId());
            applicationManagerVO.getBusinessProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
            applicationManagerVO.getBankProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
            applicationManagerVO.getCardholderProfileVO().setApplication_id(applicationManagerVO.getApplicationId());
            applicationManagerVO.getExtradetailsprofileVO().setApplication_id(applicationManagerVO.getApplicationId());

            if (!functions.isValueNull(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()) || "N".equals(applicationManagerVO.getCompanyProfileVO().getCompanyProfileSaved()))
            {
                applicationManagerDAO.insertCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }
            else
            {
                applicationManagerDAO.updateCompanyProfile(applicationManagerVO.getCompanyProfileVO());
            }

            if (!functions.isValueNull(applicationManagerVO.getOwnershipProfileVO().getOwnerShipProfileSaved()) || "N".equals(applicationManagerVO.getOwnershipProfileVO().getOwnerShipProfileSaved()))
            {
                applicationManagerDAO.insertOwnershipProfile(applicationManagerVO.getOwnershipProfileVO());
            }
            else
            {
                applicationManagerDAO.updateOwnershipProfile(applicationManagerVO.getOwnershipProfileVO());
            }

            if (!functions.isValueNull(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()) || "N".equals(applicationManagerVO.getBusinessProfileVO().getBusinessProfileSaved()))
            {
                applicationManagerDAO.insertBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }
            else
            {
                applicationManagerDAO.updateBusinessProfile(applicationManagerVO.getBusinessProfileVO());
            }

            if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getBankProfileSaved()) || "N".equals(applicationManagerVO.getBankProfileVO().getBankProfileSaved()))
            {
                applicationManagerDAO.insertBankProfile(applicationManagerVO.getBankProfileVO());
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
                {
                    applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
                }

                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
                {
                    applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
                }
            }
            else
            {
                applicationManagerDAO.updateBankProfile(applicationManagerVO.getBankProfileVO());
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()) || "N".equals(applicationManagerVO.getBankProfileVO().getIsProcessingHistory()))
                {
                    applicationManagerDAO.insertProcessingHistory(applicationManagerVO.getBankProfileVO());
                }
                else
                {
                    applicationManagerDAO.updateProcessingHistoryByID(applicationManagerVO.getBankProfileVO());
                }
                if (!functions.isValueNull(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()) || "N".equals(applicationManagerVO.getBankProfileVO().getIscurrencywisebankinfo()))
                {
                    applicationManagerDAO.insertCurrencyWiseBankInfo(applicationManagerVO.getBankProfileVO());
                }
                else
                {
                    applicationManagerDAO.updateCurrencyWiseBankInfoByID(applicationManagerVO.getBankProfileVO());
                }
            }

            if (!functions.isValueNull(applicationManagerVO.getCardholderProfileVO().getCardHolderProfileSaved()) || "N".equals(applicationManagerVO.getCardholderProfileVO().getCardHolderProfileSaved()))
            {
                applicationManagerDAO.insertCardholderProfile(applicationManagerVO.getCardholderProfileVO());
            }
            else
            {
                applicationManagerDAO.updateCardholderProfile(applicationManagerVO.getCardholderProfileVO());
            }

            if (Module.ADMIN.name().equals(applicationManagerVO.getUser()))
            {
                if (!functions.isValueNull(applicationManagerVO.getExtradetailsprofileVO().getExtraDetailsProfileSaved()) || "N".equals(applicationManagerVO.getExtradetailsprofileVO().getExtraDetailsProfileSaved()))
                {
                    applicationManagerDAO.insertExtraDetailsProfile(applicationManagerVO.getExtradetailsprofileVO());
                }
                else
                {
                    applicationManagerDAO.updateExtraDetailsProfile(applicationManagerVO.getExtradetailsprofileVO());
                }
            }
        }

        applicationManagerVO.setNotificationMessage("ALL Profile");
        return true;

    }

    //getting applicationManagerVO according to the memberid
    public List<ApplicationManagerVO> getapplicationManagerVO(String memberId,String applicationId,PaginationVO paginationVO)
    {
        return applicationManagerDAO.getapplicationManagerVO(memberId,applicationId,paginationVO);
    }

    //getting applicationManagerVO according to the memberid
    public List<ApplicationManagerVO> getPartnersNewSpecificMerchantApplicationManagerVO(String memberId,String partnerId,PaginationVO paginationVO)
    {
        return applicationManagerDAO.getPartnersNewSpecificMerchantApplicationManagerVO(memberId,partnerId,paginationVO);
    }

    public Map<String,FileDetailsListVO> uploadMultipleFileAppManager(HttpServletRequest request,ApplicationManagerVO applicationManagerVO,boolean isAPI,FormDataMultiPart formDataMultiPart) throws Exception
    {
        AppFileManager  fileManager = new AppFileManager();
        if(!functions.isValueNull(applicationManagerVO.getApplicationSaved()) || "N".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        Map<String,FileDetailsListVO> fileDetailsVOHashMap=fileManager.uploadMultipleFileForAppManager(request,applicationManagerVO,isAPI,formDataMultiPart);



        return fileDetailsVOHashMap;
    }

    //added method for single button file upload --surajT.
    public Map<String,FileDetailsListVO> uploadMultipleFileAppManagerNew(HttpServletRequest request,ApplicationManagerVO applicationManagerVO,boolean isAPI,FormDataMultiPart formDataMultiPart,String alternate_name) throws Exception
    {
        AppFileManager  fileManager = new AppFileManager();
        if(!functions.isValueNull(applicationManagerVO.getApplicationSaved()) || "N".equals(applicationManagerVO.getApplicationSaved()))
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.insertApplicationManager(applicationManagerVO);
        }
        else
        {
            applicationManagerVO.setStatus(ApplicationStatus.SAVED.name());
            applicationManagerVO.setKyc_Status(ApplicationStatus.SAVED.name());
            applicationManagerDAO.updateAppManagerStatus(applicationManagerVO);
        }

        Map<String,FileDetailsListVO> fileDetailsVOHashMap=fileManager.uploadMultipleFileForAppManagerNew(request, applicationManagerVO, isAPI, formDataMultiPart, alternate_name);



        return fileDetailsVOHashMap;
    }
    //getting uploaded file information
    public boolean insertUploadDocument(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
    {
        return applicationManagerDAO.insertUploadDocument(fileDetailsVO, applicationManagerVO);
    }

    //update on replace of the file
    public boolean updateUploadDocument(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
    {
        return applicationManagerDAO.updateMemberDocumentMapping(fileDetailsVO, applicationManagerVO);
    }

    /*search memberID and Status wise for consolidated*/

    //inserting document history for application
    public boolean insertUploadDocumentHistory(AppFileDetailsVO fileDetailsVO,ApplicationManagerVO applicationManagerVO)
    {
        return applicationManagerDAO.insertMemberDocumentHistory(fileDetailsVO, applicationManagerVO);
    }

    //getting uploaded file data for application manager
    public Map<String,FileDetailsListVO> getApplicationUploadedDetail(String memberId)
    {
       return applicationManagerDAO.getApplicationUploadedFileDetail(memberId);
    }

    public boolean insertbankApplicationMaster(ApplicationManagerVO applicationManagerVO,AppFileDetailsVO fileDetailsVO)
     {
         return applicationManagerDAO.insertbankApplicationMaster(applicationManagerVO, fileDetailsVO);
     }


     /* This is to get the List of consolidatedApplication associated with particular consolidatedid*/

    public Map<String, BankApplicationMasterVO> getBankApplicationMasterVO(BankApplicationMasterVO bankApplicationMasterVO)
    {
        return applicationManagerDAO.getBankApplicationMasterVO(bankApplicationMasterVO);
    }

    /**
     * This is to get the List of BankApplicationMaster associated with particularPgTypeId
     * @param bankApplicationMasterVO
     * @return
     */
    public Map<String, List<BankApplicationMasterVO>> getBankApplicationMasterVOForGatewayId(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy)
    {
        return applicationManagerDAO.getBankApplicationMasterVOForGatewayId(bankApplicationMasterVO, orderBy, groupBy);
    }

    public Map<String, List<BankApplicationMasterVO>> getBankApplicationMasterVOForGatewayIdandStatus(BankApplicationMasterVO bankApplicationMasterVO,String orderBy,String groupBy)
    {

        return applicationManagerDAO.getBankApplicationMasterVOForGatewayIdandStatus(bankApplicationMasterVO, orderBy, groupBy);
    }

    //Update Consolidated Member Id
    public Map<String, List<BankApplicationMasterVO>> getBankApplicationMasterVOForMemberId(BankApplicationMasterVO bankApplicationMasterVO, String orderBy,String groupBy )
    {
        return applicationManagerDAO.getBankApplicationMasterVOForMemberId(bankApplicationMasterVO, orderBy, groupBy);
    }

    //Update BankApplicationMaster
    public boolean updateBankApplicationMasterVO(BankApplicationMasterVO bankApplicationMasterVO,String bankapplicationid,String memberId)
    {
        return applicationManagerDAO.updateBankApplicationMasterVO(bankApplicationMasterVO,bankapplicationid,memberId);
    }

    public boolean insertconsolidatedapplication(AppFileDetailsVO fileDetailsVO,String adminId)
    {
        return applicationManagerDAO.insertconsolidated_application(fileDetailsVO,adminId);
    }

    public Map<String, ConsolidatedApplicationVO> getconsolidated_application(ConsolidatedApplicationVO consolidatedApplicationVO)
    {
        return applicationManagerDAO.getconsolidated_application(consolidatedApplicationVO);
    }

    //Sagar changes

    public Map<String, ConsolidatedApplicationVO> getconsolidated_applicationForMemberIdOrPgTypeId(String memberId,String name,String consolidatedId)
    {
        return applicationManagerDAO.getconsolidated_applicationIDpgtypeID(memberId, name, consolidatedId);
    }

    public boolean insertConsolidated_Application_History(AppFileDetailsVO fileDetailsVO,String adminId)
    {
        return applicationManagerDAO.insertConsolidated_Application_History(fileDetailsVO, adminId);
    }


    //update consolidated_application History Status

    public boolean deleteConsolidated_application(String consolidated_id)
    {
        return applicationManagerDAO.deleteConsolidated_Application(consolidated_id);
    }

    public boolean updateConsolidatedAppHistory(ConsolidatedAppStatus consolidatedAppStatus ,String consolidatedId)throws PZDBViolationException
    {
        return applicationManagerDAO.updateConsolidatedAppHistory(consolidatedAppStatus, consolidatedId);
    }

    public AppFileDetailsVO getSingleApplicationUploadedFileDetail(String mappingId)
    {
        return applicationManagerDAO.getSingleApplicationUploadedFileDetail(mappingId);

    }

    //END

    /*public AppFileDetailsVO createZIPOfBankAndKYCPdf(AppFileDetailsVO fileDetailsVO,BankApplicationMasterVO bankApplicationMasterVO)
    {
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        FileManager fileManager = new FileManager();
        ZipOutputStream zipOutputStream=null;
        File file=null;
        //String itemName = item.getName();

        try
        {
            zipOutputStream=fileHandlingUtil.getZIPOutputStream(fileManager.APPLICATION_ZIP_PATH+bankApplicationMasterVO.getBankfilename());
            logger.error("zipOutputStream========="+zipOutputStream);
            if(zipOutputStream==null)
            {
                logger.error("Zip not created");
            }
            else
            {
                fileHandlingUtil.addToZipFile(file, zipOutputStream);
                //fileHandlingUtil.getZIPOutputStream();
            }
        }
        catch (FileNotFoundException e)
        {
            logger.error("file not found while creating Zip file for bank application",e);
        }
        catch (IOException e)
        {
            logger.error("Exception in zip createZIPOfBankAndKYCPdf"+e);
        }

        return null;
    }
*/

    //Update Consolidated Application Status
    public boolean updateconsolidatedapplication(ConsolidatedApplicationVO consolidatedApplicationVO,String consolidatedId,String memberId)
    {
        return applicationManagerDAO.updateconsolidated_application(consolidatedApplicationVO, consolidatedId, memberId);
    }

    public boolean updateconsolidated_applicationforConsolidatedID(ConsolidatedApplicationVO consolidatedApplicationVO)
    {
        return applicationManagerDAO.updateconsolidated_applicationforConsolidatedID(consolidatedApplicationVO);
    }

    //Sagar Changes
    //Deleting consolidated Application

    public boolean updateconsolidated_applicationHistoryStatus(ConsolidatedApplicationVO consolidatedApplicationVO,String consolidatedId,String memberId)
    {
        return applicationManagerDAO.updateconsolidated_applicationHistoryStatus(consolidatedApplicationVO, consolidatedId, memberId);
    }
    //End

    //Update AppManager Status

    public Map<String, ConsolidatedApplicationVO> getconsolidated_applicationHistoryForMemberIdOrPgTypeId(String memberId,String name,String consolidatedId)
    {
        return applicationManagerDAO.getconsolidated_applicationHistoryIDpgtypeID(memberId,name,consolidatedId);
    }



    //update applicationManager Status

    public boolean deleteConsolidated_application_History(String consolidated_id)
    {
        return applicationManagerDAO.deleteConsolidated_Application_History(consolidated_id);
    }

    /**
     * Set all the Validation related to the specif and common
     * @param memberId
     * @param navigationVO
     * @param fullValidationForStep
     * @param dependencyFullValidationForStep
     * @param dependencyPageViseValidation
     * @param otherValidation
     * @param dependencyOtherFullValidationForStep
     * @param appValidationVO
     */
    public void setValidationForMember(String memberId,NavigationVO navigationVO,Map<Integer,Map<Boolean,Set<BankInputName>>> fullValidationForStep,Map<Integer,Map<Boolean,Set<BankInputName>>> dependencyFullValidationForStep,Map<Boolean,Set<BankInputName>> dependencyPageViseValidation,Map<Integer,Set<BankInputName>> otherValidation,Map<Integer,Map<Boolean,Set<BankInputName>>> dependencyOtherFullValidationForStep,Set<BankInputName> otherValidationPageVise,Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation,AppValidationVO appValidationVO)
    {
        if (appValidationVO!=null && functions.isValueNull(memberId) && appValidationVO.getSpecificValidationMapOfSet(memberId) != null)
        {
            fullValidationForStep.putAll(appValidationVO.getSpecificValidationMapOfSet(memberId));
            otherValidation.putAll(appValidationVO.getSpecificOtherOptionalValidation(memberId));
            dependencyFullValidationForStep.putAll(appValidationVO.getSpecificDependencyValidation(memberId));
            dependencyOtherFullValidationForStep.putAll(getPropertyDependencyValidation());
        }
        else
        {
            fullValidationForStep.putAll(getPropertyValidation());
            dependencyFullValidationForStep.putAll(getPropertyDependencyValidation());
        }

        if(dependencyFullValidationForStep.containsKey(navigationVO.getCurrentPageNO()))
        {
            dependencyPageViseValidation.putAll(dependencyFullValidationForStep.get(navigationVO.getCurrentPageNO()));
        }

        if(dependencyOtherFullValidationForStep.containsKey(navigationVO.getCurrentPageNO()))
        {
            dependencyOtherPageViseValidation.putAll(dependencyOtherFullValidationForStep.get(navigationVO.getCurrentPageNO()));
        }

        if(otherValidation.containsKey(navigationVO.getCurrentPageNO()))
        {
            otherValidationPageVise.addAll(otherValidation.get(navigationVO.getCurrentPageNO()));
        }

    }

    //select Bank Manage Status

    //generating consolidated Application
    public Map<String,AppFileDetailsVO> generateConsolidatedBankApplication(String[] bankApplicationIds,String memberId,HttpServletRequest request) throws PZTechnicalViolationException
    {
        AppFileManager fileManager = new AppFileManager();

        BankApplicationMasterVO bankApplicationMasterVO = new BankApplicationMasterVO();
        bankApplicationMasterVO.setMember_id(memberId);
        ApplicationManager applicationManager=new ApplicationManager();
        //bankApplicationMasterVO.setBankapplicationid(bankApplicationIds);

        HttpSession session = Functions.getNewSession(request);

        Map<String,BankApplicationMasterVO> bankApplicationMasterVOMap=getBankApplicationMasterVO(bankApplicationMasterVO);
        Map<String,FileDetailsListVO> kycFileDetails=getApplicationUploadedDetail(memberId);

        ConsolidatedApplicationVO consolidatedApplicationVO = new ConsolidatedApplicationVO();
        consolidatedApplicationVO.setMemberid(memberId);

       /* if(functions.isValueNull(consolidatedID))
        {
            consolidatedApplicationVO.setConsolidated_id(consolidatedID);
        }*/

        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = getconsolidated_application(consolidatedApplicationVO);

        logger.debug("kyc details::::::"+kycFileDetails);
        //AppFileDetailsVO singleKyc=null;

        ZipOutputStream zos=null;
        String directoryPath="";
        String ftpPath="";
        if(fileManager.isFtpEnabledForDocumentationServer())
        {
            directoryPath =fileManager.TEMP_APPLICATION_ZIP_PATH + "/" + memberId + "/";
            ftpPath=fileManager.FTP_APPLICATION_ZIP_PATH+"/"+memberId+"/";
        }
        else
        {
            directoryPath = fileManager.APPLICATION_ZIP_PATH + "/" + memberId + "/";
        }
        boolean createDirectory=false;
        boolean delete=false;
        File bankApplication=null;
        File kycFile=null;
        //File file=null;

        AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();
        Map<String,AppFileDetailsVO> fileDetailsVOList = new HashMap<String, AppFileDetailsVO>();

        AppFileHandlingUtil fileHandlingUtil=null;
        try
        {
            fileHandlingUtil= fileManager.getFileHandlingUtilAccordingToTheProperty();


            for (String bankApplicationId : bankApplicationIds)
            {
                logger.error("bank application id--------" + bankApplicationId);
                Calendar currentDate = Calendar.getInstance();
                String[] kycSelected = request.getParameterValues(bankApplicationId + "_mappingId");
                fileDetailsVO.setMemberid(memberId);
                fileDetailsVO.setFilename("");
                fileDetailsVO.setFieldName(bankApplicationId);
                fileDetailsVO.setFileType("");
                fileDetailsVO.setFilePath("");
                fileDetailsVO.setDtstamp("");
                fileDetailsVO.setTimestamp("");
                if (kycSelected == null)
                {
                    logger.error("Please select at least one kyc details");
                    fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                    fileDetailsVO.setSuccess(false);
                    fileDetailsVO.setReasonOfFailure("Please select at least one kyc details");
                }
                else
                {
                    try
                    {
                        String current_date = Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND)));

                        createDirectory = fileManager.makeDirectory(directoryPath, true, new AppFileHandlingUtil());//This is for making directory in app server
                        if (createDirectory)
                        {
                            bankApplicationMasterVO = bankApplicationMasterVOMap.get(bankApplicationId);
                            bankApplication = fileManager.getBankGenerateTemplate(bankApplicationMasterVO, fileHandlingUtil);
                            fileDetailsVO.setFilename(fileManager.removeFileExtension(bankApplicationMasterVO.getBankfilename()) +"_"+current_date+".zip");
                            zos = fileManager.getZipOutputStream(directoryPath + fileDetailsVO.getFilename());

                            fileManager.addFileToZip(bankApplication, zos);
                            if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                                fileManager.deleteFileAccordingToFTPProperty(bankApplication, null, (FtpFileHandlingUtil) fileHandlingUtil);
                            for (String kycAlternateName : kycSelected)             //Need to check
                            {
                                logger.debug("kyc Name:::" + kycAlternateName);
                                String[] splitStr = kycAlternateName.split("\\|");


                                if(splitStr.length>0)
                                {
                                    String alternateName = splitStr[0];
                                    String mappingId = splitStr[1];
                                    List<AppFileDetailsVO> fileDetailsVOs = kycFileDetails.get(alternateName).getFiledetailsvo();
                                    for (AppFileDetailsVO singleKyc : fileDetailsVOs)
                                    {
                                        logger.debug("SingleKyc::::" + singleKyc);
                                        if(singleKyc.getMappingId().equals(mappingId))
                                        {
                                            kycFile = fileManager.getKYCDetails(singleKyc, fileHandlingUtil);
                                            fileManager.addFileToZip(kycFile, zos);
                                            if (fileHandlingUtil instanceof FtpFileHandlingUtil)
                                                fileManager.deleteFileAccordingToFTPProperty(kycFile, null, (FtpFileHandlingUtil) fileHandlingUtil);
                                        }

                                    }
                                }
                            }
                            fileDetailsVO.setFileActionType(AppFileActionType.GENERATE);
                            fileDetailsVO.setSuccess(true);
                            fileDetailsVO.setReasonOfFailure("");
                            fileDetailsVO.setLabelId(bankApplicationMasterVO.getPgtypeid());
                            fileDetailsVO.setDtstamp(current_date);

                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("File Not found while creating ZIP");
                    }
                    catch (IOException e)
                    {
                        fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("File Not found while creating ZIP");
                    }
                    finally
                    {

                        try
                        {
                            if(zos!=null)
                                zos.close();
                        }
                        catch (IOException e)
                        {
                            logger.error("Exception while closing ZipoutPutStream",e);
                        }
                    }
                }

                if (fileDetailsVO.isSuccess())
                {
                    File transferFile = new File(directoryPath + fileDetailsVO.getFilename());
                    if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                        fileManager.deleteFileAccordingToFTPProperty(transferFile, ftpPath + fileDetailsVO.getFilename(), (FtpFileHandlingUtil) fileHandlingUtil);


                    if(consolidatedApplicationVOMap.containsKey(fileDetailsVO.getLabelId()))
                    {
                        logger.debug("Update....");
                        consolidatedApplicationVO = consolidatedApplicationVOMap.get(fileDetailsVO.getLabelId());
                        consolidatedApplicationVO.setStatus(BankApplicationStatus.GENERATED.name());
                        consolidatedApplicationVO.setFilename(fileDetailsVO.getFilename());
                        consolidatedApplicationVO.setDtstamp(fileDetailsVO.getDtstamp());
                        applicationManager.updateconsolidated_applicationforConsolidatedID(consolidatedApplicationVO);
                        //applicationManager.updateconsolidatedapplication(consolidatedApplicationVO, consolidatedApplicationVO.getConsolidated_id(), consolidatedApplicationVO.getMemberid());

                    }
                    else
                    {
                        logger.debug("insert....");
                        applicationManager.insertconsolidatedapplication(fileDetailsVO, (String) session.getAttribute("merchantid"));
                    }
                    applicationManager.insertConsolidated_Application_History(fileDetailsVO, (String) session.getAttribute("merchantid"));
                }

                fileDetailsVOList.put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }
        }
        finally
        {
            if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                fileManager.closeFtpConnection(null,true,(FtpFileHandlingUtil) fileHandlingUtil);
        }


        return  fileDetailsVOList;
    }

    public Map<String,AppFileDetailsVO> deleteConsolidatedZip(String consolidatedIDs,String memberId,HttpServletRequest request,String filename) throws PZTechnicalViolationException
    {
        logger.debug("consolidatedID---->"+consolidatedIDs);
        ConsolidatedApplicationVO consolidatedApplicationVO = new ConsolidatedApplicationVO();
        //Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = getconsolidated_applicationHistory(consolidatedApplicationVO);
        AppFileManager fileManager=new AppFileManager();
        AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

        String directoryPath="";
        String ftpPath="";
        if(fileManager.isFtpEnabledForDocumentationServer())
        {
            directoryPath =fileManager.TEMP_APPLICATION_ZIP_PATH + "/" + memberId + "/";
            ftpPath=fileManager.FTP_APPLICATION_ZIP_PATH+"/"+memberId+"/";

        }
        else
        {
            directoryPath = fileManager.APPLICATION_ZIP_PATH + "/" + memberId + "/";
        }
        boolean createDirectory=false;
        boolean delete=false;
        File bankApplication=null;
        Map<String,AppFileDetailsVO> fileDetailsVOList = new HashMap<String, AppFileDetailsVO>();

        AppFileHandlingUtil fileHandlingUtil=null;
        try
        {
            fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();

            logger.debug("Consolidated ID--------" + consolidatedIDs);
            logger.debug("filedetailsVO    "+filename);
            if(fileDetailsVO!=null)
            {

                try
                {
                    logger.debug("try in......");

                    logger.debug("File name......"+filename);


                    if (fileHandlingUtil instanceof FtpFileHandlingUtil)
                    {
                        File Filename = new File(ftpPath + filename);
                        fileManager.deleteFileFromFTPServer(Filename, (FtpFileHandlingUtil) fileHandlingUtil);
                    }
                    else
                    {
                        File Filename = new File(directoryPath + filename);
                        logger.debug("delete file....."+filename);
                        fileHandlingUtil.deleteFile(Filename);
                    }


                }
                catch (Exception e)
                {

                    fileDetailsVO.setReasonOfFailure("Exception while Delete........."+e.getMessage());
                }


                fileDetailsVOList.put(filename, fileDetailsVO);
            }

        }
        catch(Exception e)
        {
            logger.debug("Exception"+e.getMessage());
        }
        finally
        {
            if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                fileManager.closeFtpConnection(null,true,(FtpFileHandlingUtil) fileHandlingUtil);
        }

        return  fileDetailsVOList;
    }

    public boolean updateConsolidatedAppStatus(ConsolidatedAppStatus consolidatedAppStatus ,String consolidatedId)throws PZDBViolationException
    {
        return applicationManagerDAO.updateConsolidatedAppStatus(consolidatedAppStatus,consolidatedId);

    }

    /*public Map<String, GatewayTypeVO> getBankMerchantMappingDetails(String partnerId,String memberId) throws PZDBViolationException
    {
        return applicationManagerDAO.getBankMemberMappingWithGateway(partnerId,memberId);
    }*/

    //Bank Merchant Mapping for Application API
    public boolean insertBankMerchantMapping(List<BankTypeVO> bankTypeVOs,String memberId) throws PZDBViolationException
    {
       return applicationManagerDAO.insertBankMerchantMappingForApplication(bankTypeVOs,memberId);
    }

    public boolean deleteBankMerchantMappingForUpdate(String memberId) throws PZDBViolationException
    {
        return applicationManagerDAO.deleteBankMerchantMappingForApplication(memberId);
    }

    /*public Map<Boolean, Set<DefinedAcroFields>> getMemberAcrofFieldsValidation(String memberId)
    {
        if(memberAcroFieldsValidation.containsKey(memberId))
        {
              return memberAcroFieldsValidation.get(memberId);
        }
        else
        {
            return null;
        }
    }*/

    public Map<String, List<BankTypeVO>> getBankMerchantMappingDetailsByMap(String memberId, PaginationVO paginationVO) throws PZDBViolationException
    {
        return applicationManagerDAO.getBankMemberMappingWithGatewayMap(memberId, paginationVO);
    }

    /*public Map<Integer, Map<Boolean, Set<BankInputName>>> getSpecificValidationMapOfSet(String memberId)
    {
        if(fullValidationMapOfSet.containsKey(memberId))
        {
              return fullValidationMapOfSet.get(memberId);
        }
        else
        {
            return null;
        }
    }*/

    /*public List<GatewayTypeVO> getMemberGatewayList(String memberId)
    {
        if(memberGatewayListMap.containsKey(memberId))
        {
              return memberGatewayListMap.get(memberId);
        }
        else
        {
            return null;
        }
    }*/

    public AppValidationVO loadAllMerchantBankMapping(String memberId) throws PZDBViolationException
    {

        //logger.debug("INSIDE LOAD MERCHANT BANK MAPPING::::");
        ApplicationManager applicationManager = new ApplicationManager();
        AppRequestManager appRequestManager = new AppRequestManager();
        AppFileManager fileManager = new AppFileManager();
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        AppValidationVO appValidationVO = new AppValidationVO();

        Map<String,List<BankTypeVO>> memberGatewayListMap = applicationManager.getBankMerchantMappingDetailsByMap(memberId,null);
        Map<String, Map<Boolean, Set<DefinedAcroFields>>> memberAcroFieldsValidation = new HashMap<String, Map<Boolean, Set<DefinedAcroFields>>>();
        Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> fullValidationMapOfSet = new HashMap<String, Map<Integer, Map<Boolean, Set<BankInputName>>>>();
        Map<String, Map<Integer, Map<Boolean, Set<BankInputName>>>> dependencyFullValidationMapOfSet = new HashMap<String, Map<Integer, Map<Boolean, Set<BankInputName>>>>();
        Map<String, Map<Integer,Set<BankInputName>>> otherFullValidationMapOfSet = new HashMap<String, Map<Integer,Set<BankInputName>>>();

        if(functions.isValueNull(memberId))
        {
            for (Map.Entry<String, List<BankTypeVO>> memberGatewayListPair : memberGatewayListMap.entrySet())
            {
                //logger.debug("Inside gateway Member List"+memberGatewayListPair.getKey());
                Map<Boolean, Set<DefinedAcroFields>> definedAcroFieldsValidation = null;

                if (memberAcroFieldsValidation.containsKey(memberGatewayListPair.getKey()))
                {
                    definedAcroFieldsValidation = memberAcroFieldsValidation.get(memberGatewayListPair.getKey());
                    //logger.debug("Defined AcroFields Present for Member:::"+memberGatewayListPair.getKey()+" Acrofields:::"+definedAcroFieldsValidation);
                }
                else
                {
                    definedAcroFieldsValidation = new HashMap<Boolean, Set<DefinedAcroFields>>();
                    //logger.debug("Defined AcroFields New Member");
                }

                List<BankTypeVO> bankTypeVOList = memberGatewayListPair.getValue();
                Map<Integer, Map<Boolean, Set<BankInputName>>> pageViseFullValidationMap = null;
                Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyPageViseFullValidationMap = null;
                Map<Integer, Set<BankInputName>> pageViseOtherFullValidationMap = null;
                if (bankTypeVOList != null)
                {
                    for (BankTypeVO bankTypeVO : bankTypeVOList)
                    {
                        logger.debug("Gateway:::" + bankTypeVO.getBankId() + " Gateway Name:::" + bankTypeVO.getBankName());
                        PdfReader pdfReader = null;
                        try
                        {
                            File pdfFile = fileManager.getBankAppTemplate(bankTypeVO);

                            if (pdfFile != null && pdfFile.exists())
                            {
                                //logger.debug("Inside File Found::" + pdfFile.getAbsolutePath());
                                pdfReader = fileHandlingUtil.getPdfReader(pdfFile.getAbsolutePath());

                                fileManager.getMandatoryAndOptionalAcroFieldsAccordingToTemplate(pdfReader, definedAcroFieldsValidation);
                                //logger.debug("After getMandatoryAndOptionalAcroFieldsAccordingToTemplate():::" + definedAcroFieldsValidation);
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            logger.debug("FileNotFound while fetching the pdf from the file specified For Bank Template Name" + bankTypeVO.getFileName());
                        }
                        catch (PZTechnicalViolationException e)
                        {
                            logger.error("PZTECHNICAL EXCEPTION:::", e);
                        }
                        finally
                        {
                            if (pdfReader != null)
                            {
                                pdfReader.close();
                            }
                        }
                    }

                    memberAcroFieldsValidation.put(memberGatewayListPair.getKey(), definedAcroFieldsValidation);
                    NavigationVO navigationVO = appRequestManager.getNavigationVO(null, Module.ADMIN);

                    if (fullValidationMapOfSet.containsKey(memberGatewayListPair.getKey()))
                    {
                        pageViseFullValidationMap = fullValidationMapOfSet.get(memberGatewayListPair.getKey());
                        //logger.debug("Present pageViseValidationMap:::"+pageViseFullValidationMap);
                    }
                    else
                    {
                        pageViseFullValidationMap = new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
                    }

                    if (dependencyFullValidationMapOfSet.containsKey(memberGatewayListPair.getKey()))
                    {
                        dependencyPageViseFullValidationMap = dependencyFullValidationMapOfSet.get(memberGatewayListPair.getKey());
                        //logger.debug("DEPENDENCY Present pageViseValidationMap:::"+pageViseFullValidationMap);
                    }
                    else
                    {
                        dependencyPageViseFullValidationMap = new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
                    }

                    if (otherFullValidationMapOfSet.containsKey(memberGatewayListPair.getKey()))
                    {
                        pageViseOtherFullValidationMap = otherFullValidationMapOfSet.get(memberGatewayListPair.getKey());
                        //logger.debug("Other Present pageViseValidationMap:::"+pageViseOtherFullValidationMap);
                    }
                    else
                    {
                        pageViseOtherFullValidationMap = new HashMap<Integer, Set<BankInputName>>();
                    }

                    navigationVO.getStepAndPageName().remove(7);
                    for (Map.Entry<Integer, String> stepNoAndNamePair : navigationVO.getStepAndPageName().entrySet())
                    {

                        Map<Boolean, Set<BankInputName>> propertyFullValidation = new HashMap<Boolean, Set<BankInputName>>();

                        if (propertyValidationMapOfSet.containsKey(stepNoAndNamePair.getKey()))
                        {
                            propertyFullValidation = propertyValidationMapOfSet.get(stepNoAndNamePair.getKey());
                        }

                        //logger.debug("STEP NO::::"+stepNoAndNamePair.getKey());
                        Map<Boolean, Set<BankInputName>> fullValidation = null;
                        Map<Boolean, Set<BankInputName>> dependencyFullValidation = null;
                        if (pageViseFullValidationMap.containsKey(stepNoAndNamePair.getKey()))
                        {
                            fullValidation = pageViseFullValidationMap.get(stepNoAndNamePair.getKey());
                        }
                        else
                        {
                            fullValidation = new HashMap<Boolean, Set<BankInputName>>();
                        }

                        if (dependencyPageViseFullValidationMap.containsKey(stepNoAndNamePair.getKey()))
                        {
                            dependencyFullValidation = dependencyPageViseFullValidationMap.get(stepNoAndNamePair.getKey());
                        }
                        else
                        {
                            dependencyFullValidation = new HashMap<Boolean, Set<BankInputName>>();
                        }

                        Set<BankInputName> bankInputNameMandatoryForDefinedAcroFields = new HashSet<BankInputName>();
                        Set<BankInputName> bankInputNameOptionalForDefinedAcroFields = new HashSet<BankInputName>();
                        Set<BankInputName> bankInputNameDependencyMandatoryForDefinedAcroFields = new HashSet<BankInputName>();
                        Set<BankInputName> bankInputNameDependencyOptionalForDefinedAcroFields = new HashSet<BankInputName>();
                        //Other
                        Set<BankInputName> bankInputNameOtherOptionalForDefinedAcroFields = new HashSet<BankInputName>();

                        Map<DefinedAcroFields, Set<BankInputName>> mandatoryBankInputName = mandatoryValidationMapOfList.get(stepNoAndNamePair.getKey());
                        Map<DefinedAcroFields, Set<BankInputName>> optionalBankInputName = optionalValidationMapOfList.get(stepNoAndNamePair.getKey());

                        Map<DefinedAcroFields, Set<BankInputName>> dependencyMandatoryBankInputName = dependencyMandatoryValidationMapOfList.get(stepNoAndNamePair.getKey());
                        Map<DefinedAcroFields, Set<BankInputName>> dependencyOptionalBankInputName = dependencyOptionalValidationMapOfList.get(stepNoAndNamePair.getKey());

                        //logger.debug("mandatoryBankInputName:::"+mandatoryBankInputName);
                        //logger.debug("optionalBankInputName:::"+optionalBankInputName);
                        //logger.debug("dependencyMandatoryBankInputName:::"+dependencyMandatoryBankInputName);
                        //logger.debug("dependencyOptionalBankInputName:::"+dependencyOptionalBankInputName);

                    /*for(Map.Entry<DefinedAcroFields,Set<BankInputName>> definedAndBankInputNamePair:mandatoryBankInputName.entrySet())
                    {
                        //logger.debug("Mandatory Bank InputName::::"+mandatoryBankInputName);
                        bankInputNameMandatorySet.addAll(definedAndBankInputNamePair.getValue());
                        //logger.debug("Mandatory Bank InputName After Removal::::"+mandatoryBankInputName);
                    }*/

                        if (definedAcroFieldsValidation.containsKey(false))
                        {
                            Set<DefinedAcroFields> definedAcroFieldsList = definedAcroFieldsValidation.get(false);

                            for (DefinedAcroFields definedAcroField : definedAcroFieldsList)
                            {
                                //logger.debug("FALSE Defined Acrofields::::"+definedAcroField);
                                if (mandatoryBankInputName.containsKey(definedAcroField))
                                    bankInputNameMandatoryForDefinedAcroFields.addAll(mandatoryBankInputName.get(definedAcroField));
                                if (optionalBankInputName.containsKey(definedAcroField))
                                    bankInputNameOptionalForDefinedAcroFields.addAll(optionalBankInputName.get(definedAcroField));
                                if (dependencyMandatoryBankInputName.containsKey(definedAcroField))
                                    bankInputNameDependencyMandatoryForDefinedAcroFields.addAll(dependencyMandatoryBankInputName.get(definedAcroField));
                                if (dependencyOptionalBankInputName.containsKey(definedAcroField))
                                    bankInputNameDependencyOptionalForDefinedAcroFields.addAll(dependencyOptionalBankInputName.get(definedAcroField));

                                //logger.debug("Mandatory ::::"+bankInputNameMandatoryForDefinedAcroFields+" optional::::"+bankInputNameOptionalForDefinedAcroFields+" DEPENDENCY Mandatory::::"+bankInputNameDependencyMandatoryForDefinedAcroFields+" Dependency Optional::::"+bankInputNameDependencyOptionalForDefinedAcroFields);
                            }
                        }

                        if (definedAcroFieldsValidation.containsKey(true))
                        {
                            Set<DefinedAcroFields> definedAcroFieldsList = definedAcroFieldsValidation.get(true);

                            for (DefinedAcroFields definedAcroField : definedAcroFieldsList)
                            {
                                //logger.debug("TRUE Defined Acrofields::::"+definedAcroField);
                                if (mandatoryBankInputName.containsKey(definedAcroField))
                                    bankInputNameOptionalForDefinedAcroFields.addAll(mandatoryBankInputName.get(definedAcroField));
                                if (optionalBankInputName.containsKey(definedAcroField))
                                    bankInputNameOptionalForDefinedAcroFields.addAll(optionalBankInputName.get(definedAcroField));
                                if (dependencyMandatoryBankInputName.containsKey(definedAcroField))
                                    bankInputNameDependencyMandatoryForDefinedAcroFields.addAll(dependencyMandatoryBankInputName.get(definedAcroField));
                                if (dependencyOptionalBankInputName.containsKey(definedAcroField))
                                    bankInputNameDependencyOptionalForDefinedAcroFields.addAll(dependencyOptionalBankInputName.get(definedAcroField));
                                //logger.debug("Mandatory ::::"+bankInputNameMandatoryForDefinedAcroFields+" optional::::"+bankInputNameOptionalForDefinedAcroFields+" DEPENDENCY Mandatory::::"+bankInputNameDependencyMandatoryForDefinedAcroFields+" Dependency Optional::::"+bankInputNameDependencyOptionalForDefinedAcroFields);
                            }
                        }

                        if (fullValidation.containsKey(false))
                        {
                            bankInputNameMandatoryForDefinedAcroFields.addAll(fullValidation.get(false));
                        }

                        if (fullValidation.containsKey(true))
                        {
                            bankInputNameOptionalForDefinedAcroFields.addAll(fullValidation.get(true));
                        }

                        if (dependencyFullValidation.containsKey(false))
                        {
                            bankInputNameDependencyMandatoryForDefinedAcroFields.addAll(dependencyFullValidation.get(false));
                        }

                        if (dependencyFullValidation.containsKey(true))
                        {
                            bankInputNameDependencyOptionalForDefinedAcroFields.addAll(dependencyFullValidation.get(true));
                        }

                        if (propertyFullValidation.containsKey(false))
                        {
                            bankInputNameOtherOptionalForDefinedAcroFields.addAll(propertyFullValidation.get(false));
                        }

                        if (propertyFullValidation.containsKey(true))
                        {
                            bankInputNameOtherOptionalForDefinedAcroFields.addAll(propertyFullValidation.get(true));
                        }

                        //logger.debug("BankInput Name AcroFields Mandatory::::"+bankInputNameMandatoryForDefinedAcroFields+" Optional:::"+bankInputNameOptionalForDefinedAcroFields);
                        //logger.debug("Dependency BankInput Name AcroFields Mandatory::::"+bankInputNameDependencyMandatoryForDefinedAcroFields+" Optional:::"+bankInputNameDependencyOptionalForDefinedAcroFields);
                        //logger.debug("OTHER BankInput Name AcroFields Optional::::"+bankInputNameOtherOptionalForDefinedAcroFields);
                        bankInputNameOptionalForDefinedAcroFields.removeAll(bankInputNameMandatoryForDefinedAcroFields);
                        bankInputNameDependencyOptionalForDefinedAcroFields.removeAll(bankInputNameDependencyMandatoryForDefinedAcroFields);
                        bankInputNameOtherOptionalForDefinedAcroFields.removeAll(bankInputNameMandatoryForDefinedAcroFields);
                        bankInputNameOtherOptionalForDefinedAcroFields.removeAll(bankInputNameOptionalForDefinedAcroFields);
                        bankInputNameOtherOptionalForDefinedAcroFields.removeAll(bankInputNameDependencyMandatoryForDefinedAcroFields);
                        bankInputNameOtherOptionalForDefinedAcroFields.removeAll(bankInputNameDependencyOptionalForDefinedAcroFields);
                        //logger.debug("After Removal BankInput Name AcroFields Mandatory::::" + bankInputNameMandatoryForDefinedAcroFields + " Optional:::" + bankInputNameOptionalForDefinedAcroFields);
                        //logger.debug("Dependency After Removal BankInput Name AcroFields Mandatory::::" + bankInputNameDependencyMandatoryForDefinedAcroFields + " Optional:::" + bankInputNameDependencyOptionalForDefinedAcroFields);
                        //logger.debug("Dependency After Removal BankInput Name AcroFields Mandatory::::"+bankInputNameDependencyMandatoryForDefinedAcroFields+" Optional:::"+bankInputNameDependencyOptionalForDefinedAcroFields);
                        //logger.debug("Dependency After Removal BankInput Name AcroFields Mandatory::::"+bankInputNameDependencyMandatoryForDefinedAcroFields+" Optional:::"+bankInputNameDependencyOptionalForDefinedAcroFields);
                        //logger.debug("OTHER After Removal BankInput Name AcroFields Optional::::"+bankInputNameOtherOptionalForDefinedAcroFields);
                        fullValidation.put(false, bankInputNameMandatoryForDefinedAcroFields);
                        fullValidation.put(true, bankInputNameOptionalForDefinedAcroFields);
                        dependencyFullValidation.put(false, bankInputNameDependencyMandatoryForDefinedAcroFields);
                        dependencyFullValidation.put(true, bankInputNameDependencyOptionalForDefinedAcroFields);

                        pageViseFullValidationMap.put(stepNoAndNamePair.getKey(), fullValidation);
                        dependencyPageViseFullValidationMap.put(stepNoAndNamePair.getKey(), dependencyFullValidation);
                        pageViseOtherFullValidationMap.put(stepNoAndNamePair.getKey(), bankInputNameOtherOptionalForDefinedAcroFields);
                    }

                    fullValidationMapOfSet.put(memberGatewayListPair.getKey(), pageViseFullValidationMap);
                    dependencyFullValidationMapOfSet.put(memberGatewayListPair.getKey(), dependencyPageViseFullValidationMap);
                    otherFullValidationMapOfSet.put(memberGatewayListPair.getKey(), pageViseOtherFullValidationMap);
                }
            }

        }

        appValidationVO.setMemberGatewayListMap(memberGatewayListMap);
        appValidationVO.setDependencyFullValidationMapOfSet(dependencyFullValidationMapOfSet);
        appValidationVO.setFullValidationMapOfSet(fullValidationMapOfSet);
        appValidationVO.setMemberAcroFieldsValidation(memberAcroFieldsValidation);
        appValidationVO.setOtherFullValidationMapOfSet(otherFullValidationMapOfSet);

        return appValidationVO;
        //logger.debug("Final AcroFields FullValidationMapSet:::"+fullValidationMapOfSet);
        //logger.debug("Dependency Final AcroFields FullValidationMapSet:::"+dependencyFullValidationMapOfSet);
    }

    /*public Map<Integer, Map<Boolean, Set<BankInputName>>> getSpecificDependencyValidation(String memberId)
    {
        if(dependencyFullValidationMapOfSet.containsKey(memberId))
        return dependencyFullValidationMapOfSet.get(memberId);

        return null;
    }*/

    /*public Map<Integer, Set<BankInputName>> getSpecificOtherOptionalValidation(String memberId)
    {
        if(otherFullValidationMapOfSet.containsKey(memberId))
        return otherFullValidationMapOfSet.get(memberId);

        return null;
    }*/

    public Map<Integer, Map<Boolean, Set<BankInputName>>> getPropertyValidation()
    {
        return propertyValidationMapOfSet;
    }

    public Map<Integer, Map<Boolean, Set<BankInputName>>> getPropertyDependencyValidation()
    {
       return propertyDependencyValidationMapOfSet;
    }

    public List<String> getcurrencyCode()
    {
        return applicationManagerDAO.getcurrencyCode();
    }

    public ContractualPartnerVO getContractualPartnerDetails(String memberId, String bankName)
    {
        return applicationManagerDAO.getContractualPartnerDetails(memberId, bankName);
    }

    public List<BankTypeVO> getAllGatewayMappedToPartner(String partnerId) throws PZDBViolationException
    {
        return applicationManagerDAO.getAllGatewayForPartner(partnerId);
    }

    public List<String> getPartnerBankDetail(String partnerid)
    {
        return applicationManagerDAO.getPartnerBankDetail(partnerid);
    }

    public List<BankTypeVO>  getListOfAllBankTypeWithAvailableTemplateName() throws SQLException
    {
        List<BankTypeVO> bankTypes = ApplicationManagerService.getAllBankTypes();

        return bankTypes;
    }

    public BankTypeVO getBankTypeForPgTypeId(String pgTypeId)
    {
        BankTypeVO bankTypeVO = new BankTypeVO();
        bankTypeVO = ApplicationManagerService.getBankType(pgTypeId);
        return bankTypeVO;
    }

    public BankTypeVO getBankTypeForBankId(String bankId)
    {
        return applicationManagerDAO.getBankTypeForBankId(bankId);
    }

    public TreeMap<Integer, String> getpartnerDetails()
    {
        return applicationManagerDAO.getpartnerDetails();
    }

    public List<String> getListOfBankId()
    {
        return applicationManagerDAO.getListOfBankId();
    }

    public List<String> loadPartnerId()
    {
        return applicationManagerDAO.loadPartnerId();
    }

    public boolean updateDefaultApplicationGatewayForPartnerAndPgTypeId(String partnerId,String pgTypeId,boolean defaultApplication) throws PZDBViolationException
    {
        return applicationManagerDAO.updateDefaultApplicationGatewayForPartnerAndPgTypeId(partnerId, pgTypeId,defaultApplication);
    }
    public boolean addBankTemplate(String bankName, String fileName) throws SystemError
    {
        return applicationManagerDAO.addBankTemplate(bankName, fileName);
    }
    public boolean updateBankTemplate(String bankName, String fileName) throws SystemError
    {
        return applicationManagerDAO.updateBankTemplate(bankName, fileName);
    }
    public boolean isBankExist(String bankName) throws SystemError
    {
        return applicationManagerDAO.isBankExist(bankName);
    }
    public BankTypeVO getBankTemplateDetails(String bankName) throws SystemError
    {
        return applicationManagerDAO.getBankTemplateDetails(bankName);
    }
    public TreeMap<String,String> selectPartnerIdAndPartnerName() throws PZDBViolationException
    {
        return applicationManagerDAO.selectPartnerIdAndPartnerName();
    }
    public boolean addPartnerBankMapping(String bankId, String partnerId) throws SystemError
    {
        return applicationManagerDAO.addPartnerBankMapping(bankId, partnerId);
    }
    public boolean isPartnerBankMappingExist(String bankId, String partnerId) throws SystemError
    {
        return applicationManagerDAO.isPartnerBankMappingExist(bankId, partnerId);
    }
    public String getAppManagerStatus(String memberId) throws SystemError, Exception
    {
        return applicationManagerDAO.getAppManagerStatus(memberId);
    }
    public boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        return applicationManagerDAO.sendFile(filepath, filename, response);
    }
    public List<BankTypeVO> getBankMappingDetails() throws PZDBViolationException
    {
        return applicationManagerDAO.getBankMappingDetails();
    }
    public List<BankTypeVO> getBankMappingDetails(String bankId, String bankName, PaginationVO paginationVO) throws PZDBViolationException
    {
        return applicationManagerDAO.getBankMappingDetails(bankId, bankName, paginationVO);
    }

    // consolidated memberid in manage application and consolidated application history
    public Map<String, ConsolidatedApplicationVO> getconsolidated_memberList(String partnerId)
    {
        return applicationManagerDAO.getconsolidated_memberList(partnerId);
    }

    // consolidated memberid in manage application and consolidated application history for superpartners.
    public Map<String, ConsolidatedApplicationVO> SuperPartner_getconsolidated_memberList(String partnerId)
    {
        return applicationManagerDAO.SuperPartner_getconsolidated_memberList(partnerId);
    }

    // consolidated memberid in manage application and consolidated application history
    public Map<String, ConsolidatedApplicationVO> getconsolidated_memberList_history(String partnerId)
    {
        return applicationManagerDAO.getconsolidated_memberList_history(partnerId);
    }

    // consolidated memberid in manage application and consolidated application  for superpartner
    public Map<String, ConsolidatedApplicationVO> getconsolidated_memberList_history_superpartner(String partnerId)
    {
        return applicationManagerDAO.getconsolidated_memberList_history_superpartner(partnerId);
    }
    // consolidated application history of mapped members
    public Map<String, ConsolidatedApplicationVO> getconsolidated_applicationHistoryForMappedMembers(String memberId,String name,String consolidatedId,String partnerId)
    {
        return applicationManagerDAO.getconsolidated_applicationHistoryForMappedMembers(memberId, name, consolidatedId, partnerId);
    }
    // consolidated application history of mapped members for superpartner
    public Map<String, ConsolidatedApplicationVO> getconsolidated_applicationHistoryForSuperPartner(String memberId,String name,String consolidatedId,String partnerId)
    {
        return applicationManagerDAO.getconsolidated_applicationHistoryForSuperPartner(memberId, name, consolidatedId, partnerId);
    }
     //get consolidated application by mapped members
    public Map<String, ConsolidatedApplicationVO> getconsolidated_applicationForMappedMembers(String memberId,String name,String consolidatedId,String partnerId)
    {
        return applicationManagerDAO.getconsolidated_applicationByMappedMembers(memberId, name, consolidatedId, partnerId);
    }
}
