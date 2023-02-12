import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.SBMPaymentGateway;
import com.manager.MerchantConfigManager;
import com.manager.TerminalManager;
import com.manager.vo.TerminalVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.*;

//import java.io.PrintWriter;

public class Transactions extends HttpServlet
{

    private static Logger log = new Logger(Transactions.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Enter in Transection ");
        Merchants merchants = new Merchants();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        Functions functions = new Functions();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        ValidationErrorList validationErrorList =null;

        log.debug("CSRF check successful ");
        String merchantid           = (String) session.getAttribute("merchantid");
        ServletContext application  = getServletContext();
        String desc                 = null;
        String trackingid           = null;
        String status               = null;
        String accountid    = null;
        String terminalId   = null;
        String firstName    = null;
        String lastName     = null;
        String emailAddress = null;
        String paymentId    = null;
        String customerId   = null;
        String sb               = req.getParameter("terminalbuffer");
        String transactionMode  = req.getParameter("transactionMode");

        boolean archive     = false;
        //boolean isOptional=false;
      //  int start = 0; // start index
       // int end = 0; // end index
        int pageno      = 1;
        int records     = 30;
       // String str = null;

        status                      = Functions.checkStringNull(req.getParameter("status"));
        RequestDispatcher rdError   = req.getRequestDispatcher("/transactions.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rd2 = req.getRequestDispatcher("/transactions.jsp?date=err&ctoken="+user.getCSRFToken());

        validationErrorList         = validateOptionalParameters(req);

        if(!validationErrorList.isEmpty())
        {
            req.setAttribute("validationErrorList",validationErrorList);
            rdError.forward(req,res);
            return;
        }

        terminalId  = req.getParameter("terminalid");
        archive     = Boolean.valueOf(req.getParameter("archive"));
        desc        = req.getParameter("desc");
        firstName   = req.getParameter("firstname");
        lastName    = req.getParameter("lastname");
        emailAddress= req.getParameter("emailaddr");
        trackingid  = req.getParameter("trackingid");
        paymentId   = req.getParameter("paymentid");
        customerId          = req.getParameter("customerid");
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String dateType     = req.getParameter("datetype");
        String statusflag   = req.getParameter("statusflag");
        String issuingBank  = req.getParameter("issuingbank");
        String timezone     = req.getParameter("timezone");
        String firstsix     = req.getParameter("firstsix");
        String lastfour     = req.getParameter("lastfour");
        String cardtype     = req.getParameter("cardtype");
        String currency     = "";
        String gateway_name = "";
        if (functions.isValueNull(timezone))
        {
            timezone = timezone.substring(0,timezone.indexOf("|"));
        }

       /* if (req.getParameter("pgtypeid")!=null && req.getParameter("pgtypeid").split("-").length == 3 && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            currency = req.getParameter("pgtypeid").split("-")[1];
            gateway_name = req.getParameter("pgtypeid").split("-")[0];

        }*/
        Calendar rightNow               = Calendar.getInstance();
        SimpleDateFormat sdf            = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
        Date date                       = null;

        res.setContentType("text/html");

        try
        {
            pageno  = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno  = 1;
            records = 30;
        }


        String fyear    = "";
        String fmonth   = "";
        String fdate    = "";
        String tyear    = "";
        String tmonth   = "";
        String tdate    = "";

        startTime   = startTime.trim();
        endTime     = endTime.trim();

        if (!functions.isValueNull(startTime))
        {
            startTime   = "00:00:00";
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
           /* String fromdate = req.getParameter("fdate");
            String todate   = req.getParameter("tdate");
            //from date
            Date frtzdate       = sdf.parse(fromdate);
            String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
            //to Date
            Date totzdate       = sdf.parse(todate);
            String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
            String fdatetime1   = functions.convertDateTimeToTimeZone1(fdatetime, timezone);
            String tdatetime1   = functions.convertDateTimeToTimeZone1(tdatetime, timezone);*/
            if (functions.isValueNull(timezone))
            {
                String fromdate = req.getParameter("fdate");
                String todate   = req.getParameter("tdate");

                if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
                {
                    req.setAttribute("catchError","Invalid From & To date");
                    RequestDispatcher rdmsg = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
                    rdmsg.forward(req,res);
                    return;
                }

                //from date
                Date frtzdate       = sdf.parse(fromdate);
                String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
                //to Date
                Date totzdate       = sdf.parse(todate);
                String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;
                String fdatetime1   = functions.convertDateTimeToTimeZone1(fdatetime, timezone);
                String tdatetime1   = functions.convertDateTimeToTimeZone1(tdatetime, timezone);
                String fdt[]        = fdatetime1.split(" ");
                String tdt[]        = tdatetime1.split(" ");
                date                = sdftimezone.parse(fdt[0]);
                rightNow.setTime(date);
                fdate       = String.valueOf(rightNow.get(Calendar.DATE));
                fmonth      = String.valueOf(rightNow.get(Calendar.MONTH));
                fyear       = String.valueOf(rightNow.get(Calendar.YEAR));
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
                    req.setAttribute("dateRangeIsValid","Report date days should not be greater than 30 days!");
                    rd2.forward(req,res);
                    return;
                }
            }
           else
            {
                log.error("inside else--------->");
            String fromdate     = req.getParameter("fdate");
            String todate       = req.getParameter("tdate");

                if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
                {
                    req.setAttribute("catchError","Invalid From & To date");
                    RequestDispatcher rdmsg = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
                    rdmsg.forward(req,res);
                    return;
                }

            date                = sdf.parse(fromdate);
            rightNow.setTime(date);
            fdate   = String.valueOf(rightNow.get(Calendar.DATE));
            fmonth  = String.valueOf(rightNow.get(Calendar.MONTH));
            fyear   = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date    = sdf.parse(todate);
            rightNow.setTime(date);
            tdate   = String.valueOf(rightNow.get(Calendar.DATE));
            tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            tyear   = String.valueOf(rightNow.get(Calendar.YEAR));



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
                    req.setAttribute("dateRangeIsValid","Report date days should not be greater than 30 days!");
                    rd2.forward(req,res);
                    return;
                   }
                }

            String startTimeArr[]   = startTime.split(":");
            String endTimeArr[]     = endTime.split(":");
            log.debug("From date dd::"+fdate+" MM::"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);
            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet          = new HashSet();
            TerminalManager terminalManager     = new TerminalManager();
            TerminalVO terminalVO               = new TerminalVO();
            functions                           = new Functions();
            if(functions.isValueNull(terminalId) && !terminalId.equalsIgnoreCase("all"))
            {
                terminalVO  = terminalManager.getTerminalByTerminalId(terminalId);
                if(terminalVO != null)
                {
                    terminalVO.setTerminalId("(" + terminalVO.getTerminalId() + ")");
                    accountid = terminalVO.getAccountId();
                }
                else
                {
                    terminalVO  = new TerminalVO();
                }
            }
            if (functions.isValueNull(cardtype)&& cardtype.equals("CP"))
            {
                // Entering for Card present (transaction_card_present)

                hash = transactionentry.listCardTransactions(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid, terminalVO, firstName, lastName, emailAddress, paymentId, customerId, dateType, statusflag, issuingBank, firstsix, lastfour);
                hash1 = transactionentry.getTrackingIdList(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid, terminalVO, firstName, lastName, emailAddress, paymentId, customerId, dateType, statusflag, issuingBank, firstsix, lastfour,transactionMode);
            }
            else
            {
                // Entering for Card not present(transaction_common)
                hash = transactionentry.listTransactions(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid, terminalVO, firstName, lastName, emailAddress, paymentId, customerId, dateType, statusflag, issuingBank, firstsix, lastfour,transactionMode);
                hash1 = transactionentry.getTrackingIdList(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid, terminalVO, firstName, lastName, emailAddress, paymentId, customerId, dateType, statusflag, issuingBank, firstsix, lastfour,transactionMode);
                log.debug("inside trasaction.java else+++"+hash1);
            }
            req.setAttribute("transactionsdetails", hash);
            req.setAttribute("TrackingIDList1", hash1);

        }
        catch (SystemError se)
        {
            log.error("System error occure",se);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        catch (ParseException e)
        {   log.error("Exception ::::",e);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        catch (Exception e)
        {   log.error("Exception ::::",e);

            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }

        session.setAttribute("bank",accountid);


        try
        {
            if (functions.isValueNull(merchantid))
            {
                Map<String,Object> merchantTemplateSetting  = new HashMap<String, Object>();
                merchantTemplateSetting                     = merchantConfigManager.getSavedMemberTemplateDetails(merchantid);
                req.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
            }
        }
        catch (Exception e)
        {
            log.error("Exception while getting template preference",e);
        }
        req.setAttribute("transactionMode",transactionMode);
        RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    public ValidationErrorList validateOptionalParameters(HttpServletRequest req)
    {
        List<InputFields> inputOptionalParameter= new ArrayList<InputFields>();
        inputOptionalParameter.add(InputFields.TRACKINGID_SMALL);
        inputOptionalParameter.add(InputFields.ARCHIVE_SMALL);
        inputOptionalParameter.add(InputFields.FIRSTNAME);
        inputOptionalParameter.add(InputFields.LASTNAME);
        inputOptionalParameter.add(InputFields.EMAILADDR);
        inputOptionalParameter.add(InputFields.PAYMENTID);
        inputOptionalParameter.add(InputFields.PAGENO);
        inputOptionalParameter.add(InputFields.RECORDS);
       // inputOptionalParameter.add(InputFields.CUSTOMER_ID);
        inputOptionalParameter.add(InputFields.FIRST_SIX);
        inputOptionalParameter.add(InputFields.LAST_FOUR);

/*        inputOptionalParameter.add(InputFields.FROMDATE);
        inputOptionalParameter.add(InputFields.TODATE);
        inputOptionalParameter.add(InputFields.FROMMONTH);
        inputOptionalParameter.add(InputFields.TOMONTH);
        inputOptionalParameter.add(InputFields.FROMYEAR);
        inputOptionalParameter.add(InputFields.TOYEAR);*/
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputOptionalParameter, errorList,true);

        return errorList;
    }






   /* public HashMap getTrackingIdList(String desc, String tdtstamp, String fdtstamp, String trackingid, String status, int records, int pageno, boolean archive,Set<String> gatewayTypeSet,String accountid,TerminalVO terminalVO,String firstName,String lastName,String emailAddress,String paymentId, String customerId,String dateType,String statusflag,String issuingBank ,String terminalId, String transactionMode, String firstsix, String lastfour ) throws SystemError
    {
        log.debug("Entering listTransactions for partner");
        Functions functions = new Functions();
        Connection cn   = Database.getRDBConnection();
        ResultSet rs    = null;
        if (*//*!Functions.isValidSQL(trackingid) || *//*!Functions.isValidSQL(desc) || !Functions.isValidSQL(status))
        {
            throw new SystemError("In valid data received");

        }
        HashMap hash        = null;
        String tablename    = "";
        String fields       = "";
        String orderby      = "";
        StringBuilder query = new StringBuilder();
        //Encoding for SQL Injection check

        Codec me        = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp        = ESAPI.encoder().encodeForSQL(me, fdtstamp);
        tdtstamp        = ESAPI.encoder().encodeForSQL(me, tdtstamp);
        //desc = ESAPI.encoder().encodeForSQL(me, desc);
        //trackingid = ESAPI.encoder().encodeForSQL(me, trackingid);
        String pRefund              = "false";
        PreparedStatement pstmt     = null;
        PreparedStatement pstmt1    = null;
        int counter                 = 1;

        int start   = 0; // start index
        int end     = 0; // end index
        start       = (pageno - 1) * records;
        end         = records;

        if (gatewayTypeSet.size() == 0)
        {
            gatewayTypeSet.add(SBMPaymentGateway.GATEWAY_TYPE);
        }

        try
        {
            tablename = "transaction_common";
            if (archive)
            {
                //tablename += "_archive";        //ToDO will be used once all the archive tables will be avialbale.
                tablename = "transaction_common_archive";
            }
            // fields = "t.trackingid as transid,t.toid,t.status,t.name,t.amount,t.captureamount,t.refundamount,t.currency,t.description,t.dtstamp,t.paymodeid,t.cardtype,t.cardtypeid,t.customerId,t.accountid,t.remark,t.emailAddr,t.terminalid,t.orderdescription,t.timestamp,t.paymentid";
            fields = "t.trackingid as transid";
            //fields = "t.trackingid as transid,t.amount as amountcount";
            //}
            //query.append("select " + fields + " from " + tablename + " AS t,bin_details AS bd where t.trackingid=bd.icicitransid");
            query.append("select ");
            query.append(fields);
            query.append(" from ");
            query.append(tablename);
            query.append(" as t ");
            if (!"all".equalsIgnoreCase(statusflag) || functions.isValueNull(issuingBank))
            {
                query.append("LEFT JOIN bin_details as b on t.trackingid = b.icicitransid ");
            }
            query.append("where t.trackingid>0 ");
            //query.append(" and t.toid IN(" + merchantid + ")");
          *//*  if (functions.isValueNull(merchantid))
            {
                query.append(" and t.toid ="+merchantid);
            }
            if (functions.isValueNull(partnerName))
            {
                query.append(" and t.totype in("+partnerName+")");
            }*//*
            *//*if (functions.isValueNull(customerId))
            {
                {
                    query.append(" AND t.customerId IN(");
                    query.append(customerId);
                    query.append(")");
                }
            }*//*
            if (functions.isValueNull(desc))
            {
                {
                    query.append(" AND t.description like '");
                    query.append(desc);
                    query.append("%'");
                }
            }
            if (functions.isValueNull(status))
            {
                if (status.equalsIgnoreCase("partialrefund"))
                {
                    status = "reversed";
                    pRefund = "true";
                }
                query.append(" and t.status= ? ");
            }
            if (!"all".equalsIgnoreCase(statusflag))
            {
                query.append(" and b."+statusflag+"='Y'");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                query.append(" and t.timestamp >= ?");
            }
            else
            {
                query.append(" and t.dtstamp >= ? ");
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                query.append(" and t.timestamp <= ? ");
            }
            else
            {
                query.append(" and t.dtstamp <= ? ");
            }
            *//*if (functions.isValueNull(desc))
            {
                query.append(" and t.description= ? ");
            }*//*
            if (accountid != null && !accountid.equals("") && !accountid.equals("null"))
            {
                query.append(" and t.accountid =? ");
            }

            *//*if (functions.isValueNull(gateway_name))
            {
                query.append(" and t.fromtype= ? ");
            }*//*
           *//* if (functions.isValueNull(currency))
            {
                query.append(" and t.currency= ? ");
            }
*//*
            if (functions.isValueNull(issuingBank))
            {
                query.append(" and b.issuing_bank= ? ");
            }
            if (functions.isValueNull(trackingid.toString()))
            {
                query.append(" and t.trackingid IN("+trackingid+")");
            }

            if (functions.isValueNull(paymentId))
            {
                query.append(" and t.paymentid= ? ");
            }

            if (functions.isValueNull(firstName))
            {
                query.append(" and t.firstname= ? ");
            }
            if (functions.isValueNull(lastName))
            {
                query.append(" and t.lastname=  ? ");
            }
            if (functions.isValueNull(emailAddress))
            {
                query.append(" and t.emailAddr= ? ");
            }
          *//*  if (functions.isValueNull(cardtype))
            {
                query.append(" and t.cardtype= ? ");
            }*//*
            if (terminalId != null && !terminalId.equals("") && !terminalId.equals("null"))
            {
                query.append(" and t.terminalid= ? ");
            }
            if (functions.isValueNull(customerId))
            {
                query.append(" and t.customerId=  ? ");
            }
            if (functions.isValueNull(transactionMode))
            {
                query.append(" and t.transaction_mode=  ? ");
            }

            if (functions.isValueNull(status) && pRefund.equalsIgnoreCase("true"))
            {
                query.append(" and captureamount > refundamount");
            }
            StringBuilder countquery = new StringBuilder("select count(*) from ( ");
            countquery.append(query.toString());
            countquery.append(" ) as temp");

            query.append(" order by transid DESC");
            log.debug("query::::" + query.toString());
            log.debug("countquery::::"+countquery.toString());
            pstmt = cn.prepareStatement(query.toString());
            pstmt1 = cn.prepareStatement(countquery.toString());

            if (functions.isValueNull(status))
            {
                pstmt.setString(counter, status);
                pstmt1.setString(counter, status);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(fdtstamp))
            {
                long milliSeconds = Long.parseLong(fdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startDate = formatter1.format(calendar.getTime());
                pstmt.setString(counter, startDate);
                pstmt1.setString(counter, startDate);
                counter++;
            }
            else
            {
                pstmt.setString(counter, fdtstamp);
                pstmt1.setString(counter, fdtstamp);
                counter++;
            }
            if ("TIMESTAMP".equals(dateType) && functions.isValueNull(tdtstamp))
            {
                long milliSeconds = Long.parseLong(tdtstamp + "000");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String endDate = formatter2.format(calendar.getTime());
                pstmt.setString(counter, endDate);
                pstmt1.setString(counter, endDate);
                counter++;
            }

            else
            {
                pstmt.setString(counter, tdtstamp);
                pstmt1.setString(counter, tdtstamp);
                counter++;
            }

            log.debug("pstmt:::----------::" + pstmt);
            log.debug("pstmt1::-----------:::" + pstmt1);
            *//*if (functions.isValueNull(desc))
            {
                pstmt.setString(counter,desc);
                pstmt1.setString(counter, desc);
                counter++;
            }*//*
            if (functions.isValueNull(accountid))
            {
                pstmt.setString(counter, accountid);
                pstmt1.setString(counter, accountid);
                counter++;
            }
            *//*if (functions.isValueNull(gateway_name))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, gateway_name));
                counter++;
            }*//*
          *//*  if (functions.isValueNull(currency))
            {
                pstmt.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                pstmt1.setString(counter, ESAPI.encoder().encodeForSQL(me, currency));
                counter++;
            }*//*
            if (functions.isValueNull(issuingBank))
            {
                pstmt.setString(counter, issuingBank);
                pstmt1.setString(counter, issuingBank);
                counter++;
            }
            *//*if (functions.isValueNull(trackingid))
            {
                pstmt.setString(counter, trackingid);
                pstmt1.setString(counter, trackingid);
                counter++;
            }*//*
            if (functions.isValueNull(paymentId))
            {
                pstmt.setString(counter, paymentId);
                pstmt1.setString(counter, paymentId);
                counter++;
            }
            if (functions.isValueNull(firstName))
            {
                pstmt.setString(counter, firstName);
                pstmt1.setString(counter, firstName);
                counter++;
            }
            if (functions.isValueNull(lastName))
            {
                pstmt.setString(counter, lastName);
                pstmt1.setString(counter, lastName);
                counter++;
            }
            if (functions.isValueNull(emailAddress))
            {
                pstmt.setString(counter, emailAddress);
                pstmt1.setString(counter, emailAddress);
                counter++;
            }
          *//*  if (functions.isValueNull(cardtype))
            {
                pstmt.setString(counter, cardtype);
                pstmt1.setString(counter, cardtype);
                counter++;
            }*//*
            if (functions.isValueNull(terminalId))
            {
                pstmt.setString(counter, terminalId);
                pstmt1.setString(counter, terminalId);
                counter++;
            }
            if (functions.isValueNull(customerId))
            {
                pstmt.setString(counter, customerId);
                pstmt1.setString(counter, customerId);
                counter++;
            }
            if (functions.isValueNull(transactionMode))
            {
                pstmt.setString(counter, transactionMode);
                pstmt1.setString(counter, transactionMode);
                counter++;
            }

            log.error("pstmt:::::" + pstmt);
            log.error("pstmt1:::::" + pstmt1);

            //hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), cn));
            hash    = Database.getHashMapFromResultSetForTransactionEntry(pstmt.executeQuery());
            rs      = pstmt1.executeQuery();
            int totalrecords = 0;
            if (rs.next())

                totalrecords = rs.getInt(1);
            log.debug("totalrecords::::::" + totalrecords);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

           *//* if (totalrecords > 0)
            {*//*
            hash.put("records", "" + (hash.size() - 2));
          *//*  }*//*
        }

        catch (SQLException se)
        {
            log.error("SQL Exception leaving listTransactions", se);
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }

        log.debug("Leaving listTransactions");
        return hash;
    }*/
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