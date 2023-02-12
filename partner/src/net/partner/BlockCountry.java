package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.BlacklistVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

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
 * Created by Suneeta on 22/10/2018.
 */
public class BlockCountry extends HttpServlet
{
    private static Logger log = new Logger(BlockCountry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        PartnerFunctions partnerFunctions = new PartnerFunctions();
        Functions functions=new Functions();
        BlacklistManager blacklistManager = new BlacklistManager();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        List<BlacklistVO> listOfCountry = null;
        StringBuilder sErrorMessage=new StringBuilder();


        String error = "";
        String errormsg = "";
        String sMessage = "";
        String EOL = "<BR>";
        int records=15;
        int pageno=1;
        int start = 0;
        int end = 0;

        HttpSession session = Functions.getNewSession(req);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/blacklist.jsp?ctoken="+user.getCSRFToken());
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        //errormsg = errormsg + validateMandatoryParameters(req);
        if (functions.isValueNull(errormsg))
        {
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }


        try
        {
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.PAGENO);
            inputFieldsListMandatory.add(InputFields.RECORDS);
            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
            if(!error.isEmpty())
            {
                req.setAttribute("error",error);
                rd.forward(req,res);
                return;
            }
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberid = req.getParameter("memberid");
        String pid = req.getParameter("pid");
        String accountID=req.getParameter("accountid");
        String partnerid = session.getAttribute("partnerId").toString();

        String action = req.getParameter("upload");
        String countryCodeTelcc = "";
        String country = "";
        String country_code = "";
        String three_digit_country_code="";
        String telCc = "";
        int count = 0;
        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);


        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        start = (pageno - 1) * records;
        end = records;

        boolean isValid = true;
        if ("upload".equalsIgnoreCase(action))
            isValid = false;


        if(functions.isValueNull(error)){
            req.setAttribute("error",error);
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



        try
        {
            if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 50, false))
            {
                sErrorMessage.append("Invalid MemberID or MemberID should not be empty" + EOL);
            }
            else
            {

                if (functions.isValueNull(pid) && !partnerFunctions.isPartnerMemberMapped(memberid, pid))
                {
                    sErrorMessage.append("Partner ID/Member ID NOT mapped." + EOL);
                }
                else if (!functions.isValueNull(req.getParameter("pid")) && !partnerFunctions.isPartnerSuperpartnerMembersMapped(memberid, partnerid))
                {
                    sErrorMessage.append("Partner ID/Member ID NOT mapped." + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, isValid))
            {
                sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("country", country_code, "StrictString", 50, isValid))
            {
                sErrorMessage.append("Invalid Country or Country should not be empty" + EOL);
            }

            if(functions.isValueNull(memberid)&& functions.isValueNull(accountID)){
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountID);
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped." + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                sErrorMessage.append("Invalid PartnerId" + EOL);
            }

            if(sErrorMessage.length()>0){
                req.setAttribute("error",sErrorMessage.toString());
                rd = req.getRequestDispatcher("/blacklist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            if(country.equals("Cote d'Ivoire")){
                country ="Cote d''Ivoire";
            }

                if ("upload".equals(action))
                {
                    count = blacklistManager.insertBlockedCountry(country,country_code,telCc,three_digit_country_code,accountID,memberid, actionExecutorId,actionExecutorName);
                    if(count!=1)
                    {
                        errormsg =memberid+"-"+ country + " already exists";
                    }
                    else
                    {
                        sMessage =memberid+"-"+ country+" blocked Successfully";
                    }

                }
                listOfCountry = blacklistManager.getBlockedCountryPartner(country, accountID, memberid,records,pageno,req);

        }
        catch (PZDBViolationException e)
        {
            log.debug("Exception---"+e);
        }
        catch (Exception e)
        {
            log.debug("Exception:::::"+e);
        }
        req.setAttribute("msg",sMessage);
        req.setAttribute("error",errormsg);
        req.setAttribute("merchantid",memberid);
        req.setAttribute("listOfCountry",listOfCountry);
        rd.forward(req, res);
        return;
    }
    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

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
