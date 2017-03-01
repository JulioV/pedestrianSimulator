function inicializar() {

    //Simulador.mapa = L.map('map').setView([19.446718768712394, -99.15268495521133], 24);
    Simulador.mapa = new L.map('map')
    //map = Simulador.mapa
    Simulador.mapa.setView([0, 0], 6);
    L.Map.include(L.LayerIndexMixin);

    //L.tileLayer.provider('OpenStreetMap.BlackAndWhite',{maxZoom:25}).addTo(Simulador.mapa);
    //L.tileLayer('http://{s}.tile.cloudmade.com/{key}/{styleId}/256/{z}/{x}/{y}.png', {key: 'f848abd318664f45bc13ffb27d5be2b8', styleId: 105812,maxZoom:25}).addTo(Simulador.mapa);
    cargarControlPuertas();
    crearCapaCelulas();
    cargarPortales();
    crearBindingsEventos();
}

function crearCapaCelulas() {
    $.ajax({
        url: URLs.celulasJSON,
        type: "POST",
        dataType: "json",
        cache: false
    }).done(function(json) {
        Simulador.capas.celulas = new L.GeoJSON(json, {
            pointToLayer: function(feature, latlng) {
                if (feature.properties.p !== undefined) {
                    return L.circleMarker(latlng, Estilos.celulasPuerta);
                } else {
                    return L.circleMarker(latlng, Estilos.celulasBasico);
                }


            },
            onEachFeature: function(feature, layer) {
                Simulador.mapa.indexLayer(layer);
            }
        }).addTo(Simulador.mapa);


        crearDiccionarioCelulas();
    });
}
function cargarPortales() {
    $.ajax({
        url: URLs.portalesJSON,
        type: "POST",
        dataType: "json",
        cache: false
    }).done(function(json) {
        Simulador.portales = json;
    });
}

function cargarControlPuertas() {
    Simulador.controlPuertas.crear('#puertas');
    Simulador.mapa.on('draw:created', function(e) {
        if (e.layerType === 'rectangle') {
            Simulador.controlPuertas.modificarEstiloCelulasSeleccionadas(e.layer);
        }
    });

}
function crearCapaCampoSeguimientoEnIntervalo(intervalo) {
    $.ajax({
        url: URLs.campoSeguimientoEnIntervalo,
        type: "POST",
        data: {i: intervalo++},
        dataType: "json",
        cache: false
    }).done(function(json) {
        Simulador.capas.campoSeguimientoEnIntervalo = L.geoJson(json, {
            pointToLayer: function(feature, latlng) {
                if (feature.properties.v > 0) {
                    return new L.Marker(latlng, {icon: new L.Icon.Label.Default({labelText: feature.properties.v + " " + feature.properties.i})});
                } else {
                    return  L.circleMarker(latlng);
                }


            },
            onEachFeature: function(feature, layer) {
                //layer.bindPopup("hola" + feature.properties.v);

            }
        }).addTo(Simulador.mapa);
        crearDiccionarioCelulas();
    });
}
//Se crea un diccionario para facilitar las busquedas a la hora de animar y modificar atributos del terreno
function crearDiccionarioCelulas() {
    Simulador.dictionarioCelulas = {};
    Simulador.capas.celulas.eachLayer(function(layer) {
        Simulador.dictionarioCelulas[layer.feature.properties.i] = layer._leaflet_id;
    });
}
function crearBindingsEventos() {
    $('#simular').on({
        click: function() {
            Simulador.controlPuertas.deshabilitar();
            $.ajax({
                url: URLs.simulacion,
                type: "POST",
                data: {tS: Simulador.tiempoSimulacion, pFC:Simulador.porcentajeFramesCreacion},
                dataType: "json",
                cache: false
            }).done(function(tracks) {
                if (Simulador.playback === null)
                    Simulador.playback = new L.Playback(Simulador.mapa, tracks);
                else {
                    jQuery.each(tracks, function(i, track) {
                        Simulador.playback.addTracks(track);
                    });
                    $('#play-pause').click()

                }
                $('#play-pause').click()
                $('#time-slider').slider('value', 0);
            });
        }
    });



    $('#puertas').on({
        click: function() {

            /*
             if (Simulador.capas.campoSeguimientoEnIntervalo !== undefined)
             //Simulador.mapa.removeLayer(Simulador.capas.campoSeguimientoEnIntervalo);
             Simulador.capas.campoSeguimientoEnIntervalo.clearLayers()
             crearCapaCampoSeguimientoEnIntervalo(intervalo++);*/
            Simulador.controlPuertas.click();


        }
    });

    $('#configurar').on({
        click: function() {
            Simulador.controlPuertas.deshabilitar();
            $("#configuracion").css("visibility", "visible");
            cargarPortales();
            $("#cuerpoTablaPortales").html("");
            $.each(Simulador.portales.portales, function(indice, portal) {
                $("#cuerpoTablaPortales").append("<tr><td>" + indice + "</td><td>" + portal["p"] + "</td><td><input type='text' class='peatonesEntrada' id='" + indice + "' value='" + portal["pe"] + "'/></td><td><input type='text' class='peatonesSalida' id='" + indice + "' value='" + portal["ps"] + "'/></td></tr>");
            });
        }
    });

    $('#guardarConfiguracion').on({
        click: function() {
            Simulador.peatonesEntrada = [];
            Simulador.peatonesSalida = [];
            Simulador.capacidadesEntradaSalidaPortales = [];
            Simulador.tiempoSimulacion = $("#tiempoSimulacion").val();
            Simulador.porcentajeFramesCreacion = $("#porcentajeFramesCreacion").val();
            
            $.each($(".peatonesEntrada"), function(indice, peatonesEntrada) {
                console.log($(peatonesEntrada).attr("id"));
                Simulador.peatonesEntrada[$(peatonesEntrada).attr("id")] = $(peatonesEntrada).val();
            });

            $.each($(".peatonesSalida"), function(indice, peatonesSalida) {
                Simulador.peatonesSalida[$(peatonesSalida).attr("id")] = $(peatonesSalida).val();
            });

            $.each(Simulador.portales.portales, function(indice, portal) {
                Simulador.capacidadesEntradaSalidaPortales.push(indice + "-" + Simulador.peatonesEntrada[indice] + "-" + Simulador.peatonesSalida[indice]);
            });

            $.ajax({
                url: URLs.capacidadesEntradaSalidaPortales,
                type: "POST",
                data: {pES: Simulador.capacidadesEntradaSalidaPortales.join(","), tS: Simulador.tiempoSimulacion, pFC: Simulador.porcentajeFramesCreacion},
                dataType: "json",
                cache: false
            }).done(function(json) {
                Simulador.portales = json;
                alert("Configuracion Guardada");

            }).error(function(jqXHR, exception) {
                if (jqXHR.status === 500) {
                    alert('Internal Server Error [500].');
                } else {
                    alert('Uncaught Error.\n' + jqXHR.responseText);
                }
            });

        }
    });

    $('#opacity').on({
        click: function() {

            $("#configuracion").css("visibility", "hidden");
        }
    });

    $('#crearPortal').on({
        click: function() {
            eliminarCelulasDePortal(getCelulasEnOtroPortal(Simulador.controlPuertas.puertasSeleccionadas), function() {
                Simulador.controlPuertas.callbackCrearPortal();

            });


        }
    });

    $('#adjuntarPortal').on({
        click: function() {
            eliminarCelulasDePortal(getCelulasEnOtroPortal(Simulador.controlPuertas.puertasSeleccionadas), function() {

            });
        }
    });

    $('#eliminarPortal').on({
        click: function() {
            eliminarCelulasDePortal(getCelulasEnOtroPortal(Simulador.controlPuertas.puertasSeleccionadas), function(respuesta) {
                Simulador.controlPuertas.callbackEliminarPortal(respuesta);
            });
        }
    });
}
function crearPortal(celulas, callback) {

    $.ajax({
        url: URLs.crearPortal,
        type: "POST",
        data: {iCs: celulas.join(",")},
        dataType: "json",
        cache: false
    }).done(function(json) {
        callback(json);

    }).error(function(jqXHR, exception) {
        if (jqXHR.status === 500) {
            alert('Internal Server Error [500].');
        } else {
            alert('Uncaught Error.\n' + jqXHR.responseText);
        }
    });
    return false;
}

function eliminarCelulasDePortal(celulas, callback) {

    $.ajax({
        url: URLs.eliminarPuertaDePortal,
        type: "POST",
        data: {iCs: celulas.join(",")},
        dataType: "json",
        cache: false
    }).done(function(json) {
        callback(json);

    }).error(function(jqXHR, exception) {
        if (jqXHR.status === 500) {
            alert('Internal Server Error [500].');
        } else {
            alert('Uncaught Error.\n' + jqXHR.responseText);
        }
    });
}


function getCelulasEnOtroPortal(celulasSeleccionadas) {
    var celulasEnOtroPortal = [];
    $.each(celulasSeleccionadas, function(indice, celula) {
        if (Simulador.portales.puertas[celula.feature.properties.i])
            celulasEnOtroPortal.push(celula.feature.properties.i);
    });
    return celulasEnOtroPortal;
}

function getIndicesCelulas(celulasSeleccionadas) {
    var indices = [];
    $.each(celulasSeleccionadas, function(indice, celula) {
        indices.push(celula.feature.properties.i);
    });
    return indices;
}

$(document).ready(inicializar);