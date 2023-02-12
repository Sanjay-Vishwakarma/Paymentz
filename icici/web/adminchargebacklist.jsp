<%@ page import="java.util.Hashtable,
                 com.directi.pg.Functions,
                 java.io.FileInputStream,
                 java.io.InputStreamReader,
                 java.io.BufferedReader,java.net.URLEncoder,
                 org.owasp.esapi.ESAPI,
                 com.directi.pg.Merchants,
                 com.directi.pg.Admin"%>
<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
 <%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: May 26, 2012
  Time: 12:58:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
  <head>
  <script type="text/javascript">

        function goBack()
          {
            document.location.href = "/icici/admin/index.jsp";
          }
          function ToggleAll(checkbox,name)
           {
             flag = checkbox.checked;
             var checkboxes = document.getElementsByName(name);
             var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
          }
        function DoChargeback()
        {
            //if (isNaN(document.reversalform.numrows.value))
                //alert("please enter valid page number");

            var checkboxes = document.getElementsByName("chargeback");
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

            if (confirm("Do you really want to Chargeback all selected transaction."))
            {
                document.chargeBackForm.submit();
            }
        }
        function DoReversal()
        {
            //if (isNaN(document.reversalform.numrows.value))
                //alert("please enter valid page number");

            var checkboxes = document.getElementsByName("ch_reversal");
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

            if (confirm("Do you really want to Reverse all selected chargeback transaction."))
            {
                document.Reversal.submit();
            }
        }
        function DoSync()
        {
            //if (isNaN(document.reversalform.numrows.value))
                //alert("please enter valid page number");

            var checkboxes = document.getElementsByName("sync");
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

            if (confirm("Do you really want to Process all not in Sync transaction."))
            {
                document.notinsync.submit();
            }
        }
</script>
    </head>
  <body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
    %>
<%

        String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg == null)
        errormsg = "";

        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" ></font>");

        out.println(errormsg);

        out.println("</td></tr></table>");


  %>

<%--  <form action="/icici/servlet/AdminChargebackList" method="post" name="F1" >
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
        <tr colspan="3" width="692">
        <td colspan="3" width="692" > <input TYPE=RADIO NAME="ChargeBack" align="right"  VALUE="ChargeBack" >ChargeBack
              <input type="RADIO" NAME="ChargeBack" align="center" VALUE="ChargeBack Reversed" class=search>ChargeBack Reversed
              <input type="RADIO" NAME="ChargeBack" align="left" VALUE="Auto ChargeBack" class=search>Auto ChargeBack
        </td>


        </tr>
		<tr>
			<td class=search width="20%" align="right">Tracking Id</td>
			<td><input name="STrakingid" size="10" value="<%=request.getParameter("STrakingid")==null?"":request.getParameter("STrakingid")%>"></td>

		</tr>
		<tr>
			<td class=search width="20%" align="right">Description</td>
			<td><input name="SDescription" size="10" value="<%=request.getParameter("SDescription")==null?"":request.getParameter("SDescription")%>"></td>

		</tr>

		<tr>
			<td class=search colspan=2 align=center>
			<input type="submit" value="Search"></td>

		</tr>
	</table>
</form>--%>
<%  //ChargeBack table Start

	Hashtable hash=(Hashtable)request.getAttribute("chargebackdetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();
    String str = "";
	int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "SRecords=" + pagerecords;
     str = str + "&ctoken="+ctoken;
	int records = 0;
    int totalrecords = 0;
    int currentblock = 1;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {

    }
     
%>
<form name="chargeBackForm" action="DoBulkChargebackTransaction?ctoken=<%=ctoken%>" method="post">
<table align=center border="1" >
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<tr ><td valign="middle" colspan="8"  align="center"   bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" > ChargeBack</font></td></tr>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this,'chargeback');" name="alltrans"></td>
    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Memberid</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>--%>
	<td valign="middle" align="center" bgcolor="#008BBA">SBM MerchantId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">ChargeBack Amount</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Current Status</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Proccessing Remark </td>--%>
</tr>
<%
	String style="class=td1";
	String ext="light";
    String hiddenField = "";

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);
        hiddenField = "";

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
			out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"chargeback\" value=\""+temphash.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash.get("icicitransid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"</td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash.get("company_name")+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("merchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
            out.println("</tr>");
            hiddenField = "hid_" + temphash.get("icicitransid");
            out.println("<input type=\"hidden\" name=\""+hiddenField+"\"  value=\""+temphash.get("fk_chargeback_process_id")+"\">");

	}
%>
<tr><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><input class="button" type="button" value="Proccess Selected ChargeBack "  onClick="return DoChargeback();" ></td></tr>
<tr><td colspan=3 align="left" class="textb">Total Records : <%=totalrecords%></td>
</table>
<br>
</form>
<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true">
    <jsp:param name="numrecords" value="<%=totalrecords%>"/>
    <jsp:param name="numrows" value="<%=pagerecords%>"/>
    <jsp:param name="pageno" value="<%=pageno%>"/>
    <jsp:param name="str" value="<%=URLEncoder.encode(str)%>"/>
    <jsp:param name="page" value="AdminChargebackList"/>
    <jsp:param name="currentblock" value="<%=currentblock%>"/>
    <jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</tr>
</table>

<%  //ChargeBack Table End %>


<%  //ChargeBack Reversed table Start%>

<%  //ChargeBack table Start

	Hashtable hash1=(Hashtable)request.getAttribute("chargebackreversal");
	Hashtable temphash1=null;
    int records1 = 0;
    int totalrecords1 = 0;
    records1 = Integer.parseInt((String) hash1.get("records1"));
    totalrecords1 = Integer.parseInt((String) hash1.get("totalrecords"));   

    %>
<form name="Reversal" action="DoBulkReversalChargebackTransaction?ctoken=<%=ctoken%>" method="post">
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1">
<tr ><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" > ChargeBack Reversal</font></td></tr>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this,'ch_reversal');" name="alltrans1"></td>
    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Memberid</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>--%>
	<td valign="middle" align="center" bgcolor="#008BBA">SBM MerchantId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Chargeback Amount</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Current Status</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Proccessing Remark</td>--%>
</tr>
<%


	for(int pos=1;pos<=records1;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash1=(Hashtable)hash1.get(id);
            out.println("<tr>");
			out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"ch_reversal\" value=\""+temphash1.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash1.get("icicitransid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash1.get("toid"))+"</td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash1.get("company_name")+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash1.get("merchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash1.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash1.get("status"))+"</td>");
            out.println("</tr>");
            String hidd = "hid_" + temphash1.get("icicitransid");
            out.println("<input type=\"hidden\" name=\""+hidd+"\"  value=\""+temphash1.get("fk_chargeback_process_id")+"\">");
    }

%>
<tr><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><input class="button" type="button" value="Proccess Selected ChargeBack Reversal"  onClick="return DoReversal();" ></td></tr>
<tr><td colspan=3 align="left" class="textb">Total Records : <%=totalrecords1%></td>
</table>

<br>
<center>
</center>
</form>

<%  //ChargeBack Reversed Table End %>




<%  //Auto ChargeBack Reversal table Start
	Hashtable hash2=(Hashtable)request.getAttribute("Autochargebackreversal");
	Hashtable temphash2=null;
    int records2 = 0;
    int totalrecords2 = 0;
    records2 = Integer.parseInt((String) hash2.get("records2"));
    totalrecords2 = Integer.parseInt((String) hash2.get("totalrecords"));
%>
<form name="" action="" method="post">
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1">
<tr ><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" > Auto ChargeBack Reversal</font></td></tr>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans2"></td>
    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Memberid</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>--%>
	<td valign="middle" align="center" bgcolor="#008BBA">SBM MerchantId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Chargeback Amount</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Current Status</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Proccessing Remark</td>--%>
</tr>
<%


	for(int pos=1;pos<=records2;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash2=(Hashtable)hash2.get(id);
            out.println("<tr>");
			out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"ch_auto\" value=\""+temphash2.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash2.get("icicitransid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash2.get("toid"))+"</td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash2.get("company_name")+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash2.get("merchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash2.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash2.get("status"))+"</td>");
            out.println("</tr>");
            out.println("<input type=\"hidden\" name=\"hid_\""+temphash2.get("icicitransid")+"\" value=\""+temphash2.get("fk_chargeback_process_id")+"\">");
    }

%>
<tr><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><input class="button" type="button" value="Mark Selected transaction as PROCESSED"  onClick="" ></td></tr>
</table>
<br>
<center>
</center>
</form>

<% //file is not in snq  Start
    Hashtable hash5=(Hashtable)request.getAttribute("syncfile");
	Hashtable temphash5=null;
    int records5 = 0;
    int totalrecords5 = 0;
    records5 = Integer.parseInt((String) hash5.get("records5"));
    totalrecords5 = Integer.parseInt((String) hash5.get("totalrecords"));
%>
<form name="notinsync"  action="ProcessSyncTransaction?ctoken=<%=ctoken%>" method="post">
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1" >
<tr ><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" >Listing Not in Sync Transactions </font></td></tr>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA"><input type="checkbox" onClick="ToggleAll(this,'sync');" name="sync"></td>
    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TrackingId</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Memberid</td>
	<%--<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>--%>
	<td valign="middle" align="center" bgcolor="#008BBA">SBM MerchantId</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Chargeback Indicator</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Current Status</td>
</tr>
<%


	for(int pos=1;pos<=records5;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash5=(Hashtable)hash5.get(id);
            out.println("<tr>");
			out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"sync\" value=\""+temphash5.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String)temphash5.get("icicitransid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash5.get("toid"))+"</td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash5.get("company_name")+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash5.get("merchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash5.get("amount"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash5.get("cb_indicator"))+"</td>");
            out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash5.get("status"))+"</td>");
            out.println("</tr>");
            hiddenField = "hid_" + temphash5.get("icicitransid");
            out.println("<input type=\"hidden\"  name=\""+hiddenField+"\" value=\""+temphash5.get("fk_chargeback_process_id")+"\">");




    }

%>
<tr><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><input class="button" type="button" value="Mark Selected transaction as PROCESSED "  onClick="return DoSync();" ></td></tr>
</table>
<br>
<center>
</center>
</form>

<%  //Auto ChargeBack Reversal Table End %>


<%  //Failed File List Table Start
	Hashtable hash3=(Hashtable)request.getAttribute("failfile");
	Hashtable temphash3=null;
    int records3 = 0;
    int totalrecords3 = 0;
    records3 = Integer.parseInt((String) hash3.get("records3"));
    totalrecords3 = Integer.parseInt((String) hash3.get("totalrecords"));
%>
<form name="" action="" method="post">
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1">
<tr ><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" >Failed File List</font></td></tr>
<tr>

    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">File Name</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Remark</td>
</tr>
<%


	for(int pos=1;pos<=records3;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash3=(Hashtable)hash3.get(id);
            out.println("<tr>");
			//out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"icicitransid\" value=\""+temphash3.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;"+temphash3.get("file_name")+"</td>");

            out.println("</tr>");
            //out.println("<input type=\"hidden\" name=\"hid_\""+temphash3.get("icicitransid")+"\" value=\""+temphash3.get("fk_chargeback_process_id")+"\">");
    }

%>
</table>
<br>
<center>
</center>
</form>
<%  //Failed File List Table End %>


<%  //Not found Transaction List Table Start
	Hashtable hash4=(Hashtable)request.getAttribute("notfoundfile");
	Hashtable temphash4=null;
    int records4 = 0;
    int totalrecords4 = 0;
    records4 = Integer.parseInt((String) hash4.get("records4"));
    totalrecords4 = Integer.parseInt((String) hash4.get("totalrecords"));
    %>
<form name="form1" action="" method="post">
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1"  >
<tr ><td valign="middle" colspan="8"  align="center"  align="center" bgcolor="#008BBA"><font face="arial,verdana" size="2" color="#ffffff" >Not found Transaction List</font></td></tr>
<tr>

    <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">File Name</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Transaction line in chargeback file</td>
</tr>
<%


	for(int pos=1;pos<=records4;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		    temphash4=(Hashtable)hash4.get(id);
            out.println("<tr>");
			//out.println("<td "+style+">&nbsp;<input type=\"checkbox\" name=\"icicitransid\" value=\""+temphash3.get("icicitransid")+"\"></td>");
            out.println("<td "+style+">&nbsp;"+srno+ "</td>");

			out.println("<td "+style+">&nbsp;"+(String) temphash4.get("file_name")+"</td>");
            out.println("<td "+style+">&nbsp;<textarea  cols=\"135\" > "+(String) temphash4.get("unprocessed_transactions")+"</textarea> </td>");

            out.println("</tr>");


    }

%>
</table>
<br>
<center>
</center>
</form>
<%  //Not found Transaction List Table End %>


<input type="button" value="Back" onclick="goBack()" />
<%
    }
    else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>
  </body>
</html>