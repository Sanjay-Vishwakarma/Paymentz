import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.ISOCommReportVO;
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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 2/10/15
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateISOCommWire  extends HttpServlet
{
    Logger logger=new Logger(UpdateISOCommWire.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuffer errorMsg=new StringBuffer();

        String isoCommId = request.getParameter("isocommid");
        String settledDate = request.getParameter("settledate");

        String status= request.getParameter("status");

        if(!ESAPI.validator().isValidInput("settledate",settledDate,"fromDate",255,false))
        {
            errorMsg.append("Invalid Settled Date<BR>");
        }
        if(!ESAPI.validator().isValidInput("amount",request.getParameter("amount"),"AmountMinus",10,false))
        {
            errorMsg.append("Invalid Amount<BR>");
        }
        if(!ESAPI.validator().isValidInput("netfinalamount",request.getParameter("netfinalamount"),"AmountMinus",10,false))
        {
            errorMsg.append("Invalid Net Final Amount<BR>");
        }
        if(!ESAPI.validator().isValidInput("unpaidamount",request.getParameter("unpaidamount"),"AmountMinus",10,false))
        {
            errorMsg.append("Invalid Unpaid Amount<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("message",errorMsg.toString());
            RequestDispatcher rd=request.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
            return;
        }

        double amount = Double.valueOf(request.getParameter("amount"));
        double netFinalAmount = Double.valueOf(request.getParameter("netfinalamount"));
        double unpaidAmount = Double.valueOf(request.getParameter("unpaidamount"));

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        SimpleDateFormat actualDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetDateFormatter=new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            settledDate=targetDateFormatter.format(actualDateFormat.parse(settledDate));
            settledDate=settledDate+" "+hour+":"+minutes+":"+seconds;

            ISOCommReportVO isoCommReportVO=new ISOCommReportVO();
            isoCommReportVO.setIsoWireId(isoCommId);
            isoCommReportVO.setSettledDate(settledDate);
            isoCommReportVO.setAmount(amount);
            isoCommReportVO.setNetfinalamount(netFinalAmount);
            isoCommReportVO.setUnpaidAmount(unpaidAmount);
            isoCommReportVO.setStatus(status);
            boolean b=updateISOCommissionWire(isoCommReportVO);
            if(b)
            {
                errorMsg.append("Commission wire update successfully.");
            }
            else
            {
                errorMsg.append("Commission wire updation failed.");
            }
        }
        catch (SQLException se)
        {
            logger.error("Exception::::::::"+se);
            errorMsg.append("Internal error while processing request");
        }
        catch (Exception e)
        {
            logger.error("Exception::::::::"+e);
            errorMsg.append("Internal error while processing request");
        }
        request.setAttribute("message", errorMsg.toString());
        RequestDispatcher rd = request.getRequestDispatcher("/actionISOCommissionWireManager.jsp?ctoken="+user.getCSRFToken());
        rd.forward(request, response);
    }

    public boolean updateISOCommissionWire(ISOCommReportVO isoCommReportVO)throws SQLException,SystemError
    {
        Connection con = null;
        PreparedStatement pstmt=null;
        int k=0;
        boolean b=false;
        try
        {
            con = Database.getConnection();
            StringBuffer query = new StringBuffer("update iso_commission_wire_manager set settleddate=?,amount=?,netfinalamount=?,unpaidamount=?,status=? where iso_comm_id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, isoCommReportVO.getSettledDate());
            pstmt.setDouble(2,isoCommReportVO.getAmount());
            pstmt.setDouble(3, isoCommReportVO.getNetfinalamount());
            pstmt.setDouble(4, isoCommReportVO.getUnpaidAmount());
            pstmt.setString(5,isoCommReportVO.getStatus());
            pstmt.setString(6,isoCommReportVO.getIsoWireId());
            k=pstmt.executeUpdate();
            if(k>0)
            {
                b=true;
            }
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }
}
