import { Routes, Route, BrowserRouter } from "react-router-dom";

// Auth
import Login from "./Pages/Auth/Login";
import Signup from "./Pages/Auth/Signup";

// User
import StoreList from "./Pages/User/StoreList";
import UserDashboard from "./Pages/User/UserDashboard";
import UpdatePassword from "./Pages/User/UpdatePassword";
import MyRatings from "./Pages/User/MyRatings";

// Admin
import AdminDashboard from "./Pages/Admin/AdminDashboard";
import ManageUsers from "./Pages/Admin/ManageUsers";
import ManageStores from "./Pages/Admin/ManageStores";
import AddUser from "./Pages/Admin/AddUser";
import AddStore from "./Pages/Admin/AddStore";
import UserDetails from "./Pages/Admin/UserDetails";
import StoreDetails from "./Pages/Admin/StoreDetails";

// Owner
import OwnerDashboard from "./Pages/Owner/OwnerDashboard";
import OwnerUsers from "./Pages/Owner/OwnerUsers";
import OwnerRatings from "./Pages/Owner/OwnerRatings";
import OwnerUpdatePassword from "./Pages/Owner/OwnerUpdatePassword";
import OwnerStores from "./Pages/Owner/OwnerStores";

// Protected Route
import ProtectedRoute from "./Components/ProtectedRoute";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Public Routes */}
        <Route path="/" element={<Login />} />
        <Route path="/signup" element={<Signup />} />

        {/* USER Protected Routes */}
        <Route element={<ProtectedRoute role="USER" />}>
          <Route path="/user/dashboard" element={<UserDashboard />} />
          <Route path="/stores" element={<StoreList />} />
          <Route path="/store/:id" element={<StoreDetails />} />
          <Route path="/update-password" element={<UpdatePassword />} />
          <Route path="/my-ratings" element={<MyRatings />} />
        </Route>

        {/* ADMIN Protected Routes */}
        <Route element={<ProtectedRoute role="ADMIN" />}>
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
          {/* Users */}
          <Route path="/admin/users" element={<ManageUsers />} />
          <Route path="/admin/add-user" element={<AddUser />} />
          <Route path="/admin/users/:id" element={<UserDetails />} />
          {/* Stores */}
          <Route path="/admin/stores" element={<ManageStores />} />
          <Route path="/admin/add-store" element={<AddStore />} />
          <Route path="/admin/store-details/:id" element={<StoreDetails />} />

        </Route>
        {/* OWNER Protected Routes */}
        <Route element={<ProtectedRoute role="OWNER" />}>
          <Route path="/owner/dashboard" element={<OwnerDashboard />} />
          <Route path="/owner/stores" element={<OwnerStores />} />
          <Route path="/owner/users" element={<OwnerUsers />} />
          <Route path="/owner/ratings" element={<OwnerRatings />} />
          <Route path="/owner/update-password" element={<OwnerUpdatePassword />} />
        </Route>

      </Routes>
    </BrowserRouter>
  );
}

export default App;
