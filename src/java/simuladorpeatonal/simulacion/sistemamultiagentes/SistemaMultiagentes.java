/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.sistemamultiagentes;

import simuladorpeatonal.simulacion.celulas.Portales;
import simuladorpeatonal.simulacion.celulas.Portal;
import java.io.Serializable;
import java.util.LinkedList;
import org.json.JSONException;
import simuladorpeatonal.simulacion.Simulador;

/**
 *
 * @author Julio
 */
public class SistemaMultiagentes implements Serializable {

    private int colaEnServicio;
    private LinkedList<Peaton> colasDeEspera[];
    private LinkedList<Peaton> peatonesPendientesInsercion;
    private LinkedList<Portal> origenesDePeatonesPendientesInsercion;
    private Peatones peatones;
    private Portales portales;
    private int tiempoSimulacion;
    private int framesCreacion;
    private int cantidadPeatones;

    public SistemaMultiagentes(Peatones peatones, Portales portales) {
        asignarValoresInicio(peatones, portales);

    }

    public void reiniciar(Peatones peatones, Portales portales) {
        asignarValoresInicio(peatones, portales);
    }

    private void asignarValoresInicio(Peatones peatones, Portales portales) {
        this.peatones = peatones;
        this.portales = portales;

        this.colaEnServicio = 0;
        this.colasDeEspera = new LinkedList[Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO];
        for (int i = 0; i < Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO; i++) {
            this.colasDeEspera[i] = new LinkedList<>();
        }
        this.peatonesPendientesInsercion = new LinkedList<>();
        this.origenesDePeatonesPendientesInsercion = new LinkedList<>();
        this.portales.calcularPortalesOrigenYDestinoDisponibles();
        this.cantidadPeatones = portales.cantidadMaximaAgentesSimulacion();
    }

    public void ejecutarIntervalo(int intervalo) throws JSONException {
        insertarPeatonesPendientes(intervalo);
        crearNuevosPeatonesEnIntervalo(intervalo);
        moverPeatones(intervalo);
        this.peatones.registrarPosicionesEnBitacoraEnIntervalo(intervalo);

    }

    private void moverPeatones(int intervalo) throws JSONException {
        LinkedList<Peaton> colaServicio = this.colasDeEspera[this.colaEnServicio];
        int siguienteCola = (this.colaEnServicio + 1) % Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO;
        int tamanioCola = colaServicio.size();
        for (int i = 0; i < tamanioCola; i++) {
            Peaton peaton = colaServicio.poll();
            peaton.moverse(intervalo);
            if (!peaton.llegoADestino()) {
                if (peaton.hayIntervalosRestantesEnActivo()) {
                    colasDeEspera[siguienteCola].add(peaton);
                } else {
                    colasDeEspera[(this.colaEnServicio + Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO) % Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO].add(peaton);
                }
            }
        }

        this.colaEnServicio = siguienteCola;
        
    }

    private void crearNuevosPeatonesEnIntervalo(int intervalo) {
        
        int cantidadPeatonesNuevos = calcularCantidadPeatonesNuevosEnIntervalo(intervalo);
        for (int i = 0; i < cantidadPeatonesNuevos; i++) {
            Portal origen = portales.getPortalOrigenNuevoPeaton();
            Portal destino = portales.getPortalDestinoNuevoPeaton(origen);

            if (origen != null && destino != null) {
                Peaton agente = peatones.crearPeaton(destino);
                if (origen.insertarPeaton(intervalo, agente) == false) {
                    //agregar a cola pendiente
                    this.peatonesPendientesInsercion.add(agente);
                    this.origenesDePeatonesPendientesInsercion.add(origen);
                } else {
                    //agreagar a cola de servicio
                    int colaServicioSiguiente = (this.colaEnServicio + 1) % (Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO);
                    this.colasDeEspera[colaServicioSiguiente].add(agente);
                }
            }
        }
    }

    private void insertarPeatonesPendientes(int intervalo) {
        int cantidadAgentes = this.peatonesPendientesInsercion.size();
        for (int i = 0; i < cantidadAgentes; i++) {
            Peaton agente = this.peatonesPendientesInsercion.poll();
            Portal origen = this.origenesDePeatonesPendientesInsercion.poll();
            if (origen.insertarPeaton(intervalo, agente) == false) {
                this.peatonesPendientesInsercion.add(agente);
                this.origenesDePeatonesPendientesInsercion.add(origen);
            } else {
                //agreagar a cola de servicio
                //int colaServicioSiguiente = (this.colaEnServicio + 1) % (Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO);
                this.colasDeEspera[this.colaEnServicio].add(agente);
            }

        }
    }

    private int calcularCantidadPeatonesNuevosEnIntervalo(int intervalo) {
        int peatonesCreados = (int)Math.floor(((intervalo-1) * this.cantidadPeatones) / (this.framesCreacion / 1.0));
        int peatonesAlMomento = (int) Math.floor((intervalo * this.cantidadPeatones) / (this.framesCreacion / 1.0));
        if (peatonesAlMomento >= this.cantidadPeatones)
            return 0;
        return Math.abs(peatonesAlMomento - peatonesCreados);
        
        
        /*if(intervalo <  this.cantidadPeatones){
            return 1;
        }
        return 0;*/
        
        /*double proporcionIntervalos = 2.0f / (this.tiempoSimulacion * Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO);
         int picoAgentes = (int) Math.floor(this.cantidadPeatones / 2.0);
         int peatonesCreados = (((int) Math.floor(SistemaMultiagentes.distribucionCosenoElevada(((intervalo - 1) * proporcionIntervalos), 1.0, 1.0) * picoAgentes)));
         int peatonesACrear = (((int) Math.floor(SistemaMultiagentes.distribucionCosenoElevada((intervalo * proporcionIntervalos), 1.0, 1.0) * picoAgentes)));
         return Math.abs(peatonesACrear - peatonesCreados);*/
    }

    private static double distribucionCosenoElevada(double x, double media, double desviacionEstandar) {
        return (1 / (2 * desviacionEstandar)) * (1 + Math.cos(((x - media) / desviacionEstandar) * Math.PI));
    }

    public void setParametrosSimulacion(int tiempoSimulacion, float porcentajeFramesCreacion) {
        this.tiempoSimulacion = tiempoSimulacion;
        this.framesCreacion = Math.round(this.tiempoSimulacion * Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO * porcentajeFramesCreacion);
    }


    public boolean extraerPeatones() {
        return this.peatones.extraerTodos();
    }
}
