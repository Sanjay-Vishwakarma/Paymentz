package net.partner;

/**
 * Created by Ajit on 23/10/2018.
 */

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.directi.pg.WhitelistingDetailsVO;
import com.manager.WhiteListManager;
import com.manager.dao.WhiteListDAO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WhiteListEmail extends HttpServlet
{
    private static Logger log = new Logger(WhiteListEmail.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        StringBuffer stringBuffer = new StringBuffer();
        String msg = "";
        Functions functions = new Functions();
        WhiteListDAO whiteListDAO = new WhiteListDAO();
        List<WhitelistingDetailsVO> listOfEmail = null;
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        String errormsg = "";
        String error = "";
        String EOL = "<br>";
        int records=15;
        int pageno=1;
        int start = 0;
        int end = 0;

        HttpSession session = Functions.getNewSession(req);
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rd = req.getRequestDispatcher("/whitelistEmail.jsp?ctoken=" + user.getCSRFToken());

        //errormsg = errormsg + validateParameters(req);
        if (functions.isValueNull(errormsg))
        {
            req.setAttribute("error", errormsg);
            rd.forward(req, res);
            return;
        }
        try
        {
            //validateOptionalParameter(req);
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
            req.setAttribute("error",errormsg);
            rd.forward(req, res);
            return;
        }

        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String memberid = req.getParameter("memberid");
        String pid = req.getParameter("pid");
        String accountID = req.getParameter("accountid");
        String emailAddress = req.getParameter("emailaddr");
        String action = req.getParameter("upload");
        String name=req.getParameter("name");
        String ipAddress=req.getParameter("ipAddress");
       // String expiryDate=req.getParameter("expiryDate");
        String encryptExpiryDate="";
        String partnerid = session.getAttribute("partnerId").toString();
        WhiteListManager whiteListManager=new WhiteListManager();
        boolean isValid = true;
        if ("upload".equalsIgnoreCase(action))
            isValid = false;
        try
        {
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                stringBuffer.append("Invalid PartnerId" + EOL);
            }
            if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 20, false))
            {
                stringBuffer.append("Invalid MemberID or MemberID should not be empty" + EOL);
            }
            else
            {

                try
                {
                    if (functions.isValueNull(pid) && !partnerFunctions.isPartnerMemberMapped(memberid, pid))
                    {
                        stringBuffer.append("Partner ID/Member ID NOT mapped." + EOL);
                    }
                    else if (!functions.isValueNull(req.getParameter("pid")) && !partnerFunctions.isPartnerSuperpartnerMembersMapped(memberid, partnerid))
                    {
                        stringBuffer.append("Partner ID/Member ID NOT mapped." + EOL);
                    }
                }catch(Exception e){
                    log.error("Exception---" + e);
                }
            }
            String whitelistlevel=whiteListManager.getWhitelistingLevel(memberid);
            if(functions.isValueNull(whitelistlevel) && "Account".equalsIgnoreCase(whitelistlevel))
            {
                if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, false))
                {
                    log.debug("Invalid AccountId");
                    stringBuffer.append("Invalid accountID or accountID should not be empty" + EOL);
                }
            }else
            {
                if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, true))
                {
                    log.debug("Invalid AccountId");
                    stringBuffer.append("Invalid accountID or accountID should not be empty" + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("emailaddr", emailAddress, "Email", 50, isValid))
            {
                log.debug("Invalid email address");
                stringBuffer.append("Invalid Email or Email should not be empty" + EOL);
            }
            if(functions.isValueNull(memberid)&& functions.isValueNull(accountID))
            {
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountID);
                if(valid==false){
                    stringBuffer.append("MemberID/AccountID NOT mapped.");
                }
            }
            if(functions.isValueNull(name))
            {
                if (!ESAPI.validator().isValidInput("name", name, "SafeString", 50, false))
                {
                    log.debug("Invalid Card Holder Name.");
                    stringBuffer.append("Invalid Card Holder Name." + EOL);
                }
            }
            if(functions.isValueNull(ipAddress))
            {
                if (!ESAPI.validator().isValidInput("ipAddress", ipAddress, "IPAddress", 50, false))
                {
                    log.debug("Invalid Card Holder Name.");
                    stringBuffer.append("Invalid IP Address." + EOL);
                }
            }
            /*if(functions.isValueNull(expiryDate))
            {
                String input = expiryDate;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
                simpleDateFormat.setLenient(false);
                Date expiry = simpleDateFormat.parse(input);
                boolean expired = expiry.before(new Date());
                if (expired == true)
                {
                    stringBuffer.append("Invalid Expiry Date." + EOL);
                }
                encryptExpiryDate= PzEncryptor.hashExpiryDate(expiryDate, memberid);
            }*/
            if (stringBuffer.length() > 0)
            {
                req.setAttribute("error", stringBuffer.toString());
                rd.forward(req, res);
                return;
            }

            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        /*catch (ParseException parse)
        {
            log.debug("ParseException::::"+parse);
        }*/
        catch (PZDBViolationException e)
        {
            log.debug("PZDBViolationException:::::" + e);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        /*catch (EncryptionException e)
        {
            log.debug("EncryptionException::::"+e);
        }*/

        try
        {
                listOfEmail = whiteListDAO.getWhiteListEmailDetailsForMerchant(emailAddress, name, ipAddress, encryptExpiryDate, accountID, memberid);
                if ("upload".equalsIgnoreCase(action))
                {
                    if (listOfEmail.size() <= 0)
                    {
                        whiteListDAO.addCard("","",emailAddress,accountID,memberid,name,ipAddress,"",actionExecutorId,actionExecutorName);
                        msg = "Records Uploaded Successfully.";
                    }
                    else
                    {
                        msg = "Records Already Uploaded.";
                    }
                }
                listOfEmail = whiteListDAO.getWhiteListEmailDetailsForPartner(emailAddress,name,ipAddress,encryptExpiryDate, accountID, memberid, records, pageno, req);
            }
        catch (PZDBViolationException e)
        {
            log.debug("PZDBViolationException:::::" + e);
        }
        catch (Exception e)
        {
            log.debug("Exception::::::" + e);
        }
        req.setAttribute("error", error);
        req.setAttribute("error", stringBuffer.toString());
        req.setAttribute("msg", msg);
        req.setAttribute("listOfEmail", listOfEmail);
        rd.forward(req, res);
        return;
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MEMBERID);


        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}

