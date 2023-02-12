<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 6/30/2017
  Time: 1:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%!
  Logger logger = new Logger("addsettlementcycle.jsp");
  Functions functions = new Functions();
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<html>
<head>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({});
  </script>
  <script>
    $(function ()
    {
      $(".datepicker").datepicker({dateFormat: 'yy-mm-dd'});
    });
    function getDynamicCharges(ctoken)
    {
      var partnerId = document.getElementById("partnerid").value;
      document.f1.action = "/icici/servlet/WLPartnerInvoiceDynamicCharges?ctoken=" + ctoken + "&partnerid=" + partnerId;
      document.f1.submit();
    }
    function confirmsubmitreg()
    {
      var r = window.confirm("Are You Sure To Generate wire?");
      if (r == true)
      {
        document.getElementById("generatewire").submit();
      }
      else
      {
        return false;
      }
    }
  </script>
  <title>Bank Description Manager> Settlement Cycle Master</title>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    //Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();
    String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        White Label Invoice Manager
        <div style="float: right;">
          <form action="/icici/wlPartnerInvoiceList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Invoice List
            </button>
          </form>
        </div>
      </div>
      <form name="f1" action="/icici/servlet/WhitelableInvoice?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table align="center" width="65%">
          <tbody>
          <tr>
            <td>
              <table width="75%" align="center">
                <tbody>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Bank Account*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input name="accountid" id="accountid" value="<%=accountid%>" class="txtbox" autocomplete="on">
                   <%-- <select id="accountid" name="accountid" class="txtbox">
                      <option value="" ></option>
                      <%
                        Enumeration enumeration = accountDetails.keys();
                        Integer key =null;
                        GatewayAccount gatewayAccount = null;
                        while (enumeration.hasMoreElements())
                        {
                          String selected = "";
                          key = (Integer) enumeration.nextElement();
                          gatewayAccount = (GatewayAccount)accountDetails.get(key);
                          int acId = gatewayAccount.getAccountId();
                          String currency = gatewayAccount.getCurrency();
                          String mid = gatewayAccount.getMerchantId();
                          /*if(String.valueOf(gatewayAccount.getAccountId()).equals(accountId))
                          {
                            selected3="selected";
                          }*/
                      %>
                      <option value="<%=gatewayAccount.getAccountId()%>" <%=selected%>><%=acId+"-"+currency+"-"+mid%></option>
                      <%
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Start Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text" readonly class="datepicker"
                                                       name="firstdate" style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb"><input maxlength="10"
                                                                    type="text"
                                                                    class="txtbox"
                                                                    name="settledstarttime"
                                                                    value="00:00:00"
                                                                    placeholder="(HH:MM:SS)">
                  </td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">End Date*</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="textb"><input type="text" readonly class="datepicker"
                                                       name="lastdate" style="width: 48%;"></td>
                  <td colspan="2" align="left" class="textb" width="43%"><input
                          maxlength="10" type="text" class="txtbox" name="settledendtime"
                          value="23:59:59"
                          placeholder="(HH:MM:SS)"></td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Next Cycle Days:</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td colspan="2" class="txtbox"><input class="txtbox" type="text" name="next_cycle_days" style="width: 48%;" placeholder=""></td>
                  <td colspan="2" align="left" class="txtbox" width="43%"></td>
                </tr>
                <tr>
                  <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                  <td colspan="7" style="padding: 3px" width="50%" class="textb" align="center">
                    <button type="submit" class="buttonform" value="Add" style="width:150px ">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Create
                    </button>
                  </td>
                </tr>
              </table>
          </tbody>
        </table>
        </tbody>
      </form>
    </div>
  </div>
</div>
<%
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>
