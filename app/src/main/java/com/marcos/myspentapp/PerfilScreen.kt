package com.marcos.myspentapp

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.marcos.myspentapp.ui.theme.colorText
import com.marcos.myspentapp.ui.theme.colorTextSecondary
import com.marcos.myspentapp.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController = rememberNavController(),
    userViewModel: UserViewModel
)
 {
    val user = userViewModel.userState

    var showConf by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // FOTO DO PERFIL
    val bitmap: ImageBitmap? = remember(user.fotoUri) {
        user.fotoUri?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        .asImageBitmap()
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meu Perfil",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                expandedHeight = 30.dp,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .drawBehind {
                        drawLine(
                            color = colorText.copy(alpha = 0.4f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height - 1f),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1f),
                            strokeWidth = 8f
                        )
                    }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .padding(top = 42.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // FOTO
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3E3E3)),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Foto do usuário",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Foto",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(Modifier.width(20.dp))

                Column {
                    Text(
                        user.nome,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(user.email, fontSize = 16.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(30.dp))

            // OPÇÕES
            ProfileOptionItem(
                icon = Icons.Default.Person,
                text = "Editar Informações",
                color = MaterialTheme.colorScheme.onBackground,
                onClick = { navController.navigate(Routes.EDIT_PROFILE) }
            )

            ProfileOptionItem(
                icon = Icons.Default.Settings,
                text = "Configurações",
                color = MaterialTheme.colorScheme.onBackground,
                onClick = { showConf = true }
            )

            ProfileOptionItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = "Sair",
                color = Color.Red,
                onClick = { navController.navigate(Routes.LOGIN) }
            )

            Spacer(Modifier.height(30.dp))
        }
    }

    // CÓDIGO, TEMA, EXCLUSÃO
    CardConf(
        visible = showConf,
        onDismiss = { showConf = false },
        userViewModel = userViewModel,
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(text, fontSize = 18.sp, color = color)
        }

        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(22.dp)
        )
    }

    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)
}



@Composable
fun CardConf(
    visible: Boolean,
    onDismiss: () -> Unit,
    userViewModel: UserViewModel
) {

    val user = userViewModel.userState
    val isDarkMode = user.darkTheme

    val sheetHeightFraction = 0.60f

    val density = LocalDensity.current
    val screenHeightPx =
        with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }

    val targetOffset = screenHeightPx * (1f - sheetHeightFraction)

    val offsetY by animateFloatAsState(
        targetValue = if (visible) targetOffset else screenHeightPx,
        animationSpec = tween(
            durationMillis = 350,
            easing = FastOutSlowInEasing
        ),
        label = "offset"
    )

    var newCode by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        if (visible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { onDismiss() }
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(0, offsetY.toInt()) }
                .fillMaxWidth()
                .fillMaxHeight(sheetHeightFraction)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {

                Text(
                    "Configurações",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(20.dp))

                // CÓDIGO DE RECUPERAÇÃO
                Text(
                    "Código de Recuperação",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorTextSecondary, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        user.codeRescue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = newCode,
                    onValueChange = { newCode = it },
                    label = { Text("Novo código") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedBorderColor = Color(0xFF827E7D),
                        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedLabelColor = Color(0xFF827E7D),
                        cursorColor = Color(0xFF827E7D),
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = Color(0xFF827E7D)
                    )
                )

                Spacer(Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (newCode.isNotBlank()) {
                            userViewModel.updateCode(newCode)
                            newCode = ""
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Salvar novo código",
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.height(30.dp))

                // TEMA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Modo Escuro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Ativar tema escuro no aplicativo",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { userViewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color(0xFF827E7D),
                            uncheckedTrackColor = Color(0xFF827E7D).copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(Modifier.height(30.dp))

                // EXCLUIR USUÁRIO
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                        ,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "Excluir usuário",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // CONFIRMAR EXCLUSÃO
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Excluir conta",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Tem certeza que deseja excluir sua conta?\n" +
                            "Essa ação não poderá ser desfeita."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.deleteUser()
                        showDeleteDialog = false
                        onDismiss()
                    }
                ) {
                    Text(
                        "Excluir",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilEdit(
    fotoUriAtual: Uri? = null,
    userViewModel: UserViewModel,
    onFechar: () -> Unit = {},
    navController: NavController
) {
    val user = userViewModel.userState

    var nome by remember { mutableStateOf(user.nome) }
    var email by remember { mutableStateOf(user.email) }
    var senha by remember { mutableStateOf(user.senha) }
    var fotoUri by remember { mutableStateOf(fotoUriAtual) }



    val context = LocalContext.current

    // abrir galeria
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        fotoUri = uri
    }

    // COPIA E COLA -> (CardGasto/DetalheGasto)
    val bitmap: ImageBitmap? = remember(user.fotoUri) {
        fotoUri?.let { uri ->
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        .asImageBitmap()
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        }
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Informações",
                        fontSize = 28.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                expandedHeight = 30.dp,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .drawBehind {
                        drawLine(
                            color = colorText.copy(alpha = 0.4f),
                            start = androidx.compose.ui.geometry.Offset(0f, size.height - 1f),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height - 1f),
                            strokeWidth = 8f
                        )
                    }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO DE PERFIL
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Adicionar foto",
                        tint = Color.Gray,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedBorderColor = Color(0xFF827E7D),
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = Color(0xFF827E7D),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = Color(0xFF827E7D),
                    cursorColor = Color(0xFF827E7D)
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navController.navigate(Routes.PROFILE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = Color(0xFF827E7D))
                ) {
                    Text("Cancelar")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        userViewModel.updateName(nome)
                        userViewModel.updateEmail(email)
                        userViewModel.updateSenha(senha)
                        userViewModel.updatePhoto(fotoUri)
                        navController.navigate(Routes.PROFILE)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Salvar", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}



