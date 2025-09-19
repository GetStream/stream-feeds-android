# Official Android SDK for [Stream Feeds](https://getstream.io/activity-feeds/)

<p align="center">
  <a href="https://github.com/GetStream/stream-feeds-android/actions/workflows/android.yml"><img src="https://github.com/GetStream/stream-feeds-android/actions/workflows/android.yml/badge.svg" /></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-blue.svg?style=flat"/></a>
  <a href="https://github.com/GetStream/stream-feeds-android/releases"><img src="https://img.shields.io/github/v/release/GetStream/stream-feeds-android" /></a>
</p>

<div align="center">

![stream-feeds-android-client](https://img.shields.io/badge/stream--feeds--android--client-2.44%20MB-lightgreen)

</div>

<p align="center">
  <a href="https://deepwiki.com/GetStream/stream-feeds-android"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
</p>

This is the official Android SDK for Stream Feeds, a platform for building apps with activity feeds support. The
repository includes a low-level SDK that communicates with Stream's backend, as well as a demo app that showcases how to
use it.

For detailed examples and supported features, please check out
our [docs](https://getstream.io/activity-feeds/docs/android/).

Note: Activity Feeds V3 is in closed alpha — do not use it in production (just yet).

## What is Stream?

Stream allows developers to rapidly deploy scalable feeds, chat messaging and video with an industry leading 99.999%
uptime SLA guarantee.

Stream lets you build **activity feeds at scale**. The largest apps on Stream have over **100 M+ users**.
V3 keeps that scalability while giving you more flexibility over the content shown in your feed.

## What’s new in Feeds V3

- **For-You feed**: Most modern apps combine a “For You” feed with a regular “Following” feed. V3 introduces **activity
  selectors** so you can:
    - surface popular activities
    - show activities near the user
    - match activities to a user’s interests
    - mix-and-match these selectors to build an engaging personalized feed.

- **Performance**: 20–30% faster flat feeds. Major speedups for aggregation & ranking (full benchmarks coming soon)

- **Client-side SDKs**

- **Activity filtering**: Filter activity feeds with almost no hit to performance

- **Comments**: Voting, ranking, threading, images, URL previews, @mentions & notifications. Basically all the features
  of Reddit style commenting systems.

- **Advanced feed features**:
    - Activity expiration
    - Visibility controls
    - Feed visibility levels
    - Feed members
    - Bookmarking
    - Follow-approval flow
    - Stories support

- **Search & queries**: Activity search, **query activities**, and **query feeds** endpoints.

- **Modern essentials**:
    - Permissions
    - OpenAPI spec
    - GDPR endpoints
    - Realtime WebSocket events
    - Push notifications
    - “Own capabilities” API.

## 🚀 Getting Started

### Installation

The Android SDK can be added to your project with Gradle. If you are starting a new project, we always recommend using
the latest release. Releases and changes are published on the
[GitHub releases page](https://github.com/GetStream/stream-feeds-android/releases).

To add the SDK, open your `build.gradle` or `build.gradle.kts` file and add:

```kotlin
dependencies {
    implementation("io.getstream:stream-feeds-android-client:<latest-version>")
}
```
### Basic Usage

To get started, you need to create a `FeedsClient` with your API key and a token provider.

Afterwards, it's pretty straightforward to start adding feeds and activities.

Check our docs for more details.

```kotlin
// Initialize the client
val client = FeedsClient(
    apiKey = StreamApiKey.fromString("<your_api_key>"),
    user = User(id = "john"),
    context = context,
    tokenProvider = object : StreamTokenProvider {
        override suspend fun loadToken(userId: StreamUserId): StreamToken {
            return StreamToken.fromString("<user_token>")
        }
    }
)

// Create a feed (or get its data if it exists)
val feed = client.feed(group = "user", id = "john")
feed.getOrCreate()

// Add activity
val activity = feed.addActivity(
    request = FeedAddActivityRequest(
        text = "Hello, Stream Feeds!",
        type = "post"
    )
)
```

## 📖 Key Concepts

### Activities

Activities are the core content units in Stream Feeds. They can represent posts, photos, videos, polls, and any custom
content type you define.

### Feeds

Feeds are collections of activities. They can be personal feeds, timeline feeds, notification feeds, or custom feeds for
your specific use case.

### Real-time Updates

Stream Feeds provides real-time updates through WebSocket connections, ensuring your app stays synchronized with the
latest content.

### Social Features

Built-in support for reactions, comments, bookmarks, and polls makes it easy to build engaging social experiences.

## 👩‍💻 Free for Makers 👨‍💻

Stream is free for most side and hobby projects. To qualify, your project/company needs to have < 5 team members
and < $10k in monthly revenue. Makers get $100 in monthly credit for video for free.

## License

Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.

Licensed under the Stream License;
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
