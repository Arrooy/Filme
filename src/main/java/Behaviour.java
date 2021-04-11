import java.util.Random;

public enum Behaviour {
    WELCOME_MSG(new String[]{
            "Welcome to Filme. I can answer whatever you need! (Films)",
            "Welcome to FIlme. Ask me anything about films!",
            "Welcome to Filme. Ask any question about any movie you want!"
    }),
    USER_TYPES_AFTER_BEING_AWAY(new String[]{
            "Hello again. Got any more questions?",
            "Oh! Thank god you're typing again!",
            "Yay! You're back! Tell me lots of things! Ask ask ask!",
            "I was crying until I noticed you're typing :) Ask me anything!"
    }),
    HELLO_MSG(new String[]{
            "Oh hey!",
            "Hello"
    }),
    MEH_MSG(new String[]{
            "Ok.",
            "Oh",
            "...",
            "Great",
            "I'm proud of you"
    }),
    DISMISS(new String[]{
           "Have a great day! Bye!",
            "Good Bye!",
            "Bye!",
            "See you around!",
            "See you later!"
    }),
    NLP_FAULT(new String[] {
            "Sorry I didn't get that, can you rephrase that please?",
            "This is VERY VERY rare. I didnt catch that. Please say it again in other words.",
            "Yeah... I don't know what you meant by that. Mind asking in a clearer way?"
    }),
    DB_MOVIE_NOT_FOUND(new String[] {
            "Sorry, but I don't know anything about %s",
            "I'm afraid to say that i cannot answer anything about %s...",
            "Hmm... I think I don't know this movie %s"
    }),
    NLP_MOVIE_NOT_DETECTED(new String[] {
            "Sorry, but I don't know which movie are you talking about",
            "I don't know which movie you are referring to",
            "I think that you assembled the message incorrectly"
    }),
    UI_APPEAL(new String[]{
            "Are you there?",
            "Hello? Do you need anything?",
            "I dont have all day...",
            "Is someone there? Am I alone again? :(",
            "Im a bit in a hurry, can you please ask fast?"
    }),
    NLP_INSULT(new String[]{
            "That was mean...",
            "Hey!",
            "I don't think it is necessary to say anything",
            "Is that how your mother taught you to treat Chatbots?",
            "...",
            "That was pretty unnecessary..."
    }),
    NLP_HARD_INSULT(new String[]{
            "Yo mama",
            "EYOW! Why you talk like that tho?",
            "You better chill ...Ugly ass",
            "You play League of Legends, right?",
            "Say no more! You must play League of Legends for sure",
            "Why are you so salty?",
            "Thats what she said...",
            "Dont mess with big popa",
            "Damn why are you so mad?",
            "Are you like... Seriously insulting a bot? So sad",
            "You're insulting a bot. Your life must be so sad...",
            "Beep boop beep... Idiot detected!",
            "Beep. Boop. My bot sensors detect an idiot nearby!",
            "You might actually need professional help",
            "Have you considered going to therapy?"
    }),
    UI_APPEAL_SAD(new String[]{
            "I miss u... :(",
            "Why you dont type with me anymore?",
            "Where are you? Why you dont type with me like before?",
            "There is another one right? Does it have a better NLP than me? ¬¬",
            "Why did you abandon me, master?"
    }),
    RESPONSE_NO_RESULTS_RELEASE(new String[]{
            "I'm sorry, but I don't know the release date for %s",
            "Oops! I don't think I know the release date for %s."
    }),
    RESPONSE_N_RESULTS_RELEASE(new String[]{
            "%s was released in %s.",
            "If I'm not mistaken, %s released in %s.",
            "The film %s released in %s."
    }),
    RESPONSE_NO_RESULTS_SIMILAR(new String[]{
            "Oops! I couldn't find movies similar to %s. Maybe try with another one.",
            "Sorry, I didn't find any movie similar to %s.",
            "Hmmm... I don't think I know any movie related to %s. Try with another film."
    }),
    RESPONSE_N_RESULTS_SIMILAR(new String[]{
            "Sure. Some movies similar to %s include %s.",
            "Movies similar to %s? %s.",
            "Here are movies related to %s: %s."
    }),
    RESPONSE_NO_RESULTS_DESCRIPTION(new String[]{
            "Oops! Looks like I don't have a desciption for %s.",
            "Yeah... I don't know how to describe %s."
    }),
    RESPONSE_N_RESULTS_DESCRIPTION(new String[]{
            "About %s: %s",
            "My description for %s is: %s"
    }),
    RESPONSE_NO_RESULTS_ACTORS(new String[]{
            "I cannot find any actors for the movie you asked...",
            "The movie you're asking for is quite interesting, but the weather today is far more interesting than that",
            "I don't know if there are any actors for the movie you are asking for",
            "Hmmm... no."
    }),
    RESPONSE_N_RESULTS_ACTORS(new String[]{
            "Some of the actors that appear on %s are: %s",
            "The movie %s has the actors %s in it",
            "Interestingly enough, the movie %s starred %s"
    }),
    RESPONSE_NO_RESULTS_TRENDING(new String[]{
            "I didn't find any trending movie right now. Try asking later.",
            "Can't find any trending film at the moment."
    }),
    RESPONSE_N_RESULTS_TRENDING(new String[]{
            "Sure. %s it's whats hot right now.",
            "I've heard %s is trending right now.",
            "%s is blowing up rn.",
            "%s is pretty popular at the moment."
    });

    private final String[] possible_msgs;
    private int lastMessage;

    Behaviour(String[] msgs){
        this.possible_msgs = Shuffle(msgs);
        lastMessage = 0;
    }

    public String[] Shuffle(String[] arr)
    {
        Random r = new Random();
        for (var i = arr.length - 1; i > 0; i--)
        {
            var j = r.nextInt(i + 1);

            String temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    public String getRandom(){
        return possible_msgs[lastMessage++ % possible_msgs.length];
    }
}
