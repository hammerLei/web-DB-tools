<#include "/macros/page_macro_layui.ftl">
<@webpage pageCss="css/login.css" title="登录" pageJs="js/login.js">

<video class="video-player" preload="auto" autoplay="autoplay" loop="loop" style="width: 100%;height: auto">
    <source src="video/login.mp4" type="video/mp4">
    <!-- 此视频文件为支付宝所有，在此仅供样式参考，如用到商业用途，请自行更换为其他视频或图片，否则造成的任何问题使用者本人承担，谢谢 -->
</video>
<div class="video_mask"></div>
<div class="login">
    <h1>Web DB Demo</h1>
    <div class="layui-form">
        <div class="layui-form-item">
            <input class="layui-input" name="username" placeholder="用户名" id="username" lay-verify="required" type="text" autocomplete="off">
        </div>
        <div class="layui-form-item">
            <input class="layui-input" name="password" placeholder="密码" id="password" lay-verify="required" type="password" autocomplete="off">
        </div>
        <div class="layui-form-item" style="margin-bottom: 20px;">
             <a href="forget" class="layadmin-user-jump-change layadmin-link" style="margin-top: 7px; color: #a3c9d2c4">忘记密码？</a>
        </div>
    <#--<div class="layui-form-item form_code">
        <input class="layui-input" name="code" placeholder="验证码" lay-verify="required" type="text" autocomplete="off">
        <div class="code"><img src="../../images/code.jpg" width="116" height="36"></div>
    </div>-->
        <button class="layui-btn login_btn" lay-submit="" id="login">登录</button>
    </div>
</div>
<div class="copyright-item" style="
position: fixed;bottom: 2px;z-index: 99;background-color: rgba(165, 169, 169, 0.99);width: 100%;text-align: center;"><a href="http://www.beian.miit.gov.cn/" target="_blank">鄂ICP备18027189号-2</a></div>
<div hidden="true" page-id="databse-login-page"></div>
</@webpage>
