package name.seanpayne.util.id3tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.google.gson.Gson
import com.mpatric.mp3agic.Mp3File

class Id3Tool : CliktCommand() {
    override fun run() = Unit
}

class Id3ToJson: CliktCommand(help = "Output ID3 to JSON") {
    val inputFile by argument(help = "Input MP3 file", name = "input")

    override fun run() {
        val mp3File = Mp3File(inputFile)

        val metadata = mp3File.jsonMetadata()

        val jsonResult = Gson().toJson(metadata)

        println(jsonResult)
    }
}



fun main(args: Array<String>) = Id3Tool()
        .subcommands(Id3ToJson())
        .main(args)