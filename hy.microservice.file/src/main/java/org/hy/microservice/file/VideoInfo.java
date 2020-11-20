package org.hy.microservice.file;





/**
 * 视频信息 
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-11-12
 * @version     v1.0
 */
public class VideoInfo extends FileInfo
{
    
    private static final long serialVersionUID = 7524104877789168432L;
    
    

    /** 视频宽度 */
    private String width;
    
    /** 视频高度 */
    private String height;
    
    /** 缩略图的文件ID */
    private String thumbnailFileID;

    
    
    /**
     * 获取：视频宽度
     */
    public String getWidth()
    {
        return width;
    }

    
    /**
     * 获取：视频高度
     */
    public String getHeight()
    {
        return height;
    }

    
    /**
     * 获取：缩略图的文件ID
     */
    public String getThumbnailFileID()
    {
        return thumbnailFileID;
    }

    
    /**
     * 设置：视频宽度
     * 
     * @param width 
     */
    public void setWidth(String width)
    {
        this.width = width;
    }

    
    /**
     * 设置：视频高度
     * 
     * @param height 
     */
    public void setHeight(String height)
    {
        this.height = height;
    }

    
    /**
     * 设置：缩略图的文件ID
     * 
     * @param thumbnailFileID 
     */
    public void setThumbnailFileID(String thumbnailFileID)
    {
        this.thumbnailFileID = thumbnailFileID;
    }
    
}
