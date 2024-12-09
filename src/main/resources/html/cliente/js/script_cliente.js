//alerta de exito o no exito
function handleFormSubmission(formSelector) {
    document.querySelectorAll(formSelector).forEach(form => {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const submitButton = this.querySelector('button[type="submit"]');
            submitButton.disabled = true;

            let loadingTimer = setTimeout(() => {
                Swal.fire({
                    title: 'Por favor, espere',
                    text: 'Estamos procesando su solicitud...',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });
            }, 2000);

            const formData = new FormData(this);
            const isMultipart = Array.from(formData.values()).some(value => value instanceof File);
            const actionUrl = this.action;

            const fetchOptions = isMultipart
                ? {
                    method: 'POST',
                    body: formData
                }
                : {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(formData).toString()
                };

            fetch(actionUrl, fetchOptions)

                .then(response => response.json())

                .then(data => {

                    clearTimeout(loadingTimer);
                    if (Swal.isVisible()) {
                        Swal.close();
                    }

                    if (data.tipoMensaje === 'success') {

                        if (data.carritoHtml) {
                            const carritoContainer = document.querySelector('.cart-container');
                            if (carritoContainer) {
                                carritoContainer.innerHTML = data.carritoHtml;
                                handleFormSubmission(formSelector);
                            }
                        }

                        Swal.fire({
                            icon: 'success',
                            title: 'Éxito',
                            text: data.mensaje,
                        }).then(() => {
                            if (data.redirectUrl) {
                                window.location.href = data.redirectUrl;
                            }
                        });

                        if (data.notiCounter !== undefined) {
                            const notiCounterElement = document.querySelector('.contadorNotificaciones');
                            if (notiCounterElement) {
                                notiCounterElement.textContent = data.notiCounter;
                            }
                        }

                        if (data.cartCounter !== undefined) {
                            const cartCounterElement = document.querySelector('.cart-counter');
                            if (cartCounterElement) {
                                cartCounterElement.textContent = data.cartCounter;
                            }
                        }

                    } else if (data.tipoMensaje === 'error') {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: data.mensaje,
                        });
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al procesar la solicitud.',
                    });
                })
                .finally(() => {
                    submitButton.disabled = false;
                });
        });
    });
}

//FUNCION DE CONFIRMACION
function handleFormConfirmation(formSelector) {
    document.querySelectorAll(formSelector).forEach(form => {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const actionUrl = form.action;
            const formData = new FormData();

            const selectedComments = form.querySelectorAll('input[name="ids"]:checked');

            if (selectedComments.length > 0) {
                const submitButton = document.activeElement;
                const accion = submitButton.value;

                selectedComments.forEach(checkbox => {
                    formData.append('ids', checkbox.value);
                });
                formData.append('accion', accion);
            } else {
                const formFields = new FormData(form);
                for (let [key, value] of formFields.entries()) {
                    formData.append(key, value);
                }
            }

            fetch(actionUrl, {
                method: 'POST',
                body: formData
            })
                .then(response => response.json())
                .then(data => {
                    Swal.fire({
                        title: data.titulo || '¿Está seguro?',
                        text: data.texto || '¿Desea continuar con esta acción?',
                        icon: data.icono || 'warning',
                        showCancelButton: true,
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        confirmButtonText: data.textoBotonConfirmar || 'Sí, continuar',
                        cancelButtonText: data.textoBotonCancelar || 'Cancelar'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            fetch(actionUrl, {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/x-www-form-urlencoded',
                                },
                                body: new URLSearchParams(formData).toString()
                            })
                                .then(response => response.json())
                                .then(data => {
                                    if (data.tipoMensaje === 'success') {
                                        Swal.fire({
                                            icon: 'success',
                                            title: 'Éxito',
                                            text: data.mensaje,
                                        }).then(() => {
                                            if (data.redirectUrl) {
                                                window.location.href = data.redirectUrl;
                                            }
                                        });
                                    } else if (data.tipoMensaje === 'error') {
                                        Swal.fire({
                                            icon: 'error',
                                            title: 'Error',
                                            text: data.mensaje,
                                        });
                                    }
                                })
                                .catch(error => {
                                    console.error('Error:', error);
                                    Swal.fire({
                                        icon: 'error',
                                        title: 'Error',
                                        text: 'Error al procesar la solicitud.',
                                    });
                                });
                        }
                    });
                })
                .catch(error => {
                    console.error('Error:', error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al obtener el mensaje de confirmación.',
                    });
                });
        });
    });
}

//LLAMADA A LAS ALERTAS
document.addEventListener("DOMContentLoaded", function() {
    handleFormSubmission(".ajax-form");
    handleFormConfirmation(".confirm-form");
});

//PARA LA CANTIDAD
document.addEventListener('DOMContentLoaded', function() {
    const cantidadInput = document.getElementById('quantity2');
    const hiddenCantidadInput = document.getElementById('cantidadInput');
    const btnDecrement = document.querySelector('.btn-decrement');
    const btnIncrement = document.querySelector('.btn-increment');

    function updateHiddenInput() {
        hiddenCantidadInput.value = cantidadInput.value;
    }

    btnDecrement.addEventListener('click', function() {
        let currentValue = parseInt(cantidadInput.value);
        if (currentValue > 1) {
            cantidadInput.value = currentValue - 1;
            updateHiddenInput();
        }
    });

    btnIncrement.addEventListener('click', function() {
        let currentValue = parseInt(cantidadInput.value);
        if (currentValue < 10) {
            cantidadInput.value = currentValue + 1;
            updateHiddenInput();
        }
    });

    cantidadInput.addEventListener('change', updateHiddenInput);
});

document.addEventListener('DOMContentLoaded', function() {
    const carritoContainer = document.querySelector('.cart-container');

    carritoContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('btn-decrement')) {
            const container = event.target.closest('.product-container');
            const cantidadInput = container.querySelector('.cantidad');
            let currentValue = parseInt(cantidadInput.value);
            if (currentValue > 1) {
                cantidadInput.value = currentValue - 1;
            }
        }

        if (event.target.classList.contains('btn-increment')) {
            const container = event.target.closest('.product-container');
            const cantidadInput = container.querySelector('.cantidad');
            let currentValue = parseInt(cantidadInput.value);
            cantidadInput.value = currentValue + 1;
        }
    });

    const cantidadInputs = document.querySelectorAll('.cantidad');
    cantidadInputs.forEach(cantidadInput => {
        cantidadInput.addEventListener('change', function() {
            let currentValue = parseInt(cantidadInput.value);
            if (currentValue < 1) {
                cantidadInput.value = 1;
            }
        });
    });
});

//carrusel
const swiper = new Swiper('.productosSwiper', {
    slidesPerView: 1,
    spaceBetween: 30,
    loop: true,
    pagination: {
        el: '.swiper-pagination',
        clickable: true,
    },
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
    breakpoints: {
        670: {
            slidesPerView: 2,
            spaceBetween: 20,
        },
        820: {
            slidesPerView: 3,
            spaceBetween: 20,
        },
        1025: {
            slidesPerView: 4,
            spaceBetween: 30,
        },
        1200: {
            slidesPerView: 5,
            spaceBetween: 30,
        }
    }
});

//API DNI




