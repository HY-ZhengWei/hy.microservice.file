package org.hy.microservice.file;





/**
 * 图片信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-07-22
 * @version     v1.0
 */
public class ImageInfo extends FileInfo
{

    private static final long serialVersionUID = 2192151673923481906L;
    
    
    
    /** 图片宽度 */
    private String width;
    
    /** 图片高度 */
    private String height;

    
    
    /**
     * 获取：图片宽度
     */
    public String getWidth()
    {
        return width;
    }

    
    /**
     * 获取：图片高度
     */
    public String getHeight()
    {
        return height;
    }

    
    /**
     * 设置：图片宽度
     * 
     * @param width 
     */
    public void setWidth(String width)
    {
        this.width = width;
    }

    
    /**
     * 设置：图片高度
     * 
     * @param height 
     */
    public void setHeight(String height)
    {
        this.height = height;
    }
    
    
}
