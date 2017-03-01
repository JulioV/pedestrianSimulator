<%@page import="org.json.JSONObject"%>
<%@page contentType="application/json; charset=UTF-8" %>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>

<%

    String indicesCelulasStr[] = request.getParameter("iCs").split(",");
    if(indicesCelulasStr != null &&  request.getParameter("iCs").length() > 0){
        int[] indicesCelulas = new int[indicesCelulasStr.length];

    for (int i = 0; i < indicesCelulas.length; i++) {
        try {
            indicesCelulas[i] = Integer.parseInt(indicesCelulasStr[i]);
        } catch (NumberFormatException nfe) {
        };
    }
    JSONObject respuesta = new JSONObject();
    respuesta.put("iP", simulacion.crearPortal(indicesCelulas));
    respuesta.put("portales", simulacion.getPortalesJSON());
    out.print(respuesta);
    }
    
%>