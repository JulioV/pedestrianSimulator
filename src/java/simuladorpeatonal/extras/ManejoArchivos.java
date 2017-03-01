/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.extras;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import simuladorpeatonal.simulacion.celulas.Celula;
import simuladorpeatonal.simulacion.celulas.Celulas;
import simuladorpeatonal.simulacion.celulas.Portales;

public class ManejoArchivos {

    private String rutaArchivoCelulas;
    private String rutaArchivoVecindades;
    private static final double DISTANCIA_ENTRE_PUNTOS = 38.97;//51.96,38.97
    private static final Hashtable<String, Integer> relacionesEliminadas = new Hashtable<String, Integer>() {
        {

            put("10 11", 1);

            put("11 10", 1);
            put("53 10", 1);

            put("10 53", 1);
            put("52 53", 1);

            put("53 52", 1);
            put("222 223", 1);

            put("223 222", 1);
            put("223 265", 1);

            put("265 223", 1);
            put("265 266", 1);

            put("266 265", 1);
        }
    };

    public ManejoArchivos(String rutaArchivoCelulas, String rutaArchivoVecindades) {
        this.rutaArchivoCelulas = rutaArchivoCelulas;
        this.rutaArchivoVecindades = rutaArchivoVecindades;
    }

    public static void escribirBitacoraArchivo(String rutaArchivo, ArrayList<String> bitacora) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(rutaArchivo));
            System.out.println(System.getProperty("user.dir"));
            pw.println("ID_Peaton Frame X_POS Y_POS Rapidez");
            for (String linea : bitacora) {
                pw.println(linea);
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ManejoArchivos.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }

    }

    public void leerCelulas(Portales destinos, Celulas celulas) {

        BufferedReader br = null;
        String linea;
        String separador = ",";

        try {

            br = new BufferedReader(new FileReader(rutaArchivoCelulas));
            while ((linea = br.readLine()) != null) {
                //ID X Y PUERTA_0/1 ID_PUERTA
                String[] celula = linea.split(separador);
                celulas.agregarCelula(new Celula(Integer.parseInt(celula[0]), Double.parseDouble(celula[1]) / 100.0, Double.parseDouble(celula[2]) / 100.0));


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void leerVecindades(Celulas celulas) {
        BufferedReader br = null;
        String linea;
        String separador = ",";

        try {

            DecimalFormat df = new DecimalFormat("0.00");
            df.setRoundingMode(RoundingMode.HALF_EVEN);

            br = new BufferedReader(new FileReader(rutaArchivoVecindades));
            while ((linea = br.readLine()) != null) {
                String[] vecindad = linea.split(separador);
                Celula origen = celulas.getCelula(Integer.parseInt(vecindad[0]));
                Celula destino = celulas.getCelula(Integer.parseInt(vecindad[1]));
                double distancia = (double) (Math.round((Double.parseDouble(vecindad[2])) * 100.0) / 100.0);

                if (distancia == DISTANCIA_ENTRE_PUNTOS && origen != null && destino != null && !esRelacionEliminada(origen.getIndice(), destino.getIndice())) {
                    origen.setVecino(posicionVecino(origen, destino), destino);
                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Done");
    }

    private boolean esRelacionEliminada(int idOrigen, int idDestino) {
        return ManejoArchivos.relacionesEliminadas.containsKey(idOrigen + " " + idDestino);
    }

    private int posicionVecino(Celula celula, Celula vecino) {

        if (celula.getX() == vecino.getX() && vecino.getY() > celula.getY()) {
            return 0;
        } else if (vecino.getX() > celula.getX() && vecino.getY() > celula.getY()) {
            return 1;
        } else if (vecino.getX() > celula.getX() && vecino.getY() < celula.getY()) {
            return 2;
        } else if (vecino.getX() == celula.getX() && vecino.getY() < celula.getY()) {
            return 3;
        } else if (vecino.getX() < celula.getX() && vecino.getY() < celula.getY()) {
            return 4;
        } else if (vecino.getX() < celula.getX() && vecino.getY() > celula.getY()) {
            return 5;
        }
        return -1;
    }
}