import com.directi.pg.*;
import com.logicboxes.util.ApplicationProperties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Properties;

public class ConfirmBatch extends HttpServlet
{

    static Logger logger = new Logger(ConfirmBatch.class.getName());


    java.util.Date dt = null;
    ResultSet rs2 = null;

    public synchronized void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {   logger.debug("Entering in ConfirmBatch");
        String query = null;
        String icicimerchantid = null;
        String description = null;
        String contact_emails = null;
        int result = 0;
        int count = 0;
        int toid = 0;
        int prevtoid = 0;
        int trackingId = 0;
        int paymentTransId = 0;
        BigDecimal peramt = null;
        String supportEmail = ApplicationProperties.getProperty("SUPPORT_FROM_ADDRESS");
        PrintWriter out = response.getWriter();
        ServletContext application = getServletContext();
        response.setContentType("text/html");


        try
        {
            FileInputStream fis = new FileInputStream(application.getRealPath("") + "WEB-INF/lib/com/directi/pg/ICICI.properties");
            DataInputStream in = new DataInputStream(fis);
            Properties prop = new Properties();

            prop.load(in);
            fis.close();
            in.close();
            icicimerchantid = (String) prop.getProperty("MERCHANTID");
        }
        catch (Exception ex)
        {   logger.error("ICICI Merchanid Not Found",ex);

            return;
        }

        //reset variables
        toid = 0;
        prevtoid = 0;
        trackingId = 0;
        paymentTransId = 0;


        String podbatch = request.getParameter("podbatch");

        if (application.getAttribute(podbatch) != null)
        {
            out.println("This batch is already confirmed! podbatch : " + podbatch + "<br><br>");
            return;
        }

        application.setAttribute(podbatch, podbatch);

        Hashtable hash = new Hashtable();
        hash.put("icicicredit", "5");
        hash.put("icicicredit_type", "P"); //Leagle values : P for Percentage type F for Fixed type
        hash.put("icicicredit_individual_4", "4"); //Direct I
        hash.put("icicicredit_individual_81", "4"); //Charagdin
        hash.put("icicicredit_individual_170", "4"); //roopam
        hash.put("icicicredit_individual_321", "4.5"); //chitralekha
        //hash.put("icicicredit_individual_1","2"); //test

        String specificcharge = null;
        String defaultcharge = null;
        BigDecimal percent = null;
        count = 0;

        Connection conn = null;
        PreparedStatement p1=null;
        PreparedStatement p2 = null;
        PreparedStatement p3 = null;
        PreparedStatement p4 = null;
        PreparedStatement p5 = null;
        PreparedStatement p6 = null;
        PreparedStatement p7 = null;
        ResultSet rs=null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try
        {
            query = "select * from transaction_icicicredit where podbatch=? and status='podsent' order by toid";
            out.println(query + "<br><br>");
            conn = Database.getConnection();
            p1=conn.prepareStatement(query);
            p1.setString(1,podbatch);
            rs = p1.executeQuery();

            dt = new java.util.Date();

            StringBuffer body = new StringBuffer();
            body.append("This is to inform you that Funds for the following Transaction has been transfered Succesfully to your Account. \r\n\r\n");

            body.append("trackingid").append("\t").append("amount").append("\t").append("Transaction ID").append("\t").append("description").append("\r\n");
            body.append("-----------").append("\t").append("-------").append("\t").append("---------------").append("\t").append("-----------").append("\r\n");
            while (rs.next())
            {
                toid = rs.getInt("toid");
                description = rs.getString("description");
                trackingId = rs.getInt("icicitransid");
                count++;
                BigDecimal amt = new BigDecimal(rs.getString("amount"));
                amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
                specificcharge = (String) hash.get("icicicredit_individual_" + toid);
                defaultcharge = (String) hash.get("icicicredit");

                if (specificcharge == null)
                    percent = new BigDecimal(defaultcharge);
                else
                    percent = new BigDecimal(specificcharge);

                query = "insert into transactions (toid, totype, fromid, fromtype, description, amount, dtstamp) values (? , 'payment',? , 'icicicredit', ? , ?,?)";

                out.println(query + "<br>");
                p2=conn.prepareStatement(query);
                p2.setInt(1,toid);
                p2.setString(2,icicimerchantid);
                p2.setString(3,description);
                p2.setFloat(4,amt.floatValue());
                p2.setLong(5,(dt.getTime() / 1000));
                result = p2.executeUpdate();

                peramt = amt.multiply(percent.multiply(new BigDecimal(1 / 100.00)));
                query = "insert into transactions (toid, totype, fromid, fromtype, description, amount, dtstamp) values ('36' , 'payment',? , 'payment', 'Charges for ?' , ?,?)";

                out.println(query + "<br>");
                p3=conn.prepareStatement(query);
                p3.setInt(1,toid);
                p3.setString(2,description);
                p3.setFloat(3,peramt.floatValue());
                p3.setLong(4,(dt.getTime() / 1000));
                result =p3.executeUpdate();
                query = "select transid from transactions where toid=? and fromid=? and description=?";

                out.println(query + "<br>");
                p4=conn.prepareStatement(query);
                p4.setInt(1,toid);
                p4.setString(2,icicimerchantid);
                p4.setString(3,description);
                rs1 = p4.executeQuery();

                if (rs1.next())
                {
                    paymentTransId = rs1.getInt(1);
                    query = "update transaction_icicicredit set transid=?,status='settled',chargeper=? where icicitransid=?";
                    out.println(query + "<br>");
                    p5=conn.prepareStatement(query);
                    p5.setInt(1,paymentTransId);
                    p5.setInt(2,(percent.multiply(new BigDecimal("100"))).intValue());
                    p5.setInt(3,trackingId);
                    result = p5.executeUpdate();
                    logger.debug("Number of records updated: " + result);
                }
                else
                {
                    throw new SystemError("Error while executing query");
                }


                logger.debug("toid " + toid);
                logger.debug("prevtoid " + prevtoid);


                if ((toid != prevtoid && prevtoid != 0))
                {
                    query = "select contact_emails from members where memberid=?";

                    out.println(query + "<br>");
                    p6=conn.prepareStatement(query);
                    p6.setInt(1,prevtoid);
                    rs2 = p6.executeQuery();

                    if (rs2.next())
                    {
                        contact_emails = rs2.getString(1);
                    }

                    logger.debug("calling SendMAil for Merchant- Confirm Batch");
                    Mail.sendmail(contact_emails, supportEmail, "CONFIRMATION OF TRANSFER OF FUNDS", body.toString());
                    logger.debug("called SendMAil for Merchant-- Confirm Batch");

                    //reset mail body
                    body = new StringBuffer();
                    body.append("This is to inform you that Funds for the following Transaction has been transfered Succesfully to your Account. \r\n\r\n");
                    body.append("trackingid").append("\t").append("amount").append("\t").append("Transaction ID").append("\t").append("description").append("\r\n");
                    body.append("-----------").append("\t").append("-------").append("\t").append("---------------").append("\t").append("-----------").append("\r\n");
                    body.append(trackingId).append("\t").append(amt).append("\t\t").append(paymentTransId).append("\t\t").append(description).append("\r\n");
                }
                else
                {
                    body.append(trackingId).append("\t").append(amt).append("\t\t").append(paymentTransId).append("\t\t").append(description).append("\r\n");
                }
                prevtoid = toid;
            }
            if (count >= 1)
            {
                query = "select contact_emails from members where memberid=?";
                out.println(query + "<br>");
                p7=conn.prepareStatement(query);
                p7.setInt(1,prevtoid);
                rs2 = p7.executeQuery();
                if (rs2.next())
                {
                    contact_emails = rs2.getString(1);
                }
                logger.debug("calling SendMAil for Merchant- - Confirm Batch");
                Mail.sendmail(contact_emails, supportEmail, "CONFIRMATION OF TRANSFER OF FUNDS", body.toString());
                logger.debug("called SendMAil for Merchant-- - Confirm Batch");
            }
            out.println("total settlement count" + count);
        }
        catch (SystemError se)
        {
            logger.error("System Error in ConfirmBatch",se);
            out.println(Functions.ShowMessage("Error", se.toString() + "<br><br>Query :" + query));
        }
        catch (Exception e)
        {
            logger.error("Exception in ConfirmBatch",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(rs1);
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closePreparedStatement(p3);
            Database.closePreparedStatement(p4);
            Database.closePreparedStatement(p5);
            Database.closePreparedStatement(p6);
            Database.closePreparedStatement(p7);
            Database.closeConnection(conn);
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        service(request, response);
    }
}

