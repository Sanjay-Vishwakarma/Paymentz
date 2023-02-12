import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.logicboxes.validation.Validation;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.*;

import org.owasp.esapi.errors.ValidationException;
import org.owasp.esapi.ESAPI;


public class VBVWaitServlet extends TcServlet
{
    static Logger logger = new Logger(VBVWaitServlet.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.Servlet");
    final static String POSTURL = RB.getString("POSTURL");
    final static String DETAILSURL = RB.getString("DETAILSURL");
    final static String ICICIEMAIL = RB.getString("NOPROOFREQUIRED_EMAIL");
    final static String MANAGEMENT_NOTIFY_EMAIL = RB.getString("MANAGEMENT_NOTIFY_EMAIL");
    final static String PROXYHOST = RB.getString("PROXYHOST");
    final static String PROXYPORT = RB.getString("PROXYPORT");
    final static String PROXYSCHEME = RB.getString("PROXYSCHEME");

    final static ResourceBundle RB1 = LoadProperties.getProperty("com.directi.pg.ICICI");

    static Vector unboilchar = new Vector();
    static Vector blockedemail, blockeddomain, blockedcountry;
    static String defaultchargepercent = "500";
    static String INR_defaulttaxpercent = "1224";
    static String USD_defaulttaxpercent = "1224";

    static
    {
        logger.debug("enter inside VBVWaitServlet class static block");
        unboilchar.add("a");
        unboilchar.add("b");
        unboilchar.add("c");
        unboilchar.add("d");
        unboilchar.add("e");
        unboilchar.add("f");
        unboilchar.add("g");
        unboilchar.add("h");
        unboilchar.add("i");
        unboilchar.add("j");
        unboilchar.add("k");
        unboilchar.add("l");
        unboilchar.add("m");
        unboilchar.add("n");
        unboilchar.add("o");
        unboilchar.add("p");
        unboilchar.add("q");
        unboilchar.add("r");
        unboilchar.add("s");
        unboilchar.add("t");
        unboilchar.add("u");
        unboilchar.add("v");
        unboilchar.add("w");
        unboilchar.add("x");
        unboilchar.add("y");
        unboilchar.add("z");
        unboilchar.add("0");
        unboilchar.add("1");
        unboilchar.add("2");
        unboilchar.add("3");
        unboilchar.add("4");
        unboilchar.add("5");
        unboilchar.add("6");
        unboilchar.add("7");
        unboilchar.add("8");
        unboilchar.add("9");
    }

    public VBVWaitServlet()
    {
        super();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        doService(req, res);
    }

    public void doService(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException
    {
        ServletContext application = getServletContext();
        PrintWriter pWriter = res.getWriter();
        Hashtable otherdetails = new Hashtable();
        blockedemail = (Vector) application.getAttribute("BLOCKEDEMAIL");
        blockeddomain = (Vector) application.getAttribute("BLOCKEDDOMAIN");
        blockedcountry = (Vector) application.getAttribute("BLOCKEDCOUNTRY");
        Enumeration nenu = request.getParameterNames();
       // String fromAddress = ApplicationProperties.getProperty("COMPANY_FROM_ADDRESS");
        Transaction transaction = new Transaction();

        while (nenu.hasMoreElements())
        {
            String name = (String) nenu.nextElement();
            if (name.startsWith("TMPL_"))
                otherdetails.put(name, request.getParameter(name));
        }

        otherdetails.put("TEMPLATE", request.getParameter("TEMPLATE"));

        // MemberId
        int memberId = -9999;
        String merchantId = null;
        String displayName = null;
        String chargepercent = null;
        String dbTaxPercent = null;
        String fixamount = null;
        BigDecimal charge = null;
        BigDecimal chargeamount = null;
        BigDecimal taxPercentage = null;
        String templateCurrency = null;
        String templateAmount = null;

        res.setContentType("text/html");
        pWriter.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">");
        String Encoded_TRACKING_ID= ESAPI.encoder().encodeForHTML(request.getParameter("TRACKING_ID"));
        String Encoded_DESCRIPTION= ESAPI.encoder().encodeForHTML(request.getParameter("DESCRIPTION"));
        String Encoded_ORDER_DESCRIPTION= ESAPI.encoder().encodeForHTML(request.getParameter("ORDER_DESCRIPTION"));
        String cardholder = "";
        String ccpan = "";
        String month = "";
        String year = "";
        String ccid = "";
        String country = "";
        String emailaddr = "";
        String countryinrec = "";
        String checksumVal = request.getParameter("checksum");
        String HRCode = "";
        String namecp=null;
        String cccp=null;
        String addrcp=null;
        //parameter which show whether card number and name was copy pasted
        try
        {
        namecp = ESAPI.validator().getValidInput("NAMECP",request.getParameter("NAMECP"),"SafeString",100,false);
        cccp = ESAPI.validator().getValidInput("CCCP",request.getParameter("CCCP"),"SafeString",10,false);
        addrcp = ESAPI.validator().getValidInput("ADDRCP",request.getParameter("ADDRCP"),"SafeString",10,false);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input",e);
        }
        if (namecp != null && cccp != null && addrcp != null)
        {
            if (cccp.equalsIgnoreCase("Y") && namecp.equalsIgnoreCase("Y") && addrcp.equalsIgnoreCase("Y"))
                HRCode = HRCode + "-HRCP";
        }

        //collect all parameters got from VBV

        String STATUS = request.getParameter("status");
        String CAVV = request.getParameter("cavv");
        String ECI = request.getParameter("eci");
        String Xid = request.getParameter("xid");
        String id = request.getParameter("shoppingcontext");
        String PurchaseAmount = request.getParameter("purchaseamount");
        String Currency = request.getParameter("currency");

        if (CAVV == null)
            CAVV = "";
        if (STATUS == null)
            STATUS = "";
        if (ECI == null)
            ECI = "";
        if (Xid == null)
            Xid = "";
        if (PurchaseAmount == null)
            PurchaseAmount = "";
        if (Currency == null)
            Currency = "";


        Hashtable cardDetailHash = null;
        Hashtable MPIDataHash = new Hashtable();
        MPIDataHash.put("eci", ECI);
        MPIDataHash.put("xid", Xid);
        MPIDataHash.put("vbvstatus", STATUS);
        MPIDataHash.put("cavv", CAVV);
       // MPIDataHash.put("shoppingcontext", SHOPPING_CONTEXT);
        MPIDataHash.put("shoppingcontext", id);
        MPIDataHash.put("PurchaseAmount", PurchaseAmount);
        MPIDataHash.put("Currency", Currency);

        logger.debug(" MPIDATA=" + MPIDataHash);
        int aptprompt = 0;
        int memberaptprompt = 0;

        Connection con = null;
        ResultSet rs = null;

        int count = -9999;
        logger.debug("Entering doService");
        PrintWriter pw = null;
        String query = null;
        Hashtable hash = null;
        String type = "";

        boolean authsqlexception = false;
        StringWriter sw = new StringWriter();
        boolean authexception = false;
        boolean captureexception = false;
        boolean proofrequired = false;

        String proofsubject = "";
        String authid = "";
        String authCode = "";
        String authRRN = "";
        //for proofrequired
        String brandname = "";
        String sitename = "";

        boolean repeatIP = false;
        boolean repeatEmail = false;
        boolean repeatmachine = false;
        boolean diffCountry = false;
        boolean transactionfailed = true;
        int ipcode = -9999;
        long tempcode = -9999;
        String machineid = null;
        int ipfraudcount = 0;
        int emailfraudcount = 0;
        int machinefraudcount = 0;
        boolean proofNotRequiredAgain = false;


        String txnamount = request.getParameter("TXN_AMT");
        BigDecimal amount = new BigDecimal(txnamount);
        amount = amount.setScale(2, BigDecimal.ROUND_DOWN); //as amount value in database was round down by mysql while inserting in php page
        txnamount = amount.toString();
        String amountInDb = "";

        String trackingId = request.getParameter("shoppingcontext");
        String strPath = request.getRequestURI();
        strPath = strPath.substring(0, (strPath.lastIndexOf('/') + 1));

        String transactionDescription = "";
        String transactionOrderDescription = "";
        String transactionMessage = "Connection Failure";
        String transactionStatus = "N";
        String mailtransactionStatus = "Failed";
        String captureStatus = null;
        String clkey = "";
        String checksumAlgo = null;
        String version = request.getParameter("TMPL_VERSION");
        if (version == null)
            version = "";
        String notifyEmails = "";
        String hrAlertProof = "";
        String datamismatchproof = "";
        String dtstamp = "";
        String sql = "";
        String company_name = null;
        String currency = null;
        //boolean custremindermail = false;
        boolean hrParameterised = false;

        StringBuffer merchantsubject = new StringBuffer();
        StringBuffer tcsubject = new StringBuffer();
        StringBuffer body = new StringBuffer();
        StringBuffer tcbody = new StringBuffer();
        StringBuffer merchantbody = new StringBuffer();
        StringBuffer servicemerchantbody = new StringBuffer();

        String ipaddress = "N/A";
        String boilname = "";

        AbstractPaymentGateway pg = null;
        String accountId = "";
        try
        {
            con = Database.getConnection();
            count = Integer.parseInt((String) application.getAttribute("noOfClients"));
            count++;
            application.setAttribute("noOfClients", String.valueOf(count));

            sql = "select amount,toid,description,orderdescription,dtstamp,status,httpheader,emailaddr,country,name,boiledname,ccnum,ccid,expdate,templatecurrency,templateamount,accountid from transaction_icicicredit where icicitransid=?";

            PreparedStatement pstmt=con.prepareStatement(sql);
            pstmt.setString(1,trackingId);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                memberId = rs.getInt("toid");
                amountInDb = rs.getString("amount");
                accountId = rs.getString("accountid");

                transactionDescription = rs.getString("description");
                transactionOrderDescription = rs.getString("orderdescription");
                if (transactionOrderDescription == null)
                    transactionOrderDescription = "";

                cardholder = rs.getString("name");
                boilname = rs.getString("boiledname");
                emailaddr = rs.getString("emailaddr");
                ccpan = PzEncryptor.decryptPAN(rs.getString("ccnum"));
                ccid = rs.getString("ccid");
                String expDate =PzEncryptor.decryptExpiryDate(rs.getString("expdate")); //This will return like 08/2003
                logger.debug("expDate");
                month = expDate.substring(0, expDate.indexOf('/'));
                year = expDate.substring(3, expDate.length());
                country = rs.getString("country");
                dtstamp = rs.getString("dtstamp");
                ipaddress = rs.getString("httpheader");
                templateCurrency = rs.getString("templatecurrency");
                templateAmount = rs.getString("templateamount");
                try
                {
                    aptprompt = GatewayAccountService.getGatewayAccount(accountId).getHighRiskAmount(); // get default HR amount for this gateway
                    pg = AbstractPaymentGateway.getGateway(accountId);
                }
                catch (SystemError systemError)
                {
                    application.log(" exception " + Functions.getStackTrace(systemError));
                    otherdetails.put("TRACKING_ID", trackingId);
                    otherdetails.put("DESCRIPTION", transactionDescription);
                    otherdetails.put("ORDER_DESCRIPTION", transactionOrderDescription);
                    otherdetails.put("MESSAGE", systemError.getMessage());

                    pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                    pWriter.flush();
                    return;
                }

                //Prepare a hashtable to authenticate
                cardDetailHash = new Hashtable();
                cardDetailHash.put("ccnum", ccpan);
                cardDetailHash.put("ccid", ccid);
                cardDetailHash.put("year", year);
                cardDetailHash.put("month", month);
                cardDetailHash.put("name", cardholder);

                if (!rs.getString("status").equals("authstarted"))
                {
                    logger.debug("inside if for status!=authstarted");
                    String table = "ERROR!!! We have encountered an internal error while processing your request.<BR><BR>";
                    otherdetails.put("TRACKING_ID", trackingId);
                    otherdetails.put("DESCRIPTION", request.getParameter("DESCRIPTION"));
                    otherdetails.put("ORDER_DESCRIPTION", request.getParameter("ORDER_DESCRIPTION"));
                    otherdetails.put("MESSAGE", table);

                    pWriter.println(Template.getErrorPage(memberId + "", otherdetails));
                    pWriter.flush();
                    return;
                }
                else
                {

//////*******************  risk checking is started *********************************************//

                    if (ipaddress != null)
                    {
                        int pos = ipaddress.indexOf("Client =");
                        int endpos = ipaddress.indexOf(":");
                        ipaddress = ipaddress.substring(pos + 8, endpos);
                        ipaddress = ipaddress.trim();
                    }

                    try
                    {
                        ipcode = InetAddress.getByName(ipaddress).hashCode();
                        tempcode = ipcode - 2147483647;
                        tempcode = tempcode + 2147483647L;  //convert into long value.
                    }
                    catch (UnknownHostException nex)
                    {
                        logger.error("ipaddress is either null or not in properformat.",nex);
                    }

                    //checking for high risk transaction
                    //check whether this is blocked email
                    if (blockedemail != null)
                    {
                        if (blockedemail.contains(emailaddr))
                        {
                            proofrequired = true;
                            HRCode = HRCode + "-BEM";
                            logger.debug("*$ proof require is true because " + emailaddr + " is blocked by admin.");
                        }
                    }

                    //check whether this is blocked Domain

                    String domain = emailaddr.substring(emailaddr.indexOf("@") + 1, emailaddr.length());
                    domain = domain.trim();
                    //    logger.debug("DOMAIN NAME=" + domain);

                    if (blockeddomain != null && !blockeddomain.isEmpty())
                    {
                        for (int i = 0; i < blockeddomain.size(); i++)
                        {
                            if (domain.indexOf((String) blockeddomain.get(i)) > 0) //will chaeck any appender before domain name
                            {
                                logger.debug("block is true for " + domain + " as " + (String) blockeddomain.get(i) + " is blocked.");
                                proofrequired = true;
                                HRCode = HRCode + "-BDOM";
                                logger.debug("*$ proof require is true because " + domain + " is blocked by admin.");
                                break;
                            }
                        }
                        if (blockeddomain.contains(domain))
                        {
                            proofrequired = true;
                            HRCode = HRCode + "-BDOM";
                            logger.debug("*$ proof require is true because " + domain + " is blocked by admin.");
                        }
                    }

                    //check whether this is blocked IP
                    query = "select * from blockedip where startipcode<=? and endipcode>=?";
                    //System.out.println(query);

                    PreparedStatement pstmt2=con.prepareStatement(query);
                    pstmt2.setLong(1,tempcode);
                    pstmt2.setLong(2,tempcode);
                    ResultSet iprs = pstmt2.executeQuery();

                    if (iprs.next())
                    {
                        proofrequired = true;
                        HRCode = HRCode + "-BIP";
                        logger.debug("*$ proof require is true because " + ipaddress + " is blocked by admin.");
                    }

                    //country ckeck start
                    query = "select country from ipmap where startip<=? and endip>=?";


                    try
                    {   PreparedStatement pstmt3=con.prepareStatement(query);
                        pstmt3.setLong(1,tempcode);
                        pstmt3.setLong(2,tempcode);
                        ResultSet rs0 =pstmt3.executeQuery();
                        if (rs0.next())
                        {
                            countryinrec = rs0.getString("country");
                            logger.debug("country entered by customer=" + country + " country got:" + countryinrec);
                            if (!countryinrec.equals(country))
                            {
                                diffCountry = true;

                                //    HRMessage= HRMessage+"-Billing address mismatch\r\n";
                                HRCode = HRCode + "-DCON";
                                logger.debug("**High Risk fraud caught for diff. country");
                            }
                        }
                    }
                    catch (NullPointerException nex)
                    {
                        logger.error("Exception Occure in mapping country ",nex);
                    }
//country check ends
//check whether country is blocked
                    if (blockedcountry != null && !blockedcountry.isEmpty())
                    {
                        if (blockedcountry.contains(countryinrec))
                        {
                            proofrequired = true;
                            HRCode = HRCode + "-BCON";
                            application.log("*$ proof required is true because country " + country + " is blocked by admin.");
                        }
                    }

//ipcheck starts
                    String preboilname = "";
                    query = "select count(*),ccnum,boiledname from transaction_icicicredit where ipcode=? and to_days(now())-to_days(from_unixtime(dtstamp))<=7 group by ccnum,boiledname";


                    PreparedStatement pstmt4=con.prepareStatement(query);
                    pstmt4.setLong(1,tempcode);
                    ResultSet rs1 = pstmt4.executeQuery();
                    while (rs1.next())
                    {
                        //  ipfraudcount += rs1.getInt(1);
                        if (!ccpan.equals(PzEncryptor.decryptPAN(rs1.getString("ccnum"))))
                        {
                            ipfraudcount += 1;
                            preboilname = rs1.getString("boiledname");
                            if (!boilname.equals(preboilname) || ipfraudcount > 2)
                            {
                                repeatIP = true;
                                //HRMessage=HRMessage+"-Different Cards used from the same IP Address with different boiled name\r\n";
                                HRCode = HRCode + "-DIP";
                                logger.debug("**High Risk fraud caught for same ip diff. boiledname");
                                break;
                            }
                        }
                    }
//Ip check ends
//email address check starts

                    query = "select count(*),ccnum,boiledname from transaction_icicicredit where emailaddr=? and to_days(now())-to_days(from_unixtime(dtstamp))<=30 group by ccnum,boiledname";

                    PreparedStatement pst=con.prepareStatement(query);
                    pst.setString(1,emailaddr);
                    ResultSet rs2 = pst.executeQuery();
                    while (rs2.next())
                    {
                        // emailfraudcount += rs2.getInt(1);
                        if (!ccpan.equals(PzEncryptor.decryptPAN(rs2.getString("ccnum"))))
                        {
                            preboilname = rs2.getString("boiledname");
                            emailfraudcount += 1;
                            if (!boilname.equals(preboilname) || emailfraudcount > 2)
                            {
                                repeatEmail = true;
                                //HRMessage=HRMessage+"-Different Cards used from the same Email Address with different boiled name\r\n";
                                HRCode = HRCode + "-DEM";
                                logger.debug("**High Risk fraud caught for same emailaddress diff. boiledname");
                                break;
                            }
                        }
                    }
//Email check ends
//Machine check starts

                    machineid = getCookie(request, res);

                    if (machineid != null)
                    {
                        query = "select count(*),ccnum,boiledname from transaction_icicicredit where mid=? and to_days(now())-to_days(from_unixtime(dtstamp))<=30 group by ccnum,boiledname";
                        PreparedStatement p=con.prepareStatement(query);
                        p.setString(1,machineid);
                        ResultSet rs3 = p.executeQuery();
                        while (rs3.next())
                        {
                            //  machinefraudcount += rs3.getInt(1);
                            if (!ccpan.equals(PzEncryptor.decryptPAN(rs3.getString("ccnum"))))
                            {
                                preboilname = rs3.getString("boiledname");
                                machinefraudcount += 1;
                                if (!boilname.equals(preboilname) || machinefraudcount > 2)
                                {
                                    repeatmachine = true;
                                    //HRMessage=HRMessage+"-Different Cards used from the same Machine with different boiled name\r\n";
                                    HRCode = HRCode + "-DMC";
                                    logger.debug("**High Risk fraud caught for same machine diff. boiledname");
                                    break;
                                }
                            }
                        }
                    }
                    else //if cookie is still not set for this machine
                    {
                        //generate random number and assign it to machine id
                        Random rand = new Random();
                        rand.setSeed((new java.util.Date()).getTime());//set seed to current time.
                        Integer i = new Integer(rand.nextInt());
                        machineid = i.toString();

                        Cookie c = new Cookie("mid", machineid);
                        c.setMaxAge(2678400);
                        res.addCookie(c);
                    }
//Machine check ends
//////*******************  risk checking is done *********************************************//
                }
            }
            else
            {
                String message = "ERROR!!! We have encountered an internal error while processing your request.<BR><BR>";
                otherdetails.put("TRACKING_ID", Encoded_TRACKING_ID);
                otherdetails.put("DESCRIPTION", Encoded_DESCRIPTION);
                otherdetails.put("ORDER_DESCRIPTION", Encoded_ORDER_DESCRIPTION);
                otherdetails.put("MESSAGE", message);

                try
                {
                    logger.debug("Error !!, record not found for tracking id");
                    pWriter.println(Template.getErrorPage(request.getParameter("TOID"), otherdetails));
                    pWriter.flush();
                }
                catch (Exception e)
                {   logger.error("exception ",e);

                }
                return;
            }
        }
        catch (SQLException sqle)
        {   logger.error("SQLException",sqle);
        }
        catch (SystemError s)
        {   logger.error("System Error",s);

        }//try catch ends
        finally
        {
            Database.closeConnection(con);
        }

        StringBuffer queryString = new StringBuffer("");
        Enumeration tmplenu = request.getParameterNames();
        while (tmplenu.hasMoreElements())
        {
            String name = (String) tmplenu.nextElement();
            String value = URLEncoder.encode(request.getParameter(name));
            if (name.startsWith("TMPL_"))
                queryString.append("&").append(name).append("=").append(value);
        }
        try
        {
            String newChecksum = Checksum.generateNewChecksum(trackingId, memberId + "");
            String detailsUrl = PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + DETAILSURL + "?trackingid=" + trackingId + "&id=" + memberId + "&newchecksum=" + newChecksum + "&version=" + version + "&TEMPLATE=" + request.getParameter("TEMPLATE") + "&redirecturl=" + request.getParameter("REDIRECTURL") + "&POSTURL=" + POSTURL + queryString;
            otherdetails.put("CONTINUE", "<br><br>If this page takes longer than 60 seconds, please <a href=" + detailsUrl + ">click  here</a> to confirm your payment.");
            otherdetails.put("DETAILSURL", detailsUrl);
            pWriter.println(Template.getWaitPage(memberId + "", otherdetails));

        }
        catch (SystemError ex)
        {   logger.error("Error in Template",ex);
            pWriter.println("Error in Template " + ex.toString());
        }
        catch (Exception ex)
        {   logger.error("Error in Checksum Generation",ex);
            pWriter.println("Error in Checksum Generation " + ex.toString());
        }
        res.flushBuffer();

        // Quering Members Table to find out whether the member is a service based
        // products based member.

        try
        {
            con = Database.getConnection();
            query = "select hralertproof,datamismatchproof,isservice,notifyemail,aptprompt,company_name,brandname,sitename,chargeper,fixamount,clkey,vbv,hrparameterised,taxper,checksumalgo from members where memberid =? ";

            PreparedStatement p1=con.prepareStatement(query);
            p1.setInt(1,memberId);
            rs =p1.executeQuery();
            String isService = "";

            if (rs.next())
            {
                memberaptprompt = rs.getInt("aptprompt");
                //vbv = rs.getString("vbv");
                notifyEmails = rs.getString("notifyemail");
                company_name = rs.getString("company_name");
                isService = rs.getString("isservice");
                hrAlertProof = rs.getString("hralertproof");
                datamismatchproof = rs.getString("datamismatchproof");
                brandname = rs.getString("brandname");
                sitename = rs.getString("sitename");
                chargepercent = rs.getString("chargeper");
                dbTaxPercent = rs.getString("taxper");
                fixamount = rs.getString("fixamount");
                clkey = rs.getString("clkey");
                checksumAlgo = rs.getString("checksumalgo");
                currency = pg.getCurrency();
                //custremindermail = rs.getBoolean("custremindermail");
                hrParameterised = rs.getBoolean("hrparameterised");

                if (clkey == null || clkey.trim().equals(""))
                    throw new SystemError("Could not load Key");

                //autoredirect=rs.getString("autoredirect");

                if (brandname.trim().equals(""))
                    brandname = company_name;

                if (sitename.trim().equals(""))
                    sitename = company_name;

                if (chargepercent == null)
                    charge = new BigDecimal(defaultchargepercent);
                else
                    charge = new BigDecimal(chargepercent);

                if (dbTaxPercent == null)
                    if ("USD".equals(currency))
                        taxPercentage = new BigDecimal(USD_defaulttaxpercent);
                    else
                        taxPercentage = new BigDecimal(INR_defaulttaxpercent);
                else
                    taxPercentage = new BigDecimal(dbTaxPercent);

                application.log("Tax Percentage: " + taxPercentage.toString());
            }
            else
            {
                throw new SystemError("Query : " + query);
            }


            if (!Checksum.verifyChecksumV2(memberId + "", transactionDescription, new BigDecimal(amountInDb).doubleValue() + "", clkey, checksumVal, checksumAlgo))
            {

                String message = "ERROR!!! checksum mismatched.<BR><BR>";
                otherdetails.put("TRACKING_ID", Encoded_TRACKING_ID);
                otherdetails.put("DESCRIPTION", Encoded_DESCRIPTION);
                otherdetails.put("ORDER_DESCRIPTION", Encoded_ORDER_DESCRIPTION);
                otherdetails.put("MESSAGE", message);

                try
                {
                    application.log("Error !!, checksum mismatch for amount " + txnamount + " in db " + amountInDb);
                    /*
                    Database.executeUpdate("update transaction_icicicredit set status='failed' where icicitransid="+trackingId, con);
                    Database.commit(con);
                    */
                    pWriter.flush();
                    pWriter.println(Template.getErrorPage("" + memberId, otherdetails));
                    pWriter.flush();

                }
                catch (Exception e)
                {   logger.error("Exception ::::",e);
                    application.log("Exception  " + e);
                }
                return;

            }

            txnamount = amountInDb;
            int txnPaise = (new BigDecimal(txnamount)).multiply(new BigDecimal(100.0)).intValue();

            if (isService.equals("Y"))
                type = "sale";
            else
                type = "auth";

            try
            {
                //set mail body before doing auth
                body.append("Member ID: " + memberId + "\r\n");
                body.append("Company Name: " + company_name + "\r\n\r\n");
                body.append("Description: " + transactionDescription + "\r\n");
                body.append("Order Description: " + transactionOrderDescription + "\r\n");
                if (templateCurrency != null && !currency.equals(templateCurrency))
                    body.append("Amount: " + currency + " " + txnamount + " ( approximately " + templateCurrency + " " + templateAmount + " )" + "\r\n");
                else
                    body.append("Amount: " + currency + " " + txnamount + "\r\n");
                body.append("Card Holder : " + cardholder + "\r\n");

                // Process a auth transaction.....for both service based and
                // non-service based merchants.


                merchantId = pg.getMerchantId();
                displayName = pg.getDisplayName();

                //before going for authentication insert all details got from bank in our database

                StringBuffer sb = new StringBuffer("update transaction_icicicredit set");
                sb.append(" vbvstatus=?");
                sb.append(",cavv=?");
                sb.append(",xid=?");
                sb.append(",eci=?");


                sb.append(",icicimerchantid=? where icicitransid=?");


                //pWriter.println("Query : "+sql);
                PreparedStatement p2=con.prepareStatement(sb.toString());
                logger.debug(STATUS+"    "+ECI);
                p2.setString(1,STATUS);
                p2.setString(2,CAVV);
                p2.setString(3,Xid);
                p2.setString(4,ECI);
                p2.setString(5,merchantId);
                p2.setString(6,trackingId);
                p2.executeUpdate();
                Database.commit(con);

                // If ECI = 5 / 2, forward the transaction to payment gateway for authorization.
                //Else ECI = 6/1 , Issuing Bank/cardholder does not participate/enrolled in 3DS. So we won't consider as VBV authentication failure
                //Else if ECI = 0 / 7, do not continue with the transaction and show authentication failure response to the cardholder.
                if (ECI.equals("05") || ECI.equals("02")|| ECI.equals("06")|| ECI.equals("01"))
                {
                    if (ECI.equals("06") || ECI.equals("01"))
                    {
                       // logger.debug("*$ proof require is true for ECI 01/06");
                      //  proofrequired = true;
                        HRCode = HRCode + "-MECI";
                    }
                    hash = pg.processAuthentication(trackingId, txnamount, cardDetailHash, null, null, MPIDataHash, ipaddress);
                    logger.debug("");
                    //logger.debug("AUTHQSIRESPONSEDESC = "+message);
                }
                else
                {
                    hash = new Hashtable();
                    logger.debug("VBV authentication failed");
                    hash.put("authqsiresponsedesc", "VBV Authentication Failed");
                    hash.put("authqsiresponsecode", "4");   //For VBV fail I am putting code 4

                }

                StringBuffer sbquery = new StringBuffer();
                sbquery.append("update transaction_icicicredit set ");

                if (((String) hash.get("authqsiresponsecode")).equals("0"))
                {

                    //if member is in service mode
                    if (type.equals("sale"))
                    {
                        if (repeatEmail || repeatmachine || repeatIP || diffCountry) //if there is any alert
                        {
                            //if coutry is different then put it to proof require mode
                            if (((repeatEmail || repeatmachine || repeatIP) && hrAlertProof.equals("Y")) || (diffCountry && datamismatchproof.equals("Y")))
                            {
                                logger.debug("*$ proof require is true for sale mode because");
                                logger.debug("repeatEmail=" + repeatEmail + " repeatmachine=" + repeatmachine + " repeatIP=" + repeatIP + " and hrAlertProof=" + hrAlertProof);
                                logger.debug("diffCountry=" + diffCountry + " and datamismatchproof=" + datamismatchproof);
                                proofrequired = true;
                            }

                            sbquery.append(" status = 'authsuccessful' ");
                            transactionStatus = "Y";

                        }
                        else
                        {
                            sbquery.append(" status = 'authsuccessful',captureamount=" + txnamount);
                            transactionStatus = "Y";
                        }
                    }
                    else
                    {
                        if (repeatEmail || repeatmachine || repeatIP || diffCountry) //if there is any alert
                        {//if there is any alert
                            if (((repeatEmail || repeatmachine || repeatIP) && hrAlertProof.equals("Y")) || (diffCountry && datamismatchproof.equals("Y")))
                            {
                                logger.debug("*$ proof require is true for auth mode because");
                                logger.debug("repeatEmail=" + repeatEmail + " repeatmachine=" + repeatmachine + " repeatIP=" + repeatIP + " and hrAlertProof=" + hrAlertProof);
                                logger.debug("diffCountry=" + diffCountry + " and datamismatchproof=" + datamismatchproof);

                                proofrequired = true;
                            }
                        }
                        sbquery.append(" status = 'authsuccessful' ");
                        transactionStatus = "Y";
                    }
                }
                else
                {
                    sbquery.append(" status = 'authfailed' ");
                    transactionStatus = "N";
                }

                authid = (String) hash.get("authid");
                authCode = (String) hash.get("authcode");
                authRRN = (String) hash.get("authreceiptno");
                String message = (String) hash.get("authqsiresponsedesc");
                logger.debug("AUTHQSIRESPONSEDESC = "+message);

                if (Functions.parseData(message) != null)
                    transactionMessage = message;

                //Sending mail to inform that this is a reseller transaction who got format error or unauthorised usage error
                if (transactionMessage.indexOf("format error") != -1 || transactionMessage.indexOf("Internal Processing Error") != -1)
                {
                    Mail.sendNotificationMail("Reseller got error while Transacting", "\r\n\r\nProblem occured while authentication for tracking id=" + trackingId + "\r\nDescription:" + transactionDescription + "\r\nAuth code=" + hash.get("authqsiresponsecode") + "\r\nAuth message=" + transactionMessage);
                }

                int mailCount = 0;
                if (Functions.parseData((String) application.getAttribute("MAILCOUNT")) != null)
                    mailCount = Integer.parseInt((String) application.getAttribute("MAILCOUNT"));
                if (mailCount <= 5 && (transactionMessage.indexOf("Error while ") != -1 || transactionMessage.indexOf("Internal Processing Error") != -1) && (hash.get("authqsiresponsecode")).equals("2"))
                {
                    //problem has started so send mail
                    String ids = TimeZone.getTimeZone("GMT+5:30").getID();
                    SimpleTimeZone tz = new SimpleTimeZone(+0 * 00 * 60 * 1000, ids);
                    java.util.GregorianCalendar cal = new java.util.GregorianCalendar(tz);
                    cal.setTime(new java.util.Date());

                    mailCount++;
                    application.setAttribute("MAILCOUNT", mailCount + "");
                    logger.debug("Sending mail to System Admin");
                    Mail.sendAdminMail("Exception while Authentication", "\r\n\r\nException has occured while authentication for tracking id=" + trackingId + "\r\nTime:" + cal.getTime() + "\r\nAuth code=" + hash.get("authqsiresponsecode") + "\r\nAuth message=" + transactionMessage);
                    logger.debug("Mail sent to System Admin");
                }
                if (transactionMessage.indexOf("connection timed out") != -1 && (hash.get("authqsiresponsecode")).equals("2"))
                {
                    //connection timeout problem has started so send mail
                    String ids = TimeZone.getTimeZone("GMT+5:30").getID();
                    SimpleTimeZone tz = new SimpleTimeZone(+0 * 00 * 60 * 1000, ids);
                    java.util.GregorianCalendar cal = new java.util.GregorianCalendar(tz);
                    cal.setTime(new java.util.Date());

                    application.log("Sending mail to System Admin");
                    Mail.sendmail(MANAGEMENT_NOTIFY_EMAIL, "", "Connection Timed out while Authentication", "\r\n\r\nConnection Timed out while authentication \r\n\r\n Tracking id=" + trackingId + "\r\nTime= " + cal.getTime() + "\r\nAuth code=" + hash.get("authqsiresponsecode") + "\r\nAuth message=" + transactionMessage);
                    application.log("Mail sent to System Admin");
                }

                logger.debug("Hash : " + hash);

                Enumeration e = hash.keys();

                while (e.hasMoreElements())
                {
                    String key = (String) e.nextElement();
                    sbquery.append(" , " + key + " = '" + (String) hash.get(key) + "' ");
                }

                sbquery.append(",ipcode=" + tempcode + ",ipaddress='" + ipaddress + "',mid=" + machineid);

                //setting status to proofrequired
                if (transactionStatus.equals("Y"))
                {
                    if (proofrequired)
                    {
                        String strSql = "select * from proof_received_data where cardno=? and email=?";
                        PreparedStatement p4=con.prepareStatement(strSql);
                        p4.setString(1, Functions.encryptString(ccpan));
                        p4.setString(2,emailaddr);
                        ResultSet rsRisk = p4.executeQuery();
                        if (rsRisk.next())
                        {
                            String name = rsRisk.getString("name");

                            if (boilname.equals(getBoiledName(name)))
                            {
                                proofrequired = false;
                                proofNotRequiredAgain = true;
                            }
                        }
                    }

                    boolean checkHighrisk = true;
                    if (hrParameterised) // check for parameterised highrisk value only if merchant is allowed to do so.
                    {
                        //by setting this variable to false,merchant can avoid transaction considered as high risk in any circumstance
                        if (Functions.checkStringNull(request.getParameter("TMPL_HIGHRISK")) != null)
                            checkHighrisk = Boolean.valueOf(request.getParameter("TMPL_HIGHRISK")).booleanValue();
                    }

                    if (checkHighrisk)
                    {
                        if (memberaptprompt == 0)
                        {
                            if (txnPaise > aptprompt)
                            {
                                logger.debug("*$ proof require is true because transaction amount " + txnPaise + " is more than " + currency + " " + aptprompt);
                                HRCode = HRCode + "-HAMT";
                                proofrequired = true;
                                proofNotRequiredAgain = false;
                            }
                        }
                        else if (txnPaise > memberaptprompt)
                        {
                            logger.debug("*$ proof require is true for auth mode because transaction amount " + txnPaise + " is more than members aptprompt=" + memberaptprompt);
                            HRCode = HRCode + "-HAMT";
                            proofrequired = true;
                            proofNotRequiredAgain = false;
                        }
                    }
                    else
                    {
                        proofrequired = false;
                        logger.debug("*$ proof require is false because TMPL_HIGHRISK=" + request.getParameter("TMPL_HIGHRISK"));
                    }
                }

                logger.debug("HRCode=" + HRCode);

                if (!HRCode.equals(""))
                    HRCode = HRCode.substring(1);

                //also update charge value as we decided to charge merchant with the percentage that existed at the time of authorisation
                //not at the time of settlement.
                sbquery.append(",hrcode='" + HRCode + "',chargeper='" + charge.intValue() + "',fixamount=" + fixamount + ",taxper = " + taxPercentage.intValue());
                sbquery.append(" where icicitransid = " + trackingId);

                //pWriter.println("Query : "+sbquery.toString());

                int result = Database.executeUpdate(sbquery.toString(), con);

                if (result != 1)
                {
                    Database.rollback(con);
                    logger.debug("Leaving do service with error in result, result is " + result + " and not 1");
                    Mail.sendAdminMail("Exception while Updating status", "\r\n\r\nException has occured while updating status for tracking id=" + trackingId + "\r\nAuth code=" + hash.get("authqsiresponsecode") + "\r\nAuth message=" + transactionMessage);
                    throw new SystemError("Error in Query, result not 1 result is " + result + "  Query : " + sb.toString());
                }
                Database.commit(con);



                if (transactionStatus.equals("Y"))
                {
                    transactionfailed = false;
                    if (proofrequired)
                        mailtransactionStatus = "Successful - Proof Required";
                    else
                        mailtransactionStatus = "Successful";
                }
                else
                {
                    mailtransactionStatus = "failed";
                    transactionfailed = true;
                }
                body.append("Status: " + mailtransactionStatus + "\r\n");
                body.append("Auth Message: " + transactionMessage + "\r\n");
            }
            catch (Exception nex)
            {
                pw = new PrintWriter(sw);
                nex.printStackTrace(pw);

                authexception = true;
                tcsubject.append("Exception Occured while Auth");
                tcbody.append("Exception Occured while Auth " + sw.toString() + "\r\n\r\n");

                String exp = nex.getMessage();

                int ind = -1;

                if (exp != null)
                    ind = exp.indexOf("#1234#");

                if (exp != null && ind != -1)
                {
                    exp = exp.substring(ind + 5);
                    query = "update transaction_icicicredit set status='authfailed',authqsiresponsedesc='" + exp + "' where icicitransid = " + trackingId;
                    Database.executeUpdate(query, con);

                     // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(String.valueOf(trackingId),String.valueOf(amount.doubleValue()),ActionEntry.ACTION_AUTHORISTION_FAILED,ActionEntry.STATUS_AUTHORISTION_FAILED);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table



                }

                logger.debug("Error while communicating to ICICI for trackingId = " + trackingId + ", error is :" + nex.toString());

            }//try catch ends

            try
            {
                if (proofrequired && hash != null && (String) hash.get("authqsiresponsecode") != null && (hash.get("authqsiresponsecode")).equals("0"))
                {
                    query = "update transaction_icicicredit set status='proofrequired',pod=null where icicitransid=?";
                    PreparedStatement p= con.prepareStatement(query);
                    p.setString(1,trackingId);
                    p.executeUpdate();


                    // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(String.valueOf(trackingId),String.valueOf(amount.doubleValue()),ActionEntry.ACTION_PROOF_REQUIRED,ActionEntry.STATUS_PROOF_REQUIRED);
                    entry.closeConnection();

                    // End : Added for Action and Status Entry in Action History table



                }
                //if proof is not require but still there is any high risk fraud then don't capture it
                else
                if (!(repeatEmail || repeatmachine || repeatIP || diffCountry) && type.equals("sale") && hash != null && (String) hash.get("authqsiresponsecode") != null && (hash.get("authqsiresponsecode")).equals("0"))
                {
                    logger.debug("Service member - doing capture");
                    String ss = transaction.processCaptureAndTransaction(con, merchantId, trackingId, authid, authCode, authRRN, txnamount, String.valueOf(memberId), transactionDescription, accountId);
                    transactionStatus = "Y";
                    captureStatus = "Successful";
                    body.append("Capture Status: " + captureStatus + "\r\n");
                    if (Functions.checkStringNull(ss) == null)
                    {
                        logger.debug("calling SendMAil for response from bank during capture is null");
                        Mail.sendAdminMail("Null received:" + mailtransactionStatus + " - Order: " + transactionDescription + " Amount: " + txnamount + " Card Holder: " + cardholder, "\r\n\r\n Bank return null value while capturing transaction using advance operator.\r\nPlease check login password of your advance operator.Also check that this operator is not disable.");
                        logger.debug("called SendMAil");

                    }
                    //null pointer //change flag to send mail
                    if (ss.equals("There was an error while executing the capture"))
                    {
                        captureexception = true;
                        tcsubject.append("Exception Occured while Capture");
                    }
                }
            }
            catch (Exception cex)
            {
                pw = new PrintWriter(sw);
                cex.printStackTrace(pw);

                captureexception = true;
                tcsubject.append("Exception Occured while Capture");
                tcbody.append("Exception Occured while Caputre - " + sw.toString() + "\r\n\r\n");

                if (cex instanceof com.directi.pg.SystemError)
                {
                    String exp = cex.getMessage();
                    int ind = exp.indexOf("#1234#");
                    if (ind != -1)
                    {
                        exp = exp.substring(ind + 5);
                        query = "update transaction_icicicredit set captureresult='" + exp + "' where icicitransid = " + trackingId;
                        Database.executeQuery(query, con);
                    }
                }
                logger.debug("Error while communilog.ng to ICICI for trackingId = " + trackingId + ", error is :" + cex.toString());
            }//try catch ends

            body.append("Tracking ID: " + trackingId + "\r\n\r\n");

            merchantsubject.append(mailtransactionStatus + " - Order: " + transactionDescription + " Amount " + txnamount + " Card Holder: " + cardholder);

            tcbody.append(body.toString());

            merchantbody.append(body.toString());
            merchantbody.append("Customer Email Address: " + emailaddr + "\r\n");
            merchantbody.append("IP Address: " + ipaddress + "\r\n");
            merchantbody.append("AuthQsiResponseCode : " + (String) hash.get("authqsiresponsecode") + "\r\n\r\n");

            if (!HRCode.equals("") && !transactionfailed)
                merchantbody.append("High Risk Code: " + HRCode + "\r\n");

            merchantbody.append("Please inform your customer about the transaction status\r\n\r\n");
            merchantbody.append("***IMPORTANT***\r\n");
            merchantbody.append("Note that this transaction will appear on their Credit Card Statement as \r\n " + displayName + ".\r\n\r\n");

            servicemerchantbody.append("The following transaction has just taken place on your website.\r\n\r\n");
            servicemerchantbody.append(body.toString());
            servicemerchantbody.append("Customer Email Address: " + emailaddr + "\r\n");
            servicemerchantbody.append("IP Address: " + ipaddress + "\r\n");

            if (!HRCode.equals(""))
                servicemerchantbody.append("High Risk Code: " + HRCode + "\r\n");

            servicemerchantbody.append("AuthQsiResponseCode : " + (String) hash.get("authqsiresponsecode") + "\r\n\r\n");
            servicemerchantbody.append("This transaction has NOT BEEN CAPTURED, and you will have to MANUALLY CAPTURE this Transaction.\r\n\r\n");
            servicemerchantbody.append("Though this transaction is successfully authorized by bank, This transaction is a High Risk Transaction. This could be because of reasons such as the Customer having used multiple different Cards in the past, or having a Chargeback history, or billing address mismatches etc.\r\n\r\n");
            servicemerchantbody.append("It is advised that you check this transaction before delivering the goods since this could possibly be fraudulent transaction.\r\n\r\n");
            servicemerchantbody.append("Admin Support.\r\n");

            //mail body to ICICI

            if (!transactionfailed && txnPaise > aptprompt && !proofrequired)
            //if memberapt is set greater than 25000 and proof is not required
            {
                if (memberaptprompt == 0)
                    memberaptprompt = aptprompt;

                logger.debug("calling SendMAil for ICICI for merchant " + merchantId);
                String adminEmail = ApplicationProperties.getProperty("COMPANY_ADMIN_EMAIL");
                Mail.sendmail(ICICIEMAIL, "", null, adminEmail, merchantId + "-No need to send the proof.", "Hi,\r\n\r\nThe below mention transaction belong to merchant:  " + company_name + "\r\n\r\nShopping Transactionid :" + authid + "\r\namount: " + currency + " " + txnamount + "\r\n\r\n\t Proof document will not be sent for this transaction because their high risk transaction limit is set to Rs." + memberaptprompt / 100 + ".\r\n\r\nSo we request you to credit the amount immediately.\r\n\r\nThank You\r\nAdmin Support.\r\n");
                logger.debug("called SendMAil for ICICI for merchant " + merchantId);
            }

            if (!proofrequired)     //send mail with appropriate transaction status
            {
                if ((repeatEmail || repeatmachine || repeatIP || diffCountry) && type.equals("sale") && !transactionfailed)   //if merchant is in service mode and due to high risk alert transaction was not captured then send mail explaining it
                {
                    logger.debug("calling SendMAil for Service Merchant");
                    Mail.sendmail(notifyEmails, "", "HIGH RISK FRAUD ALERT: " + merchantsubject.toString(), servicemerchantbody.toString());
                    logger.debug("called SendMAil for Service Merchant");
                }
                else
                {   //if merchant is not in sale mode.
                    if ((repeatEmail || repeatmachine || repeatIP || diffCountry) && !transactionfailed) //id different country found then send as high risk mail as no separate mail for that
                    {
                        StringBuffer alertMessage = new StringBuffer("\r\nThough this transaction is successfully authorized by bank, This transaction is a High Risk Transaction. This could be because of reasons such as the Customer having used multiple different Cards in the past, or having a Chargeback history, or billing address mismatches etc.\r\n\r\n");
                        alertMessage.append("It is advised that you check this transaction before delivering the goods since this could possibly be fraudulent transaction.\r\n\r\n");
                        alertMessage.append(merchantbody.toString());
                        logger.debug("calling SendMAil for Merchant with different country alert");
                        Mail.sendmail(notifyEmails, "", "HIGH RISK FRAUD ALERT: " + merchantsubject.toString(), alertMessage.toString());
                        logger.debug("called SendMAil for Merchant with different country alert");
                    }
                    else
                    {
                        logger.debug("calling SendMAil for Merchant with status");
                        Mail.sendmail(notifyEmails, "", merchantsubject.toString(), merchantbody.toString());
                        logger.debug("called SendMAil for Merchant with status");
                    }
                }
            }
            //Also need to send charges that we will take from merchant for this transaction.
            // decide chargeamount based on charge percentage
            //forward it to next page - to merchant
            //we will charge that much amount only if merchant willcapture full transaction

            chargeamount = amount.multiply(charge.multiply(new BigDecimal(1 / 10000.00)));
            chargeamount = chargeamount.add(new BigDecimal(fixamount));
            chargeamount = chargeamount.setScale(2, BigDecimal.ROUND_HALF_UP);
            String chargeamt = chargeamount.toString();

            if (hash.get("authqsiresponsecode") != null && !(hash.get("authqsiresponsecode")).equals("0"))
                transactionMessage = "Failed";

            String checksum;
            if (version.equals("2"))
                checksum = Checksum.generateChecksumV3(String.valueOf(memberId), transactionStatus, transactionMessage, transactionDescription, "" + txnamount, chargeamt, clkey, checksumAlgo);
            else
                checksum = Checksum.generateChecksumV1(String.valueOf(memberId), transactionStatus, transactionMessage, transactionDescription, "" + txnamount, clkey, checksumAlgo);

            pWriter.println("<form name=\"postpay\" action=\"" + PROXYSCHEME + "://" + PROXYHOST + ":" + PROXYPORT + "" + POSTURL + "\" method=\"post\" >");
            pWriter.println("<input type=\"hidden\" name=\"status\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(transactionStatus) + "\">");
            pWriter.println("<input type=\"hidden\" name=\"message\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(transactionMessage) + "\">");
            pWriter.println("<input type=\"hidden\" name=\"amount\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(txnamount) + "\">");
            pWriter.println("<input type=\"hidden\" name=\"chargeamt\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(chargeamt) + "\">");
            pWriter.println("<input type=\"hidden\" name=\"checksum\" value=\"" + checksum + "\">");

            Enumeration enu = request.getParameterNames();
            while (enu.hasMoreElements())
            {
                String name = (String) enu.nextElement();
                pWriter.println("<input type=\"hidden\" name=\"" + name + "\" value=\"" + ESAPI.encoder().encodeForHTMLAttribute(request.getParameter(name)) + "\">");
                //hiddenvariables.append("<INPUT TYPE=\"HIDDEN\" NAME=\""+ name +"\" VALUE=\"" + req.getParameter(name) + "\">");
            }

            if (proofrequired && transactionStatus.equals("Y"))
                pWriter.println("<input type=\"hidden\" name=\"PROOFREQUIRED\" value=\"Y\">");
            else
                pWriter.println("<input type=\"hidden\" name=\"PROOFREQUIRED\" value=\"N\">");

            pWriter.println("</form>");
            pWriter.println("<script language=\"javascript\">");
            pWriter.println("document.postpay.submit();");
            pWriter.println("</script>");
            pWriter.println("</body>");
            pWriter.println("</html>");


            if (authexception || captureexception)
            {
                tcbody.append(body.toString());
                Mail.sendAdminMail(tcsubject.toString(), tcbody.toString());
            }

            if (authexception)
            {
                logger.debug("calling SendMAil for Merchant");
                Mail.sendmail(notifyEmails, "", "No Response From Bank.", "It seems that the Transaction Authorization has failed due to some error.\r\n\r\n" + merchantbody.toString());
                logger.debug("called SendMAil for Merchant");
            }


            Hashtable taghash = new Hashtable();
            taghash.put("TOID", memberId + "");
            taghash.put("NAME", cardholder);
            taghash.put("CCNUM", ccpan.substring(ccpan.length() - 4, ccpan.length()));
            taghash.put("EXPDATE", request.getParameter("EXPIRE_MONTH") + "/" + request.getParameter("EXPIRE_YEAR"));
            taghash.put("COMPANYNAME", company_name);
            taghash.put("BRANDNAME", brandname);
            taghash.put("SITENAME", sitename);
            taghash.put("AMOUNT", "" + txnamount);
            if (mailtransactionStatus.equals("Successful"))
            {
                taghash.put("TRANSAMOUNT", txnamount);

            if (type.equals("sale") && !proofrequired && !transactionfailed)
                    taghash.put("CAPAMOUNT", txnamount);
                else
                    taghash.put("CAPAMOUNT", "0.00");

            }
            taghash.put("DESCRIPTION", transactionDescription);
            taghash.put("ORDERDESCRIPTION", transactionOrderDescription);
            taghash.put("ORDER_DESCRIPTION", transactionOrderDescription);
            taghash.put("CARDHOLDER", cardholder);
            taghash.put("TRACKINGID", trackingId);
            taghash.put("EMAILADDRESS", emailaddr);
            taghash.put("IPADDRESS", ipaddress);
            taghash.put("HRCODE", HRCode);
            taghash.put("DATE", (new java.util.Date()).toString());
            taghash.put("CURRENCY", currency);
            taghash.put("DISPLAYNAME", displayName);
            if (templateCurrency != null && !currency.equals(templateCurrency))
                taghash.put("TMPL_TRANSACTION", "(approximately " + templateCurrency + " " + templateAmount + " )");
            application.log("Checking for proofreqyuired" + proofrequired);
            if (proofNotRequiredAgain)
            {
                application.log("calling Send Mail for high risk transaction (proof not reqd.)");
                Mail.sendNotificationMail("High Risk (proof not reqd. again) ( " + HRCode + " ) : " + company_name + Functions.convertStringtoDate(dtstamp), "The Transaction is a High Risk Transaction but since we have already received the proof earlier from the same customer for the same card.\r\n\r\n" + merchantbody.toString() + "\r\n\r\n");
                application.log("called Send Mail for high risk transaction (proof not reqd.)");
            }
            if (proofrequired && !transactionfailed)
            {
                String MERCHANTHRT = Functions.replaceTag((String) application.getAttribute("MERCHANTHRT"), taghash);
                String CUSTOMERHRT = Functions.replaceTag((String) application.getAttribute("CUSTOMERHRT"), taghash);

                proofsubject = "Documents Required for Order No :" + transactionDescription;

                if (repeatEmail || repeatmachine || repeatIP || diffCountry) //if there is any alert and due to that proof require is set true
                {
                    logger.debug("calling SendMAil for on High Risk proof alert");
                    Mail.sendHtmlMail(notifyEmails, "", null, null, "HIGH RISK FRAUD ALERT: " + proofsubject, MERCHANTHRT);
                    logger.debug("called SendMAil for High Risk proof alert");
                }
                else
                {
                    logger.debug("calling SendMAil for Merchant  for proof require");
                    Mail.sendHtmlMail(notifyEmails, "", null, null, proofsubject, MERCHANTHRT);
                    logger.debug("called SendMAil for Merchant ");
                }

                if (HRCode.indexOf("DCON") > 0)
                    HRCode = HRCode + "( " + country + "-" + countryinrec + " )";

                logger.debug("calling SendMAil for  high risk transaction");
                Mail.sendAdminMail("High Risk Transaction ( " + HRCode + " ) : " + company_name + Functions.convertStringtoDate(dtstamp), "It seems that the Transaction AUTHORIZATION was successful but Transaction is High Risk Transaction.\r\n\r\n" + merchantbody.toString() + "\r\n\r\n");
                logger.debug("called SendMAil for high risk transaction");

                logger.debug("calling SendMAil for  Customer for high risk transaction");
                Mail.sendHtmlMail(emailaddr, "", null, null, proofsubject, CUSTOMERHRT);
                logger.debug("called SendMAil for Customer for high risk transaction");
            }  //end if(proofrequire).

            if (!transactionfailed && /*custremindermail &&*/ !proofrequired)
            {
                taghash.put("SIGNATURE",Functions.generateSignature(memberId+""));
                String CUSTOMERNOTIFICATION = Functions.replaceTag((String) application.getAttribute("CUSTOMERNOTIFICATION"), taghash);
                logger.debug("calling SendMail for Customer for ");
                Mail.sendHtmlMail(emailaddr, "", null, null, "Your transaction at " + sitename, CUSTOMERNOTIFICATION);
                logger.debug("called SendMail for Customer for ");
            }
        }
        catch (SystemError sye)
        {
            if (count >= 0)
            {
                count = Integer.parseInt((String) application.getAttribute("noOfClients"));
                count--;
                application.setAttribute("noOfClients", String.valueOf(count));
            }
            throw new ServletException(sye.toString());
        }
        catch (SQLException sqle)
        {
            if (count >= 0)
            {
                count = Integer.parseInt((String) application.getAttribute("noOfClients"));
                count--;
                application.setAttribute("noOfClients", String.valueOf(count));
            }
            throw new ServletException(sqle.toString());
        }
        catch (Exception nex)
        {
            pw = new PrintWriter(sw);
            nex.printStackTrace(pw);

            tcsubject.append(" / Exception in VBVWait Servlet");
            tcbody.append("\r\nFollowing Exception Occured " + sw.toString() + "\r\n\r\n");
            tcbody.append(body.toString());
            logger.debug("\r\nFollowing Exception Occured " + sw.toString() + "\r\n\r\n");
            try
            {
                logger.debug("calling SendMAil");
                Mail.sendAdminMail(tcsubject.toString(), tcbody.toString());
                logger.debug("called SendMAil");
            }
            catch (SystemError sye)
            {
                if (count >= 0)
                {
                    count = Integer.parseInt((String) application.getAttribute("noOfClients"));
                    count--;
                    application.setAttribute("noOfClients", String.valueOf(count));
                }

                logger.error(sye);
                throw new ServletException(sye.toString());

            }
            finally
            {
                String message = "";
                logger.debug("SOME ERROR");
                if (!authexception)
                    message = "<b>ERROR!!!</b> We have encountered an internal error while processing your request.<BR>";
                else
                    message = "<b>ERROR!!!</b> We have encountered an internal error while processing your request. This happened because of a connectivity issue with the Credit Card Processor.<BR>";

                otherdetails.put("TRACKING_ID", Encoded_TRACKING_ID);
                otherdetails.put("DESCRIPTION", Encoded_DESCRIPTION);
                otherdetails.put("ORDER_DESCRIPTION", Encoded_ORDER_DESCRIPTION);
                otherdetails.put("MESSAGE", message);

                try
                {
                    pWriter.flush();

                    //RequestDispatcher rd=request.getRequestDispatcher("/icici/servlet/Error.jsp");
                    //rd.forward(request,res);
                    pWriter.println("<form name=\"error\" action=\"/icici/servlet/ErrorServlet\" method=\"post\" >");
                    pWriter.println("<input type=\"hidden\" name=\"ERROR\" value=\"" + URLEncoder.encode(Template.getErrorPage("" + memberId, otherdetails)) + "\">");
                    pWriter.println("</form>");
                    pWriter.println("<script language=\"javascript\">");
                    pWriter.println("document.error.submit();");
                    pWriter.println("</script>");
                    pWriter.println("</body>");
                    pWriter.println("</html>");
                }
                catch (Exception e)
                {
                    logger.error("exception occur ",e);
                }
                return;
            }
        }
        finally
        {
            Database.closeConnection(con);
            if (count >= 0)
            {
                count = Integer.parseInt((String) application.getAttribute("noOfClients"));
                count--;
                application.setAttribute("noOfClients", String.valueOf(count));
            }
        }
        ccpan=null;
        month=null;
        year=null;
        ccid=null;

    }//service ends


    private String getCookie(HttpServletRequest request, HttpServletResponse res) throws SystemError
    {
        logger.debug("Inside getCookie ");

        Cookie cook[] = request.getCookies();
        String name = null;
        String value = null;
        if (cook != null)
        {
            for (int i = 0; i < cook.length; i++)
            {
                name = cook[i].getName();
                if (name.equals("mid"))
                {
                    value = cook[i].getValue();
                    break;
                }
            }
        }
        logger.debug("leaving  getCookie " + value);
        return value;
    }

    private String getBoiledName(String name)
    {
        logger.debug("Inside getBoiledName");
        if (Functions.parseData(name) == null)
            return name;

        int index = 0;
        String temp = "", boiledName = "";
        for (int i = 1; i <= name.length(); i++)
        {
            temp = name.substring(index, i).toLowerCase();
            if (unboilchar.contains(temp))
                boiledName = boiledName + temp;
            index = i;
        }

        logger.debug("leaving getBoiledName");
        return boiledName;

    }
}//servlet class ends
