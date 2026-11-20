package sigb.vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import sigb.servicio.DevolucionService;
import sigb.servicio.ResultadoDevolucion;

/** Registro de devolución (RF-02) con generación de multa (RF-05). */
public class DevolucionDialog extends JDialog {

    private final DevolucionService servicio;
    private final JTextField txtCodigo = new JTextField(18);
    private final JLabel lblResultado = new JLabel(" ");

    public DevolucionDialog(Frame padre, DevolucionService servicio) {
        super(padre, "Registro de devolución", true);
        this.servicio = servicio;

        JPanel form = Ui.panelFormulario();
        Ui.fila(form, 0, "Código del Ejemplar:", txtCodigo);

        JButton btnRegistrar = new JButton("Registrar Devolución");
        JButton btnCancelar = new JButton("Cerrar");
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnRegistrar);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        botones.add(btnRegistrar);
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

    private void registrar() {
        try {
            ResultadoDevolucion r = servicio.registrarDevolucion(txtCodigo.getText());
            if (r.generoMulta()) {
                String valor = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
                        .format(r.multa().getValor());
                Ui.aviso(lblResultado, "Devolución registrada con " + r.diasRetraso()
                        + " día(s) de retraso. Se generó una multa de " + valor + ".");
            } else {
                Ui.exito(lblResultado, "Devolución registrada. El ejemplar quedó disponible.");
            }
            txtCodigo.setText("");
            txtCodigo.requestFocus();
        } catch (Exception ex) {
            Ui.error(lblResultado, ex);
        }
        pack();
    }
}
