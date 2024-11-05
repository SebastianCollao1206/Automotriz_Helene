//BOTONES
function adjustStock(button, change) {
    const stockInput = button.parentElement.querySelector('input[type="number"]');
    let currentStock = parseInt(stockInput.value);

    currentStock += change;

    if (currentStock < 0) {
        currentStock = 0;
    }

    stockInput.value = currentStock;
}

//
document.addEventListener('DOMContentLoaded', function() {
    // Manejo del formulario de edición de usuario
    const userForm = document.getElementById('user-form');
    if (userForm) {
        userForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Validación básica
            const nombre = document.getElementById('nombre').value.trim();
            const correo = document.getElementById('correo').value.trim();
            const dni = document.getElementById('dni').value.trim();

            if (!nombre || !correo || !dni) {
                alert('Por favor, complete todos los campos requeridos');
                return;
            }

            // Validación de DNI (8 dígitos)
            if (!/^\d{8}$/.test(dni)) {
                alert('El DNI debe contener 8 dígitos');
                return;
            }

            // Validación de correo
            if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) {
                alert('Por favor, ingrese un correo electrónico válido');
                return;
            }

            //Si todo esta correcto enviar el formulario
            this.submit();
        });
    }
});

document.getElementById("generate-variants").addEventListener("click", function() {
    var numVariantes = document.getElementById("num-variantes").value;
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "/producto/agregar?action=generarCamposVariantes&numVariantes=" + numVariantes, true);
    xhr.onload = function() {
        if (xhr.status === 200) {
            document.getElementById("variants-container").innerHTML = xhr.responseText;
        }
    };
    xhr.send();
});