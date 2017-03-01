var URLs = {
    campoSeguimientoEnIntervalo: "http://localhost:8080/SimuladorPeatonalServidor/getCampoSeguimientoEnIntervalo.jsp",
    celulasJSON: "http://localhost:8080/SimuladorPeatonalServidor/getCelulasJSON.jsp",
    portalesJSON: "http://localhost:8080/SimuladorPeatonalServidor/getPortalesJSON.jsp",
    adjuntarPuertasAPortal: "http://localhost:8080/SimuladorPeatonalServidor/adjuntarPuertasAPortal.jsp",
    crearPortal: "http://localhost:8080/SimuladorPeatonalServidor/crearPortal.jsp",
    eliminarPuertaDePortal: "http://localhost:8080/SimuladorPeatonalServidor/eliminarPuertaDePortal.jsp",
    capacidadesEntradaSalidaPortales: "http://localhost:8080/SimuladorPeatonalServidor/setCapacidadEntradaSalidaPortales.jsp",
    simulacion: "http://localhost:8080/SimuladorPeatonalServidor/simular.jsp"
};
var Simulador = {
    mapa: null,
    capas: {base: null, celulas: null},
    controlPuertas: {controlSeleccion: null, htmlControl: null,
        crear: function(htmlControl) {
            this.htmlControl = htmlControl;
            this.controlSeleccion = new L.Draw.Rectangle(Simulador.mapa, {
                shapeOptions: {
                    clickable: false
                },
                repeatMode: true
            });
        },
        click: function() {
            if (this.controlSeleccion.enabled()) {
                this.deshabilitar();
            } else {
                this.habilitar();
            }
        },
        habilitar: function() {
            $(this.htmlControl).attr("class", "button greyActive left");
            this.controlSeleccion.enable();
        }, deshabilitar: function() {
            $(this.htmlControl).attr("class", "button grey left");
            $("#menuEdicionPuertas").css("visibility", "hidden");
            if (this.puertasSeleccionadas) {
                $.each(this.puertasSeleccionadas, function(indice, puerta) {
                    puerta.setStyle(Estilos.celulaNoSeleccionada);
                });
                this.puertasSeleccionadas = undefined;
            }
            this.controlSeleccion.disable();
        },
        modificarEstiloCelulasSeleccionadas: function(rectanguloSeleccion) {
            if (this.puertasSeleccionadas) {
                $.each(this.puertasSeleccionadas, function(indice, puerta) {
                    puerta.setStyle(Estilos.celulaNoSeleccionada);
                });
                $("#menuEdicionPuertas").css("visibility", "hidden");
            }


            this.puertasSeleccionadas = Simulador.mapa.search(rectanguloSeleccion.getBounds());
            if (this.puertasSeleccionadas.length > 0) {
                $("#menuEdicionPuertas").css("left", Simulador.mapa.latLngToContainerPoint(rectanguloSeleccion.getLatLngs()[2]).x);
                $("#menuEdicionPuertas").css("top", Simulador.mapa.latLngToContainerPoint(rectanguloSeleccion.getLatLngs()[2]).y);
                $("#menuEdicionPuertas").css("visibility", "visible");
                $.each(this.puertasSeleccionadas, function(indice, puerta) {
                    puerta.setStyle(Estilos.celulaSeleccionada);
                });
            }


        }, callbackCrearPortal: function() {
            crearPortal(getIndicesCelulas(Simulador.controlPuertas.puertasSeleccionadas), function(respuesta) {
                console.log("Indice " + respuesta["iP"]);
                Simulador.portales = respuesta["portales"];
                $.each(Simulador.controlPuertas.puertasSeleccionadas, function(indice, puerta) {
                    puerta.setStyle(Estilos.celulasPuerta);
                    puerta.setStyle(Estilos.celulaSeleccionada);
                });
            })
        },
        callbackEliminarPortal: function(respuesta) {
            Simulador.portales = respuesta["portales"];
            console.log("Puertas eliminadas");
            $.each(Simulador.controlPuertas.puertasSeleccionadas, function(indice, puerta) {
                puerta.setStyle(Estilos.celulasBasico);
                puerta.setStyle(Estilos.celulaSeleccionada);
            });
        }},
    campoSeguimientoEnIntervalo: null,
    portales: null,
    estilos: null,
    dictionarioCelulas: null,
    tiempoSimulacion: 100, //segundos
    intervalosPorSegundo: 6,
    playback: null

};
var Estilos = {
    celulasBasico: {
        radius: 2,
        fillColor: "#ff7800",
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
    },
    celulasPuerta: {
        radius: 5,
        fillColor: "#ae1500",
        color: "#000",
        weight: 1,
        opacity: 1,
        fillOpacity: 0.8
    },
    celulaSeleccionada: {
        color: "#FF0",
        weight: 3
    },
    celulaNoSeleccionada: {
        color: "#000",
        weight: 1
    }
};