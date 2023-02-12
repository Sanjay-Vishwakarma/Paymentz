<html>
<head>
    <title>Merchant Payments Confirmation</title>

</head>
<body bgcolor="#FFFFFF" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<br><br>
<br>
<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">

            <img src="https://secure.pz.com/icici/images/logo.jpg" border="0">

        </td>
    </tr>
</table>
<br><br>


<font size=4>
    <center>

    <%

        String status = request.getParameter("status");
        String trackingid= request.getParameter("trackingid");


        if ("Y".equals(status))
        {
            out.println("Thank you for shopping with us. Your credit card has been charged and your transaction is successful. We will be shipping your order to you soon. <BR>");
            out.println("The Unique Transaction ID for your Transaction is : "+trackingid+"<BR>");
            out.println("Kindly Note it Down for further reference");

            //Here you need to put in, the routines for a successful
            //transaction such as sending an email to customer,
            //setting database status, informing logistics etc etc
        }
        else if ("N".equals(status))
        {
            out.println("Thank you for shopping with us. However it seems your credit card transaction failed.");

            //Here you need to put in, the routines for a failed
            //transaction such as sending an email to customer
            //setting database status etc etc
        }
        else if ("P".equals(status))
        {
            out.println("Your Transaction has been classified as a HIGH RISK Transaction by our Credit Card Processor.This requires you to Fax us an Authorisation for this transaction in order to complete the processing. This process is required by our Credit Card Processor to ensure that this transaction is being done by a genuine card-holder. The transaction will NOT be completed (and your card will NOT be charged) if you do not fax required documents.");

            //Here you need to put in, the routines for a HIGH RISK
            //transaction such as sending an email to customer and explaining him a procedure,
            //setting database status etc etc

        }
        else
        {
            out.println("Security Error. Illegal access detected");

            //Here you need to simply ignore this and dont need
            //to perform any operation in this condition
        }



    %>
    </center>
</font>
</body>

</html>
