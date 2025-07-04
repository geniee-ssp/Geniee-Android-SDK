package jp.co.geniee.samples.gnad.common;

import java.util.Random;

public class CellData {
    public String imgURL;
    public String title;
    public String content;

    SampleData[] datas = new SampleData[] {
            new SampleData("https://media.gssp.asia/img/bf0/8f2/bf08f28d19c98e2b603a21519a0948f6.png",
                    "title",
                    "description sample : ios"),
            new SampleData("https://media.gssp.asia/img/bae/bbb/baebbb357d7011d4b1a8fee309dbfe56.jpg",
                    "title",
                    "description sample : android")
    };

    public CellData() {
        Random r = new Random();
        int n = r.nextInt(datas.length);
        imgURL = datas[n].imgURL;
        title = datas[n].title;
        content = datas[n].content;
    }
}

class SampleData {
    String imgURL;
    String title;
    String content;
    public SampleData(String imgURL, String title, String content) {
        this.imgURL = imgURL;
        this.title = title;
        this.content = content;
    }
}
