<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.manager.ApplicationManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
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
  <title>Fraud System Account Master</title>
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
    }
    )
    function DoDelete(partnerid,ctoken,fsaccountid)
    {
      console.log(partnerid);
      if (confirm("Do you really want to delete this transaction."))
      {
        document.location.href = "ManagePartnerAllocatedAccountList?ctoken="+ctoken+"&action=delete&partner_id="+partnerid+"&fsaccount_id="+fsaccountid;
      }

    };

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
    String partnerid = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");
    String fsAccount = Functions.checkStringNull(request.getParameter("fsAccount")) == null ? "" : request.getParameter("fsAccount");
    String str = "";

    if(partnerid == null)
    {partnerid = "";}

    if(fsAccount == null)
    {fsAccount = "";}

    str="ctoken="+ctoken;
    if(partnerid != null) {str=str+"&partnerid=" + partnerid;}
    if(fsAccount != null) {str=str+"&accountname=" +fsAccount;}
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String managePartnerAllocatedAccountList_Account_List = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Account_List")) ? rb1.getString("managePartnerAllocatedAccountList_Account_List") : " Account List";
    String managePartnerAllocatedAccountList_Allocate_Account = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Allocate_Account")) ? rb1.getString("managePartnerAllocatedAccountList_Allocate_Account") : "Allocate Account";
    String managePartnerAllocatedAccountList_Add_New_Account = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Add_New_Account")) ? rb1.getString("managePartnerAllocatedAccountList_Add_New_Account") : "Add New Account";
    String managePartnerAllocatedAccountList_Allocated_Fraud = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Allocated_Fraud")) ? rb1.getString("managePartnerAllocatedAccountList_Allocated_Fraud") : "Allocated Fraud System Account List";
    String managePartnerAllocatedAccountList_Partner_Id = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Partner_Id")) ? rb1.getString("managePartnerAllocatedAccountList_Partner_Id") : "Partner Id";
    String managePartnerAllocatedAccountList_Select_PartnerId = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Select_PartnerId")) ? rb1.getString("managePartnerAllocatedAccountList_Select_PartnerId") : "Select Partner Id";
    String managePartnerAllocatedAccountList_Fraud_System = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Fraud_System")) ? rb1.getString("managePartnerAllocatedAccountList_Fraud_System") : "Fraud System Account Id";
    String managePartnerAllocatedAccountList_Select_Fraud = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Select_Fraud")) ? rb1.getString("managePartnerAllocatedAccountList_Select_Fraud") : "Select Fraud System Account Id";
    String managePartnerAllocatedAccountList_Search = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Search")) ? rb1.getString("managePartnerAllocatedAccountList_Search") : "Search";
    String managePartnerAllocatedAccountList_Report_Table = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Report_Table")) ? rb1.getString("managePartnerAllocatedAccountList_Report_Table") : "Report Table";
    String managePartnerAllocatedAccountList_Sr_No = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Sr_No")) ? rb1.getString("managePartnerAllocatedAccountList_Sr_No") : "Sr No";
    String managePartnerAllocatedAccountList_PartnerID = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_PartnerID")) ? rb1.getString("managePartnerAllocatedAccountList_PartnerID") : "Partner ID";
    String managePartnerAllocatedAccountList_Fraud_System_accountid = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Fraud_System_accountid")) ? rb1.getString("managePartnerAllocatedAccountList_Fraud_System_accountid") : "Fraud System Account ID";
    String managePartnerAllocatedAccountList_Account_Name = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Account_Name")) ? rb1.getString("managePartnerAllocatedAccountList_Account_Name") : "Account Name";
    String managePartnerAllocatedAccountList_Is_Active = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Is_Active")) ? rb1.getString("managePartnerAllocatedAccountList_Is_Active") : "Is Active";
    String managePartnerAllocatedAccountList_Delete = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Delete")) ? rb1.getString("managePartnerAllocatedAccountList_Delete") : "Delete";
    String managePartnerAllocatedAccountList_Showing_Page = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Showing_Page")) ? rb1.getString("managePartnerAllocatedAccountList_Showing_Page") : "Showing Page";
    String managePartnerAllocatedAccountList_of = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_of")) ? rb1.getString("managePartnerAllocatedAccountList_of") : "of";
    String managePartnerAllocatedAccountList_records = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_records")) ? rb1.getString("managePartnerAllocatedAccountList_records") : "records";
    String managePartnerAllocatedAccountList_Sorry = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_Sorry")) ? rb1.getString("managePartnerAllocatedAccountList_Sorry") : "Sorry";
    String managePartnerAllocatedAccountList_no = StringUtils.isNotEmpty(rb1.getString("managePartnerAllocatedAccountList_no")) ? rb1.getString("managePartnerAllocatedAccountList_no") : "No records found";

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/fraudSystemAccountList.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
            <%=managePartnerAllocatedAccountList_Account_List%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/managePartnerFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=managePartnerAllocatedAccountList_Allocate_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/manageFraudSystemAccount.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1" value="Fraud System Account Master"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=managePartnerAllocatedAccountList_Add_New_Account%> &nbsp; <i class ="fa fa-sign-in" style="font-size: small; color: white;"></i>
            </button>
          </form>
        </div>
      </div>
      <br><br><br><br><br><br><br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=managePartnerAllocatedAccountList_Allocated_Fraud%></strong></h2>
            </div>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
            <form action="/partner/net/ManagePartnerAllocatedAccountList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <%
                    java.util.TreeMap<String,String> partneriddetails =null;
                    partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    Functions funct = new Functions();
                    String error = (String) request.getAttribute("error");
                    if(funct.isValueNull(error))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                    }
                    String success = (String) request.getAttribute("success");
                    if(funct.isValueNull(success))
                    {
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + success + "</h5>");
                    }

                  %>

                  <div class="form-group col-md-4 has-feedback">
                    <label class="col-sm-4 control-label"><%=managePartnerAllocatedAccountList_Partner_Id%></label>
                    <div class="col-sm-8">
                      <select class="form-control" name="partnerid">
                        <option value="" default><%=managePartnerAllocatedAccountList_Select_PartnerId%></option>
                        <%
                          String Selected = "";
                          for (String pid : partneriddetails.keySet())
                          {
                            if (pid.toString().equals(partnerid))
                            {
                              Selected = "selected";
                            }
                            else
                            {
                              Selected = "";
                            }
                        %>
                        <option value="<%=pid%>" <%=Selected%>><%=partneriddetails.get(pid)%>
                        </option>
                        <%
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <div class="form-group col-md-6">
                    <label class="col-sm-4 control-label"><%=managePartnerAllocatedAccountList_Fraud_System%></label>
                    <div class="col-sm-6">
                      <select class="form-control" name="fsAccount">
                        <option value="" default><%=managePartnerAllocatedAccountList_Select_Fraud%></option>
                        <%
                          Connection conn = null;
                          try
                          {
                            conn = Database.getConnection();
                            ResultSet rs = Database.executeQuery("select fsaccountid,accountname,fsid from fraudsystem_account_mapping ", conn);
                            while (rs.next())
                            {
                              if (rs.getString("fsaccountid").equals(fsAccount))
                              {
                                out.println("<option value=" + rs.getString("fsaccountid") + " selected>" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "(" + FraudSystemService.getFSGateway(rs.getString("fsid")) + ")" + "</option>");
                              }
                              else
                              {
                                out.println("<option value=" + rs.getString("fsaccountid") + ">" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "(" + FraudSystemService.getFSGateway(rs.getString("fsid")) + ")" + "</option>");
                              }
                            }

                          }
                          catch (Exception e)
                          {
                            logger.error("Exception" + e);
                          }
                          finally
                          {
                            Database.closeConnection(conn);
                          }
                        %>
                      </select>
                    </div>
                  </div>

                  <div class="form-group col-md-2">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default" name="action" value="view"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=managePartnerAllocatedAccountList_Search%></button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=managePartnerAllocatedAccountList_Report_Table%></strong></h2>
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
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_Sr_No%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_PartnerID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_Fraud_System_accountid%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_Account_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_Is_Active%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=managePartnerAllocatedAccountList_Delete%></b></td>
                </tr>
                </thead>
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
                    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                    //System.out.println(ctoken);
                    out.println("<tr height=20px>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud System\" align=\"center\"" + style + ">&nbsp;"+srno+ "</td>");
                    out.println("<td valign=\"middle\" data-label=\"MerchantID\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"<input type=\"hidden\" name=\"username\" value=\""+temphash.get("partnerid")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction Status\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("fsaccountid"))+"<input type=\"hidden\" name=\"fsaccountid\" value=\""+temphash.get("fsaccountid")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction ID\" align=\"center\"" + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountname"))+"<input type=\"hidden\" name=\"accountname\" value=\""+temphash.get("accountname")+"\"></td>");
                    out.println("<td valign=\"middle\" data-label=\"AccountID\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("isActive")) + "<input type=\"hidden\" name=\"isTest\" value=\"" + temphash.get("isActive") + "\"></td>");
                    out.print("<td valign=\"middle\" data-label=\"Action\" align=\"center\"" + style + "><input class=\"button btn btn-default\" type=\"button\" value="+managePartnerAllocatedAccountList_Delete+"  onClick=\"return DoDelete("+temphash.get("partnerid") + ",'"+ctoken+"',"+ temphash.get("fsaccountid")+ ") \"></td>");
                    out.println("</tr>");
                  }

                %>
              </table>
            </div>

            <div id="showingid"><strong><%=managePartnerAllocatedAccountList_Showing_Page%> <%=pageno%> <%=managePartnerAllocatedAccountList_of%> <%=totalrecords%> <%=managePartnerAllocatedAccountList_records%></strong></div>
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
                out.println(Functions.NewShowConfirmation1(managePartnerAllocatedAccountList_Sorry,managePartnerAllocatedAccountList_no));
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
