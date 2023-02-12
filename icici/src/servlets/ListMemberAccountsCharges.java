import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargeManager;
import com.manager.dao.GatewayAccountDAO;
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
import java.util.Set;
/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 11:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListMemberAccountsCharges extends HttpServlet
{
    static Logger log = new Logger(ListMemberAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        int records=15;
        int pageno=1;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String errormsg="";
        String EOL = "<BR>";
        Functions functions = new Functions();
        Hashtable hash = null;
        Connection conn = null;

        String delete="";

        if(functions.isValueNull(req.getParameter("delete")))
            delete = req.getParameter("delete");

        String qry="";
        PreparedStatement pstmt2=null;
        String success1="";
        String error="";

        if (delete.equalsIgnoreCase("delete"))
        {
            try
            {
                String id1=req.getParameter("ids");
                String ids[]={};
                ids = id1.split(",");
                for (String id : ids)
                {
                    conn = Database.getConnection();
                    qry = "DELETE FROM member_accounts_charges_mapping WHERE mappingid=?";
                    pstmt2 = conn.prepareStatement(qry);
                    pstmt2.setString(1, id);
                    int j = pstmt2.executeUpdate();
                    if (j >= 1)
                    {
                        success1 = "Records Deleted Successfully.";
                    }
                    else
                    {
                        error = "Delete failed";
                    }
                    req.setAttribute("success1", success1);
                    req.setAttribute("error", error);
                }
                RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            catch (Exception e)
            {
                log.error(e);
                RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            finally
            {
                Database.closeConnection(conn);
            }
        }

            try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        String memberId = "";
        String accountId = "";
        String gateway = "";
        String currency = "";
        String pgTypeId = "";


        ChargeManager chargeManager = new ChargeManager();
        GatewayAccountDAO gatewayAccountDAO = new GatewayAccountDAO();
        memberId= req.getParameter("memberid");
        req.setAttribute("memberid",memberId);
        if(!req.getParameter("memberid").equals("0"))
        {
            memberId= req.getParameter("memberid");
            req.setAttribute("memberid",memberId);
        }
        if(functions.isValueNull(req.getParameter("pgtypeid")) && !"0".equals(req.getParameter("pgtypeid")) && "0".equals(req.getParameter("pgtypeid")))
        {
            String gatewayArr[] = req.getParameter("pgtypeid").split("-");
            gateway = gatewayArr[0];
            currency = gatewayArr[1];
            Set<String> accountids = null;
            try
            {
                accountids = gatewayAccountDAO.getAccountIds(gateway);
            }
            catch (Exception e)
            {
                log.error("Exception::::::::" + e);
                req.setAttribute("errormessage", "Internal error while processing your request");
                RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
            }
            accountId = String.valueOf(accountids).replace("[","").replace("]","");
        }
        if(!req.getParameter("accountid").equals("0"))
        {
            accountId= req.getParameter("accountid");
            req.setAttribute("accountid",accountId);
        }
        if(!req.getParameter("pgtypeid").equals("0"))
        {
            pgTypeId= req.getParameter("pgtypeid");
            req.setAttribute("pgtypeid",pgTypeId);
        }

        String payModeId = req.getParameter("paymode");
        String cardTypeId = req.getParameter("cardtype");
        String chargeId = req.getParameter("chargename");
        String mChargeValue = req.getParameter("mchargevalue");
        String aChargeValue = req.getParameter("achargevalue");
        String pChargeValue = req.getParameter("pchargevalue");
        String chargeType = req.getParameter("chargetype");
        String terminalid = req.getParameter("terminalid");

        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        if(0==pageno || 00==pageno){
            req.setAttribute("errormessage", "Invalid Page No.");
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        start = (pageno - 1) * records;
        end = records;

        try
        {
            hash = chargeManager.getMemberAccountCharges(memberId, accountId, payModeId, cardTypeId, chargeId, mChargeValue, aChargeValue, pChargeValue, chargeType, terminalid, gateway, currency, start, end,actionExecutorId,actionExecutorName);
            log.debug("hashh:::"+hash);
        }
        catch (PZDBViolationException e)
        {
            log.error("PZDBViolationException:::::", e);
            req.setAttribute("errormessage", "Internal error while processing your request");
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
        }
        StringBuffer sb=new StringBuffer();

        if(!ESAPI.validator().isValidInput("pgtypeid", pgTypeId, "alphanum", 40,true))
        {
            sb.append("Invalid Bank Name,");
        }
       /* if(!ESAPI.validator().isValidInput("accountid", accountId, "Numbers", 50,true))
        {
            sb.append("Invalid Account Id,");
        }*/
        if(!ESAPI.validator().isValidInput("memberid", memberId, "Numbers", 10,true))
        {
            sb.append("Invalid Member Id,");
        }

        if(!ESAPI.validator().isValidInput("terminalid", terminalid, "Numbers", 5,true))
        {
            sb.append("Invalid Terminal Id,");
        }

        if(sb.length()>0)
        {
            req.setAttribute("success1", sb.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req,res);
            return;
        }
        req.setAttribute("transdetails",hash);
        req.setAttribute("paymode",payModeId);
        req.setAttribute("cardtype",cardTypeId);
        req.setAttribute("chargename", chargeId);
        req.setAttribute("mchargevalue",mChargeValue);
        req.setAttribute("pchargevalue",pChargeValue);
        req.setAttribute("achargevalue",aChargeValue);
        req.setAttribute("actionExecutorId",actionExecutorId);
        req.setAttribute("actionExecutorName",actionExecutorName);
        RequestDispatcher rd = req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.MAPPINGID);
       // inputFieldsListMandatory.add(InputFields.MEMBERID);
       inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.PAYMODE);
        inputFieldsListMandatory.add(InputFields.CARDTYPE);
        inputFieldsListMandatory.add(InputFields.CHARGENAME);
        inputFieldsListMandatory.add(InputFields.CHARGETYPE);
        inputFieldsListMandatory.add(InputFields.ACHARGEVALUE);
        inputFieldsListMandatory.add(InputFields.MCHARGEVALUE);
        inputFieldsListMandatory.add(InputFields.PCHARGEVALUE);
        //inputFieldsListMandatory.add(InputFields.TERMINALID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
