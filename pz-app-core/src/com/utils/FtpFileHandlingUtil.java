package com.utils;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.enums.AppFileActionType;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.vo.applicationManagerVOs.FTPServiceDetailsVO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Created by NIKET on 4/9/2016.
 */
public class FtpFileHandlingUtil extends AppFileHandlingUtil
{
    private static Logger logger = new Logger(FtpFileHandlingUtil.class.getName());

    private static final SimpleDateFormat timestamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Functions functions = new Functions();

    private FTPServiceDetailsVO ftpServiceDetailsVO = null;

    private FTPClient ftpClient = null;

    public FTPServiceDetailsVO getFtpServiceDetailsVO()
    {
        return ftpServiceDetailsVO;
    }

    public void setFtpServiceDetailsVO(FTPServiceDetailsVO ftpServiceDetailsVO)
    {
        this.ftpServiceDetailsVO = ftpServiceDetailsVO;
    }

    public FTPClient getFtpClient()
    {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient)
    {
        this.ftpClient = ftpClient;
    }


    //BELOW This is the actual DOCUMENTS Server FTP actions


    public AppFileDetailsVO uploadSingleFile(FileItem fileItem, String DataFilePath) throws Exception
    {
        boolean success;
        String temp=DataFilePath+"/";
        File directory = new File(temp);
        File savedFile=null;

        AppFileDetailsVO fileDetailsVO =new AppFileDetailsVO();

        //File For Creation
        savedFile = new File(temp+fileItem.getName());
        //Make Directory if not present
        makeDirectory(savedFile.getAbsolutePath().replaceAll("\\\\","/"));

        if (fileExist(savedFile))
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
        InputStream fileItemInputStream=null;
        try
        {
            fileItemInputStream = fileItem.getInputStream();
            boolean created=ftpClient.storeFile(temp + fileItem.getName(), fileItemInputStream);
            showServerReply(ftpClient);
            logger.debug("CREATED ::::"+created);
        }
        finally
        {
            fileItemInputStream.close();
        }
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


    public boolean makeDirectory(String directoryPath)
    {
        boolean success=false;

        String parentDirectory=getParentDirectory(directoryPath);

        String oldFilePath = identifyTheFileLocation(parentDirectory);
        parentDirectory = getRemovedAbsolutePathLocation(parentDirectory, oldFilePath);

        try
        {
            FTPFile[] ftpFiles=this.ftpClient.listFiles(parentDirectory);

            for(FTPFile ftpFile:ftpFiles)
            {
                logger.debug("FTPFILE::::"+ftpFile.getName()+" LINK:::"+ftpFile.getLink());
            }

            if (ftpFiles.length>0)
            {
                return true;
            }
            else
            {
                success= ftpClient.makeDirectory(parentDirectory);
                showServerReply(ftpClient);
            }
        }
        catch (IOException e)
        {
            logger.error("Exception While Connecting to Document Server",e);
        }

        return success;
    }

    public File renameFile(File oldFile, File newFile)
    {
        if(oldFile!=null && newFile!=null)
        {
            String oldFilePath = identifyTheFileLocation(oldFile.getAbsolutePath());
            String newFilePath = identifyTheFileLocation(newFile.getAbsolutePath());
            String ftpOldFilePath = getRemovedAbsolutePathLocation(oldFile.getAbsolutePath().replaceAll("\\\\", "/"), oldFilePath);
            String ftpNewFilePath = getRemovedAbsolutePathLocation(newFile.getAbsolutePath().replaceAll("\\\\", "/"), newFilePath);

            try
            {
                this.ftpClient.rename(ftpOldFilePath, ftpNewFilePath);
            }
            catch (IOException e)
            {
                logger.error("Exception While Connecting to Document Server", e);
            }
        }
        return oldFile;
    }


    public boolean deleteFile(File oldFile)
    {
        if(oldFile!=null)
        {
            String oldFilePath = identifyTheFileLocation(oldFile.getAbsolutePath());
            String ftpOldFilePath = getRemovedAbsolutePathLocation(oldFile.getAbsolutePath().replaceAll("\\\\", "/"), oldFilePath);

            try
            {
                ftpClient.deleteFile(ftpOldFilePath);
            }
            catch (IOException e)
            {
                logger.error("Exception While Connecting to Document Server", e);
            }
        }
        return false;
    }


    public boolean fileExist(File file)
    {
        if(file!=null)
        {
            String propertyValue=identifyTheFileLocation(file.getAbsolutePath());

            if(functions.isValueNull(propertyValue))
            {
                String ftpURLPath=getRemovedAbsolutePathLocation(file.getAbsolutePath().replaceAll("\\\\","/"),propertyValue);

                try
                {
                    FTPFile[] ftpFiles=this.ftpClient.listFiles(ftpURLPath);

                    if(ftpFiles.length>0)
                    {
                        return true;
                    }
                }
                catch (IOException e)
                {
                    logger.error("IOException While checking the file exist in the DOCUMENT SERVER",e);
                }
            }
        }
        return false;
    }

    public File downloadFileFromPathTo(String fromPath,String toPath)
    {
        OutputStream fileOutputStream=null;
        File temporaryFile=null;
        try
        {
            String parentDirectory=getParentDirectory(toPath);
            boolean created=super.makeDirectory(parentDirectory);
            temporaryFile=new File(toPath);

            fileOutputStream=new FileOutputStream(temporaryFile);
            boolean downloaded=this.ftpClient.retrieveFile(fromPath,fileOutputStream);

        }
        catch (FileNotFoundException e)
        {
            logger.error("FileNotFoundException while getting the file from the location",e);
        }
        catch (IOException e)
        {
            logger.error("IO while getting the file from the location", e);
        }
        finally
        {
            if(fileOutputStream!=null)
            {
                try
                {
                    fileOutputStream.close();
                }
                catch (IOException e)
                {
                    logger.error("IO while closing the outputStream",e);
                }
            }
        }
        return temporaryFile;
    }

    public boolean ftpFileAndDeleteFile(File transferFile,String ftpLocation)
    {
        boolean stored=false;
        if(transferFile!=null && super.fileExist(transferFile) && functions.isValueNull(ftpLocation))
        {
            //String parentDirectory=getParentDirectory(ftpLocation);
            boolean created=makeDirectory(ftpLocation);
            InputStream inputStream=null;

            try
            {
                inputStream= new FileInputStream(transferFile);
                stored=this.ftpClient.storeFile(ftpLocation,inputStream);

            }
            catch (FileNotFoundException e)
            {
                logger.error("FileNotFoundException while transferring File through ftp",e);
            }
            catch (IOException e)
            {
                logger.error("IO Exception while transferring File through ftp",e);
            }
            finally
            {
                if(inputStream!=null)
                {
                    try
                    {
                        inputStream.close();
                    }
                    catch (IOException e)
                    {
                        logger.error("IO Exception while closing Input stream ",e);
                    }
                }
            }

            if(stored)
            {
                super.deleteFile(transferFile);
            }
        }

        return stored;
    }

    public FTPClient getFtpConnection() throws PZTechnicalViolationException
    {
        FTPClient ftpClient = null;

        if(ftpServiceDetailsVO!=null)
        {
            if(!functions.isValueNull(ftpServiceDetailsVO.getHost()) || (ftpServiceDetailsVO.getPort()==0) ||!functions.isValueNull(ftpServiceDetailsVO.getUsername()) ||!functions.isValueNull(ftpServiceDetailsVO.getPassword()))
            {
                PZExceptionHandler.raiseTechnicalViolationException(FtpFileHandlingUtil.class.getName(),"getFtpConnection()","host,port,username,password","COMMON","Please try again later after some time", PZTechnicalExceptionEnum.DOCUMENTATION_SERVER_CREDENTIAL_NOTFOUND,null,"Please try again later after some time",new Throwable("Please try again later after some time"));
            }
            else
            {
                try
                {
                    ftpClient = new FTPClient();
                    ftpClient.connect(ftpServiceDetailsVO.getHost(), ftpServiceDetailsVO.getPort());
                    showServerReply(ftpClient);
                    int replyCode = ftpClient.getReplyCode();
                    if (!FTPReply.isPositiveCompletion(replyCode)) {
                        logger.debug("Operation failed. Server reply code: " + replyCode);
                    }
                    logger.debug("FTP USERNAME:::"+ftpServiceDetailsVO.getUsername()+" PASSWORD:::"+ftpServiceDetailsVO.getPassword());
                    boolean success=ftpClient.login(ftpServiceDetailsVO.getUsername(), ftpServiceDetailsVO.getPassword());
                    showServerReply(ftpClient);
                    if (!success) {
                        logger.debug("Could not login to the server");
                    }


                    ftpClient.enterLocalPassiveMode();
                    showServerReply(ftpClient);


                    logger.debug("WORKING DIRECTORY:::::"+ftpClient.printWorkingDirectory()+" \nTIME OUT:::"+ftpClient.getConnectTimeout()+" CONNECTED:::"+ftpClient.isConnected());
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    showServerReply(ftpClient);
                }
                catch (SocketException e)
                {
                    logger.error("SOCKET Exception While socket Connection in Documentation Server:::",e);
                    PZExceptionHandler.raiseTechnicalViolationException(FtpFileHandlingUtil.class.getName(),"getFtpConnection()","host,port,username,password","COMMON","Please try again later after some time", PZTechnicalExceptionEnum.DOCUMENTATION_SERVER_CONNECTION_EXCEPTION,null,"Please try again later after some time",new Throwable("Please try again later after some time"));
                }
                catch (IOException e)
                {
                    logger.error("IOException While  Connection in Documentation Server:::",e);
                    PZExceptionHandler.raiseTechnicalViolationException(FtpFileHandlingUtil.class.getName(), "getFtpConnection()", "host,port,username,password", "COMMON", "Please try again later after some time", PZTechnicalExceptionEnum.DOCUMENTATION_SERVER_CONNECTION_EXCEPTION, null, "Please try again later after some time", new Throwable("Please try again later after some time"));
                }
            }
        }
        else
        {
            PZExceptionHandler.raiseTechnicalViolationException(FtpFileHandlingUtil.class.getName(),"getFtpConnection()","host,port,username,password","COMMON","Please try again later after some time", PZTechnicalExceptionEnum.DOCUMENTATION_SERVER_CREDENTIAL_NOTFOUND,null,"Please try again later after some time",new Throwable("Please try again later after some time"));
        }
        this.ftpClient=ftpClient;
        return ftpClient;
    }

    private void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                logger.debug("FTP ["+ftpClient.getPassiveHost()+":"+ftpClient.getPassivePort()+"] :" + aReply);
            }
        }
    }

    public boolean closeConnection(FTPClient ftpClient)
    {
        try {
            if (ftpClient!=null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                return true;
            }
        }
        catch (IOException ex) {
            logger.error("Exception while closing FTP connection",ex);
            return false;
        }
        return true;
    }

    private String getParentDirectory(String filePath)
    {
        String parentFile=null;
        logger.debug("PARENT FILE PATH:::"+filePath);
        if(filePath!=null)
        {
            int ext=filePath.trim().lastIndexOf("/");
            if(ext==filePath.length()-1)
            {
                /*filePath=filePath.substring(0,ext);
                ext=filePath.trim().lastIndexOf("/");*/
                parentFile=filePath.substring(0,ext);
            }
            else
                parentFile=filePath.substring(0,ext);
        }
        logger.debug("PARENT DIRECTORY::::"+parentFile);
        return parentFile;

    }

    private String getRemovedAbsolutePathLocation(String filePath,String tokenizer)
    {
        String exactFileLocation=null;
        logger.debug("TOKENIZER::::"+tokenizer);
        logger.debug("FILE PATH::::"+filePath);
        if(filePath!=null)
        {
            int ext=filePath.trim().lastIndexOf(tokenizer);
            exactFileLocation=filePath.substring(ext,filePath.length());
        }
        logger.debug("EXACT FILE LOCATION:::"+exactFileLocation);
        return exactFileLocation;

    }

    /**
     * Please Alter this if more file has to be ftp other than application manager
     * @param filePath
     * @return
     */
    private String identifyTheFileLocation(String filePath)
    {
        ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        Enumeration<String> keys=resourceBundle.getKeys();

        logger.debug("IDENTIFICATION FILE LOCATION FILE PATH:::"+filePath);
        while(keys.hasMoreElements())
        {

            String key=keys.nextElement();
            String value=resourceBundle.getString(key);
            String invertedValue="";

            logger.debug("FILEPATH CONTAINS \\\\::::"+filePath.contains("\\"));
            if(filePath.contains("\\"))
            {
                invertedValue = value.replaceAll("/", "\\\\");
            }
            else
            {
                invertedValue=value;
            }
            logger.debug("SEARCHED VALUE:::"+value);
            logger.debug(filePath+"=="+invertedValue);
            logger.debug("CONTAINS:::"+filePath.contains(invertedValue));
            logger.debug(filePath+"\\"+"CONTAINS2:::"+filePath+"\\".contains(invertedValue));
            logger.debug(filePath+"/"+"CONTAINS3:::"+filePath+"/".contains(invertedValue));
            if(filePath.contains(invertedValue))
            {
                logger.debug("VALUE:::::"+value);
                return value;
            }
            else if((filePath+"\\").contains(invertedValue))
            {
                logger.debug("VALUE2:::::"+value);
                return value;
            }
            else if((filePath+"/").contains(invertedValue))
            {
                logger.debug("VALUE3:::::"+value);
                return value;
            }
        }


       return null;
    }

    public static void main(String[] args)
    {
        FtpFileHandlingUtil ftpFileHandlingUtil = new FtpFileHandlingUtil();

        //System.out.println(ftpFileHandlingUtil.getParentDirectory("/DocumentServer/jjj/kkkk"));

        ResourceBundle resourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");

        if("true".equalsIgnoreCase(resourceBundle.getString("STARTFTP")))
        {
            FTPServiceDetailsVO ftpServiceDetailsVO = new FTPServiceDetailsVO();

            ftpServiceDetailsVO.setHost(resourceBundle.getString("host"));
            ftpServiceDetailsVO.setPort(Integer.valueOf(resourceBundle.getString("port")));
            ftpServiceDetailsVO.setUsername(resourceBundle.getString("username"));
            ftpServiceDetailsVO.setPassword(resourceBundle.getString("password"));


            //order is important
            ftpFileHandlingUtil.setFtpServiceDetailsVO(ftpServiceDetailsVO);//credential is been set
        }
        FTPClient ftpClient=null;
        try
        {
            ftpClient=ftpFileHandlingUtil.getFtpConnection();

            ftpClient.makeDirectory("/DOCUMENTATIONSERVER/applicationManager/AppDocument/10134");
        }
        catch (PZTechnicalViolationException e)
        {

        }
        catch (IOException e)
        {

        }
        finally
        {
            ftpFileHandlingUtil.closeConnection(ftpClient);
        }

    }

}
