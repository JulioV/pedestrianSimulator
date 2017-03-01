/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.sistemamultiagentes;

import simuladorpeatonal.simulacion.celulas.Portal;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import simuladorpeatonal.simulacion.Simulador;

/**
 *
 * @author Julio
 */
public class Peatones implements Serializable {

    private Map<Integer, Peaton> peatones;

    public Peatones() {
        this.peatones = new HashMap<>();
    }

    public Peaton crearPeaton(Portal destino) {
        Peaton agente = new Peaton(Simulador.VELOCIDAD_MAXIMA, destino);
        this.peatones.put((int) agente.getIndice(), agente);
        return agente;
    }

    public void registrarPosicionesEnBitacoraEnIntervalo(int intervalo) {
        for (Peaton peaton : this.peatones.values()) {
            
            peaton.registrarseEnBitacoraEnIntervalo(intervalo);
        }
    }

    public boolean extraerTodos() {

        for (Peaton peaton : this.peatones.values()) {
            if (!peaton.llegoADestino()) {
                peaton.autodestruirse();
            }
        }
        return true;
    }
}
