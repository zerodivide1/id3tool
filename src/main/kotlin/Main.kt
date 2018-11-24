import com.mpatric.mp3agic.Mp3File

fun main(args: Array<String>) {
    val inputFile = Mp3File(args.first())
    println("length: ${inputFile.lengthInMilliseconds}")
}