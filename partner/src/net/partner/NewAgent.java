package net.partner;
import com.directi.pg.*;
import com.manager.dao.PartnerDAO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationAccountsException;
import org.owasp.esapi.errors.AuthenticationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Kanchan on 20-01-2021.
 */
public class NewAgent extends HttpServlet
{
    //static final String ROLE = "partner";
    private static Logger Log = new Logger(NewAgent.class.getName());
    Functions functions = new Functions();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = Functions.getNewSession(request);
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        Log.debug("Entering in new agent...");
        PartnerFunctions partner = new PartnerFunctions();

        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        response.setContentType("text/html");
        PrintWriter out=response.getWriter();
        String role=(String)session.getAttribute("role");
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String redirectpage = "";
        String successMsg ="";
        String errormsg = "";
        String msg = "F";
        String EOL = "<BR>";
        String passwd=request.getParameter("passwd");
        String conpasswd=request.getParameter("conpasswd");
        String company_name=request.getParameter("company_name");
        String sitename=request.getParameter("sitename");
        String telno=request.getParameter("telno");
        String supporturl=request.getParameter("supporturl");
        String notifyemail=request.getParameter("notifyemail");
        String country=request.getParameter("country");
        String logoName=request.getParameter("logoName");
        String emailTemplateLang=request.getParameter("emailTemplateLang");
        String isipwhitelisted=request.getParameter("isipwhitelisted");
        String contact_persons=request.getParameter("contact_persons");
        String contact_emails=request.getParameter("contact_emails");
        String maincontact_ccmailid=request.getParameter("maincontact_ccmailid");
        String maincontact_phone=request.getParameter("maincontact_phone");
        String cbcontact_name=request.getParameter("cbcontact_name");
        String cbcontact_mailid=request.getParameter("cbcontact_mailid");
        String cbcontact_ccmailid=request.getParameter("cbcontact_ccmailid");
        String cbcontact_phone=request.getParameter("cbcontact_phone");
        String refundcontact_name=request.getParameter("refundcontact_name");
        String refundcontact_mailid=request.getParameter("refundcontact_mailid");
        String refundcontact_ccmailid=request.getParameter("refundcontact_ccmailid");
        String refundcontact_phone=request.getParameter("refundcontact_phone");
        String salescontact_name=request.getParameter("salescontact_name");
        String salescontact_mailid=request.getParameter("salescontact_mailid");
        String salescontact_ccmailid=request.getParameter("salescontact_ccmailid");
        String salescontact_phone=request.getParameter("salescontact_phone");
        String billingcontact_name=request.getParameter("billingcontact_name");
        String billingcontact_mailid=request.getParameter("billingcontact_mailid");
        String billingcontact_ccmailid=request.getParameter("billingcontact_ccmailid");
        String billingcontact_phone=request.getParameter("billingcontact_phone");
        String fraudcontact_name=request.getParameter("fraudcontact_name");
        String fraudcontact_mailid=request.getParameter("fraudcontact_mailid");
        String fraudcontact_ccmailid=request.getParameter("fraudcontact_ccmailid");
        String fraudcontact_phone=request.getParameter("fraudcontact_phone");
        String technicalcontact_name=request.getParameter("technicalcontact_name");
        String technicalcontact_mailid=request.getParameter("technicalcontact_mailid");
        String technicalcontact_ccmailid=request.getParameter("technicalcontact_ccmailid");
        String technicalcontact_phone=request.getParameter("technicalcontact_phone");


        if (((String) request.getParameter("step")).equals("1"))
        {
            Hashtable detailhash= new Hashtable();
            Log.debug("In NewAgent.java.....");
            try
            {
                boolean flag= true;
                detailhash.put("login",  request.getParameter("username"));
                if (request.getParameter("passwd") != null)
                {
                    passwd = AesCtr.decrypt(request.getParameter("passwd"), request.getParameter("ctoken"), 256);
                    request.setAttribute("passwd", passwd);
                }
                if (request.getParameter("conpasswd") != null)
                {
                    conpasswd = AesCtr.decrypt(request.getParameter("conpasswd"), request.getParameter("ctoken"), 256);
                    request.setAttribute("conpasswd", conpasswd);
                }
                if ((!ESAPI.validator().isValidInput("passwd", passwd, "NewPassword", 20, false)))
                {
                    flag = false;
                    errormsg = errormsg + "Please enter valid Password." + EOL;
                    detailhash.put("passwd", passwd);
                }
                else
                {
                    detailhash.put("passwd", passwd);
                }
                if ((!ESAPI.validator().isValidInput("conpasswd", conpasswd, "NewPassword", 20, false)) || (!(passwd).equals(conpasswd)))
                {
                    flag = false;
                    errormsg = errormsg + "Please enter valid Password & Confirm Password." + EOL;
                }
                else
                {
                    detailhash.put("conpasswd", request.getParameter("conpasswd"));
                }

                if (!ESAPI.validator().isValidInput("company_name ", request.getParameter("company_name"), "companyName", 100, false))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Agent Organisation Name." + EOL;
                    detailhash.put("company_name", request.getParameter("company_name"));
                }
                else
                {
                    detailhash.put("company_name", request.getParameter("company_name"));
                }

                if (!ESAPI.validator().isValidInput("contact_persons",request.getParameter("contact_persons"),"contactName", 100, false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Main Contact." + EOL;
                    detailhash.put("contact_persons", contact_persons);
                }
               else
                {
                    detailhash.put("contact_persons", contact_persons);
                }
                if (!ESAPI.validator().isValidInput("contact_emails",request.getParameter("contact_emails"),"Email", 100, false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Main Contact Email. "+ EOL;
                    detailhash.put("contact_emails", contact_emails);
                }
               else
                {
                    detailhash.put("contact_emails", contact_emails);
                }
                detailhash.put("logoName", logoName);
                detailhash.put("country", country);

                if (!ESAPI.validator().isValidInput("sitename", request.getParameter("sitename"), "URL", 100, false) || functions.hasHTMLTags(request.getParameter("sitename")))
                {
                    flag = false;
                    errormsg = errormsg + "Invalid Site URL. " + EOL;
                    detailhash.put("sitename", sitename);
                }
                else
                {
                    detailhash.put("sitename", sitename);
                }

                detailhash.put("telno",telno);
                if (!ESAPI.validator().isValidInput("telno",(String) telno,"SignupPhone",20,false) )
                {
                    Log.debug("PLS enter valid phone number ");
                    errormsg = errormsg + "Invalid Support Number."+EOL;
                    flag = false;
                    detailhash.put("telno", (String) telno);

                }
                else
                {
                    detailhash.put("telno", (String) telno);
                }

                if (!ESAPI.validator().isValidInput("supporturl", request.getParameter("supporturl"),"URL", 255, false) || functions.hasHTMLTags(request.getParameter("supporturl")))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid Support URL. " + EOL;
                    detailhash.put("supporturl", supporturl);
                }
                else
                {
                    detailhash.put("supporturl", supporturl);
                }

                if (!ESAPI.validator().isValidInput("notifyemail", request.getParameter("notifyemail"),"Email", 100, false))
                {
                    flag= false;
                    errormsg= errormsg + "Invalid  Notify Email Id." + EOL;
                    detailhash.put("notifyemail", notifyemail);
                }
                else
                {
                    detailhash.put("notifyemail", notifyemail);
                }

                detailhash.put("maincontact_ccmailid", maincontact_ccmailid);
                detailhash.put("maincontact_phone", maincontact_phone);

                detailhash.put("cbcontact_name", cbcontact_name);
                detailhash.put("cbcontact_mailid", cbcontact_mailid);
                detailhash.put("cbcontact_ccmailid", cbcontact_ccmailid);
                detailhash.put("cbcontact_phone", cbcontact_phone);

                detailhash.put("refundcontact_name", refundcontact_name);
                detailhash.put("refundcontact_mailid", refundcontact_mailid);
                detailhash.put("refundcontact_ccmailid", refundcontact_ccmailid);
                detailhash.put("refundcontact_phone", refundcontact_phone);

                detailhash.put("salescontact_name", salescontact_name);
                detailhash.put("salescontact_mailid", salescontact_mailid);
                detailhash.put("salescontact_ccmailid", salescontact_ccmailid);
                detailhash.put("salescontact_phone", salescontact_phone);

                detailhash.put("billingcontact_name", billingcontact_name);
                detailhash.put("billingcontact_mailid", billingcontact_mailid);
                detailhash.put("billingcontact_ccmailid", billingcontact_ccmailid);
                detailhash.put("billingcontact_phone", billingcontact_phone);

                detailhash.put("fraudcontact_name", fraudcontact_name);
                detailhash.put("fraudcontact_mailid", fraudcontact_mailid);
                detailhash.put("fraudcontact_ccmailid", fraudcontact_ccmailid);
                detailhash.put("fraudcontact_phone", fraudcontact_phone);

                detailhash.put("technicalcontact_name", technicalcontact_name);
                detailhash.put("technicalcontact_mailid", technicalcontact_mailid);
                detailhash.put("technicalcontact_ccmailid", technicalcontact_ccmailid);
                detailhash.put("technicalcontact_phone", technicalcontact_phone);

                detailhash.put("emailTemplateLang", emailTemplateLang);
                detailhash.put("isipwhitelisted", isipwhitelisted);

                String pid = request.getParameter("pid");
                String partner_id = request.getParameter("partnerid");
                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                if(Roles.contains("superpartner")){
                    if(!functions.isValueNull(request.getParameter("pid")))
                    {
                        errormsg = errormsg + "Please enter Valid Partner Id."+EOL;
                        flag = false;
                        detailhash.put("partnerid", request.getParameter("pid"));
                    }
                    else
                    {
                        if (!pid.equals(partner_id))
                        {
                            if (!partner.isPartnerSuperpartnerMapped(request.getParameter("pid"), String.valueOf(session.getAttribute("merchantid"))))
                            {
                                errormsg = errormsg + "Please enter Valid Partner Id." + EOL;
                                flag = false;
                                detailhash.put("partnerid", request.getParameter("pid"));
                            }
                            else
                            {
                                detailhash.put("partnerid", request.getParameter("pid"));

                                detailhash.put("pidname", partner.getNameofPartner(request.getParameter("pid")));
                            }
                        }
                        else{
                            detailhash.put("partnerid", request.getParameter("pid"));
                            detailhash.put("pidname", partner.getNameofPartner(request.getParameter("pid")));
                        }
                    }
                }
                else
                {
                    detailhash.put("partnerid", String.valueOf(session.getAttribute("merchantid")));

                    detailhash.put("pidname", partner.getNameofPartner(String.valueOf(session.getAttribute("merchantid"))));
                }

                detailhash.put("actionExecutorId",actionExecutorId);

                detailhash.put("actionExecutorName",actionExecutorName);

                if(isAgentExist(request.getParameter("company_name")))
                {
                    flag = false;
                    errormsg = errormsg + "Agent Organisation Name already Exists."+EOL;
                }

                errormsg=errormsg+ validateMandatoryParameters(request);
                if(!errormsg.equals(""))
                {
                    flag = false;
                }
                errormsg = errormsg + validateOptionalParameters(request);
                if(!errormsg.equals(""))
                {
                    flag = false;
                }

                if (flag== true)
                {
                    Log.debug("Valid User data..");
                    if (isAgentLoginNameExist((String)detailhash.get("login")))
                    {
                        Log.debug("Redirect to NEWLogin");
                        redirectpage = "/newagentloginname.jsp?ctoken="+user.getCSRFToken();
                        session.setAttribute("newmember",detailhash);
                        request.setAttribute("username",(String)detailhash.get("login"));
                    }
                    else
                    {
                        request.setAttribute("role","agent");
                        addAgent(detailhash);
                        request.setAttribute("username",(String)detailhash.get("login"));
                        successMsg = (String) detailhash.get("login") + " Registration Successful";
                        request.setAttribute("success", successMsg);
                        redirectpage = "/agentInterface.jsp?ctoken=";
                    }
                }
                else
                {
                    Log.debug("Enter valid data..");
                    redirectpage="/agentsignup.jsp?MES="+msg+"&ctoken="+user.getCSRFToken();
                    request.setAttribute("details",detailhash);
                    request.setAttribute("error",errormsg);
                }
                RequestDispatcher rd = request.getRequestDispatcher(redirectpage + user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (Exception e)
            {
                if(e.getMessage().contains("Duplicate"))
                {
                    Log.debug("redirect to NEWLOGIN");
                    redirectpage = "/newagentloginname.jsp?ctoken="+user.getCSRFToken();
                    session.setAttribute("newmember", detailhash);
                    request.setAttribute("username", (String) detailhash.get("login"));
                }
                else
                {
                    Log.error("Leaving merchants (step 1) throwing exception: ", e);
                    out.println(Functions.ShowMessage("Error", "Internal System Error while signing New Agent."));
                }
            }
            Log.debug("RedirectPage  "+redirectpage);
        }
        else if(((String) request.getParameter("step")).equals("3"))
        {
            Log.info("inside step 3");
            Hashtable detailhash= (Hashtable)session.getAttribute("newmember");
            try
            {
                if (isAgentLoginNameExist(request.getParameter("username")))
                {
                    request.setAttribute("username",(String)request.getParameter("username"));
                    errormsg= errormsg+ "Please enter valid Username";
                    request.setAttribute("error",errormsg);
                    redirectpage = "/newagentloginname.jsp?ctoken="+user.getCSRFToken();
                }
                else
                {
                    Log.debug("Change Username");
                    Log.debug(request.getParameter("username"));
                    detailhash.put("login",(String)request.getParameter("username"));
                    request.setAttribute("role","agent");
                    addAgent(detailhash);
                    request.setAttribute("username",(String)detailhash.get("login"));
                    successMsg = (String) detailhash.get("login") + " Registration Successful";
                    request.setAttribute("success",successMsg );
                    redirectpage = "/agentInterface.jsp?ctoken=";
                }
                RequestDispatcher rd = request.getRequestDispatcher(redirectpage + user.getCSRFToken());
                rd.forward(request, response);
            }
            catch (SystemError se)
            {
                if (se.getMessage().contains("Duplicate"))
                {
                    Log.debug("Redirect to NEWLogin");
                    errormsg= errormsg+ "Please enter valid Username";
                    request.setAttribute("error",errormsg);
                    redirectpage= "/newagentloginname.jsp?ctoken="+user.getCSRFToken();
                    RequestDispatcher rd = request.getRequestDispatcher(redirectpage);
                    rd.forward(request, response);
                }
                else
                {
                    Log.error("Leaving Merchants (step 3) throwing system error: ", se);
                    out.println(Functions.ShowMessage("Error", "Username is not found"));
                }
            }
            catch (Exception e)
            {
                Log.error("Leaving Merchants (step 3) throwing exception: ", e);
                out.println(Functions.ShowMessage("Error", "Username is not found"));
            }
        }
    }

    public void addAgent(Hashtable details)throws SystemError
    {
        Transaction transaction = new Transaction();
        AgentAuthenticate mem;
        HashMap mailvalue= new HashMap();

        User user= null;
        try
        {
            user = ESAPI.authenticator().createUser((String) details.get("login"), (String) details.get("passwd"), "agent");
            mem =addAgent_new(user.getAccountId(), details);
        }
        catch (AuthenticationException e)
        {
            Log.error("Add user throwing Authentication Exception ", e);

            if (e instanceof AuthenticationAccountsException)
            {
                String message= ((AuthenticationAccountsException)e).getLogMessage();
                if (message.contains("Duplicate"))
                {
                    throw  new SystemError("System Error "+message);
                }
            }

            try
            {
                Agent.DeleteBoth((String )details.get("login"));
            }
            catch (Exception e1)
            {
                Log.error("Exception while deletion of Detals:::", e1);
            }
            throw new SystemError("Error: " + e.getMessage());
        }
        mailvalue.put(MailPlaceHolder.NAME,details.get("contact_persons").toString());
        mailvalue.put(MailPlaceHolder.USERNAME,details.get("login").toString());
        mailvalue.put(MailPlaceHolder.TOID,String.valueOf(mem.agentid).toString());
        mailvalue.put(MailPlaceHolder.CONTECTEMAIL,details.get("contact_emails").toString());
        mailvalue.put(MailPlaceHolder.PARTNERID,details.get("partnerid").toString());

        Log.debug("send mail to new methods");
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        asynchronousMailService.sendMerchantSignup(MailEventEnum.AGENT_REGISTRATION,mailvalue);
    }

    public boolean isAgentLoginNameExist(String login)throws SystemError
    {
        Connection conn= null;
        PreparedStatement pstmt= null;
        ResultSet rs= null;

        try
        {
            String ROLE="agent";
            conn = Database.getConnection();
            Log.debug("check isAgentLoginNameExist method");
            String selquery = "select accountid from `user` where login = ?  and roles=?";
            pstmt=conn.prepareStatement(selquery);
            pstmt.setString(1,login);
            pstmt.setString(2,ROLE);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            Log.error(" SystemError in isAgentLoginNameExist method: ",se);
            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Exception in isAgentLoginNameExist method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }

    public boolean isAgentExist(String agentName) throws SystemError
    {
        Connection conn= null;
        PreparedStatement pstmt= null;
        ResultSet rs= null;

        try
        {
            conn=Database.getConnection();
            String sqlquery = "select agentid from agents where agentName=?";
            pstmt=conn.prepareStatement(sqlquery);
            pstmt.setString(1, agentName);
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                return true;
            }
        }
        catch (SystemError se)
        {
            Log.error(" SystemError in isAgentExist method: ",se);

            throw new SystemError("Error: " + se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Exception in isAgentExist method: ", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return false;
    }


    public AgentAuthenticate addAgent_new(long accid,Hashtable details) throws SystemError
    {
        Connection conn= null;

        ResultSet rs= null;
        PreparedStatement pstmt= null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        conn=Database.getConnection();

        PartnerDAO partnerDao = new PartnerDAO();

        StringBuffer sb = new StringBuffer("insert into agents " +
                "(accid,login,clkey," +
                "agentName,contact_emails,country," +
                "telno,contact_persons,logoName,siteurl,supporturl,notifyemail,maincontact_ccmailid,maincontact_phone,cbcontact_name,cbcontact_mailid,cbcontact_ccmailid,cbcontact_phone," +
                "refundcontact_name,refundcontact_mailid,refundcontact_ccmailid,refundcontact_phone,salescontact_name,salesemail,salescontact_ccmailid,salescontact_phone," +
                "fraudcontact_name,fraudcontact_mailid,fraudcontact_ccmailid,fraudcontact_phone,technicalcontact_name,technicalcontact_mailid,technicalcontact_ccmailid,technicalcontact_phone," +
                "billingcontact_ccmailid,billingcontact_phone,billingcontact_name,billingemail,superadminid,isIpWhitelisted,dtstamp,partnerId,actionExecutorId,actionExecutorName,emailTemplateLang) values (");

        sb.append("" + ESAPI.encoder().encodeForSQL(me,String .valueOf(accid))+"");
        sb.append(",'" + (String) details.get("login") + "'");
        sb.append(",'" + Merchants.generateKey() + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("company_name")) + "'");
        sb.append(",'" + (String) details.get("contact_emails")+ "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("country")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("telno")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("contact_persons")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("logoName")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("sitename")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("supporturl")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("notifyemail")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("maincontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("maincontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("cbcontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("cbcontact_mailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("cbcontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("cbcontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("refundcontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("refundcontact_mailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("refundcontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("refundcontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("salescontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("salescontact_mailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("salescontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("salescontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("fraudcontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("fraudcontact_mailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("fraudcontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("fraudcontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("technicalcontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("technicalcontact_mailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("technicalcontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("technicalcontact_phone")) + "'");

        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("billingcontact_ccmailid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("billingcontact_phone")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("billingcontact_name")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("billingcontact_mailid")) + "'");

        sb.append(",'" + partnerDao.getSuperAdminId((String)details.get("partnerid")) + "'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("isipwhitelisted")) + "'");
        sb.append(",unix_timestamp(now())");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me, (String) details.get("partnerid")) + "'");
        sb.append(",'"+ details.get("actionExecutorId")+ "'");
        sb.append(",'" + details.get("actionExecutorName")+"'");
        sb.append(",'" + ESAPI.encoder().encodeForSQL(me,(String) details.get("emailTemplateLang")) + "')");

        Log.error("Add NEWAGENT");
        Log.error(sb.toString());
        AgentAuthenticate mem = new AgentAuthenticate();

        try
        {
            Database.executeUpdate(sb.toString(), conn);
            String query="select agentid from agents where login=?";
            pstmt= conn.prepareStatement(query);
            pstmt.setString(1, details.get("login").toString());
            rs=pstmt.executeQuery();
            if (rs.next())
            {
                mem.agentid = rs.getInt("agentid");
                mem.telno = (String) details.get("telno");
                mem.contactemails = (String) details.get("contact_emails");
                mem.isAgentInterface = false;
            }
        }
        catch (SystemError se)
        {
            Log.error("System error",se);
            throw new SystemError(se.getMessage());
        }
        catch (Exception e)
        {
            Log.error("Error!", e);
            throw new SystemError("Error: " + e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return mem;
    }

    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.USERNAME);
        inputFieldsListOptional.add(InputFields.COUNTRY);
        inputFieldsListOptional.add(InputFields.LOGONAME);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    Log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }

    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.MAINCONTACT_CC);
        inputFieldsListOptional.add(InputFields.MAINCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.CBCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.CBCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.CBCONTACT_CC);
        inputFieldsListOptional.add(InputFields.CBCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_CC);
        inputFieldsListOptional.add(InputFields.REFUNDCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_CC);
        inputFieldsListOptional.add(InputFields.SALESCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_CC);
        inputFieldsListOptional.add(InputFields.FRAUDCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_CC);
        inputFieldsListOptional.add(InputFields.BILLINGCONTACT_PHONE);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_NAME);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_MAIL);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_CC);
        inputFieldsListOptional.add(InputFields.TECHCONTACT_PHONE);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    Log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
