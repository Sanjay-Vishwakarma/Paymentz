<%@ page import="com.directi.pg.Functions,com.manager.ApplicationManager"%>
<%@ page import="com.manager.ApplicationManagerService" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.vo.applicationManagerVOs.BankTypeVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Application Manager> Contractual Partner</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/partnerid_memberid.js"></script>--%>
<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<%
  Functions functions = new Functions();
  String partnerId = Functions.checkStringNull(request.getParameter("partnerid"));
  String bankName = Functions.checkStringNull(request.getParameter("bankName"));
  String contractualPartnerId = Functions.checkStringNull(request.getParameter("contractualpartid"));
  String contractualPartnerName = Functions.checkStringNull(request.getParameter("contractualpartname"));

  String str="ctoken=" + ctoken;

  if(partnerId!=null)str = str + "&partnerid=" + partnerId;
  if(bankName!=null)str = str + "&bankName=" + bankName;
  if(contractualPartnerId!=null)str = str + "&contractualpartid=" + contractualPartnerId;
  if(contractualPartnerName!=null)str = str + "&contractualpartname=" + contractualPartnerName;

  String partnerid = "";

  int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
  int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >

      <div class="panel-heading" >
        Contractual Partner
        <div style="float: right;">
            <form action="/icici/addContractualPartner.jsp?ctoken=<%=ctoken%>" method="POST">
                <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Add Contractual Partner
                </button>
            </form>
        </div>
      </div><br>

      <form action="/icici/servlet/ContractualPartner?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          ApplicationManager applicationManager = new ApplicationManager();

          try
          {
            PartnerDAO partnerDAO = new PartnerDAO();
            TreeMap<String,String> partnerMap = applicationManager.selectPartnerIdAndPartnerName();

            TreeMap<String,BankTypeVO> bankTypeTreeMap = ApplicationManagerService.getAllPartnerBankTypeMap();
            TreeMap<Integer, String> partneriddetails=applicationManager.getpartnerDetails();

        %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Partner ID*</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
<%--
                    <input name="partnerid" class="txtbox" id="allpid">
--%>
                    <select size="1" name="partnerid" id="bank" class="txtbox">
                      <option value="0">Select Partner ID</option>
                      <%
                        if(partneriddetails.size()>0)
                        {
                          Iterator iterator = partneriddetails.entrySet().iterator();

                          while (iterator.hasNext())
                          {
                            Map.Entry<Integer, String> mapEntry = (Map.Entry<Integer, String>) iterator.next();
                            String key = String.valueOf(mapEntry.getKey());
                            String value = mapEntry.getValue();

                            String selected = "";
                            if(key.equalsIgnoreCase(partnerId))
                              selected = "selected";
                            else
                              selected = "";
                      %>
                      <option value="<%=key%>" <%=selected%> ><%=key%>-<%=value%></option>
                      <%
                          }
                        }
                      %>
                    </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Bank Name*</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                      <select size="1" id="accountid" name="bankName" class="txtbox"  style="width:200px;">
                        <option data-bank="all" value="0" default>--All--</option>
                        <%

                          for(String gatewayType : bankTypeTreeMap.keySet())
                          {
                            BankTypeVO gt = bankTypeTreeMap.get(gatewayType);
                            String gatewayPartner = gt.getPartnerId();
                            String gatewayValue = gt.getBankName();

                            String isSelected = "";
                            if(gatewayValue.equalsIgnoreCase(bankName)){isSelected = "selected";}
                        %>
                        <option data-bank="<%=gatewayPartner%>" value="<%=gatewayValue%>" <%=isSelected%>> <%=gatewayValue+ "-" +gatewayPartner%> </option>
                        <%
                          }
                        %>

                      </select>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:10px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>

                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    String errormsg=(String)request.getAttribute("error");

    if(errormsg!=null)
    {
      out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
    }

    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
      currentblock="1";

    try
    {
      records=Integer.parseInt((String)hash.get("records"));
      totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
    }
    catch(Exception ex)
    {

    }
    if(hash!=null)
    {
      hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
  %>
  <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0">Sr No</td>
      <td valign="middle" align="center" class="th0">Partner Id</td>
      <td valign="middle" align="center" class="th0">Bank Name</td>
      <td valign="middle" align="center" class="th0">Contractual PartnerId</td>
      <td valign="middle" align="center" class="th0">Contractual Partner Name</td>
      <td valign="middle" align="center" class="th0">Action</td>
    </tr>
    </thead>
    <%
      String style="class=td1";

      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);

        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

        if(pos%2==0)
        {
          style="class=tr0";
        }
        else
        {
          style="class=tr1";
        }

        temphash=(Hashtable)hash.get(id);
        out.println("<tr>");
        out.println("<td "+style+" align=\"center\">&nbsp;"+srno+ "</td>");
        out.println("<td "+style+" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid"))+"</td>");
        out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("bankname"))+"</td>");
        out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contractual_partnerid"))+"</td>");
        out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contractual_partnername"))+"</td>");
        out.println("<td "+style+" align=\"center\">");
        out.println("<form action=\"/icici/servlet/UpdateContractualPartner?ctoken="+ctoken+"\" method=\"post\">" +
              "<input type=\"hidden\" name=\"partnerid\" value=\""+temphash.get("partnerid")+"\" >" +
              "<input type=\"hidden\" name=\"bankname\" value=\""+temphash.get("bankname")+"\" >" +
              "<input type=\"hidden\" name=\"contractualpartnerid\" value=\""+temphash.get("contractual_partnerid")+"\" >" +
              "<input type=\"hidden\" name=\"contractualpartnername\" value=\""+temphash.get("contractual_partnername")+"\" >" +
              "<input type=\"hidden\" name=\"action\" value=\"modify\">" +
              "<input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td>");
        out.println("</td>");
        out.println("</tr>");
      }
    %>
  </table>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="ContractualPartner"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
    }
  %>
</div>
<%
  }
  catch (PZDBViolationException e)
  {
    out.println("<div class=\"reporttable\">");
    out.println(Functions.NewShowConfirmation("Result","Internal Error occurred : Please contact your Admin"));
    out.println("</div>");
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