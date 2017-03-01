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
public class DispersorRastroDestino extends DispersorRastro implements Serializable {

    private Map<Integer, Double> rastroDestino;

    public DispersorRastroDestino() {
        this.rastroDestino = new HashMap<>();
    }

    public void modificarRastroDesdeCelula(Celula celula, Portal destino, int rastroNuevo) {
        Double rastroDireccionDestino = this.rastroDestino.get(destino.getIndice());
        if (rastroDireccionDestino == null || rastroNuevo < rastroDireccionDestino) {

            this.setRastroDeCelula(celula, destino, rastroNuevo);
            for (int i = 0; i < Simulador.TOTAL_VECINOS; i++) {

                if (celula.existeCelulaVecina(i)) {
                    celula.getCelulaVecina(i).dispersarRastroDestino(destino, rastroNuevo + (celula.getCelulaVecina(i).esPuerta() ? 2 : 1));
                }
            }

        }
    }

    @Override
    public double getRastroParaDestino(Portal destino) {
        Double rastro = this.rastroDestino.get(destino.getIndice());
        if (rastro == null) {
            return 0;
        }
        return rastro.doubleValue();
    }

    @Override
    public void setRastroDeCelula(Celula celula, Portal destino, double valorRastro) {
        this.rastroDestino.put(destino.getIndice(), valorRastro);
    }

    @Override
    public void dispersarRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reunirRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRastro(Portal destino, double valorRastro) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reiniciar() {
        this.rastroDestino = new HashMap<>();
    }
}
