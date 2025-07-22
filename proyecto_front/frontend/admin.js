const API_URL = "http://localhost:8080";

const ROLES = [
  "USUARIO",
  "MODERADOR",
  "ADMINISTRADOR",
  "SUPERADMINISTRADOR"
];

// Verifica autenticación y rol
const jwt = localStorage.getItem("jwt");
const role = localStorage.getItem("role");
if (!jwt || (role !== "ADMINISTRADOR" && role !== "SUPERADMINISTRADOR")) {
  window.location.href = "index.html";
}

const tablaUsuarios = document.getElementById("tablaUsuarios").querySelector("tbody");
const adminMsg = document.getElementById("adminMsg");

// Cargar usuarios
async function cargarUsuarios() {
  tablaUsuarios.innerHTML = "<tr><td colspan='5' class='text-center'>Cargando...</td></tr>";
  try {
    const response = await fetch(`${API_URL}/usuarios`, {
      headers: { Authorization: "Bearer " + jwt }
    });
    if (!response.ok) throw new Error("No se pudo cargar usuarios");
    const usuarios = await response.json();
    renderUsuarios(usuarios);
  } catch (error) {
    adminMsg.innerHTML = `<div class='alert alert-danger'>${error.message}</div>`;
  }
}

function renderUsuarios(usuarios) {
  tablaUsuarios.innerHTML = "";
  usuarios.forEach((u) => {
    const fila = document.createElement("tr");
    fila.innerHTML = `
      <td>${u.id}</td>
      <td>${u.email}</td>
      <td><span class="badge bg-primary">${u.role}</span></td>
      <td>
        <select class="form-select form-select-sm" id="rol-${u.id}">
          ${ROLES.map(
            (r) => `<option value="${r}" ${u.role === r ? "selected" : ""}>${r}</option>`
          ).join("")}
        </select>
      </td>
      <td>
        <button class="btn btn-success btn-sm" onclick="cambiarRol(${u.id})">
          <i class="fas fa-save"></i> Guardar
        </button>
      </td>
    `;
    tablaUsuarios.appendChild(fila);
  });
}

window.cambiarRol = async function (userId) {
  const select = document.getElementById(`rol-${userId}`);
  const nuevoRol = select.value;
  try {
    const response = await fetch(`${API_URL}/usuarios/${userId}/rol`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + jwt
      },
      body: JSON.stringify({ nuevoRol })
    });
    if (response.ok) {
      adminMsg.innerHTML = `<div class='alert alert-success'>Rol actualizado correctamente</div>`;
      await cargarUsuarios();
    } else {
      const error = await response.text();
      adminMsg.innerHTML = `<div class='alert alert-danger'>${error}</div>`;
    }
  } catch (error) {
    adminMsg.innerHTML = `<div class='alert alert-danger'>${error.message}</div>`;
  }
};

// Inicializar
cargarUsuarios(); 