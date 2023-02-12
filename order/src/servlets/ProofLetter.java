import com.directi.pg.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Hashtable;

public class ProofLetter extends HttpServlet {
    /**
     * It is used as the start tag in the replaceData function.
     */

    static String startTag = "<#";

    /**
     * It is the length of the start tag.
     */

    static int startTagLength = 2;

    /**
     * It is used as the end tag in the replaceData function.
     */

    static String endTag = "#>";

    /**
     * It is the length of the end tag.
     */

    static int endTagLength = 2;

    /**
     * Sole constructor.
     */


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int start = 0; // start index
        int end = 0; // end index
        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;

        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        ServletContext application = getServletContext();



        String ccnum = (String) req.getAttribute("ccnum");
        String trackingid = req.getParameter("trackingid");

        //out.println(ccnum);


        if (ccnum == null) {
            out.println(Functions.NewShowConfirmation("Error", "CCnum not available"));
            return;
        }


        StringBuffer query = new StringBuffer("select T.*,M.memberid,M.company_name,M.brandname,M.sitename,M.currency from transaction_icicicredit as T, members as M where");
        query.append(" T.icicitransid=?");
        query.append(" and T.ccnum =?");
        query.append(" and T.status='proofrequired'");
        query.append(" and T.toid=M.memberid");

        Connection conn = null;
        try {

            conn = Database.getConnection();
            //out.println(letter);
            Hashtable taghash = new Hashtable();
            PreparedStatement pstmt=conn.prepareStatement(query.toString());
            pstmt.setString(1,trackingid);
            pstmt.setString(2, PzEncryptor.decryptPAN(ccnum));
            ResultSet rsdet = pstmt.executeQuery();

            if (rsdet.next()) {
                String companyname = rsdet.getString("company_name");
                String brandname = rsdet.getString("brandname");
                String sitename = rsdet.getString("sitename");
                String currency = rsdet.getString("currency");
                String memberId = rsdet.getString("memberid");

                if (brandname.trim().equals(""))
                    brandname = companyname;

                if (sitename.trim().equals(""))
                    sitename = companyname;

                taghash.put("NAMEONCARD", rsdet.getString("name"));
                taghash.put("COMPANYNAME", companyname);
                taghash.put("BRANDNAME", brandname);
                taghash.put("SITENAME", sitename);
                taghash.put("CURRENCY", currency);
                taghash.put("DATE", (new java.util.Date()).toString());
                taghash.put("AMOUNT", rsdet.getString("amount"));
                taghash.put("CCNUM", Functions.decryptString(rsdet.getString("ccnum")));
                taghash.put("DESRIPTION", rsdet.getString("description"));
                taghash.put("TRACKINGID", rsdet.getString("icicitransid"));

                out.println(Template.getProofPage(memberId, taghash));
                ccnum=null;

            } else {
                out.println(Functions.NewShowConfirmation("Error", " Couldn't fetch Details."));
            }


        }
        catch (SystemError se) {
            //StringWriter sw=new StringWriter();
            //se.printStackTrace(new PrintWriter(sw));
            out.println(Functions.NewShowConfirmation("Error", se.toString()));

        }
        catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            out.println(Functions.NewShowConfirmation("Error!", sw.toString()));
        }
        finally {
            Database.closeConnection(conn);
        }
    }
    public static String replaceTag(String data, Hashtable values)
    {
        // cat.info("In replaceData");
        StringBuffer sb = new StringBuffer();
        int startPos = 0;
        int startTagPos = 0;
        int endTagPos = 0;
        int length = data.length();
        String value = null;
        // cat.info("Before While");
        while (startPos < length) {
            startTagPos = data.indexOf(startTag, startPos);
            if (startTagPos == -1) {
                sb.append(data.substring(startPos));
                startPos = length + 1;
            } else {
                sb.append(data.substring(startPos, startTagPos));
                endTagPos = data.indexOf(endTag, startTagPos);
                value = (String) values.get(data.substring(startTagPos + startTagLength, endTagPos));
                //System.out.println(data.substring(startTagPos+startTagLength,endTagPos)+" : "+value);
                if (value != null) {
                    sb.append(value);
                }
                startPos = endTagPos + endTagLength;
            }
        }
        // cat.info("Leaving replaceData");
        return sb.toString();
    }
}