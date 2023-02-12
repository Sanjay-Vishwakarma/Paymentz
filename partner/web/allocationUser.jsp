<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp"%>
<%@ page import="com.manager.MerchantModuleManager" %>
<%@ page import="com.manager.enums.MerchantModuleEnum" %>
<%@ page import="com.manager.vo.MerchantSubModuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/9/15
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","memberChildList");
  Logger logger=new Logger("allocationUser");
  String style="class=td1";
  String userId=request.getParameter("userid");
  String merchantId=request.getParameter("memberid");
  String error = (String) request.getAttribute("error");
  String message = (String) request.getAttribute("message");
  String successMessage = (String) request.getAttribute("success");

  MerchantModuleManager merchantModuleManager=new MerchantModuleManager();
  HashMap<String,List> merchantModuleList = merchantModuleManager.getMerchantModuleList();
  Set<String> merchantAllocatedModuleList =merchantModuleManager.getMerchantAccessModuleSet(userId);
  Set<String> merchantRestrictedList =merchantModuleManager.getMerchantRestrictedModuleSet(merchantId);
  List<MerchantSubModuleVO> subModuleList =null;
  Functions functions = new Functions();
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String allocationUser_Merchant_Module_Management = StringUtils.isNotEmpty(rb1.getString("allocationUser_Merchant_Module_Management")) ? rb1.getString("allocationUser_Merchant_Module_Management") : "Merchant Module Management";
  String allocationUser_Merchant_User = StringUtils.isNotEmpty(rb1.getString("allocationUser_Merchant_User")) ? rb1.getString("allocationUser_Merchant_User") : "Merchant User";
  String allocationUser_Module_Name = StringUtils.isNotEmpty(rb1.getString("allocationUser_Module_Name")) ? rb1.getString("allocationUser_Module_Name") : "Module Name";
  String allocationUser_Save = StringUtils.isNotEmpty(rb1.getString("allocationUser_Save")) ? rb1.getString("allocationUser_Save") : "Save";
%>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> | Merchant Module Management</title>
 <script>
  function ToggleAll(checkbox)
  {
  console.log("yes");
  if (checkbox.checked)
  {
  // Iterate each checkbox
  $(':checkbox').each(function ()
  {
  this.checked = true;
  });
  }
  else
  {
  $(':checkbox').each(function ()
  {
  this.checked = false;
  });
  }
  }
 </script>
</head>
<style type="text/css">
  input[type=radio], input[type=checkbox]{
    transform: scale(2);
    -ms-transform: scale(2);
    -webkit-transform: scale(2);
    padding: 10px;
  }
</style>
<body>
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/MemberUserList?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements()){
                String name=stringEnumeration.nextElement();
                if("accountid".equals(name)){
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
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=allocationUser_Merchant_Module_Management%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%
                if(functions.isValueNull(error)){
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+error+"</h5>");
                }
                if(functions.isValueNull(message)){
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+message+"</h5>");
                }
                if(functions.isValueNull(successMessage)){
                  out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+successMessage+"</h5>");
                }
              %>
              <form action="/partner/net/AllocationUser?ctoken=<%=ctoken%>" method="post" name="forms" class="form-horizontal">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <input type="hidden" value="<%=request.getParameter("userid")%>" name="userid">
                <input type="hidden" value="<%=request.getParameter("memberid")%>" name="memberid">
                <input type="hidden" value="<%=request.getParameter("login")%>" name="login">
                <div class="widget-content padding">
                  <div class="form-group">
                    <label class="col-md-4 control-label"><%=allocationUser_Merchant_User%></label>
                    <div class="col-md-4">
                      <input type="text" class="form-control" name="login" value="<%=ESAPI.encoder().encodeForHTML(request.getParameter("login"))%>"disabled>
                    </div>
                    <div class="col-md-6"></div>
                  </div>
                  <div class="widget-content padding">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                      <thead>
                      <tr>
                        <td  valign="left" align="left" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" ><b><%=allocationUser_Module_Name%></b>&nbsp;&nbsp;
                          <input type="checkbox" name="select-all" id="select-all"
                                 onclick="ToggleAll(this)"/>
                        </td>
                      </tr>
                      </thead>
                    </table>
                    <%
                      Set<String> keys= merchantModuleList.keySet();
                      for (String key1:keys){
                        String modules[]=key1.split("-");
                        String moduleId=modules[0];
                        String moduleName=modules[1];
                        subModuleList = merchantModuleList.get(moduleId+"-"+moduleName);
                        String checked="";
                        if (merchantRestrictedList.contains(moduleName)){
                          continue;
                        }
                        if (merchantAllocatedModuleList.contains(moduleName)){
                          checked="checked";
                        }
                    %>
                    <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                      <tr>
                        <td align="left" style="width: 20%">
                        </td>
                        <td align="left">
                          <input type="checkbox" name="moduleid" <%=checked%> style=" width: 10px;float: left;" value=<%=moduleId%>>&nbsp;&nbsp;<%=moduleName.replace("_"," ")%><br>
                        </td>
                      </tr>
                      <%
                        if(subModuleList.size()>0)
                        {
                      %>
                      <tr>
                        <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                          <%
                            for (MerchantSubModuleVO merchantSubModuleVO: subModuleList)
                            {
                              String check1="";
                              String disabled="";
                              if(merchantRestrictedList.contains(merchantSubModuleVO.getSubModuleName()))
                              {
                                continue;
                              }
                              if(merchantAllocatedModuleList.contains(merchantSubModuleVO.getSubModuleName())){
                                check1="checked";
                              }
                              if(MerchantModuleEnum.GENERATE_KEY.toString().equals(merchantSubModuleVO.getSubModuleName())){
                                disabled="disabled";
                                }
                                String style1 ="";
                                if(MerchantModuleEnum.CHECKOUT_PAGE.toString().equals(merchantSubModuleVO.getSubModuleName()))
                               {
                                style1 = "display:none";
                               }
                              %>
                          <tr style="<%=style1%>">
                            <td align="left" style="width: 30%">
                            </td>
                            <td align="left" <%=style%>>

                            <input type="checkbox" <%=check1%> <%=disabled%> style=" width: 10px;float: left;" name="submoduleid" value=<%=merchantSubModuleVO.getSubModuleId()%>>&nbsp;&nbsp;<%=merchantSubModuleVO.getSubModuleName().replace("_"," ")%></td>
                          </tr>
                          <%
                            }
                          %>
                        </table>
                      </tr>
                      <%
                          }
                        }
                      %>
                    </table>
                    <br>
                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="1" name="step">
                        <button type="submit" class="btn btn-default" id="submit" name="action" value="Save" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=allocationUser_Save%></button>
                      </center>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</div>
</body>
</html>