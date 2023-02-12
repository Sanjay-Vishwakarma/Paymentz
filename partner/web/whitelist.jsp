<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.WhitelistingDetailsVO" %>
<%@ page import="java.util.*" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="whitelistTab.jsp" %>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <style type="text/css">
        #main {
            background-color: #ffffff
        }
        #expiryDate{
            width:100%;
        }
        #expiryDate[readonly]{
            background-color: #ffffff;
        }
        :target:before {
            content: "";
            display: block;
            height: 50px;
            margin: -50px 0 0;
        }

        .table > thead > tr > th {
            font-weight: inherit;
        }

        :target:before {
            content: "";
            display: block;
            height: 90px;
            margin: -50px 0 0;
        }

        footer {
            border-top: none;
            margin-top: 0;
            padding: 0;
        }

        /********************Table Responsive Start**************************/
        @media (max-width: 640px) {
            table {
                border: 0;
            }

            table thead {
                display: none;
            }

            tr:nth-child(odd), tr:nth-child(even) {
                background: #ffffff;
            }

            table td {
                display: block;
                border-bottom: none;
                padding-left: 0;
                padding-right: 0;
            }

            table td:before {
                content: attr(data-label);
                float: left;
                width: 100%;
                font-weight: bold;
            }
        }

        table {
            width: 100%;
            max-width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
            display: table;
            border-collapse: separate;
            border-color: grey;
        }

        thead {
            display: table-header-group;
            vertical-align: middle;
            border-color: inherit;
        }

        tr:nth-child(odd) {
            background: #F9F9F9;
        }

        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }

        th {
            padding-right: 1em;
            text-align: left;
            font-weight: bold;
        }

        td, th {
            display: table-cell;
            vertical-align: inherit;
        }

        tbody {
            display: table-row-group;
            vertical-align: middle;
            border-color: inherit;
        }

        td {
            padding-top: 6px;
            padding-bottom: 6px;
            padding-left: 10px;
            padding-right: 10px;
            vertical-align: top;
            border-bottom: none;
        }

        .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
            border-top: 1px solid #ddd;
        }
        .eye-icon
        {
            float: right;
            z-index: 2;
            margin-right: 5px;
        }

        /********************Table Responsive Ends**************************/
    </style>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.2.0/css/all.css" integrity="sha384-hWVjflwFxL6sNzntih27bfxkr27PmbbK/iSvJ+a4+0owXq79v+lsFkW54bOGbiDQ" crossorigin="anonymous">
    <link rel="stylesheet" href="/partner/NewCss/css/jquery.dataTables.min.css">
    <script src="/partner/NewCss/js/jquery.dataTables.min.js"></script>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script src="/merchant/javascript/hidde.js"></script>
    <link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/content.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        function onChange()
        {
            var x=document.getElementById("isTemp");
            if("Y"=== x.value)
            {
                document.getElementById("upload").disabled=true;
            }else
            {
                document.getElementById("upload").disabled=false;
            }
        }
        function unMasking(spanid,inputid,msg)
        {
            var x=document.getElementById(inputid);
            $("#" + spanid).removeClass('fa-eye-slash').addClass('fa-eye');
            x.innerText=msg;
        }
        function masking(spanid,inputid,msg)
        {
            var x=document.getElementById(inputid);
            $("#" + spanid).removeClass('fa-eye').addClass('fa-eye-slash');
            x.innerText=msg;
        }
        $(function() {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd",endDate:'+10y'});
        });
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function Delete()
        {
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    break;
                }
            }
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to Update all selected Data."))
            {
                document.BinDelete.submit();
            }
        }
    </script>
</head>
<body onload="onChange()">
    <%
ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
		String partnerid = session.getAttribute("partnerId").toString();
String memberid=request.getParameter("memberid")==null?"":request.getParameter("memberid");
String accountid=request.getParameter("accountid")==null?"":request.getParameter("accountid");
String firstSix=request.getParameter("firstsix")==null?"":request.getParameter("firstsix");
String lastFour=request.getParameter("lastfour")==null?"":request.getParameter("lastfour");
String emailAddr=request.getParameter("emailAddr")==null?"":request.getParameter("emailAddr");
String name1=request.getParameter("name")==null?"":request.getParameter("name");
String ipAddress=request.getParameter("ipAddress")==null?"":request.getParameter("ipAddress");
String expiryDate=Functions.checkStringNull(request.getParameter("expiryDate")) == null ? "" : request.getParameter("expiryDate");
String istemp=request.getParameter("isTemp");
String pid=request.getParameter("pid")==null?"":request.getParameter("pid");
String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
        pid = String.valueOf(session.getAttribute("merchantid"));
        Config = "disabled";
    }
Functions functions = new Functions();
//PartnerFunctions partnerFunctions=new PartnerFunctions();
String str="";

if(memberid!=null)str = str + "&memberid=" + memberid;
if(pid!=null)str = str + "&pid=" + pid;
if(accountid!=null)str = str + "&accountid=" + accountid;
if(firstSix!=null)str = str + "&firstsix=" + firstSix;
if(lastFour!=null)str = str + "&lastfour=" + lastFour;
if(emailAddr!=null)str = str + "&emailAddr=" + emailAddr;
if(name1!=null)str = str + "&name=" + name1;
if(ipAddress!=null)str = str + "&ipAddress=" + ipAddress;
if(expiryDate!=null)str = str + "&expiryDate=" + expiryDate;
if(istemp!=null)str = str + "&isTemp=" + istemp;


str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
str = str + "&SRecords=" + pagerecords;
%>
<body>
<div class="content-page">
    <div class="content" style="padding:0px 20px ;margin: 0px;">
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;WhiteList Card</strong>
                            </h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="pull-right">
                            <div class="btn-group">
                                <form action="/partner/uploadBins.jsp?ctoken=<%=ctoken%>" method="POST">
                                    <button class="btn-lg" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 12px 16px;font-size: 16px;cursor: pointer;">Upload Bulk Card
                                    </button>
                                </form>
                            </div>
                        </div>
                        <div class="widget-content">
                            <div id="horizontal-form">
                                <form action="/partner/net/WhiteListCard?ctoken=<%=ctoken%>" method="post" name="forms">
                                    <div align="center">
                                        <input type="hidden" value="<%=ctoken%>" id="ctoken">
                                        <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                            <%
                                                String errormsg1 = (String) request.getAttribute("error");
                                                String msg = (String) request.getAttribute("msg");
                                                if (functions.isValueNull(errormsg1))
                                                {
                                                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                                }
                                                if(functions.isValueNull(msg))
                                                {
                                                  out.println(Functions.NewShowConfirmation1("Sorry", msg));
                                                }
                                             %>
                                        <div class="form-group col-md-1">
                                        </div>
                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Partner ID</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="pid" id="pid" class="form-control"
                                                           value="<%=pid%>" <%=Config%>>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Merchant ID*</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input type="text" name="memberid" id="member" class="form-control"
                                                           value="<%=memberid%>">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Account ID*</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input name="accountid" id="account_id" value="<%=accountid%>"
                                                           class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>First Six*</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="firstsix" name="firstsix" maxlength="6" size="6"
                                                           value="<%=firstSix%>" class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Last Four*</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="lastfour" name="lastfour" maxlength="4" size="4"
                                                           value="<%=lastFour%>" class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Email Address</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="emailaddr" name="emailAddr" type="text"
                                                           value="<%=emailAddr%>" class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Card Holder Name</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="name" name="name" type="text"
                                                           value="<%=name1%>" class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>IP Address</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <input id="ipAddress" name="ipAddress" type="text"
                                                           value="<%=ipAddress%>" class="form-control">
                                                </div>
                                            </div>
                                        </div>

                                       <%-- <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>Expiry Date</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <div class="CardExpiry"><input type="text" id="Expiry"  class="form-control" style="width:100%"  placeholder="MM/YY" name="expiryDate" onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry','CardExpiry')" autocomplete="off" onkeyup="addSlash(event,'Expiry')" value=<%=expiryDate%>></div>
                                                </div>
                                            </div>
                                        </div>--%>
                                        <div class="form-group col-md-12">
                                            <div class="ui-widget">
                                                <div class="col-sm-offset-2 col-md-3" style="margin-top:10px;">
                                                    <label>InActive</label>
                                                </div>
                                                <div class="col-md-3">
                                                    <select name="isTemp" id="isTemp" class="form-control" onchange="onChange()">
                                                        <option value="N" <%if("N".equalsIgnoreCase(istemp)){%>selected<%}%>>N</option>
                                                        <option value="Y" <%if("Y".equalsIgnoreCase(istemp)){%>selected<%}%>>Y</option>
                                                    </select>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12">
                                            <p style="text-align: center;">
                                                <button id="upload" type="submit" name="upload" value="upload"
                                                        style="display: inline-block!important;"
                                                        class="btn btn-default center-block"><i
                                                        class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;Whitelist Card
                                                </button>
                                                <button type="submit" class="btn btn-default center-block"
                                                        style="display: inline-block!important;"><i
                                                        class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;Search
                                                </button>
                                            </p>
                                        </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Whitelist Card List</strong></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
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

                            int records=0;
                            int totalrecords=0;
                            String currentblock=request.getParameter("currentblock");
                            if(currentblock==null)
                                currentblock="1";
                            try
                            {
                                records= (int) request.getAttribute("records");
                                totalrecords=(int)request.getAttribute("totalrecords");
                            }
                            catch(Exception ex)
                            {
                            }
                            if (request.getAttribute("listofcard") != null)
                            {
                                List<WhitelistingDetailsVO> cList = (List<WhitelistingDetailsVO>) request.getAttribute("listofcard");
                                if (cList.size() > 0 && records > 0)
                                {
                        %>
                        <br>
                        <form action="/partner/net/WhitelistDelete?ctoken=<%=ctoken%>" method="POST" name="BinDelete">
                        <table id="myTable" border="2" class="display table table-striped table-bordered"  width="100%"
                               style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <thead>
                            <tr style="background-color: #7eccad; color: white;">
                                <th valign="middle" align="center" style="text-align: center"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></th>
                                <th valign="middle" align="center" style="text-align: center">Member ID</th>
                                <th valign="middle" align="center" style="text-align: center">AccountID</th>
                                <th valign="middle" align="center" style="text-align: center">First Six</th>
                                <th valign="middle" align="center" style="text-align: center">Last Four</th>
                                <th valign="middle" align="center" style="text-align: center">Email Address</th>
                                <th valign="middle" align="center" style="text-align: center">Name</th>
                                <td></td>
                                <th valign="middle" align="center" style="text-align: center">IP Address</th>
                               <%-- <th valign="middle" align="center" style="text-align: center">Expiry Date</th>--%>
                                <th valign="middle" align="center" style="text-align: center">InActive</th>
                                <th valign="middle" align="center" style="text-align: center">Action Executor Id</th>
                                <th valign="middle" align="center" style="text-align: center">Action Executor Name</th>

                            </tr>
                            </thead>
                            <tbody>
                                <%
                  String style="class=td1";
                  String ext="light";
                  int pos = 1;
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
                  pos = (pagerecords * (pageno-1))+1;
                  String email="";
                    String name="";
                    String maskingName="";
                    String expiryDate2="";
                    String ipAddress2="";
                    String isTemp="";
                    	String role=(String)session.getAttribute("role");
			String username=(String)session.getAttribute("username");
			String actionExecutorId=(String)session.getAttribute("merchantid");
			String actionExecutorName=role+"-"+username;

                  for(WhitelistingDetailsVO whitelistingDetailsVO : cList)
                  {

                    if(functions.isValueNull(whitelistingDetailsVO.getEmail()))
                    email=whitelistingDetailsVO.getEmail();

                    if(functions.isValueNull(whitelistingDetailsVO.getName()))
                    {
                        name=whitelistingDetailsVO.getName();
                        maskingName=functions.getNameMasking(name);
                    }
                     /*if(functions.isValueNull(whitelistingDetailsVO.getExpiryDate()))
                    {
                        expiryDate2=whitelistingDetailsVO.getExpiryDate();
                    }*/
                    if(functions.isValueNull(whitelistingDetailsVO.getIpAddress()))
                    {
                        ipAddress2=whitelistingDetailsVO.getIpAddress();
                    }
                    isTemp=whitelistingDetailsVO.getIsTemp();

                           if(functions.isValueNull(whitelistingDetailsVO.getActionExecutorId()))
                    {
                      actionExecutorId = whitelistingDetailsVO.getActionExecutorId();
                    }
                    else
                    {
                      actionExecutorId="-";

                    }
                    if(functions.isValueNull(whitelistingDetailsVO.getActionExecutorName()))
                    {
                      actionExecutorName = whitelistingDetailsVO.getActionExecutorName();
                    }
                    else
                    {
                      actionExecutorName="-";

                    }


                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"id\" align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+whitelistingDetailsVO.getId()+"></td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getMemberid()+"<input type=\"hidden\" name=\"memberid_"+whitelistingDetailsVO.getId()+"\" value="+whitelistingDetailsVO.getMemberid()+"></td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getAccountid()+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getFirstsix()+"</td>");
                    out.println("<td align=center "+style+">"+whitelistingDetailsVO.getLastfour()+"</td>");
                    out.println("<td align=center "+style+">"+email+"</td>");
                    out.println("<td align=center "+style+">");
                    if(functions.isValueNull(whitelistingDetailsVO.getName()) || !"".equals(name))
                    out.println("<label id=\"name"+pos+"\">"+maskingName+"</label>");

                    out.println("</td>");
                    out.println("<td align=center "+style+">");
                    if(functions.isValueNull(name) || !"".equals(name))
                    out.println("<span style=\"z-index: 2;font-size:13px;margin-top:10px;\" toggle=\"#password-field\" class=\"far fa-eye-slash toggle-password eye-\" onmousedown=\"unMasking('showHidepass1"+pos+"','name"+pos+"','"+name+"')\" onmouseup=\"masking('showHidepass1"+pos+"','name"+pos+"','"+maskingName+"')\" id=\"showHidepass1"+pos+"\" >");
                    out.println("</td>");
                    out.println("<td align=center "+style+">"+ipAddress2+"</td>");
                   /* out.println("<td align=center "+style+">");*/
                    /*out.println("<input id=\"name"+pos+"\" class=\"form-control\" type=\"text\" size=\"10\" value=\""+expiryDate2+"\" disabled>");*/
                    /*out.println("</td>");*/
                    out.println("<td align=center " + style + ">&nbsp;<select class=\"textbox\" name=\"isTemp_"+whitelistingDetailsVO.getId()+"\">" + Functions.comboval(isTemp)+"</select></td>");
                     out.println("<td align=center "+style+">"+actionExecutorId+"</td>");
                      out.println("<td align=center "+style+">"+actionExecutorName+"</td>");
                     out.println("</tr>");
                    pos++;
                  }
                  %>

                    </div>

                </div>
                </table>
                <table width="100%">
                    <thead>
                    <tr>
                        <td width="15%" align="center">
                            <input type="hidden" name="delete" value="delete"><input type="button" name="delete" class="btn btn-default gotoauto" value="Update" onclick="return Delete();">
                        </td>
                    </tr>
                    </thead>
                </table>
                </form>
                <%
                    int TotalPageNo;
                    if(totalrecords%pagerecords!=0)
                    {
                        TotalPageNo=totalrecords/pagerecords+1;
                    }
                    else
                    {
                        TotalPageNo=totalrecords/pagerecords;
                    }
                %>
                <div id="showingid"><strong>Page number  <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                <div id="showingid"><strong>Total number of records  <%=totalrecords%> </strong></div>
                <jsp:include page="page.jsp" flush="true">
                    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                    <jsp:param name="numrows" value="<%=pagerecords%>"/>
                    <jsp:param name="pageno" value="<%=pageno%>"/>
                    <jsp:param name="str" value="<%=str%>"/>
                    <jsp:param name="page" value="WhiteListCard"/>
                    <jsp:param name="currentblock" value="<%=currentblock%>"/>
                    <jsp:param name="orderby" value=""/>
                </jsp:include>
                <%
                        }
                        else
                        {
                            out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                        }
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
                    }
                %>
            </div>
        </div>
    </div>
</div>
</div>
</div>
</body>
</html>