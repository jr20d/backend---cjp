const telefonos = document.getElementById("sub-tel");

//Generando html de la lista de telefonos luego de consumir la api de manera exitosa
function listarTelefonos(){
	limpiarContenido(telefonos);
	consumirApi("/api/telefonos")
	.then(
		response => {
			if (response.status === 200) {
				response.json()
				.then(resultado => {
					resultado.map((data, index) => {
						telefonos.innerHTML += `
							<div class="form-group col-md-12">
								<input id="tel${index}" type="text" class="form-control col-md-12" placeholder="Telefono ####-####" data-rule="minlen:4" maxlength="9" value="${data.telefono === null ? '' : data.telefono}">
							</div>
						`;
					})
					telefonos.innerHTML += `
						<div class="form-group text-center">
		              		<button class="btn btn-info" id="guardarTel">Guardar</button>
						</div>
					`;
					
					//Creando evento para el botón
					document.getElementById("guardarTel").addEventListener("click", () =>{
						let dto = [];
						resultado.map((data, index) => {
							dto.push({
								idTelefono: data.idTelefono,
								telefono: document.getElementById("tel" + index).value
							});
						});
						guardarTelefonos(dto);
					});
				})
			}
			else{
				mensajeRespuesta(telefonos, 3, "Ocurrió un error");
			}
		});
}

//Ejecutando método para crear listado de telefonos
listarTelefonos();

//Consumir api para guardar los datos de los telefonos
function guardarTelefonos(data){
	consumirApi("/api/telefonos", "PUT", data)
	.then(
		response =>{
			if (response.status === 200){
				response.json()
				.then(
					resultado => {
						mensajeRespuesta(telefonos, 2, resultado.mensaje);
				})
			}
			else{
				mensajeRespuesta(telefonos, 3, "Ocurrio un error: " + response.status);
			}
		})
}