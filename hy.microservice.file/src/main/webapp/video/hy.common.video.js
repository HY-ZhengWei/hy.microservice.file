/**
 * 获取视频信息
 *
 * @param i_VideoID   视频对象ID
 * @param i_IsAuto    是否自动播放
 * @param i_IsLoop    是否循环播放
 * @param i_Width     宽度
 * @param i_Height    高度
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-22
 * @version     v1.0
 */
function getVideoUrl(i_CallBackFun ,i_VideoID ,i_IsAuto ,i_IsLoop ,i_Width ,i_Height ,i_VideoUrl)
{
    var xhr = new XMLHttpRequest();
    /* 配置请求方式、请求地址以及是否同步 */
    xhr.open('POST', window.atob(i_VideoUrl), true);
    xhr.responseType = 'blob';
    /* 请求成功回调函数 */
    xhr.onload = function(e) 
    {
        if (this.status == 200) 
        {
            let v_VideoData = this.response;
            let v_VideoUrl  = URL.createObjectURL(v_VideoData);

            if ( i_CallBackFun != null )
            { 
                i_CallBackFun(i_VideoID ,i_IsAuto ,i_IsLoop ,i_Width ,i_Height ,v_VideoUrl);
            }
        }
    };
    xhr.send();
}



/**
 * 初始化视频对象
 *
 * @param i_VideoID   视频对象ID
 * @param i_IsAuto    是否自动播放
 * @param i_IsLoop    是否循环播放
 * @param i_Width     宽度
 * @param i_Height    高度
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-22
 * @version     v1.0
 */
function videoInit(i_VideoID ,i_IsAuto ,i_IsLoop ,i_Width ,i_Height ,i_VideoUrl)
{
    if ( i_Width == "100%" )
    {
        i_Width = document.body.clientWidth;
    }
    if ( i_Height == "100%" )
    {
        i_Height = $(window).height();
    }
    
    let v_Video = videojs(i_VideoID
    ,{
        width: i_Width,
        height: i_Height,
        controlBar: {
            fullscreenToggle: true
        },
        bigPlayButton: true,
        playbackRates: [0.5,1,1.25,1.5,2,3]      /* 倍速播放配置 */
     }
    ,function onPlayerReady() 
    {
        if ( i_IsAuto == "1" )
        {
            this.play();
        }
        
        this.on('ended', function() 
        {
            if ( i_IsLoop == "1" )
            { 
                this.play();
            }
        });
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