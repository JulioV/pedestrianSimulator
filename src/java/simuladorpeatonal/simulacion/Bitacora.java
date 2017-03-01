/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion;


import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import simuladorpeatonal.simulacion.sistemamultiagentes.Peaton;
import simuladorpeatonal.simulacion.celulas.Celula;
import org.json.JSONObject;
import simuladorpeatonal.simulacion.sistemamultiagentes.Peatones;

/**
 *
 * @author Julio
 */
public class Bitacora {

    private static JSONObject bitacora = new JSONObject();
    private static JSONObject bitacoraCampo = new JSONObject();
    private static ArrayList<String> instantaneasSimulador = new ArrayList<>();
    private static long timeStamp = 1388556000000L;

    public static void reiniciarBitacora() {
        Bitacora.bitacora = new JSONObject();
        Bitacora.bitacoraCampo = new JSONObject();
        Bitacora.instantaneasSimulador = new ArrayList<>();
    }

    public static void seguimientoADestinoEnIntervalo(int intervalo, int idDestino, JSONObject listaRastroDeCelulas) throws JSONException {
        Bitacora.bitacoraCampo.put(Integer.toString(intervalo), listaRastroDeCelulas);
    }

    public static void movimientoPeaton(int intervalo, Peaton peaton, Celula celula) throws JSONException {
        int diferenciaIntervalo = (int)(intervalo * (1000 / Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO));
        String indicePeatonKey = String.valueOf(peaton.getIndice());
        
        JSONObject trayectoria;
        if (!Bitacora.bitacora.has(indicePeatonKey)){
            trayectoria = new JSONObject();
            trayectoria.put("LatLng", new JSONArray());
            Bitacora.bitacora.put(indicePeatonKey, trayectoria);
        }

        trayectoria = (JSONObject)Bitacora.bitacora.get(indicePeatonKey);
        trayectoria.accumulate("LatLng",new JSONArray(Arrays.asList(celula.getX(), celula.getY())) );
        trayectoria.accumulate("TimeStamp", timeStamp + diferenciaIntervalo);

    }

    public static void llegadaPeaton(int intervalo, Peaton peaton) {
        /*String segundo = Integer.toString((int) Math.floor((double) intervalo / Simulador.INTERVALOS_SIMULACION_POR_SEGUNDO));
        try {
            JSONObject logSegundo = Bitacora.bitacora.getJSONObject(segundo);
            logSegundo.accumulate(Integer.toString(peaton.getIndice()), -1);
        } catch (NoSuchElementException exception) {
            Bitacora.bitacora.put(segundo, (new JSONObject()).put(Integer.toString(peaton.getIndice()), -1));
        }*/
    }
    
    public static ArrayList<String> getInstantaneasSimulador(){
        return Bitacora.instantaneasSimulador;
    }

    public static String getBitacora() throws JSONException {
       
        Iterator<?> keys = Bitacora.bitacora.keys();
        JSONArray respuesta = new JSONArray();
        while( keys.hasNext() ){
            String key = (String)keys.next();
            JSONObject trayectoria =Bitacora.bitacora.getJSONObject(key);
            
            
            JSONObject geometria = new JSONObject();
            geometria.put("type", "MultiPoint");
            geometria.put("coordinates", trayectoria.get("LatLng"));
            JSONObject propiedades = new JSONObject();
            propiedades.put("time", trayectoria.get("TimeStamp"));
            propiedades.put("id", key);
            JSONObject trayectoriaJSON = new JSONObject();
            trayectoriaJSON.put("type", "Feature");
            trayectoriaJSON.put("geometry", geometria);
            trayectoriaJSON.put("properties", propiedades);
            
            respuesta.put(trayectoriaJSON);
        }
        return respuesta.toString();
    }
    
    public static void registrarPosicionAgenteEnIntervalo(Peaton peaton, int intervalo, double x, double y){
        Bitacora.instantaneasSimulador.add(peaton.getIndice()+" "+ intervalo+" "+ x*100 + " "+ y*100 + " " + peaton.getVelocidad());
    }

    public static String getBitacoraCampoSeguimientoADestinoEnIntervalo(int intervalo, int idDestino) throws JSONException {
        return Bitacora.bitacoraCampo.get(Integer.toString(intervalo)).toString();
    }

   
}
