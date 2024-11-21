function adjustStock(button, change) {
    const stockInput = button.parentElement.querySelector('input[type="number"]');
    let currentStock = parseInt(stockInput.value);

    currentStock += change;

    if (currentStock < 0) {
        currentStock = 0;
    }

    stockInput.value = currentStock;
}

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