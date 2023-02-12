<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="org.owasp.esapi.reference.DefaultUser" %>


<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 9/4/2021
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title></title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>
</head>
<script type="text/javascript">
  function getSave() {
    document.getElementById('Save').submit();
  }
</script>

<%!
  private static Logger log = new Logger("resetpassword.jsp");
%>
<%
  Functions functions = new Functions();
  String memberid = "";
  String errormsg = (String) request.getAttribute("cbmessage");
  //String error = (String) .getAttribute("errormessage");
    //String forgotpassword=(String)session.getAttribute("TemparoryPassword");
  String merchantid = "";

  if (functions.isValueNull(request.getParameter("memberid")))
    memberid = request.getParameter("memberid");
  else
    memberid = "";
%>
<body background="bodybackground">
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
 /* User user=null;
  if(ctoken !=null)
  {
    if(!Functions.validateCSRFAnonymos(ctoken,user))
    {
      response.sendRedirect("/icici/logout.jsp");
      return;
    }

  }
  else if(user!=null)
  {

    ctoken = user.getCSRFToken();

  }
  else
  {
    user =   new DefaultUser(User.ANONYMOUS.getName());
    ctoken = (user).resetCSRFToken();

  }

  session.setAttribute("Anonymous", user);*/
  if (com.directi.pg.Admin.isLoggedIn(session)){
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Reset Password
        <div style="float: right;">
          <form action="/icici/merchants.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-sign-in" ></i> &nbsp;&nbsp; Merchant Details
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/ResetPassword?ctoken=<%=ctoken%>" method="POST" >
        <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
        <input type="hidden"  value="<%=memberid%>" name="memberid">

        <table align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left: 1.5%;margin-right: 2.5%;">
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="11%" class="textb"> Username or Email </td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input class="txtbox" name="username"  value="" >
                  </td>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" ></td>
                  <td width="3%" class="textb"></td>
                  <td width="10%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>&nbsp;&nbsp;Reset Password
                    </button>
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
<div class="reporttable" style="overflow: auto">

    <%
        String forgotpassword=(String)request.getAttribute("TemparoryPassword");
        String fmsg=(String)request.getAttribute("fmsg");
            if(fmsg!=null)
            {
                out.println("<h5 class=\"textb\" style=\"text-align: center;\">" + "Invalid Username"+ "</h5>");
            }

        if (request.getAttribute("TemparoryPassword") != null){
            out.println("<h5 class=\"textb\" style=\"text-align: center;\">"+ "Temporary Password::::::" + forgotpassword+ "</h5>");
        }
    else if (functions.isValueNull(forgotpassword)){
      out.println("<h5 class=\"textb\" style=\"text-align: center;\">"+"Temporary Password::::::" + forgotpassword+ "</h5>");}

    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
    }
    }
  else{
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</div>
</body>
</html>
