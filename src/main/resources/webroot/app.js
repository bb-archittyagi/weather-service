const API = "http://localhost:8080";

function fetchWeatherByCity() {
  const city = cityInput.value;
  if (!city) return alert("Enter city");

  fetch(`${API}/api/weather?city=${city}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(err => showError(err));
}

function searchWeather() {
  const city = cityInput.value;
  if (!city) return alert("Enter city");

  fetch(`${API}/api/weather/search?city=${city}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(err => showError(err));
}

function fetchWeatherByLatLon() {
  const lat = latInput.value;
  const lon = lonInput.value;
  if (!lat || !lon) return alert("Enter lat & lon");

  fetch(`${API}/api/weather/geo?lat=${lat}&lon=${lon}`)
    .then(r => r.ok ? r.json() : r.text().then(t => Promise.reject(t)))
    .then(show)
    .catch(err => showError(err));
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
