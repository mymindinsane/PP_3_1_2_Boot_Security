// Функция для редактирования пользователя
function editUser(userId) {
    // Получаем данные пользователя для редактирования через fetch
    fetch(`/admin/edituser?id=${userId}`)
        .then(response => response.json())  // Преобразуем JSON в объект
        .then(user => {
            // Заполняем поля в модальном окне данными пользователя
            document.getElementById("editId").value = user.id;
            document.getElementById("editUserName").value = user.username;
            document.getElementById("editAge").value = user.age;
            document.getElementById("editEmail").value = user.email;
            document.getElementById("editPassword").value = user.password;  // Очищаем поле пароля

            // Устанавливаем чекбоксы для ролей
            document.getElementById("roleUser").checked = user.roles.some(role => role.roleName === 'ROLE_USER');
            document.getElementById("roleAdmin").checked = user.roles.some(role => role.roleName === 'ROLE_ADMIN');

            // Открываем модальное окно
            var myModal = new bootstrap.Modal(document.getElementById('editUserModal'), {
                keyboard: false
            });
            myModal.show();
        })
        .catch(error => console.error('Error fetching user:', error));
}

// Обработчик отправки формы
document.getElementById("editUserForm").onsubmit = function (event) {
    event.preventDefault();  // Отменяем стандартное поведение формы

    // Получаем ID пользователя из поля
    const userId = document.getElementById("editId").value;

    const updatedUser = {
        id: userId,
        username: document.getElementById("editUserName").value,
        email: document.getElementById("editEmail").value,
        age: document.getElementById("editAge").value,
        password: document.getElementById("editPassword").value || '',  // Если пароль пустой, отправляем пустую строку
        roles: Array.from(document.querySelectorAll("#editRoles input:checked")).map(role => {
            return { roleName: role.value };  // Преобразуем строки в объекты с ролью
        })
    };

    console.log(updatedUser.roles);  // Проверка того, что мы правильно получили роли

// Отправляем обновленные данные на сервер
    fetch('/admin/edituserPUT', {
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
                var myModal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
                myModal.hide();  // Закрываем модальное окно
            } else {
                alert('Error editing user. Status: ' + response.status);  // Выводим статус ошибки
            }
        })
        .catch(error => console.error('Error editing user:', error));

};

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
                    row.innerHTML =
                        `<td>${user.id}</td>
                         <td>${user.username}</td>
                         <td>${user.email}</td>
                         <td>${user.age}</td>
                         <td>${user.roles.map(role => role.roleName).join(', ')}</td>
                         <td>
                             <button class="edit-btn btn btn-warning" data-id="${user.id}" onclick="editUser(${user.id})">Edit</button>
                             <button class="delete-btn btn btn-danger" data-id="${user.id}" onclick="deleteUser(${user.id})">Delete</button>
                         </td>`;
                    tableBody.appendChild(row);
                });
            } else {
                console.error('Полученные данные пусты или некорректны:', data);
            }
        })
        .catch(error => console.error('Error loading users:', error));
}

// Функция для отображения данных пользователя в модальном окне для удаления
function deleteUser(userId) {
    fetch(`/admin/edituser?id=${userId}`)
        .then(response => response.json())
        .then(user => {
            // Заполняем поля данными пользователя
            document.getElementById("deleteId").value = user.id;
            document.getElementById("deleteUserName").value = user.username;// Используй соответствующие поля
            document.getElementById("deleteAge").value = user.age;
            document.getElementById("deleteEmail").value = user.email;

            document.getElementById("deleteRoleUser").checked = user.roles.some(role => role.roleName === 'ROLE_USER');
            document.getElementById("deleteRoleAdmin").checked = user.roles.some(role => role.roleName === 'ROLE_ADMIN');

            var myModal = new bootstrap.Modal(document.getElementById('deleteUserModal'), {
                keyboard: false
            });
            myModal.show();
        })
        .catch(error => console.error('Error fetching user data for deletion:', error));
}

// Функция для отправки запроса на удаление пользователя
document.getElementById("deleteUserForm").onsubmit = function (event) {
    event.preventDefault();

    const userId = document.getElementById("deleteId").value;

    // Отправляем запрос на удаление пользователя
    fetch(`/admin/delete?id=${userId}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (response.ok) {
                loadUsers();  // Перезагружаем таблицу после удаления
                alert('User deleted successfully');
                var myModal = bootstrap.Modal.getInstance(document.getElementById('deleteUserModal'));
                myModal.hide();  // Закрываем модальное окно
            } else {
                alert('Error deleting user');
            }
        })
        .catch(error => console.error('Error deleting user:', error));
};


// Загружаем пользователей при старте страницы
document.addEventListener('DOMContentLoaded', function () {
    loadUsers();  // Вызываем функцию для загрузки пользователей
});
