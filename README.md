# HackerNews
A simple Hacker News (https://news.ycombinator.com) reader app using Hacker News API (https://github.com/HackerNews/API)

-  Support API 9 to 25
-  Follows MVP architectural pattern
-  Uses Dagger2 for DI
-  Rxjava
-  Works in either orientation (portrait/landscape)
-  Use Gradle to build, jCenter/Maven Central for dependencies (no .jar dependencies)
-  Includes Automated Test

## App has 3 screens
- Home screen with list of top items displayed in order and can be pulled to refresh.
- Item screen with list of comments and their replies.
- WebView screen that takes user to url page by clicking a FloatButton.
