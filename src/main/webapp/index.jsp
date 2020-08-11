<html>
<body>

<br><br>
<br><br>
<br><br>
<br><br>


<a href="springmvc/testRedirect">Test Redirect</a>
<br><br>

<a href="springmvc/testView">Test View</a>
<br><br>

<%--
    模拟修改操作
    1.原始数据：1, Tom, 123456, tom@qq.com, 12
    2.密码不能修改
    3.表单回显，模拟操作直接在表单填写对应的属性值
--%>
<form action="springmvc/testModelAttribute" method="post">
    <input type="hidden" name="id" value="1">
    username: <input type="text" name="username">
    <br>
    email: <input type="text" name="email">
    <br>
    age: <input type="text" name="age" value="12">
    <input type="submit" value="Submit">
</form>
<br><br>


<a href="springmvc/testSessionAttributes">Test SessionAttributes</a>
<br><br>

<a href="springmvc/testMap">Test Map</a>
<br><br>

<a href="springmvc/testModelAndView">Test ModelAndView</a>
<br><br>

<a href="springmvc/testServletAPI">Test ServletAPI</a>
<br><br>

<form action="springmvc/testPojo" method="post">
    username: <input type="text" name="username">
    <br>
    password: <input type="password" name="password">
    <br>
    email: <input type="text" name="email">
    <br>
    city: <input type="text" name="address.city">
    <br>
    province: <input type="text" name="address.province">
    <br>
    <input type="submit" value="Submit">
</form>
<br><br>

<a href="springmvc/testCookieValue">Test CookieValue</a>
<br><br>

<a href="springmvc/testRequestHeader">Test RequestHeader</a>
<br><br>

<a href="springmvc/testRequestParam?username=zhangsan&age=15">Test RequestParam</a>
<br><br>

<form action="springmvc/testRest/1" method="post">
    <input type="hidden" name="_method" value="PUT">
    <input type="submit" value="TestRest PUT">
</form>
<br><br>

<form action="springmvc/testRest/1" method="post">
    <input type="hidden" name="_method" value="DELETE">
    <input type="submit" value="TestRest DELETE">
</form>
<br><br>

<form action="springmvc/testRest" method="post">
    <input type="submit" value="TestRest Post">
</form>
<br><br>

<a href="springmvc/testRest/1">Test Rest Get</a>
<br><br>


<a href="springmvc/testPathVariable/1">Test PathVariable</a>
<br><br>

<a href="springmvc/testAntPath/masdasd/abc">Test AntPath</a>
<br><br>

<a href="springmvc/testParamsAndHeaders?username=zhangsan&age=11">Test ParamsAndHeaders</a>
<br><br>

<form action="springmvc/testMethod" method="post">
    <input type="submit" value="sub">
</form>
<br><br>

<a href="springmvc/testMethod">Test Method</a>
<br><br>

<a href="springmvc/testRequsetMapping">Test RequsetMapping</a>
<br><br>

<a href="helloworld">HelloWorld</a>

</body>
</html>
