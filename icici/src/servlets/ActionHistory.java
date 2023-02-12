
import com.directi.pg.ActionEntry;
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayTypeService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jun 8, 2012
 * Time: 9:28:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionHistory extends HttpServlet
{
      Connection cn = null;
      private static Logger logger = new Logger(ActionHistory.class.getName());

        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            doPost(request, response);
        }

      public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        logger.debug("Entering in ActionHistory");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String searchType ="";
        String searchId =null;
        String gatewaytype=null;
        String gateway=null;
        String errormsg = "";
        String EOL = "<BR>";
        boolean flag = true;
        try
        {
            validateMandatoryParameter(req);
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid input",e);
            errormsg = errormsg + "Please Enter valid input value"+EOL;
            flag= false;
        }
        searchType = req.getParameter("searchType");
        searchId = req.getParameter("SearchId");
        gatewaytype = req.getParameter("gatewayType");

        res.setContentType("text/html");

        ServletContext application = getServletContext();
        int start = 0; // start index
        int end = 0;

        if(req.getParameter("SearchId")==null || req.getParameter("SearchId").equals(""))
        {
           errormsg = errormsg + "Please Enter Value for search "+EOL;
            flag = false;

        }
        if(req.getParameter("searchType")==null)
        {
           errormsg = errormsg + "Please select any one serchType"+EOL;
            flag = false;
        }
        if(flag==false)
        {   logger.debug(errormsg);
            req.setAttribute("error",errormsg);
            logger.debug(errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionhistory.jsp");
            rd.forward(req, res);
            return;
        }
        else
        {
        if(searchType.equals("Tracking Id") )
        {
            logger.debug("Entering getActionHistoryByTrackingId");
            Hashtable transactionHistory = new Hashtable();
            logger.debug("Entering getActd"+req.getParameter("SearchId"));
            int pageno=1;
            int records=30;

            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 15;
            }
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            start = (pageno - 1) * records;
            end = records;
            try
            {
              ActionEntry entry = new ActionEntry();
              logger.debug("gateway type is ---"+gatewaytype+"------");
              Set<String> gatewayset = getGatewayHash(gatewaytype);
              transactionHistory = entry.getActionHistoryByTrackingIdAndGatewaySet(searchId,gatewayset);
              req.setAttribute("transactionHistory",transactionHistory);
            }
            catch(Exception e)
            {
                logger.error("ERROR",e);
            }
        }
        if(searchType.equals("Merchant ID") )
        {
            logger.debug("Entering getActionHistoryByMerchantId");
            Hashtable transactionHistory = new Hashtable();
            logger.debug("Entering getActd"+req.getParameter("SearchId"));
            int pageno=1;
            int records=30;
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 15;
            }
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            start = (pageno - 1) * records;
            end = records;

            logger.debug("Entering ActionHistory  start"+start+"  end"+end);
            try
            {
              ActionEntry entry = new ActionEntry();
              logger.debug(gatewaytype);
              Set<String> gatewayset = getGatewayHash(gatewaytype);
              transactionHistory = entry.getActionHistoryByMerchantIdandGatewaySet(searchId,start,end,gatewayset);
              req.setAttribute("transactionHistory",transactionHistory);
            }
            catch(Exception e)
            {
                logger.error("ERROR",e);
            }
        }
        if(searchType.equals("MID") )
        {
            logger.debug("Entering getActionHistoryByMID");
            Hashtable transactionHistory = new Hashtable();
            logger.debug("Entering getActd"+req.getParameter("SearchId"));
            int pageno=1;
            int records=30;

            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 15;
            }
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

            start = (pageno - 1) * records;
            end = records;
            try
            {
              ActionEntry entry = new ActionEntry();
              logger.debug(gatewaytype);
              Set<String> gatewayset = getGatewayHash(gatewaytype);
              transactionHistory = entry.getActionHistoryByMIDandGatewaySet(searchId,start,end,gatewayset);
              req.setAttribute("transactionHistory",transactionHistory);
            }
            catch(Exception e)
            {
                logger.error("ERROR",e);
            }
        }
            req.setAttribute("SearchId",searchId);
            req.setAttribute("searchType",searchType);
            RequestDispatcher rd = req.getRequestDispatcher("/actionhistory.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
    }
    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null") || gateway.equals("all"))
        {
           gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.GATEWAY_TYPE);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.SEARCH_ID);
        inputFieldsListMandatory.add(InputFields.SEARCH_TYPE);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
