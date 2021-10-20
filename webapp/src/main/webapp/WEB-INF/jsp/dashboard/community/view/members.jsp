<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title>AskAway | Members</title>


	<!-- Icons -->
	<link href="https://use.fontawesome.com/releases/v5.0.6/css/all.css" rel="stylesheet">

	<!--Font Awsome -->
	<script src="https://kit.fontawesome.com/eda885758a.js" crossorigin="anonymous"></script>

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
	<div class="section section-hero section-shaped">
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

		<%--Toast para cuando ejecuto una operación --%>
		<div class="position-fixed top-0 p-3 animate" style="z-index: 11; margin-left: 75%;">
			<div id="toast" role="alert" class="toast show">
				<div class="toast-header" >
					<c:if test="${operationSuccess == true}">
						<img src="<c:url value="/resources/images/success.png"/>" style="width: 50px; height: 50px;" class="rounded me-2" alt="...">
						<strong class="me-auto"><spring:message code="dashboard.operationSuccess"/></strong>
					</c:if>
					<c:if test="${operationSuccess == false}">
						<img src="<c:url value="/resources/images/error.png"/>" style="width: 50px; height: 50px;" class="rounded me-2" alt="...">
						<strong class="me-auto"><spring:message code="dashboard.operationFailure"/></strong>
					</c:if>
				</div>
			</div>
		</div>

		<div class="row">
			<%--OTRAS COMUNIDADES MODERADAS--%>
			<div class="col-3">
				<div class="white-pill mt-5 ml-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="dashboard.ModcommunitiesCaps"/></p>
						<hr>
						<%--BADGES--%>
						<div class="container-fluid">
							<c:forEach items="${moderatedCommunities}" var="com">
								<c:if test="${community.name.equals(com.name)}">
									<a class="btn btn-outline-primary badge-pill badge-lg my-3 active" href="<c:url value="/dashboard/community/${com.id}/view/members"/>"><c:out value="${com.name}"/></a>
								</c:if>
								<c:if test="${!community.name.equals(com.name)}">
									<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/dashboard/community/${com.id}/view/members"/>"><c:out value="${com.name}"/></a>
								</c:if>
							</c:forEach>
							<a class="btn btn-outline-secondary bg-secondary badge-pill badge-lg my-3" href="<c:url value="/dashboard/community/moderated"/>"><spring:message code="dashboard.backToDashboard"/></a>
						</div>
					</div>
					<c:if test="${communityPages > 1}">
					<nav>
						<ul class="pagination justify-content-center">
							<%--ANTERIOR--%>
							<c:if test="${communityPage == 0}">
							<li class="page-item disabled">
								</c:if>
								<c:if test="${communityPage != 0}">
							<li class="page-item">
								</c:if>
								<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage-1}&bannedPage=${bannedPage}"/>">
									<i class="fa fa-angle-left"></i>
								</a>
							</li>

							<%--PÁGINAS--%>
							<c:forEach var="pageNumber" begin="1" end="${communityPages}">
								<c:if test="${pageNumber-1 == communityPage}">
									<li class="page-item active">
								</c:if>
								<c:if test="${pageNumber-1 != communityPage}">
									<li class="page-item">
								</c:if>
									<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${pageNumber-1}&admittedPage=${admittedPage}&bannedPage=${bannedPage}"/>">${pageNumber}</a>
								</li>
							</c:forEach>

							<%--SIGUIENTE--%>
							<c:if test="${communityPage == communityPages}">
							<li class="page-item disabled">
								</c:if>
								<c:if test="${communityPage != communityPages}">
							<li class="page-item disabled">
								</c:if>
								<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage+1}&admittedPage=${admittedPage}&bannedPage=${bannedPage}"/>">
									<i class="fa fa-angle-right"></i>
								</a>
							</li>
						</ul>
					</nav>
					</c:if>
				</div>
			</div>

			<%--TARJETA CENTRAL--%>
			<div class="col-6">
				<div class="white-pill mt-5">
					<%--TAGS--%>
					<ul class="nav nav-tabs">
						<li class="nav-item">
							<a class="nav-link active" aria-current="page"><spring:message code="dashboard.members"/></a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="<c:url value="/dashboard/community/${communityId}/view/access"/>"><spring:message code="dashboard.access"/></a>
						</li>
					</ul>
					<%--MIEMBROS--%>
					<div class="card-body">
						<p class="h3 text-primary"><spring:message code="dashboard.members"/></p>
						<c:if test="${admitted.size() == 0}">
							<div class="d-flex flex-row justify-content-start">
								<p class="h3 text-gray"><spring:message code="dashboard.nomembers"/></p>
							</div>
						</c:if>
						<div class="overflow-auto">
							<c:forEach items="${admitted}" var="member">
							<div class="card">
								<div class="d-flex flex-row justify-content-end">
									<p class="h4 card-title position-absolute start-0 ml-2">${member.username}</p>
									<c:url value="/dashboard/community/${communityId}/kick/${member.id}" var="kickPostPath"/>
									<form action="${kickPostPath}" method="post">
										<button class="btn mb-0" >
											<div class="h4 mb-0">
												<i class="fas fa-user-minus"></i>
											</div>
										</button>
									</form>
									<c:url value="/dashboard/community/${communityId}/ban/${member.id}" var="banPostPath"/>
									<form action="${banPostPath}" method="post">
										<button class="btn mb-0" >
											<div class="h4 mb-0">
											<i class="fas fa-user-slash"></i>
											</div>
										</button>
									</form>
								</div>
							</div>
							</c:forEach>

							<%--PAGINACIÓN--%>
							<c:if test="${admittedPages > 1 }">
							<nav>
								<ul class="pagination justify-content-center">
									<%--ANTERIOR--%>
									<c:if test="${admittedPage == 0}">
										<li class="page-item disabled">
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage-1}&bannedPage=${bannedPage}"/>">
												<i class="fa fa-angle-left"></i>
											</a>
										</li>
									</c:if>
									<c:if test="${admittedPage != 0}">
										<li class="page-item">
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage-1}&bannedPage=${bannedPage}"/>">
												<i class="fa fa-angle-left"></i>
											</a>
										</li>
									</c:if>


									<%--PÁGINAS--%>
									<c:forEach var="pageNumber" begin="1" end="${admittedPages}">
										<c:if test="${pageNumber-1 == admittedPage}">
											<li class="page-item active">
												<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${pageNumber-1}&bannedPage=${bannedPage}"/>">${pageNumber}</a>
											</li>
										</c:if>
										<c:if test="${pageNumber-1 != admittedPage}">
											<li class="page-item">
												<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${pageNumber-1}&bannedPage=${bannedPage}"/>">${pageNumber}</a>
											</li>
										</c:if>

									</c:forEach>

									<%--SIGUIENTE--%>
									<c:if test="${admittedPage == admittedPages}">
									<li class="page-item disabled">
										<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage+1}&bannedPage=${bannedPage}"/>">
											<i class="fa fa-angle-right"></i>
										</a>
									</li>
									</c:if>

									<c:if test="${admittedPage != admittedPages}">
									<li class="page-item disabled">
										<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage+1}&bannedPage=${bannedPage}"/>">
											<i class="fa fa-angle-right"></i>
										</a>
									</li>
									</c:if>

								</ul>
							</nav>
							</c:if>
						</div>
						<hr>
						<c:if test="${banned.size() != 0}">
						<div class="overflow-auto">
							<p class="h3 text-primary"><spring:message code="dashboard.banned"/></p>
							<c:forEach items="${banned}" var="member">
								<div class="card">
									<div class="d-flex flex-row justify-content-end">
										<p class="h4 card-title position-absolute start-0 ml-2">${member.username}</p>
										<c:url value="/dashboard/community/${communityId}/liftBan/${member.id}" var="liftPostPath"/>
										<form action="${liftPostPath}" method="post">
											<button class="btn mb-0" >
												<div class="h4 mb-0">
												<i class="fas fa-unlock"></i>
												</div>
											</button>
										</form>
									</div>
								</c:forEach>

								<c:if test="${bannedPages > 1}">
								<%--PAGINACIÓN--%>
									<nav>
										<ul class="pagination justify-content-center">
											<%--ANTERIOR--%>
											<c:if test="${bannedPage == 0}">
												<li class="page-item disabled">
													<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${bannedPage-1}"/>">
														<i class="fa fa-angle-left"></i>
													</a>
												</li>
											</c:if>

											<c:if test="${bannedPage != 0}">
												<li class="page-item">
													<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${bannedPage-1}"/>">
														<i class="fa fa-angle-left"></i>
													</a>
												</li>
											</c:if>


											<%--PÁGINAS--%>
											<c:forEach var="pageNumber" begin="1" end="${bannedPages}">
												<c:if test="${pageNumber-1 == bannedPage}">
													<li class="page-item active">
														<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${pageNumber-1}"/>">${pageNumber}</a>
													</li>
												</c:if>
												<c:if test="${pageNumber-1 != bannedPage}">
													<li class="page-item">
														<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${pageNumber-1}"/>">${pageNumber}</a>
													</li>
												</c:if>

											</c:forEach>

											<%--SIGUIENTE--%>
											<c:if test="${bannedPage == bannedPages}">
												<li class="page-item disabled">
													<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${bannedPage+1}"/>">
														<i class="fa fa-angle-right"></i>
													</a>
												</li>
											</c:if>
											<c:if test="${bannedPage != bannedPages}">
												<li class="page-item disabled">
													<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/members?communityPage=${communityPage}&admittedPage=${admittedPage}&bannedPage=${bannedPage+1}"/>">
														<i class="fa fa-angle-right"></i>
													</a>
												</li>
											</c:if>

										</ul>
									</nav>
								</c:if>
							</div>

						</c:if>

					</div>
				</div>
			</div>

			<%--DETALLES DE LA COMUNIDAD--%>
			<div class="col-3">
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><c:out value="${community.name}"/></p>
						<hr>
						<p class="h5 my-3"><c:out value="${community.description}"/></p>
						<hr>
						<div class="d-flex justify-content-center">
							<a class="btn btn-primary" href="<c:url value="/community/view/${communityId}"/>"><spring:message code="dashboard.viewAsUser"/></a>
						</div>
					</div>
				</div>
				<%--INVITAR--%>
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="dashboard.invite"/></p>
						<hr>
						<div class="d-flex justify-content-center">
							<a class="btn btn-primary" href="<c:url value="/dashboard/community/${communityId}/invite"/>"><spring:message code="dashboard.invite"/></a>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>
</div>



</body>
</html>
