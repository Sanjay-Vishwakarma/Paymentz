package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.payment.MultiplePartnerUtill;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedHashMap;

/**
 * Created by Admin on 1/9/2016.
 */
public class PartnerUserList extends HttpServlet
{
    static Logger log = new Logger(PartnerUserList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("in side PartnerUserList---");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        res.setContentType("text/html");

        Functions function =  new Functions();
        String partnerid = (String) session.getAttribute("merchantid");
        String pid = req.getParameter("pid");
        String partner_id = "";
        if (!ESAPI.validator().isValidInput("pid", req.getParameter("pid"), "Numbers", 10, true))
        {
            req.setAttribute("error","Invalid Partner ID");
            RequestDispatcher rd = req.getRequestDispatcher("/partnerChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(function.isValueNull(pid) && partner.isPartnerSuperpartnerMapped(pid,partnerid))
        {
            partner_id = pid;
        } else if(!function.isValueNull(pid))
        {

            String Roles = partner.getRoleofPartner(partnerid);
            if (Roles.contains("superpartner"))
            {
                LinkedHashMap<Integer, String> memberHash = new LinkedHashMap();
                memberHash = partner.getPartnerDetails(partnerid);
                partner_id = partnerid;
                for (int partnerID : memberHash.keySet())
                {
                    partner_id += "," + Integer.toString(partnerID);
                }
            }
            else
            {
                partner_id = partnerid;
            }
        }else{
            req.setAttribute("error","Invalid Partner Mapping");
            RequestDispatcher rd = req.getRequestDispatcher("/partnerChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        //String errormsg = "";
        Hashtable detailHash = null;
        MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();
        detailHash = multiplePartnerUtill.getDetailsForSubpartner(partner_id);


       /* errormsg=errormsg+validateParameters(req);
        if(errormsg!=null && !errormsg.equals(""))
        {
            req.setAttribute("error",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/memberChildList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }*/

        req.setAttribute("detailHash",detailHash);
        req.setAttribute("memberid",partner_id);
        RequestDispatcher rd = req.getRequestDispatcher("/partnerChildList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

   /* private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.MERCHANTID);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional, errorList,false);

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
*/
}
