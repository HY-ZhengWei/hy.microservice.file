var ofile     = '';
var oTime     = -1;
var HYVideoA  = null;
var HYVideoB  = null;
var nextVideo = null;
var readyNext = false;
var readyOK   = true;





/**
 * 切换显示视频
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-26
 * @version     v1.0
 */
function changeNextVideoShow()
{
    if ( !readyOK )
    {
        showVideoLoading();
        setTimeout(changeNextVideoShow , 50);
        return;
    }  
    
    hideVideoLoading();
    readyNext = false;

    if ( nextVideo === HYVideoA )
    {
        $('#HYVideoA').show();
        $('#HYVideoB').hide();
        
        HYVideoA.play();
        HYVideoB.pause();
    }
    else
    {
        $('#HYVideoB').show();
        $('#HYVideoA').hide();
        
        HYVideoB.play();
        HYVideoA.pause();
    }
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
 * @param i_VideoObj  视频对象
 * @param i_VideoUrl  视频地址
 * 
 * @author      ZhengWei(HY)
 * @createDate  2021-06-25
 * @version     v1.0
 */
function reloadVideo(i_VideoObj ,i_VideoUrl)
{
    readyNext = true;
    readyOK   = false;
    
    var xhr = new XMLHttpRequest();
    /* 配置请求方式、请求地址以及是否同步 */
    xhr.open('POST', window.atob(i_VideoUrl) + '&of=' + ofile, true);
    xhr.setRequestHeader('Access-Control-Allow-Headers' ,'Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token ,Content-Disposition');
    xhr.responseType = 'blob';
    /* 请求成功回调函数 */
    xhr.onload = function(e) 
    {
        hideVideoLoading();
        
        if (this.status == 200) 
        {
            let v_HeaderFileName = this.getResponseHeader("Content-Disposition");
            console.log('Header' ,v_HeaderFileName);
            console.log('HYFile' ,this.getResponseHeader("HYFileName"));
            if ( v_HeaderFileName )
            {
                ofile = decodeURI(v_HeaderFileName.replace('attachment;filename=',''));
                ofile = ofile.split(".")[0];
            }
        
            let v_VideoData = this.response;
            let v_VideoUrl  = URL.createObjectURL(v_VideoData);

            i_VideoObj.src({type: 'application/x-mpegURL', src: v_VideoUrl});
            i_VideoObj.load();
            readyOK = true;
        }
    };
    xhr.send();
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
    var xhr = new XMLHttpRequest();
    /* 配置请求方式、请求地址以及是否同步 */
    xhr.open('POST', window.atob(i_VideoUrl), true);
    xhr.responseType = 'blob';
    /* 请求成功回调函数 */
    xhr.onload = function(e) 
    {
        if (this.status == 200) 
        {
            let v_HeaderFileName = this.getResponseHeader("Content-Disposition") ;
            if ( v_HeaderFileName )
            {
                ofile = decodeURI(v_HeaderFileName.replace('attachment;filename=',''));
                ofile = ofile.split(".")[0];
            }
            
            let v_VideoData = this.response;
            let v_VideoUrl  = URL.createObjectURL(v_VideoData);

            if ( i_CallBackFun != null )
            { 
                i_CallBackFun(i_VideoID ,i_IsAuto ,i_IsControl ,i_Width ,i_Height ,v_VideoUrl ,i_VideoUrl);
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
        },
        playbackRates: [0.5, 1, 1.5, 2]      /* 倍速播放配置 */
     }
    ,function onPlayerReady() 
    {
        if ( i_IsAuto == "1" )
        {
            this.play();
        }
        
        this.on('ended', function() 
        {
            setTimeout(changeNextVideoShow , 50);
        });
        
        this.on('timeupdate' ,function()
        {
            if ( readyOK && !readyNext && (this.currentTime() / this.duration()) >= 0.63 )
            {
                if ( nextVideo == null )
                {
                    nextVideo = HYVideoB;
                }
                else if ( nextVideo === HYVideoA )
                {
                    nextVideo = HYVideoB;
                }
                else 
                {
                    nextVideo = HYVideoA;
                }
                
                reloadVideo(nextVideo ,i_VideoReloadUrl);
            }
        });
        
    });
    
    if ( i_VideoID === 'HYVideoA' )
    {
        HYVideoA = v_Video;
    } 
    else
    {
        HYVideoB = v_Video;
    }
    
    v_Video.src({type: 'application/x-mpegURL', src: i_VideoUrl});
    v_Video.width(i_Width);
    v_Video.height(i_Height);
    document.getElementById(i_VideoID).style.opacity = 1;
    
    if ( i_IsAuto == "1" )
    {
        v_Video.play();
    }
}