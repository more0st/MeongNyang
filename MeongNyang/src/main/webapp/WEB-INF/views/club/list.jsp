﻿<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>멍냥모임</title>
<jsp:include page="/WEB-INF/views/layout/staticHeader.jsp"/>
<style type="text/css">
* { box-sizing: border-box; }
.card-region{font-size: 13px; color: #212529; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-left:4px; margin-bottom: 4px; line-height: 1.5; display: block;}
.card-title{font-size: 20px; letter-spacing: -0.02px; color: tomato; 
			overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-bottom: 4px; 
			line-height: 1.5; font-weight: 900; display: flex; justify-content: center; width: 200px;}
.card-content{font-size:14px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis; margin-bottom: 4px; 
			line-height: 1.5; font-weight: 900; display: flex; justify-content: center; width: 200px;}
.out-div{align-content: center; justify-content: center;}
.grid{display: grid; grid-template-columns: repeat(3, minmax(200px,1fr));
					grid-template-rows: repeat(auto, minmax(230px,1fr)); grid-gap : 30px;}
.in-div{border: 2px solid black;  display: flex;
					 justify-content: center; border-radius: 20px;}
.ib{display: inline-block;}


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
</script>
</head>
<body>

<header>
	<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
</header>
	
<main>
	<div class="container body-container">
	   <div class="body-title" style="text-align: center;">
			<a href="${pageContext.request.contextPath}/club/list.do';">
				<img src="${pageContext.request.contextPath}/resource/images/clubPage.png" style="width: 250px;">
			</a>
	    </div>
	   <div style="box-shadow: 0 0 15px 0 rgb(2 59 109 / 10%);border-radius: 30px; margin: 0 auto ; width: 70%;">
	    <div class="body-main mx-auto">
			<table class="table">
				<tr >
					<td width="50%">
						${dataCount}개(${page}/${total_page} 페이지)
					</td>
					<td align="right" width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/write.do';">모임 등록</button>
					</td>
					
				</tr>
			</table>
			
			
			<!-- 
			<table class="table table-border table-list">
				<thead>
					<tr style="border-top: 2px solid #eee; border-bottom: 2px solid #eee;">
						<th class="num">번호</th>
						<th class="subject">제목</th>
						<th class="name">모임장</th>
						<th class="date">작성일</th>
						<th class="hit">인원표시</th>
						<th class="hit">조회수</th>
						<th class="hit">좋아요</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="dto" items="${list}" varStatus="status">
						<tr>
							<td>${dataCount - (page-1) * size - status.index}</td>
							<td style="text-align: center;">
								<a href="${articleUrl}&num=${dto.clubNum}">${dto.subject}</a>
							</td>
							<td>${dto.userName}</td>
							<td>${dto.reg_date}</td>
							<td>${dto.nowMember }/${dto.maxMember }</td>
							<td>${dto.hitCount}</td>
							<td>${dto.boardLikeCount }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			-->
			
			
			<table class="table" style="margin-bottom:   35px;">
				<tr >
				
					<td align="right">&nbsp;</td>
							<td align="center" >
								<form name="searchForm" action="${pageContext.request.contextPath}/club/list.do" method="post">
									<select name="condition" class="form-select">
										<option value="all"      ${condition=="all"?"selected='selected'":"" }>제목+내용</option>
										<option value="userName" ${condition=="userName"?"selected='selected'":"" }>모임장</option>
										<option value="reg_date"  ${condition=="reg_date"?"selected='selected'":"" }>등록일</option>
										<option value="subject"  ${condition=="subject"?"selected='selected'":"" }>제목</option>
										<option value="content"  ${condition=="content"?"selected='selected'":"" }>내용</option>
									</select>
									<input type="text" name="keyword" value="${keyword}" class="form-control" style="border-radius: 20px; width: 340px;">
									<button type="button" class="btn" onclick="searchList();">검색</button>
								</form>
							</td>
					
					
				</tr>
					
		
			</table>

			<div class="out-div">
				<div class="grid">
					<c:forEach var="dto" items="${list}" varStatus="status">
						<div class="in-div">
							<a href="${articleUrl}&num=${dto.clubNum}">
								<span class="ib">
									<span class="card-region">no.${dataCount - (page-1) * size - status.index} </span>
									<br><span class="card-region">등록일 | ${dto.reg_date}</span>
									<span class="card-title">${dto.subject }</span><br>
									<span class="card-content">모임장 : ${dto.userName }</span>
									<span class="card-content">인원 : ${dto.nowMember }/${dto.maxMember } </span>
									<span class="card-content">조회수 : ${dto.hitCount} </span>
									<span class="card-content">좋아요 : ${dto.boardLikeCount } </span><br>
								</span>
							</a>
						</div>
					</c:forEach>
				</div>
			</div>
			<div class="page-navigation">
				${dataCount == 0 ? "등록된 모임이 없습니다." : ""}
			</div>
			<table class="table">
						<tr>
					<td width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/list.do';" title="새로고침"><i class="fa-solid fa-arrow-rotate-right"></i></button>
					</td>
					
					<td align="right" width="100">
						<button type="button" class="btn" onclick="location.href='${pageContext.request.contextPath}/club/my.do';">내모임</button>
					</td>
					
					</tr>
			</table>
			<div class="page-navigation">
				${dataCount == 0 ? "" : paging}
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