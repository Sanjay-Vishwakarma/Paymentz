<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.MerchantFraudAccountVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Sneha
  Date: 9/9/15
  Time: 4:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Logger logger = new Logger("partnerMerchantFraudAccount.jsp");
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Merchant Fraud Accounts");
%>
<html>
<head>
  <title><%=company%>| Partner Merchant Fraud Account </title>
  <style type="text/css">
    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }
  </style>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String partnerId=(String)session.getAttribute("merchantid");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String actionPartnerMerchantFraudAccount_update = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_update")) ? rb1.getString("actionPartnerMerchantFraudAccount_update") : "Update Merchant Fraud Account Configuration";
    String actionPartnerMerchantFraudAccount_MerchantID = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_MerchantID")) ? rb1.getString("actionPartnerMerchantFraudAccount_MerchantID") : "Merchant ID :";
    String actionPartnerMerchantFraudAccount_Fraud_Account = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Fraud_Account")) ? rb1.getString("actionPartnerMerchantFraudAccount_Fraud_Account") : "Fraud Account/Website :";
    String actionPartnerMerchantFraudAccount_Fraud_submerchant = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Fraud_submerchant")) ? rb1.getString("actionPartnerMerchantFraudAccount_Fraud_submerchant") : "Fraud System SubMerchant Username :";
    String actionPartnerMerchantFraudAccount_Fraud_password = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Fraud_password")) ? rb1.getString("actionPartnerMerchantFraudAccount_Fraud_password") : "Fraud System SubMerchant Password :";
    String actionPartnerMerchantFraudAccount_IsActive = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_IsActive")) ? rb1.getString("actionPartnerMerchantFraudAccount_IsActive") : "IsActive :";
    String actionPartnerMerchantFraudAccount_Y = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Y")) ? rb1.getString("actionPartnerMerchantFraudAccount_Y") : "Y";
    String actionPartnerMerchantFraudAccount_N = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_N")) ? rb1.getString("actionPartnerMerchantFraudAccount_N") : "N";
    String actionPartnerMerchantFraudAccount_IsVisible = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_IsVisible")) ? rb1.getString("actionPartnerMerchantFraudAccount_IsVisible") : "IsVisible :";
    String actionPartnerMerchantFraudAccount_Button = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Button")) ? rb1.getString("actionPartnerMerchantFraudAccount_Button") : "Button";
    String actionPartnerMerchantFraudAccount_Update = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Update")) ? rb1.getString("actionPartnerMerchantFraudAccount_Update") : "Update";
    String actionPartnerMerchantFraudAccount_Result = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_Result")) ? rb1.getString("actionPartnerMerchantFraudAccount_Result") : "Result";
    String actionPartnerMerchantFraudAccount_no = StringUtils.isNotEmpty(rb1.getString("actionPartnerMerchantFraudAccount_no")) ? rb1.getString("actionPartnerMerchantFraudAccount_no") : "No Records Found.";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <%--<div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              &lt;%&ndash;<h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Manage Merchant Fraud Account</strong></h2>&ndash;%&gt;
              <div class="additional-btn">--%>
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
          <%--<a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
          <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
          <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>--%>
        </div>
      </div>

      <%--<div class="widget-content padding">
        <div id="horizontal-form">

          <form action="/partner/net/PartnerMerchantFraudAccountList?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
            <input type="hidden" value="<%=ctoken%>" name="ctoken">
            <input type="hidden" value="<%=partnerId%>" name="partnerid">
            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

            <div class="form-group col-md-8">
              <label class="col-sm-3 control-label">Member ID</label>
              <div class="col-sm-4">
                <select name="mid" class="form-control">
                  <option value="" selected>ALL</option>
                  <%
                    Connection conn = null;
                    try
                    {
                      conn= Database.getConnection();
                      String query = "select memberid, company_name from members where activation='Y' and partnerId =? ORDER BY memberid ASC";
                      PreparedStatement pstmt = conn.prepareStatement( query );
                      pstmt.setString(1,partnerId);
                      ResultSet rs = pstmt.executeQuery();
                      while (rs.next())
                      {
                        out.println("<option value=\""+rs.getInt("memberid")+"\">"+rs.getInt("memberid")+" - "+rs.getString("company_name")+"</option>");
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
      </div>--%>
      <%--</div>
    </div>
  </div>--%>





      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=actionPartnerMerchantFraudAccount_update%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%
                MerchantFraudAccountVO fraudAccountVO = (MerchantFraudAccountVO) request.getAttribute("merchantAccountVO");
                if(fraudAccountVO != null)
                {
              %>
              <form action="/partner/net/ActionPartnerMerchantFraudAccount?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <input type="hidden" name="mappingid" value="<%=fraudAccountVO.getMerchantFraudAccountId() %>">

                <div class="widget-content padding">
                  <input type="hidden" name="merchantfraudserviceid" value="<%=fraudAccountVO.getMerchantFraudAccountId()%>">

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_MerchantID%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" name="mid1" class="form-control" value="<%=fraudAccountVO.getMemberId()%>" disabled>
                      <input type="hidden" name="mid" value="<%=fraudAccountVO.getMemberId()%>">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_Fraud_Account%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" name="fssubaccountid1" class="form-control" value="<%=fraudAccountVO.getSubAccountName()%>" disabled>
                      <input type="hidden" name="fssubaccountid" value="<%=fraudAccountVO.getFsSubAccountId()%> ">
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_Fraud_submerchant%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" name="submerchantUsername" class="form-control" value="<%=fraudAccountVO.getSubmerchantUsername()%>" >
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_Fraud_password%></label>
                    <div class="col-md-4">
                      <input type="text" size="30" name="submerchantPassword" class="form-control" value="<%=fraudAccountVO.getSubmerchantPassword()%>" >
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_IsActive%></label>
                    <div class="col-md-4">
                      <select name="isActive" class="form-control">
                        <% if(fraudAccountVO.getIsActive().equals("Y")){
                        %>
                        <option value="Y" selected><%=actionPartnerMerchantFraudAccount_Y%></option>
                        <option value="N"><%=actionPartnerMerchantFraudAccount_N%></option>
                        <%}else{%>
                        <option value="Y" ><%=actionPartnerMerchantFraudAccount_Y%></option>
                        <option value="N" selected><%=actionPartnerMerchantFraudAccount_N%></option>
                        <%}%>
                      </select>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=actionPartnerMerchantFraudAccount_IsVisible%></label>
                    <div class="col-md-4">
                      <select name="isVisible" class="form-control">
                        <% if(fraudAccountVO.getIsVisible().equals("Y")){
                        %>
                        <option value="Y" selected><%=actionPartnerMerchantFraudAccount_Y%></option>
                        <option value="N"><%=actionPartnerMerchantFraudAccount_N%></option>
                        <%}else{%>
                        <option value="Y" ><%=actionPartnerMerchantFraudAccount_Y%></option>
                        <option value="N" selected><%=actionPartnerMerchantFraudAccount_N%></option>
                        <%}%>
                      </select>
                    </div>
                    <div class="col-md-6"></div>
                  </div>


                  <div class="form-group">
                    <div class="col-md-3"></div>
                    <label class="col-md-3 control-label" style="visibility: hidden;"><%=actionPartnerMerchantFraudAccount_Button%></label>
                    <div class="col-md-4">
                      <input type="hidden" value="1" name="step">
                      <button type="submit" class="btn btn-default" id="submit" name="action" value="update" style="display: -webkit-box;"><i class="fa fa-sign-in"></i>&nbsp;&nbsp;<%=actionPartnerMerchantFraudAccount_Update%></button>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                  <%--<div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="1" name="step">
                        <button type="submit" class="btn btn-default" id="submit" name="action" value="update" style="display: -webkit-box;"><i class="fa fa-sign-in"></i>&nbsp;&nbsp;Update</button>
                      </center>
                  </div>--%>

                </div>

              </form>

            </div>
          </div>
        </div>
      </div>


      <%
          String msg = (String)request.getAttribute("msg");
          Functions functions=new Functions();
          if(functions.isValueNull(msg)){

            out.println("<div class=\"row reporttable\">");
            out.println("<div class=\"col-md-12\">\n" +
                    "    <div class=\"widget\">\n" +
                    "      <div class=\"widget-header\">\n" +
                    "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>"+actionPartnerMerchantFraudAccount_update+"</strong></h2>\n" +
                    "        <div class=\"additional-btn\">\n" +
                    "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                    "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                    "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "      <div class=\"widget-content padding\">");
            out.println(Functions.NewShowConfirmation1("Result", msg));
            out.println("</div>");
            out.println("</div>\n" +
                    "  </div>\n" +
                    "</div>");

          }
        }
        else if (request.getAttribute("updateMsg") != null)
        {

          out.println("<div class=\"widget-content padding\">");
          out.println(Functions.NewShowConfirmation1("Result", (String)request.getAttribute("updateMsg")));
          out.println("</div>");
        }
        else
        {
          out.println("<div class=\"row reporttable\">");
          out.println("<div class=\"col-md-12\">\n" +
                  "    <div class=\"widget\">\n" +
                  "      <div class=\"widget-header\">\n" +
                  "        <h2><i class=\"fa fa-table\"></i>&nbsp;&nbsp;<strong>"+actionPartnerMerchantFraudAccount_update+"</strong></h2>\n" +
                  "        <div class=\"additional-btn\">\n" +
                  "          <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                  "          <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                  "          <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                  "        </div>\n" +
                  "      </div>\n" +
                  "      <div class=\"widget-content padding\">");
          out.println(Functions.NewShowConfirmation1(actionPartnerMerchantFraudAccount_Result, actionPartnerMerchantFraudAccount_no));
          out.println("</div>");
          out.println("</div>\n" +
                  "  </div>\n" +
                  "</div>");
        }
      %>

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

</body>
</html>