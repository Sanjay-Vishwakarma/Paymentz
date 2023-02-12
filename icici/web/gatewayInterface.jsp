<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.FileDetailsVO" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>

<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/11/14
  Time: 3:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
   Functions functions = new Functions();
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript">
        $(":file").filestyle({buttonText: "Find file"});
    </script>
    <title>Settings> GateWay Master</title>
</head>
<body>
<%
    Map<String,FileDetailsVO> fileDetailsVOMap=new HashMap<String, FileDetailsVO>();

    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (!com.directi.pg.Admin.isLoggedIn(session))
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }

    if(request.getAttribute("fileDetailsVOMap")!=null)
    {
       fileDetailsVOMap= (Map<String, FileDetailsVO>) request.getAttribute("fileDetailsVOMap");
    }
    for(Map.Entry<String,FileDetailsVO> fileDetailsVOEntry:fileDetailsVOMap.entrySet())
    {
        //System.out.println("Reson for failure:::::"+fileDetailsVOEntry.getValue().getFieldName()+" :::::::"+fileDetailsVOEntry.getValue().getReasonOfFailure());
    }
    Enumeration<String> names=request.getParameterNames();
    while(names.hasMoreElements())
    {
        String name=names.nextElement();
       //System.out.println("Name:::::"+name+" value:::::"+request.getParameter(name));
    }
%>
<script type="text/javascript" src="/merchant/stylenew01/filestyle1.js"> </script>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Gateway Master
                <div style="float: right;">
                    <form action="/icici/addGatewayType.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add New Gateway" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Gateway
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                <form action="/icici/addBankPartnerMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                    <button type="submit" class="addnewmember" value="Add Bank Partner Mapping" name="submit">
                        <i class="fa fa-sign-in"></i>
                        &nbsp;&nbsp;Add Bank Partner Mapping
                    </button>
                </form>
            </div>
                <div style="float: right;">
                    <form action="/icici/addBankAgentMapping.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add Bank Agent Mapping" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Agent Mapping
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/listGatewayTypeDetails?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%

                    String str="ctoken=" + ctoken;
                    if(functions.isValueNull(request.getParameter("upload_template")))
                    str+="&upload_template=upload_template";

                    str+="&pgtypeid="+request.getParameter("pgtypeid");
                    str+="&gateway="+request.getParameter("gateway");

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >PgType Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input  type="text" name="pgtypeid"  class="txtbox" >
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Gateway Name</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input   maxlength="15" type="text" name="gateway" class="txtbox">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                    </td>

                                </tr>


                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>

                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

            </form>

        </div>
    </div>
</div>
<div class="reporttable">
<%
    Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;


    String errormsg=(String)request.getAttribute("message");
    if(errormsg!=null)
    {
        out.println("<center><font class=\"textb\"><b>"+errormsg+"</b></font></center>");
    }

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
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
        String buttonName="";
        String buttonClassName="";
        String imgPath="";
%>
    <div style="align:right">
        <%--<form name="exportform" method="post" action="/icici/servlet/listGatewayTypeDetails?ctoken=<%=ctoken%>">--%>
            <input type="hidden" value="<%=functions.isValueNull(request.getParameter("pgtypeid"))?request.getParameter("pgtypeid"):""%>" name="pgtypeid">
            <input type="hidden" value="<%=functions.isValueNull(request.getParameter("gateway"))?request.getParameter("gateway"):""%>" name="gateway">
            <input type="hidden" value="<%=pageno%>" name="SPageno">
            <input type="hidden" value="<%=pagerecords%>" name="SRecords">
             <%--<%
               if(functions.isValueNull(request.getParameter("upload_template")))
               {
                   buttonName="Go Back";
                   buttonClassName = "addnewmember";
               }
                else
               {
                   buttonName="Upload Template";
                   buttonClassName = "button3";
                   imgPath="/merchant/images/pdflogo.jpg";
            %>--%>
            <%--<%
//                }
                if(functions.isValueNull(request.getParameter("upload_template"))){
            %>
                    <button type="submit" class="addnewmember" value="Go Back" name="submit" style="height:27;width:11%;margin-left:81% ;margin-top:0px; margin-bottom:-6px"><i class="fa fa-sign-in"></i><b style="font-size:12px; font-family:inherit">&nbsp;&nbsp;&nbsp;Go Back</b></button>
            <%
                }
                else{
            %>
                    <input type="hidden" value="upload_template" name="upload_template">
                    <button type="submit" class="button3" style="height:30;width:20%;margin-left:79% ;margin-top:0px"><b>Upload Template</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/pdflogo.jpg"></button>
            <%
                }
            %>--%>
       <%-- </form>--%>

    </div>

<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">

        <tr>
            <td valign="middle" class="th0" align="center">Sr no</td>
            <td valign="middle" class="th0" align="center">PaymentGateway Id</td>
            <td valign="middle" class="th0" align="center">Gateway Name</td>
            <td valign="middle" class="th0" align="center">Currency</td>
            <td valign="middle" class="th0" align="center">Name</td>
            <%
               if(functions.isValueNull(request.getParameter("upload_template")))
               {

            %>
            <form action="/icici/servlet/ViewBankTemplate?ctoken=<%=ctoken%>" method="post" name="myformname" id="myformname" target="_blank">
            <%--<input type="hidden" name="ctoken" value="<%=ctoken%>">--%>
                <input type="hidden" value="<%=functions.isValueNull(request.getParameter("pgtypeid"))?request.getParameter("pgtypeid"):""%>" name="pgtypeid">
                <input type="hidden" value="<%=functions.isValueNull(request.getParameter("gateway"))?request.getParameter("gateway"):""%>" name="gateway">
                <input type="hidden" value="<%=pageno%>" name="SPageno">
                <input type="hidden" value="<%=pagerecords%>" name="SRecords">
                <input type="hidden" value="upload_template" name="upload_template">
            <td valign="middle" class="th0" width="5%" align="center">View Template</td>
            <td valign="middle" class="th0" width="5%" align="center">Template</td>
            <td valign="middle" class="th0" width="5%" align="center">Result</td>

            <%
                }
                else
                {
            %>
            <td valign="middle" class="th0" align="center" colspan = "2">Action</td>
            <%
                }
            %>
        </tr>

    <%
        String style="class=td1";
        String ext="light";
        String upload="|Upload";
        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            if(pos%2==0)
            {
                style="class=tr1";
                ext="dark";
            }
            else
            {
                style="class=tr0";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            if(temphash.containsKey("templatename"))
            {
               upload="|"+temphash.get("templatename")+"|Replace";
            }
            out.println("<tr>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+srno+ "</td>");
            out.println("<td "+style+" align=\"center\">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"</td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("gateway"))+"</td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("currency"))+"</td>");
            out.println("<td "+style+" align=\"center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("name"))+"</td>");

            if(functions.isValueNull(request.getParameter("upload_template")))
            {
                if(temphash.containsKey("templatename") || (fileDetailsVOMap.containsKey(temphash.get("pgtypeid")) && fileDetailsVOMap.get(temphash.get("pgtypeid")).isSuccess()))
                out.println("<td "+style+" align=\"center\"><input type=\"image\" src=\"/icici/images/pdflogo.jpg\" width=\"21%\" name=\"action\" value="+temphash.get("pgtypeid")+"></td>");
                else
                    out.println("<td "+style+" align=\"center\"></td>");
                out.println("<td "+style+" align=\"left\"><input type=\"file\" class=\"filestyle\" name=\""+temphash.get("pgtypeid")+"_"+temphash.get("name")+upload+"\"></td>");
                out.println("<td><div width=\"10px\" class=\"scrolladmin\">");
                if(fileDetailsVOMap.containsKey(temphash.get("pgtypeid")) && !fileDetailsVOMap.get(temphash.get("pgtypeid")).isSuccess())
                {
                    out.println("<span class=\"txtboxerror\" style=\"margin-left: 20%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size:11px\">"+fileDetailsVOMap.get(temphash.get("pgtypeid")).getReasonOfFailure()+"</span>");
                }
                else if(fileDetailsVOMap.containsKey(temphash.get("pgtypeid")) && fileDetailsVOMap.get(temphash.get("pgtypeid")).isSuccess())
                {
                    out.println("<span class=\"txtboxconfirm\" style=\"margin-left: 20%;padding-bottom: 1%;padding-top: 1%;padding-right: 1%;padding-left: 1%;font-size:11px\">Success</span>");
                }
                out.println("</div>");
                out.println("</td>");
            }
            else
            {
            out.println("<td "+style+" align=\"center\"><form action=\"/icici/servlet/viewGatewayTypeDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"pgtypeid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"View\"><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
            out.println("<td "+style+" align=\"center\"><form action=\"/icici/servlet/viewGatewayTypeDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"pgtypeid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("pgtypeid"))+"\"><input type=\"submit\" name=\"submit\" class=\"gotoauto\" value=\"Edit\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");
            }
            out.println("</tr>");
        }
        if(functions.isValueNull(request.getParameter("upload_template")))
        {
    %>
    <tr>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0">  </td>
        <td class="th0"></td>
        <td class="th0"><Button type="submit" align="center" value="Upload" class="addnewmember" onclick="myUploadBankTemplate('<%=ctoken%>')">Upload</Button></td>
    </tr>
      <%
          }
      %>
       </form>
</table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="listGatewayTypeDetails"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>

<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
    }

%>
</div>
<script src='/icici/stylenew/AfterAppManager.js'></script>
</body>
</html>