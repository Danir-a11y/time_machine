const API_URL = "http://localhost:8080/api/history/events";

let date = null;

const eventsEl = document.getElementById("events");
const moviesEl = document.getElementById("movies");
const birthsEl = document.getElementById("births");
const deathsEl = document.getElementById("deaths");
const loadBtn = document.getElementById("loadBtn");
const dateInput = document.getElementById("dateInput");
const loadingEl = document.getElementById("loading");

loadBtn.onclick = () => {
  date = dateInput.value;
  if (!date) {
    alert("Выбери дату");
    return;
  }
  load();
};

// Функция показа/скрытия загрузки
function showLoading() {
  loadingEl.style.display = 'flex';
}

function hideLoading() {
  loadingEl.style.display = 'none';
}

async function load() {
  // Очищаем предыдущие результаты
  eventsEl.innerHTML = "";
  moviesEl.innerHTML = "";
  birthsEl.innerHTML = "";
  deathsEl.innerHTML = "";

  // Показываем индикатор загрузки
  showLoading();

  // Блокируем кнопку на время загрузки
  loadBtn.disabled = true;
  loadBtn.textContent = "Загрузка...";

  try {
    const url = `${API_URL}?date=${date}`;
    const res = await fetch(url);

    if (!res.ok) {
      throw new Error("Ошибка загрузки данных");
    }

    const data = await res.json();

    renderEvents(data.historicalEvents || []);
    renderMovies(data.movies || []);
    renderBirths(data.births || []);
    renderDeaths(data.deaths || []);
  } catch (err) {
    eventsEl.innerHTML = "<p class='error'>Ошибка загрузки данных. Попробуйте еще раз.</p>";
    console.error(err);
  } finally {
    // Всегда скрываем индикатор и разблокируем кнопку
    hideLoading();
    loadBtn.disabled = false;
    loadBtn.textContent = "Отправиться в прошлое";
  }
}

// Остальные функции renderEvents, renderMovies и т.д. остаются без изменений
function renderEvents(events) {
  eventsEl.innerHTML = "";

  if (!events.length) {
    eventsEl.innerHTML = "<p>Событий нет</p>";
    return;
  }

  events.forEach(e => {
    eventsEl.innerHTML += `
      <a href="${e.wikiUrl || '#'}" target="_blank" class="event-link">
        <div class="event">
          ${e.photoUrl ? `
            <div class="event-image-wrapper">
              <img src="${e.photoUrl}" alt="${e.description}" onerror="this.style.display='none'">
              <div class="event-overlay">
                <span class="event-overlay-text">🔗 Открыть на Википедии</span>
              </div>
            </div>` : ""}
          <div class="event-description">${e.description}</div>
        </div>
      </a>
    `;
  });
}



function renderMovies(movies) {
  moviesEl.innerHTML = "";

  if (!movies.length) {
    moviesEl.innerHTML = "<p>Нет фильмов</p>";
    return;
  }

  movies.forEach(m => {

    const genresText = (m.genres && m.genres.length)
      ? m.genres.join(", ")
      : "Жанр неизвестен";

    const durationText = m.duration
      ? `${m.duration} мин`
      : "Длительность неизвестна";

    const kpUrl = `https://www.kinopoisk.ru/film/${m.kinopoiskId}/`;

    moviesEl.innerHTML += `
      <div class="movie clickable" onclick="window.open('${kpUrl}', '_blank')">
        <div class="movie-image-wrapper">
          <img src="${m.photoUrl}" alt="${m.name}" onerror="this.style.display='none'">
          <div class="movie-overlay">
            <span class="movie-overlay-text">🔗 Открыть в Кинопоиске</span>
          </div>
        </div>
        <div class="movie-title">${m.name}</div>
        <div class="movie-meta">${genresText}</div>
        <div class="movie-meta">${durationText}</div>
      </div>
    `;
  });
}

function openPersonPage(name) {
  window.location.href = `person_extra.html?name=${encodeURIComponent(name)}`;
}

function searchPerson() {
  const name = document.getElementById("searchInput").value;
  if (!name) return;

  window.location.href = `person_extra.html?name=${encodeURIComponent(name)}`;
}

function getCleanName(name) {
  return name
      .split("(")[0]
      .replace(",", "")
      .trim()
      .split(" ")
      .slice(0, 2)
      .join(" ");
}

function renderBirths(births) {
  birthsEl.innerHTML = "";

  if (!births.length) {
    birthsEl.innerHTML = "<p>Нет данных</p>";
    return;
  }

  births.forEach(b => {
    const imgUrl = b.photoUrl || "";
    const wikiUrl = b.wikiUrl || "#";
    const name = b.name || b.text || "Неизвестно";
    const year = b.year || "неизвестен";
    const occupation = b.occupation || "—";

    birthsEl.innerHTML += `
      <div class="person">
    
        ${imgUrl ? `
          <a href="${wikiUrl}" target="_blank" class="person-image-wrapper">
            <img src="${imgUrl}" alt="${name}" onerror="this.style.display='none'">
            <div class="person-overlay">
              <span class="person-overlay-text">Открыть на Википедии</span>
            </div>
          </a>
        ` : ''}
    
        <div class="person-name">${name}</div>
    
        <div class="person-actions">
          <span class="person-life-btn"
            onclick="openPersonPage(getCleanName('${name}'))">
            😀
          </span>
        </div>
    
      </div>
    `;
  });
}

function renderDeaths(deaths) {
  deathsEl.innerHTML = "";

  if (!deaths.length) {
    deathsEl.innerHTML = "<p>Нет данных</p>";
    return;
  }

  deaths.forEach(d => {
    const imgUrl = d.photoUrl || "";
    const wikiUrl = d.wikiUrl || "#";
    const name = d.name || "Неизвестно";
    const year = d.year || "неизвестен";
    const cause = d.cause || "—";

    deathsEl.innerHTML += `
      <a href="${wikiUrl}" target="_blank" class="person-link">
        <div class="person">
          ${imgUrl ? `<div class="person-image-wrapper">
                        <img src="${imgUrl}" alt="${name}" onerror="this.style.display='none'">
                        <div class="person-overlay"><span class="person-overlay-text">🔗 Открыть на Википедии</span></div>
                      </div>` : ""}
          <div class="person-name">${name}</div>
          <div class="person-actions">
            <span class="person-life-btn"
                  onclick="openPersonPage(getCleanName('${name}'))">
              Подробнее...
            </span>
          </div>
        </div>
      </a>
    `;
  });
}