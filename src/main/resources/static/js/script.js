// Функция для редактирования пользователя
function editUser(userId) {
    // Получаем данные пользователя для редактирования через fetch
    fetch(`/admin/edituser/${userId}`)
        .then(response => response.json())  // Преобразуем JSON в объект
        .then(user => {
            // Заполняем поля в модальном окне данными пользователя
            document.getElementById("editId").value = user.id;
            document.getElementById("editFirstName").value = user.firstName;
            document.getElementById("editLastName").value = user.lastName;
            document.getElementById("editAge").value = user.age;
            document.getElementById("editEmail").value = user.email;
            document.getElementById("editPassword").value = '';  // Очищаем поле пароля
            document.getElementById("editRole").value = user.role;

            // Открываем модальное окно
            var myModal = new bootstrap.Modal(document.getElementById('editUserModal'), {
                keyboard: false
            });
            myModal.show();

            // Обрабатываем отправку формы
            document.getElementById("editUserForm").onsubmit = function (event) {
                event.preventDefault();  // Отменяем стандартное поведение формы

                // Получаем новые значения из формы
                const updatedUser = {
                    id: userId,
                    firstName: document.getElementById("editFirstName").value,
                    lastName: document.getElementById("editLastName").value,
                    email: document.getElementById("editEmail").value,
                    age: document.getElementById("editAge").value,
                    password: document.getElementById("editPassword").value,  // Мы можем оставить это пустым, если не хотим изменять пароль
                    role: document.getElementById("editRole").value
                };

                // Отправляем обновленные данные на сервер
                fetch(`/admin/edituser`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(updatedUser),  // Преобразуем объект в JSON
                })
                    .then(response => {
                        if (response.ok) {
                            loadUsers();  // Перезагружаем таблицу после редактирования
                            alert('User updated successfully');
                            myModal.hide();  // Закрываем модальное окно
                        } else {
                            alert('Error editing user');
                        }
                    })
                    .catch(error => console.error('Error editing user:', error));
            }
        })
        .catch(error => console.error('Error fetching user:', error));
}

// Функция для получения всех пользователей
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
                        <td>${user.firstName}</td>
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

// Загружаем пользователей при старте страницы
document.addEventListener('DOMContentLoaded', function () {
    loadUsers();  // Вызываем функцию для загрузки пользователей
});

