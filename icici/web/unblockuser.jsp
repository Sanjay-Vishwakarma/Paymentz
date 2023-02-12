<%--
  Created by IntelliJ IDEA.
  User: NIKET
  Date: 11-06-2016
  Time: 15:07
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.DefaultUser" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.List" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%!private Functions functions = new Functions();%>
<html>
<head>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit !important;
      }
    }
  </style>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
  </script>
  <script>

    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });
  </script>
  <script language="javascript">

    function confirmation()
    {

      if(confirm("Do u really want to unlock this account"))
      {
        return true;
      }
      return false;
    }
  </script>
</head>
<title>Account History> Unblock Account</title>
<body class="bodybackground">
<%--<br><br><br><br><br><br><br><br>--%>
<%--<div class="qwipitab" style="margin-top: 0px">--%>
 <%-- <h3 align="center"><p class="textb">Blocked Account List</p> </h3>--%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<h3 align="center"><p class="textb">Blocked Account List</p> </h3>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Blocked Account List
      </div>
      <form action="/icici/servlet/BlockedUserList?ctoken=<%=ctoken%>" method="post" name="forms" onsubmit="">
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <br>
      <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
        <tr>
          <td>
            <%
              /*String errormsg1 = (String) request.getAttribute("error");
              if (errormsg1 != null)
              {
                out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
              }
              String errormsg = (String) request.getAttribute("cbmessage");
              if (errormsg == null)
                errormsg = "";
              out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
              out.println(errormsg);
              out.println("</b></font></td></tr></table>");*/

              String login = request.getParameter("login")==null?"":request.getParameter("login");

              String str="ctoken=" + ctoken;

              if(login!=null)str = str + "&login=" + login;

              int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
              int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

            %>
            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
              <br/>
              <tr>
                <td width="2%" class="textb">&nbsp;</td>
                <td width="20%" class="textb" for="mid">Login Name</td>
                <td width="0%" class="textb"></td>
                <td width="22%" class="textb">
                  <input name="login" id="" value="<%=login%>" class="txtbox" autocomplete="on" >
                </td>
                <td width="10%" class="textb">&nbsp;</td>
                <td width="40%" class="textb"></td>
                <td width="5%" class="textb"></td>
                <td width="50%" class="textb">
                  <button type="submit" class="buttonform" style="margin-left:40px; ">
                    <i class="fa fa-clock-o"></i>
                    &nbsp;&nbsp;Search
                  </button>
                </td>
              </tr>
            </table>
            <br/>
          </td>
        </tr>
      </table>
      </form>
    </div>
  </div>
  </div>

<div class="reporttable">

  <%
    /*String error1=(String ) request.getAttribute("error");
    if(error1 !=null)
    {
      out.println("<BR>");
      out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" ><b>");
      out.println(error1);
      out.println("</b></font></td></tr></table>");
      out.println("<BR>");
    }*/

      Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;
        String errormsg=(String)request.getAttribute("message");

        if(errormsg!=null)
        {
            out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
        }

        String msg=(String)request.getAttribute("msg");

        if(msg!=null)
        {
            out.println("<center><font class=\"textb\">"+msg+"</font></center>");
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
<%--  <%
    List<DefaultUser> defaultUsers = null;
    PaginationVO paginationVO = null;

    if(request.getAttribute("DefaultUsers")!=null)
    {
      defaultUsers= (List<DefaultUser>) request.getAttribute("DefaultUsers");
    }

    if(!functions.isValueNull(request.getParameter("MES")))
    {
      if(request.getAttribute("error")!=null)
      {
        ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
        for (ValidationException errorList : error.errors())
        {
          out.println("<center><font class=\"textb\">" + errorList.getMessage() + "</font></center>");
        }
      }
    }
    else if(request.getAttribute("Update")!=null && ("true".equals((String) request.getAttribute("Update"))))
    {
      out.println("<center><font class=\"textb\"></font></center>");
    }


    if(defaultUsers!=null && defaultUsers.size()>0)
    {
      paginationVO= (PaginationVO) request.getAttribute("PaginationVO");
      paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

      int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
  %>
--%>
 <%-- <form action="/icici/servlet/UnlockUser" method="post">
    <input type="hidden" name="ctoken" value="<%=ctoken%>"/>
  <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center" class="th0">Sr no</td>
      <td valign="middle" align="center" class="th0">Account Id</td>
      <td valign="middle" align="center" class="th0" >Login name</td>
      <td valign="middle" align="center" class="th0" >Role</td>
      <td valign="middle" align="center" class="th0" >Action</td>
    </tr>
    </thead>--%>

    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0">Sr No</td>
        <td valign="middle" align="center" class="th0">Account Id</td>
        <td valign="middle" align="center" class="th0">Login Name</td>
        <td valign="middle" align="center" class="th0">Role</td>
        <td valign="middle" align="center" class="th0">Action</td>
      <tr>
      </thead>
    <%
      String style="class=td1";

      for(int pos=1;pos<=records;pos++)
      {
        String id = Integer.toString(pos);

        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

        if (pos % 2 == 0)
        {
          style = "class=tr0";
        }
        else
        {
          style = "class=tr1";
        }
        temphash = (Hashtable) hash.get(id);
        out.println("<tr>");
        out.println("<td align=center " + style + ">&nbsp;" + srno + "</td>");
        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid")) + "<input type=\"hidden\" name=\"accountid\" value=\"" + temphash.get("accountid") + "\"></td>");
        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("login")) + "<input type=\"hidden\" name=\"login\" value=\"" + temphash.get("login") + "\"></td>");
        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("roles")) + "<input type=\"hidden\" name=\"roles\" value=\"" + temphash.get("roles") + "\"></td>");
        out.println("<td align=center "+style+"><form action=\"/icici/servlet/UnlockUser?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"accountid\" value=\"" + temphash.get("accountid") + "\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"Unblock\" value=\"Unblock\" class=\"gotoauto\" id=\"unblock\"  onclick= \"return confirmation();\"></form></td>");
        out.println("</tr>");
      }
     /* String style="class=td1";
      String ext="light";

      for(DefaultUser defaultUser:defaultUsers)
      {
        out.println("<tr>");
        out.println("<td "+style+">&nbsp;"+srno+ "</td>");
        out.println("<td align=\"center\" "+style+">&nbsp;" +defaultUser.getAccountId()+"</td>");
        out.println("<td align=\"center\" "+style+">&nbsp;" +defaultUser.getAccountName()+"</td>");
        out.println("<td align=\"center\" "+style+">&nbsp;" +defaultUser.getRoles()+"</td>");
        out.println("<td align=\"center\" "+style+" >");
        out.println("<button type=submit name='action' value=\""+defaultUser.getAccountId()+"_Update\" class=\"goto\" onclick= 'return confirmation();'>UnBlock</buuton>");
        out.println("</td>");
        out.println("</tr>");

        srno++;
      }*/
    %>
  </table>
  </form>

  <br>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true" >
        <jsp:param name="numrecords" value="<%=totalrecords/*paginationVO.getTotalRecords()*/%>" />
        <jsp:param name="numrows" value="<%=pagerecords/*paginationVO.getRecordsPerPage()*/%>"/>
        <jsp:param name="pageno" value="<%=pageno/*paginationVO.getPageNo()*/%>"/>
        <jsp:param name="str" value="<%=str/*paginationVO.getInputs()*/%>"/>
        <jsp:param name="page" value="BlockedUserList"/>
        <jsp:param name="currentblock" value="<%=currentblock/*paginationVO.getCurrentBlock()*/%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </tr>
  </table>

  <%
    /*}
    else if(functions.isValueNull(request.getParameter("MES")))
    {
      out.println(Functions.NewShowConfirmation("Failure","No Records Found"));
      out.println("<br>");
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry", "No Locked User Available"));
    }*/
    }
    else
    {
      out.println(Functions.NewShowConfirmation("Sorry","No Locked User Available"));
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