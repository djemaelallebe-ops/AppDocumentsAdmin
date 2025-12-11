# Mes Papiers - Application de gestion de documents administratifs

Application mobile Android permettant de rassembler, organiser, securiser et exporter ses documents administratifs importants (identite, factures, revenus, etc.) **sans dependre d'un cloud, d'un compte ou d'un prestataire**.

## Caracteristiques principales

- **100% hors-ligne** : Aucune communication reseau requise
- **Stockage local securise** : Donnees chiffrees dans la sandbox de l'application
- **Multi-profils** : Gerez les documents de toute la famille (enfants, parents, proches...)
- **Scanner integre** : Capturez vos documents avec l'appareil photo (conversion PDF)
- **Import PDF** : Importez des documents existants
- **Export ZIP** : Creez des dossiers complets a partager
- **Gratuit sans limites** : Aucune fonctionnalite payante, aucune restriction

## Documentation

- [Cahier des Charges Complet](docs/CAHIER_DES_CHARGES.md) - Specifications detaillees du produit

## Structure du projet

```
app/
  src/main/
    java/com/mespapiers/app/
      data/                    # Couche donnees
        local/
          dao/                 # Data Access Objects (Room)
          entity/              # Entites de base de donnees
          database/            # Configuration Room
        repository/            # Repositories
      domain/
        model/                 # Modeles metier
      ui/
        theme/                 # Theme Material 3
        navigation/            # Navigation Compose
        screens/
          onboarding/          # Ecrans d'introduction
          profile/             # Gestion des profils
          dashboard/           # Ecran principal
          document/            # Ajout/edition documents
          viewer/              # Visualisation PDF
          export/              # Export ZIP
          settings/            # Parametres
          support/             # Ecran de soutien
      di/                      # Injection de dependances (Hilt)
      util/                    # Utilitaires (FileManager)
    res/                       # Ressources Android
docs/
  CAHIER_DES_CHARGES.md        # Specifications produit completes
```

## Technologies utilisees

- **Kotlin** - Langage principal
- **Jetpack Compose** - UI declarative moderne
- **Material 3** - Design system
- **Room** - Base de donnees SQLite
- **Hilt** - Injection de dependances
- **CameraX** - Capture photo
- **ML Kit Document Scanner** - Scan de documents
- **Coroutines & Flow** - Programmation asynchrone

## Configuration requise

- Android SDK 26+ (Android 8.0 Oreo)
- Android Studio Hedgehog ou plus recent
- JDK 17

## Build

```bash
# Clone le projet
git clone https://github.com/your-repo/AppDocumentsAdmin.git

# Ouvre dans Android Studio et synchronise Gradle

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease
```

## Principes fondamentaux

1. **Simplicite radicale** - Peu d'ecrans, peu d'options visibles
2. **Style administratif brut** - Interface sobre et fonctionnelle
3. **Securite & confidentialite** - Pas de cloud, pas de compte, pas de collecte
4. **Universalite** - Adapte a tous les cas d'usage familiaux
5. **Aucune barriere economique** - Entierement gratuit
