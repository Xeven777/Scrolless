# APK Optimization Tasks

## Overview

This document tracks all optimizations to reduce Scrolless APK size and improve build efficiency.

### Results (2026-09-22)

| Variant | Before | After | Saved |
|---------|--------|-------|-------|
| OSS Debug | 18.46 MB | **6.43 MB** | **65%** |
| Play Debug | 18.52 MB | **6.45 MB** | **65%** |
| OSS Release | (minify already on) | **3.49 MB** | — |

Uncompressed debug APK: 44.85 MB → 13.68 MB. DEX: 40.70 MB → 10.46 MB.

---

## 1. R8 Full Mode (High Impact) — DONE

**File:** `gradle.properties`
**Expected impact:** ~5-15% smaller APK via more aggressive tree-shaking and inlining

### Applied

```properties
android.enableR8.fullMode=true
```

---

## 2. Enable ShrinkResources for Debug (High Impact) — DONE

**File:** `app/build.gradle.kts`
**Expected impact:** Removes unused debug-only resources, smaller dev APK

### Applied

Debug build type now has `isMinifyEnabled = true`, `isShrinkResources = true`, and the same ProGuard files as release.

---

## 3. Native Library Compression (Medium Impact) — SKIPPED (low value)

**File:** `app/build.gradle.kts`
**Current state:** Only `libandroidx.graphics.path.so` (~37 KB total across 4 ABIs)

Native libs are negligible. ABI splits not worth the distribution complexity for ~37 KB.

---

## 4. Unused Language Resources (Medium Impact) — DEFERRED

**File:** `app/build.gradle.kts`
**Current state:** 32 translations present (4–5 strings each) — intentional for an accessibility tool

`resources.arsc` already shrank ~1 MB → 421 KB via resource shrinking. Restricting locales was **not** applied; revisit only if product decides to drop languages.

---

## 5. Debug APK Zip Alignment (Low Impact) — N/A

Zip alignment is handled by AGP by default.

---

## 6. Compose Compiler Optimizations (Medium Impact) — N/A

No safe size-related Gradle flags to add here; Compose compiler is on Kotlin 2.4.0 plugin.

---

## 7. ProGuard Rules Hardening (Medium Impact) — NOT APPLIED (as written)

Current rules already include `-repackageclasses` and source-file attributes.

**Do not add** the blanket `-keep class dagger.hilt.** { *; }` / Material / Navigation keeps suggested earlier — those *increase* size by disabling shrinking. R8 full mode + existing rules are doing the work (mapping under `app/build/outputs/mapping/`).

Optional later: strip verbose `Timber.v`/`Timber.d` calls from release via `-assumenosideeffects` if log volume matters at runtime (not APK size).

---

## 8. Remove Test Dependencies from Release APK — DONE (already correct)

Test deps use `testImplementation` / `androidTestImplementation` only.

---

## 9. PNG/WebP Image Optimization — DONE (already WebP)

Drawables and launcher icons are already `.webp`. Only `ic_launcher-playstore.png` remains (Play listing asset, not in APK).

---

## 10. Remove Unused Dependencies — DONE

Removed (no Kotlin/Compose usages found):

- `coil` from `app`, `core/data`, `core/designsystem`
- `androidx.palette` from `app`

**Kept:** `appcompat` — required for `?attr/colorControlNormal` in drawables (build fails without it).

---

## 11. Enable Dependency Locking (Build Stability) — NOT DONE

Not size-related. Optional follow-up:

```bash
./gradlew dependencies.lock
```

---

## Verification

```bash
./gradlew clean assembleDebug assembleRelease

ls -lh app/build/outputs/apk/oss/debug/*.apk \
       app/build/outputs/apk/oss/release/*.apk \
       app/build/outputs/apk/play/debug/*.apk
```

R8 outputs: `app/build/outputs/mapping/{ossDebug,ossRelease,playDebug}/`

---

## Priority Order (status)

| Priority | Task | Status | Impact |
|----------|------|--------|--------|
| 1 | R8 Full Mode | ✅ Done | High |
| 2 | ShrinkResources + minify for Debug | ✅ Done | High |
| 3 | Remove Unused Dependencies (coil, palette) | ✅ Done | Medium |
| 4 | ProGuard Hardening | ⏸ Not as written | — |
| 5 | Native Lib Compression | ⏭ Skip (~37 KB) | Low |
| 6 | Unused Language Resources | ⏸ Deferred (product) | Low |
| 7 | Compose Compiler Optimizations | ⏭ N/A | — |
| 8 | PNG→WebP | ✅ Already WebP | — |
| 9 | ABI Split | ⏭ Skip | Low |
| 10 | Debug Zip Alignment | ✅ AGP default | Low |
| 11 | Dependency Locking | ⬜ Optional | Build only |

---

## Notes

- Debug builds now run R8 — first build after clean is slower; subsequent incremental builds are fine
- Always test after each optimization — some shrinking rules can break things
- Test release builds on a physical device before relying on them
- Release size is what matters most for distribution: **3.49 MB OSS release APK**
- Dummy keystore used only for local size measurement; real releases still need `ANDROID_RELEASE_*` env vars
