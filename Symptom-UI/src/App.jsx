// src/App.jsx
import {
  createBrowserRouter,
  createRoutesFromElements,
  Navigate,
  Route,
  RouterProvider,
} from "react-router-dom";
import { useValue } from "./loginContext";

import History from "./History";
import Layout from "./Layout";
import LoginPage from "./pages/Login";
import SignupPage from "./pages/Signup";
import SymptomForm from "./SymptomForm";

function ProtectedRoute({ element }) {
  const { user } = useValue();
  return user ? element : <Navigate to="/login" replace />;
}

export default function App() {
  const routes = createRoutesFromElements(
    <>
      {/* top-level layout (Navbar shown here) */}
      <Route path="/" element={<Layout />}>
        {/* public */}
        <Route index element={<Navigate to="/symptoms" replace />} />
        <Route path="login" element={<LoginPage />} />
        <Route path="signup" element={<SignupPage />} />

        {/* protected routes (will render inside Layout -> Outlet) */}
        <Route path="symptoms" element={<ProtectedRoute element={<SymptomForm />} />} />
        <Route path="history" element={<ProtectedRoute element={<History />} />} />

        {/* fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </>
  );

  const router = createBrowserRouter(routes);
  return <RouterProvider router={router} />;
}
