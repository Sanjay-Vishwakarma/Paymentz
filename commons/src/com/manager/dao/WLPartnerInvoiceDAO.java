package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.WLPartnerCommissionVO;
import com.manager.vo.WLPartnerInvoiceVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 Created by IntelliJ IDEA.
 User: Supriya
 Date: 5/9/2016
 Time: 3:06 PM
 To change this template use File | Settings | File Templates.
 */public class WLPartnerInvoiceDAO
{
    Logger logger = new Logger(WLPartnerInvoiceDAO.class.getName());

    public List<WLPartnerCommissionVO> getWLPartnerInvoiceCommissions(String partnerId, String pgtypeId) throws PZDBViolationException
    {
        List<WLPartnerCommissionVO> wlPartnerCommissionVOs = new LinkedList();
        WLPartnerCommissionVO wlPartnerCommissionVO = null;
        ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "SELECT wlpc.commission_id,wlpc.commission_value,C.valuetype,C.chargename,wlpc.isinput_required,C.keyword,wlpc.sequence_no,C.frequency,C.category,C.subkeyword FROM wl_partner_commission_mapping AS wlpc, charge_master AS C WHERE wlpc.isinput_required='Y' AND wlpc.commission_id=C.chargeid AND wlpc.partner_id=? AND wlpc.pgtype_id=? ORDER BY wlpc.sequence_no";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            pstmt.setString(2, pgtypeId);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                wlPartnerCommissionVO = new WLPartnerCommissionVO();
                wlPartnerCommissionVO.setChargeId(rs.getString("commission_id"));
                wlPartnerCommissionVO.setCommissionValue(rs.getDouble("commission_value"));
                wlPartnerCommissionVO.setIsInputRequired(rs.getString("isinput_required"));
                wlPartnerCommissionVO.setSequenceNo(rs.getString("sequence_no"));

                chargeMasterVO.setValueType(rs.getString("valuetype"));
                chargeMasterVO.setChargeName(rs.getString("chargename"));
                chargeMasterVO.setKeyword(rs.getString("keyword"));
                chargeMasterVO.setFrequency(rs.getString("frequency"));
                chargeMasterVO.setCategory(rs.getString("category"));
                chargeMasterVO.setSubKeyword(rs.getString("subkeyword"));

                wlPartnerCommissionVO.setChargeMasterVO(chargeMasterVO);
                wlPartnerCommissionVOs.add(wlPartnerCommissionVO);
            }
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception while getting WL partner invoice commission list", e);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getWLPartnerInvoiceCommissions", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error while getting WL partner invoice commission list::", systemError);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getWLPartnerInvoiceCommissions()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return wlPartnerCommissionVOs;

    }

    public Hashtable getListOfWLPartnerInvoices(String fdtstamp, String tdtstamp, String partnerId, String isPaid, int start, int end) throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        try
        {
            conn = Database.getRDBConnection();
            Functions functions = new Functions();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT id,partner_id,start_date,end_date,setteled_date,amount,netfinal_amount,unpaid_amount,currency,status,reportfile_path,transactionfile_path,from_unixtime(creation_on) as creationtime from wl_invoice_manager where id>0 ");
            StringBuffer countquery = new StringBuffer("SELECT count(*) FROM wl_invoice_manager where id>0");
            if (functions.isValueNull(fdtstamp))
            {
                query.append(" and creation_on >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
                countquery.append(" and creation_on >= " + ESAPI.encoder().encodeForSQL(me, fdtstamp));
            }
            if (functions.isValueNull(tdtstamp))
            {
                query.append(" and creation_on <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
                countquery.append(" and creation_on <= " + ESAPI.encoder().encodeForSQL(me, tdtstamp));
            }
            if (functions.isValueNull(partnerId))
            {
                query.append(" and  partner_id='" + ESAPI.encoder().encodeForSQL(me, partnerId) + "'");
                countquery.append(" and partner_id='" + ESAPI.encoder().encodeForSQL(me, partnerId) + "'");
            }
            /* if (functions.isValueNull(pgtypeId))
            {
                query.append(" and pgtype_id='" + ESAPI.encoder().encodeForSQL(me, pgtypeId) + "'");
                countquery.append(" and pgtype_id='" + ESAPI.encoder().encodeForSQL(me, pgtypeId) + "'");
            }*/
            if (functions.isValueNull(isPaid))
            {
                if (isPaid.equalsIgnoreCase("Y"))
                {
                    query.append(" and status='paid'");
                    countquery.append(" and status='paid'");
                }
                else
                {
                    query.append(" and status='unpaid'");
                    countquery.append(" and status='unpaid'");
                }
            }
            query.append(" ORDER BY creation_on DESC limit " + start + "," + end);



            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            int totalrecords = 0;
            if (rs.next())
            {
                totalrecords = rs.getInt(1);
            }
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getListOfWLPartnerInvoices", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getListOfWLPartnerInvoices()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public Hashtable getListOfWLPartnerCommissions(String partnerId, String pgtypeId,int start, int end)throws PZDBViolationException
    {
        Connection conn = null;
        Hashtable hash = null;
        try
        {
            conn = Database.getRDBConnection();
            Functions functions = new Functions();
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            StringBuffer query = new StringBuffer("SELECT wlpc.id,wlpc.partner_id, p.partnerName, wlpc.pgtype_id, gt.gateway,gt.currency,wlpc.commission_id,cm.chargename,cm.keyword,wlpc.commission_value, wlpc.start_date,wlpc.end_date, wlpc.sequence_no,wlpc.isActive, wlpc.actionExecutorId,wlpc.actionExecutorName FROM wl_partner_commission_mapping AS wlpc JOIN partners p ON wlpc.partner_id=p.partnerid LEFT JOIN gateway_type gt ON wlpc.pgtype_id=gt.pgtypeid JOIN charge_master cm ON wlpc.commission_id=cm.chargeid where wlpc.commission_id>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from wl_partner_commission_mapping AS wlpc JOIN partners p ON wlpc.partner_id=p.partnerid LEFT JOIN gateway_type gt ON wlpc.pgtype_id=gt.pgtypeid JOIN charge_master cm ON wlpc.commission_id=cm.chargeid where wlpc.commission_id>0  ");
            /*if (functions.isValueNull(partnerId))
            {
                query.append(" and p.partnerid=" + partnerId +"");
                countquery.append(" and p.partnerid=" + partnerId + "");
            }*/
            if (functions.isValueNull(partnerId))
            {
                query.append(" and p.partnerid=" + partnerId +"");
                countquery.append(" and p.partnerid=" + partnerId + "");
            }
            if (functions.isValueNull(pgtypeId))
            {
                query.append(" and gt.pgtypeid=" + pgtypeId + "");
                countquery.append(" and gt.pgtypeid=" + pgtypeId + "");
            }
            query.append(" order by wlpc.id DESC LIMIT " + start + "," + end);
            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            ResultSet rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception", e);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getListOfWLPartnerCommissions", null, "Common", "Sql exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            logger.error("System Error", systemError);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getListOfWLPartnerCommissions()", null, "Common", "Sql exception", PZDBExceptionEnum.INCORRECT_QUERY, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return hash;
    }

    public double getWLPartnerUnpaidAmount(String partnerId)
    {
        double unpaidAmount = 0.00;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            String query = "select unpaid_amount as 'UnpaidBalanceAmount' from wl_invoice_manager where partner_id=? ORDER BY id DESC LIMIT 1";
            pstmt = conn.prepareStatement(query);
            pstmt.setString(1, partnerId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                unpaidAmount = rs.getDouble("UnpaidBalanceAmount");
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System Error::", systemError);
        }
        catch (SQLException e)
        {
            logger.error("SQL Exception ::", e);
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(conn);
        }
        return unpaidAmount;
    }

    public String getPartnerFirstTransactionDate(String partnerName) throws PZDBViolationException
    {
        String query = null;

        long trackingId_common = 0;
        long trackingId_qwipi = 0;
        long trackingId_ecore = 0;

        String firstTransactionDate_common = null;
        String firstTransactionDate_qwipi = null;
        String firstTransactionDate_ecore = null;

        String partnerFirstTransactionDate = null;
        ResultSet rsPayout = null;
        Connection conn = null;
        try
        {
            conn = Database.getRDBConnection();
            query = "SELECT MIN(trackingid) AS 'trackingid_common',FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM  transaction_common WHERE totype=?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, partnerName);
            rsPayout = preparedStatement.executeQuery();
            if (rsPayout.next())
            {
                trackingId_common = rsPayout.getLong("trackingid_common");
                logger.error("trackingId_common:::" + trackingId_common);
                firstTransactionDate_common = rsPayout.getString("firsttransdate");
                logger.error("firstTransactionDate_common::::" + firstTransactionDate_common);
            }

            query = "SELECT MIN(trackingid) AS 'trackingid_qwipi',FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM  transaction_qwipi WHERE totype=?";
            PreparedStatement preparedStatement1 = conn.prepareStatement(query);
            preparedStatement1.setString(1, partnerName);
            ResultSet rsPayout1 = preparedStatement1.executeQuery();
            if (rsPayout1.next())
            {
                trackingId_qwipi = rsPayout1.getLong("trackingid_qwipi");
                logger.error("trackingId_qwipi::::" + trackingId_qwipi);
                firstTransactionDate_qwipi = rsPayout.getString("firsttransdate");
                logger.error("firstTransactionDate_qwipi::::" + firstTransactionDate_qwipi);
            }

            query = "SELECT MIN(trackingid) AS 'trackingid_ecore',FROM_UNIXTIME(dtstamp) AS 'firsttransdate' FROM  transaction_ecore WHERE totype=?";
            PreparedStatement preparedStatement2 = conn.prepareStatement(query);
            preparedStatement2.setString(1, partnerName);
            ResultSet rsPayout2 = preparedStatement2.executeQuery();
            if (rsPayout2.next())
            {
                trackingId_ecore = rsPayout2.getLong("trackingid_ecore");
                logger.error("trackingId_ecore::::" + trackingId_ecore);
                firstTransactionDate_ecore = rsPayout.getString("firsttransdate");
                logger.error("firstTransactionDate_ecore::::" + firstTransactionDate_ecore);
            }

            if (((trackingId_ecore > trackingId_common) && (trackingId_qwipi > trackingId_common)) || (trackingId_ecore == 0 || trackingId_qwipi == 0))
            {
                partnerFirstTransactionDate = firstTransactionDate_common;
                logger.error("partnerFirstTransactionDate_common::::" + partnerFirstTransactionDate);
            }
            else if (((trackingId_ecore > trackingId_qwipi) && (trackingId_common > trackingId_qwipi)) || (trackingId_ecore == 0 || trackingId_common == 0))
            {
                partnerFirstTransactionDate = firstTransactionDate_qwipi;
                logger.error("partnerFirstTransactionDate_qwipi::::" + partnerFirstTransactionDate);
            }
            else if (((trackingId_common > trackingId_ecore) && (trackingId_qwipi > trackingId_ecore)) || (trackingId_common == 0 || trackingId_qwipi == 0))
            {
                partnerFirstTransactionDate = firstTransactionDate_ecore;
                logger.error("partnerFirstTransactionDate_ecore::::" + partnerFirstTransactionDate);
            }
            //return partnerFirstTransactionDate;
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError while collecting gateway partner first transaction date on gateway type ", systemError);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getPartnersFirstTransactionDate()", null, "Common", "SystemError while getting first transaction date on main transaction tables", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            logger.error("SQLException while collecting gateway partner first transaction date on gateway type", e);
            PZExceptionHandler.raiseDBViolationException(WLPartnerInvoiceDAO.class.getName(), "getPartnersFirstTransactionDate()", null, "Common", "SqlException due to incorrect query on main transaction tables", PZDBExceptionEnum.INCORRECT_QUERY, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return partnerFirstTransactionDate;
    }

    public WLPartnerInvoiceVO getWLInvoiceReport(String invoiceId) throws SystemError, SQLException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WLPartnerInvoiceVO wlPartnerInvoiceVO = null;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer sbQuery = new StringBuffer("select * from wl_invoice_manager where id=?");
            pstmt = con.prepareStatement(sbQuery.toString());
            pstmt.setString(1, invoiceId);
            rs = pstmt.executeQuery();
            if (rs.next())
            {
                wlPartnerInvoiceVO = new WLPartnerInvoiceVO();
                wlPartnerInvoiceVO.setInvoiceId(rs.getString("id"));
                wlPartnerInvoiceVO.setPartnerId(rs.getString("partner_id"));
                wlPartnerInvoiceVO.setCurrency(rs.getString("currency"));
                wlPartnerInvoiceVO.setStartDate(rs.getString("start_date"));
                wlPartnerInvoiceVO.setEndDate(rs.getString("end_date"));
                wlPartnerInvoiceVO.setSettledDate(rs.getString("setteled_date"));
                wlPartnerInvoiceVO.setAmount(rs.getDouble("amount"));
                wlPartnerInvoiceVO.setNetFinalAmount(rs.getDouble("netfinal_amount"));
                wlPartnerInvoiceVO.setUnpaidAmount(rs.getDouble("unpaid_amount"));
                wlPartnerInvoiceVO.setStatus(rs.getString("status"));
                wlPartnerInvoiceVO.setReportFilePath(rs.getString("reportfile_path"));
                wlPartnerInvoiceVO.setTransactionFilePath(rs.getString("transactionfile_path"));
            }
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return wlPartnerInvoiceVO;
    }

    public boolean updateWLPartnerInvoice(WLPartnerInvoiceVO wlPartnerInvoiceVO) throws SQLException, SystemError
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        int k = 0;
        boolean b = false;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("update wl_invoice_manager set setteled_date=?,amount=?,netfinal_amount=?,unpaid_amount=?,status=? where id=?");
            pstmt = con.prepareStatement(query.toString());
            pstmt.setString(1, wlPartnerInvoiceVO.getSettledDate());
            pstmt.setDouble(2, wlPartnerInvoiceVO.getAmount());
            pstmt.setDouble(3, wlPartnerInvoiceVO.getNetFinalAmount());
            pstmt.setDouble(4, wlPartnerInvoiceVO.getUnpaidAmount());
            pstmt.setString(5, wlPartnerInvoiceVO.getStatus());
            pstmt.setString(6, wlPartnerInvoiceVO.getInvoiceId());
            k = pstmt.executeUpdate();
            if (k > 0)
            {
                b = true;
            }
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return b;
    }

  public List<WLPartnerInvoiceVO> getListOfAllWLPartnerGateways()throws  PZDBViolationException
  {
      Connection conn=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      WLPartnerInvoiceVO wlPartnerInvoiceVO=null;
      List<WLPartnerInvoiceVO> wlPartnerInvoiceVOList=new LinkedList();
      try
      {
          conn=Database.getRDBConnection();
          String query = "SELECT DISTINCT gt.pgtypeid,gt.gateway,gt.currency FROM gateway_type AS gt JOIN gatewaytype_partner_mapping AS gpm ON gt.pgtypeid=gpm.pgtypeid  ORDER  BY  gt.pgtypeid ASC ";
          pstmt = conn.prepareStatement(query);
          rs = pstmt.executeQuery();
          while (rs.next())
          {
              wlPartnerInvoiceVO=new WLPartnerInvoiceVO();
              wlPartnerInvoiceVO.setPgtypeId(rs.getString("pgtypeid"));
              wlPartnerInvoiceVO.setGateway(rs.getString("gateway"));
              wlPartnerInvoiceVO.setCurrency(rs.getString("currency"));
              wlPartnerInvoiceVOList.add(wlPartnerInvoiceVO);
          }
      }
      catch (SQLException e)
      {
          logger.error("Leaving WLPartnerInvoiceDAO throwing SQL Exception as System Error :::: ",e);
          PZExceptionHandler.raiseDBViolationException("WLPartnerInvoiceDAO","getAllWhitelabelPartners()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
      }
      catch (SystemError se)
      {
          logger.error("Leaving WLPartnerInvoiceDAO throwing SQL Exception as System Error :::: ",se);
          PZExceptionHandler.raiseDBViolationException("WLPartnerInvoiceDAO","getAllWhitelabelPartners()",null,"Common","SQL Exception Thrown:::", PZDBExceptionEnum.SQL_EXCEPTION,null,se.getMessage(),se.getCause());
      }
      finally
      {
          Database.closeResultSet(rs);
          Database.closePreparedStatement(pstmt);
          Database.closeConnection(conn);
      }
      return  wlPartnerInvoiceVOList;
  }

    public static boolean deleteWLInvoiceReport(String invoiceId) throws SystemError, SQLException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        boolean isDeleted = false;
        try
        {
            con = Database.getRDBConnection();
            StringBuffer dQuery = new StringBuffer("delete from wl_invoice_manager where id =?");
            pstmt = con.prepareStatement(dQuery.toString());
            pstmt.setString(1, invoiceId);
            int d = pstmt.executeUpdate();
            if (d==1)
                isDeleted = true;
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return isDeleted;
    }

}
