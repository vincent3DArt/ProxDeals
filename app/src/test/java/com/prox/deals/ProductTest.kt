package com.prox.deals

import com.prox.deals.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A small, beginner-level unit test. It checks the savings math on the Product
 * model — pure logic, no Android needed, so it runs fast on the JVM.
 */
class ProductTest {

    private fun sample(price: Double, original: Double) = Product(
        id = 1, name = "Test", retailer = "TestMart",
        price = price, originalPrice = original, size = "1L",
        emoji = "🧪", isBestDeal = false, reason = "test"
    )

    @Test
    fun savings_isPriceDifference() {
        val p = sample(price = 2.0, original = 3.0)
        assertEquals(1.0, p.savings, 0.001)
    }

    @Test
    fun savingsPercent_isRoundedDown() {
        val p = sample(price = 75.0, original = 100.0)
        assertEquals(25, p.savingsPercent)
    }

    @Test
    fun savingsPercent_handlesZeroOriginal() {
        val p = sample(price = 5.0, original = 0.0)
        assertEquals(0, p.savingsPercent)
    }
}
