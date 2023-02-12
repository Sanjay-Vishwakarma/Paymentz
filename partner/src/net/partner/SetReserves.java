package net.partner;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class SetReserves extends HttpServlet
{
    private static Logger logger = new Logger(SetReserves.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in SetReserves");
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        logger.debug("success");

        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        errorMsg = validateParameters(req);
        if(!partner.isEmptyOrNull(errorMsg))
        {

            String redirectpage = "/memberpreference.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }
        else
        {

            String memberids[] = req.getParameterValues("memberid");
    //        String reserves[] = req.getParameterValues("reserves");
           // System.out.println("reserves..."+reserves);
            String aptprompts[] = req.getParameterValues("aptprompt");
            //String chargepers[] = req.getParameterValues("chargeper");
            //String fixamounts[] = req.getParameterValues("fixamount");
            String accountIds[] = req.getParameterValues("accountids");
            //String reserveReasons[] = req.getParameterValues("reserve_reason");
            //String reversalCharges[]  =  req.getParameterValues("reversalcharge");
            //String withdrawalCharges[] = req.getParameterValues("withdrawalcharge");
            //String chargebackCharges[] = req.getParameterValues("chargebackcharge");
            //String taxPercentages[] = req.getParameterValues("taxper");
            String isValidateEmail[] = req.getParameterValues("isValidateEmail");
            /*String custremindermail[] = req.getParameterValues("custremindermail");*/
            String daily_amount_limit=req.getParameter("daily_amount_limit");
            String monthly_amount_limit=req.getParameter("monthly_amount_limit");
            String daily_card_limit=req.getParameter("daily_card_limit");
            String weekly_card_limit=req.getParameter("weekly_card_limit");
            String monthly_card_limit=req.getParameter("monthly_card_limit");
            String isrefund=req.getParameter("isrefund");
            String refunddailylimit=req.getParameter("refunddailylimit");
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
            String partnerId=req.getParameter("partnerId");
            String agentid=req.getParameter("agentId");
            String check_limit=req.getParameter("check_limit");
            String invoicetemplate=req.getParameter("invoicetemplate");
            String isPoweredBy=req.getParameter("isPoweredBy");
            String template=req.getParameter("template");
            String iswhitelisted=req.getParameter("iswhitelisted");
            String masterCardSupported=req.getParameter("masterCardSupported");
            String isipwhitelist=req.getParameter("isipwhitelisted");
            String isPODRequired=req.getParameter("isPODRequired");
            String daily_card_amount_limit=req.getParameter("daily_card_amount_limit");
            String weekly_card_amount_limit=req.getParameter("weekly_card_amount_limit");
            String monthly_card_amount_limit=req.getParameter("monthly_card_amount_limit");
            String card_check_limit=req.getParameter("card_check_limit");
            String card_transaction_limit=req.getParameter("card_transaction_limit");

            String autoSelectTerminal = req.getParameter("autoSelectTerminal");
            String maxScoreAllowed = req.getParameter("maxscoreallowed");
            String maxScoreReversal = req.getParameter("maxscoreautoreversal");

            String weekly_amount_limit = req.getParameter("weekly_amount_limit");
            String isappmanageractivate=req.getParameter("isappmanageractivate");


            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            int updRecs = 0;
            Connection cn=null;
            PreparedStatement pstmt = null;
            if (memberids != null)
            { //process only if there is at least one record to be updated
                try
                {
                    //int size = reserves.length;
                    for (int i = 0; i < 1; i++)
                    {
                        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountIds[i]);
                        //account.
                        String reserve = "", aptprompt = "", fixamount = "", accountID = "";
                        String chargeper = "",reversalCharge="",withdrawalCharge="",chargebackCharge="",taxPercentage="";
                        String isValidEmail="N";
                        String isCustEmail="N";
                        String custrememail="N";
                        //chargeper = getValidPercentage(chargepers[i]);
                        //taxPercentage = getValidPercentage(taxPercentages[i]);
      //                  reserve = getValidPercentage(reserves[i]);
                        aptprompt = getValidPercentage(aptprompts[i]);

                        //fixamount = getValidAmount(fixamounts[i]);

                        //reversalCharge = getValidAmount(reversalCharges[i]);
                        //withdrawalCharge = getValidAmount(withdrawalCharges[i]);
                        //chargebackCharge = getValidAmount(chargebackCharges[i]);

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
                        if(isrefund==null)
                        {
                            isrefund="N";
                        }
                        if(refunddailylimit==null)
                        {
                            refunddailylimit="0";
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
                        if(isPODRequired==null)
                        {
                            isPODRequired="N";
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
                           // if(chargeper == null)
                           //     chargeper = String.valueOf(account.getChargePercentage());
                           // if(reversalCharge == null)
                             //   reversalCharge = String.valueOf(account.getReversalCharge());
                           // if(withdrawalCharge == null)
                            //    withdrawalCharge = String.valueOf(account.getWithdrawalCharge());
                            //if(chargebackCharge == null)
                            //    chargebackCharge = String.valueOf(account.getChargebackCharge());
                            //if(taxPercentage == null)
                              //  taxPercentage = String.valueOf(account.getTaxPercentage());
                           // if (reserve  == null) { reserve = "0";}
                            if (aptprompt== null)
                                aptprompt = String.valueOf(account.getHighRiskAmount());
                           // if (fixamount== null) { fixamount = "0";}
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
                    //    if(reserve==null)
                    //    {
                    //        reserve="0";
                    //    }

                        cn= Database.getConnection();
                        query = "update members set aptprompt=?,accountid=?,isValidateEmail=?,daily_amount_limit=?,monthly_amount_limit=?,daily_card_limit=?,weekly_card_limit=?,monthly_card_limit=?,activation=?,partnerId=?,check_limit=?,card_transaction_limit=?,card_check_limit=?,daily_card_amount_limit=?,weekly_card_amount_limit=?,monthly_card_amount_limit=?,isrefund=?,refunddailylimit=? ,agentId=?, maxScoreAllowed=?,maxScoreAutoReversal=?,weekly_amount_limit=? where memberid=?";
                        pstmt= cn.prepareStatement(query);
                        pstmt.setString(1,aptprompt);
                        pstmt.setString(2,accountID);
                        pstmt.setString(3,isValidEmail);
                        pstmt.setString(4,daily_amount_limit);
                        pstmt.setString(5,monthly_amount_limit);
                        pstmt.setString(6,daily_card_limit);
                        pstmt.setString(7,weekly_card_limit);
                        pstmt.setString(8,monthly_card_limit);
                        /*pstmt.setString(9,custrememail);*/
                        pstmt.setString(9,active);
                        pstmt.setString(10,partnerId);
                        pstmt.setString(11,check_limit);
                        pstmt.setString(12,String.valueOf(card_transaction_limit));
                        pstmt.setString(13,String.valueOf(card_check_limit));
                        pstmt.setString(14,daily_card_amount_limit);
                        pstmt.setString(15,weekly_card_amount_limit);
                        pstmt.setString(16,monthly_card_amount_limit);
                        pstmt.setString(17,isrefund);
                        pstmt.setString(18,refunddailylimit);
                        pstmt.setString(19,agentid);
                        pstmt.setString(20,maxScoreAllowed);
                        pstmt.setString(21,maxScoreReversal);
                        pstmt.setString(22,weekly_amount_limit);
                        pstmt.setString(23,memberids[i]);

                        logger.debug("result " + query);
                        int result = pstmt.executeUpdate();
                        logger.debug("result " + query);


                        if (result > 0)
                            updRecs++;
                    }//end for

                }
                catch (Exception e)
                {
                    //e.printStackTrace();
                    logger.error("Error while set reserves :",e);

                    //sErrorMessage.append(errorMsg);
                    req.setAttribute("error",errorMsg);


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

            String redirectpage = "/backofficeaccess.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
        }//post ends
    }
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
            {  logger.error("Number Formet Exception",ne);

                returnVal = "0";
            }
        }
        return returnVal;
    }

    private String validateParameters(HttpServletRequest req)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<br>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        inputFieldsListOptional.add(InputFields.DAILY_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_AMT_LIMIT);

        inputFieldsListOptional.add(InputFields.DAILY_CARD_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_CARD_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_CARD_LIMIT);

        inputFieldsListOptional.add(InputFields.HIGH_RISK_AMT);

        inputFieldsListOptional.add(InputFields.DAILY_CARD_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.WEEKLY_CARD_AMT_LIMIT);
        inputFieldsListOptional.add(InputFields.MONTHLY_CARD_AMT_LIMIT);

        inputFieldsListOptional.add(InputFields.MAX_SCORE_ALLOWED);
        inputFieldsListOptional.add(InputFields.MAX_SCORE_REVERSEL);
        inputFieldsListOptional.add(InputFields.REFUND_DAILY_LIMIT);

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
