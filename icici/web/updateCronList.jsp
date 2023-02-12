<%@ page import=" java.util.Hashtable, java.util.Iterator, java.util.List" %>
<%@ page import="com.logicboxes.cron.CronManager"%>
<%@ page import="com.logicboxes.cron.AlarmEntry"%>
<%@ include file="index.jsp"%>
<head>
    <title>Update Cron List</title>
    <link rel="stylesheet" type="text/css" href="./styles.css">

</head>

<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    CronManager cronManager = CronManager.getInstance();
    List list;

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
                    out.println("Cron " + removeClass + ", " + removeMethod);
                    if (success)
                        out.println("Removed successfully<BR><BR>");
                    else
                        out.println(" Could not be Removed<BR><BR>");
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
                    out.println("Cron " + removeClass + ", " + removeMethod);
                    if (success)
                        out.println("Restored successfully<BR><BR>");
                    else
                        out.println(" Could not be Restored<BR><BR>");
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
<p><a href="/icici/listCrons.jsp?ctoken=<%=ctoken%>">Return to Cron List</a>
</p>
</body>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>