<%@ page import="com.directi.pg.Database,com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: supriya
  Date: 10/10/15
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Settings> Charges Interface> Partner Commission</title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger = new Logger("listPartnerCommission.jsp");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String partnerId=nullToStr(request.getParameter("partnerid"));
    String memberid=nullToStr(request.getParameter("memberid"));

    String str="ctoken=" + ctoken;
    if(memberid!=null){str=str+"&memberid="+memberid;}
    if(partnerId!=null){str=str+"&partnerid="+partnerId;}

%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Partner Commission
        <div style="float: right;">
          <form action="/icici/managePartnerCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Partner Commission" name="submit" style="width:250px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New Partner Commission
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ListPartnerCommission?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
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
                  <td width="11%" class="textb">PartnerId</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="partnerid" id="pid1" value="<%=partnerId%>" class="txtbox" autocomplete="on">
                   <%-- <select name="partnerid" class="txtbox"><option value="" selected></option>
                      <%
                        Connection conn = null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("partnerId") + "\">" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>--%>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb" for="mid" >MemberId</td>
                  <td width="5%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                   <%-- <select name="memberid" class="txtbox"><option value="" selected></option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          String query = "select memberid, company_name from members where activation='Y' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\"" + rs.getInt("memberid") + "\">" + rs.getInt("memberid") + " - " + rs.getString("company_name") + "</option>");
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception:::::" + e);
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
  <%  String errormsg1 = (String)request.getAttribute("message");
    String role="Admin";
    String username=(String)session.getAttribute("username");
    String actionExecutorId=(String)session.getAttribute("merchantid");
    String actionExecutorName=role+"-"+username;

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
    Functions functions=new Functions();
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
  <div id="containrecord"></div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="5%"  class="th0">Partner ID</td>
      <td width="5%"  class="th0">Member ID</td>
      <td width="5%" class="th1">Terminal ID</td>
      <td width="15%" class="th0">Commission ON</td>
      <td width="4%" class="th1">Commission Value</td>
      <td width="4%" class="th1">Start Date</td>
      <td width="4%" class="th1">End Date</td>
      <td width="4%" class="th1">Sequence Number</td>
      <td width="4%" class="th1" colspan="2">Action</td>
      <td width="4%" class="th1">Action Executor Id</td>
      <td width="4%" class="th1">Action Executor Name</td>
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
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" );
            temphash=(Hashtable)hash.get(id);
               if(functions.isValueNull((String)temphash.get("actionExecutorId")))
            {
                actionExecutorId=(String)temphash.get("actionExecutorId");
            }
            else
            {
                actionExecutorId="-";
            }
            if(functions.isValueNull((String)temphash.get("actionExecutorName")))
            {
                actionExecutorName=(String)temphash.get("actionExecutorName");
            }
            else
            {
                actionExecutorName="-";
            }


            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("commission_value"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("startdate")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+targetFormat.format(targetFormat.parse((String)temphash.get("enddate")))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("sequence_no"))+"</td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionPartnerCommission?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"commissionid\" value=\"" + temphash.get("id") + "\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"goto\" value=\"Modify\"></form></td>");
            out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionPartnerCommission?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"goto\" value=\"History\" disabled></form></td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
            out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
        }
    %>
    <table align=center valign=top><tr>
      <td align=center>
        <jsp:include page="page.jsp" flush="true">
          <jsp:param name="numrecords" value="<%=totalrecords%>"/>
          <jsp:param name="numrows" value="<%=pagerecords%>"/>
          <jsp:param name="pageno" value="<%=pageno%>"/>
          <jsp:param name="str" value="<%=str%>"/>
          <jsp:param name="page" value="ListPartnerCommission"/>
          <jsp:param name="currentblock" value="<%=currentblock%>"/>
          <jsp:param name="orderby" value=""/>
        </jsp:include>
      </td>
    </tr>
    </table>
      <%
      }
      else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>