package sigb.servicio;

import java.util.List;
import sigb.dao.MultaDAO;
import sigb.dao.UsuarioDAO;
import sigb.modelo.Multa;
import sigb.modelo.Usuario;

/** Gestión de multas: consulta de pendientes y registro de pago (libera la RN-03). */
public class MultaService {

    private final UsuarioDAO usuarioDAO;
    private final MultaDAO multaDAO;

    public MultaService(UsuarioDAO usuarioDAO, MultaDAO multaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.multaDAO = multaDAO;
    }

    public List<Multa> listarPendientes(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe ingresar el documento del estudiante.");
        }
        Usuario usuario = usuarioDAO.buscarPorDocumento(documento.trim())
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe un usuario con documento " + documento.trim() + "."));
        return multaDAO.listarPendientesPorUsuario(usuario.getId());
    }

    public void registrarPago(int idMulta) {
        if (!multaDAO.marcarPagada(idMulta)) {
            throw new ReglaNegocioException("La multa no existe o ya estaba pagada.");
        }
    }
}
