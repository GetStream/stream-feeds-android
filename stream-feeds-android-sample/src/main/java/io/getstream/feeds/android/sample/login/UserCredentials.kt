package io.getstream.feeds.android.sample.login

import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
import io.getstream.feeds.android.sample.DemoAppConfig

data class UserCredentials(
    val user: User,
    val userToken: UserToken,
) {

    val id: String
        get() = user.id

    companion object {

        val Petar = UserCredentials(
            user = User(
                id = "petar",
                name = "Petar",
                imageURL = "https://ca.slack-edge.com/T02RM6X6B-U07LDJZRUTG-a4129fed05b6-512",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("petar")),
        )
        val Gian = UserCredentials(
            user = User(
                id = "gian",
                name = "Gian",
                imageURL = "https://ca.slack-edge.com/T02RM6X6B-U09645WUWQ3-a89200afc9fd-512",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("gian"))
        )
        val Luke = UserCredentials(
            user = User(
                id = "luke_skywalker",
                name = "Luke Skywalker",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/2/20/LukeTLJ.jpg",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("luke_skywalker")),
        )
        val Leia = UserCredentials(
            user = User(
                id = "leia_organa",
                name = "Leia Organa",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/f/fc/Leia_Organa_TLJ.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("leia_organa")),
        )
        val Han = UserCredentials(
            user = User(
                id = "han_solo",
                name = "Han Solo",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/e/e2/TFAHanSolo.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("han_solo"))
        )
        val Lando = UserCredentials(
            user = User(
                id = "lando_calrissian",
                name = "Lando Calrissian",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/8/8f/Lando_ROTJ.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("lando_calrissian")),
        )
        val Chewbacca = UserCredentials(
            user = User(
                id = "chewbacca",
                name = "Chewbacca",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/4/48/Chewbacca_TLJ.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("chewbacca")),
        )
        val C3PO = UserCredentials(
            user = User(
                id = "c3po",
                name = "C-3PO",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/3/3f/C-3PO_TLJ_Card_Trader_Award_Card.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("c3po")),
        )
        val R2D2 = UserCredentials(
            user = User(
                id = "r2d2",
                name = "R2-D2",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/e/eb/ArtooTFA2-Fathead.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("r2d2")),
        )
        val Anakin = UserCredentials(
            user = User(
                id = "anakin_skywalker",
                name = "Anakin Skywalker",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/6/6f/Anakin_Skywalker_RotS.png",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("anakin_skywalker")),
        )
        val ObiWan = UserCredentials(
            user = User(
                id = "obi_wan_kenobi",
                name = "Obi-Wan Kenobi",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/4/4e/ObiWanHS-SWE.jpg",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("obi_wan_kenobi")),
        )
        val Padme = UserCredentials(
            user = User(
                id = "padme_amidala",
                name = "Padmé Amidala",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/b/b2/Padmegreenscrshot.jpg",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("padme_amidala")),
        )
        val QuiGon = UserCredentials(
            user = User(
                id = "qui_gon_jinn",
                name = "Qui-Gon Jinn",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/f/f6/Qui-Gon_Jinn_Headshot_TPM.jpg",
            ),
            userToken = UserToken(DemoAppConfig.Current.token("qui_gon_jinn")),
        )

        val BuiltIn = listOf(
            Petar,
            Gian,
            Luke,
            Leia,
            Han,
            Lando,
            Chewbacca,
            C3PO,
            R2D2,
            Anakin,
            ObiWan,
            Padme,
            QuiGon
        )
    }
}
