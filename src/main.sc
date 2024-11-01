require: slotfilling/slotFilling.sc
    module = sys.zb-common

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
    
    state: GameBegins
        intent!: /startGame
        script: 
            var wordId = $reactions.random(86);
            $session.word = $HangmanGameData[wordId].value.word;
            var maskedword = $session.word.replace(/./g, '_ ');
            $reactions.answer("Guess the word: " + maskedword);
            $session.guessedLetters = ["f","e"];
            $session.incorrectGuesses = 0;
            $session.maxIncorrectGuesses = 6;
            $reactions.answer($session.guessedLetters);
            $reactions.transition("/GuessLetter")
            
    state: GuessLetter
            script:
                $reactions.answer("Guess a letter (or the whole word):")
            
    state: GameProcess
            intent: /Letter
            script:
                var guess = $request.query
                $reactions.answer($session.guessedLetters);
                if ($session.guessedLetters.includes(guess)) {
                $reactions.answer("Hmmm, looks like you've already tried that letter. Could you choose another one?");
                } else {
                $session.guessedLetters.push(guess);
                if (!$session.word.includes(guess)) {
                    $session.incorrectGuesses += 1;
                    }
                }
                if ($session.incorrectGuesses = 4);
                $reactions.answer("Careful there, you've got 2 more attempts!");
                elseif ($session.incorrectGuesses = 5);
                $reactions.answer("Attention, you've got only 1 attempt left!");
                var display = "";
                for (var i = 0; i < $session.word.length; i += 1) {
                    if ($session.guessedLetters.includes($session.word[i])) {
                        display += $session.word[i];
                    } else {
                        display += "_ ";
                    }
                }
                    
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

    state: Start
        q!: $regex</start>
        a: Hey, do you want to play Hangman?