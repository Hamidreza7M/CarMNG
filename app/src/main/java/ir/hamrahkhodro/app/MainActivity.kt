package ir.hamrahkhodro.app

import android.app.Activity
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import kotlin.concurrent.thread

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

    data class CarModel(val brand: String, val name: String, val image: String = "")
    private val carModels = listOf(
        CarModel("ایران‌خودرو", "پژو ۲۰۷ دنده‌ای", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "پژو ۲۰۷ اتوماتیک", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "پژو ۲۰۶ تیپ ۲", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "پژو پارس", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "دنا پلاس", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "تارا دنده‌ای", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "تارا اتوماتیک", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "سورن پلاس", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "رانا پلاس", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "سمند LX", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "پژو ۴۰۵", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "پژو پارس ELX", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("ایران‌خودرو", "آریسان ۲", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "ری‌را", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "هایما S5", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("ایران‌خودرو", "هایما S7", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("سایپا", "شاهین", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "کوییک", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "کوییک S", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "ساینا S", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "اطلس", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "تیبا", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "تیبا ۲", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "پراید ۱۳۱", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "پراید ۱۱۱", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "سهند", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایپا", "سایپا ۱۵۱", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("MVM", "X22", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "X22 Pro", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "X33", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "X55 Pro", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "315", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "X33 Cross", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("MVM", "530 / 550", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("فونیکس", "Tiggo 7 Pro", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("فونیکس", "Arrizo 6 Pro", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("فونیکس", "FX", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("فونیکس", "Tiggo 8 Pro", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("چری", "Arrizo 5", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("بهمن", "دیگنیتی", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("بهمن", "فیدلیتی", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("بهمن", "رسپکت", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("بهمن", "مزدا ۳", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کرمان موتور", "J4", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کرمان موتور", "KMC T8", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کرمان موتور", "KMC J7", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کرمان موتور", "JAC S3", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کرمان موتور", "JAC S5", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("پارس‌خودرو", "رنو تندر ۹۰", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("پارس‌خودرو", "رنو ساندرو", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("رنو", "داستر", "https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_207i.jpg"),
        CarModel("تویوتا", "کرولا", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("تویوتا", "کمری", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("هیوندای", "توسان", "https://commons.wikimedia.org/wiki/Special:FilePath/MVM_X22_01_2024-07-31.jpg"),
        CarModel("کیا", "سراتو", "https://commons.wikimedia.org/wiki/Special:FilePath/IKCO_Tara_AT_01_2024-05-02.jpg"),
        CarModel("پارس‌خودرو", "کوییک (مونتاژ/عرضه سایپا)", "https://commons.wikimedia.org/wiki/Special:FilePath/Saipa_Shahin_01_2023-07-03.jpg"),
        CarModel("سایر", "خودروی دیگر / ورود دستی")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = deep
        window.navigationBarColor = page
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
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
        val logo = TextView(this).apply {
            text = "M"; textSize = 34f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER; setTextColor(Color.WHITE)
            background = rounded(teal, 24); layoutParams = LinearLayout.LayoutParams(dp(78), dp(78)).apply { bottomMargin = dp(20) }
        }
        body.addView(logo)
        body.addView(label("MYCAR", 30f, deep, true).apply { gravity = Gravity.CENTER })
        body.addView(label("همراه هوشمند نگهداری خودرو", 15f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(8), 0, dp(26)) })
        card(body) {
            addView(label("ورود یا ثبت‌نام", 20f, ink, true))
            addView(label("برای شروع، شماره موبایل ایران را وارد کن.", 13f, muted, false).apply { setPadding(0, dp(8), 0, dp(16)) })
            val phone = field("شماره موبایل، مثل 09123456789", "", true).apply { inputType = InputType.TYPE_CLASS_PHONE }
            addView(phone)
            actionButtonIn(this, "دریافت کد تأیید", true) {
                val number = normalizeDigits(phone.text.toString()).filter { it.isDigit() }
                if (number.matches(Regex("09[0-9]{9}"))) showOtp(number) else toast("شماره موبایل باید ۱۱ رقم و با ۰۹ شروع شود")
            }
            addView(label("نسخه آزمایشی: پیامک واقعی ارسال نمی‌شود؛ کد تست 12345 است.", 12f, muted, false).apply { setPadding(0, dp(10), 0, 0) })
        }
        body.addView(label("اطلاعات این نسخه روی همین گوشی ذخیره می‌شود.", 12f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(22), 0, 0) })
        scroller.addView(body); root.addView(scroller); setContentView(root)
    }

    private fun showOtp(phone: String) {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(22), dp(8), dp(22), 0) }
        box.addView(label("کد تأیید برای $phone", 15f, ink, true).apply { setPadding(0, 0, 0, dp(8)) })
        val otp = field("کد ۵ رقمی", "", true).apply { inputType = InputType.TYPE_CLASS_NUMBER; gravity = Gravity.CENTER; textSize = 22f }
        box.addView(otp)
        AlertDialog.Builder(this).setTitle("تأیید شماره موبایل").setView(box).setCancelable(false)
            .setPositiveButton("تأیید", null).setNegativeButton("بازگشت") { _, _ -> showPhoneLogin() }.create().also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        if (normalizeDigits(otp.text.toString()).filter { it.isDigit() } == "12345") {
                            prefs.edit().putBoolean("logged_in", true).putString("phone", phone).apply()
                            dialog.dismiss(); showApp()
                        } else toast("کد واردشده صحیح نیست؛ کد تست 12345 است")
                    }
                }
                dialog.show()
            }
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
        setContentView(root)
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
                loadCarImage(selected.optString("image"), img)
                val info = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
                info.addView(label(selected.optString("model"), 17f, ink, true))
                info.addView(label("${selected.optString("brand")} • ${selected.optString("year", "سال نامشخص")}", 12f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
                info.addView(label("کارکرد: ${selected.optString("km", "۰")} کیلومتر", 12f, teal, true).apply { setPadding(0, dp(4), 0, 0) })
                line.addView(info); addView(line)
                actionButtonIn(this, "تغییر خودروی فعال", false) { chooseActiveCar() }
            }
            val id = selected.optString("id")
            val histories = load("history").filter { it.optString("vehicleId") == id }
            val expenses = load("expenses").filter { it.optString("vehicleId") == id }
            val total = expenses.sumOf { it.optLong("amount", 0L) }
            val metrics = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            metric(metrics, "سوابق", histories.size.toString()); metric(metrics, "هزینه کل", moneyShort(total)); metric(metrics, "خودروها", cars.size.toString())
            content.addView(metrics)
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
                row.addView(image, LinearLayout.LayoutParams(dp(108), dp(82))); loadCarImage(car.optString("image"), image)
                val info = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0); layoutParams = LinearLayout.LayoutParams(0, -2, 1f) }
                info.addView(label(car.optString("model"), 16f, ink, true))
                info.addView(label("${car.optString("brand")} • ${car.optString("year", "سال نامشخص")}", 12f, muted, false).apply { setPadding(0, dp(4), 0, 0) })
                info.addView(label("${car.optString("km", "۰")} کیلومتر", 12f, teal, true).apply { setPadding(0, dp(4), 0, 0) })
                row.addView(info); addView(row)
                actionButtonIn(this, if (car.optString("id") == prefs.getString("active_car", "")) "خودروی فعال ✓" else "انتخاب به‌عنوان خودروی فعال", false) { prefs.edit().putString("active_car", car.optString("id")).apply(); tab = "خانه"; showApp() }
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
        box.addView(label("برند خودرو", 12f, muted, true)); box.addView(brandSpinner)
        box.addView(label("مدل خودرو", 12f, muted, true).apply { setPadding(0, dp(8), 0, 0) }); box.addView(modelSpinner)
        box.addView(year); box.addView(km); box.addView(plate)
        AlertDialog.Builder(this).setTitle("افزودن خودرو به MYCAR").setView(box).setNegativeButton("انصراف", null).setPositiveButton("افزودن") { _, _ ->
            val brand = brandSpinner.selectedItem?.toString() ?: "سایر"
            val model = modelSpinner.selectedItem?.toString() ?: "خودروی دیگر"
            val selected = carModels.firstOrNull { it.brand == brand && it.name == model }
            val obj = JSONObject().put("id", UUID.randomUUID().toString()).put("brand", brand).put("model", model)
                .put("year", normalizeDigits(year.text.toString())).put("km", normalizeDigits(km.text.toString()).filter { it.isDigit() }.ifBlank { "0" })
                .put("plate", plate.text.toString()).put("image", selected?.image ?: "").put("created", System.currentTimeMillis())
            save("cars", obj)
            if (prefs.getString("active_car", "").isNullOrBlank()) prefs.edit().putString("active_car", obj.optString("id")).apply()
            tab = "خودروها"; showApp()
        }.show()
    }

    private fun chooseActiveCar() {
        val cars = load("cars")
        if (cars.isEmpty()) return addVehicle()
        AlertDialog.Builder(this).setTitle("انتخاب خودرو").setItems(cars.map { it.optString("model") }.toTypedArray()) { _, which ->
            prefs.edit().putString("active_car", cars[which].optString("id")).apply(); showApp()
        }.show()
    }

    private fun deleteCar(car: JSONObject) {
        val id = car.optString("id")
        saveList("cars", load("cars").filterNot { it.optString("id") == id })
        listOf("history", "expenses", "services").forEach { key -> saveList(key, load(key).filterNot { it.optString("vehicleId") == id }) }
        if (prefs.getString("active_car", "") == id) prefs.edit().putString("active_car", load("cars").firstOrNull()?.optString("id") ?: "").apply()
    }

    private fun selectedCar(): JSONObject? {
        val cars = load("cars")
        val id = prefs.getString("active_car", "") ?: ""
        return cars.firstOrNull { it.optString("id") == id } ?: cars.firstOrNull()?.also { prefs.edit().putString("active_car", it.optString("id")).apply() }
    }

    private fun showMaintenancePlan(car: JSONObject) {
        val model = car.optString("model")
        val is207 = model.contains("۲۰۷") || model.contains("207")
        val title = if (is207) "برنامه پیشنهادی پژو ۲۰۷" else "برنامه پیشنهادی ${model}"
        val plan = if (is207) listOf(
            "هر ۱۰٬۰۰۰ کیلومتر یا ۱ سال (هرکدام زودتر): سرویس دوره‌ای و تعویض روغن موتور و فیلتر روغن",
            "در بازدیدهای دوره‌ای: بررسی فیلتر هوا و تعویض طبق وضعیت و دفترچه خودرو",
            "در بازدیدهای دوره‌ای: بررسی ترمز، لاستیک‌ها، مایعات، نشتی‌ها و وضعیت باتری",
            "شمع، تسمه‌ها، مایع خنک‌کننده و روغن گیربکس: مطابق جدول دقیق تیپ موتور و دفترچه همان خودرو"
        ) else listOf(
            "بررسی دفترچه همان تیپ خودرو؛ برنامه سازنده ممکن است با موتور و سال ساخت تفاوت داشته باشد.",
            "برای نسخه راهنمای اولیه: بازدید ایمنی، لاستیک، ترمز، نشتی‌ها و سطح مایعات به‌صورت دوره‌ای.",
            "روغن و فیلترها را فقط بر اساس بازه معتبر همان مدل و شرایط کارکرد ثبت کن.",
            "برای قطعات مصرفی، از بازه‌های عمومی به‌عنوان جایگزین دفترچه سازنده استفاده نکن."
        )
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        box.addView(label("${car.optString("brand")} • ${car.optString("model")}", 14f, teal, true))
        plan.forEachIndexed { i, item -> box.addView(label("${i + 1}. $item", 13f, ink, false).apply { setPadding(0, dp(10), 0, dp(2)) }) }
        box.addView(label("منبع راهنمای ۲۰۷: جدول سرویس‌های دوره‌ای منتشرشده برای ۲۰۷i موتور TU5/TU5P و محتوای راهنمای سرویس ایران‌خودرو. برنامه دقیق به تیپ و سال ساخت وابسته است. این فهرست راهنمای اولیه است؛ دفترچه مالک خودرو اولویت دارد.", 11f, muted, false).apply { setPadding(0, dp(12), 0, dp(4)) })
        AlertDialog.Builder(this).setTitle(title).setView(box).setPositiveButton("افزودن یادآوری ۱۰هزار کیلومتر") { _, _ ->
            val currentKm = normalizeDigits(car.optString("km", "0")).filter { it.isDigit() }.toIntOrNull() ?: 0
            val nextKm = ((currentKm / 10000) + 1) * 10000
            save("services", JSONObject().put("id", UUID.randomUUID().toString()).put("vehicleId", car.optString("id"))
                .put("title", "سرویس دوره‌ای و روغن موتور").put("km", nextKm.toString()).put("date", "")
                .put("note", "بازه پیشنهادی اولیه؛ دفترچه خودرو را بررسی کن").put("created", System.currentTimeMillis()))
            toast("یادآوری اضافه شد")
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
        items.forEach { itemCard(content, it, "service") }
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
        val items = load("expenses").filter { it.optString("vehicleId") == car.optString("id") }
        val total = items.sumOf { it.optLong("amount", 0L) }
        card(content) {
            addView(label("مجموع هزینه‌های ${car.optString("model")}", 13f, muted, true))
            addView(label("${money(total)} تومان", 24f, teal, true).apply { setPadding(0, dp(7), 0, 0) })
        }
        sectionTitle(content, "ریز هزینه‌ها")
        actionButton("＋ ثبت هزینه جدید", true) { addExpense() }
        if (items.isEmpty()) emptyState(content, "هنوز هزینه‌ای ثبت نشده", "هزینه بنزین، تعمیر، بیمه و سرویس را ثبت کن.")
        items.forEach { itemCard(content, it, "expense") }
    }

    private fun profileScreen() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        box.addView(label("شماره موبایل", 12f, muted, true))
        box.addView(label(prefs.getString("phone", "") ?: "", 18f, ink, true).apply { setPadding(0, dp(6), 0, dp(14)) })
        box.addView(label("حساب این نسخه آزمایشی روی دستگاه ذخیره می‌شود و پیامک واقعی ارسال نمی‌شود.", 12f, muted, false))
        box.addView(label("منابع و مجوز عکس‌ها", 14f, ink, true).apply { setPadding(0, dp(16), 0, dp(6)) })
        box.addView(label("پژو ۲۰۷i: Telluride749 / Wikimedia Commons (CC BY-SA 4.0). MVM X22 و سایپا شاهین: قندون / Wikimedia Commons (CC BY یا CC BY-SA). تارا: Wikimedia Commons (CC BY 4.0). جزئیات و پیوند هر فایل در README پروژه است.", 11f, muted, false))
        AlertDialog.Builder(this).setTitle("پروفایل MYCAR").setView(box).setNegativeButton("بستن", null).setNeutralButton("خروج از حساب") { _, _ -> prefs.edit().putBoolean("logged_in", false).apply(); showPhoneLogin() }.show()
    }

    private fun addService() {
        val car = selectedCar() ?: run { toast("ابتدا خودرو اضافه کن"); addVehicle(); return }
        val box = formBox()
        val title = field("عنوان یادآوری (مثلاً روغن موتور)")
        val dueKm = field("کیلومتر یادآوری (اختیاری)", "", true)
        val dueDate = field("تاریخ یادآوری شمسی (اختیاری)", "مثلاً ۱۴۰۵/۰۹/۱۵")
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
        val date = field("تاریخ انجام (شمسی)", "۱۴۰۵/۰۸/۰۷")
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
        val category = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, listOf("سوخت", "سرویس و تعمیر", "بیمه و عوارض", "پارکینگ و کارواش", "سایر")) }
        val amount = field("مبلغ به تومان", "", true); val date = field("تاریخ شمسی", "۱۴۰۵/۰۸/۰۷"); val place = field("محل خرید یا انجام (اختیاری)")
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

    private fun loadCarImage(url: String, view: ImageView) {
        if (url.isBlank()) { view.setImageResource(android.R.drawable.ic_menu_gallery); return }
        val fileName = "car_" + url.substringAfterLast("/").replace(Regex("[^A-Za-z0-9._-]"), "_") + ".img"
        val file = File(File(filesDir, "car-images").apply { mkdirs() }, fileName)
        fun setBitmap(file: File) { try { val bitmap = BitmapFactory.decodeFile(file.absolutePath); if (bitmap != null) runOnUiThread { view.setImageBitmap(bitmap) } } catch (_: Exception) {} }
        if (file.exists() && file.length() > 0) { setBitmap(file); return }
        thread {
            try {
                val conn = URL(url).openConnection(); conn.connectTimeout = 10000; conn.readTimeout = 15000
                conn.setRequestProperty("User-Agent", "MYCAR Android App")
                conn.getInputStream().use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                setBitmap(file)
            } catch (_: Exception) { runOnUiThread { view.setImageResource(android.R.drawable.ic_menu_gallery) } }
        }
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
    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply { this.text = text; textSize = size; setTextColor(color); gravity = Gravity.START; if (bold) typeface = Typeface.DEFAULT_BOLD }
    private fun field(hintText: String, initial: String = "", numeric: Boolean = false): EditText = EditText(this).apply {
        hint = hintText; setText(initial); textSize = 14f; isSingleLine = true; setTextColor(ink); setHintTextColor(muted); layoutDirection = View.LAYOUT_DIRECTION_RTL
        if (numeric) inputType = InputType.TYPE_CLASS_NUMBER
        setPadding(dp(10), dp(5), dp(10), dp(5))
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
