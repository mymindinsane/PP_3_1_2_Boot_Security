// Функция для загрузки данных пользователей и отображения в таблице
function loadUsers() {
    fetch('/admin/allusers')
        .then(response => response.json())  // Преобразуем JSON в объект
        .then(data => {
            console.log(data);  // Логируем полученные данные

            const tableBody = document.querySelector('#users-table tbody');
            tableBody.innerHTML = '';  // Очищаем таблицу перед добавлением новых данных

            // Теперь предполагаем, что 'data' - это массив
            // Проходим по всем пользователям и добавляем строки в таблицу
            if (data && data.length) {
                data.forEach(user => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>${user.age}</td>
                        <td>${user.roles.map(role => role.roleName).join(', ')}</td>
                        <td>
                            <button class="edit-btn btn btn-warning" data-id="${user.id}" onclick="editUser(${user.id})">Edit</button>
                            <button class="delete-btn btn btn-danger" data-id="${user.id}" onclick="deleteUser(${user.id})">Delete</button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });
            } else {
                console.error('Полученные данные пусты или некорректны:', data);
            }
        })
        .catch(error => console.error('Error loading users:', error));
}

// Функция для удаления пользователя
function deleteUser(userId) {
    if (confirm("Are you sure you want to delete this user?")) {
        fetch(`/admin/delete/${userId}`, {
            method: 'DELETE',
        })
            .then(response => {
                if (response.ok) {
                    loadUsers();  // Перезагружаем таблицу после удаления пользователя
                    alert('User deleted successfully');
                } else {
                    alert('Error deleting user');
                }
            })
            .catch(error => console.error('Error deleting user:', error));
    }
}

// Функция для редактирования пользователя
function editUser(userId) {
    const user = {
        id: userId,
        firstName: prompt('Enter new first name'),
        lastName: prompt('Enter new last name'),
        email: prompt('Enter new email'),
        age: prompt('Enter new age'),
        role: prompt('Enter new role')
    };

    fetch(`/admin/edituser`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    })
        .then(response => {
            if (response.ok) {
                loadUsers();  // Перезагружаем таблицу после редактирования
                alert('User updated successfully');
            } else {
                alert('Error editing user');
            }
        })
        .catch(error => console.error('Error editing user:', error));
}

// Функция для добавления нового пользователя
function addUser() {
    const user = {
        firstName: document.getElementById("first-name").value,
        lastName: document.getElementById("last-name").value,
        email: document.getElementById("email").value,
        age: document.getElementById("age").value,
        role: document.getElementById("role").value
    };

    fetch('/admin/adduser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user),
    })
        .then(response => {
            if (response.ok) {
                loadUsers();  // Перезагружаем таблицу после добавления
                alert('User added successfully');
            } else {
                alert('Error adding user');
            }
        })
        .catch(error => console.error('Error adding user:', error));
}

// Загружаем пользователей при старте страницы
document.addEventListener('DOMContentLoaded', function () {
    loadUsers();
});
