import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import com.manager.dao.PartnerDAO;
import com.manager.vo.fraudruleconfVOs.RuleMasterVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
public class TransactionDetails extends HttpServlet
{
    private static Logger log = new Logger(TransactionDetails.class.getName());
    Functions functions = new Functions();
    ServletContext ctx = null;
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Date date1          = new Date();
        log.error("TransactionDetails startTime---" + date1.getTime());
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd2 = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());

        if(req.getParameter("doget")!=null && !req.getParameter("doget").equals("") && req.getParameter("doget").equals("true"))
        {
            doGet(req,res);
            return;
        }
        if (ctx == null) ctx = getServletContext();
        int start           = 0; // start index
        int end             = 0; // end index
        String str          = null;
        String perfectmatch = null;
        perfectmatch        = Functions.checkStringNull(req.getParameter("perfectmatch"));
        boolean flag        = false;
        String errormsg     = "";
        String massage      = "";
        Hashtable actionhash    = null;
        String iciciTransId     = null;
        String gateway          = null;
        String status           = req.getParameter("status");
        String accountid        = req.getParameter("accountid");
        String cardpresent      = req.getParameter("cardpresent");
        String EOL              = "<BR>";
        Hashtable hash          = null;
        Hashtable hash_payout = null;
        List<RuleMasterVO> ruleMasterVOList=null;
        String merchantId = null;
        boolean archive= false;
        archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();


        //Hashtable childhash=null;
        //Hashtable hash = new Hashtable();
        TransactionManager transactionManager   = new TransactionManager();
        ActionEntry entry                       = new ActionEntry();
        /*if (!req.getParameter("accountid").equalsIgnoreCase("0") && !req.getParameter("accountid").equals(""))*/
       /* if (functions.isValueNull(req.getParameter("accountid")))
        {
            accountid = req.getParameter("accountid");
        }*/

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid Input",e);
            errormsg    += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag        = false;
            log.debug("message..." + e.getMessage());
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        //This condition is for transaction details by trackingId doGet code shifted to doPost
        if(functions.isValueNull(req.getParameter("action")) && "TransactionDetails".equals(req.getParameter("action")) && functions.isValueNull(req.getParameter("STrackingid")) && !req.getParameter("accountid").equalsIgnoreCase("0") )
        {
            String icicitransid = req.getParameter("STrackingid");
           /* boolean archive     = false;

            if(functions.isValueNull(req.getParameter("archive")))
            {
                archive = Boolean.valueOf(req.getParameter("archive")).booleanValue();
            }*/
            try
            {
                if (cardpresent.equals("CP"))
                {
                    Date date2  = new Date();
                    log.error("getCardPresentTransactionDetails startTime---" + date2.getTime());
                    hash        = transactionManager.getCardPresentTransactionDetails(icicitransid, archive, accountid);
                    //childhash=transactionManager.getChildDetailscardPresent(iciciTransId, status);
                    hash.put("CP","CP");
                    log.error("getCardPresentTransactionDetails End time--->" + (new Date()).getTime());
                    log.error("getCardPresentTransactionDetails Diff time--->" + ((new Date()).getTime() - date2.getTime()));

                    Date date3  = new Date();
                    log.error("getActionHistoryByTrackingIdAndGatewayCP startTime---" + date3.getTime());
                    actionhash  = entry.getActionHistoryByTrackingIdAndGatewayCP(iciciTransId,gateway);
                    log.error("getActionHistoryByTrackingIdAndGatewayCP End time--->" + (new Date()).getTime());
                    log.error("getActionHistoryByTrackingIdAndGatewayCP Diff time--->" + ((new Date()).getTime() - date3.getTime()));
                }
                else
                {
                    Date date4  = new Date();
                    log.error("getTransactionDetails startTime---" + date4.getTime());

                    hash            = transactionManager.getTransactionDetails(icicitransid, archive,accountid);

                     hash_payout    = transactionManager.getPayoutStartDetails(icicitransid);

                    // childhash=transactionManager.getChildDetails(iciciTransId, status);
                    log.error("getTransactionDetails End time--->" + (new Date()).getTime());
                    log.error("getTransactionDetails Diff time--->" + ((new Date()).getTime() - date4.getTime()));

                    Date date5  = new Date();
                    log.error("getActionHistoryByTrackingIdAndGateway startTime---" + date5.getTime());

                    actionhash = entry.getActionHistoryByTrackingIdAndGateway(iciciTransId,gateway);
                    log.error("getActionHistoryByTrackingIdAndGateway End time--->" + (new Date()).getTime());
                    log.error("getActionHistoryByTrackingIdAndGateway Diff time--->" + ((new Date()).getTime() - date5.getTime()));
                }
                if(req.getAttribute("message")!=null && functions.isValueNull((String) req.getAttribute("message")))
                {
                    massage = "<center><font class=\"text\" face=\"arial\"><b>" + (String) req.getAttribute("message") + "</b></font></center>";
                }
                log.debug("fetch record through getTransactionDetails");
                req.setAttribute("transactionsdetails", hash);
                req.setAttribute("transpayoutdetails",hash_payout);

                req.setAttribute("message", massage);
                //req.setAttribute("childhash",childhash);
                entry.closeConnection();
                req.setAttribute("actionHistory", actionhash);
                ruleMasterVOList = getFraudDetails(icicitransid);
                req.setAttribute("transactionDetailsFraud",ruleMasterVOList);
                log.debug("forwarding to transactionDetails.jsp");
                //hash=null;
                RequestDispatcher rd = req.getRequestDispatcher("/transactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);

            }
            catch (Exception e)
            {   log.error("Exception in doGet method",e);
                errormsg    += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
                flag        = false;
                log.debug("message..." + e.getMessage());
                req.setAttribute("errormessage",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            return;
        }
        String name             = req.getParameter("name");
        String desc             = req.getParameter("desc");
        String amount           = req.getParameter("amount");
        String refundamount     = req.getParameter("refundamount");
        String firstfourofccnum = req.getParameter("firstfourofccnum");
        String lastfourofccnum  = req.getParameter("lastfourofccnum");
        String emailaddr        = req.getParameter("emailaddr");
        String telno=  req.getParameter("telno");
        String telnocc= req.getParameter("telnocc");
        String orderdesc        = req.getParameter("orderdesc");
        //String status = req.getParameter("status");
        String statusType       = req.getParameter("statusType");
        String statusflag       = req.getParameter("statusflag");
        String trackingid       = req.getParameter("STrackingid");
        String auth             = req.getParameter("AUTHORIZATION_CODE");
        String arn              = req.getParameter("ARN");
        String rrn              = req.getParameter("RRN");
        String toid             = "";
        if (!req.getParameter("toid").equalsIgnoreCase("0"))
        {
            toid = req.getParameter("toid");
        }
        String fdate    = req.getParameter("fdate");
        String tdate    = req.getParameter("tdate");
        String fmonth   = req.getParameter("fmonth");
        String tmonth   = req.getParameter("tmonth");
        String fyear    = req.getParameter("fyear");
        String tyear    = req.getParameter("tyear");
        String remark   = req.getParameter("remark");
        String pgtypeid     = req.getParameter("pgtypeid");
        String startTime    = req.getParameter("starttime");
        String endTime      = req.getParameter("endtime");
        String dateType     = req.getParameter("datetype");
        String issuing_bank = req.getParameter("issuing_bank");
        String cardtype     = req.getParameter("cardtype");
        String paymentid    = req.getParameter("paymentid");
        String customerid   = req.getParameter("customerid");
        String transactionMode = req.getParameter("transactionMode");
        String totype = req.getParameter("partnerid");
        String bankaccount =  req.getParameter("bankaccount");
//        System.out.println("TransactionDetails Bankaccount ============> "+bankaccount);
        PartnerDAO partnerDAO = new PartnerDAO();
        try
        {
            if(functions.isValueNull(totype))
            {
                totype = partnerDAO.getPartnerName(totype);
            }

        }
        catch (Exception e)
        {
            log.error("exception e" + e);
        }

        if(functions.isValueNull(bankaccount))
        {
            if (!ESAPI.validator().isValidInput(req.getParameter("bankaccount"), req.getParameter("bankaccount"), "Number", 20, false))
            {
                errormsg    += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Invalid Bank Account Number. It accepts Only Numeric value" + EOL + "</b></font></center>";
            }
        }
        if(functions.isValueNull(desc))
        {
            System.out.println("Inside descriptor ==========================>");
            if (!ESAPI.validator().isValidInput(desc, desc, "Description", 100, true))
            {
                System.out.println("Inside Error ===========================>");
                errormsg    += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + "Invalid Description" + EOL + "</b></font></center>";
            }
        }
        if(!errormsg.toString().isEmpty()){
            System.out.println("Inside Error part 2 =======================>");
            req.setAttribute("errormessage", errormsg);
            RequestDispatcher requestDispatcher = req.getRequestDispatcher("/transactions.jsp");
            requestDispatcher.forward(req, res);
            return;
        }

        //String gateway = "";
        String currency     = "";
        String gateway_name = "";

        if (req.getParameter("pgtypeid") != null && req.getParameter("pgtypeid").split("-").length == 3
                && !req.getParameter("pgtypeid").equalsIgnoreCase(""))
        {
            gateway         = req.getParameter("pgtypeid").split("-")[2];
            currency        = req.getParameter("pgtypeid").split("-")[1];
            gateway_name    = req.getParameter("pgtypeid").split("-")[0];
        }
        if (!(req.getParameter("pgtypeid").split("-").length == 3))
        {
            gateway_name = req.getParameter("pgtypeid");
        }

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

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

        String startTimeArr[]   = startTime.split(":");
        String endTimeArr[]     = endTime.split(":");
        String sp="-";
        String ap="0";
        if(fmonth.length()==1)  fmonth= ap+fmonth;
        if(fdate.length()==1)   fdate= ap+fdate;
        if(tmonth.length()==1)  tmonth= ap+tmonth;
        if(tdate.length()==1)  tdate= ap+tdate;

        String fdatetime1=fyear+sp+fmonth+sp+fdate+" "+startTime;
        String tdatetime1=tyear+sp+tmonth+sp+tdate+" "+endTime;
        log.error("fdatetime1-->"+fdatetime1);
        log.error("tdatetime1-->"+tdatetime1);
        String fdtstamp         =  Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
        String tdtstamp         = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);
       // boolean archive         = Boolean.valueOf(req.getParameter("archive")).booleanValue();
      /*  long fdtstamp2= Long.parseLong(fdtstamp);
        long tdtstamp2= Long.parseLong(tdtstamp);
        Date fdatetime=new Date((long)fdtstamp2*1000);
        Date tdatetime=new Date((long)tdtstamp2*1000);*/

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);
        req.setAttribute("pgtypeid",gateway);
        req.setAttribute("toid",toid);
        req.setAttribute("accountid",accountid);
        req.setAttribute("transactionDetailsFraud",ruleMasterVOList);

        PrintWriter out = res.getWriter();

        //Functions fn=new Functions();
        int pageno                  = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records                 = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);
        StringBuffer trackingIds    = new StringBuffer();
        StringBuffer descriptionIds        = new StringBuffer();

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
        if (functions.isValueNull(desc))
        {
            desc = desc.trim();
            List<String> descList = new ArrayList<>();

            if (desc.contains(","))
            {
                descList = Arrays.asList(desc.split(","));

            }else if(desc.contains(" "))
            {
                descList = Arrays.asList(desc.split(" "));
            }

            if(descList != null && descList.size() > 0){

                for (String ids : descList)
                {
                    if (descriptionIds.length() == 0)
                    {


                        if (!ESAPI.validator().isValidInput(ids.toString(), ids.toString(), "Description", 100, true)){
                            //errorMessage
                            errormsg = errormsg + "Invalid Descriptions" + EOL;
                            break;
                        }
                        descriptionIds.append("'" + ids + "'");
                    }
                    else
                    {

                        if (!ESAPI.validator().isValidInput(ids.toString(), ids.toString(), "Description", 100, true)){
                            //errorMessage
                            errormsg = errormsg + "Invalid Descriptions" + EOL;
                            break;
                        }
                        descriptionIds.append(",'" + ids + "'");
                    }

                }
                if(!errormsg.toString().isEmpty()){
                    req.setAttribute("error", errormsg);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/transactions.jsp");

                    requestDispatcher.forward(req, res);
                    return;
                }


                //error message return
                desc = descriptionIds.toString();

            }
        }
        try
        {
            Set<String> gatewayTypeSet = GatewayTypeService.getGatewayHash(gateway);
            HashMap trackinghash;
            SimpleDateFormat sdf            = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdftimezone    = new SimpleDateFormat("yyyy-MM-dd");
            //from date
           /* Date frtzdate       = sdf.parse(fdate);
            String fdatetime    = sdftimezone.format(frtzdate)+ " " + startTime;
            //to Date
            Date totzdate       = sdf.parse(tdate);
            String tdatetime    = sdftimezone.format(totzdate) + " " + endTime;*/
          /*  String fdatetime1   = fdatetime;
            String tdatetime1   = tdatetime;*/

           /* log.error("fdatetime1-->"+fdatetime1);
            log.error("tdatetime1-->"+tdatetime1);*/

            if(!functions.isValueNull(trackingid))
            {
                if(!functions.isValueNull(orderdesc))
                {
                  if(!functions.isValueNull(desc))
                {
                    log.error("inside if dateRangeIsValid check  trackingid "+trackingid+" orderdesc "+orderdesc+" desc "+desc );
                boolean dateRangeIsValid = datedifference(fdatetime1, tdatetime1);
                log.error("dateRangeIsValid-->"+dateRangeIsValid);
                if (!dateRangeIsValid)
                {
                    errormsg += "<center><font class=\"text\" face=\"arial\"><b>" + "Report date days should not be greater than 90 days!" + EOL + "</b></font></center>";

                    req.setAttribute("dateRangeIsValid", errormsg);
                    rd2.forward(req, res);
                    return;
                }
                }
            }
            }

            if (cardpresent.equals("CP"))
            {
                Date date6  = new Date();
                log.error("cardPresentlistTransactions startTime---" + date6.getTime());
                hash            = transactionManager.cardPresentlistTransactions(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid, paymentid, dateType,customerid,telno,telnocc,totype);
                trackinghash    = transactionManager.cardPresentlistTrackingIds(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid, paymentid, dateType,customerid,telno,telnocc,totype);
                log.error("cardPresentlistTransactions End time--->" + (new Date()).getTime());
                log.error("cardPresentlistTransactions Diff time--->" + ((new Date()).getTime() - date6.getTime()));
            }
            else
            {
                Date date7  = new Date();
                log.error("listTransactions startTime---" + date7.getTime());
                if(functions.isValueNull(statusType) && "detail".equals(statusType)){
                    hash            = transactionManager.listTransactionsBasedOnDetailStatus(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid,paymentid, dateType ,arn,rrn,auth,statusType,customerid,transactionMode,telno,telnocc,totype);
                    trackinghash    = transactionManager.listTrackingidsBasedOnDetailStatus(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid, paymentid, dateType, arn, rrn, auth, statusType,customerid,transactionMode,telno,telnocc,totype);
                }else {

                    hash            = transactionManager.listTransactions(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid,paymentid, dateType ,arn,rrn,auth,customerid,transactionMode,telno, telnocc,totype,bankaccount);
                    trackinghash    = transactionManager.listTrackingIds(toid, trackingIds.toString(), name, desc, orderdesc, amount, refundamount, firstfourofccnum, lastfourofccnum, emailaddr, tdtstamp, fdtstamp, status, records, pageno, perfectmatch, archive, gatewayTypeSet, remark, cardtype, issuing_bank, statusflag, gateway_name, currency, accountid, paymentid, dateType, arn, rrn, auth,customerid,transactionMode,telno,telnocc,totype);
                }
                log.error("listTransactions End time--->" + (new Date()).getTime());
                log.error("listTransactions Diff time--->" + ((new Date()).getTime() - date7.getTime()));
            }
            req.setAttribute("transactionsdetails", hash);
            req.setAttribute("TrackingIDList1", trackinghash);
            req.setAttribute("transactionMode",transactionMode);
            req.setAttribute("transpayoutdetails",hash_payout);
        }
        catch (Exception e)
        {   log.error("Exception:::: errror in doPost",e);
            Functions.NewShowConfirmation1("Error", "Internal System Error while getting list of Transactions");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        log.error("Leaving TransactionDetails End time--->" + (new Date()).getTime());
        log.error("TransactionDetails Diff time--->" + ((new Date()).getTime() - date1.getTime()));
    }
    public List<RuleMasterVO> getFraudDetails(String iciciTransId)throws SystemError
    {
        List<RuleMasterVO> ruleMasterVOList = new ArrayList<>();
        RuleMasterVO ruleMasterVO           = null;
        Connection conn             = null;
        PreparedStatement pstmt     = null;
        ResultSet rs                = null;
        StringBuffer stringBuffer   = new StringBuffer();
        try
        {
            conn    = Database.getRDBConnection();
            stringBuffer.append("SELECT rt.rulename,rt.rulescore,rt.status FROM fraudtransaction_rules_triggered AS rt JOIN fraud_transaction AS ft ON rt.fraud_transid=ft.fraud_transaction_id WHERE ft.trackingid=?");
            pstmt   =conn.prepareStatement(stringBuffer.toString());
            pstmt.setString(1,iciciTransId);
            rs      = pstmt.executeQuery();
            while (rs.next()){
                ruleMasterVO    = new RuleMasterVO();

                ruleMasterVO.setRuleName(rs.getString("rulename"));
                ruleMasterVO.setDefaultScore(rs.getString("rulescore"));
                ruleMasterVO.setDefaultStatus(rs.getString("status"));

                ruleMasterVOList.add(ruleMasterVO);
            }
        }
        catch (SQLException e)
        {
            throw new SystemError(e.toString());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return ruleMasterVOList;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator               = new InputValidator();
        List<InputFields> inputFieldsListMandatory  = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.COMMASEPRATED_TRACKINGID_TRA);
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.NAME_SMALL);
     //   inputFieldsListMandatory.add(InputFields.DESC);
        inputFieldsListMandatory.add(InputFields.AMOUNT);
        inputFieldsListMandatory.add(InputFields.FIRSTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.LASTFOURCCNUM);
        inputFieldsListMandatory.add(InputFields.EMAILADDR);
        inputFieldsListMandatory.add(InputFields.ORDERDESC);
        inputFieldsListMandatory.add(InputFields.STATUS_LIST);
        inputFieldsListMandatory.add(InputFields.TOID);
        inputFieldsListMandatory.add(InputFields.CARD_TYPE);
        inputFieldsListMandatory.add(InputFields.CARD_ISSUING_BANK);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        //inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.STARTTIME);
        inputFieldsListMandatory.add(InputFields.ENDTIME);
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.AUTHORIZATION_CODE);
        inputFieldsListMandatory.add(InputFields.ARN);
        inputFieldsListMandatory.add(InputFields.RRN);
        inputFieldsListMandatory.add(InputFields.TELNO);
        inputFieldsListMandatory.add(InputFields.TELCC);
        inputFieldsListMandatory.add(InputFields.PARTNERID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

    public Boolean datedifference(String startdate, String endDate){
        boolean isvaliddate= false;
        try{

            Date fromDate = stringToDate(startdate);
            Date toDate = stringToDate(endDate);
            LocalDateTime from = LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime to= LocalDateTime.ofInstant(toDate.toInstant(), ZoneId.systemDefault());

            if(Duration.between(from, to).toDays() <= 96){

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