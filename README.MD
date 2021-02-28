### About SCRAPI
SCRAPI (short for "Scala Reddit API") is a wrapper built around the Reddit API which allows users to get/post data from/to 
Reddit in Scala applications. It was inspired largely by the PRAW project (https://github.com/praw-dev/praw), which is the
Python equivalent of this project.

### Building SCRAPI 
SCRAPI can be built using sbt by running the following commands:
```
git clone https://github.com/McKalvan/SCRAPI.git
cd SCRAPI
sbt package
```

### Getting Started
SCRAPI can be used with or without a Reddit OAuth2 token (see https://github.com/reddit-archive/reddit/wiki/OAuth2).
Start by importing the "Reddit" object and requesting an OAuth2 token from the Reddit API. While this step is technically optional,
it is highly recommended and is the only way that SCRAPI will be able perform certain actions on behalf of users:
```scala
import com.github.mckalvan.scrapi.models.Reddit
val reddit: Reddit.type = Reddit
reddit.tokenize(
    "<USERNAME>",
    "<PASSWORD>",
    "<CLIENT_ID>",
    "<CLIENT_SECRET>",
    "<USER_AGENT>"
)
```

The Reddit object (tokenized or not) will act as the facade through which all other functionality of the SCRAPI library can be accessed.
The methods provided in the Reddit object (w/ the exception of Subreddits) will all make some sort of request against the
Reddit API and then parse the resulting data of that request into a case class denoted by Parsed<NAME_OF_DATATYPE>, ex. 
Subreddit -> ParsedSubreddit, Comment -> ParsedComment, etc. The resulting fields that were extracted into a given case class
can simply be referred to as any val would be. Any Parsed* class can be converted back into a json string by calling "asJson" on it.

The 6 methods and their related classes are detailed below.

#### Subreddit
The Subreddit class takes the name of a subreddit as an argument and returns a ParsedSubreddit instance containing metadata about the 
subreddit. The ParsedSubreddit class also defines methods for retrieving submissions and additional metadata for that subreddit.
```scala
import com.github.mckalvan.scrapi.models.Reddit

// Get a Subreddit instance for the r/redditDev Subreddit.
val subreddit = Reddit.subreddit("redditDev")

// Get the top submissions for a given subreddit at the current time
subreddit.topSubmissions()

// Get new submissions for a given subreddit
subreddit.newSubmissions()

// Get hot submissions for a given subreddit
subreddit.hotSubmissions()

// Get rising submissions for a given subreddit
subreddit.risingSubmissions()

// Get list of submissions from a given subreddit sorted by some Param (see Params below)
subreddit.sortSubmissions()

// Get list of controversial submissions from a given subreddit
subreddit.controversialSubmissions()

// Get one random submission from a given subreddit
subreddit.randomSubmission()

// Stream comments from a given subreddit
subreddit.stream.comments().foreach(x => println(x.asJson))

// Stream submissions from a given subreddit
subreddit.stream.submissions().foreach(x => println(x.asJson))
```

 
#### Subreddits
The Subreddits object defines a series of methods that allow for various lists of Subreddits to be retrieved from the Reddit API.  

```scala
import com.github.mckalvan.scrapi.models.Reddit

val subreddits = Reddit.subreddits

// Get trending subreddits
subreddits.trending()

// Get default subreddits
subreddits.default()

// Get popular subreddits
subreddits.popular()

// Get new subreddits
subreddits.new_subreddits()

// Stream new subreddit as ParsedSubreddit instances as they are created
subreddits.stream().foreach(x => println(x.asJson))
``` 

#### Submission
The Submission class takes the id of a submission as an argument and returns a ParsedSubmission instance containing metadata
about the submission. The ParsedSubmission class also defines methods for getting any comments associated w/ the submission as well,
as well as methods for getting the ParsedUser and ParsedSubreddit instances associated w/ the given submission.

```scala
import com.github.mckalvan.scrapi.models.Reddit

val submission = Reddit.submission("kvzaot")

// Gets ParsedUser object associated w/ a given submission
submission.userObj

// Gets the ParsedSubreddit instance associated w/ a given submission
submission.subredditObj

// Gets a list of the top comments from a given submission
submission.topComments()

// Gets a list of hot comments from a given submission
submission.hotComments()

// Gets a list of the newest comments from a given submission
submission.newComments()

// Gets a list of the best comments from a given submission
submission.bestComments()
``` 

The ParsedSubmission class inherits from and supports methods defined by the following traits:
Votable, Gildable, Awardable, Reportable, Repliable

#### Comment
The Comment class takes the id of a particular comment as an argument and returns a ParsedComment instance containing metadata on
that comment. The ParsedComment class also defines methods for getting the ParsedUser, ParsedSubmission, and ParsedSubreddit
objects associated w/ the given comment.

```scala
import com.github.mckalvan.scrapi.models.Reddit

val comment = Reddit.comment("gj2szb7")

// Gets the ParsedUser object associated w/ a given comment
comment.userObj

// Gets the ParsedSubmission object associated w/ a given comment
comment.submissionObj

// Gets the ParsedSubreddit object associated w/ a given comment
comment.subredditObj
```
The ParsedComment class inherits from and supports methods defined by the following traits:
Votable, Gildable, Awardable, Reportable, Repliable

#### MoreComments
The MoreComments class is a class which, given a submission id, subreddit, and sorting type, returns a sequence of ParsedComment
by paginating the set of comments available under a given submission. The initial set of comments pulled may not represent the full
set of comments available on a given submission, but further comments can be retrieved by calling the "getMore" method as demonstrated below.

```scala
import com.github.mckalvan.scrapi.models.Reddit

val submissionComments = Reddit.comments("gj2szb7", "redditdev", "new")

// Access the initial set of comments gathered by the MoreComments instance
submissionComments.comments

// Gets more comments from the submission. Note that this requires additional calls to the Reddit API,
// so there are two additional arguments that this method accepts to limit both the total number of comments return and/or
// the total number of requests made before returning.
submissionComments.getMore(1000, 100)
```

#### User
The User class takes a username as an argument and returns a ParsedUser instance containing metadata about that particular user.
The ParsedUser instance also contains defines a method for retrieving a the comments/submissions that a given user has publicly made on Reddit.

```scala
import com.github.mckalvan.scrapi.models.Reddit

val user = Reddit.user("SomeUser123")

// Gets the comments/submissions associated w/ the given user.
// Note that these are parsed into a GenericSubmission class as the Reddit API will return both commens and submissions in the same response
user.getPosts()
```

#### Using Params in SCRAPI
Almost every method publicly accessible method supports an additional varg-type argument of type Params, which consists of 0 or more
string tuples that are passed into calls against the Reddit API as additional arguments. 

For example, an application interested only in top submissions in a subreddit from the past hour can do the following:
```scala
import com.github.mckalvan.scrapi.models.Reddit

// Get a Subreddit instance for the r/redditDev Subreddit.
val subreddit = Reddit.subreddit("redditDev")
subreddit.topSubmissions(("t", "hour"))
```

Endpoint specific params can be found here: https://www.reddit.com/dev/api

#### Streaming in SCRAPI
The Subreddits and ParsedSubreddit class currently support streaming near-realtime events from the Reddit API. Streaming in SCRAPI is essentially just
a series of micro-batches made against a certain endpoint that filter out any rgecords that have already been seen since the stream
started and push any new records out to the iterator for the user to consume. 

#### Rate limiting
SCRAPI handles rate-limiting internally when make calls to the Reddit API. Every response from the Reddit API contains
a JSON blob somewhere that specifies rate-limit specific information, which SCRAPI then parses and maintains internally. If
a user has under 10 calls left before being rate-limited by the API, SCRAPI delays the call until it can safely be made after
a certain amount of time, after which the user should be safe to resume making calls against the API. More info re. rate-limiting
can be found here: https://github.com/reddit-archive/reddit/wiki/API#rules

### Contributing
Contributions are more than welcome in any part of this project! I have done all of the work independently thus far, so there is
almost certainly something out there that can be added to the project or done better. This is my first run at creating an open source 
project, so it would also definitely be nice to have some guidance on typical life-cycles and versioning practices for open-source projects. 
