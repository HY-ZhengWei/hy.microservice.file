package org.hy.microservice.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.app.Param;
import org.hy.common.file.FileHelp;
import org.hy.common.license.base64.Base64Factory;
import org.hy.common.video.VideoHelp;
import org.hy.common.xml.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;





/**
 * 文件处理的控制层
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-10-16
 * @version     v1.0
 */
@Controller
@RequestMapping("file")
public class FileController
{
    
    private static final Logger                    $Logger       = new Logger(FileController.class);
    
    private static final ExpireMap<String ,String> $NewestVideos = new ExpireMap<String ,String>();
    
    @Autowired
    @Qualifier("FileService")
    private FileService fileService;
    
    @javax.annotation.Resource
    private ResourceLoader resourceLoader;
    
    @Autowired
    @Qualifier("MS_File_WebURL")
    private Param fileServiceURL;
    
    @Autowired
    @Qualifier("MS_File_SaveDir")
    private Param fileServiceSaveDir;
    
    @Autowired
    @Qualifier("MS_File_FFMpegHome")
    private Param ffMpegHome;
    
    
    
    /**
     * 获取视频页面
     * 
     * 测试URL： http://127.0.0.1/hy.microservice.file/file/getVideo/demoVideo/*.page?atob=2
     * 测试URL： https://industry.wzyb.com.cn/msFile/file/getVideo/LiKu/*.page?atob=2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-18
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @param i_Structure   文件结构，simple：未按日期、时间分目录保存的，所有文件都放在同一个根目录中
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value={"/getVideo/**/{fileName:.+}","/{fileName:.+}"})
    public String getVideo(@PathVariable String fileName
                          ,@RequestParam(value="image"        ,required=false) String i_Image
                          ,@RequestParam(value="width"        ,required=false) String i_Width
                          ,@RequestParam(value="height"       ,required=false) String i_Height
                          ,@RequestParam(value="sizeFit"      ,required=false) String i_SizeFit
                          ,@RequestParam(value="auto"         ,required=false) String i_Auto
                          ,@RequestParam(value="loopPlayback" ,required=false) String i_LoopPlayback
                          ,@RequestParam(value="control"      ,required=false) String i_IsControl
                          ,@RequestParam(value="reLoad"       ,required=false) String i_IsReLoad
                          ,@RequestParam(value="atob"         ,required=false) String i_AtoB
                          ,@RequestParam(value="structure"    ,required=false) String i_Structure
                          ,ModelMap io_Model
                          ,HttpServletRequest i_Request
                          ,HttpServletResponse i_Response) throws UnsupportedEncodingException
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");              // 支持跨域请求 i_Request.getHeader("Origin")
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");           // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");
        i_Response.setDateHeader("Expires", 0);
        i_Response.setHeader("Cache-Control", "no-cache, no-store");
        i_Response.setHeader("Pragma", "no-cache");
        
        String v_RequestURI = i_Request.getRequestURI();
        if ( !v_RequestURI.endsWith(".page") )
        {
            return "";
        }
        
        String v_ServerHome = i_Request.getScheme() + "://" + i_Request.getServerName()
                            + (i_Request.getServerPort() == 80 || i_Request.getServerPort() == 443 ? "" : ":" + i_Request.getServerPort())
                            + "/" + v_RequestURI.split("/")[1];
        
        if ( v_RequestURI.contains("getVideo/") )
        {
            fileName = v_RequestURI.split("getVideo/")[1];
        }
        
        String v_Token = StringHelp.getUUID();
        String v_M3U8  = v_ServerHome + "/file/showVideo/" + StringHelp.replaceAll(fileName ,".page" ,".m3u8") + "?token=" + v_Token + "&control=" + Help.NVL(i_IsControl ,"1");
        if ( "2".equals(i_AtoB) )
        {
            // 通过SDK直取摄像设备上的视频数据转成的HLS流
            v_M3U8 += "&live=1";
        }
        else if ( "3".equals(i_AtoB) )
        {
            // FFMpeg直接将RTSP转成的HLS流
            v_M3U8 += "&live=2&structure=" + Help.NVL(i_Structure ,"simple");
        }
        
        io_Model.put("videoWidth"        ,Help.NVL(i_Width   ,"100%"));
        io_Model.put("videoHeight"       ,Help.NVL(i_Height  ,"100%"));
        io_Model.put("videoSizeFit"      ,Help.NVL(i_SizeFit ,"none"));    // fill表示填充满
        io_Model.put("videoAuto"         ,Help.NVL(i_Auto ,"0"));
        io_Model.put("videoLoopPlayback" ,Help.NVL(i_LoopPlayback ,"0"));
        io_Model.put("videoReLoad"       ,Help.NVL(i_IsReLoad ,"0"));
        io_Model.put("videoControl"      ,Help.NVL(i_IsControl ,"1"));
        io_Model.put("videoImage"        ,Help.NVL(i_Image));
        io_Model.put("videoUrl"          ,new String(Base64Factory.getIntance().encode(v_M3U8) ,"UTF-8"));
        
        $Logger.info("打开视频页面：" + v_M3U8);
        
        if ( "2".equals(i_AtoB) || "3".equals(i_AtoB) )
        {
            return "/video/videoLive";
        }
        else if ( "1".equals(i_AtoB) )
        {
            return "/video/videoMore";
        }
        else
        {
            return "/video/videoOne";
        }
    }
    
    
    
    /**
     * 寻找时间线最新的视频文件
     * 
     * @param i_VideoRootDir      寻找视频文件的根目录
     * @param i_OFile
     * @param i_VideoNameSuffix   寻找视频文件的扩展名
     * @param i_VideoNameID       寻找视频文件的标识ID
     * @return
     */
    private synchronized String findNewestVideo(File i_VideoRootDir ,String i_OFile ,String i_VideoNameSuffix ,String i_VideoNameID)
    {
        int     v_WaitCount    = 0;
        boolean v_Continue     = true;
        File    v_LastTimeFile = null;
        String  v_RetFileName  = $NewestVideos.get(i_VideoNameID);
        String  v_LogInfo      = i_VideoNameID + "；" + i_VideoRootDir.toString() + Help.getSysPathSeparator() + i_VideoNameSuffix;
        
        if ( !Help.isNull(v_RetFileName) )
        {
            // 10秒内取高速缓存中的
            return v_RetFileName;
        }
        
        $Logger.info("准备处理视频：" + v_LogInfo);
        
        do
        {
            $Logger.info("寻找视频：" + v_LogInfo);
            v_LastTimeFile = FileHelp.findLastName(i_VideoRootDir ,null ,i_VideoNameSuffix ,true);
            
            if ( v_LastTimeFile != null )
            {
                v_RetFileName = StringHelp.replaceAll(v_LastTimeFile.toString() ,fileServiceSaveDir.getValue() ,"");
            }
            
            if ( v_LastTimeFile == null || (!Help.isNull(i_OFile) && v_RetFileName.indexOf(i_OFile) > 0) )
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception exce)
                {
                    // Nothing.
                }
                
                v_WaitCount++;
                if ( v_WaitCount >= 10 * 20 )
                {
                    $Logger.warn("寻找超时：" + v_LogInfo);
                    return null;
                }
            }
            else
            {
                v_Continue = false;
            }
        }
        while ( v_Continue );
        
        $NewestVideos.put(i_VideoNameID ,v_RetFileName ,10);
        $Logger.info("寻到视频：" + v_RetFileName);
        return v_RetFileName;
    }
    
    
    
    /**
     * 显示视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-12
     * @version     v1.0
     *              v2.0  2021-06-18  升级：改为支持M3U8的视频显示
     *
     * @param fileName
     * @param i_Request
     * @param i_Structure   文件结构，simple：未按日期、时间分目录保存的，所有文件都放在同一个根目录中
     * @return
     */
    @RequestMapping(value={"/showVideo/**/{fileName:.+}","/{fileName:.+}"})
    public void showVideo(@PathVariable String fileName
                         ,@RequestParam(value="token"     ,required=false) String i_Token
                         ,@RequestParam(value="live"      ,required=false) String i_Live
                         ,@RequestParam(value="of"        ,required=false) String i_OFile
                         ,@RequestParam(value="structure" ,required=false) String i_Structure
                         ,HttpServletRequest i_Request
                         ,HttpServletResponse i_Response)
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");              // 支持跨域请求 i_Request.getHeader("Origin")
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");           // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token ,Content-Disposition");
        i_Response.setHeader("Access-Control-Expose-Headers"    ,"Content-Disposition");
        i_Response.setDateHeader("Expires", 0);
        i_Response.setHeader("Cache-Control", "no-cache, no-store");
        i_Response.setHeader("Pragma", "no-cache");
        
        OutputStream v_VideoOut = null;
        
        try
        {
            if ( Help.isNull(fileName) )
            {
                return;
            }
            
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showVideo/") )
            {
                fileName = v_RequestURI.split("showVideo/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // 配对最新的视频（预防重复视频的显示，及阻塞等待的功能）
            String [] v_Finds = fileName.split("\\*");
            if ( v_Finds.length == 2 )
            {
                String v_OFile = (String)i_Request.getSession().getAttribute("OFile" + i_Token);
                if ( !Help.isNull(v_OFile) && Help.isNull(i_OFile) )
                {
                    i_OFile = v_OFile;
                }
                
                Date    v_Now          = Date.getNowTime();
                String  v_Time         = null;
                File    v_VideoRootDir = null;
                
                if ( "simple".equalsIgnoreCase(i_Structure) )
                {
                    v_Time = "";
                }
                else
                {
                    // 跨天跨零点的处理
                    if ( v_Now.getHours() == 0 )
                    {
                        v_Time = "";
                    }
                    // 跨小时的处理，并冗余5分钟
                    else if ( v_Now.getMinutes() <= 5 )
                    {
                        v_Time = Help.getSysPathSeparator() + v_Now.getYMD();
                    }
                    else
                    {
                        v_Time = Help.getSysPathSeparator() + v_Now.getYMD() + Help.getSysPathSeparator() + StringHelp.lpad(v_Now.getHours() ,2 ,"0");
                        
                    }
                }
                v_VideoRootDir = new File(fileServiceSaveDir.getValue() + StringHelp.replaceAll(fileName ,"*" + v_Finds[1] ,"") + v_Time);
                
                if ( "1".equals(i_Live) || "2".equals(i_Live) )
                {
                    File v_LastTimeFile = FileHelp.findLastName(v_VideoRootDir ,null ,v_Finds[1] ,true);
                    if ( v_LastTimeFile != null )
                    {
                        fileName = StringHelp.replaceAll(v_LastTimeFile.toString() ,fileServiceSaveDir.getValue() ,"");
                    }
                }
                else
                {
                    fileName = findNewestVideo(v_VideoRootDir ,i_OFile ,v_Finds[1] ,fileName);
                }
                
                if ( Help.isNull(fileName) )
                {
                    return;
                }
            }
            
            File   v_VideoFile = new File(fileServiceSaveDir.getValue() + fileName);
            String v_VideoName = v_VideoFile.getName();
            String v_UserAgent = i_Request.getHeader("User-Agent").toLowerCase();
            
            i_Request.getSession().setAttribute("OFile" + i_Token ,v_VideoName);
            // 如果是火狐浏览器的话，设置浏览器的编码格式
            if ( v_UserAgent.indexOf("firefox") != -1 )
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + new String(v_VideoName.getBytes("GB2312"), "ISO-8859-1"));
            }
            else
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(v_VideoName, "UTF-8"));
            }
            i_Response.addHeader("HYFileName" ,v_VideoName);
     
            
            FileHelp v_FileHelp = new FileHelp();
            if ( v_RequestURI.toLowerCase().endsWith(".m3u8") )
            {
                if ( !v_VideoFile.exists() || !v_VideoFile.canRead() )
                {
                    $Logger.warn("访问视频：" + fileServiceSaveDir.getValue() + ":" + fileName + "\tof=" + i_OFile);
                    $Logger.warn("访问视频：" + v_VideoFile.toString());
                    return;
                }
                
                String v_M3U8Content = v_FileHelp.getContent(v_VideoFile ,"UTF-8" ,true);
                
                // http://127.0.0.1/msFile/file/play/T1N151/2022-12-09/14/20221209144755.h265.ts
                v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,"http://127.0.0.1/msFile" ,i_Request.getScheme() + "://" + i_Request.getServerName() + (i_Request.getServerPort() == 80 || i_Request.getServerPort() == 443 ? "" : ":" + i_Request.getServerPort()) + "/" + v_RequestURI.split("/")[1]);
                v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,".ts" ,".ts?token=" + i_Token);
                
                // 直播流：FFMpeg直接将RTSP转成的HLS流
                if ( "2".equals(i_Live) )
                {
                    StringBuilder v_M3U8Buffer = new StringBuilder();
                    String []     v_M3U8CArr   = v_M3U8Content.split("\n");
                    int           v_FRootIndex = fileName.lastIndexOf(Help.getSysPathSeparator());
                    String        v_FRoot      = StringHelp.replaceAll(fileName.substring(0 ,v_FRootIndex) ,"\\" ,"/");
                    
                    for (String v_Line : v_M3U8CArr)
                    {
                        if ( StringHelp.isContains(v_Line ,".ts") )
                        {
                            v_Line = i_Request.getScheme() + "://" + i_Request.getServerName() + (i_Request.getServerPort() == 80 || i_Request.getServerPort() == 443 ? "" : ":" + i_Request.getServerPort())
                                   + "/" + v_RequestURI.split("/")[1]
                                   + "/file/play/" + v_FRoot + "/"
                                   + v_Line;
                        }
                        
                        v_M3U8Buffer.append(v_Line).append("\n");
                    }
                    
                    v_M3U8Content = v_M3U8Buffer.toString();
                }
                // 直播流：通过SDK直取摄像设备上的视频数据转成的HLS流
                else if ( "1".equals(i_Live) )
                {
                    v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,"#EXT-X-ENDLIST" ,"");
                    Integer v_M3U8Sequence = 0;
                    String  v_OldFileName  = "";
                    boolean v_IsNew        = true;
                    
                    synchronized ( this )
                    {
                        v_OldFileName  = (String) i_Request.getSession().getAttribute(i_Token + "_FileName");
                        v_M3U8Sequence = (Integer)i_Request.getSession().getAttribute(i_Token + "_Sequence");
                        
                        v_IsNew = !v_VideoName.equals(v_OldFileName);
                        if ( v_M3U8Sequence == null || v_M3U8Sequence < 0 )
                        {
                            v_M3U8Sequence = 0;
                        }
                        else
                        {
                            if ( v_IsNew )
                            {
                                v_M3U8Sequence += 1;
                            }
                        }
                        i_Request.getSession().setAttribute(i_Token + "_FileName" ,v_VideoName);
                        i_Request.getSession().setAttribute(i_Token + "_Sequence" ,v_M3U8Sequence);
                    }
                    
                    String v_FindKey = "#EXT-X-MEDIA-SEQUENCE:";
                    int    v_SIndex  = v_M3U8Content.indexOf(v_FindKey);
                    int    v_EIndex  = v_M3U8Content.indexOf("\r\n" ,v_SIndex + v_FindKey.length());
                    
                    v_M3U8Content = v_M3U8Content.substring(0 ,v_SIndex + v_FindKey.length())
                                  + v_M3U8Sequence
                                  + v_M3U8Content.substring(v_EIndex);
                }
                
                byte [] v_M3U8Byte = v_M3U8Content.getBytes();
                i_Response.addHeader("Content-Length", "" + v_M3U8Byte.length);
                i_Response.setCharacterEncoding("UTF-8");
                i_Response.setContentType("application/x-mpegURL");
                
                v_VideoOut = i_Response.getOutputStream();
                v_VideoOut.write(v_M3U8Byte);
                
                $Logger.info("访问视频：" + v_M3U8Content);
            }
            else
            {
                byte [] v_VideoByte = v_FileHelp.getContentByte(v_VideoFile);
                v_VideoOut = i_Response.getOutputStream();
                v_VideoOut.write(v_VideoByte);
            }
            
            $Logger.info("访问视频：" + fileServiceSaveDir.getValue() + ":" + fileName + "\tof=" + i_OFile);
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        finally
        {
            // 此处不用关闭
            /*
            if ( null != v_VideoOut )
            {
                try
                {
                    v_VideoOut.flush();
                    v_VideoOut.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            */
        }
    }
    
    
    
    /**
     * 播放视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-18
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/play/**/{fileName:.+}","/{fileName:.+}"})
    public void paly(@PathVariable String fileName ,@RequestParam(value="token" ,required=false) String i_Token ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");              // 支持跨域请求 i_Request.getHeader("Origin")
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");           // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");
        i_Response.setDateHeader("Expires", 0);
        i_Response.setHeader("Cache-Control", "no-cache, no-store");
        i_Response.setHeader("Pragma", "no-cache");
        
        FileInputStream v_VideoInput = null;
        OutputStream    v_VideoOut   = null;
        
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( !v_RequestURI.toLowerCase().endsWith(".ts") )
            {
                $Logger.warn("扩展名非法：" + v_RequestURI);
                return;
            }
            
            if ( v_RequestURI.contains("play/") )
            {
                fileName = v_RequestURI.split("play/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            fileName = StringHelp.replaceAll(fileName ,"/" ,Help.getSysPathSeparator());
            
            File v_VideoFile   = new File(fileServiceSaveDir.getValue() + fileName);
            String v_VideoName = v_VideoFile.getName();
            String v_UserAgent = i_Request.getHeader("User-Agent").toLowerCase();
            
            if ( !v_VideoFile.exists() || !v_VideoFile.canRead() )
            {
                $Logger.warn("请求文件不存在：" + v_RequestURI);
                v_VideoFile = new File(fileServiceSaveDir.getValue() + "loading.ts");
            }
            
            // 如果是火狐浏览器的话，设置浏览器的编码格式
            if ( v_UserAgent.indexOf("firefox") != -1 )
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + new String(v_VideoName.getBytes("GB2312"), "ISO-8859-1"));
            }
            else
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(v_VideoName, "UTF-8"));
            }
     
            i_Response.setCharacterEncoding("UTF-8");
            i_Response.addHeader("Content-Length", "" + v_VideoFile.length());
            i_Response.setContentType("video/mpeg4");
            
            
            // 获取response输出流
            v_VideoOut   = i_Response.getOutputStream();
            v_VideoInput = new FileInputStream(v_VideoFile);
            byte [] v_VideoBuffer = new byte[1024];
            int     v_BufferLen   = 0;
            while ( (v_BufferLen = v_VideoInput.read(v_VideoBuffer)) != -1 )
            {
                v_VideoOut.write(v_VideoBuffer ,0 ,v_BufferLen);
            }
            
            $Logger.info("播放视频：" + fileServiceSaveDir.getValue() + fileName);
        }
        catch (Exception e)
        {
            $Logger.error(e);
        }
        finally
        {
            if ( null != v_VideoInput )
            {
                try
                {
                    v_VideoInput.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                
                v_VideoInput = null;
            }
            
            // 此处不用关闭
            /*
            if ( null != v_VideoOut )
            {
                try
                {
                    v_VideoOut.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
            */
        }
    }
    
    
    
    /**
     * 显示图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/showImage/**/{fileName:.+}","/{fileName:.+}"}, produces={"image/jpeg" ,"image/png" ,"image/gif" ,"application/x-jpg"})
    @ResponseBody
    public ResponseEntity<Resource> showImage(@PathVariable String fileName ,HttpServletRequest i_Request)
    {
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showImage/") )
            {
                fileName = v_RequestURI.split("showImage/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // resourceLoader.getResource("file:" + uploadPicturePath + fileName) 返回指定路径的资源句柄，这里返回的就是URL [file:D:/springboot/upload/test.png]
            // ResponseEntity.ok(T) 返回指定内容主体
            return ResponseEntity.ok(resourceLoader.getResource("file:" + fileServiceSaveDir.getValue() + fileName));
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 显示Excel
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-28
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/showExcel/**/{fileName:.+}","/{fileName:.+}"}, produces={"application/vnd.ms-excel" ,"application/x-xls"})
    @ResponseBody
    public ResponseEntity<Resource> showExcel(@PathVariable String fileName ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showExcel/") )
            {
                fileName = v_RequestURI.split("showExcel/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // 设置附加文件名。转码是为了防止文件名称中文时乱码的情况
            i_Response.setHeader("Content-Disposition" ,"attachment;filename=" + StringHelp.toCharEncoding(fileName ,"UTF-8" ,"ISO-8859-1"));
            i_Response.setStatus(HttpServletResponse.SC_OK);
            
            return ResponseEntity.ok(resourceLoader.getResource("file:" + fileServiceSaveDir.getValue() + fileName));
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 显示Word
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-28
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/showWord/**/{fileName:.+}","/{fileName:.+}"}, produces={"application/msword"})
    @ResponseBody
    public ResponseEntity<Resource> showWord(@PathVariable String fileName ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showWord/") )
            {
                fileName = v_RequestURI.split("showWord/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // 设置附加文件名。转码是为了防止文件名称中文时乱码的情况
            i_Response.setHeader("Content-Disposition" ,"attachment;filename=" + StringHelp.toCharEncoding(fileName ,"UTF-8" ,"ISO-8859-1"));
            i_Response.setStatus(HttpServletResponse.SC_OK);
            
            return ResponseEntity.ok(resourceLoader.getResource("file:" + fileServiceSaveDir.getValue() + fileName));
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 显示PPT
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-28
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/showPPT/**/{fileName:.+}","/{fileName:.+}"}, produces={"application/vnd.ms-powerpoint" ,"application/x-ppt"})
    @ResponseBody
    public ResponseEntity<Resource> showPPT(@PathVariable String fileName ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showPPT/") )
            {
                fileName = v_RequestURI.split("showPPT/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // 设置附加文件名。转码是为了防止文件名称中文时乱码的情况
            i_Response.setHeader("Content-Disposition" ,"attachment;filename=" + StringHelp.toCharEncoding(fileName ,"UTF-8" ,"ISO-8859-1"));
            i_Response.setStatus(HttpServletResponse.SC_OK);
            
            return ResponseEntity.ok(resourceLoader.getResource("file:" + fileServiceSaveDir.getValue() + fileName));
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 显示文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-28
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     */
    @RequestMapping(value={"/showFile/**/{fileName:.+}","/{fileName:.+}"}, produces={"application/octet-stream"})
    @ResponseBody
    public ResponseEntity<Resource> showFile(@PathVariable String fileName ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showFile/") )
            {
                fileName = v_RequestURI.split("showFile/")[1];
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
            
            // 设置附加文件名。转码是为了防止文件名称中文时乱码的情况
            i_Response.setHeader("Content-Disposition" ,"attachment;filename=" + StringHelp.toCharEncoding(fileName ,"UTF-8" ,"ISO-8859-1"));
            i_Response.setStatus(HttpServletResponse.SC_OK);
            
            return ResponseEntity.ok(resourceLoader.getResource("file:" + fileServiceSaveDir.getValue() + fileName));
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 上传视频或图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-08-13
     * @version     v1.0
     *
     * @param i_Params
     * @return
     */
    @RequestMapping(value="upload" ,produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FileInfo upload(HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        FileInfo                             v_Ret              = new FileInfo();
        MultipartHttpServletRequest          v_MultipartRequest = (MultipartHttpServletRequest) i_Request;
        MultiValueMap<String, MultipartFile> v_FileMap          = v_MultipartRequest.getMultiFileMap();
        String                               v_FileName         = null;
        FileHelp                             v_FileHelp         = new FileHelp();
        String                               v_ServiceType      = i_Request.getParameter("serviceType");
        String                               v_UserID           = i_Request.getParameter("userid");
        String                               v_UserName         = i_Request.getParameter("username");
        String                               v_UserIcon         = i_Request.getParameter("usericon");
        String                               v_GroupID          = i_Request.getParameter("groupid");
        String                               v_GroupInfo        = i_Request.getParameter("groupinfo");
        
        try
        {
            // 保存用户头像
            if ( !Help.isNull(v_UserIcon) )
            {
                String v_UserIconFName = StringHelp.getFileName(StringHelp.replaceAll(v_UserIcon ,new String[] {"https://thirdwx.qlogo.cn/mmopen" ,"/"} ,new String[] {""}));
                String v_SaveFullPath  = this.fileServiceSaveDir.getValue() + "userIcon" + Help.getSysPathSeparator() + v_UserIconFName;
                File   v_UserIconFile  = new File(v_SaveFullPath);
                
                // 防止重复保存
                if ( !v_UserIconFile.exists() )
                {
                    v_FileHelp.download(v_UserIcon ,v_UserIconFile);
                }
                
                $Logger.info("保存用户(" + v_UserID + ")头像" + v_SaveFullPath + " = " + v_UserIcon);
                v_UserIcon = fileServiceURL.getValue() + "/file/showImage/userIcon/" + v_UserIconFName;
            }
            else
            {
                v_UserIcon = "";
            }
            
            int    v_FileCount = 0;
            String v_GifName   = "";
            
            for (Map.Entry<String, List<MultipartFile>> entity : v_FileMap.entrySet())
            {
                List<MultipartFile> mfs = entity.getValue();
                for (MultipartFile mf:mfs)
                {
                    // 上传文件名、后缀名
                    v_FileName = mf.getOriginalFilename();
                    //如果文件名不为“”也不为空，则说明这个entity有上传文件
                    if ( !Help.isNull(v_FileName) )
                    {
                        //获取文件输入流
                        InputStream in = mf.getInputStream();
                        
                        // 输出到本地路径 WEB-INF/classes/uploadFiles
                        v_GifName  = StringHelp.getUUID();
                        v_FileName = v_GifName + StringHelp.getFilePostfix(v_FileName);
                        v_FileName = v_FileName.toLowerCase();
                        
                        String v_SaveDir      = this.fileServiceSaveDir.getValue() + v_ServiceType + "/";
                        String v_SaveFullPath = v_SaveDir + v_FileName;
                        File   v_OutDir       = new File(v_SaveDir);
                        File   v_OutFile      = new File(v_SaveFullPath);
                        
                        if ( !v_OutDir.exists() )
                        {
                            v_OutDir.mkdirs();
                        }
                        
                        v_FileHelp.copyFile(in ,v_OutFile);
                        
                        $Logger.info(v_SaveFullPath);
                        
                        if ( StringHelp.isContains(v_FileName ,".png" ,".jpg" ,".bmp") )
                        {
                            ImageInfo v_Image = new ImageInfo();
                            
                            v_Image.setServiceType(v_ServiceType);
                            v_Image.setUserID(v_UserID);
                            v_Image.setUserName(v_UserName);
                            v_Image.setUserIcon(v_UserIcon);
                            v_Image.setFileSize(v_OutFile.length());
                            v_Image.setUrl(fileServiceURL.getValue() + "/file/showImage/" + v_ServiceType + "/" + v_FileName);
                            v_Image.setPath(v_SaveFullPath);
                            v_Image.setGroupID(v_GroupID);
                            v_Image.setGroupInfo(v_GroupInfo);
                            v_Image.setIsShow(1);
                            
                            this.getImageInfo(v_OutFile ,v_Image);
                            
                            fileService.addImage(v_Image);
                            v_FileCount++;
                            
                            if ( !Help.isNull(v_Ret.getUrl()) )
                            {
                                v_Ret.setId( v_Ret.getId()  + ";");
                                v_Ret.setUrl(v_Ret.getUrl() + ";");
                            }
                            
                            v_Ret.setId(Help.NVL( v_Ret.getId())  + v_Image.getId());
                            v_Ret.setUrl(Help.NVL(v_Ret.getUrl()) + v_Image.getUrl());
                        }
                        else if ( StringHelp.isContains(v_FileName ,".avi" ,".mp4" ,"m4v" ,"mov" ,"3gp" ,"webm") )
                        {
                            VideoInfo v_Video = new VideoInfo();
                            
                            v_Video.setServiceType(v_ServiceType);
                            v_Video.setUserID(v_UserID);
                            v_Video.setUserName(v_UserName);
                            v_Video.setUserIcon(v_UserIcon);
                            v_Video.setFileSize(v_OutFile.length());
                            v_Video.setUrl(fileServiceURL.getValue() + "/file/showVideo/" + v_ServiceType + "/" + v_FileName);
                            v_Video.setPath(v_SaveFullPath);
                            v_Video.setGroupID(v_GroupID);
                            v_Video.setGroupInfo(v_GroupInfo);
                            v_Video.setIsShow(1);
                            
                            this.getVideoInfo(v_SaveFullPath ,v_Video);
                            
                            
                            // 生成缩略图（动图）
                            v_GifName = v_GifName.toLowerCase() + ".gif";
                            String    v_GifPath = v_SaveDir + v_GifName;
                            double    v_WHDiff  = Help.subtract(v_Video.getWidth() ,v_Video.getHeight());
                            boolean   v_GifRet  = VideoHelp.toGif(v_OutFile.toString() ,v_GifPath ,5 * 24 ,v_WHDiff >= 0 ? "480x320" : "320x480");
                            
                            if ( v_GifRet )
                            {
                                ImageInfo v_Image = new ImageInfo();
                                
                                v_Image.setServiceType(v_ServiceType);
                                v_Image.setUserID(v_UserID);
                                v_Image.setUserName(v_UserName);
                                v_Image.setUserIcon(v_UserIcon);
                                v_Image.setFileSize(new File(v_GifPath).length());
                                v_Image.setUrl(fileServiceURL.getValue() + "/file/showImage/" + v_ServiceType + "/" + v_GifName);
                                v_Image.setPath(v_GifPath);
                                v_Image.setGroupID(v_GroupID);
                                v_Image.setGroupInfo(v_GroupInfo);
                                v_Image.setIsShow(0);
                                v_Image.setWidth( v_WHDiff >= 0 ? "480" : "320");
                                v_Image.setHeight(v_WHDiff >= 0 ? "320" : "480");
                                
                                fileService.addImage(v_Image);
                                v_Video.setThumbnailFileID(v_Image.getId());
                                
                                v_FileCount++;
                                
                                if ( !Help.isNull(v_Ret.getUrl()) )
                                {
                                    v_Ret.setId( v_Ret.getId()  + ";");
                                    v_Ret.setUrl(v_Ret.getUrl() + ";");
                                }
                                
                                v_Ret.setId(Help.NVL( v_Ret.getId())  + v_Image.getId());
                                v_Ret.setUrl(Help.NVL(v_Ret.getUrl()) + v_Image.getUrl());
                            }
                            
                            if ( v_FileName.toLowerCase().endsWith(".mp4") )
                            {
                                String v_M3U8Path = VideoHelp.mp4ToM3U8(v_SaveFullPath ,v_SaveDir ,3 ,"http://127.0.0.1/msFile/file/play/" + v_ServiceType + "/");
                                
                                if ( !Help.isNull(v_M3U8Path) )
                                {
                                    v_SaveFullPath = v_M3U8Path;
                                    v_FileName     = StringHelp.replaceAll(v_FileName ,new String[]{".mp4" ,".MP4" ,".mP4" ,".Mp4"} ,new String[]{".m3u8"});
                                    v_OutFile.delete();
                                    
                                    v_Video.setUrl(fileServiceURL.getValue() + "/file/showVideo/" + v_ServiceType + "/" + v_FileName);
                                    v_Video.setPath(v_SaveFullPath);
                                }
                            }
                            
                            fileService.addVideo(v_Video);
                            
                            v_FileCount++;
                            
                            if ( !Help.isNull(v_Ret.getUrl()) )
                            {
                                v_Ret.setId( v_Ret.getId()  + ";");
                                v_Ret.setUrl(v_Ret.getUrl() + ";");
                            }
                            
                            v_Ret.setId(Help.NVL( v_Ret.getId())  + v_Video.getId());
                            v_Ret.setUrl(Help.NVL(v_Ret.getUrl()) + v_Video.getUrl());
                        }
                        else
                        {
                            FileInfo v_File     = new FileInfo();
                            String   v_ShowName = "showFile";
                            
                            if ( StringHelp.isContains(v_FileName ,".xls" ,".xlsx") )
                            {
                                v_File.setType(3);
                                v_ShowName = "showExcel";
                            }
                            else if ( StringHelp.isContains(v_FileName ,".doc" ,".docx") )
                            {
                                v_File.setType(4);
                                v_ShowName = "showWord";
                            }
                            else if ( StringHelp.isContains(v_FileName ,".ppt" ,".pptx") )
                            {
                                v_File.setType(5);
                                v_ShowName = "showPPT";
                            }
                            else if ( StringHelp.isContains(v_FileName ,".pdf") )
                            {
                                v_File.setType(6);
                                v_ShowName = "showPDF";
                            }
                            
                            v_File.setServiceType(v_ServiceType);
                            v_File.setUserID(v_UserID);
                            v_File.setUserName(v_UserName);
                            v_File.setUserIcon(v_UserIcon);
                            v_File.setFileSize(v_OutFile.length());
                            v_File.setUrl(fileServiceURL.getValue() + "/file/" + v_ShowName + "/" + v_ServiceType + "/" + v_FileName);
                            v_File.setPath(v_SaveFullPath);
                            v_File.setGroupID(v_GroupID);
                            v_File.setGroupInfo(v_GroupInfo);
                            v_File.setIsShow(1);
                            
                            fileService.addFile(v_File);
                            v_FileCount++;
                            
                            if ( !Help.isNull(v_Ret.getUrl()) )
                            {
                                v_Ret.setId( v_Ret.getId()  + ";");
                                v_Ret.setUrl(v_Ret.getUrl() + ";");
                            }
                            
                            v_Ret.setId(Help.NVL( v_Ret.getId())  + v_File.getId());
                            v_Ret.setUrl(Help.NVL(v_Ret.getUrl()) + v_File.getUrl());
                        }
                        
                        $Logger.info("用户(" + v_UserID + ")上传发布" + v_SaveFullPath);
                    }
                }
            }
            
            return v_Ret;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        v_Ret.setUrl("");
        
        return v_Ret;
    }
    
    
    
    /**
     * 图片信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-09-27
     * @version     v1.0
     *
     * @param i_Image
     * @param io_ImageInfo
     */
    private void getImageInfo(File i_Image ,ImageInfo io_ImageInfo)
    {
        try
        {
            BufferedImage v_Img = ImageIO.read(new FileInputStream(i_Image));
            
            if ( v_Img != null )
            {
                io_ImageInfo.setWidth( "" + v_Img.getWidth());
                io_ImageInfo.setHeight("" + v_Img.getHeight());
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 获取视频信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-09-26
     * @version     v1.0
     *
     * @param i_VideoFile
     * @param io_Video
     */
    private void getVideoInfo(String i_VideoFile ,VideoInfo io_Video)
    {
        try
        {
            VideoHelp.$FFMpegHome = this.ffMpegHome.getValue();
            
            org.hy.common.video.VideoInfo v_Info = VideoHelp.getVideoInfo(i_VideoFile);
            
            if ( v_Info != null )
            {
                if ( v_Info.getWidth() != null )
                {
                    io_Video.setWidth("" + v_Info.getWidth());
                }
                
                if ( v_Info.getHeight() != null )
                {
                    io_Video.setHeight("" + v_Info.getHeight());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
