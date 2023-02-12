package net.partner;

import com.directi.pg.*;
import com.manager.PartnerManager;
import com.manager.vo.PartnerDetailsVO;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.User;

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
import java.util.ResourceBundle;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 4/15/15
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SetThemeColor extends HttpServlet
{
    private static Logger logger = new Logger(SetThemeColor.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String SetThemeColor_Updated_Successfully_errormsg = StringUtils.isNotEmpty(rb1.getString("SetThemeColor_Updated_Successfully_errormsg")) ? rb1.getString("SetThemeColor_Updated_Successfully_errormsg") : "Updated Successfully";



        PartnerFunctions partner=new PartnerFunctions();
        Functions functions = new Functions();
        RequestDispatcher rdSuccess= req.getRequestDispatcher("/merchantTheme.jsp?&ctoken="+user.getCSRFToken());
        String errorMsg = "";
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }

        /*if(!partner.isEmptyOrNull(errorMsg))
        {

            String redirectpage = "/transactionsprocess.jsp?ctoken="+user.getCSRFToken();
            req.setAttribute("error", errorMsg);
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);
            return;
        }*/
        else
        {

            String partnerid=((String)session.getAttribute("merchantid"));
            //System.out.println("partnerid----------"+partnerid);
            String currentTheme = req.getParameter("currenttemplatetheme");

            //System.out.println("currenttemplatetheme in servlet...."+currentTheme);
            //System.out.println("");

            String query = null;

            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            int updRecs = 0;
            Connection cn=null;
            PreparedStatement pstmt = null;
            if (partnerid != null)
            { //process only if there is at least one record to be updated
                try
                {
                        cn= Database.getConnection();
                        query = "update partners set current_theme =? where partnerId=?";
                        pstmt= cn.prepareStatement(query);
                        pstmt.setString(1,currentTheme);
                        pstmt.setString(2,partnerid);
                        int result = pstmt.executeUpdate();
                    String success = "";
                        if(result > 0)
                        {
                            success = SetThemeColor_Updated_Successfully_errormsg;
                            Merchants.refresh();
                            //System.out.println("Merchants.refresh() called::::::");
                        }
                        //System.out.println("result count......."+result);
                        /*if(result>0)
                        {
                            System.out.println("inside result.......");

                        }*/
                        //System.out.println("flightmode================"+flightMode);
                        //MVC Pattern
                        PartnerManager partnerManager = new PartnerManager();

                        PartnerDetailsVO partnerDetailsVOs= partnerManager.getselfPartnerDetails(partnerid);
                        //System.out.println("partnerDetailsVOs------------"+partnerDetailsVOs);
                        req.setAttribute("partnerDetailsVOs", partnerDetailsVOs);
                        logger.debug("partnerDetailsVOs getPartnerId:::::" + partnerDetailsVOs.getPartnerId());
                        logger.debug("partnerDetailsVOs getAddress:::::"+partnerDetailsVOs.getAddress());
                        //System.out.println("Default theme" + partnerDetailsVOs.getDefaultTheme());
                        //System.out.println("Current theme"+partnerDetailsVOs.getCurrentTheme())

                        req.setAttribute("msg", success);
                        rdSuccess.forward(req,res);



                }
                catch (Exception e)
                {

                    logger.error("Error while set reserves :",e);
                    req.setAttribute("error",errorMsg);
                }
                finally
                {
                    Database.closePreparedStatement(pstmt);
                    Database.closeConnection(cn);
                }
            }

            //sSuccessMessage.append(updRecs + " Records Updated");

            /*StringBuilder chargeBackMessage = new StringBuilder();
            //chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            //chargeBackMessage.append(sErrorMessage.toString());

            String redirectpage = "/merchantTheme.jsp?ctoken="+user.getCSRFToken();
            //req.setAttribute("cbmessage", sSuccessMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);*/

        }
    }
}
