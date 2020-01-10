<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Title</title>
<style>
#iframeTop {
	width: 100%;
}
#iframeLeft {
	width: 15%;
	float: left;
}
#iframeContent {
	width: 85%;
}

#iframeFooter {
	width: 100%;
}
</style>
</head>
<#include "/base.ftl">
<body>
	<iframe id="iframeTop" name="iframeTop" style="background-color: red" frameborder="0"
		src="${basePath}/top"></iframe>
	<iframe id="iframeLeft" name="iframeLeft" style="background-color: yellow;"  frameborder="0"
		src="${basePath}/left"></iframe>
	<iframe id="iframeContent" name="iframeContent" style="background-color: blue;" frameborder="0"
		src="${basePath}/mainContent"></iframe>
	<iframe id="iframeFooter" name="iframeFooter" style="background-color: green;" frameborder="0"
		src="${basePath}/footer"></iframe>
</body>
<script type="text/javascript">
$(function(){
    var height=$(window).height()//document.documentElement.clientHeight;
  	var topHeight=(height*0.05);
  	var contentHeight=(height*0.85);
    var footerHeight=(height*0.05);
    $("#iframeTop").css("height",topHeight+"px");
    $("#iframeLeft").css("height",contentHeight+"px");
    $("#iframeContent").css("height",contentHeight+"px");
    $("#iframeFooter").css("height",footerHeight+"px");
})







</script>
</html>