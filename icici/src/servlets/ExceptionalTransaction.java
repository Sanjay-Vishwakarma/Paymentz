import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jun 13, 2012
 * Time: 7:24:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionalTransaction extends HttpServlet
{
    Connection cn = null;

    static Logger logger = new Logger(ExceptionalTransaction.class.getName());

        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            doPost(request, response);
        }

         public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
        {
            HttpSession session = req.getSession();
            Functions functions = new Functions();
            ResultSet rs = null;

            User user =  (User)session.getAttribute("ESAPIUserSessionKey");
            if(Functions.validateCSRF(req.getParameter("ctoken"),user))
            {
                session.setAttribute("ESAPIUserSessionKey",user);
            }
            else
            {
                res.sendRedirect("/icici/logout.jsp");
                return;
            }
            logger.debug("success");

            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/logout.jsp");
                return;
            }
            ServletContext application = getServletContext();
            String errormsg = "";
            String EOL = "<BR>";
            boolean flag = true;
            try
            {
                validateOptionalParameter(req);
                validateMandatoryParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Input valid value",e);
                errormsg = errormsg + "Please Enter valid value "+EOL;
                flag = false;
            }
            String searchstatus = req.getParameter("searchstatus");
            String searchType = req.getParameter("searchType");
            String searchId = req.getParameter("SearchId");

            int start = 0; // start index
            int end = 0; // end index


        //Add Date Filter start
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invelid description",e);
            }
            finally
            {
                Database.closeConnection(cn);
            }

            String fdate = req.getParameter("fdate");
            String tdate = req.getParameter("tdate");
            String fmonth = req.getParameter("fmonth");
            String tmonth = req.getParameter("tmonth");
            String fyear = req.getParameter("fyear");
            String tyear = req.getParameter("tyear");

            Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
        if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);

        if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        fdtstamp= ESAPI.encoder().encodeForSQL(me,fdtstamp);
        tdtstamp=ESAPI.encoder().encodeForSQL(me,tdtstamp);
        searchId=ESAPI.encoder().encodeForSQL(me,searchId);
        //date filter end

        if(searchstatus==null || searchstatus.equals(""))
        {
            errormsg = errormsg + "Please Select  Search Status  "+EOL;
            flag = false;
        }

        if(searchType!=null && (searchId==null || searchId.equals("")))
        {
            errormsg = errormsg + "Please Enter Search Id  "+EOL;
            flag = false;
        }

        logger.debug("Entering in ExceptionalTransaction");
        PrintWriter out = res.getWriter();
        int pageno=1;
        int records=30;
            try
            {
                validateOptionalParameter(req);
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
                pageno = 1;
                records = 15;
            }
            pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
            records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        // calculating start & end
        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;
        //Serching through Merchant ID Start
        if(flag==true)
        {

           Hashtable isoCodeMap = new   Hashtable();
         try
        {
            cn = Database.getConnection();
            Statement stmnt = cn.createStatement();
            String query = "select code,reason from rs_codes";
            rs = stmnt.executeQuery(query);

            isoCodeMap = Functions.getDetailedHashFromResultSet(rs);

        }
        catch (Exception e)
        {
            logger.error("Error while loading Exceptional Transaction",e);
        }
         finally
         {
             Database.closeResultSet(rs);
             Database.closeConnection(cn);
         }

        if(searchstatus.equals("authfailed"))
        {
            logger.debug("Enter in authfailed");
            StringBuffer query = new StringBuffer("select icicitransid,icicimerchantid,toid,accountid,amount,status,date_format(from_unixtime(dtstamp),'%d/%m/%Y') as \"Transaction Date\",timestamp,authqsiresponsecode,authqsiresponsedesc from transaction_icicicredit where status='authfailed'");
            StringBuffer count=new StringBuffer("select count(*) from transaction_icicicredit where status='authfailed'");


            if(searchType!=null && searchType.equals("Tracking ID") )
            {
                query.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }


            if(searchType!=null && searchType.equals("Merchant ID") )
            {
                query.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and dtstamp >= " + fdtstamp);
                count.append(" and dtstamp >= " + fdtstamp);
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " + tdtstamp);
                count.append(" and dtstamp <= " + tdtstamp);
            }

            query.append(" order by icicitransid DESC limit " + start + "," + end);

            //logger.debug(query.toString());
        try
        {
            cn = Database.getConnection();
            hash = Database.getHashFromResultSetForExceptionalTrans(Database.executeQuery(query.toString(),cn),isoCodeMap);

            logger.debug("record"+hash);
            rs = Database.executeQuery(count.toString(), cn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

        }
        catch (Exception e)
        {   logger.error("SQL",e);
            out.println(Functions.ShowMessage("Error!", e.toString()));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
        }
        }

        if(searchstatus.equals("authstarted") )
        {
            StringBuffer query = new StringBuffer("select icicitransid,icicimerchantid,toid,accountid,amount,status,date_format(from_unixtime(dtstamp),'%d/%m/%Y') as \"Transaction Date\",timestamp,authqsiresponsecode,authqsiresponsedesc from transaction_icicicredit where status='authstarted'");
            StringBuffer count = new StringBuffer("select count(*) from transaction_icicicredit where status='authstarted'");

            if(searchType!=null && searchType.equals("Tracking ID") )
            {
                query.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }

            if(searchType!=null && searchType.equals("Merchant ID") )
            {
                query.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                count.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
            }
            if (functions.isValueNull(fdtstamp))
            {
                 query.append(" and dtstamp >= " + fdtstamp);
                 count.append(" and dtstamp >= " + fdtstamp);
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and dtstamp <= " + tdtstamp);
                count.append(" and dtstamp <= " + tdtstamp);
            }
            query.append(" order by icicitransid DESC limit " + start + "," + end);


                try
                {
                    cn = Database.getConnection();

                    hash = Database.getHashFromResultSetForExceptionalTrans(Database.executeQuery(query.toString(),cn),isoCodeMap);
                    //logger.debug(query.toString());
                    logger.debug("record"+hash);
                    rs = Database.executeQuery(count.toString(), cn);
                    rs.next();
                    int totalrecords = rs.getInt(1);

                    hash.put("totalrecords", "" + totalrecords);
                    hash.put("records", "0");

                    if (totalrecords > 0)
                        hash.put("records", "" + (hash.size() - 2));
                }
                catch (Exception e)
                {
                    out.println(Functions.ShowMessage("Error!", e.toString()));
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closeConnection(cn);
                }
                }


             if(searchstatus.equals("reversal"))
                {
                StringBuffer query = new StringBuffer("select icicitransid,icicimerchantid,toid,accountid,amount,status,date_format(from_unixtime(dtstamp),'%d/%m/%Y') as \"Transaction Date\",timestamp,refundqsiresponsecode,refundqsiresponsedesc from transaction_icicicredit where status='markedforreversal'");
                StringBuffer count = new StringBuffer("select count(*) from transaction_icicicredit where status='markedforreversal'");

                    if(searchType!=null && searchType.equals("Tracking ID") )
                    {
                        query.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                        count.append("  and icicitransid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                    }

                    if(searchType!=null && searchType.equals("Merchant ID") )
                    {
                        query.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                        count.append("  and toid='"+ ESAPI.encoder().encodeForSQL(me,searchId)+"'");
                    }

                    if (functions.isValueNull(fdtstamp))
                    {
                        query.append(" and dtstamp >= " + fdtstamp);
                        count.append(" and dtstamp >= " + fdtstamp);
                    }
                    if (functions.isValueNull(tdtstamp))
                    {
                        query.append(" and dtstamp <= " + tdtstamp);
                        count.append(" and dtstamp <= " + tdtstamp);
                    }

                    query.append(" order by icicitransid DESC limit " + start + "," + end);

                try
                {
                    cn = Database.getConnection();

                    hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(),cn));
                    //logger.debug(query.toString());
                    logger.debug("record"+hash);
                    rs = Database.executeQuery(count.toString(), cn);
                    rs.next();
                    int totalrecords = rs.getInt(1);

                    hash.put("totalrecords", "" + totalrecords);
                    hash.put("records", "0");

                    if (totalrecords > 0)
                        hash.put("records", "" + (hash.size() - 2));

                }
                catch (Exception e)
                {
                    out.println(Functions.ShowMessage("Error!", e.toString()));
                }
                finally
                {
                    Database.closeResultSet(rs);
                    Database.closeConnection(cn);
                }
                }
        //Serching through Merchant ID END

           req.setAttribute("exceptionaltransaction", hash);
           req.setAttribute("SearchId",searchId);
           req.setAttribute("searchstatus",searchstatus);
           RequestDispatcher rd = req.getRequestDispatcher("/exceptionaltransaction.jsp");
           rd.forward(req, res);
        }
        else
        {
            req.setAttribute("error",errormsg);
            logger.debug(errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/exceptionaltransaction.jsp");
            rd.forward(req, res);
        }
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputFieldsListMandatory.add(InputFields.SEARCH_TYPE);
        inputFieldsListMandatory.add(InputFields.SEARCH_ID);
        inputFieldsListMandatory.add(InputFields.FROMDATE);
        inputFieldsListMandatory.add(InputFields.TODATE);
        inputFieldsListMandatory.add(InputFields.FROMMONTH);
        inputFieldsListMandatory.add(InputFields.TOMONTH);
        inputFieldsListMandatory.add(InputFields.FROMYEAR);
        inputFieldsListMandatory.add(InputFields.TOYEAR);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.SEARCH_STATUS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
