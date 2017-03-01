/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.celulas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Julio
 */
public class Celulas implements Serializable {

    private HashMap<Integer, Celula> celulas;

    public Celulas() {
        celulas = new HashMap<>();
    }
    
    public Celula getCelula(Integer indice){
        return this.celulas.get(indice);
    }
    
    public LinkedList<Celula> getCelulas(int[] indices){
         LinkedList<Celula> puertas = new LinkedList<>();
         for(int indicePuerta : indices){
             puertas.push(this.getCelula(indicePuerta));
         }
         return puertas;
    }
    
    public void agregarCelula(Celula celula){
        this.celulas.put(celula.getIndice(), celula);
    }
    
    public JSONObject getCelulasJSON() throws JSONException{
        JSONObject respuesta = new JSONObject();
        for (Celula celula : celulas.values()) {
            respuesta.accumulate("features", celula.getCelulaJSON());
           
        }
        return respuesta;
    }
    
    public JSONObject getCampoSeguimientoADestinoJSON(Portal destino) throws JSONException{
        JSONObject respuesta = new JSONObject();
        for (Celula celula : celulas.values()) {
            respuesta.accumulate("features", celula.getCampoSeguimientoADestinoJSON(destino));
           
        }
        return respuesta;
    }
    
    public boolean reiniciar(){
         for(Celula celula : this.celulas.values()){
            celula.reiniciarDispersores();
            if(!celula.estaVacia()){
                //return false;
            }
        }
        return true;
    }
    
}
