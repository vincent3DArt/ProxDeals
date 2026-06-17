package com.prox.deals.data

import kotlinx.coroutines.delay

/**
 * Pretends to be a server. There is no real backend — everything lives in this
 * list. `loadDeals()` waits half a second so we can show a loading spinner,
 * just like a real network call would.
 *
 * `object` in Kotlin means "a single shared instance" (a singleton). We never
 * create more than one MockRepository, so `object` is simpler than a class here.
 */
object MockRepository {

    /** The full catalogue of deals. */
    private val allProducts = listOf(
        Product(1, "Whole Milk", "FreshMart", 2.49, 3.49, "1L", "🥛", true,
            "Lowest price in 30 days and 29% below the usual shelf price."),
        Product(2, "Free-Range Eggs", "GreenGrocer", 3.99, 5.49, "12 pack", "🥚", true,
            "Cheaper per egg than buying the 6-pack, and a top-rated brand."),
        Product(3, "Bananas", "FreshMart", 1.29, 1.79, "1kg", "🍌", false,
            "Solid everyday saving on a staple you'll likely buy anyway."),
        Product(4, "Cheddar Cheese", "ValueFoods", 4.50, 6.00, "500g", "🧀", true,
            "25% off a block that normally never goes on sale here."),
        Product(5, "Orange Juice", "GreenGrocer", 2.99, 3.99, "1.5L", "🧃", false,
            "Big bottle for the price of the small one this week."),
        Product(6, "Sourdough Bread", "ValueFoods", 2.20, 3.20, "450g", "🍞", false,
            "Bakery-fresh loaf marked down at a great per-slice cost."),
        Product(7, "Greek Yogurt", "FreshMart", 3.10, 4.10, "500g", "🥄", false,
            "High-protein tub at its best price across all retailers today."),
        Product(8, "Chicken Breast", "ValueFoods", 5.99, 7.99, "500g", "🍗", true,
            "Lean protein at $2 off — the cheapest of the three retailers."),
        Product(9, "Apples", "GreenGrocer", 2.00, 2.80, "1kg", "🍎", false,
            "In-season pricing makes this a reliable weekly buy."),
        Product(10, "Pasta", "ValueFoods", 0.99, 1.49, "500g", "🍝", false,
            "Pantry staple under a dollar — stock up while it lasts."),
        Product(11, "Tomato Sauce", "FreshMart", 1.10, 1.60, "400g", "🥫", false,
            "Pairs with the pasta deal for a cheap dinner combo."),
        Product(12, "Coffee Beans", "GreenGrocer", 6.99, 9.49, "250g", "☕", true,
            "Premium beans at 26% off — rarely discounted this much.")
    )

    /** Every retailer name, for the filter chips. Sorted and de-duplicated. */
    val retailers: List<String> = allProducts.map { it.retailer }.distinct().sorted()

    /**
     * Simulates fetching deals from a server.
     *
     * `suspend` marks a function that can pause without blocking the screen.
     * `delay(500)` waits 500ms. We also randomly fail ~1 in 6 times so you can
     * see the error state — set FORCE_ERROR below to control it during a demo.
     */
    suspend fun loadDeals(): List<Product> {
        delay(500) // fake network latency so the loading state is visible
        if (FORCE_ERROR) throw RuntimeException("Couldn't reach the deals service")
        return allProducts
    }

    /** Flip to true to always show the error state (handy for the demo video). */
    var FORCE_ERROR = false
}
