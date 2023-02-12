<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.ExchangeRatesVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Jitendra
  Date: 02/02/18
  Time: 12:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
  Functions functions = new Functions();
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Exchange Rates </title>
</head>
<body>
  <%
  ExchangeRatesVO exchangeRatesVO=(ExchangeRatesVO)request.getAttribute("exchangeRatesVO");
  String message=(String)request.getAttribute("msg");
  //System.out.println("message:::::"+message);
  if(exchangeRatesVO!=null)
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Update Currency Exchange Master
        <div style="float: right;">
          <form action="/icici/servlet/ListExchangeRates?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Charge Master" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Currency Exchange Master
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/UpdateCurrencyExchange?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="<%=exchangeRatesVO.getId()%>" name="id">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                    <%
                      if (functions.isValueNull(message)){
                        out.print("<tr><td class='textb' colspan='4'>");
                        out.print("<center>"+message+"</center>");
                        out.print("</td></tr>");
                      }
                    %>
                <tr><td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">From Currency</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input maxlength="50" type="text" name="fromcurrency" class="txtbox" value="<%=exchangeRatesVO.getFromCurrency()%>" readonly style="background-color: rgb(229, 229, 229)">
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
                  <td width="43%" class="textb">To Currency</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input maxlength="255" type="text" name="tocurrency" class="txtbox" value="<%=exchangeRatesVO.getToCurrency()%>" readonly style="background-color: rgb(229, 229, 229)">
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
                  <td width="43%" class="textb">Exchange Rate</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb">
                    <input maxlength="255" type="text" name="exchangerate" class="txtbox" value="<%=functions.round(exchangeRatesVO.getExchangeValue(),5)%>" placeholder="0.00000">
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
                  <td width="50%" class="textb"></td>
                </tr>

                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" name="action" value="update">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Update
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
      }
      else
      {
        out.println("<br><br><br>");
        out.println(Functions.NewShowConfirmation("Sorry","No Records Found"));
      }
    %>
