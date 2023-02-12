import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.manager.dao.PayoutDAO;
import com.manager.dao.TerminalDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.WireVO;
import com.payment.exceptionHandler.PZDBViolationException;
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

/**
 * Created by Admin on 4/14/2017.
 */
public class AddMerchantWire extends HttpServlet
{
    private static Logger logger = new Logger(AddMerchantWire.class.getName());

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

        String EOL = "<BR>";
        StringBuilder sberror=new StringBuilder();
        String status1 = null;
        WireVO wireVO = new WireVO();
        TerminalVO terminalVO = new TerminalVO();
        PayoutDAO payoutDAO = new PayoutDAO();
        TerminalDAO terminalDAO=new TerminalDAO();
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
        Hashtable<String,String> details=new Hashtable<>();
        RequestDispatcher rd = request.getRequestDispatcher("/addwire.jsp?ctoken="+user.getCSRFToken());
        FileUploadBean fub = new FileUploadBean();

        try
        {
            fub.doUploadForMerchantSettled(request, null);
        }
        catch(SystemError sys){
            request.setAttribute("statusmsg", "Your file already exists in the System. Please Upload new File.");
            request.setAttribute("detailHash", details);
            rd.forward(request, response);
            return;
        }

        String isSubmitted = fub.getFieldValue("isSubmitted");
        Functions functions = new Functions();
        if ("true".equals(isSubmitted))
        {
            //System.out.println("toid-----"+fub.getFieldValue("toid")+"---accountid----"+fub.getFieldValue("accountid"));
            if (functions.hasHTMLTags(fub.getFieldValue("pgtypeid")))
            {
                sberror.append("Invalid Gateway,");
            }
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
            if (!ESAPI.validator().isValidInput("terminalid", fub.getFieldValue("terminalid"),"Numbers",10,false))
            {
                sberror.append("Invalid TerminalId ,");
            }
            else
            {
                details.put("terminalid", fub.getFieldValue("terminalid"));
            }
            /*if (!ESAPI.validator().isValidInput("paymode", fub.getFieldValue("paymode"),"Numbers",10,false))
            {
                sberror.append("Invalid Paymode ,");
            }
            else
            {
                details.put("paymode", fub.getFieldValue("paymode"));
            }
            if (!ESAPI.validator().isValidInput("cardtype", fub.getFieldValue("cardtype"),"Numbers",10,false))
            {
                sberror.append("Invalid Card Type,");
            }
            else
            {
                details.put("cardtype", fub.getFieldValue("cardtype"));
            }*/
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
            /*if (!ESAPI.validator().isValidInput("currency", fub.getFieldValue("currency"),"StrictString",3,false))
            {
                sberror.append("Invalid Wire Currency ,");
            }
            else
            {
                details.put("currency", fub.getFieldValue("currency"));
            }*/
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
                details.put("rrtime",fub.getFieldValue("rrtime"));
            }
            if (!ESAPI.validator().isValidInput("cycleno",fub.getFieldValue("cycleno"),"Numbers",25,false))
            {
                sberror.append("Invalid Settlement Cycle No ,");
            }
            else
            {
                details.put("cycleno", fub.getFieldValue("cycleno"));
            }
            /*if (!ESAPI.validator().isValidInput("settlementreportfilepath",fub.getFieldValue("settlementreportfilepath"),"reportFile",120,false))
            {
                sberror.append("Invalid Settlement Report File Name ,");
            }
            if (!ESAPI.validator().isValidInput("settledtransactionfilepath",fub.getFieldValue("settledtransactionfilepath"), "transactionFile",120,false))
            {
                sberror.append("Invalid Settlement File Name");
            }*/

            if (sberror.length() > 0)
            {
                request.setAttribute("statusmsg", sberror.toString());
                request.setAttribute("detailHash", details);
                rd.forward(request, response);
                return;
            }
            /*else
            {
                if(!("pdf".equals(fub.getFieldValue("reportpath").split("\\.")[1])))
                {
                    sberror.append("Invalid Settlement Report File Name ,");
                }
                if(!("xls".equals(fub.getFieldValue("settletranspath").split("\\.")[1])))
                {
                    sberror.append("Invalid Settlement File Name");
                }
                if (sberror.length() > 0)
                {
                    request.setAttribute("statusmsg", sberror.toString());
                    rd.forward(request, response);
                    return;
                }
            }*/

        }

        String firstDateTimestamp= commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("firstdate"),fub.getFieldValue("settledstarttime"));
        String lastDateTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("lastdate"),fub.getFieldValue("settledendtime"));
        String declinedcoverdateuptoTimestamp=commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("declinedcoverdateupto"),fub.getFieldValue("declinedcovertimeupto"));
        String reversedcoverdateuptoTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("reversedcoverdateupto"),fub.getFieldValue("reversedcovertimeupto"));
        String chargebackcoverdateuptoTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("chargebackcoverdateupto"),fub.getFieldValue("chargebackcovertimeupto"));
        String rrDateTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(fub.getFieldValue("rrdate"),fub.getFieldValue("rrtime"));

        String amount =  fub.getFieldValue("amount");
        String balanceamount = fub.getFieldValue("balanceamount");
        String unpaidamount = fub.getFieldValue("unpaidamount");
        String famount = fub.getFieldValue("famount");
        String currency = "";
        String status = fub.getFieldValue("status");
        String reportpath = fub.getFieldValue("settlementreportfilepath");
        String settletranspath = fub.getFieldValue("settledtransactionfilepath");
        String memberid = fub.getFieldValue("toid");
        String terminalid = fub.getFieldValue("terminalid");
        String accountid = fub.getFieldValue("accountid");
        String paymode = fub.getFieldValue("paymode");
        String cardtype = fub.getFieldValue("cardtype");
        String rrwire = fub.getFieldValue("rrwire");
        String rrdate = fub.getFieldValue("rrdate");
        String cycleno = fub.getFieldValue("cycleno");
        logger.debug("memberid-----"+memberid);
        logger.debug("firstdate-----"+firstDateTimestamp);
        logger.debug("lastdate-----"+lastDateTimestamp);
        logger.debug("declinedcoverdateuptoTimestamp-----"+declinedcoverdateuptoTimestamp);
        logger.debug("reversedcoverdateuptoTimestamp-----"+reversedcoverdateuptoTimestamp);
        logger.debug("chargebackcoverdateuptoTimestamp-----"+chargebackcoverdateuptoTimestamp);
        logger.debug("rrdate-----"+rrdate);
        logger.debug("reportpath-----"+reportpath);
        logger.debug("settletranspath-----"+settletranspath);
        logger.debug("rrDateTimestamp-----"+rrDateTimestamp);

        try
        {
            terminalVO=terminalDAO.getActiveInActiveTerminalInfo(terminalid);
            currency=terminalVO.getCurrency();
        }
        catch (PZDBViolationException e)
        {
            request.setAttribute("statusmsg","SQL Exception Generated!");
            request.setAttribute("detailHash", details);
            rd.forward(request, response);
            return;
        }
        wireVO.setMemberId(memberid);
        wireVO.setMarkForDeletion("N");
        wireVO.setWireAmount(Double.parseDouble(amount));
        wireVO.setWireBalanceAmount(Double.parseDouble(balanceamount));
        wireVO.setUnpaidAmount(0.00);
        wireVO.setNetFinalAmount(Double.parseDouble(famount));
        wireVO.setCurrency(currency);
        wireVO.setStatus("unpaid");
        wireVO.setRollingReserveIncluded("Y");
        wireVO.setFirstDate(firstDateTimestamp);
        wireVO.setLastDate(lastDateTimestamp);
        wireVO.setDeclinedcoverdateupto(declinedcoverdateuptoTimestamp);
        wireVO.setReversedcoverdateupto(reversedcoverdateuptoTimestamp);
        wireVO.setChargebackcoverdateupto(chargebackcoverdateuptoTimestamp);
        wireVO.setReserveReleasedUptoDate(rrDateTimestamp);
        wireVO.setSettlementCycleNo(cycleno);
        wireVO.setSettlementReportFilePath(reportpath);
        wireVO.setTerminalId(terminalid);
        wireVO.setSettledTransactionFilePath(settletranspath);
        terminalVO.setAccountId(accountid);
        wireVO.setTerminalVO(terminalVO);

        status1 = payoutDAO.generateSettlementCycleWireNew(wireVO);
        if("success".equals(status1))
        {
            request.setAttribute("statusmsg",status1);
            request.setAttribute("detailHash", details);
            rd.forward(request, response);
            return;
        }
        else
        {
            request.setAttribute("statusmsg",status1);
            request.setAttribute("detailHash", details);
            rd.forward(request, response);
            return;
        }
    }
}
