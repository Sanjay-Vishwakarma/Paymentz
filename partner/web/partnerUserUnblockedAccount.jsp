<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp"%>

<%!
  private static Logger log=new Logger("partnerUserUnblockedAccount.jsp");
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerUserUnblockedAccount");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnerUserUnblockedAccount_Blocked_Partner_User_List = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Blocked_Partner_User_List")) ? rb1.getString("partnerUserUnblockedAccount_Blocked_Partner_User_List") : "Blocked Partner's User List";
  String partnerUserUnblockedAccount_Srno = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Srno")) ? rb1.getString("partnerUserUnblockedAccount_Srno") : "Sr no";
  String partnerUserUnblockedAccount_PartnrID = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_PartnrID")) ? rb1.getString("partnerUserUnblockedAccount_PartnrID") : "Partnr ID";
  String partnerUserUnblockedAccount_UserID = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_UserID")) ? rb1.getString("partnerUserUnblockedAccount_UserID") : "User ID";
  String partnerUserUnblockedAccount_Login_Name = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Login_Name")) ? rb1.getString("partnerUserUnblockedAccount_Login_Name") : "Login Name";
  String partnerUserUnblockedAccount_Role = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Role")) ? rb1.getString("partnerUserUnblockedAccount_Role") : "Role";
  String partnerUserUnblockedAccount_Contact_Email = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Contact_Email")) ? rb1.getString("partnerUserUnblockedAccount_Contact_Email") : "Contact Email";
  String partnerUserUnblockedAccount_Action = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Action")) ? rb1.getString("partnerUserUnblockedAccount_Action") : "Action";
  String partnerUserUnblockedAccount_Showing_Page = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Showing_Page")) ? rb1.getString("partnerUserUnblockedAccount_Showing_Page") : "Showing Page";
  String partnerUserUnblockedAccount_of = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_of")) ? rb1.getString("partnerUserUnblockedAccount_of") : "of";
  String partnerUserUnblockedAccount_records = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_records")) ? rb1.getString("partnerUserUnblockedAccount_records") : "records";
  String partnerUserUnblockedAccount_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_Sorry")) ? rb1.getString("partnerUserUnblockedAccount_Sorry") : "Sorry";
  String partnerUserUnblockedAccount_no = StringUtils.isNotEmpty(rb1.getString("partnerUserUnblockedAccount_no")) ? rb1.getString("partnerUserUnblockedAccount_no") : "No records found.";

%>
<html>
<head>
  <style type="text/css">
    @media (max-width: 640px){

      #smalltable{
        width: 100%!important;
      }
    }
  </style>
  <script language="javascript">

    function confirmation()
    {

      if(confirm("Do u really want to unlock this Partner user account"))
      {
        return true;
      }
      return false;
    }
  </script>
</head>
<title><%=company%> | Partner User Unblocked Account </title>
<body class="bodybackground">
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerUserUnblockedAccount_Blocked_Partner_User_List%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%
              String error1=(String ) request.getAttribute("error");
              if(error1 !=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error1 + "</h5>");
              }
              ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
              if (partner.isLoggedInPartner(session))
              {
                HashMap partnerhash=null;
                if(request.getAttribute("blockedsubpartner")!=null)
                {
                  partnerhash=(HashMap)request.getAttribute("blockedsubpartner");
                }
                HashMap temphash=null;
                String str="";
                int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                str = str + "SRecords=" + pagerecords;
                str = str + "&ctoken="+ctoken;
                int records=0;
                int totalrecords=0;

                String currentblock=request.getParameter("currentblock");

                if(currentblock==null)
                  currentblock="1";


                try
                {
                  records=Integer.parseInt((String)partnerhash.get("records"));
                  totalrecords=Integer.parseInt((String)partnerhash.get("totalrecords"));
                }
                catch(Exception ex)
                {
                }

                if(request.getAttribute("msg")!=null)
                {
                  out.println(Functions.NewShowConfirmation1("Successfull",request.getAttribute("msg").toString()));
                  out.println("<br>");
                }

                if(records>0)
                {

            %>

            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
              <thead>
              <tr>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 8%;"><b><%=partnerUserUnblockedAccount_Srno%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 10%;"><b><%=partnerUserUnblockedAccount_PartnrID%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 10%;"><b><%=partnerUserUnblockedAccount_UserID%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=partnerUserUnblockedAccount_Login_Name%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=partnerUserUnblockedAccount_Role%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=partnerUserUnblockedAccount_Contact_Email%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=partnerUserUnblockedAccount_Action%></b></td>
              </tr>
              </thead>
              <tbody>
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

                  temphash=(HashMap)partnerhash.get(id);

                  out.println("<tr>");
                  out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("roles"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("contact_emails"))+"</td>");
                  out.println("<td align=\"center\" "+style+" >");
                  out.println("<form action=/partner/net/PartnerUserUnblockedAccount?ctoken"+ctoken+" method=post><input type =hidden  name=login value=\""+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("login"))+"\" ><input type =hidden  name=ctoken value="+ctoken+"><input type=submit value=\"Unlock\" class=\"goto\" onclick= 'return confirmation();'></form>");
                  out.println("</td>");
                  out.println("</tr>");
                }
              %>
              </tbody>
            </table>

          </div>
        </div>
      </div>

      <div id="showingid"><strong><%=partnerUserUnblockedAccount_Showing_Page%> <%=pageno%> <%=partnerUserUnblockedAccount_of%> <%=totalrecords%> <%=partnerUserUnblockedAccount_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PartnerUserBlockedList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(partnerUserUnblockedAccount_Sorry, partnerUserUnblockedAccount_no));
          }
        }
        else
        {
          response.sendRedirect("/partner/logout.jsp");
          return;
        }
      %>

    </div>
  </div>
</div>
</body>
</html>