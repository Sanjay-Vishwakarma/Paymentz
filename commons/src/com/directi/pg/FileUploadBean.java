package com.directi.pg;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ResourceBundle;


public class FileUploadBean
{
    static Logger Log = new Logger(FileUploadBean.class.getName());

    public FileUploadBean()
    {

    }

    public String getFilename()
    {
        return filename;
    }

    public String getFilepath()
    {
        return retpath;
    }

    public void setNewFilename(String filename)
    {
        newFilename = filename;
    }

    public String getNewFilename()
    {
        return newFilename;
    }

    public void setSavePath(String savePath)
    {
        path = savePath;
    }

    public String getSavePath()
    {
        return path;
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

    public String getPath1()
    {
        return path1;
    }
    public void setPath1(String path1)
    {
        this.path1 = path1;
    }
    public String getPath2()
    {
        return path2;
    }
    public void setPath2(String path2)
    {
        this.path2 = path2;
    }
    public String getPath3()
    {
        return path3;
    }
    public void setPath3(String path3)
    {
        this.path3 = path3;
    }

    public String getFieldValue(String fieldName)
    {
        if (dict == null || fieldName == null)
            return null;
        else
            return (String) dict.get(fieldName);
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

        filename = filename.toLowerCase();
        if (!validateExtension(filename))
        {
            filename = null;
            dontwrite = true;
        }

    }

    private boolean validateExtension(String filename)
    {
        if (Functions.parseData(filename) == null)
        {
            return false;
        }

        boolean validated = false;
        String[] extensions = Functions.convertCommaseperatedStringtoStringarr(".bmp,.jpg,.jpeg,.gif,.ico,.png");
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

    private void setContenttype(String s)
    {
        if (s == null)
            return;
        int pos = s.indexOf(": ");
        if (pos != -1)
            contenttype = s.substring(pos + 2, s.length());
    }

    public void uploadPartnerLogo(HttpServletRequest request) throws SystemError, IOException
    {
        int byteLength = 256;
        Log.debug("Entering doUpload method ");
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[ byteLength ];
        int i = in.readLine(line, 0, byteLength);
        if (i < 3)
            throw new SystemError("File selected does not exist, please select filename first.");
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();

        for (; i != -1; i = in.readLine(line, 0, byteLength))
        {
            String newLine = new String(line, 0, i);
            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            int pos1 = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos1 + 6, pos1 + 12);
            Log.debug("field name=" + fieldName);
            dict.put("name", fieldName);

            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));

                if (filename == null || filename.equals("") || filename.matches(".*\\s.*"))
                    throw new SystemError("Please provide valid file");

                File partnerPath=new File(path+filename);
                File merchantPath=new File(path1+filename);
                File agentPath=new File(path2+filename);
                File orderPath=new File(path3+filename);

               /* if(merchantPath.exists() || partnerPath.exists() || agentPath.exists() || orderPath.exists())
                {
                    throw new SystemError("This file already exists in the System. Please Upload new File..");
                }*/


                i = in.readLine(line, 0, byteLength);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, byteLength);
                i = in.readLine(line, 0, byteLength);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, byteLength);
                }

                try
                {
                    //Check length of file before writting
                    final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
                    int MAXFILELENGTH = Integer.parseInt(RB.getString("MAXFILELENGTH"));
                    if (buffer.size() > MAXFILELENGTH)
                    {
                        Log.debug("file size = " + buffer.size());
                        throw new SystemError("File size is too big. File size should not exceed 500KB, please select another file.");
                    }

                    Log.debug("Checking for newFilename=" + newFilename);
                    if (newFilename != null)
                    {
                        //System.out.println("newFilename not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {   Log.debug("filename setting done  filename=" + filename);
                            filename = newFilename + filename;

                        }
                        else
                        {
                            Log.debug("filename passed not in proper format=" + newFilename);
                            dontwrite = true;
                            throw new SystemError("File selected is not in proper format, please select proper file.");
                        }
                    }

                    if (!dontwrite)
                    {
                        Log.debug("writing to file with path=" + path);
                        Log.debug("writing to file with path=" + path1);
                        Log.debug("writing to file with path=" + path2);
                        Log.debug("writing to file with path=" + path3);

                        partnerPath = new File(path);
                        merchantPath = new File(path1);
                        agentPath = new File(path2);
                        orderPath = new File(path3);

                        merchantPath.mkdirs();
                        partnerPath.mkdirs();
                        agentPath.mkdirs();
                        orderPath.mkdirs();

                        String filePath = String.valueOf(merchantPath != null ? ((Object) (merchantPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath1 = String.valueOf(partnerPath != null ? ((Object) (partnerPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath2 = String.valueOf(agentPath != null ? ((Object) (agentPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath3 = String.valueOf(orderPath != null ? ((Object) (orderPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);

                        File checkfile = new File(filePath);
                        File checkfile1 = new File(filePath1);
                        File checkfile2 = new File(filePath2);
                        File checkfile3 = new File(filePath3);

                        /*if (checkfile.exists() || checkfile1.exists() || checkfile2.exists() || checkfile3.exists())
                        {
                            //if yes then throw exception
                            throw new SystemError("File with name " + filename + " already exist, please select another name.");

                        }*/

                        RandomAccessFile f = new RandomAccessFile(filePath, "rw");
                        RandomAccessFile f1 = new RandomAccessFile(filePath1, "rw");
                        RandomAccessFile f2 = new RandomAccessFile(filePath2, "rw");
                        RandomAccessFile f3 = new RandomAccessFile(filePath3, "rw");

                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);
                        f.close();

                        f1.write(bytes, 0, bytes.length - 2);
                        f1.close();

                        f2.write(bytes, 0, bytes.length - 2);
                        f2.close();

                        f3.write(bytes, 0, bytes.length - 2);
                        f3.close();

                        logUploadedFile(request);
                        Log.debug("file written successfully");

                    }
                }
                catch (Exception exception)
                {
                    Log.error("Exception thrown in doUpload=",exception);
                    throw new SystemError(exception.toString());
                    //System.out.println(exception.toString());
                }
                continue;
            }//if newline

            i = in.readLine(line, 0, byteLength);
            newLine = new String(line, 0, i);
            StringBuffer fieldValue = new StringBuffer(byteLength);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
            {
                i = in.readLine(line, 0, byteLength);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) && (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() - 2));
                else
                    fieldValue.append(newLine);
            }
            dict.put(fieldName, fieldValue.toString());
        }//for
        Log.debug("Leaving doUpload Method with dict=" + dict);

    }//end Upload method

    public void uploadPartnerIcon(HttpServletRequest request) throws SystemError, IOException
    {
        int byteLength = 256;
        Log.debug("Entering doUpload method ");
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[ byteLength ];
        int i = in.readLine(line, 0, byteLength);
        if (i < 3)
            throw new SystemError("File selected does not exist, please select filename first.");
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();

        for (; i != -1; i = in.readLine(line, 0, byteLength))
        {
            String newLine = new String(line, 0, i);
            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            int pos1 = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos1 + 6, pos1 + 12);
            Log.debug("field name=" + fieldName);
            dict.put("name", fieldName);

            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));

                if (filename == null || filename.equals("") || filename.matches(".*\\s.*"))
                    throw new SystemError("Please provide valid file");

                File partnerPath=new File(path+filename);
                File merchantPath=new File(path1+filename);
                File agentPath=new File(path2+filename);
                File orderPath=new File(path3+filename);

                /*if(merchantPath.exists() || partnerPath.exists() || agentPath.exists() || orderPath.exists())
                {
                    throw new SystemError("This file already exists in the System. Please Upload new File..");
                }*/


                i = in.readLine(line, 0, byteLength);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, byteLength);
                i = in.readLine(line, 0, byteLength);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, byteLength);
                }

                try
                {
                    //Check length of file before writting
                    final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
                    int MAXFILELENGTH = Integer.parseInt(RB.getString("MAXFILELENGTH"));
                    if (buffer.size() > MAXFILELENGTH)
                    {
                        Log.debug("file size = " + buffer.size());
                        throw new SystemError("File size is too big. File size should not exceed 500KB, please select another file.");
                    }

                    Log.debug("Checking for newFilename=" + newFilename);
                    if (newFilename != null)
                    {
                        //System.out.println("newFilename not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {   Log.debug("filename setting done  filename=" + filename);
                            filename = newFilename + filename;

                        }
                        else
                        {
                            Log.debug("filename passed not in proper format=" + newFilename);
                            dontwrite = true;
                            throw new SystemError("File selected is not in proper format, please select proper file.");
                        }
                    }

                    if (!dontwrite)
                    {
                        Log.debug("writing to file with path=" + path);
                        Log.debug("writing to file with path=" + path1);
                        Log.debug("writing to file with path=" + path2);
                        Log.debug("writing to file with path=" + path3);

                        partnerPath = new File(path);
                        merchantPath = new File(path1);
                        agentPath = new File(path2);
                        orderPath = new File(path3);

                        merchantPath.mkdirs();
                        partnerPath.mkdirs();
                        agentPath.mkdirs();
                        orderPath.mkdirs();

                        String filePath = String.valueOf(merchantPath != null ? ((Object) (merchantPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath1 = String.valueOf(partnerPath != null ? ((Object) (partnerPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath2 = String.valueOf(agentPath != null ? ((Object) (agentPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath3 = String.valueOf(orderPath != null ? ((Object) (orderPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);

                        File checkfile = new File(filePath);
                        File checkfile1 = new File(filePath1);
                        File checkfile2 = new File(filePath2);
                        File checkfile3 = new File(filePath3);

                        /*if (checkfile.exists() || checkfile1.exists() || checkfile2.exists() || checkfile3.exists())
                        {
                            //if yes then throw exception
                            throw new SystemError("File with name " + filename + " already exist, please select another name.");

                        }*/

                        RandomAccessFile f = new RandomAccessFile(filePath, "rw");
                        RandomAccessFile f1 = new RandomAccessFile(filePath1, "rw");
                        RandomAccessFile f2 = new RandomAccessFile(filePath2, "rw");
                        RandomAccessFile f3 = new RandomAccessFile(filePath3, "rw");

                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);
                        f.close();

                        f1.write(bytes, 0, bytes.length - 2);
                        f1.close();

                        f2.write(bytes, 0, bytes.length - 2);
                        f2.close();

                        f3.write(bytes, 0, bytes.length - 2);
                        f3.close();

                        logUploadedFile(request);
                        Log.debug("file written successfully");

                    }
                }
                catch (Exception exception)
                {
                    Log.error("Exception thrown in doUpload=",exception);
                    throw new SystemError(exception.toString());
                    //System.out.println(exception.toString());
                }
                continue;
            }//if newline

            i = in.readLine(line, 0, byteLength);
            newLine = new String(line, 0, i);
            StringBuffer fieldValue = new StringBuffer(byteLength);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
            {
                i = in.readLine(line, 0, byteLength);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) && (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() - 2));
                else
                    fieldValue.append(newLine);
            }
            dict.put(fieldName, fieldValue.toString());
        }//for
        Log.debug("Leaving doUpload Method with dict=" + dict);

    }//end Upload method

    public void uploadPartnerFavicon(HttpServletRequest request) throws SystemError, IOException
    {
        int byteLength = 256;
        Log.debug("Entering doUpload method ");
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[ byteLength ];
        int i = in.readLine(line, 0, byteLength);
        if (i < 3)
            throw new SystemError("File selected does not exist, please select filename first.");
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();

        for (; i != -1; i = in.readLine(line, 0, byteLength))
        {
            String newLine = new String(line, 0, i);
            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            int pos1 = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos1 + 6, pos1 + 12);
            Log.debug("field name=" + fieldName);
            dict.put("name", fieldName);

            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));

                if (filename == null || filename.equals("") || filename.matches(".*\\s.*"))
                    throw new SystemError("Please provide valid file");

                File partnerPath=new File(path+filename);
                File merchantPath=new File(path1+filename);
                File agentPath=new File(path2+filename);
                File orderPath=new File(path3+filename);

                /*if(merchantPath.exists() || partnerPath.exists() || agentPath.exists() || orderPath.exists())
                {
                    throw new SystemError("This file already exists in the System. Please Upload new File..");
                }*/


                i = in.readLine(line, 0, byteLength);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, byteLength);
                i = in.readLine(line, 0, byteLength);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, byteLength);
                }

                try
                {
                    //Check length of file before writting
                    final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");
                    int MAXFILELENGTH = Integer.parseInt(RB.getString("MAXFILELENGTH"));
                    if (buffer.size() > MAXFILELENGTH)
                    {
                        Log.debug("file size = " + buffer.size());
                        throw new SystemError("File size is too big. File size should not exceed 500KB, please select another file.");
                    }

                    Log.debug("Checking for newFilename=" + newFilename);
                    if (newFilename != null)
                    {
                        //System.out.println("newFilename not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {   Log.debug("filename setting done  filename=" + filename);
                            filename = newFilename + filename;

                        }
                        else
                        {
                            Log.debug("filename passed not in proper format=" + newFilename);
                            dontwrite = true;
                            throw new SystemError("File selected is not in proper format, please select proper file.");
                        }
                    }

                    if (!dontwrite)
                    {
                        Log.debug("writing to file with path=" + path);
                        Log.debug("writing to file with path=" + path1);
                        Log.debug("writing to file with path=" + path2);
                        Log.debug("writing to file with path=" + path3);

                        partnerPath = new File(path);
                        merchantPath = new File(path1);
                        agentPath = new File(path2);
                        orderPath = new File(path3);

                        merchantPath.mkdirs();
                        partnerPath.mkdirs();
                        agentPath.mkdirs();
                        orderPath.mkdirs();

                        String filePath = String.valueOf(merchantPath != null ? ((Object) (merchantPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath1 = String.valueOf(partnerPath != null ? ((Object) (partnerPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath2 = String.valueOf(agentPath != null ? ((Object) (agentPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        String filePath3 = String.valueOf(orderPath != null ? ((Object) (orderPath)) : "") + System.getProperty("file.separator") + String.valueOf(filename);

                        File checkfile = new File(filePath);
                        File checkfile1 = new File(filePath1);
                        File checkfile2 = new File(filePath2);
                        File checkfile3 = new File(filePath3);

                        /*if (checkfile.exists() || checkfile1.exists() || checkfile2.exists() || checkfile3.exists())
                        {
                            //if yes then throw exception
                            throw new SystemError("File with name " + filename + " already exist, please select another name.");

                        }*/

                        RandomAccessFile f = new RandomAccessFile(filePath, "rw");
                        RandomAccessFile f1 = new RandomAccessFile(filePath1, "rw");
                        RandomAccessFile f2 = new RandomAccessFile(filePath2, "rw");
                        RandomAccessFile f3 = new RandomAccessFile(filePath3, "rw");

                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);
                        f.close();

                        f1.write(bytes, 0, bytes.length - 2);
                        f1.close();

                        f2.write(bytes, 0, bytes.length - 2);
                        f2.close();

                        f3.write(bytes, 0, bytes.length - 2);
                        f3.close();

                        logUploadedFile(request);
                        Log.debug("file written successfully");

                    }
                }
                catch (Exception exception)
                {
                    Log.error("Exception thrown in doUpload=",exception);
                    throw new SystemError(exception.toString());
                    //System.out.println(exception.toString());
                }
                continue;
            }//if newline

            i = in.readLine(line, 0, byteLength);
            newLine = new String(line, 0, i);
            StringBuffer fieldValue = new StringBuffer(byteLength);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
            {
                i = in.readLine(line, 0, byteLength);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) && (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() - 2));
                else
                    fieldValue.append(newLine);
            }
            dict.put(fieldName, fieldValue.toString());
        }//for
        Log.debug("Leaving doUpload Method with dict=" + dict);

    }

    //This function will be called under merchant context when merchant will upload image
    //here we are also checking file size..
    public void doUpload(HttpServletRequest request) throws SystemError, IOException
    {
        int byteLength = 256;
        Log.debug("Entering doUpload method ");
        ServletInputStream in = request.getInputStream();
        byte line[] = new byte[ byteLength ];
        int i = in.readLine(line, 0, byteLength);
        if (i < 3)
            throw new SystemError("File selected does not exist, please select filename first.");
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        dict = new Hashtable();

        for (; i != -1; i = in.readLine(line, 0, byteLength))
        {
            String newLine = new String(line, 0, i);

            if (!newLine.startsWith("Content-Disposition: form-data; name=\""))
                continue;
            int pos1 = newLine.indexOf("name=\"");
            String fieldName = newLine.substring(pos1 + 6, pos1 + 12);
            Log.debug("field name=" + fieldName);
            dict.put("name", fieldName);

            if (newLine.indexOf("filename=\"") != -1)
            {
                setFilename(new String(line, 0, i - 2));

                if (filename == null || filename.equals(""))
                    throw new SystemError("Please provide valid file");

                File file=new File(path+filename);

                if(file.exists())
                {

                    throw new SystemError("This file already exists in the System. Please Upload new File..");
                }


                i = in.readLine(line, 0, byteLength);
                setContenttype(new String(line, 0, i - 2));
                i = in.readLine(line, 0, byteLength);
                i = in.readLine(line, 0, byteLength);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                for (newLine = new String(line, 0, i); i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
                {
                    buffer.write(line, 0, i);
                    i = in.readLine(line, 0, byteLength);
                }

                try
                {
                    //Check length of file before writting
                    final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.template");

                    int MAXFILELENGTH = Integer.parseInt(RB.getString("MAXFILELENGTH"));


                    if (buffer.size() > MAXFILELENGTH)
                    {
                        Log.debug("file size = " + buffer.size());
                        throw new SystemError("File size is too big. File size should not exceed 500KB, please select another file.");
                    }

                    Log.debug("Checking for newFilename=" + newFilename);
                    if (newFilename != null)
                    {
                        //System.out.println("newFilename not null");
                        int pos = filename.lastIndexOf(".");
                        if (pos != -1)
                        {   Log.debug("filename setting done  filename=" + filename);
                            filename = newFilename + filename;

                        }
                        else
                        {
                            Log.debug("filename passed not in proper format=" + newFilename);
                            dontwrite = true;
                            throw new SystemError("File selected is not in proper format, please select proper file.");
                        }
                    }

                    if (!dontwrite)
                    {
                        Log.debug("writing to file with path=" + path);
                        file = new File(path);
                        file.mkdirs();

                        String filePath = String.valueOf(path != null ? ((Object) (path)) : "") + System.getProperty("file.separator") + String.valueOf(filename);
                        File checkfile = new File(filePath);

                        if (checkfile.exists())
                        {
                            //if yes then throw exception
                            throw new SystemError("File with name " + filename + " already exist, please select another name.");

                        }

                        RandomAccessFile f = new RandomAccessFile(filePath, "rw");
                        byte bytes[] = buffer.toByteArray();


                        f.write(bytes, 0, bytes.length - 2);
                        f.close();
                        logUploadedFile(request);
                        Log.debug("file written successfully");

                    }

                }
                catch (Exception exception)
                {
                    Log.error("Exception thrown in doUpload=",exception);
                    throw new SystemError(exception.toString());
                    //System.out.println(exception.toString());
                }
                continue;
            }//if newline

            //		int pos = newLine.indexOf("name=\"");
            //		String fieldName = newLine.substring(pos + 6, newLine.length() - 3);
            //       Log.info("field name="+fieldName);
            i = in.readLine(line, 0, byteLength);
            i = in.readLine(line, 0, byteLength);
            newLine = new String(line, 0, i);
            Log.debug("new line in dict=" + newLine);
            StringBuffer fieldValue = new StringBuffer(byteLength);
            Log.debug("field value=" + fieldValue);
            for (; i != -1 && !newLine.startsWith(boundary); newLine = new String(line, 0, i))
            {
                i = in.readLine(line, 0, byteLength);
                if ((i == boundaryLength + 2 || i == boundaryLength + 4) && (new String(line, 0, i)).startsWith(boundary))
                    fieldValue.append(newLine.substring(0, newLine.length() - 2));
                else
                    fieldValue.append(newLine);
            }

            dict.put(fieldName, fieldValue.toString());

        }//for


        Log.debug("Leaving doUpload Method with dict=" + dict);

    }//end Upload method

    public boolean doRemove() throws SystemError, Exception
    {
        boolean flag = false;
        Log.debug("inside doRemove()");
        String filename = getNewFilename();
        File file = new File(getSavePath() + System.getProperty("file.separator") + String.valueOf(filename));
        if (file.exists())
        {
            if (file.delete())
                flag = true;
            else
                throw new Exception("Exception while deleting file from location " + getSavePath());
        }
        else
            throw new SystemError("File with name " + filename + " not found.");

        Log.debug("leaving doRemove()");
        return flag;
    }

    private String path;
    private String path1;
    private String path2;
    private String path3;
    private String retpath;
    private String filename;
    private String newFilename;
    private String contenttype;
    private Dictionary dict;
    private boolean dontwrite = false;

    private String logpath;

    private void logUploadedFile(HttpServletRequest request)
    {
        boolean logfileUpload = true;
        if (logfileUpload)
        {
            try
            {
                String fileUploadLogPath = logpath;
                RandomAccessFile raf = new RandomAccessFile(fileUploadLogPath + System.getProperty("file.separator") + "fileUploadLog.txt","rw");
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


}
