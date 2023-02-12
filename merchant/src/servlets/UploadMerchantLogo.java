import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.TransactionLogger;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PartnerManager;
import com.manager.enums.FileActionType;
import com.manager.enums.FunctionalUsage;
import com.manager.enums.TemplatePreference;
import com.manager.enums.UploadFileType;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.FileDetailsVO;
import com.manager.vo.fileRelatedVOs.UploadLabelVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import org.apache.commons.fileupload.FileItem;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Sanjay on 2/23/2022.
 */
public class UploadMerchantLogo extends HttpServlet
{
    private Logger logger = new Logger(UploadMerchantLogo.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(UploadMerchantLogo.class.getName());
    private Functions functions = new Functions();
    //private static String log_store = ApplicationProperties.getProperty("LOG_STORE");
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        Merchants merchants = new Merchants();

        String errorMsg = "";
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        //Manager Instance
        FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
        PartnerManager partnerManager = new PartnerManager();

        List<FileItem> fileItems=null;
        Map<String,UploadLabelVO> uploadLabelVOMap=null;
        List<UploadFileType> uploadFileTypes = new ArrayList<UploadFileType>();
        Map<String,Object> merchantTemplateInformationUpdate=new HashMap<String, Object>();


        FileDetailsVO fileDetailsVO = null;

        String memberId=null;
        String pid=null;
        boolean saved=false;


        RequestDispatcher rdsuccess=request.getRequestDispatcher("/addMerchantLogo.jsp?MES=SUCCESS&ctoken="+user.getCSRFToken());
        RequestDispatcher rderror=request.getRequestDispatcher("/addMerchantLogo.jsp?MES=ERR&ctoken="+user.getCSRFToken());


        try
        {

            fileItems= fileHandlingUtil.getListOfFileItem(request);
            for (FileItem fileItem: fileItems)
            {
                logger.debug("FIle item: "+fileItem.getFieldName());
                if (fileItem.getSize()>0 && !fileItem.isFormField() && (fileItem.getFieldName()).equals(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString()))
                {

                    try
                    {
                        if (!ESAPI.validator().isValidInput("memberid", memberId, "OnlyNumber", 10, true))
                        {
                            request.setAttribute("error","Invalid Merchant id");
                            rderror.forward(request,response);
                            return;
                        }
                        if (!functions.isValueNull(memberId) || memberId.equals(null) || memberId.equals(""))
                        {
                            request.setAttribute("error","Please enter Merchant Id");
                            rderror.forward(request,response);
                            return;
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("exception while processing with merchant id: "+e.getMessage());
                    }

                    uploadLabelVOMap = partnerManager.getListOfUploadLabel(FunctionalUsage.MERCHANTLOGONAME.toString());
                    if (uploadLabelVOMap!= null && uploadLabelVOMap.containsKey(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name()))
                    {
                        UploadLabelVO uploadLabelVO= uploadLabelVOMap.get(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.name());
                        if (functions.isValueNull(uploadLabelVO.getSupportedFileType()))
                        {
                            for (String filetype: uploadLabelVO.getSupportedFileType().split(","))
                            {
                                if (UploadFileType.PDF.toString().equalsIgnoreCase(filetype))
                                {
                                    uploadFileTypes.add(UploadFileType.PDF);
                                }
                                if (UploadFileType.EXCEL.toString().equalsIgnoreCase(filetype))
                                {
                                    uploadFileTypes.add(UploadFileType.EXCEL);
                                }
                                if (UploadFileType.JPG.toString().equalsIgnoreCase(filetype))
                                {
                                    uploadFileTypes.add(UploadFileType.JPG);
                                }
                                if (UploadFileType.PNG.toString().equalsIgnoreCase(filetype))
                                {
                                    uploadFileTypes.add(UploadFileType.PNG);
                                }
                            }
                        }
                    }

                    fileDetailsVO= fileHandlingUtil.doValidationOnFileItem(fileItem,uploadFileTypes);
                    logger.debug("FIle Size:: "+fileItem.getSize());
                    if (fileDetailsVO==null && fileItem.getSize()>60000)
                    {
                        fileDetailsVO= new FileDetailsVO();
                        fileDetailsVO.setFilename(fileItem.getName());
                        fileDetailsVO.setFileType(getFileExtension(fileItem.getName()));
                        fileDetailsVO.setFilePath("");
                        Calendar currentDate= Calendar.getInstance();
                        fileDetailsVO.setDtstamp(Functions.converttomillisec(String.valueOf(currentDate.get(Calendar.MONTH)), String.valueOf(currentDate.get(Calendar.DATE)), String.valueOf(currentDate.get(Calendar.YEAR)), String.valueOf(currentDate.get(Calendar.HOUR)), String.valueOf(currentDate.get(Calendar.MINUTE)), String.valueOf(currentDate.get(Calendar.SECOND))));
                        fileDetailsVO.setTimestamp("");
                        fileDetailsVO.setFileActionType(FileActionType.VALIDATION);
                        fileDetailsVO.setSuccess(false);
                        fileDetailsVO.setReasonOfFailure("file exceeds than 60 kb of size");
                    }

                    if (fileDetailsVO==null)
                    {
                        byte[] imgData= fileItem.get();
                        merchantTemplateInformationUpdate.put(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString(),imgData);
                        merchantTemplateInformationUpdate.put(TemplatePreference.MERCHANTLOGONAME.toString(),"merchant_"+memberId+"."+getFileExtension(fileItem.getName().toString()));
                        String merchant_uploaded_path= null;

                        try
                        {
                            merchant_uploaded_path= ApplicationProperties.getProperty("MERCHANT_LOGO_PATH");
                        }
                        catch (Exception e){
                            logger.error("exception while upload file:", e);
                        }
                        File oriFile=new File(merchant_uploaded_path+"/merchant_"+memberId+"."+getFileExtension(fileItem.getName().toString()));
                        File fileJpg=new File(merchant_uploaded_path+"/merchant_"+memberId+".jpg");
                        File filePng=new File(merchant_uploaded_path+"/merchant_"+memberId+".png");

                        transactionLogger.debug("oriFile UploadMerchantLogo--"+oriFile);
                        transactionLogger.debug("filePng UploadMerchantLogo--"+filePng);
                        transactionLogger.debug("fileJpg UploadMerchantLogo--"+fileJpg);

                        if (fileJpg.exists())
                        {
                            fileJpg.delete();
                        }
                        if(filePng.exists())
                        {
                            filePng.delete();
                        }

                        try
                        {
                            fileItem.write(oriFile);
                        }
                        catch (PZTechnicalViolationException e)
                        {
                            logger.error("exception while creating file",e);
                        }
                        catch (Exception e)
                        {
                            logger.error("exception while creating file",e);
                        }
                    }
                    else
                    {
                        request.setAttribute("cbmessage",fileDetailsVO.getReasonOfFailure());
                        rderror.forward(request, response);
                        return;
                    }
                }
                else if(fileItem.getSize()>0 && fileItem.isFormField() && "memberid".equals(fileItem.getFieldName()))
                {
                    memberId=fileHandlingUtil.getEncodedFieldValueWhileUpload(fileItem);
                }
                fileItem.delete();
            }
            Map<String,Object> presentTemplateDetails=partnerManager.getSavedMemberTemplateDetails(memberId);

            if(  presentTemplateDetails!=null && presentTemplateDetails.containsKey(TemplatePreference.TRANSACTIONPAGEMERCHANTLOGO.toString()) && merchantTemplateInformationUpdate.size()>0)
            {
                saved=partnerManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate, memberId);

            }
            else if( merchantTemplateInformationUpdate.size()>0)
            {
                saved=partnerManager.insertMemberTemplateDetails(merchantTemplateInformationUpdate, memberId);
            }
            else
            {
                PZExceptionHandler.raiseConstraintViolationException(UploadMerchantLogo.class.getName(), "Upload Merchant Logo", null, "common", "Error while Uploading Logo", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, "Error while Uploading Logo", new Throwable("Error while Uploading Logo"));
            }

            if(saved)
            {
                request.setAttribute("cbmessage","Updated Successfully");
                rdsuccess.forward(request,response);
                return;
            }
            else
            {
                request.setAttribute("error","please try Again");
                rderror.forward(request,response);
                return;
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PzConstraintViolationException while uploading the logo", e);
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnicalViolationException while uploading the logo",e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDBViolationException while uploading the logo", e);
        }
        catch (Exception e)
        {
            logger.debug("Exception:::::"+e);
        }

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
}
