import com.directi.pg.*;
import com.manager.MerchantConfigManager;
import com.manager.TerminalManager;
import com.manager.enums.TemplatePreference;
import com.manager.vo.TerminalVO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sagar on 19-Apr-18.
 */
public class SetColumnConfig extends HttpServlet
{
    private static Logger log = new Logger(SetColumnConfig.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();
        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        String memberids                            = (String) session.getAttribute("merchantid");
        Functions functions = new Functions();
        String Date         = "";
        String Time_Zone    = "";
        String TrackingID   = "";
        String OrderId      = "";
        String OrderDescription = "";
        String CardHoldername   = "";
        String CustomerEmail    = "";
        String CustomerID       = "";
        String PayMode          = "";
        String CardType         = "";
        String Amount           = "";
        String IssuingBank      = "";
        String RefundedAmt      = "";
        String Currency         = "";
        String Status           = "";
        String Remark           = "";
        String Terminal         = "";
        String LastUpdateDate   = "";
        String issuingBank      = "";
        String transactionMode      = "";

        String desc         = null;
        String trackingid   = null;
        String status       = null;
        String accountid    = null;
        String terminalId   = null;
        String firstName    = null;
        String lastName     = null;
        String emailAddress = null;
        String paymentId    = null;
        String customerId   = null;
        Calendar rightNow       = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        Date date               = null;
        String startTime        = req.getParameter("starttime");
        String endTime          = req.getParameter("endtime");
        String dateType         = req.getParameter("datetype");
        String statusflag       = req.getParameter("statusflag");
        String firstsix     = "";
        String lastfour     = "";
        int pageno          = 1;
        int records         = 30;
        boolean archive     = false;

        /*trackingid=req.getParameter(TemplatePreference.Transactions_TrackingID.toString());*/
        trackingid  = req.getParameter("trackingid");
        issuingBank = req.getParameter("issuingbank");

        Date        = req.getParameter(TemplatePreference.Transactions_Date.toString());
        Time_Zone   = req.getParameter(TemplatePreference.Transactions_TimeZone.toString());
        TrackingID  = req.getParameter(TemplatePreference.Transactions_TrackingID.toString());
        PayMode     = req.getParameter(TemplatePreference.Transactions_PayMode.toString());
        CardType    = req.getParameter(TemplatePreference.Transactions_CardType.toString());
        Currency    = req.getParameter(TemplatePreference.Transactions_Currency.toString());
        OrderId     = req.getParameter(TemplatePreference.Transactions_OrderId.toString());

        OrderDescription    = req.getParameter(TemplatePreference.Transactions_OrderDescription.toString());
        Amount              = req.getParameter(TemplatePreference.Transactions_Amount.toString());
        CardHoldername      = req.getParameter(TemplatePreference.Transactions_CardHoldername.toString());
        RefundedAmt         = req.getParameter(TemplatePreference.Transactions_RefundedAmt.toString());
        CustomerEmail       = req.getParameter(TemplatePreference.Transactions_CustomerEmail.toString());
        Status              = req.getParameter(TemplatePreference.Transactions_Status.toString());
        Terminal            = req.getParameter(TemplatePreference.Transactions_Terminal.toString());
        CustomerID          = req.getParameter(TemplatePreference.Transactions_CustomerID.toString());
        Remark              = req.getParameter(TemplatePreference.Transactions_Remark.toString());
        LastUpdateDate      = req.getParameter(TemplatePreference.Transactions_LastUpdateDate.toString());
        IssuingBank         = req.getParameter(TemplatePreference.Transactions_IssuingBank.toString());
        transactionMode     = req.getParameter(TemplatePreference.Transactions_Mode.toString());

        boolean isMerchantSaved         = false;
        boolean isMerchantDeleted       = false;
        Map<String,Object> merchantTemplateInformationInsert    = new HashMap<String, Object>();
        Map<String,Object> merchantTemplateInformationDelete    = new HashMap<String, Object>();
        ServletContext application                              = getServletContext();
        try
        {

            Hashtable hash = new Hashtable();
            RequestDispatcher rd;

            log.debug("From ::"+req.getParameter("fromdate")+" Todate::"+req.getParameter("todate"));

            String fromdate = req.getParameter("fdate");
            String todate   = req.getParameter("tdate");
            /*String fmonth = req.getParameter("fmonth");
            String tmonth = req.getParameter("tmonth");
            String fyear = req.getParameter("fyear");
            String tyear = req.getParameter("tyear");*/
            //from Date
            date            = sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate    = String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear    = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date            = sdf.parse(todate);
            rightNow.setTime(date);
            String tdate    = String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth   = String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear    = String.valueOf(rightNow.get(Calendar.YEAR));

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

            log.debug("From date dd::"+fdate+" MM;;"+fmonth+" YY::"+ fyear + " To date dd::" + tdate + " MM::" + tmonth+" YY::"+tyear);

            /*String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");*/
            String fdtstamp     = Functions.converttomillisec(fmonth, fdate, fyear, startTimeArr[0], startTimeArr[1], startTimeArr[2]);
            String tdtstamp     = Functions.converttomillisec(tmonth, tdate, tyear, endTimeArr[0], endTimeArr[1], endTimeArr[2]);

            TransactionEntry transactionentry   = (TransactionEntry) session.getAttribute("transactionentry");
            Set<String> gatewayTypeSet          = new HashSet();
            TerminalManager terminalManager     = new TerminalManager();
            TerminalVO terminalVO               = new TerminalVO();
            functions                           = new Functions();
            if(functions.isValueNull(terminalId) && !terminalId.equalsIgnoreCase("all"))
            {
                terminalVO  = terminalManager.getTerminalByTerminalId(terminalId);
                terminalVO.setTerminalId("("+terminalVO.getTerminalId()+")");
                accountid   = terminalVO.getAccountId();
            }

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
            gatewayTypeSet.add("qwipi");
            gatewayTypeSet.add("ecore");
            gatewayTypeSet.add("common");
            log.error("transactionMode----> " + transactionMode);
            log.error("Status----> "+Status);
            hash = transactionentry.listTransactions(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid,terminalVO,firstName, lastName, emailAddress,paymentId,customerId,dateType,statusflag, issuingBank,firstsix,lastfour,"" );
            HashMap hash1 = transactionentry.getTrackingIdLists(desc, tdtstamp, fdtstamp, trackingid, status, records, pageno, archive, gatewayTypeSet, accountid, terminalVO, firstName, lastName, emailAddress, paymentId, customerId, dateType, statusflag, issuingBank, firstsix, lastfour,"" );

            log.debug("Transections are set successfully ");

            req.setAttribute("transactionsdetails", hash);
            req.setAttribute("TrackingIDList1", hash1);
            log.debug("set col confg---"+hash1);

            log.info("Redirect to ::::: transactions.jsp");

            log.debug("From ::"+req.getParameter("fromdate")+" Todate::"+req.getParameter("todate"));



            if (functions.isValueNull(memberids))

            {

                Map<String,Object> presentTemplateDetails=merchantConfigManager.getSavedMemberTemplateDetails(memberids);
                log.debug("Date:::"+Date+" TrackingID:::"+TrackingID+":::: PayMode:::"+PayMode+":::: CardType:::"+CardType+":::: IssuingBank:::"+IssuingBank+":::: Time_Zone:::"+Time_Zone);

                if(!functions.isValueNull(Date) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Date.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Date.name(),Date);
                }
                else if(functions.isValueNull(Date) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Date.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Date.name(),Date);
                }

                if(!functions.isValueNull(Time_Zone) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_TimeZone.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_TimeZone.name(),Time_Zone);
                }
                else if(functions.isValueNull(Time_Zone)  && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_TimeZone.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_TimeZone.name(),Time_Zone);
                }

                if(!functions.isValueNull(TrackingID) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_TrackingID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_TrackingID.name(),TrackingID);
                }
                else if(functions.isValueNull(TrackingID)  && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_TrackingID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_TrackingID.name(),TrackingID);
                }

                if(!functions.isValueNull(PayMode) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_PayMode.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_PayMode.name(),PayMode);
                }
                else if(functions.isValueNull(PayMode) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_PayMode.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_PayMode.name(),PayMode);
                }

                if(!functions.isValueNull(CardType) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_CardType.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_CardType.name(),CardType);
                }
                else if(functions.isValueNull(CardType) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_CardType.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_CardType.name(),CardType);
                }

                if(!functions.isValueNull(IssuingBank) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_IssuingBank.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_IssuingBank.name(),IssuingBank);
                }
                else if(functions.isValueNull(IssuingBank) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_IssuingBank.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_IssuingBank.name(),IssuingBank);
                }
                if(!functions.isValueNull(Currency) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Currency.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Currency.name(),Currency);
                }
                else if(functions.isValueNull(Currency) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Currency.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Currency.name(),Currency);
                }

                if(!functions.isValueNull(OrderId) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_OrderId.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_OrderId.name(),OrderId);
                }
                else if(functions.isValueNull(OrderId) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_OrderId.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_OrderId.name(),OrderId);
                }

                if(!functions.isValueNull(OrderDescription) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_OrderDescription.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_OrderDescription.name(),OrderDescription);
                }
                else if(functions.isValueNull(OrderDescription) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_OrderDescription.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_OrderDescription.name(),OrderDescription);
                }

                if(!functions.isValueNull(CustomerEmail) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_CustomerEmail.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_CustomerEmail.name(),CustomerEmail);
                }
                else if(functions.isValueNull(CustomerEmail) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_CustomerEmail.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_CustomerEmail.name(),CustomerEmail);
                }

                if(!functions.isValueNull(CardHoldername) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_CardHoldername.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_CardHoldername.name(),CardHoldername);
                }
                else if(functions.isValueNull(CardHoldername) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_CardHoldername.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_CardHoldername.name(),CardHoldername);
                }

                if(!functions.isValueNull(RefundedAmt) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_RefundedAmt.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_RefundedAmt.name(),RefundedAmt);
                }
                else if(functions.isValueNull(RefundedAmt) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_RefundedAmt.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_RefundedAmt.name(),RefundedAmt);
                }

                if(!functions.isValueNull(Amount) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Amount.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Amount.name(),Amount);
                }
                else if(functions.isValueNull(Amount) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Amount.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Amount.name(),Amount);
                }

                if(!functions.isValueNull(Status) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Status.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Status.name(),Status);
                }
                else if(functions.isValueNull(Status) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Status.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Status.name(),Status);
                }

                if(!functions.isValueNull(Terminal) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Terminal.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Terminal.name(),Terminal);
                }
                else if(functions.isValueNull(Terminal) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Terminal.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Terminal.name(),Terminal);
                }

                if(!functions.isValueNull(CustomerID) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_CustomerID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_CustomerID.name(),CustomerID);
                }
                else if(functions.isValueNull(CustomerID) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_CustomerID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_CustomerID.name(),CustomerID);
                }

                if(!functions.isValueNull(Remark) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Remark.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Remark.name(),Remark);
                }
                else if(functions.isValueNull(Remark) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Remark.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Remark.name(),Remark);
                }

                if(!functions.isValueNull(LastUpdateDate) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_LastUpdateDate.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_LastUpdateDate.name(),LastUpdateDate);
                }
                else if(functions.isValueNull(LastUpdateDate) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_LastUpdateDate.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_LastUpdateDate.name(),LastUpdateDate);
                }

                if(!functions.isValueNull(transactionMode) && presentTemplateDetails.containsKey(TemplatePreference.Transactions_Mode.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.Transactions_Mode.name(),transactionMode);
                }
                else if(functions.isValueNull(transactionMode) && !presentTemplateDetails.containsKey(TemplatePreference.Transactions_Mode.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.Transactions_Mode.name(),transactionMode);
                }

                isMerchantDeleted   = merchantConfigManager.deleteMemberTemplateDetails(merchantTemplateInformationDelete,memberids);
                isMerchantSaved     = merchantConfigManager.insertMemberTemplateDetails(merchantTemplateInformationInsert,memberids);
                //isMerchantUpdated = merchantConfigManager.updateMemberTemplateDetails(merchantTemplateInformationUpdate,memberids);
                log.debug("IsSaved::::"+isMerchantSaved+"isDeleted:::::"+isMerchantDeleted);


                        /*RequestDispatcher rd = req.getRequestDispatcher("/servlet/Transactions?ctoken="+user.getCSRFToken());
                        rd.forward(req, res);*/
                Map<String,Object> merchantTemplateSetting  = new HashMap<String, Object>();
                merchantTemplateSetting                     = merchantConfigManager.getSavedMemberTemplateDetails(memberids);
                req.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
                RequestDispatcher view                      = req.getRequestDispatcher("/transactions.jsp?ctoken="+user.getCSRFToken());
                view.forward(req, res);

            }


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
    }
}
