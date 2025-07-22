const role = localStorage.getItem('role');
const email = localStorage.getItem('email');
const jwt = localStorage.getItem('jwt');

const modulesByRole = {
  SUPERADMINISTRADOR: ['config', 'usuarios', 'logs', 'auditoria', 'documentos'],
  ADMINISTRADOR: ['usuarios', 'reportes', 'logs'],
  MODERADOR: ['moderacion'],
  USUARIO: ['misDocumentos', 'compartidos']
};

const menuConfig = {
  config: { icon: 'fa-cogs', label: 'Configuración' },
  usuarios: { icon: 'fa-users', label: 'Gestión de Usuarios' },
  logs: { icon: 'fa-clipboard-list', label: 'Logs' },
  auditoria: { icon: 'fa-search', label: 'Auditoría' },
  reportes: { icon: 'fa-flag', label: 'Reportes' },
  moderacion: { icon: 'fa-gavel', label: 'Moderación' },
  misDocumentos: { icon: 'fa-file-alt', label: 'Mis Documentos' },
  compartidos: { icon: 'fa-share-alt', label: 'Compartidos' }
};

function renderSidebar(role) {
  const sidebar = document.getElementById('sidebar');
  sidebar.innerHTML = '';
  const userInfo = document.createElement('div');
  userInfo.className = 'sidebar-user-info p-3 border-bottom';
  userInfo.innerHTML = `
    <div class="fw-bold mb-1"><i class="fas fa-user-circle me-2"></i>${email}</div>
    <div class="badge bg-${getRoleColor(role)}">${role}</div>
    <button class="btn btn-outline-danger btn-sm mt-2 w-100" onclick="logout()"><i class="fas fa-sign-out-alt me-1"></i>Salir</button>
  `;
  sidebar.appendChild(userInfo);
  const menu = document.createElement('ul');
  menu.className = 'nav flex-column mt-3';
  (modulesByRole[role] || []).forEach((mod) => {
    const item = document.createElement('li');
    item.className = 'nav-item';
    item.innerHTML = `
      <a href="#" class="nav-link" onclick="loadModule('${mod}')">
        <i class="fas ${menuConfig[mod].icon} me-2"></i>${menuConfig[mod].label}
      </a>
    `;
    menu.appendChild(item);
  });
  sidebar.appendChild(menu);
}

window.loadModule = function(moduleName) {
  const main = document.getElementById('main-content');
  main.innerHTML = '<div class="text-center p-5"><div class="spinner-border"></div></div>';
  import(`./modules/${moduleName}.js`)
    .then((mod) => {
      main.innerHTML = '';
      mod.render(main, { role, email, jwt });
    })
    .catch(() => {
      main.innerHTML = `<div class='alert alert-danger'>No se pudo cargar el módulo <b>${moduleName}</b></div>`;
    });
};

function getRoleColor(role) {
  switch (role) {
    case 'SUPERADMINISTRADOR': return 'primary';
    case 'ADMINISTRADOR': return 'purple';
    case 'MODERADOR': return 'warning';
    case 'USUARIO': return 'success';
    default: return 'secondary';
  }
}

window.logout = function() {
  localStorage.clear();
  window.location.href = 'index.html';
};

// Inicializar
if (!role || !jwt) {
  window.location.href = 'index.html';
} else {
  renderSidebar(role);
  loadModule(modulesByRole[role][0]);
} 