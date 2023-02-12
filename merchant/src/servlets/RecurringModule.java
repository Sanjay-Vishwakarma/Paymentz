import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.RecurringManager;
import com.manager.TerminalManager;
import com.manager.vo.PaginationVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TerminalVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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


/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 4/10/15
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecurringModule extends HttpServlet
{
    private static Logger log = new Logger(RecurringModule.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        log.debug("Enter in RecurringModule---");

        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RecurringBillingVO recurringBillingVO = null;
        List<RecurringBillingVO> listOfRecurringVo =null;
        RecurringManager recurringManager = new RecurringManager();
        PaginationVO paginationVO = new PaginationVO();


        String errormsg = "";
        String toid = (String)session.getAttribute("merchantid");

        errormsg = errormsg+validateParameters(req);
        String rbid = req.getParameter("rbid");
        String trackingid = req.getParameter("trackingid");
        String name = req.getParameter("name");
        String firstSix = req.getParameter("firstsix");
        String lastFour = req.getParameter("lastfour");
        String terminalid = req.getParameter("terminalid");

        String sb = req.getParameter("terminalbuffer");

        int records=15;
        int pageno=1;
        String errorList =null;
        int start = 0; // start index
        int end = 0; // end index

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno", req.getParameter("SPageno"), "Numbers", 5, true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);

            recurringBillingVO = new RecurringBillingVO();

            paginationVO.setInputs("&rbid="+rbid+"&trackingid="+trackingid+"&name="+name+"&firstsix="+firstSix+"&lastfour="+lastFour+"&terminalid="+terminalid+"&terminalbuffer="+sb);
            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(RecurringModule.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));

            recurringBillingVO.setOriginTrackingId(trackingid);
            recurringBillingVO.setRbid(rbid);
            recurringBillingVO.setMemberId(toid);
            recurringBillingVO.setFirstSix(firstSix);
            recurringBillingVO.setLastFour(lastFour);
            recurringBillingVO.setCardHolderName(name);
            recurringBillingVO.setTerminalid(terminalid);

            TerminalManager terminalManager = new TerminalManager();
            TerminalVO terminalVO = new TerminalVO();
            Functions functions=new Functions();

            if(functions.isValueNull(terminalid) && !terminalid.equalsIgnoreCase("all"))
            {
                terminalVO=terminalManager.getTerminalByTerminalId(terminalid);
                recurringBillingVO.setTerminalid("("+recurringBillingVO.getTerminalid()+")");
            }
            else
            {
                if(sb!=null){
                    recurringBillingVO.setTerminalid(sb);
                }
            }

            listOfRecurringVo = recurringManager.getRBSubscriptionDetails(recurringBillingVO,paginationVO);
        }
        catch (PZDBViolationException dbe)
        {
            log.error("DB Connection in Recurring Module in merchant---", dbe);
            PZExceptionHandler.handleDBCVEException(dbe,toid,"Recurring Module Merchant Interface");
        }
        catch (ValidationException e)
        {
            log.error("Invalid page no or records", e);
            pageno = 1;
            records = 15;
        }


        req.setAttribute("error",errormsg);
        req.setAttribute("recurringBillingVO",listOfRecurringVo);
        req.setAttribute("paginationVO",paginationVO);
        req.setAttribute("terminalid",terminalid);
        req.setAttribute("terminalbuffer",sb);
        RequestDispatcher rd = req.getRequestDispatcher("/recurringModule.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.RBID);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.NAME_SMALL);
        inputFieldsListOptional.add(InputFields.FIRST_SIX);
        inputFieldsListOptional.add(InputFields.LAST_FOUR);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}
