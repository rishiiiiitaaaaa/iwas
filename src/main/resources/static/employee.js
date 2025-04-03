// Wait for the DOM content to fully load before executing the script
document.addEventListener("DOMContentLoaded", function() {
    const email = localStorage.getItem("email");
    // If no email is found, redirect the user to the login page
    if (!email) {
        alert("No user logged in. Redirecting to login page.");
        window.location.href = "loginCap.html";
        return;
    }

    let employeeId = null; //variable to store employee id
    //ref to employee values
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

    //used for editing employee details
    editProfileBtn.addEventListener("click", function() {
        editProfileModal.style.display = "block";
    });

    closeModal.addEventListener("click", function() {
        editProfileModal.style.display = "none";
    });
    //used in saving the changes
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
    //function for fetching leave request of employee 
    function fetchLeaveRequests() {
        employeeId = localStorage.getItem("employee_id"); // Ensure employeeId is retrieved
        if (!employeeId) {
            console.error("Error: Employee ID is missing!"); //used for debugging through console
            return Promise.resolve(false);
        }


        return fetch(`http://localhost:9091/leaverequests/employee/${employeeId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                console.log(" Leave Requests API Response:", data);

                const leaveTableBody = document.getElementById("leaveTableBody");
                leaveTableBody.innerHTML = ""; // Clear previous data

                let leaveActive = false;
                const today = new Date();
                today.setHours(0, 0, 0, 0); // Remove time component

                data.forEach(request => {
                    const startDate = new Date(request.startDate);
                    const endDate = new Date(request.endDate);

                    //  Normalize dates (remove time part)
                    startDate.setHours(0, 0, 0, 0);
                    endDate.setHours(23, 59, 59, 999);

                    console.log(` Checking leave: ${startDate.toDateString()} - ${endDate.toDateString()}, Status: ${request.status}`);

                    // LOGIC for Checking if today falls inside an approved leave period
                    if (request.status === "APPROVED" && today >= startDate && today <= endDate) {
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

                console.log(" FINAL leaveActive value:", leaveActive);

                //  Update Availability Text Correctly
                empAvailabilityElem.textContent = leaveActive ? "Unavailable" : "Available";

                return leaveActive;
            })
            .catch(error => {
                console.error(" Error fetching leave requests:", error);
                empAvailabilityElem.textContent = "Available"; // Default to available if error
                return false;
            });
    }
    // viewing leave form for applying for leave
    document.getElementById("leaveForm").addEventListener("submit", function(event) {
        event.preventDefault();

        employeeId = localStorage.getItem("employee_id"); // Ensure employeeId is retrieved before sending request
        if (!employeeId) {
            alert(" Error: Employee ID is missing!");
            return;
        }

        const leaveRequest = {
            employee: { id: employeeId }, // Correct structure for backend
            startDate: document.getElementById("startDate").value,
            endDate: document.getElementById("endDate").value,
            description: document.getElementById("description").value,
            status: "PENDING"
        };

        console.log(" Sending Leave Request:", leaveRequest);

        fetch(`http://localhost:9091/leaverequests`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(leaveRequest)
            })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(`Error ${response.status}: ${text}`); });
                }
                return response.json();
            })
            .then(data => {
                alert("Leave request submitted successfully!");
                console.log("Leave Request API Response:", data);
                document.getElementById("leaveForm").reset();

                // Refresh leave requests and update availability after submitting
                fetchLeaveRequests();
            })
            .catch(error => {
                console.error("Error applying for leave:", error);
                alert(" Error submitting leave request. Check console for details.");
            });
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
