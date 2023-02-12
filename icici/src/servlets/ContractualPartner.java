

import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
 * Created by Swamy on 25/4/2017.
 */
public class ContractualPartner extends HttpServlet
{
    private static Logger logger = new Logger(ContractualPartner.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Inside ContractualPartner.java");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rdError=request.getRequestDispatcher("/contractualPartner.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/contractualPartner.jsp?Success=YES&ctoken=" + user.getCSRFToken());

        Functions functions = new Functions();

        String error = "";

        int records=15;
        int pageno=1;

        /*error = error + validateMandatoryParameters(request);

        if(functions.isValueNull(error))
        {
            request.setAttribute("error",error);
            rdError.forward(request, response);
            return;
        }*/

        int start = 0; // start index
        int end = 0; // end index

        String partnerId = null;
        String bankName = null;

        if(!request.getParameter("partnerid").equals("0"))
        {
            partnerId= request.getParameter("partnerid");
            request.setAttribute("partnerid",partnerId);
        }

        if(!request.getParameter("bankName").equals("0"))
        {
            bankName= request.getParameter("bankName");
            request.setAttribute("bankName",bankName);
        }

        //System.out.println("PartnerId--->"+partnerId+"---BankName----"+bankName);

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection con = null;
        ResultSet rs = null;
        Hashtable hash = null;

        try
        {
            con = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select partnerid,bankname,contractual_partnerid,contractual_partnername FROM contractualpartner_appmanager WHERE partnerid > 0");
            StringBuffer countquery = new StringBuffer("select count(*) FROM contractualpartner_appmanager WHERE partnerid > 0");

            if(functions.isValueNull(partnerId))
            {
                query.append(" and partnerid =" + ESAPI.encoder().encodeForSQL(me,partnerId));
                countquery.append(" and partnerid =" + ESAPI.encoder().encodeForSQL(me,partnerId));
            }

            if(functions.isValueNull(bankName))
            {
                query.append(" and bankname = '"+ESAPI.encoder().encodeForSQL(me,bankName)+"'");
                countquery.append(" and bankname = '"+ESAPI.encoder().encodeForSQL(me,bankName)+"'");
            }

            query.append(" order by partnerid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), con));

            rs = Database.executeQuery(countquery.toString(), con);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            request.setAttribute("transdetails", hash);

        }
        catch (SystemError systemError)
        {
            logger.error("SystemError in ContractualPartner.java------"+ systemError.getMessage());
        }
        catch (SQLException e)
        {
            logger.error("SQLException in ContractualPartner.java------"+ e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(con);
        }
        rdSuccess.forward(request, response);
    }

    private String validateMandatoryParameters(HttpServletRequest request)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.RECORDS);
        inputFieldsListOptional.add(InputFields.PAGENO);
        inputFieldsListOptional.add(InputFields.CONTRACTUAL_PARTNER_ID);
        inputFieldsListOptional.add(InputFields.CONTRACTUAL_PARTNER_NAME);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(request,inputFieldsListOptional,errorList,false);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
}
