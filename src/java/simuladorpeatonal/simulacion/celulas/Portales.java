/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.celulas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import org.json.JSONException;
import org.json.JSONObject;
import simuladorpeatonal.simulacion.enums.TipoPortal;

/**
 *
 * @author Julio
 */
public class Portales implements Serializable {

    private HashMap<Integer, Portal> portales;
    private LinkedList<Portal> origenes, destinos;

    public Portales() {
        portales = new HashMap<>();
        this.origenes = new LinkedList<>();
        this.destinos = new LinkedList<>();
    }

    public Portal getPortalOrigenNuevoPeaton() {
        Portal portal = origenes.poll();
        if (portal != null && portal.hayLugaresEntrada()) {
            portal.quitarUnLugarEntrada();
            //Verifica si hay mas lugares de donde escoger y agrega el portal para ser tomado en cuenta.
            if (portal.hayLugaresEntrada()) {
                origenes.add(portal);
            }
            return portal;
        }
        return null;
    }

    public Portal getPortalDestinoNuevoPeaton(Portal origen) {
        Portal destino = destinos.poll();
        if (destino != null && destino.getIndice() == origen.getIndice()) {
            if (destinos.size() > 0) {
                destinos.add(destino);
                return getPortalDestinoNuevoPeaton(origen);
            } else {
                return null;
            }
        }
        if (destino != null && destino.hayLugaresSalida()) {
            destino.quitarUnLugarSalida();
            //Verifica si hay mas lugares de donde escoger y agrega el portal para ser tomado en cuenta.
            if (destino.hayLugaresSalida()) {
                destinos.add(destino);
            }
            return destino;
        }
        return null;
    }

    public int cantidadMaximaAgentesSimulacion() {
        int cantidad = 0;
        for (Portal origen : origenes) {
            cantidad += origen.getPeatonesMaximosEntrada();
        }
        return cantidad;
    }

    private LinkedList<Portal> getPortalesDestinoDisponibles() {
        LinkedList<Portal> destinosDisponibles = new LinkedList<>();
        for (Portal portal : this.portales.values()) {
            if (portal.esTipo(TipoPortal.SALIDA) || portal.esTipo(TipoPortal.ENTRADA_SALIDA)) {
                destinosDisponibles.add(portal);
            }
        }
        return destinosDisponibles;
    }

    private LinkedList<Portal> getPortalesOrigenDisponibles() {
        LinkedList<Portal> origenesDisponibles = new LinkedList<>();
        for (Portal portal : this.portales.values()) {
            if (portal.esTipo(TipoPortal.ENTRADA) || portal.esTipo(TipoPortal.ENTRADA_SALIDA)) {
                origenesDisponibles.add(portal);
            }
        }
        return origenesDisponibles;
    }

    public boolean eliminarPuertaDePortal(Celula puerta) {

        Portal portal = puerta.getPortalPerteneciente();
        if (portal != null && portal.eliminarPuerta(puerta)) {
            puerta.desconvertirEnPuerta();
            if (portal.cantidadPuertas() == 0) {
                this.portales.remove(portal.getIndice());
            }
            return true;
        }
        return false;

    }

    public boolean adjuntarPuertasAPortal(Integer indiceDestino, LinkedList<Celula> puertas) {

        Portal portal = this.portales.get(indiceDestino);
        if (portal != null) {
            for (Celula celula : puertas) {
                celula.convertirEnPuerta(portal);
                portal.agregarPuerta(celula);
            }
            return true;
        }


        return false;

    }

    public int crearPortal(LinkedList<Celula> puertas) {
        Portal portal = new Portal();
        for (Celula celula : puertas) {
            celula.convertirEnPuerta(portal);
            portal.agregarPuerta(celula);
        }
        this.portales.put(portal.getIndice(), portal);
        return portal.getIndice();
    }

    public void calcularPortalesOrigenYDestinoDisponibles() {
        this.origenes = this.getPortalesOrigenDisponibles();
        this.destinos = this.getPortalesDestinoDisponibles();
    }

    public boolean dispersarRastroDestinos() {

        for (Portal destino : this.destinos) {
            destino.dispersarRastroDestino();
        }
        return true;
    }

    public Portal getPortalPorIndice(int idDestino) {
        return this.portales.get(idDestino);
    }

    public boolean setCapacidadEntradaSalidaDePortal(int indicePortal, int entrada, int salida) {
        if (this.portales.containsKey(indicePortal)) {
            Portal portal = portales.get(indicePortal);
            portal.setPeatonesEntrada(entrada);
            portal.setPeatonesSalida(salida);
            if(portal.getPeatonesEntrada() == 0)
                portal.setTipo(TipoPortal.SALIDA);
            if(portal.getPeatonesSalida() == 0)
                portal.setTipo(TipoPortal.ENTRADA);
            return true;
        }
        return false;
    }

    public JSONObject getPortalesJSON() throws JSONException {
        JSONObject puertasJSON = new JSONObject();
        for (Portal portal : this.portales.values()) {
            puertasJSON = portal.addPuertasJSON(puertasJSON);
        }

        JSONObject portalesJSON = new JSONObject();
        for (Portal portal : this.portales.values()) {
            JSONObject atributos = new JSONObject();
            atributos.put("p",  portal.getCantidadPuertas());
            atributos.put("pe",  portal.getPeatonesEntrada());
            atributos.put("ps",  portal.getPeatonesSalida());
            portalesJSON.put(Integer.toString(portal.getIndice()),atributos);
        }
        JSONObject respuesta = new JSONObject();
        respuesta.put("puertas", puertasJSON);
        respuesta.put("portales", portalesJSON);

        return respuesta;
    }
}
