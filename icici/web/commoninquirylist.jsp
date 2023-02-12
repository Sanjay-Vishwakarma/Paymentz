<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 4/4/13
  Time: 12:21 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Common Integration> Common Inquiry</title>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
        function DoReverse()
        {
            var checkboxes = document.getElementsByName("trackingid");
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
                alert("Select at least one transaction");
                return false;
            }

            if (confirm("Do you really want to inquire all selected transaction."))
            {
                document.reversalform.submit();
            }
        }
    </script>
</head>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str="";
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
        try
        {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {

        }
        Calendar rightNow = Calendar.getInstance();
        String currentyear= "" + rightNow.get(rightNow.YEAR);
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
        str = str + "ctoken=" + ctoken;
        if (fdate != null) str = str + "&fdate=" + fdate;
        if (tdate != null) str = str + "&tdate=" + tdate;
        if (fmonth != null) str = str + "&fmonth=" + fmonth;
        if (tmonth != null) str = str + "&tmonth=" + tmonth;
        if (fyear != null) str = str + "&fyear=" + fyear;
        if (tyear != null) str = str + "&tyear=" + tyear;


        String reqaccountid=request.getParameter("accountid");
        String toid=request.getParameter("toid");
        String description = Functions.checkStringNull(request.getParameter("orderid")) == null ? "" : request.getParameter("orderid");
        String trackingid = Functions.checkStringNull(request.getParameter("trackingid")) == null ? "" : request.getParameter("trackingid");
        String paymentid = Functions.checkStringNull(request.getParameter("paymentid")) == null ? "" : request.getParameter("paymentid");


        /*if(trackingid!=null)
        {
            str = str + "&trackingid="+trackingid;
        }
        if(paymentid!=null)
        {
            str = str + "&paymentid="+paymentid;
        }
        if(reqaccountid!=null)
        {
            str = str + "&accountid="+reqaccountid;
        }
        if(toid!=null)
        {
            str = str + "&toid="+toid;
        }if(description!=null)
        {
            str = str + "&description="+description;
        }*/
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
%>
<body>

<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String pgtypeid = "";
        String currency= "";
%>

<form action="/icici/servlet/CommonInquiryList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Common Inquiry
                </div>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="fdate" class="textb">
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="textb">
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="textb">
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="tdate" class="textb">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="tmonth" class="textb">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="tyear" class="textb" style="width:54px;">
                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2013));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Payment/Id</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input type=text name="paymentid" maxlength="100" class="txtbox" value="<%=paymentid%>">
                                    </td>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Tracking ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="500" type="text" name="trackingid"  class="txtbox" value="<%=trackingid%>">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Order ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input maxlength="500" type="text" name="orderid"  class="txtbox" value="<%=description%>">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="Submit">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>


                                </tr>
                                <tr><td>&nbsp;</td></tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</form>
<div class="reporttable">
    <%
        String errormsg1 = (String)request.getAttribute("cbmessage");
        Functions functions = new Functions();
        if (errormsg1 == null)
        {
            errormsg1 = "";
        }
        else
        {  // out.println("<BR>");
            out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

            out.println(errormsg1);

            out.println("</font></td></tr></table>");
           // out.println("<BR>");
        }

        String error= (String)request.getAttribute("errormessage");
        if (error!= null)
        {
            out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
        }
    %>

    <%
        //Hashtable hash = (Hashtable)request.getAttribute("transdetails");

       // Hashtable temphash=null;
        str = str + "&pgtypeid="+pgtypeid;
        str = str + "&currency="+currency;

        //int records=0;
        //int totalrecords=0;
        error=(String ) request.getAttribute("error");
        if(error !=null)
        {
            out.println("<center><font class=\"text\" face=\"arial\">"+error+"</font></center>");
        }


        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        /*try
        {
            records=Integer.parseInt((String)hash.get("records"));
            totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
        }
        catch(Exception ex)
        {

        }*/
        /*if(hash!=null)
        {
            hash = (Hashtable)request.getAttribute("transdetails");
        }
        if(records>0)
        {*/
        if(request.getAttribute("transdetails")!=null)
        {

            List<TransactionVO> inquiryVOList= (List<TransactionVO>) request.getAttribute("transdetails");
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
            if(inquiryVOList.size()>0)
            {
    %>

    <form name="reversalform" action="CommonInquiry?ctoken=<%=ctoken%>" method="post">
    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <%--<td width="5%" valign="middle" align="center" class="th0">Sr No</td>--%>
            <td valign="middle" align="center" class="th0" width="5%"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
            <td valign="middle" align="center" class="th0">Tracking Id</td>
            <td valign="middle" align="center" class="th0">Account Id</td>
            <td valign="middle" align="center" class="th0">Member Id</td>
            <td valign="middle" align="center" class="th0">Description</td>
            <td valign="middle" align="center" class="th0">Amount</td>
            <td valign="middle" align="center" class="th0">Status</td>
            <td valign="middle" align="center" class="th0">Time</td>
            <td valign="middle" align="center" class="th0">Payment Id</td>
            <td valign="middle" align="center" class="th0">Remark</td>
           <%-- <td valign="middle" align="center" class="th0">Inquiry</td>--%>
        </tr>
        </thead>
        <%
            String style="class=tr1";
            String ext="light";
            int pos=0;

            /*for(int pos=1;pos<=records;pos++)
            {*/
                String id=Integer.toString(pos);
                //int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
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
                /*temphash=(Hashtable)hash.get(id);
                out.println("<tr>");
                out.println("<form  action=\"/icici/servlet/CommonInquiry\" method=\"POST\">");
                //out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=\"center\" " + style + ">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""  +ESAPI.encoder().encodeForHTML((String)temphash.get("trackingid"))+ "\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                if (functions.isValueNull((String) temphash.get("accountid")))
                {
                    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("accountid")) + "<input type=\"hidden\" name=\"accountid\" value=\"" + temphash.get("accountid") + "\"></td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                }
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid\" value=\""+temphash.get("toid")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");

                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"<input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"))+"</td>");
                if (functions.isValueNull((String) temphash.get("paymentid")))
                {
                    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("paymentid")) + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                }

                if (functions.isValueNull((String)temphash.get("remark")))
                {
                    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("remark")) + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                }
                out.println("<input type=\"hidden\" name=\"pgtypeid\" value=\""+pgtypeid+"\">");
                out.println("<input type=\"hidden\" name=\"fmonth\" value=\""+fmonth+"\">");
                out.println("<input type=\"hidden\" name=\"fdate\" value=\""+fdate+"\">");
                out.println("<input type=\"hidden\" name=\"fyear\" value=\""+fyear+"\">");
                out.println("<input type=\"hidden\" name=\"tmonth\" value=\""+tmonth+"\">");
                out.println("<input type=\"hidden\" name=\"tdate\" value=\""+tdate+"\">");
                out.println("<input type=\"hidden\" name=\"tyear\" value=\""+tyear+"\">");
                //out.println("<td align=center "+style+ "><input type=submit class=\"gotoauto\" value=Inquiry style=\"margin: -1px;\"> </font></a></font></td>");
                out.println("</form>");
                out.println("</tr>");*/

                for(TransactionVO transactionVO : inquiryVOList)
                {
                    out.println("<tr>");
                    out.println("<td align=\"center\" " + style + ">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\""+transactionVO.getTrackingId()+"\"></td>");
                    out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId()) + "</td>");
                    if (functions.isValueNull(transactionVO.getAccountId()))
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getAccountId()) + "<input type=\"hidden\" name=\"accountid_"+transactionVO.getTrackingId()+"\" value=\""+ transactionVO.getAccountId()+"\"></td>");
                    }
                    else
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                    }
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getMemberId())+"<input type=\"hidden\" name=\"toid_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getMemberId()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getOrderDesc())+"<input type=\"hidden\" name=\"desc_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getOrderDesc()+"\"></td>");

                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getAmount())+ "<input type =\"hidden\" name=\"amount_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getAmount()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getStatus())+"</td>"+"<input type=\"hidden\" name=\"status_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getStatus()+"\"></td>");
                    out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp())+"</td>"+"<input type=\"hidden\" name=\"timestamp_"+transactionVO.getTrackingId()+"\" value=\""+transactionVO.getTimestamp()+"\"></td>");
                    if (functions.isValueNull(transactionVO.getPaymentId()))
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getPaymentId()) + "</td>");
                    }
                    else
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                    }

                    if (functions.isValueNull(transactionVO.getRemark()))
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getRemark()) + "</td>");
                    }
                    else
                    {
                        out.println("<td align=center " + style + ">&nbsp;" + "-" + "</td>");
                    }
            }
        %>

        <thead>
        <tr>
            <td class="th0" colspan="14">
                <center><button type="submit" class="addnewmember" value="Inquiry" onclick="return DoReverse()">Inquiry</button></center>
            </td>
        </tr>
        </thead>

    </table>

    </form>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
                <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
                <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
                <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
                <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
                <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No Data Found"));
            }
        }
            else
            {
                Functions functions1=new Functions();
                if (functions.isValueNull(request.getParameter("trackingid"))){

                }else{
                    out.println(Functions.NewShowConfirmation("Sorry","No records found."));
                }
            }
      /*  else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No Data Found"));

            *//*if (functions.isValueNull(request.getParameter("trackingid"))){

            }else{
                out.println(Functions.NewShowConfirmation("Sorry","Invalid TrackingId"));
            }*//*
        }
*/
    %>

</div>
<%
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
    }
%>
</body>
</html>