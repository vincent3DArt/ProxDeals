# Prox Deals — Grocery Search & Deals (Track A)

A mobile-first Android prototype for searching grocery products, browsing deals,
and saving the best ones. Built for the Prox Mobile UI/UX Implementation
assessment. Uses local mock data only — no backend.

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
│   ├── Product.kt           Data model (+ computed savings / savingsPercent)
│   └── MockRepository.kt    Fake "server" with the product catalogue
│
└── ui/
    ├── theme/
    │   ├── Color.kt         Brand palette (market green + savings amber)
    │   └── Theme.kt         Material 3 color scheme + typography scale
    ├── components/
    │   ├── CommonComponents.kt   LoadingState, EmptyState, ErrorState,
    │   │                         BestDealBadge, SavingsLabel
    │   └── DealCard.kt           Reusable deal card + SearchBar
    └── screens/
        ├── DealsScreen.kt   Search + filters + deal list (3 states)
        ├── DetailScreen.kt  Big image, price, "why it's a good deal", save CTA
        └── SavedScreen.kt   Saved deals list with empty state
```

**Data flow (one direction):** the ViewModel holds state → screens read it and
draw → user taps a button → screen calls a ViewModel function → state changes →
Compose redraws the affected screens. The cards themselves are "dumb": they
receive data and report taps via callbacks, so they're easy to reuse and test.

---

## UX decisions

- **Visual hierarchy on each card:** product name and bold green price are the
  loudest elements; retailer/size are muted; the struck-through original price
  and amber "You save $X" label make the deal obvious at a glance.
- **One accent for savings.** Green is the brand; amber is reserved *only* for
  savings cues (Best Deal badge, secondary actions) so "money saved" reads
  instantly. (Restraint: spend boldness in one place.)
- **Three honest states.** Loading shows a spinner with a message; empty states
  tell you what to do next ("Try a different search or clear your filters");
  the error state explains the problem and gives a Retry button.
- **Filters stay visible** above the list so users always see how results are
  being narrowed, and a **Clear** chip resets everything in one tap.
- **Save is reachable twice** — the heart on every card and a full-width CTA on
  the detail screen — because saving is the core action.
- **Mobile-first & responsive:** single-column list, horizontally scrollable
  filter chips so they never overflow on small phones, and `verticalScroll` on
  the detail screen so content is reachable on short screens.

---

## How this could fit into the real Prox app

This prototype maps cleanly onto a production feature:

- `MockRepository` would be swapped for a **real deals API** (retrofit/Ktor)
  pulling live retailer pricing — the rest of the app wouldn't change because
  screens only talk to the ViewModel, not the data source.
- The savings logic (`savings`, `savingsPercent`, Best Deal flag) would move
  **server-side**, computed from real price history, so "Best Deal" is trustworthy.
- Saved deals would sync to a **user account** (currently in-memory only).
- The card/badge/state components are reusable building blocks for other Prox
  surfaces like a home feed or a retailer page.

---

## What I'd improve with more time

- **Persist saved deals** with DataStore or Room so they survive app restart
  (right now they're only kept in memory while the app is open).
- **Real product images** loaded with Coil instead of emoji placeholders.
- **Price range slider** filter, not just sort.
- **Bottom navigation bar** instead of a top heart icon for the Saved tab.
- **Animations** on save (heart fill) and card appearance.
- **Accessibility pass:** content descriptions on every interactive element,
  and verify with TalkBack.
- **More tests**, including a Compose UI test for the search-to-empty-state flow.

---

## Testing plan

**Unit tests (included):** `ProductTest.kt` checks the savings math
(difference, percentage, divide-by-zero guard). These run on the JVM in seconds.

**What I'd add:**
- ViewModel tests: search filters the list, retailer filter works, toggleSaved
  adds/removes correctly, clearFilters resets state.
- Compose UI tests: typing a no-match query shows the empty state; tapping a
  card navigates to detail; Retry re-runs the load.
- Manual smoke test of all three states (set `MockRepository.FORCE_ERROR = true`
  to force the error state for the demo).

### Devices and screen sizes to test
- Small phone (~5.0", e.g. Pixel 4a / 360dp wide) — check chips don't overflow.
- Standard phone (Pixel 7, ~6.3").
- Large phone (Pixel 8 Pro / fold outer screen).
- Tablet (Nexus 9) — confirm the single-column list still looks intentional.
- Light **and** dark mode.
- Font scale set to "Largest" in system settings — confirm text doesn't clip.
- Landscape orientation — confirm scroll works and state survives rotation.

### Bugs / UX issues to check before shipping
- Saved deals reset on rotation? (Should NOT — ViewModel keeps them.)
- Empty state shows correctly when filters exclude everything.
- Heart icon state matches reality on both the list and detail screen.
- Back button from detail returns to the right screen.
- Long product names wrap cleanly and don't push the price off-screen.
- Detail screen for a missing id shows the fallback message, not a crash.

---

## Google Play Store deployment readiness

This is a **prototype**, so it is *demo-ready* but not *store-ready*. To ship:

1. **App identity:** final app icon (adaptive icon), real `applicationId`,
   incrementing `versionCode`/`versionName`.
2. **Signing:** generate an upload keystore and configure release signing;
   enable Play App Signing.
3. **Release build:** turn on `isMinifyEnabled` + R8 shrinking, build an
   **Android App Bundle (.aab)**.
4. **Store listing:** title, short/long description, screenshots for phone +
   tablet, feature graphic, privacy policy URL, content rating questionnaire,
   data-safety form (this app collects no data).
5. **Pre-launch:** run through Play Console's pre-launch report (automated
   device testing) and fix any crashes/accessibility flags.

### Tools I would use
- **Android Studio** — build, profiler, layout inspector, device emulators.
- **Internal testing track (Play Console)** — share the .aab with a small group
  before public release.
- **Firebase Crashlytics** — catch real-world crashes with stack traces.
- **Google Play Console** — releases, staged rollout, pre-launch reports, vitals.
- (Optional) **Firebase Analytics** to see which deals get saved most.

---

## How to run

1. Open the `ProxDeals` folder in Android Studio (Hedgehog or newer).
2. Let Gradle sync (it downloads the dependencies).
3. Pick an emulator or device and press **Run ▶**.
4. To demo the error state, set `MockRepository.FORCE_ERROR = true` and re-run.
