let isLogin = false;

const form = document.getElementById("authForm");
const title = document.getElementById("title");
const switchText = document.getElementById("switchText");
const message = document.getElementById("message");

switchText.addEventListener("click", () => {
    isLogin = !isLogin;

    title.innerText = isLogin ? "Вход" : "Регистрация";
    switchText.innerText = isLogin
        ? "Нет аккаунта? Зарегистрироваться"
        : "Уже есть аккаунт? Войти";
});

form.addEventListener("submit", async (e) => {
    e.preventDefault();

    message.innerHTML = "";

    const formData = new FormData(form);

    const url = isLogin
        ? "/api/login"
        : "/api/register";

    try {
        const response = await fetch(url, {
            method: "POST",
            body: formData
        });

        const text = await response.text(); // ← ОБЯЗАТЕЛЬНО

        message.innerHTML = `<div class="card">${text}</div>`;

    } catch (error) {
        message.innerHTML = `<div class="error">Ошибка соединения</div>`;
    }
});
form.addEventListener("submit", async (e) => {
    e.preventDefault();

    message.innerHTML = "";

    const formData = new FormData(form);

    const url = isLogin
        ? "/api/login"
        : "/api/register";

    try {
        const response = await fetch(url, {
            method: "POST",
            body: formData
        });

        const text = await response.text();

        if (text === "USERNAME_TAKEN") {
            alert("Данное имя уже используется");
            return;
        }

        if (text === "OK" || text === "Успешный вход") {
            // сохраняем пользователя
            localStorage.setItem("user", formData.get("username"));

            // редирект
            window.location.href = "/api/index.html";
            return;
        }

        message.innerHTML = `<div class="card">${text}</div>`;

    } catch (error) {
        message.innerHTML = `<div class="error">Ошибка соединения</div>`;
    }
});
function goHome() {
    window.location.href = "/api/index.html";
}