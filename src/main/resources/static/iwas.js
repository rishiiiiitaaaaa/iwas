const BASE_URL = "http://localhost:9091";
// Login function 
async function loginUser(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        alert("Login Failed! Invalid credentials.");
        return;
    }

    const data = await response.json();
    localStorage.setItem("email", data.email);
    localStorage.setItem("role", data.role);

    // Redirect based on role
    if (data.role === "Admin") {
        window.location.href = "adminDashboard.html";
    } else {
        window.location.href = "employeeDashboard.html";
    }
}
// REGISTER FUNCTION
async function registerUser(event) {
    event.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email, password }),
    });

    const result = await response.text();

    if (response.ok) {
        alert("User Registered Successfully!");

        // Redirect to login page after 1.5 seconds
        setTimeout(() => {
            window.location.href = "loginCap.html";
        }, 1500);
    } else {
        alert("Registration Failed: " + result);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const loginButton = document.getElementById("loginButton");
    if (loginButton) loginButton.addEventListener("click", loginUser);

    const registerButton = document.getElementById("registerButton");
    if (registerButton) registerButton.addEventListener("click", registerUser);

});