import com.directi.pg.*;
import com.manager.TransactionManager;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/29/13
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Compliance extends HttpServlet
{
    static Logger log = new Logger(Compliance.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in Compliance");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ServletContext application = getServletContext();

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        String ccnum=null;
        String firstsix=null;
        String lastfour=null;
        String accountid=null;
        String gateway="";
        String gateway_name="";
        String currency="";
        String pgtypeid="";
        String error= "";
        int records=15;
        int pageno=1;
        int start = 0; // start index
        int end = 0; // end index

        TransactionManager transactionManager = new TransactionManager();
        error = error + validateMandatoryParameters(req);
        firstsix = req.getParameter("firstsix");
        lastfour = req.getParameter("lastfour");
        if(error.length() > 0)
        {
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/compliance.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        error = error + validateOptionalParameters(req);
        Functions functions=new Functions();
        if(!("0".equals(req.getParameter("accountid"))) && functions.isValueNull(req.getParameter("accountid")))
        {
            accountid= req.getParameter("accountid");
            req.setAttribute("accountid",accountid);
        }

        if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            currency = req.getParameter("pgtypeid").split("-")[1];
            gateway_name = req.getParameter("pgtypeid").split("-")[0];
        }
        if (!(req.getParameter("pgtypeid").split("-").length == 3))
        {
            gateway_name = req.getParameter("pgtypeid");
        }


        if(error.length() > 0)
        {
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/compliance.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        start = (pageno - 1) * records;
        end = records;
        StringBuffer sb=null;
        StringBuffer count=null;
        Hashtable hash = null;
        req.setAttribute("firstsix", firstsix);
        req.setAttribute("lastfour",lastfour);

        try
        {
            hash = transactionManager.getListOfTransactionForCompliance(gateway_name, accountid, currency, firstsix, lastfour, start, end);
            req.setAttribute("transdetails", hash);
        }
        catch (PZDBViolationException e)
        {
            log.error("Error while getting list of transaction",e);
        }

        req.setAttribute("error",error);
        RequestDispatcher rd = req.getRequestDispatcher("/compliance.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.FIRST_SIX);
        inputFieldsListOptional.add(InputFields.LAST_FOUR);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
