/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.celulas;

import simuladorpeatonal.simulacion.enums.EstadoCelula;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import simuladorpeatonal.simulacion.Simulador;
import simuladorpeatonal.simulacion.dispersores.DispersorRastroEspera;
import simuladorpeatonal.simulacion.dispersores.DispersorRastroDestino;
import simuladorpeatonal.simulacion.dispersores.DispersorRastroEvacion;
import simuladorpeatonal.simulacion.dispersores.DispersorRastroSeguimiento;

/**
 *
 * @author Julio
 */
public class Celula implements Serializable {

    private int indice;
    private double x;
    private double y;
    private EstadoCelula estado;
    private Celula[] vecinos;
    private DispersorRastroDestino dispersorDestino;
    private DispersorRastroSeguimiento dispersorSeguimiento;
    private DispersorRastroEvacion dispersorEvasion;
    private DispersorRastroEspera dispersorEspera;
    private Portal portalPerteneciente;
    private boolean esPuerta;
    private Random random;
    private static final Hashtable<String, Integer> relacionesEliminadas = new Hashtable<String, Integer>() {
        {

            
        }
    };

    private boolean esRelacionEliminada(int idOrigen, int idDestino) {
        return Celula.relacionesEliminadas.containsKey(idOrigen + " " + idDestino);
    }

    public Celula(int indice, double x, double y) {
        this.indice = indice;
        this.x = x;
        this.y = y;
        this.estado = EstadoCelula.VACIA;
        this.vecinos = new Celula[Simulador.TOTAL_VECINOS];
        this.portalPerteneciente = null;
        this.esPuerta = false;
        this.dispersorDestino = new DispersorRastroDestino();
        this.dispersorEvasion = new DispersorRastroEvacion();
        this.dispersorSeguimiento = new DispersorRastroSeguimiento();
        this.dispersorEspera = new DispersorRastroEspera();
        random = new Random();
    }

    public int getIndice() {
        return indice;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean esPuerta() {
        return this.esPuerta;
    }

    public boolean estaVacia() {
        if (this.estado == EstadoCelula.VACIA) {
            return true;
        }
        return false;
    }

    public void ocupar() {
        this.estado = EstadoCelula.OCUPADA;
    }

    public void desocupar() {
        this.estado = EstadoCelula.VACIA;
    }

    protected JSONObject getCelulaJSON() throws JSONException {

        JSONObject geometria = new JSONObject();
        geometria.put("type", "Point");
        geometria.accumulate("coordinates", this.x);
        geometria.accumulate("coordinates", this.y);

        JSONObject propiedades = new JSONObject();
        propiedades.put("i", this.indice);

        if (this.esPuerta()) {
            propiedades.put("p", true);
        }
        JSONObject celulaJSON = new JSONObject();
        celulaJSON.put("type", "Feature");
        celulaJSON.put("properties", propiedades);
        celulaJSON.put("geometry", geometria);

        return celulaJSON;
    }

    protected JSONObject getCampoSeguimientoADestinoJSON(Portal destino) throws JSONException {
        JSONObject geometria = new JSONObject();
        geometria.put("type", "Point");
        geometria.accumulate("coordinates", this.x);
        geometria.accumulate("coordinates", this.y);

        JSONObject propiedades = new JSONObject();
        propiedades.put("i", this.indice);
        propiedades.put("v", this.dispersorDestino.getRastroParaDestino(destino));

        JSONObject celulaJSON = new JSONObject();
        celulaJSON.put("type", "Feature");
        celulaJSON.put("properties", propiedades);
        celulaJSON.put("geometry", geometria);

        return celulaJSON;
    }

    public void setVecino(int index, Celula celula) {
        this.vecinos[index] = celula;
    }

    public boolean existeCelulaVecina(int idVecino) {
        return this.vecinos[idVecino] != null;
    }

    public Celula getCelulaVecina(int idVecino) {
        return this.vecinos[idVecino];

    }

    public void setRastroSeguimiento(Portal destino, double valor) {
        this.dispersorSeguimiento.setRastro(destino, valor);
    }

    public void setRastroEvacion(Portal destino, double valor) {
        this.dispersorEvasion.setRastro(destino, valor);
    }

    public void setRastroEspera(Portal destino, double valor) {
        this.dispersorEspera.setRastro(destino, valor);
    }

    public double probabilidadDeElegirAVecino(Celula celulaAnterior, Celula celulaVecina, boolean peatonMovimiento) {
        if (celulaAnterior == null) {
            return 1.0;
        }
        int posicionVecinalCelulaAnterior = getPosicionCelulaEnVecinos(celulaAnterior);
        int offsetPosicionVecinal = 3 - posicionVecinalCelulaAnterior;
        //System.out.println("Posicion Vecinal Anterior " + posicionVecinalCelulaAnterior);

        int posicionVecinalConOffset = (getPosicionCelulaEnVecinos(celulaVecina) + offsetPosicionVecinal) % Simulador.TOTAL_VECINOS;
        if (posicionVecinalConOffset < 0) {
            posicionVecinalConOffset += Simulador.TOTAL_VECINOS;
        }
        //System.out.println(" equivale a " + posicionVecinalConOffset + " con probabilidad " + Simulador.PROBABILIDAD_MOVIMIENTO_A_VECINO[posicionVecinalConOffset]);
        return Simulador.PROBABILIDAD_MOVIMIENTO_A_VECINO[posicionVecinalConOffset] * ((peatonMovimiento && (posicionVecinalConOffset == 3 || posicionVecinalConOffset == 2 || posicionVecinalConOffset == 4)) ? 0 : 1);

    }

    private boolean frenarPorFaltaEspacio(Celula celulaAnterior) {
        if (celulaAnterior == null) {
            return false;
        }
        int vecinoFrontalesOcupados = 0;
        for (Celula vecina : this.vecinos) {
            if (vecina != null && !vecina.estaVacia()) {
                int posicionVecinalCelulaAnterior = getPosicionCelulaEnVecinos(celulaAnterior);
                int offsetPosicionVecinal = 3 - posicionVecinalCelulaAnterior;
                //System.out.println("Posicion Vecinal Anterior " + posicionVecinalCelulaAnterior);

                int posicionVecinalConOffset = (getPosicionCelulaEnVecinos(vecina) + offsetPosicionVecinal) % Simulador.TOTAL_VECINOS;
                if (posicionVecinalConOffset < 0) {
                    posicionVecinalConOffset += Simulador.TOTAL_VECINOS;
                }
                if (posicionVecinalConOffset == 0 || posicionVecinalConOffset == 1 || posicionVecinalConOffset == 5) {
                    vecinoFrontalesOcupados++;
                }
            }

        }

        if (vecinoFrontalesOcupados == 3) {
            return true;
        }
        return false;

    }

    public Celula elegirNuevaCelula(Portal destino, Celula celulaAnterior, boolean peatonEnMovimiento) {
        double probabilidadMovimiento = 0.5;
        Celula celulaElegida = null;
        double probabilidadTemporal = 0;
        if (peatonEnMovimiento && frenarPorFaltaEspacio(celulaAnterior) && random.nextFloat() > 0.1) {
            return null;
        }
        for (Celula vecina : this.vecinos) {
            if (vecina != null && vecina.estaVacia() && !esRelacionEliminada(this.getIndice(), vecina.getIndice())) {
                probabilidadTemporal = vecina.probabilidadMovimiento(destino, this.dispersorDestino.getRastroParaDestino(destino), peatonEnMovimiento ? Simulador.CASTIGO_POR_VECINOS_OCUPADOS[this.getVecinosOcupados()] : 0.0);
                probabilidadTemporal *= probabilidadDeElegirAVecino(celulaAnterior, vecina, peatonEnMovimiento);
                if (probabilidadTemporal >= probabilidadMovimiento) {
                    if (probabilidadTemporal == probabilidadMovimiento) {
                        if (random.nextBoolean()) {
                            probabilidadMovimiento = probabilidadTemporal;
                            celulaElegida = vecina;
                        }
                    } else {
                        probabilidadMovimiento = probabilidadTemporal;
                        celulaElegida = vecina;
                    }

                }
            }
        }
        return celulaElegida;
    }

    private double probabilidadMovimiento(Portal destino, double campoDireccionDestinoOrigen, double rastroEspera) {

        double probabilidadADestino = (campoDireccionDestinoOrigen - this.dispersorDestino.getRastroParaDestino(destino)) * 3;
        probabilidadADestino += this.dispersorSeguimiento.getRastroParaDestino(destino) * 1.5;
        probabilidadADestino -= this.dispersorEvasion.getRastroParaDestino(destino) * 1.2;

        //probabilidadADestino -= rastroEspera * 3;
        //probabilidadADestino -= this.dispersorEspera.getRastroParaDestino(portalFalso) * 1.5;
        //System.out.println("Rastro espera: " + this.dispersorEspera.getRastroParaDestino(portalFalso));
        return probabilidadADestino;
    }

    public int getVecinosOcupados() {
        int vecinosOcupados = 0;
        for (Celula vecina : this.vecinos) {
            if (vecina != null && !vecina.estaVacia()) {
                vecinosOcupados++;
            }
        }
        return vecinosOcupados;
    }

    public int getPosicionCelulaEnVecinos(Celula celula) {
        short direccion = -1;
        while (direccion++ < Simulador.TOTAL_VECINOS) {
            if (this.vecinos[direccion] != null && this.vecinos[direccion].getIndice() == celula.getIndice()) {
                return direccion;
            }
        }
        return -1;
    }

    public void reiniciarDispersores() {
        this.dispersorDestino.reiniciar();
        this.dispersorSeguimiento.reiniciar();
        this.dispersorEvasion.reiniciar();
        this.dispersorEspera.reiniciar();
    }

    public void dispersarRastroEspera(Portal destino) {
        dispersorEspera.dispersarRastroDesdeCelula(this, destino, 0);
    }

    public void reunirRastroEspera(Portal destino) {
        dispersorEspera.reunirRastroDesdeCelula(this, destino, 0);
    }

    public void dispersarRastroSeguimiento(Celula celulaDestino, Portal destino) {
        int offsetDireccion = this.getPosicionCelulaEnVecinos(celulaDestino);
        dispersorSeguimiento.dispersarRastroDesdeCelula(celulaDestino, destino, offsetDireccion);
    }

    public void dispersarRastroEvasion(Celula celulaDestino, Portal destino) {
        int offsetDireccion = this.getPosicionCelulaEnVecinos(celulaDestino);
        dispersorEvasion.dispersarRastroDesdeCelula(celulaDestino, destino, offsetDireccion);
    }

    public void reunirRastroSeguimiento(Celula celulaDestino, Portal destino) {
        int offsetDireccion = this.getPosicionCelulaEnVecinos(celulaDestino);
        dispersorSeguimiento.reunirRastroDesdeCelula(celulaDestino, destino, offsetDireccion);
    }

    public void reunirRastroEvasion(Celula celulaDestino, Portal destino) {
        int offsetDireccion = this.getPosicionCelulaEnVecinos(celulaDestino);
        dispersorEvasion.reunirRastroDesdeCelula(celulaDestino, destino, offsetDireccion);
    }

    public void dispersarRastroDestino(Portal destino, int rastroNuevo) {
        this.dispersorDestino.modificarRastroDesdeCelula(this, destino, rastroNuevo);
    }

    public void convertirEnPuerta(Portal portal) {
        this.esPuerta = true;
        this.portalPerteneciente = portal;
    }

    public void desconvertirEnPuerta() {
        this.esPuerta = false;
        this.portalPerteneciente = null;
    }

    public boolean esParteDelPortalDestino(Portal portal) {
        return (this.esPuerta() && this.portalPerteneciente.getIndice() == portal.getIndice());
    }

    public Portal getPortalPerteneciente() {
        return this.portalPerteneciente;
    }
}
