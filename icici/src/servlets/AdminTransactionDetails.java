import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class AdminTransactionDetails extends HttpServlet
{

    private static Logger log = new Logger(AdminTransactionDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        TransactionManager transactionManager = new TransactionManager();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        boolean flag=false;
        String errormsg="";
        String EOL = "<BR>";

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        int pageno=1;
        int records=30;
        String perfectmatch = Functions.checkStringNull(req.getParameter("perfectmatch"));

        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            log.error("Invelid description",e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+ errormsg + e.getMessage() + EOL + "</b></font></center>";
            flag = false;

            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/adminTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String name = req.getParameter("name");
        String desc = req.getParameter("desc");
        String orderdesc = req.getParameter("orderdesc");
        String trackingid = req.getParameter("STrackingid");
        String status = req.getParameter("status");
        String fdate = req.getParameter("fdate");
        String tdate = req.getParameter("tdate");
        String fmonth = req.getParameter("fmonth");
        String tmonth =  req.getParameter("tmonth");
        String fyear = req.getParameter("fyear");
        String tyear = req.getParameter("tyear");
        String gateway = req.getParameter("gateway");
        req.setAttribute("gateway",gateway);
        Functions functions=new Functions();
        if(!functions.isValueNull(trackingid))
        {
            if(!functions.isValueNull(orderdesc))
            {
                req.setAttribute("errormessage","Kindly provide value in Trackingid OR Order Description field");
                RequestDispatcher rd = req.getRequestDispatcher("/adminTransactionDetails.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
        }
        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + (rightNow.get(rightNow.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(rightNow.MONTH));

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");


        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        PrintWriter out = res.getWriter();

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

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        try
        {
            Set<String> gatewayTypeSet = getGatewayHash(gateway);
            Hashtable hash = transactionManager.getTransactionList(trackingid, name, desc, orderdesc, tdtstamp, fdtstamp, status, records, pageno, perfectmatch,gatewayTypeSet);
             String getterminalidId = getterminalidIds(trackingid);
            req.setAttribute("transactionsdetails", hash);
            req.setAttribute("terminalid", getterminalidId);
            //log.info(hash);
            hash=null;
            RequestDispatcher rd = req.getRequestDispatcher("/adminTransactionDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (Exception e)
        {
            log.error("Exception ::::",e);
            out.println(Functions.ShowMessage("Error", "Internal Error While listing Transaction"));
        }
    }

    public Set<String> getAccountIds(String gateway) throws SystemError
    {
        Functions functions = new Functions();
        Set<String> accountIds = new HashSet<String>();
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        if(functions.isValueNull(gateway))
        {
            try
            {
                conn = Database.getRDBConnection();
                query = new StringBuffer("select A.accountid from gateway_accounts A, gateway_type T where A.pgtypeid = T.pgtypeid and T.gateway=?");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,gateway);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    accountIds.add((String)rs.getString("accountid"));
                }
            }
            catch (SQLException se)
            {
                throw new SystemError(se.toString());
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        return accountIds;
    }

    public String getterminalidIds(String trackingid) throws SystemError
    {
        Functions functions = new Functions();
        String terminalid = "";
        StringBuffer query = new StringBuffer();
        Hashtable hash = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt=null;
        if(functions.isValueNull(trackingid))
        {
            try
            {
                conn = Database.getRDBConnection();
                query = new StringBuffer("select terminalid from transaction_common where trackingid=? ");
                pstmt=conn.prepareStatement(query.toString());
                pstmt.setString(1,trackingid);
                rs = pstmt.executeQuery();
                while(rs.next())
                {
                    terminalid = rs.getString("terminalid");
                }
            }
            catch (SQLException se)
            {
                throw new SystemError(se.toString());
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        return terminalid;
    }

    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.NAME_TRA);
        inputFieldsListMandatory.add(InputFields.DESC);
        inputFieldsListMandatory.add(InputFields.ORDERDESC);
        inputFieldsListMandatory.add(InputFields.TRACKINGID_TRA);
        //inputFieldsListMandatory.add(InputFields.STATUS);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);
        //inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
