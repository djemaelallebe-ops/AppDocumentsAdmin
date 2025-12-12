package com.mespapiers.app.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mespapiers.app.domain.model.Category
import com.mespapiers.app.domain.model.CategoryType
import com.mespapiers.app.domain.model.Document
import com.mespapiers.app.domain.model.DocumentState
import com.mespapiers.app.ui.components.EmptyScreen
import com.mespapiers.app.ui.components.LoadingScreen
import com.mespapiers.app.ui.components.MesPapiersSnackbarHost
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

    val snackbarHostState = remember { SnackbarHostState() }

    // Support reminder dialog
    if (reminderState.showSupportReminder) {
        SupportReminderDialog(
            onSupport = {
                viewModel.dismissSupportReminder()
                onSettingsClick()
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
                        AnimatedVisibility(
                            visible = currentProfile != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            currentProfile?.let {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { MesPapiersSnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingScreen(message = "Chargement...")
                }

                categoriesWithDocuments.isEmpty() -> {
                    EmptyScreen(
                        icon = Icons.Outlined.Description,
                        title = "Aucune categorie",
                        subtitle = "Creez un profil pour commencer"
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(
                            items = categoriesWithDocuments,
                            key = { _, item -> item.category.id }
                        ) { _, categoryWithDocs ->
                            CategoryCard(
                                category = categoryWithDocs.category,
                                documents = categoryWithDocs.documents,
                                onDocumentClick = onDocumentClick,
                                onAddDocument = { onAddDocument(categoryWithDocs.category.id) }
                            )
                        }

                        // Bottom spacer for FAB
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
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
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (documents.size) {
                            0 -> "Aucun document"
                            1 -> "1 document"
                            else -> "${documents.size} documents"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    onClick = onAddDocument,
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter un document",
                            tint = categoryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Documents list
            AnimatedVisibility(visible = documents.isNotEmpty()) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = documents,
                            key = { it.id }
                        ) { document ->
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
}

@Composable
private fun DocumentTile(
    document: Document,
    categoryColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = categoryColor
                        )
                    }
                }

                // Expiry indicator
                if (document.state != DocumentState.ACTIVE) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(16.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = when (document.state) {
                            DocumentState.EXPIRED -> MaterialTheme.colorScheme.error
                            DocumentState.EXPIRING_SOON -> Color(0xFFFF9800)
                            else -> Color.Transparent
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = document.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Show period if available
            document.periodMonth?.let { month ->
                document.periodYear?.let { year ->
                    Text(
                        text = "${getMonthShort(month)} $year",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
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
        title = {
            Text(
                "Merci d'utiliser Mes Papiers",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                "L'application est gratuite et le restera toujours. Si elle vous aide a gerer vos documents, vous pouvez la soutenir en laissant un avis ou un petit pourboire.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onSupport) {
                Text("Soutenir")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onNever) {
                    Text("Ne plus rappeler")
                }
                TextButton(onClick = onLater) {
                    Text("Plus tard")
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
        title = {
            Text(
                "Mes Papiers vous rend service ?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                "Vous pouvez nous aider en laissant une note sur Google Play. Cela aide d'autres personnes a decouvrir l'application.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onRate) {
                Text("Laisser une note")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onNever) {
                    Text("Ne plus afficher")
                }
                TextButton(onClick = onLater) {
                    Text("Plus tard")
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

private fun getMonthShort(month: Int): String = when (month) {
    1 -> "Jan"
    2 -> "Fev"
    3 -> "Mar"
    4 -> "Avr"
    5 -> "Mai"
    6 -> "Juin"
    7 -> "Juil"
    8 -> "Aout"
    9 -> "Sep"
    10 -> "Oct"
    11 -> "Nov"
    12 -> "Dec"
    else -> ""
}
