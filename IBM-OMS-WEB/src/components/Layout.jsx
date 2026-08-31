import { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import {
  LayoutDashboard,
  ShoppingCart,
  Boxes,
  Truck,
  BarChart3,
  CreditCard,
  Bell,
  Users,
  Menu,
  X,
  Sun,
  Moon,
  LogOut,
} from 'lucide-react';
import { useAuth } from '../auth/AuthContext';
import { NAV } from '../auth/permissions';
import { useTheme } from '../theme/ThemeContext';
import NotificationBell from './NotificationBell';
import './Layout.css';

const ICONS = {
  LayoutDashboard,
  ShoppingCart,
  Boxes,
  Truck,
  BarChart3,
  CreditCard,
  Bell,
  Users,
};

export default function Layout() {
  const { user, roles, hasRole, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const visibleNav = NAV.filter((item) => hasRole(item.roles));

  // Close the mobile drawer whenever the route changes.
  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  return (
    <div className="layout">
      {/* Backdrop shown behind the drawer on mobile */}
      {mobileOpen && <div className="sidebar-backdrop" onClick={() => setMobileOpen(false)} />}

      <aside className={`sidebar ${mobileOpen ? 'open' : ''}`}>
        <div className="brand">
          <span className="brand-mark">OMS</span>
          <span className="brand-text">Admin</span>
          <button className="sidebar-close" onClick={() => setMobileOpen(false)} aria-label="Close menu">
            <X size={18} />
          </button>
        </div>
        <nav className="nav">
          {visibleNav.map((item) => {
            const Icon = ICONS[item.icon];
            return (
              <NavLink
                key={item.key}
                to={item.path}
                end={item.path === '/'}
                className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              >
                {Icon && <Icon size={17} className="nav-icon" />}
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
        <div className="sidebar-foot muted">Order Management System</div>
      </aside>

      <div className="main">
        <header className="topbar">
          <button className="hamburger" onClick={() => setMobileOpen(true)} aria-label="Open menu">
            <Menu size={20} />
          </button>
          <div className="topbar-spacer" />
          <div className="topbar-user">
            <NotificationBell />
            <button className="icon-btn" onClick={toggle} title="Toggle theme" aria-label="Toggle theme">
              {theme === 'dark' ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <div className="user-meta">
              <div className="user-name">{user?.username}</div>
              <div className="user-roles">{roles.join(', ') || 'No role'}</div>
            </div>
            <button className="icon-btn" onClick={logout} title="Log out" aria-label="Log out">
              <LogOut size={18} />
            </button>
          </div>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
