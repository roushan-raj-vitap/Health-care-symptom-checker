// src/Layout.jsx
import { Outlet } from "react-router-dom";
import Navbar from "./pages/Navbar";

export default function Layout() {
  return (
    <>
      <Navbar />
      <main style={{ padding: 0 }}>
        <Outlet />
      </main>
    </>
  );
}
