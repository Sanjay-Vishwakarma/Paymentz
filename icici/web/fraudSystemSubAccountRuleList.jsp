<%@ page import="com.directi.pg.Database,
                 com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%@ page import="java.sql.ResultSet" %>
<%--
  Created by IntelliJ IDEA.
  User: kiran
  Date: 14/7/15
  Time: 5:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger logger=new Logger("fraudSystemSubAccountRuleList.jsp");
%>
<%
  String fssubaccountid =Functions.checkStringNull(request.getParameter("fssubaccountid"));
  if (fssubaccountid == null){fssubaccountid = "";}

  String status = Functions.checkStringNull(request.getParameter("status"));
  if (status == null){status = "";}
  String str1= "";

  str1="ctoken="+ctoken;
  if (fssubaccountid != null){str1 = str1 + "&fssubaccountid=" + fssubaccountid;}
  if (status != null){str1 = str1 + "&status=" + status;}

%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Fraud Rules> Manage SubAccount Rules</title>
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
        Fraud Sub Account Rule
        <div style="float: right;">
          <form action="/icici/manageFraudSystemSubAccountRule.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Fraud Account Rule Mapping" name="submit" class="addnewmember" style="width: 270px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;New Sub Account Rule Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/FraudSystemSubAccountRuleList?ctoken=<%=ctoken%>" method="post" name="forms">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
          <%
          String str="ctoken=" + ctoken;
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
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="15%" class="textb" >Fraud Sub Account</td>
                  <td width="12%" class="textb">
                    <select name="fssubaccountid" class="txtbox" style="width: 200px;">
                      <option value="" selected>Select Fraud Sub Account</option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn = Database.getConnection();
                          StringBuffer qry = new StringBuffer("select subacc.fssubaccountid,fs.fsname,acc.accountname,subaccountname from fsaccount_subaccount_mapping as subacc,fraudsystem_account_mapping as acc,fraudsystem_master as fs where subacc.fsaccountid=acc.fsaccountid and acc.fsid=fs.fsid");
                          PreparedStatement ps = conn.prepareStatement(qry.toString());
                          ResultSet rs = ps.executeQuery();
                          while(rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("subacc.fssubaccountid")+"\">"+rs.getInt("subacc.fssubaccountid")+" : "+rs.getString("subaccountname")+" ( "+rs.getString("acc.accountname")+" : "+rs.getString("fs.fsname")+" ) "+"</option>");
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
                  <td width="3%" class="textb" >&nbsp;</td>
                  <td width="5%" class="textb" >Status</td>
                  <td width="12%" class="textb">
                    <select name="status" size="1" class="txtbox" style="width: 130px;">
                      <option value="" selected="">Select Status</option>
                      <option value="Enable">Enable</option>
                      <option value="Disable">Disable</option>
                    </select>
                  </td>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="10%" class="textb" align="center">
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
  <%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");
    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
      out.println(error);
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
  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="20%" class="th1">Sub Account Name/Website</td>
      <td width="20%" class="th1">Rule</td>
      <td width="10%" class="th1">Status</td>
      <td width="10%" class="th0">Score</td>
      <td width="10%" class="th0">Limit</td>
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
      <td align="center" <%=style%>><%=temphash.get("subaccountname")%></td>
      <td align="center" <%=style%>><%=temphash.get("rulename")%></td>
      <td align="center" <%=style%>><%=temphash.get("status")%></td>
      <td align="center" <%=style%>><%=temphash.get("score")%></td>
      <td align="center" <%=style%>><%=temphash.get("value")==null?"-":temphash.get("value")%></td>
      <td align="center" <%=style%>><form action="/icici/servlet/ActionFraudSystemSubAccountRule?ctoken=<%=ctoken%>"  method="post" name="from_"+<%=temphash.get("ruleid")%>>
        <input type="hidden" name="ruleid" value="<%=temphash.get("ruleid")%>"><input type="hidden" name="fssubaccountid" value=<%=temphash.get("fssubaccountid")%>>
        <input type="hidden" name="action" value="modify"><input type="submit" class="gotoauto" value="Modify">
      </form></td>
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
          <jsp:param name="str" value="<%=str1%>"/>
          <jsp:param name="page" value="FraudSystemSubAccountRuleList"/>
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
         out.println(Functions.NewShowConfirmation("Result","No records found."));
      }
  %>
</div>
<%
  }
%>
</body>
</html>