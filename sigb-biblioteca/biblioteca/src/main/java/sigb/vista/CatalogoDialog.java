package sigb.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sigb.modelo.Material;
import sigb.servicio.CatalogoService;

/** Prototipo 3: catálogo y disponibilidad (RF-03). */
public class CatalogoDialog extends JDialog {

    private final CatalogoService servicio;
    private final JTextField txtBuscar = new JTextField(22);
    private final JLabel lblMensaje = new JLabel(" ");
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID", "Título del Libro", "Autor", "Disponibles", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    public CatalogoDialog(Frame padre, CatalogoService servicio) {
        super(padre, "Catálogo y disponibilidad", true);
        this.servicio = servicio;

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        norte.add(new JLabel("Buscar título:"));
        norte.add(txtBuscar);
        norte.add(btnBuscar);

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(620, 240));

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> dispose());
        JPanel sur = new JPanel(new BorderLayout());
        sur.add(lblMensaje, BorderLayout.CENTER);
        sur.add(btnVolver, BorderLayout.EAST);

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        contenido.add(norte, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);
        contenido.add(sur, BorderLayout.SOUTH);
        setContentPane(contenido);

        pack();
        setLocationRelativeTo(padre);
        buscar();
    }

    private void buscar() {
        try {
            modelo.setRowCount(0);
            for (Material m : servicio.buscar(txtBuscar.getText())) {
                modelo.addRow(new Object[] {
                    m.getId(),
                    m.getTitulo(),
                    m.getAutor(),
                    m.contarDisponibles() + " de " + m.getEjemplares().size(),
                    m.consultarDisponibilidad() ? "Disponible" : "No disponible"
                });
            }
            if (modelo.getRowCount() == 0) {
                Ui.error(lblMensaje, new IllegalArgumentException("No se encontraron materiales."));
            } else {
                lblMensaje.setText(" ");
            }
        } catch (Exception ex) {
            Ui.error(lblMensaje, ex);
        }
    }
}
