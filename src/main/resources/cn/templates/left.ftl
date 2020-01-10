<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Title</title>
</head>
<#include "/base.ftl">
<body>
left

<ul>
	<li><a href="#" >基础管理</a>

		<ul>
			<li><a href="${basePath}/basic/user/userPage" target="iframeContent">用户管理</a></li>
            <li><a href="${basePath}/basic/dept/deptPage" target="iframeContent">部门管理</a></li>

		</ul>

	</li>
            <li><a href="${basePath}/project/projectPage" target="iframeContent">项目管理</a></li>

    </li>
</ul>




</body>

</html>