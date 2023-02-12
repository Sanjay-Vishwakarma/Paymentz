<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.directi.pg.Merchants" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/26/13
  Time: 2:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
  <title></title>
  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
  <script language="javascript">

    function check() {
      var  retpath = document.form1.logoName.value;
      var pos = retpath.lastIndexOf(".");
      var filename="";
      if (pos != -1)
        filename = retpath.substring(pos + 1);
      else
        filename = retpath;

      if (filename==('jpeg')||filename==('bmp')||filename==('jpg')||filename==('gif')||filename==('ico') ||filename==('png')) {

        return true;
      }
      alert('Please select a valid image file (.jpg/.bmp./.png/.ico/.gif/.jpeg) instead!');
      return false;

    }


  </script>
</head>

<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    Hashtable partneridhash= Merchants.partnerDetailsHash;
    String partid = Functions.checkStringNull(request.getParameter("partid"));

%>

<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Partner Icon
        <div style="float: right;">
          <form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Partner Details
            </button>
          </form>
        </div>
      </div>
      <br>
      <%

        String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
        if (errormsg == null)
        {
          errormsg = "";
        }
        if (request.getParameter("MES") != null)
        {
          String mes = (String) request.getParameter("MES");


          if (mes.equals("F"))
          {
            out.println("<table align=\"center\"><tr><td align=center ><font class=\"textb\" ><b>Logo File Upload Failed...");
            out.println("</b></font></td></tr><tr><td algin=\"center\" >");
            out.println("<font class=\"textb\" ><b>");
            errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
            out.println(errormsg);

            out.println("</b></font></td></tr></table>");

          }
          else
          {
            String partnerName = null;
            if(partid!=null)
              partnerName=(String)partneridhash.get(partid);
            out.println("<table align=\"center\"><tr><td align=center><font class=\"textb\" ><b>Logo File Uploaded Successfully...  ");
            out.println("</b></font></td></tr><tr><td algin=\"center\" >");

            out.println("</td></tr></table>");
          }

        }

      %>
      <table  align="center" width="70%" cellpadding="2" cellspacing="2">
        <form action="/icici/servlet/NewPartnerIcon?ctoken=<%=ctoken%>" method="post" name="form1" ENCTYPE="multipart/form-data">
          <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >

          <tr>
            <td>

              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center" >


                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Partner Name</td>
                  <td class="textb">:</td>
                  <td class="textb">
                    <select size="1" name="partid" class="txtbox">
                      <option value="">Select Partner</option>
                      <%
                        String key ="";
                        String value = "";
                        String selected3 = "";
                        //ArrayList retHashMap=new ArrayList();
                        TreeMap retHashMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
                        Iterator iter = partneridhash.entrySet().iterator();
                        while (iter.hasNext())
                        {
                          Map.Entry entry = (Map.Entry) iter.next();
                          key = String.valueOf(entry.getKey());
                          value = String.valueOf(entry.getValue());
                          //retHashMap.;
                          retHashMap.put(value,key);
                          //retHashMap.put(value, key);
                        }


                        Iterator iter1 = retHashMap.entrySet().iterator();

                        while (iter1.hasNext())
                        {
                          Map.Entry entry1 = (Map.Entry) iter1.next();
                          if (entry1.getValue().equals(partid))
                            selected3 = "selected";
                          else
                            selected3 = "";

                      %>
                      <option value="<%=entry1.getValue()%>" <%=selected3%>><%=entry1.getKey()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"><span class="textb">Icon Name*</span><br>
                    (Ex. logo.jpg / logo.jpeg)</td>
                  <td class="textb">:</td>
                  <td class="textb"><input  type="file" maxlength="30"   value="Select Icon File" name="logonm" size="35"></td>
                </tr>

                <tr><td colspan="4">&nbsp;</td></tr>

                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb"></td>
                  <td class="textb"></td>
                  <td>
                    <input id="submit" type="Submit" value="Submit" name="Submit" class="buttonform" onclick="return check()">
                  </td>
                </tr>
                <tr><td colspan="4">&nbsp;</td></tr>

              </table>

            </td>
          </tr>
        </form>
      </table>
    </div>
  </div>
</div>

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