package com.zipper.fetch.cookie.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.toSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoCompleteTextField(
    value: String,
    onItemSelected: (String) -> Unit,
    items: List<String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = MaterialTheme.shapes.small,
) {

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var expanded by remember { mutableStateOf(false) }
    var text by remember {
        mutableStateOf(value)
    }
    Box(
        modifier = modifier
            .onGloballyPositioned { textFieldSize = it.size.toSize() }
            .clickable {
                expanded = true
            },
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onItemSelected,
            enabled = false,
            singleLine = true,
            label = label,
            shape = shape,
            textStyle = textStyle,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    if (expanded) {
                        Icon(Icons.Filled.KeyboardArrowDown, "collapse")
                    } else {
                        Icon(Icons.Filled.KeyboardArrowRight, "expand")
                    }
                }
            },
            colors = textFieldColors(),
            modifier = modifier
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        Text(text = item)
                    },
                    onClick = {
                        text = items[index]
                        onItemSelected(items[index])
                        expanded = false
                    })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = MaterialTheme.colorScheme.inverseSurface,
        disabledLabelColor = MaterialTheme.colorScheme.inverseSurface,
        disabledLeadingIconColor = MaterialTheme.colorScheme.inverseSurface,
        disabledPlaceholderColor = MaterialTheme.colorScheme.inverseSurface,
        disabledTrailingIconColor = MaterialTheme.colorScheme.inverseSurface,
        disabledBorderColor = MaterialTheme.colorScheme.inverseSurface
    )
}