// src/components/EmailForm.jsx
import { useState } from 'react';

export default function EmailForm({ onEmailCreated, loading }) {
  const [from, setFrom] = useState('client@example.com');
  const [subject, setSubject] = useState('Problème sur ma facture FACT-12345');
  const [body, setBody] = useState(
    "Bonjour, je ne comprends pas le montant de la facture FACT-12345, pouvez-vous vérifier ?"
  );

  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);

    try {
      await onEmailCreated({ from, subject, body });
    } catch (err) {
      console.error(err);
      setError(err.message || 'Erreur lors de l’envoi');
    }
  }

  return (
    <form className="email-form" onSubmit={handleSubmit}>
      <h2>Simuler un email client</h2>

      <div className="form-group">
        <label>De (email du client)</label>
        <input
          type="email"
          value={from}
          onChange={(e) => setFrom(e.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label>Objet</label>
        <input
          type="text"
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label>Message</label>
        <textarea
          rows={5}
          value={body}
          onChange={(e) => setBody(e.target.value)}
          required
        />
      </div>

      {error && <p className="error">{error}</p>}

      <button type="submit" disabled={loading}>
        {loading ? 'Analyse en cours...' : 'Envoyer & analyser'}
      </button>
    </form>
  );
}
