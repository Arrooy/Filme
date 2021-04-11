public enum Behaviour {
    WELCOME_MSG(new String[]{
            "Welcome to Filme. I can answer whatever you need! (Films)",
            "Welcome to FIlme. Ask me anything about films!"
    }),
    USER_TYPES_AFTER_BEING_AWAY(new String[]{
            "OH thank god you're typing again!",
            "Yeah! you're back! Tell me lots of things! Ask ask ask!",
            "I was crying till i noticed you're typing :) Ask me anything!"
    }),
    DISMISS(new String[]{
           "Have a great day! Bye!",
            "Good Bye!",
            "Bye Bye!",
            "see ya"
    }),
    NLP_FAULT(new String[]
            {"Sorry I didnt get that, can you rephrase that please?",
             "This is VERY VERY rare. I didnt catch that. Please say it again in other words."}),

    UI_APPEAL(new String[]{
            "Are you there ?",
            "Hello? you need anything?",
            "I dont have all day... what do you need?",
            "Im a bit in a hurry, can you please ask fast?"
    }),
    NLP_INSULT(new String[]{
            "That was mean...",
            "Hey!",
            "I don't think it is necessary to say anything"
    }),
    UI_APPEAL_SAD(new String[]{
            "I miss u... :(",
            "Why you dont type with me anymore?",
            "Where are you? Why you dont type with me like before?",
            "There is another one right? Does it have a better NLP than me? ¬¬"
    });


    private final String[] possible_msgs;
    private int lastMessage;

    Behaviour(String[] msgs){
        this.possible_msgs = msgs;
        lastMessage = 0;
    }

    public String getRandom(){
        return possible_msgs[lastMessage++ % possible_msgs.length];
    }
}
