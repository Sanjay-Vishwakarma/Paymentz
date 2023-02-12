package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionEntry;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sanjay  on 12/1/2021.
 */
public class PayoutTransactionList extends HttpServlet
{

    static Logger log = new Logger(PayoutTransactionList.class.getName());
    private static Functions functions = new Functions();
    PartnerFunctions partnerFunctions = new PartnerFunctions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
//        System.out.println("inside doPost method");
        log.debug("inside do post method");
        HttpSession session = req.getSession();
        PartnerFunctions partner = new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/merchantPayoutTransactions.jsp?=ctoken" + user.getCSRFToken());
        String partnerid = (String) session.getAttribute("merchantid");
        String accountid = null;
        String partnerName = null;
        String currency="";
        String remark = null;
        String desc = null;
        String status = null;
        String emailAddress = null;
        String trackingid = null;
        String amount = null;
        String bankaccount = null;
        String merchantid = null;
        String terminalid = null;
        int pageno = 1;
        int records = 30;
        String errormsg = "";
        String error = "";
        String EOL = "<BR>";
        String fyear = "";
        String fmonth = "";
        String fdate = "";
        String tyear = "";
        String tmonth = "";
        String tdate = "";

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        error = validateOptionalParameter(req);
        try
        {
            log.error("inside try block..." +error);

            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandotary = new ArrayList<InputFields>();
            inputFieldsListMandotary.add(InputFields.PAGENO);
            inputFieldsListMandotary.add(InputFields.RECORDS);
            inputValidator.InputValidations(req, inputFieldsListMandotary, true);
            if (!error.isEmpty())
            {
                req.setAttribute("error", error);
                rd.forward(req, res);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("inside catch block..." + e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>" + errormsg  + EOL + "</b></font></center>";

            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }


        accountid = req.getParameter("accountid");
        desc = req.getParameter("desc");
        amount = req.getParameter("amount");
        String startTime = req.getParameter("starttime");
        String endTime = req.getParameter("endtime");
        trackingid = req.getParameter("trackingid");
        emailAddress = req.getParameter("emailaddr");
        bankaccount = req.getParameter("bankaccount");
        merchantid = req.getParameter("memberid");
        status = req.getParameter("status");
        String pid          = req.getParameter("pid");
        String dateType=Functions.checkStringNull(req.getParameter("datetype"));

        if (functions.isValueNull(req.getParameter("memberid")) && merchantid.contains(req.getParameter("memberid")))
        {
            merchantid = req.getParameter("memberid");
        }
        else if (functions.isValueNull(req.getParameter("memberid")) && !merchantid.contains(req.getParameter("memberid")))
        {
            error = " Invalid partner member configuration";
            req.setAttribute("error", error);
            rd = req.getRequestDispatcher("/merchantPayoutTransactions.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        StringBuffer trackingIds = new StringBuffer();

        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList = null;
            if (trackingid.contains(","))
            {
                trackingidList = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList = Arrays.asList(trackingid.split(" "));
            }
            int i = 0;
            Iterator itr = trackingidList.iterator();
            while (itr.hasNext())
            {
                if (i != 0)
                {
                    trackingIds.append(",");
                }
                trackingIds.append("" + itr.next() + "");
                i++;
            }
        }


        res.setContentType("text/html");

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"),1);
        records=Functions.convertStringtoInt(req.getParameter("SRecords"),30);
        try{
            startTime =startTime.trim();
            endTime = endTime.trim();
            if(!functions.isValueNull(startTime))
            {
                startTime="00:00:00";
            }
            if(!functions.isValueNull(endTime))
            {
                endTime="23:59:59";
            }
            String fromdate = req.getParameter("fromdate");
            String todate   = req.getParameter("todate");

            date        = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate   = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date    = sdf.parse(todate);
            rightNow.setTime(date);
            tdate   = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");
            log.debug("From date dd::"+fdate+" MM::"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);
            String fdtstamp     = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp     = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionEntry = new TransactionEntry();
            Set <String> gatewayTypeSet = new HashSet();
            Hashtable hash =null;
            StringBuilder sb = new StringBuilder();


            if(functions.isValueNull(pid)&& partner.isPartnerSuperpartnerMapped(pid,partnerid)){
                hash   = partner.getPartnerNameFromPartnerId(pid, req.getParameter("memberid"));
            }
            else if (!functions.isValueNull(pid))
            {
                hash   = partner.getPartnerNameFromPartnerIdAndSuperPartnerId(partnerid, req.getParameter("memberid"));
            }
            else {
                error = "Invalid partner mapping.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/merchantPayoutTransactions.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }


            if(hash.size()>0)
            {
                Enumeration enu3    = hash.keys();
                String key3         = "";
                while (enu3.hasMoreElements())
                {
                    key3        = (String) enu3.nextElement();
                    partnerName = (String) hash.get(key3);
                    sb.append("'"+partnerName+"'");
                    sb.append(",");
                }
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ',')
                {
                    partnerName = sb.substring(0, sb.length() - 1);
                }
            }

            PartnerFunctions transdetail    = new PartnerFunctions();

            int totalRecords=transdetail.getCountOfPayoutList(desc,status,tdtstamp,fdtstamp,trackingIds,accountid,merchantid ,emailAddress,dateType,terminalid,partnerName,pageno,records,bankaccount,amount,remark,currency );
            HashMap hash1    = transdetail.getTransactionListDetails(desc,status,tdtstamp,fdtstamp,trackingIds,accountid,merchantid ,emailAddress,dateType,terminalid,partnerName,pageno,records,bankaccount,amount,remark,currency,totalRecords );

            log.debug("New Transections  are set successfully");
            req.setAttribute("payouttransaction", hash1);

            req.setAttribute("memberid",merchantid);

        }

        catch(Exception e)
        {
            log.error("Exception ::::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        session.setAttribute("bank",bankaccount);
        req.setAttribute("error",error);
        rd=req.getRequestDispatcher("/merchantPayoutTransactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req,res);
        return;

    }

    private String validateOptionalParameter(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error="";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.DESC);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.EMAILADDR);
        inputFieldsListOptional.add(InputFields.BANK_ACCOUNT_NO);
        inputFieldsListOptional.add(InputFields.AMOUNT);
        ValidationErrorList errorList = new ValidationErrorList();

        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                }
            }
        }
        return error;
    }

}
