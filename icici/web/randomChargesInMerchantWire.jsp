<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 23/7/15
  Time: 6:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title> Merchant Random Charges</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger=new Logger("randomChargesInMerchantWire");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Merchant Wire Manager
        <div style="float: right;">
          <form action="/icici/addwire.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New wire
            </button>
          </form>
        </div>
      </div><br>
      <form action="/icici/servlet/WireList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <%
          String str="";
          String fdate=null;
          String tdate=null;
          String fmonth=null;
          String tmonth=null;
          String fyear=null;
          String tyear=null;
          String toid=null;
          String accountid=null;
          String terminalid=null;
          try
          {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
            toid = ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",10,true);
            accountid = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);
            terminalid = ESAPI.validator().getValidInput("accountid",request.getParameter("tyear"),"Numbers",10,true);
          }
          catch(ValidationException e)
          {
            logger.error("Date Format Exception while select",e);
          }
          Calendar rightNow = Calendar.getInstance();
          if (fdate == null) fdate = "" + 1;
          if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
          if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
          if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
          if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
          if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
          str = str + "ctoken=" + ctoken;
          if (fdate != null) str = str + "&fdate=" + fdate;
          if (tdate != null) str = str + "&tdate=" + tdate;
          if (fmonth != null) str = str + "&fmonth=" + fmonth;
          if (tmonth != null) str = str + "&tmonth=" + tmonth;
          if (fyear != null) str = str + "&fyear=" + fyear;
          if (tyear != null) str = str + "&tyear=" + tyear;
          if (toid != null) str = str + "&toid=" + toid;
          if (accountid != null) str = str + "&accountid=" + accountid;
          if (terminalid != null) str = str + "&terminalid=" + terminalid;

          int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
          int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

        %>
        <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
          <tr><td colspan="12">&nbsp;</td></tr>
          <tr>
            <td colspan="2" class="textb">Member ID</td>
            <td colspan="2" class="textb">
              <select name="toid" class="txtbox"><option value="" selected></option>
                  <%
            Hashtable<String, String> members = ChargesUtils.getMembers();
            Iterator it = members.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry pair = (Map.Entry) it.next();
                 out.println("<option value=\"" + pair.getKey() + "\" >" + pair.getKey() + " - " + pair.getValue() + "</option>");
            }
            %>
            </td>
            <td colspan="2" class="textb">Account ID</td>
            <td colspan="2" class="textb" >
              <select name="accountid" class="txtbox" style="width: 430px"><option value="" selected></option>
                <%
                  Hashtable<String, String> map = GatewayAccountService.getMerchantDetails();
                  it = map.entrySet().iterator();
                  while (it.hasNext())
                  {
                    Map.Entry pair = (Map.Entry) it.next();
                    out.println("<option value=\"" + pair.getKey() + "\">" + pair.getValue() + "</option>");
                  }
                %>
              </select>
            </td>
            <td colspan="2" class="textb">Terminal ID</td>
            <td colspan="2" class="textb">
              <select name="terminalid" ><option value="" selected></option>
                <%
                  Connection conn=null;
                  try
                  {
                    conn = Database.getConnection();
                    String query = "SELECT terminalid FROM member_account_mapping";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next())
                    {
                      out.println("<option value=\"" + rs.getString("terminalid") + "\"  >" + rs.getString("terminalid") + "</option>");
                    }
                  }
                  catch (Exception e)
                  {
                    logger.error("Exception::"+e);
                  }
                  finally
                  {
                    Database.closeConnection(conn);
                  }
                  int year = Calendar.getInstance().get(Calendar.YEAR);
                %>
              </select>
            </td>
            </td>
          </tr>
          <tr><td colspan="12">&nbsp;</td></tr>
          <tr>
            <td colspan="2" class="textb" >From</td>
            <td colspan="2" class="textb">
              <select size="1" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                <%
                  if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                  else
                    out.println(Functions.printoptions(1, 31));
                %>
              </select>
              <select size="1" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                <%
                  if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                  else
                    out.println(Functions.printoptions(1, 12));
                %>
              </select>
              <select size="1" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                <%
                  if (fyear != null)
                    out.println(Functions.yearoptions(2005, year, fyear));
                  else
                    out.println(Functions.printoptions(2005, year));
                %>
              </select>
            </td>
            <td colspan="2" class="textb" >To</td>
            <td colspan="2" class="textb">
              <select size="1" name="tdate" >
                <%
                  if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                  else
                    out.println(Functions.printoptions(1, 31));
                %>
              </select>
              <select size="1" name="tmonth" >
                <%
                  if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                  else
                    out.println(Functions.printoptions(1, 12));
                %>
              </select>
              <select size="1" name="tyear" >
                <%
                  if (tyear != null)
                    out.println(Functions.yearoptions(2005, year, tyear));
                  else
                    out.println(Functions.printoptions(2005, year));
                %>
              </select>
            </td>
            <td colspan="2" class="textb">Is Paid</td>
            <td colspan="2" class="textb">
              <Select name="isrr"><option value=""> </option><option value="N">Unpaid </option> <option value="Y"> Paid </option> </Select>
            </td>
          </tr>
          <tr>
            <td colspan="12" class="textb" >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="12" class="textb" align="right">
              <button type="submit" class="buttonform">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
              </button></td>
            </td>
          </tr>
          <tr>
            <td colspan="12" class="textb">&nbsp;</td>
          </tr>
        </table>
      </form>
    </div>
  </div>
</div>
<div class="reporttable">
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
  <div id="containrecord" align="center">
    <h4>Random Charges Applied In Merchant Wire</h4>
  </div>
  <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="4%" class="th0">Sr No</td>
      <td width="5%"  class="th0">Merchant ID</td>
      <td width="5%" class="th1">Terminal ID</td>
      <td width="15%" class="th0">Charge Name</td>
      <td width="4%" class="th1">Charge Rate</td>
      <td width="4%" class="th1">Charge Counter</td>
      <td width="4%" class="th1">Charge Amount</td>
      <td width="4%" class="th1">Charge Value</td>
      <td width="5%" class="th1">Charge Value Type</td>
      <td width="20%" class="th1">Charge Remark</td>
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
            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargerate"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargecounter"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeamount"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargevalue"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("valuetype"))+"</td>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeremark"))+"</td>");
            out.println("</tr>");
        }
    }
      else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
      {
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("statusMsg")));
      }
      else
      {
         out.println(Functions.NewShowConfirmation("Result", "Random Charges Not Founds For Current Wire."));
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
