import { useEffect, useState } from 'react';
import { analyticsApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, SummaryCard, Spinner } from '../components/ui';
import ChartCard from '../components/ChartCard';
import { useToast } from '../components/Toast';

function isoDaysAgo(n) {
  const d = new Date();
  d.setDate(d.getDate() - n);
  return d.toISOString().slice(0, 10);
}

export default function Analytics() {
  const toast = useToast();
  const [kpi, setKpi] = useState(null);
  const [series, setSeries] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      const [k, ts] = await Promise.allSettled([
        analyticsApi.latestKpis(),
        analyticsApi.timeseries(isoDaysAgo(30), isoDaysAgo(0)),
      ]);
      if (k.status === 'fulfilled') setKpi(k.value || null);
      if (ts.status === 'fulfilled') setSeries(ts.value || []);
      if (k.status === 'rejected' && ts.status === 'rejected') {
        toast.error(errorMessage(k.reason, 'Failed to load analytics'));
      }
      setLoading(false);
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading) return <Spinner label="Loading analytics…" />;

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
  const ordersChart = {
    labels: series.map((p) => p.date),
    datasets: [
      {
        label: 'Orders',
        data: series.map((p) => p.orders),
        backgroundColor: '#2f5bea',
      },
    ],
  };

  return (
    <>
      <PageHeader title="Analytics" subtitle="Business KPIs and 30-day trends." />
      <div className="grid-cards">
        <SummaryCard title="Revenue (24h)" value={fmt(kpi?.revenueLast24h)} accent="green" />
        <SummaryCard title="Orders (24h)" value={kpi?.ordersLast24h ?? '—'} accent="blue" />
        <SummaryCard title="AOV (24h)" value={fmt(kpi?.aovLast24h)} accent="amber" />
        <SummaryCard title="Refund rate (7d)" value={pct(kpi?.refundRateLast7d)} accent="red" />
      </div>
      <div className="grid-charts">
        <ChartCard title="Revenue (30d)" data={revenueChart} />
        <ChartCard title="Orders (30d)" type="bar" data={ordersChart} />
      </div>
    </>
  );
}

const fmt = (v) =>
  v == null ? '—' : new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(v);
const pct = (v) => (v == null ? '—' : `${(v * 100).toFixed(1)}%`);
