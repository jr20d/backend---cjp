let usuario = document.getElementById("usuario"),
	password = document.getElementById("password"),
	mensaje = document.getElementById("mensaje"),
	clave = document.getElementById("token"),
	form = document.getElementById("login");

document.getElementById("usuario").addEventListener("keypress", (e) =>{
	if (e.key === "Enter"){
		enviarDatos();
	}
});

document.getElementById("password").addEventListener("keypress", (e) =>{
	if (e.key === "Enter"){
		enviarDatos();
	}
});

document.getElementById("sesion").addEventListener("click", enviarDatos);

function enviarDatos(){
	
	if (usuario.value.trim().length > 0 && password.value.trim().length > 0){
		
		consumirApi({
			url: "/auth",
			body: {
				usuario: usuario.value.trim(),
				password: password.value.trim()
			},
			metodo: "POST"
		})
		.then(response =>{
			if (response.ok && response.status === 200){
				response.json()
				.then(respuesta =>{
					localStorage.setItem("token", respuesta.token);
					localStorage.setItem("tiempo", 1 * 60 * 60);
					localStorage.setItem("contador", 0);
					localStorage.setItem("otro", respuesta.authorities[0].authority);
					document.cookie = "auth=" + localStorage.getItem("token");
					document.cookie = "rol=" + localStorage.getItem("otro") + "; path=/usuario";
					location.reload();
				});
			}
			else{
				mensaje.style.display = "block";
				mensaje.innerText = "Error en usuario o contraseña";
			}
		});
	}
	else{
		localStorage.clear();
		mensaje.style.display = "block";
		mensaje.innerText = "Ingresar usuario y contraseña";
	}
}

consumirApi({
	url: "/login",
	metodo: "GET"
})
.then(response =>{
	if (!response.ok && response.status !== 200){
		localStorage.clear();
		document.cookie = "auth=; max-age=0";
		document.cookie = "rol=; max-age=0; path=/usuario";
	}
})
.catch(error =>{
	console.log(error);
});

//consumo de api
async function consumirApi(data){
	const host = "https://admon-cjp.herokuapp.com";
	let t = localStorage.getItem("token");
	let token = (t != null) ? "Bearer " + t : null;
	const response = await fetch((host + data.url), {
		method: data.metodo,
		mode: 'cors',
		cache: 'no-cache',
		credentials: 'same-origin',
		headers: {
			'content-type': 'application/json',
			'Authorization': token
		},
		redirect: 'follow',
		referrerPolicy: 'no-referrer',
		body: JSON.stringify(data.body)
	})
	return response;
}