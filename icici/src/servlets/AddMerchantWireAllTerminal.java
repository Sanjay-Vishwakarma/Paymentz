import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.manager.dao.PayoutDAO;
import com.manager.dao.WhiteListDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.WireVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Mahima on 4/14/2017.
 */
public class AddMerchantWireAllTerminal extends HttpServlet
{
    private static Logger logger = new Logger(AddMerchantWireAllTerminal.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering in AddWireList");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sberror=new StringBuilder();
        String status1 = null;
        Hashtable<String,String> details=new Hashtable<>();
        WireVO wireVO = new WireVO();
        TerminalVO terminalVO = new TerminalVO();
        WhiteListDAO whiteListDAO=new WhiteListDAO();
        PayoutDAO payoutDAO = new PayoutDAO();
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        List<WireVO> listOfMerchantId=null;

        RequestDispatcher rd = request.getRequestDispatcher("/addWireForAllTerminal.jsp?ctoken="+user.getCSRFToken());
        FileUploadBean fub = new FileUploadBean();

        try
        {
            fub.doUploadForMerchantSettled(request, null);
        }
        catch(SystemError sys){
            request.setAttribute("statusmsg", "Your file already exists in the System. Please Upload new File.");
            rd.forward(request, response);
            return;
        }
        long startDate = 0;
        long endDate = 0;
        try
        {
        String isSubmitted = fub.getFieldValue("isSubmitted");
        if ("true".equals(isSubmitted))
        {
            details.put("pgtypeid",fub.getFieldValue("pgtypeid"));
            if (!ESAPI.validator().isValidInput("toid", fub.getFieldValue("toid"),"Numbers",10,false) || fub.getFieldValue("toid").equalsIgnoreCase("0"))
            {
                sberror.append("Invalid MemberId ,");
            }
            else
            {
                details.put("toid",fub.getFieldValue("toid"));
            }
            if (!ESAPI.validator().isValidInput("accountid", fub.getFieldValue("accountid"),"Numbers",10,false) || fub.getFieldValue("accountid").equalsIgnoreCase("0"))
            {
                sberror.append("Invalid AccountId ,");
            }
            else
            {
                details.put("accountid", fub.getFieldValue("accountid"));
            }
            if (!ESAPI.validator().isValidInput("amount", fub.getFieldValue("amount"),"AmountStr",20,false))
            {
                sberror.append("Invalid Amount ,");
            }
            else
            {
                details.put("amount", fub.getFieldValue("amount"));
            }
            if (!ESAPI.validator().isValidInput("balanceamount", fub.getFieldValue("balanceamount"),"AmountStr",20,false))
            {
                sberror.append("Invalid Balance Amount ,");
            }
            else
            {
                details.put("balanceamount", fub.getFieldValue("balanceamount"));
            }
            if (!ESAPI.validator().isValidInput("unpaidamount", fub.getFieldValue("unpaidamount"),"AmountStr",20,false))
            {
                sberror.append("Invalid Unpaid Amount ,");
            }
            else
            {
                details.put("unpaidamount", fub.getFieldValue("unpaidamount"));
            }
            if (!ESAPI.validator().isValidInput("famount", fub.getFieldValue("famount"),"AmountStr",20,false))
            {
                sberror.append("Invalid Net Final Amount ,");
            }
            else
            {
                details.put("famount", fub.getFieldValue("famount"));
            }
            if (!ESAPI.validator().isValidInput("status", fub.getFieldValue("status"),"SafeString",100,false))
            {
                sberror.append("Invalid Wire Status ,");
            }
            else
            {
                details.put("status", fub.getFieldValue("status"));
            }
            if (!ESAPI.validator().isValidInput("rrwire", fub.getFieldValue("rrwire"),"SafeString",100,false))
            {
                sberror.append("Invalid IsRolling Reserve ,");
            }
            else
            {
                details.put("rrwire", fub.getFieldValue("rrwire"));
            }
            if (!ESAPI.validator().isValidInput("firstdate", fub.getFieldValue("firstdate"),"fromDate",16,false))
            {
                sberror.append("Invalid Settelment Start Date ,");
            }
            else
            {
                details.put("firstdate", fub.getFieldValue("firstdate"));
            }
            if (!ESAPI.validator().isValidInput("settledstarttime", fub.getFieldValue("settledstarttime"),"time",255,false))
            {
                sberror.append("Invalid Settlement Start Time ,");
            }
            else
            {
                details.put("settledstarttime", fub.getFieldValue("settledstarttime"));
            }
            if (!ESAPI.validator().isValidInput("lastdate", fub.getFieldValue("lastdate"),"fromDate",16,false))
            {
                sberror.append("Invalid Settelment End Date ,");
            }
            else
            {
                details.put("lastdate", fub.getFieldValue("lastdate"));
            }
            if (!ESAPI.validator().isValidInput("settledendtime", fub.getFieldValue("settledendtime"),"time",255,false))
            {
                sberror.append("Invalid Settlement End Time ,");
            }
            else
            {
                details.put("settledendtime", fub.getFieldValue("settledendtime"));
            }
            if (!ESAPI.validator().isValidInput("declinedcoverdateupto", fub.getFieldValue("declinedcoverdateupto"),"fromDate",16,false))
            {
                sberror.append("Invalid Declined Cover Date ,");
            }
            else
            {
                details.put("declinedcoverdateupto", fub.getFieldValue("declinedcoverdateupto"));
            }
            if (!ESAPI.validator().isValidInput("declinedcovertimeupto", fub.getFieldValue("declinedcovertimeupto"),"time",255,false))
            {
                sberror.append("Invalid Declined Covered Time ,");
            }
            else
            {
                details.put("declinedcovertimeupto", fub.getFieldValue("declinedcovertimeupto"));
            }
            if (!ESAPI.validator().isValidInput("reversedcoverdateupto", fub.getFieldValue("reversedcoverdateupto"),"fromDate",16,false))
            {
                sberror.append("Invalid Reversed Cover Date ,");
            }
            else
            {
                details.put("reversedcoverdateupto", fub.getFieldValue("reversedcoverdateupto"));
            }
            if (!ESAPI.validator().isValidInput("reversedcovertimeupto", fub.getFieldValue("reversedcovertimeupto"),"time",255,false))
            {
                sberror.append("Invalid Reversed Covered Time ,");
            }
            else
            {
                details.put("reversedcovertimeupto", fub.getFieldValue("reversedcovertimeupto"));
            }
            if (!ESAPI.validator().isValidInput("chargebackcoverdateupto", fub.getFieldValue("chargebackcoverdateupto"),"fromDate",16,false))
            {
                sberror.append("Inavlid Chargeback Cover Date ,");
            }
            else
            {
                details.put("chargebackcoverdateupto", fub.getFieldValue("chargebackcoverdateupto"));
            }
            if (!ESAPI.validator().isValidInput("chargebackcovertimeupto", fub.getFieldValue("chargebackcovertimeupto"),"time",255,false))
            {
                sberror.append("Invalid Chargeback Covered Time ,");
            }
            else
            {
                details.put("chargebackcovertimeupto", fub.getFieldValue("chargebackcovertimeupto"));
            }
            if (!ESAPI.validator().isValidInput("rrdate", fub.getFieldValue("rrdate"),"fromDate",16,false))
            {
                sberror.append("Invalid Reserve Release Date ,");
            }
            else
            {
                details.put("rrdate", fub.getFieldValue("rrdate"));
            }
            if (!ESAPI.validator().isValidInput("rrtime", fub.getFieldValue("rrtime"),"time",255,false))
            {
                sberror.append("Invalid Reserve Release Time ,");
            }
            else
            {
                details.put("rrtime", fub.getFieldValue("rrtime"));
            }
            if (!ESAPI.validator().isValidInput("cycleno",fub.getFieldValue("cycleno"),"Numbers",25,false))
            {
                sberror.append("Invalid Settlement Cycle No ,");
            }
            else
            {
                details.put("cycleno", fub.getFieldValue("cycleno"));
            }
            if (!"".equalsIgnoreCase(fub.getFieldValue("firstdate")) && !"".equalsIgnoreCase(fub.getFieldValue("lastdate")))
            {
                startDate = Date.parse(fub.getFieldValue("firstdate"));
                endDate = Date.parse(fub.getFieldValue("lastdate"));
                if (startDate >= endDate)
                {
                    sberror.append("Start Date is greater then End Date");
                }
            }
            if (!"0".equalsIgnoreCase(fub.getFieldValue("toid")) && !"0".equalsIgnoreCase(fub.getFieldValue("accountid")))
            {
                boolean valid = whiteListDAO.getMemberidAccountid(fub.getFieldValue("toid"), fub.getFieldValue("accountid"));
                if (valid == false)
                {
                    sberror.append("MemberID/AccountID NOT mapped.");
                }
            }
            if (sberror.length() > 0)
            {
                request.setAttribute("statusmsg", sberror.toString());
                request.setAttribute("detailHash", details);
                rd.forward(request, response);
                return;
            }
        }
        }
        catch (Exception e)
        {
            logger.debug("Exception---->"+e);
        }

        String firstDateTimestamp= commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("firstdate"),fub.getFieldValue("settledstarttime"));
        String lastDateTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("lastdate"),fub.getFieldValue("settledendtime"));
        String declinedcoverdateuptoTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("declinedcoverdateupto"), fub.getFieldValue("declinedcovertimeupto"));
        String reversedcoverdateuptoTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("reversedcoverdateupto"),fub.getFieldValue("reversedcovertimeupto"));
        String chargebackcoverdateuptoTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("chargebackcoverdateupto"),fub.getFieldValue("chargebackcovertimeupto"));
        String rrDateTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("rrdate"),fub.getFieldValue("rrtime"));

        String amount =  fub.getFieldValue("amount");
        String balanceAmount = fub.getFieldValue("balanceamount");
        String finalAmount = fub.getFieldValue("famount");
        String memberId = fub.getFieldValue("toid");
        String accountId = fub.getFieldValue("accountid");
        String cycleno = fub.getFieldValue("cycleno");
        String settledTransFile = fub.getFieldValue("settledTransFile");
        String settledReportFile = fub.getFieldValue("settledReportFile");

        try
        {
            listOfMerchantId=payoutDAO.getMemberTerminalId(memberId,accountId);
        }
        catch (Exception e){
            logger.debug("Exception"+e);
            request.setAttribute("statusmsg", "Internal Error while processing your request");
            rd.forward(request, response);
            return;
        }

        wireVO.setMemberId(memberId);
        wireVO.setMarkForDeletion("N");
        wireVO.setWireAmount(Double.parseDouble(amount));
        wireVO.setWireBalanceAmount(Double.parseDouble(balanceAmount));
        wireVO.setUnpaidAmount(0.00);
        wireVO.setNetFinalAmount(Double.parseDouble(finalAmount));
        wireVO.setStatus("unpaid");
        wireVO.setRollingReserveIncluded("Y");
        wireVO.setFirstDate(firstDateTimestamp);
        wireVO.setLastDate(lastDateTimestamp);
        wireVO.setDeclinedcoverdateupto(declinedcoverdateuptoTimestamp);
        wireVO.setReversedcoverdateupto(reversedcoverdateuptoTimestamp);
        wireVO.setChargebackcoverdateupto(chargebackcoverdateuptoTimestamp);
        wireVO.setReserveReleasedUptoDate(rrDateTimestamp);
        wireVO.setSettlementCycleNo(cycleno);
        wireVO.setSettlementReportFilePath(settledReportFile);
        wireVO.setSettledTransactionFilePath(settledTransFile);
        terminalVO.setAccountId(accountId);
        wireVO.setTerminalVO(terminalVO);
        wireVO.setReportid(payoutDAO.getreportid());
        status1 = payoutDAO.generateSettlementCycleWireForAllTerminal(wireVO,listOfMerchantId);
        if("success".equals(status1))
        {
            request.setAttribute("success",status1);
            rd.forward(request, response);
            return;
        }
        else
        {
            request.setAttribute("failure",status1);
            rd.forward(request, response);
            return;
        }
    }
}
