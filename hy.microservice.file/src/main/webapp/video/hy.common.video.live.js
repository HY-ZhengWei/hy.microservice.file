var ofile = '';





/**
 * 获取视频信息
 *
 * @param i_VideoObj  视频对象
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-25
 * @version     v1.0
 */
function showVideoLoading()
{
    $('#loadingBar').show();
}



/**
 * 获取视频信息
 *
 * @param i_VideoObj  视频对象
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-25
 * @version     v1.0
 */
function hideVideoLoading()
{
    $('#loadingBar').hide();
}



/**
 * 获取视频信息
 *
 * @param i_VideoID   视频对象ID
 * @param i_IsAuto    是否自动播放
 * @param i_IsControl 是否允许控制视频
 * @param i_Width     宽度
 * @param i_Height    高度
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-22
 * @version     v1.0
 */
function getVideoUrl(i_CallBackFun ,i_VideoID ,i_IsAuto ,i_IsControl ,i_Width ,i_Height ,i_VideoUrl)
{
    if ( i_CallBackFun != null )
    { 
        i_CallBackFun(i_VideoID ,i_IsAuto ,i_IsControl ,i_Width ,i_Height ,window.atob(i_VideoUrl) ,i_VideoUrl);
    }
}



/**
 * 初始化视频对象
 *
 * @param i_VideoID   视频对象ID
 * @param i_IsAuto    是否自动播放
 * @param i_IsControl 是否允许控制视频
 * @param i_Width     宽度
 * @param i_Height    高度
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-22
 * @version     v1.0
 */
function videoInit(i_VideoID ,i_IsAuto ,i_IsControl ,i_Width ,i_Height ,i_VideoUrl ,i_VideoReloadUrl)
{
    if ( i_Width == "100%" )
    {
        i_Width = document.body.clientWidth;
    }
    if ( i_Height == "100%" )
    {
        i_Height = $(window).height();
    }
    
    $('#loadingBar').width(i_Width);
    $('#loadingBar').height(i_Height);
    
    let v_Video = videojs(i_VideoID
    ,{
        width: i_Width,
        height: i_Height,
        liveui: true,
        controlBar: {
            fullscreenToggle: true
        }
     }
    ,function onPlayerReady() 
    {
        if ( i_IsAuto == "1" )
        {
            this.play();
        }
    });
    
    v_Video.src({type: 'application/x-mpegURL', src: i_VideoUrl});
    v_Video.width(i_Width);
    v_Video.height(i_Height);
    document.getElementById(i_VideoID).style.opacity = 1;
    
    if ( i_IsAuto == "1" )
    {
        v_Video.play();
    }
}