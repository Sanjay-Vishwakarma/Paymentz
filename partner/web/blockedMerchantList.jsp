<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp"%>

<%!
  private static Logger log=new Logger("blockedMerchantList.jsp");
%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","BlockedMerchantList");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String BlockedMerchantList_Blocked_Merchant = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Blocked_Merchant")) ? rb1.getString("BlockedMerchantList_Blocked_Merchant") : "Blocked Merchant Account List";
  String BlockedMerchantList_Sr_no = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Sr_no")) ? rb1.getString("BlockedMerchantList_Sr_no") : "Sr no";
  String BlockedMerchantList_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Merchant_ID")) ? rb1.getString("BlockedMerchantList_Merchant_ID") : "Merchant ID";
  String BlockedMerchantList_Login_Name = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Login_Name")) ? rb1.getString("BlockedMerchantList_Login_Name") : "Login Name";
  String BlockedMerchantList_Contact_Email = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Contact_Email")) ? rb1.getString("BlockedMerchantList_Contact_Email") : "Contact Email";
  String BlockedMerchantList_Action = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Action")) ? rb1.getString("BlockedMerchantList_Action") : "Action";
  String BlockedMerchantList_Showing_Page = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Showing_Page")) ? rb1.getString("BlockedMerchantList_Showing_Page") : "Showing Page";
  String BlockedMerchantList_of = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_of")) ? rb1.getString("BlockedMerchantList_of") : "of";
  String BlockedMerchantList_records = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_records")) ? rb1.getString("BlockedMerchantList_records") : "records";
  String BlockedMerchantList_Sorry = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_Sorry")) ? rb1.getString("BlockedMerchantList_Sorry") : "Sorry";
  String BlockedMerchantList_no = StringUtils.isNotEmpty(rb1.getString("BlockedMerchantList_no")) ? rb1.getString("BlockedMerchantList_no") : "No records found.";

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

      if(confirm("Do you really want to unblock this Merchant account?"))
      {
        return true;
      }
      return false;
    }
  </script>
</head>
<title><%=company%> | Merchant Unblocked Account </title>
<body class="bodybackground">
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=BlockedMerchantList_Blocked_Merchant%></strong></h2>
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
                HashMap merchanthash=null;
                if(request.getAttribute("blockedmerchant")!=null)
                {
                  merchanthash=(HashMap)request.getAttribute("blockedmerchant");
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
                  records=Integer.parseInt((String)merchanthash.get("records"));
                  totalrecords=Integer.parseInt((String)merchanthash.get("totalrecords"));
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
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=BlockedMerchantList_Sr_no%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=BlockedMerchantList_Merchant_ID%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=BlockedMerchantList_Login_Name%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=BlockedMerchantList_Contact_Email%></b></td>
                <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=BlockedMerchantList_Action%></b></td>
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

                  temphash=(HashMap)merchanthash.get(id);

                  out.println("<tr>");
                  out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"</td>");
                  out.println("<td align=\"center\" "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("contact_emails"))+"</td>");
                  out.println("<td align=\"center\" "+style+" >");
                  out.println("<form action=/partner/net/UnBlockedAccount?ctoken"+ctoken+" method=post><input type =hidden  name=login value=\""+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("login"))+"\" ><input type =hidden  name=ctoken value="+ctoken+"><input type=submit value=\"Unblock\" class=\"goto\" onclick= 'return confirmation();'></form>");
                  out.println("</td>");
                  out.println("</tr>");
                }
              %>
              </tbody>
            </table>

          </div>
        </div>
      </div>

      <div id="showingid"><strong><%=BlockedMerchantList_Showing_Page%> <%=pageno%> <%=BlockedMerchantList_of%> <%=totalrecords%> <%=BlockedMerchantList_records%></strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="BlockedMerchantList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
        }
        else
        {
          out.println(Functions.NewShowConfirmation1(BlockedMerchantList_Sorry, BlockedMerchantList_no));
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