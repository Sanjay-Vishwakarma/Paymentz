<%@ page import="PaymentClient.PaymentClientImpl,
                 PaymentClient.PaymentClientException,
                 java.util.*,
                 java.io.PrintWriter,
                 java.security.Security"%>
<%
    String auth=request.getParameter("auth");
    if (auth!=null)
    {
        try
        {
            String ccNum=request.getParameter("ccnum");
            String expDate=request.getParameter("expdate");
            String description=request.getParameter("description");
            String merchantId="00000486";
            String amount=request.getParameter("amount");

            out.println("Creating client..."+"<br>");
            // Create a Payment Client Object
            PaymentClientImpl paymentClient = new PaymentClientImpl();

            // Add the Extended DigitalOrderFields for MOTO
            paymentClient.addDigitalOrderField("CardNum", ccNum);
            paymentClient.addDigitalOrderField("CardExp", expDate);
            out.println("Authorising...");

            out.flush();

            boolean result=paymentClient.sendMOTODigitalOrder(description, merchantId,
                    Integer.parseInt(amount),"en", "");

           out.print("Authorised...got result "+result);
            paymentClient.nextResult();

                out.println("DigitalReceipt.TransactionNo " + paymentClient.getResultField("DigitalReceipt.TransactionNo")+"<br>");
                out.println("DigitalReceipt.AuthorizeId " + paymentClient.getResultField("DigitalReceipt.AuthorizeId")+"<br>");
                out.println("DigitalReceipt.BatchNo " + paymentClient.getResultField("DigitalReceipt.BatchNo")+"<br>");
                out.println("DigitalReceipt.ReceiptNo " + paymentClient.getResultField("DigitalReceipt.ReceiptNo")+"<br>");
                out.println("DigitalReceipt.DR " + paymentClient.getResultField("DigitalReceipt.DR")+"<br>");
                out.println("DigitalReceipt.ID " + paymentClient.getResultField("DigitalReceipt.ID")+"<br>");
                out.println("DigitalReceipt.AVSResultCode " + paymentClient.getResultField("DigitalReceipt.AVSResultCode")+"<br>");
                out.println("DigitalReceipt.CSCResultCode " + paymentClient.getResultField("DigitalReceipt.CSCResultCode")+"<br><br>");
                out.println("DigitalReceipt.AcqResponseCode " + paymentClient.getResultField("DigitalReceipt.AcqResponseCode")+"<br>");
                out.println("DigitalReceipt.QSIResponseCode" + paymentClient.getResultField("DigitalReceipt.QSIResponseCode")+"<br>");
                out.println("message " + getResponseCodeDescription(paymentClient.getResultField("DigitalReceipt.QSIResponseCode"))+"<br>");

        }
        catch (Throwable e)
        {
            PrintWriter pw =new PrintWriter(out);
            e.printStackTrace(pw);
            out.println("#1234#" + out.toString());
        }


    }
    else
    {
  %>   <form action="testQSI.jsp" method="post">
       <table>
       <tr><td><input type="hidden" name="auth" value="true"></td></tr>
       <tr><td>CCNUM:<input type="text" name="ccnum"></td></tr>
       <tr><td>EXP Date(yy/mm):<input type="text" name="expdate"></td></tr>
       <tr><td>Amount(in paise):<input type="text" name="amount"></td></tr>
       <tr><td>Order Id:<input type="text" name="description" value="<%=(new Date()).getTime()%>"></td></tr>
       <tr><td><input type="submit" name="submit" value="Submit"></td></tr>
       </table>
       </form>
<%
    }
%>
<%!    public String getResponseCodeDescription( String responseCode )
        {

            String retVal = "";

            char code;
            code = responseCode.charAt( 0 );

            switch ( code )
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
                case 'Z':
                    retVal = "Transaction failed (Invalid Card No)";
                    break;
                default:
                    retVal = "Presently unhandled return code";
                    break;
            }

            return retVal;
        }


%>