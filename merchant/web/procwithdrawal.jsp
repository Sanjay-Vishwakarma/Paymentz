<%@ page errorPage="error.jsp"
         import="com.directi.pg.Functions,com.directi.pg.Member,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,javax.servlet.RequestDispatcher,java.math.BigDecimal,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Hashtable" %>
<%@ include file="ietest.jsp" %>
<%String company = (String)session.getAttribute("company");%>


<%
    Merchants merchants = new Merchants();
    String errormsg="";
    String desc=null;
    String amt=null;
    String EOL = "<BR>";
    try
    {
       desc =ESAPI.validator().getValidInput("desc",request.getParameter("desc"),"SafeString",50,false);
       amt =ESAPI.validator().getValidInput("withdrawamt",request.getParameter("withdrawamt"),"Numbers",50,false);
    }
    catch(ValidationException e)

    {
       errormsg=errormsg +"Enter Valid Description & Amount. OR should not be Empty "+EOL;
        request.setAttribute("error",errormsg);
        RequestDispatcher rd = request.getRequestDispatcher("/withdrawal.jsp");
        rd.forward(request, response);
        return;
    }


    /*if (desc == null || amt == null)
    {
        out.println(Functions.NewShowConfirmation("Information", "Description or Amount  not Passed"));
        return;
    }


    if (!Functions.isValidate(desc))
    {
        out.println(Functions.NewShowConfirmation("Information", "Description is not valid"));
        return;
    }*/


    String merchantid = (String) session.getAttribute("merchantid");
    Hashtable merchantDetailsHash = merchants.getMemberDetails(merchantid);
    String merchantCurrency = (String) session.getAttribute("currency");
    String withdrawalCharges = (String) merchantDetailsHash.get("withdrawalcharge");
    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");


    BigDecimal withdrawamt = new BigDecimal(amt);
    BigDecimal reserves = new BigDecimal((String) merchantDetailsHash.get("reserves"));
    BigDecimal taxPercentage = new BigDecimal((String) merchantDetailsHash.get("taxper"));
    taxPercentage = taxPercentage.multiply(new BigDecimal("0.01"));
    reserves = reserves.multiply(new BigDecimal("0.01"));
    BigDecimal balance = new BigDecimal(transactionentry.getBalance());
    BigDecimal calcTax = transactionentry.calculateTax(withdrawalCharges, taxPercentage.toString());
    BigDecimal withdrawbalance = transactionentry.getWithdrawBalance(withdrawalCharges, calcTax.toString());
    BigDecimal balanceafterwithdrawal = balance.subtract(withdrawamt).subtract(new BigDecimal(withdrawalCharges)).subtract(calcTax);
    if (withdrawamt.compareTo(withdrawbalance) == 1)
    {
        request.setAttribute("withdrawamtmessage", "The Amount is more than the WithDrawable Amount.");
        RequestDispatcher rd = request.getRequestDispatcher("withdrawal.jsp");
        rd.forward(request, response);
    }
    else if (withdrawamt.compareTo(new BigDecimal("0")) <= 0)
    {
        request.setAttribute("withdrawamtmessage", "The Amount cannot be 0.");
        RequestDispatcher rd = request.getRequestDispatcher("withdrawal.jsp");
        rd.forward(request, response);
    }
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Withdrawal</title>
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
		<script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
    <script language="javascript">


		$(document).ready(function() {

                    document.getElementById('submit').disabled =  false;
      				$("#submit").click(function() {

					var encryptedString =  $.jCryption.encrypt($("#password").val(), $("#ctoken").val());
					document.getElementById('password').value =  encryptedString;

                    document.getElementById('isEncrypted').value =  true;
				});

			});

        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function callForgotTransPass(form)
        {
            form.action = "post";
            form.action = "servlet/ForgotTransPwd";
            form.submit();
        }
    </script>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>

<body>
<%@ include file="Top.jsp" %>

<%
      user =  (User)session.getAttribute("ESAPIUserSessionKey");
     if(user==null ||user.getCSRFToken()==null || user.getCSRFToken().equals(""))
        {

            response.sendRedirect("/merchant/Logout.jsp");
        }
         if(!user.getCSRFToken().equals(ctoken))
        {

            response.sendRedirect("/merchant/Logout.jsp");
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
                    (<%=ESAPI.encoder().encodeForHTML(merchantCurrency)%>)
                </td>
                <td class="text" align="right">
                    <%=balance%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Withdrawn Amount
                </td>
                <td class="textb" align="left">
                    (<%=ESAPI.encoder().encodeForHTML(merchantCurrency)%>)
                </td>
                <td class="text" align="right">
                    <%=withdrawamt%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Withdrawal Charges
                </td>
                <td class="textb" align="left">
                    (<%=ESAPI.encoder().encodeForHTML(merchantCurrency)%>)
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
                    (<%=merchantCurrency%>)
                </td>
                <td class="text" align="right">
                    <%=calcTax%>/-
                </td>
            </tr>
            <tr>
                <td class="textb" align="right">
                    Final Balance
                </td>
                <td class="textb" align="left">
                    (<%=merchantCurrency%>)
                </td>
                <td class="text" align="right">
                    <%=balanceafterwithdrawal%>/-
                </td>
            </tr>

            <tr>
                <td colspan="3">&nbsp;</td>
            </tr>
        </table>
    </td>
    </tr>
</table>

<hr color="#007ACC" width="750" align="center" size="1"><br><br>

<form action="servlet/WithDraw?ctoken=<%=ctoken%>" name="form1" method="post">
    <input type="hidden" name="withdrawamt" value="<%=amt%>">
    <input type="hidden" name="desc" value="<%=desc%>">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
    <table  align="center" width="600" cellpadding="2" cellspacing="2">
    <tr>
            <td>

    <%
        String err= (String)request.getAttribute("ermsg");
        if(err!=null)
        {   out.println("<table align=\"center\" ><tr><td><font class=\"text\" > ");
            out.println(err);
            out.println("</font> </td></tr> </table>");
        }

    %></td>

                    </tr> </table>

    <table  align="center" width="600" cellpadding="2" cellspacing="2">
    <tr>
            <td>

                <table border="0" bgcolor="#CCE0FF" width="100%" align="center" cellpadding="2" cellspacing="2">
                    <tr bgcolor="#007ACC">
                        <td class="label" colspan="3" valign=center>&nbsp;&nbsp;Withdrawal Information</td>
                    </tr>
                    <tr>
                        <td class="textb" align="left" nowrap>&nbsp;&nbsp;Cheque drawn in favour of</td>
                        <td align="left" class="text">:</td>
                        <td align="left" class="text"><%=merchants.getCompany(merchantid)%></td>
                    </tr>
                    <tr><td colspan="3">&nbsp;</td></tr>
                    <tr>
                        <td class="textb" align="left" valign="top" nowrap>&nbsp;&nbsp;Mailing Address</td>
                        <td align="left" class="text">:</td>
                        <td class="text" align="left" valign="top">
                            <%=((Member) session.getAttribute("memberObj")).address%>
                        </td>

                    </tr>

                    <tr><td colspan="3">&nbsp;</td></tr>
                    <tr>
                        <td class="textb" align="left">&nbsp;&nbsp;Transaction Password</td>
                        <td align="left" class="text">:</td>
                        <td class="textBoxes" align="left"><input id="password" maxlength="125" value=""  type="password" name="password"></td>
                    </tr>
                    <tr><td colspan="3" align="center"><input id="submit"  disabled="disabled" type="Submit" value="Withdraw" name="withdraw"
                                                              class="submit" onClick="javascript:updateCSRF(document.form1)"></td></tr>
                    <tr><td colspan="3" align="center">&nbsp;<br/><a
                            href="javascript:callForgotTransPass(document.form1)" class="link">Forgot Password?</a></td>
                    </tr>
                </table>

            </td>
        </tr>
    </table>
</form>
<br>

<p>&nbsp;&nbsp; </p>
</body>

</html>
