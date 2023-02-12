<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultipleMemberUtill" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title>Merchant Management > Merchant's User Management</title>
    <script language="javascript">
        function DoReverse(ctoken)
        {
            if (confirm("Do you really want to Delete this User."))
            {
                return true;
            }
            else
                return false;
        }
    </script>

</head>
<body>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%
  /*MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();*/
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
          Merchant's User Management
        <div style="float: right;">
          <form action="/icici/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="Add New Child Merchant" name="submit">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New User
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms" >
        <br>
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>

              <%
                /*String memberid = (String)request.getAttribute("memberid");*/
                String merchantid = nullToStr(request.getParameter("merchantid"));

                if(request.getAttribute("error")!=null)
                {
                  String message = (String) request.getAttribute("error");
                  if(message != null)
                    out.println("<center><font class=\"textb\">"+message+"</font></center><br/><br/>");
                }

              %>
        <%--<table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
          <tr>
            <td width="2%" class="textb">&nbsp;</td>
            <td width="20%" class="textb" for="mid" >Member ID</td>
           &lt;%&ndash; <td width="0%" class="textb"></td>&ndash;%&gt;
            <td width="22%" class="textb">
                <input name="merchantid" id="mid" value="<%=merchantid%>" class="form-control" autocomplete="on">

              &lt;%&ndash;<select size="1" name="merchantid" class="txtboxsignup" id="dropdown1" >

                <option value="">--Select MerchantId--</option>
                <%
                  LinkedHashMap merchantHash = multipleMemberUtill.selectMemberId();
                  String selected3 = "";
                  String login="";

                  for(Object merchantId :merchantHash.keySet())
                  {
                    login = (String) merchantHash.get(merchantId);

                    if(merchantId.equals(memberid))
                      selected3 = "selected";
                    else
                      selected3 = "";
                %>
                <option value="<%=merchantId%>"<%=selected3%>><%=merchantId%>-<%=login%></option>
                &lt;%&ndash;<option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>&ndash;%&gt;

                <%
                  }
                %>

              </select>&ndash;%&gt;

            </td>

            <td width="10%" class="textb">&nbsp;</td>
            <td width="40%" class="textb" ></td>
            <td width="5%" class="textb"></td>
            <td width="50%" class="textb">
              <button type="submit" class="buttonform" style="margin-left:40px; ">
                <i class="fa fa-clock-o"></i>
                &nbsp;&nbsp;Search
              </button>
            </td>

          </tr>
        </table>--%>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="20%" class="textb" >Member Id</td>
                  <td width="0%" class="textb"></td>
                  <td width="22%" class="textb">
                    <%--  <label >Merchant ID</label>--%>
                    <input name="merchantid" id="mid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(merchantid)%>" class="form-control" autocomplete="on">

                    <%-- <select size="1" name="merchantid" class="txtboxsignup" id="dropdown1" >

                       <option value="">--Select MerchantId--</option>
                       <%
                         LinkedHashMap merchantHash = multipleMemberUtill.selectMemberId();
                         String selected3 = "";
                         String login="";

                         for(Object merchantId :merchantHash.keySet())
                         {
                           login = (String) merchantHash.get(merchantId);

                           if(merchantId.equals(memberid))
                             selected3 = "selected";
                           else
                             selected3 = "";
                       %>
                       <option value="<%=merchantId%>"<%=selected3%>><%=merchantId%>-<%=login%></option>
                       &lt;%&ndash;<option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>&ndash;%&gt;

                       <%
                         }
                       %>

                     </select>
       --%>
                  </td>

                  <td width="10%" class="textb">&nbsp;</td>
                  <td width="40%" class="textb" ></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </td>

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

  <br>
  <%
    int records = 0;
    Hashtable temphash=null;
    Functions functions=new Functions();
    Hashtable detailHash = (Hashtable)request.getAttribute("detailHash");
    if(detailHash!=null && (detailHash.size()!=0 && detailHash.size()!=1))
    {
      detailHash = (Hashtable)request.getAttribute("detailHash");
      records=Integer.parseInt((String) detailHash.get("records"));

    }

    if(records>0)
    {
  %>
  <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td valign="middle" align="center"  class="th0">Sr no</td>
      <%--<td valign="middle" align="center" class="th0">Member Id</td>--%>
      <td valign="middle" align="center" class="th0">User Name</td>
      <td valign="middle" align="center" class="th0">Contact Email</td>
      <td valign="middle" align="center" class="th0">Phone Number</td>
      <td valign="middle" align="center" class="th0" colspan = "6">Action</td>
    </tr>
    </thead>
      <%
        String style="class=td1";
        String ext="light";

        for(int i = 1;i<=records;i++)
        {
            String id=Integer.toString(i);

          int srno=i+ records;

            if(i%2==0)
            {
                style="class=tr1";
                ext="dark";
            }
            else
            {
                style="class=tr0";
                ext="light";
            }

        temphash=(Hashtable)detailHash.get(id);

          //System.out.println("userid::::"+temphash.get("userid"));

          out.println("<tr>");
          out.println("<td align=\"center\" "+style+">&nbsp;"+i+ "</td>");
          /*out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) detailHash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\"value=\""+detailHash.get("memberid")+"\"></td>");*/
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"emailaddress\" value=\""+temphash.get("contact_emails")+"\"></td>");
          if(functions.isValueNull((String) temphash.get("telno"))){
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("telno"))+"<input type=\"hidden\" name=\"telno\" value=\""+temphash.get("telno")+"\"></td>");
          }
          else{
          out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML("-")+"<input type=\"hidden\" name=\"telno\" value=\""+"-"+"\"></td>");
          }
          out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"View\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
          out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Edit\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"modify1\"></form></td>");
         // out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Delete\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");
          out.println("<td align=\"center\" "+style+"><form action=\"/icici/servlet/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\" name=\"formAction\" onSubmit=\"return DoReverse('"+ctoken+"')\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String) request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"submit\" name=\"submit\" value=\"Delete\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"delete\"></form></td>");
          out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/MemberUserTerminalList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Add Terminal\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"addterminal\"></form></td>");
          out.println("<td align=\"center\""+style+"><form action=\"/icici/allocationUser.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"merchantid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Module Allocation\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"moduleallocation\"></form></td>");

          out.println("</tr>");
        }
        %>
    <%
      }
  else if (records==0)
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
<%!
      public static String nullToStr(String str)
      {
        if(str == null)
          return "";
        return str;
      }
 %>