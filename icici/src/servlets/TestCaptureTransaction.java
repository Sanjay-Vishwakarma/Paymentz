import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class TestCaptureTransaction extends HttpServlet
{

    static Logger logger = new Logger(TestCaptureTransaction.class.getName());

    public TestCaptureTransaction()
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
        logger.debug("Entering in TestCaptureTransaction");
        PrintWriter pWriter = res.getWriter();
        //res.setContentType("text/plain");

        String memberid = (String) req.getParameter("merchantid");
        String descriptions = (String) req.getParameter("description");
        String captureamounts = (String) req.getParameter("captureamount");
        String checksum = req.getParameter("checksum");
        String dummystatus = req.getParameter("status");
        if (dummystatus == null)
            dummystatus = "capturesuccess";

        StringBuffer status = new StringBuffer(" ");
        StringBuffer newdescription = new StringBuffer(" ");
        StringBuffer chargeamt = new StringBuffer(" ");

        String key = "";
        String checksumAlgorithm = "";
        String description = "";
        String tempstatus = "";
        String tempdescription = "";
        Hashtable hashReceived = new Hashtable();
        Hashtable hashAvailable = new Hashtable();
        Connection conn = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);


        try
        {
            conn = Database.getConnection();
            String s1="select clkey,checksumalgo from members where memberid=?";
            pstmt=conn.prepareStatement(s1);
            pstmt.setString(1,memberid);
            rs = pstmt.executeQuery();

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

                while (descriptionsstz.hasMoreElements())
                {
                    description = descriptionsstz.nextToken();

                    hashAvailable.put(description, dummystatus);
                }

                hashReceived.putAll(hashAvailable); // This will over write values

                Enumeration resultenu = hashReceived.keys();

                float amt = 0;
                try
                {
                    if (captureamounts != null)
                        amt = Float.parseFloat(captureamounts);
                }
                catch (Exception e)
                {
                }

                while (resultenu.hasMoreElements())
                {
                    tempdescription = (String) resultenu.nextElement();
                    tempstatus = (String) hashReceived.get(tempdescription);
                    float tranFee = amt * (float) 0.015;

                    pWriter.print(tempdescription + "=" + tempstatus + "=" + tranFee + "&");
                    status.append(tempstatus + ",");
                    newdescription.append(tempdescription + ",");
                    chargeamt.append(tranFee + ",");
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
        {   logger.error("Exception:::::::: in TestCaptureTransaction",ex);
            pWriter.print(sw.toString());
        }
        finally {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
    }

}



