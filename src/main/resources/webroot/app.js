// Backend base URL (UPDATED PORT)
const API = "http://localhost:8070";

// DOM elements
const cityInput = document.getElementById("cityInput");
const latInput = document.getElementById("latInput");
const lonInput = document.getElementById("lonInput");
const result = document.getElementById("result");

function fetchWeatherByCity() {
  const city = cityInput.value.trim();
  if (!city) return alert("Enter city name");

  fetch(`${API}/api/weather?city=${encodeURIComponent(city)}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(showError);
}

function searchWeather() {
  const city = cityInput.value.trim();
  if (!city) return alert("Enter city name");

  fetch(`${API}/api/weather/search?city=${encodeURIComponent(city)}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(showError);
}

function fetchWeatherByLatLon() {
  const lat = latInput.value.trim();
  const lon = lonInput.value.trim();

  if (!lat || !lon) return alert("Enter latitude & longitude");

  fetch(`${API}/api/weather/geo?lat=${lat}&lon=${lon}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(showError);
}

function show(data) {
  result.classList.remove("hidden");
  result.innerHTML = `
    <h4>📍 ${data.city}</h4>
    <div class="weather-grid">
      <div>🌡 ${data.temperature} °C</div>
      <div>💧 ${data.humidity}%</div>
      <div>🌬 ${data.wind_speed} m/s</div>
      <div>👁 ${data.visibility} m</div>
      <div>☁ ${data.weather_main}</div>
      <div>${data.weather_description}</div>
    </div>
  `;
}

function showError(err) {
  result.classList.remove("hidden");
  result.innerHTML = `<b>Error:</b> ${err}`;
}
