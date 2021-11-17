<%----%><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title><spring:message code="community.title.all"/></title>


	<!-- Icons -->
	<%--    <link href="/assets/vendor/nucleo/css/nucleo-icons.css" rel="stylesheet">--%>
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!-- BLK• CSS -->
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
		  integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">

	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/blk-design-system.css"/>" rel="stylesheet">
	<link type="text/css" href="<c:url value="/resources/styles/general.css"/>" rel="stylesheet">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
			crossorigin="anonymous"></script>

<script>
	function saveSelectsAndQuery(){
		let url = new URL(window.location.href);
		let query = url.searchParams.get("query");
		let filter = url.searchParams.get("filter");
		let order = url.searchParams.get("order");
		let filterSelect = document.getElementById('filterSelect');
		let orderSelect = document.getElementById('orderSelect');
		let searchBar = document.getElementById('query');
		if( filter ) {
			filterSelect.selectedIndex = filter;
		}
		if(order) {
			orderSelect.selectedIndex = order;
		}
		if(query) {
			searchBar.value = query;
		}
	}
</script>



</head>
<body onload="saveSelectsAndQuery()">

<c:choose>
	<c:when test="${is_user_present == true}">
		<jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
			<jsp:param name="user_name" value="${user.getUsername()}"/>
			<jsp:param name="user_email" value="${user.getEmail()}"/>
			<jsp:param name="user_notifications" value="${notifications.getTotal()}"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
	</c:otherwise>
</c:choose>

<div class="wrapper">
<div class="section section-hero section-shaped">
	<div class="shape shape-style-1 shape-default shape-skew">
		<span class="span-150 square1"></span>
		<span class="span-50 square2"></span>
		<span class="span-50 square3"></span>
		<span class="span-75 square4"></span>
		<span class="span-100 square5"></span>
		<span class="span-75 square6"></span>
		<span class="span-50 square7"></span>
		<span class="span-100 square3"></span>
		<span class="span-50 square2"></span>
		<span class="span-100 square4"></span>
	</div>

	<div>
		<%--TARJETA SUPERIOR--%>
		<div class="col-6 center">
			<div class="white-pill h-75 ">
				<div class="align-items-start d-flex justify-content-center my-3">
					<p class="h1 text-primary bold"><strong><spring:message code="askAway"/></strong></p>
				</div>
				<div class="text-gray text-center mt--4 mb-2"><spring:message code="questions"/></div>

				<%--BARRA DE BÚSQUEDAS--%>
				<div class="form-group mx-5">
					<form action="<c:url value="/community/view/all"/>" method="get">
						<div class="input-group">
							<input class="form-control rounded" type="search" name="query" id="query" placeholder="<spring:message code="placeholder.searchQuestion"/>">
							<input class="btn btn-primary" type="submit" value="<spring:message code="button.search"/>">
						</div>
						<div class="container mt-3">
							<div class="row">
								<div class="col">
									<select class="form-control" name="filter" aria-label="<spring:message code="filter"/>" id="filterSelect">
										<option selected value="0"><spring:message code="filter.noFilter"/></option>
										<option value="1"><spring:message code="filter.hasAnswers"/></option>
										<option value="2"><spring:message code="filter.noAnswers"/></option>
										<option value="3"><spring:message code="filter.verifiedAnswers"/></option>
									</select>
								</div>
								<div class="col">
									<select class="form-control" name="order" aria-label="<spring:message code="order"/>" id="orderSelect">
										<option selected value="0"><spring:message code="order.mostRecent"/></option>
										<option value="1"><spring:message code="order.leastRecent"/></option>
										<option value="2"><spring:message code="order.closestMatch"/></option>
										<option value="3"><spring:message code="order.positiveQuestionVotes"/></option>
										<option value="4"><spring:message code="order.positiveAnswerVotes"/></option>
									</select>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

		<div class="row">
			<%--COMUNIDADES--%>
			<div class="col-3 ">
				<div class="white-pill mt-5 ml-3">
					<div class="card-body">

						<p class="h3 text-primary text-center"><spring:message code="community.communities"/></p>
						<hr>
						<%--Badges de las comunidades--%>
						<div class="container-fluid">
							<a class="btn btn-light badge-pill badge-lg my-3" href="<c:url value="/community/view/all"/>"><spring:message code="community.all"/></a>
							<c:forEach items="${communityList}" var="community">
								<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/community/view/${community.id}"/>"><c:out value="${community.name}"/></a>
							</c:forEach>
						</div>
					</div>
				</div>

			</div>

			<%--PREGUNTAS--%>
			<div class="col-6">
				<div class="white-pill mt-5">
					<div class="card-body">
						<div class="h2 text-primary">
							<ul class="nav nav-tabs">
								<li class="nav-item">
									<a class="nav-link active" href="#"><spring:message code="questions"/></a>
								</li>
								<li class="nav-item">
									<a class="nav-link" href="<c:url value="/community/search?query=${param.query}"/>"><spring:message code="communities"/></a>
								</li>
								<li class="nav-item">
									<a class="nav-link" href="<c:url value="/user/search?query=${param.query}"/>"><spring:message code="users"/></a>
								</li>
							</ul>
						</div>
						<c:if test="${questionList.size() == 0}">
							<p class="row h1 text-gray"><spring:message code="community.noResults"/></p>
							<div class="d-flex justify-content-center">
								<img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
							</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${questionList}" var="question">
								<a class="d-block" href="<c:url value="/question/view/${question.id}"/>">
								<div class="card p-3 m-3 shadow-sm--hover ">
									<div class="row">
										<div class="col-auto">
											<div class="d-flex align-items-center mt-2">
												<c:if test="${question.votes >=0}">
													<div class="h4 mr-2 text-success">
														<i class="fas fa-arrow-alt-circle-up"></i>
													</div>
													<p class="h5 text-success">${question.votes}</p>
												</c:if>
												<c:if test="${question.votes < 0}">
													<div class="h4 mr-2 text-warning">
														<i class="fas fa-arrow-alt-circle-down"></i>
													</div>
													<p class="h5 text-warning">${question.votes}</p>
												</c:if>
											</div>
										</div>
										<div class="col">
											<div class="d-flex flex-column justify-content-start ml-3">
												<div class="h2 text-primary"><c:out value="${question.title}"/></div>
												<p><span class="badge badge-primary badge-pill"><c:out value="${question.community.name}"/></span></p>
												<p class="h6"><spring:message code="question.askedBy"/> <c:out value="${question.owner.username}"/></p>
											</div>
											<div class="col-12 text-wrap-ellipsis">
												<p class="h5"><c:out value="${question.body}"/></p>
											</div>
											<div class="d-flex ml-3 align-items-center ">
												<div class="h4">
													<i class="fas fa-calendar"></i>
												</div>
												<p class="ml-3 h6">${question.smartDate.date}</p>
											</div>
										</div>
									</div>
								</div>
								</a>
							</c:forEach>
						</div>
					</div>
					<c:if test="${count > 0}">
						<nav aria-label="...">
							<form method="get" id="paginationForm">
								<input type="hidden" name="page" id="page" value=""/>
								<ul class="pagination">
									<c:if test="${currentPage > 1}">
										<li>
											<a class="page-link mr-2 " onclick="submit(${currentPage - 1})" tabindex="-1"><spring:message code="community.previous"/></a>
											<span class="sr-only"></span>
										</li>
									</c:if>

									<c:forEach begin="1" end="${count}" var="i">
										<li class="page-item ${currentPage == i ? "active":""}"><a class="page-link" onclick="submit(${i})"><c:out value="${i}"/></a></li>
									</c:forEach>
									<c:if test="${currentPage < count}">
										<li >
											<a class="page-link ml-1" onclick="submit(${currentPage + 1})"><spring:message code="community.next"/></a>
											<span class="sr-only"></span>
										</li>
									</c:if>
								</ul>
							</form>
						</nav>
					</c:if>
				</div>
			</div>

			<%--HACER PREGUNTA--%>
			<div class="col-3">
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="landing.question.trigger"/></p>
						<hr>
						<p class="h5 my-3"><spring:message code="landing.question.callToAction"/></p>
						<a class="btn btn-primary" href="<c:url value="/question/ask/community"/>"><spring:message code="ask"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	function submit(page){
		document.querySelector("#page").value = page;
		document.querySelector("#paginationForm").submit();
	}
</script>
</body>
</html>
