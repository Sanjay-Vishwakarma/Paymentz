package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.\
 * User: Saurabh
 * Date: 3/4/14
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTransSummaryList extends HttpServlet
{
    private static Logger log = new Logger(MerchantTransSummaryList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        log.debug("Entering in MerchantTransSummaryList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
        PartnerManager partnerManager = new PartnerManager();
        StringBuffer stringBufferbincountrysuccessful = new StringBuffer();
        StringBuffer stringBufferbincountryfailed = new StringBuffer();
        StringBuffer stringBufferipcountrysuccessful = new StringBuffer();
        StringBuffer stringBufferipcountryfailed = new StringBuffer();
        HashMap<String, String> hashMapbincountrysuccessful = null;
        HashMap<String, String> hashMapbincountryfailed = null;
        HashMap<String, String> hashMapipcountrysuccessful = null;
        HashMap<String, String> hashMapipcountryfailed = null;
        String fdtstamp = "";
        String tdtstamp = "";
        String toid=null;
        String country = req.getParameter("country");
        //System.out.println("country----------"+country);
        String ipaddress = req.getParameter("ipaddress");
        String totype = (String) session.getAttribute("partnername");
        //String toid = req.getParameter("toid");
        String TIMESTAMP = req.getParameter("TIMESTAMP");
        String dtstamp = req.getParameter("dtstamp");
        String bincountrysuccessful = "";
        String bincountryfailed = "";
        String ipcountrysuccessful = "";
        String ipcountryfailed = "";
        String accountid=req.getParameter("accountid")==null?"":req.getParameter("accountid");

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        String partner_id="";
        String partnerid=(String)session.getAttribute("merchantid");
        String PID=req.getParameter("pid");
        if(functions.isValueNull(PID)){
            if(partner.isPartnerSuperpartnerMapped(PID,partnerid)){
                partner_id = PID;
            }
            else{
                partner_id = "";
            }
        }
        else{
             partner_id = partnerid;
        }
        //String toid=null;
        //String error= "";
        StringBuffer errorMsg = new StringBuffer();

        /*String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/

        HashMap statushash=null;
        HashMap statushash1=null;
        HashMap totalcounthash=null;
        HashMap totalcounthash1=null;
        List<String> perCurrency = new ArrayList<String>();

        Calendar cal= Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;

        log.debug("From ::" + req.getParameter("fromdate") + " Todate::" + req.getParameter("todate"));
        String fromDate=req.getParameter("fromdate");
        String toDate= req.getParameter("todate");
        String startTime =req.getParameter("starttime");
        String endTime =req.getParameter("endtime");
        toid= req.getParameter("toid");

        log.debug("Valid data provided for Account Summary");

        String currency = req.getParameter("currency");



        //String fdtstamp = "";
        //String tdtstamp = "";
        RequestDispatcher rdError=req.getRequestDispatcher("/merchanttranssummarylist.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= req.getRequestDispatcher("/merchanttranssummarylist.jsp?Success=YES&ctoken="+user.getCSRFToken());

        if(functions.isFutureDateComparisonWithFromAndToDate(fromDate, toDate, "dd/MM/yyyy"))
        {
            req.setAttribute("catchError","Invalid From & To date");
            rdError.forward(req, res);
            return;
        }

        if (!ESAPI.validator().isValidInput("accountid", accountid, "Number", 255, true))
        {
            req.setAttribute("catchError", "Invalid Account Id");
            rdError.forward(req, res);
            return;
        }

        String errormsg = "";
        errormsg = errormsg+validateMandatoryParameters(req);
        errormsg = errormsg+validateOptionalParameters(req);
        if(functions.isValueNull(errormsg))
        {
            log.debug("catchError::::::"+errormsg);
            req.setAttribute("catchError",errormsg);
            rdError.forward(req, res);
            return;
        }

        //toid = req.getParameter("toid");
        String pid = req.getParameter("pid");
        try
        {
            if(functions.isValueNull(toid))
            {
                if (functions.isValueNull(pid) && !partner.isPartnerMemberMapped(toid, pid))
                {
                    log.error("PartnerID and MerchantID mismatch.");
                    errormsg = "PartnerID and MerchantID mismatch.";
                    req.setAttribute("catchError",errormsg);
                    rdError.forward(req, res);
                    return;
                }
            }
            if(functions.isValueNull(pid) && !partner.isPartnerSuperpartnerMapped(pid, partnerid)){

                errormsg = "Invalid Partner mapping.";
                req.setAttribute("catchError",errormsg);
                rdError.forward(req, res);
                return;
            }
        }
        catch(Exception e){
            log.error("Exception---" + e);
        }

        try
        {
            startTime=startTime.trim();
            endTime=endTime.trim();

            if (!functions.isValueNull(startTime))
            {
                startTime="00:00:00";
            }
            if (!functions.isValueNull(endTime))
            {
                endTime="23:59:59";
            }

            String startTimeArr[]=startTime.split(":");
            String endTimeArr[]=endTime.split(":");
            date=sdf.parse(fromDate);
            cal.setTime(date);
            String fdate=String.valueOf(cal.get(Calendar.DATE));
            String fmonth=String.valueOf(cal.get(Calendar.MONTH));
            String fyear=String.valueOf(cal.get(Calendar.YEAR));

            //to Date
            date=sdf.parse(toDate);
            cal.setTime(date);
            String tdate=String.valueOf(cal.get(Calendar.DATE));
            String tmonth=String.valueOf(cal.get(Calendar.MONTH));
            String tyear=String.valueOf(cal.get(Calendar.YEAR));

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+fyear+" To date dd::"+tdate+" MM::"+tmonth+" YY::"+tyear);

            //conversion to dtstamp
            fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);
            statushash=partnerManager.getReport(toid, partner_id, fdtstamp, tdtstamp, currency, accountid);
            totalcounthash=partnerManager.getReportSucccessCount(toid, partner_id, fdtstamp, tdtstamp, currency, accountid);
            statushash1=partnerManager.getReportpercentage(toid, partner_id, fdtstamp, tdtstamp, currency, accountid);
            totalcounthash1=partnerManager.getReportpercentageSuccessCount(toid, partner_id, fdtstamp, tdtstamp, currency, accountid);

            try
        {
            //System.out.println("execute query::::");
            hashMapbincountrysuccessful = partnerManager.getbincountrysuccessful(totype,toid,country,tdtstamp,fdtstamp,accountid,currency);
            hashMapbincountryfailed = partnerManager.getbincountryfailed(totype,toid, country, tdtstamp, fdtstamp,accountid,currency);
            hashMapipcountrysuccessful = partnerManager.getipcountrysuccessful(totype,toid,tdtstamp, fdtstamp,accountid,currency);
            hashMapipcountryfailed = partnerManager.getipcountryfailed(totype,toid,tdtstamp, fdtstamp,accountid,currency);
        }
        catch (PZDBViolationException e)
        {
            log.error("Exception---" + e);
        }
            log.error(" CountrySuccessful Chart---"+hashMapbincountrysuccessful);
            log.error(" CountryFailed Chart---"+hashMapbincountryfailed);

            //For bincountrysuccessful
            if (hashMapbincountrysuccessful.size() > 0)
            {
                //System.out.println("hashMapbincountrysuccessful size:::"+ hashMapbincountrysuccessful.size());
                for (Map.Entry<String, String> entry : hashMapbincountrysuccessful.entrySet()) {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferbincountrysuccessful.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else
            {
                String blankData= "No Data To Display";
                stringBufferbincountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            bincountrysuccessful = "data : [" + stringBufferbincountrysuccessful.toString() + "],";

            // For bincountryfailed
            if (hashMapbincountryfailed.size() > 0)
            {
                //System.out.println("hashMapbincountryfailed size:::"+ hashMapbincountryfailed.size());
                for (Map.Entry<String, String> entry : hashMapbincountryfailed.entrySet()) {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferbincountryfailed.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else
            {
                String blankData= "No Data To Display";
                stringBufferbincountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            bincountryfailed = "data : [" + stringBufferbincountryfailed.toString() + "],";

            // For ipcountrysuccessful
            if (hashMapipcountrysuccessful.size() > 0)
            {
                //System.out.println("hashMapipcountrysuccessful size:::"+ hashMapipcountrysuccessful.size());
                for (Map.Entry<String, String> entry : hashMapipcountrysuccessful.entrySet()) {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferipcountrysuccessful.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else
            {
                String blankData= "No Data To Display";
                stringBufferipcountrysuccessful.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
            ipcountrysuccessful = "data : [" + stringBufferipcountrysuccessful.toString() + "],";

            // For ipcountryfailed
            if (hashMapipcountryfailed.size() > 0)
            {
                //System.out.println("hashMapipcountryfailed size:::"+ hashMapipcountryfailed.size());
                for (Map.Entry<String, String> entry : hashMapipcountryfailed.entrySet()) {
                    String countryName = entry.getKey();
                    String count = entry.getValue();
                    stringBufferipcountryfailed.append("{ value: " + count + " , label: '" + countryName + "'},");
                }
            }
            else
            {
                String blankData= "No Data To Display";
                stringBufferipcountryfailed.append("{ value: " + 00 + " , label: '" + blankData + "'},");
            }
              ipcountryfailed = "data : [" + stringBufferipcountryfailed.toString() + "],";

        }
        catch (ParseException e)
        {
            log.error("Invalid date format", e);
        }
        catch (SystemError systemError)
        {
            log.error("Error while collection status Report", systemError);
        }

        req.setAttribute("toid",toid);
        req.setAttribute("status_report",statushash);
        req.setAttribute("status_reportpercentage",statushash1);
        req.setAttribute("totalcounthash",totalcounthash);
        req.setAttribute("totalcounthash1",totalcounthash1);
        req.setAttribute("perCurrency",perCurrency);
        req.setAttribute("error",errorMsg);
        req.setAttribute("fdtstamp",fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        req.setAttribute("bincountrysuccessful", bincountrysuccessful);
        req.setAttribute("bincountryfailed", bincountryfailed);
        req.setAttribute("ipcountrysuccessful", ipcountrysuccessful);
        req.setAttribute("ipcountryfailed", ipcountryfailed);
        rdSuccess.forward(req,res);
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.TOID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.CURRENCY);
        inputFieldsListOptional.add(InputFields.PID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }

}


