// src/App.jsx
import { useEffect, useState } from 'react';
import './index.css';
import { createEmail, fetchTickets, updateTicketStatus } from './api';
import EmailForm from './components/EmailForm';
import TicketList from './components/TicketList';

function App() {
  const [tickets, setTickets] = useState([]);
  const [loadingTickets, setLoadingTickets] = useState(false);
  const [creatingEmail, setCreatingEmail] = useState(false);
  const [filterStatus, setFilterStatus] = useState('ALL');

  async function loadTickets() {
    setLoadingTickets(true);
    try {
      const data = await fetchTickets();
      data.sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
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
      setTickets((prev) => [created, ...prev]);
    } finally {
      setCreatingEmail(false);
    }
  }

  async function handleChangeStatus(id, status) {
    try {
      const updated = await updateTicketStatus(id, status);
      setTickets((prev) =>
        prev.map((t) => (t.id === id ? updated : t))
      );
    } catch (err) {
      console.error('Erreur mise à jour statut', err);
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>AI Support Orchestrator</h1>
        <p>
          Analyse automatique des emails clients avec Gemini : classification, résumé, réponse proposée, validation humaine.
        </p>
      </header>

      <main className="app-main">
        <section className="left-panel">
          <EmailForm onEmailCreated={handleEmailCreated} loading={creatingEmail} />
        </section>

        <section className="right-panel">
          <TicketList
            tickets={tickets}
            loading={loadingTickets}
            filterStatus={filterStatus}
            onFilterChange={setFilterStatus}
            onChangeStatus={handleChangeStatus}
          />
        </section>
      </main>
    </div>
  );
}

export default App;
