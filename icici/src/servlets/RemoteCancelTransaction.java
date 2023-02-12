import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.fraud.vo.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class RemoteCancelTransaction extends HttpServlet
{

    private static Logger logger = new Logger(RemoteCancelTransaction.class.getName());

    public RemoteCancelTransaction()
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
    {   logger.debug("Entering in RemoteCancelTransaction");
        PrintWriter pWriter = res.getWriter();
        HttpSession session = req.getSession();

        String merchantid = (String) req.getParameter("merchantid");
        String descriptions = (String) req.getParameter("description");
        String checksum = req.getParameter("checksum");

        StringBuffer csvdescription = new StringBuffer(" ");
        StringBuffer status = new StringBuffer(" ");
        StringBuffer newdescription = new StringBuffer(" ");

        String key = "";
        String checksumalgo = "";
        String description = "";
        String icicimerchantid = "";
        BigDecimal captureamount = null;
        String tempstatus = "";
        String tempdescription = "";
        Connection conn = null;
        Hashtable hashReceived = new Hashtable();
        Hashtable hashAvailable = new Hashtable();
        PreparedStatement pst = null;
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet statusrs = null;
        try
        {
            conn = Database.getConnection();
            String st1="select clkey,checksumalgo from members where memberid=?";
            pst=conn.prepareStatement(st1);
            pst.setString(1,merchantid);
            rs = pst.executeQuery();

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
                StringTokenizer descriptionsstz = new StringTokenizer(descriptions, ",");


                while (descriptionsstz.hasMoreElements())
                {
                    description = descriptionsstz.nextToken();
                    csvdescription.append("'" + description + "',");
                    hashReceived.put(description, "Not Found"); //so that all those description which we can't get from our record will have value not found

                }

                csvdescription.deleteCharAt(csvdescription.length() - 1);
                String st2="update transaction_icicicredit set status='authcancelled' where toid=? and description in (?) and status ='authsuccessful'";
                pst1=conn.prepareStatement(st2);
                pst1.setString(1,merchantid);
                pst1.setString(2,csvdescription.toString());
                rs1 = pst1.executeQuery();

                //Hashtable hash=Functions.getDetailedHashFromResultSet(Database.executeQuery("select description,status from transaction_icicicredit where toid=" + merchantid +" and description in ("+ csvdescription +") order by icicitransid,description asc",Database.getConnection()));
                String st3="select description,status from transaction_icicicredit where toid=? and description in (?) order by icicitransid,description asc";
                pst2=conn.prepareStatement(st3);
                pst2.setString(1,merchantid);
                pst2.setString(2,csvdescription.toString());
                statusrs = pst2.executeQuery();
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

                    hashAvailable.put(description, tempstatus);
                }

                hashReceived.putAll(hashAvailable); // This will over write values

                Enumeration resultenu = hashReceived.keys();

                while (resultenu.hasMoreElements())
                {
                    tempdescription = (String) resultenu.nextElement();
                    tempstatus = (String) hashReceived.get(tempdescription);

                    pWriter.print(tempdescription + "=" + tempstatus + "&");
                    status.append(tempstatus + ",");
                    newdescription.append(tempdescription + ",");
                }

                pWriter.print("checksum=" + Functions.generateChecksumV1(newdescription.deleteCharAt(newdescription.length() - 1).toString().trim(), status.deleteCharAt(status.length() - 1).toString().trim(), key, checksumalgo));
                pWriter.flush();
                pWriter.close();
            }
            else
            {
                logger.debug(" checksum Mismatch " + merchantid + descriptions + checksum);
                pWriter.print("error=ChecksumMismatch");
                return;
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception in RemoteCancelTransaction:::::::",ex);
            pWriter.print(ex);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(statusrs);
            Database.closePreparedStatement(pst);
            Database.closePreparedStatement(pst1);
            Database.closePreparedStatement(pst2);
            Database.closeConnection(conn);
        }
    }

}