/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import simuladorpeatonal.extras.ControladorBD;
import simuladorpeatonal.simulacion.Simulador;

/**
 *
 * @author Julio
 */
public class SimulacionBean {

    private Simulador simulador;

    public SimulacionBean() {
        this.simulador = ControladorBD.cargarSimuladorBD(130);
    }

    public JSONObject getCelulasJSON() throws JSONException {
        return this.simulador.getTerrenoJSON();
    }

    public JSONObject getPortalesJSON() throws JSONException {
        return this.simulador.getPortalesJSON();
    }

    public boolean eliminarPuertaDePortal(int[] indicesPuertas) {
        return this.simulador.eliminarPuertaDePortal(indicesPuertas);
    }

    public boolean adjuntarPuertasAPortal(int indiceDestino, int[] indicesPuerta) {
        return this.simulador.adjuntarPuertasAPortal(indiceDestino, indicesPuerta);
    }

    public int crearPortal(int[] indicesPuerta) {
        return this.simulador.crearPortal(indicesPuerta);
    }

    public String getCampoSeguimientoEnIntervaloJSON(int intervalo) {
        return this.simulador.getCampoSeguimientoEnIntervaloJSON(intervalo);
    }

    public String simular() {
        try {
            String respuesta = this.simulador.simular();
            return respuesta;
        } catch (JSONException ex) {
            Logger.getLogger(SimulacionBean.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public boolean setCapacidadEntradaSalidaDePortal(int indicePortal, int entrada, int salida){
        return this.simulador.setCapacidadEntradaSalidaDePortal(indicePortal, entrada, salida);
    }

    public void setParametrosSimulacion(int tiempo, float porcentajeFramesCreacion) {
        this.simulador.setParametrosSimulacion(tiempo, porcentajeFramesCreacion);
    }
}
