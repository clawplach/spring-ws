<%@ include file="includeTop.jsp" %>

<div id="content">
	<div id="insert"><img src="images/webflow-logo.jpg"/></div>
	<h2>Select category</h2>
	<table>
	<tr class="readOnly">
		<td>Price:</td><td>${sale.price}</td>
	</tr>
	<tr class="readOnly">
		<td>Item count:</td><td>${sale.itemCount}</td>
	</tr>
	<form name="categoryForm" method="post">
	<spring:nestedPath path="sale">
		<tr>
			<td>Category:</td>
			<td>
				<spring:bind path="category">
					<select name="${status.expression}">
						<option value="" <c:if test="${status.value ==''}">selected</c:if>>
							None (0.02 discount rate)
						</option>
						<option value="A" <c:if test="${status.value =='A'}">selected</c:if>>
							Cat. A (0.1 discount rate when more than 100 items)
						</option>
						<option value="B" <c:if test="${status.value =='B'}">selected</c:if>>
							Cat. B (0.2 discount rate when more than 200 items)
						</option>
					</select>
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td>Is shipping required?:</td>
			<td>
				<spring:bind path="shipping"> 
					<INPUT type="hidden" name="_${status.expression}"  value="marker" /> 
					<INPUT type="checkbox" name="${status.expression}" value="true" <c:if test="${status.value}">checked</c:if>> 
				</spring:bind>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="buttonBar">
				<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}">
				<input type="submit" class="button" name="_eventId_submit" value="Next">
				<input type="submit" class="button" name="_eventId_preview" value="Preview Sale">
			</td>
		</tr>
	</spring:nestedPath>
	</form>
	</table>
</div>

<%@ include file="includeBottom.jsp" %>