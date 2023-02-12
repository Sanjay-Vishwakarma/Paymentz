import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayTypeService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
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
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/9/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommonActionHistory extends HttpServlet
{
    private static Logger log = new Logger(CommonActionHistory.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in CommonInquiryList");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        Functions functions = new Functions();
        TransactionVO transactionVO = new TransactionVO();
        TransactionManager transactionManager = new TransactionManager();
        log.debug("success");

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        String error= "";
        String EOL = "<BR>";

        String status=null;
        String paymentid=null;
        String pgtypeid = req.getParameter("pgtypeid");
        String gateway = "";
        String currency = "";

        int records=15;
        int pageno=1;
        String trackingid=null;
        String memberid=null;
        String accountid=null;
        Hashtable hash = null;
        Hashtable statushash=null;

        Hashtable transdetail=null;

        error = error + validateOptionalParameters(req);
         //new
        if(!req.getParameter("toid").equals("0"))
        {
            memberid= req.getParameter("toid");
            req.setAttribute("toid",memberid);
        }
        if(!req.getParameter("accountid").equals("0"))
        {
            accountid= req.getParameter("accountid");
            req.setAttribute("accountid",accountid);
        }
        if (pgtypeid.split("-").length == 3 && !pgtypeid.equalsIgnoreCase("0"))
        {
            gateway = pgtypeid.split("-")[0];
            currency = pgtypeid.split("-")[1];
        }
        req.setAttribute("pgtypeid",pgtypeid);

        paymentid= req.getParameter("paymentid");
        req.setAttribute("paymentid",paymentid);

        trackingid= req.getParameter("trackingid");
        req.setAttribute("trackingid",trackingid);

        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input",e);
            error += "<center><font class=\"text\" face=\"arial\"><b>"+ error + e.getMessage() + EOL + "</b></font></center>";
            req.setAttribute("errormessage",error);
            RequestDispatcher rd = req.getRequestDispatcher("/commonactionhistory.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        status = req.getParameter("status");

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;

        transactionVO.setStatus(status);
        transactionVO.setTrackingId(trackingid);
        transactionVO.setToid(memberid);
        transactionVO.setPgtypeid(gateway);
        transactionVO.setCurrency(currency);
        transactionVO.setAccountId(accountid);

        hash = transactionManager.getTransactionListForCommonActionHistory(transactionVO,String.valueOf(start),String.valueOf(end));
        req.setAttribute("transdetails", hash);
        RequestDispatcher rd = req.getRequestDispatcher("/commonactionhistory.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);


        //Set<String> gatewayTypeSet = getGatewayHash(gateway);
        /*Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        //Iterator i = gatewayTypeSet.iterator();
        String tablename=null;
        StringBuffer sb=null;
        StringBuffer count=null;
        try
        {
            conn = Database.getConnection();
            *//*while(i.hasNext())
            {*//*
                //tablename = Database.getTableName((String) i.next());
                *//*if(tablename.equals("transaction_common"))
                {*//*
                    sb=new StringBuffer("select D.trackingid,D.action,D.status,D.timestamp,D.amount,D.remark,D.responsetransactionid,D.responsetransactionstatus,D.responsecode,D.responsedescription,D.actionexecutorid,D.actionexecutorname,D.ipaddress from transaction_common_details as D,transaction_common as T where T.trackingid=D.trackingid and detailid>0");
                    count=new StringBuffer("select count(*) from transaction_common_details as D,transaction_common as T where T.trackingid=D.trackingid and detailid>0");

                    if (functions.isValueNull(status))
                    {
                        sb.append(" and D.status='" + ESAPI.encoder().encodeForSQL(me,status) + "'");
                        count.append(" and D.status='" + ESAPI.encoder().encodeForSQL(me,status) + "'");
                    }

                    if (functions.isValueNull(trackingid))
                    {
                        sb.append(" and D.trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
                        count.append(" and D.trackingid=" + ESAPI.encoder().encodeForSQL(me,trackingid));
                    }

                    if (functions.isValueNull(memberid))
                    {
                        sb.append(" and T.toid=" + ESAPI.encoder().encodeForSQL(me,memberid));
                        count.append(" and T.toid=" + ESAPI.encoder().encodeForSQL(me,memberid));
                    }

                    if (functions.isValueNull(gateway))
                    {
                        sb.append(" and T.fromtype= '" + ESAPI.encoder().encodeForSQL(me,gateway)+"'");
                        count.append(" and T.fromtype= '" + ESAPI.encoder().encodeForSQL(me,gateway)+"'");
                    }

                    if (functions.isValueNull(currency))
                    {
                        sb.append(" and T.currency= '" + ESAPI.encoder().encodeForSQL(me,currency)+"'");
                        count.append(" and T.currency= '" + ESAPI.encoder().encodeForSQL(me,currency)+"'");
                    }


            if (functions.isValueNull(accountid))
                    {
                        sb.append(" and T.accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
                        count.append(" and T.accountid=" + ESAPI.encoder().encodeForSQL(me,accountid));
                    }
                    else
                    {
                        gateway=req.getParameter("gateway");
                        Set<String> pgaccounts= GatewayAccountService.getAccountIdsFromPgTypeId(gateway);
                        if(!pgaccounts.isEmpty())
                        {   sb.append(" and T.accountid IN (");
                            count.append(" and T.accountid IN (");

                            Iterator in=pgaccounts.iterator() ;
                            while(in.hasNext())
                            {
                                String temp=(String)in.next();
                                sb.append("'"+temp+"'");
                                count.append("'"+temp+"'");

                                if(in.hasNext())
                                {
                                    sb.append(",");
                                    count.append(",");
                                }
                            }
                            sb.append(")");
                            count.append(")");
                        }
                    }
                    sb.append(" order by D.trackingid desc LIMIT " + start + "," + end);
                //}
            //}
            log.debug("query---"+sb.toString());
            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(sb.toString(), conn));
            ResultSet rs = Database.executeQuery(count.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);

        }
        catch(SystemError s)
        {
            log.error("SystemError",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");

        }
        catch (SQLException e)
        {
            log.error("SQLError",e);
            //error= error+"Sql Exception while listing records.<br>";
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeConnection(conn);
        }
        req.setAttribute("error",error);*/

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
    private String validateOptionalParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields>  inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.TOID);
        inputFieldsListOptional.add(InputFields.PAYMENTID);
        inputFieldsListOptional.add(InputFields.TRACKINGID_SMALL);
        inputFieldsListOptional.add(InputFields.MEMBERID);
        inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListOptional)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage()+EOL;
                }
            }
        }
        return error;
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        //inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.STATUS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}