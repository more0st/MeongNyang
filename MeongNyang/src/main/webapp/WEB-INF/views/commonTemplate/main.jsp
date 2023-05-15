<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>:::멍냥마켓:::</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
a:hover{color: black; text-decoration: none;}

.card{width: 330px; height: 450px; float: left; margin-left: 20px; display: block;}

.card-img{width: 100% ; height: 300px; border-style:none;  border-radius : 20px;  }

.card-body{margin-top: 12px;}

.card-title{font-size: 16px; letter-spacing: -0.02px; color: #212529; 
			overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-bottom: 4px; line-height: 1.5; font-weight: 700; display: block;}

.card-price{font-size: 15px; font-weight: 700; line-height: 1.5; margin-bottom: 4px; display: block;}

.card-region{font-size: 13px; color: #212529; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-bottom: 4px; line-height: 1.5; display: block;}

.card-count{color: #868e96; font-size: 13px; display: block;}

.card-wrap{}

span a{
	color: gray;
	font-size: 12px;
}
span a:hover{
	color: tomato;
	font-size: 12px;
}

.card-img:hover {
    opacity:0.5;
}

</style>
</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	    <div class="inner-page mx-auto" style="text-align: center; font-size: 30px; margin-bottom: 15px; font-weight: 900;" >
			실시간 인기 매물
	    </div>
	    <div style="width: 100%; display: flex; justify-content: space-around; flex-wrap: wrap;">
	    
	    <c:forEach  var="n" begin="1" end="9" step="1">
	   	<article class = "card">
	   			<span>
			   		<a href="#">
			   				<img class="card-img" src= "resource/images/add_photo.png">
		   			<span class="card-body" style="display: inline-block;">
						<span class="card-region" > 서교동 </span>	
		   				<span class="card-title"> 판매합니다 </span>
		   			</span>
			   		</a> 
						<span class="card-price"> 100,000원</span>
						<a href="#">관심</a> ·
						<a href="#">쪽지</a>	
	   			</span>
	   	</article>
	   	</c:forEach>

	  
	   
	    </div>
	</div>
</main>

<footer>
	<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/></body>
</html>