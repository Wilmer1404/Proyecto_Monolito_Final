export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-clipboard-list me-2"></i>Logs del Sistema</h3>
    <div class="alert alert-info mt-4">
      <b>Acceso según nivel:</b> Superadmin ve todos los logs, Admin solo logs generales.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 