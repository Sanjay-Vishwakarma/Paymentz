/*
 * Copyright 1999 QSI Payments, Inc.
 * All rights reserved.  This precautionary copyright notice against
 * inadvertent publication is neither an acknowledgement of publication,
 * nor a waiver of confidentiality.
 *
 * Identification:
 *	$Id: ReceiptPage.java,v 1.1 2012/10/21 22:48:34 cvs Exp $
 *
 * Description:
 *      Example Payment Client Receipt - this class extracts, marks up and
 *      displays the fields of the Digital Receipt.
 */

import PaymentClient.PaymentClientImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author SimonM
 * @version $Revision: 1.1 $
 */
public class ReceiptPage extends Object
{

    static public String CVS_ID = "@(#)$Id: ReceiptPage.java,v 1.1 2012/10/21 22:48:34 cvs Exp $";

    private PaymentClientImpl m_paymentClient = null;
    private HttpServletResponse m_servletResponse = null;

    /* ========================================================================
     *
     * Constructors
     */

    public ReceiptPage(PaymentClientImpl paymentClient,
                       HttpServletResponse httpServletResponse)
    {

        m_paymentClient = paymentClient;
        m_servletResponse = httpServletResponse;
    }

    /* ========================================================================
    *
    * Methods
    */

    /*
     * Extracts, marks up and displays the fields of the Digital Receipt.
     *
     * @param paymentClient a reference to the PaymentClientImpl class instance for which the
     *                      digitalReceipt is being processed.
     * @param res           the HttpServletResponse class in to send the output to.
     */
    public void display(String title, String againLink,
                        String againLinkText) throws IOException
    {

        // obtain the receipt values from the Payment Client result set
        String merchantID =
                m_paymentClient.getResultField("DigitalReceipt.MerchantId");
        String sessionID =
                m_paymentClient.getResultField("DigitalReceipt.SessionId");
        String amount =
                m_paymentClient.getResultField("DigitalReceipt.PurchaseAmountInteger");
        String locale =
                m_paymentClient.getResultField("DigitalReceipt.Locale");
        String receiptNumber =
                m_paymentClient.getResultField("DigitalReceipt.ReceiptNo");
        String transactionNumber =
                m_paymentClient.getResultField("DigitalReceipt.TransactionNo");
        String acquirerResponseCode =
                m_paymentClient.getResultField("DigitalReceipt.AcqResponseCode");

        // obtain the QSI reponse code, determine the description and
        // create the final two rows of the table containing the results
        String qsiRespCode =
                m_paymentClient.getResultField("DigitalReceipt.QSIResponseCode");

        String qsiRespCodeDesc = getResponseCodeDescription(qsiRespCode);

        // prepare the output stream - ie the HTML response writer
        m_servletResponse.setContentType("text/html");
        PrintWriter out = m_servletResponse.getWriter();

        // set its title
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<TITLE>" + title + "</TITLE>");
        out.println("</HEAD>");

        // start on the body
        out.println("<BODY>");

        out.println("<H1><center>" + title + "</center></H1>");
        out.println("<center>");
        out.println("<table cellpadding='5' border='0' rows = '1' cols='2' width='85%'>");

        out.println("<tr>");
        out.println("<td align='left'>Merchant ID</td>");
        out.println("<td align='left'>" + merchantID + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Session ID</td>");
        out.println("<td align='left'>" + sessionID + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Amount</td>");
        out.println("<td align='left'>" + amount + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Locale</td>");
        out.println("<td align='left'>" + locale + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Receipt Number</td>");
        out.println("<td align='left'>" + receiptNumber + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td><hr></td><td><hr></td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>TransactionNo</td>");
        out.println("<td align='left'>" + transactionNumber + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Acquirer Response Code</td>");
        out.println("<td align='left'>" + acquirerResponseCode + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>QSI Response Code</td>");
        out.println("<td align='left'>" + qsiRespCode + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td align='left'>Response Description</td>");
        out.println("<td align='left'>" + qsiRespCodeDesc + "</td>");
        out.println("</tr>");

        out.println("<TR><TD></TD>");
        out.println("<TD><BR><BR><BR><A HREF='/" + againLink + "'>" +
                againLinkText + "</A></TD></TR>");

        out.println("</table>");
        out.println("</center>");

        // close the open tags
        out.println("</BODY>");
        out.println("</HTML>");
    }

    /**
     * Based on the supplied reponse code, return a description.
     *
     * @param responseCode the response code numeric identifier as a String object
     * @ return String
     * a description for the supplied numeric response code id
     */
    private String getResponseCodeDescription(String responseCode)
    {

        String retVal = "";

        char code;
        code = responseCode.charAt(0);

        switch (code)
        {
            case '0':
                retVal = "Successful Transaction";
                break;
            case '1':
                retVal = "An unknown error occurred";
                break;
            case '2':
                retVal = "Bank declined transaction (Contact Bank)";
                break;
            case '3':
                retVal = "No reply from Bank";
                break;
            case '4':
                retVal = "Expired Card";
                break;
            case '5':
                retVal = "Insufficient funds";
                break;
            case '6':
                retVal = "Error communicating with Bank";
                break;
            case '7':
                retVal = "Payment Server system error";
                break;
            case '8':
                retVal = "Transaction type is not supported";
                break;
            case '9':
                retVal = "Bank declined transaction (Do not contact Bank)";
                break;
            case 'D':
                retVal = "Deferred transaction has been received and is awaiting processing";
                break;
            case 'P':
                retVal = "Transaction has been recieved by the PaymentAdaptor and is being processed";
                break;
            case 'R':
                retVal = "Transaction was not processed - Reached limit of retry attempts allowed";
                break;
            default:
                retVal = "Presently unhandled return code";
                break;
        }
        return retVal;
    }


}