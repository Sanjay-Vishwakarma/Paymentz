import com.directi.pg.*;
import com.manager.WLPartnerInvoiceManager;
import com.payment.exceptionHandler.PZDBViolationException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Naushad on 11/26/2016.
 */
public class WLPartnerCommMappingList extends HttpServlet
{
    static Logger logger = new Logger(WLPartnerCommMappingList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doProcess(req, res);
    }
    private void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        Connection conn = null;
        int records = 15;
        int pageno = 1;

        //String partnerId = request.getParameter("partnername");
        String partnerId="";
        String pgtypeId = request.getParameter("gatewaybankname");

        partnerId = request.getParameter("partnername");

        String errormsg = "";
        String EOL = "<BR>";
        String option_delete= "";
        if(functions.isValueNull(request.getParameter("delete")))
        {
            option_delete= request.getParameter("delete");
        }
        String query="";
        String error1="";
        String success1= "";
        PreparedStatement pstmt= null;
        Connection con= null;

        if (option_delete.equalsIgnoreCase("delete"))
        {
            try
            {
                String id1= request.getParameter("ids");
                String ids[]={};
                ids= id1.split(",");
                for (String id: ids)
                {
                    con= Database.getConnection();
                    query= "Delete from wl_partner_commission_mapping where id=?";
                    pstmt= con.prepareStatement(query);
                    pstmt.setString(1, id);
                    int j= pstmt.executeUpdate();
                    if (j >= 1)
                    {
                        success1= "Records deleted successfully";
                    }
                    else
                    {
                        error1=" Deletion of record failed";
                    }
                    request.setAttribute("success1", success1);
                    request.setAttribute("error1", error1);
                }
                RequestDispatcher rd= request.getRequestDispatcher("/wlpartnercommmappinglist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
            catch (Exception e)
            {
               logger.error("Eception:: ",e);
                RequestDispatcher rd= request.getRequestDispatcher("/wlpartnercommmappinglist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request,response);
                return;
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(con);
            }
        }

        try
        {
            logger.debug("going for validation");
            validateOptionalParameter(request);
            logger.debug("validation done");
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            logger.debug("message..."+e.getMessage());
            request.setAttribute("message",errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/wlpartnercommmappinglist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
            return;
        }

        Hashtable hash = null;
        WLPartnerInvoiceManager wlPartnerInvoiceManager=new WLPartnerInvoiceManager();
        StringBuffer sb = new StringBuffer();
        RequestDispatcher rd = request.getRequestDispatcher("/wlpartnercommmappinglist.jsp?ctoken=" + user.getCSRFToken());
        if(!ESAPI.validator().isValidInput("partnername", partnerId, "Numbers", 10,true))
        {
            sb.append("Invalid Partner Id<BR>");
        }
        if(!ESAPI.validator().isValidInput("gatewaybankname", pgtypeId, "Numbers", 10,true))
        {
            sb.append("Invalid Bank Name<BR>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg", sb.toString());
            rd.forward(request,response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        try
        {
            hash= wlPartnerInvoiceManager.getListOfWLPartnerCommissions(partnerId, pgtypeId, start, end);
            request.setAttribute("transdetails", hash);
        }
        catch (PZDBViolationException pze)
        {
            logger.error("System error while perform select query",pze);
            Functions.ShowMessage("Error", "Internal System Error while getting list of WL Partner Invoices");
        }
        request.setAttribute("statusMsg", sb.toString());
        RequestDispatcher rd1 = request.getRequestDispatcher("/wlpartnercommmappinglist.jsp?ctoken=" + user.getCSRFToken());
        rd1.forward(request, response);
        return;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
