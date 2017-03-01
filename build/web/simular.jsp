<%@page contentType="application/json; charset=UTF-8" %>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>

<%
    int tiempoSimulacion = Integer.parseInt(request.getParameter("tS"));
    float porcentajeFramesCreacion = Float.parseFloat(request.getParameter("pFC"));
    simulacion.setParametrosSimulacion(tiempoSimulacion, porcentajeFramesCreacion);
    out.print(simulacion.simular());
%>