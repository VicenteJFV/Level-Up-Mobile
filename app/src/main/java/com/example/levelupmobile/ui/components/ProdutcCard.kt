// app/src/main/java/com/example/levelupmobile/ui/components/ProductCard.kt
package com.example.levelupmobile.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.draw.scale
import com.example.levelupmobile.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProductCard(
    name: String,
    price: String,
    imageUrl: String?,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.06f else 1f,
        animationSpec = spring(),
        label = "add-scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(SurfaceDark)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Carga drawable por nombre
            val context = LocalContext.current
            val imageRes = imageUrl
                ?.let { context.resources.getIdentifier(it, "drawable", context.packageName) }
                ?.takeIf { it != 0 }
                ?: android.R.drawable.ic_menu_gallery

            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = ElectricBlue,
                    fontSize = 18.sp
                )
            )
            Text(
                text = price,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = NeonGreen,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    pressed = true
                    onAddToCart()
                },
                modifier = Modifier.scale(scale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = BlackBackground
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Agregar", fontWeight = FontWeight.Bold)
            }

            LaunchedEffect(pressed) {
                if (pressed) {
                    delay(200)
                    pressed = false
                }
            }
        }
    }
}
