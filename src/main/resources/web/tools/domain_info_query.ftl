<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
    <legend>域名信息</legend>
</fieldset>

<table class="layui-table" lay-even="" lay-skin="row">
    <colgroup>
        <col width="30%">
        <col width="70%">
    </colgroup>
    <thead>
    <tr>
        <th>domain</th>
        <th>note</th>
    </tr>
    </thead>
    <tbody>

    <tr>
        <td>${domain}</td>
        <td <#if domainIsToChime?exists && domainIsToChime == 0> style="color: #f38b4ed6"</#if>>${note!''}</td>
    </tr>
    </tbody>
</table>

