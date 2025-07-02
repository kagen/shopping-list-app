import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shoppinglistapp.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAddDialog(
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    val quantityOptions = (1..9).map { it.toString() }
    var quantityDropdownExpanded by remember { mutableStateOf(false) }
    var basePrice by remember { mutableStateOf("") }
    var selectedGroups by remember { mutableStateOf(listOf<String>()) }
    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("商品を追加", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("商品名") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("アイコン (1文字)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("メモ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = quantityDropdownExpanded,
                    onExpandedChange = { quantityDropdownExpanded = !quantityDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("数量") },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = quantityDropdownExpanded,
                        onDismissRequest = { quantityDropdownExpanded = false }
                    ) {
                        quantityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    quantity = option
                                    quantityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = basePrice,
                    onValueChange = { basePrice = it },
                    label = { Text("基準価額") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("グループ登録（複数可）")
                selectedGroups.forEach { Text("- $it") }
                Spacer(Modifier.height(8.dp))
                Text("タグ登録（複数可）")
                selectedTags.forEach { Text("# $it") }
                Spacer(Modifier.height(8.dp))
                Text("写真追加（1枚）: ${imageUri?.toString() ?: "未選択"}")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val product = Product(
                    name = name,
                    icon = icon.takeIf { it.isNotBlank() },
                    quantity = quantity.toIntOrNull() ?: 1,
                    memo = memo
                    // basePrice, groups, tags, imageUri はデータクラス側で拡張対応
                )
                onSave(product)
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}
