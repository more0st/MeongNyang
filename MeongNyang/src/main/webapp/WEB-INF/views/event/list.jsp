<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.event.EventDAO"%>
<%@page import="com.event.EventDTO"%>
<%@page import="com.member.MemberDAO"%>
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

a:hover{color: black; text-decoration: none;}

.card{width: 200px; height: 200px; float: center; margin-left: 10px; display: block; margin-bottom: 10px;}

.card-img{width: 100% ; height: 300px; border-style:none;  border-radius : 20px;  }

.card-body{margin-top: 12px;}

.card-title{font-size: 20px; letter-spacing: -0.02px; color: tomato; 
			overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-bottom: 4px; 
			line-height: 1.5; font-weight: 900; display: flex; justify-content: center; width: 200px;}

.card-price{font-size: 12px; font-weight: 500; line-height: 1.5; margin-bottom: 10px; margin-right:15px; display: flex; justify-content: right;}
.card-date{font-size: 12px; font-weight: 500; line-height: 1.5; margin-bottom: 10px; margin-right:15px; display: flex; justify-content: center;}

.card-region{font-size: 13px; color: #212529; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-left:4px; margin-bottom: 4px; line-height: 1.5; display: block;}

.card-count{color: #868e96; font-size: 13px; display: block;}

.event {
 background: white; width: 200px; height: 200px; 
 border-radius: 30px; border: 5px solid tomato;
 }
.event:hover {

box-shadow: 0 0 15px 0 rgb(2 59 109 / 30%);
border:5px solid yellow;

 }
 
 .more{
 	background: skyblue;
 	color: black;
 }
 
 .more:hover {
 	background: skyblue;
 	color : white;
 }
 .join{
 	background: #FFF136;
 	color: black;
 }
 
 .join:hover {
 	background: #FFF136;
 	color : white;
 }

 .ok{
 	background: #eee;
 	color: black;
 	disabled="disabled";
 }
 .ok:hover{
 	background: #eee;
 	color: black;
 	disabled="disabled";
 }

.card-img:hover {
    opacity:0.5;
}


.body-main {
	max-width: 700px;
}



.table-list thead > tr:first-child{ background: #ffedea; }
.table-list th, .table-list td { text-align: center; }
.table-list .left { text-align: left; padding-left: 5px; }

.table-list .num { width: 60px; color: #787878; }
.table-list .subject { color: #787878; }
.table-list .name { width: 100px; color: #787878; }
.table-list .date { width: 100px; color: #787878; }
.table-list .hit { width: 70px; color: #787878; }
</style>
<script type="text/javascript">

function searchList() {
	const f = document.searchForm;
	f.submit();
}

function join(eNum){
	if(confirm("이벤트에 참여하시겠습니까 ? ")){
		location.href="${pageContext.request.contextPath}/event/join.do?eNum="+eNum;
	}
}

function join_ok(eNum){
	if(confirm("이미 참여한 이벤트입니다. 참여를 취소하시겠습니까 ? ")){
		location.href="${pageContext.request.contextPath}/event/delParticipant.do?eNum="+eNum;
	}
}

function endEvent(){
	alert("종료된 이벤트입니다.");
}

function myEvent(){
		$(".popup-dialog").dialog({
			title:"나의 참여현황"
		});
}

</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>



	<div class="container body-container" style="width: 60%; margin-bottom: 0;">
	   <div class="body-title" style="text-align: center;">
			<a href="${pageContext.request.contextPath}/event/list.do';">
				<img src="${pageContext.request.contextPath}/resource/images/eventPage.png" style="width: 250px;">
			</a>
			 
	    </div>
	   <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto 30px ; width: 100%;">
	    <div class="body-main mx-auto">
			<table class="table">
				<tr>
					<td width="50%" >
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					<td align="right">&nbsp;</td>
				</tr>
			</table>
	<div class="container body-container" style="min-height: 100px;">

	    <div style="width: 100%; display: flex; justify-content: space-around; flex-wrap: wrap;">
	    
	    <c:forEach   var="dto" items="${list}" varStatus="status">
		   	<article class = "card">
				   		<div class="event" >
				   			<span>
						   		<a href="#">
						   			<span class="card-body" style="display: inline-block; justify-content: center;">
										<span class="card-region" > <br>No.${dataCount - (page-1) * size - status.index} </span>	
						   				<span class="card-title"> ${dto.subject} </span>
										<span style="display: flex; justify-content: center; padding-bottom: 10px;">
										<input type="hidden" name="eNum" value="${dto.eNum}">
											<button type="button" class="btn more" style="display: inline-block;" onclick="location.href='${articleUrl}&eNum=${dto.eNum}';">자세히</button>
													<button type="button" class="btn ok" style="display: inline-block;" onclick="join_ok(${dto.eNum});">완료</button>
											<c:if test="${dto.enabled!=0}">
														<button type="button" class="btn join" style="display: inline-block;" onclick="join(${dto.eNum});">참여</button>
											</c:if>
										<c:if test="${dto.enabled==0}">
													<button type="button" class="btn ok" style="display: inline-block;" onclick="endEvent();">종료</button>
										</c:if>
										</span>
										<span class="card-date"> ${dto.start_date} ~ ${dto.end_date} </span>
										<c:if test="${dto.enabled==0}">
										<span class="card-date" style="color: black; font-weight: 700; font-size: 15px;"> 종료된 이벤트입니다. </span>
										</c:if>
						   			</span>
						   		</a> 
				   			</span>
			   			</div>
		   	</article>
		 </c:forEach>

	    </div>
	</div>
			
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 이벤트가 없습니다." : paging}
			</div>
			
			<table class="table">
				<tr>
					<td width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/event/list.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					<td align="center">
						<form name="searchForm" action="${pageContext.request.contextPath}/event/list.do" method="post">
							<select name="eventStatus" class="form-select" style="width: 70px; text-align: center;">
								<option value="2"      ${eventStatus=="2"?"selected='selected'":"" }>전체</option>
								<option value="1"      ${eventStatus=="1"?"selected='selected'":"" }>진행</option>
								<option value="0" ${eventStatus=="0"?"selected='selected'":"" }>종료</option>
							</select>
							<button type="button" class="btn" onclick="searchList();">검색</button>
							<input type="hidden" name="page" value="${page}">
							<input type="hidden" name="size" value="${size}">
							<input type="hidden" name="eNum" value="${dto.eNum}">
						</form>
					</td>
					<td align="right" width="100">
						<c:if test="${sessionScope.member.userId != 'admin' && sessionScope.member.userId !=null}">
						<button type="button" class="btn" onclick="myEvent();">내참여현황</button>
						</c:if>
					</td>
					<td align="right" width="100">
						<c:if test="${sessionScope.member.userId == 'admin'}">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/event/write.do';">등록하기</button>
						</c:if>
					</td>
				</tr>
			</table>
				<div class="popup-dialog" style="display: none;">
						<p style="display: flex; justify-content: center; text-align: center;">참여한 이벤트가 없습니다.</p>
					<c:forEach var="user" items="${userList}">
							<p style="font-size: 15px;">${user.subject}</p>
					</c:forEach>
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