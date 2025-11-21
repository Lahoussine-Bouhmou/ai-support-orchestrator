// src/api.js

const API_BASE = 'http://localhost:8080/api';

export async function createEmail(email) {
  const response = await fetch(`${API_BASE}/emails`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(email),
  });

  if (!response.ok) {
    const text = await response.text().catch(() => '');
    throw new Error(`Erreur API (${response.status}): ${text}`);
  }

  return response.json();
}

export async function fetchTickets() {
  const response = await fetch(`${API_BASE}/emails/tickets`);

  if (!response.ok) {
    const text = await response.text().catch(() => '');
    throw new Error(`Erreur API (${response.status}): ${text}`);
  }

  return response.json();
}
