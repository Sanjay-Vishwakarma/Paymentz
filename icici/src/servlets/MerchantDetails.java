import com.directi.pg.*;
import com.manager.dao.MerchantDAO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

public class MerchantDetails extends HttpServlet
{
    private static Logger log = new Logger(MerchantDetails.class.getName());
    Functions functions = new Functions();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        boolean flag=false;
        String errormsg="";
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        MerchantDAO merchantDAO = new MerchantDAO();
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        ServletContext application = getServletContext();
        PrintWriter out = res.getWriter();

        String ignoredates = Functions.checkStringNull(req.getParameter("ignoredates"));
        String perfectmatch = Functions.checkStringNull(req.getParameter("perfectmatch"));

        String EOL = "<BR>";
        if(perfectmatch!=null && perfectmatch.equalsIgnoreCase("Yes")  )
        {
            req.setAttribute("perfectmatch","Yes");
        }
        else
        {
            req.setAttribute("perfectmatch","No");
        }

        String fixamount =Functions.checkStringNull(req.getParameter("fixamount"));
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invelid description",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;

            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/merchantDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String singleMemberid = req.getParameter("singlememberid");
        if(functions.isValueNull(singleMemberid))
        {
            try
            {
                Hashtable hash = merchantDAO.getMerchantDetails(singleMemberid);
                req.setAttribute("merchantdetails", hash);

                RequestDispatcher rd = req.getRequestDispatcher("/merchantDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }

            catch (Exception e)
            {
                log.error("Exception in doGet::::",e);
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
                flag = false;

                req.setAttribute("errormessage",errormsg);
                RequestDispatcher rd = req.getRequestDispatcher("/merchantDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
            }
            return;
        }

        String memberid =req.getParameter("memberid");
        String company_name = req.getParameter("company_name");
        String sitename = req.getParameter("sitename");
        String partnername = req.getParameter("partnerName");
        String domain = req.getParameter("domain");
        String activation = req.getParameter("activation");
        String icici  = req.getParameter("icici");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String contact_emails = req.getParameter("contact_emails");
        String contact_persons = req.getParameter("contact_persons");
        String login = req.getParameter("login");

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        res.setContentType("text/html");
        req.setAttribute("fdate", fdate);
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("fmonth",fmonth);
        req.setAttribute("fyear",fyear);
        req.setAttribute("tdate",tdate);
        req.setAttribute("tdtstamp", tdtstamp);
        req.setAttribute("tmonth",tmonth);
        req.setAttribute("tyear",tyear);

        int pageno = 0;
        int records = 30;

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",3,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }

        try
        {

            Hashtable hash = merchantDAO.listMembers(memberid, partnername, company_name, sitename, activation, icici, fixamount, fdtstamp, tdtstamp, records, pageno, ignoredates, perfectmatch, contact_emails, contact_persons,login,domain);
            req.setAttribute("merchantsdetails", hash);

        }
        catch (Exception e)
        {
            log.error("Exception in fatch record",e);
            Functions.ShowMessage("Error", "Internal System Error");
        }
        RequestDispatcher rd = req.getRequestDispatcher("/merchants.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();

        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.ACTIVATION);
        inputFieldsListOptional.add(InputFields.COMPANY_NAME);
        inputFieldsListOptional.add(InputFields.SITENAME);
        inputFieldsListOptional.add(InputFields.ICICI);
        inputFieldsListOptional.add(InputFields.FROMDATE);
        inputFieldsListOptional.add(InputFields.TODATE);
        inputFieldsListOptional.add(InputFields.FROMMONTH);
        inputFieldsListOptional.add(InputFields.TOMONTH);
        inputFieldsListOptional.add(InputFields.FROMYEAR);
        inputFieldsListOptional.add(InputFields.TOYEAR);
        inputFieldsListOptional.add(InputFields.CONTACT_EMAIL);
        inputFieldsListOptional.add(InputFields.CONTACT_PERSON);

        inputValidator.InputValidations(req, inputFieldsListOptional, true);
    }
}