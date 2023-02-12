import com.directi.pg.*;
import com.manager.ChargeManager;
import com.manager.WiresManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.MerchantWireVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/26/14
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionMemberAccountsCharges extends HttpServlet
{
    private static Logger log = new Logger(ActionMemberAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ResultSet rs2 = null;
        String errormsg="";
        String success1="";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + e.getMessage() + EOL + "</b></font></center>";
            log.debug("message..." + e.getMessage());
            req.setAttribute("errormessage", errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        String action = req.getParameter("action");
        String mappingid = req.getParameter("mappingid");

        try
        {
            conn = Database.getRDBConnection();
            String qry = "";
            HashMap rsDetails = new HashMap();
            if ("history".equalsIgnoreCase(action))
            {
                qry = "SELECT m.terminalid,m.accountid,m.memberid,m.mappingid,h.* FROM ChargeVersionMemberMaster AS h, member_accounts_charges_mapping AS m WHERE m.mappingid=h.member_accounts_charges_mapping_id AND m.mappingid=?";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, mappingid);
                rs = pstmt.executeQuery();
                int i = 1;
                while (rs.next())
                {
                    HashMap record = new HashMap();
                    record.put("terminalid", rs.getString("terminalid"));
                    record.put("accountid", rs.getString("accountid"));
                    record.put("memberid", rs.getString("memberid"));
                    record.put("mappingid", rs.getString("mappingid"));
                    record.put("merchantChargeValue", rs.getString("merchantChargeValue"));
                    record.put("agentCommision", rs.getString("agentCommision"));
                    record.put("partnerCommision", rs.getString("partnerCommision"));
                    record.put("effectiveStartDate", rs.getString("effectiveStartDate"));
                    record.put("effectiveEndDate", rs.getString("effectiveEndDate"));

                    rsDetails.put(i, record);
                    i = i + 1;
                }

            }

           /* if("delete".equalsIgnoreCase(action))
            {
                int j=0;
                qry="DELETE FROM member_accounts_charges_mapping WHERE mappingid=?";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1,mappingid);
                j=pstmt.executeUpdate();
                if(j==1)
                {
                    success1 = "Deleted Successfully";
                    log.debug("Deleted::" +success1);
                }
                req.setAttribute("success1",success1);
            }
*/
            else if ("modify".equalsIgnoreCase(action))
            {
                qry = "select * from member_accounts_charges_mapping macm LEFT JOIN charge_master cm ON  macm.chargeid=cm.chargeid where mappingid=?";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, mappingid);
                rs2 = pstmt.executeQuery();
                if (rs2.next())
                {
                    rsDetails.put("chargename", rs2.getString("chargename"));
                    rsDetails.put("memberid", rs2.getString("memberid"));
                    rsDetails.put("accountid", rs2.getString("accountid"));
                    rsDetails.put("terminalid", rs2.getString("terminalid"));
                    rsDetails.put("paymodeid", rs2.getString("paymodeid"));
                    rsDetails.put("cardtypeid", rs2.getString("cardtypeid"));
                    rsDetails.put("chargeid", rs2.getString("chargeid"));
                    rsDetails.put("chargevalue", rs2.getString("chargevalue"));
                    rsDetails.put("agentchargevalue", rs2.getString("agentchargevalue"));
                    rsDetails.put("partnerchargevalue", rs2.getString("partnerchargevalue"));
                    rsDetails.put("valuetype", rs2.getString("valuetype"));
                    rsDetails.put("category", rs2.getString("category"));
                    rsDetails.put("sequencenum", rs2.getString("sequencenum"));
                    rsDetails.put("keyword", rs2.getString("keyword"));
                    rsDetails.put("subkeyword", rs2.getString("subkeyword"));
                    rsDetails.put("frequency", rs2.getString("frequency"));
                    rsDetails.put("isinput_required", rs2.getString("isinput_required"));
                }
                qry = "SELECT effectiveStartDate,effectiveEndDate FROM ChargeVersionMemberMaster WHERE chargeversionId=(SELECT MAX(chargeversionId) FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=?)";
                pstmt = conn.prepareStatement(qry);
                pstmt.setString(1, mappingid);
                rs2 = pstmt.executeQuery();
                int i = 0;
                if (rs2.next())
                {

                    rsDetails.put("lastupdateDate", rs2.getString("effectiveEndDate"));
                    rsDetails.put("effectiveStartDate", rs2.getString("effectiveStartDate"));
                }
                rsDetails.put("mappingid", mappingid);

                try
                {
                    ChargeManager chargeManager = new ChargeManager();
                    WiresManager wiresManager = new WiresManager();
                    ChargeVO chargeVO = null;
                    MerchantWireVO merchantWireVO = null;
                    TerminalVO terminalVO = null;

                    chargeVO = chargeManager.getMerchantChargeDetails(mappingid);
                    if (chargeVO != null)
                    {
                        terminalVO = new TerminalVO();
                        terminalVO.setMemberId(chargeVO.getMemberid());
                        terminalVO.setTerminalId(chargeVO.getTerminalid());
                        terminalVO.setAccountId(chargeVO.getAccountId());
                        merchantWireVO = wiresManager.getMerchantRecentWire(terminalVO);
                    }
                    if (merchantWireVO == null)
                    {
                        rsDetails.put("version", "false");
                    }
                    else
                    {
                        String endDate = merchantWireVO.getSettlementEndDate();
                        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
                        String errorMsg = commonFunctionUtil.newValidateDateWithNewFormat(endDate, rs2.getString("effectiveEndDate"), null, null);
                        if (functions.isValueNull(errorMsg))
                        {
                            rsDetails.put("lastupdateDate", endDate);
                        }
                    }
                }
                catch (PZDBViolationException e)
                {
                    log.error("PZDBViolationException::::::" + e);
                    errormsg = "<center><font class=\"text\" face=\"arial\"><b>Internal error while processing your request</b></font></center>";
                }
            }
            else
            {
                errormsg = "<center><font class=\"text\" face=\"arial\"><b> Action is not defined</b></font></center>";
            }
            req.setAttribute("action", action);
            req.setAttribute("chargedetails", rsDetails);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError:::::::", systemError);
            errormsg = "<center><font class=\"text\" face=\"arial\"><b>Internal error while processing your request</b></font></center>";
        }
        catch (SQLException e)
        {
            log.error("SQLException:::::::", e);
            errormsg = "<center><font class=\"text\" face=\"arial\"><b>Internal error while processing your request</b></font></center>";
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        req.setAttribute("errormessage", errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
        return;
    }

        /*if (functions.isValueNull(success1))
        {
            RequestDispatcher requestDispatcher=req.getRequestDispatcher("/listMemberAccountsCharges.jsp?ctoken=" + user.getCSRFToken());
            requestDispatcher.forward(req,res);
            return;
        }
        else
        {
            RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }*/

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.MAPPINGID);
        inputFieldsListMandatory.add(InputFields.ACTION);
        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
