import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';
import { Line, Bar } from 'react-chartjs-2';
import './ChartCard.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Tooltip,
  Legend,
  Filler
);

const baseOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: { mode: 'index', intersect: false },
  },
  scales: {
    x: { grid: { display: false }, ticks: { color: '#98a2b3' } },
    y: { grid: { color: '#eef1f5' }, ticks: { color: '#98a2b3' }, beginAtZero: true },
  },
};

export default function ChartCard({ title, type = 'line', data, height = 220 }) {
  const Comp = type === 'bar' ? Bar : Line;
  return (
    <div className="chart-card card">
      <div className="chart-title">{title}</div>
      <div className="chart-body" style={{ height }}>
        <Comp data={data} options={baseOptions} />
      </div>
    </div>
  );
}
