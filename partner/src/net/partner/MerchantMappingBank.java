package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
//import com.manager.ApplicationManager;
import com.manager.PartnerManager;
import com.manager.ProfileManagementManager;
import com.manager.dao.PartnerDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.manager.vo.userProfileVOs.MerchantVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: NIKET
 * Date: 9/04/15
 * Time: 1:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantMappingBank extends HttpServlet
{
    private static Logger logger = new Logger(MerchantMappingBank.class.getName());

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = Functions.getNewSession(request);

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //Manager Instance
//        ApplicationManager applicationManager= new ApplicationManager();
        //Error List
        ValidationErrorList validationErrorList = null;

        Map<String, GatewayTypeVO> gatewayTypeVOs =null;


        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantmappingbank.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError=request.getRequestDispatcher("/merchantmappingbank.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        /*try
        {*/

        validationErrorList = validateOptionParameter(request);

        if (!validationErrorList.isEmpty())
        {
            request.setAttribute("error", validationErrorList);
            rdError.forward(request, response);
            return;
        }

        //getting Members & Bank Mapping according to Member
//            gatewayTypeVOs=applicationManager.getBankMerchantMappingDetails(session.getAttribute("merchantid").toString(), request.getParameter("toid"));

        //request.setAttribute("isMemberMappedWithPartner",isMemberMappedWithPartner);

        request.setAttribute("memberGatewayMap",gatewayTypeVOs);
       /* }
        catch (PZDBViolationException e)
        {
            logger.error("SQL exception",e);
            PZExceptionHandler.handleDBCVEException(e, session.getAttribute("merchantid").toString(), "Exception While getting Merchant Bank Mapping Details List.");
            request.setAttribute("catchError","Kindly check on for the Merchant Bank Mapping after some time");
            rdError.forward(request,response);
            return;
        }*/


        rdSuccess.forward(request,response);
        return;

    }

    public void  doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request,response);
    }

    public ValidationErrorList validateOptionParameter(HttpServletRequest request)
    {
        ValidationErrorList validationErrorList = new ValidationErrorList();
        List<InputFields> inputOptionalParameter = new ArrayList<InputFields>();

        inputOptionalParameter.add(InputFields.TOID);

        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,inputOptionalParameter,validationErrorList,false);

        return validationErrorList;
    }
}


