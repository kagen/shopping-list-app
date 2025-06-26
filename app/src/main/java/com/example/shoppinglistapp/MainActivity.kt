package com.example.shoppinglistapp

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.unit.sp


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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun ProductItem(product: Product) {
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(product.isChecked) }
    var showMemo by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    var editedMemo by remember { mutableStateOf(product.memo ?: "") }
    var editedQuantity by remember { mutableStateOf(product.quantity.toString()) }
    var editedChecked by remember { mutableStateOf(product.isChecked) }

    val quantityOptions = (1..9).map { it.toString() }
    var quantityDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(showMemo) {
        if (showMemo) {
            delay(2000)
            showMemo = false
        }
    }

    val gestureModifier = Modifier
        .combinedClickable(
            onClick = {
                if (!product.memo.isNullOrBlank()) {
                    showMemo = true
                }
            },
            onDoubleClick = {
                isChecked = !isChecked
            },
            onLongClick = {
                editedMemo = product.memo ?: ""
                editedQuantity = product.quantity.toString()
                editedChecked = product.isChecked
                showEditDialog = true
            }
        )

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxSize()
            .then(gestureModifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(12.dp))
                .background(Color.White, shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = product.icon ?: product.name.take(1),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!product.memo.isNullOrBlank()) {
                Text(
                    text = "🔴",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            } else if (product.quantity > 1) {
                Text(
                    text = "x${product.quantity}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    color = Color.Black
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
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "商品を編集",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 14.sp)
                )
            },
            text = {
                Column {
                    Text(
                        text = "商品名: ${product.name}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = editedChecked,
                            onCheckedChange = { editedChecked = it }
                        )
                        Text("買い物完了")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedMemo,
                        onValueChange = { editedMemo = it },
                        label = { Text("メモ") },
                        modifier = Modifier.height(200.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = quantityDropdownExpanded,
                        onExpandedChange = { quantityDropdownExpanded = !quantityDropdownExpanded },
                    ) {
                        OutlinedTextField(
                            value = editedQuantity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("数量") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        DropdownMenu(
                            expanded = quantityDropdownExpanded,
                            onDismissRequest = { quantityDropdownExpanded = false },
                        ) {
                            quantityOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        editedQuantity = option
                                        quantityDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    product.memo = editedMemo
                    product.quantity = editedQuantity.toIntOrNull() ?: 1
                    product.isChecked = editedChecked
                    isChecked = editedChecked
                    showEditDialog = false
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }
}

