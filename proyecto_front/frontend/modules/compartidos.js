export function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-share-alt me-2"></i>Documentos Compartidos</h3>
    <div class="alert alert-success mt-4">
      <b>Área personal de usuario.</b><br>
      Aquí puedes ver los documentos que te han compartido y gestionar permisos.
    </div>
    <div class="text-muted">(Módulo en desarrollo)</div>
  `;
} 