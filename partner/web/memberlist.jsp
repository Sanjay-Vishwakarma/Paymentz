<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/17/14
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    session.setAttribute("submit","memberlist");
%>
<%@ include file="top.jsp"%>
<html>
<head>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
    <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>

    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> Merchant Management> Merchant Master</title>
    <style type="text/css">
        @media(max-width: 991px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit !important;
            }
        }
    </style>
        <style>
             #ui-id-2
             {
                overflow: auto;
                max-height: 350px;
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
</head>
<body>

<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    LinkedHashMap<String, Integer> memberidDetails= partner.getPartnerCountryDetail((String) session.getAttribute("merchantid"));
    String status=nullToStr(request.getParameter("status"));
    if (partner.isLoggedInPartner(session))
    {
        String Config =" ";

        String str = "";
        String memberid=Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
        String username=Functions.checkStringNull(request.getParameter("username"))==null?"":request.getParameter("username");
        //String pid=Functions.checkStringNull(request.getParameter("partnerlist"))==null?"":request.getParameter("partnerlist");
        String contactpersonname=Functions.checkStringNull(request.getParameter("contact_persons"));
        String contactpersonemail=Functions.checkStringNull(request.getParameter("contact_emails"));
        String companyname=Functions.checkStringNull(request.getParameter("company_name"));
        String country=Functions.checkStringNull(request.getParameter("country"));
        String dateType = Functions.checkStringNull(request.getParameter("datetype"));
        String mailresult = request.getAttribute("mailresult")!=null?(String)request.getAttribute("mailresult"):"";
        String fromdate=null;
        String todate=null;

        String pid = null;
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if(Roles.contains("superpartner")){
            pid=Functions.checkStringNull(request.getParameter("partnerlist"))==null?"":request.getParameter("partnerlist");
        }else{
            Config = "disabled";
            pid = String.valueOf(session.getAttribute("merchantid"));
        }

        str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if(pid!=null)str = str + "&partnerlist=" + pid;
        else
            pid="";
        if(memberid!=null)str = str + "&memberid=" + memberid;
        else
            memberid="";
        if(username!=null)str = str + "&username=" + username;
        else
            username="";
        if(contactpersonname!=null)str = str + "&contact_persons=" + contactpersonname;
        else
            contactpersonname="";
        if(contactpersonemail!=null)str = str + "&contact_emails=" + contactpersonemail;
        else
            contactpersonemail="";
        if(companyname!=null)str = str + "&company_name=" + companyname;
        else
            companyname="";
        if(status!=null)str = str + "&status=" + status;
        else
            status="";
        if(country!=null)str = str + "&country=" + country;
        else
            country="";
        if(dateType!=null)str = str + "&datetype=" + dateType;
        else
            dateType="";

        int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

        str = str + "&SRecords=" + pagerecords;
        //String date = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        if (fromdate!=null) str =str + "&fromdate="+fromdate;
        //else fromdate="";
        if (todate!=null) str =str + "&todate="+todate;
        //else todate="";

        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String memberlist_Merchant_Master = StringUtils.isNotEmpty(rb1.getString("memberlist_Merchant_Master")) ? rb1.getString("memberlist_Merchant_Master") : "Merchant Master";
        String memberlist_From = StringUtils.isNotEmpty(rb1.getString("memberlist_From")) ? rb1.getString("memberlist_From") : "From";
        String memberlist_To = StringUtils.isNotEmpty(rb1.getString("memberlist_To")) ? rb1.getString("memberlist_To") : "To";
        String memberlist_Date_Type = StringUtils.isNotEmpty(rb1.getString("memberlist_Date_Type")) ? rb1.getString("memberlist_Date_Type") : "Data Type";
        String memberlist_Creation_Date = StringUtils.isNotEmpty(rb1.getString("memberlist_Creation_Date")) ? rb1.getString("memberlist_Creation_Date") : "Creation Date";
        String memberlist_Activation_Date = StringUtils.isNotEmpty(rb1.getString("memberlist_Activation_Date")) ? rb1.getString("memberlist_Activation_Date") : "Activation Date";
        String memberlist_Partner_ID = StringUtils.isNotEmpty(rb1.getString("memberlist_Partner_ID")) ? rb1.getString("memberlist_Partner_ID") : "Partner ID";
        String memberlist_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("memberlist_Merchant_ID")) ? rb1.getString("memberlist_Merchant_ID") : "Merchant ID";
        String memberlist_Username = StringUtils.isNotEmpty(rb1.getString("memberlist_Username")) ? rb1.getString("memberlist_Username") : "Username";
        String memberlist_Contact_Person_Name = StringUtils.isNotEmpty(rb1.getString("memberlist_Contact_Person_Name")) ? rb1.getString("memberlist_Contact_Person_Name") : "Contact Person Name";
        String memberlist_Contact_Person_Email = StringUtils.isNotEmpty(rb1.getString("memberlist_Contact_Person_Email")) ? rb1.getString("memberlist_Contact_Person_Email") : "Contact Person Email";
        String memberlist_Company_Name = StringUtils.isNotEmpty(rb1.getString("memberlist_Company_Name")) ? rb1.getString("memberlist_Company_Name") : "Company Name";
        String memberlist_Status = StringUtils.isNotEmpty(rb1.getString("memberlist_Status")) ? rb1.getString("memberlist_Status") : "Status";
        String memberlist_All = StringUtils.isNotEmpty(rb1.getString("memberlist_All")) ? rb1.getString("memberlist_All") : "All";
        String memberlist_Test = StringUtils.isNotEmpty(rb1.getString("memberlist_Test")) ? rb1.getString("memberlist_Test") : "Test";
        String memberlist_Active = StringUtils.isNotEmpty(rb1.getString("memberlist_Active")) ? rb1.getString("memberlist_Active") : "Active";
        String memberlist_Inactive = StringUtils.isNotEmpty(rb1.getString("memberlist_Inactive")) ? rb1.getString("memberlist_Inactive") : "Inactive";
        String memberlist_Country = StringUtils.isNotEmpty(rb1.getString("memberlist_Country")) ? rb1.getString("memberlist_Country") : "Country";
        String memberlist_Path = StringUtils.isNotEmpty(rb1.getString("memberlist_Path")) ? rb1.getString("memberlist_Path") : "Path";
        String memberlist_Search = StringUtils.isNotEmpty(rb1.getString("memberlist_Search")) ? rb1.getString("memberlist_Search") : "Search";
        String memberlist_Report_Table = StringUtils.isNotEmpty(rb1.getString("memberlist_Report_Table")) ? rb1.getString("memberlist_Report_Table") : "Report Table";
        String memberlist_Sr_No = StringUtils.isNotEmpty(rb1.getString("memberlist_Sr_No")) ? rb1.getString("memberlist_Sr_No") : "Sr No";
        String memberlist_Partner_Id = StringUtils.isNotEmpty(rb1.getString("memberlist_Partner_Id")) ? rb1.getString("memberlist_Partner_Id") : "Partner Id";
        String memberlist_Merchant_Id = StringUtils.isNotEmpty(rb1.getString("memberlist_Merchant_Id")) ? rb1.getString("memberlist_Merchant_Id") : "Merchant Id";
        String memberlist_Merchant_Username = StringUtils.isNotEmpty(rb1.getString("memberlist_Merchant_Username")) ? rb1.getString("memberlist_Merchant_Username") : "Merchant Username";
        String memberlist_Contact_Email = StringUtils.isNotEmpty(rb1.getString("memberlist_Contact_Email")) ? rb1.getString("memberlist_Contact_Email") : "Contact Email";
        String memberlist_Account_Status = StringUtils.isNotEmpty(rb1.getString("memberlist_Account_Status")) ? rb1.getString("memberlist_Account_Status") : "Account Status";
        String memberlist_Action = StringUtils.isNotEmpty(rb1.getString("memberlist_Action")) ? rb1.getString("memberlist_Action") : "Action";
        String memberlist_Showing_Page = StringUtils.isNotEmpty(rb1.getString("memberlist_Showing_Page")) ? rb1.getString("memberlist_Showing_Page") : "Showing Page";
        String memberlist_of = StringUtils.isNotEmpty(rb1.getString("memberlist_of")) ? rb1.getString("memberlist_of") : "of";
        String memberlist_records = StringUtils.isNotEmpty(rb1.getString("memberlist_records")) ? rb1.getString("memberlist_records") : "records";
        String memberlist_Sorry = StringUtils.isNotEmpty(rb1.getString("memberlist_Sorry")) ? rb1.getString("memberlist_Sorry") : "Sorry";
        String memberlist_No_records_found = StringUtils.isNotEmpty(rb1.getString("memberlist_No_records_found")) ? rb1.getString("memberlist_No_records_found") : "No records found.";
        String memberlist_page_no = StringUtils.isNotEmpty(rb1.getString("memberlist_page_no")) ? rb1.getString("memberlist_page_no") : "Page number";
        String memberlist_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("memberlist_total_no_of_records")) ? rb1.getString("memberlist_total_no_of_records") : "Total number of records";


%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnermerchantsignup.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" value="memberlist" name="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 45px;width: 200px;" src="/partner/images/addnewmember.png">
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <br>
            <br>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=memberlist_Merchant_Master%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/MemberDetailList?ctoken=<%=ctoken%>" method="post" name="forms">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                                    <%
                                        Functions funct = new Functions();
                                        String message = (String) request.getAttribute("error");
                                        if(funct.isValueNull(message))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                        }
                                        String catcherror= (String)request.getAttribute("catchError");
                                        if(funct.isValueNull(catcherror))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + catcherror + "</h5>");
                                        }
                                        String success = (String) request.getAttribute("success");
                                        if(funct.isValueNull(success))
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + success + "</h5>");
                                        }

                                        //  pid=Functions.checkStringNull(request.getParameter("partnerlist"))==null?"":request.getParameter("partnerlist");;

                                    %>

                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_From%></label>
                                        <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_To%></label>
                                        <input type="text" size="16" name="todate" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=memberlist_Date_Type%></label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("activation_date".equals(dateType))
                                                {%>
                                            <option value="creation_date"><%=memberlist_Creation_Date%></option>
                                            <option value="activation_date" SELECTED><%=memberlist_Activation_Date%></option>
                                            <%}
                                            else
                                            {%>
                                            <option value="creation_date" SELECTED><%=memberlist_Creation_Date%></option>
                                            <option value="activation_date"><%=memberlist_Activation_Date%></option>
                                            <%}
                                            %>
                                        </select>
                                    </div>

                                    <div class="ui-widget form-group col-md-4 has-feedback">
                                        <label for="pid"><%=memberlist_Partner_ID%></label>
                                        <input name="partnerlist" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="partnerlist" value="<%=pid%>">
                                    </div>

                                    <div class="ui-widget form-group col-md-4 has-feedback">
                                        <label for="member"><%=memberlist_Merchant_ID%></label>
                                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_Username%></label>
                                        <input  type="text" name="username" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_Contact_Person_Name%></label>
                                        <input  type="text" name="contact_persons" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contactpersonname)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=memberlist_Contact_Person_Email%></label>
                                        <input  type="text" name="contact_emails" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contactpersonemail)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=memberlist_Company_Name%></label>
                                        <input  type="text" name="company_name" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(companyname)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_Status%></label>
                                        <select size="1" name="status" class="form-control">
                                            <option value="" <%=status.equals("")?"selected":""%>><%=memberlist_All%></option>
                                            <option value="T" <%=status.equals("T")?"selected":""%>><%=memberlist_Test%></option>
                                            <option value="Y" <%=status.equals("Y")?"selected":""%>><%=memberlist_Active%></option>
                                            <option value="N" <%=status.equals("N")?"selected":""%>><%=memberlist_Inactive%></option>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=memberlist_Country%></label>
                                        <select size="1" name="country" class="form-control">
                                            <option value="" selected="selected"><%=memberlist_All%></option>
                                                <%
                                                Functions functions1=new Functions();
                                                String selected3 = "";
                                                String key3 = "";
                                                for(Object mid:memberidDetails.keySet())
                                                {
                                                    key3 = String.valueOf(mid);
                                                    if (key3.equals(country))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                                    if(functions1.isValueNull(key3)){
                                            %>
                                            <option value="<%=key3%>" <%=selected3%>><%=key3%></option>
                                                <%
                                                }
                                                }
                                            %>
                                            </select>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label style="color: transparent;"><%=memberlist_Path%></label>
                                        <button type="submit" class="btn btn-default" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;<%=memberlist_Search%>
                                        </button>
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberlist_Report_Table%></strong></h2>
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
                                HashMap hash = (HashMap)request.getAttribute("transdetails");

                                HashMap temphash=null;
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
                                    hash = (HashMap)request.getAttribute("transdetails");
                                }
                                String msg= (String) request.getAttribute("msg");
                                if (funct.isValueNull(msg)){
                                    out.println(Functions.NewShowConfirmation1("",msg));
                                }
                                else if(records>0)
                                {
                            %>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post" action="/partner/net/ExportMemberDetails?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="toid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" name="spid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(companyname)%>" name="company_name">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" name="fromdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" name="todate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contactpersonname)%>" name="contact_persons">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contactpersonemail)%>" name="contact_emails">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>" name="datetype">
                                        <input type="hidden" value="<%=partnerid%>" name="partnerId">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/merchant/images/excel.png">

                                        </button>
                                    </form>
                                </div>
                            </div>
                            <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Sr_No%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Creation_Date%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Activation_Date%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Partner_Id%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Merchant_Id%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Merchant_Username%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Contact_Person_Name%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Company_Name%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Contact_Email%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Account_Status%></b></td>
                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Country%></b></td>
                                    <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=memberlist_Action%></b></td>
                                </tr>
                                </thead>
                                <%
                                    Functions functions = new Functions();
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
                                        temphash=(HashMap)hash.get(id);
                                        String activation_date = "";
                                        if (functions.isValueNull((String) temphash.get("activation_date")))
                                        {
                                            activation_date = functions.convertDtstampToDateTime((String)temphash.get("activation_date"));
                                        }
                                        else
                                        {
                                            activation_date = "-";
                                        }

                                        String login=ESAPI.encoder().encodeForHTML((String) temphash.get("login"));
                                        String unblock=partner.getUnblockStatus(login);
                                        String disabled="";
                                        if ("unlocked".equalsIgnoreCase(unblock)){
                                            disabled="disabled";
                                        }
                                        out.println("<tr id=\"maindata\">");
                                        out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Creation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) Functions.convertDtstampToDateTime((String)temphash.get("dtstamp")))+ "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Activation Date\" align=\"center\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(activation_date)+ "</td>");
                                        out.println("<td valign=\"middle\" data-label=\"Partner Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"<input type=\"hidden\" name=\"partnerlist\"value=\""+temphash.get("partnerId")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Merchant Id\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\"value=\""+temphash.get("memberid")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Merchant Username\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Person Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"contact_persons\" value=\""+temphash.get("contact_persons")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Company Name\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"<input type=\"hidden\" name=\"company_name\" value=\""+temphash.get("company_name")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Email\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(functions.getEmailMasking((String)temphash.get("contact_emails")))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Contact Email\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("activation"))+"<input type=\"hidden\" name=\"activation\" value=\""+temphash.get("activation")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Country\" align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("country"))+"<input type=\"hidden\" name=\"country\" value=\""+temphash.get("country")+"\"></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"action\" value=\"View\" class=\"gotoauto btn btn-default\" ><input type=\"hidden\" name=\"action\" value=\"View\">");
                                        out.println(requestParameter.toString());
                                        out.println("</form></td>");
                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+"><form action=\"/partner/net/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"chk\" value=\"Edit\" class=\"gotoauto btn btn-default\"><input type=\"hidden\" name=\"action\" value=\"modify\">");
                                        out.println(requestParameter.toString());
                                        out.println("</form></td>");

                                        out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\""+style+">");
                                        out.println("<form action=\"/partner/net/VerifyMemberRegistration?ctoken="+ctoken+"\" method=\"POST\">");
                                        out.println("<input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"partnerId\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("partnerId"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"emailtoken\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"pidName\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerId"))+"\">");
                                        out.println("<input type=\"hidden\" name=\"contact_persons\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"\">");
                                        out.println("<input type=\"submit\" name=\"Verify\" value=\"Verify\" class=\"gotoauto btn btn-default\">");
                                        out.println("</form>");
                                        out.println("</td>");

                                        out.println("<td align=\"center\" data-label=\"Action\" "+style+"><form action=\"/partner/net/UnblockMerchant?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"partnerid\" value=\""+ESAPI.encoder().encodeForHTML((String)temphash.get("partnerid"))+"\"><input type=\"hidden\" name=\"login\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("login"))+"\"><input type=\"hidden\" name=\"unblocked\"  value=\"\"+ESAPI.encoder().encodeForHTML((String) temphash.get(\"login\"))+\"\"><input type=\"submit\" name=\"Unblock\" value=\"Unblock\" id=\"unblock\" Onclick=\"return ConfirmUnblock();\" class=\"btn btn-default gotoauto\" width=\"100\"" +disabled+" >");
                                        out.println(requestParameter.toString());
                                        out.println("</form></td>");
                                        out.println("</tr>");
                                    }
                                %>
                            </table>

                            <%--<table align=center valign=top style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <td  align="left" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Total Records : <%=totalrecords%></td>
                                    <td  align="right" class="th0" style="padding-top: 13px;padding-bottom: 13px;">Page No : <%=pageno%></td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                    <td class="th0" style="padding-top: 13px;padding-bottom: 13px;">&nbsp;</td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td align=center>
                                        <jsp:include page="page.jsp" flush="true">
                                            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                            <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                            <jsp:param name="pageno" value="<%=pageno%>"/>
                                            <jsp:param name="str" value="<%=str%>"/>
                                            <jsp:param name="page" value="MemberDetailList"/>
                                            <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                            <jsp:param name="orderby" value=""/>
                                        </jsp:include>
                                    </td>
                                </tr>
                                </tbody>
                            </table>--%>



                        </div>
                    </div>
                </div>
            </div>

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
            <div id="showingid"><strong><%=memberlist_page_no%> <%=pageno%> <%=memberlist_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
            <div id="showingid"><strong><%=memberlist_total_no_of_records%>   <%=totalrecords%> </strong></div>
         <%--   <div id="showingid"><strong><%=memberlist_Showing_Page%>  <%=pageno%>  of  <%=totalrecords%> <%=memberlist_records%> </strong></div>
--%>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="MemberDetailList"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%
                    }
                    else
                    if(mailresult.equals("success"))
                    {
                        out.println(Functions.NewShowConfirmation1("Mail Result", "Member Verification Mail Sent.<br>Member Id : "+memberid));
                    }else
                    {
                        out.println(Functions.NewShowConfirmation1(memberlist_Sorry, memberlist_No_records_found));
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
    public static String getStatus(String str)
    {
        if(str.equals("Y"))
            return "Active";
        else if(str.equals("T"))
            return "Test";
        else if(str.equals("N"))
            return "Inactive";

        return str;
    }
%>