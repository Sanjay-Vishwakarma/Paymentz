package com.utils;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.enums.AppFileActionType;
import com.enums.AppUploadFileType;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.AppUploadLabelVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.vo.applicationManagerVOs.AppFileValidationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: NIKET
 * Date: 8/14/14
 * Time: 5:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppFileHandlingUtil
{
    private static Logger logger = new Logger(AppFileHandlingUtil.class.getName());
    private static Functions functions = new Functions();

    private static final SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int MAGIC_PDF[] = new int [] { 0x25,0x50,0x44,0x46 };
    private static final int MAGIC_EXCEL[] = new int [] { 0x50,0x4B,0x03,0x04 };
    private static final int MAGIC_PNG[] = new int[] { 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a };
    private static final int MAGIC_JPG[] =  new int[] { 0xFF, 0xD8, 0xFF};

    public List<FileItem> getListOfFileItemForApplicationManager(FormDataMultiPart formDataMultiPart,ApplicationManagerVO applicationManagerVO)  //Changes for Multiple KYC
    {
        DiskFileItemFactory factory = new DiskFileItemFactory();

        List<FileItem> items = new ArrayList<FileItem>();

        for(Map.Entry<String,AppUploadLabelVO> uploadLabelVOEntry:applicationManagerVO.getUploadLabelVOs().entrySet())
        {
            AppUploadLabelVO uploadLabelVO=uploadLabelVOEntry.getValue();
            if(uploadLabelVO!=null && functions.isValueNull(uploadLabelVO.getAlternateName()))
            {
                FormDataBodyPart formDataBodyPart = formDataMultiPart.getField(uploadLabelVO.getAlternateName());

                if(formDataBodyPart!=null && !MediaType.TEXT_PLAIN_TYPE.equals(formDataBodyPart.getMediaType()) && functions.isValueNull(formDataBodyPart.getContentDisposition().getFileName()))
                {
                    logger.debug("CONDITION::::"+formDataBodyPart.getMediaType()+" FILE NAME:::"+formDataBodyPart.getContentDisposition().getFileName()+" SIZE:::"+formDataBodyPart.getContentDisposition().getSize());
                    logger.debug("FILE NAME:::" + formDataBodyPart.getContentDisposition().getFileName());
                    //factory.setRepository(formDataBodyPart.getValueAs(File.class));

                    String name = "";

                    /*if (applicationManagerVO.getFileDetailsVOs().containsKey(uploadLabelVO.getAlternateName()))
                    {
                        name = "|Replace";
                    }*/

                    FileItem fileItem = factory.createItem(uploadLabelVO.getAlternateName() + "|" + uploadLabelVOEntry.getValue().getLabelId()+name,null,false,formDataBodyPart.getContentDisposition().getFileName());

                    try
                    {
                        try {
                            Streams.copy(formDataBodyPart.getValueAs(InputStream.class), fileItem.getOutputStream(), true);
                        } catch (FileUploadBase.FileUploadIOException var8) {
                            throw (FileUploadException)var8.getCause();
                        } catch (IOException var9) {
                            throw new FileUploadBase.IOFileUploadException("Processing of multipart/form-data request failed. " + var9.getMessage(), var9);
                        }

                    }
                    catch (FileUploadException e)
                    {
                        logger.error("FileUploadException::::",e);

                    }
                    items.add(fileItem);
                }
            }
        }

        return items;
    }

    //this is to get List of FileItem(DiskFileItem)
    public List<FileItem> getListOfFileItem(HttpServletRequest request)
            throws PZTechnicalViolationException
    {
        //FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //factory.setFileCleaningTracker(fileCleaningTracker);
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<AppFileDetailsVO> listOfFiles=new ArrayList<AppFileDetailsVO>();
        List<FileItem> items = null;

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart)
        {
            FileUploadException fileUploadException = new FileUploadException("MultiPartContent not available ",new Throwable("MultiPartContent not available"));
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "File request is not a  MultiPartContent", PZTechnicalExceptionEnum.IOEXCEPTION, null, fileUploadException.getMessage(), fileUploadException.getCause());
        }
        try
        {
            items = upload.parseRequest(request);
        }
        catch (FileUploadException e)
        {
            logger.error(" Error in creating xml data file ",e);
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "doMultipleFileUpload()", null, "Common", "Failure while uploading File in the system::", PZTechnicalExceptionEnum.IOEXCEPTION, null,e.getMessage(), e.getCause());
        }

        return items;
    }

    //this is to get FileDetails Vo per FileItem for File Type
    public AppFileDetailsVO doValidationOnFileItem(FileItem fileItem,List<AppUploadFileType> uploadFileTypes) throws PZTechnicalViolationException
    {

        AppFileDetailsVO fileDetailsVO =null;
        AppFileValidationVO fileValidationVO=multipleFileTypeCases(uploadFileTypes,fileItem);

        if(!fileValidationVO.isFileExtensionValidation())
        {
            fileDetailsVO=new AppFileDetailsVO();
            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(AppFileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("File is not a " + fileValidationVO.getTotalFileExtensionCheckOn() + " extension");
        }
        if(!fileValidationVO.isFileContentValidation() && fileValidationVO.isFileExtensionValidation())
        {
            fileDetailsVO=new AppFileDetailsVO();
            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath("");
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(AppFileActionType.VALIDATION);
            fileDetailsVO.setSuccess(false);
            fileDetailsVO.setReasonOfFailure("Invalid file type, Accepts only "+fileValidationVO.getFileType().toString()+" format");
        }
        return fileDetailsVO;
    }
    //upload file into server with dataPathFile as file path
    public AppFileDetailsVO uploadSingleFile(FileItem fileItem,String DataFilePath) throws Exception
    {
        boolean success;
        String temp=DataFilePath+"/";
        File directory = new File(temp);
        File savedFile=null;

        AppFileDetailsVO fileDetailsVO =new AppFileDetailsVO();
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
            fileDetailsVO.setFileActionType(AppFileActionType.UPLOAD);
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
        fileDetailsVO.setFileActionType(AppFileActionType.UPLOAD);
        fileDetailsVO.setSuccess(true);
        return fileDetailsVO;
    }

    //add case just as the inputValidator for validating the file content
    private AppFileValidationVO multipleFileTypeCases(List<AppUploadFileType> uploadFileTypeList,FileItem fileItem) throws PZTechnicalViolationException
    {
        AppFileValidationVO fileValidationVO= new AppFileValidationVO();
        fileValidationVO.setTotalFileExtensionCheckOn("."+uploadFileTypeList.toString());

        try
        {
            for (AppUploadFileType uploadFileType : uploadFileTypeList)
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
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(),"multipleFileTypeCases()",null,"common","No file found exception",PZTechnicalExceptionEnum.IOEXCEPTION,null,io.getMessage(),io.getCause());
        }
        return fileValidationVO;
    }

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
                    System.out.print(pathname.getName());
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

    //Excel Magic Header
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
                    System.out.print(pathname.getName());
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

    //PDF Magic Header
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

    //EXCEL Magic Header
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

    //PNG Magic Header
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

    //JPG Magic Header
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
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
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


    public AppFileDetailsVO uploadGatewayFile(FileItem fileItem,String DataFilePath) throws Exception
    {
        boolean success;
        File savedFile=null;

        AppFileDetailsVO fileDetailsVO = new AppFileDetailsVO();
        savedFile = new File(DataFilePath +"/"+ fileItem.getName());

        if (savedFile.exists())
        {
            fileDetailsVO.setFilename(fileItem.getName());
            fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
            fileDetailsVO.setFilePath(DataFilePath  + "/" + fileItem.getName());
            Calendar currentDate= Calendar.getInstance();
            fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
            fileDetailsVO.setTimestamp(timestamp.format(currentDate.getTime()));
            fileDetailsVO.setFileActionType(AppFileActionType.UPLOAD);
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
        fileDetailsVO.setFileActionType(AppFileActionType.UPLOAD);
        fileDetailsVO.setSuccess(true);
        return fileDetailsVO;
    }

    public boolean createFileFromBytes(byte[] bytes,String file) throws PZTechnicalViolationException
    {
        FileOutputStream outputStream=null;
        try
        {
            outputStream= new FileOutputStream(file);

            /*int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1)
            {*/
            outputStream.write(bytes);
            outputStream.flush();
            /*}*/
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(),"createFileFromInputStream()",null,"common","Exception while creating file",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(),"createFileFromInputStream()",null,"common","Exception while creating file",PZTechnicalExceptionEnum.IOEXCEPTION,null,e.getMessage(),e.getCause());
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

    public boolean fileExist(File file)
    {
        return file.exists();
    }

    //delete the file
    public boolean deleteFile(File oldFile)
    {
        return oldFile.delete();
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
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "getEncodedFieldValueWhileUpload()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return reader;
    }

    public PdfStamper getPdfStamper(PdfReader pdfReader,String pdfOutputSheet) throws PZTechnicalViolationException
    {
        PdfStamper stamper=null;
        try
        {
            stamper = new PdfStamper(pdfReader,new FileOutputStream(pdfOutputSheet));
        }
        catch (FileNotFoundException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.FILE_NOTFOUND_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (DocumentException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.DOCUMENT_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(AppFileHandlingUtil.class.getName(), "getPdfStamper()", null, "common", "Technical Exception", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return stamper;
    }

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
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(AppFileHandlingUtil.class.getName(),"getEncodedFieldValueWhileUpload()",null,"common","Technical Ezxception",PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null,e.getMessage(),e.getCause(),null,"While getting field value");
        }
        return value;
    }
}