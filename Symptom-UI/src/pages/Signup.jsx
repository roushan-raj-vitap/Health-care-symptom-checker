// src/pages/SignupPage.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom"; // <-- react-router-dom
import "../Styles/App.css";
import { useValue } from "../loginContext";

function SignupPage() {
  const { signup } = useValue();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    role: "USER",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [showPwd, setShowPwd] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  }

  function validate() {
    const errs = {};
    if (!formData.email) errs.email = "Email is required";
    else if (!/^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(formData.email))
      errs.email = "Invalid email";
    if (!formData.username) errs.username = "Username is required";
    if (!formData.password) errs.password = "Password is required";
    else if (formData.password.length < 6)
      errs.password = "Password must be at least 6 characters";
    return errs;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length !== 0) return;

    setLoading(true);
    try {
      await signup(formData); // now throws on error
      navigate("/"); // only navigate on success
    } catch (err) {
      alert(err.message || "Signup failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-wrap">
      <section className="auth-card">
        <div className="auth-header">
          <div className="auth-logo" aria-hidden="true">
            OC
          </div>
          <div>
            <h3>Create Account</h3>
            <p>Sign up to start your journey</p>
          </div>
        </div>

        <form className="auth-form" noValidate onSubmit={handleSubmit}>
          <label className="auth-label" htmlFor="email">Email</label>
          <input className="auth-input" type="email" id="email" name="email"
            value={formData.email} onChange={handleChange} placeholder="you@example.com" />
          {errors.email && <small style={{ color: "red" }}>{errors.email}</small>}

          <label className="auth-label" htmlFor="username">Username</label>
          <input className="auth-input" type="text" id="username" name="username"
            value={formData.username} onChange={handleChange} placeholder="Enter your username" />
          {errors.username && <small style={{ color: "red" }}>{errors.username}</small>}

          <div className="auth-row">
            <label className="auth-label" htmlFor="password">Password</label>
            <small className="hint">(min 6 chars)</small>
          </div>

          <div className="password-field">
            <input className="auth-input"
              type={showPwd ? "text" : "password"}
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Enter your password"
            />
            <button type="button" className="toggle-pass" onClick={() => setShowPwd(s => !s)}>
              {showPwd ? "Hide" : "Show"}
            </button>
          </div>
          {errors.password && <small style={{ color: "red" }}>{errors.password}</small>}

          <div className="auth-actions">
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? "Signing up…" : "Sign up"}
            </button>
          </div>
        </form>

        <div className="auth-footer">
          <p>Already have an account? <a className="link" href="/login">Sign in</a></p>
          <p className="terms">By signing up you agree to our terms and privacy policy.</p>
        </div>
      </section>
    </main>
  );
}

export default SignupPage;
