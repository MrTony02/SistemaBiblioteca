package sigb.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import sigb.modelo.Prestamo;
import sigb.servicio.PrestamoService;

/** Prototipo 2: registro de préstamo. */
public class PrestamoDialog extends JDialog {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PrestamoService servicio;
    private final JTextField txtDocumento = new JTextField(18);
    private final JTextField txtCodigo = new JTextField(18);
    private final JLabel lblResultado = new JLabel(" ");

    public PrestamoDialog(Frame padre, PrestamoService servicio) {
        super(padre, "Registro de nuevo préstamo", true);
        this.servicio = servicio;

        JPanel form = Ui.panelFormulario();
        Ui.fila(form, 0, "Documento Estudiante:", txtDocumento);
        Ui.fila(form, 1, "Código del Ejemplar:", txtCodigo);

        JButton btnConfirmar = new JButton("Confirmar Préstamo");
        JButton btnCancelar = new JButton("Cancelar");
        btnConfirmar.addActionListener(e -> confirmar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnConfirmar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.add(btnConfirmar);
        botones.add(btnCancelar);

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(botones, BorderLayout.NORTH);
        sur.add(lblResultado, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        contenido.add(form, BorderLayout.NORTH);
        contenido.add(sur, BorderLayout.CENTER);
        setContentPane(contenido);

        pack();
        setLocationRelativeTo(padre);
    }

    private void confirmar() {
        try {
            Prestamo p = servicio.registrarPrestamo(txtDocumento.getText(), txtCodigo.getText());
            Ui.exito(lblResultado, "Préstamo exitoso. Fecha límite de devolución: "
                    + p.getFechaLimite().format(FORMATO) + ".");
            txtCodigo.setText("");
            txtCodigo.requestFocus();
        } catch (Exception ex) {
            Ui.error(lblResultado, ex);
        }
        pack();
    }
}
