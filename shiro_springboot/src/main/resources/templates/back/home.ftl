<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<h2>登录成功页面（需要登录认证）</h2>
<h3><a href="/login/login">返回登录页</a></h3>

<!--************************************************************分割线************************************************************-->

<table border="1">
    <tr>
        <td><a href="/doLogout">Shiro注销</a></td>
    </tr>
    <#list roleList as role>
        <tr>
            <td><a>${role.roleName}</a></td>
        </tr>
    </#list>
    <#list permissionList as permission>
        <tr>
            <td><a href="${permission.permissionUrl}">${permission.permissionName}</a></td>
        </tr>
    </#list>
</table>

</body>
</html>
