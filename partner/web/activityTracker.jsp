<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="net.partner.PartnerFunctions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.dao.ActivityTrackerDAO" %>
<%@ page import="com.manager.enums.ActivityLogParameters" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 7/28/14
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company              = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    String partnerid            = String.valueOf(session.getAttribute("partnerId"));
    PartnerFunctions partner1   = new PartnerFunctions();
    ActivityTrackerDAO activityTrackerDAO = new ActivityTrackerDAO();
    Functions functions= new Functions();
%>
<%!
    Logger logger=new Logger("activityTracker.jsp");
%>
<html>
<head>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%>  Bank Accounts</title>
    <style type="text/css">
        @media(max-width: 767px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit!important;
            }
        }

    </style>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>
</head>


<body>
<%

    ctoken          = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    session.setAttribute("submit","activityTracker");
    String fromdate = null;
    String todate   = null;
    if (partner.isLoggedInPartner(session))
    {
        String str  = "";
        Date date                       = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date         = originalFormat.format(date);
        date.setDate(1);
        String fromDate     = originalFormat.format(date);

        fromdate            = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate              = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        String modulename   = nullToStr(Functions.checkStringNull(request.getParameter("modulename")));
        String role         = Functions.checkStringNull(request.getParameter("role"))==null?"":request.getParameter("role");
        String username     = nullToStr(Functions.checkStringNull(request.getParameter("username")));
        String partnerId    = (String) session.getAttribute("merchantid");

        if (modulename != null) str = str + "&modulename=" + modulename;
        if (role != null) str = str + "&role=" + role;
        if (username != null) str = str + "&username=" + username;
        if (partnerId != null) str = str + "&partnerId=" + partnerId;
        if (fromdate!=null) str =str + "&fromdate="+fromdate;
        if (todate!=null) str =str + "&todate="+todate;

        String roles                                = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        ResourceBundle rb1 = null;
        String language_property1                   = (String)session.getAttribute("language_property");
        rb1                                         = LoadProperties.getProperty(language_property1);

%>

<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Activity Tracker </strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>

                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form  name="forms" method="post" action="/partner/net/ActivityTracker?ctoken=<%=ctoken%>" <%--class="form-horizontal"--%>>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                    <%

                                        String pid      = null;
                                        String Config   ="";
                                        String Roles    = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                        if(Roles.contains("superpartner")){
                                            pid = Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
                                        }else{
                                            Config  = "disabled";
                                            pid     = String.valueOf(session.getAttribute("merchantid"));
                                        }
                                        if(pid!=null)str = str + "&pid=" + pid;
                                        else
                                            pid="";

                                        str             = str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                        int pageno      = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                                        int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                                        str = str + "&SRecords=" + pagerecords;
                                        //str = str + "&bank=" + bank;
                                    %>
                                    <%
                                        String message = (String) request.getAttribute("error");
                                        if(partner1.isValueNull(message))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                        }

                                        String msg = (String) request.getAttribute("message");
                                        //String errormsg=(String)request.getAttribute("message");
                                        if(partner1.isValueNull(msg))
                                        {
                                            //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                                        }

                                        String successMsg = (String) request.getAttribute("success");
                                        //String errormsg=(String)request.getAttribute("message");
                                        if(partner1.isValueNull(successMsg))
                                        {
                                            //out.println("<center><font class=\"textb\"><b>"+msg+"</b></font></center><br>");
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMsg + "</h5>");
                                        }
                                    %>



                                    <div class="form-group col-md-4 has-feedback">
                                        <label>From</label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label>To</label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Partner Id *</label>
                                        <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="pid" value="<%=pid%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label> Username</label>
                                        <input class="form-control" maxlength="22" type="text" name="username" id="username" value="<%=username%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Module Name</label>
                                        <select size="1" name="modulename" id="modulename" class="form-control">
                                            <option value="">All</option>

                                            <%
                                                String interfaceName = ActivityLogParameters.PARTNER.toString()+"','"+ActivityLogParameters.MERCHANT.toString();

                                                TreeMap<String, String> parameter1  = activityTrackerDAO.getModuleNameByInterface(interfaceName);
                                                if(functions.isValueNull(roles) && !roles.contains("superpartner") )
                                                {
                                                    parameter1.remove("Partner Default Configuration");
                                                    parameter1.remove("Partner Master");
                                                }


                                                Set statusSet1                      = parameter1.keySet();
                                                Iterator iterator1                  = statusSet1.iterator();
                                                String selected1                    = "";
                                                String key1                         = "";
                                                String value1                       = "";

                                                while (iterator1.hasNext())
                                                {
                                                    key1 = (String)iterator1.next();
                                                    value1 = (String) parameter1.get(key1);

                                                    if (key1.equals(modulename))
                                                        selected1 = "selected";
                                                    else
                                                        selected1 = "";
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key1)%>" <%=selected1%>><%=ESAPI.encoder().encodeForHTML(value1)%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="color: transparent;">Search</label>
                                        <button type="submit" class="btn btn-default" style="display: inherit;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                                    </div>

                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>

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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-x: auto;">

                            <%  StringBuffer requestParameter           = new StringBuffer();
                                Enumeration<String> stringEnumeration   = request.getParameterNames();
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


                                Hashtable hash          = (Hashtable) request.getAttribute("activitylist");
                                Hashtable temphash      = null;
                                int records             = 0;
                                int totalrecords        = 0;

                                String errormsg       = (String)request.getAttribute("message");
                                String currentblock   = request.getParameter("currentblock");
                                if(currentblock==null)
                                    currentblock="1";
                                try
                                {
                                    records      =Integer.parseInt((String)hash.get("records"));
                                    totalrecords =Integer.parseInt((String)hash.get("totalrecords"));
                                }
                                catch(Exception ex)
                                {

                                }
                                if(records>0)
                                {
                            %>
                            <table class="table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Sr No</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Activity Date</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Interface</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Role</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">User Name</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">User ID</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Action</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Module Name</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">User Description</td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">Label Name</td>
                                </tr>
                                </thead>

                                <%

                                    String style="class=td1";
                                    String ext="light";

                                    for(int pos=1 ;pos <= records ;pos++)
                                    {
                                        //String id=Integer.toString(pos);

                                        String id   = Integer.toString(pos);
                                        style       = "class=\"tr" + (pos + 1) % 2 + "\"";
                                        int srno    = Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                                        temphash        =   (Hashtable)hash.get(id);
                                        String separator    ="-";
                                        String username1    ="";
                                        String username_id  ="";
                                        String usernameid   = ESAPI.encoder().encodeForHTML((String)temphash.get("user_name"));
                                        int index = usernameid.lastIndexOf('-');
                                        String lastString = usernameid.substring(index +1);
                                        logger.error("lastString::: " + lastString);
                                        username_id= lastString;
                                        logger.error("Substring before last separator = " + usernameid.substring(0, index));
                                        username1= usernameid.substring(0,index);
                                        /*String[] arrSplit   = usernameid.split(separator);
                                        for (int i=0; i < arrSplit.length; i++){
                                            username1   = arrSplit[0];
                                            username_id = arrSplit[1];

                                        }*/
                                %>
                                <tr>
                                    <td align="center" <%=style%>><%=srno%></td>
                                    <td align="center" <%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))%></td>
                                    <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("interface"))%></td>
                                    <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("role"))%></td>
                                    <td align="center"<%=style%>><%=username1%></td>
                                    <td align="center"<%=style%>><%=username_id%></td>
                                    <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("action"))%></td>
                                    <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("module_name"))%></td>
                                    <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("description"))%></td>
                                    <td <%=style%>><%=ESAPI.encoder().encodeForHTML((String)temphash.get("lable_values"))%></td>
                                </tr>
                                <%
                                    }
                                %>
                            </table>

                            <% int TotalPageNo;
                                if(totalrecords%pagerecords!=0)
                                {
                                    TotalPageNo=totalrecords/pagerecords+1;
                                }
                                else
                                {
                                    TotalPageNo=totalrecords/pagerecords;
                                }
                            %>
                            <div id="showingid"><strong>Page number <%=pageno%> Of <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong>Total number of records   <%=totalrecords%> </strong></div>

                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="ActivityTracker"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>


                            <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
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
            </div>
        </div>
    </div>
</div>
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