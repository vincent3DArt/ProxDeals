package com.prox.deals.data

/**
 * A single grocery deal.
 *
 * In Kotlin, a `data class` is like a Java class where equals(), hashCode(),
 * toString() and a copy() method are all generated for you automatically.
 * We use `val` (read-only, like `final` in Java) for every field because a
 * product never changes once created.
 */
data class Product(
    val id: Int,
    val name: String,
    val retailer: String,
    val price: Double,        // current deal price
    val originalPrice: Double, // normal price (used to compute savings)
    val size: String,         // e.g. "500g", "1L", "12 pack"
    val emoji: String,        // simple stand-in for a product image
    val isBestDeal: Boolean,  // shows the "Best Deal" badge
    val reason: String,       // short text explaining WHY the deal is good

    // --- Free-bundle deal fields ---------------------------------------------
    // These have default values, so existing products that don't pass them in
    // still work. In Kotlin a parameter with `= ...` is optional, like an
    // overloaded constructor in Java but without writing extra constructors.
    val isFreeDeal: Boolean = false,        // true when the item is free with a bundle
    val requiredPurchase: String = "",      // the item you must buy, e.g. "Greek Yogurt"
    val promotionDescription: String = ""   // full promo text, e.g. "Free when purchased with Greek Yogurt"
) {
    /** How many dollars you save vs. the original price. */
    val savings: Double
        get() = originalPrice - price

    /** Savings as a whole-number percentage, e.g. 25. */
    val savingsPercent: Int
        get() = if (originalPrice > 0)
            (((originalPrice - price) / originalPrice) * 100).toInt()
        else 0
}
