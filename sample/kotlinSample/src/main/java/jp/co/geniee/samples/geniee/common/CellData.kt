package jp.co.geniee.samples.geniee.common

import kotlin.random.Random

class CellData {
    @JvmField var imgURL: String
    @JvmField var title: String
    @JvmField var content: String

    init {
        val data = DATAS[Random.nextInt(DATAS.size)]
        imgURL = data.imgURL
        title = data.title
        content = data.content
    }

    private class SampleData(val imgURL: String, val title: String, val content: String)

    private companion object {
        val DATAS = arrayOf(
            SampleData("https://media.gssp.asia/img/bf0/8f2/bf08f28d19c98e2b603a21519a0948f6.png",
                "title",
                "description sample : ios"),
            SampleData("https://media.gssp.asia/img/bae/bbb/baebbb357d7011d4b1a8fee309dbfe56.jpg",
                "title",
                "description sample : android"),
        )
    }
}
