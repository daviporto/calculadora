import android.util.Log
import android.view.View
import java.lang.ArithmeticException



fun main(args:Array<String>){
    val a = ExecuteCalculationListener()
    a.onClick("17.0+3.05^55")
}

class ExecuteCalculationListener {
    val numbers = "0123456789."
    var anyError = false
    lateinit var errorMessage: String

    fun findOperator(option1: Char, option2: Char, text: String): Int {
        for (i in text.indices) {
            if (text[i] == option1)
                return i
            if (text[i] == option2)
                return i
        }
        return -1
    }

    fun findLeftNumber(operatorPosition: Int, text: String): Int {
        var relativePosition = operatorPosition
        while (relativePosition > 0) {
            relativePosition--
            if (text[relativePosition] !in numbers)
                return  relativePosition + 1
        }
        if (relativePosition == operatorPosition) {
            anyError = true
            errorMessage = "operação inválida"
            return 0
        } else {
            return  relativePosition
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
        } else{

            return relativePosition + 1
        }
    }

    fun reformatExpression(leftOperatorPosition:Int, rightOperatorPosition:Int, text: String, resultExpression:String):String{
        val leftPart = text.substring(0, leftOperatorPosition)
        val rightPart = text.substring(rightOperatorPosition)
        return leftPart + resultExpression + rightPart;
    }

    fun formatResult(text:String, casasDecimais:Int):String{
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

    fun onClick(text:String) {
        var text = text
        var result = 0.0

        while (true) {
            //potencia e raiz quadrada
            val operatorPosition = findOperator('^', 'V', text)
            if (operatorPosition == -1)
                break
            var leftNumberPosition = operatorPosition
            var leftNumber = 0.0
            if (text[operatorPosition] != 'V'){
                leftNumberPosition = findLeftNumber(operatorPosition, text)
                leftNumber = text.substring(leftNumberPosition  , operatorPosition).toDouble()
            }


            var rightNumberPosition = findRightNumber(operatorPosition, text)
            val rightNumber = text.substring(operatorPosition + 1 , rightNumberPosition  ).toDouble()
            if (anyError) break

            var result = 0.0
            if (text[operatorPosition] == '^')
                try {
                    result = Math.pow(leftNumber,rightNumber)
                }catch (e:ArithmeticException){
                    println("impossivel realizar operacao de potencia")
                    return
                }
            else{
                if(rightNumber < 0.0){
                    print("impossivel realizar operacao de raiz quadrada de um numero negativo")
                    return
                }
                result = Math.sqrt(rightNumber)
                leftNumberPosition = operatorPosition

            }

            text = reformatExpression(leftNumberPosition,rightNumberPosition,text,result.toString())
            println(text)
        }

        while (true) {
            //multiplicacao e divisao
            val operatorPosition = findOperator('*', '/', text)
            if (operatorPosition == -1)
                break
            val leftNumberPosition = findLeftNumber(operatorPosition, text)
            val leftNumber = text.substring(leftNumberPosition , operatorPosition).toDouble()

            var rightNumberPosition = findRightNumber(operatorPosition, text)
            val rightNumber = text.substring(operatorPosition + 1, rightNumberPosition ).toDouble()
            if (anyError) break

            var result = 0.0
            if (text[operatorPosition] == '*')
                result = leftNumber * rightNumber
            else{
                if (rightNumber == 0.0){
                    println("impossivel dividir por 0")
                    return
                }
                result = leftNumber / rightNumber

            }


            text = reformatExpression(leftNumberPosition,rightNumberPosition,text,result.toString())
            println(text)
        }

        while (true) {
            //mais e menos
            val operatorPosition = findOperator('+', '-', text)
            if (operatorPosition == -1)
                break
            val leftNumberPosition = findLeftNumber(operatorPosition, text)
            val leftNumber = text.substring(leftNumberPosition , operatorPosition).toDouble()

            var rightNumberPosition = findRightNumber(operatorPosition, text)
            val rightNumber = text.substring(operatorPosition + 1, rightNumberPosition ).toDouble()
            if (anyError) break

            var result = 0.0
            if (text[operatorPosition] == '+')
                result = leftNumber + rightNumber
            else
                result = leftNumber - rightNumber

            text = reformatExpression(leftNumberPosition,rightNumberPosition,text,result.toString())
            println(text)
        }

//        text = formatResult(text, 4)
        print(text)

    }
}