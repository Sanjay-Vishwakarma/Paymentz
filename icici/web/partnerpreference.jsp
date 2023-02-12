<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.PartnerModuleManager" %>
<%@ include file="index.jsp" %>

<%!
  private static Logger log=new Logger("partnerpreference.jsp");
%>

<html>
<head>
</head>
<title>Partners Management> Partner Default Configuration </title>
<script>
  var lablevalues = new Array();
  function ChangeFunction(Value , lable){
    console.log("Value" + Value + "lable" + lable);
    var finalvalue=lable+"="+Value;
    console.log("finalvalue" + finalvalue );
    lablevalues.push(finalvalue);
    console.log(lablevalues);
    document.getElementById("onchangedvalue").value = lablevalues;
  }

  function change(dropdown,input1){
    var val = dropdown.options[dropdown.selectedIndex].value;
    if(val.trim()==='N'){

      document.getElementById(input1).disabled = true;

    }else{
      document.getElementById(input1).disabled = false;
    }
  }
</script>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Partner Default Configuration
      </div>
      <% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        String partnerId1 = request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
      %>
      <form action="/icici/servlet/PartnerDetails?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <br>
        <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                }
                String errormsg = (String) request.getAttribute("cbmessage");
                if (errormsg == null)
                  errormsg = "";
                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                out.println(errormsg);
                out.println("</b></font></td></tr></table>");
                Hashtable temphash = null;
              %>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb">Partner Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <input name="partnerid" id="pid1" value="<%=partnerId1%>" class="txtbox" autocomplete="on">
                    <%--<input type="text" size="10"name="partnerid"class="txtbox" value=<%=partnerId1%>>--%>
                  </td>
                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<form action="/icici/servlet/SetReservesDefault?ctoken=<%=ctoken%>" method=post>
  <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>

  <div class="reporttable" style="margin-bottom: 9px">
    <%
      Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
      int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
      int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
      int records = 0;
      int totalrecords = 0;
      if ((hash != null && hash.size() > 0))
      {
        try
        {
          records = Integer.parseInt((String) hash.get("records"));
          totalrecords = Integer.parseInt((String) hash.get("totalrecords"));

        }
        catch (Exception ex)
        {
          log.error("Records & TotalRecords is found null", ex);
        }
      }
      if (records > 0)
      {
    %>
    <center><font class="textb">Total records: <%=totalrecords%>
    </font></center>
    <br>
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <table align=center class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <%
        String style = "td1";
        for (int pos = 1; pos <= records; pos++)
        {
          String id = Integer.toString(pos);
          int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
          if (pos % 2 == 0)
            style = "tr0";
          else
            style = "tr0";
          temphash = (Hashtable) hash.get(id);
          String partnerId = (String) temphash.get("partnerid");
          String partnerName = (String) temphash.get("partnerName");
          String siteUrl=(String) temphash.get("siteurl");
          String isReadOnly = "";
      %>
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0"> Sr. No.</td>
        <td valign="middle" align="center" class="th0"> Partner Id</td>
        <td valign="middle" align="center" class="th0"> Partner Name</td>
        <td valign="middle" align="center" class="th0"> Site Url</td>
      </tr>
      </thead>
      <tr>
        <td align="center" class="<%=style%>"><%=srno%>
        </td>
        <td align="center" class="<%=style%>"><%=partnerId%><input type=hidden name="partnerid"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerId)%>">
        </td>
        <td align="center" class="<%=style%>"><%=partnerName%><input type="hidden"name="partnerName"value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>">
        </td>
        <td align="center" class="<%=style%>"><%=siteUrl%><input type="hidden" name="siteurl"value="<%=ESAPI.encoder().encodeForHTMLAttribute(siteUrl)%>">
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
           style="margin-bottom: 0px">
      <thead>
      <tr class="th0">
        <td colspan="6">
          <center><b>Member Limits</b></center>
        </td>
      </tr>
      </thead>
      <thead>
      <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Amount Limit  </td>
        <td  style="height: 30px"valign="middle"  align="center"  class="tr1">  Daily Card Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Card Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Card Limit  </td>
      </tr>
      </thead>
      <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit')">
        </td>
      </tr>
      <thead>
      <tr>
        <td valign="middle" align="center" class="tr1"> Daily Card Amount Limit</td>
        <td valign="middle" align="center" class="tr1"> Weekly Card Amount Limit</td>
        <td valign="middle" align="center" class="tr1"> Monthly Card Amount Limit</td>
        <td valign="middle" align="center" class="tr1"> Card Limit Check</td>
        <td valign="middle" align="center" class="tr1"> Card Amount Limit Check</td>
        <td valign="middle" align="center" class="tr1"> Amount Limit Check</td>
      </tr>
      </thead>
      <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_amount_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit')">
        </td>
        <td align="center" class="<%=style%>"><select
                name='card_check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Limit Check')">
          <%
            if (temphash.get("card_check_limit").equals("0"))
            {
          %>
          <option value="<%=temphash.get("card_check_limit")%>"
                  selected="selected"><%=temphash.get("card_check_limit")%>
          </option>
          <option value="1">1</option>
          <% }
          else
          {%>
          <option value="0">0</option>
          <option value="<%=temphash.get("card_check_limit")%>"
                  selected="selected"><%=temphash.get("card_check_limit")%>
          </option>
          <%}%></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='card_transaction_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Amount Limit Check')">
          <%
            if (temphash.get("card_transaction_limit").equals("0"))
            {
          %>
          <option value="<%=temphash.get("card_transaction_limit")%>"
                  selected="selected"><%=temphash.get("card_transaction_limit")%>
          </option>
          <option value="1">1</option>
          <% }
          else
          {%>
          <option value="0">0</option>
          <option value="<%=temphash.get("card_transaction_limit")%>"
                  selected="selected"><%=temphash.get("card_transaction_limit")%>
          </option>
          <%}%></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Amount Limit Check')">
          <%
            if (temphash.get("check_limit").equals("0"))
            {
          %>
          <option value="<%=temphash.get("check_limit")%>"
                  selected="selected"><%=temphash.get("check_limit")%>
          </option>
          <option value="1">1</option>
          <% }
          else
          {%>
          <option value="0">0</option>
          <option value="<%=temphash.get("check_limit")%>"
                  selected="selected"><%=temphash.get("check_limit")%>
          </option>
          <%}%></select>
        </td>
      </tr>
      <thead>
      <tr>
        <td valign="middle" align="center" class="tr1">Card Velocity Check</td>
        <td valign="middle" align="center" class="tr1">Limit Routing</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='card_velocity_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Velocity Check')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_velocity_check")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='limitRouting' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Limit Routing')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("limitRouting")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <thead>
        <tr class="th0">
          <td colspan="6" style="height: 30px">
            <center><b>Transaction Limits</b></center>
          </td>
        </tr>
      </thead>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Daily Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Monthly Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Amount Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Daily Amount Limit</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Monthly Amount Limit</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='vpaAddressLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vpaAddressLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='vpaAddressDailyCount'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Daily Count')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='vpaAddressMonthlyCount'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Monthly Count')">
        </td>
        <td align="center" class="<%=style%>">
          <select name='vpaAddressAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Amount Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vpaAddressAmountLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='vpaAddressDailyAmountLimit'
                 maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='vpaAddressMonthlyAmountLimit'
                 maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Monthly Amount Limit')">
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Count Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Daily Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Monthly Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Amount Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Daily Amount Limit </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Monthly Amount Limit </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='customerIpLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Count Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customerIpLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerIpDailyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Daily Count')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerIpMonthlyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Monthly Count')">
        </td>
        <td align="center" class="<%=style%>">
          <select name='customerIpAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Amount Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpAmountLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text name='customerIpDailyAmountLimit' size="10" maxlength="15"
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text name='customerIpMonthlyAmountLimit' size="10" maxlength="15"
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Monthly Amount Limit')">
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Count Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Daily Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Monthly Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Amount Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Daily Amount Limit</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Monthly Amount Limit</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='customerNameLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Count Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name="customerNameDailyCount"
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Count')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name="customerNameMonthlyCount"
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Monthly Count')">
        </td>
        <td align="center" class="<%=style%>">
          <select name='customerNameAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Amount Limit Check')">
              <%
                out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customerNameAmountLimitCheck"))));
              %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" maxlength="15" name='customerNameDailyAmountLimit'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Amount Limit')">
        </td> <td align="center" class="<%=style%>">
          <input type=text size="10" maxlength="15" name='customerNameMonthlyAmountLimit'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Monthly Amount Limit')">
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Count Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Daily Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Monthly Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Amount Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Daily Amount Limit</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Email Monthly Amount Limit</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='customerEmailLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Count Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customerEmailLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerEmailDailyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Daily Count')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerEmailMonthlyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Monthly Count')">
        </td>
        <td align="center" class="<%=style%>">
          <select name='customerEmailAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Amount Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customerEmailAmountLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerEmailDailyAmountLimit' maxlength="15"
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerEmailMonthlyAmountLimit' maxlength="15"
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Monthly Amount Limit')">
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Count Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Daily Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Monthly Count</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Amount Limit Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Daily Amount Limit</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Phone Monthly Amount Limit</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='customerPhoneLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Count Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerPhoneDailyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Daily Count')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerPhoneMonthlyCount'
                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Monthly Count')">
        </td>
        <td align="center" class="<%=style%>">
          <select name='customerPhoneAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Amount Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneAmountLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerPhoneDailyAmountLimit' maxlength="15"
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size="10" name='customerPhoneMonthlyAmountLimit' maxlength="15"
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Monthly Amount Limit')">
        </td>
      </tr>
    </table>
  </div>

  <%-- flags for payout --%>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
            style="margin-bottom: 0px">
      <thead>
        <tr class="th0">
          <td colspan="6" style="height: 30px">
            <center><b>Payout Limits</b></center>
          </td>
        </tr>
      </thead>
      <tr>
        <td valign="middle" align="center" class="tr1"> Payout Bank AccountNo Limit Check</td>
        <td valign="middle" align="center" class="tr1"> Bank AccountNo Daily Count</td>
        <td valign="middle" align="center" class="tr1"> Bank AccountNo Monthly Count</td>
        <td valign="middle" align="center" class="tr1"> Payout Bank AccountNo Amount Limit Check</td>
        <td valign="middle" align="center" class="tr1"> Bank AccountNo Daily Amount Limit</td>
        <td valign="middle" align="center" class="tr1"> Bank AccountNo Monthly Amount Limit</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='payoutBankAccountNoLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Bank AccountNo Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='bankAccountNoDailyCount'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Count')" >
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='bankAccountNoMonthlyCount'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Count')" >
        </td>

        <td align="center" class="<%=style%>">
          <select name='payoutBankAccountNoAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Bank AccountNo Amount Limit Check')">
            <%
              out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoAmountLimitCheck"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='bankAccountNoDailyAmountLimit'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Amount Limit')">
        </td>
        <td align="center" class="<%=style%>">
          <input type=text size=10 name='bankAccountNoMonthlyAmountLimit'
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Amount Limit')">
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="6" style="height: 30px">
          <center><b>General Configuration</b></center>
        </td>
      </tr>
      </thead>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Activation</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> HasPaid</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is MerchantInterface</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Login with Otp</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is ExcessCaptureAllowed</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is FlightMode</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist Transactions</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Activation')">
          <%
            if (temphash.get("activation").equals("T"))
            {
          %>
          <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                  selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
          </option>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else if (temphash.get("activation").equals("N"))
          {
          %>
          <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                  selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
          </option>
          <option value="T">T</option>
          <option value="Y">Y</option>
          <%
          }
          else
          {
          %>
          <option value="T">T</option>
          <option value="N">N</option>
          <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                  selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
          </option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='haspaid' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HasPaid')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("haspaid")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='merchant_interface_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is MerchantInterface')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_interface_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='merchant_verify_otp' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Login with Otp')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_verify_otp")))); %></select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='is_excesscapture_allowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is ExcessCaptureAllowed')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_excesscapture_allowed")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='flight_mode' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is FlightMode')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("flight_mode")))); %></select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='blacklist_transaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Blacklist Transactions')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("blacklist_transaction")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="8" style="height: 30px">
          <center><b>Transaction Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is Service  </td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Redirect  </td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  VBV  </td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  MasterCardSupported  </td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Select Terminal</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is POD Required </td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is RestrictedTicket</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >  Bin Routing</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='isservice' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Service')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isservice")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='auto_redirect' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Auto Redirect')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("auto_redirect")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='vbv' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VBV')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vbv")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='mastercard_supported' <%=isReadOnly%> onchange="ChangeFunction(this.value,'MasterCardSupported')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("mastercard_supported")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='auto_select_terminal' onchange="ChangeFunction(this.value,'Auto Select Terminal')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("auto_select_terminal")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_pod_required' onchange="ChangeFunction(this.value,'Is POD Required')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_pod_required")))); %></select>
        </td>
        <td align="center"  class="<%=style%>"><select name='is_restricted_ticket' onchange="ChangeFunction(this.value,'Is RestrictedTicket')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_restricted_ticket")))); %></select>
        </td>
        <td align="center"  class="<%=style%>"><select name='binRouting' onchange="ChangeFunction(this.value,'Bin Routing')">
          <%
            out.println(Functions.comboval9(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("binRouting")))); %></select>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Allowed Day's</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Is  Email Limit Enabled</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Bin Service</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Exp Date Offset</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Support Section</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Support Number Needed</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Card Whitelist Level</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Multi Currency Support</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='chargeback_allowed_days'value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("chargeback_allowed_days"))%>" onchange="ChangeFunction(this.value,'Chargeback Allowed Day')">
        </td>
        <td align="center" class="<%=style%>"><select
                name='email_limit_enabled' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is  Email Limit Enabled')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("email_limit_enabled")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='bin_service' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bin Service')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("bin_service")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='exp_date_offset'value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("exp_date_offset"))%>">
        </td>

        <td align="center" class="<%=style%>"><select
                name='support_section' <%=isReadOnly%> onchange="change(this,'supportNoNeeded')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("support_section")))); %></select>
        </td>

        <td align="center" class="<%=style%>"><select
                name='supportNoNeeded' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Support Number Needed')" id="supportNoNeeded"  <%if(temphash.get("support_section").equals("N")){  %>disabled<%}%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("supportNoNeeded")))); %></select>
          <input type=hidden size=10  name='supportNoNeeded' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("supportNoNeeded"))%>" >
        </td>

        <td align="center" class="<%=style%>">
          <select name='card_whitelist_level' onchange="ChangeFunction(this.value,'Card Whitelist Level')">
            <%
              out.println(Functions.combovalForCardWhitelistLevel(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_whitelist_level")))); %>
          </select>
        </td>

        <td align="center" class="<%=style%>"><select
                name='multi_Currency_support' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Multi Currency Support')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("multi_Currency_support")))); %></select>
        </td>

      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> IP Validation Required</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Personal Info Display</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Personal Info Validation</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Rest Checkout Page </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">EMI Support</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Merchant Order Details</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Market Place</td>
        <%--<td style="height: 30px" valign="middle" align="center" class="tr1">Is Cvv Store</td>--%>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Is Unique OrderId Required</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='ip_validation_required' onchange="ChangeFunction(this.value,'IP Validation Required')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_validation_required")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='personal_info_display' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Personal Info Display')">
          <% out.println(Functions.comboPersonalInfo(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_display")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='personal_info_validation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Personal Info Validation')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_validation")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='hosted_payment_page' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Rest Checkout Page')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hosted_payment_page")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='emiSupport' <%=isReadOnly%> onchange="ChangeFunction(this.value,'EMI Support')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emiSupport")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='merchant_order_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Order Details')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_order_details")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='marketplace' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Market Place')">
          <% out.println(Functions.comboval4(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("marketplace")))); %></select>
        </td>
        <%--<td align="center" class="<%=style%>"><select name='isCvvStore' <%=isReadOnly%>>
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isCvvStore")))); %></select>
        </td>--%>
        <td align="center" class="<%=style%>"><select name='isUniqueOrderIdRequired' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Unique OrderId Required')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isUniqueOrderIdRequired")))); %></select>
        </td>
      </tr>

      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Card Expiry Date Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Is OTPRequired</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Is CardStorageRequired</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='cardExpiryDateCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Expiry Date Check')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("cardExpiryDateCheck")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='isOTPRequired' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is OTPRequired')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isOTPRequired"))));%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select name="isCardStorageRequired" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is CardStorageRequired')">
          <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCardStorageRequired"))));%>
        </select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="7" style="height: 30px">
          <center><b>BackOffice Access Management</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> DashBoard </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Account Details</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Settings</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Transaction Management</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoicing</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Rejected Transaction</td>
        <%-- <td style="height: 30px" valign="middle" align="center" class="tr1"> EMI Configuration</td>--%>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='dashboard_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'DashBoard')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("dashboard_access")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='accounting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Details')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounting_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='setting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Settings')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("setting_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='transactions_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Management')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transactions_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='invoicing_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoicing')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoicing_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='rejected_transaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Rejected Transaction')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("rejected_transaction"))));
            %>
          </select>
        </td>
        <%--<td align="center" class="<%=style%>"><select
                name='emi_configuration' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emi_configuration")))); %></select>
        </td>--%>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Virtual Terminal</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Management</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Application Manager</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Recurring</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Token Management</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Virtual Checkout </td>
        <%--<td style="height: 30px"valign="middle" align="center" class="tr1" > &nbsp;</td>--%>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='virtualterminal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Terminal')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("virtualterminal_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='merchantmgt_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Management')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_access")))); %></select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='is_appmanager_activate' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Application Manager')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_appmanager_activate")))); %></select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='is_recurring' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Recurring')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_recurring"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='is_card_registration_allowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Token Management')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("is_card_registration_allowed"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='virtual_checkout' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Checkout')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("virtual_checkout"))));
            %>
          </select>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Pay By Link </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name="paybylink" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Pay By Link')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("paybylink"))));
            %>
          </select>
        </td>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Account Details
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Account Summary  </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Charges Summary </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Transaction Summary</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Reports </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='accounts_account_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Summary')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_account_summary_access")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='accounts_charges_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Charges Summary')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_charges_summary_access")))); %>
        </select>
        </td>
        </select>

        <td align="center" class="<%=style%>"><select
                name='accounts_transaction_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Summary')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_transaction_summary_access")))); %>
        </select>
        </td>
        </select>

        <td align="center" class="<%=style%>"><select
                name='accounts_reports_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reports')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_reports_summary_access")))); %>
        </select>
        </td>
        </select>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Settings
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Profile </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Organisation Profile</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> View Key</td>
<%--
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Page</td>
--%>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Generate Key </td>
        <%-- <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice Configuration</td>--%>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Configuration</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Fraud Rule Configuration</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Whitelist Details</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" > Block Details</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='settings_merchant_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Profile')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_profile_access")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='settings_organisation_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Organisation Profile')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_organisation_profile_access")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='generateview' <%=isReadOnly%> onchange="ChangeFunction(this.value,'View Key')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("generateview")))); %>
        </select>
        </td>
        </select>
        <%--<td align="center" class="<%=style%>"><select
                name='settings_checkout_page_access' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_checkout_page_access")))); %></select>
        </td>--%>
        </select>
        <td align="center" class="<%=style%>"><select
                name='settings_generate_key_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Key')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_generate_key_access")))); %></select>
        </td>
        </select>
        <%-- <td align="center" class="<%=style%>"><select
                 name='settings_invoice_config_access' <%=isReadOnly%>>
           <%
             out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
         </td>
         </select>--%>
        <td align="center" class="<%=style%>"><select
                name='settings_merchant_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Configuration')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_config_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>">
          <select name='settings_fraudrule_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Rule Configuration')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_fraudrule_config_access"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='settings_whitelist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Whitelist Details')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_whitelist_details"))));
            %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='settings_blacklist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Block Details')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_blacklist_details"))));
            %>
          </select>
        </td>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Transaction Management
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Transactions </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Capture </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Reversal</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='transmgt_transaction_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transactions')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_transaction_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='transmgt_capture_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Capture')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_capture_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='transmgt_reversal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reversal')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_reversal_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='transmgt_payout_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_payout_access")))); %></select>
        </td>
        </select>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Invoicing
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Generate Invoice </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice History </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice Configuration</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='invoice_generate_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Invoice')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_generate_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='invoice_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice History')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_history_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='settings_invoice_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice Configuration')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
        </td>
        </select>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Token Management
        </td>
      </tr>

      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Registration History </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Register Card </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='tokenmgt_registration_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Registration History')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_registration_history_access")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='tokenmgt_register_card_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Register Card')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_register_card_access")))); %></select>
        </td>
        </select>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Merchant Management
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> User Management </td>
<%--
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Key </td>
--%>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='merchantmgt_user_management_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'User Management')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_user_management_access")))); %></select>
        </td>
        <%--<td align="center" class="<%=style%>"><select
                name='isMerchantKey' <%=isReadOnly%>>
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMerchantKey")))); %></select>
        </td>--%>
        </select>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="th0">
        <td colspan="12" style="height: 30px">
          <center><b>Template Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> IS Pharma(Y/N)</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Powered By Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Template</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> PCI Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Partner Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Visa Secure Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> isMerchantLogoBO</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> MC Secure Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Consent</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Security Logo</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Timer</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Timer Time</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='is_pharma' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Pharma(Y/N)')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_pharma")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='is_powered_by' onchange="ChangeFunction(this.value,'Is Powered By Logo')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_powered_by")))); %></select>
        </td>
        <input type="hidden" name="is_powered_by" value="<%=(String) temphash.get("is_powered_by")%>">
        <td align="center" class="<%=style%>"><select
                name='template' onchange="ChangeFunction(this.value,'Template')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("template")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_pci_logo' onchange="ChangeFunction(this.value,'PCI Logo')">
          <%
            if("Y".equals(temphash.get("is_pci_logo")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_partner_logo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Partner Logo')">
          <%
            if("Y".equals(temphash.get("is_partner_logo")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select name='vbvLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Visa Secure Logo')">
          <%
            if("Y".equals(temphash.get("vbvLogo")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select name='isMerchantLogoBO'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'isMerchantLogoBO')">
            <%
              if ("Y".equals(temphash.get("isMerchantLogoBO")))
              {
            %>
              <option value="Y">Y</option>
              <option value="N">N</option>
          <%
            }
            else
            {
          %>
            <option value="N">N</option>
            <option value="Y">Y</option>
          <% } %>
          </select>
        </td>
        <td align="center" class="<%=style%>"><select name='masterSecureLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'MC Secure Logo')">
          <%
            if("Y".equals(temphash.get("masterSecureLogo")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><select name='consent'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Consent')">
          <%
            if("Y".equals(temphash.get("consent")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>

        <td align="center" class="<%=style%>"><select name='isSecurityLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Is Security Logo')">
          <%
            if("Y".equals(temphash.get("isSecurityLogo")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>

        <td align="center" class="<%=style%>"><select name='checkoutTimer' onchange="ChangeFunction(this.value,'Checkout Timer')">
          <%
            if("Y".equals(temphash.get("checkoutTimer")))
            {
          %>
          <option value="Y">Y</option>
          <option value="N">N</option>
          <%
          }
          else
          {
          %>
          <option value="N">N</option>
          <option value="Y">Y</option>
          <% }%>
        </select>
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='checkoutTimerTime' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("checkoutTimerTime"))%>" onchange="ChangeFunction(this.value,'Checkout Timer Time')">
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0"><td colspan="5" style="height: 30px"><center><b>Token Configuration</b></center></td></tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is TokenizationAllowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is AddressDetails Required</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Token Valid Days</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Card Encryption Enable</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='is_tokenization_allowed' onchange="ChangeFunction(this.value,'Is TokenizationAllowed')">
          <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("is_tokenization_allowed"))));  %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_address_details_required' onchange="ChangeFunction(this.value,'Is AddressDetails Required')">
          <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("is_address_details_required"))));  %></select>
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='token_valid_days' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("token_valid_days"))%>" onchange="ChangeFunction(this.value,'Token Valid Days')">
        </td>
        <td align="center" class="<%=style%>"><select name='is_card_encryption_enable' onchange="ChangeFunction(this.value,'Is Card Encryption Enable')">
          <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("is_card_encryption_enable"))));  %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
           style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="7" style="height: 30px">
          <center><b>Fraud Defender Configuration</b></center>
        </td>
      </tr>
    </table>

    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Purchase Inquiry
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund  </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='ispurchase_inquiry_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_refund")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='ispurchase_inquiry_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Blacklist')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_blacklist")))); %>
        </select>
        </td>
        </select>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Fraud Determined
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='isfraud_determined_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_refund")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='isfraud_determined_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Blacklist')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_blacklist")))); %>
        </select>
        </td>
        </select>

      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Dispute Initiated
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='isdispute_initiated_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_refund")))); %>
        </select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='isdispute_initiated_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Blacklist')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_blacklist")))); %>
        </select>
        </td>
        </select>

      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Exception file listing
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='isexception_file_listing_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Exception file listing Blacklist')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isexception_file_listing_blacklist")))); %>
        </select>
        </td>
      </tr>
    </table>
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="td1">
        <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
          Stop payment
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='isstop_payment_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Stop payment Blacklist')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isstop_payment_blacklist")))); %>
        </select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="4" style="height: 30px">
          <center><b>Whitelisting Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Card Whitelisted</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Ip Whitelisted</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is IP Whitelisted For APIs</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Domain Whitelisted</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='iswhitelisted' onchange="ChangeFunction(this.value,'Is Card Whitelisted')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("iswhitelisted")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_ip_whitelisted' onchange="ChangeFunction(this.value,'Is Ip Whitelisted')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_ip_whitelisted")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='is_rest_whitelisted' onchange="ChangeFunction(this.value,'Is IP Whitelisted For APIs')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_rest_whitelisted")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='isDomainWhitelisted' onchange="ChangeFunction(this.value,'Is Domain Whitelisted')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isDomainWhitelisted")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
      <tr class="th0">
        <td colspan="2" style="height: 30px">
          <center><b>HR Transaction Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> HR alertPROOF</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> HR Parameterized</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='hralertproof' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR alertPROOF')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hralertproof")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='hrparameterised' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR Parameterized')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hrparameterised")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="8" style="height: 30px">
          <center><b>Refund Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Refund</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Daily Refund Limit</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Allowed Day's</td>
        <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Is Refund Mail Sent</td>--%>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Multiple Refund</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Partial Refund</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='is_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_refund")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='refund_daily_limit'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refund_daily_limit"))%>" onchange="ChangeFunction(this.value,'Daily Refund Limit')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='refundallowed_days'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refundallowed_days"))%>" onchange="ChangeFunction(this.value,'Refund Allowed Day')">
        </td>

        <%--<td align="center" class="<%=style%>"><select name='isRefundEmailSent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund Mail Sent')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isRefundEmailSent")))); %></select>
        </td>--%>
        <td align="center" class="<%=style%>"><select name='isMultipleRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Multiple Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMultipleRefund")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='isPartialRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Partial Refund')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPartialRefund")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="6" style="height: 30px">
          <center><b>Email Configuration</b></center>
        </td>
      </tr>
      </thead>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> IS Validate Email</td>
        <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Reminder Email</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Email Sent</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Email (Y/N)</td>--%>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Email Template Language</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='is_validate_email' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Validate Email')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_validate_email")))); %></select>
        </td>
        <%--<td align="center" class="<%=style%>"><select
                name='cust_reminder_mail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Reminder Email')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("cust_reminder_mail")))); %></select>
        </td>--%>
        <%--<td align="center" class="<%=style%>"><select
                name='merchant_email_sent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Email Sent')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_email_sent")))); %></select>
        </td>--%>

        <%--<td align="center" class="<%=style%>"><select
                name='chargebackEmail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Chargeback Email (Y/N)')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackEmail")))); %></select>
        </td>--%>

        <td align="center" class="<%=style%>"><select
                name='emailTemplateLang' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Email Template Language')">
          <%
            out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emailTemplateLang")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="2" style="height: 30px">
          <center><b>SMS Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Merchant SMS Activation</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Customer SMS Activation</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select name='merchant_sms_activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant SMS Activation')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_sms_activation")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select name='customer_sms_activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer SMS Activation')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customer_sms_activation")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="2" style="height: 30px">
          <center><b>Invoice Configuration</b></center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Invoice Merchant Details</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Is IP whitelist for Invoice</td>
      </tr>
      <tr>
        <td align="center" align="center" class="<%=style%>"><select
                name='invoice_template' onchange="ChangeFunction(this.value,'Invoice Merchant Details')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_template")))); %></select>
        </td>
        <td align="center" align="center" class="<%=style%>"><select
                name='ip_whitelist_invoice' onchange="ChangeFunction(this.value,'Is IP whitelist for Invoic')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_whitelist_invoice")))); %></select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0">
        <td colspan="5" style="height: 30px">
          <center>Fraud Configuration</center>
        </td>
      </tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Max Score Allowed</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Max Score Reversal</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Online Fraud Check</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1">Internal Fraud Check</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='max_score_allowed'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("max_score_allowed"))%>" onchange="ChangeFunction(this.value,'Max Score Allowed')">
        </td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='max_score_auto_reversal'
                                                     value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("max_score_auto_reversal"))%>" onchange="ChangeFunction(this.value,'Max Score Reversal')">
        </td>
        <td align="center" class="<%=style%>"><select
                name='online_fraud_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Online Fraud Check')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("online_fraud_check")))); %></select>
        </td>
        <td align="center" class="<%=style%>"><select
                name='internalFraudCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Internal Fraud Check')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("internalFraudCheck")))); %></select>

        </td>

      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0"><td colspan="2" style="height: 30px"><center><b>Split transaction configuration</b></center></td></tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Split Payment Allowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Split Payment Type</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='is_split_payment' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Split Payment Allowed')">
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_split_payment")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='split_payment_type' onchange="ChangeFunction(this.value,'Split Payment Type')">
            <%
              out.println(Functions.combovalForSplitPayment(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("split_payment_type")))); %>
          </select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0"><td colspan="7" style="height: 30px"><center><b>Invoizer Configuration</b></center></td></tr>
      <tr>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Virtual Checkout Allowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Phone Required</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Email Required</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Share Allowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Signature Allowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Is SaveReceipt Allowed</td>
        <td style="height: 30px"valign="middle" align="center" class="tr1" >Default Language</td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>">
          <select name='isVirtualCheckoutAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Virtual Checkout Allowed')">
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isVirtualCheckoutAllowed")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='isMobileAllowedForVC' onchange="ChangeFunction(this.value,'Is Phone Required')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMobileAllowedForVC")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='isEmailAllowedForVC' onchange="ChangeFunction(this.value,'Is Email Required')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isEmailAllowedForVC")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='isShareAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Share Allowed')">
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isShareAllowed")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='isSignatureAllowed' onchange="ChangeFunction(this.value,'Is Signature Allowed')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSignatureAllowed")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='isSaveReceiptAllowed' onchange="ChangeFunction(this.value,'Is Save Receipt Allowed')">
            <%
              out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSaveReceiptAllowed")))); %>
          </select>
        </td>
        <td align="center" class="<%=style%>">
          <select name='defaultLanguage' onchange="ChangeFunction(this.value,'Default Language')">
            <%
              out.println(Functions.combovalLang(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("defaultLanguage")))); %>
          </select>
        </td>
      </tr>
    </table>
  </div>
  <div class="reporttable" style="margin-bottom: 9px;">
    <table border="0" width="100%" style="width: 50%" align="center"
           class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
      <tr class="th0"><td colspan="6" style="height: 30px"><center><b>Merchant Notification Callback</b></center></td></tr>
      <tr>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Reconciliation </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Transactions (3D/Non-3d/Both/No)</td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund  Notification </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Notification </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout Notification </td>
        <td style="height: 30px" valign="middle" align="center" class="tr1"> Inquiry Notification </td>
      </tr>
      <tr>
        <td align="center" class="<%=style%>"><select
                name='reconciliationNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reconciliation')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("reconciliationNotification")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='transactionNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transactions (3D/Non-3d/Both/No)')">
          <%
            out.println(Functions.comboval3D(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transactionNotification")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='refundNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Refund  Notification')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("refundNotification")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
                name='chargebackNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Chargeback Notification')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackNotification")))); %></select>
        </td>
        </select>
        <td align="center" class="<%=style%>"><select
             name="payoutNotification" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Notification')">
          <%
            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutNotification"))));%></select>
        </td>
        </select>
        <td align="center" class="<%=style%>">
          <select name="inquiryNotification" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Inquiry Notification')">
              <%
           out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("inquiryNotification"))));
           %></select>
        </td>
        </select>
      </tr>
    </table>
  </div>

  <div class="reporttable" style="margin-bottom: 9px;">
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="th0">
          <td colspan="7" style="height: 30px">
            <center><b>Merchant Email Notification Settings</b></center>
          </td>
        </tr>
      </table>
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Setup Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String merchantRegistrationMail="";
          String merchantChangePassword="";
          String merchantChangeProfile="";
          if("Y".equals(temphash.get("merchantRegistrationMail")))
            merchantRegistrationMail="checked";
          if("Y".equals(temphash.get("merchantChangePassword")))
            merchantChangePassword="checked";
          if("Y".equals(temphash.get("merchantChangeProfile")))
            merchantChangeProfile="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Registration</td>
          <td align="center" class="tr0"><input type="checkbox" name='merchantRegistrationMail' <%=merchantRegistrationMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Registration')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Password</td>
          <td align="center" class="tr0"><input type="checkbox" name='merchantChangePassword' <%=merchantChangePassword%>
                                                value="Y" onchange="ChangeFunction(this.value,'Change Password')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Profile</td>
          <td align="center" class="tr0"><input type="checkbox" name='merchantChangeProfile' <%=merchantChangeProfile%>
                                                value="Y" onchange="ChangeFunction(this.value,'Change Profile')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Transaction Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String transactionSuccessfulMail="";
          String transactionFailMail="";
          String transactionCapture="";
          String transactionPayoutSuccess="";
          String transactionPayoutFail="";
          if("Y".equals(temphash.get("transactionSuccessfulMail")))
            transactionSuccessfulMail="checked";
          if("Y".equals(temphash.get("transactionFailMail")))
            transactionFailMail="checked";
          if("Y".equals(temphash.get("transactionCapture")))
            transactionCapture="checked";
          if("Y".equals(temphash.get("transactionPayoutSuccess")))
            transactionPayoutSuccess="checked";
          if("Y".equals(temphash.get("transactionPayoutFail")))
            transactionPayoutFail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Successful</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionSuccessfulMail' <%=transactionSuccessfulMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Transaction Successful Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Failed</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionFailMail' <%=transactionFailMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Transaction Failed Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Capture</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionCapture' <%=transactionCapture%>
                                                value="Y" onchange="ChangeFunction(this.value,'Transaction Capture Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Successful</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutSuccess' <%=transactionPayoutSuccess%>
                                                value="Y"  onchange="ChangeFunction(this.value,'Transaction Payout Successful Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Fail</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutFail' <%=transactionPayoutFail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Transaction Payout Fail Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Refund/Chargeback Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String refundMail="";
          String chargebackMail="";
          if("Y".equals(temphash.get("refundMail")))
            refundMail="checked";
          if("Y".equals(temphash.get("chargebackMail")))
            chargebackMail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Refunds</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='refundMail' <%=refundMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Refunds Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Chargebacks</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='chargebackMail' <%=chargebackMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Chargebacks Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Invoice Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String transactionInvoice="";
          if("Y".equals(temphash.get("transactionInvoice")))
            transactionInvoice="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Invoice</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='transactionInvoice' <%=transactionInvoice%>
                                                value="Y" onchange="ChangeFunction(this.value,'Invoice Mail')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Tokenization Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String cardRegistration="";
          if("Y".equals(temphash.get("cardRegistration")))
            cardRegistration="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Card Registration</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='cardRegistration' <%=cardRegistration%>
                                                value="Y" onchange="ChangeFunction(this.value,'Tokenization Card Registration Mails')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Payout Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String payoutReport="";
          if("Y".equals(temphash.get("payoutReport")))
            payoutReport="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Report</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='payoutReport' <%=payoutReport%>
                                                value="Y" onchange="ChangeFunction(this.value,'Payout Report Mails')" >
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
<%--
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Monitoring Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String monitoringAlertMail="";
          String monitoringSuspensionMail="";
          if("Y".equals(temphash.get("monitoringAlertMail")))
            monitoringAlertMail="checked";
          if("Y".equals(temphash.get("monitoringSuspensionMail")))
            monitoringSuspensionMail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Alerts</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='monitoringAlertMail' <%=monitoringAlertMail%>
                                                value="Y">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Suspension</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='monitoringSuspensionMail' <%=monitoringSuspensionMail%>
                                                value="Y">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    --%>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Fraud Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String highRiskRefunds="";
          String fraudFailedTxn="";
          String dailyFraudReport="";
          if("Y".equals(temphash.get("highRiskRefunds")))
            highRiskRefunds="checked";
          if("Y".equals(temphash.get("fraudFailedTxn")))
            fraudFailedTxn="checked";
          if("Y".equals(temphash.get("dailyFraudReport")))
            dailyFraudReport="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> High Risk Refunds</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='highRiskRefunds' <%=highRiskRefunds%>
                                                value="Y" onchange="ChangeFunction(this.value,'High Risk Refunds')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Fraud Failed Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='fraudFailedTxn' <%=fraudFailedTxn%>
                                                value="Y"onchange="ChangeFunction(this.value,'Fraud Failed Transaction')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Daily Fraud Report</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='dailyFraudReport' <%=dailyFraudReport%>
                                                value="Y" onchange="ChangeFunction(this.value,'Daily Fraud Report')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="8" style="height: 30px">
            <center><b>Recon Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String successReconMail="";
          String refundReconMail="";
          String chargebackReconMail="";
          String payoutReconMail="";
          if("Y".equals(temphash.get("successReconMail")))
            successReconMail="checked";
          if("Y".equals(temphash.get("refundReconMail")))
            refundReconMail="checked";
          if("Y".equals(temphash.get("chargebackReconMail")))
            chargebackReconMail="checked";
          if("Y".equals(temphash.get("payoutReconMail")))
            payoutReconMail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Success Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='successReconMail' <%=successReconMail%>
                                                value="Y" onchange="ChangeFunction(CHECK(this),'Success Trnasaction')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Refund Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='refundReconMail' <%=refundReconMail%>
                                                value="Y" onchange="ChangeFunction(CHECK(this),'Refund Transaction')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> ChargebackReversed/Casefiling Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='chargebackReconMail' <%=chargebackReconMail%>
                                                value="Y" onchange="ChangeFunction(CHECK(this),'Chargeback Transaction')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Transaction</td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td align="center" class="tr0"><input type="checkbox" name='payoutReconMail' <%=payoutReconMail%>
                                                value="Y" onchange="ChangeFunction(CHECK(this),'Payout Recon')">
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
        </tr>
      </table>
    </div>
  </div>


  <div class="reporttable" style="margin-bottom: 9px;">
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="th0">
          <td colspan="7" style="height: 30px">
            <center><b>Customer Email Notification Settings</b></center>
          </td>
        </tr>
      </table>
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Customer Transaction Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String customerTransactionSuccessfulMail="";
          String customerTransactionFailMail="";
          String customerTransactionPayoutSuccess="";
          String customerTransactionPayoutFail="";
          if("Y".equals(temphash.get("customerTransactionSuccessfulMail")))
            customerTransactionSuccessfulMail="checked";
          if("Y".equals(temphash.get("customerTransactionFailMail")))
            customerTransactionFailMail="checked";
          if("Y".equals(temphash.get("customerTransactionPayoutSuccess")))
            customerTransactionPayoutSuccess="checked";
          if("Y".equals(temphash.get("customerTransactionPayoutFail")))
            customerTransactionPayoutFail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Successful</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerTransactionSuccessfulMail' <%=customerTransactionSuccessfulMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Transaction Successful Mail')">
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Failed</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerTransactionFailMail' <%=customerTransactionFailMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Transaction Failed Mail')">
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Successful</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutSuccess' <%=customerTransactionPayoutSuccess%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Transaction Payout Successful Mail')">
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Fail</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutFail' <%=customerTransactionPayoutFail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Transaction Payout Fail Mail')">
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Customer Refund Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String customerRefundMail="";
          if("Y".equals(temphash.get("customerRefundMail")))
            customerRefundMail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Refunds</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerRefundMail' <%=customerRefundMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Refunds Mail')">
        </tr>
      </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
      <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
             style="margin-bottom: 0px">
        <tr class="td1">
          <td colspan="9" style="height: 30px">
            <center><b>Customer Tokenization Mails</b></center>
          </td>
        </tr>
        </thead>
        <%
          String customerTokenizationMail="";
          if("Y".equals(temphash.get("customerTokenizationMail")))
            customerTokenizationMail="checked";
        %>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
          <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Support</td>
        </tr>
        <tr>
          <td style="height: 30px" valign="middle" align="center" class="tr0"> Card Registration</td>
          <td align="center" class="tr0"><input type="checkbox" name='customerTokenizationMail' <%=customerTokenizationMail%>
                                                value="Y" onchange="ChangeFunction(this.value,'Customer Tokenization Card Registration Mail')">
        </tr>
      </table>
    </div>
  </div>
  <%
    }
  %>
  <table align="center">
    <tr>
      <td>
        <button type="submit" value="Save" class="buttonform" style="margin-left: 76%">
          Save
        </button>
      </td>
    </tr>
  </table>
</form>
<br>
<%
  }
  else
  {
    out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    out.println("</div>");
  }
%>
</div>
</body>
</html>