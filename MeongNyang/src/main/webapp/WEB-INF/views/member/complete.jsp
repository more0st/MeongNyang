<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>멍냥마켓</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.info-continer { margin: 0 auto; padding: 70px 5px 40px; width: 500px; }
.info-continer .info-title { text-align: center; padding: 10px 0 20px; }
.info-continer .info-title > h3 { font-weight: bold; font-size:26px; color: #424951; }
.info-continer .info-box {
	text-align: center;
    border: 1px solid #eee;
    padding: 50px 20px;
    transition: all ease-in-out 0.3s;
    background: #ffffff;
    border-radius: 4px;
}
.info-continer .info-box:hover { border-color: #ffffff; box-shadow: 0px 0 25px 0 #eee; }
.info-continer .info-message { padding: 5px 10px 10px; font-size: 15px; }
.info-continer .info-footer { padding: 5px 10px; }

.btnConfirm {
	background-color:tomato; border:none;
	width: 100%; padding: 15px 0;
	font-size: 15px; color:white; font-weight: 700;  cursor: pointer; vertical-align: baseline; border-radius: 10px;
}

.btnConfirm:hover {
	background-color:#ffa393; border:none;
	width: 100%; padding: 15px 0;
	font-size: 15px; color:#eee; font-weight: 700;  cursor: pointer; vertical-align: baseline; border-radius: 10px;
}

</style>

</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
		<div class="inner-page" style="padding: 0px;">
			<div class="info-continer">

				<div class="info-box" style="border-radius: 30px; width: 500px; height: 200px;">
					<div class="info-message">
						${message} 
					</div>
					<div class="info-footer">
						<button type="button" class="btnConfirm" onclick="location.href='${pageContext.request.contextPath}/';">메인화면으로 이동</button>
					</div>
				</div>
			</div>
	    </div>
	</div>
</main>

<footer>
    <jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
</footer>

<jsp:include page="/WEB-INF/views/layout/staticFooter.jsp"/>
</body>
</html>