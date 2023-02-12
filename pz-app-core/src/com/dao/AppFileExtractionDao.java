package com.dao;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.vo.applicationManagerVOs.AppUploadLabelVO;

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
public class AppFileExtractionDao
{
    private static Logger logger=new Logger(AppFileExtractionDao.class.getName());
    private static Functions functions = new Functions();

    //getting the label for the upload file
    //getting the label for the upload file from partnerid --suraj
    public Map<String,AppUploadLabelVO> getListOfUploadLabel(String functionalUsage,String partnerid)
    {
        Connection con=null;
        ResultSet rsLabel=null;

        Map<String,AppUploadLabelVO> uploadLabelVOMap = new HashMap<String, AppUploadLabelVO>();
        try
        {
            con= Database.getConnection();
            String query = "select * from `uploadfile_label` where partnerid=?";
            PreparedStatement psLabel=con.prepareStatement(query);
            psLabel.setString(1,partnerid);
            rsLabel=psLabel.executeQuery();
            while(rsLabel.next())
            {
                AppUploadLabelVO uploadLabelVO = new AppUploadLabelVO();
                uploadLabelVO.setLabelId(rsLabel.getString("label_id"));
                uploadLabelVO.setLabelName(rsLabel.getString("label_name"));
                uploadLabelVO.setAlternateName(rsLabel.getString("alternate_name"));
                uploadLabelVO.setFunctionalUsage(rsLabel.getString("functional_usage"));
                uploadLabelVO.setSupportedFileType(rsLabel.getString("supportedfile_type"));
                uploadLabelVO.setIsManadatory(rsLabel.getString("ismandatory"));
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
          Database.closeConnection(con);
        }
     return uploadLabelVOMap;
    }

    //getting the label for the upload file
    public Map<String,AppUploadLabelVO> getListOfUploadLabelWithAlternateNameAsKey(String functionalUsage)
    {
        Connection con=null;
        ResultSet rsLabel=null;

        Map<String,AppUploadLabelVO> uploadLabelVOMap = new HashMap<String, AppUploadLabelVO>();
        try
        {
            con= Database.getConnection();
            String query = "select * from `uploadfile_label` where functional_usage=?";
            PreparedStatement psLabel=con.prepareStatement(query);
            psLabel.setString(1,functionalUsage);
            rsLabel=psLabel.executeQuery();
            while(rsLabel.next())
            {
                AppUploadLabelVO uploadLabelVO = new AppUploadLabelVO();
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
          Database.closeConnection(con);
        }
     return uploadLabelVOMap;
    }

}
