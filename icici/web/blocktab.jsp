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

    <title>Transaction Management> Block Transactions</title>
    <style>
        .panelbutton{
            width:90%;
        }
    </style>

</head>
<body class="bodybackground">
<%

    String buttonvalue=request.getParameter("submit");

    if(buttonvalue==null)
    {
        buttonvalue=(String)session.getAttribute("submit");
    }
    System.out.println("button value----"+buttonvalue);
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

%>
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px;">
    <table align=center border="2px" cellspacing="2" cellpadding="0" width="100%" >
        <tr>
            <td>
                <table align="center" border="0" cellspacing="2" cellpadding="0" style="border: 2px solid #2c3e50" width="100%" >
                    <tr bgcolor="#34495e">
                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                        <td width="1%" height="30" valign="bottom" align="center"><form action="/icici/blockedCardList.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbutton"  value="Block Cards"  name="submit"
                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Block Cards"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    else if(!buttonvalue.equals("Block IPs") && !buttonvalue.equals("Block Emails") && !buttonvalue.equals("Block Countries") && !buttonvalue.equals("Block Names") && !buttonvalue.equals("Block Bin") && !buttonvalue.equals("Block VPA Address")&& !buttonvalue.equals("Block Phone") && !buttonvalue.equals("Block Bank Account"))
                                    { %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Block Cards
                            </button>
                        </form>
                        </td>
                        <td width="1%" height="30" valign="bottom" align="center">
                            <form action="/icici/blacklistIp.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block IPs"  name="submit"

                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block IPs"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block IP
                                </button>
                            </form>
                        </td>
                        <td width="1%" height="30" valign="middle" align="center"><form action="/icici/blacklistEmail.jsp?ctoken=<%=ctoken%>" method="POST">
                            <button type="submit" class="panelbutton"  value="Block Emails"   name="submit"
                                    <%
                                        if(buttonvalue!=null)
                                        {
                                            if(buttonvalue.equals("Block Emails"))
                                            {   %>
                                    style="background-color:#2c3e50;color: #ffffff;"
                                    <%}
                                    }
                                    %>>
                                Block Emails
                            </button>
                        </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/blacklistCountry.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block Countries" name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block Countries"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block Country
                                </button>
                            </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/blacklistName.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block Names" name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block Names"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block Names
                                </button>
                            </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/binBlacklist.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block Bin"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block Bin"))
                                                {   %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block Bin
                                </button>
                            </form></td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/blacklistVpaAddress.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block VPA Address"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block VPA Address"))
                                                {
                                        %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block VPA Address
                                </button>
                            </form>
                        </td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/blockphone.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block Phone"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block Phone"))
                                                {
                                        %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block Phone
                                </button>
                            </form>
                        </td>
                        <td width="1%" height="30" valign="middle" align="center">
                            <form action="/icici/blockBankAccountNo.jsp?ctoken=<%=ctoken%>" method="POST">
                                <button type="submit" class="panelbutton"  value="Block Bank Account"  name="submit"
                                        <%
                                            if(buttonvalue!=null)
                                            {
                                                if(buttonvalue.equals("Block Bank Account"))
                                                {
                                        %>
                                        style="background-color:#2c3e50;color: #ffffff;"
                                        <%}
                                        }
                                        %>>
                                    Block Bank Account No
                                </button>
                            </form>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</div>
</body>
</html>