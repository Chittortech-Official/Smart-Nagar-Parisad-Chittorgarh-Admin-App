# गढ़-सेवा (GarhSeva) - Smart Nagar Parishad Chittorgarh

> **Executive Municipal Command Center & Grievance Redressal System**  
> Developed by **ChittorTech** for Nagar Parishad Chittorgarh  
> Under the leadership of Hon. Chairman **Anil Inani** & Hon. Vice Chairman **Sudarshan Rampuria**

---

## 📁 Repository Structure

```
Smart-Nagar-Parishad-Chittorgarh-Admin-App/
├── app/                  # 📱 Native Android App (Jetpack Compose, Supabase Realtime, Ktor OkHttp)
├── gradle/               # ⚙️ Gradle build system & dependencies
├── supabase/             # 🗄️ PostgreSQL database schemas, triggers & ward migrations
├── web/                  # 🌐 Web Admin Dashboard (React 19, TypeScript, Vite, Tailwind CSS)
├── build.gradle.kts      # Top-level Gradle configuration
├── settings.gradle.kts   # Project module inclusions (:app)
├── local.properties      # Android SDK & Keystore configuration (gitignored)
└── chittortech_release_key.jks # Production signing keystore (gitignored)
```

---

## 📱 1. Android Executive Mobile App (`/app`)

* **Framework**: Jetpack Compose (Kotlin 2.0+), Material3
* **Target SDK**: Android 15 (API 36) / Min SDK: Android 8.0 (API 26)
* **Backend**: Supabase Database & Realtime (Ktor OkHttp WebSocket Engine)
* **Live Monitoring**: 24×7 Background Foreground Service (`SnpLiveMonitoringService`) with automatic reboot listener (`SnpBootReceiver`)
* **Alerts**: Dynamic aesthetic notification cards with instant action buttons:
  - 📞 *कॉल करें (+91...)* (Direct Citizen Dialing)
  - 👁️ *प्रगति देखें* (Instant deep-linking to ticket detail)

### Build Commands:
```bash
# Debug APK Build
./gradlew assembleDebug

# Production Release App Bundle (for Google Play Store)
./gradlew bundleRelease
```

---

## 🌐 2. Web Admin Portal (`/web`)

* **Framework**: React 19 + TypeScript + Vite + Tailwind CSS
* **Directory**: `web/`

### Development:
```bash
cd web
npm install
npm run dev
```

---

## 🗄️ 3. Database & SQL Triggers (`/supabase`)

* `supabase/schema.sql` - Complete PostgreSQL database schema with RLS policies, tickets, departments, wards, and SLA triggers.
* `supabase/supabase_push_notifications_trigger.sql` - Real-time database notification triggers.
* `supabase/update_wards_official_2026.sql` - Municipal ward master data.
