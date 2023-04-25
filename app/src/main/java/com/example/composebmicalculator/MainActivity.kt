package com.example.composebmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = viewModel<BmiViewModel>()
            val navController = rememberNavController() // navController 객체

            val bmi = viewModel.bmi.value

            NavHost(navController = navController, startDestination = "home") {  // 화면 구성
                composable(route = "home") {
                    HomeScreen() {height, weight ->
                        viewModel.bmiCalculate(height, weight)
                        navController.navigate("result")
                    }
                }
                composable(route = "result") {
                    ResultScreen(navController, bmi = bmi)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onResultClicked : (Double, Double) -> Unit
    ) {
    val (height, setHeight) = rememberSaveable {
        mutableStateOf("")
    }

    val (weight, setWeight) = rememberSaveable {
        mutableStateOf("")
    }

    Scaffold(           // TopAppBar 구현 하기 위해서 Scaffold 감싸기
        modifier = Modifier.fillMaxSize(),
        topBar = {      // topBar : () -> Unit  함수 형태 이기 때문에, {} 내부에 작성
            TopAppBar(  // TopAppBar(title=: () -> Unit)  함수 형태 이기 때문에, title {} 내부에 작성
                title = { Text(text = "비만도 계산기") }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)        // 컴포즈 1.2.0부터는 컴포저블에 전달 되는 패딩 매개 변수를 사용 해야 한다. 최 상위 컨테이너 / 뷰에 적용 한다.
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = height,
                onValueChange = setHeight,
                label = { Text("키") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = weight,
                onValueChange = setWeight,
                label = { Text("몸무게") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    if(height.isNotEmpty() && weight.isNotEmpty()){
                        onResultClicked(height.toDouble(), weight.toDouble())
                    }
                }) {
                Text(text = "결과")
            }
        }
    }
}

@Composable
fun ResultScreen(navController: NavController, bmi: Double) {
    val result = when{
        bmi >= 35 -> "고도 비만"
        bmi >= 30 -> "2단계 비만"
        bmi >= 25 -> "1단계 비만"
        bmi >= 23 -> "과체중"
        bmi >= 18.5 -> "정상"
        else -> "저체중"
    }

    val imageRes = when{
        bmi >= 23 -> R.drawable.baseline_sentiment_very_dissatisfied_24
        bmi >= 18.5 -> R.drawable.baseline_sentiment_satisfied_24
        else -> R.drawable.baseline_sentiment_dissatisfied_24
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "비만도 계산기") },
                navigationIcon = {
                    Icon(imageVector = Icons.Default.ArrowBack,
                        contentDescription = "home",
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = result, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(
                    color = Color.Black
                )
            )
        }
    }
}

class BmiViewModel : ViewModel() {
    private val _bmi = mutableStateOf(0.0)
    val bmi: State<Double> = _bmi                   // State 값이 변경 될 경우 화면이 다시 그려짐 -> 값 자동으로 갱신 됨

    fun bmiCalculate(height: Double, weight: Double) {
        _bmi.value = weight / (height / 100.0).pow(2.0)
    }
}
