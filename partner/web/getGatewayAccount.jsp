<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: admin1
  Date: 2/9/13
  Time: 9:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Gateway Account Information</title>

    <link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link href="/partner/NewCss/libs/bootstrap/css/bootstrap.min.css" rel="stylesheet" />

    <style type="text/css">
        .table > thead > tr > th {font-weight: inherit;}

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

            table tr:nth-child(odd) {background: #cacaca!important;}

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

        #maintitle{
            text-align: center;
            background: #7eccad;
            color: #fff;
            font-size: 14px;
            line-height: 35px!important;
        }

        hr{
            margin-top: 30px;
            margin-bottom: 30px;
        }

    </style>

</head>
<body>
       <%
           Hashtable hash=(Hashtable) request.getAttribute("hiddenvariables");
           if(hash==null){


       %>
         <%--<h1> Gateway Account Information</h1>--%>
       <div class="widget">
           <div class="widget-header transparent">
               <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Gateway Account Information</strong></h2>
           </div>
       </div>
         <center> <font color=red size=2> Sorry , You have not Selected any gateway</font></center>
<%
    }else
    {
%>

      <%-- <h1 align=center> Gateway Account Information</h1>--%>
       <div class="widget">
           <div class="widget-header transparent" style="height: inherit;">
               <h2 style="text-align: center; margin-top: 15px;"><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Gateway Account Information</strong></h2>
           </div>
       </div>

       <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

           <tr>
               <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;">Account ID</td>
               <td class="tr1" align="center"><%=hash.get("accountid")%></td>
           </tr>
           <tr>
               <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;">PG TYPE ID</td>
               <td class="tr1" align="center"><%=hash.get("pgtypeid")%></td>
           </tr>
           <tr>
               <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;">Alias Name</td>
               <td class="tr1" align="center"><%=hash.get("aliasname")%></td>
           </tr>
           <tr>
               <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;">Display Name</td>
               <td class="tr1" align="center"><%=hash.get("displayname")%></td>
           </tr>
           <tr>
               <td class="tr0" align="center" style="background-color: #7eccad !important;color: white;border-bottom: 1px solid #ddd;">MID</td>
               <td class="tr1" align="center"><%=hash.get("merchantid")%></td>
           </tr>
       </table>

       <hr/>

           <div class="widget">
               <div class="widget-header transparent">
                   <h2 id="maintitle"><strong>&nbsp;&nbsp;Limits</strong></h2>
               </div>
           </div>

           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Daily Amount Limit</label>
               <input type="text" class="form-control" size=10 name='daily_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("dailyamountlimit")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Monthly Amount Limit</label>
               <input type="text" class="form-control" size=10 name='monthly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("monthlyamountlimit")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Daily Card Limit</label>
               <input type="text" class="form-control" size=10 name='daily_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("dailycardlimit")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Weekly Card Limit</label>
               <input type="text" class="form-control" size=10 name='weekly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("weeklycardlimit")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Monthly Card Limit</label>
               <input type="text" class="form-control" size=10 name='monthly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("monthlycardlimit")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Min Transaction Amount</label>
               <input type="text" class="form-control" size=10 name='min_trans_amount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("mintxnamount")%>" disabled/>
           </div>
           <div class="form-group col-md-2 has-feedback">
               <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Max Transaction Amount</label>
               <input type="text" class="form-control" size=10 name='max_trans_amount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=hash.get("maxtxnamount")%>" disabled/>
           </div>




       <%
    }
%>
</body>
</html>