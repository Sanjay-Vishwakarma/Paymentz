<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.math.BigDecimal" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Withdrawal</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>
<%@ include file="Top.jsp" %>

<%
    application.log("Entering withdrawal.jsp");
    String merchantid = (String) session.getAttribute("merchantid");
     user =  (User)session.getAttribute("ESAPIUserSessionKey");
     if(user==null ||user.getCSRFToken()==null || user.getCSRFToken().equals(""))
        {

            response.sendRedirect("/merchant/Logout.jsp");
        }
         if(!user.getCSRFToken().equals(ctoken))
        {

            response.sendRedirect("/merchant/Logout.jsp");
        }



    if (!session.getAttribute("activation").equals("Y"))
    {
        out.println(Functions.NewShowConfirmation1("Sorry", "You Cannot withdraw money as Your account is in <i>Test Mode</i>"));
        return;
    }

    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
    Hashtable merchantDetails = merchants.getMemberDetails(merchantid);
    BigDecimal reserves = new BigDecimal((String) merchantDetails.get("reserves"));
    String taxPer = (String) merchantDetails.get("taxper");
    String withdrawalCharges = (String) merchantDetails.get("withdrawalcharge");
    String currency = (String) session.getAttribute("currency");
    application.log("Curr" + currency + "reser" + reserves + "taxper" + taxPer + "MID" + merchantid);

    reserves = reserves.multiply(new BigDecimal("0.01"));
    BigDecimal taxPercentage = new BigDecimal(taxPer);
    taxPercentage = taxPercentage.multiply(new BigDecimal("0.01"));
    BigDecimal balance = new BigDecimal(transactionentry.getBalance());


    application.log("Charges:  " + withdrawalCharges);
    BigDecimal calcTax = transactionentry.calculateTax(withdrawalCharges, taxPercentage.toString());
    BigDecimal withdrawbalance = transactionentry.getWithdrawBalance(withdrawalCharges, calcTax.toString());
    boolean nobalance = false;

    if (withdrawbalance.compareTo(new BigDecimal("0")) <= 0)
    {
        withdrawbalance = new BigDecimal("0");
        nobalance = true;
    }

%>
<p class="title"><%=company%> Merchant Withdrawal</p>
<table border="0" width="750" align="center">
    <tr><td>

        <table border="0" width="350">
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td class="subtitle" colspan="3">Balance Information</td>
            </tr>
            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Balance
                </td>
                <td class="textb" align="left">
                    (<%=currency%>)
                </td>
                <td class="text" align="right">
                    <%=balance%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Reserves
                </td>
                <td class="textb" align="left">
                    (<%=currency%>)
                </td>
                <td class="text" align="right">
                    <%=reserves%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Withdrawal Charges
                </td>
                <td class="textb" align="left">
                    (<%=currency%>)
                </td>
                <td class="text" align="right">
                    <%=ESAPI.encoder().encodeForHTML(withdrawalCharges)%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Service Tax @ <%=taxPercentage%>%
                </td>
                <td class="textb" align="left">
                    (<%=currency%>)
                </td>
                <td class="text" align="right">
                    <%=calcTax%>/-
                </td>
            </tr>

            <tr>
                <td class="textb" align="right">
                    Withdrawable Balance
                </td>
                <td class="textb" align="left">
                    (<%=currency%>)
                </td>
                <td class="text" align="right">
                    <%
                        if (nobalance)
                        {
                            out.println("0.0");
                        }
                        else
                            out.println(withdrawbalance);
                    %>/-
                </td>
            </tr>

            <tr>
                <td colspan="3" >&nbsp;</td>
            </tr>
        </table>
    </td>
    </tr>
</table>


<hr color="#007ACC" width="750" align="center" size="1"><br><br>

<p class=info align=center>
    <%
        if (request.getAttribute("withdrawamtmessage") != null)
        {
            out.println(ESAPI.encoder().encodeForHTML((String) request.getAttribute("withdrawamtmessage")));
        }
    %>
</p>

<%
    if (!nobalance)
    {
%>
<form action="procwithdrawal.jsp" name="form1" method="POST">
<table  align="center" width="450" cellpadding="2" cellspacing="2">

        <tr>
            <td>
  <%
        String errormsg= (String)request.getAttribute("error");
        if(errormsg!=null)
        {   /*out.println("<li class=\"alert alert-danger alert-dismissable\" align=\"center\" > ");
            out.println(errormsg);
            out.println("</li> ");*/
            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
        }

        %>
       </td>
       </tr></table>

    <table  align="center" width="450" cellpadding="2" cellspacing="2">

        <tr>
            <td>

                 <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table border="0" bgcolor="#CCE0FF" width="100%" align="center" cellpadding="2" cellspacing="2">
                    <tr bgcolor="#007ACC">
                        <td class="label" colspan="3" valign="center">Withdrawals</td>
                    </tr>
                    <tr>
                        <td class="textb" align="left">&nbsp;&nbsp;Withdrawal Amount (<%=ESAPI.encoder().encodeForHTML(currency)%>) : </td>
                        <td align="left" class="text">:</td>
                        <td class="textBoxes" align="left"><input type="text" name="withdrawamt"></td>

                    </tr>
                    <tr><td colspan="3"   >&nbsp;</td></tr>
                    <tr>
                        <td class="textb" align="left">&nbsp;&nbsp;Description:</td>
                        <td align="left" class="text">:</td>
                        <td class="textBoxes" align="left"><input type="text" maxlength="50"  name="desc"></td>
                    </tr>
                    <tr><td colspan="3" align="center"><input type="Submit" value="Withdraw" name="withdraw"
                                                              class="submit"></td></tr>
                </table>

            </td>
        </tr>
    </table>
</form>
<%
}
else
{
%>
<p class="textb" align=center>Withdrawable Balance is Nil</p>
<%
    }
%>
<br>

<p>&nbsp;&nbsp; </p>
</body>

</html>

