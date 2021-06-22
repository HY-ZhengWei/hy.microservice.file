package org.hy.microservice.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    
    private static final Logger $Logger = new Logger(FileController.class);
    
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
     * @author      ZhengWei(HY)
     * @createDate  2021-06-18
     * @version     v1.0
     *
     * @param fileName
     * @param i_Request
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping(value={"/getVideo/{*}/{fileName:.+}","/{fileName:.+}"})
    public String getVideo(@PathVariable String fileName
                          ,@RequestParam(value="image"        ,required=false) String i_Image
                          ,@RequestParam(value="width"        ,required=false) String i_Width
                          ,@RequestParam(value="height"       ,required=false) String i_Height
                          ,@RequestParam(value="auto"         ,required=false) String i_Auto
                          ,@RequestParam(value="loopPlayback" ,required=false) String i_LoopPlayback
                          ,ModelMap io_Model
                          ,HttpServletRequest i_Request
                          ,HttpServletResponse i_Response) throws UnsupportedEncodingException
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");     // 支持跨域请求
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");  // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");
        
        String v_RequestURI = i_Request.getRequestURI();
        if ( !v_RequestURI.endsWith(".page") )
        {
            return "";
        }
        
        String v_ServerHome = i_Request.getScheme() + "://" + i_Request.getServerName() + "/" + v_RequestURI.split("/")[1];
        
        if ( v_RequestURI.contains("getVideo/") )
        {
            fileName = v_RequestURI.split("getVideo/")[1];
        }
        
        String v_M3U8 = v_ServerHome + "/file/showVideo/" + StringHelp.replaceAll(fileName ,".page" ,".m3u8");
        
        io_Model.put("videoWidth"        ,Help.NVL(i_Width ,"100%"));
        io_Model.put("videoHeight"       ,Help.NVL(i_Height ,"100%"));
        io_Model.put("videoAuto"         ,Help.NVL(i_Auto ,"0"));
        io_Model.put("videoLoopPlayback" ,Help.NVL(i_LoopPlayback ,"0"));
        io_Model.put("videoImage"        ,Help.NVL(i_Image));
        io_Model.put("videoUrl"          ,new String(Base64Factory.getIntance().encode(v_M3U8) ,"UTF-8"));
        
        return "/video/video";
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
     * @return
     */
    @RequestMapping(value={"/showVideo/{*}/{fileName:.+}","/{fileName:.+}"})
    public void showVideo(@PathVariable String fileName ,@RequestParam(value="token" ,required=false) String i_Token ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");     // 支持跨域请求
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");  // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");
        
        OutputStream v_VideoOut = null;
        
        try
        {
            String v_RequestURI = i_Request.getRequestURI();
            if ( v_RequestURI.contains("showVideo/") )
            {
                fileName = v_RequestURI.split("showVideo/")[1];
            }
            
            File   v_VideoFile = new File(fileServiceSaveDir.getValue() + fileName);
            String v_VideoName = v_VideoFile.getName();
            String v_UserAgent = i_Request.getHeader("User-Agent").toLowerCase();
            
            // 如果是火狐浏览器的话，设置浏览器的编码格式
            if ( v_UserAgent.indexOf("firefox") != -1 )
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + new String(v_VideoName.getBytes("GB2312"), "ISO-8859-1"));
            }
            else
            {
                i_Response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(v_VideoName, "UTF-8"));
            }
     
            
            FileHelp v_FileHelp = new FileHelp();
            
            if ( v_RequestURI.toLowerCase().endsWith(".m3u8") )
            {
                String v_M3U8Content = v_FileHelp.getContent(v_VideoFile ,"UTF-8" ,true);
                v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,"http://127.0.0.1" ,i_Request.getScheme() + "://" + i_Request.getServerName() + (i_Request.getServerPort() == 80 ? "" : ":" + i_Request.getServerPort()));
                v_M3U8Content = StringHelp.replaceAll(v_M3U8Content ,".ts" ,".ts?token=" + i_Token);
                
                byte [] v_M3U8Byte = v_M3U8Content.getBytes();
                i_Response.addHeader("Content-Length", "" + v_M3U8Byte.length);
                i_Response.setCharacterEncoding("UTF-8");
                i_Response.setContentType("application/x-mpegURL");
                
                v_VideoOut = i_Response.getOutputStream();
                v_VideoOut.write(v_M3U8Byte);
            }
            else
            {
                byte [] v_VideoByte = v_FileHelp.getContentByte(v_VideoFile);
                v_VideoOut = i_Response.getOutputStream();
                v_VideoOut.write(v_VideoByte);
            }
            
            $Logger.info("访问视频：" + fileServiceSaveDir.getValue() + fileName);
        }
        catch (Exception e)
        {
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
            
            $Logger.error(e);
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
    @RequestMapping(value={"/play/{*}/{fileName:.+}","/{fileName:.+}"})
    public void paly(@PathVariable String fileName ,@RequestParam(value="token" ,required=false) String i_Token ,HttpServletRequest i_Request ,HttpServletResponse i_Response)
    {
        i_Response.setHeader("Access-Control-Allow-Origin"      ,"*");     // 支持跨域请求
        i_Response.setHeader("Access-Control-Allow-Credentials" ,"true");  // 支持cookie跨域
        i_Response.setHeader("Access-Control-Allow-Methods"     ,"*");
        i_Response.setHeader("Access-Control-Allow-Headers"     ,"Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token");
        
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
            
            File   v_VideoFile = new File(fileServiceSaveDir.getValue() + fileName);
            String v_VideoName = v_VideoFile.getName();
            String v_UserAgent = i_Request.getHeader("User-Agent").toLowerCase();
            
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
        }
        catch (Exception e)
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
            }
            
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
    @RequestMapping(value={"/showImage/{*}/{fileName:.+}","/{fileName:.+}"}, produces={"image/jpeg" ,"image/png" ,"image/gif" ,"application/x-jpg"})
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
    @RequestMapping(value={"/showExcel/{*}/{fileName:.+}","/{fileName:.+}"}, produces={"application/vnd.ms-excel" ,"application/x-xls"})
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
    @RequestMapping(value={"/showWord/{*}/{fileName:.+}","/{fileName:.+}"}, produces={"application/msword"})
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
    @RequestMapping(value={"/showPPT/{*}/{fileName:.+}","/{fileName:.+}"}, produces={"application/vnd.ms-powerpoint" ,"application/x-ppt"})
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
    @RequestMapping(value={"/showFile/{*}/{fileName:.+}","/{fileName:.+}"}, produces={"application/octet-stream"})
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
                        
                        $Logger.info(v_FileName);
                        
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
                            if ( v_FileName.toLowerCase().endsWith(".mp4") )
                            {
                                String v_M3U8Path = VideoHelp.mp4ToM3U8(v_SaveFullPath ,v_SaveDir ,3 ,"http://127.0.0.1/msFile/file/paly/" + v_ServiceType + "/");
                                
                                if ( !Help.isNull(v_M3U8Path) )
                                {
                                    v_SaveFullPath = v_M3U8Path;
                                    v_FileName = StringHelp.replaceAll(v_FileName ,new String[]{".mp4" ,".MP4" ,".mP4" ,".Mp4"} ,new String[]{".m3u8"});
                                    v_OutFile.delete();
                                }
                            }
                            
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
                            
                            this.getVideoInfo(v_OutFile ,v_Video);
                            
                            
                            // 生成缩略图（动图）
                            v_GifName = v_GifName.toLowerCase() + ".gif";
                            String    v_GifPath = v_SaveDir + v_GifName;
                            double    v_WHDiff  = Help.subtract(v_Video.getWidth() ,v_Video.getHeight());
                            boolean   v_GifRet  = VideoHelp.toGif(v_OutFile.toString() ,v_GifPath ,10 * 24 ,v_WHDiff >= 0 ? "480x320" : "320x480");
                            
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
    private void getVideoInfo(File i_VideoFile ,VideoInfo io_Video)
    {
        try
        {
            VideoHelp.$FFMpegHome = this.ffMpegHome.getValue();
            
            org.hy.common.video.VideoInfo v_Info = VideoHelp.getVideoInfo(i_VideoFile.toString());
            
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
