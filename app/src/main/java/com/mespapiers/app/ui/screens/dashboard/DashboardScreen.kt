package com.mespapiers.app.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.CategoryType
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentState
import com.mespapiers.app.ui.theme.CategoryHealth
import com.mespapiers.app.ui.theme.CategoryHousing
import com.mespapiers.app.ui.theme.CategoryIdentity
import com.mespapiers.app.ui.theme.CategoryIncome
import com.mespapiers.app.ui.theme.CategoryOther

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onDocumentClick: (String) -> Unit,
    onAddDocument: (String) -> Unit,
    onExportClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSwitchProfile: () -> Unit
) {
    val currentProfile by viewModel.currentProfile.collectAsState()
    val categoriesWithDocuments by viewModel.categoriesWithDocuments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val reminderState by viewModel.reminderState.collectAsState()

    // Support reminder dialog
    if (reminderState.showSupportReminder) {
        SupportReminderDialog(
            onSupport = {
                viewModel.dismissSupportReminder()
                onSettingsClick() // Navigate to support screen
            },
            onLater = viewModel::dismissSupportReminder,
            onNever = viewModel::disableSupportReminder
        )
    }

    // Rating reminder dialog
    if (reminderState.showRatingReminder) {
        RatingReminderDialog(
            onRate = {
                viewModel.markAsRated()
                // TODO: Open Play Store
            },
            onLater = viewModel::dismissRatingReminder,
            onNever = viewModel::disableRatingReminder
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mes documents",
                            style = MaterialTheme.typography.titleLarge
                        )
                        currentProfile?.let {
                            Text(
                                text = it.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSwitchProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Changer de profil")
                    }
                    IconButton(onClick = onExportClick) {
                        Icon(Icons.Default.FolderZip, contentDescription = "Exporter")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Parametres")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categoriesWithDocuments) { categoryWithDocs ->
                    CategoryCard(
                        category = categoryWithDocs.category,
                        documents = categoryWithDocs.documents,
                        onDocumentClick = onDocumentClick,
                        onAddDocument = { onAddDocument(categoryWithDocs.category.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    documents: List<Document>,
    onDocumentClick: (String) -> Unit,
    onAddDocument: () -> Unit
) {
    val categoryColor = getCategoryColor(category.type)
    val categoryIcon = getCategoryIcon(category.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Category header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (documents.isEmpty()) "Aucun document"
                        else "${documents.size} document(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onAddDocument) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ajouter",
                        tint = categoryColor
                    )
                }
            }

            if (documents.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                // Documents grid
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(documents) { document ->
                        DocumentTile(
                            document = document,
                            categoryColor = categoryColor,
                            onClick = { onDocumentClick(document.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentTile(
    document: Document,
    categoryColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = categoryColor
                )

                // Expiry indicator
                if (document.state != DocumentState.ACTIVE) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp),
                        shape = MaterialTheme.shapes.small,
                        color = when (document.state) {
                            DocumentState.EXPIRED -> MaterialTheme.colorScheme.error
                            DocumentState.EXPIRING_SOON -> Color(0xFFFF9800)
                            else -> Color.Transparent
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = when (document.state) {
                                DocumentState.EXPIRED -> "Expire"
                                DocumentState.EXPIRING_SOON -> "Expire bientot"
                                else -> null
                            },
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = document.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SupportReminderDialog(
    onSupport: () -> Unit,
    onLater: () -> Unit,
    onNever: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onLater,
        title = { Text("Merci d'utiliser Mes Papiers") },
        text = {
            Text("L'application est gratuite et le restera toujours. Si elle vous aide a gerer vos documents, vous pouvez la soutenir en laissant un avis ou un petit pourboire.")
        },
        confirmButton = {
            Button(onClick = onSupport) {
                Text("Soutenir")
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onLater) {
                    Text("Plus tard")
                }
                TextButton(onClick = onNever) {
                    Text("Ne plus me le rappeler")
                }
            }
        }
    )
}

@Composable
private fun RatingReminderDialog(
    onRate: () -> Unit,
    onLater: () -> Unit,
    onNever: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onLater,
        title = { Text("Mes Papiers vous rend service ?") },
        text = {
            Text("Vous pouvez nous aider en laissant une note sur Google Play. Cela aide d'autres personnes a decouvrir l'application.")
        },
        confirmButton = {
            Button(onClick = onRate) {
                Text("Laisser une note")
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onLater) {
                    Text("Plus tard")
                }
                TextButton(onClick = onNever) {
                    Text("Ne plus afficher")
                }
            }
        }
    )
}

private fun getCategoryColor(type: CategoryType): Color = when (type) {
    CategoryType.IDENTITY -> CategoryIdentity
    CategoryType.FAMILY_HEALTH -> CategoryHealth
    CategoryType.HOUSING_BILLS -> CategoryHousing
    CategoryType.INCOME_TAX -> CategoryIncome
    CategoryType.OTHER -> CategoryOther
}

private fun getCategoryIcon(type: CategoryType): ImageVector = when (type) {
    CategoryType.IDENTITY -> Icons.Outlined.Badge
    CategoryType.FAMILY_HEALTH -> Icons.Outlined.FamilyRestroom
    CategoryType.HOUSING_BILLS -> Icons.Outlined.Home
    CategoryType.INCOME_TAX -> Icons.Outlined.Payments
    CategoryType.OTHER -> Icons.Outlined.Folder
}
