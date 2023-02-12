package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.PartnerManager;
import com.manager.TerminalManager;
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
import java.util.*;

public class MemberMappingDetails extends HttpServlet
{
    private static Logger logger = new Logger(MemberMappingDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user           = (User)session.getAttribute("ESAPIUserSessionKey");
        String url          = "/membermappingpreference.jsp?ctoken=\""+user.getCSRFToken()+"\"";


        TerminalManager terminalManager = new TerminalManager();
        PartnerManager partnerManager = new PartnerManager();
        String partnerid = (String) session.getAttribute("merchantid");
        String pid = req.getParameter("partnerid");
        PartnerFunctions partner = new PartnerFunctions();

        List<String>members= partner.getMembersUnderSuperpartner(String.valueOf(session.getAttribute("merchantid")));

        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }


        res.setContentType("text/html");
        String errormsg = "";
        String msg = "";
        Functions functions = new Functions();
        Hashtable memberhash = null;
        /*try
        {
           // validateOptionalParameter(req);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

            inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
            inputFieldsListMandatory.add(InputFields.MEMBERID);
            inputValidator.InputValidations(req,inputFieldsListMandatory,true);
        }
        catch(ValidationException e)
        {
            logger.error("ENTER valid data in memberid::::",e);
            errormsg = errormsg + "Please Enter valid value in Member Id.";
        }*/
        String memberid = req.getParameter("memberid");
        String memberlist_new = partner.getPartnerMemberRS(pid);
        try
        {
            if (functions.isValueNull(memberid) && ESAPI.validator().isValidInput("memberid", req.getParameter("memberid"), "OnlyNumber", 10, true))
            {
                if (functions.isValueNull(pid) && !partner.isPartnerMemberMapped(memberid, pid))
                {
                    logger.error("PartnerID and MerchantID mismatch.");
                    msg = "PartnerID and MerchantID mismatch.";
                }
                else if (functions.isValueNull(pid) && functions.isValueNull(memberid))
                {
                    if (!memberlist_new.contains(memberid))
                    {
                        msg = "PartnerID and MerchantID mismatch.";
                    }
                }
                else if(!members.contains(memberid))
                {
                    msg = "PartnerID and MerchantID mismatch.";
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Exception::: ", e);
        }

        String paymodeid = "";
        if (req.getParameter("paymodeid") != null)
        {
            paymodeid = req.getParameter("paymodeid");
        }
        String cardtypeid = "";
        if (req.getParameter("cardtypeid") != null)
        {
            cardtypeid = req.getParameter("cardtypeid");
        }
        String ActiveOrInActive = "";
        if (req.getParameter("isactive") != null)
        {
            ActiveOrInActive = req.getParameter("isactive");
        }
        if ("All".equalsIgnoreCase(ActiveOrInActive))
            ActiveOrInActive = "";

        String ispayoutactive = "";
        if (req.getParameter("ispayoutactive") != null)
        {
            ispayoutactive = req.getParameter("ispayoutactive");
        }
        if ("All".equalsIgnoreCase(ispayoutactive))
            ispayoutactive = "";

        if (!ESAPI.validator().isValidInput("paymodeid", req.getParameter("paymodeid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " Invalid PaymodeId "+"<br>";

        if (!ESAPI.validator().isValidInput("cardtypeid", req.getParameter("cardtypeid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " Invalid cardtypeId "+"<br>";
        if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " Invalid Merchant Id."+"<br>";

        try
        {
            //validateOptionalParameter(req);
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

            inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
          //  inputFieldsListMandatory.add(InputFields.MEMBERID);
            inputValidator.InputValidations(req, inputFieldsListMandatory, true);
        }
        catch (ValidationException e)
        {
            logger.error("ENTER valid data in accountid::::", e);
            errormsg = errormsg + "Please Enter valid value in Account Id.";
        }
        String accountid = req.getParameter("accountid");


        if (functions.isValueNull(errormsg))
        {
            logger.debug("ENTER VALID DATA");
            req.setAttribute("error", errormsg);
        }
        if (!functions.isValueNull(memberid) && !functions.isValueNull(accountid))
        {
            logger.error("kindly search Member Id and Account Id::::");
            errormsg = errormsg + "kindly search Member Id and Account Id";

            req.setAttribute("accountid", accountid);
            req.setAttribute("accountids", partnerManager.loadGatewayAccount(partnerid));
            req.setAttribute("paymodeids", terminalManager.getPaymodeType());
            req.setAttribute("cardtypeids", terminalManager.getCardTypeID());
            req.setAttribute("paymodeid", paymodeid);
            req.setAttribute("cardtypeid", cardtypeid);
            req.setAttribute("error", errormsg);

            RequestDispatcher rd = req.getRequestDispatcher(url);
            rd.forward(req, res);
            return;
        }

        //query
            else if (( members.contains(memberid))&& functions.isValueNull(memberid) || functions.isValueNull(accountid) || functions.isValueNull(paymodeid) || functions.isValueNull(cardtypeid))
            {
                if (functions.isValueNull(memberid) && !functions.isValueNull(accountid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
                {
                    url += "&memberid=" + memberid;
                }
                if (functions.isValueNull(accountid) && !functions.isValueNull(memberid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
                {
                    url += "&accountid=" + accountid;
                }
                if (functions.isValueNull(memberid) && !functions.isValueNull(accountid) && (functions.isValueNull(paymodeid) || functions.isValueNull(cardtypeid)))
                {
                    url += "&memberid=" + memberid;
                    url += "&paymodeid=" + paymodeid;
                    url += "&cardtypeid=" + cardtypeid;
                }
                if (functions.isValueNull(accountid) && !functions.isValueNull(memberid) && (functions.isValueNull(paymodeid) || functions.isValueNull(cardtypeid)))
                {
                    url += "&accountid=" + accountid;
                    url += "&paymodeid=" + paymodeid;
                    url += "&cardtypeid=" + cardtypeid;
                }
                if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
                {
                    url += "&memberid=" + memberid;
                    url += "&accountid=" + accountid;
                }
                if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(cardtypeid) && !functions.isValueNull(paymodeid))
                {
                    url += "&memberid=" + memberid;
                    url += "&accountid=" + accountid;
                    url += "&cardtypeid=" + cardtypeid;
                }
                if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(paymodeid) && !functions.isValueNull(cardtypeid))
                {
                    url += "&memberid=" + memberid;
                    url += "&accountid=" + accountid;
                    url += "&paymodeid=" + paymodeid;
                }
                if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(paymodeid) && functions.isValueNull(cardtypeid))
                {
                    url += "&memberid=" + memberid;
                    url += "&accountid=" + accountid;
                    url += "&paymodeid=" + paymodeid;
                    url += "&cardtypeid=" + cardtypeid;
                }
                if (functions.isValueNull(ActiveOrInActive))
                {
                    url += "&isActive=" + ActiveOrInActive;
                }
                if (functions.isValueNull(ispayoutactive))
                {
                    url += "&payoutActivation=" + ispayoutactive;
                }
                memberhash = partnerManager.getMembermapping(accountid, memberid, partnerid, paymodeid, cardtypeid, ActiveOrInActive, ispayoutactive);
            }

            req.setAttribute("memberdetails", memberhash);
            req.setAttribute("accountid", accountid);

            if (functions.isValueNull(memberid))
            {
                req.setAttribute("accountids", partnerManager.loadMerchantsGatewayAccounts(partnerid, memberid));
            }
            else
            {
                req.setAttribute("accountids", partnerManager.loadGatewayAccount(partnerid));
            }

            req.setAttribute("paymodeids", terminalManager.getPaymodeType());
            req.setAttribute("cardtypeids", terminalManager.getCardTypeID());
            req.setAttribute("memberid", memberid);
            req.setAttribute("paymodeid", paymodeid);
            req.setAttribute("cardtypeid", cardtypeid);
            req.setAttribute("msg", msg);

            RequestDispatcher rd = req.getRequestDispatcher(url);
            rd.forward(req, res);
        }


    /*private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }*/

}