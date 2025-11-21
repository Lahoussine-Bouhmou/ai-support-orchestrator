// src/components/TicketList.jsx
import TicketCard from './TicketCard';

export default function TicketList({ tickets, loading }) {
  return (
    <div className="ticket-list">
      <div className="ticket-list-header">
        <h2>Tickets générés</h2>
        {loading && <span className="loader">Chargement...</span>}
      </div>

      {tickets.length === 0 && !loading && (
        <p>Aucun ticket pour le moment. Envoie un email pour commencer.</p>
      )}

      {tickets.map((ticket) => (
        <TicketCard key={ticket.id} ticket={ticket} />
      ))}
    </div>
  );
}
