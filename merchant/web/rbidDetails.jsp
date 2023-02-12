<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.manager.dao.RecurringDAO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="/Top.jsp" %>

<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 4/10/15
  Time: 5:36 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <%--<script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>
</head>
<style type="text/css">

    <%--   #main{background-color: #ffffff}

       :target:before {
           content: "";
           display: block;
           height: 50px;
           margin: -50px 0 0;
       }

       .table > thead > tr > th {font-weight: inherit;}

       :target:before {
           content: "";
           display: block;
           height: 90px;
           margin: -50px 0 0;
       }

       footer{border-top:none;margin-top: 0;padding: 0;}

       /********************Table Responsive Start**************************/

       @media (max-width: 640px){

           table {border: 0;}

           /*table tr {
               padding-top: 20px;
               padding-bottom: 20px;
               display: block;
           }*/

           table thead { display: none;}

           tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

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

       tr:nth-child(odd) {background: #F9F9F9;}

       tr {
           display: table-row;
           vertical-align: inherit;
           border-color: inherit;
       }

       th {padding-right: 1em;text-align: left;font-weight: bold;}

       td, th {display: table-cell;vertical-align: inherit;}

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

       .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

       /********************Table Responsive Ends**************************/
   --%>

    #myTable th{
        text-align: center;
    }

</style>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String rbidDetails_GoBack = StringUtils.isNotEmpty(rb1.getString("rbidDetails_GoBack"))?rb1.getString("rbidDetails_GoBack"): "Go Back";
    String rbidDetails_recurring = StringUtils.isNotEmpty(rb1.getString("rbidDetails_recurring"))?rb1.getString("rbidDetails_recurring"): "Recurring Subscription Details";
    String rbidDetails_Interval = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Interval"))?rb1.getString("rbidDetails_Interval"): "Interval";
    String rbidDetails_Frequency = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Frequency"))?rb1.getString("rbidDetails_Frequency"): "Frequency";
    String rbidDetails_Run = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Run"))?rb1.getString("rbidDetails_Run"): "Run Date";
    String rbidDetails_Subscribe = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Subscribe"))?rb1.getString("rbidDetails_Subscribe"): "Subscribe Amount";
    String rbidDetails_Card = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Card"))?rb1.getString("rbidDetails_Card"): "Card Number";
    String rbidDetails_holder = StringUtils.isNotEmpty(rb1.getString("rbidDetails_holder"))?rb1.getString("rbidDetails_holder"): "Card Holder";
    String rbidDetails_status = StringUtils.isNotEmpty(rb1.getString("rbidDetails_status"))?rb1.getString("rbidDetails_status"): "Recurring Status";
    String rbidDetails_sr = StringUtils.isNotEmpty(rb1.getString("rbidDetails_sr"))?rb1.getString("rbidDetails_sr"): "Sr.No";
    String rbidDetails_TrackingId = StringUtils.isNotEmpty(rb1.getString("rbidDetails_TrackingId"))?rb1.getString("rbidDetails_TrackingId"): "TrackingId";
    String rbidDetails_Amount1 = StringUtils.isNotEmpty(rb1.getString("rbidDetails_Amount1"))?rb1.getString("rbidDetails_Amount1"): "Amount";
    String rbidDetails_date = StringUtils.isNotEmpty(rb1.getString("rbidDetails_date"))?rb1.getString("rbidDetails_date"): "Transaction Date(yyyy/MM/dd)";
    String rbidDetails_status1 = StringUtils.isNotEmpty(rb1.getString("rbidDetails_status1"))?rb1.getString("rbidDetails_status1"): "Transaction Status";
%>
<title> <%=company%> | Recurring Billing Module</title>

<body class="pace-done widescreen fixed-left-void">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post">

                        <%
                            Enumeration<String> stringEnumeration=request.getParameterNames();
                            while(stringEnumeration.hasMoreElements())
                            {
                                String name=stringEnumeration.nextElement();

                                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                            }
                        %>
                        <%--<button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/merchant/images/goBack.png">
                        </button>--%>
                        <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=rbidDetails_GoBack%></button>
                    </form>
                </div>
            </div>

            <%--<div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Recurring Module</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                &lt;%&ndash;<div class="rowcontainer-fluid " >
                                    <div class="row rowadd" >
                                        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
                                            <div class="form foreground bodypanelfont_color panelbody_color">
                                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp; Recurring Module</h2>
                                                <hr class="hrform">
                                            </div>&ndash;%&gt;

                                <form action="/merchant/servlet/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post" >
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                    <%
                                        String uId = "";
                                        if(session.getAttribute("role").equals("submerchant"))
                                        {
                                            uId = (String) session.getAttribute("userid");
                                        }
                                        else
                                        {
                                            uId = (String) session.getAttribute("merchantid");
                                        }
                                    %>


                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Recurring Billing Id</label>
                                        <input name="rbid" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label >First Six</label>
                                        <input name="firstsix" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Last Four</label>
                                        <input name="lastfour" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Tracking ID</label>
                                        <input name="trackingid" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label >Card Holder Name</label>
                                        <input name="name" size="10" class="form-control">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Terminal ID</label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <option value="all">All</option>
                                            <%
                                                StringBuffer terminalBuffer = new StringBuffer();
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager=new TerminalManager();
                                                List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                for (TerminalVO terminalVO:terminalVOList)
                                                {
                                                    String str1 = "";
                                                    //String terminalid = (String)request.getAttribute("terminalid");
                                                    if((terminalVO.getTerminalId()).equals((String)request.getAttribute("terminalid")))
                                                    {
                                                        str1= "selected";
                                                    }
                                                    if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                    {
                                                        terminalBuffer.append(",");
                                                    }
                                                    terminalBuffer.append(terminalVO.getTerminalId());
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType())%> </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-9 has-feedback">&nbsp;</div>
                                    <div class="form-group col-md-3">
                                        <button type="submit" class="btnblue" style="margin-left:100px;padding-right: 50px;background: rgb(126, 204, 173);">
                                            <i class="fa fa-save"></i>
                                            &nbsp;&nbsp;Search
                                        </button>

                                    </div>
                                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                </form>
                            </div>
                        </div>
                    </div>--%>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=rbidDetails_recurring%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>

                                <tr style="color: white;">
                                    <th width="12%" align="center" >Recurring Billing ID</th>
                                    <th width="12%" align="center" ><%=rbidDetails_Interval%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_Frequency%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_Run%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_Subscribe%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_Card%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_holder%></th>
                                    <th width="12%" align="center" ><%=rbidDetails_status%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    RecurringBillingVO recurringBillingVO = null;

                                    recurringBillingVO = (RecurringBillingVO) request.getAttribute("recurringsub");
                                %>
                                <%
                                    out.println("<tr>");
                                    /*out.println("<td align=center class=\"textb\" data-label=\"RBID\">&nbsp;"+recurringBillingVO.getRbid()+"</td>");*/
                                    if (recurringBillingVO.getRecurring_subscrition_id()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"rbid\">&nbsp;" + recurringBillingVO.getRecurring_subscrition_id() + "</td>");
                                    }
                                    else if (recurringBillingVO.getRecurring_subscrition_id()== null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"rbid\">&nbsp;"+"-"+"</td>");
                                    }
                                    if (recurringBillingVO.getInterval()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;" + recurringBillingVO.getInterval() + "</td>");
                                    }
                                    else if (recurringBillingVO.getInterval()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;"+"-"+"</td>");
                                    }

                                    if (recurringBillingVO.getFrequency()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Frequency\">&nbsp;" + recurringBillingVO.getFrequency() + "</td>");
                                    }
                                    else if (recurringBillingVO.getFrequency()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Frequency\">&nbsp;"+"-"+"</td>");
                                    }

                                    if (recurringBillingVO.getRunDate()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Run Date\">&nbsp;" + recurringBillingVO.getRunDate() + "</td>");
                                    }
                                    else if (recurringBillingVO.getRunDate()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Run Date\">&nbsp;"+"-"+"</td>");
                                    }
                                    if(recurringBillingVO.getAmount()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Subscribe Amount\">&nbsp;"+recurringBillingVO.getAmount()+"</td>");
                                    }
                                    else if (recurringBillingVO.getAmount()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Subscribe Amount\">&nbsp;"+"-"+"</td>");
                                    }
                                    if(recurringBillingVO.getFirstSix()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Card Number\">&nbsp;"+recurringBillingVO.getFirstSix()+"****"+recurringBillingVO.getLastFour()+"</td>");
                                    }
                                    else if (recurringBillingVO.getFirstSix()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Card Number\">&nbsp;"+"-"+"</td>");
                                    }
                                    if(recurringBillingVO.getCardHolderName()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Card Holder\">&nbsp;"+recurringBillingVO.getCardHolderName()+"</td>");;
                                    }
                                    else if (recurringBillingVO.getCardHolderName()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Card Holder\">&nbsp;"+"-"+"</td>");
                                    }

                                    if(recurringBillingVO.getRecurringStatus()!=null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Recurring Status\">&nbsp;"+recurringBillingVO.getRecurringStatus()+"</td>");
                                    }
                                    else if (recurringBillingVO.getRecurringStatus()==null)
                                    {
                                        out.println("<td align=center class=\"textb\" data-label=\"Recurring Status\">&nbsp;"+"-"+"</td>");
                                    }
                                    out.println("</tr>");
                                %>

                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>


            <%-- Recurring Transaction Details --%>

            <%
                if(request.getAttribute("transactionList")!=null)
                {
                    List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("transactionList");
                    if(rbList.size()>0)
                    {
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Recurring Transaction Details</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>

                                <tr style="color: white;">
                                    <th  align="center"><%=rbidDetails_sr%></th>
                                    <th  align="center"><%=rbidDetails_TrackingId%></th>
                                    <th  align="center"><%=rbidDetails_Amount1%></th>
                                    <th  align="center"><%=rbidDetails_date%></th>
                                    <th  align="center"><%=rbidDetails_status1%></th>

                                </tr>
                                </thead>
                                <tbody>
                                <%
                                            String style="class=td1";
                                            String ext="light";
                                            int pos = 1;
                                            for(RecurringBillingVO recVO : rbList)
                                            {
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

                                                out.println("<tr>");
                                                out.println("<td align=center data-label=\"Sr.No\">" +pos+"</td>");
                                                out.println("<td align=center data-label=\"TrackingId\">" +recVO.getNewPzTransactionID()+"</td>");
                                                out.println("<td align=center data-label=\"Amount\">" +recVO.getAmount()+"</td>");

                                                if ("null".equals(recVO.getRecurringRunDate()))
                                                {
                                                    out.println("<td align=center data-label=\"Transaction Date(yyyy/MM/dd)\">" +"-"+"</td>");

                                                }
                                                else if (!"null".equals(recVO.getRecurringRunDate()))
                                                {
                                                    out.println("<td align=center data-label=\"Transaction Date(yyyy/MM/dd)\">" +recVO.getRecurringRunDate()+"</td>");
                                                }

                                                out.println("<td align=center data-label=\"Transaction Status\">" +recVO.getTransactionStatus()+"</td>");
                                                out.println("</tr>");
                                                pos++;
                                            }
                                        }
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
</div>
</body>
</html>