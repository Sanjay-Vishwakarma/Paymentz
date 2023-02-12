<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.*" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 8/3/15
  Time: 7:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="ietest.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Reports");

%>
<html>
<head>
  <title>Partner  |Merchant Wire Reports</title>
</head>
<body class="bodybackground">
<%@ include file="top.jsp" %>
<link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>

<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
  });
</script>
<script>
  function download()
  {
    var answer = confirm("do you want to Download File?");
    if(answer == true)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
</script>


<style type="text/css">
  #myTable th{text-align: center;}

</style>


<%!
  private static Logger logger = new Logger("MerchantWireReports.jsp");
  private static TerminalManager terminalManager = new TerminalManager();
%>
<%
  if (partner.isLoggedInPartner(session))
  {
    StringBuffer optionTag=new StringBuffer();
    Set<String> memberList=new HashSet<String>();
    List<TerminalVO> terminalVO = terminalManager.getTerminalsByPartnerId(session.getAttribute("merchantid").toString());
    String fromdate = null;
    String todate = null;

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

    for(TerminalVO terminalVO1:terminalVO)
    {
      if(terminalVO1.getTerminalId().equals(request.getParameter("terminalid")))
        optionTag.append("<option value=\""+terminalVO1.getTerminalId()+"\" selected>"+terminalVO1.toString()+"</option>");
      else
        optionTag.append("<option value=\""+terminalVO1.getTerminalId()+"\" >"+terminalVO1.toString()+"</option>");
      memberList.add(terminalVO1.getMemberId());
    }
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String listMerchantWireRandomCharges_Random_Charges = StringUtils.isNotEmpty(rb1.getString("listMerchantWireRandomCharges_Random_Charges")) ? rb1.getString("listMerchantWireRandomCharges_Random_Charges") : "Random Charges Applied In Current Wire";
    String listMerchantWireRandomCharges_Result = StringUtils.isNotEmpty(rb1.getString("listMerchantWireRandomCharges_Result")) ? rb1.getString("listMerchantWireRandomCharges_Result") : "Result";
    String listMerchantWireRandomCharges_charges = StringUtils.isNotEmpty(rb1.getString("listMerchantWireRandomCharges_charges")) ? rb1.getString("listMerchantWireRandomCharges_charges") : "Random Charges Not Found In Current Wire.";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <br><br>
      <%--<div class="pull-right">
        <div class="btn-group">
          <form action="/partner/net/MerchantWireReports?ctoken=<%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if("memberid".equals(name))
                {
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
      <br><br><br>--%>

      <%--<form  name="form" method="post" action="/partner/net/MerchantWireReports?ctoken=<%=ctoken%>">

        <div class="row">

          <div class="col-sm-12 portlets ui-sortable">

            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Wire Reports</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                  <div class="form-group col-md-4 has-feedback">
                    <label>From</label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=fromdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>To</label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=todate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>


                  <div class="form-group col-md-4 has-feedback">
                    <label>Member ID</label>
                    <select size="1" name="toid" class="form-control">
                      <option value="">All</option>
                      <%
                        for(String toId:memberList)
                        {
                          if(toId.equals(request.getParameter("toid")))
                            out.println("<option value=\""+toId+"\" selected>"+toId+"</option>");
                          else
                            out.println("<option value=\""+toId+"\" >"+toId+"</option>");
                        }
                      %>
                    </select>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Terminal ID</label>
                    <select size="1" name="terminalid" class="form-control">
                      <option value="">All</option>
                      <%=optionTag%>
                    </select>
                    &lt;%&ndash;<input type=text name="trackingid"  maxlength="20" class="form-control" size="15"<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" >&ndash;%&gt;
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Is Paid</label>
                    <select size="1" name="paid" class="form-control">
                      <option value="all">All</option>
                      <option value="paid">Paid</option>
                      <option value="unpaid">Unpaid</option>
                    </select>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="color: transparent;">Search</label>
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;Search</button>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>

      </form>--%>

      <div class="row reporttable">

        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%--Summary Data--%><%=listMerchantWireRandomCharges_Random_Charges%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%--<div class="table-responsive datatable">--%>
              <%--<input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">--%>

              <%----------------------Report Start-----------------------------------%>
              <%--<div align="center" class="textb"><h5><b><u>Random Charges Applied In Current Wire</u></b></h5></div>--%>

              <%

                Functions functions=new Functions();
                Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                Hashtable temphash=null;
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
                  hash = (Hashtable)request.getAttribute("transdetails");
                }
                if(records>0)
                {
              %>



              <table id="myTable" class="display table table table-striped table-bordered dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="background-color: #7eccad !important;color: white;">
                  <th>Sr No</th>
                  <th>Member ID</th>
                  <th>Terminal ID</th>
                  <th>Charge Name</th>
                  <th>Charge Rate</th>
                  <th>Charge Counter</th>
                  <th>Charge Amount</th>
                  <th>Charge Value</th>
                  <th>Charge Value Type</th>
                  <th>Charge Remark</th>
                </tr>
                </thead>

                <tbody>


                <%
                  String style="class=td1";
                  String ext="light";
                  for(int pos=1;pos<=records;pos++)
                  {
                    String id=Integer.toString(pos);
                    int srno=Integer.parseInt(id);
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
                    temphash=(Hashtable)hash.get(id);
                    out.println("<tr>");
                    out.println("<td align=\"center\" data-label=\"Sr No\" "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td align=\"center\" data-label=\"Member ID\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("memberid"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Terminal ID\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Name\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Rate\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargerate"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Counter\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargecounter"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Amount\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeamount"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Value\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargevalue"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Value Type\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("valuetype"))+"</td>");
                    out.println("<td align=\"center\" data-label=\"Charge Remark\" "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeremark"))+"</td>");
                    out.println("</tr>");
                  }

                %>


                </tbody>

              </table>

              <%--<div id="showingid"><strong>Showing Page <%=pageno%> of <%=totalrecords%> records</strong></div>

              <jsp:include page="page.jsp" flush="true">
                  <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                  <jsp:param name="numrows" value="<%=pagerecords%>"/>
                  <jsp:param name="pageno" value="<%=pageno%>"/>
                  <jsp:param name="str" value="<%=str%>"/>
                  <jsp:param name="page" value="MerchantWireReports"/>
                  <jsp:param name="currentblock" value="<%=currentblock%>"/>
                  <jsp:param name="orderby" value=""/>
              </jsp:include>--%>

            </div>
          </div>
        </div>
      </div>

      <%

          }
          else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
          {
            out.println(Functions.NewShowConfirmation1("Result",(String)request.getAttribute("statusMsg")));
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(listMerchantWireRandomCharges_Result, listMerchantWireRandomCharges_charges));
          }
        }
        else
        {
          response.sendRedirect("/partner/Logout.jsp");
          return;
        }

      %>

    </div>
  </div>
</div>


</body>
</html>