package jp.co.geniee.samples.common

import java.util.Random

class CellData {
    var imgURL: String = ""
    var title: String = ""
    var content: String = ""

    private var datas = arrayOf(
            SampleData("https://media.gssp.asia/img/bf0/8f2/bf08f28d19c98e2b603a21519a0948f6.png",
                    "title",
                    "description sample : ios"),
            SampleData("https://media.gssp.asia/img/bae/bbb/baebbb357d7011d4b1a8fee309dbfe56.jpg",
                    "title",
                    "description sample : android"))

    init {
        val r = Random()
        val n = r.nextInt(datas.size)
        imgURL = datas[n].imgURL
        title = datas[n].title
        content = datas[n].content
    }
}

internal class SampleData(var imgURL: String, var title: String, var content: String)
