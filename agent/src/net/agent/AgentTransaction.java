package net.agent;
import com.directi.pg.*;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: Saurabh
 * Date: 2/21/14
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentTransaction extends HttpServlet
{
    private static Logger log = new Logger(AgentTransaction.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        log.debug("Enter in AgentTransaction ");
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {
            res.sendRedirect("/agent/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("CSRF check successful ");
        String agentid = (String) session.getAttribute("merchantid");
        String desc=null;
        String trackingid=null;
        String status=null;
        String fromdate=null;
        String todate=null;
        /*String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/
        //String accountid=null;
        boolean archive =false;
        int pageno=1;
        int records=30;
        String merchantid=null;
        status = Functions.checkStringNull(req.getParameter("status"));
        StringBuffer errormsg = new StringBuffer();

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        RequestDispatcher rdError = req.getRequestDispatcher("/agentTransactions.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = req.getRequestDispatcher("/agentTransactions.jsp?Success=YES&ctoken="+user.getCSRFToken());

        Functions functions = new Functions();
        String error = "";

        error = error + validateOptionalParameters(req);

        desc = req.getParameter("desc");
        trackingid = req.getParameter("trackingid");
        fromdate =req.getParameter("fromdate");
        todate =req.getParameter("todate");
        /*fmonth = req.getParameter("fmonth");
        tmonth =  req.getParameter("tmonth");
        fyear = req.getParameter("fyear");
        tyear = req.getParameter("tyear");*/
        merchantid= req.getParameter("memberid");
        String cardtype= req.getParameter("cardtype");
        archive = Boolean.valueOf(req.getParameter("archive"));

        res.setContentType("text/html");

        if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
        {
            req.setAttribute("catchError","Invalid From & To date");
            rdError.forward(req, res);
            return;
        }

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("pageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("records",req.getParameter("SRecords"),"Numbers",5,true), 30);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            errormsg.append("Invalid page no or records");
            req.setAttribute("error",errormsg.toString());
            pageno = 1;
            records = 30;
        }
        TransactionEntry transactionentry =new TransactionEntry();
        try
        {
            //from Date
            date=sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate=String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear=String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(todate);
            rightNow.setTime(date);
            String tdate=String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth=String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear=String.valueOf(rightNow.get(Calendar.YEAR));

            log.debug("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            Set<String> gatewayTypeSet = new HashSet();
            gatewayTypeSet.addAll(transactionentry.getGatewayHashFromAgent(merchantid,agentid).keySet());
            AgentFunctions transdetail=new AgentFunctions();

            if(!functions.isValueNull(merchantid))
            {
                merchantid = agent.getAgentMemberList(agentid);
            }
            Hashtable hash =null;
            if(cardtype.equals("CP")){
                hash = transdetail.listTransactionsCPNew(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, "null", merchantid);
            }
            else
            {
                 hash = transdetail.listTransactionsNew(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, "null", merchantid);
            }
            log.debug("Transactions are set successfully ");
            req.setAttribute("fdtstamp", fdtstamp);
            req.setAttribute("tdtstamp", tdtstamp);
            req.setAttribute("transactionsdetails", hash);

        }
        catch (SQLException se)
        {
            log.error("SQLException occurred",se);
            errormsg.append("Internal error while accessing data.");
            req.setAttribute("error",errormsg.toString());
            Functions.NewShowConfirmation1("Error", "SQLException while getting list of Transactions");
        }
        catch (SystemError se)
        {
            log.error("SystemError occurred",se);
            errormsg.append("Internal error while accessing data.");
            req.setAttribute("error",errormsg.toString());
            Functions.NewShowConfirmation1("Error", "SystemError while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Exception occurred",e);
            errormsg.append("Internal error while accessing data.");
            req.setAttribute("error",errormsg.toString());
            Functions.NewShowConfirmation1("Error", "Exception while getting list of Transactions");
        }
        //session.setAttribute("bank",accountid);
        req.setAttribute("memberid",merchantid);
        req.setAttribute("error",error);

        rdSuccess.forward(req, res);
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.DESC);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.FROMMONTH);
        inputFieldsListOptional.add(InputFields.TOMONTH);
        inputFieldsListOptional.add(InputFields.FROMYEAR);
        inputFieldsListOptional.add(InputFields.TOYEAR);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional,errorList,true);

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
