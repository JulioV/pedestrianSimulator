/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simuladorpeatonal.extras;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simuladorpeatonal.simulacion.Simulador;

/**
 *
 * @author Julio
 */
public class ControladorBD {

    private static String URL = "jdbc:postgresql://localhost/SimuladorPeatonal";
    private static String USUARIO = "postgres";
    private static String PASSWORD = "postgres";

    public static void guardarSimuladorBD(Simulador simulador, String nombre) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(simulador);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            try (Connection connection = DriverManager.getConnection(URL, USUARIO, PASSWORD)) {
                String consulta = "INSERT INTO simulador ( objeto,nombre) VALUES(?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(consulta)) {
                    preparedStatement.setBinaryStream(1, bais, baos.toByteArray().length);
                    preparedStatement.setString(2, nombre);
                    preparedStatement.executeUpdate();
                }
            }

        } catch (IOException | SQLException ex) {
            Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static Simulador cargarSimuladorBD(Integer indice) {
        ObjectInputStream ois = null;
        try {
            Class.forName("org.postgresql.Driver");
            try (Connection connection = DriverManager.getConnection(URL, USUARIO, PASSWORD)) {

                String consulta = "SELECT objeto from simulador where indice = (Select MAX(indice) from simulador) ";
                try (PreparedStatement preparedStatement = connection.prepareStatement(consulta)) {

                    //preparedStatement.setInt(1, indice);

                    ResultSet rs = preparedStatement.executeQuery();
                    rs.next();
                    ois = new ObjectInputStream(rs.getBinaryStream(1));
                    preparedStatement.close();
                    return (Simulador) ois.readObject();
                }
            }
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
        
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ControladorBD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;

    }
}
