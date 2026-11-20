package sigb;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sigb.config.Servicios;
import sigb.dao.jdbc.ConexionBD;
import sigb.vista.MenuPrincipal;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignorada) {
            // se usa el aspecto por defecto de Swing
        }

        SwingUtilities.invokeLater(() -> {
            try {
                ConexionBD.probarConexion();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar a la base de datos.\n"
                                + "Revise db.properties (url, usuario, contraseña) y que el servidor esté activo.\n\n"
                                + ex.getMessage(),
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            new MenuPrincipal(Servicios.crearConJdbc()).setVisible(true);
        });
    }
}
