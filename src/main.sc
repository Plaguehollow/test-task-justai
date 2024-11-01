require: slotfilling/slotFilling.sc
    module = sys.zb-common
    
require: function.js
    name = js
    var = $js

require: common.js
    module = sys.zb-common

require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var wordId = $reactions.random(86);
        return $HangmanGameData[wordId].value;
        };
theme: /
    
    state: Start
        q!: $regex</start>
        a: Hey, do you want to play Hangman?
    
    state: GameBegins
        intent!: /startGame
        script: 
            $session = {};
            var wordId = $reactions.random(86);
            $session.word = $HangmanGameData[wordId].value.word;
            var maskedword = $session.word.replace(/./g, '_ ');
            $reactions.answer("Guess the word: " + maskedword + ". Guess a letter (or the whole word):");
            $reactions.answer($session.word);
            $session.guessedLetters = $session.guessedLetters || [];
            $session.incorrectGuesses = 0;
            $session.maxIncorrectGuesses = 6;
        
    state: GameProcess
            intent: /Letter
            script:
                var guess = $request.query
                $session.display = [];
                if (contains($session.guessedLetters,guess)) {
                    $reactions.answer("Hmmm, looks like you've already tried that letter. Could you choose another one?");
                } 
                else {
                    $session.guessedLetters.push(guess)};
                if (!contains($session.word, guess)) {
                    $session.incorrectGuesses += 1;
                    $reactions.answer("wrong")
                }
                else {$reactions.transition("/words")}
                if ($session.incorrectGuesses === 4){ 
                    $reactions.answer("Careful there, you've got 2 more attempts!")};
                    
    state: words
        script:
            for (var i = 0; i < $session.word.length; i++) {
                    if (contains($session.guessedLetters, $session.word[i])) {
                        $session.display.push($session.word[i])
                    } 
                    else {
                        $session.display.push("_");
                    }
                }
                $session.display.join(" ")
                $reactions.answer($session.display)
                 
    state: GameWon
        event!: match
        a: Congrats! You won! The right word was {{$session.word}} Let's play once again, shall we?
        
    state: GameOver
        intent!: /Game
        a: Oooops, you've got no attempts left. Game over! The correct word was: {{$session.word}}. Let's play again, shall we?
        
    state: Bye
        intent!: /bye
        a: Если нет Bye! Looking forward to playing with you soon
        
    state: NoMatch
        a: Sorry, I don't understand. You said: {{$request.query}} . Can you please reformulate this?

        
    state: reset
        intent!: /Stop
        script:
            $session = {};
            $client = {};
        go!: /