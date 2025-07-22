package io.getstream.feeds.android.sample

data class DemoAppConfig(
    val apiKey: String,
    val token: (String) -> String,
) {

    companion object {

        val Staging = DemoAppConfig(
            apiKey = "pd67s34fzpgw",
            token = { userId ->
                when (userId) {
                    "luke_skywalker" -> "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibHVrZV9za3l3YWxrZXIifQ.hZ59SWtp_zLKVV9ShkqkTsCGi_jdPHly7XNCf5T_Ev0"
                    "petar" -> "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicGV0YXIifQ.mZFi4iSblaIoyo9JDdcxIkGkwI-tuApeSBawxpz42rs"
                    else -> ""
                }
            }
        )

        val Production = DemoAppConfig(
            apiKey = "fa5xpkvxrdw4",
            token = { userId ->
                when (userId) {
                    // TODO: Setup users
                    else -> ""
                }
            }
        )

        var Current: DemoAppConfig = Staging
    }
}