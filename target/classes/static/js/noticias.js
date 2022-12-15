let contenedorModal = document.createElement("div");
let modal = document.createElement("div");
let noticias = document.getElementById("noticias-registros");
let cargando = document.createElement("img");
let body = document.getElementsByTagName("body")[0];

cargando.src="/img/loading.gif";
cargando.setAttribute("id", "cargando");
cargando.setAttribute("class", "cargando");


//Crear ventana modal para la noticia
function formularioNoticia(data = {}){
	contenedorModal.setAttribute("id", "modal");
	contenedorModal.setAttribute("class", "contenedor-modal");
	modal.setAttribute("class", "ventana-modal");
	imagenBase64 = (data.imagen) ? data.imagen : "";
	modal.innerHTML = `
		<div class="col-lg-12 col-md-12 col-sm-12" style="position:relative">
			<button id="cerrar" class="pull-right" style="border: none; background-color: transparent; font-size: 25px;"><i class="fas fa-times"></i></button>
            <h4 class="title">Noticia</h4>
            <form id="agregar-noticia-form" class="contact-form" role="form" method="POST">
              <div class="form-group">
                <input id="titulo" type="text" value="${data.titulo ? data.titulo : ""}" name="titulo" class="form-control" placeholder="Titulo de noticia" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
                <div class="col-md-9">
                	<label>Subir imagen de la noticia</label>
                    <div class="fileupload fileupload-new" data-provides="fileupload">
                      <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
                        <img id="imagen-noticia" src="${data.imagen ? data.imagen : "https://www.placehold.it/200x150/EFEFEF/AAAAAA&text=sin+imagen"}" alt="" />
                      </div>
                      <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                      <div>
                        <span class="btn btn-theme02 btn-file"  style="background-color: #7D9BBE;">
	                        <span class="fileupload-new"><i class="fa fa-paperclip"></i> Seleccionar imágen</span>
	                        <input id="imagen" type="file" class="default" />
                        </span>
                      </div>
                    </div>
              </div>
              <div class="form-group col-md-12">
              	<label for="contenido">Contenido de la noticia</label>
              	<div id="toolbar-container"></div>
                <div id="editor" style="min-height: 150px; border: 1px solid #ccc;">
			    </div>
              </div>
              <div class="form-send col-md-12">
                <button id="publicar" type="button" class="btn btn-large btn-primary">${data.idNoticia ? "Guardar datos" : "Publicar noticia"}</button>
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
	
	document.getElementById("imagen").addEventListener("change", (event)=>{
		previsualizarImagen(event, "imagen-noticia");
	});
	
	//Agregando el editor de texto
	if (data.contenido){
		editor(data.contenido);
	}
	else{
		editor();
	}
	
	
	//Evento del botón publicar noticia o para actualizar noticia dependiendo el valor del objeto data
	if (data.idNoticia){
		document.getElementById("publicar").addEventListener("click", () =>{
			guardarNoticia(data);
		});
	}
	else{
		document.getElementById("publicar").addEventListener("click", guardarNoticia);
	}
	
}

//Evento del boton para crear nueva noticia
document.getElementById("agregar").addEventListener("click", () =>{
	formularioNoticia();
});

//Validando y guardando los datos de la noticia
function guardarNoticia(data = {}){
	//variables que tomarán los controles
	let titulo = document.getElementById("titulo");
	let imagen = document.getElementById("imagen");
	let contenido = getContenido();
	//Validando campos
	if (validarEditor(contenido) && validar(titulo) && (validarImagen(imagen) || data.imagen)){
		mensajeRespuesta(document.getElementById("agregar-noticia-form"), 1, null);
		let noticiaDto = {
			titulo: titulo.value.trim(),
			imagen: imagenBase64.trim(),
			contenido: contenido.trim()
		}
		
		if (data.titulo && data.idNoticia && data.contenido && data.fecha && data.imagen){
			//Pasando los nuevos datos
			data.titulo = noticiaDto.titulo.trim();
			data.imagen = (!validarImagen(imagen)) ? data.imagen : noticiaDto.imagen.trim();
			data.contenido = noticiaDto.contenido.trim();
			//actualizando los datos del registro
			consumirApi("/api/noticias", "PUT", data)
			.then(response => {
				if (response.ok && response.status === 200){
					response.json()
					.then(resultado => {
						if (resultado.ok){
								cerrarVentana(contenedorModal, modal);
								listarNoticias();
								guardado(resultado.mensaje);
						}
						else{
							mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, resultado.mensaje);
						}
					});
				}
				else{
					body.removeChild(enviandoDatosModal);
					if (response.status === 403){
						mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, "No posee permisos para realizar esta operación");
					}
					else{
						mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, "No se pudo realizar esta operación");
					}
				}
			});
		}
		else{
			//Ingresando registro
			consumirApi("/api/noticias", "POST", noticiaDto)
			.then(response => {
				if (response.ok && response.status === 200){
					response.json()
					.then(resultado => {
						if (resultado.ok){
								cerrarVentana(contenedorModal, modal);
								listarNoticias();
								guardado(resultado.mensaje);
						}
						else{
							mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, resultado.mensaje);
						}
					});
				}
				else{
					if (response.status === 403){
						mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, "No posee permisos para realizar esta operación");
					}
					else{
						mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, "No se pudo realizar esta operación");
					}
				}
			});
		}
	}
	else{
		mensajeRespuesta(document.getElementById("agregar-noticia-form"), 3, "Faltan campos por completar");
	}
}

//Mostrar listado de noticias
function listarNoticias(){
	body.appendChild(cargando);
	noticias.innerHTML = "";
	consumirApi("/api/noticias")
		.then(response => {
			response.json()
			.then(resultado => {
				body.removeChild(cargando);
				resultado.map((noticia, index) => {
					let fecha = new Date(noticia.fecha.replace(".000+0000", ""));
					noticias.innerHTML += `
						<div class="col-md-4 col-sm-4 mb">
			                <div class="${(index % 2 === 0) ? "grey-panel pn" : "darkblue-panel pn"}" style="display: block; height: 360px; position: relative;">
			                  <div class="${(index % 2 === 0) ? "grey-header" : "darkblue-header"}">
			                    <h5>${noticia.titulo}</h5>
			                  </div>
			                  <div style="width: 100%; height: 200px; overflow: hidden;">
			                  	<img src="${noticia.imagen}" width="100%"/>
			                  </div>
			                  <p style="color: ${(index % 2 === 0) ? "#000" : "#fff"};">${fecha.getDate()}/${fecha.getMonth() + 1}/${fecha.getFullYear()}</p>
			                  <footer style="padding: 0 5px;">
			                    <div class="pull-left">
			                      <h5><button id="editNoticia${index}" style="background-color: transparent; border: none;"><i class="fa fa-edit"></i> Editar</button></h5>
			                    </div>
			                    <div class="pull-right">
			                      <h5><button id="eliminarNoticia${index}" style="background-color: transparent; border: none;"><i class="far fa-trash-alt"></i> Eliminar</button></h5>
			                    </div>
			                  </footer>
			                </div>
			              </div>
					`;
				});
				//Creando eventos para los botones de eliminar y actualizar noticia
				resultado.map((noticia, index) =>{
					document.getElementById("editNoticia" + index).addEventListener("click", () =>{
						inicio();
						formularioNoticia(noticia);
					});
					document.getElementById("eliminarNoticia" + index).addEventListener("click", () =>{
						eliminar("/api/noticias", noticia, listarNoticias);
					});
				});
			});
		});
}

//Mostrando listado de noticias
listarNoticias();