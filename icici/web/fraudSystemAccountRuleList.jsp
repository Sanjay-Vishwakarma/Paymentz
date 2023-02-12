<%@ page import="com.directi.pg.Database,com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 14/7/15
  Time: 3:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String fsaccountid = Functions.checkStringNull(request.getParameter("fsaccountid"));
  Logger logger = new Logger("fraudSystemAccountRuleList.jsp");
  String status = Functions.checkStringNull(request.getParameter("status"));
  String str ="";

  str="ctoken="+ctoken;
  if(fsaccountid == null) { fsaccountid = ""; }
  if(status == null) { status = ""; }

  if(fsaccountid != null) { str = str + "&fsaccountid" +fsaccountid; }
  if(status != null)      {    str = str + "&status" +status; }
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Rules> Manage Account Rules</title>
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
        Fraud Account Rule Mapping
        <div style="float: right;">
          <form action="/icici/manageFraudSystemAccountRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add Fraud Account Rule Mapping" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Fraud Account Rule Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemAccountRuleList?ctoken=<%=ctoken%>" method="post" name="forms">
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
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud System Account</td>
                  <td width="12%" class="textb">
                    <select name="fsaccountid" class="txtbox" style="width: 200px;">
                      <option value="" selected>Select Fraud System Account </option>
                      <%
                        Connection conn= null;
                        try
                        {
                          conn = Database.getConnection();
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
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb" align="center">Status</td>
                  <td width="12%" class="textb">
                    <select name="status" size="1" class="txtbox" style="width: 130px;">
                      <option value="">Select Status</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
                </tr>
                <tr><td colspan="4" height="25px">&nbsp;</td></tr>
              </table>
            </td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
  <%  String errormsg1 = (String)request.getAttribute("message");
    if (errormsg1 == null)
    {
      errormsg1 = "";
    }
    else
    {
      out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
      out.println(errormsg1);
      out.println("</font></td></tr></table>");
    }
  %>
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
    if(records>0)
    {

  %>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="20%" class="th1">Account Name</td>
      <td width="20%" class="th1">Rule Name</td>
      <td width="10%" class="th1">Status</td>
      <td width="10%" class="th1">Score</td>
      <td width="10%" class="th1">Limit</td>
      <td width="5%" class="th1">Action</td>
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
            %>
    <tr>
      <td align="center" <%=style%>><%=srno%></td>
      <td align="center" <%=style%>><%=temphash.get("accountname")%></td>
      <td align="center" <%=style%>><%=temphash.get("rulename")%></td>
      <td align="center" <%=style%>><%=temphash.get("status")%></td>
      <td align="center" <%=style%>><%=temphash.get("score")%></td>
      <td align="center" <%=style%>><%=temphash.get("value")==null?"-":temphash.get("value")%></td>
      <td align="center" <%=style%>>
        <form action="/icici/servlet/ActionFraudSystemAccountRule?ctoken=<%=ctoken%>"  method="post">
          <input type="hidden" name="ruleid" value="<%=temphash.get("ruleid")%>">
          <input type="hidden" name="fsaccountid" value=<%=temphash.get("fsaccountid")%>>
          <input type="hidden" name="action" value="modify">
          <input type="submit" class="gotoauto" value="Modify">
        </form>
      </td>
    </tr>
      <%
        }
    %>
    <table align=center valign=top><tr>
      <td align=center>
        <jsp:include page="page.jsp" flush="true">
          <jsp:param name="numrecords" value="<%=totalrecords%>"/>
          <jsp:param name="numrows" value="<%=pagerecords%>"/>
          <jsp:param name="pageno" value="<%=pageno%>"/>
          <jsp:param name="str" value="<%=str%>"/>
          <jsp:param name="page" value="FraudSystemAccountRuleList"/>
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
        out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg") +"No Records Found."));
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result","No Records Found."));
      }
  %>
</div>
<%
  }
%>
</body>
</html>
