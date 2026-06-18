# Prox Deals — Grocery Search & Deals (Track A)

A mobile-first Android prototype for searching grocery products, browsing deals,
and saving the best ones — including **free-bundle deals** ("free when you buy
X"). Built for the Prox Mobile UI/UX Implementation assessment. Uses local mock
data only — no backend.

---

## Tech stack and why

| Choice | Reason |
|---|---|
| **Kotlin** | The modern, default language for Android. Concise and null-safe. |
| **Jetpack Compose** | Declarative UI — you describe what the screen should look like for a given state, and it redraws automatically when state changes. Far less boilerplate than the old XML + View system. |
| **Material 3** | Google's current design system. Gives accessible, consistent components (cards, chips, top bars) and free light/dark theming. |
| **ViewModel + `mutableStateOf`** | The simplest state management that still survives screen rotation. No database, no dependency injection, no architecture frameworks — appropriate for a prototype. |
| **Navigation-Compose** | Standard, lightweight way to move between the three screens. |
| **Local mock data** | The brief requires no backend. `MockRepository` fakes a network call with a 0.5s delay so loading/error states are real. |

---

## Component structure

```
com.prox.deals
├── MainActivity.kt          Single activity; sets the theme and hosts navigation
├── ProxNavHost.kt           Routes + creates the one shared ViewModel
├── DealsViewModel.kt        ALL app state: deals, search, filters, saved IDs
│
├── data/
│   ├── Product.kt           Data model (+ savings, free-deal fields)
│   └── MockRepository.kt    Fake "server" with the product catalogue
│
└── ui/
    ├── theme/
    │   ├── Color.kt         Brand palette (market green + savings amber)
    │   └── Theme.kt         Material 3 color scheme + typography scale
    ├── components/
    │   ├── CommonComponents.kt   LoadingState, EmptyState, ErrorState,
    │   │                         BestDealBadge, FreeBadge, SavingsLabel
    │   └── DealCard.kt           Reusable deal card + SearchBar
    └── screens/
        ├── DealsScreen.kt   Search + filters + deal list (3 states)
        ├── DetailScreen.kt  Big image, price, "why it's a good deal", free promo
        └── SavedScreen.kt   Saved deals list with empty state
```

**Data flow (one direction):** the ViewModel holds state → screens read it and
draw → user taps a button → screen calls a ViewModel function → state changes →
Compose redraws the affected screens. The cards themselves are "dumb": they
receive data and report taps via callbacks, so they're easy to reuse and test.

---

## Features

- **Search** products by name or retailer.
- **Filter by retailer** (chips: All / FreshMart / GreenGrocer / ValueFoods).
- **Sort by price** — Price ↑ (low to high) and Price ↓ (high to low).
- **Free Deals filter** — a chip next to Price ↓ that shows only free-bundle items.
- **Clear** — resets search, retailer, sort, and the Free Deals filter in one tap.
- **Save / unsave** any deal (heart on the card, or a CTA on the detail screen).
- **Three load states** — loading spinner, empty state, error state with Retry.

### Free-bundle deals

Some products are "free when purchased with" another item — for example
**Bananas are free when you buy Greek Yogurt**. These items:

- show their **normal price** with a green **FREE** badge and the promo text
  ("Free when purchased with Greek Yogurt") on both the card and detail screen,
- appear when the **Free Deals** filter is turned on,
- and on the **Saved screen** drop to **$0.00** once their required partner item
  is also saved. Save the Banana alone and it shows $2.00; save Greek Yogurt too
  and the Banana becomes $0.00 with a "You save $2.00" label. This mimics how a
  real bundle unlocks once both items are in the basket.

Current free-bundle deals in the mock data:

| Item | Normal price | Free when you also buy |
|---|---|---|
| Bananas | $2.00 | Greek Yogurt |
| Strawberry Jam | $4.49 | Sourdough Bread |
| BBQ Sauce | $3.99 | Chicken Breast |
| Almond Milk | $3.79 | Cereal |

---

## UX decisions

- **Visual hierarchy on each card:** product name and bold green price are the
  loudest elements; retailer/size are muted; the struck-through original price
  and amber "You save $X" label make a discount obvious at a glance.
- **One accent for savings.** Green is the brand; amber is reserved *only* for
  savings cues (Best Deal badge, secondary actions) so "money saved" reads
  instantly. The FREE badge uses the brand green so "free" feels positive.
- **Three honest states.** Loading shows a spinner with a message; empty states
  tell you what to do next ("Try a different search or clear your filters");
  the error state explains the problem and gives a Retry button.
- **Filters stay visible** above the list so users always see how results are
  being narrowed, and the chips wrap responsively with `FlowRow` (they stack on
  narrow phones, spread out on wide screens) instead of forcing sideways scroll.
- **Free deals are honest about the catch.** The promo text and required item
  are always shown, so the user understands the $0.00 only applies as a bundle.
- **Save is reachable twice** — the heart on every card and a full-width CTA on
  the detail screen — because saving is the core action.

---

## How this could fit into the real Prox app

This prototype maps cleanly onto a production feature:

- `MockRepository` would be swapped for a **real deals API** pulling live
  retailer pricing — the rest of the app wouldn't change because screens only
  talk to the ViewModel, not the data source.
- The savings logic and **Best Deal / FREE** flags would move **server-side**,
  computed from real price history and promotions, so the badges are trustworthy.
- The free-bundle unlock would be tied to the **actual cart contents** instead of
  the saved list (saving is the closest signal we have in a no-cart prototype).
- Saved deals would sync to a **user account** (currently in-memory only).
- The card/badge/state components are reusable building blocks for other Prox
  surfaces like a home feed or a retailer page.

---

## What I'd improve with more time

- **Persist saved deals** with DataStore or Room so they survive app restart.
- **Real product images** loaded with Coil instead of emoji placeholders.
- **Tie free-deal unlocking to a real cart** rather than the saved list.
- **More filters** (category, dietary, on-sale-only) and a search history.
- **Animations** on save (heart fill) and on the price changing to $0.00.
- **Accessibility pass:** content descriptions on every interactive element,
  verified with TalkBack.
- **More tests**, including a Compose UI test for search → empty state and a
  unit test for the free-deal unlock price.

---

## Testing plan

**Unit tests:** the savings math on `Product` (difference, percentage,
divide-by-zero guard) and the free-deal unlock price (Banana = $2.00 alone,
$0.00 once Greek Yogurt is saved). These run on the JVM in seconds.

**What I'd add:**
- ViewModel tests: search filters the list, retailer filter works, the Free
  Deals filter shows only free items, toggleSaved adds/removes correctly,
  clearFilters resets everything.
- Compose UI tests: typing a no-match query shows the empty state; tapping a
  card navigates to detail; Retry re-runs the load.
- Manual smoke test of all three load states (set `MockRepository.FORCE_ERROR
  = true` to force the error state for a demo).

### Devices and screen sizes to test
- Small phone (~5.0", 360dp wide) — confirm filter chips wrap, don't overflow.
- Standard phone (Pixel 7, ~6.3").
- Large phone / fold outer screen.
- Tablet — confirm the single-column list still looks intentional.
- Light **and** dark mode.
- Font scale set to "Largest" — confirm text doesn't clip.
- Landscape — confirm scroll works and state survives rotation.

### Bugs / UX issues to check before shipping
- Saved deals reset on rotation? (Should NOT — the ViewModel keeps them.)
- Free Deals filter combines correctly with search, retailer, and Price ↑/↓.
- Banana shows $0.00 in Saved only after Greek Yogurt is also saved, and reverts
  to $2.00 if Greek Yogurt is unsaved.
- FREE badge and promo text show on the card, detail, and saved screens.
- Empty state shows when filters exclude everything.
- Back button from detail returns to the right screen.
- Long product names wrap cleanly and don't push the price off-screen.

---

## Google Play Store deployment readiness

This is a **prototype**, so it is *demo-ready* but not *store-ready*. To ship:

1. **App identity:** final adaptive app icon, real `applicationId`, incrementing
   `versionCode`/`versionName`.
2. **Signing:** generate an upload keystore and enable Play App Signing.
3. **Release build:** turn on R8 shrinking, build an **Android App Bundle (.aab)**.
4. **Store listing:** title, descriptions, phone + tablet screenshots, feature
   graphic, privacy policy URL, content rating, and the data-safety form (this
   app collects no data).
5. **Pre-launch:** run Play Console's pre-launch report and fix any flags.

### Tools I would use
- **Android Studio** — build, profiler, layout inspector, emulators.
- **Internal testing track (Play Console)** — share the .aab with testers first.
- **Firebase Crashlytics** — catch real-world crashes with stack traces.
- **Google Play Console** — releases, staged rollout, vitals.

---

## How to run

1. Open the `ProxDeals` folder in Android Studio (Hedgehog or newer).
2. Let Gradle sync (it downloads the dependencies).
3. Pick an emulator or device and press **Run ▶**.
4. To demo the error state, set `MockRepository.FORCE_ERROR = true` and re-run.

### Quick free-deal demo
1. Tap **Free Deals** — only Bananas, Strawberry Jam, BBQ Sauce, Almond Milk
   show, each at its normal price with a FREE badge.
2. Save the **Banana**, then open **Saved Deals** — it shows $2.00.
3. Go back, save **Greek Yogurt**, return to Saved — the Banana is now **$0.00**.
