package com.zipper.fetch.maotai.zhcs

enum class MiniProgram(
    val title: String,
    val host: String,
    val appId: String,
    val ak: String,
    val sk: String,
    val sendCodeCode: String,
    val phoneLoginCode: String
) {

    ZHCS(
        "遵义出山",
        "https://gw.huiqunchina.com",
        "wx624149b74233c99a",
        "dceec997f6c9c222ac122f727ec42668",
        "c8e8294fdc59d814ef2e0e38e53fe4f2",
        "0d1iSrFa1yx2SF0XGaHa1aWD7I2iSrFB",
        "0c1D4hGa12ERSF0xUAHa1fmYeY0D4hGw"
    )
}