

/**
 * Created by pranav on 04-05-2017.
 */
import com.directi.pg.*;
import com.manager.TokenManager;
import com.manager.vo.DateDetailsVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
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

public class ListMerchantRegisterCard extends HttpServlet
{
    private static Logger log = new Logger(ListMerchantRegisterCard.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        log.debug("Enter in Listmerchantregistercard");

        Functions functions= new Functions();

        if (!Admin.isLoggedIn(session))
        {
            res.sendRedirect("/icici/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        res.setContentType("text/html");

        RequestDispatcher rd = req.getRequestDispatcher("/listmerchantregistercard.jsp?ctoken="+user.getCSRFToken());

        String merchantid ="";
        String firstName=null;
        String lastName=null;
        String email=null;
        String description=null;
        String partnerid=null;

        description = req.getParameter("description");
        firstName=req.getParameter("firstname");
        lastName=req.getParameter("lastname");
        email=req.getParameter("email");

        merchantid ="";
        partnerid = "";


        if(!req.getParameter("memberid").equals("0"))
        {
            merchantid= req.getParameter("memberid");
            req.setAttribute("memberid",merchantid);
        }
        if(!req.getParameter("partnerid").equals("0"))
        {
            partnerid= req.getParameter("partnerid");
            req.setAttribute("partnerid",partnerid);
        }

        String errormsg = "";
        String EOL = "<BR>";

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..." + e.getMessage());
            req.setAttribute("message",errormsg);
            rd.forward(req, res);
        }

        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth = req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");

        int records=15;
        int pageno=1;

        Calendar rightNow = Calendar.getInstance();
        Date date=new Date();
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );

       /* if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);*/

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"),15);

        try
        {
            TokenManager tokenManager=new TokenManager();
            if(functions.isFutureDateComparisonWithFromAndToDate(fdtstamp, tdtstamp, "dd/MM/yyyy"))
            {
                req.setAttribute("error","Invalid From & To date");
                rd.forward(req, res);
                return;
            }

            PaginationVO paginationVO=new PaginationVO();
            paginationVO.setInputs("fmonth=" + fmonth + "&fdate=" + fdate + "&fyear=" + fyear + "&tmonth=" + tmonth + "&tdate=" + tdate + "&tyear=" + tyear  + "&memberid=" + merchantid + "&partnerid=" + partnerid);
            paginationVO.setPage(ListMerchantRegisterCard.class.getName());
            paginationVO.setPageNo(pageno);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            paginationVO.setRecordsPerPage(records);


            List<TokenDetailsVO> tokenDetailsVOList=tokenManager.getMerchantActiveTokens(merchantid);
            for(TokenDetailsVO tokenDetailsVO:tokenDetailsVOList)
            {
                DateDetailsVO dateDetailsVO=tokenManager.getDateDifference(tokenDetailsVO.getCreationOn(), targetFormat.format(date));
                if(tokenManager.isTokenExpired(dateDetailsVO,tokenDetailsVO.getTokenValidDays()))
                {
                    String status=tokenManager.doTokenInactive(tokenDetailsVO.getTokenId());
                    log.debug(status);
                }
            }

            Hashtable hash=tokenManager.getMerchantRegistrationCardLists(merchantid,fdtstamp,tdtstamp,description,firstName,lastName,email,records, pageno,partnerid, user.getAccountName());

            req.setAttribute("paginationVO", paginationVO);
            req.setAttribute("listmerchantregistercard", hash);

        }
        catch (Exception dbe)
        {
            log.error("Exception in listmerchantregistercard---", dbe);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        inputFieldsListMandatory.add(InputFields.FIRSTNAME);
        inputFieldsListMandatory.add(InputFields.LASTNAME);
        inputFieldsListMandatory.add(InputFields.EMAIL);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}

