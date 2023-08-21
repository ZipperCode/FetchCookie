package com.zipper.fetch.cookie.ui.minimt.model


interface IDrawDown {
    val text: String
}

sealed class MiniProgramConfig(
    override val text: String,
    val appId: String,
    val host: String,
    val ak: String,
    val sk: String,
    val sendCodeCode: String,
    val phoneLoginCode: String
) : IDrawDown {

    object Zhcs : MiniProgramConfig(
        "遵义出山",
        "wx624149b74233c99a",
        "https://gw.huiqunchina.com",
        "dceec997f6c9c222ac122f727ec42668",
        "c8e8294fdc59d814ef2e0e38e53fe4f2",
        "0d1iSrFa1yx2SF0XGaHa1aWD7I2iSrFB",
        "0c1D4hGa12ERSF0xUAHa1fmYeY0D4hGw"
    )

    object Lgkx : MiniProgramConfig(
        "乐港空巷",
        "wx613ba8ea6a002aa8",
        "https://gw.huiqunchina.com",
        "f6a0e9167212300eb05fca46cd45e94b",
        "c5e2b60c0165fb364ce8e005e6187760",
        "0d1iSrFa1yx2SF0XGaHa1aWD7I2iSrFB",
        "0c1D4hGa12ERSF0xUAHa1fmYeY0D4hGw"
    )

    /**
     * referer: https://servicewechat.com/wx5508e31ffe9366b8/15/page-frame.html
     * openId: oK-hU5EHb3VnwkcMON6qOeK9fWNc
     * toekn: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDE3OTc1MjUxIiwiaXNzIjoiZ21hbGwtc3RhcnNreSIsImxvZ0lkIjoibnVsbCIsImV4cCI6MTcwMDI4NjI4NCwiaWF0IjoxNjkyNTEwMjg0fQ.KutlqqiX8QliZphKJSSUyPZxHODnEKVBq980uE1Uf98
     */
    object Gyjp: MiniProgramConfig(
        "贵盐精品",
        "wx5508e31ffe9366b8",
        "https://gw.huiqunchina.com",
        "c1ea3cdb3b2043cff6c166fe62d2eee4",
        "ebb346c5c2b3cbcefd46b8e95fbb0f35",
        "0f1S0pGa1yatSF0DB7Ia1dPKCP1S0pGi",
        "0b1ycTkl2Y18Sb4Ed3nl2pB1g54ycTk5"
    )

    /**
     * referer: https://servicewechat.com/wxded2e7e6d60ac09d/11/page-frame.html
     */
    object Xlhg: MiniProgramConfig(
        "新联惠购",
        "wxded2e7e6d60ac09d",
        "https://gw.huiqunchina.com",
        "1608466d96bddacb530a8fa9a9428d14",
        "2e95d1a0b3ac14d5cf01b106314443bb",
        "0f19osGa1xtnTF0wwCFa1Fzrex39osGh",
        "0b1IyWkl2oaWRb4W2sml2IHEmv4IyWkm"
    )

    /**
     * referer: https://servicewechat.com/wx61549642d715f361/11/page-frame.html
     */
    object Glyp: MiniProgramConfig(
        "新联惠购",
        "wx61549642d715f361",
        "https://gw.huiqunchina.com",
        "00670fb03584fbf44dd6b136e534f495",
        "0d65f24dbe2bc1ede3c3ceeb96ef71bb",
        "0b1GrUFa18ZOSF0iJCHa1qcGAZ3GrUFl",
        "0e1uysGa1g4nTF07xoGa1i3apK2uysGX"
    )

    object Hljg: MiniProgramConfig(
        "航旅今购",
        "wx936aa5357931e226",
        "https://gw.huiqunchina.com",
        "",
        "",
        "0f1lyS000XSPxQ1iVb000f6vI04lyS0H",
        "0f1CRbGa1EBwSF0VzvFa1q03lD4CRbGz"
    )

    object Yljx: MiniProgramConfig(
        "驿路今寻",
        "wxee0ce83ab4b26f9c",
        "https://gw.huiqunchina.com",
        "",
        "",
        "0e1hvkml2CyPTb46voll25SDyj3hvkmQ",
        "0a1FFk000TBhxQ1S0O100VY4Gd0FFk0m"
    )
}
