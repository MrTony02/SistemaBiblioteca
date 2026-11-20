package sigb.vista;

import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import sigb.config.Servicios;

/** Prototipo 1: menú principal. */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal(Servicios servicios) {
        super("SIGB - Sistema de Biblioteca");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("SISTEMA DE BIBLIOTECA", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));

        JButton btnPrestamo = new JButton("Realizar Préstamo");
        JButton btnDevolucion = new JButton("Registrar Devolución");
        JButton btnCatalogo = new JButton("Consultar Catálogo");
        JButton btnMultas = new JButton("Gestión de Multas");
        JButton btnSalir = new JButton("Salir del Sistema");

        btnPrestamo.addActionListener(e -> new PrestamoDialog(this, servicios.prestamos()).setVisible(true));
        btnDevolucion.addActionListener(e -> new DevolucionDialog(this, servicios.devoluciones()).setVisible(true));
        btnCatalogo.addActionListener(e -> new CatalogoDialog(this, servicios.catalogo()).setVisible(true));
        btnMultas.addActionListener(e -> new MultasDialog(this, servicios.multas()).setVisible(true));
        btnSalir.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 40, 18, 40));
        panel.add(titulo);
        panel.add(btnPrestamo);
        panel.add(btnDevolucion);
        panel.add(btnCatalogo);
        panel.add(btnMultas);
        panel.add(btnSalir);
        setContentPane(panel);

        pack();
        setLocationRelativeTo(null);
    }
}
