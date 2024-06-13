
document.getElementById('contactoForm').addEventListener('submit', function(event) {
    const mensaje = document.getElementById('mensaje').value;
    if (mensaje.length < 30) {
        alert('El mensaje debe tener al menos 30 caracteres.');
        event.preventDefault();
    } else {
        alert('Formulario enviado correctamente.');
    }
});
