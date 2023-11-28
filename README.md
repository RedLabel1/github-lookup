# github-lookup

100% Kotlin, MVVM, modular, clean, one-activity architecture, view based app.
Kotlin coroutines and Flow are a key part of this app.

## *** IMPORTANT ***
Add these entries to your local.properties in order to run the app:

```
BASE_URL=https://api.github.com/
PERSONAL_ACCESS_TOKEN=YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
```

### On list-detail implementation
A shared VM, scoped to the UI module's nav_graph, is being used to share the data between Fragments, via an immutable state.

### On Unit Tests
Every UseCase, Repository and ViewModel is fully tested.

### Shortcomings
There is no pagination. GitHub API doesn't seem to allow for any kind of result sorting by stars, so pagination has been left out in favor of a better UX. We can discuss my pagination implementation in other private projects of mine that I can make available for you.
There are no complex animations. Out of scope since I didn't see where to fit them in this small app. [I have a good example that uses MotionLayout in the Play Store](https://play.google.com/store/apps/details?id=com.redlabel.cyberpunkpuzzlesolver&hl=en&gl=US&pli=1). No need for a login to try it out.
The repos that I'm querying are all from the user "kotlin" (https://github.com/kotlin), and the page size is only 8 in an effort to bypass GitHub's soft ban for an excess of queries.
