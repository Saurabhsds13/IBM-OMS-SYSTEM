import { useEffect, useState } from 'react';
import { dashboardApi, analyticsApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, SummaryCard, Spinner, StatusBadge } from '../components/ui';
import ChartCard from '../components/ChartCard';
import { useAuth } from '../auth/AuthContext';
import { useToast } from '../components/Toast';

function isoDaysAgo(n) {
  const d = new Date();
  d.setDate(d.getDate() - n);
  return d.toISOString().slice(0, 10);
}

export default function Dashboard() {
  const { user } = useAuth();
  const toast = useToast();
  const [summary, setSummary] = useState(null);
  const [series, setSeries] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let active = true;
    (async () => {
      const [s, ts] = await Promise.allSettled([
        dashboardApi.summary(),
        analyticsApi.timeseries(isoDaysAgo(30), isoDaysAgo(0)),
      ]);
      if (!active) return;
      if (s.status === 'fulfilled') setSummary(s.value);
      else toast.error(errorMessage(s.reason, 'Failed to load dashboard summary'));
      if (ts.status === 'fulfilled') setSeries(ts.value || []);
      setLoading(false);
    })();
    return () => {
      active = false;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) return <Spinner label="Loading dashboard…" />;

  const byStatus = summary?.ordersByStatus || {};
  const kpi = summary?.latestKpi;

  const revenueChart = {
    labels: series.map((p) => p.date),
    datasets: [
      {
        label: 'Revenue',
        data: series.map((p) => p.revenue),
        borderColor: '#16a34a',
        backgroundColor: '#16a34a22',
        fill: true,
        tension: 0.35,
        pointRadius: 0,
      },
    ],
  };
  const statusLabels = Object.keys(byStatus);
  const ordersByStatusChart = {
    labels: statusLabels,
    datasets: [
      {
        label: 'Orders',
        data: statusLabels.map((k) => byStatus[k]),
        backgroundColor: '#2f5bea',
      },
    ],
  };

  return (
    <>
      <PageHeader
        title={`Welcome, ${user?.username || 'admin'}`}
        subtitle="Operational overview across orders, inventory, and sales."
      />

      <div className="grid-cards">
        <SummaryCard title="Total Orders" value={summary?.totalOrders ?? 0} accent="blue" hint={`${byStatus.PENDING || 0} pending`} />
        <SummaryCard title="Inventory SKUs" value={summary?.inventorySkuCount ?? 0} accent="amber" hint={`${summary?.lowStockCount ?? 0} low stock`} />
        <SummaryCard title="Pending Events" value={summary?.pendingOutbox ?? 0} accent="red" hint="Outbox queue" />
        <SummaryCard
          title="Revenue (24h)"
          value={kpi ? formatCurrency(kpi.revenueLast24h) : '—'}
          accent="green"
          hint={kpi ? `${kpi.ordersLast24h} orders` : 'No KPI snapshot'}
        />
      </div>

      {statusLabels.length > 0 && (
        <div className="card" style={{ padding: 16, marginBottom: 20 }}>
          <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 12 }}>Orders by status</div>
          <div className="row gap-8 wrap">
            {statusLabels.map((s) => (
              <span key={s} className="row gap-8 center" style={{ marginRight: 16 }}>
                <StatusBadge status={s} />
                <strong>{byStatus[s]}</strong>
              </span>
            ))}
          </div>
        </div>
      )}

      <div className="grid-charts">
        <ChartCard title="Revenue (30d)" data={revenueChart} />
        <ChartCard title="Orders by status" type="bar" data={ordersByStatusChart} />
      </div>
    </>
  );
}

function formatCurrency(v) {
  if (v == null) return '—';
  return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(v);
}
