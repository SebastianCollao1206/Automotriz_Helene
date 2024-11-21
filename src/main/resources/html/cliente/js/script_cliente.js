//alerta de exito o no exito
function handleFormSubmission(formSelector) {
    document.querySelectorAll(formSelector).forEach(form => {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            // Disable the submit button to prevent multiple clicks
            const submitButton = this.querySelector('button[type="submit"]');
            submitButton.disabled = true;

            // Show loading alert immediately
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

                    //
                    clearTimeout(loadingTimer);

                    // Close any open Swal if it was shown
                    if (Swal.isVisible()) {
                        Swal.close();
                    }

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
                })   //;


                .finally(() => {
                    // Re-enable the submit button
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
function increment(id) {
    document.getElementById(id).value = parseInt(document.getElementById(id).value) + 1;
}

function decrement(id) {
    var value = parseInt(document.getElementById(id).value);
    if (value > 1) {
        document.getElementById(id).value = value - 1;
    }
}

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



