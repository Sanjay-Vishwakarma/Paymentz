<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="Top.jsp"%>
<%@include file="ietest.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.util.Calendar,java.util.Enumeration,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.payment.MultipleMemberUtill" %>

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

  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
  <script>
    $(document).ready(function(){
      $('#myTable').dataTable();
    });
  </script>
  <title>Merchant Child Signup</title>
</head>
<body>
<%
  MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","User Management");
%>
<%--<div class="rowcontainer-fluid " >
  <div class="row rowadd" >
    <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
      <div class="form foreground bodypanelfont_color panelbody_color">
        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;&lt;%&ndash;<%=company%>&ndash;%&gt; Merchant Master
          <form action="/merchant/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="Add New Child Merchant" name="submit" style="float: right; margin-top: -26px; background-color: #68C39F; color: #ffffff">

              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New User

            </button>
          </form>
        </h2>
        <hr class="hrform">
      </div>--%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <%--<div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/merchant/servlet/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms" >
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <%
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<center><font class=\"alert alert-danger alert-dismissable\">"+message+"</font></center><br/><br/>");
                    }

                  %>
                  <div class="form-group col-md-2 has-feedback">
                    <label>Member Id</label>
                  </div>
                  <div class="form-group col-md-3 has-feedback">
                    <input type="text" name="merchantid" class="form-control"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                  </div>
                  &lt;%&ndash;
                                      <select size="1" name="merchantid" class="txtboxsignup" id="dropdown1">

                                        <option value="">--Select MerchantId--</option>
                                        <%
                                          String mId = "";
                                          Hashtable merchantHash = multipleMemberUtill.selectMemberIdForDropDown();
                                          Enumeration merEnum = merchantHash.keys();
                                          String login = "";
                                          String selected3 = "";
                                          while (merEnum.hasMoreElements())
                                          {
                                            mId = (String) merEnum.nextElement();
                                            login = (String) merchantHash.get(mId);

                                            if(mId.equals(request.getAttribute("memberid")))
                                              selected3 = "selected";
                                            else
                                              selected3 = "";

                                          /*Enumeration enu = merchantHash.keys();
                                          String selected = "";
                                          String key = "";
                                          String value = "";


                                          while (enu.hasMoreElements())
                                          {
                                            key = (String) enu.nextElement();
                                            value = (String) merchantHash.get(key);

                                            if (key.equals(request.getAttribute("memberid")))
                                              selected = "selected";
                                            else
                                              selected = "";*/

                                        %>
                                        <option value="<%=mId%>"<%=selected3%>><%=mId%>--<%=login%></option>
                                        &lt;%&ndash;<option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>&ndash;%&gt;

                                        <%
                                          }
                                        %>

                                      </select>
                  &ndash;%&gt;
                  <div class="form-group col-md-4">
                    <button type="submit" class="btn btn-default" style="margin-left:300px;padding-right: 50px;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>--%>
      <form action="/merchant/servlet/MemberUserList?ctoken=<%=ctoken%>" name="form" method="post">

        <div class="pull-right">
          <div class="btn-group">


            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();

                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>
            <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/merchant/images/goBack.png">
            </button>--%>
            <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;Go Back</button>

          </div>
        </div>
      </form>
      <br>
      <br>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Merchant Master List</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <form action="/merchant/servlet/EditMemberUserList?ctoken=<%=ctoken%>" method="POST" class="form-horizontal">
              <input type="hidden" name="ctoken" value="<%=ctoken%>">

              <%
                Hashtable detailHash = (Hashtable) request.getAttribute("detailHash");
                if(detailHash!=null)
                {
                  detailHash = (Hashtable)request.getAttribute("detailHash");
                }
                String action = (String) request.getAttribute("action");
                String isreadonly =(String) request.getAttribute("action");
                String conf = "";

                if(isreadonly.equalsIgnoreCase("view"))
                {
                  conf = "disabled";
                }
                else if (isreadonly.equalsIgnoreCase("modify"))
                {
                  conf = "";
                }
                if (detailHash != null && detailHash.size() > 0)
                {
                  String style="class=tr0";
              %>
              <%--<div class="form-group">
                <label class="col-md-2 control-label">Member User Details</label>
              </div>--%>
              <div class="widget-content padding">

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-2 control-label">Parent Merchant Id</label>
                  <div class="col-md-4">
                    <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("memberid"))%>"disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-2 control-label">Login</label>
                  <div class="col-md-4">
                    <input type="text" class="form-control" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("login"))%>"disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-2 control-label">Email Address</label>
                  <div class="col-md-4">
                    <input type="text" class="form-control" name="contact_emails" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" <%=conf%> >
                  </div>
                  <%--<div class="col-md-6"></div>--%>
               </div>
                <div class="form-group">
                 <div class="col-md-2"></div>
                 <label class="col-md-2 control-label">Phone CC Code</label>
                 <div class="col-md-4">
                   <input type="text" class="form-control" name="telnocc" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("telnocc")!= null? (String)detailHash.get("telnocc") : "-")%>" <%=conf%> >
                 </div>
                 <%--<div class="col-md-6"></div>--%>
               </div>
               <div class="form-group">
                 <div class="col-md-2"></div>
                 <label class="col-md-2 control-label">Phone N0</label>
                 <div class="col-md-4">
                   <input type="text" class="form-control" name="telno" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("telno")!=null? (String)detailHash.get("telno") : "-")%>" <%=conf%> >
                 </div>
                 <%--<div class="col-md-6"></div>--%>
               </div>
                <%
                  out.println("<div class=\"form-group\">");
                  out.println("<div class=\"col-md-4\"></div>");
                  out.println("<div class=\"col-md-4\">");
                  if(!conf.equalsIgnoreCase("disabled"))
                    out.println("<input type=\"submit\" class=\"btn btn-default\" name=\"modify\" value=\"Save\" "+conf+"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"hidden\" name=\"memberid\" value=\""+(String)detailHash.get("memberid")+"\"><input type=\"hidden\" name=\"login\" value=\""+request.getAttribute("login")+"\">");
                  out.println("</div>");
                  out.println("</div>");
                %>
                <%

                  }
                  else
                  {
                    out.println(Functions.NewShowConfirmation1("Sorry","No records found for given search criteria."));
                  }
                %>
                <%--<table  align="center" style="width:100%" id="myTable" class="display table">
                  <tr <%=style%>>
                    <td class="th0" colspan="2">Member User Details</td>
                  </tr>

                  <tr <%=style%>>
                    <td class="tr1">Parent MemberId: </td>
                    <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("memberid"))%>"disabled></td>
                  </tr>
                  <tr <%=style%>>
                    <td class="tr1">Login: </td>
                    <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("login"))%>"disabled></td>
                  </tr>
                  <tr <%=style%>>
                    <td class="tr1">Email Address </td>
                    <td class="tr1"><input type="text" class="txtbox1" name="contact_emails" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" <%=conf%> ></td>
                  </tr>
                  <%
                    out.println("<tr>");
                    if(!conf.equalsIgnoreCase("disabled"))
                      out.println("<td colspan='2' style='text-align:center'><input type=\"submit\" class=\"btn btn-default\" name=\"modify\" value=\"Save\" "+conf+"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"hidden\" name=\"memberid\" value=\""+(String)detailHash.get("memberid")+"\"><input type=\"hidden\" name=\"login\" value=\""+request.getAttribute("login")+"\"></td>");
                    out.println("</tr>");
                  %>
                  <%

                    }
                    else
                    {
                      out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
                    }
                  %>
                </table>--%>
                <%--<table  align="center" style="width:100%" id="myTable" class="display table">
                    <tr <%=style%>>
                      <td class="th0" colspan="2">Member User Details</td>
                    </tr>

                    <tr <%=style%>>
                      <td class="tr1">Parent MemberId: </td>
                      <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("memberid"))%>"disabled></td>
                    </tr>
                    <tr <%=style%>>
                      <td class="tr1">Login: </td>
                      <td class="tr1"><input type="text" class="txtbox1" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("login"))%>"disabled></td>
                    </tr>
                    <tr <%=style%>>
                      <td class="tr1">Email Address </td>
                      <td class="tr1"><input type="text" class="txtbox1" name="contact_emails" value="<%=ESAPI.encoder().encodeForHTML((String)detailHash.get("emailaddress"))%>" <%=conf%> ></td>
                    </tr>
                    <%
                      out.println("<tr>");
                      if(!conf.equalsIgnoreCase("disabled"))
                        out.println("<td colspan='2' style='text-align:center'><input type=\"submit\" class=\"btn btn-default\" name=\"modify\" value=\"Save\" "+conf+"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"hidden\" name=\"memberid\" value=\""+(String)detailHash.get("memberid")+"\"><input type=\"hidden\" name=\"login\" value=\""+request.getAttribute("login")+"\"></td>");
                      out.println("</tr>");
                    %>
                    <%

                      }
                      else
                      {
                        out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
                      }
                    %>
                  </table>--%>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>