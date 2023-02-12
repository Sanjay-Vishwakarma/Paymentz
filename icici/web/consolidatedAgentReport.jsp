<%@ page import="java.util.List" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 10/14/2020
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="index.jsp"%>
<html>
<head>
  <title></title>
</head>
<body>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: 'dd/mm/yy'});
  });
</script>
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
  function isNumberKey(evt)
  {
    console.log("in is number key ----",evt)
    var charCode = (evt.which) ? evt.which : evt.keyCode

    if ($.inArray(charCode, [46, 8, 9, 27, 13, 110, 190, 118]) !== -1 ||
              // Allow: Ctrl+A,Ctrl+C,Ctrl+V, Command+A
            ((charCode == 65 || charCode == 86 || charCode == 67) && (evt.ctrlKey === true || evt.metaKey === true)) ||
              // Allow: home, end, left, right, down, up
            (charCode >= 35 && charCode <= 40))
    {
      // let it happen, don't do anything
      return;
    }

    if (charCode > 31 && (charCode < 48 || charCode > 57))
    {
      evt.preventDefault();
    }

    onPasteNumCheck(evt)
    return true;
  }

  function emptycheck(currency){
    if (currency.value=="")
    {
      alert("Conversion Rate should not be empty.");
      currency.focus();
      currency.select();
    }
    else{
      var a = currency.value;
      currency.value = parseFloat(a).toFixed(4);
    }
  }

  function onPasteNumCheck(evt)
  {
    console.log("event ---",evt.target)
    var regex = new RegExp('^[0-9\.]+$');
    if (regex.test(document.getElementById(evt.target.id).value))
    {
      return true;
    }
    else
    {
      document.getElementById(evt.target.id).value = "";
      return false;
    }
  }

</script>
<script type="text/javascript">
  function getPreviousWireDetails(ctoken)
  {
    console.log("inside")
    var agentId = document.getElementById("agnt").value;
    document.f1.action = "/icici/servlet/ConsolidatedAgentPayoutReport?ctoken=" + ctoken + "&action=getinfo";
    document.getElementById("submit_button").click();

  }
</script>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Consolidated Agent Payout Report
        <div style="float: right;margin-right:5px;">
          <form action="/icici/agentPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Payout Report">
          </form>
        </div>
      </div>
      <br>
      <%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
          Functions functions=new Functions();
          String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
          String startDate=Functions.checkStringNull((String)request.getAttribute("startDate"))==null?"": (String) request.getAttribute("startDate");
          String endDate=Functions.checkStringNull(request.getParameter("endDate"))==null?"":request.getParameter("endDate");
          String startTime=Functions.checkStringNull((String)request.getAttribute("starttime"))==null?"00:00:00": (String) request.getAttribute("starttime");
          String endTime=Functions.checkStringNull(request.getParameter("endtime"))==null?"23:59:59":request.getParameter("endtime");
          List<String> stringList=(List)request.getAttribute("stringList");
          List<String> result=(List)request.getAttribute("result");
          Set<String> processingCurrencySet = (Set) request.getAttribute("processingCurrencySet");
          session.setAttribute("processingCurrencySet", processingCurrencySet);
          System.out.println("StartDate==="+startDate+"---startTime---"+startTime);
      %>
      <form action="/icici/servlet/ConsolidatedAgentPayoutReport?ctoken=<%=ctoken%>" method="post" name="f1">
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
                  <td width="11%" class="textb">Agent Id*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on" onblur="return getPreviousWireDetails('<%=ctoken%>')">
                  </td>
                </tr>
                </tr>

                <tr><td colspan="4">&nbsp;</td>

                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">Start Date*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input type="text" class="datepicker" readonly name="startDate" value="<%=startDate%>">
                  </td>

                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" name="starttime" value="<%=startTime%>" placeholder="00:00:00" class="txtbox">
                  </td>
                </tr>
                </tr>

                <tr><td colspan="4">&nbsp;</td>

                <tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb">End Date*</td>
                  <td width="3%" class="textb">:</td>
                  <td width="12%" class="textb">
                    <input type="text" class="datepicker" readonly name="endDate" value="<%=endDate%>">
                  </td>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input type="text" name="endtime" value="<%=endTime%>" placeholder="23:59:59" class="txtbox">
                  </td>
                </tr>
                </tr>

                <tr><td>&nbsp;</td></tr>
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" name="action" value="next" class="buttonform" id="submit_button">
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

      <form name="TerminalDetails" action="/icici/servlet/ConsolidatedAgentPayoutReport?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" name="agentid" value="<%=agentId%>">
        <input type="hidden" name="startDate" value="<%=startDate%>">
        <input type="hidden" name="endDate" value="<%=endDate%>">
        <input type="hidden" name="starttime" value="<%=startTime%>">
        <input type="hidden" name="endtime" value="<%=endTime%>">
        <%
          List<TerminalVO> terminalList=(List)request.getAttribute("terminalList");
          //List<TerminalVO> filterList=(List)request.getAttribute("filterList");
          List<TerminalVO> settledList=(List)request.getAttribute("settledList");
          if(terminalList!=null && terminalList.size()>0 )
          {
        %>
        <table align=center style="width:80%" border="1" class="table table-striped table-bordered table-hover table-green dataTable">
          <thead>
          <tr>
            <td valign="middle" width="10%" align="center" class="th0"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
            <td width="5%" valign="middle" align="center" class="th0">Agent Id</td>
            <td width="15%" valign="middle" align="center" class="th0">Terminal Id</td>
            <td width="15%" valign="middle" align="center" class="th0">Member Id</td>
            <td width="15%" valign="middle" align="center" class="th0">Account Id</td>
            <td width="15%" valign="middle" align="center" class="th0">Bank Wire Id</td>
            <td width="35%" valign="middle" align="center" class="th0">Date Range</td>
          </tr>
          </thead>
          <%
            for(TerminalVO terminalVO:terminalList)
            {
              String disable="";
              if(settledList!=null)
              {
                for (TerminalVO terminalVO1 : settledList)
                {
                  String terminalAgentid=terminalVO1.getTerminalId()+"_"+terminalVO1.getAgentId()+"_"+terminalVO1.getWireId();
                  if (terminalAgentid.equals(terminalVO.getTerminalId()+"_"+terminalVO.getAgentId()+"_"+terminalVO.getWireId()))
                  {
                    disable="disabled";
                    break;
                  }
                }
              }
          %>
          <tr>
            <td align="center" class="tr0"><input type="checkbox" name="terminalId" value="<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" <%=disable%>></td>
            <td align="center" class="tr0"><%=terminalVO.getAgentId()%><input type="hidden" name="agentid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getAgentId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getTerminalId()%><input type="hidden" name="terminalid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getTerminalId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getMemberId()%><input type="hidden" name="memberid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getMemberId()%>"></td>
            <td align="center" class="tr0"><%=terminalVO.getAccountId()%><input type="hidden" name="accountid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getAccountId()%>" ></td>
            <td align="center" class="tr0"><%=terminalVO.getWireId()%><input type="hidden" name="wireid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getWireId()%>" ></td>
            <td align="center" class="tr0"><%=terminalVO.getStartDate()%> - <%=terminalVO.getEndDate()%><input type="hidden" name="wireid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getStartDate()%>" ></td>
            <input type="hidden" name="startdt_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getStartDate()%>" ></td>
            <input type="hidden" name="enddt_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getEndDate()%>" ></td>
            <input type="hidden" name="paymodeid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getPaymodeId()%>" ></td>
            <input type="hidden" name="cardtypeid_<%=terminalVO.getTerminalId()%>_<%=terminalVO.getWireId()%>" value="<%=terminalVO.getCardTypeId()%>" ></td>
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
        if(stringList!=null && stringList.size()>0){
      %>
      <form name="TerminalDetails" action="/icici/servlet/ConsolidatedAgentPayoutReport?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" name="agentid" value="<%=agentId%>">
        <input type="hidden" name="startDate" value="<%=startDate%>">
        <input type="hidden" name="endDate" value="<%=endDate%>">
        <input type="hidden" name="starttime" value="<%=startTime%>">
        <input type="hidden" name="endtime" value="<%=endTime%>">
        <input type="hidden" name="processingCurrencySet" value="<%=processingCurrencySet%>">
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
            TreeMap<String , String> Terminalid1 = new TreeMap<String , String>();
            String cycleid1 = "";
            for(String s:stringList)
            {
              String responseArr[]=s.split(":");
              if(Terminalid1.containsKey(responseArr[4] + ":" + responseArr[6])){
                cycleid1 = Terminalid1.get(responseArr[4] + ":" + responseArr[6])+","+ responseArr[0];
          %>
          <script type="text/javascript">
            element = document.getElementById(<%=responseArr[4]%>);
            element.style.display="none";
          </script>
          <%
            }else{
              cycleid1 = responseArr[0];
            }
            Terminalid1.put(responseArr[4] + ":" + responseArr[6],cycleid1);
          %>
          <tr id="<%=responseArr[4]%>">
            <td align="center" class="tr0"><%=cycleid1%></td>
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
          String disabled="";
          if(stringList.toString().contains("Failed"))
          {
            disabled="disabled";
          }
          System.out.println("disabled--"+disabled);

        %>

        <div style="text-align: center">
          <%
            for(String currency:processingCurrencySet)
            {
              if(!"EUR".equals(currency))
              {
                String settlementCurrency="EUR";
          %>
          <tr>
            <td class="textb">&nbsp;</td>
            <td class="textb"><%=currency%> to EUR &nbsp;Conversion Rate:
              *
            </td>
            <td class="textb">:</td>
            <td colspan="4" class="textb">
              <input maxlength="10" type="text" class="txtbox" id="<%=currency%>_<%=settlementCurrency%>_conversion_rate" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" onblur="emptycheck(this)"
                     name="<%=currency%>_<%=settlementCurrency%>_conversion_rate" value=""
                     style="width:6%;" autofocus="autofocus">
            </td>
          </tr>
          <tr>
            <td colspan="7">&nbsp;</td>
          </tr>
          <%
              }
            }
          %>
          <%--Conersion Rate: <input type="text"  name="conversionRate" <%=disabled%> style="display: inline-block;  width: 100px;height: 29px">--%>
          <button type="submit"  name="action" value="proceed" class="buttonform" <%=disabled%> style="display: inline-block;">
            <i class="fa fa-sign-in"></i>
            &nbsp;&nbsp;Proceed
          </button>
        </div>
      </form>
      <%
        }
        if(result!=null)
        {
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
          TreeMap<String , String> Terminalid = new TreeMap<String , String>();
          String cycleid = "";
          for(String s:result)
          {
            String responseArr[]=s.split(":");
            if(Terminalid.containsKey(responseArr[4] + ":" + responseArr[6])){
              cycleid = Terminalid.get(responseArr[4] + ":" + responseArr[6])+","+ responseArr[0];
        %>
              <script type="text/javascript">
                element = document.getElementById(<%=responseArr[4]%>);
                element.style.display="none";
              </script>
        <%
            }else{
              cycleid = responseArr[0];
            }
            Terminalid.put(responseArr[4] + ":" + responseArr[6],cycleid);
        %>
        <tr id="<%=responseArr[4]%>">
          <td align="center" class="tr0"><%=cycleid%></td>
          <td align="center" class="tr1"><%=responseArr[1]%></td>
          <td align="center" class="tr0"><%=responseArr[2]%></td>
          <td align="center" class="tr1"><%=responseArr[3]%></td>
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
      %>
      </br>
      </br>
      <%
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
