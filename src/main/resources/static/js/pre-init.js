let GoogleAuth // Google Auth object
let authorized = false
let initialized = false
function preloaderOn () {
    $(".preloader").css("display", "block")
}
function preloaderOff () {
    $(".preloader").css("display", "none")
}
// функция-слушатель,
// которая вызывается при каждом изменении
// состояния аутентификации на фронтенде
// , при этом получая через аргумент состояние аутентификации
function checkSignInStatus (isSignedIn) {
    preloaderOn()
    // если состояние переключилось в "вошел"
    if (isSignedIn) {
        //hide login button
        $('div#g-signin2').hide()
        //show logout button
        $('#signout').show()
    } else {
        //show login button
        $('div#g-signin2').show()
        //hide logout button
        $('#signout').hide()
        fetch('/api/auth/user/signedout', {
            method: 'GET'
        }).then((response) => {
            return response.status
        }).then(responseStatusCode => {
            if (responseStatusCode) {
                if (responseStatusCode === 200) {
                    preloaderOn()
                    $("#username").text("")
                    $("#useremail").text("")
                    $("#userpicture").attr("src", "images/hourglass.jpeg")
                }
            }
        }).catch((error) => {
            console.log(error.message)
            throw error
        }).finally(() => {
            preloaderOff()
        })
    }
}
// инициализация фронтенд-логики гугл-аутентификации
function init () {
    preloaderOn()
    gapi.load('auth2', function() {
        $('.dropdown-tyaa').click(function(){
            $(this).find('.dropdown-content-tyaa').stop().slideToggle(400);
        });
        // получение объекта управления аутентификацией гугл
        GoogleAuth = gapi.auth2.getAuthInstance();
        // устанавливаем функцию-слушатель,
        // которая будет вызываться при каждом изменении
        // состояния аутентификации на фронтенде
        GoogleAuth.isSignedIn.listen(checkSignInStatus);
        // получение текущего состояния аутентификации
        const isSignedIn = GoogleAuth.isSignedIn.get();
        // ... и передача его на анализ
        checkSignInStatus(isSignedIn);
    });
}