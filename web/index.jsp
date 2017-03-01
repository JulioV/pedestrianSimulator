<%-- 
    Document   : index
    Created on : 19-ago-2013, 19:39:26
    Author     : Julio
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="simulacion" class="Beans.SimulacionBean" scope="session"></jsp:useBean>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!--link rel="stylesheet" href="css/leaflet.css" /-->
        <link rel="stylesheet" href="css/leaflet.iconlabel.css" />
        <link rel="stylesheet" href="css/leaflet.draw.css" />
        <link rel="stylesheet" href="css/simulador.css" />

        <link rel="stylesheet" href="leafleft-playback/lib/leaflet/leaflet.css">
        <link rel="stylesheet" href="leafleft-playback/lib/bootstrap/css/bootstrap.css" />
        <link rel="stylesheet" href="leafleft-playback/lib/jquery-ui/jquery-ui.css" />
        <link rel="stylesheet" href="leafleft-playback/lib/font-awesome/css/font-awesome.css" />
        <link rel="stylesheet" href="leafleft-playback/lib/bootstrap-timepicker/bootstrap-timepicker.css" />
        <link rel="stylesheet" href="leafleft-playback/lib/awesome-markers/leaflet.awesome-markers.css" />
        <link rel="stylesheet" href="leafleft-playback/simple.css" />




        <title>Simulador</title>
    </head>
    <body>

        <div id="map"></div>
        <div id="menuFlotante">
            <input type="button" id="puertas" class="button grey left" value="Modificar Portales"/>
            <input type="button" id="configurar" class="button grey center" value="Configuracion"/>
            <input type="button" id="simular" class="button grey right" value="Simular"/>

        </div>
        <div id="menuEdicionPuertas">
            <table>
                <tr>
                    <!--td>Portal Nuevo</td><td></td><td><input type="button" id="crearPortal" class="button grey center" value="Crear"/></td-->
                    <td><input type="button" id="crearPortal" class="button grey center" value="Crear Nuevo Portal"/></td>
                </tr>
                <!--tr>
                    <td>Portal Existente</td><td></td><td><input type="button" id="adjuntarPortal" class="button grey center" value="Adjuntar"/></td>
                    
                </tr-->
                <tr>
                    <!--td>Eliminar</td><td></td><td><input type="button" id="eliminarPortal" class="button grey center" value="Eliminar"/></td-->
                    <td><input type="button" id="eliminarPortal" class="button grey center" value="Eliminar Portal"/></td>
                </tr>
            </table>            
        </div>
        <div class="containerConfiguracion" id="configuracion"  >
            <div id="opacity"></div>

            <div class="tabs">
                <section >
                    <input id="tab-1" type="radio" name="radio-set" class="tab-selector-1" checked="checked" />
                    <label for="tab-1" class="tab-label-1">Simulacion</label>

                    <input id="tab-2" type="radio" name="radio-set" class="tab-selector-2" />
                    <label for="tab-2" class="tab-label-2">Cargar</label>

                    <input id="tab-3" type="radio" name="radio-set" class="tab-selector-3" />
                    <label for="tab-3" class="tab-label-3">Guardar</label>

                    <!--input id="tab-4" type="radio" name="radio-set" class="tab-selector-4" />
                        <label for="tab-4" class="tab-label-4">Contact</label-->

                    <div class="clear-shadow"></div>

                    <div class="content">
                        <div class="content-1">
                            <p>Tiempo de Simulacion: <input type="text" name="tiempoSimulacion" id="tiempoSimulacion" size="10" value="100"></p>
                            <br>
                            <p>Porcentaje tiempo para creación de peatones: <input type="text" name="porcentajeFramesCreacion" id="porcentajeFramesCreacion" size="10" value="1"></p>
                            <br>
                            <p>Lista de portales:</p>
                            <table id="tablaPortales">
                                <thead><td>ID</td><td># Puertas</td><td>Peatones de Entrada</td><td>Peatones de Salida</td></thead>
                                <tbody id="cuerpoTablaPortales">

                                </tbody>
                            </table>
                            <br>
                            <br>

                            <input type="button" id="guardarConfiguracion" name="guardarConfiguracion" value="Guardar" class="button grey center"/>
                        </div>
                        <div class="content-2">

                        </div>
                        <div class="content-3">

                        </div>
                        <div class="content-4">

                        </div>
                    </div>
                </section>
            </div>
        </div>


        <!--script type="text/javascript" src="js/leaflet/leaflet.js"></script>
            <script type="text/javascript" src="js/leaflet/leaflet-src.js"></script-->
        <script type="text/javascript" src="js/leaflet/leaflet.gpsAnimation.js"></script>
        <script type="text/javascript" src="js/leaflet/leaflet-providers.js"></script>
        <!--script type="text/javascript" src="js/leaflet/AnimatedMarker.js"></script-->
        <script type="text/javascript" src="js/leaflet/rtree.js"></script>
        <script type="text/javascript" src="js/leaflet/leaflet.iconlabel.js"></script>
        <script type="text/javascript" src="js/leaflet/leaflet.layerindex.js"></script>

        <script type="text/javascript" src="js/leaflet/leaflet.draw-src.js"></script>

        <script type="text/javascript" src="js/jquery/jquery-2.0.3.js"></script>


        <script src="leafleft-playback/lib/jquery1.9.1.js"></script>
        <script src="leafleft-playback/lib/jquery-ui/jquery-ui.js"></script>
        <script src="leafleft-playback/lib/bootstrap/js/bootstrap.js"></script>
        <script src="leafleft-playback/lib/bootstrap-timepicker/bootstrap-timepicker.js"></script>
        <!--script src="../../lib/leaflet/leaflet-src.js"></script>

        <script src="leafleft-playback/src/Util.js"></script>
        <script src="leafleft-playback/src/MoveableMarker.js"></script>
        <script src="leafleft-playback/src/TickPoint.js"></script>
        <script src="leafleft-playback/src/Tick.js"></script>
        <script src="leafleft-playback/src/Clock.js"></script>
        <script src="leafleft-playback/src/TracksLayer.js"></script>
        <script src="leafleft-playback/src/Control.js"></script>
        <script src="leafleft-playback/src/Playback.js"></script>
        <script src="leafleft-playback/lib/awesome-markers/leaflet.awesome-markers.js"></script-->

        <script type="text/javascript" src="js/simulador/clases.js"></script>
        <script type="text/javascript" src="js/simulador/main.js"></script>
    </body>
</html>
