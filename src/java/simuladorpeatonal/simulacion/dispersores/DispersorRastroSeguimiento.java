/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.simulacion.dispersores;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Portal;

/**
 *
 * @author Julio
 */
public class DispersorRastroSeguimiento extends DispersorRastro implements Serializable{

    //private  static final double guiaDispersion[] = {-4.0, 0.0,-4.0, 0.75,2.0, 3.0, 5.0,6.0, -4.0, 0.5, 3.0, 5.0, -4.0, 0.25, 3.0, 5.0, -4.0, 0.0, 3.0, 5.0};
    private  static final double guiaDispersion[] = {-4, 0,3,5,-4, 0.75,2, 3, 5,6, -4, 0.5, 3, 5, -4, 0.25, 3, 5, -4, 0, 3, 5};
                                                    //-4, 1, 2, 3, 5, 6, -4, 0.75f, 3, 5, -4, 0.5f, 3, 5, -4, 0.25f, 3, 5, -4, 0, 3, 5
    private Map<Integer, Double> rastroSeguimiento;
    
    public DispersorRastroSeguimiento() {
        this.rastroSeguimiento = new HashMap<>();
    }
    
    @Override
    public void dispersarRastroDesdeCelula(Celula celulaDispersion, Portal destino,int offsetDireccion){
        super.modificarRastroDesdeCelula(celulaDispersion, offsetDireccion, destino, guiaDispersion, 1);
    }
    
    @Override
    public void reunirRastroDesdeCelula(Celula celulaDispersion,  Portal destino,int offsetDireccion){
        super.modificarRastroDesdeCelula(celulaDispersion, offsetDireccion, destino, guiaDispersion, -1);
    }

    @Override
    public void setRastroDeCelula(Celula celula, Portal destino, double valorRastro) {
        celula.setRastroSeguimiento(destino, valorRastro);
    }
    
    @Override
    public void setRastro(Portal destino, double valorRastro) {
        int idDestino = destino.getIndice();
        if (this.rastroSeguimiento.containsKey(idDestino)) {
            this.rastroSeguimiento.put(idDestino, this.rastroSeguimiento.get(idDestino) + valorRastro);
        } else {
            this.rastroSeguimiento.put(idDestino, valorRastro);
        }
    }
    
    @Override
    public double getRastroParaDestino(Portal destino){
        Double rastro = this.rastroSeguimiento.get(destino.getIndice());
        if(rastro == null)
            return 0;
        return rastro.doubleValue();
    }
    
    @Override
    public void reiniciar(){
        this.rastroSeguimiento = new HashMap<>();
    }
}
