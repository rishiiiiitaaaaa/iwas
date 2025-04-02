document.addEventListener("DOMContentLoaded", function() {
    const email = localStorage.getItem("email");
    if (!email) {
        alert("No user logged in. Redirecting to login page.");
        window.location.href = "loginCap.html";
        return;
    }

    let employeeId = null;

    const empNameElem = document.getElementById("empName");
    const empEmailElem = document.getElementById("empEmail");
    const empRoleElem = document.getElementById("empRole");
    const empAvailabilityElem = document.getElementById("empAvailability");
    const empSkillsElem = document.getElementById("empSkills");

    const editProfileBtn = document.getElementById("editProfileBtn");
    const editProfileModal = document.getElementById("editProfileModal");
    const closeModal = document.querySelector(".close");
    const saveChangesBtn = document.getElementById("saveChangesBtn");

    const newNameInput = document.getElementById("newName");
    const newRoleInput = document.getElementById("newRole");
    const newAvailabilityInput = document.getElementById("newAvailability");
    const newSkillsInput = document.getElementById("newSkills");

    //function that fetches all the data of employee 
    function fetchEmployeeData() {
        fetch(`http://localhost:9091/employees/email/${email}`)
            .then(response => response.json())
            .then(data => {
                if (!data.id) throw new Error("Employee ID not found");

                employeeId = data.id;
                localStorage.setItem("employee_id", employeeId);

                empNameElem.textContent = data.name;
                empEmailElem.textContent = data.email;
                empRoleElem.textContent = data.role;

                // Fetch leave requests and check if any leave is active
                fetchLeaveRequests().then(leaveActive => {
                    empAvailabilityElem.textContent = leaveActive ? "Unavailable" : "Available";
                });

                empSkillsElem.textContent = data.skills.length ? data.skills.join(", ") : "None";
            })
            .catch(error => {
                console.error("Error fetching employee data:", error);
                alert("Error fetching profile details.");
            });
    }

    fetchEmployeeData();


    editProfileBtn.addEventListener("click", function() {
        editProfileModal.style.display = "block";
    });

    closeModal.addEventListener("click", function() {
        editProfileModal.style.display = "none";
    });

    saveChangesBtn.addEventListener("click", function(event) {
        event.preventDefault();

        const updatedData = {
            name: newNameInput.value.trim(),
            role: newRoleInput.value.trim(),
            availability: newAvailabilityInput.value === "true",
            skills: newSkillsInput.value ? newSkillsInput.value.split(",").map(skill => skill.trim()) : []
        };

        fetch(`http://localhost:9091/employees/update/${employeeId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedData)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(`Error: ${response.status} - ${text}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                alert("Profile updated successfully!");

                if (!data || typeof data !== "object") {
                    throw new Error("Invalid response format");
                }
                empNameElem.textContent = data.name || "N/A";
                empRoleElem.textContent = data.role || "N/A";
                empAvailabilityElem.textContent = data.availability ? "Available" : "Unavailable";
                empSkillsElem.textContent = Array.isArray(data.skills) && data.skills.length ? data.skills.join(", ") : "None";

                editProfileModal.style.display = "none";
            })
            .catch(error => {
                console.error("Error updating profile:", error);
                alert("Error updating profile. Check console for details.");
            });
    });

    //function that fetches leave request 
    function fetchLeaveRequests() {
        return fetch(`http://localhost:9091/leaverequests/employee/${employeeId}`)
            .then(response => response.json())
            .then(data => {
                const leaveTableBody = document.getElementById("leaveTableBody");
                leaveTableBody.innerHTML = "";

                let leaveActive = false;
                const today = new Date();

                data.forEach(request => {
                    const endDate = new Date(request.endDate);

                    if (request.status === "Approved" && endDate >= today) {
                        leaveActive = true;
                    }

                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${request.startDate}</td>
                        <td>${request.endDate}</td>
                        <td>${request.description}</td>
                        <td>${request.status}</td>
                    `;
                    leaveTableBody.appendChild(row);
                });

                return leaveActive; // Return whether the employee is currently on leave
            })
            .catch(error => {
                console.error("Error fetching leave requests:", error);
                return false; // If there's an error, assume leave is not active
            });
    }


    document.getElementById("leaveForm").addEventListener("submit", function(event) {
        event.preventDefault();

        const leaveRequest = {
            employee: { id: employeeId },
            startDate: document.getElementById("startDate").value,
            endDate: document.getElementById("endDate").value,
            description: document.getElementById("description").value,
            status: "PENDING"
        };


        fetch(`http://localhost:9091/leaverequests`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(leaveRequest)
            })
            .then(response => response.json())
            .then(() => {
                alert("Leave request submitted successfully!");
                document.getElementById("leaveForm").reset();
                fetchLeaveRequests();
            })
            .catch(error => console.error("Error applying for leave:", error));
    });
});

//it helps in redirecting to the login page 
document.addEventListener("DOMContentLoaded", function() {
    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", function() {

            sessionStorage.clear();
            localStorage.removeItem("token");
            window.location.href = "loginCap.html";
        });
    }
});
