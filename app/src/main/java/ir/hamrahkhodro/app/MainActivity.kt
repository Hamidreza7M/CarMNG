package ir.hamrahkhodro.app

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("hamrah_khodro", MODE_PRIVATE) }
    private val teal = Color.rgb(7, 107, 116)
    private val ink = Color.rgb(30, 45, 52)
    private val muted = Color.rgb(105, 120, 126)
    private val page = Color.rgb(244, 247, 248)
    private lateinit var root: LinearLayout
    private lateinit var activeContent: LinearLayout
    private var currentTab = "خانه"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(5, 91, 99)
        window.navigationBarColor = page
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        showScreen()
    }

    private fun showScreen() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(page)
            layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(18))
            setBackgroundColor(Color.rgb(5, 91, 99))
        }
        header.addView(label("همراه خودرو", 23f, Color.WHITE, true))
        header.addView(label("دستیار نگهداری و هزینه‌های خودرو", 13f, Color.rgb(215, 239, 239), false).apply {
            setPadding(0, dp(4), 0, 0)
        })
        root.addView(header)

        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
            setBackgroundColor(Color.WHITE)
        }
        listOf("خانه", "سرویس‌ها", "سوابق", "هزینه‌ها").forEach { tab ->
            val b = TextView(this).apply {
                text = tab
                gravity = Gravity.CENTER
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(if (tab == currentTab) Color.WHITE else muted)
                background = rounded(if (tab == currentTab) teal else Color.WHITE, 12)
                setPadding(dp(5), dp(12), dp(5), dp(12))
                setOnClickListener { currentTab = tab; showScreen() }
            }
            tabs.addView(b, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = dp(3); marginEnd = dp(3)
            })
        }
        root.addView(tabs)

        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(24))
        }
        scroll.addView(content)
        activeContent = content
        when (currentTab) {
            "خانه" -> home(content)
            "سرویس‌ها" -> serviceTab(content)
            "سوابق" -> historyTab(content)
            "هزینه‌ها" -> expensesTab(content)
        }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun home(c: LinearLayout) {
        val vehicle = prefs.getString("vehicle", "پژو ۲۰۷ دنده‌ای") ?: "پژو ۲۰۷ دنده‌ای"
        val year = prefs.getString("year", "۱۴۰۱") ?: "۱۴۰۱"
        val km = prefs.getString("km", "۶۵٬۰۰۰") ?: "۶۵٬۰۰۰"
        card(c) {
            addView(label("خودروی من", 13f, muted, true))
            addView(label(vehicle, 22f, ink, true).apply { setPadding(0, dp(6), 0, dp(2)) })
            addView(label("مدل سال $year  •  کیلومتر فعلی: $km", 13f, muted, false))
            actionButton("ویرایش اطلاعات خودرو", false) { editVehicle() }
        }
        val services = load("services")
        val history = load("history")
        val expenses = load("expenses")
        val total = expenses.sumOf { it.optLong("amount", 0L) }
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        metric(row, "سرویس ثبت‌شده", services.size.toString())
        metric(row, "تعمیرات", history.size.toString())
        metric(row, "هزینه ثبت‌شده", moneyShort(total))
        c.addView(row)
        sectionTitle(c, "دسترسی سریع")
        actionButton("＋ ثبت سرویس یا یادآوری", true) { addService() }
        actionButton("＋ ثبت تعمیر یا سرویس انجام‌شده", false) { addHistory() }
        actionButton("＋ ثبت هزینه خودرو", false) { addExpense() }
        sectionTitle(c, "یادآوری‌های پیش رو")
        if (services.isEmpty()) {
            emptyState(c, "هنوز یادآوری‌ای ثبت نشده است", "برای تعویض روغن، فیلتر، لنت یا بیمه یک یادآوری بساز.")
        } else {
            services.take(3).forEach { itemCard(c, it, "service") }
        }
        sectionTitle(c, "آخرین سوابق")
        if (history.isEmpty()) emptyState(c, "هنوز سابقه‌ای ثبت نشده", "تعمیرات و سرویس‌های انجام‌شده را ثبت کن تا پرونده خودرو کامل شود.")
        else history.take(3).forEach { itemCard(c, it, "history") }
    }

    private fun serviceTab(c: LinearLayout) {
        sectionTitle(c, "سرویس‌ها و یادآوری‌ها")
        actionButton("＋ افزودن یادآوری سرویس", true) { addService() }
        val items = load("services")
        if (items.isEmpty()) emptyState(c, "لیست سرویس خالی است", "مثلاً تعویض روغن در کیلومتر ۷۰٬۰۰۰ را ثبت کن.")
        items.forEach { itemCard(c, it, "service") }
    }

    private fun historyTab(c: LinearLayout) {
        sectionTitle(c, "تاریخچه تعمیرات")
        actionButton("＋ ثبت تعمیر یا سرویس", true) { addHistory() }
        val items = load("history")
        if (items.isEmpty()) emptyState(c, "هنوز سابقه‌ای وجود ندارد", "سوابق تعمیرگاه و قطعات تعویض‌شده را اینجا نگه دار.")
        items.forEach { itemCard(c, it, "history") }
    }

    private fun expensesTab(c: LinearLayout) {
        val items = load("expenses")
        val total = items.sumOf { it.optLong("amount", 0L) }
        card(c) {
            addView(label("مجموع هزینه‌های ثبت‌شده", 13f, muted, true))
            addView(label("${money(total)} تومان", 24f, teal, true).apply { setPadding(0, dp(6), 0, 0) })
            addView(label("این مبلغ بر اساس مواردی است که خودت ثبت کرده‌ای.", 12f, muted, false))
        }
        sectionTitle(c, "هزینه‌ها")
        actionButton("＋ ثبت هزینه جدید", true) { addExpense() }
        if (items.isEmpty()) emptyState(c, "هنوز هزینه‌ای ثبت نشده", "هزینه بنزین، تعمیر، بیمه و سرویس را ثبت کن.")
        items.forEach { itemCard(c, it, "expense") }
    }

    private fun editVehicle() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val name = field("مدل خودرو", prefs.getString("vehicle", "پژو ۲۰۷ دنده‌ای") ?: "پژو ۲۰۷ دنده‌ای")
        val year = field("سال ساخت", prefs.getString("year", "۱۴۰۱") ?: "۱۴۰۱")
        val km = field("کیلومتر فعلی", prefs.getString("km", "۶۵٬۰۰۰") ?: "۶۵٬۰۰۰", false)
        box.addView(name); box.addView(year); box.addView(km)
        AlertDialog.Builder(this).setTitle("اطلاعات خودرو").setView(box)
            .setNegativeButton("انصراف", null).setPositiveButton("ذخیره") { _, _ ->
                prefs.edit().putString("vehicle", name.text.toString().ifBlank { "خودروی من" })
                    .putString("year", year.text.toString()).putString("km", km.text.toString()).apply()
                showScreen()
            }.show()
    }

    private fun addService() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val title = field("عنوان سرویس (مثلاً تعویض روغن)")
        val dueKm = field("کیلومتر یادآوری (اختیاری)", "مثلاً ۷۰۰۰۰", false)
        val dueDate = field("تاریخ یادآوری (اختیاری)", "مثلاً ۱۴۰۵/۰۹/۱۵")
        val note = field("توضیحات (اختیاری)")
        listOf(title, dueKm, dueDate, note).forEach { box.addView(it) }
        AlertDialog.Builder(this).setTitle("یادآوری سرویس جدید").setView(box)
            .setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
                if (title.text.isNotBlank()) {
                    save("services", JSONObject().put("title", title.text.toString()).put("km", dueKm.text.toString())
                        .put("date", dueDate.text.toString()).put("note", note.text.toString()).put("created", System.currentTimeMillis()))
                    showScreen()
                } else toast("عنوان سرویس را وارد کن")
            }.show()
    }

    private fun addHistory() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val title = field("عنوان تعمیر یا سرویس")
        val date = field("تاریخ", "مثلاً ۱۴۰۵/۰۸/۰۷")
        val km = field("کیلومتر خودرو", "مثلاً ۶۵۰۰۰", false)
        val amount = field("هزینه به تومان (اختیاری)", "مثلاً ۱۵۰۰۰۰۰", false)
        val details = field("قطعات یا توضیحات")
        listOf(title, date, km, amount, details).forEach { box.addView(it) }
        AlertDialog.Builder(this).setTitle("ثبت تعمیر یا سرویس انجام‌شده").setView(box)
            .setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
                if (title.text.isNotBlank()) {
                    val cost = amount.text.toString().toLongOrNull() ?: 0L
                    save("history", JSONObject().put("title", title.text.toString()).put("date", date.text.toString())
                        .put("km", km.text.toString()).put("amount", cost).put("note", details.text.toString()).put("created", System.currentTimeMillis()))
                    if (cost > 0) save("expenses", JSONObject().put("title", title.text.toString()).put("category", "تعمیر و سرویس")
                        .put("amount", cost).put("date", date.text.toString()).put("created", System.currentTimeMillis()))
                    showScreen()
                } else toast("عنوان تعمیر را وارد کن")
            }.show()
    }

    private fun addExpense() {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), 0) }
        val title = field("عنوان هزینه (مثلاً بنزین)")
        val category = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                listOf("سوخت", "تعمیر و سرویس", "بیمه و عوارض", "پارکینگ و کارواش", "سایر"))
        }
        val amount = field("مبلغ به تومان", "مثلاً ۳۰۰۰۰۰", false)
        val date = field("تاریخ", "مثلاً ۱۴۰۵/۰۸/۰۷")
        box.addView(title); box.addView(category); box.addView(amount); box.addView(date)
        AlertDialog.Builder(this).setTitle("ثبت هزینه خودرو").setView(box)
            .setNegativeButton("انصراف", null).setPositiveButton("ثبت") { _, _ ->
                val cost = amount.text.toString().replace(",", "").replace("٬", "").toLongOrNull()
                if (title.text.isBlank() || cost == null || cost <= 0) toast("عنوان و مبلغ معتبر وارد کن")
                else {
                    save("expenses", JSONObject().put("title", title.text.toString()).put("category", category.selectedItem.toString())
                        .put("amount", cost).put("date", date.text.toString()).put("created", System.currentTimeMillis()))
                    showScreen()
                }
            }.show()
    }

    private fun itemCard(parent: LinearLayout, item: JSONObject, kind: String) {
        card(parent) {
            val title = item.optString("title", "بدون عنوان")
            addView(label(title, 16f, ink, true))
            val detail = when (kind) {
                "service" -> listOfNotNull(item.optString("km").takeIf { it.isNotBlank() }?.let { "کیلومتر: $it" },
                    item.optString("date").takeIf { it.isNotBlank() }?.let { "تاریخ: $it" },
                    item.optString("note").takeIf { it.isNotBlank() }).joinToString("  •  ")
                "history" -> listOfNotNull(item.optString("date").takeIf { it.isNotBlank() }?.let { "تاریخ: $it" },
                    item.optString("km").takeIf { it.isNotBlank() }?.let { "کیلومتر: $it" },
                    item.optString("note").takeIf { it.isNotBlank() }).joinToString("  •  ")
                else -> "${item.optString("category", "هزینه")}  •  ${money(item.optLong("amount", 0L))} تومان  •  ${item.optString("date", "")}" 
            }
            if (detail.isNotBlank()) addView(label(detail, 12f, muted, false).apply { setPadding(0, dp(6), 0, 0) })
            if (kind == "history" && item.optLong("amount", 0L) > 0) addView(label("هزینه: ${money(item.optLong("amount"))} تومان", 13f, teal, true).apply { setPadding(0, dp(6), 0, 0) })
            val delete = TextView(this@MainActivity).apply {
                text = "حذف"; textSize = 12f; setTextColor(Color.rgb(180, 65, 65)); gravity = Gravity.END
                setPadding(0, dp(10), 0, 0)
                setOnClickListener {
                    AlertDialog.Builder(this@MainActivity).setMessage("این مورد حذف شود؟")
                        .setNegativeButton("انصراف", null).setPositiveButton("حذف") { _, _ ->
                            remove(kindKey(kind), item); showScreen()
                        }.show()
                }
            }
            addView(delete)
        }
    }

    private fun kindKey(kind: String) = when (kind) { "service" -> "services"; "history" -> "history"; else -> "expenses" }

    private fun remove(key: String, item: JSONObject) {
        val old = load(key); val fresh = JSONArray()
        for (i in 0 until old.length()) {
            val candidate = old.optJSONObject(i) ?: continue
            if (candidate.optLong("created", -1L) != item.optLong("created", -2L)) fresh.put(candidate)
        }
        prefs.edit().putString(key, fresh.toString()).apply()
    }

    private fun load(key: String): List<JSONObject> {
        val arr = try { JSONArray(prefs.getString(key, "[]")) } catch (_: Exception) { JSONArray() }
        return (0 until arr.length()).mapNotNull { arr.optJSONObject(it) }.sortedByDescending { it.optLong("created", 0L) }
    }

    private fun save(key: String, value: JSONObject) {
        val arr = JSONArray(); load(key).forEach { arr.put(it) }; arr.put(value)
        prefs.edit().putString(key, arr.toString()).apply()
    }

    private fun card(parent: LinearLayout, content: LinearLayout.() -> Unit) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(15), dp(16), dp(15)); background = rounded(Color.WHITE, 18)
            elevation = dp(1).toFloat(); content()
        }
        parent.addView(box, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(12) })
    }

    private fun metric(parent: LinearLayout, caption: String, value: String) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(5), dp(13), dp(5), dp(13)); background = rounded(Color.WHITE, 14)
            addView(label(value, 18f, teal, true))
            addView(label(caption, 10f, muted, false).apply { setPadding(0, dp(5), 0, 0); gravity = Gravity.CENTER })
        }
        parent.addView(box, LinearLayout.LayoutParams(0, -1, 1f).apply { marginStart = dp(4); marginEnd = dp(4); bottomMargin = dp(12) })
    }

    private fun sectionTitle(parent: LinearLayout, title: String) {
        parent.addView(label(title, 17f, ink, true).apply { setPadding(dp(2), dp(10), dp(2), dp(10)) })
    }

    private fun emptyState(parent: LinearLayout, title: String, subtitle: String) {
        card(parent) {
            addView(label(title, 15f, ink, true))
            addView(label(subtitle, 12f, muted, false).apply { setPadding(0, dp(7), 0, 0) })
        }
    }

    private fun actionButton(title: String, primary: Boolean, action: () -> Unit) {
        val button = TextView(this).apply {
            text = title; textSize = 14f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            setTextColor(if (primary) Color.WHITE else teal)
            background = rounded(if (primary) teal else Color.WHITE, 14)
            setPadding(dp(14), dp(14), dp(14), dp(14)); setOnClickListener { action() }
        }
        activeContent.addView(button, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(9) })
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean): TextView = TextView(this).apply {
        this.text = text; textSize = size; setTextColor(color); gravity = Gravity.START
        if (bold) typeface = Typeface.DEFAULT_BOLD
    }

    private fun field(hintText: String, initial: String = "", numeric: Boolean = false): EditText = EditText(this).apply {
        hint = hintText; setText(initial); textSize = 14f; singleLine = true; setTextColor(ink); setHintTextColor(muted)
        layoutDirection = View.LAYOUT_DIRECTION_RTL
        if (numeric) inputType = android.text.InputType.TYPE_CLASS_NUMBER
        setPadding(dp(10), dp(5), dp(10), dp(5))
    }

    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply {
        setColor(color); cornerRadius = dp(radius).toFloat()
    }

    private fun money(value: Long): String = NumberFormat.getNumberInstance(Locale("fa", "IR")).format(value)
    private fun moneyShort(value: Long): String = when {
        value >= 1_000_000_000L -> "${money(value / 1_000_000_000L)} میلیارد"
        value >= 1_000_000L -> "${money(value / 1_000_000L)} م.ت"
        else -> money(value)
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
