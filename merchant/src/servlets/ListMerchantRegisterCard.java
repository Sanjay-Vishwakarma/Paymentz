import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.TokenManager;
import com.manager.vo.DateDetailsVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class ListMerchantRegisterCard extends HttpServlet
{
    private static Logger log = new Logger(ListMerchantRegisterCard.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session     = req.getSession();
        log.debug("Enter in ListMerchantRegisterCard");
        Merchants merchants     = new Merchants();
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user           =  (User)session.getAttribute("ESAPIUserSessionKey");
        String merchantid   = (String) session.getAttribute("merchantid");

        session.setAttribute("submit","Registration History");
        ServletContext application = getServletContext();

        /*String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;*/
        String firstName    = null;
        String lastName     = null;
        String email        = null;
        String description  = null;
        StringBuilder  sb   = new StringBuilder();

       // int start = 0; // start index
       // int end = 0; // end index

        int pageno  = 1;
        int records = 30;

        String EOL      = "<BR>";
        String errorMsg = "";

        //String str = null;

        description     = req.getParameter("description");
        firstName       = req.getParameter("firstname");
        lastName        = req.getParameter("lastname");
        email           = req.getParameter("email");

        /*fdate = req.getParameter("fdate");
        tdate = req.getParameter("tdate");
        fmonth = req.getParameter("fmonth");
        tmonth = req.getParameter("tmonth");
        fyear = req.getParameter("fyear");
        tyear = req.getParameter("tyear");*/

        //Calendar rightNow = Calendar.getInstance();

        /*if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

        /*String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");*/


        if(!ESAPI.validator().isValidInput("description", description, "Description",100, true))
        {
            sb.append("Invalid Description"+EOL);
        }
        if(!ESAPI.validator().isValidInput("firstname", firstName, "contactName", 50, true))
        {
            sb.append("Invalid First Name"+EOL);
        }
        if(!ESAPI.validator().isValidInput("lastname", lastName, "contactName", 50, true))
        {
            sb.append("Invalid Last Name"+EOL);
        }
        if(!ESAPI.validator().isValidInput("cardholderemail", email, "Email", 50, true))
        {
            sb.append("Invalid Email"+EOL);
        }

        try
        {
            //validateOptionalParameter(req);
            InputValidator inputValidator               = new InputValidator();
            List<InputFields> inputFieldsListMandatory  = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);

            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errorMsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errorMsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..."+e.getMessage());
            req.setAttribute("error",errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher("/listmerchantregistercard.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        if(sb.length()>0)
        {
            req.setAttribute("error",sb.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/listmerchantregistercard.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req,res);
            return;
        }

        res.setContentType("text/html");


        try
        {
            pageno      = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",req.getParameter("SPageno"),"Numbers",3,true), 1);
            records     = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",req.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 30;
        }
        try
        {
            TokenManager tokenManager       = new TokenManager();
            SimpleDateFormat actualFormat   = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat actualFormat1  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

            String fDate    = req.getParameter("fdate");
            String tDate    = req.getParameter("tdate");
            fDate           = actualFormat1.format(actualFormat.parse(fDate))+ " 00:00:00";
            tDate           = actualFormat1.format(actualFormat.parse(tDate))+ " 23:59:59";

            Date date   = new Date();

            List<TokenDetailsVO> tokenDetailsVOList = tokenManager.getMerchantActiveTokens(merchantid);
            for(TokenDetailsVO tokenDetailsVO:tokenDetailsVOList)
            {
                DateDetailsVO dateDetailsVO = tokenManager.getDateDifference(tokenDetailsVO.getCreationOn(), targetFormat.format(date));
                if(tokenManager.isTokenExpired(dateDetailsVO,tokenDetailsVO.getTokenValidDays()))
                {
                    String status   = tokenManager.doTokenInactive(tokenDetailsVO.getTokenId());
                    log.debug(status);
                }
            }

            Hashtable hash  = tokenManager.getMerchantRegistrationCardList(merchantid,fDate,tDate,description,firstName,lastName,email,records, pageno,(String) session.getAttribute("role"), user.getAccountName());
            req.setAttribute("listmerchantregistercard",hash);
            req.setAttribute("fdate", fDate);
            req.setAttribute("tdate", tDate);
            //rd.forward(req, res);
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZConstraintViolationException in PZDBViolationException---", dbe);
            Functions.ShowMessage("Error", "Internal System Error");
            //req.setAttribute("error","Internal error while processing your request");
            //rd.forward(req, res);
        }
        catch (Exception dbe)
        {
            log.error("Exception in ListMerchantRegisterCard---", dbe);
            //req.setAttribute("error","Internal error while processing your request");
            Functions.ShowMessage("Error", "Internal System Error");
            //rd.forward(req, res);
        }
        RequestDispatcher rd1 = req.getRequestDispatcher("/listmerchantregistercard.jsp?ctoken="+user.getCSRFToken());
        rd1.forward(req, res);
    }

   /* private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/
}
