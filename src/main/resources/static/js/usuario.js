let contenedorModal = document.createElement("div");
let modal = document.createElement("div");

//Verificando si ya se logeo el usario y su privilegio
if (localStorage.getItem("otro")){
	document.cookie = "rol=" + localStorage.getItem("otro") + "; path=/usuario";
}

function abrirVentana(data = {}){
	contenedorModal.setAttribute("id", "modal");
	contenedorModal.setAttribute("class", "contenedor-modal");
	modal.setAttribute("class", "ventana-modal");
	modal.innerHTML = `
		<div class="col-lg-12 col-md-12 col-sm-12" style="position:relative">
			<button id="cerrar" class="pull-right" style="border: none; background-color: transparent; font-size: 25px;"><i class="fas fa-times"></i></button>
            <h4 class="title">Usuario</h4>
            <form id="usuario-form" class="contact-form" role="form" method="POST">
              <div class="form-group">
                <input id="usuario" type="text" ${data.usuario ? "readonly" : ""} value="${data.usuario ? data.usuario : ""}" name="usuario" class="form-control" placeholder="Ingresar usuario" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
                <input id="password" type="password" value="" name="password" class="form-control" placeholder="Ingresar contraseña" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
                <input id="passwordConfirm" type="password" value="" name="passwordConfirm" class="form-control" placeholder="Ingresar contraseña" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
              	<label>Ingresar el rol del usuario</label>
                <select id="rol" class="form-control">
                	<option id="op1" value="1">Admin</option>
                	${data.usuario === "root" ? '' : '<option id="op2" value="2">Comun</option>'}
                </select>
              </div>
              <div class="form-send col-md-12">
                <button id="guardar" type="button" class="btn btn-large btn-primary">Guardar datos</button>
              </div>
            </form>
          </div>
	`;

	document.getElementById("container").appendChild(contenedorModal);
	document.getElementById("container").appendChild(modal);
	
	if (data.rol === "Administrador"){
		document.getElementById("op1").setAttribute("selected", "true")
	}
	else{
		document.getElementById("op2").setAttribute("selected", "true")
	}
	
	contenedorModal.addEventListener("click", () =>{
		cerrarVentana(contenedorModal, modal);
	});
	document.getElementById("cerrar").addEventListener("click", () =>{
		cerrarVentana(contenedorModal, modal);
	});
	
	//Evento del botón actualizar datos y agregar
	if (data.usuario){
		document.getElementById("guardar").addEventListener("click", () =>{
			guardar(data);
		});
	}
	else{
		document.getElementById("guardar").addEventListener("click", guardar);
	}
}

document.getElementById("agregar").addEventListener("click", abrirVentana);

//Fucnión para guardar los datos del usuario o agregar uno nuevo
function guardar(data = {}){
	let usuario = document.getElementById("usuario");
	let password = document.getElementById("password");
	let passwordConfirm = document.getElementById("passwordConfirm");
	let idRol = document.getElementById("rol");
	
	if (validar(usuario) && ((validar(password) && validar(passwordConfirm)) || data.usuario)){
		mensajeRespuesta(document.getElementById("usuario-form"), 1, null);
		if (password.value.trim() === passwordConfirm.value.trim()){
			let dto = {
					usuario: usuario.value.trim().toLowerCase(),
					password: password.value.trim(),
					rol: {
						idRol: idRol.value
					}
				}
			if (data.usuario){
				dto.usuario = data.usuario;
				consumirApi("/api/usuarios", "PUT", dto)
				.then(response =>{
					if (response.ok && response.status === 200){
						response.json()
						.then(respuesta =>{
							if (respuesta.ok){
								cerrarVentana(contenedorModal, modal);
								listado();
								guardado(respuesta.mensaje);
							}
							else{
								mensajeRespuesta(document.getElementById("usuario-form"), 3, respuesta.mensaje);
							}
						});
					}
					else{
						mensajeRespuesta(document.getElementById("usuario-form"), 3, "Ocurrió un error al realizar la operación");
					}
				});
			}
			else{
				consumirApi("/api/usuarios", "POST", dto)
				.then(response =>{
					if (response.ok && response.status === 200){
						response.json()
						.then(respuesta =>{
							if (respuesta.ok){
								cerrarVentana(contenedorModal, modal);
								listado();
								guardado(respuesta.mensaje);
							}
							else{
								mensajeRespuesta(document.getElementById("usuario-form"), 3, respuesta.mensaje);
							}
						});
					}
					else{
						mensajeRespuesta(document.getElementById("usuario-form"), 3, "Ocurrió un error al realizar la operación");
					}
				});
			}
		}
		else{
			mensajeRespuesta(document.getElementById("usuario-form"), 3, "La contraseña ingresada y la confirmación no coinciden");
		}
	}
	else{
		mensajeRespuesta(document.getElementById("usuario-form"), 3, "Asegurese de agregar el usuario y contraseña");
	}
}

//Listado de usuarios
function listado(){
	let tabla = document.getElementById("registros");
	let registros;
	consumirApi("/api/usuarios")
	.then(response =>{
		if (response.ok && response.status === 200){
			tabla.innerHTML = "";
			response.json()
			.then(resultado =>{
				registros = resultado;
				resultado.map((usuario, index) =>{
					if (usuario.usuario === "root"){
						tabla.innerHTML += `
							<tr>
					            <td>${usuario.usuario}</td>
					            <td>${usuario.rol}</td>
					            <td>
					                <button id="editarUsuario-${index}" class="btn btn-primary btn-xs"><i class="fa fa-pencil"></i></button>
					            </td>
					       </tr>
						`;
					}
					else{
						tabla.innerHTML += `
							<tr>
					            <td>${usuario.usuario}</td>
					            <td>${usuario.rol}</td>
					            <td>
					                <button id="editarUsuario-${index}" class="btn btn-primary btn-xs"><i class="fa fa-pencil"></i></button>
					                <button id='eliminarUsuario-${index}' class='btn btn-danger btn-xs'><i class='fas fa-trash-alt'></i></button>
					            </td>
					       </tr>
						`;
					}
				});
				
				registros.map((usuario, index) =>{
					document.getElementById("editarUsuario-" + index).addEventListener("click", ()=>{
						inicio();
						abrirVentana(usuario);
					});
					if (usuario.usuario !== "root"){
						document.getElementById("eliminarUsuario-" + index).addEventListener("click", ()=>{
							let dto = {
									usuario: usuario.usuario,
									password: "",
									rol: {
										idRol: (usuario.rol === "Administrador") ? 1 : 2
									}
								}
							eliminar("/api/usuarios", dto, listado);
						});
					}
				});
			});
		}
	});
}

//Mostrando lista de usuarios
listado();