import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class TransactionStatus extends HttpServlet
{

    private static Logger logger = new Logger(TransactionStatus.class.getName());

    public TransactionStatus()
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

        logger.debug("Entering in TransactionStatus");
        PrintWriter pWriter = res.getWriter();
        //res.setContentType("text/plain");
        HttpSession session = req.getSession();

        String merchantid = (String) req.getParameter("merchantid");
        String descriptions = (String) req.getParameter("description");
        String checksum = req.getParameter("checksum");
        StringBuffer csvdescription = new StringBuffer(" ");
        StringBuffer status = new StringBuffer(" ");
        StringBuffer newdescription = new StringBuffer(" ");
        StringBuffer newchargeamount = new StringBuffer(" ");


        String key = "";
        String checksumalgo = "";
        String description = "";
        String tempstatus = "";
        String tempamt = "";
        String chargeamt = "";
        String fixamount = "";
        BigDecimal transamount = null;
        BigDecimal charge = null;
        BigDecimal chargeamount = null;
        Hashtable hashReceived = new Hashtable();
        Hashtable hashAvailable = new Hashtable();
        Hashtable hashChargeAmount = new Hashtable();


        Connection con = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1=null;
        ResultSet resultSet=null;
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            String s1="select clkey,checksumalgo from members where memberid=?";
            pstmt=conn.prepareStatement(s1);
            pstmt.setString(1,merchantid);
            rs = pstmt.executeQuery();

            if (rs.next())
            {
                key = rs.getString(1);
                checksumalgo = rs.getString(2);
            }
            else
            {
                pWriter.print("error=Merchant not found.");
                return;
            }

            logger.debug(" verify checksum ");

            //verify checksum
            if (Functions.verifyChecksumV1(merchantid, descriptions, checksum, key, checksumalgo))
            {
                logger.debug(" verifed checksum ");
                StringTokenizer stz = new StringTokenizer(descriptions, ",");

                while (stz.hasMoreElements())
                {
                    description = stz.nextToken();
                    csvdescription.append("'" + description + "',");
                    hashReceived.put(description, "Not Found"); //so that all those description which we can't get from our record will have value not found
                }

                csvdescription.deleteCharAt(csvdescription.length() - 1);

                // As same description can have many states so instead of directly putting in to hash table retrieving resultset
                //hash.putAll(Functions.getDetailedHashFromResultSet(Database.executeQuery("select description,status from transaction_icicicredit where toid=" + merchantid +" and description in ("+ csvdescription +") order by icicitransid,description asc",Database.getConnection())));
                String s="select description,status,chargeper,amount,captureamount,fixamount from transaction_icicicredit where toid=? and description in (?) order by icicitransid,description asc";
                con = Database.getConnection();
                pstmt1=con.prepareStatement(s);
                pstmt1.setString(1,merchantid);
                pstmt1.setString(2,csvdescription.toString());
                resultSet = pstmt1.executeQuery( );

                while (resultSet.next())
                {

                    //If transaction is captured then consider captureamount
                    if (resultSet.getDouble("captureamount") != 0.0)
                        transamount = new BigDecimal(resultSet.getDouble("captureamount"));
                    else
                        transamount = new BigDecimal(resultSet.getDouble("amount"));

                    if (resultSet.getString("fixamount") != null)
                        fixamount = resultSet.getString("fixamount");
                    else
                        fixamount = "0.0";

                    if (resultSet.getString("chargeper") != null)
                        charge = new BigDecimal(resultSet.getString("chargeper"));
                    else
                        charge = new BigDecimal(0.0); //if there is no chargeper means something is wrong so take 0.0
                    //So that caller can also know that something is wrong
                    chargeamount = transamount.multiply(charge.multiply(new BigDecimal(1 / 10000.00)));
                    chargeamount = chargeamount.add(new BigDecimal(fixamount));
                    chargeamount = chargeamount.setScale(2, BigDecimal.ROUND_HALF_UP);
                    tempamt = chargeamount.toString();

                    description = resultSet.getString("description");
                    tempstatus = resultSet.getString("status");
                    if ((String) hashAvailable.get(description) != null)
                    {
                        String st = (String) hashAvailable.get(description);
                        String chamt = (String) hashChargeAmount.get(description);

                        //if new status is failed or authfailed and previous status is not failed or authfail then take previous status and charges
                        if (!(st.equals("failed") || st.equals("authfailed")) && (tempstatus.equals("failed") || tempstatus.equals("authfailed")))
                        {
                            tempstatus = st;
                            tempamt = chamt;

                        }

                    }
                    hashAvailable.put(description, tempstatus);
                    hashChargeAmount.put(description, tempamt);
                }

                hashReceived.putAll(hashAvailable); // This will over write values
                Enumeration enu = hashReceived.keys();

                while (enu.hasMoreElements())
                {
                    description = (String) enu.nextElement();
                    tempstatus = (String) hashReceived.get(description);
                    if ((String) hashChargeAmount.get(description) != null)
                        chargeamt = (String) hashChargeAmount.get(description);
                    else
                        chargeamt = "0.0";

                    pWriter.print(description + "|" + tempstatus + "|" + chargeamt + "&");
                    status.append(tempstatus + ",");
                    newdescription.append(description + ",");
                    newchargeamount.append(chargeamt + ",");

                }
                int random = (int) ((2147483647) * java.lang.Math.random());
                pWriter.print("checksum=" + Functions.generateChecksumV3(newdescription.deleteCharAt(newdescription.length() - 1).toString().trim(), status.deleteCharAt(status.length() - 1).toString().trim(), newchargeamount.deleteCharAt(newchargeamount.length() - 1).toString().trim(), key, random, checksumalgo) + "&random=" + random);
                pWriter.flush();
                pWriter.close();
            }
            else
            {
                logger.debug(" checksum Mismatch " + merchantid + descriptions + checksum);
                pWriter.print("error=Checksum Mismatch");
                return;
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception in TransactionStatus::::::",ex);
            pWriter.print(ex);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(resultSet);
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeConnection(con);
            Database.closeConnection(conn);
        }
    }

}



