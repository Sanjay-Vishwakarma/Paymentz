<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.enums.PZTransactionCurrency" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jitendra
  Date: 27/02/18
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Exchange Rates</title>
</head>
<body>
<%!
  Logger logger=new Logger("manageChargeMaster");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String message=(String)request.getAttribute("msg");
    Functions functions=new Functions();
%>

<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Create New Mapping
        <div style="float: right;">
          <form action="/icici/servlet/ListExchangeRates?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Charge Master" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Currency Exchange Master
            </button>
          </form>
        </div>
      </div>
      <br>
      <form action="/icici/servlet/addNewExchangeRate?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <%
                if (functions.isValueNull(message)){
                  out.print("<tr><td class='textb'>");
                  out.print("<center>"+message+"</center>");
                  out.print("</tr></td>");
                }
              %>
            </td>
          </tr>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >From Currency*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <select name="fromcurrency" class="txtbox">
                      <option value="" selected>&nbsp;&nbsp;&nbsp;&nbsp;Select Currency</option>
                      <%
                        for (PZTransactionCurrency transactionCurrency: PZTransactionCurrency.values()){
                          out.println("<option value=\""+transactionCurrency.name()+"\">"+transactionCurrency.name()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >To Currency*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <select name="tocurrency" class="txtbox">
                      <option value="" selected>&nbsp;&nbsp;&nbsp;&nbsp;Select Currency</option>
                      <%
                        for (PZTransactionCurrency transactionCurrency: PZTransactionCurrency.values()){
                          out.println("<option value=\""+transactionCurrency.name()+"\">"+transactionCurrency.name()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Exchange Rate*</td>
                  <td class="textb">:</td>
                  <td>
                    <input maxlength="255" type="text" name="exchangerate" class="txtbox" value=""placeholder="0.00000">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
                </tr>

                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Save">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Save
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb"></td>
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
  }else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</div>
</body>
</html>