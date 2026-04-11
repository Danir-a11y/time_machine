// ===== Получение имени из URL
function getNameFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get("name");
}

// ===== Поиск прямо на странице
function searchPerson() {
    const name = document.getElementById("searchInput").value;
    if (!name) return;

    window.location.href = `person_extra.html?name=${encodeURIComponent(name)}`;
}

// ===== Получение краткой инфы
async function loadPerson(name) {
    const url = `https://ru.wikipedia.org/api/rest_v1/page/summary/${encodeURIComponent(name)}`;

    try {
        const res = await fetch(url);
        if (!res.ok) return null;
        return await res.json();
    } catch {
        return null;
    }
}

function formatDate(dateString) {
    if (!dateString) return "—";

    try {
        // если приходит ISO строка → делаем Date
        const date = new Date(dateString);

        if (isNaN(date)) return dateString; // если вдруг криво

        const day = String(date.getDate()).padStart(2, "0");
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const year = date.getFullYear();

        return `${day}.${month}.${year}`;
    } catch {
        return dateString;
    }
}
// ===== Получение даты рождения и смерти
async function loadBirthDeath(name) {
    try {
        // 1. получаем wikidata id
        const wikiRes = await fetch(
            `https://ru.wikipedia.org/w/api.php?action=query&titles=${encodeURIComponent(name)}&prop=pageprops&format=json&origin=*`
        );
        const wikiData = await wikiRes.json();

        const page = Object.values(wikiData.query.pages)[0];

        if (!page.pageprops || !page.pageprops.wikibase_item) {
            return { birth: null, death: null };
        }

        const wikidataId = page.pageprops.wikibase_item;

        // 2. получаем данные
        const dataRes = await fetch(
            `https://www.wikidata.org/wiki/Special:EntityData/${wikidataId}.json`
        );
        const data = await dataRes.json();

        const entity = data.entities[wikidataId];
        const claims = entity.claims;

        let birth = null;
        let death = null;

        if (claims.P569) {
            birth = claims.P569[0].mainsnak.datavalue.value.time.slice(1, 11);
        }

        if (claims.P570) {
            death = claims.P570[0].mainsnak.datavalue.value.time.slice(1, 11);
        }

        return { birth, death };


    } catch (e) {
        return { birth: null, death: null };
    }
}

// ===== Рендер человека
function renderPerson(data) {
    const container = document.getElementById("personContent");

    container.innerHTML = `
        <h2>${data.title}</h2>
        ${data.thumbnail ? `<img src="${data.thumbnail.source}" width="200">` : ""}
        <p>${data.extract}</p>
    `;
}

function goHome() {
    window.location.href = "index.html";
}

// ===== Рендер дат
function renderDates(dates) {
    const list = document.getElementById("randomDates");

    list.innerHTML = `
        <li><b>Дата рождения:</b> ${formatDate(dates.birth) || "неизвестно"}</li>
        <li><b>Дата смерти:</b> ${formatDate(dates.death) || "—"}</li>
    `;
}

// ===== ИНИЦИАЛИЗАЦИЯ
async function init() {
    const name = getNameFromURL();
    if (!name) return;

    const person = await loadPerson(name);
    if (!person) return;

    renderPerson(person);

    const dates = await loadBirthDeath(person.title);
    renderDates(dates);
}

init();