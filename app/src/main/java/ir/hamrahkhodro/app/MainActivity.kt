package ir.hamrahkhodro.app

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.widget.DatePicker
import android.widget.NumberPicker
import java.net.HttpURLConnection
import java.net.URL
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.text.InputFilter
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
import java.util.GregorianCalendar
import java.util.UUID

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("mycar_store", MODE_PRIVATE) }
    private val teal = Color.rgb(13, 111, 112)
    private val deep = Color.rgb(12, 45, 55)
    private val ink = Color.rgb(29, 43, 52)
    private val muted = Color.rgb(105, 121, 128)
    private val page = Color.rgb(245, 247, 248)
    private val accent = Color.rgb(218, 246, 237)
    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private var tab = "خانه"
    private val mainHandler = Handler(Looper.getMainLooper())
    private var otpRunnable: Runnable? = null
    private var dateCalendar = "شمسی"
    private var expenseCategoryFilter = "همه"
    private var expensePeriodFilter = "همه زمان‌ها"
    private var vazirTypeface: Typeface? = null

    data class CarModel(val brand: String, val name: String, val image: String = "")
    data class MaintenanceItem(val title: String, val intervalKm: Int, val intervalMonths: Int, val detail: String)
    private val carModels = listOf(
        CarModel("ایران‌خودرو", "پژو ۲۰۷ دنده‌ای"),
        CarModel("ایران‌خودرو", "پژو ۲۰۷ اتوماتیک"),
        CarModel("ایران‌خودرو", "پژو ۲۰۶ تیپ ۲"),
        CarModel("ایران‌خودرو", "پژو پارس"),
        CarModel("ایران‌خودرو", "دنا پلاس"),
        CarModel("ایران‌خودرو", "تارا دنده‌ای"),
        CarModel("ایران‌خودرو", "تارا اتوماتیک"),
        CarModel("ایران‌خودرو", "سورن پلاس"),
        CarModel("ایران‌خودرو", "رانا پلاس"),
        CarModel("ایران‌خودرو", "سمند LX"),
        CarModel("ایران‌خودرو", "پژو ۴۰۵"),
        CarModel("ایران‌خودرو", "پژو پارس ELX"),
        CarModel("ایران‌خودرو", "آریسان ۲"),
        CarModel("ایران‌خودرو", "ری‌را"),
        CarModel("ایران‌خودرو", "هایما S5"),
        CarModel("ایران‌خودرو", "هایما S7"),
        CarModel("سایپا", "شاهین"),
        CarModel("سایپا", "کوییک"),
        CarModel("سایپا", "کوییک S"),
        CarModel("سایپا", "ساینا S"),
        CarModel("سایپا", "اطلس"),
        CarModel("سایپا", "تیبا"),
        CarModel("سایپا", "تیبا ۲"),
        CarModel("سایپا", "پراید ۱۳۱"),
        CarModel("سایپا", "پراید ۱۱۱"),
        CarModel("سایپا", "سهند"),
        CarModel("سایپا", "سایپا ۱۵۱"),
        CarModel("MVM", "X22"),
        CarModel("MVM", "X22 Pro"),
        CarModel("MVM", "X33"),
        CarModel("MVM", "X55 Pro"),
        CarModel("MVM", "315"),
        CarModel("MVM", "X33 Cross"),
        CarModel("MVM", "530 / 550"),
        CarModel("فونیکس", "Tiggo 7 Pro"),
        CarModel("فونیکس", "Arrizo 6 Pro"),
        CarModel("فونیکس", "FX"),
        CarModel("فونیکس", "Tiggo 8 Pro"),
        CarModel("چری", "Arrizo 5"),
        CarModel("بهمن", "دیگنیتی"),
        CarModel("بهمن", "فیدلیتی"),
        CarModel("بهمن", "رسپکت"),
        CarModel("بهمن", "مزدا ۳"),
        CarModel("کرمان موتور", "J4"),
        CarModel("کرمان موتور", "KMC T8"),
        CarModel("کرمان موتور", "KMC J7"),
        CarModel("کرمان موتور", "JAC S3"),
        CarModel("کرمان موتور", "JAC S5"),
        CarModel("پارس‌خودرو", "رنو تندر ۹۰"),
        CarModel("پارس‌خودرو", "رنو ساندرو"),
        CarModel("رنو", "داستر"),
        CarModel("تویوتا", "کرولا"),
        CarModel("تویوتا", "کمری"),
        CarModel("هیوندای", "توسان"),
        CarModel("کیا", "سراتو"),
        CarModel("پارس‌خودرو", "کوییک (مونتاژ/عرضه سایپا)"),
        CarModel("سایر", "خودروی دیگر / ورود دستی")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = deep
        window.navigationBarColor = page
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        loadVazirmatn()
        if (prefs.getBoolean("logged_in", false)) showApp() else showPhoneLogin()
    }

    private fun base(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(page)
        layoutDirection = View.LAYOUT_DIRECTION_RTL
    }

    private fun showPhoneLogin() {
        root = base()
        val scroller = ScrollView(this)
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER_HORIZONTAL; setPadding(dp(24), dp(48), dp(24), dp(32)) }
        val logoBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(teal, 28)
            setPadding(dp(18), dp(16), dp(18), dp(16))
            layoutParams = LinearLayout.LayoutParams(dp(110), dp(110)).apply { bottomMargin = dp(18) }
        }
        val logoIcon = TextView(this).apply {
            text = "🚗"
            textSize = 42f
            gravity = Gravity.CENTER
        }
        val logoLetter = TextView(this).apply {
            text = "MYCAR"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(2), 0, 0)
        }
        logoBox.addView(logoIcon)
        logoBox.addView(logoLetter)
        body.addView(logoBox)
        body.addView(label("MYCAR", 30f, deep, true).apply { gravity = Gravity.CENTER })
        body.addView(label("همراه هوشمند نگهداری خودرو", 15f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(8), 0, dp(26) ) })
        card(body) {
            addView(label("ورود یا ثبت‌نام", 20f, ink, true))
            addView(label("برای شروع، شماره موبایل ایران را وارد کن.", 13f, muted, false).apply { setPadding(0, dp(8), 0, dp(16)) })
            val phone = field("شماره موبایل، مثل 09123456789", "", true).apply { inputType = InputType.TYPE_CLASS_NUMBER; filters = arrayOf(InputFilter.LengthFilter(11)); textDirection = View.TEXT_DIRECTION_LTR; gravity = Gravity.CENTER }
            addView(phone)
            actionButtonIn(this, "دریافت کد تأیید", true) {
                val number = normalizeDigits(phone.text.toString()).filter { it.isDigit() }
                if (number.matches(Regex("09[0-9]{9}"))) showOtp(number) else toast("شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود")
            }
            addView(label("نسخه آزمایشی: پیامک واقعی ارسال نمی‌شود؛ کد تست 12345 است.", 12f, muted, false).apply { setPadding(0, dp(10), 0, 0) })
        }
        body.addView(label("اطلاعات این نسخه روی همین گوشی ذخیره می‌شود.", 12f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(22), 0, 0) })
        scroller.addView(body); root.addView(scroller); setContentView(root); applyVazirmatn(root)
    }

    private fun showOtp(phone: String) {
        otpRunnable?.let { mainHandler.removeCallbacks(it) }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(22), dp(8), dp(22), 0) }
        box.addView(label("کد تأیید برای $phone", 15f, ink, true).apply { setPadding(0, 0, 0, dp(8)) })
        val otp = field("کد ۵ رقمی", "", true).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(5))
            gravity = Gravity.CENTER
            textDirection = View.TEXT_DIRECTION_LTR
            textSize = 22f
        }
        box.addView(otp)
        val timerLabel = label("برای وارد کردن کد ۰۲:۰۰ فرصت داری.", 13f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(10), 0, dp(4)) }
        box.addView(timerLabel)
        val resend = TextView(this).apply {
            text = "ارسال مجدد کد"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setTextColor(muted)
            setPadding(dp(10), dp(10), dp(10), dp(8))
            isEnabled = false
        }
        box.addView(resend)
        val dialog = AlertDialog.Builder(this).setTitle("تأیید شماره موبایل").setView(box).setCancelable(false)
            .setPositiveButton("تأیید", null).setNegativeButton("بازگشت") { _, _ -> otpRunnable?.let { mainHandler.removeCallbacks(it) }; showPhoneLogin() }.create()
        fun startCountdown() {
            otpRunnable?.let { mainHandler.removeCallbacks(it) }
            resend.isEnabled = false
            resend.setTextColor(muted)
            val endAt = System.currentTimeMillis() + 120_000L
            val runner = object : Runnable {
                override fun run() {
                    val remaining = (endAt - System.currentTimeMillis()).coerceAtLeast(0L)
                    val seconds = (remaining / 1000L).toInt()
                    timerLabel.text = if (remaining > 0L) "زمان باقی‌مانده: %02d:%02d".format(seconds / 60, seconds % 60) else "مهلت کد تمام شد؛ می‌توانی کد جدید بگیری."
                    if (remaining > 0L && dialog.isShowing) mainHandler.postDelayed(this, 1000L)
                    else if (dialog.isShowing) { resend.isEnabled = true; resend.setTextColor(teal) }
                }
            }
            otpRunnable = runner
            runner.run()
        }
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val entered = normalizeDigits(otp.text.toString()).filter { it.isDigit() }
                if (System.currentTimeMillis() > (prefs.getLong("otp_expires_at", 0L))) {
                    toast("مهلت کد تمام شده؛ کد جدید بگیر")
                } else if (entered == "12345") {
                    otpRunnable?.let { mainHandler.removeCallbacks(it) }
                    prefs.edit().putBoolean("logged_in", true).putString("phone", phone).apply()
                    dialog.dismiss(); showApp()
                } else toast("کد واردشده صحیح نیست؛ کد تست 12345 است")
            }
            resend.setOnClickListener {
                if (resend.isEnabled) {
                    prefs.edit().putLong("otp_expires_at", System.currentTimeMillis() + 120_000L).apply()
                    otp.setText("")
                    toast("کد آزمایشی جدید ارسال شد: 12345")
                    startCountdown()
                }
            }
            prefs.edit().putLong("otp_expires_at", System.currentTimeMillis() + 120_000L).apply()
            startCountdown()
        }
        dialog.setOnDismissListener { otpRunnable?.let { mainHandler.removeCallbacks(it) }; otpRunnable = null }
        dialog.show()
    }

    private fun showApp() {
        root = base()
        val header = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(18), dp(20), dp(18)); background = rounded(deep, 0) }
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
        titleCol.addView(label("MYCAR", 24f, Color.WHITE, true))
        titleCol.addView(label("مدیریت هوشمند مالکیت خودرو", 12f, Color.rgb(190, 221, 221), false).apply { setPadding(0, dp(4), 0, 0) })
        top.addView(titleCol)
        val profile = TextView(this).apply { text = "پروفایل"; textSize = 12f; setTextColor(Color.WHITE); gravity = Gravity.CENTER; background = rounded(Color.rgb(39, 75, 82), 12); setPadding(dp(12), dp(10), dp(12), dp(10)); setOnClickListener { profileScreen() } }
        top.addView(profile); header.addView(top)
        root.addView(header)
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; setPadding(dp(8), dp(8), dp(8), dp(8)); setBackgroundColor(Color.WHITE) }
        listOf("خانه", "خودروها", "سرویس‌ها", "سوابق", "هزینه‌ها").forEach { item ->
            val b = TextView(this).apply {
                text = item; gravity = Gravity.CENTER; textSize = 11f; typeface = Typeface.DEFAULT_BOLD
                setTextColor(if (item == tab) Color.WHITE else muted); background = rounded(if (item == tab) teal else Color.WHITE, 11)
                setPadding(dp(3), dp(12), dp(3), dp(12)); setOnClickListener { tab = item; showApp() }
            }
            tabs.addView(b, LinearLayout.LayoutParams(0, -2, 1f).apply { marginStart = dp(2); marginEnd = dp(2) })
        }
        root.addView(tabs)
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(14), dp(16), dp(24)) }
        scroll.addView(content)
        when (tab) {
            "خانه" -> home()
            "خودروها" -> vehiclesScreen()
            "سرویس‌ها" -> serviceScreen()
            "سوابق" -> historyScreen()
            "هزینه‌ها" -> expensesScreen()
        }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root); applyVazirmatn(root)
    }

    private fun home() {
        val cars = load("cars")
        sectionTitle(content, "سلام، خوش آمدی 👋")
        content.addView(label("همه سرویس‌ها و هزینه‌های خودروهایت را یک‌جا مدیریت کن.", 13f, muted, false).apply { setPadding(0, 0, 0, dp(12)) })
        if (cars.isEmpty()) {
            card(content) {
                addView(label("هنوز خودرویی اضافه نکرده‌ای", 18f, ink, true))
                addView(label("برای دیدن برنامه سرویس اختصاصی و ثبت سوابق، اولین خودرو را به پروفایلت اضافه کن.", 13f, muted, false).apply { setPadding(0, dp(8), 0, dp(14)) })
                actionButtonIn(this, "＋ افزودن خودرو", true) { addVehicle() }
            }
            sectionTitle(content, "با MYCAR چه کارهایی می‌توانی انجام دهی؟")
            listOf("یادآوری سرویس بر اساس زمان و کیلومتر", "ثبت تعمیرات، مکان انجام و هزینه", "مشاهده مجموع هزینه‌های مالکیت", "دسترسی به برنامه پیشنهادی سرویس هر مدل").forEach { bullet(content, it) }
            return
        }
        val selected = selectedCar()
        if (selected != null) {
            card(content) {
                addView(label("خودروی انتخاب‌شده", 12f, muted, true))
                val line = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                val img = ImageView(this@MainActivity).apply { scaleType = ImageView.ScaleType.CENTER_CROP; background = rounded(Color.rgb(236, 241, 243), 12) }
                line.addView(img, LinearLayout.LayoutParams(dp(112), dp(78)))
                setCarIllustration(img, selected.optString("model"), selected.optString("brand")); loadRealCarPhoto(img, selected.optString("model"), selected.optString("brand"))
                val info = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
                info.addView(label(selected.optString("model"), 17f, ink, true))
                info.addView(label("${selected.optString("brand")} • ${selected.optString("year", "سال نامشخص")}", 12f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
                info.addView(label("کارکرد: ${selected.optString("km", "۰")} کیلومتر", 12f, teal, true).apply { setPadding(0, dp(4), 0, 0) })
                line.addView(info); addView(line)
                actionButtonIn(this, "جابجایی بین خودروها / پروفایل‌ها", false) { chooseCarProfile() }
            }
            marketPriceCard(selected)
            // Insurance reminders
            val bodyExp = selected.optString("bodyInsuranceExpiry", "")
            val thirdExp = selected.optString("thirdPartyInsuranceExpiry", "")
            if (bodyExp.isNotBlank() || thirdExp.isNotBlank()) {
                sectionTitle(content, "یادآوری بیمه")
                card(content) {
                    if (bodyExp.isNotBlank()) {
                        addView(label("بیمه بدنه", 13f, ink, true))
                        addView(label("تاریخ اتمام: $bodyExp  (یک‌ساله)", 12f, muted, false).apply { setPadding(0, dp(4), 0, dp(8)) })
                    }
                    if (thirdExp.isNotBlank()) {
                        addView(label("بیمه شخص ثالث", 13f, ink, true))
                        addView(label("تاریخ اتمام: $thirdExp  (یک‌ساله)", 12f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
                    }
                    addView(label("برای تمدید، اطلاعات را در پروفایل خودرو به‌روز کن.", 11f, teal, false).apply { setPadding(0, dp(10), 0, 0) })
                }
            }
            val id = selected.optString("id")
            val histories = load("history").filter { it.optString("vehicleId") == id }
            val expenses = load("expenses").filter { it.optString("vehicleId") == id }
            val total = expenses.sumOf { it.optLong("amount", 0L) }
            val metrics = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            metric(metrics, "سوابق", histories.size.toString()); metric(metrics, "هزینه کل", moneyShort(total)); metric(metrics, "خودروها", cars.size.toString())
            content.addView(metrics)
            sectionTitle(content, "سرویس‌های پیش‌رو")
            val upcoming = load("services").filter { it.optString("vehicleId") == id }.sortedBy { normalizeDigits(it.optString("km", "999999999")).filter { ch -> ch.isDigit() }.toLongOrNull() ?: Long.MAX_VALUE }.take(2)
            if (upcoming.isEmpty()) {
                emptyState(content, "سرویسی در تقویم نیست", "برنامه پیشنهادی خودرو را باز کن تا یادآوری‌های اولیه اضافه شوند.")
            } else {
                upcoming.forEach { itemCard(content, it, "service") }
            }
            sectionTitle(content, "دسترسی سریع")
            actionButton("＋ ثبت سرویس انجام‌شده", true) { addHistory() }
            actionButton("＋ ثبت هزینه", false) { addExpense() }
            actionButton("برنامه سرویس پیشنهادی", false) { showMaintenancePlan(selected) }
            sectionTitle(content, "آخرین سوابق")
            if (histories.isEmpty()) emptyState(content, "هنوز سابقه‌ای ثبت نشده", "بعد از سرویس یا تعمیر، اطلاعات آن را ثبت کن.")
            else histories.take(3).forEach { itemCard(content, it, "history") }
        }
    }

    private fun vehiclesScreen() {
        sectionTitle(content, "خودروهای من")
        content.addView(label("برای هر خودرو، برنامه سرویس و سوابق جداگانه نگهداری می‌شود.", 13f, muted, false).apply { setPadding(0, 0, 0, dp(12)) })
        actionButton("＋ افزودن خودرو", true) { addVehicle() }
        val cars = load("cars")
        if (cars.isEmpty()) emptyState(content, "لیست خودروها خالی است", "از دکمه بالا، برند و مدل خودرو را انتخاب کن.")
        cars.forEach { car ->
            card(content) {
                val row = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                val image = ImageView(this@MainActivity).apply { scaleType = ImageView.ScaleType.CENTER_CROP; background = rounded(Color.rgb(236, 241, 243), 12) }
                row.addView(image, LinearLayout.LayoutParams(dp(108), dp(82))); setCarIllustration(image, car.optString("model"), car.optString("brand")); loadRealCarPhoto(image, car.optString("model"), car.optString("brand"))
                val info = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
                info.addView(label(car.optString("model"), 16f, ink, true))
                info.addView(label("${car.optString("brand")} • ${car.optString("year", "سال نامشخص")}", 12f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
                info.addView(label("${car.optString("km", "۰")} کیلومتر", 12f, teal, true).apply { setPadding(0, dp(4), 0, 0) })
                row.addView(info); addView(row)
                actionButtonIn(this, if (car.optString("id") == prefs.getString("selected_car", "")) "پروفایل انتخاب‌شده ✓" else "انتخاب این پروفایل", false) { prefs.edit().putString("selected_car", car.optString("id")).apply(); tab = "خانه"; showApp() }
                actionButtonIn(this, "برنامه سرویس این مدل", false) { showMaintenancePlan(car) }
                val delete = TextView(this@MainActivity).apply { text = "حذف خودرو"; gravity = Gravity.END; setTextColor(Color.rgb(176, 66, 66)); textSize = 12f; setPadding(0, dp(8), 0, 0); setOnClickListener { AlertDialog.Builder(this@MainActivity).setMessage("حذف این خودرو و سوابق مرتبط؟").setNegativeButton("انصراف", null).setPositiveButton("حذف") { _, _ -> deleteCar(car); showApp() }.show() } }
                addView(delete)
            }
        }
    }

    private fun addVehicle() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val brands = carModels.map { it.brand }.distinct()
        val brandSpinner = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, brands) }
        val modelSpinner = Spinner(this)
        fun updateModels(brand: String) { modelSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, carModels.filter { it.brand == brand }.map { it.name }) }
        updateModels(brands.first())
        brandSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) = updateModels(brands[position])
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
        val year = field("سال ساخت (مثلاً ۱۴۰۱)")
        val km = field("کارکرد فعلی به کیلومتر", "0", true)
        val plate = field("پلاک یا نام دلخواه (اختیاری)")
        val bodyIns = dateField("تاریخ اتمام بیمه بدنه (تقویم شمسی/میلادی)")
        val thirdIns = dateField("تاریخ اتمام بیمه شخص ثالث (تقویم شمسی/میلادی)")
        box.addView(label("برند خودرو", 12f, muted, true)); box.addView(brandSpinner)
        box.addView(label("مدل خودرو", 12f, muted, true).apply { setPadding(0, dp(8), 0, 0) }); box.addView(modelSpinner)
        box.addView(label("اطلاعات پایه", 13f, ink, true).apply { setPadding(0, dp(10), 0, dp(4) ) })
        box.addView(year)
        box.addView(label("کارکرد فعلی خودرو", 12f, muted, true).apply { setPadding(0, dp(8), 0, dp(4)) })
        box.addView(km)
        val mileagePresets = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER; setPadding(0, dp(4), 0, dp(4)) }
        listOf(0, 50000, 100000, 150000, 200000).forEach { value -> val preset = TextView(this).apply { text = if(value==0) "صفر" else "${value/1000}هزار"; textSize=10f; gravity=Gravity.CENTER; setTextColor(teal); background=rounded(accent,9); setPadding(dp(6),dp(8),dp(6),dp(8)); setOnClickListener { km.setText(value.toString()) } }; mileagePresets.addView(preset, LinearLayout.LayoutParams(0,-2,1f).apply { marginStart=dp(2); marginEnd=dp(2) }) }
        box.addView(mileagePresets); box.addView(plate)
        box.addView(label("بیمه (مدت اعتبار یک سال)", 13f, ink, true).apply { setPadding(0, dp(12), 0, dp(4) ) })
        box.addView(bodyIns); box.addView(thirdIns)
        box.addView(label("یادآوری انقضای بیمه در داشبورد نمایش داده می‌شود.", 11f, muted, false).apply { setPadding(0, dp(6), 0, 0) })
        AlertDialog.Builder(this).setTitle("افزودن خودرو به MYCAR").setView(box).setNegativeButton("انصراف", null).setPositiveButton("افزودن") { _, _ ->
            val brand = brandSpinner.selectedItem?.toString() ?: "سایر"
            val model = modelSpinner.selectedItem?.toString() ?: "خودروی دیگر"
            val selected = carModels.firstOrNull { it.brand == brand && it.name == model }
            val obj = JSONObject().put("id", UUID.randomUUID().toString()).put("brand", brand).put("model", model)
                .put("year", normalizeDigits(year.text.toString())).put("km", normalizeDigits(km.text.toString()).filter { it.isDigit() }.ifBlank { "0" })
                .put("plate", plate.text.toString()).put("image", selected?.image ?: "").put("created", System.currentTimeMillis())
                .put("bodyInsuranceExpiry", bodyIns.text.toString().trim())
                .put("thirdPartyInsuranceExpiry", thirdIns.text.toString().trim())
            save("cars", obj)
            if (prefs.getString("selected_car", "").isNullOrBlank()) prefs.edit().putString("selected_car", obj.optString("id")).apply()
            tab = "خودروها"; showApp()
        }.show()
    }

    private fun chooseCarProfile() {
        val cars = load("cars")
        if (cars.isEmpty()) return addVehicle()
        AlertDialog.Builder(this).setTitle("انتخاب پروفایل خودرو").setItems(cars.map { it.optString("model") }.toTypedArray()) { _, which ->
            prefs.edit().putString("selected_car", cars[which].optString("id")).apply(); showApp()
        }.show()
    }

    private fun deleteCar(car: JSONObject) {
        val id = car.optString("id")
        saveList("cars", load("cars").filterNot { it.optString("id") == id })
        listOf("history", "expenses", "services").forEach { key -> saveList(key, load(key).filterNot { it.optString("vehicleId") == id }) }
        if (prefs.getString("selected_car", "") == id) prefs.edit().putString("selected_car", load("cars").firstOrNull()?.optString("id") ?: "").apply()
    }

    private fun selectedCar(): JSONObject? {
        val cars = load("cars")
        val id = prefs.getString("selected_car", "") ?: ""
        return cars.firstOrNull { it.optString("id") == id } ?: cars.firstOrNull()?.also { prefs.edit().putString("selected_car", it.optString("id")).apply() }
    }

    private fun maintenancePlanFor(car: JSONObject): Pair<String, List<MaintenanceItem>> {
        val model = car.optString("model")
        val normalized = normalizeDigits(model).lowercase(Locale.ROOT)
        val is207 = normalized.contains("207")
        val is206 = normalized.contains("206")
        val isTiba = model.contains("تیبا")
        val isShahin = model.contains("شاهین")
        val isMvm = car.optString("brand").contains("MVM") || car.optString("brand").contains("فونیکس") || car.optString("brand").contains("چری")
        val isIranKhodro = car.optString("brand").contains("ایران") || model.contains("دنا") || model.contains("تارا") || model.contains("سورن") || model.contains("سمند") || model.contains("رانا") || model.contains("پارس")
        val title: String
        val items: List<MaintenanceItem>
        when {
            is207 -> {
                title = "برنامه سرویس پژو ۲۰۷"
                items = listOf(
                    MaintenanceItem("سرویس کامل و بازدید ایمنی", 10000, 12, "بازدید نشتی‌ها، ترمز، تایرها، فرمان، تعلیق، چراغ‌ها و سطح مایعات."),
                    MaintenanceItem("تعویض روغن موتور و فیلتر روغن", 10000, 12, "در جدول سرویس ۲۰۷ با موتور TU5/TU5P هر ۱۰٬۰۰۰ کیلومتر یا یک سال، هرکدام زودتر برسد."),
                    MaintenanceItem("فیلتر هوای موتور", 40000, 24, "بازه تعویض درج‌شده در جدول ۲۰۷؛ در گردوغبار زودتر بازدید شود."),
                    MaintenanceItem("تسمه تایم و تسمه آلترناتور", 80000, 36, "بازه اعلام‌شده ۸۰٬۰۰۰ کیلومتر یا ۳ سال؛ سابقه تعویض و وضعیت تسمه اهمیت دارد."),
                    MaintenanceItem("مایع خنک‌کننده", 40000, 36, "تعویض مایع خنک‌کننده طبق برنامه ۳ ساله؛ بازدید سطح و نشتی در سرویس‌ها."),
                    MaintenanceItem("فیلتر کابین", 20000, 12, "بازدید در سرویس‌های دوره‌ای و تعویض زودتر در آلودگی و گردوغبار."),
                    MaintenanceItem("ترمز، تایرها و تعلیق", 10000, 12, "بازدید وضعیت لنت و دیسک، فشار و سایش تایر، اتصالات و کمک‌فنرها.")
                )
            }
            isTiba -> {
                title = "برنامه سرویس تیبا"
                items = listOf(
                    MaintenanceItem("سرویس دوره‌ای و تعویض روغن و فیلتر", 10000, 12, "جدول سرویس تیبا دوره‌های کیلومتری ۱۰٬۰۰۰ را مبنا قرار می‌دهد؛ زمان و کیلومتر هر دو مهم‌اند."),
                    MaintenanceItem("فیلتر هوا و فیلتر کابین", 10000, 12, "در هر سرویس بازدید؛ تعویض بر اساس برنامه و آلودگی محیط."),
                    MaintenanceItem("مایع خنک‌کننده", 40000, 24, "تعویض هر ۴۰٬۰۰۰ کیلومتر یا ۲۴ ماه."),
                    MaintenanceItem("ترمزها، تایرها و تعلیق", 10000, 12, "بازدید در سرویس دوره‌ای؛ لنت و قطعات فرسایشی بر اساس وضعیت واقعی تعویض شوند."),
                    MaintenanceItem("فیلتر سوخت", 20000, 24, "در جدول نگهداری تیبا برای این قلم دوره‌های تعویض مشخص شده؛ سابقه سرویس ثبت شود."),
                    MaintenanceItem("روغن ترمز و سیستم فرمان", 10000, 12, "سطح، نشتی و عملکرد بررسی شود؛ زمان تعویض بر اساس وضعیت و برنامه نگهداری خودرو.")
                )
            }
            isShahin -> {
                title = "برنامه سرویس سایپا شاهین"
                items = listOf(
                    MaintenanceItem("سرویس کامل و روغن موتور و فیلتر", 10000, 12, "بازه پایه منتشرشده برای برخی تیپ‌ها ۱۰٬۰۰۰ کیلومتر یا ۱۲ ماه است؛ تیپ و سال تولید در تعیین برنامه دقیق مؤثر است."),
                    MaintenanceItem("فیلتر هوای موتور", 10000, 12, "بازدید در هر سرویس و تعویض در موعد یا زودتر در شرایط گردوغبار."),
                    MaintenanceItem("مایع خنک‌کننده", 60000, 36, "بازه پیشنهادی منتشرشده ۶۰٬۰۰۰ کیلومتر یا ۳۶ ماه؛ سطح و نشتی در هر سرویس بررسی شود."),
                    MaintenanceItem("ترمز، تایرها و تعلیق", 10000, 12, "بازدید دوره‌ای؛ تعویض قطعات فرسایشی بر اساس اندازه‌گیری و وضعیت واقعی."),
                    MaintenanceItem("روغن گیربکس", 40000, 24, "برنامه با نوع گیربکس دستی یا اتوماتیک و تیپ خودرو متفاوت است؛ این مورد را در پرونده سرویس ثبت کن و از روغن نامتناسب استفاده نکن.")
                )
            }
            isMvm -> {
                title = "برنامه نگهداری ${model}"
                items = listOf(
                    MaintenanceItem("روغن موتور و فیلتر روغن", 7500, 6, "برای استفاده شهری، راهنماهای تخصصی X22 بازه حدود ۵ تا ۷٫۵ هزار کیلومتر را پیشنهاد می‌کنند؛ مشخصات موتور و نوع روغن مهم است."),
                    MaintenanceItem("فیلتر هوا و فیلتر کابین", 10000, 12, "بازدید در هر سرویس؛ در محیط آلوده معمولاً زودتر به تعویض نیاز دارند."),
                    MaintenanceItem("شمع موتور", 25000, 24, "بازه تخمینی برای شمع معمولی؛ نوع شمع و موتور می‌تواند بازه را تغییر دهد."),
                    MaintenanceItem("ترمزها، تایرها و تعلیق", 10000, 12, "بازدید وضعیت لنت، دیسک، تایر، اتصالات و کمک‌فنرها."),
                    MaintenanceItem("روغن گیربکس", 40000, 24, "موعد دقیق به مدل و گیربکس دستی یا CVT/اتوماتیک وابسته است؛ از مشخصات یک گیربکس برای دیگری استفاده نکن."),
                    MaintenanceItem("سیستم خنک‌کاری و تسمه‌ها", 10000, 12, "بازدید سطح مایع، نشتی و وضعیت تسمه‌ها در هر سرویس؛ تعویض قطعات حساس بر اساس نسخه دقیق خودرو.")
                )
            }
            isIranKhodro || is206 -> {
                title = "برنامه سرویس ${model}"
                items = listOf(
                    MaintenanceItem("سرویس دوره‌ای و بازدید ایمنی", 10000, 12, "در برنامه منتشرشده محصولات رایج ایران‌خودرو، بازدید دوره‌ای هر ۱۰٬۰۰۰ کیلومتر یا یک سال انجام می‌شود."),
                    MaintenanceItem("تعویض روغن موتور و فیلتر روغن", 10000, 12, "موعد کیلومتری یا زمانی هرکدام زودتر برسد؛ در استفاده سنگین، فاصله سرویس کوتاه‌تر می‌شود."),
                    MaintenanceItem("فیلتر هوا و فیلتر کابین", 10000, 12, "بازدید دوره‌ای و تعویض بر اساس میزان آلودگی و برنامه سرویس مدل."),
                    MaintenanceItem("ترمز، تایرها، فرمان و تعلیق", 10000, 12, "بازدید وضعیت و نشتی‌ها در هر سرویس دوره‌ای."),
                    MaintenanceItem("تسمه‌ها و سیستم خنک‌کاری", 20000, 12, "بازدید و ثبت سابقه؛ موعد تعویض تسمه تایم باید متناسب با موتور دقیق خودرو اعمال شود."),
                    MaintenanceItem("روغن گیربکس و مایعات", 20000, 24, "بازدید سطح، نشتی و وضعیت؛ موعد تعویض به موتور و نوع گیربکس وابسته است.")
                )
            }
            else -> {
                title = "برنامه پایه نگهداری ${model}"
                items = listOf(
                    MaintenanceItem("سرویس دوره‌ای و روغن موتور و فیلتر", 10000, 12, "برنامه پایه برای ثبت و یادآوری؛ بازه روغن در برخی موتورهای توربو یا خودروهای مونتاژی کوتاه‌تر است."),
                    MaintenanceItem("فیلتر هوا و فیلتر کابین", 10000, 12, "بازدید در هر سرویس و تعویض با توجه به آلودگی محیط."),
                    MaintenanceItem("ترمزها، تایرها، فرمان و تعلیق", 10000, 12, "بازدید ایمنی دوره‌ای؛ تعویض قطعات مصرفی بر اساس وضعیت واقعی."),
                    MaintenanceItem("مایع خنک‌کننده و روغن ترمز", 20000, 24, "کنترل سطح و نشتی؛ زمان تعویض به نوع مایع و مشخصات خودرو وابسته است."),
                    MaintenanceItem("شمع، تسمه‌ها و روغن گیربکس", 20000, 24, "در برنامه پایه به‌عنوان مورد قابل بررسی ثبت می‌شود؛ موعد دقیق به موتور و نوع گیربکس بستگی دارد.")
                )
            }
        }
        return title to items
    }

    private fun showMaintenancePlan(car: JSONObject) {
        val (title, plan) = maintenancePlanFor(car)
        val currentKm = normalizeDigits(car.optString("km", "0")).filter { it.isDigit() }.toIntOrNull() ?: 0
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(4)) }
        box.addView(label("${car.optString("brand")} • ${car.optString("model")}", 14f, teal, true))
        box.addView(label("برنامه نگهداری داخلی MYCAR • مبنای کارکرد فعلی: ${money(currentKm.toLong())} کیلومتر", 12f, muted, false).apply { setPadding(0, dp(6), 0, dp(10)) })
        plan.forEach { item ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), dp(10), dp(12), dp(10)); background = rounded(Color.rgb(245, 248, 249), 12) }
            row.addView(label(item.title, 14f, ink, true))
            row.addView(label("هر ${money(item.intervalKm.toLong())} کیلومتر یا ${item.intervalMonths} ماه", 12f, teal, true).apply { setPadding(0, dp(4), 0, dp(3)) })
            row.addView(label(item.detail, 12f, muted, false))
            box.addView(row, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(8) })
        }
        box.addView(label("برنامه‌های منتشرشده برای ۲۰۷، تیبا، دنا و X22 در این نسخه مبنای داده‌اند. در مدل‌ها و تیپ‌های دیگر، برخی موعدها برنامه پایه‌اند و با مشخصات دقیق موتور/گیربکس باید تکمیل شوند؛ شرایط سخت رانندگی ممکن است سرویس زودتر بخواهد.", 11f, muted, false).apply { setPadding(0, dp(8), 0, dp(4)) })
        val scroll = ScrollView(this).apply { addView(box) }
        AlertDialog.Builder(this).setTitle(title).setView(scroll).setPositiveButton("افزودن یادآوری‌ها") { _, _ ->
            val currentServices = load("services").filter { it.optString("vehicleId") == car.optString("id") }.toMutableList()
            var added = 0
            plan.forEach { item ->
                if (currentServices.none { it.optString("title") == item.title }) {
                    save("services", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                        .put("title", item.title).put("km", (currentKm + item.intervalKm).toString()).put("date", "")
                        .put("note", "برنامه MYCAR: هر ${item.intervalKm} کیلومتر یا ${item.intervalMonths} ماه. ${item.detail}")
                        .put("created", System.currentTimeMillis()))
                    added++
                }
            }
            toast(if (added > 0) "$added یادآوری اضافه شد" else "یادآوری‌های این برنامه قبلاً اضافه شده‌اند")
            tab = "سرویس‌ها"; showApp()
        }.setNegativeButton("بستن", null).show()
    }

    private fun serviceScreen() {
        val car = selectedCar()
        if (car == null) { emptyState(content, "ابتدا خودرو اضافه کن", "برای ساخت برنامه سرویس، باید مدل خودرو مشخص باشد."); actionButton("＋ افزودن خودرو", true) { addVehicle() }; return }
        sectionTitle(content, "سرویس‌ها • ${car.optString("model")}")
        actionButton("مشاهده برنامه پیشنهادی این مدل", true) { showMaintenancePlan(car) }
        actionButton("＋ افزودن یادآوری دلخواه", false) { addService() }
        val items = load("services").filter { it.optString("vehicleId") == car.optString("id") }
        if (items.isEmpty()) emptyState(content, "هنوز یادآوری‌ای ثبت نشده", "از برنامه پیشنهادی یا دکمه افزودن یادآوری استفاده کن.")
        val groups = linkedMapOf("روغن و فیلترها" to listOf("روغن", "فیلتر"), "تسمه‌ها و موتور" to listOf("تسمه", "شمع", "موتور"), "ترمز، تایر و ایمنی" to listOf("ترمز", "تایر", "تعلیق", "ایمنی"), "مایعات و خنک‌کاری" to listOf("مایع", "خنک", "گیربکس", "روغن ترمز"), "سایر سرویس‌ها" to emptyList())
        val used=mutableSetOf<String>()
        groups.forEach { (group,keys) -> val subset=items.filter { item -> !used.contains(item.optString("id")) && item.optString("title").containsAny(keys) }; if(subset.isNotEmpty()){ sectionTitle(content,group); subset.forEach { itemCard(content,it,"service"); used.add(it.optString("id")) } } }
        val rest=items.filterNot { used.contains(it.optString("id")) }; if(rest.isNotEmpty()){ sectionTitle(content,"سایر موارد"); rest.forEach { itemCard(content,it,"service") } }
    }

    private fun historyScreen() {
        if (selectedCar() == null) { emptyState(content, "ابتدا خودرو اضافه کن", "سوابق به هر خودرو متصل هستند."); actionButton("＋ افزودن خودرو", true) { addVehicle() }; return }
        sectionTitle(content, "سوابق سرویس و تعمیرات")
        actionButton("＋ ثبت سرویس انجام‌شده", true) { addHistory() }
        val id = selectedCar()!!.optString("id")
        val items = load("history").filter { it.optString("vehicleId") == id }
        if (items.isEmpty()) emptyState(content, "هنوز سابقه‌ای وجود ندارد", "تاریخ، کیلومتر، محل انجام و هزینه را ثبت کن.")
        items.forEach { itemCard(content, it, "history") }
    }

    private fun expensesScreen() {
        val car = selectedCar()
        if (car == null) { emptyState(content, "ابتدا خودرو اضافه کن", "هزینه‌ها به هر خودرو متصل هستند."); actionButton("＋ افزودن خودرو", true) { addVehicle() }; return }
        val all = load("expenses").filter { it.optString("vehicleId") == car.optString("id") }
        val categories = listOf("همه", "سوخت", "سرویس و تعمیر", "بیمه و عوارض", "پارکینگ و کارواش", "قطعات و لوازم", "سایر")
        val periods = listOf("همه زمان‌ها", "۳۰ روز اخیر", "۹۰ روز اخیر", "۱۲ ماه اخیر")
        card(content) {
            addView(label("گزارش هزینه‌های ${car.optString("model")}", 16f, ink, true))
            addView(label("فیلتر و تحلیل هزینه‌ها بر اساس دسته و بازه زمانی", 12f, muted, false).apply { setPadding(0,dp(5),0,dp(12)) })
            val cat = Spinner(this@MainActivity).apply { adapter=ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, categories); setSelection(categories.indexOf(expenseCategoryFilter).coerceAtLeast(0)); onItemSelectedListener=object: android.widget.AdapterView.OnItemSelectedListener { override fun onItemSelected(p: android.widget.AdapterView<*>?,v:View?,pos:Int,id:Long){ if(expenseCategoryFilter!=categories[pos]) { expenseCategoryFilter=categories[pos]; showApp() } }; override fun onNothingSelected(p: android.widget.AdapterView<*>?){} } }
            addView(label("دسته‌بندی", 12f, muted, true)); addView(cat)
            val period = Spinner(this@MainActivity).apply { adapter=ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, periods); setSelection(periods.indexOf(expensePeriodFilter).coerceAtLeast(0)); onItemSelectedListener=object: android.widget.AdapterView.OnItemSelectedListener { override fun onItemSelected(p: android.widget.AdapterView<*>?,v:View?,pos:Int,id:Long){ if(expensePeriodFilter!=periods[pos]) { expensePeriodFilter=periods[pos]; showApp() } }; override fun onNothingSelected(p: android.widget.AdapterView<*>?){} } }
            addView(label("بازه زمانی", 12f, muted, true).apply { setPadding(0,dp(8),0,0) }); addView(period)
        }
        val cutoff = when(expensePeriodFilter) { "۳۰ روز اخیر" -> System.currentTimeMillis()-30L*86400000; "۹۰ روز اخیر" -> System.currentTimeMillis()-90L*86400000; "۱۲ ماه اخیر" -> System.currentTimeMillis()-365L*86400000; else -> 0L }
        val timed = all.filter { it.optLong("created",0L) >= cutoff }
        val items = timed.filter { expenseCategoryFilter=="همه" || it.optString("category")==expenseCategoryFilter }
        val total = items.sumOf { it.optLong("amount") }
        val avg = if(items.isEmpty()) 0L else total/items.size
        val metrics = LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL }
        metric(metrics,"جمع هزینه فیلترشده",moneyShort(total)); metric(metrics,"تعداد",items.size.toString()); metric(metrics,"میانگین",moneyShort(avg)); content.addView(metrics)
        sectionTitle(content,"تفکیک هزینه بر اساس دسته")
        val categoryTotals = timed.groupBy { it.optString("category","سایر") }.mapValues { entry -> entry.value.sumOf { it.optLong("amount") } }.toList().sortedByDescending { it.second }
        if(categoryTotals.isEmpty()) emptyState(content,"داده‌ای برای نمودار نیست","با ثبت هزینه، گزارش دسته‌بندی‌شده اینجا نمایش داده می‌شود.") else {
            val maxAmount = categoryTotals.maxOf { it.second }.coerceAtLeast(1L)
            categoryTotals.forEach { (name,value) ->
                val row=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(0,dp(4),0,dp(8)) }
                val line=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER_VERTICAL }
                line.addView(label(name,12f,ink,true),LinearLayout.LayoutParams(0,-2,1f)); line.addView(label("${money(value)} تومان",12f,teal,true)); row.addView(line)
                val bar=ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal).apply { max=100; progress=((value*100/maxAmount).toInt()).coerceIn(1,100); progressTintList=android.content.res.ColorStateList.valueOf(teal); progressBackgroundTintList=android.content.res.ColorStateList.valueOf(Color.rgb(230,238,239)) }
                row.addView(bar,LinearLayout.LayoutParams(-1,dp(7))); content.addView(row)
            }
        }
        sectionTitle(content,"ریز هزینه‌ها")
        actionButton("＋ ثبت هزینه جدید", true) { addExpense() }
        if (items.isEmpty()) emptyState(content,"هزینه‌ای با این فیلتر نیست","فیلتر دسته یا بازه زمانی را تغییر بده.")
        items.forEach { itemCard(content,it,"expense") }
    }

    private fun profileScreen() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        box.addView(label("شماره موبایل", 12f, muted, true))
        box.addView(label(prefs.getString("phone", "") ?: "", 18f, ink, true).apply { setPadding(0, dp(6), 0, dp(14)) })
        box.addView(label("حساب این نسخه آزمایشی روی دستگاه ذخیره می‌شود و پیامک واقعی ارسال نمی‌شود.", 12f, muted, false))
        box.addView(label("تصاویر خودرو", 14f, ink, true).apply { setPadding(0, dp(16), 0, dp(6)) })
        box.addView(label("عکس واقعی خودروها در صورت اتصال اینترنت از ویکی‌مدیا دریافت می‌شود؛ در صورت نبود اینترنت، تصویر جایگزین نمایش داده می‌شود.", 11f, muted, false))
        AlertDialog.Builder(this).setTitle("پروفایل MYCAR").setView(box).setNegativeButton("بستن", null).setNeutralButton("خروج از حساب") { _, _ -> prefs.edit().putBoolean("logged_in", false).apply(); showPhoneLogin() }.show()
    }

    private fun addService() {
        val car = selectedCar() ?: run { toast("ابتدا خودرو اضافه کن"); addVehicle(); return }
        val box = formBox()
        val title = field("عنوان یادآوری (مثلاً روغن موتور)")
        val dueKm = field("کیلومتر یادآوری (اختیاری)", "", true)
        val dueDate = dateField("تاریخ یادآوری (اختیاری)")
        val note = field("توضیحات (اختیاری)")
        listOf(title, dueKm, dueDate, note).forEach { box.addView(it) }
        AlertDialog.Builder(this).setTitle("یادآوری سرویس").setView(box).setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
            if (title.text.isNotBlank()) save("services", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                .put("title", title.text.toString()).put("km", normalizeDigits(dueKm.text.toString())).put("date", dueDate.text.toString())
                .put("note", note.text.toString()).put("created", System.currentTimeMillis())) else toast("عنوان سرویس را وارد کن")
            showApp()
        }.show()
    }

    private fun addHistory() {
        val car = selectedCar() ?: run { toast("ابتدا خودرو اضافه کن"); addVehicle(); return }
        val box = formBox()
        val title = field("عنوان سرویس یا تعمیر")
        val date = dateField("تاریخ انجام سرویس")
        val km = field("کارکرد خودرو به کیلومتر", car.optString("km", "0"), true)
        val place = field("محل انجام / نام تعمیرگاه")
        val amount = field("هزینه به تومان", "0", true)
        val details = field("قطعات و توضیحات (اختیاری)")
        listOf(title, date, km, place, amount, details).forEach { box.addView(it) }
        AlertDialog.Builder(this).setTitle("ثبت سرویس انجام‌شده").setView(box).setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
            val cost = normalizeDigits(amount.text.toString()).filter { it.isDigit() }.toLongOrNull() ?: 0L
            if (title.text.isBlank()) toast("عنوان سرویس را وارد کن") else {
                val dateText = date.text.toString()
                val mileage = normalizeDigits(km.text.toString()).filter { it.isDigit() }
                save("history", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                    .put("title", title.text.toString()).put("date", dateText).put("km", mileage).put("place", place.text.toString())
                    .put("amount", cost).put("note", details.text.toString()).put("created", System.currentTimeMillis()))
                if (cost > 0) save("expenses", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                    .put("title", title.text.toString()).put("category", "سرویس و تعمیر").put("amount", cost).put("date", dateText)
                    .put("place", place.text.toString()).put("created", System.currentTimeMillis()))
                if (mileage.isNotBlank()) updateCarMileage(car, mileage)
                showApp()
            }
        }.show()
    }

    private fun addExpense() {
        val car = selectedCar() ?: run { toast("ابتدا خودرو اضافه کن"); addVehicle(); return }
        val box = formBox(); val title = field("عنوان هزینه")
        val category = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, listOf("سوخت", "سرویس و تعمیر", "قطعات و لوازم", "بیمه و عوارض", "پارکینگ و کارواش", "سایر")) }
        val amount = field("مبلغ به تومان", "", true); val date = dateField("تاریخ هزینه"); val place = field("محل خرید یا انجام (اختیاری)")
        box.addView(title); box.addView(category); box.addView(amount); box.addView(date); box.addView(place)
        AlertDialog.Builder(this).setTitle("ثبت هزینه خودرو").setView(box).setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
            val cost = normalizeDigits(amount.text.toString()).filter { it.isDigit() }.toLongOrNull()
            if (title.text.isBlank() || cost == null || cost <= 0) toast("عنوان و مبلغ معتبر وارد کن") else {
                save("expenses", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                    .put("title", title.text.toString()).put("category", category.selectedItem.toString()).put("amount", cost)
                    .put("date", date.text.toString()).put("place", place.text.toString()).put("created", System.currentTimeMillis()))
                showApp()
            }
        }.show()
    }

    private fun itemCard(parent: LinearLayout, item: JSONObject, kind: String) {
        card(parent) {
            addView(label(item.optString("title", "بدون عنوان"), 16f, ink, true))
            val parts = mutableListOf<String>()
            item.optString("date").takeIf { it.isNotBlank() }?.let { parts.add("تاریخ: $it") }
            item.optString("km").takeIf { it.isNotBlank() }?.let { parts.add("کارکرد: $it کیلومتر") }
            item.optString("place").takeIf { it.isNotBlank() }?.let { parts.add("محل: $it") }
            if (kind == "service") item.optString("note").takeIf { it.isNotBlank() }?.let { parts.add(it) }
            if (kind == "expense") parts.add("${item.optString("category", "هزینه")} • ${money(item.optLong("amount"))} تومان")
            if (kind == "history" && item.optLong("amount") > 0) parts.add("هزینه: ${money(item.optLong("amount"))} تومان")
            if (parts.isNotEmpty()) addView(label(parts.joinToString("\n"), 12f, muted, false).apply { setPadding(0, dp(7), 0, 0) })
            val delete = TextView(this@MainActivity).apply { text = "حذف"; textSize = 12f; setTextColor(Color.rgb(180, 65, 65)); gravity = Gravity.END; setPadding(0, dp(10), 0, 0); setOnClickListener {
                AlertDialog.Builder(this@MainActivity).setMessage("این مورد حذف شود؟").setNegativeButton("انصراف", null).setPositiveButton("حذف") { _, _ -> remove(kindKey(kind), item); showApp() }.show()
            } }
            addView(delete)
        }
    }

    private fun updateCarMileage(car: JSONObject, mileage: String) {
        val cars = load("cars").map { if (it.optString("id") == car.optString("id")) JSONObject(it.toString()).put("km", mileage) else it }
        saveList("cars", cars)
    }
    private fun kindKey(kind: String) = when (kind) { "service" -> "services"; "history" -> "history"; else -> "expenses" }
    private fun remove(key: String, item: JSONObject) { saveList(key, load(key).filterNot { it.optString("id") == item.optString("id") }) }
    private fun load(key: String): List<JSONObject> {
        val arr = try { JSONArray(prefs.getString(key, "[]")) } catch (_: Exception) { JSONArray() }
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it) }.sortedByDescending { it.optLong("created", 0L) }
    }
    private fun save(key: String, value: JSONObject) { saveList(key, load(key) + value) }
    private fun saveList(key: String, values: List<JSONObject>) { val arr = JSONArray(); values.forEach { arr.put(it) }; prefs.edit().putString(key, arr.toString()).apply() }

    private fun String.containsAny(values: List<String>): Boolean = values.isNotEmpty() && values.any { this.contains(it, ignoreCase = true) }

    private fun dateField(hintText: String): EditText = field(hintText, "", false).apply {
        isFocusable = false; isClickable = true; compoundDrawablePadding=dp(8)
        setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        setOnClickListener { chooseDate(this) }
        setOnLongClickListener { chooseDate(this); true }
    }

    private fun chooseDate(target: EditText) {
        val options = arrayOf("تقویم شمسی", "تقویم میلادی")
        AlertDialog.Builder(this).setTitle("انتخاب نوع تقویم").setItems(options) { _, which ->
            dateCalendar = if(which==0) "شمسی" else "میلادی"
            if(which==1) {
                val c=Calendar.getInstance()
                DatePickerDialog(this,{ _:DatePicker,y:Int,m:Int,d:Int -> target.setText("%04d/%02d/%02d".format(y,m+1,d)) },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show()
            } else showJalaliPicker(target)
        }.show()
    }

    private fun showJalaliPicker(target: EditText) {
        val box=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; gravity=Gravity.CENTER; setPadding(dp(12),dp(8),dp(12),dp(8)) }
        val year=NumberPicker(this).apply { minValue=1300; maxValue=1500; value=1405; wrapSelectorWheel=false }
        val month=NumberPicker(this).apply { minValue=1; maxValue=12; value=8; displayedValues=arrayOf("فروردین","اردیبهشت","خرداد","تیر","مرداد","شهریور","مهر","آبان","آذر","دی","بهمن","اسفند"); wrapSelectorWheel=true }
        val day=NumberPicker(this).apply { minValue=1; maxValue=31; value=1; wrapSelectorWheel=true }
        box.addView(year,LinearLayout.LayoutParams(0,dp(150),1f)); box.addView(month,LinearLayout.LayoutParams(0,dp(150),1f)); box.addView(day,LinearLayout.LayoutParams(0,dp(150),1f))
        month.setOnValueChangedListener { _,_,v -> day.maxValue=if(v<=6)31 else if(v<=11)30 else if(isJalaliLeap(year.value))30 else 29; if(day.value>day.maxValue)day.value=day.maxValue }
        year.setOnValueChangedListener { _,_,_ -> if(month.value==12){day.maxValue=if(isJalaliLeap(year.value))30 else 29;if(day.value>day.maxValue)day.value=day.maxValue} }
        AlertDialog.Builder(this).setTitle("انتخاب تاریخ شمسی").setView(box).setNegativeButton("لغو",null).setPositiveButton("انتخاب") { _,_ -> target.setText("%04d/%02d/%02d".format(year.value,month.value,day.value)) }.show()
    }
    private fun isJalaliLeap(y:Int):Boolean { val breaks=intArrayOf(-61,9,38,199,426,686,756,818,1111,1181,1210,1635,2060,2097,2192,2262,2324,2394,2456,3178); var jp=breaks[0]; var jump=0; for(i in 1 until breaks.size){ val jm=breaks[i]; jump=jm-jp; if(y<jm)break; jp=jm }; val n=y-jp; if(n<jump){ var r=(n* (jump/33.0)).toInt()%4; if(jump-r==4)r=4; return r==0 }; return false }

    private fun loadVazirmatn() {
        Thread {
            try {
                val file=File(filesDir,"Vazirmatn-Regular.ttf")
                if(!file.exists()) { val conn=URL("https://raw.githubusercontent.com/rastikerdar/vazirmatn/master/fonts/ttf/Vazirmatn-Regular.ttf").openConnection() as HttpURLConnection; conn.connectTimeout=5000; conn.readTimeout=7000; conn.inputStream.use { input -> FileOutputStream(file).use { input.copyTo(it) } }; conn.disconnect() }
                val face=Typeface.createFromFile(file); runOnUiThread { vazirTypeface=face; if(::root.isInitialized) applyVazirmatn(root) }
            } catch (_:Exception) { /* use Android's Persian-capable fallback font while offline */ }
        }.start()
    }
    private fun applyVazirmatn(view: View) {
        val face=vazirTypeface ?: return
        if(view is TextView) view.typeface=face
        if(view is android.view.ViewGroup) for(i in 0 until view.childCount) applyVazirmatn(view.getChildAt(i))
    }

    private fun loadRealCarPhoto(view: ImageView, model: String, brand: String) {
        val query = java.net.URLEncoder.encode("${model} ${brand} car", "UTF-8")
        Thread {
            try {
                val api="https://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=${query}&gsrnamespace=6&gsrlimit=8&prop=imageinfo&iiprop=url&iiurlwidth=900&format=json"
                val conn=URL(api).openConnection() as HttpURLConnection; conn.connectTimeout=6000; conn.readTimeout=7000; conn.setRequestProperty("User-Agent","MYCAR-MaintenanceApp/1.5 (photo lookup)")
                val json=org.json.JSONObject(conn.inputStream.bufferedReader().use { it.readText() }); conn.disconnect()
                val pages=json.optJSONObject("query")?.optJSONObject("pages") ?: return@Thread
                val candidates=(0 until pages.length()).mapNotNull { pages.optJSONObject(pages.names().optString(it)) }.mapNotNull { it.optJSONArray("imageinfo")?.optJSONObject(0)?.optString("thumburl")?.takeIf { u -> u.startsWith("https://") } }
                val photoUrl=candidates.firstOrNull() ?: return@Thread
                val imageConn=URL(photoUrl).openConnection() as HttpURLConnection; imageConn.connectTimeout=6000; imageConn.readTimeout=7000
                val bitmap=imageConn.inputStream.use { BitmapFactory.decodeStream(it) }; imageConn.disconnect()
                if(bitmap!=null) runOnUiThread { if(view.isAttachedToWindow) { view.setImageBitmap(bitmap); view.contentDescription="عکس واقعی ${brand} ${model} از ویکی‌مدیا" } }
            } catch (_:Exception) { /* illustration remains as an offline fallback */ }
        }.start()
    }

    private fun marketPriceCard(car: JSONObject) {
        val model=car.optString("model"); val year=normalizeDigits(car.optString("year")).toIntOrNull() ?: 1400
        val km=normalizeDigits(car.optString("km","0")).filter { it.isDigit() }.toLongOrNull() ?: 0L
        val baseline:Long?=when {
            model.contains("۲۰۷") || model.contains("207") -> if(model.contains("اتومات")) 3_700_000_000L else 2_600_000_000L
            model.contains("دنا") -> if(model.contains("اتومات")) 3_750_000_000L else 2_850_000_000L
            model.contains("تارا") -> if(model.contains("اتومات")) 3_400_000_000L else 2_850_000_000L
            model.contains("شاهین") -> if(model.contains("اتومات")) 2_900_000_000L else 2_300_000_000L
            model.contains("کوییک") -> 1_600_000_000L
            model.contains("ساینا") -> 1_650_000_000L
            model.contains("ری‌را") -> 4_700_000_000L
            model.contains("سورن") -> 2_600_000_000L
            model.contains("رانا") -> 2_200_000_000L
            model.contains("X22") -> 3_300_000_000L
            else -> null
        }
        card(content) {
            addView(label("برآورد ارزش بازار خودرو",15f,ink,true))
            if(baseline==null) addView(label("برای این مدل داده مرجع کافی نداریم؛ استعلام آگهی‌های روز را باز کن.",12f,muted,false).apply { setPadding(0,dp(6),0,dp(6)) }) else {
                val age=(1405-year).coerceAtLeast(0); val factor=(1.0-age*0.035-km/1000000.0*0.10).coerceIn(0.55,1.0); val estimate=(baseline*factor).toLong(); val low=(estimate*0.92).toLong(); val high=(estimate*1.08).toLong()
                addView(label("${money(low)} تا ${money(high)} تومان",20f,teal,true).apply { setPadding(0,dp(8),0,dp(3)) })
                addView(label("برآورد تقریبی بر پایه مدل، سال ساخت و کارکرد؛ نه قیمت قطعی معامله. وضعیت بدنه، تیپ، گیربکس و شهر می‌تواند اختلاف زیادی ایجاد کند.",11f,muted,false))
            }
            actionButtonIn(this,"استعلام قیمت‌های روز در وب",false) { try { startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+Uri.encode("قیمت روز بازار ${model} ${car.optString("year")}")))) } catch (_:Exception) { toast("مرورگر در دسترس نیست") } }
        }
    }

    // Local fallback illustration; a real Wikimedia Commons photo replaces it when online.
    private fun setCarIllustration(view: ImageView, model: String, brand: String) {
        val width = 720
        val height = 400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val hueColor = when {
            brand.contains("سایپا") -> Color.rgb(44, 116, 139)
            brand.contains("MVM") || brand.contains("فونیکس") || brand.contains("چری") -> Color.rgb(113, 89, 159)
            brand.contains("بهمن") || brand.contains("کرمان") -> Color.rgb(28, 125, 116)
            model.contains("207") || model.contains("۲۰۷") || model.contains("206") -> Color.rgb(24, 113, 131)
            else -> Color.rgb(41, 89, 111)
        }
        paint.shader = LinearGradient(0f, 0f, width.toFloat(), height.toFloat(), intArrayOf(Color.rgb(235, 246, 246), Color.rgb(213, 230, 235)), null, Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
        paint.color = Color.argb(30, 255, 255, 255)
        canvas.drawCircle(585f, 75f, 94f, paint)
        canvas.drawCircle(100f, 60f, 48f, paint)
        paint.color = Color.rgb(194, 213, 218)
        canvas.drawRoundRect(RectF(0f, 314f, 720f, 400f), 0f, 0f, paint)
        paint.color = Color.rgb(148, 171, 179)
        canvas.drawOval(RectF(105f, 300f, 610f, 355f), paint)
        // Car body silhouette
        val body = Path().apply {
            moveTo(110f, 274f); lineTo(142f, 233f); quadTo(164f, 218f, 222f, 214f)
            lineTo(286f, 158f); quadTo(309f, 139f, 352f, 139f); lineTo(445f, 144f)
            quadTo(473f, 147f, 498f, 176f); lineTo(546f, 214f); lineTo(590f, 226f)
            quadTo(613f, 234f, 615f, 257f); lineTo(612f, 282f); quadTo(606f, 296f, 584f, 296f)
            lineTo(147f, 296f); quadTo(118f, 296f, 110f, 274f); close()
        }
        paint.color = hueColor
        canvas.drawPath(body, paint)
        // windows
        val windows = Path().apply {
            moveTo(248f, 208f); lineTo(300f, 164f); quadTo(312f, 154f, 337f, 154f)
            lineTo(368f, 154f); lineTo(368f, 208f); close()
            moveTo(382f, 154f); lineTo(440f, 158f); quadTo(459f, 161f, 474f, 181f)
            lineTo(496f, 208f); lineTo(382f, 208f); close()
        }
        paint.color = Color.rgb(205, 229, 234); canvas.drawPath(windows, paint)
        paint.color = Color.argb(120, 255, 255, 255); canvas.drawRect(373f, 151f, 379f, 211f, paint)
        paint.color = Color.rgb(245, 250, 250); canvas.drawRoundRect(RectF(128f, 246f, 170f, 261f), 7f, 7f, paint)
        paint.color = Color.rgb(255, 202, 117); canvas.drawRoundRect(RectF(574f, 248f, 602f, 261f), 5f, 5f, paint)
        paint.color = Color.rgb(35, 47, 55)
        canvas.drawCircle(218f, 290f, 43f, paint); canvas.drawCircle(511f, 290f, 43f, paint)
        paint.color = Color.rgb(197, 211, 215)
        canvas.drawCircle(218f, 290f, 23f, paint); canvas.drawCircle(511f, 290f, 23f, paint)
        paint.color = Color.rgb(110, 132, 141); canvas.drawCircle(218f, 290f, 8f, paint); canvas.drawCircle(511f, 290f, 8f, paint)
        paint.color = Color.argb(150, 255, 255, 255)
        canvas.drawRoundRect(RectF(175f, 274f, 540f, 280f), 3f, 3f, paint)
        view.setImageBitmap(bitmap)
        view.scaleType = ImageView.ScaleType.CENTER_CROP
        view.contentDescription = "تصویرسازی گرافیکی خودرو $model"
    }

    private fun card(parent: LinearLayout, contentBlock: LinearLayout.() -> Unit) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(15), dp(16), dp(15)); background = rounded(Color.WHITE, 18); elevation = dp(1).toFloat(); contentBlock() }
        parent.addView(box, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
    }
    private fun formBox() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
    private fun actionButton(title: String, primary: Boolean, action: () -> Unit) {
        val b = TextView(this).apply { text = title; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setTextColor(if (primary) Color.WHITE else teal); background = rounded(if (primary) teal else Color.WHITE, 14); setPadding(dp(14), dp(14), dp(14), dp(14)); setOnClickListener { action() } }
        content.addView(b, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(9) })
    }
    private fun actionButtonIn(parent: LinearLayout, title: String, primary: Boolean, action: () -> Unit) {
        val b = TextView(this).apply { text = title; textSize = 13f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setTextColor(if (primary) Color.WHITE else teal); background = rounded(if (primary) teal else accent, 13); setPadding(dp(12), dp(12), dp(12), dp(12)); setOnClickListener { action() } }
        parent.addView(b, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(12) })
    }
    private fun sectionTitle(parent: LinearLayout, text: String) { parent.addView(label(text, 18f, ink, true).apply { setPadding(dp(2), dp(8), dp(2), dp(10)) }) }
    private fun emptyState(parent: LinearLayout, title: String, subtitle: String) { card(parent) { addView(label(title, 15f, ink, true)); addView(label(subtitle, 12f, muted, false).apply { setPadding(0, dp(7), 0, 0) }) } }
    private fun bullet(parent: LinearLayout, text: String) { parent.addView(label("✓  $text", 13f, ink, false).apply { setPadding(dp(4), dp(7), dp(4), dp(7)) }) }
    private fun metric(parent: LinearLayout, caption: String, value: String) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(4), dp(13), dp(4), dp(13)); background = rounded(Color.WHITE, 14); addView(label(value, 16f, teal, true)); addView(label(caption, 10f, muted, false).apply { setPadding(0, dp(5), 0, 0); gravity = Gravity.CENTER }) }
        parent.addView(box, LinearLayout.LayoutParams(0, -1, 1f).apply { marginStart = dp(3); marginEnd = dp(3); bottomMargin = dp(12) })
    }
    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply { this.text = text; textSize = size; setTextColor(color); gravity = Gravity.START; if (bold) typeface = Typeface.DEFAULT_BOLD; vazirTypeface?.let { typeface = it } }
    private fun field(hintText: String, initial: String = "", numeric: Boolean = false): EditText = EditText(this).apply {
        hint = hintText; setText(initial); textSize = 14f; vazirTypeface?.let { typeface = it }; isSingleLine = true; setTextColor(ink); setHintTextColor(muted); layoutDirection = View.LAYOUT_DIRECTION_RTL
        if (numeric) inputType = InputType.TYPE_CLASS_NUMBER
        setPadding(dp(14), dp(12), dp(14), dp(12))
        background = rounded(Color.rgb(248, 250, 251), 12)
        // subtle border via padding look
        setPadding(dp(14), dp(12), dp(14), dp(12))
    }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun money(value: Long): String = NumberFormat.getNumberInstance(Locale("fa", "IR")).format(value)
    private fun moneyShort(value: Long): String = when { value >= 1_000_000_000L -> "${money(value / 1_000_000_000L)} میلیارد"; value >= 1_000_000L -> "${money(value / 1_000_000L)} م.ت"; else -> money(value) }
    private fun normalizeDigits(input: String): String {
        val persian = "۰۱۲۳۴۵۶۷۸۹"; val arabic = "٠١٢٣٤٥٦٧٨٩"
        return input.map { ch -> when { ch in persian -> ('0'.code + persian.indexOf(ch)).toChar(); ch in arabic -> ('0'.code + arabic.indexOf(ch)).toChar(); else -> ch } }.joinToString("")
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
