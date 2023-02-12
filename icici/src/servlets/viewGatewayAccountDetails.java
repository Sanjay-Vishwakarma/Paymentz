import com.directi.pg.*;

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
import java.sql.*;
import java.util.Hashtable;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 3/2/14
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class viewGatewayAccountDetails extends HttpServlet
{
    private static Logger log = new Logger(viewGatewayAccountDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String accountid="";
        String action = "";
        String isReadOnly = "view";

        try
        {
            accountid = ESAPI.validator().getValidInput("accountid",req.getParameter("accountid"),"Numbers",10,true);
            action = ESAPI.validator().getValidInput("action",req.getParameter("action"),"SafeString",30,true);

        }
        catch (ValidationException e)
        {
            log.error("ValidationException while getting GatewayAccountDetails ",e);
        }
        Connection conn = null;
        Hashtable hash = null;
        String modify = "disabled";
        StringBuilder dataStringBuilder = new StringBuilder();
        String tableColumn = "";
        String gateway_table_name = "";
        String gateway = "";
        String currency = "";
        String pgtypeid = null;
        Functions functions=new Functions();
        PreparedStatement ps = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        ResultSet resultset = null;
        PreparedStatement preparedStatementData = null;
        ResultSet tableMetaDataRs = null;
        try
        {
            conn = Database.getConnection();
            String query = "SELECT * FROM gateway_accounts WHERE accountid=?";
            ps = conn.prepareStatement(query);
            ps.setString(1,accountid);
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSet(rs);
            if(action.equalsIgnoreCase("modify")) {
                isReadOnly="modify";
                modify = "";
            }

            // getting table data
            Hashtable innerHash = (Hashtable)hash.get(1 + "");
            pgtypeid = (String)innerHash.get("pgtypeid");
            gateway_table_name = "";
            gateway = "";

            if(pgtypeid != null)
            {
                String tableNameQuery = "select gateway_table_name,gateway,currency from gateway_type where pgtypeid = ?";
                preparedStatement = conn.prepareStatement(tableNameQuery);
                preparedStatement.setString(1, pgtypeid);
                resultset = preparedStatement.executeQuery();
                while(resultset.next())
                {
                    gateway_table_name = resultset.getString("gateway_table_name");
                    gateway = resultset.getString("gateway");
                    currency = resultset.getString("currency");
                }

                if(functions.isValueNull(gateway_table_name))
                {
                    try
                    {
                        String tableDataQuery = "select * from " + gateway_table_name + " where accountid = ?";
                        preparedStatementData = conn.prepareStatement(tableDataQuery);
                        preparedStatementData.setInt(1,Integer.parseInt(accountid));

                        tableMetaDataRs = preparedStatementData.executeQuery();
                        Integer count = tableMetaDataRs.getMetaData().getColumnCount();

                        boolean containData = false;

                        while(tableMetaDataRs.next())
                        {
                            containData = true;
                            for(int i=1;i<=count;i++)
                            {
                                String coulumnName = tableMetaDataRs.getMetaData().getColumnName(i);
                                String columnNameLabel = tableMetaDataRs.getMetaData().getColumnName(i);
                                int columnNameSize = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
                                String columnType = tableMetaDataRs.getMetaData().getColumnTypeName(i);
                                String temp = tableMetaDataRs.getString(coulumnName);
                                if(!functions.isValueNull(temp))
                                {
                                    temp="";
                                }
                                String isOptional="true";
                                columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
                                if (tableMetaDataRs.getMetaData().isNullable(i) == 0)
                                {
                                    columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1) + "*";
                                    isOptional="false";
                                }

                                dataStringBuilder.append("<tr>");
                                dataStringBuilder.append("<td class=tr1>" + columnNameLabel + "</td>");
                                dataStringBuilder.append("<td class=tr1><input type=\"hidden\" name=\""+coulumnName+"_isOptional\" value=\""+isOptional+"\"><input type=\"hidden\" name=\""+coulumnName+"_size\" value=\""+columnNameSize+"\"><input class=txtbox1  type='text' name='" + coulumnName + "' value='" + temp + "'" + modify + ">( " + columnType + " - "+columnNameSize+")</td>");
                                dataStringBuilder.append("<input type='hidden' name='" + coulumnName + "' value='" + temp + "'>");
                                dataStringBuilder.append("</tr>");
                                tableColumn += coulumnName + ",";
                            }
                        }

                        if(!containData)
                        {
                            tableDataQuery = "select * from " + gateway_table_name + " where 1=1 limit 1";
                            preparedStatementData = conn.prepareStatement(tableDataQuery);

                            tableMetaDataRs = preparedStatementData.executeQuery();
                            count = tableMetaDataRs.getMetaData().getColumnCount();
                            while(tableMetaDataRs.next())
                            {
                                containData = true;
                                for(int i=1;i<=count;i++)
                                {
                                    String coulumnName = tableMetaDataRs.getMetaData().getColumnName(i);
                                    String columnNameLabel = tableMetaDataRs.getMetaData().getColumnName(i);
                                    int columnNameSize = tableMetaDataRs.getMetaData().getColumnDisplaySize(i);
                                    String columnType = tableMetaDataRs.getMetaData().getColumnTypeName(i);

                                    String temp = tableMetaDataRs.getString(coulumnName);
                                    columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1);
                                    String isOptional="false";
                                    if (tableMetaDataRs.getMetaData().isNullable(i) == 0)
                                    {
                                        columnNameLabel = columnNameLabel.substring(0, 1).toUpperCase() + columnNameLabel.substring(1) + "*";
                                        isOptional="true";
                                    }

                                    dataStringBuilder.append("<tr>");
                                    dataStringBuilder.append("<td class=tr1>" + columnNameLabel + "</td>");
                                    dataStringBuilder.append("<td class=tr1><input type=\"hidden\" name=\""+coulumnName+"_isOptional\" value=\""+isOptional+"\"><input type=\"hidden\" name=\""+coulumnName+"_size\" value=\""+columnNameSize+"\"><input class=txtbox1  type='text' name='" + coulumnName + "' value='" + temp + "'" + modify + ">( " + columnType + " - "+columnNameSize+")</td>");
                                    dataStringBuilder.append("<input type='hidden' name='" + coulumnName + "' value='" + temp + "'>");
                                    dataStringBuilder.append("</tr>");

                                    tableColumn += coulumnName + ",";
                                }
                            }
                        }
                        if(tableColumn.length() > 0)
                            tableColumn = tableColumn.substring(0,tableColumn.length()-1);
                    }
                    catch (Exception e)
                    {
                        log.error("Exception while getting the gateway accounts details",e);
                    }
                }
            }
            // ends here
        }catch (SystemError systemError)
        {
            log.error("Sql Exception :::::",systemError);
            log.error("Exception while updating template fees : " + systemError.getMessage());

        }catch (Exception e)
        {
            log.error("Sql Exception :::::",e);
            log.error("Exception while updating template fees : " + e.getMessage());


        }finally
        {
            Database.closeResultSet(rs);
            Database.closeResultSet(resultset);
            Database.closeResultSet(tableMetaDataRs);
            Database.closePreparedStatement(ps);
            Database.closePreparedStatement(preparedStatement);
            Database.closePreparedStatement(preparedStatementData);
            Database.closeConnection(conn);
        }

        req.setAttribute("pgtypeid",pgtypeid);
        req.setAttribute("isreadonly",isReadOnly);
        req.setAttribute("gatewayaccountdetails",hash);
        req.setAttribute("accountid",accountid);
        req.setAttribute("gateway_table_name",gateway_table_name);
        req.setAttribute("gateway",gateway);
        req.setAttribute("currency", currency);
        req.setAttribute("table_data",dataStringBuilder.toString());
        req.setAttribute("table_column",tableColumn);
        req.setAttribute("action",action);
       req.setAttribute("actionExecutorId",actionExecutorId);
       req.setAttribute("actionExecutorName",actionExecutorName);
        RequestDispatcher rd = req.getRequestDispatcher("/viewGatewayAccountDetails.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}