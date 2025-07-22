export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-search me-2"></i>Panel de Auditoría</h3>
    <div class="alert alert-info mt-4">
      <b>Acceso exclusivo para Superadministrador.</b><br>
      Aquí puedes auditar acciones, cambios y accesos en el sistema.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 