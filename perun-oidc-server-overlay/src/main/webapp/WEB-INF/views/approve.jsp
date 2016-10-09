<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ page import="org.springframework.security.core.AuthenticationException"%>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException"%>
<%@ page import="org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="approve.title" var="title"/>
<o:header title="${title}"/>
<o:topbar pageName="Approve" />
<div class="container main">
	<% if (session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null && !(session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) instanceof UnapprovedClientAuthenticationException)) { %>
	<div class="alert-message error">
		<a href="#" class="close">&times;</a>

		<p><strong><spring:message code="approve.error.not_granted"/></strong> 
			(<%= ((AuthenticationException) session.getAttribute(AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage() %>)
		</p>
	</div>
	<% } %>
	<c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION" />

	<div class="row" >
		<div class="span8 offset2" >




			<div class="logos">
				<img class="elixir-logo" src="resources/images/ELIXIR_logo.png" />
				<c:if test="${ not empty client.logoUri }">
					<img class="arrow" src="resources/images/arrow.png" />
					<img class="service-logo" src="api/clients/${ client.id }/logo" />
				</c:if>
			</div>





			<h1><spring:message code="approve.required_for"/>&nbsp;
				<c:choose>
					<c:when test="${empty client.clientName}">
						<em style="white-space: nowrap;"><c:out value="${client.clientId}" /></em>
					</c:when>
					<c:otherwise>
						<em style="white-space: nowrap;"><c:out value="${client.clientName}" /></em>
					</c:otherwise>
				</c:choose>
			</h1>





			<form name="confirmationForm"
				action="authorize" method="post">

				<c:if test="${ client.dynamicallyRegistered }">
					<c:choose>
						<c:when test="${ gras }">
							<!-- client is "generally recognized as safe, display a more muted block -->
							<div>
							    <p class="alert alert-info">
							        <i class="icon-globe"></i>

							        <spring:message code="approve.dynamically_registered"/>

							   </p>
							</div>
						</c:when>
						<c:otherwise>
							<!-- client is dynamically registered -->
							<div class="alert alert-block <c:out value="${ count eq 0 ? 'alert-error' : 'alert-warn' }" />">
								<h4>
									<i class="icon-globe"></i> <spring:message code="approve.caution.title"/>:
								</h4>

								<p>
                                <spring:message code="approve.dynamically_registered" arguments="${ client.createdAt }"/>
                                </p>
                                <p>
								<c:choose>
                                   <c:when test="${count == 0}">
                                       <spring:message code="approve.caution.message.none" arguments="${count}"/>
                                   </c:when>
                                   <c:when test="${count == 1}">
                                       <spring:message code="approve.caution.message.singular" arguments="${count}"/>
                                   </c:when>
								   <c:otherwise>
                                       <spring:message code="approve.caution.message.plural" arguments="${count}"/>
								   </c:otherwise>
							   </c:choose>
							   </p>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>

<%--						<c:if test="${ not empty client.logoUri }">
					<ul class="thumbnails">
						<li class="span5">
							<a class="thumbnail" data-toggle="modal" data-target="#logoModal"><img src="api/clients/${ client.id }/logo" /></a>
						</li>
					</ul>
					<!-- Modal -->
					<div id="logoModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="logoModalLabel" aria-hidden="true">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
							<h3 id="logoModalLabel">
								<c:choose>
									<c:when test="${empty client.clientName}">
										<em><c:out value="${client.clientId}" /></em>
									</c:when>
									<c:otherwise>
										<em><c:out value="${client.clientName}" /></em>
									</c:otherwise>
								</c:choose>
							</h3>
						</div>
						<div class="modal-body">
							<img src="api/clients/${ client.id }/logo" />
							<c:if test="${ not empty client.clientUri }">
								<a href="<c:out value="${ client.clientUri }" />"><c:out value="${ client.clientUri }" /></a>
							</c:if>
						</div>
						<div class="modal-footer">
							<button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
						</div>
					</div>
				</c:if>--%>


<%--				<c:if test="${ (not empty client.clientDescription) || (not empty client.clientUri) || (not empty client.policyUri) || (not empty client.tosUri) || (not empty contacts) }">
					<div class="muted moreInformationContainer">
						<c:out value="${client.clientDescription}" />
						<c:if test="${ (not empty client.clientUri) || (not empty client.policyUri) || (not empty client.tosUri)  || (not empty contacts) }">
							<div id="toggleMoreInformation" style="cursor: pointer;">
								<small><i class="icon-chevron-right"></i> <spring:message code="approve.more_information"/></small>
							</div>
							<div id="moreInformation" class="hide">
								<ul>
									<c:if test="${ not empty client.clientUri }">
										<li><spring:message code="approve.home_page"/>: <a href="<c:out value="${ client.clientUri }" />"><c:out value="${ client.clientUri }" /></a></li>
									</c:if>
									<c:if test="${ not empty client.policyUri }">
										<li><spring:message code="Policy"/>: <a href="<c:out value="${ client.policyUri }" />"><c:out value="${ client.policyUri }" /></a></li>
									</c:if>
									<c:if test="${ not empty client.tosUri }">
										<li><spring:message code="approve.terms"/>: <a href="<c:out value="${ client.tosUri }" />"><c:out value="${ client.tosUri }" /></a></li>
									</c:if>
									<c:if test="${ (not empty contacts) }">
										<li><spring:message code="approve.contacts"/>: <c:out value="${ contacts }" /></li>
									</c:if>
								</ul>
							</div>
						</c:if>
					</div>
				</c:if>--%>


<%--						<c:if test="${ client.subjectType == 'PAIRWISE' }">
					<div class="alert alert-success">
						<spring:message code="approve.pairwise"/>
					</div>
				</c:if>--%>
				<c:if test="${ not empty client.clientDescription }">
					<span class="muted"><c:out value="${client.clientDescription}" /></span>
				</c:if>

				<a data-toggle="collapse" data-target="#clientinfo" class="more-info-btn">
					<spring:message code="approve.more_information"/>
					<i class="icon-chevron-down"></i>
				</a>

				<div id="clientinfo" class="collapse clientinfo">

					<c:if test="${ (not empty client.clientUri) || (not empty client.policyUri) || (not empty client.tosUri) || (not empty contacts) }">
						<ul>
							<c:if test="${ not empty client.clientUri }">
								<li><spring:message code="approve.home_page"/>: <a href="<c:out value="${ client.clientUri }" />"><c:out value="${ client.clientUri }" /></a></li>
							</c:if>
							<c:if test="${ not empty client.policyUri }">
								<li><spring:message code="Policy"/>: <a href="<c:out value="${ client.policyUri }" />"><c:out value="${ client.policyUri }" /></a></li>
							</c:if>
							<c:if test="${ not empty client.tosUri }">
								<li><spring:message code="approve.terms"/>: <a href="<c:out value="${ client.tosUri }" />"><c:out value="${ client.tosUri }" /></a></li>
							</c:if>
							<c:if test="${ (not empty contacts) }">
								<li><spring:message code="approve.contacts"/>: <c:out value="${ contacts }" /></li>
							</c:if>
						</ul>
					</c:if>

					<div>
						<c:choose>
							<c:when test="${ empty client.redirectUris }">
								<div class="alert alert-block alert-error">
									<h4>
										<i class="icon-info-sign"></i> <spring:message code="approve.warning"/>:
									</h4>
									<spring:message code="approve.no_redirect_uri"/>
									<spring:message code="approve.redirect_uri" arguments="${redirect_uri}"/>
								</div>
							</c:when>
							<c:otherwise>
								<spring:message code="approve.redirect_uri" arguments="${redirect_uri}" />
							</c:otherwise>
						</c:choose>
					</div>

				</div>





				<h3 class="approve-scopes"><spring:message code="approve.access_to"/></h3>

				<c:if test="${ empty client.scope }">
						<div class="alert alert-block alert-error">
							<h4>
								<i class="icon-info-sign"></i> <spring:message code="approve.warning"/>:
							</h4>
							<p>
							   <spring:message code="approve.no_scopes"/>
							</p>
						</div>
				</c:if>




				<fieldset class="scopes">
					<c:forEach var="scope" items="${ scopes }">

						<label for="scope_${ fn:escapeXml(scope.value) }" class="scope checkbox">

							<input type="checkbox" name="scope_${ fn:escapeXml(scope.value) }" id="scope_${ fn:escapeXml(scope.value) }" value="${ fn:escapeXml(scope.value) }" checked="checked">

							<c:if test="${ not empty scope.icon }">
								<i class="icon-${ fn:escapeXml(scope.icon) }"></i>
							</c:if>
							<c:choose>
								<c:when test="${ not empty scope.description }">
									<c:out value="${ scope.description }" />
								</c:when>
								<c:otherwise>
									<c:out value="${ scope.value }" />
								</c:otherwise>
							</c:choose>


							<c:if test="${ not empty claims[scope.value] }">
								<ul class="claims">
								<c:forEach var="claim" items="${ claims[scope.value] }">

									<c:choose>
										<c:when test="${ fn:length(claims[scope.value]) == 1}">
											<li>
												<i><c:out value="${ claim.value }" /></i>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<c:out value="${ claim.key }" />:
												<i><c:out value="${ claim.value }" /></i>
											</li>
										</c:otherwise>
									</c:choose>

								</c:forEach>
								</ul>
							</c:if>

							<%--<c:if test="${ scope.structured }">
								<input name="scopeparam_${ fn:escapeXml(scope.value) }" type="text" value="${ fn:escapeXml(scope.structuredValue) }" placeholder="${ fn:escapeXml(scope.structuredParamDescription) }">
							</c:if>--%>
						</label>

					</c:forEach>
				</fieldset>





				<h3>
						<spring:message code="approve.do_authorize"/>
						"<c:choose>
							<c:when test="${empty client.clientName}">
								<c:out value="${client.clientId}" />
							</c:when>
							<c:otherwise>
								<c:out value="${client.clientName}" />
							</c:otherwise>
						</c:choose>"?
				</h3>
				<input type="hidden" id="user_oauth_approval" name="user_oauth_approval" value="true" />
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

				<div class="row answer-btns">
					<div class="span4">

						<spring:message code="approve.label.authorize" var="authorize_label"/>
						<input name="authorize" value="${authorize_label}" type="submit"
						       onclick="$('#user_oauth_approval').attr('value',true)"
						       class="btn btn-success btn-large btn-block" />
					</div>
					<div class="span4">

						<spring:message code="approve.label.deny" var="deny_label"/>
						<input name="deny" value="${deny_label}" type="submit"
						       onclick="$('#user_oauth_approval').attr('value',false)"
						       class="btn btn-danger btn-large btn-block" />
					</div>
				</div>




				<fieldset style="text-align: left" class="remember">
					<label for="remember-forever" class="radio">
						<input type="radio" name="remember" id="remember-forever" value="until-revoked"  ${ !consent ? 'checked="checked"' : '' }>
						<spring:message code="approve.remember.until_revoke"/>
						<small>(<a href="${config.issuer}">${config.issuer}</a>)</small>
					</label>
					<label for="remember-hour" class="radio">
						<input type="radio" name="remember" id="remember-hour" value="one-hour">
						<spring:message code="approve.remember.one_hour"/>
					</label>
					<label for="remember-not" class="radio">
						<input type="radio" name="remember" id="remember-not" value="none" ${ consent ? 'checked="checked"' : '' }>
						<spring:message code="approve.remember.next_time"/>
					</label>
				</fieldset>


			</form>

		</div>
	</div>

</div>
<script type="text/javascript">
<!--

$(document).ready(function() {
		$('.claim-tooltip').popover();
		$('.claim-tooltip').on('click', function(e) {
			e.preventDefault();
			$(this).popover('show');
		});

		$(document).on('click', '#toggleMoreInformation', function(event) {
			event.preventDefault();
			if ($('#moreInformation').is(':visible')) {
				// hide it
				$('.moreInformationContainer', this.el).removeClass('alert').removeClass('alert-info').addClass('muted');
				$('#moreInformation').hide('fast');
				$('#toggleMoreInformation i').attr('class', 'icon-chevron-right');
			} else {
				// show it
				$('.moreInformationContainer', this.el).addClass('alert').addClass('alert-info').removeClass('muted');
				$('#moreInformation').show('fast');
				$('#toggleMoreInformation i').attr('class', 'icon-chevron-down');
			}
		});
		
    	var creationDate = "<c:out value="${ client.createdAt }" />";
		var displayCreationDate = $.t('approve.dynamically-registered-unkown');
		var hoverCreationDate = "";
		if (creationDate != null && moment(creationDate).isValid()) {
			creationDate = moment(creationDate);
			if (moment().diff(creationDate, 'months') < 6) {
				displayCreationDate = creationDate.fromNow();
			} else {
				displayCreationDate = "on " + creationDate.format("LL");
			}
			hoverCreationDate = creationDate.format("LLL");
		}
		
		$('#registrationTime').html(displayCreationDate);
		$('#registrationTime').attr('title', hoverCreationDate);

		
		
});

//-->
</script>
<o:footer/>