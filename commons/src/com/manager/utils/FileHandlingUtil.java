package com.manager.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.SystemError;
import com.logicboxes.util.Util;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ContentTypeEnum;
import com.manager.enums.FileActionType;
import com.manager.enums.UploadFileType;
import com.manager.vo.BlacklistVO;
import com.manager.vo.FileDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.fileRelatedVOs.FileValidationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.request.PZFileVO;
import com.payment.response.PZChargebackRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.errors.EncryptionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 8/14/14
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileHandlingUtil
{
    private static final SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int MAGIC_PDF[] = new int [] { 0x25,0x50,0x44,0x46 };
    private static final int MAGIC_EXCEL[] = new int [] { 0x50,0x4B,0x03,0x04 };
    private static final int MAGIC_PNG[] = new int[] { 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
    private static final int MAGIC_JPG[] =  new int[] { 0xFF, 0xD8, 0xFF};
    private static Logger logger = new Logger(FileHandlingUtil.class.getName());
    private static Functions functions = new Functions();

    public static String getFirstSix(String ccpan)
    {
        String firstSix = "";
        int len = ccpan.length();
        if (len == 19){
            firstSix = ccpan.substring(ccpan.length() - 19, ccpan.length() - 13);
        }
        else if (len == 16){
            firstSix = ccpan.substring(ccpan.length() - 16, ccpan.length() - 10);
        }
        else if (len == 15){
            firstSix = ccpan.substring(ccpan.length() - 15, ccpan.length() - 9);
        }
        else if (len == 14){
            firstSix = ccpan.substring(ccpan.length() - 14, ccpan.length() - 8);
        }
        else if (len == 13){
            firstSix = ccpan.substring(ccpan.length() - 13, ccpan.length() - 7);
        }
        else if (len == 12){
            firstSix = ccpan.substring(ccpan.length() - 12, ccpan.length() - 6);
        }
        return firstSix;
    }

    public static String getLastFour(String ccpan)
    {
        String lastFour = "";
        int len = ccpan.length();
        if(len == 19){
            lastFour=ccpan.substring(ccpan.length()-4);
        }
        if(len == 16){
            lastFour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 15)
        {
            lastFour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 14)
        {
            lastFour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 13)
        {
            lastFour = ccpan.substring(ccpan.length() - 4);
        }
        else if (len == 12)
        {
            lastFour = ccpan.substring(ccpan.length() - 4);
        }
        return lastFour;
    }

    public  boolean sendFile(File file,HttpServletResponse response)throws PZTechnicalViolationException
    {

        if(!file.exists())
        {
            FileNotFoundException fileNotFoundException = new FileNotFoundException(file.getName()+"File not found in the system");
            logger.error("File not found exception::::",fileNotFoundException);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"sendFile()",null,"Common","FileNotFoundException while downloading file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,fileNotFoundException.getMessage(),fileNotFoundException.getCause());
            return false;
        }
        else
        {
            int length = 0;
            // Set browser download related headers
            DataInputStream in = null;
            javax.servlet.ServletOutputStream op = null;
            try
            {
                response.setContentType("application/octat-stream");
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                op = response.getOutputStream();

                byte[] bbuf = new byte[1024];
                in = new DataInputStream(new FileInputStream(file));

                while ((in != null) && ((length = in.read(bbuf)) != -1))
                {
                    op.write(bbuf, 0, length);
                }
            }
            catch (FileNotFoundException e)
            {
                logger.error("down loading file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"sendFile()",null,"Common","FileNotFoundException while downloading file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,e.getMessage(),e.getCause());

            }
            catch (IOException e)
            {
                logger.error("down loading file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"sendFile()",null,"Common","IOEXCEPTION while downloading file::"+file.getName(), PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());

            }
            finally
            {
                try
                {
                    if(in!=null)
                        in.close();
                    if(op!=null)
                    {
                        op.flush();
                        op.close();
                    }
                }
                catch(IOException e)
                {
                    logger.error("Exception while closing IO stream ::",e);
                    //todo ask sir for if this exception has to be raised
                }
            }
            logger.info("Successfully donloaded  file======" + file.getName());
            return true;
        }

    }

    //this is to open file not to download(only open the file in window)
    public  boolean openPdfFile(File file,HttpServletResponse response)throws PZTechnicalViolationException
    {

        if(!file.exists())
        {
            FileNotFoundException fileNotFoundException = new FileNotFoundException(file.getName()+"File not found in the system");
            logger.error("File not found exception::::",fileNotFoundException);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"sendFile()",null,"Common","FileNotFoundException while downloading file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,fileNotFoundException.getMessage(),fileNotFoundException.getCause());
            return false;
        }
        else
        {
            int length = 0;
            // Set browser download related headers
            DataInputStream in = null;
            javax.servlet.ServletOutputStream op = null;

            response.setContentType("text/html");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "inline");

            response.setContentType("application/pdf");

            BufferedInputStream  bis = null;
            BufferedOutputStream bos = null;
            try {

                op = response.getOutputStream();
                InputStream isr=new FileInputStream(file);
                bis = new BufferedInputStream(isr);
                bos = new BufferedOutputStream(op);
                byte[] buff = new byte[2048];
                int bytesRead;
                // Simple read/write loop.
                while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                }
            }
            catch (FileNotFoundException e)
            {
                logger.error("Opening file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"Common","FileNotFoundException while opening file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,e.getMessage(),e.getCause());

            }
            catch (IOException e)
            {
                logger.error("Opening file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"Common","IOEXCEPTION while opening file::"+file.getName(), PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());

            }
            finally {
                if (bis != null)
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("Exception while closing BufferedInputStream::::",e);
                        PZExceptionHandler.raiseAndHandleTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"common","Technical Exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Opening File");
                    }
                if (bos != null)
                    try
                    {
                        bos.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("Exception while closing BufferedOutputStream::::",e);
                        PZExceptionHandler.raiseAndHandleTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"common","Technical Exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Opening File");
                    }
            }
            return true;
        }

    }

    public boolean fileExist(File file)
    {
        return file.exists();
    }

    //this is to open file not to download(only open the file in window)
    public  boolean openAllTypeOfFile(File file,HttpServletResponse response,ContentTypeEnum contentType)throws PZTechnicalViolationException
    {

        if(!file.exists())
        {
            FileNotFoundException fileNotFoundException = new FileNotFoundException(file.getName()+"File not found in the system");
            logger.error("File not found exception::::",fileNotFoundException);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"sendFile()",null,"Common","FileNotFoundException while downloading file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,fileNotFoundException.getMessage(),fileNotFoundException.getCause());
            return false;
        }
        else
        {
            int length = 0;
            // Set browser download related headers
            DataInputStream in = null;
            javax.servlet.ServletOutputStream op = null;

            response.setContentType("text/html");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "inline");

            response.setContentType(contentType.toString());

            BufferedInputStream  bis = null;
            BufferedOutputStream bos = null;
            try {

                op = response.getOutputStream();
                InputStream isr=new FileInputStream(file);
                bis = new BufferedInputStream(isr);
                bos = new BufferedOutputStream(op);
                byte[] buff = new byte[2048];
                int bytesRead;
                // Simple read/write loop.
                while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                    bos.write(buff, 0, bytesRead);
                }
            }
            catch (FileNotFoundException e)
            {
                logger.error("Opening file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"Common","FileNotFoundException while opening file::"+file.getName(), PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,e.getMessage(),e.getCause());

            }
            catch (IOException e)
            {
                logger.error("Opening file exception::", e);
                PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"Common","IOEXCEPTION while opening file::"+file.getName(), PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());

            }
            finally {
                if (bis != null)
                    try
                    {
                        bis.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("Exception while closing BufferedInputStream::::",e);
                        PZExceptionHandler.raiseAndHandleTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"common","Technical Exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Opening File");
                    }
                if (bos != null)
                    try
                    {
                        bos.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("Exception while closing BufferedOutputStream::::",e);
                        PZExceptionHandler.raiseAndHandleTechnicalViolationException(FileHandlingUtil.class.getName(),"openFile()",null,"common","Technical Exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause(),null,"Opening File");
                    }
            }
            return true;
        }

    }

    public boolean createChartFile(File file,StringBuilder chartContent) throws PZTechnicalViolationException
    {
        FileWriter fw = null;
        try
        {
            logger.debug(" Creating xml data file " );
            fw = new FileWriter(file,false);
            fw.write(chartContent.toString());
            fw.flush();
            logger.debug("xml data file created Successfully" );
        }
        catch (IOException e)
        {
            logger.error(" Error in creating xml data file ",e);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"createChartFile()",null,"Common","Failure while creating File in the system::"+file.getName(), PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            if (fw != null)
            {
                try
                {
                    fw.close();
                }
                catch (IOException e)
                {
                    logger .error("Could not close file handler" + Util.getStackTrace(e));
                }
            }
        }
        return true;
    }

    //this is to get List of FileItem(DiskFileItem)
    public List<FileItem> getListOfFileItem(HttpServletRequest request)
            throws PZTechnicalViolationException
    {
        //FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //factory.setFileCleaningTracker(fileCleaningTracker);
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileDetailsVO> listOfFiles=new ArrayList<FileDetailsVO>();
        List<FileItem> items = null;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart)
        {
            FileUploadException fileUploadException = new FileUploadException("MultiPartContent not available ",new Throwable("MultiPartContent not available"));
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "File request is not a  MultiPartContent", PZTechnicalExceptionEnum.IOEXCEPTION, null,fileUploadException.getMessage(),fileUploadException.getCause());
        }
        try
        {
            items = upload.parseRequest(request);
        }
        catch (FileUploadException e)
        {
            logger.error(" Error in creating xml data file ",e);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "Failure while uploading File in the system::", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(), e.getCause());
        }

        return items;
    }

    //this is to get FileDetails Vo per FileItem for File Type
    public FileDetailsVO doValidationOnFileItem(FileItem fileItem,List<UploadFileType> uploadFileTypes) throws PZTechnicalViolationException
    {

        FileDetailsVO fileDetailsVO =null;
        FileValidationVO fileValidationVO=multipleFileTypeCases(uploadFileTypes,fileItem);
        if(!fileValidationVO.isFileExtensionValidation())
        {
            fileDetailsVO=new FileDetailsVO();
            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(FileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("File is not a " + fileValidationVO.getTotalFileExtensionCheckOn() + " extension");


        }
        if(!fileValidationVO.isFileContentValidation() && fileValidationVO.isFileExtensionValidation())
        {
            fileDetailsVO=new FileDetailsVO();
            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(FileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("File is not of "+fileValidationVO.getFileType().toString()+" format");
        }

        return fileDetailsVO;
    }

    //upload file into server with dataPathFile as file path
    public FileDetailsVO uploadSingleFile(FileItem fileItem,String DataFilePath) throws Exception
    {
        boolean success;
        String temp=DataFilePath+"/";
        File directory = new File(temp);
        File savedFile=null;

        FileDetailsVO fileDetailsVO =new FileDetailsVO();
        if (directory.exists())
        {
            savedFile = new File(temp+fileItem.getName());
        }
        else
        {
            success = directory.mkdir();
            savedFile = new File(temp+fileItem.getName());
        }
        if (savedFile.exists())
        {

            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath(DataFilePath  + "/" + fileItem.getName());
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(FileActionType.UPLOAD);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("File Exist in the System");
            return fileDetailsVO;
        }
        //this is used to copy temp to file under desired Folder
        fileItem.write(savedFile);
        fileDetailsVO.setFilename(fileItem.getName());
        fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
        fileDetailsVO.setFilePath(DataFilePath  + "/" + fileItem.getName());
        Calendar currentDate= Calendar.getInstance();
        fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
        fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
        fileDetailsVO.setFileActionType(FileActionType.UPLOAD);
        fileDetailsVO.setSuccess(true);
        return fileDetailsVO;
    }

    public boolean makeDirectory(String directoryPath)
    {
        boolean success=false;
        File directory = new File(directoryPath);
        if (directory.exists())
        {
            return true;
        }
        else
        {
            return success = directory.mkdir();
        }
    }

    //rename the file
    public File renameFile(File oldFile,File newFile)
    {
        oldFile.renameTo(newFile);
        return oldFile;
    }

    //delete the file
    public boolean deleteFile(File oldFile)
    {
        return oldFile.delete();
    }

    /*//this is only for pdf multiple upload
    public List<FileDetailsVO> doMultipleFileUpload(HttpServletRequest request, String DataFilePath)
            throws PZTechnicalViolationException
    {
        SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //factory.setFileCleaningTracker(fileCleaningTracker);
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileDetailsVO> listOfFiles=new ArrayList<FileDetailsVO>();
        List items = null;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart)
        {
            FileUploadException fileUploadException = new FileUploadException("MultiPartContent not available ",new Throwable("MultiPartContent not available"));
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "File request is not a  MultiPartContent", PZTechnicalExceptionEnum.IOEXCEPTION, fileUploadException.getMessage(),fileUploadException.getCause());
        }
        try
        {
            items = upload.parseRequest(request);
        }
        catch (FileUploadException e)
        {
            logger.error(" Error in creating xml data file ",e);
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "Failure while uploading File in the system::", PZTechnicalExceptionEnum.IOEXCEPTION, e.getMessage(), e.getCause());
        }
        Iterator itr = items.iterator();
        while (itr.hasNext())
        {
            FileItem item = (FileItem) itr.next();
            if(item.getSize()>0 && !item.isFormField())
            {
                FileDetailsVO fileDetailsVO=new FileDetailsVO();
                fileDetailsVO.setFieldName(item.getFieldName());

                logger.debug("Form field name:::"+item.getFieldName()+" isformfield::"+item.isFormField());
                String itemName=null;
                try
                {
                    itemName= item.getName();
                    //this is for pdf extension check
                    if(!"pdf".equalsIgnoreCase(getFileExtension(itemName)))
                    {
                        fileDetailsVO.setFilename(itemName);
                        fileDetailsVO.setFileType(getFileExtension(itemName));
                        fileDetailsVO.setFilePath(DataFilePath  + "/" + itemName);
                        Calendar currentDate= Calendar.getInstance();
                        fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                        fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("File is not a .pdf extension");
                        listOfFiles.add(fileDetailsVO);
                        item.delete();
                        continue;
                    }
                    //this validation is for the pdf content check
                    if(!getMagicHeaderPDFCheck().accept(((DiskFileItem) item).getStoreLocation()))
                    {
                        fileDetailsVO.setFilename(itemName);
                        fileDetailsVO.setFileType(getFileExtension(itemName));
                        fileDetailsVO.setFilePath(DataFilePath  + "/" + itemName);
                        Calendar currentDate= Calendar.getInstance();
                        fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                        fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("File is not in the pdf format");
                        listOfFiles.add(fileDetailsVO);
                        item.delete();
                        continue;
                    }

                    //content type check success continue from below
                    logger.debug("content type:::"+item.getContentType());
                    boolean success = false;
                    String temp=DataFilePath+"/";
                    File directory = new File(temp);
                    File savedFile=null;
                    if (directory.exists())
                    {
                        savedFile = new File(temp+itemName);
                    }
                    else
                    {
                        success = directory.mkdir();
                        savedFile = new File(temp+itemName);
                    }
                    if (savedFile.exists())
                    {
                        fileDetailsVO.setFilename(itemName);
                        fileDetailsVO.setFileType(getFileExtension(itemName));
                        fileDetailsVO.setFilePath(DataFilePath  + "/" + itemName);
                        Calendar currentDate= Calendar.getInstance();
                        fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                        fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("File Exist in the System");
                        listOfFiles.add(fileDetailsVO);
                        item.delete();
                        continue;
                    }
                    item.write(savedFile);
                    fileDetailsVO.setFilename(itemName);
                    fileDetailsVO.setFileType(getFileExtension(itemName));
                    fileDetailsVO.setFilePath(DataFilePath  + "/" + itemName);
                    Calendar currentDate= Calendar.getInstance();
                    fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                    fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                    fileDetailsVO.setSuccess(true);
                    listOfFiles.add(fileDetailsVO);
                }
                catch (Exception e)
                {
                    fileDetailsVO.setFilename(itemName);
                    fileDetailsVO.setFileType(getFileExtension(itemName));
                    fileDetailsVO.setFilePath(DataFilePath  + "/" + itemName);
                    Calendar currentDate= Calendar.getInstance();
                    fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                    fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
                    listOfFiles.add(fileDetailsVO);
                    fileDetailsVO.setSuccess(false);
                    fileDetailsVO.setReasonOfFailure("file cannot be uploaded due to some internal issue");
                    item.delete();
                    continue;
                }
            }
        }
        return listOfFiles;
    }*/
    //this is used to convert type text to UTF-8 parameter type
    public String getEncodedFieldValueWhileUpload(FileItem fileItem)
    {
        String value=null;
        try
        {
            value=fileItem.getString("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("UnsupportedEncodingException exception while reading data from Multipart/form-data field",e);
            fileItem.delete();
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(FileHandlingUtil.class.getName(),"getEncodedFieldValueWhileUpload()",null,"common","Technical Ezxception",PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause(),null,"While getting field value");
        }
        return value;
    }

    public PdfReader getPdfReader(String pdfDataSheet) throws PZTechnicalViolationException
    {
        PdfReader reader=null;
        try
        {
            reader = new PdfReader(pdfDataSheet);
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getEncodedFieldValueWhileUpload()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return reader;
    }

    public  PdfStamper getPdfStamper(PdfReader pdfReader,String pdfOutputSheet) throws PZTechnicalViolationException
    {
        PdfStamper stamper=null;
        try
        {
            stamper = new PdfStamper(pdfReader,new FileOutputStream(pdfOutputSheet));
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (DocumentException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.DOCUMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return stamper;
    }

    //Excel Magic Header

    /* public AcroFields getAcroFieldsOfPdf(String pdfDataSheet,String pdfOutputSheet) throws PZTechnicalViolationException
     {
         PdfReader reader=null;
         PdfStamper stamper=null;
         AcroFields fields = null;
         // Fill out the data sheet form with data

         try
         {
             reader = new PdfReader(pdfDataSheet);
             stamper = new PdfStamper(reader,
                     new FileOutputStream(pdfOutputSheet));
             fields = stamper.getAcroFields();


         }
         catch (FileNotFoundException e)
         {
             PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"getAcroFieldsOfPdf()",null,"common","Technical Exception",PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION,null,e.getMessage(),e.getCause());
         }
         catch (DocumentException e)
         {
             PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getAcroFieldsOfPdf()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.DOCUMENT_EXCEPTION, null, e.getMessage(), e.getCause());
         }
         catch (IOException e)
         {
             PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(), "getAcroFieldsOfPdf()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
         }

         return fields;
     }*/
    protected String getFileExtension(String filename)
    {
        String extension=null;
        if(filename!=null)
        {
            int ext=filename.trim().lastIndexOf(".");
            extension=filename.substring(ext+1);
        }
        return extension;
    }

    //PDF Magic Header

    private FileFilter getMagicHeaderPDFCheck()
    {
        FileFilter pdfAndDirectoryFileFilter = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                FileInputStream ins=null;
                if(pathname.isDirectory())
                    return true;

                // Check whether we have a PDF file
                byte[] pdfMagicNumbers = {0x25,0x50,0x44,0x46};
                try
                {
                    //System.out.println(pathname.getName());
                    ins= new FileInputStream(pathname);
                    for(int i = 0; i < pdfMagicNumbers.length; i++){
                        if(ins.read() != pdfMagicNumbers[i])
                            return false;
                    }
                }catch(IOException e){
                    logger.error("error while checking Magic header PDF",e);
                    return false;
                }
                finally {
                    if (ins != null)
                    {
                        try
                        {
                            ins.close();
                        }
                        catch (IOException e)
                        {
                            logger.error("error while closing inputStream",e);
                        }
                    }
                }
                return true;
            }
        }  ;

        return pdfAndDirectoryFileFilter;
    }

    //EXCEL Magic Header

    private FileFilter getMagicHeaderEXCELCheck()
    {
        FileFilter excelAndDirectoryFileFilter = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                FileInputStream ins=null;
                if(pathname.isDirectory())
                    return true;

                // Check whether we have a excel file
                byte[] excelMagicNumbers = {0x50,0x4B,0x03,0x04};
                try
                {
                    //System.out.println(pathname.getName());
                    ins= new FileInputStream(pathname);
                    for(int i = 0; i < excelMagicNumbers.length; i++){
                        if(ins.read() != excelMagicNumbers[i])
                            return false;
                    }
                }catch(IOException e){
                    logger.error("error while checking Magic header Excel",e);
                    return false;
                }
                finally {
                    if (ins != null)
                    {
                        try
                        {
                            ins.close();
                        }
                        catch (IOException e)
                        {
                            logger.error("error while closing inputStream",e);
                        }
                    }
                }
                return true;
            }
        }  ;

        return excelAndDirectoryFileFilter;
    }

    //PNG Magic Header

    private boolean getMagicHeaderPDFCheck(FileItem fileItem)
    {
        InputStream ins = null;
        try
        {
            ins=fileItem.getInputStream();
            for (int i = 0; i < MAGIC_PDF.length; ++i)
            {
                if (ins.read() != MAGIC_PDF[i])
                {
                    return false;
                }
            }
        }
        catch(IOException e)
        {
            logger.error("error while checking Magic header Excel",e);
            return false;
        }
        finally
        {
            if (ins != null)
            {
                try
                {
                    ins.close();
                }
                catch (IOException e)
                {
                    logger.error("error while closing inputStream",e);
                }
            }
        }
        return true;

    }

    //JPG Magic Header

    private boolean getMagicHeaderEXCELCheck(FileItem fileItem)
    {
        InputStream ins = null;
        try
        {
            ins=fileItem.getInputStream();
            for (int i = 0; i < MAGIC_EXCEL.length; ++i)
            {
                if (ins.read() != MAGIC_EXCEL[i])
                {
                    return false;
                }
            }
        }
        catch(IOException e)
        {
            logger.error("error while checking Magic header Excel",e);
            return false;
        }
        finally
        {
            if (ins != null)
            {
                try
                {
                    ins.close();
                }
                catch (IOException e)
                {
                    logger.error("error while closing inputStream",e);
                }
            }
        }

        return true;

    }

    private boolean getMagicHeaderPNGCheck(FileItem fileItem) throws IOException
    {
        InputStream ins =null;
        try
        {
            ins=fileItem.getInputStream();
            for (int i = 0; i < MAGIC_PNG.length; ++i)
            {
                if (ins.read() != MAGIC_PNG[i])
                {
                    return false;
                }
            }
        }
        catch(IOException e)
        {
            logger.error("error while checking Magic header Excel",e);
            return false;
        }
        finally
        {
            if (ins != null)
            {
                try
                {
                    ins.close();
                }
                catch (IOException e)
                {
                    logger.error("error while closing inputStream",e);
                }
            }
        }
        return true;

    }

    private boolean getMagicHeaderJPGCheck(FileItem fileItem) throws IOException
    {
        InputStream ins =null;
        try
        {
            ins=fileItem.getInputStream();
            for (int i = 0; i < MAGIC_JPG.length; ++i)
            {
                if (ins.read() != MAGIC_JPG[i])
                {
                    return false;
                }
            }
        }
        catch(IOException e)
        {
            logger.error("error while checking Magic header Excel",e);
            return false;
        }
        finally
        {
            if (ins != null)
            {
                try
                {
                    ins.close();
                }
                catch (IOException e)
                {
                    logger.error("error while closing inputStream",e);
                }
            }
        }
        return true;

    }

    //add case just as the inputValidator for validating the file content
    private FileValidationVO multipleFileTypeCases(List<UploadFileType> uploadFileTypeList,FileItem fileItem) throws PZTechnicalViolationException
    {
        FileValidationVO fileValidationVO= new FileValidationVO();
        fileValidationVO.setTotalFileExtensionCheckOn("."+uploadFileTypeList.toString());

        try
        {
            for (UploadFileType uploadFileType : uploadFileTypeList)
            {
                logger.debug("Upload File Type:::"+uploadFileType.name());
                logger.debug("File Extension:::"+getFileExtension(fileItem.getName()));
                switch (uploadFileType)
                {
                    case PDF:
                        //this has to be in same order as below
                        fileValidationVO.setFileExtensionValidation(uploadFileType.toString().equalsIgnoreCase(getFileExtension(fileItem.getName())));
                        fileValidationVO.setFileContentValidation(getMagicHeaderPDFCheck(fileItem));
                        fileValidationVO.setFileType(uploadFileType);
                        break;

                    case EXCEL:
                        fileValidationVO.setFileExtensionValidation(uploadFileType.toString().equalsIgnoreCase(getFileExtension(fileItem.getName())));
                        fileValidationVO.setFileContentValidation(getMagicHeaderEXCELCheck(fileItem));
                        fileValidationVO.setFileType(uploadFileType);
                        break;

                    case PNG:
                        fileValidationVO.setFileExtensionValidation(uploadFileType.toString().equalsIgnoreCase(getFileExtension(fileItem.getName())));
                        fileValidationVO.setFileContentValidation(getMagicHeaderPNGCheck(fileItem));
                        fileValidationVO.setFileType(uploadFileType);
                        break;

                    case JPG:
                        fileValidationVO.setFileExtensionValidation(uploadFileType.toString().equalsIgnoreCase(getFileExtension(fileItem.getName())));
                        fileValidationVO.setFileContentValidation(getMagicHeaderJPGCheck(fileItem));
                        fileValidationVO.setFileType(uploadFileType);
                        break;


                    default:

                        break;
                }
            }
        }
        catch (IOException io)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"multipleFileTypeCases()",null,"common","No file found exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return fileValidationVO;
    }

    public ZipOutputStream getZIPOutputStream(String fileName) throws FileNotFoundException
    {
        FileOutputStream fos = new FileOutputStream(fileName);
        logger.debug("file name::::"+fos);
        logger.error("file out stream"+fos);
        ZipOutputStream zos = new ZipOutputStream(fos);
        logger.error("zip out stream"+zos);
        return zos;
    }

    /**
     * This is to add File to the ZIP
     * @param file
     * @param zos
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void  addToZipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0)
        {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

    public boolean createFileFromBytes(byte[] bytes,String file) throws PZTechnicalViolationException
    {
        FileOutputStream outputStream=null;
        try
        {
            outputStream= new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"createFileFromInputStream()",null,"common","Exception while creating file",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(FileHandlingUtil.class.getName(),"createFileFromInputStream()",null,"common","Exception while creating file",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        finally
        {
            try
            {
                if(outputStream!=null)
                {
                    outputStream.close();
                }
            }
            catch (IOException e)
            {
                logger.error("IO Exception While Creating the file",e);
            }
        }
        return true;
    }

    public String removeFilePath(String filename,String filePath)
    {
        String fileWithoutExtension=null;
        if(filename!=null)
        {
            int ext=filename.trim().lastIndexOf(filePath);
            fileWithoutExtension=filename.substring(0,ext);
        }
        return fileWithoutExtension;
    }

    public List<String> readCardDetails(String fileName, String accountId, String memberId) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sb=new StringBuilder();
        InputStream inputStream = null;
        Functions functions=new Functions();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }

            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("INSERT INTO whitelist_details(firstsix,lastfour,accountid,memberid,isApproved,emailAddr,isTemp)VALUES");

            final int batchSize = 1000;
            int count = 0;
            while (rows.hasNext())
            {
                count=count+1;
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                String firstSix="";
                String lastFour="";
                String email="";
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum() == 0){
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            firstSix=getFirstSix(cell.getStringCellValue());
                            lastFour=getLastFour(cell.getStringCellValue());
                        }
                    }
                    else if(cell.getCellNum() == 1){
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            email=cell.getStringCellValue();
                        }
                    }
                }
                if ((functions.isValueNull(firstSix) && functions.isValueNull(lastFour)) || functions.isValueNull(email))
                {
                    sb.append("('" + firstSix + "','" + lastFour + "'," + accountId + "," + memberId + ",'Y','" + email + "','N'),");
                }
                if(count==batchSize){
                    count=0;
                    queryBatch.add(stringBuilder.toString()+sb.toString());
                    sb=null;
                    sb=new StringBuilder();
                }
            }
            queryBatch.add(stringBuilder.toString() + sb.toString());
        }
        catch (IOException e)
        {
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if (num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (SystemError systemError)
        {
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return queryBatch;
    }

    public List<String> readCardDetailsNewForGroup(String fileName, String accountId, String memberId) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        Functions functions = new Functions();
        boolean isRecordAvailable = false;
        if(functions.isValueNull(accountId)){
            accountId=accountId;
        }else {
            accountId="0";
        }

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if (inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }

            WhiteListManager whiteListManager = new WhiteListManager();
            MerchantDAO merchantDAO = new MerchantDAO();

            MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memberId);
            String level = merchantDetailsVO.getCardWhitelistLevel();
            String companyName = merchantDetailsVO.getCompany_name();

           /* GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String gateway = account.getGateway();*/

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT IGNORE INTO whitelist_details(firstsix,lastfour,accountid,memberid,isApproved,emailAddr,isTemp,name,ipAddress,expiryDate)VALUES");

            final int batchSize = 1000;
            int count = 0;
            while (rows.hasNext())
            {
                count = count + 1;
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                String firstSix = "";
                String lastFour = "";
                String email = "";
                String cardHolderName="";
                String ipAddress="";
                String expiryDate="";
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.getCellNum() == 0)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            firstSix = getFirstSix(cell.getStringCellValue());
                            lastFour = getLastFour(cell.getStringCellValue());
                        }
                    }
                    else if (cell.getCellNum() == 1)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            email = cell.getStringCellValue();
                        }
                    }
                    else if(cell.getCellNum()==2)
                    {
                        if(functions.isValueNull(cell.getStringCellValue())){
                            cardHolderName=cell.getStringCellValue();
                        }
                    }
                    else if(cell.getCellNum()==3){
                        if(functions.isValueNull(cell.getStringCellValue())){
                            ipAddress=cell.getStringCellValue();
                        }
                    }
                    else if(cell.getCellNum()==4){
                        if(functions.isValueNull(cell.getStringCellValue())){
                            expiryDate=cell.getStringCellValue();
                        }
                    }
                }
                String encryptExpiry="";
                if(functions.isValueNull(expiryDate)){
                    encryptExpiry= PzEncryptor.hashExpiryDate(expiryDate, memberId);
                }
                if ((functions.isValueNull(firstSix) && functions.isValueNull(lastFour)) || functions.isValueNull(email))
                {
                    /*if ("Group".equals(level))
                    {
                        if (functions.isValueNull(email))
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableOnOtherGroup(firstSix, lastFour, email, gateway, companyName);
                        }
                        else
                        {
                            isRecordAvailable = whiteListManager.isRecordAvailableOnOtherGroup(firstSix, lastFour, gateway, companyName);
                        }
                    }*/
                    if (!isRecordAvailable)
                    {
                        sb.append("('" + firstSix + "','" + lastFour + "'," + accountId + "," + memberId + ",'Y','" + email + "','N','" + cardHolderName +"','"+ipAddress+"','"+encryptExpiry+"'),");
                    }
                }
                if (count == batchSize)
                {
                    count = 0;
                    queryBatch.add(stringBuilder.toString() + sb.toString());
                    sb = null;
                    sb = new StringBuilder();
                }
            }
            if(functions.isValueNull(sb.toString()))
            {
                queryBatch.add(stringBuilder.toString() + sb.toString());
            }
        }
        catch (IOException e)
        {
            if (e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if (num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (SystemError systemError)
        {
            throw new SystemError("Error: invalid file content");
        }
        catch (PZDBViolationException e)
        {
            throw new SystemError("Error: Database communication");
        }
        catch (EncryptionException e)
        {
            logger.error("EncryptionException FileHandlingUtil ::::::",e);
        }
        catch (Exception e){
            logger.error("Exception FileHandlingUtil ::::::", e);
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return queryBatch;
    }
    public List<String> readBinDetails(String fileName,String accountId,String memberId) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sb=new StringBuilder();
        InputStream inputStream = null;
        Functions functions=new Functions();
        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }

            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("INSERT INTO whitelist_bins(startbin,endbin,accountid,memberid)VALUES");

            final int batchSize = 1000;
            int count = 0;
            while(rows.hasNext())
            {
                count=count+1;
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                String startBin="";
                String endBin="";
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum() == 0){
                        startBin=String.valueOf((int)(cell.getNumericCellValue()));
                    }
                    else if(cell.getCellNum() == 1){
                        endBin=String.valueOf((int)(cell.getNumericCellValue()));
                    }
                }
                if((functions.isValueNull(startBin) && functions.isValueNull(endBin)))
                {
                    sb.append("('" + startBin + "','" + endBin + "'," + accountId + "," + memberId +"),");
                }
                if(count==batchSize){
                    count=0;
                    queryBatch.add(stringBuilder.toString()+sb.toString());
                    sb=null;
                    sb=new StringBuilder();
                }
            }
            queryBatch.add(stringBuilder.toString() + sb.toString());
        }
        catch (IOException e)
        {
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return queryBatch;
    }
    public List<String> readBlacklistBinDetails(String fileName,String accountId,String memberId) throws SystemError
    {
        List<String> queryBatch = new ArrayList();
        StringBuilder sb=new StringBuilder();
        InputStream inputStream = null;
        Functions functions=new Functions();
        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }

            StringBuilder stringBuilder=new StringBuilder();
            stringBuilder.append("INSERT INTO blacklist_bin(startbin,accountid,memberid,endbin)VALUES");

            final int batchSize = 1000;
            int count = 0;
            while(rows.hasNext())
            {
                count=count+1;
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                String startBin="";
                String endBin="";
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum() == 0){
                        startBin=String.valueOf((int)(cell.getNumericCellValue()));
                    }
                    else if(cell.getCellNum() == 1){
                        endBin=String.valueOf((int)(cell.getNumericCellValue()));
                    }
                }
                if((functions.isValueNull(startBin) && functions.isValueNull(endBin)))
                {
                    sb.append("(" + startBin + ","+ accountId + "," + memberId +"," + endBin +"),");
                }
                if(count==batchSize){
                    count=0;
                    queryBatch.add(stringBuilder.toString()+sb.toString());
                    sb=null;
                    sb=new StringBuilder();
                }
            }
            queryBatch.add(stringBuilder.toString() + sb.toString());
        }
        catch (IOException e)
        {
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return queryBatch;
    }


    public boolean isFileNameValid(String fileName)
    {
        boolean isValidFilename = false;
        if(!fileName.endsWith(".xls"))
        {
            return isValidFilename;
        }
        isValidFilename = true;
        return isValidFilename;
    }

    public void deleteFile(String filename)
    {
        File deleteFile = new File(filename ) ;
        if( deleteFile.exists() )
            deleteFile.delete() ;
    }

    public List<TransactionVO> readFraudDetails(String fileName) throws SystemError
    {
        List<TransactionVO> vList = new ArrayList();
        TransactionVO transactionVO=null;
        InputStream inputStream = null;

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }
            while(rows.hasNext())
            {
                int i=1;
                String trackingId="";
                String paymentId="";
                String reason="";
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                cells = row.cellIterator();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.getCellNum() == 0)
                    {
                        try
                        {
                            trackingId = row.getCell((short) 0).getNumericCellValue()+"";
                            Double trackid = Double.parseDouble(trackingId);
                            int trackingid = trackid.intValue();
                            trackingId = String.valueOf(trackingid);
                        }
                        catch (NumberFormatException nfe)
                        {
                            trackingId = row.getCell((short) 0).getStringCellValue() + "";
                        }
                    }
                    else if (cell.getCellNum() == 1)
                    {
                        try
                        {
                            paymentId = row.getCell((short) 1).getNumericCellValue() + "";
                            Double pymtid = Double.parseDouble(paymentId);
                            int paymentid = pymtid.intValue();
                            paymentId = String.valueOf(paymentid);
                        }
                        catch (NumberFormatException nfe)
                        {
                            paymentId = row.getCell((short) 1).getStringCellValue() + "";
                        }

                    }
                    else if (cell.getCellNum() == 2)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            reason = cell.getStringCellValue();

                        }
                    }
                    if(i == 3 || i==2)
                    {
                        if (functions.isValueNull(trackingId) || functions.isValueNull(paymentId) || functions.isValueNull(reason))
                        {
                            transactionVO = new TransactionVO();
                            if (functions.isValueNull(trackingId))
                            {
                                transactionVO.setTrackingId(trackingId);
                            }
                            if (functions.isValueNull(paymentId))
                            {
                                transactionVO.setPaymentId(paymentId);
                            }
                            if (functions.isValueNull(reason))
                            {
                                transactionVO.setFraudreason(reason);
                            }
                        }
                    }
                    i++;
                    }
                vList.add(transactionVO);
                }

        }
        catch (IOException e)
        {
            logger.error("IOException FileHandlingUtil ::::::", e);
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            logger.error("NumberFormatException FileHandlingUtil ::::::", num);
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {
            logger.error("SystemError FileHandlingUtil ::::::", systemError);
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return vList;
    }

    public List<TransactionVO> readReconTxn(String fileName) throws SystemError
    {
        List<TransactionVO> vList = new ArrayList();
        TransactionVO transactionVO=null;
        InputStream inputStream = null;

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }
            while(rows.hasNext())
            {
                int i=1;
                String trackingId="";
                String paymentId="";
                String status="";
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                cells = row.cellIterator();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.getCellNum() == 0)
                    {
                        try
                        {
                            trackingId = row.getCell((short) 0).getNumericCellValue() + "";
                            Double trackid = Double.parseDouble(trackingId);
                            int trackingid = trackid.intValue();
                            trackingId = String.valueOf(trackingid);
                        }
                        catch (NumberFormatException nfe)
                        {
                            trackingId = row.getCell((short) 0).getStringCellValue() + "";
                        }
                    }
                    else if (cell.getCellNum() == 1)
                    {
                        try
                        {
                            paymentId = row.getCell((short) 1).getNumericCellValue() + "";
                            Double pymtid = Double.parseDouble(paymentId);
                            int paymentid = pymtid.intValue();
                            paymentId = String.valueOf(paymentid);
                        }
                        catch (NumberFormatException nfe)
                        {
                            paymentId = row.getCell((short) 1).getStringCellValue() + "";
                        }
                    }
                    else if (cell.getCellNum() == 2)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            status = cell.getStringCellValue();
                        }
                    }
                    if(i == 3 || i==2)
                    {
                        if (functions.isValueNull(trackingId) || functions.isValueNull(paymentId) || functions.isValueNull(status))
                        {
                             transactionVO = new TransactionVO();
                            if (functions.isValueNull(trackingId))
                            {
                                transactionVO.setTrackingId(trackingId);
                            }
                            if (functions.isValueNull(paymentId))
                            {
                                transactionVO.setPaymentId(paymentId);
                            }
                            if (functions.isValueNull(status))
                            {
                                transactionVO.setStatus(status);
                            }
                        }
                    }
                    i++;
                }
                vList.add(transactionVO);
            }

        }
        catch (IOException e)
        {
            logger.error("IOException FileHandlingUtil ::::::", e);
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            logger.error("NumberFormatException FileHandlingUtil ::::::", num);
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {
            logger.error("SystemError FileHandlingUtil ::::::", systemError);
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return vList;
    }

    public List<TransactionVO> readPayoutUpload(String fileName) throws SystemError
    {
        List<TransactionVO> vList = new ArrayList();
        TransactionVO transactionVO=null;
        InputStream inputStream = null;

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            if(inputStream == null)
            {
                throw new SystemError("File not found");
            }
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop=0;
            boolean isFileProceeding = false;
            String paymentId="";
            String fullname="";
            String bankaccount="";
            String ifsccode="";
            String amount="";
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Invalid file format");
            }
            while(rows.hasNext())
            {
                int i=1;
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                Iterator cells = row.cellIterator();
                cells = row.cellIterator();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if (cell.getCellNum() == 0)
                    {
                        if (functions.isValueNull(cell.getStringCellValue()))
                        {
                            fullname = cell.getStringCellValue();
                            logger.error("fullname :::::::::" + fullname);

                        }
                    }
                     if (cell.getCellNum() == 1)
                    {
                        try
                        {
                            if (functions.isValueNull(cell.getStringCellValue()))
                            {
                                bankaccount = cell.getStringCellValue();
                                logger.error("bankaccount :::::::::" + bankaccount);
                            }
                        }catch (NumberFormatException ne)
                        {
                            bankaccount = String.valueOf((long)cell.getNumericCellValue());
                            logger.error("bankaccount :::::::::" + bankaccount);
                        }
                    }
                     if (cell.getCellNum() == 2)
                    {
                        try{
                            if (functions.isValueNull(cell.getStringCellValue()))
                            {
                                ifsccode = cell.getStringCellValue();
                                logger.error("ifsccode :::::::::" + ifsccode);
                            }
                        }catch (NumberFormatException ne)
                        {
                            ifsccode = String.valueOf((long)cell.getNumericCellValue());
                        }
                    }
                     if (cell.getCellNum() == 3)
                    {
                        try
                        {
                            amount = row.getCell((short) 3).getNumericCellValue() + "";
                            Double amt = Double.parseDouble(amount);
                            amount = String.valueOf(amt);
                            logger.error("amount :::::::::" + amount);

                        }
                        catch (NumberFormatException nfe)
                        {
                            amount = row.getCell((short) 3).getStringCellValue() + "";
                            logger.error("amount :::::::::" + amount);


                        }
                    }
                     if (cell.getCellNum() == 5)
                    {
                        try
                        {
                            paymentId = row.getCell((short) 5).getNumericCellValue() + "";
                            Double trid = Double.parseDouble(paymentId);
                            int trans = trid.intValue();
                            paymentId = String.valueOf(trans);
                            logger.error("paymentId :::::::::" + paymentId);


                        }
                        catch (NumberFormatException nfe)
                        {
                            paymentId = row.getCell((short) 5).getStringCellValue() + "";
                            logger.error("paymentId :::::::::" + paymentId);

                        }
                    }
                        if (functions.isValueNull(fullname) || functions.isValueNull(paymentId) || functions.isValueNull(bankaccount) || functions.isValueNull(ifsccode)|| functions.isValueNull(amount))
                        {
                            transactionVO = new TransactionVO();
                            if (functions.isValueNull(fullname))
                            {
                                transactionVO.setFullname(fullname);
                            }
                            if (functions.isValueNull(paymentId))
                            {
                                transactionVO.setPaymentId(paymentId);
                            }
                            if (functions.isValueNull(bankaccount))
                            {
                                transactionVO.setBankaccount(bankaccount);
                            }
                            if (functions.isValueNull(ifsccode))
                            {
                                transactionVO.setIFSCCode(ifsccode);
                            }
                            if (functions.isValueNull(amount))
                            {
                                transactionVO.setAmount(amount);
                            }
                        }

                    i++;
                }
                vList.add(transactionVO);
            }

        }
        catch (IOException e)
        {
            logger.error("IOException FileHandlingUtil ::::::", e);
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            logger.error("NumberFormatException FileHandlingUtil ::::::",num);
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: invalid file content");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {
            logger.error("SystemError  processpayoutupload---------->",systemError);
            throw new SystemError("Error: invalid file content");
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }
        return vList;
    }


    public List<TransactionVO> readFraudFile(String fileName) throws SystemError
    {
        //throw new SystemError("Functionality is not allowed for selected Account");
        List<TransactionVO> transactionVOs = new ArrayList();
        InputStream inputStream = null;



        try
        {
            //Create a new workbook instance.
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName));
            //Get first sheet from the workbook.
            HSSFSheet sheet = workbook.getSheetAt(0);
            //Iterator through each row one by one.
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            //Skip header rows.
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;


                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                TransactionVO transactionVO = new TransactionVO();
                try
                {
                    transactionVO.setTrackingId(row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setTrackingId(row.getCell((short) 0).getStringCellValue() + "");
                }
                try
                {
                    transactionVO.setPaymentId(row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setPaymentId(row.getCell((short) 1).getStringCellValue() + "");
                }

                transactionVO.setCurrency(row.getCell((short) 2).getStringCellValue());
                /*try
                {
                    transactionVO.setCurrency(row.getCell((short) 2).getStringCellValue());
                }catch(NullPointerException E){

                    throw new SystemError("Error: Currency should Not be empty");
                }catch(NumberFormatException N) {
                    throw new SystemError("Invalid Currency");
                }*/
                try
                {
                    transactionVO.setFraudAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setFraudAmount(row.getCell((short) 3).getStringCellValue() + "");
                }
                transactionVO.setFraudreason(row.getCell((short) 4).getStringCellValue());
                try
                {
                    transactionVO.setDate(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setDate(row.getCell((short) 5).getStringCellValue());
                }
                transactionVOs.add(transactionVO);
            }
        }
        catch (IOException e)
        {
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Please Enter Required Filed");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Please Enter Required Filed");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {

            throw new SystemError(systemError.getMessage());
        }finally{
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }

        return transactionVOs;
    }

    public List<TransactionVO> readFraudFileXlsx(String fileName) throws SystemError
    {
        //throw new SystemError("Functionality is not allowed for selected Account");
        List<TransactionVO> transactionVOs = new ArrayList();
        InputStream inputStream = null;

        try
        {
            //Create a new workbook instance.
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName));
            //Get first sheet from the workbook.
            XSSFSheet sheet = workbook.getSheetAt(0);
            //Iterator through each row one by one.
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            //Skip header rows.
            while (rows.hasNext())
            {
                XSSFRow row = (XSSFRow) rows.next();
                cntRowToLeaveFromTop++;


                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                XSSFRow row = (XSSFRow) rows.next();
                TransactionVO transactionVO = new TransactionVO();
                try
                {
                    transactionVO.setTrackingId(row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setTrackingId(row.getCell((short) 0).getStringCellValue() + "");
                }
                try
                {
                    transactionVO.setPaymentId(row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setPaymentId(row.getCell((short) 1).getStringCellValue() + "");
                }

                transactionVO.setCurrency(row.getCell((short) 2).getStringCellValue());
                try
                {
                    transactionVO.setFraudAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setFraudAmount(row.getCell((short) 3).getStringCellValue() + "");
                }
                transactionVO.setFraudreason(row.getCell((short) 4).getStringCellValue());
                try
                {
                    transactionVO.setDate(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    transactionVO.setDate(row.getCell((short) 5).getStringCellValue());
                }
                transactionVOs.add(transactionVO);
            }
        }
        catch (IOException e)
        {
            if(e.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Please Enter Required Filed");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch (NumberFormatException num)
        {
            if(num.getMessage().toLowerCase().contains("invalid"))
            {
                throw new SystemError("Error: Please Enter Required Filed");
            }
            throw new SystemError("ERROR:  File cant be read");
        }
        catch(SystemError systemError)
        {

            throw new SystemError(systemError.getMessage());
        }finally{
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: File cant be closed successfully");
                }
            }
        }

        return transactionVOs;
    }

    public List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError
    {
        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                PZChargebackRecord loadTransactions = new PZChargebackRecord();
                try
                {
                    loadTransactions.setTrackingid((long)row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getStringCellValue());
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setTrackingid("");
                }
                try
                {
                    loadTransactions.setPaymentid((long)row.getCell((short) 1).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setPaymentid(row.getCell((short) 1).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setPaymentid("");
                }
                try
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setCurrency(row.getCell((short) 2).getNumericCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setCurrency("");
                }
                try
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 3).getStringCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackAmount("");
                }
                try
                {
                    loadTransactions.setChargebackReason(row.getCell((short) 4).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackReason(row.getCell((short) 4).getStringCellValue());
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackReason("");
                }
                try
                {
                    loadTransactions.setIsBlacklist(row.getCell((short) 5).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setIsBlacklist(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setIsBlacklist("");
                }
                try
                {
                    loadTransactions.setIsRefund(row.getCell((short) 6).getStringCellValue());
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setIsRefund(row.getCell((short) 6).getNumericCellValue()+"");
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setIsRefund("");
                }
                try
                {
                    logger.error("DATE---" + row.getCell((short) 7).getStringCellValue());
                    loadTransactions.setChargebackDate(row.getCell((short) 7).getStringCellValue());
                }
                catch (NumberFormatException e)
                {
                    logger.error("DATE===" + row.getCell((short) 7).getDateCellValue());
                    Date date=row.getCell((short) 7).getDateCellValue();
                    logger.error("Format Date--"+simpleDateFormat.format(date));
                    loadTransactions.setChargebackDate(simpleDateFormat.format(date));
                }
                catch (IllegalStateException e)
                {
                    logger.error("DATE Illegal ===" + row.getCell((short) 7).getDateCellValue());
                    Date date=row.getCell((short) 7).getDateCellValue();
                    logger.error("Format Date--"+simpleDateFormat.format(date));
                    loadTransactions.setChargebackDate(simpleDateFormat.format(date));
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setChargebackDate("");
                }
                pzChargebackRecordList.add(loadTransactions);
            }
        }
        catch (Exception e)
        {
            logger.error("IOException:::::", e);
        }

        return pzChargebackRecordList;
    }

    public List<BlacklistVO> readBlockedPhone(PZFileVO fileName) throws SystemError
    {
        List<BlacklistVO> blockVpalist= new ArrayList();

        try
        {
            HSSFWorkbook workbook= new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet= workbook.getSheetAt(0);
            Iterator rows= sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding= true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                BlacklistVO loadvparecords= new BlacklistVO();

                try
                {
                    loadvparecords.setPhone((row.getCell((short) 0).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getPhone(), loadvparecords.getPhone(), "Phone", 24, false))
                    {
                        if(functions.isValueNull(loadvparecords.getPhone()))
                        {
                            throw new SystemError(" Invalid Phone Number " + loadvparecords.getPhone() + "<BR/>");
                        }
                    }
                }
                catch (Exception ne)
                {
                    logger.error("Error :: ", ne);
                    if(functions.isValueNull(ne.getMessage()))
                    {
                        loadvparecords.setError(ne.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if(!functions.isValueNull(loadvparecords.getPhone())){
                        throw new SystemError(" Invalid Phone Number  and Phone Number should not be empty." + loadvparecords.getPhone() + "<BR/>");
                    }
                }
                catch (Exception ne)
                {
                    logger.error("Error :: ", ne);
                    if(functions.isValueNull(ne.getMessage()))
                    {
                        loadvparecords.setError(ne.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    loadvparecords.setBlacklistReason((row.getCell((short)1).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason " + loadvparecords.getBlacklistReason() + "<BR/>");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if ( !ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        if(functions.isValueNull(loadvparecords.getPhone()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason for Phone Number " + loadvparecords.getPhone());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if((!ESAPI.validator().isValidInput(loadvparecords.getPhone(), loadvparecords.getPhone(), "Phone", 24, false)) && (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false)))
                    {
                        System.out.println(loadvparecords.getPhone());
                        if(functions.isValueNull(loadvparecords.getPhone()) && functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid Phone Number " + loadvparecords.getPhone() + " for invalid Blacklist Reason " + loadvparecords.getBlacklistReason());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }

                blockVpalist.add(loadvparecords);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: "+e.getMessage());

        }
        return blockVpalist;
    }

    public List<BlacklistVO> readblacklistip(PZFileVO fileName) throws SystemError
    {
        List<BlacklistVO> blacklistip= new ArrayList();

        try
        {
            HSSFWorkbook workbook= new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet= workbook.getSheetAt(0);
            Iterator rows= sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding= true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                System.out.println("row1");
                HSSFRow row= (HSSFRow)rows.next();
                BlacklistVO loadvparecords= new BlacklistVO();

                try
                {
                    loadvparecords.setMemberId(((long) row.getCell((short) 0).getNumericCellValue() + ""));
                    // loadvparecords.setMemberId((row.getCell((short) 1).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getMemberId(), loadvparecords.getMemberId(), "Numbers", 50, false))
                    {
                        if(functions.isValueNull(loadvparecords.getMemberId()))
                        {
                            throw new SystemError(" Invalid Member Id " + loadvparecords.getMemberId() + "<BR/>");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    loadvparecords.setIpAddress((row.getCell((short) 1).getStringCellValue() + ""));
                    //loadvparecords.setIpAddress((row.getCell((short) 1).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getIpAddress(), loadvparecords.getIpAddress(), "IPAddress", 50, false))
                    {
                        if(functions.isValueNull(loadvparecords.getIpAddress()))
                        {
                            throw new SystemError(" Invalid IP Address " + loadvparecords.getIpAddress() + "<BR/>");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }

                try
                {
                    loadvparecords.selectIpVersion((row.getCell((short) 2).getStringCellValue()));
                    boolean isIPv4 = false;
                    boolean isIPv6 = false;
                    isIPv4 = isIPv4(loadvparecords.getselectIpVersion());
                    isIPv6 = isIPv6(loadvparecords.getselectIpVersion());
                    if (isIPv6 == true && loadvparecords.getselectIpVersion().equals("IPv4"))
                    {
                        if(functions.isValueNull(loadvparecords.getselectIpVersion()))
                        {
                            throw new SystemError(" Invalid IP Version " + loadvparecords.getselectIpVersion() + "<BR/>");
                        }
                    }
                    else if (isIPv4 == true && loadvparecords.getselectIpVersion().equals("IPv6"))
                    {
                        if(functions.isValueNull(loadvparecords.getselectIpVersion()))
                        {
                            throw new SystemError(" Invalid IP Version " + loadvparecords.getselectIpVersion() + "<BR/>");
                        }
                    }

                }
                catch (Exception ne)
                {
                    logger.error("Error :: ", ne);
                    if(functions.isValueNull(ne.getMessage()))
                    {
                        loadvparecords.setError(ne.getMessage() + "<BR/>");
                    }

                }
                blacklistip.add(loadvparecords);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: "+e.getMessage());

        }
        return blacklistip;
    }
    private boolean isIPv4(String ipAddress)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < ipAddress.length(); i++)
        {
            c = ipAddress.charAt(i);
            if (c == '.')
            {
                count++;
            }
        }
        if (count == 4)
        {
            status = true;
        }
        return status;
    }

    private boolean isIPv6(String ipAddress)
    {
        boolean status = false;
        int count = 1;
        char c;
        for (int i = 1; i < ipAddress.length(); i++)
        {
            c = ipAddress.charAt(i);
            if (c == ':')
            {
                count++;
            }
        }
        if (count == 8)
        {
            status = true;
        }
        return status;
    }

    public List<BlacklistVO> readBlockedVPAFile(PZFileVO fileName) throws SystemError
    {
        List<BlacklistVO> blockVpalist= new ArrayList();

        try
        {
            HSSFWorkbook workbook= new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet= workbook.getSheetAt(0);
            Iterator rows= sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding= true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                BlacklistVO loadvparecords= new BlacklistVO();

                try
                {
                    loadvparecords.setVpaAddress((row.getCell((short)0).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getVpaAddress(), loadvparecords.getVpaAddress(), "VPAAddress", 50, false))
                    {
                        if(functions.isValueNull(loadvparecords.getVpaAddress()))
                        {
                            throw new SystemError(" Invalid VPA Address " + loadvparecords.getVpaAddress() + "<BR/>");
                        }
                    }
                }
                catch (Exception ne)
                {
                    logger.error("Error :: ", ne);
                    if(functions.isValueNull(ne.getMessage()))
                    {
                        loadvparecords.setError(ne.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    loadvparecords.setBlacklistReason((row.getCell((short)1).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason " + loadvparecords.getBlacklistReason() + "<BR/>");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if ( !ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        System.out.println(loadvparecords.getVpaAddress());
                        if(functions.isValueNull(loadvparecords.getVpaAddress()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason for VPA Address " + loadvparecords.getVpaAddress());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if((!ESAPI.validator().isValidInput(loadvparecords.getVpaAddress(), loadvparecords.getVpaAddress(), "VPAAddress", 50, false)) && (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false)))
                    {
                        if(functions.isValueNull(loadvparecords.getVpaAddress()) && functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid VPA Address " + loadvparecords.getVpaAddress() + " for invalid Blacklist Reason " + loadvparecords.getBlacklistReason());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }

                try
                {
                    if (!functions.isValueNull(loadvparecords.getVpaAddress()) && functions.isValueNull(loadvparecords.getBlacklistReason()))
                    {
                        throw  new SystemError(" Invalid VPA Address " +"<BR/>");
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ",e);
                    if (functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }

                blockVpalist.add(loadvparecords);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: "+e.getMessage());

        }
        return blockVpalist;
    }

    public List<BlacklistVO> readBlockedEmail(PZFileVO fileName)throws SystemError
    {
        List<BlacklistVO> blockemaillist= new ArrayList();

        try
        {
            HSSFWorkbook workbook= new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet= workbook.getSheetAt(0);
            Iterator rows= sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding= true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow) rows.next();
                BlacklistVO  loademailrecords= new BlacklistVO();

                try
                {
                    loademailrecords.setEmailAddress((row.getCell((short)0).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loademailrecords.getEmailAddress(), loademailrecords.getEmailAddress(), "Email", 100, false))
                    {
                        if(functions.isValueNull(loademailrecords.getEmailAddress()))
                        {
                            throw new SystemError("Invalid EmailAddress " + loademailrecords.getEmailAddress() + "<BR/>");
                        }
                    }

                }
                catch (Exception e)
                {
                    logger.error("Error:: ",e);
                    if (functions.isValueNull(e.getMessage()))
                    {
                        loademailrecords.setError(e.getMessage() +"<BR/>");
                    }
                }
                blockemaillist.add(loademailrecords);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: ",e);
        }

        return blockemaillist;
    }
    public List<BlacklistVO> readBlockedBankAccount(PZFileVO fileName) throws SystemError
    {
        List<BlacklistVO> blockbankaccount= new ArrayList();

        try
        {
            HSSFWorkbook workbook= new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet= workbook.getSheetAt(0);
            Iterator rows= sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding= true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row= (HSSFRow)rows.next();
                BlacklistVO loadvparecords= new BlacklistVO();

                try
                {
                    loadvparecords.setBlacklistBankAccountNo((row.getCell((short) 0).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistBankAccountNo(), loadvparecords.getBlacklistBankAccountNo(), "OnlyNumber", 50, false))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistBankAccountNo()))
                        {
                            throw new SystemError(" Invalid Bank Account Number " + loadvparecords.getBlacklistBankAccountNo() + "<BR/>");
                        }
                    }
                }
                catch (Exception ne)
                {
                    logger.error("Error :: ", ne);
                    if(functions.isValueNull(ne.getMessage()))
                    {
                        loadvparecords.setError(ne.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    loadvparecords.setBlacklistReason((row.getCell((short)1).getStringCellValue()));
                    if (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason " + loadvparecords.getBlacklistReason() + "<BR/>");
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if ( !ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistBankAccountNo()))
                        {
                            throw new SystemError(" Invalid Blacklist Reason for Bank Account Number " + loadvparecords.getBlacklistBankAccountNo());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }
                try
                {
                    if((!ESAPI.validator().isValidInput(loadvparecords.getBlacklistBankAccountNo(), loadvparecords.getBlacklistBankAccountNo(), "Numbers", 30, false)) && (!ESAPI.validator().isValidInput(loadvparecords.getBlacklistReason(), loadvparecords.getBlacklistReason(), "Description", 100, false)))
                    {
                        if(functions.isValueNull(loadvparecords.getBlacklistBankAccountNo()) && functions.isValueNull(loadvparecords.getBlacklistReason()))
                        {
                            throw new SystemError(" Invalid Bank Account Number " + loadvparecords.getBlacklistBankAccountNo() + " for invalid Blacklist Reason " + loadvparecords.getBlacklistReason());
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.error("Error :: ", e);
                    if(functions.isValueNull(e.getMessage()))
                    {
                        loadvparecords.setError(e.getMessage() + "<BR/>");
                    }
                }

                blockbankaccount.add(loadvparecords);
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: "+e.getMessage());

        }
        return blockbankaccount;
    }

}