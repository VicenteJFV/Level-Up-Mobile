package com.example.levelupmobile.ui.screens

import androidx. compose.foundation.Image
import androidx.compose.foundation. background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit. sp
import com.example.levelupmobile. ui.components.AppButton
import com.example.levelupmobile.ui.components.ButtonType
import com.example.levelupmobile.ui.theme.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

// Animaciones
import androidx.compose.animation. AnimatedVisibility
import androidx. compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation. slideInVertically
import androidx. compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

import com.example.levelupmobile. vm.models.toCLP

// Coil
import coil.compose.AsyncImage
import coil.request. ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    ui: com.example.levelupmobile. vm.models.ProductUi?,
    onAddToCart: () -> Unit,
    onBack: () -> Unit = {}
) {
    var show by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(),
        label = "detail-add-scale"
    )

    LaunchedEffect(Unit) { show = true }

    val ctx = LocalContext.current
    val imageModifier = Modifier. size(AppDimensions.imageXLarge)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "🎯 Detalle del Producto",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BlackBackground)
            )
        },
        containerColor = BlackBackground,
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = showAlert,
                    enter = fadeIn() + slideInVertically { it / 4 },
                    exit = fadeOut() + slideOutVertically { it / 4 }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ElectricBlue)
                            .padding(AppDimensions. paddingMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✅ Producto agregado al carrito",
                            style = MaterialTheme.typography. bodyMedium.copy(
                                color = WhiteText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(AppDimensions.paddingMedium),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions. spacingMedium)
                ) {
                    AppButton(
                        text = "Volver",
                        onClick = onBack,
                        type = ButtonType. Outlined
                    )

                    AppButton(
                        text = if (ui == null) "Cargando..." else "Agregar al carrito",
                        onClick = {
                            if (ui != null) {
                                pressed = true
                                showAlert = true
                                onAddToCart()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale),
                        type = ButtonType. Primary,
                        enabled = ui != null
                    )
                }
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
                    .padding(AppDimensions.paddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingLarge)
            ) {
                val imageUrl = ui?.imageUrl
                if (! imageUrl.isNullOrBlank() && (imageUrl.startsWith("http://") || imageUrl.startsWith("https://"))) {
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
                    val resId = if (imageRes != 0) imageRes else android. R.drawable.ic_menu_gallery

                    Image(
                        painter = painterResource(resId),
                        contentDescription = ui?.name ?: "Imagen del producto",
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = ui?.name ?:  "Cargando...",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = ui?.description ?: "Cargando descripción...",
                    style = MaterialTheme.typography.bodyLarge. copy(
                        color = WhiteText,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier. padding(horizontal = AppDimensions.paddingMedium)
                )

                val priceLabel = ui?.price?. toCLP() ?: "..."
                Card(
                    modifier = Modifier
                        . fillMaxWidth()
                        . padding(horizontal = AppDimensions.paddingMedium),
                    colors = CardDefaults. cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(AppDimensions.cornerRadius)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimensions.paddingMedium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Precio",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WhiteText,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(Modifier.height(AppDimensions.spacingXSmall))
                        Text(
                            text = priceLabel,
                            style = MaterialTheme.typography. titleLarge.copy(
                                color = WhiteText,
                                fontWeight = FontWeight. Bold,
                                fontSize = 24.sp
                            )
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(showAlert) {
        if (showAlert) {
            delay(2000)
            showAlert = false
        }
    }

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(180)
            pressed = false
        }
    }
}