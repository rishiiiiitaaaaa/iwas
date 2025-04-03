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
    const confirmPassword = document.getElementById("confirmPassword").value;
    const role = document.getElementById("role").value;
    const skillsInput = document.getElementById("skill").value.trim();
    const skills = skillsInput ? skillsInput.split(",").map(skill => skill.trim()) : [];


    if (!name || !email || !password || !confirmPassword || !role) {
        alert("All fields are required!");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email, password, role, skills }), //  Send as array
        });

        const result = await response.text();

        if (response.ok) {
            alert("User Registered Successfully!");
            setTimeout(() => { window.location.href = "loginCap.html"; }, 1000);
        } else {
            alert("Registration Failed: " + result);
        }
    } catch (error) {
        alert("An error occurred: " + error.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const loginButton = document.getElementById("loginButton");
    if (loginButton) loginButton.addEventListener("click", loginUser);

    const registerButton = document.getElementById("registerButton");
    if (registerButton) registerButton.addEventListener("click", registerUser);

});
