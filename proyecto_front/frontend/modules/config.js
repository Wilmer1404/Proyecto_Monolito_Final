export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-cogs me-2"></i>Configuración del Sistema</h3>
    <div class="alert alert-info mt-4">
      <b>Acceso exclusivo para Superadministrador.</b><br>
      Aquí podrás modificar parámetros críticos del sistema, gestionar backups, y más.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 