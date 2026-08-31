/**
 * Exports an array of row objects to a downloaded CSV file.
 *
 * @param {string} filename base name (a timestamp + .csv are appended)
 * @param {Array<{key:string,header:string,value?:(row)=>any}>} columns column defs
 * @param {Array<object>} rows the data rows
 */
export function exportCsv(filename, columns, rows) {
  const escape = (val) => {
    if (val == null) return '';
    const s = String(val);
    // Quote if it contains comma, quote, or newline; double embedded quotes.
    if (/[",\n]/.test(s)) {
      return `"${s.replace(/"/g, '""')}"`;
    }
    return s;
  };

  const header = columns.map((c) => escape(c.header)).join(',');
  const lines = rows.map((row) =>
    columns.map((c) => escape(c.value ? c.value(row) : row[c.key])).join(',')
  );
  const csv = [header, ...lines].join('\r\n');

  const stamp = new Date().toISOString().slice(0, 19).replace(/[:T]/g, '-');
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `${filename}-${stamp}.csv`;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}
