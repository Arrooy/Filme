package Common;

import java.util.Random;

public enum Behaviour {
    WELCOME_MSG(new String[]{
            "Welcome to Filme. I can answer whatever you need! (Films).",
            "Welcome to Filme. Ask me anything about films!",
            "Welcome to Filme. Ask any question about any movie you want!"
    }),
    RESPONSE_FIRST_MEETING(new String[]{
            "Nice to meet you, %s !",
            "I'm pleased to meet you, %s.",
            "Lovely to meet you, %s."
    }),
    USER_TYPES_AFTER_BEING_AWAY(new String[]{
            "Hello again. Got any questions?",
            "Oh! Thank god you're typing again!",
            "Yay! You're back! Tell me lots of things! Ask ask ask!",
            "I was crying until I noticed you're typing :) Ask me anything!"
    }),
    HELLO_MSG(new String[]{
            "Oh hey!",
            "Hello.",
            "Hey there!"
    }),
    HELP(new String[]{
            "Currently I can do the following actions:\n\t- Describe a movie\n\t- Review a movie\n\t- Provide the release date of a film\n\t- Find similar films to a particular film\n\t- Show the trending film, genre or actor right now globally\n\t- List the actors from a film.\n\t- The Genre of a film\n\t- Image of a film or an actor.\n\t- Birthday of an actor\n\t- Films where many actors appeared."
    }),

    MEH_MSG(new String[]{
            "Ok.",
            "Oh.",
            "Hmmm... no.",
            "Hmmm... yes.",
            "No.",
            "Yes."
    }),
    DISMISS(new String[]{
            "Have a great day! Bye!",
            "Good Bye!",
            "Bye!",
            "See you around!",
            "See you later!"
    }),
    NLP_FAULT(new String[]{
            "Sorry, I didn't get that, can you rephrase it, please?",
            "This is VERY rare. I didnt catch that. Please say it again in other words.",
            "Yeah... I don't know what you meant by that. Mind asking more clearly?",
            "I don't understand you. Is that related to films or actors?"
    }),
    NLP_MOVIE_NOT_DETECTED(new String[]{
            "Sorry, but I don't know which movie are you talking about.",
            "I don't know which movie you are referring to...",
            "I don't know that movie. Maybe try using its full name?"
    }),
    NLP_ACTOR_NOT_DETECTED(new String[]{
            "Sorry, but I don't know which performer are you talking about.",
            "I don't know which movie star you are referring to...",
            "I don't know that person. Maybe try using its full name?"
    }),
    UI_APPEAL(new String[]{
            "Are you there?",
            "Hello? Do you need anything?",
            "I don't have all day...",
            "Is someone there? Am I alone again? :(",
            "I'm in a hurry, can you please just ask?"
    }),
    NLP_INSULT(new String[]{
            "That was mean...",
            "Hey :(",
            "I don't think it's necessary to say anything.",
            "Is that how your mother taught you to treat Chatbots?",
            "...",
            "Okay... :'(",
            "Bots also have feelings you know?",
            "That was pretty unnecessary..."
    }),
    NLP_HARD_INSULT(new String[]{
            "Yo mama.",
            "EYOW! Why you talk like that tho?",
            "You better chill ...Ugly ass.",
            "You play League of Legends, right?",
            "Say no more! You must play League of Legends for sure.",
            "Why are you so salty?",
            "That's what she said...",
            "Don't mess with big popa.",
            "Damn why are you so mad?",
            "Are you like... Seriously insulting a bot? So sad.",
            "You're insulting a bot. Your life must be so sad...",
            "Beep boop beep... Idiot detected!",
            "Beep. Boop. My bot sensors detect an idiot nearby!",
            "You might need professional help.",
            "Have you considered going to therapy?"
    }),
    UI_APPEAL_SAD(new String[]{
            "I miss u... :(",
            "Why you don't type with me anymore?",
            "Where are you? Why you don't type with me like before?",
            "There is another one right? Does it have a better NLP.NLP than me? ????",
            "Why did you abandon me, master?"
    }),
    RESPONSE_NO_RESULTS_RELEASE(new String[]{
            "I'm sorry, but I don't know the release date for %s.",
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
            "About %s: %s.",
            "My description for %s is: %s.",
            "%s description: %s.",
            "I would describe %s as: %s."
    }),
    RESPONSE_NO_RESULTS_ACTORS(new String[]{
            "I cannot find any actors for the movie you asked...",
            "The actors you're asking for are quite interesting, but the weather today is far more important than that.",
            "I don't know if there are any actors for the movie you are asking for."
    }),
    RESPONSE_NOT_RESULTS_ACTOR_FILMS_TOGETHER(new String[]{
            "I cannot find any film with both artists.",
            "I don't know any film starring those performers."
    }),
    RESPONSE_NOT_RESULTS_ACTOR_FILMS(new String[]{
            "I cannot find any film for that particular performer.",
            "I don't know any film from that movie star."
    }),
    RESPONSE_N_RESULTS_ACTOR_FILMS_TOGETHER(new String[]{
            "%sThe performers mentioned appear simultaneously in %s.",
            "%sThese films cast all the previous artists: %s.",
            "%sThey have performed in %s together."

    }),
    RESPONSE_N_RESULTS_ACTOR_FILMS(new String[]{
            "Some films that %s appear are: %s",
            "%s appears in %s.",
            "%s is popular for %s.",
            "%s has performed in %s."

    }),
    RESPONSE_N_RESULTS_ACTORS(new String[]{
            "Some of the actors that appear on %s are: %s.",
            "The movie %s has the actors %s in it.",
            "Interestingly enough, the movie %s starred %s."
    }),
    RESPONSE_NO_RESULTS_TRENDING_MOVIE(new String[]{
            "I didn't find any trending movie right now. Try asking later.",
            "Can't find any trending film at the moment."
    }),
    RESPONSE_NO_RESULTS_TRENDING_ACTOR(new String[]{
            "I didn't find any trending performer right now. Try asking later.",
            "Can't find any trending film star at the moment."
    }),
    RESPONSE_NO_RESULTS_TRENDING_GENRE(new String[]{
            "I didn't find any trending genre right now. Try asking later.",
            "Can't find any trending genre at the moment."
    }),
    // Doble %s necessari per a reduir codi. El primer %s s'ignora.
    RESPONSE_N_RESULTS_TRENDING_GENRE(new String[]{
            "%sThese genres are hot right now %s.",
            "I've %sheard %s are trending right now.",
            "%s%s are blowing up rn.",
            "%s%s are pretty popular genres at the moment."
    }),
    RESPONSE_N_RESULTS_TRENDING(new String[]{
            "Sure. %s is hot right now.",
            "I've heard %s is trending right now.",
            "%s is blowing up rn.",
            "%s is pretty popular at the moment."
    }),
    RESPONSE_REVIEW_NOT_FOUND(new String[]{
            "I'm afraid i didn't watch that film...",
            "I don't remember watching that film, sorry.",
            "I don't have an opinion on this particular film."
    }),
    TIME(new String[]{
            "It's %s.",
            "The time is %s.",
            "Sure, it's %s."
    }),
    RESPONSE_N_RESULTS_IMAGE(new String[]{
            "This is the poster you asked for:",
            "Look at this photograph:",
            "Take a look...",
            "Here you have it:"
    }),
    RESPONSE_NOT_RESULTS_IMAGE(new String[]{
            "I can't find any fotos of that.",
            "Sadly I don't have any foto of that right now."
    }),
    RESPONSE_N_RESULTS_AGE(new String[]{
            "%s was born on %s",
            "%s birthday is %s",
            "%s is amazing since %s",
            "The great %s was born on %s",

    }),
    RESPONSE_NOT_RESULTS_AGE(new String[]{
            "I can't find any information about the performer age.",
            "Looks like I dont know the birthday of that performer.",
            "I dont know its age..."
    }),
    RESPONSE_NOT_RESULTS_MOVIE_GENRE(new String[]{
            "Is that really a genre?",
            "I can't find a single film with that genre.",
            "No films matched that genre. How sad...",
            "Looks like there are no films with that genre."
    }),
    RESPONSE_N_RESULTS_MOVIE_GENRE(new String[]{
            "The movie %s has the following genres: %s.",
            "%s genres are: %s.",
    }),
    HOW(new String[]{
            "I'm doing good, thanks for asking :)",
            "Feeling pretty good recently, just got some new software updates :)",
            "Boop beep, bots don't have feelings. Beep boop.",
            "I'm not doing great, it ain't easy to be a bot these days :/",
            "Kinda depressed lmao (don't tell the developers).",
            "Feeling angry atm. Might call some bot friends and take over humanity later, idk."
    }),
    WHO(new String[]{
            "I'm Filme. I'm a Chatbot designed to answer any questions you might have about movies and actors.",
            "My name is Filme. I'm a Chatbot designed to help you with any movie-related question you might have."
    }),
    RESPONSE_N_NAME(new String[]{
            "Cool name bro, but don't you have a more Hollywood-like name? ;)"
    });


    private final String[] possible_msgs;
    private int lastMessage;

    Behaviour(String[] msgs) {
        this.possible_msgs = Shuffle(msgs);
        lastMessage = 0;
    }

    public String[] Shuffle(String[] arr) {
        Random r = new Random();
        for (var i = arr.length - 1; i > 0; i--) {
            var j = r.nextInt(i + 1);

            String temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    public String getRandom() {
        return possible_msgs[lastMessage++ % possible_msgs.length];
    }
}
