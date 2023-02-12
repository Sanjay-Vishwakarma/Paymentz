<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 10/9/15
  Time: 11:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  session.setAttribute("submit","Merchant Fraud Accounts");
  Logger logger = new Logger("addNewPartnerMerchantFraudAccount.jsp");
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
%>
<html>
<head>
  <title><%=company%> | New Merchant Fraud Account</title>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <style type="text/css">
    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }
  </style>
<script type="application/javascript">
    $(function ()
    {
     /*$('#pid').on('change', function (request, response)
      {
        alert("inside partnerid ")
        $.ajax({
          url: "/partner/net/GetDetails",
          dataType: "json",
          data: {
            partnerid: $('#pid').val(),
            ctoken: $('#ctoken').val(),
            method: "patnersmember",
            term: request.term

          },
          success: function (data)
          {
            $('#memberid').find('option').not(':first').remove();
            $.each(data.aaData, function (i, data)
            {
              console.log(data.value);
              var div_data = "<option value=" + data.value + ">" + data.value + data.text + "</option>";
              $(div_data).appendTo('#memberid');
            });
          }
        });
        minLength: 0


      });*/
      // Retrieve Fraud account onchange of partner id value.
      $('#partnerid').on('change', function (request, response)
      {
        $.ajax({
          url: "/partner/net/GetDetails",
          dataType: "json",
          data: {
            partnerid: $('#partnerid').val(),
            ctoken: $('#ctoken').val(),
            method: "getPartnersAccount",
            term: request.term

          },
          success: function (data)
          {

            $('#fsAccount').find('option').not(':first').remove();
            $.each(data.aaData, function (i, data)
            {
              console.log(data.value);
              var div_data = "<option value=" + data.value + ">" + data.text + "</option>";
              $(div_data).appendTo('#fsAccount');
            });
          }
        });
        minLength: 0


      });
    });

  </script>

</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    LinkedHashMap memberidDetails=null;
    java.util.TreeMap<String,String> partneriddetails =null;
    partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Style1= "";
    String Style2= "";
    String dISABLED_Id="";

    if(Roles.contains("superpartner")){
      Style1= "style=\"display: none\"";
    }else{
      dISABLED_Id=String.valueOf(session.getAttribute("merchantid"));
      Style2= "style=\"display: none\"";
    }
    Functions function = new Functions();
    String partnerid =function.isValueNull(request.getParameter("pid")) ? request.getParameter("pid"):"";
    String memberId =function.isValueNull(request.getParameter("memberid")) ? request.getParameter("memberid"):"";
    String fsAccount = nullToStr(request.getParameter("fsAccount"));
    String subaccountName = nullToStr(request.getParameter("subaccountName"));
    String submerchantUsername = nullToStr(request.getParameter("submerchantUsername"));
    String submerchantPassword = nullToStr(request.getParameter("submerchantPassword"));
    String isActive = nullToStr(request.getParameter("isActive"));
    String isVisible = nullToStr(request.getParameter("isVisible"));
    String partner_id = session.getAttribute("merchantid").toString();
    String partnerid1 = session.getAttribute("partnerId").toString();

    if(function.isValueNull(partnerid)){
      memberidDetails=partner.getPartnerMembersDetail(partnerid);
    }
    else{
      memberidDetails = partner.getSuperPartnerMembersDetail(partner_id);
    }
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String addNewPartnerMerchantFraudAccount_Partner_Merchant = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Partner_Merchant")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Partner_Merchant") : "Partner Merchant Fraud Accounts";
    String addNewPartnerMerchantFraudAccount_PartnerID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_PartnerID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_PartnerID") : "Partner ID :";
    String addNewPartnerMerchantFraudAccount_select_PartnerID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_select_PartnerID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_select_PartnerID") : "Select Partner ID";
    String addNewPartnerMerchantFraudAccount_Partner_ID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Partner_ID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Partner_ID") : "Partner ID";
    String addNewPartnerMerchantFraudAccount_Fraud_System = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_System")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_System") : "Fraud System Account ID* :";
    String addNewPartnerMerchantFraudAccount_Select_AccountID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Select_AccountID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Select_AccountID") : "Select Account ID";
    String addNewPartnerMerchantFraudAccount_Fraud_Account = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Account")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Account") : "Fraud Account/Website* :";
    String addNewPartnerMerchantFraudAccount_submerchant = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_submerchant")) ? rb1.getString("addNewPartnerMerchantFraudAccount_submerchant") : "Fraud System SubMerchant Username :";
    String addNewPartnerMerchantFraudAccount_Password = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Password")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Password") : "Fraud System SubMerchant Password :";
    String addNewPartnerMerchantFraudAccount_MerchantID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_MerchantID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_MerchantID") : "Merchant ID* :";
    String addNewPartnerMerchantFraudAccount_Select_MerchantID = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Select_MerchantID")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Select_MerchantID") : "Select Merchant ID";
    String addNewPartnerMerchantFraudAccount_Fraud_Activation = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Activation")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Activation") : "Fraud Account Activation* :";
    String addNewPartnerMerchantFraudAccount_N = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_N")) ? rb1.getString("addNewPartnerMerchantFraudAccount_N") : "N";
    String addNewPartnerMerchantFraudAccount_Y = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Y")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Y") : "Y";
    String addNewPartnerMerchantFraudAccount_Fraud_Rule = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Rule")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Fraud_Rule") : "Fraud Rule Configuration Visibility* :";
    String addNewPartnerMerchantFraudAccount_Button = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Button")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Button") : "Button";
    String addNewPartnerMerchantFraudAccount_Save = StringUtils.isNotEmpty(rb1.getString("addNewPartnerMerchantFraudAccount_Save")) ? rb1.getString("addNewPartnerMerchantFraudAccount_Save") : "Save";
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->

    <%--Test Level--%>
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>" method="POST">
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
            <%--<button type="submit" value="Add Merchant Fraud Account" name="submit" class="btn btn-default" style="width:250px; font-size:14px">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Add Merchant Fraud Account
            </button>--%>
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>

      <%--<div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Partner Merchant Fraud Accounts</strong></h2>
              <div class="additional-btn">
                <form action="/partner/partnerMerchantFraudAccount.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button type="submit" class="btn btn-default" value="Add New Member" name="submit" style="width: 250px; font-size:14px">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Merchant Fraud Account Master
                  </button>
                </form>
              </div>
            </div>--%>

      <%--<div align="center" class="textb"><h5><b><u>Merchant Information</u></b></h5></div>--%>
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addNewPartnerMerchantFraudAccount_Partner_Merchant%></strong></h2>
              <div class="additional-btn">
                <%--<form action="/partner/memberlist.jsp?ctoken=<%=ctoken%>" method="POST">
                  <button type="submit" class="btn btn-default" value="Add New Member" name="submit" style="/*width: 250px;*/ font-size:14px;">
                    <i class="fa fa-sign-in"></i>
                    &nbsp;&nbsp;Merchant Master
                  </button>
                </form>--%>
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br>
            <%
              Functions functions = new Functions();
              StringBuffer statusMsg = (StringBuffer) request.getAttribute("statusMsg");
              if(statusMsg!=null && functions.isValueNull(statusMsg.toString()))
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+statusMsg.toString()+"</h5>");
              }

              StringBuffer success = (StringBuffer) request.getAttribute("success");
              if(success!=null && functions.isValueNull(success.toString()))
              {
                out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+success.toString()+"</h5>");
              }
            %>

            <br>

            <form action="/partner/net/AddNewPartnerMerchantFraudAccount?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <%--<input type="hidden" value="<%=partnerid%>"  id="partnerid">--%>
              <input type="hidden" value="<%=partnerid1%>" name="partnerid" id="partnerid">

              <div class="widget-content padding">

                <div class="form-group" <%=Style2%>>
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_PartnerID%></label>
                  <div class="col-md-4">
                    <%--<input name="pid" id="pid" value="<%=partnerid%>" class="form-control" autocomplete="on">--%>
                    <input name="pid" type="hidden" value="<%=partnerid%>">


                   <select name="partnerid" value="<%=partnerid%>" class="form-control" id="pid" >
                      <option value="" selected><%=addNewPartnerMerchantFraudAccount_select_PartnerID%></option>
                      <%
                        String Selected = "";
                        for(String pid : partneriddetails.keySet())
                        {
                          if(pid.toString().equals(partnerid))
                          {
                            Selected="selected";
                          }
                          else
                          {
                            Selected="";
                          }
                      %>
                      <option value="<%=pid%>" <%=Selected%>><%=partneriddetails.get(pid)%></option>
                      <%
                        }
                      %>

                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group" <%=Style1%>>
                  <div class="col-md-2"></div>
                  <label class="col-sm-4 control-label"><%=addNewPartnerMerchantFraudAccount_Partner_ID%></label>
                  <div class="col-md-4">
                    <input  class="form-control" value="<%=dISABLED_Id%>" disabled>
                    <input type="hidden" class="form-control" value="<%=dISABLED_Id%>" disabled>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_Fraud_System%></label>
                  <div class="col-md-4">
                    <select name="fsAccount" class="form-control" id="fsAccount">
                      <option value="" selected><%=addNewPartnerMerchantFraudAccount_Select_AccountID%></option>
                      <%
                        Connection conn = null;
                        try
                        {
                          conn= Database.getConnection();
                          ResultSet rs = Database.executeQuery("SELECT DISTINCT am.fsaccountid,am.accountname FROM partner_fsaccounts_mapping AS pam " +
                                  "JOIN fraudsystem_account_mapping AS am ON am.fsaccountid=pam.fsaccountid JOIN partners p ON p.partnerId = pam.partnerid " +
                                  "where pam.isActive='Y' and  (pam.partnerid= '"+partner_id+ "' OR p.superadminid= '"+partner_id+ "')" ,conn);
                          while (rs.next())
                          {
                            if(fsAccount.equals(rs.getString("fsaccountid")))
                            {
                              out.println("<option value=" + rs.getString("fsaccountid") + " selected>" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "</option>");
                            }else{
                              out.println("<option value=" + rs.getString("fsaccountid") + ">" + rs.getString("fsaccountid") + " - " + rs.getString("accountname") + "</option>");
                            }
                          }
                        }
                        catch (Exception e)
                        {
                          logger.error("Exception"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_Fraud_Account%><br>
                    (www.abc.com)</label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="subaccountName" value="<%=subaccountName%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_submerchant%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="submerchantUsername" value="<%=submerchantUsername%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_Password%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="submerchantPassword" value="<%=submerchantPassword%>">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_MerchantID%></label>
                  <div class="col-md-4">
                    <input name="memberid" id="member" value="<%=memberId%>" class="form-control" autocomplete="on">
                  <%--  <select name="memberId" class="form-control" id="memberid">
                      <option value="" selected><%=addNewPartnerMerchantFraudAccount_Select_MerchantID%></option>
                      <%
                        String isSelected="";
                        for(Object mid : memberidDetails.keySet())
                        {
                          if(mid.toString().equals(member))
                          {
                            isSelected="selected";
                          }
                          else
                          {
                            isSelected="";
                          }
                      %>
                      <option value="<%=mid%>" <%=isSelected%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                      <%
                        }
                      %>
                    </select>--%>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_Fraud_Activation%></label>
                  <div class="col-md-4">
                    <select name="isActive" size="1" class="form-control" style="width: 70px;">
                      <%
                        if(isActive.equals("N"))
                        {
                      %>
                      <option value="N"><%=addNewPartnerMerchantFraudAccount_N%></option>
                      <option value="Y"><%=addNewPartnerMerchantFraudAccount_Y%></option>
                      <%
                      }
                      else{
                      %>
                      <option value="Y"><%=addNewPartnerMerchantFraudAccount_Y%></option>
                      <option value="N"><%=addNewPartnerMerchantFraudAccount_N%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=addNewPartnerMerchantFraudAccount_Fraud_Rule%></label>
                  <div class="col-md-4">
                    <select name="isVisible" size="1" class="form-control" style="width: 70px;">
                      <%
                        if(isVisible.equals("N"))
                        {
                      %>
                      <option value="N"><%=addNewPartnerMerchantFraudAccount_N%></option>
                      <option value="Y"><%=addNewPartnerMerchantFraudAccount_Y%></option>
                      <%
                      }
                      else{
                      %>
                      <option value="Y"><%=addNewPartnerMerchantFraudAccount_Y%></option>
                      <option value="N"><%=addNewPartnerMerchantFraudAccount_N%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility: hidden;"><%=addNewPartnerMerchantFraudAccount_Button%></label>
                  <div class="col-md-4">
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=addNewPartnerMerchantFraudAccount_Save%></button>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <%--<div class="form-group col-md-12 has-feedback">
                  <center>
                    <label >&nbsp;</label>
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
                  </center>
                </div>--%>

              </div>
            </form>
          </div>
        </div>
      </div>
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
</body>
</html>