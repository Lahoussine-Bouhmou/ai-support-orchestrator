// src/components/TicketList.jsx
import TicketCard from './TicketCard';

export default function TicketList({ tickets, loading, filterStatus, onChangeStatus, onFilterChange }) {
  const filtered = filterStatus && filterStatus !== 'ALL'
    ? tickets.filter((t) => (t.status || 'NEW') === filterStatus)
    : tickets;

  return (
    <div className="ticket-list">
      <div className="ticket-list-header">
        <h2>Tickets générés</h2>

        <div className="ticket-filters">
          <label>
            Statut :{' '}
            <select value={filterStatus} onChange={(e) => onFilterChange(e.target.value)}>
              <option value="ALL">Tous</option>
              <option value="NEW">Nouveaux</option>
              <option value="VALIDATED">Validés</option>
              <option value="NEEDS_REVIEW">À corriger</option>
            </select>
          </label>

          {loading && <span className="loader">Chargement...</span>}
        </div>
      </div>

      {filtered.length === 0 && !loading && (
        <p>Aucun ticket pour le filtre sélectionné.</p>
      )}

      {filtered.map((ticket) => (
        <TicketCard key={ticket.id} ticket={ticket} onChangeStatus={onChangeStatus} />
      ))}
    </div>
  );
}
