package com.prox.deals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prox.deals.data.Product

/**
 * One deal in the list. Tapping the card opens the detail screen; tapping the
 * heart saves/unsaves it. `onClick` and `onToggleSave` are passed in so this
 * component doesn't need to know anything about navigation or the ViewModel —
 * it just reports taps upward. That's a common, beginner-friendly Compose pattern.
 */
@Composable
fun DealCard(
    product: Product,
    isSaved: Boolean,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier,
    // Optional price to show instead of product.price. The Saved screen passes
    // $0.00 for a free item once its partner is saved. Defaults to the normal
    // price so every other caller behaves exactly as before.
    priceOverride: Double? = null
) {
    // The price this card actually displays.
    val shownPrice = priceOverride ?: product.price
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder: a tinted square with the product emoji.
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 30.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Badges row: a deal can carry the Best Deal badge, the FREE
                // badge (any free-bundle item), or both.
                if (product.isBestDeal || product.isFreeDeal) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (product.isBestDeal) BestDealBadge()
                        if (product.isFreeDeal) FreeBadge()
                    }
                    Spacer(Modifier.padding(top = 4.dp))
                }
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "${product.retailer} · ${product.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.padding(top = 4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "$%.2f".format(shownPrice),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Struck-through original only when the shown price is lower.
                    if (shownPrice < product.originalPrice) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "$%.2f".format(product.originalPrice),
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                }
                // "You save $X" label, based on the shown price.
                val shownSavings = product.originalPrice - shownPrice
                if (shownSavings > 0) {
                    Spacer(Modifier.padding(top = 4.dp))
                    val percent = ((shownSavings / product.originalPrice) * 100).toInt()
                    SavingsLabel(shownSavings, percent)
                }
                // Promotion explanation, shown on free-bundle cards so the user
                // sees the offer ("Free when purchased with …") right in the list.
                if (product.isFreeDeal && product.promotionDescription.isNotBlank()) {
                    Spacer(Modifier.padding(top = 4.dp))
                    Text(
                        product.promotionDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Save / unsave heart button.
            IconButton(onClick = onToggleSave) {
                Icon(
                    imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isSaved) "Remove from saved" else "Save deal",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** Search box used at the top of the Deals screen. */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text("Search products or retailers") },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}
