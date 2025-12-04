import React, { useEffect, useState } from "react";
import apiRequest from "../../api";
import "../../styles/App.css";

function AdminUsers() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const data = await apiRequest("/admin/users", "GET");
        setUsers(data);
      } catch (err) {
        console.error("Error fetching users:", err);
      }
    };
    fetchUsers();
  }, []);

  return (
    <div className="page-content">
      <h2>All Users</h2>
      <table className="list-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Address</th>
            <th>Role</th>
            <th>Avg Rating</th> {/* NEW */}
            <th style={{ width: 180 }}>Actions</th>
          </tr>
        </thead>

        <tbody>
          {filtered.length === 0 ? (
            <tr>
              <td colSpan="7" style={{ textAlign: "center", padding: 16 }}>
                No users found
              </td>
            </tr>
          ) : (
            filtered.map((u) => (
              <tr key={u.id}>
                <td>{u.id}</td>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.address}</td>
                <td>{u.role}</td>
                <td>{u.role === "OWNER" ? u.avgRating.toFixed(2) : "-"}</td> {/* NEW */}
                <td>
                  <button className="btn-view" onClick={() => navigate(`/admin/users/${u.id}`)}>
                    View
                  </button>
                  {loggedUser?.role === "ADMIN" && (
                    <button
                      className="btn-delete"
                      style={{ marginLeft: 8 }}
                      onClick={() => handleDelete(u.id)}
                    >
                      Delete
                    </button>
                  )}
                </td>
              </tr>
            ))
          )}
        </tbody>
        <tbody>
          {users.map(u => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>{u.address}</td>
              <td>{u.role}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
export default AdminUsers;