package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/11/15
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerDetails extends HttpServlet
{

    private static Logger log = new Logger(PartnerDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in List ListPartnerDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            log.debug("member is logout ");
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String partnerid=((String)session.getAttribute("merchantid"));
        Connection conn = null;
        int records=15;
        int pageno=1;

        String partnerId = null;
        String partnerName = null;
        String contact_persons=null;
        String contact_emails =null;
        String address=null;
        String country=null;
        String descriptor=null;
        String errorMessage = "";
        String EOL = "<BR>";
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            errorMessage = "<center><font class=\"text\" face=\"arial\"> "+errorMessage +e.getMessage()+EOL+"</font></center>" ;
            req.setAttribute("message",errorMessage);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerdetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }

        partnerId = req.getParameter("partnerId");
        partnerName = req.getParameter("partnerName");
        contact_persons=req.getParameter("contact_persons");
        contact_emails=req.getParameter("contact_emails");
        address=req.getParameter("address");
        country=req.getParameter("country");


        int start = 0; // start index
        int end = 0; // end index

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = partner.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = partner.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        ResultSet rs = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query = new StringBuffer("select partnerId,partnerName,contact_persons,contact_emails,address,country from partners where partnerId>0");
            StringBuffer countquery = new StringBuffer("select count(*) from partners where partnerId>0");


            if (partner.isValueNull(partnerId))
            {
                query.append(" and partnerId=" + ESAPI.encoder().encodeForSQL(me,partnerId));
                countquery.append(" and partnerId=" + ESAPI.encoder().encodeForSQL(me,partnerId));
            }
            if (partner.isValueNull(partnerName))
            {
                query.append(" and partnerName ='" + ESAPI.encoder().encodeForSQL(me,partnerName) + "'");
                countquery.append(" and partnerName ='" + ESAPI.encoder().encodeForSQL(me,partnerName) + "'");
            }

            /*if (isInputRequired != null)
            {
                query.append(" and isinputrequired='" + ESAPI.encoder().encodeForSQL(me,isInputRequired) + "'");
                countquery.append(" and isinputrequired='" + ESAPI.encoder().encodeForSQL(me,isInputRequired) + "'");
            }*/
            query.append(" order by partnerId desc LIMIT " + start + "," + end);


            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);

            RequestDispatcher rd = req.getRequestDispatcher("/partnerdetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);

        }
        catch (SystemError s)
        {
            log.error("System error while perform select query",s);
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.PARTNER_NAME);
        inputFieldsListMandatory.add(InputFields.PARTNER_ID);

        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
