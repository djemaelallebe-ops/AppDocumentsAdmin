package com.mespapiers.app.ui.screens.document

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mespapiers.app.domain.model.DocumentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentInfoScreen(
    documentId: String,
    viewModel: DocumentInfoViewModel = hiltViewModel(),
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val document by viewModel.document.collectAsState()
    val title by viewModel.title.collectAsState()
    val documentType by viewModel.documentType.collectAsState()
    val periodMonth by viewModel.periodMonth.collectAsState()
    val periodYear by viewModel.periodYear.collectAsState()
    val expiryDate by viewModel.expiryDate.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informations du document") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Nom du document") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Document type dropdown
            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it }
            ) {
                OutlinedTextField(
                    value = documentType?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type de document") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    DocumentType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                viewModel.updateDocumentType(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Show period fields for bills/payslips
            if (documentType?.let { it == DocumentType.ELECTRICITY_BILL ||
                        it == DocumentType.GAS_BILL ||
                        it == DocumentType.WATER_BILL ||
                        it == DocumentType.INTERNET_BILL ||
                        it == DocumentType.RENT_RECEIPT ||
                        it == DocumentType.PAYSLIP } == true) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Periode",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Month dropdown
                var monthExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = monthExpanded,
                    onExpandedChange = { monthExpanded = it }
                ) {
                    OutlinedTextField(
                        value = periodMonth?.let { getMonthName(it) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mois") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = monthExpanded,
                        onDismissRequest = { monthExpanded = false }
                    ) {
                        (1..12).forEach { month ->
                            DropdownMenuItem(
                                text = { Text(getMonthName(month)) },
                                onClick = {
                                    viewModel.updatePeriodMonth(month)
                                    monthExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Year field
                OutlinedTextField(
                    value = periodYear?.toString() ?: "",
                    onValueChange = { viewModel.updatePeriodYear(it.toIntOrNull()) },
                    label = { Text("Annee") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Show year field for tax notices
            if (documentType == DocumentType.TAX_NOTICE) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = periodYear?.toString() ?: "",
                    onValueChange = { viewModel.updatePeriodYear(it.toIntOrNull()) },
                    label = { Text("Annee fiscale") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Show expiry date for identity documents
            if (documentType?.let { it == DocumentType.IDENTITY_CARD ||
                        it == DocumentType.PASSPORT ||
                        it == DocumentType.DRIVING_LICENSE } == true) {

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = expiryDate ?: "",
                    onValueChange = viewModel::updateExpiryDate,
                    label = { Text("Date d'expiration (JJ/MM/AAAA)") },
                    placeholder = { Text("31/12/2030") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveDocument(onSaved) },
                enabled = title.isNotBlank() && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(if (isSaving) "Enregistrement..." else "Enregistrer")
            }
        }
    }
}

private fun getMonthName(month: Int): String = when (month) {
    1 -> "Janvier"
    2 -> "Fevrier"
    3 -> "Mars"
    4 -> "Avril"
    5 -> "Mai"
    6 -> "Juin"
    7 -> "Juillet"
    8 -> "Aout"
    9 -> "Septembre"
    10 -> "Octobre"
    11 -> "Novembre"
    12 -> "Decembre"
    else -> ""
}
