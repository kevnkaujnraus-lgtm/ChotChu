package com.example.chotchu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/* ======================= BỎ DẤU TIẾNG VIỆT ======================= */

private const val S1 =
    "ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝàáâãèéêìíòóôõùúýĂăĐđĨĩŨũƠơƯưẠạẢảẤấẦầẨẩẪẫẬậẮắẰằẲẳẴẵẶặẸẹẺẻẼẽẾếỀềỂểỄễỆệỈỉỊịỌọỎỏỐốỒồỔổỖỗỘộỚớỜờỞởỠỡỢợỤụỦủỨứỪừỬửỮữỰựỲỳỴỵỶỷỸỹ"
private const val S0 =
    "AAAAEEEIIOOOOUUYaaaaeeeiiOOoouuyAaDdIiUuOoUuAaAaAaAaAaAaAaAaAaAaAaAaEeEeEeEeEeEeEeEeIiIiOoOoOoOoOoOoOoOoOoOoOoOoUuUuUuUuUuUuUuYyYyYyYy"

fun removeAccents(input: String): String {
    val sb = StringBuilder(input.length)
    for (c in input) {
        val i = S1.indexOf(c)
        sb.append(if (i >= 0) S0[i] else c)
    }
    return sb.toString().uppercase()
}

/* ======================= TỪ ĐIỂN ======================= */

val OFFLINE_DICTIONARY: Map<String, List<String>> = linkedMapOf(
    "Cơ thể người" to listOf(
        "Đầu", "Tóc", "Trán", "Mắt", "Mày", "Mi", "Mũi", "Má", "Miệng", "Môi",
        "Răng", "Lưỡi", "Lợi", "Cằm", "Tai", "Cổ", "Họng", "Thanh quản", "Não", "Hộp sọ",
        "Vai", "Ngực", "Vú", "Lưng", "Eo", "Bụng", "Rốn", "Hông", "Nách",
        "Tay", "Cánh tay", "Khuỷu tay", "Cổ tay", "Bàn tay", "Ngón tay", "Móng tay",
        "Chân", "Đùi", "Đầu gối", "Bắp chân", "Cổ chân", "Bàn chân", "Ngón chân", "Móng chân",
        "Da", "Thịt", "Mỡ", "Xương", "Xương sống", "Xương sườn", "Khớp", "Gân", "Cơ",
        "Máu", "Tim", "Gan", "Mật", "Phổi", "Thận", "Dạ dày", "Ruột",
        "Ruột non", "Ruột già", "Ruột thừa", "Tụy", "Bàng quang", "Tâm thu", "Tâm nhĩ"
    ),
    "Động vật" to listOf(
        "Chó", "Mèo", "Gà", "Vịt", "Ngỗng", "Trâu", "Bò", "Heo", "Lợn", "Dê", "Cừu", "Ngựa", "Thỏ",
        "Khỉ", "Vượn", "Đười ươi", "Hổ", "Sư tử", "Báo", "Voi", "Gấu", "Gấu trúc", "Hươu", "Nai",
        "Cáo", "Sói", "Sóc", "Nhím", "Tê giác", "Hà mã", "Hươu cao cổ", "Kangaroo", "Rái cá", "Chồn", "Dơi",
        "Chim sẻ", "Bồ câu", "Đại bàng", "Diều hâu", "Cú mèo", "Thiên nga", "Vịt trời", "Đà điểu",
        "Chim cánh cụt", "Vẹt", "Sáo", "Họa mi", "Chào mào", "Quạ", "Cò", "Vạc", "Bồ nông", "Yểng",
        "Cá chép", "Cá mè", "Cá trắm", "Cá trôi", "Cá rô", "Cá lóc", "Cá quả", "Cá trê", "Cá bống",
        "Cá ngừ", "Cá hồi", "Cá mập", "Cá voi", "Cá heo", "Cá đuối", "Cá ngựa", "Cá sấu",
        "Tôm", "Cua", "Ghẹ", "Mực", "Bạch tuộc", "Ốc", "Hến", "Trai", "Sò", "Ba ba", "Rùa",
        "Rắn", "Thằn lằn", "Thạch sùng", "Kỳ nhông", "Tắc kè", "Ếch", "Nhái", "Cóc",
        "Ong", "Kiến", "Bướm", "Muỗi", "Ruồi", "Gián", "Chuồn chuồn", "Bọ cạp", "Nhện", "Ve sầu"
    ),
    "Đồ vật trong nhà" to listOf(
        "Bát", "Chén", "Đĩa", "Tô", "Đũa", "Thìa", "Muỗng", "Muôi", "Vá", "Đồ khui",
        "Dao", "Kéo", "Thớt", "Nồi", "Chảo", "Nồi cơm điện", "Ấm nước", "Bình thủy",
        "Rổ", "Rá", "Chõ", "Lồng bàn", "Chạn", "Tủ lạnh", "Lò vi sóng", "Máy xay sinh tố",
        "Ly", "Cốc", "Tách", "Ống hút", "Chai", "Lọ", "Hũ", "Khay",
        "Bàn", "Ghế", "Sofa", "Tủ", "Kệ", "Tivi", "Quạt", "Điều hòa", "Rèm", "Cửa",
        "Đồng hồ", "Tranh", "Đèn", "Bình hoa", "Lọ hoa", "Thảm", "Ổ cắm", "Công tắc",
        "Ổ điện", "Dây sạc", "Bàn là", "Máy giặt", "Sọt rác",
        "Giường", "Gối", "Chăn", "Đệm", "Tủ quần áo", "Gương", "Lược", "Bàn trang điểm",
        "Móc áo", "Giá treo", "Đèn ngủ",
        "Bàn chải", "Kem đánh răng", "Khăn mặt", "Khăn tắm", "Xà phòng", "Dầu gội",
        "Sữa tắm", "Bồn cầu", "Vòi sen", "Chậu", "Gáo", "Chổi", "Cây lau nhà", "Xô"
    )
)

val ONLINE_RICH_TOPICS: Map<String, List<String>> = linkedMapOf(
    "Địa lý Việt Nam" to listOf(
        "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "Huế", "Nha Trang", "Đà Lạt", "Vũng Tàu", "Quảng Ninh",
        "Sa Pa", "Phú Quốc", "Hạ Long", "Ninh Bình", "Phong Nha", "Bà Nà", "Phan Xi Păng", "Trường Sa", "Hoàng Sa", "Mê Kông"
    ),
    "Công nghệ thông tin" to listOf(
        "Máy tính", "Laptop", "Điện thoại", "Máy in", "Bàn phím", "Chuột", "Màn hình", "CPU", "RAM", "Ổ cứng",
        "Phần mềm", "Ứng dụng", "Trình duyệt", "Mạng", "Wifi", "Bluetooth", "Mã nguồn", "Dữ liệu", "Hệ điều hành", "Trang chủ"
    ),
    "Khoa học vũ trụ" to listOf(
        "Mặt trời", "Mặt trăng", "Trái đất", "Sao hỏa", "Sao kim", "Sao thủy", "Sao mộc", "Sao thổ", "Thiên hà", "Hố đen",
        "Sao chổi", "Thiên thạch", "Vũ trụ", "Tàu vũ trụ", "Phi hành gia", "Ngân hà", "Vệ tinh", "Quỹ đạo", "Kính viễn vọng", "Trọng lực"
    )
)

/* ======================= MÔ HÌNH ======================= */

class PlayerSlot(val slotId: Int, val name: String, val isBot: Boolean, active: Boolean = true) {
    var active by mutableStateOf(active)
    var score by mutableIntStateOf(0)
}

data class GameDialog(val title: String, val message: String, val color: Color)

class GameEngine {
    var dictionary by mutableStateOf<Map<String, List<String>>>(LinkedHashMap(OFFLINE_DICTIONARY))
    var topic by mutableStateOf("Cơ thể người")
    var mode by mutableStateOf("1v1v1")
    var currentTurn by mutableIntStateOf(0)
    var currentChain by mutableStateOf("")
    var status by mutableStateOf("⏳ Khởi động...")
    var statusColor by mutableStateOf(Color(0xFFE67E22))
    var dialog by mutableStateOf<GameDialog?>(null)
    var tick by mutableIntStateOf(0)   // kích hoạt lại lượt bot

    val usedWords = mutableStateListOf<String>()
    val logs = mutableStateListOf<String>()

    val slots = listOf(
        PlayerSlot(0, "Bạn (P1)", isBot = false),
        PlayerSlot(1, "Bot Alpha", isBot = true),
        PlayerSlot(2, "Bot Beta", isBot = true)
    )

    fun log(msg: String) {
        logs.add(msg)
        if (logs.size > 200) logs.removeAt(0)
    }

    private fun topicWords() = dictionary[topic] ?: emptyList()

    fun onModeChange(value: String) {
        mode = value
        slots[2].active = (mode == "1v1v1")
        resetGame()
    }

    fun onTopicChange(value: String) {
        topic = value
        resetGame()
    }

    fun resetGame() {
        currentChain = ""
        currentTurn = 0
        log("=== BẮT ĐẦU VÁN MỚI ($topic) ===")
        tick++
    }

    fun nextTurn() {
        val numPlayers = if (mode == "1v1v1") 3 else 2
        currentTurn = (currentTurn + 1) % numPlayers
        while (!slots[currentTurn].active) {
            currentTurn = (currentTurn + 1) % numPlayers
        }
        log("-> Lượt P${currentTurn + 1}")
        tick++
    }

    val isBotTurn: Boolean get() = slots[currentTurn].isBot

    /* ---------- Hành động của người chơi ---------- */

    fun onClickLetter(char: Char) {
        if (slots[currentTurn].isBot) {
            dialog = GameDialog("Cảnh báo", "Đợi lượt của bạn!", Color(0xFFE67E22))
            return
        }
        val newChain = currentChain + char
        val available = topicWords().filter { it !in usedWords }
        val possibleFuture = available.filter { removeAccents(it).startsWith(newChain) }

        if (possibleFuture.isEmpty()) {
            log("❌ Sai chuỗi '$newChain' (hoặc đã bị dùng ở ván trước) -> THUA!")
            dialog = GameDialog(
                "THUA RỒI",
                "Chuỗi '$newChain' không hợp lệ hoặc đã dùng ở ván trước.\nBạn đã THUA!",
                Color(0xFFC0392B)
            )
            resetGame()
            return
        }
        currentChain = newChain
        log("[P1] +'$char' -> $currentChain")
    }

    fun actionNextTurn() {
        if (slots[currentTurn].isBot) return
        val chainRaw = currentChain.uppercase()
        val exact = topicWords().filter { removeAccents(it) == chainRaw && it !in usedWords }
        if (exact.isNotEmpty()) {
            log("❌ P1 bỏ lỡ từ hoàn chỉnh '${exact[0]}' -> Mất lượt chốt!")
            dialog = GameDialog(
                "Bỏ lỡ",
                "Chuỗi này đã tạo thành từ '${exact[0]}'. Bạn bấm tiếp theo nên mất lượt chốt!",
                Color(0xFFE67E22)
            )
        } else {
            log("[P1] Chuyển lượt.")
        }
        nextTurn()
    }

    fun actionSkipTurn() {
        if (slots[currentTurn].isBot) return
        log("[P${currentTurn + 1}] Bỏ lượt.")
        nextTurn()
    }

    fun actionChotDirectly() {
        val p = slots[currentTurn]
        if (p.isBot) {
            dialog = GameDialog("Cảnh báo", "Chưa tới lượt bạn!", Color(0xFFE67E22))
            return
        }
        if (currentChain.isEmpty()) {
            dialog = GameDialog("Cảnh báo", "Chưa có chuỗi chữ!", Color(0xFFE67E22))
            return
        }
        val chainRaw = currentChain.uppercase()
        val words = topicWords()

        if (words.any { removeAccents(it) == chainRaw && it in usedWords }) {
            dialog = GameDialog("Lỗi trùng lặp", "Từ này đã được sử dụng ở ván trước rồi!", Color(0xFFC0392B))
            return
        }
        val exact = words.filter { removeAccents(it) == chainRaw && it !in usedWords }
        if (exact.isNotEmpty()) {
            val chosen = exact[0]
            usedWords.add(chosen)
            p.score++
            log("🏆 P${p.slotId + 1} CHỐT THÀNH CÔNG: '$chosen'")
            dialog = GameDialog(
                "CHIẾN THẮNG!",
                "Bạn đã chốt thành công từ '$chosen' và kết thúc ván!",
                Color(0xFF27AE60)
            )
            resetGame()
        } else {
            log("❌ Chốt sai '$currentChain' -> THUA!")
            dialog = GameDialog(
                "THUA",
                "'$currentChain' không chính xác hoặc không có trong từ điển.\nBạn đã THUA.",
                Color(0xFFC0392B)
            )
            resetGame()
        }
    }

    /* ---------- Lượt của bot ---------- */

    fun botPlay() {
        val bot = slots[currentTurn]
        val chainRaw = removeAccents(currentChain).uppercase()
        val words = topicWords()
        val available = words.filter { it !in usedWords }
        val matching = available.filter { removeAccents(it).startsWith(chainRaw) }

        if (chainRaw.isEmpty() || matching.isEmpty()) {
            if (available.isEmpty()) {
                dialog = GameDialog("Hết từ", "Đã dùng hết từ vựng của chủ đề này qua các ván!", Color(0xFFC0392B))
                resetGame()
                return
            }
            val chosen = available.random()
            val clean = removeAccents(chosen)
            if (clean.length == 1 || chainRaw.length == clean.length) {
                botWin(bot, chosen, "P${bot.slotId + 1} đã tự động chốt từ '$chosen'!")
                return
            }
            currentChain = clean[0].toString()
            log("[Bot ${bot.slotId + 1}] mở '${clean[0]}'")
        } else {
            val exact = matching.filter { removeAccents(it) == chainRaw }
            if (exact.isNotEmpty()) {
                botWin(bot, exact[0], "P${bot.slotId + 1} tự động hoàn thành và chốt từ '${exact[0]}'!")
                return
            }
            val target = matching[0]
            val targetClean = removeAccents(target)
            if (targetClean.length > chainRaw.length) {
                val nextChar = targetClean[chainRaw.length]
                currentChain += nextChar
                log("[Bot] nối '$nextChar'")
                val newChainRaw = removeAccents(currentChain).uppercase()
                val check = words.filter { removeAccents(it) == newChainRaw && it !in usedWords }
                if (check.isNotEmpty()) {
                    botWin(bot, check[0], "P${bot.slotId + 1} tự động chốt từ '${check[0]}' ngay sau khi nối!")
                    return
                }
            } else {
                usedWords.add(target)
                bot.score++
                log("🤖 P${bot.slotId + 1} chốt '$target'")
                resetGame()
                return
            }
        }
        nextTurn()
    }

    private fun botWin(bot: PlayerSlot, word: String, message: String) {
        usedWords.add(word)
        bot.score++
        log("🤖 P${bot.slotId + 1} TỰ ĐỘNG CHỐT: '$word'")
        dialog = GameDialog("Bot Thắng", message, Color(0xFFC0392B))
        resetGame()
    }

    /* ---------- Kiểm tra mạng, nạp thêm chủ đề ---------- */

    suspend fun fetchOnlineDictionary() {
        status = "🌐 Đang kiểm tra mạng..."
        statusColor = Color(0xFFE67E22)
        val online = withContext(Dispatchers.IO) {
            try {
                val conn = URL("https://www.google.com").openConnection() as HttpURLConnection
                conn.connectTimeout = 3000
                conn.readTimeout = 3000
                conn.requestMethod = "HEAD"
                conn.connect()
                conn.responseCode
                conn.disconnect()
                true
            } catch (e: Exception) {
                false
            }
        }
        if (online) {
            val merged = LinkedHashMap(dictionary)
            ONLINE_RICH_TOPICS.forEach { (k, v) -> merged.putIfAbsent(k, v) }
            dictionary = merged
            status = "🌐 Online (Mở rộng)"
            statusColor = Color(0xFF27AE60)
            log("🚀 Đã kết nối mạng: Nạp thành công các chủ đề phong phú trực tuyến!")
        } else {
            status = "📶 Offline Mode"
            statusColor = Color(0xFF2980B9)
            log("📴 Không có mạng: Đang dùng kho từ điển cục bộ siêu đầy đủ.")
        }
    }
}

/* ======================= GIAO DIỆN ======================= */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { ChotChuScreen() }
        }
    }
}

private val NAVY = Color(0xFF2C3E50)
private val GRAY = Color(0xFF7F8C8D)
private val RED = Color(0xFFE74C3C)
private val YELLOW = Color(0xFFF1C40F)
private val SLATE = Color(0xFF34495E)

@Composable
fun ChotChuScreen(engine: GameEngine = remember { GameEngine() }) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { engine.fetchOnlineDictionary() }

    // Bot tự chơi sau 1.2 giây khi tới lượt
    LaunchedEffect(engine.tick) {
        if (engine.isBotTurn) {
            delay(1200)
            engine.botPlay()
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F6FA)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            /* --- Hàng trạng thái + chủ đề --- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    engine.status,
                    color = engine.statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                DropdownPicker(
                    label = engine.topic,
                    options = engine.dictionary.keys.toList(),
                    onSelect = engine::onTopicChange
                )
            }

            /* --- Chế độ chơi --- */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chế độ:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
                DropdownPicker(
                    label = engine.mode,
                    options = listOf("1v1", "1v1v1"),
                    onSelect = engine::onModeChange
                )
            }

            /* --- Bàn cờ --- */
            BoardCanvas(engine)

            /* --- Điểm số --- */
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                engine.slots.forEach { p ->
                    if (p.active) {
                        val isTurn = p.slotId == engine.currentTurn
                        Text(
                            "P${p.slotId + 1}đ:${p.score}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTurn) RED else SLATE
                        )
                    }
                }
            }

            Text(
                "💡 Bot tự chốt nếu đủ từ. P1 tự bấm CHỐT.",
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF2980B9),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                "Chuỗi: [ ${engine.currentChain.ifEmpty { "TRỐNG" }} ]",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE67E22),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            /* --- Nhật ký (co giãn theo màn hình) --- */
            val scroll = rememberScrollState()
            LaunchedEffect(engine.logs.size) { scroll.animateScrollTo(scroll.maxValue) }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(6.dp))
                    .border(1.dp, Color(0xFFBDC3C7), RoundedCornerShape(6.dp))
                    .padding(6.dp)
                    .verticalScroll(scroll)
            ) {
                engine.logs.forEach { Text(it, fontSize = 11.sp, color = SLATE) }
            }

            /* --- Bàn phím A-Z --- */
            listOf("ABCDEFGHI", "JKLMNOPQR", "STUVWXYZ").forEach { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    row.forEach { ch ->
                        KeyButton(ch, Modifier.weight(1f)) { engine.onClickLetter(ch) }
                    }
                    repeat(9 - row.length) { Spacer(Modifier.weight(1f)) }
                }
            }

            /* --- Nút hành động --- */
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ActionButton("CHỐT", Color(0xFF27AE60), Modifier.weight(1f)) { engine.actionChotDirectly() }
                ActionButton("TIẾP THEO", Color(0xFF2980B9), Modifier.weight(1f)) { engine.actionNextTurn() }
                ActionButton("BỎ LƯỢT", GRAY, Modifier.weight(1f)) { engine.actionSkipTurn() }
            }

            Text(
                "Đã chốt: " + (engine.usedWords.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "(Trống)"),
                fontSize = 11.sp,
                color = GRAY,
                maxLines = 2
            )
        }
    }

    /* --- Hộp thoại thông báo --- */
    engine.dialog?.let { d ->
        AlertDialog(
            onDismissRequest = { engine.dialog = null },
            confirmButton = {
                TextButton(onClick = { engine.dialog = null }) { Text("OK") }
            },
            title = { Text(d.title, color = d.color, fontWeight = FontWeight.Bold) },
            text = { Text(d.message) }
        )
    }
}

@Composable
private fun BoardCanvas(engine: GameEngine) {
    val measurer = rememberTextMeasurer()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(NAVY, RoundedCornerShape(8.dp))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val corners = listOf(
                Offset(w / 2f, h * 0.18f),
                Offset(w * 0.82f, h * 0.80f),
                Offset(w * 0.18f, h * 0.80f)
            )
            val dash = PathEffect.dashPathEffect(floatArrayOf(9f, 9f))

            if (engine.mode == "1v1v1") {
                for (i in 0..2) {
                    drawLine(GRAY, corners[i], corners[(i + 1) % 3], strokeWidth = 4f, pathEffect = dash)
                }
            } else {
                drawLine(GRAY, corners[0], corners[1], strokeWidth = 4f, pathEffect = dash)
            }

            val box = 26f
            engine.slots.forEach { p ->
                if (!p.active) return@forEach
                val c = corners[p.slotId]
                val isTurn = p.slotId == engine.currentTurn
                drawRect(
                    color = if (isTurn) RED else SLATE,
                    topLeft = Offset(c.x - box, c.y - box),
                    size = androidx.compose.ui.geometry.Size(box * 2, box * 2)
                )
                drawRect(
                    color = if (isTurn) YELLOW else Color(0xFF95A5A6),
                    topLeft = Offset(c.x - box, c.y - box),
                    size = androidx.compose.ui.geometry.Size(box * 2, box * 2),
                    style = Stroke(width = 4f)
                )
                val layout = measurer.measure(
                    "P${p.slotId + 1}",
                    TextStyle(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
                drawText(
                    layout,
                    topLeft = Offset(c.x - layout.size.width / 2f, c.y - layout.size.height / 2f)
                )
            }
        }
    }
}

@Composable
private fun KeyButton(ch: Char, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFECF0F1),
            contentColor = NAVY
        )
    ) {
        Text(ch.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActionButton(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        contentPadding = PaddingValues(2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun DropdownPicker(label: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
        ) {
            Text(label, fontSize = 12.sp, maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        expanded = false
                        onSelect(opt)
                    }
                )
            }
        }
    }
}
