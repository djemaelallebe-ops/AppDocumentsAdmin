package com.mespapiers.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            // Page indicator
            PageIndicator(
                currentPage = currentPage,
                totalPages = 6,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                modifier = Modifier.weight(1f),
                label = "onboarding_content"
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> StoragePage()
                    2 -> AddDocumentsPage()
                    3 -> ExportPage()
                    4 -> UnlimitedPage()
                    5 -> CreateProfilePage(
                        profileName = profileName,
                        onNameChange = viewModel::updateProfileName,
                        isLoading = isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage > 0) {
                    TextButton(onClick = viewModel::previousPage) {
                        Text("Retour")
                    }
                } else {
                    Spacer(modifier = Modifier.size(80.dp))
                }

                if (currentPage < 5) {
                    Button(
                        onClick = viewModel::nextPage,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = when (currentPage) {
                                0 -> "Continuer"
                                1 -> "OK"
                                2 -> "Compris"
                                3 -> "Super, continuer"
                                else -> "Continuer"
                            }
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.completeOnboarding(onComplete) },
                        enabled = profileName.isNotBlank() && !isLoading,
                        modifier = Modifier.height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Creer le profil")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    icon: ImageVector,
    title: String,
    description: String,
    subtitle: String? = null,
    bulletPoints: List<String>? = null,
    note: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        subtitle?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        bulletPoints?.let { points ->
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                points.forEach { point ->
                    Text(
                        text = "• $point",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        note?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WelcomePage() {
    OnboardingPageContent(
        icon = Icons.Outlined.Description,
        title = "Bienvenue dans Mes Papiers",
        description = "Une application simple et securisee pour organiser vos documents administratifs.",
        subtitle = "Vos documents sont stockes uniquement sur votre telephone."
    )
}

@Composable
private fun StoragePage() {
    OnboardingPageContent(
        icon = Icons.Outlined.CloudOff,
        title = "Stockage local",
        description = "Mes Papiers ne cree aucun compte et ne synchronise rien sur internet.",
        subtitle = "Tous vos documents sont enregistres dans un espace securise, local a votre appareil."
    )
}

@Composable
private fun AddDocumentsPage() {
    OnboardingPageContent(
        icon = Icons.Outlined.Description,
        title = "Ajouter des documents",
        description = "Vous pouvez :",
        bulletPoints = listOf(
            "Scanner un document avec l'appareil photo (conversion en PDF)",
            "Importer un PDF deja present sur votre telephone"
        ),
        note = "Les documents sont organises en dossiers (Identite, Factures, Revenus...)."
    )
}

@Composable
private fun ExportPage() {
    OnboardingPageContent(
        icon = Icons.Outlined.FolderZip,
        title = "Export ZIP",
        description = "Vous pouvez creer un dossier ZIP avec les documents de votre choix :",
        subtitle = "Carte d'identite, Facture d'electricite, Assurance habitation, etc.",
        note = "Le ZIP est pret a etre partage par email, messagerie ou autre. Il ne quitte votre telephone que si vous choisissez vous-meme de le partager."
    )
}

@Composable
private fun UnlimitedPage() {
    OnboardingPageContent(
        icon = Icons.Outlined.Group,
        title = "Sans limites, sans compte",
        description = "Vous pouvez creer autant de profils que necessaire : vous-meme, vos enfants, un parent, un proche...",
        subtitle = "Il n'y a aucune limite, aucun compte, aucun mode payant."
    )
}

@Composable
private fun CreateProfilePage(
    profileName: String,
    onNameChange: (String) -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Creation du profil",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Donnez un nom a votre premier profil",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = profileName,
            onValueChange = onNameChange,
            label = { Text("Nom du profil") },
            placeholder = { Text("Moi") },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
