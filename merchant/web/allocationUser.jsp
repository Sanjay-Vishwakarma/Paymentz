<%@ page import="com.manager.MerchantModuleManager" %>
<%@ page import="com.manager.vo.MerchantSubModuleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.MerchantModuleEnum" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="functions.jsp" %>
<%@ include file="Top.jsp" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/9/15
  Time: 9:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "User Management");
    String message = (String) request.getAttribute("message");
    Functions functions = new Functions();
    Logger log = new Logger("/allocationUser.jsp");
    String style = "class=td1";
    String userId = (String) request.getParameter("userid");
    String merchantId = (String) request.getParameter("merchantid");
    log.error("merchantId--->"+merchantId);

    String role = (String)session.getAttribute("role");
    MerchantModuleManager merchantModuleManager = new MerchantModuleManager();
    HashMap<String, List> hash;
    if(role.contains("submerchant")){
        hash = merchantModuleManager.merchantUserModuleList(userId);
        log.error("submerhant -->"+hash);
    }else
    {
         hash = merchantModuleManager.getMerchantModuleList();
    }
    Set<String> moduleSet1 = merchantModuleManager.getMerchantAccessModuleSet(userId);
    Set<String> notAllocate = merchantModuleManager.getMerchantRestrictedModuleSet(merchantId);
    List<MerchantSubModuleVO> list = null;
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String allocationUser_Go_Back = StringUtils.isNotEmpty(rb1.getString("allocationUser_Go_Back"))?rb1.getString("allocationUser_Go_Back"): "Go Back";
    String allocationUser_Module_Management = StringUtils.isNotEmpty(rb1.getString("allocationUser_Module_Management"))?rb1.getString("allocationUser_Module_Management"): "Module Management";
    String allocationUser_Merchant_User = StringUtils.isNotEmpty(rb1.getString("allocationUser_Merchant_User"))?rb1.getString("allocationUser_Merchant_User"): "Merchant User";
    String allocationUser_Module = StringUtils.isNotEmpty(rb1.getString("allocationUser_Module"))?rb1.getString("allocationUser_Module"): "Merchant Module";
    String allocationUser_Module_Name = StringUtils.isNotEmpty(rb1.getString("allocationUser_Module_Name"))?rb1.getString("allocationUser_Module_Name"): "Module Name";
    String allocationUser_Save = StringUtils.isNotEmpty(rb1.getString("allocationUser_Save"))?rb1.getString("allocationUser_Save"): "Save";

%>
<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
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
    input[type=radio], input[type=checkbox] {
        transform: scale(2);
        -ms-transform: scale(2);
        -webkit-transform: scale(2);
        padding: 10px;
    }
</style>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form action="/merchant/servlet/MemberUserList?ctoken=<%=ctoken%>" name="form" method="post">
                <div class="pull-right">
                    <div class="btn-group">
                        <%
                            Enumeration<String> aName = request.getParameterNames();
                            while (aName.hasMoreElements())
                            {
                                String name = aName.nextElement();
                                String value = request.getParameter(name);
                                if (value == null || value.equals("null"))
                                {
                                    value = "";
                                }
                        %>
                        <input type=hidden name=<%=name%> value=<%=value%>>
                        <%
                            }
                        %>
                        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i
                                class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=allocationUser_Go_Back%>
                        </button>
                    </div>
                </div>
            </form>
            <br><br>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=allocationUser_Module_Management%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%
                           /* String errormsg1 = (String) request.getAttribute("error");
                            if (errormsg1 != null)
                            {
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                            }*/
                            if (functions.isValueNull(message))
                            {
                                out.println(Functions.NewShowConfirmation1("Result", message));
                            }
                        %>
                        <div class="widget-content padding">
                            <form action="/merchant/servlet/AllocationUser?ctoken=<%=ctoken%>" method="post"
                                  name="forms" class="form-horizontal">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=userId%>" name="userid">
                                <input type="hidden" value="<%=merchantId%>" name="merchantid">
                                <input type="hidden" value="<%=request.getParameter("login")%>" name="login">

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-2 control-label"><%=allocationUser_Merchant_User%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="login"
                                               value="<%=ESAPI.encoder().encodeForHTML((String)request.getParameter("login"))%>"
                                               disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-12 portlets ui-sortable">
                                        <div class="widget">
                                            <div class="widget-header transparent">
                                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=allocationUser_Module%>
                                                    </strong></h2>

                                                <div class="additional-btn">
                                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                                    <a href="#" class="widget-toggle"><i
                                                            class="icon-down-open-2"></i></a>
                                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                                </div>
                                            </div>
                                            <div class="widget-content padding">
                                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                                <table align=center width="50%"
                                                       class="display table table table-striped table-bordered table-hover dataTable"
                                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                    <thead>
                                                    <tr>
                                                        <td valign="left" align="left"
                                                            style="color: white;padding-top: 13px;padding-bottom: 13px;">
                                                            <b><%=allocationUser_Module_Name%></b>&nbsp;&nbsp;
                                                            <input type="checkbox" name="select-all" id="select-all"
                                                                   onclick="ToggleAll(this)"/>
                                                        </td>
                                                    </tr>
                                                    </thead>
                                                </table>
                                                    <%
                             Set<String> keys=hash.keySet();
                             for (String key1:keys){
                               String modules[]=key1.split("-");
                               String moduleId=modules[0];
                               String moduleName=modules[1];
                               list=hash.get(moduleId+"-"+moduleName);
                               String checked="";
                               if (notAllocate.contains(moduleName)){
                                  continue;
                               }
                               if (moduleSet1.contains(moduleName)){
                                    checked="checked";
                               }
                          %>
                                                <table align=center width="50%"
                                                       class="display table table table-striped table-bordered table-hover dataTable"
                                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                    <tr>
                                                        <td align="left" style="width: 20%">
                                                        </td>
                                                        <td align="left">
                                                            <input type="checkbox" name="moduleid" <%=checked%>
                                                                   style=" width: 10px;float: left;"
                                                                   value=<%=moduleId%>>&nbsp;&nbsp;<%=moduleName.replace("_", " ")%>
                                                            <br>
                                                        </td>
                                                    </tr>
                                                    <%
                                                        if (list.size() > 0)
                                                        {
                                                    %>
                                                    <tr>
                                                        <table align=center width="50%"
                                                               class="display table table table-striped table-bordered table-hover dataTable"
                                                               style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                            <%
                                                                for (MerchantSubModuleVO merchantSubModuleVO : list)
                                                                {
                                                                    String check1 = "";
                                                                    String disabled = "";
                                                                    if (notAllocate.contains(merchantSubModuleVO.getSubModuleName()))
                                                                    {
                                                                        continue;
                                                                    }
                                                                    if (moduleSet1.contains(merchantSubModuleVO.getSubModuleName()))
                                                                    {
                                                                        check1 = "checked";
                                                                    }
                                                                    String style1 ="";
                                                                    if(MerchantModuleEnum.GENERATE_KEY.toString().equals(merchantSubModuleVO.getSubModuleName())){
                                                                        style1 = "display:none";
                                                                    }

                                                                    if(MerchantModuleEnum.CHECKOUT_PAGE.toString().equals(merchantSubModuleVO.getSubModuleName()))
                                                                    {
                                                                        style1 = "display:none";
                                                                    }
                                                            %>
                                                            <tr style="<%=style1%>">
                                                                <td align="left" style="width: 30%">
                                                                </td>
                                                                <td align="left" <%=style%>>

                                                                    <input type="checkbox" <%=check1%> style=" width: 10px;float: left;" name="submoduleid" value=<%=merchantSubModuleVO.getSubModuleId()%>>&nbsp;&nbsp;<%=merchantSubModuleVO.getSubModuleName().replace("_", " ")%>
                                                                </td>
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

                                                <div class="form-group col-md-5"></div>
                                                <div class="form-group col-md-4">
                                                    <button type="submit" class="btn btn-default" value="Save">
                                                        <i class="fa fa-sign-in"></i>
                                                        &nbsp;&nbsp;<%=allocationUser_Save%>
                                                    </button>
                                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>