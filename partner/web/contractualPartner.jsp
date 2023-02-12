<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.manager.ApplicationManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","contractualPartner");
%>
<html lang="en">
<head>
  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
  <%--<script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <title><%=company%> Application Manager> Contractual Partner</title>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }

  </script>
</head>
<body>
<%
  ApplicationManager applicationManager = new ApplicationManager();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String partnerid=(String)session.getAttribute("merchantid");
  List<String> memberidDetails = applicationManager.getPartnerBankDetail((String) session.getAttribute("merchantid"));

  // String memberid=nullToStr((String)request.getAttribute("toid"));
%>
<%
  String pid = "";
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  String Config ="";
  if(Roles.contains("superpartner")){
    pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
  }else{
    Config = "disabled";
    pid = String.valueOf(session.getAttribute("merchantid"));
  }
  String bankName = Functions.checkStringNull(request.getParameter("bankName"))==null?"":request.getParameter("bankName");
  String contractualPartnerId = Functions.checkStringNull(request.getParameter("contractualpartid"));
  String contractualPartnerName = Functions.checkStringNull(request.getParameter("contractualpartname"));

  String str="ctoken=" + ctoken;

  if(pid!=null)str = str + "&pid=" + pid;
  if(bankName!=null)str = str + "&bankName=" + bankName;
  if(contractualPartnerId!=null)str = str + "&contractualpartid=" + contractualPartnerId;
  if(contractualPartnerName!=null)str = str + "&contractualpartname=" + contractualPartnerName;

  int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String contractualPartner_Add_Contractual_Partner = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Add_Contractual_Partner")) ? rb1.getString("contractualPartner_Add_Contractual_Partner") : "Add Contractual Partner";
  String contractualPartner_Contract_Details = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Contract_Details")) ? rb1.getString("contractualPartner_Contract_Details") : "Contract Details";
  String contractualPartner_PartnerId = StringUtils.isNotEmpty(rb1.getString("contractualPartner_PartnerId")) ? rb1.getString("contractualPartner_PartnerId") : "Partner Id";
  String contractualPartner_Bank_Name = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Bank_Name")) ? rb1.getString("contractualPartner_Bank_Name") : "Bank Name";
  String contractualPartner_ALL = StringUtils.isNotEmpty(rb1.getString("contractualPartner_ALL")) ? rb1.getString("contractualPartner_ALL") : "ALL";
  String contractualPartner_Search = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Search")) ? rb1.getString("contractualPartner_Search") : "Search";
  String contractualPartner_Report_Table = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Report_Table")) ? rb1.getString("contractualPartner_Report_Table") : "Report Table";
  String contractualPartner_Sr_No = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Sr_No")) ? rb1.getString("contractualPartner_Sr_No") : "Sr No";
  String contractualPartner_Contractual_PartnerId = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Contractual_PartnerId")) ? rb1.getString("contractualPartner_Contractual_PartnerId") : "Contractual PartnerId";
  String contractualPartner_Contractual_Partner_Name = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Contractual_Partner_Name")) ? rb1.getString("contractualPartner_Contractual_Partner_Name") : "Contractual Partner Name";
  String contractualPartner_Action = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Action")) ? rb1.getString("contractualPartner_Action") : "Action";
  String contractualPartner_ShowingPage = StringUtils.isNotEmpty(rb1.getString("contractualPartner_ShowingPage")) ? rb1.getString("contractualPartner_ShowingPage") : "Showing Page";
  String contractualPartner_of = StringUtils.isNotEmpty(rb1.getString("contractualPartner_of")) ? rb1.getString("contractualPartner_of") : "of";
  String contractualPartner_records = StringUtils.isNotEmpty(rb1.getString("contractualPartner_records")) ? rb1.getString("contractualPartner_records") : "records";
  String contractualPartner_Sorry = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Sorry")) ? rb1.getString("contractualPartner_Sorry") : "Sorry";
  String contractualPartner_No = StringUtils.isNotEmpty(rb1.getString("contractualPartner_No")) ? rb1.getString("contractualPartner_No") : "No Records Found.";
  String contractualPartner_Modify = StringUtils.isNotEmpty(rb1.getString("contractualPartner_Modify")) ? rb1.getString("contractualPartner_Modify") : "Modify";
  String contractualPartner_page_no = StringUtils.isNotEmpty(rb1.getString("contractualPartner_page_no")) ? rb1.getString("contractualPartner_page_no") : "Page number";
  String contractualPartner_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("contractualPartner_total_no_of_records")) ? rb1.getString("contractualPartner_total_no_of_records") : "Total number of records";



%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/addContractualPartner.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" name="submit" name="B1"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 8px 21px;font-size: 10px;cursor: pointer;">
              <%=contractualPartner_Add_Contractual_Partner%> &nbsp; <i class ="fa fa-plus-square" style="font-size: small; color: white;"></i>
            </button>
            <%--<button class="btn-xs" type="submit" value="contractualPartner" name="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/newuser.png">
            </button>--%>
          </form>
        </div>
      </div>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=contractualPartner_Contract_Details%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <%
              String res=(String)request.getAttribute("message");
              if(res!=null)
              {
                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+res+"</b></font></center>");
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+res+"</h5>");
              }

              String errorMsg=(String)request.getAttribute("error");
              if(errorMsg!=null)
              {
                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+errorMsg+"</b></font></center>");
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorMsg+"</h5>");
              }
              String error=(String)request.getAttribute("errorMsg");
              if(error!=null)
              {
                //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+errorMsg+"</b></font></center>");
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+error+"</h5>");
              }
              String error1 = (String) request.getAttribute("error1");
              if(error1!=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error1 + "</h5>");
              }
            %>
            <div class="widget-content padding">
              <div id="horizontal-form">

                <form action="/partner/net/ContractualPartner" method="post" name="forms" >

                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <input type="hidden" id="partnerid" value="<%=partnerid%>" name="ctoken">

                  <div class="form-group col-md-4">
                    <label><%=contractualPartner_PartnerId%></label>
                    <input type="text" value="<%=pid%>" name="pid"  id="pid" maxlength="20" class="form-control" autocomplete="on" <%=Config%>>
                  </div>

                  <div class="form-group col-md-4">
                    <label><%=contractualPartner_Bank_Name%></label>

                    <select size="1" name="bankName" class="form-control">
                      <option value=""><%=contractualPartner_ALL%></option>
                      <%
                        if(memberidDetails.size()>0)
                        {
                          Iterator iterator = memberidDetails.iterator();

                          while (iterator.hasNext())
                          {
                            //Map.Entry<String,String> memberEntry = (Map.Entry<String, String>) iterator.next();

                            String memberName = (String) iterator.next();
                            String select = "";
                            if(memberName.equalsIgnoreCase(bankName))
                              select = "selected";
                            else
                              select = "";
                      %>
                      <option value="<%=memberName%>" <%=select%>><%=memberName%></option>
                      <%
                          }
                        }
                      %>
                    </select>
                  </div>

                  <div class="form-group col-md-4">
                    <label>&nbsp;</label>
                    <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-floppy-o"></i>&nbsp;&nbsp;<%=contractualPartner_Search%></button>

                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <br>


      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=contractualPartner_Report_Table%></strong></h2>
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

                Hashtable hash = (Hashtable)request.getAttribute("transdetails");

                Hashtable temphash=null;
                int records=0;
                int totalrecords=0;

                String errormsg=(String)request.getAttribute("error");

                if(errormsg!=null)
                {
                  out.println("<center><font class=\"textb\">"+errormsg+"</font></center>");
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
                  str = str + "&pid=" + pid;
                  str = str + "&bankname=" + bankName;
                  hash = (Hashtable)request.getAttribute("transdetails");
                }
                if(records>0)
                {
              %>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_Sr_No%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_PartnerId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_Bank_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_Contractual_PartnerId%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_Contractual_Partner_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=contractualPartner_Action%></b></td>

                </tr>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                </thead>

                <%


                  String style="class=td1";

                  for(int pos=1;pos<=records;pos++)
                  {
                    String id = Integer.toString(pos);

                    int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                    temphash = (Hashtable) hash.get(id);
                    out.println("<tr>");
                    out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\">&nbsp;" + srno + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Partner Id\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("partnerid")) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Bank Name\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("bankname")) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Contractual PartnerId\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("contractual_partnerid")) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Contractual Partner Name\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("contractual_partnername")) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\">");
                    out.println("<form action=\"/partner/net/UpdateContractualPartner?ctoken="+ctoken+"\" method=\"post\">" +
                            "<input type=\"hidden\" name=\"partnerid\" value=\""+temphash.get("partnerid")+"\" >" +
                            "<input type=\"hidden\" name=\"bankname\" value=\""+temphash.get("bankname")+"\" >" +
                            "<input type=\"hidden\" name=\"action\" value=\"modify\">" +
                            "<input type=\"submit\" class=\"gotoauto btn btn-default\" value="+contractualPartner_Modify+">");
                    out.println(requestParameter.toString());
                    out.println("</form></td>");
                    out.println("</tr>");
                  }

                %>
              </table>
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
              <div id="showingid"><strong><%=contractualPartner_page_no%> <%=pageno%> <%=contractualPartner_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
              <div id="showingid"><strong><%=contractualPartner_total_no_of_records%>   <%=totalrecords%> </strong></div>


              <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ContractualPartner"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
              </jsp:include>

              <%
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(contractualPartner_Sorry,contractualPartner_No));
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