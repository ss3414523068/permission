<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>

<h2>登录页面</h2>
<h3><a href="/">返回欢迎页</a></h3>

<!--************************************************************分割线************************************************************-->

<form action="/login/doLogin" method="post">
    <table border="1">
        <tr>
            <td>name<input type="text" name="name"></td>
        </tr>
        <tr>
            <td>password<input type="text" name="password"></td>
        </tr>
        <tr>
            <td><input type="submit" value="登录"></td>
        </tr>
    </table>
</form>

</body>
</html>
