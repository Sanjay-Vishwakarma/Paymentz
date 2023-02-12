

import com.directi.pg.*;
import com.directi.pg.Functions;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TerminalManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class CopyTerminal extends HttpServlet
{
    private static Logger logger = new Logger(CopyTerminal.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info(":::::: CopyTerminal Start :::::: ");
        HttpSession session         = request.getSession();
        User user                   = (User) session.getAttribute("ESAPIUserSessionKey");
        boolean flag                = true;
        String errormsg             = "";
        String EOL                  = "<BR>";
        MerchantDAO merchantDAO     = new MerchantDAO();
        Functions functions         = new Functions();
        String error                = "";
        String memberid             = "";
        String pgtypeid             = "";
        String terminalId           = "";
        TerminalVO terminalVO       = null;
        TerminalManager terminalManager             = new TerminalManager();
        String success                              = "";
        boolean isTerminalUnique                    = true;
        boolean isGatewayAccountId                  = false;
        RequestDispatcher requestDispatcher         = null;
        boolean isMasterCardSupport                 = false;
        GatewayAccountService gatewayAccountService = new GatewayAccountService();


        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        response.setContentType("text/html");
        error = validateParameters(request);

        if (functions.isValueNull(request.getParameter("memberid")))
        {
            try
            {
                boolean isPresent = merchantDAO.isMemberExist(request.getParameter("memberid"));
                if (!isPresent)
                {
                    error = error + "Merchant does not exist in the system" + EOL;
                }
            }
            catch (Exception e)
            {
                logger.error("Merchant does not exist in the system", e);
            }
        }
        if (functions.isValueNull(request.getParameter("pgtypeid")))
        {
            boolean isPresent = false;
            try
            {
                String str[]        = request.getParameter("pgtypeid").split("-");
                pgtypeid            = str[2];

                isPresent   = GatewayTypeService.isValidGatewayType(pgtypeid);
                logger.error("Gateway isPresent ------>"+isPresent);
                if (!isPresent)
                {
                    error = error + "Invalid Gateway . Please select a proper Gateway" + EOL;
                }
            }
            catch (Exception e)
            {
                logger.error("Gateway does not exist in the system", e);
                error = error + "Invalid Gateway . Please select a proper Gateway" + EOL;
            }
        }
        if (functions.isValueNull(error))
        {
            request.setAttribute("error", error);
            if (functions.isValueNull(request.getParameter("terminalid")))
            {
                terminalId = request.getParameter("terminalid");
                request.setAttribute("terminalid", terminalId);
            }

            requestDispatcher = request.getRequestDispatcher("/copyTerminal.jsp");
            requestDispatcher.forward(request, response);
            return;
        }

        if (functions.isEmptyOrNull(error))
        {
            memberid    = request.getParameter("memberid");
            //pgtypeid    = request.getParameter("pgtypeid");
            terminalId  = request.getParameter("terminalid");

            try
            {
                terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
                if(terminalVO != null){
                    isGatewayAccountId = gatewayAccountService.isGatewayAccountIDMapped(pgtypeid,terminalVO.getAccountId());

                    if(!isGatewayAccountId){
                        error               = error + "TerminalId is not mapped with Gateway  " + EOL;
                    }
                }else{
                    error = error + "Terminal ID doesn't exist.";
                }

                if(functions.isValueNull(error)){
                    request.setAttribute("error", error);
                    requestDispatcher = request.getRequestDispatcher("/copyTerminal.jsp");
                    requestDispatcher.forward(request, response);
                    return;
                }

                if (terminalVO != null && !terminalManager.isTerminalUnique(memberid, terminalVO.getAccountId(), terminalVO.getPaymodeId(), terminalVO.getCardTypeId()))
                {
                    isTerminalUnique    = false;
                    error               = error + "Same Terminal Configuration Already exist  " + EOL;
                }

                if (terminalVO != null )
               {
                    isMasterCardSupport = terminalManager.isMasterCardSupported(terminalVO.getAccountId());

                    if(!isMasterCardSupport){
                        error = error + "MasterCard Not Support." + EOL;
                    }

                   if(isTerminalUnique && isMasterCardSupport && isGatewayAccountId){
                        terminalVO.setMemberId(memberid);

                        success = terminalManager.masterCardTerminalConfiguration(terminalVO);
                    }
               }


                if(functions.isValueNull(error)){
                    request.setAttribute("error", error);
                    requestDispatcher = request.getRequestDispatcher("/copyTerminal.jsp");
                    requestDispatcher.forward(request, response);
                    return;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception while adding new terminal", e);
                error = error + "Could Not Added New Terminal.";
                request.setAttribute("error", error);
            }
        }

         logger.info(":::::: CopyTerminal End :::::: ");
         request.setAttribute("accountids", loadGatewayAccounts());
         request.setAttribute("paymodeids", terminalManager.loadPaymodeids());
         request.setAttribute("cardtypeids", terminalManager.loadcardtypeids());
         request.setAttribute("success1", success);
         request.setAttribute("error1", error);
         requestDispatcher = request.getRequestDispatcher("/membermappingpreference.jsp?ctoken=" + user.getCSRFToken());
        requestDispatcher.forward(request, response);
}

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator       = new InputValidator();
        String error                        = "";
        String EOL                          = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.TERMINALID);

        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        return error;
    }
    Hashtable loadGatewayAccounts()
    {
        return GatewayAccountService.getMerchantDetails();
    }

}
