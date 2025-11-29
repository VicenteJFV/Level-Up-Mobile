package com.example.levelupmobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.levelupmobile.ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.theme.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

// Animaciones
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

import com.example.levelupmobile.vm.models.toCLP

// Coil
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    ui: com.example.levelupmobile.vm.models.ProductUi?,
    onAddToCart: () -> Unit,
    onBack: () -> Unit = {}
) {
    var show by remember { mutableStateOf(false) }      // entrada con fade/slide
    var pressed by remember { mutableStateOf(false) }   // “pop” del botón agregar
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(),
        label = "detail-add-scale"
    )

    LaunchedEffect(Unit) { show = true } // dispara animación de entrada

    val ctx = LocalContext.current
    val imageModifier = Modifier.size(180.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🎯 Detalle del Producto",
                        style = MaterialTheme.typography.titleLarge,
                        color = ElectricBlue
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground,
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Volver")
                }

                AppButton(
                    text = if (ui == null) "Cargando..." else "Agregar al carrito",
                    onClick = {
                        if (ui != null) {
                            pressed = true
                            onAddToCart()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .scale(scale),
                    type = ButtonType.Secondary,
                    enabled = ui != null
                )
            }
        }
    ) { padding ->
        AnimatedVisibility(
            visible = show,
            enter = fadeIn() + slideInVertically { it / 6 },
            exit = fadeOut() + slideOutVertically { it / 6 },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mostrar imagen remota con Coil si la URL comienza con http/https,
                // si no, intentar cargar drawable por nombre; fallback a placeholder.
                val imageUrl = ui?.imageUrl
                if (!imageUrl.isNullOrBlank() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
                    AsyncImage(
                        model = ImageRequest.Builder(ctx)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = ui?.name ?: "Imagen del producto",
                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                        error = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier
                    )
                } else {
                    val imageRes = imageUrl?.let {
                        ctx.resources.getIdentifier(it, "drawable", ctx.packageName)
                    } ?: 0
                    val resId = if (imageRes != 0) imageRes else android.R.drawable.ic_menu_gallery

                    Image(
                        painter = painterResource(resId),
                        contentDescription = ui?.name ?: "Imagen del producto",
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = ui?.name ?: "Cargando...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = NeonGreen,
                        fontSize = 22.sp
                    )
                )

                Text(
                    text = ui?.description ?: "Cargando descripción...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = LightGrayText,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val priceLabel = ui?.price?.toCLP() ?: "..."
                Text(
                    text = "Precio: $priceLabel",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(180)
            pressed = false
        }
    }
}
