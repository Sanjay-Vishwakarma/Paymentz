import com.directi.pg.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


public class CaptureTransaction extends HttpServlet
{

    private static Logger logger = new Logger(CaptureTransaction.class.getName());

    public CaptureTransaction()
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

    public synchronized void doService(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in CaptureTransaction");
        PrintWriter pWriter = res.getWriter();
        //res.setContentType("text/plain");
        HttpSession session = req.getSession();

        String memberid = (String) req.getParameter("merchantid");
        String descriptions = (String) req.getParameter("description");
        String captureamounts = (String) req.getParameter("captureamount");
        String checksum = req.getParameter("checksum");

        StringBuffer csvdescription = new StringBuffer(" ");
        StringBuffer status = new StringBuffer(" ");
        StringBuffer newdescription = new StringBuffer(" ");
        StringBuffer chargeamt = new StringBuffer(" ");

        String key = "";
        String checksumAlgorithm = "";
        String description = "";
        String icicimerchantid = "";
        BigDecimal captureamount = null;
        String tempstatus = "";
        String tempdescription = "";
        String tempchargeamt = "";
        Hashtable hashReceived = new Hashtable();
        Hashtable hashAvailable = new Hashtable();
        Hashtable chargeHash = new Hashtable();
        Hashtable hash = new Hashtable();
        Connection conn = null;

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Transaction transaction = new Transaction();

        Connection con = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        PreparedStatement p3 = null;
        PreparedStatement p4 = null;
        PreparedStatement p5 = null;
        ResultSet statusrs = null;
        ResultSet rs1 = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String query="select clkey,checksumalgo from members where memberid=?";
            p1=conn.prepareStatement(query);
            p1.setString(1,memberid);
            rs = p1.executeQuery();

            if (rs.next())
            {
                key = rs.getString(1);
                checksumAlgorithm = rs.getString(2);
            }
            else
            {
                pWriter.print("error=Merchant not found.");
                return;
            }
            logger.debug(" verify checksum ");

            //verify checksum
            if (Functions.verifyChecksumV2(memberid, descriptions, captureamounts, checksum, key, checksumAlgorithm))
            {
                logger.debug(" verifed checksum ");
                StringTokenizer descriptionsstz = new StringTokenizer(descriptions, ",");
                StringTokenizer captureamountsstz = new StringTokenizer(captureamounts, ",");

                while (descriptionsstz.hasMoreElements())
                {
                    //if(captureamountsstz.hasMoreElements())
                    String tempcaptureamount = captureamountsstz.nextToken();
                    description = descriptionsstz.nextToken();

                    csvdescription.append("'" + description + "',");
                    hashReceived.put(description, "Not Found"); //so that all those description which we can't get from our record will have value not found
                    chargeHash.put(description, "0.00"); //so that all those description which we can't get from our record will have value 0.00
                    hash.put(description, tempcaptureamount);
                }
                csvdescription.deleteCharAt(csvdescription.length() - 1);
                String query1="select  icicimerchantid,icicitransid,captureamount,authid,authcode,authreceiptno,description,status,amount,accountid from transaction_icicicredit where toid=? and description in (?) and status='authsuccessful'  order by icicitransid asc";
                p2=conn.prepareStatement(query1);
                p2.setString(1,memberid);
                p2.setString(2,csvdescription.toString());

                rs1 = p2.executeQuery();

                boolean connectionfailure = false;
                Vector invalidAmount = new Vector();

                while (rs1.next())
                {
                    description = rs1.getString("description");
                    icicimerchantid = rs1.getString("icicimerchantid");
                    captureamount = new BigDecimal((String) hash.get(description));

                    int icicitransid = rs1.getInt("icicitransid");
                    String authid = rs1.getString("authid");
                    String authCode = rs1.getString("authcode");
                    String authRRN = rs1.getString("authreceiptno");
                    BigDecimal authamount = new BigDecimal(rs1.getString("amount"));
                    String accountId = rs1.getString("accountid");
                    if (captureamount.compareTo(authamount) <= 0)
                    {
                        try
                        {
                            //captureexception = false;
                            if (!connectionfailure)
                            {
                                // BigDecimal bdcaptureamount = new BigDecimal( captureamount.toString() );
                                // bdcaptureamount = bdcaptureamount.multiply( new BigDecimal( 100.0 ) );
                                String query2="update transaction_icicicredit set captureamount=? where icicitransid=?";
                                p3=conn.prepareStatement(query2);
                                p3.setBigDecimal(1,captureamount);
                                p3.setInt(2,icicitransid);
                                p3.executeQuery();
                                String message = transaction.processCaptureAndTransaction(conn, icicimerchantid, "" + icicitransid, "" + authid, authCode, authRRN, captureamount.toString(), memberid, description, accountId);
                            }
                            //if (ss.equals("There was an error while executing the capture"))
                            //captureexception = true;
                        }
                        catch (SystemError se)
                        {
                            logger.error("Catch SystemError...",se);
                            se.printStackTrace(pw);
                            try
                            {
                                String error = se.toString();
                                int pos = error.indexOf("#1234#");

                                if (error.indexOf("#E5231:QSI.util.tls.TLSException") != -1 || error.indexOf("#E5231:QSI.PaymentServer.SSL.MalformedDRException") != -1)
                                    connectionfailure = true;

                                if (pos != -1 && error.length() > pos + 6 + 1)
                                {
                                    error = error.substring(pos + 6 + 1);

                                    //update data if exception in processCaptureAndTransaction
                                    String query2="update transaction_icicicredit set captureresult=? where icicitransid=?";
                                    p4=conn.prepareStatement(query2);
                                    p4.setString(1,error);
                                    p4.setInt(2,icicitransid);
                                    p4.executeQuery();

                                    StringBuffer errorbody = new StringBuffer();
                                    errorbody.append("trackingid").append("\t").append("description").append("\t").append("Amount\r\n");
                                    errorbody.append("----------").append("\t").append("-----------").append("\t").append("------\r\n");

                                    errorbody.append(icicitransid).append("\t").append(description).append("\t").append("" + captureamount).append("\r\n");
                                    errorbody.append("\r\n\r\n" + error);

                                    logger.debug("calling SendMAil for -capture Error");
                                    Mail.sendAdminMail("Exception while  Capture ", errorbody.toString());
                                    logger.debug("called SendMAil for -capture Error");
                                }
                            }
                            catch (SystemError ise)
                            {
                                logger.error("Error while update :",ise);
                            }
                            logger.error("Error while Capture :" , se);
                            logger.debug("Beaking loop and coming out....");
                            break;

                        }
                        catch (Exception ex)
                        {   logger.error("Exception:::::",ex);
                            String message = ex.getMessage();
                            if (message.indexOf("#E5231:QSI.util.tls.TLSException") != -1 || message.indexOf("#E5231:QSI.PaymentServer.SSL.MalformedDRException") != -1)
                                connectionfailure = true;

                        }
                    }
                    else
                    {
                        invalidAmount.add(description);
                    }
                }//while ends

                //hash.putAll(Functions.getDetailedHashFromResultSet(Database.executeQuery("select description,status from transaction_icicicredit where toid=" + merchantid +" and description in ("+ csvdescription +") order by icicitransid,description asc",Database.getConnection())));
                con = Database.getConnection();
                String query3="select description,status,captureamount,chargeper as transpercfee,fixamount as transfixfee from transaction_icicicredit where toid=? and description in (?) order by icicitransid,description asc";
                p5=con.prepareStatement(query3);
                p5.setString(1,memberid);
                p5.setString(2,csvdescription.toString());
                statusrs = p5.executeQuery();
                while (statusrs.next())
                {
                    description = statusrs.getString("description");
                    tempstatus = statusrs.getString("status");
                    if ((String) hashAvailable.get(description) != null)
                    {
                        String st = (String) hashAvailable.get(description);

                        //if new status is failed or authfailed and previous status is not failed or authfail then take previous status
                        if (!(st.equals("failed") || st.equals("authfailed")) && (tempstatus.equals("failed") || tempstatus.equals("authfailed")))
                            tempstatus = st;

                    }
                    BigDecimal amt = new BigDecimal(statusrs.getString("captureamount"));
                    BigDecimal percent = new BigDecimal(statusrs.getString("transpercfee"));
                    BigDecimal peramt = amt.multiply(percent.multiply(new BigDecimal(1 / 10000.00)));
                    peramt = peramt.add(new BigDecimal(statusrs.getString("transfixfee")));
                    peramt = peramt.setScale(2, BigDecimal.ROUND_HALF_UP);

                    chargeHash.put(description, peramt.floatValue() + "");  //overwrite previous 0.00 value
                    hashAvailable.put(description, tempstatus);

                }

                hashReceived.putAll(hashAvailable); // This will over write Not Found values


                Enumeration resultenu = hashReceived.keys();

                while (resultenu.hasMoreElements())
                {
                    tempdescription = (String) resultenu.nextElement();
                    tempstatus = (String) hashReceived.get(tempdescription);
                    tempchargeamt = (String) chargeHash.get(tempdescription);
                    if (invalidAmount.contains(description))
                        tempstatus = "invalidamount";

                    pWriter.print(tempdescription + "=" + tempstatus + "=" + tempchargeamt + "&");
                    status.append(tempstatus + ",");
                    newdescription.append(tempdescription + ",");
                    chargeamt.append(tempchargeamt + ",");
                }

                pWriter.print("checksum=" + Functions.generateChecksumV2(newdescription.deleteCharAt(newdescription.length() - 1).toString().trim(), status.deleteCharAt(status.length() - 1).toString().trim(), chargeamt.deleteCharAt(chargeamt.length() - 1).toString().trim(), key, checksumAlgorithm));
                pWriter.flush();
                pWriter.close();
            }
            else
            {
                logger.debug(" checksum Mismatch " + memberid + descriptions + checksum);
                pWriter.print("error=ChecksumMismatch");
                return;
            }
        }
        catch (Exception ex)
        {   logger.error("Exception in CaptureEransection",ex);
            ex.printStackTrace(pw);
            pWriter.print(sw.toString());
        }
        finally
        {
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closePreparedStatement(p3);
            Database.closePreparedStatement(p4);
            Database.closePreparedStatement(p5);
            Database.closeResultSet(statusrs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
            Database.closeConnection(con);
        }
    }

}



