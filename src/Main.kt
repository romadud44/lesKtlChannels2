import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.util.*
import kotlin.system.measureTimeMillis

suspend fun main() {
    /**
     * В классе с функцией main необходимо написать функцию, которая преобразует строку в список строк.
     */
    fun getStringList(inputString: String): List<String> {
        return inputString.trim().splitToSequence(' ').filter { it.isNotEmpty() }.toList()
    }

    /**
     * Написать функцию getList(text: String), которая отправляет данные этого списка в канал с задержкой 10L.
     */
    suspend fun CoroutineScope.getList(text: String): ReceiveChannel<String> = produce(capacity = 3) {
        val textList = getStringList(text)
        for (i in textList) {
            delay(10L)
            send(i)
        }
        channel.close()
    }

    /**
     *  Использовать тестовую переменную первой задачи. Кроме функции getList(text: String) и getStringList(text: String),
     *  необходимо написать функцию modifiedList, объединяющую каналы и отправляет данные в новый канал, которые представляют
     *  следующий вид, у каждого элемента списка берется первый элемент и переводится в верхний регистр. В итоге результат
     *  объединения каналов со всеми преобразованиями в функции main возвращается в строку stringResult.
     *
     * Кроме того, необходимо полученную строку разбить и поместить в список <String>. Вывести в консоль полученный список,
     * состоящий из первых символов каждого слова, затраченное время на вышеуказанные операции.
     */
    suspend fun CoroutineScope.modifiedList(channel: ReceiveChannel<String>): ReceiveChannel<String> = produce(capacity = 3) {
        channel.consumeEach { it ->
            send(it.replaceFirstChar { it.uppercaseChar() })
        }
    }
    /**
     * В функции main необходимо получить эти данные и вернуть их в виде исходного текста, сохранить результат в
     * переменную stringResult. Посчитать время, затраченное на получение данных в main. Вывести в консоль полученный
     * результат в stringResult.
     */

    val resultList = mutableListOf<String>()
    val time = measureTimeMillis {
        coroutineScope {
            val channelOne = getList(Storage().text)
            val channelTwo = modifiedList(channelOne)
            channelTwo.consumeEach {
                println("Полученные данные из второго канала: $it")
                resultList += it
            }
            val stringResult = resultList.joinToString(
                separator = " "
            )
            println("Строка на выходе: $stringResult")
            val listResult = stringResult.toFirstCharList()

            println("Список на выходе: $listResult")
        }
    }
    println("Затраченное время: $time мс.")
}
/**
 *   Создайте класс Storage – это хранилище, в котором находится текстовая переменная text, в ней хранится басня
 *   Крылова «Мартышка и очки».
 */
class Storage() {
    val text = "Мартышка и очки"
}
fun String.toFirstCharList():List<Char>{
    val listOfStrings = this.trim().splitToSequence(' ').filter { it.isNotEmpty() }.toMutableList()
    val result = listOfStrings.map{it.first()}
    return result
}