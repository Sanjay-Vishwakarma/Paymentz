package net.agent;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saurabh
 * Date: 2/21/13
 * Time: 3:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentSetMemberConfig extends HttpServlet
{
    private static Logger logger = new Logger(AgentSetMemberConfig.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        logger.debug("Entering Transaction..... ");
        AgentFunctions agent=new AgentFunctions();
        if (!agent.isLoggedInAgent(session))
        {
            res.sendRedirect("/agent/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String memberids[] = req.getParameterValues("memberid");
        String reserves[] = req.getParameterValues("reserves");
        String aptprompts[] = req.getParameterValues("aptprompt");
        String chargepers[] = req.getParameterValues("chargeper");
        String fixamounts[] = req.getParameterValues("fixamount");
        String accountIds[] = req.getParameterValues("accountids");
        String reserveReasons[] = req.getParameterValues("reserve_reason");
        String reversalCharges[]  =  req.getParameterValues("reversalcharge");
        String withdrawalCharges[] = req.getParameterValues("withdrawalcharge");
        String chargebackCharges[] = req.getParameterValues("chargebackcharge");
        String taxPercentages[] = req.getParameterValues("taxper");
        String isValidateEmail[] = req.getParameterValues("isValidateEmail");
        /*String custremindermail[] = req.getParameterValues("custremindermail");*/
        String daily_amount_limit=req.getParameter("daily_amount_limit");
        String monthly_amount_limit=req.getParameter("monthly_amount_limit");
        String daily_card_limit=req.getParameter("daily_card_limit");
        String weekly_card_limit=req.getParameter("weekly_card_limit");
        String monthly_card_limit=req.getParameter("monthly_card_limit");
        String active=req.getParameter("activation");
        String icici=req.getParameter("icici");
        String query = null;
        String ispharma=req.getParameter("isPharma");
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        String haspaid=req.getParameter("haspaid");
        String isservice=req.getParameter("isservice");
        String hralertproof=req.getParameter("hralertproof");
        String autoredirect=req.getParameter("autoredirect");
        String vbv=req.getParameter("vbv");
        String hrparameterised=req.getParameter("hrparameterised");

        String check_limit=req.getParameter("check_limit");
        String invoicetemplate=req.getParameter("invoicetemplate");
        String isPoweredBy=req.getParameter("isPoweredBy");
        String template=req.getParameter("template");
        String iswhitelisted=req.getParameter("iswhitelisted");
        String masterCardSupported=req.getParameter("masterCardSupported");

        String daily_card_amount_limit=req.getParameter("daily_card_amount_limit");
        String weekly_card_amount_limit=req.getParameter("weekly_card_amount_limit");
        String monthly_card_amount_limit=req.getParameter("monthly_card_amount_limit");
        String card_check_limit=req.getParameter("card_check_limit");
        String card_transaction_limit=req.getParameter("card_transaction_limit");

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        int updRecs = 0;
        Connection cn=null;
        PreparedStatement pstmt = null;
        if (memberids != null)
        { //process only if there is at least one record to be updated
            try
            {
                int size = reserves.length;
                for (int i = 0; i < size; i++)
                {
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIds[i]);
                    //account.
                    String reserve = "", aptprompt = "", fixamount = "", accountID = "";
                    String chargeper = "",reversalCharge="",withdrawalCharge="",chargebackCharge="",taxPercentage="";
                    String isValidEmail="N";
                    String isCustEmail="N";
                    /*String custrememail="N";*/
                    chargeper = getValidPercentage(chargepers[i]);
                    taxPercentage = getValidPercentage(taxPercentages[i]);
                    reserve = getValidPercentage(reserves[i]);
                    aptprompt = getValidPercentage(aptprompts[i]);

                    fixamount = getValidAmount(fixamounts[i]);

                    reversalCharge = getValidAmount(reversalCharges[i]);
                    withdrawalCharge = getValidAmount(withdrawalCharges[i]);
                    chargebackCharge = getValidAmount(chargebackCharges[i]);

                    isValidEmail = isValidateEmail[i];
                    /*custrememail = custremindermail[i];*/
                    accountID =  Functions.checkStringNull (accountIds[i]);
                    if(card_check_limit==null)
                    {
                        card_check_limit="0";
                    }
                    if(card_transaction_limit==null)
                    {
                        card_transaction_limit="0";
                    }
                    if(isValidEmail==null)
                    {
                        isValidEmail="N";
                    }
                    /*if(custrememail==null)
                    {
                        custrememail="N";
                    }*/
                    if(active==null)
                    {
                        active="T";
                    }
                    if(haspaid==null)
                    {
                        haspaid="N";
                    }
                    if(isservice==null)
                    {
                        isservice="N";
                    }
                    if(icici==null)
                    {
                        icici="N";
                    }
                    if(hralertproof==null)
                    {
                        hralertproof="N";
                    }
                    if(autoredirect==null)
                    {
                        autoredirect="N";
                    }
                    if(vbv==null)
                    {
                        vbv="N";
                    }
                    if(hrparameterised==null)
                    {
                        hrparameterised="N";
                    }
                    if(invoicetemplate==null)
                    {
                        invoicetemplate="Y";
                    }
                    if(check_limit==null)
                    {
                        check_limit="0";
                    }
                    if(masterCardSupported==null)
                    {
                        masterCardSupported="N";
                    }
                    if(iswhitelisted==null)
                    {
                        iswhitelisted="N";
                    }
                    if(accountID != null)
                    {
                        if(chargeper == null)
                            chargeper = String.valueOf(account.getChargePercentage());
                        if(reversalCharge == null)
                            reversalCharge = String.valueOf(account.getReversalCharge());
                        if(withdrawalCharge == null)
                            withdrawalCharge = String.valueOf(account.getWithdrawalCharge());
                        if(chargebackCharge == null)
                            chargebackCharge = String.valueOf(account.getChargebackCharge());
                        if(taxPercentage == null)
                            taxPercentage = String.valueOf(account.getTaxPercentage());
                        if (reserve  == null) { reserve = "0";}
                        if (aptprompt== null)
                            aptprompt = String.valueOf(account.getHighRiskAmount());
                        if (fixamount== null) { fixamount = "0";}
                        if (daily_card_amount_limit== null) { daily_card_amount_limit = "1000.00";}
                        if (weekly_card_amount_limit== null) { weekly_card_amount_limit = "5000.00";}
                        if (monthly_card_amount_limit== null) { monthly_card_amount_limit = "10000.00";}

                    }
//                    query = "update members set aptprompt=" + aptprompt + ",reserves=" + reserve + ",chargeper=" + chargeper +
//                            ",fixamount=" + fixamount + ",reserve_reason='" + reserveReasons[i] + "',accountid="+accountID+",reversalcharge="+reversalCharge+",withdrawalcharge="+withdrawalCharge+",chargebackcharge="+chargebackCharge+",taxper="+taxPercentage+" where memberid=" + memberids[i];
//
//                    int result = Database.executeUpdate(query, Database.getConnection());
                    if(aptprompt==null)
                    {
                        aptprompt="0";
                    }
                    if(reserve==null)
                    {
                        reserve="0";
                    }

                    cn= Database.getConnection();
                    query = "update members set aptprompt= ?,reserves= ?,chargeper=?,fixamount=?,reserve_reason=?,accountid=?,reversalcharge=?,withdrawalcharge=?,chargebackcharge=?,taxper=?,isPharma=?,isValidateEmail=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,activation=?,icici=?,haspaid=?,isservice=?,hralertproof=?,autoredirect=?,vbv=?,hrparameterised=?,check_limit=?,masterCardSupported=?,invoicetemplate=?,isPoweredBy=?,template=?,iswhitelisted=?,card_transaction_limit=?,card_check_limit=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=? where memberid=?";
                    pstmt= cn.prepareStatement(query);
                    pstmt.setString(1,aptprompt);
                    pstmt.setString(2,reserve);
                    pstmt.setString(3,chargeper);
                    pstmt.setString(4,fixamount);
                    pstmt.setString(5,reserveReasons[i]);
                    pstmt.setString(6,accountID);
                    pstmt.setString(7,reversalCharge);
                    pstmt.setString(8,withdrawalCharge);
                    pstmt.setString(9,chargebackCharge);
                    pstmt.setString(10,taxPercentage);
                    pstmt.setString(11,ispharma);
                    pstmt.setString(12,isValidEmail);
                    pstmt.setString(13,daily_amount_limit);
                    pstmt.setString(14,monthly_amount_limit);
                    pstmt.setString(15,daily_card_limit);
                    pstmt.setString(16,weekly_card_limit);
                    pstmt.setString(17,monthly_card_limit);
                    /*pstmt.setString(18,custrememail);*/
                    pstmt.setString(18,active);
                    pstmt.setString(19,icici);
                    pstmt.setString(20,haspaid);
                    pstmt.setString(21,isservice);
                    pstmt.setString(22,hralertproof);
                    pstmt.setString(23,autoredirect);
                    pstmt.setString(24,vbv);
                    pstmt.setString(25,hrparameterised);

                    pstmt.setString(26,check_limit);
                    pstmt.setString(27,masterCardSupported);
                    pstmt.setString(28,invoicetemplate);
                    pstmt.setString(29,isPoweredBy);
                    pstmt.setString(30,template);
                    pstmt.setString(31,iswhitelisted);
                    pstmt.setString(32,String.valueOf(card_transaction_limit));
                    pstmt.setString(33,String.valueOf(card_check_limit));
                    pstmt.setString(34,daily_card_amount_limit);
                    pstmt.setString(35,weekly_card_amount_limit);
                    pstmt.setString(36,monthly_card_amount_limit);
                    pstmt.setString(37,memberids[i]);

                    int result = pstmt.executeUpdate();

                    logger.debug("result " + result);

                    if (result > 0)
                        updRecs++;
                }//end for

            }
            catch (Exception e)
            {
                logger.error("Error while set reserves :",e);

                sErrorMessage.append("Internal Error while Retrieving records");


            }//try catch ends
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(cn);
            }
        }

        sSuccessMessage.append(updRecs + " Records Updated");
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/memberConfig.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }//post ends

    private String getValidPercentage(String paramValue)
    {
        String returnVal = getValidAmount(paramValue);
        if(returnVal != null)
        {
            returnVal =  "" + new BigDecimal(returnVal).multiply(new BigDecimal("100"));
        }
        return returnVal;
    }
    private String getValidAmount(String paramValue)
    {
        if ("N/A".equals(paramValue))
        {
            return null;
        }
        String returnVal = Functions.checkStringNull(paramValue);
        if(returnVal == null )
        {
            returnVal = "0";
        }
        else
        {
            try
            {
                returnVal = "" + new BigDecimal(returnVal);
            }
            catch(NumberFormatException ne)
            {  logger.error("Number Format Exception",ne);

                returnVal = "0";
            }
        }
        return returnVal;
    }

}
