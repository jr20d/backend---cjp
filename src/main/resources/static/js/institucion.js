let contenedorModal = document.createElement("div");
let modal = document.createElement("div");
let cargando = document.createElement("img");
let body = document.getElementsByTagName("body")[0];

cargando.src="/img/loading.gif";
cargando.setAttribute("id", "cargando");
cargando.setAttribute("class", "cargando");

//Evento para mostrar la ventana del formulario para agregar una nueva institucion
document.getElementById("agregar").addEventListener("click", abrirVentana);

//ventana con el formulario para la institución
function abrirVentana(data = {}){
	contenedorModal.setAttribute("id", "modal");
	contenedorModal.setAttribute("class", "contenedor-modal");
	modal.setAttribute("class", "ventana-modal");
	imagenBase64 = (data.logo) ? data.logo : "";
	modal.innerHTML = `
		<div class="col-lg-12 col-md-12 col-sm-12" style="position:relative">
			<button id="cerrar" class="pull-right" style="border: none; background-color: transparent; font-size: 25px;"><i class="fas fa-times"></i></button>
            <h4 class="title">Institución</h4>
            <form id="agregar-institucion-form" class="contact-form" role="form" method="POST">
              <div class="form-group">
                <input id="nombre" type="text" value="${data.nombre ? data.nombre : ""}" name="nombre" class="form-control" placeholder="Nombre institución" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
                <div class="col-md-6">
                	<label>Subir logo de la institución</label>
                    <div class="fileupload fileupload-new" data-provides="fileupload">
                      <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
                        <img id="logo-institucion" src="${data.logo ? data.logo : "https://www.placehold.it/200x150/EFEFEF/AAAAAA&text=sin+imagen"}" alt="" />
                      </div>
                      <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                      <div>
                        <span class="btn btn-theme02 btn-file"  style="background-color: #7D9BBE;">
	                        <span class="fileupload-new"><i class="fa fa-paperclip"></i> Seleccionar imágen</span>
	                        <input id="logo" type="file" class="default" />
                        </span>
                      </div>
                    </div>
              </div>
              <div class="form-group">
              	<div class="col-md-6">
              		<div class="col-md-12 text-center">
              			<label>Seleccionar opción</label>
              		</div>
                    <label for="agree" class="control-label col-lg-9 col-sm-6">Mostrar el nombre de la institución</label>
                    <div class="col-lg-3 col-sm-3">
                      <input type="checkbox" style="width: 20px" class="checkbox form-control" id="mostrar" name="mostrar" ${data.mostrar ? "checked" : ""} />
                    </div>
                    <br>
                    <div class="col-md-12 text-justify">
              			<h5>No se recomienda marcar esta opción cuando se ha agregado el logo respectivo de la institución</h5>
              		</div>
                 </div>
              </div>
              <div class="form-send col-md-12">
                <button id="guardarInstitucion" type="button" class="btn btn-large btn-primary">${data.idInstitucion ? "Guardar datos" : "Agregar Institución"}</button>
              </div>
            </form>
          </div>
	`;

	document.getElementById("container").appendChild(contenedorModal);
	document.getElementById("container").appendChild(modal);
	
	contenedorModal.addEventListener("click", () =>{
		cerrarVentana(contenedorModal, modal);
	});
	document.getElementById("cerrar").addEventListener("click", () =>{
		cerrarVentana(contenedorModal, modal);
	});
	
	//Evento para seleccionar le logo de la institución
	document.getElementById("logo").addEventListener("change", (event)=>{
		previsualizarImagen(event, "logo-institucion");
		verificar = true;
	});
	
	//Evento del botón para agregar o actualizar datos
	if (data.idInstitucion){
		document.getElementById("guardarInstitucion").addEventListener("click", (event) =>{
			guardarInst(data);
		});
	}
	else{
		document.getElementById("guardarInstitucion").addEventListener("click", guardarInst);
	}
}
let verificar = false;
//Guardar datos de la institución
function guardarInst(data = {}){
	let nombre = document.getElementById("nombre");
	let logo = document.getElementById("logo");
	let mostrar = document.getElementById("mostrar");
	//Objeto a enviar
	dto = {
		nombre: nombre.value.trim().toUpperCase(),
		logo: logo.files[0] ? imagenBase64 : "",
		mostrar: logo.files[0] ? mostrar.checked : true
	}
	
	if (validar(nombre)){
		mensajeRespuesta(document.getElementById("agregar-institucion-form"), 1, null);
		if (data.idInstitucion){
			data.nombre = dto.nombre;
			data.logo = verificar ? dto.logo : data.logo;
			data.mostrar = verificar? dto.mostrar : data.logo === ""? true : mostrar.checked;
			consumirApi("/api/instituciones", "PUT", data)
			.then(response =>{
				if (response.ok && response.status === 200){
					response.json()
					.then(resultado =>{
						if (resultado.ok){
							cerrarVentana(contenedorModal, modal);
							listarInstituciones();
							guardado(resultado.mensaje);
							verificar = false;
						}
						else{
							mensajeRespuesta(document.getElementById("agregar-institucion-form"), 3, resultado.mensaje);
						}
					});
				}
				else{
					mensajeRespuesta(document.getElementById("agregar-institucion-form"), 3, "No se pudo realizar esta operación");
				}
			});
		}
		else{
			consumirApi("/api/instituciones", "POST", dto)
			.then(response =>{
				if (response.ok && response.status === 200){
					response.json()
					.then(resultado =>{
						if (resultado.ok){
							cerrarVentana(contenedorModal, modal);
							listarInstituciones();
							guardado(resultado.mensaje);
							verificar = false;
						}
						else{
							mensajeRespuesta(document.getElementById("agregar-institucion-form"), 3, resultado.mensaje);
						}
					});
				}
				else{
					mensajeRespuesta(document.getElementById("agregar-institucion-form"), 3, "No se pudo realizar esta operación");
				}
			});
		}		
	}
	else{
		mensajeRespuesta(document.getElementById("agregar-institucion-form"), 3, "Agregar el nombre de la institución");
	}
}

//Listado de instituciones
function listarInstituciones(){
	body.appendChild(cargando);
	let instituciones = document.getElementById("instituciones-registros");
	instituciones.innerHTML = "";
	consumirApi("/api/instituciones")
	.then(response =>{
		if (response.ok && response.status === 200){
			response.json()
			.then(resultado =>{
				body.removeChild(cargando);
				resultado.map((institucion, index) =>{
					instituciones.innerHTML += `
						<div class="tarjeta">
		            		<div class="img-tarjeta">
		            			<img src="${institucion.logo? institucion.logo : "https://www.placehold.it/300x200/EFEFEF/AAAAAA&text=sin+logo"}">
		            		</div>
		            		<div class="titulo-tarjeta">
		            			<h1>${institucion.nombre}</h1>
		            		</div>
		            		<div class="botones-tarjeta">
		            			<h5><button id="actualizarInst-${index}"><i class="fa fa-edit"></i> Editar</button></h5>
		            			<h5><button id="eliminarInst-${index}"><i class="far fa-trash-alt"></i> Eliminar</button></h5>
		            		</div>
		            	</div>
					`;
				});
				//Agregando los eventos para actualizar y eliminar
				resultado.map((institucion, index) =>{
					document.getElementById("actualizarInst-" + index).addEventListener("click", () =>{
						abrirVentana(institucion);
						inicio();
					})
					document.getElementById("eliminarInst-" + index).addEventListener("click", () =>{
						eliminar("/api/instituciones", institucion, listarInstituciones);
					})
				});
			});
		}
	});
}

//Mostrando lista de instituciones
listarInstituciones();