// функция реакции на отклик от сервера
// аутентификации гугл об успешном входе в учетную запись;
// googleUser - аргумент, который буден сформирован и передан автоматически,
// он содержит токен
function onSignIn(googleUser) {
    if (!initialized) {
        init()
        initialized = true
    }
    const id_token = googleUser.getAuthResponse().id_token
    // запрос на серверную часть нашего приложения
    // с передачей токена
    fetch('/api/auth/user/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({
            'idToken': id_token
        })
    }).then((response) => {
        return response.json()
    }).then(response => {
        if (response) {
            if (response.status === 'success') {
                // вывод открытых данных учетной записи в меню аутентификации
                $("#username").text(response.data.name)
                $("#useremail").text(response.data.email)
                $("#userpicture").attr("src", response.data.pictureUrl)
                authorized = true
            }
        }
    }).catch((error) => {
        console.log(error.message)
        throw error
    }).finally(() => {
        preloaderOff()
    })
}
$('#signout-li').click(signOut)
function signOut() {
    // $(document.body).find("section#find, section#create").html("")
    const auth2 = gapi.auth2.getAuthInstance()
    // отправка запроса на выход из учетной записи на Google Auth Server
    auth2.signOut().then(function () {
        // после получения отклика об успешном выходе из учетной записи от Google Auth Server
        // меняем значение флага "вошел" на отрицательное
        authorized = false
        // и скрываем элемент презагрузки
        preloaderOff()
    })
}
// при клике по стандартной кноке входа Гугл
$('div#g-signin2').click(function(){
    // начинаем отображение элемента презагрузки
    preloaderOn()
})