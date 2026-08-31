import { useEffect, useState } from 'react';
import { notificationsApi } from '../services/endpoints';
import { errorMessage } from '../services/api';
import { PageHeader, DataTable, StatusBadge, SkeletonTable } from '../components/ui';
import { useToast } from '../components/Toast';

export default function Notifications() {
  const toast = useToast();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      setEvents((await notificationsApi.pending()) || []);
    } catch (err) {
      toast.error(errorMessage(err, 'Failed to load outbox'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const retry = async (id) => {
    setBusyId(id);
    try {
      await notificationsApi.retry(id);
      toast.success('Event re-queued');
      await load();
    } catch (err) {
      toast.error(errorMessage(err, 'Retry failed'));
    } finally {
      setBusyId(null);
    }
  };

  const columns = [
    { key: 'id', header: 'ID' },
    { key: 'aggregateType', header: 'Aggregate' },
    { key: 'aggregateId', header: 'Aggregate ID' },
    { key: 'eventType', header: 'Event' },
    { key: 'status', header: 'Status', render: (e) => <StatusBadge status={e.status} /> },
    { key: 'attemptCount', header: 'Attempts' },
    {
      key: 'actions',
      header: '',
      render: (e) => (
        <button className="btn btn-sm" disabled={busyId === e.id} onClick={() => retry(e.id)}>
          Retry
        </button>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Notifications"
        subtitle="Pending outbox events awaiting dispatch."
        actions={
          <button className="btn" onClick={load}>
            Refresh
          </button>
        }
      />
      {loading ? (
        <SkeletonTable columns={7} rows={6} />
      ) : (
        <DataTable columns={columns} rows={events} rowKey={(e) => e.id} empty="No pending events." />
      )}
    </>
  );
}
