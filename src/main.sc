require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: text/text.sc
    module = zenbot-common
    
require: where/where.sc
    module = zenbot-common

require: common.js
    module = zenbot-common

require: hangmanGameData.csv
    name = HangmanGameData
    var = $HangmanGameData

patterns:
    $Word = $entity<HangmanGameData> || converter = function ($parseTree) {
        var id = $parseTree.HangmanGameData[0].value;
        return $HangmanGameData[id].value;
        };

theme: /

    state: Start
        q!: $regex</start>
        a: Предлагаю поиграть в игру "угадай столицу".

    state: CountryPattern
        intent: /startGame
        script:
            $session.Geography =  $Geography[$jsapi.random(192)]
            var Country = $session.Geography.value.country
            $reactions.answer("Какой город является столицей:" + capitalize($nlp.inflect(Country, "gent")));
                
    state: AssertCity
        intent: /City
        script:
            var city = $parseTree._AnswerCity.name
            if (city == $session.Geography.value.name) {
                $reactions.answer("Ответ верный");
                $reactions.transition("/CountryPattern");
                $session.points += 1
            }
            else
               $reactions.answer("Не верно. Ответ: {{$session.Geography.value.name}}");
               $reactions.transition("/CountryPattern");
                
    state: StopGame
        intent: /Stop
        a:Вы отгадали {{$session.points}} стран. Если захотите продолжить просто напишите старт или го.
        script:
            $session.points = 0
    
    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /