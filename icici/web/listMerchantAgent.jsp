<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 6/29/2015
  Time: 4:02 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title> Merchant Management > Merchant Agent Mapping</title>

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>

</head>
<body>
<%!
  private static Logger logger=new Logger("listMerchantAgent.jsp");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
    String memberId=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
    String str="ctoken=" + ctoken;
    if(memberId!=null){str=str+"&memberid="+memberId;}
    if(agentId!=null){str=str+"&agentid="+agentId;}
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Agent Mapping
        <div style="float: right;">
          <form action="/icici/manageMerchantAgent.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember" style="width:300px">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Mapping
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ListMerchantAgent?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          //String str="ctoken=" + ctoken;
          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
        %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Agent ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on" >
                    <%--<select name="agentid" class="txtbox" style="width: 172px;"><option value="" selected></option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn = Database.getConnection();
                          String  query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                          PreparedStatement pstmt = conn.prepareStatement( query );
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("agentid")+"\">"+rs.getInt("agentid")+" - "+rs.getString("agentName")+"</option>");
                          }
                        }
                        catch(Exception e)
                        {
                          logger.error("Exception:::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" >Member ID</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="agnt-mid" value="<%=memberId%>" class="txtbox" autocomplete="on" >
                    <%--<select name="memberid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn=null;
                        try
                        {
                          conn=Database.getConnection();
                          String query = "select memberid,company_name from members where activation='Y' ORDER BY memberid ASC";
                          PreparedStatement pstmt = conn.prepareStatement(query);
                          ResultSet rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("memberid") + "\">" + rs.getInt("memberid") + " - " + rs.getString("company_name") + "</option>");
                          }
                        }
                        catch(Exception e)
                        {
                          logger.error("Exception::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" >
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>
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
    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
      out.println(error);
    }
    Hashtable temphash=null;
    if (hash != null && hash.size() > 0)
    {
      int records=0;
      int totalrecords=0;
      String currentblock=request.getParameter("currentblock");
      if(currentblock==null)
        currentblock="1";
      try
      {
        records=Functions.convertStringtoInt((String)hash.get("records"),15);
        totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
      }
      catch(Exception ex)
      {
        logger.error("Records & TotalRecords is found null",ex);
      }

      if(records>0)
      {
  %>
  <table align=center width="70%" class="table table-striped table-bordered table-green dataTable" >
    <tr>
      <td valign="middle" align="center" class="th0">Sr No</td>
      <td valign="middle" align="center" class="th0">Merchant ID</td>
      <td valign="middle" align="center" class="th0">Company Name</td>
      <td valign="middle" align="center" class="th0">Agent ID</td>
      <td valign="middle" align="center" class="th0">Agent Name</td>
      <td valign="middle" align="center" class="th0">Creation On</td>
      <%--<td valign="middle" align="center" class="th0" colspan="2">Action</td>--%>
    </tr>
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
        out.println("<tr>");
        out.println("<td align=\"center\" "+style+">"+srno+ "</td>");
        out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
        out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("merchantname"))+"</td>");
        out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("agentid"))+"</td>");
        out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("agentname"))+"</td>");
        out.println("<td align=\"center\" "+style+">"+ESAPI.encoder().encodeForHTML((String)temphash.get("mappingon"))+"</td>");
//        out.println("<td align=\"center\" "+style+"><form action=\"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("id")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" disabled class=\"gotoauto\" value=\"Modify\"></form></td>");
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
        <jsp:param name="page" value="ListMerchantAgent"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
      }
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry","No records found."));
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