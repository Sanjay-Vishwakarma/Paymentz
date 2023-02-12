  <%@ page import="org.owasp.esapi.ESAPI" %>
  <%@ page import="com.logicboxes.util.ApplicationProperties" %>

  <%


      ESAPI.authenticator().logout();
  %>
  <html>
  <head>
      <title>Session Expired</title>
  </head>
  <body >
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <table bgcolor="#007ACC" align="center" width="50%" cellpadding="2" cellspacing="0" >
      <tr>
          <td  ><font size="4" color="#ffffff"><strong>Session Expired.</strong></font><br>
              <table bgcolor="#CCE0FF"  width="100%" align="center" cellpadding="2" cellspacing="0">
                  <tr >
                      <td align="center"><font size="2" face="Arial, Helvetica, sans-serif" align="center">Suggestion</font><br><p>*For security reasons, we have disabled double clicks and Back, Forward and Refresh tabs of the browser. Also,&nbsp;the&nbsp;session will expire automatically, if the browser window is idle for a long time. <br><br>*If the problem persists, please try again after clearing the <strong>Temporary Files </strong>from your web browser. <br></p></td>
                  </tr>
              </table></td>
      </tr>
  </table>
  </body>
  </html>


