<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>

<style type="text/css">
.members-form { max-width: 360px; margin: 0 auto; background: #fefeff; padding: 30px 25px; box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%); }
.members-form .row { margin-bottom: 1.5rem; }
.members-form label { display: block; font-weight: 500; margin-bottom: 0.5rem; font-family: Verdana, sans-serif; }
.members-form input { display: block; width: 100%; padding: 7px 5px; }
.members-form button { padding: 8px 30px; font-size: 15px; width: 97%; }

.members-message { margin: 0 auto; padding: 20px 5px; }
.members-message p { color: #023b6d; }

.text-center { text-align: center; }

</style>

<script type="text/javascript">
function sendLogin() {
    const f = document.loginForm;

	let str = f.userId.value;
    if(!str) {
        alert("아이디를 입력하세요. ");
        f.userId.focus();
        return;
    }

    str = f.userPwd.value;
    if(!str) {
        alert("패스워드를 입력하세요. ");
        f.userPwd.focus();
        return;
    }

    f.action = "${pageContext.request.contextPath}/member/login_ok.do";
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
		<div class="body-title">
			<h2><i class="fa-solid fa-lock"></i> Members Login </h2>
		</div>
		
		<div class="body-main">
			<div style="margin: 0 -15px 50px -15px"></div>
			<form name="loginForm" method="post">
				<div class="members-form">
					<div class="row text-center">
						<i class="fa-solid fa-lock" style="font-size: 37px; color: #023b6d;"></i>
					</div>
					<div class="row">
						<label for="login-userId">Your ID</label>
						<input name="userId" type="text" class="form-control" id="login-userId" placeholder="아이디">
					</div>
					<div class="row">
						<label for="login-password">Your Password</label>
						<input name="userPwd" type="password" class="form-control" id="login-password" autocomplete="off"
							placeholder="패스워드">
					</div>
					<div class="row text-center">
						<button type="button" class="btn btn-primary" onclick="sendLogin();">Login</button>
					</div>
					<p class="text-center">
						<a href="${pageContext.request.contextPath}/member/member.do">회원가입</a> <span>|</span>
						<a href="${pageContext.request.contextPath}/">아이디 찾기</a> <span>|</span>
						<a href="${pageContext.request.contextPath}/">패스워드 찾기</a>
					</p>
				</div>
			</form>
			<div class="members-message">
				<p class="text-center">
					${message}
				</p>
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