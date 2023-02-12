import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
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
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
public class MerchantDetails extends HttpServlet
{
    static Logger log = new Logger(MerchantDetails.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        String memberid = null;
        PrintWriter out = res.getWriter();

        try
        {
            memberid = ESAPI.validator().getValidInput("memberid", req.getParameter("memberid"), "Numbers", 10, true);
        }
        catch(ValidationException e)
        {
            log.error("Invalid Input ",e);
            out.println(Functions.ShowMessage("Error", "INVALID MemberId"));
        }
        log.debug("member id is :::::::"+memberid);
        if (memberid == null || memberid.equals(""))
            doPost(req, res);

        try
        {
            Hashtable hash = getMerchantDetails(memberid);
            req.setAttribute("merchantdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/merchantDetails.jsp");
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            log.error("SystemError:::::",se);
            StringWriter sw = new StringWriter();

            out.println(Functions.ShowMessage("Error", sw.toString()));
        }catch (Exception e){
            log.error("Exception in doGet::::",e);
            StringWriter sw = new StringWriter();
            out.println(Functions.ShowMessage("Error", sw.toString()));
        }
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        boolean flag = false;
        String errormsg="";

        String ignoredates = Functions.checkStringNull(req.getParameter("ignoredates"));
        String perfectmatch = Functions.checkStringNull(req.getParameter("perfectmatch"));

        String memberid = null;
        String company_name = null;
        String sitename = null;
        String activation =null;
        String icici =null;
        String reserves = null;
        String chargeper = null;
        String fixamount =Functions.checkStringNull(req.getParameter("fixamount"));

        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;

        try
        {
            memberid = ESAPI.validator().getValidInput("memberid", req.getParameter("memberid"), "Numbers", 10, true);
            company_name = ESAPI.validator().getValidInput("company_name",req.getParameter("company_name"),"SafeString",50,true);
            sitename = ESAPI.validator().getValidInput("sitename",req.getParameter("sitename"),"URL",50,true);
            activation = ESAPI.validator().getValidInput("activation",req.getParameter("activation"),"Statues",10,true);
            icici  = ESAPI.validator().getValidInput("icici",req.getParameter("icici"),"Statues",10,true);
            reserves =ESAPI.validator().getValidInput("reserves",req.getParameter("reserves"),"SafeString",50,true);
            chargeper = ESAPI.validator().getValidInput("chargeper",req.getParameter("chargeper"),"Numbers",10,true);
            fdate = ESAPI.validator().getValidInput("fdate",req.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",req.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",req.getParameter("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",req.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",req.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",req.getParameter("tyear"),"Years",4,true);
        }
        catch(ValidationException e)
        {
            log.error("Invelid description",e);
        }

        Calendar rightNow = Calendar.getInstance();

        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
        if (fmonth == null) fmonth = "" + (rightNow.get(Calendar.MONTH));
        if (tmonth == null) tmonth = "" + (rightNow.get(Calendar.MONTH));
        if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
        if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

        String fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
        String tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

        res.setContentType("text/html");
        req.setAttribute("fdtstamp", fdtstamp);
        req.setAttribute("tdtstamp", tdtstamp);

        PrintWriter out = res.getWriter();

        int pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        int records = Functions.convertStringtoInt(req.getParameter("SRecords"), 30);

        try
        {
            Hashtable hash = listMembers(memberid, company_name, sitename, activation, icici, reserves, chargeper, fixamount, fdtstamp, tdtstamp, records, pageno, ignoredates, perfectmatch);
            req.setAttribute("merchantsdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/merchants.jsp");
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            log.error("SystemError in fatch record",se);
            out.println(Functions.ShowMessage("Error", "Internal System Error"));
        }
        catch (Exception e)
        {
            log.error("Exception in fatch record",e);
            out.println(Functions.ShowMessage("Error", "Internal System Error"));
        }
    }

    public Hashtable listMembers(String memberid, String company_name, String sitename, String activation, String icici, String reserves, String chargeper, String fixamount, String fdtstamp, String tdtstamp, int records, int pageno, String ignoredates, String perfectmatch) throws SystemError
    {
        log.debug("Entering listMembers");
        Hashtable hash = null;

        int start = 0; // start index
        int end = 0; // end index
        start = (pageno - 1) * records;
        end = records;

        Connection  cn = null;
        try{ Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("select memberid,company_name,sitename,activation,icici,reserves/100 as reserves,chargeper/100 as chargeper from members where 1=1 ");
            StringBuffer countquery = new StringBuffer("select count(*) from members where 1=1 ");
            StringBuffer condQuery = new StringBuffer();
            if (memberid != null)
                condQuery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me,memberid));
            if (company_name != null){
                if (perfectmatch == null)
                    condQuery.append(" and company_name like '%" + ESAPI.encoder().encodeForSQL(me,company_name) + "%'");
                else
                    condQuery.append(" and company_name='" + ESAPI.encoder().encodeForSQL(me,company_name) + "'");
            }
            if (sitename != null){
                if (perfectmatch == null)
                    condQuery.append(" and sitename like '%" + ESAPI.encoder().encodeForSQL(me,sitename) + "%'");
                else
                    condQuery.append(" and sitename='" + ESAPI.encoder().encodeForSQL(me,sitename) + "'");
            }
            if (activation != null)
                condQuery.append(" and activation='" + ESAPI.encoder().encodeForSQL(me,activation) + "'");
            if (icici != null)
                condQuery.append(" and icici='" + ESAPI.encoder().encodeForSQL(me,icici) + "'");
            if (reserves != null)
                condQuery.append(" and reserves=" + ESAPI.encoder().encodeForSQL(me,reserves));
            if (chargeper != null)
                condQuery.append(" and chargeper=" + ESAPI.encoder().encodeForSQL(me,chargeper));
            if(ignoredates == null){
                if (fdtstamp != null)
                    condQuery.append(" and dtstamp >= " + ESAPI.encoder().encodeForSQL(me,fdtstamp));
                if (tdtstamp != null)
                    condQuery.append(" and dtstamp <= " + ESAPI.encoder().encodeForSQL(me,tdtstamp));
            }
            query.append(condQuery);
            //query.append(" order by activation,company_name ");
            query.append(" order by memberid asc ");
            query.append(" limit " + start + "," + end);

            countquery.append(condQuery);


            cn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), cn));
            ResultSet rs = Database.executeQuery(countquery.toString(), cn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }
        finally
        {
            Database.closeConnection(cn);
        }

        log.debug("Leaving listMembers");
        return hash;
    }

    public Hashtable getMerchantDetails(String memberid) throws SystemError
    {
        log.debug("Entering getMemberDetails");
        Hashtable hash = null;
        String query="";
        Connection con=Database.getConnection();
        try
        {
            query = "select memberid, company_name, contact_persons,contact_emails,notifyemail,sitename,brandname,telno,faxno,address,city,state,zip,country,activation,icici,date_format(from_unixtime(dtstamp),'%d-%m-%Y') as actDate,date_format(from_unixtime(unix_timestamp(timestmp)),'%d-%m-%Y') as modDate from members where memberid =?";
            PreparedStatement pstmt=con.prepareStatement(query.toString());
            pstmt.setString(1,memberid);
            hash = Database.getHashFromResultSet(pstmt.executeQuery());
        }
        catch (SQLException se)
        {
            throw new SystemError(se.toString());
        }

        int mId=0;
        try
        {
            mId=Integer.parseInt(memberid);
        }
        catch (Exception e)
        {
        }
        hash.put("balance",new TransactionEntry(mId).getBalance());


        log.debug("Leaving getMemberDetails");
        return hash;
    }

}
