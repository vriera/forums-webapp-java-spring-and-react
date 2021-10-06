<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>

<head>
	<meta charset="utf-8">
	<title>AskAway | Dashboard</title>
	<!-- Argon CSS -->
	<%--<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">--%>
	<link type="text/css" href="<c:url value="/resources/styles/argon-design-system.css"/>" rel="stylesheet">
	<link rel="stylesheet" href="<c:url value="/resources/styles/general.css"/>" type="text/css">


	<!--Creo que es el JS de bootstrap -->
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/js/bootstrap.bundle.min.js"
			integrity="sha384-JEW9xMcG8R+pH31jmWH6WWP0WintQrMb4s7ZOdauHnUtxwoG2vI5DkLtS3qm9Ekf"
			crossorigin="anonymous"></script>

	<!--Font Awsome -->
	<script src="https://kit.fontawesome.com/eda885758a.js" crossorigin="anonymous"></script>

	<!--Material design -->
	<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
	<link rel="icon" href="<c:url value="/resources/images/favicon.ico"/>">

</head>
<body>

<c:choose>
	<c:when test="${user == true}">
		<jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
			<jsp:param name="user_name" value="${user_name}"/>
			<jsp:param name="user_email" value="user_email"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
	</c:otherwise>
</c:choose>

<div class="wrapper">
	<div class="section section-hero section-shaped pt-3">
		<div class="shape shape-style-1 shape-default shape-skew viewheight-90">
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


		<div class="row">

			<div class="white-pill d-flex flex-column flex-shrink-0 col-3 mt-5" >
				<!-- INFORMACION DE USUARIO -->
				<div class="d-flex justify-content-center">
					<p class="h1 text-primary"><c:out value="${currentUser.username}"/></p>
				</div>
				<div class="d-flex justify-content-center">
					<p><spring:message code="emailEquals"/></p>
					<p><c:out value="${currentUser.email}"/></p>
				</div>
				<!-- DASHBOARD - OPCIONES VERTICALES -->
				<ul class="nav nav-pills flex-column mb-auto">
					<li>
						<a href="<c:url value="/dashboard/question/view?page=0"/>" class="h5 nav-link" aria-current="page">
							<i class="fas fa-question mr-3"></i>
							<spring:message code="dashboard.questions"/>
						</a>
					</li>
					<li>
						<a href="<c:url value="/dashboard/answer/view?page=0"/>" class="h5 nav-link link-dark">
							<i class="fas fa-reply mr-3"></i>
							<spring:message code="dashboard.answers"/>
						</a>
					</li>
					<li>
						<a href="<c:url value="/dashboard/community/admitted?page=0"/>" class="h5 nav-link link-dark active">
							<i class="fas fa-users mr-3"></i>
							<spring:message code="dashboard.communities"/>
						</a>
					</li>
					<li>
						<a href="<c:url value="/dashboard/community/moderated?page=0"/>" class="h5 nav-link link-dark">
							<i class="fas fa-users-cog mr-3"></i>
							<spring:message code="dashboard.Modcommunities"/>
						</a>
					</li>
				</ul>
				<hr>
			</div>


			<%--COMMUNITIES--%>
			<div class="col-6">
				<div class="white-pill mt-5">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="community.communities"/></p>
						<p class="h5 text-center"><spring:message code="community.typeMember"/></p>
						<hr>

						<c:if test="${communities.size() == 0}">
							<p class="row h1 text-gray"><spring:message code="dashboard.noQuestions"/></p>
							<div class="d-flex justify-content-center">
								<img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar">
							</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${communities}" var="community">
								<a class="d-block" href="<c:url value="/community/view/${community.id}"/>">
									<div class="card p-3 m-3 shadow-sm--hover ">
										<div class="row">
											<div class="d-flex flex-column justify-content-start ml-3">
												<div class="h2 text-primary"><c:out value="${community.name}"/></div>
											</div>
											<div class="col-12 text-wrap-ellipsis">
												<p class="h5"><c:out value="${community.description}"/></p>
											</div>
										</div>
									</div>
								</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<%--HACER PREGUNTA--%>
			<div class="col-3">
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="title.createCommunity"/></p>
						<hr>
						<p class="h5 my-3"><spring:message code="subtitle.createCommunity"/></p>
						<a class="btn btn-primary" href="<c:url value="/community/create"/>"><spring:message code="button.createCommunity"/></a>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


</body>
</html>
