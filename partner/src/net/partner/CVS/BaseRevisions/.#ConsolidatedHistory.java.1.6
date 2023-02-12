package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.vo.ActionVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.owasp.esapi.ESAPI;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 07-Apr-17.
 */
public class ConsolidatedHistory extends HttpServlet
{
    private static Logger logger= new Logger(ConsolidatedHistory.class.getName());
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        Functions functions =new Functions();

        ValidationErrorList validationErrorList=null;

        //ConsolidatedApplicationVO consolidatedApplicationVO = new ConsolidatedApplicationVO();

        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap =null;

        ApplicationManager applicationManager = new ApplicationManager();
        ActionVO actionVO = new ActionVO();
        String partnerId = (String) session.getAttribute("merchantid");


        RequestDispatcher rdError = request.getRequestDispatcher("/consolidatedHistory.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/consolidatedHistory.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try
        {
            validationErrorList=validateOptionalParameter(request);

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("errorC",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            String MemberID=request.getParameter("memberid");
            String Name=request.getParameter("gateway");
            String pid=request.getParameter("partnerid");
            String consolidatedId=request.getParameter("consolidatedId");
            if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"), "Numbers", 20, false))
            {
                request.setAttribute("errorC","Invalid Member Id");
                rdError.forward(request,response);
                return;
            }
            if (functions.isValueNull(pid)){
                consolidatedApplicationVOMap=applicationManager.getconsolidated_applicationHistoryForMappedMembers(MemberID, Name, consolidatedId,pid);

            }else{
                consolidatedApplicationVOMap=applicationManager.getconsolidated_applicationHistoryForSuperPartner(MemberID, Name, consolidatedId, partnerId);
            }

            if(consolidatedApplicationVOMap!=null && consolidatedApplicationVOMap.size()>0)
            {
                logger.debug("Query---------- "+consolidatedApplicationVOMap);
                request.setAttribute("consolidatedApplicationVOMap", consolidatedApplicationVOMap);
                rdSuccess.forward(request,response);
                return;
            }
            else
            {
                request.setAttribute("errorC","Invalid Member Id");
                rdError.forward(request,response);
                return;
            }


        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }

    }

    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    private ValidationErrorList validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.CONSOLIDATEDID);

        ValidationErrorList validationErrorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,validationErrorList,true);
        return validationErrorList;
    }

}
