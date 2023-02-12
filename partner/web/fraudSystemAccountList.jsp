<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="functions.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: NAMRATA B. BARI
  Date: 17/01/2020
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
  <%
    String company= (String)session.getAttribute("partnername");
  %>
  <title><%=company%> Fraud Management> Fraud System Account Master</title>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
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
</head>
<body>

<%!
  Logger logger =new Logger("test.jsp");
  private ApplicationManager applicationManager = new ApplicationManager();
  private Functions functions = new Functions();
%>
<%
  if (partner.isLoggedInPartner(session))
  {
  logger.debug("Fraud System Account Master");
  session.setAttribute("submit","Fraud System Account Master");
  String fsid = Functions.checkStringNull(request.getParameter("fsid"));
  String accountName = Functions.checkStringNull(request.getParameter("fsaccountid"));
  String str = "";

  if(fsid == null)
  {fsid = "";}

  if(accountName == null)
  {accountName = "";}

  str="ctoken="+ctoken;
  if(fsid != null) {str=str+"&fsid=" + fsid;}
  if(accountName != null) {str=str+"&accountname=" +accountName;}
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String fraudSystemAccountList_Allocated_Account_List = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Allocated_Account_List")) ? rb1.getString("fraudSystemAccountList_Allocated_Account_List") : " Allocated Account List";
    String fraudSystemAccountList_Allocate_Account = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Allocate_Account")) ? rb1.getString("fraudSystemAccountList_Allocate_Account") : "Allocate Account";
    String fraudSystemAccountList_Add_New_Account = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Add_New_Account")) ? rb1.getString("fraudSystemAccountList_Add_New_Account") : "Add New Account";
    String fraudSystemAccountList_Fraud_System_Account = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Fraud_System_Account")) ? rb1.getString("fraudSystemAccountList_Fraud_System_Account") : "Fraud System Account";
    String fraudSystemAccountList_Fraud_System = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Fraud_System")) ? rb1.getString("fraudSystemAccountList_Fraud_System") : "Fraud System";
    String fraudSystemAccountList_ALL = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_ALL")) ? rb1.getString("fraudSystemAccountList_ALL") : "ALL";
    String fraudSystemAccountList_Fraud_System_Account_Id = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Fraud_System_Account_Id")) ? rb1.getString("fraudSystemAccountList_Fraud_System_Account_Id") : "Fraud System Account Id";
    String fraudSystemAccountList_Search = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Search")) ? rb1.getString("fraudSystemAccountList_Search") : "Search";
    String fraudSystemAccountList_Report_Table = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Report_Table")) ? rb1.getString("fraudSystemAccountList_Report_Table") : "Report Table";
    String fraudSystemAccountList_Sr_No = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Sr_No")) ? rb1.getString("fraudSystemAccountList_Sr_No") : "Sr No";
    String fraudSystemAccountList_Fraud_System_Account_ID = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Fraud_System_Account_ID")) ? rb1.getString("fraudSystemAccountList_Fraud_System_Account_ID") : "Fraud System Account ID";
    String fraudSystemAccountList_Account_Name = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Account_Name")) ? rb1.getString("fraudSystemAccountList_Account_Name") : "Account Name";
    String fraudSystemAccountList_User_Name = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_User_Name")) ? rb1.getString("fraudSystemAccountList_User_Name") : "User Name";
    String fraudSystemAccountList_isTest = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_isTest")) ? rb1.getString("fraudSystemAccountList_isTest") : "isTest";
    String fraudSystemAccountList_Fraud_System_Name = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Fraud_System_Name")) ? rb1.getString("fraudSystemAccountList_Fraud_System_Name") : "Fraud System Name";
    String fraudSystemAccountList_ContactEmail = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_ContactEmail")) ? rb1.getString("fraudSystemAccountList_ContactEmail") : "Contact Email";
    String fraudSystemAccountList_Action = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Action")) ? rb1.getString("fraudSystemAccountList_Action") : "Action";
    String fraudSystemAccountList_ShowingPage = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_ShowingPage")) ? rb1.getString("fraudSystemAccountList_ShowingPage") : "Showing Page";
    String fraudSystemAccountList_of = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_of")) ? rb1.getString("fraudSystemAccountList_of") : "of";
    String fraudSystemAccountList_records = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_records")) ? rb1.getString("fraudSystemAccountList_records") : "records";
    String fraudSystemAccountList_Sorry = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Sorry")) ? rb1.getString("fraudSystemAccountList_Sorry") : "Sorry";
    String fraudSystemAccountList_No_records = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_No_records")) ? rb1.getString("fraudSystemAccountList_No_records") : "No records found";
    String fraudSystemAccountList_Modify = StringUtils.isNotEmpty(rb1.getString("fraudSystemAccountList_Modify")) ? rb1.getString("fraudSystemAccountList_Modify") : "Modify";
%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/managePartnerAllocatedAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
             <%=fraudSystemAccountList_Allocated_Account_List%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>
      <div class="pull-right">
        <div class="btn-group">

          <form action="/partner/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=fraudSystemAccountList_Allocate_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=fraudSystemAccountList_Add_New_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>
      <br><br><br><br><br><br><br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=fraudSystemAccountList_Fraud_System_Account%></strong></h2>
            </div>
            <div class="additional-btn">
               <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
            <form action="/partner/net/FraudSystemAccountList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <%
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                  %>

                  <div class="form-group col-md-4 has-feedback">
                    <label class="col-sm-4 control-label"><%=fraudSystemAccountList_Fraud_System%></label>
                    <div class="col-sm-8">
                      <select class="form-control" name="fsid" id="partnerid">
                        <option value="" selected><%=fraudSystemAccountList_ALL%></option>
                        <%
                          Hashtable<String,String> fraudSystem = FraudSystemService.getFraudSystem();
                          Iterator it1 = fraudSystem.entrySet().iterator();
                          while (it1.hasNext())
                          {
                            Map.Entry pair = (Map.Entry)it1.next();
                            if(pair.getKey().equals(fsid)){
                              out.println("<option value=\""+pair.getKey()+"\" selected>"+pair.getKey()+" - "+pair.getValue()+"</option>");
                            }else
                            {
                              out.println("<option value=\"" + pair.getKey() + "\">" + pair.getKey() + " - " + pair.getValue() + "</option>");
                            }
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <div class="form-group col-md-6">
                    <label class="col-sm-4 control-label"><%=fraudSystemAccountList_Fraud_System_Account_Id%></label>
                    <div class="col-sm-6">
                      <input name="fsaccountid" id="member" value="<%=accountName%>" class="form-control">
                    </div>
                  </div>

                  <div class="form-group col-md-2">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default" name="action" value="1_Edit"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=fraudSystemAccountList_Search%></button>
                    </div>
                  </div>

                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=fraudSystemAccountList_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%
                Hashtable hash = (Hashtable)request.getAttribute("transdetails");
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

                if(records > 0)
                {
              %>

              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_Sr_No%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_Fraud_System_Account_ID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_Account_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_User_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_isTest%> </b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_Fraud_System_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_ContactEmail%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudSystemAccountList_Action%></b></td>
                   </tr>
                </thead>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%
                String style="class=td1";

                for(int pos=1;pos<=records;pos++)
                {
                String id=Integer.toString(pos);
                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                if(pos%2==0)
                {
                style="class=tr0";
                }
                else
                {
                style="class=tr1";
                }

                temphash=(Hashtable)hash.get(id);
                    out.println("<tr height=20px>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud System\" align=\"center\"" + style + ">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction Status\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("fsaccountid"))+"<input type=\"hidden\" name=\"fsaccountid\" value=\""+temphash.get("fsaccountid")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction ID\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountname"))+"<input type=\"hidden\" name=\"accountname\" value=\""+temphash.get("accountname")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"MerchantID\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("username"))+"<input type=\"hidden\" name=\"username\" value=\""+temphash.get("username")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"AccountID\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isTest"))+"<input type=\"hidden\" name=\"isTest\" value=\""+temphash.get("isTest")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Score\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("fsname"))+"<input type=\"hidden\" name=\"fsname\" value=\""+temphash.get("fsname")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Score\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_email"))+"<input type=\"hidden\" name=\"fsname\" value=\""+temphash.get("contact_email")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Reversal Score\" align=\"center\"" + style + ">&nbsp;<form action=\"/partner/net/ActionFraudSystemAccount?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\""+temphash.get("fsaccountid")+"\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"btn btn-default\" value="+fraudSystemAccountList_Modify+"></form></td>");
                    out.println("</tr>");

                  }

                %>
              </table>
            </div>

            <div id="showingid"><strong><%=fraudSystemAccountList_ShowingPage%> <%=pageno%> <%=fraudSystemAccountList_of%> <%=totalrecords%> <%=fraudSystemAccountList_records%></strong></div>
            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=totalrecords%>"/>
              <jsp:param name="numrows" value="<%=pagerecords%>"/>
              <jsp:param name="pageno" value="<%=pageno%>"/>
              <jsp:param name="str" value="<%=str%>"/>
              <jsp:param name="page" value="FraudSystemAccountList"/>
              <jsp:param name="currentblock" value="<%=currentblock%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%
              }
              else
              {
                out.println(Functions.NewShowConfirmation1(fraudSystemAccountList_Sorry, fraudSystemAccountList_No_records));
              }

            %>
          </div>
        </div>
      </div>

      <%
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
