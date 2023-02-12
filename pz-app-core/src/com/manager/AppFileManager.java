package com.manager;

import com.dao.AppFileExtractionDao;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.enums.AppFileActionType;
import com.enums.AppUploadFileType;
import com.enums.BankApplicationStatus;
import com.enums.DefinedAcroFields;
import com.helper.MerchantBankAppSwitch;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.utils.AppFileHandlingUtil;
import com.utils.FtpFileHandlingUtil;
import com.vo.applicationManagerVOs.*;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.net.ftp.FTPClient;
import org.owasp.esapi.ESAPI;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * Created by admin on 8/31/2017.
 */
public class AppFileManager
{

    private static Logger logger = new Logger(AppFileManager.class.getName());
    private static Functions functions = new Functions();
    //multiple file upload  for merchant For Application manager

    //AppFileHandlingUtil instance
    AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();

    //Dao for extracting the name of the file
    AppFileExtractionDao fileExtractionDao= new AppFileExtractionDao();

    // Application Manager Configuration
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

    public String GATEWAY_TEMPLATE_PATH=applicationResourceBundle.getString("GATEWAY_TEMPLATE_PATH");
    public String FTP_APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("FTP_APPLICATION_DOCUMENT_PATH");
    public String FTP_APPLICATION_BACKUP_DOCUMENT_PATH=applicationResourceBundle.getString("FTP_APPLICATION_BACKUP_DOCUMENT_PATH");
    public String FTP_BANK_APPLICATION_PATH=applicationResourceBundle.getString("FTP_BANK_APPLICATION_PATH");
    public String FTP_APPLICATION_ZIP_PATH=applicationResourceBundle.getString("FTP_APPLICATION_ZIP_PATH");


    public String APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("APPLICATION_DOCUMENT_PATH");
    public String APPLICATION_BACKUP_DOCUMENT_PATH=applicationResourceBundle.getString("APPLICATION_BACKUP_DOCUMENT_PATH");
    public String BANK_APPLICATION_PATH=applicationResourceBundle.getString("BANK_APPLICATION_PATH");
    public String APPLICATION_ZIP_PATH=applicationResourceBundle.getString("APPLICATION_ZIP_PATH");

    public String TEMP_APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("TEMP_APPLICATION_DOCUMENT_PATH");
    public String TEMP_APPLICATION_BACKUP_DOCUMENT_PATH=applicationResourceBundle.getString("TEMP_APPLICATION_BACKUP_DOCUMENT_PATH");
    public String TEMP_BANK_APPLICATION_PATH=applicationResourceBundle.getString("TEMP_BANK_APPLICATION_PATH");
    public String TEMP_APPLICATION_ZIP_PATH=applicationResourceBundle.getString("TEMP_APPLICATION_ZIP_PATH");

    public String STARTFTP = applicationResourceBundle.getString("STARTFTP");
    public String host=applicationResourceBundle.getString("host");
    public String port=applicationResourceBundle.getString("port");
    public String username=applicationResourceBundle.getString("username");
    public String password=applicationResourceBundle.getString("password");

    public AppFileHandlingUtil getFileHandlingUtilAccordingToTheProperty() throws PZTechnicalViolationException
    {
        //ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(STARTFTP))
        {
            FtpFileHandlingUtil ftpFileHandlingUtil= new FtpFileHandlingUtil();
            FTPServiceDetailsVO ftpServiceDetailsVO = new FTPServiceDetailsVO();

            ftpServiceDetailsVO.setHost(host);
            ftpServiceDetailsVO.setPort(functions.isValueNull(port)?Integer.valueOf(port):0);
            ftpServiceDetailsVO.setUsername(username);
            ftpServiceDetailsVO.setPassword(password);


            //order is important
            ftpFileHandlingUtil.setFtpServiceDetailsVO(ftpServiceDetailsVO);//credential is been set
            getFtpClientAccordingToTheProperty(ftpFileHandlingUtil);//socket connection made
            return ftpFileHandlingUtil;
        }

        return this.fileHandlingUtil;
    }

    private FTPClient getFtpClientAccordingToTheProperty(FtpFileHandlingUtil ftpFileHandlingUtil) throws PZTechnicalViolationException
    {
        //ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(STARTFTP) && ftpFileHandlingUtil!=null)
        {
            return ftpFileHandlingUtil.getFtpConnection();
        }

        return null;
    }

    private boolean closeFtpClientAccordingToTheProperty(FtpFileHandlingUtil ftpFileHandlingUtil) throws PZTechnicalViolationException
    {
        //ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(STARTFTP) && ftpFileHandlingUtil!=null && ftpFileHandlingUtil.getFtpClient()!=null)
        {
            return ftpFileHandlingUtil.closeConnection(ftpFileHandlingUtil.getFtpClient());
        }

        return true;
    }

    /**
     * This is to open FTP socket from appServer to Document Server
     * @param propertyName
     * @param isApplicationManager
     * @return
     * @throws PZTechnicalViolationException
     */
    public FTPClient getFtpConnection(String propertyName,boolean isApplicationManager,FtpFileHandlingUtil ftpFileHandlingUtil) throws PZTechnicalViolationException
    {
        if(isApplicationManager)
        {
            return getFtpClientAccordingToTheProperty(ftpFileHandlingUtil);
        }
        else
        {
            //WRITE the code if any other functionality is using document server for ftp using propertyName as input
        }

        return null;
    }

    /**
     * This is to close ftp socket connection
     * @param propertyName
     * @param isApplicationManager
     * @return
     * @throws PZTechnicalViolationException
     */
    public boolean closeFtpConnection(String propertyName,boolean isApplicationManager,FtpFileHandlingUtil ftpFileHandlingUtil) throws PZTechnicalViolationException
    {
        if(isApplicationManager)
        {
            return closeFtpClientAccordingToTheProperty(ftpFileHandlingUtil);
        }
        else
        {
            //WRITE the code if any other functionality is using document server for ftp using propertyName as input
        }

        return true;
    }

    // Changes for multiple KYC
    public Map<String,FileDetailsListVO> uploadMultipleFileForAppManager(HttpServletRequest request,ApplicationManagerVO applicationManagerVO,boolean isAPI,FormDataMultiPart formDataMultiPart) throws PZTechnicalViolationException
    {
        //manager instance
        ApplicationManager applicationManager = new ApplicationManager();

        Map<String,FileDetailsListVO> fileDetailsVOHashMap = new HashMap<String, FileDetailsListVO>();
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        List<FileItem> fileItemList=null;
        MultiHashMap mp = new MultiHashMap();
        AppFileHandlingUtil fileHandlingUtil=null;
        try
        {
            fileHandlingUtil = getFileHandlingUtilAccordingToTheProperty();
            if (isAPI)
            {
                fileItemList = this.fileHandlingUtil.getListOfFileItemForApplicationManager(formDataMultiPart, applicationManagerVO);
            }
            else
            {
                fileItemList = this.fileHandlingUtil.getListOfFileItem(request);
            }

            /*for (List<FileItem> FileItemlist : fileItemList)
            {*/

            for (FileItem fileItem : fileItemList)
            {
                AppFileDetailsVO fileDetailsVO = null;
                if (fileItem.getSize() > 0 && !fileItem.isFormField())
                {
                    String alternateName = fileItem.getFieldName().split("\\|")[0];
                    String labelID = fileItem.getFieldName().split("\\|")[1];

                    logger.debug("Inside FileManager alternatename " + alternateName);
                    logger.debug("Inside FileManager labelID " + labelID);

                    if (applicationManagerVO.getUploadLabelVOs() != null && applicationManagerVO.getUploadLabelVOs().containsKey(labelID))
                    {
                        AppUploadLabelVO uploadLabelVO = applicationManagerVO.getUploadLabelVOs().get(labelID);

                        for (String fileType : uploadLabelVO.getSupportedFileType().split(","))
                        {
                            if ("PDF".equalsIgnoreCase(fileType))
                                uploadFileTypeList.add(AppUploadFileType.PDF);
                            if ("XLSX".equalsIgnoreCase(fileType))
                                uploadFileTypeList.add(AppUploadFileType.EXCEL);
                            if ("PNG".equalsIgnoreCase(fileType))
                                uploadFileTypeList.add(AppUploadFileType.PNG);
                            if ("JPG".equalsIgnoreCase(fileType))
                                uploadFileTypeList.add(AppUploadFileType.JPG);
                        }
                    }
                    fileDetailsVO = this.fileHandlingUtil.doValidationOnFileItem(fileItem, uploadFileTypeList);
                    if (fileDetailsVO == null)
                    {
                        logger.debug("Inside FileManager.fileDetailsVO" + fileDetailsVO);
                        try
                        {
                            if (fileItem.getFieldName().contains("|Replace"))
                            {
                                logger.debug("inside replace condition");

                                HashMap<String,AppFileDetailsVO> fileDetailsVOMap= new HashMap<String, AppFileDetailsVO>();

                                List<AppFileDetailsVO> existingFilesVO = applicationManagerVO.getFileDetailsVOs().get(alternateName).getFiledetailsvo();
                                String mappingId = fileItem.getFieldName().split("\\|")[3];
                                logger.debug("existingFilesVO- Size"+existingFilesVO.size());
                                for (AppFileDetailsVO existingFileDetailsVO : existingFilesVO)
                                {
                                    logger.debug("existingFileDetailsVO.getMappingId()"+existingFileDetailsVO.getMappingId());
                                    logger.debug("fileItem.mappingId()"+mappingId);
                                    if(existingFileDetailsVO.getMappingId().equals(mappingId))
                                    {
                                        logger.debug("if existingFileDetailsVO.getMappingId().equals(mappingId)");
                                        fileDetailsVOMap.put(alternateName,existingFileDetailsVO);
                                    }
                                }

                                fileDetailsVO = replaceFileAppManagerUpload(fileItem, (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_DOCUMENT_PATH : APPLICATION_DOCUMENT_PATH) + applicationManagerVO.getMemberId(), (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_BACKUP_DOCUMENT_PATH : APPLICATION_BACKUP_DOCUMENT_PATH) + applicationManagerVO.getMemberId(), fileDetailsVOMap, fileHandlingUtil);

                            }
                            else
                            {
                                fileDetailsVO = fileHandlingUtil.uploadSingleFile(fileItem, (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_DOCUMENT_PATH : APPLICATION_DOCUMENT_PATH) + applicationManagerVO.getMemberId());
                            }
                        }
                        catch (Exception e)
                        {
                            logger.debug("error while uploading the file ::" + e);
                            fileDetailsVO = new AppFileDetailsVO();
                            fileDetailsVO.setFilename(fileItem.getName());
                            fileDetailsVO.setFieldName(fileItem.getFieldName());
                            fileDetailsVO.setFileType("");
                            fileDetailsVO.setFilePath("");
                            fileDetailsVO.setDtstamp("");
                            fileDetailsVO.setTimestamp("");
                            fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                        }
                    }

                    fileDetailsVO.setLabelId(fileItem.getFieldName().split("\\|")[1]);
                    fileDetailsVO.setFieldName(fileItem.getFieldName().split("\\|")[0]);

                    //ToDO add error log to display labelid and field name
                    //System.out.println(fileDetailsVO.getFilename()+"   ----fileDetailsVO.setLabelId.........." + fileItem.getFieldName());
                    logger.debug("fileDetailsVO.setLabelId.........." + fileItem.getFieldName().split("\\|")[1]);
                    logger.debug("fileDetailsVO.setFieldName........" + fileItem.getFieldName().split("\\|")[0]);
                    //fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                    mp.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                    if (AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                    {                        //alternateName  applicationVO
                        logger.debug("inside if...." + fileDetailsVO.isSuccess());
                        applicationManagerVO.setNotificationMessage(applicationManager.insertUploadDocument(fileDetailsVO, applicationManagerVO), " Kyc", " Uploaded Successful");
                        applicationManager.insertUploadDocumentHistory(fileDetailsVO, applicationManagerVO);

                    }
                    else if (AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                    {

                        logger.debug("inside else if " + fileDetailsVO.isSuccess());
                        applicationManagerVO.setNotificationMessage(applicationManager.updateUploadDocument(fileDetailsVO, applicationManagerVO), " Kyc", " Uploaded Successful");
                        applicationManager.insertUploadDocumentHistory(fileDetailsVO, applicationManagerVO);

                    }
                }
                logger.debug("BEFORE FILEITEM DELETE");
                fileItem.delete();
                logger.debug("AFTER FILEITEM DELETE");
            }
            /*}*/

        }
        finally
        {
            try
            {
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    this.closeFtpConnection(null,true,(FtpFileHandlingUtil)fileHandlingUtil);
            }
            catch (PZTechnicalViolationException e)
            {
                logger.debug("Technical exception while closing FTP connection"+e);
            }
        }
        fileDetailsVOHashMap = getMultipleFileVO(mp);
        return fileDetailsVOHashMap;
    }

    //new method for add single button file upload

    public Map<String,FileDetailsListVO> uploadMultipleFileForAppManagerNew(HttpServletRequest request,ApplicationManagerVO applicationManagerVO,boolean isAPI,FormDataMultiPart formDataMultiPart,String alternate_name) throws PZTechnicalViolationException
    {
        //manager instance
        ApplicationManager applicationManager = new ApplicationManager();

        Map<String,FileDetailsListVO> fileDetailsVOHashMap = new HashMap<String, FileDetailsListVO>();
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        List<FileItem> fileItemList=null;
        MultiHashMap mp = new MultiHashMap();
        AppFileHandlingUtil fileHandlingUtil=null;
        try
        {
            fileHandlingUtil = getFileHandlingUtilAccordingToTheProperty();
            if (isAPI)
            {
                fileItemList = this.fileHandlingUtil.getListOfFileItemForApplicationManager(formDataMultiPart, applicationManagerVO);
            }
            else
            {
                fileItemList = this.fileHandlingUtil.getListOfFileItem(request);
            }

            /*for (List<FileItem> FileItemlist : fileItemList)
            {*/

            for (FileItem fileItem : fileItemList)
            {
                AppFileDetailsVO fileDetailsVO = null;
                if (fileItem.getSize() > 0 && !fileItem.isFormField())
                {
                    String alternateName = fileItem.getFieldName().split("\\|")[0];
                    String labelID = fileItem.getFieldName().split("\\|")[1];

                    if (alternateName.equals(alternate_name))
                    {
                        logger.debug("Inside FileManager alternatename " + alternateName);
                        logger.debug("Inside FileManager labelID " + labelID);

                        if (applicationManagerVO.getUploadLabelVOs() != null && applicationManagerVO.getUploadLabelVOs().containsKey(labelID))
                        {
                            AppUploadLabelVO uploadLabelVO = applicationManagerVO.getUploadLabelVOs().get(labelID);

                            for (String fileType : uploadLabelVO.getSupportedFileType().split(","))
                            {
                                if ("PDF".equalsIgnoreCase(fileType))
                                    uploadFileTypeList.add(AppUploadFileType.PDF);
                                if ("XLSX".equalsIgnoreCase(fileType))
                                    uploadFileTypeList.add(AppUploadFileType.EXCEL);
                                if ("PNG".equalsIgnoreCase(fileType))
                                    uploadFileTypeList.add(AppUploadFileType.PNG);
                                if ("JPG".equalsIgnoreCase(fileType))
                                    uploadFileTypeList.add(AppUploadFileType.JPG);
                            }
                        }
                        fileDetailsVO = this.fileHandlingUtil.doValidationOnFileItem(fileItem, uploadFileTypeList);
                        if (fileDetailsVO == null)
                        {
                            logger.debug("Inside FileManager.fileDetailsVO" + fileDetailsVO);
                            try
                            {
                                if (fileItem.getFieldName().contains("|Replace"))
                                {
                                    logger.debug("inside replace condition");

                                    HashMap<String, AppFileDetailsVO> fileDetailsVOMap = new HashMap<String, AppFileDetailsVO>();

                                    List<AppFileDetailsVO> existingFilesVO = applicationManagerVO.getFileDetailsVOs().get(alternateName).getFiledetailsvo();
                                    String mappingId = fileItem.getFieldName().split("\\|")[3];
                                    logger.debug("existingFilesVO- Size" + existingFilesVO.size());
                                    for (AppFileDetailsVO existingFileDetailsVO : existingFilesVO)
                                    {
                                        logger.debug("existingFileDetailsVO.getMappingId()" + existingFileDetailsVO.getMappingId());
                                        logger.debug("fileItem.mappingId()" + mappingId);
                                        if (existingFileDetailsVO.getMappingId().equals(mappingId))
                                        {
                                            logger.debug("if existingFileDetailsVO.getMappingId().equals(mappingId)");
                                            fileDetailsVOMap.put(alternateName, existingFileDetailsVO);
                                        }
                                    }

                                    fileDetailsVO = replaceFileAppManagerUpload(fileItem, (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_DOCUMENT_PATH : APPLICATION_DOCUMENT_PATH) + applicationManagerVO.getMemberId(), (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_BACKUP_DOCUMENT_PATH : APPLICATION_BACKUP_DOCUMENT_PATH) + applicationManagerVO.getMemberId(), fileDetailsVOMap, fileHandlingUtil);

                                }
                                else
                                {
                                    fileDetailsVO = fileHandlingUtil.uploadSingleFile(fileItem, (isFtpEnabledForDocumentationServer() ? FTP_APPLICATION_DOCUMENT_PATH : APPLICATION_DOCUMENT_PATH) + applicationManagerVO.getMemberId());
                                }
                            }
                            catch (Exception e)
                            {
                                logger.debug("error while uploading the file ::" + e);
                                fileDetailsVO = new AppFileDetailsVO();
                                fileDetailsVO.setFilename(fileItem.getName());
                                fileDetailsVO.setFieldName(fileItem.getFieldName());
                                fileDetailsVO.setFileType("");
                                fileDetailsVO.setFilePath("");
                                fileDetailsVO.setDtstamp("");
                                fileDetailsVO.setTimestamp("");
                                fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                                fileDetailsVO.setSuccess(false);
                                fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                            }
                        }

                        fileDetailsVO.setLabelId(fileItem.getFieldName().split("\\|")[1]);
                        fileDetailsVO.setFieldName(fileItem.getFieldName().split("\\|")[0]);

                        logger.debug("fileDetailsVO.setLabelId.........." + fileItem.getFieldName().split("\\|")[1]);
                        logger.debug("fileDetailsVO.setFieldName........" + fileItem.getFieldName().split("\\|")[0]);
                        mp.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                        if (AppFileActionType.UPLOAD.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                        {
                            logger.debug("inside if...." + fileDetailsVO.isSuccess());
                            applicationManagerVO.setNotificationMessage(applicationManager.insertUploadDocument(fileDetailsVO, applicationManagerVO), " Kyc", " Uploaded Successful");
                            applicationManager.insertUploadDocumentHistory(fileDetailsVO, applicationManagerVO);

                        }
                        else if (AppFileActionType.REPLACE.equals(fileDetailsVO.getFileActionType()) && fileDetailsVO.isSuccess())
                        {

                            logger.debug("inside else if " + fileDetailsVO.isSuccess());
                            applicationManagerVO.setNotificationMessage(applicationManager.updateUploadDocument(fileDetailsVO, applicationManagerVO), " Kyc", " Uploaded Successful");
                            applicationManager.insertUploadDocumentHistory(fileDetailsVO, applicationManagerVO);

                        }
                    }
                    logger.debug("BEFORE FILEITEM DELETE");
                    fileItem.delete();
                    logger.debug("AFTER FILEITEM DELETE");
                }
            }
        }
        finally
        {
            try
            {
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    this.closeFtpConnection(null,true,(FtpFileHandlingUtil)fileHandlingUtil);
            }
            catch (PZTechnicalViolationException e)
            {
                logger.debug("Technical exception while closing FTP connection"+e);
            }
        }
        fileDetailsVOHashMap = getMultipleFileVO(mp);
        return fileDetailsVOHashMap;
    }
    public Map<String,FileDetailsListVO> getMultipleFileVO(MultiHashMap mp){                                  //Add for multiple KYC
        HashMap<String,FileDetailsListVO> fileDetailsVOHashMap=new HashMap<String, FileDetailsListVO>();
        Set set = mp.entrySet();
        Iterator i = set.iterator();
        List<AppFileDetailsVO> list = null;
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            list=(List<AppFileDetailsVO>)mp.get(me.getKey());
            FileDetailsListVO fileDetailsListVO = new FileDetailsListVO();
            fileDetailsListVO.setFiledetailsvo(list);
            fileDetailsVOHashMap.put(me.getKey().toString(),fileDetailsListVO);

            for(int j=0;j<list.size();j++)
            {   AppFileDetailsVO fd = list.get(j);
                //System.out.println(":Key :"+me.getKey()+": value :"+fd.getFilename());
            }
        }
        return fileDetailsVOHashMap;
    }

    public boolean isFtpEnabledForDocumentationServer()
    {
        //ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(STARTFTP))
        {
            return true;
        }

        return false;
    }

    public boolean deleteFileAccordingToFTPProperty(File file,String ftpLocation,FtpFileHandlingUtil ftpFileHandlingUtil)
    {
        if(isFtpEnabledForDocumentationServer() && file!=null)
        {
            if(functions.isValueNull(ftpLocation))
            {
                return ftpFileHandlingUtil.ftpFileAndDeleteFile(file,ftpLocation);
            }
            else
            {
                return fileHandlingUtil.deleteFile(file);
            }
        }
        return true;
    }

    public boolean deleteFileAccordingToFTPProperty(List<File> files)
    {
        Set<Boolean> delete= new HashSet<Boolean>() ;
        if (isFtpEnabledForDocumentationServer())
        {
            for (File file : files)
            {
                if(file.exists())
                    delete.add(fileHandlingUtil.deleteFile(file));
            }
            return !delete.contains(false);
        }
        return true;
    }

    public ZipOutputStream getZipOutputStream(String fileName) throws FileNotFoundException
    {
        return fileHandlingUtil.getZIPOutputStream(fileName);
    }

    public void addFileToZip(File file,ZipOutputStream zipOutputStream) throws IOException
    {
        fileHandlingUtil.addToZipFile(file,zipOutputStream);
    }

    public boolean makeDirectory(String directoryPath,boolean isApplicationManager,AppFileHandlingUtil fileHandlingUtil)
    {
        boolean directoryMade=false;
        if(isApplicationManager)
        {
            directoryMade=fileHandlingUtil.makeDirectory(directoryPath);
        }
        else
        {
            directoryMade=this.fileHandlingUtil.makeDirectory(directoryPath);
        }

        return directoryMade;
    }


    public boolean deleteFileFromFTPServer(File file,FtpFileHandlingUtil ftpFileHandlingUtil)
    {
        logger.debug("inside method....");
        //fileHandlingUtil.deleteFile(file);
        if(isFtpEnabledForDocumentationServer() && file!=null)
        {
            //System.out.println("isFtpEnabledForDocumentationServer......");
            if(ftpFileHandlingUtil.fileExist(file))
            {
                return ftpFileHandlingUtil.deleteFile(file);
            }

        }
        return false;
    }

    //replace template emptyAcroFieds with value
    public AppFileDetailsVO fillAcroFieldOfBankTemplate(ApplicationManagerVO applicationManagerVO, BankTypeVO bankTypeVO,Map<String,List<BankApplicationMasterVO>> bankApplicationMasterMap,AppFileHandlingUtil fileHandlingUtil) throws PZTechnicalViolationException
    {
        boolean mkDirSuccess=false;
        boolean delete=false;
        PdfReader pdfReader=null;
        PdfStamper pdfStamper=null;
        Calendar currentDate= Calendar.getInstance();
        SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        AcroFields acroFields = null;

        MerchantBankAppSwitch merchantBankAppSwitch = new MerchantBankAppSwitch();

        ApplicationManager applicationManager = new ApplicationManager();

        BankApplicationMasterVO bankApplicationMasterVO=null;

        List<BankApplicationMasterVO> bankApplicationMasterVOList=null;

        File file=null;

        AppFileDetailsVO fileDetailsVO=null;
        try
        {
            fileDetailsVO=new AppFileDetailsVO();

            fileDetailsVO.setFieldName(bankTypeVO.getBankId());
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(AppFileActionType.GENERATE);
            fileDetailsVO.setFilename(removeFileExtension(bankTypeVO.getFileName()) + "(" + fileDetailsVO.getDtstamp() + ")." + getFileExtension(bankTypeVO.getFileName()));

            mkDirSuccess=this.fileHandlingUtil.makeDirectory((isFtpEnabledForDocumentationServer()?TEMP_BANK_APPLICATION_PATH: BANK_APPLICATION_PATH)+applicationManagerVO.getMemberId()+"/");
            if(mkDirSuccess)
            {
                pdfReader=this.fileHandlingUtil.getPdfReader(GATEWAY_TEMPLATE_PATH /*+bankTypeVO.getBankId()+"_"+bankTypeVO.getBankName()+"/"*/+bankTypeVO.getFileName());
                pdfStamper=fileHandlingUtil.getPdfStamper(pdfReader, (isFtpEnabledForDocumentationServer()?TEMP_BANK_APPLICATION_PATH: BANK_APPLICATION_PATH) + applicationManagerVO.getMemberId() + "/" + fileDetailsVO.getFilename());
                acroFields=pdfStamper.getAcroFields();
                Set<String> fieldsName=acroFields.getFields().keySet();
                for(String singleField:fieldsName)
                {
                    acroFields.setFieldProperty(singleField, "setfflags", PdfFormField.FF_READ_ONLY, null);
                    System.out.println(singleField + " " + acroFields + " " + applicationManagerVO + " "+ bankTypeVO);
                    merchantBankAppSwitch.fillAcroFieldsForBankApplication(singleField,acroFields,applicationManagerVO,bankTypeVO);
                }
                fileDetailsVO.setSuccess(true);
            }
            else
            {
                fileDetailsVO.setSuccess(false);
            }
            if(fileDetailsVO.isSuccess())
            {
                //applicationManager.insertbankApplicationMaster(applicationManagerVO,fileDetailsVO);
                bankApplicationMasterVO= new BankApplicationMasterVO();
                bankApplicationMasterVO.setMember_id(applicationManagerVO.getMemberId());
                bankApplicationMasterVO.setPgtypeid(bankTypeVO.getBankId());
                //select Record
                if (!bankApplicationMasterMap.containsKey(bankTypeVO.getBankId()) || bankApplicationMasterMap.get(bankTypeVO.getBankId()).size() <= 2)
                {
                    //INSERT RECORD
                    applicationManager.insertbankApplicationMaster(applicationManagerVO, fileDetailsVO);
                }
                else
                {
                    bankApplicationMasterVO.setStatus(BankApplicationStatus.GENERATED.name());
                    bankApplicationMasterVO.setBankfilename(fileDetailsVO.getFilename());
                    bankApplicationMasterVO.setDtstamp(fileDetailsVO.getDtstamp());

                    file = new File((isFtpEnabledForDocumentationServer()?FTP_BANK_APPLICATION_PATH: BANK_APPLICATION_PATH)+bankApplicationMasterMap.get(bankTypeVO.getBankId()).get(2).getMember_id()+"/"+bankApplicationMasterMap.get(bankTypeVO.getBankId()).get(2).getBankfilename());
                    logger.debug("file for delete "+file);
                    if(fileHandlingUtil.fileExist(file))
                        delete=fileHandlingUtil.deleteFile(file);

                    applicationManager.updateBankApplicationMasterVO(bankApplicationMasterVO, bankApplicationMasterMap.get(bankTypeVO.getBankId()).get(2).getBankapplicationid(), bankApplicationMasterVO.getMember_id());
                    logger.debug("updateddddd=======");
                    //UPDATE TABLE
                }
                logger.debug("bankApplicationMasterMap");

            }
        }
        catch (DocumentException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileManager.class.getName(), "fillAcroFieldOfBankTemplate()", null, "common", "technical Exception", PZTechnicalExceptionEnum.DOCUMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileManager.class.getName(),"fillAcroFieldOfBankTemplate()",null,"common","technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally {
            if(pdfReader!=null)
            {
                pdfReader.close();
            }
            if(pdfStamper!=null)
            {
                try
                {
                    pdfStamper.close();
                }
                catch (DocumentException e)
                {
                    logger.debug("pdfStamper not closed properly "+e);
                }
                catch (IOException e)
                {
                    logger.error("IOException---->", e);  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

        //This Is for transferring the file After AcroFields is been field.
        if(fileDetailsVO.isSuccess())
        {
            if(isFtpEnabledForDocumentationServer())
            {
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                {
                    File transferFile= new File(TEMP_BANK_APPLICATION_PATH + applicationManagerVO.getMemberId() + "/" + fileDetailsVO.getFilename());
                    deleteFileAccordingToFTPProperty(transferFile,FTP_BANK_APPLICATION_PATH + applicationManagerVO.getMemberId() + "/" + fileDetailsVO.getFilename(),(FtpFileHandlingUtil) fileHandlingUtil);
                }
            }
        }
        return fileDetailsVO;
    }



    public File getConsolidatedZipFile(ConsolidatedApplicationVO consolidatedApplicationVO,AppFileHandlingUtil fileHandlingUtil) throws FileNotFoundException
    {
        File file= null;
        if(consolidatedApplicationVO!=null && consolidatedApplicationVO.getMemberid()!=null)
        {
            if(isFtpEnabledForDocumentationServer())
            {
                file = ((FtpFileHandlingUtil)fileHandlingUtil).downloadFileFromPathTo(FTP_APPLICATION_ZIP_PATH + consolidatedApplicationVO.getMemberid() + "/" + consolidatedApplicationVO.getFilename(), TEMP_APPLICATION_ZIP_PATH + consolidatedApplicationVO.getMemberid() + "/" + consolidatedApplicationVO.getFilename());
            }
            else
            {
                logger.debug("file Path::" + APPLICATION_ZIP_PATH + consolidatedApplicationVO.getMemberid() + "/" + consolidatedApplicationVO.getFilename());
                file = new File(APPLICATION_ZIP_PATH + consolidatedApplicationVO.getMemberid() + "/" + consolidatedApplicationVO.getFilename());
            }
        }

        return file;
    }

    public File getKYCDetails(AppFileDetailsVO fileDetailsVO,AppFileHandlingUtil fileHandlingUtil)
    {
        File file= null;
        if(fileDetailsVO!=null && fileDetailsVO.getMemberid()!=null)
        {
            if(isFtpEnabledForDocumentationServer())
            {
                file = ((FtpFileHandlingUtil)fileHandlingUtil).downloadFileFromPathTo(FTP_APPLICATION_DOCUMENT_PATH + fileDetailsVO.getMemberid() + "/" + fileDetailsVO.getFilename(), TEMP_APPLICATION_DOCUMENT_PATH + fileDetailsVO.getMemberid() + "/" + fileDetailsVO.getFilename());
            }
            else
            {
                logger.debug("file Path for KYC::" + APPLICATION_DOCUMENT_PATH + fileDetailsVO.getMemberid() + "/" + fileDetailsVO.getFilename());
                file = new File(APPLICATION_DOCUMENT_PATH + fileDetailsVO.getMemberid() + "/" + fileDetailsVO.getFilename());
            }
        }

        return file;

    }

    public File getBankGenerateTemplate(BankApplicationMasterVO bankApplicationMasterVO,AppFileHandlingUtil fileHandlingUtil) throws FileNotFoundException
    {
        File file= null;
        if(bankApplicationMasterVO!=null && bankApplicationMasterVO.getMember_id()!=null)
        {
            if(isFtpEnabledForDocumentationServer())
            {
                file = ((FtpFileHandlingUtil)fileHandlingUtil).downloadFileFromPathTo(FTP_BANK_APPLICATION_PATH + bankApplicationMasterVO.getMember_id() + "/" + bankApplicationMasterVO.getBankfilename(), TEMP_BANK_APPLICATION_PATH + bankApplicationMasterVO.getMember_id() + "/" + bankApplicationMasterVO.getBankfilename());
            }
            else
            {
                logger.debug("file Path::" + BANK_APPLICATION_PATH + bankApplicationMasterVO.getMember_id() + "/" + bankApplicationMasterVO.getBankfilename());
                file = new File(BANK_APPLICATION_PATH + bankApplicationMasterVO.getMember_id() + "/" + bankApplicationMasterVO.getBankfilename());
            }
        }

        return file;
    }

    //check whether the file exist in the system
    private AppFileDetailsVO replaceFileAppManagerUpload(FileItem fileItem,String originalFilePath,String replaceFilePath,HashMap<String,AppFileDetailsVO> fileDetailsVOHashMap,AppFileHandlingUtil fileHandlingUtil) throws Exception
    {
        //date instance
        Calendar currentDate= Calendar.getInstance();
        SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        AppFileDetailsVO fileDetailsVO=new AppFileDetailsVO();
        File replaceDirectory = new File(replaceFilePath+"/");

        File replaceFile= new File(originalFilePath+"/"+fileItem.getName());
        File originalFile=null;
        File moveFile=null;
        if(fileHandlingUtil.fileExist(replaceFile))//Replaced the code replaceFile.exists() to the condition for DOCUMENTATION SERVER
        {
            if(fileDetailsVOHashMap!=null && fileDetailsVOHashMap.containsKey(fileItem.getFieldName().split("\\|")[0]) && fileItem.getName().equals(fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getFilename()))
            {
                fileHandlingUtil.makeDirectory(replaceFilePath+"/");

                //Replaced the code !replaceDirectory.exists() to the condition for DOCUMENTATION SERVER
                /*if(!replaceDirectory.exists())
                {
                    replaceDirectory.mkdir();
                }*/

                moveFile=new File(replaceFilePath+"/"+removeFileExtension(replaceFile.getName())+"_"+currentDate.getTimeInMillis()+"."+getFileExtension(replaceFile.getName()));
                replaceFile=fileHandlingUtil.renameFile(replaceFile, moveFile);
                fileDetailsVO=fileHandlingUtil.uploadSingleFile(fileItem, originalFilePath);
                fileDetailsVO.setMovedFileName(moveFile.getName());
                fileDetailsVO.setFileActionType(AppFileActionType.REPLACE);
            }
            else
            {
                fileDetailsVO.setFilename(fileItem.getName());
                fileDetailsVO.setFileType("");
                fileDetailsVO.setFilePath(originalFilePath  + "/" + fileItem.getName());
                fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                fileDetailsVO.setFileActionType(AppFileActionType.REPLACE);
                fileDetailsVO.setReasonOfFailure("File Exist in the system");
                fileDetailsVO.setSuccess(false);
            }

        }
        else
        {
            fileHandlingUtil.makeDirectory(replaceFilePath+"/");
            //Replaced the code !replaceDirectory.exists() to the condition for DOCUMENTATION SERVER
            /*if(!replaceDirectory.exists())
            {
                replaceDirectory.mkdir();
            }*/
            moveFile=new File(replaceFilePath+"/"+removeFileExtension(fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getFilename())+"_"+currentDate.getTimeInMillis()+"."+getFileExtension(fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getFilename()));
            originalFile=new File(originalFilePath+"/"+fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getFilename());
            originalFile=fileHandlingUtil.renameFile(originalFile, moveFile);
            fileDetailsVO=fileHandlingUtil.uploadSingleFile(fileItem, originalFilePath);
            fileDetailsVO.setMovedFileName(moveFile.getName());
            fileDetailsVO.setFileActionType(AppFileActionType.REPLACE);
        }
        fileDetailsVO.setMappingId(fileDetailsVOHashMap!=null && fileDetailsVOHashMap.containsKey(fileItem.getFieldName().split("\\|")[0])?fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getMappingId():"");

        return fileDetailsVO;
    }


    /*public Map<String,AppFileDetailsVO> addBankTemplate(HttpServletRequest request) throws PZTechnicalViolationException, PZDBViolationException, SystemError
    {
        PdfReader pdfReader=null;
        Map<String,AppFileDetailsVO> fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        ApplicationManager applicationManager = new ApplicationManager();
        List<String> bankName = new ArrayList<String>();
        List<String> fileName = new ArrayList<String>();
        String fieldName = "";

        List<FileItem> fileItemList = fileHandlingUtil.getListOfFileItem(request);
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        //filling up element
        uploadFileTypeList.add(AppUploadFileType.PDF);

        for(FileItem fileItem : fileItemList)
        {
            AppFileDetailsVO fileDetailsVO = null;
            if(fileItem.getSize()>0 && !fileItem.isFormField())
            {
                logger.debug("file name::::::"+fileItem.getFieldName()+" value::::"+fileItem.getName());
                fileDetailsVO = fileHandlingUtil.doValidationOnFileItem(fileItem,uploadFileTypeList);

                if(fileDetailsVO == null)
                {
                    pdfReader = fileHandlingUtil.getPdfReader(System.getProperty("catalina.base").replace("\\", "/") + "/temp/" + ((DiskFileItem) fileItem).getStoreLocation().getName());
                    fileDetailsVO = checkAcroFiledsOfFileWithBankEnum(pdfReader);
                    if(fileDetailsVO == null)
                    {
                        try{
                            logger.debug("before----");
                            fileDetailsVO = fileHandlingUtil.uploadGatewayFile(fileItem, GATEWAY_TEMPLATE_PATH);
                            fileName.add(fileItem.getName());
                            logger.debug("after-----");
                            if(!fileDetailsVO.isSuccess()){
                                fileDetailsVOHashMap.put(fieldName,fileDetailsVO);
                                return fileDetailsVOHashMap;
                            }

                            String a [] = fileItem.getFieldName().split("_");
                            bankName.add(a[0]);
                            fieldName = a[1];
                        }
                        catch(Exception e)
                        {
                            logger.debug("error while uploading the file ::"+e);
                            fileDetailsVO=new AppFileDetailsVO();
                            fileDetailsVO.setFilename(fileItem.getName());
                            fileDetailsVO.setFieldName(fileItem.getFieldName());
                            fileDetailsVO.setFileType("");
                            fileDetailsVO.setFilePath("");
                            fileDetailsVO.setDtstamp("");
                            fileDetailsVO.setTimestamp("");
                            fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                        }
                    }
                }
                fileDetailsVO.setLabelId(fieldName);
                fileDetailsVO.setFieldName(fieldName);
                fileDetailsVOHashMap.put(fieldName,fileDetailsVO);
            }
            else if(bankName.size()<=0 && "bankname".equals(fileItem.getFieldName()))
            {
                logger.debug("Bank name---->"+fileItem.getString());
//                bankName = fileItem.getString();
                bankName.add(fileItem.getString());
            }
            fileItem.delete();
            if(pdfReader!=null)
            {
                pdfReader.close();
            }
        }

        if(!ESAPI.validator().isValidInput("bankname", bankName.get(0), "alphanum", 255, false))
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setFieldName("");
            fileDetailsVO.setReasonOfFailure("Invalid Bank Name");

            File originalFile = null;
            originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
            fileHandlingUtil.deleteFile(originalFile);

            fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
        }
        else if (!applicationManager.isBankExist(bankName.get(0)))
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            logger.debug("Bank not exist::" + bankName);
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            if (applicationManager.addBankTemplate(bankName.get(0), fileName.get(0)))
            {
                fileDetailsVO.setSuccess(true);
                fileDetailsVO.setFieldName(fieldName);
                fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }
            else
            {
                fileDetailsVO.setSuccess(false);
                fileDetailsVO.setFieldName("");
                fileDetailsVO.setReasonOfFailure("File cannot be added please try after sometime");

                File originalFile = null;
                originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
                fileHandlingUtil.deleteFile(originalFile);

                fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }
        }
        else
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            logger.debug("Bank exist::" + bankName);
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            File originalFile = null;
            originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
            fileHandlingUtil.deleteFile(originalFile);

            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setFieldName("");
            fileDetailsVO.setReasonOfFailure("Bank already exist");
            fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
        }
        return fileDetailsVOHashMap;
    }*/

    public Map<String,AppFileDetailsVO> addBankTemplate(HttpServletRequest request) throws PZTechnicalViolationException, PZDBViolationException, SystemError
    {
        PdfReader pdfReader=null;
        Map<String,AppFileDetailsVO> fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        ApplicationManager applicationManager = new ApplicationManager();
        List<String> bankName = new ArrayList<String>();
        List<String> fileName = new ArrayList<String>();
        String fieldName = "";

        List<FileItem> fileItemList = fileHandlingUtil.getListOfFileItem(request);
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        //filling up element
        uploadFileTypeList.add(AppUploadFileType.PDF);

        for(FileItem fileItem : fileItemList)
        {
            AppFileDetailsVO fileDetailsVO = null;
            if(fileItem.getSize()>0 && !fileItem.isFormField())
            {
                logger.debug("file name::::::"+fileItem.getFieldName()+" value::::"+fileItem.getName());
                fileDetailsVO = fileHandlingUtil.doValidationOnFileItem(fileItem,uploadFileTypeList);

                if(fileDetailsVO == null)
                {
                    pdfReader = fileHandlingUtil.getPdfReader(System.getProperty("catalina.base").replace("\\","/")+"/temp/"+((DiskFileItem)fileItem).getStoreLocation().getName());
                    fileDetailsVO = checkAcroFiledsOfFileWithBankEnum(pdfReader);
                    if(fileDetailsVO == null)
                    {
                        try{
                            logger.debug("before----");
                            fileDetailsVO = fileHandlingUtil.uploadGatewayFile(fileItem, GATEWAY_TEMPLATE_PATH);
                            fileName.add(fileItem.getName());
                            logger.debug("after-----");
                            if(!fileDetailsVO.isSuccess()){
                                fileDetailsVOHashMap.put(fieldName,fileDetailsVO);
                                return fileDetailsVOHashMap;
                            }

                            String a [] = fileItem.getFieldName().split("_");
                            bankName.add(a[0]);
                            fieldName = a[1];
                        }
                        catch(Exception e)
                        {
                            logger.debug("error while uploading the file ::"+e);
                            fileDetailsVO=new AppFileDetailsVO();
                            fileDetailsVO.setFilename(fileItem.getName());
                            fileDetailsVO.setFieldName(fileItem.getFieldName());
                            fileDetailsVO.setFileType("");
                            fileDetailsVO.setFilePath("");
                            fileDetailsVO.setDtstamp("");
                            fileDetailsVO.setTimestamp("");
                            fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                        }
                    }
                }
                fileDetailsVO.setLabelId(fieldName);
                fileDetailsVO.setFieldName(fieldName);
                fileDetailsVOHashMap.put(fieldName,fileDetailsVO);
            }
            else if(bankName.size()<=0 && "bankname".equals(fileItem.getFieldName()))
            {
                logger.debug("Bank name---->"+fileItem.getString());
//                bankName = fileItem.getString();
                bankName.add(fileItem.getString());
            }
            fileItem.delete();
            if(pdfReader!=null)
            {
                pdfReader.close();
            }
        }

        if(!ESAPI.validator().isValidInput("bankname", bankName.get(0), "alphanum", 255, false) || functions.hasHTMLTags(bankName.get(0)))
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setFieldName("");
            fileDetailsVO.setReasonOfFailure("Invalid Bank Name");

            File originalFile = null;
            originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
            fileHandlingUtil.deleteFile(originalFile);

            fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
        }
        else if (!applicationManager.isBankExist(bankName.get(0)))
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            logger.debug("Bank not exist::" + bankName);
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            if (applicationManager.addBankTemplate(bankName.get(0), fileName.get(0)))
            {
                fileDetailsVO.setSuccess(true);
                fileDetailsVO.setFieldName(fieldName);
                fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }
            else
            {
                fileDetailsVO.setSuccess(false);
                fileDetailsVO.setFieldName("");
                fileDetailsVO.setReasonOfFailure("File cannot be added please try after sometime");

                File originalFile = null;
                originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
                fileHandlingUtil.deleteFile(originalFile);

                fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
            }
        }
        else
        {
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            logger.debug("Bank exist::" + bankName);
            AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

            File originalFile = null;
            originalFile = new File(GATEWAY_TEMPLATE_PATH + "/" + fileName.get(0));
            fileHandlingUtil.deleteFile(originalFile);

            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setFieldName("");
            fileDetailsVO.setReasonOfFailure("Bank already exist");
            fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
        }
        return fileDetailsVOHashMap;
    }

    public Map<String,AppFileDetailsVO> uploadMultipleBankTemplate(HttpServletRequest request) throws PZTechnicalViolationException, PZDBViolationException, SystemError
    {
        PdfReader pdfReader = null;
        Map<String,AppFileDetailsVO> fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
        ApplicationManager applicationManager = new ApplicationManager();
        List<String> bankName = new ArrayList<String>();
        List<String> fileName = new ArrayList<String>();
        String fieldName = "";
        String strBankName = "";
        int s = 0;

        List<FileItem> fileItemList = fileHandlingUtil.getListOfFileItem(request);
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        //filling up element
        uploadFileTypeList.add(AppUploadFileType.PDF);

        for(FileItem fileItem : fileItemList)
        {
            AppFileDetailsVO fileDetailsVO = null;
            if(fileItem.getSize()>0 && !fileItem.isFormField())
            {
                logger.debug("file name::::::"+fileItem.getFieldName()+" value::::"+fileItem.getName());
                fileDetailsVO = fileHandlingUtil.doValidationOnFileItem(fileItem,uploadFileTypeList);
                logger.debug("After File Validation---");

                if(fileDetailsVO == null)
                {
                    pdfReader = fileHandlingUtil.getPdfReader(System.getProperty("catalina.base").replace("\\","/")+"/temp/"+((DiskFileItem)fileItem).getStoreLocation().getName());
                    fileDetailsVO = checkAcroFiledsOfFileWithBankEnum(pdfReader);
                    String arrBankName [] = fileItem.getFieldName().split("_");
                    strBankName = arrBankName[0];
                    if(fileDetailsVO == null)
                    {
                        try
                        {
                            logger.debug("inside replace condition");
                            fileDetailsVO = deleteOldBankTemplateAndUploadNew(fileItem, GATEWAY_TEMPLATE_PATH);
                            fileName.add(fileItem.getName());

                            bankName.add(arrBankName[0]);
                            fieldName = arrBankName[1];
                        }
                        catch(Exception e)
                        {
                            logger.debug("error while uploading the file ::"+e);
                            fileDetailsVO = new AppFileDetailsVO();
                            fileDetailsVO.setFilename(fileItem.getName());
                            fileDetailsVO.setFieldName(strBankName);
                            fileDetailsVO.setFileType("");
                            fileDetailsVO.setFilePath("");
                            fileDetailsVO.setDtstamp("");
                            fileDetailsVO.setTimestamp("");
                            fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                        }
                    }
                    else{
                        fieldName = arrBankName[1];
                        fileDetailsVO.setFilename(fileItem.getName());
                        fileDetailsVO.setFieldName(strBankName);
                        fileDetailsVO.setFileType("");
                        fileDetailsVO.setFilePath("");
                        fileDetailsVO.setDtstamp("");
                        fileDetailsVO.setTimestamp("");
                        fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                        fileDetailsVO.setSuccess(false);
                    }
                }
                fileDetailsVO.setLabelId(fieldName);
                fileDetailsVO.setFieldName(strBankName);
                fileDetailsVOHashMap.put(fieldName,fileDetailsVO);
            }

            fileItem.delete();
            if(pdfReader!=null)
            {
                pdfReader.close();
            }
        }

        int i = 0;
        if (fileName.size()>0)
        {
            logger.debug("In replace else condition");
            fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();
            for (String bName : bankName)
            {
                AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();

                if (applicationManager.updateBankTemplate(bankName.get(i), fileName.get(i)))
                {
                    fileDetailsVO.setSuccess(true);
                    fileDetailsVO.setFieldName(bankName.get(i));
                    fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                }
                else
                {
                    fileDetailsVO.setSuccess(false);
                    fileDetailsVO.setFieldName(bankName.get(i));
                    fileDetailsVO.setReasonOfFailure("File cannot be upload please try after sometime");
                    fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                }
                i++;
            }
        }
        return fileDetailsVOHashMap;
    }

    //upload Template for Bank
    public Map<String,AppFileDetailsVO> uploadMultipleTemplateForBank(HttpServletRequest request,StringBuffer queryString) throws PZTechnicalViolationException, PZDBViolationException
    {
        PdfReader pdfReader=null;
        //manager instance
        GatewayManager gatewayManager = new GatewayManager();

        Map<String,AppFileDetailsVO> fileDetailsVOHashMap = new HashMap<String, AppFileDetailsVO>();

        List<FileItem> fileItemList=fileHandlingUtil.getListOfFileItem(request);
        List<AppUploadFileType> uploadFileTypeList = new ArrayList<AppUploadFileType>();
        //filling up element
        uploadFileTypeList.add(AppUploadFileType.PDF);

        for(FileItem fileItem:fileItemList)
        {

            AppFileDetailsVO fileDetailsVO = null;
            if(fileItem.getSize()>0 && !fileItem.isFormField())
            {
                logger.debug("file name::::::"+fileItem.getFieldName()+" value::::"+fileItem.getName());
                fileDetailsVO=fileHandlingUtil.doValidationOnFileItem(fileItem,uploadFileTypeList);
                if(fileDetailsVO==null)
                {
                    pdfReader=fileHandlingUtil.getPdfReader(System.getProperty("catalina.base").replace("\\","/")+"/temp/"+((DiskFileItem)fileItem).getStoreLocation().getName());
                    fileDetailsVO=checkAcroFiledsOfFileWithBankEnum(pdfReader);
                    if(fileDetailsVO==null)
                    {
                        try{
                            if(fileItem.getFieldName().contains("|Replace"))
                            {
                                logger.debug("inside replace condition");
                                fileDetailsVO=deleteOldBankTemplateAndUploadNew(fileItem, GATEWAY_TEMPLATE_PATH + fileItem.getFieldName().split("\\|")[0]);

                            }
                            else
                            {
                                fileDetailsVO=fileHandlingUtil.uploadSingleFile(fileItem,GATEWAY_TEMPLATE_PATH+fileItem.getFieldName().split("\\|")[0]);
                            }
                        }catch(Exception e)
                        {
                            logger.debug("error while uploading the file ::"+e);
                            fileDetailsVO=new AppFileDetailsVO();
                            fileDetailsVO.setFilename(fileItem.getName());
                            fileDetailsVO.setFieldName(fileItem.getFieldName());
                            fileDetailsVO.setFileType("");
                            fileDetailsVO.setFilePath("");
                            fileDetailsVO.setDtstamp("");
                            fileDetailsVO.setTimestamp("");
                            fileDetailsVO.setFileActionType(AppFileActionType.EXCEPTION);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("upload after some time,internal issue");
                        }
                    }
                }
                fileDetailsVO.setLabelId(fileItem.getFieldName().split("\\|")[1]);
                fileDetailsVO.setFieldName(fileItem.getFieldName().split("\\|")[0].split("_")[0]);
                fileDetailsVOHashMap.put(fileDetailsVO.getFieldName(),fileDetailsVO);
                if(fileDetailsVO.isSuccess())
                {
                    //gatewayManager.updateTemplateNameOfGatewayType(fileDetailsVO);
                }
            }
            else if(fileItem.isFormField())
            {
                String value = fileHandlingUtil.getEncodedFieldValueWhileUpload(fileItem);

                if(!functions.isValueNull(value))
                    value="";
                queryString.append("&"+fileItem.getFieldName()+"="+value);
            }
            fileItem.delete();
            if(pdfReader!=null)
            {
                pdfReader.close();
            }
        }

        return fileDetailsVOHashMap;
    }

    //delete old File and upload with new File
    private AppFileDetailsVO deleteOldBankTemplateAndUploadNew(FileItem fileItem,String originalFilePath) throws Exception
    {
        AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();
        File originalFile = null;

        originalFile = new File(originalFilePath+"/" +fileItem.getFieldName().split("\\|")[1]);
        logger.debug("originalFile--->"+originalFile);
        fileHandlingUtil.deleteFile(originalFile);
        fileDetailsVO=fileHandlingUtil.uploadGatewayFile(fileItem, originalFilePath);
        logger.debug("uploaded successfully::");
        fileDetailsVO.setFileActionType(AppFileActionType.DELETE);

        return fileDetailsVO;
    }




    private  AppFileDetailsVO  checkAcroFiledsOfFileWithBankEnum(PdfReader pdfReader)
    {
        AppFileDetailsVO fileDetailsVO =null;
        StringBuffer acroFieldsMessage= new StringBuffer();

        AcroFields acroFields=pdfReader.getAcroFields();
        Set<String> fieldsName=acroFields.getFields().keySet();
        for(String fileds:fieldsName)
        {
            if(!DefinedAcroFields.isInEnum(fileds))
            {
                if(acroFieldsMessage.length()>0)
                {
                    acroFieldsMessage.append(" , ");
                }
                acroFieldsMessage.append(fileds);
            }
        }
        if(acroFieldsMessage.length()>0)
        {
            fileDetailsVO=new AppFileDetailsVO();
            fileDetailsVO.setFilename("");
            fileDetailsVO.setFileType(getFileExtension(""));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp("");
            fileDetailsVO.setFileActionType(AppFileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("Fields not allowed are " + acroFieldsMessage );
        }


        return fileDetailsVO;
    }

    public String getFileExtension(String filename)
    {
        String extension=null;
        if(filename!=null)
        {
            int ext=filename.trim().lastIndexOf(".");
            extension=filename.substring(ext+1);
        }
        return extension;
    }

    public String removeFileExtension(String filename)
    {
        String fileWithoutExtension=null;
        if(filename!=null)
        {
            int ext=filename.trim().lastIndexOf(".");
            fileWithoutExtension=filename.substring(0,ext);
        }
        return fileWithoutExtension;
    }

    public File getBankAppTemplate(BankTypeVO bankTypeVO) throws FileNotFoundException
    {
        File file= null;
        if(bankTypeVO!=null && functions.isValueNull(bankTypeVO.getFileName()) &&  bankTypeVO.getFileName().contains(".pdf"))
        {
            logger.debug("file Path::" + GATEWAY_TEMPLATE_PATH /*+ bankTypeVO.getBankId() + "_" + bankTypeVO.getBankName() + "/"*/ + bankTypeVO.getFileName());
            file = new File(GATEWAY_TEMPLATE_PATH /*+ bankTypeVO.getBankId() + "_" + bankTypeVO.getBankName() + "/"*/ + bankTypeVO.getFileName());
        }
        return file;
    }

    //getting the list of file label -- added partnerid --suraj
    public Map<String,AppUploadLabelVO> getListOfUploadLabel(String functionalUsage,String partnerid)
    {
        return fileExtractionDao.getListOfUploadLabel(functionalUsage,partnerid);
    }

    public void getMandatoryAndOptionalAcroFieldsAccordingToTemplate(PdfReader pdfReader,Map<Boolean,Set<DefinedAcroFields>> acroFieldsListValidation)
    {

        Set<DefinedAcroFields> definedAcroFields=null;

        AcroFields acroFields=pdfReader.getAcroFields();
        Set<String> fieldsName=acroFields.getFields().keySet();
        for(String singleField:fieldsName)
        {
            PdfDictionary dict;

            AcroFields.Item item=acroFields.getFieldItem(singleField);

            for(Iterator i=item.merged.iterator();i.hasNext();)
            {
                dict= (PdfDictionary) i.next();
                boolean isOptional=true;

                logger.debug(" Acrofields:::"+singleField);

                if(dict.get(PdfName.FF)!=null)
                {
                    isOptional=false;
                }
                else
                {
                    isOptional=true;
                }

                if(DefinedAcroFields.isInEnum(singleField) && acroFieldsListValidation.containsKey(isOptional))
                {
                    definedAcroFields=acroFieldsListValidation.get(isOptional);
                    if(DefinedAcroFields.isInEnum(singleField))
                        definedAcroFields.add(DefinedAcroFields.getEnum(singleField));
                    logger.debug(" Present acroFields Boolean:::"+isOptional+" DefinedAcroFields:::"+definedAcroFields);
                }
                else if(DefinedAcroFields.isInEnum(singleField))
                {
                    definedAcroFields=new HashSet<DefinedAcroFields>();
                    if(DefinedAcroFields.isInEnum(singleField))
                        definedAcroFields.add(DefinedAcroFields.getEnum(singleField));
                    logger.debug(" 1st Defined AcroFields:::"+definedAcroFields);
                }

                if(definedAcroFields!=null)
                    acroFieldsListValidation.put(isOptional,definedAcroFields);
            }

        }
        logger.debug("Final Pdf  Acrofields::::"+acroFieldsListValidation);
    }
}
