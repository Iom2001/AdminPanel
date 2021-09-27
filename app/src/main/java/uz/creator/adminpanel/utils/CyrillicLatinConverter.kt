package uz.creator.adminpanel.utils

object CyrillicLatinConverter {

    fun ltc(latinText: String): String {
        var text = ""
        if (!latinText.isNullOrBlank()) {
            text = latinText
        }
        text = text.replace("Yu", "Ю")
        text = text.replace("yu", "ю")
        text = text.replace("Ya", "Я")
        text = text.replace("ya", "я")
        text = text.replace("Ch", "Ч")
        text = text.replace("ch", "ч")
        text = text.replace("Sh", "Ш")
        text = text.replace("sh", "ш")
        text = text.replace("Sh", "Щ")
        text = text.replace("sh", "щ")

        text = text.replace("G'", "Ғ")
        text = text.replace("g'", "ғ")
        text = text.replace("O'", "Ў")
        text = text.replace("o'", "ў")
        text = text.replace("Gʻ", "Ғ")
        text = text.replace("gʻ", "ғ")
        text = text.replace("Oʻ", "Ў")
        text = text.replace("oʻ", "ў")
        text = text.replace("G’", "Ғ")
        text = text.replace("g’", "ғ")
        text = text.replace("O’", "Ў")
        text = text.replace("o’", "ў")
        text = text.replace("G`", "Ғ")
        text = text.replace("g`", "ғ")
        text = text.replace("O`", "Ў")
        text = text.replace("o`", "ў")
        text = text.replace("G‘", "Ғ")
        text = text.replace("g‘", "ғ")
        text = text.replace("O‘", "Ў")
        text = text.replace("o‘", "ў")

//        text = text.replace("ʻ", "ъ")
//        text = text.replace("’", "ъ")
//        text = text.replace("`", "ъ")
//        text = text.replace("'", "ъ")

        text = text.replace("‘", "ъ")

        text = text.replace("Yo", "Ё")
        text = text.replace("yo", "ё")

        text = text.replace("A", "А")
        text = text.replace("a", "а")
        text = text.replace("B", "Б")
        text = text.replace("b", "б")
        text = text.replace("V", "В")
        text = text.replace("v", "в")
        text = text.replace("G", "Г")
        text = text.replace("g", "г")
        text = text.replace("D", "Д")
        text = text.replace("d", "д")
        text = text.replace("E", "Е")
        text = text.replace("e", "е")
        text = text.replace("J", "Ж")
        text = text.replace("j", "ж")
        text = text.replace("Z", "З")
        text = text.replace("z", "з")
        text = text.replace("I", "И")
        text = text.replace("i", "и")
        text = text.replace("Y", "Й")
        text = text.replace("y", "й")
        text = text.replace("K", "К")
        text = text.replace("k", "к")
        text = text.replace("L", "Л")
        text = text.replace("l", "л")
        text = text.replace("M", "М")
        text = text.replace("m", "м")
        text = text.replace("N", "Н")
        text = text.replace("n", "н")
        text = text.replace("O", "О")
        text = text.replace("o", "о")
        text = text.replace("P", "П")
        text = text.replace("p", "п")
        text = text.replace("R", "Р")
        text = text.replace("r", "р")
        text = text.replace("S", "С")
        text = text.replace("s", "с")
        text = text.replace("T", "Т")
        text = text.replace("t", "т")
        text = text.replace("U", "У")
        text = text.replace("u", "у")
        text = text.replace("F", "Ф")
        text = text.replace("f", "ф")
        text = text.replace("X", "Х")
        text = text.replace("x", "х")
        text = text.replace("C", "Ц")
        text = text.replace("c", "ц")
        text = text.replace("E", "Э")
        text = text.replace("e", "э")
        text = text.replace("H", "Ҳ")
        text = text.replace("h", "ҳ")
        text = text.replace("Q", "Қ")
        text = text.replace("q", "қ")
        return text
    }

    fun ctl(krillText: String): String {
        var text = ""
        if (!krillText.isNullOrBlank()) {
            text = krillText
        }
        text = text.replace("Ю", "Yu")
        text = text.replace("ю", "yu")
        text = text.replace("юе", "yuye")
        text = text.replace("Я", "Ya")
        text = text.replace("я", "ya")
        text = text.replace("Ч", "Ch")
        text = text.replace("ч", "ch")
        text = text.replace("Ш", "Sh")
        text = text.replace("ш", "sh")
        text = text.replace("Щ", "Sh")
        text = text.replace("щ", "sh")
        text = text.replace("Ё", "Yo")

        text = text.replace("ёе", "yoye")
        text = text.replace("ё", "yo")
        text = text.replace("Ғ", "G'")
        text = text.replace("ғ", "g'")
        text = text.replace("Ў", "O'")
        text = text.replace("ў", "o'")
        text = text.replace("ъ", "‘")
        text = text.replace("А", "A")
        text = text.replace("а", "a")
        text = text.replace("ае", "aye")
        text = text.replace("Б", "B")
        text = text.replace("б", "b")
        text = text.replace("В", "V")
        text = text.replace("в", "v")
        text = text.replace("Г", "G")
        text = text.replace("г", "g")
        text = text.replace("Д", "D")
        text = text.replace("д", "d")
        text = text.replace("Е", "E")
        text = text.replace("е", "e")
        text = text.replace("Ж", "J")
        text = text.replace("ж", "j")
        text = text.replace("З", "Z")
        text = text.replace("з", "z")
        text = text.replace("И", "I")
        text = text.replace("и", "i")
        text = text.replace("ие", "iye")
        text = text.replace("Й", "Y")
        text = text.replace("й", "y")
        text = text.replace("К", "K")
        text = text.replace("к", "k")
        text = text.replace("Л", "L")
        text = text.replace("л", "l")
        text = text.replace("М", "M")
        text = text.replace("м", "m")
        text = text.replace("Н", "N")
        text = text.replace("н", "n")
        text = text.replace("О", "O")
        text = text.replace("о", "o")
        text = text.replace("ое", "oye")
        text = text.replace("П", "P")
        text = text.replace("п", "p")
        text = text.replace("Р", "R")
        text = text.replace("р", "r")
        text = text.replace("С", "S")
        text = text.replace("с", "s")
        text = text.replace("Т", "T")
        text = text.replace("т", "t")
        text = text.replace("У", "U")
        text = text.replace("у", "u")
        text = text.replace("уе", "uye")
        text = text.replace("Ф", "F")
        text = text.replace("ф", "f")
        text = text.replace("Х", "X")
        text = text.replace("х", "x")
        text = text.replace("Ц", "C")
        text = text.replace("ц", "c")
        text = text.replace("Э", "E")
        text = text.replace("э", "e")
        text = text.replace("Ҳ", "H")
        text = text.replace("ҳ", "h")
        text = text.replace("Қ", "Q")
        text = text.replace("қ", "q")

        return text
    }

}