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
        a: Предлагаю поиграть в игру "угадай столицу". Для страрта напишите старт или го.

    state: CountryPattern
        intent: /startGame
        script:
            if ($session.points == null){
                $session.points = 0
                }
            $session.Geography =  $Geography[$jsapi.random(192)]
            var Country = $session.Geography.value.country
            $reactions.answer("Какой город является столицей:" + capitalize($nlp.inflect(Country, "gent")));
                
    state: AssertCity
        intent: /City
        script:
            var city = $parseTree._AnswerCity.name
            if (city == $session.Geography.value.name) {
                $reactions.answer("Ответ верный");
                $session.points += 1
                $reactions.transition("/CountryPattern");
            }
            else
               $reactions.answer("Не верно. Ответ:{{$session.Geography.value.name}}");
               $reactions.transition("/CountryPattern");
                
    state: StopGame
        intent: /Stop
        script:
            log($session.points)
            $reactions.answer("Вы отгадали {{$session.points}} стран. Если захотите продолжить просто напишите старт или го.");
            $session.points = 0
    
    state: NoMatch
        event!: noMatch
        script: 
            $reactions.answer("Не верно. Ответ:{{$session.Geography.value.name}}");
            $reactions.transition("/CountryPattern");

    state: reset
        q!: reset
        script:
            $session = {};
            $client = {};
        go!: /