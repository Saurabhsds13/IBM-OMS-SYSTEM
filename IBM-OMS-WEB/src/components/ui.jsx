import './ui.css';

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="page-header">
      <div>
        <h1>{title}</h1>
        {subtitle && <p className="muted">{subtitle}</p>}
      </div>
      {actions && <div className="row gap-8 center">{actions}</div>}
    </div>
  );
}

export function SummaryCard({ title, value, hint, accent = 'blue' }) {
  return (
    <div className="summary-card card">
      <span className={`summary-dot dot-${accent}`} />
      <div className="summary-title">{title}</div>
      <div className="summary-value">{value ?? '—'}</div>
      {hint && <div className="summary-hint">{hint}</div>}
    </div>
  );
}

const STATUS_MAP = {
  PENDING: 'amber',
  APPROVED: 'blue',
  PARTIALLY_SHIPPED: 'blue',
  SHIPPED: 'green',
  DELIVERED: 'green',
  CANCELLED: 'red',
  FAILED: 'red',
  DEAD_LETTER: 'red',
  PUBLISHED: 'green',
  SUCCESS: 'green',
  REFUNDED: 'gray',
};

export function StatusBadge({ status }) {
  const tone = STATUS_MAP[status] || 'gray';
  return <span className={`badge badge-${tone}`}>{status ?? '—'}</span>;
}

export function Spinner({ label = 'Loading…' }) {
  return (
    <div className="spinner-wrap">
      <div className="spinner" />
      <span className="muted">{label}</span>
    </div>
  );
}

export function EmptyState({ message = 'No data yet.' }) {
  return <div className="empty-state muted">{message}</div>;
}

/**
 * Minimal declarative data table.
 * columns: [{ key, header, render?(row), width? }]
 */
export function DataTable({ columns, rows, rowKey, empty }) {
  if (!rows || rows.length === 0) {
    return <EmptyState message={empty || 'No records found.'} />;
  }
  return (
    <div className="table-wrap card">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key} style={c.width ? { width: c.width } : undefined}>
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={rowKey ? rowKey(row) : i}>
              {columns.map((c) => (
                <td key={c.key}>{c.render ? c.render(row) : row[c.key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
