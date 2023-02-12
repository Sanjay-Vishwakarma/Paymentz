import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
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
import java.util.*;

public class MemberMappingDetails extends HttpServlet
{

    private static Logger logger = new Logger(MemberMappingDetails.class.getName());
    static LinkedHashMap<String,String> paymodeids=new LinkedHashMap<String,String>();
    static LinkedHashMap<String,String> cardtypeids=new LinkedHashMap<String,String>();
    static{
        Connection conn=null;
        ResultSet rs = null;
        Date date1=new Date();
        try
        {
            logger.error("Inside MemberMappingDetails Static block");
            logger.error("startTime---" + date1.getTime());
            conn = Database.getConnection();
            Date date2=new Date();
            logger.error("payment_type query startTime---" + date2.getTime());
            rs = Database.executeQuery("select * from payment_type order by paymentType", conn);
            while (rs.next())
            {
                String paymodeid = rs.getString("paymodeid");
                String paymode = rs.getString("paymentType");
                paymodeids.put(paymodeid + "", paymode);
            }
            logger.error("payment_type query End time---" + (new Date()).getTime());
            logger.error("payment_type query Diff time---" + ((new Date()).getTime() - date1.getTime()));

            rs=null;
            Date date3=new Date();
            logger.error("card_type query startTime---" + date3.getTime());
            rs = Database.executeQuery("select * from card_type order by cardType", conn);
            while (rs.next())
            {
                cardtypeids.put( rs.getString("cardtypeid")+"", rs.getString("cardType"));
            }
            logger.error("card_type query End time---" + (new Date()).getTime());
            logger.error("card_type query Diff time---" + ((new Date()).getTime() - date3.getTime()));

            logger.error("End time---" + (new Date()).getTime());
            logger.error("Diff time---" + ((new Date()).getTime() - date1.getTime()));
        }
        catch(Exception e)
        {
            logger.error("Exception while loading paymodeids",e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Date date3=new Date();
        logger.error("startTime---" + date3.getTime());
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String url = "/membermappingpreference.jsp?ctoken=\"" + user.getCSRFToken() + "\"";

        //This is for Terminal Summary
        String action=req.getParameter("action");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        res.setContentType("text/html");
        String errormsg = "";
        Functions functions = new Functions();

        String memberid = "";
        if(req.getParameter("memberid") != null)
            memberid = req.getParameter("memberid");

        String accountid = "";
        if(req.getParameter("accountid") != null)
            accountid = req.getParameter("accountid");

        String paymodeid= "";
        if(req.getParameter("paymodeid") != null)
            paymodeid = req.getParameter("paymodeid");

        String cardTypeId = "";
        if (req.getParameter("ctype1") != null)
            cardTypeId = req.getParameter("ctype1");

        String ActiveOrInActive = "";
        if (req.getParameter("isactive") != null)
            ActiveOrInActive = req.getParameter("isactive");

        if ("All".equalsIgnoreCase(ActiveOrInActive))
            ActiveOrInActive = "";

        String PayoutActiveorInactive= "";
        if (req.getParameter("ispayoutactive")!= null)
            PayoutActiveorInactive= req.getParameter("ispayoutactive");

        if ("All".equalsIgnoreCase(PayoutActiveorInactive))
            PayoutActiveorInactive= "";

        if (!ESAPI.validator().isValidInput("memberid", req.getParameter("memberid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " --Invalid MemberId-- ";
        if (!ESAPI.validator().isValidInput("accountid", req.getParameter("accountid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " --Invalid AccountId-- ";
        if (!ESAPI.validator().isValidInput("paymodeid", req.getParameter("paymodeid"), "OnlyNumber", 10, true))
            errormsg = errormsg + " --Invalid PaymodeId-- ";
        if (!ESAPI.validator().isValidInput("ctype1", req.getParameter("ctype1"), "OnlyNumber", 10, true))
            errormsg = errormsg + " --Invalid CardTypeId-- ";



            if (!functions.isValueNull(memberid) && !functions.isValueNull(accountid) && !functions.isValueNull(paymodeid) && !functions.isValueNull(cardTypeId))
            {
                logger.error("kindly search Member Id Or Account Id Or Paymode Id Or CardType Id ::::");
                //errormsg = errormsg + "kindly search Member Id and Account Id";
                req.setAttribute("accountid", accountid);
                req.setAttribute("memberid", memberid);
                req.setAttribute("paymodeid", paymodeid);
                req.setAttribute("ctype1", cardTypeId);
                req.setAttribute("isactive",ActiveOrInActive);
                req.setAttribute("ispayoutactive",PayoutActiveorInactive);
                req.setAttribute("paymodeids", loadPaymodeids());
                req.setAttribute("cardtypeids", loadcardtypeids());
                RequestDispatcher rd = req.getRequestDispatcher(url);
                rd.forward(req, res);
                return;
            }
            if ((functions.isEmptyOrNull(memberid) && functions.isEmptyOrNull(accountid)) && (!functions.isEmptyOrNull(paymodeid) || !functions.isEmptyOrNull(cardTypeId)))
                errormsg = errormsg + " --Please provide either Member Id or Account Id-- ";
            else if(functions.isValueNull(memberid) || functions.isValueNull(accountid) || functions.isValueNull(paymodeid) || functions.isValueNull(cardTypeId))
            {
                Hashtable hash = null;
                Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
                StringBuffer query = new StringBuffer("select memberid,accountid,paymodeid,cardtypeid,monthly_amount_limit,daily_amount_limit,daily_card_limit,weekly_card_limit,monthly_card_limit,daily_card_amount_limit,weekly_card_amount_limit,monthly_card_amount_limit,min_transaction_amount,max_transaction_amount,isActive,priority,isTest,terminalid,weekly_amount_limit,is_recurring,isRestrictedTicketActive,isTokenizationActive,isManualRecurring,addressDetails,addressValidation,cardDetailRequired,isPSTTerminal,isCardEncryptionEnable,riskruleactivation,daily_avg_ticket,weekly_avg_ticket,monthly_avg_ticket,settlement_currency,min_payout_amount,payoutActivation,autoRedirectRequest,isCardWhitelisted,isEmailWhitelisted,currency_conversion,conversion_currency,binRouting,emi_support,whitelisting_details,cardLimitCheck,cardAmountLimitCheck,amountLimitCheck,actionExecutorId,actionExecutorName,processor_partnerid,payout_priority,daily_amount_limit_check,weekly_amount_limit_check,monthly_amount_limit_check,daily_card_limit_check,weekly_card_limit_check,monthly_card_limit_check,daily_card_amount_limit_check,weekly_card_amount_limit_check,monthly_card_amount_limit_check from member_account_mapping as M where accountid>0 ");
                StringBuffer countquery = new StringBuffer("select count(*) from member_account_mapping where accountid>0 ");



                if (functions.isValueNull(memberid))
                {
                    query.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));
                    countquery.append(" and memberid=" + ESAPI.encoder().encodeForSQL(me, memberid));

                    url += "&memberid=\"" + memberid + "\"";
                }
                if (functions.isValueNull(accountid))
                {
                    query.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
                    countquery.append(" and accountid=" + ESAPI.encoder().encodeForSQL(me, accountid));
                    url += "&accountid=\"" + accountid + "\"";
                }
                if (functions.isValueNull(paymodeid))
                {
                    query.append(" and paymodeid=" + ESAPI.encoder().encodeForSQL(me, paymodeid));
                    countquery.append(" and paymodeid=" +ESAPI.encoder().encodeForSQL(me, paymodeid));
                    url += "&paymodeid=\"" + paymodeid + "\"";
                }
                if (functions.isValueNull(cardTypeId))
                {
                    query.append(" and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardTypeId));
                    countquery.append(" and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardTypeId));
                    url += "&cardtypeid=\"" + cardTypeId + "\"";
                }
                if (functions.isValueNull(memberid) && functions.isValueNull(accountid) && functions.isValueNull(paymodeid) && functions.isValueNull(cardTypeId))
                {
                    query.append(" and memberid= " + ESAPI.encoder().encodeForSQL(me, memberid) + " and accountid=" + ESAPI.encoder().encodeForSQL(me, accountid)+ " and paymodeid=" +ESAPI.encoder().encodeForSQL(me, paymodeid) + " and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardTypeId));
                    countquery.append(" and memberid= " + ESAPI.encoder().encodeForSQL(me, memberid) + " and accountid=" + ESAPI.encoder().encodeForSQL(me, accountid) +" and paymodeid=" +ESAPI.encoder().encodeForSQL(me,paymodeid) + " and cardtypeid=" + ESAPI.encoder().encodeForSQL(me, cardTypeId));
                    url += "&memberid=\"" + memberid + "\" &accountid=\"" + accountid + "\" &paymodeid=\"" + paymodeid + "\" &cardtypeid=\"" + cardTypeId + "\"";
                }
                if (!functions.isEmptyOrNull(ActiveOrInActive)){
                    query.append(" and isActive='" + ESAPI.encoder().encodeForSQL(me, ActiveOrInActive) +"'");
                    countquery.append(" and isActive='" + ESAPI.encoder().encodeForSQL(me, ActiveOrInActive) + "'");
                    url += "&isActive=\"" + ActiveOrInActive + "\"";
                }
                if (!functions.isEmptyOrNull(PayoutActiveorInactive))
                {
                    query.append(" and payoutActivation='" + ESAPI.encoder().encodeForSQL(me, PayoutActiveorInactive) + "'");
                    countquery.append(" and payoutActivation='" + ESAPI.encoder().encodeForSQL(me, PayoutActiveorInactive)+ "'");
                    url += "&payoutActivation=\"" +PayoutActiveorInactive + "\"";
                }
                query.append(" order by memberid ");

                logger.debug("---query---" + query);
                logger.error("---countquery---" + countquery);



            if (action.equalsIgnoreCase("search"))
            {

                Connection con = null;
                ResultSet rs = null;
                try
                {
                    //con = Database.getConnection();
                    con = Database.getRDBConnection();
                    Date date4=new Date();
                    if (true)
                    {
                        logger.error("query startTime---" + date4.getTime());
                        hash = Database.getHashFromResultSet(Database.executeQuery(query.toString(), con));
                        Date date5=new Date();
                        logger.error("Countquery startTime---" + date5.getTime());
                        rs = Database.executeQuery(countquery.toString(), con);
                        rs.next();
                        int totalrecords = rs.getInt(1);
                        logger.error("Countquery End time---" + (new Date()).getTime());
                        logger.error("Countquery Diff time---" + ((new Date()).getTime() - date5.getTime()));

                        hash.put("totalrecords", "" + totalrecords);
                        hash.put("records", "0");
                        if (totalrecords > 0)
                            hash.put("records", "" + (hash.size() - 2));
                    }
                    else
                    {
                        hash = new Hashtable();
                        hash.put("records", "0");
                        hash.put("totalrecords", "0");
                    }

                    req.setAttribute("memberdetails", hash);
                    logger.error("query End time---" + (new Date()).getTime());
                    logger.error("query Diff time---" + ((new Date()).getTime() - date4.getTime()));
                    logger.error(" inside search button value===================>"+action);
                }
                catch (SystemError se)
                {
                    logger.error("System Error::::", se);
                }
                catch (Exception e)
                {
                    logger.error("Exception::::", e);
                }
                finally
                {
                    try
                    {
                        Database.closeResultSet(rs);
                        con.close();
                    }
                    catch (SQLException e)
                    {
                        logger.error("SQL Exception::::", e);
                    }
                }
            }
            else /*if(action.equalsIgnoreCase("tsummary"))*/{
                logger.error("Terminal summary====="+action);

                Connection con=null;
                ResultSet rs=null;

                try{
                    con=Database.getConnection();
                    Date date6=new Date();

                    if (true){

                        logger.error("Query startTime======>"+date6.getTime());
                        hash=Database.getHashFromResultSet(Database.executeQuery(query.toString(),con));
                        Date date7=new Date();
                        logger.error("Countquery startTime---" + date7.getTime());

                        rs=Database.executeQuery(countquery.toString(),con);
                        rs.next();
                        int finalRecord=rs.getInt(1);
                        logger.error("Countquery End time---" + (new Date()).getTime());
                        logger.error("Countquery Diff time---" + ((new Date()).getTime() - date7.getTime()));
                        hash.put("totalrecords", "" + finalRecord);
                        hash.put("records", "0");
                        if (finalRecord > 0)
                            hash.put("records", "" + (hash.size() - 2));
                    }
                    else
                    {
                        hash = new Hashtable();
                        hash.put("records", "0");
                        hash.put("totalrecords", "0");
                    }

                    req.setAttribute("memberdetails", hash);
                    logger.error("query End time---" + (new Date()).getTime());
                    logger.error("query Diff time---" + ((new Date()).getTime() - date6.getTime()));


                }
                catch (SystemError se)
                {
                    logger.error("System Error::::", se);
                }
                catch (Exception e)
                {
                    logger.error("Exception::::", e);
                }
                finally
                {
                    try
                    {
                        Database.closeResultSet(rs);
                        con.close();
                    }
                    catch (SQLException e)
                    {
                        logger.error("SQL Exception::::", e);
                    }
                }

            }

            }


        if (errormsg != "")
        {
            logger.debug("ENTER VALID DATA");
            req.setAttribute("error", errormsg.trim());
        }
        req.setAttribute("accountid", accountid);
        req.setAttribute("memberid", memberid);
        req.setAttribute("paymodeid", paymodeid);
        req.setAttribute("ctype1", cardTypeId);
        req.setAttribute("isactive",ActiveOrInActive);
        req.setAttribute("ispayoutactive",PayoutActiveorInactive);
        req.setAttribute("accountids",loadGatewayAccounts());
        req.setAttribute("paymodeids", loadPaymodeids());
        req.setAttribute("cardtypeids", loadcardtypeids());


        req.setAttribute("action",action);
        RequestDispatcher rd = req.getRequestDispatcher(url);
        rd.forward(req, res);
        logger.error("End time---" + (new Date()).getTime());
        logger.error("Diff time---" + ((new Date()).getTime() - date3.getTime()));
    }

    LinkedHashMap<String,String> loadPaymodeids()
    {
        return paymodeids;
    }
    LinkedHashMap<String,String> loadcardtypeids()
    {
        return cardtypeids;
    }
    Hashtable loadGatewayAccounts()
    {
        return GatewayAccountService.getMerchantDetails();
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);
        inputFieldsListMandatory.add(InputFields.MEMBERID);
        inputFieldsListMandatory.add(InputFields.PAYMODEID);
        inputFieldsListMandatory.add(InputFields.CARDTYPEID);
        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }



}