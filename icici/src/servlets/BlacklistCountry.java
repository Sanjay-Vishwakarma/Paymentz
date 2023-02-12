import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 5/10/2015.
 */
public class BlacklistCountry extends HttpServlet
{
    private static Logger logger = new Logger(BlacklistCountry.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in BlockedNameList");
        HttpSession session = req.getSession();
        Functions functions=new Functions();
        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PaginationVO paginationVO = new PaginationVO();
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        BlacklistManager blacklistManager = new BlacklistManager();
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String error = "";
        String sMessage = "";
        String countryCodeTelcc = "";
        List<BlacklistVO> listOfcountrys = null;
        int count = 0;
        String country = "";
        String country_code = "";
        String three_digit_country_code="";
        String telCc = "";
        String action=req.getParameter("unblock");
        String reason=req.getParameter("reason");
        String remark=req.getParameter("remark");
        String EOL = "<br>";
        boolean isValid=true;

        try{
            error = error + validateOptionalParameters(req);
            if("block".equalsIgnoreCase(req.getParameter("bbtn")))
                isValid=false;


            if (!ESAPI.validator().isValidInput("toid", req.getParameter("toid"), "Numbers", 50, isValid))
            {
                logger.debug("Invalid memberid");
                sErrorMessage.append("Invalid memberID or memberID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid"), "Numbers", 50, isValid))
            {
                logger.debug("Invalid accountid");
                sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
            }

            if(functions.isValueNull(req.getParameter("toid"))&& functions.isValueNull(req.getParameter("accountid"))){
                boolean valid=whiteListDAO.getMemberidAccountid(req.getParameter("toid"),req.getParameter("accountid"));
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped.");
                }
            }
            String accountId="",memberId="";
            if(functions.isValueNull(req.getParameter("toid"))&& functions.isValueNull(req.getParameter("accountid")))
            {
                accountId=req.getParameter("accountid");
                memberId=req.getParameter("toid");
            }

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"), 15));
            paginationVO.setPage(BlacklistCountry.class.getName());
            paginationVO.setInputs("&accountid=" +accountId+ "&toid=" + memberId);
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"), 1));
            if(sErrorMessage.length()>0){
                String redirectpage = "/blacklistCountry.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }

            countryCodeTelcc = req.getParameter("countryList");
            if(!"Select Country".equalsIgnoreCase(countryCodeTelcc) && null!=countryCodeTelcc)
            {
                String[] sData = countryCodeTelcc.split("\\|");
                country = sData[3];
                telCc = sData[2];
                three_digit_country_code=sData[1];
                country_code = sData[0];
            }
            if(country.equals("Cote d'Ivoire")){
                country ="Cote d''Ivoire";
            }
            if("search".equalsIgnoreCase(req.getParameter("sbtn")))
            {
                listOfcountrys = blacklistManager.getBlockedCountryPages(country, req.getParameter("accountid"), req.getParameter("toid"),reason,remark, paginationVO);
            }
            if ("block".equalsIgnoreCase(req.getParameter("bbtn")) && !req.getParameter("countryList").equals(""))
                {
                    count = blacklistManager.insertBlockedCountrys(country, country_code, telCc, three_digit_country_code, req.getParameter("accountid"), req.getParameter("toid"), actionExecutorId, actionExecutorName,reason,remark);
                    if (count != 1)
                    {
                        sMessage = req.getParameter("toid") + "-" + country + " is already Blocked";
                    }
                    else
                    {
                        sMessage = req.getParameter("toid") + "-" + country + " is Blocked Successfully";
                    }
                    listOfcountrys = blacklistManager.getBlockedCountryPages(country, req.getParameter("accountid"), req.getParameter("toid"),reason,remark, paginationVO);
                }
            String[] ids=req.getParameterValues("id");
            if("unblock".equalsIgnoreCase(action) && error.equals(""))
            {
                for(String id : ids)
                {
                    blacklistManager.unblockCountry(id);
                }
                listOfcountrys = blacklistManager.getBlockedCountryPages(country, req.getParameter("accountid"), req.getParameter("toid"),reason,remark, paginationVO);
                sMessage =" Records Unblocked Successfully";
            }
            listOfcountrys = blacklistManager.getBlockedCountryPages(country, req.getParameter("accountid"), req.getParameter("toid"),reason,remark, paginationVO);
        }
        catch (PZDBViolationException dbe)
        {
            PZExceptionHandler.handleDBCVEException(dbe, "", "Blacklist country");
        }
        catch (Exception e){
            logger.error("Catch Exception...",e);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        req.setAttribute("paginationVO", paginationVO);
        req.setAttribute("error",error);
        req.setAttribute("msg",sMessage);
        req.setAttribute("listofcountrys",listOfcountrys);
        req.setAttribute("count",count);
        RequestDispatcher rd = req.getRequestDispatcher("/blacklistCountry.jsp?ctoken=" + user.getCSRFToken());
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
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
