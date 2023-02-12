package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.AppFileManager;
//import com.manager.GatewayManager;
import com.enums.ApplicationStatus;
import com.enums.BankApplicationStatus;
import com.utils.AppFileHandlingUtil;
import com.utils.FtpFileHandlingUtil;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.BankTypeVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CreatePartnerBankApplication extends HttpServlet
{
    private static Logger logger= new Logger(CreatePartnerBankApplication.class.getName());
    private static Functions functions = new Functions();
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String BANK_APPLICATION_PATH = applicationResourceBundle.getString("BANK_APPLICATION_PATH");

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //manager instance
        //GatewayManager gatewayManager = new GatewayManager();
        AppFileManager fileManager = new AppFileManager();
        ApplicationManager applicationManager = new ApplicationManager();
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        //Vo instance
        BankTypeVO bankTypeVO=null;
        BankApplicationMasterVO bankApplicationMasterVO=null;
        ApplicationManagerVO applicationManagerVO =new ApplicationManagerVO();
        AppFileDetailsVO fileDetailsVO=null;
        Map<String,AppFileDetailsVO> fileDetailsVOMap = new HashMap<String, AppFileDetailsVO>();
        Map<String,BankApplicationMasterVO> bankApplicationMasterVOMap = null;
        Map<String,List<BankApplicationMasterVO>> bankApplicationMasterListMap = null;
        List<BankApplicationMasterVO> bankApplicationMasterList = null;

        boolean createFile=true;
        RequestDispatcher rdError = request.getRequestDispatcher("/net/PartnerBankDetailList?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/net/PartnerBankDetailList?MES=Success&ctoken="+user.getCSRFToken());
        ValidationErrorList validationErrorList = new ValidationErrorList();
        String[] pgtypeids = request.getParameterValues("pgtypeid");
        try
        {
            logger.debug("ACTION--"+request.getParameter("action"));
            if("Generate PDF".equals(request.getParameter("action")))
            {
                logger.debug("inside Generate PDF");
                if(pgtypeids==null)
                {
                    validationErrorList.addError("Please select Bank for creation of template",new ValidationException("Please select Bank for creation of template","Please select Bank for creation of template::::"));
                    request.setAttribute("error",validationErrorList);
                    rdError.forward(request,response);
                    return;
                }
                else
                {
                    applicationManagerVO.setMemberId(request.getParameter("memberid"));
                    applicationManager.populateAppllicationData(applicationManagerVO);
                    bankApplicationMasterVO=new BankApplicationMasterVO();
                    bankApplicationMasterVO.setMember_id(applicationManagerVO.getMemberId());
                    bankApplicationMasterListMap=applicationManager.getBankApplicationMasterVOForGatewayId(bankApplicationMasterVO,"dtstamp desc",null);

                    fileHandlingUtil=fileManager.getFileHandlingUtilAccordingToTheProperty();

                    for(String pgtypeId: pgtypeids)
                    {

                        if(bankApplicationMasterListMap.containsKey(pgtypeId))
                        {
                            bankApplicationMasterList=bankApplicationMasterListMap.get(pgtypeId);
                            for(BankApplicationMasterVO bankApplicationMasterVO1:bankApplicationMasterList)
                            {

                                if(ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus()))
                                {
                                    createFile=false;
                                    break;
                                }
                                if(BankApplicationStatus.VERIFIED.name().equals(bankApplicationMasterVO1.getStatus()) || BankApplicationStatus.GENERATED.name().equals(bankApplicationMasterVO1.getStatus()))
                                {
                                    createFile=false;
                                    break;
                                }
                                else if (BankApplicationStatus.INVALIDATED.name().equals(bankApplicationMasterVO1.getStatus()))
                                    createFile=true;
                            }
                        }
                        if(createFile)
                        {
                            bankTypeVO = applicationManager.getBankTypeForPgTypeId(pgtypeId);

                            fileDetailsVO = fileManager.fillAcroFieldOfBankTemplate(applicationManagerVO, bankTypeVO,bankApplicationMasterListMap,fileHandlingUtil);

                        }
                        else
                        {
                            fileDetailsVO=new AppFileDetailsVO();
                            fileDetailsVO.setFieldName(pgtypeId);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure(ApplicationStatus.MODIFIED.name().equals(applicationManagerVO.getStatus())?"Application of Member is in Modified Status":"Latest PDF has already been verified/generated");
                        }
                        fileDetailsVOMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                    }

                    request.setAttribute("fileDetailsVOMap",fileDetailsVOMap);
                    rdSuccess.forward(request,response);
                    return;
                }
            }
            else if ("Verified PDF".equals(request.getParameter("action")))
            {
                logger.debug("inside Verified PDF");
                if (pgtypeids == null)
                {
                    validationErrorList.addError("Please select Bank for Verification Of template", new ValidationException("Please select Bank for Verification Of template", "Please select Bank for Verification Of template"));
                    request.setAttribute("error", validationErrorList);
                    rdError.forward(request, response);
                    return;
                }
                else
                {
                    bankApplicationMasterVO = new BankApplicationMasterVO();
                    bankApplicationMasterVO.setMember_id(applicationManagerVO.getMemberId());
                    bankApplicationMasterListMap = applicationManager.getBankApplicationMasterVOForGatewayId(bankApplicationMasterVO, "dtstamp desc", null);
                    for (String pgtypeId : pgtypeids)
                    {
                        if (bankApplicationMasterListMap.containsKey(pgtypeId) && !BankApplicationStatus.VERIFIED.name().equals(bankApplicationMasterListMap.get(pgtypeId).get(0).getStatus()))
                        {
                            bankApplicationMasterVO = new BankApplicationMasterVO();
                            applicationManagerVO.setMemberId(request.getParameter("memberid"));
                            bankApplicationMasterVO.setPgtypeid(pgtypeId);
                            bankApplicationMasterVO.setMember_id(applicationManagerVO.getMemberId());
                            bankApplicationMasterVO.setStatus(BankApplicationStatus.VERIFIED.name());
                            applicationManager.updateBankApplicationMasterVO(bankApplicationMasterVO, bankApplicationMasterListMap.get(pgtypeId).get(0).getBankapplicationid(), bankApplicationMasterVO.getMember_id());
                            fileDetailsVO = new AppFileDetailsVO();
                            fileDetailsVO.setFieldName(pgtypeId);
                            fileDetailsVO.setSuccess(true);
                            fileDetailsVO.setReasonOfFailure("Success");
                        }
                        else if(bankApplicationMasterListMap.containsKey(pgtypeId) && BankApplicationStatus.VERIFIED.name().equals(bankApplicationMasterListMap.get(pgtypeId).get(0).getStatus()))
                        {
                            fileDetailsVO = new AppFileDetailsVO();
                            fileDetailsVO.setFieldName(pgtypeId);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("Latest PDF has already been verified");
                        }
                        else
                        {
                            fileDetailsVO = new AppFileDetailsVO();
                            fileDetailsVO.setFieldName(pgtypeId);
                            fileDetailsVO.setSuccess(false);
                            fileDetailsVO.setReasonOfFailure("Application file not present to be verified");
                        }
                        fileDetailsVOMap.put(fileDetailsVO.getFieldName(), fileDetailsVO);
                    }
                    request.setAttribute("fileDetailsVOMap",fileDetailsVOMap);
                    rdSuccess.forward(request, response);
                    return;
                }
            }
            else /*if (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("|View"))*/
            {
                logger.debug("inside Download PDF");
                    /*bankApplicationMasterVO = new BankApplicationMasterVO();
                    bankApplicationMasterVO.setBankapplicationid(request.getParameter("action").split("_")[0]);
                    bankApplicationMasterVOMap = applicationManager.getBankApplicationMasterVO(bankApplicationMasterVO);
                    File file = null;
                    fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();
                    file = fileManager.getBankGenerateTemplate(bankApplicationMasterVOMap.get(bankApplicationMasterVO.getBankapplicationid()), fileHandlingUtil);
                    fileHandlingUtil.openPdfFile(file, response);
                    fileManager.deleteFileAccordingToFTPProperty(file, null, (FtpFileHandlingUtil) fileHandlingUtil);
                    return;*/

                    /*String mappingid[] = request.getParameter("action").split("\\|");
                    String fileName = mappingid[2];*/
                String fileName = request.getParameter("fileName");
                String filePath = BANK_APPLICATION_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZTechnical Exception ",e);
            PZExceptionHandler.handleTechicalCVEException(e, null, "While creation of the Application");
        }
        catch (Exception e)
        {
            logger.error("PZTechnical Exception ",e);
        }
        finally
        {
            if(fileHandlingUtil instanceof FtpFileHandlingUtil)
            {
                try
                {
                    fileManager.closeFtpConnection(null,true,(FtpFileHandlingUtil)fileHandlingUtil);
                }
                catch (PZTechnicalViolationException e)
                {
                    logger.error("PZTechnicalViolation Exception while closing the FTP Connection:::",e);
                }
            }
        }
    }
}
