/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.dispersores;

import java.util.HashMap;
import java.util.Map;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Portal;

/**
 *
 * @author Julio
 */
public class DispersorRastroEspera extends DispersorRastro {

    private static final double guiaDispersion[] = {-1, 0.5, -4, 0.5, 2, 3, 4, 5, 6};
    //private static final double guiaDispersion[] = {-1, 0.5f, 1, 2, 6, -4, 0, -2, 0.5f, 2, 3, -5, 0, -3, 0.5f, 3, 4, -6, 0, -4, 0.5f, 4, 5, -1, 0, -5, 0.5f, 5, 6, -2, 0, -6, 0.5f, 6};
    private int rondasDispersion = 0;
    private Map<Integer, Double> rastroEspera;

    public DispersorRastroEspera() {
        this.rondasDispersion = 0;
        this.rastroEspera = new HashMap<>();
    }

    @Override
    public void dispersarRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion) {
        if (this.rondasDispersion == 0) {
            this.rondasDispersion++;
            super.modificarRastroDesdeCelula(celulaDispersion, 0, destino, guiaDispersion, 1);
        }

    }

    @Override
    public void reunirRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion) {
        while (rondasDispersion > 0) {
            super.modificarRastroDesdeCelula(celulaDispersion, 0, destino, guiaDispersion, -1);
            rondasDispersion--;
        }

    }

    @Override
    public void setRastroDeCelula(Celula celula, Portal destino, double valorRastro) {
        celula.setRastroEspera(destino, valorRastro);
    }

    @Override
    public void setRastro(Portal destino, double valorRastro) {
        int idDestino = destino.getIndice();
        if (this.rastroEspera.containsKey(idDestino)) {
            this.rastroEspera.put(idDestino, this.rastroEspera.get(idDestino) + valorRastro);
        } else {
            this.rastroEspera.put(idDestino, valorRastro);
        }
    }

    @Override
    public double getRastroParaDestino(Portal destino) {
        double rastroParaDestino = 0.0f;
        for (Map.Entry<Integer, Double> rastroPeaton : this.rastroEspera.entrySet()) {
            if (rastroPeaton.getKey() != destino.getIndice()) {
                rastroParaDestino += rastroPeaton.getValue().doubleValue();
            }
        }
        return rastroParaDestino;

    }

    @Override
    public void reiniciar() {
        this.rondasDispersion = 0;
        this.rastroEspera = new HashMap<>();
    }
}
