package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.WhitelistingDetailsVO;
import com.manager.dao.WhiteListDAO;
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
 * Created by Ajit on 23/10/2018.
 */
public class WhiteListBin extends HttpServlet
{
    private static Logger logger = new Logger(WhiteListBin.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        StringBuilder sErrorMessage = new StringBuilder();
        String msg = "";
        Functions functions = new Functions();
        List<WhitelistingDetailsVO> listOfBin = null;
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        WhiteListDAO whiteListDAO = new WhiteListDAO();
        HttpSession session = Functions.getNewSession(req);
        String errormsg = "";
        String error = "";
        String EOL = "<BR>";

        if (!partnerFunctions.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        RequestDispatcher rd = req.getRequestDispatcher("/whitelistBin.jsp?ctoken=" + user.getCSRFToken());

        //errormsg = errormsg + validateParameters(req);
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
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("error",errormsg);
            rd.forward(req, res);
            return;
        }
        String memberid = req.getParameter("memberid");
        String pid = req.getParameter("pid");
        String startBin = req.getParameter("startBin");
        String endBin = req.getParameter("endBin");

        String startCard = req.getParameter("startCard");
        String endCard = req.getParameter("endCard");
        String accountid = req.getParameter("accountid");
        String partnerid = session.getAttribute("partnerId").toString();
        String action = req.getParameter("upload");
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;
        int records=15;
        int pageno=1;
        int start = 0;
        int end = 0;

        try
        {
            pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SPageno",req.getParameter("SPageno"),"Numbers",5,true), 1);
            records = Functions.convertStringtoInt(ESAPI.validator().getValidInput("SRecords",req.getParameter("SRecords"),"Numbers",5,true), 15);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        boolean isValid = true;
        if ("upload".equalsIgnoreCase(action))
            isValid = false;

        try
        {
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                sErrorMessage.append("Invalid PartnerId" + EOL);
            }
            if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 20, false))
            {
                sErrorMessage.append("Invalid MemberID or MemberID should not be empty" + EOL);
            }
            else
            {

                try
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
                catch (Exception e)
                {
                    logger.error("Exception---" + e);
                }
            }
            if (!ESAPI.validator().isValidInput("accountid", accountid, "Numbers", 50, isValid))
            {
                logger.debug("Invalid AccountId");
                sErrorMessage.append("Invalid accountID or accountID should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("startBin", startBin, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append("Invalid Start Bin or Start Bin should not be empty" + EOL);
            }

            if (!ESAPI.validator().isValidInput("startCard", startCard, "OnlyNumber", 13, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append("Invalid Start Card or Start Card should not be empty" + EOL);
            }
            if (!ESAPI.validator().isValidInput("endBin", endBin, "FirstSixcc", 6, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append("Invalid End Bin or End Bin should not be empty" + EOL);
            }

            if (!ESAPI.validator().isValidInput("endCard", endCard, "OnlyNumber", 13, isValid))
            {
                logger.debug("Invalid bin");
                sErrorMessage.append("Invalid End Card or End Card should not be empty" + EOL);
            }

            if(functions.isValueNull(startBin)&& !functions.isValueNull(endBin)|| !functions.isValueNull(startCard) && functions.isValueNull(endCard)){
                sErrorMessage.append("Please provide both StartBin And EndBin , else Click on Single Bin.");
                String redirectpage = "/whitelistBin.jsp?ctoken=" + user.getCSRFToken();
                req.setAttribute("error", sErrorMessage.toString());
                rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
                return;
            }
             if(startBin.compareTo(endBin) ==0 || startBin.compareTo(endBin) ==1)
            {
                if (startCard.compareTo(endCard) == 0 || (startCard.compareTo(endCard) >= 1))
                {
                    sErrorMessage.append("Start Card greater than End Card.");
                }
            }
            if(functions.isValueNull(memberid)&& functions.isValueNull(accountid))
            {
                boolean valid=whiteListDAO.getMemberidAccountid(memberid,accountid);
                if(valid==false){
                    sErrorMessage.append("MemberID/AccountID NOT mapped.");
                }
            }
            if (sErrorMessage.length() > 0)
            {
                req.setAttribute("error", sErrorMessage.toString());
                rd.forward(req, res);
                return;
            }

               listOfBin = whiteListDAO.getWhiteListBinForPartner(startBin, endBin,startCard,endCard, accountid, memberid, records, pageno,req);
                if ("upload".equalsIgnoreCase(action))
                {
                    if (listOfBin.size() <= 0)
                    {
                        if (functions.isValueNull(startBin) && functions.isValueNull(endBin) && functions.isValueNull(startCard)&& functions.isValueNull(endCard))
                        {
                            whiteListDAO.addBin(startBin, endBin,startCard,endCard, accountid, memberid,actionExecutorId,actionExecutorName);
                            msg = "Records Uploaded Successfully";
                        }
                    }
                    else
                    {
                        msg = "Records Already Uploaded.";
                    }
                }
                listOfBin = whiteListDAO.getWhiteListBinForPartner(startBin, endBin,startCard,endCard, accountid, memberid, records, pageno,req);

        }
        catch (PZDBViolationException e)
        {
            logger.debug("PZDBViolationException:::" + e);
        }
        catch (Exception e)
        {
            logger.debug("Exception::::" + e);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("error", errormsg);
        req.setAttribute("error", errormsg);
        req.setAttribute("msg", msg);
        req.setAttribute("error", sErrorMessage.toString());
        req.setAttribute("listOfBin", listOfBin);
        rd.forward(req, res);
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