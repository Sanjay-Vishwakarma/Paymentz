<%@ page import="com.directi.pg.Functions,
                 com.fraud.FraudSystemService"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 13/7/15
  Time: 4:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String fsid = Functions.checkStringNull(request.getParameter("fsid"));
  String accountName = Functions.checkStringNull(request.getParameter("accountname"));
  String str = "";

  if(fsid == null)
  {fsid = "";}

  if(accountName == null)
  {accountName = "";}

  str="ctoken="+ctoken;
  if(fsid != null) {str=str+"&fsid=" + fsid;}
  if(accountName != null) {str=str+"&accountname=" +accountName;}
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Management> Fraud SystemAccount Master</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        FraudSystem Accounts
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="New Fraud System Account" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Account
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="New PSP Fraud System Account" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Allocate Account
            </button>
          </form>
        </div>
        <div style="float: right;">
          <form action="/icici/managePartnerAllocatedAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Allocated Account List" name="submit" class="addnewmember" style="width: 200px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Allocated Account List
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemAccountList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
                <tr>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="9%" class="textb" align="center">Fraud System</td>
                  <td width="12%" class="textb">
                    <select name="fsid" class="txtbox" style="width: 200px;"><option value="" selected>Select Fraud System</option>
                      <%
                        Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                        Iterator it1 = fraudSystem.entrySet().iterator();
                        while (it1.hasNext())
                        {
                          Map.Entry pair = (Map.Entry)it1.next();
                          out.println("<option value=\""+pair.getKey()+"\">"+pair.getKey()+" - "+pair.getValue()+"</option>");
                        }
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb" align="center">Fraud System AccountId</td>
                  <td width="12%" class="textb">
                    <input  maxlength="255" type="text" name="fsaccountid"  value="" class="txtbox">
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" align="right">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="20px">&nbsp;</td></tr>
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
    Functions functions= new Functions();
    String msg = (String) request.getAttribute("msg");
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

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

    if(records > 0)
    {
  %>
  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr >
      <td width="5%" class="th1">Sr No</td>
      <td width="10%" class="th1">Fraud System Account ID</td>
      <td width="20%" class="th1">Account Name</td>
      <td width="15%" class="th1">User Name</td>
      <td width="10%" class="th1">isTest</td>
      <td width="15%" class="th0">Fraud System Name</td>
      <td width="15%" class="th0">Contact Email</td>
      <td width="30%" class="th1">Action</td>
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
        out.println("<tr height=20px>");

        out.println("<td align=center width=\"5%\" "+style+">&nbsp;"+srno+ "</td>");
        out.println("<td align=center width=\"10%\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("fsaccountid"))+"<input type=\"hidden\" name=\"fsaccountid\" value=\""+temphash.get("fsaccountid")+"\"></td>");
        out.println("<td align=center width=\"10%\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountname"))+"<input type=\"hidden\" name=\"accountname\" value=\""+temphash.get("accountname")+"\"></td>");
        out.println("<td align=center width=\"20%\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("username"))+"<input type=\"hidden\" name=\"username\" value=\""+temphash.get("username")+"\"></td>");
        out.println("<td align=center width=\"10%\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isTest"))+"<input type=\"hidden\" name=\"isTest\" value=\""+temphash.get("isTest")+"\"></td>");
        out.println("<td align=center width=\"20%\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fsname"))+"<input type=\"hidden\" name=\"fsname\" value=\""+temphash.get("fsname")+"\"></td>");
        out.println("<td align=center width=\"15%\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_email"))+"<input type=\"hidden\" name=\"fsname\" value=\""+temphash.get("contact_email")+"\"></td>");
        out.println("<td align=\"center\" width=\"30%\" "+style+">&nbsp;<form action=\"/icici/servlet/ActionFraudSystemAccount?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("fsaccountid")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td></td>");
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
        <jsp:param name="page" value="FraudSystemAccountList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else if(functions.isValueNull(msg))
    {
      out.println(Functions.NewShowConfirmation("Result",msg.toString()));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
  %>
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
