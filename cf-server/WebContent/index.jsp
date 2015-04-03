<%@page import="com.cf.model.Fx"%>
<%@page import="java.util.List"%>
<%@page import="com.cf.processor.impl.ResourceHandler"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FX</title>
</head>
<body>
<table width="59%" border="1">
	<thead>
		<tr>
			<th></th>
			<th><b>User ID</b></th>
			<th><b>Currency From</b></th>
			<th><b>Currency To</b></th>
			<th><b>Amount Buy</b></th>
			<th><b>Amount Sell</b></th>
			<th><b>Rate</b></th>
			<th><b>Time</b></th>
			<th><b>Country</b></th>
		<tr>
	</thead>
	<tbody>
    <%
        List<Fx> listFx = ResourceHandler.INSTANCE.readFromCSV();
        for(Fx fx : listFx)
        {
            %>
                <tr>
                	<td></td>
					<td><%=fx.getUserId() %></td>
					<td><%=fx.getCurrencyFrom().name() %></td>
					<td><%=fx.getCurrencyTo().name() %></td>
					<td><%=fx.getAmountBuy() %></td>
					<td><%=fx.getAmountSell() %></td>
					<td><%=fx.getRate() %></td>
					<td><%=fx.getTimePlaced() %></td>
					<td><%=fx.getOriginatingCountry() %></td>
                </tr>
            <% 
        }
    %>
    </tbody>
</table>
</body>
</html>