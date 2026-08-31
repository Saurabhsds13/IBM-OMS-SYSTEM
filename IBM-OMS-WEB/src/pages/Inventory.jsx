import { useEffect, useState } from 'react';
import { inventoryApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, DataTable, SkeletonTable } from '../components/ui';
import RoleGate from '../auth/RoleGate';
import { useToast } from '../components/Toast';
import { exportCsv } from '../services/csv';

const WRITE_ROLES = ['OPS_MANAGER', 'ADMIN'];

export default function Inventory() {
  const toast = useToast();
  const [stock, setStock] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);

  const load = async () => {
    setLoading(true);
    try {
      setStock((await inventoryApi.list()) || []);
    } catch (err) {
      toast.error(errorMessage(err, 'Failed to load inventory'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const adjust = async (kind, productCode) => {
    const qtyStr = window.prompt(`${kind === 'reserve' ? 'Reserve' : 'Release'} quantity for ${productCode}:`, '1');
    if (qtyStr == null) return;
    const qty = Number(qtyStr);
    if (!Number.isInteger(qty) || qty < 1) return toast.error('Enter a positive whole number');
    setBusy(true);
    try {
      await (kind === 'reserve' ? inventoryApi.reserve : inventoryApi.release)(productCode, qty);
      toast.success(`Stock ${kind}d`);
      await load();
    } catch (err) {
      toast.error(errorMessage(err, 'Adjustment failed'));
    } finally {
      setBusy(false);
    }
  };

  const columns = [
    { key: 'productCode', header: 'Product' },
    { key: 'availableQty', header: 'Available' },
    { key: 'reservedQty', header: 'Reserved' },
    { key: 'vendorName', header: 'Vendor' },
    { key: 'location', header: 'Location' },
    {
      key: 'actions',
      header: '',
      render: (r) => (
        <RoleGate roles={WRITE_ROLES}>
          <div className="row gap-8">
            <button className="btn btn-sm" disabled={busy} onClick={() => adjust('reserve', r.productCode)}>
              Reserve
            </button>
            <button className="btn btn-sm" disabled={busy} onClick={() => adjust('release', r.productCode)}>
              Release
            </button>
          </div>
        </RoleGate>
      ),
    },
  ];

  const exportRows = () =>
    exportCsv(
      'inventory',
      [
        { key: 'productCode', header: 'Product Code' },
        { key: 'availableQty', header: 'Available' },
        { key: 'reservedQty', header: 'Reserved' },
        { key: 'vendorName', header: 'Vendor' },
        { key: 'location', header: 'Location' },
      ],
      stock
    );

  return (
    <>
      <PageHeader
        title="Inventory"
        subtitle="Stock levels and reservations."
        actions={
          <button className="btn" onClick={exportRows} disabled={stock.length === 0}>
            Export CSV
          </button>
        }
      />
      {loading ? (
        <SkeletonTable columns={6} rows={6} />
      ) : (
        <DataTable columns={columns} rows={stock} rowKey={(r) => r.id ?? r.productCode} empty="No inventory records." />
      )}
    </>
  );
}
