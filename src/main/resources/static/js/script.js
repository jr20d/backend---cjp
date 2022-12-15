var CKEDITOR;
let imagenCKEDITOR;
let imagenBase64;

//Cerrar ventana modal
function cerrarVentana(elemento1, elemento2){
	document.getElementById("container").removeChild(elemento1);
	document.getElementById("container").removeChild(elemento2);
}

//Precargar imágen en base64
function previsualizarImagen(event, idImage){
	let archivo = event.target.files[0];
	let elementoImagen = document.getElementById(idImage);
	let imagen = new FileReader();
	
	if (archivo === undefined){
		elementoImagen.src = "https://www.placehold.it/200x150/EFEFEF/AAAAAA&text=sin+imagen";
		return false;
	}
	
	if (archivo.type === "image/png" || archivo.type === "image/jpeg" || archivo.type === "image/svg+xml"){
		imagen.onload = () =>{
			imagenBase64 = imagen.result;
			elementoImagen.src = imagen.result;
		}
		imagen.readAsDataURL(archivo);
	}
	else{
		elementoImagen.src = "https://www.placehold.it/200x150/EFEFEF/AAAAAA&text=no+image";
		archivo.value = "";
		imagenBase64 = null;
	}
}

//Crear editor de texto y iconfiguración para subida de imágenes
function editor(contenido = ""){
	DecoupledEditor
    .create( document.querySelector( '#editor' ) )
    .then( editor => {
    	CKEDITOR = editor;
        const toolbarContainer = document.querySelector( '#toolbar-container' );
        toolbarContainer.appendChild( editor.ui.view.toolbar.element );
        imagenCKEDITOR = document.querySelector(".ck-file-dialog-button");
        const fileAnterior = imagenCKEDITOR.children[1];
        imagenCKEDITOR.removeChild(fileAnterior);
        imagenCKEDITOR.innerHTML += `
        	<input type="file" id="img-editor" style="display: none;">
        `;
        
        CKEDITOR.setData(contenido);
        
        document.getElementById("img-editor").addEventListener("change", (event) =>{
    		agregarImagenEditor(event);
    	});
        
        imagenCKEDITOR.addEventListener("click", ()=>{
        	document.getElementById("img-editor").click();
        })
    })
    .catch( error => {
        console.error( error );
    });
}

//Función para agregar imágenes al editor de texto
function agregarImagenEditor(event){
	let imagen = new FileReader();
	
	imagen.onload = () =>{
		CKEDITOR.setData(CKEDITOR.getData() + '<img src="' + imagen.result + '">');
	};
	imagen.readAsDataURL(event.target.files[0]);
	imagenCKEDITOR.children[1].value = null;
}

//Retornar el editor
function getContenido(){
	return CKEDITOR.getData();
}

//Validación de campos de tipo input el parametro recibe el control
function validar(elemento){
	let validar;
	if (elemento === undefined || elemento.value.trim().length === 0){
		validar = false;
	}
	else{
		validar = true;
	}
	return validar;
}

//validación para campos de tipo input file
function validarImagen(elemento){
	let validar;
	if (elemento === undefined || elemento.value === ""){
		validar = false;
	}
	else{
		validar = true;
	}
	
	return validar;
}

//Validación para el editor de texto
function validarEditor(contenido){
	let validar;
	if (contenido.trim().length === 0){
		validar = false;
	}
	else{
		validar = true;
	}
	return validar;
}

//Función para consumir servicios web mediante fetch api
async function consumirApi(url, metodo="GET", data){
	const host = "https://admon-cjp.herokuapp.com";
	const response = await fetch((host + url), {
		method: metodo,
		mode: 'cors',
		cache: 'no-cache',
		credentials: 'same-origin',
		headers: {
			'content-type': 'application/json',
			'Authorization': 'Bearer ' + localStorage.getItem("token")
		},
		redirect: 'follow',
		referrerPolicy: 'no-referrer',
		body: JSON.stringify(data)
	})
	return response;
}

//Limpiar el contenido de un elemento html
function limpiarContenido(elemento){
	elemento.innerHTML = "";
}

//Mostrar mensajes de respuesta
function mensajeRespuesta(elemento, tipo, contenido){
	let mensaje = document.createElement("div");
	let enviando = document.createElement("img");
	enviando.src="/img/subir.gif";
	//Verificando si ya existe el elemento
	if (document.getElementById("mensaje")){
		elemento.removeChild(document.getElementById("mensaje"));
	}
	mensaje.setAttribute("id", "mensaje");
	
	switch(tipo){
		case 1:
			mensaje.setAttribute("class", "col-md-12 text-center");
			break;
		case 2:
			mensaje.setAttribute("class", "alert col-md-12 bg-primary text-center");
			break;
		default:
			mensaje.setAttribute("class", "error-message col-md-12");
			break;
	}
	elemento.appendChild(mensaje);
	mensaje.style.display = "block";
	
	if (tipo !== 1){
		let tiempo;
		mensaje.innerText = contenido;
		tiempo = setTimeout(() => {
			elemento.removeChild(mensaje);
			clearTimeout(tiempo);
		}, 5500);
	}
	else{
		mensaje.appendChild(enviando);
	}
}


function inicio(){	
	window.scrollTo({
		  top: 0,
		  left: 0,
		  behavior: 'smooth'
		});
}

//animacion confirmacion de proceso
function guardado(mensaje){
	Swal.fire({
		  position: 'center-end',
		  icon: 'success',
		  title: mensaje,
		  showConfirmButton: false,
		  timer: 2000
		})
}

//Ventana de eliminación
function eliminar(url, data, funcion){
	Swal.fire({
		  title: 'Estás seguro?',
		  text: "Este registro será borrado!",
		  icon: 'warning',
		  showCancelButton: true,
		  confirmButtonColor: '#3085d6',
		  cancelButtonColor: '#d33',
		  confirmButtonText: 'Si'
		}).then((result) => {
		  if (result.isConfirmed) {
			  consumirApi(url, "DELETE", data)
			  .then(response => {
				  if (response.status === 200){
					  response.json()
					  .then(resultado =>{
						  if (resultado.ok){
							  funcion();
							  Swal.fire(
								      'Borrado!',
								      resultado.mensaje,
								      'success'
								    )
						  }
						  else{
							  Swal.fire(
								      'Error!',
								      resultado.mensaje,
								      'error'
							  )
						  }
					  });
				  }
				  else{
					  Swal.fire(
						      'Error!',
						      (response.status === 403) ? "No posee privilegios para realizar esta operación" : 'No se pudo completar la operación.',
						      'error'
					  )
				  }
			  });
		  }
		})
}

//Renovar token
let temporizador;
if (localStorage.getItem("token")){
	let contador = localStorage.getItem("contador");
	let tiempo = localStorage.getItem("tiempo");
	temporizador = setInterval(() => {
		if (contador >= tiempo - (10 * 60) && contador < tiempo){
			consumirApi("/auth/extend", "POST")
			.then(response =>{
				if (response.ok && response.status === 200){
					response.json()
					.then(respuesta =>{
						localStorage.setItem("token", respuesta.token);
						document.cookie = "auth=" + localStorage.getItem("token");
						contador = 0;
					});
				}
				else{
					clearInterval(temporizador);
					localStorage.clear();
					document.cookie = "auth=; max-age=0";
					document.cookie = "rol=; max-age=0; path=/usuario";
				}
			});
		}
		else{
			contador++;
		}
		localStorage.setItem("contador", contador);
	}, 1000);
}
else{
	clearInterval(temporizador);
}