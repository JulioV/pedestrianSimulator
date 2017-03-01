/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.celulas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import simuladorpeatonal.simulacion.Bitacora;
import simuladorpeatonal.simulacion.sistemamultiagentes.Peaton;
import simuladorpeatonal.simulacion.enums.TipoPortal;

/**
 *
 * @author Julio
 */
public class Portal implements Serializable {

    private static short cuentaPortales = 0;
    private int indice;
    private TipoPortal tipo;
    private LinkedList<Celula> puertas;
    private int peatonesEntrada;
    private int peatonesSalida;
    private Random random = new Random();
    public Portal() {
        this.indice = ++cuentaPortales;
        this.puertas = new LinkedList<>();
        this.tipo = TipoPortal.ENTRADA_SALIDA;
        this.peatonesEntrada = 0;
        this.peatonesSalida = 0;
    }
    
    public Portal(int indice){
        this.indice = indice;
    }

    public boolean hayLugaresEntrada() {
        return this.peatonesEntrada > 0;
    }

    public boolean hayLugaresSalida() {
        return this.peatonesSalida > 0;
    }

    public void quitarUnLugarEntrada() {
        this.peatonesEntrada--;
    }

    public void quitarUnLugarSalida() {
        this.peatonesSalida--;
    }

    public int getPeatonesMaximosEntrada() {
        return peatonesEntrada;
    }

    public void setPeatonesEntrada(int cantidad) {
        this.peatonesEntrada = cantidad;
    }

    public void setPeatonesSalida(int cantidad) {
        this.peatonesSalida = cantidad;
    }

    public void setTipo(TipoPortal tipo) {
        this.tipo = tipo;
    }
    
    public int cantidadPuertas(){
        return this.puertas.size();
    }

    public boolean insertarPeaton(int intervalo, Peaton peaton) {

        Celula puertaDisponible = obtenerPuertaDisponible();
        if (puertaDisponible != null) {
            peaton.colocarPeatonEnCelula(puertaDisponible);
            //Bitacora.movimientoPeaton(intervalo, peaton, puertaDisponible);

            return true;
        }
        return false;

    }

    public boolean esTipo(TipoPortal tipo) {
        return this.tipo == tipo;
    }

    private Celula obtenerPuertaDisponible() {
        LinkedList<Celula> puertasDisponibles = new LinkedList<>();
        for (Celula puerta : this.puertas) {
            if (puerta.estaVacia()) {
                puertasDisponibles.add(puerta);
            }
        }
        if (puertasDisponibles.size() > 0) {
            return puertasDisponibles.get(random.nextInt(puertasDisponibles.size()));
        }
        /*int cantidadPuertas = this.puertas.size();
         for (int i = 0; i < cantidadPuertas; i++) {
         CelulaPuerta puerta = this.puertas.poll();
         this.puertas.add(puerta);
         if (puerta.estaVacia()) {
         return puerta;
         }
         }*/
        return null;


    }

    public int getIndice() {
        return indice;
    }

    public boolean agregarPuerta(Celula celula) {
        return this.puertas.add(celula);
    }

    public boolean eliminarPuerta(Celula celula) {
        return this.puertas.remove(celula);
    }

    public void dispersarRastroDestino() {
        for (Celula celula : puertas) {
            celula.dispersarRastroDestino(this, 0);
        }
    }

    public JSONObject addPuertasJSON(JSONObject puertas) throws JSONException {
        for (Celula puerta : this.puertas) {
            puertas.accumulate(Integer.toString(puerta.getIndice()), this.indice);
        }
        return puertas;

    }

    public int getCantidadPuertas() {
        return this.puertas.size();
    }
    
    public int getPeatonesEntrada() {
        return this.peatonesEntrada;
    }
    
    public int getPeatonesSalida() {
        return this.peatonesSalida;
    }
}
