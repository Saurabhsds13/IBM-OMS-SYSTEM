import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { NAV } from '../auth/permissions';
import { useTheme } from '../theme/ThemeContext';
import './Layout.css';

export default function Layout() {
  const { user, roles, hasRole, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const visibleNav = NAV.filter((item) => hasRole(item.roles));

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">OMS</span>
          <span className="brand-text">Admin</span>
        </div>
        <nav className="nav">
          {visibleNav.map((item) => (
            <NavLink
              key={item.key}
              to={item.path}
              end={item.path === '/'}
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-foot muted">Order Management System</div>
      </aside>

      <div className="main">
        <header className="topbar">
          <div className="topbar-spacer" />
          <div className="topbar-user">
            <button className="btn btn-sm" onClick={toggle} title="Toggle theme" aria-label="Toggle theme">
              {theme === 'dark' ? 'Light' : 'Dark'} mode
            </button>
            <div className="user-meta">
              <div className="user-name">{user?.username}</div>
              <div className="user-roles">{roles.join(', ') || 'No role'}</div>
            </div>
            <button className="btn btn-sm" onClick={logout}>
              Log out
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
