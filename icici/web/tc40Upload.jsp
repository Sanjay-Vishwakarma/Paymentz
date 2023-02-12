<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/26/13
  Time: 1:48 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp" %>
<html>
<head>
  <title>Bank Details> Bulk TC40 Upload</title>

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

 <%-- <script type="text/javascript" language="JavaScript" src="http://code.jquery.com/jquery-latest.min.js"></script>--%>
  <%--<script type="text/javascript" language="JavaScript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>--%>
  <script type="text/javascript">
    /*$(document).ready(function()
    {
    $("#select1").change(function() {
      if ($(this).data('options') == undefined) {
        /!*Taking an array of all options-2 and kind of embedding it on the select1*!/
        $(this).data('options', $('#select2 option').clone());
      }
      var id = $(this).val();
      var options = $(this).data('options').filter('[value=' + id + ']');
      $('#select2').html(options);
    });
    });*/

    function check() {
      var  retpath = document.FIRCForm.File.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;

      if (filename==('xls')) {

        return true;
      }
      alert('Please select a .xls file instead!');
      return false;

    }
    function checkData(data)
    {
      if(data.checked)
      {
        document.getElementById("isRefundAll").value='Y';
      }
      else
      {
        document.getElementById("isRefundAll").value='N';
      }

    }

    $( "input[name='pgtypeid']").on( "click", function() {
      if($('input:radio[name=pgtypeid]:checked').val()=="N")
      {
        document.FIRCForm.company_bankruptcydate.disabled = true;

      }
      else
      {
        document.FIRCForm.company_bankruptcydate.disabled = false;
      }
    });

  </script>
  <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>--%>

</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String str = "";
    String pgtypeid = "";
    String currency= "";
    currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
    pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
    String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    //System.out.println("pgtypeid::::::>"+pgtypeid);
    //System.out.println("accountid::::::>"+accountid2);

    List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
    List<String> gatwayName = GatewayTypeService.loadGateway();
    Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();

    if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
    else
      pgtypeid="";
    if(accountid2!=null)str = str + "&accountid=" + accountid2;
    else
      accountid2="";
    if(currency!=null)str = str + "&currency=" + currency;
    else
      currency="";


%>

<form name = "FIRCForm" action="/icici/tc40file.jsp?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data" >
  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
  <div class="row">
    <div class="col-lg-12">
      <div class="panel panel-default">
        <div class="panel-heading">
          TC40 File
        </div>
        <br>
        <table  border="0" cellpadding="5" cellspacing="0" align="center">
          <tr>
            <td>
              <table  border="0" cellpadding="5" cellspacing="0" align="center">
                <tr>
                  <td>&nbsp;</td>
                  <td>

                  </td>
                </tr>
                </td>

                <tr>


                  <td class="textb">
                    Account/Gateway&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                  </td>
                  <td>
                    <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                   <%-- <select size="1" id="bank" class="txtbox" name="pgtypeid" >
                      <option value="--All--" default>--All--</option>
                      <%
                        StringBuilder sb = new StringBuilder();
                        for(String gatewayType : gatwayName)
                        {
                          String st = "";
                          String name = gatewayType;
                          if(name != null)
                          {
                            if(pgtypeid.equalsIgnoreCase(gatewayType))
                              st = "<option value='" + gatewayType + "'selected>" + gatewayType.toUpperCase() + "</option>";
                            else
                              st = "<option value='" + gatewayType + "'>" + gatewayType.toUpperCase() + "</option>";
                            sb.append(st);
                          }
                        }
                      %>
                      <%=sb.toString()%>
                    </select>--%>
                  </td>

                  <td>
                  <td valign="middle" align="center" class="textb">Currency&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;</td> <td class="textb">
                  <select size="1" id="currency" class="txtbox" name="currency">
                    <option value="--All--" default>--All--</option>
                    <%
                      StringBuilder sb1 = new StringBuilder();
                      for(String currency2 : gatewayCurrency)
                      {
                        String st = "";
                        String name = currency2;
                        if(name != null)
                        {
                          if(currency.equalsIgnoreCase(currency2))
                            st = "<option value='" + currency2 + "'selected>" + currency2 + "</option>";
                          else
                            st = "<option value='" + currency2 + "'>" + currency2 + "</option>";
                          sb1.append(st);
                        }

                      }
                    %>
                    <%=sb1.toString()%>
                  </select>
                </td>

                  <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                  <td>
                  <td valign="middle" align="center" class="textb">Accounts&nbsp;&nbsp;:&nbsp;&nbsp;</td> <td class="textb">
                  <input name="accountid" id="accountid1" value="<%=accountid2%>" class="txtbox" autocomplete="on">
                  <%--<select size="1" id="accountid" name="accountid" class="txtbox">
                    <option data-bank="all" data-curr="all" value="0" data-target="0">Select AccountID</option>
                    <%
                      TreeSet accountSet = new TreeSet<Integer>();
                      accountSet.addAll(accountDetails.keySet());
                      Iterator enu3 = accountSet.iterator();
                      String selected3 = "";
                      GatewayAccount value3 = null;
                      while (enu3.hasNext())
                      {
                        value3 = (GatewayAccount)accountDetails.get(enu3.next());
                        int acId = value3.getAccountId();
                        String currency2 = value3.getCurrency();
                        String mid = value3.getMerchantId();
                        String gateway2 = value3.getGateway();
                        String gatewayName = value3.getGatewayName();
                        //newly added
                        if (String.valueOf(acId).equals(accountid2))
                          selected3 = "selected";
                        else
                          selected3 = "";

                    %>
                    <option data-bank="<%=value3.getGateway()%>" data-curr="<%=value3.getCurrency()%>" value="<%=value3.getAccountId()%>" <%=selected3%>><%=acId+"-"+currency2+"-"+mid+"-"+gateway2+"-"+gatewayName%></option>
                    <%
                      }
                    %>
                  </select>--%>
                </td>


                </tr>

                <tr>
                  <td>&nbsp;</td>
                  <td>

                  </td>
                </tr>
                <tr>
                  <td class="textb">
                    TC40 File :&nbsp;&nbsp;&nbsp;
                  </td>
                  <td>
                    <input name="File" type="file" value="choose File">
                  </td>
                </tr>
                <tr>
                  <td>&nbsp;</td>

                </tr>
                <tr>
                  <td width="25%" class="textb" >
                    <input type=checkbox name="refund" value="refund" id="refund" onclick="checkData(this)">
                    <input type=hidden name="isRefundAll" id="isRefundAll" value="N">


                    &nbsp;&nbsp; Refund All Transaction</td>

                </tr>
                <tr>
                  <td colspan="2" align="right">
                    <button name="mybutton" type="submit" value="Upload" class="buttonform" onclick="return check()">Upload</button>
                    <%-- <%
                       if ()
                     %>--%>
                  </td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                  <td>

                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
<%
  String message=(String)request.getAttribute("res");
  if(message!=null)
  {
    out.println(Functions.ShowMessage("Message",message));
  }

%>
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