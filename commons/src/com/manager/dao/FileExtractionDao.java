package com.manager.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.vo.fileRelatedVOs.UploadLabelVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 3/10/15
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileExtractionDao
{
    private static Logger logger=new Logger(FileExtractionDao.class.getName());
    private static Functions functions = new Functions();

    //getting the label for the upload file
    public Map<String,UploadLabelVO> getListOfUploadLabel(String functionalUsage)
    {
        Connection con=null;
        ResultSet rsLabel=null;
        PreparedStatement psLabel=null;

        Map<String,UploadLabelVO> uploadLabelVOMap = new HashMap<String, UploadLabelVO>();
        try
        {
            con= Database.getRDBConnection();
            String query = "select * from `uploadfile_label` where functional_usage=?";
            psLabel=con.prepareStatement(query);
            psLabel.setString(1,functionalUsage);
            rsLabel=psLabel.executeQuery();
            while(rsLabel.next())
            {
                UploadLabelVO uploadLabelVO = new UploadLabelVO();
                uploadLabelVO.setLabelId(rsLabel.getString("label_id"));
                uploadLabelVO.setLabelName(rsLabel.getString("label_name"));
                uploadLabelVO.setAlternateName(rsLabel.getString("alternate_name"));
                uploadLabelVO.setFunctionalUsage(rsLabel.getString("functional_usage"));
                uploadLabelVO.setSupportedFileType(rsLabel.getString("supportedfile_type"));
                uploadLabelVOMap.put(uploadLabelVO.getLabelId(), uploadLabelVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error while getting the label name from the table",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql exception while getting the label from the table",e);
        }
        finally
        {
            Database.closeResultSet(rsLabel);
            Database.closePreparedStatement(psLabel);
            Database.closeConnection(con);
        }
     return uploadLabelVOMap;
    }

    //getting the label for the upload file
    public Map<String,UploadLabelVO> getListOfUploadLabelWithAlternateNameAsKey(String functionalUsage)
    {
        Connection con=null;
        ResultSet rsLabel=null;
        PreparedStatement psLabel=null;

        Map<String,UploadLabelVO> uploadLabelVOMap = new HashMap<String, UploadLabelVO>();
        try
        {
            con= Database.getConnection();
            String query = "select * from `uploadfile_label` where functional_usage=?";
            psLabel=con.prepareStatement(query);
            psLabel.setString(1,functionalUsage);
            rsLabel=psLabel.executeQuery();
            while(rsLabel.next())
            {
                UploadLabelVO uploadLabelVO = new UploadLabelVO();
                uploadLabelVO.setLabelId(rsLabel.getString("label_id"));
                uploadLabelVO.setLabelName(rsLabel.getString("label_name"));
                uploadLabelVO.setAlternateName(rsLabel.getString("alternate_name"));
                uploadLabelVO.setFunctionalUsage(rsLabel.getString("functional_usage"));
                uploadLabelVO.setSupportedFileType(rsLabel.getString("supportedfile_type"));
                uploadLabelVOMap.put(uploadLabelVO.getAlternateName(), uploadLabelVO);
            }
        }
        catch (SystemError systemError)
        {
            logger.error("System error while getting the label name from the table",systemError);
        }
        catch (SQLException e)
        {
            logger.error("Sql exception while getting the label from the table",e);
        }
        finally
        {
            Database.closeResultSet(rsLabel);
            Database.closePreparedStatement(psLabel);
            Database.closeConnection(con);
        }
     return uploadLabelVOMap;
    }

}
