package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionEntry;
import com.manager.MerchantConfigManager;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 10/8/13
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerTransaction extends HttpServlet
{
    static Logger log                   = new Logger(PartnerTransaction.class.getName());
    private static Functions functions  = new Functions();
    PartnerFunctions partnerFunctions   = new PartnerFunctions();
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session         = req.getSession();
        log.debug("Enter in Transection ");
        PartnerFunctions partner    = new PartnerFunctions();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rd    = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
        RequestDispatcher rd2    = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
        String partnerid        = (String) session.getAttribute("merchantid");
        String desc             = null;
        String trackingid       = null;
        String status           = null;
        String firstName        = null;
        String lastName         = null;
        String emailAddress     = null;
        String paymentId        = null;
        String customerId       = null;
        String accountid        = null;
        String terminalid       = null;
        String error            = "";
        boolean archive         = false;
        int pageno              = 1;
        int records             = 30;
        String merchantid       = null;
        String partnerName      = null;
        String errormsg         = "";
        String EOL              = "<BR>";
        String fyear            = "";
        String fmonth           = "";
        String fdate            = "";
        String tyear            = "";
        String tmonth           = "";
        String tdate            = "";
        String Partner_id       = "";
        String transactionMode  = "";


        Calendar rightNow               = Calendar.getInstance();
        SimpleDateFormat sdf            = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
        Date date                       = null;

        error = error + validateOptionalParameters(req);

        try
        {
            //validateOptionalParameter(req);
            InputValidator inputValidator               = new InputValidator();
            List<InputFields> inputFieldsListMandatory  = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);
            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
            if(!error.isEmpty())
            {
                req.setAttribute("error",error);
                rd.forward(req,res);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("error",errormsg);
            rd.forward(req, res);
            return;
        }


        accountid       = req.getParameter("accountid");
        archive         = Boolean.valueOf(req.getParameter("archive"));
        desc            = req.getParameter("desc");
        trackingid      = req.getParameter("trackingid");
        status          = Functions.checkStringNull(req.getParameter("status"));
        firstName       = req.getParameter("firstname");
        lastName        = req.getParameter("lastname");
        emailAddress    = req.getParameter("emailaddr");
        paymentId       = req.getParameter("paymentid");
        customerId      = req.getParameter("customerid");
        terminalid      = req.getParameter("terminalid");
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String dateType     = req.getParameter("datetype");
        String issuingbank  = req.getParameter("issuingbank");
        String statusflag   = req.getParameter("statusflag");
        String timezone     = req.getParameter("timezone");
        String cardtype     = req.getParameter("cardtype");
        String pid          = req.getParameter("pid");
        transactionMode     = req.getParameter("transactionMode");
        merchantid     = req.getParameter("memberid");
        if(functions.isValueNull(req.getParameter("memberid")) && merchantid.contains(req.getParameter("memberid")))
        {
            merchantid = req.getParameter("memberid");
        }
        else if(functions.isValueNull(req.getParameter("memberid")) && !merchantid.contains(req.getParameter("memberid"))){
            error = "Invalid partner member configuration.";
            req.setAttribute("error", error);
            rd = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        StringBuffer trackingIds = new StringBuffer();

        if(!functions.isValueNull(transactionMode)){
            transactionMode = "";
        }

        if (functions.isValueNull(trackingid))
        {
            List<String> trackingidList = null;
            if(trackingid.contains(","))
            {
                trackingidList  = Arrays.asList(trackingid.split(","));
            }
            else
            {
                trackingidList  = Arrays.asList(trackingid.split(" "));
            }

            int i           = 0;
            Iterator itr    = trackingidList.iterator();
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

        if(!ESAPI.validator().isValidInput("trackingid",req.getParameter("trackingid"),"Numbers",100,true))
        {
            log.error("Invalid TrackingID.");
            error   = "Invalid TrackingID.";
            req.setAttribute("error",error);
            rd = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        else
        {
            trackingid = req.getParameter("trackingid");
        }

        if (functions.isValueNull(timezone))
        {
            timezone = timezone.substring(0,timezone.indexOf("|"));
        }

        String gateway          = "";
        String currency         = "";
        String gateway_name     = "";

        if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            gateway         = req.getParameter("pgtypeid").split("-")[2];
            currency        = req.getParameter("pgtypeid").split("-")[1];
            gateway_name    = req.getParameter("pgtypeid").split("-")[0];
        }
        if (accountid.equals("0"))
        {
            accountid = "";
        }

        res.setContentType("text/html");

        pageno      = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records     = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);
        try
        {
            startTime   = startTime.trim();
            endTime     = endTime.trim();

            if (!functions.isValueNull(startTime))
            {
                startTime = "00:00:00";
            }
            if (!functions.isValueNull(endTime))
            {
                endTime="23:59:59";
            }

            if (functions.isValueNull(timezone))
            {
                String fromdate = req.getParameter("fromdate");
                String todate   = req.getParameter("todate");
                //from date
                Date frtzdate       = sdf.parse(fromdate);
                String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
                //to Date
                Date totzdate       = sdf.parse(todate);
                String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                String fdatetime1   = functions.convertDateTimeToTimeZone1(fdatetime, timezone);
                String tdatetime1   = functions.convertDateTimeToTimeZone1(tdatetime, timezone);

                String fdt[]    = fdatetime1.split(" ");
                String tdt[]    = tdatetime1.split(" ");
                date            = sdftimezone.parse(fdt[0]);
                rightNow.setTime(date);
                fdate   = String.valueOf(rightNow.get(Calendar.DATE));
                fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                fyear   = String.valueOf(rightNow.get(Calendar.YEAR));
                startTime   = fdt[1];
                date        = sdftimezone.parse(tdt[0]);
                rightNow.setTime(date);
                tdate   = String.valueOf(rightNow.get(Calendar.DATE));
                tmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
                tyear   = String.valueOf(rightNow.get(Calendar.YEAR));
                endTime = tdt[1];
                boolean dateRangeIsValid = datedifference(fdatetime1, tdatetime1);
                log.error("dateRangeIsValid-->"+dateRangeIsValid);
                if(!dateRangeIsValid){
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ "Report date days should not be greater than 30 days!" +  EOL + "</b></font></center>";
                    req.setAttribute("dateRangeIsValid",errormsg);
                    rd2.forward(req,res);
                    return;
                }

            }
            else
            {

                String fromdate = req.getParameter("fromdate");
                String todate   = req.getParameter("todate");

                //from date
                Date frtzdate       = sdf.parse(fromdate);
                String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
                //to Date
                Date totzdate       = sdf.parse(todate);
                String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                String fdatetime1   = fdatetime;
                String tdatetime1   = tdatetime;
                boolean dateRangeIsValid = datedifference(fdatetime1, tdatetime1);
                log.error("dateRangeIsValid-->"+dateRangeIsValid);
                if(!dateRangeIsValid){
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ "Report date days should not be greater than 30 days!" +  EOL + "</b></font></center>";
                    req.setAttribute("dateRangeIsValid",errormsg);
                    rd2.forward(req,res);
                    return;
                }

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
            }

            /*String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);*/

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");
            log.debug("From date dd::"+fdate+" MM::"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);
            String fdtstamp     = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp     = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry =new TransactionEntry();

            Set<String> gatewayTypeSet = new HashSet();
            Hashtable hash              = null;
            StringBuilder sb            = new StringBuilder();
                if(functions.isValueNull(pid)&& partner.isPartnerSuperpartnerMapped(pid,partnerid)){
                    Date date4 = new Date();
                    log.error("getPartnerNameFromPartnerId starts ############"+date4.getTime());
                    hash   = partner.getPartnerNameFromPartnerId(pid, req.getParameter("memberid"));
                    log.error("getPartnerNameFromPartnerId ends ############"+new Date().getTime());
                    log.error("getPartnerNameFromPartnerId diff ############"+(new Date().getTime()-date4.getTime()));
                    log.error("getPartnerNameFromPartnerId---"+hash);
                }
                else if (!functions.isValueNull(pid))
                {
                    Date date5 = new Date();
                    log.error("getPartnerNameFromPartnerIdAndSuperPartnerId starts ############"+date5.getTime());
                    hash   = partner.getPartnerNameFromPartnerIdAndSuperPartnerId(partnerid, req.getParameter("memberid"));
                    log.error("getPartnerNameFromPartnerIdAndSuperPartnerId ends ############"+new Date().getTime());
                    log.error("getPartnerNameFromPartnerIdAndSuperPartnerId diff ############"+(new Date().getTime()-date5.getTime()));
                    log.error("getPartnerNameFromPartnerIdAndSuperPartnerId---"+hash);
                }
                else {
                    error = "Invalid partner mapping.";
                    req.setAttribute("error", error);
                    rd = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
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
                        log.error("Partner name before---"+partnerName);
                        partnerName = sb.substring(0, sb.length() - 1);
                        log.error("Partner name after---"+partnerName);
                    }
                }

            /*if(functions.isValueNull(req.getParameter("memberid")) && merchantid.contains(req.getParameter("memberid")))
            {
                merchantid = req.getParameter("memberid");
            }
            else if(functions.isValueNull(req.getParameter("memberid")) && !merchantid.contains(req.getParameter("memberid"))){
                error = "Invalid partner member configuration.";
                req.setAttribute("error", error);
                rd = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }*/

            PartnerFunctions transdetail    = new PartnerFunctions();
            HashMap hash1;
            HashMap trackinghash;
            String amountcount=null;
            String cardpresent = null;
            if (functions.isValueNull(req.getParameter("cardpresent")))
                cardpresent=req.getParameter("cardpresent");
            //System.out.println("cardtype ::" + cardtype);
            if( functions.isValueNull(cardpresent) && cardpresent.equals("N"))
            {
                int totalRecords = transdetail.getTransactionsCountNew(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid,partnerName,transactionMode);
                hash1           = transdetail.listTransactionsNew(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid,partnerName,transactionMode,totalRecords);
                trackinghash    = transdetail.getTrackingIdList(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionMode);
                amountcount    = transdetail.getTotalAmount(desc, tdtstamp, fdtstamp, trackingIds, status, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionMode);
            }
            else{
                hash1           = transdetail.listCardPrsentTransactions(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank,cardtype, terminalid,partnerName);
                trackinghash    = transdetail.getCPTrackingIdList(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid,partnerName);
            }

            log.debug("Transections are set successfully");
            req.setAttribute("transactionsdetails", hash1);
            req.setAttribute("TrackingIDList1", trackinghash);
            req.setAttribute("amountcount", amountcount);
            req.setAttribute("memberid",merchantid);
            req.setAttribute("customerid", customerId);
            req.setAttribute("transactionMode", transactionMode);

            List<String> listofMerchantId= new ArrayList<>();
            listofMerchantId= partner.getListOfMerchantId(desc, tdtstamp, fdtstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, currency, merchantid, dateType, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionMode);
            String[] merchants = listofMerchantId.stream().toArray(String[]::new);
            try
            {
                if (merchants!= null)
                {
                    Map<String, Object> merchantTemplateSetting = new HashMap<String, Object>();
                    for (String merchant: merchants)
                    {
                        merchantTemplateSetting = merchantConfigManager.getSavedMemberTemplateDetails(merchant);
                    }
                    req.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("Exception while setting template values ", e);
            }
        }
        catch (SystemError se)
        {
            log.error("System error occure",se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {
            log.error("Exception ::::",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        session.setAttribute("bank",accountid);

        req.setAttribute("error", error);
        rd = req.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error    = "";
        String EOL      = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.DESC);
      //  inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListOptional.add(InputFields.FDATE);
        inputFieldsListOptional.add(InputFields.TDATE);
        inputFieldsListOptional.add(InputFields.PID);
        inputFieldsListOptional.add(InputFields.EMAILADDR);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage() + EOL);
                }
            }
        }
        return error;
    }
    public Boolean datedifference(String startdate, String endDate){
        boolean isvaliddate= false;
        try{

            Date fromDate = stringToDate(startdate);
            Date toDate = stringToDate(endDate);
            LocalDateTime from = LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime to= LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault());

            if(Duration.between(from, to).toDays() <= 93){

                isvaliddate= true;
            }
            else {
                isvaliddate= false;
            }
        }
        catch (ParseException e)
        {
            log.error("ParseException--->",e);
        }

        return isvaliddate;
    }

    public static Date stringToDate(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(dateStr);
    }
}