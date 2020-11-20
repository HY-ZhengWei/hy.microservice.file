package org.hy.microservice.file;

import java.util.List;

import org.hy.common.xml.annotation.XType;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.annotation.Xparam;
import org.hy.common.xml.annotation.Xsql;





/**
 * 文件的DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-10-16
 * @version     v1.0
 */
@Xjava(id="FileDAO" ,value=XType.XSQL)
public interface IFileDAO
{
    
    /**
     * 添加文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @param i_File
     * @return
     */
    @Xsql("XSQL_FileInfo_Insert_Default")
    public int addFile(FileInfo i_File);
    
    
    
    /**
     * 添加图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @param i_Image
     * @return
     */
    @Xsql("XSQL_FileInfo_Insert_Image")
    public int addImage(ImageInfo i_Image);
    
    
    
    /**
     * 添加视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-12
     * @version     v1.0
     *
     * @param i_Video
     * @return
     */
    @Xsql("XSQL_FileInfo_Insert_Video")
    public int addVideo(VideoInfo i_Video);
    
    
    
    /**
     * 获取所有视频列表
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-12
     * @version     v1.0
     *
     * @return
     */
    @Xsql("XSQL_FileInfo_QueryVideos_ByAll")
    public List<VideoInfo> queryVideos();
    
    
    
    /**
     * 获取所有图片列表
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    @Xsql("XSQL_FileInfo_QueryImages_ByAll")
    public List<ImageInfo> queryImages();
    
    
    
    /**
     * 获取所有文件列表
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    @Xsql("XSQL_FileInfo_QueryFiles_ByAll")
    public List<FileInfo> queryFiles();
    
    
    
    /**
     * 获取一个视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-12
     * @version     v1.0
     *
     * @return
     */
    @Xsql(id="XSQL_FileInfo_QueryVideo_ByID" ,returnOne=true)
    public VideoInfo queryVideo(@Xparam(id="id" ,notNull=true) String i_ID);
    
    
    
    /**
     * 获取一个图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    @Xsql(id="XSQL_FileInfo_QueryImage_ByID" ,returnOne=true)
    public ImageInfo queryImage(@Xparam(id="id" ,notNull=true) String i_ID);
    
    
    
    /**
     * 获取一个文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    @Xsql(id="XSQL_FileInfo_QueryFile_ByID" ,returnOne=true)
    public FileInfo queryFile(@Xparam(id="id" ,notNull=true) String i_ID);
    
    
}