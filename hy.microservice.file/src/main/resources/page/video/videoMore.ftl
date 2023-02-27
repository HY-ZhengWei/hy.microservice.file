<#assign ctx = request.contextPath />

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>视频播放</title>
    
    <link rel="stylesheet" href="${ctx}/video/video-js.css" />
    <link rel="stylesheet" href="${ctx}/bootstrap-4.3.1-dist/css/bootstrap.min.css" />
    
    <script type="text/javascript" charset="utf-8" src="${ctx}/jquery/jquery.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/video/video.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/video/hy.common.video.more.js"></script>
    
    <style type="text/css">
    body { 
        width: 100%;
        height: 100%;
        margin: 0px;
        padding: 0px;
        background-color: #000000;
    }
    
    #HYVideoA {
        position: absolute;
        top: 0px;
        left: 0px;
        opacity: 1;
        z-index: 99;
    }
    
    #HYVideoB {
        position: absolute;
        top: 0px;
        left: 0px;
        opacity: 1;
        z-index: 98;
    }
    
    #loadingBar {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 999;
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
    }
    
    .spinner {
      margin: 100px auto;
      width: 50px;
      height: 60px;
      text-align: center;
      font-size: 10px;
    }
     
    .spinner > div {
      height: 100%;
      width: 6px;
      display: inline-block;
       
      -webkit-animation: stretchdelay 1.2s infinite ease-in-out;
      animation: stretchdelay 1.2s infinite ease-in-out;
    }
    
    .spinner .rect1 {
        background-color: #E74C3C;
    }
     
    .spinner .rect2 {
        background-color: #A569BD;
      -webkit-animation-delay: -1.1s;
      animation-delay: -1.1s;
    }
     
    .spinner .rect3 {
        background-color: #5DADE2;
      -webkit-animation-delay: -1.0s;
      animation-delay: -1.0s;
    }
     
    .spinner .rect4 {
        background-color: #45B39D;
      -webkit-animation-delay: -0.9s;
      animation-delay: -0.9s;
    }
     
    .spinner .rect5 {
        background-color: #F5B041;
      -webkit-animation-delay: -0.8s;
      animation-delay: -0.8s;
    }
     
    @-webkit-keyframes stretchdelay {
      0%, 40%, 100% { -webkit-transform: scaleY(0.4) } 
      20% { -webkit-transform: scaleY(1.0) }
    }
     
    @keyframes stretchdelay {
      0%, 40%, 100% {
        transform: scaleY(0.4);
        -webkit-transform: scaleY(0.4);
      }  20% {
        transform: scaleY(1.0);
        -webkit-transform: scaleY(1.0);
      }
    }
    </style>
</head>
<body>
    <video id="HYVideoA" class="video-js vjs-default-skin vjs-big-play-centered" controls preload="auto" language="zh-CN"
           poster="${videoImage}" data-setup='{}' x5-video-player-type="h5-page" webkit-playsinline playsinline x5-video-player-type="h5-page" disablePictureInPicture <#if videoSizeFit == 'fill' >style="object-fit: fill"</#if>>
    </video>
    
    <video id="HYVideoB" class="video-js vjs-default-skin vjs-big-play-centered" controls preload="auto" language="zh-CN"
           poster="${videoImage}" data-setup='{}' x5-video-player-type="h5-page" webkit-playsinline playsinline x5-video-player-type="h5-page" disablePictureInPicture <#if videoSizeFit == 'fill' >style="object-fit: fill"</#if>>
    </video>
    
    <div id="loadingBar">
        <div class="spinner">
          <div class="rect1"></div>
          <div class="rect2"></div>
          <div class="rect3"></div>
          <div class="rect4"></div>
          <div class="rect5"></div>
        </div>
    </div>
    
    
    
    <script language="javascript">
       
        var v_VideoWidth   = "${videoWidth}";
        var v_VideoHeight  = "${videoHeight}";
        var v_VideoSizeFit = "${videoSizeFit}";
        var v_VideoSrc     = "${videoUrl}";
        var v_IsAuto       = "${videoAuto}";
        var v_IsControl    = "${videoControl}";
        
        
        $(document).ready(function()
        {
            hideVideoLoading();
            getVideoUrl(videoInit ,'HYVideoA' ,v_IsAuto ,v_IsControl ,v_VideoWidth ,v_VideoHeight ,v_VideoSrc);
            getVideoUrl(videoInit ,'HYVideoB' ,"0"      ,v_IsControl ,v_VideoWidth ,v_VideoHeight ,v_VideoSrc);
        });
    
    </script>
</body>
</html>