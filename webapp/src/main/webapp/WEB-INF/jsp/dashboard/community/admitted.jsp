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
	<c:when test="${is_user_present == true}">
		<jsp:include page="/WEB-INF/jsp/components/navbarLogged.jsp">
			<jsp:param name="user_name" value="${user.getUsername()}"/>
			<jsp:param name="user_email" value="${user.getEmail()}"/>
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

			<%--LEFT PANE--%>
			<div class="col-3">
				<div class="white-pill d-flex flex-column mt-5" >
					<!-- INFORMACIÓN DE USUARIO -->
					<div class="d-flex justify-content-center">
						<p class="h1 text-primary"><c:out value="${currentUser.username}"/></p>
					</div>

					<div class="d-flex justify-content-center">
						<p><spring:message code="emailEquals"/></p>
						<p><c:out value="${currentUser.email}"/></p>
					</div>
					<!-- DASHBOARD - OPCIONES VERTICALES -->
					<ul class="nav nav-pills flex-column">
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

					</ul>
				</div>
			</div>


			<%--TARJETA CENTRAL--%>
			<div class="col-6">
				<%--COMMUNITIES--%>
				<div class="white-pill mt-5">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="community.communities"/></p>
						<p class="h5 text-center"><spring:message code="community.typeMember"/></p>

						<!-- tabs -->
						<ul class="nav nav-tabs">
							<li class="nav-item">
								<a class="nav-link active" href="#"><spring:message code="dashboard.admitted"/></a>
							</li>
							<li class="nav-item">
								<a class="nav-link" href="<c:url value="/dashboard/community/moderated"/>"><spring:message code="dashboard.moderated"/></a>
							</li>
							<li class="nav-item">
								<a class="nav-link" href="<c:url value="/dashboard/community/manageAccess"/>"><spring:message code="dashboard.manageAccess"/></a>
							</li>
						</ul>


						<c:if test="${communities.size() == 0}">
						<p class="row h1 text-gray"><spring:message code="dashboard.noCommunities"/></p>
						<div class="d-flex justify-content-center">
							<img class="row w-25 h-25" src="<c:url value="/resources/images/empty.png"/>" alt="No hay nada para mostrar"/>
						</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${communities}" var="community">
								<a class="d-block" href="<c:url value="/community/view/${community.id}"/>">
									<div class="card p-3 m-3 shadow-sm--hover ">

										<div class="ml-3 d-flex" style="justify-content: space-between">
											<div>
												<p class="h2 text-primary"><c:out value="${community.name}"/></p>
											</div>
											<div>
												<c:url value="/dashboard/community/${community.id}/leaveCommunity" var="leavePostPath"/>
												<form action="${leavePostPath}" method="post">
													<button class="text-black-50 h4 mr-3" ><i class="fas fa-sign-out-alt"></i></button>
												</form>
												<c:url value="/dashboard/community/${community.id}/blockCommunity" var="blockPostPath"/>
												<form action="${blockPostPath}" method="post">
													<button class="text-black-50 h4 mr-3" ><i class="fas fa-ban"></i></button>
												</form>
											</div>
										</div>

										<div class="row ">
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


			<%--RIGHT PANE--%>
			<div class="col-3">
				<%--CREAR COMUNIDAD--%>
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
