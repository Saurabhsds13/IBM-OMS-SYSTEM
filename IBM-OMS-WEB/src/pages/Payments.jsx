import { useState } from 'react';
import { paymentsApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, StatusBadge } from '../components/ui';
import { useToast } from '../components/Toast';
import { useConfirm } from '../components/ConfirmDialog';

export default function Payments() {
  const toast = useToast();
  const confirm = useConfirm();
  const [orderNumber, setOrderNumber] = useState('');
  const [amount, setAmount] = useState('');
  const [result, setResult] = useState(null);
  const [busy, setBusy] = useState(false);

  const initiate = async () => {
    const amt = Number(amount);
    if (!orderNumber.trim()) return toast.error('Order number is required');
    if (!(amt > 0)) return toast.error('Amount must be greater than 0');
    setBusy(true);
    try {
      const payment = await paymentsApi.initiate(orderNumber.trim(), amt);
      setResult(payment);
      toast.success('Payment initiated');
    } catch (err) {
      toast.error(errorMessage(err, 'Initiate failed'));
    } finally {
      setBusy(false);
    }
  };

  const refund = async () => {
    if (!result?.id) return;
    const ok = await confirm({
      title: 'Refund payment?',
      message: `This will refund payment #${result.id} for order ${result.orderNumber}. This cannot be undone.`,
      confirmLabel: 'Refund',
      danger: true,
    });
    if (!ok) return;
    setBusy(true);
    try {
      const payment = await paymentsApi.refund(result.id);
      setResult(payment);
      toast.success('Refund processed');
    } catch (err) {
      toast.error(errorMessage(err, 'Refund failed'));
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <PageHeader title="Payments" subtitle="ADMIN-only payment operations." />
      <div className="card" style={{ padding: 20, maxWidth: 520 }}>
        <div className="field">
          <label>Order number</label>
          <input className="input" value={orderNumber} onChange={(e) => setOrderNumber(e.target.value)} />
        </div>
        <div className="field">
          <label>Amount</label>
          <input
            className="input"
            type="number"
            min="0"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
        </div>
        <button className="btn btn-primary" onClick={initiate} disabled={busy}>
          Initiate payment
        </button>

        {result && (
          <div className="card" style={{ padding: 16, marginTop: 20, background: '#fafbfc' }}>
            <div className="row between center">
              <div>
                <div className="muted" style={{ fontSize: 12 }}>
                  Payment #{result.id} · {result.orderNumber}
                </div>
                <div style={{ fontSize: 18, fontWeight: 600, marginTop: 4 }}>
                  {new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD' }).format(result.amount || 0)}
                </div>
              </div>
              <StatusBadge status={result.status} />
            </div>
            <button className="btn btn-sm" style={{ marginTop: 14 }} onClick={refund} disabled={busy}>
              Refund
            </button>
          </div>
        )}
      </div>
    </>
  );
}
