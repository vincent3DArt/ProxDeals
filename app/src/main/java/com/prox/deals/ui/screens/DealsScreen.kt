package com.prox.deals.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.prox.deals.LoadState
import com.prox.deals.PriceSort
import com.prox.deals.ui.components.DealCard
import com.prox.deals.ui.components.EmptyState
import com.prox.deals.ui.components.ErrorState
import com.prox.deals.ui.components.LoadingState
import com.prox.deals.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DealsScreen(
    vm: DealsViewModel,
    onDealClick: (Int) -> Unit,
    onSavedClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prox Deals", fontWeight = FontWeight.Bold) },
                actions = {
                    // Badge-style count of saved items next to the heart.
                    IconButton(onClick = onSavedClick) {
                        Icon(Icons.Filled.Favorite, contentDescription = "Saved deals")
                    }
                    if (vm.savedIds.isNotEmpty()) {
                        Text(
                            "${vm.savedIds.size}",
                            modifier = Modifier.padding(end = 12.dp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // --- Search + filters (always visible) ---------------------------
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.padding(top = 8.dp))
                SearchBar(query = vm.searchQuery, onQueryChange = vm::onSearchChange)
                Spacer(Modifier.padding(top = 8.dp))

                // Retailer filter chips. FlowRow lays them out left-to-right and
                // automatically wraps to a new line when they run out of width —
                // so on a narrow phone they stack onto more rows, and on a wide
                // tablet they spread across fewer rows. This is the responsive
                // part: the layout reshapes itself to the available screen size.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = vm.selectedRetailer == null,
                        onClick = { vm.onRetailerSelected(null) },
                        label = { Text("All") }
                    )
                    vm.retailers.forEach { retailer ->
                        FilterChip(
                            selected = vm.selectedRetailer == retailer,
                            onClick = { vm.onRetailerSelected(retailer) },
                            label = { Text(retailer) }
                        )
                    }
                }

                Spacer(Modifier.padding(top = 4.dp))

                // Price sort chips + clear filters, also wrapping with the width.
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FilterChip(
                        selected = vm.priceSort == PriceSort.LowToHigh,
                        onClick = { vm.onPriceSortChange(PriceSort.LowToHigh) },
                        label = { Text("Price ↑") }
                    )
                    FilterChip(
                        selected = vm.priceSort == PriceSort.HighToLow,
                        onClick = { vm.onPriceSortChange(PriceSort.HighToLow) },
                        label = { Text("Price ↓") }
                    )
                    AssistChip(
                        onClick = { vm.clearFilters() },
                        label = { Text("Clear") }
                    )
                }
            }

            // --- Body switches on the load state -----------------------------
            when (vm.loadState) {
                LoadState.Loading -> LoadingState()
                LoadState.Error -> ErrorState(onRetry = { vm.loadDeals() })
                LoadState.Success -> {
                    val deals = vm.visibleDeals
                    if (deals.isEmpty()) {
                        EmptyState(
                            emoji = "🔍",
                            title = "No deals match",
                            message = "Try a different search or clear your filters."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(deals, key = { it.id }) { product ->
                                DealCard(
                                    product = product,
                                    isSaved = vm.isSaved(product.id),
                                    onClick = { onDealClick(product.id) },
                                    onToggleSave = { vm.toggleSaved(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}