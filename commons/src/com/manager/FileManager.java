package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import com.manager.dao.FileExtractionDao;
import com.manager.enums.*;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.*;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.manager.vo.morrisBarVOs.MorrisBarChartVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.fileupload.FileItem;
import org.eclipse.persistence.jaxb.MarshallerProperties;


import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/14/14
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileManager
{
    private static Logger logger = new Logger(FileManager.class.getName());
    //support class
    private static Functions functions = new Functions();
    //Dao for extracting the name of the file
    FileExtractionDao fileExtractionDao= new FileExtractionDao();
    //FileHandlingUtil instance
    FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
    //properties
    String pdfReportFilePath = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE_PATH");
    String xlsSettledFilePath=ApplicationProperties.getProperty("SETTLEMENT_FILE_PATH");
    String MERCHANT_XML_DATA_FILE_PATH=ApplicationProperties.getProperty("XML_DATA_FILE_PATH");
    String MERCHANT_JSON_DATA_FILE_PATH=ApplicationProperties.getProperty("JSON_DATA_FILE_PATH");
    String AGENT_PAYOUT_REPORT_FILE_PATH=ApplicationProperties.getProperty("AGENT_PAYOUT_REPORT_FILE_PATH");
    String PARTNER_PAYOUT_REPORT_FILE_PATH=ApplicationProperties.getProperty("PARTNER_PAYOUT_REPORT_FILE_PATH");


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



    public File getSettlementOrReportFile(String fileName) throws FileNotFoundException
    {
        File file= null;
        if(fileName.contains(".pdf"))
        {
            logger.debug("file Path::"+pdfReportFilePath+fileName);
            file=new File(pdfReportFilePath+fileName);
        }
        else if(fileName.contains(".xls"))
        {
            logger.debug("file Path::"+xlsSettledFilePath+fileName);
            file= new File(xlsSettledFilePath+fileName);
        }
        return file;
    }

    public void createStatusChartXML(HashMap<String,MorrisBarChartVO> statusViseBarChartContent,InputDateVO inputDateVO,TerminalVO terminalVO,TransactionReportVO transactionReportVO) throws PZTechnicalViolationException
    {
        String terminalId= Functions.checkStringNull(terminalVO.getTerminalId())==null?"_":"_"+terminalVO.getTerminalId()+"_";
        for(Map.Entry<String,MorrisBarChartVO> fileContentPair : statusViseBarChartContent.entrySet())
        {
            logger.debug("Status:::"+fileContentPair.getKey());
            File file =new File(MERCHANT_JSON_DATA_FILE_PATH+"/"+terminalVO.getMemberId()+terminalId+fileContentPair.getKey()+"_"+inputDateVO.getsMinTransactionDate().replaceAll("[-:\\s]", "")+"_"+inputDateVO.getsMaxTransactionDate().replaceAll("[-:\\s]", "")+".json");

            StringWriter stringWriter = new StringWriter();
            try
            {
                JAXBContext jaxbContext = JAXBContext.newInstance(MorrisBarChartVO.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

                // output pretty printed
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
                jaxbMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
                jaxbMarshaller.marshal(fileContentPair.getValue(), stringWriter);
            }
            catch (PropertyException e)
            {
                logger.error("PropertyException--->", e);
            }
            catch (JAXBException e)
            {
                logger.error("JAXBException--->", e);
            }

            StringBuilder fileContent= new StringBuilder(stringWriter.toString());

            transactionReportVO.setChartContent(fileContentPair.getKey(),fileContent);
            //fileHandlingUtil.createChartFile(file,fileContent);//This is for creating the file which is not imp
        }

    }
    //getting file for Agent
    public File getAgentReportFile(String fileName) throws FileNotFoundException
    {
        File file= null;
        if(fileName.contains(".pdf"))
        {
            logger.debug("file Path::"+AGENT_PAYOUT_REPORT_FILE_PATH+fileName);
            file=new File(AGENT_PAYOUT_REPORT_FILE_PATH+fileName);
        }

        return file;
    }
    public File getPartnerReportFile(String fileName) throws FileNotFoundException
    {
        File file= null;
        if(fileName.contains(".pdf"))
        {
            logger.debug("file Path::"+PARTNER_PAYOUT_REPORT_FILE_PATH+fileName);
            file=new File(PARTNER_PAYOUT_REPORT_FILE_PATH+fileName);
        }

        return file;
    }

    public boolean makeDirectory(String directoryPath,boolean isApplicationManager,FileHandlingUtil fileHandlingUtil)
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

    public File getBankAppTemplate(GatewayTypeVO gatewayTypeVO) throws FileNotFoundException
    {
        File file= null;
        if(gatewayTypeVO!=null && gatewayTypeVO.getGatewayType()!=null && functions.isValueNull(gatewayTypeVO.getGatewayType().getTemplatename()) &&  gatewayTypeVO.getGatewayType().getTemplatename().contains(".pdf"))
        {
            logger.debug("file Path::" + GATEWAY_TEMPLATE_PATH + gatewayTypeVO.getPgTYypeId() + "_" + gatewayTypeVO.getGatewayType().getName() + "/" + gatewayTypeVO.getGatewayType().getTemplatename());
            file = new File(GATEWAY_TEMPLATE_PATH + gatewayTypeVO.getPgTYypeId() + "_" + gatewayTypeVO.getGatewayType().getName() + "/" + gatewayTypeVO.getGatewayType().getTemplatename());
        }

        return file;
    }

    public ZipOutputStream getZipOutputStream(String fileName) throws FileNotFoundException
    {
        return fileHandlingUtil.getZIPOutputStream(fileName);
    }

    public void addFileToZip(File file,ZipOutputStream zipOutputStream) throws IOException
    {
        fileHandlingUtil.addToZipFile(file,zipOutputStream);
    }
    //delete old File and upload with new File
    private FileDetailsVO deleteOldBankTemplateAndUploadNew(FileItem fileItem,String originalFilePath) throws Exception
    {

        FileDetailsVO fileDetailsVO=new FileDetailsVO();


        File replaceFile= new File(originalFilePath+"/"+fileItem.getName());
        File originalFile=null;

        originalFile=new File(originalFilePath+"/"+fileItem.getFieldName().split("\\|")[1]);
        fileHandlingUtil.deleteFile(originalFile);
        fileDetailsVO=fileHandlingUtil.uploadSingleFile(fileItem, originalFilePath);
        fileDetailsVO.setFileActionType(FileActionType.DELETE);

        return fileDetailsVO;
    }

    //check whether the file exist in the system
    private FileDetailsVO replaceFileAppManagerUpload(FileItem fileItem,String originalFilePath,String replaceFilePath,HashMap<String,FileDetailsVO> fileDetailsVOHashMap,FileHandlingUtil fileHandlingUtil) throws Exception
    {
        //date instance
        Calendar currentDate= Calendar.getInstance();
        SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        FileDetailsVO fileDetailsVO=new FileDetailsVO();
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
                fileDetailsVO.setFileActionType(FileActionType.REPLACE);
            }
            else
            {
                fileDetailsVO.setFilename(fileItem.getName());
                fileDetailsVO.setFileType("");
                fileDetailsVO.setFilePath(originalFilePath  + "/" + fileItem.getName());
                fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                fileDetailsVO.setFileActionType(FileActionType.REPLACE);
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
            fileDetailsVO.setFileActionType(FileActionType.REPLACE);
        }
        fileDetailsVO.setMappingId(fileDetailsVOHashMap!=null && fileDetailsVOHashMap.containsKey(fileItem.getFieldName().split("\\|")[0])?fileDetailsVOHashMap.get(fileItem.getFieldName().split("\\|")[0]).getMappingId():"");

        return fileDetailsVO;
    }


    private  FileDetailsVO  checkAcroFiledsOfFileWithBankEnum(PdfReader pdfReader)
    {
        FileDetailsVO fileDetailsVO =null;
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
            fileDetailsVO=new FileDetailsVO();
            fileDetailsVO.setFilename("");
            fileDetailsVO.setFileType(getFileExtension(""));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp("");
            fileDetailsVO.setFileActionType(FileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("Fields not allowed are " + acroFieldsMessage );
        }


        return fileDetailsVO;
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


   public boolean isFtpEnabledForDocumentationServer()
    {
        //ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(STARTFTP))
        {
            return true;
        }

        return false;
    }


    /*public String getPropertyFromDocumentServerProperties(String key)
    {
        ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        String value=null;

        if(resourceBundle.containsKey(key))
            value=resourceBundle.getString(key);

        return value;
    }*/



}
