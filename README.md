# WortMeister 🇩🇪 — Akıllı Almanca & İngilizce Morfolojik Sözlük, 6 Oyun & SRS Flashcard

WortMeister, harici framework bağımlılığı olmaksızın saf yerel Android (Kotlin, Jetpack Compose, Room SQLite v6, Coroutines, Web Speech & TextToSpeech) mimarisiyle inşa edilmiş çevrimdışı öncelikli (offline-first) akıllı bir dil öğrenme platformudur.

---

## 🌟 Temel Özellikler

- **0ms Çevrimdışı Morfoloji ve Sözlük:**
  - 150+ hazır kelime, A1-C1 CEFR seviyeleri.
  - Fiil zamanları (Präsens, Präteritum, Partizip II, Hilfsverb hat/ist, Imperativ).
  - İsim artikelleri (`der`, `die`, `das`) ve çoğul formlar.
  - Sıfat derecelendirmeleri (Positiv, Komparativ, Superlativ).
  - Nomen-Verb-Verbindungen (Deyimsel kalıplar).
- **6 İnteraktif Test Modu:**
  1. **Artikel Testi:** `der`, `die`, `das` artikellerini doğru eşleştirme.
  2. **Fiil Testi:** Präteritum, Partizip II ve yardımcı fiil çekimleri.
  3. **İsim Testi:** Çoğul formlar ve Türkçe anlamlar.
  4. **Sıfat Testi:** Anlam ve Komparativ/Superlativ derecelendirmeleri.
  5. **Kalıp Testi:** Günlük deyimler ve C1 Nomen-Verb kalıpları.
  6. **Karma Test:** Tüm kelime türlerinden rastgele sorular.
- **Deste Kapsamı:** Tüm desteleri dahil etme veya seçilen destelerle oynama.
- **SRS Leitner Flashcard:** Günlük çalışma seansları, 3D flip kartlar, aralıklı tekrar algoritması.
- **Saf #000000 Pitch-Black OLED Arayüzü:** Sıfır emoji kuralı, %100 vektör ikonlar.
- **Telaffuz ve Ses:** Dil kilitli TTS ve mikrofon telaffuz değerlendirmesi.

---

## 🛠️ Mimari ve Teknolojiler

- **Dil:** Kotlin 2.0+
- **UI:** Jetpack Compose (Material3, Pitch-Black OLED dark theme)
- **Veritabanı:** Room SQLite (v6.0.0 şeması)
- **Asenkron:** Kotlin Coroutines & StateFlow
- **Testler:** JUnit 4 birim testleri
