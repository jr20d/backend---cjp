const credenciales = document.getElementById("sub-password");

document.getElementById("guardarPass").addEventListener("click", guardarPassword);

//Guardar contraseña
function guardarPassword(){
	let pass1 = document.getElementById("pass1");
	let pass2 = document.getElementById("pass2");
	
	if (pass1.value.trim().length > 0 && pass2.value.trim().length > 0){
		if (pass1.value === pass2.value){
			let dto = {
				password: pass1.value
			}
			
			consumirApi("/auth/credenciales", "PUT", dto)
			.then(response =>{
				if (response.status === 200){
					response.json()
					.then(resultado => {
						mensajeRespuesta(credenciales, 2, resultado.mensaje);
						pass1.value = "";
						pass2.value = "";
					})
				}
				else{
					mensajeRespuesta(credenciales, 3, "Ocurrio un error: " + response.status);
				}
			})
		}
		else{
			mensajeRespuesta(credenciales, 3, "Las contraseñas no coinciden");
		}
	}
	else{
		mensajeRespuesta(credenciales, 3, "La contraseña no puede quedar en blanco");
	}
}