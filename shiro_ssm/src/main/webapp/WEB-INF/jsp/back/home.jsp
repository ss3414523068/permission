<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
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
    <c:forEach items="${roleList}" var="role">
        <tr>
            <td><a>${role.roleName}</a></td>
        </tr>
    </c:forEach>
    <c:forEach items="${permissionList}" var="permission">
        <tr>
            <td><a href="${permission.permissionUrl}">${permission.permissionName}</a></td>
        </tr>
    </c:forEach>
</table>

</body>
</html>
