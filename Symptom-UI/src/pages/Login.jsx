// src/pages/LoginPage.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom"; // correct import
import "../Styles/App.css";
import { useValue } from "../loginContext";

function LoginPage() {
  const { login } = useValue();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    if (!email || !password) {
      alert("Email and password are required");
      return;
    }
    if (password.length < 6) {
      alert("Password must be at least 6 characters");
      return;
    }

    setLoading(true);
    try {
      await login({ email, password }); // throws on error
      navigate("/");
    } catch (err) {
      alert(err.message || "Invalid credentials");
    } finally {
      setLoading(false);
    }
  }

  function fillDemo() {
    setEmail("demo@gmail.com");
    setPassword("Demo@8754");
  }

  return (
    <main className="auth-wrap">
      <section className="auth-card">
        <div className="auth-header">
          <div className="auth-logo" aria-hidden="true">OC</div>
          <div>
            <h1>Welcome back</h1>
            <p>Sign in to continue to your account</p>
          </div>
        </div>

        <form className="auth-form" noValidate onSubmit={handleSubmit}>
          <label className="auth-label" htmlFor="email">Email</label>
          <input className="auth-input" id="email" name="email" type="email"
            value={email} onChange={e => setEmail(e.target.value)} placeholder="you@example.com" required />

          <div className="auth-row">
            <label className="auth-label" htmlFor="password">Password</label>
            <small className="hint">(min 6 chars)</small>
          </div>
          <div className="password-field">
            <input className="auth-input" id="password" name="password" 
              type={showPwd ? "text" : "password"} value={password}
              onChange={e => setPassword(e.target.value)} placeholder="Enter your password" required minLength={6} />
            <button type="button" className="toggle-pass" onClick={() => setShowPwd(s => !s)}>
              {showPwd ? "Hide" : "Show"}
            </button>
          </div>

          <div className="auth-meta">
            <label className="remember"><input type="checkbox" name="remember" value="1" /> Remember me</label>
            <a className="link-small" href="#">Forgot?</a>
          </div>

          <div className="auth-actions">
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? "Signing in…" : "Sign in"}
            </button>
            <button type="button" className="btn-ghost" onClick={fillDemo}>Demo</button>
          </div>
        </form>

        <div className="auth-footer">
          <p>Don't have an account? <a className="link" href="/signup">Create account</a></p>
          <p className="terms">By signing in you agree to our terms and privacy policy.</p>
        </div>
      </section>
    </main>
  );
}

export default LoginPage;
