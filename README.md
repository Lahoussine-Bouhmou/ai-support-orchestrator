# AI Support Orchestrator

Application web de traitement automatique des emails clients avec IA (Gemini) :

- Classification des emails (facture, bug, etc.)
- Extraction d'informations clés (ex : numéro de facture)
- Résumé automatique
- Proposition de réponse professionnelle
- Validation humaine du message avant envoi

Stack : **Java / Spring Boot, React, Gemini API, H2**.

---

## Objectif du projet

Ce projet a été réalisé pour démontrer l'intégration concrète d'outils IA dans un contexte métier
de **support client** :

- automatiser le pré-traitement des emails reçus,
- assister les agents avec une réponse IA de haute qualité,
- garder l'humain dans la boucle via un système de validation.

Il correspond à un cas d’usage réel d’**intégration d’outils IA dans des processus métiers**.

---

## Architecture

- **Backend** (`/backend`)
  - API REST Spring Boot
  - Endpoint `POST /api/emails` : réception d'un email (from, subject, body)
  - Appel au modèle **Gemini** (Google AI) pour :
    - catégoriser l’email (`QUESTION_FACTURE`, `BUG`, `DEMANDE_INFO_PRODUIT`, `AUTRE`)
    - extraire des infos (nom client, numéro de facture…)
    - générer un résumé
    - proposer une réponse professionnelle
  - Persistance des tickets en base H2
  - Gestion du statut de ticket : `NEW`, `VALIDATED`, `NEEDS_REVIEW`
  - Endpoint `GET /api/emails/tickets` : récupération de tous les tickets
  - Endpoint `PUT /api/emails/tickets/{id}/status` : mise à jour du statut

- **Frontend** (`/frontend`)
  - Application **React (Vite)**
  - Formulaire pour simuler un email client
  - Affichage des tickets générés (classement par date)
  - Détail d’un ticket : email original, résumé IA, numéro de facture, réponse proposée
  - Boutons :
    - **“Valider la réponse”** → passe le ticket en `VALIDATED`
    - **“À corriger”** → passe le ticket en `NEEDS_REVIEW`
  - Filtre par statut : *Tous / Nouveaux / Validés / À corriger*

---

## Installation

### 1. Cloner le repository

```bash
git clone https://github.com/<ton-username>/ai-support-orchestrator.git
cd ai-support-orchestrator
````

### 2. Backend (Spring Boot)

```bash
cd backend
./mvnw spring-boot:run     # ou mvn spring-boot:run
```

Configurer la clé API Gemini via une variable d’environnement :

```bash
export GEMINI_API_KEY="TA_CLE_GEMINI"
```

Le backend démarre sur `http://localhost:8080`.

### 3. Frontend (React)

```bash
cd ../frontend
npm install
npm run dev
```

Le frontend démarre (par défaut) sur `http://localhost:5173` (ou 517x).

---

## Exemple d’utilisation

1. Lancer le backend et le frontend.
2. Dans l’interface web :

    * saisir un email client (ex. question sur une facture FACT-12345),
    * cliquer sur **“Envoyer & analyser”**.
3. L’application :

    * envoie l’email au backend,
    * appelle Gemini pour l’analyser,
    * crée un ticket avec :

        * catégorie,
        * résumé IA,
        * numéro de facture,
        * réponse proposée.
4. Dans le panneau de droite :

    * voir le ticket,
    * afficher l’email original,
    * lire la réponse IA,
    * cliquer sur **“Valider la réponse”** ou **“À corriger”**.

---

## Points techniques intéressants

* Intégration d’un modèle **Gemini** via API REST, avec parsing robuste du JSON retourné.
* Séparation claire **backend / frontend**.
* Gestion des erreurs (fallback si l’IA ne répond pas comme prévu).
* CORS configuré pour permettre aux clients web locaux d’appeler l’API.
* Base de données en mémoire (H2) pour un setup simple mais extensible (migration possible vers PostgreSQL/MySQL).

---

## Pistes d’évolution

* Support d’autres types d’emails (bugs techniques, demandes d’info produit).
* Mise en place de **workflows IA** configurables (enchaînement de plusieurs modèles/étapes).
* Authentification des utilisateurs (agents support).
* Déploiement sur le cloud (AWS / GCP) avec base de données persistante.
* Intégration dans un vrai outil de ticketing (Jira, Zendesk, etc.) via API.

```
