// src/components/TicketCard.jsx

export default function TicketCard({ ticket }) {
  return (
    <div className="ticket-card">
      <div className="ticket-header">
        <span className={`badge badge-${ticket.category?.toLowerCase()}`}>
          {ticket.category}
        </span>
        <h3>{ticket.subject}</h3>
        <p className="ticket-from">De : {ticket.fromEmail}</p>
      </div>

      <div className="ticket-body">
        <details>
          <summary>Voir l’email original</summary>
          <p className="ticket-original-body">{ticket.body}</p>
        </details>

        <div className="ticket-summary">
          <h4>Résumé (IA)</h4>
          <p>{ticket.summary}</p>
        </div>

        {ticket.invoiceNumber && (
          <p className="ticket-meta">
            <strong>Facture :</strong> {ticket.invoiceNumber}
          </p>
        )}
      </div>

      <div className="ticket-reply">
        <h4>Réponse proposée (IA)</h4>
        <pre>{ticket.suggestedReply}</pre>
      </div>
    </div>
  );
}
