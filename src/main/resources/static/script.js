document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    if (loginForm) {
        loginForm.addEventListener("submit", async function(event) {
            event.preventDefault();
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;

            const response = await fetch("http://localhost:9091/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password })
            });

            const messageElement = document.getElementById("message");
            if (response.ok) {
                messageElement.innerText = "Login successful!";
                messageElement.style.color = "green";
                setTimeout(() => { window.location.href = "dashboard.html"; }, 1000);
            } else {
                messageElement.innerText = "Invalid credentials!";
                messageElement.style.color = "red";
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async function(event) {
            event.preventDefault();
            const name = document.getElementById("name").value;
            const email = document.getElementById("email").value;
            const password = document.getElementById("password").value;
            const role = document.getElementById("role").value;

            const response = await fetch("http://localhost:9091/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email, password, role })
            });

            const messageElement = document.getElementById("message");
            if (response.ok) {
                messageElement.innerText = "Registration successful!";
                messageElement.style.color = "green";
                setTimeout(() => { window.location.href = "login.html"; }, 1000);
            } else {
                messageElement.innerText = "Registration failed!";
                messageElement.style.color = "red";
            }
        });
    }
});