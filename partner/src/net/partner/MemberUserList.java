package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.payment.MultipleMemberUtill;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.commons.lang.StringUtils;
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
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Admin on 1/9/2016.
 */
public class MemberUserList extends HttpServlet
{
    static Logger log = new Logger(MemberUserList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("in side MemberUserList---");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String MemberUserList_MerchantID_errormsg = StringUtils.isNotEmpty(rb1.getString("MemberUserList_MerchantID_errormsg")) ? rb1.getString("MemberUserList_MerchantID_errormsg") : "Invalid Merchant ID.";
        String MemberUserList_PartnerID_errormsg = StringUtils.isNotEmpty(rb1.getString("MemberUserList_PartnerID_errormsg")) ? rb1.getString("MemberUserList_PartnerID_errormsg") : "Invalid Partner ID.";
        String MemberUserList_Partner_Member_errormsg = StringUtils.isNotEmpty(rb1.getString("MemberUserList_Partner_Member_errormsg")) ? rb1.getString("MemberUserList_Partner_Member_errormsg") : "Invalid Partner Member Mapping";
        String MemberUserList_Partner_Mapping_errormsg = StringUtils.isNotEmpty(rb1.getString("MemberUserList_Partner_Mapping_errormsg")) ? rb1.getString("MemberUserList_Partner_Mapping_errormsg") : "Invalid Partner Mapping";

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        res.setContentType("text/html");

        String memberid = req.getParameter("merchantid");
        String partnerid = req.getParameter("partnerid");
        String pid = req.getParameter("pid");
        String partner_id ="";

        String errormsg = "";
        Hashtable detailHash = new Hashtable();
        MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
        Functions functions = new Functions();
        String EOL = "</BR>";

        if (!ESAPI.validator().isValidInput("memberid", memberid, "Numbers", 10, false))
        {
            errormsg =  MemberUserList_MerchantID_errormsg+ EOL;
        }
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            errormsg = errormsg+  MemberUserList_PartnerID_errormsg+ EOL;
        }
        errormsg=errormsg+validateParameters(req);
        if(errormsg!=null && !errormsg.equals(""))
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

            try
            {
                if (functions.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid, partnerid))
                {
                    if (functions.isValueNull(memberid) && !partner.isPartnerMemberMapped(memberid, pid))
                    {
                        req.setAttribute("error",MemberUserList_Partner_Member_errormsg );
                        RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                }
                else if (!functions.isValueNull(pid))
                {
                    if (functions.isValueNull(memberid) && !partner.isPartnerSuperpartnerMembersMapped(memberid, partnerid))
                    {
                        req.setAttribute("error", MemberUserList_Partner_Member_errormsg);
                        RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken=" + user.getCSRFToken());
                        rd.forward(req, res);
                        return;
                    }
                }
                else
                {
                    req.setAttribute("error",MemberUserList_Partner_Mapping_errormsg );
                    RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken=" + user.getCSRFToken());
                    rd.forward(req, res);
                    return;
                }
            }
            catch (Exception e)
            {
                log.error("Exception---" + e);
            }


            try
            {
                detailHash = multipleMemberUtill.getDetailsForSubmerchant(memberid);

            }
            catch (Exception e)
            {
                log.error("Exception in isMemberUser method: ", e);
            }

        req.setAttribute("detailHash",detailHash);
        req.setAttribute("memberid",memberid);
        RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        //String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        //inputFieldsListOptional.add(InputFields.MERCHANTID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage());
                }
            }
        }
        return error;
    }

}
