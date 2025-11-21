// src/components/TicketCard.jsx

export default function TicketCard({ ticket, onChangeStatus }) {
  const statusLabel = {
    NEW: 'Nouveau',
    VALIDATED: 'Validé',
    NEEDS_REVIEW: 'À corriger',
  }[ticket.status || 'NEW'];

  return (
    <div className="ticket-card">
      <div className="ticket-header">
        <div className="ticket-header-top">
          <span className={`badge badge-${ticket.category?.toLowerCase()}`}>
            {ticket.category}
          </span>

          <span className={`status-pill status-${(ticket.status || 'NEW').toLowerCase()}`}>
            {statusLabel}
          </span>
        </div>

        <h3>{ticket.subject}</h3>
        <p className="ticket-from">De : {ticket.fromEmail}</p>
        {ticket.createdAt && (
          <p className="ticket-date">
            Créé le {new Date(ticket.createdAt).toLocaleString('fr-FR')}
          </p>
        )}
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

      <div className="ticket-actions">
        <button
          className="btn-secondary"
          onClick={() => onChangeStatus(ticket.id, 'NEEDS_REVIEW')}
          disabled={ticket.status === 'NEEDS_REVIEW'}
        >
          À corriger
        </button>
        <button
          className="btn-primary"
          onClick={() => onChangeStatus(ticket.id, 'VALIDATED')}
          disabled={ticket.status === 'VALIDATED'}
        >
          Valider la réponse
        </button>
      </div>
    </div>
  );
}
