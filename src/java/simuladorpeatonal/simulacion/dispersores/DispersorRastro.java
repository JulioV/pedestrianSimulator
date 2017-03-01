/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.dispersores;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import simuladorpeatonal.simulacion.Simulador;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Portal;

/**
 *
 * @author Julio
 */
public abstract class DispersorRastro implements Serializable {
    //La guia para moverse entre celulas va de -1 a -6 para poder identificarlas por numeros negativos
    //La guia para moverse a vecinos va de 1 a 6 para poder identificarlos en el arreglo

    public void modificarRastroDesdeCelula(Celula celulaDispersion, int offsetDireccion, Portal destino, double guiaDispersionRastro[], int agregarBorrarRastro) {
        Celula celulaActiva = celulaDispersion;
        double valorRastro = 0.0f;
        int i = 0, direccionCelulaParaEsparcir = 0;
        do {
            direccionCelulaParaEsparcir = ((int) Math.abs(guiaDispersionRastro[i]) - 1 + offsetDireccion) % Simulador.TOTAL_VECINOS;
            if (guiaDispersionRastro[i] < 0) {
                if (!celulaActiva.existeCelulaVecina(direccionCelulaParaEsparcir)) {
                    return;
                }
                valorRastro = (guiaDispersionRastro[++i]);
                celulaActiva = celulaActiva.getCelulaVecina(direccionCelulaParaEsparcir);
                this.setRastroDeCelula(celulaActiva, destino, valorRastro * agregarBorrarRastro);


            } else if (celulaActiva.existeCelulaVecina(direccionCelulaParaEsparcir)) {
                this.setRastroDeCelula(celulaActiva.getCelulaVecina(direccionCelulaParaEsparcir), destino, valorRastro * agregarBorrarRastro);
            }
            i++;
        } while (i < guiaDispersionRastro.length);

    }

    public abstract void setRastroDeCelula(Celula celula, Portal destino, double valorRastro);

    public abstract void dispersarRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion);

    public abstract void reunirRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion);

    public abstract void setRastro(Portal destino, double valorRastro);

    public abstract double getRastroParaDestino(Portal destino);

    public abstract void reiniciar();
}
