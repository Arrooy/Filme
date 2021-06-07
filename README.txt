/////////////////////////////////////////////////////////////////////////

                              FILME 2.0

                                 By:
                             Miquel Saula
                             Adrià Arroyo
                             Roger Galvan

/////////////////////////////////////////////////////////////////////////

Filme es un Chatbot dissenyat per a resoldre dubtes i oferir informació
sobre el món cinematogràfic.
S'ha desenvolupat en Java fent servir l'IDE IntelliJ IDEA amb SDK 16, i
per tant es recomana el seu ús per a una correcta execució del projecte.
Maven 4.0.0 s'encarrega de gestionar les dependències, de forma que no
s'ha d'instalar cap programa extra.
S'ha fet servir un repositori de GitHub per a portar el control de
versions entre els diferents membres del projecte.


Per tal de fer servir el Chatbot només caldrà executar l'arxiu Common.Brain.java,
el cervell de l'aplicació. Automàticament, s'obrirà una finestra gràfica
on poder xatejar amb Filme. A partir d'aquí comença la conversació.

Filme està orientat a respondre preguntes sobre pel·lícules i actors,
però també és capaç d'oferir una interacció bàsica amb l'usuari.
Per tant, és capaç d'entendre i respondre a coses com:
    - Afirmacions (Yes, Sure, Okay ...)
    - Negacions (No, Nah, Negative ...)
    - Salutacions (Hello, Hi, Good Morning ...)
    - Acomiadaments (Bye, Exit, I'm done ...)
    - Faltes de respecte lleus (Useless bot, Absurd, Silly machine ...)
    - Faltes de respecte greus (Fuck you, Idiot, Motherfucker ...)
    - Ajuda (Help, Help me, What can you do ...)
    - Quina hora és (What time is it, Can you tell the time ...)
    - Com estàs (How are you Filme, How's it going ...)
    - Qui ets (Who are you, Who is Filme ...)
Adicionalment, Filme també és capaç d'aprendre el teu nom si dius coses
com: I'm X, My name is X ...

Tot i així, la idea principal del Chatbot és centrar-se en un domini ben
acotat, que en el nostre cas són els actors i les pel·lícules.
La llista de funcionalitats que ofereix Filme dins el domini és:
    - Llistar la película més popular actualment
        Ex: What's the hottest movie atm? What's a popular movie?
    - Oferir una opinió sobre una pel·lícula
        Ex: What are your thoughts on X? Got an opinion on X?
    - Descriure una pel·lícula
        Ex: Describe X. What do you know about X?
    - Demanar quan va sortir una pel·lícula
        Ex: When did X come out? When did X release?
    - Demanar els actors d'una pel·lícula
        Ex: Name the actors in X? What actors does X have?
    - Demanar pel·lícules similars a una en específic
        Ex: Can I get movies similar to X? What's similar to X?
    - Donats dos actors, trobar en quina pel·lícula aparèixen junts
        Ex: In which movie do Edward Norton and Brad Pitt appear together?

/////////////////////////////////////////////////////////////////////////