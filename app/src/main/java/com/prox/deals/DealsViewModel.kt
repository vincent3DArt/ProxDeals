package com.prox.deals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prox.deals.data.MockRepository
import com.prox.deals.data.Product
import kotlinx.coroutines.launch

/** The three things the deals list can be doing at any moment. */
enum class LoadState { Loading, Success, Error }

/** Which way the user wants the list sorted by price. */
enum class PriceSort { None, LowToHigh, HighToLow }

/**
 * One ViewModel shared by every screen. This is the "single source of truth"
 * for the whole app, which keeps state management simple — no database, no
 * dependency injection, no repository interfaces. A ViewModel survives screen
 * rotation, so saved deals and filters don't reset when you turn the phone.
 *
 * Each piece of state is a Compose `mutableStateOf`. When you assign a new
 * value, any screen reading it redraws automatically. That's the whole trick.
 */
class DealsViewModel : ViewModel() {

    // --- Backing state -------------------------------------------------------

    var loadState by mutableStateOf(LoadState.Loading)
        private set

    /** The full list returned by the "server". */
    private var allDeals by mutableStateOf<List<Product>>(emptyList())

    /** Text typed into the search box. */
    var searchQuery by mutableStateOf("")
        private set

    /** Currently selected retailer filter, or null for "All". */
    var selectedRetailer by mutableStateOf<String?>(null)
        private set

    /** Current price sort choice. */
    var priceSort by mutableStateOf(PriceSort.None)
        private set

    /** When true, the list shows only free-bundle deals. */
    var showFreeDealsOnly by mutableStateOf(false)
        private set

    /** IDs of deals the user has saved. A Set gives fast "is this saved?" checks. */
    var savedIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    val retailers: List<String> get() = MockRepository.retailers

    // --- Derived (computed) lists -------------------------------------------

    /** The deals to show after search + retailer + free-deals filter + sort. */
    val visibleDeals: List<Product>
        get() {
            var list = allDeals

            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.retailer.contains(searchQuery, ignoreCase = true)
                }
            }
            if (selectedRetailer != null) {
                list = list.filter { it.retailer == selectedRetailer }
            }
            // Free Deals filter: when on, keep only free-bundle items.
            if (showFreeDealsOnly) {
                list = list.filter { it.isFreeDeal }
            }
            // Sort by the displayed price (free items show at their original
            // price, so they sort naturally alongside everything else).
            list = when (priceSort) {
                PriceSort.LowToHigh -> list.sortedBy { it.price }
                PriceSort.HighToLow -> list.sortedByDescending { it.price }
                PriceSort.None -> list
            }
            return list
        }

    /** The full Product objects the user has saved. */
    val savedDeals: List<Product>
        get() = allDeals.filter { savedIds.contains(it.id) }

    /**
     * True when a free-bundle deal's required partner item is ALSO saved.
     * e.g. Bananas (free with Greek Yogurt) → true once Greek Yogurt is saved.
     */
    fun isFreeDealUnlocked(product: Product): Boolean {
        if (!product.isFreeDeal) return false
        return savedDeals.any { it.name.equals(product.requiredPurchase, ignoreCase = true) }
    }

    /**
     * The price to show in the SAVED screen. A free-bundle item drops to $0.00
     * once its required partner is also saved; otherwise it shows its normal
     * price. Regular products always show their normal price.
     */
    fun savedDisplayPrice(product: Product): Double {
        return if (isFreeDealUnlocked(product)) 0.0 else product.price
    }

    // --- Actions the UI can call --------------------------------------------

    init {
        loadDeals() // load as soon as the ViewModel is created
    }

    fun loadDeals() {
        loadState = LoadState.Loading
        // viewModelScope.launch runs the suspend function off the main thread.
        viewModelScope.launch {
            try {
                allDeals = MockRepository.loadDeals()
                loadState = LoadState.Success
            } catch (e: Exception) {
                loadState = LoadState.Error
            }
        }
    }

    fun onSearchChange(text: String) { searchQuery = text }

    fun onRetailerSelected(retailer: String?) { selectedRetailer = retailer }

    fun onPriceSortChange(sort: PriceSort) { priceSort = sort }

    /** Toggle the "Free Deals only" filter on/off. */
    fun toggleFreeDealsOnly() { showFreeDealsOnly = !showFreeDealsOnly }

    fun clearFilters() {
        selectedRetailer = null
        priceSort = PriceSort.None
        searchQuery = ""
        showFreeDealsOnly = false  // turn the Free Deals filter back off
    }

    /** Add or remove a deal from saved. Returns nothing — state updates the UI. */
    fun toggleSaved(id: Int) {
        savedIds = if (savedIds.contains(id)) savedIds - id else savedIds + id
    }

    fun isSaved(id: Int): Boolean = savedIds.contains(id)

    /** Find one product by id (used by the detail screen). */
    fun productById(id: Int): Product? = allDeals.firstOrNull { it.id == id }
}
