let contenedorModal = document.createElement("div");
let modal = document.createElement("div");
let cargando = document.createElement("img");
let body = document.getElementsByTagName("body")[0];

cargando.src="/img/loading.gif";
cargando.setAttribute("id", "cargando");
cargando.setAttribute("class", "cargando");

function abrirVentana(data = {}){
	contenedorModal.setAttribute("id", "modal");
	contenedorModal.setAttribute("class", "contenedor-modal");
	modal.setAttribute("class", "ventana-modal");
	modal.innerHTML = `
		<div class="col-lg-12 col-md-12 col-sm-12" style="position:relative">
			<button id="cerrar" class="pull-right" style="border: none; background-color: transparent; font-size: 25px;"><i class="fas fa-times"></i></button>
            <h4 class="title">Sección</h4>
            <form id="actualizar-seccion-form" class="contact-form" role="form" method="POST">
              <div class="form-group">
                <input id="titulo" type="text" value="${data.titulo ? data.titulo : ""}" name="titulo" class="form-control" placeholder="Contenido o titulo" data-rule="minlen:4" data-msg="Please enter at least 4 chars" >
              </div>
              <div class="form-group">
                <div class="col-md-6">
                	<label>Subir foto</label>
                    <div class="fileupload fileupload-new" data-provides="fileupload">
                      <div class="fileupload-new thumbnail" style="width: 200px; height: 150px;">
                        <img id="foto-seccion" src="${data.url && data.url !== "" ? data.url : "https://www.placehold.it/200x150/EFEFEF/AAAAAA&text=sin+foto"}" alt="" />
                      </div>
                      <div class="fileupload-preview fileupload-exists thumbnail" style="max-width: 200px; max-height: 150px; line-height: 20px;"></div>
                      <div>
                        <span class="btn btn-theme02 btn-file"  style="background-color: #7D9BBE;">
	                        <span class="fileupload-new"><i class="fa fa-paperclip"></i> Seleccionar foto</span>
	                        <input id="foto" type="file" class="default" />
                        </span>
                      </div>
                    </div>
              </div>
              <div class="form-send col-md-12">
                <button id="guardar" type="button" class="btn btn-large btn-primary">Guardar datos</button>
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
	document.getElementById("foto").addEventListener("change", (event)=>{
		previsualizarImagen(event, "foto-seccion");
		verificar = true;
	});
	
	//Evento del botón actualizar datos
	document.getElementById("guardar").addEventListener("click", () =>{
		guardar(data);
	});
}

//funcion para guardar los datos
function guardar(data){
	let titulo = document.getElementById("titulo");
	let foto = document.getElementById("foto");
	
	if (validar(titulo)){
		mensajeRespuesta(document.getElementById("actualizar-seccion-form"), 1, null);
		data.titulo = titulo.value.trim();
		data.url = (foto.files[0]) ? imagenBase64 : data.url;
		consumirApi("/api/secciones", "PUT", data)
		.then(response =>{
			if (response.ok && response.status === 200){
				response.json()
				.then(resultado =>{
					if (resultado.ok){
						cerrarVentana(contenedorModal, modal);
						listado();
						guardado(resultado.mensaje);
					}
					else{
						mensajeRespuesta(document.getElementById("actualizar-seccion-form"), 3, resultado.mensaje);
					}
				});
			}
			else{
				mensajeRespuesta(document.getElementById("actualizar-seccion-form"), 3, "Error al realziar esta operación");
			}
		});
	}
	else{
		mensajeRespuesta(document.getElementById("actualizar-seccion-form"), 3, "El campo de titulo o contenido no puede quedar vacío");
	}
}

//Función para crear listado de las secciones
function listado(){
	body.appendChild(cargando);
	let principal = document.getElementById("seccion-principal");
	let noticias = document.getElementById("seccion-noticias");
	let contenido = "";
	
	principal.innerHTML = "";
	noticias.innerHTML = "";
	
	consumirApi("/api/secciones")
	.then(response =>{
		if (response.ok && response.status === 200){
			response.json()
			.then(resultado =>{
				body.removeChild(cargando);
				resultado.map((seccion, index) =>{
					contenido += `
						<div class="col-md-4 col-sm-4 mb">
			                <div class="grey-panel pn" style="display: block; height: 250px; position: relative;">
			                  <div style="width: 100%; height: 200px; overflow: hidden;">
			                  	<img src="${seccion.url}" width="100%"/>
			                  </div>
			                  <footer style="padding: 0 5px;">
			                    <div class="pull-center">
			                      <h5><button id="${seccion.idCategoria === 1 ? "seccionPrincipalEdit-" + index : "seccionNoticiasEdit-" + index}" style="background-color: transparent; border: none;"><i class="fa fa-edit"></i> Editar</button></h5>
			                    </div>
			                  </footer>
			                </div>
			              </div>
					`;
					if (seccion.idCategoria === 1){
						principal.innerHTML += contenido;
					}
					else{
						noticias.innerHTML = contenido;
					}
					contenido = "";
				});
				
				//Agregando eventos a los botones de actualizar
				resultado.map((seccion, index) =>{
					if (seccion.idCategoria === 1){
						document.getElementById("seccionPrincipalEdit-" + index).addEventListener("click", () =>{
							abrirVentana(seccion);
							inicio();
						});
					}
					else{
						document.getElementById("seccionNoticiasEdit-" + index).addEventListener("click", () =>{
							abrirVentana(seccion);
							inicio();
						});
					}
				});
			});
		}
	});
}

//Mostrando listado
listado();