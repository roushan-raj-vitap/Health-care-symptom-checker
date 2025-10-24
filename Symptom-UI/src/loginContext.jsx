// src/loginContext.jsx
import { createContext, useContext, useState } from "react";

const loginContext = createContext();

function useValue() {
  const ctx = useContext(loginContext);
  if (ctx === undefined) {
    throw new Error("useValue must be used inside CustomLoginContext");
  }
  return ctx;
}

// With Vite proxy leave empty
const API_BASE = ""; // proxy will forward to backend

// Helper: check if JWT is expired (reads exp claim, which is seconds since epoch)
function isTokenExpired(token) {
  if (!token) return true;
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return true;
    const payload = JSON.parse(atob(parts[1].replace(/-/g, "+").replace(/_/g, "/")));
    if (!payload.exp) return true;
    const now = Math.floor(Date.now() / 1000);
    return payload.exp <= now;
  } catch (e) {
    console.warn("Failed to parse token for expiry check:", e);
    return true;
  }
}

/**
 * apiFetch wrapper:
 * - avoids adding Authorization header to public auth endpoints (/user/login, /user/register)
 * - clears stale tokens from localStorage if expired
 * - always reads token fresh from localStorage
 */
async function apiFetch(path, opts = {}) {
  // Normalize path (ensure leading '/')
  const p = path.startsWith("/") ? path : "/" + path;

  // Do not attach Authorization for auth endpoints
  const skipAuthFor = ["/user/login", "/user/register"];

  // Read token fresh each call
  let token = localStorage.getItem("token");

  // If token exists but expired, remove it and avoid sending it
  if (token && isTokenExpired(token)) {
    console.info("Client: token expired — clearing stored token.");
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    token = null;
  }

  const headers = {
    "Content-Type": "application/json",
    ...(opts.headers || {}),
  };

  // Attach Authorization header unless request is to a public auth endpoint
  const isAuthEndpoint = skipAuthFor.includes(p);
  if (!isAuthEndpoint && token) {
    headers["Authorization"] = `Bearer ${token}`;
  } else if (isAuthEndpoint) {
    // debug note
    // console.log(`Not attaching Authorization header for auth endpoint: ${p}`);
  }

  return fetch(API_BASE + p, { ...opts, headers });
}

function CustomLoginContext({ children }) {
  const [user, setUser] = useState(() => {
    const storedUser = localStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : null;
  });

  async function login({ email, password }) {
    try {
      // For login we call apiFetch("/user/login") which will NOT attach Authorization header
      const resp = await apiFetch("/user/login", {
        method: "POST",
        body: JSON.stringify({ email, password }),
      });

      // parse body (attempt)
      const data = await resp.json().catch(() => ({}));

      if (!resp.ok) {
        const msg = data?.message || `Login failed (${resp.status})`;
        throw new Error(msg);
      }

      console.log("login response", data);

      if (!data.token) throw new Error("Login response missing token");

      // store fresh token & user
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data.user ?? data));
      setUser(data.user ?? data);

      return true;
    } catch (error) {
      console.error("Error logging in:", error);
      throw error;
    }
  }

  async function signup({ username, password, email, role }) {
    try {
      // signup also uses apiFetch("/user/register") — no Authorization header attached
      const resp = await apiFetch("/user/register", {
        method: "POST",
        body: JSON.stringify({ username, password, email, role }),
      });

      const data = await resp.json().catch(() => ({}));

      if (!resp.ok) {
        const msg = data?.message || `Signup failed (${resp.status})`;
        throw new Error(msg);
      }

      console.log("signup response", data);

      if (!data.token) throw new Error("No token in signup response");

      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify(data.user ?? data));
      setUser(data.user ?? data);

      return true;
    } catch (error) {
      console.error("Signup error:", error);
      throw error;
    }
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  }

  return (
    <loginContext.Provider value={{ user, login, logout, signup }}>
      {children}
    </loginContext.Provider>
  );
}

export { CustomLoginContext, loginContext, useValue };
