package net.partner;

import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ChargebackManager;
import com.manager.dao.ChargebackDAO;
import com.manager.vo.InputDateVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionVO;
import com.manager.vo.payoutVOs.CBReasonsVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima Rai.
 * Date: 17/05/18
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerMerchantChargebackList extends HttpServlet
{
    private static Logger log = new Logger(PartnerMerchantChargebackList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in PartnerMerchantChargeback");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        ChargebackManager chargebackManager = new ChargebackManager();
        ChargebackDAO chargebackdao = new ChargebackDAO();
        TransactionVO transactionVO = new TransactionVO();
        PaginationVO paginationVO = new PaginationVO();
        InputDateVO inputDateVO = new InputDateVO();

        List<TransactionVO> cbVOList = null;
        List<CBReasonsVO> reasonVOList = null;
        Functions functions = new Functions();

        String error= "";
        String toId = "";
        String trackingId="";
        String orderId="";
        String toType="";
        String paymentid="";
        String partnerid = String.valueOf(session.getAttribute("merchantid"));
        //String errormsg = "";
        //String EOL = "<BR>";


        toId=req.getParameter("toid");
        trackingId= req.getParameter("trackingid");
        orderId=req.getParameter("description");
        paymentid=req.getParameter("paymentid");

        if(functions.isValueNull(req.getParameter("trackingid")) && !functions.isNumericVal(req.getParameter("trackingid")))
        {
            log.error("Invalid TrackingID allowed only numeric value e.g.[0 to 9] .");
            error="Invalid TrackingID allowed only numeric value e.g.[0 to 9].";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(functions.isValueNull(req.getParameter("toid")) && !functions.isNumericVal(req.getParameter("toid")))
        {
            log.error("Invalid memberid.");
            error="Invalid Member ID";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (!ESAPI.validator().isValidInput("pid",req.getParameter("pid"),"Numbers",10,true))
        {
            error="Invalid Partner Id.";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if (!ESAPI.validator().isValidInput("trackingid",req.getParameter("trackingid"),"Numbers",100,true))
        {
            log.error("Invalid TrackingID.");
            error="Invalid TrackingID.";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            trackingId = req.getParameter("trackingid");
        }
        if (!ESAPI.validator().isValidInput("description",req.getParameter("description"),"Description",60,true))
        {
            log.error("Invalid Description");
            error="Invalid Order ID.";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        else
        {
            orderId=req.getParameter("description");
        }
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            error="<center><table align=\"center\" class=\"textb\" >" +error +e.getMessage() +"</table></center>";
            req.setAttribute("error",error);
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        if(functions.isValueNull(req.getParameter("SPageno")))
        {
            int page = Integer.parseInt(req.getParameter("SPageno"));
            if (page <= 0)
            {
                error = "<center><table align=\"center\" class=\"textb\" >" + "Invalid Page No." + "</table></center>";
                log.debug("Entering in Invalid Page No Error ->"+error);
                req.setAttribute("error", error);
                RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken=" + user.getCSRFToken());
                rd.forward(req, res);
                return;
            }
            log.debug("Entering in Invalid Page No Error ->"+error);
        }

        try
        {
            if(functions.isValueNull(error))
            {
                PZExceptionHandler.raiseConstraintViolationException("PartnerMerchantChargebackList.java", "doPost()", null, "partner", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
            }
            StringBuffer trackingIds=new StringBuffer();
            if (functions.isValueNull(trackingId))
            {
                List<String> trackingidList = null;
                if(trackingId.contains(","))
                {
                    trackingidList = Arrays.asList(trackingId.split(","));
                }
                else
                {
                    trackingidList = Arrays.asList(trackingId.split(" "));
                }

                int i = 0;
                Iterator itr = trackingidList.iterator();
                while (itr.hasNext())
                {
                    if(i!=0)
                    {
                        trackingIds.append(",");
                    }
                    trackingIds.append(""+itr.next()+"");
                    i++;
                }
            }
            PartnerFunctions partner = new PartnerFunctions();
            String pid = req.getParameter("pid");
            if(functions.isValueNull(pid)&& partner.isPartnerSuperpartnerMapped(pid,partnerid)){
                toType = "'"+partner.getPartnerName(pid)+"'";
            }else if(!functions.isValueNull(pid)){
                String ID = String.valueOf(session.getAttribute("merchantid"));
                String Roles = partner.getRoleofPartner(ID);
                if(Roles.contains("superpartner")){
                    Connection con = null;
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    String name="'"+(String)session.getAttribute("partnername")+"'";
                    try
                    {
                        con = Database.getRDBConnection();
                        String qry = "select partnerName AS NAME from partners where superadminid=?";
                        pstmt = con.prepareStatement(qry);
                        pstmt.setString(1, ID);
                        rs = pstmt.executeQuery();
                        while (rs.next())
                        {
                            name = name + ", '"+rs.getString("NAME")+"'";
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("Exception---" + e);
                    }
                    finally
                    {
                        Database.closeResultSet(rs);
                        Database.closePreparedStatement(pstmt);
                        Database.closeConnection(con);
                    }
                    toType=name;
                }else{
                    toType="'"+(String)session.getAttribute("partnername")+"'";
                }
            }else{
                toType = "'NULL'";
            }

            if(functions.isValueNull(toId) && !partner.isPartnerSuperpartnerMembersMapped(toId,partnerid)){
                toId="0";
            }

            transactionVO.setOrderId(orderId);
            transactionVO.setToid(toId);
            transactionVO.setTrackingId(trackingIds.toString());
            transactionVO.setToType(toType);
            transactionVO.setPaymentId(paymentid);

            paginationVO.setPageNo(Functions.convertStringtoInt(req.getParameter("SPageno"), 1));
            paginationVO.setPage(PartnerMerchantChargebackList.class.getName());
            paginationVO.setCurrentBlock(Functions.convertStringtoInt(req.getParameter("currentblock"),1));
            paginationVO.setRecordsPerPage(Functions.convertStringtoInt(req.getParameter("SRecords"),15));
            int totalrecords= chargebackdao.getCountofCommonChargebacklistNew(transactionVO,inputDateVO,paginationVO);
            cbVOList = chargebackdao.getCommonChargebackListNew(transactionVO, inputDateVO, paginationVO,totalrecords);
            reasonVOList = chargebackManager.getCBReasonList();

            req.setAttribute("trackingid",trackingId);
            req.setAttribute("toid",toId);
            req.setAttribute("description",orderId);
            req.setAttribute("cbVO",cbVOList);
            req.setAttribute("reason",reasonVOList);
            req.setAttribute("paginationVO",paginationVO);
            req.setAttribute("error",error);

            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException:::::", dbe);
            req.setAttribute("error","No record found");
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException:::::", cve);
            req.setAttribute("error",cve.getPzConstraint().getMessage());
            RequestDispatcher rd = req.getRequestDispatcher("/partnerMerchantChargebackList.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
            return;
        }
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.PAYMENTID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}