import com.directi.pg.*;
import com.logicboxes.util.Util;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

public class ChargebackList extends HttpServlet
{

    private static Logger logger = new Logger(ChargebackList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in ChargebackList");
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        logger.debug("success");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        // RequestDispatcher rd = req.getRequestDispatcher("/chargebacklist.jsp");
        res.setContentType("text/html");
        StringBuffer errormsg1=new StringBuffer();
        String errormsg2="";
        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");
        String cc = Functions.checkStringNull(req.getParameter("SCc"));
        String authid=null;
        String captureid=null;
        String authCode=null;
        String referenceNo=null;
        String captureRRN="";
        int records=15;
        int pageno=1;


        logger.debug("authid::::"+req.getParameter("authid"));
        authid = getValidInput("authid", req.getParameter("authid"), "Numbers",200, true , errormsg1 );
        logger.debug("valid authid::::"+authid);
        logger.debug("captureid"+req.getParameter("captureid"));
        captureid = getValidInput("captureid", req.getParameter("captureid"), "Numbers",200, true, errormsg1);
        logger.debug("authcode"+req.getParameter("authcode"));
        authCode = getValidInput("authcode", req.getParameter("authcode"), "SafeString",200, true, errormsg1);
        logger.debug("referenceno"+req.getParameter("referenceno"));
        referenceNo = getValidInput("referenceno", req.getParameter("referenceno"), "Numbers",200, true, errormsg1);
        logger.debug("error messages "+ errormsg1.toString());
        logger.debug("error messages length "+ errormsg1.length());
        if(errormsg1.length()!=0)
        {
             logger.debug("invalid input forwarding to search page ");
             req.setAttribute("error",errormsg1.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/chargebacklist.jsp?ctoken="+user.getCSRFToken());
             rd.forward(req, res);
        }

        captureRRN = getCaptureRRNFromReferenceNo(referenceNo);
        cc = Functions.checkStringNull(req.getParameter("SCc"));

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            errormsg2 = errormsg1 + "Please enter valid PageNo.";
            req.setAttribute("error1",errormsg2);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        StringBuffer query = new StringBuffer("select T.status,T.icicitransid,T.transid,T.description,T.captureamount-T.refundamount as amount,T.authid,T.captureid,T.authcode,T.capturereceiptno,T.accountid from transaction_icicicredit as T ,members as M where icicitransid>1 and T.toid=M.memberid");
        StringBuffer countquery = new StringBuffer("select count(*) from transaction_icicicredit as T where icicitransid>1");

        if (functions.isValueNull(authid))
        {
            query.append(" and authid in (" + authid + ")");
            countquery.append(" and authid in(" + authid + ")");
        }

        if (functions.isValueNull(captureid))
        {
            query.append(" and captureid in(" + captureid + ")");
            countquery.append(" and captureid in(" + captureid + ")");
        }
        if (functions.isValueNull(authCode))
        {
            if (authCode.indexOf(",") != -1)
            {
                authCode = authCode.replaceAll(",", "','");
            }
            query.append(" and authcode in('" + authCode + "')");
            countquery.append(" and authcode in('" + authCode + "')");
        }

        if (functions.isValueNull(captureRRN))
        {
            query.append(" and capturereceiptno in(" + captureRRN + ")");
            countquery.append(" and capturereceiptno in(" + captureRRN + ")");
        }


        query.append(" and ( status='settled' or ( status='reversed' and captureamount>refundamount))  order by T.icicitransid desc LIMIT " + start + "," + end);
        countquery.append(" and ( status='settled' or ( status='reversed' and captureamount>refundamount))");

        
        Connection conn = null;
        ResultSet rs =  null;
        try
        {

            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            req.setAttribute("transdetails", hash);
            RequestDispatcher rd = req.getRequestDispatcher("/chargebacklist.jsp");
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("System error",se);
            out.println(Functions.ShowMessage("Error", "Internal System Error"));
        }
        catch (Exception e)
        {   logger.error("Exception Occure",e);
            out.println(Functions.ShowMessage("Error!", "Internal System Error"));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }

    private String getValidInput(String field, String parameter, String ruleType,int length, boolean mandatory ,StringBuffer errors)
    {
        String EOL = "<BR>";

        String validInput = null;
        if (parameter!=null && !parameter.equals(""))
        {
            String[] parameterArr = Util.getStringArray(parameter, ",");

            for (int i = 0; i < parameterArr.length; i++)
            {
                String validString = null;
                try
                {
                   validString= ESAPI.validator().getValidInput(field,parameterArr[i],ruleType,length,mandatory);
                }
                catch (ValidationException e)
                {
                       logger.error("Invalid input:::"+field+" ",e);
                    errors = errors.append("Please enter valid value for  "+ field+EOL);
                    return  validInput;
                }
                parameterArr[i] = validString;

            }
            validInput = Util.getDelimitedList(parameterArr, ",");
        }
        return validInput;


    }

    private String getCaptureRRNFromReferenceNo(String referenceNo)
    {
        String captureRRN = null;
        if (referenceNo != null)
        {
            String[] referenceNoArr = Util.getStringArray(referenceNo, ",");
            String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String singleDigitYear = year.substring(year.length() - 1);
            for (int i = 0; i < referenceNoArr.length; i++)
            {
                String refNo = referenceNoArr[i];
                referenceNoArr[i] = singleDigitYear + refNo.substring(11, refNo.length() - 1);
            }
            captureRRN = Util.getDelimitedList(referenceNoArr, ",");
        }
        return captureRRN;
    }

    public static void main(String[] args)
    {
        /*System.out.println(new ChargebackList().getCaptureRRNFromReferenceNo(Functions.checkStringNull(null)));*/
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
