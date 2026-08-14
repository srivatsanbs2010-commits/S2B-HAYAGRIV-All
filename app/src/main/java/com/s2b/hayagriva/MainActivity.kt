package com.s2b.hayagriva

import android.Manifest
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        setContent { HayagrivaApp(::speak) }
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
    }
    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hayagriva")
    }
    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}

@Composable
fun HayagrivaApp(speak: (String)->Unit) {
    val context = LocalContext.current
    var listening by remember { mutableStateOf(false) }
    var userText by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English") }
    var mode by remember { mutableStateOf("Friendly") }
    val history = remember { mutableStateListOf<String>() }

    val pulse by rememberInfiniteTransition(label="pulse").animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse), label="pulse"
    )

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        listening = false
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrBlank()) {
            userText = text
            history.add("You: $text")
            val reply = handleIntent(text)
            history.add("Hayagriva: $reply")
            speak(reply)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {}

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF050711),
            surface = Color(0xFF0B1020),
            primary = Color(0xFF8B7CFF),
            secondary = Color(0xFF55D6FF)
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF050711), Color(0xFF0A1020), Color(0xFF03040A)))
            )
        ) {
            Column(Modifier.fillMaxSize().padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("S2B", fontSize=14.sp, color=Color(0xFF9FA8FF), fontWeight=FontWeight.Bold)
                        Text("HAYAGRIVA", fontSize=27.sp, fontWeight=FontWeight.Black)
                        Text("$mode • $language", color=Color.LightGray, fontSize=12.sp)
                    }
                    IconButton(onClick = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))
                    }) { Icon(Icons.Default.Settings, "Permissions") }
                }

                Spacer(Modifier.height(12.dp))

                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment=Alignment.Center) {
                    Box(
                        Modifier.size(235.dp).scale(if (listening) pulse else 1f)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF8B7CFF), Color(0xFF243B7A), Color.Transparent)
                                ), CircleShape
                            )
                    )
                    Box(
                        Modifier.size(155.dp).alpha(if (listening) 1f else .86f)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFFB9B2FF), Color(0xFF4A47A5), Color(0xFF10132D))
                                ), CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment=Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoAwesome, null, tint=Color.White, modifier=Modifier.size(48.dp))
                            Text(if (listening) "LISTENING" else "HAYAGRIVA",
                                fontSize=13.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                }

                if (userText.isNotBlank()) {
                    Card(Modifier.fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=Color(0x5512182D))) {
                        Text(userText, Modifier.padding(14.dp), color=Color.White)
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick={ language = if(language=="English") "Tamil" else if(language=="Tamil") "Hindi" else "English" },
                        label={Text(language)}, leadingIcon={Icon(Icons.Default.Language,null)})
                    AssistChip(onClick={ mode = if(mode=="Friendly") "Coding" else if(mode=="Coding") "Research" else "Friendly" },
                        label={Text(mode)}, leadingIcon={Icon(Icons.Default.Tune,null)})
                    AssistChip(onClick={
                        val i=Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="*/*";addCategory(Intent.CATEGORY_OPENABLE)}
                        context.startActivity(i)
                    }, label={Text("Files")}, leadingIcon={Icon(Icons.Default.FolderOpen,null)})
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceEvenly) {
                    SmallAction("Camera", Icons.Default.CameraAlt) {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                        context.startActivity(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))
                    }
                    SmallAction("Alarm", Icons.Default.Alarm) {
                        context.startActivity(Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_MESSAGE,"S2B Hayagriva"))
                    }
                    SmallAction("Calendar", Icons.Default.CalendarMonth) {
                        context.startActivity(Intent(Intent.ACTION_INSERT).setData(CalendarContract.Events.CONTENT_URI))
                    }
                    SmallAction("PDF", Icons.Default.PictureAsPdf) {
                        makePdf(context)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        listening=true
                        val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to Hayagriva")
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, when(language){"Tamil"->"ta-IN";"Hindi"->"hi-IN";else->"en-IN"})
                        }
                        speechLauncher.launch(intent)
                    },
                    Modifier.fillMaxWidth().height(58.dp),
                    shape=RoundedCornerShape(18.dp)
                ) {
                    Icon(if(listening) Icons.Default.GraphicEq else Icons.Default.Mic, null)
                    Spacer(Modifier.width(10.dp))
                    Text(if(listening) "Listening…" else "TALK TO HAYAGRIVA", fontWeight=FontWeight.Bold)
                }

                if (history.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(Modifier.heightIn(max=120.dp)) {
                        items(history.takeLast(4)) { Text(it, Modifier.padding(3.dp), fontSize=12.sp, color=Color.LightGray) }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallAction(title:String, icon: androidx.compose.ui.graphics.vector.ImageVector, click:()->Unit) {
    Column(horizontalAlignment=Alignment.CenterHorizontally, modifier=Modifier.clickable{click()}.padding(4.dp)) {
        Icon(icon, title, tint=Color(0xFFB9B2FF))
        Text(title, fontSize=10.sp)
    }
}

fun handleIntent(text:String):String {
    val t=text.lowercase(Locale.getDefault())
    return when {
        t.contains("hello") || t.contains("hi") -> "Hello! I am Hayagriva. How can I help?"
        t.contains("time") -> "I can open the clock or provide time using the device."
        t.matches(Regex(".*\\d+\\s*[+\\-*/]\\s*\\d+.*")) -> "Math mode is ready. I can solve arithmetic expressions."
        t.contains("weather") -> "Weather integration is included as a secure network module in the project."
        t.contains("research") -> "Deep Research mode is ready for a backend search provider."
        t.contains("code") -> "Coding mode is enabled. Connect the Gemini backend for full code generation."
        else -> "I heard you. Connect the secure Gemini backend for full conversational intelligence."
    }
}

fun makePdf(context: android.content.Context) {
    val doc=PdfDocument()
    val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create())
    val p=android.graphics.Paint().apply{textSize=26f}
    page.canvas.drawText("S2B Hayagriva",40f,70f,p)
    p.textSize=16f
    page.canvas.drawText("Generated by Hayagriva",40f,105f,p)
    doc.finishPage(page)
    val dir=context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file=File(dir,"S2B_Hayagriva_Report.pdf")
    file.outputStream().use{doc.writeTo(it)}
    doc.close()
    Toast.makeText(context,"PDF created: ${file.name}",Toast.LENGTH_LONG).show()
}
