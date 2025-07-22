// Módulo de gestión de usuarios para el dashboard
const ROLES = ["USUARIO", "ADMINISTRADOR", "MODERADOR", "SUPERADMINISTRADOR"];

export async function render(container, { role, email, jwt }) {
  container.innerHTML = `
    <h3><i class="fas fa-users me-2"></i>Gestión de Usuarios</h3>
    <div class="alert alert-info mt-4">
      <b>Panel de administración de usuarios.</b><br>
      Aquí puedes ver y gestionar los usuarios del sistema.
    </div>
    <table class="table table-striped mt-4">
      <thead>
        <tr>
          <th>#</th>
          <th>Email</th>
          <th>Rol</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody id="usuarios-tbody">
        <tr>
          <td colspan="4" class="text-center text-muted">Cargando usuarios...</td>
        </tr>
      </tbody>
    </table>
  `;

  try {
    const response = await fetch("http://localhost:8080/usuarios", {
      headers: {
        Authorization: "Bearer " + jwt
      }
    });
    const tbody = container.querySelector('#usuarios-tbody');
    if (!response.ok) {
      tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">No se pudieron cargar los usuarios</td></tr>`;
      return;
    }
    const usuarios = await response.json();
    if (!usuarios.length) {
      tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">No hay usuarios registrados.</td></tr>`;
      return;
    }
    tbody.innerHTML = usuarios.map((u, i) => `
      <tr>
        <td>${i + 1}</td>
        <td>${u.email}</td>
        <td>
          <select class="form-select form-select-sm" data-user-id="${u.id}" data-current-rol="${u.role}">
            ${ROLES.map(r => `<option value="${r}" ${u.role === r ? 'selected' : ''}>${r}</option>`).join('')}
          </select>
        </td>
        <td>
          <button class="btn btn-primary btn-sm guardar-rol" data-user-id="${u.id}">Guardar</button>
        </td>
      </tr>
    `).join('');

    // Agregar listeners a los botones de guardar
    tbody.querySelectorAll('.guardar-rol').forEach(btn => {
      btn.addEventListener('click', async function() {
        const userId = this.getAttribute('data-user-id');
        const select = tbody.querySelector(`select[data-user-id='${userId}']`);
        const nuevoRol = select.value;
        const currentRol = select.getAttribute('data-current-rol');
        if (nuevoRol === currentRol) {
          alert('El usuario ya tiene ese rol.');
          return;
        }
        this.disabled = true;
        this.innerHTML = '<span class="spinner-border spinner-border-sm"></span>';
        try {
          const res = await fetch(`http://localhost:8080/usuarios/${userId}/rol`, {
            method: 'PATCH',
            headers: {
              'Content-Type': 'application/json',
              Authorization: 'Bearer ' + jwt
            },
            body: JSON.stringify({ nuevoRol })
          });
          if (res.ok) {
            select.setAttribute('data-current-rol', nuevoRol);
            alert('Rol actualizado correctamente.');
          } else {
            const err = await res.text();
            alert('Error al actualizar el rol: ' + err);
          }
        } catch (e) {
          alert('Error de red al actualizar el rol.');
        } finally {
          this.disabled = false;
          this.innerHTML = 'Guardar';
        }
      });
    });
  } catch (err) {
    const tbody = container.querySelector('#usuarios-tbody');
    tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger">Error de conexión</td></tr>`;
  }
} 