export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-flag me-2"></i>Panel de Reportes</h3>
    <div class="alert alert-info mt-4">
      <b>Acceso para Administrador.</b><br>
      Aquí puedes ver y gestionar reportes de contenido.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 