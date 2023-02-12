import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 4/2/2016.
 */
public class UpdateTerminalThreshold extends HttpServlet
{
    private static Logger logger = new Logger(UpdateTerminalThreshold.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String error = "";
        String success = "";
        String action = "";
        Connection conn = null;

        if (!ESAPI.validator().isValidInput("action ", req.getParameter("action"), "SafeString", 100, false))
        {
            logger.debug("Select valid action");
            error += "Invalid Action Selected";
        }
        else
            action = req.getParameter("action");

        if (action.equalsIgnoreCase("UPDATE"))
        {
            PreparedStatement pstmnt = null;
            try
            {
                conn = Database.getConnection();
                error = validateMandatoryParameters(req, action);
                if (error.equals(""))
                {
                    String memberId = req.getParameter("memberid");
                    String accountid = req.getParameter("accountid");
                    String terminalId = req.getParameter("terminalid");

                    String daily_approval_ratio = req.getParameter("daily_approval_ratio");
                    String weekly_approval_ratio = req.getParameter("weekly_approval_ratio");
                    String monthly_approval_ratio = req.getParameter("monthly_approval_ratio");

                    String inactive_period_threshold = req.getParameter("inactive_period_threshold");
                    String first_submission_threshold = req.getParameter("first_submission_threshold");
                    String manual_capture_threshold = req.getParameter("manual_capture_threshold");

                    String daily_cb_ratio = req.getParameter("daily_cb_ratio");
                    String weekly_cb_ratio = req.getParameter("weekly_cb_ratio");
                    String monthly_cb_ratio = req.getParameter("monthly_cb_ratio");

                    String daily_cb_ratio_amount = req.getParameter("daily_cb_ratio_amount");
                    String weekly_cb_ratio_amount = req.getParameter("weekly_cb_ratio_amount");
                    String monthly_cb_ratio_amount = req.getParameter("monthly_cb_ratio_amount");

                    String daily_rf_ratio = req.getParameter("daily_rf_ratio");
                    String weekly_rf_ratio = req.getParameter("weekly_rf_ratio");
                    String monthly_rf_ratio = req.getParameter("monthly_rf_ratio");

                    String daily_rf_ratio_amount = req.getParameter("daily_rf_ratio_amount");
                    String weekly_rf_ratio_amount = req.getParameter("weekly_rf_ratio_amount");
                    String monthly_rf_ratio_amount = req.getParameter("monthly_rf_ratio_amount");

                    String daily_avgticket_threshold = req.getParameter("daily_avgticket_threshold");
                    String weekly_avgticket_threshold = req.getParameter("weekly_avgticket_threshold");
                    String monthly_avgticket_threshold = req.getParameter("monthly_avgticket_threshold");

                    String daily_vs_quarterly_avgticket_threshold = req.getParameter("daily_vs_quarterly_avgticket_threshold");
                    String alert_cbcount_threshold = req.getParameter("alert_cbcount_threshold");
                    String suspend_cbcount_threshold = req.getParameter("suspend_cbcount_threshold");

                    String priormonth_rf_vs_currentmonth_sales_threshold = req.getParameter("priormonth_rf_vs_currentmonth_sales_threshold");
                    String samecard_cardamount_threshold = req.getParameter("samecard_cardamount_threshold");

                    String resume_processing_alert = req.getParameter("resume_processing_alert");
                    String daily_avgticket_percentage_threshold = req.getParameter("daily_avgticket_percentage_threshold");
                    String daily_cb_ratio_suspension = req.getParameter("daily_cb_ratio_suspension");

                    String daily_cb_amount_ratio_suspension = req.getParameter("daily_cb_amount_ratio_suspension");
                    String samecard_sameamount_consequence_threshold = req.getParameter("samecard_sameamount_consequence_threshold");
                    String samecard_consequently_threshold = req.getParameter("samecard_consequently_threshold");

                    String query = "update member_terminal_threshold  set daily_approval_ratio=?,weekly_approval_ratio=?,monthly_approval_ratio=?,daily_cb_ratio=?,weekly_cb_ratio=?,monthly_cb_ratio=?,daily_rf_ratio=?,weekly_rf_ratio=?,monthly_rf_ratio=?,inactive_period_threshold=?,first_submission_threshold=?,daily_avgticket_threshold=?,weekly_avgticket_threshold=?,monthly_avgticket_threshold=?,suspend_cbcount_threshold=?,monthly_cb_amount_ratio=?,priormonth_rf_vs_currentmonth_sales_threshold=?,manualcapture_alert_threshold=?,samecard_sameamount_threshold=?,daily_vs_quarterly_avgticket_threshold=?,alert_cbcount_threshold=?,daily_cb_amount_ratio=?,weekly_cb_amount_ratio=?,daily_rf_amount_ratio=?,weekly_rf_amount_ratio=?,monthy_rf_amount_ratio=?,resume_processing_alert=?,daily_avgticket_percentage_threshold=?,daily_cb_ratio_suspension=?,daily_cb_amount_ratio_suspension=?,samecard_sameamount_consequence_threshold=?,samecard_consequently_threshold=? where memberid=? and terminalid=?";
                    pstmnt = conn.prepareStatement(query);
                    pstmnt.setString(1, daily_approval_ratio);
                    pstmnt.setString(2, weekly_approval_ratio);
                    pstmnt.setString(3, monthly_approval_ratio);
                    pstmnt.setString(4, daily_cb_ratio);
                    pstmnt.setString(5, weekly_cb_ratio);
                    pstmnt.setString(6, monthly_cb_ratio);
                    pstmnt.setString(7, daily_rf_ratio);
                    pstmnt.setString(8, weekly_rf_ratio);
                    pstmnt.setString(9, monthly_rf_ratio);
                    pstmnt.setString(10, inactive_period_threshold);
                    pstmnt.setString(11, first_submission_threshold);
                    pstmnt.setString(12, daily_avgticket_threshold);
                    pstmnt.setString(13, weekly_avgticket_threshold);
                    pstmnt.setString(14, monthly_avgticket_threshold);
                    pstmnt.setString(15, suspend_cbcount_threshold);
                    pstmnt.setString(16, monthly_cb_ratio_amount);
                    pstmnt.setString(17, priormonth_rf_vs_currentmonth_sales_threshold);
                    pstmnt.setString(18, manual_capture_threshold);
                    pstmnt.setString(19, samecard_cardamount_threshold);
                    pstmnt.setString(20, daily_vs_quarterly_avgticket_threshold);
                    pstmnt.setString(21, alert_cbcount_threshold);
                    pstmnt.setString(22, daily_cb_ratio_amount);
                    pstmnt.setString(23, weekly_cb_ratio_amount);
                    pstmnt.setString(24, daily_rf_ratio_amount);
                    pstmnt.setString(25, weekly_rf_ratio_amount);
                    pstmnt.setString(26, monthly_rf_ratio_amount);
                    pstmnt.setString(27, resume_processing_alert);
                    pstmnt.setString(28, daily_avgticket_percentage_threshold);
                    pstmnt.setString(29, daily_cb_ratio_suspension);
                    pstmnt.setString(30, daily_cb_amount_ratio_suspension);
                    pstmnt.setString(31, samecard_sameamount_consequence_threshold);
                    pstmnt.setString(32, samecard_consequently_threshold);
                    pstmnt.setString(33, memberId);
                    pstmnt.setString(34, terminalId);

                    int k = pstmnt.executeUpdate();
                    if (k > 0)
                    {
                        success = "Terminal Ratio Configuration Updated Successfully.";
                    }
                    else
                    {
                        success = "Could Not Update Terminal Ratio Configuration.";
                    }
                }
            }
            catch (SystemError e)
            {
                logger.error("SystemError while updating terminal ratio configuration", e);
                error = error + "Could Not Update Terminal Ratio Configuration.";
            }
            catch (SQLException e)
            {
                logger.error("SQLException while updating terminal ratio configuration", e);
                error = error + "Could Not Update Terminal ratio Configuration.";
            }
            finally
            {
                Database.closePreparedStatement(pstmnt);
                Database.closeConnection(conn);
            }
        }
        req.setAttribute("success1", success);
        req.setAttribute("error1", error);
        RequestDispatcher rd = req.getRequestDispatcher("/merchantTerminalThreshold.jsp?ctoken=" + user.getCSRFToken());
        rd.forward(req, res);
    }

    private String validateMandatoryParameters(HttpServletRequest req, String action)
    {
        InputValidator inputValidator = new InputValidator();
        String error = "";
        String EOL = "<BR>";
        List<InputFields> inputFieldsListOptional = new ArrayList<InputFields>();

        if (action.equalsIgnoreCase("UPDATE"))
        {
            inputFieldsListOptional.add(InputFields.DAILY_APPROVAL_RATIO);
            inputFieldsListOptional.add(InputFields.WEEKLY_APPROVAL_RATIO);
            inputFieldsListOptional.add(InputFields.MONTHLY_APPROVAL_RATIO);

            inputFieldsListOptional.add(InputFields.DAILY_CHARGEBACK_RATIO);
            inputFieldsListOptional.add(InputFields.WEEKLY_CHARGEBACK_RATIO);
            inputFieldsListOptional.add(InputFields.MONTHLY_CHARGEBACK_RATIO);

            inputFieldsListOptional.add(InputFields.DAILY_CHARGEBACK_RATIO_AMOUNT);
            inputFieldsListOptional.add(InputFields.WEEKLY_CHARGEBACK_RATIO_AMOUNT);
            inputFieldsListOptional.add(InputFields.AMOUNT_MONTHLY_CHARGEBACK_RATIO);

            inputFieldsListOptional.add(InputFields.DAILY_REFUND_RATIO);
            inputFieldsListOptional.add(InputFields.WEEKLY_REFUND_RATIO);
            inputFieldsListOptional.add(InputFields.MONTHLY_REFUND_RATIO);

            inputFieldsListOptional.add(InputFields.DAILY_REFUND_RATIO_AMOUNT);
            inputFieldsListOptional.add(InputFields.WEEKLY_REFUND_RATIO_AMOUNT);
            inputFieldsListOptional.add(InputFields.MONTHLY_REFUND_RATIO_AMOUNT);

            inputFieldsListOptional.add(InputFields.INACTIVE_PERIOD);
            inputFieldsListOptional.add(InputFields.FIRST_SUBMISSION);

            inputFieldsListOptional.add(InputFields.DAILY_AVGTICKET_AMOUNT);
            inputFieldsListOptional.add(InputFields.WEEKLY_AVGTICKET_AMOUNT);
            inputFieldsListOptional.add(InputFields.MONTHLY_AVGTICKET_AMOUNT);

            inputFieldsListOptional.add(InputFields.MANUAL_CAPTURE_THRESHOLD);
            inputFieldsListOptional.add(InputFields.CHARGEBACK_INCOUNT);
            inputFieldsListOptional.add(InputFields.MONTHLY_CREDIT_AMOUNT);

            inputFieldsListOptional.add(InputFields.SAME_CARDAMOUNT_THRESHOLD);
            inputFieldsListOptional.add(InputFields.DAILY_QUARTERLY_AVGTICKET_THRESHOLD);
            inputFieldsListOptional.add(InputFields.CHARGEBACK_INCOUNT_FORALERT);
            inputFieldsListOptional.add(InputFields.RESUME_PROCESSING_ALERT);
            inputFieldsListOptional.add(InputFields.DAILY_AVGTICKET_PERCENTAGE_THRESHOLD);

            inputFieldsListOptional.add(InputFields.DAILY_CB_RATIO_SUSPENSION);
            inputFieldsListOptional.add(InputFields.DAILY_CB_AMOUNT_RATIO_SUSPENSION);
            inputFieldsListOptional.add(InputFields.SAMECARD_SAMEAMOUNT_CONSEQUENCE_THRESHOLD);
            inputFieldsListOptional.add(InputFields.SAMECARD_CONSEQUENTLY_THRESHOLD);

            inputFieldsListOptional.add(InputFields.MEMBERID);
            inputFieldsListOptional.add(InputFields.ACCOUNTID_SMALL);
            inputFieldsListOptional.add(InputFields.TERMINALID);
        }

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req, inputFieldsListOptional, errorList, false);

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : inputFieldsListOptional)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        return error;
    }
}
