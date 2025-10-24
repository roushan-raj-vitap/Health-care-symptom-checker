// src/components/Navbar.jsx
import { Link, useNavigate } from "react-router-dom";
import { useValue } from "../loginContext";
import "../Styles/Nav.css";

export default function Navbar() {
  const { user, logout } = useValue();
  const navigate = useNavigate();

  function handleLogOut(e) {
    e.preventDefault();
    logout();
    navigate("/login");
  }

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <Link to="/" className="navbar-logo">Health care</Link>
      </div>

      <ul className="navbar-links">
        <li><Link to="/">Home</Link></li>
        <li><Link to="/symptoms">Symptoms</Link></li>
        <li><Link to="/history">History</Link></li>
        <li><Link to="/about">About</Link></li>
      </ul>

      <div className="navbar-actions">
        {user ? (
          <>
            <span className="navbar-user">Hi, {user.username ?? user.email}</span>
            <button className="btn-primary navbar-btn" onClick={handleLogOut}>
              Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="btn-ghost navbar-btn">Login</Link>
            <Link to="/signup" className="btn-primary navbar-btn">Sign up</Link>
          </>
        )}
      </div>
    </nav>
  );
}
