<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ page import="org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%!
  private static Logger log=new Logger("adminTransactionDetails.jsp");
  Functions functions = new Functions();
%>
<%
  String fdate=null;
  String tdate=null;
  String fmonth=null;
  String tmonth=null;
  String fyear=null;
  String tyear=null;
  String startTime = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime = Functions.checkStringNull(request.getParameter("endtime"));
  Calendar rightNow = Calendar.getInstance();
  try
  {

    fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
    tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
    fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
    tmonth = ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
    fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
    tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);


  }
  catch(ValidationException e)
  {


  }
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";
  if (fdate == null) fdate = "" + (rightNow.get(Calendar.DATE)-1);
  if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
  if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
  if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
  if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
  if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
  String str = "";
  String currentyear= ""+rightNow.get(rightNow.YEAR);
%>
<%--
  Created by IntelliJ IDEA.
  User: vivek
  Date: 8/27/2019
  Time: 3:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title>Transaction Management> Export SFTP Transaction Detail</title>
</head>
<body>
<form name="form" method="post" action="/icici/servlet/ExportSFTPTransaction?ctoken=<%=ctoken%>">
  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
  <div class="row">
    <div class="col-lg-12">
      <div class="panel panel-default" >
        <div class="panel-heading" >
          Export SFTP Transactions Details
        </div><br>
        <%
          String error=(String ) request.getAttribute("errormessage");
          if(error !=null)
          {
//out.println(error);
            out.println("<center><p class=\"textb\">"+error+"</center>");
          } %>
        <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.0%;margin-right: 2.5% ">

          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >From</td>
                  <td width="15%" class="textb">
                    <select size="1" name="fdate">
                      <%
                        if (fdate != null)
                          out.println(Functions.dayoptions(1, 31, fdate));
                        else
                          out.println(Functions.printoptions(1, 31));
                      %>
                    </select>
                    <select size="1" name="fmonth" >
                      <%
                        if (fmonth != null)
                          out.println(Functions.newmonthoptions(1, 12, fmonth));
                        else
                          out.println(Functions.printoptions(1, 12));
                      %>
                    </select>
                    <select size="1" name="fyear" >
                      <%
                        if (fyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb">
                    <input type="text" size="6" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>"/>
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >To</td>
                  <td width="15%" class="textb">
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

                    <select size="1" name="tyear">

                      <%
                        if (tyear != null)
                          out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                        else
                          out.println(Functions.printoptions(2005, 2013));
                      %>
                    </select>
                  </td>
                  <td width="10%" class="textb">
                    <input type="text" size="6" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>"/>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <button type="submit" class="buttonform">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Export
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >&nbsp;</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">&nbsp;</td>

                </tr>
              </table>
            </td>
          </tr>
        </table>

      </div>
    </div>
  </div>
</form>
</body>
</html>
