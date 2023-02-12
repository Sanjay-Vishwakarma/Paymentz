<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties,org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 1/5/2016
  Time: 12:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","memberChildList");
  String partnerId=(String)session.getAttribute("merchantid");
  String memberid=nullToStr(request.getParameter("merchantid"));

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String memberChildList_Merchants_User_Management = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_User_Management")) ? rb1.getString("memberChildList_Merchants_User_Management") : "Merchant's User Management";
  String memberChildList_Partner_ID = StringUtils.isNotEmpty(rb1.getString("memberChildList_Partner_ID")) ? rb1.getString("memberChildList_Partner_ID") : "Partner ID";
  String memberChildList_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchant_ID")) ? rb1.getString("memberChildList_Merchant_ID") : "Merchant ID*";
  String memberChildList_Search = StringUtils.isNotEmpty(rb1.getString("memberChildList_Search")) ? rb1.getString("memberChildList_Search") : "Search";
  String memberChildList_Merchants_User_List = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_User_List")) ? rb1.getString("memberChildList_Merchants_User_List") : "Merchant's User List";
  String memberChildList_Merchants_Sr_no = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_Sr_no")) ? rb1.getString("memberChildList_Merchants_Sr_no") : "Sr no";
  String memberChildList_Merchants_User_Name = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_User_Name")) ? rb1.getString("memberChildList_Merchants_User_Name") : "User Name";
  String memberChildList_Merchants_Contact_Email = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_Contact_Email")) ? rb1.getString("memberChildList_Merchants_Contact_Email") : "Contact Email";
//  String memberChildList_Merchants_Telno = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_Telno")) ? rb1.getString("memberChildList_Merchants_Telno") : "Phone No";
  String memberChildList_Merchants_Action = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_Action")) ? rb1.getString("memberChildList_Merchants_Action") : "Action";
  String memberChildList_Merchants_Sorry = StringUtils.isNotEmpty(rb1.getString("memberChildList_Merchants_Sorry")) ? rb1.getString("memberChildList_Merchants_Sorry") : "Sorry";
  String memberChildList_No_records_found = StringUtils.isNotEmpty(rb1.getString("memberChildList_No_records_found")) ? rb1.getString("memberChildList_No_records_found") : "No records found for given search criteria.";

%>
<html>
<script>
  function ConfirmUnblock()
  {
    var x = confirm("Do you really want to Unblock this User ?");
    if (x)
      return true;
    else
      return false;
  }
</script>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> User Management> Merchant User Management</title>
</head>
<body>
<%
  // MultipleMemberUtill multipleMemberUtill = new MultipleMemberUtill();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  PartnerFunctions partner1=new PartnerFunctions();
  if (partner.isLoggedInPartner(session))

  {
%>
<html lang="en">
<head>
  <style type="text/css">
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <title><%=company%> Merchant Delete</title>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
    function DoReverse(ctoken)
    {
      if (confirm("Do you really want to Delete this User ?"))
      {
        return true;
      }
      else
        return false;
    }
  </script>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <%-- <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
   <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
   <script>
     $(function () {
       $("#mid").autocomplete({
         source: function( request, response ) {
           $.ajax( {
             url: "/partner/net/GetDetails",
             dataType: "json",
             data: {
               partnerid: $('#partnerid').val(),
               ctoken: $('#ctoken').val(),
               method:"memberid",
               term: request.term



             },
             success: function( data ) {
               //response( data );

               response($.map(data, function (value, key) {
                 return {
                   label: value,
                   value: key
                 };
               }));
             }
           } );
         },
         minLength: 2



       });



     });
   </script>--%>
  <div class="content-page">
    <div class="content">
      <div class="page-heading">
        <div class="pull-right">
          <div class="btn-group">
            <form action="/partner/memberChildSignup.jsp?ctoken=<%=ctoken%>" method="POST">
              <button class="btn-xs" type="submit" value="memberChildList" name="submit" name="B1" style="background: transparent;border: 0;">
                <img style="height: 35px;" src="/partner/images/newuser.png">
              </button>
            </form>
          </div>
        </div>
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=memberChildList_Merchants_User_Management%></strong>
                </h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <div class="widget-content">
                <div id="horizontal-form">
                  <form action="/partner/net/MemberUserList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <input type="hidden" value="<%=partnerId%>" name="partnerid" id="partnerid">
                    <%
                      int pageno=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                      int pagerecords=partner1.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                      String Config = "";
                      String pid = null;
                      String role=(String)session.getAttribute("role");
                      String username=(String)session.getAttribute("username");
                      String actionExecutorId=(String)session.getAttribute("merchantid");
                      String actionExecutorName=role+"-"+username;
                      String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                      if(Roles.contains("superpartner")){
                        pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
                      }else{
                        Config = "disabled";
                        pid = String.valueOf(session.getAttribute("merchantid"));
                      }
                      //String memberid = (String)request.getAttribute("memberid");
                      Functions functions = new Functions();
                      String successMessage = (String) request.getAttribute("success");
                      if(functions.isValueNull(successMessage))
                      {
                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+successMessage+"</h5>");
                      }
                      String error = (String) request.getAttribute("error");
                      if(functions.isValueNull(error))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+error+"</h5>");
                      }
                    %>

                    <div class="form-group col-md-4">
                      <label class="col-sm-4 control-label"><%=memberChildList_Partner_ID%></label>
                      <div class="col-sm-8">
                        <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                        <input type ="hidden" name="pid" value="<%=pid%>">
                      </div>
                    </div>

                    <div class="form-group col-md-4">
                      <label class="col-sm-4 control-label"><%=memberChildList_Merchant_ID%></label>
                      <div class="col-sm-8">
                        <input name="merchantid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                        <%--<select size="1" name="merchantid" class="form-control" id="dropdown1" >

                          <option value="">Select Merchant ID</option>
                          <%
                            LinkedHashMap merchantHash = multipleMemberUtill.selectMemberIdForPartners(partnerId);
                            String selected3 = "";
                            String login="";

                            for(Object mId :merchantHash.keySet())
                            {
                              login = (String) merchantHash.get(mId);

                              if(mId.equals(request.getAttribute("memberid")))
                                selected3 = "selected";
                              else
                                selected3 = "";
                          %>
                          <option value="<%=mId%>"<%=selected3%>><%=mId%>-<%=login%></option>
                          <%
                            }
                          %>

                        </select>--%>
                      </div>
                    </div>
                    <div class="form-group col-md-4">
                      <div class="col-sm-offset-2 col-sm-3">
                        <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                          &nbsp;&nbsp;<%=memberChildList_Search%></button>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberChildList_Merchants_User_List%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <div class="widget-content padding" style="overflow-y: auto;">
                <%
                  StringBuffer requestParameter = new StringBuffer();
                  Enumeration<String> stringEnumeration = request.getParameterNames();
                  while(stringEnumeration.hasMoreElements())
                  {
                    String name=stringEnumeration.nextElement();
                    if("SPageno".equals(name) || "SRecords".equals(name))
                    {

                    }
                    else
                      requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                  }
                  requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                  requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                  int records = 0;
                  Hashtable temphash=null;

                  Hashtable detailHash = (Hashtable)request.getAttribute("detailHash");
                  if(detailHash!=null && (detailHash.size()!=0 && detailHash.size()!=1))
                  {
                    detailHash = (Hashtable)request.getAttribute("detailHash");
                    records=Integer.parseInt((String) detailHash.get("records"));

                  }
                  String msg= (String) request.getAttribute("msg");
                  if (functions.isValueNull(msg)){
                    out.println(Functions.NewShowConfirmation1("",msg));
                  }
                  else if(records>0)
                  {
                %>
                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <th valign="middle" align="center" style="text-align: center"><%=memberChildList_Merchants_Sr_no%></th>
                    <th valign="middle" align="center" style="text-align: center"><%=memberChildList_Merchants_User_Name%></th>
                    <th valign="middle" align="center" style="text-align: center"><%=memberChildList_Merchants_Contact_Email%></th>
                    <th valign="middle" align="center" style="text-align: center">Phone Number</th>
                    <th valign="middle" align="center" colspan="7" style="text-align: center"><%=memberChildList_Merchants_Action%></th>
                  </tr>
                  </thead>
                  <tbody>
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

                      String login=ESAPI.encoder().encodeForHTML((String) temphash.get("login"));
                      String unblock=partner.getUnblockStatus(login);
                      String disabled="";
                      if ("unlocked".equalsIgnoreCase(unblock))
                      {
                        disabled = "disabled";
                      }

                      out.println("<tr>");
                      out.println("<td align=\"center\" data-label=\"Sr no\" "+style+">&nbsp;"+i+ "</td>");
                      out.println("<td align=\"center\" data-label=\"Login\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
                      out.println("<td align=\"center\" data-label=\"Contact Email\" "+style+">&nbsp;"+functions.getEmailMasking((String) temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"emailaddress\" value=\""+temphash.get("contact_emails")+"\"></td>");
                     if(functions.isValueNull((String) temphash.get("telno"))){
                       out.println("<td align=\"center\" data-label=\"telno\" "+style+">&nbsp;"+functions.getPhoneNumMasking((String) temphash.get("telno"))+"<input type=\"hidden\" name=\"telno\" value=\""+temphash.get("telno")+"\"></td>");

                     }
                      else{
                       out.println("<td align=\"center\" data-label=\"telno\" "+style+">&nbsp;"+"-"+"<input type=\"hidden\" name=\"telno\" value=\""+"-"+"\"></td>");

                     }
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/net/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><button type=\"submit\" name=\"User Management\" value=\"View\" class=\"btn btn-default gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"View\">View</button>");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/net/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\">" +
                              "<input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\">" +
                              "<button type=\"submit\" name=\"memberChildList\" value=\"Edit\" class=\"btn btn-default gotoauto\">" +
                              "<input type=\"hidden\" name=\"action\" value=\"modify1\">Edit</button>");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+">" +
                              "<form action=\"/partner/net/EditMemberUserList?ctoken="+ctoken+"\" method=\"POST\" name=\"formAction\" onSubmit=\"return DoReverse('"+ctoken+"')\">" +
                              "<input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) session.getAttribute("merchantid"))+"\">" +
                              "<input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\">" +
                              "<input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\">" +
                              "<input type=\"hidden\" name=\"action\" value=\"delete\">" +
                              "<button type=\"submit\" name=\"memberChildList\" value=\"Delete\" class=\"btn btn-default button\">"+
                              "Delete"+
                              "</button>");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/net/MemberUserTerminalList?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"User Management\" value=\"Add Terminal\" class=\"gotoauto btn btn-default\">");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/allocationUser.jsp?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"MerchantModuleMappingList\" value=\"Module Allocation\" class=\"gotoauto btn btn-default\" width=\"100\">");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/net/UnblockMerchantUser?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String)request.getAttribute("memberid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"userid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("userid"))+"\"><input type=\"submit\" name=\"Unblock\" value=\"Unblock\" Onclick=\"return ConfirmUnblock();\" class=\"gotoauto btn btn-default\" width=\"100\" "+disabled+">");
                      out.println(requestParameter.toString());
                      out.println("</form></td>");
                      out.println("</tr>");
                    }
                  %>
                  <%
                    }
                    else if (records==0)
                    {
                      out.println(Functions.NewShowConfirmation1(memberChildList_Merchants_Sorry,memberChildList_No_records_found));
                    }
                  %>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>



      <%
  }
%>

    <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
    <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
    <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
    <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">

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