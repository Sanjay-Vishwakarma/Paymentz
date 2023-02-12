package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.fraud.FraudTransaction;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by SurajT. on 5/7/2018.
 */
public class FraudKycList extends HttpServlet
{
    static Logger logger = new Logger(FraudKycList.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        doPost(req, res);

    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("listing Fraud Kyc in doPost method");
        Functions functions = new Functions();
        FraudTransaction fraudTransaction=new FraudTransaction();
        StringBuilder sbError = new StringBuilder();
        String errormsg="";
        String EOL = "<BR>";
        int start = 0; // start index
        int end = 0; // end index
        boolean flag=false;

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        if(functions.isValueNull(req.getParameter("fsCustRegId")) && functions.isValueNull(req.getParameter("fsid")))
        {
            try
            {
                Hashtable hash =fraudTransaction.getFraudKycDetails(req.getParameter("fsCustRegId"), req.getParameter("fsid"));
                req.setAttribute("kycdetails", hash);
                logger.debug("Forwarding to fraudKycDetails.jsp");
                RequestDispatcher rd = req.getRequestDispatcher("/fraudKycDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            catch (SystemError se)
            {
                logger.error("SysyemError in doGet method",se);
            }
            catch (Exception e)
            {
                logger.error("Exception in doGet method", e);
            }
            return;
        }

        String fsid = req.getParameter("fsid");
        String fskycstatus = req.getParameter("fskycstatus");
        String custRegId = req.getParameter("custRegId");
        String toid = "";
        String partnerId = (String) session.getAttribute("merchantid");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar rightNow = Calendar.getInstance();
        String fromdate = req.getParameter("fromdate");
        String todate = req.getParameter("todate");
        Date date= null;


        if (!ESAPI.validator().isValidInput("chargeId", custRegId, "Numbers", 50, true))
        {
            sbError.append("Invalid Customer Registration Id " + EOL);
        }

        if (!ESAPI.validator().isValidInput("startDate", fromdate, "fromDate", 20, true))
        {
            sbError.append("Invalid From Date" + EOL);
        }
        if (!ESAPI.validator().isValidInput("endDate", todate, "fromDate", 20, true))
        {
            sbError.append("Invalid To Date" + EOL);
        }
        if (functions.isValueNull(req.getParameter("SPageno"))){
            if (!ESAPI.validator().isValidInput("pageno", req.getParameter("SPageno"), "Numbers", 5, true)){
                sbError.append("Invalid Page No" + EOL);
            }
        }

        if (sbError.length() > 0)
        {
            req.setAttribute("errormessage", sbError.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/fraudKycList.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        try
        {

            if(functions.isFutureDateComparisonWithFromAndToDate(fromdate, todate, "dd/MM/yyyy"))
            {
                req.setAttribute("errormessage","Invalid From & To date");
                RequestDispatcher rd = req.getRequestDispatcher("/fraudKycList.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

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

            String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
            res.setContentType("text/html");
            req.setAttribute("fdtstamp", fdtstamp);
            req.setAttribute("tdtstamp", tdtstamp);


            int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            Hashtable hash =fraudTransaction.listPartnerFraudKycTransactions(partnerId, custRegId, tdtstamp, fdtstamp, fskycstatus, records, pageno);
            req.setAttribute("kycdetails",hash);
            logger.debug("KYC Transaction details is successfully load the data and forwaring to fraudKycList.jsp");

        }
        catch (SystemError se)
        {

            logger.error("errror in doPost ", se);
            Functions.ShowMessage("Error", "Internal System Error while getting list of KYC Transactions");
        }
        catch (Exception e)
        {
            logger.error("Exception:::: errror in doPost", e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of KYC Transactions");
        }
        RequestDispatcher rd1 = req.getRequestDispatcher("/fraudKycList.jsp?ctoken="+user.getCSRFToken());
        rd1.forward(req, res);

    }

}
