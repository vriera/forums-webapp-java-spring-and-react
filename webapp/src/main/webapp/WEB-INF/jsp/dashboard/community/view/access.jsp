<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>

	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta charset="utf-8">
	<title>AskAway | Access</title>


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



</head>
<body>

<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>

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
		<c:if test="${operationSuccess == true}">
			<div class="position-fixed top-0 p-3 animate" style="z-index: 11; margin-left: 75%;">
				<div id="toast" role="alert" class="toast show">
					<div class="toast-header" >
						<img src="<c:url value="/resources/images/success.png"/>" style="width: 50px; height: 50px;" class="rounded me-2" alt="...">
						<strong class="me-auto"><spring:message code="dashboard.operationSuccess"/></strong>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${operationSuccess == false}">
			<div class="position-fixed top-0 p-3 animate" style="z-index: 11; margin-left: 75%;">
				<div id="toast" role="alert" class="toast show">
					<div class="toast-header" >
						<img src="<c:url value="/resources/images/error.png"/>" style="width: 50px; height: 50px;" class="rounded me-2" alt="...">
						<strong class="me-auto"><spring:message code="dashboard.operationFailure"/></strong>
					</div>
				</div>
			</div>
		</c:if>

		<div class="row">
			<%--OTRAS COMUNIDADES MODERADAS--%>
			<div class="col-3">
				<div class="white-pill mt-5 ml-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><spring:message code="dashboard.ModcommunitiesCaps"/></p>
						<hr>
						<%--BADGES--%>
						<div class="container-fluid">
							<c:forEach items="${moderatedCommunities}" var="community">
								<a class="btn btn-outline-primary badge-pill badge-lg my-3" href="<c:url value="/dashboard/community/${community.id}/view/members"/>"><c:out value="${community.name}"/></a>
							</c:forEach>
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
									<a class="page-link" href=""<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage-1}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
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
									<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${pageNumber-1}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">${pageNumber}</a>
									</li>
								</c:forEach>

									<%--SIGUIENTE--%>
								<c:if test="${communityPage == communityPages}">
								<li class="page-item disabled">
									</c:if>
									<c:if test="${communityPage != communityPages}">
								<li class="page-item disabled">
									</c:if>
									<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage+1}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
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
							<a class="nav-link" href="/dashboard/community/${communityId}/view/members"><spring:message code="dashboard.members"/></a>
						</li>
						<li class="nav-item">
							<a class="nav-link active" href="#"><spring:message code="dashboard.access"/></a>
						</li>
					</ul>
					<%--PEDIDOS--%>
					<div class="card-body">
						<c:if test="${requested.size() != 0}">
						<div class="overflow-auto">
							<p class="h3 text-primary"><spring:message code="dashboard.pendingRequests"/></p>
							<c:forEach items="${requested}" var="member">
								<div class="card">
									<div class="d-flex flex-row justify-content-end">
										<p class="h4 card-title position-absolute start-0 ml-2"><c:out value="${member.username}"/></p>
										<a class="text-black-50 h4 mr-3" href="<c:url value="/dashboard/community/${communityId}/admitAccess/${member.id}"/>"><i class="fas fa-check-circle"></i></a>
										<a class="text-black-50 h4 mr-3" href="<c:url value="/dashboard/community/${communityId}/rejectAccess/${member.id}"/>"><i class="fas fa-times-circle"></i></a>
									</div>
								</div>
							</c:forEach>

							<%--PAGINACIÓN--%>
							<c:if test="${requestedPages > 1 }">
								<nav>
									<ul class="pagination justify-content-center">
											<%--ANTERIOR--%>
										<c:if test="${requestedPage == 0}">
										<li class="page-item disabled">
											</c:if>
											<c:if test="${requestedPage != 0}">
										<li class="page-item">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage-1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
												<i class="fa fa-angle-left"></i>
											</a>
										</li>

											<%--PÁGINAS--%>
										<c:forEach var="pageNumber" begin="1" end="${requestedPages}">
											<c:if test="${pageNumber-1 == requestedPage}">
												<li class="page-item active">
											</c:if>
											<c:if test="${pageNumber-1 != requestedPage}">
												<li class="page-item">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${pageNumber-1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">${pageNumber}</a>
											</li>
										</c:forEach>

											<%--SIGUIENTE--%>
										<c:if test="${requestedPage == requestedPages}">
										<li class="page-item disabled">
											</c:if>
											<c:if test="${requestedPage != requestedPages}">
										<li class="page-item disabled">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage+1}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage}"/>">
												<i class="fa fa-angle-right"></i>
											</a>
										</li>
									</ul>
								</nav>
							</c:if>
						</div>
						</c:if>

						<hr>

						<%--INVITED--%>
						<div class="overflow-auto">
							<c:if test="${invited.size() == 0}">
								<div class="d-flex flex-row justify-content-start">
									<p class="h3 text-gray"><spring:message code="dashboard.noPendingInvites"/></p>
									<a class="btn btn-primary position-absolute end-0 mr-6" href="<c:url value="/dashboard/community/${communityId}/invite"/>"><spring:message code="dashboard.invite"/></a>
								</div>
							</c:if>
							<c:if test="${invited.size() != 0}">
								<p class="h3 text-primary"><spring:message code="dashboard.pendingInvites"/></p>
							</c:if>
							<c:forEach items="${invited}" var="member">
								<div class="card">
									<div class="d-flex flex-row justify-content-end">
										<p class="h4 card-title position-absolute start-0 ml-2"><c:out value="${member.username}"/></p>
										<a class="text-black-50 h4 mr-3" href="<c:url value="/dashboard/community/${communityId}/invite/${member.id}"/>"><i class="fas fa-redo-alt"></i></a>
									</div>
								</div>
							</c:forEach>

							<c:if test="${invitedPages > 1}">
								<%--PAGINACIÓN--%>
								<nav>
									<ul class="pagination justify-content-center">
											<%--ANTERIOR--%>
										<c:if test="${invitedPage == 0}">
										<li class="page-item disabled">
											</c:if>
											<c:if test="${invitedPage != 0}">
										<li class="page-item">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${invitedPage-1}&rejectedPage=${rejectedPage}"/>">
												<i class="fa fa-angle-left"></i>
											</a>
										</li>

											<%--PÁGINAS--%>
										<c:forEach var="pageNumber" begin="1" end="${invitedPages}">
											<c:if test="${pageNumber-1 == invitedPage}">
												<li class="page-item active">
											</c:if>
											<c:if test="${pageNumber-1 != invitedPage}">
												<li class="page-item">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${pageNumber-1}&rejectedPage=${rejectedPage}"/>">${pageNumber}</a>
											</li>
										</c:forEach>

											<%--SIGUIENTE--%>
										<c:if test="${invitedPage == invitedPages}">
										<li class="page-item disabled">
											</c:if>
											<c:if test="${invitedPage != invitedPages}">
										<li class="page-item disabled">
											</c:if>
											<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${invitedPage+1}&rejectedPage=${rejectedPage}"/>">
												<i class="fa fa-angle-right"></i>
											</a>
										</li>
									</ul>
								</nav>
							</c:if>

						<hr>

						<%--INVITACIONES RECHAZADAS--%>
							<div class="card-body">
								<c:if test="${rejected.size() != 0}">
									<div class="overflow-auto">
										<p class="h3 text-primary"><spring:message code="dashboard.rejectedInvites"/></p>
										<c:forEach items="${rejected}" var="member">
											<div class="card">
												<div class="d-flex flex-row justify-content-end">
													<p class="h4 card-title position-absolute start-0 ml-2"><c:out value="${member.username}"/></p>
													<a class="text-black-50 h4 mr-3" href="<c:url value="/dashboard/community/${communityId}/invite/${member.id}"/>"><i class="fas fa-redo-alt"></i></a>
												</div>
											</div>
										</c:forEach>

											<%--PAGINACIÓN--%>
										<c:if test="${rejectedPages > 1 }">
											<nav>
												<ul class="pagination justify-content-center">
														<%--ANTERIOR--%>
													<c:if test="${rejectedPage == 0}">
													<li class="page-item disabled">
														</c:if>
														<c:if test="${rejectedPage != 0}">
													<li class="page-item">
														</c:if>
														<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage-1}"/>">
															<i class="fa fa-angle-left"></i>
														</a>
													</li>

														<%--PÁGINAS--%>
													<c:forEach var="pageNumber" begin="1" end="${rejectedPages}">
														<c:if test="${pageNumber-1 == rejectedPage}">
															<li class="page-item active">
														</c:if>
														<c:if test="${pageNumber-1 != rejectedPage}">
															<li class="page-item">
														</c:if>
														<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${pageNumber-1}"/>">${pageNumber}</a>
														</li>
													</c:forEach>

														<%--SIGUIENTE--%>
													<c:if test="${rejectedPage == rejectedPages}">
													<li class="page-item disabled">
														</c:if>
														<c:if test="${rejectedPage != rejectedPages}">
													<li class="page-item disabled">
														</c:if>
														<a class="page-link" href="<c:url value="/dashboard/community/${communityId}/view/access?communityPage=${communityPage}&requestedPage=${requestedPage}&invitedPage=${invitedPage}&rejectedPage=${rejectedPage+1}"/>">
															<i class="fa fa-angle-right"></i>
														</a>
													</li>
												</ul>
											</nav>
										</c:if>
									</div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>

			<%--TARJETA DERECHA--%>
			<div class="col-3">
				<%--DETALLES DE LA COMUNIDAD--%>
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center"><c:out value="${community.name}"/></p>
						<hr>
						<p class="h5 my-3"><c:out value="${community.description}"/></p>
					</div>
				</div>
				<%--INVITAR--%>
				<div class="white-pill mt-5 mr-3">
					<div class="card-body">
						<p class="h3 text-primary text-center">Invitá para hacer crecer tu comunidad</p>
						<hr>
						<p class="h5 my-3">ACÁ IRÍA EL FORM PARA INVITAR</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>


</body>
</html>