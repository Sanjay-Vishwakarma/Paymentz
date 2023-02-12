import com.directi.pg.*;
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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BlockCountry extends HttpServlet
{
    private static Logger logger = new Logger(BlockCountry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in BlockCountry");
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        int insertcount = 0, result = 0;
        String incorrectCountries = "", preExistingCountries = "";

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection connection = null;
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            connection = Database.getConnection();
            String countryQuery = "select trim(code) as code,trim(name) as name from country_code2name";
            rs = Database.executeQuery(countryQuery, connection);
            ArrayList arrCodes = new ArrayList();
            ArrayList arrNames = new ArrayList();
            while (rs.next())
            {
                arrCodes.add(rs.getString("code"));
                arrNames.add(rs.getString("name"));
            }
            rs.close();
            for (int i = 1; i <= 5; i++)
            {
                String country = Functions.checkStringNull(req.getParameter("country" + i));
                if (country != null  && !ESAPI.validator().isValidInput("country",req.getParameter("country" + i),"SafeString",50,false))
                {
                    country = country.trim();
                    String code = getCode(country, arrCodes, arrNames);

                    if (code.length() > 0)
                    {// check validity of country
                        countryQuery = "select * from blockedcountry where country=?";
                        PreparedStatement ps = connection.prepareStatement(countryQuery);
                        ps.setString(1,code);
                        rs = ps.executeQuery();
                        if (rs.next())
                        {
                            preExistingCountries += country + " ";
                        }
                        else
                        {
                            countryQuery = "insert into blockedcountry(country,countryname) values(?,?)";
                            pstmt= connection.prepareStatement(countryQuery);
                            pstmt.setString(1,code);
                            pstmt.setString(2,getName(code, arrCodes, arrNames));
                            logger.debug("insert query for insert Country in database");
                            result = pstmt.executeUpdate();
                            if (result > 0)
                                insertcount++;
                        }
                    }
                    else
                        incorrectCountries += country + " ";
                }
            }
            String message = "";
            if (Functions.checkStringNull(preExistingCountries) != null)

                sErrorMessage.append("Below Countries were already blocked.<br>" + preExistingCountries + "<br>&nbsp;<br>");
            if (Functions.checkStringNull(incorrectCountries) != null)

                sErrorMessage.append("Below Countries are not blocked due to incorrect name.<br>" + incorrectCountries);

            if (insertcount > 0)

                sSuccessMessage.append(insertcount + " Country/Countries are blocked.<br>&nbsp;<br>");
            else
                out.println(Functions.ShowMessage("Information", "No Country is blocked.<br>&nbsp;<br>" + message));
            sErrorMessage.append("No Country is blocked.<br>&nbsp;<br>");
        }
        catch (SystemError se)
        {   logger.error("System Error::: in BlockCountry",se);

            sErrorMessage.append("Internal System Error");
        }
        catch (Exception e)
        {   logger.error("Exception occure in Block Country::::",e);

            sErrorMessage.append("Internal System Error");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(connection);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/servlet/BlockedCountryList?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);
    }

    private String getCode(String name, ArrayList arrCodes, ArrayList arrNames)
    {
        String code = "";
        boolean flag = true;
        int size = arrNames.size();
        for (int i = 0; i < size; i++)
        {
            if (name.equalsIgnoreCase(arrNames.get(i).toString()))
            {
                code = arrCodes.get(i).toString();
                flag = false;
                break;
            }
        }
        if (flag)
            for (int i = 0; i < size; i++)
            {
                if (name.equalsIgnoreCase(arrCodes.get(i).toString()))
                {
                    code = name.toUpperCase();
                    break;
                }
            }

        return code;
    }

    private String getName(String code, ArrayList arrCodes, ArrayList arrNames)
    {
        String name = "";
        int size = arrCodes.size();
        for (int i = 0; i < size; i++)
        {
            if (code.equalsIgnoreCase(arrCodes.get(i).toString()))
            {
                name = arrNames.get(i).toString();
                break;
            }
        }
        return name;
    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.COUNTRY);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}
