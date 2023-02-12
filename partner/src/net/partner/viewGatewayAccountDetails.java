package net.partner;

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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

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
            log.debug("Entering in viewGatewayAccountDetails");
            PrintWriter out = res.getWriter();
            HttpSession session = req.getSession();
            PartnerFunctions partner=new PartnerFunctions();
            if (!partner.isLoggedInPartner(session))
            {
                res.sendRedirect("/partner/Logout.jsp");
                return;
            }
            User user =  (User)session.getAttribute("ESAPIUserSessionKey");


            String accountid="";
            String action = "";
            String isReadOnly = "view";
            String tableCoulumnName  = null;

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
            String hashTable = null;
            String tableColumn = "";
            String table_data = "";
            String gateway_table_name = "";
            Hashtable tableDataHashTable = new Hashtable();
            Functions functions=new Functions();
            PreparedStatement ps = null;
            PreparedStatement preparedStatement = null;
            PreparedStatement preparedStatementData = null;
            ResultSet tableMetaDataRs = null;
            ResultSet rs = null;
            ResultSet resultset = null;
            try
            {
                conn = Database.getConnection();
                String query = "SELECT * FROM gateway_accounts WHERE accountid=?";
                ps = conn.prepareStatement(query);
                ps.setString(1,accountid);
                rs = ps.executeQuery();
                hash = Database.getHashFromResultSet(rs);
                log.debug("===rec===" + hash);
                if(action.equalsIgnoreCase("modify")) {
                    isReadOnly="modify";
                    modify = "";
                }

                // getting table data

                Hashtable innerHash = (Hashtable)hash.get(1 + "");
                Integer pgtypeid = Integer.parseInt((String)innerHash.get("pgtypeid"));
                gateway_table_name = "";

                if(pgtypeid != null)
                {
                    String tableNameQuery = "select gateway_table_name from gateway_type where pgtypeid = ?";
                    preparedStatement = conn.prepareStatement(tableNameQuery);
                    preparedStatement.setInt(1,pgtypeid);
                    resultset = preparedStatement.executeQuery();
                    while(resultset.next())
                    {
                        gateway_table_name = resultset.getString("gateway_table_name");
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
                                    String temp = tableMetaDataRs.getString(coulumnName);

                                        dataStringBuilder.append("<tr>");
                                        dataStringBuilder.append("<td class=tr1>" + coulumnName + "</td>");
                                        dataStringBuilder.append("<td class=tr1><input class=txtbox1  type='text' name='" + coulumnName + "' value='" + temp + "'" + modify + "></td>");
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
                                        String temp = tableMetaDataRs.getString(coulumnName);

                                        dataStringBuilder.append("<tr>");
                                        dataStringBuilder.append("<td class=tr1>" + coulumnName + "</td>");
                                        dataStringBuilder.append("<td class=tr1><input class=txtbox1  type='text' name='" + coulumnName + "' value='" + temp + "'" + modify + "></td>");
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
                Database.closePreparedStatement(preparedStatementData);
                Database.closePreparedStatement(ps);
                Database.closePreparedStatement(preparedStatement);
                Database.closeConnection(conn);
            }

            req.setAttribute("isreadonly",isReadOnly);
            req.setAttribute("gatewayaccountdetails",hash);
            req.setAttribute("accountid",accountid);
            req.setAttribute("gateway_table_name",gateway_table_name);
            req.setAttribute("table_data",dataStringBuilder.toString());
            req.setAttribute("table_column",tableColumn);
            req.setAttribute("action",action);
            RequestDispatcher rd = req.getRequestDispatcher("/viewGatewayAccountDetails.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
    }
}