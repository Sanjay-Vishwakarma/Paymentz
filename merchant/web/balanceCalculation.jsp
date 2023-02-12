<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ include file="Top.jsp" %>
<%@include file="payoutPercentage.jsp"%>
<%--<%String company = (String)session.getAttribute("company");--%>
  <%--session.setAttribute("submit","Payout");--%>
<%--%>--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Balance Calculation</title>
</head>
<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
  <div class="content">
    <div class="content">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent" style="width:100% ;">
              <div style="float: left;" width="50%">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Balance Calculation </strong></h2>
              </div>
              <div class="pull-right"style="padding: 10px;">
                <div class="btn-group">
                  <form action="/merchant/payoutlist.jsp?ctoken=<%=ctoken%>" method="POST" style="margin:0;">
                    <button type="hidden" name="submit" <%--value="back"--%> class="btn btn-default" style="display: -webkit-box; margin-right: 0px;">&nbsp;&nbsp;Back</button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
        <table border="1" style="height: 10%" class="display table table-striped table-bordered" >
          <thead>
          <tr>
          <th  colspan="4" style="text-align:center">Balance as of 07.04.2022</th>
          </tr>
          </thead>
          <tr>
            <th >&nbsp;</th>
            <th >Count</th>
            <th >Amount</th>
            <th >Total</th>
          </tr>
          <tbody>
          <tr class="table-active">
          <th  bgcolor="#7ECCAD" style="color: white">Deposits</th>
          <th  bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          <th  bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          <th  bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          <tr>
            <td>Total Volumn</td>
            <td>&nbsp;</td>
            <td>103,538,041.20</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>Fees</td>
            <td>&nbsp;</td>
            <td>13,033,306.94</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>MDR (6.00%)</td>
            <td>&nbsp;</td>
            <td>6,212,282.47</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>GST (6%)</td>
            <td>&nbsp;</td>
            <td>6,212,282.47</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>Approved Trxns (18 INR)</td>
            <td>24,194</td>
            <td>435,492.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>Declined (18 INR)</td>
            <td>9,625</td>
            <td>173,250.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr class="table-active">
            <th bgcolor="#7ECCAD" style="color: white">Refund</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          <tr>
            <td>Refund Fee(400)</td>
            <td>&nbsp;</td>
            <td>0.00</td>
            <td>&nbsp;</td>
          </tr>

          <tr class="table-active">
            <th bgcolor="#7ECCAD" style="color: white">Chargeback</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">909,000.00</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          <tr>
            <td>Chargeback Fee (2000)</td>
            <td>39.00</td>
            <td>78,000.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>AMC (35000)</td>
            <td>11.00</td>
            <td>385,000.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>Set up (200000)</td>
            <td>1.00</td>
            <td>200,000.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr class="table-active">
            <th bgcolor="#7ECCAD" style="color: white">Payout Transactions</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          <tr>
            <td>Total Volumn</td>
            <td>&nbsp;</td>
            <td>14,235,227.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>Payout fees (3.50%)</td>
            <td>&nbsp;</td>
            <td>498,232.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr class="bg-warning">
            <th bgcolor="#7ECCAD" style="color: white">Settlement effected</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          <tr>
            <td>Amount settled</td>
            <td>&nbsp;</td>
            <td>72,198,478.00</td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <th bgcolor="#7ECCAD" style="color: white">Total (Current balance)</th>
            <th bgcolor="#7ECCAD" style="color: white">INR</th>
            <th bgcolor="#7ECCAD" style="color: white">2,000,795.00</th>
            <th bgcolor="#7ECCAD" style="color: white">&nbsp;</th>
          </tr>
          </tbody>
        </table>
    </div>
  </div>
</body>
</html>
