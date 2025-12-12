# CAHIER DES CHARGES DETAILLE - APPLICATION « MES PAPIERS »

## 0. Cadre general

| Element | Description |
|---------|-------------|
| **Type d'application** | Application mobile de gestion de documents administratifs |
| **Plateforme cible initiale** | Android (smartphone principalement) |
| **Fonctionnement** | 100 % hors-ligne, stockage local, aucune communication reseau requise |
| **Modele economique** | Application entierement gratuite, sans limite d'usage, avec soutien volontaire (tips + dons externes) |

---

# PARTIE I - PHILOSOPHIE & POSITIONNEMENT

## I.1. Mission

Permettre a toute personne de :
- rassembler,
- organiser,
- securiser,
- exporter

ses documents administratifs importants (identite, factures, revenus, etc.) **sans dependre d'un cloud, d'un compte ou d'un prestataire**.

## I.2. Principes fondamentaux

### 1. Simplicite radicale
- Peu d'ecrans.
- Peu d'options visibles en surface.
- Aucune surcharge visuelle.

### 2. Style "Administratif Brut"
- Interface sobre, inspiree d'un "dossier administratif propre" : fond clair, cadres blancs, icones simples.
- Pas d'effets graphiques inutiles.

### 3. Securite & confidentialite
- Pas de cloud.
- Pas de compte.
- Pas de synchronisation.
- Pas de collecte de donnees.

### 4. Universalite des usages
- Capable de gerer la situation d'une personne seule, d'un couple, d'une famille nombreuse, d'un aidant qui suit plusieurs profils (enfants, parent dependant, voisin, etc.).

### 5. Aucune barriere economique
- Application entierement gratuite.
- Aucune fonctionnalite payante.
- Aucune limitation cachee.
- Soutien entierement volontaire (tips + dons externes).

---

# PARTIE II - ABSENCE TOTALE DE LIMITES D'USAGE

## II.1. Philosophie

Aucune limitation artificielle ne doit empecher un usage intensif dans des cas reels :
- mere seule avec 4 enfants,
- personne aidant un parent age + un enfant handicape,
- personne gerant des dossiers de proches (voisins, freres/soeurs, etc.),
- foyers recomposes, situations administratives complexes.

## II.2. Exigences fonctionnelles

| Element | Limite |
|---------|--------|
| **Profils** | Nombre illimite |
| **Documents** | Nombre illimite |
| **Categories** | Pas de limite stricte (categories de base fournies, systeme supporte ajouts personnalises) |
| **Exports (ZIP, PDF)** | Aucune restriction d'usage |
| **Historique de versions** | Illimite (dans la limite naturelle de l'espace disque) |

**Aucune fonctionnalite ne doit etre conditionnee a :**
- un paiement,
- un seuil d'usage,
- une periode d'essai,
- un abonnement.

---

# PARTIE III - ARCHITECTURE FONCTIONNELLE & TECHNIQUE (HAUT NIVEAU)

## III.1. Composants majeurs

| Module | Description |
|--------|-------------|
| **Module Profils** | Gestion des profils locaux (creation, selection, suppression) |
| **Module Documents** | Gestion des documents par profil (ajout, archivage, affichage, suppression) |
| **Module Categories** | Systeme de dossiers logiques (Identite, Sante, Logement, Revenus, Divers...) |
| **Module Scanner** | Scan photo vers PDF multipage |
| **Module Import** | Import de PDF existants depuis le stockage du telephone |
| **Module Export ZIP** | Selection de documents vers creation fichier ZIP vers partage |
| **Module Visualisation** | Lecture de documents PDF |
| **Module Historique** | Gestion et consultation des versions d'un document |
| **Module Rappels / Notifications** | Rappel mensuel (soutien), Rappel toutes les 10 ouvertures (note Play Store) |
| **Module Soutien volontaire** | Tips via Google Play, Dons externes (PayPal, etc.) |

## III.2. Stockage local

- **Metadonnees** : SQLite chiffree (une base par profil recommande, ou base unique avec champ `profile_id`).
- **Fichiers** : stockes dans la sandbox de l'application (repertoires internes non accessibles aux autres apps).

### Entites principales (conceptuellement)

- `Profile`
- `Category`
- `Document`
- `DocumentVersion`
- `ReminderConfig`
- `AppStats` (compteur d'ouvertures, dates de derniers rappels, etc.)

---

# PARTIE IV - PARCOURS UTILISATEUR (UX)

## IV.1. Onboarding

### Ecran 1 - Bienvenue

**Texte :**
- "Bienvenue dans Mes Papiers"
- "Une application simple et securisee pour organiser vos documents administratifs."
- "Vos documents sont stockes uniquement sur votre telephone."

**Actions :**
- Bouton : `Continuer`
- Lien discret : `En savoir plus`

### Ecran 2 - Stockage local

**Texte :**
- "Mes Papiers ne cree aucun compte et ne synchronise rien sur internet."
- "Tous vos documents sont enregistres dans un espace securise, local a votre appareil."

**Actions :**
- Bouton : `OK`
- Lien : `Comment ca fonctionne ?`

### Ecran 3 - Ajouter des documents

**Texte :**
- "Vous pouvez :"
  - Scanner un document avec l'appareil photo (conversion en PDF)
  - Importer un PDF deja present sur votre telephone

**Note :**
- "Les documents sont organises en dossiers (Identite, Factures, Revenus...)."

**Actions :**
- Bouton : `Compris`

### Ecran 4 - Export ZIP (fonctionnalite cle)

**Texte :**
- "Vous pouvez creer un dossier ZIP avec les documents de votre choix :"
- "Carte d'identite, Facture d'electricite, Assurance habitation, etc."
- "Le ZIP est pret a etre partage par email, messagerie ou autre."

**Note :**
- "Il ne quitte votre telephone que si vous choisissez vous-meme de le partager."

**Actions :**
- Bouton : `Super, continuer`

### Ecran 5 - (Optionnel) "Sans limites, sans compte"

**Texte :**
- "Vous pouvez creer autant de profils que necessaire : vous-meme, vos enfants, un parent, un proche..."
- "Il n'y a aucune limite, aucun compte, aucun mode payant."

**Actions :**
- Bouton : `Continuer`

### Ecran 6 - Creation du premier profil

**Champ :** `Nom du profil` (exemple prerempli : "Moi")

**Actions :**
- Bouton : `Creer le profil` → arrivee sur le Dashboard

---

# PARTIE V - MODULE PROFILS

## V.1. Fonctions

- Creer un nouveau profil (nom obligatoire).
- Selectionner un profil existant.
- Renommer un profil.
- Supprimer un profil (avec double confirmation).

## V.2. Ecran Profile Picker

**Contenu :**
- Liste de cartes ou tuiles, chacune avec :
  - Nom du profil
  - Eventuellement une initiale ou icone

**Boutons :**
- `Ajouter un profil`
- `Gerer les profils` (renommage/suppression)

---

# PARTIE VI - DASHBOARD & CATEGORIES

## VI.1. Dashboard

- Affiche les categories pour le **profil actif**.
- Scroll vertical.
- Chaque categorie = bloc (carte) comprenant :
  - Titre (ex. "Identite & Permis")
  - sous-texte eventuel ("3 documents")
  - Indicateur (si factures manquantes, si aucun document, etc.)
  - Grille de tuiles de documents.

## VI.2. Categories par defaut

| Categorie | Contenu type |
|-----------|--------------|
| **Identite & Permis** | CNI, Passeport, Permis, etc. |
| **Sante & Famille** | Carte sante, mutuelle, livret, etc. |
| **Logement & Factures** | Factures electricite, gaz, internet, assurances... |
| **Revenus & Impots** | Bulletins de salaire, avis d'impot, releves, etc. |
| **Autres documents** | Tout ce qui ne rentre pas dans les categories ci-dessus |

*Les categories supplementaires ou personnalisables peuvent arriver plus tard (extension du produit).*

## VI.3. Grille adaptative

**Sur smartphone :**
- 2 colonnes en petit ecran
- 3 colonnes si largeur suffisante

**Sur tablette (plus tard) :**
- 3 a 6 colonnes selon orientation et taille

## VI.4. Tuiles

**Chaque tuile contient :**
- Icone (solide, simple, symbolique)
- Label centre (nom du document)
- Indicateur visuel (pastille expiration eventuelle)

**Interaction :**
- Tap sur la tuile → ouvre la **Derniere version** du document dans le Viewer
- Long press (optionnel) → menu contextuel (voir, historique, renommer, supprimer)

---

# PARTIE VII - GESTION DES DOCUMENTS

## VII.1. Ajout d'un document

### Mode Scanner
1. Ouvre le module de capture photo.
2. Permet d'ajouter une ou plusieurs pages.
3. Affiche une preview multipage.
4. L'utilisateur valide ou refait.

### Mode Import PDF
1. Ouvre l'explorateur de fichiers.
2. L'utilisateur selectionne un PDF existant.

### Apres scan/import, l'utilisateur doit :
- Choisir la **categorie**.
- Saisir ou confirmer le **nom du document**.
- Completer les metadonnees specifiques selon la categorie.

## VII.2. Metadonnees par logique

### Identite & Permis
| Champ | Description |
|-------|-------------|
| Nom du document | ex. Carte d'identite |
| Date d'expiration | JJ/MM/AAAA |

### Sante & Famille
| Champ | Description |
|-------|-------------|
| Nom du document | Carte sante, Livret de famille |
| Option | personne concernee (si plusieurs profils de type enfant) |

### Logement & Factures
| Champ | Description |
|-------|-------------|
| Nom du document | Facture Electricite, Facture Gaz... |
| Periode | Mois + annee (Selecteur mois/annee) |

### Revenus & Impots
| Champ | Description |
|-------|-------------|
| Nom du document | Bulletin de Salaire, Avis d'Impot |
| Periode | Mois + annee OU annee unique (pour les impots) |

### Autres
| Champ | Description |
|-------|-------------|
| Nom du document | champ texte libre |

---

# PARTIE VIII - HISTORIQUE DES VERSIONS

## VIII.1. Principe

Chaque document possede un **ensemble de versions**, correspondant a :
- chaque nouveau scan importe,
- chaque nouveau PDF importe sous le meme document logique.

## VIII.2. Affichage

**Ecran "Historique" :**
- Liste de versions triee par date decroissante.
- Chaque version affiche :
  - date d'ajout,
  - (optionnel) source (scanne/importe).

**Actions :**
- Tap → ouvrir la version dans le Viewer.
- Long press → option de suppression.

*NB : La version actuelle du document = la plus recente.*

---

# PARTIE IX - VISUALISATION

## IX.1. Viewer PDF

- Affiche le PDF en plein ecran.
- Permet zoom et scroll.

**Barre d'actions :**
- `Retour`
- `Exporter / Partager` (cette version comme PDF)
- `Historique` (acces aux versions)
- `Supprimer cette version` (optionnel)

---

# PARTIE X - EXPORTATION (PDF ET ZIP)

## X.1. Export PDF unitaire

Depuis le Viewer, l'utilisateur peut :
- Exporter la version en PDF →
  - Creation d'une **copie temporaire** avec le nom correct (voir regle de renommage).
  - Ouverture du menu de partage systeme (Gmail, Messages, Drive, etc.).

## X.2. Export ZIP personnalise

**Ecran "Exporter mon dossier" :**

1. L'utilisateur voit la liste des categories et documents associes.
2. Il coche les documents qu'il veut inclure.
3. Il valide.
4. L'application genere un ZIP avec une structure de dossiers :

```
/Identite/
/Sante/
/Logement/
/Revenus/
/Divers/
```

5. Le ZIP est ensuite propose au partage via le sheet systeme.

**Important :**
- Le ZIP est cree uniquement localement.
- Aucune copie n'est envoyee ailleurs automatiquement.

---

# PARTIE XI - REGLE DE RENOMMAGE DES FICHIERS EXPORTES

## XI.1. Format general

Nom de fichier exporte = **nom du document tel qu'affiche dans l'UI**, enrichi de la periode si applicable.

**Regles :**
- Espaces naturels
- Accents conserves
- Majuscules "normales"
- Aucun tiret
- Aucun underscore

**Exemples :**
- `Carte d'identite.pdf`
- `Permis de conduire.pdf`
- `Facture Electricite Decembre 2024.pdf`
- `Quittance de Loyer Avril 2025.pdf`
- `Bulletin de Salaire Novembre 2024.pdf`
- `Avis d'Impot 2023.pdf`

## XI.2. Normalisation technique

**Avant export :**
- Supprimer les caracteres interdits dans un nom de fichier (`/ \ : * ? " < > |`).
- Reduire les doubles espaces.
- Ajouter `.pdf` si inexistant.

> L'original interne (fichier dans la sandbox) garde un identifiant interne (UUID, etc.).
> Seule la **copie exportee** est renommee ainsi.

---

# PARTIE XII - RAPPELS & INTERACTIONS

## XII.1. Rappel mensuel - soutien

**Condition :** au moins 30 jours depuis le dernier rappel.

**Affichage :** au lancement de l'app.

**Texte possible :**
> **Titre :** Merci d'utiliser Mes Papiers
>
> **Message :** L'application est gratuite et le restera toujours.
> Si elle vous aide a gerer vos documents, vous pouvez la soutenir en laissant un avis ou un petit pourboire.

**Boutons :**
- `Soutenir l'application` (ouvre ecran de soutien)
- `Laisser un avis` (ouvre Play Store)
- `Plus tard`
- Optionnel : `Ne plus me le rappeler` (desactive definitivement)

## XII.2. Rappel toutes les 10 ouvertures - note Play Store

**Condition :**
- Compteur `appOpenCount`.
- Si `appOpenCount % 10 == 0` et que l'utilisateur n'a pas encore note l'app (et n'a pas desactive les rappels de note).

**Texte :**
> **Titre :** Mes Papiers vous rend service ?
>
> **Message :** Vous pouvez nous aider en laissant une note sur Google Play. Cela aide d'autres personnes a decouvrir l'application.

**Boutons :**
- `Laisser une note` (ouvre Play Store, flag `hasRated = true`)
- `Plus tard`
- `Ne plus afficher` (flag `ratingPromptDisabled = true`)

---

# PARTIE XIII - ECRAN "SOUTENIR L'APPLICATION"

## XIII.1. Section 1 - Tips via Google Play

**Texte :**
> "Si vous le souhaitez, vous pouvez soutenir le developpement avec un petit pourboire via Google Play."

**Boutons de montants :**
| Montant |
|---------|
| 0,99 EUR |
| 1,99 EUR |
| 2,99 EUR |
| 4,99 EUR |
| 9,99 EUR |
| 19,99 EUR |
| 49,99 EUR |

*Aucune fonctionnalite n'est liee a ces paiements.*

## XIII.2. Section 2 - Don libre externe

**Texte :**
> "Vous pouvez aussi faire un don libre via les solutions suivantes (ce don ne debloque aucune fonctionnalite dans l'application) :"

**Liens :**
- PayPal (lien ou bouton)
- Autre (Lydia, Ko-fi, Tipeee...) selon choix ulterieur

---

# PARTIE XIV - ACCESSIBILITE & PERFORMANCE

## XIV.1. Accessibilite

- Police suffisamment grande par defaut.
- Contrastes forts.
- Pas de choix de design "fantaisie" qui reduise la lisibilite.

## XIV.2. Performance

- L'app doit rester fluide avec plusieurs centaines de documents.
- Chargement rapide du Dashboard.
- Scanner et viewer PDF reactifs.

---

# PARTIE XV - ERGONOMIE HORS-LIGNE & COMPORTEMENTS SPECIAUX

- L'app doit fonctionner **exactement de la meme maniere** connecte ou non.
- Tout message d'erreur reseau doit etre evite, puisqu'il n'y a pas d'usage reseau.

**Cas particuliers a gerer :**
- Espace disque insuffisant → message clair lors de scan/import/export.
- Document corrompu → message propre.

---

# PARTIE XVI - CONTEXTE TECHNIQUE GENERAL

## XVI.1. Specifications techniques

| Element | Description |
|---------|-------------|
| **Plateforme cible** | Android en premier (smartphone, eventuellement tablette plus tard) |
| **Mode de fonctionnement** | 100 % hors-ligne, 0 requete reseau, 0 backend, 0 cloud |
| **Persistance** | Metadonnees en SQLite chiffre (ou similaire), Fichiers PDF + ZIP dans la sandbox application |
| **Securite d'acces** | Protection par biometrie / PIN au lancement de l'app (screen lock optionnel, mais prevu) |

---

# PARTIE XVII - ARCHITECTURE LOGICIELLE (COUCHES & MODULES)

## XVII.1. Decoupage en couches

### 1. UI Layer (Presentation)
- Ecrans : Onboarding, ProfilePicker, Dashboard, AddDocument, Viewer, Export, History, Settings, Support.
- Logique de navigation.
- Affichage des etats (chargement, OK, erreur).

### 2. Domain Layer
**UseCases / Interactors :**
- `CreateProfile`, `SwitchProfile`, `DeleteProfile`
- `AddDocument`, `UpdateDocument`, `DeleteDocument`
- `AddDocumentVersion`, `GetLatestVersion`, `ListVersions`
- `GenerateZipExport`
- `GetDashboardSummary`
- `ScheduleMonthlySupportReminder`, `ShouldShowRatingPrompt`

*Contient les regles metier (expiration, periodicite, indicateurs).*

### 3. Data Layer
**Repositories :**
- `ProfileRepository`
- `CategoryRepository`
- `DocumentRepository`
- `DocumentVersionRepository`
- `SettingsRepository`
- `StatsRepository` (app opens, last prompts)

**Sources :**
- SQLite / Room / Moor / etc.
- File storage (PDF + ZIP)
- API systeme (notifications locales, scanner, file picker)

---

# PARTIE XVIII - MODELES DE DONNEES / ENTITES METIER

## XVIII.1. Entite `Profile`

Represente une personne geree dans l'app.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string UUID | PK |
| `name` | string (<= 80) | ex : "Moi", "Enfant 1", "Papa" |
| `createdAt` | timestamp | |
| `updatedAt` | timestamp | |
| `isArchived` | bool | si on veut un jour "masquer" sans supprimer |

## XVIII.2. Entite `Category`

Categories logiques (Identite, Sante, etc.).

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string UUID | PK |
| `profileId` | string | FK → Profile.id |
| `type` | enum string | `"IDENTITY" \| "FAMILY_HEALTH" \| "HOUSING_BILLS" \| "INCOME_TAX" \| "OTHER"` |
| `customLabel` | string nullable | pour renommage eventuel |
| `orderIndex` | int | pour l'ordre d'affichage |
| `createdAt` | timestamp | |
| `updatedAt` | timestamp | |

## XVIII.3. Entite `Document`

Un "document logique" (ex : "Carte d'identite", "Facture Electricite").

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string UUID | PK |
| `profileId` | string | FK → Profile |
| `categoryId` | string | FK → Category |
| `title` | string (<= 120) | ex : "Carte d'identite", "Facture Electricite" |
| `documentType` | enum string | `"IDENTITY" \| "PERMIT" \| "HEALTH_CARD" \| "BILL" \| "PAYSLIP" \| "TAX_NOTICE" \| "OTHER"` |
| `periodMonth` | int nullable (1-12) | pour factures/salaires |
| `periodYear` | int nullable | pour factures/revenus/impots |
| `expiryDate` | date nullable | pour CNI, permis, etc. |
| `isFavourite` | bool | eventuellement pour marquer les documents souvent exportes |
| `createdAt` | timestamp | |
| `updatedAt` | timestamp | |

## XVIII.4. Entite `DocumentVersion`

Une version precise d'un document (fichier PDF concret).

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string UUID | PK |
| `documentId` | string | FK → Document |
| `filePath` | string | chemin interne (sandbox) vers le PDF |
| `pagesCount` | int | nombre de pages du PDF |
| `source` | enum string | `"SCANNED" \| "IMPORTED"` |
| `createdAt` | timestamp | |
| `isDeleted` | bool | logique, sans supprimer physiquement le fichier |

## XVIII.5. Entite `AppSettings`

Configuration globale de l'app.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string | singleton key, ex : "GLOBAL_SETTINGS" |
| `biometricEnabled` | bool | |
| `tipSupportEnabled` | bool | |
| `externalDonationVisible` | bool | |

## XVIII.6. Entite `AppStats`

Statistiques d'usage pour les rappels.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | string | singleton key, ex : "GLOBAL_STATS" |
| `appOpenCount` | int | |
| `lastSupportPromptAt` | timestamp nullable | |
| `lastRatingPromptAt` | timestamp nullable | |
| `hasRated` | bool | |
| `ratingPromptDisabled` | bool | |

---

# PARTIE XIX - SCHEMA SQLITE

## XIX.1. Table `profiles`

```sql
CREATE TABLE profiles (
  id TEXT PRIMARY KEY,
  name TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  updated_at INTEGER NOT NULL,
  is_archived INTEGER NOT NULL DEFAULT 0
);
```

## XIX.2. Table `categories`

```sql
CREATE TABLE categories (
  id TEXT PRIMARY KEY,
  profile_id TEXT NOT NULL,
  type TEXT NOT NULL,
  custom_label TEXT,
  order_index INTEGER NOT NULL,
  created_at INTEGER NOT NULL,
  updated_at INTEGER NOT NULL,
  FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE
);
CREATE INDEX idx_categories_profile ON categories(profile_id);
```

## XIX.3. Table `documents`

```sql
CREATE TABLE documents (
  id TEXT PRIMARY KEY,
  profile_id TEXT NOT NULL,
  category_id TEXT NOT NULL,
  title TEXT NOT NULL,
  document_type TEXT NOT NULL,
  period_month INTEGER,
  period_year INTEGER,
  expiry_date INTEGER,
  is_favourite INTEGER NOT NULL DEFAULT 0,
  created_at INTEGER NOT NULL,
  updated_at INTEGER NOT NULL,
  FOREIGN KEY (profile_id) REFERENCES profiles(id) ON DELETE CASCADE,
  FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);
CREATE INDEX idx_documents_profile ON documents(profile_id);
CREATE INDEX idx_documents_category ON documents(category_id);
CREATE INDEX idx_documents_type ON documents(document_type);
CREATE INDEX idx_documents_period ON documents(period_year, period_month);
```

## XIX.4. Table `document_versions`

```sql
CREATE TABLE document_versions (
  id TEXT PRIMARY KEY,
  document_id TEXT NOT NULL,
  file_path TEXT NOT NULL,
  pages_count INTEGER NOT NULL,
  source TEXT NOT NULL,
  created_at INTEGER NOT NULL,
  is_deleted INTEGER NOT NULL DEFAULT 0,
  FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
);
CREATE INDEX idx_versions_document ON document_versions(document_id);
```

## XIX.5. Table `app_settings`

```sql
CREATE TABLE app_settings (
  id TEXT PRIMARY KEY,
  biometric_enabled INTEGER NOT NULL,
  tip_support_enabled INTEGER NOT NULL,
  external_donation_visible INTEGER NOT NULL
);
```

## XIX.6. Table `app_stats`

```sql
CREATE TABLE app_stats (
  id TEXT PRIMARY KEY,
  app_open_count INTEGER NOT NULL,
  last_support_prompt_at INTEGER,
  last_rating_prompt_at INTEGER,
  has_rated INTEGER NOT NULL,
  rating_prompt_disabled INTEGER NOT NULL
);
```

---

# PARTIE XX - STOCKAGE FICHIERS (SANDBOX)

## XX.1. Arborescence recommandee (interne)

Racine donnees (ex. `/data/user/0/app/files/` ou equivalent) :

```
/profiles/
  /[profileId]/
    /documents/
      /[documentId]/
        [versionId].pdf
    /exports/
      export_[timestamp].zip
```

Le **chemin exact** (filePath) de chaque version est stocke dans `document_versions.file_path`.

## XX.2. Gestion de la suppression

- Suppression logique (`is_deleted = 1`) d'abord,
- Purge physique differee si besoin (garbage collector interne).

---

# PARTIE XXI - MACHINE A ETATS - DOCUMENT

## XXI.1. Etats metier simplifies

| Etat | Description |
|------|-------------|
| `EMPTY` | document logique cree mais aucune version (optionnel, selon UX) |
| `ACTIVE` | au moins une version non supprimee |
| `EXPIRED` | si `expiryDate` < date du jour |
| `ALERT_EXPIRY` | si `expiryDate` - today <= 90 jours |

*Ces etats ne necessitent pas de champ dedie, ils sont derives a la volee.*

---

# PARTIE XXII - FLUX D'AJOUT D'UN DOCUMENT

## XXII.1. Ajout via Scanner

1. UI : l'utilisateur choisit `Scanner un document`.
2. Le module scanner ouvre la camera.
3. L'utilisateur capture 1..N pages.
4. Le module :
   - detecte les bords,
   - applique redressement + filtre,
   - assemble les pages en **un PDF multi-page** temporaire.
5. On passe a l'ecran "Informations document" :
   - Selecteur de categorie
   - Champ titre
   - Champs supplementaires selon categorie (expiryDate, periodMonth, periodYear).
6. Creation d'un nouvel enregistrement `Document` si necessaire (si document logique nouveau).
7. Creation d'un `DocumentVersion` :
   - `id` (UUID)
   - `file_path` = path final
   - `pages_count` = N
   - `source` = "SCANNED"
8. Deplacement du PDF depuis un emplacement temporaire vers sa destination definitive (`/profiles/[p]/documents/[d]/[v].pdf`).
9. Mise a jour `Document.updatedAt`.

## XXII.2. Ajout via Import PDF

1. UI : l'utilisateur choisit `Importer un fichier PDF`.
2. Le file picker retourne un URI / path.
3. L'app copie le fichier dans la sandbox (`/profiles/[p]/documents/tmp/`).
4. Ecran "Informations document" identique au flux scanner.
5. Creation `Document` + `DocumentVersion` avec `source = "IMPORTED"`.

---

# PARTIE XXIII - LOGIQUE D'EXPORT PDF UNITAIRE

## XXIII.1. Renommage

**Entrees :**
- `document.title`
- `document.periodMonth` / `document.periodYear`
- `documentType` (permet de decider si on inclut mois/annee dans le nom)

**Pseudo-algo :**

```pseudo
function buildExportFileName(document):
    baseName = document.title.trim()

    if document.documentType in ["BILL", "PAYSLIP"]:
        if document.periodMonth != null and document.periodYear != null:
            monthLabel = toFrenchMonth(document.periodMonth) // 1 -> "Janvier" etc.
            baseName = baseName + " " + monthLabel + " " + document.periodYear.toString()
    else if document.documentType == "TAX_NOTICE":
        if document.periodYear != null:
            baseName = baseName + " " + document.periodYear.toString()

    sanitized = removeIllegalFileChars(baseName)
    sanitized = collapseMultipleSpaces(sanitized).trim()

    return sanitized + ".pdf"
```

`removeIllegalFileChars` supprime : `/ \ : * ? " < > |`

## XXIII.2. Export

1. Copier le PDF de `document_versions.file_path` vers un dossier temporaire d'export avec le **nom calcule**.
2. Appeler le systeme de partage (intent / share sheet).

---

# PARTIE XXIV - LOGIQUE D'EXPORT ZIP

## XXIV.1. Selection

- L'utilisateur arrive sur un ecran listant :
  - categories → documents → checkbox par document.
- On recupere la liste des `documentId` selectionnes.
- Pour chaque doc, on prend la **version la plus recente** (createdAt max, `is_deleted = 0`).

## XXIV.2. Structure ZIP

Pour chaque document selectionne :
- Determiner le chemin interne dans le ZIP :
  - dossier = categorie (avec label type "Identite", "Logement", etc.)
  - fichier = nom selon la regle de renommage.

**Exemple :**
```
Identite/Carte d'identite.pdf
Logement/Facture Electricite Decembre 2024.pdf
Revenus/Bulletin de Salaire Novembre 2024.pdf
```

## XXIV.3. Processus

**Pseudo-algo :**

```pseudo
function generateZipExport(profileId, selectedDocumentIds):
    documents = loadDocumentsWithLatestVersion(profileId, selectedDocumentIds)
    zipPath = buildExportZipPath(profileId, currentTimestamp())

    with ZipWriter(zipPath) as zip:
        for doc in documents:
            latestVersion = doc.latestVersion
            if latestVersion == null:
                continue
            exportName = buildExportFileName(doc)
            zipEntryPath = mapCategoryToFolder(doc.categoryType) + "/" + exportName
            zip.addFile(latestVersion.file_path, zipEntryPath)

    return zipPath
```

Ensuite :
- App → sheet de partage systeme avec `zipPath` comme fichier a partager.

---

# PARTIE XXV - RAPPELS & STATS - LOGIQUE TECHNIQUE

## XXV.1. Increment du compteur d'ouvertures

Au lancement de l'app (apres unlock biometrique, le cas echeant) :

```pseudo
function onAppStart():
    stats = loadAppStats()
    stats.app_open_count += 1
    saveAppStats(stats)

    if shouldShowSupportPrompt(stats):
        showSupportPopup()
        stats.last_support_prompt_at = now()
        saveAppStats(stats)
        return

    if shouldShowRatingPrompt(stats):
        showRatingPopup()
        stats.last_rating_prompt_at = now()
        saveAppStats(stats)
```

## XXV.2. Rappel mensuel de soutien

```pseudo
function shouldShowSupportPrompt(stats):
    if stats.last_support_prompt_at == null:
        return true
    return (now() - stats.last_support_prompt_at) >= 30 days
```

## XXV.3. Rappel toutes les 10 ouvertures pour la note

```pseudo
function shouldShowRatingPrompt(stats):
    if stats.has_rated:
        return false
    if stats.rating_prompt_disabled:
        return false
    return (stats.app_open_count % 10 == 0)
```

**Actions boutons :**
- `Laisser une note` → open Play Store ; `stats.has_rated = true`.
- `Ne plus afficher` → `stats.rating_prompt_disabled = true`.

---

# PARTIE XXVI - ONBOARDING - PERSISTANCE

Un flag `hasCompletedOnboarding` peut etre stocke dans `app_settings` ou dans un fichier de prefs leger.

- Si false → afficher Onboarding.
- Apres validation du dernier ecran → `hasCompletedOnboarding = true` et creation du premier profil.

---

# PARTIE XXVII - SECURITE & BIOMETRIE

## XXVII.1. Contexte

- Option dans `AppSettings` : `biometricEnabled`.
- Si true → a l'ouverture de l'app, demander auth biometrique (ou code PIN si fallback) via API systeme.

## XXVII.2. Flux

```pseudo
function launchApp():
    settings = loadAppSettings()
    if settings.biometric_enabled:
        if not biometricAuthSucceeded():
            closeApp()
            return

    onAppStart()
```

---

# PARTIE XXVIII - EXTENSIBILITE PREVUE

- Ajout futur de :
  - champs supplementaires par document (ex. n deg. contrat, n deg. police d'assurance) → prevoir colonnes `extra_json` (TEXT nullable) dans `documents`.
  - sauvegarde/restauration globale via fichier chiffre → reutiliser la logique ZIP + encryption.

---

# PARTIE XXIX - CONTRAINTES QUALITE

| Contrainte | Description |
|------------|-------------|
| **Identifiants** | Tous les `id` internes sont des UUID (string) |
| **Transactions** | Toutes les operations DB sont transactionnelles (notamment creation document + version) |
| **Gestion erreurs** | Toute erreur sur lecture fichier doit etre geree par un message clair |
| **Performance** | Temps de chargement initial du Dashboard < 500ms sur un device moyen avec quelques centaines de documents |

*Les versions peuvent etre chargees a la demande.*

---

# ANNEXE A - LISTE DES ECRANS

| Ecran | Description |
|-------|-------------|
| Onboarding (1-6) | Introduction et creation premier profil |
| ProfilePicker | Selection/gestion des profils |
| Dashboard | Vue principale avec categories et documents |
| AddDocument | Ajout de document (scanner ou import) |
| DocumentInfo | Saisie des metadonnees du document |
| Viewer | Visualisation PDF |
| History | Historique des versions d'un document |
| Export | Selection et generation ZIP |
| Settings | Parametres de l'application |
| Support | Ecran de soutien (tips + dons) |

---

# ANNEXE B - GLOSSAIRE

| Terme | Definition |
|-------|------------|
| **Profil** | Personne pour laquelle les documents sont geres |
| **Categorie** | Dossier logique regroupant des documents par theme |
| **Document** | Entite logique representant un type de document |
| **Version** | Fichier PDF physique associe a un document |
| **Sandbox** | Espace de stockage prive de l'application |
| **Export ZIP** | Archive contenant plusieurs documents pour partage |

---

*Document genere le 2025-12-09*
*Version 1.0*
