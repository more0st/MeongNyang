<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.members-form { width: 430px; padding: 45px 10px 15px; margin: 0 auto; background: #fefeff; }
.members-form .members-title { text-align: center; padding: 10px 0 20px; }
.members-form .members-title > h3 { font-weight: bold; font-size:26px; color: #424951; }
.members-form .info-box { padding: 30px 20px; box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%); border-radius: 30px;}
.members-form .row { margin-bottom: 1.5rem; }
.members-form input { display: block; width: 100%; padding: 10px 10px; border-radius: 20px;}

.members-message { margin: 0 auto; padding: 5px 5px 20px; }
.members-message p { color: #023b6d; }

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

<script type="text/javascript">


function sendOk() {
	const f = document.pwdForm;

	let str = f.userId.value;
	if(!str) {
		alert("아이디를 입력하세요. ");
		f.userId.focus();
		return;
	}

	f.action = "${pageContext.request.contextPath}/member/pwdFind_ok.do";
	f.submit();
}
</script>

</head>
<body>

<header>
    <jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>

	<div class="container body-container">
		<div class="inner-page"  style="padding-top: 10px;">
			<div class="members-form">
				<div class="members-title">
					<h3> 비밀번호 찾기 </h3>
				</div>
				<div class="info-box">
					<form name="pwdForm" method="post">
						<div class="row text-center">
							아이디를 입력해주세요.
						</div>
						<div class="row">
							<input name="userId" type="text" class="form-control" placeholder="아이디">
						</div>
						<div>
							<button type="button" class="btnConfirm" onclick="sendOk();">확인</button>
							<input type="hidden" name="mode" value="${mode}">
						</div>
					</form>
				</div>
			</div>
			<div class="members-message">
				<p class="text-center">${message}</p>
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