// Получаем параметры из URL
function getQueryParams() {
    const params = new URLSearchParams(window.location.search);
    return {
        name: params.get("name")
    };
}

// 🧹 Очищаем имя (убираем "(р. 1937)" и т.п.)
function cleanName(name) {
    if (!name) return "";
    return name.split("(")[0].trim();
}

// 🌐 Запрос к Википедии
async function loadPersonData(name) {
    try {
        const url = `https://ru.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(name)}`;

        const res = await fetch(url);

        if (!res.ok) {
            throw new Error("Ошибка загрузки");
        }

        return await res.json();
    } catch (err) {
        console.error(err);
        return null;
    }
}

// 🎨 Рендер
function renderPerson(data, name) {
    const container = document.getElementById("personContent");

    if (!data) {
        container.innerHTML = "<p>Не удалось загрузить данные</p>";
        return;
    }

    container.innerHTML = `
    <div class="person-card">
      <h2>${data.title}</h2>

      ${data.thumbnail ? `
        <img src="${data.thumbnail.source}" class="person-image">
      ` : ""}

      <p class="person-description">
        ${data.extract || "Описание отсутствует"}
      </p>

      <a href="${data.content_urls.desktop.page}" target="_blank" class="wiki-link">
        🔗 Читать на Википедии
      </a>
    </div>
  `;
}

// 🚀 Инициализация
async function init() {
    const { name } = getQueryParams();

    if (!name) {
        document.getElementById("personContent").innerHTML = "<p>Имя не передано</p>";
        return;
    }

    const decodedName = decodeURIComponent(name);
    const cleanedName = cleanName(decodedName);

    // Показываем имя
    document.getElementById("personName").textContent = cleanedName;

    // Загружаем данные
    const data = await loadPersonData(cleanedName);

    // Рендерим
    renderPerson(data, cleanedName);
}

// запуск
init();