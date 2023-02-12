<%@ page import="com.logicboxes.util.ApplicationProperties,java.math.BigDecimal" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN((form.numrows.value)))
                return false;
            else
                return true;
        }
    </script>
    <link rel="stylesheet" type="text/css" href="style.css"/>
</head>

<body>


<%@ include file="Top.jsp" %>
<p class="title"><%=company%> Merchant Settings</p>
<center><h3>
    <% if (request.getParameter("rs") != null)
    {

        String rs = ESAPI.encoder().encodeForHTML((String) request.getParameter("rs"));
        if (rs.equals("false"))
        {
    %>
    <p class="subtitle"><font color="red"> You are not authorised to make changes to organisation's details without
        permission.</font></p>
    <%
    }
    else if (rs.equals("true"))
    {
    %>
    <p class="subtitle"><font color="blue">Company details have been updated.</font></p>
    <%
            }
        }
    %>
</h3></center>

<p>
    <table border="0" width="100%" align="center">
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td class="subtitle">High Risk Transaction</td>
        </tr>
        <tr>
            <td><span class="text">Your High Risk Transaction Limit has been set to </span>
                <%
                    Hashtable memberDetails = merchants.getMemberDetails((String) session.getAttribute("merchantid"));
                    String currency = (String) session.getAttribute("currency");
                    String aptPrompt = (String) memberDetails.get("aptprompt");
                    BigDecimal apt = new BigDecimal(aptPrompt);
                    apt = apt.multiply(new BigDecimal("0.01"));

                    
                         if(user!=null)
                             {  ctoken =   user.getCSRFToken();
                                }
                %>
                <span class="textb"><%=currency%> <%=apt.toString()%>/-</span>
            </td>
        </tr>
        <tr>
            <td>
                <span class="text">Any transaction that crosses this limit will require a proof of transaction from the customer.</span>
            </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
    <hr color="#007ACC" width="100%" align="center" size="1"><br><br>


    <table bgcolor="#007ACC" align="center" width="50%" cellpadding="2" cellspacing="2">
        <tr>
            <td>
                <table border="0" bgcolor="#CCE0FF" width="100%" align="center" cellpadding="2" cellspacing="0">
                    <tr><td>&nbsp;</td></tr>
                    <tr>
                        <td class="subtitle">&nbsp;&nbsp;Settings<br><br></td>

                    </tr>
                    <tr>
                        <td>
                            &nbsp;<span class="textb">1. </span>
                            <form action="/merchant/servlet/MerchantProfile?ctoken=<%=ctoken%>" ><input type="hidden" value="<%=ctoken%>" name="ctoken"><input type="submit" value="Change Merchant Profile" name="submit" class="buttonadmin"></form>
                            <span class="text">To change your Merchant Profile on <%=company%></span>
                            <hr color="#007ACC" width="100%" align="center" size="1">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;<span class="textb">2. </span>
                            <form action="/merchant/servlet/OrganisationProfile?ctoken=<%=ctoken%>" ><input type="hidden" value="<%=ctoken%>" name="ctoken"><input type="submit" value="Change Organisation Profile" name="submit" class="buttonadmin"></form>
                            <span class="text">To change your Organisation Profile on <%=company%></span>
                            <hr color="#007ACC" width="100%" align="center" size="1">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;<span class="textb">3. </span>
                            <form action="/merchant/servlet/MerchantTemplate?ctoken=<%=ctoken%>" ><input type="hidden" value="<%=ctoken%>" name="ctoken"><input type="submit" value="Customise Transaction Pages" name="submit" class="buttonadmin"></form>
                            <span class="text">This template individualizes the appearance of payment page .</span>
                            <hr color="#007ACC" width="100%" align="center" size="1">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;<span class="textb">4. </span>
                            <form action="/merchant/generatekey.jsp?ctoken=<%=ctoken%>" ><input type="hidden" value="<%=ctoken%>" name="ctoken"><input type="submit" value="Generate Key" name="submit" class="buttonadmin"></form>
                            <span class="text">This will generate a new key which will be required to be replaced in your checkout and redirect pages.</span>
                            <hr color="#007ACC" width="100%" align="center" size="1">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            &nbsp;&nbsp;<span class="textb">5. </span>
                            <form action="/merchant/chngpwd.jsp?ctoken=<%=ctoken%>" ><input type="hidden" value="<%=ctoken%>" name="ctoken"><input type="submit" value="Change Password" name="submit" class="buttonadmin"></form>
                            <span class="text">To change you Merchant Administration module's Administrator Password.</span>
                            <!--<hr color="#007ACC" width="100%" align="center" size="1">-->
                        </td>
                    </tr>

                    <tr><td>&nbsp;</td></tr>
                </table>

            </td>
        </tr>
    </table>

    <br>

</p>

<p>&nbsp;&nbsp; </p>
</body>

</html>
