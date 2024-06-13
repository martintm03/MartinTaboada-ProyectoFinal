document.getElementById('registroForm').addEventListener('submit', function(event) {
    let password = document.getElementById('password').value;
    let regex = /^(?=.*[A-Z])(?=.*\d).{8,}$/; //Patrón para la contraseña

    if (!regex.test(password)) {
        alert('La contraseña debe contener al menos una letra mayúscula, un número y tener más de 8 caracteres.');
        event.preventDefault(); // Evita que el formulario se envíe
    } else {
        // Aviso de éxito después de verificar la contraseña
        alert('Cuenta creada correctamente.');
    }
});