import { useState } from 'react';
import { usersApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader } from '../components/ui';
import { useToast } from '../components/Toast';
import { ROLES } from '../auth/permissions';

const ALL_ROLES = [ROLES.ADMIN, ROLES.OPS_MANAGER, ROLES.VIEWER];

export default function Users() {
  const toast = useToast();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [roles, setRoles] = useState([ROLES.VIEWER]);
  const [busy, setBusy] = useState(false);
  const [created, setCreated] = useState(null);

  const toggleRole = (role) =>
    setRoles((r) => (r.includes(role) ? r.filter((x) => x !== role) : [...r, role]));

  const submit = async () => {
    if (!username.trim()) return toast.error('Username is required');
    if (password.length < 8) return toast.error('Password must be at least 8 characters');
    if (roles.length === 0) return toast.error('Select at least one role');
    setBusy(true);
    try {
      const user = await usersApi.create({ username: username.trim(), password, roles });
      setCreated(user);
      toast.success(`User "${user.username}" created`);
      setUsername('');
      setPassword('');
      setRoles([ROLES.VIEWER]);
    } catch (err) {
      toast.error(errorMessage(err, 'Create user failed'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="Users" subtitle="Provision admin console users (ADMIN only)." />
      <div className="card" style={{ padding: 20, maxWidth: 480 }}>
        <div className="field">
          <label>Username</label>
          <input className="input" value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <div className="field">
          <label>Password</label>
          <input
            className="input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 8 characters"
          />
        </div>
        <div className="field">
          <label>Roles</label>
          <div className="row gap-8 wrap">
            {ALL_ROLES.map((role) => (
              <button
                key={role}
                type="button"
                className={`btn btn-sm ${roles.includes(role) ? 'btn-primary' : ''}`}
                onClick={() => toggleRole(role)}
              >
                {role}
              </button>
            ))}
          </div>
        </div>
        <button className="btn btn-primary" onClick={submit} disabled={busy}>
          {busy ? 'Creating…' : 'Create user'}
        </button>

        {created && (
          <div className="card" style={{ padding: 14, marginTop: 18, background: '#fafbfc' }}>
            <div style={{ fontWeight: 600 }}>{created.username}</div>
            <div className="muted" style={{ fontSize: 12 }}>
              Roles: {(created.roles || []).join(', ')} · {created.enabled ? 'Enabled' : 'Disabled'}
            </div>
          </div>
        )}
      </div>
    </>
  );
}
