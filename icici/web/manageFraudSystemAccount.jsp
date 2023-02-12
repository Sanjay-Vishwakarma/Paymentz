<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title> Add New Fraud System Account</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                FraudSystem Accounts
                <div style="float: right;">
                    <form action="/icici/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="" name="submit" class="addnewmember" style="width: 250px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Account List
                        </button>
                    </form>
                </div>>
                <div style="float: right;">
                    <form action="/icici/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="" name="submit" class="addnewmember" style="width: 200px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Allocate Account
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/ManageFraudSystemAccount?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <table align="center" width="65%" cellpadding="2" cellspacing="2">
                    <tbody>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tbody>
                                <tr><td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb">Fraud System*</td>
                                    <td style="padding: 3px" width="5%" class="textb">:</td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <select name="fsid" class="txtbox" style="width: 200px;">
                                            <option value="">All</option>
                                            <%
                                                Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                                                Iterator it1 = fraudSystem.entrySet().iterator();
                                                while (it1.hasNext())
                                                {
                                                    Map.Entry pair = (Map.Entry)it1.next();
                                                    out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Fraud System MerchantId/Account Name*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="255" type="text" name="accountName"></td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">User Name</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="225" type="text" name="userName"></td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Password</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="50" type="text" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                                                    name="pwd"></td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Person Name*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="100" type="text" name="contactname"></td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">Contact Email*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px"><input class="txtbox" maxlength="255" type="text" name="contactemail"></td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" class="textb">isTest*</td>
                                    <td style="padding: 3px" class="textb">:</td>
                                    <td style="padding: 3px">
                                        <select name="isTest" size="1" class="txtbox" style="width: 70px;">
                                            <option value="Y">Y</option>
                                            <option value="N">N</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr><td>&nbsp;</td></tr>
                                <tr>
                                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                                    <td style="padding: 3px" width="43%" class="textb"></td>
                                    <td style="padding: 3px" width="5%" class="textb"></td>
                                    <td style="padding: 3px" width="50%" class="textb">
                                        <button type="submit" class="buttonform">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;Save
                                        </button>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
<%
        String message = (String)request.getAttribute("statusMsg");
        Functions functions=new Functions();
        if(functions.isValueNull(message)){
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Result",message));
            out.println("</div>");
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>