package net.partner;

import com.directi.pg.*;
import com.manager.MerchantConfigManager;
import com.manager.enums.TemplatePreference;
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
 * Created by Kanchan on 16-11-2022.
 */
public class SetColumnConfig extends HttpServlet
{
    private static Logger logger= new Logger(SetColumnConfig.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        logger.error("Entering in SetColumnConfig.java... ");
        HttpSession session= request.getSession();
        PartnerFunctions partner= new PartnerFunctions();
        User user= (User)session.getAttribute("ESAPIUserSessionKey");
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        MerchantConfigManager merchantConfigManager= new MerchantConfigManager();
        String partnerid= (String) session.getAttribute("merchantid");
        Functions functions= new Functions();
        String Date= "";
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
        String RefundedAmt      = "";
        String Currency         = "";
        String Status           = "";
        String Remark           = "";
        String Terminal         = "";
        String LastUpdateDate   = "";
        String transactionMode      = "";
        String PaymentId      = "";
        String PartnerId      = "";
        String MerchantId      = "";

        String desc             = request.getParameter("desc");
        String trackingid       = request.getParameter("trackingid");
        String issuingbank       = request.getParameter("issuingbank");
        String status           = request.getParameter("status");
        String firstName        = request.getParameter("firstname");
        String lastName         = request.getParameter("lastname");
        String emailAddress     = request.getParameter("emailaddr");
        String paymentId        = request.getParameter("paymentid");
        String customerId       = request.getParameter("customerid");
        String accountid        = request.getParameter("accountid");
        String terminalid       = request.getParameter("terminalid");
        String merchantid   =      request.getParameter("memberid");
        String transactionmode = request.getParameter("transactionMode");
        String cardtype = request.getParameter("cardtype");
        String pid = request.getParameter("pid");
        String cardpresent= request.getParameter("cardpresent");
        String currency= null;
        String partnerName= null;
        String error            = "";
        boolean archive         = false;
        archive= Boolean.valueOf(request.getParameter("archive"));
        int pageno              = 1;
        int records             = 30;
        String starttime        = request.getParameter("starttime");
        String endtime        = request.getParameter("endtime");
        String datetype= request.getParameter("datetype");
        String statusflag= request.getParameter("statusflag");
        Calendar rightNow       = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/MM/yyyy");
        Date date               = null;

        Date=   request.getParameter(TemplatePreference.partnerTransactions_Transaction_Date1.toString());
        Time_Zone= request.getParameter(TemplatePreference.partnerTransactions_TimeZone.toString());
        TrackingID= request.getParameter(TemplatePreference.partnerTransactions_TrackingID.toString());
        PaymentId= request.getParameter(TemplatePreference.partnerTransactions_PaymentID.toString());
        PartnerId= request.getParameter(TemplatePreference.partnerTransactions_PartnerID.toString());
        MerchantId= request.getParameter(TemplatePreference.partnerTransactions_MerchantID.toString());
        OrderId= request.getParameter(TemplatePreference.partnerTransactions_OrderID.toString());
        OrderDescription= request.getParameter(TemplatePreference.partnerTransactions_OrderDescription.toString());
        CardHoldername= request.getParameter(TemplatePreference.partnerTransactions_Card_Holder_Name.toString());
        CustomerEmail= request.getParameter(TemplatePreference.partnerTransactions_Customer_Email.toString());
        CustomerID= request.getParameter(TemplatePreference.partnerTransactions_CustomerID.toString());
        PayMode= request.getParameter(TemplatePreference.partnerTransactions_PaymentMode.toString());
        CardType= request.getParameter(TemplatePreference.partnerTransactions_PaymentBrand.toString());
        transactionMode= request.getParameter(TemplatePreference.transactions_mode.toString());
        Amount= request.getParameter(TemplatePreference.partnerTransactions_Amount.toString());
        RefundedAmt= request.getParameter(TemplatePreference.partnerTransactions_RefundAmount.toString());
        Currency= request.getParameter(TemplatePreference.partnerTransactions_Currency.toString());
        Status= request.getParameter(TemplatePreference.partnerTransactions_Status1.toString());
        Remark= request.getParameter(TemplatePreference.partnerTransactions_Remark.toString());
        Terminal= request.getParameter(TemplatePreference.partnerTransactions_TerminalID.toString());
        LastUpdateDate= request.getParameter(TemplatePreference.partnerTransactions_LastUpdateDate.toString());

        boolean isMerchantSaved         = false;
        boolean isMerchantDeleted       = false;
        Map<String,Object> merchantTemplateInformationInsert    = new HashMap<String, Object>();
        Map<String,Object> merchantTemplateInformationDelete    = new HashMap<String, Object>();
        ServletContext application                              = getServletContext();

        try
        {
            HashMap hash1= new HashMap();
            HashMap trackinghash= new HashMap();
            String amountcount= null;
            RequestDispatcher rd;
            StringBuffer trackingIds= new StringBuffer();
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

            String gateway          = "";
            String gateway_currency  = "";
            String gateway_name     = "";

            if (request.getParameter("pgtypeid")!=null && request.getParameter("pgtypeid").split("-").length == 3 && !request.getParameter("pgtypeid").equalsIgnoreCase(""))
            {
                gateway         = request.getParameter("pgtypeid").split("-")[2];
                gateway_currency  = request.getParameter("pgtypeid").split("-")[1];
                gateway_name    = request.getParameter("pgtypeid").split("-")[0];
            }
            String fromdate= request.getParameter("fromdate");
            String todate= request.getParameter("todate");


            date= sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate= String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth= String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear= String.valueOf(rightNow.get(Calendar.YEAR));

            date=  sdf.parse(todate);
            rightNow.setTime(date);
            String tdate= String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth= String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear= String.valueOf(rightNow.get(Calendar.YEAR));

            starttime= starttime.trim();
            endtime= endtime.trim();
            if (!functions.isValueNull(starttime))
            {
                starttime   = "00:00:00";
            }
            if (!functions.isValueNull(endtime))
            {
                endtime  = "23:59:59";
            }

            String[] startTimeArr= starttime.split(":");
            String[] endTimeArr= endtime.split(":");
            logger.error("From date dd::" + fdate + " MM;;" + fmonth + " YY::" + fyear + " To date dd::" + tdate + " MM::" + tmonth + " YY::" + tyear);

            String fdstamp= Functions.converttomillisec(fmonth,fdate,fyear,startTimeArr[0],startTimeArr[1],startTimeArr[2]);
            String tdstamp= Functions.converttomillisec(tmonth,tdate,tyear,endTimeArr[0],endTimeArr[1],endTimeArr[2]);
            Set<String> gatewayTypeSet = new HashSet();

            Hashtable hash              = null;
            StringBuilder sb            = new StringBuilder();
            if(functions.isValueNull(pid)&& partner.isPartnerSuperpartnerMapped(pid,partnerid)){
                hash   = partner.getPartnerNameFromPartnerId(pid, request.getParameter("memberid"));
            }
            else if (!functions.isValueNull(pid))
            {
                hash   = partner.getPartnerNameFromPartnerIdAndSuperPartnerId(partnerid, request.getParameter("memberid"));
            }
            else {
                error = "Invalid partner mapping.";
                request.setAttribute("error", error);
                rd = request.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
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

            try
            {
                pageno= Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
                records= Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
            }
            catch (ValidationException e)
            {
                logger.error("ValidationException:::: ",e);
                pageno  = 1;
                records = 30;
            }

            if (cardpresent.equals("N"))
            {
                int totalRecords = partner.getTransactionsCountNew(desc, tdstamp, fdstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, gateway_currency, merchantid, datetype, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionmode);
                hash1 = partner.listTransactionsNew(desc, tdstamp, fdstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, gateway_currency, merchantid, datetype, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionmode, totalRecords);
                trackinghash = partner.getTrackingIdList(desc, tdstamp, fdstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, gateway_currency, merchantid, datetype, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionmode);
                amountcount = partner.getTotalAmount(desc, tdstamp, fdstamp, trackingIds, status, archive, gatewayTypeSet, accountid, gateway_name, gateway_currency, merchantid, datetype, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionmode);
            }
            logger.error("transaction sets successfully");
            request.setAttribute("transactionsdetails", hash1);
            request.setAttribute("TrackingIDList1", trackinghash);
            request.setAttribute("amountcount",amountcount);

            List<String> listofMerchantId= new ArrayList<>();
             listofMerchantId= partner.getListOfMerchantId(desc, tdstamp, fdstamp, trackingIds, status, records, pageno, archive, gatewayTypeSet, accountid, gateway_name, gateway_currency, merchantid, datetype, firstName, lastName, emailAddress, paymentId, customerId, statusflag, issuingbank, cardtype, terminalid, partnerName, transactionmode);

            String[] merchants = listofMerchantId.stream().toArray(String[]::new);
            Map<String,Object> presentTemplateDetails= new HashMap<>();
            if (merchants!= null)
            {
                for (String saveMerchant: merchants)
                {
                    presentTemplateDetails = merchantConfigManager.getSavedMemberTemplateDetails(saveMerchant);
                }
                if (!functions.isValueNull(Date) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Transaction_Date1.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Transaction_Date1.name(), Date);
                }
                else if (functions.isValueNull(Date) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Transaction_Date1.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Transaction_Date1.name(), Date);
                }
                if (!functions.isValueNull(Time_Zone) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TimeZone.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_TimeZone.name(), Time_Zone);
                }
                else if (functions.isValueNull(Time_Zone) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TimeZone.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_TimeZone.name(),Time_Zone);
                }
                if (!functions.isValueNull(TrackingID) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TrackingID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_TrackingID.name(), TrackingID);
                }
                else if (functions.isValueNull(TrackingID) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TrackingID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_TrackingID.name(), TrackingID);
                }
                if (!functions.isValueNull(PaymentId) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_PaymentID.name(),PaymentId);
                }
                else if (functions.isValueNull(PaymentId) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_PaymentID.name(),PaymentId);
                }
                if (!functions.isValueNull(PartnerId) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PartnerID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_PartnerID.name(),PartnerId);
                }
                else if (functions.isValueNull(PartnerId) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PartnerID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_PartnerID.name(),PartnerId);
                }
                if (!functions.isValueNull(MerchantId) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_MerchantID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_MerchantID.name(),MerchantId);
                }
                else if (functions.isValueNull(MerchantId) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_MerchantID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_MerchantID.name(),MerchantId);
                }
                if (!functions.isValueNull(OrderId) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_OrderID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_OrderID.name(),OrderId);
                }
                else if (functions.isValueNull(OrderId) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_OrderID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_OrderID.name(),OrderId);
                }
                if (!functions.isValueNull(OrderDescription) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_OrderDescription.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_OrderDescription.name(),OrderDescription);
                }
                else if (functions.isValueNull(OrderDescription) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_OrderDescription.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_OrderDescription.name(),OrderDescription);
                }
                if (!functions.isValueNull(CardHoldername) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Card_Holder_Name.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Card_Holder_Name.name(),CardHoldername);
                }
                else if (functions.isValueNull(CardHoldername) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Card_Holder_Name.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Card_Holder_Name.name(),CardHoldername);
                }
                if (!functions.isValueNull(CustomerEmail) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Customer_Email.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Customer_Email.name(),CustomerEmail);
                }
                else if (functions.isValueNull(CustomerEmail) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Customer_Email.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Customer_Email.name(),CustomerEmail);
                }
                if (!functions.isValueNull(CustomerID) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_CustomerID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_CustomerID.name(),CustomerID);
                }
                else if (functions.isValueNull(CustomerID) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_CustomerID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_CustomerID.name(),CustomerID);
                }
                if (!functions.isValueNull(PayMode) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentMode.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_PaymentMode.name(),PayMode);
                }
                else if (functions.isValueNull(PayMode) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentMode.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_PaymentMode.name(),PayMode);
                }
                if (!functions.isValueNull(CardType) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentBrand.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_PaymentBrand.name(),CardType);
                }
                else if (functions.isValueNull(CardType) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_PaymentBrand.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_PaymentBrand.name(),CardType);
                }
                if (!functions.isValueNull(transactionMode) && presentTemplateDetails.containsKey(TemplatePreference.transactions_mode.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.transactions_mode.name(),transactionMode);
                }
                else if(functions.isValueNull(transactionMode) && !presentTemplateDetails.containsKey(TemplatePreference.transactions_mode.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.transactions_mode.name(),transactionMode);
                }
                if (!functions.isValueNull(Amount) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Amount.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Amount.name(),Amount);
                }
                else if (functions.isValueNull(Amount) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Amount.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Amount.name(),Amount);
                }
                if (!functions.isValueNull(RefundedAmt) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_RefundAmount.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_RefundAmount.name(),RefundedAmt);
                }
                else if (functions.isValueNull(RefundedAmt) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_RefundAmount.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_RefundAmount.name(),RefundedAmt);
                }
                if(!functions.isValueNull(Status) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Status1.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Status1.name(),Status);
                }
                else if(functions.isValueNull(Status) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Status1.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Status1.name(),Status);
                }
                if(!functions.isValueNull(Currency) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Currency.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Currency.name(),Currency);
                }
                else if(functions.isValueNull(Currency) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Currency.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Currency.name(),Currency);
                }
                if(!functions.isValueNull(Remark) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Remark.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_Remark.name(),Remark);
                }
                else if(functions.isValueNull(Remark) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_Remark.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_Remark.name(),Remark);
                }
                if(!functions.isValueNull(Terminal) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TerminalID.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_TerminalID.name(),Terminal);
                }
                else if(functions.isValueNull(Terminal) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_TerminalID.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_TerminalID.name(),Terminal);
                }
                if(!functions.isValueNull(LastUpdateDate) && presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_LastUpdateDate.name()))
                {
                    merchantTemplateInformationDelete.put(TemplatePreference.partnerTransactions_LastUpdateDate.name(),LastUpdateDate);
                }
                else if(functions.isValueNull(LastUpdateDate) && !presentTemplateDetails.containsKey(TemplatePreference.partnerTransactions_LastUpdateDate.name()))
                {
                    merchantTemplateInformationInsert.put(TemplatePreference.partnerTransactions_LastUpdateDate.name(),LastUpdateDate);
                }

                for (String Merchants: merchants)
                {
                    isMerchantDeleted = merchantConfigManager.deleteMemberTemplateDetails(merchantTemplateInformationDelete, Merchants);
                    isMerchantSaved = merchantConfigManager.insertMemberTemplateDetails(merchantTemplateInformationInsert, Merchants);

                }
                logger.error("isDeleted:::::::: "+isMerchantDeleted+" "+"isSaved:::::::::::: "+isMerchantSaved);

                Map<String,Object>merchantTemplateSetting= new HashMap<>();
                for (String saveMerchant: merchants)
                {
                    merchantTemplateSetting = merchantConfigManager.getSavedMemberTemplateDetails(saveMerchant);
                }
                request.setAttribute("merchantTemplateSetting",merchantTemplateSetting);
                RequestDispatcher view  = request.getRequestDispatcher("/partnerTransactions.jsp?ctoken="+user.getCSRFToken());
                view.forward(request,response);
            }
        }
        catch (ParseException e)
        {
            logger.error("ParseException::::: ", e);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError:::: ", systemError);
        }
        catch (Exception e)
        {
            logger.error("Exception::::: ", e);
        }

    }
}

