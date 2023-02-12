import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.enums.ConsolidatedAppStatus;
import com.manager.AppFileManager;
import com.manager.ApplicationManager;
import com.manager.vo.ActionVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

//import com.manager.vo.gatewayVOs.GatewayTypeVO;

/**
 * Created by admin on 07-Apr-17.
 */
public class ConsolidatedHistoryAction extends HttpServlet
{

    private static Logger logger =new Logger(ConsolidatedHistoryAction.class.getName());
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String APPLICATION_ZIP_PATH=applicationResourceBundle.getString("APPLICATION_ZIP_PATH");
    private Functions functions = new Functions();

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        File f = new File(filepath);
        int length = 0;

        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
        /*File file = new File(filepath);
        file.delete();*/
        logger.info("Successful#######");
        return true;
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        logger.debug("inside 0000000000........");

        HttpSession session = functions.getNewSession(request);

        Admin admin=new Admin();
        if (!admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        ApplicationManager applicationManager=new ApplicationManager();
        //VO
        ActionVO actionVO = new ActionVO();
        ConsolidatedApplicationVO consolidatedApplicationVO=new ConsolidatedApplicationVO();
        //GatewayTypeVO gatewayTypeVO=null;
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        AppFileDetailsVO fileDetailsVO=new AppFileDetailsVO();
        AppFileManager fileManager=new AppFileManager();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = null;

                //Enum instance
        ConsolidatedAppStatus consolidatedAppStatus=null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;
        //Boolean
        boolean isUpdated=false;
        boolean isDeleted=false;


        String directoryPath="";
        String ftpPath="";
        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/consolidatedHistory.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/consolidatedHistory.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        logger.debug("inside 1111111........");
        try
        {
            if (functions.isValueNull(request.getParameter("action")))
            {
                /*validationErrorList = validateMandatorParameter(request);
                if (!validationErrorList.isEmpty())
                {
                    logger.error("validation error");
                    request.setAttribute("error", validationErrorList);
                    rdError.forward(request, response);
                    return;
                }*/
                actionVO.setAllContentAuto(request.getParameter("action"));
            }

            if(actionVO.isDelete())
            {
                String MemberID=request.getParameter("memberid");
                String Name=request.getParameter("gateway");
                String consolidatedId=request.getParameter("consolidatedId");
                String filename = request.getParameter("filename");
                logger.debug("consolidated ID...."+consolidatedApplicationVO.getConsolidated_id());
                logger.debug("Member ID.........."+request.getParameter("memberid"));

                //deleting Zip in consolidated ZIp Folder

                fileDetailsVOMap = applicationManager.deleteConsolidatedZip(consolidatedApplicationVO.getConsolidated_id(), request.getParameter("memberid"), request,filename);
                isDeleted = applicationManager.deleteConsolidated_application_History(actionVO.getActionCriteria().split("_")[0]);
                if (isDeleted){
                    request.setAttribute("message","consolidated Application is deleted successfully");
                }
            }
            else if(isUpdated)
                rdSuccess=request.getRequestDispatcher("/servlet/ManageBankApp?consolidatedId=" + actionVO.getActionCriteria().split("_")[0] + "&MES=SUCCESS&ctoken=" + user.getCSRFToken());

            else if (!functions.isValueNull(request.getParameter("action")) || (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("_Download")))
            {
                String fileName = request.getParameter("fileName");
                String filePath = APPLICATION_ZIP_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("catchError","Kindly check for the consolidated Id");
                rdError.forward(request, response);
                return;
            }

            request.setAttribute("isUpdated",isUpdated);
            request.setAttribute("actionVO",actionVO);
            session.setAttribute("actionVO",actionVO);
            rdSuccess.forward(request,response);
        }

        catch (PZTechnicalViolationException e)
        {
            logger.error("Technical exception while getting Consolidated App Status", e);
            PZExceptionHandler.handleTechicalCVEException(e, null, null);
            request.setAttribute("catchError", "invalid Status");
            rdError.forward(request, response);
            return;
        }
        /*catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while getting Consolidated App Status", e);
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("catchError", "Invalid Status");
            rdError.forward(request, response);
            return;
        }*/
        catch (Exception e)
        {
           logger.error("Catch Exception...",e);
        }
    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request, response);
    }

    private ValidationErrorList validateMandatorParameter(HttpServletRequest request)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);

        return validationErrorList;
    }
}
