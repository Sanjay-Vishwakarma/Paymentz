import com.directi.pg.*;
import com.directi.pg.core.GatewayTypeService;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class AdminReverseList extends HttpServlet
{

    private static Logger logger = new Logger(AdminReverseList.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in AdminReverseList ");

        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions = new Functions();
        int start = 0; // start index
        int end = 0; // end index

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String str = null;
        String key = null;
        String checksum = null;
        String merchantid = null;
        String errormsg = "";
        res.setContentType("text/html");

        PrintWriter out = res.getWriter();
        String data = req.getParameter("data");
        String cc = Functions.checkStringNull(req.getParameter("SCc"));
        String description=null;
        String trakingid=null;
        int records=15;
        int pageno=1;
        String gateway="";
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
          logger.error("Invalid input",e);
          errormsg = errormsg + "Please Enter valid value in following field.";
        }
        description = req.getParameter("SDescription");
        trakingid = req.getParameter("STrakingid");
        gateway = req.getParameter("gateway");

        if(errormsg!="")
        {   logger.debug("ENTER VALID DATA");
            req.setAttribute("error",errormsg);
        }

        //Functions fn=new Functions();
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
        Set<String> gatewayTypeSet = getGatewayHash(gateway);

        //	out.println("merchantid "+ merchantid);
        logger.debug("Execute select query for fatch records ");
        Hashtable hash = null;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        String tablename="";
        StringBuffer query = new StringBuffer();

        try
        {
            String field="";
            Iterator i = gatewayTypeSet.iterator();
            while(i.hasNext())
            {
                tablename = Database.getTableName((String)i.next());
                if(tablename.equals("transaction_icicicredit"))
                {   field="icicitransid";
                    query.append("select T.status,T.icicitransid,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.accountid,T.icicimerchantid,M.company_name from transaction_icicicredit as T,members as M where icicitransid>0");
                }
                else
                {   field="trackingid";
                    query.append("select T.status,T.trackingid as icicitransid,T.transid,T.description,T.amount,T.captureamount,T.refundamount,T.accountid,T.fromid as icicimerchantid,M.company_name from "+tablename+" as T,members as M where trackingid>0");
                }


          if (functions.isValueNull(description))
        {
            query.append(" and description like '%" + ESAPI.encoder().encodeForSQL(me,description) + "%'");
        }

        if (functions.isValueNull(trakingid))
        {
            query.append(" and "+field+"=" + ESAPI.encoder().encodeForSQL(me,trakingid));
        }


        query.append(" and status='markedforreversal' and T.toid=M.memberid ");

                if(i.hasNext())
                    query.append(" UNION ");

            
            
            }
            query.append(" order by icicitransid desc");
            }
            catch(Exception e)
            {
                logger.error("Error occured ",e);
            }

        //	out.println("query "+ query);
        //	out.println("countquery "+ countquery);
        Connection conn = null;
        ResultSet rs=null;
        try
        {   logger.debug("Query executed is -----"+query.toString());
            StringBuffer countquery = new StringBuffer("select count(*) from ( " + query + ") as temp ");

            conn = Database.getConnection();
            hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), conn));
            //	out.println(hash.toString());

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            //	out.println(hash.toString());
            req.setAttribute("transdetails", hash);

            RequestDispatcher rd = req.getRequestDispatcher("/adminreverselist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {
            logger.error("System error in AdminReverseList",se);
            out.println(Functions.ShowMessage("Error", "System Error Occur"));
            //System.out.println(se.toString());
        }
        catch (Exception e)
        {   logger.error("Exception in AdminReverseList",e);
            out.println(Functions.ShowMessage("Error!", "Internal System Error"));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
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

        inputFieldsListMandatory.add(InputFields.STRACKINGID);
        inputFieldsListMandatory.add(InputFields.SDESCRIPTION);
        inputFieldsListMandatory.add(InputFields.GATEWAY);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
