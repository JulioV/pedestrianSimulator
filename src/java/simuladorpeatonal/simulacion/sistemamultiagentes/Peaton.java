/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.sistemamultiagentes;

import org.json.JSONException;
import simuladorpeatonal.simulacion.Bitacora;
import simuladorpeatonal.simulacion.Simulador;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Portal;
import sun.security.krb5.internal.crypto.Des;

/**
 *
 * @author Julio
 */
public class Peaton {

    private static short cuentaPeatones = 0;
    private short index;
    private byte velocidad;
    private byte velocidadMaxima;
    private Celula posicionActual;
    private Celula posicionAnterior;
    private Portal destino;
    private boolean llegoADestino;
    private byte intervalosEnActivo;

    public Peaton(byte velocidadMaxima, Portal destino) {
        this.llegoADestino = Boolean.FALSE;
        this.index = ++cuentaPeatones;
        this.velocidad = 0;
        this.velocidadMaxima = velocidadMaxima;
        this.destino = destino;
        this.intervalosEnActivo = 0;
    }

    public void registrarseEnBitacoraEnIntervalo(int intervalo) {
        if (!this.llegoADestino && this.posicionActual != null) {
            Bitacora.registrarPosicionAgenteEnIntervalo(this, intervalo, this.posicionActual.getX(), this.posicionActual.getY());
        }
    }

    public void autodestruirse() {
        if (this.posicionActual != null) {
            this.posicionActual.desocupar();
        }

        this.llegoADestino = Boolean.TRUE;
    }

    public short getIndice() {
        return index;
    }

    public byte getVelocidad() {
        return this.velocidad;
    }

    private void acelerar() {
        if (this.velocidad < this.velocidadMaxima) {
            this.velocidad++;
        }
    }

    private void desacelerar() {
        if (this.velocidad > 0) {
            this.velocidad--;
        }
    }

    private void disminuirIntervalosEnActivo(int intervalo) {

        if (this.intervalosEnActivo < 1) {


            this.intervalosEnActivo = this.velocidad;
        }
        this.intervalosEnActivo--;

    }

    public boolean hayIntervalosRestantesEnActivo() {
        return this.intervalosEnActivo > 0;
    }

    public boolean desacelerarPorRetroceso(Celula celulaNueva) {

        if (this.posicionAnterior == null) {
            return false;
        }
        int posicionVecinalCelulaAnterior = this.posicionActual.getPosicionCelulaEnVecinos(this.posicionAnterior);
        int offsetPosicionVecinal = 3 - posicionVecinalCelulaAnterior;
        int posicionVecinalConOffset = (this.posicionActual.getPosicionCelulaEnVecinos(celulaNueva) + offsetPosicionVecinal) % Simulador.TOTAL_VECINOS;
        if (posicionVecinalConOffset < 0) {
            posicionVecinalConOffset += Simulador.TOTAL_VECINOS;
        }
        return Simulador.DESACELERAR_POR_RETROCESO[posicionVecinalConOffset];


    }

    public void moverse(int intervalo) throws JSONException {
        Portal portalFalso = new Portal(this.index);
        Celula nuevaPosicion = posicionActual.elegirNuevaCelula(destino, this.posicionAnterior,this.velocidad == 0?false:true );
        portalFalso = null;
        disminuirIntervalosEnActivo(intervalo);
        if (nuevaPosicion == null) {
            System.out.println("Freno en intervalo " + intervalo);
            desacelerar();
            if (this.velocidad == 0) {
                Portal portalFalso2 = new Portal(this.index);
                this.posicionActual.dispersarRastroEspera(portalFalso2);
                portalFalso2 = null;
            }
        } else {
            boolean desacelerarPorRetroceso = this.desacelerarPorRetroceso(nuevaPosicion);
            this.reunirRastros(posicionAnterior, posicionActual);
            this.colocarPeatonEnCelula(nuevaPosicion);

            if (posicionActual.esParteDelPortalDestino(destino)) {
                this.registrarPosicionPeaton(intervalo);
                this.autodestruirse();
            } else {
                if (desacelerarPorRetroceso) {
                    System.out.println("Se desacelero por retroceso " + intervalo);
                    desacelerar();
                            
                } else {
                    acelerar();
                }
                dispersarRastros(posicionAnterior, posicionActual);
            }
        }
        this.registrarPosicionPeaton(intervalo);
        if (this.intervalosEnActivo == 0) {
        }
        //Bitacora.movimientoPeaton(intervalo, this, this.posicionActual);
    }

    public void registrarPosicionPeaton(int intervalo) throws JSONException {
        Bitacora.movimientoPeaton(intervalo, this, this.posicionActual);
    }

    public boolean llegoADestino() {
        return this.llegoADestino;
    }

    public void colocarPeatonEnCelula(Celula nuevaPosicion) {
        if (this.posicionActual != null) {
            this.posicionActual.desocupar();
        }
        this.posicionAnterior = this.posicionActual;
        this.posicionActual = nuevaPosicion;
        this.posicionActual.ocupar();
    }

    public void reunirRastros(Celula posicionAnterior, Celula posicionActual) {
        posicionActual.reunirRastroEspera(this.destino);
        if (posicionAnterior != null) {
            posicionAnterior.reunirRastroSeguimiento(posicionActual, this.destino);
            posicionAnterior.reunirRastroEvasion(posicionActual, this.destino);
        }
    }

    public void dispersarRastros(Celula posicionAnterior, Celula posicionActual) {
        posicionAnterior.dispersarRastroSeguimiento(posicionActual, destino);
        posicionAnterior.dispersarRastroEvasion(posicionActual, destino);
    }
}
