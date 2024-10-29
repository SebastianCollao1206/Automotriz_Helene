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

            // Si todo esta correcto, enviar el formulario
            this.submit();
        });
    }
});

//Campos automaticos
document.getElementById('generate-variants').addEventListener('click', function() {
    const numVariants = document.getElementById('num-variantes').value;
    const variantsContainer = document.getElementById('variants-container');
    variantsContainer.innerHTML = '';

    for (let i = 1; i <= numVariants; i++) {
        const variantHTML = `
            <div class="variant mb-4 mt-5">
                <h4 class="mb-3 text-center">Variante ${i}</h4>
                <div class="row mb-3 d-flex justify-content-center gap-4">
                    <div class="col-md-5">
                        <label for="codigo-${i}" class="form-label">Código</label>
                        <input type="text" class="form-control" id="codigo-${i}" required>
                    </div>
                    <div class="col-md-5">
                        <label for="tamaño-${i}" class="form-label">Tamaño</label>
                        <select class="form-select" id="tamaño-${i}" required>
                            <option value="" style="display: none;">Selecciona un tamaño</option>
                            <option value="pequeño">Pequeño</option>
                            <option value="mediano">Mediano</option>
                            <option value="grande">Grande</option>
                            <!-- Add more sizes as needed -->
                        </select>
                        <div class="form-text">
                            <a href="nuevo_tamaño.html">¿No existe el tamaño?</a>
                        </div>
                    </div>
                </div>
                <div class="row mb-3 d-flex justify-content-center gap-4">
                    <div class="col-md-5">
                        <label for="precio-${i}" class="form-label">Precio</label>
                        <input type="number" class="form-control" id="precio-${i}" required>
                    </div>
                    <div class="col-md-5">
                        <label for="imagen-${i}" class="form-label">Imagen (URL)</label>
                        <input type="text" class="form-control" id="imagen-${i}" required>
                    </div>
                    
                </div>
                <div class="row mb-3 d-flex justify-content-center gap-4">
                    <div class="col-md-5">
                        <label for="stock-${i}" class="form-label">Stock</label>
                        <input type="number" class="form-control" id="stock-${i}" required>
                    </div>
                    <div class="col-md-5">
                        <label for="cantidad-${i}" class="form-label">Cantidad</label>
                        <input type="number" class="form-control" id="cantidad-${i}" required>
                    </div>
                </div>
            </div>
        `;
        variantsContainer.innerHTML += variantHTML;
    }
});