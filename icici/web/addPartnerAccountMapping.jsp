<%--
  Created by IntelliJ IDEA.
  User: Swamy
  Date: 5/9/2015
  Time: 3:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%!
  Logger logger=new Logger("addPartnerAccountMapping.jsp");
  Functions functions=new Functions();
%>
<html>
<head>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
  <title>Add Partner Account Mapping</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String str2 = "";
    String pgtypeid = "";
    String currency= "";
    currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
    pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
    String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String partnerid = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");

    List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
    List<String> gatwayName = GatewayTypeService.loadGateway();
    Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();

    if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
    else
      pgtypeid="";
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Add Partner Account Mapping
      </div>
      <form name="addtype" action="/icici/servlet/AddPartnerAccountMapping?ctoken=<%=ctoken%>" method="post" name="form1" onsubmit="return check();">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr>
                  <td colspan="4">
                    <%
                      if(request.getAttribute("message")!=null)
                      {
                        out.println((String)request.getAttribute("message"));
                      }
                    %>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Gateway </td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input name="pgtypeid" id="pgtypeid" value="<%=pgtypeid%>" class="txtbox" autocomplete="on" >
                    <%--<select size="1" id="bank" class="txtbox" name="pgtypeid" >
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
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Currency </td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">

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
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Account id *</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input name="accountid" id="accid" value="<%=accountid2%>" class="txtbox" autocomplete="on" >

                  <%--<select size="1" id="accountid" name="accountid" class="txtbox">
                      <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
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
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Partner Id * </td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input name="partnerid" id="allpid" value="<%=partnerid%>" class="txtbox" autocomplete="on" >

                  <%-- <select name="partnerid" class="txtbox">
                      <option value=""></option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT partnerId,partnerName FROM partners ORDER BY partnerId";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("partnerId") + "\" >" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">IsActive</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px">
                    <select name="isactive" id="isactive">
                      <option value='Y'>Y</option>
                      <option value='N'>N</option>
                    </select></td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform" value="Add" style="width:150px ">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Add
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
    if (functions.isValueNull((String) request.getAttribute("statusMsg")))
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("statusMsg")));
      out.println("</div>");
    }
  }
%>
</body>
</html>
