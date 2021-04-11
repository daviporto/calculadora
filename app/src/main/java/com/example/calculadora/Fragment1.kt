package com.example.calculadora

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.trimmedLength
import com.example.calculadora.databinding.Fragment1Binding
import kotlinx.android.synthetic.main.fragment_1.*
import java.lang.ArithmeticException
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.text.deleteAt


class Fragment1 : Fragment(), View.OnClickListener {
    private lateinit var binding: Fragment1Binding
    private val apendTextListener = ApendTextListener()
    private val deleteLastListener = DeleteLastListener()
    private val clearTextListener = ClearTextListener()
    private val executeCalculationListener = ExecuteCalculationListener()
    var anyError = false
    val numbers = "0123456789.E"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Fragment1Binding.inflate(layoutInflater)
        binding.btn0.setOnClickListener(apendTextListener)
        binding.btn1.setOnClickListener(apendTextListener)
        binding.btn2.setOnClickListener(apendTextListener)
        binding.btn3.setOnClickListener(apendTextListener)
        binding.btn4.setOnClickListener(apendTextListener)
        binding.btn5.setOnClickListener(apendTextListener)
        binding.btn6.setOnClickListener(apendTextListener)
        binding.btn7.setOnClickListener(apendTextListener)
        binding.btn8.setOnClickListener(apendTextListener)
        binding.btn9.setOnClickListener(apendTextListener)
        binding.btnDot.setOnClickListener(apendTextListener)

        binding.btnPlus.setOnClickListener(apendTextListener)
        binding.btnMinus.setOnClickListener(apendTextListener)
        binding.btnDivide.setOnClickListener(apendTextListener)
        binding.btnTimes.setOnClickListener(apendTextListener)
        binding.btnPotentia.setOnClickListener(apendTextListener)
        binding.btnSqrt.setOnClickListener(apendTextListener)

        binding.btnErase.setOnClickListener(deleteLastListener)
        binding.btnEquals.setOnClickListener(executeCalculationListener)
        binding.btnClear.setOnClickListener(clearTextListener)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onClick(view: View) {
        if (view is Button) {
            binding.txtResult.append(view.text)
        }
    }

    inner class ApendTextListener : View.OnClickListener {

        private fun numDigids(t:CharSequence):Int{
            val text = t.toString()
            if (text.isEmpty())
                return 0

            if (text.length <= 9)
                return  text.length

//            println(text[text.length -1])
            if (text[text.length - 1 ] !in numbers)
                return  0

            var  relativePosition = text.length
            while(relativePosition > 0){
                relativePosition--
                if (text[relativePosition] !in numbers){
                    return text.length - relativePosition - 1
                }
            }

            return text.length
        }


        override fun onClick(view: View?) {
            if (view is Button) {
                if (anyError){
                    clearTextListener.onClick(view)
                    anyError = false
                }
                if(numDigids(binding.txtResult.text.toString() + view.text) <=9)
                    binding.txtResult.append(view.text)
            }
        }
    }

    inner class DeleteLastListener : View.OnClickListener {
        override fun onClick(view: View?) {
            if (binding.txtResult.text.isEmpty())
                return

            if (view is Button) {
                var text = binding.txtResult.text
                if (text is Editable)
                    text = text.replace(text.length - 1, text.length, "")
                binding.txtResult.text = text
            }
        }
    }

    inner class ClearTextListener : View.OnClickListener {
        override fun onClick(view: View?) {
            binding.txtResult.text = ""
        }
    }

    inner class ExecuteCalculationListener : View.OnClickListener {

        lateinit var errorMessage: String

        fun findOperator(option1: Char, option2: Char, text: String, ignoreFirstMinus: Boolean = false): Int {
            if (!ignoreFirstMinus) {
                for (i in text.indices) {
                    if (text[i] == option1)
                        return i
                    if (text[i] == option2)
                        return i
                }
            } else {
                for (i in text.indices) {
                    if (text[i] == option1) {
                        if (i != 0)
                            return i
                    }
                    if (text[i] == option2) {
                        if (i != 0)
                            return i
                    }
                }
            }
            return -1
        }

        fun findLeftNumber(operatorPosition: Int, text: String): Int {
            var relativePosition = operatorPosition
            while (relativePosition > 0) {
                relativePosition--
                if (text[relativePosition] !in numbers)
                    return relativePosition + 1
            }
            if (relativePosition == operatorPosition) {
                anyError = true
                errorMessage = "operação inválida"
                return 0
            } else {
                return relativePosition
            }
        }

        fun findRightNumber(operatorPosition: Int, text: String): Int {
            var relativePosition = operatorPosition
            while (relativePosition < text.length - 1) {
                relativePosition++
                if (text[relativePosition] !in numbers)
                    return relativePosition
            }
            if (relativePosition == operatorPosition) {
                anyError = true
                errorMessage = "operação inválida"
                return 0
            } else {

                return relativePosition + 1
            }
        }

        fun reformatExpression(
            leftOperatorPosition: Int,
            rightOperatorPosition: Int,
            text: String,
            resultExpression: String
        ): String {
            val leftPart = text.substring(0, leftOperatorPosition)
            val rightPart = text.substring(rightOperatorPosition)
            return leftPart + resultExpression + rightPart;
        }

        fun formatResult(text: String, casasDecimais: Int): String {
            var casaDecimal = 0
            for (i in text.indices) {
                if (casaDecimal != 0)
                    casaDecimal++
                if (casaDecimal == casasDecimais)
                    return text.substring(0, i)
                else
                    if (text[i] == '.')
                        casaDecimal++

            }
            return text
        }

        override fun onClick(view: View?) {
            var text = binding.txtResult.text.toString()


            var result = BigDecimal(0)

            while (true) {
                //potencia e raiz quadrada
                val operatorPosition = findOperator('^', 'V', text)
                if (operatorPosition == -1)
                    break
                var leftNumberPosition = operatorPosition
                var leftNumber = BigDecimal(0)
                if (text[operatorPosition] != 'V') {
                    leftNumberPosition = findLeftNumber(operatorPosition, text)
                    leftNumber = text.substring(leftNumberPosition, operatorPosition).toBigDecimal()
                }


                var rightNumberPosition = findRightNumber(operatorPosition, text)
                var rightNumber = 0
                try {
                    rightNumber =
                        text.substring(operatorPosition + 1, rightNumberPosition).toInt()
                }catch (e:NumberFormatException){
                    binding.txtResult.setText("o expoente precisa ser positivo")
                    anyError = true
                }
                if (anyError) break

                var result = BigDecimal(0)
                if (text[operatorPosition] == '^')
                        result = leftNumber.pow(rightNumber)
                else {
                    if (rightNumber < 0.0) {
                        anyError = true
                        binding.txtResult.setText("impossivel realizar operacao de raiz quadrada de um numero negativo")
                        return
                    }
                    result = BigDecimal(Math.sqrt(leftNumber.toDouble()))
                    leftNumberPosition = operatorPosition
                }
                result = result.setScale(2, RoundingMode.HALF_EVEN)
                result.stripTrailingZeros()
                text = reformatExpression(
                    leftNumberPosition,
                    rightNumberPosition,
                    text,
                    result.toString()
                )
                println(text)
            }

            while (true) {
                //multiplicacao e divisao
                val operatorPosition = findOperator('X', '/', text)
                if (operatorPosition == -1)
                    break
                val leftNumberPosition = findLeftNumber(operatorPosition, text)
                val leftNumber = text.substring(leftNumberPosition, operatorPosition).toBigDecimal()

                var rightNumberPosition = findRightNumber(operatorPosition, text)
                val rightNumber =
                    text.substring(operatorPosition + 1, rightNumberPosition).toBigDecimal()
                if (anyError) break

                var result = BigDecimal(0)
                if (text[operatorPosition] == 'X')
                    result = leftNumber * rightNumber
                else {
                    if (rightNumber.equals(0)) {
                        anyError = true
                        binding.txtResult.setText("impossivel dividir por 0")
                        return
                    }
                    result = leftNumber.divide(rightNumber)
                }

                result = result.setScale(2, RoundingMode.HALF_EVEN)
                result.stripTrailingZeros()
                text = reformatExpression(
                    leftNumberPosition,
                    rightNumberPosition,
                    text,
                    result.toString()
                )
                println(text)
            }

            while (true) {
                //mais e menos
                val operatorPosition = findOperator('+', '-', text, true)
                if (operatorPosition == -1)
                    break
                val leftNumberPosition = findLeftNumber(operatorPosition, text)
                println(text + "  " + leftNumberPosition + "  " + operatorPosition )
                val leftNumber = text.substring(leftNumberPosition, operatorPosition).toBigDecimal()

                var rightNumberPosition = findRightNumber(operatorPosition, text)
                val rightNumber =
                    text.substring(operatorPosition + 1, rightNumberPosition).toBigDecimal()
                if (anyError) break

                var result = BigDecimal(0)
                if (text[operatorPosition] == '+')
                    result = leftNumber.plus(rightNumber)
                else
                    result = leftNumber.minus(rightNumber)

                result = result.setScale(2, RoundingMode.HALF_EVEN)
                result.stripTrailingZeros()
                text = reformatExpression(
                    leftNumberPosition,
                    rightNumberPosition,
                    text,
                    result.toString()
                )

            }

            binding.txtResult.setText(text)
        }
    }
}