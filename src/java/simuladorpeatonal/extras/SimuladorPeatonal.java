/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.extras;

import java.util.Random;
import simuladorpeatonal.simulacion.Simulador;

/**
 *
 * @author Julio
 */
public class SimuladorPeatonal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

	//#### IMPORTANT: Uncomment as appropiate #####

	//Create a new simulator. Both CSV files are the output of the simulation        
	Simulador simulador = new Simulador("celulas.csv", "vecindades.csv");

	//OPTIONAL: Save a created simulator on a PostgreSQL database
        //ControladorBD.guardarSimuladorBD(simulador, "Simulador Ejemplo");
       
	//OPTIONAL: Load a simulator from a PostgreSQL database
        //Simulador simulador = ControladorBD.cargarSimuladorBD(90);
        
	//Run simulation
	simulador.setTiempoSimulacion(100);
        simulador.simular();

        
        
    }
    
    public static int generaPeatones (int intervalo){
	/*TEST THIS FUNCTION USING THIS CODE IN MAIN
	int peatonesCreados = 0;
        int peatonesCreadosTotales = 0;
        for (int i = 0; i < 550; i++){
            peatonesCreados = generaPeatones(i);
            System.out.println(peatonesCreados);
            peatonesCreadosTotales += peatonesCreados;
        }
        System.out.println("Total: " + peatonesCreadosTotales);*/

        int peatonesCreados = (int)Math.floor(((intervalo-1) * 117) / (477 / 1.0));
        int peatonesAlMomento = (int) Math.floor((intervalo * 117) / ( 477/ 1.0));
        if (peatonesAlMomento >= 117)
            return 0;
        return Math.abs(peatonesAlMomento - peatonesCreados);
    }

    
    
    public static void probabilidadMovimientoAVecino(int celulaAnterior){
        int posicionVecinalCelulaAnterior = 0;
        int offsetPosicionVecinal = 0;
        double[] probabilidadMovimientoVecinal = new double[]{0.30,0.25,0.10,0.05,0.10,0.20};
        
        for(int i = 0; i < Simulador.TOTAL_VECINOS; i++){
            posicionVecinalCelulaAnterior = i;
            offsetPosicionVecinal = 3- posicionVecinalCelulaAnterior;
            System.out.println("Posicion Vecinal Anterior "+ posicionVecinalCelulaAnterior);
            for(int j = 0; j < Simulador.TOTAL_VECINOS; j++){
                int posicionVecinalConOffset = (j + offsetPosicionVecinal )% Simulador.TOTAL_VECINOS;
                if(posicionVecinalConOffset < 0){
                    posicionVecinalConOffset += Simulador.TOTAL_VECINOS;
                }
                System.out.println("Celula posible "+ j +" equivale a "+ posicionVecinalConOffset + " con probabilidad " + probabilidadMovimientoVecinal[posicionVecinalConOffset]);
            }
        }
    }

    public void QuickSort(int a[], int low, int high) {
        int pivot;
        if (high > low) {
            pivot = Partition(a, low, high);
            QuickSort(a, low, pivot - 1);
            QuickSort(a, pivot + 1, high);
        }
    }

    public int Partition(int a[], int low, int high) {
        int ultimaCerrada = low, primeraAbierta, pivot = a[low];
        primeraAbierta = low + 1;
        for (int i = low + 1; i <= high; i++) {
            if (a[i] <= pivot) {
                swap(a, i, primeraAbierta);
                ultimaCerrada = primeraAbierta;
                primeraAbierta++;
            }
        }

        swap(a, low, ultimaCerrada);

        return ultimaCerrada;
    }

    public void swap(int a[], int left, int right) {
        int temp = a[left];
        a[left] = a[right];
        a[right] = temp;
    }
}
