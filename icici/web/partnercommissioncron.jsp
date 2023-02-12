<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: oct 6, 2015
  Time: 7:29:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@include file="index.jsp"%>
<html>
<head>
  <title></title>
  <script>
    function payoutcronconfirm()
    {
      var r= window.confirm("Are You Sure To Execute Payout Cron?");
      if(r==true)
      {
        document.getElementById("payoutcronfrm").submit();
      }
      else
      {

      }
    }
    function commissioncronconfirm()
    {
      var r= window.confirm("Are You Sure To Execute Partner Commission Cron?");
      if(r==true)
      {
        document.getElementById("payoutcronfrm").submit();
      }
      else
      {

      }
    }
  </script>
</head>
<body>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Partner Commission Cron Summary
        <div style="float: right;">
          <form  id="payoutcronfrm" action="/icici/partnercommissioncron.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="button"  class="buttonform"  value="Payout Cron " onclick="commissioncronconfirm()">
          </form>
        </div>
        <div style="float: right;margin-right:5px;">
          <form action="/icici/partnerWirePayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Payout Report">
          </form>
        </div>
      </div>
      <br>
      <%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
          try
          {
            PayoutManager payoutManager=new PayoutManager();
            List<String> stringList=payoutManager.partnerCommissionCron();
      %>
      <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
          <td width="2%"valign="middle" align="center" class="th0">CycleID</td>
          <td width="8%" valign="middle" align="center" class="th0">MemberID</td>
          <td width="8%" valign="middle" align="center" class="th0">AccountID</td>
          <td width="8%" valign="middle" align="center" class="th0">TerminalID</td>
          <td width="8%" valign="middle" align="center" class="th0">Status</td>
          <td width="8%" valign="middle" align="center" class="th0">Description</td>
        </tr>
        </thead>
        <%
          for(String s:stringList)
          {
            String responseArr[]=s.split(":");
        %>
        <tr>
          <td align="center" class="tr0"><%=responseArr[0]%></td>
          <td align="center" class="tr1"><%=responseArr[1]%></td>
          <td align="center" class="tr0"><%=responseArr[2]%></td>
          <td align="center" class="tr1"><%=responseArr[3]%></td>
          <td align="center" class="tr1"><%=responseArr[4]%></td>
          <td align="center" class="tr1"><%=responseArr[5]%></td>
        </tr>
        <%
          }
        %>
      </table>
      <%
          }
          catch (SystemError systemError)
          {

            systemError.printStackTrace();
            out.println("Internal Error While Processing Request "+systemError);
          }
          catch (SQLException se)
          {
            se.printStackTrace();
            out.println("Internal Error While Processing Request "+se);
          }
          catch (Exception e)
          {
            e.printStackTrace();
            out.println("Internal Error While Processing Request "+e);
          }
        }
        else
        {
          response.sendRedirect("/icici/logout.jsp");
          return;
        }
      %>
    </div>
  </div>
</div>
</body>
</html>