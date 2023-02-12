import com.directi.pg.*;
import com.manager.ChargeManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.ChargeVO;
import com.payment.exceptionHandler.PZDBViolationException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/28/14
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateMemberAccountsCharges  extends HttpServlet
{
    private static Logger log = new Logger(UpdateMemberAccountsCharges.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in List Member Accounts Charges");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Functions functions=new Functions();
        log.debug("ctoken==="+req.getParameter("ctoken"));

        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        Connection conn = null;
        String errormsg="";
        String EOL = "<BR>";
        String membercharge=null;
        String agentcharge=null;
        String partnercharge=null;
        String startDate=req.getParameter("startdate");
        String endDate=req.getParameter("enddate");
        String mappingId=req.getParameter("mappingid");
        String lastUpdatedStartDate=req.getParameter("pstartdate");
        String lastUpdatedEndDate=req.getParameter("penddate");
        String memberChargeVal=req.getParameter("memberchargeval");
        String agentChargeVal=req.getParameter("agentchargeval");
        String partnerChargeVal=req.getParameter("partnerchargeval");

        String version=req.getParameter("version");
        StringBuilder sbError = new StringBuilder();
        boolean flag=true;
        if (!ESAPI.validator().isValidInput("memberchargeval",req.getParameter("memberchargeval"),"Numbers",25,false)){
            sbError.append("Invalid Member Value" + EOL);
        }
        else{
            membercharge=req.getParameter("memberchargeval");
        }

        if (!ESAPI.validator().isValidInput("agentchargeval",req.getParameter("agentchargeval"),"Numbers",25,false)){
            sbError.append("Invalid Agent Value" + EOL);
        }
        else{
            agentcharge=req.getParameter("agentchargeval");
        }

        if(!ESAPI.validator().isValidInput("partnerchargeval",req.getParameter("partnerchargeval"),"Numbers",25,false)){
            sbError.append("Invalid Partner Value" + EOL);
        }
        else{
            partnercharge=req.getParameter("partnerchargeval");
        }

        if(!ESAPI.validator().isValidInput("startdate", req.getParameter("startdate"), "fromDate", 20, false)){
            sbError.append("Invalid Start Date" + EOL);
        }
        if (!ESAPI.validator().isValidInput("enddate", req.getParameter("enddate"), "fromDate", 20, false)){
            sbError.append("Invalid End Date" + EOL);
        }

        if (sbError.length() > 0){
            req.setAttribute("errormessage",sbError.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
        String message=commonFunctionUtil.newValidateDate(startDate,endDate,null,null);
        if (functions.isValueNull(message))
        {
            req.setAttribute("errormessage", message);
            RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken=" + user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if("false".equals(version)){
            //TODO:Update merchantChargeValue,agentCommission,partnerCommission,effectiveStartDate,effectiveEndDate in ChargeVersionMemberMaster
            startDate=commonFunctionUtil.convertDatepickerToTimestamp(startDate,"00:00:00");
            endDate=commonFunctionUtil.convertDatepickerToTimestamp(endDate,"23:59:59");

            //TODO:Update chargevalue,agentchargevalue,partnerchargevalue in member_accounts_charges_mapping
            ChargeVO chargeVO = new ChargeVO();
            chargeVO.setChargevalue(memberChargeVal);
            chargeVO.setAgentChargeValue(agentChargeVal);
            chargeVO.setPartnerChargeValue(partnerChargeVal);
            chargeVO.setStartdate(startDate);
            chargeVO.setEnddate(endDate);
            chargeVO.setMappingid(mappingId);
            ChargeManager chargeManager = new ChargeManager();
            try
            {
                boolean result=chargeManager.updateMerchantCharge(chargeVO);
                if(result){
                    errormsg += "<center><font class=\"textb\" ><b>Charge updated successfully. "+ EOL + "</b></font></center>";
                }else
                {
                    errormsg += "<center><font class=\"textb\" ><b> Charge updating failed."+ EOL + "</b></font></center>";
                }
            }
            catch (PZDBViolationException e)
            {
                log.error("PZDBViolationException:::::::::"+e);
            }
        }
        else{
            lastUpdatedStartDate=commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedStartDate);
            lastUpdatedEndDate=commonFunctionUtil.convertTimestampToDatepicker(lastUpdatedEndDate);
            String message1= commonFunctionUtil.newValidateDate(startDate, endDate, lastUpdatedStartDate, lastUpdatedEndDate);
            if(message1!=null){
                req.setAttribute("errormessage",message1);
                RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            PreparedStatement preparedStatement = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try{
                startDate=commonFunctionUtil.convertDatepickerToTimestamp(startDate,"00:00:00");
                endDate=commonFunctionUtil.convertDatepickerToTimestamp(endDate,"23:59:59");

                conn= Database.getConnection();
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
                Date newStartDate = sdf.parse(startDate);
                Date dateBefore = new Date(newStartDate.getTime() - 1 * 24 * 3600 * 1000 );
                String q1="SELECT MAX(chargeversionId) AS chargeversionId FROM ChargeVersionMemberMaster WHERE member_accounts_charges_mapping_id=?";
                preparedStatement=conn.prepareStatement(q1);
                preparedStatement.setString(1,mappingId);
                rs=preparedStatement.executeQuery();
                if(rs.next()){
                    String last_update_version=rs.getString("chargeversionId");
                    String updatememberhistory="UPDATE ChargeVersionMemberMaster SET effectiveEndDate=? WHERE chargeversionId=? AND  member_accounts_charges_mapping_id=?";
                    preparedStatement=conn.prepareStatement(updatememberhistory);
                    preparedStatement.setString(1,sdf.format(dateBefore)+" 23:59:59");
                    preparedStatement.setString(2,last_update_version);
                    preparedStatement.setString(3,mappingId);
                    int i =preparedStatement.executeUpdate();
                    if(i==1){
                        String q2="UPDATE member_accounts_charges_mapping SET chargevalue=?,agentchargevalue=?,partnerchargevalue=? WHERE mappingid=?";
                        preparedStatement=conn.prepareStatement(q2);
                        preparedStatement.setString(1,membercharge);
                        preparedStatement.setString(2,agentcharge);
                        preparedStatement.setString(3,partnercharge);
                        preparedStatement.setString(4,mappingId);
                        int j =preparedStatement.executeUpdate();
                        if(j==1){
                            String qry="INSERT INTO ChargeVersionMemberMaster (merchantChargeValue,agentCommision,partnerCommision,effectiveStartDate,effectiveEndDate,member_accounts_charges_mapping_id) VALUES (?,?,?,?,?,?)";
                            pstmt =conn.prepareStatement(qry);
                            pstmt.setString(1,membercharge);
                            pstmt.setString(2,agentcharge);
                            pstmt.setString(3,partnercharge);
                            pstmt.setString(4,startDate);
                            pstmt.setString(5,endDate);
                            pstmt.setString(6,mappingId);
                            pstmt.executeUpdate();
                            int k =preparedStatement.executeUpdate();
                            if(k==1){
                                errormsg += "<center><font class=\"textb\" ><b>Charge updated successfully. "+ EOL + "</b></font></center>";
                            }
                            else{
                                errormsg += "<center><font class=\"textb\" ><b>Charge updating failed. "+ EOL + "</b></font></center>";
                            }
                        }
                        else{
                            errormsg += "<center><font class=\"textb\" ><b> Charge updating failed. "+ EOL + "</b></font></center>";
                        }
                    }
                    else{
                        errormsg += "<center><font class=\"textb\" ><b> Charge updating failed."+ EOL + "</b></font></center>";
                    }
                }
                else{
                    errormsg += "<center><font class=\"textb\" ><b> update charges record Failed. "+ EOL + "</b></font></center>";
                }

            }
            catch (SystemError systemError){
                log.error("SystemError:::::",systemError);
                req.setAttribute("errormessage","Internal error while updating charge on member account.");
                RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            catch (SQLException e){
                log.error("SQLException::::::",e);
                req.setAttribute("errormessage","Internal error while updating charge on member account.");
                RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            catch (ParseException e){
                log.error("ParseException:::::",e);
                req.setAttribute("errormessage","Internal error while updating charge on member account.");
                RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            finally
            {
                Database.closeResultSet(rs);
                Database.closePreparedStatement(pstmt);
                Database.closePreparedStatement(preparedStatement);
                Database.closeConnection(conn);
            }
        }
        req.setAttribute("errormessage",errormsg);
        RequestDispatcher rd = req.getRequestDispatcher("/actionmemberaccountcharges.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
