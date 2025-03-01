function editUser(userId) {

    fetch(`/admin/edituser?id=${userId}`)
        .then(response => response.json())
        .then(user => {

            document.getElementById("editId").value = user.id;
            document.getElementById("editUserName").value = user.username;
            document.getElementById("editAge").value = user.age;
            document.getElementById("editEmail").value = user.email;
            document.getElementById("editPassword").value = user.password;


            document.getElementById("roleUser").checked = user.roles.some(role =>
                role.roleName === 'ROLE_USER');
            document.getElementById("roleAdmin").checked = user.roles.some(role =>
                role.roleName === 'ROLE_ADMIN');


            var myModal = new bootstrap.Modal(document.getElementById('editUserModal'), {
                keyboard: false
            });
            myModal.show();
        })
        .catch(error => console.error('Error fetching user:', error));
}


document.getElementById("editUserForm").onsubmit = function (event) {
    event.preventDefault();

    const userId = document.getElementById("editId").value;

    const updatedUser = {
        id: userId,
        username: document.getElementById("editUserName").value,
        email: document.getElementById("editEmail").value,
        age: document.getElementById("editAge").value,
        password: document.getElementById("editPassword").value || '',
        roles: Array.from(document.querySelectorAll("#editRoles input:checked")).map(role => {
            return {roleName: role.value};
        })
    };

    console.log(updatedUser.roles);


    fetch('/admin/edituserPUT', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedUser),
    })
        .then(response => {
            if (response.ok) {
                loadUsers();
                alert('User updated successfully');
                var myModal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
                myModal.hide();
            } else {
                alert('Error editing user. Status: ' + response.status);
            }
        })
        .catch(error => console.error('Error editing user:', error));

};


function loadUsers() {
    fetch('/admin/allusers')
        .then(response => response.json())
        .then(data => {
            console.log(data);

            const tableBody = document.querySelector('#users-table tbody');
            tableBody.innerHTML = '';

            if (data && data.length) {
                data.forEach(user => {
                    const row = document.createElement('tr');
                    row.innerHTML =
                        `<td>${user.id}</td>
                         <td>${user.username}</td>
                         <td>${user.email}</td>
                         <td>${user.age}</td>
                         <td>${user.roles.map(role => role.roleName).join(', ')}</td>
                         <td>
                             <button class="edit-btn btn btn-warning" data-id="${user.id}" 
                             onclick="editUser(${user.id})">Edit</button>
                             <button class="delete-btn btn btn-danger" data-id="${user.id}" 
                             onclick="deleteUser(${user.id})">Delete</button>
                         </td>`;
                    tableBody.appendChild(row);
                });
            } else {
                console.error('Полученные данные пусты или некорректны:', data);
            }
        })
        .catch(error => console.error('Error loading users:', error));
}


function deleteUser(userId) {
    fetch(`/admin/edituser?id=${userId}`)
        .then(response => response.json())
        .then(user => {

            document.getElementById("deleteId").value = user.id;
            document.getElementById("deleteUserName").value = user.username;
            document.getElementById("deleteAge").value = user.age;
            document.getElementById("deleteEmail").value = user.email;

            document.getElementById("deleteRoleUser").checked = user.roles.some(role =>
                role.roleName === 'ROLE_USER');
            document.getElementById("deleteRoleAdmin").checked = user.roles.some(role =>
                role.roleName === 'ROLE_ADMIN');

            var myModal = new bootstrap.Modal(document.getElementById('deleteUserModal'), {
                keyboard: false
            });
            myModal.show();
        })
        .catch(error => console.error('Error fetching user data for deletion:', error));
}


document.getElementById("deleteUserForm").onsubmit = function (event) {
    event.preventDefault();

    const userId = document.getElementById("deleteId").value;


    fetch(`/admin/delete?id=${userId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                loadUsers();
                alert('User deleted successfully');
                var myModal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
                myModal.hide();
            } else {
                alert('Error deleting user');
            }
        })
        .catch(error => console.error('Error deleting user:', error));
};

function loadContent(url, element) {
    document.querySelectorAll('.nav-link').forEach(link =>
        link.classList.remove('active'));


    element.classList.add('active');

    if (url === '/admin/allusers') {
        document.getElementById('newUserFormContainer').style.display = 'none';
        document.getElementById('contentArea').style.display = 'block';
        loadUsers(); // Загружаем список пользователей
    } else if (url === '/admin/adduser') {
        document.getElementById('newUserFormContainer').style.display = 'block';
        document.getElementById('contentArea').style.display = 'none';
    }
}

document.getElementById('newUserForm').onsubmit = function (event) {
    event.preventDefault();
    const newUser = {
        username: document.getElementById('newUserName').value,
        age: document.getElementById('newUserAge').value,
        email: document.getElementById('newUserEmail').value,
        password: document.getElementById('newUserPassword').value,
        roles: []
    };


    if (document.getElementById('roleUserAdd').checked) {
        newUser.roles.push('ROLE_USER');
    }
    if (document.getElementById('roleAdminAdd').checked) {
        newUser.roles.push('ROLE_ADMIN');
    }

    fetch('/admin/adduser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(newUser)
    })
        .then(response => {
            if (response.ok) {
                alert('New user added successfully');
                loadUsers();
                loadContent('/admin/allusers', document.querySelector('.nav-link'));
            } else {
                alert('Error adding new user');
            }
        })
        .catch(error => console.error('Error:', error));
};


document.addEventListener('DOMContentLoaded', function () {
    loadUsers();
});
