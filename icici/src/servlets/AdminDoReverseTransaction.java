import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

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
import java.util.*;


public class AdminDoReverseTransaction extends HttpServlet
{

    private static Logger logger = new Logger(AdminDoReverseTransaction.class.getName());


    String name = null;
    boolean reversed = false;

    Database db = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt1 = null;
    ResultSet rs = null;
    String query = null;
    int count = 1;

    Enumeration enu = null;

    Hashtable reversalhash = null;


    float val = 0;
    java.util.Date dt = null;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Transaction transaction = new Transaction();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String accountid=null;
        String icicitransid = null;
        String refundCode = null;
        String refundRRN = null;
        String refundid = null;
        boolean flag = true;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String errormsg1="";
        db = new Database();
        enu = req.getParameterNames();
        String  redirectpage="";
        reversed = false;
        String EOL="<BR>";
        reversalhash = new Hashtable();
        count = 1;
        //Added for reversal mail
        HashMap<String,Collection> membersMap = new HashMap<String,Collection>();

        Collection<Hashtable> listOfRefunds = null;

        Hashtable refundDetails = null;

        errormsg1 = errormsg1 + validateMandatoryParameters(req);
        if(!errormsg1.equals(""))
            flag = false;
         /*if(!ESAPI.validator().isValidInput("icicitransid",req.getParameter("icicitransid"),"Numbers",10,false))
         {
            //errormsg1 = errormsg1 + "<br><font face=\"arial\" color=\"red\" size=\"2\">Please Enter Username.</font>";
            flag = false;
         }
        else
            icicitransid=req.getParameter("icicitransid");
        if(!ESAPI.validator().isValidInput("accountid",req.getParameter("accountid"),"Numbers",10,false))
        {
            //errormsg1 = errormsg1 + "<br><font face=\"arial\" color=\"red\" size=\"2\">Please Enter Username.</font>";
            flag = false;
        }
        else
            icicitransid=req.getParameter("accountid");
        if(!ESAPI.validator().isValidInput("refundid",req.getParameter("refundid"),"Numbers",10,false))
        {
             errormsg1 = errormsg1 + "RefundId should not be empty or Enter valid value."+EOL;
            flag = false;
        }
        else
            refundid = req.getParameter("refundid");
        if(!ESAPI.validator().isValidInput("refundcode",req.getParameter("refundcode"),"Numbers",10,false))
        {
            errormsg1 = errormsg1 + "RefundCode should not be empty or Enter valid value."+EOL;
            flag = false;
        }
        else
            refundCode = req.getParameter("refundcode");
        if(!ESAPI.validator().isValidInput("refundreceiptno",req.getParameter("refundreceiptno"),"Numbers",10,false))
        {
            errormsg1 = errormsg1 + "Refund Receipt No. should not be empty or Enter valid value."+EOL;
            flag = false;
        }
        else
            refundRRN = req.getParameter("refundreceiptno");*/
        if(!flag)
        {
            sErrorMessage.append(errormsg1);
            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            redirectpage = "/servlet/AdminReverseList?action=F&ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }

        try
        {
            if (Functions.parseData(icicitransid) == null || Functions.parseData(refundCode) == null || Functions.parseData(refundid) == null || Functions.parseData(refundRRN) == null)
            {
                try
                {
                    Integer.parseInt(refundid);
                }
                catch (NumberFormatException ex)
                {
                    sErrorMessage.append("Invalid refundid");
                }
                sErrorMessage.append("Invalid Insufficient data");

                StringBuilder chargeBackMessage = new StringBuilder();
                chargeBackMessage.append(sSuccessMessage.toString());
                chargeBackMessage.append("<BR/>");
                chargeBackMessage.append(sErrorMessage.toString());

                redirectpage = "/servlet/AdminReverseList?action=F&ctoken="+user.getCSRFToken();
                req.setAttribute("cbmessage", chargeBackMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);

                return;

            }

            if (!icicitransid.trim().equals(""))
            {
                String gateway= GatewayAccountService.getGatewayAccount(accountid).getGateway();
                String tableName=Database.getTableName(gateway);
                
                conn = db.getConnection();
                PreparedStatement pstmt=conn.prepareStatement("");
                if(tableName.equals("transaction_icicicredit"))
                {
                    query = "update transaction_icicicredit set status='reversed',refundid= ?,refundcode= ?,refundreceiptno= ? where icicitransid= ? and status='markedforreversal'";
                    pstmt= conn.prepareStatement(query);
                    pstmt.setString(1,refundid);
                    pstmt.setString(2,refundCode);
                    pstmt.setString(3,refundRRN);
                    pstmt.setString(4,icicitransid);
                }
                else
                {
                    query = "update "+tableName+" set status='reversed',refundcode= ?,refundinfo= ? where trackingid= ? and status='markedforreversal'";
                    pstmt= conn.prepareStatement(query);

                    pstmt.setString(1,refundCode);
                    pstmt.setString(2,refundid+"-"+refundRRN);
                    pstmt.setString(3,icicitransid);
                }

                int result = pstmt.executeUpdate();
                logger.debug("No of Rows updated : " + result + "<br>");

                if (result != 1)
                {

                    sErrorMessage.append("Failed to Reversed. No rows Updated");

                }
                conn = db.getConnection();

                query = "select T.* from transaction_icicicredit as T where T.icicitransid= ? and status='reversed'";
                pstmt1 = conn.prepareStatement(query);
                pstmt1.setString(1,icicitransid);

                rs = pstmt1.executeQuery();

                if (rs.next())
                {
                    icicitransid = rs.getString("icicitransid");
                    String toid = rs.getString("toid");
                    String rsdescription = rs.getString("description");
                    String captureamount = rs.getString("captureamount");
                    String accountId = rs.getString("accountid");
                    String cardholdername = rs.getString("name");
                    String refundamount = rs.getString("refundamount");

                // preparing collections of refunds as per merchant
                    refundDetails = new Hashtable();
                    refundDetails.put("icicitransid",icicitransid);
                    refundDetails.put("captureamount",captureamount);
                    refundDetails.put("refundamount",refundamount);
                    refundDetails.put("description",rsdescription);
                    refundDetails.put("accountid",accountId);
                    refundDetails.put("cardholdername",Functions.decryptString(cardholdername));

                    if(membersMap.get(toid)==null)
                    {
                        listOfRefunds = new ArrayList<Hashtable>();
                        listOfRefunds.add(refundDetails);
                        membersMap.put(toid,listOfRefunds);
                    }
                     else
                    {
                         listOfRefunds = membersMap.get(toid);
                         listOfRefunds.add(refundDetails);
                         membersMap.put(toid,listOfRefunds);
                    }

                    // Start : Added for Action and Status Entry in Action History table

                    ActionEntry entry = new ActionEntry();
                    int actionEntry = entry.actionEntry(icicitransid,refundamount,ActionEntry.ACTION_REVERSAL_SUCCESSFUL,ActionEntry.STATUS_REVERSAL_SUCCESSFUL);
                    entry.closeConnection();
                    // End : Added for Action and Status Entry in Action History table
                }
                sSuccessMessage.append("Transaction Reversed. \r\n");

                SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION,icicitransid,"","",null);
            }

        }
        catch (SystemError se)
        {
            logger.error("SystemError while reversal :",se);

             sErrorMessage.append("Fail Transaction");
        }
        catch (Exception e)
        {
            logger.error("Error while reversal :",e);

             sErrorMessage.append("Transaction is not Done");

        }//try catch ends
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(pstmt1);
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }

         //sending mail to merchant for transaction reversal
        Set members = membersMap.keySet();
                for(Object memberid : members )
                {
                    logger.info("calling sendReverseTransactionMail");
                    try
                    {
                    transaction.sendReverseTransactionMail((ArrayList)membersMap.get(memberid),(String)memberid);
                    }
                    catch(SystemError se)
                    {
                       logger.error("Error while sending reversal mail:",se);
                    }
                    logger.info("called sendReverseTransactionMail");
                }


           StringBuilder chargeBackMessage = new StringBuilder();
                chargeBackMessage.append(sSuccessMessage.toString());
                chargeBackMessage.append("<BR/>");
                chargeBackMessage.append(sErrorMessage.toString());

                redirectpage = "/servlet/AdminReverseList?action=F&ctoken="+user.getCSRFToken();
                req.setAttribute("cbmessage", chargeBackMessage.toString());
                RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
                rd.forward(req, res);
    }//post ends
    private String validateMandatoryParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();
        inputFieldsListOptional.add(InputFields.REFUNDID);
        inputFieldsListOptional.add(InputFields.REFUND_CODE);
        inputFieldsListOptional.add(InputFields.REFUND_RECEIPT_NO);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListOptional,errorList,false);

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