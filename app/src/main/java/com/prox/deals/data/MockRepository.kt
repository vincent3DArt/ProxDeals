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
        // --- Regular deals ---------------------------------------------------
        Product(1, "Pasta", "ValueFoods", 0.99, 1.49, "500g", "🍝", false,
            "Pantry staple under a dollar — stock up while it lasts."),
        Product(2, "Tomato Sauce", "FreshMart", 0.99, 1.60, "400g", "🥫", false,
            "Pairs with the pasta deal for a cheap dinner combo."),
        Product(3, "White Rice", "ValueFoods", 1.49, 2.20, "1kg", "🍚", false,
            "Bulk bag at the lowest price per kilo this week."),
        Product(4, "Apples", "GreenGrocer", 2.00, 2.80, "1kg", "🍎", false,
            "In-season pricing makes this a reliable weekly buy."),
        Product(5, "Whole Milk", "FreshMart", 2.00, 3.49, "1L", "🥛", false,
            "Everyday fresh milk marked down this week."),
        Product(6, "Greek Yogurt", "FreshMart", 3.25, 4.10, "500g", "🥄", true,
            "High-protein tub at its best price across all retailers today."),
        Product(7, "Sourdough Bread", "ValueFoods", 3.25, 4.00, "450g", "🍞", false,
            "Bakery-fresh loaf at a great per-slice cost."),
        Product(8, "Cheddar Cheese", "ValueFoods", 4.50, 6.00, "500g", "🧀", true,
            "25% off a block that normally never goes on sale here."),
        Product(9, "Orange Juice", "GreenGrocer", 4.50, 5.50, "1.5L", "🧃", false,
            "Big bottle for the price of the small one this week."),
        Product(10, "Free-Range Eggs", "GreenGrocer", 5.99, 7.49, "12 pack", "🥚", true,
            "Cheaper per egg than buying the 6-pack, and a top-rated brand."),
        Product(11, "Chicken Breast", "ValueFoods", 5.99, 7.99, "500g", "🍗", false,
            "Lean protein at $2 off — the cheapest of the three retailers."),
        Product(12, "Coffee Beans", "GreenGrocer", 7.49, 9.49, "250g", "☕", true,
            "Premium beans at 21% off — rarely discounted this much."),
        Product(13, "Cereal", "FreshMart", 7.49, 8.49, "750g", "🥣", false,
            "Family-size box and a breakfast favourite."),
        Product(14, "Salmon Fillet", "GreenGrocer", 8.99, 11.00, "400g", "🐟", true,
            "Fresh fillet at its lowest price this month."),
        Product(15, "Olive Oil", "ValueFoods", 8.99, 10.50, "750ml", "🫒", false,
            "Extra-virgin oil at a strong discount."),
        Product(16, "Ground Beef", "ValueFoods", 10.00, 12.00, "1kg", "🥩", false,
            "Family pack of lean ground beef on special."),
        Product(17, "Ribeye Steak", "GreenGrocer", 14.99, 19.99, "500g", "🥩", true,
            "Premium cut at a noticeable discount this weekend."),

        // --- Free-bundle deals ----------------------------------------------
        // These are NORMAL products shown at their usual price, but flagged as
        // free-bundle deals: they carry a FREE badge and the promo text, and
        // show up when the "Free Deals" filter is on. The price stays at the
        // original price (e.g. Bananas $2.00) — the item only becomes free in
        // the basket when its required partner item is also bought.
        Product(18, "Bananas", "FreshMart", 2.00, 2.00, "1kg", "🍌", false,
            "Free when purchased with Greek Yogurt",
            isFreeDeal = true,
            requiredPurchase = "Greek Yogurt",
            promotionDescription = "Free when purchased with Greek Yogurt"),
        Product(19, "Strawberry Jam", "ValueFoods", 4.49, 4.49, "340g", "🍓", false,
            "Free when purchased with Sourdough Bread",
            isFreeDeal = true,
            requiredPurchase = "Sourdough Bread",
            promotionDescription = "Free when purchased with Sourdough Bread"),
        Product(20, "BBQ Sauce", "ValueFoods", 3.99, 3.99, "500ml", "🍖", false,
            "Free when purchased with Chicken Breast",
            isFreeDeal = true,
            requiredPurchase = "Chicken Breast",
            promotionDescription = "Free when purchased with Chicken Breast"),
        Product(21, "Almond Milk", "FreshMart", 3.79, 3.79, "1L", "🥛", false,
            "Free when purchased with Cereal",
            isFreeDeal = true,
            requiredPurchase = "Cereal",
            promotionDescription = "Free when purchased with Cereal")
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
