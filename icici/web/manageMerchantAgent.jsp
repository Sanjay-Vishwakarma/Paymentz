<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.List" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip1
  Date: 6/30/15
  Time: 4:01 PM
  To change this template use File | Settings | File Templates
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <link rel="stylesheet" href="/resources/demos/style.css">
  <title></title>

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
  Logger logger = new Logger("manageMerchantAgent.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
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
          <form action="/icici/listMerchantAgent.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember" style="width:300px">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Mapping Master
            </button>
          </form>
        </div>
      </div>
      <%
        List errorList=(List)request.getAttribute("errorList");
        if(errorList!=null)
        {
          out.println("<table align=\"center\" font class=\"textb\"><tr><td><b>");
          out.println(errorList);
          out.println("</b></font></td></tr></table>");
        }
      %>
      <form action="/icici/servlet/ManageMerchantAgent?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" class="textb">&nbsp;</td>
                  <td style="padding: 3px" class="textb">Agent Id * :</td>
                  <td style="padding: 3px" class="textb">:</td>
                  <td style="padding: 3px"><input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on" >
                  <%--<select name="agentid" id="agnt"><option value="" selected></option>


                    <%
                      Connection conn = null;
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
                    %>
                  </select>--%></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb">Merchant Id * :</td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <input name="memberid" id="agnt-mid" value="<%=memberId%>" class="txtbox" autocomplete="on" >

                   <%-- <select name="memberid"><option value="" selected></option>--%>
                      <%--<%
                          query = "select memberid,company_name from members where activation='Y' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement( query );
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
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
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="43%" class="textb"></td>
                  <td style="padding: 3px" width="5%" class="textb"></td>
                  <td style="padding: 3px" width="50%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Save
                    </button>
                  </td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>

    </div>
  </div>
</div>
<%
  if(request.getAttribute("message")!=null)
  {
%>
<div class="reporttable">
  <%
    out.println(Functions.NewShowConfirmation("Result", (String) request.getAttribute("message")));
  %>
</div>
<%
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</body>
</html>