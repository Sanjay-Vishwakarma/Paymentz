import com.directi.pg.*;

import com.manager.ChargeManager;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListGatewayAccountsCharges extends HttpServlet
{
    static Logger log = new Logger(ListGatewayAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        int records=15;
        int pageno=1;
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        Hashtable hash = null;
        ChargeManager chargeManager=new ChargeManager();
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;
            log.debug("message..."+e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/listGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String accountId = "";
      // if(!("0".equals(req.getParameter("accountid"))))

           if(!("0".equals(req.getParameter("accountid"))) && functions.isValueNull(req.getParameter("accountid")))
        {
            accountId= req.getParameter("accountid");
            log.debug("accountId::::"+accountId);
            req.setAttribute("accountid",accountId);

        }
        String chargeId = req.getParameter("chargename");
        String gateway=null;
        String currency=null;
        String pgtypeid=null;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;


        if(functions.isValueNull(req.getParameter("pgtypeid")) && !("0").equals(req.getParameter("pgtypeid")))
        {
            String gatewayArr[] = req.getParameter("pgtypeid").split("-");
            gateway=gatewayArr[0];
            currency=gatewayArr[1];
            pgtypeid=gatewayArr[2];

        }

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);

        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        try
        {
            hash= chargeManager.getListOfGatewayAccountCharges(pgtypeid,currency,accountId, chargeId, gateway, start, end,actionExecutorId,actionExecutorName);
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);
        }
        catch (PZDBViolationException pze)
        {
            log.error("System error while perform select query",pze);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Gateway Account Charges");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/listGatewayAccountsCharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.MAPPINGID);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.CHARGEID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}