document.addEventListener("DOMContentLoaded", function() {
    fetchEmployees();
    fetchProjects();

    document.getElementById("showFormButton").addEventListener("click", function() {
        document.getElementById("employeeFormContainer").style.display = "block";
    });

    document.getElementById("showProjectFormButton").addEventListener("click", function() {
        document.getElementById("projectFormContainer").style.display = "block";
    });

    document.getElementById("saveEmployeeButton").addEventListener("click", saveEmployee);
    document.getElementById("saveProjectButton").addEventListener("click", saveProject);
});

function fetchEmployees() {
    fetch("http://localhost:9091/employees")
        .then(response => response.json())
        .then(data => {
            const employeeTableBody = document.getElementById("employeeTableBody");
            employeeTableBody.innerHTML = "";
            data.forEach(employee => {
                employeeTableBody.innerHTML += `
                    <tr>
                        <td>${employee.id}</td>
                        <td>${employee.name}</td>
                        <td>${employee.email}</td>
                        <td>${employee.role}</td>
                        <td>${employee.skills ? employee.skills.join(", ") : ""}</td>
                        <td>
                            <button onclick="editEmployee(${employee.id}, '${employee.name}', '${employee.email}', '${employee.role}', '${employee.skills.join(", ")}')">Update</button>
                            <button onclick="deleteEmployee(${employee.id})">Delete</button>
                        </td>
                    </tr>`;
            });
        });
}

function saveEmployee() {
    const id = document.getElementById("employeeId").value;
    const skillsValue = document.getElementById("skills").value.trim();

    const employee = {
        name: document.getElementById("name").value.trim(),
        email: document.getElementById("email").value.trim(),
        role: document.getElementById("role").value.trim(),
        skills: skillsValue ? skillsValue.split(",").map(skill => skill.trim()) : []
    };

    const method = id ? "PUT" : "POST";
    const url = id ? `http://localhost:9091/employees/update/${id}` : "http://localhost:9091/employees/add";

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(employee)
    }).then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || "Failed to save employee"); });
        }
        return response.json();
    }).then(() => {
        alert(id ? "Employee Updated" : "Employee Added");
        fetchEmployees();
        document.getElementById("employeeFormContainer").style.display = "none";
    }).catch(error => console.error("Error saving employee:", error));
}

function editEmployee(id, name, email, role, skills) {
    document.getElementById("employeeId").value = id;
    document.getElementById("name").value = name;
    document.getElementById("email").value = email;
    document.getElementById("role").value = role;
    document.getElementById("skills").value = skills;
    document.getElementById("employeeFormContainer").style.display = "block";
}

function deleteEmployee(id) {
    if (confirm("Are you sure you want to delete this employee?")) {
        fetch(`http://localhost:9091/employees/${id}`, {
            method: "DELETE"
        }).then(() => {
            alert("Employee Deleted");
            fetchEmployees();
        });
    }
}

function fetchProjects() {
    fetch("http://localhost:9091/projects")
        .then(response => response.json())
        .then(data => {
            const projectTableBody = document.getElementById("projectTableBody");
            projectTableBody.innerHTML = "";
            data.forEach(project => {
                projectTableBody.innerHTML += `
                    <tr>
                        <td>${project.id}</td>
                        <td>${project.name}</td>
                        <td>${project.requiredSkills ? project.requiredSkills.join(", ") : ""}</td>
                        <td>
                            <button onclick="editProject(${project.id}, '${project.name}', '${project.requiredSkills.join(", ")}')">Update</button>
                            <button onclick="deleteProject(${project.id})">Delete</button>
                            <button onclick="fetchSuggestedEmployees(${project.id})">Suggest Employees</button>
                        </td>
                    </tr>
                    <tr id="suggested-employees-${project.id}" style="display: none;">
                        <td colspan="4">
                            <div id="suggested-list-${project.id}"></div>
                        </td>
                    </tr>`;
            });
        });
}

function saveProject() {
    const id = document.getElementById("projectId").value;
    const project = {
        name: document.getElementById("projectName").value.trim(),
        requiredSkills: document.getElementById("requiredSkills").value.trim() ? document.getElementById("requiredSkills").value.split(",").map(skill => skill.trim()) : []
    };

    const method = id ? "PUT" : "POST";
    const url = id ? `http://localhost:9091/projects/update/${id}` : "http://localhost:9091/projects";

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(project)
    }).then(() => {
        alert(id ? "Project Updated" : "Project Added");
        fetchProjects();
        document.getElementById("projectFormContainer").style.display = "none";
    });
}

function editProject(id, name, requiredSkills) {
    document.getElementById("projectId").value = id;
    document.getElementById("projectName").value = name;
    document.getElementById("requiredSkills").value = requiredSkills.join(", ");
    document.getElementById("projectFormContainer").style.display = "block";
}

function deleteProject(id) {
    if (confirm("Are you sure you want to delete this project?")) {
        fetch(`http://localhost:9091/projects/${id}`, {
            method: "DELETE"
        }).then(() => {
            alert("Project Deleted");
            fetchProjects();
        });
    }
}

// Fetch suggested employees for a project
function fetchSuggestedEmployees(projectId) {
    const suggestionsRow = document.getElementById(`suggested-employees-${projectId}`);
    const suggestionsList = document.getElementById(`suggested-list-${projectId}`);

    if (suggestionsRow.style.display === "none") {
        fetch(`http://localhost:9091/api/match/${projectId}`)
            .then(response => response.json())
            .then(data => {
                if (data.length === 0) {
                    suggestionsList.innerHTML = `<p>No matching employees found.</p>`;
                } else {
                    suggestionsList.innerHTML = "";
                    data.forEach(employee => {
                        suggestionsList.innerHTML += `
                            <div>
                                <span>${employee.name} - ${employee.skills ? employee.skills.join(", ") : "No Skills"}</span>
                                <button onclick="assignEmployeeToProject(${employee.id}, ${projectId})">Assign</button>
                            </div>`;
                    });
                }
            });
        suggestionsRow.style.display = "table-row"; // Show the suggestions row
    } else {
        suggestionsRow.style.display = "none"; // Hide the suggestions row
    }
}

// Assign employee to project
function assignEmployeeToProject(employeeId, projectId) {
    if (!employeeId || !projectId) {
        console.error('Invalid employee ID or project ID');
        return;
    }

    fetch(`http://localhost:9091/api/allocations/assign/${employeeId}/${projectId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => {
            if (!response.ok) {
                console.error('Error response:', response);
                return response.json().then(err => { throw new Error(err.message || 'Failed to assign employee'); });
            }
            return response.json();
        })
        .then(data => {
            console.log('Employee assigned successfully:', data);
        })
        .catch(error => {
            console.error('Error:', error.message);
        });
}

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
document.addEventListener("DOMContentLoaded", function() {
    fetchLeaveRequests();
});

function fetchLeaveRequests() {
    fetch("http://localhost:9091/leaverequests")
        .then(response => response.json())
        .then(data => {
            console.log("Leave Requests Data:", data);
            const leaveTableBody = document.getElementById("leaveRequestTableBody");
            leaveTableBody.innerHTML = "";

            data.forEach(leave => {
                console.log("Processing Leave:", leave);

                const employeeName = leave.employee && leave.employee.name ? leave.employee.name : "Unknown";
                const startDate = leave.startDate ? new Date(leave.startDate).toLocaleDateString() : "N/A";
                const endDate = leave.endDate ? new Date(leave.endDate).toLocaleDateString() : "N/A";

                leaveTableBody.innerHTML += `
                    <tr>
                        <td>${leave.id}</td>
                        <td>${employeeName}</td>
                        <td>${startDate}</td>
                        <td>${endDate}</td>
                        <td>${leave.status || "Pending"}</td>
                        <td>
                            <button onclick="updateLeaveStatus(${leave.id}, 'approve')">Approve</button>
                            <button onclick="updateLeaveStatus(${leave.id}, 'reject')">Reject</button>
                        </td>
                    </tr>`;
            });
        })
        .catch(error => console.error("Error fetching leave requests:", error));
}

function updateLeaveStatus(id, action) {
    let url = `http://localhost:9091/leaverequests/${id}/${action}`;

    fetch(url, {
        method: "PUT",
        headers: { "Content-Type": "application/json" }
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error("Failed to update leave status");
        }
    }).then(updatedLeave => {
        alert(`Leave ${action}d successfully`);

        if (updatedLeave && updatedLeave.employee_id) {
            if (action === "approve") {
                updateEmployeeAvailability(updatedLeave.employee_id, false);
            } else if (action === "reject") {
                updateEmployeeAvailability(updatedLeave.employee_id, true);
            }
        } else {
            console.error("Error: Employee ID missing in response", updatedLeave);
        }

        fetchLeaveRequests();
    }).catch(error => console.error("Error updating leave:", error));
}

function updateEmployeeAvailability(employeeId, availability) {
    fetch(`http://localhost:9091/employees/${employeeId}/availability`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ availability: availability })
    }).then(response => {
        if (!response.ok) {
            throw new Error("Failed to update employee availability");
        }
    }).catch(error => console.error("Error updating employee availability:", error));
}

function saveLeaveRequest() {
    const leave = {
        employee_id: document.getElementById("leaveEmployeeId").value,
        start_date: document.getElementById("leaveStartDate").value,
        end_date: document.getElementById("leaveEndDate").value
    };

    fetch("http://localhost:9091/leaverequests", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(leave)
    }).then(() => {
        alert("Leave Request Submitted");
        fetchLeaveRequests();
        document.getElementById("leaveFormContainer").style.display = "none";
    }).catch(error => console.error("Error saving leave request:", error));
}

function populateLeaveRequestsTable(leaveRequests) {
    const tableBody = document.getElementById("leaveRequestsTableBody");
    tableBody.innerHTML = ""; // Clear existing rows

    leaveRequests.forEach(leave => {
        const row = document.createElement("tr");

        // Extract employee name safely
        const employeeName = leave.employee && leave.employee.name ? leave.employee.name : "Unknown";
        const startDate = leave.startDate ? new Date(leave.startDate).toLocaleDateString() : "N/A";
        const endDate = leave.endDate ? new Date(leave.endDate).toLocaleDateString() : "N/A";
        const status = leave.status || "Pending";
        const description = leave.description || "No description";

        row.innerHTML = `
            <td>${leave.id}</td>
            <td>${employeeName}</td>
            <td>${startDate}</td>
            <td>${endDate}</td>
            <td>${status}</td>
            <td>${description}</td>
            <td>
                <button onclick="updateLeaveStatus(${leave.id}, 'APPROVED')">Approve</button>
                <button onclick="updateLeaveStatus(${leave.id}, 'REJECTED')">Reject</button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}