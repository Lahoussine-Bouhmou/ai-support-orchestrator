// src/App.jsx
import { useEffect, useState } from 'react';
import './index.css';
import { createEmail, fetchTickets } from './api';
import EmailForm from './components/EmailForm';
import TicketList from './components/TicketList';

function App() {
  const [tickets, setTickets] = useState([]);
  const [loadingTickets, setLoadingTickets] = useState(false);
  const [creatingEmail, setCreatingEmail] = useState(false);

  async function loadTickets() {
    setLoadingTickets(true);
    try {
      const data = await fetchTickets();
      // on peut trier par id décroissant pour voir les plus récents en haut
      data.sort((a, b) => b.id - a.id);
      setTickets(data);
    } catch (err) {
      console.error('Erreur chargement tickets', err);
    } finally {
      setLoadingTickets(false);
    }
  }

  useEffect(() => {
    loadTickets();
  }, []);

  async function handleEmailCreated(email) {
    setCreatingEmail(true);
    try {
      const created = await createEmail(email);
      // ajouter le nouveau ticket en tête
      setTickets((prev) => [created, ...prev]);
    } finally {
      setCreatingEmail(false);
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>AI Support Orchestrator</h1>
        <p>
          Analyse automatique des emails clients avec Gemini : classification,
          résumé, réponse proposée.
        </p>
      </header>

      <main className="app-main">
        <section className="left-panel">
          <EmailForm onEmailCreated={handleEmailCreated} loading={creatingEmail} />
        </section>

        <section className="right-panel">
          <TicketList tickets={tickets} loading={loadingTickets} />
        </section>
      </main>
    </div>
  );
}

export default App;
