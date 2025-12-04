import React, { useState } from "react";
import apiRequest from "../../api";
import "../../Styles/App.css";
import { useNavigate } from "react-router-dom";

export default function UpdatePassword() {
  const [oldPass, setOldPass] = useState("");
  const [newPass, setNewPass] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const updatePassword = async () => {
    if (!oldPass || !newPass) return alert("All fields required");

    setLoading(true);
    try {
      await apiRequest("/user/change-password", "POST", {
        oldPassword: oldPass,
        newPassword: newPass,
      });
      alert("Password updated successfully!");
      setOldPass("");
      setNewPass("");
    } catch (err) {
      alert(err.message || "Failed to update password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-content">
      <button className="btn-view" onClick={() => navigate("/user/dashboard")}>← Back</button>
      <div className="form-box update-pass-box">
        <h3 className="form-title">Update Password</h3>
        <input
          type="password"
          className="input-box"
          placeholder="Old Password"
          value={oldPass}
          onChange={(e) => setOldPass(e.target.value)}
        />
        <input
          type="password"
          className="input-box"
          placeholder="New Password"
          value={newPass}
          onChange={(e) => setNewPass(e.target.value)}
        />
        <div className="form-actions">
          <button className="btn-view" onClick={updatePassword} disabled={loading}>
            {loading ? "Updating..." : "Update"}
          </button>
          <button className="btn-view" onClick={() => { setOldPass(""); setNewPass(""); }}>
            Reset
          </button>
        </div>
      </div>
    </div>
  );
}
