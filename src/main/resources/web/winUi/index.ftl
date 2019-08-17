<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8"/>
    <meta name="renderer" content="webkit">
    <title>WinAdmin - Win10风格管理系统</title>
    <link href="/lib/layui/css/layui.css" rel="stylesheet"/>
    <link href="/lib/animate/animate.min.css" rel="stylesheet"/>
    <link href="/lib/font-awesome-4.7.0/css/font-awesome.css" rel="stylesheet"/>
    <link href="/lib/winui/css/winui.css" rel="stylesheet"/>
    <script type="text/javascript" charset="utf-8" async="" src="https://l2dwidget.js.org/lib/L2Dwidget.0.min.js"></script>

    <style>
        body {
            /*在页面顶部加载背景最佳，如有必要这块可以从数据库读取*/
            background-image: url(/images/bg_01.jpg);
        }
        canvas#live2dcanvas {
            border: 0 !important;
            right 0: ;
        }
    </style>
    <style>
        .live2d-widget-dialog-container {
            width: 300px;
            height: 120px;
            position: absolute;
            bottom: 65%;
            right: 0px;
            transform-origin: right;
            padding: 12px;
            box-sizing: border-box;
            -webkit-font-smoothing: antialiased;
        }
        .live2d-widget-dialog {
            width: 100%;
            height: 100%;
            color: #917159;
            font-size: 16px;
            padding: 12px;
            border: 2px solid rgb(236, 203, 180);
            background: rgb(252, 248, 244);
            box-sizing: border-box;
            border-radius: 10px;
            transform: rotate(-2deg);
            opacity: 0;
            transition: 200ms opacity;
            box-shadow: rgba(0, 0, 0, 0.12) 0px 1px 6px, rgba(0, 0, 0, 0.12) 0px 1px 4px;
            animation: live2d-widget-dialog-tingle 4s ease-in-out 0s infinite alternate;
        }
        @keyframes live2d-widget-dialog-tingle {
            0% { transform: translate(-1px, 1.5px) rotate(-2deg); }
            100% { transform: translate(1px, -1.5px) rotate(2deg); }
        }
    </style>
</head>
<body>

<script src="https://l2dwidget.js.org/lib/L2Dwidget.min.js"></script>
<script type="text/javascript">
    L2Dwidget
        .on('*', (name) => {
        console.log('%c EVENT ' + '%c -> ' + name, 'background: #222; color: yellow', 'background: #fff; color: #000')
    })
    .init({
        dialog: {
            // 开启对话框
            enable: true,
            script: {
                // 每空闲 10 秒钟，显示一条一言
                'every idle 10s': '$hitokoto$',
                // 当触摸到星星图案
                // 'hover .star': '星星在天上而你在我心里 (*/ω＼*)',
                // 当触摸到角色身体
                'tap body': '哎呀！别碰我！',
                // 当触摸到角色头部
                'tap face': '人家已经不是小孩子了！'
            }
        }
    });
</script>


<!-- 桌面 -->
<div class="winui-desktop">

</div>

<!-- 开始菜单 -->
<div class="winui-start sp layui-hide">
    <!-- 左边设置 -->
    <div class="winui-start-left">
        <div class="winui-start-item bottom user-center" data-text="个人中心" win-url="/update_password" win-title="个人中心"
             win-id="0" win-opentype="2"><i class="fa fa-user"></i></div>
        <div class="winui-start-item winui-start-individuation bottom theme-setting" data-text="主题设置"><i
                class="fa fa-cog"></i></div>
        <div class="winui-start-item bottom logout" data-text="注销登录"><i class="fa fa-power-off"></i></div>
    </div>
    <!-- 中间导航 -->
    <div class="winui-start-center">
        <div class="layui-side-scroll">
            <ul class="winui-menu layui-nav layui-nav-tree" lay-filter="winuimenu"></ul>
        </div>
    </div>
    <!-- 右边磁贴 -->
    <div class="winui-start-right">
        <div class="layui-side-scroll">
            <div class="winui-nav-tile">
                <div class="winui-tilebox">
                    <div class="winui-tilebox-head">快捷通道</div>
                    <div class="winui-tilebox-body">
                        <div class="winui-tile winui-tile-normal" win-title="分配给我的jira未解决的问题" win-opentype="3"
                             win-url="">
                            <i class="fa fa-fw fa-adjust"></i>
                            <span>分配给我的未解决的jira问题</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-url="" win-title="OA"
                             win-opentype="3">
                            <i class="fa fa-fw fa-circle-o-notch"></i>
                            <span>OA</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-url=""
                             win-title="Site Cms 后台" win-opentype="3">
                            <i class="fa fa-fw fa-list-alt"></i>
                            <span>Site Cms管理后台</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-url="https://mail.qq.com/"
                             win-title="email" win-opentype="3">
                            <i class="fa fa-fw fa-window-maximize"></i>
                            <span>邮箱</span>
                        </div>
                        <div class="winui-tile winui-tile-long" win-url="" win-title="Chime 官网"
                             win-opentype="3">
                            <p style="font-size:30px;font-family:'STKaiti';">Tab</p>
                            <span>Chime官网</span>
                        </div>
                        <div class="winui-tile winui-tile-long">
                            <i class="fa fa-fw fa-spin fa-spinner"></i>
                            <span>敬请期待</span>
                        </div>
                        <div class="winui-tile winui-tile-normal">
                            <i class="fa fa-fw fa-spin fa-refresh"></i>
                            <span>敬请期</span>
                        </div>
                        <div class="winui-tile winui-tile-normal">
                            <i class="fa fa-fw fa-calendar"></i>
                            <span>敬请</span>
                        </div>
                        <div class="winui-tile winui-tile-long">
                            <i class="fa fa-fw fa-clock-o"></i>
                            <span>敬</span>
                        </div>
                    </div>
                </div>
                <div class="winui-tilebox">
                    <div class="winui-tilebox-head">工具集</div>
                    <div class="winui-tilebox-body">
                        <div class="winui-tile winui-tile-long" win-id="-1" win-url="/domainInfo"
                             win-title="域名信息查询" win-opentype="2">
                            <i class="fa fa-file-text"></i>
                            <span>domain域名信息查询</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-id="-2" win-url="/jsonFormart"
                             win-title="Json 格式化" win-opentype="2">
                            <i class="fa fa-file-text"></i>
                            <span>Json 格式化</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-id="-3" win-url="/regular"
                             win-title="正则表达式测试" win-opentype="2">
                            <img src="/images/logo_100.png"/>
                            <span>正则表达式测试</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-id="-4" win-url="/freemarker"
                             win-title="freemarker解析测试" win-opentype="2">
                            <i class="fa fa-file-text"></i>
                            <span>freemarker解析测试</span>
                        </div>
                        <div class="winui-tile winui-tile-normal" win-id="-5" win-url="/"
                             win-title="文件上传" win-opentype="2">
                            <img src="/images/qzone_32.png"/>
                            <span>文件上传</span>
                        </div>
                    </div>
                </div>
            <#--<div class="winui-tilebox">-->
            <#--<div class="winui-tilebox-head">占位菜单</div>-->
            <#--<div class="winui-tilebox-body">-->
            <#--<div class="winui-tile winui-tile-normal">-->
            <#--<i class="fa fa-file-text"></i>-->
            <#--<span>文章管理</span>-->
            <#--</div>-->
            <#--<div class="winui-tile winui-tile-normal">-->
            <#--<i class="fa fa-file-text"></i>-->
            <#--<span>文章管理</span>-->
            <#--</div>-->
            <#--<div class="winui-tile winui-tile-normal">-->
            <#--<i class="fa fa-file-text"></i>-->
            <#--<span>文章管理</span>-->
            <#--</div>-->
            <#--</div>-->
            <#--</div>-->
            <#--<div class="winui-tilebox">-->
            <#--<div class="winui-tilebox-head">占位菜单</div>-->
            <#--<div class="winui-tilebox-body">-->
            <#--<div class="winui-tile winui-tile-long">-->
            <#--<i class="fa fa-file-text"></i>-->
            <#--<span>文章管理</span>-->
            <#--</div>-->
            <#--</div>-->
            <#--</div>-->
            </div>
        </div>
    </div>
</div>

<!-- 任务栏 -->
<div class="winui-taskbar">
    <!-- 开始菜单触发按钮 -->
    <div class="winui-taskbar-start sp"><i class="fa fa-windows"></i></div>
    <!-- 任务项 -->
    <ul class="winui-taskbar-task"></ul>
    <!-- 任务栏时间 -->
    <div class="winui-taskbar-time"></div>
    <!-- 控制中心 -->
    <div class="winui-taskbar-console sp">
        <i class="fa fa-comment-o"></i>
    </div>
    <!-- 显示桌面 -->
    <div class="winui-taskbar-desktop">
    </div>
</div>

<!--控制中心-->
<div class="winui-console layui-hide slideOutRight sp">
    <h1>最新通知</h1>
    <div class="winui-message" style="bottom:20px !important;">
        <div class="layui-side-scroll">
            <div class="winui-message-item">
                <h2>系统消息</h2>
                <div class="content">
                    全新改版、王者归来！
                </div>
            </div>
            <div class="winui-message-item">
                <h2>万水千山总是情!</h2>
                <div class="content">
                    <a target="_blank" class="layui-btn layui-btn-primary " style="font-size: 15px;" href="https://github.com/hammerLei/web-DB-tools">点个<i class="fa fa-star-o" ></i> star行不行！</a>
                </div>
            </div>
            <div class="winui-message-item">
                <h2>加我QQ</h2>
                <div class="content">
                    <a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=463367527&site=qq&menu=yes"><img border="0"
                                                                                                              src="http://wpa.qq.com/pa?p=2:463367527:41"
                                                                                                              alt="点击这里给我发消息"
                                                                                                              title="点击这里给我发消息"></a>
                </div>

            </div>
            <div class="winui-message-item">
                <h2>扫一扫加我微信</h2>
                <div class="content">
                    <img width="80%" style="padding: 0" border="0" src="/images/weixin.png" alt="我的微信"
                         title="我微信">
                </div>
            <#--<div class="content">-->
            <#--<a target="_blank"-->
            <#--href="//shang.qq.com/wpa/qunwpa?idkey=2553e50e446e595eacf83789876b4ae93e619c41dbe69bed54dadcb21d9b3185"><img-->
            <#--border="0" src="//pub.idqqimg.com/wpa/images/group.png" alt="Chime 交流群1" title="1Chime 交流群"></a>-->
            <#--</div>-->
            <#--<h2>Chime 交流群(Mac下使用手机QQ扫描二维码)</h2>-->
            <#--<div class="content">-->
            <#--<img width="80%" style="padding: 0" border="0" src="/images/1538273771405.png" alt="Chime 交流群"-->
            <#--title="Chime 交流群">-->
            <#--</div>-->
            </div>
        </div>
    </div>
<#--<div class="winui-shortcut">-->
<#--<h2><span class="extend-switch sp">展开</span></h2>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--<div class="winui-shortcut-item">-->
<#--<i class="fa fa-cog"></i>-->
<#--<span>设置</span>-->
<#--</div>-->
<#--</div>-->
</div>

<!--layui.js-->
<script src="/lib/layui/layui.js"></script>
<script>
    layui.config({
        base: '/js/' //指定 index.js 路径
        , version: '1.0.0-betaindexftl'
    }).use('index');
</script>
<#include "/base/tj.ftl">
</body>
</html>