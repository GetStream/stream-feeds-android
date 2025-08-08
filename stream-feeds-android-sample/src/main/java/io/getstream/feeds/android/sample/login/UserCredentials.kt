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
        val Luke = UserCredentials(
            user = User(
                id = "luke_skywalker",
                name = "Luke Skywalker",
                imageURL = "https://vignette.wikia.nocookie.net/starwars/images/2/20/LukeTLJ.jpg"
            ),
            userToken = UserToken(DemoAppConfig.Current.token("luke_skywalker"))
        )
        val Gian = UserCredentials(
            user = User(
                id = "gian",
                name = "Gian",
                imageURL = "https://ca.slack-edge.com/T02RM6X6B-U09645WUWQ3-a89200afc9fd-512"
            ),
            userToken = UserToken(DemoAppConfig.Current.token("gian"))
        )

        val BuiltIn = listOf(Petar, Luke, Gian)
    }
}
