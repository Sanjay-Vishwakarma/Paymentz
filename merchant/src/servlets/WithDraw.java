import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import javacryption.aes.AesCtr;
import org.owasp.esapi.ESAPI;
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
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Hashtable;

public class WithDraw extends HttpServlet
{

    private static Logger log = new Logger(WithDraw.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Merchants merchants = new Merchants();
        HttpSession session = req.getSession();
        ServletContext ctx = getServletContext();

        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        log.debug("CSRF check successful ");
        String merchantid = (String) session.getAttribute("merchantid");
        String transid = null;

        ServletContext application = getServletContext();
        String transpasswd=null;
        log.debug("password      "+req.getParameter("password"));
        if(req.getParameter("password")!=null )
        {

            transpasswd = AesCtr.decrypt(req.getParameter("password"), req.getParameter("ctoken"), 256);
            req.setAttribute("transpasswd",transpasswd);


        }
        log.debug("password 11111111     "+transpasswd);

        String ermsg="";
        String desc = Functions.checkStringNull(req.getParameter("desc"));
        try
        {
            transpasswd = ESAPI.validator().getValidInput("transpasswd",transpasswd,"Password",30,false);
        }
        catch(ValidationException e)
        {
          log.error("Invalid Withdrawal password",e);

        }
        String billdesc = null;
        String memberdesc = null;

        String withdrawamt = Functions.checkStringNull(req.getParameter("withdrawamt"));
        String msg = "";
        String type = "T";

        TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();

        if (!((String) session.getAttribute("activation")).equals("Y"))
        {
            out.println(Functions.NewShowConfirmation1("Sorry", "You Cannot withdraw money as Your account has been put to test Mode"));
            return;
        }


        log.debug("inside withdraw");

        try
        {

            Hashtable merchantDetails = merchants.getMemberDetails(merchantid);
            String charges = (String) merchantDetails.get("withdrawalcharge");
            String accountId = (String) merchantDetails.get("accountid");
            GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
            String currency = account.getCurrency();
            String charges_toid = String.valueOf(account.getChargesAccount());
            String tax_toid = String.valueOf(account.getTaxAccount());

            if (!merchants.isTransPassword(transpasswd, merchantid))
            {
                type = "invalid";
                req.setAttribute("type", type);
            }
            else if (!transactionentry.isTransactionLock("" + merchantid))
            {
                ctx.log("Entering block for making withdrawal entries : ");
                transactionentry.setTransactionLock(merchantid);
                BigDecimal taxPercentage = new BigDecimal((String) merchantDetails.get("taxper")).multiply(new BigDecimal("0.01"));
                BigDecimal calculatedTax = transactionentry.calculateTax(charges, taxPercentage.toString());
                ctx.log("values from Servlet : " + calculatedTax + " " + taxPercentage + " " + currency + " ");
                BigDecimal withdrawablebalance = transactionentry.getWithdrawBalance(charges, calculatedTax.toString());
                if (withdrawablebalance.compareTo(new BigDecimal(withdrawamt)) != -1)
                {
                    transid = transactionentry.withDraw(merchantid, withdrawamt, desc, charges, charges_toid, calculatedTax.toString(), taxPercentage.toString(), tax_toid);

                    String contactPersons = merchants.getColumn("contact_persons", merchantid);
                    desc = "Make Cheque/DD of amount : " + currency + " " + withdrawamt + " <BR> Memberid : " + merchantid + " <BR> Party Name : " + merchants.getCompany(merchantid) + "<BR> Contact Person : " + contactPersons + " <BR> Address : " + ((Member) session.getAttribute("memberObj")).address;

                    transactionentry.insertFinanceActivities(transid, desc);

                    billdesc = "Transid : " + transid + "\r\n\r\n" + "Make Cheque/DD of amount: " + currency + " " + withdrawamt + "\r\n Memberid : " + merchantid + "\r\n Party Name : " + merchants.getCompany(merchantid) + "\r\n Address : " + ((Member) session.getAttribute("memberObj")).address + "\r\n\r\n Tel no : " + ((Member) session.getAttribute("memberObj")).telno;

                    memberdesc = "Your request for Cheque/DD of :" + currency + " " + withdrawamt + " is Sucessfully completed";

                    memberdesc = memberdesc + "\r\n Amount : " + currency + " " + withdrawamt;
                    memberdesc = memberdesc + "\r\n Party Name : " + merchants.getCompany(merchantid);
                    memberdesc = memberdesc + "\r\n Address : " + ((Member) session.getAttribute("memberObj")).address;

                    req.setAttribute("withdrawamt", withdrawamt);
                    req.setAttribute("desc", desc);

                    type = "done";
                    req.setAttribute("type", type);
                    req.setAttribute("transid", transid);
                    String supportFromAddress = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
                    log.debug("calling SendMAil for - finance" + (String) application.getAttribute("BILLING_EMAIL"));
                    Mail.sendmail((String) application.getAttribute("BILLING_EMAIL"), supportFromAddress, "Finance Activity", billdesc);
                    log.debug("called SendMAil for - finance");

                    log.debug("calling SendMAil for - member");
                    Mail.sendmail(((Member) session.getAttribute("memberObj")).contactemails, supportFromAddress, "Withdrawal Request Intimation for " + currency + " " + withdrawamt, memberdesc);
                    log.debug("called SendMAil for - member");
                }
                else
                {
                    type = "nomoney";
                    req.setAttribute("type", type);
                }
                transactionentry.deleteTransactionLock(merchantid);
            }
            else
            {
                type = "simul";
                req.setAttribute("type", type);
            }

            RequestDispatcher rd = req.getRequestDispatcher("/withdrawn.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError se)
        {
            StringWriter sw = new StringWriter();
            log.error("SystemError :::::::::::", se);
            out.println(Functions.NewShowConfirmation1("Error", "Internal Error while Withdraw"));
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            log.error("SystemError :::::::::::",e);
            out.println(Functions.NewShowConfirmation1("Error", sw.toString()));
        }
        out.println("</body></html>");

    }
}
