<%@ page import="java.util.*"%>
<%@ page import="com.logicboxes.util.Util"%>
<%@ page import="com.logicboxes.cron.CronManager"%>
<%@ page import="com.logicboxes.cron.AlarmEntry"%>
<%@ include file="index.jsp"%>
<%@ page import="com.directi.pg.Logger"%>
<%!
    private static Logger logger=new Logger("listCrons.jsp");
%>
<head>
    <title>Settings> Cron List</title>
    <script language="Javascript">
        function removeCron(className, methodName)
        {

            if (confirm("Are you sure you want to remove this Cron?"))
            {    var ctoken= document.cron.ctoken.value;

                document.cron.className.value = className;
                document.cron.methodName.value = methodName;
                document.cron.doWhat.value = 'Remove';
                document.cron.action = '/icici/listCrons.jsp?ctoken='+ctoken;
                document.cron.submit();
            }
        }

        function updateCron(className, methodName)
        {
            var ctoken= document.cron.ctoken.value;

            document.cron.className.value = className;
            document.cron.methodName.value = methodName;
            document.cron.action = '/icici/updateCronTime.jsp?ctoken='+ctoken;
            document.cron.submit();
        }

        function reloadList()
        {   var ctoken= document.cron.ctoken.value;

            document.cron.doWhat.value = 'Reload';
            document.cron.action = '/icici/listCrons.jsp?ctoken='+ctoken;
            document.cron.submit();
        }

        function restoreCron(className, methodName)
        {
            if (confirm("Are you sure you want to Restore this Cron?"))
            {
                var ctoken= document.cron.ctoken.value;

                document.cron.className.value = className;
                document.cron.methodName.value = methodName;
                document.cron.doWhat.value = 'Restore';
                document.cron.action = '/icici/listCrons.jsp?ctoken='+ctoken;
                document.cron.submit();
            }
        }

    </script>

</head>

<body class="bodybackground">
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px">

<%   ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>






<%
    try{
    CronManager cronManager = CronManager.getInstance();
    List list = cronManager.getAllCrons();

%>
    <%
        String removeClass = request.getParameter("className");
        String removeMethod = request.getParameter("methodName");
        String doWhat = request.getParameter("doWhat");

        if ((removeClass != null && !"".equals(removeClass)) && (removeMethod != null && !"".equals(removeMethod)))
        {
            if ("Remove".equals(doWhat))
            {
                list = cronManager.getAllCrons();
                for (Iterator i = list.iterator(); i.hasNext();)
                {
                    AlarmEntry alarm = (AlarmEntry) i.next();
                    Hashtable args = alarm.args;
                    String className = alarm.className;// (String) args.get("classname");
                    String methodName = alarm.methodName;//(String) args.get("methodname");
                    if (removeClass.equals(className) && removeMethod.equals(methodName))
                    {
                        boolean success = cronManager.removeCron(alarm);
                        out.println("<br><center><font class=\"textb\"><b>");
                        out.println("Cron " + removeClass + ", " + removeMethod);
                        if (success)
                            out.println("Removed successfully<BR><BR>");
                        else
                            out.println(" Could not be Removed<BR><BR>");
                        out.println("</b></font>");
                        break;

                    }
                }
            }
            else if ("Restore".equals(doWhat))
            {
                list = cronManager.getRemovedCrons();
                for (Iterator i = list.iterator(); i.hasNext();)
                {
                    AlarmEntry alarm = (AlarmEntry) i.next();
                    Hashtable args = alarm.args;
                    String className = alarm.className;//(String) args.get("classname");
                    String methodName = alarm.methodName;//(String) args.get("methodname");
                    if (removeClass.equals(className) && removeMethod.equals(methodName))
                    {
                        alarm.reSetAlarmTime(alarm);
                        boolean success = cronManager.restoreCron(alarm);
                        out.println("<center><font class=\"textb\"><b>");
                        out.println("Cron " + removeClass + ", " + removeMethod);
                        if (success)
                            out.println("Restored successfully<BR><BR>");
                        else
                            out.println(" Could not be Restored<BR><BR>");
                        out.println("</b></font>");
                        break;
                    }
                }
            }
        }
        else if ("Reload".equals(doWhat))
        {
            cronManager.refreshCronList();
            out.println("Cron list refreshed successfully<BR><BR>");
        }
    %>
    <table width="100%">
        <tr>
            <td class="textb" align="left" style="font-weight: bold;font-size:13px ">
                &nbsp;&nbsp;&nbsp;List of Active Crons
            </td>
            <td class="textb" align="right">
                <font color=red>Red</font> = Non-Idempotent<br>
                Current time: <%="<B>" + new Date() + "</B>"%>
            </td>

        </tr>
        <tr>
            <td>
                &nbsp;
            </td>
        </tr>
    </table>

<table width="90%" border="1" class="table table-striped table-bordered table-green dataTable">

     <form name="cron" action="" method="post">
        <input type="hidden" name="className" value="">
        <input type="hidden" name="methodName" value="">
        <input type="hidden" name="doWhat" value="">
        <input type="hidden" name="ctoken" value="<%=ctoken%>">
    </form>
    <thead>
    <tr>
        <td class="th0">Cron Params</td>
        <td class="th0">Next Alarm</td>
        <td class="th0">Remove Cron</td>
        <td class="th0">Run Cron</td>

    </tr>
    </thead>
    <%
        int ct = 1;
        int count  = 0 ;
        try
        {
            for (Iterator i = list.iterator(); i.hasNext();)
            {
                if (ct > 2)
                    ct = 1;
                out.println("<tr class=\"tr0" + ct + "\">");
                AlarmEntry alarm = (AlarmEntry) i.next();
                Hashtable args = alarm.args;
                String className = alarm.className;//(String) args.get("classname");
                String methodName = alarm.methodName;//(String) args.get("methodname");
                if(!alarm.isIdempotent)
                {
                 out.println("<td align=center class=\"textb\">Class: " + className + "<BR>Method: " + methodName + "</td>");
                }else
                {
                 out.println("<td align=center class=\"textb\">Class: " + className + "<BR>Method: " + methodName + "</font></td>");
                }
                out.println("<td align=center class=\"textb\">" + new Date(alarm.alarmTime) + "</font></td>");
                String remove = "removeCron('" + className + "','" + methodName + "')";
                String update = "updateCron('" + className + "','" + methodName + "')";
                //out.println("Call: " + call);
                out.println("<td align=center><input type=\"button\" class=\"gotoauto\" value=\"Remove\" onclick=\"" + remove + "\"></td>");
                out.println("<td align=center><input type=\"button\" class=\"gotoauto\" value=\"Run Now\" onclick=\"" + update + "\"></td>");
                //out.println("<td><font face=\"verdana\" size=\"1\">" + alarm.className + "</font></td>");
                //out.println("<td><font face=\"verdana\" size=\"1\">" + alarm.methodName + "</font></td>");
                out.println("</tr>");
                ct++;
                count++;
            }
        }
        catch (Exception e)
        {   logger.error("error",e);
            out.println("ERROR: in listActiveCrons" + e);
        }
    %>
</table>

<p class="textb" >&nbsp;&nbsp;&nbsp;Total No Of Crons = <%=count%></p>

<%
    list = cronManager.getRemovedCrons();
%>
</div>
<div class="reporttable">
<p class="textb"  style="font-weight: bold;font-size:13px ">&nbsp;&nbsp;&nbsp;List of Removed Crons</p>
<table width="90%" border="1" bordercolorlight="#336699" bordercolordark="#FFFFFF" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
        <tr>
            <td class="th0">Cron Params</td>
            <td class="th0">Next Alarm</td>
            <td class="th0">Restore Cron</td>
            <!--
              <td class="tableheadings"><font face="verdana" size="2">Cron Class</font></td>
              <td class="tableheadings"><font face="verdana" size="2">Cron Method</font></td>
              -->
        </tr>
    </thead>
    <%
        ct = 1;
        count = 0;
        try
        {
            for (Iterator i = list.iterator(); i.hasNext();)
            {
                if (ct > 2)
                    ct = 1;
                out.println("<tr class=\"tr0" + ct + "\">");
                AlarmEntry alarm = (AlarmEntry) i.next();
                //Hashtable args = alarm.args;
                String className = alarm.className; //(String) get("classname");
                String methodName = alarm.methodName;//(String) args.get("methodname");
                out.println("<td align=center class=\"textb\">Class: " + className + "<BR>Method: " + methodName + "</font></td>");
                out.println("<td align=center class=\"textb\">" + new Date(alarm.alarmTime) + "</font></td>");
                String restore = "restoreCron('" + className + "','" + methodName + "')";
		        out.println("<td align=center><input type=\"button\" class=\"gotoauto\" value=\"Restore\" onclick=\"" + restore + "\"></td>");
                out.println("</tr>");
                ct++;
                count++;
            }
        }
        catch (Exception e)
        {   logger.error("error",e);
            out.println("ERROR in listRemovedCrons: " + e);
        }
    %>
</table>
<p class="textb">&nbsp;&nbsp;&nbsp;Total No Of Removed Crons = <%=count%></p> <br>
<%
  }
  catch(NoClassDefFoundError ne){
        out.println("ERROR in listRemovedCrons:"+Util.getStackTrace(ne));
        logger.error("Exception while getting listRemovedCrons ",ne);

  }
    catch (Throwable t){
        out.println("ERROR in listRemovedCrons: "+t);
        logger.error("Exception while getting listRemovedCrons ",t);

    }
%>
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