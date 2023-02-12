<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.BankManager" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.AgentCommissionVO" %>
<%@ page import="java.util.HashMap" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 10/14/2015
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="index.jsp"%>
<html>
<head>
  <title></title>
  <script>
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("terminalId");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function Submit()
    {
      var checkboxes = document.getElementsByName("terminalId");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one record");
        return false;
      }
      if (confirm("Do you really want to Generate Agent Settlement Report."))
      {
        document.TerminalDetails.submit();
      }
    }
  </script>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Agent Commission Cron Summary
        <div style="float: right;margin-right:5px;">
          <form action="/icici/agentPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Payout Report">
          </form>
        </div>
        <div style="float: right;margin-right:5px;">
          <form action="/icici/consolidatedAgentReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform" style="width: auto"  value="Consolidated Agent Payout">
          </form>
        </div>
      </div>
      <br>
      <%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
          String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
          String memberId=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
          String accountId=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
          String bankWireId=Functions.checkStringNull(request.getParameter("bankwireid"))==null?"":request.getParameter("bankwireid");
          List<String> stringList=(List)request.getAttribute("result");
          if(stringList!=null){
            if(stringList.size()>0)
              bankWireId="";
          }
      %>
      <form action="/icici/servlet/AgentPayoutReport?ctoken=<%=ctoken%>" method="post" name="f1">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr><td align="center" colspan="4" class="textb">
                  <%
                    String message=(String)request.getAttribute("statusMsg");
                    Functions functions=new Functions();
                    if(functions.isValueNull(message) )
                    {
                      out.println(message);
                    }
                  %>
                </td>
                </tr>

                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">Agent Id</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on">
                  </td>
                </tr>
                </tr>

                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">MemberId</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="agnt-mid" value="<%=memberId%>" class="txtbox" autocomplete="on" >
                  </td>
                </tr>
                </tr>

                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">Account ID</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="accountid" id="agnt-accid" value="<%=accountId%>" class="txtbox" autocomplete="on" >
                  </td>
                </tr>
                </tr>

                <tr><td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">BankWire Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="bankwireid" id="bankwireid" value="<%=bankWireId%>" class="txtbox" autocomplete="on" >
                    <%--<select name="bankwireid" id="bankwireid" class="txtbox" style="width: 165px">
                      <option value="" selected></option>
                      <%
                        for (String bankId : bankWiresMap.keySet())
                        {
                          BankWireManagerVO bankWireManagerVO = bankWiresMap.get(bankId);
                          String accountId1 =bankWireManagerVO.getAccountId();
                          String MID = bankWireManagerVO.getMid();
                          String value = bankId + "-" + accountId1 + "-" + MID;
                          String isSelected = "";
                          if (bankId.equalsIgnoreCase(bankWireId))
                            isSelected = "selected";
                      %>
                      <option value="<%=bankId%>" <%=isSelected%>> <%=value%></option>
                      <%
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>

                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" name="action" value="next" class="buttonform">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Next
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

      <form name="TerminalDetails" action="/icici/servlet/AgentPayoutReport?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" name="bankwireid" value="<%=bankWireId%>">
        <input type="hidden" name="memberid" value="<%=memberId%>">
        <input type="hidden" name="agentid" value="<%=agentId%>">
        <%
          List<TerminalVO> terminalList=(List)request.getAttribute("terminalList");
          List<TerminalVO> filterList=(List)request.getAttribute("filterList");
          List<TerminalVO> settledList=(List)request.getAttribute("settledList");
          if(terminalList!=null )
          {
        %>
        <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
          <thead>
          <tr>
            <td valign="middle" width="10%" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
            <td width="20%" valign="middle" align="center" class="th0">Agent Id</td>
            <td width="20%" valign="middle" align="center" class="th0">Terminal Id</td>
            <td width="20%" valign="middle" align="center" class="th0">Member Id</td>
            <td width="20%" valign="middle" align="center" class="th0">Account Id</td>
          </tr>
          </thead>
          <%
            for(TerminalVO terminalVO:filterList)
            {
              String disable="";
              if(settledList!=null)
              {
                for (TerminalVO terminalVO1 : settledList)
                {
                  String terminalAgentid=terminalVO1.getTerminalId()+"_"+terminalVO1.getAgentId();
                  if (terminalAgentid.equals(terminalVO.getTerminalId()+"_"+terminalVO.getAgentId()))
                  {
                    disable="disabled";
                    break;
                  }
                }
              }
          %>
          <tr>
            <td align="center" class="tr0"><input type="checkbox" name="terminalId" value="<%=terminalVO.getTerminalId()%>_<%=terminalVO.getAgentId()%>" <%=disable%>></td>
            <td align="center" class="tr0"><%=terminalVO.getAgentId()%><input type="hidden" name="agentid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getAgentId()%>" value="<%=terminalVO.getAgentId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getTerminalId()%><input type="hidden" name="terminalid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getAgentId()%>" value="<%=terminalVO.getTerminalId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getMemberId()%><input type="hidden" name="memberid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getAgentId()%>" value="<%=terminalVO.getMemberId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getAccountId()%><input type="hidden" name="accountid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getAgentId()%>" value="<%=terminalVO.getAccountId()%>" ></td>
          </tr>
          <%
            }
          %>
        </table>
        <div style="text-align: center">
          <button type="button" class="buttonform" onclick="return Submit();" style="display: inline-block;">
            <i class="fa fa-sign-in"></i>
            &nbsp;&nbsp;Submit
          </button>
        </div>
        <%
          }
        %>
      </form>
      <%
        if(stringList!=null){
      %>
      <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
          <td width="2%"valign="middle" align="center" class="th0">CycleID</td>
          <td width="8%" valign="middle" align="center" class="th0">MemberID</td>
          <td width="8%" valign="middle" align="center" class="th0">AccountID</td>
          <td width="8%" valign="middle" align="center" class="th0">AgentID</td>
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
          <td align="center" class="tr0"><%=responseArr[3]%></td>
          <td align="center" class="tr1"><%=responseArr[4]%></td>
          <td align="center" class="tr1"><%=responseArr[5]%></td>
          <td align="center" class="tr1"><%=responseArr[6]%></td>
        </tr>
        <%
          }
        %>
      </table>
      <%
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