<#assign ctx = request.contextPath />

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>视频播放</title>
    
    <link href="${ctx}/video/video-js.css" rel="stylesheet">
    
    <script type="text/javascript" charset="utf-8" src="${ctx}/jquery/jquery.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/video/video.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/video/hy.common.video.js"></script>
    
    <style type="text/css">
    body { 
        width: 100%;
        height: 100%;
        margin: 0px;
        padding: 0px;
    }
    
    #myvideo {
        position: absolute;
        top: 0;
        left: 0;
        opacity: 0;
    }
    </style>
</head>
<body>
    <video id="myvideo" class="video-js vjs-default-skin vjs-big-play-centered" controls preload="auto"
           poster="${videoImage}" data-setup='{}' x5-video-player-type="h5-page">
    </video>
    
    
    
    <script language="javascript">
       
        var v_VideoWidth  = "${videoWidth}";
        var v_VideoHeight = "${videoHeight}";
        var v_VideoSrc    = "${videoUrl}";
        var v_IsAuto      = "${videoAuto}";
        var v_IsLoop      = "${videoLoopPlayback}";
        
        
        $(document).ready(function()
        {
            getVideoUrl(videoInit ,'myvideo' ,v_IsAuto ,v_IsLoop ,v_VideoWidth ,v_VideoHeight ,v_VideoSrc);
        });
    
    </script>
</body>
</html>