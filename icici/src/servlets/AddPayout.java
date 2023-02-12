package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PayoutManager;
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

/**
 * Created with IntelliJ IDEA.
 * User: sanjeet
 * Date: 1/12/15
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */


public class AddPayout extends HttpServlet
{
    Logger logger = new Logger(AddPayout.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Entering Addpayout servlet::::::::::");

        FileUploadBean fub = new FileUploadBean();

        try
        {
            logger.debug("Entering Into AddPayout");
            HttpSession session = request.getSession();
            Hashtable hashtable = null;
            Hashtable detailhash = new Hashtable();
            boolean flag = true;
            //String errormsg = "";
            //String EOL = "<br>";
            StringBuilder sbError = new StringBuilder();
            String EOL = "<BR>";
            String payoutDate = null;
            String paymentReceiptDate = null;

            User user = (User) session.getAttribute("ESAPIUserSessionKey");
            Functions functions = new Functions();
            String filePath = "", pathtolog = "", settleID = "", payoutCurrency = "", conversionRate = "", beneficiaryBankDetails = "", remitterBankDetails = "", remarks = "";
            String swiftMessage = "", swiftUpload = "", paymentReceiptConfirmation = "";

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
                request.setAttribute("message", "Your file already exists in the System. Please Upload new File.");
                RequestDispatcher rd = request.getRequestDispatcher("/addPayout.jsp?mappingid=" + fub.getFieldValue("settledid") + "&action=update&ctoken=" + user.getCSRFToken());
                rd.forward(request, response);

                return;
            }

// End
            payoutDate = fub.getFieldValue("payoutDate");
            paymentReceiptDate = fub.getFieldValue("paymentReceiptDate");
            settleID = fub.getFieldValue("settledid");
            String cycleid = fub.getFieldValue("reportid");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat inputformat = new SimpleDateFormat("dd/MM/yyyy");

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
                        sbError.append("Invalid Payout Date " + EOL);
                        sbError.append("Invalid Payment ReceiptDate" + EOL);
                    }
                    request.setAttribute("paymentReceiptDate", fub.getFieldValue("paymentReceiptDate"));
                }
                request.setAttribute("payoutDate", fub.getFieldValue("payoutDate"));
            }

            else
            {
                sbError.append("Payout Date should not be blank" + EOL);

                request.setAttribute("payoutDate",fub.getFieldValue("payoutDate"));
                //request.setAttribute("paymentReceiptDate",fub.getFieldValue("paymentReceiptDate"));
            }

          /*  String payoutDate = fub.getFieldValue("payoutDate");

            if (functions.isValueNull(payoutDate))
            {
                payoutDate = formatter.format(inputformat.parse(payoutDate));
            }*/
            /*if(!ESAPI.validator().isValidInput("payout_currency",request.getParameter("payout_currency"),"currency_unit",5,false))
            {
                errormsg=errormsg + "Invalid Currency." + EOL;
                detailhash.put("payout_currency", fub.getFieldValue("payout_currency"));
            }
            else
            {
                detailhash.put("payout_currency", fub.getFieldValue("payout_currency"));
            }*/
            payoutCurrency = fub.getFieldValue("payoutCurrency");
            conversionRate = fub.getFieldValue("conversionRate");


            if (functions.isValueNull(fub.getFieldValue("payoutCurrency")))
            {
                if (!ESAPI.validator().isValidInput("payoutCurrency", fub.getFieldValue("payoutCurrency"), "StrictString", 20, true))
                {
                    sbError.append("Invalid Payout Currency." + EOL);
                }
                request.setAttribute("payoutCurrency",payoutCurrency);

            }
            else
            {
                sbError.append("Invalid Payout Currency." + EOL);
                request.setAttribute("payoutCurrency",payoutCurrency);
            }

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
                request.setAttribute("ConversionRate",conversionRate);
            }
            else
            {
                sbError.append("Invalid Conversion Rate." + EOL);
                request.setAttribute("ConversionRate",conversionRate);
            }

            if (functions.isValueNull(fub.getFieldValue("payoutAmount")))
            {
                if (!ESAPI.validator().isValidInput("payoutAmount", fub.getFieldValue("payoutAmount"), "AmountMinus", 20, true))
                {
                    sbError.append("Invalid Payout Amount." + EOL);
                }
                request.setAttribute("payoutAmount",fub.getFieldValue("payoutAmount"));
            }
            else
            {
                sbError.append("Invalid Payout Amount." + EOL);
                request.setAttribute("payoutAmount",fub.getFieldValue("payoutAmount"));
            }

            if(sbError.length()>0)
            {
                request.setAttribute("beneficiaryBankDetails",fub.getFieldValue("beneficiarybankdetails"));
                request.setAttribute("remitterBankDetails",fub.getFieldValue("remitterbankdetails"));
                request.setAttribute("remarks",fub.getFieldValue("remarks"));
                request.setAttribute("swiftMessage",fub.getFieldValue("swiftmessage"));
                request.setAttribute("error", sbError.toString());
                RequestDispatcher rd1 = request.getRequestDispatcher("/addPayout.jsp?mappingid="+fub.getFieldValue("settledid")+"&action=update&ctoken=" + user.getCSRFToken());
               /* logger.debug("addPayout java:::::"+"addPayout.jsp?mappingid="+payoutDetailsVO.getSettledId()+"&action=update&ctoken=" + user.getCSRFToken());*/
                rd1.forward(request, response);

            }
            else
            {
                String payoutAmount = fub.getFieldValue("payoutAmount");

           /* double payoutAmount = 0.0;
            if (functions.isValueNull(fub.getFieldValue("payoutAmount")))
            {
                //payoutAmount = Double.parseDouble(fub.getFieldValue("payoutAmount"));
                payoutAmount = fub.getFieldValue("payoutAmount");
                System.out.println("PAYAMT::  "+ payoutAmount);
            }*/

            beneficiaryBankDetails = fub.getFieldValue("beneficiarybankdetails");
            remitterBankDetails = fub.getFieldValue("remitterbankdetails");
            remarks = fub.getFieldValue("remarks");
            swiftMessage = fub.getFieldValue("swiftmessage");
            swiftUpload = fub.getFilename();
           /* String paymentReceiptDate = fub.getFieldValue("paymentReceiptDate");
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
            payoutDetailsVO.setPaymentReceiptDate(functions.isValueNull(paymentReceiptDate)?paymentReceiptDate:"0000-00-00 00:00:00");
            payoutDetailsVO.setPaymentReceiptConfirmation(paymentReceiptConfirmation);
            payoutDetailsVO.setCycleid(cycleid);


            PayoutManager payoutManager = new PayoutManager();
                request.setAttribute("settledid", settleID);
            /*request.setAttribute("payout_date", payoutDate);
            request.setAttribute("payout_currency", payoutCurrency);
            request.setAttribute("conversion_rate", conversionRate);
            request.setAttribute("payout_amount", payoutAmount);
            request.setAttribute("beneficiary_bank_details", beneficiaryBankDetails);
            request.setAttribute("remitter_bank_details", remitterBankDetails);
            request.setAttribute("remarks", remarks);
            request.setAttribute("swift_message", swiftMessage);
            request.setAttribute("swift_upload", swiftUpload);
            request.setAttribute("payment_receipt_date", paymentReceiptDate);
            request.setAttribute("payment_receipt_confirmation", paymentReceiptConfirmation);*/


                request.setAttribute("payoutDate","");
                request.setAttribute("paymentReceiptDate","");
                request.setAttribute("payoutCurrency","");
                request.setAttribute("ConversionRate","");
                request.setAttribute("payoutAmount","");
                request.setAttribute("beneficiaryBankDetails","");
                request.setAttribute("remitterBankDetails","");
                request.setAttribute("remarks","");
                request.setAttribute("swiftMessage","");
                request.setAttribute("cycleid",cycleid);


            String status = payoutManager.getMerchantpayOutHash(payoutDetailsVO);
                String ctokenSplit[] = status.split("Id=");
                String payotid=ctokenSplit[1];
                request.setAttribute("message", status);
                RequestDispatcher rd1 = request.getRequestDispatcher("/servlet/ActionWireManager?message="+status+"&payoutid="+payotid+"&mappingid="+payoutDetailsVO.getSettledId()+"&action=update&ctoken=" + user.getCSRFToken());
                logger.debug("addPayout java:::::"+"addPayout.jsp?mappingid="+payoutDetailsVO.getSettledId()+"&action=update&ctoken=" + user.getCSRFToken());
                rd1.forward(request, response);
            }
        }
        catch (Exception e)
        {
           logger.error("Catch Exception...",e);
        }

    }
}



