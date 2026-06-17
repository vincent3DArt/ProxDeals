package com.prox.deals.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prox.deals.DealsViewModel
import com.prox.deals.ui.components.BestDealBadge
import com.prox.deals.ui.components.SavingsLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    productId: Int,
    vm: DealsViewModel,
    onBack: () -> Unit
) {
    val product = vm.productById(productId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deal details", fontWeight = FontWeight.Bold) },
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
        // Defensive: if the id isn't found (e.g. data reloaded), show a message.
        if (product == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("This deal is no longer available.")
            }
            return@Scaffold
        }

        val isSaved = vm.isSaved(product.id)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Large image placeholder.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 80.sp)
            }

            Spacer(Modifier.height(16.dp))

            if (product.isBestDeal) {
                BestDealBadge()
                Spacer(Modifier.height(8.dp))
            }

            Text(product.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                "${product.retailer} · ${product.size}",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$%.2f".format(product.price),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "$%.2f".format(product.originalPrice),
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            SavingsLabel(product.savings, product.savingsPercent)

            Spacer(Modifier.height(20.dp))

            // "Why this is a good deal" explainer card.
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Why this is a good deal",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(product.reason, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Primary call-to-action: save / unsave.
            Button(
                onClick = { vm.toggleSaved(product.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(if (isSaved) "Saved — tap to remove" else "Save this deal")
            }
        }
    }
}
