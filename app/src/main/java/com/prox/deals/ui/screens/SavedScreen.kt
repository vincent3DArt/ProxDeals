package com.prox.deals.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prox.deals.DealsViewModel
import com.prox.deals.ui.components.DealCard
import com.prox.deals.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    vm: DealsViewModel,
    onDealClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved deals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        val saved = vm.savedDeals

        if (saved.isEmpty()) {
            EmptyState(
                emoji = "💚",
                title = "No saved deals yet",
                message = "Tap the heart on any deal to keep it here for later.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(saved, key = { it.id }) { product ->
                    DealCard(
                        product = product,
                        isSaved = true,
                        onClick = { onDealClick(product.id) },
                        onToggleSave = { vm.toggleSaved(product.id) },
                        // Free items drop to $0.00 once their required partner
                        // is also saved (e.g. Bananas free when Greek Yogurt
                        // is saved). Otherwise they show their normal price.
                        priceOverride = vm.savedDisplayPrice(product)
                    )
                }
            }
        }
    }
}
