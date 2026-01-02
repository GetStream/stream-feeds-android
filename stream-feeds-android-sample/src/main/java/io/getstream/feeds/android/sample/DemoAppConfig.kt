/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.sample

data class DemoAppConfig(val apiKey: String, val token: (String) -> String) {

    companion object {

        val Staging =
            DemoAppConfig(
                apiKey = "pd67s34fzpgw",
                token = { userId ->
                    when (userId) {
                        "petar" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicGV0YXIifQ.mZFi4iSblaIoyo9JDdcxIkGkwI-tuApeSBawxpz42rs"
                        "gian" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZ2lhbiJ9.DEb-U3KExmEmYLkc9nYA1CFMSYJF-eUZrcDWDJhtiIY"
                        "luke_skywalker" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibHVrZV9za3l3YWxrZXIifQ.hZ59SWtp_zLKVV9ShkqkTsCGi_jdPHly7XNCf5T_Ev0"
                        "leia_organa" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibGVpYV9vcmdhbmEifQ.8NXs4DZrx_hljsaC8d6xlZ07FUgenKmb6hDNU-KFQ3M"
                        "han_solo" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiaGFuX3NvbG8ifQ.lLYA_RUGZlmWULg-En-7tbTAuoVWFSR1-ad_e7s8PqM"
                        "lando_calrissian" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibGFuZG9fY2Fscmlzc2lhbiJ9.QIxUC5nTo3x1C4bkyEv5b8-pHZwIE5BDeRuBw4Z1K14"
                        "chewbacca" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiY2hld2JhY2NhIn0.4FJLy1za8OWCS8Bf6fW76w_TGfvJ0Q8o60gLk0qtrnc"
                        "c3po" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYzNwbyJ9._hR-bDio4Ai9hWHhYXuKaiXQ8gyzhll7RRHqj7Eb1Ok"
                        "r2d2" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicjJkMiJ9.NYxcxPTrB6ov0skhA-Kmvrmf9ewWP94BwHCQROuQ4ec"
                        "anakin_skywalker" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYW5ha2luX3NreXdhbGtlciJ9.4Rsce_GZeY9g4SHAVgqkjgqAHl70_8iSHCAYeRSuMY8"
                        "obi_wan_kenobi" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoib2JpX3dhbl9rZW5vYmkifQ.2BTRdIM1xAy8mTWfAT_y61Mjrkjuwv7X-SQRFO0UJ74"
                        "padme_amidala" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicGFkbWVfYW1pZGFsYSJ9.4czOc0NE73usN7eSUoWzg6-_sw5BhahE_QRMC-minHc"
                        "qui_gon_jinn" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicXVpX2dvbl9qaW5uIn0.aetVSDimtIqV2tM5AA-7MsClTmFtVKIr4lzOKOFlv6Q"
                        else -> ""
                    }
                },
            )

        val Production =
            DemoAppConfig(
                apiKey = "2wyrh3ssdhfs",
                token = { userId ->
                    when (userId) {
                        "petar" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicGV0YXIifQ.0-fAFuzhkibafsuiHnVytiYz_6bMqDBFksCvRxrVn2A"
                        "gian" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiZ2lhbiJ9.KB5fzlZgCz9n8bZNQpLseS2rkpPWrJXLujUrQHf_INo"
                        "luke_skywalker" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibHVrZV9za3l3YWxrZXIifQ.P6tZUf7OTEYNUUA4yPOlwN3fWqebDGoARSw09ooeyaY"
                        "leia_organa" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibGVpYV9vcmdhbmEifQ.buNtkyPDegZ4G6NjMghYHIt3vH5nnnLpNh9MjgJaSXI"
                        "han_solo" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiaGFuX3NvbG8ifQ.KEeSINlSt7zp-UItfNR9yzvHuPVjAGJqfBGzZcilKZE"
                        "lando_calrissian" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoibGFuZG9fY2Fscmlzc2lhbiJ9.fBjR-FBGAV4H6fteW53ggJ4_D_QDMh-0yLFzhMSxJC0"
                        "chewbacca" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiY2hld2JhY2NhIn0.I3cPFebLRDFPTCagqhFPgsijyb0c6hBOPTcZAlYjGq8"
                        "c3po" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYzNwbyJ9.rmm2E1EaC599gne9eqSWJX3rMY958mQhliRwL9DEu8s"
                        "r2d2" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicjJkMiJ9.D-GZg_PyffOf7D_WPTVP9SOaRklPVgvuGY3iRTqbeh0"
                        "anakin_skywalker" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiYW5ha2luX3NreXdhbGtlciJ9.WOllUZafwfejx1kErZPrPeXTJYnEFTxj3SQBixaTbVA"
                        "obi_wan_kenobi" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoib2JpX3dhbl9rZW5vYmkifQ.uJW6FnsPhmLo4p9pKb3QzaIfOV-nNcZumNE1LG1dQls"
                        "padme_amidala" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicGFkbWVfYW1pZGFsYSJ9.RNTUNRdwLn3x1h2VMBqe1JoS6P88mKWqsHYAihFuxBU"
                        "qui_gon_jinn" ->
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoicXVpX2dvbl9qaW5uIn0.NpIlGpn97YurPezcoEiqgGcS5qJu5SoavsE1JcY8lM8"
                        else -> ""
                    }
                },
            )

        var Current: DemoAppConfig = Production
    }
}
