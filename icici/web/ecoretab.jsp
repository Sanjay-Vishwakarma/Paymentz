<%--
  Created by IntelliJ IDEA.
  User: sagar
  Date: 8/23/14
  Time: 5:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>

<html>
<head>

    <title>Bank Details > ECORE</title>
</head>
<body class="bodybackground">
<%

    String buttonvalue=request.getParameter("submit");

    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
        //System.out.println("buttonvalue::"+buttonvalue);

    }
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

%>
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px;">
    <table align=center border="2px" cellspacing="0" cellpadding="2" width="100%">
        <tr>
            <td>
                <table align="center" border="0" cellspacing="2" cellpadding="0" style="border: 2px solid #2c3e50" width="100%">
                    <tr bgcolor="#34495e">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                        <td width="13%" height="30" valign="bottom" align="center">
                            <form action="/icici/ecorechecklist.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button class="panelbuttonecore" type="submit" value="Inquiry" name="submit"

                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Inquiry"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    else if(!buttonvalue.equals("Manual Reconcilation") && !buttonvalue.equals("Settle Cron") && !buttonvalue.equals("Refund") && !buttonvalue.equals("Chargeback") && !buttonvalue.equals("Manual Settlement"))
                                    { %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Inquiry
                            </button>
                        </form>
                        </td>
                        <td width="13%" height="30" valign="middle" align="center"><form action="/icici/ecorereconcilationlist.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbuttonecore" value="Manual Reconcilation"   name="submit"
                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Manual Reconcilation"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Manual Reconcilation
                            </button>
                        </form></td>
                        <td width="13%" height="30" valign="middle" align="center">
                            <form action="/icici/EcoreSettlementCron.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbuttonecore" value="Settle Cron" name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Settle Cron"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Settle Cron
                                </button>
                            </form></td>
                        <td width="13%" height="30" valign="middle" align="center">
                            <form action="/icici/ecorerefundlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbuttonecore" value="Refund"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Refund"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Refund
                                </button>
                            </form></td>
                        <td width="13%" height="30" valign="middle" align="center">
                            <form action="/icici/ecorechargebacklist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbuttonecore" value="Chargeback"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Chargeback"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Chargeback
                                </button>
                            </form></td>
                        <td width="13%" height="30" valign="middle" align="center">
                            <form action="/icici/ecoreManualSettlement.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbuttonecore" value="Manual Settlement"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Manual Settlement"))
                                                {   %>
                                        style="background-color:#2c3e50;color:#ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Manual Settlement
                                </button>
                            </form>
                        </td>
                    </tr>
                </table>
            </TD>

        </tr>
    </table>
</div>
</body>
</html>