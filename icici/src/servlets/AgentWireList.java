import com.directi.pg.*;
import com.manager.vo.AgentManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/12/14
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentWireList extends HttpServlet
{
    private static Logger log = new Logger(AgentWireList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String errormsg = "";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("message",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/agentWireList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        Functions functions = new Functions();

        String agentId=req.getParameter("agentid");
        String toId=req.getParameter("toid");
        String terminalId=req.getParameter("terminalid");
        String isPaid=req.getParameter("isrr");

        if(!functions.isValueNull(agentId) || "0".equals(agentId)){
            agentId="";
        }
        if(!functions.isValueNull(toId) || "0".equals(toId)){
            toId="";
        }
        if(!functions.isValueNull(terminalId) || "0".equals(terminalId)){
            terminalId="";
        }
        if(!functions.isValueNull(isPaid)){
            isPaid="";
        }

        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        int records=15;
        int pageno=1;

        int start = 0; // start index
        int end = 0; // end index

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        AgentManager agentManager = new AgentManager();
        Hashtable agentwirehash= agentManager.getAgentWireList(toId,terminalId,agentId, isPaid, fdtstamp, tdtstamp, pageno, records);
        req.setAttribute("transdetails", agentwirehash);
        RequestDispatcher rd = req.getRequestDispatcher("/agentWireList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);

    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.TERMINALID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
