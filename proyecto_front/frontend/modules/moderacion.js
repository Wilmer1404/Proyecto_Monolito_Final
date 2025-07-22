export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-gavel me-2"></i>Moderación de Documentos Reportados</h3>
    <div class="alert alert-warning mt-4">
      <b>Acceso exclusivo para Moderador.</b><br>
      Aquí puedes ver documentos reportados, revisarlos temporalmente y eliminarlos si es necesario. Toda acción será registrada.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 