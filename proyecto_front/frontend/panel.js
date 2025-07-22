// Configuración de la API
const API_URL = "http://localhost:8080";

// Clase principal para manejar el panel
class PanelManager {
  constructor() {
    this.jwt = localStorage.getItem("jwt");
    this.email = localStorage.getItem("email") || "";
    this.role = localStorage.getItem("role") || "";

    this.initializePanel();
    this.setupEventListeners();
  }

  // Inicializar el panel
  initializePanel() {
    // Verificar autenticación
    if (!this.jwt || !this.role) {
      window.location.href = "index.html";
      return;
    }

    // Mostrar información del usuario
    this.displayUserInfo();

    // Cargar todos los datos
    this.loadAllData();
  }

  // Mostrar información del usuario
  displayUserInfo() {
    const bienvenida = document.getElementById("bienvenida");
    if (bienvenida) {
      bienvenida.innerHTML = `
                <div class="welcome-message">
                    <div class="alert alert-info">
                        <i class="fas fa-user-circle me-2"></i>
                        Bienvenido <b>${this.email}</b> | Rol: <b>${this.role}</b>
                    </div>
                </div>
            `;
    }
  }

  // Configurar event listeners
  setupEventListeners() {
    // Botón de logout
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", () => this.logout());
    }

    // Formulario de búsqueda
    const buscadorForm = document.getElementById("buscadorForm");
    if (buscadorForm) {
      buscadorForm.addEventListener("submit", (e) => this.handleSearch(e));
    }

    // Formulario de crear documento
    const crearDocForm = document.getElementById("crearDocForm");
    if (crearDocForm) {
      crearDocForm.addEventListener("submit", (e) =>
        this.handleCreateDocument(e)
      );
    }

    // Formulario de invitar usuario
    const invitarUsuarioForm = document.getElementById("invitarUsuarioForm");
    if (invitarUsuarioForm) {
      invitarUsuarioForm.addEventListener("submit", (e) =>
        this.handleInvitarUsuario(e)
      );
    }
  }

  // Abrir modal para invitar usuario
  invitarUsuario(documentoId) {
    document.getElementById("documentoIdInvitar").value = documentoId;
    document.getElementById("correoInvitado").value = "";
    document.getElementById("rolInvitado").value = "";
    const modal = new bootstrap.Modal(
      document.getElementById("invitarUsuarioModal")
    );
    modal.show();
  }

  // Manejar el envío del formulario de invitación
  async handleInvitarUsuario(e) {
    e.preventDefault();
    const form = e.target;
    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }
    const documentoId = document.getElementById("documentoIdInvitar").value;
    const correoInvitado = document.getElementById("correoInvitado").value;
    const rol = document.getElementById("rolInvitado").value;
    const submitBtn = form.querySelector('button[type="submit"]');
    this.setLoadingState(submitBtn, true);
    try {
      const response = await fetch(
        `${API_URL}/documentos/${documentoId}/invitar?correoInvitado=${encodeURIComponent(
          correoInvitado
        )}&rol=${rol}`,
        {
          method: "POST",
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (response.ok) {
        this.showToast("Invitación enviada correctamente", "success");
        const modal = bootstrap.Modal.getInstance(
          document.getElementById("invitarUsuarioModal")
        );
        if (modal) modal.hide();
        form.reset();
        form.classList.remove("was-validated");
        await this.cargarInvitaciones();
      } else {
        const error = await response.text();
        this.showToast(error || "Error al enviar la invitación", "danger");
      }
    } catch (error) {
      this.showToast("Error al enviar la invitación", "danger");
    } finally {
      this.setLoadingState(submitBtn, false);
    }
  }

  // Cargar todos los datos
  async loadAllData() {
    try {
      await Promise.all([
        this.cargarMisDocumentos(),
        this.cargarCompartidos(),
        this.cargarInvitaciones(),
        this.cargarSuscripciones(),
      ]);
    } catch (error) {
      console.error("Error cargando datos:", error);
      this.showToast("Error al cargar los datos", "danger");
    }
  }

  // Cargar documentos propios con paginación
  async cargarMisDocumentos(page = 0) {
    try {
      const response = await fetch(
        `${API_URL}/documentos?correo=${encodeURIComponent(this.email)}&page=${page}&size=5&sort=fechaCreacion,desc`,
        {
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const ul = document.getElementById("misDocumentos");
      if (ul) {
        ul.innerHTML = "";
        const documentos = data.content || [];
        if (documentos.length === 0) {
          ul.innerHTML = `
            <li class="list-group-item text-center text-muted">
              <i class="fas fa-file-alt me-2"></i>
              No tienes documentos creados
            </li>
          `;
        } else {
          documentos.forEach((doc) => {
            const fecha = doc.fechaCreacion
              ? new Date(doc.fechaCreacion).toLocaleDateString("es-ES")
              : "";
            ul.innerHTML += `
              <li class="list-group-item d-flex justify-content-between align-items-center">
                <div>
                  <i class="fas fa-file-alt me-2 text-primary"></i>
                  <span class="fw-bold">${doc.titulo}</span>
                  ${doc.publico ? '<span class="badge bg-success ms-2">Público</span>' : '<span class="badge bg-secondary ms-2">Privado</span>'}
                  ${doc.rutaArchivo ? `<a href=\"${API_URL}/documentos/${doc.id}/archivo\" class=\"ms-2 btn btn-sm btn-outline-secondary\" target=\"_blank\"><i class=\"fas fa-download\"></i> Descargar archivo</a>` : ''}
                </div>
                <div class="d-flex align-items-center gap-2">
                  <span class="text-muted small">
                    <i class="fas fa-calendar me-1"></i>
                    ${fecha}
                  </span>
                  <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary btn-sm" onclick="panelManager.verDocumento(${doc.id})" title="Ver documento">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-outline-success btn-sm" onclick="panelManager.invitarUsuario(${doc.id})" title="Invitar usuario">
                      <i class="fas fa-user-plus"></i>
                    </button>
                    <button class="btn btn-outline-warning btn-sm" onclick="panelManager.editarDocumento(${doc.id})" title="Editar">
                      <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-outline-danger btn-sm" onclick="panelManager.eliminarDocumento(${doc.id})" title="Eliminar">
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </div>
              </li>
            `;
          });
        }
        this.mostrarPaginacion('paginacion-misDocumentos', data.totalPages, page, 'cargarMisDocumentos');
      }
    } catch (error) {
      console.error("Error cargando mis documentos:", error);
      this.showToast("Error al cargar tus documentos", "danger");
    }
  }

  // Cargar documentos compartidos con paginación
  async cargarCompartidos(page = 0) {
    try {
      const response = await fetch(
        `${API_URL}/documentos/compartidos?correoInvitado=${encodeURIComponent(this.email)}&page=${page}&size=5&sort=fechaCreacion,desc`,
        {
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const ul = document.getElementById("compartidos");
      if (ul) {
        ul.innerHTML = "";
        const documentos = data.content || [];
        if (documentos.length === 0) {
          ul.innerHTML = `
            <li class="list-group-item text-center text-muted">
              <i class="fas fa-share-alt me-2"></i>
              No tienes documentos compartidos
            </li>
          `;
        } else {
          documentos.forEach((doc) => {
            const fecha = doc.fechaCreacion
              ? new Date(doc.fechaCreacion).toLocaleDateString("es-ES")
              : "";
            let botones = `
              <button class="btn btn-outline-primary btn-sm" onclick="panelManager.verDocumento(${doc.id})" title="Ver documento">
                <i class="fas fa-eye"></i>
              </button>
            `;

            // Si el usuario es EDITOR, muestra el botón de editar
            if (doc.rolInvitacion === "EDITOR") {
              botones += `
                <button class="btn btn-outline-success btn-sm ms-2" onclick="panelManager.editarDocumento(${doc.id})" title="Editar">
                  <i class="fas fa-edit"></i>
                </button>
              `;
            }
            ul.innerHTML += `
              <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <i class="fas fa-share-alt me-2 text-success"></i>
                    <span class="fw-bold">${doc.titulo}</span>
                    <span class="text-muted small d-block">
                      <i class="fas fa-user me-1"></i>
                      Autor: ${doc.autorCorreo}
                    </span>
                    <span class="text-muted small">
                      <i class="fas fa-calendar me-1"></i>
                      ${fecha}
                    </span>
                  </div>
                  ${botones}
                </div>
              </li>
            `;
          });
        }
        this.mostrarPaginacion('paginacion-compartidos', data.totalPages, page, 'cargarCompartidos');
      }
    } catch (error) {
      console.error("Error cargando documentos compartidos:", error);
      this.showToast("Error al cargar documentos compartidos", "danger");
    }
  }

  // Cargar invitaciones aceptadas con paginación
  async cargarInvitaciones(page = 0) {
    try {
      const response = await fetch(
        `${API_URL}/documentos/invitaciones?correo=${encodeURIComponent(this.email)}&page=${page}&size=5&sort=id,desc`,
        {
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const ul = document.getElementById("invitaciones");
      if (ul) {
        ul.innerHTML = "";
        const invitaciones = data.content || [];
        if (invitaciones.length === 0) {
          ul.innerHTML = `
            <li class="list-group-item text-center text-muted">
              <i class="fas fa-envelope me-2"></i>
              No tienes invitaciones
            </li>
          `;
        } else {
          invitaciones.forEach((inv) => {
            ul.innerHTML += `
              <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <i class="fas fa-envelope me-2 ${inv.aceptada ? 'text-success' : 'text-warning'}"></i>
                    <span class="fw-bold">${inv.rol}</span> - ${inv.correoInvitado}
                    ${inv.aceptada ? '<span class="badge bg-success ms-2">Aceptada</span>' : '<span class="badge bg-warning ms-2">Pendiente</span>'}
                  </div>
                  ${!inv.aceptada ? `<button class="btn btn-success btn-sm" onclick="panelManager.aceptarInvitacion(${inv.documentoId}, '${inv.correoInvitado}')" title="Aceptar invitación"><i class="fas fa-check"></i></button>` : ""}
                </div>
              </li>
            `;
          });
        }
        this.mostrarPaginacion('paginacion-invitaciones', data.totalPages, page, 'cargarInvitaciones');
      }
    } catch (error) {
      console.error("Error cargando invitaciones:", error);
      this.showToast("Error al cargar invitaciones", "danger");
    }
  }

  // Cargar suscripciones con paginación
  async cargarSuscripciones(page = 0) {
    try {
      const response = await fetch(
        `${API_URL}/documentos/suscripciones?correo=${encodeURIComponent(this.email)}&page=${page}&size=5&sort=id,desc`,
        {
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const ul = document.getElementById("suscripciones");
      if (ul) {
        ul.innerHTML = "";
        const suscripciones = data.content || [];
        if (suscripciones.length === 0) {
          ul.innerHTML = `
            <li class="list-group-item text-center text-muted">
              <i class="fas fa-bell me-2"></i>
              No tienes suscripciones
            </li>
          `;
        } else {
          suscripciones.forEach((sub) => {
            ul.innerHTML += `
              <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <i class="fas fa-bell me-2 text-info"></i>
                    <span class="fw-bold">${sub.titulo}</span>
                    <span class="text-muted small d-block">
                      <i class="fas fa-tag me-1"></i>
                      ${sub.categoria || "Sin categoría"}
                    </span>
                  </div>
                  <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary btn-sm" onclick="panelManager.verDocumento(${sub.documentoId})" title="Ver documento">
                      <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-outline-danger btn-sm" onclick="panelManager.cancelarSuscripcion(${sub.documentoId})" title="Cancelar suscripción">
                      <i class="fas fa-times"></i>
                    </button>
                  </div>
                </div>
              </li>
            `;
          });
        }
        this.mostrarPaginacion('paginacion-suscripciones', data.totalPages, page, 'cargarSuscripciones');
      }
    } catch (error) {
      console.error("Error cargando suscripciones:", error);
      this.showToast("Error al cargar suscripciones", "danger");
    }
  }

  // Manejar búsqueda
  async handleSearch(e) {
    e.preventDefault();
    const titulo = document.getElementById("buscaTitulo").value;
    // Solo buscar por título
    try {
      const params = new URLSearchParams();
      if (titulo) params.append("titulo", titulo);
      const response = await fetch(
        `${API_URL}/documentos/buscar?${params.toString()}`,
        {
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      const ul = document.getElementById("busquedaResultados");
      if (ul) {
        ul.innerHTML = "";
        const documentos = data.content || [];
        if (documentos.length === 0) {
          ul.innerHTML = `
            <li class="list-group-item text-center text-muted">
              <i class="fas fa-info-circle me-2"></i>
              No se encontraron documentos
            </li>
          `;
        } else {
          documentos.forEach((doc) => {
            const fecha = doc.fechaCreacion
              ? new Date(doc.fechaCreacion).toLocaleDateString("es-ES")
              : "";
            ul.innerHTML += `
              <li class="list-group-item">
                <div class="d-flex justify-content-between align-items-center">
                  <div>
                    <i class="fas fa-file-alt me-2 text-primary"></i>
                    <span class="fw-bold">${doc.titulo}</span>
                    <span class="text-muted small d-block">
                      <i class="fas fa-user me-1"></i>
                      Autor: ${doc.autorCorreo}
                    </span>
                    <span class="text-muted small">
                      <i class="fas fa-calendar me-1"></i>
                      ${fecha}
                    </span>
                  </div>
                  <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary btn-sm" onclick="panelManager.verDocumento(${doc.id})" title="Ver documento">
                      <i class="fas fa-eye"></i>
                    </button>
                    ${doc.publico ? `<button class="btn btn-outline-success btn-sm" onclick="panelManager.suscribirseDocumento(${doc.id})" title="Suscribirse"><i class="fas fa-bell"></i></button>` : ""}
                  </div>
                </div>
              </li>
            `;
          });
        }
      }
    } catch (error) {
      console.error("Error en búsqueda:", error);
      this.showToast("Error al realizar la búsqueda", "danger");
    }
  }

  // Manejar creación de documento
  async handleCreateDocument(e) {
    e.preventDefault();
    const form = e.target;

    if (!form.checkValidity()) {
      form.classList.add("was-validated");
      return;
    }

    const titulo = document.getElementById("docTitulo").value;
    const publico = document.getElementById("docPublico").value === "true";
    const archivoInput = document.getElementById("docArchivo");
    const archivo = archivoInput && archivoInput.files.length > 0 ? archivoInput.files[0] : null;
    const submitBtn = form.querySelector('button[type="submit"]');

    // Mostrar estado de carga
    this.setLoadingState(submitBtn, true);

    try {
      const formData = new FormData();
      formData.append("titulo", titulo);
      formData.append("publico", publico);
      if (archivo) {
        formData.append("archivo", archivo);
      }
      const response = await fetch(`${API_URL}/documentos`, {
        method: "POST",
        headers: {
          Authorization: "Bearer " + this.jwt,
          "X-Autor-Correo": this.email,
        },
        body: formData,
      });

      if (response.ok) {
        this.showToast("Documento creado exitosamente", "success");
        // Refrescar la lista de documentos
        await this.cargarMisDocumentos();
        // Cerrar el modal si existe
        const modal = bootstrap.Modal.getInstance(document.getElementById("crearDocModal"));
        if (modal) modal.hide();
      } else {
        const error = await response.text();
        this.showToast(error || "Error al crear el documento", "danger");
      }
    } catch (error) {
      console.error("Error creando documento:", error);
      this.showToast("Error al crear el documento", "danger");
    } finally {
      this.setLoadingState(submitBtn, false);
    }
  }

  // Funciones adicionales para interactuar con documentos

  // Ver documento
  async verDocumento(id) {
    try {
      const response = await fetch(`${API_URL}/documentos/${id}`, {
        headers: {
          Authorization: "Bearer " + this.jwt,
        },
      });
      if (response.ok) {
        const doc = await response.json();
        if (doc.rutaArchivo) {
          // Descargar el archivo usando fetch con JWT y abrirlo en una nueva pestaña
          const archivoResp = await fetch(`${API_URL}/documentos/${doc.id}/archivo`, {
            headers: {
              Authorization: "Bearer " + this.jwt,
            },
          });
          if (archivoResp.ok) {
            const blob = await archivoResp.blob();
            const url = window.URL.createObjectURL(blob);
            window.open(url, '_blank');
            // Liberar el objeto URL después de un tiempo
            setTimeout(() => window.URL.revokeObjectURL(url), 10000);
          } else {
            this.showToast("No tienes permiso para ver el archivo o no existe.", "danger");
          }
        } else {
          this.showToast(`No hay archivo adjunto para este documento`, "info");
        }
      } else {
        this.showToast("Error al cargar el documento", "danger");
      }
    } catch (error) {
      console.error("Error viendo documento:", error);
      this.showToast("Error al cargar el documento", "danger");
    }
  }

  // Editar documento
  async editarDocumento(id) {
    try {
      // Obtener datos del documento
      const response = await fetch(`${API_URL}/documentos/${id}`, {
        headers: {
          Authorization: "Bearer " + this.jwt,
        },
      });
      if (!response.ok) {
        this.showToast("Error al cargar el documento", "danger");
        return;
      }
      const doc = await response.json();
      // Obtener rol del usuario sobre el documento
      let rol = "AUTOR";
      if (doc.autorCorreo !== this.email) {
        // Buscar invitación aceptada en la lista de invitaciones
        const invitacionesResp = await fetch(`${API_URL}/documentos/${id}/invitaciones`, {
          headers: { Authorization: "Bearer " + this.jwt },
        });
        if (invitacionesResp.ok) {
          const invitaciones = await invitacionesResp.json();
          const miInv = invitaciones.find(i => i.correoInvitado === this.email && i.aceptada);
          if (miInv) rol = miInv.rol;
        }
      }
      // Rellenar campos del modal
      document.getElementById("editDocTitulo").value = doc.titulo;
      document.getElementById("editDocPublico").value = doc.publico ? "true" : "false";
      document.getElementById("editDocArchivo").value = "";
      // Controlar permisos
      const disabled = (rol === "LECTOR");
      document.getElementById("editDocTitulo").disabled = disabled;
      document.getElementById("editDocPublico").disabled = disabled;
      document.getElementById("editDocArchivo").disabled = disabled;
      document.getElementById("editarDocGuardarBtn").disabled = disabled;
      // Mostrar modal
      const modal = new bootstrap.Modal(document.getElementById("editarDocModal"));
      modal.show();
      // Manejar envío del formulario
      const form = document.getElementById("editarDocForm");
      form.onsubmit = async (e) => {
        e.preventDefault();
        if (disabled) return;
        if (!form.checkValidity()) {
          form.classList.add("was-validated");
          return;
        }
        const titulo = document.getElementById("editDocTitulo").value;
        const publico = document.getElementById("editDocPublico").value;
        const archivoInput = document.getElementById("editDocArchivo");
        const archivo = archivoInput && archivoInput.files.length > 0 ? archivoInput.files[0] : null;
        const formData = new FormData();
        formData.append("titulo", titulo);
        formData.append("publico", publico);
        if (archivo) formData.append("archivo", archivo);
        // Enviar PATCH multipart
        try {
          const patchResp = await fetch(`${API_URL}/documentos/${id}`, {
            method: "PATCH",
            headers: {
              Authorization: "Bearer " + this.jwt,
              "X-Autor-Correo": this.email,
            },
            body: formData,
          });
          if (patchResp.ok) {
            this.showToast("Documento editado exitosamente", "success");
            modal.hide();
            await this.cargarMisDocumentos();
            await this.cargarCompartidos();
          } else {
            this.showToast("Error al editar el documento", "danger");
          }
        } catch (err) {
          this.showToast("Error al editar el documento", "danger");
        }
      };
    } catch (error) {
      console.error("Error editando documento:", error);
      this.showToast("Error al editar el documento", "danger");
    }
  }

  // Eliminar documento
  async eliminarDocumento(id) {
    if (!confirm("¿Estás seguro de que quieres eliminar este documento?")) {
      return;
    }

    try {
      const userId = localStorage.getItem("userId");
      const response = await fetch(`${API_URL}/documentos/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + this.jwt,
          "X-User-Id": userId,
        },
      });

      if (response.ok) {
        this.showToast("Documento eliminado exitosamente", "success");
        await this.cargarMisDocumentos();
      } else {
        this.showToast("Error al eliminar el documento", "danger");
      }
    } catch (error) {
      console.error("Error eliminando documento:", error);
      this.showToast("Error al eliminar el documento", "danger");
    }
  }

  // Aceptar invitación
  async aceptarInvitacion(documentoId, correoInvitado) {
    try {
      const response = await fetch(
        `${API_URL}/documentos/${documentoId}/aceptar?correoInvitado=${encodeURIComponent(
          correoInvitado
        )}`,
        {
          method: "POST",
          headers: {
            Authorization: "Bearer " + this.jwt,
          },
        }
      );

      if (response.ok) {
        this.showToast("Invitación aceptada exitosamente", "success");
        await this.cargarInvitaciones();
        await this.cargarCompartidos(); // Refrescar documentos compartidos
      } else {
        this.showToast("Error al aceptar la invitación", "danger");
      }
    } catch (error) {
      console.error("Error aceptando invitación:", error);
      this.showToast("Error al aceptar la invitación", "danger");
    }
  }

  // Suscribirse a documento
  async suscribirseDocumento(id) {
    try {
      const response = await fetch(`${API_URL}/documentos/${id}/suscribirse`, {
        method: "POST",
        headers: {
          Authorization: "Bearer " + this.jwt,
          "X-Correo-Invitado": this.email,
        },
      });

      if (response.ok) {
        this.showToast("Suscripción realizada exitosamente", "success");
        await this.cargarSuscripciones();
      } else {
        this.showToast("Error al suscribirse al documento", "danger");
      }
    } catch (error) {
      console.error("Error suscribiéndose:", error);
      this.showToast("Error al suscribirse al documento", "danger");
    }
  }

  // Cancelar suscripción
  async cancelarSuscripcion(id) {
    if (!confirm("¿Estás seguro de que quieres cancelar la suscripción?")) {
      return;
    }

    try {
      const response = await fetch(`${API_URL}/documentos/${id}/suscribirse`, {
        method: "DELETE",
        headers: {
          Authorization: "Bearer " + this.jwt,
          "X-Correo-Invitado": this.email,
        },
      });

      if (response.ok) {
        this.showToast("Suscripción cancelada exitosamente", "success");
        await this.cargarSuscripciones();
      } else {
        this.showToast("Error al cancelar la suscripción", "danger");
      }
    } catch (error) {
      console.error("Error cancelando suscripción:", error);
      this.showToast("Error al cancelar la suscripción", "danger");
    }
  }

  // Establecer estado de carga
  setLoadingState(button, isLoading) {
    if (isLoading) {
      button.classList.add("loading");
      button.disabled = true;
      button.innerHTML =
        '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';
    } else {
      button.classList.remove("loading");
      button.disabled = false;
      button.innerHTML = '<i class="fas fa-plus me-2"></i>Crear Documento';
    }
  }

  // Mostrar toast
  showToast(message, type = "info") {
    const toast = document.createElement("div");
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute("role", "alert");
    toast.setAttribute("aria-live", "assertive");
    toast.setAttribute("aria-atomic", "true");

    toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${
                      type === "success"
                        ? "check-circle"
                        : type === "danger"
                        ? "exclamation-triangle"
                        : "info-circle"
                    } me-2"></i>
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;

    const container =
      document.getElementById("toast-container") || document.body;
    container.appendChild(toast);

    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    // Remover del DOM después de que se oculte
    toast.addEventListener("hidden.bs.toast", () => {
      if (container.contains(toast)) {
        container.removeChild(toast);
      }
    });
  }

  // Cerrar sesión
  logout() {
    localStorage.clear();
    window.location.href = "index.html";
  }

  // Mostrar controles de paginación reutilizable
  mostrarPaginacion(containerId, totalPages, currentPage, funcName) {
    const paginacion = document.getElementById(containerId);
    if (!paginacion) return;
    paginacion.innerHTML = "";
    if (totalPages <= 1) return;
    if (currentPage > 0) {
      paginacion.innerHTML += `<button class="btn btn-secondary btn-sm me-2" onclick="panelManager.${funcName}(${currentPage - 1})">Anterior</button>`;
    }
    paginacion.innerHTML += `<span>Página ${currentPage + 1} de ${totalPages}</span>`;
    if (currentPage < totalPages - 1) {
      paginacion.innerHTML += `<button class="btn btn-secondary btn-sm ms-2" onclick="panelManager.${funcName}(${currentPage + 1})">Siguiente</button>`;
    }
  }
}

// Inicializar cuando el DOM esté listo
document.addEventListener("DOMContentLoaded", function () {
  // Inicializar el gestor del panel
  window.panelManager = new PanelManager();
});
