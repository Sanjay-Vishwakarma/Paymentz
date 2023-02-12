<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="Top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Sagar
  Date: 9/2/16
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">


  <%-- <style type="text/css">

     #main{background-color: #ffffff}

     :target:before {
       content: "";
       display: block;
       height: 50px;
       margin: -50px 0 0;
     }

     .table > thead > tr > th {font-weight: inherit;}

     :target:before {
       content: "";
       display: block;
       height: 90px;
       margin: -50px 0 0;
     }

     footer{border-top:none;margin-top: 0;padding: 0;}

     /********************Table Responsive Start**************************/

     @media (max-width: 640px){

       table {border: 0;}

       table tr {
         padding-top: 20px;
         padding-bottom: 20px;
         display: block;
       }

       table thead { display: none;}

       tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

       table td {
         display: block;
         border-bottom: none;
         padding-left: 0;
         padding-right: 0;
       }

       table td:before {
         content: attr(data-label);
         float: left;
         width: 100%;
         font-weight: bold;
       }

     }

     table {
       width: 100%;
       max-width: 100%;
       border-collapse: collapse;
       margin-bottom: 20px;
       display: table;
       border-collapse: separate;
       border-color: grey;
     }

     thead {
       display: table-header-group;
       vertical-align: middle;
       border-color: inherit;

     }

     tr:nth-child(odd) {background: #F9F9F9;}

     tr {
       display: table-row;
       vertical-align: inherit;
       border-color: inherit;
     }

     th {padding-right: 1em;text-align: left;font-weight: bold;}

     td, th {display: table-cell;vertical-align: inherit;}

     tbody {
       display: table-row-group;
       vertical-align: middle;
       border-color: inherit;
     }

     td {
       padding-top: 6px;
       padding-bottom: 6px;
       padding-left: 10px;
       padding-right: 10px;
       vertical-align: top;
       border-bottom: none;
     }

     .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: none;}

     /********************Table Responsive Ends**************************/

   </style>--%>

  <style type="text/css">
    .table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td{
      border: 1px solid #ddd;
    }
  </style>
  <%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  %>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <title><%=company%> | Merchant Module Management</title>
</head>
<body>
<%!
  Logger logger=new Logger("merchantModuleMappingList");
%>
<%
  Logger logger = new Logger("merchantModuleMappingList.jsp");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<%--<div class="rowcontainer-fluid " >
  <div class="row rowadd" >
    <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
      <div class="form foreground bodypanelfont_color panelbody_color">
        <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp; Merchant's Merchant Master
          <form action="/merchant/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" class="addnewmember" value="User Management" name="submit" style="float: right; margin-top: -26px; background-color: #68C39F; color: #ffffff">

              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add New User

            </button>
          </form></h2>
        <hr class="hrform">
      </div>--%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
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
      <br><br>

      <%
        Hashtable hash = (Hashtable)request.getAttribute("transdetails");
        Functions functions=new Functions();

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

      <div class="row">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Module Allocation</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="pull-right">
              <div class="btn-group">

                <form action="/merchant/servlet/AllocationUser?ctoken=<%=ctoken%>" method="POST" class="form-horizontal">
                  <input type="hidden" value="<%=request.getAttribute("userid")%>" name="userid">
                  <input type="hidden" value="<%=session.getAttribute("merchantid")%>" name="merchantid">
                  <input type="hidden" value="<%=request.getAttribute("login")%>" name="login">
                  <%

                    Enumeration<String> aname=request.getParameterNames();
                    while(aname.hasMoreElements())
                    {
                      String name=aname.nextElement();
                      String value = request.getParameter(name);
                      if(value==null || value.equals("null"))
                      {
                        value = "";
                      }
                  %>
                  <input type=hidden name=<%=name%> value=<%=value%>>
                  <%
                    }
                  %>
<%--                  <button type="submit" class="btn-xs" value="AllocationUser" name="submit" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/merchant/images/moduleallocation.png">
                  </button>--%>
                  <button type="submit" name="submit" value="AllocationUser" class="btn btn-default" style="display: -webkit-box; margin-right: 15px;"><i class="fa fa-puzzle-piece" aria-hidden="true"></i>&nbsp;&nbsp;Module Allocation</button>
                </form>
              </div>
            </div>
            <div class="widget-content padding">

              <%
                if(functions.isValueNull((String ) request.getAttribute("errormessage")))
                {
                  out.println(Functions.NewShowConfirmation1("Result", (String) request.getAttribute("errormessage")));
                }
              %>

              <%--<div class="form-group" >
                <div class="col-md-5"></div>
                <div class="col-md-2" style="color: white;">
                  <label>&nbsp;User&nbsp;ID:-&nbsp;<%=request.getAttribute("userid")%></label></div>

                <div class="col-md-5"></div>
              </div><br>
              <div class="form-group">
                <div class="col-md-5"></div>
                <div class="col-md-2" style="color: white;">
                  <label>&nbsp;User&nbsp;Name:-&nbsp;<%=request.getAttribute("login")%></label></div>
                <div class="col-md-5"></div>
              </div><br>--%>
              <%--<div class="form-group">
                <div class="col-md-5">
                  &lt;%&ndash;<label class="col-sm-6 control-label"></label>&ndash;%&gt;</div>
                <div class="col-md-2" style="color: white;">
                  <label class="col-sm-6 control-label">List&nbsp;of&nbsp;Module&nbsp;Allocation&nbsp;</label></div>
                <div class="col-md-5"></div>
              </div>--%>

              <div id="horizontal-form">

                <div class="form-group col-md-6">
                  <label>User ID :- <%=request.getAttribute("userid")%></label><br>
                  <label>User Name :- <%=request.getAttribute("login")%></label>

                </div>

              </div>
              <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                <%--<thead>

                <tr style="text-align: center;color: white;">
                  <th style="text-align: center">&nbsp;User&nbsp;ID:-&nbsp;<%=request.getAttribute("userid")%></th>
                  <th style="text-align: center">List of Module Allocation</th>
                  <th style="text-align: center">&nbsp;User&nbsp;Name:-&nbsp;<%=request.getAttribute("login")%></th>
                </tr>
                </thead>--%>
                <thead>
                <tr style="color: white;">
                  <th style="text-align: center" >Mapping Id</th>
                  <th style="text-align: center" >Module Name</th>
                  <th style="text-align: center" >Action</th>
                </tr>
                </thead>
                <%
                  if(records>0)
                  {

                    String style="class=td1";
                    String ext="light";
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
                  <td style="text-align: center;vertical-align: middle;" data-label="Mapping Id"><%=mappingId%></td>
                  <td style="text-align: center;vertical-align: middle;" data-label="Module Name"><%=(String)temphash.get("modulename")%></td>
                  <td style="text-align: center;vertical-align: middle;" data-label="Action">&nbsp;<form action="/merchant/servlet/ActionMerchantModule?ctoken=<%=ctoken%>" method="post"><input type="hidden" name="mappingid" value=<%=mappingId%>><input type="hidden" name="login" value="<%=ESAPI.encoder().encodeForHTML((String)request.getAttribute("login"))%>"><input type="hidden" name="userid" value="<%=ESAPI.encoder().encodeForHTML((String)request.getAttribute("userid"))%>"><input type="hidden" name="action" value="remove"> <input type="submit" class="btn btn-default" value="Remove"></form></td>
                </tr>
                </tbody>
                <%

                  }
                %>
              </table>
            </div>
          </div>
        </div>
      </div>

      <%
        }
        else
        {

          out.println(Functions.NewShowConfirmation1("Sorry", "No records found for given search criteria."));
        }

      %>

    </div>
  </div>
</div>
</body>
</html>
