package com.directi.pg.fileupload;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Mail;
import com.directi.pg.SystemError;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Created by admin on 3/27/2018.
 */
public class SettlementFileUpload
{
    private static Logger logger = new Logger(SettlementFileUpload.class.getName());
    private boolean started;
    private String path;
    private String retpath;
    private String filename;
    private String contenttype;
    private Dictionary dict;
    private boolean dontwrite = false;
    private String logpath;

    public SettlementFileUpload()
    {
        started = true;
    }

    public String getFilename()
    {
        return filename;
    }

    private void setFilename(String s)
    {
        if (s == null)
            return;
        int pos = s.indexOf("filename=\"");
        if (pos != -1)
        {
            retpath = s.substring(pos + 10, s.length() - 1);
            pos = retpath.lastIndexOf("\\");
            if (pos != -1)
                filename = retpath.substring(pos + 1);
            else
                filename = retpath;
        }

        if (!validateExtension(filename))
        {
            filename = null;
            dontwrite = true;
        }
    }

    public String getFilepath()
    {
        return retpath;
    }

    public String getSavePath()
    {
        return path;
    }

    public void setSavePath(String savePath)
    {
        path = savePath;
    }

    public String getLogpath()
    {
        return logpath;
    }

    public void setLogpath(String logpath)
    {
        this.logpath = logpath;
    }

    public String getContentType()
    {
        return contenttype;
    }

    public String getFieldValue(String fieldName)
    {
        if (dict == null || fieldName == null)
            return null;
        else
            return (String) dict.get(fieldName);
    }

    public void Upload(String path, Part filePart, String fileName) throws SystemError, IOException
    {
        OutputStream out = null;
        InputStream filecontent = null;


        try
        {
            out = new FileOutputStream(new File(path + File.separator + fileName));
            try
            {

                filecontent = filePart.getInputStream();
            }
            catch (MessagingException e)
            {
                logger.error("MessagingException---->",e);  //To change body of catch statement use File | Settings | File Templates.
            }

            int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }

        }
        catch (FileNotFoundException fne)
        {

        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
            if (filecontent != null)
            {
                filecontent.close();
            }

        }
    }

    private boolean validateExtension(String filename)
    {
        if (Functions.parseData(filename) == null)
        {
            return false;
        }

        boolean validated = false;
        String[] extensions = Functions.convertCommaseperatedStringtoStringarr(".xls");
        for (int i = 0; i < extensions.length; i++)
        {
            if (filename.endsWith(extensions[i].toLowerCase()))
            {
                validated = true;
                break;
            }
        }
        return validated;
    }

    private String getFileExtension(String filename)
    {
        String extension = null;
        if (filename != null)
        {
            int ext = filename.trim().indexOf(".");
            extension = filename.substring(ext + 1);
        }
        return extension;
    }

    private void setContenttype(String s)
    {
        if (s == null)
            return;
        int pos = s.indexOf(": ");
        if (pos != -1)
            contenttype = s.substring(pos + 2, s.length());
    }

    public void doUpload(HttpServletRequest request, String username) throws IOException, SystemError
    {
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[128];
        int i = in.readLine(line, 0, 128);
        if (i < 3)
            return;
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();
        for (; i != -1; i = in.readLine(line, 0, 128))
        {
            String newLine = new String(line, 0, i);
            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));
                if (filename == null)
                {
                    throw new SystemError("Please provide valid file");
                }

                File file = new File(path + filename);

                if (file.exists())
                {
                    throw new SystemError("Your file already exists in the System. Please Upload new File.");
                }

                i = in.readLine(line, 0, 128);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, 128);
                i = in.readLine(line, 0, 128);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, 128);
                }

                try
                {
                    if (username != null)
                    {
                        //System.out.println("username not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {
                            String ext = filename.substring(pos);
                            filename = username + ext;
                        }
                        else
                        {
                            dontwrite = true;
                        }
                    }

                    if (!dontwrite)
                    {
                        //	System.out.println("writing file");
                        RandomAccessFile f = new
                                RandomAccessFile(String.valueOf(path != null ? ((Object) (path)) : "") +
                                String.valueOf(filename), "rw");
                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);

                        f.close();
                        logUploadedFile(request);
                    }

                }
                catch (Exception exception)
                {
                    //System.out.println(exception.toString());
                }
                continue;
            }
            int pos = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos + 6, newLine.length() -
                    3);
            i = in.readLine(line, 0, 128);
            i = in.readLine(line, 0, 128);
            newLine = new String(line, 0, i);
            StringBuffer fieldValue = new StringBuffer(128);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new
                    String(line, 0, i))
            {
                i = in.readLine(line, 0, 128);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) &&
                        (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() -
                            2));
                else
                    fieldValue.append(newLine);
            }

            dict.put(fieldName, fieldValue.toString());
        }
    }

    private void logUploadedFile(HttpServletRequest request)
    {
        boolean logfileUpload = true;
        if (logfileUpload)
        {
            try
            {
                String fileUploadLogPath = logpath;
                RandomAccessFile raf = new RandomAccessFile(fileUploadLogPath + System.getProperty("file.separator") + "fileUploadHistory.log", "rw");
                raf.seek(raf.length());  //set pointer to end of file

                //Prepare query for all parameters
                Calendar cal = Calendar.getInstance();

                raf.writeBytes(cal.getTime() + " -> ");
                String ipAddr = request.getRemoteAddr();
                raf.writeBytes(":" + ipAddr + ":");
                raf.writeBytes(":" + request.getRequestURL() + ":");
                String filePath = String.valueOf(path != null ? ((Object) (path)) : "") + System.getProperty(
                        "file.separator") + String.valueOf(filename);
                raf.writeBytes(filePath);

                raf.writeBytes("\n");
                raf.close();
            }
            catch (Exception e)
            {
                Hashtable errorHash = new Hashtable();
                errorHash.put("error", Functions.getStackTrace(e));
                try
                {
                    Mail.sendAdminMail("Error in logging uploaded file", "Error in logging uploaded file");
                }
                catch (Exception e1)
                {

                }

            }
        }

    }

    public boolean isFileExists(String filePath, String fileName)
    {
        boolean result = false;
        File file = new File(filePath + fileName);
        if (file.exists())
        {
            result = true;
        }
        return result;
    }

    public HashMap getInputFields(HttpServletRequest request, String username) throws IOException
    {
        HashMap<String, String> hashMap = new HashMap();
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[128];
        int i = in.readLine(line, 0, 128);
        if (i < 3)
            return hashMap;
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();
        for (; i != -1; i = in.readLine(line, 0, 128))
        {
            String newLine = new String(line, 0, i);
            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));
                if (filename == null)
                {
                    //throw new SystemError("Please provide valid file");
                }

                File file = new File(path + filename);

                if (file.exists())
                {
                    //throw new SystemError("Your file already exists in the System. Please Upload new File.");
                }

                i = in.readLine(line, 0, 128);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, 128);
                i = in.readLine(line, 0, 128);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, 128);
                }

                try
                {
                    if (username != null)
                    {
                        //System.out.println("username not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {
                            String ext = filename.substring(pos);
                            filename = username + ext;
                        }
                        else
                        {
                            dontwrite = true;
                        }
                    }

                    if (!dontwrite)
                    {
                        //	System.out.println("writing file");
                        RandomAccessFile f = new
                                RandomAccessFile(String.valueOf(path != null ? ((Object) (path)) : "") +
                                String.valueOf(filename), "rw");
                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);

                        f.close();
                        logUploadedFile(request);
                    }

                }
                catch (Exception exception)
                {
                    //System.out.println(exception.toString());
                }
                continue;
            }
            int pos = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos + 6, newLine.length() -
                    3);
            i = in.readLine(line, 0, 128);
            i = in.readLine(line, 0, 128);
            newLine = new String(line, 0, i);
            StringBuffer fieldValue = new StringBuffer(128);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new
                    String(line, 0, i))
            {
                i = in.readLine(line, 0, 128);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) &&
                        (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() -
                            2));
                else
                    fieldValue.append(newLine);
            }
            hashMap.put(fieldName, fieldValue.toString());
        }
        return hashMap;
    }
}
