package sigb.vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sigb.modelo.Multa;
import sigb.servicio.MultaService;

/** Gestión de multas: ver las pendientes de un estudiante y registrar su pago. */
public class MultasDialog extends JDialog {

    private final MultaService servicio;
    private final JTextField txtDocumento = new JTextField(16);
    private final JLabel lblMensaje = new JLabel(" ");
    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[] {"ID Multa", "ID Préstamo", "Valor", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);

    public MultasDialog(Frame padre, MultaService servicio) {
        super(padre, "Gestión de multas", true);
        this.servicio = servicio;

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> cargar());
        txtDocumento.addActionListener(e -> cargar());

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        norte.add(new JLabel("Documento Estudiante:"));
        norte.add(txtDocumento);
        norte.add(btnBuscar);

        tabla.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(520, 200));

        JButton btnPagar = new JButton("Registrar pago");
        JButton btnCerrar = new JButton("Volver al Menú");
        btnPagar.addActionListener(e -> pagar());
        btnCerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        botones.add(btnPagar);
        botones.add(btnCerrar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(lblMensaje, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);

        JPanel contenido = new JPanel(new BorderLayout(0, 6));
        contenido.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        contenido.add(norte, BorderLayout.NORTH);
        contenido.add(scroll, BorderLayout.CENTER);
        contenido.add(sur, BorderLayout.SOUTH);
        setContentPane(contenido);

        pack();
        setLocationRelativeTo(padre);
    }

    private void cargar() {
        try {
            modelo.setRowCount(0);
            for (Multa m : servicio.listarPendientes(txtDocumento.getText())) {
                modelo.addRow(new Object[] {m.getId(), m.getIdPrestamo(), moneda.format(m.getValor()), "Pendiente"});
            }
            if (modelo.getRowCount() == 0) {
                Ui.exito(lblMensaje, "El estudiante no tiene multas pendientes.");
            } else {
                lblMensaje.setText(" ");
            }
        } catch (Exception ex) {
            Ui.error(lblMensaje, ex);
        }
    }

    private void pagar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            Ui.error(lblMensaje, new IllegalArgumentException("Seleccione una multa de la tabla."));
            return;
        }
        try {
            servicio.registrarPago((Integer) modelo.getValueAt(fila, 0));
            cargar();
            Ui.exito(lblMensaje, "Pago registrado.");
        } catch (Exception ex) {
            Ui.error(lblMensaje, ex);
        }
    }
}
