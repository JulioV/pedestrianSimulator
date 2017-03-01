/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.dispersores;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Portal;

/**
 *
 * @author Julio
 */
public class DispersorRastroEvacion extends DispersorRastro implements Serializable {

    private Map<Integer, Double> rastroEvacion;
    //private static final double guiaDispersion[] = {-1.0, 0.0,  -1.0, 0.75, 2.0,3.0,5.0, 6.0, -1.0, 0.5, 2.0, 6.0, -1.0, 0.25, 2.0, 6.0, -1.0, 0.0, 2.0, 6.0};
    private static final double guiaDispersion[] = {-1, 0,-1, 0.95,  -1, 0.75, -1, 0.65, -1, 0.45, -1, 0.25, -1, 0.0, 2, 6};

    public DispersorRastroEvacion() {
        this.rastroEvacion = new HashMap<>();
    }
    
    @Override
    public void dispersarRastroDesdeCelula(Celula celulaDispersion, Portal destino, int offsetDireccion) {
        super.modificarRastroDesdeCelula(celulaDispersion, offsetDireccion, destino, guiaDispersion, 1);
    }

    @Override
    public void reunirRastroDesdeCelula(Celula celulaDispersion, Portal destino,int offsetDireccion) {
        super.modificarRastroDesdeCelula(celulaDispersion, offsetDireccion, destino, guiaDispersion, -1);
    }

    @Override
    public void setRastroDeCelula(Celula celula, Portal destino, double valorRastro) {
        celula.setRastroEvacion(destino, valorRastro);
    }

    @Override
    public void setRastro(Portal destino, double valorRastro) {
        int idDestino = destino.getIndice();
        if (this.rastroEvacion.containsKey(idDestino)) {
            this.rastroEvacion.put(idDestino, this.rastroEvacion.get(idDestino) + valorRastro);
        } else {
            this.rastroEvacion.put(idDestino, valorRastro);
        }
    }

    @Override
    public double getRastroParaDestino(Portal destino) {
        
        double rastroParaDestino = 0.0f;
        for (Entry<Integer, Double> rastroDestino : this.rastroEvacion.entrySet()) {
            if (rastroDestino.getKey() != destino.getIndice()) {
                rastroParaDestino += rastroDestino.getValue().doubleValue();
            }
        }
        return rastroParaDestino;
    }
    
    @Override
    public void reiniciar(){
        this.rastroEvacion = new HashMap<>();
    }

  
}
