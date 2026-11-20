package sigb.vista;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sigb.dao.PersistenciaException;
import sigb.servicio.ReglaNegocioException;

/** Utilidades compartidas por las pantallas. */
final class Ui {

    private static final Color VERDE = new Color(0, 110, 0);
    private static final Color ROJO = new Color(170, 0, 0);

    private Ui() {
    }

    static JPanel panelFormulario() {
        return new JPanel(new GridBagLayout());
    }

    /** Agrega una fila "etiqueta + campo" a un panel con GridBagLayout. */
    static void fila(JPanel panel, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = fila;
        g.insets = new Insets(5, 6, 5, 6);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        panel.add(new JLabel(etiqueta), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        panel.add(campo, g);
    }

    static void exito(JLabel etiqueta, String mensaje) {
        mostrar(etiqueta, VERDE, mensaje);
    }

    static void aviso(JLabel etiqueta, String mensaje) {
        mostrar(etiqueta, new Color(190, 100, 0), mensaje);
    }

    static void error(JLabel etiqueta, Exception ex) {
        String texto;
        if (ex instanceof ReglaNegocioException || ex instanceof IllegalArgumentException) {
            texto = ex.getMessage();
        } else if (ex instanceof PersistenciaException) {
            texto = "Error de base de datos. " + ex.getMessage();
        } else {
            texto = "Error inesperado: " + ex;
        }
        mostrar(etiqueta, ROJO, texto);
    }

    private static void mostrar(JLabel etiqueta, Color color, String texto) {
        etiqueta.setForeground(color);
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        etiqueta.setText("<html><body style='width:340px'>" + seguro + "</body></html>");
    }
}
