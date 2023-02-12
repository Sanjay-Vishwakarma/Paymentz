package net.partner;

import com.directi.pg.*;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
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
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ajit on 22/10/2018.
 */
public class WhiteListCard extends HttpServlet
{
    private static Logger logger = new Logger(WhiteListCard.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(req);
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }

        Functions functions = new Functions();
        StringBuffer stringBuffer = new StringBuffer();
        String msg = "";
        WhiteListDAO whiteListDAO = new WhiteListDAO();
        List<WhitelistingDetailsVO> listOfCard = null;

        Connection conn = null;
        String errormsg = "";
        String error = "";
        String EOL = "<BR>";

        String memberid = req.getParameter("memberid");
        String pid = req.getParameter("pid");
        String action = req.getParameter("upload");
        String accountID = req.getParameter("accountid");
        String firstSix = req.getParameter("firstsix");
        String lastFour = req.getParameter("lastfour");
        String emailAddress = req.getParameter("emailAddr");
        String name=req.getParameter("name");
        String ipAddress=req.getParameter("ipAddress");
        //String expiryDate=req.getParameter("expiryDate");
        String encryptExpiryDate="";
        String partnerid = session.getAttribute("partnerId").toString();
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        String whitelistlevel="";
        int records=15;
        int pageno=1;
        int start = 0;
        int end = 0;

        //errormsg = errormsg + validateParameters(req);
        if (functions.isValueNull(errormsg))
        {
            req.setAttribute("error", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
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
                RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req,res);
                return;
            }
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        boolean isRecordAvailable = false;
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
                    logger.error("Exception---" + e);
                }
            }
            try
            {
                WhiteListManager whiteListManager=new WhiteListManager();
                whitelistlevel=whiteListManager.getWhitelistingLevel(memberid);
                MerchantDAO merchantDAO=new MerchantDAO();
                if(!functions.isValueNull(whitelistlevel)){
                    if(functions.isValueNull(memberid)){
                        boolean isPresent=merchantDAO.isMemberExist(memberid);
                        if(!isPresent){
                            stringBuffer.append("Merchant does not exist in the system" + EOL);
                            req.setAttribute("error", stringBuffer.toString());
                            RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken="+user.getCSRFToken());
                            rd.forward(req, res);
                            return;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Exception---" + e);
            }
            if("Account".equalsIgnoreCase(whitelistlevel))
            {
                if (!ESAPI.validator().isValidInput("accountid", accountID, "Numbers", 50, isValid))
                {
                    logger.debug("Invalid AccountId");
                    stringBuffer.append("Invalid accountID or accountID should not be empty" + EOL);
                }
            }
            if (!ESAPI.validator().isValidInput("firstsix", firstSix, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid firstsix");
                stringBuffer.append("Invalid card number firstsix or firstsix should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("lastfour", lastFour, "LastFourcc", 4, isValid))
            {
                logger.debug("Invalid lastfour");
                stringBuffer.append("Invalid card number lastfour or lastfour should not be empty" + EOL);
            }
            if (functions.isValueNull(emailAddress))
            {
                if (!ESAPI.validator().isValidInput("emailaddr", emailAddress, "Email", 50, isValid))
                {
                    logger.debug("Invalid email address");
                    stringBuffer.append("Invalid Email or Email should not be empty" + EOL);
                }
            }
            if(functions.isValueNull(name))
            {
                if (!ESAPI.validator().isValidInput("name", name, "StrictString", 50, isValid))
                {
                    logger.debug("Invalid Card Holder Name.");
                    stringBuffer.append("Invalid Card Holder Name." + EOL);
                }
            }
            if(functions.isValueNull(ipAddress))
            {
                if (!ESAPI.validator().isValidInput("ipAddress", ipAddress, "IPAddress", 50, isValid))
                {
                    logger.debug("Invalid Invalid IP Address.");
                    stringBuffer.append("Invalid IP Address." + EOL);
                }
            }
           /* if(functions.isValueNull(expiryDate))
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
              //  encryptExpiryDate= PzEncryptor.hashExpiryDate(expiryDate, memberid);
            }*/
            if(functions.isValueNull(memberid)&& functions.isValueNull(accountID))
            {
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountID);
                if(valid==false){
                    stringBuffer.append("MemberID/AccountID NOT mapped.");
                }
            }

            if (stringBuffer.length() > 0)
            {
                req.setAttribute("error", stringBuffer.toString());
                RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }

            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        catch (PZDBViolationException e)
        {
            logger.debug("PZDBViolationException::::" + e);
        }
        /*catch(ParseException parse)
        {
            logger.debug("ParseException::::" + parse);
        }
        catch(EncryptionException e)
        {
            logger.debug("EncryptionException::::" + e);
        }*/

        try
        {
            if ("upload".equalsIgnoreCase(action))
                {
                    try
                    {
                        isRecordAvailable = whiteListDAO.isRecordAvailableForMemberWithoutEmailid(memberid, accountID, firstSix, lastFour, name, ipAddress,encryptExpiryDate,whitelistlevel);
                        if (!isRecordAvailable)
                        {
                            whiteListDAO.addCard(firstSix, lastFour, emailAddress, accountID, memberid,name,ipAddress,"",actionExecutorId,actionExecutorName);
                            msg = "Records Uploaded Successfully.";
                        }
                        else if (functions.isValueNull(emailAddress))
                        {
                            whiteListDAO.updateEmildId(emailAddress, firstSix, lastFour);
                            msg = "Records Update Successfully.";

                        }
                        else
                        {
                            stringBuffer.append("Records already Updated " + EOL);
                        }
                    }
                    catch (PZDBViolationException e)
                    {
                        logger.debug("PZDBViolationException:::::"+e);
                    }
                }
                listOfCard = whiteListDAO.getWhiteListCardForPartner(firstSix, lastFour, emailAddress,name, ipAddress, encryptExpiryDate, accountID, memberid, records, pageno,actionExecutorId, actionExecutorName, req);
        }
        catch (PZDBViolationException e)
        {
            logger.debug("PZDBViolationException::::" + e);
        }
        catch (Exception e)
        {
            logger.debug("Exception::::::" + e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        req.setAttribute("error", error);
        req.setAttribute("error", stringBuffer.toString());
        req.setAttribute("msg", msg);
        req.setAttribute("listofcard", listOfCard);
        RequestDispatcher rd = req.getRequestDispatcher("/whitelist.jsp?ctoken=" + user.getCSRFToken());
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
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }
}

