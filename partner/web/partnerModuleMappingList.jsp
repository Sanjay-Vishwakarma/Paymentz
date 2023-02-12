<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 5/12/2016
  Time: 12:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerModuleMappingList");
  Logger logger=new Logger("partnerModuleMappingList.jsp");
%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Partner User Management</title>
</head>
<body>
<%!
  Logger logger=new Logger("partnerModuleMappingList.jsp");
%>
<%--<% Logger logger = new Logger("partnerModuleMappingList.jsp");

%>--%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("accountid".equals(name))
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                }
                else
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>

            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>
      <%--<div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><i class="fa fa-th-large"></i>&nbsp;&nbsp;<strong>Merchant's Merchant Master</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">
                <form action="/partner/partnerChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button class="btn-xs" type="submit" value="partnerChildList" name="submit" name="B1" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/partner/images/newuser.png">
                  </button>
                </form>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/PartnerUserList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  &lt;%&ndash;<%
                      String partnerId = (String) session.getAttribute("merchantid");
                     /* if(request.getAttribute("error")!=null)
                      {
                        String message = (String) request.getAttribute("error");
                        if(message != null)
                          out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+message+"</h5>");
                      }*/

                  %>&ndash;%&gt;


                  <div class="form-group col-md-8">
                    <label class="col-sm-3 control-label">Partner ID</label>
                    <div class="col-sm-6">
                      <input type="text" class="form-control" name="partnerid"  value="<%=(String) session.getAttribute("merchantid")%>" disabled>
                    </div>
                  </div>


                  &lt;%&ndash;<div class="form-group col-md-3 has-feedback">&nbsp;</div>&ndash;%&gt;
                  <div class="form-group col-md-3">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;Search</button>
                    </div>
                  </div>


                </form>
              </div>
            </div>
          </div>
        </div>
      </div>--%>

      <%


        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        //Functions functions=new Functions();

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

      %>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>List of Module Allocation</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <%
              String merchantid= (String) session.getAttribute("merchantid");
              Functions functions = new Functions();
              //String memberid = (String)request.getAttribute("memberid");
              String error = (String) request.getAttribute("error");
              if(functions.isValueNull(error))
              {
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+error+"</h5>");
              }

            %>

            <%
              String message = (String) request.getAttribute("errormessage");
              if(functions.isValueNull(message))
              {
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+message+"</h5>");
              }

              String successMessage = (String) request.getAttribute("success");
              if(functions.isValueNull(successMessage))
              {
                                            /*out.println("<center><font class=\"textb\">"+(String) request.getAttribute("error")+"</font></center><br/><br/>");*/
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+successMessage+"</h5>");
              }

            %>
            <div class="pull-right">
              <div class="btn-group col-md-6">
                <form action="/partner/net/PartnerAllocationUser?ctoken=<%=ctoken%>" method="POST">
                  <input type="hidden" value="<%=request.getAttribute("userid")%>" name="userid">
                  <input type="hidden" value="<%=session.getAttribute("merchantid")%>" name="partnerid">
                  <input type="hidden" value="<%=request.getAttribute("login")%>" name="login">
                  <button type="submit" class="btn btn-default" value="AllocationUser" name="submit">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Module Allocation
                  </button>
                </form>
              </div>
            </div>

            <div class="widget-content padding" style="overflow-x: auto;">

              <div id="horizontal-form">

                <%--<div class="form-group col-md-6">
                  <label class="col-sm-3 control-label">User ID</label>
                  <div class="col-sm-6">
                    <input type="text" class="form-control" value="<%=request.getAttribute("userid")%>" disabled>
                  </div>
                </div>

                <div class="form-group col-md-6">
                  <label class="col-sm-3 control-label">User Name</label>
                  <div class="col-sm-6">
                    <input type="text" class="form-control" value="<%=request.getAttribute("login")%>" disabled>
                  </div>
                </div>--%>

                  <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead style="display: table-header-group!important;">
                    <tr>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">User ID:- <%=request.getAttribute("userid")%></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">User Name:- <%=request.getAttribute("login")%></td>
                    </tr>
                    </thead>
                  </table>

                <%
                  if(records>0)
                  {

                    String style="class=td1";
                    String ext="light";
                    %>



              <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Mapping ID</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Module Name</b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Action</b></td>
                </tr>
                </thead>

                <%
                  for(int pos=1;pos<=records;pos++)
                  {
                    String id=Integer.toString(pos);

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
                    String mappingId=(String)temphash.get("mappingid");

                %>




                <tbody>
                <tr>
                  <td align="center" style="vertical-align: middle;" <%=style%> data-label="Mapping ID"><%=mappingId%></td>
                  <td align="center" style="vertical-align: middle;" <%=style%> data-label="Module Name"><%=(String)temphash.get("modulename")%></td>
                  <td align="center" style="vertical-align: middle;" <%=style%> data-label="Action" style="line-height:0.00;">&nbsp;<form action="/partner/net/ActionPartnerModule?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=mappingId%>><input type="hidden" name="login" value="<%=ESAPI.encoder().encodeForHTML((String)request.getAttribute("login"))%>"><input type="hidden" name="userid" value="<%=ESAPI.encoder().encodeForHTML((String)request.getAttribute("userid"))%>"><input type="hidden" name="action" value="remove"> <input type="submit" class="btn btn-default" value="Remove"></form></td>
                </tr>
                </tbody>

                <%
                  }
                %>



              </table>

                <%
                  }
                  else
                  {
                    out.println("<div class=\"row reporttable\">");
                    out.println("<div class=\"widget-content padding\">");
                    out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
                    out.println("</div>");
                    out.println("</div>");
                  }

                %>

              <%--<table align=center valign=top style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="background-color: #7eccad !important;color: white;">
                  <td  align="left" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Total Records : <%=totalrecords%></td>
                  <td  align="right" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Page No : <%=pageno%></td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                  <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                </tr>
                </thead>

                <tbody>
                <tr>
                  <td align=center>
                    <jsp:include page="page.jsp" flush="true">
                      <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                      <jsp:param name="numrows" value="<%=pagerecords%>"/>
                      <jsp:param name="pageno" value="<%=pageno%>"/>
                      <jsp:param name="str" value="<%=str%>"/>
                      <jsp:param name="page" value="PartnerMerchantFraudAccountList"/>
                      <jsp:param name="currentblock" value="<%=currentblock%>"/>
                      <jsp:param name="orderby" value=""/>
                    </jsp:include>
                  </td>
                </tr>
                </tbody>
              </table>--%>


              <%--<div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>


              <jsp:include page="page.jsp" flush="true">
                  <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                  <jsp:param name="numrows" value="<%=pagerecords%>"/>
                  <jsp:param name="pageno" value="<%=pageno%>"/>
                  <jsp:param name="str" value="<%=str%>"/>
                  <jsp:param name="page" value="PartnerMerchantFraudAccountList"/>
                  <jsp:param name="currentblock" value="<%=currentblock%>"/>
                  <jsp:param name="orderby" value=""/>
              </jsp:include>--%>




              </div>
            </div>
          </div>
        </div>
      </div>

     <%-- <%
        }
        else
        {
 /*     *//*out.println("<br><div class=\"reporttable\" style=\"margin-bottom: 9px;\">");*//*

          out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
     *//* out.println("</div>");*/

                  out.println("<div class=\"row reporttable\">");
                  out.println("<div class=\"widget-content padding\">");
                  out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
                  out.println("</div>");
                  out.println("</div>");
        }

      %>
--%>

    </div>
  </div>
</div>

</body>
</html>