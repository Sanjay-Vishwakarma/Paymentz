import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public class AdminDoProofReceived extends HttpServlet
{

    private static Logger logger = new Logger(AdminDoProofReceived.class.getName());

    String name = null;
    boolean captured = false;

    Database db = null;
    Connection conn = null;
    ResultSet rs = null;
    PreparedStatement p1 = null;
    PreparedStatement p2 = null;
    PreparedStatement p3 = null;
    String query = null;
    int count = 1;

    Enumeration enu = null;

    Hashtable reversalhash = null;


    float val = 0;
    java.util.Date dt = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Enterig in AdminDoProofReceived ");
        String icicitransid = null;
        String subject = null;
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        if (!Admin.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        db = new Database();
        enu = req.getParameterNames();

        captured = false;

        reversalhash = new Hashtable();
        String contact_emails = null;


        count = 1;
        try
        {
            //out.println("<br><br>"+name +" : "+ value +"<br>");
            //(TO_DAYS(now()) - TO_DAYS(timestamp))
             try
             {
                validateMandatoryParameter(req);
             }
            catch(ValidationException e)
            {
                logger.error("Invalid Input ",e);
                return;
            }
            icicitransid = req.getParameter("icicitransid");



            if (!icicitransid.trim().equals(""))
            {

                conn = db.getConnection();
                query = "select T.ccnum,T.expdate,T.toid,T.orderdescription,T.emailaddr,T.ipaddress,T.description,T.amount,T.name,M.contact_emails,M.company_name,from_unixtime(T.dtstamp+950400,'%Y %D %M') as date,M.currency,T.templateamount,T.templatecurrency,T.accountid  from transaction_icicicredit as T,members as  M where T.icicitransid=? and T.status='proofrequired' and M.memberid=T.toid ";

                p1=conn.prepareStatement(query);
                p1.setString(1,icicitransid);
                rs = p1.executeQuery();

                if (rs.next())
                {
                    contact_emails = rs.getString("contact_emails");
                    String emailaddr = rs.getString("emailaddr");
                    String ipaddress = rs.getString("ipaddress");

                    String name = rs.getString("name");
                    String toId = rs.getString("toid");
                    String ccnum =  rs.getString("ccnum");

                    String expdate = rs.getString("expdate");
                    String lastDate = rs.getString("date");
                    String companyName = rs.getString("company_name");
                    String description = rs.getString("description");
                    String orderDescription = rs.getString("orderdescription");

                    String amount = rs.getString("amount");
                    String templateAmount = rs.getString("templateamount");
                    String templateCurrency = rs.getString("templatecurrency");
                    String accountId = rs.getString("accountid");
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    String displayname = account.getDisplayName();
                    String currency = account.getCurrency();
                    String company = ApplicationProperties.getProperty("COMPANY");
                    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
                    subject = "Order: " + description + "- High Risk Transaction - Authorisation received";
                    ServletContext application = getServletContext();
                    String doProofReceivedMailBody = (String) application.getAttribute("DOPROOFRECEIVED");
                    logger.info("Proof Received Mail Body : " + doProofReceivedMailBody);

                    Hashtable taghash = new Hashtable();
                    taghash.put("LASTDATE", lastDate);
                    taghash.put("TOID", toId);
                    taghash.put("COMPANY_NAME", companyName);
                    taghash.put("DESCRIPTION", description);
                    taghash.put("ORDER_DESCRIPTION", orderDescription);
                    taghash.put("CURRENCY", currency);
                    taghash.put("AMOUNT", amount);
                    if(name !=null && !name.equals(""))
                    {
                         taghash.put("NAME",  name);
                    }

                    taghash.put("TRACKINGID", icicitransid);
                    taghash.put("CUST_EMAIL_ADDR", emailaddr);
                    logger.debug("IP ADDR::"+ipaddress);
                    //taghash.put("IP_ADDR", ipaddress);
                    taghash.put("DISPLAY_NAME", displayname);
                    taghash.put("COMPANY", company);
                    taghash.put("SUPPORT_URL", supportUrl);
                    logger.debug(taghash);
                    if (templateCurrency != null && !currency.equals(templateCurrency))
                        taghash.put("TMPL_TRANSACTION", "(approximately " + templateCurrency + " " + templateAmount + " )");
                    doProofReceivedMailBody = Functions.replaceTag(doProofReceivedMailBody, taghash);
                    query = "update transaction_icicicredit set status='authsuccessful' where icicitransid=? and status='proofrequired'";


                    p2=conn.prepareStatement(query);
                    p2.setString(1,icicitransid);
                    int result = p2.executeUpdate();
                    logger.debug("No of Rows updated : " + result + "<br>");

                    if (result != 1)
                    {

                        sErrorMessage.append("Failed for proof received. No rows Updated.");

                    }

                    // Start : Added for Action and Status Entry in Action History table
                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(icicitransid,amount,ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL,ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL);
                    entry.closeConnection();
                    // End : Added for Action and Status Entry in Action History table


                    query = "insert into proof_received_data (icicitransid,cardno,name,expdate,ipaddress,email) values (?,?,?,?,?,?)";
                    p3=conn.prepareStatement(query);
                    p3.setString(1,icicitransid);
                    p3.setString(2,ccnum);
                    p3.setString(3,name);

                    p3.setString(4,expdate);
                    p3.setString(5,ipaddress);
                    p3.setString(6,emailaddr);
                    result = p3.executeUpdate();
                    logger.debug("Proof Received Mail Body : " + doProofReceivedMailBody);
                    String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                    //String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
                    //Mail.sendHtmlMail(contact_emails, fromAddress, null, adminEmail, subject, doProofReceivedMailBody);

                    sSuccessMessage.append("Transaction Set to Authsuccessful.");
                    ccnum=null;

                    expdate=null;
                }//if rs ends
                else
                {
                    sErrorMessage.append("Transation not available.");

                }


            }
        }
        catch (SystemError se)
        {
            logger.error("System Error::::",se);

            sErrorMessage.append("Internal Error While Proof Received.");
        }
        catch (Exception e)
        {
            logger.error("Error while reversal :", e);
            sErrorMessage.append("Internal Error While Proof Received.");

        }//try catch ends
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closePreparedStatement(p3);
            Database.closeConnection(conn);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        logger.debug("forwarding to member preference");
        RequestDispatcher rd = req.getRequestDispatcher("/servlet/AdminProofrequiredList?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }//post ends

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ICICITRANSEID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}