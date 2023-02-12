package servlets;

import com.directi.pg.*;
import com.manager.WhiteListManager;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
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
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Vivek on 3/9/2020.
 */
public class WhitelistBinCountry extends HttpServlet
{
    private String pageClassname="WhitelistBinCountry";
    private static Logger log = new Logger(WhitelistBinCountry.class.getName());
    private Functions functions = new Functions();
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        Functions functions = new Functions();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PaginationVO paginationVO = new PaginationVO();
        Connection conn=null;

        List<String>  dbCountryList=new ArrayList<>();
        List<String>  countryList=new ArrayList<>();
        List<String>  reqCountryList=new ArrayList<>();
        List<WhitelistingDetailsVO>  listofBinCountry=new ArrayList<>();

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberId = "";
        String accountId ="";
        String error = "";
        String sMessage = "";
        String EOL = "<br>";

        String country="";
        country=req.getParameter("country");

        accountId = req.getParameter("accountid");
        memberId = req.getParameter("toid");
        String action=req.getParameter("delete");

        boolean isValid = true;
        if ("whitelist".equalsIgnoreCase(req.getParameter("bbtn")))
            isValid = false;

        WhiteListManager whiteListManager = new WhiteListManager();
        try
        {
            error = error + validateOptionalParameters(req);
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("WhitelistBinCountry.java", "doPost()", null, "icici", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }
            if (!ESAPI.validator().isValidInput("toid", memberId, "Numbers", 50, isValid))
            {
                log.debug("Invalid memberid");
                sErrorMessage.append("Invalid memberID or memberID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 50, isValid))
            {
                log.debug("Invalid accountid");
                sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
            }

           if(!functions.isValueNull(country) && "whitelist".equalsIgnoreCase(req.getParameter("bbtn")))
           {
               log.debug("Please select at least one country");
               sErrorMessage.append("Please select at least one country" + EOL);
           }

            if(functions.isValueNull(memberId)&& functions.isValueNull(accountId)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberId,accountId);
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped.");
                }
            }
            if (sErrorMessage.length() > 0) {
                String redirectpage = "/whitelistBinCountry.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }
            String[] countries=null;
            String country1="";
            if(functions.isValueNull(country) && country.contains(","))
            {
                countries=country.split(",");
                int i=0;
                for (String cty : countries)
                {
                    if(i==0)
                        country1+="'"+cty+"'";
                    else
                        country1+=",'"+cty+"'";
                    i++;
                    reqCountryList.add(cty);
                }
            }else if(functions.isValueNull(country))
            {
                country1 = "'"+country+"'";
                reqCountryList.add(country);
            }
            if ("whitelist".equalsIgnoreCase(req.getParameter("bbtn"))) {
                dbCountryList=whiteListManager.getWhiteListBinCountry(country1, accountId, memberId,actionExecutorId,actionExecutorName);
                for (String cty : reqCountryList)
                {
                    if(!dbCountryList.contains(cty))
                        countryList.add(cty);
                }

                if(!countryList.isEmpty())
                {
                    boolean flag = whiteListManager.insertBinCountryRoutingDetails(memberId, accountId, countryList,actionExecutorId,actionExecutorName);
                    if (flag)
                    {
                        sMessage = "Country Uploaded Successful for Member ID:" + memberId;
                    }
                }else
                    sMessage = "Your Country Already Whitelisted";
            }
            String[] ids=req.getParameterValues("id");
            if ("delete".equalsIgnoreCase(action))
            {
                for(String id : ids)
                {
                    whiteListManager.deleteBinCountry(id);
                }
                sMessage ="Records Deleted Successfully";
            }
            listofBinCountry = whiteListManager.getWhiteListBinCountryPage(country1, accountId, memberId,actionExecutorId,actionExecutorName, paginationVO);

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setInputs("accountid=" + accountId + "&toid=" + memberId);
            paginationVO.setPage(pageClassname);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            req.setAttribute("paginationVO", paginationVO);
        }
        catch (Exception e)
        {
           log.error("Catch Exception...",e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        String redirectpage = "/whitelistBinCountry.jsp?ctoken=" + user.getCSRFToken();
        req.setAttribute("error", error);
        req.setAttribute("msg",sMessage);
        req.setAttribute("listofBinCountry",listofBinCountry);
       /* req.setAttribute("actionExecutorId",actionExecutorId);
        req.setAttribute("actionExecutorName",actionExecutorName);
       */
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
