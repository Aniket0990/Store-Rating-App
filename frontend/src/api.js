const API_BASE_URL = "http://10.47.60.194:8080/api";

export const apiRequest = async (endpoint, method = "GET", body = null) => {
  const token = localStorage.getItem("token");

  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;

  console.log("API Request:", {
    method,
    url: `${API_BASE_URL}${endpoint}`,
    headers,
    body,
  });

  const options = { method, headers };
  if (body) options.body = JSON.stringify(body);

  const res = await fetch(`${API_BASE_URL}${endpoint}`, options);

  console.log("API Response Status:", res.status, res.statusText);

  const contentType = res.headers.get("content-type") || "";
  const isJson = contentType.includes("application/json");

  if (!res.ok) {
    if (res.status === 403) throw new Error("You do not have permission (403).");
    if (res.status === 401) throw new Error("Unauthorized (401)");

    if (isJson) {
      const err = await res.json().catch(() => null);
      throw new Error(err?.message || JSON.stringify(err) || `Request failed: ${res.status}`);
    } else {
      const txt = await res.text().catch(() => null);
      throw new Error(txt || `Request failed: ${res.status}`);
    }
  }

  if (res.status === 204) return null;
  return isJson ? res.json() : res.text();
};

export default apiRequest;
