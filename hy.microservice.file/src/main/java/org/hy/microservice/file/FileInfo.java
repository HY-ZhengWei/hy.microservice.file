package org.hy.microservice.file;

import org.hy.microservice.file.common.BaseViewMode;





/**
 * 文件信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-10-16
 * @version     v1.0
 */
public class FileInfo extends BaseViewMode
{
    
    private static final long serialVersionUID = 7998652338890257393L;

    /** 主键ID */
    private String  id;
    
    /** 文件类型：0.普通文件；1.照片；2.视频；3.Excel；4.Word；5.PPT；6.PDF；7.压缩包 */
    private Integer type;
    
    /** 文件地址 */
    private String  url;
    
    /** 文件保存的路径（全路径） */
    private String  path;
    
    /** 分组编号 */
    private String  groupID;
    
    /** 分组信息 */
    private String  groupInfo;
    
    /** 文件大小 */
    private Long    fileSize;
    
    /** 点赞次数 */
    private Long    goodCount;
    
    /** 我是否点赞过 */
    private Integer myIsNice; 
    
    
    
    public String getGoodCountInfo()
    {
        return toCountInfo(this.goodCount);
    }
    
    
    
    public void setGoodCountInfo(String i_GoodCountInfo)
    {
        // Nothing.
    }
    
    
    
    public void setLocation(String i_Location)
    {
        this.url = i_Location;
    }
    
    
    /**
     * 配合富文本编辑器的使用，用于返回值的传递
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-10-19
     * @version     v1.0
     *
     * @return
     */
    public String getLocation()
    {
        return this.url;
    }


    /**
     * 获取：主键ID
     */
    public String getId()
    {
        return id;
    }


    /**
     * 获取：文件地址
     */
    public String getUrl()
    {
        return url;
    }


    /**
     * 获取：文件类型：0.普通文件；1.照片；2.视频；3.Excel；4.Word；5.PPT；6.PDF；7.压缩包
     */
    public Integer getType()
    {
        return type;
    }

    
    /**
     * 获取：分组编号
     */
    public String getGroupID()
    {
        return groupID;
    }

    
    /**
     * 获取：分组信息
     */
    public String getGroupInfo()
    {
        return groupInfo;
    }


    /**
     * 获取：点赞次数
     */
    public Long getGoodCount()
    {
        return goodCount;
    }

    
    /**
     * 获取：我是否点赞过
     */
    public Integer getMyIsNice()
    {
        return myIsNice;
    }

    
    /**
     * 设置：主键ID
     * 
     * @param id 
     */
    public void setId(String id)
    {
        this.id = id;
    }

    
    /**
     * 设置：文件地址
     * 
     * @param url 
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    
    /**
     * 设置：文件类型：0.普通文件；1.照片；2.视频；3.Excel；4.Word；5.PPT；6.PDF；7.压缩包
     * 
     * @param type 
     */
    public void setType(Integer type)
    {
        this.type = type;
    }

    
    /**
     * 设置：分组编号
     * 
     * @param groupID 
     */
    public void setGroupID(String groupID)
    {
        this.groupID = groupID;
    }

    
    /**
     * 设置：分组信息
     * 
     * @param groupInfo 
     */
    public void setGroupInfo(String groupInfo)
    {
        this.groupInfo = groupInfo;
    }


    /**
     * 设置：点赞次数
     * 
     * @param goodCount 
     */
    public void setGoodCount(Long goodCount)
    {
        this.goodCount = goodCount;
    }

    
    /**
     * 设置：我是否点赞过
     * 
     * @param myIsNice 
     */
    public void setMyIsNice(Integer myIsNice)
    {
        this.myIsNice = myIsNice;
    }

    
    /**
     * 获取：文件大小
     */
    public Long getFileSize()
    {
        return fileSize;
    }

    
    /**
     * 设置：文件大小
     * 
     * @param fileSize 
     */
    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    
    /**
     * 获取：文件保存的路径（全路径）
     */
    public String getPath()
    {
        return path;
    }

    
    /**
     * 设置：文件保存的路径（全路径）
     * 
     * @param path 
     */
    public void setPath(String path)
    {
        this.path = path;
    }

}
