<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.CommissionVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 8/8/16
  Time: 8:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

  <script type="text/javascript">
    $('#sandbox-container input').datepicker({

    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker();
      //$("#yourinput").datepicker( "setDate" , "7/11/2005" );
    });

    function getDynamicCharges(data,ctoken)
    {
      var accountId=data.value;
      document.f1.action="/icici/servlet/GetDynamicInputCharges?ctoken="+ctoken+"&accountid="+accountId;
      document.f1.submit();
    }
  </script>
  <title></title>
</head>
<body>
<%!
  private static Logger logger=new Logger("addnewisocommwire.jsp");
%>
<%

  Functions functions=new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String accountId="";
    accountId=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    List<CommissionVO> commissionVOs=(List)request.getAttribute("commissionVOList");
    Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();
    try
    {
%>n
<div class="row" style="margin-top:0px ">
  <div class="col-lg-12" style="padding-top: 80px">
    <div class="panel panel-default" style="margin-top:5px">
      <div class="panel-heading" >
        ISO Commission Report Master
        <div style="float: right;">
          <form action="/icici/isocommwiremanagerlist.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Wire Master" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Wire Master
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/AddNewISOCommWireList?ctoken=<%=ctoken%>" method="post" name="f1" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table width="50%" align="center" border="0">
          <tbody>
          <tr><td colspan="7" align="center" class="textb"><h4>Add New Wire</h4></td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <tr>
            <td  colspan="3" class="textb">Account ID*:</td>
            <td colspan="4" class="textb">
              <input name="accountid" id="accountid" value="<%=accountId%>" class="txtbox" autocomplete="on">
            <%--<select size="1" id="accountid" name="accountid" class="txtbox" style="width: 360px" onchange="getDynamicCharges(this,'<%=ctoken%>')">
                <option value="">Select AccountID</option>
                <%
                  Enumeration enu3 = accountDetails.keys();
                  Integer key3 =null;
                  GatewayAccount value3 = null;
                  while (enu3.hasMoreElements())
                  {
                    String selected3 = "";
                    key3 = (Integer) enu3.nextElement();
                    value3 = (GatewayAccount)accountDetails.get(key3);
                    int acId = value3.getAccountId();
                    String currency = value3.getCurrency();
                    String mid = value3.getMerchantId();
                    if(String.valueOf(value3.getAccountId()).equals(accountId))
                    {
                      selected3="selected";
                    }
                %>
                <option value="<%=value3.getAccountId()%>" <%=selected3%>><%=acId+"-"+currency+"-"+mid%></option>
                <%
                  }
                %>
              </select>--%>
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <tr>
            <td colspan="3" class="textb">Start Date*:</td>
            <td colspan="2" class="textb">
              <input type="text" readonly class="datepicker" name="firstdate">
            </td>
            <td colspan="2" align="left" class="textb">
              Time*: <input maxlength="10" type="text" class="txtbox" name="settledstarttime"  value="" placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <tr>
            <td colspan="3" class="textb">End Date*:</td>
            <td colspan="2">
              <input type="text" readonly class="datepicker" name="lastdate">
            </td>
            <td colspan="2" class="textb">
              Time*:<input maxlength="10" type="text" class="txtbox" name="settledendtime"  value=""placeholder="(HH:MM:SS)">
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <%
            if(commissionVOs!=null)
            {
              StringBuffer dynamicCommissionIds=new StringBuffer();
              for(CommissionVO commissionVO:commissionVOs)
              {

                String countOrAmountString="";
                if("FlatRate".equalsIgnoreCase(commissionVO.getChargeMasterVO().getValueType()))
                {
                  countOrAmountString="Counter";
                }
                else
                {
                  countOrAmountString=" Amount";
                }
                if(dynamicCommissionIds.length()>0)
                {
                  dynamicCommissionIds.append(",");
                }
                dynamicCommissionIds.append(commissionVO.getChargeId());
          %>
          <tr>
            <td  colspan="2" class="textb"><%=commissionVO.getChargeMasterVO().getChargeName()%> <%=countOrAmountString%>*:</td>
            <td  colspan="1" class="textb" align="center">

            </td>
            <td colspan="3" class="textb">
              <input maxlength="10" type="text" class="txtbox" name="dynamicchargevalue_<%=commissionVO.getChargeId()%>"  value="" style="width: 220px">
              <input maxlength="10" type="hidden" class="txtbox" name="dynamicchargename_<%=commissionVO.getChargeId()%>"  value="<%=commissionVO.getChargeMasterVO().getChargeName()%>" style="width: 220px">
              <input maxlength="10" type="hidden" class="txtbox" name="dynamicchargetype_<%=commissionVO.getChargeId()%>"  value="<%=commissionVO.getChargeMasterVO().getValueType()%>" style="width: 220px"></td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          <%
            }
          %>
          <input type="hidden" name="dynamiccommissionids" value="<%=dynamicCommissionIds.toString()%>">
          <%
            }
          %>
          <tr>
            <td align="center" colspan="6" class="textb">
              <button type="submit" class="buttonform" value="Save">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Save
              </button>
            </td>
          </tr>
          <tr><td colspan="6">&nbsp;</td></tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
<%
    }
    catch (Exception e)
    {
      logger.error("Jsp Page Exception ::", e);
    }
  }
  if(functions.isValueNull((String) request.getAttribute("statusMsg")))
  {
    out.println("<div class=\"reporttable\">");
    out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
    out.println("</div>");
  }
%>
</body>
</html>