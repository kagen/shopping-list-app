package com.example.shoppinglistapp // ←あなたのパッケージ名

import kotlinx.coroutines.delay
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.shoppinglistapp.ui.theme.ShoppingListAppTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.platform.LocalContext
import com.example.shoppinglistapp.data.Product

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingListAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "plan_list") {
        composable("plan_list") {
            ShoppingPlanListScreen(onPlanClick = { planId ->
                navController.navigate("plan_detail/$planId")
            })
        }
        composable("plan_detail/{planId}") { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: "unknown"
            ShoppingPlanDetailScreen(planId = planId)
        }
    }
}

@Composable
fun ShoppingPlanListScreen(onPlanClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("🛒 買い物計画一覧", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        val plans = listOf("2025-06-25", "2025-06-24", "2025-06-23")

        for (plan in plans) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onPlanClick(plan) }
            ) {
                Text(
                    text = plan,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { /* TODO: 新規計画作成 */ }) {
            Text("＋ 新しい買い物計画を作成")
        }
    }
}
@Composable
fun ShoppingPlanDetailScreen(planId: String) {
    // ダミーデータ
    val groups = listOf(
        Group("Aスーパー", listOf(
            Product("牛乳", "🥛", 2 , "那須の牛乳"),
            Product("パン", "🍞", 1),
            Product("スイカ", null, 1),
        )),
        Group("精肉店", listOf(
            Product("豚肉", "🥩", 3, "油少ない"),
            Product("鶏むね肉", null, 1),
        ))
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("🗓️ $planId の買い物計画", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        groups.forEach { group ->
            GroupCard(group)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


data class Product(
    val name: String,
    val icon: String?, // 絵文字 or null
    val quantity: Int
)

data class Group(
    val name: String,
    val products: List<Product>
)
@Composable
fun GroupCard(group: Group) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(group.name, style = MaterialTheme.typography.titleMedium)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                ProductGrid(products = group.products)
            }
        }
    }
}
@Composable
fun ProductGrid(products: List<Product>) {
    val columns = 3
    Column {
        products.chunked(columns).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEach { product ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .aspectRatio(1f)
                    ) {
                        ProductItem(product)
                    }
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductItem(product: Product) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(product.isChecked) }
    var showMemo by remember { mutableStateOf(false) }

    LaunchedEffect(showMemo) {
        if (showMemo) {
            delay(2000)
            showMemo = false
        }
    }

    val gestureModifier = Modifier
        .combinedClickable(
            onClick = {
                // 🟡 シングルタップ：メモ表示（Box内に表示）
                if (!product.memo.isNullOrBlank()) {
                    showMemo = true
                }
            },
            onDoubleClick = {
                // ✅ ダブルタップ：買い物完了（チェック）
                isChecked = !isChecked
            },
            onLongClick = {
                // ✏️ 長押し：あとでダイアログを追加予定
            }
        )

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = product.icon ?: product.name.take(1),
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodySmall)
            }

            if (product.quantity > 1) {
                Text(
                    text = "🔴${product.quantity}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }

            if (isChecked) {
                Text(
                    text = "✅",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                )
            }

            if (showMemo) {
                Text(
                    text = product.memo ?: "",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(
                            color = Color(0xFF323232), // ← 背景色変更（例: 濃いグレー）
                            shape = RoundedCornerShape(12.dp) // ← 角を丸くする
                        )
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)) // ← 枠線追加（任意）
                        .padding(horizontal = 12.dp, vertical = 6.dp) // ← 内側の余白調整
                        .shadow(4.dp), // ← ドロップシャドウ（任意）
                    color = Color.White, // 文字色
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}


