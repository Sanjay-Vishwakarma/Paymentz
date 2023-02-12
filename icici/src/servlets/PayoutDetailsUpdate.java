package servlets;

/**
 * Created by Admin on 8/19/2019.
 */

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PayoutManager;
import com.manager.dao.PayoutDAO;
import com.manager.vo.payoutVOs.PayoutDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


public class PayoutDetailsUpdate extends HttpServlet
{
    Logger logger = new Logger(PayoutDetailsUpdate.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        logger.debug("Entering PayoutDetailsUpdate servlet::::::::::");
        FileUploadBean fub = new FileUploadBean();

        try
        {
            logger.debug("Entering Into PayoutDetailsUpdate");
            HttpSession session = request.getSession();
            Hashtable hashtable = null;
            StringBuilder sbError = new StringBuilder();
            String EOL = "<BR>";
            String payoutDate = null;
            String paymentReceiptDate = null;

            Functions functions = new Functions();
            PayoutDAO payoutDAO = new PayoutDAO();
            Hashtable payOutDetailsUpdate = null;
            String filePath = "", pathtolog = "", settleID = "", payoutCurrency = "", conversionRate = "", beneficiaryBankDetails = "", remitterBankDetails = "", remarks = "";
            String swiftMessage = "", swiftUpload = "", paymentReceiptConfirmation = "";
            User user = (User) session.getAttribute("ESAPIUserSessionKey");

            if (!Admin.isLoggedIn(session))
            {
                logger.debug("Admin is logout ");
                response.sendRedirect("/icici/logout.jsp");
                return;
            }

// upload method

            filePath = ApplicationProperties.getProperty("PAYOUT_REPORT_FILE");
            pathtolog = ApplicationProperties.getProperty("LOG_STORE");
            fub.setLogpath(pathtolog);
            fub.setSavePath(filePath);

            try
            {
                fub.doUpload1(request, null);
            }
            catch (SystemError sys)
            {
                payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdate(fub.getFieldValue("PayoutId"));
                request.setAttribute("viewpayout", payOutDetailsUpdate);
                request.setAttribute("isreadonly", "Update");
                request.setAttribute("message", "Your file already exists in the System. Please Upload new File.");
                RequestDispatcher rd = request.getRequestDispatcher("/payoutDetailsUpdate.jsp?mappingid=" + fub.getFieldValue("settledid") + "&action=update&ctoken=" + user.getCSRFToken());
                rd.forward(request, response);
                return;
            }

// end
            payoutDate = fub.getFieldValue("payoutDate");
            paymentReceiptDate = fub.getFieldValue("paymentReceiptDate");
            settleID = fub.getFieldValue("settledid");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat inputformat = new SimpleDateFormat("dd/MM/yyyy");

            /*if (payoutDate != null)
            {
                payoutDate = formatter.format(inputformat.parse(payoutDate));
                Date payDate = (Date) formatter.parse(payoutDate);

                paymentReceiptDate = formatter.format(inputformat.parse(paymentReceiptDate));
                Date receiptDate = (Date) formatter.parse(paymentReceiptDate);

                logger.debug("payoutDate :: " + payoutDate + "  paymentReceiptDate :: " + paymentReceiptDate);
                if (receiptDate.before(payDate))
                {
                    sbError.append("Invalid Payout Date" + EOL);
                    sbError.append("Invalid Payment Receipt Date" + EOL);
                }
            }*/
            if (functions.isValueNull(payoutDate))
            {
                payoutDate = formatter.format(inputformat.parse(payoutDate));
                Date payDate = (Date) formatter.parse(payoutDate);
                if (functions.isValueNull(paymentReceiptDate))
                {
                    paymentReceiptDate = formatter.format(inputformat.parse(paymentReceiptDate));
                    Date receiptDate = (Date) formatter.parse(paymentReceiptDate);

                    logger.debug("payoutDate :: " + payoutDate + "  paymentReceiptDate :: " + paymentReceiptDate);
                    if (receiptDate.before(payDate))
                    {
                        sbError.append("Invalid Payout Date" + EOL);
                        sbError.append("Invalid Payment Receipt Date" + EOL);
                    }
                }
            }
            else
            {
                sbError.append("Payout Date should not be blank" + EOL);
            }


            /*String payoutDate = fub.getFieldValue("payoutDate");
            if (functions.isValueNull(payoutDate))
            {
                payoutDate = formatter.format(inputformat.parse(payoutDate));
            }*/

            payoutCurrency = fub.getFieldValue("payoutCurrency");
            conversionRate = fub.getFieldValue("conversionRate");

            if (functions.isValueNull(payoutCurrency))
            {
                if (!ESAPI.validator().isValidInput("payoutCurrency", fub.getFieldValue("payoutCurrency"), "StrictString", 50, true))
                {
                    sbError.append("Invalid Payout Currency." + EOL);
                }
            }
            else
                sbError.append("Invalid Payout Currency." + EOL);

            if (functions.isValueNull(conversionRate))
            {
                if (!ESAPI.validator().isValidInput("conversionRate", fub.getFieldValue("conversionRate"), "Amountpayout", 20, true))
                {
                    sbError.append("Invalid Conversion Rate." + EOL);
                }
                if (fub.getFieldValue("conversionRate").equals("0.00"))
                {
                    sbError.append("Invalid Conversion Rate." + EOL);
                }
            }

            else
                sbError.append("Invalid Conversion Rate." + EOL);

            if (functions.isValueNull(fub.getFieldValue("payoutAmount")))
            {

                if (!ESAPI.validator().isValidInput("payoutAmount", fub.getFieldValue("payoutAmount"), "AmountMinus", 20, true))
                {
                    sbError.append("Invalid Payout Amount." + EOL);
                }
            }
            else
                sbError.append("Invalid Payout Amount." + EOL);


            if (sbError.length() > 0)
            {
                payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdate(fub.getFieldValue("PayoutId"));
                request.setAttribute("viewpayout", payOutDetailsUpdate);
                request.setAttribute("isreadonly", "Update");
                request.setAttribute("PayoutId", fub.getFieldValue("PayoutId"));
                request.setAttribute("error", sbError.toString());
                request.setAttribute("settledid", settleID);
                RequestDispatcher rd1 = request.getRequestDispatcher("/payoutDetailsUpdate.jsp?mappingid=" + fub.getFieldValue("settledid") + "&action=update&ctoken=" + user.getCSRFToken());
               /* logger.debug("addPayout java:::::"+"addPayout.jsp?mappingid="+payoutDetailsVO.getSettledId()+"&action=update&ctoken=" + user.getCSRFToken());*/
                rd1.forward(request, response);

            }
            else
            {
                String payoutAmount = fub.getFieldValue("payoutAmount");
                /*if (functions.isValueNull(fub.getFieldValue("payoutAmount")))
                    payoutAmount = Double.parseDouble(fub.getFieldValue("payoutAmount"));*/
                beneficiaryBankDetails = fub.getFieldValue("beneficiarybankdetails");
                remitterBankDetails = fub.getFieldValue("remitterbankdetails");
                remarks = fub.getFieldValue("remarks");
                swiftMessage = fub.getFieldValue("swiftmessage");
                swiftUpload = fub.getFilename();
            /*String paymentReceiptDate = fub.getFieldValue("paymentReceiptDate");
            if (functions.isValueNull(paymentReceiptDate))
            {
                paymentReceiptDate = formatter.format(inputformat.parse(paymentReceiptDate));
            }*/
                paymentReceiptConfirmation = fub.getFieldValue("paymentReceiptConfirmation");

                PayoutDetailsVO payoutDetailsVO = new PayoutDetailsVO(); // getter setter method call

                // Created SetMethod PayoutDetailsVO class
                payoutDetailsVO.setSettledId(settleID);
                payoutDetailsVO.setPayoutDate(payoutDate);
                payoutDetailsVO.setPayoutCurrency(payoutCurrency);
                payoutDetailsVO.setConversionRate(conversionRate);
                payoutDetailsVO.setPayoutAmount(payoutAmount);
                payoutDetailsVO.setBeneficiaryBankDetails(beneficiaryBankDetails);
                payoutDetailsVO.setRemitterBankDetails(remitterBankDetails);
                payoutDetailsVO.setRemarks(remarks);
                payoutDetailsVO.setSwiftMessage(swiftMessage);
                payoutDetailsVO.setSwiftUpload(swiftUpload);
                //System.out.println("inside servlet ::::"+ (functions.isValueNull(paymentReceiptDate)?paymentReceiptDate:"0000-00-00 00:00:00"));
                payoutDetailsVO.setPaymentReceiptDate(functions.isValueNull(paymentReceiptDate)?paymentReceiptDate:"0000-00-00 00:00:00");
                payoutDetailsVO.setPaymentReceiptConfirmation(paymentReceiptConfirmation);
                payoutDetailsVO.setPayoutId(fub.getFieldValue("PayoutId"));


                //System.out.println(payoutDetailsVO + "entering payoutdetailsvo");  // Display Data into Tomcat

                PayoutManager payoutManager = new PayoutManager();

                request.setAttribute("settledid", settleID);
                request.setAttribute("reportid", fub.getFieldValue("reportid"));
                request.setAttribute("payoutid", fub.getFieldValue("PayoutId"));
                request.setAttribute("payout_date", payoutDate);
                request.setAttribute("payout_currency", payoutCurrency);
                request.setAttribute("conversion_rate", conversionRate);
                request.setAttribute("payout_amount", payoutAmount);
                request.setAttribute("beneficiary_bank_details", beneficiaryBankDetails);
                request.setAttribute("remitter_bank_details", remitterBankDetails);
                request.setAttribute("remarks", remarks);
                request.setAttribute("swift_message", swiftMessage);
                request.setAttribute("swift_upload", swiftUpload);
                request.setAttribute("payment_receipt_date", paymentReceiptDate);
                request.setAttribute("payment_receipt_confirmation", paymentReceiptConfirmation);


                String status = payoutManager.merchantpayOutUpdate(payoutDetailsVO);

                payOutDetailsUpdate = payoutDAO.getPayOutDetailsUpdate(payoutDetailsVO.getPayoutId());
                request.setAttribute("viewpayout", payOutDetailsUpdate);
                request.setAttribute("isreadonly", "Update");

                request.setAttribute("message", status);
                RequestDispatcher rd1 = request.getRequestDispatcher("/payoutDetailsUpdate.jsp?mappingid=" + payoutDetailsVO.getSettledId() + "&action=update&ctoken=" + user.getCSRFToken());
                rd1.forward(request, response);
            }
        }
        catch (Exception e)
        {
           logger.error("Catch Exception..",e);
        }
    }

}



