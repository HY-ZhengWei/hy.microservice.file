package org.hy.microservice.file;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.xml.annotation.Xjava;





/**
 * 文件操作的业务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-10-16
 * @version     v1.0
 */
@Xjava
public class FileService
{
    
    @Xjava
    private IFileDAO fileDAO;
    
    
    
    /**
     * 添加文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @param io_File
     * @return
     */
    public boolean addFile(FileInfo io_File)
    {
        io_File.setId(StringHelp.getUUID());
        io_File.setServiceType(Help.NVL(io_File.getServiceType() ,"-"));
        io_File.setType(io_File.getType() == null ? 0 : io_File.getType());
        io_File.setAuditState("1");
        
        return this.fileDAO.addFile(io_File) == 1;
    }
    
    
    
    /**
     * 添加图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @param io_Image
     * @return
     */
    public boolean addImage(ImageInfo io_Image)
    {
        io_Image.setId(StringHelp.getUUID());
        io_Image.setServiceType(Help.NVL(io_Image.getServiceType() ,"-"));
        io_Image.setType(1);
        io_Image.setAuditState("1");
        
        return this.fileDAO.addImage(io_Image) == 1;
    }
    
    
    
    /**
     * 添加视频
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-11-12
     * @version     v1.0
     *
     * @param io_Video
     * @return
     */
    public boolean addVideo(VideoInfo io_Video)
    {
        io_Video.setId(StringHelp.getUUID());
        io_Video.setServiceType(Help.NVL(io_Video.getServiceType() ,"-"));
        io_Video.setType(2);
        io_Video.setAuditState("1");
        
        return this.fileDAO.addVideo(io_Video) == 1;
    }
    
    
    
    /**
     * 获取所有文件列表
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    public List<FileInfo> queryFiles()
    {
        return this.fileDAO.queryFiles();
    }
    
    
    
    /**
     * 获取所有图片列表
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    public List<ImageInfo> queryImages()
    {
        return this.fileDAO.queryImages();
    }
    
    
    
    /**
     * 获取一个图片
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    public ImageInfo queryImage(String i_ID)
    {
        return this.fileDAO.queryImage(i_ID);
    }
    
    
    
    /**
     * 获取一个文件
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-16
     * @version     v1.0
     *
     * @return
     */
    public FileInfo queryFile(String i_ID)
    {
        return this.fileDAO.queryFile(i_ID);
    }
    
}
