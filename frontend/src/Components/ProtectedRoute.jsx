import React from "react";
import { Outlet, Navigate } from "react-router-dom";

const ProtectedRoute = ({ role }) => {
  try {
    const raw = localStorage.getItem("user");
    const user = raw ? JSON.parse(raw) : null;

    if (!user) return <Navigate to="/" />;

    const roles = Array.isArray(role) ? role : [role];
    if (roles.length > 0 && !roles.includes(user.role)) return <Navigate to="/" />;

    return <Outlet />;
  } catch (e) {
    console.error("ProtectedRoute error:", e);
    return <Navigate to="/" />;
  }
};

export default ProtectedRoute;
