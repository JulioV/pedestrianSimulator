/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion;

import simuladorpeatonal.simulacion.sistemamultiagentes.Peatones;
import simuladorpeatonal.simulacion.sistemamultiagentes.SistemaMultiagentes;
import simuladorpeatonal.simulacion.celulas.Celulas;
import simuladorpeatonal.simulacion.celulas.Portales;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import simuladorpeatonal.extras.ManejoArchivos;
import simuladorpeatonal.simulacion.celulas.Celula;

/**
 *
 * @author Julio
 */
public class Simulador implements Serializable {

    private int tiempoSimulacion;
    public final static Integer TOTAL_VECINOS = 6;
    public final static Integer INTERVALOS_SIMULACION_POR_SEGUNDO = 6;
    public final static Byte VELOCIDAD_MAXIMA = 4;
    public final static double[] PROBABILIDAD_MOVIMIENTO_A_VECINO = new double[]{0.65,0.5,0.15,0.15,0.15,0.5};
    public final static double[] CASTIGO_POR_VECINOS_OCUPADOS = new double[]{0.00,0.05,0.05,0.166,0.166,0.166};
    public final static boolean[] DESACELERAR_POR_RETROCESO = new boolean[]{false,false,true,true,true,false};
    private Portales portales;
    private Celulas terreno;
    private Peatones peatones;
    private SistemaMultiagentes sistemaMA;
    private boolean reconstruirCampoDestino;
    
    public Simulador(String archivoCelulas, String archivoVecindades) {
        peatones = new Peatones();
        this.construirTerreno(archivoCelulas, archivoVecindades);
        sistemaMA = new SistemaMultiagentes(peatones, portales);
        reconstruirCampoDestino = false;
    }

    public JSONObject getTerrenoJSON() throws JSONException {
        return terreno.getCelulasJSON();
    }

    public JSONObject getPortalesJSON() throws JSONException {
        return portales.getPortalesJSON();
    }

    public boolean eliminarPuertaDePortal(int indicesPuertas[]) {
        for (int indice : indicesPuertas) {
            if (!portales.eliminarPuertaDePortal(terreno.getCelula(indice))) {
                return false;
            }
        }
        this.reconstruirCampoDestino = true;
        return true;
    }

    public boolean adjuntarPuertasAPortal(int indiceDestino, int indicesPuerta[]) {

        if (portales.adjuntarPuertasAPortal(indiceDestino, this.terreno.getCelulas(indicesPuerta))) {
            this.reconstruirCampoDestino = true;
            return true;
        }
        return false;
    }

    public int crearPortal(int indicesPuerta[]) {
        LinkedList<Celula> celulas = this.terreno.getCelulas(indicesPuerta);
        if (celulas.size() > 0) {
            this.reconstruirCampoDestino = true;
        }
        return portales.crearPortal(celulas);
    }

    public String getCampoSeguimientoEnIntervaloJSON(int intervalo) {
        try {
            return Bitacora.getBitacoraCampoSeguimientoADestinoEnIntervalo(intervalo, 2);
        } catch (JSONException ex) {
            Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public void setParametrosSimulacion(int tiempoSimulacion, float porcentajeFramesCreacion) {
        this.tiempoSimulacion = tiempoSimulacion;
        this.sistemaMA.setParametrosSimulacion(tiempoSimulacion, porcentajeFramesCreacion);
    }
    
    

    public void prepararSimulador() {
        /* Temporal 
         setCapacidadEntradaSalidaDePortal(1, 20, 20);
         setCapacidadEntradaSalidaDePortal(2, 20, 20);
         setCapacidadEntradaSalidaDePortal(3, 20, 20);
         setCapacidadEntradaSalidaDePortal(4, 20, 20);*/

        Bitacora.reiniciarBitacora();
        this.peatones = new Peatones();
        this.terreno.reiniciar();
        sistemaMA.reiniciar(peatones, portales);
        if (this.reconstruirCampoDestino) {
            portales.calcularPortalesOrigenYDestinoDisponibles();
            portales.dispersarRastroDestinos();
            
        }
    }

    public boolean setCapacidadEntradaSalidaDePortal(int indicePortal, int entrada, int salida) {
        return this.portales.setCapacidadEntradaSalidaDePortal(indicePortal, entrada, salida);
    }

    public String simular() throws JSONException {
        prepararSimulador();
        int intervalosSimulacion = tiempoSimulacion * Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO;
        for (int i = 0; i < intervalosSimulacion; i++) {
            sistemaMA.ejecutarIntervalo(i);
            //Bitacora.seguimientoADestinoEnIntervalo(i, 2, this.terreno.getCampoSeguimientoADestinoJSON(this.portales.getPortalPorIndice(2)));
        }
        sistemaMA.extraerPeatones();
        
        ManejoArchivos.escribirBitacoraArchivo("C:\\Users\\Julio\\Documents\\CIC\\tesis\\simulador\\SimuladorPeatonalServidor\\bitacora.csv", Bitacora.getInstantaneasSimulador());
        return Bitacora.getBitacora();

    }

    private void construirTerreno(String archivoCelulas, String archivoVecindades) {
        portales = new Portales();
        terreno = new Celulas();
        ManejoArchivos lector = new ManejoArchivos(archivoCelulas, archivoVecindades);
        lector.leerCelulas(portales, terreno);
        lector.leerVecindades(terreno);
        portales.dispersarRastroDestinos();
    }
}
