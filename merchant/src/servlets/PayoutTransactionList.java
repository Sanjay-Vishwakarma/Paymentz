/**
 * Created by Sanjay on 12/28/2021.
 */


import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.directi.pg.TransactionEntry;
import com.manager.MerchantConfigManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
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
import java.text.SimpleDateFormat;
import java.util.*;

public class PayoutTransactionList extends HttpServlet
{

    private static Logger log = new Logger(PayoutTransactionList.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String error = "";
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        Functions functions = new Functions();
        if (!merchants.isLoggedIn(session))
        {
            resp.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ValidationErrorList validationErrorList = null;

        log.debug("CSRF check successful ");
        String merchantid = (String) session.getAttribute("merchantid");
        ServletContext application = getServletContext();
        String status = Functions.checkStringNull(req.getParameter("status")) == null ? "" : req.getParameter("status");
        String desc = Functions.checkStringNull(req.getParameter("desc")) == null ? "" : req.getParameter("desc");
        String accountid = Functions.checkStringNull(req.getParameter("accountid")) == null ? "" : req.getParameter("accountid");
        String trackingid = Functions.checkStringNull(req.getParameter("trackingid")) == null ? "" : req.getParameter("trackingid");
        String emailAddress = Functions.checkStringNull(req.getParameter("emailaddr")) == null ? "" : req.getParameter("emailaddr");
        String terminalId = null;
        String bankaccount = Functions.checkStringNull(req.getParameter("bankaccount")) == null ? "" : req.getParameter("bankaccount");
        String remark = null;

        String amount = Functions.checkStringNull(req.getParameter("amount")) == null ? "" : req.getParameter("amount");
       /* if (!ESAPI.validator().isValidInput("amount", amount, "Amount", 20, true))
        {
            error = "Invalid Amount";
        }*/
        String currency = null;

        int pageno = 1;
        int records = 30;

        RequestDispatcher rdError = req.getRequestDispatcher("/payoutTransactionList.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        validationErrorList = validateOptionalParameters(req);


        if (!validationErrorList.isEmpty())
        {
            req.setAttribute("validationErrorList", validationErrorList);
            rdError.forward(req, resp);
            return;
        }

        String startTime = req.getParameter("starttime");
        String endTime = req.getParameter("endtime");
        String dateType = req.getParameter("datetype");
        String fullname = "";
        String ifsc = "";
        String partnerid = "";
        String partnerName = "";

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;

        resp.setContentType("text/html");

        try
        {
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", req.getParameter("SRecords"), "Numbers", 3, true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch (ValidationException e)
        {
            log.error("Invalid page no or records", e);
            pageno = 1;
            records = 30;
        }


        String fyear = "";
        String fmonth = "";
        String fdate = "";
        String tyear = "";
        String tmonth = "";
        String tdate = "";


        startTime = startTime.trim();
        endTime = endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime = "00:00:00";
        }
        if (!functions.isValueNull(endTime))
        {
            endTime = "23:59:59";
        }


        try
        {
            Hashtable hash = new Hashtable();
            HashMap hash1 = new HashMap();
            RequestDispatcher rd;
            String fromdate = req.getParameter("fdate");
            String todate = req.getParameter("tdate");
            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            tdate = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear = String.valueOf(rightNow.get(Calendar.YEAR));

            log.debug("startTime::" + startTime);
            log.debug("endTime::" + endTime);


            String startTimeArr[] = startTime.split(":");
            String endTimeArr[] = endTime.split(":");
            log.debug("From date dd::" + fdate + " MM::" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet = new HashSet();
            TerminalManager terminalManager = new TerminalManager();
            TerminalVO terminalVO = new TerminalVO();
            functions = new Functions();
            if (!functions.isValueNull(error))
            {
                hash = transactionentry.payoutTransactionLists(desc, status, tdtstamp, fdtstamp, trackingid, accountid, emailAddress, dateType, terminalVO, pageno, records, bankaccount, fullname, ifsc, amount, currency, remark, partnerName, partnerid, merchantid);
            }

            log.error("hash terminalVO----" + terminalVO);
            req.setAttribute("error", error);
            req.setAttribute("payoutTransaction", hash);


        }
        catch (Exception e)
        {
            log.error("Exception ::::", e);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/payoutTransactionList.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, resp);


    }

    public ValidationErrorList validateOptionalParameters(HttpServletRequest req)
    {
        List<InputFields> inputOptionalParameter= new ArrayList<InputFields>();
        inputOptionalParameter.add(InputFields.TRACKINGID_SMALL);
        inputOptionalParameter.add(InputFields.ACCOUNTID_SMALL);
        inputOptionalParameter.add(InputFields.MEMBERID);
        inputOptionalParameter.add(InputFields.EMAILADDR);
        inputOptionalParameter.add(InputFields.DESC);
        inputOptionalParameter.add(InputFields.PAGENO);
        inputOptionalParameter.add(InputFields.RECORDS);
        inputOptionalParameter.add(InputFields.AMOUNT);
        inputOptionalParameter.add(InputFields.BANK_ACCOUNT_NO);
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputOptionalParameter, errorList,true);

        return errorList;
    }

}
