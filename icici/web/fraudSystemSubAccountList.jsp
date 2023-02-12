<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 14/7/15
  Time: 12:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String fsAccountId = Functions.checkStringNull(request.getParameter("fsaccountid"));
  String fsSubAccountId = Functions.checkStringNull(request.getParameter("fssubaccountid"));
  Logger logger = new Logger("fraudSystemSubAccountList.jsp");

  if(fsAccountId == null) { fsAccountId = ""; }
  if(fsSubAccountId == null) { fsSubAccountId = ""; }

  String str="ctoken=" + ctoken;
  if(fsAccountId != null) { str = str + "&fsaccountid=" +fsAccountId; }
  if(fsSubAccountId != null) { str = str + "&fssubaccountid="+fsSubAccountId; }
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Fraud Sub Accounts</title>
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
        Fraud System Sub Accounts
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccountSubAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New Fraud Sub Account" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Fraud Sub Account
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemSubAccountList?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
                <tr>
                  <td width="1%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud System Account</td>
                  <td width="2%" class="textb"></td>
                  <td width="12%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 200px;">
                      <option value="" selected>ALL</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn= Database.getConnection();
                          ResultSet rs = Database.executeQuery("select fsaccountid,accountname from fraudsystem_account_mapping",conn);
                          while (rs.next())
                          {
                            out.println("<option value="+rs.getString("fsaccountid")+">"+rs.getString("fsaccountid")+" - "+rs.getString("accountname")+"</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </td>
                  <td width="5%" class="textb">&nbsp;</td>
                  <td width="13%" class="textb" >Fraud Sub Account Id</td>
                  <td width="2%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input  maxlength="50" type="text" name="fssubaccountid"  value="" class="txtbox" style="width: 170px;">
                  </td>
                  <td width="7%" class="textb"></td>
                  <td width="11%" class="textb" align="center">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
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
    <tr height="50">
      <td width="10%" class="th1">Sr No.</td>
      <td width="25%" class="th0">Fraud Sub Account/Website</td>
      <td width="25%" class="th1">Fraud System Account Name</td>
      <td width="25%" class="th1">Fraud System Name</td>
      <td width="25%" class="th1">Action</td>
    </tr>
    </thead>
    <%
      String style="class=td1";
      String ext="light";

      for(int pos=1;pos<=records;pos++)
      {
        String id=Integer.toString(pos);

        int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

        if(pos%2==0)
        {
          style="class=tr0";
          ext="dark";
        }
        else
        {
          style="class=tr1";
          ext="light";
        }

        temphash=(Hashtable)hash.get(id);
        out.println("<tr height=20>");
        out.println("<td align=center "+style+">"+srno+ "</td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("subaccountname"))+"<input type=\"hidden\" name=\"subaccountname\" value=\""+temphash.get("subaccountname")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("accountname"))+"<input type=\"hidden\" name=\"accountname\" value=\""+temphash.get("accountname")+"\"></td>");
        out.println("<td align=center "+style+">" +ESAPI.encoder().encodeForHTML((String) temphash.get("fsname"))+"<input type=\"hidden\" name=\"fsname\" value=\""+temphash.get("fsname")+"\"></td>");
        out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/ActionFraudSystemSubAccount?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("fssubaccountid")+"\"><input type=\"hidden\" name=\"fsaccountid\" value=\""+temphash.get("fsaccountid")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"gotoauto\" value=\"Modify\"></form></td></td>");
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
        <jsp:param name="page" value="FraudSystemSubAccountList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>
  <%
    }
    else if((String)request.getAttribute("statusMsg")!=null)
    {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Result", "No Records Found."));
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
