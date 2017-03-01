<%@page import="org.json.JSONObject"%>
<%@page contentType="application/json; charset=UTF-8" %>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>

<%
   
    int tiempoSimulacion = Integer.parseInt(request.getParameter("tS"));
    float porcentajeFramesCreacion = Float.parseFloat(request.getParameter("pFC"));
    simulacion.setParametrosSimulacion(tiempoSimulacion, porcentajeFramesCreacion);
    
    String capacidadesEntradaSalida[] = request.getParameter("pES").split(",");
    if (capacidadesEntradaSalida != null && request.getParameter("pES").length() > 0) {
        for (String parametroPortal : capacidadesEntradaSalida) {
            String[] parametrosStr = parametroPortal.split("-");
            int[] parametros = new int [parametrosStr.length];
            for (int i = 0; i < parametrosStr.length; i++) {
                try {
                    parametros[i] = Integer.parseInt(parametrosStr[i]);
                } catch (NumberFormatException nfe) {
                };
            }
           

            if (!simulacion.setCapacidadEntradaSalidaDePortal(parametros[0], parametros[1], parametros[2])) {
                response.sendError(500);
            }

        }
       
        out.print(simulacion.getPortalesJSON());

    }

%>