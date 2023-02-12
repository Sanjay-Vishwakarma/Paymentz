import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.dao.MerchantDAO;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.payoutVOs.MerchantWireVO;
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
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 1/12/15
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMerchantWire  extends HttpServlet
{
    Logger logger=new Logger(UpdateMerchantWire.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        try
        {
            logger.debug("Entering Into UpdateMerchantWire");
            HttpSession session = request.getSession();
            Hashtable hashtable = null;

            User user = (User) session.getAttribute("ESAPIUserSessionKey");

            if (!Admin.isLoggedIn(session))
            {
                logger.debug("Admin is logout ");
                response.sendRedirect("/icici/logout.jsp");
                return;
            }

            String errormsg = "";
            String EOL = "<BR>";
            String mailSent = "";
            StringBuffer sberror = new StringBuffer();
            String action = request.getParameter("UpdateForAll");
            String update = request.getParameter("Update");
            int count=0;
            boolean isValidReceiverBankDetails=false;
            Functions functions=new Functions();
            boolean result = false;

            if (!ESAPI.validator().isValidInput("settledate", request.getParameter("settledate"), "fromDate", 20, false))
            {
              sberror.append("Invalid Settlement Date"+EOL);
            }
            if (!ESAPI.validator().isValidInput("amount", request.getParameter("amount"), "AmountMinus", 20, false))
            {
                sberror.append("Invalid Processing Amount" + EOL);
            }
            if (!ESAPI.validator().isValidInput("balanceamount", request.getParameter("balanceamount"), "AmountMinus", 20, false))
            {
                sberror.append("Invalid Gross Amount " + EOL);
            }
            if (!ESAPI.validator().isValidInput("famount", request.getParameter("famount"), "AmountMinus", 20, false))
            {
                sberror.append("Invalid Net Final Amount" + EOL);
            }
            if (!ESAPI.validator().isValidInput("unpaidamount", request.getParameter("unpaidamount"), "AmountMinus", 20, false))
            {
                sberror.append("Invalid UnPaid Amount" + EOL);
            }
           /* if (!ESAPI.validator().isValidInput("payoutAmount", request.getParameter("payoutAmount"), "AmountMinus", 20, true))
            {
                sberror.append("Invalid Payout Amount" + EOL);
            }
            if (!ESAPI.validator().isValidInput("payoutCurrency", request.getParameter("payoutCurrency"), "currency_unit", 3, true))
            {
                sberror.append("Invalid Payout Currency" + EOL);
            }
            if (!ESAPI.validator().isValidInput("payerBankDetails", request.getParameter("payerBankDetails"), "SafeString", 50, true))
            {
                sberror.append("Invalid Remitter Bank Details" + EOL);
            }
            if(functions.isValueNull(request.getParameter("receiverBankDetailsCount")) && !"0".equalsIgnoreCase(request.getParameter("receiverBankDetailsCount")))
            {
                count=Integer.parseInt(request.getParameter("receiverBankDetailsCount"));
                for (int i=0;i<=count;i++)
                {
                    if (!ESAPI.validator().isValidInput("receiverBankDetails", request.getParameter("receiverBankDetails_"+i), "SafeString", 50, true))
                    {
                        isValidReceiverBankDetails=true;
                    }
                }
            }
            if (!ESAPI.validator().isValidInput("receiverBankDetails", request.getParameter("receiverBankDetails"), "SafeString", 50, true) || isValidReceiverBankDetails)
            {
                sberror.append("Invalid Beneficiary Bank Details" + EOL);
            }
            if (!ESAPI.validator().isValidInput("paymentReceiptDate", request.getParameter("paymentReceiptDate"), "fromDate", 20, true))
            {
                sberror.append("Invalid Payment Receipt Date" + EOL);
            }
            if (!ESAPI.validator().isValidInput("remark", request.getParameter("remark"), "SafeString", 50, true))
            {
                sberror.append("Invalid Remark" + EOL);
            }
            if (!ESAPI.validator().isValidInput("conversionRate", request.getParameter("conversionRate"), "NDigitsAmount", 15, true))
            {
                sberror.append("Invalid Conversion Rate" + EOL);
            }*/
            if (sberror.length() > 0)
            {
                errormsg += "<center><font class=\"text\" face=\"arial\"><b>" + errormsg + sberror.toString() + EOL + "</b></font></center>";
                request.setAttribute("message", errormsg);
                RequestDispatcher rd = request.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

            String wireId = request.getParameter("id");
            String settlementDate = request.getParameter("settledate");
            Double amount = Double.valueOf(request.getParameter("amount"));
            Double balanceAmount = Double.valueOf(request.getParameter("balanceamount"));
            Double netFinalAmount = Double.valueOf(request.getParameter("famount"));
            Double unpaidAmount = Double.valueOf(request.getParameter("unpaidamount"));
            String status = request.getParameter("status");
            String accountId = request.getParameter("accountid");
            String memberId = request.getParameter("toid");
           /* String payoutCurrency = request.getParameter("payoutCurrency");
            double payoutAmount=0.0;
            if(functions.isValueNull(request.getParameter("payoutAmount")))
                 payoutAmount = Double.parseDouble(request.getParameter("payoutAmount"));
            String payerBankDetails = request.getParameter("payerBankDetails");
            String receiverBankDetails = request.getParameter("receiverBankDetails");
            if(count!=0)
            {
                for (int i = 1; i <= count+1; i++)
                {
                    if(functions.isValueNull(request.getParameter("receiverBankDetails_" + i)))
                    {
                        receiverBankDetails =receiverBankDetails+ "," + request.getParameter("receiverBankDetails_" + i);
                    }
                }
            }
            String paymentConfirmation = request.getParameter("paymentConfirmation");
            String paymentReceiptDate = request.getParameter("paymentReceiptDate");
            String remark = request.getParameter("remark");
            String conversionRate=request.getParameter("conversionRate");*/

            CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
            String settleDateTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(settlementDate, "00:00:00");
            //String paymentReceiptDateTimestamp = commonFunctionUtil.convertDatepickerToTimestamp(paymentReceiptDate, "00:00:00");

            MerchantWireVO merchantWireVO = new MerchantWireVO();

            merchantWireVO.setSettledId(wireId);
            merchantWireVO.setSettleDate(settleDateTimestamp);
            merchantWireVO.setAmount(amount);
            merchantWireVO.setBalanceAmount(balanceAmount);
            merchantWireVO.setNetFinalAmount(netFinalAmount);
            merchantWireVO.setUnpaidAmount(unpaidAmount);
            merchantWireVO.setStatus(status);
            merchantWireVO.setMemberId(memberId);
            merchantWireVO.setAccountId(accountId);
            /*merchantWireVO.setPayoutAmount(payoutAmount);
            merchantWireVO.setPayoutCurrency(payoutCurrency);
            merchantWireVO.setPayerBankDetails(payerBankDetails);
            merchantWireVO.setReceiverBankDetails(receiverBankDetails);
            merchantWireVO.setPaymentConfirmation(paymentConfirmation);
            merchantWireVO.setPaymentReceiptDate(paymentReceiptDateTimestamp);
            merchantWireVO.setRemark(remark);
            merchantWireVO.setConversionRate(conversionRate);
*/
            MerchantDAO merchantDAO = new MerchantDAO();

            if ("Update".equalsIgnoreCase(update))
            {
                merchantDAO.updateMerchantWire(merchantWireVO);
                if (merchantWireVO.isUpdated())
                {
                    //Call Payout Mail Email With Attachment
                    errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updated Successfully</b></font></center>";
                }
                else
                {
                    errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updation Failed</b></font></center>";
                }
            }

            if ("Update For All".equalsIgnoreCase(action))
            {
                result = merchantDAO.updateMerchantWireForAllTerminal(merchantWireVO);
                if (result == true)
                {
                    errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updated Successfully For ALL Terminals.</b></font></center>";
                }
                else
                {
                    errormsg += "<center><font class=\"textb\" face=\"arial\"><b>Wire Updation Failed</b></font></center>";
                }
            }
            request.setAttribute("message", errormsg);
            RequestDispatcher rd = request.getRequestDispatcher("/wirelist.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (Exception e)
        {
           logger.error("Catch Exception...",e);
        }
    }

}
