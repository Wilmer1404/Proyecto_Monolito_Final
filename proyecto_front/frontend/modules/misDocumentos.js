export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-file-alt me-2"></i>Mis Documentos</h3>
    <div class="alert alert-success mt-4">
      <b>Área personal de usuario.</b><br>
      Aquí puedes subir, editar, eliminar y compartir tus documentos.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 