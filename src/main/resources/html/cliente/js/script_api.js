let boton = document.getElementById("searchDniBtn");
boton.addEventListener("click", traerDatos);

function traerDatos() {
    let dni = document.getElementById("dniSearch").value;
    fetch(
        "https://apiperu.dev/api/dni/" + dni + "?api_token=f3dc3bdb2de4fd456a681730b96bff1c73e1495b7824f472a48d73b0c3d14b25"
    )
        .then((datos) => datos.json())
        .then((datos) => {
            document.getElementById("customerName").value =
                datos.data.nombres + " " +
                datos.data.apellido_paterno + " " +
                datos.data.apellido_materno;

            // Agregar campo oculto para DNI
            document.getElementById("hiddenDni").value = datos.data.numero;
        })
        .catch((error) => {
            console.error('Error:', error);
            alert('No se pudo encontrar la información del DNI');
        });
}