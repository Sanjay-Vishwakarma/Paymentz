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

    <title>Bank Details > QWIPI</title>

    <style>

    .buttonpanel{
        width:136px !important;
    }
    .buttonpanel2{
        width:177px !important;
    }
    </style>

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

                        <td width="1%" height="30" valign="bottom" align="center"><form action="/icici/qwipichecklist.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbutton" value="Inquiry"  name="submit"

                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Inquiry"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    else if(!buttonvalue.equals("Reconcilation cron") && !buttonvalue.equals("Settle Cron") && !buttonvalue.equals("Refund") && !buttonvalue.equals("Details") && !buttonvalue.equals("Chargeback") && !buttonvalue.equals("Manual Settlement") && !buttonvalue.equals("Verify Order & Refund Alert"))
                                    { %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Inquiry
                            </button>
                        </form>
                        </td>
                        <td width="1%" height="30" valign="bottom" align="center">
                            <form action="/icici/servlet/QwipiReconcilationList?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbutton" value="Reconcilation cron"  name="submit"

                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Reconcilation cron"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Reconciliation
                            </button>
                        </form>
                        </td>
                        <td width="1%" height="30" valign="middle" align="center"><form action="/icici/QwipiSettlementCron.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbutton" value="Settle Cron"   name="submit"
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
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/qwipirefundlist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton" value="Refund" name="submit"
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
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/servlet/QwipiDetailList?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton" value="Details" <%--style="width: 100;" --%> name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Details"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Details
                                </button>
                            </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/qwipichargebacklist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton" value="Chargeback"  name="submit"
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
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/qwipiManualSettlement.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"  class="panelbutton buttonpanel" value="Manual Settlement"  name="submit"

                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Manual Settlement"))
                                                {   %>
                                        style="background-color:#2c3e50;color:#ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Manual&nbsp; Settlement
                                </button>
                            </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/qwipiVerifyOrderRefundAlertList.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit"  class="panelbutton buttonpanel2" value="Verify Order & Refund Alert" name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Verify Order & Refund Alert"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Verify Order & Refund Alert
                                </button>
                            </form></td>

                    </tr>
                </table>
            </TD>

        </tr>
    </table>
</div>
</body>
</html>